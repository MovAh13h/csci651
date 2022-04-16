package rover;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalTime;

public class RoutingTable {
	public ConcurrentHashMap<InetAddress, RoutingEntry> t;

	public RoutingTable(byte[] data) {
		t = new ConcurrentHashMap<InetAddress, RoutingEntry>();

		for (int i = 0; i < data.length / 20; i++) {
			byte[] ripEntryData = Arrays.copyOfRange(data, i * 20, i * 20 + 20);
			RoutingEntry re = new RoutingEntry(ripEntryData);
			add(re);
		}
	}

	public RoutingTable(byte id) {
		t = new ConcurrentHashMap<InetAddress, RoutingEntry>();
		try {
			InetAddress ip = InetAddress.getByName("10.0." + id + ".0");
			InetAddress nextHop = InetAddress.getByName("127.0.0.1");
			InetAddress subnet = InetAddress.getByName("255.255.255.0");
			int cost = 0;
			t.put(ip, new RoutingEntry(ip, nextHop, subnet, cost));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public boolean updateTable(RoutingTable ot, InetAddress addr) throws Exception {
		// ignore self packet
		if (addr.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
			return false;
		}

		boolean changed = false;

		for (Map.Entry<InetAddress, RoutingEntry> e : t.entrySet()) {
			InetAddress key = e.getKey();
			RoutingEntry re = e.getValue();

			if (re.nextHop.toString().equals(addr.getHostAddress())) {
				if (ot.t.get(key).cost == 16) {
					if (re.cost != ot.t.get(key).cost) {
						re.cost = ot.t.get(key).cost;
						re.time = LocalTime.now();
						changed = true;
					}
				} else {
					if (re.cost != ot.t.get(key).cost + 1) {
						re.cost = ot.t.get(key).cost + 1;
						re.time = LocalTime.now();
						changed = true;
					}
				}
			}
		}

		for (Map.Entry<InetAddress, RoutingEntry> e : ot.t.entrySet()) {
			InetAddress key = e.getKey();
			RoutingEntry re = e.getValue();

			if (re.nextHop.toString().equals(InetAddress.getLocalHost().getHostAddress())) {
				continue;
			}

			if (!this.t.containsKey(key)) {
				t.put(key, new RoutingEntry(key, addr, re.subnet, re.cost + 1));
				changed = true;
			} else {
				if (re.cost + 1 < t.get(key).cost) {
					t.get(key).cost = re.cost + 1;
					t.get(key).nextHop = addr;
					t.get(key).time = LocalTime.now();
					changed = true;
				}
			}
		}

		return changed;
	}

	public boolean cleanUpTable() {
		boolean changed = false;

		for (RoutingEntry re : t.values()) {
			if (re.time.plusSeconds(10).compareTo(LocalTime.now()) < 0) {
				for (RoutingEntry rr : t.values()) {
					if (rr.cost != 16) {
						re.cost = 16;
						changed = true;
					}
				}
			}
		}

		return changed;
	}

	public void add(RoutingEntry re) {
		t.put(re.ip, re);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(LocalTime.now());
		sb.append("\nAddress\t\tNextHop\t\tCost\n");

		for (RoutingEntry re : t.values()) {
			sb.append(re.ip.getHostAddress() + "/24\t");
			sb.append(re.nextHop.getHostAddress() + "\t");
			sb.append(re.cost + "\n");
		}

		return sb.toString();
	}
}