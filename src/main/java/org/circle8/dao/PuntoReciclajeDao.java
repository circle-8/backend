package org.circle8.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.dto.Dia;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.TipoResiduo;
import org.circle8.exception.PersistenceException;

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
	public List<PuntoReciclajeDto> getPuntos() throws PersistenceException{
//		List<PuntoReciclajeDto> list = new ArrayList<PuntoReciclajeDto>();
//		PuntoReciclaje p = new PuntoReciclaje(0, 0, 0, List.of(Dia.LUNES, Dia.MIERCOLES,Dia.VIERNES), List.of(new TipoResiduo(1, "ORGANICO"),new TipoResiduo(2, "TUVIE")), null, null, null);
//		list.add(PuntoReciclajeDto.from(p));
//		return  list;
		var selectSQL = "";
		try ( var t = open(true); var select = t.prepareStatement(selectSQL) ) {
			try ( var rs = select.executeQuery() ) {
				var listado = new ArrayList<PuntoReciclajeDto>();
				if ( !rs.next() )
					return listado;
				
						

				return listado;
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}		
	}

}
