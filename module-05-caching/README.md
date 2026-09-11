# Module 05 — Caching

## What this module covers

Adding a cache layer to the existing Task API from Module 04. Building an LRU cache
from scratch, integrating Caffeine, and solving the cache invalidation problem.

## Labs

| # | Lab | Concept | Status |
|---|-----|---------|--------|
| 1 | [LRU Cache from Scratch](lab-01-lru-cache/) | Eviction, O(1) operations, hit rate, Zipf distribution | ✅ Done |
| 2 | [Caffeine cache on Task API](lab-02-cached-task-api/) | @Cacheable, before/after latency, 574ms → 0ms | ✅ Done |
| 3 | [Cache invalidation](lab-02-cached-task-api/) | Stale data, cache-aside, write-through, TTL | ✅ Done |

## Key takeaways

- LRU cache = HashMap + Doubly Linked List = O(1) get/put with eviction
- 80/20 access pattern: 100-item cache served 82% of 100,000 queries
- @Cacheable skips the method entirely on cache hit — 574ms → 0ms
- Cache invalidation is the hard problem: every strategy trades freshness for speed
- Cache-aside (evict on write) is the most common production pattern
- TTL is the simplest safety net but guarantees a stale window
- Production systems combine both: evict on known writes + TTL for unknown ones