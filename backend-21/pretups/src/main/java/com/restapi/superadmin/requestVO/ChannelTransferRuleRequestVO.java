package com.restapi.superadmin.requestVO;

public class ChannelTransferRuleRequestVO {
	
	
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
	
}
