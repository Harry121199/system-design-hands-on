# Lab 03 — N+1 Problem

## What I built
- QueryCounter + Hibernate StatementInspector to count SQL queries per operation
- Benchmark endpoint comparing N+1 vs JOIN FETCH on 100 projects × 500 tasks
- Same data loaded, drastically different query counts

## Benchmark results (H2 in-memory)
- N+1: 111 queries, 245ms, 50,000 tasks
- JOIN FETCH: 1 query, 44ms, 50,000 tasks
- 110 queries saved, ~5.5x faster

## The N+1 problem explained
- JPA loads 100 projects in 1 query
- Then each `project.getTasks()` fires a separate SELECT
- 1 + 100 = 101 queries minimum (more with eager @ManyToOne)
- Invisible in dev (5 projects = 6 queries), devastating in prod (10,000 = 10,001)

## The fix
- `@Query("SELECT p FROM Project p JOIN FETCH p.tasks")`
- Database does the JOIN internally, returns one big result set
- JPA maps the combined rows back into Project + Task objects in memory
- 1 query instead of 101

## Why it matters at scale
- Each query = 1 network round trip (~2ms on real databases)
- 101 queries = 202ms of pure network wait
- 1 query = 2ms
- On disk-based DBs with millions of rows, N+1 can take 20+ seconds