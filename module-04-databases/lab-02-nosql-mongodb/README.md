# Lab 02 — NoSQL with MongoDB

## Problem
SQL forces rigid schemas — every row has the same columns. When data has nested
structures (comments, tags), variable fields, or needs horizontal scaling, document
databases like MongoDB offer a different model.

## What I learned
- @Document("tasks") maps a Java class to a MongoDB collection
- Documents can contain nested objects (comments) and arrays (tags) — no JOINs needed
- One read returns the complete document with all embedded data
- findByTagsContaining queries inside arrays — impossible in SQL without a join table
- Flexible schema: each document can have different fields
- @Document(collection = "tasks") causes a locale/collation bug — use @Document("tasks")
- Embedded MongoDB downloads a real mongod binary — first start is slow, then cached

## SQL vs MongoDB comparison
| | SQL (H2/PostgreSQL) | MongoDB |
|---|---|---|
| Storage unit | Row in a table | Document in a collection |
| Schema | Fixed columns | Flexible per document |
| Nested data | JOIN to separate tables | Embed inside the document |
| Array fields | Join table needed | Native array support |
| Transactions | ACID built in | Available but less common |
| Scaling | Vertical (bigger server) | Horizontal (more servers) |

## How to run
```bash
cd lab-02-nosql-mongodb
.\mvnw.cmd spring-boot:run
# First run downloads MongoDB binary (~200MB, cached after)
curl -s -X POST http://localhost:8081/api/v1/tasks -H "Content-Type: application/json" -d '{"title":"Test","description":"Doc model","priority":"HIGH","tags":["test"]}'
```