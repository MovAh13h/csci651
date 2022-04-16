package rover;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender extends Thread {
	private Rover r;
	private boolean cleanUp = true;

	public Sender(Rover r) {
		this.r = r;
	}

	public void send() throws Exception {
		try (DatagramSocket s = new DatagramSocket()) {
			InetAddress g = InetAddress.getByName(this.r.multicastIP);
			System.out.println(this.r.table);
			Packet p = new Packet(this.r.table, (byte) 2);
			byte[] pbytes = p.getBytes();

			DatagramPacket packet = new DatagramPacket(pbytes, pbytes.length, g, this.r.port);
			s.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				cleanUp = !cleanUp;

				// every 10 seconds
				if (cleanUp) {
					this.r.table.cleanUpTable();
				}

				this.send();

				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}