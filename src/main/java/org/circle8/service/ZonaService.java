package org.circle8.service;

import java.util.List;

import org.circle8.dao.ZonaDao;
import org.circle8.dto.TipoResiduoDto;
import org.circle8.dto.ZonaDto;
import org.circle8.entity.TipoResiduo;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.ZonaExpand;
import org.circle8.filter.ZonaFilter;

import com.google.inject.Inject;

import lombok.val;

public class ZonaService {

	private final ZonaDao dao;
	
	@Inject
	public ZonaService(ZonaDao dao) {
		this.dao = dao;
	}
	
	public ZonaDto save(Long organizacionId,ZonaDto dto) throws ServiceException {
		var entity = dto.toEntity();	
		try (var t = dao.open()) {
			entity = dao.save(t, organizacionId, entity);
			for(TipoResiduo tr : entity.tipoResiduo){
				dao.saveTipos(t, entity.id, tr.id);
			}
			t.commit();
			val f = ZonaFilter.builder().id(entity.id).build();
			val x = new ZonaExpand(true, false);
			return get(f, x);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar la zona", e);
		}
	}
	
	public ZonaDto put(Long zonaId,Long organizacionId,ZonaDto dto) throws ServiceException {
		var entity = dto.toEntity();	
		try (var t = dao.open()) {
			this.dao.deleteTipos(t, zonaId);
			this.dao.update(t, zonaId, entity);
			for(TipoResiduoDto tr : dto.tipoResiduo){
				dao.saveTipos(t, zonaId, tr.id);
			}
			//TODO: validar puntos que puedan quedar afuera
			t.commit();
			val f = ZonaFilter.builder().id(zonaId).build();
			val x = new ZonaExpand(true, false);
			val zona = get(f, x);			
			return zona;
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar la zona", e);
		}
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

}
