package org.openinfinity.cloud.util;

import com.liferay.portal.model.User;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import java.util.List;

/**
 * Interface for accessing Liferay services
 * User: tapantim
 * Date: 20.2.2013
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
public interface LiferayService {

    public User getUser(PortletRequest request);

    public User getUser(PortletRequest request, ResourceResponse response);

    public List<String> getOrganizationNames(User user);
}
