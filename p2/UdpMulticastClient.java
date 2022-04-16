import java.net.*;
import java.io.*;

public class UdpMulticastClient implements Runnable {
    public Rover r;
    public String ip;
    public int port;
    public String multicastIP;
    public RoutingTable table;

    public UdpMulticastClient(Rover r) {
        this.r = r;
        this.port = r.port;
        this.multicastIP = r.multicastIP;
        this.table = r.table;
        this.ip = r.selfIP;
    }

    public void receiveUDPMessage() throws IOException {
        byte[] buffer = new byte[1024];

        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(multicastIP);
        socket.joinGroup(group);

        while(true) {
            try {
                DatagramPacket packet=new DatagramPacket(buffer,buffer.length);

                // blocking call.... waits for next packet
                socket.receive(packet);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( packet.getData() );
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                RoutingTable tt = (RoutingTable) objectInputStream.readObject();
                
                boolean c = this.r.updateTable(tt, packet.getAddress());
                c = c || this.r.cleanup();

                if (c) {
                    printTheRoutingTable(this.r.table);
                }

                if (Math.random() > 1) {
                    break;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        socket.leaveGroup(group);
        socket.close();
    }

    private void printTheRoutingTable(RoutingTable t) {
        System.out.println("Address\t\tNext Hop\t\tCost");
        System.out.println(t);
    }

    public void run(){
        try {
            receiveUDPMessage();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}