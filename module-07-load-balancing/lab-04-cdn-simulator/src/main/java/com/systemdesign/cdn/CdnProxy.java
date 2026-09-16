package com.systemdesign.cdn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CdnProxy {

    private final String originHost;
    private final int originPort;
    private final long ttlMs;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);

    public CdnProxy(String originHost, int originPort, long ttlMs) {
        this.originHost = originHost;
        this.originPort = originPort;
        this.ttlMs = ttlMs;
    }

    public void start(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("CDN edge proxy started on port " + port);
        System.out.println("Origin: " + originHost + ":" + originPort);
        System.out.println("TTL: " + ttlMs + "ms\n");

        while (true) {
            Socket client = server.accept();
            new Thread(()-> handleRequest(client)).start();
        }
    }

    private void handleRequest(Socket client) {
        try (client;
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream out = client.getOutputStream();) {
            String requestLine = in.readLine();
            if (requestLine == null ) return;

            // Read and discard headers
            while (true) {
                String line = in.readLine();
                if (line == null || line.isEmpty()) break;
            }

            // Extract the path as cache key
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) return;
            String path = parts[1];

            // Stats endpoint - not cached
            if (path.equals("/cdn/stats")) {
                serveStats(out);
                return;
            }

            // Purge endpoint - clear cache
            if (path.equals("/cdn/purge")) {
                cache.clear();
                hits.set(0);
                misses.set(0);
                String body = "{\"purged\":true,\"message\":\"Cache cleared\"}";
                out.write(("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: "
                        + body.length() + "\r\nConnection: close\r\n\r\n" + body).getBytes());
                out.flush();
                System.out.println("[PURGE] Cache Cleared");
                return;
            }

            // Check cache
            CacheEntry entry = cache.get(path);
            if (entry != null && !entry.isExpired()) {
                hits.incrementAndGet();
                out.write(entry.getResponse().getBytes());
                out.flush();
                System.out.printf("[HIT]  %s (age: %dms, remaining: %dms)%n",
                        path, entry.ageMs(), entry.remainingMs());
                return;
            }

            // Cache miss or expired - fetch from origin
            misses.incrementAndGet();
            String reason = entry == null?"not cached":"expired";
            System.out.printf("[MISS] %s (%s) → fetching from origin%n", path, reason);

            String response = fetchFromOrigin(requestLine);
            if (response != null) {
                cache.put(path, new CacheEntry(response, ttlMs));
                out.write(response.getBytes());
                out.flush();
            } else {
                String body = "Origin server unreachable";
                out.write(("HTTP/1.1 502 Bad Gateway\r\nContent-Length: "
                        + body.length() + "\r\nConnection: close\r\n\r\n" + body).getBytes());
                out.flush();
            }
        }catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String fetchFromOrigin(String requestLine) {
        try (Socket origin = new Socket(originHost, originPort);
             OutputStream originOut = origin.getOutputStream();
             BufferedReader originIn = new BufferedReader(new InputStreamReader(origin.getInputStream()))) {

            originOut.write((requestLine + "\r\nHost: localhost\r\nConnection: close\r\n\r\n").getBytes());
            originOut.flush();

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = originIn.readLine()) != null) {
                response.append(line).append("\r\n");
            }
            return response.toString();
        } catch (IOException e) {
            System.out.println("[ERROR] Origin Unreachable" + e.getMessage());
            return null;
        }
    }


    private void serveStats(OutputStream out) throws IOException {
        long totalHits = hits.get();
        long totalMisses = misses.get();
        long total = totalHits + totalMisses;
        double hitRate = total > 0 ? (totalHits * 100.0 / total) : 0;

        StringBuilder entries = new StringBuilder();
        cache.forEach((path, entry) -> {
            entries.append(String.format("    \"%s\": {\"expired\": %s, \"age_ms\": %d, \"remaining_ms\": %d},\n",
                    path, entry.isExpired(), entry.ageMs(), entry.remainingMs()));
        });

        String body = String.format("""
                {
                  "hits": %d,
                  "misses": %d,
                  "hit_rate": "%.1f%%",
                  "cached_entries": %d,
                  "entries": {
                %s  }
                }""", totalHits, totalMisses, hitRate, cache.size(),
                entries.toString());

        out.write(("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: "
                + body.length() + "\r\nConnection: close\r\n\r\n" + body).getBytes());
        out.flush();
    }
    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        String originHost = args.length > 1 ? args[1] : "localhost";
        int originPort = args.length > 2 ? Integer.parseInt(args[2]) : 8081;
        long ttlMs = args.length > 3 ? Long.parseLong(args[3]) : 10000;

        new CdnProxy(originHost, originPort, ttlMs).start(port);
    }
}
