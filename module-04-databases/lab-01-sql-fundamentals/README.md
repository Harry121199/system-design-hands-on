# Lab 01 — SQL Fundamentals with H2

## Problem
In-memory data structures (ConcurrentHashMap) lose everything on restart.
Relational databases provide ACID guarantees and persist data permanently.

## What I learned
- @Entity + @Table maps a Java class to a database table
- @Id + @GeneratedValue lets the database auto-generate primary keys
- Spring Data JPA generates SQL from method names (findByStatus → SELECT WHERE status = ?)
- show-sql=true reveals every query JPA generates — essential for debugging
- H2 console provides a web UI to run SQL directly against the database
- EXPLAIN shows the query plan — how the database finds your data
- Without an index, the database does a full table scan (reads every row)
- CREATE INDEX makes queries fast by building a sorted lookup structure
- Compound indexes (status, priority) serve multi-column WHERE clauses in one lookup
- Single-column indexes don't combine — the database picks one and scans for the rest
- 50,000 rows is enough to see the difference; at millions, it's the difference between ms and minutes

## ACID properties
| Property | Meaning |
|----------|---------|
| Atomicity | Transaction fully completes or fully rolls back |
| Consistency | Data always satisfies constraints |
| Isolation | Concurrent transactions don't interfere |
| Durability | Committed data survives crashes |

## How to run
```bash
cd lab-01-sql-fundamentals
.\mvnw.cmd spring-boot:run
# H2 console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:taskdb, user: admin, pass: admin
```