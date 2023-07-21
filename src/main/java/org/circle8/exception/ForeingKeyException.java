package org.circle8.exception;

public class ForeingKeyException extends PersistenceException {
	public ForeingKeyException(String message) { super(message); }

	public ForeingKeyException(String message, Throwable cause) { super(message, cause); }
}
