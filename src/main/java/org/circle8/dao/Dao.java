package org.circle8.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import org.circle8.exception.PersistenceException;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
abstract class Dao {
	@FunctionalInterface
	interface FromList<T> {
		T map(ResultSet rs) throws SQLException;
	}

	protected final DataSource ds;

	@Inject
	Dao(DataSource ds) { this.ds = ds; }

	public Transaction open() throws PersistenceException {
		return open(false);
	}

	public Transaction open(boolean autoCommit) throws PersistenceException {
		return new Transaction(this.ds, autoCommit);
	}

	/**
	 * @return for l=[x, y, z] returns "?,?,?", for l=[] returns "", for l=[x] returns "?"
	 */
	protected String listParam(List<?> l) {
		return l.stream()
			.map(p -> "?")
			.collect(Collectors.joining(","));
	}

	/**
	 * Appends to the SQL in conditions the where for the list, in case the list is not empty
	 */
	protected void appendListCondition(
		List<?> l,
		String whereFmt,
		StringBuilder conditions,
		List<Object> params
	) {
		if ( !l.isEmpty() ) {
			conditions.append(String.format(whereFmt, listParam(l)));
			params.addAll(l);
		}
	}

	protected void appendCondition(
		Object o,
		String where,
		StringBuilder conditions,
		List<Object> params
	) {
		if ( o != null ) {
			conditions.append(where);
			if ( o instanceof LocalDate ld ) params.add(Date.valueOf(ld));
			else if ( o instanceof ZonedDateTime zd ) params.add(Timestamp.from(zd.toInstant()));
			else params.add(o);
		}
	}

	protected <T> List<T> buildList(ResultSet rs, FromList<T> fromList) throws SQLException {
		val l = new ArrayList<T>();
		while ( rs.next() ) l.add(fromList.map(rs));
		return l;
	}
}
