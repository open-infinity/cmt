package org.openinfinity.cloud.service.administrator;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 15.3.2013
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public interface PortalDbUserPoolService {

    /**
     * Returns next free userid or null if all users are reserved
     * @return userid or null if all users are taken
     */
    public String getNextFreeUserid();

    /**
     * Release given userid to pool.
     * @return released userid 
     */
    public String releaseUserid(String userid);
}
