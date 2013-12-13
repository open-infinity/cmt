package org.openinfinity.cloud.application.template.controller;

import org.openinfinity.core.exception.AbstractCoreException;
import org.openinfinity.core.exception.ApplicationException;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.portlet.ModelAndView;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.Map;

/**
 * Abstract class for Spring controllers
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
public class AbstractController {
    @ExceptionHandler({ApplicationException.class, BusinessViolationException.class,
            SystemException.class})
    public ModelAndView handleException(RenderRequest renderRequest, RenderResponse renderResponse,
                                        AbstractCoreException abstractCoreException) {
        ModelAndView modelAndView = new ModelAndView("error");
        if (abstractCoreException.isErrorLevelExceptionMessagesIncluded())
            modelAndView.addObject("errorLevelExceptions",
                    abstractCoreException.getErrorLevelExceptionIds());
        if (abstractCoreException.isWarningLevelExceptionMessagesIncluded())
            modelAndView.addObject("warningLevelExceptions",
                    abstractCoreException.getWarningLevelExceptionIds());
        if (abstractCoreException.isInformativeLevelExceptionMessagesIncluded())
            modelAndView.addObject("informativeLevelExceptions",
                    abstractCoreException.getInformativeLevelExceptionIds());

        // TODO
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>)renderRequest.getAttribute(ActionRequest.USER_INFO);
        if (userInfo == null) return new ModelAndView("error");

        return modelAndView;
    }
}
