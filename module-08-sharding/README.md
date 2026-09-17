# Module 08 — Sharding & Partitioning

## Overview
Split the Task API database across multiple shards using consistent hashing, with scatter-gather for cross-shard queries and live rebalancing.

## Labs

### Lab 01 — Shard Router ✅
- 3 H2 shards with consistent hash routing by project ID
- 50,000 tasks distributed across shards with co-location
- Raw JDBC for explicit shard control

### Lab 02 — Cross-Shard Queries ✅
- Scatter-gather: query all shards in parallel, merge results
- Single-shard: 1ms, scatter-gather: 28ms (28x overhead)
- Aggregations need client-side post-processing

### Lab 03 — Rebalancing ✅
- Added 4th shard dynamically, 28% of projects migrated (close to ideal 25%)
- 14,000 tasks moved in 138ms with delete-after-write safety
- Consistent hashing minimized disruption across all shards