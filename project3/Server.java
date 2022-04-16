import java.nio.file.*;
import java.net.*;
import java.util.*;

public class Server {
	static int port = 63000;
	static byte[][] data;

	static int mtu = 1200;
	
	static byte ACK = 0b00000100;
	static byte SEQ = 0b00000000;
	static byte DATA = SEQ;
	static byte COMMAND = 0b00000010;
	static byte NOFRAGS = SEQ;
	static byte MOREFRAGS = 0b00000001;
	static byte NONACK = SEQ;
	static byte NACK = 0b00001000;

	public static byte[][] chunkData(byte[] data, int chunk) {
	    int rest = data.length % chunk;
		int chunks = data.length / chunk + (rest > 0 ? 1 : 0);
		byte[][] arrays = new byte[chunks][];

		for(int i = 0; i < data.length; i += chunk){
			Arrays.copyOfRange(data, i, Math.min(data.length, i + chunk));
		}

		for(int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++){
			arrays[i] = Arrays.copyOfRange(data, i * chunk, i * chunk + chunk);
		}

		if (rest > 0) {
			arrays[chunks - 1] = Arrays.copyOfRange(data, (chunks - 1) * chunk, (chunks - 1) * chunk + rest);
		}

		return arrays;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage: java Server [port:int] [img:string]");
			System.exit(1);
		}

		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {}

		data = chunkData(Files.readAllBytes(Paths.get(args[1])), mtu);

		startServer();
	}

	public static void startServer() throws Exception {
		DatagramSocket ss = new DatagramSocket(port);

		System.out.println("* Server listening on port " + port);

		while (true) {
			DatagramPacket dp_recv = new DatagramPacket(new byte[40], 40);
			ss.receive(dp_recv);

			SocketAddress remoteSockAddr = dp_recv.getSocketAddress();
			
			try {
				byte[] bdata = Arrays.copyOfRange(dp_recv.getData(), dp_recv.getOffset(), dp_recv.getLength());
				Packet p = new Packet(bdata);

				String msg = new String(p.data());
				if (msg.equals("shutdown")) { break; }

				String[] split = msg.split(" ");
				String cmd = split[0];

				if ((p.flag() & NACK) == NACK) {
					try {
						int seq = p.seq();
						byte[] chunk = Arrays.copyOf(data[seq], data[seq].length);
						Packet nresp = new Packet(seq, data.length, (byte)(ACK | NOFRAGS), chunk);
						byte[] nbresp = nresp.toByteArray();

						DatagramPacket dpnack = new DatagramPacket(nbresp, nbresp.length, remoteSockAddr);
						ss.send(dpnack);
					} catch (Exception e) {}
					continue;
				}

				if (cmd.equals("move_left")
					|| cmd.equals("move_right")
					|| cmd.equals("move_forward")
					|| cmd.equals("move_back")) {
					System.out.println("Sending Command ACK: " + remoteSockAddr.toString());
					Packet presp = new Packet(0, 0, (byte)(ACK | NOFRAGS), new byte[0]);
					byte[] bpresp = presp.toByteArray();
					DatagramPacket dpresp = new DatagramPacket(bpresp, bpresp.length, remoteSockAddr);
					ss.send(dpresp);
				} else if (cmd.equals("camera")) {
					// mtu is set to the 13 bytes pf header are already taken
					// into account. 8 from UDP header and 5 from custom header
					
					System.out.println("Sending " + data.length + " chunks to " + remoteSockAddr.toString());

					for (int i = 0; i < data.length; i++) {
						byte flag = DATA;

						if (i == data.length - 1) {
							flag |= NOFRAGS;
						} else {
							flag |= MOREFRAGS;
						}

						if (i % 1237 == 0) {
							continue;
						}

						byte[] bchunk = Arrays.copyOf(data[i], data[i].length);
						Packet pchunk = new Packet(i, data.length, flag, bchunk);
						byte[] bpchunk = pchunk.toByteArray();

						DatagramPacket dpchunk = new DatagramPacket(bpchunk, bpchunk.length, remoteSockAddr);
						ss.send(dpchunk);

						// this is to simulate some form of computation since I have
						// already chunked up and kept my data ready. Can be changed
						// or removed as per need
						Thread.sleep(1);
					}
					System.out.println("Sent " + data.length + " camera chunks to " + remoteSockAddr.toString());
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// drop packet if not enough data
				continue;
			}
		}

		ss.close();
	}
}