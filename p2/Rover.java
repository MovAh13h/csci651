import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;;

public class Rover {
    Thread client;
    Thread sender;
    RoutingTable table;
    HashMap<String, Long> tMap;
    int port;
    String multicastIP;
    String selfIP;
    int id;

    public Rover(int id, String multicastIP, int port) {
        this.id = id;
        this.multicastIP = multicastIP;
        this.port = port;
        this.table = new RoutingTable(id);
        this.tMap = new HashMap<>();
        this.selfIP = "10.0." + id + ".0";
    }

    public void run() {
        client = new Thread(new UdpMulticastClient(this));
        sender = new Thread(new UdpMulticastSender(this));

        client.start();
        sender.start();
    }

    public boolean updateTable(RoutingTable ot, InetAddress addr) throws UnknownHostException {
        boolean c = false;

        for (Entry<String, RoutingEntry> e : table.t.entrySet()) {
            String key = e.getKey();
            RoutingEntry re = e.getValue();

            if (re.nextHop.equals(addr.getHostAddress())) {
                if (ot.t.get(key).metric == 16) {
                    if (re.metric != ot.t.get(key).metric) {
                        re.metric = ot.t.get(key).metric;
                        c = true;
                    }
                } else {
                    if (re.metric != ot.t.get(key).metric + 1) {
                        re.metric = ot.t.get(key).metric + 1;
                        c = true;
                    }
                }
            }
        }

        for (Entry<String, RoutingEntry> e : ot.t.entrySet()) {
            String key = e.getKey();
            RoutingEntry re = e.getValue();

            if (re.nextHop.equals(InetAddress.getLocalHost().getHostAddress())) {
                continue;
            }
            
            if (!table.t.containsKey(key)){
                table.t.put(key, new RoutingEntry(key, addr.getHostAddress(), re.subnet, re.metric + 1));
                c = true;
            } else {
                if (re.metric + 1 < table.t.get(key).metric) {
                    table.t.get(key).metric = re.metric + 1;
                    table.t.get(key).nextHop = addr.getHostAddress();
                    c = true;
                }
            }
        }

        tMap.put(addr.getHostAddress(), System.currentTimeMillis());
        return c;
    }

    public boolean cleanup() {
        boolean c = false;

        LinkedList<String> r = new LinkedList<>();
        for (Entry<String, Long> e : tMap.entrySet()) {
            String key = e.getKey();
            long t = e.getValue();

            if (System.currentTimeMillis() - t > 10000) {
                for (Map.Entry<String, RoutingEntry> ee : table.t.entrySet()) {
                    RoutingEntry re = ee.getValue();
                    if (re.nextHop.equals(key)) re.metric = 16;                    
                }
                r.add(key);
            }
        }

        for (String k : r) tMap.remove(k);
        
        return c;
    }
}
