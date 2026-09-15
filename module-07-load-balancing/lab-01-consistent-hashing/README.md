# Lab 01 — Consistent Hashing

## What I built
- Consistent hash ring using TreeMap + MurmurHash3
- Virtual nodes (150 per server) for even key distribution
- Demo comparing consistent hashing vs naive hash % N

## How it works
- Hash space is a ring (0 to 2^31)
- Servers placed at multiple points (virtual nodes) on the ring
- A key is hashed, walks clockwise to the first server
- TreeMap.ceilingEntry() = "walk clockwise", firstEntry() = "wrap around"

## Key results
- 3 servers: keys distributed ~33% each (even with MurmurHash3)
- Adding Server-D: ~25% keys remapped (close to ideal 1/N)
- Naive hash % N: 75% keys remapped on same change
- Consistent hashing moved ~8x fewer keys

## Why it matters
- Cache locality: same user always hits the same server
- Minimal disruption: adding/removing servers only moves ~1/N of keys
- Used in: DynamoDB, Cassandra, CDNs, Redis Cluster

## Implementation details
- MurmurHash3 (32-bit) for uniform distribution
- 150 virtual nodes per server for balanced load
- Java's String.hashCode() was too skewed — switched to MurmurHash