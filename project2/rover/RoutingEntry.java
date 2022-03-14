package rover;

import java.net.*;
import java.util.Arrays;

public class RoutingEntry {
    public static int INFINITY = 16;

    private InetAddress destination;
    private InetAddress subnet;
    private InetAddress gateway; // next hop
    private int metric;

    public RoutingEntry(InetAddress d, InetAddress s, InetAddress g, int c) {
        this.destination = d;
        this.subnet = s;
        this.gateway = g;
        this.metric = c;
    }

    // RIP entry to Routing Entry conversion
    public RoutingEntry(byte[] data) {
        int i = 0;
        i++; // addrFamInt 1
        i++; // addrFamInt 2
        i++; // route tag 1
        i++; // route tag 2
        try {
            byte[] destB = new byte[] {data[i++], data[i++], data[i++], data[i++]};
            byte[] subnetB = new byte[] {data[i++], data[i++], data[i++], data[i++]};
            byte[] gatewayB = new byte[] {data[i++], data[i++], data[i++], data[i++]};
            metric = data[i++] << 24 | data[i++] << 16 | data[i++] << 8 | data[i++];

            destination = InetAddress.getByAddress(destB);
            subnet = InetAddress.getByAddress(subnetB);
            gateway = InetAddress.getByAddress(gatewayB);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public InetAddress getDestination() {
        return destination;
    }

    public void setDestination(InetAddress d) {
        destination = d;
    }

    public InetAddress getSubnet() {
        return subnet;
    }

    public void setSubnet(InetAddress s) {
        subnet = s;
    }

    public InetAddress getGateway() {
        return gateway;
    }

    public void setGateway(InetAddress g) {
        gateway = g;
    }

    public int getMetric() {
        return metric;
    }

    public void setMetric(int m) {
        metric = m;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("RoutingEntry(");
        sb.append(getDestination() + "|");
        sb.append(getSubnet() + "|");
        sb.append(getGateway() + "|");
        sb.append(getMetric() + ")\n");

        return sb.toString();
    }

    public byte[] toRIPEntry() {
        byte[] b = new byte[20];

        int i = 0;
        b[i++] = 0; // addrfamint 1
        b[i++] = 0; // addrfamint 2
        b[i++] = 0; // route tag 1
        b[i++] = 0; // route tag 2

        byte[] destB = destination.getAddress();
        byte[] subnetB = subnet.getAddress();
        byte[] nextHopB = gateway.getAddress();
        byte[] metricB = new byte[] {
                (byte) (metric >>> 24),
                (byte) (metric >>> 16),
                (byte) (metric >>> 8),
                (byte) metric
        };

        for (int j = 0; j < 4; j++) {
            b[i + 4] = destB[j];
            b[i + 8] = subnetB[j];
            b[i + 12] = nextHopB[j];
            b[i + 16] = metricB[j];
            i++;
        }

        return b;
    }

    public String getCIDR() {
        try {
            byte[] h = new byte[4];
            byte[] s = new byte[4];

            String[] hsplit = destination.getHostAddress().split("\\.");
            String[] ssplit = subnet.getHostAddress().split("\\.");

            for (int i = 0; i < 4; i++) {
                h[i] = (byte) Byte.toUnsignedInt((byte) Integer.parseInt(hsplit[i]));
                s[i] = (byte) Byte.toUnsignedInt((byte) Integer.parseInt(ssplit[i]));
            }

            int cidrCount = 0;
            for (byte b : s) {
                for (int i = 128; i > 0; i >>= 1) {
                    int bit = (b & i) > 0 ? 1 : 0;
                    if (bit == 1) {
                        cidrCount++;
                    }
                }
            }

            int octetsF = cidrCount / 8;
            int octetsR = cidrCount % 8;

            byte rem = 0b00000000;
            for (int i = 0b10000000; i > 0; i >>= 1) {
                if (octetsR > 0) {
                    rem |= i;
                    octetsR--;
                } else {
                    break;
                }
            }

            int i = 0;
            byte[] f = new byte[4];

            for (i = 0; i < octetsF; i++)
                f[i] = (byte) (0b11111111 & h[i]);

            f[i] = rem;

            InetAddress ff = InetAddress.getByAddress(f);

            return ff.toString().substring(1) + "/" + cidrCount;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return "";
    }
}
