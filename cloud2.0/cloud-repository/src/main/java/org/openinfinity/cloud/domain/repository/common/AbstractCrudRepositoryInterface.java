package org.openinfinity.cloud.domain.repository.common;

import java.math.BigInteger;
import java.util.Collection;

public  interface AbstractCrudRepositoryInterface<T extends Object> /**extends Repository<T, BigInteger>*/ {

	public T create(T item);
	
	public void update(T item);
	
	public Collection<T> loadAll();
	
	public T load(BigInteger id);
	
	public void delete (T item);

    void delete(BigInteger id);

}
