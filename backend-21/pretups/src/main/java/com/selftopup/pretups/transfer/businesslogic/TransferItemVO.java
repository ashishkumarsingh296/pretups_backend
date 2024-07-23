package com.selftopup.pretups.transfer.businesslogic;

/**
 * @(#)TransferItemVO.java
 *                         Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Abhijit Chauhan June 22,2005 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.selftopup.pretups.util.PretupsBL;

import java.util.HashMap;

public class TransferItemVO implements Serializable {
    private String _transferID;
    private Date _transferDate;
    private Date _transferDateTime;
    private String _msisdn;
    private Date _entryDate;
    private String _entryDisplayDate;
    private Date _entryDateTime;
    private String _entryDisplayDateTime;
    private long _requestValue;
    private long _previousBalance;
    private long _postBalance;
    private String _transferStatus;
    private String _userType;
    private String _transferType;
    private String _validationStatus;
    private String _updateStatus;
    private long _transferValue;
    private String _interfaceType;
    private String _interfaceID;
    private String _interfaceResponseCode;
    private String _interfaceReferenceID;
    private String _subscriberType;
    private String _serviceClass;
    private String _entryType;
    private Date _previousExpiry;
    private Date _newExpiry;
    private String _interfaceHandlerClass;
    private String _firstCall;
    private int _validity;
    private Object transferVO = null;
    private ArrayList _transferItemList; // List of the Items in the transfer
                                         // request
    private long _prefixID;
    private int _sNo;
    private String _serviceClassCode;
    private String _protocolStatus;
    private String _accountStatus;
    private String _transferValueStr;
    private String _transferStatusMessage;
    private boolean _usingAllServiceClass;
    private String _graceDaysStr;
    private Date _previousGraceDate;
    private Date _newGraceDate;
    private String _interfaceReferenceID1;
    private String _interfaceReferenceID2;
    private String _updateStatus1;
    private String _updateStatus2;
    private String _transferType1;
    private String _transferType2;
    private long _adjustValue;
    private String _interfaceDesc = null;
    private String _referenceID;
    private String _language;
    private String _country;
    private boolean _balanceCheckReq = true;// This flag is introduces so that
                                            // if IN does not send balance at
                                            // the time of validation then it
                                            // should not be validated. Will be
                                            // used for sender generally.

    // added for Get number back service, amount deducted and number back
    // allowed
    private int _amountDeducted;
    private boolean _numberBackAllowed = false;
    private String _valExtAccountStatus;

    // these three contains prev bundle values
    private String _bundleTypes;// bundle codes
    private String _prevBundleBals;
    private String _prevBundleExpiries;

    private String _bonusBundleValidities;
    private String _inAccountId;
    private String _bonus1Name;
    private String _bonus2Name;
    private String _selectorName;
    private String _changedBundleCodes;

    // Parameters for storing Dedicated account information.
    String _dedicatedAccountID;
    String _dedicatedAccountValues;
    String _dedicatedAccountExpiry;
    String _senderDediAccountExpiry;

    // added for lmb
    private double _lmbAllowedBal;
    public Date _subscriberDateEnterActive;
    private HashMap _balanceMap;
    private String _OldExpiryInMillis;
    private long _lmbdebitvalue;
    private String _oldExporyInMillis;
    private String _postValidationStatus;

    // added by nilesh : consolidated for logger
    private String _valInterfaceID;
    private String _interfaceValResponseCode;
    private String _valProtocolStatus;
    private String _promoStatus;
    private String _interfacePromoStatus;
    private Date _previousPromoExpiry;
    private Date _newPromoExpiry;
    private String _cosStatus;
    private long _previousPromoBalance;
    private long _newPromoBalance;
    private String _serviceProviderName;
    private String _newServiceClssCode;

    // vastrix
    private String _previousPromoExpiryInCal;
    private String _previousExpiryInCal;
    private long _postCreditPromoBalance;
    private Date _postCreditPromoValidity;
    private String _interfaceCosStatus;
    private Date _postCreditCoreValidity;
    private long _postCreditCoreBalance;

    /**
     * @return Returns the entryDisplayDateTime.
     */
    public String getEntryDisplayDateTime() {
        return _entryDisplayDateTime;
    }

    /**
     * @param entryDisplayDateTime
     *            The entryDisplayDateTime to set.
     */
    public void setEntryDisplayDateTime(String entryDisplayDateTime) {
        _entryDisplayDateTime = entryDisplayDateTime;
    }

    /**
     * @return Returns the transferItemList.
     */
    public ArrayList getTransferItemList() {
        return _transferItemList;
    }

    /**
     * @param transferItemList
     *            The transferItemList to set.
     */
    public void setTransferItemList(ArrayList transferItemList) {
        _transferItemList = transferItemList;
    }

    /**
     * @return Returns the teansferVO.
     */
    public Object getTransferVO() {
        return transferVO;
    }

    /**
     * @param teansferVO
     *            The teansferVO to set.
     */
    public void setTransferVO(Object transferVO) {
        this.transferVO = transferVO;
    }

    public String toString() {
        return ("_transferID:" + _transferID + " _msisdn:" + _msisdn + " _subscriberType:" + _subscriberType + " _userType:" + _userType + " _transferType:" + _transferType + "_validationStatus:" + _validationStatus + " _updateStatus:" + _updateStatus + " _transferValue:" + _transferValue + " _transferStatus:" + _transferStatus + "_serviceClass:" + _serviceClass + " _interfaceResponseCode:" + _interfaceResponseCode);
    }

    /**
     * @return Returns the accessFee.
     */
    /**
     * @return Returns the entryDate.
     */
    public Date getEntryDate() {
        return _entryDate;
    }

    /**
     * @param entryDate
     *            The entryDate to set.
     */
    public void setEntryDate(Date entryDate) {
        _entryDate = entryDate;
    }

    /**
     * @return Returns the entryDateTime.
     */
    public Date getEntryDateTime() {
        return _entryDateTime;
    }

    /**
     * @param entryDateTime
     *            The entryDateTime to set.
     */
    public void setEntryDateTime(Date entryDateTime) {
        _entryDateTime = entryDateTime;
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

    /**
     * @return Returns the interfaceType.
     */
    public String getInterfaceType() {
        return _interfaceType;
    }

    /**
     * @param interfaceType
     *            The interfaceType to set.
     */
    public void setInterfaceType(String interfaceType) {
        _interfaceType = interfaceType;
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

    /**
     * @return Returns the requestValue.
     */
    public long getRequestValue() {
        return _requestValue;
    }

    /**
     * @param requestValue
     *            The requestValue to set.
     */
    public void setRequestValue(long requestValue) {
        _requestValue = requestValue;
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
     * @return Returns the transferID.
     */
    public String getTransferID() {
        return _transferID;
    }

    /**
     * @param transferID
     *            The transferID to set.
     */
    public void setTransferID(String transferID) {
        _transferID = transferID;
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
     * @return Returns the transferType.
     */
    public String getTransferType() {
        return _transferType;
    }

    /**
     * @param transferType
     *            The transferType to set.
     */
    public void setTransferType(String transferType) {
        _transferType = transferType;
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

    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param userType
     *            The userType to set.
     */
    public void setUserType(String userType) {
        _userType = userType;
    }

    public String getEntryType() {
        return _entryType;
    }

    public void setEntryType(String entryType) {
        _entryType = entryType;
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

    public String getInterfaceHandlerClass() {
        return _interfaceHandlerClass;
    }

    public void setInterfaceHandlerClass(String interfaceHandlerClass) {
        _interfaceHandlerClass = interfaceHandlerClass;
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

    public String getEntryDisplayDate() {
        return _entryDisplayDate;
    }

    public void setEntryDisplayDate(String entryDisplayDate) {
        _entryDisplayDate = entryDisplayDate;
    }

    public int getValidity() {
        return _validity;
    }

    public void setValidity(int validity) {
        _validity = validity;
    }

    public long getPrefixID() {
        return _prefixID;
    }

    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
    }

    public int getSNo() {
        return _sNo;
    }

    public void setSNo(int no) {
        _sNo = no;
    }

    public String getServiceClassCode() {
        return _serviceClassCode;
    }

    public void setServiceClassCode(String serviceClassCode) {
        _serviceClassCode = serviceClassCode;
    }

    public String getAccountStatus() {
        return _accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        _accountStatus = accountStatus;
    }

    public String getProtocolStatus() {
        return _protocolStatus;
    }

    public void setProtocolStatus(String protocolStatus) {
        _protocolStatus = protocolStatus;
    }

    public String getInterfaceResponseCode() {
        return _interfaceResponseCode;
    }

    public void setInterfaceResponseCode(String interfaceResponseCode) {
        _interfaceResponseCode = interfaceResponseCode;
    }

    public String getTransferValueStr() {
        return _transferValueStr;
    }

    public void setTransferValueStr(String transferValueStr) {
        _transferValueStr = transferValueStr;
    }

    public String getTransferStatusMessage() {
        return _transferStatusMessage;
    }

    public void setTransferStatusMessage(String transferStatusMessage) {
        _transferStatusMessage = transferStatusMessage;
    }

    public boolean isUsingAllServiceClass() {
        return _usingAllServiceClass;
    }

    public void setUsingAllServiceClass(boolean usingAllServiceClass) {
        _usingAllServiceClass = usingAllServiceClass;
    }

    public String getGraceDaysStr() {
        return _graceDaysStr;
    }

    public void setGraceDaysStr(String graceDaysStr) {
        _graceDaysStr = graceDaysStr;
    }

    public Date getNewGraceDate() {
        return _newGraceDate;
    }

    public void setNewGraceDate(Date newGraceDate) {
        _newGraceDate = newGraceDate;
    }

    public Date getPreviousGraceDate() {
        return _previousGraceDate;
    }

    public void setPreviousGraceDate(Date previousGraceDate) {
        _previousGraceDate = previousGraceDate;
    }

    public String getUpdateStatus1() {
        return _updateStatus1;
    }

    public void setUpdateStatus1(String updateStatus1) {
        _updateStatus1 = updateStatus1;
    }

    public String getUpdateStatus2() {
        return _updateStatus2;
    }

    public void setUpdateStatus2(String updateStatus2) {
        _updateStatus2 = updateStatus2;
    }

    public String getTransferType1() {
        return _transferType1;
    }

    public void setTransferType1(String transferType1) {
        _transferType1 = transferType1;
    }

    public String getTransferType2() {
        return _transferType2;
    }

    public void setTransferType2(String transferType2) {
        _transferType2 = transferType2;
    }

    public long getAdjustValue() {
        return _adjustValue;
    }

    public void setAdjustValue(long adjustValue) {
        _adjustValue = adjustValue;
    }

    public String getInterfaceDesc() {
        return _interfaceDesc;
    }

    public void setInterfaceDesc(String interfaceDesc) {
        _interfaceDesc = interfaceDesc;
    }

    public String getInterfaceReferenceID() {
        return _interfaceReferenceID;
    }

    public void setInterfaceReferenceID(String interfaceReferenceID) {
        _interfaceReferenceID = interfaceReferenceID;
    }

    public String getInterfaceReferenceID1() {
        return _interfaceReferenceID1;
    }

    public void setInterfaceReferenceID1(String interfaceReferenceID1) {
        _interfaceReferenceID1 = interfaceReferenceID1;
    }

    public String getInterfaceReferenceID2() {
        return _interfaceReferenceID2;
    }

    public void setInterfaceReferenceID2(String interfaceReferenceID2) {
        _interfaceReferenceID2 = interfaceReferenceID2;
    }

    public String getReferenceID() {
        return _referenceID;
    }

    public void setReferenceID(String referenceID) {
        _referenceID = referenceID;
    }

    public String getCountry() {
        return _country;
    }

    public void setCountry(String country) {
        _country = country;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String language) {
        _language = language;
    }

    /**
     * @return Returns the balanceCheckReq.
     */
    public boolean isBalanceCheckReq() {
        return _balanceCheckReq;
    }

    /**
     * @param balanceCheckReq
     *            The balanceCheckReq to set.
     */
    public void setBalanceCheckReq(boolean balanceCheckReq) {
        _balanceCheckReq = balanceCheckReq;
    }

    /**
     * @return Returns the amountDeducted.
     */
    public int getAmountDeducted() {
        return _amountDeducted;
    }

    /**
     * @param amountDeducted
     *            The amountDeducted to set.
     */
    public void setAmountDeducted(int amountDeducted) {
        _amountDeducted = amountDeducted;
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
     * @return Returns the valExtAccountStatus.
     */
    public String getValExtAccountStatus() {
        return _valExtAccountStatus;
    }

    /**
     * @param valExtAccountStatus
     *            The valExtAccountStatus to set.
     */
    public void setValExtAccountStatus(String valExtAccountStatus) {
        _valExtAccountStatus = valExtAccountStatus;
    }

    /**
     * @return Returns the getBundleTypes.
     */
    public String getBundleTypes() {
        return _bundleTypes;
    }

    /**
     * @param bundleTypes
     *            The setBundleTypes to set.
     */
    public void setBundleTypes(String bundleTypes) {
        _bundleTypes = bundleTypes;
    }

    /**
     * @return Returns the prevBundleBals.
     */
    public String getPrevBundleBals() {
        return _prevBundleBals;
    }

    /**
     * @param prevBundleBals
     *            The prevBundleBals to set.
     */
    public void setPrevBundleBals(String prevBundleBals) {
        _prevBundleBals = prevBundleBals;
    }

    /**
     * @return Returns the prevBundleExpiries.
     */
    public String getPrevBundleExpiries() {
        return _prevBundleExpiries;
    }

    /**
     * @param prevBundleExpiries
     *            The prevBundleExpiries to set.
     */
    public void setPrevBundleExpiries(String prevBundleExpiries) {
        _prevBundleExpiries = prevBundleExpiries;
    }

    public String getBonusBundleValidities() {
        return _bonusBundleValidities;
    }

    /**
     * @param bonusBundleValidities
     *            The setBonusBundleValidities to set.
     */
    public void setBonusBundleValidities(String bonusBundleValidities) {
        _bonusBundleValidities = bonusBundleValidities;
    }

    public String getInAccountId() {
        return _inAccountId;
    }

    public void setInAccountId(String inAccountId) {
        _inAccountId = inAccountId;
    }

    public String getBonus1Name() {
        return _bonus1Name;
    }

    public void setBonus1Name(String bonus1Name) {
        _bonus1Name = bonus1Name;
    }

    public String getBonus2Name() {
        return _bonus2Name;
    }

    public void setBonus2Name(String bonus2Name) {
        _bonus2Name = bonus2Name;
    }

    public String getSelectorName() {
        return _selectorName;
    }

    public void setSelectorName(String selectorName) {
        _selectorName = selectorName;
    }

    /**
     * @return Returns the changedBundleCodes.
     */
    public String getChangedBundleCodes() {
        return _changedBundleCodes;
    }

    /**
     * @param changedBundleCodes
     *            The changedBundleCodes to set.
     */
    public void setChangedBundleCodes(String changedBundleCodes) {
        _changedBundleCodes = changedBundleCodes;
    }

    public String getDedicatedAccountID() {
        return _dedicatedAccountID;
    }

    public void setDedicatedAccountID(String accountID) {
        _dedicatedAccountID = accountID;
    }

    public String getDedicatedAccountValues() {
        return _dedicatedAccountValues;
    }

    public void setDedicatedAccountValues(String accountValues) {
        _dedicatedAccountValues = accountValues;
    }

    public String getDedicatedAccountExpiry() {
        return _dedicatedAccountExpiry;
    }

    public void setDedicatedAccountExpiry(String accountExpiry) {
        _dedicatedAccountExpiry = accountExpiry;
    }

    public String getSenderDediAccountExpiry() {
        return _senderDediAccountExpiry;
    }

    public void setSenderDediAccountExpiry(String dediAccountExpiry) {
        _senderDediAccountExpiry = dediAccountExpiry;
    }

    public double getLmbAllowedBal() {
        return _lmbAllowedBal;
    }

    public void setLmbAllowedBal(double allowedBal) {
        _lmbAllowedBal = allowedBal;
    }

    public HashMap getBalanceMap() {
        return _balanceMap;
    }

    public void setBalanceMap(HashMap map) {
        _balanceMap = map;
    }

    public String getOldExpiryInMillis() {
        return _OldExpiryInMillis;
    }

    public void setOldExpiryInMillis(String oldExpiryInMillis) {
        _OldExpiryInMillis = oldExpiryInMillis;
    }

    public long getLmbdebitvalue() {
        return _lmbdebitvalue;
    }

    public void setLmbdebitvalue(long lmbdebitvalue) {
        _lmbdebitvalue = lmbdebitvalue;
    }

    /**
     * @return Returns the oldExporyInMillis.
     */
    public String getOldExporyInMillis() {
        return _oldExporyInMillis;
    }

    /**
     * @param oldExporyInMillis
     *            The oldExporyInMillis to set.
     */
    public void setOldExporyInMillis(String oldExporyInMillis) {
        _oldExporyInMillis = oldExporyInMillis;
    }

    public String getPostValidationStatus() {
        return _postValidationStatus;
    }

    public void setPostValidationStatus(String validationStatus) {
        _postValidationStatus = validationStatus;
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
     * @return Returns the previousPromoExpiry.
     */
    public Date getPreviousPromoExpiry() {
        return _previousPromoExpiry;
    }

    /**
     * @param p_previousPromoExpiry
     *            The previousPromoExpiry to set.
     */
    public void setPreviousPromoExpiry(Date p_previousPromoExpiry) {
        _previousPromoExpiry = p_previousPromoExpiry;
    }

    /**
     * @return Returns the newPromoExpiry.
     */
    public Date getNewPromoExpiry() {
        return _newPromoExpiry;
    }

    /**
     * @param p_newPromoExpiry
     *            The newPromoExpiry to set.
     */
    public void setNewPromoExpiry(Date p_newPromoExpiry) {
        _newPromoExpiry = p_newPromoExpiry;
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

    /**
     * @return Returns the previousPromoBalance.
     */
    public long getPreviousPromoBalance() {
        return _previousPromoBalance;
    }

    /**
     * @param p_previousPromoBalance
     *            The previousPromoBalance to set.
     */
    public void setPreviousPromoBalance(long p_previousPromoBalance) {
        _previousPromoBalance = p_previousPromoBalance;
    }

    /**
     * @return Returns the newPromoBalance.
     */
    public long getNewPromoBalance() {
        return _newPromoBalance;
    }

    /**
     * @param p_newPromoBalance
     *            The newPromoBalance to set.
     */
    public void setNewPromoBalance(long p_newPromoBalance) {
        _newPromoBalance = p_newPromoBalance;
    }

    /**
     * @param p_serviceProviderName
     *            The serviceProviderName to set.
     */
    public String getServiceProviderName() {
        return _serviceProviderName;
    }

    /**
     * @param p_serviceProviderName
     *            The serviceProviderName to set.
     */
    public void setServiceProviderName(String p_serviceProviderName) {
        _serviceProviderName = p_serviceProviderName;
    }

    /**
     * @return Returns the newServiceClssCode.
     */
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
}
