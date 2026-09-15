package com.systemdesign.hashing;

import java.util.*;

public class ConsistentHashDemo {

    public static void main(String[] args) {
        System.out.println("=== CONSISTENT HASHING DEMO ===\n");

        // --- Part 1: Basic routing ---
        ConsistentHashRing ring = new ConsistentHashRing(150);
        ring.addNode("Server-A");
        ring.addNode("Server-B");
        ring.addNode("Server-C");

        System.out.println("Ring: " + ring.getNodeCount() + " nodes, "
                + ring.getRingSize() + " virtual nodes\n");

        System.out.println("--- Request routing ---");
        String[] keys = {"user-1", "user-2", "user-3", "order-100", "order-200",
                "task-500", "task-600", "session-abc", "session-xyz", "report-42"};
        for (String key : keys) {
            System.out.printf("  %-15s → %s%n", key, ring.getNode(key));
        }

        // --- Part 2: Same key always hits same server ---
        System.out.println("\n--- Consistency check (same key = same server) ---");
        for (int i = 0; i < 5; i++) {
            System.out.printf("  Request %d: user-1 → %s%n", i + 1, ring.getNode("user-1"));
        }

        // --- Part 3: Distribution check ---
        System.out.println("\n--- Distribution across 10,000 keys ---");
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < 10_000; i++) {
            String node = ring.getNode("key-" + i);
            counts.merge(node, 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            double pct = e.getValue() / 100.0;
            System.out.printf("  %-12s: %,5d keys (%5.1f%%)%n", e.getKey(), e.getValue(), pct);
        }

        // --- Part 4: Add a server — measure remapping ---
        System.out.println("\n--- Adding Server-D ---");
        Map<String, String> beforeAdd = new HashMap<>();
        for (int i = 0; i < 10_000; i++) {
            beforeAdd.put("key-" + i, ring.getNode("key-" + i));
        }

        ring.addNode("Server-D");

        int remapped = 0;
        Map<String, Integer> countsAfter = new LinkedHashMap<>();
        for (int i = 0; i < 10_000; i++) {
            String key = "key-" + i;
            String newNode = ring.getNode(key);
            countsAfter.merge(newNode, 1, Integer::sum);
            if (!newNode.equals(beforeAdd.get(key))) remapped++;
        }
        System.out.printf("  Keys remapped: %,d / 10,000 (%.1f%%)%n", remapped, remapped / 100.0);
        System.out.println("  Ideal remap (1/N): " + (10_000 / 4) + " (25.0%)");
        System.out.println("\n  New distribution:");
        for (Map.Entry<String, Integer> e : countsAfter.entrySet()) {
            double pct = e.getValue() / 100.0;
            System.out.printf("    %-12s: %,5d keys (%5.1f%%)%n", e.getKey(), e.getValue(), pct);
        }

        // --- Part 5: Remove a server — measure remapping ---
        System.out.println("\n--- Removing Server-B ---");
        Map<String, String> beforeRemove = new HashMap<>();
        for (int i = 0; i < 10_000; i++) {
            beforeRemove.put("key-" + i, ring.getNode("key-" + i));
        }

        ring.removeNode("Server-B");

        int remapped2 = 0;
        Map<String, Integer> countsAfterRemove = new LinkedHashMap<>();
        for (int i = 0; i < 10_000; i++) {
            String key = "key-" + i;
            String newNode = ring.getNode(key);
            countsAfterRemove.merge(newNode, 1, Integer::sum);
            if (!newNode.equals(beforeRemove.get(key))) remapped2++;
        }
        System.out.printf("  Keys remapped: %,d / 10,000 (%.1f%%)%n", remapped2, remapped2 / 100.0);
        System.out.println("  Only Server-B's keys moved to neighbors\n");
        System.out.println("  New distribution:");
        for (Map.Entry<String, Integer> e : countsAfterRemove.entrySet()) {
            double pct = e.getValue() / 100.0;
            System.out.printf("    %-12s: %,5d keys (%5.1f%%)%n", e.getKey(), e.getValue(), pct);
        }

        // --- Part 6: Compare with naive hash % N ---
        System.out.println("\n=== COMPARISON: hash % N (naive) ===");
        String[] servers3 = {"Server-A", "Server-B", "Server-C"};
        String[] servers4 = {"Server-A", "Server-B", "Server-C", "Server-D"};
        int naiveRemapped = 0;
        for (int i = 0; i < 10_000; i++) {
            String key = "key-" + i;
            int h = Math.abs(key.hashCode());
            String before3 = servers3[h % 3];
            String before4 = servers4[h % 4];
            if (!before3.equals(before4)) naiveRemapped++;
        }
        System.out.printf("  Naive hash %%N remapped: %,d / 10,000 (%.1f%%)%n",
                naiveRemapped, naiveRemapped / 100.0);
        System.out.println("  Consistent hashing remapped: " + remapped + " (" + (remapped / 100.0) + "%)");
        System.out.println("  Consistent hashing moved ~" +
                (naiveRemapped / Math.max(remapped, 1)) + "x fewer keys");
    }
}