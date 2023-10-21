package org.circle8.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Plan;
import org.circle8.entity.Suscripcion;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.SuscripcionFilter;

import com.google.inject.Inject;

import lombok.val;

public class SuscripcionDao extends Dao {
	private static final String INSERT = """
		INSERT INTO public."Suscripcion"("UltimaRenovacion", "ProximaRenovacion", "PlanId")
		VALUES (?, ?, ?);
		""";
	
	private static final String SELECT_FMT = """
			SELECT
			      %s
			  FROM "Suscripcion" AS sus
			    %s
			 WHERE 1 = 1
			""";
	
	private static final String SELECT_SIMPLE = """
			sus."ID", sus."UltimaRenovacion", sus."ProximaRenovacion", sus."PlanId"
			""";
	
	private static final String SELECT_PLAN = """
			, p."ID" AS IDplan, p."Nombre", p."Precio", p."MesesRenovacion", p."CantUsuarios"
			""";
	
	private static final String JOIN_PLAN = """
			JOIN "Plan" AS p ON p."ID" = sus."PlanId"
			""";
	
	private static final String WHERE_ID = """
			AND sus."ID" = ?
			""";
	
	private static final String WHERE_PLAN_ID = """
			AND sus."PlanId" = ?
			""";

	@Inject SuscripcionDao(DataSource ds) { super(ds); }

	public Suscripcion save(Transaction t, Suscripcion s) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setDate(1, Date.valueOf(s.ultimaRenovacion));
			insert.setDate(2, Date.valueOf(s.proximaRenovacion));
			insert.setLong(3, s.plan.id);
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the suscripcion failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( rs.next() ) s.id = rs.getLong(1);
				else throw new SQLException("Creating the suscripcion failed, no ID obtained");
			}

			return s;
		} catch ( SQLException e ) {
			throw new PersistenceException("error inserting suscripcion", e);
		}
	}

	public Optional<Suscripcion> get(Transaction t, SuscripcionFilter f) throws PersistenceException {
		try ( val select = createSelect(t, f) ) {
			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();

				return Optional.of(buildSuscripcion(rs));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting suscripcion", e);
		}
	}
	
	private PreparedStatement createSelect(Transaction t, SuscripcionFilter f)
			throws SQLException, PersistenceException {
		var select = SELECT_SIMPLE + SELECT_PLAN;
		var join = JOIN_PLAN;

		List<Object> parameters = new ArrayList<>();
		var b = new StringBuilder(String.format(SELECT_FMT, select, join));
		appendCondition(f.id, WHERE_ID, b, parameters);
		appendCondition(f.planId, WHERE_PLAN_ID, b, parameters);
		appendInequality(f.proximaRenovacion, "AND sus.\"ProximaRenovacion\" %s\n", b, parameters);
		appendInequality(f.ultimaRenovacion, "AND sus.\"UltimaRenovacion\" %s\n", b, parameters);

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}
	
	private Suscripcion buildSuscripcion(ResultSet rs) throws SQLException {
		val s = new Suscripcion(rs.getLong("ID"));
		if(rs.getDate("UltimaRenovacion") != null)
			s.ultimaRenovacion = rs.getDate("UltimaRenovacion").toLocalDate();
		if(rs.getTimestamp("ProximaRenovacion") != null)
			s.proximaRenovacion = rs.getDate("UltimaRenovacion").toLocalDate();
		s.plan = Plan.builder()
				.id(rs.getLong("IDplan"))
				.nombre(rs.getString("Nombre"))
				.precio(rs.getBigDecimal("Precio"))
				.mesesRenovacion(rs.getInt("MesesRenovacion"))
				.cantidadUsuarios(rs.getInt("CantUsuarios"))
				.build();

		return s;
	}
	
}
	
	
