package org.openinfinity.cloud.comon.web;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import org.apache.log4j.Logger;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.stereotype.Service;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    public List<String> getOrganizationNames(User user) {
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
    
    public List<Long> getOrganizationIds(User user){
        List<Organization> organizationList = null;
        List<Organization> subOrganizationList = null;
        List<Long> orgIdList = new ArrayList<Long>();
        try {
            organizationList = user.getOrganizations();
            subOrganizationList = OrganizationLocalServiceUtil.getSuborganizations(organizationList);
            for (Organization organization : organizationList) {
                orgIdList.add(organization.getOrganizationId());
            }
            for (Organization organization : subOrganizationList) {
                orgIdList.add(organization.getOrganizationId());
            }
        } catch (Exception e) {
            ExceptionUtil.throwSystemException("Error getting organizations for user " + user.getFullName());
        } 
        return orgIdList;
    }

    public List<Organization> getOrganizations(User user) {
        List<Organization> userOrganizations = null;
        List<Organization> subOrganizations = null;
        try {
            userOrganizations = user.getOrganizations();
            subOrganizations = OrganizationLocalServiceUtil.getSuborganizations(userOrganizations);
            userOrganizations.addAll(subOrganizations);
        } catch (Exception e) {
            ExceptionUtil.throwSystemException("Error getting organizations for user " + user.getFullName());
        }
        return userOrganizations;
    }
}
