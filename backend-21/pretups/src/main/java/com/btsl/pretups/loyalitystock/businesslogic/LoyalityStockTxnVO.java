/**
 * @(#)NetworkStockTxnVO.java
 *                            Value Object for StockItems.
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Sandeep Goel 04/09/2005 Initial Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 */
package com.btsl.pretups.loyalitystock.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class LoyalityStockTxnVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String _txnNo;
    private String _networkCode;
    private String _networkFor;
    private String _stockType;
    private String _referenceNo;
    private Date _txnDate;
    private String _txnDateStr;
    private long _requestedQuantity;
    private long _approvedQuantity;
    private String _initiaterRemarks;
    private String _firstApprovedRemarks;
    private String _secondApprovedRemarks;
    private String _firstApprovedBy;
    private String _secondApprovedBy;
    private Date _firstApprovedOn;
    private Date _secondApprovedOn;
    private String _cancelledBy;
    private Date _cancelledOn;
    private String _createdBy;
    private Date _createdOn;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _txnStatus;
    private String _entryType;
    private String _txnType;
    private String _initiatedBy;
    private long _firstApproverLimit;
    private String _userID;
    private long _txnMrp;
    private Long _requestedPoints;

    // added by sandeep goel for display purpose
    private String _initiaterName;
    private String _txnStatusName;
    private String _networkForName;
    private String _networkName;
    private long _lastModifiedTime;
    private String _txnMrpStr;
    private String _approvedOnStr;
    // ends here

    // fields used in the logger
    private String _otherInfo = null;
    private String _txnCategory = null;
    private long _postStock = 0;
    private long _previousStock = 0;
    private String _productCode = null;

    private ArrayList _networkStockTxnItemsList;
    // form Mali -- +ve commision apply
    private long _tax3value = 0;

    // added for multiple wallet
    private String _txnWallet = null;
    private String _refTxnID;
    private Timestamp _dbDateTime = null;

    /**
     * @return the refTxnID
     */
    public String getRefTxnID() {
        return _refTxnID;
    }

    /**
     * @param refTxnID
     *            the refTxnID to set
     */
    public void setRefTxnID(String refTxnID) {
        _refTxnID = refTxnID;
    }

    public String getTxnWallet() {
        return _txnWallet;
    }

    public void setTxnWallet(String txnWallet) {
        _txnWallet = txnWallet;
    }

    public long getApprovedQuantity() {
        return _approvedQuantity;
    }

    public void setApprovedQuantity(long approvedQuantity) {
        _approvedQuantity = approvedQuantity;
    }

    public String getCancelledBy() {
        return _cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        _cancelledBy = cancelledBy;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public String getEntryType() {
        return _entryType;
    }

    public void setEntryType(String entryType) {
        _entryType = entryType;
    }

    public String getFirstApprovedBy() {
        return _firstApprovedBy;
    }

    public void setFirstApprovedBy(String firstApprovedBy) {
        _firstApprovedBy = firstApprovedBy;
    }

    public Date getFirstApprovedOn() {
        return _firstApprovedOn;
    }

    public void setFirstApprovedOn(Date firstApprovedOn) {
        _firstApprovedOn = firstApprovedOn;
    }

    public String getFirstApprovedRemarks() {
        return _firstApprovedRemarks;
    }

    public void setFirstApprovedRemarks(String firstApprovedRemarks) {
        _firstApprovedRemarks = firstApprovedRemarks;
    }

    public long getFirstApproverLimit() {
        return _firstApproverLimit;
    }

    public void setFirstApproverLimit(long firstApproverLimit) {
        _firstApproverLimit = firstApproverLimit;
    }

    public String getInitiatedBy() {
        return _initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        _initiatedBy = initiatedBy;
    }

    public String getInitiaterRemarks() {
        return _initiaterRemarks;
    }

    public void setInitiaterRemarks(String initiaterRemarks) {
        _initiaterRemarks = initiaterRemarks;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getCancelledOn() {
        return _cancelledOn;
    }

    public void setCancelledOn(Date cancelledOn) {
        _cancelledOn = cancelledOn;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getNetworkFor() {
        return _networkFor;
    }

    public void setNetworkFor(String networkFor) {
        _networkFor = networkFor;
    }

    public String getReferenceNo() {
        return _referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        _referenceNo = referenceNo;
    }

    public long getRequestedQuantity() {
        return _requestedQuantity;
    }

    public void setRequestedQuantity(long requestedQuantity) {
        _requestedQuantity = requestedQuantity;
    }

    public String getSecondApprovedBy() {
        return _secondApprovedBy;
    }

    public void setSecondApprovedBy(String secondApprovedBy) {
        _secondApprovedBy = secondApprovedBy;
    }

    public Date getSecondApprovedOn() {
        return _secondApprovedOn;
    }

    public void setSecondApprovedOn(Date secondApprovedOn) {
        _secondApprovedOn = secondApprovedOn;
    }

    public String getSecondApprovedRemarks() {
        return _secondApprovedRemarks;
    }

    public void setSecondApprovedRemarks(String secondApprovedRemarks) {
        _secondApprovedRemarks = secondApprovedRemarks;
    }

    public String getStockType() {
        return _stockType;
    }

    public void setStockType(String stockType) {
        _stockType = stockType;
    }

    public Date getTxnDate() {
        return _txnDate;
    }

    public void setTxnDate(Date txnDate) {
        _txnDate = txnDate;
    }

    public long getTxnMrp() {
        return _txnMrp;
    }

    public void setTxnMrp(long txnMrp) {
        _txnMrp = txnMrp;
    }

    public String getTxnNo() {
        return _txnNo;
    }

    public void setTxnNo(String txnNo) {
        _txnNo = txnNo;
    }

    public String getTxnStatus() {
        return _txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        _txnStatus = txnStatus;
    }

    public String getTxnType() {
        return _txnType;
    }

    public void setTxnType(String txnType) {
        _txnType = txnType;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        _userID = userID;
    }

    public ArrayList getNetworkStockTxnItemsList() {
        return _networkStockTxnItemsList;
    }

    public void setNetworkStockTxnItemsList(ArrayList stockTxnItemsList) {
        _networkStockTxnItemsList = stockTxnItemsList;
    }

    public String getTxnStatusName() {
        return _txnStatusName;
    }

    public void setTxnStatusName(String txnStatusName) {
        _txnStatusName = txnStatusName;
    }

    public String getInitiaterName() {
        return _initiaterName;
    }

    public void setInitiaterName(String initiaterName) {
        _initiaterName = initiaterName;
    }

    public String getNetworkForName() {
        return _networkForName;
    }

    public void setNetworkForName(String networkForName) {
        _networkForName = networkForName;
    }

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    public String getTxnMrpStr() {
        return _txnMrpStr;
    }

    public void setTxnMrpStr(String txnMrpStr) {
        _txnMrpStr = txnMrpStr;
    }

    public String getTxnDateStr() {
        return _txnDateStr;
    }

    public void setTxnDateStr(String txnDateStr) {
        _txnDateStr = txnDateStr;
    }

    public String getApprovedOnStr() {
        return _approvedOnStr;
    }

    public void setApprovedOnStr(String approvedOnStr) {
        _approvedOnStr = approvedOnStr;
    }

    public String getOtherInfo() {
        return _otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        _otherInfo = otherInfo;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getTxnCategory() {
        return _txnCategory;
    }

    public void setTxnCategory(String txnCategory) {
        _txnCategory = txnCategory;
    }

    public long getPostStock() {
        return _postStock;
    }

    public void setPostStock(long postStock) {
        _postStock = postStock;
    }

    public long getPreviousStock() {
        return _previousStock;
    }

    public void setPreviousStock(long previousStock) {
        _previousStock = previousStock;
    }

    /**
     * @return Returns the tax3value.
     */
    public long getTax3value() {
        return _tax3value;
    }

    /**
     * @param tax3value
     *            The tax3value to set.
     */
    public void setTax3value(long tax3value) {
        _tax3value = tax3value;
    }

    public Timestamp getDBDateTime() {
        return _dbDateTime;
    }

    public void setDBDateTime(Timestamp dbDateTime) {
        _dbDateTime = dbDateTime;
    }

    public Long getRequestedPoints() {
        return _requestedPoints;
    }

    public void setRequestedPoints(Long requestedPoints) {
        _requestedPoints = requestedPoints;
    }

}
