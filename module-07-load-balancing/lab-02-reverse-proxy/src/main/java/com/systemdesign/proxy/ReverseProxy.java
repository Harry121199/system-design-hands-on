package com.systemdesign.proxy;

import com.systemdesign.hashing.ConsistentHashRing;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ReverseProxy {

    private final List<String> backends;
    private final ConsistentHashRing hashRing;
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private final String strategy;

    public ReverseProxy(List<String> backends, String strategy) {
        this.backends = backends;
        this.strategy = strategy;
        this.hashRing = new ConsistentHashRing(150);
        for (String backend : backends) {
            hashRing.addNode(backend);
        }
    }

    public void start(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Reverse proxy started on port " + port);
        System.out.println("Strategy: " + strategy);
        System.out.println("Backends: " + backends);

        while (true) {
            Socket client = server.accept();
            new Thread(() -> handleRequest(client)).start();
        }
    }

    private void handleRequest(Socket client) {
        try (client) {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();

            // Read the request line
            String requestLine = in.readLine();
            if (requestLine == null) return;

            // Read headers
            Map<String, String> headers = new LinkedHashMap<>();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                int colon = line.indexOf(":");
                if (colon > 0) {
                    headers.put(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
                }
            }

            // Pick backend
            String routingKey = headers.getOrDefault("X-User-Id", client.getInetAddress().getHostAddress());
            String backend = pickBackend(routingKey);
            System.out.printf("[%s] %s → %s (key: %s)%n", strategy, requestLine, backend, routingKey);

            // Forward to backend
            String[] hostPort = backend.split(":");
            String host = hostPort[0];
            int port = Integer.parseInt(hostPort[1]);

            try (Socket backendSocket = new Socket(host, port)) {
                OutputStream backendOut = backendSocket.getOutputStream();
                BufferedReader backendIn = new BufferedReader(new InputStreamReader(backendSocket.getInputStream()));

                // Forward request line + headers + X-Forwarded-For
                String clientIp = client.getInetAddress().getHostAddress();
                backendOut.write((requestLine + "\r\n").getBytes());
                for (Map.Entry<String, String> h : headers.entrySet()) {
                    backendOut.write((h.getKey() + ": " + h.getValue() + "\r\n").getBytes());
                }
                backendOut.write(("X-Forwarded-For: " + clientIp + "\r\n").getBytes());
                backendOut.write("Connection: close\r\n".getBytes());
                backendOut.write("\r\n".getBytes());
                backendOut.flush();

                // Read backend response and forward to client
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = backendIn.readLine()) != null) {
                    response.append(responseLine).append("\r\n");
                }
                out.write(response.toString().getBytes());
                out.flush();

            } catch (ConnectException e) {
                // Backend down
                String body = "Backend " + backend + " is unavailable";
                String errorResponse = "HTTP/1.1 502 Bad Gateway\r\n"
                        + "Content-Length: " + body.length() + "\r\n\r\n" + body;
                out.write(errorResponse.getBytes());
                out.flush();
                System.out.println("  → 502 Bad Gateway: " + backend);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String pickBackend(String key) {
        if ("consistent".equals(strategy)) {
            return hashRing.getNode(key);
        }
        // Round-robin
        int idx = roundRobinIndex.getAndIncrement() % backends.size();
        return backends.get(idx);
    }

    public static void main(String[] args) throws IOException {
        String strategy = args.length > 0 ? args[0] : "round-robin";
        List<String> backends = List.of("localhost:8081", "localhost:8082");
        new ReverseProxy(backends, strategy).start(8080);
    }
}