package org.openinfinity.cloud.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.*;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.model.ExpandoBridge;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 20.2.2013
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
public class UserMock implements User {

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getBirthday() throws PortalException, SystemException {
        return null;
    }

    @Override
    public String getCompanyMx() throws PortalException, SystemException {
        return null;
    }

    @Override
    public Contact getContact() throws PortalException, SystemException {
        return null;
    }

    @Override
    public long getPrimaryKey() {
        return 0;
    }

    @Override
    public void setPrimaryKey(long pk) {
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public void setUuid(String uuid) {
    }

    @Override
    public long getUserId() {
        return 0;
    }

    @Override
    public void setUserId(long userId) {
    }

    @Override
    public String getUserUuid() throws SystemException {
        return null;
    }

    @Override
    public void setUserUuid(String userUuid) {
    }

    @Override
    public long getCompanyId() {
        return 0;
    }

    @Override
    public void setCompanyId(long companyId) {
    }

    @Override
    public Date getCreateDate() {
        return null;
    }

    @Override
    public void setCreateDate(Date createDate) {
    }

    @Override
    public Date getModifiedDate() {
        return null;
    }

    @Override
    public void setModifiedDate(Date modifiedDate) {
    }

    @Override
    public boolean getDefaultUser() {
        return false;
    }

    @Override
    public boolean isDefaultUser() {
        return false;
    }

    @Override
    public void setDefaultUser(boolean defaultUser) {
    }

    @Override
    public long getContactId() {
        return 0;
    }

    @Override
    public void setContactId(long contactId) {
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(String password) {
    }

    @Override
    public boolean getPasswordEncrypted() {
        return false;
    }

    @Override
    public boolean isPasswordEncrypted() {
        return false;
    }

    @Override
    public void setPasswordEncrypted(boolean passwordEncrypted) {
    }

    @Override
    public boolean getPasswordReset() {
        return false;
    }

    @Override
    public boolean isPasswordReset() {
        return false;
    }

    @Override
    public void setPasswordReset(boolean passwordReset) {
    }

    @Override
    public Date getPasswordModifiedDate() {
        return null;
    }

    @Override
    public void setPasswordModifiedDate(Date passwordModifiedDate) {
    }

    @Override
    public String getDigest() {
        return null;
    }

    @Override
    public void setDigest(String digest) {
    }

    @Override
    public String getReminderQueryQuestion() {
        return null;
    }

    @Override
    public void setReminderQueryQuestion(String reminderQueryQuestion) {
    }

    @Override
    public String getReminderQueryAnswer() {
        return null;
    }

    @Override
    public void setReminderQueryAnswer(String reminderQueryAnswer) {
    }

    @Override
    public int getGraceLoginCount() {
        return 0;
    }

    @Override
    public void setGraceLoginCount(int graceLoginCount) {
    }

    @Override
    public String getScreenName() {
        return null;
    }

    @Override
    public void setScreenName(String screenName) {
    }

    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public void setEmailAddress(String emailAddress) {
    }

    @Override
    public long getFacebookId() {
        return 0;
    }

    @Override
    public void setFacebookId(long facebookId) {
    }

    @Override
    public String getOpenId() {
        return null;
    }

    @Override
    public void setOpenId(String openId) {
    }

    @Override
    public long getPortraitId() {
        return 0;
    }

    @Override
    public void setPortraitId(long portraitId) {
    }

    @Override
    public String getLanguageId() {
        return null;
    }

    @Override
    public String getDigest(String password) {
        return null;
    }

    @Override
    public String getDisplayEmailAddress() {
        return null;
    }

    @Override
    public String getDisplayURL(ThemeDisplay themeDisplay) throws PortalException, SystemException {
        return null;
    }

    @Override
    public String getDisplayURL(String portalURL, String mainPath) throws PortalException, SystemException {
        return null;
    }

    @Override
    public boolean getFemale() throws PortalException, SystemException {
        return false;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public Group getGroup() throws PortalException, SystemException {
        return null;
    }

    @Override
    public long[] getGroupIds() throws PortalException, SystemException {
        return new long[0];
    }

    @Override
    public List<Group> getGroups() throws PortalException, SystemException {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public String getLogin() throws PortalException, SystemException {
        return null;
    }

    @Override
    public boolean getMale() throws PortalException, SystemException {
        return false;
    }

    @Override
    public List<Group> getMyPlaces() throws PortalException, SystemException {
        return null;
    }

    @Override
    public List<Group> getMyPlaces(int max) throws PortalException, SystemException {
        return null;
    }

    @Override
    public long[] getOrganizationIds() throws PortalException, SystemException {
        return new long[0];
    }

    @Override
    public List<Organization> getOrganizations() throws PortalException, SystemException {
        return null;
    }

    @Override
    public boolean getPasswordModified() {
        return false;
    }

    @Override
    public PasswordPolicy getPasswordPolicy() throws PortalException, SystemException {
        return null;
    }

    @Override
    public String getPasswordUnencrypted() {
        return null;
    }

    @Override
    public int getPrivateLayoutsPageCount() throws PortalException, SystemException {
        return 0;
    }

    @Override
    public int getPublicLayoutsPageCount() throws PortalException, SystemException {
        return 0;
    }

    @Override
    public Set<String> getReminderQueryQuestions() throws PortalException, SystemException {
        return null;
    }

    @Override
    public long[] getRoleIds() throws SystemException {
        return new long[0];
    }

    @Override
    public List<Role> getRoles() throws SystemException {
        return null;
    }

    @Override
    public double getSocialContributionEquity() {
        return 0;
    }

    @Override
    public double getSocialParticipationEquity() {
        return 0;
    }

    @Override
    public double getSocialPersonalEquity() {
        return 0;
    }

    @Override
    public long[] getTeamIds() throws SystemException {
        return new long[0];
    }

    @Override
    public List<Team> getTeams() throws SystemException {
        return null;
    }

    @Override
    public long[] getUserGroupIds() throws SystemException {
        return new long[0];
    }

    @Override
    public List<UserGroup> getUserGroups() throws SystemException {
        return null;
    }

    @Override
    public TimeZone getTimeZone() {
        return null;
    }

    @Override
    public boolean hasCompanyMx() throws PortalException, SystemException {
        return false;
    }

    @Override
    public boolean hasCompanyMx(String emailAddress) throws PortalException, SystemException {
        return false;
    }

    @Override
    public boolean hasMyPlaces() throws SystemException {
        return false;
    }

    @Override
    public boolean hasOrganization() throws PortalException, SystemException {
        return false;
    }

    @Override
    public boolean hasPrivateLayouts() throws PortalException, SystemException {
        return false;
    }

    @Override
    public boolean hasPublicLayouts() throws PortalException, SystemException {
        return false;
    }

    @Override
    public boolean hasReminderQuery() {
        return false;
    }

    @Override
    public boolean isFemale() throws PortalException, SystemException {
        return false;
    }

    @Override
    public boolean isMale() throws PortalException, SystemException {
        return false;
    }

    @Override
    public boolean isPasswordModified() {
        return false;
    }

    @Override
    public void setLanguageId(String languageId) {
    }

    @Override
    public String getTimeZoneId() {
        return null;
    }

    @Override
    public void setPasswordModified(boolean passwordModified) {
    }

    @Override
    public void setPasswordUnencrypted(String passwordUnencrypted) {
    }

    @Override
    public void setTimeZoneId(String timeZoneId) {
    }

    @Override
    public String getGreeting() {
        return null;
    }

    @Override
    public void setGreeting(String greeting) {
    }

    @Override
    public String getComments() {
        return null;
    }

    @Override
    public void setComments(String comments) {
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public void setFirstName(String firstName) {
    }

    @Override
    public String getMiddleName() {
        return null;
    }

    @Override
    public void setMiddleName(String middleName) {
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public void setLastName(String lastName) {
    }

    @Override
    public String getJobTitle() {
        return null;
    }

    @Override
    public void setJobTitle(String jobTitle) {
    }

    @Override
    public Date getLoginDate() {
        return null;
    }

    @Override
    public void setLoginDate(Date loginDate) {
    }

    @Override
    public String getLoginIP() {
        return null;
    }

    @Override
    public void setLoginIP(String loginIP) {
    }

    @Override
    public Date getLastLoginDate() {
        return null;
    }

    @Override
    public void setLastLoginDate(Date lastLoginDate) {
    }

    @Override
    public String getLastLoginIP() {
        return null;
    }

    @Override
    public void setLastLoginIP(String lastLoginIP) {
    }

    @Override
    public Date getLastFailedLoginDate() {
        return null;
    }

    @Override
    public void setLastFailedLoginDate(Date lastFailedLoginDate) {
    }

    @Override
    public int getFailedLoginAttempts() {
        return 0;
    }

    @Override
    public void setFailedLoginAttempts(int failedLoginAttempts) {
    }

    @Override
    public boolean getLockout() {
        return false;
    }

    @Override
    public boolean isLockout() {
        return false;
    }

    @Override
    public void setLockout(boolean lockout) {
    }

    @Override
    public Date getLockoutDate() {
        return null;
    }

    @Override
    public void setLockoutDate(Date lockoutDate) {
    }

    @Override
    public boolean getAgreedToTermsOfUse() {
        return false;
    }

    @Override
    public boolean isAgreedToTermsOfUse() {
        return false;
    }

    @Override
    public void setAgreedToTermsOfUse(boolean agreedToTermsOfUse) {
    }

    @Override
    public boolean getActive() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setActive(boolean active) {
    }

    @Override
    public User toEscapedModel() {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void setNew(boolean n) {
    }

    @Override
    public boolean isCachedModel() {
        return false;
    }

    @Override
    public void setCachedModel(boolean cachedModel) {
    }

    @Override
    public boolean isEscapedModel() {
        return false;
    }

    @Override
    public void setEscapedModel(boolean escapedModel) {
    }

    @Override
    public Serializable getPrimaryKeyObj() {
        return null;
    }

    @Override
    public ExpandoBridge getExpandoBridge() {
        return null;
    }

    @Override
    public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
    }

    @Override
    public int compareTo(User user) {
        return 0;
    }

    @Override
    public String toXmlString() {
        return null;
    }

    @Override
    public void updateSocialContributionEquity(double value) {
    }

    @Override
    public void updateSocialParticipationEquity(double value) {
    }
}
