package com.web.pretups.channel.transfer.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * @(#)O2CBatchWithdrawForm.java
 *                               Copyright(c) 2011, Comviva.
 *                               All Rights Reserved
 * 
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Chhaya Sikheria 17/10/2011 Initial Creation
 * 
 */

public class O2CBatchWithdrawForm  {

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
    private Date _transferDate = null;
    private String _txnProfile = null;
    private String _requestedQuantity = null;
    private String _transferMrp = null;
    private String _initiatorRemarks = null;
    private String _approverRemarks = null;
    private String _approvedBy = null;
    private String _approvedOn = null;
    private String _cancelledBy = null;
    private String _cancelledOn = null;
    private String _rcrdStatus = null;
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
    private int _totalRecords = 0;
    private int _newRecords = 0;
    private int _approvedRecords = 0;
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
    private Map _downLoadDataMap = null;
    private ArrayList _o2cBatchItemVOList = null;
    private Date _commPrfApplicableDate;
    private String _errorFlag;
    private String _domainTypeCode = null;
    private String _concatStr = null;
    private String _pageOffset = null;
    private String _defaultLang = null;
    private String _secondLang = null;
    private String _currentDateFlag = null;
    private String _currentDateFlagMsisdn = null;
    private String _userName = null;
    private String _toCategoryCode = null;
    private int _productTypesListSize = 0;
    private String _productType = null;
    private ArrayList _c2cBatchMasterVOList;
    private String _transferType = null;
    private String _transferSubType = null;
    private String _transaction = null;
    private String _loggedInUserCategoryCode;
    private String _loggedInUserName;
    private String _loginUserID;
    private long _time;
    private ArrayList _userList;
    private int _userListSize;
    private String _userID;
    private ArrayList _geographicalDomainList = null;
    private String _geographicalDomainCode = null;
    private String _geographicalDomainCodeDesc = null;
    private String _domainName;
    private String _transferCategory = null;
    private int _c2cApprvoalLevel;
    private String _geographyCode = null;
    private String _categoryCodeDesc = null;
    private String _externalTxnMandatory = null;
    private ArrayList _o2cBatchMasterVOList = null;
    private int _o2cOrderApprovalLevel;
    private int _level1ApprovedRecords = 0;
    private int _level2ApprovedRecords = 0;
    private String _firstApproverRemarks = null;
    private String _secondApproverRemarks = null;
    private String _thirdApproverRemarks = null;
    private String _domainDesc = null;

    private String _isValidate = null;

    private String _walletType = null;
    private ArrayList _walletTypeList = null;
    // For PIN Authentication in O2C Transactions- 04/03/13.
    private boolean _showPin = false;
    private String _smsPin = null;
    private String _displayMsisdn = null;
    private String _displayPin = null;
    // For CAPTCHA
    private String j_captcha_response = null;

    public ArrayList getWalletTypeList() {
        return _walletTypeList;
    }

    public String getWalletType() {
        return _walletType;

    }

    public void setWalletTypeList(ArrayList typeList) {
        _walletTypeList = typeList;
    }

    public void setWalletType(String type) {
        _walletType = type;
    }

    /**
     * @return the isValidate
     */
    public String getIsValidate() {
        return _isValidate;
    }

    /**
     * @param isValidate
     *            the isValidate to set
     */
    public void setIsValidate(String isValidate) {
        _isValidate = isValidate;
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
    public String getErrorFlag() {
        return _errorFlag;
    }

    /**
     * @param errorFlag
     *            The errorFlag to set.
     */
    public void setErrorFlag(String errorFlag) {
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
    public ArrayList getO2cBatchItemVOList() {
        return _o2cBatchItemVOList;
    }

    /**
     * @param focBatchItemVOList
     *            The focBatchItemVOList to set.
     */
    public void setO2cBatchItemVOList(ArrayList o2cBatchItemVOList) {
        _o2cBatchItemVOList = o2cBatchItemVOList;
    }

    /**
     * @return Returns the downLoadDataMap.
     */
    public Map getDownLoadDataMap() {
        return _downLoadDataMap;
    }

    /**
     * @param downLoadDataMap
     *            The downLoadDataMap to set.
     */
    public void setDownLoadDataMap(Map downLoadDataMap) {
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

    /**
     * @return Returns the file.
     */
  /*  public FormFile getFile() {
        return _file;
    }
*/
    /**
     * @param file
     *            The file to set.
     */
  /*  public void setFile(FormFile file) {
        _file = file;
    }
*/
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
     * @return Returns the ApprovedBy.
     */
    public String getApprovedBy() {
        return _approvedBy;
    }

    /**
     * @param ApprovedBy
     *            The ApprovedBy to set.
     */
    public void setApprovedBy(String approvedBy) {
        _approvedBy = approvedBy;
    }

    /**
     * @return Returns the ApprovedOn.
     */
    public String getApprovedOn() {
        return _approvedOn;
    }

    /**
     * @param ApprovedOn
     *            The ApprovedOn to set.
     */
    public void setApprovedOn(String approvedOn) {
        _approvedOn = approvedOn;
    }

    /**
     * @return Returns the ApproverRemarks.
     */
    public String getApproverRemarks() {
        return _approverRemarks;
    }

    /**
     * @param ApproverRemarks
     *            The ApproverRemarks to set.
     */
    public void setApproverRemarks(String approverRemarks) {
        _approverRemarks = approverRemarks;
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
     * @return Returns the ApprovedRecords.
     */
    public int getApprovedRecords() {
        return _approvedRecords;
    }

    /**
     * @param ApprovedRecords
     *            The ApprovedRecords to set.
     */
    public void setApprovedRecords(int approvedRecords) {
        _approvedRecords = approvedRecords;
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

        _transferDate = null;
        _txnProfile = null;
        _requestedQuantity = null;
        _transferMrp = null;
        _initiatorRemarks = null;
        _approverRemarks = null;
        _approvedBy = null;
        _approvedOn = null;
        _cancelledBy = null;
        _cancelledOn = null;
        _rcrdStatus = null;
        _domainCodeDesc = null;
        _domainList = null;
        _categoryList = null;
        _categoryName = null;
        _productTypeCode = null;
        _productTypeCodeDesc = null;
        _productTypeList = null;
        _defaultLang = null;
        _secondLang = null;
        _toCategoryCode = null;

        // Used for approval
        _requestType = null;
        _totalRecords = 0;
        _newRecords = 0;
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
        _currentDateFlag = null;
        _currentDateFlagMsisdn = null;
        _firstApproverRemarks = null;
        _secondApproverRemarks = null;
        _thirdApproverRemarks = null;

    }
    
    public void semiFlush() {
        _geographicalDomainCode = null;
        _categoryCode = null;
        _categoryName = null;
        _batchName = null;
        _domainCode = null;
        _productName = null;
        _walletType=null;
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

    public int getC2cBatchMasterVOListSize() {
        if (_c2cBatchMasterVOList != null && !_c2cBatchMasterVOList.isEmpty()) {
            return _c2cBatchMasterVOList.size();
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

    public String getCurrentDateFlag() {
        return _currentDateFlag;
    }

    public void setCurrentDateFlag(String currentDateFlag) {
        this._currentDateFlag = currentDateFlag;
    }

    public String getCurrentDateFlagMsisdn() {
        return _currentDateFlagMsisdn;
    }

    public void setCurrentDateFlagMsisdn(String currentDateFlagMsisdn) {
        this._currentDateFlagMsisdn = currentDateFlagMsisdn;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public void setDomainDesc(String domainName) {
        // TODO Auto-generated method stub
        _domainDesc = domainName;
    }

    public String getToCategoryCode() {
        return _toCategoryCode;
    }

    public void setToCategoryCode(String toCategoryCode) {
        _toCategoryCode = toCategoryCode;
    }

    public String getProductType() {
        return _productType;
    }

    public void setProductType(String productType) {
        _productType = productType;
    }

    public int getProductTypesListSize() {
        return _productTypesListSize;
    }

    public void setProductTypesListSize(int productTypesListSize) {
        _productTypesListSize = productTypesListSize;
    }

    public ArrayList getC2cBatchMasterVOList() {
        return _c2cBatchMasterVOList;
    }

    public void setC2cBatchMasterVOList(ArrayList batchMasterVOList) {
        _c2cBatchMasterVOList = batchMasterVOList;
    }

    public String getTransferSubType() {
        return _transferSubType;
    }

    public void setTransferSubType(String transferSubType) {
        _transferSubType = transferSubType;
    }

    public String getTransferType() {
        return _transferType;
    }

    public void setTransferType(String transferType) {
        _transferType = transferType;
    }

    public String getTransaction() {
        return _transaction;
    }

    public void setTransaction(String transaction) {
        _transaction = transaction;
    }

    /**
     * @return Returns the loggedInUserCategoryCode.
     */
    public String getLoggedInUserCategoryCode() {
        return _loggedInUserCategoryCode;
    }

    /**
     * @param loggedInUserCategoryCode
     *            The loggedInUserCategoryCode to set.
     */
    public void setLoggedInUserCategoryCode(String loggedInUserCategoryCode) {
        _loggedInUserCategoryCode = loggedInUserCategoryCode;
    }

    /**
     * @return Returns the loggedInUserName.
     */
    public String getLoggedInUserName() {
        return _loggedInUserName;
    }

    /**
     * @param loggedInUserName
     *            The loggedInUserName to set.
     */
    public void setLoggedInUserName(String loggedInUserName) {
        _loggedInUserName = loggedInUserName;
    }

    public String getLoginUserID() {
        return _loginUserID;
    }

    public void setLoginUserID(String userID) {
        _loginUserID = userID;
    }

    public long getTime() {
        return _time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
        _time = time;
    }

    public ArrayList getUserList() {
        return _userList;
    }

    public void setUserList(ArrayList userList) {
        _userList = userList;
    }

    public int getUserListSize() {
        if (_userList != null) {
            return _userList.size();
        }
        return 0;
    }

    public void setUserListSize(ArrayList userList) {
        if (userList != null) {
            _userListSize = userList.size();
        } else {
            _userListSize = 0;
        }
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userid) {
        _userID = userid;
    }

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

    public int getSizeOfGeographicalDomainList() {
        if (_geographicalDomainList != null) {
            return _geographicalDomainList.size();
        } else {
            return 0;
        }
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String name) {
        _domainName = name;
    }

    public String getTransferCategory() {
        return _transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
    }

    public int getC2cApprvoalLevel() {
        return _c2cApprvoalLevel;
    }

    public void setC2cApprvoalLevel(int apprvoalLevel) {
        _c2cApprvoalLevel = apprvoalLevel;
    }

    public String getCategoryCodeDesc() {
        return _categoryCodeDesc;
    }

    public void setCategoryCodeDesc(String codeDesc) {
        _categoryCodeDesc = codeDesc;
    }

    public String getGeographyCode() {
        return _geographyCode;
    }

    public void setGeographyCode(String code) {
        _geographyCode = code;
    }

    /**
     * @return the _externalTxnMandatory
     */
    public String getExternalTxnMandatory() {
        return _externalTxnMandatory;
    }

    /**
     * @param txnMandatory
     *            the _externalTxnMandatory to set
     */
    public void setExternalTxnMandatory(String txnMandatory) {
        _externalTxnMandatory = txnMandatory;
    }

    /**
     * @param orderApprovalLevel
     *            the _o2cBatchMasterVOList to set
     */
    public void setO2cBatchMasterVOList(ArrayList orderApprovalLevel) {
        _o2cBatchMasterVOList = orderApprovalLevel;
    }

    public int getO2cBatchMasterVOListSize() {
        if (_o2cBatchMasterVOList != null && !_o2cBatchMasterVOList.isEmpty()) {
            return _o2cBatchMasterVOList.size();
        }
        return 0;
    }

    /**
     * @param orderApprovalLevel
     *            the _o2cOrderApprovalLevel to set
     */
    public void setO2cOrderApprovalLevel(int orderApprovalLevel) {
        _o2cOrderApprovalLevel = orderApprovalLevel;
    }

    /**
     * @return the _o2cBatchMasterVOList
     */
    public ArrayList getO2cBatchMasterVOList() {
        return _o2cBatchMasterVOList;
    }

    /**
     * @return the _externalTxnMandatory
     */
    public int getO2cOrderApprovalLevel() {
        return _o2cOrderApprovalLevel;
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

    public String getFirstApproverRemarks() {
        // TODO Auto-generated method stub
        return _firstApproverRemarks;
    }

    public void setFirstApproverRemarks(String firstApproverRemarks) {
        // TODO Auto-generated method stub
        _firstApproverRemarks = firstApproverRemarks;
    }

    public String getSecondApproverRemarks() {
        // TODO Auto-generated method stub
        return _secondApproverRemarks;
    }

    public void setSecondApproverRemarks(String secondApproverRemarks) {
        // TODO Auto-generated method stub
        _secondApproverRemarks = secondApproverRemarks;
    }

    public String getThirdApproverRemarks() {
        // TODO Auto-generated method stub
        return _thirdApproverRemarks;
    }

    public void getThirdApproverRemarks(String thirdApproverRemarks) {
        // TODO Auto-generated method stub
        _thirdApproverRemarks = thirdApproverRemarks;
    }

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
