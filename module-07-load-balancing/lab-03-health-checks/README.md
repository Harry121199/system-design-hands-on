# Lab 03 — Health Checks + Failover

## What I built
- /health endpoint on Task API returning {"status": "UP"}
- Background health checker pinging backends every 5 seconds
- Automatic failover: dead backends skipped, recovered backends restored
- Ring-walk failover: walks clockwise to next healthy node (not random pick)
- 503 Service Unavailable when all backends are down

## How it works
- ScheduledExecutorService runs checkHealth() every 5 seconds
- checkHealth() opens TCP socket, sends GET /health, checks for 200
- healthyBackends (ConcurrentHashMap set) tracks live servers
- pickBackend() builds a skip set of unhealthy nodes, passes to getNodeSkipping()
- getNodeSkipping() walks clockwise on the hash ring, skipping dead nodes

## Failover behavior
- Both alive: harry → 8082, alice → 8081 (consistent hashing)
- 8082 killed: harry → 8081 (walks clockwise to next node), alice → 8081
- 8082 recovered: harry → 8082 (returns to original), alice → 8081
- Minimal key disruption — only the dead server's keys move

## Key concepts
- Health checks detect failure before users hit it
- Ring-walk failover preserves consistent hashing guarantees
-