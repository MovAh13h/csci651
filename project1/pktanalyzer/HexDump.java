package pktanalyzer;

import java.util.ArrayList;

public class HexDump {
	private String[] hexdump;

	HexDump(byte[] data) throws Exception {
		ArrayList<String> hex = new ArrayList<>();
		ArrayList<String> dump = new ArrayList<>();

		StringBuilder sbh = new StringBuilder();
		StringBuilder sbd = new StringBuilder();

		for (int i = 0; i < data.length; i++) {
			String hexByte = String.format("%02x", data[i]);
			sbh.append(hexByte);

			if (data[i] >= 32 && data[i] <= 126) {
				sbd.append((char) data[i]);
			} else {
				sbd.append('.');
			}

			if (((i + 1) % 16 == 0) || (i == data.length - 1)) {
				hex.add(String.format("%-39s", sbh.toString()));
				dump.add(String.format("%-16s", sbd.toString())
					.replace(' ', '.'));

				sbh.delete(0, sbh.length());
				sbd.delete(0, sbd.length());
				continue;
			}

			if (i % 2 == 1) {
				sbh.append(" ");
			}
		}

		hexdump = new String[hex.size()];

		for (int i = 0; i < hex.size(); i++) {
			hexdump[i] = hex.get(i) + "    '" + dump.get(i) + "'";
		}
	}

	public String[] hexdump() {
		return hexdump;
	}
}