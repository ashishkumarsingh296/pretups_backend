/**
 * @# ChannelTransferRuleVO.java
 * 
 *    --------------------------------------------------------------------------
 *    ------
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    Aug 1, 2005 Sandeep Goel Initial creation
 *    May 12, 2006 Sandeep Goel Modification
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ChannelTransferRuleVO implements Serializable {

    private String _transferRuleID;
    private String _type;
    private String _domainCode;
    private String _networkCode;
    private String _fromCategory;
    private String _toCategory;

    private String _transferChnlBypassAllowed;
    private String _withdrawAllowed;
    private String _withdrawChnlBypassAllowed;
    private String _returnAllowed;
    private String _returnChnlBypassAllowed;
    private String _approvalRequired;
    private long _firstApprovalLimit;
    private long _secondApprovalLimit;
    private String _createdBy;
    private Date _createdOn = null;
    private String _modifiedBy;
    private Date _modifiedOn = null;
    private String _uncntrlTransferAllowed;

    private String _uncntrlTransferAllowedTmp;// used in the case of modify
    // transfer rule

    private long _lastModifiedTime = 0;
    private ArrayList _productVOList = null;
    private String[] _productArray;
    private String _fromCategoryDes;
    private String _toCategoryDes;
    private int _fromSeqNo;
    private int _toSeqNo;
    private String _parentAssocationAllowed;
    private String _directTransferAllowed;
    private String _transferType;
    private String _transferAllowed;
    private String _focTransferType;
    private String _focAllowed;
    private String _transferTypeDesc;
    private String _restrictedMsisdnAccess;

    private String _restrictedRechargeAccess;
    private String _ownerCategoryName;

    // new fields added in the table
    private String _toDomainCode = null;
    private String _uncntrlTransferLevel = null;
    private String _cntrlTransferLevel = null;
    private String _fixedTransferLevel = null;
    private String _fixedTransferCategory = null;
    private String _uncntrlReturnAllowed = null;
    private String _uncntrlReturnLevel = null;
    private String _cntrlReturnLevel = null;
    private String _fixedReturnLevel = null;
    private String _fixedReturnCategory = null;
    private String _uncntrlWithdrawAllowed = null;
    private String _uncntrlWithdrawLevel = null;
    private String _cntrlWithdrawLevel = null;
    private String _fixedWithdrawLevel = null;
    private String _fixedWithdrawCategory = null;

    // added by vipul for Modify Status O2C/C2C Transfer rules
    private String _status = null;
    private String _statusDesc = null;
    private String _transferRuleType = null;
    // added by Lohit for Btach Direct Payout
    private String _dpAllowed;
    // added by nilesh
    private String _previousStatus;
    private String _ruleType;
	
	private String _fromDomainDes;
	private String _toDomainDes;

    public ChannelTransferRuleVO() {
        super();
    }

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append("_approvalRequired =" + _approvalRequired);
        sbf.append(",_type=" + _type);
        sbf.append(",_parentAssocationAllowed =" + _parentAssocationAllowed);
        sbf.append(",_directTransferAllowed =" + _directTransferAllowed);
        sbf.append(",_createdBy =" + _createdBy);
        sbf.append(",_domainCode =" + _domainCode);
        sbf.append(",_firstApprovalLimit =" + _firstApprovalLimit);
        sbf.append(",_fromCategory =" + _fromCategory);
        sbf.append(",_fromCategoryDes =" + _fromCategoryDes);
        sbf.append(",_fromSeqNo =" + _fromSeqNo);
        sbf.append(",_lastModifiedTime=" + _lastModifiedTime);
        sbf.append(",_modifiedBy =" + _modifiedBy);
        sbf.append(",_networkCode =" + _networkCode);
        sbf.append(",_modifiedOn =" + _modifiedOn);
        sbf.append(",_returnAllowed =" + _returnAllowed);
        sbf.append(",_returnChnlBypassAllowed =" + _returnChnlBypassAllowed);
        sbf.append(",_secondApprovalLimit =" + _secondApprovalLimit);
        sbf.append(",_toCategory =" + _toCategory);
        sbf.append(",_toCategoryDes =" + _toCategoryDes);
        sbf.append(",_toSeqNo =" + _toSeqNo);
        sbf.append(",_transferChnlBypassAllowed =" + _transferChnlBypassAllowed);
        sbf.append(",_transferRuleID =" + _transferRuleID);
        sbf.append(",_withdrawAllowed =" + _withdrawAllowed);
        sbf.append(",_withdrawChnlBypassAllowed =" + _withdrawChnlBypassAllowed);
        sbf.append(",_createdOn =" + _createdOn);
        sbf.append(",_productArray =" + _productArray);
        sbf.append(", =" + _productVOList);
        sbf.append(",_uncntrlTransferAllowed =" + _uncntrlTransferAllowed);
        sbf.append(",_transferAllowed =" + _transferAllowed);
        sbf.append(",_transferType =" + _transferType);
        sbf.append(",_focAllowed =" + _focAllowed);
        sbf.append(",_focTransferType =" + _focTransferType);
        sbf.append(",_restrictedMsisdnAccess =" + _restrictedMsisdnAccess);
        sbf.append(",_ownerCategoryName =" + _ownerCategoryName);
        sbf.append(",_toDomainCode=" + _toDomainCode);
        sbf.append(",_uncntrlTransferLevel=" + _uncntrlTransferLevel);
        sbf.append(",_cntrlTransferLevel=" + _cntrlTransferLevel);
        sbf.append(",_fixedTransferLevel=" + _fixedTransferLevel);
        sbf.append(",_fixedTransferCategory=" + _fixedTransferCategory);
        sbf.append(",_uncntrlReturnAllowed=" + _uncntrlReturnAllowed);
        sbf.append(",_uncntrlReturnLevel=" + _uncntrlReturnLevel);
        sbf.append(",_cntrlReturnLevel=" + _cntrlReturnLevel);
        sbf.append(",_fixedReturnLevel=" + _fixedReturnLevel);
        sbf.append(",_fixedReturnCategory=" + _fixedReturnCategory);
        sbf.append(",_uncntrlWithdrawAllowed=" + _uncntrlWithdrawAllowed);
        sbf.append(",_uncntrlWithdrawLevel=" + _uncntrlWithdrawLevel);
        sbf.append(",_cntrlWithdrawLevel=" + _cntrlWithdrawLevel);
        sbf.append(",_fixedWithdrawLevel=" + _fixedWithdrawLevel);
        sbf.append(",_fixedWithdrawCategory=" + _fixedWithdrawCategory);
        sbf.append(",_restrictedRechargeAccess=" + _restrictedRechargeAccess);
        sbf.append(",_dpAllowed =" + _dpAllowed);
        sbf.append(",_status=" + _status);
        return sbf.toString();
    }

    /**
     * @return Returns the ownerCategoryName.
     */
    public String getOwnerCategoryName() {
        return _ownerCategoryName;
    }

    /**
     * @param ownerCategoryName
     *            The ownerCategoryName to set.
     */
    public void setOwnerCategoryName(String ownerCategoryName) {
        _ownerCategoryName = ownerCategoryName;
    }

    public String getUncntrlTransferAllowed() {
        return _uncntrlTransferAllowed;
    }

    public void setUncntrlTransferAllowed(String uncntrlTransferAllowed) {
        _uncntrlTransferAllowed = uncntrlTransferAllowed;
    }

    public String getApprovalRequired() {
        return _approvalRequired;
    }

    public void setApprovalRequired(String approvalRequired) {
        _approvalRequired = approvalRequired;
    }

    public String getParentAssocationAllowed() {
        return _parentAssocationAllowed;
    }

    public void setParentAssocationAllowed(String parentAssocationAllowed) {
        _parentAssocationAllowed = parentAssocationAllowed;
    }

    public String getDirectTransferAllowed() {
        return _directTransferAllowed;
    }

    public void setDirectTransferAllowed(String directTransferAllowed) {
        _directTransferAllowed = directTransferAllowed;
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

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public long getFirstApprovalLimit() {
        return _firstApprovalLimit;
    }

    public void setFirstApprovalLimit(long firstApprovalLimit) {
        _firstApprovalLimit = firstApprovalLimit;
    }

    public String getFromCategory() {
        return _fromCategory;
    }

    public void setFromCategory(String fromCategory) {
        _fromCategory = fromCategory;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
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

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getReturnAllowed() {
        return _returnAllowed;
    }

    public void setReturnAllowed(String returnAllowed) {
        _returnAllowed = returnAllowed;
    }

    public String getReturnChnlBypassAllowed() {
        return _returnChnlBypassAllowed;
    }

    public void setReturnChnlBypassAllowed(String returnChnlBypassAllowed) {
        _returnChnlBypassAllowed = returnChnlBypassAllowed;
    }

    public long getSecondApprovalLimit() {
        return _secondApprovalLimit;
    }

    public void setSecondApprovalLimit(long secondApprovalLimit) {
        _secondApprovalLimit = secondApprovalLimit;
    }

    public String getToCategory() {
        return _toCategory;
    }

    public void setToCategory(String toCategory) {
        _toCategory = toCategory;
    }

    public String getTransferChnlBypassAllowed() {
        return _transferChnlBypassAllowed;
    }

    public void setTransferChnlBypassAllowed(String transferChnlBypassAllowed) {
        _transferChnlBypassAllowed = transferChnlBypassAllowed;
    }

    public String getTransferRuleID() {
        return _transferRuleID;
    }

    public void setTransferRuleID(String transferRuleID) {
        _transferRuleID = transferRuleID;
    }

    public String getWithdrawAllowed() {
        return _withdrawAllowed;
    }

    public void setWithdrawAllowed(String withdrawAllowed) {
        _withdrawAllowed = withdrawAllowed;
    }

    public String getWithdrawChnlBypassAllowed() {
        return _withdrawChnlBypassAllowed;
    }

    public void setWithdrawChnlBypassAllowed(String withdrawChnlBypassAllowed) {
        _withdrawChnlBypassAllowed = withdrawChnlBypassAllowed;
    }

    public ArrayList getProductVOList() {
        return _productVOList;
    }

    public void setProductVOList(ArrayList productVOList) {
        _productVOList = productVOList;
    }

    public String[] getProductArray() {
        return _productArray;
    }

    public void setProductArray(String[] productArray) {
        _productArray = productArray;
    }

    public String getFromCategoryDes() {
        return _fromCategoryDes;
    }

    public void setFromCategoryDes(String fromCategoryDes) {
        _fromCategoryDes = fromCategoryDes;
    }

    public String getToCategoryDes() {
        return _toCategoryDes;
    }

    public void setToCategoryDes(String toCategoryDes) {
        _toCategoryDes = toCategoryDes;
    }

    public int getFromSeqNo() {
        return _fromSeqNo;
    }

    public void setFromSeqNo(int fromSeqNo) {
        _fromSeqNo = fromSeqNo;
    }

    public int getToSeqNo() {
        return _toSeqNo;
    }

    public void setToSeqNo(int toSeqNo) {
        _toSeqNo = toSeqNo;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getUncntrlTransferAllowedTmp() {
        return _uncntrlTransferAllowedTmp;
    }

    public void setUncntrlTransferAllowedTmp(String uncntrlTransferAllowedTmp) {
        _uncntrlTransferAllowedTmp = uncntrlTransferAllowedTmp;
    }

    public String getFocAllowed() {
        return _focAllowed;
    }

    public void setFocAllowed(String focAllowed) {
        _focAllowed = focAllowed;
    }

    public String getFocTransferType() {
        return _focTransferType;
    }

    public void setFocTransferType(String focTransferType) {
        _focTransferType = focTransferType;
    }

    public String getTransferAllowed() {
        return _transferAllowed;
    }

    public void setTransferAllowed(String transferAllowed) {
        _transferAllowed = transferAllowed;
    }

    public String getTransferType() {
        return _transferType;
    }

    public void setTransferType(String transferType) {
        _transferType = transferType;
    }

    public String getTransferTypeDesc() {
        return _transferTypeDesc;
    }

    public void setTransferTypeDesc(String transferTypeDesc) {
        _transferTypeDesc = transferTypeDesc;
    }

    public String getRestrictedMsisdnAccess() {
        return _restrictedMsisdnAccess;
    }

    public void setRestrictedMsisdnAccess(String restrictedMsisdnAccess) {
        _restrictedMsisdnAccess = restrictedMsisdnAccess;
    }

    public String getCntrlReturnLevel() {
        return _cntrlReturnLevel;
    }

    public void setCntrlReturnLevel(String cntrlReturnLevel) {
        _cntrlReturnLevel = cntrlReturnLevel;
    }

    public String getCntrlTransferLevel() {
        return _cntrlTransferLevel;
    }

    public void setCntrlTransferLevel(String cntrlTransferLevel) {
        _cntrlTransferLevel = cntrlTransferLevel;
    }

    public String getCntrlWithdrawLevel() {
        return _cntrlWithdrawLevel;
    }

    public void setCntrlWithdrawLevel(String cntrlWithdrawLevel) {
        _cntrlWithdrawLevel = cntrlWithdrawLevel;
    }

    public String getFixedReturnCategory() {
        return _fixedReturnCategory;
    }

    public void setFixedReturnCategory(String fixedReturnCategory) {
        _fixedReturnCategory = fixedReturnCategory;
    }

    public String getFixedReturnLevel() {
        return _fixedReturnLevel;
    }

    public void setFixedReturnLevel(String fixedReturnLevel) {
        _fixedReturnLevel = fixedReturnLevel;
    }

    public String getFixedTransferCategory() {
        return _fixedTransferCategory;
    }

    public void setFixedTransferCategory(String fixedTransferCategory) {
        _fixedTransferCategory = fixedTransferCategory;
    }

    public String getFixedTransferLevel() {
        return _fixedTransferLevel;
    }

    public void setFixedTransferLevel(String fixedTransferLevel) {
        _fixedTransferLevel = fixedTransferLevel;
    }

    public String getFixedWithdrawCategory() {
        return _fixedWithdrawCategory;
    }

    public void setFixedWithdrawCategory(String fixedWithdrawCategory) {
        _fixedWithdrawCategory = fixedWithdrawCategory;
    }

    public String getFixedWithdrawLevel() {
        return _fixedWithdrawLevel;
    }

    public void setFixedWithdrawLevel(String fixedWithdrawLevel) {
        _fixedWithdrawLevel = fixedWithdrawLevel;
    }

    public String getToDomainCode() {
        return _toDomainCode;
    }

    public void setToDomainCode(String toDomainCode) {
        _toDomainCode = toDomainCode;
    }

    public String getUncntrlReturnAllowed() {
        return _uncntrlReturnAllowed;
    }

    public void setUncntrlReturnAllowed(String uncntrlReturnAllowed) {
        _uncntrlReturnAllowed = uncntrlReturnAllowed;
    }

    public String getUncntrlReturnLevel() {
        return _uncntrlReturnLevel;
    }

    public void setUncntrlReturnLevel(String uncntrlReturnLevel) {
        _uncntrlReturnLevel = uncntrlReturnLevel;
    }

    public String getUncntrlTransferLevel() {
        return _uncntrlTransferLevel;
    }

    public void setUncntrlTransferLevel(String uncntrlTransferLevel) {
        _uncntrlTransferLevel = uncntrlTransferLevel;
    }

    public String getUncntrlWithdrawAllowed() {
        return _uncntrlWithdrawAllowed;
    }

    public void setUncntrlWithdrawAllowed(String uncntrlWithdrawAllowed) {
        _uncntrlWithdrawAllowed = uncntrlWithdrawAllowed;
    }

    public String getUncntrlWithdrawLevel() {
        return _uncntrlWithdrawLevel;
    }

    public void setUncntrlWithdrawLevel(String uncntrlWithdrawLevel) {
        _uncntrlWithdrawLevel = uncntrlWithdrawLevel;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getStatusDesc() {
        return _statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    public String getTransferRuleType() {
        return _transferRuleType;
    }

    public void setTransferRuleType(String transferRuleType) {
        _transferRuleType = transferRuleType;
    }

    /**
     * Returns restrictedRechargeAccess
     * 
     * @return Returns the restrictedRechargeAccess.
     */
    public String getRestrictedRechargeAccess() {
        return this._restrictedRechargeAccess;
    }

    /**
     * Sets restrictedRechargeAccess
     * 
     * @param restrictedRechargeAccess
     *            String
     */
    public void setRestrictedRechargeAccess(String restrictedRechargeAccess) {
        this._restrictedRechargeAccess = restrictedRechargeAccess;
    }

    public String getDpAllowed() {
        return _dpAllowed;
    }

    public void setDpAllowed(String dpAllowed) {
        _dpAllowed = dpAllowed;
    }

    // added by nilesh
    public String getPreviousStatus() {
        return _previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        _previousStatus = previousStatus;
    }

    public String getRuleType() {
        return _ruleType;
    }

    public void setRuleType(String ruleType) {
        _ruleType = ruleType;
    }

	public String getFromDomainDes()
	{
		return _fromDomainDes;
	}

	public void setFromDomainDes(String fromDomainDes)
	{
		_fromDomainDes = fromDomainDes;
	}

	public String getToDomainDes()
	{
		return _toDomainDes;
	}

	public void setToDomainDes(String toDomainDes)
	{
		_toDomainDes = toDomainDes;
	}
}
