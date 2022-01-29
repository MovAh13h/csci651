/*
 * EthernetPacket.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */

package pktanalyzer;

import java.util.Arrays;

/**
 * The classes parses a Ethernet packet from the bytes provided.
 *
 * Reference: https://en.wikipedia.org/wiki/Ethernet_frame
 *
 * @author Tanishq Jain <tj3989@cs.rit.edu>
 */

public class EthernetPacket {
	// destination MAC address
	private String destMac = "";

	// source MAC address
	private String srcMac = "";

	// EtherType of the packet
	private EtherType et;

	// frame length
	private int frameLength = 0;

	// payload of the packet
	private byte[] payload;

	EthernetPacket(byte[] data) throws Exception {
		// Prof. said String.format is ok if used for pretty printing. just
		// not for pasring.
		
		// parse dest mac address
		for (int i = 0; i < 6; i++) {
			if (i < 5) {
				destMac += String.format("%02x", data[i]) + ":";
			} else {
				destMac += String.format("%02x", data[i]);
			}
		}

		// parse src mac address
		for (int i = 6; i < 12; i++) {
			if (i < 11) {
				srcMac += String.format("%02x", data[i]) + ":";
			} else {
				srcMac += String.format("%02x", data[i]);
			}
		}

		int value = (data[12] & 0xff) << 8 | data[13] & 0xff;

		if (value != 0x8100) {
			// VTAG is not present
			et = new EtherType(value);
		} else {
			// VTAG is present
			et = new EtherType((data[16] & 0xff) << 8 | data[17] & 0xff);
		}
		
		frameLength = data.length;

		// TODO: In future handle the following:
		// 1) Checksum
		// 2) Exact payload size by frameLength - (7/13 + 4) depending upon vtag
		//    present or no
		if (vlan()) {
			payload = Arrays.copyOfRange(data, 18, data.length);
		} else {
			payload = Arrays.copyOfRange(data, 14, data.length);
		}
	}

	public String destMac() {
		return destMac;
	}

	public String srcMac() {
		return srcMac;
	}

	public boolean vlan() {
		return et.value() == 0x8100;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("ETHER: ----- Ether Header -----\n");
		sb.append("ETHER:\n");
		sb.append("ETHER: Packet size = " + frameLength() + " bytes\n");
		sb.append("ETHER: Destination = " + destMac() + ",\n");
		sb.append("ETHER: Source      = " + srcMac() + ",\n");
		sb.append("ETHER: Ethertype = " + String.format("%04x", et.value())
			+ " (" + et.label() + ")\n");
		sb.append("ETHER:\n");

		return sb.toString();
	}

	public byte[] payload() {
		// arrays are by reference in Java. send a copy so outside changes
		// to the byte array dont affect internal data
		return Arrays.copyOfRange(this.payload, 0, this.payload.length);
	}

	public String ethertypeLabel() {
		return et.label();
	}

	public int ethertypeValue() {
		return et.value();
	}

	public int frameLength() {
		return frameLength;
	}
}