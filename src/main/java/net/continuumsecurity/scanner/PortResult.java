package net.continuumsecurity.scanner;

public class PortResult {
    private PortState state;
    int port;

    public PortResult(int port) {
        this.port = port;
    }

    public PortState getState() {
        return state;
    }

    public void setState(PortState state) {
        this.state = state;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public enum PortState {
        OPEN,
        CLOSED,
        TIMEDOUT;

        public static PortState fromString(String value) {
            switch(value.toLowerCase()) {
                case "open" : return OPEN;
                case "closed" : return CLOSED;
                case "timedout" : return TIMEDOUT;
            }
            throw new RuntimeException("Unknown port state: "+value);
        }
    }
}
