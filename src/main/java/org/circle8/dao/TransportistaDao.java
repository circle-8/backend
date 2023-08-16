package org.circle8.dao;

import java.sql.SQLException;
import java.sql.Statement;

import org.circle8.entity.Transportista;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;

public class TransportistaDao {

	public Transportista save(Transaction t, Transportista tr) throws PersistenceException {
		var insertSQL = "INSERT INTO \"Transportista\"(\"UsuarioId\") VALUES(?)";
		try ( var insert = t.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
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
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Transportista_UsuarioId_key") )
				throw new DuplicatedEntry("transportista already exists", e);
			else
				throw new PersistenceException("error inserting transportista", e);
		}

		return tr;
	}
}
