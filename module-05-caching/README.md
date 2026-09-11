# Module 05 — Caching

## What this module covers

Adding a cache layer to the existing Task API from Module 04. Building an LRU cache
from scratch, integrating Caffeine, and solving the cache invalidation problem.

## Labs

| # | Lab | Concept | Status |
|---|-----|---------|--------|
| 1 | [LRU Cache from Scratch](lab-01-lru-cache/) | Eviction, O(1) operations, hit rate, Zipf distribution | ✅ Done |
| 2 | [Caffeine cache on Task API](lab-02-cached-task-api/) | @Cacheable, before/after latency, 574ms → 0ms | ✅ Done |
| 3 | Cache invalidation | Stale data on update, cache-aside, write-through, TTL | ⬜ Not Started |