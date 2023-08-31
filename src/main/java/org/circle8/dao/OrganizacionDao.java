package org.circle8.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Organizacion;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;

import com.google.inject.Inject;

public class OrganizacionDao extends Dao {
	@Inject public OrganizacionDao(DataSource ds) { super(ds); }
	
	private static final String INSERT = """
			INSERT INTO "Organizacion" ("RazonSocial", "UsuarioId")
			VALUES(?, ?)
			""";
	
	private static final String UPDATE = """
			UPDATE "Organizacion"
			SET "RazonSocial"=?
			WHERE "UsuarioId"=?
			""";
	
	private static final String SELECT = """
			SELECT "ID", "RazonSocial", "UsuarioId" 
			FROM "Organizacion" 
			WHERE "ID" = ?
			""";

	public Organizacion save(Transaction t, Organizacion o) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setString(1, o.razonSocial);
			insert.setLong(2, o.usuarioId);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the organizacion failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( rs.next() ) o.id = rs.getLong(1);
				else throw new SQLException("Creating the transportista failed, no ID obtained");
			}

			return o;
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Organizacion_UsuarioId_key") )
				throw new DuplicatedEntry("organizacion already exists", e);
			else
				throw new PersistenceException("error inserting organizacion", e);
		}
	}

	public Optional<Organizacion> get(long id) throws PersistenceException {
		try ( var t = open(true); var select = t.prepareStatement(SELECT) ) {
			select.setLong(1, id);
			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() ) return Optional.empty();
				return Optional.of(new Organizacion(
					rs.getLong("ID"),
					rs.getString("RazonSocial"),
					rs.getLong("UsuarioId")
				));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting organizacion", e);
		}
	}
	
	public void update(Transaction t, User u) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(UPDATE) ) {
			put.setString(1, u.razonSocial);
			put.setLong(2, u.id);			
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe la organizacion");
		} catch ( SQLException e ) {
			throw new PersistenceException("error updating organizacion", e);
		}
	}
}
