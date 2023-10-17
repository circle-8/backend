package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dto.Dia;
import org.circle8.entity.Ciudadano;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.Solicitud;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.SolicitudExpand;
import org.circle8.filter.SolicitudFilter;
import org.circle8.service.SolicitudService;
import org.circle8.utils.Dates;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SolicitudDao extends Dao {
	private static final String INSERT_INTO_FMT = """
		INSERT INTO public."Solicitud"(
		    "FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId", "PuntoReciclajeId"
		) SELECT ?, -- Fecha creacion
		         ?, -- Fecha modificacion = fecha creacion
		         ?, -- estado PENDIENTE
		         %s,
		         %s,
		         ?,
		         ?
		""";

	private static final String CIUDADANO_PUNTO_RECICLAJE_FIELD = """
		(SELECT "CiudadanoId" FROM "PuntoReciclaje" WHERE "ID" = ?)
		""";

	private static final String CIUDADANO_RESIDUO_FIELD = """
		(SELECT "CiudadanoId" FROM "Residuo" r JOIN "PuntoResiduo" sub_pr ON r."PuntoResiduoId" = sub_pr."ID" WHERE r."ID" = ?)
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

	private static final String WHERE_RESIDUO_ID = """
		AND s."ResiduoId" = ?
		""";
	private static final String WHERE_PUNTO_RECICLAJE_ID = """
		AND s."PuntoReciclajeId" = ?
		""";

	private static final String WHERE_NOT_ESTADOS = """
		AND s."Estado" NOT IN ( %S )
		""";
	private static final String WHERE_ESTADOS = """
		AND s."Estado" IN ( %S )
		""";

	private static final String SELECT_FMT = """
		SELECT
		       %s
		  FROM "Solicitud" AS s
		  JOIN "Ciudadano" AS c1 ON c1."ID" = s."CiudadanoSolicitanteId"
		  JOIN "Ciudadano" AS c2 ON c2."ID" = s."CiudadanoSolicitadoId"
		  JOIN "Residuo" AS r ON r."ID" = s."ResiduoId"
		  JOIN "PuntoReciclaje" AS prc ON prc."ID" = s."PuntoReciclajeId"
		    %s
		 WHERE 1 = 1
		   AND s."TransaccionId" IS NULL
		""";
	private static final String SELECT_SIMPLE = """
		s."ID", s."FechaCreacion", "FechaModificacion", "Estado",
		"CiudadanoSolicitanteId", c1."UsuarioId" as SolicitanteUsuarioId,
		"CiudadanoSolicitadoId", c2."UsuarioId" as SolicitadoUsuarioId,
		"ResiduoId", "CiudadanoCancelaId", r."FechaLimiteRetiro",
		s."PuntoReciclajeId", prc."CiudadanoId" AS "PuntoReciclajeCiudadanoId"
		""";
	private static final String SELECT_X_RESIDUOS = """
		, r."FechaCreacion" AS ResiduoFechaCreacion, r."FechaLimiteRetiro", r."Descripcion",
		  pr."CiudadanoId" AS ResiduoCiudadanoId, r."PuntoResiduoId",
		  tr."ID" AS TipoResiduoId, tr."Nombre" AS TipoResiduoNombre, r."Base64"
		""";
	private static final String SELECT_X_CIUDADANOS = """
		, u1."Username" AS SolicitanteUsername, u1."NombreApellido" AS SolicitanteNombre
		, u2."Username" AS SolicitadoUsername, u2."NombreApellido" AS SolicitadoNombre
		""";
	private static final String SELECT_X_PUNTO_RECICLAJE = """
		, prc."Titulo", prc."Latitud", prc."Longitud", prc."DiasAbierto"
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

	private static final String UPDATE_TRANSACCION = """
		UPDATE "Solicitud"
		SET "TransaccionId" = ?
		WHERE "ID" = ?
		""";
	
	private static final String DELETE = """
			DELETE FROM "Solicitud" 
			WHERE "TransaccionId"=? AND "ResiduoId"=?;
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
		ps.setLong(7, puntoReciclajeId);

		return ps;
	}

	public List<Solicitud> list(Transaction t, SolicitudFilter f, SolicitudExpand x) throws PersistenceException {
		try (
			var select = createSelect(t, f, x);
			var rs = select.executeQuery()
		) {
			val l = new ArrayList<Solicitud>();
			while ( rs.next() ) {
				l.add(buildSolicitud(rs, x));
			}

			return l;
		} catch (SQLException e) {
			throw new PersistenceException("error getting solicitudes", e);
		}
	}

	public List<Solicitud> list(SolicitudFilter f, SolicitudExpand x) throws PersistenceException {
		try ( var t = open(true) ) {
			return list(t, f, x);
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
		val r = buildResiduo(rs, x.residuo, x.residuoBase64);
		val solicitante = buildSolicitante(rs, x.ciudadanos);
		val solicitado = buildSolicitado(rs, x.ciudadanos);
		val users = Map.of(solicitado.id, solicitado.usuarioId, solicitante.id, solicitante.usuarioId);
		val puntoReciclaje = buildPuntoReciclaje(rs, x.puntoReciclaje, users);

		return new Solicitud(
			rs.getLong("ID"),
			rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC),
			rs.getTimestamp("FechaModificacion").toInstant().atZone(Dates.UTC),
			getEstado(rs),
			solicitante,
			solicitado,
			r,
			rs.getLong("CiudadanoCancelaId"),
			puntoReciclaje
		);
	}

	private Residuo buildResiduo(ResultSet rs, boolean expand, boolean expandBase64) throws SQLException {
		if ( !expand ) return Residuo.builder().id(rs.getLong("ResiduoId")).build();

		byte[] base64 = null;
		if ( expandBase64 )
			base64 = rs.getBytes("Base64");


		// TODO: ver si es posible evitar duplicado con PuntoResiduoDao
		val limit = rs.getTimestamp("FechaLimiteRetiro");
		val limitDate = limit != null ? limit.toInstant().atZone(Dates.UTC) : null;
		return Residuo.builder()
			.id(rs.getLong("ResiduoId"))
			.ciudadano(Ciudadano.builder().id(rs.getLong("ResiduoCiudadanoId")).build())
			.fechaCreacion(rs.getTimestamp("ResiduoFechaCreacion").toInstant().atZone(Dates.UTC))
			.fechaLimiteRetiro(limitDate)
			.tipoResiduo(new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")))
			.puntoResiduo(new PuntoResiduo(rs.getLong("PuntoResiduoId"), rs.getLong("ResiduoCiudadanoId"))) // para evitar recursividad dentro de residuo
			.descripcion(rs.getString("Descripcion"))
			.base64(base64)
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

	private PuntoReciclaje buildPuntoReciclaje(
		ResultSet rs,
		boolean expand,
		Map<Long, Long> users
	) throws SQLException {
		val ciudadanoId = rs.getLong("PuntoReciclajeCiudadanoId");
		if ( !expand ) return new PuntoReciclaje(rs.getLong("PuntoReciclajeId"), ciudadanoId);

		val usuarioId = users.get(ciudadanoId);
		return new PuntoReciclaje(
			rs.getLong("PuntoReciclajeId"),
			rs.getString("Titulo"),
			rs.getDouble("Latitud"),
			rs.getDouble("Longitud"),
			Dia.getDia(rs.getString("DiasAbierto")),
			List.of(),
			ciudadanoId,
			User.builder().id(usuarioId).build(),
			""
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
	
	public void deleteByResiduo(Transaction t, Long transaccionId, Long residuoId) throws PersistenceException, NotFoundException {
		try ( var delete = t.prepareStatement(DELETE) ) {
			delete.setLong(1, transaccionId);
			delete.setLong(2, residuoId);
			int deleted = delete.executeUpdate();
			if ( deleted == 0 )
				throw new NotFoundException("No existe la solicitud con transaccionId "+transaccionId+" y residuoId"+residuoId);
		} catch (SQLException e) {
			throw new PersistenceException("error deleting solicitud", e);
		}
	}

	// TODO: se puede hacer que aprobar, cancelar, y set transaccion sean uno solo, y recibir una especie de SolicitudUpdate
	public void setTransaccion(Transaction t, Long id, Long transaccionId) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(UPDATE_TRANSACCION) ) {
			put.setLong(1, transaccionId);
			put.setLong(2, id);
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
		if ( x.puntoReciclaje ) selectFields += SELECT_X_PUNTO_RECICLAJE;

		var joinFields = "";
		if ( x.residuo ) joinFields += JOIN_X_RESIDUO;
		if ( x.ciudadanos ) joinFields += JOIN_X_CIUDADANO;

		var sql = String.format(SELECT_FMT, selectFields, joinFields);

		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();

		appendCondition(f.id, WHERE_ID, b, parameters);
		appendCondition(f.solicitanteId, WHERE_SOLICITANTE, b, parameters);
		appendCondition(f.solicitadoId, WHERE_SOLICITADO, b, parameters);
		appendCondition(f.residuoId, WHERE_RESIDUO_ID, b, parameters);
		appendCondition(f.puntoReciclajeId, WHERE_PUNTO_RECICLAJE_ID, b, parameters);
		appendListCondition(f.notEstados, WHERE_NOT_ESTADOS, b, parameters);
		appendListCondition(f.estados, WHERE_ESTADOS, b, parameters);

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
