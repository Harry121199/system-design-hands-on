# Module 02 — How the Web Works

## What this module covers

The full request lifecycle — from typing a URL to seeing a page. DNS resolution,
stateless vs stateful servers, and what sits between the client and your application.

## Labs

| # | Lab | Concept | Status |
|---|-----|---------|--------|
| 1 | [DNS Resolver](lab-01-dns-resolver/) | Domain to IP translation, caching, multiple IPs | ✅ Done |
| 2 | [Stateless vs Stateful Server](lab-02-stateful-vs-stateless/) | HTTP is stateless, sessions and tokens add state | ✅ Done |
| 3 | [Reverse Proxy with Filtering](lab-03-reverse-proxy/) | What sits between client and server in production | ✅ Done |


## Key takeaways

- DNS translates domain names to IPs — the first step of every web request, and results are cached
- HTTP is stateless — sessions store state on the server, tokens store state on the client
- Token-based auth (JWT) enables horizontal scaling because servers store nothing
- A reverse proxy handles logging, filtering, load balancing, and error handling in front of your app
- Everything we built here maps directly to production tools: DNS servers, Spring Security, Nginx