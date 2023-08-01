package org.circle8.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Ciudadano;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.Solicitud;
import org.circle8.entity.TipoResiduo;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.SolicitudExpand;
import org.circle8.filter.SolicitudFilter;
import org.circle8.service.SolicitudService;
import org.circle8.utils.Dates;

import com.google.inject.Inject;

import lombok.val;
import org.jetbrains.annotations.NotNull;

public class SolicitudDao extends Dao {
	private static final String INSERT_INTO_FMT = """
		INSERT INTO public."Solicitud"(
		    "FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId"
		) SELECT ?, -- Fecha creacion
		         ?, -- Fecha modificacion = fecha creacion
		         ?, -- estado PENDIENTE
		         %s,
		         %s,
		         ?
		""";

	private static final String CIUDADANO_PUNTO_RECICLAJE_FIELD = """
		(SELECT "CiudadanoId" FROM "PuntoReciclaje" WHERE "ID" = ?)
		""";

	private static final String CIUDADANO_RESIDUO_FIELD = """
		(SELECT "CiudadanoId" FROM "Residuo" r JOIN "PuntoResiduo" pr ON r."PuntoResiduoId" = pr."ID" WHERE r."ID" = ?)
		""";

	private static final String WHERE_SOLICITANTE = """
		AND s."CiudadanoSolicitanteId" = ?
		""";

	private static final String WHERE_SOLICITADO = """
		AND s."CiudadanoSolicitadoId" = ?
		""";

	private static final String WHERE_ID = """
		AND s."ID" = ?
		""";

	private static final String SELECT_FMT = """
		SELECT
		       %s
		  FROM "Solicitud" AS s
		  JOIN "Ciudadano" AS c1 ON c1."ID" = s."CiudadanoSolicitanteId"
		  JOIN "Ciudadano" AS c2 ON c2."ID" = s."CiudadanoSolicitadoId"
		  JOIN "Residuo" AS r ON r."ID" = s."ResiduoId"
		    %s
		 WHERE 1 = 1
		""";
	private static final String SELECT_SIMPLE = """
		s."ID", s."FechaCreacion", "FechaModificacion", "Estado",
		"CiudadanoSolicitanteId", c1."UsuarioId" as SolicitanteUsuarioId,
		"CiudadanoSolicitadoId", c2."UsuarioId" as SolicitadoUsuarioId,
		"ResiduoId", "CiudadanoCancelaId", r."FechaLimiteRetiro"
		""";
	private static final String SELECT_X_RESIDUOS = """
		, r."FechaCreacion" AS ResiduoFechaCreacion, r."FechaLimiteRetiro", r."Descripcion",
		  pr."CiudadanoId" AS ResiduoCiudadanoId, r."PuntoResiduoId",
		  tr."ID" AS TipoResiduoId, tr."Nombre" AS TipoResiduoNombre
		""";
	private static final String SELECT_X_CIUDADANOS = """
		, u1."Username" AS SolicitanteUsername, u1."NombreApellido" AS SolicitanteNombre
		, u2."Username" AS SolicitadoUsername, u2."NombreApellido" AS SolicitadoNombre
		""";
	private static final String JOIN_X_RESIDUO = """
		JOIN "TipoResiduo" AS tr on tr."ID" = r."TipoResiduoId"
		JOIN "PuntoResiduo" AS pr on pr."ID" = r."PuntoResiduoId"
		""";
	private static final String JOIN_X_CIUDADANO = """
		JOIN "Usuario" AS u1 ON u1."ID" = c1."UsuarioId"
		JOIN "Usuario" AS u2 ON u2."ID" = c2."UsuarioId"
		""";

	private static final String PUT_APROBAR = """
		UPDATE "Solicitud"
		SET "FechaModificacion" = ?, "Estado" = ?
		WHERE "ID"=?
		""";

	private static final String PUT_CANCELAR = """
		UPDATE "Solicitud"
		SET "FechaModificacion" = ?, "Estado" = ?, "CiudadanoCancelaId" = ?
		WHERE "ID"=?
		""";

	@Inject
	public SolicitudDao(DataSource ds) { super(ds); }

	public Long save(
		Transaction t,
		long residuoId,
		long puntoReciclajeId,
		SolicitudService.TipoSolicitud tipo
	) throws PersistenceException {
		try ( val insert = createInsert(t, residuoId, puntoReciclajeId, tipo) ) {
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the solicitud failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( !rs.next() )
					throw new SQLException("Creating the solicitud failed, no ID obtained");

				return rs.getLong(1);
			}
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Solicitud_Estado_CiudadanoSolicitanteId_CiudadanoSolicitado_key") )
				throw new DuplicatedEntry("solicitud already saved", e);

			throw new PersistenceException("error inserting solicitud", e);
		}
	}

	private PreparedStatement createInsert(
		Transaction t,
		long residuoId,
		long puntoReciclajeId,
		SolicitudService.TipoSolicitud tipo
	) throws PersistenceException, SQLException {
		val insert = String.format(
			INSERT_INTO_FMT,
			tipo.isRetiro() ? CIUDADANO_PUNTO_RECICLAJE_FIELD : CIUDADANO_RESIDUO_FIELD,
			tipo.isRetiro() ? CIUDADANO_RESIDUO_FIELD : CIUDADANO_PUNTO_RECICLAJE_FIELD
		);

		val ps = t.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
		val now = Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant());
		ps.setTimestamp(1, now);
		ps.setTimestamp(2, now);
		ps.setString(3, EstadoSolicitud.PENDIENTE.name());
		ps.setLong(4, tipo.isRetiro() ? puntoReciclajeId : residuoId);
		ps.setLong(5, tipo.isRetiro() ? residuoId : puntoReciclajeId);
		ps.setLong(6, residuoId);

		return ps;
	}

	public List<Solicitud> list(SolicitudFilter f, SolicitudExpand x) throws PersistenceException{
		try (
			var t = open(true);
			var select = createSelect(t, f, x);
			var rs = select.executeQuery()
		) {
			val l = new ArrayList<Solicitud>();
			while ( rs.next() ) {
				l.add(buildSolicitud(rs, x));
			}

			return l;
		} catch (SQLException e) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}
	}

	public Optional<Solicitud> get(Transaction t, long id, SolicitudExpand x) throws PersistenceException {
		val f = SolicitudFilter.builder().id(id).build();
		try ( val select = createSelect(t, f, x) ) {
			select.setLong(1, id);

			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();

				return Optional.of(buildSolicitud(rs, x));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting solicitud", e);
		}
	}

	@NotNull
	private Solicitud buildSolicitud(ResultSet rs, SolicitudExpand x) throws SQLException {
		val r = buildResiduo(rs, x.residuo);
		val solicitante = buildSolicitante(rs, x.ciudadanos);
		val solicitado = buildSolicitado(rs, x.ciudadanos);

		return new Solicitud(
			rs.getLong("ID"),
			rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC),
			rs.getTimestamp("FechaModificacion").toInstant().atZone(Dates.UTC),
			getEstado(rs),
			solicitante,
			solicitado,
			r,
			rs.getLong("CiudadanoCancelaId")
		);
	}

	private Residuo buildResiduo(ResultSet rs, boolean expand) throws SQLException {
		if ( !expand ) return Residuo.builder().id(rs.getLong("ResiduoId")).build();

		// TODO: ver si es posible evitar duplicado con PuntoResiduoDao
		val limit = rs.getTimestamp("FechaLimiteRetiro");
		val limitDate = limit != null ? limit.toInstant().atZone(Dates.UTC) : null;
		return Residuo.builder()
			.id(rs.getLong("ResiduoId"))
			.ciudadanoId(rs.getLong("ResiduoCiudadanoId"))
			.fechaCreacion(rs.getTimestamp("ResiduoFechaCreacion").toInstant().atZone(Dates.UTC))
			.fechaLimiteRetiro(limitDate)
			.tipoResiduo(new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")))
			.puntoResiduo(new PuntoResiduo(rs.getLong("PuntoResiduoId"), rs.getLong("ResiduoCiudadanoId"))) // para evitar recursividad dentro de residuo
			.descripcion(rs.getString("Descripcion"))
			.build();
	}

	private Ciudadano buildSolicitante(ResultSet rs, boolean expand) throws SQLException {
		if ( !expand )
			return new Ciudadano(rs.getLong("CiudadanoSolicitanteId"), rs.getLong("SolicitanteUsuarioId"));

		return new Ciudadano(
			rs.getLong("CiudadanoSolicitanteId"),
			rs.getString("SolicitanteUsername"),
			rs.getString("SolicitanteNombre"),
			rs.getLong("SolicitanteUsuarioId")
		);
	}

	private Ciudadano buildSolicitado(ResultSet rs, boolean expand) throws SQLException {
		if ( !expand )
			return new Ciudadano(rs.getLong("CiudadanoSolicitadoId"), rs.getLong("SolicitadoUsuarioId"));

		return new Ciudadano(
			rs.getLong("CiudadanoSolicitadoId"),
			rs.getString("SolicitadoUsername"),
			rs.getString("SolicitadoNombre"),
			rs.getLong("SolicitadoUsuarioId")
		);
	}

	public void aprobar(Transaction t,Long id,EstadoSolicitud estado) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(PUT_APROBAR) ) {
			put.setTimestamp(1, Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant()));
			put.setString(2, estado.name());
			put.setLong(3, id);
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe la solicitud con id "+id);
		} catch (SQLException e) {
			throw new PersistenceException("error updating solicitud", e);
		}
	}

	public void cancelar(Transaction t,Long id,Long ciudadanoID,EstadoSolicitud estado) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(PUT_CANCELAR) ) {
			put.setTimestamp(1, Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant()));
			put.setString(2, estado.name());
			put.setLong(3, ciudadanoID);
			put.setLong(4, id);
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe la solicitud con id "+id);
		} catch (SQLException e) {
			throw new PersistenceException("error updating solicitud", e);
		}
	}

	private PreparedStatement createSelect(
		Transaction t,
		SolicitudFilter f,
		SolicitudExpand x
	) throws PersistenceException, SQLException {
		var selectFields = SELECT_SIMPLE;
		if ( x.residuo ) selectFields += SELECT_X_RESIDUOS;
		if ( x.ciudadanos ) selectFields += SELECT_X_CIUDADANOS;

		var joinFields = "";
		if ( x.residuo ) joinFields += JOIN_X_RESIDUO;
		if ( x.ciudadanos ) joinFields += JOIN_X_CIUDADANO;

		var sql = String.format(SELECT_FMT, selectFields, joinFields);
		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();

		if ( f.id != null ) {
			b.append(WHERE_ID);
			parameters.add(f.id);
		}

		if ( f.solicitanteId != null ) {
			b.append(WHERE_SOLICITANTE);
			parameters.add(f.solicitanteId);
		}

		if ( f.solicitadoId != null ) {
			b.append(WHERE_SOLICITADO);
			parameters.add(f.solicitadoId);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

	/**
	 * Devuelve el estado de la solicitud.
	 * Valida que la fecha de limite de retiro del residuo sea menor a la fecha actual.
	 */
	private EstadoSolicitud getEstado(ResultSet rs) throws SQLException {
		val estado = EstadoSolicitud.valueOf(rs.getString("Estado"));
		val fechaLimite = rs.getTimestamp("FechaLimiteRetiro");

		if ( EstadoSolicitud.PENDIENTE.equals(estado)
			&& fechaLimite != null
			&& fechaLimite.toInstant().atZone(Dates.UTC).isBefore(ZonedDateTime.now(Dates.UTC))
		) {
			return EstadoSolicitud.EXPIRADA;
		}

		return estado;
	}
}
