package org.circle8.exception;

public class DuplicatedEntry extends PersistenceException {
	public DuplicatedEntry(String message) { super(message); }

	public DuplicatedEntry(String message, Throwable cause) { super(message, cause); }
}
