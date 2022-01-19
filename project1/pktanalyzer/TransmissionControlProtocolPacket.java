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

	// flag
	private byte flags;

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
		sourcePort = ((data[0] & 0xff) << 8) | data[1] & 0xff;
		
		// parse dest port
		destPort = ((data[2] & 0xff) << 8) | data[3] & 0xff;

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
		flags = data[13];

		// NOTE: Sign bit masking for window size? hex could turn into
		// negative on base 10 i think and that would mess up the window
		// size. Ask prof.
		windowSize = ((data[14] & 0xff) << 8) | data[15] & 0xff;
		checksum = ((data[16] & 0xff) << 8) | data[17] & 0xff;
		urgentPtr = ((data[18] & 0xff) << 8) | data[19] & 0xff;

		if ((dataOffset() >> 2) > 5) {
			options = ((data[20] & 0xff) << 24)
						| ((data[21] & 0xff) << 16)
						| ((data[22] & 0xff) << 8)
						| ((data[23] & 0xff) << 0);
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
		sb.append("TCP: Flags = 0x" + String.format("%02x\n", flags()));

		if (urg()) {
			sb.append("TCP:       ..1. .... = Urgent pointer\n");
		} else {
			sb.append("TCP:       ..0. .... = No Urgent pointer\n");
		}

		if (ack()) {
			sb.append("TCP:       ...1 .... = Acknowledgement\n");
		} else {
			sb.append("TCP:       ...0 .... = No acknowledgement\n");
		}

		if (psh()) {
			sb.append("TCP:       .... 1... = Push\n");
		} else {
			sb.append("TCP:       .... 0... = No push\n");
		}

		if (rst()) {
			sb.append("TCP:       .... .1.. = Reset\n");
		} else {
			sb.append("TCP:       .... .0.. = No reset\n");
		}

		if (syn()) {
			sb.append("TCP:       .... ..1. = Syn\n");
		} else {
			sb.append("TCP:       .... ..0. = No syn\n");
		}

		if (fin()) {
			sb.append("TCP:       .... ...1 = Fin\n");
		} else {
			sb.append("TCP:       .... ...0 = No fin\n");
		}

		sb.append("TCP: Window = " + windowSize() + "\n");
		sb.append("TCP: Checksum = 0x" + String.format("%04x\n", checksum()));
		sb.append("TCP: Urgent pointer = " + urgentPtr() + "\n");

		// TODO: ask professor if parsing options is necessay
		sb.append("TCP: No options\n");

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

	public byte flags() {
		return flags;
	}

	public boolean fin() {
		return (flags & 0b00000001) > 0;
	}

	public boolean syn() {
		return (flags & 0b00000010) > 0;
	}

	public boolean rst() {
		return (flags & 0b00000100) > 0;
	}

	public boolean psh() {
		return (flags & 0b00001000) > 0;
	}

	public boolean ack() {
		return (flags & 0b00010000) > 0;
	}

	public boolean urg() {
		return (flags & 0b00100000) > 0;
	}

	public boolean ece() {
		return (flags & 0b01000000) > 0;
	}

	public boolean cwr() {
		return (flags & 0b10000000) > 0;
	}

	public boolean nsr() {
		return nsr;
	}
}