package org.circle8.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.sql.DataSource;

import lombok.val;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Transaccion;
import org.circle8.exception.ForeingKeyException;
import org.circle8.exception.PersistenceException;
import org.circle8.utils.Dates;

import com.google.inject.Inject;

public class ResiduoDao extends Dao {

	private static final String INSERT = """
			INSERT INTO "Residuo"(
			"FechaCreacion", "PuntoResiduoId", "TipoResiduoId", "Descripcion", "FechaLimiteRetiro")
			VALUES (?, ?, ?, ?, ?);
			  """;
	private static final String SELECT = """
		SELECT r."ID", "FechaCreacion", "FechaRetiro", "FechaLimiteRetiro", "Descripcion",
		       "TipoResiduoId", tr."Nombre" AS TipoResiduoNombre,
		       "PuntoResiduoId", pr."CiudadanoId",
		       "TransaccionId"
		  FROM "Residuo" AS r
		  JOIN "PuntoResiduo" AS pr ON pr."ID" = r."PuntoResiduoId"
		  JOIN "TipoResiduo" AS tr ON tr."ID" = r."TipoResiduoId"
		 WHERE r."ID" = ?
		""";

	@Inject
	public ResiduoDao(DataSource ds) {
		super(ds);
	}

	public Residuo save(Transaction t,Residuo residuo) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setTimestamp(1, Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant()));
			insert.setLong(2, residuo.puntoResiduo.id);
			insert.setLong(3, residuo.tipoResiduo.id);
			insert.setString(4, residuo.descripcion);
			insert.setTimestamp(5, residuo.fechaLimiteRetiro != null? Timestamp.from(residuo.fechaLimiteRetiro.toInstant()) : null);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the residuo failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next())
					residuo.id = rs.getLong(1);
				else
					throw new SQLException("Creating the residuo failed, no ID obtained");
			}
		} catch (SQLException e) {
			if ( e.getMessage().contains("Residuo_TipoResiduoId_fkey") )
				throw new ForeingKeyException("No existe el tipo de residuo con id " + residuo.tipoResiduo.id, e);
			else if(e.getMessage().contains("Residuo_PuntoResiduoId_fkey"))
				throw new ForeingKeyException("No existe el punto residuo con id " + residuo.puntoResiduo.id, e);
			else
				throw new PersistenceException("error inserting residuo", e);
		}

		return residuo;
	}

	public Optional<Residuo> get(Transaction t, long residuoId) throws PersistenceException {
		try ( val ps = t.prepareStatement(SELECT) ) {
			ps.setLong(1, residuoId);

			try ( val rs = ps.executeQuery() ) {
				if ( !rs.next() ) return Optional.empty();

				val retiroTimestamp = rs.getTimestamp("FechaRetiro");
				val limiteTimestamp = rs.getTimestamp("FechaLimiteRetiro");
				return Optional.of(new Residuo(
					rs.getLong("ID"),
					rs.getLong("CiudadanoId"),
					rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC),
					retiroTimestamp != null ? retiroTimestamp.toInstant().atZone(Dates.UTC) : null,
					limiteTimestamp != null ? limiteTimestamp.toInstant().atZone(Dates.UTC) : null,
					rs.getString("Descripcion"),
					new PuntoResiduo(rs.getLong("PuntoResiduoId")),
					new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")),
					new Transaccion(rs.getLong("TransaccionId"))
				));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error selecting residuo", e);
		}
	}
}
