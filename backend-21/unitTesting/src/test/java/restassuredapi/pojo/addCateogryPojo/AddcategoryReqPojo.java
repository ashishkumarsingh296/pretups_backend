package restassuredapi.pojo.addCateogryPojo;

import java.util.ArrayList;

import java.util.ArrayList;

public class AddcategoryReqPojo {

	private String domainCodeforDomain;
	private String domainTypeCode;
	private String domainName;
	private String numberOfCategories;

	private String categoryCode;
	private String categoryName;
	private String agentCategoryCode;
	private String agentAllowed;

	private ArrayList modifiedMessageGatewayList;
	private ArrayList messageGatewayList;

	private String parentCategoryCode;
	private String grphDomainType;
	private String multipleGrphDomains;
	private String fixedRoles;
	private String multipleLoginAllowed;
	private String viewOnNetworkBlock;
	private String maxLoginCount;
	private String categoryStatus;
	private String displayAllowed;
	private String modifyAllowed;
	private String productTypeAssociationAllowed;
	private String maxTxnMsisdnOld;
	private String maxTxnMsisdns;
	private String unctrlTransferAllowed;
	private String scheduledTransferAllowed;
	private String restrictedMsisdns;
	private String userIdPrefix;
	private long lastModifiedTime;
	private String serviceAllowed;
	private String outletsAllowed;
	private String hierarchyAllowed;
	private String transferToListOnly;
	private String lowBalanceAlertAllow;

	private String rechargeByParentOnly;
	private String cp2pPayer;
	private String cp2pPayee;
	private String cp2pWithinList;
	private String listLevelCode;

	private String checkArray[];

	private String[] roleFlag;

	//new
	private String domainCodeforCategory;
	private String authType;
	private String categoryType;
	private int categorySequenceNumber;
	public String getDomainCodeforDomain() {
		return domainCodeforDomain;
	}
	public void setDomainCodeforDomain(String domainCodeforDomain) {
		this.domainCodeforDomain = domainCodeforDomain;
	}
	public String getDomainTypeCode() {
		return domainTypeCode;
	}
	public void setDomainTypeCode(String domainTypeCode) {
		this.domainTypeCode = domainTypeCode;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getNumberOfCategories() {
		return numberOfCategories;
	}
	public void setNumberOfCategories(String numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getAgentCategoryCode() {
		return agentCategoryCode;
	}
	public void setAgentCategoryCode(String agentCategoryCode) {
		this.agentCategoryCode = agentCategoryCode;
	}
	public String getAgentAllowed() {
		return agentAllowed;
	}
	public void setAgentAllowed(String agentAllowed) {
		this.agentAllowed = agentAllowed;
	}
	public ArrayList getModifiedMessageGatewayList() {
		return modifiedMessageGatewayList;
	}
	public void setModifiedMessageGatewayList(ArrayList modifiedMessageGatewayList) {
		this.modifiedMessageGatewayList = modifiedMessageGatewayList;
	}
	public ArrayList getMessageGatewayList() {
		return messageGatewayList;
	}
	public void setMessageGatewayList(ArrayList messageGatewayList) {
		this.messageGatewayList = messageGatewayList;
	}
	public String getParentCategoryCode() {
		return parentCategoryCode;
	}
	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}
	public String getGrphDomainType() {
		return grphDomainType;
	}
	public void setGrphDomainType(String grphDomainType) {
		this.grphDomainType = grphDomainType;
	}
	public String getMultipleGrphDomains() {
		return multipleGrphDomains;
	}
	public void setMultipleGrphDomains(String multipleGrphDomains) {
		this.multipleGrphDomains = multipleGrphDomains;
	}
	public String getFixedRoles() {
		return fixedRoles;
	}
	public void setFixedRoles(String fixedRoles) {
		this.fixedRoles = fixedRoles;
	}
	public String getMultipleLoginAllowed() {
		return multipleLoginAllowed;
	}
	public void setMultipleLoginAllowed(String multipleLoginAllowed) {
		this.multipleLoginAllowed = multipleLoginAllowed;
	}
	public String getViewOnNetworkBlock() {
		return viewOnNetworkBlock;
	}
	public void setViewOnNetworkBlock(String viewOnNetworkBlock) {
		this.viewOnNetworkBlock = viewOnNetworkBlock;
	}
	public String getMaxLoginCount() {
		return maxLoginCount;
	}
	public void setMaxLoginCount(String maxLoginCount) {
		this.maxLoginCount = maxLoginCount;
	}
	public String getCategoryStatus() {
		return categoryStatus;
	}
	public void setCategoryStatus(String categoryStatus) {
		this.categoryStatus = categoryStatus;
	}
	public String getDisplayAllowed() {
		return displayAllowed;
	}
	public void setDisplayAllowed(String displayAllowed) {
		this.displayAllowed = displayAllowed;
	}
	public String getModifyAllowed() {
		return modifyAllowed;
	}
	public void setModifyAllowed(String modifyAllowed) {
		this.modifyAllowed = modifyAllowed;
	}
	public String getProductTypeAssociationAllowed() {
		return productTypeAssociationAllowed;
	}
	public void setProductTypeAssociationAllowed(String productTypeAssociationAllowed) {
		this.productTypeAssociationAllowed = productTypeAssociationAllowed;
	}
	public String getMaxTxnMsisdnOld() {
		return maxTxnMsisdnOld;
	}
	public void setMaxTxnMsisdnOld(String maxTxnMsisdnOld) {
		this.maxTxnMsisdnOld = maxTxnMsisdnOld;
	}
	public String getMaxTxnMsisdns() {
		return maxTxnMsisdns;
	}
	public void setMaxTxnMsisdns(String maxTxnMsisdns) {
		this.maxTxnMsisdns = maxTxnMsisdns;
	}
	public String getUnctrlTransferAllowed() {
		return unctrlTransferAllowed;
	}
	public void setUnctrlTransferAllowed(String unctrlTransferAllowed) {
		this.unctrlTransferAllowed = unctrlTransferAllowed;
	}
	public String getScheduledTransferAllowed() {
		return scheduledTransferAllowed;
	}
	public void setScheduledTransferAllowed(String scheduledTransferAllowed) {
		this.scheduledTransferAllowed = scheduledTransferAllowed;
	}
	public String getRestrictedMsisdns() {
		return restrictedMsisdns;
	}
	public void setRestrictedMsisdns(String restrictedMsisdns) {
		this.restrictedMsisdns = restrictedMsisdns;
	}
	public String getUserIdPrefix() {
		return userIdPrefix;
	}
	public void setUserIdPrefix(String userIdPrefix) {
		this.userIdPrefix = userIdPrefix;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public String getServiceAllowed() {
		return serviceAllowed;
	}
	public void setServiceAllowed(String serviceAllowed) {
		this.serviceAllowed = serviceAllowed;
	}
	public String getOutletsAllowed() {
		return outletsAllowed;
	}
	public void setOutletsAllowed(String outletsAllowed) {
		this.outletsAllowed = outletsAllowed;
	}
	public String getHierarchyAllowed() {
		return hierarchyAllowed;
	}
	public void setHierarchyAllowed(String hierarchyAllowed) {
		this.hierarchyAllowed = hierarchyAllowed;
	}
	public String getTransferToListOnly() {
		return transferToListOnly;
	}
	public void setTransferToListOnly(String transferToListOnly) {
		this.transferToListOnly = transferToListOnly;
	}
	public String getLowBalanceAlertAllow() {
		return lowBalanceAlertAllow;
	}
	public void setLowBalanceAlertAllow(String lowBalanceAlertAllow) {
		this.lowBalanceAlertAllow = lowBalanceAlertAllow;
	}
	public String getRechargeByParentOnly() {
		return rechargeByParentOnly;
	}
	public void setRechargeByParentOnly(String rechargeByParentOnly) {
		this.rechargeByParentOnly = rechargeByParentOnly;
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
	public String getListLevelCode() {
		return listLevelCode;
	}
	public void setListLevelCode(String listLevelCode) {
		this.listLevelCode = listLevelCode;
	}
	public String[] getCheckArray() {
		return checkArray;
	}
	public void setCheckArray(String[] checkArray) {
		this.checkArray = checkArray;
	}
	public String[] getRoleFlag() {
		return roleFlag;
	}
	public void setRoleFlag(String[] roleFlag) {
		this.roleFlag = roleFlag;
	}
	public String getDomainCodeforCategory() {
		return domainCodeforCategory;
	}
	public void setDomainCodeforCategory(String domainCodeforCategory) {
		this.domainCodeforCategory = domainCodeforCategory;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	public int getCategorySequenceNumber() {
		return categorySequenceNumber;
	}
	public void setCategorySequenceNumber(int categorySequenceNumber) {
		this.categorySequenceNumber = categorySequenceNumber;
	}

}
