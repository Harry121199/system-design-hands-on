# Lab 02 — Normalization vs Denormalization

## What I built
- Created DenormalizedTask entity — flat table with project_name, user_name copied in
- Loaded 50,000 denormalized tasks alongside 50,000 normalized tasks
- Benchmark endpoint comparing reads and writes on both schemas

## Benchmark results (H2 in-memory, warm run)
- Normalized read (3-table JOIN): ~0ms
- Denormalized read (single table): ~10ms
- Normalized write (1 row): ~0ms
- Denormalized write (5,000 rows): ~20ms

## Why normalized reads won here
- H2 is in-memory — JOINs are nearly free (no disk seeks)
- Native query returns raw arrays; Spring Data maps to entities (overhead)
- On disk-based databases with millions of rows, JOINs get expensive

## The real tradeoff
- Writes expose the true cost: 1 row vs 5,000 rows for a name change
- Denormalization risks inconsistency if partial updates fail
- Rule: normalize first, denormalize only when reads are the proven bottleneck

## Key concept
- Normalization = one source of truth, JOINs to read
- Denormalization = duplicated data, fast reads, expensive writes