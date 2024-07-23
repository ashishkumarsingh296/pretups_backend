/**
 * 
 */
package com.btsl.pretups.sos.businesslogic;

import java.util.Date;

import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.PretupsBL;

/**
 * @author shamit.jain
 * 
 */
public class SOSVO extends RequestVO {

    // private String _transactionid=null;
    // private Locale _locale=null;
    private String _subscriberMSISDN;
    // private String _serviceType;
    private long _debitAmount;
    private String _msisdnPrefix;
    // private String _type;
    private String _networkCode;
    private String _subscriberType;
    private String _paymentMethodType; // Payment method type to be used in
                                       // request
    private long _prefixID;
    private String _interfaceID;
    private String _interfaceHandlerClass;
    private String _validationStatus;
    private String _protocolStatus;
    private String _accountStatus;
    private String _interfaceResponseCode;
    private String _interfaceReferenceID;
    private String _referenceID;
    private String _errorCode;
    private String _transactionStatus;
    private String _transferStatus;
    private Date _previousExpiry;
    private Date _previousGraceDate;
    private String _firstCall;
    private String _graceDaysStr;
    private String _serviceClassCode;
    private long _previousBalance;
    private String _serviceClass;
    private boolean _numberBackAllowed = false;
    private String _updateStatus;
    private Date _newExpiry;
    private Date _newGraceDate;
    private long _postBalance;
    private String _userID;
    private Object _sosReturnMsg = null;
    private int _totalRecords;
    private int _totalSucces;
    private int _totalFail;
    private int _recordCount;
    private String _errorStage;

    // Added by Nand
    private Date _rechargeDate;
    private Date _rechargeDateTime;
    private String _rechargeDateStr;
    private long _rechargeAmount;
    private String _rechargeAmountStr;
    private String _creditAmountStr;
    private String _reconciliationFlag;
    private Date _reconciliationDate;
    private String _reconciliationBy;
    private Date _settlementDate;
    private String _settlementDateTimeStr;
    private String _settlementFlag;
    private String _settlementReconFlag;
    private Date _settlementReconDate;
    private String _settlementStatus;
    private String _settlementReconBy;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _productName;
    private String _cardGroupSetID;
    private String _version;
    private String _cardGroupID;
    private String _cardGroupCode;
    private String _errorMessage;
    private String _previousBalanceStr;
    private String _postBalanceStr;
    private String _debitAmountStr;
    private String _settlementAmountStr;
    private long _creditAmount;
    private double _lmbAmountAtIN;
    private String _lmbUpdateStatus;
    private String _oldExpiryInMillis;
    private String _validityExpired;
    private String _settlmntServiceType;
    // for USSD Change
    private String _cellId;
    private String _switchId;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_transactionID=" + getTransactionID());
        sbf.append(", _rechargeDateTime=" + _rechargeDateTime);
        sbf.append(", _referenceID=" + _referenceID);
        sbf.append(", _subscriberMSISDN=" + _subscriberMSISDN);
        sbf.append(", _transferStatus=" + _transferStatus);
        sbf.append(", _transactionStatus=" + _transactionStatus);
        sbf.append(", _settlementStatus=" + _settlementStatus);
        sbf.append(", _errorCode=" + _errorCode);
        sbf.append(", _previousBalance=" + _previousBalance);
        sbf.append(", _postBalance=" + _postBalance);
        sbf.append(", _interfaceResponseCode=" + _interfaceResponseCode);
        sbf.append(", _validationStatus=" + _validationStatus);
        sbf.append(", _sosReturnMsg=" + _sosReturnMsg);
        sbf.append(", _errorStage=" + _errorStage);
        sbf.append(",_creditAmount=" + _creditAmount);
        sbf.append(",_debitAmount=" + _debitAmount);
        sbf.append(",_rechargeAmount=" + _rechargeAmount);
        sbf.append(",_lmbAmountAtIN=" + _lmbAmountAtIN);
        sbf.append(",_lmbUpdateStatus=" + _lmbUpdateStatus);
        sbf.append(",_cellId=" + _cellId);
        sbf.append(",_switchId=" + _switchId);
        return sbf.toString();
    }

    /**
     * @return Returns the userID.
     */
    public String getErrorStage() {
        return _errorStage;
    }

    /**
     * @param userID
     *            The errorStage to set.
     */
    public void setErrorStage(String errorStage) {
        _errorStage = errorStage;
    }

    public int getTotalRecords() {
        return _totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
    }

    public int getTotalSucces() {
        return _totalSucces;
    }

    public void setTotalSucces(int totalSucces) {
        _totalSucces = totalSucces;
    }

    public int getTotalFail() {
        return _totalFail;
    }

    public void setTotalFail(int totalFail) {
        _totalFail = totalFail;
    }

    public Object getSOSReturnMsg() {
        return _sosReturnMsg;
    }

    public void setSOSReturnMsg(Object sosReturnMsg) {
        _sosReturnMsg = sosReturnMsg;
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            The userID to set.
     */
    public void setUserID(String userID) {
        _userID = userID;
    }

    /**
     * @return Returns the postBalance.
     */
    public long getPostBalance() {
        return _postBalance;
    }

    public String getPostBalanceAsString() {
        return PretupsBL.getDisplayAmount(_postBalance);
    }

    /**
     * @param postBalance
     *            The postBalance to set.
     */
    public void setPostBalance(long postBalance) {
        _postBalance = postBalance;
    }

    public Date getNewGraceDate() {
        return _newGraceDate;
    }

    public void setNewGraceDate(Date newGraceDate) {
        _newGraceDate = newGraceDate;
    }

    public Date getNewExpiry() {
        return _newExpiry;
    }

    public void setNewExpiry(Date newExpiry) {
        _newExpiry = newExpiry;
    }

    /**
     * @return Returns the updateStatus.
     */
    public String getUpdateStatus() {
        return _updateStatus;
    }

    /**
     * @param updateStatus
     *            The updateStatus to set.
     */
    public void setUpdateStatus(String updateStatus) {
        _updateStatus = updateStatus;
    }

    /**
     * @return Returns the numberBackAllowed.
     */
    public boolean isNumberBackAllowed() {
        return _numberBackAllowed;
    }

    /**
     * @param numberBackAllowed
     *            The numberBackAllowed to set.
     */
    public void setNumberBackAllowed(boolean numberBackAllowed) {
        _numberBackAllowed = numberBackAllowed;
    }

    /**
     * @return Returns the serviceClass.
     */
    public String getServiceClass() {
        return _serviceClass;
    }

    /**
     * @param serviceClass
     *            The serviceClass to set.
     */
    public void setServiceClass(String serviceClass) {
        _serviceClass = serviceClass;
    }

    /**
     * @return Returns the previousBalance.
     */
    public long getPreviousBalance() {
        return _previousBalance;
    }

    public String getPreviousBalanceAsString() {
        return PretupsBL.getDisplayAmount(_previousBalance);
    }

    /**
     * @param previousBalance
     *            The previousBalance to set.
     */
    public void setPreviousBalance(long previousBalance) {
        _previousBalance = previousBalance;
    }

    public String getServiceClassCode() {
        return _serviceClassCode;
    }

    public void setServiceClassCode(String serviceClassCode) {
        _serviceClassCode = serviceClassCode;
    }

    public String getGraceDaysStr() {
        return _graceDaysStr;
    }

    public void setGraceDaysStr(String graceDaysStr) {
        _graceDaysStr = graceDaysStr;
    }

    /**
     * @return Returns the firstCall.
     */
    public String getFirstCall() {
        return _firstCall;
    }

    /**
     * @param firstCall
     *            The firstCall to set.
     */
    public void setFirstCall(String firstCall) {
        _firstCall = firstCall;
    }

    public Date getPreviousGraceDate() {
        return _previousGraceDate;
    }

    public void setPreviousGraceDate(Date previousGraceDate) {
        _previousGraceDate = previousGraceDate;
    }

    public Date getPreviousExpiry() {
        return _previousExpiry;
    }

    public void setPreviousExpiry(Date previousExpiry) {
        _previousExpiry = previousExpiry;
    }

    /**
     * @return Returns the transferStatus.
     */
    public String getTransferStatus() {
        return _transferStatus;
    }

    /**
     * @param transferStatus
     *            The transferStatus to set.
     */
    public void setTransferStatus(String transferStatus) {
        _transferStatus = transferStatus;
    }

    public String getTransactionStatus() {
        return _transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        _transactionStatus = transactionStatus;
    }

    /**
     * @return Returns the errorCode.
     */
    public String getErrorCode() {
        return _errorCode;
    }

    /**
     * @param errorCode
     *            The errorCode to set.
     */
    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    public String getReferenceID() {
        return _referenceID;
    }

    public void setReferenceID(String referenceID) {
        _referenceID = referenceID;
    }

    public String getInterfaceReferenceID() {
        return _interfaceReferenceID;
    }

    public void setInterfaceReferenceID(String interfaceReferenceID) {
        _interfaceReferenceID = interfaceReferenceID;
    }

    public String getProtocolStatus() {
        return _protocolStatus;
    }

    public void setProtocolStatus(String protocolStatus) {
        _protocolStatus = protocolStatus;
    }

    public String getAccountStatus() {
        return _accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        _accountStatus = accountStatus;
    }

    public String getInterfaceResponseCode() {
        return _interfaceResponseCode;
    }

    public void setInterfaceResponseCode(String interfaceResponseCode) {
        _interfaceResponseCode = interfaceResponseCode;
    }

    /**
     * @return Returns the validationStatus.
     */
    public String getValidationStatus() {
        return _validationStatus;
    }

    /**
     * @param validationStatus
     *            The validationStatus to set.
     */
    public void setValidationStatus(String validationStatus) {
        _validationStatus = validationStatus;
    }

    public String getInterfaceHandlerClass() {
        return _interfaceHandlerClass;
    }

    public void setInterfaceHandlerClass(String interfaceHandlerClass) {
        _interfaceHandlerClass = interfaceHandlerClass;
    }

    /**
     * @return Returns the interfaceID.
     */
    public String getInterfaceID() {
        return _interfaceID;
    }

    /**
     * @param interfaceID
     *            The interfaceID to set.
     */
    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }

    public long getPrefixID() {
        return _prefixID;
    }

    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
    }

    public String getPaymentMethodType() {
        return _paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        _paymentMethodType = paymentMethodType;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
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

    /*
     * public String getTransactionid() {
     * return _transactionid;
     * }
     * 
     * public void setTransactionid(String _transactionid) {
     * this._transactionid = _transactionid;
     * }
     * 
     * public String getType() {
     * return _type;
     * }
     * public void setType(String type) {
     * _type = type;
     * }
     */
    /*
     * public Locale getLocale() {
     * return _locale;
     * }
     * public void setLocale(Locale locale) {
     * _locale = locale;
     * }
     */

    /*
     * public String getServiceType() {
     * return _serviceType;
     * }
     * public void setServiceType(String serviceType) {
     * _serviceType = serviceType;
     * }
     */

    public long getDebitAmount() {
        return _debitAmount;
    }

    public void setDebitAmount(long debitAmount) {
        _debitAmount = debitAmount;
    }

    public String getMsisdnPrefix() {
        return _msisdnPrefix;
    }

    public void setMsisdnPrefix(String msisdnPrefix) {
        _msisdnPrefix = msisdnPrefix;
    }

    /**
     * @return Returns the subscriberMSISDN.
     */
    public String getSubscriberMSISDN() {
        return _subscriberMSISDN;
    }

    /**
     * @param filteredMSISDN
     *            The subscriberMSISDN to set.
     */
    public void setSubscriberMSISDN(String subscriberMSISDN) {
        _subscriberMSISDN = subscriberMSISDN;
    }

    public Date getRechargeDate() {
        return _rechargeDate;
    }

    public void setRechargeDate(Date date) {
        _rechargeDate = date;
    }

    public Date getRechargeDateTime() {
        return _rechargeDateTime;
    }

    public void setRechargeDateTime(Date dateTime) {
        _rechargeDateTime = dateTime;
    }

    public long getRechargeAmount() {
        return _rechargeAmount;
    }

    public void setRechargeAmount(long amount) {
        _rechargeAmount = amount;
    }

    public String getRechargeAmountStr() {
        return _rechargeAmountStr;
    }

    public void setRechargeAmountStr(String amountStr) {
        _rechargeAmountStr = amountStr;
    }

    public String getCreditAmountStr() {
        return _creditAmountStr;
    }

    public void setCreditAmountStr(String amountStr) {
        _creditAmountStr = amountStr;
    }

    public boolean isReconciliationFlag() {
        if (null != _reconciliationFlag && ("Y").equals(_reconciliationFlag)) {
            return true;
        } else {
            return false;
        }
    }

    public void setReconciliationFlag(String flag) {
        _reconciliationFlag = flag;
    }

    public Date getReconciliationDate() {
        return _reconciliationDate;
    }

    public void setReconciliationDate(Date date) {
        _reconciliationDate = date;
    }

    public String getReconciliationBy() {
        return _reconciliationBy;
    }

    public void setReconciliationBy(String by) {
        _reconciliationBy = by;
    }

    public Date getSettlementDate() {
        return _settlementDate;
    }

    public void setSettlementDate(Date date) {
        _settlementDate = date;
    }

    public String getSettlementFlag() {
        return _settlementFlag;
    }

    public void setSettlementFlag(String flag) {
        _settlementFlag = flag;
    }

    public boolean isSettlementReconFlag() {
        if (null != _settlementReconFlag && ("Y").equals(_settlementReconFlag)) {
            return true;
        } else {
            return false;
        }
    }

    public void setSettlementReconFlag(String reconFlag) {
        _settlementReconFlag = reconFlag;
    }

    public Date getSettlementReconDate() {
        return _settlementReconDate;
    }

    public void setSettlementReconDate(Date reconDate) {
        _settlementReconDate = reconDate;
    }

    public String getSettlementReconBy() {
        return _settlementReconBy;
    }

    public void setSettlementReconBy(String reconBy) {
        _settlementReconBy = reconBy;
    }

    public String getSettlementDateTimeStr() {
        return _settlementDateTimeStr;
    }

    public void setSettlementDateTimeStr(String dateTimeStr) {
        _settlementDateTimeStr = dateTimeStr;
    }

    public String getSettlementStatus() {
        return _settlementStatus;
    }

    public void setSettlementStatus(String status) {
        _settlementStatus = status;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String by) {
        _modifiedBy = by;
    }

    public String getRechargeDateStr() {
        return _rechargeDateStr;
    }

    public void setRechargeDateStr(String dateStr) {
        _rechargeDateStr = dateStr;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String name) {
        _productName = name;
    }

    public String getCardGroupSetID() {
        return _cardGroupSetID;
    }

    public void setCardGroupSetID(String groupSetID) {
        _cardGroupSetID = groupSetID;
    }

    public String getCardGroupID() {
        return _cardGroupID;
    }

    public void setCardGroupID(String groupID) {
        _cardGroupID = groupID;
    }

    public String getCardGroupCode() {
        return _cardGroupCode;
    }

    public void setCardGroupCode(String groupCode) {
        _cardGroupCode = groupCode;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String _version) {
        this._version = _version;
    }

    public String getErrorMessage() {
        return _errorMessage;
    }

    public void setErrorMessage(String message) {
        _errorMessage = message;
    }

    public String getPreviousBalanceStr() {
        return _previousBalanceStr;
    }

    public void setPreviousBalanceStr(String balanceStr) {
        _previousBalanceStr = balanceStr;
    }

    public String getPostBalanceStr() {
        return _postBalanceStr;
    }

    public void setPostBalanceStr(String balanceStr) {
        _postBalanceStr = balanceStr;
    }

    public String getDebitAmountStr() {
        return _debitAmountStr;
    }

    public void setDebitAmountStr(String amountStr) {
        _debitAmountStr = amountStr;
    }

    public String getSettlementAmountStr() {
        return _settlementAmountStr;
    }

    public void setSettlementAmountStr(String amountStr) {
        _settlementAmountStr = amountStr;
    }

    public long getCreditAmount() {
        return _creditAmount;
    }

    public void setCreditAmount(long amount) {
        _creditAmount = amount;
    }

    public double getLmbAmountAtIN() {
        return _lmbAmountAtIN;
    }

    public void setLmbAmountAtIN(double lmbAmountAtIN) {
        _lmbAmountAtIN = lmbAmountAtIN;
    }

    public String getLmbUpdateStatus() {
        return _lmbUpdateStatus;
    }

    public void setLmbUpdateStatus(String lmbUpdateStatus) {
        _lmbUpdateStatus = lmbUpdateStatus;
    }

    public String getOldExpiryInMillis() {
        return _oldExpiryInMillis;
    }

    public void setOldExpiryInMillis(String oldExpiryInMillis) {
        _oldExpiryInMillis = oldExpiryInMillis;
    }

    public int getRecordCount() {
        return _recordCount;
    }

    public void setRecordCount(int recordCount) {
        _recordCount = recordCount;
    }

    public String getValidityExpired() {
        return _validityExpired;
    }

    public void setValidityExpired(String expired) {
        _validityExpired = expired;
    }

    public String getSettlmntServiceType() {
        return _settlmntServiceType;
    }

    public void setSettlmntServiceType(String serviceType) {
        _settlmntServiceType = serviceType;
    }

    public String getCellId() {
        return _cellId;
    }

    public void setCellId(String id) {
        _cellId = id;
    }

    public String getSwitchId() {
        return _switchId;
    }

    public void setSwitchId(String id) {
        _switchId = id;
    }
}
