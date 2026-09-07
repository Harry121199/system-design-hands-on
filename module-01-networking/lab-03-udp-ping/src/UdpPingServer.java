import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.Random;

public class UdpPingServer {

    private static final int PORT = 7072;
    private static final double DROP_RATE = 0.3;

    public static void main(String[] args) throws Exception{
        DatagramSocket socket = new DatagramSocket(PORT);
        Random random = new Random();
        System.out.println("UDP Ping server listening on port "+ PORT +"...");

        byte[] buffer = new byte[1024];

        while(true){
            // Recieve a packet
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            String message = new String(request.getData(),0,request.getLength());

            // Simulate packet loss
            if(random.nextDouble() < DROP_RATE){
                System.out.println("DROPPED: " + message + " from " + request.getAddress() + ":" + request.getPort());
                continue;
            }

            System.out.println("Received from "+request.getAddress().getHostAddress()+": "+message);

            // Build Response
            String response = "PONG" + Instant.now();
            byte[] responseBytes = response.getBytes();

            // Send response back to whoever sent the request
            DatagramPacket reply = new DatagramPacket(
                    responseBytes,
                    responseBytes.length,
                    request.getAddress(),
                    request.getPort()
            );
            socket.send(reply);
        }
    }
}