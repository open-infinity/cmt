package org.openinfinity.cloud.domain.repository.common;

import java.math.BigInteger;
import java.util.Collection;

public  interface AbstractIndexMapperRepositoryInterface<T extends Object>{

	public T create(T product);
	
	public void update(T product);
	
	public Collection<T> loadAll();
	
	public T load(BigInteger id);
	
	public void delete(T product);


    Collection<T> loadAllForTemplate(int index1);

    void create(int index1, int index2);

    void deleteByTemplate(int templateId);
	
}
