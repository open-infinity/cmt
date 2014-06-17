package org.openinfinity.cloud.common.aspect;

import com.liferay.portal.model.User;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.openinfinity.cloud.common.web.LiferayService;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

@Aspect
public class Authentication {
    private static final Logger LOG = Logger.getLogger(Authentication.class.getName());

    @Autowired
    private LiferayService liferayService;

    @Pointcut("@annotation(org.openinfinity.cloud.common.annotation.Authenticated) && args(request, response,..)")
    void authenticated(ResourceRequest request, ResourceResponse response){}

    @Before("authenticated(request, response)")
    public void authenticateUser(ResourceRequest request, ResourceResponse response) {
        User user = liferayService.getUser(request, response);
        if (user == null) ExceptionUtil.throwSystemException("User authentication with Liferay failed");
    }
}
