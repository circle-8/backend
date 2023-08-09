package org.circle8.service;

import java.util.ArrayList;
import java.util.List;

import org.circle8.dao.TransaccionDao;
import org.circle8.dto.TransaccionDto;
import org.circle8.entity.Residuo;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.TransaccionFilter;

import com.google.inject.Inject;

public class TransaccionService {
	private final TransaccionDao dao;

	@Inject
	public TransaccionService(TransaccionDao dao) {
		this.dao = dao;
	}

	public TransaccionDto get(Long transaccionId, TransaccionExpand expand) throws ServiceException {
		try{
			return this.dao.get(transaccionId, expand)
				.map(TransaccionDto::from)
				.orElseThrow(() -> new NotFoundException("No existe la transaccion"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener la transaccion", e);
		}
	}

	public List<TransaccionDto> list(TransaccionFilter f, TransaccionExpand x) throws ServiceException {
		try {
			return this.dao.list(f, x).stream().map(TransaccionDto::from).toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener las transacciones", e);
		}
	}

   public TransaccionDto save(TransaccionDto dto) throws ServiceError, NotFoundException {
		var entity = dto.toEntity();
		try (var t = dao.open()) {
			entity = dao.save(t, entity);
			dto.id = entity.id;
			for(Residuo r : entity.residuos) {
				dao.saveResiduo(t, r.id, dto.id);
			}
			t.commit();
			List<String> expandList = new ArrayList<>();
			expandList.add("residuos");
			return this.dao.get(t, dto.id, new TransaccionExpand(expandList))
																	.map(TransaccionDto::from)
																	.orElseThrow(() -> new NotFoundException("No existe la transaccion que se buscaba actualizar"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar la transaccion", e);
      }
   }

	public TransaccionDto put(Long id, Long residuoId) throws ServiceException{
		try (var t = dao.open()){
			TransaccionDto dto = dao.get(t, id, new TransaccionExpand(new ArrayList<>()))
											.map(TransaccionDto::from)
											.orElseThrow(() -> new NotFoundException("No existe la transaccion"));
			dao.saveResiduo(t, residuoId, id);
			t.commit();
			List<String> expandList = new ArrayList<>();
			expandList.add("residuos");
			return this.dao.get(t, dto.id, new TransaccionExpand(expandList))
								.map(TransaccionDto::from)
								.orElseThrow(() -> new NotFoundException("No existe la transaccion"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al tratar de agregar un residuo a la transaccion", e);
		}
	}
}
