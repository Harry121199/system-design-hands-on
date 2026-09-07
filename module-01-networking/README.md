# Module 01 — Networking & Protocols

## What this module covers

How machines communicate over a network — TCP/IP, UDP, HTTP, sockets, and the
request-response lifecycle that every system design sits on top of.

## Labs

| # | Lab | Concept | Status |
|---|-----|---------|--------|
| 1 | [TCP Echo Server](lab-01-tcp-echo-server/) | TCP sockets, 3-way handshake, streams, blocking I/O | ✅ Done |
| 2 | [Multi-threaded Chat Server](lab-02-multithreaded-chat-server/) | Concurrency, thread-per-connection, synchronized shared state | ✅ Done |
| 3 | [UDP Ping Server](lab-03-udp-ping/) | TCP vs UDP, fire-and-forget, packet loss, RTT measurement | ✅ Done |
| 4 | [HTTP Server from Scratch](lab-04-http-server/) | HTTP is text over TCP, request/response format, routing | ✅ Done |

## Key takeaways

- TCP guarantees delivery and ordering via the 3-way handshake — at the cost of overhead
- UDP skips all guarantees for raw speed — packet loss is silent
- A single-threaded server handles one client at a time; thread-per-connection fixes this
- HTTP is structured text (request line + headers + body) sent over a TCP socket
- Spring Boot, Tomcat, Nginx — they all parse this same text format under the hood