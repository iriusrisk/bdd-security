package net.continuumsecurity;

import java.util.ArrayList;
import java.util.List;

public class AWSClient {

    public List<String> getAllHosts() {
        List<String> hosts = new ArrayList<>();
        hosts.add("127.0.0.1");
        return hosts;
    };
}
