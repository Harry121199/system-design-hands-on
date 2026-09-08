# Lab 03 — REST vs gRPC benchmark

## Problem
Opinions are everywhere — "gRPC is faster", "REST is simpler." Instead of
guessing, we measured payload size, serialization speed, and deserialization
speed with 10,000 iterations.

## Results (from my machine)
| Metric | JSON (REST) | Protobuf (gRPC) | Difference |
|--------|-------------|-----------------|------------|
| Payload size | 202 bytes | 123 bytes | 39% smaller |
| Serialization (10k) | 31ms | 11ms | 2.8x faster |
| Deserialization (10k) | 72ms | 14ms | 5.1x faster |

## What I learned
- Protobuf uses field tags (1 byte) instead of field names (11+ bytes) — that's why it's smaller
- JSON requires parsing text → objects (slow); Protobuf maps binary directly to fields (fast)
- Deserialization gap is bigger than serialization — JSON parsing is especially expensive
- REST wins on readability, browser support, and ecosystem tooling
- gRPC wins on performance, type safety, and contract enforcement
- Use REST for public/browser APIs, gRPC for internal service-to-service communication

## How to run
```bash
cd lab-03-benchmark
mvn compile
mvn exec:java "-Dexec.mainClass=com.systemdesign.benchmark.PayloadBenchmark"
```