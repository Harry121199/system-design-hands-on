import java.io.*;
import java.net.*;

public class EchoServer {
    private static final int PORT = 7070;

    public static void main(String[] args) {
        System.out.println("Echo server starting on port " + PORT);

        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server Listening ... waiting for client.");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

            //Get the input and output streams from the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            //Echo loop - read a line, send it back
            String message;
            while((message = in.readLine()) != null){
                System.out.println("Received: "+message);
                out.println("ECHO: " + message);
            }
            System.out.println("Client disconnected");
        } catch (IOException e) {
            System.err.println("Server error: "+e.getMessage());
        }
    }
}