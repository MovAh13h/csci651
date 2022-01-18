package pktanalyzer;

public class UserDatagramProtocolPacket {
	// source port
	private int sourcePort;

	// destination port
	private int destPort;

	// length
	private int length;

	// checksum
	private int checksum;

	// hexdump
	private HexDump hd;

	UserDatagramProtocolPacket(byte[] data) throws Exception {
		// parse source port
		sourcePort = ((data[0] & 0xff) << 8) | data[1] & 0xff;
		
		// parse dest port
		destPort = ((data[2] & 0xff) << 8) | data[3] & 0xff;

		// parse length of udp packet
		length = ((data[4] & 0xff) << 8) | data[5] & 0xff;

		// parse checksum
		checksum = ((data[6] & 0xff) << 8) | data[7] & 0xff;

		// get the hex dump of this packet
		hd = new HexDump(data);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("UDP: ----- UDP Header -----\n");
		sb.append("UDP:                       \n");
		sb.append("UDP: Source port = " + sourcePort() + "\n");
		sb.append("UDP: Destination port = " + destPort() + "\n");
		sb.append("UDP: Length = " + length() + "\n");
		sb.append("UDP: Checksum = " + String.format("0x%04x\n", checksum()));
		sb.append("UDP:                       \n");
		sb.append("UDP: Data: (first 64 bytes)\n");

		String[] hexdump = hd.hexdump();

		for (int i = 0; i < Math.min(hexdump.length, 4); i++) {
			sb.append("UDP: " + hexdump[i] + "\n");
		}

		return sb.toString();
	}

	public int checksum() {
		return checksum;
	}

	public int sourcePort() {
		return sourcePort;
	}

	public int destPort() {
		return destPort;
	}

	public int length() {
		return length;
	}
}