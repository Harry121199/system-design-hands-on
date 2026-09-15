# Module 06 — Data Modeling

## Overview
Evolved the flat Task API into a relational domain with Users, Projects, and Tasks.
Explored normalization vs denormalization tradeoffs and diagnosed the N+1 query problem.

## Labs

### Lab 01 — JPA Relationships
- Added User (1) → Project (many) → Task (many) with @ManyToOne / @OneToMany
- Foreign keys: projects.user_id, tasks.project_id
- @JsonIgnore to prevent circular serialization
- 10 users × 10 projects × 500 tasks = 50,000 tasks

### Lab 02 — Normalization vs Denormalization
- Denormalized table with project_name, user_name copied into each task row
- Normalized write: 1 row updated. Denormalized write: 5,000 rows updated
- Rule: normalize first, denormalize only when reads are the proven bottleneck

### Lab 03 — N+1 Problem
- N+1: 111 queries, 245ms for 100 projects with tasks
- JOIN FETCH: 1 query, 44ms for the same data
- 110 queries saved, ~5.5x faster on in-memory H2
- On real databases with network latency, the difference is 100x+

## Key takeaways
- Foreign keys live on the "many" side (owning side of the relationship)
- Denormalization trades write complexity for read speed
- N+1 is invisible in dev, devastating in prod — always check query counts
- JOIN FETCH is the standard fix — one query instead of N+1