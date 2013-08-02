package org.openinfinity.cloud.application.invoicing.model.user;

import java.util.Collection;

public interface User {
    
    public long getUserId();
    
    public Collection<Long> getOrganizationIds();

}
