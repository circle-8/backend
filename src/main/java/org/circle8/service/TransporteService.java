package org.circle8.service;

import java.util.List;

import org.circle8.dao.Transaction;
import org.circle8.dao.TransporteDao;
import org.circle8.dto.TransporteDto;
import org.circle8.entity.Transporte;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransporteExpand;
import org.circle8.filter.TransporteFilter;

import com.google.inject.Inject;

import lombok.val;

public class TransporteService {

	private final TransporteDao dao;

	@Inject
	public TransporteService(TransporteDao dao) {
		this.dao = dao;
	}
	
	public TransporteDto get(long id, TransporteExpand x) throws ServiceException {
		val f = new TransporteFilter(id);
		try ( val t = dao.open(true) ) {
			return TransporteDto.from(get(t, f, x));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}	
	
	private Transporte get(Transaction t, TransporteFilter f, TransporteExpand x) throws ServiceException {
		try {
			return this.dao.get(t, f, x)
				.orElseThrow(() -> new NotFoundException("No existe el transporte"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar el transporte", e);
		}
	}
	
	public List<TransporteDto> list(TransporteFilter f, TransporteExpand x) throws ServiceError{
		try {
			return this.dao.list(f, x).stream().map(TransporteDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de transportes", e);
		}
	}
}
