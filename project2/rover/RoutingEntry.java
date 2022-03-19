package rover;

import java.net.*;
import java.time.LocalTime;

public class RoutingEntry {
    public static int INFINITY = 16;

    private InetAddress destination;
    private InetAddress subnet;
    private InetAddress gateway; // next hop
    private int metric;
    private LocalTime time;

    public RoutingEntry(InetAddress d, InetAddress s, InetAddress g, int c) {
        this.destination = d;
        this.subnet = s;
        this.gateway = g;
        this.metric = c;
        this.time = LocalTime.now();
    }

    public RoutingEntry(InetAddress d, InetAddress s, InetAddress g, int c, LocalTime t) {
        this(d, s, g, c);
        this.time = t;
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

    public LocalTime getLocalTime() {
        return this.time;
    }

    public void setLocalTime(LocalTime t) {
        this.time = t;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("RoutingEntry(");
        sb.append(getDestination().getHostAddress() + "|");
        sb.append(getSubnet().getHostAddress() + "|");
        sb.append(getGateway().getHostAddress() + "|");
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

        for (int j = 0; j < 4; j++) b[i++] = destB[j];
        for (int j = 0; j < 4; j++) b[i++] = subnetB[j];
        for (int j = 0; j < 4; j++) b[i++] = nextHopB[j];
        for (int j = 0; j < 4; j++) b[i++] = metricB[j];

        return b;
    }

    public boolean equals(Object s) {
        if (this == s) {
            return true;
        } else if (s == null || getClass() != s.getClass()) {
            return false;
        }

        RoutingEntry ss = (RoutingEntry) s;

        return getDestination() == ss.getDestination() && getSubnet() == ss.getSubnet();
    }
}
