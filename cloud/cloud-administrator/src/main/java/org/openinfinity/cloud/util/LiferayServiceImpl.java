/*
 * Copyright (c) 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openinfinity.cloud.util;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Liferay related utilities
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Component("liferayService")
public class LiferayServiceImpl implements LiferayService {
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
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, AdminGeneral.HTTP_ERROR_CODE_USER_NOT_LOGGED_IN);
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
}
