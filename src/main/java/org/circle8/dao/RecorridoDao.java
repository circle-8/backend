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
import org.circle8.exception.PersistenceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.RecorridoFilter;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			new Punto(rs.getFloat("LatitudInicio"), rs.getFloat("LongitudInicio")),
			new Punto(rs.getFloat("LatitudFin"), rs.getFloat("LongitudFin")),
			new ArrayList<>()
		);
	}
}
