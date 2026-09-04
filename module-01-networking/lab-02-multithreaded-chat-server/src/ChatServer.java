import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer{
    private static final int PORT = 7071;
    private static final List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args){
        System.out.println("Chat Server starting on port " + PORT + ".....");
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server listening... waiting for clients.");
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected."+ clientSocket.getRemoteSocketAddress());

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        }catch (IOException e){
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable{
        private final Socket socket;
        private PrintWriter out;
        public ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try{
                BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(),true);
                out.println("Welcome! you are connected. Type messages:");

                synchronized (clientWriters){
                    clientWriters.add(out);
                }

                String message;
                while((message = in.readLine()) != null){
                    System.out.println("Received from: " + socket.getRemoteSocketAddress()+": "+message);
                    broadcast(message, this);
                }
            }catch (IOException e){
                System.err.println("Client disconnected: " + socket.getRemoteSocketAddress());
            }finally {
                synchronized (clientWriters){
                    clientWriters.remove(out);
                }
                try{
                    socket.close();
                }catch (IOException ignored){}
            }
        }
        private void broadcast(String message, ClientHandler sender){
            String fullMessage = "["+socket.getRemoteSocketAddress()+"]: "+message;
            synchronized (clientWriters){
                for(PrintWriter clientWriter : clientWriters){
                    if(clientWriter != sender.out) clientWriter.println(fullMessage);
                }
            }
        }
    }
}