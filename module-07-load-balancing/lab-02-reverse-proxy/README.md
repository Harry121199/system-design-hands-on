# Lab 02 — Reverse Proxy with Load Balancing

## What I built
- Reverse proxy on port 8080, forwarding to two Task API instances (8081, 8082)
- Two routing strategies: round-robin and consistent hashing (reusing Lab 01's hash ring)
- X-Forwarded-For header injection, 502 Bad Gateway on backend failure
- Connection: close header to avoid HTTP keep-alive timeout (60s → instant)

## How it works
- Round-robin: alternates 8081 → 8082 → 8081 → 8082
- Consistent hashing: hashes X-User-Id header (or client IP) to pick a backend
- Same user always hits the same server — preserves cache locality

## Key observations
- Round-robin: even distribution but no cache affinity
- Consistent hashing: sticky routing but no failover (502 if hashed backend is dead)
- HTTP keep-alive caused 60s delay — fixed by sending Connection: close
- Lab 03 adds health checks to skip dead backends automatically

## Reused components
- ConsistentHashRing from Lab 01 (Maven dependency, not copy-paste)
- Thread-per-client pattern from Module 01
- HTTP parsing from Module 01 Lab 04