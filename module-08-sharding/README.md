# Module 08 — Sharding & Partitioning

## Overview
Split the Task API database across multiple shards using consistent hashing, with scatter-gather for cross-shard queries.

## Labs

### Lab 01 — Shard Router ✅
- 3 H2 shards with consistent hash routing by project ID
- 50,000 tasks distributed across shards with co-location
- Raw JDBC for explicit shard control

### Lab 02 — Cross-Shard Queries ✅
- Scatter-gather: query all shards in parallel, merge results
- Single-shard: 1ms, scatter-gather: 28ms (28x overhead)
- Aggregations need client-side post-processing

### Lab 03 — Rebalancing (upcoming)