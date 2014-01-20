package org.openinfinity.cloud.common.web;

import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import java.util.List;

public interface LiferayService {
    User getUser(PortletRequest request);

    User getUser(PortletRequest request, ResourceResponse response);

    // FIXME: use Collection
    List<String> getOrganizationNames(User user);
    
    List<Long> getOrganizationIds(User user);

    List<Organization> getOrganizations(User user);
}