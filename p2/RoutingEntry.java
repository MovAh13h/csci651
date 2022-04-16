import java.io.Serializable;

public class RoutingEntry implements Serializable {
    String ip;
    String nextHop;
    int metric;
    String subnet;

    public RoutingEntry (String ip, String nextHop, String subnet, int metric) {
        this.ip = ip;
        this.nextHop = nextHop;
        this.metric = metric;
        this.subnet = subnet;
    }
}
