package com.systemdesign.cdn;

public class CacheEntry {
    private final String response;
    private final Long createdAt;
    private final Long ttlMs;

    public CacheEntry(String response, Long ttlMs) {
        this.response = response;
        this.ttlMs = ttlMs;
        this.createdAt = System.currentTimeMillis();
    }

    public String getResponse() {
        return response;
    }
    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > +ttlMs;
    }

    public long ageMs() {
        return System.currentTimeMillis() - createdAt;
    }

    public long remainingMs() {
        long remaining = ttlMs - (System.currentTimeMillis() - createdAt);
        return Math.max(0, remaining);
    }
}
