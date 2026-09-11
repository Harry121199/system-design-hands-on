public class LRUCacheDemo {

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("  LRU Cache Demo — capacity = 3");
        System.out.println("==========================================\n");

        LRUCache<String, String> cache = new LRUCache<>(3);

        // Fill the cache
        System.out.println("--- Fill cache ---");
        cache.put("task_1", "Learn TCP");
        cache.printState();
        cache.put("task_2", "Learn HTTP");
        cache.printState();
        cache.put("task_3", "Learn REST");
        cache.printState();

        // Access task_1 — moves it to most recent
        System.out.println("\n--- Access task_1 (moves to front) ---");
        System.out.println("  get(task_1) = " + cache.get("task_1"));
        cache.printState();

        // Insert task_4 — evicts the LEAST recently used
        System.out.println("\n--- Insert task_4 (cache full, must evict) ---");
        cache.put("task_4", "Learn gRPC");
        cache.printState();

        // Try to get the evicted key
        System.out.println("\n--- Try to get evicted key ---");
        System.out.println("  get(task_2) = " + cache.get("task_2"));

        // Cache stats
        System.out.println("\n--- Stats ---");
        System.out.println("  Hits: " + cache.getHits());
        System.out.println("  Misses: " + cache.getMisses());
        System.out.printf("  Hit rate: %.1f%%%n", cache.hitRate());

        // Scale test — simulate database query caching
        System.out.println("\n==========================================");
        System.out.println("  Scale test — 100,000 queries, cache=100");
        System.out.println("==========================================\n");

        LRUCache<Integer, String> bigCache = new LRUCache<>(100);
        java.util.Random random = new java.util.Random(42);

        long dbTime = 0;
        long cacheTime = 0;

        for (int i = 0; i < 100_000; i++) {
            // 80% of queries access 20% of keys (Zipf-like distribution)
            int key = random.nextDouble() < 0.8
                    ? random.nextInt(20)        // hot keys (0-19)
                    : random.nextInt(1000);     // cold keys (0-999)

            long start = System.nanoTime();
            String cached = bigCache.get(key);
            cacheTime += System.nanoTime() - start;

            if (cached == null) {
                // Simulate slow database query
                long dbStart = System.nanoTime();
                try { Thread.sleep(0, 1000); } catch (InterruptedException ignored) {}
                String result = "Task-" + key;
                dbTime += System.nanoTime() - dbStart;

                bigCache.put(key, result);
            }
        }

        System.out.println("Queries: 100,000");
        System.out.println("Cache hits: " + bigCache.getHits());
        System.out.println("Cache misses: " + bigCache.getMisses());
        System.out.printf("Hit rate: %.1f%%%n", bigCache.hitRate());
        System.out.println("DB calls avoided: " + bigCache.getHits());
        System.out.printf("Estimated DB time saved: %dms%n", dbTime * bigCache.getHits() / Math.max(bigCache.getMisses(), 1) / 1_000_000);
    }
}