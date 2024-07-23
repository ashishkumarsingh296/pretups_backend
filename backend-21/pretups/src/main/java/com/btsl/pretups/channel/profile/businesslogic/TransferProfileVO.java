package com.btsl.pretups.channel.profile.businesslogic;

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

public class TransferProfileVO implements Serializable {

    private long _lastModifiedTime = 0;

    private boolean _unctrlTransferFlag;

    private String _profileId;

    private String _profileName;

    private String _shortName;

    private String _status;

    private String _profileStatusName;

    private String _description;

    private long _dailyInCount;

    private long _dailyInValue;

    private long _weeklyInCount;

    private long _weeklyInValue;

    private long _monthlyInCount;

    private long _monthlyInValue;

    private long _dailyOutCount;

    private long _dailyOutValue;

    private long _weeklyOutCount;

    private long _weeklyOutValue;

    private long _monthlyOutCount;

    private long _monthlyOutValue;

    private long _dailySubscriberOutCount;

    private long _weeklySubscriberOutCount;

    private long _monthlySubscriberOutCount;

    private long _dailySubscriberOutValue;

    private long _weeklySubscriberOutValue;

    private long _monthlySubscriberOutValue;

    private long _unctrlDailyInCount;

    private long _unctrlDailyInValue;

    private long _unctrlWeeklyInCount;

    private long _unctrlWeeklyInValue;

    private long _unctrlMonthlyInCount;

    private long _unctrlMonthlyInValue;

    private long _unctrlDailyOutCount;

    private long _unctrlDailyOutValue;

    private long _unctrlWeeklyOutCount;

    private long _unctrlWeeklyOutValue;

    private long _unctrlMonthlyOutCount;

    private long _unctrlMonthlyOutValue;

    private String _createdBy;

    private String _modifiedBy;

    private Date _createdOn;

    private Date _modifiedOn;

    private String _networkCode;

    private String _category;
    private String _categoryName;

    private ArrayList _profileProductList;

    private long _dailyC2STransferOutCount;
    private long _dailyC2STransferOutValue;
    private long _weeklyC2STransferOutCount;
    private long _weeklyC2STransferOutValue;
    private long _monthlyC2STransferOutCount;
    private long _monthlyC2STransferOutValue;

    private boolean _isUpdateRecord = true;

    // Alerting variables
    private long _dailyInAltCount;
    private long _dailyInAltValue;
    private long _dailyOutAltCount;
    private long _dailyOutAltValue;

    private long _weeklyInAltCount;
    private long _weeklyInAltValue;
    private long _weeklyOutAltCount;
    private long _weeklyOutAltValue;

    private long _dailySubscriberOutAltCount;
    private long _weeklySubscriberOutAltCount;
    private long _monthlySubscriberOutAltCount;

    private long _dailySubscriberOutAltValue;
    private long _weeklySubscriberOutAltValue;
    private long _monthlySubscriberOutAltValue;

    private long _monthlyInAltCount;
    private long _monthlyInAltValue;
    private long _monthlyOutAltCount;
    private long _monthlyOutAltValue;

    private long _unctrlDailyInAltCount;
    private long _unctrlDailyInAltValue;
    private long _unctrlDailyOutAltCount;
    private long _unctrlDailyOutAltValue;

    private long _unctrlWeeklyInAltCount;
    private long _unctrlWeeklyInAltValue;
    private long _unctrlWeeklyOutAltCount;
    private long _unctrlWeeklyOutAltValue;

    private long _unctrlMonthlyInAltCount;
    private long _unctrlMonthlyInAltValue;
    private long _unctrlMonthlyOutAltCount;
    private long _unctrlMonthlyOutAltValue;

    private String _parentProfileID = null;

    private String _isDefault = null;
    private String _isDefaultDesc = "N";

    // 6.4 changes
    private long _dailySubscriberInCount;
    private long _weeklySubscriberInCount;
    private long _monthlySubscriberInCount;
    private long _dailySubscriberInValue;
    private long _weeklySubscriberInValue;
    private long _monthlySubscriberInValue;
    private long _dailySubscriberInAltValue;
    private long _weeklySubscriberInAltValue;
    private long _monthlySubscriberInAltValue;
    private long _dailySubscriberInAltCount;
    private long _weeklySubscriberInAltCount;
    private long _monthlySubscriberInAltCount;

    // roam penalty
    private long _dailyRoamAmount;

    public long getDailyRoamAmount() {
        return _dailyRoamAmount;
    }

    public void setDailyRoamAmount(long _dailyRoamAmount) {
        this._dailyRoamAmount = _dailyRoamAmount;
    }

    /**
     * @return Returns the categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param categoryName
     *            The categoryName to set.
     */
    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public ArrayList getProfileProductList() {
        return _profileProductList;
    }

    public void setProfileProductList(ArrayList profileProductList) {
        this._profileProductList = profileProductList;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String category) {
        _category = category;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public long getDailyInCount() {
        return _dailyInCount;
    }

    public void setDailyInCount(long dailyInCount) {
        _dailyInCount = dailyInCount;
    }

    public long getDailyInValue() {
        return _dailyInValue;
    }

    public void setDailyInValue(long dailyInValue) {
        _dailyInValue = dailyInValue;
    }

    public long getDailyOutCount() {
        return _dailyOutCount;
    }

    public void setDailyOutCount(long dailyOutCount) {
        _dailyOutCount = dailyOutCount;
    }

    public long getDailyOutValue() {
        return _dailyOutValue;
    }

    public void setDailyOutValue(long dailyOutValue) {
        _dailyOutValue = dailyOutValue;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    /**
     * @return Returns the profileStatusName.
     */
    public String getProfileStatusName() {
        return _profileStatusName;
    }

    /**
     * @param profileStatusName
     *            The profileStatusName to set.
     */
    public void setProfileStatusName(String profileStatusName) {
        _profileStatusName = profileStatusName;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public long getMonthlyInCount() {
        return _monthlyInCount;
    }

    public void setMonthlyInCount(long monthlyInCount) {
        _monthlyInCount = monthlyInCount;
    }

    public long getMonthlyInValue() {
        return _monthlyInValue;
    }

    public void setMonthlyInValue(long monthlyInValue) {
        _monthlyInValue = monthlyInValue;
    }

    public long getMonthlyOutCount() {
        return _monthlyOutCount;
    }

    public void setMonthlyOutCount(long monthlyOutCount) {
        _monthlyOutCount = monthlyOutCount;
    }

    public long getMonthlyOutValue() {
        return _monthlyOutValue;
    }

    public void setMonthlyOutValue(long monthlyOutValue) {
        _monthlyOutValue = monthlyOutValue;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getProfileId() {
        return _profileId;
    }

    public void setProfileId(String profileId) {
        _profileId = profileId;
    }

    public String getProfileName() {
        return _profileName;
    }

    public void setProfileName(String profileName) {
        _profileName = profileName;
    }

    public String getShortName() {
        return _shortName;
    }

    public void setShortName(String shortName) {
        _shortName = shortName;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public long getUnctrlDailyInCount() {
        return _unctrlDailyInCount;
    }

    public void setUnctrlDailyInCount(long unctrlDailyInCount) {
        _unctrlDailyInCount = unctrlDailyInCount;
    }

    public long getUnctrlDailyInValue() {
        return _unctrlDailyInValue;
    }

    public void setUnctrlDailyInValue(long unctrlDailyInValue) {
        _unctrlDailyInValue = unctrlDailyInValue;
    }

    public long getUnctrlDailyOutCount() {
        return _unctrlDailyOutCount;
    }

    public void setUnctrlDailyOutCount(long unctrlDailyOutCount) {
        _unctrlDailyOutCount = unctrlDailyOutCount;
    }

    public long getUnctrlDailyOutValue() {
        return _unctrlDailyOutValue;
    }

    public void setUnctrlDailyOutValue(long unctrlDailyOutValue) {
        _unctrlDailyOutValue = unctrlDailyOutValue;
    }

    public long getUnctrlMonthlyInCount() {
        return _unctrlMonthlyInCount;
    }

    public void setUnctrlMonthlyInCount(long unctrlMonthlyInCount) {
        _unctrlMonthlyInCount = unctrlMonthlyInCount;
    }

    public long getUnctrlMonthlyInValue() {
        return _unctrlMonthlyInValue;
    }

    public void setUnctrlMonthlyInValue(long unctrlMonthlyInValue) {
        _unctrlMonthlyInValue = unctrlMonthlyInValue;
    }

    public long getUnctrlMonthlyOutCount() {
        return _unctrlMonthlyOutCount;
    }

    public void setUnctrlMonthlyOutCount(long unctrlMonthlyOutCount) {
        _unctrlMonthlyOutCount = unctrlMonthlyOutCount;
    }

    public long getUnctrlMonthlyOutValue() {
        return _unctrlMonthlyOutValue;
    }

    public void setUnctrlMonthlyOutValue(long unctrlMonthlyOutValue) {
        _unctrlMonthlyOutValue = unctrlMonthlyOutValue;
    }

    public boolean isUnctrlTransferFlag() {
        return _unctrlTransferFlag;
    }

    public void setUnctrlTransferFlag(boolean unctrlTransferFlag) {
        _unctrlTransferFlag = unctrlTransferFlag;
    }

    public long getUnctrlWeeklyInCount() {
        return _unctrlWeeklyInCount;
    }

    public void setUnctrlWeeklyInCount(long unctrlWeeklyInCount) {
        _unctrlWeeklyInCount = unctrlWeeklyInCount;
    }

    public long getUnctrlWeeklyInValue() {
        return _unctrlWeeklyInValue;
    }

    public void setUnctrlWeeklyInValue(long unctrlWeeklyInValue) {
        _unctrlWeeklyInValue = unctrlWeeklyInValue;
    }

    public long getUnctrlWeeklyOutCount() {
        return _unctrlWeeklyOutCount;
    }

    public void setUnctrlWeeklyOutCount(long unctrlWeeklyOutCount) {
        _unctrlWeeklyOutCount = unctrlWeeklyOutCount;
    }

    public long getUnctrlWeeklyOutValue() {
        return _unctrlWeeklyOutValue;
    }

    public void setUnctrlWeeklyOutValue(long unctrlWeeklyOutValue) {
        _unctrlWeeklyOutValue = unctrlWeeklyOutValue;
    }

    public long getWeeklyInCount() {
        return _weeklyInCount;
    }

    public void setWeeklyInCount(long weeklyInCount) {
        _weeklyInCount = weeklyInCount;
    }

    public long getWeeklyInValue() {
        return _weeklyInValue;
    }

    public void setWeeklyInValue(long weeklyInValue) {
        _weeklyInValue = weeklyInValue;
    }

    public long getWeeklyOutCount() {
        return _weeklyOutCount;
    }

    public void setWeeklyOutCount(long weeklyOutCount) {
        _weeklyOutCount = weeklyOutCount;
    }

    public long getWeeklyOutValue() {
        return _weeklyOutValue;
    }

    public void setWeeklyOutValue(long weeklyOutValue) {
        _weeklyOutValue = weeklyOutValue;
    }

    public String toString() {
        final StringBuilder strBuild = new StringBuilder("Profile Name=" + _profileName);

        strBuild.append(" ,Short Name=").append(_shortName);
        strBuild.append(" ,Description=").append(_description);
        strBuild.append(" ,Daily In Count=").append(_dailyInCount);
        strBuild.append(" ,Daily In Value=").append(_dailyInValue);
        strBuild.append(" ,Daily Out Count=").append(_dailyOutCount);
        strBuild.append(" ,Daily Out Value=").append(_dailyOutValue);
        strBuild.append(" ,Weekly In Count=").append(_weeklyInCount);
        strBuild.append(" ,Weekly In Value=").append(_weeklyInValue);
        strBuild.append(" ,Weekly Out Count=").append(_weeklyOutCount);
        strBuild.append(" ,Weekly Out Value=").append(_weeklyOutValue);
        strBuild.append(" ,Monthly In Count=").append(_monthlyInCount);
        strBuild.append(" ,Monthly In Value=").append(_monthlyInValue);
        strBuild.append(" ,Monthly Out Count=").append(_monthlyOutCount);
        strBuild.append(" ,Monthly Out Value=").append(_monthlyOutValue);
        strBuild.append(" ,Daily Subscriber Out Count=").append(_dailySubscriberOutCount);
        strBuild.append(" ,Weekly Subscriber Out Count=").append(_weeklySubscriberOutCount);
        strBuild.append(" ,Monthly Subscriber Out Count=").append(_monthlySubscriberOutCount);
        strBuild.append(" ,Daily Subscriber Out Value=").append(_dailySubscriberOutValue);
        strBuild.append(" ,Weekly Subscriber Out Value=").append(_weeklySubscriberOutValue);
        strBuild.append(" ,Monthly Subscriber Out Value=").append(_monthlySubscriberOutValue);

        strBuild.append(" ,Daily In AltCount=").append(_dailyInAltCount);
        strBuild.append(" ,Daily In AltValue=").append(_dailyInAltValue);
        strBuild.append(" ,Daily Out AltCount=").append(_dailyOutAltCount);
        strBuild.append(" ,Daily Out AltValue=").append(_dailyOutAltValue);
        strBuild.append(" ,Weekly In AltCount=").append(_weeklyInAltCount);
        strBuild.append(" ,Weekly In AltValue=").append(_weeklyInAltValue);
        strBuild.append(" ,Weekly Out AltCount=").append(_weeklyOutAltCount);
        strBuild.append(" ,Weekly Out AltValue=").append(_weeklyOutAltValue);
        strBuild.append(" ,Monthly In AltCount=").append(_monthlyInAltCount);
        strBuild.append(" ,Monthly In AltValue=").append(_monthlyInAltValue);
        strBuild.append(" ,Monthly Out AltCount=").append(_monthlyOutAltCount);
        strBuild.append(" ,Monthly Out AltValue=").append(_monthlyOutAltValue);
        strBuild.append(" ,Daily Subscriber Out AltCount=").append(_dailySubscriberOutAltCount);
        strBuild.append(" ,Weekly Subscriber Out AltCount=").append(_weeklySubscriberOutAltCount);
        strBuild.append(" ,Monthly Subscriber Out AltCount=").append(_monthlySubscriberOutAltCount);
        strBuild.append(" ,Daily Subscriber Out AltValue=").append(_dailySubscriberOutAltValue);
        strBuild.append(" ,Weekly Subscriber Out AltValue=").append(_weeklySubscriberOutAltValue);
        strBuild.append(" ,Monthly Subscriber Out AltValue=").append(_monthlySubscriberOutAltValue);

        if (_unctrlTransferFlag = true) {
            strBuild.append(" ,Outside Daily In Count=").append(_unctrlDailyInCount);
            strBuild.append(" ,Outside Daily In Value=").append(_unctrlDailyInValue);
            strBuild.append(" ,Outside Daily Out Count=").append(_unctrlDailyOutCount);
            strBuild.append(" ,Outside Daily Out Value=").append(_unctrlDailyOutValue);
            strBuild.append(" ,Outside Weekly In Count=").append(_unctrlWeeklyInCount);
            strBuild.append(" ,Outside Weekly In Value=").append(_unctrlWeeklyInValue);
            strBuild.append(" ,Outside Weekly Out Count=").append(_unctrlWeeklyOutCount);
            strBuild.append(" ,Outside Weekly Out Value=").append(_unctrlWeeklyOutValue);
            strBuild.append(" ,Outside Monthly In Count=").append(_unctrlMonthlyInCount);
            strBuild.append(" ,Outside Monthly In Value=").append(_unctrlMonthlyInValue);
            strBuild.append(" ,Outside Monthly Out Count=").append(_unctrlMonthlyOutCount);
            strBuild.append(" ,Outside Monthly Out Value=").append(_unctrlMonthlyOutValue);

            strBuild.append(" ,Outside Daily In AltCount=").append(_unctrlDailyInAltCount);
            strBuild.append(" ,Outside Daily In AltValue=").append(_unctrlDailyInAltValue);
            strBuild.append(" ,Outside Daily Out AltCount=").append(_unctrlDailyOutAltCount);
            strBuild.append(" ,Outside Daily Out AltValue=").append(_unctrlDailyOutAltValue);
            strBuild.append(" ,Outside Weekly In AltCount=").append(_unctrlWeeklyInAltCount);
            strBuild.append(" ,Outside Weekly In AltValue=").append(_unctrlWeeklyInAltValue);
            strBuild.append(" ,Outside Weekly Out AltCount=").append(_unctrlWeeklyOutAltCount);
            strBuild.append(" ,Outside Weekly Out AltValue=").append(_unctrlWeeklyOutAltValue);
            strBuild.append(" ,Outside Monthly In AltCount=").append(_unctrlMonthlyInAltCount);
            strBuild.append(" ,Outside Monthly In AltValue=").append(_unctrlMonthlyInAltValue);
            strBuild.append(" ,Outside Monthly Out AltCount=").append(_unctrlMonthlyOutAltCount);
            strBuild.append(" ,Outside Monthly Out AltValue=").append(_unctrlMonthlyOutAltValue);

        }
        strBuild.append(" ,Created By=").append(_createdBy);
        strBuild.append(" ,Modufied By=").append(_modifiedBy);
        strBuild.append(" ,Created On=").append(_createdOn);
        strBuild.append(" ,Modufied ON=").append(_modifiedOn);
        strBuild.append(" ,Last Modified Time=").append(_lastModifiedTime);
        strBuild.append(" ,_parentProfileID=").append(_parentProfileID);
        return strBuild.toString();
    }

    public long getDailySubscriberOutCount() {
        return _dailySubscriberOutCount;
    }

    public void setDailySubscriberOutCount(long dailySubscriberOutCount) {
        _dailySubscriberOutCount = dailySubscriberOutCount;
    }

    public long getDailySubscriberOutValue() {
        return _dailySubscriberOutValue;
    }

    public void setDailySubscriberOutValue(long dailySubscriberOutValue) {
        _dailySubscriberOutValue = dailySubscriberOutValue;
    }

    public long getMonthlySubscriberOutCount() {
        return _monthlySubscriberOutCount;
    }

    public void setMonthlySubscriberOutCount(long monthlySubscriberOutCount) {
        _monthlySubscriberOutCount = monthlySubscriberOutCount;
    }

    public long getMonthlySubscriberOutValue() {
        return _monthlySubscriberOutValue;
    }

    public void setMonthlySubscriberOutValue(long monthlySubscriberOutValue) {
        _monthlySubscriberOutValue = monthlySubscriberOutValue;
    }

    public long getWeeklySubscriberOutCount() {
        return _weeklySubscriberOutCount;
    }

    public void setWeeklySubscriberOutCount(long weeklySubscriberOutCount) {
        _weeklySubscriberOutCount = weeklySubscriberOutCount;
    }

    public long getWeeklySubscriberOutValue() {
        return _weeklySubscriberOutValue;
    }

    public void setWeeklySubscriberOutValue(long weeklySubscriberOutValue) {
        _weeklySubscriberOutValue = weeklySubscriberOutValue;
    }

    public long getDailyC2STransferOutCount() {
        return _dailyC2STransferOutCount;
    }

    public void setDailyC2STransferOutCount(long dailyC2STransferOutCount) {
        _dailyC2STransferOutCount = dailyC2STransferOutCount;
    }

    public long getDailyC2STransferOutValue() {
        return _dailyC2STransferOutValue;
    }

    public void setDailyC2STransferOutValue(long dailyC2STransferOutValue) {
        _dailyC2STransferOutValue = dailyC2STransferOutValue;
    }

    public long getMonthlyC2STransferOutCount() {
        return _monthlyC2STransferOutCount;
    }

    public void setMonthlyC2STransferOutCount(long monthlyC2STransferOutCount) {
        _monthlyC2STransferOutCount = monthlyC2STransferOutCount;
    }

    public long getMonthlyC2STransferOutValue() {
        return _monthlyC2STransferOutValue;
    }

    public void setMonthlyC2STransferOutValue(long monthlyC2STransferOutValue) {
        _monthlyC2STransferOutValue = monthlyC2STransferOutValue;
    }

    public long getWeeklyC2STransferOutCount() {
        return _weeklyC2STransferOutCount;
    }

    public void setWeeklyC2STransferOutCount(long weeklyC2STransferOutCount) {
        _weeklyC2STransferOutCount = weeklyC2STransferOutCount;
    }

    public long getWeeklyC2STransferOutValue() {
        return _weeklyC2STransferOutValue;
    }

    public void setWeeklyC2STransferOutValue(long weeklyC2STransferOutValue) {
        _weeklyC2STransferOutValue = weeklyC2STransferOutValue;
    }

    public boolean isUpdateRecord() {
        return _isUpdateRecord;
    }

    public void setUpdateRecord(boolean isUpdateRecord) {
        _isUpdateRecord = isUpdateRecord;
    }

    public long getDailyInAltCount() {
        return _dailyInAltCount;
    }

    public void setDailyInAltCount(long dailyInAltCount) {
        _dailyInAltCount = dailyInAltCount;
    }

    public long getDailyInAltValue() {
        return _dailyInAltValue;
    }

    public void setDailyInAltValue(long dailyInAltValue) {
        _dailyInAltValue = dailyInAltValue;
    }

    public long getDailyOutAltCount() {
        return _dailyOutAltCount;
    }

    public void setDailyOutAltCount(long dailyOutAltCount) {
        _dailyOutAltCount = dailyOutAltCount;
    }

    public long getDailyOutAltValue() {
        return _dailyOutAltValue;
    }

    public void setDailyOutAltValue(long dailyOutAltValue) {
        _dailyOutAltValue = dailyOutAltValue;
    }

    public long getDailySubscriberOutAltCount() {
        return _dailySubscriberOutAltCount;
    }

    public void setDailySubscriberOutAltCount(long dailySubscriberOutAltCount) {
        _dailySubscriberOutAltCount = dailySubscriberOutAltCount;
    }

    public long getDailySubscriberOutAltValue() {
        return _dailySubscriberOutAltValue;
    }

    public void setDailySubscriberOutAltValue(long dailySubscriberOutAltValue) {
        _dailySubscriberOutAltValue = dailySubscriberOutAltValue;
    }

    public long getMonthlyInAltCount() {
        return _monthlyInAltCount;
    }

    public void setMonthlyInAltCount(long monthlyInAltCount) {
        _monthlyInAltCount = monthlyInAltCount;
    }

    public long getMonthlyInAltValue() {
        return _monthlyInAltValue;
    }

    public void setMonthlyInAltValue(long monthlyInAltValue) {
        _monthlyInAltValue = monthlyInAltValue;
    }

    public long getMonthlyOutAltCount() {
        return _monthlyOutAltCount;
    }

    public void setMonthlyOutAltCount(long monthlyOutAltCount) {
        _monthlyOutAltCount = monthlyOutAltCount;
    }

    public long getMonthlyOutAltValue() {
        return _monthlyOutAltValue;
    }

    public void setMonthlyOutAltValue(long monthlyOutAltValue) {
        _monthlyOutAltValue = monthlyOutAltValue;
    }

    public long getMonthlySubscriberOutAltCount() {
        return _monthlySubscriberOutAltCount;
    }

    public void setMonthlySubscriberOutAltCount(long monthlySubscriberOutAltCount) {
        _monthlySubscriberOutAltCount = monthlySubscriberOutAltCount;
    }

    public long getMonthlySubscriberOutAltValue() {
        return _monthlySubscriberOutAltValue;
    }

    public void setMonthlySubscriberOutAltValue(long monthlySubscriberOutAltValue) {
        _monthlySubscriberOutAltValue = monthlySubscriberOutAltValue;
    }

    public long getUnctrlDailyInAltCount() {
        return _unctrlDailyInAltCount;
    }

    public void setUnctrlDailyInAltCount(long unctrlDailyInAltCount) {
        _unctrlDailyInAltCount = unctrlDailyInAltCount;
    }

    public long getUnctrlDailyInAltValue() {
        return _unctrlDailyInAltValue;
    }

    public void setUnctrlDailyInAltValue(long unctrlDailyInAltValue) {
        _unctrlDailyInAltValue = unctrlDailyInAltValue;
    }

    public long getUnctrlDailyOutAltCount() {
        return _unctrlDailyOutAltCount;
    }

    public void setUnctrlDailyOutAltCount(long unctrlDailyOutAltCount) {
        _unctrlDailyOutAltCount = unctrlDailyOutAltCount;
    }

    public long getUnctrlDailyOutAltValue() {
        return _unctrlDailyOutAltValue;
    }

    public void setUnctrlDailyOutAltValue(long unctrlDailyOutAltValue) {
        _unctrlDailyOutAltValue = unctrlDailyOutAltValue;
    }

    public long getUnctrlMonthlyInAltCount() {
        return _unctrlMonthlyInAltCount;
    }

    public void setUnctrlMonthlyInAltCount(long unctrlMonthlyInAltCount) {
        _unctrlMonthlyInAltCount = unctrlMonthlyInAltCount;
    }

    public long getUnctrlMonthlyInAltValue() {
        return _unctrlMonthlyInAltValue;
    }

    public void setUnctrlMonthlyInAltValue(long unctrlMonthlyInAltValue) {
        _unctrlMonthlyInAltValue = unctrlMonthlyInAltValue;
    }

    public long getUnctrlMonthlyOutAltCount() {
        return _unctrlMonthlyOutAltCount;
    }

    public void setUnctrlMonthlyOutAltCount(long unctrlMonthlyOutAltCount) {
        _unctrlMonthlyOutAltCount = unctrlMonthlyOutAltCount;
    }

    public long getUnctrlMonthlyOutAltValue() {
        return _unctrlMonthlyOutAltValue;
    }

    public void setUnctrlMonthlyOutAltValue(long unctrlMonthlyOutAltValue) {
        _unctrlMonthlyOutAltValue = unctrlMonthlyOutAltValue;
    }

    public long getUnctrlWeeklyInAltCount() {
        return _unctrlWeeklyInAltCount;
    }

    public void setUnctrlWeeklyInAltCount(long unctrlWeeklyInAltCount) {
        _unctrlWeeklyInAltCount = unctrlWeeklyInAltCount;
    }

    public long getUnctrlWeeklyInAltValue() {
        return _unctrlWeeklyInAltValue;
    }

    public void setUnctrlWeeklyInAltValue(long unctrlWeeklyInAltValue) {
        _unctrlWeeklyInAltValue = unctrlWeeklyInAltValue;
    }

    public long getUnctrlWeeklyOutAltCount() {
        return _unctrlWeeklyOutAltCount;
    }

    public void setUnctrlWeeklyOutAltCount(long unctrlWeeklyOutAltCount) {
        _unctrlWeeklyOutAltCount = unctrlWeeklyOutAltCount;
    }

    public long getUnctrlWeeklyOutAltValue() {
        return _unctrlWeeklyOutAltValue;
    }

    public void setUnctrlWeeklyOutAltValue(long unctrlWeeklyOutAltValue) {
        _unctrlWeeklyOutAltValue = unctrlWeeklyOutAltValue;
    }

    public long getWeeklyInAltCount() {
        return _weeklyInAltCount;
    }

    public void setWeeklyInAltCount(long weeklyInAltCount) {
        _weeklyInAltCount = weeklyInAltCount;
    }

    public long getWeeklyInAltValue() {
        return _weeklyInAltValue;
    }

    public void setWeeklyInAltValue(long weeklyInAltValue) {
        _weeklyInAltValue = weeklyInAltValue;
    }

    public long getWeeklyOutAltCount() {
        return _weeklyOutAltCount;
    }

    public void setWeeklyOutAltCount(long weeklyOutAltCount) {
        _weeklyOutAltCount = weeklyOutAltCount;
    }

    public long getWeeklyOutAltValue() {
        return _weeklyOutAltValue;
    }

    public void setWeeklyOutAltValue(long weeklyOutAltValue) {
        _weeklyOutAltValue = weeklyOutAltValue;
    }

    public long getWeeklySubscriberOutAltCount() {
        return _weeklySubscriberOutAltCount;
    }

    public void setWeeklySubscriberOutAltCount(long weeklySubscriberOutAltCount) {
        _weeklySubscriberOutAltCount = weeklySubscriberOutAltCount;
    }

    public long getWeeklySubscriberOutAltValue() {
        return _weeklySubscriberOutAltValue;
    }

    public void setWeeklySubscriberOutAltValue(long weeklySubscriberOutAltValue) {
        _weeklySubscriberOutAltValue = weeklySubscriberOutAltValue;
    }

    public String getParentProfileID() {
        return _parentProfileID;
    }

    public void setParentProfileID(String parentProfileID) {
        _parentProfileID = parentProfileID;
    }

    /**
     * @return the isDefault
     */
    public String getIsDefault() {
        return _isDefault;
    }

    /**
     * @param isDefault
     *            the isDefault to set
     */
    public void setIsDefault(String isDefault) {
        _isDefault = isDefault;
    }

    /**
     * @return the isDefaultDesc
     */
    public String getIsDefaultDesc() {
        return _isDefaultDesc;
    }

    /**
     * @param isDefaultDesc
     *            the isDefaultDesc to set
     */
    public void setIsDefaultDesc(String isDefaultDesc) {
        _isDefaultDesc = isDefaultDesc;
    }

    /*
     * reversal
     */
    public long getDailySubscriberInCount() {
        return _dailySubscriberInCount;
    }

    public void setDailySubscriberInCount(long subscriberInCount) {
        _dailySubscriberInCount = subscriberInCount;
    }

    public long getWeeklySubscriberInCount() {
        return _weeklySubscriberInCount;
    }

    public void setWeeklySubscriberInCount(long subscriberInCount) {
        _weeklySubscriberInCount = subscriberInCount;
    }

    public long getMonthlySubscriberInCount() {
        return _monthlySubscriberInCount;
    }

    public void setMonthlySubscriberInCount(long subscriberInCount) {
        _monthlySubscriberInCount = subscriberInCount;
    }

    public long getDailySubscriberInValue() {
        return _dailySubscriberInValue;
    }

    public void setDailySubscriberInValue(long subscriberInValue) {
        _dailySubscriberInValue = subscriberInValue;
    }

    public long getWeeklySubscriberInValue() {
        return _weeklySubscriberInValue;
    }

    public void setWeeklySubscriberInValue(long subscriberInValue) {
        _weeklySubscriberInValue = subscriberInValue;
    }

    public long getMonthlySubscriberInValue() {
        return _monthlySubscriberInValue;
    }

    public void setMonthlySubscriberInValue(long subscriberInValue) {
        _monthlySubscriberInValue = subscriberInValue;
    }

    public long getDailySubscriberInAltValue() {
        return _dailySubscriberInAltValue;
    }

    public void setDailySubscriberInAltValue(long subscriberInAltValue) {
        _dailySubscriberInAltValue = subscriberInAltValue;
    }

    public long getWeeklySubscriberInAltValue() {
        return _weeklySubscriberInAltValue;
    }

    public void setWeeklySubscriberInAltValue(long subscriberInAltValue) {
        _weeklySubscriberInAltValue = subscriberInAltValue;
    }

    public long getMonthlySubscriberInAltValue() {
        return _monthlySubscriberInAltValue;
    }

    public void setMonthlySubscriberInAltValue(long subscriberInAltValue) {
        _monthlySubscriberInAltValue = subscriberInAltValue;
    }

    public long getDailySubscriberInAltCount() {
        return _dailySubscriberInAltCount;
    }

    public void setDailySubscriberInAltCount(long subscriberInAltCount) {
        _dailySubscriberInAltCount = subscriberInAltCount;
    }

    public long getWeeklySubscriberInAltCount() {
        return _weeklySubscriberInAltCount;
    }

    public void setWeeklySubscriberInAltCount(long subscriberInAltCount) {
        _weeklySubscriberInAltCount = subscriberInAltCount;
    }

    public long getMonthlySubscriberInAltCount() {
        return _monthlySubscriberInAltCount;
    }

    public void setMonthlySubscriberInAltCount(long subscriberInAltCount) {
        _monthlySubscriberInAltCount = subscriberInAltCount;
    }

}
