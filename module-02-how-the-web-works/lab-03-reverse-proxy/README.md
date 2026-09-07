# Lab 03 — Reverse proxy with filtering

## Problem
In production, clients never connect directly to application servers. A reverse proxy
sits in front, handling logging, security filtering, load distribution, and error
handling — keeping the backend servers hidden and protected.

## What I learned
- A reverse proxy accepts the client's TCP connection, then opens a new one to the backend
- Round-robin load balancing alternates requests across backends evenly
- Path-based filtering blocks restricted routes before they reach the backend
- X-Forwarded-For header preserves the client's real IP through the proxy
- When a backend is down, the proxy catches the IOException and returns 502 Bad Gateway
- The client only sees the proxy's address — backend servers are completely hidden
- This is exactly what Nginx, Envoy, and HAProxy do in production
- synchronized on getNextBackend() prevents race conditions in backend selection

## How to run
```bash
cd lab-03-reverse-proxy/src
javac BackendServer.java ReverseProxy.java

# Terminal 1 — backend A
java BackendServer 8081 Backend-A

# Terminal 2 — backend B
java BackendServer 8082 Backend-B

# Terminal 3 — proxy
java ReverseProxy

# Terminal 4 — test
curl.exe -s "http://localhost:8080/hello"
curl.exe -s "http://localhost:8080/admin"
```