# Lab 01 — Shard Router

## What I built
- Three independent H2 databases (shard-a, shard-b, shard-c) in one Spring Boot app
- Shard router using consistent hash ring from Module 07 Lab 01
- Routes by project ID — all tasks for a project co-located on same shard
- 100 projects × 500 tasks = 50,000 tasks distributed across 3 shards

## Shard distribution
- shard-a: 27 projects, 13,500 tasks
- shard-b: 33 projects, 16,500 tasks
- shard-c: 40 projects, 20,000 tasks

## How it works
- ShardConfig creates 3 DataSource beans (separate H2 in-memory DBs)
- ShardRouter places shard names on consistent hash ring (150 virtual nodes)
- getShardForProject(id) hashes "project-{id}", walks clockwise, returns DataSource
- Controller uses raw JDBC (not JPA) — we control which DB gets each query

## API
- GET /api/v1/projects/{id}/tasks — routes to correct shard, returns tasks
- GET /api/v1/shard-map — shows project/task counts per shard

## Key concepts
- Co-location: related data on same shard avoids cross-shard queries
- Consistent hashing: adding/removing shards moves minimal data
- Raw JDBC over JPA: sharding requires explicit control over which DB to query
- No single database bottleneck: reads/writes scale horizontally