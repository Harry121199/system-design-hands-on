# Lab 01 — DNS Resolver

## Problem
Browsers speak domain names (google.com). Machines speak IP addresses (142.250.146.139).
DNS translates between them. Every web request starts with a DNS lookup — understanding
it explains why first page loads are slower and why caching matters everywhere.

## What I learned
- `InetAddress.getAllByName()` triggers a DNS lookup through the OS
- Big domains return multiple IPs — the simplest form of load balancing
- `localhost` resolves from the local hosts file (0ms, no network needed)
- Corporate DNS servers can intercept failed lookups and redirect them
- Canonical hostnames reveal infrastructure (Google's 1e100.net, AWS EC2 names)
- DNS results are cached — second lookup is nearly instant
- Java caches DNS results within the JVM by default (controlled by `networkaddress.cache.ttl`)
- This is the same cache-aside pattern used in Redis, CDNs, and browser caches

## How to run
```bash
cd lab-01-dns-resolver/src
javac DnsResolver.java
java DnsResolver
```