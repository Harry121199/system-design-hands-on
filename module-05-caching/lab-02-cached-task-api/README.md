# Lab 02 — Caffeine cache on Task API

## Problem
The Module 04 Task API hits the database on every request — even when 100 users
ask for the same data within 1 second. Adding a cache layer eliminates redundant
database queries.

## What I learned
- @EnableCaching activates Spring's cache infrastructure
- @Cacheable stores the method's return value — second call skips the method entirely
- @CacheEvict removes cached entries when data changes
- Caffeine is an in-memory cache configured via spring.cache.caffeine.spec
- maximumSize=500 limits memory usage, expireAfterWrite=60s prevents stale data
- Cold cache (first call) hits DB and stores result
- Warm cache (subsequent calls) returns stored result — 0ms, no SQL executed
- show-sql=true proves caching: SQL appears on misses, disappears on hits
- Integration with Module 04: same entity, same 50,000 rows, cache added as a layer

## Benchmark results
| Call | Latency |
|------|---------|
| No cache | 574ms |
| Cold cache | 230ms |
| Warm cache | 0ms |

## How to run
```bash
cd lab-02-cached-task-api
.\mvnw.cmd spring-boot:run
curl -s http://localhost:8080/api/v1/benchmark
```


## Lab 03 — Cache invalidation (added to this project)

### Problem
Cached data goes stale when the database is updated. Three strategies handle this
differently, each trading freshness for speed.

### Strategies implemented
| Strategy | On write | Stale risk | Speed |
|----------|----------|------------|-------|
| Cache-aside | Evict from cache | One stale read after write | Fast |
| Write-through | Update cache immediately | None | Medium |
| TTL-based | Do nothing | Entire TTL window | Fastest |

### What I learned
- Cache-aside is the most common pattern — evict on write, lazy reload on next read
- TTL is the simplest but guarantees stale reads within the TTL window
- Write-through eliminates stale reads but makes writes slower (two operations)
- Production systems combine cache-aside + TTL as a safety net
- The core trade-off: fewer DB hits = faster but higher stale risk
- There is no caching strategy with zero stale risk AND zero DB hits

### Benchmark results
| Strategy | DB hits | Time | Stale? |
|----------|---------|------|--------|
| No cache | 3 | 679ms | Never |
| Cache-aside | 2 | 452ms | No |
| TTL-based | 1 | 206ms | Yes |