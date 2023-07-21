package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import lombok.val;
import org.circle8.dto.Dia;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.PuntoReciclajeFilter;

import com.google.inject.Inject;

public class PuntoReciclajeDao extends Dao {

	private static final String WHERE_AREA = """
		AND pr."Latitud" BETWEEN ? AND ?
		AND pr."Longitud" BETWEEN ? AND ?
		""";

	private static final String SELECT = """
		   SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", prtr."TipoResiduoId", tr."Nombre", pr."CiudadanoId", ciu."UsuarioId"
		     FROM "PuntoReciclaje" AS pr
		LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		    WHERE 1=1
		""";

	private static final String SELECT_GET = """
		SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", pr."CiudadanoId" , prtr."TipoResiduoId", tr."Nombre", ciu."UsuarioId"
		  FROM "PuntoReciclaje" AS pr
		  LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		  LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		  LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		 WHERE pr."ID" = ?
		   AND pr."CiudadanoId" = ?
		""";

	private static final String WHERE_CIUDADADO_NULL = """
		AND pr."CiudadanoId" IS NULL
		""";

	private static final String WHERE_CIUDADADO_NOT_NULL = """
		AND pr."CiudadanoId" IS NOT NULL
		""";

	private static final String WHERE_TIPO = """
		    AND pr."ID" IN (
		    SELECT spr."ID"
		      FROM "PuntoReciclaje" spr
		 LEFT JOIN "PuntoReciclaje_TipoResiduo" AS sprtr ON sprtr."PuntoReciclajeId" = spr."ID"
		 LEFT JOIN "TipoResiduo" AS str on str."ID" = sprtr."TipoResiduoId"
		     WHERE str."Nombre" IN (%s)
		    )
		""";

	@Inject
	PuntoReciclajeDao(DataSource ds) {
		super(ds);
	}

	/**
	 * Obtiene el listado de puntos de reciclaje
	 */
	public List<PuntoReciclaje> list(PuntoReciclajeFilter filter) throws PersistenceException {
		try (var t = open(true); var select = createSelectForList(t, filter)) {
			try (var rs = select.executeQuery()) {
				return getList(rs, filter);
			}
		} catch (SQLException e) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}
	}

	/**
	 * Obtiene un punto de reciclaje por medio de su id
	 */
	public Optional<PuntoReciclaje> get(Long id, Long recicladorId) throws PersistenceException {
		try (var t = open(true); var select = createSelectForGet(t, id, recicladorId);
			 var rs = select.executeQuery()) {
			return Optional.ofNullable(getPunto(rs));
		} catch (SQLException e) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}
	}

	/**
	 * Procesa el resultado de la consulta de list
	 * Agrupa los tipos de residuo por cada punto
	 * Valida el filtro de dias
	 */
	private List<PuntoReciclaje> getList(ResultSet rs, PuntoReciclajeFilter filter) throws SQLException {
		var mapPuntos = new HashMap<Long, PuntoReciclaje>();
		while (rs.next()) {
			val id = rs.getLong("ID");
			PuntoReciclaje punto = mapPuntos.get(id);
			if (punto == null) {
				punto = new PuntoReciclaje(
					id,
					rs.getString("Titulo"),
					rs.getDouble("Latitud"),
					rs.getDouble("Longitud"),
					Dia.getDia(rs.getString("DiasAbierto")),
					new ArrayList<>(),
					rs.getLong("CiudadanoId"),
					User.builder().id(rs.getLong("UsuarioId")).build()
				);
				mapPuntos.put(id, punto);
			}
			if (rs.getInt("TipoResiduoId") != 0)
				punto.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
		}

		return mapPuntos.values().stream()
			.filter(p -> !filter.hasDias() || !Collections.disjoint(filter.dias, p.dias))
			.toList();
	}

	/**
	 * Procesa el resultado de la consulta de get
	 */
	private PuntoReciclaje getPunto(ResultSet rs) throws SQLException {
		PuntoReciclaje punto = null;
		if (rs.next()) {
			punto = new PuntoReciclaje(
				rs.getLong("ID"),
				rs.getString("Titulo"),
				rs.getDouble("Latitud"),
				rs.getDouble("Longitud"),
				Dia.getDia(rs.getString("DiasAbierto")),
				new ArrayList<>(),
				rs.getLong("CiudadanoId"),
				User.builder().id(rs.getLong("UsuarioId")).build()
			);
			if (rs.getInt("TipoResiduoId") != 0)
				punto.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
		}
		return punto;
	}

	/**
	 *
	 */
	private PreparedStatement createSelectForList(Transaction t, PuntoReciclajeFilter f) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT);
		List<Object> parameters = new ArrayList<>();

		b.append(f.isPuntoVerde() ? WHERE_CIUDADADO_NULL : WHERE_CIUDADADO_NOT_NULL);

		if (f.hasTipo()) {
			String marks = f.tiposResiduos.stream()
				.map(tr -> "?")
				.collect(Collectors.joining(","));

			b.append(String.format(WHERE_TIPO, marks));
			parameters.addAll(f.tiposResiduos);
		}

		if (f.hasReciclador()) {
			b.append("AND pr.\"CiudadanoId\" = ?\n");
			parameters.add(f.reciclador_id);
		}

		if (f.hasArea()) {
			b.append(WHERE_AREA);
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

	private PreparedStatement createSelectForGet(Transaction t, Long id, Long recicladorId) throws PersistenceException, SQLException {
		var b = SELECT_GET;

		var p = t.prepareStatement(b.toString());
		p.setLong(1, id);
		p.setLong(2, recicladorId);

		return p;
	}
}
