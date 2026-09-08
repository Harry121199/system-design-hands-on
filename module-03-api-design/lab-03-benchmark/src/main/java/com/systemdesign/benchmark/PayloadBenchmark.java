package com.systemdesign.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdesign.grpc.proto.Task;
import java.util.LinkedHashMap;
import java.util.Map;

public class PayloadBenchmark {

    private static final int ITERATIONS = 10_000;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {

        System.out.println("=== REST vs gRPC Benchmark ===\n");
        System.out.println("Iterations per test: " + ITERATIONS);
        System.out.println();

        // ============ PAYLOAD SIZE ============

        System.out.println("--- 1. Payload size ---\n");

        // JSON (REST)
        Map<String, Object> jsonTask = new LinkedHashMap<>();
        jsonTask.put("id", "a1b2c3d4");
        jsonTask.put("title", "Learn gRPC");
        jsonTask.put("description", "Build a gRPC API and compare with REST");
        jsonTask.put("status", "IN_PROGRESS");
        jsonTask.put("priority", "HIGH");
        jsonTask.put("createdAt", "2026-09-08T12:00:00");
        jsonTask.put("updatedAt", "2026-09-08T12:30:00");

        byte[] jsonBytes = mapper.writeValueAsBytes(jsonTask);
        System.out.println("JSON payload: " + jsonBytes.length + " bytes");
        System.out.println("JSON text:    " + mapper.writeValueAsString(jsonTask));
        System.out.println();

        // Protobuf (gRPC)
        Task protoTask = Task.newBuilder()
                .setId("a1b2c3d4")
                .setTitle("Learn gRPC")
                .setDescription("Build a gRPC API and compare with REST")
                .setStatus("IN_PROGRESS")
                .setPriority("HIGH")
                .setCreatedAt("2026-09-08T12:00:00")
                .setUpdatedAt("2026-09-08T12:30:00")
                .build();

        byte[] protoBytes = protoTask.toByteArray();
        System.out.println("Proto payload: " + protoBytes.length + " bytes");
        System.out.println("Proto hex:     " + bytesToHex(protoBytes, 40) + "...");
        System.out.println();

        double reduction = (1.0 - (double) protoBytes.length / jsonBytes.length) * 100;
        System.out.printf("Protobuf is %.0f%% smaller than JSON%n%n", reduction);

        // ============ SERIALIZATION SPEED ============

        System.out.println("--- 2. Serialization speed ---\n");

        // JSON serialization
        long jsonSerStart = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            mapper.writeValueAsBytes(jsonTask);
        }
        long jsonSerTime = (System.nanoTime() - jsonSerStart) / 1_000_000;

        // Proto serialization
        long protoSerStart = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            protoTask.toByteArray();
        }
        long protoSerTime = (System.nanoTime() - protoSerStart) / 1_000_000;

        System.out.println("JSON  serialization: " + jsonSerTime + "ms (" + ITERATIONS + " iterations)");
        System.out.println("Proto serialization: " + protoSerTime + "ms (" + ITERATIONS + " iterations)");
        System.out.printf("Proto is %.1fx faster at serialization%n%n",
                (double) jsonSerTime / protoSerTime);

        // ============ DESERIALIZATION SPEED ============

        System.out.println("--- 3. Deserialization speed ---\n");

        // JSON deserialization
        long jsonDeStart = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            mapper.readValue(jsonBytes, Map.class);
        }
        long jsonDeTime = (System.nanoTime() - jsonDeStart) / 1_000_000;

        // Proto deserialization
        long protoDeStart = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            Task.parseFrom(protoBytes);
        }
        long protoDeTime = (System.nanoTime() - protoDeStart) / 1_000_000;

        System.out.println("JSON  deserialization: " + jsonDeTime + "ms (" + ITERATIONS + " iterations)");
        System.out.println("Proto deserialization: " + protoDeTime + "ms (" + ITERATIONS + " iterations)");
        System.out.printf("Proto is %.1fx faster at deserialization%n%n",
                (double) jsonDeTime / protoDeTime);

        // ============ SUMMARY ============

        System.out.println("--- Summary ---\n");
        System.out.println("| Metric              | JSON (REST)   | Protobuf (gRPC) | Winner   |");
        System.out.println("|---------------------|---------------|-----------------|----------|");
        System.out.printf("| Payload size        | %d bytes      | %d bytes       | Protobuf |%n",
                jsonBytes.length, protoBytes.length);
        System.out.printf("| Serialization       | %dms          | %dms           | Protobuf |%n",
                jsonSerTime, protoSerTime);
        System.out.printf("| Deserialization     | %dms          | %dms           | Protobuf |%n",
                jsonDeTime, protoDeTime);
        System.out.println("| Human readability   | Easy          | Binary          | JSON     |");
        System.out.println("| Browser support     | Native        | Needs proxy     | JSON     |");
        System.out.println("| Type safety         | Runtime       | Compile-time    | Protobuf |");
    }

    private static String bytesToHex(byte[] bytes, int limit) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, limit); i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }
}