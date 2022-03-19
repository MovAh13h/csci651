package rip;

import java.util.*;

import rover.RoutingEntry;
import rover.Table;

// Ref: https://en.wikibooks.org/wiki/Routing_protocols_and_architectures/Routing_Information_Protocol
class Entry {
	private byte[] addrFamIdnt;
	private byte[] routeTag;
	private byte[] ip;
	private byte[] subnet;
	private byte[] nextHop;
	private byte[] metric;

	public Entry(byte[] data) throws ArrayIndexOutOfBoundsException {
		addrFamIdnt = new byte[] {data[0], data[1]};
		routeTag = new byte[] {data[2], data[3]};
		ip = new byte[] {data[4], data[5], data[6], data[7]};
		subnet = new byte[] {data[8], data[9], data[10], data[11]};
		nextHop = new byte[] {data[12], data[13], data[14], data[15]};
		metric = new byte[] {data[16], data[17], data[18], data[19]};
 	}

 	public byte[] getBytes() {
 		byte[] b = new byte[20];

 		b[0] = addrFamIdnt[0];
 		b[1] = addrFamIdnt[1];

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

 	public String toString() {
 		StringBuilder sb = new StringBuilder();
 		// TODO: if needed
 		return sb.toString();
 	}
}

public class Packet {
	private byte command;
	private byte version;
	private byte[] routingDomain;
	private HashMap<String, Entry> entries;

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

	public Packet(Table t, byte command) {
		this.command = command; // 2
		this.version = 0b00000010; // 2
		this.routingDomain = new byte[] {0, 0};
		this.entries = new HashMap<>();

		Iterator<RoutingEntry> it = t.iter();

		while (it.hasNext()) {
			RoutingEntry re = it.next();
			try {
				Entry e = new Entry(re.toRIPEntry());
				this.entries.put(e.getIP(), e);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public byte getCommand() {
		return this.command;
	}

	public byte getVersion() {
		return this.version;
	}

	public byte[] getRoutingDomain() {
		return Arrays.copyOf(this.routingDomain, this.routingDomain.length);
	}

	public byte[] getBytes() {
		int i = 0;
		byte[] b = new byte[(entries.size() * 20) + 4];

		b[i++] = command;
		b[i++] = version;
		b[i++] = routingDomain[0];
		b[i++] = routingDomain[1];

		// https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
		for (Entry e : entries.values()) {
			byte[] bb = e.getBytes();

			for (int j = 0; j < 20; j++) {
				b[i++] = bb[j];
			}
		}

		return b;
	}
}