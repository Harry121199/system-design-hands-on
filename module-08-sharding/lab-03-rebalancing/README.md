# Lab 03 — Rebalancing

## What I built
- Dynamic shard addition: shard-d starts empty, not in the hash ring
- Rebalance endpoint detects which projects moved after ring update
- Migrates projects + tasks from old shards to new shard
- Delete-after-write ensures data exists on at least one shard at all times

## Rebalance results
- 28 out of 100 projects migrated (28%, close to ideal 25%)
- 14,000 tasks moved in 138ms
- Data pulled from all 3 source shards (9 from A, 6 from B, 13 from C)

## Before → After distribution
- shard-a: 27 → 18 projects
- shard-b: 33 → 27 projects
- shard-c: 40 → 27 projects
- shard-d: 0 → 28 projects

## How rebalancing works
1. Create schema on new shard
2. Snapshot current project→shard assignments
3. Add new shard to consistent hash ring
4. Compare before vs after — detect which projects need to move
5. For each migration: read from source → write to destination → delete from source
6. Routing immediately reflects new assignments

## Key concepts
- Consistent hashing minimizes data movement (~1/N of keys move)
- Shard-c lost the most keys (closest clockwise neighbor to shard-d)
- Delete-after-write prevents data loss during migration
- Real systems use dual-read (check old + new shard) during migration window
- Hot migrations in production need versioning or change-data-capture to handle writes during migration