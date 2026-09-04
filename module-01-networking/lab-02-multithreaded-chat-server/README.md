
## Lab 02 — Multi-threaded TCP Chat Server

### Problem
The echo server from Lab 01 handles only one client at a time. The second client's
connection sits in the OS backlog because `accept()` is never called again — the main
thread is stuck in the read/write loop for client 1.

### What I learned
- The main thread should only `accept()` connections in a loop and hand them off
- `Thread` + `Runnable` lets each client get its own read/write loop
- A shared `List<PrintWriter>` holds every connected client's output stream
- `broadcast()` loops through the list and writes to everyone except the sender
- `synchronized` prevents race conditions when multiple threads add/remove/read the shared list
- `setDaemon(true)` on the client's listener thread ensures it dies when the main thread exits
- This model works but has a limit: one OS thread per client means thousands of connections = thousands of threads = memory exhaustion

### How to run
```bash
cd lab-02-multithreaded-chat-server/src

# Terminal 1 — server
javac ChatServer.java
java ChatServer

# Terminal 2, 3, 4 — clients
javac ChatClient.java
java ChatClient
```