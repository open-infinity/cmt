package org.openinfinity.cloud.service.administrator;

import java.util.List;

import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.domain.repository.administrator.KeyRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Security key service handling interface implementation. 
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 */
@Service("keyService")
public class KeyServiceImpl implements KeyService {

	@Autowired
	@Qualifier("keyRepository")
	private KeyRepository keyRepository;
	
	@Log
	public Key getKey(int id) {
		return keyRepository.getKey(id);
	}
	
	@Log
	public Key getKeyByInstanceId(int instanceId) {
		return keyRepository.getKeyForInstance(instanceId);
	}
	
	@Log
	public List<Key> getKeys() {
		return keyRepository.getKeys();
	}
	
	@Log
	public void addKey(Key key) {
		keyRepository.addKey(key);
	}
	
	@Log
	public void deleteKey(int instanceId) {
		keyRepository.removeKeyByInstanceId(instanceId);
	}
	
}
