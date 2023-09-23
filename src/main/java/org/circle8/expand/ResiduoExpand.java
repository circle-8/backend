package org.circle8.expand;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class ResiduoExpand {
	public static final ResiduoExpand EMPTY = new ResiduoExpand(false);
	public final boolean base64;

	public ResiduoExpand(List<String> expand) {
		this.base64 = expand.contains("base64");
	}
}
