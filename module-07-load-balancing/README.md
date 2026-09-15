# Module 07 — Load Balancing & Proxies

## Overview
Implement load balancing strategies from scratch, building toward a fully proxied Task API with health checks and failover.

## Labs

### Lab 01 — Consistent Hashing ✅
- Consistent hash ring with TreeMap + MurmurHash3
- 150 virtual nodes per server for even distribution
- Adding a server remaps ~25% of keys (vs 75% with naive hash % N)

### Lab 02 — Reverse Proxy ✅
- Proxy on port 8080 with round-robin and consistent hashing strategies
- Reuses Lab 01 hash ring as Maven dependency
- Connection: close fix for HTTP keep-alive timeout
- 502 Bad Gateway when backend is down — no automatic failover yet

### Lab 03 — Health Checks + Failover (upcoming)