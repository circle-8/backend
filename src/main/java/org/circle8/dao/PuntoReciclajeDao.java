package org.circle8.dao;

import java.sql.PreparedStatement;
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
				HashMap<Long, PuntoReciclaje> l = new HashMap<Long, PuntoReciclaje>();
				while ( rs.next() ) {
					if(l.containsKey(rs.getLong("ID"))) {
						var tr = new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre"));
						l.get(rs.getLong("ID")).tipoResiduo.add(tr);
					}else {						
						var listTipoResiduo = new ArrayList<TipoResiduo>();
						listTipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
						List<Dia> dias = obtenerDia(rs.getString("DiasAbierto"));
						if(filter.hasDias()) {
							for(int i = 0; i < dias.size(); i++) {
								if(filter.dias.contains(String.valueOf(dias.get(i).ordinal()))) {
									l.put(rs.getLong("ID"), new PuntoReciclaje(rs.getLong("ID"), rs.getDouble("Latitud"), rs.getDouble("Longitud"),
											dias, listTipoResiduo , "/user/"+rs.getLong("UsuarioId"), rs.getLong("UsuarioId"), null));
									break;
								}
							}
						}else {
							l.put(rs.getLong("ID"), new PuntoReciclaje(rs.getLong("ID"), rs.getDouble("Latitud"), rs.getDouble("Longitud"),
									dias, listTipoResiduo , "/user/"+rs.getLong("UsuarioId"), rs.getLong("UsuarioId"), null));
						}						
					}
				}				

				return new ArrayList<PuntoReciclaje>(l.values());
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
	 */
	private List<Dia> obtenerDia(String rs) {		
		var result = rs.replace("[", "").replace("]", "");
		var listDias = new ArrayList<Dia>();
		if(result.contains("1")) {
			var dias = result.split(",");
			for (int i = 0; i < dias.length; i++) {				
	            if (dias[i].trim().equals("1")) {
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
		  """;

		// TODO: cambiar por expand, ver de donde obtener el reciclador_id
		var select = "pr.\"ID\", pr.\"Latitud\","
				+ " pr.\"Longitud\", pr.\"DiasAbierto\","
				+ " prtr.\"TipoResiduoId\", tr.\"Nombre\", ciu.\"UsuarioId\"";
		
		var join = """
		   LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		   LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		   LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		   """ ;

		var sql = String.format(selectFmt, select, join);

		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();
		
//		if(f.hasDias()) {			
//			b.append("AND pr.\"DiasAbierto\"  LIKE \'%"+f.dias+"%\'\n");
//			parameters.add(f.dias);
//		}
		
		if ( f.hasTipo() ) {
			String marks = f.tiposResiduos.stream()
					.map(tr -> "?")
					.collect(Collectors.joining(","));

			b.append("AND tr.\"Nombre\" IN (").append(marks).append(")\n");
			parameters.addAll(f.tiposResiduos);
		}
		
		if(f.hasReciclador()) {
			b.append("AND pr.\"CiudadanoId\" = ?\n");
			parameters.add(f.reciclador_id);
		}
		
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
		
		System.out.println(sql);

		return p;
	}

}
