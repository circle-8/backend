package org.circle8.dao;

import com.google.inject.Inject;
import lombok.SneakyThrows;
import lombok.val;

import org.circle8.entity.Ciudadano;
import org.circle8.entity.Punto;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Residuo;
import org.circle8.entity.Retiro;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Zona;
import org.circle8.enums.RecorridoEnum;
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.RecorridoFilter;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RecorridoDao extends Dao {
	private static final String SELECT_FMT = """
		SELECT %s
		  FROM "Recorrido" r
		  %s
		 WHERE 1=1
		  %s
		 ORDER BY r."ID"
		""";
	private static final String SELECT_SIMPLE = """
		r."ID", r."FechaRetiro", "FechaInicio", "FechaFin", "RecicladorId", r."ZonaId",
		"LatitudInicio", "LongitudInicio", "LatitudFin", "LongitudFin", z."OrganizacionId",
		rurb."UsuarioId", res."ID" AS ResiduoId, res."FechaCreacion", res."Descripcion",
		res."TipoResiduoId", tr."Nombre" AS TipoResiduoNombre, pr."Latitud", pr."Longitud"
		""";
	private static final String JOIN_SIMPLE = """
		JOIN "Zona" AS z ON z."ID" = r."ZonaId"
		JOIN "RecicladorUrbano" AS rurb ON rurb."ID" = r."RecicladorId"
		LEFT JOIN "Residuo" AS res on res."RecorridoId" = r."ID"
		LEFT JOIN "PuntoResiduo" AS pr ON pr."ID" = res."PuntoResiduoId"
		LEFT JOIN "TipoResiduo" AS tr ON tr."ID" = res."TipoResiduoId"
		""";
	private static final String SELECT_ZONA = """
		, z."Polyline", z."Nombre" AS ZonaNombre
		""";
	private static final String SELECT_RECICLADOR = """
		, u."Username", u."NombreApellido"
		""";
	private static final String JOIN_RECICLADOR = """
		JOIN "Usuario" AS u ON u."ID" = rurb."UsuarioId"
		""";

	private static final String INSERT = """
		INSERT INTO "Recorrido"(
		    "FechaRetiro", "RecicladorId", "ZonaId",
		    "LatitudInicio", "LongitudInicio",
		    "LatitudFin", "LongitudFin"
		) VALUES (?, ?, ?, ?, ?, ?, ?);
		""";

	private static final String DELETE = """
		DELETE FROM "Recorrido"
		 WHERE "ID" = ?
		   AND "ZonaId" = ?
		""";

	private static final String UPDATE_INICIO = """
		UPDATE public."Recorrido" AS r
			SET "FechaInicio" = ?
			WHERE r."ID" = ?;
		""";

	private static final String UPDATE_FIN = """
		UPDATE public."Recorrido" AS r
			SET "FechaFin" = ?
			WHERE r."ID" = ?;
		""";

	private static final String UPDATE = """
		UPDATE public."Recorrido" AS r
		SET %s
		WHERE r."ID" = ?
		AND r."ZonaId" = ?;
		""";

	private static final String SET_FECHA_RETIRO = """
		"FechaRetiro"= ?
		""";

	private static final String SET_RECICLADOR = """
		"RecicladorId"= ?
		""";


	private final ZonaDao zonaDao;

	@Inject
	public RecorridoDao(DataSource ds, ZonaDao zonaDao) {
		super(ds);
		this.zonaDao = zonaDao;
	}

	public Optional<Recorrido> get(
		Transaction t,
		long id,
		RecorridoExpand x
	) throws PersistenceException {
		try ( val select = createSelect(t, RecorridoFilter.byId(id), x) ) {
			try ( var rs = select.executeQuery() ) {
				var r = buildRecorridos(rs, x);
				return r.stream().findFirst();
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting recorrido", e);
		}
	}

	private PreparedStatement createSelect(
		Transaction t,
		RecorridoFilter f,
		RecorridoExpand x
	) throws SQLException, PersistenceException {
		val select = new StringBuilder(SELECT_SIMPLE);
		val join = new StringBuilder(JOIN_SIMPLE);
		if ( x.zona ) select.append(SELECT_ZONA);
		if ( x.reciclador ) {
			select.append(SELECT_RECICLADOR);
			join.append(JOIN_RECICLADOR);
		}

		List<Object> parameters = new ArrayList<>();
		val where = new StringBuilder();
		appendCondition(f.id, "AND r.\"ID\" = ?", where, parameters);

		val sql = String.format(SELECT_FMT, select, join, where);
		return t.prepareStatement(sql, parameters);
	}

	private Collection<Recorrido> buildRecorridos(ResultSet rs, RecorridoExpand x) throws SQLException {
		var recorridos = new HashMap<Long, Recorrido>();
		while ( rs.next() ) {
			val id = rs.getLong("ID");
			val r = recorridos.computeIfAbsent(id, newId -> buildRecorrido(rs, x, newId));

			val residuoId = rs.getLong("ResiduoId");
			if ( residuoId != 0L ) {
				val residuo = Residuo.builder()
					.id(residuoId)
					.fechaCreacion(Dates.atUTC(rs.getTimestamp("FechaCreacion")))
					.descripcion(rs.getString("Descripcion"))
					.tipoResiduo(new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")))
					.build();
				r.puntos.add(new Retiro(rs.getFloat("Latitud"), rs.getFloat("Longitud"), residuo));
			}
		}
		return recorridos.values();
	}

	@SneakyThrows
	private Recorrido buildRecorrido(ResultSet rs, RecorridoExpand x, long id) {
		val z = new Zona(rs.getLong("ZonaId"));
		z.organizacionId = rs.getLong("OrganizacionId");
		if ( x.zona ) {
			z.polyline = zonaDao.getPolyline(rs.getString("Polyline"));
			z.nombre = rs.getString("ZonaNombre");
		}

		val rec = new Ciudadano(rs.getLong("RecicladorId"), rs.getLong("UsuarioId"));
		if ( x.reciclador ) {
			rec.username = rs.getString("Username");
			rec.nombre = rs.getString("NombreApellido");
		}

		return new Recorrido(
			id,
			rs.getTimestamp("FechaRetiro").toInstant().atZone(Dates.UTC).toLocalDate(),
			Dates.atUTC(rs.getTimestamp("FechaInicio")),
			Dates.atUTC(rs.getTimestamp("FechaFin")),
			rs.getLong("RecicladorId"),
			rec,
			rs.getLong("ZonaId"),
			rs.getLong("OrganizacionId"),
			z,
			new Punto(rs.getDouble("LatitudInicio"), rs.getDouble("LongitudInicio")),
			new Punto(rs.getDouble("LatitudFin"), rs.getDouble("LongitudFin")),
			new ArrayList<>()
		);
	}

	public Recorrido save(Transaction t, Recorrido r) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setDate(1, Date.valueOf(r.fechaRetiro));
			insert.setLong(2, r.recicladorId);
			insert.setLong(3, r.zonaId);
			insert.setDouble(4, r.puntoInicio.latitud);
			insert.setDouble(5, r.puntoInicio.longitud);
			insert.setDouble(6, r.puntoFin.latitud);
			insert.setDouble(7, r.puntoFin.longitud);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the recorrido failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next()) r.id = rs.getLong(1);
				else throw new SQLException("Creating the recorrido failed, no ID obtained");
			}

			return r;
		} catch (SQLException e) {
			if ( e.getMessage().contains("Recorrido_RecicladorId_fkey") )
				throw new ForeignKeyException("No existe el reciclador", e);
			else
				throw new PersistenceException("error inserting recorrido", e);
		}
	}

	public void delete(Transaction t, long id, long zonaId) throws PersistenceException {
		try ( val delete = t.prepareStatement(DELETE) ) {
			delete.setLong(1, id);
			delete.setLong(2, zonaId);

			if (delete.executeUpdate() <= 0 )
				throw new SQLException("deleting the recorrido failed, no affected rows");

		} catch (SQLException e) {
			if ( e.getMessage().contains("Residuo_RecorridoId_fkey") )
				throw new ForeignKeyException("El recorrido ya cuenta con residuos, no puede ser eliminado");
			throw new PersistenceException("error deleting recorrido", e);
		}
	}

	public void updateDate(Transaction t, long id, RecorridoEnum o) throws NotFoundException, PersistenceException {
		String updateSql;
		if(o == RecorridoEnum.FIN)
			updateSql = UPDATE_FIN;
		else
			updateSql = UPDATE_INICIO;
		try ( var update = t.prepareStatement(updateSql) ) {
			update.setTimestamp(1, Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant()));
			update.setLong(2, id);

			if(update.executeUpdate() <= 0){
				throw new NotFoundException("updating the recorrido failed, no affected rows");
			}

		} catch (SQLException e) {
			throw new PersistenceException("error updating recorrido", e);
		}
	}
	public void putSave(Transaction t, Recorrido r) throws PersistenceException, NotFoundException {
		try (var put = createPut(t, r)){
			val s = put.toString();
			if(put.executeUpdate() <= 0){
				throw new NotFoundException("updating the recorrido failed, no affected rows");
			}
		} catch (SQLException e) {
			if(e.getMessage().contains("Recorrido_RecicladorId_fkey"))
				throw new PersistenceException("El recicladorId ingresado no existe en la tabla", e);
			throw new PersistenceException("error updating recorrido", e);
      }
   }

	private PreparedStatement createPut(Transaction t, Recorrido r) throws PersistenceException, SQLException {
		val put = new StringBuilder();
		List<Object> parameters = new ArrayList<>();
		if(r.recicladorId != null){
			put.append(SET_RECICLADOR);
			parameters.add(r.recicladorId);
		}
		if(r.fechaRetiro != null){
			if(!put.isEmpty())
				put.append(", ");
			put.append(SET_FECHA_RETIRO);
			parameters.add(r.fechaRetiro);
		}
		parameters.add(r.id);
		parameters.add(r.zonaId);
		val sql = String.format(UPDATE, put);
		var p = t.prepareStatement(sql);
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		return p;
	}
}
