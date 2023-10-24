package org.circle8.service.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.controller.chat.response.ChatResponse;
import org.circle8.controller.chat.response.ConversacionResponse;
import org.circle8.dao.MensajeDao;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.TransaccionDao;
import org.circle8.dao.Transaction;
import org.circle8.dao.UserDao;
import org.circle8.dto.ChatDto;
import org.circle8.dto.ConversacionDto;
import org.circle8.dto.MensajeDto;
import org.circle8.dto.UserDto;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Residuo;
import org.circle8.entity.Transaccion;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.InequalityFilter;
import org.circle8.filter.MensajeFilter;
import org.circle8.filter.RecorridoFilter;
import org.circle8.filter.TransaccionFilter;
import org.circle8.service.chat.dto.IConversacion;
import org.circle8.service.chat.dto.RecorridoConv;
import org.circle8.service.chat.dto.TransaccionConv;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Singleton
public class ChatService {
	private final UserDao users;
	private final TransaccionDao transacciones;
	private final RecorridoDao recorridos;
	private final MensajeDao mensajes;

	public enum ConversacionType {
		TRANSACCION, RECORRIDO
	}

	record ResiduoEntry(Long usuarioId, List<Residuo> residuos) {
		String buildDescripcion() {
			var descripcion = residuos.get(0).formatted();
			return residuos.size() == 1
				? String.format("%s...", descripcion.substring(0, Math.min(25, descripcion.length())))
				: String.format("Entrega %d residuos", residuos.size());
		}
	}


	@Inject
	public ChatService(
		UserDao users,
		TransaccionDao transacciones,
		RecorridoDao recorridos,
		MensajeDao mensajes
	) {
		this.users = users;
		this.transacciones = transacciones;
		this.recorridos = recorridos;
		this.mensajes = mensajes;
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
				.filter(tr -> tr.residuos.stream().anyMatch(r -> Objects.equals(user.ciudadanoId, r.ciudadano.id)))
				.map(tr -> (IConversacion) new TransaccionConv(tr, ConversacionResponse.Type.TRANSACCION_ENTREGAS))
				.toList();

			val rs = getRecorridos(t);
			val asReciclador = rs.stream()
				.filter(r -> r.recicladorId.equals(user.recicladorUrbanoId))
				.map(r -> (IConversacion) new RecorridoConv(r, ConversacionResponse.Type.RECORRIDO_TRANSPORTES))
				.toList();
			val asEntregas = rs.stream()
				.filter(rec -> user.ciudadanoId != null && rec.getResiduos().stream().anyMatch(r -> r.ciudadano.id == user.ciudadanoId))
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

		return transacciones.list(t, f, x).stream()
			.filter(tr -> !tr.residuos.isEmpty())
			.toList();
	}

	private List<Recorrido> getRecorridos(Transaction t) throws PersistenceException {
		val f = RecorridoFilter.builder()
			.fechaFin(InequalityFilter.<ZonedDateTime>builder().isNull(true).build())
			.build();
		val x = RecorridoExpand.ALL;
		return recorridos.list(t, f, x).stream()
			.filter(r -> !r.puntos.isEmpty())
			.toList();
	}


	public List<ChatDto> chats(ConversacionType type, Long id, Long userId) throws ServiceException {
		try ( val t = users.open() ) {
			val user = users.get(null, userId).orElseThrow(() -> new NotFoundException("usuario inexistente"));
			return switch ( type ) {
				case TRANSACCION -> getTransaccionChats(t, id, user);
				case RECORRIDO -> getRecorridoChats(t, id, user);
			};
		} catch ( PersistenceException e ) {
			throw new ServiceError("error al listar chats", e);
		}
	}

	private List<ChatDto> getTransaccionChats(Transaction t, Long id, User user) throws NotFoundException, PersistenceException {
		val x = TransaccionExpand.builder().transporte(true).residuos(true).build();
		val tr = transacciones.get(t, id, x).orElseThrow(() -> new NotFoundException("transaccion inexistente"));

		val isReciclador = user.ciudadanoId.equals(tr.puntoReciclaje.recicladorId);
		val isTransportista = tr.transporte != null && tr.transporte.transportistaId.equals(user.transportistaId);
		val isCiudadano = tr.residuos.stream().map(r -> r.ciudadano.id).anyMatch(c -> c.equals(user.ciudadanoId));

		val l = new ArrayList<ChatDto>();

		if ( isReciclador || isTransportista ) {
			// Soy reciclador o transportista, tengo que ver todos los que entregan
			val resMap = getGroupedResiduos(tr.residuos);
			resMap.entrySet().stream()
				.map(e -> new ResiduoEntry(e.getKey(), e.getValue()))
				.map(e -> new ChatDto(
					String.format("TRA-%d+%d+%d", id, user.id, e.usuarioId),
					String.format("Entrega %s", e.residuos.get(0).ciudadano.username),
					e.buildDescripcion(),
					ChatResponse.Type.CIUDADANO,
					e.usuarioId,
					false, // TODO
					null, // TODO
					UserDto.from(user)
				))
				.forEach(l::add);
		}

		if ( isTransportista || isCiudadano ) {
			// Soy transportista o ciudadano, puedo ver al reciclador
			l.add(new ChatDto(
				String.format("TRA-%d+%d+%d", id, tr.puntoReciclaje.reciclador.id, user.id),
				String.format("Recibe %s", tr.puntoReciclaje.reciclador.username),
				"",
				ChatResponse.Type.RECICLADOR,
				tr.puntoReciclaje.reciclador.id,
				false, // TODO
				null, // TODO
				UserDto.from(user)
			));
		}

		if ( ( isReciclador || isCiudadano ) && tr.transporte != null && tr.transporte.transportistaId != null ) {
			// Soy reciclador o ciudadano, puedo ver al transportista
			var idForCiudadano = String.format("TRA-%d+%d+%d", id, tr.transporte.transportista.usuarioId, user.id);
			var idForReciclador = String.format("TRA-%d+%d+%d", id, user.id, tr.transporte.transportista.usuarioId);
			l.add(new ChatDto(
				isReciclador ? idForReciclador : idForCiudadano,
				String.format("Transporta %s", tr.transporte.transportista.user.username),
				"",
				ChatResponse.Type.TRANSPORTISTA,
				tr.transporte.transportista.usuarioId,
				false, // TODO
				null, // TODO
				UserDto.from(user)
			));
		}

		return l.stream()
			.filter(c -> c.externalId != user.id)
			.toList();
	}

	@NotNull
	private Map<Long, List<Residuo>> getGroupedResiduos(List<Residuo> rs) {
		val resMap = new HashMap<Long, List<Residuo>>();
		for ( Residuo r : rs )
			resMap.computeIfAbsent(r.ciudadano.usuarioId, v -> new ArrayList<>()).add(r);
		return resMap;
	}

	private List<ChatDto> getRecorridoChats(Transaction t, Long id, User user) throws NotFoundException, PersistenceException {
		val x = RecorridoExpand.ALL;
		val rec = recorridos.get(t, id, x).orElseThrow(() -> new NotFoundException("recorrido inexistente"));

		val l = new ArrayList<ChatDto>();
		val isReciclador = user.recicladorUrbanoId != null;
		if ( isReciclador && !rec.puntos.isEmpty() ) {
			// Soy reciclador, puedo ver a todos los residuos
			val resMap = getGroupedResiduos(rec.puntos.stream().map(p -> p.residuo).toList());
			resMap.entrySet().stream()
				.map(e -> new ResiduoEntry(e.getKey(), e.getValue()))
				.map(e -> new ChatDto(
					String.format("REC-%d+%d+%d", id, user.id, e.usuarioId),
					String.format("Entrega %s", e.residuos.get(0).ciudadano.username),
					e.buildDescripcion(),
					ChatResponse.Type.CIUDADANO,
					e.usuarioId,
					false, // TODO
					null, // TODO
					UserDto.from(user)
				))
				.forEach(l::add);
		} else {
			// Soy ciudadano, solo puedo ver al reciclador
			l.add(new ChatDto(
				String.format("REC-%d+%d+%d", id, rec.reciclador.usuarioId, user.id),
				String.format("Transporta %s", rec.reciclador.username),
				"",
				ChatResponse.Type.RECICLADOR_URBANO,
				rec.reciclador.usuarioId,
				false, // TODO
				null, // TODO
				UserDto.from(user)
			));
		}

		return l;
	}

	public List<MensajeDto> mensajes(String chatId) throws ServiceException {
		var filterBuilder = MensajeFilter.builder().chatId(chatId);
		var messagesFilter = filterBuilder
			.type(ChatMessageResponse.Type.MESSAGE)
			.build();
		var componentFilter = filterBuilder
			.type(ChatMessageResponse.Type.COMPONENT)
			.ack(false)
			.limit(1L)
			.build();

		try ( val t = mensajes.open() ) {
			var messages = mensajes.list(t, messagesFilter);
			var components = mensajes.list(t, componentFilter);
			return Stream.of(messages, components)
				.flatMap(List::stream)
				.map(MensajeDto::from)
				.sorted(Comparator.comparing(MensajeDto::getTimestamp))
				.toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al listar los mensajes", e);
		}
	}

}
