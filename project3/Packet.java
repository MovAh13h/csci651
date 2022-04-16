import java.util.Arrays;

// MSB
// 0/1
// 0/1
// 0/1
// 0/1
// 0/1
// 0/1 = SEQ/NACK
// 0/1 = DATA/Command
// 0/1 = NO FRAGMENTS/MORE FRAGMENTS
// LSB

public class Packet {
	private int seq;
	private int tl;
	private byte flag;
	private byte[] data;

	public Packet(int seq, int tl, byte flag, byte[] data) {
		this.seq = seq;
		this.tl = tl;
		this.flag = flag;
		this.data = Arrays.copyOf(data, data.length);
	}

	public Packet(byte[] data) throws ArrayIndexOutOfBoundsException {
		this.seq = (data[0] & 0xff) << 24 | (data[1] & 0xff) << 16 | (data[2] & 0xff) << 8 | (data[3] & 0xff);
		this.tl = (data[4] & 0xff) << 24 | (data[5] & 0xff) << 16 | (data[6] & 0xff) << 8 | (data[7] & 0xff);
		this.flag = data[8];
		this.data = Arrays.copyOfRange(data, 9, data.length);
	}

	public int seq() {
		return this.seq;
	}

	public int tl() {
		return this.tl;
	}

	public byte flag() {
		return this.flag;
	}

	public byte[] data() {
		return Arrays.copyOf(this.data, this.data.length);
	}

	public byte[] toByteArray() {
		int len = 9 + this.data.length;
		byte[] res = new byte[len];

		res[0] = (byte)(seq >> 24);
		res[1] = (byte)(seq >> 16);
		res[2] = (byte)(seq >> 8);
		res[3] = (byte)(seq >> 0);
		
		res[4] = (byte)(tl >> 24);
		res[5] = (byte)(tl >> 16);
		res[6] = (byte)(tl >> 8);
		res[7] = (byte)(tl >> 0);
		res[8] = flag;

		for (int i = 0; i < data.length; ++i) {
			res[i + 9] = data[i];
		}

		return res;
	}
}