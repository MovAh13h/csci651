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
					// triggered updates
					Sender s = new Sender(rover);
					s.startSend();
				} else if (command == 0b00000010) {
					Table t = new Table(Arrays.copyOfRange(arr, 4, arr.length));

					synchronized (t) {
						Iterator<RoutingEntry> it = t.iter();

						while (it.hasNext()) {
							RoutingEntry re = it.next();

							if (!re.getDestination().equals(this.rover.getNetworkIp())) {
								this.rover.updateTable(re, packet.getAddress());
							}
						}	
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
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