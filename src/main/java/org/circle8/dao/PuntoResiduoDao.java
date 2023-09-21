package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dto.TipoUsuario;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.PuntoResiduoExpand;
import org.circle8.filter.PuntoResiduoFilter;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PuntoResiduoDao extends Dao {
	private static final String SELECT_FMT = """
		SELECT DISTINCT %s
		  FROM "PuntoResiduo" AS pr
		  JOIN "Ciudadano" AS c ON c."ID" = pr."CiudadanoId"
		    %s
		 WHERE 1=1
		""";

	private static final String SELECT_SIMPLE = """
			pr."ID", "Latitud", "Longitud", "CiudadanoId", c."UsuarioId"
			""";

	private static final String SELECT_CIUDADANO = """
			, u."Username", u."NombreApellido", u."Email", u."TipoUsuario"
			""" ;

	private static final String SELECT_RESIDUOS = """
			, r."ID" AS ResiduoId, r."FechaCreacion", r."FechaLimiteRetiro", r."Descripcion", tr."ID" AS TipoResiduoId,	tr."Nombre" AS TipoResiduoNombre
			""";

	private static final String JOIN_TIPO = """
		LEFT JOIN "Residuo" AS r ON r."PuntoResiduoId" = pr."ID"
		LEFT JOIN "TipoResiduo" AS tr on tr."ID" = r."TipoResiduoId"
		""";

	private static final String JOIN_CIUDADANO = """
			JOIN "Usuario" AS u ON u."ID" = c."UsuarioId"
			""";

	private static final String WHERE_ID = """		
			AND pr."ID" = ?
			""";

	private static final String WHERE_CIUDADANO = """
		AND pr."CiudadanoId" = ?
		""";

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

	private static final String INSERT = """
			INSERT INTO public."PuntoResiduo"(
			"CiudadanoId", "Latitud", "Longitud")
			VALUES (?, ?, ?);
			  """;
	private static final String PUT = """
			UPDATE public."PuntoResiduo"
			SET "Latitud"=?, "Longitud"=?
			WHERE "ID"=? AND "CiudadanoId"=?;
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

		if ( f.ciudadanoId != null ) {
			conditions.append(WHERE_CIUDADANO);
			parameters.add(f.ciudadanoId);
		}

		if ( f.hasTipo() ) {
			appendListCondition(f.tipoResiduos, WHERE_TIPO, conditions, parameters);
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
					val residuoId = rs.getLong("ResiduoId");
					if ( residuoId == 0 ) break;

					val limit = rs.getTimestamp("FechaLimiteRetiro");
					val limitDate = limit != null ? limit.toInstant().atZone(Dates.UTC) : null;
					val fechaTimestamp = rs.getTimestamp("FechaCreacion");
					val r = Residuo.builder()
						.id(rs.getLong("ResiduoId"))
						.ciudadanoId(ciudadanoId)
						.fechaCreacion(fechaTimestamp != null ? fechaTimestamp.toInstant().atZone(Dates.UTC) : null)
						.fechaLimiteRetiro(limitDate)
						.tipoResiduo(new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")))
						.puntoResiduo(new PuntoResiduo(p.id)) // para evitar recursividad dentro de residuo
						.descripcion(rs.getString("Descripcion"))
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

		val sql = String.format(SELECT_FMT, select, join);
		var conditions = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();
		
		appendCondition(id, WHERE_ID, conditions, parameters);
		appendCondition(ciudadanoId, WHERE_CIUDADANO, conditions, parameters);
		
		if ( x.residuos ) {
			conditions.append(WHERE_RESIDUO);
			parameters.add(Timestamp.from(ZonedDateTime.now().toInstant()));
		}	
		
		var p = t.prepareStatement(conditions.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		return p;
	}

	public PuntoResiduo save(Transaction t,PuntoResiduo punto) throws PersistenceException, NotFoundException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, punto.ciudadanoId);
			insert.setDouble(2, punto.latitud);
			insert.setDouble(3, punto.longitud);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the punto residuo failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next())
					punto.id = rs.getLong(1);
				else
					throw new SQLException("Creating the residuo failed, no ID obtained");
			}
		} catch (SQLException e) {
			if ( e.getMessage().contains("PuntoResiduo_CiudadanoId_fkey") )
				throw new NotFoundException("No existe el punto de residuo con ciudadano_id " + punto.ciudadanoId);
			else
				throw new PersistenceException("error inserting residuo", e);
		}
		return punto;
	}

	public PuntoResiduo put(Transaction t,PuntoResiduo punto) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(PUT) ) {
			put.setDouble(1, punto.latitud);
			put.setDouble(2, punto.longitud);
			put.setLong(3, punto.id);
			put.setLong(4, punto.ciudadanoId);
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe el punto de residuo con id "
						+ punto.id + " o ciudadano_id " + punto.ciudadanoId);
		} catch (SQLException e) {
			throw new PersistenceException("error inserting residuo", e);
		}

		return punto;
	}
}
