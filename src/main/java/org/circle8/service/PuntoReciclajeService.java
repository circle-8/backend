package org.circle8.service;

import java.util.List;

import org.circle8.controller.request.punto_reciclaje.PuntoReciclajePostRequest;
import org.circle8.dao.PuntoReciclajeDao;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.dto.TipoResiduoDto;
import org.circle8.exception.NotFoundException;

import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.filter.PuntoReciclajeFilter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PuntoReciclajeService {

	private final PuntoReciclajeDao dao;

	@Inject
	public PuntoReciclajeService(PuntoReciclajeDao dao) {
		this.dao = dao;
	}

	/**
	 * Obtiene el listado de puntos de reciclaje
	 */
	public List<PuntoReciclajeDto> list(PuntoReciclajeFilter filter) throws ServiceError{
		try {
			return this.dao.list(filter).stream().map(PuntoReciclajeDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de puntos de reciclaje", e);
		}
	}

	/**
	 * Guarda un nuevo punto de reciclaje
	 * @param dto
	 * @return
	 * @throws ServiceError
	 */
	public PuntoReciclajeDto save(PuntoReciclajeDto dto) throws ServiceError, NotFoundException {
		var entity = dto.toEntity();
		try (var t = dao.open()) {
			entity = dao.save(t, entity);
			dto.id = entity.id;
			for(TipoResiduoDto tr : dto.tipoResiduo){
				dao.saveTipos(t, tr.id, dto.id);
			}
			t.commit();
			return PuntoReciclajeDto.from(this.dao.get(dto.id, dto.recicladorId).
				orElseThrow(() -> new NotFoundException("No existe el punto de reciclaje")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el punto de reciclaje", e);
		}
	}

	/**
	 * Obtiene un punto de reciclaje por medio de su id
	 */
	public PuntoReciclajeDto get(Long id, Long recicladorId) throws ServiceError, NotFoundException {
		try {
			return PuntoReciclajeDto.from(this.dao.get(id, recicladorId).
				orElseThrow(() -> new NotFoundException("No existe el punto de reciclaje")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el punto de reciclaje", e);
		}

	}

	/**
	 * Elimina un punto de reciclaje
	 * @param id
	 * @param recicladorId
	 * @return
	 * @throws ServiceError
	 */
	public void delete(Long id, Long recicladorId) throws ServiceError, NotFoundException {
		try (var t = dao.open()) {
			dao.deleteTipos(t, id);
			dao.delete(t, id, recicladorId);
			t.commit();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al eliminar el punto de reciclaje", e);
		}
	}

	/**
	 * Actualiza un punto de reciclaje
	 * @param id
	 * @param recicladorId
	 * @param req
	 * @return
	 * @throws ServiceError
	 */
	public PuntoReciclajeDto put(Long id, Long recicladorId, PuntoReciclajePostRequest req) throws ServiceError, NotFoundException {
		try (var t = dao.open()){

			if(!req.tiposResiduo.isEmpty()){
				dao.deleteTipos(t, id);
				for(Integer tr : req.tiposResiduo){
					dao.saveTipos(t, tr, id);
				}
			}
			this.dao.put(t, id, recicladorId, req);
			t.commit();
			return PuntoReciclajeDto.from(this.dao.get(id, recicladorId).
				orElseThrow(() -> new NotFoundException("No existe el punto de reciclaje")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al modificar el punto de reciclaje", e);
		}
	}
}
