package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.entity.Ciudadano;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.entity.Residuo;
import org.circle8.entity.Solicitud;
import org.circle8.entity.Transportista;
import org.circle8.exception.PersistenceException;
import org.circle8.service.SolicitudService;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
		       "ResiduoId", "CiudadanoCancelaId"
		  FROM "Solicitud" AS s
		  JOIN "Ciudadano" AS c1 ON c1."ID" = s."CiudadanoSolicitanteId"
		  JOIN "Ciudadano" AS c2 ON c2."ID" = s."CiudadanoSolicitadoId"
		 WHERE s."ID" = ?
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
			// TODO: handle el caso de solicitud duplicada: Solicitud_Estado_CiudadanoSolicitanteId_CiudadanoSolicitado_key
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

	public Optional<Solicitud> get(Transaction t, long id) throws PersistenceException {
		try ( val select = t.prepareStatement(SELECT) ) {
			select.setLong(1, id);

			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();

				return Optional.of(new Solicitud(
					rs.getLong("ID"),
					rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC),
					rs.getTimestamp("FechaModificacion").toInstant().atZone(Dates.UTC),
					EstadoSolicitud.valueOf(rs.getString("Estado")),
					new Ciudadano(rs.getLong("CiudadanoSolicitanteId"), rs.getLong("SolicitanteUsuarioId")),
					new Ciudadano(rs.getLong("CiudadanoSolicitadoId"), rs.getLong("SolicitadoUsuarioId")),
					Residuo.builder().id(rs.getLong("ResiduoId")).build(),
					rs.getLong("CiudadanoCancelaId")
				));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting user", e);
		}
	}
}
