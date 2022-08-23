package model.services;

import java.util.List;

import model.dao.SellerDao;
import model.dao.FactoryDao;
import model.entities.Seller;

public class SellerService {

	private SellerDao sellerDao = FactoryDao.createSellerDao();

	public List<Seller> findAll() {
		return sellerDao.findAll();
	}
	
	public void SaveOrUpdate(Seller obj) {
		if (obj.getId() == null) {
			sellerDao.insert(obj);
		}
		else {
			sellerDao.update(obj);
		}
	}
	
	public void remove(Seller obj) {
		sellerDao.deleteById(obj.getId());
	}
}
