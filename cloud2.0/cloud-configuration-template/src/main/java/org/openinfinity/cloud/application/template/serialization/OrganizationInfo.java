package org.openinfinity.cloud.application.template.serialization;


public class OrganizationInfo {
    long organizationId;
    long companyId;
    long parentOrganizationId;
    String name;
    String type;
    boolean recursable;
    long regionId;
    long countryId;
    int statusId;
    String comments;

    public OrganizationInfo(long organizationId, long companyId, long parentOrganizationId, String name, String type, boolean recursable, long regionId, long countryId, int statusId, String comments) {
        this.organizationId = organizationId;
        this.companyId = companyId;
        this.parentOrganizationId = parentOrganizationId;
        this.name = name;
        this.type = type;
        this.recursable = recursable;
        this.regionId = regionId;
        this.countryId = countryId;
        this.statusId = statusId;
        this.comments = comments;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRecursable() {
        return recursable;
    }

    public void setRecursable(boolean recursable) {
        this.recursable = recursable;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
