package model.dao;

import java.util.List;

public interface Dao<T, I> {

	void insert(T obj);
	
	void update(T obj);
	
	void deleteById(I id);
	
	T findById(I id);
	
	List<T> findAll();
}
