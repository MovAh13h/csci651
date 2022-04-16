package rover;

import java.net.InetAddress;
import java.time.LocalTime;

public class RoutingEntry {
	public InetAddress ip;
	public InetAddress nextHop;
	public InetAddress subnet;
	public int cost;
	public LocalTime time;

	public RoutingEntry(InetAddress ip, InetAddress nextHop, InetAddress subnet, int cost) {
		this.nextHop = nextHop;
		this.subnet = subnet;
		this.cost = cost;
		this.time = LocalTime.now();
	}

	public RoutingEntry(InetAddress ip, InetAddress nextHop, InetAddress subnet, int cost, LocalTime t) {
		this(ip, nextHop, subnet, cost);
		this.time = t;
	}

    public RoutingEntry(byte[] data) {
        int i = 0;
        i++; // addrFamInt 1
        i++; // addrFamInt 2
        i++; // route tag 1
        i++; // route tag 2
        try {
            byte[] ipB = new byte[] {data[i++], data[i++], data[i++], data[i++]};
            byte[] subnetB = new byte[] {data[i++], data[i++], data[i++], data[i++]};
            byte[] nextHopB = new byte[] {data[i++], data[i++], data[i++], data[i++]};
            cost = data[i++] << 24 | data[i++] << 16 | data[i++] << 8 | data[i++];

            ip = InetAddress.getByAddress(ipB);
            subnet = InetAddress.getByAddress(subnetB);
            nextHop = InetAddress.getByAddress(nextHopB);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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