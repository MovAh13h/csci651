package rover;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rover.RoutingEntry;

class Entry {
	private byte[] addr;
	private byte[] routeTag;
	private byte[] ip;
	private byte[] subnet;
	private byte[] nextHop;
	private byte[] metric;

	public Entry(byte[] data) throws ArrayIndexOutOfBoundsException {
		addr = new byte[] {data[0], data[1]};
		routeTag = new byte[] {data[2], data[3]};
		ip = new byte[] {data[4], data[5], data[6], data[7]};
		subnet = new byte[] {data[8], data[9], data[10], data[11]};
		nextHop = new byte[] {data[12], data[13], data[14], data[15]};
		metric = new byte[] {data[16], data[17], data[18], data[19]};
 	}

 	public byte[] getBytes() {
 		byte[] b = new byte[20];

 		b[0] = addr[0];
 		b[1] = addr[1];

 		b[2] = routeTag[0];
 		b[3] = routeTag[1];

 		for (int i = 0; i < 4; i++) {
 			b[i + 4] = ip[i];
			b[i + 8] = subnet[i];
			b[i + 12] = nextHop[i];
			b[i + 16] = metric[i];
 		}

 		return b;
 	}

 	public String getIP() {
 		return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
 	}
}

public class Packet {
	byte command;
	byte version;
	byte[] routingDomain;
	HashMap<String, Entry> entries;

	public Packet(byte[] data) throws ArrayIndexOutOfBoundsException {
		this.command = data[0];
		this.version = data[1];
		this.routingDomain = new byte[] {data[2], data[3]};
		this.entries = new HashMap<>();

		for (int i = 0; i < 25; i++) {
			try {
				int startIndex = i * 20 + 4; // 20 bytes each + skip first 4
				Entry e = new Entry(Arrays.copyOfRange(data, startIndex, data.length));
				this.entries.put(e.getIP(), e);
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
	}

	public Packet(RoutingTable t, byte command) {
		this.command = command;
		this.version = (byte) 2;
		this.routingDomain = new byte[] {0, 0};
		this.entries = new HashMap<>();

		for (Map.Entry<InetAddress, RoutingEntry> e : t.t.entrySet()) {
			RoutingEntry re = (RoutingEntry) e.getValue();

			try {
				Entry e = new Entry(re.toRIPEntry());
				this.entries.put(e.getIP(), e);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public byte[] getBytes() {
		return new byte[] {};
	}
}