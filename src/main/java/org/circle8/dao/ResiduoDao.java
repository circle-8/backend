package org.circle8.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.circle8.entity.Residuo;
import org.circle8.exception.ForeingKeyException;
import org.circle8.exception.PersistenceException;

import com.google.inject.Inject;

public class ResiduoDao extends Dao{

	@Inject
	public ResiduoDao(DataSource ds) {
		super(ds);
	}
	
	public Residuo save(Transaction t,Residuo residuo) throws PersistenceException {
		var insertSQL = """
				INSERT INTO "Residuo"(
				"FechaCreacion", "PuntoResiduoId", "TipoResiduoId", "Descripcion", "FechaLimiteRetiro")
				VALUES (?, ?, ?, ?, ?);
				  """;
		try ( var insert = t.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
			insert.setLong(2, residuo.puntoResiduoId);
			insert.setLong(3, residuo.tipoResiduoId);			
			insert.setString(4, residuo.descripcion);
			insert.setTimestamp(5, residuo.fechaLimiteRetiro != null? Timestamp.valueOf(residuo.fechaLimiteRetiro) : null);
			
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the residuo failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next())
					residuo.id = rs.getLong(1);
				else
					throw new SQLException("Creating the residuo failed, no ID obtained");
			}
		} catch (SQLException e) {
			if ( e.getMessage().contains("Residuo_TipoResiduoId_fkey") )
				throw new ForeingKeyException("No existe el tipo de residuo con id " + residuo.tipoResiduoId, e);
			else if(e.getMessage().contains("Residuo_PuntoResiduoId_fkey"))
				throw new ForeingKeyException("No existe el punto residuo con id " + residuo.puntoResiduoId, e);
			else
				throw new PersistenceException("error inserting residuo", e);
		} 
		
		return residuo;
	}
}
