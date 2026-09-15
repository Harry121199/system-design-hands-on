# Module 07 — Load Balancing & Proxies

## Overview
Built load balancing from scratch — consistent hashing, reverse proxy, and health-checked failover in front of a real Task API.

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
- 503 when all backends are down