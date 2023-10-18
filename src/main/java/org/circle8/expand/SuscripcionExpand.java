package org.circle8.expand;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class SuscripcionExpand {
	public static final SuscripcionExpand EMPTY = new SuscripcionExpand(false);
	
	public final boolean plan;
	
	public SuscripcionExpand(List<String> expands) {
		this.plan = expands.contains("plan");
	}
}
