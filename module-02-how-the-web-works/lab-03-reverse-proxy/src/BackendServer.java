import java.io.*;
import java.net.*;

public class BackendServer {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        String name = args.length > 1 ? args[1] : "Server-" + port;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(name + " running on port " + port);

            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handle(client, name, port)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handle(Socket client, String name, int port) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream out = client.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) { client.close(); return; }

            System.out.println(name + " received: " + requestLine);

            // Read headers
            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                // consume
            }

            // Parse path
            String path = requestLine.split(" ")[1];

            String body = "{\"server\": \"" + name + "\", \"port\": " + port + ", \"path\": \"" + path + "\"}";

            String response =
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + body.getBytes().length + "\r\n" +
                    "\r\n" +
                    body;

            out.write(response.getBytes());
            out.flush();
            client.close();

        } catch (IOException e) {
            System.err.println("Handler error: " + e.getMessage());
        }
    }
}