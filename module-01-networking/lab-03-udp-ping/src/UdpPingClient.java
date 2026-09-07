import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UdpPingClient {
    private static final String HOST = "localhost";
    private static final int PORT = 7072;
    private static final int PING_COUNT = 10;
    private static final int TIMEOUT_MS = 1000;

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);

        InetAddress address = InetAddress.getByName(HOST);

        long totalTime = 0;
        int received = 0;
        int lost = 0;

        System.out.println("Pinging "+HOST+":"+PORT+" WITH "+PING_COUNT + " packets...\n");

        for(int i = 1; i<= PING_COUNT; i++) {
            String message = "PING " + i;
            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);

            long startTime = System.nanoTime();
            socket.send(sendPacket);

            try {
                byte[] buffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);

                long rtt = (System.nanoTime() - startTime) / 1_000_000;

                totalTime += rtt;
                received++;
            } catch (SocketTimeoutException e) {
                System.out.println("Request "+i+" timed out.");
                lost++;
            }
        }

        System.out.println("\n--- Ping statistics ---");
        System.out.println("Sent: " + PING_COUNT + ", Received: " + received + ", Lost: " + lost);
        if(received > 0){
            System.out.println("Avg RTT: " + (totalTime / received) + "ms");
        }
        socket.close();
    }
}