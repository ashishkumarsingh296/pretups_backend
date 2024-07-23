/**
 * @(#)CategorisationVO.java
 *                           Copyright(c) 2005, Bharti Telesoft Ltd.
 *                           All Rights Reserved
 * 
 *                           <description>
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Narendra Kumar 17 Jan 2014 Initital Creation
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           This VO for Categorisation profile
 */

package com.btsl.pretups.categorisation.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CategorisationVO implements Serializable {
    private String _profileId = null;
    private String _networkCode = null;
    private String _userID = null;
    private String _userIdPrefix = null;
    private String _getProfileName = null;
    private String _categoryCode = null;
    private String _channelDomain = null;
    private String _region = null;
    private String _areaCode = null;
    private String _transferType = null;
    private String _countBegin = null;
    private String _countEnd = null;
    private String _amountBegin = null;
    private String _amountEnd = null;
    private String _activedaytBegin = null;
    private String _activedaytEnd = null;
    private String _classType = null;
    private Date _createdOn = null;
    private String _createdBy = null;
    private String _modifiedBy = null;
    private Date _modifiedOn = null;
    private String _code = null;// selected radio button value
    private ArrayList _dataList;

    public ArrayList getDataList() {
        return _dataList;
    }

    public void setDataList(ArrayList list) {
        _dataList = list;
    }

    public String toString() {
        StringBuffer strBuff = new StringBuffer("\nProfileId=" + _profileId);
        strBuff.append("\n NetworkCode=" + _networkCode);
        strBuff.append("\n UserID=" + _userID);
        strBuff.append("\n TransferCountBegin=" + _countBegin);
        strBuff.append("\n Region=" + _region);
        strBuff.append("\n AreaCode=" + _areaCode);
        strBuff.append("\n TransferCountEnd=" + _countEnd);
        strBuff.append("\n TransferAmountBegin=" + _amountBegin);
        strBuff.append("\n TransferAmountEnd=" + _amountEnd);
        strBuff.append("\n ActiveDayBegin=" + _activedaytBegin);
        strBuff.append("\n ActiveDayEnd=" + _activedaytEnd);
        strBuff.append("\n TransferType=" + _transferType);
        strBuff.append("\n ClassType=" + _classType);
        // strBuff.append("\n ProfileName="+_profileName);
        strBuff.append("\n ModifiedBy=" + _modifiedBy);
        strBuff.append("\n ModifyOn=" + _modifiedOn);
        strBuff.append("\n ButtonClassType=" + _code);
        strBuff.append("\n DoaminCode=" + _channelDomain);
        strBuff.append("\n CategoryCode=" + _categoryCode);

        return strBuff.toString();
    }

    public void flush() {
        _code = null;
        _dataList = null;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date on) {
        _createdOn = on;
    }

    public String getRegion() {
        return _region;
    }

    public void setRegion(String _region) {
        this._region = _region;
    }

    public String getAreaCode() {
        return _areaCode;
    }

    public void setAreaCode(String code) {
        _areaCode = code;
    }

    public String getTransferType() {
        return _transferType;
    }

    public void setTransferType(String type) {
        _transferType = type;
    }

    public String getCountBegin() {
        return _countBegin;
    }

    public void setCountBegin(String begin) {
        _countBegin = begin;
    }

    public String getCountEnd() {
        return _countEnd;
    }

    public void setCountEnd(String end) {
        _countEnd = end;
    }

    public String getAmountBegin() {
        return _amountBegin;
    }

    public void setAmountBegin(String begin) {
        _amountBegin = begin;
    }

    public String getAmountEnd() {
        return _amountEnd;
    }

    public void setAmountEnd(String end) {
        _amountEnd = end;
    }

    public String getActivedaytBegin() {
        return _activedaytBegin;
    }

    public void setActivedaytBegin(String begin) {
        _activedaytBegin = begin;
    }

    public String getActivedaytEnd() {
        return _activedaytEnd;
    }

    public void setActivedaytEnd(String end) {
        _activedaytEnd = end;
    }

    public String getClassType() {
        return _classType;
    }

    public void setClassType(String type) {
        _classType = type;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String code) {
        _categoryCode = code;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String _userid) {
        _userID = _userid;
    }

    public String getUserIdPrefix() {
        return _userIdPrefix;
    }

    public void setUserIdPrefix(String idPrefix) {
        _userIdPrefix = idPrefix;
    }

    public String getProfileId() {
        return _profileId;
    }

    public void setProfileId(String id) {
        _profileId = id;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    public String getProfileName() {
        return _getProfileName;
    }

    public void setProfileName(String profileName) {
        _getProfileName = profileName;
    }

    public String getChannelDomain() {
        return _channelDomain;
    }

    public void setChannelDomain(String domain) {
        _channelDomain = domain;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String by) {
        _modifiedBy = by;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String _code) {
        this._code = _code;
    }

}
