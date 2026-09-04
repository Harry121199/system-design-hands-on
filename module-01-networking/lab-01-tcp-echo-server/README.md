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