package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.circle8.dto.TipoUsuario;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.PuntoResiduoExpand;
import org.circle8.filter.PuntoResiduoFilter;
import org.circle8.utils.Dates;

import com.google.inject.Inject;

import lombok.val;

public class PuntoResiduoDao extends Dao {
	private static final String SELECT_FMT = """
		SELECT DISTINCT %s
		  FROM "PuntoResiduo" AS pr
		  JOIN "Ciudadano" AS c ON c."ID" = pr."CiudadanoId"
		    %s
		 WHERE 1=1
		""";
	private static final String SELECT_SIMPLE = "pr.\"ID\", \"Latitud\", \"Longitud\", \"CiudadanoId\", c.\"UsuarioId\"";
	private static final String SELECT_CIUDADANO = ", u.\"Username\", u.\"NombreApellido\", u.\"Email\", u.\"TipoUsuario\"";
	private static final String SELECT_RESIDUOS = ", r.\"ID\" AS ResiduoId, r.\"FechaCreacion\", tr.\"ID\" AS TipoResiduoId," +
		"tr.\"Nombre\" AS TipoResiduoNombre";
	private static final String JOIN_TIPO = """
		LEFT JOIN "Residuo" AS r ON r."PuntoResiduoId" = pr."ID"
		LEFT JOIN "TipoResiduo" AS tr on tr."ID" = r."TipoResiduoId"
		""";
	private static final String JOIN_CIUDADANO = "JOIN \"Usuario\" AS u ON u.\"ID\" = c.\"UsuarioId\"\n";
	private static final String WHERE_AREA = """
		AND pr."Latitud" BETWEEN ? AND ?
		AND pr."Longitud" BETWEEN ? AND ?
		""";
	private static final String WHERE_RESIDUO = """
		AND r."RecorridoId" IS NULL
		AND r."TransaccionId" IS NULL
		AND r."FechaRetiro" IS NULL
		AND ( r."FechaLimiteRetiro" IS NULL OR r."FechaLimiteRetiro" > ? )
		""";
	private static final String WHERE_TIPO = """
		AND tr."Nombre" IN ( %s )
		""" + WHERE_RESIDUO;
	private static final String WHERE_IDS = """
		AND "CiudadanoId" = ?
		AND pr."ID" = ?
		""";

	@Inject
	public PuntoResiduoDao(DataSource ds) { super(ds); }

	public List<PuntoResiduo> list(PuntoResiduoFilter f, PuntoResiduoExpand x) throws PersistenceException {
		try ( val t = open(true); val select = createSelect(t, f, x); val rs = select.executeQuery() ) {
			val l = new ArrayList<PuntoResiduo>();
			while ( rs.next() ) {
				val u = new User();
				u.id = rs.getLong("UsuarioId");
				if ( x.ciudadano ) {
					u.username = rs.getString("Username");
					u.nombre = rs.getString("NombreApellido");
					u.email = rs.getString("Email");
					u.tipo = TipoUsuario.valueOf(rs.getString("TipoUsuario"));
				}

				l.add(new PuntoResiduo(
					rs.getLong("ID"),
					rs.getDouble("Latitud"),
					rs.getDouble("Longitud"),
					rs.getLong("CiudadanoId"),
					u,
					List.of() // TODO unimplemented expand on list
				));
			}

			return l;
		} catch ( SQLException e) {
			throw new PersistenceException("error getting PuntoResiduo", e);
		}
	}

	private PreparedStatement createSelect(
		Transaction t,
		PuntoResiduoFilter f,
		PuntoResiduoExpand x
	) throws PersistenceException, SQLException {
		final String select = x.ciudadano ? SELECT_SIMPLE + SELECT_CIUDADANO : SELECT_SIMPLE;

		val joinB = new StringBuilder();
		if ( f.hasTipo() ) joinB.append(JOIN_TIPO);
		if ( x.ciudadano ) joinB.append(JOIN_CIUDADANO);

		val join = joinB.toString();
		val sql = String.format(SELECT_FMT, select, join);

		val conditions = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();
		if ( f.hasArea() ) {
			conditions.append(WHERE_AREA);
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}

		if ( f.hasTipo() ) {
			val marks = f.tipoResiduos.stream()
				.map(tr -> "?")
				.collect(Collectors.joining(","));

			conditions.append(String.format(WHERE_TIPO, marks));
			parameters.addAll(f.tipoResiduos);
			parameters.add(Timestamp.from(ZonedDateTime.now().toInstant()));
		}

		val p = t.prepareStatement(conditions.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i+1, parameters.get(i));

		return p;
	}

	public Optional<PuntoResiduo> get(
		Long ciudadanoId,
		Long id,
		PuntoResiduoExpand x
	) throws PersistenceException {
		try (
			val t = open(true);
			val select = createIdSelect(t, ciudadanoId, id, x);
			val rs = select.executeQuery()
		) {
			if ( !rs.next() ) return Optional.empty();

			val u = new User();
			u.id = rs.getLong("UsuarioId");
			if ( x.ciudadano ) {
				u.username = rs.getString("Username");
				u.nombre = rs.getString("NombreApellido");
				u.email = rs.getString("Email");
				u.tipo = TipoUsuario.valueOf(rs.getString("TipoUsuario"));
			}

			val residuos = new ArrayList<Residuo>();
			val p = new PuntoResiduo(
				rs.getLong("ID"),
				rs.getDouble("Latitud"),
				rs.getDouble("Longitud"),
				rs.getLong("CiudadanoId"),
				u,
				residuos
			);

			if ( x.residuos ) {
				do {
					val r = Residuo.builder()
						.id(rs.getLong("ResiduoId"))
						.ciudadanoId(ciudadanoId)
						.fechaCreacion(rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC))
						.tipoResiduo(new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")))
						.puntoResiduo(new PuntoResiduo(p.id)) // para evitar recursividad dentro de residuo
						.build();
					residuos.add(r);
				} while ( rs.next() );
			}

			return Optional.of(p);
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting PuntoResiduo by ID", e);
		}
	}

	private PreparedStatement createIdSelect(
		Transaction t,
		Long ciudadanoId,
		Long id,
		PuntoResiduoExpand x
	) throws SQLException, PersistenceException {
		var select = SELECT_SIMPLE;
		if ( x.ciudadano ) select += SELECT_CIUDADANO;
		if ( x.residuos ) select += SELECT_RESIDUOS;

		var join = JOIN_TIPO;
		if ( x.ciudadano ) join += JOIN_CIUDADANO;

		val sql = String.format(SELECT_FMT, select, join) + WHERE_IDS + WHERE_RESIDUO;
		val p = t.prepareStatement(sql);
		p.setLong(1, ciudadanoId);
		p.setLong(2, id);
		p.setTimestamp(3, Timestamp.from(ZonedDateTime.now().toInstant()));

		return p;
	}
}
