package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Plan;
import org.circle8.entity.Transporte;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;

import com.google.inject.Inject;

import lombok.val;

public class PlanDao extends Dao{

	private static final String SELECT = """
		SELECT p."ID", p."Nombre", p."Precio", p."MesesRenovacion", p."CantUsuarios"
		  FROM "Plan" p
		 WHERE 1=1
		""";

	private static final String UPDATE = """
			UPDATE "Plan"
			SET %s
			WHERE "ID"=?;
			""";

	private static final String SET_NOMBRE = """
			"Nombre"=?
			""";
	private static final String SET_PRECIO = """
			"Precio"=?
			""";
	private static final String SET_CANT_USUARIOS = """
			"CantUsuarios"=?
			""";
	private static final String SET_MESES_RENOVACION = """
			"MesesRenovacion"=?
			""";

	private static final String WHERE_ID = "AND p.\"ID\" = ?\n";
	private static final String DELETE = """
		DELETE FROM "Plan"
		 WHERE "ID" = ?
		""";
	private static final String INSERT = """
		INSERT INTO "Plan"(
		    "Nombre", "Precio", "MesesRenovacion",
		    "CantUsuarios"
		) VALUES (?, ?, ?, ?);
		""";

	@Inject
	PlanDao(DataSource ds) {
		super(ds);
	}

	public Optional<Plan> get(
		Transaction t,
		Long id
	) throws PersistenceException {
		try (
			val select = createSelect(t, id);
			val rs = select.executeQuery()
		) {
			if ( !rs.next() ) return Optional.empty();

			return Optional.of(buildPlan(rs));
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting plan", e);
		}
	}

	public List<Plan> list(
		Transaction t
	) throws PersistenceException {
		List<Plan> responseList = new ArrayList<>();
		try (
			val select = createSelect(t, null);
			val rs = select.executeQuery()
		) {
			while (rs.next()) {
				responseList.add(buildPlan(rs));
			}
			return responseList;
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting plan list", e);
		}
	}

	Plan buildPlan(ResultSet rs) throws SQLException {
		return Plan.builder()
						 .id(rs.getLong("ID"))
						 .cantUsuarios(rs.getInt("CantUsuarios"))
						 .nombre(rs.getString("Nombre"))
						 .precio(rs.getBigDecimal("Precio"))
						 .mesesRenovacion(rs.getInt("MesesRenovacion"))
						 .build();
	}

	private PreparedStatement createSelect(Transaction t, Long id) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT);
		List<Object> params = new ArrayList<>();

		if(id != null) {
			b.append(WHERE_ID);
			params.add(id);
		}

		return t.prepareStatement(b.toString(), params);
	}

	public Plan save(Transaction t, Plan p) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setString(1, p.nombre);
			insert.setBigDecimal(2, p.precio);
			insert.setInt(3, p.mesesRenovacion);
			insert.setInt(4, p.cantUsuarios);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the plan failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next()) p.id = rs.getLong(1);
				else throw new SQLException("Creating the plan failed, no ID obtained");
			}

			return p;
		} catch (SQLException e) {
			throw new PersistenceException("error inserting plan", e);
		}
	}

	public void update(Transaction t, Plan p) throws NotFoundException, PersistenceException {
		try (var update = createUpdate(t, p)) {
			int insertions = update.executeUpdate();
			if (insertions == 0) {
				throw new NotFoundException("No existe el plan a actualizar.");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error updating plan", e);
		}
	}

	private PreparedStatement createUpdate(Transaction t, Plan p) throws PersistenceException, SQLException {
		val set = new ArrayList<String>();
		List<Object> parameters = new ArrayList<>();

		if(p.nombre != null) {
			set.add(SET_NOMBRE);
			parameters.add(p.nombre);
		}

		if(p.precio != null) {
			set.add(SET_PRECIO);
			parameters.add(p.precio);
		}

		if(p.cantUsuarios != null) {
			set.add(SET_CANT_USUARIOS);
			parameters.add(p.cantUsuarios);
		}

		if(p.mesesRenovacion != null) {
			set.add(SET_MESES_RENOVACION);
			parameters.add(p.mesesRenovacion);
		}

		parameters.add(p.id);

		val sets = String.join(", ", set);
		val sql = String.format(UPDATE, sets);
		var ps = t.prepareStatement(sql);
		for (int i = 0; i < parameters.size(); i++)
			ps.setObject(i + 1, parameters.get(i));
		return ps;
	}

	public void delete(Transaction t, Long id) throws PersistenceException, NotFoundException {
		try ( val delete = t.prepareStatement(DELETE) ) {
			delete.setLong(1, id);

			if (delete.executeUpdate() <= 0 )
				throw new NotFoundException("deleting the plan failed, no affected rows");

		} catch (SQLException e) {
			throw new PersistenceException("error deleting plan", e);
		}
	}
}
