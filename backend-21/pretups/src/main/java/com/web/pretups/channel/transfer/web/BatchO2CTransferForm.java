/**
 * @(#)O2CBatchForm.java
 *                       Copyright(c) 2012, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       02/02/2012 Initial Creation
 * 
 */

package com.web.pretups.channel.transfer.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;


import com.btsl.util.BTSLUtil;

public class BatchO2CTransferForm {
    private String _batchId = null;
    private String _networkCode = null;
    private String _networkCodeFor = null;
    private String _batchName = null;
    private String _status = null;
    private String _domainCode = null;
    private String _productCode = null;
    private String _productName = null;
    private ArrayList _productList = null;
    private String _batchFileName = null;
    private String _batchTotalRecord = null;
    private Date _batchDate = null;
    private String _createdBy = null;
    private Date _createdOn = null;
    private String _modifiedBy = null;
    private Date _modifiedOn = null;
    private String _batchDetailId = null;
    private String _categoryCode = null;
    private String _msisdn = null;
    private String _userId = null;
    private String _userGradeCode = null;
    private String _referenceNo = null;
    private String _extTxnNo = null;
    private Date _extTxnDate = null;
    private Date _transferDate = null;
    private String _txnProfile = null;
    private String _requestedQuantity = null;
    private String _transferMrp = null;
    private String _initiatorRemarks = null;
    private String _firstApproverRemarks = null;
    private String _secondApproverRemarks = null;
    private String _firstApprovedBy = null;
    private String _firstApprovedOn = null;
    private String _secondApprovedBy = null;
    private String _secondApprovedOn = null;
    private String _cancelledBy = null;
    private String _cancelledOn = null;
    private String _rcrdStatus = null;
    private String _geographyCode = null;
    private String _geographicalDomainCode = null;
    private String _geographicalDomainCodeDesc = null;
    private ArrayList _geographicalDomainList = null;
    private String _domainCodeDesc = null;
    private ArrayList _domainList = null;
    private ArrayList _categoryList = null;
    private String _categoryName = null;
    private String _productTypeCode = null;
    private String _productTypeCodeDesc = null;
    private ArrayList _productTypeList = null;
    //private FormFile _file;
    private String _productShortCode = null;
    private String _requestType = null;
    private String _externalTxnExist = null;
    private String _externalTxnMandatory = null;
    private int _o2cOrderApprovalLevel = 0;
    private int _totalRecords = 0;
    private int _newRecords = 0;
    private int _level1ApprovedRecords = 0;
    private int _level2ApprovedRecords = 0;
    private int _rejectedRecords = 0;
    private int _closedRecords = 0;
    private int _selectedIndex = 0;
    private long _productMrp;
    private String _productMrpStr = null;
    private String _fromDate = null;
    private String _toDate = null;
    private String _fromDateMsisdn = null;
    private String _toDateMsisdn = null;
    private ArrayList _batchList = null;
    private String _searchType = null;
    private ArrayList _errorList = null;
    private String _noOfRecords = null;
    private String _processedRecs = null;
    private String _viewErrorLog = null;
    private LinkedHashMap _downLoadDataMap = null;
    private ArrayList _o2cBatchItemVOList = null;
    private Date _commPrfApplicableDate;
    private boolean _errorFlag;
    private String _domainTypeCode = null;
    private String _concatStr = null;
    private String _pageOffset = null;
    // fields to store o2c notification message
    private String _defaultLang = null;
    private String _secondLang = null;
    private Map _batchO2CItems;

    // For PIN Authentication in O2C Transactions- 28/04/15.
    private boolean _showPin = false;
    private String _smsPin = null;
    private String _displayMsisdn = null;
    private String _displayPin = null;
    // For CAPTCHA
    private String j_captcha_response = null;

    public Map getBatchO2CItems() {
        return _batchO2CItems;
    }

    public void setBatchO2CItems(Map batchO2CItems) {
        _batchO2CItems = batchO2CItems;
    }

    /**
     * @return Returns the defaultLang.
     */
    public String getDefaultLang() {
        return _defaultLang;
    }

    /**
     * @param defaultLang
     *            The defaultLang to set.
     */
    public void setDefaultLang(String defaultLang) {
        _defaultLang = defaultLang;
    }

    /**
     * @return Returns the secondLang.
     */
    public String getSecondLang() {
        return _secondLang;
    }

    /**
     * @param secondLang
     *            The secondLang to set.
     */
    public void setSecondLang(String secondLang) {
        _secondLang = secondLang;
    }

    /**
     * @return Returns the concatStr.
     */
    public String getConcatStr() {
        return _concatStr;
    }

    /**
     * @param concatStr
     *            The concatStr to set.
     */
    public void setConcatStr(String concatStr) {
        _concatStr = concatStr;
    }

    /**
     * @return Returns the errorFlag.
     */
    public boolean getErrorFlag() {
        return _errorFlag;
    }

    /**
     * @param errorFlag
     *            The errorFlag to set.
     */
    public void setErrorFlag(boolean errorFlag) {
        _errorFlag = errorFlag;
    }

    /**
     * @return Returns the commPrfApplicableDate.
     */
    public Date getCommPrfApplicableDate() {
        return _commPrfApplicableDate;
    }

    /**
     * @param commPrfApplicableDate
     *            The commPrfApplicableDate to set.
     */
    public void setCommPrfApplicableDate(Date commPrfApplicableDate) {
        _commPrfApplicableDate = commPrfApplicableDate;
    }

    /**
     * @return Returns the focBatchItemVOList.
     */
    public ArrayList getO2CBatchItemVOList() {
        return _o2cBatchItemVOList;
    }

    /**
     * @param focBatchItemVOList
     *            The focBatchItemVOList to set.
     */
    public void setO2CBatchItemVOList(ArrayList o2cBatchItemVOList) {
        _o2cBatchItemVOList = o2cBatchItemVOList;
    }

    /**
     * @return Returns the downLoadDataMap.
     */
    public LinkedHashMap getDownLoadDataMap() {
        return _downLoadDataMap;
    }

    /**
     * @param downLoadDataMap
     *            The downLoadDataMap to set.
     */
    public void setDownLoadDataMap(LinkedHashMap downLoadDataMap) {
        _downLoadDataMap = downLoadDataMap;
    }

    /**
     * @return Returns the searchType.
     */
    public String getSearchType() {
        return _searchType;
    }

    /**
     * @param searchType
     *            The searchType to set.
     */
    public void setSearchType(String searchType) {
        _searchType = searchType;
    }

    /**
     * @return Returns the batchList.
     */
    public ArrayList getBatchList() {
        return _batchList;
    }

    /**
     * @param batchList
     *            The batchList to set.
     */
    public void setBatchList(ArrayList batchList) {
        _batchList = batchList;
    }

    public int getSizeOfBatchList() {
        if (_batchList != null) {
            return _batchList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the productShortCode.
     */
    public String getProductShortCode() {
        return _productShortCode;
    }

    /**
     * @param productShortCode
     *            The productShortCode to set.
     */
    public void setProductShortCode(String productShortCode) {
        _productShortCode = productShortCode;
    }

    /**
     * @return Returns the categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param categoryName
     *            The categoryName to set.
     */
    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    private ArrayList _o2cBatchMasterVOList = null;

    /**
     * @return Returns the o2cBatchMasterVOList.
     */
    public ArrayList getO2CBatchMasterVOList() {
        return _o2cBatchMasterVOList;
    }

    /**
     * @param o2cBatchMasterVOList
     *            The o2cBatchMasterVOList to set.
     */
    public void setO2CBatchMasterVOList(ArrayList o2cBatchMasterVOList) {
        _o2cBatchMasterVOList = o2cBatchMasterVOList;
    }


    /**
     * @return Returns the productTypeCode.
     */
    public String getProductTypeCode() {
        return _productTypeCode;
    }

    /**
     * @param productTypeCode
     *            The productTypeCode to set.
     */
    public void setProductTypeCode(String productTypeCode) {
        _productTypeCode = productTypeCode;
    }

    /**
     * @return Returns the productTypeCodeDesc.
     */
    public String getProductTypeCodeDesc() {
        return _productTypeCodeDesc;
    }

    /**
     * @param productTypeCodeDesc
     *            The productTypeCodeDesc to set.
     */
    public void setProductTypeCodeDesc(String productTypeCodeDesc) {
        _productTypeCodeDesc = productTypeCodeDesc;
    }

    /**
     * @return Returns the productTypeList.
     */
    public ArrayList getProductTypeList() {
        return _productTypeList;
    }

    /**
     * @param productTypeList
     *            The productTypeList to set.
     */
    public void setProductTypeList(ArrayList productTypeList) {
        _productTypeList = productTypeList;
    }

    public int getSizeOfProductTypeList() {
        if (_productTypeList != null) {
            return _productTypeList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the batchDate.
     */
    public Date getBatchDate() {
        return _batchDate;
    }

    /**
     * @param batchDate
     *            The batchDate to set.
     */
    public void setBatchDate(Date batchDate) {
        _batchDate = batchDate;
    }

    /**
     * @return Returns the batchDetailId.
     */
    public String getBatchDetailId() {
        return _batchDetailId;
    }

    /**
     * @param batchDetailId
     *            The batchDetailId to set.
     */
    public void setBatchDetailId(String batchDetailId) {
        _batchDetailId = batchDetailId;
    }

    /**
     * @return Returns the batchFileName.
     */
    public String getBatchFileName() {
        return _batchFileName;
    }

    /**
     * @param batchFileName
     *            The batchFileName to set.
     */
    public void setBatchFileName(String batchFileName) {
        _batchFileName = batchFileName;
    }

    /**
     * @return Returns the batchId.
     */
    public String getBatchId() {
        return _batchId;
    }

    /**
     * @param batchId
     *            The batchId to set.
     */
    public void setBatchId(String batchId) {
        _batchId = batchId;
    }

    /**
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return _batchName;
    }

    /**
     * @param batchName
     *            The batchName to set.
     */
    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    /**
     * @return Returns the batchTotalRecord.
     */
    public String getBatchTotalRecord() {
        return _batchTotalRecord;
    }

    /**
     * @param batchTotalRecord
     *            The batchTotalRecord to set.
     */
    public void setBatchTotalRecord(String batchTotalRecord) {
        _batchTotalRecord = batchTotalRecord;
    }

    /**
     * @return Returns the cancelledBy.
     */
    public String getCancelledBy() {
        return _cancelledBy;
    }

    /**
     * @param cancelledBy
     *            The cancelledBy to set.
     */
    public void setCancelledBy(String cancelledBy) {
        _cancelledBy = cancelledBy;
    }

    /**
     * @return Returns the cancelledOn.
     */
    public String getCancelledOn() {
        return _cancelledOn;
    }

    /**
     * @param cancelledOn
     *            The cancelledOn to set.
     */
    public void setCancelledOn(String cancelledOn) {
        _cancelledOn = cancelledOn;
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the domainCode.
     */
    public String getDomainCode() {
        return _domainCode;
    }

    /**
     * @param domainCode
     *            The domainCode to set.
     */
    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    /**
     * @return Returns the extTxnDate.
     */
    public Date getExtTxnDate() {
        return _extTxnDate;
    }

    /**
     * @param extTxnDate
     *            The extTxnDate to set.
     */
    public void setExtTxnDate(Date extTxnDate) {
        _extTxnDate = extTxnDate;
    }

    /**
     * @return Returns the extTxnNo.
     */
    public String getExtTxnNo() {
        return _extTxnNo;
    }

    /**
     * @param extTxnNo
     *            The extTxnNo to set.
     */
    public void setExtTxnNo(String extTxnNo) {
        _extTxnNo = extTxnNo;
    }

    /**
     * @return Returns the firstApprovedBy.
     */
    public String getFirstApprovedBy() {
        return _firstApprovedBy;
    }

    /**
     * @param firstApprovedBy
     *            The firstApprovedBy to set.
     */
    public void setFirstApprovedBy(String firstApprovedBy) {
        _firstApprovedBy = firstApprovedBy;
    }

    /**
     * @return Returns the firstApprovedOn.
     */
    public String getFirstApprovedOn() {
        return _firstApprovedOn;
    }

    /**
     * @param firstApprovedOn
     *            The firstApprovedOn to set.
     */
    public void setFirstApprovedOn(String firstApprovedOn) {
        _firstApprovedOn = firstApprovedOn;
    }

    /**
     * @return Returns the firstApproverRemarks.
     */
    public String getFirstApproverRemarks() {
        return _firstApproverRemarks;
    }

    /**
     * @param firstApproverRemarks
     *            The firstApproverRemarks to set.
     */
    public void setFirstApproverRemarks(String firstApproverRemarks) {
        _firstApproverRemarks = firstApproverRemarks;
    }

    /**
     * @return Returns the geographyCode.
     */
    public String getGeographyCode() {
        return _geographyCode;
    }

    /**
     * @param geographyCode
     *            The geographyCode to set.
     */
    public void setGeographyCode(String geographyCode) {
        _geographyCode = geographyCode;
    }

    /**
     * @return Returns the initiatorRemarks.
     */
    public String getInitiatorRemarks() {
        return _initiatorRemarks;
    }

    /**
     * @param initiatorRemarks
     *            The initiatorRemarks to set.
     */
    public void setInitiatorRemarks(String initiatorRemarks) {
        _initiatorRemarks = initiatorRemarks;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the networkCodeFor.
     */
    public String getNetworkCodeFor() {
        return _networkCodeFor;
    }

    /**
     * @param networkCodeFor
     *            The networkCodeFor to set.
     */
    public void setNetworkCodeFor(String networkCodeFor) {
        _networkCodeFor = networkCodeFor;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    /**
     * @return Returns the rcrdStatus.
     */
    public String getRcrdStatus() {
        return _rcrdStatus;
    }

    /**
     * @param rcrdStatus
     *            The rcrdStatus to set.
     */
    public void setRcrdStatus(String rcrdStatus) {
        _rcrdStatus = rcrdStatus;
    }

    /**
     * @return Returns the referenceNo.
     */
    public String getReferenceNo() {
        return _referenceNo;
    }

    /**
     * @param referenceNo
     *            The referenceNo to set.
     */
    public void setReferenceNo(String referenceNo) {
        _referenceNo = referenceNo;
    }

    /**
     * @return Returns the requestedQuantity.
     */
    public String getRequestedQuantity() {
        return _requestedQuantity;
    }

    /**
     * @param requestedQuantity
     *            The requestedQuantity to set.
     */
    public void setRequestedQuantity(String requestedQuantity) {
        _requestedQuantity = requestedQuantity;
    }

    /**
     * @return Returns the secondApprovedBy.
     */
    public String getSecondApprovedBy() {
        return _secondApprovedBy;
    }

    /**
     * @param secondApprovedBy
     *            The secondApprovedBy to set.
     */
    public void setSecondApprovedBy(String secondApprovedBy) {
        _secondApprovedBy = secondApprovedBy;
    }

    /**
     * @return Returns the secondApprovedOn.
     */
    public String getSecondApprovedOn() {
        return _secondApprovedOn;
    }

    /**
     * @param secondApprovedOn
     *            The secondApprovedOn to set.
     */
    public void setSecondApprovedOn(String secondApprovedOn) {
        _secondApprovedOn = secondApprovedOn;
    }

    /**
     * @return Returns the secondApproverRemarks.
     */
    public String getSecondApproverRemarks() {
        return _secondApproverRemarks;
    }

    /**
     * @param secondApproverRemarks
     *            The secondApproverRemarks to set.
     */
    public void setSecondApproverRemarks(String secondApproverRemarks) {
        _secondApproverRemarks = secondApproverRemarks;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the transferDate.
     */
    public Date getTransferDate() {
        return _transferDate;
    }

    /**
     * @param transferDate
     *            The transferDate to set.
     */
    public void setTransferDate(Date transferDate) {
        _transferDate = transferDate;
    }

    /**
     * @return Returns the transferMrp.
     */
    public String getTransferMrp() {
        return _transferMrp;
    }

    /**
     * @param transferMrp
     *            The transferMrp to set.
     */
    public void setTransferMrp(String transferMrp) {
        _transferMrp = transferMrp;
    }

    /**
     * @return Returns the txnProfile.
     */
    public String getTxnProfile() {
        return _txnProfile;
    }

    /**
     * @param txnProfile
     *            The txnProfile to set.
     */
    public void setTxnProfile(String txnProfile) {
        _txnProfile = txnProfile;
    }

    /**
     * @return Returns the userGradeCode.
     */
    public String getUserGradeCode() {
        return _userGradeCode;
    }

    /**
     * @param userGradeCode
     *            The userGradeCode to set.
     */
    public void setUserGradeCode(String userGradeCode) {
        _userGradeCode = userGradeCode;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    public int getSizeOfCategoryList() {
        if (_categoryList != null) {
            return _categoryList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the categoryList.
     */
    public ArrayList getCategoryList() {
        return _categoryList;
    }

    /**
     * @param categoryList
     *            The categoryList to set.
     */
    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    /**
     * @return Returns the domainCodeDesc.
     */
    public String getDomainCodeDesc() {
        return _domainCodeDesc;
    }

    /**
     * @param domainCodeDesc
     *            The domainCodeDesc to set.
     */
    public void setDomainCodeDesc(String domainCodeDesc) {
        _domainCodeDesc = domainCodeDesc;
    }

    /**
     * @return Returns the domainList.
     */
    public ArrayList getDomainList() {
        return _domainList;
    }

    /**
     * @param domainList
     *            The domainList to set.
     */
    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    public int getSizeOfDomainList() {
        if (_domainList != null) {
            return _domainList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the geographicalDomainCode.
     */
    public String getGeographicalDomainCode() {
        return _geographicalDomainCode;
    }

    /**
     * @param geographicalDomainCode
     *            The geographicalDomainCode to set.
     */
    public void setGeographicalDomainCode(String geographicalDomainCode) {
        _geographicalDomainCode = geographicalDomainCode;
    }

    /**
     * @return Returns the geographicalDomainCodeDesc.
     */
    public String getGeographicalDomainCodeDesc() {
        return _geographicalDomainCodeDesc;
    }

    /**
     * @param geographicalDomainCodeDesc
     *            The geographicalDomainCodeDesc to set.
     */
    public void setGeographicalDomainCodeDesc(String geographicalDomainCodeDesc) {
        _geographicalDomainCodeDesc = geographicalDomainCodeDesc;
    }

    /**
     * @return Returns the geographicalDomainList.
     */
    public ArrayList getGeographicalDomainList() {
        return _geographicalDomainList;
    }

    /**
     * @param geographicalDomainList
     *            The geographicalDomainList to set.
     */
    public void setGeographicalDomainList(ArrayList geographicalDomainList) {
        _geographicalDomainList = geographicalDomainList;
    }

    public int getSizeOfGeographicalDomainList() {
        if (_geographicalDomainList != null) {
            return _geographicalDomainList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the externalTxnExist.
     */
    public String getExternalTxnExist() {
        return _externalTxnExist;
    }

    /**
     * @param externalTxnExist
     *            The externalTxnExist to set.
     */
    public void setExternalTxnExist(String externalTxnExist) {
        _externalTxnExist = externalTxnExist;
    }

    /**
     * @return Returns the externalTxnMandatory.
     */
    public String getExternalTxnMandatory() {
        return _externalTxnMandatory;
    }

    /**
     * @param externalTxnMandatory
     *            The externalTxnMandatory to set.
     */
    public void setExternalTxnMandatory(String externalTxnMandatory) {
        _externalTxnMandatory = externalTxnMandatory;
    }

    /**
     * @return Returns the focOrderApprovalLevel.
     */
    public int getO2COrderApprovalLevel() {
        return _o2cOrderApprovalLevel;
    }

    /**
     * @param focOrderApprovalLevel
     *            The focOrderApprovalLevel to set.
     */
    public void setO2COrderApprovalLevel(int o2cOrderApprovalLevel) {
        _o2cOrderApprovalLevel = o2cOrderApprovalLevel;
    }

    /**
     * @return Returns the level1ApprovedRecords.
     */
    public int getLevel1ApprovedRecords() {
        return _level1ApprovedRecords;
    }

    /**
     * @param level1ApprovedRecords
     *            The level1ApprovedRecords to set.
     */
    public void setLevel1ApprovedRecords(int level1ApprovedRecords) {
        _level1ApprovedRecords = level1ApprovedRecords;
    }

    /**
     * @return Returns the level2ApprovedRecords.
     */
    public int getLevel2ApprovedRecords() {
        return _level2ApprovedRecords;
    }

    /**
     * @param level2ApprovedRecords
     *            The level2ApprovedRecords to set.
     */
    public void setLevel2ApprovedRecords(int level2ApprovedRecords) {
        _level2ApprovedRecords = level2ApprovedRecords;
    }

    /**
     * @return Returns the newRecords.
     */
    public int getNewRecords() {
        return _newRecords;
    }

    /**
     * @param newRecords
     *            The newRecords to set.
     */
    public void setNewRecords(int newRecords) {
        _newRecords = newRecords;
    }

    /**
     * @return Returns the rejectedRecords.
     */
    public int getRejectedRecords() {
        return _rejectedRecords;
    }

    /**
     * @param rejectedRecords
     *            The rejectedRecords to set.
     */
    public void setRejectedRecords(int rejectedRecords) {
        _rejectedRecords = rejectedRecords;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    /**
     * @return Returns the totalRecords.
     */
    public int getTotalRecords() {
        return _totalRecords;
    }

    /**
     * @param totalRecords
     *            The totalRecords to set.
     */
    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
    }

    /**
     * @return Returns the closedRecords.
     */
    public int getClosedRecords() {
        return _closedRecords;
    }

    /**
     * @param closedRecords
     *            The closedRecords to set.
     */
    public void setClosedRecords(int closedRecords) {
        _closedRecords = closedRecords;
    }

    /**
     * @return Returns the selectedIndex.
     */
    public int getSelectedIndex() {
        return _selectedIndex;
    }

    /**
     * @param selectedIndex
     *            The selectedIndex to set.
     */
    public void setSelectedIndex(int selectedIndex) {
        _selectedIndex = selectedIndex;
    }

    /**
     * @return Returns the fromDate.
     */
    public String getFromDate() {
        return _fromDate;
    }

    /**
     * @param fromDate
     *            The fromDate to set.
     */
    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    /**
     * @return Returns the toDate.
     */
    public String getToDate() {
        return _toDate;
    }

    /**
     * @param toDate
     *            The toDate to set.
     */
    public void setToDate(String toDate) {
        _toDate = toDate;
    }

    // This method is used to flush the form bean.

    public void flush() {
        _productShortCode = null;
        _batchId = null;
        _networkCode = null;
        _networkCodeFor = null;
        _batchName = null;
        _status = null;
        _domainCode = null;
        _productCode = null;
        _batchFileName = null;
        _batchTotalRecord = null;
        _batchDate = null;
        _createdBy = null;
        _createdOn = null;
        _modifiedBy = null;
        _modifiedOn = null;
        _batchDetailId = null;
        _categoryCode = null;
        _msisdn = null;
        _userId = null;
        _userGradeCode = null;
        _referenceNo = null;
        _extTxnNo = null;
        _extTxnDate = null;
        _transferDate = null;
        _txnProfile = null;
        _requestedQuantity = null;
        _transferMrp = null;
        _initiatorRemarks = null;
        _firstApproverRemarks = null;
        _secondApproverRemarks = null;
        _firstApprovedBy = null;
        _firstApprovedOn = null;
        _secondApprovedBy = null;
        _secondApprovedOn = null;
        _cancelledBy = null;
        _cancelledOn = null;
        _rcrdStatus = null;
        _geographyCode = null;
        _geographicalDomainCode = null;
        _geographicalDomainCodeDesc = null;
        _geographicalDomainList = null;
        _domainCodeDesc = null;
        _domainList = null;
        _categoryList = null;
        _categoryName = null;
        _productTypeCode = null;
        _productTypeCodeDesc = null;
        _productTypeList = null;
        _defaultLang = null;
        _secondLang = null;

        // Used for approval
        _requestType = null;
        _externalTxnExist = null;
        _externalTxnMandatory = null;
        _o2cOrderApprovalLevel = 0;
        _totalRecords = 0;
        _newRecords = 0;
        _level1ApprovedRecords = 0;
        _level2ApprovedRecords = 0;
        _rejectedRecords = 0;
        _closedRecords = 0;
        _selectedIndex = 0;
        _fromDate = null;
        _toDate = null;
        _o2cBatchMasterVOList = null;
        _productMrp = 0;
        _productMrpStr = null;
        _productName = null;
        _productList = null;
        _fromDateMsisdn = null;
        _toDateMsisdn = null;
        _searchType = null;
        _errorList = null;
        _noOfRecords = null;
        _processedRecs = null;
        _viewErrorLog = null;
        _o2cBatchItemVOList = null;
        _domainTypeCode = null;
        _downLoadDataMap = null;
        _concatStr = null;
        _pageOffset = null;

    }

    public void semiFlush() {
        _geographicalDomainCode = null;
        _batchName = null;
        _categoryCode = null;
        _categoryName = null;
        _domainCode = null;
        _productName = null;
        _productMrp = 0;
        _productMrpStr = null;
        _productShortCode = null;
        _batchId = null;
        _productCode = null;
        _batchFileName = null;
    }

    /**
     * @return Returns the domainTypeCode.
     */
    public String getDomainTypeCode() {
        return _domainTypeCode;
    }

    /**
     * @param domainTypeCode
     *            The domainTypeCode to set.
     */
    public void setDomainTypeCode(String domainTypeCode) {
        _domainTypeCode = domainTypeCode;
    }

    public int getO2CBatchMasterVOListSize() {
        if (_o2cBatchMasterVOList != null && !_o2cBatchMasterVOList.isEmpty()) {
            return _o2cBatchMasterVOList.size();
        }
        return 0;
    }

    /**
     * @return Returns the fromDateMsisdn.
     */
    public String getFromDateMsisdn() {
        return _fromDateMsisdn;
    }

    /**
     * @param fromDateMsisdn
     *            The fromDateMsisdn to set.
     */
    public void setFromDateMsisdn(String fromDateMsisdn) {
        _fromDateMsisdn = fromDateMsisdn;
    }

    /**
     * @return Returns the productList.
     */
    public ArrayList getProductList() {
        return _productList;
    }

    /**
     * @param productList
     *            The productList to set.
     */
    public void setProductList(ArrayList productList) {
        _productList = productList;
    }

    public int getSizeOfProductList() {
        if (_productList != null) {
            return _productList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the productName.
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     *            The productName to set.
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    /**
     * @return Returns the toDateMsisdn.
     */
    public String getToDateMsisdn() {
        return _toDateMsisdn;
    }

    /**
     * @param toDateMsisdn
     *            The toDateMsisdn to set.
     */
    public void setToDateMsisdn(String toDateMsisdn) {
        _toDateMsisdn = toDateMsisdn;
    }

    /**
     * @return Returns the productMrp.
     */
    public long getProductMrp() {
        return _productMrp;
    }

    /**
     * @param productMrp
     *            The productMrp to set.
     */
    public void setProductMrp(long productMrp) {
        _productMrp = productMrp;
    }

    /**
     * @return Returns the productMrpStr.
     */
    public String getProductMrpStr() {
        return _productMrpStr;
    }

    /**
     * @param productMrpStr
     *            The productMrpStr to set.
     */
    public void setProductMrpStr(String productMrpStr) {
        _productMrpStr = productMrpStr;
    }

    /**
     * @return Returns the errorList.
     */
    public ArrayList getErrorList() {
        return _errorList;
    }

    /**
     * @param errorList
     *            The errorList to set.
     */
    public void setErrorList(ArrayList errorList) {
        _errorList = errorList;
    }

    /**
     * @return Returns the noOfRecords.
     */
    public String getNoOfRecords() {
        return _noOfRecords;
    }

    /**
     * @param noOfRecords
     *            The noOfRecords to set.
     */
    public void setNoOfRecords(String noOfRecords) {
        _noOfRecords = noOfRecords;
    }

    /**
     * @return Returns the processedRecs.
     */
    public String getProcessedRecs() {
        return _processedRecs;
    }

    /**
     * @param processedRecs
     *            The processedRecs to set.
     */
    public void setProcessedRecs(String processedRecs) {
        _processedRecs = processedRecs;
    }

    /**
     * @return Returns the viewErrorLog.
     */
    public String getViewErrorLog() {
        return _viewErrorLog;
    }

    /**
     * @param viewErrorLog
     *            The viewErrorLog to set.
     */
    public void setViewErrorLog(String viewErrorLog) {
        _viewErrorLog = viewErrorLog;
    }

    /**
     * @return Returns the size of errorList.
     */
    public int getErrorListSize() {
        if (_errorList == null) {
            return 0;
        } else {
            return _errorList.size();
        }
    }

    /**
     * @return Returns the pageOffset.
     */
    public String getPageOffset() {
        return _pageOffset;
    }

    /**
     * @param pageOffset
     *            The pageOffset to set.
     */
    public void setPageOffset(String pageOffset) {
        _pageOffset = pageOffset;
    }

    // For PIN Authentication in O2C Transactions- 04/03/13.

    public boolean getShowPin() {
        return _showPin;
    }

    /**
     * @param _showPin
     *            to set.
     */
    public void setShowPin(boolean ShowPin) {
        _showPin = ShowPin;
    }

    /**
     * @return Returns the smsPin.
     */
    public String getSmsPin() {
        return _smsPin;
    }

    /**
     * @param smsPin
     *            The smsPin to set.
     */
    public void setSmsPin(String smsPin) {
        _smsPin = smsPin;
    }

    public String getDisplayMsisdn() {
        return _displayMsisdn;
    }

    /**
     * @param displayMsisdn
     *            The displayMsisdn to set.
     */
    public void setDisplayMsisdn(String displayMsisdn) {
        _displayMsisdn = displayMsisdn;
    }

    /**
     * @return Returns the displayPin.
     */
    public String getDisplayPin() {
        return _displayPin;
    }

    /**
     * @param displayPin
     *            The displayPin to set.
     */
    public void setDisplayPin(String displayPin) {
        _displayPin = displayPin;
    }

    public String getJ_captcha_response() {
        return j_captcha_response;
    }

    public void setJ_captcha_response(String j_captcha_response) {
        this.j_captcha_response = j_captcha_response;
    }
}
