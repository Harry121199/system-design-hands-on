# Module 07 — Load Balancing, CDN & Proxies

## Overview
Built load balancing, health-checked failover, and edge caching from scratch — all in front of a real Task API.

## Labs

### Lab 01 — Consistent Hashing ✅
- Hash ring with TreeMap + MurmurHash3, 150 virtual nodes
- Adding a server remaps ~25% of keys (vs 75% with naive hash % N)

### Lab 02 — Reverse Proxy ✅
- Proxy on port 8080 with round-robin and consistent hashing
- Reuses Lab 01 hash ring as Maven dependency
- Connection: close fix for HTTP keep-alive 60s timeout

### Lab 03 — Health Checks + Failover ✅
- Background health checker pings /health every 5 seconds
- Ring-walk failover skips dead nodes clockwise
- Automatic recovery when backends come back online

### Lab 04 — CDN Cache Simulator ✅
- Edge caching proxy with configurable TTL
- 66.7% hit rate on rapid requests, <1ms cache hits
- /cdn/stats and /cdn/purge endpoints
- Maps to real CDN concepts: edge nodes, TTL, cache invalidation