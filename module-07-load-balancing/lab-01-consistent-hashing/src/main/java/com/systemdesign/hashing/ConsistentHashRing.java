package com.systemdesign.hashing;

import java.util.*;

public class ConsistentHashRing {

    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            int hash = hash(node + "-vn" + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            int hash = hash(node + "-vn" + i);
            ring.remove(hash);
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        int hash = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            entry = ring.firstEntry();  // Wrap around the ring
        }
        return entry.getValue();
    }

    public int getNodeCount() {
        return (int) ring.values().stream().distinct().count();
    }

    public int getRingSize() {
        return ring.size();
    }

    private int hash(String key) {
        byte[] data = key.getBytes();
        int seed = 0x9747b28c;
        int len = data.length;
        int h = seed ^ len;
        int i = 0;

        while (i + 4 <= len) {
            int k = (data[i] & 0xFF)
                    | ((data[i + 1] & 0xFF) << 8)
                    | ((data[i + 2] & 0xFF) << 16)
                    | ((data[i + 3] & 0xFF) << 24);
            k *= 0xcc9e2d51;
            k = Integer.rotateLeft(k, 15);
            k *= 0x1b873593;
            h ^= k;
            h = Integer.rotateLeft(h, 13);
            h = h * 5 + 0xe6546b64;
            i += 4;
        }

        int remaining = 0;
        switch (len - i) {
            case 3: remaining ^= (data[i + 2] & 0xFF) << 16;
            case 2: remaining ^= (data[i + 1] & 0xFF) << 8;
            case 1: remaining ^= (data[i] & 0xFF);
                remaining *= 0xcc9e2d51;
                remaining = Integer.rotateLeft(remaining, 15);
                remaining *= 0x1b873593;
                h ^= remaining;
        }

        h ^= len;
        h ^= (h >>> 16);
        h *= 0x85ebca6b;
        h ^= (h >>> 13);
        h *= 0xc2b2ae35;
        h ^= (h >>> 16);

        return h & Integer.MAX_VALUE;
    }
}