package rover;

public class Rover extends Thread {
	private byte id; // unique 8 bit number for Rover
	private String multicastIP;
	private int port;
	private Table table;
	private Thread sender;
	private Thread client;

	public Rover(byte id, String multicastIP, int port) {
		this.id = id;
		this.multicastIP = multicastIP;
		this.port = port;
		table = new Table(this.id);
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