package org.openinfinity.cloud.application.invoicing.model.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;

/**
 * Liferay user implemantation, wraps actual liferay user
 * @author kilpopas
 *
 */
public class LiferayUserImpl implements User{
    public LiferayUserImpl(com.liferay.portal.model.User user) {
        super();
        this.user = user;
    }

    private com.liferay.portal.model.User user;

    public List<Organization> getOrganizations() throws PortalException,
    SystemException {
        return user.getOrganizations();
    }

    public long getUserId() {
        return user.getUserId();
    }

    //TODO: return also all suborganizations for this user (see implementation in method getInstances for the cloud admin controller)
    public Collection<Long> getOrganizationIds() {
        Collection<Long> ret=null;
        try {
            long[] organizationIds = user.getOrganizationIds();
            Long[] ids=ArrayUtils.toObject(organizationIds);
            ret=Arrays.asList(ids);
        } catch (PortalException e) {
            throw new RuntimeException(e);
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
        return ret;

    }
}
