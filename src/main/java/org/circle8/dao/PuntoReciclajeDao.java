package org.circle8.dao;

import java.util.ArrayList;
import java.util.List;

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
	public List<PuntoReciclaje> getPuntos(PuntoReciclajeFilter filter) throws PersistenceException{
		
		List<PuntoReciclaje> list = new ArrayList<PuntoReciclaje>();
		PuntoReciclaje p = new PuntoReciclaje(0, 0, 0, List.of(Dia.LUNES, Dia.JUEVES), List.of(new TipoResiduo(1, "ORGANICO")), null, null, null);
		PuntoReciclaje p1 = new PuntoReciclaje(1, -3555, -8888, List.of(Dia.LUNES, Dia.MIERCOLES,Dia.VIERNES,Dia.DOMINGO), List.of(new TipoResiduo(1, "PLASTICO"),new TipoResiduo(2, "PAPEL")), null, null, null);
		list.add(p);
		list.add(p1);
		return  list;
//		var selectSQL = "";
//		try ( var t = open(true); var select = t.prepareStatement(selectSQL) ) {
//			try ( var rs = select.executeQuery() ) {
//				var listado = new ArrayList<PuntoReciclaje>();
//				if ( !rs.next() )
//					return listado;
//				
//						
//
//				return listado;
//			}
//		} catch ( SQLException e ) {
//			throw new PersistenceException("error getting punto reciclaje", e);
//		}		
	}

}
