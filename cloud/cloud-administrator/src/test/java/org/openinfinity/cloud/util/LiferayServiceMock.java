package org.openinfinity.cloud.util;

import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import org.openinfinity.cloud.comon.web.LiferayService;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 20.2.2013
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public class LiferayServiceMock implements LiferayService {

    private List<String> userOrgs;
    private UserMock userMock;

    public LiferayServiceMock() {
    }

    public void mockUserWithOrganizations(String userOrg, String... subOrgs) {
        userOrgs = new LinkedList<String>();
        userOrgs.add(userOrg);
        userOrgs.addAll(Arrays.asList(subOrgs));

        userMock = new UserMock();
    }

    @Override
    public User getUser(PortletRequest request) {
        return userMock;
    }

    @Override
    public User getUser(PortletRequest request, ResourceResponse response) {
        return userMock;
    }

    @Override
    public List<String> getOrganizationNames(User user) {
        return userOrgs;
    }

    @Override
    public List<Long> getOrganizationIds(User user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Organization> getOrganizations(User user) {
        return null;
    }


}
