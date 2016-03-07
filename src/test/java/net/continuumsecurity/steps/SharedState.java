package net.continuumsecurity.steps;

import edu.umass.cs.benchlab.har.HarEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stephen on 04/03/2016.
 */
public class SharedState {
    private static SharedState ourInstance = new SharedState();
    Map<String, List<HarEntry>> methodProxyMap = new HashMap<String, List<HarEntry>>();

    public synchronized Map<String, List<HarEntry>> getMethodProxyMap() {
        return methodProxyMap;
    }

    public static SharedState getInstance() {
        return ourInstance;
    }

    private SharedState() {
    }
}
