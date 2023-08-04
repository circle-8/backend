package org.circle8.controller.request;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

public interface IRequest {
	@ToString
	class Validation {
		public List<String> reasons = new ArrayList<>();
		public void add(String reason) { this.reasons.add(reason); }
		public boolean valid() { return reasons.isEmpty(); }
		public String message() { return reasons.toString();}
	}

	Validation valid();
}
