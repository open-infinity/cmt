package org.openinfinity.cloud.application.invoicing.model.user;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;

/**
 * Dummy user implementation, enables development time running at servlet container
 * @author kilpopas
 *
 */
public class DummyUserImpl implements User{

    @Override
    public long getUserId() {
        return 10710;
    }

    @Override
    public Collection<Long> getOrganizationIds() {
        long[] organizationIds={10495};
        Long[] ids=ArrayUtils.toObject(organizationIds);
        
        return Arrays.asList(ids);
    }

}
