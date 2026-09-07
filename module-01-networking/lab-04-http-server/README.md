# Lab 04 — HTTP Server from Scratch

## Problem
Every web framework (Spring Boot, Express, Django) speaks HTTP. But HTTP is not
magic — it's structured text over a TCP connection. Building an HTTP server from
raw sockets reveals exactly what frameworks abstract away.

## What I learned
- HTTP request = request line + headers + blank line + body
- Request line format: `METHOD /path HTTP/1.1`
- Headers are key-value pairs, one per line, ending at the blank line
- HTTP response = status line + headers + blank line + body
- `Content-Length` tells the client how many bytes to read for the body
- `Content-Type` tells the client how to interpret the body
- `Connection: close` ends the conversation after one response
- Routing is just matching the method + path string
- Spring Boot's @GetMapping is a fancy version of the if/else chain I wrote
- The browser sends 10+ headers for a simple page visit

## HTTP request/response format
```
GET /hello HTTP/1.1 → request line
Host: localhost:8080 → header
Accept: text/html → header
→ blank line (end of headers)

HTTP/1.1 200 OK → status line
Content-Type: text/html → header
Content-Length: 20 → header
→ blank line (end of headers)

<h1>Hello World</h1> → body 
````
## How to run
cd lab-04-http-server/src
javac SimpleHttpServer.java
java SimpleHttpServer
# Open browser: http://localhost:8080/hello