# Module 04 — Databases

## What this module covers

How to persist data reliably — SQL vs NoSQL trade-offs, ACID guarantees,
indexing internals, and choosing the right database for the right problem.

## Labs

| # | Lab | Concept | Status |
|---|-----|---------|--------|
| 1 | [SQL Fundamentals](lab-01-sql-fundamentals/) | Relational model, ACID, JPA, indexes, query plans | ✅ Done |
| 2 | [NoSQL with MongoDB](lab-02-nosql-mongodb/) | Document model, flexible schema, embedded data | ✅ Done |
| 3 | [Build your own B-Tree](lab-03-btree-index/) | How CREATE INDEX works internally — from scratch | ✅ Done |
| 4 | [SQL vs NoSQL benchmark](lab-04-sql-vs-nosql/) | Same domain, both databases, real numbers | ✅ Done |

## Key takeaways

- SQL databases provide ACID guarantees — atomicity, consistency, isolation, durability
- NoSQL document databases embed related data inside documents — no JOINs needed
- Indexes turn O(n) full scans into O(log n) lookups using B-Tree data structures
- A B-Tree with 10,000 entries needs only 9 node reads vs 10,000 row scans
- Neither SQL nor NoSQL is universally better — the choice depends on data shape and access patterns
- SQL for relationships, transactions, and complex queries
- MongoDB for flexible schemas, embedded documents, and write-heavy workloads