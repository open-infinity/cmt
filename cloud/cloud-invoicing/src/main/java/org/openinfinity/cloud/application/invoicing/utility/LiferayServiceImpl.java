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

package org.openinfinity.cloud.application.invoicing.utility;

import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.invoicing.model.user.LiferayUserImpl;
import org.openinfinity.cloud.application.invoicing.model.user.User;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

/**
 * Liferay related utilities
 * @author Ossi Hämäläinen
 * @author Pasi Kilponen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
public abstract class LiferayServiceImpl {
    private static final Logger LOG = Logger.getLogger(LiferayServiceImpl.class.getName());

    public static User getUser(PortletRequest request) {
        User user = null;

        try {
            com.liferay.portal.model.User portaluser = com.liferay.portal.util.PortalUtil.getUser(request);
            user=new LiferayUserImpl(portaluser);
        } catch (PortalException e) {
            LOG.error("User not found, not logged in? "+e.getLocalizedMessage());
            return null;
        } catch (SystemException e) {
            LOG.error("Someting is wrong: "+e.getLocalizedMessage());
            return null;
        }

        return user;	
    }


    public static List<String> getOrganizationNames(LiferayUserImpl user) {
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
