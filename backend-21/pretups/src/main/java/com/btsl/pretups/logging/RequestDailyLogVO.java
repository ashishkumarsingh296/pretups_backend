package com.btsl.pretups.logging;

/*
 * @(#)RequestDailyLogVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Divyakant Verma 07/02/2008 Initial creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Logger used to track the time, taken by IN during validation and topup.
 */

import java.io.Serializable;

public class RequestDailyLogVO implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private String _instanceID;
    private String _receiverNetworkCode;
    private long _requestRecivedTime;
    private long _requestLogTime;
    private String _requestId;
    private String _serviceType;
    private String _requestSourceType;
    private long _requestExitTime;
    private String _requestSources;// TBD
    private String _senderUserName;
    private String _senderUserID;
    private String _senderCategory;
    private String _senderMSISDN;
    private String _senderNetworkCode;
    private String _transactionStatus;
    private String _errorCode;
    private String ReceiverUserID;
    private String _receiverCategory;
    private String _receiverMSISDN;
    private String _amount;
    private String _subServiceType;
    private long _receiverValidateTime;
    private long _receiverToPUPTime;
    private long _senderValidateTime;
    private long _senderToPUPTime;
    private String _senderInterfaceID;
    private String _receiverInterfaceID;
    private long senderCreditBackTopupTime;
    private long _totalTime;
    private long _preTUPSProcessingTime;
    private String _transactionID;
    private String _INtransactionID;
	private String _INValidationResp;
	private String _INValidationURL;
	private String _INCreditResp;
	private String _INCreditURL;
	
    public String getTransactionID() {
        return _transactionID;
    }

    public void setTransactionID(String transactionID) {
        _transactionID = transactionID;
    }

    public String getAmount() {
        return _amount;
    }

    public void setAmount(String amount) {
        _amount = amount;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    public String getInstanceID() {
        return _instanceID;
    }

    public void setInstanceID(String instanceID) {
        _instanceID = instanceID;
    }

    public String getSenderInterfaceID() {
        return _senderInterfaceID;
    }

    public void setSenderInterfaceID(String senderInterfaceID) {
        _senderInterfaceID = senderInterfaceID;
    }

    public String getReceiverInterfaceID() {
        return _receiverInterfaceID;
    }

    public void setReceiverInterfaceID(String receiverInterfaceID) {
        _receiverInterfaceID = receiverInterfaceID;
    }

    public String getReceiverNetworkCode() {
        return _receiverNetworkCode;
    }

    public void setReceiverNetworkCode(String networkID) {
        _receiverNetworkCode = networkID;
    }

    public long getPreTUPSProcessingTime() {
        // TT - RVT - RTT - SVT -STT
        // in case of C2S module SVT & STT will be 0.
        return _totalTime - _receiverValidateTime - _receiverToPUPTime - _senderValidateTime - _senderToPUPTime;
    }

    public String getReceiverMSISDN() {
        return _receiverMSISDN;
    }

    public void setReceiverMSISDN(String receiverMSISDN) {
        _receiverMSISDN = receiverMSISDN;
    }

    public long getReceiverToPUPTime() {
        return _receiverToPUPTime;
    }

    public void setReceiverToPUPTime(long receiverToPUPTime) {
        _receiverToPUPTime = receiverToPUPTime;
    }

    public long getReceiverValidateTime() {
        return _receiverValidateTime;
    }

    public void setReceiverValidateTime(long receiverValidateTime) {
        _receiverValidateTime = receiverValidateTime;
    }

    public long getSenderToPUPTime() {
        return this._senderToPUPTime;
    }

    public void setSenderToPUPTime(long senderToPUPTime) {
        this._senderToPUPTime = senderToPUPTime;
    }

    public long getSenderValidateTime() {
        return this._senderValidateTime;
    }

    public void setSenderValidateTime(long senderValidateTime) {
        this._senderValidateTime = senderValidateTime;
    }

    public String getRequestId() {
        return _requestId;
    }

    public void setRequestId(String requestId) {
        _requestId = requestId;
    }

    public long getRequestExitTime() {
        return _requestExitTime;
    }

    public void setRequestExitTime(long requestLogTime) {
        _requestExitTime = requestLogTime;
    }

    public long getRequestRecivedTime() {
        return _requestRecivedTime;
    }

    public void setRequestRecivedTime(long requestRecivedTime) {
        _requestRecivedTime = requestRecivedTime;
    }

    public String getRequestSources() {
        return _requestSources;
    }

    public void setRequestSources(String requestSources) {
        _requestSources = requestSources;
    }

    public String getRequestSourceType() {
        return _requestSourceType;
    }

    public void setRequestSourceType(String requestSourceType) {
        _requestSourceType = requestSourceType;
    }

    public String getSenderCategory() {
        return _senderCategory;
    }

    public void setSenderCategory(String senderCategory) {
        _senderCategory = senderCategory;
    }

    public String getSenderMSISDN() {
        return _senderMSISDN;
    }

    public void setSenderMSISDN(String senderMSISDN) {
        _senderMSISDN = senderMSISDN;
    }

    public String getSenderNetworkCode() {
        return _senderNetworkCode;
    }

    public void setSenderNetworkCode(String senderNetworkCode) {
        _senderNetworkCode = senderNetworkCode;
    }

    public String getSenderUserName() {
        return _senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        _senderUserName = senderUserName;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getSubServiceType() {
        return _subServiceType;
    }

    public void setSubServiceType(String subServiceType) {
        _subServiceType = subServiceType;
    }

    public long getTotalTime() {
        return _totalTime;
    }

    public void setTotalTime(long totalTime) {
        _totalTime = totalTime;
    }

    public String getTransactionStatus() {
        return _transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        _transactionStatus = transactionStatus;
    }

    public String getSenderUserID() {
        return _senderUserID;
    }

    public void setSenderUserID(String senderUserID) {
        _senderUserID = senderUserID;
    }

    public void setPreTUPSProcessingTime(long preTUPSProcessingTime) {
        _preTUPSProcessingTime = preTUPSProcessingTime;
    }

    public String getINtransactionID() {
        return this._INtransactionID;
    }

    public void setINtransactionID(String ntransactionID) {
        this._INtransactionID = ntransactionID;
    }

    public long getRequestLogTime() {
        return this._requestLogTime;
    }

    public void setRequestLogTime(long requestLogTime) {
        this._requestLogTime = requestLogTime;
    }

    public String getReceiverUserID() {
        return this.ReceiverUserID;
    }

    public void setReceiverUserID(String receiverUserID) {
        this.ReceiverUserID = receiverUserID;
    }

    public String getReceiverCategory() {
        return this._receiverCategory;
    }

    public void setReceiverCategory(String receiverCategory) {
        this._receiverCategory = receiverCategory;
    }

    public long getSenderCreditBackTopupTime() {
        return this.senderCreditBackTopupTime;
    }

    public void setSenderCreditBackTopupTime(long senderCreditBackTopupTime) {
        this.senderCreditBackTopupTime = senderCreditBackTopupTime;
    }
	public String getINValidationResp() {
		return _INValidationResp;
	}
	public void setINValidationResp(String validationResp) {
		_INValidationResp = validationResp;
	}
	public String getINValidationURL() {
		return _INValidationURL;
	}
	public void setINValidationURL(String validationURL) {
		_INValidationURL = validationURL;
	}
	public String getINCreditResp() {
		return _INCreditResp;
	}
	public void setINCreditResp(String creditResp) {
		_INCreditResp = creditResp;
	}
	public String getINCreditURL() {
		return _INCreditURL;
	}
	public void setINCreditURL(String creditURL) {
		_INCreditURL = creditURL;
	}
}
