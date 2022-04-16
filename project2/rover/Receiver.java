package rover;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class Receiver extends Thread {
	private Rover r;

	public Receiver(Rover r) {
		this.r = r;
	}

	@SuppressWarnings("deprecation")
	public void receive() throws Exception {
		byte[] buff = new byte[1024];

		MulticastSocket socket = new MulticastSocket(this.r.port);
		InetAddress g = InetAddress.getByName(this.r.multicastIP);

		socket.joinGroup(g);

		while (true) {
			try {
				DatagramPacket p = new DatagramPacket(buff, buff.length);
				socket.receive(p);
				byte[] arr = Arrays.copyOfRange(p.getData(), p.getOffset(), p.getLength());
				byte command = arr[0];

				if (command == (byte)1) {
					// triggereed update
					Sender s = new Sender(this.r);
					s.send();
				} else if (command == (byte) 2) {
					RoutingTable t = new RoutingTable(Arrays.copyOfRange(arr, 4, arr.length));

					boolean print = this.r.table.updateTable(t, p.getAddress());
					print = print || this.r.table.cleanUpTable();

					if (print) {
						System.out.println(this.r.table);
					}
				}

				// to stop unreachable code error
				if (Math.random() > 1) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		socket.leaveGroup(g);
		socket.close();
	}

	@Override
	public void run() {
		try {
			this.receive();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}