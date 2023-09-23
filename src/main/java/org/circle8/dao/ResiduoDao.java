package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Transaccion;
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.ResiduoExpand;
import org.circle8.filter.ResiduosFilter;
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
import java.util.Optional;

public class ResiduoDao extends Dao {

	private static final String INSERT = """
			INSERT INTO "Residuo"(
			"FechaCreacion", "PuntoResiduoId", "TipoResiduoId", "Descripcion", "FechaLimiteRetiro", "Base64")
			VALUES (?, ?, ?, ?, ?, ?);
			  """;
	private static final String SELECT = """
		SELECT r."ID", "FechaCreacion", "FechaRetiro", "FechaLimiteRetiro", "Descripcion",
		       "TipoResiduoId", tr."Nombre" AS TipoResiduoNombre,
		       "PuntoResiduoId", pr."CiudadanoId",
		       "TransaccionId", "RecorridoId", "Base64"
		  FROM "Residuo" AS r
		  JOIN "PuntoResiduo" AS pr ON pr."ID" = r."PuntoResiduoId"
		  JOIN "TipoResiduo" AS tr ON tr."ID" = r."TipoResiduoId"
		 WHERE 1=1
		""";
	private static final String ORDER_BY = """
		ORDER BY r."FechaCreacion" DESC
		""";
	private static final String WHERE_ID = """
		AND r."ID" = ?
		""";
	private static final String WHERE_PUNTOS = """
		AND r."PuntoResiduoId" IN ( %s )
		""";
	private static final String WHERE_CIUDADANO = """
		AND pr."CiudadanoId" IN ( %s )
		""";
	private static final String WHERE_TIPOS = """
		AND r."TipoResiduoId" IN ( %s )
		""";
	private static final String WHERE_TRANSACCION = """
		AND r."TransaccionId" = ?
		""";
	private static final String WHERE_RECORRIDO = """
		AND r."RecorridoId" = ?
		""";
	private static final String WHERE_RETIRADO = """
		AND r."FechaRetiro" IS NOT NULL
		""";
	private static final String WHERE_NOT_RETIRADO = """
		AND r."FechaRetiro" IS NULL
		""";
	private static final String WHERE_FECHA_LIMITE = """
		AND ( r."FechaLimiteRetiro" >= ? OR r."FechaLimiteRetiro" IS NULL )
		""";
	private static final String UPDATE = """
		UPDATE "Residuo"
		   SET "FechaCreacion" = ?,
		       "FechaRetiro" = ?,
		       "PuntoResiduoId" = ?,
		       "TipoResiduoId" = ?,
		       "TransaccionId" = ?,
		       "RecorridoId" = ?,
		       "Descripcion" = ?,
		       "FechaLimiteRetiro" = ?,
		       "Base64" = ?
		 WHERE "ID" = ?
		""";

	private static final String DELETE = """
			DELETE FROM "Residuo"
			WHERE "ID" = ?
			""";

	@Inject
	public ResiduoDao(DataSource ds) {
		super(ds);
	}

	public Residuo save(Transaction t, Residuo residuo) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setTimestamp(1, Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant()));
			insert.setLong(2, residuo.puntoResiduo.id);
			insert.setLong(3, residuo.tipoResiduo.id);
			insert.setString(4, residuo.descripcion);
			insert.setTimestamp(5, residuo.fechaLimiteRetiro != null? Timestamp.from(residuo.fechaLimiteRetiro.toInstant()) : null);
			insert.setBytes(6, residuo.base64);

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
				throw new ForeignKeyException("No existe el tipo de residuo con id " + residuo.tipoResiduo.id, e);
			else if(e.getMessage().contains("Residuo_PuntoResiduoId_fkey"))
				throw new ForeignKeyException("No existe el punto residuo con id " + residuo.puntoResiduo.id, e);
			else
				throw new PersistenceException("error inserting residuo", e);
		}

		return residuo;
	}

	public Optional<Residuo> get(Transaction t, long residuoId, ResiduoExpand x) throws PersistenceException {
		try ( val ps = t.prepareStatement(SELECT + WHERE_ID) ) {
			ps.setLong(1, residuoId);

			try ( val rs = ps.executeQuery() ) {
				if ( !rs.next() ) return Optional.empty();
				return Optional.of(buildResiduo(rs, x));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error selecting residuo", e);
		}
	}

	@NotNull
	private static Residuo buildResiduo(ResultSet rs, ResiduoExpand x) throws SQLException {
		byte[] base64 = null;
		if ( x.base64 )
			base64 = rs.getBytes("Base64");

		return new Residuo(
			rs.getLong("ID"),
			rs.getLong("CiudadanoId"),
			rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC),
			Dates.atUTC(rs.getTimestamp("FechaRetiro")),
			Dates.atUTC(rs.getTimestamp("FechaLimiteRetiro")),
			rs.getString("Descripcion"),
			new PuntoResiduo(rs.getLong("PuntoResiduoId")),
			new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")),
			new Transaccion(rs.getLong("TransaccionId")),
			new Recorrido(rs.getLong("RecorridoId")),
			base64
		);
	}

	public void update(Transaction t, Residuo r) throws PersistenceException {
		try ( var ps = t.prepareStatement(UPDATE) ) {
			ps.setTimestamp(1, Timestamp.from(r.fechaCreacion.toInstant()));
			ps.setTimestamp(2, r.fechaRetiro != null ? Timestamp.from(r.fechaRetiro.toInstant()) : null);
			ps.setLong(3, r.puntoResiduo.id);
			ps.setLong(4, r.tipoResiduo.id);
			ps.setObject(5, r.transaccion != null && r.transaccion.id != 0 ? r.transaccion.id : null);
			ps.setObject(6, r.recorrido != null && r.recorrido.id != 0 ? r.recorrido.id : null);
			ps.setString(7, r.descripcion);
			ps.setTimestamp(8, r.fechaLimiteRetiro != null ? Timestamp.from(r.fechaLimiteRetiro.toInstant()) : null);
			ps.setBytes(9, r.base64);
			ps.setLong(10, r.id);

			int updates = ps.executeUpdate();
			if ( updates == 0 )
				throw new SQLException("Updating the residuo failed, no affected rows");
		} catch (SQLException e) {
			if ( e.getMessage().contains("Residuo_TipoResiduoId_fkey") )
				throw new ForeignKeyException("No existe el tipo de residuo con id " + r.tipoResiduo.id, e);
			else if(e.getMessage().contains("Residuo_PuntoResiduoId_fkey"))
				throw new ForeignKeyException("No existe el punto residuo con id " + r.puntoResiduo.id, e);
			else if(e.getMessage().contains("Residuo_RecorridoId_fkey"))
				throw new ForeignKeyException("No existe el recorrido con id " + r.recorrido.id, e);
			else if(e.getMessage().contains("Residuo_TransaccionId_fkey"))
				throw new ForeignKeyException("No existe la transaccion con id " + r.transaccion.id, e);
			else
				throw new PersistenceException("error updating residuo", e);
		}
	}


	public void delete(Transaction t, long id) throws PersistenceException {
		try ( val delete = t.prepareStatement(DELETE) ) {
			delete.setLong(1, id);

			if (delete.executeUpdate() <= 0 )
				throw new SQLException("deleting the residuo failed, no affected rows");

		} catch (SQLException e) {
			if ( e.getMessage().contains("Solicitud_ResiduoId_fkey") )
				throw new ForeignKeyException("El residuo se encuentra asociado a una solicitud");
			throw new PersistenceException("error deleting residuo", e);
		}
	}

	public List<Residuo> list(Transaction t, ResiduosFilter f, ResiduoExpand x) throws PersistenceException {
		try (val ps = createListSelect(t, f); val rs = ps.executeQuery()) {
			return buildList(rs, r -> buildResiduo(r, x));
		} catch (SQLException e) {
			throw new PersistenceException("error selecting residuos list", e);
		}
	}

	public List<Residuo> list(ResiduosFilter f, ResiduoExpand x) throws PersistenceException {
		try ( var t = open(true) ) {
			return list(t, f, x);
		}
	}

	private PreparedStatement createListSelect(
		Transaction t,
		ResiduosFilter f
	) throws SQLException, PersistenceException {
		val conditions = new StringBuilder(SELECT);
		List<Object> parameters = new ArrayList<>();

		appendListCondition(f.puntosResiduo, WHERE_PUNTOS, conditions, parameters);
		appendListCondition(f.ciudadanos, WHERE_CIUDADANO, conditions, parameters);
		appendListCondition(f.tipos, WHERE_TIPOS, conditions, parameters);
		appendCondition(f.transaccion, WHERE_TRANSACCION, conditions, parameters);
		appendCondition(f.recorrido, WHERE_RECORRIDO, conditions, parameters);
		appendCondition(f.fechaLimiteRetiro, WHERE_FECHA_LIMITE, conditions, parameters);

		if ( f.retirado != null ) {
			conditions.append(f.retirado ? WHERE_RETIRADO : WHERE_NOT_RETIRADO);
		}

		conditions.append(ORDER_BY);
		val p = t.prepareStatement(conditions.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i+1, parameters.get(i));

		return p;
	}
}
