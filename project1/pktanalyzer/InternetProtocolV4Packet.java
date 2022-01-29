/*
 * InternetProtocolV4Packet.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */

package pktanalyzer;

import java.util.Arrays;

/**
 * This classes a IPv4 packet from the bytes provided.
 *
 * Reference: https://en.wikipedia.org/wiki/IPv4
 *
 * @author Tanishq Jain <tj3989@cs.rit.edu>
 */

public class InternetProtocolV4Packet {
	// version
	private int version;

	// length
	private int ihl;

	// TOS->dscp
	private int dscp;

	// TOS->ecn
	private int ecn;

	// total length
	private int totalLength;

	// identification
	private int identification;

	// flags
	private int flags;

	// flag offset
	private int flagOffset;

	// ttl
	private int ttl;

	// protocol
	private int protocol;

	// protocol label
	private String protocolLabel;

	// header checksum
	private int headerChecksum;

	// src ip
	private String srcIp = "";

	// dest ip
	private String destIp = "";

	// options
	private byte[] options;

	// payload
	private byte[] payload;

	InternetProtocolV4Packet(byte[] data) throws Exception {
		version = (data[0] & 0xff) >> 4; // ok
		ihl     = (data[0] & 0xff & 0b00001111) << 2;

		dscp = (data[1] & 0xff) >> 2; // ok
		ecn  = data[1] & 0xff & 0b00000011; // ok

		totalLength    = (data[2] & 0xff) << 8 | data[3] & 0xff; // ok
		identification = (data[4] & 0xff) << 8 | data[5] & 0xff; // ok
		
		flags          = (data[6] & 0xff & 0b01100000) >> 5;
		flagOffset     = (data[6] & 0xff & 0b00011111) << 5 | data[7] & 0xff;
		
		ttl      = data[8] & 0xff;
		protocol = data[9] & 0xff;

		handleProtocolLabel(protocol);

		headerChecksum = (data[10] & 0xff) << 8 | data[11] & 0xff;

		// parse src ip
		srcIp += (data[12] & 0xff) + ".";
		srcIp += (data[13] & 0xff) + ".";
		srcIp += (data[14] & 0xff) + ".";
		srcIp += data[15] & 0xff;

		// parse dest ip
		destIp += (data[16] & 0xff) + ".";
		destIp += (data[17] & 0xff) + ".";
		destIp += (data[18] & 0xff) + ".";
		destIp += data[19] & 0xff;

		// 0 - 15 ie 0-F
		if ((ihl >> 2) > 5 && (ihl >> 2) < 16) {
			// options are present
			options = Arrays.copyOfRange(data, 20, ihl);
			payload = Arrays.copyOfRange(data, ihl, totalLength());
		} else {
			// options are not present
			payload = Arrays.copyOfRange(data, 20, totalLength());
		}
	}

	public byte[] payload() {
		if (payload != null) {
			return Arrays.copyOfRange(payload, 0, payload.length);
		} else {
			return null;
		}
	}

	public byte[] options() {
		if (options != null) {
			return Arrays.copyOfRange(options, 0, options.length);
		} else {
			return null;
		}
	}

	public int flags() {
		return flags;
	}
	public int flagOffset() {
		return flagOffset;
	}

	public int headerChecksum() {
		return headerChecksum;
	}

	public int totalLength() {
		return totalLength;
	}

	public int ecn() {
		return ecn;
	}

	public int dscp() {
		return dscp;
	}

	public int ihl() {
		return ihl;
	}

	public int version() {
		return version;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("IP: ----- IP Header -----\n");
		sb.append("IP:                      \n");
		sb.append("IP: Version = " + version() + "\n");
		sb.append("IP: Header length = " + ihl() + " bytes\n");
		
		sb.append("IP: Differentiated Services Code Point: 0x"
			+ String.format("%02x\n", dscp()));
		sb.append("IP: Explicit Congestion Notification = 0b"
			+ String.format("%2s\n", Integer.toBinaryString(ecn()))
			.replace(" ", "0"));

		if ((ecn() & 0b11) == 0b00) {
			sb.append("IP:       0b00 = Non ECN-Capable Transport\n");
		} else if ((ecn() & 0b11) == 0b10) {
			sb.append("IP:       0b10 = ECN Capable Transport, ECT(0)\n");
		} else if ((ecn() & 0b11) == 0b01) {
			sb.append("IP:       0b01 = ECN Capable Transport, ECT(1)\n");
		} else if ((ecn() & 0b11) == 0b11) {
			sb.append("IP:       0b11 = Congestion Encountered, CE\n");
		}

		sb.append("IP: Total length = " + totalLength() + " bytes\n");
		sb.append("IP: Identification = " + identification() + "\n");
		sb.append("IP: Flags = 0x" + String.format("%02x\n", flags()));

		if ((flags() & 0b10) == 0b10) {
			sb.append("IP:       .1.. .... = do not fragment\n");
		} else {
			sb.append("IP:       .0.. .... = OK to fragment\n");
		}

		if ((flags() & 0b1) == 0b0) {
			sb.append("IP:       ..0. .... = last fragment\n");
		} else {
			sb.append("IP:       ..1. .... = more fragment\n");
		}

		sb.append("IP: Fragment offset = " + flagOffset() + " bytes\n");
		sb.append("IP: Time to live = " + ttl() + " seconds/hops\n");
		sb.append("IP: Protocol = " + protocol() + " ("
			+ protocolLabel() + ")\n");
		sb.append("IP: Header checksum = 0x"
			+ String.format("%04x\n", headerChecksum()));
		sb.append("IP: Source address = " + sourceIP() + "\n");
		sb.append("IP: Destination address = " + destIP() + "\n");

		if ((ihl() >> 2) <= 5) {
			sb.append("IP: No options\n");
		} else {
			sb.append("IP: Options present\n");
		}

		sb.append("IP:\n");

		return sb.toString();
	}

	public String sourceIP() {
		return srcIp;
	}

	public String destIP() {
		return destIp;
	}

	public int identification() {
		return identification;
	}

	public int ttl() {
		return ttl;
	}

	public int protocol() {
		return protocol;
	}

	public String protocolLabel() {
		return protocolLabel;
	}

	private void handleProtocolLabel(int protocol) {
		switch (protocol) {
			case 17:
				// User Datagram Protocol
				protocolLabel = "UDP";
				break;

			case 1:
				// Internet Control Message Protocol
				protocolLabel = "ICMP";
				break;

			case 2:
				// Internet Group Management Protocol	
				protocolLabel = "IGMP";
				break;

			case 6:
				// Transmission Control Protocol	
				protocolLabel = "TCP";
				break;

			case 41:
				// IPv6 encapsulation
				protocolLabel = "ENCAP";
				break;

			case 89:
				// Open Shortest Path First
				protocolLabel = "OSPF";
				break;

			case 132:
				// Stream Control Transmission Protocol
				protocolLabel = "SCTP";
				break;

			default:
				protocolLabel = "UNKNOWN";
		}
	}
}