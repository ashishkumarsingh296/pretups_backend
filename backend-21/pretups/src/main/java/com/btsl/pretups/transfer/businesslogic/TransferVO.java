package com.btsl.pretups.transfer.businesslogic;

/*
 * @(#)TransferVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/06/2005 Initial Creation
 * Abhijit 10/08/2006 Modification for ID=SUBTYPVALRECLMT
 * -----------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class TransferVO implements Serializable {

    private Log _log = LogFactory.getLog(TransferVO.class.getName());
    private String _module;
    private String _productCode;
    private String _productShortCode;
    private String _gatewayName;
    private String _productName;
    private Object _senderVO = null;
    private Object _receiverVO = null;
    private String _subscriberType;
    private long _transferValue;
    private Date _transferDate;
    private Date _transferDateTime;
    private String _transferDisplayDateTime; // for displaying date & time in
    // jsp page
    private String _errorCode;
    private String _transferStatus;
    private String _referenceID;
    private String _pinSentToMsisdn;
    private String _language;
    private long _skey;
    private Date _skeyGenerationTime;
    private String _skeySentToMsisdn;
    private String _requestThroughQueue;
    private String _creditBackStatus;
    private String _reconciliationFlag;
    private Date _reconciliationDate;
    private String _reconciliationDisplayDate;
    private String _reconciliationBy;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _instanceID;
    private String _requestGatewayCode;
    private String _requestGatewayType;
    private String _networkName;
    private int _radioIndex;
    private String _toDate;
    private String _fromDate;
    private String _sourceType;
    private String _productType;

    private String _transferID;
    private long _paymentValue; // Payment value from subscriber to the sender
    private String _paymentMethodType; // Payment method type to be used in
    // request
    private String _serviceType; // Service type to be used in request
    private String _differentialApplicable; // Differential Applicable in the
    // transfer
    private long _creditAmount; // Amount to be credited in case of failure
    private long _quantity; // Quantity Requested
    private String _country; // Country code
    private String _defaultPaymentMethod; // flag indicating whether Payment
    // method is default
    private String _cardGroupSetID;
    private String _cardGroupID;
    private String _cardGroupCode;
    private String _cardGroupSetName;

    private String _version;

    private String _buddy;

    private String _receiverTax1Type;
    private double _receiverTax1Rate;
    private long _receiverTax1Value;
    private double _receiverTax2Rate;
    private String _receiverTax2Type;
    private long _receiverTax2Value;
    private long _receiverGracePeriod;
    private long _receiverBonusValue;
    private int _receiverValidity;
    private int _receiverBonusValidity;
    private long _receiverAccessFee;
    private long _senderTransferValue;
    private long _receiverTransferValue;
    private Date _validityDateToBeSet;
    private String _receiverValPeriodType;
    private String _receiverMsisdn;
    private String _senderMsisdn;

    private long _queueAdditionTime;
    private String _messageCode;
    private String[] _messageArguments; // arguments send with the message
    private String _networkCode;
    private String _receiverNetworkCode;
    private String _senderID;

    private String _requestID;
    private String _receiverTax3Type;
	private double _receiverTax3Rate;
	private long _receiverTax3Value;
	private double _receiverTax4Rate;
	private String _receiverTax4Type;
	private long _receiverTax4Value;

    private ArrayList _transferItemList; // List of the Items in the transfer
    // request
    private String _senderTax1Type;
    private double _senderTax1Rate;
    private long _senderTax1Value;
    private double _senderTax2Rate;
    private String _senderTax2Type;
    private long _senderTax2Value;
    private long _senderAccessFee;

    private long _requestedAmount;

    private boolean _underProcessMsgReq;
    private String _senderReturnMessage;
    private Locale _locale;
    private String _msgGatewayFlowType;
    private String _msgGatewayResponseType;
    private long _msgGatewayTimeOutValue;
    private Object _receiverReturnMsg = null;

    // added by sandeep for enquiry jsp
    private String _transferValueStr;
    private String _transferDateStr;
    private String _senderName;
    private String _serviceName;
    private String _errorMessage;

    private String _incomingSmsStr;
    private String _senderAllServiceClassID;
    private String _receiverAllServiceClassID;
    private String _subService;
    private long _minCardGroupAmount; // Min amount in card group range
    private String _differentialAllowedForService;
    private String _giveOnlineDifferential;

    private RequestVO _requestVO = null;
    private TransferItemVO _senderTransferItemVO = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private long _requestStartTime;
    private long _senderPostBalance;
    private long _receiverPostBalance;
    private String _msisdnNewExpiryStr;
    private String _msisdnPreviousExpiryStr;
    private Date _msisdnNewExpiry;
    private Date _msisdnPreviousExpiry;
    private String _transferCategory;
    private String _receiverSubscriberType;// this variable will be used for
    // ID=SUBTYPVALRECLMT. This field
    // will be used in
    // PretupsBL(validateReceiverLimit)
    // to distinguish the receiver
    // subscriber type is either PRE or
    // POST

    private String _txnStatus = null; // this variable is used to store the code
    // (e.g. 250) but _transferStatus
    private Object _otherInfo1 = null;
    private Object _otherInfo2 = null;

    private String _senderInterfaceStatusType;
    private String _receiverInterfaceStatusType;

    private String _serialNumber; // Added for EVD/EVR process
    private PaymentMethodKeywordVO _paymentMethodKeywordVO;
    private String _lastTransferId = null; // added for MVD

    private double _convertedRequestedAmount;
    // added by PN for cellplus controller
    private int _validityDaysToExtend;
    private String _feeForValidityExtentionStr = null;
    private long _feeForValidityExtention;
    private String _valExtTransferID;
    private double _receiverBonus1;
    private double _receiverBonus2;

    // Gift recharge
    private Object _gifterReturnMsg = null;

    private String _underProcessCheckReqd = TypesI.YES;
    private long _bonusTalkTimeValue;
    private long _calminusBonusvalue;

    // added for card group slab suspend/resume
    private String _status;
    // added by vikask for new cardgroup adding
    private long _receiverBonus1Validity;
    private long _receiverBonus2Validity;
    private long _receiverCreditBonusValidity;
    // added by amit
    private String _online;
    private String _both;
    private String _extCreditIntfceType;

    // Addedd for bonus bundle in card group.
    private double _senderConvFactor;
    private double _receiverConvFactor;
    private String _bonusBundleIdS = null;
    private String _bonusBundleValidities = null;
    private String _bonusBundleValues = null;
    private String _bonusBundleTypes = null;
    private String _selectorBundleId = null;
    private String _selectorBundleType = null;
    private String _selectorCode = null;
    private Object _bonusItems = null;
    private String _bonusSummarySting = null;

    private String _bonusBundleNames = null;
    private String _bonusBundleCode = null;
    private String _bonusBundleRate = null;
    private String _receiverBundleID;
    private String _activeUserId = null;
    private String _type = null;
    private String _bonusSummaryMessageSting = null;
    // Added for LMB

    private String _value;
    // added for SOS recharge service
    private long _senderSettlementValue;
    private Date _lastTransferDateTime;
    private double _senderAccessFeeRate;
    private String _senderAccessFeeType;
    private String _lmbCreditUpdateStatus;
    // added by nilesh:for MRP block time
    private long _lastMRP = 0;
    private String _lastServiceType = null;
    // vastrix
    private String _cosRequired;
    private double _inPromo;
    private String _newCos;
    private String _rechargeComment;

    // For Service Provider
    private String _serviceProviderName;
    private String _receiverServiceProviderName;
    // For Reversal
    private String _tempId = null;
    private String _subscriberStatus = null;
    private String _combinedKey = null;
    private String _serviceClassCode;
    private String _serviceClass;
    private String _reversalPermitted=null;

  
	//
    private String _cellId;
    private String _switchId;

    private String _previousPromoBalance = null;
    private String _newPromoBalance = null;
    private String _previousPromoExpiry = null;
    private String _newPromoExpiry = null;

    // Added for reducing Db hits

    // added by gaurav for PROMO and COS
    private String _valInterfaceID;
    private String _interfaceValResponseCode;
    private String _valProtocolStatus;
    private String _promoStatus;
    private String _interfacePromoStatus;
    private String _cosStatus;
    private String _newServiceClssCode;
    private String _previousPromoExpiryInCal;
    private String _previousExpiryInCal;
    private long _postCreditPromoBalance;
    private Date _postCreditPromoValidity;
    private String _interfaceCosStatus;
    private Date _postCreditCoreValidity;
    private long _postCreditCoreBalance;
    private Date _previousExpiry;
    private Date _newGraceDate;

    private Date _newExpiry;
    private long _previousBalance;
    private long _postBalance;
    private String _postValidationStatus;
    // VFE 6 CR
    private String _info1 = null;
    private String _info2 = null;
    private String _info3 = null;
    private String _info4 = null;
    private String _info5 = null;
    private String _info6 = null;
    private String _info7 = null;
    private String _info8 = null;
    private String _info9 = null;
    private String _info10 = null;
    private Date _oldtransferDateTime;

    private String _dummyServiceType = null;
    // sonali garg
    private String _maxCardGroupSlabAmount; // Max amount in card group range
    private String _minCardGroupSlabAmount; // Min amount in card group range
    
    private String _gradeCode;
    private String _categoryCode;
    private String _interfaceReferenceId;
    private String voucherType;
    private String voucherSegment;
	private String productId;
	private String voucherQuantity = null;
	
	private String txnBatchId = null; //txnBatchId, added for multiple-DVD-process
	
    // private HashMap<String, ServiceClassVO> _serviceClassMap=null;
	private boolean isInGeoFencing = true;

    public String toString() {
        final StringBuilder sbd = new StringBuilder();
        sbd.append("_module  =").append(_module);
        sbd.append(",_senderMsisdn  =").append(_senderMsisdn);
        sbd.append(",_cardGroupCode =").append(_cardGroupCode);
        sbd.append(",_creditAmount =").append(_creditAmount);
        sbd.append(",_defaultPaymentMethod =").append(_defaultPaymentMethod);
        sbd.append(",_errorCode =").append(_errorCode);
        sbd.append(",_instanceID =").append(_instanceID);
        sbd.append(",_messageCode =").append(_messageCode);
        sbd.append(",_networkCode =").append(_networkCode);
        sbd.append(",_productCode =").append(_productCode);
        sbd.append(",_quantity =").append(_quantity);
        sbd.append(", _receiverMsisdn =").append(_receiverMsisdn);
        sbd.append(",_receiverTransferValue =").append(_receiverTransferValue);
        sbd.append(",_receiverValidity =").append(_receiverValidity);
        sbd.append(",_requestedAmount =").append(_requestedAmount);
        sbd.append(",_senderTransferValue =").append(_senderTransferValue);
        sbd.append(",_subService =").append(_subService);
        sbd.append(",_senderTransferItemVO =").append(_senderTransferItemVO);
        sbd.append(",_receiverTransferItemVO =").append(_receiverTransferItemVO);
        sbd.append(",_transferID =").append(_transferID);
        sbd.append(",_transferCategory =").append(_transferCategory);
        sbd.append(",_receiverSubscriberType =").append(_receiverSubscriberType);
        sbd.append(",_transferStatus =").append(_transferStatus);
        sbd.append(", _transferValueStr =").append(_transferValueStr);
        sbd.append(",_transferItemList =").append(_transferItemList);
        sbd.append(",_paymentMethodKeywordVO =").append(_paymentMethodKeywordVO);
        sbd.append(",_lastTransferId =").append(_lastTransferId);
        sbd.append(", _receiverBonus1 =").append(_receiverBonus1);
        sbd.append(", _receiverBonus2 =").append(_receiverBonus2);
        sbd.append(",_bonusTalkTimeValue=").append(_bonusTalkTimeValue);
        sbd.append(",_calminusBonusvalue=").append(_calminusBonusvalue);
        sbd.append(",_status=").append(_status);
        sbd.append(", _receiverCreditBonusValidity =").append(_receiverCreditBonusValidity);
        sbd.append(", _onLine =").append(_online);
        sbd.append(", _both =").append(_both);
        sbd.append(", _extCreditIntfceType").append(_extCreditIntfceType);
        sbd.append(", _senderConvFactor=").append(_senderConvFactor);
        sbd.append(", _receiverConvFactor=").append(_receiverConvFactor);
        // Added for bonus bundles
        sbd.append(", _bonusBundleIdS=").append(_bonusBundleIdS);
        sbd.append(", _bonusBundleValidities=").append(_bonusBundleValidities);
        sbd.append(",  _bonusBundleValues=").append(_bonusBundleValues);
        sbd.append(", _bonusBundleTypes=").append(_bonusBundleTypes);
        sbd.append(", _selectorBundleId=").append(_selectorBundleId);
        sbd.append(", _selectorBundleType=").append(_selectorBundleType);
        sbd.append(", _selectorCode=").append(_selectorCode);
        sbd.append(", _bonusSummarySting=").append(_bonusSummarySting);
        sbd.append(", _bonusSummaryMessageSting=").append(_bonusSummaryMessageSting);
        sbd.append(", _lmbCreditUpdateStatus =").append(_lmbCreditUpdateStatus);
        // added by nilesh:: for MRP block time
        sbd.append(", _lastServiceType =").append(_lastServiceType);
        sbd.append(", _inPromo =").append(_inPromo);
        sbd.append(", _cosRequired =").append(_cosRequired);
        sbd.append(", _categoryCode =").append(_categoryCode);
        sbd.append(", _gradeCode =").append(_gradeCode);
        sbd.append(", _senderPostBalance ="+_senderPostBalance);
        sbd.append(", _receiverPostBalance ="+_receiverPostBalance);
        sbd.append(", _interfaceReferenceId ="+_interfaceReferenceId);
		sbd.append(", voucherType ="+voucherType);
        sbd.append(", voucherSegment ="+voucherSegment);
        sbd.append(", productId ="+productId);
        sbd.append(", txnBatchId ="+txnBatchId);
		sbd.append(", isInGeoFencing ="+isInGeoFencing);
        return sbd.toString();
    }

    /**
     * @return the _cellId
     */
    public String getCellId() {
        return _cellId;
    }

    /**
     * @param id
     *            the _cellId to set
     */
    public void setCellId(String id) {
        _cellId = id;
    }

    /**
     * @return the _switchId
     */
    public String getSwitchId() {
        return _switchId;
    }

    /**
     * @param id
     *            the _switchId to set
     */
    public void setSwitchId(String id) {
        _switchId = id;
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
     * @return Returns the cardGroupName.
     */
    public String getCardGroupSetName() {
        return _cardGroupSetName;
    }

    /**
     * @param cardGroupName
     *            The cardGroupName to set.
     */
    public void setCardGroupSetName(String cardGroupName) {
        _cardGroupSetName = cardGroupName;
    }

    /**
     * @return Returns the subscriberType.
     */
    public String getSubscriberType() {
        return _subscriberType;
    }

    /**
     * @param subscriberType
     *            The subscriberType to set.
     */
    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
    }

    /**
     * @return Returns the receiverMsisdn.
     */
    public String getReceiverMsisdn() {
        return _receiverMsisdn;
    }

    /**
     * @param receiverMsisdn
     *            The receiverMsisdn to set.
     */
    public void setReceiverMsisdn(String receiverMsisdn) {
        _receiverMsisdn = receiverMsisdn;
    }

    /**
     * @return Returns the senderMsisdn.
     */
    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    /**
     * @param senderMsisdn
     *            The senderMsisdn to set.
     */
    public void setSenderMsisdn(String senderMsisdn) {
        _senderMsisdn = senderMsisdn;
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
     * @return Returns the creditBackStatus.
     */
    public String getCreditBackStatus() {
        return _creditBackStatus;
    }

    /**
     * @param creditBackStatus
     *            The creditBackStatus to set.
     */
    public void setCreditBackStatus(String creditBackStatus) {
        _creditBackStatus = creditBackStatus;
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

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return _language;
    }

    /**
     * @param language
     *            The language to set.
     */
    public void setLanguage(String language) {
        _language = language;
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
     * @return Returns the module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * @param module
     *            The module to set.
     */
    public void setModule(String module) {
        _module = module;
    }

    /**
     * @return Returns the pinSentToMsisdn.
     */
    public String getPinSentToMsisdn() {
        return _pinSentToMsisdn;
    }

    /**
     * @param pinSentToMsisdn
     *            The pinSentToMsisdn to set.
     */
    public void setPinSentToMsisdn(String pinSentToMsisdn) {
        _pinSentToMsisdn = pinSentToMsisdn;
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
     * @return Returns the receiverVO.
     */
    public Object getReceiverVO() {
        return _receiverVO;
    }

    /**
     * @param receiverVO
     *            The receiverVO to set.
     */
    public void setReceiverVO(Object receiverVO) {
        _receiverVO = receiverVO;
    }

    /**
     * @return Returns the reconciliationBy.
     */
    public String getReconciliationBy() {
        return _reconciliationBy;
    }

    /**
     * @param reconciliationBy
     *            The reconciliationBy to set.
     */
    public void setReconciliationBy(String reconciliationBy) {
        _reconciliationBy = reconciliationBy;
    }

    /**
     * @return Returns the reconciliationDate.
     */
    public Date getReconciliationDate() {
        return _reconciliationDate;
    }

    /**
     * @param reconciliationDate
     *            The reconciliationDate to set.
     */
    public void setReconciliationDate(Date reconciliationDate) {
        _reconciliationDate = reconciliationDate;
    }

    /**
     * @return Returns the reconciliationFlag.
     */
    public String getReconciliationFlag() {
        return _reconciliationFlag;
    }

    /**
     * @param reconciliationFlag
     *            The reconciliationFlag to set.
     */
    public void setReconciliationFlag(String reconciliationFlag) {
        _reconciliationFlag = reconciliationFlag;
    }

    /**
     * @return Returns the referenceID.
     */
    public String getReferenceID() {
        return _referenceID;
    }

    /**
     * @param referenceID
     *            The referenceID to set.
     */
    public void setReferenceID(String referenceID) {
        _referenceID = referenceID;
    }

    /**
     * @return Returns the requestThroughQueue.
     */
    public String getRequestThroughQueue() {
        return _requestThroughQueue;
    }

    /**
     * @param requestThroughQueue
     *            The requestThroughQueue to set.
     */
    public void setRequestThroughQueue(String requestThroughQueue) {
        _requestThroughQueue = requestThroughQueue;
    }

    /**
     * @return Returns the senderVO.
     */
    public Object getSenderVO() {
        return _senderVO;
    }

    /**
     * @param senderVO
     *            The senderVO to set.
     */
    public void setSenderVO(Object senderVO) {
        _senderVO = senderVO;
    }

    /**
     * @return Returns the skey.
     */
    public long getSkey() {
        return _skey;
    }

    /**
     * @param skey
     *            The skey to set.
     */
    public void setSkey(long skey) {
        _skey = skey;
    }

    /**
     * @return Returns the skeyGenerationTime.
     */
    public Date getSkeyGenerationTime() {
        return _skeyGenerationTime;
    }

    /**
     * @param skeyGenerationTime
     *            The skeyGenerationTime to set.
     */
    public void setSkeyGenerationTime(Date skeyGenerationTime) {
        _skeyGenerationTime = skeyGenerationTime;
    }

    /**
     * @return Returns the skeySentToMsisdn.
     */
    public String getSkeySentToMsisdn() {
        return _skeySentToMsisdn;
    }

    /**
     * @param skeySentToMsisdn
     *            The skeySentToMsisdn to set.
     */
    public void setSkeySentToMsisdn(String skeySentToMsisdn) {
        _skeySentToMsisdn = skeySentToMsisdn;
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
     * @return Returns the transferDateTime.
     */
    public Date getTransferDateTime() {
        return _transferDateTime;
    }

    /**
     * @param transferDateTime
     *            The transferDateTime to set.
     */
    public void setTransferDateTime(Date transferDateTime) {
        _transferDateTime = transferDateTime;
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

    /**
     * @return Returns the transferValue.
     */
    public long getTransferValue() {
        return _transferValue;
    }

    /**
     * @param transferValue
     *            The transferValue to set.
     */
    public void setTransferValue(long transferValue) {
        _transferValue = transferValue;
    }

    /**
     * @return Returns the instanceID.
     */
    public String getInstanceID() {
        return _instanceID;
    }

    /**
     * @param instanceID
     *            The instanceID to set.
     */
    public void setInstanceID(String instanceID) {
        _instanceID = instanceID;
    }

    public long getCreditAmount() {
        return _creditAmount;
    }

    public void setCreditAmount(long creditAmount) {
        _creditAmount = creditAmount;
    }

    public String getDifferentialApplicable() {
        return _differentialApplicable;
    }

    public void setDifferentialApplicable(String differentialApplicable) {
        _differentialApplicable = differentialApplicable;
    }

    public String getPaymentMethodType() {
        return _paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        _paymentMethodType = paymentMethodType;
    }

    public long getPaymentValue() {
        return _paymentValue;
    }

    public void setPaymentValue(long paymentValue) {
        _paymentValue = paymentValue;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getTransferID() {
        return _transferID;
    }

    public void setTransferID(String transferID) {
        _transferID = transferID;
    }

    public String getCountry() {
        return _country;
    }

    public void setCountry(String country) {
        _country = country;
    }

    public long getQuantity() {
        return _quantity;
    }

    public void setQuantity(long quantity) {
        _quantity = quantity;
    }

    public String getDefaultPaymentMethod() {
        return _defaultPaymentMethod;
    }

    public void setDefaultPaymentMethod(String defaultPaymentMethod) {
        _defaultPaymentMethod = defaultPaymentMethod;
    }

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public int getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    public String getFromDate() {
        return _fromDate;
    }

    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    public String getToDate() {
        return _toDate;
    }

    public void setToDate(String toDate) {
        _toDate = toDate;
    }

    /**
     * @return Returns the cardGroupID.
     */
    public String getCardGroupID() {
        return _cardGroupID;
    }

    /**
     * @param cardGroupID
     *            The cardGroupID to set.
     */
    public void setCardGroupID(String cardGroupID) {
        _cardGroupID = cardGroupID;
    }

    /**
     * @return Returns the cardGroupSetID.
     */
    public String getCardGroupSetID() {
        return _cardGroupSetID;
    }

    /**
     * @param cardGroupSetID
     *            The cardGroupSetID to set.
     */
    public void setCardGroupSetID(String cardGroupSetID) {
        _cardGroupSetID = cardGroupSetID;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        _version = version;
    }

    /**
     * To get the value of transferDisplayDateTime field
     * 
     * @return transferDisplayDateTime.
     */
    public String getTransferDisplayDateTime() {
        return _transferDisplayDateTime;
    }

    /**
     * To set the value of transferDisplayDateTime field
     */
    public void setTransferDisplayDateTime(String transferDisplayDateTime) {
        _transferDisplayDateTime = transferDisplayDateTime;
    }

    /**
     * To get the value of reconciliationDisplayDate field
     * 
     * @return reconciliationDisplayDate.
     */
    public String getReconciliationDisplayDate() {
        return _reconciliationDisplayDate;
    }

    /**
     * To set the value of reconciliationDisplayDate field
     */
    public void setReconciliationDisplayDate(String reconciliationDisplayDate) {
        _reconciliationDisplayDate = reconciliationDisplayDate;
    }

    public long getReceiverAccessFee() {
        return _receiverAccessFee;
    }

    public String getReceiverAccessFeeAsString() {
        return PretupsBL.getDisplayAmount(_receiverAccessFee);
    }

    public void setReceiverAccessFee(long receiverAccessFee) {
        _receiverAccessFee = receiverAccessFee;
    }

    public int getReceiverBonusValidity() {
        return _receiverBonusValidity;
    }

    public void setReceiverBonusValidity(int receiverBonusValidity) {
        _receiverBonusValidity = receiverBonusValidity;
    }

    public long getReceiverBonusValue() {
        return _receiverBonusValue;
    }

    public String getReceiverBonusValueAsString() {
        return PretupsBL.getDisplayAmount(_receiverBonusValue);
    }

    public void setReceiverBonusValue(long receiverBonusValue) {
        _receiverBonusValue = receiverBonusValue;
    }

    public long getReceiverGracePeriod() {
        return _receiverGracePeriod;
    }

    public void setReceiverGracePeriod(long receiverGracePeriod) {
        _receiverGracePeriod = receiverGracePeriod;
    }

    public String getReceiverTax1Type() {
        return _receiverTax1Type;
    }

    public void setReceiverTax1Type(String receiverTax1Type) {
        _receiverTax1Type = receiverTax1Type;
    }

    public long getReceiverTax1Value() {
        return _receiverTax1Value;
    }

    public void setReceiverTax1Value(long receiverTax1Value) {
        _receiverTax1Value = receiverTax1Value;
    }

    public String getReceiverTax1ValueAsString() {
        return PretupsBL.getDisplayAmount(_receiverTax1Value);
    }

    public String getReceiverTax1RateAsString() {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax1Type)) {
            return PretupsBL.getDisplayAmount(Double.valueOf(_receiverTax1Rate).longValue());
        } else {
            return String.valueOf(_receiverTax1Rate);
        }
    }

    public String getReceiverTax2Type() {
        return _receiverTax2Type;
    }

    public void setReceiverTax2Type(String receiverTax2Type) {
        _receiverTax2Type = receiverTax2Type;
    }

    public long getReceiverTax2Value() {
        return _receiverTax2Value;
    }

    public void setReceiverTax2Value(long receiverTax2Value) {
        _receiverTax2Value = receiverTax2Value;
    }

    public String getReceiverTax2ValueAsString() {
        return PretupsBL.getDisplayAmount(_receiverTax2Value);
    }

    public long getReceiverTransferValue() {
        return _receiverTransferValue;
    }

    public String getReceiverTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_receiverTransferValue);
    }

    public void setReceiverTransferValue(long receiverTransferValue) {
        _receiverTransferValue = receiverTransferValue;
    }

    public int getReceiverValidity() {
        return _receiverValidity;
    }

    public void setReceiverValidity(int receiverValidity) {
        _receiverValidity = receiverValidity;
    }

    public long getSenderTransferValue() {
        return _senderTransferValue;
    }

    public String getSenderTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_senderTransferValue);
    }

    public void setSenderTransferValue(long senderTransferValue) {
        _senderTransferValue = senderTransferValue;
    }

    public Date getValidityDateToBeSet() {
        return _validityDateToBeSet;
    }

    public void setValidityDateToBeSet(Date validityDateToBeSet) {
        _validityDateToBeSet = validityDateToBeSet;
    }

    public String getValidityDateToBeSetAsString() {
        final String METHOD_NAME = "getValidityDateToBeSetAsString";
        try {
            return BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(_validityDateToBeSet));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            return "";
        }
    }

    /**
     * @return Returns the cardGroupCode.
     */
    public String getCardGroupCode() {
        return _cardGroupCode;
    }

    /**
     * @param cardGroupCode
     *            The cardGroupCode to set.
     */
    public void setCardGroupCode(String cardGroupCode) {
        _cardGroupCode = cardGroupCode;
    }

    public String getReceiverValPeriodType() {
        return _receiverValPeriodType;
    }

    public void setReceiverValPeriodType(String receiverValPeriodType) {
        _receiverValPeriodType = receiverValPeriodType;
    }

    public long getQueueAdditionTime() {
        return _queueAdditionTime;
    }

    public void setQueueAdditionTime(long queueAdditionTime) {
        _queueAdditionTime = queueAdditionTime;
    }

    /**
     * @return Returns the requestGatewayCode.
     */
    public String getRequestGatewayCode() {
        return _requestGatewayCode;
    }

    /**
     * @param requestGatewayCode
     *            The requestGatewayCode to set.
     */
    public void setRequestGatewayCode(String requestGatewayCode) {
        _requestGatewayCode = requestGatewayCode;
    }

    /**
     * @return Returns the requestGatewayType.
     */
    public String getRequestGatewayType() {
        return _requestGatewayType;
    }

    /**
     * @param requestGatewayType
     *            The requestGatewayType to set.
     */
    public void setRequestGatewayType(String requestGatewayType) {
        _requestGatewayType = requestGatewayType;
    }

    public String[] getMessageArguments() {
        return _messageArguments;
    }

    public void setMessageArguments(String[] messageArguments) {
        _messageArguments = messageArguments;
    }

    public String getMessageCode() {
        return _messageCode;
    }

    public void setMessageCode(String messageCode) {
        _messageCode = messageCode;
    }

    public double getReceiverTax1Rate() {
        return _receiverTax1Rate;
    }

    public void setReceiverTax1Rate(double receiverTax1Rate) {
        _receiverTax1Rate = receiverTax1Rate;
    }

    public double getReceiverTax2Rate() {
        return _receiverTax2Rate;
    }

    public void setReceiverTax2Rate(double receiverTax2Rate) {
        _receiverTax2Rate = receiverTax2Rate;
    }

    public String getReceiverTax2RateAsString() {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax2Type)) {
            return PretupsBL.getDisplayAmount(Double.valueOf(_receiverTax2Rate).longValue());
        } else {
            return String.valueOf(_receiverTax2Rate);
        }
    }

    /**
     * @return Returns the gatewayName.
     */
    public String getGatewayName() {
        return _gatewayName;
    }

    /**
     * @param gatewayName
     *            The gatewayName to set.
     */
    public void setGatewayName(String gatewayName) {
        _gatewayName = gatewayName;
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

    public String getBuddy() {
        return _buddy;
    }

    public void setBuddy(String buddy) {
        _buddy = buddy;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getReceiverNetworkCode() {
        return _receiverNetworkCode;
    }

    public void setReceiverNetworkCode(String receiverNetworkCode) {
        _receiverNetworkCode = receiverNetworkCode;
    }

    public String getSenderID() {
        return _senderID;
    }

    public void setSenderID(String senderID) {
        _senderID = senderID;
    }

    public String getRequestID() {
        return _requestID;
    }

    public void setRequestID(String requestID) {
        _requestID = requestID;
    }

    public long getSenderAccessFee() {
        return _senderAccessFee;
    }

    public String getSenderAccessFeeAsString() {
        return PretupsBL.getDisplayAmount(_senderAccessFee);
    }

    public void setSenderAccessFee(long senderAccessFee) {
        _senderAccessFee = senderAccessFee;
    }

    public double getSenderTax1Rate() {
        return _senderTax1Rate;
    }

    public void setSenderTax1Rate(double senderTax1Rate) {
        _senderTax1Rate = senderTax1Rate;
    }

    public String getSenderTax1RateAsString() {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax1Type)) {
            return PretupsBL.getDisplayAmount(Double.valueOf(_senderTax1Rate).longValue());
        } else {
            return String.valueOf(_senderTax1Rate);
        }
    }

    public String getSenderTax1Type() {
        return _senderTax1Type;
    }

    public void setSenderTax1Type(String senderTax1Type) {
        _senderTax1Type = senderTax1Type;
    }

    public long getSenderTax1Value() {
        return _senderTax1Value;
    }

    public void setSenderTax1Value(long senderTax1Value) {
        _senderTax1Value = senderTax1Value;
    }

    public String getSenderTax1ValueAsString() {
        return PretupsBL.getDisplayAmount(_senderTax1Value);
    }

    public double getSenderTax2Rate() {
        return _senderTax2Rate;
    }

    public void setSenderTax2Rate(double senderTax2Rate) {
        _senderTax2Rate = senderTax2Rate;
    }

    public String getSenderTax2RateAsString() {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax2Type)) {
            return PretupsBL.getDisplayAmount(Double.valueOf(_senderTax2Rate).longValue());
        } else {
            return String.valueOf(_senderTax2Rate);
        }
    }

    public String getSenderTax2Type() {
        return _senderTax2Type;
    }

    public void setSenderTax2Type(String senderTax2Type) {
        _senderTax2Type = senderTax2Type;
    }

    public long getSenderTax2Value() {
        return _senderTax2Value;
    }

    public void setSenderTax2Value(long senderTax2Value) {
        _senderTax2Value = senderTax2Value;
    }

    public String getSenderTax2ValueAsString() {
        return PretupsBL.getDisplayAmount(_senderTax2Value);
    }

    public ArrayList getTransferItemList() {
        return _transferItemList;
    }

    public void setTransferItemList(ArrayList transferItemList) {
        _transferItemList = transferItemList;
    }

    public long getRequestedAmount() {
        return _requestedAmount;
    }

    public void setRequestedAmount(long requestedAmount) {
        _requestedAmount = requestedAmount;
    }

    public String getSenderReturnMessage() {
        return _senderReturnMessage;
    }

    public void setSenderReturnMessage(String senderReturnMessage) {
        _senderReturnMessage = senderReturnMessage;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    public String getSourceType() {
        return _sourceType;
    }

    public void setSourceType(String sourceType) {
        _sourceType = sourceType;
    }

    public String getMsgGatewayFlowType() {
        return _msgGatewayFlowType;
    }

    public void setMsgGatewayFlowType(String msgGatewayFlowType) {
        _msgGatewayFlowType = msgGatewayFlowType;
    }

    public String getMsgGatewayResponseType() {
        return _msgGatewayResponseType;
    }

    public void setMsgGatewayResponseType(String msgGatewayResponseType) {
        _msgGatewayResponseType = msgGatewayResponseType;
    }

    public long getMsgGatewayTimeOutValue() {
        return _msgGatewayTimeOutValue;
    }

    public void setMsgGatewayTimeOutValue(long msgGatewayTimeOutValue) {
        _msgGatewayTimeOutValue = msgGatewayTimeOutValue;
    }

    public boolean isUnderProcessMsgReq() {
        return _underProcessMsgReq;
    }

    public void setUnderProcessMsgReq(boolean underProcessMsgReq) {
        _underProcessMsgReq = underProcessMsgReq;
    }

    public Object getReceiverReturnMsg() {
        return _receiverReturnMsg;
    }

    public void setReceiverReturnMsg(Object receiverReturnMsg) {
        _receiverReturnMsg = receiverReturnMsg;
    }

    public String getProductType() {
        return _productType;
    }

    public void setProductType(String productType) {
        _productType = productType;
    }

    public String getTransferValueStr() {
        return _transferValueStr;
    }

    public void setTransferValueStr(String transferValueStr) {
        _transferValueStr = transferValueStr;
    }

    public String getTransferDateStr() {
        return _transferDateStr;
    }

    public void setTransferDateStr(String transferDateStr) {
        _transferDateStr = transferDateStr;
    }

    public String getSenderName() {
        return _senderName;
    }

    public void setSenderName(String senderName) {
        _senderName = senderName;
    }

    public String getIncomingSmsStr() {
        return _incomingSmsStr;
    }

    public void setIncomingSmsStr(String incomingSmsStr) {
        _incomingSmsStr = incomingSmsStr;
    }

    public String getErrorMessage() {
        return _errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        _errorMessage = errorMessage;
    }

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    public String getReceiverAllServiceClassID() {
        return _receiverAllServiceClassID;
    }

    public void setReceiverAllServiceClassID(String receiverAllServiceClassID) {
        _receiverAllServiceClassID = receiverAllServiceClassID;
    }

    public String getSenderAllServiceClassID() {
        return _senderAllServiceClassID;
    }

    public void setSenderAllServiceClassID(String senderAllServiceClassID) {
        _senderAllServiceClassID = senderAllServiceClassID;
    }

    public String getSubService() {
        return _subService;
    }

    public void setSubService(String subService) {
        _subService = subService;
    }

    public long getMinCardGroupAmount() {
        return _minCardGroupAmount;
    }

    public void setMinCardGroupAmount(long minCardGroupAmount) {
        _minCardGroupAmount = minCardGroupAmount;
    }

    public String getDifferentialAllowedForService() {
        return _differentialAllowedForService;
    }

    public void setDifferentialAllowedForService(String differentialAllowed) {
        _differentialAllowedForService = differentialAllowed;
    }

    public String getGiveOnlineDifferential() {
        return _giveOnlineDifferential;
    }

    public void setGiveOnlineDifferential(String giveOnlineDifferential) {
        _giveOnlineDifferential = giveOnlineDifferential;
    }

    /**
     * @return Returns the _requestVO.
     */
    public RequestVO getRequestVO() {
        return _requestVO;
    }

    /**
     * @param _requestvo
     *            The _requestVO to set.
     */
    public void setRequestVO(RequestVO _requestvo) {
        _requestVO = _requestvo;
    }

    /**
     * @return Returns the _senderTransferItemVO.
     */
    public TransferItemVO getSenderTransferItemVO() {
        return _senderTransferItemVO;
    }

    /**
     * @param transferItemVO
     *            The _senderTransferItemVO to set.
     */
    public void setSenderTransferItemVO(TransferItemVO transferItemVO) {
        _senderTransferItemVO = transferItemVO;
    }

    /**
     * @return Returns the _receiverTransferItemVO.
     */
    public TransferItemVO getReceiverTransferItemVO() {
        return _receiverTransferItemVO;
    }

    /**
     * @param transferItemVO
     *            The _receiverTransferItemVO to set.
     */
    public void setReceiverTransferItemVO(TransferItemVO transferItemVO) {
        _receiverTransferItemVO = transferItemVO;
    }

    /**
     * @return Returns the requestStartTime.
     */
    public long getRequestStartTime() {
        return _requestStartTime;
    }

    /**
     * @param requestStartTime
     *            The requestStartTime to set.
     */
    public void setRequestStartTime(long requestStartTime) {
        _requestStartTime = requestStartTime;
    }

    /**
     * @return Returns the receiverPostBalance.
     */
    public long getReceiverPostBalance() {
        return _receiverPostBalance;
    }

    /**
     * @param receiverPostBalance
     *            The receiverPostBalance to set.
     */
    public void setReceiverPostBalance(long receiverPostBalance) {
        _receiverPostBalance = receiverPostBalance;
    }

    /**
     * @return Returns the senderPostBalance.
     */
    public long getSenderPostBalance() {
        return _senderPostBalance;
    }

    /**
     * @param senderPostBalance
     *            The senderPostBalance to set.
     */
    public void setSenderPostBalance(long senderPostBalance) {
        _senderPostBalance = senderPostBalance;
    }

    public Date getMsisdnNewExpiry() {
        return _msisdnNewExpiry;
    }

    public void setMsisdnNewExpiry(Date msisdnNewExpiry) {
        _msisdnNewExpiry = msisdnNewExpiry;
    }

    public String getMsisdnNewExpiryStr() {
        return _msisdnNewExpiryStr;
    }

    public void setMsisdnNewExpiryStr(String msisdnNewExpiryStr) {
        _msisdnNewExpiryStr = msisdnNewExpiryStr;
    }

    public Date getMsisdnPreviousExpiry() {
        return _msisdnPreviousExpiry;
    }

    public void setMsisdnPreviousExpiry(Date msisdnPreviousExpiry) {
        _msisdnPreviousExpiry = msisdnPreviousExpiry;
    }

    public String getMsisdnPreviousExpiryStr() {
        return _msisdnPreviousExpiryStr;
    }

    public void setMsisdnPreviousExpiryStr(String msisdnPreviousExpiryStr) {
        _msisdnPreviousExpiryStr = msisdnPreviousExpiryStr;
    }

    public String getTransferCategory() {
        return _transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
    }

    public String getReceiverSubscriberType() {
        return _receiverSubscriberType;
    }

    public void setReceiverSubscriberType(String receiverSubscriberType) {
        _receiverSubscriberType = receiverSubscriberType;
    }

    public String getTxnStatus() {
        return _txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        _txnStatus = txnStatus;
    }

    public Object getOtherInfo1() {
        return _otherInfo1;
    }

    public void setOtherInfo1(Object otherInfo1) {
        _otherInfo1 = otherInfo1;
    }

    public Object getOtherInfo2() {
        return _otherInfo2;
    }

    public void setOtherInfo2(Object otherInfo2) {
        _otherInfo2 = otherInfo2;
    }

    public String getSerialNumber() {
        return _serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        _serialNumber = serialNumber;
    }

    /**
     * @return Returns the paymentMethodKeywordVO.
     */
    public PaymentMethodKeywordVO getPaymentMethodKeywordVO() {
        return _paymentMethodKeywordVO;
    }

    /**
     * @param paymentMethodKeywordVO
     *            The paymentMethodKeywordVO to set.
     */
    public void setPaymentMethodKeywordVO(PaymentMethodKeywordVO paymentMethodKeywordVO) {
        _paymentMethodKeywordVO = paymentMethodKeywordVO;
    }

    public String getReceiverInterfaceStatusType() {
        return _receiverInterfaceStatusType;
    }

    public void setReceiverInterfaceStatusType(String interfaceStatusType) {
        _receiverInterfaceStatusType = interfaceStatusType;
    }

    public String getSenderInterfaceStatusType() {
        return _senderInterfaceStatusType;
    }

    public void setSenderInterfaceStatusType(String interfaceStatusType) {
        _senderInterfaceStatusType = interfaceStatusType;
    }

    public String getLastTransferId() {
        return _lastTransferId;
    }

    public void setLastTransferId(String lastTransferId) {
        _lastTransferId = lastTransferId;
    }

    /*
     * This code was commented on 01/04/08 to eliminate fetch conversion rate
     * step.
     * Now Moldova will support single currency. Previously conversion rate was
     * required to
     * support multiple currency for moldova.
     * 
     * public double getConvertedRequestedAmount() {
     * return _convertedRequestedAmount;
     * }
     * 
     * public void setConvertedRequestedAmount(double convertedRequestedAmount)
     * {
     * _convertedRequestedAmount = convertedRequestedAmount;
     * }
     */
    // End of single currency request change

    public long getFeeForValidityExtention() {
        return _feeForValidityExtention;
    }

    public void setFeeForValidityExtention(long feeForValidityExtention) {
        _feeForValidityExtention = feeForValidityExtention;
    }
    

    public String getFeeForValidityExtentionStr() {
        return _feeForValidityExtentionStr;
    }

    public void setFeeForValidityExtentionStr(String feeForValidityExtentionStr) {
        _feeForValidityExtentionStr = feeForValidityExtentionStr;
    }

    public int getValidityDaysToExtend() {
        return _validityDaysToExtend;
    }

    public void setValidityDaysToExtend(int validityDaysToExtend) {
        _validityDaysToExtend = validityDaysToExtend;
    }

    public String getValExtTransferID() {
        return _valExtTransferID;
    }

    public void setValExtTransferID(String valExtTransferID) {
        _valExtTransferID = valExtTransferID;
    }

    /**
     * @return Returns the receiverBonus1.
     */
    public double getReceiverBonus1() {
        return _receiverBonus1;
    }

    /**
     * @param receiverBonus1
     *            The receiverBonus1 to set.
     */
    public void setReceiverBonus1(double receiverBonus1) {
        _receiverBonus1 = receiverBonus1;
    }

    /**
     * @return Returns the receiverBonus2.
     */
    public double getReceiverBonus2() {
        return _receiverBonus2;
    }

    /**
     * @param receiverBonus2
     *            The receiverBonus2 to set.
     */
    public void setReceiverBonus2(double receiverBonus2) {
        _receiverBonus2 = receiverBonus2;
    }

    // for gift recharge.
    public Object getGifterReturnMsg() {
        return _gifterReturnMsg;
    }

    public void setGifterReturnMsg(Object gifterReturnMsg) {
        _gifterReturnMsg = gifterReturnMsg;
    }

    public String getUnderProcessCheckReqd() {
        return _underProcessCheckReqd;
    }

    public void setUnderProcessCheckReqd(String underProcessCheckReqd) {
        _underProcessCheckReqd = underProcessCheckReqd;
    }

    public long getBonusTalkTimeValue() {
        return _bonusTalkTimeValue;
    }

    /**
     * @param bonusTalkTimeValue
     *            The bonusTalkTimeValue to set.
     */
    public void setBonusTalkTimeValue(long bonusTalkTimeValue) {
        _bonusTalkTimeValue = bonusTalkTimeValue;
    }

    public long getCalminusBonusvalue() {
        return _calminusBonusvalue;
    }

    /**
     * @param CalminusBonusvalue
     *            The CalminusBonusvalue to set.
     */
    public void setCalminusBonusvalue(long calminusBonusvalue) {
        _calminusBonusvalue = calminusBonusvalue;
    }
    
    
    

    /**
     * @return Returns the _status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param groupSlabStatus
     *            The _status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    // added by vikask for card group updation
    /**
     * @return Returns the receiverBonus1Validity.
     */
    public long getReceiverBonus1Validity() {
        return _receiverBonus1Validity;
    }

    /**
     * @param receiverBonus1Validity
     *            The receiverBonus1Validity to set.
     */
    public void setReceiverBonus1Validity(long receiverBonus1Validity) {
        _receiverBonus1Validity = receiverBonus1Validity;
    }

    /**
     * @return Returns the receiverBonus2Validity.
     */
    public long getReceiverBonus2Validity() {
        return _receiverBonus2Validity;
    }

    /**
     * @param receiverBonus2Validity
     *            The receiverBonus2Validity to set.
     */
    public void setReceiverBonus2Validity(long receiverBonus2Validity) {
        _receiverBonus2Validity = receiverBonus2Validity;
    }

    /**
     * @return Returns the receiverCreditBonusValidity.
     */
    public long getReceiverCreditBonusValidity() {
        return _receiverCreditBonusValidity;
    }

    /**
     * @param _receiverCreditBonusValidity
     *            The _receiverCreditBonusValidity to set.
     */
    public void setReceiverCreditBonusValidity(long receiverCreditBonusValidity) {
        _receiverCreditBonusValidity = receiverCreditBonusValidity;
    }

    /**
     * @return Returns the both.
     */
    public String getBoth() {
        return _both;
    }

    /**
     * @param both
     *            The both to set.
     */
    public void setBoth(String both) {
        _both = both;
    }

    /**
     * @return Returns the onLine.
     */
    public String getOnline() {
        return _online;
    }

    /**
     * @param onLine
     *            The onLine to set.
     */
    public void setOnline(String online) {
        _online = online;
    }
    
    public String getReversalPermitted() {
		  return _reversalPermitted;
	  }

	  public void setReversalPermitted(String permitted) {
		  _reversalPermitted = permitted;
	  }
    /**
     * @return Returns the extCreditIntfceType.
     */
    public String getExtCreditIntfceType() {
        return _extCreditIntfceType;
    }

    /**
     * @param extCreditIntfceType
     *            The extCreditIntfceType to set.
     */
    public void setExtCreditIntfceType(String extCreditIntfceType) {
        _extCreditIntfceType = extCreditIntfceType;
    }

    /**
     * @return Returns the senderConvFactor.
     */
    public double getSenderConvFactor() {
        return _senderConvFactor;
    }

    /**
     * @param senderConvFactor
     *            The senderConvFactor to set.
     */
    public void setSenderConvFactor(double senderConvFactor) {
        _senderConvFactor = senderConvFactor;
    }

    /**
     * @return Returns the bonusBundleIdS.
     */
    public String getBonusBundleIdS() {
        return _bonusBundleIdS;
    }

    /**
     * @param bonusBundleIdS
     *            The bonusBundleIdS to set.
     */
    public void setBonusBundleIdS(String bonusBundleIdS) {
        _bonusBundleIdS = bonusBundleIdS;
    }

    /**
     * @return Returns the bonusBundleNames.
     */
    public String getBonusBundleNames() {
        return _bonusBundleNames;
    }

    /**
     * @param bonusBundleNames
     *            The bonusBundleNames to set.
     */
    public void setBonusBundleNames(String bonusBundleNames) {
        _bonusBundleNames = bonusBundleNames;
    }

    /**
     * @return Returns the bonusBundleRate.
     */
    public String getBonusBundleRate() {
        return _bonusBundleRate;
    }

    /**
     * @param bonusBundleRate
     *            The bonusBundleRate to set.
     */
    public void setBonusBundleRate(String bonusBundleRate) {
        _bonusBundleRate = bonusBundleRate;
    }

    /**
     * @return Returns the bonusBundleTypes.
     */
    public String getBonusBundleTypes() {
        return _bonusBundleTypes;
    }

    /**
     * @param bonusBundleTypes
     *            The bonusBundleTypes to set.
     */
    public void setBonusBundleTypes(String bonusBundleTypes) {
        _bonusBundleTypes = bonusBundleTypes;
    }

    /**
     * @return Returns the bonusBundleValidities.
     */
    public String getBonusBundleValidities() {
        return _bonusBundleValidities;
    }

    /**
     * @param bonusBundleValidities
     *            The bonusBundleValidities to set.
     */
    public void setBonusBundleValidities(String bonusBundleValidities) {
        _bonusBundleValidities = bonusBundleValidities;
    }

    /**
     * @return Returns the bonusBundleValues.
     */
    public String getBonusBundleValues() {
        return _bonusBundleValues;
    }

    /**
     * @param bonusBundleValues
     *            The bonusBundleValues to set.
     */
    public void setBonusBundleValues(String bonusBundleValues) {
        _bonusBundleValues = bonusBundleValues;
    }

    /**
     * @return Returns the bonusItems.
     */
    public Object getBonusItems() {
        return _bonusItems;
    }

    /**
     * @param bonusItems
     *            The bonusItems to set.
     */
    public void setBonusItems(Object bonusItems) {
        _bonusItems = bonusItems;
    }

    /**
     * @return Returns the bonusSummarySting.
     */
    public String getBonusSummarySting() {
        return _bonusSummarySting;
    }

    /**
     * @param bonusSummarySting
     *            The bonusSummarySting to set.
     */
    public void setBonusSummarySting(String bonusSummarySting) {
        _bonusSummarySting = bonusSummarySting;
    }

    /**
     * @return Returns the convertedRequestedAmount.
     */
    public double getConvertedRequestedAmount() {
        return _convertedRequestedAmount;
    }

    /**
     * @param convertedRequestedAmount
     *            The convertedRequestedAmount to set.
     */
    public void setConvertedRequestedAmount(double convertedRequestedAmount) {
        _convertedRequestedAmount = convertedRequestedAmount;
    }

    /**
     * @return Returns the receiverConvFactor.
     */
    public double getReceiverConvFactor() {
        return _receiverConvFactor;
    }

    /**
     * @param receiverConvFactor
     *            The receiverConvFactor to set.
     */
    public void setReceiverConvFactor(double receiverConvFactor) {
        _receiverConvFactor = receiverConvFactor;
    }

    /**
     * @return Returns the selectorBundleId.
     */
    public String getSelectorBundleId() {
        return _selectorBundleId;
    }

    /**
     * @param selectorBundleId
     *            The selectorBundleId to set.
     */
    public void setSelectorBundleId(String selectorBundleId) {
        _selectorBundleId = selectorBundleId;
    }

    /**
     * @return Returns the selectorBundleType.
     */
    public String getSelectorBundleType() {
        return _selectorBundleType;
    }

    /**
     * @param selectorBundleType
     *            The selectorBundleType to set.
     */
    public void setSelectorBundleType(String selectorBundleType) {
        _selectorBundleType = selectorBundleType;
    }

    /**
     * @return Returns the selectorCode.
     */
    public String getSelectorCode() {
        return _selectorCode;
    }

    /**
     * @param selectorCode
     *            The selectorCode to set.
     */
    public void setSelectorCode(String selectorCode) {
        _selectorCode = selectorCode;
    }

    /**
     * @return Returns the bonusBundleCode.
     */
    public String getBonusBundleCode() {
        return _bonusBundleCode;
    }

    /**
     * @param bonusBundleCode
     *            The bonusBundleCode to set.
     */
    public void setBonusBundleCode(String bonusBundleCode) {
        _bonusBundleCode = bonusBundleCode;
    }

    /**
     * @return Returns the receiverBundleID.
     */
    public String getReceiverBundleID() {
        return _receiverBundleID;
    }

    /**
     * @param receiverBundleID
     *            The receiverBundleID to set.
     */
    public void setReceiverBundleID(String receiverBundleID) {
        _receiverBundleID = receiverBundleID;
    }

    /**
     * @return Returns the activeUserId.
     */
    public String getActiveUserId() {
        return _activeUserId;
    }

    /**
     * @param activeUserId
     *            The activeUserId to set.
     */
    public void setActiveUserId(String activeUserId) {
        _activeUserId = activeUserId;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return the bonusSummaryMessageSting
     */
    public String getBonusSummaryMessageSting() {
        return _bonusSummaryMessageSting;
    }

    /**
     * @param bonusSummaryMessageSting
     *            the bonusSummaryMessageSting to set
     */
    public void setBonusSummaryMessageSting(String bonusSummaryMessageSting) {
        _bonusSummaryMessageSting = bonusSummaryMessageSting;
    }

    public String getOnLine() {
        return _online;
    }

    /**
     * @param onLine
     *            The onLine to set.
     */
    public void setOnLine(String online) {
        _online = online;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String _value) {
        this._value = _value;
    }

    public long getSenderSettlementValue() {
        return _senderSettlementValue;
    }

    public void setSenderSettlementValue(long senderTransferValue) {
        _senderSettlementValue = senderTransferValue;
    }

    public void setLastTransferDateTime(Date p_date) {
        _lastTransferDateTime = p_date;
    }

    public Date getLastTransferDateTime() {
        return _lastTransferDateTime;
    }

    public double getSenderAccessFeeRate() {
        return _senderAccessFeeRate;
    }

    public void setSenderAccessFeeRate(double senderAccessFeeRate) {
        _senderAccessFeeRate = senderAccessFeeRate;
    }

    public String getSenderAccessFeeType() {
        return _senderAccessFeeType;
    }

    public void setSenderAccessFeeType(String senderAccessFeeType) {
        _senderAccessFeeType = senderAccessFeeType;
    }

    public String getLmbCreditUpdateStatus() {
        return _lmbCreditUpdateStatus;
    }

    public void setLmbCreditUpdateStatus(String lmbCreditUpdateStatus) {
        this._lmbCreditUpdateStatus = lmbCreditUpdateStatus;
    }

    /**
     * @return the lastMRP
     */
    public long getLastMRP() {
        return _lastMRP;
    }

    /**
     * @param lastMRP
     *            the lastMRP to set
     */
    public void setLastMRP(long lastMRP) {
        _lastMRP = lastMRP;
    }

    /**
     * @return the lastServiceType
     */
    public String getLastServiceType() {
        return _lastServiceType;
    }

    /**
     * @param lastServiceType
     *            the lastServiceType to set
     */
    public void setLastServiceType(String lastServiceType) {
        _lastServiceType = lastServiceType;
    }

    /*
     * public HashMap<String, ServiceClassVO> getServiceClassMap() {
     * return _serviceClassMap;
     * }
     * 
     * public void setServiceClassMap(HashMap classMap) {
     * _serviceClassMap = classMap;
     * }
     */
    /**
     * @return the _inPromo
     */
    public double getInPromo() {
        return _inPromo;
    }

    /**
     * @param promo
     *            the _inPromo to set
     */
    public void setInPromo(double promo) {
        _inPromo = promo;
    }

    /**
     * @return the _cosRequired
     */
    public String getCosRequired() {
        return _cosRequired;
    }

    /**
     * @param required
     *            the _cosRequired to set
     */
    public void setCosRequired(String required) {
        _cosRequired = required;
    }

    public String getNewCos() {
        return _newCos;
    }

    public void setNewCos(String newCos) {
        _newCos = newCos;
    }

    /**
     * @return the _rechargeComment
     */
    public String getRechargeComment() {
        return _rechargeComment;
    }

    /**
     * @param _rechargeComment
     *            the comment to set
     */
    public void setRechargeComment(String comment) {
        _rechargeComment = comment;
    }

    public void setServiceProviderName(String p_serviceProviderName) {
        _serviceProviderName = p_serviceProviderName;
    }

    /**
     * @param p_serviceProviderName
     *            The serviceProviderName to set.
     */
    public String getServiceProviderName() {
        return _serviceProviderName;
    }

    /**
     * @return Returns the tempId.
     */
    public String getTempId() {
        return _tempId;
    }

    /**
     * @param tempId
     *            The tempId to set.
     */
    public void setTempId(String tempId) {
        _tempId = tempId;
    }

    public String getSubscriberStatus() {
        return _subscriberStatus;
    }

    public void setSubscriberStatus(String status) {
        _subscriberStatus = status;
    }

    public String getCombinedKey() {
        return _serviceType + ":" + _subscriberStatus;
    }

    public void setCombinedKey(String key) {
        _combinedKey = key;
    }

    public String getReceiverServiceProviderName() {
        return _receiverServiceProviderName;
    }

    public void setReceiverServiceProviderName(String serviceProviderName) {
        _receiverServiceProviderName = serviceProviderName;
    }

    public String getNewPromoBalance() {
        return _newPromoBalance;
    }

    public void setNewPromoBalance(String newPromoBalance) {
        _newPromoBalance = newPromoBalance;
    }

    public String getNewPromoExpiry() {
        return _newPromoExpiry;
    }

    public void setNewPromoExpiry(String newPromoExpiry) {
        _newPromoExpiry = newPromoExpiry;
    }

    public String getPreviousPromoBalance() {
        return _previousPromoBalance;
    }

    public void setPreviousPromoBalance(String previousPromoBalance) {
        _previousPromoBalance = previousPromoBalance;
    }

    public String getPreviousPromoExpiry() {
        return _previousPromoExpiry;
    }

    public void setPreviousPromoExpiry(String previousPromoExpiry) {
        _previousPromoExpiry = previousPromoExpiry;
    }

    /**
     * @return Returns the interfaceID.
     */
    public String getValInterfaceID() {
        return _valInterfaceID;
    }

    /**
     * @param interfaceID
     *            The interfaceID to set.
     */
    public void setValInterfaceID(String valInterfaceID) {
        _valInterfaceID = valInterfaceID;
    }

    public String getInterfaceValResponseCode() {
        return _interfaceValResponseCode;
    }

    public void setInterfaceValResponseCode(String interfaceVaResponseCode) {
        _interfaceValResponseCode = interfaceVaResponseCode;
    }

    public String getValProtocolStatus() {
        return _valProtocolStatus;
    }

    public void setValProtocolStatus(String valProtocolStatus) {
        _valProtocolStatus = valProtocolStatus;
    }

    /**
     * @return Returns the promoStatus.
     */
    public String getPromoStatus() {
        return _promoStatus;
    }

    /**
     * @param p_promoStatus
     *            The promoStatus to set.
     */
    public void setPromoStatus(String p_promoStatus) {
        _promoStatus = p_promoStatus;
    }

    /**
     * @return Returns the interfacePromoStatus.
     */
    public String getInterfacePromoStatus() {
        return _interfacePromoStatus;
    }

    /**
     * @param p_interfacePromoStatus
     *            The interfacePromoStatus to set.
     */
    public void setInterfacePromoStatus(String p_interfacePromoStatus) {
        _interfacePromoStatus = p_interfacePromoStatus;
    }

    /**
     * @return Returns the cosStatus.
     */
    public String getCosStatus() {
        return _cosStatus;
    }

    /**
     * @param p_cosStatus
     *            The cosStatus to set.
     */
    public void setCosStatus(String p_cosStatus) {
        _cosStatus = p_cosStatus;
    }

    public String getNewServiceClssCode() {
        return _newServiceClssCode;
    }

    /**
     * @param p_newServiceClssCode
     *            The newServiceClssCode to set.
     */
    public void setNewServiceClssCode(String p_newServiceClssCode) {
        _newServiceClssCode = p_newServiceClssCode;
    }

    /**
     * @return Returns the previousExpiryInCal.
     */
    public String getPreviousExpiryInCal() {
        return _previousExpiryInCal;
    }

    /**
     * @param p_previousExpiryInCal
     *            The previousExpiryInCal to set.
     */
    public void setPreviousExpiryInCal(String p_previousExpiryInCal) {
        _previousExpiryInCal = p_previousExpiryInCal;
    }

    /**
     * @return Returns the previousPromoExpiryInCal.
     */
    public String getPreviousPromoExpiryInCal() {
        return _previousPromoExpiryInCal;
    }

    /**
     * @param p_previousPromoExpiryInCal
     *            The previousPromoExpiryInCal to set.
     */
    public void setPreviousPromoExpiryInCal(String p_previousPromoExpiryInCal) {
        _previousPromoExpiryInCal = p_previousPromoExpiryInCal;
    }

    /**
     * @return Returns the interfaceCOSStatus.
     */
    public String getInterfaceCosStatus() {
        return _interfaceCosStatus;
    }

    /**
     * @param p_interfaceCOSStatus
     *            The interfaceCOSStatus to set.
     */
    public void setInterfaceCosStatus(String p_interfaceCosStatus) {
        _interfaceCosStatus = p_interfaceCosStatus;
    }

    public Date getPostCreditCoreValidity() {
        return _postCreditCoreValidity;
    }

    public void setPostCreditCoreValidity(Date creditCoreValidity) {
        _postCreditCoreValidity = creditCoreValidity;
    }

    public long getPostCreditCoreBalance() {
        return _postCreditCoreBalance;
    }

    public void setPostCreditCoreBalance(long creditCoreBalance) {
        _postCreditCoreBalance = creditCoreBalance;
    }

    public long getPostCreditPromoBalance() {
        return _postCreditPromoBalance;
    }

    public void setPostCreditPromoBalance(long creditPromoBalance) {
        _postCreditPromoBalance = creditPromoBalance;
    }

    public Date getPostCreditPromoValidity() {
        return _postCreditPromoValidity;
    }

    public void setPostCreditPromoValidity(Date creditPromoValidity) {
        _postCreditPromoValidity = creditPromoValidity;
    }

    public Date getNewExpiry() {
        return _newExpiry;
    }

    public void setNewExpiry(Date newExpiry) {
        _newExpiry = newExpiry;
    }

    public Date getPreviousExpiry() {
        return _previousExpiry;
    }

    public void setPreviousExpiry(Date previousExpiry) {
        _previousExpiry = previousExpiry;
    }

    public Date getNewGraceDate() {
        return _newGraceDate;
    }

    public void setNewGraceDate(Date newGraceDate) {
        _newGraceDate = newGraceDate;
    }

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

    public String getPostValidationStatus() {
        return _postValidationStatus;
    }

    public void setPostValidationStatus(String validationStatus) {
        _postValidationStatus = validationStatus;
    }

    public String getInfo1() {
        return _info1;
    }

    public void setInfo1(String info1) {
        _info1 = info1;
    }

    public String getInfo2() {
        return _info2;
    }

    public void setInfo2(String info2) {
        _info2 = info2;
    }

    public String getInfo3() {
        return _info3;
    }

    public void setInfo3(String info3) {
        _info3 = info3;
    }

    public String getInfo4() {
        return _info4;
    }

    public void setInfo4(String info4) {
        _info4 = info4;
    }

    public String getInfo5() {
        return _info5;
    }

    public void setInfo5(String info5) {
        _info5 = info5;
    }

    public String getInfo6() {
        return _info6;
    }

    public void setInfo6(String info6) {
        _info6 = info6;
    }

    public String getInfo7() {
        return _info7;
    }

    public void setInfo7(String info7) {
        _info7 = info7;
    }

    public String getInfo8() {
        return _info8;
    }

    public void setInfo8(String info8) {
        _info8 = info8;
    }

    public String getInfo9() {
        return _info9;
    }

    public void setInfo9(String info9) {
        _info9 = info9;
    }

    public String getInfo10() {
        return _info10;
    }

    public void setInfo10(String info10) {
        _info10 = info10;
    }

    /**
     * @return
     * @author sonali.garg
     */
    public String getMaxCardGroupSlabAmount() {
        return _maxCardGroupSlabAmount;
    }

    /**
     * @param maxCardGroupSlabAmount
     * @author sonali.garg
     */
    public void setMaxCardGroupSlabAmount(String maxCardGroupSlabAmount) {
        _maxCardGroupSlabAmount = maxCardGroupSlabAmount;
    }

    /**
     * @return
     * @author sonali.garg
     */
    public String getMinCardGroupSlabAmount() {
        return _minCardGroupSlabAmount;
    }

    /**
     * @param minCardGroupSlabAmount
     * @author sonali.garg
     */
    public void setMinCardGroupSlabAmount(String minCardGroupSlabAmount) {
        _minCardGroupSlabAmount = minCardGroupSlabAmount;
    }

    public Date getOldtransferDateTime() {
        return _oldtransferDateTime;
    }

    public void setOldtransferDateTime(Date oldtransferDateTime) {
        _oldtransferDateTime = oldtransferDateTime;
    }

    public String getDummyServiceType() {
        return _dummyServiceType;
    }

    public void setDummyServiceType(String serviceType) {
        _dummyServiceType = serviceType;
    }

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

    public String getServiceClassCode() {
        return _serviceClassCode;
    }

    public void setServiceClassCode(String serviceClassCode) {
        _serviceClassCode = serviceClassCode;
    }
    
    public String getGradeCode() {
        return _gradeCode;
    }
    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }
    public String getCategoryCode() {
        return _categoryCode;
    }
    public void setCategoryCode(String code) {
        _categoryCode = code;
    }
    
    public String getReceiverTax3Type() {
		  return _receiverTax3Type;
	  }
	  public void setReceiverTax3Type(String receiverTax3Type) {
		  _receiverTax3Type = receiverTax3Type;
	  }

	  public long getReceiverTax3Value() {
		  return _receiverTax3Value;
	  }
	  public void setReceiverTax3Value(long receiverTax3Value) {
		  _receiverTax3Value = receiverTax3Value;
	  }
	  public String getReceiverTax3ValueAsString() {
		  return PretupsBL.getDisplayAmount(_receiverTax3Value);
	  }

	  public String getReceiverTax3RateAsString()
	  {
		  if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax3Type))
			  return PretupsBL.getDisplayAmount(Double.valueOf(_receiverTax3Rate).longValue());
		  else
		  {
			  return String.valueOf(_receiverTax3Rate);
		  }
	  }
	  
	  public String getReceiverTax4Type() {
		  return _receiverTax4Type;
	  }
	  public void setReceiverTax4Type(String receiverTax4Type) {
		  _receiverTax4Type = receiverTax4Type;
	  }

	  public long getReceiverTax4Value() {
		  return _receiverTax4Value;
	  }
	  public void setReceiverTax4Value(long receiverTax4Value) {
		  _receiverTax4Value = receiverTax4Value;
	  }
	  public String getReceiverTax4ValueAsString() {
		  return PretupsBL.getDisplayAmount(_receiverTax4Value);
	  }
	  public String getReceiverTax4RateAsString()
	  {
		  if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax4Type))
			  return PretupsBL.getDisplayAmount(Double.valueOf(_receiverTax4Rate).longValue());
		  else
		  {
			  return String.valueOf(_receiverTax4Rate);
		  }
	  }
	  public double getReceiverTax3Rate() {
		  return _receiverTax3Rate;
	  }

	  public void setReceiverTax3Rate(double tax3Rate) {
		  _receiverTax3Rate = tax3Rate;
	  }

	  public double getReceiverTax4Rate() {
		  return _receiverTax4Rate;
	  }

	  public void setReceiverTax4Rate(double tax4Rate) {
		  _receiverTax4Rate = tax4Rate;
	  }

	/**
     * @return the interfaceReferenceId
     */
    public String getInterfaceReferenceId() {
	return _interfaceReferenceId;
    }
   /**
    * @param interfaceReferenceId The interfaceReferenceId to set.
    */
    public void setInterfaceReferenceId(String interfaceReferenceId) {
	_interfaceReferenceId = interfaceReferenceId;
	}
    public String getVoucherSegment() {
		return voucherSegment;
	}
	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}
   
    public String getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getVoucherQuantity() {
		return voucherQuantity;
	}

	public void setVoucherQuantity(String voucherQuantity) {
		this.voucherQuantity = voucherQuantity;
	}

	public String getTxnBatchId() {
		return txnBatchId;
	}

	public void setTxnBatchId(String txnBatchId) {
		this.txnBatchId = txnBatchId;
	}
	
	public boolean isInGeoFencing() {
		return isInGeoFencing;
	}

	public void setInGeoFencing(boolean isInGeoFencing) {
		this.isInGeoFencing = isInGeoFencing;
	}
	
}