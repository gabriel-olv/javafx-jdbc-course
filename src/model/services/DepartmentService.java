package model.services;

import java.util.List;

import model.dao.DepartmentDao;
import model.dao.FactoryDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao departmentDao = FactoryDao.createDepartmentDao();

	public List<Department> findAll() {
		return departmentDao.findAll();
	}
}
