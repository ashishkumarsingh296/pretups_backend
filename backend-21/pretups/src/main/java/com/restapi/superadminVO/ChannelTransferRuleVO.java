package com.restapi.superadminVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ChannelTransferRuleVO {
		
		private String networkCode;
	    private String transferRuleID;
	    private String type;	
	    private String domainCode;
	    private String toDomainCode;
	    private String fromCategory;
	    private String toCategory;
	    private String parentAssocationAllowed;
	    
	    private String transferType;
	    private String restrictedMsisdnAccess;

	    private String restrictedRechargeAccess;
	    
	    private String directTransferAllowed;
	    private String transferChnlBypassAllowed;
	    
	    private String uncntrlTransferAllowed;
	    private String uncntrlTransferLevel;
	    private String cntrlTransferLevel ;
	    private String fixedTransferLevel ;
		   
	    private String fixedTransferCategory ;
	    private String withdrawChnlBypassAllowed;
	    private String withdrawAllowed;
	    private String uncntrlWithdrawAllowed;
	    private String uncntrlWithdrawLevel;
	    private String cntrlWithdrawLevel ;
	    private String fixedWithdrawLevel ;
	    private String fixedWithdrawCategory ;
	    private String returnAllowed;
	    private String returnChnlBypassAllowed;
	    private String uncntrlReturnAllowed ;   
	    private String uncntrlReturnLevel ;
	    private String cntrlReturnLevel ;
	    private String fixedReturnLevel ;
	    private String fixedReturnCategory;
	    private String createdBy;
	    private Date createdOn = null;
	    private String modifiedBy;
	    private Date modifiedOn = null;
        private String ruleType;
        private ArrayList productVOList ;
        private String[] productArray;
		 
        private String status;
        private String transferAllowed;
        private String approvalRequired;
        private Long firstApprovalLimit;
        private Long secondApprovalLimit;
        private String focTransferType;
        private String focAllowed;
        private String dpAllowed;
        private String fromCategoryDes;
        private String toCategoryDes;
        private Integer fromSeqNo;
        private Integer toSeqNo;
        private String uncntrlTransferAllowedTmp;
        private String statusDesc;
        private String transferRuleType;
        
        private String previousStatus;
        
    	private String fromDomainDes;
    	private String toDomainDes;
    	private Long lastModifiedTime;
        
        
        
	    public String getUncntrlTransferAllowedTmp() {
			return uncntrlTransferAllowedTmp;
		}


		public void setUncntrlTransferAllowedTmp(String uncntrlTransferAllowedTmp) {
			this.uncntrlTransferAllowedTmp = uncntrlTransferAllowedTmp;
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


		public String getPreviousStatus() {
			return previousStatus;
		}


		public void setPreviousStatus(String previousStatus) {
			this.previousStatus = previousStatus;
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


		public Long getLastModifiedTime() {
			return lastModifiedTime;
		}


		public void setLastModifiedTime(Long lastModifiedTime) {
			this.lastModifiedTime = lastModifiedTime;
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


		public Integer getFromSeqNo() {
			return fromSeqNo;
		}


		public void setFromSeqNo(Integer fromSeqNo) {
			this.fromSeqNo = fromSeqNo;
		}


		public Integer getToSeqNo() {
			return toSeqNo;
		}


		public void setToSeqNo(Integer toSeqNo) {
			this.toSeqNo = toSeqNo;
		}


		public String getApprovalRequired() {
			return approvalRequired;
		}


		public void setApprovalRequired(String approvalRequired) {
			this.approvalRequired = approvalRequired;
		}


		public Long getFirstApprovalLimit() {
			return firstApprovalLimit;
		}


		public void setFirstApprovalLimit(Long firstApprovalLimit) {
			this.firstApprovalLimit = firstApprovalLimit;
		}


		public Long getSecondApprovalLimit() {
			return secondApprovalLimit;
		}


		public void setSecondApprovalLimit(Long secondApprovalLimit) {
			this.secondApprovalLimit = secondApprovalLimit;
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


		public String getDpAllowed() {
			return dpAllowed;
		}


		public void setDpAllowed(String dpAllowed) {
			this.dpAllowed = dpAllowed;
		}


		public String getStatus() {
			return status;
		}


		public void setStatus(String status) {
			this.status = status;
		}


		public String getTransferAllowed() {
			return transferAllowed;
		}


		public void setTransferAllowed(String transferAllowed) {
			this.transferAllowed = transferAllowed;
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


		@Override
		public String toString() {
			return "ChannelTransferRuleVO [networkCode=" + networkCode + ", transferRuleID=" + transferRuleID
					+ ", type=" + type + ", domainCode=" + domainCode + ", toDomainCode=" + toDomainCode
					+ ", fromCategory=" + fromCategory + ", toCategory=" + toCategory + ", parentAssocationAllowed="
					+ parentAssocationAllowed + ", transferType=" + transferType + ", restrictedMsisdnAccess="
					+ restrictedMsisdnAccess + ", restrictedRechargeAccess=" + restrictedRechargeAccess
					+ ", directTransferAllowed=" + directTransferAllowed + ", transferChnlBypassAllowed="
					+ transferChnlBypassAllowed + ", uncntrlTransferAllowed=" + uncntrlTransferAllowed
					+ ", uncntrlTransferLevel=" + uncntrlTransferLevel + ", cntrlTransferLevel=" + cntrlTransferLevel
					+ ", fixedTransferLevel=" + fixedTransferLevel + ", fixedTransferCategory=" + fixedTransferCategory
					+ ", withdrawChnlBypassAllowed=" + withdrawChnlBypassAllowed + ", withdrawAllowed="
					+ withdrawAllowed + ", uncntrlWithdrawAllowed=" + uncntrlWithdrawAllowed + ", uncntrlWithdrawLevel="
					+ uncntrlWithdrawLevel + ", cntrlWithdrawLevel=" + cntrlWithdrawLevel + ", fixedWithdrawLevel="
					+ fixedWithdrawLevel + ", fixedWithdrawCategory=" + fixedWithdrawCategory + ", returnAllowed="
					+ returnAllowed + ", returnChnlBypassAllowed=" + returnChnlBypassAllowed + ", uncntrlReturnAllowed="
					+ uncntrlReturnAllowed + ", uncntrlReturnLevel=" + uncntrlReturnLevel + ", cntrlReturnLevel="
					+ cntrlReturnLevel + ", fixedReturnLevel=" + fixedReturnLevel + ", fixedReturnCategory="
					+ fixedReturnCategory + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", modifiedBy="
					+ modifiedBy + ", modifiedOn=" + modifiedOn + ", ruleType=" + ruleType + ", productVOList="
					+ productVOList + ", productArray=" + Arrays.toString(productArray) + ", status=" + status
					+ ", transferAllowed=" + transferAllowed + ", approvalRequired=" + approvalRequired
					+ ", firstApprovalLimit=" + firstApprovalLimit + ", secondApprovalLimit=" + secondApprovalLimit
					+ ", focTransferType=" + focTransferType + ", focAllowed=" + focAllowed + ", dpAllowed=" + dpAllowed
					+ ", fromCategoryDes=" + fromCategoryDes + ", toCategoryDes=" + toCategoryDes + ", fromSeqNo="
					+ fromSeqNo + ", toSeqNo=" + toSeqNo + ", uncntrlTransferAllowedTmp=" + uncntrlTransferAllowedTmp
					+ ", statusDesc=" + statusDesc + ", transferRuleType=" + transferRuleType + ", previousStatus="
					+ previousStatus + ", fromDomainDes=" + fromDomainDes + ", toDomainDes=" + toDomainDes
					+ ", lastModifiedTime=" + lastModifiedTime + ", getUncntrlTransferAllowedTmp()="
					+ getUncntrlTransferAllowedTmp() + ", getStatusDesc()=" + getStatusDesc()
					+ ", getTransferRuleType()=" + getTransferRuleType() + ", getPreviousStatus()="
					+ getPreviousStatus() + ", getFromDomainDes()=" + getFromDomainDes() + ", getToDomainDes()="
					+ getToDomainDes() + ", getLastModifiedTime()=" + getLastModifiedTime() + ", getFromCategoryDes()="
					+ getFromCategoryDes() + ", getToCategoryDes()=" + getToCategoryDes() + ", getFromSeqNo()="
					+ getFromSeqNo() + ", getToSeqNo()=" + getToSeqNo() + ", getApprovalRequired()="
					+ getApprovalRequired() + ", getFirstApprovalLimit()=" + getFirstApprovalLimit()
					+ ", getSecondApprovalLimit()=" + getSecondApprovalLimit() + ", getFocTransferType()="
					+ getFocTransferType() + ", getFocAllowed()=" + getFocAllowed() + ", getDpAllowed()="
					+ getDpAllowed() + ", getStatus()=" + getStatus() + ", getTransferAllowed()=" + getTransferAllowed()
					+ ", getProductVOList()=" + getProductVOList() + ", getProductArray()="
					+ Arrays.toString(getProductArray()) + ", getNetworkCode()=" + getNetworkCode()
					+ ", getTransferRuleID()=" + getTransferRuleID() + ", getType()=" + getType() + ", getDomainCode()="
					+ getDomainCode() + ", getToDomainCode()=" + getToDomainCode() + ", getFromCategory()="
					+ getFromCategory() + ", getToCategory()=" + getToCategory() + ", getParentAssocationAllowed()="
					+ getParentAssocationAllowed() + ", getTransferType()=" + getTransferType()
					+ ", getRestrictedMsisdnAccess()=" + getRestrictedMsisdnAccess()
					+ ", getRestrictedRechargeAccess()=" + getRestrictedRechargeAccess()
					+ ", getDirectTransferAllowed()=" + getDirectTransferAllowed() + ", getTransferChnlBypassAllowed()="
					+ getTransferChnlBypassAllowed() + ", getUncntrlTransferAllowed()=" + getUncntrlTransferAllowed()
					+ ", getUncntrlTransferLevel()=" + getUncntrlTransferLevel() + ", getCntrlTransferLevel()="
					+ getCntrlTransferLevel() + ", getFixedTransferLevel()=" + getFixedTransferLevel()
					+ ", getFixedTransferCategory()=" + getFixedTransferCategory() + ", getWithdrawChnlBypassAllowed()="
					+ getWithdrawChnlBypassAllowed() + ", getWithdrawAllowed()=" + getWithdrawAllowed()
					+ ", getUncntrlWithdrawAllowed()=" + getUncntrlWithdrawAllowed() + ", getUncntrlWithdrawLevel()="
					+ getUncntrlWithdrawLevel() + ", getCntrlWithdrawLevel()=" + getCntrlWithdrawLevel()
					+ ", getFixedWithdrawLevel()=" + getFixedWithdrawLevel() + ", getFixedWithdrawCategory()="
					+ getFixedWithdrawCategory() + ", getReturnAllowed()=" + getReturnAllowed()
					+ ", getReturnChnlBypassAllowed()=" + getReturnChnlBypassAllowed() + ", getUncntrlReturnAllowed()="
					+ getUncntrlReturnAllowed() + ", getUncntrlReturnLevel()=" + getUncntrlReturnLevel()
					+ ", getCntrlReturnLevel()=" + getCntrlReturnLevel() + ", getFixedReturnLevel()="
					+ getFixedReturnLevel() + ", getFixedReturnCategory()=" + getFixedReturnCategory()
					+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedOn()=" + getCreatedOn() + ", getModifiedBy()="
					+ getModifiedBy() + ", getModifiedOn()=" + getModifiedOn() + ", getRuleType()=" + getRuleType()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}


		public String getNetworkCode() {
			return networkCode;
		}


		public void setNetworkCode(String networkCode) {
			this.networkCode = networkCode;
		}


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


		public String getToDomainCode() {
			return toDomainCode;
		}


		public void setToDomainCode(String toDomainCode) {
			this.toDomainCode = toDomainCode;
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


		public String getParentAssocationAllowed() {
			return parentAssocationAllowed;
		}


		public void setParentAssocationAllowed(String parentAssocationAllowed) {
			this.parentAssocationAllowed = parentAssocationAllowed;
		}


		public String getTransferType() {
			return transferType;
		}


		public void setTransferType(String transferType) {
			this.transferType = transferType;
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


		public String getDirectTransferAllowed() {
			return directTransferAllowed;
		}


		public void setDirectTransferAllowed(String directTransferAllowed) {
			this.directTransferAllowed = directTransferAllowed;
		}


		public String getTransferChnlBypassAllowed() {
			return transferChnlBypassAllowed;
		}


		public void setTransferChnlBypassAllowed(String transferChnlBypassAllowed) {
			this.transferChnlBypassAllowed = transferChnlBypassAllowed;
		}


		public String getUncntrlTransferAllowed() {
			return uncntrlTransferAllowed;
		}


		public void setUncntrlTransferAllowed(String uncntrlTransferAllowed) {
			this.uncntrlTransferAllowed = uncntrlTransferAllowed;
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


		public String getWithdrawChnlBypassAllowed() {
			return withdrawChnlBypassAllowed;
		}


		public void setWithdrawChnlBypassAllowed(String withdrawChnlBypassAllowed) {
			this.withdrawChnlBypassAllowed = withdrawChnlBypassAllowed;
		}


		public String getWithdrawAllowed() {
			return withdrawAllowed;
		}


		public void setWithdrawAllowed(String withdrawAllowed) {
			this.withdrawAllowed = withdrawAllowed;
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


		

		public String getRuleType() {
			return ruleType;
		}


		public void setRuleType(String ruleType) {
			this.ruleType = ruleType;
		}
				
		
	  	}


