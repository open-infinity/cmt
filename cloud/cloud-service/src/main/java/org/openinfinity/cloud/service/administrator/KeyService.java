package org.openinfinity.cloud.service.administrator;

import java.util.List;

import org.openinfinity.cloud.domain.Key;

/**
 * Security key service handling interface. 
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 */
public interface KeyService {

	Key getKey(int id);
	
	Key getKeyByInstanceId(int instanceId);
	
	List<Key> getKeys();
	
	void addKey(Key key);
	
	void deleteKey(int instanceId);
	
}