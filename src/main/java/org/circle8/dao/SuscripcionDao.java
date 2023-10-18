package org.circle8.dao;

import com.google.inject.Inject;

import lombok.val;

import org.circle8.entity.Ciudadano;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Residuo;
import org.circle8.entity.Retiro;
import org.circle8.entity.Suscripcion;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Transporte;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.expand.SuscripcionExpand;
import org.circle8.expand.TransporteExpand;
import org.circle8.filter.RecorridoFilter;
import org.circle8.filter.SuscripcionFilter;
import org.circle8.filter.TransporteFilter;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
			sus.R"ID", sus."UltimaRenovacion", sus."ProximaRenovacion", sus."PlanId"
			""";
	
	private static final String SELECT_PLAN = """
			, p."ID", p."Nombre", p."Precio", p."MesesRenovacion", p."CantUsuarios"
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

	public Optional<Suscripcion> get(Transaction t, SuscripcionFilter f, SuscripcionExpand x) throws PersistenceException {
		try ( val select = createSelect(t, f, x) ) {
			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();

				return Optional.of(buildSuscripcion(rs, x));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting suscripcion", e);
		}
	}
	
	private PreparedStatement createSelect(Transaction t, SuscripcionFilter f, SuscripcionExpand x)
			throws SQLException, PersistenceException {
		val select = new StringBuilder(SELECT_SIMPLE);
		val join = new StringBuilder("");
		if (x.plan) {
			select.append(SELECT_PLAN);
			join.append(JOIN_PLAN);
		}

		List<Object> parameters = new ArrayList<>();
		val where = new StringBuilder();
		appendCondition(f.id, WHERE_ID, where, parameters);
		appendCondition(f.planId, WHERE_PLAN_ID, where, parameters);
		appendInequality(f.proximaRenovacion, "AND sus.\"ProximaRenovacion\" %s\n", where, parameters);
		appendInequality(f.ultimaRenovacion, "AND sus.\"UltimaRenovacion\" %s\n", where, parameters);

		val sql = String.format(SELECT_FMT, select, join, where);
		return t.prepareStatement(sql, parameters);
	}
	
	private Suscripcion buildSuscripcion(ResultSet rs, SuscripcionExpand x) throws SQLException {
		val s = new Suscripcion(rs.getLong("ID"));
		if(rs.getDate("UltimaRenovacion") != null)
			s.ultimaRenovacion = rs.getDate("UltimaRenovacion").toLocalDate();
		if(rs.getTimestamp("ProximaRenovacion") != null)
			s.proximaRenovacion = rs.getDate("UltimaRenovacion").toLocalDate();

		return s;
	}
	
}
	
	
