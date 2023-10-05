package org.circle8.service;

import org.circle8.dao.PlanDao;
import org.circle8.dao.SuscripcionDao;
import org.circle8.dto.PlanDto;
import org.circle8.dto.RecorridoDto;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;

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
}
