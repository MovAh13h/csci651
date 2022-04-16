package rover;

import java.net.InetAddress;

public class Rover {
	byte id;
	InetAddress ip;
	String multicastIP;
	int port;
	Thread receiver;
	Thread sender;
	RoutingTable table;

	public Rover(byte id, String multicastIP, int port) {
		this.id = id;
		this.multicastIP = multicastIP;
		this.port = port;
		this.table = new RoutingTable(id);
		System.out.println(this.table);
		try {
			ip = InetAddress.getByAddress(new byte[] {10, 0, id, 0});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println(this.table);
		receiver = new Receiver(this);
		receiver.start();

		sender = new Sender(this);
		sender.start();
	}
}