package com.restapi.superadmin.responseVO;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.btsl.common.BaseResponse;
import com.restapi.superadmin.requestVO.ChannelTransferRuleRequestVO;


public class ChannelTransferRuleViewResponseVO extends BaseResponse{
	
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
    private String[] productArray;
    private String domainCodeDesc;
    private String toDomainCodeDesc;
    private String fromCategoryDesc;
    private String toCategoryDesc;
    private String parentAssocationAllowedDesc;
    
    private String transferTypeDesc;
       
    
    private String uncntrlTransferLevelDesc;
    private String cntrlTransferLevelDesc ;
    private String fixedTransferLevelDesc ;
	   
    private String fixedTransferCategoryDesc ;
   
    private String uncntrlWithdrawLevelDesc;
    private String cntrlWithdrawLevelDesc ;
    private String fixedWithdrawLevelDesc ;
    private String fixedWithdrawCategoryDesc ;
    private String uncntrlReturnLevelDesc ;
    private String cntrlReturnLevelDesc ;
    private String fixedReturnLevelDesc ;
    private String fixedReturnCategoryDesc;
    private String []productArrayDesc;
    private boolean uncntrlTransferAllowedFlag;
    private boolean parentAssociationAllowedFlag;
    private boolean chnlByPassFlag;
    private boolean restrictedMsisdnAccessFlag;	
	
    private boolean restrictedRechargeFlag;
    private Integer toCategorySeqNumber;
    private Integer fromCategorySeqNumber;
    
	
	public boolean isUncntrlTransferAllowedFlag() {
		return uncntrlTransferAllowedFlag;
	}

	public void setUncntrlTransferAllowedFlag(boolean uncntrlTransferAllowedFlag) {
		this.uncntrlTransferAllowedFlag = uncntrlTransferAllowedFlag;
	}

	public boolean isParentAssociationAllowedFlag() {
		return parentAssociationAllowedFlag;
	}

	public void setParentAssociationAllowedFlag(boolean parentAssociationAllowedFlag) {
		this.parentAssociationAllowedFlag = parentAssociationAllowedFlag;
	}

	public boolean isChnlByPassFlag() {
		return chnlByPassFlag;
	}

	public void setChnlByPassFlag(boolean chnlByPassFlag) {
		this.chnlByPassFlag = chnlByPassFlag;
	}

	public boolean isRestrictedMsisdnAccessFlag() {
		return restrictedMsisdnAccessFlag;
	}

	public void setRestrictedMsisdnAccessFlag(boolean restrictedMsisdnAccessFlag) {
		this.restrictedMsisdnAccessFlag = restrictedMsisdnAccessFlag;
	}

	public boolean isRestrictedRechargeFlag() {
		return restrictedRechargeFlag;
	}

	public void setRestrictedRechargeFlag(boolean restrictedRechargeFlag) {
		this.restrictedRechargeFlag = restrictedRechargeFlag;
	}

	public Integer getToCategorySeqNumber() {
		return toCategorySeqNumber;
	}

	public void setToCategorySeqNumber(Integer toCategorySeqNumber) {
		this.toCategorySeqNumber = toCategorySeqNumber;
	}

	public Integer getFromCategorySeqNumber() {
		return fromCategorySeqNumber;
	}

	public void setFromCategorySeqNumber(Integer fromCategorySeqNumber) {
		this.fromCategorySeqNumber = fromCategorySeqNumber;
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

	public String[] getProductArray() {
		return productArray;
	}

	public void setProductArray(String[] productArray) {
		this.productArray = productArray;
	}

	public String getDomainCodeDesc() {
		return domainCodeDesc;
	}

	public void setDomainCodeDesc(String domainCodeDesc) {
		this.domainCodeDesc = domainCodeDesc;
	}

	public String getToDomainCodeDesc() {
		return toDomainCodeDesc;
	}

	public void setToDomainCodeDesc(String toDomainCodeDesc) {
		this.toDomainCodeDesc = toDomainCodeDesc;
	}

	public String getFromCategoryDesc() {
		return fromCategoryDesc;
	}

	public void setFromCategoryDesc(String fromCategoryDesc) {
		this.fromCategoryDesc = fromCategoryDesc;
	}

	public String getToCategoryDesc() {
		return toCategoryDesc;
	}

	public void setToCategoryDesc(String toCategoryDesc) {
		this.toCategoryDesc = toCategoryDesc;
	}

	public String getParentAssocationAllowedDesc() {
		return parentAssocationAllowedDesc;
	}

	public void setParentAssocationAllowedDesc(String parentAssocationAllowedDesc) {
		this.parentAssocationAllowedDesc = parentAssocationAllowedDesc;
	}

	public String getTransferTypeDesc() {
		return transferTypeDesc;
	}

	public void setTransferTypeDesc(String transferTypeDesc) {
		this.transferTypeDesc = transferTypeDesc;
	}

	
	public String getUncntrlTransferLevelDesc() {
		return uncntrlTransferLevelDesc;
	}

	public void setUncntrlTransferLevelDesc(String uncntrlTransferLevelDesc) {
		this.uncntrlTransferLevelDesc = uncntrlTransferLevelDesc;
	}

	public String getCntrlTransferLevelDesc() {
		return cntrlTransferLevelDesc;
	}

	public void setCntrlTransferLevelDesc(String cntrlTransferLevelDesc) {
		this.cntrlTransferLevelDesc = cntrlTransferLevelDesc;
	}

	public String getFixedTransferLevelDesc() {
		return fixedTransferLevelDesc;
	}

	public void setFixedTransferLevelDesc(String fixedTransferLevelDesc) {
		this.fixedTransferLevelDesc = fixedTransferLevelDesc;
	}

	public String getFixedTransferCategoryDesc() {
		return fixedTransferCategoryDesc;
	}

	public void setFixedTransferCategoryDesc(String fixedTransferCategoryDesc) {
		this.fixedTransferCategoryDesc = fixedTransferCategoryDesc;
	}

	
	
	public String getUncntrlWithdrawLevelDesc() {
		return uncntrlWithdrawLevelDesc;
	}

	public void setUncntrlWithdrawLevelDesc(String uncntrlWithdrawLevelDesc) {
		this.uncntrlWithdrawLevelDesc = uncntrlWithdrawLevelDesc;
	}

	public String getCntrlWithdrawLevelDesc() {
		return cntrlWithdrawLevelDesc;
	}

	public void setCntrlWithdrawLevelDesc(String cntrlWithdrawLevelDesc) {
		this.cntrlWithdrawLevelDesc = cntrlWithdrawLevelDesc;
	}

	public String getFixedWithdrawLevelDesc() {
		return fixedWithdrawLevelDesc;
	}

	public void setFixedWithdrawLevelDesc(String fixedWithdrawLevelDesc) {
		this.fixedWithdrawLevelDesc = fixedWithdrawLevelDesc;
	}

	public String getFixedWithdrawCategoryDesc() {
		return fixedWithdrawCategoryDesc;
	}

	public void setFixedWithdrawCategoryDesc(String fixedWithdrawCategoryDesc) {
		this.fixedWithdrawCategoryDesc = fixedWithdrawCategoryDesc;
	}

	
	public String getUncntrlReturnLevelDesc() {
		return uncntrlReturnLevelDesc;
	}

	public void setUncntrlReturnLevelDesc(String uncntrlReturnLevelDesc) {
		this.uncntrlReturnLevelDesc = uncntrlReturnLevelDesc;
	}

	public String getCntrlReturnLevelDesc() {
		return cntrlReturnLevelDesc;
	}

	public void setCntrlReturnLevelDesc(String cntrlReturnLevelDesc) {
		this.cntrlReturnLevelDesc = cntrlReturnLevelDesc;
	}

	public String getFixedReturnLevelDesc() {
		return fixedReturnLevelDesc;
	}

	public void setFixedReturnLevelDesc(String fixedReturnLevelDesc) {
		this.fixedReturnLevelDesc = fixedReturnLevelDesc;
	}

	public String getFixedReturnCategoryDesc() {
		return fixedReturnCategoryDesc;
	}

	public void setFixedReturnCategoryDesc(String fixedReturnCategoryDesc) {
		this.fixedReturnCategoryDesc = fixedReturnCategoryDesc;
	}

	public String[] getProductArrayDesc() {
		return productArrayDesc;
	}

	public void setProductArrayDesc(String[] productArrayDesc) {
		this.productArrayDesc = productArrayDesc;
	}

	private ChannelTransferRuleRequestVO channelTransferRuleRequestVO;

	public ChannelTransferRuleRequestVO getChannelTransferRuleRequestVO() {
		return channelTransferRuleRequestVO;
	}

	public void setChannelTransferRuleRequestVO(ChannelTransferRuleRequestVO channelTransferRuleRequestVO) {
		this.channelTransferRuleRequestVO = channelTransferRuleRequestVO;
	}

	@Override
	public String toString() {
		return "ChannelTransferRuleViewResponseVO [domainCode=" + domainCode + ", toDomainCode=" + toDomainCode
				+ ", fromCategory=" + fromCategory + ", toCategory=" + toCategory + ", parentAssocationAllowed="
				+ parentAssocationAllowed + ", transferType=" + transferType + ", restrictedMsisdnAccess="
				+ restrictedMsisdnAccess + ", restrictedRechargeAccess=" + restrictedRechargeAccess
				+ ", directTransferAllowed=" + directTransferAllowed + ", transferChnlBypassAllowed="
				+ transferChnlBypassAllowed + ", uncntrlTransferAllowed=" + uncntrlTransferAllowed
				+ ", uncntrlTransferLevel=" + uncntrlTransferLevel + ", cntrlTransferLevel=" + cntrlTransferLevel
				+ ", fixedTransferLevel=" + fixedTransferLevel + ", fixedTransferCategory=" + fixedTransferCategory
				+ ", withdrawChnlBypassAllowed=" + withdrawChnlBypassAllowed + ", withdrawAllowed=" + withdrawAllowed
				+ ", uncntrlWithdrawAllowed=" + uncntrlWithdrawAllowed + ", uncntrlWithdrawLevel="
				+ uncntrlWithdrawLevel + ", cntrlWithdrawLevel=" + cntrlWithdrawLevel + ", fixedWithdrawLevel="
				+ fixedWithdrawLevel + ", fixedWithdrawCategory=" + fixedWithdrawCategory + ", returnAllowed="
				+ returnAllowed + ", returnChnlBypassAllowed=" + returnChnlBypassAllowed + ", uncntrlReturnAllowed="
				+ uncntrlReturnAllowed + ", uncntrlReturnLevel=" + uncntrlReturnLevel + ", cntrlReturnLevel="
				+ cntrlReturnLevel + ", fixedReturnLevel=" + fixedReturnLevel + ", fixedReturnCategory="
				+ fixedReturnCategory + ", productArray=" + Arrays.toString(productArray) + ", domainCodeDesc="
				+ domainCodeDesc + ", toDomainCodeDesc=" + toDomainCodeDesc + ", fromCategoryDesc=" + fromCategoryDesc
				+ ", toCategoryDesc=" + toCategoryDesc + ", parentAssocationAllowedDesc=" + parentAssocationAllowedDesc
				+ ", transferTypeDesc=" + transferTypeDesc + ", uncntrlTransferLevelDesc=" + uncntrlTransferLevelDesc
				+ ", cntrlTransferLevelDesc=" + cntrlTransferLevelDesc + ", fixedTransferLevelDesc="
				+ fixedTransferLevelDesc + ", fixedTransferCategoryDesc=" + fixedTransferCategoryDesc
				+ ", uncntrlWithdrawLevelDesc=" + uncntrlWithdrawLevelDesc + ", cntrlWithdrawLevelDesc="
				+ cntrlWithdrawLevelDesc + ", fixedWithdrawLevelDesc=" + fixedWithdrawLevelDesc
				+ ", fixedWithdrawCategoryDesc=" + fixedWithdrawCategoryDesc + ", uncntrlReturnLevelDesc="
				+ uncntrlReturnLevelDesc + ", cntrlReturnLevelDesc=" + cntrlReturnLevelDesc + ", fixedReturnLevelDesc="
				+ fixedReturnLevelDesc + ", fixedReturnCategoryDesc=" + fixedReturnCategoryDesc + ", productArrayDesc="
				+ Arrays.toString(productArrayDesc) + ", uncntrlTransferAllowedFlag=" + uncntrlTransferAllowedFlag
				+ ", parentAssociationAllowedFlag=" + parentAssociationAllowedFlag + ", chnlByPassFlag="
				+ chnlByPassFlag + ", restrictedMsisdnAccessFlag=" + restrictedMsisdnAccessFlag
				+ ", restrictedRechargeFlag=" + restrictedRechargeFlag + ", toCategorySeqNumber=" + toCategorySeqNumber
				+ ", fromCategorySeqNumber=" + fromCategorySeqNumber + ", channelTransferRuleRequestVO="
				+ channelTransferRuleRequestVO + "]";
	}
	
	
}
