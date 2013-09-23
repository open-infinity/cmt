package org.openinfinity.domain.service;

import java.math.BigInteger;
import java.util.Collection;

public abstract interface AbstractCrudServiceInterface<T extends Object> {

	public T create(T product);
	
	public void update(T product);
	
	public Collection<T> loadAll();
	
	public T loadById(BigInteger id);
	
	public void delete (T product);
	
}
