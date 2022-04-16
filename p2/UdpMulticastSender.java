import java.io.*;
import java.net.*;

public class UdpMulticastSender implements Runnable  {
    public int port; 
    public String multicastIP;
    public String ip;
    public RoutingTable table;

    public UdpMulticastSender(Rover r) {
        this.port = r.port;
        this.multicastIP = r.multicastIP;
        this.table = r.table;
        this.ip = r.selfIP;
    }

    public void sendUdpMessage() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(multicastIP);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(oStream);
        objStream.writeObject(table);
        objStream.flush();

        byte[] msg = oStream.toByteArray();
        DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);

        socket.send(packet);
        socket.close();
    }

    public void run(){
        while (true) {
            try {
                sendUdpMessage();
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}