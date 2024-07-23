package com.restapi.networkadminVO;

import java.util.ArrayList;
import java.util.Date;

public class AddO2CTransferRuleVO {

	private String transferRuleID;
    private String type;
    private String domainCode;
    private String networkCode;
    private String fromCategory;
    private String toCategory;

    private String transferChnlBypassAllowed;
    private String withdrawAllowed;
    private String withdrawChnlBypassAllowed;
    private String returnAllowed;
    private String returnChnlBypassAllowed;
    private String approvalRequired;
    private long firstApprovalLimit;
    private long secondApprovalLimit;
    private String createdBy;
    private Date createdOn = null;
    private String modifiedBy;
    private Date modifiedOn = null;
    private String uncntrlTransferAllowed;

    private String uncntrlTransferAllowedTmp;// used in the case of modify
    // transfer rule

    private long lastModifiedTime = 0;
    private ArrayList productVOList = null;
    private String[] productArray;
    private String fromCategoryDes;
    private String toCategoryDes;
    private int fromSeqNo;
    private int toSeqNo;
    private String parentAssocationAllowed;
    private String directTransferAllowed;
    private String transferType;
    private String transferAllowed;
    private String focTransferType;
    private String focAllowed;
    private String transferTypeDesc;
    private String restrictedMsisdnAccess;

    private String restrictedRechargeAccess;
    private String ownerCategoryName;

    // new fields added in the table
    private String toDomainCode = null;
    private String uncntrlTransferLevel = null;
    private String cntrlTransferLevel = null;
    private String fixedTransferLevel = null;
    private String fixedTransferCategory = null;
    private String uncntrlReturnAllowed = null;
    private String uncntrlReturnLevel = null;
    private String cntrlReturnLevel = null;
    private String fixedReturnLevel = null;
    private String fixedReturnCategory = null;
    private String uncntrlWithdrawAllowed = null;
    private String uncntrlWithdrawLevel = null;
    private String cntrlWithdrawLevel = null;
    private String fixedWithdrawLevel = null;
    private String fixedWithdrawCategory = null;

    // added by vipul for Modify Status O2C/C2C Transfer rules
    private String status = null;
    private String statusDesc = null;
    private String transferRuleType = null;
    // added by Lohit for Btach Direct Payout
    private String dpAllowed;
    // added by nilesh
    private String previousStatus;
    private String ruleType;
	
	private String fromDomainDes;
	private String toDomainDes;
	
	
	
	
	
	
	public String getTransferRuleID() {
		return transferRuleID;
	}
	public void setTransferRuleID(String transferRuleID) {
		this.transferRuleID = transferRuleID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getFromCategory() {
		return fromCategory;
	}
	public void setFromCategory(String fromCategory) {
		this.fromCategory = fromCategory;
	}
	public String getToCategory() {
		return toCategory;
	}
	public void setToCategory(String toCategory) {
		this.toCategory = toCategory;
	}
	public String getTransferChnlBypassAllowed() {
		return transferChnlBypassAllowed;
	}
	public void setTransferChnlBypassAllowed(String transferChnlBypassAllowed) {
		this.transferChnlBypassAllowed = transferChnlBypassAllowed;
	}
	public String getWithdrawAllowed() {
		return withdrawAllowed;
	}
	public void setWithdrawAllowed(String withdrawAllowed) {
		this.withdrawAllowed = withdrawAllowed;
	}
	public String getWithdrawChnlBypassAllowed() {
		return withdrawChnlBypassAllowed;
	}
	public void setWithdrawChnlBypassAllowed(String withdrawChnlBypassAllowed) {
		this.withdrawChnlBypassAllowed = withdrawChnlBypassAllowed;
	}
	public String getReturnAllowed() {
		return returnAllowed;
	}
	public void setReturnAllowed(String returnAllowed) {
		this.returnAllowed = returnAllowed;
	}
	public String getReturnChnlBypassAllowed() {
		return returnChnlBypassAllowed;
	}
	public void setReturnChnlBypassAllowed(String returnChnlBypassAllowed) {
		this.returnChnlBypassAllowed = returnChnlBypassAllowed;
	}
	public String getApprovalRequired() {
		return approvalRequired;
	}
	public void setApprovalRequired(String approvalRequired) {
		this.approvalRequired = approvalRequired;
	}
	public long getFirstApprovalLimit() {
		return firstApprovalLimit;
	}
	public void setFirstApprovalLimit(long firstApprovalLimit) {
		this.firstApprovalLimit = firstApprovalLimit;
	}
	public long getSecondApprovalLimit() {
		return secondApprovalLimit;
	}
	public void setSecondApprovalLimit(long secondApprovalLimit) {
		this.secondApprovalLimit = secondApprovalLimit;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getUncntrlTransferAllowed() {
		return uncntrlTransferAllowed;
	}
	public void setUncntrlTransferAllowed(String uncntrlTransferAllowed) {
		this.uncntrlTransferAllowed = uncntrlTransferAllowed;
	}
	public String getUncntrlTransferAllowedTmp() {
		return uncntrlTransferAllowedTmp;
	}
	public void setUncntrlTransferAllowedTmp(String uncntrlTransferAllowedTmp) {
		this.uncntrlTransferAllowedTmp = uncntrlTransferAllowedTmp;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public ArrayList getProductVOList() {
		return productVOList;
	}
	public void setProductVOList(ArrayList productVOList) {
		this.productVOList = productVOList;
	}
	public String[] getProductArray() {
		return productArray;
	}
	public void setProductArray(String[] productArray) {
		this.productArray = productArray;
	}
	public String getFromCategoryDes() {
		return fromCategoryDes;
	}
	public void setFromCategoryDes(String fromCategoryDes) {
		this.fromCategoryDes = fromCategoryDes;
	}
	public String getToCategoryDes() {
		return toCategoryDes;
	}
	public void setToCategoryDes(String toCategoryDes) {
		this.toCategoryDes = toCategoryDes;
	}
	public int getFromSeqNo() {
		return fromSeqNo;
	}
	public void setFromSeqNo(int fromSeqNo) {
		this.fromSeqNo = fromSeqNo;
	}
	public int getToSeqNo() {
		return toSeqNo;
	}
	public void setToSeqNo(int toSeqNo) {
		this.toSeqNo = toSeqNo;
	}
	public String getParentAssocationAllowed() {
		return parentAssocationAllowed;
	}
	public void setParentAssocationAllowed(String parentAssocationAllowed) {
		this.parentAssocationAllowed = parentAssocationAllowed;
	}
	public String getDirectTransferAllowed() {
		return directTransferAllowed;
	}
	public void setDirectTransferAllowed(String directTransferAllowed) {
		this.directTransferAllowed = directTransferAllowed;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getTransferAllowed() {
		return transferAllowed;
	}
	public void setTransferAllowed(String transferAllowed) {
		this.transferAllowed = transferAllowed;
	}
	public String getFocTransferType() {
		return focTransferType;
	}
	public void setFocTransferType(String focTransferType) {
		this.focTransferType = focTransferType;
	}
	public String getFocAllowed() {
		return focAllowed;
	}
	public void setFocAllowed(String focAllowed) {
		this.focAllowed = focAllowed;
	}
	public String getTransferTypeDesc() {
		return transferTypeDesc;
	}
	public void setTransferTypeDesc(String transferTypeDesc) {
		this.transferTypeDesc = transferTypeDesc;
	}
	public String getRestrictedMsisdnAccess() {
		return restrictedMsisdnAccess;
	}
	public void setRestrictedMsisdnAccess(String restrictedMsisdnAccess) {
		this.restrictedMsisdnAccess = restrictedMsisdnAccess;
	}
	public String getRestrictedRechargeAccess() {
		return restrictedRechargeAccess;
	}
	public void setRestrictedRechargeAccess(String restrictedRechargeAccess) {
		this.restrictedRechargeAccess = restrictedRechargeAccess;
	}
	public String getOwnerCategoryName() {
		return ownerCategoryName;
	}
	public void setOwnerCategoryName(String ownerCategoryName) {
		this.ownerCategoryName = ownerCategoryName;
	}
	public String getToDomainCode() {
		return toDomainCode;
	}
	public void setToDomainCode(String toDomainCode) {
		this.toDomainCode = toDomainCode;
	}
	public String getUncntrlTransferLevel() {
		return uncntrlTransferLevel;
	}
	public void setUncntrlTransferLevel(String uncntrlTransferLevel) {
		this.uncntrlTransferLevel = uncntrlTransferLevel;
	}
	public String getCntrlTransferLevel() {
		return cntrlTransferLevel;
	}
	public void setCntrlTransferLevel(String cntrlTransferLevel) {
		this.cntrlTransferLevel = cntrlTransferLevel;
	}
	public String getFixedTransferLevel() {
		return fixedTransferLevel;
	}
	public void setFixedTransferLevel(String fixedTransferLevel) {
		this.fixedTransferLevel = fixedTransferLevel;
	}
	public String getFixedTransferCategory() {
		return fixedTransferCategory;
	}
	public void setFixedTransferCategory(String fixedTransferCategory) {
		this.fixedTransferCategory = fixedTransferCategory;
	}
	public String getUncntrlReturnAllowed() {
		return uncntrlReturnAllowed;
	}
	public void setUncntrlReturnAllowed(String uncntrlReturnAllowed) {
		this.uncntrlReturnAllowed = uncntrlReturnAllowed;
	}
	public String getUncntrlReturnLevel() {
		return uncntrlReturnLevel;
	}
	public void setUncntrlReturnLevel(String uncntrlReturnLevel) {
		this.uncntrlReturnLevel = uncntrlReturnLevel;
	}
	public String getCntrlReturnLevel() {
		return cntrlReturnLevel;
	}
	public void setCntrlReturnLevel(String cntrlReturnLevel) {
		this.cntrlReturnLevel = cntrlReturnLevel;
	}
	public String getFixedReturnLevel() {
		return fixedReturnLevel;
	}
	public void setFixedReturnLevel(String fixedReturnLevel) {
		this.fixedReturnLevel = fixedReturnLevel;
	}
	public String getFixedReturnCategory() {
		return fixedReturnCategory;
	}
	public void setFixedReturnCategory(String fixedReturnCategory) {
		this.fixedReturnCategory = fixedReturnCategory;
	}
	public String getUncntrlWithdrawAllowed() {
		return uncntrlWithdrawAllowed;
	}
	public void setUncntrlWithdrawAllowed(String uncntrlWithdrawAllowed) {
		this.uncntrlWithdrawAllowed = uncntrlWithdrawAllowed;
	}
	public String getUncntrlWithdrawLevel() {
		return uncntrlWithdrawLevel;
	}
	public void setUncntrlWithdrawLevel(String uncntrlWithdrawLevel) {
		this.uncntrlWithdrawLevel = uncntrlWithdrawLevel;
	}
	public String getCntrlWithdrawLevel() {
		return cntrlWithdrawLevel;
	}
	public void setCntrlWithdrawLevel(String cntrlWithdrawLevel) {
		this.cntrlWithdrawLevel = cntrlWithdrawLevel;
	}
	public String getFixedWithdrawLevel() {
		return fixedWithdrawLevel;
	}
	public void setFixedWithdrawLevel(String fixedWithdrawLevel) {
		this.fixedWithdrawLevel = fixedWithdrawLevel;
	}
	public String getFixedWithdrawCategory() {
		return fixedWithdrawCategory;
	}
	public void setFixedWithdrawCategory(String fixedWithdrawCategory) {
		this.fixedWithdrawCategory = fixedWithdrawCategory;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getTransferRuleType() {
		return transferRuleType;
	}
	public void setTransferRuleType(String transferRuleType) {
		this.transferRuleType = transferRuleType;
	}
	public String getDpAllowed() {
		return dpAllowed;
	}
	public void setDpAllowed(String dpAllowed) {
		this.dpAllowed = dpAllowed;
	}
	public String getPreviousStatus() {
		return previousStatus;
	}
	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public String getFromDomainDes() {
		return fromDomainDes;
	}
	public void setFromDomainDes(String fromDomainDes) {
		this.fromDomainDes = fromDomainDes;
	}
	public String getToDomainDes() {
		return toDomainDes;
	}
	public void setToDomainDes(String toDomainDes) {
		this.toDomainDes = toDomainDes;
	}
	
	
	
}
