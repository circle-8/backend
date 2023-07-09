package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.circle8.dto.Dia;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.TipoResiduo;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.PuntoReciclajeFilter;

import com.google.inject.Inject;

public class PuntoReciclajeDao extends Dao{

	@Inject
	PuntoReciclajeDao(DataSource ds) {
		super(ds);
	}
	
	/**
	 * Obtiene el listado de puntos de reciclaje
	 * @return
	 * @throws PersistenceException
	 */
	public List<PuntoReciclaje> list(PuntoReciclajeFilter filter) throws PersistenceException{
		try ( var t = open(true); var select = createSelectForList(t, filter) ) {
			try ( var rs = select.executeQuery() ) {
				var listado = new ArrayList<PuntoReciclaje>();
				HashMap<Long, PuntoReciclaje> l = new HashMap<Long, PuntoReciclaje>();
				while ( rs.next() ) {
					if(l.containsKey(rs.getLong("ID"))) {
						
					}else {						
						var listTipoResiduo = new ArrayList<TipoResiduo>();
						listTipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
						listado.add(new PuntoReciclaje(rs.getLong("ID"), rs.getDouble("Latitud"), rs.getDouble("Longitud"),
										obtenerDia(rs), listTipoResiduo , null, 0, null));
					}
				}				

				return listado;
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}		
	}
	
	/**
	 * Parsea el string de dias y devuelve el listado
	 * en base a los que esten marcados como 1
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<Dia> obtenerDia(ResultSet rs) throws SQLException {		
		var result = rs.getString("DiasAbierto");
		result = result.replace("[", "").replace("]", "").replaceAll(" ","");
		var listDias = new ArrayList<Dia>();
		if(result.contains("1")) {
			var dias = result.split(",");
			for (int i = 0; i < dias.length; i++) {
	            if (dias[i].equals("1")) {
	            	listDias.add(Dia.get(i));
	            }
	        }
		}		
		return listDias;
	}
	
	/**
	 * Arma el Select para la consulta de list
	 * @param t
	 * @param f
	 * @return
	 * @throws PersistenceException
	 * @throws SQLException
	 */
	private PreparedStatement createSelectForList(Transaction t, PuntoReciclajeFilter f) throws PersistenceException, SQLException {
		var selectFmt = """
		 SELECT %s
		   FROM "PuntoReciclaje" AS pr
		   %s
		  WHERE 1=1
		  ORDER BY pr.\"ID\"
		  """;

		// TODO: cambiar por expand, ver de donde obtener el reciclador_id
		var select = "pr.\"ID\", pr.\"Latitud\","
				+ " pr.\"Longitud\", pr.\"DiasAbierto\","
				+ " prtr.\"TipoResiduoId\", tr.\"Nombre\"";
		
		var join = """
		   LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		   LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		   """ ;

		var sql = String.format(selectFmt, select, join);

		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();
		
		if(f.hasDias()) {
			String marks = "[" + f.dias.stream()
					.map(tr -> "?")
					.collect(Collectors.joining(",")) + "]";
			b.append("AND pr.\"DiasAbierto\" LIKE '%").append(marks).append("%'\n");
			parameters.addAll(f.tipoResiduo);
		}
		
		if ( f.hasTipo() ) {
			String marks = f.tipoResiduo.stream()
				.map(tr -> "?")
				.collect(Collectors.joining(","));

			b.append("AND tr.\"Nombre\" IN (").append(marks).append(")\n");
			parameters.addAll(f.tipoResiduo);
		}
		
//		TODO: ver con que tabla toma el reciclador
//		if(f.hasReciclador()) {
//			b.append("AND pr.\"CiudadanoId\" = ?");
//			parameters.add(f.reciclador_id);
//		}
		
		if ( f.hasArea() ) {
			b.append("AND pr.\"Latitud\" BETWEEN ? AND ?\n")
				.append("AND pr.\"Longitud\" BETWEEN ? AND ?\n");
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}		

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++) {
			p.setObject(i+1, parameters.get(i));
		}		

		return p;
	}

}
