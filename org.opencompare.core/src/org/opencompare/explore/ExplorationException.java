package org.opencompare.explore;

public class ExplorationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExplorationException() {
	}

	public ExplorationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExplorationException(String message) {
		super(message);
	}

	public ExplorationException(Throwable cause) {
		super(cause);
	}

}
