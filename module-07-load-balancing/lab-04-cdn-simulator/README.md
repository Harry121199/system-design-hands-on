# Lab 04 — CDN Cache Simulator

## What I built
- Edge caching proxy on port 8080, forwarding cache misses to origin (Task API on 8081)
- In-memory cache keyed by URL path with configurable TTL (default 10s)
- Cache HIT serves instantly without touching the origin
- Cache MISS fetches from origin, caches the response, then serves it
- /cdn/stats endpoint showing hits, misses, hit rate, and per-entry TTL status
- /cdn/purge endpoint to clear the cache (simulates CDN cache invalidation)

## How it works
- ConcurrentHashMap<String, CacheEntry> stores responses keyed by path
- CacheEntry tracks creation time and TTL, isExpired() checks if stale
- On HIT: serve from cache, log age and remaining TTL
- On MISS: fetch from origin, store in cache, serve to client
- On expired entry: treated as MISS, fetched fresh from origin

## Benchmark (10s TTL)
- 1 miss + 2 hits = 66.7% hit rate on 3 rapid requests
- Cache hit serves in <1ms (no network to origin)
- After TTL expires, next request re-fetches and caches again

## How this maps to real CDNs
- Our proxy = one CDN edge node (e.g., Cloudflare Mumbai PoP)
- ConcurrentHashMap = edge cache (real CDNs use disk + memory)
- TTL = Cache-Control max-age header in production
- /cdn/purge = CDN purge API (Cloudflare, AWS CloudFront invalidation)
- Missing: geographic distribution, cache hierarchies, conditional requests (ETag/If-None-Match)

## Key concepts
- CDN reduces origin load — origin only hit on cache miss
- TTL tradeoff: short = fresher data but more origin hits, long = faster but stale risk
- Cache invalidation is the hard part — purge vs TTL vs event-driven