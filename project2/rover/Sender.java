package rover;

import java.io.IOException;
import java.net.*;

import rip.Packet;

class Sender implements Runnable {
	private Rover rover;

	public Sender(Rover r) {
		this.rover = r;
	}

	public void startSend() throws IOException {
		try (DatagramSocket socket = new DatagramSocket()) {
			String multicastIP = this.rover.getMulticastIP();
			InetAddress group = InetAddress.getByName(multicastIP);

			Table t = this.rover.getTable();

			// TODO: Convert table to RIP Packet here
			//       and send that rip packet over the socket
			Packet p = new Packet(t, this.rover.getRoverID());
			byte[] pbytes = p.getBytes();
			
			// how many bytes of data exists?
			byte[] lb = new byte[] {
				(byte) (pbytes.length >>> 24),
				(byte) (pbytes.length >>> 16),
				(byte) (pbytes.length >>> 8),
				(byte) (pbytes.length >>> 0),
			};

			byte[] merger = new byte[lb.length + pbytes.length];

			for (int i = 0; i < 4; i++) merger[i] = lb[i];
			for (int i = 4; i < pbytes.length + 4; i++) merger[i] = pbytes[i - 4];

			DatagramPacket packet = new DatagramPacket(merger, merger.length, group, this.rover.getPort());
			// byte[] bb = new byte[] {this.rover.getRoverID()};
			// DatagramPacket packet = new DatagramPacket(bb, bb.length, group, this.rover.getPort());
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
				System.out.println("[SEND] Sending");
				this.startSend();
				// Send routing table every 5 seconds
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}