package org.circle8.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import org.circle8.controller.chat.response.ConversacionResponse;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.TransaccionDao;
import org.circle8.dao.Transaction;
import org.circle8.dao.UserDao;
import org.circle8.dto.ConversacionDto;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Transaccion;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.InequalityFilter;
import org.circle8.filter.RecorridoFilter;
import org.circle8.filter.TransaccionFilter;
import org.circle8.service.dto.IConversacion;
import org.circle8.service.dto.RecorridoConv;
import org.circle8.service.dto.TransaccionConv;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Singleton
public class ChatService {
	private final UserDao users;
	private final TransaccionDao transacciones;
	private final RecorridoDao recorridos;

	@Inject
	public ChatService(UserDao users, TransaccionDao transacciones, RecorridoDao recorridos) {
		this.users = users;
		this.transacciones = transacciones;
		this.recorridos = recorridos;
	}

	public List<ConversacionDto> list(Long userId) throws ServiceException {
		try ( val t = users.open() ) {
			val user = users.get(null, userId).orElseThrow(() -> new NotFoundException("usuario inexistente"));

			val ts = getTransacciones(t);
			val transportes = user.transportistaId == null
				? List.<TransaccionConv>of()
				: ts.stream()
				.filter(tr -> tr.transporte != null && user.transportistaId.equals(tr.transporte.transportistaId))
				.map(tr -> (IConversacion) new TransaccionConv(tr, ConversacionResponse.Type.TRANSACCION_TRANSPORTES))
				.toList();
			val recibos = ts.stream()
				.filter(tr -> tr.puntoReciclaje.recicladorId.equals(user.ciudadanoId))
				.map(tr -> (IConversacion) new TransaccionConv(tr, ConversacionResponse.Type.TRANSACCION_RECIBOS))
				.toList();
			val entregas = ts.stream()
				.filter(tr -> tr.residuos.stream().anyMatch(r -> Objects.equals(user.ciudadanoId, r.ciudadanoId)))
				.map(tr -> (IConversacion) new TransaccionConv(tr, ConversacionResponse.Type.TRANSACCION_ENTREGAS))
				.toList();

			val rs = getRecorridos(t);
			val asReciclador = rs.stream()
				.filter(r -> r.recicladorId.equals(user.recicladorUrbanoId))
				.map(r -> (IConversacion) new RecorridoConv(r, ConversacionResponse.Type.RECORRIDO_TRANSPORTES))
				.toList();
			val asEntregas = rs.stream()
				.filter(rec -> user.ciudadanoId != null && rec.getResiduos().stream().anyMatch(r -> r.ciudadanoId == user.ciudadanoId))
				.map(r -> (IConversacion) new RecorridoConv(r, ConversacionResponse.Type.RECORRIDO_ENTREGAS))
				.toList();

			return Stream.of(transportes, recibos, entregas, asReciclador, asEntregas)
				.flatMap(List::stream)
				.map(tr -> tr.toConversacion(user))
				.sorted(Comparator.comparing(dto -> dto.fechaConversacion))
				.toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("error al listar conversaciones", e);
		}
	}

	private List<Transaccion> getTransacciones(Transaction t) throws PersistenceException {
		val f = TransaccionFilter.builder()
			.fechaRetiro(InequalityFilter.<ZonedDateTime>builder().isNull(true).build())
			.build();
		val x = TransaccionExpand.builder()
			.transporte(true)
			.residuos(true)
			.build();

		return transacciones.list(t, f, x);
	}

	private List<Recorrido> getRecorridos(Transaction t) throws PersistenceException {
		val f = RecorridoFilter.builder()
			.fechaFin(InequalityFilter.<ZonedDateTime>builder().isNull(true).build())
			.build();
		val x = RecorridoExpand.ALL;
		return recorridos.list(t, f, x);
	}
}
