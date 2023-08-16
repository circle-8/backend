package org.circle8.dao;

import lombok.val;
import org.circle8.exception.PersistenceException;
import org.intellij.lang.annotations.MagicConstant;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Wrapper for {@link Connection} which can be used across services and DAOs
 */
public class Transaction implements AutoCloseable {
	private final Connection conn;
	private boolean committed;

	Transaction(DataSource ds, boolean autoCommit) throws PersistenceException {
		try {
			this.conn = ds.getConnection();
			this.conn.setAutoCommit(autoCommit);
			this.committed = autoCommit;
		} catch (SQLException e) {
			throw new PersistenceException("error creating connection", e);
		}
	}

	public Statement createStatement() throws PersistenceException {
		try {
			return this.conn.createStatement();
		} catch ( SQLException e ) {
			throw new PersistenceException("error creating statement", e);
		}
	}

	public PreparedStatement prepareStatement(final String sql, List<Object> params) throws PersistenceException {
		val p = this.prepareStatement(sql);

		try {
			for (int i = 0; i < params.size(); i++)
				p.setObject(i + 1, params.get(i));
		} catch ( SQLException e ) {
			throw new PersistenceException("error setting parameters for prepared statement", e);
		}

		return p;
	}

	public PreparedStatement prepareStatement(final String sql) throws PersistenceException {
		try {
			return this.conn.prepareStatement(sql);
		} catch ( SQLException e ) {
			throw new PersistenceException("error preparing statement", e);
		}
	}

	public PreparedStatement prepareStatement(final String sql, @MagicConstant(flagsFromClass = Statement.class) int resultSetType) throws PersistenceException {
		try {
			return this.conn.prepareStatement(sql, resultSetType);
		} catch ( SQLException e ) {
			throw new PersistenceException("error preparing statement", e);
		}
	}

	public void commit() throws PersistenceException {
		try {
			this.conn.commit();
			this.committed = true;
		} catch (SQLException e) {
			throw new PersistenceException("error committing", e);
		}
	}

	public void rollback() throws PersistenceException {
		try {
			this.conn.rollback();
		} catch (SQLException e) {
			throw new PersistenceException("error rollbacking", e);
		}
	}

	/**
	 * If the statements aren't committed, then close rollbacks them
	 */
	@Override
	public void close() throws PersistenceException {
		try {
			if ( !this.committed)
				this.rollback();

			this.conn.close();
		} catch (SQLException e) {
			throw new PersistenceException("error closing connection", e);
		}
	}
}
