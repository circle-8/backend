package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.circle8.dto.Dia;
import org.circle8.entity.PuntoVerde;
import org.circle8.entity.TipoResiduo;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.PuntoVerdeFilter;

import com.google.inject.Inject;

import lombok.val;

public class PuntoVerdeDao extends Dao{	
	private static final String WHERE_AREA = """
			AND pr."Latitud" BETWEEN ? AND ?
			AND pr."Longitud" BETWEEN ? AND ?
			""";
	private static final String SELECT_LIST = """
		   SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", prtr."TipoResiduoId", tr."Nombre"
		     FROM "PuntoReciclaje" AS pr
		LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		    WHERE 1=1
		    AND pr."CiudadanoId" IS NULL
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
	PuntoVerdeDao(DataSource ds) {
		super(ds);
	}
	
	/**
	 * obtiene el listado de puntos verdes
	 * @param filter
	 * @return
	 * @throws PersistenceException
	 */
	public List<PuntoVerde> list(PuntoVerdeFilter filter) throws PersistenceException{
		try ( var t = open(true); var select = createSelectForList(t, filter) ) {
			try ( var rs = select.executeQuery() ) {
				return getList(rs, filter);
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting puntos verdes", e);
		}
	}
	
	/**
	 * Procesa el resultado de la consulta de list
	 * Agrupa los tipos de residuo por cada punto
	 * Valida el filtro de dias
	 */
	private List<PuntoVerde> getList(ResultSet rs, PuntoVerdeFilter filter) throws SQLException{
		var mapPuntos = new HashMap<Long, PuntoVerde>();
		while ( rs.next() ) {
			val id = rs.getLong("ID");
			PuntoVerde punto = mapPuntos.get(id);
			if ( punto == null) {
				punto = new PuntoVerde(
					id,
					rs.getString("Titulo"),
					rs.getDouble("Latitud"),
					rs.getDouble("Longitud"),
					Dia.getDia(rs.getString("DiasAbierto")),
					new ArrayList<>()
				);
				mapPuntos.put(id, punto);
			}
			if(rs.getInt("TipoResiduoId") != 0)
				punto.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
		}

		return mapPuntos.values().stream()
			.filter(p -> !filter.hasDias() || !Collections.disjoint(filter.dias, p.dias))
			.toList();
	}
	
	/**
	 * Crea la consulta de select para el metodo list
	 * @param t
	 * @param f
	 * @return
	 * @throws PersistenceException
	 * @throws SQLException
	 */
	private PreparedStatement createSelectForList(Transaction t, PuntoVerdeFilter f) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT_LIST);
		List<Object> parameters = new ArrayList<>();

		if ( f.hasTipo() ) {
			String marks = f.tiposResiduos.stream()
					.map(tr -> "?")
					.collect(Collectors.joining(","));

			b.append(String.format(WHERE_TIPO, marks));
			parameters.addAll(f.tiposResiduos);
		}
		
		if ( f.hasArea() ) {
			b.append(WHERE_AREA);
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i+1, parameters.get(i));

		return p;
	}

}
