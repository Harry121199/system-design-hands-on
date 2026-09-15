package com.systemdesign.proxy;

import com.systemdesign.hashing.ConsistentHashRing;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HealthCheckProxy {

    private final List<String> backends;
    private final Set<String> healthyBackends = ConcurrentHashMap.newKeySet();
    private final ConsistentHashRing hashRing;
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private final String strategy;

    public HealthCheckProxy(List<String> backends, String strategy) {
        this.backends = backends;
        this.strategy = strategy;
        this.hashRing = new ConsistentHashRing(150);
        for (String backend : backends) {
            hashRing.addNode(backend);
            healthyBackends.add(backend);
        }
    }

    public void start(int port) throws IOException {
        startHealthChecker();

        ServerSocket server = new ServerSocket(port);
        System.out.println("Health-check proxy started on port " + port);
        System.out.println("Strategy: " + strategy);
        System.out.println("Backends: " + backends);
        System.out.println("Health checks every 5 seconds on /health\n");

        while (true) {
            Socket client = server.accept();
            new Thread(() -> handleRequest(client)).start();
        }
    }

    private void startHealthChecker() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "health-checker");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            for (String backend : backends) {
                boolean healthy = checkHealth(backend);
                boolean wasHealthy = healthyBackends.contains(backend);

                if (healthy && !wasHealthy) {
                    healthyBackends.add(backend);
                    System.out.println("[HEALTH] " + backend + " is back UP");
                } else if (!healthy && wasHealthy) {
                    healthyBackends.remove(backend);
                    System.out.println("[HEALTH] " + backend + " is DOWN");
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private boolean checkHealth(String backend) {
        String[] hostPort = backend.split(":");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostPort[0], Integer.parseInt(hostPort[1])), 2000);
            OutputStream out = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write("GET /health HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n".getBytes());
            out.flush();

            String statusLine = in.readLine();
            return statusLine != null && statusLine.contains("200");
        } catch (Exception e) {
            return false;
        }
    }

    private void handleRequest(Socket client) {
        try (client;
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream out = client.getOutputStream();) {

            String requestLine = in.readLine();
            if (requestLine == null) return;

            Map<String, String> headers = new LinkedHashMap<>();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                int colon = line.indexOf(":");
                if (colon > 0) {
                    headers.put(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
                }
            }

            String routingKey = headers.getOrDefault("X-User-Id", client.getInetAddress().getHostAddress());
            String backend = pickBackend(routingKey);

            if (backend == null) {
                String body = "No healthy backends available";
                out.write(("HTTP/1.1 503 Service Unavailable\r\nContent-Length: " + body.length() + "\r\n\r\n" + body).getBytes());
                out.flush();
                System.out.println("[" + strategy + "] " + requestLine + " → 503 (no healthy backends)");
                return;
            }

            System.out.printf("[%s] %s → %s (key: %s) [healthy: %s]%n",
                    strategy, requestLine, backend, routingKey, healthyBackends);

            String[] hostPort = backend.split(":");
            try (Socket backendSocket = new Socket(hostPort[0], Integer.parseInt(hostPort[1]));
                 OutputStream backendOut = backendSocket.getOutputStream();
                 BufferedReader backendIn = new BufferedReader(new InputStreamReader(backendSocket.getInputStream()));
                 ) {

                String clientIp = client.getInetAddress().getHostAddress();
                backendOut.write((requestLine + "\r\n").getBytes());
                for (Map.Entry<String, String> h : headers.entrySet()) {
                    backendOut.write((h.getKey() + ": " + h.getValue() + "\r\n").getBytes());
                }
                backendOut.write(("X-Forwarded-For: " + clientIp + "\r\n").getBytes());
                backendOut.write("Connection: close\r\n".getBytes());
                backendOut.write("\r\n".getBytes());
                backendOut.flush();

                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = backendIn.readLine()) != null) {
                    response.append(responseLine).append("\r\n");
                }
                out.write(response.toString().getBytes());
                out.flush();

            } catch (ConnectException e) {
                healthyBackends.remove(backend);
                String body = "Backend " + backend + " failed";
                out.write(("HTTP/1.1 502 Bad Gateway\r\nContent-Length: " + body.length() + "\r\n\r\n" + body).getBytes());
                out.flush();
                System.out.println("  → 502 and marked " + backend + " as DOWN");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String pickBackend(String key) {
        if (healthyBackends.isEmpty()) return null;

        if ("consistent".equals(strategy)) {
            // Build set of unhealthy nodes to skip
            Set<String> skipNodes = new HashSet<>(backends);
            skipNodes.removeAll(healthyBackends);
            return hashRing.getNodeSkipping(key, skipNodes);
        }

        // Round-robin over healthy only
        List<String> healthy = new ArrayList<>(healthyBackends);
        int idx = roundRobinIndex.getAndIncrement() % healthy.size();
        return healthy.get(idx);
    }


    public static void main(String[] args) throws IOException {
        String strategy = args.length > 0 ? args[0] : "consistent";
        List<String> backends = List.of("localhost:8081", "localhost:8082");
        new HealthCheckProxy(backends, strategy).start(8080);
    }
}