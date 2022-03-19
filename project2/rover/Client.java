package rover;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Iterator;

class Client implements Runnable {
	private Rover rover;

	public Client(Rover r) {
		this.rover = r;
	}

	@SuppressWarnings("deprecation")
	public void startRecv() throws IOException {
		byte[] buff = new byte[1024];

		MulticastSocket socket = new MulticastSocket(this.rover.getPort());
		InetAddress group = InetAddress.getByName(this.rover.getMulticastIP());

		socket.joinGroup(group);

		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				socket.receive(packet);
				byte[] arr = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());

				byte command = arr[0];
				// byte version = arr[1];				
				// byte[] routingDomain = new byte[] {arr[2], arr[3]};

				if (command == 0b00000001) {
					Sender s = new Sender(rover);
					s.startSend();
				} else if (command == 0b00000010) {
					Table t = new Table(Arrays.copyOfRange(arr, 4, arr.length));

					Iterator<RoutingEntry> it = t.iter();

					while (it.hasNext()) {
						RoutingEntry re = it.next();

						this.rover.updateTable(re, packet.getAddress());
					}	
				}

				

				// byte command = arr[4];

				// // byte version = arr[5];
				// // byte[] routingDomain = new byte[] {arr[6], arr[7]};

				// byte roverId = arr[7];

				// // ignore self send packets on multicast channel
				// if (roverId == this.rover.getRoverID()) {
				// 	continue;
				// }

				// // if its a new guy, send your table immediately
				// if (command == 0b00000001) {
				// 	Sender s = new Sender(this.rover);
				// 	s.startSend();
				// } else if (command == 0b00000010) {
				// 	// else run DV on this guys table.
				// 	byte[] data = Arrays.copyOfRange(arr, 8, len + 4);
				// 	Table t = new Table(roverId, data);

				// 	Iterator<RoutingEntry> it = t.iter();

				// 	while (it.hasNext()) {
				// 		RoutingEntry re = it.next();
				// 		System.out.println(re.toString());
				// 	}

				// 	this.rover.updateTable(t);
				// }
				
				// String msg = Arrays.toString(arr);
				// System.out.println("[RECV] Message:\n" + msg);
				if (Math.random() > 1) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		socket.leaveGroup(group);
		socket.close();
	} 

	@Override
	public void run() {
		try {
			this.startRecv();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}