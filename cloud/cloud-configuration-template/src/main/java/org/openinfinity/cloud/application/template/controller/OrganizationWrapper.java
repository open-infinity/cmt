package org.openinfinity.cloud.application.template.controller;

import com.liferay.portal.model.Organization;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;

@Component
public class OrganizationWrapper {

    private Collection<OrganizationInfo> availableOrganizations;

    private Collection<OrganizationInfo> selectedOrganizations;

    public OrganizationWrapper() {
    }

    public OrganizationWrapper(Collection<OrganizationInfo> availableOrganizations, Collection<OrganizationInfo> selectedOrganizations) {
        this.availableOrganizations = availableOrganizations;
        this.selectedOrganizations = selectedOrganizations;
    }

    public void construct(Collection<Organization> available, Collection<Organization> selected){
        this.availableOrganizations = createfromOrganizations(available);
        this.selectedOrganizations = createfromOrganizations(selected);
    }

    private Collection<OrganizationInfo> createfromOrganizations(Collection<Organization> organizations){
        LinkedList<OrganizationInfo> organizationInfos = new LinkedList<OrganizationInfo>();
        for (Organization o :organizations){
            organizationInfos.add(new OrganizationInfo(o.getOrganizationId(), o.getCompanyId(), o.getParentOrganizationId(), o.getName(), o.getType(), o.isRecursable(), o.getRegionId(), o.getCountryId(), o.getStatusId(), o.getComments()));
        }
        return organizationInfos;
    }

    public Collection<OrganizationInfo> getAvailableOrganizations() {
        return availableOrganizations;
    }

    public void setAvailableOrganizations(Collection<OrganizationInfo> availableOrganizations) {
        this.availableOrganizations = availableOrganizations;
    }

    public Collection<OrganizationInfo> getSelectedOrganizations() {
        return selectedOrganizations;
    }

    public void setSelectedOrganizations(Collection<OrganizationInfo> selectedOrganizations) {
        this.selectedOrganizations = selectedOrganizations;
    }

}
