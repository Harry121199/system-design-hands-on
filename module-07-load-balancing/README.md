# Module 07 — Load Balancing & Proxies

## Overview
Implement load balancing strategies from scratch, building toward a fully proxied Task API with health checks and failover.

## Labs

### Lab 01 — Consistent Hashing ✅
- Consistent hash ring with TreeMap + MurmurHash3
- 150 virtual nodes per server for even distribution
- Adding a server remaps ~25% of keys (vs 75% with naive hash % N)
- Reused in Module 08 for database sharding

### Lab 02 — Reverse Proxy + Task API (upcoming)
### Lab 03 — Health Checks + Failover (upcoming)