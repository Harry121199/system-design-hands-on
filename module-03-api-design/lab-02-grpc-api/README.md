# Lab 02 — gRPC API

## Problem
REST sends JSON text over HTTP/1.1 — readable but verbose. When services talk to each
other thousands of times per second, that overhead matters. gRPC uses binary Protobuf
over HTTP/2 for smaller payloads and type-safe contracts.

## What I learned
- The .proto file IS the contract — both client and server generate code from it
- Field tags (= 1, = 2) replace field names in binary encoding — much smaller
- Task.newBuilder().setTitle(...).build() — immutable, generated, type-safe
- responseObserver.onNext() sends the response, onCompleted() closes the stream
- gRPC uses Status codes (NOT_FOUND, INVALID_ARGUMENT) instead of HTTP status codes
- The client calls stub.createTask() like a local method — no JSON, no URL construction
- Protobuf handles serialization/deserialization — no Jackson, no manual parsing
- Compile-time safety: typos in field names are caught by the compiler, not at runtime

## REST vs gRPC comparison
| | REST | gRPC |
|---|---|---|
| Contract | Implicit (docs/Swagger) | Explicit (.proto file) |
| Encoding | JSON (text) | Protobuf (binary) |
| Transport | HTTP/1.1 | HTTP/2 |
| Type safety | Runtime errors | Compile-time errors |
| Client code | Manual HTTP + JSON parsing | Generated stub methods |
| Error handling | HTTP status codes | gRPC Status codes |

## How to run
```bash
cd lab-02-grpc-api
mvn compile

# Terminal 1
mvn exec:java "-Dexec.mainClass=com.systemdesign.grpc.GrpcServer"

# Terminal 2
mvn exec:java "-Dexec.mainClass=com.systemdesign.grpc.GrpcClient"
```