package com.selftopup.pretups.channel.transfer.businesslogic;

/*
 * @(#)C2STransferVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.io.Serializable;

import com.selftopup.pretups.transfer.businesslogic.TransferVO;

public class C2STransferVO extends TransferVO implements Serializable {

    private String _senderNetworkCode;
    private String _referenceID;
    private String _pinSentToMsisdn;
    private String _language;
    private String _domainCode;
    private String _ownerUserID;
    private boolean _transferProfileCtInitializeReqd = false;
    private String _differentialGiven;
    private String _grphDomainCode;
    private String value;
    private long _requestStartTime;
    private String _senderCategoryCode;
    private String _processed;
    private String _txnCalculationDone;
    private long _counts;
    private String _userId;
    private String _categoryCode;

    // added by vikram for vfe
    private String _activeUserId;
    private String _activeUserName;
    // to check the activation bonus field is present in the response from IN.
    private String _activeBonusProvided;
    // added by nilesh :consolidated for logger
    private String _cellId;
    private String _switchId;
    private long _totalCommission;
    private String _reverseTransferID;
    private String _subscriberSID;// added by rahuld for private recharge
    // vastrix
    private String _serviceProviderName;

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String language) {
        _language = language;
    }

    public String getPinSentToMsisdn() {
        return _pinSentToMsisdn;
    }

    public void setPinSentToMsisdn(String pinSentToMsisdn) {
        _pinSentToMsisdn = pinSentToMsisdn;
    }

    public String getReferenceID() {
        return _referenceID;
    }

    public void setReferenceID(String referenceID) {
        _referenceID = referenceID;
    }

    public String getSenderNetworkCode() {
        return _senderNetworkCode;
    }

    public void setSenderNetworkCode(String senderNetworkCode) {
        _senderNetworkCode = senderNetworkCode;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public String getOwnerUserID() {
        return _ownerUserID;
    }

    public void setOwnerUserID(String ownerUserID) {
        _ownerUserID = ownerUserID;
    }

    public boolean isTransferProfileCtInitializeReqd() {
        return _transferProfileCtInitializeReqd;
    }

    public void setTransferProfileCtInitializeReqd(boolean transferProfileCtInitializeReqd) {
        _transferProfileCtInitializeReqd = transferProfileCtInitializeReqd;
    }

    public String getDifferentialGiven() {
        return _differentialGiven;
    }

    public void setDifferentialGiven(String differentialGiven) {
        _differentialGiven = differentialGiven;
    }

    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    public void setGrphDomainCode(String grphDomainCode) {
        _grphDomainCode = grphDomainCode;
    }

    public long getRequestStartTime() {
        return _requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        _requestStartTime = requestStartTime;
    }

    /**
     * @return Returns the processed.
     */
    public String getProcessed() {
        return _processed;
    }

    /**
     * @param processed
     *            The processed to set.
     */
    public void setProcessed(String processed) {
        _processed = processed;
    }

    /**
     * @return Returns the senderCategoryCode.
     */
    public String getSenderCategoryCode() {
        return _senderCategoryCode;
    }

    /**
     * @param senderCategoryCode
     *            The senderCategoryCode to set.
     */
    public void setSenderCategoryCode(String senderCategoryCode) {
        _senderCategoryCode = senderCategoryCode;
    }

    /**
     * @return Returns the txnCalculationDone.
     */
    public String getTxnCalculationDone() {
        return _txnCalculationDone;
    }

    /**
     * @param txnCalculationDone
     *            The txnCalculationDone to set.
     */
    public void setTxnCalculationDone(String txnCalculationDone) {
        _txnCalculationDone = txnCalculationDone;
    }

    /**
     * @return Returns the counts.
     */
    public long getCounts() {
        return _counts;
    }

    /**
     * @param counts
     *            The counts to set.
     */
    public void setCounts(long counts) {
        _counts = counts;
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
     * @return the activeUserId
     */
    public String getActiveUserId() {
        return _activeUserId;
    }

    /**
     * @param activeUserId
     *            the activeUserId to set
     */
    public void setActiveUserId(String activeUserId) {
        this._activeUserId = activeUserId;
    }

    /**
     * @return the activeUserName
     */
    public String getActiveUserName() {
        return _activeUserName;
    }

    /**
     * @param activeUserName
     *            the activeUserName to set
     */
    public void setActiveUserName(String activeUserName) {
        this._activeUserName = activeUserName;
    }

    public String getActiveBonusProvided() {
        return _activeBonusProvided;
    }

    public void setActiveBonusProvided(String bonusAccID) {
        _activeBonusProvided = bonusAccID;
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
    public void setswitchId(String id) {
        _switchId = id;
    }

    // added by gaurav
    public void setSwitchID(String id) {
        _switchId = id;
    }

    /**
     * @return Returns the totalCommission.
     */
    public long getTotalCommission() {
        return _totalCommission;
    }

    /**
     * @param totalCommission
     *            The totalCommission to set.
     */
    public void setTotalCommission(long totalCommission) {
        _totalCommission = totalCommission;
    }

    /**
     * @return Returns the reverseTransferID.
     */
    public String getReverseTransferID() {
        return _reverseTransferID;
    }

    /**
     * @param reverseTransferID
     *            The reverseTransferID to set.
     */
    public void setReverseTransferID(String reverseTransferID) {
        _reverseTransferID = reverseTransferID;
    }

    /**
     * @return the subscriberSID
     */
    public String getSubscriberSID() {
        return _subscriberSID;
    }

    /**
     * @param subscriberSID
     *            the subscriberSID to set
     */
    public void setSubscriberSID(String subscriberSID) {
        _subscriberSID = subscriberSID;
    }

    public String getServiceProviderName() {
        return _serviceProviderName;
    }

    public void setServiceProviderName(String providerName) {
        _serviceProviderName = providerName;
    }
}
