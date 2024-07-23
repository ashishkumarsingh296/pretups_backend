/*
 * @# ChannelUserTransferVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Aug 30, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.user.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.util.BTSLUtil;

public class ChannelUserTransferVO implements Serializable {

    private String _userID;
    private String _userCategoryCode;
    private String _userCategoryDesc;
    private String _userName;
    private String _fromParentID;
    private String _fromOwnerID;
    private String _status;
    private String _toOwnerID;
    private String _toParentID;
    private String _domainCode;
    private String _zoneCode;
    private String _networkCode;

    private String _modifiedBy;
    private String _createdBy;
    private Date _modifiedOn;
    private Date _createdOn;
    private long _lastModifiedTime;
    private ArrayList _userHierarchyList = null;
    private String _multibox;

    // 5.1.3
    private String _serviceType;
 

	private String _zoneName;
    private String _domainName;
    private String _categoryName;
    private String _msisdn;
    private String _loginId = null;
    private String _parentUserName;
    private String _toParentUserName = null;
 // added for channel user transfer 
	private boolean _isOperationNotAllow=false;
	private String _toParentUserID;
	private String _parentUserID;
	private String _domainCodeDesc;
	private ArrayList _domainList=null;
	private String _geographicalCode;
	private String _otp;	
	private int _invalidOtpCount;

	public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param type
     *            The _serviceType to set.
     */
    public void setServiceType(String type) {
        _serviceType = type;
    }

    /**
     * @return Returns the _categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param name
     *            The _categoryName to set.
     */
    public void setCategoryName(String name) {
        _categoryName = name;
    }

    /**
     * @return Returns the _domainName.
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * @param name
     *            The _domainName to set.
     */
    public void setDomainName(String name) {
        _domainName = name;
    }

    /**
     * @return Returns the _zoneName.
     */
    public String getZoneName() {
        return _zoneName;
    }

    /**
     * @param name
     *            The _zoneName to set.
     */
    public void setZoneName(String name) {
        _zoneName = name;
    }

    // 5.1.3 End

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

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
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

    public ChannelUserTransferVO() {

    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        this._userID = userID;
    }

    public String getUserCategoryCode() {
        return _userCategoryCode;
    }

    public void setUserCategoryCode(String categoryCode) {
        this._userCategoryCode = categoryCode;
    }

    public String getFromOwnerID() {
        return _fromOwnerID;
    }

    public void setFromOwnerID(String ownerID) {
        this._fromOwnerID = ownerID;
    }

    public String getFromParentID() {
        return _fromParentID;
    }

    public void setFromParentID(String parentID) {
        this._fromParentID = parentID;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        this._status = status;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        this._userName = userName;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public String getToOwnerID() {
        return _toOwnerID;
    }

    public void setToOwnerID(String toOwnerID) {
        _toOwnerID = toOwnerID;
    }

    public String getToParentID() {
        return _toParentID;
    }

    public void setToParentID(String toParentID) {
        _toParentID = toParentID;
    }

    public String getZoneCode() {
        return _zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        _zoneCode = zoneCode;
    }

    public ArrayList getUserHierarchyList() {
        return _userHierarchyList;
    }

    public void setUserHierarchyList(ArrayList userHierarchyList) {
        _userHierarchyList = userHierarchyList;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getMultibox() {
        return _multibox;
    }

    public void setMultibox(String multibox) {
        _multibox = multibox;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append("_createdBy=" + _createdBy);
        sbf.append(",_domainCode=" + _domainCode);
        sbf.append(",_fromOwnerID=" + _fromOwnerID);
        sbf.append(",_fromParentID=" + _fromParentID);
        sbf.append(",_lastModifiedTime=" + _lastModifiedTime);
        sbf.append(",_modifiedBy=" + _modifiedBy);
        sbf.append(",_status=" + _status);
        sbf.append(",_toOwnerID=" + _toOwnerID);
        sbf.append(",_toParentID=" + _toParentID);
        sbf.append(",_userCategoryCode=" + _userCategoryCode);
        sbf.append(",_userID=" + _userID);
        sbf.append(",_userName=" + _userName);
        sbf.append(",_zoneCode=" + _zoneCode);
        sbf.append(",_createdOn=" + _createdOn);
        sbf.append(",_modifiedOn=" + _modifiedOn);
        sbf.append(",_networkCode=" + _networkCode);
        sbf.append(",_userHierarchyList=" + _userHierarchyList);
        sbf.append(",_multibox=" + _multibox);
        sbf.append(",_invalidOtpCount=" + _invalidOtpCount);
        if(!BTSLUtil.isNullString(_otp))
        	sbf.append(",Otp=" + BTSLUtil.encryptText(_otp));
        // code added for channel user transfer
        sbf.append(",_toParentUserID=" + _toParentUserID);
        sbf.append(",_parentUserID=" + _parentUserID);
        sbf.append(",_domainCodeDesc=" + _domainCodeDesc);
        sbf.append(",_domainList=" + _domainList);
        sbf.append(",_geographicalCode=" + _geographicalCode);
        

        
        return sbf.toString();
    }

    public String getUserCategoryDesc() {
        return _userCategoryDesc;
    }

    public void setUserCategoryDesc(String userCategoryDesc) {
        _userCategoryDesc = userCategoryDesc;
    }

    public String getUserNamewithUserId() {
        if (!BTSLUtil.isNullString(this._userID)) {
            return this._userName + "(" + this._userID + ")";
        }
        return this._userName;
    }

    /**
     * @return Returns the loginId.
     */
    public String getLoginId() {
        return _loginId;
    }

    /**
     * @param loginId
     *            The loginId to set.
     */
    public void setLoginId(String loginId) {
        _loginId = loginId;
    }

    public String getLoginIdwithUserId() {
        if (!BTSLUtil.isNullString(this._userID)) {
            return this._loginId + "(" + this._userID + ")";
        }
        return this._loginId;
    }
    // added for channel user transfer
    public boolean getIsOperationNotAllow()
    {
        return _isOperationNotAllow;
    }

    public void setIsOperationNotAllow(boolean isOperationNotAllow)
    {
        _isOperationNotAllow = isOperationNotAllow;
    }
    
    
    
    /**
 	 * @return the _parentUserName
 	 */
 	public String getParentUserName() {
 		return _parentUserName;
 	}

 	/**
 	 * @param _parentUserName the _parentUserName to set
 	 */
 	public void setParentUserName(String _parentUserName) {
 		this._parentUserName = _parentUserName;
 	}

 	/**
 	 * @return the _toParentUserName
 	 */
 	public String getToParentUserName() {
 		return _toParentUserName;
 	}

 	/**
 	 * @param _toParentUserName the _toParentUserName to set
 	 */
 	public void setToParentUserName(String _toParentUserName) {
 		this._toParentUserName = _toParentUserName;
 	}
	public String getToParentUserID()
	{
		return _toParentUserID;
	}

	public void setToParentUserID(String toParentUserID)
	{
		_toParentUserID = toParentUserID;
	}
	  public String getParentUserID()
	    {
	        return _parentUserID;
	    }

	    public void setParentUserID(String parentUserID)
	    {
	        _parentUserID = parentUserID;
	    }
	 

	    public String getDomainCodeDesc()
	    {
	        return _domainCodeDesc;
	    }

	    public void setDomainCodeDesc(String domainCodeDesc)
	    {
	        _domainCodeDesc = domainCodeDesc;
	    }

	    public ArrayList getDomainList()
	    {
	        return _domainList;
	    }

	    public void setDomainList(ArrayList domainList)
	    {
	        _domainList = domainList;
	    }
	    public String getGeographicalCode()
		{
			return _geographicalCode;
		}
		public void setGeographicalCode(String userGeographicalCode)
		{
			_geographicalCode = userGeographicalCode;
		}
		
		/**
		 * @return the _otp
		 */
		public String getOtp() {
			return _otp;
		}

		/**
		 * @param _otp the _otp to set
		 */
		public void setOtp(String _otp) {
			this._otp = _otp;
		}

		/**
		 * @return the _invalidOtpCount
		 */
		public int getInvalidOtpCount() {
			return _invalidOtpCount;
		}

		/**
		 * @param _invalidOtpCount the _invalidOtpCount to set
		 */
		public void setInvalidOtpCount(int _invalidOtpCount) {
			this._invalidOtpCount = _invalidOtpCount;
		}

}
