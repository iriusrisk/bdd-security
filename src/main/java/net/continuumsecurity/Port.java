package net.continuumsecurity;

/**
 * Created by stephen on 09/06/15.
 */
public class Port {
    int number;
    State state;

    public Port(int number) {
        this.number = number;
    }

    public Port(int number, State state) {
        this.number = number;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        OPEN("open"),
        CLOSED("closed"),
        FILTERED("filtered"),
        TIMEDOUT("timedout");

        private String text;

        State(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static State fromString(String text) {
            if (text != null) {
                for (State state : State.values()) {
                    if (text.equalsIgnoreCase(state.text)) {
                        return state;
                    }
                }
            }
            throw new IllegalArgumentException("Cannot parse port state: "+text);
        }
    }
}
