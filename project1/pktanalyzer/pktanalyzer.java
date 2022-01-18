package pktanalyzer;

import java.io.*;
import java.nio.file.Files;

public class pktanalyzer {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage:");
			System.err.println("      java pktanalyzer ./path_to_packet.bin");
			System.exit(1);
		}

		File packet_file = new File(args[0]);

		if (!packet_file.exists()) {
			System.err.println("Could not find the specified file.");
			System.err.println("Please enter a valid file path.");
			System.exit(1);
		}

		try {
			byte[] packet_data = Files.readAllBytes(packet_file.toPath());
			 
			EthernetPacket epp = new EthernetPacket(packet_data);

			System.out.print(epp);

			// check if its an IP
			if (epp.ethertypeLabel() == "IP") { 
				InternetProtocolV4Packet ippp
					= new InternetProtocolV4Packet(epp.payload());

				System.out.print(ippp);

				byte[] ipv4Payload = ippp.payload();

				// check if UDP
				if (ippp.protocolLabel() == "UDP") {
					UserDatagramProtocolPacket udpp = new UserDatagramProtocolPacket(ipv4Payload);

					System.out.println(udpp);
				} else if (ippp.protocolLabel() == "TCP") {
					// check if TCP
					TransmissionControlProtocolPacket tcpp = new
						TransmissionControlProtocolPacket(ipv4Payload);

					System.out.println(tcpp);
				} else if (ippp.protocolLabel() == "ICMP") {
					// if ICMP packet
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}