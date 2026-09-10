# Lab 03 — Build your own B-Tree index

## Problem
CREATE INDEX makes queries fast, but what actually gets created? A B-Tree — a
balanced tree that stays wide and short, minimizing disk reads. We built one
from scratch to see exactly how databases find your data.

## What I learned
- B-Tree nodes hold multiple keys (unlike binary trees with just 1)
- More keys per node = shorter tree = fewer disk reads per lookup
- Insert always goes to a leaf, then splits upward if the node overflows
- The tree grows upward from splits, not downward — that's why it stays balanced
- Search follows comparisons at each level — eliminating 2/3 of entries per step
- 10,000 entries in an order-4 tree = height 8 = at most 9 node reads
- Full scan reads all 10,000 entries; B-Tree reads 9 — 1,111x fewer
- At 1 million rows, a B-Tree reads ~13 nodes vs 1,000,000 rows scanned
- This is the exact data structure PostgreSQL, MySQL, and H2 use for indexes

## Results from my machine
| Entries | Tree height | Reads per lookup | Full scan reads | Speedup |
|---------|-------------|------------------|-----------------|---------|
| 12 | 2 | 3 | 12 | 4x |
| 10,000 | 8 | 9 | 10,000 | 1,111x |

## How to run
```bash
cd lab-03-btree-index/src
javac BTree.java BTreeDemo.java
java BTreeDemo
```