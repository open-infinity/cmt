package org.openinfinity.cloud.service.liferay;

import java.util.Collection;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import com.liferay.portal.model.User;

public interface LiferayService {
    User getUser(PortletRequest request);

    User getUser(PortletRequest request, ResourceResponse response);

    Collection<String> getOrganizationNames(User user);
    
    Collection<Long> getOrganizationIds(User user);
}
