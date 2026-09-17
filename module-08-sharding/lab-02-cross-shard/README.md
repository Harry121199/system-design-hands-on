# Lab 02 — Cross-Shard Queries

## What I built
- Scatter-gather service querying all 3 shards in parallel
- ExecutorService with 3 threads (one per shard), 5-second timeout
- Cross-shard endpoints: filter by priority, filter by status, count by status
- Benchmark comparing single-shard vs scatter-gather latency

## Benchmark results
- Single-shard query: 1ms (direct route to correct shard)
- Scatter-gather across 3 shards: 28ms (parallel, but thread + connection overhead)
- 28x slower even with just 3 shards

## Key observations
- by-priority/HIGH: 16,701 results merged from 3 shards (4,553 + 5,472 + 6,676)
- count-by-status: returned 9 rows (3 statuses × 3 shards) — app must merge/sum
- Aggregations across shards require client-side post-processing

## How scatter-gather works
1. Scatter: submit same query to all shards in parallel (ExecutorService)
2. Wait: Future.get() with 5s timeout per shard
3. Gather: merge results, count per shard, report errors for failed shards
4. Return: combined result set to client

## Key concepts
- Design shard key to avoid cross-shard queries for common operations
- Scatter-gather is expensive — hits every shard even if most data is elsewhere
- Parallel execution helps but doesn't eliminate overhead
- Aggregations (GROUP BY, COUNT) need client-side merging
- At 100 shards, scatter-gather becomes a real bottleneck