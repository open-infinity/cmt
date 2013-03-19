package org.openinfinity.cloud.domain.repository.administrator;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 15.3.2013
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
public interface PortalDbUserRepository {

    /**
     * Returns next free userid or null if all ids are taken
     * @return userid or null if no userid is available
     */
    public String getNextFreeUserid();
}
