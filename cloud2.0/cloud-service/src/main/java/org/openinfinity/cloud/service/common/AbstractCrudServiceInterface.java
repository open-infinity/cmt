package org.openinfinity.cloud.service.common;

import java.math.BigInteger;
import java.util.Collection;

public abstract interface AbstractCrudServiceInterface<T extends Object> {

	public T create(T obj);
	
	public void update(T obj);
	
	public Collection<T> loadAll();
	
	public T load(BigInteger id);
	
	public void delete (T obj);

    void delete(BigInteger id);
	
}
