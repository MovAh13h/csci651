package rover;

import java.io.IOException;
import java.net.*;

import rip.Packet;

class Sender implements Runnable {
	private Rover rover;
	private boolean cleanUp = true;

	public Sender(Rover r) {
		this.rover = r;
	}

	public void startSend() throws IOException {
		try (DatagramSocket socket = new DatagramSocket()) {
			String multicastIP = this.rover.getMulticastIP();
			InetAddress group = InetAddress.getByName(multicastIP);

			Table t = this.rover.getTable();

			Packet p = new Packet(t, (byte)2);
			byte[] pbytes = p.getBytes();
			
			DatagramPacket packet = new DatagramPacket(pbytes, pbytes.length, group, this.rover.getPort());
			
			socket.send(packet);
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

				// every 10 seconds clean up
				if (cleanUp) {
					this.rover.cleanRouteTable();
				}

				this.startSend();
				// Send routing table every 5 seconds
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}