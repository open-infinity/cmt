package org.openinfinity.cloud.application.invoicing.utility;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Helper class to get Spring resources from Spring ApplicationContext. Typical
 * usage case is when class is not Spring aware (not annotated with spring
 * annotations) in which case Autowired annotation cannot be used
 * <p>
 * Example usage:
 * <p>
 * MessageResources
 * messageResources=ApplicationContextProvider.getContext().getBean
 * (MessageResources.class);
 *
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * Sets application context, called by Spring
     */
    public final void setApplicationContext(final ApplicationContext ctx)
            throws BeansException {
        ApplicationContextProvider.setContext(ctx);
    }

    private static void setContext(ApplicationContext context) {
        ApplicationContextProvider.context = context;
    }

    /**
     * Returns application context.
     * @return ApplicationContext
     */
    public static ApplicationContext getContext() {
        return context;
    }

}
