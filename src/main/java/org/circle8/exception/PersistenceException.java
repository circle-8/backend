package org.circle8.exception;

public class PersistenceException extends Exception {
	public PersistenceException(String message) { super(message); }

	public PersistenceException(String message, Throwable cause) { super(message, cause); }
}
