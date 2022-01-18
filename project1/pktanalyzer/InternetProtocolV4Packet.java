package pktanalyzer;

import java.util.Arrays;

// https://en.wikipedia.org/wiki/IPv4
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
		// first half of the byte is the version
		// second half of the byte is length; & it with 0b00001111 to get the 
		// last 4 bits (LSB)
		// TODO: Is sign bit masking necessary here? imo no
		version = data[0] >> 4; // ok

		// TODO: is this correct? why does this work? dont understand the math
		// behind this. Ask professor
		ihl     = (data[0] & 0xff & 0b00001111) << 2; // why

		dscp = data[1] >> 2; // ok
		ecn  = data[1] & 0b00000011; // ok

		// total length is 2 bytes. so first byte has to come 8 towards left
		// + its 8 bits resulting in a 16 bits. we mask the sign bit to remove
		// +/-
		totalLength = ((data[2] & 0xff) << 8) | (data[3] & 0xff); // ok

		identification = ((data[4] & 0xff) << 8) | (data[5] & 0xff); // ok
		
		// if flag is first 3 MSBs then it should be right shifted 5 times
		// to bring those bits to LSBs but >> 5 gets me incorrect answer
		// and >> 4 gets me the correct answer. Why?
		flags      = (data[6] & 0xff & 0b11100000); // not ok

		// ok
		flagOffset = ((data[6] & 0xff & 0b00011111) << 5) | data[7] & 0xff;

		// Note: masking needed here?
		ttl = data[8] & 0xff;
		protocol = data[9] & 0xff;

		handleProtocolLabel(protocol);

		headerChecksum = ((data[10] & 0xff) << 8) | data[11] & 0xff;

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

		if (ihl > 5 && ihl < 16) {
			// options are present
			options = Arrays.copyOfRange(data, 19, ihl);
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
		sb.append("IP: Type of service = 0x" + String.format("%02x", dscp())
			+ "\n");
		sb.append("IP:       xxx. .... = 0 (precedence)\n");
		sb.append("IP:       ...0 .... = normal delay\n");
		sb.append("IP:       .... 0... = normal throughput\n");
		sb.append("IP:       .... .0.. = normal reliability\n");
		sb.append("IP: Total length = " + totalLength() + " bytes\n");
		sb.append("IP: Identification = " + identification() + "\n");
		sb.append("IP: Flags = 0x" + (flags() >> 4) + "\n");
		
		if ((flags() & 0b01000000) == 0b01000000) {
			sb.append("IP:       .1.. .... = do not fragment\n");
		} else {
			sb.append("IP:       .0.. .... = OK to fragment\n");
		}

		if ((flags() & 0b00100000) == 0b00000000) {
			sb.append("IP:       ..0. .... = last fragment\n");
		} else {
			sb.append("IP:       ..1. .... = more fragment\n");
		}

		// TODO: this
		sb.append("IP: Fragment offset = " + " bytes\n");
		sb.append("IP: Time to live = " + ttl() + " seconds/hops\n");
		sb.append("IP: Protocol = " + protocol() + " ("
			+ protocolLabel() + ")\n");
		sb.append("IP: Header checksum = 0x"
			+ Integer.toHexString(headerChecksum()) + "\n");
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