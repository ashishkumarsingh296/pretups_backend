package com.restapi.superadmin.responseVO;

import java.util.ArrayList;

public class TransferProfileFormVO  {

	private String networkName;
	private ArrayList domainTypeList;
	private ArrayList lookupStatusList;

	private ArrayList productBalanceList;
	private ArrayList tansferProfileList;
	private String code;
	private String domainTypeCode;
	private String domainName;
	private ArrayList searchDomainList;
	private String domainCodeforCategory;
	private boolean unctrlTransferFlag;

	private String status;

	private String profileStatusName;
	private String profileName;
	private String profileId;
	private String networkCode;
	private String category;
	private String categoryName;
	private String productCode;

	private String shortName;
	private String description;

	private String dailyInCount;
	private String dailyInValue;
	private String dailyOutCount;
	private String dailyOutValue;

	private String weeklyInCount;
	private String weeklyInValue;
	private String weeklyOutCount;
	private String weeklyOutValue;

	private String dailySubscriberOutCount;
	private String weeklySubscriberOutCount;
	private String monthlySubscriberOutCount;

	private String dailySubscriberOutValue;
	private String weeklySubscriberOutValue;
	private String monthlySubscriberOutValue;

	private String monthlyInCount;
	private String monthlyInValue;
	private String monthlyOutCount;
	private String monthlyOutValue;

	private String unctrlDailyInCount;
	private String unctrlDailyInValue;
	private String unctrlDailyOutCount;
	private String unctrlDailyOutValue;

	private String unctrlWeeklyInCount;
	private String unctrlWeeklyInValue;
	private String unctrlWeeklyOutCount;
	private String unctrlWeeklyOutValue;

	private String unctrlMonthlyInCount;
	private String unctrlMonthlyInValue;
	private String unctrlMonthlyOutCount;
	private String unctrlMonthlyOutValue;

	private ArrayList categoryList;
	private ArrayList domainList;
	private boolean listSizeFlag;
	private boolean subscriberOutCountFlag;

	// Alerting variables
	private String dailyInAltCount;
	private String dailyInAltValue;
	private String dailyOutAltCount;
	private String dailyOutAltValue;

	private String weeklyInAltCount;
	private String weeklyInAltValue;
	private String weeklyOutAltCount;
	private String weeklyOutAltValue;

	private String dailySubscriberOutAltCount;
	private String weeklySubscriberOutAltCount;
	private String monthlySubscriberOutAltCount;

	private String dailySubscriberOutAltValue;
	private String weeklySubscriberOutAltValue;
	private String monthlySubscriberOutAltValue;

	private String monthlyInAltCount;
	private String monthlyInAltValue;
	private String monthlyOutAltCount;
	private String monthlyOutAltValue;

	private String unctrlDailyInAltCount;
	private String unctrlDailyInAltValue;
	private String unctrlDailyOutAltCount;
	private String unctrlDailyOutAltValue;

	private String unctrlWeeklyInAltCount;
	private String unctrlWeeklyInAltValue;
	private String unctrlWeeklyOutAltCount;
	private String unctrlWeeklyOutAltValue;

	private String unctrlMonthlyInAltCount;
	private String unctrlMonthlyInAltValue;
	private String unctrlMonthlyOutAltCount;
	private String unctrlMonthlyOutAltValue;
	private long lastModifiedTime;
	private boolean isDefaultProfileModified;
	private String defaultCommProfile = "N";
	private String isDefault = "N";

	// 6.4 changes

	private String dailySubscriberInCount;
	private String weeklySubscriberInCount;
	private String monthlySubscriberInCount;

	private String dailySubscriberInValue;
	private String weeklySubscriberInValue;
	private String monthlySubscriberInValue;

	private String dailySubscriberInAltCount;
	private String weeklySubscriberInAltCount;
	private String monthlySubscriberInAltCount;

	private String dailySubscriberInAltValue;
	private String weeklySubscriberInAltValue;
	private String monthlySubscriberInAltValue;

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public ArrayList getDomainTypeList() {
		return domainTypeList;
	}

	public void setDomainTypeList(ArrayList domainTypeList) {
		this.domainTypeList = domainTypeList;
	}

	public ArrayList getLookupStatusList() {
		return lookupStatusList;
	}

	public void setLookupStatusList(ArrayList lookupStatusList) {
		this.lookupStatusList = lookupStatusList;
	}

	public ArrayList getProductBalanceList() {
		return productBalanceList;
	}

	public void setProductBalanceList(ArrayList productBalanceList) {
		this.productBalanceList = productBalanceList;
	}

	public ArrayList getTansferProfileList() {
		return tansferProfileList;
	}

	public void setTansferProfileList(ArrayList tansferProfileList) {
		this.tansferProfileList = tansferProfileList;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public ArrayList getSearchDomainList() {
		return searchDomainList;
	}

	public void setSearchDomainList(ArrayList searchDomainList) {
		this.searchDomainList = searchDomainList;
	}

	public String getDomainCodeforCategory() {
		return domainCodeforCategory;
	}

	public void setDomainCodeforCategory(String domainCodeforCategory) {
		this.domainCodeforCategory = domainCodeforCategory;
	}

	public boolean isUnctrlTransferFlag() {
		return unctrlTransferFlag;
	}

	public void setUnctrlTransferFlag(boolean unctrlTransferFlag) {
		this.unctrlTransferFlag = unctrlTransferFlag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProfileStatusName() {
		return profileStatusName;
	}

	public void setProfileStatusName(String profileStatusName) {
		this.profileStatusName = profileStatusName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDailyInCount() {
		return dailyInCount;
	}

	public void setDailyInCount(String dailyInCount) {
		this.dailyInCount = dailyInCount;
	}

	public String getDailyInValue() {
		return dailyInValue;
	}

	public void setDailyInValue(String dailyInValue) {
		this.dailyInValue = dailyInValue;
	}

	public String getDailyOutCount() {
		return dailyOutCount;
	}

	public void setDailyOutCount(String dailyOutCount) {
		this.dailyOutCount = dailyOutCount;
	}

	public String getDailyOutValue() {
		return dailyOutValue;
	}

	public void setDailyOutValue(String dailyOutValue) {
		this.dailyOutValue = dailyOutValue;
	}

	public String getWeeklyInCount() {
		return weeklyInCount;
	}

	public void setWeeklyInCount(String weeklyInCount) {
		this.weeklyInCount = weeklyInCount;
	}

	public String getWeeklyInValue() {
		return weeklyInValue;
	}

	public void setWeeklyInValue(String weeklyInValue) {
		this.weeklyInValue = weeklyInValue;
	}

	public String getWeeklyOutCount() {
		return weeklyOutCount;
	}

	public void setWeeklyOutCount(String weeklyOutCount) {
		this.weeklyOutCount = weeklyOutCount;
	}

	public String getWeeklyOutValue() {
		return weeklyOutValue;
	}

	public void setWeeklyOutValue(String weeklyOutValue) {
		this.weeklyOutValue = weeklyOutValue;
	}

	public String getDailySubscriberOutCount() {
		return dailySubscriberOutCount;
	}

	public void setDailySubscriberOutCount(String dailySubscriberOutCount) {
		this.dailySubscriberOutCount = dailySubscriberOutCount;
	}

	public String getWeeklySubscriberOutCount() {
		return weeklySubscriberOutCount;
	}

	public void setWeeklySubscriberOutCount(String weeklySubscriberOutCount) {
		this.weeklySubscriberOutCount = weeklySubscriberOutCount;
	}

	public String getMonthlySubscriberOutCount() {
		return monthlySubscriberOutCount;
	}

	public void setMonthlySubscriberOutCount(String monthlySubscriberOutCount) {
		this.monthlySubscriberOutCount = monthlySubscriberOutCount;
	}

	public String getDailySubscriberOutValue() {
		return dailySubscriberOutValue;
	}

	public void setDailySubscriberOutValue(String dailySubscriberOutValue) {
		this.dailySubscriberOutValue = dailySubscriberOutValue;
	}

	public String getWeeklySubscriberOutValue() {
		return weeklySubscriberOutValue;
	}

	public void setWeeklySubscriberOutValue(String weeklySubscriberOutValue) {
		this.weeklySubscriberOutValue = weeklySubscriberOutValue;
	}

	public String getMonthlySubscriberOutValue() {
		return monthlySubscriberOutValue;
	}

	public void setMonthlySubscriberOutValue(String monthlySubscriberOutValue) {
		this.monthlySubscriberOutValue = monthlySubscriberOutValue;
	}

	public String getMonthlyInCount() {
		return monthlyInCount;
	}

	public void setMonthlyInCount(String monthlyInCount) {
		this.monthlyInCount = monthlyInCount;
	}

	public String getMonthlyInValue() {
		return monthlyInValue;
	}

	public void setMonthlyInValue(String monthlyInValue) {
		this.monthlyInValue = monthlyInValue;
	}

	public String getMonthlyOutCount() {
		return monthlyOutCount;
	}

	public void setMonthlyOutCount(String monthlyOutCount) {
		this.monthlyOutCount = monthlyOutCount;
	}

	public String getMonthlyOutValue() {
		return monthlyOutValue;
	}

	public void setMonthlyOutValue(String monthlyOutValue) {
		this.monthlyOutValue = monthlyOutValue;
	}

	public String getUnctrlDailyInCount() {
		return unctrlDailyInCount;
	}

	public void setUnctrlDailyInCount(String unctrlDailyInCount) {
		this.unctrlDailyInCount = unctrlDailyInCount;
	}

	public String getUnctrlDailyInValue() {
		return unctrlDailyInValue;
	}

	public void setUnctrlDailyInValue(String unctrlDailyInValue) {
		this.unctrlDailyInValue = unctrlDailyInValue;
	}

	public String getUnctrlDailyOutCount() {
		return unctrlDailyOutCount;
	}

	public void setUnctrlDailyOutCount(String unctrlDailyOutCount) {
		this.unctrlDailyOutCount = unctrlDailyOutCount;
	}

	public String getUnctrlDailyOutValue() {
		return unctrlDailyOutValue;
	}

	public void setUnctrlDailyOutValue(String unctrlDailyOutValue) {
		this.unctrlDailyOutValue = unctrlDailyOutValue;
	}

	public String getUnctrlWeeklyInCount() {
		return unctrlWeeklyInCount;
	}

	public void setUnctrlWeeklyInCount(String unctrlWeeklyInCount) {
		this.unctrlWeeklyInCount = unctrlWeeklyInCount;
	}

	public String getUnctrlWeeklyInValue() {
		return unctrlWeeklyInValue;
	}

	public void setUnctrlWeeklyInValue(String unctrlWeeklyInValue) {
		this.unctrlWeeklyInValue = unctrlWeeklyInValue;
	}

	public String getUnctrlWeeklyOutCount() {
		return unctrlWeeklyOutCount;
	}

	public void setUnctrlWeeklyOutCount(String unctrlWeeklyOutCount) {
		this.unctrlWeeklyOutCount = unctrlWeeklyOutCount;
	}

	public String getUnctrlWeeklyOutValue() {
		return unctrlWeeklyOutValue;
	}

	public void setUnctrlWeeklyOutValue(String unctrlWeeklyOutValue) {
		this.unctrlWeeklyOutValue = unctrlWeeklyOutValue;
	}

	public String getUnctrlMonthlyInCount() {
		return unctrlMonthlyInCount;
	}

	public void setUnctrlMonthlyInCount(String unctrlMonthlyInCount) {
		this.unctrlMonthlyInCount = unctrlMonthlyInCount;
	}

	public String getUnctrlMonthlyInValue() {
		return unctrlMonthlyInValue;
	}

	public void setUnctrlMonthlyInValue(String unctrlMonthlyInValue) {
		this.unctrlMonthlyInValue = unctrlMonthlyInValue;
	}

	public String getUnctrlMonthlyOutCount() {
		return unctrlMonthlyOutCount;
	}

	public void setUnctrlMonthlyOutCount(String unctrlMonthlyOutCount) {
		this.unctrlMonthlyOutCount = unctrlMonthlyOutCount;
	}

	public String getUnctrlMonthlyOutValue() {
		return unctrlMonthlyOutValue;
	}

	public void setUnctrlMonthlyOutValue(String unctrlMonthlyOutValue) {
		this.unctrlMonthlyOutValue = unctrlMonthlyOutValue;
	}

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList getDomainList() {
		return domainList;
	}

	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}

	public boolean isListSizeFlag() {
		return listSizeFlag;
	}

	public void setListSizeFlag(boolean listSizeFlag) {
		this.listSizeFlag = listSizeFlag;
	}

	public boolean isSubscriberOutCountFlag() {
		return subscriberOutCountFlag;
	}

	public void setSubscriberOutCountFlag(boolean subscriberOutCountFlag) {
		this.subscriberOutCountFlag = subscriberOutCountFlag;
	}

	public String getDailyInAltCount() {
		return dailyInAltCount;
	}

	public void setDailyInAltCount(String dailyInAltCount) {
		this.dailyInAltCount = dailyInAltCount;
	}

	public String getDailyInAltValue() {
		return dailyInAltValue;
	}

	public void setDailyInAltValue(String dailyInAltValue) {
		this.dailyInAltValue = dailyInAltValue;
	}

	public String getDailyOutAltCount() {
		return dailyOutAltCount;
	}

	public void setDailyOutAltCount(String dailyOutAltCount) {
		this.dailyOutAltCount = dailyOutAltCount;
	}

	public String getDailyOutAltValue() {
		return dailyOutAltValue;
	}

	public void setDailyOutAltValue(String dailyOutAltValue) {
		this.dailyOutAltValue = dailyOutAltValue;
	}

	public String getWeeklyInAltCount() {
		return weeklyInAltCount;
	}

	public void setWeeklyInAltCount(String weeklyInAltCount) {
		this.weeklyInAltCount = weeklyInAltCount;
	}

	public String getWeeklyInAltValue() {
		return weeklyInAltValue;
	}

	public void setWeeklyInAltValue(String weeklyInAltValue) {
		this.weeklyInAltValue = weeklyInAltValue;
	}

	public String getWeeklyOutAltCount() {
		return weeklyOutAltCount;
	}

	public void setWeeklyOutAltCount(String weeklyOutAltCount) {
		this.weeklyOutAltCount = weeklyOutAltCount;
	}

	public String getWeeklyOutAltValue() {
		return weeklyOutAltValue;
	}

	public void setWeeklyOutAltValue(String weeklyOutAltValue) {
		this.weeklyOutAltValue = weeklyOutAltValue;
	}

	public String getDailySubscriberOutAltCount() {
		return dailySubscriberOutAltCount;
	}

	public void setDailySubscriberOutAltCount(String dailySubscriberOutAltCount) {
		this.dailySubscriberOutAltCount = dailySubscriberOutAltCount;
	}

	public String getWeeklySubscriberOutAltCount() {
		return weeklySubscriberOutAltCount;
	}

	public void setWeeklySubscriberOutAltCount(String weeklySubscriberOutAltCount) {
		this.weeklySubscriberOutAltCount = weeklySubscriberOutAltCount;
	}

	public String getMonthlySubscriberOutAltCount() {
		return monthlySubscriberOutAltCount;
	}

	public void setMonthlySubscriberOutAltCount(String monthlySubscriberOutAltCount) {
		this.monthlySubscriberOutAltCount = monthlySubscriberOutAltCount;
	}

	public String getDailySubscriberOutAltValue() {
		return dailySubscriberOutAltValue;
	}

	public void setDailySubscriberOutAltValue(String dailySubscriberOutAltValue) {
		this.dailySubscriberOutAltValue = dailySubscriberOutAltValue;
	}

	public String getWeeklySubscriberOutAltValue() {
		return weeklySubscriberOutAltValue;
	}

	public void setWeeklySubscriberOutAltValue(String weeklySubscriberOutAltValue) {
		this.weeklySubscriberOutAltValue = weeklySubscriberOutAltValue;
	}

	public String getMonthlySubscriberOutAltValue() {
		return monthlySubscriberOutAltValue;
	}

	public void setMonthlySubscriberOutAltValue(String monthlySubscriberOutAltValue) {
		this.monthlySubscriberOutAltValue = monthlySubscriberOutAltValue;
	}

	public String getMonthlyInAltCount() {
		return monthlyInAltCount;
	}

	public void setMonthlyInAltCount(String monthlyInAltCount) {
		this.monthlyInAltCount = monthlyInAltCount;
	}

	public String getMonthlyInAltValue() {
		return monthlyInAltValue;
	}

	public void setMonthlyInAltValue(String monthlyInAltValue) {
		this.monthlyInAltValue = monthlyInAltValue;
	}

	public String getMonthlyOutAltCount() {
		return monthlyOutAltCount;
	}

	public void setMonthlyOutAltCount(String monthlyOutAltCount) {
		this.monthlyOutAltCount = monthlyOutAltCount;
	}

	public String getMonthlyOutAltValue() {
		return monthlyOutAltValue;
	}

	public void setMonthlyOutAltValue(String monthlyOutAltValue) {
		this.monthlyOutAltValue = monthlyOutAltValue;
	}

	public String getUnctrlDailyInAltCount() {
		return unctrlDailyInAltCount;
	}

	public void setUnctrlDailyInAltCount(String unctrlDailyInAltCount) {
		this.unctrlDailyInAltCount = unctrlDailyInAltCount;
	}

	public String getUnctrlDailyInAltValue() {
		return unctrlDailyInAltValue;
	}

	public void setUnctrlDailyInAltValue(String unctrlDailyInAltValue) {
		this.unctrlDailyInAltValue = unctrlDailyInAltValue;
	}

	public String getUnctrlDailyOutAltCount() {
		return unctrlDailyOutAltCount;
	}

	public void setUnctrlDailyOutAltCount(String unctrlDailyOutAltCount) {
		this.unctrlDailyOutAltCount = unctrlDailyOutAltCount;
	}

	public String getUnctrlDailyOutAltValue() {
		return unctrlDailyOutAltValue;
	}

	public void setUnctrlDailyOutAltValue(String unctrlDailyOutAltValue) {
		this.unctrlDailyOutAltValue = unctrlDailyOutAltValue;
	}

	public String getUnctrlWeeklyInAltCount() {
		return unctrlWeeklyInAltCount;
	}

	public void setUnctrlWeeklyInAltCount(String unctrlWeeklyInAltCount) {
		this.unctrlWeeklyInAltCount = unctrlWeeklyInAltCount;
	}

	public String getUnctrlWeeklyInAltValue() {
		return unctrlWeeklyInAltValue;
	}

	public void setUnctrlWeeklyInAltValue(String unctrlWeeklyInAltValue) {
		this.unctrlWeeklyInAltValue = unctrlWeeklyInAltValue;
	}

	public String getUnctrlWeeklyOutAltCount() {
		return unctrlWeeklyOutAltCount;
	}

	public void setUnctrlWeeklyOutAltCount(String unctrlWeeklyOutAltCount) {
		this.unctrlWeeklyOutAltCount = unctrlWeeklyOutAltCount;
	}

	public String getUnctrlWeeklyOutAltValue() {
		return unctrlWeeklyOutAltValue;
	}

	public void setUnctrlWeeklyOutAltValue(String unctrlWeeklyOutAltValue) {
		this.unctrlWeeklyOutAltValue = unctrlWeeklyOutAltValue;
	}

	public String getUnctrlMonthlyInAltCount() {
		return unctrlMonthlyInAltCount;
	}

	public void setUnctrlMonthlyInAltCount(String unctrlMonthlyInAltCount) {
		this.unctrlMonthlyInAltCount = unctrlMonthlyInAltCount;
	}

	public String getUnctrlMonthlyInAltValue() {
		return unctrlMonthlyInAltValue;
	}

	public void setUnctrlMonthlyInAltValue(String unctrlMonthlyInAltValue) {
		this.unctrlMonthlyInAltValue = unctrlMonthlyInAltValue;
	}

	public String getUnctrlMonthlyOutAltCount() {
		return unctrlMonthlyOutAltCount;
	}

	public void setUnctrlMonthlyOutAltCount(String unctrlMonthlyOutAltCount) {
		this.unctrlMonthlyOutAltCount = unctrlMonthlyOutAltCount;
	}

	public String getUnctrlMonthlyOutAltValue() {
		return unctrlMonthlyOutAltValue;
	}

	public void setUnctrlMonthlyOutAltValue(String unctrlMonthlyOutAltValue) {
		this.unctrlMonthlyOutAltValue = unctrlMonthlyOutAltValue;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public boolean isDefaultProfileModified() {
		return isDefaultProfileModified;
	}

	public void setDefaultProfileModified(boolean isDefaultProfileModified) {
		this.isDefaultProfileModified = isDefaultProfileModified;
	}

	public String getDefaultCommProfile() {
		return defaultCommProfile;
	}

	public void setDefaultCommProfile(String defaultCommProfile) {
		this.defaultCommProfile = defaultCommProfile;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getDailySubscriberInCount() {
		return dailySubscriberInCount;
	}

	public void setDailySubscriberInCount(String dailySubscriberInCount) {
		this.dailySubscriberInCount = dailySubscriberInCount;
	}

	public String getWeeklySubscriberInCount() {
		return weeklySubscriberInCount;
	}

	public void setWeeklySubscriberInCount(String weeklySubscriberInCount) {
		this.weeklySubscriberInCount = weeklySubscriberInCount;
	}

	public String getMonthlySubscriberInCount() {
		return monthlySubscriberInCount;
	}

	public void setMonthlySubscriberInCount(String monthlySubscriberInCount) {
		this.monthlySubscriberInCount = monthlySubscriberInCount;
	}

	public String getDailySubscriberInValue() {
		return dailySubscriberInValue;
	}

	public void setDailySubscriberInValue(String dailySubscriberInValue) {
		this.dailySubscriberInValue = dailySubscriberInValue;
	}

	public String getWeeklySubscriberInValue() {
		return weeklySubscriberInValue;
	}

	public void setWeeklySubscriberInValue(String weeklySubscriberInValue) {
		this.weeklySubscriberInValue = weeklySubscriberInValue;
	}

	public String getMonthlySubscriberInValue() {
		return monthlySubscriberInValue;
	}

	public void setMonthlySubscriberInValue(String monthlySubscriberInValue) {
		this.monthlySubscriberInValue = monthlySubscriberInValue;
	}

	public String getDailySubscriberInAltCount() {
		return dailySubscriberInAltCount;
	}

	public void setDailySubscriberInAltCount(String dailySubscriberInAltCount) {
		this.dailySubscriberInAltCount = dailySubscriberInAltCount;
	}

	public String getWeeklySubscriberInAltCount() {
		return weeklySubscriberInAltCount;
	}

	public void setWeeklySubscriberInAltCount(String weeklySubscriberInAltCount) {
		this.weeklySubscriberInAltCount = weeklySubscriberInAltCount;
	}

	public String getMonthlySubscriberInAltCount() {
		return monthlySubscriberInAltCount;
	}

	public void setMonthlySubscriberInAltCount(String monthlySubscriberInAltCount) {
		this.monthlySubscriberInAltCount = monthlySubscriberInAltCount;
	}

	public String getDailySubscriberInAltValue() {
		return dailySubscriberInAltValue;
	}

	public void setDailySubscriberInAltValue(String dailySubscriberInAltValue) {
		this.dailySubscriberInAltValue = dailySubscriberInAltValue;
	}

	public String getWeeklySubscriberInAltValue() {
		return weeklySubscriberInAltValue;
	}

	public void setWeeklySubscriberInAltValue(String weeklySubscriberInAltValue) {
		this.weeklySubscriberInAltValue = weeklySubscriberInAltValue;
	}

	public String getMonthlySubscriberInAltValue() {
		return monthlySubscriberInAltValue;
	}

	public void setMonthlySubscriberInAltValue(String monthlySubscriberInAltValue) {
		this.monthlySubscriberInAltValue = monthlySubscriberInAltValue;
	}

}
