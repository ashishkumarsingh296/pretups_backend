package restassuredapi.pojo.addCateogryPojo;

/*
 * @# CategoryVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 28, 2005 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CategoryVO implements Serializable {

	private String _categoryCode;
	private String _categoryName;
	private String _domainCodeforCategory;
	private int _sequenceNumber;
	private String _grphDomainType;
	private String _grphDomainTypeName;
	private int _grphDomainSequenceNo;
	private String _multipleGrphDomains;
	private String _webInterfaceAllowed;
	private String _smsInterfaceAllowed;
	private String _fixedRoles;
	private String _multipleLoginAllowed;
	private String _viewOnNetworkBlock;
	private long _maxLoginCount;
	private String _categoryStatus;
	private Date _createdOn;
	private String _createdBy;
	private Date _modifiedOn;
	private String _modifiedBy;
	private String _displayAllowed;
	private String _modifyAllowed;
	private String _hierarchyAllowed;
	private String _agentAllowed;
	private String _categoryType;
	private String _productTypeAssociationAllowed;
	private String _maxTxnMsisdn;
	private String _unctrlTransferAllowed;
	private String _scheduledTransferAllowed;
	private String _serviceAllowed;
	private String _restrictedMsisdns;
	private String _parentCategoryCode;
	private String _txnOutsideHierchy;
	private String _userIdPrefix;
	private long _lastModifiedTime;
	private int _recordCount;
	private int _radioIndex;
	private String _productTypeAllowed;
	private String _domainTypeCode;
	private ArrayList _allowedGatewayTypes;
	private String _domainAllowed;
	private String _fixedDomains;
	private String _outletsAllowed;
	private int _categorySequenceNumber;
	private int _geographicalDomainSeqNo;
	private int _maxTxnMsisdnInt;
	private String _categoryTypeCode;
	// for Agent Category

	private String _agentGeographicalDomainList;
	private ArrayList _agentRoleTypeList;
	private ArrayList _agentCategoryStatusList;
	private String _agentMultipleGrphDomains;
	private String _agentProductTypeAssociationAllowed;
	private String _agentDisplayAllowed;
	private String _agentModifyAllowed;
	private String _agentAgentAllowed;
	private String _agentCategoryType;
	private String _agentDomainCodeforCategory;
	private String _agentDomainName;
	private String _agentCategoryName;
	private String _agentCategoryCode;
	private String _agentGrphDomainType;
	private String _agentFixedRoles;
	private String _agentCategoryStatus;
	private String _agentUserIdPrefix;
	private String _agentOutletsAllowed;
	private HashMap _agentRolesMapSelected;
	private String _agentRoleName;
	private ArrayList _agentMessageGatewayTypeList;
	private ArrayList _agentModifiedMessageGatewayTypeList;
	private String _agentGatewayName;
	private String _agentCheckArray[];
	private String _agentGatewayType;
	private String _agentMultipleLoginAllowed;
	private String _agentViewOnNetworkBlock;
	private String _agentRestrictedMsisdns;
	private String _agentScheduledTransferAllowed;
	private String _agentUnctrlTransferAllowed;
	private String _agentServiceAllowed;
	private long _agentMaxLoginCount;
	private String _agentMaxTxnMsisdn;
	private String _agentHierarchyAllowed;
	private String _agentAllowedFlag;

	private String _agentWebInterfaceAllowed;
	private String _agentSmsInterfaceAllowed;
	private String _domainName;
	private String _transferToListOnly;
	private int _numberOfCategoryForDomain;
	// for low balance alert
	private String _lowBalAlertAllow;
	private String _agentLowBalAlertAllow;

	// for categoryList management
	private String _rechargeByParentOnly;
	private String _cp2pPayer;
	private String _cp2pPayee;
	private String _cp2pWithinList;
	private String _parentOrOwnerRadioValue;
	// for agent categoryList management
	private String _agentRechargeByParentOnly;
	private String _agentCp2pPayer;
	private String _agentCp2pPayee;
	private String _agentCp2pWithinList;
	private String _agentParentOrOwnerRadioValue;
	// added for OTP Authetication
	// private String _otpAuthenticationAllowed;
	private String _authenticationType;

	/**
	 * @return Returns the numberOfCategoryForDomain.
	 */
	public int getNumberOfCategoryForDomain() {
		return this._numberOfCategoryForDomain;
	}

	/**
	 * @param numberOfCategoryForDomain The numberOfCategoryForDomain to set.
	 */
	public void setNumberOfCategoryForDomain(int numberOfCategoryForDomain) {
		this._numberOfCategoryForDomain = numberOfCategoryForDomain;
	}

	/**
	 * @return
	 */
	public String getTransferToListOnly() {
		return this._transferToListOnly;
	}

	/**
	 * @param transferToListOnly
	 */
	public void setTransferToListOnly(String transferToListOnly) {
		this._transferToListOnly = transferToListOnly;
	}

	/**
	 * @return Returns the categoryType.
	 */
	public String getCategoryType() {
		return _categoryType;
	}

	/**
	 * @param categoryType The categoryType to set.
	 */
	public void setCategoryType(String categoryType) {
		_categoryType = categoryType;
	}

	/**
	 * @return Returns the agentAllowed.
	 */
	public String getAgentAllowed() {
		return _agentAllowed;
	}

	/**
	 * @param agentAllowed The agentAllowed to set.
	 */
	public void setAgentAllowed(String agentAllowed) {
		_agentAllowed = agentAllowed;
	}

	/**
	 * @return Returns the hierarchyAllowed.
	 */
	public String getHierarchyAllowed() {
		return _hierarchyAllowed;
	}

	/**
	 * @param hierarchyAllowed The hierarchyAllowed to set.
	 */
	public void setHierarchyAllowed(String hierarchyAllowed) {
		_hierarchyAllowed = hierarchyAllowed;
	}

	/**
	 * @return Returns the agentMaxLoginCount.
	 */
	public long getAgentMaxLoginCount() {
		return _agentMaxLoginCount;
	}

	/**
	 * @param agentMaxLoginCount The agentMaxLoginCount to set.
	 */
	public void setAgentMaxLoginCount(long agentMaxLoginCount) {
		_agentMaxLoginCount = agentMaxLoginCount;
	}

	/**
	 * @return Returns the agentSmsInterfaceAllowed.
	 */
	public String getAgentSmsInterfaceAllowed() {
		return _agentSmsInterfaceAllowed;
	}

	/**
	 * @param agentSmsInterfaceAllowed The agentSmsInterfaceAllowed to set.
	 */
	public void setAgentSmsInterfaceAllowed(String agentSmsInterfaceAllowed) {
		_agentSmsInterfaceAllowed = agentSmsInterfaceAllowed;
	}

	/**
	 * @return Returns the agentWebInterfaceAllowed.
	 */
	public String getAgentWebInterfaceAllowed() {
		return _agentWebInterfaceAllowed;
	}

	/**
	 * @param agentWebInterfaceAllowed The agentWebInterfaceAllowed to set.
	 */
	public void setAgentWebInterfaceAllowed(String agentWebInterfaceAllowed) {
		_agentWebInterfaceAllowed = agentWebInterfaceAllowed;
	}

	public String toString() {
		StringBuffer strBuff = new StringBuffer("\nCategory Code=" + _categoryCode);
		strBuff.append("\nCategory Name=" + _categoryName);
		strBuff.append("\nDomain code for category=" + _domainCodeforCategory);
		strBuff.append("\nDomain Type=" + _domainTypeCode);
		strBuff.append("\nSequence Number=" + _sequenceNumber);
		strBuff.append("\nGrph domain type=" + _grphDomainType);
		strBuff.append("\nGrph domain type name=" + _grphDomainTypeName);
		strBuff.append("\nGrph domain sequence no=" + _grphDomainSequenceNo);
		strBuff.append("\nMultiple Grph domains=" + _multipleGrphDomains);
		strBuff.append("\nWeb Interface Allowed=" + _webInterfaceAllowed);
		strBuff.append("\nSms Interface Allowed=" + _smsInterfaceAllowed);
		strBuff.append("\nFixed roles=" + _fixedRoles);
		strBuff.append("\nMultiple Login Allowed=" + _multipleLoginAllowed);
		strBuff.append("\nView on network block=" + _viewOnNetworkBlock);
		strBuff.append("\nMax login count=" + _maxLoginCount);
		strBuff.append("\nCategory Status=" + _categoryStatus);
		strBuff.append("\nCategory Created on=" + _createdOn);
		strBuff.append("\nCategory Modified on=" + _modifiedOn);
		strBuff.append("\nCategory modified by=" + _modifiedBy);
		strBuff.append("\nDisplay Allowed=" + _displayAllowed);
		strBuff.append("\nModify Allowed=" + _modifyAllowed);
		strBuff.append("\nProduct Type Association Allowed=" + _productTypeAssociationAllowed);
		strBuff.append("\nMax Txn Msisdn=" + _maxTxnMsisdn);
		strBuff.append("\nUnctrl transfer allowed=" + _unctrlTransferAllowed);
		strBuff.append("\nSchedule Transfer Allowed=" + _scheduledTransferAllowed);
		strBuff.append("\nRestricted Msisdns=" + _restrictedMsisdns);
		strBuff.append("\nParent Category code=" + _parentCategoryCode);
		strBuff.append("\nUser id prefix=" + _userIdPrefix);
		strBuff.append("\nLast Modified=" + _lastModifiedTime);
		strBuff.append("\nRecord Count=" + _recordCount);
		strBuff.append("\nProduct Type Allowed=" + _productTypeAllowed);
		strBuff.append("\nService Allowed=" + _serviceAllowed);
		strBuff.append("\nDomain Allowed=" + _domainAllowed);
		strBuff.append("\nFixed Domains=" + _fixedDomains);
		strBuff.append("\nOutlets Allowed=" + _outletsAllowed);
		strBuff.append("\nTransfer To List Only=" + _transferToListOnly);
		strBuff.append("\nLow Balance Alert Allow=" + _lowBalAlertAllow);

		return strBuff.toString();
	}

	/**
	 * To get the value of categorySequenceNumber field
	 * 
	 * @return categorySequenceNumber.
	 */
	public int getCategorySequenceNumber() {
		return _categorySequenceNumber;
	}

	/**
	 * To set the value of categorySequenceNumber field
	 */
	public void setCategorySequenceNumber(int categorySequenceNumber) {
		_categorySequenceNumber = categorySequenceNumber;
	}

	/**
	 * To get the value of geographicalDomainSeqNo field
	 * 
	 * @return geographicalDomainSeqNo.
	 */
	public int getGeographicalDomainSeqNo() {
		return _geographicalDomainSeqNo;
	}

	/**
	 * To set the value of geographicalDomainSeqNo field
	 */
	public void setGeographicalDomainSeqNo(int geographicalDomainSeqNo) {
		_geographicalDomainSeqNo = geographicalDomainSeqNo;
	}

	/**
	 * To get the value of radioIndex field
	 * 
	 * @return radioIndex.
	 */
	public int getRadioIndex() {
		return _radioIndex;
	}

	/**
	 * To set the value of radioIndex field
	 */
	public void setRadioIndex(int radioIndex) {
		_radioIndex = radioIndex;
	}

	/**
	 * To get the value of recordCount field
	 * 
	 * @return recordCount.
	 */
	public int getRecordCount() {
		return _recordCount;
	}

	/**
	 * To set the value of recordCount field
	 */
	public void setRecordCount(int recordCount) {
		_recordCount = recordCount;
	}

	/**
	 * To get the value of lastModified field
	 * 
	 * @return lastModified.
	 */
	public long getLastModifiedTime() {
		return _lastModifiedTime;
	}

	/**
	 * To set the value of lastModified field
	 */
	public void setLastModifiedTime(long lastModifiedTime) {
		_lastModifiedTime = lastModifiedTime;
	}

	/**
	 * To get the value of userIdPrefix field
	 * 
	 * @return userIdPrefix.
	 */
	public String getUserIdPrefix() {
		return _userIdPrefix;
	}

	/**
	 * To set the value of userIdPrefix field
	 */
	public void setUserIdPrefix(String userIdPrefix) {
		_userIdPrefix = userIdPrefix;
	}

	/**
	 * To get the value of categoryCode field
	 * 
	 * @return categoryCode.
	 */
	public String getCategoryCode() {
		return _categoryCode;
	}

	/**
	 * To set the value of categoryCode field
	 */
	public void setCategoryCode(String categoryCode) {
		_categoryCode = categoryCode;
	}

	/**
	 * To get the value of createdBy field
	 * 
	 * @return createdBy.
	 */
	public String getCreatedBy() {
		return _createdBy;
	}

	/**
	 * To set the value of createdBy field
	 */
	public void setCreatedBy(String createdBy) {
		_createdBy = createdBy;
	}

	/**
	 * To get the value of createdOn field
	 * 
	 * @return createdOn.
	 */
	public Date getCreatedOn() {
		return _createdOn;
	}

	/**
	 * To set the value of createdOn field
	 */
	public void setCreatedOn(Date createdOn) {
		_createdOn = createdOn;
	}

	/**
	 * To get the value of modifiedBy field
	 * 
	 * @return modifiedBy.
	 */
	public String getModifiedBy() {
		return _modifiedBy;
	}

	/**
	 * To set the value of modifiedBy field
	 */
	public void setModifiedBy(String modifiedBy) {
		_modifiedBy = modifiedBy;
	}

	/**
	 * To get the value of modifiedOn field
	 * 
	 * @return modifiedOn.
	 */
	public Date getModifiedOn() {
		return _modifiedOn;
	}

	/**
	 * To set the value of modifiedOn field
	 */
	public void setModifiedOn(Date modifiedOn) {
		_modifiedOn = modifiedOn;
	}

	/**
	 * To get the value of categoryName field
	 * 
	 * @return categoryName.
	 */
	public String getCategoryName() {
		return _categoryName;
	}

	/**
	 * To set the value of categoryName field
	 */
	public void setCategoryName(String categoryName) {
		_categoryName = categoryName;
	}

	/**
	 * To get the value of categoryStatus field
	 * 
	 * @return categoryStatus.
	 */
	public String getCategoryStatus() {
		return _categoryStatus;
	}

	/**
	 * To set the value of categoryStatus field
	 */
	public void setCategoryStatus(String categoryStatus) {
		_categoryStatus = categoryStatus;
	}

	/**
	 * To get the value of displayAllowed field
	 * 
	 * @return displayAllowed.
	 */
	public String getDisplayAllowed() {
		return _displayAllowed;
	}

	/**
	 * To set the value of displayAllowed field
	 */
	public void setDisplayAllowed(String displayAllowed) {
		_displayAllowed = displayAllowed;
	}

	/**
	 * To get the value of domainCodeforCategory field
	 * 
	 * @return domainCodeforCategory.
	 */
	public String getDomainCodeforCategory() {
		return _domainCodeforCategory;
	}

	/**
	 * To set the value of domainCodeforCategory field
	 */
	public void setDomainCodeforCategory(String domainCodeforCategory) {
		_domainCodeforCategory = domainCodeforCategory;
	}

	/**
	 * To get the value of fixedRoles field
	 * 
	 * @return fixedRoles.
	 */
	public String getFixedRoles() {
		return _fixedRoles;
	}

	public void setFixedRoles(String fixedRoles) {
		_fixedRoles = fixedRoles;
	}

	public String getGrphDomainType() {
		return _grphDomainType;
	}

	public void setGrphDomainType(String grphDomainType) {
		_grphDomainType = grphDomainType;
	}

	public String getMaxTxnMsisdn() {
		return _maxTxnMsisdn;
	}

	public void setMaxTxnMsisdn(String maxTxnMsisdn) {
		_maxTxnMsisdn = maxTxnMsisdn;
	}

	public String getModifyAllowed() {
		return _modifyAllowed;
	}

	public void setModifyAllowed(String modifyAllowed) {
		_modifyAllowed = modifyAllowed;
	}

	public String getMultipleGrphDomains() {
		return _multipleGrphDomains;
	}

	public void setMultipleGrphDomains(String multipleGrphDomains) {
		_multipleGrphDomains = multipleGrphDomains;
	}

	public String getMultipleLoginAllowed() {
		return _multipleLoginAllowed;
	}

	public void setMultipleLoginAllowed(String multipleLoginAllowed) {
		_multipleLoginAllowed = multipleLoginAllowed;
	}

	public String getParentCategoryCode() {
		return _parentCategoryCode;
	}

	public void setParentCategoryCode(String parentCategoryCode) {
		_parentCategoryCode = parentCategoryCode;
	}

	public String getRestrictedMsisdns() {
		return _restrictedMsisdns;
	}

	public void setRestrictedMsisdns(String restrictedMsisdns) {
		_restrictedMsisdns = restrictedMsisdns;
	}

	public String getScheduledTransferAllowed() {
		return _scheduledTransferAllowed;
	}

	public void setScheduledTransferAllowed(String scheduledTransferAllowed) {
		_scheduledTransferAllowed = scheduledTransferAllowed;
	}

	public int getSequenceNumber() {
		return _sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		_sequenceNumber = sequenceNumber;
	}

	public String getSmsInterfaceAllowed() {
		return _smsInterfaceAllowed;
	}

	public void setSmsInterfaceAllowed(String smsInterfaceAllowed) {
		_smsInterfaceAllowed = smsInterfaceAllowed;
	}

	public String getUnctrlTransferAllowed() {
		return _unctrlTransferAllowed;
	}

	public void setUnctrlTransferAllowed(String unctrlTransferAllowed) {
		_unctrlTransferAllowed = unctrlTransferAllowed;
	}

	public String getViewOnNetworkBlock() {
		return _viewOnNetworkBlock;
	}

	public void setViewOnNetworkBlock(String viewOnNetworkBlock) {
		_viewOnNetworkBlock = viewOnNetworkBlock;
	}

	public String getWebInterfaceAllowed() {
		return _webInterfaceAllowed;
	}

	public void setWebInterfaceAllowed(String webInterfaceAllowed) {
		_webInterfaceAllowed = webInterfaceAllowed;
	}

	public String getTxnOutsideHierchy() {
		return _txnOutsideHierchy;
	}

	public void setTxnOutsideHierchy(String txnOutsideHierchy) {
		_txnOutsideHierchy = txnOutsideHierchy;
	}

	public long getMaxLoginCount() {
		return _maxLoginCount;
	}

	public void setMaxLoginCount(long maxLoginCount) {
		_maxLoginCount = maxLoginCount;
	}

	public int getGrphDomainSequenceNo() {
		return _grphDomainSequenceNo;
	}

	public void setGrphDomainSequenceNo(int grphDomainSequenceNo) {
		_grphDomainSequenceNo = grphDomainSequenceNo;
	}

	public String getCombinedKey() {
		return _categoryCode + ":" + _domainCodeforCategory + ":" + _sequenceNumber;
	}

	public String getProductTypeAssociationAllowed() {
		return _productTypeAssociationAllowed;
	}

	public void setProductTypeAssociationAllowed(String productTypeAssociationAllowed) {
		_productTypeAssociationAllowed = productTypeAssociationAllowed;
	}

	public String getProductTypeAllowed() {
		return _productTypeAllowed;
	}

	public void setProductTypeAllowed(String productTypeAllowed) {
		_productTypeAllowed = productTypeAllowed;
	}

	public String getServiceAllowed() {
		return _serviceAllowed;
	}

	public void setServiceAllowed(String serviceAllowed) {
		_serviceAllowed = serviceAllowed;
	}

	public String getDomainTypeCode() {
		return _domainTypeCode;
	}

	public void setDomainTypeCode(String domainTypeCode) {
		_domainTypeCode = domainTypeCode;
	}

	public ArrayList getAllowedGatewayTypes() {
		return _allowedGatewayTypes;
	}

	public void setAllowedGatewayTypes(ArrayList allowedGatewayTypes) {
		_allowedGatewayTypes = allowedGatewayTypes;
	}

	public String getDomainAllowed() {
		return _domainAllowed;
	}

	public void setDomainAllowed(String domainAllowed) {
		_domainAllowed = domainAllowed;
	}

	public String getFixedDomains() {
		return _fixedDomains;
	}

	public void setFixedDomains(String fixedDomains) {
		_fixedDomains = fixedDomains;
	}

	public int getMaxTxnMsisdnInt() {
		return _maxTxnMsisdnInt;
	}

	public void setMaxTxnMsisdnInt(int maxTxnMsisdnInt) {
		_maxTxnMsisdnInt = maxTxnMsisdnInt;
	}

	public String getGrphDomainTypeName() {
		return _grphDomainTypeName;
	}

	public void setGrphDomainTypeName(String grphDomainTypeName) {
		_grphDomainTypeName = grphDomainTypeName;
	}

	public String getOutletsAllowed() {
		return _outletsAllowed;
	}

	public void setOutletsAllowed(String outletsAllowed) {
		_outletsAllowed = outletsAllowed;
	}

	public String getAgentAgentAllowed() {
		return _agentAgentAllowed;
	}

	public void setAgentAgentAllowed(String agentAgentAllowed) {
		_agentAgentAllowed = agentAgentAllowed;
	}

	public String getAgentAllowedFlag() {
		return _agentAllowedFlag;
	}

	public void setAgentAllowedFlag(String agentAllowedFlag) {
		_agentAllowedFlag = agentAllowedFlag;
	}

	public String getAgentCategoryCode() {
		return _agentCategoryCode;
	}

	public void setAgentCategoryCode(String agentCategoryCode) {
		_agentCategoryCode = agentCategoryCode;
	}

	public String getAgentCategoryName() {
		return _agentCategoryName;
	}

	public void setAgentCategoryName(String agentCategoryName) {
		_agentCategoryName = agentCategoryName;
	}

	public String getAgentCategoryStatus() {
		return _agentCategoryStatus;
	}

	public void setAgentCategoryStatus(String agentCategoryStatus) {
		_agentCategoryStatus = agentCategoryStatus;
	}

	public ArrayList getAgentCategoryStatusList() {
		return _agentCategoryStatusList;
	}

	public void setAgentCategoryStatusList(ArrayList agentCategoryStatusList) {
		_agentCategoryStatusList = agentCategoryStatusList;
	}

	public String getAgentCategoryType() {
		return _agentCategoryType;
	}

	public void setAgentCategoryType(String agentCategoryType) {
		_agentCategoryType = agentCategoryType;
	}

	public String[] getAgentCheckArray() {
		return _agentCheckArray;
	}

	public void setAgentCheckArray(String[] agentCheckArray) {
		_agentCheckArray = agentCheckArray;
	}

	public String getAgentDisplayAllowed() {
		return _agentDisplayAllowed;
	}

	public void setAgentDisplayAllowed(String agentDisplayAllowed) {
		_agentDisplayAllowed = agentDisplayAllowed;
	}

	public String getAgentDomainCodeforCategory() {
		return _agentDomainCodeforCategory;
	}

	public void setAgentDomainCodeforCategory(String agentDomainCodeforCategory) {
		_agentDomainCodeforCategory = agentDomainCodeforCategory;
	}

	public String getAgentDomainName() {
		return _agentDomainName;
	}

	public void setAgentDomainName(String agentDomainName) {
		_agentDomainName = agentDomainName;
	}

	public String getAgentFixedRoles() {
		return _agentFixedRoles;
	}

	public void setAgentFixedRoles(String agentFixedRoles) {
		_agentFixedRoles = agentFixedRoles;
	}

	public String getAgentGatewayName() {
		return _agentGatewayName;
	}

	public void setAgentGatewayName(String agentGatewayName) {
		_agentGatewayName = agentGatewayName;
	}

	public String getAgentGatewayType() {
		return _agentGatewayType;
	}

	public void setAgentGatewayType(String agentGatewayType) {
		_agentGatewayType = agentGatewayType;
	}

	public String getAgentGeographicalDomainList() {
		return _agentGeographicalDomainList;
	}

	public void setAgentGeographicalDomainList(String agentGeographicalDomainList) {
		_agentGeographicalDomainList = agentGeographicalDomainList;
	}

	public String getAgentGrphDomainType() {
		return _agentGrphDomainType;
	}

	public void setAgentGrphDomainType(String agentGrphDomainType) {
		_agentGrphDomainType = agentGrphDomainType;
	}

	public String getAgentHierarchyAllowed() {
		return _agentHierarchyAllowed;
	}

	public void setAgentHierarchyAllowed(String agentHierarchyAllowed) {
		_agentHierarchyAllowed = agentHierarchyAllowed;
	}

	public String getAgentMaxTxnMsisdn() {
		return _agentMaxTxnMsisdn;
	}

	public void setAgentMaxTxnMsisdn(String agentMaxTxnMsisdn) {
		_agentMaxTxnMsisdn = agentMaxTxnMsisdn;
	}

	public ArrayList getAgentMessageGatewayTypeList() {
		return _agentMessageGatewayTypeList;
	}

	public void setAgentMessageGatewayTypeList(ArrayList agentMessageGatewayTypeList) {
		_agentMessageGatewayTypeList = agentMessageGatewayTypeList;
	}

	public ArrayList getAgentModifiedMessageGatewayTypeList() {
		return _agentModifiedMessageGatewayTypeList;
	}

	public void setAgentModifiedMessageGatewayTypeList(ArrayList agentModifiedMessageGatewayTypeList) {
		_agentModifiedMessageGatewayTypeList = agentModifiedMessageGatewayTypeList;
	}

	public String getAgentModifyAllowed() {
		return _agentModifyAllowed;
	}

	public void setAgentModifyAllowed(String agentModifyAllowed) {
		_agentModifyAllowed = agentModifyAllowed;
	}

	public String getAgentMultipleGrphDomains() {
		return _agentMultipleGrphDomains;
	}

	public void setAgentMultipleGrphDomains(String agentMultipleGrphDomains) {
		_agentMultipleGrphDomains = agentMultipleGrphDomains;
	}

	public String getAgentMultipleLoginAllowed() {
		return _agentMultipleLoginAllowed;
	}

	public void setAgentMultipleLoginAllowed(String agentMultipleLoginAllowed) {
		_agentMultipleLoginAllowed = agentMultipleLoginAllowed;
	}

	public String getAgentOutletsAllowed() {
		return _agentOutletsAllowed;
	}

	public void setAgentOutletsAllowed(String agentOutletsAllowed) {
		_agentOutletsAllowed = agentOutletsAllowed;
	}

	public String getAgentProductTypeAssociationAllowed() {
		return _agentProductTypeAssociationAllowed;
	}

	public void setAgentProductTypeAssociationAllowed(String agentProductTypeAssociationAllowed) {
		_agentProductTypeAssociationAllowed = agentProductTypeAssociationAllowed;
	}

	public String getAgentRestrictedMsisdns() {
		return _agentRestrictedMsisdns;
	}

	public void setAgentRestrictedMsisdns(String agentRestrictedMsisdns) {
		_agentRestrictedMsisdns = agentRestrictedMsisdns;
	}

	public String getAgentRoleName() {
		return _agentRoleName;
	}

	public void setAgentRoleName(String agentRoleName) {
		_agentRoleName = agentRoleName;
	}

	public HashMap getAgentRolesMapSelected() {
		return _agentRolesMapSelected;
	}

	public void setAgentRolesMapSelected(HashMap agentRolesMapSelected) {
		_agentRolesMapSelected = agentRolesMapSelected;
	}

	public ArrayList getAgentRoleTypeList() {
		return _agentRoleTypeList;
	}

	public void setAgentRoleTypeList(ArrayList agentRoleTypeList) {
		_agentRoleTypeList = agentRoleTypeList;
	}

	public String getAgentScheduledTransferAllowed() {
		return _agentScheduledTransferAllowed;
	}

	public void setAgentScheduledTransferAllowed(String agentScheduledTransferAllowed) {
		_agentScheduledTransferAllowed = agentScheduledTransferAllowed;
	}

	public String getAgentServiceAllowed() {
		return _agentServiceAllowed;
	}

	public void setAgentServiceAllowed(String agentServiceAllowed) {
		_agentServiceAllowed = agentServiceAllowed;
	}

	public String getAgentUnctrlTransferAllowed() {
		return _agentUnctrlTransferAllowed;
	}

	public void setAgentUnctrlTransferAllowed(String agentUnctrlTransferAllowed) {
		_agentUnctrlTransferAllowed = agentUnctrlTransferAllowed;
	}

	public String getAgentUserIdPrefix() {
		return _agentUserIdPrefix;
	}

	public void setAgentUserIdPrefix(String agentUserIdPrefix) {
		_agentUserIdPrefix = agentUserIdPrefix;
	}

	public String getAgentViewOnNetworkBlock() {
		return _agentViewOnNetworkBlock;
	}

	public void setAgentViewOnNetworkBlock(String agentViewOnNetworkBlock) {
		_agentViewOnNetworkBlock = agentViewOnNetworkBlock;
	}

	public String getCategoryTypeCode() {
		return _categoryTypeCode;
	}

	public void setCategoryTypeCode(String categoryTypeCode) {
		_categoryTypeCode = categoryTypeCode;
	}

	public String getDomainName() {
		return _domainName;
	}

	public void setDomainName(String domainName) {
		_domainName = domainName;
	}

	public String getLowBalAlertAllow() {
		return _lowBalAlertAllow;
	}

	public void setLowBalAlertAllow(String lowBalAlertAllow) {
		_lowBalAlertAllow = lowBalAlertAllow;
	}

	public String getAgentLowBalAlertAllow() {
		return _agentLowBalAlertAllow;
	}

	public void setAgentLowBalAlertAllow(String agentLowBalAlertAllow) {
		_agentLowBalAlertAllow = agentLowBalAlertAllow;
	}

	public String getRechargeByParentOnly() {
		return _rechargeByParentOnly;
	}

	public void setRechargeByParentOnly(String byParentOnly) {
		_rechargeByParentOnly = byParentOnly;
	}

	public String getCp2pPayer() {
		return _cp2pPayer;
	}

	public void setCp2pPayer(String payer) {
		_cp2pPayer = payer;
	}

	public String getCp2pPayee() {
		return _cp2pPayee;
	}

	public void setCp2pPayee(String payee) {
		_cp2pPayee = payee;
	}

	public String getCp2pWithinList() {
		return _cp2pWithinList;
	}

	public void setCp2pWithinList(String withinList) {
		_cp2pWithinList = withinList;
	}

	public String getParentOrOwnerRadioValue() {
		return _parentOrOwnerRadioValue;
	}

	public void setParentOrOwnerRadioValue(String orOwnerRadioValue) {
		_parentOrOwnerRadioValue = orOwnerRadioValue;
	}

	public String getAgentRechargeByParentOnly() {
		return _agentRechargeByParentOnly;
	}

	public void setAgentRechargeByParentOnly(String rechargeByParentOnly) {
		_agentRechargeByParentOnly = rechargeByParentOnly;
	}

	public String getAgentCp2pPayer() {
		return _agentCp2pPayer;
	}

	public void setAgentCp2pPayer(String cp2pPayer) {
		_agentCp2pPayer = cp2pPayer;
	}

	public String getAgentCp2pPayee() {
		return _agentCp2pPayee;
	}

	public void setAgentCp2pPayee(String cp2pPayee) {
		_agentCp2pPayee = cp2pPayee;
	}

	public String getAgentCp2pWithinList() {
		return _agentCp2pWithinList;
	}

	public void setAgentCp2pWithinList(String cp2pWithinList) {
		_agentCp2pWithinList = cp2pWithinList;
	}

	public String getAgentParentOrOwnerRadioValue() {
		return _agentParentOrOwnerRadioValue;
	}

	public void setAgentParentOrOwnerRadioValue(String parentOrOwnerRadioValue) {
		_agentParentOrOwnerRadioValue = parentOrOwnerRadioValue;
	}

	public String getAuthenticationType() {
		return _authenticationType;
	}

	public void setAuthenticationType(String type) {
		_authenticationType = type;
	}

	public String getTrasnferKey() {
		return _categoryCode + ":" + _domainCodeforCategory;
	}

	public static CategoryVO getInstance() {
		return new CategoryVO();
	}
}
