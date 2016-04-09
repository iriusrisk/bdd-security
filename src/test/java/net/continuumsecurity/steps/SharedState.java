package net.continuumsecurity.steps;

import edu.umass.cs.benchlab.har.HarEntry;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.UserPassCredentials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stephen on 04/03/2016.
 */
public class SharedState {
    private static SharedState ourInstance = new SharedState();
    Map<String, List<HarEntry>> methodProxyMap = new HashMap<String, List<HarEntry>>();
    private Map<String, String> sessionIds = new HashMap<>();
    private List<HarEntry> recordedEntries;
    private boolean httpHeadersRecorded = false;
    private boolean navigated;
    private boolean spidered;
    private Credentials credentials;



    public synchronized Map<String, List<HarEntry>> getMethodProxyMap() {
        return methodProxyMap;
    }

    public boolean isNavigated() {
        return navigated;
    }

    public void setNavigated(boolean navigated) {
        this.navigated = navigated;
    }

    public boolean isSpidered() {
        return spidered;
    }

    public void setSpidered(boolean spidered) {
        this.spidered = spidered;
    }

    public static SharedState getInstance() {
        return ourInstance;
    }

    private SharedState() {
    }

    public Map<String, String> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(Map<String, String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<HarEntry> getRecordedEntries() {
        return recordedEntries;
    }

    public void setRecordedEntries(List<HarEntry> recordedEntries) {
        this.recordedEntries = recordedEntries;
    }

    public boolean isHttpHeadersRecorded() {
        return httpHeadersRecorded;
    }

    public void setHttpHeadersRecorded(boolean httpHeadersRecorded) {
        this.httpHeadersRecorded = httpHeadersRecorded;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public UserPassCredentials getUserPassCredentials() {
        return (UserPassCredentials)credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
