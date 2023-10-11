package org.circle8.service;

import java.util.List;

import org.circle8.dao.PlanDao;
import org.circle8.dao.SuscripcionDao;
import org.circle8.dto.PlanDto;
import org.circle8.dto.RecorridoDto;
import org.circle8.dto.TransporteDto;
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.expand.TransporteExpand;
import org.circle8.filter.RecorridoFilter;

import com.google.inject.Inject;

import lombok.val;

public class PlanService {

	private PlanDao dao;

	@Inject
	public PlanService(PlanDao dao) { this.dao = dao; }

	public PlanDto get(long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var r = this.dao.get(t, id)
								 .orElseThrow(() -> new NotFoundException("No existe el plan"));
			return PlanDto.from(r);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar el plan", e);
		}
	}

	public List<PlanDto> list() throws ServiceException {
		try ( val t = dao.open(true) ) {
			return this.dao.list(t).stream().map(PlanDto::from).toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener los planes", e);
		}
	}

	public void delete(long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.delete(t, id);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al eliminar el plan", e);
		}
	}

	public PlanDto save(PlanDto dto) throws ServiceException {
		try( var t = dao.open(true) ) {
			val r = dao.save(t, dto.toEntity());
			dto.id = r.id;

			return dto;
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el plan", e);
		}
	}

	public PlanDto update(PlanDto p) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.update(t, p.toEntity());
			return get(p.id);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al actualizar el transporte.", e);
		}
	}

}
