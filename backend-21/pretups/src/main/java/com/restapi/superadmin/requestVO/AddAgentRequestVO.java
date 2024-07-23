package com.restapi.superadmin.requestVO;

public class AddAgentRequestVO {

	private String domainCodeofCategory;
	private String domainName;
	private String parentCategoryCode;
	private String agentCategoryCode;
	private String agentCategoryName;
	private String geoDomainType;
	private String roleType;
	private String userIDPrefix;
	private String outletAllowed;
	private String hierarchyAllowed;
	private String[] roleFlag;
	private String allowedSources;
	private String multipleLoginAllowed;
	private String scheduleTransferAllowed;
	private String uncontrolledTransferAllowed;
	private String restrictedMsisdn;
	private String servicesAllowed;
	private String viewonNetworkBlock;
	private String allowLowBalanceAlert;
	private String maximumTransMsisdn;
	private String maximumLoginCount;
	private String transferToListOnly;
	private String rechargeThruParentOnly;
	private String cp2pPayer;
	private String cp2pPayee;
	private String cp2pWithinList;
	private String agentAllowed;
	private String parentOrOwnerRadioValue;

	public String getAgentCategoryCode() {
		return agentCategoryCode;
	}

	public void setAgentCategoryCode(String agentCategoryCode) {
		this.agentCategoryCode = agentCategoryCode;
	}

	public String getAgentCategoryName() {
		return agentCategoryName;
	}

	public void setAgentCategoryName(String agentCategoryName) {
		this.agentCategoryName = agentCategoryName;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getUserIDPrefix() {
		return userIDPrefix;
	}

	public void setUserIDPrefix(String userIDPrefix) {
		this.userIDPrefix = userIDPrefix;
	}

	public String getOutletAllowed() {
		return outletAllowed;
	}

	public void setOutletAllowed(String outletAllowed) {
		this.outletAllowed = outletAllowed;
	}

	public String getHierarchyAllowed() {
		return hierarchyAllowed;
	}

	public void setHierarchyAllowed(String hierarchyAllowed) {
		this.hierarchyAllowed = hierarchyAllowed;
	}

	public String[] getRoleFlag() {
		return roleFlag;
	}

	public void setRoleFlag(String[] roleFlag) {
		this.roleFlag = roleFlag;
	}

	public String getMultipleLoginAllowed() {
		return multipleLoginAllowed;
	}

	public void setMultipleLoginAllowed(String multipleLoginAllowed) {
		this.multipleLoginAllowed = multipleLoginAllowed;
	}

	public String getScheduleTransferAllowed() {
		return scheduleTransferAllowed;
	}

	public void setScheduleTransferAllowed(String scheduleTransferAllowed) {
		this.scheduleTransferAllowed = scheduleTransferAllowed;
	}

	public String getUncontrolledTransferAllowed() {
		return uncontrolledTransferAllowed;
	}

	public void setUncontrolledTransferAllowed(String uncontrolledTransferAllowed) {
		this.uncontrolledTransferAllowed = uncontrolledTransferAllowed;
	}

	public String getRestrictedMsisdn() {
		return restrictedMsisdn;
	}

	public void setRestrictedMsisdn(String restrictedMsisdn) {
		this.restrictedMsisdn = restrictedMsisdn;
	}

	public String getServicesAllowed() {
		return servicesAllowed;
	}

	public void setServicesAllowed(String servicesAllowed) {
		this.servicesAllowed = servicesAllowed;
	}

	public String getViewonNetworkBlock() {
		return viewonNetworkBlock;
	}

	public void setViewonNetworkBlock(String viewonNetworkBlock) {
		this.viewonNetworkBlock = viewonNetworkBlock;
	}

	public String getAllowLowBalanceAlert() {
		return allowLowBalanceAlert;
	}

	public void setAllowLowBalanceAlert(String allowLowBalanceAlert) {
		this.allowLowBalanceAlert = allowLowBalanceAlert;
	}

	public String getMaximumTransMsisdn() {
		return maximumTransMsisdn;
	}

	public void setMaximumTransMsisdn(String maximumTransMsisdn) {
		this.maximumTransMsisdn = maximumTransMsisdn;
	}

	public String getMaximumLoginCount() {
		return maximumLoginCount;
	}

	public void setMaximumLoginCount(String maximumLoginCount) {
		this.maximumLoginCount = maximumLoginCount;
	}

	public String getTransferToListOnly() {
		return transferToListOnly;
	}

	public void setTransferToListOnly(String transferToListOnly) {
		this.transferToListOnly = transferToListOnly;
	}

	public String getRechargeThruParentOnly() {
		return rechargeThruParentOnly;
	}

	public void setRechargeThruParentOnly(String rechargeThruParentOnly) {
		this.rechargeThruParentOnly = rechargeThruParentOnly;
	}

	public String getCp2pPayer() {
		return cp2pPayer;
	}

	public void setCp2pPayer(String cp2pPayer) {
		this.cp2pPayer = cp2pPayer;
	}

	public String getCp2pPayee() {
		return cp2pPayee;
	}

	public void setCp2pPayee(String cp2pPayee) {
		this.cp2pPayee = cp2pPayee;
	}

	public String getCp2pWithinList() {
		return cp2pWithinList;
	}

	public void setCp2pWithinList(String cp2pWithinList) {
		this.cp2pWithinList = cp2pWithinList;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getGeoDomainType() {
		return geoDomainType;
	}

	public void setGeoDomainType(String geoDomainType) {
		this.geoDomainType = geoDomainType;
	}

	public String getAllowedSources() {
		return allowedSources;
	}

	public void setAllowedSources(String allowedSources) {
		this.allowedSources = allowedSources;
	}

	public String getParentCategoryCode() {
		return parentCategoryCode;
	}

	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}

	public String getDomainCodeofCategory() {
		return domainCodeofCategory;
	}

	public void setDomainCodeofCategory(String domainCodeofCategory) {
		this.domainCodeofCategory = domainCodeofCategory;
	}

	public String getAgentAllowed() {
		return agentAllowed;
	}

	public void setAgentAllowed(String agentAllowed) {
		this.agentAllowed = agentAllowed;
	}

	public String getParentOrOwnerRadioValue() {
		return parentOrOwnerRadioValue;
	}

	public void setParentOrOwnerRadioValue(String parentOrOwnerRadioValue) {
		this.parentOrOwnerRadioValue = parentOrOwnerRadioValue;
	}

}
