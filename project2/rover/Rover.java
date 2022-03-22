package rover;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.Iterator;

public class Rover extends Thread {
	private byte id; // unique 8 bit number for Rover
	private String multicastIP;
	private int port;
	private InetAddress selfIp;
	private Table table;
	private Thread sender;
	private Thread client;

	public Rover(byte id, String multicastIP, int port) throws UnknownHostException {
		this.id = id;
		this.multicastIP = multicastIP;
		this.port = port;
		table = new Table(this.id);
		selfIp = InetAddress.getByName("10.0." + id + ".0");
		InetAddress subnet = InetAddress.getByName("255.255.255.0");
		InetAddress gateway = InetAddress.getByName("127.0.0.1");
		table.add(new RoutingEntry(selfIp, subnet, gateway, 0));
		System.out.println(table);
	}
	
	public InetAddress getNetworkIp() {
		return selfIp;
	}

	public byte getRoverID() {
		return this.id;
	}
	
	public String getMulticastIP() {
		return this.multicastIP;
	}

	public int getPort() {
		return this.port;
	}

	public Table getTable() {
		return this.table;
	}

	public void updateTable(RoutingEntry r, InetAddress src) {
		InetAddress d1 = r.getDestination();
		InetAddress s1 = r.getSubnet();
		InetAddress g1 = r.getGateway();
		int metric = r.getMetric() + 1;
		metric = metric >= RoutingEntry.INFINITY ? RoutingEntry.INFINITY : metric;

		// nothing in table, add it directly
		if (table.size() == 0) {
			r.setMetric(metric);
			r.setLocalTime(LocalTime.now());
			table.add(r);
			System.out.println(table);
		} else {
			boolean added = false;
			Iterator<RoutingEntry> it = table.iter();

			while (it.hasNext()) {
				RoutingEntry re = it.next();

				if (r.equals(re)) {
					added = true;
					// just a normal timely update
					if (metric == 1) {
						re.setLocalTime(LocalTime.now());
					}

					if (re.getGateway().equals(g1) && re.getMetric() != 1 && re.getMetric() != metric) {
						re.setMetric(metric);
						re.setLocalTime(LocalTime.now());
						System.out.println(table);
					} else if (!re.getGateway().equals(g1) && metric < re.getMetric()) {
						re.setGateway(g1);
						re.setMetric(metric);
						re.setLocalTime(LocalTime.now());
						System.out.println(table);
					}

					added = true;
				}
			}

			if (added == false) {
				r.setLocalTime(LocalTime.now());
				r.setMetric(metric);
				table.add(r);
				System.out.println(table);
			}
		}
	}

	public void cleanRouteTable() {
		Iterator<RoutingEntry> it = table.iter();

		while (it.hasNext()) {
			RoutingEntry re = it.next();

			if (re.getLocalTime().plusSeconds(10).compareTo(LocalTime.now()) < 0 && !re.getDestination().equals(selfIp) && re.getMetric() != RoutingEntry.INFINITY) {
				re.setMetric(RoutingEntry.INFINITY);
				re.setLocalTime(LocalTime.now());
				Iterator<RoutingEntry> itt = table.iter();

				while (itt.hasNext()) {
					RoutingEntry rr = itt.next();

					if (rr.getGateway().equals(re.getDestination())) {
						rr.setMetric(RoutingEntry.INFINITY);
						rr.setLocalTime(LocalTime.now());
					}
				}

				System.out.println(table);
			}
		}
	}

	@Override
	public void run() {
		client = new Thread(new Client(this));
		sender = new Thread(new Sender(this));

		client.start();
		sender.start();
	}
}