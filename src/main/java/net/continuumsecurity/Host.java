package net.continuumsecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 09/06/15.
 */
public class Host {
    String name;
    List<Port> ports = new ArrayList<>();

    public Host(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getPortNumbers() {
        List<Integer> portNums = new ArrayList<>();
        for (Port port : ports) {
            portNums.add(port.getNumber());
        }
        return portNums;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    public void addPort(Port port) {
       ports.add(port);
    }
}
