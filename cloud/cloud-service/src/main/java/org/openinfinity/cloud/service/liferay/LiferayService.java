package org.openinfinity.cloud.service.liferay;

import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.model.User;

public interface LiferayService {
    User getUser(PortletRequest request);

    User getUser(PortletRequest request, ResourceResponse response);

    // FIXME: use Collection
    List<String> getOrganizationNames(User user);
    
    List<Long> getOrganizationIds(User user);
}
