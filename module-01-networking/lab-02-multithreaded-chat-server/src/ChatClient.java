import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient{
    private static final String HOST = "localhost";
    private static final int PORT = 7071;

    public static void main(String[] args){
        System.out.println("Connecting to chat server...");
        try(Socket socket = new Socket(HOST,PORT);Scanner scanner = new Scanner(System.in)){
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            //Separate thread to listen for incoming messages
            Thread listener = new Thread(()->{
               try{
                   String serverMessage;
                   while((serverMessage = in.readLine())!=null){
                       System.out.println(serverMessage);
                   }
               }catch (IOException e){
                   System.out.println("Disconnected from server");
               }
            });
            listener.setDaemon(true);
            listener.start();

            // Main thread reads user input and sends to server
            System.out.println("Enter 'exit' to exit");
            while(true){
                String message = scanner.nextLine();
                if("exit".equals(message)){break;}
                out.println(message);
            }
            System.out.println("Diconnected.");
        }catch (IOException e){
            System.err.println("Client error: "+e.getMessage());
        }
    }
}