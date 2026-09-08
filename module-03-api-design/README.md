# Module 03 — API Design

## What this module covers

How frontend and backend communicate — REST, gRPC, and the trade-offs between them.
Building the same Task Management API in both styles and comparing payload size, latency,
and developer experience.

## Labs

| # | Lab | Concept | Status |
|---|-----|---------|--------|
| 1 | [REST API](lab-01-rest-api/) | Resource design, HTTP verbs, status codes, filtering | ✅ Done |
| 2 | [gRPC API](lab-02-grpc-api/) | Binary protocol, Protobuf, code generation, type safety | ✅ Done |
| 3 | [Comparison benchmark](lab-03-benchmark/) | Payload size, serialization speed, DX side by side | ✅ Done |

## Key takeaways

- REST maps resources to URLs and uses HTTP verbs — simple, readable, browser-native
- gRPC uses binary Protobuf over HTTP/2 — smaller, faster, type-safe
- The .proto file is the contract — both sides generate code from it, eliminating mismatches
- Protobuf is ~40% smaller and 3-5x faster than JSON for the same data
- REST for public APIs and browsers, gRPC for internal service-to-service communication
- API versioning (v1 in the URL) protects existing clients when the API evolves