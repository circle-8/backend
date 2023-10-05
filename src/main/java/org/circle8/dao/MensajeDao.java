package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.entity.Mensaje;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.MensajeFilter;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MensajeDao extends Dao {
	private static final String SELECT = """
	SELECT "ID", "Type", "Timestamp", "From", "To", "Message", "Ack", "RecorridoId", "TransaccionId"
	  FROM "Mensaje"
	 WHERE 1=1
	""";
	private static final String WHERE_USUARIOS = """
	AND (
	       "From" IN ( %s )
	    OR "To" IN ( %s )
	)
	""";
	private static final String WHERE_RECORRIDO_ID = "AND \"RecorridoId\" = ?\n";
	private static final String WHERE_TRANSACCION_ID = "AND \"TransaccionId\" = ?\n";
	private static final String WHERE_TYPE = "AND \"Type\" = ?\n";
	private static final String WHERE_TIMESTAMP = "AND \"Timestamp\" %s\n";
	private static final String WHERE_ACK = "AND \"ACK\" = ?\n";

	@Inject
	public MensajeDao(DataSource ds) { super(ds); }

	public List<Mensaje> list(Transaction t, MensajeFilter f) throws PersistenceException {
		try ( val select = createSelect(t, f) ) {
			try ( var rs = select.executeQuery() ) {
				return buildList(rs, r -> new Mensaje(
					rs.getLong("ID"),
					ChatMessageResponse.Type.valueOf(rs.getString("Type")),
					Dates.atUTC(rs.getTimestamp("Timestamp")),
					rs.getLong("From") != 0 ? rs.getLong("From") : null,
					rs.getLong("To"),
					rs.getString("Message"),
					rs.getBoolean("Ack"),
					rs.getLong("RecorridoId") != 0 ? rs.getLong("RecorridoId") : null,
					rs.getLong("TransaccionId") != 0 ? rs.getLong("TransaccionId") : null
				));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error listing mensajes", e);
		}
	}

	private PreparedStatement createSelect(Transaction t, MensajeFilter f) throws SQLException, PersistenceException {
		val conditions = new StringBuilder(SELECT);
		List<Object> parameters = new ArrayList<>();

		appendListCondition(f.usuarios, WHERE_USUARIOS, conditions, parameters);
		appendCondition(f.recorridoId, WHERE_RECORRIDO_ID, conditions, parameters);
		appendCondition(f.transaccionId, WHERE_TRANSACCION_ID, conditions, parameters);
		appendCondition(f.type, WHERE_TYPE, conditions, parameters);
		appendCondition(f.ack, WHERE_ACK, conditions, parameters);
		appendInequality(f.timestamp, WHERE_TIMESTAMP, conditions, parameters);

		val p = t.prepareStatement(conditions.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i+1, parameters.get(i));

		return p;
	}
}
