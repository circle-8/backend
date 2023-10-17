package org.circle8.service.chat.dto;

import org.circle8.controller.chat.response.ConversacionResponse;
import org.circle8.dto.ConversacionDto;
import org.circle8.dto.UserDto;
import org.circle8.entity.Transaccion;
import org.circle8.entity.User;

public record TransaccionConv(
	Transaccion t,
	ConversacionResponse.Type type
) implements IConversacion {

	@Override
	public ConversacionDto toConversacion(User user) {
		return new ConversacionDto(
			"TRA-" + this.t.id,
			"Transaccion #" + this.t.id,
			makeDescripcion(user),
			this.type,
			this.t.id,
			makeResiduoId(user),
			false, // TODO
			null, // TODO
			this.t.fechaCreacion,
			UserDto.from(user)
		);
	}

	private String makeDescripcion(User user) {
		return switch (this.type) {
			case TRANSACCION_TRANSPORTES -> makeTransportas(this.t.residuos);
			case TRANSACCION_ENTREGAS -> makeEntregas(user, this.t.residuos);
			case TRANSACCION_RECIBOS -> String.format(
				"Recibís %s residuo%s",
				this.t.residuos.size(),
				this.t.residuos.size() > 1 ? "s" : ""
			);
			default -> throw new IllegalArgumentException(
				String.format("%s no definido para Transaccion", this.type.name())
			);
		};
	}

	private Long makeResiduoId(User user) {
		return switch (this.type) {
			case TRANSACCION_ENTREGAS -> makeResiduoId(user, this.t.residuos);
			default -> null;
		};
	}
}
