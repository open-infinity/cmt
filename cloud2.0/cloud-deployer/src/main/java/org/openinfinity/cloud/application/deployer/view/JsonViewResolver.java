package org.openinfinity.cloud.application.deployer.view;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.openinfinity.cloud.application.deployer.model.DeploymentTableData;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class JsonViewResolver implements ModelAndViewResolver {
	@Override
	public ModelAndView resolveModelAndView(Method arg0, Class arg1, Object returnValue, ExtendedModelMap arg3, NativeWebRequest arg4) {
		/*
		 * Return a special ModelAndView if our method returns YourAjaxResultTypeHere.
		 */
		if(returnValue instanceof Collection<?>) {
			Iterator iterator = ((Collection)returnValue).iterator();
			if (iterator.hasNext() && !(iterator.next() instanceof DeploymentTableData)){
				return UNRESOLVED;
			}
			ModelAndView mav = new ModelAndView();
			MappingJacksonJsonView v = new MappingJacksonJsonView();
			v.setBeanName("deployments");
			
			mav.setView(v);
			mav.addObject("deployments", returnValue);
			return mav;
		}
		
		/*
		 * Otherwise just do the default thing.
		 */
		return UNRESOLVED;
	}

}
