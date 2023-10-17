package org.circle8.utils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Dates {
	public static final ZoneId UTC = ZoneOffset.UTC;

	private Dates() {}

	public static ZonedDateTime atUTC(Timestamp timestamp) {
		if ( timestamp == null ) return null;
		return timestamp.toInstant().atZone(UTC);
	}

	public static ZonedDateTime now() {
		return ZonedDateTime.now(UTC);
	}
}
