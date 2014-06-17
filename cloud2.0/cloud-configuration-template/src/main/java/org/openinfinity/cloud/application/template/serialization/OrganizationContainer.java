package org.openinfinity.cloud.application.template.serialization;

import com.liferay.portal.model.Organization;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Component
public class OrganizationContainer {

    private Collection<OrganizationInfo> available;

    private Collection<OrganizationInfo> selected;

    public OrganizationContainer() {
    }

    public OrganizationContainer(Collection<OrganizationInfo> availableOrganizations, Collection<OrganizationInfo> selectedOrganizations) {
        this.available = availableOrganizations;
        this.selected = selectedOrganizations;
    }

    public OrganizationContainer construct(Collection<Organization> available, Collection<Organization> selected){
        this.available = createfromOrganizations(available);
        this.selected = createfromOrganizations(selected);
        return this;
    }

    public OrganizationContainer(List<Organization> available, List<Organization> selected){
        this.available = createfromOrganizations(available);
        this.selected = createfromOrganizations(selected);
    }

    private Collection<OrganizationInfo> createfromOrganizations(Collection<Organization> organizations){
        LinkedList<OrganizationInfo> organizationInfos = new LinkedList<OrganizationInfo>();
        for (Organization o :organizations){
            organizationInfos.add(new OrganizationInfo(o.getOrganizationId(), o.getCompanyId(), o.getParentOrganizationId(), o.getName(), o.getType(), o.isRecursable(), o.getRegionId(), o.getCountryId(), o.getStatusId(), o.getComments()));
        }
        return organizationInfos;
    }

    public Collection<OrganizationInfo> getAvailable() {
        return available;
    }

    public void setAvailable(Collection<OrganizationInfo> available) {
        this.available = available;
    }

    public Collection<OrganizationInfo> getSelected() {
        return selected;
    }

    public void setSelected(Collection<OrganizationInfo> selected) {
        this.selected = selected;
    }

}
