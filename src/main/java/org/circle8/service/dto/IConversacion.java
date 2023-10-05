package org.circle8.service.dto;

import org.circle8.dto.ConversacionDto;
import org.circle8.entity.Residuo;
import org.circle8.entity.User;

import java.util.List;

public interface IConversacion {
	ConversacionDto toConversacion(User user);

	default String makeTransportas(List<Residuo> rs) {
		return String.format("Transportás %s residuo%s", rs.size(), rs.size() > 1 ? "s" : "");
	}

	default String makeEntregas(User u, List<Residuo> rs) {
		return String.format(
			"Entregás %s...",
			rs.stream()
				.filter(r -> r.ciudadano.id == u.ciudadanoId)
				.findFirst()
				.map(Residuo::formatted)
				.map(r -> r.substring(0, Math.min(10, r.length())))
				.orElse("")
		);
	}

	default Long makeResiduoId(User u, List<Residuo> rs) {
		return rs.stream()
			.filter(r -> r.ciudadano.id == u.ciudadanoId)
			.findFirst()
			.map(r -> r.id)
			.orElse(null);
	}
}
