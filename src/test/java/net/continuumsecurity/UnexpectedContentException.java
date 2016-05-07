package net.continuumsecurity;

public class UnexpectedContentException extends RuntimeException {
	public UnexpectedContentException(String msg) {
		super(msg);
	}

    public UnexpectedContentException(Throwable cause) {
        super(cause);
    }

    public UnexpectedContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
