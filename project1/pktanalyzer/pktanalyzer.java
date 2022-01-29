/*
 * pktanalyzer.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */

package pktanalyzer;

import java.io.File;
import java.nio.file.Files;

/**
 * Entry point of the program.
 * Parses command-line args, performs error checks and runs apt. packet parsers
 *
 * @author Tanishq Jain <tj3989@cs.rit.edu>
 */

public class pktanalyzer {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage:");
			System.err.println("      java pktanalyzer ./path_to_packet.bin");
			System.exit(1);
		}

		// get file handler
		File packet_file = new File(args[0]);

		if (!packet_file.exists()) {
			System.err.println("Could not find the specified file.");
			System.err.println("Please enter a valid file path.");
			System.exit(1);
		}

		try {
			// read file as bytes
			byte[] packet_data = Files.readAllBytes(packet_file.toPath());
			 
			// run the ethernet packet parser
			EthernetPacket epp = new EthernetPacket(packet_data);

			// pretty print the packet
			System.out.print(epp);

			// check if its an IP
			if (epp.ethertypeLabel() == "IP") {
				// run ipv4 packet parser
				InternetProtocolV4Packet ippp
					= new InternetProtocolV4Packet(epp.payload());

				// pretty print
				System.out.print(ippp);

				// get the payload of the ipv4 packet
				byte[] ipv4Payload = ippp.payload();

				// check if UDP
				if (ippp.protocolLabel() == "UDP") {
					// run udp parser
					UserDatagramProtocolPacket udpp = new UserDatagramProtocolPacket(ipv4Payload);

					// pretty print
					System.out.println(udpp);
				} else if (ippp.protocolLabel() == "TCP") {
					// run tcp packet parser
					TransmissionControlProtocolPacket tcpp = new
						TransmissionControlProtocolPacket(ipv4Payload);

					// pretty print
					System.out.println(tcpp);
				} else if (ippp.protocolLabel() == "ICMP") {
					// run icmp packet parser
					InternetControlMessageProtocolPacket icmpp = new
					InternetControlMessageProtocolPacket(ipv4Payload);

					// pretty print
					System.out.println(icmpp);
				} else {
					// unhandled packet
					System.out.println("*** Unhandled Packet type inside IPv4 ***");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}