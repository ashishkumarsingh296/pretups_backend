/*
 * Created on Sep 13, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.inter.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

public class VOMSVoucherVO implements Serializable {
    static Logger _logger = Logger.getLogger(VOMSVoucherVO.class.getName());

    protected String _serialNo;
    protected String _productID;
    protected String _productConsumtionID;
    protected String _productName;
    protected long _productCode;
    protected String _pinNo;
    protected String _generationBatchNo;
    protected String _enableBatchNo;
    protected String _saleBatchNo;
    protected long _mrp;
    protected String _voucherStatus;
    protected Date _expiryDate;
    protected String _rechargeSource;
    protected String _consumedBy;
    protected Date _createdOn;
    protected Date _consumedOn;
    protected String _transactionID;
    protected String _rechargePartnerID;
    protected String _requestSource;
    protected String _requestPartnerID;
    protected long _talkTime;
    protected Date _validUpto;
    protected long _gracePeriod;
    protected long _accessFee;
    protected int _taxRate;
    protected long _taxAmount;
    protected String _partnerProductID;
    protected String _productionLocationCode;
    protected String _userLocationCode;
    protected String _productionLocationName;
    protected String _userLocationName;
    protected String _modifiedBy;
    protected Date _modifiedDate;
    protected String _msisdn;
    protected String _attribute1;
    protected Date _modifiedOn;
    private String _categoryName = null;
    private String _domainName = null;
    private String _previousStatus = null;
    private String _prevStatusModifiedBy = null;
    private String _prevStatusModifiedOn = null;
    private String _statusChangeSource = null;
    private String _enabledOn = null;
    private String _expiryDateStr = null;
    private String _consumedOnStr = null;
    private String _validUptoStr = null;
    private ArrayList _voucherAuditList = null;
    private String _lastErrorMessage = null;
    private int _count = 0;
    private int _enableCount = 0;
    private int _stDamageCount = 0;
    private int _stDamageCountAfterEn = 0;
    private int _consumptionCount = 0;
    private String _toSerialNo = null; // used for writing in file
    private String _process = null; // used for writing in file
    private String _option = null;
    private String _categoryType = null;
    private String _serviceCode = null;
    private int _noOfArguments = 0;
    private String _noOfArgumentsStr;
    private int _validity = 0;
    protected int _attemptAllowed = 0;
    protected int _attemptUsed = 0;
    protected int _attemptNo = 0;
    protected long _totalValueAllowed = 0;
    protected long _totalValueUsed = 0;
    protected long _oneTimeAmount = 0;
    protected int _usageAllowedDays = 0;
    protected Date _useBeforeDate;
    protected Date _firstConsumedOn;
    protected String _firstConsumedBy;
    protected String _lastConsumedBy;
    protected Date _lastConsumedOn;
    protected String _oneTimeUsage;
    protected String _purposeID;
    protected long _previousBalance;
    protected long _newBalance;
    protected String _requestedBy;
    protected long _defaultValue;
    protected long _onetimeUsageValue;
    protected String _onetimeUsageString;
    protected String _voucherAttemptType;
    protected int _lastAttemptNo;
    protected String _multipleConsumedBy;
    protected String _voucherUpdated;
    /* code added by kamini for multiuse */
    protected ArrayList _voucherUsage = null;
    protected long _valueUsed = 0;
    protected String _lastConsumedOption = null;
    protected String _lastconsumedOnStr = null;
    protected String _lastUserLocationName = null;
    protected String _consumeBeforeStr = null;
    protected String _modifiedOnstr = null;
    protected String _createdOnStr = null;
    protected String _attemptType = null;
    protected String _firstConsumedOnStr = null;
    protected String _usageString = null;
    protected String _status = null;
    protected int _noOfRequests = 0;
    protected int _lastRequestAttemptNo = 0;
    protected String _oneTimeUsageAllowed = null;
    protected int _transactionStatus;
    private int _currentAttemptNo;
    private String _preStatusName = null;
    private String _currentStatusName = null;
    private String _currentStatus = null;

    // Added by amit ruwali
    private long _maxReqQuantity;
    private long _minReqQuantity;
    private String _statusChangePartnerID = null;
    private String _batchNo = null;
    private String _message = null;
    private String _processStatus = null;
    
    private String soldStatus = null;
    private long _payableAmount;

    public VOMSVoucherVO()

    {
        super();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" _currentStatus=" + _currentStatus);
        sb.append(" _modifiedBy=" + _modifiedBy);
        sb.append(" _podifiedOn=" + _modifiedOn);
        sb.append(" _previousStatus=" + _previousStatus);
        sb.append(" _odifiedBy=" + _modifiedBy);
        sb.append(" _odifiedBy=" + _modifiedBy);
        sb.append(" _odifiedBy=" + _modifiedBy);
        sb.append(" _odifiedBy=" + _modifiedBy);
        sb.append("_modifiedOn=+_modifiedOn");
        sb.append("_getPreviousStatus=+_modifiedOn");
        sb.append("_getSerialNo=+_getSerialNo");
        sb.append("_userLocationCode=+__userLocationCode");

        return sb.toString();
    }

    /**
     * @return Returns the batchNo.
     */
    public String getBatchNo() {
        return _batchNo;
    }

    /**
     * @param batchNo
     *            The batchNo to set.
     */
    public void setBatchNo(String batchNo) {
        _batchNo = batchNo;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @param message
     *            The message to set.
     */
    public void setMessage(String message) {
        _message = message;
    }

    /**
     * @return Returns the processStatus.
     */
    public String getProcessStatus() {
        return _processStatus;
    }

    /**
     * @param processStatus
     *            The processStatus to set.
     */
    public void setProcessStatus(String processStatus) {
        _processStatus = processStatus;
    }

    /**
     * @return Returns the statusChangePartnerID.
     */
    public String getStatusChangePartnerID() {
        return _statusChangePartnerID;
    }

    /**
     * @param statusChangePartnerID
     *            The statusChangePartnerID to set.
     */
    public void setStatusChangePartnerID(String statusChangePartnerID) {
        _statusChangePartnerID = statusChangePartnerID;
    }

    /**
     * @return Returns the maxReqQuantity.
     */

    public long getMaxReqQuantity() {
        return _maxReqQuantity;
    }

    /**
     * @param maxReqQuantity
     *            The maxReqQuantity to set.
     */
    public void setMaxReqQuantity(long maxReqQuantity) {
        _maxReqQuantity = maxReqQuantity;
    }

    /**
     * @return Returns the minReqQuantity.
     */
    public long getMinReqQuantity() {
        return _minReqQuantity;
    }

    /**
     * @param minReqQuantity
     *            The minReqQuantity to set.
     */
    public void setMinReqQuantity(long minReqQuantity) {
        _minReqQuantity = minReqQuantity;
    }

    public java.lang.String getSerialNo() {
        return _serialNo;
    }

    public java.lang.String getProductID() {
        return _productID;
    }

    public java.lang.String getPinNo() {
        return _pinNo;
    }

    public String getGenerationBatchNo() {
        return _generationBatchNo;
    }

    public String getEnableBatchNo() {
        return _enableBatchNo;
    }

    public String getSaleBatchNo() {
        return _saleBatchNo;
    }

    public long getMRP() {
        return _mrp;
    }

    public java.lang.String getVoucherStatus() {
        return _voucherStatus;
    }

    public Date getExpiryDate() {
        return _expiryDate;
    }

    public java.lang.String getRechargeSource() {
        return _rechargeSource;
    }

    public String getConsumedBy() {
        return _consumedBy;
    }

    public Date getConsumedOn() {
        return _consumedOn;
    }

    public String getTransactionID() {
        return _transactionID;
    }

    public String getRechargePartnerID() {
        return _rechargePartnerID;
    }

    public String getRequestSource() {
        return _requestSource;
    }

    public String getRequestPartnerID() {
        return _requestPartnerID;
    }

    public long getTalkTime() {
        return _talkTime;
    }

    public Date getValidUpto() {
        return _validUpto;
    }

    public long getGracePeriod() {
        return _gracePeriod;
    }

    public int getTaxRate() {
        return _taxRate;
    }

    public long getTaxAmount() {

        return _taxAmount;
    }

    public String getPartnerProductID() {
        return _partnerProductID;

    }

    public String getProductionLocationCode() {

        return _productionLocationCode;
    }

    public String getUserLocationCode() {

        return _userLocationCode;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public java.util.Date getModifiedDate() {
        return _modifiedDate;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setSerialNo(java.lang.String p_serialNo) {
        _serialNo = p_serialNo;
    }

    public void setProductID(java.lang.String p_productID) {
        _productID = p_productID;
    }

    public void setPinNo(java.lang.String p_pinNo) {
        _pinNo = p_pinNo;
    }

    public void setGenerationBatchNo(String p_generationBatchNo) {
        _generationBatchNo = p_generationBatchNo;
    }

    public void setEnableBatchNo(String p_enableBatchNo) {
        _enableBatchNo = p_enableBatchNo;
    }

    public void SetSaleBatchNo(String p_saleBatchNo) {
        _saleBatchNo = p_saleBatchNo;
    }

    public void setMRP(long p_mrp) {
        _mrp = p_mrp;
    }

    public void setVoucherStatus(java.lang.String p_status) {
        _voucherStatus = p_status;
    }

    public void setExpiryDate(java.util.Date p_expiryDate) {
        _expiryDate = p_expiryDate;
    }

    public void setRechargeSource(java.lang.String p_rechargeSource) {
        _rechargeSource = p_rechargeSource;
    }

    public void setConsumedBy(String p_consumedBy) {
        _consumedBy = p_consumedBy;
    }

    public void setConsumedOn(java.util.Date p_consumedOn) {
        _consumedOn = p_consumedOn;
    }

    public void setTransactionID(String p_transactionID) {
        _transactionID = p_transactionID;
    }

    public void setRechargePartnerID(String p_rechargePartnerID) {
        _rechargePartnerID = p_rechargePartnerID;
    }

    public void setRequestSource(String p_requestSource) {
        _requestSource = p_requestSource;
    }

    public void setRequestPartnerID(String p_requestPartnerID) {
        _requestPartnerID = p_requestPartnerID;
    }

    public void setTalkTime(long p_talkTime) {
        _talkTime = p_talkTime;
    }

    public void SetValidUpto(java.util.Date p_validUpto) {
        _validUpto = p_validUpto;
    }

    public void setGracePeriod(long p_gracePeriod) {
        _gracePeriod = p_gracePeriod;
    }

    public void setTaxRate(int p_taxRate) {
        _taxRate = p_taxRate;
    }

    public void setTaxAmount(long p_taxAmount) {
        _taxAmount = p_taxAmount;
    }

    public void setPartnerProductID(String p_partnerProductID) {
        _partnerProductID = p_partnerProductID;
    }

    public void setProductionLocationCode(String p_productionLocationCode) {

        _productionLocationCode = p_productionLocationCode;
    }

    public void setUserLocationCode(String p_userLocationCode) {
        _userLocationCode = p_userLocationCode;
    }

    public void setModifiedBy(String p_modifiedBy) {
        _modifiedBy = p_modifiedBy;
    }

    public void setModifiedDate(java.util.Date p_modifiedDate) {
        _modifiedDate = p_modifiedDate;
    }

    public void setMsisdn(String p_msisdn) {
        _msisdn = p_msisdn;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String string) {
        _productName = string;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date date) {
        _modifiedOn = date;
    }

    public String getProductionLocationName() {
        return _productionLocationName;
    }

    public String getUserLocationName() {
        return _userLocationName;
    }

    public void setProductionLocationName(String string) {
        _productionLocationName = string;
    }

    public void setUserLocationName(String string) {
        _userLocationName = string;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public String getDomainName() {
        return _domainName;
    }

    public String getEnabledOn() {
        return _enabledOn;
    }

    public String getPreviousStatus() {
        return _previousStatus;
    }

    public String getPrevStatusModifiedBy() {
        return _prevStatusModifiedBy;
    }

    public String getPrevStatusModifiedOn() {
        return _prevStatusModifiedOn;
    }

    public String getStatusChangeSource() {
        return _statusChangeSource;
    }

    public void setCategoryName(String string) {
        _categoryName = string;
    }

    public void setDomainName(String string) {
        _domainName = string;
    }

    public void setEnabledOn(String string) {
        _enabledOn = string;
    }

    public void setPreviousStatus(String string) {
        _previousStatus = string;
    }

    public void setPrevStatusModifiedBy(String string) {
        _prevStatusModifiedBy = string;
    }

    public void setPrevStatusModifiedOn(String string) {
        _prevStatusModifiedOn = string;
    }

    public void setStatusChangeSource(String string) {
        _statusChangeSource = string;
    }

    public String getExpiryDateStr() {
        return _expiryDateStr;
    }

    public void setExpiryDateStr(String string) {
        _expiryDateStr = string;
    }

    public String getConsumedOnStr() {
        return _consumedOnStr;
    }

    public void setConsumedOnStr(String string) {
        _consumedOnStr = string;
    }

    public String getValidUptoStr() {
        return _validUptoStr;
    }

    public void setValidUptoStr(String string) {
        _validUptoStr = string;
    }

    /**
     * @return
     */
    public ArrayList getVoucherAuditList() {
        return _voucherAuditList;
    }

    /**
     * @param list
     */
    public void setVoucherAuditList(ArrayList list) {
        _voucherAuditList = list;
    }

    /**
     * @return
     */
    public String getLastErrorMessage() {
        return _lastErrorMessage;
    }

    /**
     * @param string
     */
    public void setLastErrorMessage(String string) {
        _lastErrorMessage = string;
    }

    /**
     * @return
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param string
     */
    public void setCreatedOn(Date date) {
        _createdOn = date;
    }

    /**
     * @return
     */
    public int getCount() {
        return _count;
    }

    /**
     * @param i
     */
    public void setCount(int i) {
        _count = i;
    }

    /**
     * @return
     */
    public int getEnableCount() {
        return _enableCount;
    }

    /**
     * @param i
     */
    public void setEnableCount(int i) {
        _enableCount = i;
    }

    /**
     * @return
     */
    public String getToSerialNo() {
        return _toSerialNo;
    }

    /**
     * @param string
     */
    public void setToSerialNo(String string) {
        _toSerialNo = string;
    }

    /**
     * @return
     */
    public long getProductCode() {
        return _productCode;
    }

    /**
     * @param l
     */
    public void setProductCode(long l) {
        _productCode = l;
    }

    /**
     * @return
     */
    public int getStDamageCount() {
        return _stDamageCount;
    }

    /**
     * @return
     */
    public int getStDamageCountAfterEn() {
        return _stDamageCountAfterEn;
    }

    /**
     * @param i
     */
    public void setStDamageCount(int i) {
        _stDamageCount = i;
    }

    /**
     * @param i
     */
    public void setStDamageCountAfterEn(int i) {
        _stDamageCountAfterEn = i;
    }

    /**
     * @return
     */
    public int getConsumptionCount() {
        return _consumptionCount;
    }

    /**
     * @param i
     */
    public void setConsumptionCount(int i) {
        _consumptionCount = i;
    }

    /**
     * @return
     */
    public String getProcess() {
        return _process;
    }

    /**
     * @param string
     */
    public void setProcess(String string) {
        _process = string;
    }

    /**
     * @return
     */
    public String getOption() {
        return _option;
    }

    /**
     * @param string
     */
    public void setOption(String string) {
        _option = string;
    }

    /**
     * @return
     */
    public String getCategoryType() {
        return _categoryType;
    }

    /**
     * @param string
     */
    public void setCategoryType(String string) {
        _categoryType = string;
    }

    /**
     * @return
     */
    public int getNoOfArguments() {
        return _noOfArguments;
    }

    /**
     * @return
     */
    public String getServiceCode() {
        return _serviceCode;
    }

    /**
     * @param i
     */
    public void setNoOfArguments(int i) {
        _noOfArguments = i;
    }

    /**
     * @param string
     */
    public void setServiceCode(String string) {
        _serviceCode = string;
    }

    /**
     * @return
     */
    public int getValidity() {
        return _validity;
    }

    /**
     * @param i
     */
    public void setValidity(int i) {
        _validity = i;
    }

    /**
     * @return
     */
    public int getAttemptAllowed() {
        return _attemptAllowed;
    }

    /**
     * @return
     */
    public int getAttemptUsed() {
        return _attemptUsed;
    }

    /**
     * @return
     */
    public long getOneTimeAmount() {
        return _oneTimeAmount;
    }

    /**
     * @return
     */
    public long getTotalValueAllowed() {
        return _totalValueAllowed;
    }

    /**
     * @return
     */
    public long getTotalValueUsed() {
        return _totalValueUsed;
    }

    /**
     * @return
     */
    public Date getUseBeforeDate() {
        return _useBeforeDate;
    }

    /**
     * @param i
     */
    public void setAttemptAllowed(int i) {
        _attemptAllowed = i;
    }

    /**
     * @param i
     */
    public void setAttemptUsed(int i) {
        _attemptUsed = i;
    }

    /**
     * @param l
     */
    public void setOneTimeAmount(long l) {
        _oneTimeAmount = l;
    }

    /**
     * @param l
     */
    public void setTotalValueAllowed(long l) {
        _totalValueAllowed = l;
    }

    /**
     * @param l
     */
    public void setTotalValueUsed(long l) {
        _totalValueUsed = l;
    }

    /**
     * @param date
     */
    public void setUseBeforeDate(Date date) {
        _useBeforeDate = date;
    }

    /**
     * @return
     */
    public String getProductConsumtionID() {
        return _productConsumtionID;
    }

    /**
     * @param string
     */
    public void setProductConsumtionID(String string) {
        _productConsumtionID = string;
    }

    /**
     * @return
     */
    public Date getFirstConsumedOn() {
        return _firstConsumedOn;
    }

    /**
     * @param date
     */
    public void setFirstConsumedOn(Date date) {
        _firstConsumedOn = date;
    }

    /**
     * @return
     */
    public String getLastConsumedBy() {
        return _lastConsumedBy;
    }

    /**
     * @return
     */
    public Date getLastConsumedOn() {
        return _lastConsumedOn;
    }

    /**
     * @param string
     */
    public void setLastConsumedBy(String string) {
        _lastConsumedBy = string;
    }

    /**
     * @param date
     */
    public void setLastConsumedOn(Date date) {
        _lastConsumedOn = date;
    }

    /**
     * @return
     */
    public String getOneTimeUsage() {
        return _oneTimeUsage;
    }

    /**
     * @param string
     */
    public void setOneTimeUsage(String string) {
        _oneTimeUsage = string;
    }

    /**
     * @return
     */
    public String getPurposeID() {
        return _purposeID;
    }

    /**
     * @param string
     */
    public void setPurposeID(String string) {
        _purposeID = string;
    }

    /**
     * @return
     */
    public int getAttemptNo() {
        return _attemptNo;
    }

    /**
     * @param i
     */
    public void setAttemptNo(int i) {
        _attemptNo = i;
    }

    /**
     * @return
     */
    public String getRequestedBy() {
        return _requestedBy;
    }

    /**
     * @param string
     */
    public void setRequestedBy(String string) {
        _requestedBy = string;
    }

    /**
     * @return
     */
    public long getNewBalance() {
        return _newBalance;
    }

    /**
     * @return
     */
    public long getPreviousBalance() {
        return _previousBalance;
    }

    /**
     * @param l
     */
    public void setNewBalance(long l) {
        _newBalance = l;
    }

    /**
     * @param l
     */
    public void setPreviousBalance(long l) {
        _previousBalance = l;
    }

    /**
     * @return
     */
    public long getDefaultValue() {
        return _defaultValue;
    }

    /**
     * @param l
     */
    public void setDefaultValue(long l) {
        _defaultValue = l;
    }

    /**
     * @return
     */
    public int getUsageAllowedDays() {
        return _usageAllowedDays;
    }

    /**
     * @param i
     */
    public void setUsageAllowedDays(int i) {
        _usageAllowedDays = i;
    }

    /**
     * @return
     */
    public long getOnetimeUsageValue() {
        return _onetimeUsageValue;
    }

    /**
     * @param l
     */
    public void setOnetimeUsageValue(long l) {
        _onetimeUsageValue = l;
    }

    /**
     * @return
     */
    public String getOnetimeUsageString() {
        return _onetimeUsageString;
    }

    /**
     * @param string
     */
    public void setOnetimeUsageString(String string) {
        _onetimeUsageString = string;
    }

    /**
     * @return
     */
    public String getNoOfArgumentsStr() {
        return _noOfArgumentsStr;
    }

    /**
     * @param string
     */
    public void setNoOfArgumentsStr(String string) {
        _noOfArgumentsStr = string;
    }

    /**
     * @return
     */
    public String getVoucherAttemptType() {
        return _voucherAttemptType;
    }

    /**
     * @param string
     */
    public void setVoucherAttemptType(String string) {
        _voucherAttemptType = string;
    }

    /**
     * @return
     */
    public int getLastAttemptNo() {
        return _lastAttemptNo;
    }

    /**
     * @param i
     */
    public void setLastAttemptNo(int i) {
        _lastAttemptNo = i;
    }

    /**
     * @return
     */
    public String getFirstConsumedBy() {
        return _firstConsumedBy;
    }

    /**
     * @param string
     */
    public void setFirstConsumedBy(String string) {
        _firstConsumedBy = string;
    }

    /**
     * @return
     */
    public String getMultipleConsumedBy() {
        return _multipleConsumedBy;
    }

    /**
     * @param string
     */
    public void setMultipleConsumedBy(String string) {
        _multipleConsumedBy = string;
    }

    /**
     * @return
     */
    public String getVoucherUpdated() {
        return _voucherUpdated;
    }

    /**
     * @param string
     */
    public void setVoucherUpdated(String string) {
        _voucherUpdated = string;
    }

    /**
     * @return
     */
    public ArrayList getVoucherUsage() {
        return _voucherUsage;
    }

    /**
     * @param list
     */
    public void setVoucherUsage(ArrayList list) {
        _voucherUsage = list;
    }

    /**
     * @param list
     */
    public long getValueUsed() {
        return _valueUsed;
    }

    /**
     * @param list
     */
    public void setValueUsed(long vUsed) {
        _valueUsed = vUsed;
    }

    /**
     * @param list
     */
    public String getLastConsumedOption() {
        return _lastConsumedOption;
    }

    /**
     * @param list
     */
    public void setLastConsumedOption(String lConOption) {
        _lastConsumedOption = lConOption;
    }

    public String getLastConsumedOnStr() {
        return _lastconsumedOnStr;
    }

    public void setLastConsumedOnStr(String string) {
        _lastconsumedOnStr = string;
    }

    public String getLastUserLocationName() {
        return _lastUserLocationName;
    }

    public void setLastUserLocationName(String string) {
        _lastUserLocationName = string;
    }

    /**
     * @return
     */
    public String getAttemptType() {
        return _attemptType;
    }

    /**
     * @return
     */
    public String getConsumeBeforeStr() {
        return _consumeBeforeStr;
    }

    /**
     * @return
     */
    public String getCreatedOnStr() {
        return _createdOnStr;
    }

    /**
     * @return
     */
    public String getFirstConsumedOnStr() {
        return _firstConsumedOnStr;
    }

    /**
     * @return
     */
    public String getModifiedOnstr() {
        return _modifiedOnstr;
    }

    /**
     * @param string
     */
    public void setAttemptType(String string) {
        _attemptType = string;
    }

    /**
     * @param string
     */
    public void setConsumeBeforeStr(String string) {
        _consumeBeforeStr = string;
    }

    /**
     * @param string
     */
    public void setCreatedOnStr(String string) {
        _createdOnStr = string;
    }

    /**
     * @param string
     */
    public void setFirstConsumedOnStr(String string) {
        _firstConsumedOnStr = string;
    }

    /**
     * @param string
     */
    public void setModifiedOnstr(String string) {
        _modifiedOnstr = string;
    }

    /**
     * @return
     */
    public String getUsageString() {
        return _usageString;
    }

    /**
     * @param string
     */
    public void setUsageString(String string) {
        _usageString = string;
    }

    /**
     * @return
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param string
     */
    public void setStatus(String string) {
        _status = string;
    }

    /**
     * @return
     */
    public int getNoOfRequests() {
        return _noOfRequests;
    }

    /**
     * @param i
     */
    public void setNoOfRequests(int i) {
        _noOfRequests = i;
    }

    /**
     * @return
     */
    public int getLastRequestAttemptNo() {
        return _lastRequestAttemptNo;
    }

    /**
     * @param i
     */
    public void setLastRequestAttemptNo(int i) {
        _lastRequestAttemptNo = i;
    }

    /**
     * @return
     */
    public String getOneTimeUsageAllowed() {
        return _oneTimeUsageAllowed;
    }

    /**
     * @param string
     */
    public void setOneTimeUsageAllowed(String string) {
        _oneTimeUsageAllowed = string;
    }

    /**
     * @return
     */
    public int getTransactionStatus() {
        return _transactionStatus;
    }

    /**
     * @param int
     */
    public void setTransactionStatus(int p_int) {
        _transactionStatus = p_int;
    }

    /**
     * @return
     */
    public int getCurrentAttemptNo() {
        return _currentAttemptNo;
    }

    /**
     * @param i
     */
    public void setCurrentAttemptNo(int i) {
        _currentAttemptNo = i;
    }

    /**
     * @return Returns the currentStatusName.
     */
    public String getCurrentStatusName() {
        return _currentStatusName;
    }

    /**
     * @param currentStatusName
     *            The currentStatusName to set.
     */
    public void setCurrentStatusName(String currentStatusName) {
        _currentStatusName = currentStatusName;
    }

    /**
     * @return Returns the preStatusName.
     */
    public String getPreStatusName() {
        return _preStatusName;
    }

    /**
     * @param preStatusName
     *            The preStatusName to set.
     */
    public void setPreStatusName(String preStatusName) {
        _preStatusName = preStatusName;
    }

    public String getCurrentStatus() {
        return _currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        _currentStatus = currentStatus;
    }

    /**
     * @return Returns the payableAmount.
     */
    public long getPayableAmount() {
        return this._payableAmount;
    }

    /**
     * @param payableAmount
     *            The payableAmount to set.
     */
    public void setPayableAmount(long payableAmount) {
        this._payableAmount = payableAmount;
    }
    
    public String getSoldStatus() {
        return soldStatus;
    }

    public void setSoldStatus(String soldStatus) {
    	this.soldStatus = soldStatus;
    }
}
