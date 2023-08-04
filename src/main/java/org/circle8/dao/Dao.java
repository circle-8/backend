package org.circle8.dao;

import javax.sql.DataSource;

import org.circle8.exception.PersistenceException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
abstract class Dao {
	protected final DataSource ds;

	@Inject
	Dao(DataSource ds) { this.ds = ds; }

	public Transaction open() throws PersistenceException {
		return open(false);
	}

	public Transaction open(boolean autoCommit) throws PersistenceException {
		return new Transaction(this.ds, autoCommit);
	}
}
