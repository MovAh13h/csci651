package rover;

import java.util.*;

// Ref: https://en.wikipedia.org/wiki/Routing_table

public class Table {
	private byte id;
	// Is a Key val map needed? can i just loop through it?
	// private HashMap<Byte, RoutingEntry> table;
	private Vector<RoutingEntry> table;

	public Table(byte id) {
		this.id = id;
		this.table = new Vector<>();
	}

	public Table(byte[] data) {
		this.table = new Vector<>();
		
		for (int i = 0; i < data.length / 20; i++) {
			byte[] ripEntryData = Arrays.copyOfRange(data, i * 20, i * 20 + 20);
			RoutingEntry re = new RoutingEntry(ripEntryData);
			this.table.add(re);
		}
	}

	public Iterator<RoutingEntry> iter() {
		return this.table.iterator();
	}

	public void add(RoutingEntry re) {
		this.table.add(re);
	}

	public byte getID() {
		return id;
	}

	public int size() {
		return table.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Address\t\tNextHop\t\tCost\n");

		Iterator<RoutingEntry> it = iter();

		while (it.hasNext()) {
			RoutingEntry re = it.next();

			sb.append(re.getDestination().getHostAddress() + "/24\t");
			sb.append(re.getGateway().getHostAddress() + "\t");
			sb.append(re.getMetric() + "\n");
		}

		return sb.toString();
	}
}