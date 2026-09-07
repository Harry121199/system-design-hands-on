import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReverseProxy {

    private static final int PROXY_PORT = 8080;
    private static final String[] BACKENDS = {"localhost:8081", "localhost:8082"};
    private static final Set<String> BLOCKED_PATHS = Set.of("/admin", "/secret");
    private static int currentIndex = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PROXY_PORT);
        System.out.println("Reverse proxy running on port " + PROXY_PORT);
        System.out.println("Backends: " + Arrays.toString(BACKENDS));
        System.out.println("Blocked paths: " + BLOCKED_PATHS);

        while (true) {
            Socket client = serverSocket.accept();
            new Thread(() -> handleClient(client)).start();
        }
    }

    private static void handleClient(Socket client) {
        try (
                BufferedReader clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream clientOut = client.getOutputStream()
        ) {
            // Step 1: Read the client's request
            String requestLine = clientIn.readLine();
            if (requestLine == null) { client.close(); return; }

            List<String> headers = new ArrayList<>();
            String header;
            while ((header = clientIn.readLine()) != null && !header.isEmpty()) {
                headers.add(header);
            }

            String path = requestLine.split(" ")[1];
            String clientAddress = client.getRemoteSocketAddress().toString();

            // Step 2: Log the request
            System.out.println("\n[" + LocalDateTime.now() + "] " + clientAddress + " → " + requestLine);

            // Step 3: Filter — block restricted paths
            if (BLOCKED_PATHS.contains(path)) {
                System.out.println("  BLOCKED: " + path);
                String body = "{\"error\": \"403 Forbidden\", \"path\": \"" + path + "\"}";
                String response =
                        "HTTP/1.1 403 Forbidden\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.getBytes().length + "\r\n" +
                        "\r\n" +
                        body;
                clientOut.write(response.getBytes());
                clientOut.flush();
                client.close();
                return;
            }

            // Step 4: Pick a backend (round-robin)
            String backend = getNextBackend();
            String[] parts = backend.split(":");
            String backendHost = parts[0];
            int backendPort = Integer.parseInt(parts[1]);

            System.out.println("  FORWARDING to " + backend);

            // Step 5: Forward the request to the backend
            try (Socket backendSocket = new Socket(backendHost, backendPort)) {
                OutputStream backendOut = backendSocket.getOutputStream();
                BufferedReader backendIn = new BufferedReader(
                        new InputStreamReader(backendSocket.getInputStream())
                );

                // Send original request + add proxy header
                backendOut.write((requestLine + "\r\n").getBytes());
                for (String h : headers) {
                    backendOut.write((h + "\r\n").getBytes());
                }
                backendOut.write(("X-Forwarded-For: " + clientAddress + "\r\n").getBytes());
                backendOut.write("\r\n".getBytes());
                backendOut.flush();

                // Step 6: Read backend response and forward to client
                StringBuilder backendResponse = new StringBuilder();
                int ch;
                while ((ch = backendSocket.getInputStream().read()) != -1) {
                    backendResponse.append((char) ch);
                }

                clientOut.write(backendResponse.toString().getBytes());
                clientOut.flush();

                System.out.println("  RESPONSE sent back to client");

            } catch (IOException e) {
                System.out.println("  BACKEND DOWN: " + backend);
                String body = "{\"error\": \"502 Bad Gateway\", \"backend\": \"" + backend + "\"}";
                String response =
                        "HTTP/1.1 502 Bad Gateway\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.getBytes().length + "\r\n" +
                        "\r\n" +
                        body;
                clientOut.write(response.getBytes());
                clientOut.flush();
            }

            client.close();

        } catch (IOException e) {
            System.err.println("Proxy error: " + e.getMessage());
        }
    }

    private static synchronized String getNextBackend() {
        String backend = BACKENDS[currentIndex];
        currentIndex = (currentIndex + 1) % BACKENDS.length;
        return backend;
    }
}