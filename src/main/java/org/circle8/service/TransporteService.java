package org.circle8.service;

import java.util.List;

import org.circle8.dao.ResiduoDao;
import org.circle8.dao.Transaction;
import org.circle8.dao.TransporteDao;
import org.circle8.dto.TransporteDto;
import org.circle8.entity.Transporte;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.ResiduoExpand;
import org.circle8.expand.TransporteExpand;
import org.circle8.filter.ResiduosFilter;
import org.circle8.filter.TransporteFilter;

import com.google.inject.Inject;

import lombok.val;

public class TransporteService {

	private final TransporteDao dao;
	private final ResiduoDao residuoDao;

	@Inject
	public TransporteService(TransporteDao dao, ResiduoDao residuoDao) {
		this.dao = dao;
		this.residuoDao = residuoDao;
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
	
	public TransporteDto update(TransporteDto tr) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.update(t, tr.toEntity());
			return get(tr.id, TransporteExpand.EMPTY);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al actualizar el transporte.", e);
		}
	}	
	
	public TransporteDto fin(TransporteDto tr) throws ServiceException {
		try ( val t = dao.open(true) ) {
			val transF = TransporteFilter.builder().id(tr.id).build();
			val trans = this.dao.get(t, transF, new TransporteExpand(false, true))
					.orElseThrow(() -> new NotFoundException("No existe el transporte"));			
			
			val f = ResiduosFilter.builder()
					.retirado(false)
					.transaccion(trans.transaccionId)
					.build();
			val residuosNoRetirados = residuoDao.list(t, f, ResiduoExpand.EMPTY);
			if (!residuosNoRetirados.isEmpty())
				throw new ServiceException("No puede finalizar el transporte si no ha retirado todos los residuos");		
			
			dao.update(t, tr.toEntity());
			return get(tr.id, TransporteExpand.EMPTY);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al actualizar el transporte.", e);
		}
	}	
}
