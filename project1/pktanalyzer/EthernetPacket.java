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

		// check if the 17-18th bytes are 0x8100 for VTAG
		int value = 0, length = 0;
		value = data[16] & 0xff;
		value = (value << 8) | data[17] & 0xff;

		if (value != 0x8100) {
			// VTAG is not present
			// if VTAG is not present
			// bytes 12-13 are the EtherType/Size followed by the payload
			length = data[12] & 0xff;
			length = (length << 8) | (data[13] & 0xff);
		} else {
			// VTAG is present
			// if VTAG is present, bytes 12-15 are the VTAG data and byte 16-17
			// are 0x8100
			length = value;
		}
		
		et = new EtherType(length);
		
		frameLength = data.length;

		// TODO: In future handle the following:
		// 1) Checksum
		// 2) Exact payload size by frameLength - (7/13 + 4) depending upon vtag
		//    present or no
		//    
		// Note: This class might get turned into a package of its own later on
		// and hence I want objects of this class to be independent and not
		// rely on any form of outside data after the constructor is run. Hence,
		// internal copy of the payload is made.
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