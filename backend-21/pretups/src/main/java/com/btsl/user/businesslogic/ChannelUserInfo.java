/**
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.util.*;

import com.btsl.user.businesslogic.entity.Categories;
import com.btsl.user.businesslogic.entity.GeographicalDomainTypes;

/*
import com.comviva.vms.entity.Categories;
import com.comviva.vms.entity.GeographicalDomainTypes;
import com.comviva.vms.entity.Users;

import com.comviva.vms.repository.repositoryimpl.UserRolesVO;
*/import lombok.Getter;
import lombok.Setter;

/**
 * Get ChannelUser model
 *
 * @author VENKATESAN.S
 */

@Getter
@Setter
public class ChannelUserInfo extends UsersInfo {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	private String networkName;
	private String reportHeaderName;
	private String parentName;
	private String ownerName;
	private String contactPersonch;
	private String networkstatus;
	private String language1Message;
	private String language2Message;
	private String sosAllowed;
	private Long sosAllowedAmount;
	private Long sosThresholdLimit;
	private String trannferRuleTypeId;
	private com.btsl.user.businesslogic.entity.Users Users;
	private String userGrade;
	private String userGradeName;
	private String transferProfileID;
	private String transferProfileName;
	private String transferProfileStatus;
	private String commissionProfileSetID;
	private String commissionProfileSetName;
	private String commissionProfileSetVersion;
	private String commissionProfileStatus;
	private String inSuspend;
	private String outSuspened;
	private String geographicalCode;
	private String geographicalDesc;
	private String outletCode;
	private String subOutletCode;
	private String activatedOn;
	private String lmsProfile;
	private String optInOutStatus;
	private String controlGroup;
	private String domainID;
	private String domainTypeCode;
	private String domainName;
	private String staffMsisdn;

	public String getStaffMsisdn() {
		return staffMsisdn;
	}
	public void setStaffMsisdn(String staffMsisdn) {
		this.staffMsisdn = staffMsisdn;
	}
	private int invalidPinCount;
	private String restrictedMsisdnAllow;
	private Categories categories;
	private GeographicalDomainTypes geographicalDomainTypes;
	@SuppressWarnings("rawtypes")
	private ArrayList associatedServiceTypeList;
	private String activeUserID;
	private String statusDesc;
	private String createdByUserName;
	private String language;
	private String countryCode;
	private boolean isStaffUser;
	private String parentLoginId;
	private Date loginTime;
	private String networkId;
	private String domainStatus;
	private String applicationID;
	private String mpayProfileID;
	private String userProfileID;
	private String accessType;
	private String mcommerceServiceAllow;
	private String phoneProfile;
	private String smsPin;
	private String pinReset;
	private long prefixId;
	private String pinRequired;
	private String message;
	@SuppressWarnings("rawtypes")
	private ArrayList menuItemList;
	private Map<String, HashMap<String, ArrayList<UserRolesVO>>> menuItemListNew;
	private transient List<UserGeographiesVO> userGeographiesList;
	private ArrayList<UserGeographies> geographicalAreaList;
	@SuppressWarnings("rawtypes")
	private ArrayList domainList;
	@SuppressWarnings("rawtypes")
	private ArrayList serviceList;
	@SuppressWarnings("rawtypes")
	private transient List associatedProductTypeList;
	private String homeNetwork;
	private boolean issuperUser;
	private String rightClickEnable;
	private String calendarType;
	private String homeNetworkName;
	private ArrayList<UserBalanceVO> userBalanceList;
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getReportHeaderName() {
		return reportHeaderName;
	}
	public void setReportHeaderName(String reportHeaderName) {
		this.reportHeaderName = reportHeaderName;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getContactPersonch() {
		return contactPersonch;
	}
	public void setContactPersonch(String contactPersonch) {
		this.contactPersonch = contactPersonch;
	}
	public String getNetworkstatus() {
		return networkstatus;
	}
	public void setNetworkstatus(String networkstatus) {
		this.networkstatus = networkstatus;
	}
	public String getLanguage1Message() {
		return language1Message;
	}
	public void setLanguage1Message(String language1Message) {
		this.language1Message = language1Message;
	}
	public String getLanguage2Message() {
		return language2Message;
	}
	public void setLanguage2Message(String language2Message) {
		this.language2Message = language2Message;
	}
	public String getSosAllowed() {
		return sosAllowed;
	}
	public void setSosAllowed(String sosAllowed) {
		this.sosAllowed = sosAllowed;
	}
	public Long getSosAllowedAmount() {
		return sosAllowedAmount;
	}
	public void setSosAllowedAmount(Long sosAllowedAmount) {
		this.sosAllowedAmount = sosAllowedAmount;
	}
	public Long getSosThresholdLimit() {
		return sosThresholdLimit;
	}
	public void setSosThresholdLimit(Long sosThresholdLimit) {
		this.sosThresholdLimit = sosThresholdLimit;
	}
	public String getTrannferRuleTypeId() {
		return trannferRuleTypeId;
	}
	public void setTrannferRuleTypeId(String trannferRuleTypeId) {
		this.trannferRuleTypeId = trannferRuleTypeId;
	}
	public String getUserGrade() {
		return userGrade;
	}
	public void setUserGrade(String userGrade) {
		this.userGrade = userGrade;
	}
	public String getUserGradeName() {
		return userGradeName;
	}
	public void setUserGradeName(String userGradeName) {
		this.userGradeName = userGradeName;
	}
	public String getTransferProfileID() {
		return transferProfileID;
	}
	public void setTransferProfileID(String transferProfileID) {
		this.transferProfileID = transferProfileID;
	}
	public String getTransferProfileName() {
		return transferProfileName;
	}
	public void setTransferProfileName(String transferProfileName) {
		this.transferProfileName = transferProfileName;
	}
	public String getTransferProfileStatus() {
		return transferProfileStatus;
	}
	public void setTransferProfileStatus(String transferProfileStatus) {
		this.transferProfileStatus = transferProfileStatus;
	}
	public String getCommissionProfileSetID() {
		return commissionProfileSetID;
	}
	public void setCommissionProfileSetID(String commissionProfileSetID) {
		this.commissionProfileSetID = commissionProfileSetID;
	}
	public String getCommissionProfileSetName() {
		return commissionProfileSetName;
	}
	public void setCommissionProfileSetName(String commissionProfileSetName) {
		this.commissionProfileSetName = commissionProfileSetName;
	}
	public String getCommissionProfileSetVersion() {
		return commissionProfileSetVersion;
	}
	public void setCommissionProfileSetVersion(String commissionProfileSetVersion) {
		this.commissionProfileSetVersion = commissionProfileSetVersion;
	}
	public String getCommissionProfileStatus() {
		return commissionProfileStatus;
	}
	public void setCommissionProfileStatus(String commissionProfileStatus) {
		this.commissionProfileStatus = commissionProfileStatus;
	}
	public String getInSuspend() {
		return inSuspend;
	}
	public void setInSuspend(String inSuspend) {
		this.inSuspend = inSuspend;
	}
	public String getOutSuspened() {
		return outSuspened;
	}
	public void setOutSuspened(String outSuspened) {
		this.outSuspened = outSuspened;
	}
	public String getGeographicalCode() {
		return geographicalCode;
	}
	public void setGeographicalCode(String geographicalCode) {
		this.geographicalCode = geographicalCode;
	}
	public String getGeographicalDesc() {
		return geographicalDesc;
	}
	public void setGeographicalDesc(String geographicalDesc) {
		this.geographicalDesc = geographicalDesc;
	}
	public String getOutletCode() {
		return outletCode;
	}
	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}
	public String getSubOutletCode() {
		return subOutletCode;
	}
	public void setSubOutletCode(String subOutletCode) {
		this.subOutletCode = subOutletCode;
	}
	public String getActivatedOn() {
		return activatedOn;
	}
	public void setActivatedOn(String activatedOn) {
		this.activatedOn = activatedOn;
	}
	public String getLmsProfile() {
		return lmsProfile;
	}
	public void setLmsProfile(String lmsProfile) {
		this.lmsProfile = lmsProfile;
	}
	public String getOptInOutStatus() {
		return optInOutStatus;
	}
	public void setOptInOutStatus(String optInOutStatus) {
		this.optInOutStatus = optInOutStatus;
	}
	public String getControlGroup() {
		return controlGroup;
	}
	public void setControlGroup(String controlGroup) {
		this.controlGroup = controlGroup;
	}
	public String getDomainID() {
		return domainID;
	}
	public void setDomainID(String domainID) {
		this.domainID = domainID;
	}
	public String getDomainTypeCode() {
		return domainTypeCode;
	}
	public void setDomainTypeCode(String domainTypeCode) {
		this.domainTypeCode = domainTypeCode;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public int getInvalidPinCount() {
		return invalidPinCount;
	}
	public void setInvalidPinCount(int invalidPinCount) {
		this.invalidPinCount = invalidPinCount;
	}
	public String getRestrictedMsisdnAllow() {
		return restrictedMsisdnAllow;
	}
	public void setRestrictedMsisdnAllow(String restrictedMsisdnAllow) {
		this.restrictedMsisdnAllow = restrictedMsisdnAllow;
	}
	public ArrayList getAssociatedServiceTypeList() {
		return associatedServiceTypeList;
	}
	public void setAssociatedServiceTypeList(ArrayList associatedServiceTypeList) {
		this.associatedServiceTypeList = associatedServiceTypeList;
	}
	public String getActiveUserID() {
		return activeUserID;
	}
	public void setActiveUserID(String activeUserID) {
		this.activeUserID = activeUserID;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getCreatedByUserName() {
		return createdByUserName;
	}
	public void setCreatedByUserName(String createdByUserName) {
		this.createdByUserName = createdByUserName;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public boolean isStaffUser() {
		return isStaffUser;
	}
	public void setStaffUser(boolean isStaffUser) {
		this.isStaffUser = isStaffUser;
	}
	public String getParentLoginId() {
		return parentLoginId;
	}
	public void setParentLoginId(String parentLoginId) {
		this.parentLoginId = parentLoginId;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	public String getDomainStatus() {
		return domainStatus;
	}
	public void setDomainStatus(String domainStatus) {
		this.domainStatus = domainStatus;
	}
	public String getApplicationID() {
		return applicationID;
	}
	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}
	public String getMpayProfileID() {
		return mpayProfileID;
	}
	public void setMpayProfileID(String mpayProfileID) {
		this.mpayProfileID = mpayProfileID;
	}
	public String getUserProfileID() {
		return userProfileID;
	}
	public void setUserProfileID(String userProfileID) {
		this.userProfileID = userProfileID;
	}
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public String getMcommerceServiceAllow() {
		return mcommerceServiceAllow;
	}
	public void setMcommerceServiceAllow(String mcommerceServiceAllow) {
		this.mcommerceServiceAllow = mcommerceServiceAllow;
	}
	public String getPhoneProfile() {
		return phoneProfile;
	}
	public void setPhoneProfile(String phoneProfile) {
		this.phoneProfile = phoneProfile;
	}
	public String getSmsPin() {
		return smsPin;
	}
	public void setSmsPin(String smsPin) {
		this.smsPin = smsPin;
	}
	public String getPinReset() {
		return pinReset;
	}
	public void setPinReset(String pinReset) {
		this.pinReset = pinReset;
	}
	public long getPrefixId() {
		return prefixId;
	}
	public void setPrefixId(long prefixId) {
		this.prefixId = prefixId;
	}
	public String getPinRequired() {
		return pinRequired;
	}
	public void setPinRequired(String pinRequired) {
		this.pinRequired = pinRequired;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList getMenuItemList() {
		return menuItemList;
	}
	public void setMenuItemList(ArrayList menuItemList) {
		this.menuItemList = menuItemList;
	}
	public Map<String, HashMap<String, ArrayList<UserRolesVO>>> getMenuItemListNew() {
		return menuItemListNew;
	}
	public void setMenuItemListNew(Map<String, HashMap<String, ArrayList<UserRolesVO>>> menuItemListNew) {
		this.menuItemListNew = menuItemListNew;
	}
	public ArrayList getDomainList() {
		return domainList;
	}
	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}
	public ArrayList getServiceList() {
		return serviceList;
	}
	public void setServiceList(ArrayList serviceList) {
		this.serviceList = serviceList;
	}
	public List getAssociatedProductTypeList() {
		return associatedProductTypeList;
	}
	public void setAssociatedProductTypeList(List associatedProductTypeList) {
		this.associatedProductTypeList = associatedProductTypeList;
	}
	public String getHomeNetwork() {
		return homeNetwork;
	}
	public void setHomeNetwork(String homeNetwork) {
		this.homeNetwork = homeNetwork;
	}
	public boolean isIssuperUser() {
		return issuperUser;
	}
	public void setIssuperUser(boolean issuperUser) {
		this.issuperUser = issuperUser;
	}
	public String getRightClickEnable() {
		return rightClickEnable;
	}
	public void setRightClickEnable(String rightClickEnable) {
		this.rightClickEnable = rightClickEnable;
	}
	public String getCalendarType() {
		return calendarType;
	}
	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}
	public String getHomeNetworkName() {
		return homeNetworkName;
	}
	public void setHomeNetworkName(String homeNetworkName) {
		this.homeNetworkName = homeNetworkName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public com.btsl.user.businesslogic.entity.Users getUsers() {
		return Users;
	}
	public void setUsers(com.btsl.user.businesslogic.entity.Users users) {
		Users = users;
	}
	public ArrayList<UserBalanceVO> getUserBalanceList() {
		return userBalanceList;
	}
	public void setUserBalanceList(ArrayList<UserBalanceVO> userBalanceList) {
		this.userBalanceList = userBalanceList;
	}
	public Categories getCategories() {
		return categories;
	}
	public void setCategories(Categories categories) {
		this.categories = categories;
	}
	public GeographicalDomainTypes getGeographicalDomainTypes() {
		return geographicalDomainTypes;
	}
	public void setGeographicalDomainTypes(GeographicalDomainTypes geographicalDomainTypes) {
		this.geographicalDomainTypes = geographicalDomainTypes;
	}
	public List<UserGeographiesVO> getUserGeographiesList() {
		return userGeographiesList;
	}
	public void setUserGeographiesList(List<UserGeographiesVO> userGeographiesList) {
		this.userGeographiesList = userGeographiesList;
	}
	public ArrayList<UserGeographies> getGeographicalAreaList() {
		return geographicalAreaList;
	}
	public void setGeographicalAreaList(ArrayList<UserGeographies> geographicalAreaList) {
		this.geographicalAreaList = geographicalAreaList;
	}

	


}
