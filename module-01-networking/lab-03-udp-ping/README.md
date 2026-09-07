# Lab 03 — UDP Ping Server

## Problem
TCP's reliability comes at a cost — handshake overhead, retransmission delays,
ordering buffers. For use cases like gaming, video streaming, and DNS, speed matters
more than guaranteed delivery. UDP skips all guarantees for raw speed.

## What I learned
- `DatagramSocket` binds to a port without any listening or handshake
- `DatagramPacket` is a self-contained envelope: data + destination address + port
- `socket.receive()` blocks until a packet arrives — no connection exists
- Each packet is independent — the server has no concept of "a client"
- `setSoTimeout()` is essential — without it, a lost reply blocks forever
- Round-trip time (RTT) is measured by timing send → receive
- Packet loss is silent — no error, no notification, just a timeout
- 30% simulated drop rate showed real packet loss behavior

## TCP vs UDP summary
| | TCP | UDP |
|---|---|---|
| Connection | 3-way handshake | None |
| Reliability | Guaranteed | Fire and forget |
| Ordering | In order | Any order |
| Speed | Slower | Faster |
| Java classes | Socket, ServerSocket | DatagramSocket, DatagramPacket |

## How to run
```bash
cd lab-03-udp-ping/src

# Terminal 1
javac UdpPingServer.java
java UdpPingServer

# Terminal 2
javac UdpPingClient.java
java UdpPingClient
```