package com.restapi.superadmin.responseVO;

/**
 * @(#) TransferProfileVO.java
 *      Copyright(c) 2005, Bharti Telesoft Ltd.
 *      All Rights Reserved
 * 
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Author Date History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      manoj kumar 26/07/2005 Initial Creation
 * 
 *      This class holds the values coming from the DB
 * 
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class FetchTransferProfileVO implements Serializable {

	private long lastModifiedTime = 0;

	private boolean unctrlTransferFlag;

	private String profileId;

	private String profileName;

	private String shortName;

	private String status;

	private String profileStatusName;

	private String description;

	private long dailyInCount;

	private long dailyInValue;

	private long weeklyInCount;

	private long weeklyInValue;

	private long monthlyInCount;

	private long monthlyInValue;

	private long dailyOutCount;

	private long dailyOutValue;

	private long weeklyOutCount;

	private long weeklyOutValue;

	private long monthlyOutCount;

	private long monthlyOutValue;

	private long dailySubscriberOutCount;

	private long weeklySubscriberOutCount;

	private long monthlySubscriberOutCount;

	private long dailySubscriberOutValue;

	private long weeklySubscriberOutValue;

	private long monthlySubscriberOutValue;

	private long unctrlDailyInCount;

	private long unctrlDailyInValue;

	private long unctrlWeeklyInCount;

	private long unctrlWeeklyInValue;

	private long unctrlMonthlyInCount;

	private long unctrlMonthlyInValue;

	private long unctrlDailyOutCount;

	private long unctrlDailyOutValue;

	private long unctrlWeeklyOutCount;

	private long unctrlWeeklyOutValue;

	private long unctrlMonthlyOutCount;

	private long unctrlMonthlyOutValue;

	private String createdBy;

	private String modifiedBy;

	private Date createdOn;

	private Date modifiedOn;

	private String networkCode;

	private String category;
	private String categoryName;

	private ArrayList profileProductList;

	private long dailyC2STransferOutCount;
	private long dailyC2STransferOutValue;
	private long weeklyC2STransferOutCount;
	private long weeklyC2STransferOutValue;
	private long monthlyC2STransferOutCount;
	private long monthlyC2STransferOutValue;

	private boolean isUpdateRecord = true;

	// Alerting variables
	private long dailyInAltCount;
	private long dailyInAltValue;
	private long dailyOutAltCount;
	private long dailyOutAltValue;

	private long weeklyInAltCount;
	private long weeklyInAltValue;
	private long weeklyOutAltCount;
	private long weeklyOutAltValue;

	private long dailySubscriberOutAltCount;
	private long weeklySubscriberOutAltCount;
	private long monthlySubscriberOutAltCount;

	private long dailySubscriberOutAltValue;
	private long weeklySubscriberOutAltValue;
	private long monthlySubscriberOutAltValue;

	private long monthlyInAltCount;
	private long monthlyInAltValue;
	private long monthlyOutAltCount;
	private long monthlyOutAltValue;

	private long unctrlDailyInAltCount;
	private long unctrlDailyInAltValue;
	private long unctrlDailyOutAltCount;
	private long unctrlDailyOutAltValue;

	private long unctrlWeeklyInAltCount;
	private long unctrlWeeklyInAltValue;
	private long unctrlWeeklyOutAltCount;
	private long unctrlWeeklyOutAltValue;

	private long unctrlMonthlyInAltCount;
	private long unctrlMonthlyInAltValue;
	private long unctrlMonthlyOutAltCount;
	private long unctrlMonthlyOutAltValue;

	private String parentProfileID = null;

	private String isDefault = null;
	private String isDefaultDesc = "N";

	// 6.4 changes
	private long dailySubscriberInCount;
	private long weeklySubscriberInCount;
	private long monthlySubscriberInCount;
	private long dailySubscriberInValue;
	private long weeklySubscriberInValue;
	private long monthlySubscriberInValue;
	private long dailySubscriberInAltValue;
	private long weeklySubscriberInAltValue;
	private long monthlySubscriberInAltValue;
	private long dailySubscriberInAltCount;
	private long weeklySubscriberInAltCount;
	private long monthlySubscriberInAltCount;

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public boolean isUnctrlTransferFlag() {
		return unctrlTransferFlag;
	}

	public void setUnctrlTransferFlag(boolean unctrlTransferFlag) {
		this.unctrlTransferFlag = unctrlTransferFlag;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDailyInCount() {
		return dailyInCount;
	}

	public void setDailyInCount(long dailyInCount) {
		this.dailyInCount = dailyInCount;
	}

	public long getDailyInValue() {
		return dailyInValue;
	}

	public void setDailyInValue(long dailyInValue) {
		this.dailyInValue = dailyInValue;
	}

	public long getWeeklyInCount() {
		return weeklyInCount;
	}

	public void setWeeklyInCount(long weeklyInCount) {
		this.weeklyInCount = weeklyInCount;
	}

	public long getWeeklyInValue() {
		return weeklyInValue;
	}

	public void setWeeklyInValue(long weeklyInValue) {
		this.weeklyInValue = weeklyInValue;
	}

	public long getMonthlyInCount() {
		return monthlyInCount;
	}

	public void setMonthlyInCount(long monthlyInCount) {
		this.monthlyInCount = monthlyInCount;
	}

	public long getMonthlyInValue() {
		return monthlyInValue;
	}

	public void setMonthlyInValue(long monthlyInValue) {
		this.monthlyInValue = monthlyInValue;
	}

	public long getDailyOutCount() {
		return dailyOutCount;
	}

	public void setDailyOutCount(long dailyOutCount) {
		this.dailyOutCount = dailyOutCount;
	}

	public long getDailyOutValue() {
		return dailyOutValue;
	}

	public void setDailyOutValue(long dailyOutValue) {
		this.dailyOutValue = dailyOutValue;
	}

	public long getWeeklyOutCount() {
		return weeklyOutCount;
	}

	public void setWeeklyOutCount(long weeklyOutCount) {
		this.weeklyOutCount = weeklyOutCount;
	}

	public long getWeeklyOutValue() {
		return weeklyOutValue;
	}

	public void setWeeklyOutValue(long weeklyOutValue) {
		this.weeklyOutValue = weeklyOutValue;
	}

	public long getMonthlyOutCount() {
		return monthlyOutCount;
	}

	public void setMonthlyOutCount(long monthlyOutCount) {
		this.monthlyOutCount = monthlyOutCount;
	}

	public long getMonthlyOutValue() {
		return monthlyOutValue;
	}

	public void setMonthlyOutValue(long monthlyOutValue) {
		this.monthlyOutValue = monthlyOutValue;
	}

	public long getDailySubscriberOutCount() {
		return dailySubscriberOutCount;
	}

	public void setDailySubscriberOutCount(long dailySubscriberOutCount) {
		this.dailySubscriberOutCount = dailySubscriberOutCount;
	}

	public long getWeeklySubscriberOutCount() {
		return weeklySubscriberOutCount;
	}

	public void setWeeklySubscriberOutCount(long weeklySubscriberOutCount) {
		this.weeklySubscriberOutCount = weeklySubscriberOutCount;
	}

	public long getMonthlySubscriberOutCount() {
		return monthlySubscriberOutCount;
	}

	public void setMonthlySubscriberOutCount(long monthlySubscriberOutCount) {
		this.monthlySubscriberOutCount = monthlySubscriberOutCount;
	}

	public long getDailySubscriberOutValue() {
		return dailySubscriberOutValue;
	}

	public void setDailySubscriberOutValue(long dailySubscriberOutValue) {
		this.dailySubscriberOutValue = dailySubscriberOutValue;
	}

	public long getWeeklySubscriberOutValue() {
		return weeklySubscriberOutValue;
	}

	public void setWeeklySubscriberOutValue(long weeklySubscriberOutValue) {
		this.weeklySubscriberOutValue = weeklySubscriberOutValue;
	}

	public long getMonthlySubscriberOutValue() {
		return monthlySubscriberOutValue;
	}

	public void setMonthlySubscriberOutValue(long monthlySubscriberOutValue) {
		this.monthlySubscriberOutValue = monthlySubscriberOutValue;
	}

	public long getUnctrlDailyInCount() {
		return unctrlDailyInCount;
	}

	public void setUnctrlDailyInCount(long unctrlDailyInCount) {
		this.unctrlDailyInCount = unctrlDailyInCount;
	}

	public long getUnctrlDailyInValue() {
		return unctrlDailyInValue;
	}

	public void setUnctrlDailyInValue(long unctrlDailyInValue) {
		this.unctrlDailyInValue = unctrlDailyInValue;
	}

	public long getUnctrlWeeklyInCount() {
		return unctrlWeeklyInCount;
	}

	public void setUnctrlWeeklyInCount(long unctrlWeeklyInCount) {
		this.unctrlWeeklyInCount = unctrlWeeklyInCount;
	}

	public long getUnctrlWeeklyInValue() {
		return unctrlWeeklyInValue;
	}

	public void setUnctrlWeeklyInValue(long unctrlWeeklyInValue) {
		this.unctrlWeeklyInValue = unctrlWeeklyInValue;
	}

	public long getUnctrlMonthlyInCount() {
		return unctrlMonthlyInCount;
	}

	public void setUnctrlMonthlyInCount(long unctrlMonthlyInCount) {
		this.unctrlMonthlyInCount = unctrlMonthlyInCount;
	}

	public long getUnctrlMonthlyInValue() {
		return unctrlMonthlyInValue;
	}

	public void setUnctrlMonthlyInValue(long unctrlMonthlyInValue) {
		this.unctrlMonthlyInValue = unctrlMonthlyInValue;
	}

	public long getUnctrlDailyOutCount() {
		return unctrlDailyOutCount;
	}

	public void setUnctrlDailyOutCount(long unctrlDailyOutCount) {
		this.unctrlDailyOutCount = unctrlDailyOutCount;
	}

	public long getUnctrlDailyOutValue() {
		return unctrlDailyOutValue;
	}

	public void setUnctrlDailyOutValue(long unctrlDailyOutValue) {
		this.unctrlDailyOutValue = unctrlDailyOutValue;
	}

	public long getUnctrlWeeklyOutCount() {
		return unctrlWeeklyOutCount;
	}

	public void setUnctrlWeeklyOutCount(long unctrlWeeklyOutCount) {
		this.unctrlWeeklyOutCount = unctrlWeeklyOutCount;
	}

	public long getUnctrlWeeklyOutValue() {
		return unctrlWeeklyOutValue;
	}

	public void setUnctrlWeeklyOutValue(long unctrlWeeklyOutValue) {
		this.unctrlWeeklyOutValue = unctrlWeeklyOutValue;
	}

	public long getUnctrlMonthlyOutCount() {
		return unctrlMonthlyOutCount;
	}

	public void setUnctrlMonthlyOutCount(long unctrlMonthlyOutCount) {
		this.unctrlMonthlyOutCount = unctrlMonthlyOutCount;
	}

	public long getUnctrlMonthlyOutValue() {
		return unctrlMonthlyOutValue;
	}

	public void setUnctrlMonthlyOutValue(long unctrlMonthlyOutValue) {
		this.unctrlMonthlyOutValue = unctrlMonthlyOutValue;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
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

	public ArrayList getProfileProductList() {
		return profileProductList;
	}

	public void setProfileProductList(ArrayList profileProductList) {
		this.profileProductList = profileProductList;
	}

	public long getDailyC2STransferOutCount() {
		return dailyC2STransferOutCount;
	}

	public void setDailyC2STransferOutCount(long dailyC2STransferOutCount) {
		this.dailyC2STransferOutCount = dailyC2STransferOutCount;
	}

	public long getDailyC2STransferOutValue() {
		return dailyC2STransferOutValue;
	}

	public void setDailyC2STransferOutValue(long dailyC2STransferOutValue) {
		this.dailyC2STransferOutValue = dailyC2STransferOutValue;
	}

	public long getWeeklyC2STransferOutCount() {
		return weeklyC2STransferOutCount;
	}

	public void setWeeklyC2STransferOutCount(long weeklyC2STransferOutCount) {
		this.weeklyC2STransferOutCount = weeklyC2STransferOutCount;
	}

	public long getWeeklyC2STransferOutValue() {
		return weeklyC2STransferOutValue;
	}

	public void setWeeklyC2STransferOutValue(long weeklyC2STransferOutValue) {
		this.weeklyC2STransferOutValue = weeklyC2STransferOutValue;
	}

	public long getMonthlyC2STransferOutCount() {
		return monthlyC2STransferOutCount;
	}

	public void setMonthlyC2STransferOutCount(long monthlyC2STransferOutCount) {
		this.monthlyC2STransferOutCount = monthlyC2STransferOutCount;
	}

	public long getMonthlyC2STransferOutValue() {
		return monthlyC2STransferOutValue;
	}

	public void setMonthlyC2STransferOutValue(long monthlyC2STransferOutValue) {
		this.monthlyC2STransferOutValue = monthlyC2STransferOutValue;
	}

	public boolean isUpdateRecord() {
		return isUpdateRecord;
	}

	public void setUpdateRecord(boolean isUpdateRecord) {
		this.isUpdateRecord = isUpdateRecord;
	}

	public long getDailyInAltCount() {
		return dailyInAltCount;
	}

	public void setDailyInAltCount(long dailyInAltCount) {
		this.dailyInAltCount = dailyInAltCount;
	}

	public long getDailyInAltValue() {
		return dailyInAltValue;
	}

	public void setDailyInAltValue(long dailyInAltValue) {
		this.dailyInAltValue = dailyInAltValue;
	}

	public long getDailyOutAltCount() {
		return dailyOutAltCount;
	}

	public void setDailyOutAltCount(long dailyOutAltCount) {
		this.dailyOutAltCount = dailyOutAltCount;
	}

	public long getDailyOutAltValue() {
		return dailyOutAltValue;
	}

	public void setDailyOutAltValue(long dailyOutAltValue) {
		this.dailyOutAltValue = dailyOutAltValue;
	}

	public long getWeeklyInAltCount() {
		return weeklyInAltCount;
	}

	public void setWeeklyInAltCount(long weeklyInAltCount) {
		this.weeklyInAltCount = weeklyInAltCount;
	}

	public long getWeeklyInAltValue() {
		return weeklyInAltValue;
	}

	public void setWeeklyInAltValue(long weeklyInAltValue) {
		this.weeklyInAltValue = weeklyInAltValue;
	}

	public long getWeeklyOutAltCount() {
		return weeklyOutAltCount;
	}

	public void setWeeklyOutAltCount(long weeklyOutAltCount) {
		this.weeklyOutAltCount = weeklyOutAltCount;
	}

	public long getWeeklyOutAltValue() {
		return weeklyOutAltValue;
	}

	public void setWeeklyOutAltValue(long weeklyOutAltValue) {
		this.weeklyOutAltValue = weeklyOutAltValue;
	}

	public long getDailySubscriberOutAltCount() {
		return dailySubscriberOutAltCount;
	}

	public void setDailySubscriberOutAltCount(long dailySubscriberOutAltCount) {
		this.dailySubscriberOutAltCount = dailySubscriberOutAltCount;
	}

	public long getWeeklySubscriberOutAltCount() {
		return weeklySubscriberOutAltCount;
	}

	public void setWeeklySubscriberOutAltCount(long weeklySubscriberOutAltCount) {
		this.weeklySubscriberOutAltCount = weeklySubscriberOutAltCount;
	}

	public long getMonthlySubscriberOutAltCount() {
		return monthlySubscriberOutAltCount;
	}

	public void setMonthlySubscriberOutAltCount(long monthlySubscriberOutAltCount) {
		this.monthlySubscriberOutAltCount = monthlySubscriberOutAltCount;
	}

	public long getDailySubscriberOutAltValue() {
		return dailySubscriberOutAltValue;
	}

	public void setDailySubscriberOutAltValue(long dailySubscriberOutAltValue) {
		this.dailySubscriberOutAltValue = dailySubscriberOutAltValue;
	}

	public long getWeeklySubscriberOutAltValue() {
		return weeklySubscriberOutAltValue;
	}

	public void setWeeklySubscriberOutAltValue(long weeklySubscriberOutAltValue) {
		this.weeklySubscriberOutAltValue = weeklySubscriberOutAltValue;
	}

	public long getMonthlySubscriberOutAltValue() {
		return monthlySubscriberOutAltValue;
	}

	public void setMonthlySubscriberOutAltValue(long monthlySubscriberOutAltValue) {
		this.monthlySubscriberOutAltValue = monthlySubscriberOutAltValue;
	}

	public long getMonthlyInAltCount() {
		return monthlyInAltCount;
	}

	public void setMonthlyInAltCount(long monthlyInAltCount) {
		this.monthlyInAltCount = monthlyInAltCount;
	}

	public long getMonthlyInAltValue() {
		return monthlyInAltValue;
	}

	public void setMonthlyInAltValue(long monthlyInAltValue) {
		this.monthlyInAltValue = monthlyInAltValue;
	}

	public long getMonthlyOutAltCount() {
		return monthlyOutAltCount;
	}

	public void setMonthlyOutAltCount(long monthlyOutAltCount) {
		this.monthlyOutAltCount = monthlyOutAltCount;
	}

	public long getMonthlyOutAltValue() {
		return monthlyOutAltValue;
	}

	public void setMonthlyOutAltValue(long monthlyOutAltValue) {
		this.monthlyOutAltValue = monthlyOutAltValue;
	}

	public long getUnctrlDailyInAltCount() {
		return unctrlDailyInAltCount;
	}

	public void setUnctrlDailyInAltCount(long unctrlDailyInAltCount) {
		this.unctrlDailyInAltCount = unctrlDailyInAltCount;
	}

	public long getUnctrlDailyInAltValue() {
		return unctrlDailyInAltValue;
	}

	public void setUnctrlDailyInAltValue(long unctrlDailyInAltValue) {
		this.unctrlDailyInAltValue = unctrlDailyInAltValue;
	}

	public long getUnctrlDailyOutAltCount() {
		return unctrlDailyOutAltCount;
	}

	public void setUnctrlDailyOutAltCount(long unctrlDailyOutAltCount) {
		this.unctrlDailyOutAltCount = unctrlDailyOutAltCount;
	}

	public long getUnctrlDailyOutAltValue() {
		return unctrlDailyOutAltValue;
	}

	public void setUnctrlDailyOutAltValue(long unctrlDailyOutAltValue) {
		this.unctrlDailyOutAltValue = unctrlDailyOutAltValue;
	}

	public long getUnctrlWeeklyInAltCount() {
		return unctrlWeeklyInAltCount;
	}

	public void setUnctrlWeeklyInAltCount(long unctrlWeeklyInAltCount) {
		this.unctrlWeeklyInAltCount = unctrlWeeklyInAltCount;
	}

	public long getUnctrlWeeklyInAltValue() {
		return unctrlWeeklyInAltValue;
	}

	public void setUnctrlWeeklyInAltValue(long unctrlWeeklyInAltValue) {
		this.unctrlWeeklyInAltValue = unctrlWeeklyInAltValue;
	}

	public long getUnctrlWeeklyOutAltCount() {
		return unctrlWeeklyOutAltCount;
	}

	public void setUnctrlWeeklyOutAltCount(long unctrlWeeklyOutAltCount) {
		this.unctrlWeeklyOutAltCount = unctrlWeeklyOutAltCount;
	}

	public long getUnctrlWeeklyOutAltValue() {
		return unctrlWeeklyOutAltValue;
	}

	public void setUnctrlWeeklyOutAltValue(long unctrlWeeklyOutAltValue) {
		this.unctrlWeeklyOutAltValue = unctrlWeeklyOutAltValue;
	}

	public long getUnctrlMonthlyInAltCount() {
		return unctrlMonthlyInAltCount;
	}

	public void setUnctrlMonthlyInAltCount(long unctrlMonthlyInAltCount) {
		this.unctrlMonthlyInAltCount = unctrlMonthlyInAltCount;
	}

	public long getUnctrlMonthlyInAltValue() {
		return unctrlMonthlyInAltValue;
	}

	public void setUnctrlMonthlyInAltValue(long unctrlMonthlyInAltValue) {
		this.unctrlMonthlyInAltValue = unctrlMonthlyInAltValue;
	}

	public long getUnctrlMonthlyOutAltCount() {
		return unctrlMonthlyOutAltCount;
	}

	public void setUnctrlMonthlyOutAltCount(long unctrlMonthlyOutAltCount) {
		this.unctrlMonthlyOutAltCount = unctrlMonthlyOutAltCount;
	}

	public long getUnctrlMonthlyOutAltValue() {
		return unctrlMonthlyOutAltValue;
	}

	public void setUnctrlMonthlyOutAltValue(long unctrlMonthlyOutAltValue) {
		this.unctrlMonthlyOutAltValue = unctrlMonthlyOutAltValue;
	}

	public String getParentProfileID() {
		return parentProfileID;
	}

	public void setParentProfileID(String parentProfileID) {
		this.parentProfileID = parentProfileID;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getIsDefaultDesc() {
		return isDefaultDesc;
	}

	public void setIsDefaultDesc(String isDefaultDesc) {
		this.isDefaultDesc = isDefaultDesc;
	}

	public long getDailySubscriberInCount() {
		return dailySubscriberInCount;
	}

	public void setDailySubscriberInCount(long dailySubscriberInCount) {
		this.dailySubscriberInCount = dailySubscriberInCount;
	}

	public long getWeeklySubscriberInCount() {
		return weeklySubscriberInCount;
	}

	public void setWeeklySubscriberInCount(long weeklySubscriberInCount) {
		this.weeklySubscriberInCount = weeklySubscriberInCount;
	}

	public long getMonthlySubscriberInCount() {
		return monthlySubscriberInCount;
	}

	public void setMonthlySubscriberInCount(long monthlySubscriberInCount) {
		this.monthlySubscriberInCount = monthlySubscriberInCount;
	}

	public long getDailySubscriberInValue() {
		return dailySubscriberInValue;
	}

	public void setDailySubscriberInValue(long dailySubscriberInValue) {
		this.dailySubscriberInValue = dailySubscriberInValue;
	}

	public long getWeeklySubscriberInValue() {
		return weeklySubscriberInValue;
	}

	public void setWeeklySubscriberInValue(long weeklySubscriberInValue) {
		this.weeklySubscriberInValue = weeklySubscriberInValue;
	}

	public long getMonthlySubscriberInValue() {
		return monthlySubscriberInValue;
	}

	public void setMonthlySubscriberInValue(long monthlySubscriberInValue) {
		this.monthlySubscriberInValue = monthlySubscriberInValue;
	}

	public long getDailySubscriberInAltValue() {
		return dailySubscriberInAltValue;
	}

	public void setDailySubscriberInAltValue(long dailySubscriberInAltValue) {
		this.dailySubscriberInAltValue = dailySubscriberInAltValue;
	}

	public long getWeeklySubscriberInAltValue() {
		return weeklySubscriberInAltValue;
	}

	public void setWeeklySubscriberInAltValue(long weeklySubscriberInAltValue) {
		this.weeklySubscriberInAltValue = weeklySubscriberInAltValue;
	}

	public long getMonthlySubscriberInAltValue() {
		return monthlySubscriberInAltValue;
	}

	public void setMonthlySubscriberInAltValue(long monthlySubscriberInAltValue) {
		this.monthlySubscriberInAltValue = monthlySubscriberInAltValue;
	}

	public long getDailySubscriberInAltCount() {
		return dailySubscriberInAltCount;
	}

	public void setDailySubscriberInAltCount(long dailySubscriberInAltCount) {
		this.dailySubscriberInAltCount = dailySubscriberInAltCount;
	}

	public long getWeeklySubscriberInAltCount() {
		return weeklySubscriberInAltCount;
	}

	public void setWeeklySubscriberInAltCount(long weeklySubscriberInAltCount) {
		this.weeklySubscriberInAltCount = weeklySubscriberInAltCount;
	}

	public long getMonthlySubscriberInAltCount() {
		return monthlySubscriberInAltCount;
	}

	public void setMonthlySubscriberInAltCount(long monthlySubscriberInAltCount) {
		this.monthlySubscriberInAltCount = monthlySubscriberInAltCount;
	}

}
