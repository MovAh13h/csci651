package pktanalyzer;

import java.util.Arrays;

// https://en.wikipedia.org/wiki/Transmission_Control_Protocol
public class TransmissionControlProtocolPacket {
	// source port
	private Integer sourcePort;

	// destination port
	private Integer destPort;

	// sequence number
	private Long sequenceNo;

	// ack number
	private Long ackNo;

	// data offset
	private Integer dataOffset;

	// ECN-nonce - concealment protection
	private boolean nsr = false;
	
	// Congestion window reduced
	private boolean cwr = false;

	// ECN-Echo
	private boolean ece = false;

	// Urgent pointer field is significant
	private boolean urg = false;

	// Indicates that the Acknowledgment field is significant
	private boolean ack = false;

	// Push function
	private boolean psh = false;
	
	// Reset the connection
	private boolean rst = false;
	
	// Synchronize sequence numbers
	private boolean syn = false;

	// Last packet from sender
	private boolean fin = false;

	// window size
	private Integer windowSize;

	// checksum
	private Integer checksum;

	// urgent pointer
	private Integer urgentPtr;

	// options (optional)
	private Integer options;

	// payload
	private byte[] payload;

	// hexdump
	private HexDump hd;

	TransmissionControlProtocolPacket(byte[] data) throws Exception {
		// parse source port
		sourcePort = ((data[0] & 0xff) << 8) + (data[1] & 0xff);
		
		// parse dest port
		destPort = ((data[2] & 0xff) << 8) + (data[3] & 0xff);

		// parse sequence number
		sequenceNo = (long) (data[4] & 0xff) << 24
						| (data[5] & 0xff) << 16
						| (data[6] & 0xff) << 8
						| (data[7] & 0xff) << 0;

		ackNo = (long) (data[8] & 0xff) << 24
					| (data[9] & 0xff) << 16
					| (data[10] & 0xff) << 8
					| (data[11] & 0xff) << 0;

		dataOffset = (data[12] & 0xff) >> 4;

		nsr = (data[12] & 0b00000001) > 0;
		cwr = (data[13] & 0b10000000) > 0;
		ece = (data[13] & 0b01000000) > 0;
		urg = (data[13] & 0b00100000) > 0;
		ack = (data[13] & 0b00010000) > 0;
		psh = (data[13] & 0b00001000) > 0;
		rst = (data[13] & 0b00000100) > 0;
		syn = (data[13] & 0b00000010) > 0;
		fin = (data[13] & 0b00000001) > 0;

		// NOTE: Sign bit masking for window size? hex could turn into
		// negative on base 10 i think and that would mess up the window
		// size. Ask prof.
		windowSize = ((data[14] & 0xff) << 8) + (data[15] & 0xff);
		checksum = ((data[16] & 0xff) << 8) + (data[17] & 0xff);
		urgentPtr = ((data[18] & 0xff) << 8) + (data[19] & 0xff);

		if ((dataOffset() >> 2) > 5) {
			options = ((data[20] & 0xff) << 24)
						+ ((data[21] & 0xff) << 16)
						+ ((data[22] & 0xff) << 8)
						+ ((data[23] & 0xff) << 0);
			payload = Arrays.copyOfRange(data, 24, data.length);
		} else {
			payload = Arrays.copyOfRange(data, 20, data.length);
		}

		hd = new HexDump(data);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("TCP: ----- TCP Header -----\n");
		sb.append("TCP:                       \n");
		sb.append("TCP: Source port = " + sourcePort() + "\n");
		sb.append("TCP: Destination port = " + destPort() + "\n");
		sb.append("TCP: Sequence number = " + sequenceNo() + "\n");
		sb.append("TCP: Acknowledgement number = " + ackNo() + "\n");
		sb.append("TCP: Data offset = " + dataOffset() + " bytes\n");
		sb.append("TCP: Flags = ");

		sb.append("TCP:                       \n");
		sb.append("TCP: Data: (first 64 bytes)\n");

		String[] hexdump = hd.hexdump();
		for (int i = 0; i < Math.min(hexdump.length, 4); i++) {
			sb.append("TCP: " + hexdump[i] + "\n");
		}

		return sb.toString();
	}

	public byte[] payload() {
		return Arrays.copyOfRange(payload, 0, payload.length);
	}

	public int options() {
		return options;
	}

	public int urgentPtr() {
		return urgentPtr;
	}

	public int checksum() {
		return checksum;
	}

	public int windowSize() {
		return windowSize;
	}

	public int sourcePort() {
		return sourcePort;
	}

	public int destPort() {
		return destPort;
	}

	public long sequenceNo() {
		return sequenceNo;
	}

	public long ackNo() {
		return ackNo;
	}

	public int dataOffset() {
		return dataOffset;
	}

	public boolean fin() {
		return fin;
	}

	public boolean syn() {
		return syn;
	}

	public boolean rst() {
		return rst;
	}

	public boolean psh() {
		return psh;
	}

	public boolean ack() {
		return ack;
	}

	public boolean urg() {
		return urg;
	}

	public boolean ece() {
		return ece;
	}

	public boolean cwr() {
		return cwr;
	}

	public boolean nsr() {
		return nsr;
	}
}