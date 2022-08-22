package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.exceptions.DbException;
import db.exceptions.DbIntegrityException;
import db.exceptions.EntityNotFoundException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoImplJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoImplJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("INSERT INTO department (Name) " + "VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			int rows = st.executeUpdate();
			if (rows > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				conn.commit();
			} else {
				conn.rollback();
				throw new DbException("Unexpected error: no rows affected");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("UPDATE department SET Name = ? WHERE department.id = ?;");
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			int rows = st.executeUpdate();
			if (rows > 0) {
				conn.commit();
			} else {
				conn.rollback();
				throw new DbException("Unexpected error: no rows affected");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE FROM department WHERE department.id = ?;");
			st.setInt(1, id);
			int rows = st.executeUpdate();
			if (rows > 0) {
				conn.commit();
			} else {
				conn.rollback();
				throw new DbException("Unexpected error: no rows affected");
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (e.getClass() == SQLIntegrityConstraintViolationException.class) {
				throw new DbIntegrityException(e.getMessage());
			}
			e.printStackTrace();
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM department WHERE department.Id = ?;");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				return instantiateDepartment(rs);
			} else {
				throw new EntityNotFoundException("Department with Id = " + id + " was not found");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		return null;
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM department;");
			rs = st.executeQuery();
			List<Department> list = new ArrayList<>();
			while (rs.next()) {
				list.add(instantiateDepartment(rs));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		return null;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("Id"), rs.getString("Name"));
	}
}
