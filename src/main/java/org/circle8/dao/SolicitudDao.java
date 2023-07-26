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
import org.circle8.entity.Residuo;
import org.circle8.entity.Solicitud;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.SolicitudFilter;
import org.circle8.service.SolicitudService;
import org.circle8.utils.Dates;

import com.google.inject.Inject;

import lombok.val;

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

	private static final String SELECT = """
			SELECT s."ID", s."FechaCreacion", "FechaModificacion", "Estado",
			       "CiudadanoSolicitanteId", c1."UsuarioId" as SolicitanteUsuarioId,
			       "CiudadanoSolicitadoId", c2."UsuarioId" as SolicitadoUsuarioId,
			       "ResiduoId", "CiudadanoCancelaId", res."FechaLimiteRetiro"
			  FROM "Solicitud" AS s
			  JOIN "Ciudadano" AS c1 ON c1."ID" = s."CiudadanoSolicitanteId"
			  JOIN "Ciudadano" AS c2 ON c2."ID" = s."CiudadanoSolicitadoId"
			  JOIN "Residuo" AS res ON res."ID" = s."ResiduoId"
			 WHERE 1=1
			""";
	
	private static final String WHERE_SOLICITANTE = """
			AND s."CiudadanoSolicitanteId" = ?
		""";
	
	private static final String WHERE_SOLICITADO = """
			AND s."CiudadanoSolicitadoId" = ?
		""";
	
	private static final String SELECT_GET = """
		SELECT s."ID", s."FechaCreacion", "FechaModificacion", "Estado",
		       "CiudadanoSolicitanteId", c1."UsuarioId" as SolicitanteUsuarioId,
		       "CiudadanoSolicitadoId", c2."UsuarioId" as SolicitadoUsuarioId,
		       "ResiduoId", "CiudadanoCancelaId", res."FechaLimiteRetiro"
		  FROM "Solicitud" AS s
		  JOIN "Ciudadano" AS c1 ON c1."ID" = s."CiudadanoSolicitanteId"
		  JOIN "Ciudadano" AS c2 ON c2."ID" = s."CiudadanoSolicitadoId"
		  JOIN "Residuo" AS res ON res."ID" = s."ResiduoId"
		 WHERE s."ID" = ?
		""";
	
	private static final String PUT = """
			UPDATE "Solicitud"
			SET "FechaModificacion"=?, "Estado"=?
			WHERE "ID"=?;
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
	
	public List<Solicitud> list(SolicitudFilter filter) throws PersistenceException{
		try (var t = open(true); var select = createSelectForList(t,filter);var rs = select.executeQuery()) {
			val l = new ArrayList<Solicitud>();
			while ( rs.next() ) {
				l.add(new Solicitud(
						rs.getLong("ID"),
						rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC),
						rs.getTimestamp("FechaModificacion").toInstant().atZone(Dates.UTC),
						getEstado(rs),
						new Ciudadano(rs.getLong("CiudadanoSolicitanteId"), rs.getLong("SolicitanteUsuarioId")),
						new Ciudadano(rs.getLong("CiudadanoSolicitadoId"), rs.getLong("SolicitadoUsuarioId")),
						Residuo.builder().id(rs.getLong("ResiduoId")).build(),
						rs.getLong("CiudadanoCancelaId")
					));
			}

			return l;
		} catch (SQLException e) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}
	}

	public Optional<Solicitud> get(Transaction t, long id) throws PersistenceException {
		try ( val select = t.prepareStatement(SELECT_GET) ) {
			select.setLong(1, id);

			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();
				

				return Optional.of(new Solicitud(
					rs.getLong("ID"),
					rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC),
					rs.getTimestamp("FechaModificacion").toInstant().atZone(Dates.UTC),
					getEstado(rs),
					new Ciudadano(rs.getLong("CiudadanoSolicitanteId"), rs.getLong("SolicitanteUsuarioId")),
					new Ciudadano(rs.getLong("CiudadanoSolicitadoId"), rs.getLong("SolicitadoUsuarioId")),
					Residuo.builder().id(rs.getLong("ResiduoId")).build(),
					rs.getLong("CiudadanoCancelaId")
				));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting solicitud", e);
		}
	}
	
	public void put(Transaction t,Long id,EstadoSolicitud estado) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(PUT) ) {
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
	
	private PreparedStatement createSelectForList(Transaction t,SolicitudFilter f) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT);
		List<Object> parameters = new ArrayList<>();
		
		if(f.hasSolicitante()) {
			b.append(WHERE_SOLICITANTE);
			parameters.add(f.solicitanteId);
		}
		
		if(f.hasSolicitado()) {
			b.append(WHERE_SOLICITADO);
			parameters.add(f.solicitadoId);
		}	
		
		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}
	
	/**
	 * Devuelve el estado de la solicitud
	 * valida que la fecha de limite de retiro 
	 * del residuo sea menor a la fecha actual.
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private EstadoSolicitud getEstado(ResultSet rs) throws SQLException {
		val estado = EstadoSolicitud.valueOf(rs.getString("Estado"));
		val fechaLimite = rs.getTimestamp("FechaLimiteRetiro");
		if(estado.equals(EstadoSolicitud.PENDIENTE) && fechaLimite != null &&
				fechaLimite.toInstant().atZone(Dates.UTC).isBefore(ZonedDateTime.now(Dates.UTC))) {
			return EstadoSolicitud.EXPIRADA;
		}
		return estado;
	}
}
