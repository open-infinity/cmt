package org.openinfinity.cloud.util;

import com.liferay.portal.model.User;

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


    private String userName;
    private String userOrg;
    private List<String> orgs;
    private long userId;

    private UserMock userMock;

    public LiferayServiceMock() {
    }

    public void addUser(String userName, long userId, String userOrg, String... subOrgs) {
        this.userName = userName;
        this.userOrg = userOrg;
        this.userId = userId;
        orgs = new LinkedList<String>();
        orgs.add(userOrg);
        orgs.addAll(Arrays.asList(subOrgs));

        userMock = new UserMock();
        userMock.setUserId(userId);
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
        return orgs;
    }


}
