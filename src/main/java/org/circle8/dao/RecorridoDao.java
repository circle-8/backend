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
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.RecorridoFilter;
import org.circle8.service.RecorridoService.UpdateEnum;
import org.circle8.utils.Dates;
import org.circle8.utils.Puntos;

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
		rurb."UsuarioId"
		""";
	private static final String SELECT_RESIDUOS = """
		, res."ID" AS ResiduoId, res."FechaCreacion", res."Descripcion",
		res."TipoResiduoId", tr."Nombre" AS TipoResiduoNombre, pr."Latitud", pr."Longitud"
		""";
	private static final String JOIN_SIMPLE = """
		JOIN "Zona" AS z ON z."ID" = r."ZonaId"
		JOIN "RecicladorUrbano" AS rurb ON rurb."ID" = r."RecicladorId"
		""";
	private static final String JOIN_RESIDUOS = """
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

	private static final String UPDATE = """
		UPDATE public."Recorrido" AS r
		SET %s
		WHERE 1 = 1
		%s
		""";

	private static final String WHERE_ID = "AND r.\"ID\" = ?\n";
	private static final String WHERE_ZONA_ID = "AND r.\"ZonaId\" = ?;\n";
	private static final String SET_FECHA_FIN = "\"FechaFin\" = ?\n";
	private static final String SET_FECHA_INICIO = "\"FechaInicio\" = ?\n";
	private static final String SET_FECHA_RETIRO = "\"FechaRetiro\"= ?\n";
	private static final String SET_RECICLADOR = "\"RecicladorId\"= ?\n";
	private static final String SET_LATITUD_INICIO = "\"LatitudInicio\"= ?\n";
	private static final String SET_LATITUD_FIN = "\"LatitudFin\"= ?\n";
	private static final String SET_LONGITUD_INICIO = "\"LongitudInicio\"= ?\n";
	private static final String SET_LONGITUD_FIN = "\"LongitudFin\"= ?\n";


	private static final String UPDATE_ZONA_NULL = """
			UPDATE "Recorrido"
			SET "ZonaId" = NULL
			WHERE "ZonaId" = ?;
			""";

	@Inject
	public RecorridoDao(DataSource ds) {
		super(ds);
	}

	public Optional<Recorrido> get(
		Transaction t,
		long id,
		RecorridoExpand x
	) throws PersistenceException {
		try (
			val select = createSelect(t, RecorridoFilter.byId(id), x);
			val rs = select.executeQuery()
		) {
			var r = buildRecorridos(rs, x);
			return r.stream().findFirst();
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting recorrido", e);
		}
	}

	public List<Recorrido> list(Transaction t, RecorridoFilter f, RecorridoExpand x) throws PersistenceException {
		try (
			val select = createSelect(t, f, x);
			val rs = select.executeQuery()
		) {
			return new ArrayList<>(buildRecorridos(rs, x));
		} catch ( SQLException e ) {
			throw new PersistenceException("error listing recorrido", e);
		}
	}

	public List<Recorrido> list(RecorridoFilter f, RecorridoExpand x) throws PersistenceException {
		try ( val t = open(true) ) {
			return list(t, f, x);
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
		if ( x.residuos ) {
			select.append(SELECT_RESIDUOS);
			join.append(JOIN_RESIDUOS);
		}
		if ( x.reciclador ) {
			select.append(SELECT_RECICLADOR);
			join.append(JOIN_RECICLADOR);
		}

		List<Object> parameters = new ArrayList<>();
		val where = new StringBuilder();
		appendCondition(f.id, "AND r.\"ID\" = ?\n", where, parameters);
		appendCondition(f.recicladorId, "AND r.\"RecicladorId\" = ?\n", where, parameters);
		appendCondition(f.organizacionId, "AND z.\"OrganizacionId\" = ?\n", where, parameters);
		appendCondition(f.zonaId, "AND z.\"ID\" = ?\n", where, parameters);
		appendInequality(f.fechaRetiro, "AND r.\"FechaRetiro\" %s\n", where, parameters);
		appendInequality(f.fechaInicio, "AND r.\"FechaInicio\" %s\n", where, parameters);
		appendInequality(f.fechaFin, "AND r.\"FechaFin\" %s\n", where, parameters);

		val sql = String.format(SELECT_FMT, select, join, where);
		return t.prepareStatement(sql, parameters);
	}

	private Collection<Recorrido> buildRecorridos(ResultSet rs, RecorridoExpand x) throws SQLException {
		var recorridos = new HashMap<Long, Recorrido>();
		while ( rs.next() ) {
			val id = rs.getLong("ID");
			val r = recorridos.computeIfAbsent(id, newId -> buildRecorrido(rs, x, newId));

			if ( !x.residuos ) continue;
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
			z.polyline = Puntos.getPolyline(rs.getString("Polyline"));
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


	public void update(Transaction t, Recorrido r, UpdateEnum o) throws PersistenceException, NotFoundException {
		try (var put = createUpdate(t, r, o)){
			if(put.executeUpdate() <= 0){
				throw new NotFoundException("updating the recorrido failed, no affected rows");
			}
		} catch (SQLException e) {
			if(e.getMessage().contains("Recorrido_RecicladorId_fkey"))
				throw new ForeignKeyException("El recicladorId ingresado no existe en la tabla", e);
			throw new PersistenceException("error updating recorrido", e);
		}
	}


	private PreparedStatement createUpdate(Transaction t, Recorrido r, UpdateEnum o) throws PersistenceException, SQLException {
		val set = new ArrayList<String>();
		val parameters = new ArrayList<>();

		final String where;
		if(o == UpdateEnum.RETIRO) {
			appendUpdate(r.recicladorId, SET_RECICLADOR, set, parameters);
			appendUpdate(r.fechaRetiro, SET_FECHA_RETIRO, set, parameters);
			if ( r.puntoInicio != null ) {
				appendUpdate(r.puntoInicio.latitud, SET_LATITUD_INICIO, set, parameters);
				appendUpdate(r.puntoInicio.longitud, SET_LONGITUD_INICIO, set, parameters);
			}
			if ( r.puntoFin != null ) {
				appendUpdate(r.puntoFin.latitud, SET_LATITUD_FIN, set, parameters);
				appendUpdate(r.puntoFin.longitud, SET_LONGITUD_FIN, set, parameters);
			}

			where = WHERE_ID + WHERE_ZONA_ID;
			parameters.add(r.id);
			parameters.add(r.zonaId);
		} else {
			set.add(o == UpdateEnum.FIN ? SET_FECHA_FIN : SET_FECHA_INICIO);
			parameters.add(Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant()));

			where = WHERE_ID;
			parameters.add(r.id);
		}

		val sql = String.format(UPDATE, String.join(", ", set), where);
		var p = t.prepareStatement(sql);
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		return p;
	}

	public void desasociarZona(Transaction t,Long zonaId) throws PersistenceException {
		try ( val update =  t.prepareStatement(UPDATE_ZONA_NULL) ) {
			update.setLong(1, zonaId);
			update.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("error Updating zona in recorrido", e);
		}
	}
}
