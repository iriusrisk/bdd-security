package net.continuumsecurity;

public class FatalException extends RuntimeException {
    public FatalException(String msg) {
        super(msg);
        System.exit(1);
    }
}

