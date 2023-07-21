package org.circle8.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.circle8.entity.TipoResiduo;
import org.circle8.exception.PersistenceException;

import com.google.inject.Inject;

import lombok.val;

public class TipoResiduoDao extends Dao{
	
	private static final String SELECT_LIST = """
			SELECT "ID", "Nombre" FROM "TipoResiduo"
			""";

	@Inject
	TipoResiduoDao(DataSource ds) {
		super(ds);
	}
	
	public List<TipoResiduo> list() throws PersistenceException{
		try ( var t = open(true); var select = t.prepareStatement(SELECT_LIST)) {
			try ( var rs = select.executeQuery() ) {
				val l = new ArrayList<TipoResiduo>();
				while ( rs.next() ) {
					l.add(TipoResiduo.builder()
							.id(rs.getLong("ID"))
							.nombre(rs.getString("Nombre"))
							.build());
				}
				return l;
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting tipos de residuo", e);
		}
	}

}
