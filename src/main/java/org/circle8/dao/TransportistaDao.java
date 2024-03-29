package org.circle8.dao;

import com.google.inject.Inject;
import org.circle8.entity.Transportista;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;
import org.circle8.utils.Puntos;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransportistaDao extends Dao {

	private static final String INSERT = """
			INSERT INTO "Transportista"("UsuarioId")
			VALUES (?);
			""";

	private static final String SELECT_FMT = """
			SELECT
			       %s
			  FROM "Transportista" AS t
			 WHERE 1 = 1
			""";

	private static final String SELECT_SIMPLE = """
			t."ID", t."UsuarioId", t."Polyline"
			""";

	private static final String WHERE_ID = """
			AND t."ID" = ?
			""";

	private static final String WHERE_USUARIO = """
			AND t."UsuarioId" = ?
			""";

	@Inject
	TransportistaDao(DataSource ds) {
		super(ds);
	}


	public Transportista save(Transaction t, Transportista tr) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, tr.usuarioId);
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the transportista failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( rs.next() )
					tr.id = rs.getLong(1);
				else
					throw new SQLException("Creating the transportista failed, no ID obtained");
			}

			return tr;
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Transportista_UsuarioId_key") )
				throw new DuplicatedEntry("transportista already exists", e);
			else
				throw new PersistenceException("error inserting transportista", e);
		}
	}

	public Optional<Transportista> get(Transaction t, Long id, Long userId) throws PersistenceException {
		try ( var select = createSelect(t, id, userId) ) {
			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();
				return Optional.of(buildTransportista(rs));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting transportista", e);
		}
	}

	private PreparedStatement createSelect(Transaction t, Long id, Long usuarioId)
			throws PersistenceException, SQLException {
		var selectFields = SELECT_SIMPLE;

		var sql = String.format(SELECT_FMT, selectFields);
		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();

		if (id != null) {
			appendCondition(id, WHERE_ID, b, parameters);
		}

		if (usuarioId != null) {
			appendCondition(usuarioId, WHERE_USUARIO, b, parameters);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

	private Transportista buildTransportista(ResultSet rs) throws SQLException {
		var t = new Transportista();
		t.id = rs.getLong("ID");
		t.usuarioId = rs.getLong("UsuarioId");
		t.polyline = Puntos.getPolyline(rs.getString("Polyline"));
		return t;
	}
}
