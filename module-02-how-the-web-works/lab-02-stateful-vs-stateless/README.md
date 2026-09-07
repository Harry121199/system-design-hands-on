# Lab 02 — Stateless vs Stateful Server

## Problem
HTTP is stateless — every request is independent. But apps need to remember users.
Two approaches exist: session-based (server stores state) and token-based (client
carries state). The choice directly impacts scalability.

## What I learned
- `HttpSession` stores user data in server memory, identified by a JSESSIONID cookie
- Server restart destroys all sessions — every user is logged out
- Sessions tie a user to a specific server instance (sticky sessions)
- JWT tokens are self-contained — username, cart, and timestamp are encoded inside
- The server stores nothing — it just verifies the signature and reads the data
- Token-based state survives server restarts because state lives on the client
- Stateless servers can scale horizontally — any server handles any request
- Trade-off: tokens grow larger as you add more claims; sessions keep a small cookie

## How to run
```bash
cd lab-02-stateful-vs-stateless
mvnw.cmd spring-boot:run

# Session-based
curl.exe -v -X POST "http://localhost:8080/session/login?username=Harry" -c cookies.txt
curl.exe -b cookies.txt -X POST "http://localhost:8080/session/cart/add?item=Laptop"
curl.exe -b cookies.txt "http://localhost:8080/session/cart"

# Token-based
curl.exe -s -X POST "http://localhost:8080/token/login?username=Harry"
curl.exe -s -X POST -H "Authorization: Bearer TOKEN" "http://localhost:8080/token/cart/add?item=Laptop"
curl.exe -s -H "Authorization: Bearer TOKEN" "http://localhost:8080/token/cart"
```