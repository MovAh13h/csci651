package rover;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Rover extends Thread {
	private byte id; // unique 8 bit number for Rover
	private String multicastIP;
	private int port;
	private Table table;
	private Thread sender;
	private Thread client;

	public Rover(byte id, String multicastIP, int port) throws UnknownHostException {
		this.id = id;
		this.multicastIP = multicastIP;
		this.port = port;
		table = new Table(this.id);
		InetAddress self = InetAddress.getByName("10.0." + id + ".0");
		InetAddress subnet = InetAddress.getByName("255.255.255.0");
		InetAddress gateway = InetAddress.getByName("127.0.0.1");
		table.add(new RoutingEntry(self, subnet, gateway, 0));
	}
	
	public byte getRoverID() {
		return this.id;
	}
	
	public String getMulticastIP() {
		return this.multicastIP;
	}

	public int getPort() {
		return this.port;
	}

	public Table getTable() {
		return this.table;
	}

	public void updateTable(Table t) {
		
	}

	@Override
	public void run() {
		client = new Thread(new Client(this));
		sender = new Thread(new Sender(this));

		client.start();
		sender.start();
	}
}