package com.systemdesign.cachedapi.config;

import java.util.concurrent.atomic.AtomicInteger;

public class QueryCounter {
    private static final AtomicInteger count = new AtomicInteger(0);

    public static void increment() { count.incrementAndGet(); }
    public static int getCount() { return count.get(); }
    public static void reset() { count.set(0); }

}
