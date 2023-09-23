package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.entity.Consejo;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.update.UpdateConsejo;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConsejoDao extends Dao {
	private static final String INSERT = """
		INSERT INTO "Consejo"("Titulo", "Descripcion", "FechaCreacion")
		VALUES (?, ?, ?);
		""";

	private static final String SELECT = """
		SELECT "ID", "Titulo", "Descripcion", "FechaCreacion"
		  FROM "Consejo"
		 ORDER BY "FechaCreacion" DESC, "ID" DESC
		""";

	private static final String UPDATE = """
		UPDATE public."Consejo"
		SET %s
		WHERE "ID" = ?;
		""";
	private static final String SET_TITULO = "\"Titulo\" = ?";
	private static final String SET_DESCRIPCION = "\"Descripcion\" = ?";

	private static final String DELETE = "DELETE FROM public.\"Consejo\" WHERE \"ID\" = ?;\n";

	@Inject
	public ConsejoDao(DataSource ds) { super(ds); }

	public List<Consejo> list() throws PersistenceException {
		try ( var t = open(true); var select = t.prepareStatement(SELECT) ) {
			try ( var rs = select.executeQuery() ) {
				return buildList(rs, r -> new Consejo(
					rs.getLong("ID"),
					rs.getString("Titulo"),
					rs.getString("Descripcion"),
					rs.getDate("FechaCreacion").toLocalDate()
				));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting consejos", e);
		}
	}

	public Consejo save(Consejo c) throws PersistenceException {
		try ( var t = open(true) ; var insert = createInsert(t, c) ) {
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the consejo failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( !rs.next() )
					throw new SQLException("Creating the consejo failed, no ID obtained");

				c.id = rs.getLong(1);
				return c;
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error inserting consejo", e);
		}
	}

	private PreparedStatement createInsert(Transaction t, Consejo c) throws PersistenceException, SQLException {
		val ps = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, c.titulo);
		ps.setString(2, c.descripcion);
		ps.setDate(3, date(c.fechaCreacion));
		return ps;
	}

	public void update(UpdateConsejo c) throws PersistenceException, NotFoundException {
		try ( var t = open(true) ; var put = createUpdate(t, c) ) {
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe el consejo");
		} catch ( SQLException e ) {
			throw new PersistenceException("error updating consejo", e);
		}
	}

	private PreparedStatement createUpdate(Transaction t, UpdateConsejo c) throws PersistenceException, SQLException {
		val set = new ArrayList<String>();
		List<Object> parameters = new ArrayList<>();

		appendUpdate(c.titulo, SET_TITULO, set, parameters);
		appendUpdate(c.descripcion, SET_DESCRIPCION, set, parameters);

		parameters.add(c.id);

		val sql = String.format(UPDATE, String.join(", ", set));
		return prepareStatement(t, sql, parameters);
	}

	public void delete(long id) throws PersistenceException {
		try ( val t = open(true); val delete = t.prepareStatement(DELETE) ) {
			delete.setLong(1, id);

			if (delete.executeUpdate() <= 0 )
				throw new SQLException("deleting the consejo failed, no affected rows");

		} catch (SQLException e) {
			throw new PersistenceException("error deleting consejo", e);
		}
	}
}
