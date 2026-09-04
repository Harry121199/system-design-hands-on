import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoClient {
    private static final String HOST = "localhost";
    private static final int PORT = 7070;

    public static void main(String[] args){
        System.out.println("Connecting to server at" + HOST + ":" + PORT);

        try(Socket socket = new Socket(HOST,PORT); Scanner scanner = new Scanner(System.in)){
            System.out.println("Connected! type message (type 'exit' to quit");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);


            String message;
            while(true){
                System.out.println("> ");
                message = scanner.nextLine();

                if("exit".equalsIgnoreCase(message)) break;

                out.println(message);
                String response = in.readLine();
                System.out.println("Server: "+ response);
            }
            System.out.println("Disconnected");
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }
}