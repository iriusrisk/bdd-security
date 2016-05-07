package net.continuumsecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 09/06/15.
 */
public class Hosts {
    List<Host> hosts = new ArrayList<>();

    public void addHost(Host host) {
        hosts.add(host);
    }

    public List<Host> getHosts() {
        return hosts;
    }
}
