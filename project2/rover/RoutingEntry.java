package rover;

import java.net.InetAddress;
import java.time.LocalTime;

public class RoutingEntry {
	public InetAddress ip;
	public InetAddress nextHop;
	public InetAddress subnet;
	public int cost;
	public LocalTime time;

	public RoutingEntry(InetAddress nextHop, InetAddress subnet, int cost) {
		this.nextHop = nextHop;
		this.subnet = subnet;
		this.cost = cost;
		this.time = LocalTime.now();
	}

	public RoutingEntry(InetAddress nextHop, InetAddress subnet, int cost, LocalTime t) {
		this(nextHop, subnet, cost);
		this.time = t;
	}

	public byte[] toRIPEntry() {
        byte[] b = new byte[20];

        int i = 0;
        b[i++] = 0; // addrfamint 1
        b[i++] = 0; // addrfamint 2
        b[i++] = 0; // route tag 1
        b[i++] = 0; // route tag 2

        byte[] ipB = ip.getAddress();
        byte[] subnetB = subnet.getAddress();
        byte[] nextHopB = nextHop.getAddress();
        byte[] costB = new byte[] {
            (byte) (cost >>> 24),
            (byte) (cost >>> 16),
            (byte) (cost >>> 8),
            (byte) cost
        };

        for (int j = 0; j < 4; j++) b[i++] = ipB[j];
        for (int j = 0; j < 4; j++) b[i++] = subnetB[j];
        for (int j = 0; j < 4; j++) b[i++] = nextHopB[j];
        for (int j = 0; j < 4; j++) b[i++] = costB[j];

        return b;
    }

	public boolean equals(Object s) {
        if (this == s) {
            return true;
        } else if (s == null || getClass() != s.getClass()) {
            return false;
        }

        RoutingEntry ss = (RoutingEntry) s;

        return ip.equals(ss.ip) && subnet.equals(ss.subnet);
    }
}