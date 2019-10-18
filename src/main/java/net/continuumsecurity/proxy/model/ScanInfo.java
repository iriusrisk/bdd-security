package net.continuumsecurity.proxy.model;

import org.zaproxy.clientapi.core.ApiResponseSet;

/**
 * Created by stephen on 16/04/15.
 */
public class ScanInfo implements Comparable<ScanInfo> {
    int progress;
    int id;
    State state;

    @Override
    public int compareTo(ScanInfo o) {
        return id-o.getId();
    }

    public enum State {
        NOT_STARTED,
        FINISHED,
        PAUSED,
        RUNNING;

        public static State parse(String s) {
            if ("NOT_STARTED".equalsIgnoreCase(s)) return NOT_STARTED;
            if ("FINISHED".equalsIgnoreCase(s)) return FINISHED;
            if ("PAUSED".equalsIgnoreCase(s)) return PAUSED;
            if ("RUNNING".equalsIgnoreCase(s)) return RUNNING;
            throw new RuntimeException("Unknown state: "+s);
        }
    }

    public ScanInfo(ApiResponseSet response) {
        id = Integer.parseInt(response.getStringValue("id"));
        progress = Integer.parseInt(response.getStringValue("progress"));
        state = State.parse(response.getStringValue("state"));
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
