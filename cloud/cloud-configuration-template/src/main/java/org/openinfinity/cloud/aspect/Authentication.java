package org.openinfinity.cloud.aspect;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.openinfinity.cloud.application.template.controller.ElementController;
import org.openinfinity.cloud.comon.web.LiferayService;
import org.openinfinity.core.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;

import com.liferay.portal.model.User;

@Aspect
public class Authentication {
    private static final Logger LOG = Logger.getLogger(Authentication.class.getName());

	@Autowired
    private LiferayService liferayService;
	
	@Pointcut("@annotation(authenticated) && args(request, response,..)")
	void liferayAuthentication(ResourceRequest request, ResourceResponse response){}
	
	@Before("liferayAuthentication(request, response)")
	public void doLiferayAuthentication(ResourceRequest request, ResourceResponse response) {
		User user = liferayService.getUser(request, response);
		LOG.debug("**************** Advicing **********************");
		
		if (user == null) throw new SystemException("User authentication failed");
  }
}
