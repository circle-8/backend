package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.entity.Mensaje;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.MensajeFilter;
import org.circle8.update.UpdateMensaje;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
	private static final String WHERE_ACK = "AND \"Ack\" = ?\n";

	private static final String INSERT = """
	INSERT INTO public."Mensaje"("Type", "Timestamp", "From", "To", "Message", "Ack", "RecorridoId", "TransaccionId")
	VALUES (?, ?, ?, ?, ?, ?, ?, ?);
	""";

	private static final String UPDATE = """
		UPDATE public."Mensaje"
		SET %s
		WHERE "ID" = ?;
		""";
	private static final String SET_TYPE = "\"Type\" = ?";
	private static final String SET_TIMESTAMP = "\"Timestamp\" = ?";
	private static final String SET_FROM = "\"From\" = ?";
	private static final String SET_TO = "\"To\" = ?";
	private static final String SET_MESSAGE = "\"Message\" = ?";
	private static final String SET_ACK = "\"Ack\" = ?";
	private static final String SET_RECORRIDO = "\"RecorridoId\" = ?";
	private static final String SET_TRANSACCION = "\"TransaccionId\" = ?";

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

		appendCondition(f.recorridoId, WHERE_RECORRIDO_ID, conditions, parameters);
		appendCondition(f.transaccionId, WHERE_TRANSACCION_ID, conditions, parameters);
		appendCondition(f.type, WHERE_TYPE, conditions, parameters);
		appendCondition(f.ack, WHERE_ACK, conditions, parameters);
		appendInequality(f.timestamp, WHERE_TIMESTAMP, conditions, parameters);
		if ( f.usuarios != null && !f.usuarios.isEmpty() ) {
			conditions.append(String.format(WHERE_USUARIOS, listParam(f.usuarios), listParam(f.usuarios)));
			f.usuarios.forEach(u -> addObject(u, parameters));
			f.usuarios.forEach(u -> addObject(u, parameters));
		}
		appendCondition(f.limit, "LIMIT ?", conditions, parameters);

		val p = t.prepareStatement(conditions.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i+1, parameters.get(i));

		return p;
	}

	public Mensaje save(Transaction t, Mensaje m) throws PersistenceException {
		try ( var insert = createInsert(t, m) ) {
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the mensaje failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( !rs.next() )
					throw new SQLException("Creating the mensaje failed, no ID obtained");

				m.id = rs.getLong(1);
				return m;
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error inserting mensaje", e);
		}
	}

	public Mensaje save(Mensaje m) throws PersistenceException {
		try ( var t = open() ) {
			return save(t, m);
		}
	}

	private PreparedStatement createInsert(Transaction t, Mensaje m) throws PersistenceException, SQLException {
		val ps = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, m.type.toString());
		ps.setTimestamp(2, timestamp(m.timestamp));
		ps.setObject(3, m.from);
		ps.setLong(4, m.to);
		ps.setString(5, m.message);
		ps.setBoolean(6, m.ack);
		ps.setObject(7, m.recorridoId);
		ps.setObject(8, m.transaccionId);
		return ps;
	}

	public void update(UpdateMensaje m) throws PersistenceException, NotFoundException {
		try ( var t = open(true) ; var put = createUpdate(t, m) ) {
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe el mensaje");
		} catch ( SQLException e ) {
			throw new PersistenceException("error updating mensaje", e);
		}
	}

	private PreparedStatement createUpdate(Transaction t, UpdateMensaje m) throws PersistenceException, SQLException {
		val set = new ArrayList<String>();
		List<Object> parameters = new ArrayList<>();

		appendUpdate(m.type, SET_TYPE, set, parameters);
		appendUpdate(m.timestamp, SET_TIMESTAMP, set, parameters);
		appendUpdate(m.from, SET_FROM, set, parameters);
		appendUpdate(m.to, SET_TO, set, parameters);
		appendUpdate(m.message, SET_MESSAGE, set, parameters);
		appendUpdate(m.ack, SET_ACK, set, parameters);
		appendUpdate(m.recorridoId, SET_RECORRIDO, set, parameters);
		appendUpdate(m.transaccionId, SET_TRANSACCION, set, parameters);

		parameters.add(m.id);

		val sql = String.format(UPDATE, String.join(", ", set));
		return prepareStatement(t, sql, parameters);
	}
}
