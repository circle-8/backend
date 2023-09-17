package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RecicladorUrbanoDao extends Dao {
	private static final String INSERT = """
			INSERT INTO public."RecicladorUrbano"(
			"UsuarioId", "OrganizacionId", "ZonaId",
			"FechaNacimiento", "DNI", "Domicilio", "Telefono"
			)
			VALUES (?, ?, ?, ?, ?, ?, ?);
			""";

	private static final String UPDATE = """
			UPDATE "RecicladorUrbano"
			SET %s
			WHERE "UsuarioId"=?
			""";

	private static final String SET_ORGANIZACION = "\"OrganizacionId\" = ?\n";
	private static final String SET_ZONA = "\"ZonaId\" = ?\n";
	private static final String SET_FECHA_NACIMIENTO = "\"FechaNacimiento\" = ?\n";
	private static final String SET_DNI = "\"DNI\" = ?\n";
	private static final String SET_DOMICILIO = "\"Domicilio\" = ?\n";
	private static final String SET_TELEFONO = "\"Telefono\" = ?\n";

	private static final String UPDATE_ZONA_NULL = """
			UPDATE "RecicladorUrbano"
			SET "ZonaId" = NULL
			WHERE 1=1
			%s
			""";

	@Inject
	public RecicladorUrbanoDao(DataSource ds) { super(ds); }


	public Long save(Transaction t, User u) throws PersistenceException, NotFoundException {
		try ( var insert = createInsert(t, u) ) {
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the reciclador failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( rs.next() )
					return rs.getLong(1);
				else
					throw new SQLException("Creating the reciclador failed, no ID obtained");
			}
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Ciudadano_UsuarioId_key") )
				throw new DuplicatedEntry("ciudadano already exists", e);
			else if(e.getMessage().contains("RecicladorUrbano_OrganizacionId_fkey"))
				throw new NotFoundException("No existe la organizacion con id " + u.organizacionId);
			else
				throw new PersistenceException("error inserting ciudadano", e);
		}
	}

	private PreparedStatement createInsert(Transaction t,User u) throws PersistenceException, SQLException {
		val ps = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
		ps.setLong(1, u.id);
		ps.setLong(2, u.organizacionId);
		ps.setObject(3, u.zonaId);
		ps.setDate(4, u.reciclador != null ? date(u.reciclador.fechaNacimiento) : null);
		ps.setString(5, u.reciclador != null ? u.reciclador.dni : null);
		ps.setString(6, u.reciclador != null ? u.reciclador.domicilio : null);
		ps.setString(7, u.reciclador != null ? u.reciclador.telefono : null);
		return ps;
	}

	public void update(Transaction t, User u) throws PersistenceException, NotFoundException {
		try ( var put = createUpdate(t, u) ) {
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe el reciclador");
		} catch ( SQLException e ) {
			throw new PersistenceException("error updating user", e);
		}
	}

	private PreparedStatement createUpdate(Transaction t, User u) throws PersistenceException, SQLException {
		val set = new ArrayList<String>();
		List<Object> parameters = new ArrayList<>();

		appendUpdate(u.organizacionId, SET_ORGANIZACION, set, parameters);
		appendUpdate(u.zonaId, SET_ZONA, set, parameters);
		if ( u.reciclador != null ) {
			appendUpdate(u.reciclador.fechaNacimiento, SET_FECHA_NACIMIENTO, set, parameters);
			appendUpdate(u.reciclador.dni, SET_DNI, set, parameters);
			appendUpdate(u.reciclador.domicilio, SET_DOMICILIO, set, parameters);
			appendUpdate(u.reciclador.telefono, SET_TELEFONO, set, parameters);
		}

		parameters.add(u.id);

		val sql = String.format(UPDATE, String.join(", ", set));
		return prepareStatement(t, sql, parameters);
	}

	public void desasociarZona(Transaction t, Long recicladorId, Long zonaId) throws PersistenceException {
		try ( val update =  createUpdateZona(t, recicladorId, zonaId) ) {
			update.executeUpdate();
		} catch ( SQLException e ) {
			throw new PersistenceException("error updating zona in reciclador", e);
		}
	}

	private PreparedStatement createUpdateZona(
		Transaction t,
		Long recicladorId,
		Long zonaId
	) throws PersistenceException, SQLException {
		val conditions = new StringBuilder();
		val parameters = new ArrayList<>();

		appendCondition(recicladorId, "AND \"ID\" = ?", conditions, parameters);
		appendCondition(zonaId, "AND \"ZonaId\" = ?", conditions, parameters);

		val sql = String.format(UPDATE_ZONA_NULL, conditions);
		return prepareStatement(t, sql, parameters);
	}
}
