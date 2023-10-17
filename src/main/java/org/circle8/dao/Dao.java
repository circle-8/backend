package org.circle8.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.InequalityFilter;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

	protected PreparedStatement prepareStatement(
		Transaction t,
		String sql,
		List<Object> parameters
	) throws PersistenceException, SQLException{
		var p = t.prepareStatement(sql);
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		return p;
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
		if ( l != null && !l.isEmpty() ) {
			conditions.append(String.format(whereFmt, listParam(l)));
			l.forEach(p -> addObject(p, params));
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
			addObject(o, params);
		}
	}

	protected void appendInequality(
		InequalityFilter<?> f,
		String whereFmt,
		StringBuilder conditions,
		List<Object> params
	) {
		if ( f == null ) return;

		appendCondition(f.equal, whereFmt.formatted("= ?"), conditions, params);
		appendCondition(f.gt, whereFmt.formatted("> ?"), conditions, params);
		appendCondition(f.ge, whereFmt.formatted(">= ?"), conditions, params);
		appendCondition(f.lt, whereFmt.formatted("< ?"), conditions, params);
		appendCondition(f.le, whereFmt.formatted("<= ?"), conditions, params);

		if ( Boolean.TRUE.equals(f.isNull) ) {
			conditions.append(whereFmt.formatted("IS NULL"));
		} else if ( Boolean.FALSE.equals(f.isNull) ) {
			conditions.append(whereFmt.formatted("IS NOT NULL"));
		}
	}

	protected void appendUpdate(
		Object o,
		String where,
		Collection<String> sets,
		List<Object> params
	) {
		if ( o != null ) {
			sets.add(where);
			addObject(o, params);
		}
	}

	protected static void addObject(Object o, List<Object> params) {
		if ( o instanceof LocalDate ld ) params.add(Date.valueOf(ld));
		else if ( o instanceof ZonedDateTime zd ) params.add(Timestamp.from(zd.toInstant()));
		else if ( o instanceof Enum<?> e ) params.add(e.toString());
		else params.add(o);
	}

	protected <T> List<T> buildList(ResultSet rs, FromList<T> fromList) throws SQLException {
		val l = new ArrayList<T>();
		while ( rs.next() ) l.add(fromList.map(rs));
		return l;
	}

	protected Date date(LocalDate date) {
		return date != null ? Date.valueOf(date) : null;
	}

	protected Timestamp timestamp(ZonedDateTime timestamp) {
		return timestamp != null ? Timestamp.from(timestamp.toInstant()) : null;
	}
}
