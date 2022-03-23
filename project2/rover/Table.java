package rover;

import java.time.LocalTime;
import java.util.*;

// Ref: https://en.wikipedia.org/wiki/Routing_table
public class Table {
	private byte id; // rover id
	private Vector<RoutingEntry> table; // routing table

	// Constructor
	public Table(byte id) {
		this.id = id;
		this.table = new Vector<>();
	}

	// Constructor
	public Table(byte[] data) {
		this.table = new Vector<>();
		
		for (int i = 0; i < data.length / 20; i++) {
			byte[] ripEntryData = Arrays.copyOfRange(data, i * 20, i * 20 + 20);
			RoutingEntry re = new RoutingEntry(ripEntryData);
			this.table.add(re);
		}
	}

	// Iterator over the entries of the table
	public Iterator<RoutingEntry> iter() {
		return this.table.iterator();
	}

	// Add new entry to the table
	public void add(RoutingEntry re) {
		this.table.add(re);
	}

	// ID associated to the Rover
	public byte getID() {
		return id;
	}

	// Number of entries in table
	public int size() {
		return table.size();
	}

	// String representation of Table
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(LocalTime.now());
		sb.append("\nAddress\t\tNextHop\t\tCost\n");

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