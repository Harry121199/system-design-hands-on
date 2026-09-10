# Lab 04 — SQL vs NoSQL benchmark

## Problem
Instead of guessing which database is better, we measured insert speed,
query speed, and schema flexibility with 10,000 entries on both SQL (H2)
and NoSQL (MongoDB) in the same Spring Boot app.

## Results (from my machine, 10,000 entries)
| Test | SQL (H2) | MongoDB | Winner |
|------|----------|---------|--------|
| Insert 10k | 366ms | 303ms | MongoDB |
| Query by status | 256ms | 85ms | MongoDB |
| Compound query | 10ms | 16ms | SQL |
| Count | 11ms | 29ms | SQL |
| Schema flexibility | ALTER TABLE | Just add fields | MongoDB |

## What I learned
- No clear winner — each database excels at different operations
- MongoDB's insert and single-field query is faster due to less overhead
- SQL's query optimizer shines on compound queries after warmup
- MongoDB stores tags and metadata with zero schema changes
- SQL would need ALTER TABLE or new join tables for the same flexibility
- Use SQL for strict relationships and transactions
- Use MongoDB for self-contained documents and rapid schema evolution
- Both returned the same row counts (3311 TODO, 1063 TODO+HIGH) — correctness confirmed

## How to run
```bash
cd lab-04-sql-vs-nosql
.\mvnw.cmd spring-boot:run
curl -s http://localhost:8082/benchmark/run?count=10000
```