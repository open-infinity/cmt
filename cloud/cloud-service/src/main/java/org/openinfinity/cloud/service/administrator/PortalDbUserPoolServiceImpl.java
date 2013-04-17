package org.openinfinity.cloud.service.administrator;

import org.openinfinity.cloud.domain.repository.administrator.PortalDbUserRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 15.3.2013
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
@Service("portalDbUserPoolService")
public class PortalDbUserPoolServiceImpl implements PortalDbUserPoolService {

    @Autowired
    @Qualifier("portalDbUserRepository")
    private PortalDbUserRepository repository;

    @Override
    @Log
    @Transactional("userPool")
    public String getNextFreeUserid() {
        return repository.getNextFreeUserid();
    }

	@Override
    @Log
    @Transactional("userPool")
	public String releaseUserid(String userid) {
		return repository.releaseUserid(userid);
	}
    
}
