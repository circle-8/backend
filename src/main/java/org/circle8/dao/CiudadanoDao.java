package org.circle8.dao;

import java.sql.SQLException;
import java.sql.Statement;

import org.circle8.entity.Ciudadano;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;

public class CiudadanoDao {

	public Ciudadano save(Transaction t, Ciudadano c) throws PersistenceException {
		var insertSQL = "INSERT INTO \"Ciudadano\"(\"UsuarioId\") VALUES(?)";
		try ( var insert = t.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, c.usuarioId);
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the ciudadano failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( rs.next() )
					c.id = rs.getLong(1);
				else
					throw new SQLException("Creating the ciudadano failed, no ID obtained");
			}
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Ciudadano_UsuarioId_key") )
				throw new DuplicatedEntry("ciudadano already exists", e);
			else
				throw new PersistenceException("error inserting ciudadano", e);
		}

		return c;
	}
}
