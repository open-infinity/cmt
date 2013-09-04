package org.openinfinity.cloud.service.liferay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Service;

import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.service.liferay.LiferayServiceImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

@Service("liferayService")
public class LiferayServiceImpl implements LiferayService{

private static final Logger LOG = Logger.getLogger(LiferayServiceImpl.class.getName());
    
    public User getUser(PortletRequest request) {
        User user = null;
        
        try {
            user = com.liferay.portal.util.PortalUtil.getUser(request);
        } catch (PortalException e) {
            LOG.error("User not found, not logged in? "+e.getLocalizedMessage());
            return null;
        } catch (SystemException e) {
            LOG.error("Someting is wrong: "+e.getLocalizedMessage());
            return null;
        }
        
        return user;    
    }
    
    public User getUser(PortletRequest request, ResourceResponse response) {
        User user = getUser(request);
        if(user == null) 
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_USER_NOT_LOGGED_IN);
        return user;    
    }

    public Collection<String> getOrganizationNames(User user) {
        List<Organization> userOrganizations = null;
        List<Organization> subOrganizations = null;
        try {
            userOrganizations = user.getOrganizations();
            subOrganizations = OrganizationLocalServiceUtil.getSuborganizations(userOrganizations);
        } catch (Exception e) {
            throw new RuntimeException("user organizations or suborganizations could not be obtained", e);
        }

        List<String> orgNames = new LinkedList<String>();
        for (Organization org : userOrganizations)
            orgNames.add(org.getName());
        for (Organization subOrg : subOrganizations)
            orgNames.add(subOrg.getName());
        return orgNames;
    }
    
    public Collection<Long> getOrganizationIds(User user){
        List<Organization> organizationList = null;
        List<Organization> subOrganizationList = null;
        try {
            organizationList = user.getOrganizations();
            subOrganizationList = OrganizationLocalServiceUtil.getSuborganizations(organizationList);
        } catch (PortalException e) {
            LOG.error("Could not get organizations for user "+user.getFullName()+": "+e.getLocalizedMessage());
        } catch (SystemException e) {
            LOG.error("Something is wrong: "+e.getLocalizedMessage());
        }
        Collection<Long> orgIdList = new ArrayList<Long>();
        if (organizationList != null) {
            for (Organization organization : organizationList) {
                LOG.info("Adding organization " + organization.getName() + " to user " + user.getScreenName());
                orgIdList.add(organization.getOrganizationId());
            }
        }
        if (subOrganizationList != null) {
            for (Organization organization : subOrganizationList) {
                LOG.info("Adding organization " + organization.getName() + " to user " + user.getScreenName());
                orgIdList.add(organization.getOrganizationId());
            }
        }
        return orgIdList;
    }
    
}
