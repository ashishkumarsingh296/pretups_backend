package com.selftopup.pretups.domain.businesslogic;

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
    public long _lastModifiedTime;
    public int _recordCount;
    public int _radioIndex;
    public String _productTypeAllowed;
    public String _domainTypeCode;
    public ArrayList _allowedGatewayTypes;
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

    /**
     * @return Returns the numberOfCategoryForDomain.
     */
    public int getNumberOfCategoryForDomain() {
        return this._numberOfCategoryForDomain;
    }

    /**
     * @param numberOfCategoryForDomain
     *            The numberOfCategoryForDomain to set.
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
     * @param categoryType
     *            The categoryType to set.
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
     * @param agentAllowed
     *            The agentAllowed to set.
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
     * @param hierarchyAllowed
     *            The hierarchyAllowed to set.
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
     * @param agentMaxLoginCount
     *            The agentMaxLoginCount to set.
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
     * @param agentSmsInterfaceAllowed
     *            The agentSmsInterfaceAllowed to set.
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
     * @param agentWebInterfaceAllowed
     *            The agentWebInterfaceAllowed to set.
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

    /**
     * To set the value of fixedRoles field
     */
    public void setFixedRoles(String fixedRoles) {
        _fixedRoles = fixedRoles;
    }

    /**
     * To get the value of grphDomainType field
     * 
     * @return grphDomainType.
     */
    public String getGrphDomainType() {
        return _grphDomainType;
    }

    /**
     * To set the value of grphDomainType field
     */
    public void setGrphDomainType(String grphDomainType) {
        _grphDomainType = grphDomainType;
    }

    /**
     * To get the value of maxTxnMsisdn field
     * 
     * @return maxTxnMsisdn.
     */
    public String getMaxTxnMsisdn() {
        return _maxTxnMsisdn;
    }

    /**
     * To set the value of maxTxnMsisdn field
     */
    public void setMaxTxnMsisdn(String maxTxnMsisdn) {
        _maxTxnMsisdn = maxTxnMsisdn;
    }

    /**
     * To get the value of modifyAllowed field
     * 
     * @return modifyAllowed.
     */
    public String getModifyAllowed() {
        return _modifyAllowed;
    }

    /**
     * To set the value of modifyAllowed field
     */
    public void setModifyAllowed(String modifyAllowed) {
        _modifyAllowed = modifyAllowed;
    }

    /**
     * To get the value of multipleGrphDomains field
     * 
     * @return multipleGrphDomains.
     */
    public String getMultipleGrphDomains() {
        return _multipleGrphDomains;
    }

    /**
     * To set the value of multipleGrphDomains field
     */
    public void setMultipleGrphDomains(String multipleGrphDomains) {
        _multipleGrphDomains = multipleGrphDomains;
    }

    /**
     * To get the value of multipleLoginAllowed field
     * 
     * @return multipleLoginAllowed.
     */
    public String getMultipleLoginAllowed() {
        return _multipleLoginAllowed;
    }

    /**
     * To set the value of multipleLoginAllowed field
     */
    public void setMultipleLoginAllowed(String multipleLoginAllowed) {
        _multipleLoginAllowed = multipleLoginAllowed;
    }

    /**
     * To get the value of parentCategoryCode field
     * 
     * @return parentCategoryCode.
     */
    public String getParentCategoryCode() {
        return _parentCategoryCode;
    }

    /**
     * To set the value of parentCategoryCode field
     */
    public void setParentCategoryCode(String parentCategoryCode) {
        _parentCategoryCode = parentCategoryCode;
    }

    /**
     * To get the value of restrictedMsisdns field
     * 
     * @return restrictedMsisdns.
     */
    public String getRestrictedMsisdns() {
        return _restrictedMsisdns;
    }

    /**
     * To set the value of restrictedMsisdns field
     */
    public void setRestrictedMsisdns(String restrictedMsisdns) {
        _restrictedMsisdns = restrictedMsisdns;
    }

    /**
     * To get the value of scheduledTransferAllowed field
     * 
     * @return scheduledTransferAllowed.
     */
    public String getScheduledTransferAllowed() {
        return _scheduledTransferAllowed;
    }

    /**
     * To set the value of scheduledTransferAllowed field
     */
    public void setScheduledTransferAllowed(String scheduledTransferAllowed) {
        _scheduledTransferAllowed = scheduledTransferAllowed;
    }

    /**
     * To get the value of sequenceNumber field
     * 
     * @return sequenceNumber.
     */
    public int getSequenceNumber() {
        return _sequenceNumber;
    }

    /**
     * To set the value of sequenceNumber field
     */
    public void setSequenceNumber(int sequenceNumber) {
        _sequenceNumber = sequenceNumber;
    }

    /**
     * To get the value of smsInterfaceAllowed field
     * 
     * @return smsInterfaceAllowed.
     */
    public String getSmsInterfaceAllowed() {
        return _smsInterfaceAllowed;
    }

    /**
     * To set the value of smsInterfaceAllowed field
     */
    public void setSmsInterfaceAllowed(String smsInterfaceAllowed) {
        _smsInterfaceAllowed = smsInterfaceAllowed;
    }

    /**
     * To get the value of unctrlTransferAllowed field
     * 
     * @return unctrlTransferAllowed.
     */
    public String getUnctrlTransferAllowed() {
        return _unctrlTransferAllowed;
    }

    /**
     * To set the value of unctrlTransferAllowed field
     */
    public void setUnctrlTransferAllowed(String unctrlTransferAllowed) {
        _unctrlTransferAllowed = unctrlTransferAllowed;
    }

    /**
     * To get the value of viewOnNetworkBlock field
     * 
     * @return viewOnNetworkBlock.
     */
    public String getViewOnNetworkBlock() {
        return _viewOnNetworkBlock;
    }

    /**
     * To set the value of viewOnNetworkBlock field
     */
    public void setViewOnNetworkBlock(String viewOnNetworkBlock) {
        _viewOnNetworkBlock = viewOnNetworkBlock;
    }

    /**
     * To get the value of webInterfaceAllowed field
     * 
     * @return webInterfaceAllowed.
     */
    public String getWebInterfaceAllowed() {
        return _webInterfaceAllowed;
    }

    /**
     * To set the value of webInterfaceAllowed field
     */
    public void setWebInterfaceAllowed(String webInterfaceAllowed) {
        _webInterfaceAllowed = webInterfaceAllowed;
    }

    /**
     * To get the value of txnOutsideHierchy field
     * 
     * @return txnOutsideHierchy.
     */
    public String getTxnOutsideHierchy() {
        return _txnOutsideHierchy;
    }

    /**
     * To set the value of txnOutsideHierchy field
     */
    public void setTxnOutsideHierchy(String txnOutsideHierchy) {
        _txnOutsideHierchy = txnOutsideHierchy;
    }

    public long getMaxLoginCount() {
        return _maxLoginCount;
    }

    public void setMaxLoginCount(long maxLoginCount) {
        _maxLoginCount = maxLoginCount;
    }

    /**
     * @return Returns the grphDomainSequenceNo.
     */
    public int getGrphDomainSequenceNo() {
        return _grphDomainSequenceNo;
    }

    /**
     * @param grphDomainSequenceNo
     *            The grphDomainSequenceNo to set.
     */
    public void setGrphDomainSequenceNo(int grphDomainSequenceNo) {
        _grphDomainSequenceNo = grphDomainSequenceNo;
    }

    public String getCombinedKey() {
        return _categoryCode + ":" + _domainCodeforCategory + ":" + _sequenceNumber;
    }

    /**
     * @return Returns the productTypeAssociationAllowed.
     */
    public String getProductTypeAssociationAllowed() {
        return _productTypeAssociationAllowed;
    }

    /**
     * @param productTypeAssociationAllowed
     *            The productTypeAssociationAllowed to set.
     */
    public void setProductTypeAssociationAllowed(String productTypeAssociationAllowed) {
        _productTypeAssociationAllowed = productTypeAssociationAllowed;
    }

    /**
     * @return Returns the productTypeAllowed.
     */
    public String getProductTypeAllowed() {
        return _productTypeAllowed;
    }

    /**
     * @param productTypeAllowed
     *            The productTypeAllowed to set.
     */
    public void setProductTypeAllowed(String productTypeAllowed) {
        _productTypeAllowed = productTypeAllowed;
    }

    /**
     * @return Returns the serviceAllowed.
     */
    public String getServiceAllowed() {
        return _serviceAllowed;
    }

    /**
     * @param serviceAllowed
     *            The serviceAllowed to set.
     */
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

    /**
     * @return Returns the domainAllowed.
     */
    public String getDomainAllowed() {
        return _domainAllowed;
    }

    /**
     * @param domainAllowed
     *            The domainAllowed to set.
     */
    public void setDomainAllowed(String domainAllowed) {
        _domainAllowed = domainAllowed;
    }

    /**
     * @return Returns the fixedDomains.
     */
    public String getFixedDomains() {
        return _fixedDomains;
    }

    /**
     * @param fixedDomains
     *            The fixedDomains to set.
     */
    public void setFixedDomains(String fixedDomains) {
        _fixedDomains = fixedDomains;
    }

    public int getMaxTxnMsisdnInt() {
        return _maxTxnMsisdnInt;
    }

    public void setMaxTxnMsisdnInt(int maxTxnMsisdnInt) {
        _maxTxnMsisdnInt = maxTxnMsisdnInt;
    }

    /**
     * @return Returns the grphDomainTypeName.
     */
    public String getGrphDomainTypeName() {
        return _grphDomainTypeName;
    }

    /**
     * @param grphDomainTypeName
     *            The grphDomainTypeName to set.
     */
    public void setGrphDomainTypeName(String grphDomainTypeName) {
        _grphDomainTypeName = grphDomainTypeName;
    }

    /**
     * @return Returns the outletsAllowed.
     */
    public String getOutletsAllowed() {
        return _outletsAllowed;
    }

    /**
     * @param outletsAllowed
     *            The outletsAllowed to set.
     */
    public void setOutletsAllowed(String outletsAllowed) {
        _outletsAllowed = outletsAllowed;
    }

    // for agent category seter gater

    /**
     * @return Returns the agentAgentAllowed.
     */
    public String getAgentAgentAllowed() {
        return _agentAgentAllowed;
    }

    /**
     * @param agentAgentAllowed
     *            The agentAgentAllowed to set.
     */
    public void setAgentAgentAllowed(String agentAgentAllowed) {
        _agentAgentAllowed = agentAgentAllowed;
    }

    /**
     * @return Returns the agentAllowedFlag.
     */
    public String getAgentAllowedFlag() {
        return _agentAllowedFlag;
    }

    /**
     * @param agentAllowedFlag
     *            The agentAllowedFlag to set.
     */
    public void setAgentAllowedFlag(String agentAllowedFlag) {
        _agentAllowedFlag = agentAllowedFlag;
    }

    /**
     * @return Returns the agentCategoryCode.
     */
    public String getAgentCategoryCode() {
        return _agentCategoryCode;
    }

    /**
     * @param agentCategoryCode
     *            The agentCategoryCode to set.
     */
    public void setAgentCategoryCode(String agentCategoryCode) {
        _agentCategoryCode = agentCategoryCode;
    }

    /**
     * @return Returns the agentCategoryName.
     */
    public String getAgentCategoryName() {
        return _agentCategoryName;
    }

    /**
     * @param agentCategoryName
     *            The agentCategoryName to set.
     */
    public void setAgentCategoryName(String agentCategoryName) {
        _agentCategoryName = agentCategoryName;
    }

    /**
     * @return Returns the agentCategoryStatus.
     */
    public String getAgentCategoryStatus() {
        return _agentCategoryStatus;
    }

    /**
     * @param agentCategoryStatus
     *            The agentCategoryStatus to set.
     */
    public void setAgentCategoryStatus(String agentCategoryStatus) {
        _agentCategoryStatus = agentCategoryStatus;
    }

    /**
     * @return Returns the agentCategoryStatusList.
     */
    public ArrayList getAgentCategoryStatusList() {
        return _agentCategoryStatusList;
    }

    /**
     * @param agentCategoryStatusList
     *            The agentCategoryStatusList to set.
     */
    public void setAgentCategoryStatusList(ArrayList agentCategoryStatusList) {
        _agentCategoryStatusList = agentCategoryStatusList;
    }

    /**
     * @return Returns the agentCategoryType.
     */
    public String getAgentCategoryType() {
        return _agentCategoryType;
    }

    /**
     * @param agentCategoryType
     *            The agentCategoryType to set.
     */
    public void setAgentCategoryType(String agentCategoryType) {
        _agentCategoryType = agentCategoryType;
    }

    /**
     * @return Returns the agentCheckArray.
     */
    public String[] getAgentCheckArray() {
        return _agentCheckArray;
    }

    /**
     * @param agentCheckArray
     *            The agentCheckArray to set.
     */
    public void setAgentCheckArray(String[] agentCheckArray) {
        _agentCheckArray = agentCheckArray;
    }

    /**
     * @return Returns the agentDisplayAllowed.
     */
    public String getAgentDisplayAllowed() {
        return _agentDisplayAllowed;
    }

    /**
     * @param agentDisplayAllowed
     *            The agentDisplayAllowed to set.
     */
    public void setAgentDisplayAllowed(String agentDisplayAllowed) {
        _agentDisplayAllowed = agentDisplayAllowed;
    }

    /**
     * @return Returns the agentDomainCodeforCategory.
     */
    public String getAgentDomainCodeforCategory() {
        return _agentDomainCodeforCategory;
    }

    /**
     * @param agentDomainCodeforCategory
     *            The agentDomainCodeforCategory to set.
     */
    public void setAgentDomainCodeforCategory(String agentDomainCodeforCategory) {
        _agentDomainCodeforCategory = agentDomainCodeforCategory;
    }

    /**
     * @return Returns the agentDomainName.
     */
    public String getAgentDomainName() {
        return _agentDomainName;
    }

    /**
     * @param agentDomainName
     *            The agentDomainName to set.
     */
    public void setAgentDomainName(String agentDomainName) {
        _agentDomainName = agentDomainName;
    }

    /**
     * @return Returns the agentFixedRoles.
     */
    public String getAgentFixedRoles() {
        return _agentFixedRoles;
    }

    /**
     * @param agentFixedRoles
     *            The agentFixedRoles to set.
     */
    public void setAgentFixedRoles(String agentFixedRoles) {
        _agentFixedRoles = agentFixedRoles;
    }

    /**
     * @return Returns the agentGatewayName.
     */
    public String getAgentGatewayName() {
        return _agentGatewayName;
    }

    /**
     * @param agentGatewayName
     *            The agentGatewayName to set.
     */
    public void setAgentGatewayName(String agentGatewayName) {
        _agentGatewayName = agentGatewayName;
    }

    /**
     * @return Returns the agentGatewayType.
     */
    public String getAgentGatewayType() {
        return _agentGatewayType;
    }

    /**
     * @param agentGatewayType
     *            The agentGatewayType to set.
     */
    public void setAgentGatewayType(String agentGatewayType) {
        _agentGatewayType = agentGatewayType;
    }

    /**
     * @return Returns the agentGeographicalDomainList.
     */
    public String getAgentGeographicalDomainList() {
        return _agentGeographicalDomainList;
    }

    /**
     * @param agentGeographicalDomainList
     *            The agentGeographicalDomainList to set.
     */
    public void setAgentGeographicalDomainList(String agentGeographicalDomainList) {
        _agentGeographicalDomainList = agentGeographicalDomainList;
    }

    /**
     * @return Returns the agentGrphDomainType.
     */
    public String getAgentGrphDomainType() {
        return _agentGrphDomainType;
    }

    /**
     * @param agentGrphDomainType
     *            The agentGrphDomainType to set.
     */
    public void setAgentGrphDomainType(String agentGrphDomainType) {
        _agentGrphDomainType = agentGrphDomainType;
    }

    /**
     * @return Returns the agentHierarchyAllowed.
     */
    public String getAgentHierarchyAllowed() {
        return _agentHierarchyAllowed;
    }

    /**
     * @param agentHierarchyAllowed
     *            The agentHierarchyAllowed to set.
     */
    public void setAgentHierarchyAllowed(String agentHierarchyAllowed) {
        _agentHierarchyAllowed = agentHierarchyAllowed;
    }

    /**
     * @return Returns the agentMaxTxnMsisdn.
     */
    public String getAgentMaxTxnMsisdn() {
        return _agentMaxTxnMsisdn;
    }

    /**
     * @param agentMaxTxnMsisdn
     *            The agentMaxTxnMsisdn to set.
     */
    public void setAgentMaxTxnMsisdn(String agentMaxTxnMsisdn) {
        _agentMaxTxnMsisdn = agentMaxTxnMsisdn;
    }

    /**
     * @return Returns the agentMessageGatewayTypeList.
     */
    public ArrayList getAgentMessageGatewayTypeList() {
        return _agentMessageGatewayTypeList;
    }

    /**
     * @param agentMessageGatewayTypeList
     *            The agentMessageGatewayTypeList to set.
     */
    public void setAgentMessageGatewayTypeList(ArrayList agentMessageGatewayTypeList) {
        _agentMessageGatewayTypeList = agentMessageGatewayTypeList;
    }

    /**
     * @return Returns the agentModifiedMessageGatewayTypeList.
     */
    public ArrayList getAgentModifiedMessageGatewayTypeList() {
        return _agentModifiedMessageGatewayTypeList;
    }

    /**
     * @param agentModifiedMessageGatewayTypeList
     *            The agentModifiedMessageGatewayTypeList to set.
     */
    public void setAgentModifiedMessageGatewayTypeList(ArrayList agentModifiedMessageGatewayTypeList) {
        _agentModifiedMessageGatewayTypeList = agentModifiedMessageGatewayTypeList;
    }

    /**
     * @return Returns the agentModifyAllowed.
     */
    public String getAgentModifyAllowed() {
        return _agentModifyAllowed;
    }

    /**
     * @param agentModifyAllowed
     *            The agentModifyAllowed to set.
     */
    public void setAgentModifyAllowed(String agentModifyAllowed) {
        _agentModifyAllowed = agentModifyAllowed;
    }

    /**
     * @return Returns the agentMultipleGrphDomains.
     */
    public String getAgentMultipleGrphDomains() {
        return _agentMultipleGrphDomains;
    }

    /**
     * @param agentMultipleGrphDomains
     *            The agentMultipleGrphDomains to set.
     */
    public void setAgentMultipleGrphDomains(String agentMultipleGrphDomains) {
        _agentMultipleGrphDomains = agentMultipleGrphDomains;
    }

    /**
     * @return Returns the agentMultipleLoginAllowed.
     */
    public String getAgentMultipleLoginAllowed() {
        return _agentMultipleLoginAllowed;
    }

    /**
     * @param agentMultipleLoginAllowed
     *            The agentMultipleLoginAllowed to set.
     */
    public void setAgentMultipleLoginAllowed(String agentMultipleLoginAllowed) {
        _agentMultipleLoginAllowed = agentMultipleLoginAllowed;
    }

    /**
     * @return Returns the agentOutletsAllowed.
     */
    public String getAgentOutletsAllowed() {
        return _agentOutletsAllowed;
    }

    /**
     * @param agentOutletsAllowed
     *            The agentOutletsAllowed to set.
     */
    public void setAgentOutletsAllowed(String agentOutletsAllowed) {
        _agentOutletsAllowed = agentOutletsAllowed;
    }

    /**
     * @return Returns the agentProductTypeAssociationAllowed.
     */
    public String getAgentProductTypeAssociationAllowed() {
        return _agentProductTypeAssociationAllowed;
    }

    /**
     * @param agentProductTypeAssociationAllowed
     *            The agentProductTypeAssociationAllowed to set.
     */
    public void setAgentProductTypeAssociationAllowed(String agentProductTypeAssociationAllowed) {
        _agentProductTypeAssociationAllowed = agentProductTypeAssociationAllowed;
    }

    /**
     * @return Returns the agentRestrictedMsisdns.
     */
    public String getAgentRestrictedMsisdns() {
        return _agentRestrictedMsisdns;
    }

    /**
     * @param agentRestrictedMsisdns
     *            The agentRestrictedMsisdns to set.
     */
    public void setAgentRestrictedMsisdns(String agentRestrictedMsisdns) {
        _agentRestrictedMsisdns = agentRestrictedMsisdns;
    }

    /**
     * @return Returns the agentRoleName.
     */
    public String getAgentRoleName() {
        return _agentRoleName;
    }

    /**
     * @param agentRoleName
     *            The agentRoleName to set.
     */
    public void setAgentRoleName(String agentRoleName) {
        _agentRoleName = agentRoleName;
    }

    /**
     * @return Returns the agentRolesMapSelected.
     */
    public HashMap getAgentRolesMapSelected() {
        return _agentRolesMapSelected;
    }

    /**
     * @param agentRolesMapSelected
     *            The agentRolesMapSelected to set.
     */
    public void setAgentRolesMapSelected(HashMap agentRolesMapSelected) {
        _agentRolesMapSelected = agentRolesMapSelected;
    }

    /**
     * @return Returns the agentRoleTypeList.
     */
    public ArrayList getAgentRoleTypeList() {
        return _agentRoleTypeList;
    }

    /**
     * @param agentRoleTypeList
     *            The agentRoleTypeList to set.
     */
    public void setAgentRoleTypeList(ArrayList agentRoleTypeList) {
        _agentRoleTypeList = agentRoleTypeList;
    }

    /**
     * @return Returns the agentScheduledTransferAllowed.
     */
    public String getAgentScheduledTransferAllowed() {
        return _agentScheduledTransferAllowed;
    }

    /**
     * @param agentScheduledTransferAllowed
     *            The agentScheduledTransferAllowed to set.
     */
    public void setAgentScheduledTransferAllowed(String agentScheduledTransferAllowed) {
        _agentScheduledTransferAllowed = agentScheduledTransferAllowed;
    }

    /**
     * @return Returns the agentServiceAllowed.
     */
    public String getAgentServiceAllowed() {
        return _agentServiceAllowed;
    }

    /**
     * @param agentServiceAllowed
     *            The agentServiceAllowed to set.
     */
    public void setAgentServiceAllowed(String agentServiceAllowed) {
        _agentServiceAllowed = agentServiceAllowed;
    }

    /**
     * @return Returns the agentUnctrlTransferAllowed.
     */
    public String getAgentUnctrlTransferAllowed() {
        return _agentUnctrlTransferAllowed;
    }

    /**
     * @param agentUnctrlTransferAllowed
     *            The agentUnctrlTransferAllowed to set.
     */
    public void setAgentUnctrlTransferAllowed(String agentUnctrlTransferAllowed) {
        _agentUnctrlTransferAllowed = agentUnctrlTransferAllowed;
    }

    /**
     * @return Returns the agentUserIdPrefix.
     */
    public String getAgentUserIdPrefix() {
        return _agentUserIdPrefix;
    }

    /**
     * @param agentUserIdPrefix
     *            The agentUserIdPrefix to set.
     */
    public void setAgentUserIdPrefix(String agentUserIdPrefix) {
        _agentUserIdPrefix = agentUserIdPrefix;
    }

    /**
     * @return Returns the agentViewOnNetworkBlock.
     */
    public String getAgentViewOnNetworkBlock() {
        return _agentViewOnNetworkBlock;
    }

    /**
     * @param agentViewOnNetworkBlock
     *            The agentViewOnNetworkBlock to set.
     */
    public void setAgentViewOnNetworkBlock(String agentViewOnNetworkBlock) {
        _agentViewOnNetworkBlock = agentViewOnNetworkBlock;
    }

    /**
     * @return Returns the categoryTypeCode.
     */
    public String getCategoryTypeCode() {
        return _categoryTypeCode;
    }

    /**
     * @param categoryTypeCode
     *            The categoryTypeCode to set.
     */
    public void setCategoryTypeCode(String categoryTypeCode) {
        _categoryTypeCode = categoryTypeCode;
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    /**
     * @return Returns the lowBalAlertAllow.
     */
    public String getLowBalAlertAllow() {
        return _lowBalAlertAllow;
    }

    /**
     * @param lowBalAlertAllow
     *            The lowBalAlertAllow to set.
     */
    public void setLowBalAlertAllow(String lowBalAlertAllow) {
        _lowBalAlertAllow = lowBalAlertAllow;
    }

    // Added on 12/07/07 for Low balance alert allow
    /**
     * @return Returns the _agentLowBalAlertAllow.
     */
    public String getAgentLowBalAlertAllow() {
        return _agentLowBalAlertAllow;
    }

    /**
     * @param balAlertAllow
     *            The _agentLowBalAlertAllow.
     */
    public void setAgentLowBalAlertAllow(String agentLowBalAlertAllow) {
        _agentLowBalAlertAllow = agentLowBalAlertAllow;
    }

    /**
     * @return Returns the _rechargeByParentOnly.
     */
    public String getRechargeByParentOnly() {
        return _rechargeByParentOnly;
    }

    /**
     * @param byParentOnly
     *            The _rechargeByParentOnly to set.
     */
    public void setRechargeByParentOnly(String byParentOnly) {
        _rechargeByParentOnly = byParentOnly;
    }

    /**
     * @return Returns the _cp2pPayer.
     */
    public String getCp2pPayer() {
        return _cp2pPayer;
    }

    /**
     * @param payer
     *            The _cp2pPayer to set.
     */
    public void setCp2pPayer(String payer) {
        _cp2pPayer = payer;
    }

    /**
     * @return Returns the _cp2pPayee.
     */
    public String getCp2pPayee() {
        return _cp2pPayee;
    }

    /**
     * @param payee
     *            The _cp2pPayee to set.
     */
    public void setCp2pPayee(String payee) {
        _cp2pPayee = payee;
    }

    /**
     * @return Returns the _cp2pWithinList.
     */
    public String getCp2pWithinList() {
        return _cp2pWithinList;
    }

    /**
     * @param withinList
     *            The _cp2pWithinList to set.
     */
    public void setCp2pWithinList(String withinList) {
        _cp2pWithinList = withinList;
    }

    /**
     * @return Returns the _parentOrOwnerRadioValue.
     */
    public String getParentOrOwnerRadioValue() {
        return _parentOrOwnerRadioValue;
    }

    /**
     * @param orOwnerRadioValue
     *            The _parentOrOwnerRadioValue to set.
     */
    public void setParentOrOwnerRadioValue(String orOwnerRadioValue) {
        _parentOrOwnerRadioValue = orOwnerRadioValue;
    }

    /**
     * @return Returns the _agentRechargeByParentOnly.
     */
    public String getAgentRechargeByParentOnly() {
        return _agentRechargeByParentOnly;
    }

    /**
     * @param rechargeByParentOnly
     *            The _agentRechargeByParentOnly to set.
     */
    public void setAgentRechargeByParentOnly(String rechargeByParentOnly) {
        _agentRechargeByParentOnly = rechargeByParentOnly;
    }

    /**
     * @return Returns the _agentCp2pPayer.
     */
    public String getAgentCp2pPayer() {
        return _agentCp2pPayer;
    }

    /**
     * @param cp2pPayer
     *            The _agentCp2pPayer to set.
     */
    public void setAgentCp2pPayer(String cp2pPayer) {
        _agentCp2pPayer = cp2pPayer;
    }

    /**
     * @return Returns the _agentCp2pPayee.
     */
    public String getAgentCp2pPayee() {
        return _agentCp2pPayee;
    }

    /**
     * @param cp2pPayee
     *            The _agentCp2pPayee to set.
     */
    public void setAgentCp2pPayee(String cp2pPayee) {
        _agentCp2pPayee = cp2pPayee;
    }

    /**
     * @return Returns the _agentCp2pWithinList.
     */
    public String getAgentCp2pWithinList() {
        return _agentCp2pWithinList;
    }

    /**
     * @param cp2pWithinList
     *            The _agentCp2pWithinList to set.
     */
    public void setAgentCp2pWithinList(String cp2pWithinList) {
        _agentCp2pWithinList = cp2pWithinList;
    }

    /**
     * @return Returns the _agentParentOrOwnerRadioValue.
     */
    public String getAgentParentOrOwnerRadioValue() {
        return _agentParentOrOwnerRadioValue;
    }

    /**
     * @param parentOrOwnerRadioValue
     *            The _agentParentOrOwnerRadioValue to set.
     */
    public void setAgentParentOrOwnerRadioValue(String parentOrOwnerRadioValue) {
        _agentParentOrOwnerRadioValue = parentOrOwnerRadioValue;
    }
}
