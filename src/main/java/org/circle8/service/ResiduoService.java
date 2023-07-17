package org.circle8.service;

import org.circle8.dao.ResiduoDao;
import org.circle8.dto.ResiduoDto;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;

import com.google.inject.Inject;

public class ResiduoService {
	private final ResiduoDao dao;

	@Inject
	public ResiduoService(ResiduoDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Guarda un residuo
	 * @param dto residuo dto que luego sirve como return
	 * @return
	 * @throws el mismo residuo que se recibió como parámetro, pero modificado
	 */
	public ResiduoDto save(ResiduoDto dto) throws ServiceError {
		var residuo = dto.toEntity();		
		try( var t = dao.open()) {
			residuo = dao.save(t, residuo);
			dto.id = residuo.id;			
			t.commit();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el residuo", e);
		}		
		return dto;
	}
}
