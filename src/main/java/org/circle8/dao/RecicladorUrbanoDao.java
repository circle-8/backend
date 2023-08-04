package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;

import lombok.val;

public class RecicladorUrbanoDao {
	private static final String INSERT = """
			INSERT INTO public."RecicladorUrbano"(
			"UsuarioId", "OrganizacionId", "ZonaId")
			VALUES (?, ?, ?);
			""";
	
	private static final String INSERT_WITHOUT_ZONA = """
			INSERT INTO public."RecicladorUrbano"(
			"UsuarioId", "OrganizacionId")
			VALUES (?, ?);
			""";
	

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
		val insert = u.zonaId != null ? INSERT : INSERT_WITHOUT_ZONA;		
		val ps = t.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
		ps.setLong(1, u.id);
		ps.setLong(2, u.organizacionId);
		if(u.zonaId != null)
			ps.setLong(3, u.zonaId);
		return ps;
	}
}
