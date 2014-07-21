package org.opencompare.external;

public class ExternalException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExternalException() {
	}

	public ExternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExternalException(String message) {
		super(message);
	}

	public ExternalException(Throwable cause) {
		super(cause);
	}

}
