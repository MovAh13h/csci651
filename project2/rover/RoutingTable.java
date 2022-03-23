package rover;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingTable {
	public ConcurrentHashMap<InetAddress, RoutingEntry> t;

	public RoutingTable() {
		t = new ConcurrentHashMap<InetAddress, RoutingEntry>();
				
	}
}