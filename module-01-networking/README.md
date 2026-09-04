# System Design Hands-On

> A hands-on, module-by-module implementation of system design concepts using **Java 21 + Spring Boot**.  
> Based on the [roadmap.sh System Design Roadmap](https://roadmap.sh/system-design).

## Roadmap

| Phase | Module | Topic | Status        |
|-------|--------|-------|---------------|
| 1 - Fundamentals | `module-01` | Networking & Protocols | 🔄 1/4 labs |
| 1 - Fundamentals | `module-02` | How the Web Works | ⬜ Not Started |
| 1 - Fundamentals | `module-03` | API Design (REST, gRPC, GraphQL) | ⬜ Not Started |
| 2 - Data & Storage | `module-04` | Databases (SQL vs NoSQL) | ⬜ Not Started |
| 2 - Data & Storage | `module-05` | Caching Strategies | ⬜ Not Started |
| 2 - Data & Storage | `module-06` | Data Modeling | ⬜ Not Started |
| 3 - Scalability | `module-07` | Load Balancing, CDN & Proxies | ⬜ Not Started |
| 3 - Scalability | `module-08` | Sharding & Partitioning | ⬜ Not Started |
| 4 - Distributed Systems | `module-09` | CAP, Consistency & Consensus | ⬜ Not Started |
| 4 - Distributed Systems | `module-10` | Message Queues & Event-Driven | ⬜ Not Started |
| 5 - Advanced Patterns | `module-11` | Microservices, CQRS, Saga, Rate Limiting | ⬜ Not Started |
| 6 - Capstone | `module-12` | Design Problems (URL Shortener, Chat, etc.) | ⬜ Not Started |

## Tech Stack

- Java 21
- Spring Boot 4.1.x
- Maven
- IntelliJ IDEA

## How Each Module Works

1. **Problem statement** — a real-world scenario that motivates the concept
2. **Break it down** — decompose to the fundamental unit
3. **Step-by-step implementation** — code it, one step at a time
4. **Verify & reflect** — run it, break it, understand trade-offs

# Module 01 — Networking & Protocols

## What this module covers

Understanding how machines communicate over a network — TCP/IP, UDP, sockets, and the
request-response lifecycle that every system design sits on top of.

## Labs

| # | Lab | Concept | Status |
|---|-----|---------|--------|
| 1 | TCP Echo Server | TCP sockets, 3-way handshake, streams, blocking I/O | ✅ Done |
| 2 | Multi-threaded TCP Chat Server | Concurrency, thread-per-connection model | ⬜ Next |
| 3 | UDP Ping Server | TCP vs UDP trade-offs | ⬜ Not Started |
| 4 | HTTP Server from Scratch | HTTP is text over TCP | ⬜ Not Started |

## Lab 01 — TCP Echo Server

### Problem
Two machines need to exchange data reliably. TCP guarantees bytes arrive in order
or the connection fails. An echo server is the simplest proof of this.

### What I learned
- `ServerSocket` binds to a port and listens for incoming connections
- `accept()` blocks until a client completes the 3-way handshake (SYN → SYN-ACK → ACK)
- `Socket` represents one established bidirectional TCP connection
- `InputStream` / `OutputStream` are the raw byte pipes inside a socket
- `BufferedReader` and `PrintWriter` wrap those pipes for line-by-line text I/O
- The client gets a random ephemeral port; the server listens on a fixed port

### How to run
```bash
cd lab-01-tcp-echo-server/src

# Terminal 1 — start the server
javac EchoServer.java
java EchoServer

# Terminal 2 — start the client
javac EchoClient.java
java EchoClient
```