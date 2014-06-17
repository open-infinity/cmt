package org.openinfinity.cloud.domain.repository.common;

import java.util.Collection;

public  interface AbstractIndexMapperRepositoryInterface<T extends Object>{

	public T create(T item);
	
	public void update(T item);
	
	public Collection<T> loadAll();
	
	public T load(int id);
	
	public void delete(T item);


    Collection<T> loadAllForTemplate(int index1);

    void create(int index1, int index2);

    void deleteByTemplate(int templateId);
	
}
