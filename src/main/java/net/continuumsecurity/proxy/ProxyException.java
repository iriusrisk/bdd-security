package net.continuumsecurity.proxy;

public class ProxyException extends RuntimeException {

    private static final long serialVersionUID = -8089119902100465025L;

    public ProxyException() {
        super();
    }

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }
}
