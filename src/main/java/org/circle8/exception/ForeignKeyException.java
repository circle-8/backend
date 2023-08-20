package org.circle8.exception;

public class ForeignKeyException extends PersistenceException {
	public ForeignKeyException(String message) { super(message); }

	public ForeignKeyException(String message, Throwable cause) { super(message, cause); }
}
