# Lab 01 — LRU cache from scratch

## Problem
Every request hits the database even when the same data was fetched moments ago.
An LRU cache keeps recently accessed data in memory and evicts the least recently
used item when capacity is reached.

## What I learned
- LRU = HashMap (O(1) lookup) + Doubly Linked List (O(1) reorder)
- Dummy head/tail nodes eliminate null checks in linked list operations
- get() moves the accessed node to the head — it's now "most recent"
- put() adds to head, evicts from tail when capacity exceeded
- The evicted item is always the one that hasn't been accessed the longest
- Accessing a key saves it from eviction — that single get() changes the outcome
- 80/20 Zipf distribution: 100-item cache served 82% of 100,000 queries
- Hit rate depends on access pattern — random access = low hit rate, skewed access = high hit rate

## How to run
```bash
cd lab-01-lru-cache/src
javac LRUCache.java LRUCacheDemo.java
java LRUCacheDemo
```