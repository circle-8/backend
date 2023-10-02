package org.circle8.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import org.circle8.controller.chat.response.ConversacionResponse;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.TransaccionDao;
import org.circle8.dao.UserDao;
import org.circle8.dto.ConversacionDto;
import org.circle8.entity.Transaccion;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.InequalityFilter;
import org.circle8.filter.TransaccionFilter;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
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

	record TransaccionConv(Transaccion t, ConversacionResponse.Type type) {}

	public List<ConversacionDto> list(Long userId) throws ServiceException {
		try ( val t = users.open() ) {
			val user = users.get(null, userId).orElseThrow(() -> new NotFoundException("usuario inexistente"));

			val f = TransaccionFilter.builder()
				.fechaRetiro(InequalityFilter.<ZonedDateTime>builder().isNull(true).build())
				.build();
			val x = TransaccionExpand.builder()
				.transporte(true)
				.residuos(true)
				.build();
			val ts = transacciones.list(t, f, x);

			val transportes = user.transportistaId == null
				? List.<TransaccionConv>of()
				: ts.stream()
				.filter(tr -> tr.transporte != null && user.transportistaId.equals(tr.transporte.transportistaId))
				.map(tr -> new TransaccionConv(tr, ConversacionResponse.Type.TRANSACCION_TRANSPORTES))
				.toList();
			val recibos = ts.stream()
				.filter(tr -> tr.puntoReciclaje.recicladorId.equals(user.ciudadanoId))
				.map(tr -> new TransaccionConv(tr, ConversacionResponse.Type.TRANSACCION_RECIBOS))
				.toList();
			val entregas = ts.stream()
				.filter(tr -> tr.residuos.stream().anyMatch(r -> r.ciudadanoId == user.ciudadanoId))
				.map(tr -> new TransaccionConv(tr, ConversacionResponse.Type.TRANSACCION_ENTREGAS))
				.toList();

			// TODO recorrido

			return Stream.of(transportes, recibos, entregas)
				.flatMap(List::stream)
				.map(conv -> new ConversacionDto(
					"TRA-"+conv.t.id,
					"Transaccion #"+conv.t.id,
					makeDescripcion(user, conv),
					conv.type,
					conv.t.id,
					makeResiduoId(user, conv),
					String.format("/user/%s/conversacion/%s/chats", userId, "TRA-"+conv.t.id),
					false, // TODO
					null, // TODO
					conv.t.fechaCreacion
				))
				.sorted(Comparator.comparing(dto -> dto.fechaConversacion))
				.toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("error al listar conversaciones", e);
		}
	}

	private String makeDescripcion(User user, TransaccionConv conv) {
		return switch ( conv.type ) {
			case TRANSACCION_TRANSPORTES -> String.format(
				"Transportás %s residuo%s",
				conv.t.residuos.size(),
				conv.t.residuos.size() > 1 ? "s" : ""
			);
			case TRANSACCION_ENTREGAS -> String.format(
				"Entregás %s...",
				conv.t.residuos.stream()
					.filter(r -> r.ciudadanoId == user.ciudadanoId)
					.findFirst()
					.map(r -> r.descripcion)
					.map(r -> r.replace("\n", " "))
					.map(r -> r.replace("\u200B", " "))
					.map(r -> r.substring(0, Math.min(10, r.length())))
					.orElse("")
			);
			case TRANSACCION_RECIBOS -> String.format(
				"Recibís %s residuo%s",
				conv.t.residuos.size(),
				conv.t.residuos.size() > 1 ? "s" : ""
			);
			default -> throw new IllegalArgumentException(
				String.format("%s no definido para Transaccion", conv.type.name())
			);
		};
	}

	private Long makeResiduoId(User user, TransaccionConv conv) {
		return switch ( conv.type ) {
			case TRANSACCION_ENTREGAS -> conv.t.residuos
				.stream()
				.filter(r -> r.ciudadanoId == user.ciudadanoId)
				.findFirst()
				.map(r -> r.id)
				.orElse(null);
			default -> null;
		};
	}
}
