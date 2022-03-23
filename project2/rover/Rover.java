package rover;

import java.net.InetAddress;

public class Rover extends Thread {
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

	}

	@Override
	public void run() {
		receiver = new Receiver(this);
		receiver.start();

		sender = new Sender(this);
		sender.start();
	}

	public void cleanUpTable() {
		
	}
}