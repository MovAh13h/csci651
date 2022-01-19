/*
 * InternetControlMessageProtocolPacket.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */

package pktanalyzer;

/**
 * The classes parses a ICMP packet from the bytes provided.
 *
 * @author Tanishq Jain <tj3989@cs.rit.edu>
 */

public class InternetControlMessageProtocolPacket {
	// type
	private int type;

	// code
	private int code;

	// checksum
	private int checksum;

	InternetControlMessageProtocolPacket(byte[] data) throws Exception {		
		type = data[0] & 0xff;
		code = data[1] & 0xff;
		checksum = (data[2] & 0xff) << 8 | data[3] & 0xff;	
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("ICMP: ----- ICMP Header -----\n");
		sb.append("ICMP:                        \n");
		sb.append("ICMP: Type = " + type() + " (" + getType(type()) + ")\n");
		sb.append("ICMP: Code = " + code() + "\n");
		sb.append("ICMP: Checksum = 0x" + String.format("%02x\n", checksum()));
		sb.append("ICMP:                        \n");

		return sb.toString();
	}

	public int type() {
		return type;
	}

	public int code() {
		return code;
	}

	public int checksum() {
		return checksum;
	}

	static public String getType(int type) {
		switch (type) {
			case 0:
				return "Echo reply";
			
			case 3:
				return "Destination unreachable";
			
			case 4:
				return "Source quench";

			case 5:
				return "Redirect message";

			case 8:
				return "Echo request";

			case 9:
				return "Router advertisement";

			case 10:
				return "Router solicitation";

			case 11:
				return "Time exceeded";

			case 12:
				return "Bad IP header";

			case 13:
				return "Timestamp";

			case 14:
				return "Timestamp reply";

			case 15:
				return "Information request";

			case 16:
				return "Information reply";

			case 17:
				return "Address Mask request";

			case 18:
				return "Address Mask reply";

			case 30:
				return "Traceroute";

			case 42:
				return "Extended echo request";

			case 43:
				return "Extended echo reply";

			default:
				return "No description";
		}
	}
}