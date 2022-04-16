import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RoutingTable implements Serializable {
    public HashMap<String, RoutingEntry> t = new HashMap<>();

    public RoutingTable(int id) {
        String selfIp = "10.10." + id + ".0";
        t.put(selfIp, new RoutingEntry(selfIp, selfIp, "24", 1));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, RoutingEntry> entry : t.entrySet()) {
            String key = entry.getKey();
            RoutingEntry re = entry.getValue();

            sb.append(key+ "/" + 24 + "\t"+ re.nextHop + "\t" + re.metric + "\n");
        }

        return sb.toString();
    }
}
