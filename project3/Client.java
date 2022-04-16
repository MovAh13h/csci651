import java.io.*;
import java.net.*;
import java.util.*;
import java.time.*;

public class Client {
	static String outputFileName;
	static byte[] buffer = new byte[2000];
	static File f;

	static byte NACK = 0b00001000;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("java Client [output_file_name:string]");
			System.exit(1);
		}

		outputFileName = args[0];

		try {
			DatagramSocket sss = new DatagramSocket();
			Scanner in = new Scanner(System.in);
			// should probably be more since we are dealing with space but
			// whatever
			sss.setSoTimeout(10000);

			while (true) {
				System.out.print("\n> ");
				String commands = in.nextLine();

				if (commands.equals("exit")) {
					break;
				}

				String[] split = commands.split(" ");

				InetAddress dest = InetAddress.getByName(split[0]);
				Integer dest_port = Integer.parseInt(split[1]);

				String cmd = split[2];
				
				// small commands
				if (cmd.equals("move_left")
					|| cmd.equals("move_right")
					|| cmd.equals("move_forward")
					|| cmd.equals("move_back")) {
						byte[] cmdb = cmd.getBytes();
						Packet p = new Packet(0, 0, (byte)2, cmdb);
						byte[] pb = p.toByteArray();
						DatagramPacket dp = new DatagramPacket(pb, pb.length, dest, dest_port);
						DatagramPacket drecv = new DatagramPacket(new byte[10], 10);
						
						Instant f = Instant.now();
						boolean first = true;
						while (true) {
							Instant s = Instant.now();

							Duration d = Duration.between(f, s);

							if (first) {
								first = false;
								try {
									System.out.println("Transmitting Command...");
									sss.send(dp);	
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (d.getSeconds() > 10) {
								try {
									System.out.println("Retransmitting Command...");
									sss.send(dp);	
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							try {
								sss.receive(drecv);
							} catch (IOException e) {
								e.printStackTrace();
								continue;
							}

							// ideally we would compute the checksum
							// here and use or discard packet
							// accordingly but more explanation
							// on this in paper submitted
							break;
						}

						System.out.println("ACK Received!");
				} else if (cmd.equals("camera")) {
					byte[] cmdb = cmd.getBytes();
					Packet p = new Packet(0, 0, (byte)2, cmdb);
					byte[] pb = p.toByteArray();
					DatagramPacket dp = new DatagramPacket(pb, pb.length, dest, dest_port);

					Instant f = Instant.now();
					boolean first = true;

					while (true) {
						Instant s = Instant.now();

						Duration d = Duration.between(f, s);

						if (first) {
							first = false;
							try {
								System.out.println("Transmitting Command...");
								sss.send(dp);	
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (d.getSeconds() > 10) {
							try {
								System.out.println("Retransmitting Command...");
								sss.send(dp);	
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						
						try {
							byte[] dpbuffer = new byte[1209];
							DatagramPacket drecv = new DatagramPacket(dpbuffer, dpbuffer.length);
							
							sss.receive(drecv);

							byte[] bdrecv = Arrays.copyOfRange(drecv.getData(), drecv.getOffset(), drecv.getLength());
							Packet pdrecv = new Packet(bdrecv);

							int tl = 0;

							if (pdrecv.tl() > 1) {
								tl = (pdrecv.tl() - 1);
							} else {
								tl = 0;
							}

							HashMap<Integer, byte[]> buffer = new HashMap<>();

							buffer.put(pdrecv.seq(), pdrecv.data());

							while (tl != 0) {
								DatagramPacket cpck = new DatagramPacket(dpbuffer, dpbuffer.length);
								try {
									sss.receive(cpck);
									
									tl = (tl - 1);

									byte[] bcpck = Arrays.copyOfRange(drecv.getData(), drecv.getOffset(), drecv.getLength());
									Packet pcpck = new Packet(bcpck);

									buffer.put(pcpck.seq(), pcpck.data());
								} catch (SocketTimeoutException ste) {						
									int n = findFirstNack(buffer, pdrecv.tl());
									System.out.println("Sending NACK for chunk number: " + n);

									if (n == -1) { break; }

									Packet nackp = new Packet(n, 0, (byte)(NACK), new byte[0]);
									byte[] bnackp = nackp.toByteArray();

									DatagramPacket dpnack = new DatagramPacket(bnackp, bnackp.length, dest, dest_port);

									sss.send(dpnack);
								}
							}
							
							pushToApplicationLayer(buffer);
						} catch (IOException e) {
							e.printStackTrace();
							continue;
						}

						// ideally we would compute the checksum
						// here and use or discard packet
						// accordingly but more explanation
						// on this in paper submitted
						break;
					}
				}
			}

			in.close();
			sss.close();	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static int findFirstNack(HashMap<Integer, byte[]> hm, int tl) {
		for (int i = 0; i < tl; i++) {
			if (!hm.containsKey(i)) {
				return i;
			}
		}

		return -1;
	}

	public static void pushToApplicationLayer(HashMap<Integer, byte[]> hm) {
		System.out.println("Application layer Chunks:" + hm.size());
		try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
			for (int i = 0; i < hm.size(); i++) {
				fos.write(hm.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}