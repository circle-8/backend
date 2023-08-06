package org.circle8.service;

import java.util.List;

import org.circle8.dao.PuntoResiduoDao;
import org.circle8.dao.ZonaDao;
import org.circle8.dto.ZonaDto;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.PuntoResiduoExpand;
import org.circle8.expand.ZonaExpand;
import org.circle8.filter.ZonaFilter;

import com.google.inject.Inject;

import lombok.val;

public class ZonaService {

	private final ZonaDao dao;
	private final PuntoResiduoDao puntoResiduoDao;
	
	@Inject
	public ZonaService(ZonaDao dao, PuntoResiduoDao puntoResiduoDao) {
		this.dao = dao;
		this.puntoResiduoDao = puntoResiduoDao;
	}
	
	public ZonaDto get(ZonaFilter f, ZonaExpand x) throws ServiceException {
		try ( val t = dao.open(true) ) {
			val zona = this.dao.get(t,f, x)
					.orElseThrow(() -> new NotFoundException("No existe la zona"));
			return ZonaDto.from(zona);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la zona", e);
		}
	}
	
	public List<ZonaDto> list(ZonaFilter f, ZonaExpand x) throws ServiceError{
		try {
			return this.dao.list(f, x).stream().map(ZonaDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de zonas", e);
		}
	}
	
	public ZonaDto includePuntoResiduo(Long ciudadanoId, Long puntoResiduoId, Long zonaId) throws ServiceError, ServiceException {
		try ( val t = dao.open(true) ) {
			val f = ZonaFilter.builder().id(zonaId).build();
			val zona = this.dao.get(t,f, ZonaExpand.EMPTY)
					.orElseThrow(() -> new NotFoundException("No existe la zona"));
			
			val punto = this.puntoResiduoDao.get(ciudadanoId, puntoResiduoId, PuntoResiduoExpand.EMPTY)
					.orElseThrow(() -> new NotFoundException("No existe el punto de residuo"));;
			
			//TODO: validar que el punto este dentro de la zona
					
			this.dao.includePuntoResiduo(t, puntoResiduoId, zonaId);
					
			return ZonaDto.from(zona);		
		}catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el punto de residuo", e);
		}	
	}

}
