package org.circle8.service;

import java.sql.SQLException;
import java.util.List;

import org.circle8.controller.request.punto_reciclaje.PuntoReciclajeRequest;
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
	public PuntoReciclajeDto save(PuntoReciclajeDto dto) throws ServiceError {
		var entity = dto.toEntity();
		try (var t = dao.open()) {
			entity = dao.save(t, entity);
			dto.id = entity.id;
			for(TipoResiduoDto tr : dto.tipoResiduo){
				dao.saveRelacion(t, tr.id, dto.id);
			}
			t.commit();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el punto de reciclaje", e);
		}
		return dto;
	}

	/**
	 * Obtiene un punto de reciclaje por medio de su id
	 */
	public PuntoReciclajeDto get(Long id, Long recicladorId) throws ServiceError, NotFoundException {
		try {
			return PuntoReciclajeDto.from(this.dao.get(id, recicladorId).
				orElseThrow(() -> new NotFoundException("No existe el punto de reciclaje")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de puntos de reciclaje", e);
		}

	}

	/**
	 * Elimina un punto de reciclaje
	 * @param id
	 * @param recicladorId
	 * @return
	 * @throws ServiceError
	 */
	public boolean delete(Long id, Long recicladorId) throws ServiceError {
		boolean delete = false;
		try (var t = dao.open()) {
			dao.deleteRelacion(t, id);
			delete = dao.delete(t, id, recicladorId);
			t.commit();
			return delete;
		} catch (PersistenceException | SQLException e) {
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
	public boolean put(Long id, Long recicladorId, PuntoReciclajeRequest req) throws ServiceError{
		boolean update = false;
		try (var t = dao.open()){

			if(!req.tiposResiduo.isEmpty()){
				dao.deleteRelacion(t, id);
				for(Integer tr : req.tiposResiduo){
					dao.saveRelacion(t, tr, id);
				}
			}
			update = this.dao.put(t, id, recicladorId, req);
			t.commit();
			return update;
		} catch (PersistenceException | SQLException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de puntos de reciclaje", e);
		}
	}
}
