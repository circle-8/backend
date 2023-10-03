package org.circle8.service.dto;

import org.circle8.controller.chat.response.ConversacionResponse;
import org.circle8.dto.ConversacionDto;
import org.circle8.entity.Recorrido;
import org.circle8.entity.User;
import org.circle8.utils.Dates;

import java.util.regex.Pattern;

public record RecorridoConv(
	Recorrido r,
	ConversacionResponse.Type type
) implements IConversacion {
	@Override
	public ConversacionDto toConversacion(User user) {
		return new ConversacionDto(
			"REC-" + this.r.id,
			(!Pattern.matches(".*[zZ][oO][nN][aA].*", this.r.zona.nombre) ? "Zona: " : "") + this.r.zona.nombre,
			makeDescripcion(user),
			this.type,
			this.r.id,
			makeResiduoId(user),
			String.format("/user/%s/conversacion/%s/chats", user.id, "REC-" + this.r.id),
			false, // TODO
			null, // TODO
			this.r.fechaRetiro.atStartOfDay(Dates.UTC)
		);
	}

	private String makeDescripcion(User user) {
		return switch (this.type) {
			case RECORRIDO_TRANSPORTES -> makeTransportas(r.getResiduos());
			case RECORRIDO_ENTREGAS -> makeEntregas(user, r.getResiduos());
			default -> throw new IllegalArgumentException(
				String.format("%s no definido para Recorrido", this.type.name())
			);
		};
	}

	private Long makeResiduoId(User user) {
		return switch (this.type) {
			case RECORRIDO_ENTREGAS -> makeResiduoId(user, r.getResiduos());
			default -> null;
		};
	}
}
