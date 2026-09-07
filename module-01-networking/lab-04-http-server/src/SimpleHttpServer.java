import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class SimpleHttpServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("HTTP server running on http://localhost:" + PORT);

        while (true) {
            Socket client = serverSocket.accept();

            Thread handler = new Thread(() -> handleRequest(client));
            handler.start();
        }
    }

    private static void handleRequest(Socket client) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream out = client.getOutputStream()
        ) {
            // Step 1: Read the request line (e.g., "GET /hello HTTP/1.1")
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                client.close();
                return;
            }

            System.out.println("Request: " + requestLine);

            // Step 2: Read all headers (until blank line)
            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                System.out.println("  Header: " + headerLine);
            }

            // Step 3: Parse the method and path
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Step 4: Route the request
            String responseBody;
            int statusCode;
            String statusText;

            if (method.equals("GET") && path.equals("/")) {
                statusCode = 200;
                statusText = "OK";
                responseBody = "<h1>Welcome</h1><p>Server time: " + LocalDateTime.now() + "</p>";

            } else if (method.equals("GET") && path.equals("/hello")) {
                statusCode = 200;
                statusText = "OK";
                responseBody = "<h1>Hello World</h1>";

            } else if (method.equals("GET") && path.equals("/health")) {
                statusCode = 200;
                statusText = "OK";
                responseBody = "{\"status\": \"UP\"}";

            } else {
                statusCode = 404;
                statusText = "Not Found";
                responseBody = "<h1>404 Not Found</h1><p>" + path + " does not exist.</p>";
            }

            // Step 5: Build the HTTP response
            String response =
                    "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + responseBody.getBytes().length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    responseBody;

            out.write(response.getBytes());
            out.flush();
            client.close();

        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        }
    }
}