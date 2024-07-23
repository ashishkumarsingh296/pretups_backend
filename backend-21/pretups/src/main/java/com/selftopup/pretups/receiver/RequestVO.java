package com.selftopup.pretups.receiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.selftopup.pretups.gateway.businesslogic.MessageGatewayVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.MCDListVO;

/*
 * RequestVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 28/06/2005 Initial Creation
 * Saurabh 14/12/2006 Modified changeID=TATASKYRCHG
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * This class is used to collect the Request parameter details that are received
 * for processing
 */
public class RequestVO extends MCDListVO implements Serializable {
    private long _requestID;
    private String _requestIDStr;
    private String _requestMSISDN;
    private String _filteredMSISDN;
    private String _requestMessage;
    private String[] _requestMessageArray;
    private Object _senderVO;
    private String _serviceType;
    private String _sourceType;
    private String _type;
    private String _module;
    private String _instanceID;
    private String _login;
    private String _password;
    private String _requestGatewayCode;
    private String _requestGatewayType;

    private String _messageCode;
    private String[] _messageArguments; // arguments send with the message
    private String _remoteIP;
    private String _servicePort;
    private int _remotePort;
    private boolean _decreaseLoadCounters = false;
    private boolean _decreaseNetworkLoadCounters = false;

    // Gurjeet: Added for C2S
    private String _decryptedMessage;
    private Date _createdOn;
    private String _UDH;
    private String _UDHHex;
    private boolean _isPlainMessage = false;
    private String _tempTransID;

    private Object _fixedInformationVO;
    private Locale _locale;
    private String _simProfileID;
    private String _msgResponseType;
    private String _incomingSmsStr;

    private MessageGatewayVO _messageGatewayVO;

    private String _senderReturnMessage;
    private boolean _senderMessageRequired = true;
    private boolean _unmarkSenderUnderProcess = true;

    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;

    private String _reqContentType = null;
    private String _reqSelector = null;
    private int _actionValue = -1;
    private boolean _successTxn = true;
    private String _transactionID = null;
    private String _actualMessageFormat;
    private long _requestStartTime;
    private String _useInterfaceLanguage = null;
    private boolean _performIntfceCatRoutingBeforeVal = false;
    private boolean _senderInterfaceInfoInDBFound = false;
    private boolean _receiverInterfaceInfoInDBFound = false;
    private boolean _senderDeletionReqFromSubRouting = false;
    private boolean _receiverDeletionReqFromSubRouting = false;
    private boolean _interfaceCatRoutingDone = false;
    private String _groupType = null;
    private boolean _decreaseGroupTypeCounter = true;
    private boolean _pushMessage = true;// This field will be used to decide
                                        // whether message has to be pushed to
                                        // sender or not
    private String _requestNetworkCode = null;
    private Object _valueObject = null;
    private String _externalNetworkCode = null;
    private String _senderExternalCode = null;
    private String _senderLoginID = null;
    private HashMap _requestMap = null;
    private boolean _pinValidationRequired = true;
    private boolean _passwordValidationRequired = false;
    private String _param1 = null;// added for DP6
    private boolean _messageAlreadyParsed = false;// added for DP6
    private boolean _unmarkSenderRequired = true;// Added For Registration
                                                 // process

    private String _notificationMSISDN = null;// cahngeID=TATASKYRCHG added by
                                              // saurabh for notification
                                              // nnumber of tatasky controller.
    private String _externalReferenceNum = null;// To enquire Transaction Status
                                                // with External Refrence Number

    // added for c2s enquiry handler : Ranjana
    private String _slabAmount = null;// get the slab amount corresponding to
                                      // requested service
    private boolean _slab = false;
    private String _enquiryServiceType = null;
    private String _receiverServiceClassId = null;
    private String _receiverSubscriberType = null;
    // Add for MSISDn not found In IN 31/01/08
    private String _intMsisdnNotFound = null;
    // End of 31/01/08

    // Added for logging the time taken by IN for sender validation & topup
    // 07/02/2008
    private long _validationSenderRequestSent;
    private long _validationSenderResponseReceived;
    private long _topUPSenderRequestSent;
    private long _topUPSenderResponseReceived;
    // Added for logging the time taken by IN for receiver validation & topup
    // 07/02/2008
    private long _validationReceiverRequestSent;
    private long _validationReceiverResponseReceived;
    private long _topUPReceiverRequestSent;
    private long _topUPReceiverResponseReceived;

    // For gift Recharge.
    private Locale _gifterLocale = null;
    private String _gifterMSISDN = null;
    private String _gifterName = null;

    // Changes for c2c transfer
    private String _receiverExtCode = null;
    private String _receiverLoginID = null;
    private String _receiverMsisdn = null;
    private String _ramount = null;
    private String _mobileLineQty = null;
    private String _fixedLineQty = null;

    private String _requestLoginId = null;
    private String _activerUserId = null;
    private String _messageSentMsisdn = null;
    private String _parentMsisdnPOS = null;
    private String _posMSISDN = null;
    private String _posUserMSISDN = null;
    private String _evdPin = null;
    private String _sid = null;

    // For CRBT Song Selection by shashank
    private String _songCode = null;

    private boolean _privateRechBinMsgAllowed = false;

    // Changes for GMB
    private String _cellId;
    private String _switchId;
    private String _serviceKeyword = null;
    private String _ussdSessionID = null;

    // For C2C DrCr transfer
    private String _txntype = null;
    private String _purpose = null;
    // added for voms
    private String _serialNo = null;
    private long _voucherAmount = 0;
    private String _consumed = "N";
    private String _vomsMessage = null;
    private String _vomsError = null;
    private String _vomsRegion = null;
    private String _vomsValid = null;

    private long _postValidationTimeTaken;

    private String _userCategory = null;

    // Added for Multiple Credit List CR
    private String _mcdListName = null;
    private int _mcdListAddCount = 0;
    private String _mcdPIn = null;

    // For DMS module
    private String _txnAuthStatus = null;

    // vastrix -- logs
    private long _creditTime = 0L;
    private long _promoTime = 0L;
    private long _cosTime = 0L;
    private long _creditValTime = 0L;
    private long _promoValTime = 0L;
    private long _cosValTime = 0L;

    // added by shashank for channel user authentication 17/JAN/2013
    // private String _txnAuthStatus = null;
    private String _validatePassword = null;
    private String _validatePin = null;
    private String _validMSISDN = null;
    // added by sonali
    private String _externalTransactionNum = null;
    // added by sonali
    private String _remarks = null;
    // for queue implementation logic
    private boolean _toBeProcessedFromQueue = false;

    // added by harsh for Scheduled (Add/Modify/Delete) Request
    private String _mcdSenderProfile = null;
    private String _mcdScheduleType = null;
    private String _mcdListStatus = null;
    private String _mcdFailRecords = "";
    private String _mcdNoOfSchedules = "";
    private String _mcdNextScheduleDate = "";
    // added by Pradyumn for Scheduled Credit Transfer (View/Delete complete
    // List API)
    private Date _executedUpto;

    // Added By Diwakar on 20-JAN-2014 for ROBI
    private String _newPassword;
    private String _confirmNewPassword;
    private String _reqDate;
    // private String _categoryCode;
    private String _employeeCode;
    private String _ssn;
    // ENded here by Diwakqar

    private String _categoryCode = null;
    private String _txnDate = null;
    private String _extTxnDate = null;
    private int _invoiceSize;
    private String _xMontoDeudaTotal;
    private String _xOpcionRecaudacion;
    private String _xCodTipoServicio;
    private String _invoiceno;
    private ArrayList _enquiryItemList; // List of the Items in the transfer
                                        // request

    // added by narendra for vfe6 cr

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
    private String _recmsg = "Y";
    // By rahul for PPB Enq// on same line as for c2s enquiry handler
    private String _minAmtDue = null;
    private String _dueAmt = null;
    private String _dueDate = null;

    // added by sonali for self topup
    // private String _imei=null;
    private String _emailId = null;
    private int _numberOfRegisteredCards = 0;
    private String _userLoginId = null;
    // Changes ended.

    // added by Vikas Singh
    private String _newNickName = null;
    private String _nickName = null;
    private String _pin = null;
    private String _cvv = null;

    // added by sonali
    private String _imei = null;
    private String _requestType = null;
    private String _encryptionKey = null;

    public boolean getPrivateRechBinMsgAllowed() {
        return _privateRechBinMsgAllowed;
    }

    public void setPrivateRechBinMsgAllowed(boolean privateRechBinMsgAllowed) {
        _privateRechBinMsgAllowed = privateRechBinMsgAllowed;
    }

    public String getSid() {
        return _sid;
    }

    public void setSid(String sid) {
        _sid = sid;
    }

    /**
     * @return the evdPin
     */
    public String getEvdPin() {
        return _evdPin;
    }

    /**
     * @param evdPin
     *            the evdPin to set
     */
    public void setEvdPin(String evdPin) {
        _evdPin = evdPin;
    }

    /**
     * @return Returns the requestMessage.
     */
    public String getRequestMessage() {
        return _requestMessage;
    }

    /**
     * @param requestMessage
     *            The requestMessage to set.
     */
    public void setRequestMessage(String requestMessage) {
        _requestMessage = requestMessage;
    }

    /**
     * @return Returns the filteredMSISDN.
     */
    public String getFilteredMSISDN() {
        return _filteredMSISDN;
    }

    /**
     * @param filteredMSISDN
     *            The filteredMSISDN to set.
     */
    public void setFilteredMSISDN(String filteredMSISDN) {
        _filteredMSISDN = filteredMSISDN;
    }

    /**
     * @return Returns the requestMSISDN.
     */
    public String getRequestMSISDN() {
        return _requestMSISDN;
    }

    /**
     * @param requestMSISDN
     *            The requestMSISDN to set.
     */
    public void setRequestMSISDN(String requestMSISDN) {
        _requestMSISDN = requestMSISDN;
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
     * @return Returns the requestMessageArray.
     */
    public String[] getRequestMessageArray() {
        return _requestMessageArray;
    }

    /**
     * @param requestMessageArray
     *            The requestMessageArray to set.
     */
    public void setRequestMessageArray(String[] requestMessageArray) {
        _requestMessageArray = requestMessageArray;
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

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the messageArguments.
     */
    public String[] getMessageArguments() {
        return _messageArguments;
    }

    /**
     * @param messageArguments
     *            The messageArguments to set.
     */
    public void setMessageArguments(String[] messageArguments) {
        _messageArguments = messageArguments;
    }

    /**
     * @return Returns the messageCode.
     */
    public String getMessageCode() {
        return _messageCode;
    }

    /**
     * @param messageCode
     *            The messageCode to set.
     */
    public void setMessageCode(String messageCode) {
        _messageCode = messageCode;
    }

    /**
     * @return Returns the login.
     */
    public String getLogin() {
        return _login;
    }

    /**
     * @param login
     *            The login to set.
     */
    public void setLogin(String login) {
        _login = login;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return _password;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword(String password) {
        _password = password;
    }

    /**
     * @return Returns the remoteIP.
     */
    public String getRemoteIP() {
        return _remoteIP;
    }

    /**
     * @param remoteIP
     *            The remoteIP to set.
     */
    public void setRemoteIP(String remoteIP) {
        _remoteIP = remoteIP;
    }

    /**
     * @return Returns the servicePort.
     */
    public String getServicePort() {
        return _servicePort;
    }

    /**
     * @param servicePort
     *            The servicePort to set.
     */
    public void setServicePort(String servicePort) {
        _servicePort = servicePort;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_requestID:" + _requestID);
        sbf.append(" _requestMSISDN:" + _requestMSISDN);
        sbf.append(" _filteredMSISDN:" + _filteredMSISDN);
        int index = _requestMessage.indexOf("CARDNO=");
        if (index != -1) {
            sbf.append(" _requestMessage:" + _requestMessage.substring(0, index + "CARDNO=".length()) + "************" + _requestMessage.substring(index + "CARDNO=".length() + 12));
        } else {
            sbf.append(" _requestMessage:" + _requestMessage);
        }
        sbf.append(" _module:" + _module);
        sbf.append(" _instanceID:" + _instanceID);
        sbf.append(" _login:" + _login);
        sbf.append(" _password:" + _password);
        sbf.append(" _requestGatewayCode:" + _requestGatewayCode);
        sbf.append(" _requestGatewayType:" + _requestGatewayType);
        sbf.append(" _remoteIP:" + _remoteIP);
        sbf.append(" _servicePort:" + _servicePort);
        sbf.append(" _remotePort:" + _remotePort);
        sbf.append(" _useInterfaceLanguage:" + _useInterfaceLanguage);
        sbf.append(" _txnAuthStatus:" + _txnAuthStatus); // added by shashank
                                                         // for channel user
                                                         // authentication
        sbf.append(" _validatePassword:" + _validatePassword);
        sbf.append(" _validatePin:" + _validatePin);
        sbf.append(" _validMSISDN:" + _validMSISDN);// end
        return sbf.toString();

    }

    /**
     * @return Returns the remotePort.
     */
    public int getRemotePort() {
        return _remotePort;
    }

    /**
     * @param remotePort
     *            The remotePort to set.
     */
    public void setRemotePort(int remotePort) {
        _remotePort = remotePort;
    }

    public boolean isDecreaseLoadCounters() {
        return _decreaseLoadCounters;
    }

    public void setDecreaseLoadCounters(boolean decreaseLoadCounters) {
        _decreaseLoadCounters = decreaseLoadCounters;
    }

    public long getRequestID() {
        return _requestID;
    }

    public void setRequestID(long requestID) {
        _requestID = requestID;
        _requestIDStr = String.valueOf(requestID);
    }

    public String getRequestIDStr() {
        return _requestIDStr;
    }

    public void setRequestIDStr(String requestIDStr) {
        _requestIDStr = requestIDStr;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getUDH() {
        return _UDH;
    }

    public void setUDH(String udh) {
        _UDH = udh;
    }

    public boolean isPlainMessage() {
        return _isPlainMessage;
    }

    public void setPlainMessage(boolean isPlainMessage) {
        _isPlainMessage = isPlainMessage;
    }

    public String getDecryptedMessage() {
        return _decryptedMessage;
    }

    public void setDecryptedMessage(String decryptedMessage) {
        _decryptedMessage = decryptedMessage;
    }

    public String getTempTransID() {
        return _tempTransID;
    }

    public void setTempTransID(String tempTransID) {
        _tempTransID = tempTransID;
    }

    public Object getFixedInformationVO() {
        return _fixedInformationVO;
    }

    public void setFixedInformationVO(Object fixedInformationVO) {
        _fixedInformationVO = fixedInformationVO;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    /**
     * @return Returns the simProfileID.
     */
    public String getSimProfileID() {
        return _simProfileID;
    }

    /**
     * @param simProfileID
     *            The simProfileID to set.
     */
    public void setSimProfileID(String simProfileID) {
        _simProfileID = simProfileID;
    }

    public String getSenderReturnMessage() {
        return _senderReturnMessage;
    }

    public void setSenderReturnMessage(String senderReturnMessage) {
        _senderReturnMessage = senderReturnMessage;
    }

    /**
     * @return Returns the uDHHex.
     */
    public String getUDHHex() {
        return _UDHHex;
    }

    /**
     * @param hex
     *            The uDHHex to set.
     */
    public void setUDHHex(String hex) {
        _UDHHex = hex;
    }

    /**
     * @return Returns the messageGatewayVO.
     */
    public MessageGatewayVO getMessageGatewayVO() {
        return _messageGatewayVO;
    }

    /**
     * @param messageGatewayVO
     *            The messageGatewayVO to set.
     */
    public void setMessageGatewayVO(MessageGatewayVO messageGatewayVO) {
        _messageGatewayVO = messageGatewayVO;
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

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getSourceType() {
        return _sourceType;
    }

    public void setSourceType(String sourceType) {
        _sourceType = sourceType;
    }

    public boolean isSenderMessageRequired() {
        return _senderMessageRequired;
    }

    public void setSenderMessageRequired(boolean senderMessageRequired) {
        _senderMessageRequired = senderMessageRequired;
    }

    public boolean isUnmarkSenderUnderProcess() {
        return _unmarkSenderUnderProcess;
    }

    public void setUnmarkSenderUnderProcess(boolean unmarkSenderUnderProcess) {
        _unmarkSenderUnderProcess = unmarkSenderUnderProcess;
    }

    public String getMsgResponseType() {
        return _msgResponseType;
    }

    public void setMsgResponseType(String msgResponseType) {
        _msgResponseType = msgResponseType;
    }

    public String getIncomingSmsStr() {
        return _incomingSmsStr;
    }

    public void setIncomingSmsStr(String incomingSmsStr) {
        _incomingSmsStr = incomingSmsStr;
    }

    public String getReqContentType() {
        return _reqContentType;
    }

    public void setReqContentType(String reqContentType) {
        _reqContentType = reqContentType;
    }

    public Locale getReceiverLocale() {
        return _receiverLocale;
    }

    public void setReceiverLocale(Locale receiverLocale) {
        _receiverLocale = receiverLocale;
    }

    public Locale getSenderLocale() {
        return _senderLocale;
    }

    public void setSenderLocale(Locale senderLocale) {
        _senderLocale = senderLocale;
    }

    public String getReqSelector() {
        return _reqSelector;
    }

    public void setReqSelector(String reqSelector) {
        _reqSelector = reqSelector;
    }

    public int getActionValue() {
        return _actionValue;
    }

    public void setActionValue(int actionValue) {
        _actionValue = actionValue;
    }

    public boolean isSuccessTxn() {
        return _successTxn;
    }

    public void setSuccessTxn(boolean successTxn) {
        _successTxn = successTxn;
    }

    public String getTransactionID() {
        return _transactionID;
    }

    public void setTransactionID(String transactionID) {
        _transactionID = transactionID;
    }

    public String getActualMessageFormat() {
        return _actualMessageFormat;
    }

    public void setActualMessageFormat(String actualMessageFormat) {
        _actualMessageFormat = actualMessageFormat;
    }

    public long getRequestStartTime() {
        return _requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        _requestStartTime = requestStartTime;
    }

    public String getUseInterfaceLanguage() {
        return _useInterfaceLanguage;
    }

    public void setUseInterfaceLanguage(String useInterfaceLanguage) {
        _useInterfaceLanguage = useInterfaceLanguage;
    }

    public boolean isPerformIntfceCatRoutingBeforeVal() {
        return _performIntfceCatRoutingBeforeVal;
    }

    public void setPerformIntfceCatRoutingBeforeVal(boolean performIntfceCatRoutingBeforeVal) {
        _performIntfceCatRoutingBeforeVal = performIntfceCatRoutingBeforeVal;
    }

    public boolean isReceiverDeletionReqFromSubRouting() {
        return _receiverDeletionReqFromSubRouting;
    }

    public void setReceiverDeletionReqFromSubRouting(boolean receiverDeletionReqFromSubRouting) {
        _receiverDeletionReqFromSubRouting = receiverDeletionReqFromSubRouting;
    }

    public boolean isReceiverInterfaceInfoInDBFound() {
        return _receiverInterfaceInfoInDBFound;
    }

    public void setReceiverInterfaceInfoInDBFound(boolean receiverInterfaceInfoInDBFound) {
        _receiverInterfaceInfoInDBFound = receiverInterfaceInfoInDBFound;
    }

    public boolean isSenderDeletionReqFromSubRouting() {
        return _senderDeletionReqFromSubRouting;
    }

    public void setSenderDeletionReqFromSubRouting(boolean senderDeletionReqFromSubRouting) {
        _senderDeletionReqFromSubRouting = senderDeletionReqFromSubRouting;
    }

    public boolean isSenderInterfaceInfoInDBFound() {
        return _senderInterfaceInfoInDBFound;
    }

    public void setSenderInterfaceInfoInDBFound(boolean senderInterfaceInfoInDBFound) {
        _senderInterfaceInfoInDBFound = senderInterfaceInfoInDBFound;
    }

    public boolean isInterfaceCatRoutingDone() {
        return _interfaceCatRoutingDone;
    }

    public void setInterfaceCatRoutingDone(boolean interfaceCatRoutingDone) {
        _interfaceCatRoutingDone = interfaceCatRoutingDone;
    }

    public boolean isDecreaseNetworkLoadCounters() {
        return _decreaseNetworkLoadCounters;
    }

    public void setDecreaseNetworkLoadCounters(boolean decreaseNetworkLoadCounters) {
        _decreaseNetworkLoadCounters = decreaseNetworkLoadCounters;
    }

    public String getGroupType() {
        return _groupType;
    }

    public void setGroupType(String groupType) {
        _groupType = groupType;
    }

    public boolean isDecreaseGroupTypeCounter() {
        return _decreaseGroupTypeCounter;
    }

    public void setDecreaseGroupTypeCounter(boolean decreaseGroupTypeCounter) {
        _decreaseGroupTypeCounter = decreaseGroupTypeCounter;
    }

    /**
     * @return Returns the pushMessage.
     */
    public boolean isPushMessage() {
        return _pushMessage;
    }

    /**
     * @param pushMessage
     *            The pushMessage to set.
     */
    public void setPushMessage(boolean pushMessage) {
        _pushMessage = pushMessage;
    }

    public String getRequestNetworkCode() {
        return _requestNetworkCode;
    }

    public void setRequestNetworkCode(String requestNetworkCode) {
        _requestNetworkCode = requestNetworkCode;
    }

    public Object getValueObject() {
        return _valueObject;
    }

    public void setValueObject(Object valueObject) {
        _valueObject = valueObject;
    }

    public String getExternalNetworkCode() {
        return _externalNetworkCode;
    }

    public void setExternalNetworkCode(String externalNetworkCode) {
        _externalNetworkCode = externalNetworkCode;
    }

    public String getSenderLoginID() {
        return _senderLoginID;
    }

    public void setSenderLoginID(String senderLoginID) {
        _senderLoginID = senderLoginID;
    }

    public String getSenderExternalCode() {
        return _senderExternalCode;
    }

    public void setSenderExternalCode(String senderExternalCode) {
        _senderExternalCode = senderExternalCode;
    }

    public HashMap getRequestMap() {
        return _requestMap;
    }

    public void setRequestMap(HashMap requestMap) {
        _requestMap = requestMap;
    }

    public boolean isPasswordValidationRequired() {
        return _passwordValidationRequired;
    }

    public void setPasswordValidationRequired(boolean passwordValidationRequired) {
        _passwordValidationRequired = passwordValidationRequired;
    }

    public boolean isPinValidationRequired() {
        return _pinValidationRequired;
    }

    public void setPinValidationRequired(boolean pinValidationRequired) {
        _pinValidationRequired = pinValidationRequired;
    }

    public String getNotificationMSISDN() {
        return _notificationMSISDN;
    }

    public void setNotificationMSISDN(String notificationMSISDN) {
        _notificationMSISDN = notificationMSISDN;
    }

    public String getParam1() {
        return _param1;
    }

    public void setParam1(String iccid) {
        _param1 = iccid;
    }

    public boolean isMessageAlreadyParsed() {
        return _messageAlreadyParsed;
    }

    public void setMessageAlreadyParsed(boolean messageAlreadyParsed) {
        _messageAlreadyParsed = messageAlreadyParsed;
    }

    public boolean isUnmarkSenderRequired() {
        return _unmarkSenderRequired;
    }

    public void setUnmarkSenderRequired(boolean senderRequired) {
        _unmarkSenderRequired = senderRequired;
    }

    /**
     * @return Returns the _externalReferenceNum.
     */
    public String getExternalReferenceNum() {
        return _externalReferenceNum;
    }

    /**
     * @param referenceNum
     *            The _externalReferenceNum to set.
     */
    public void setExternalReferenceNum(String referenceNum) {
        _externalReferenceNum = referenceNum;
    }

    /**
     * @return Returns the enquiryServiceType.
     */
    public String getEnquiryServiceType() {
        return _enquiryServiceType;
    }

    /**
     * @param enquiryServiceType
     *            The enquiryServiceType to set.
     */
    public void setEnquiryServiceType(String enquiryServiceType) {
        _enquiryServiceType = enquiryServiceType;
    }

    /**
     * @return Returns the receiverServiceClassId.
     */
    public String getReceiverServiceClassId() {
        return _receiverServiceClassId;
    }

    /**
     * @param receiverServiceClassId
     *            The receiverServiceClassId to set.
     */
    public void setReceiverServiceClassId(String receiverServiceClassId) {
        _receiverServiceClassId = receiverServiceClassId;
    }

    /**
     * @return Returns the slab.
     */
    public boolean isSlab() {
        return _slab;
    }

    /**
     * @param slab
     *            The slab to set.
     */
    public void setSlab(boolean slab) {
        _slab = slab;
    }

    /**
     * @return Returns the slabAmount.
     */
    public String getSlabAmount() {
        return _slabAmount;
    }

    /**
     * @param slabAmount
     *            The slabAmount to set.
     */
    public void setSlabAmount(String slabAmount) {
        _slabAmount = slabAmount;
    }

    public String getReceiverSubscriberType() {
        return _receiverSubscriberType;
    }

    /**
     * @param subscriberType
     *            The _receiverSubscriberType to set.
     */
    public void setReceiverSubscriberType(String subscriberType) {
        _receiverSubscriberType = subscriberType;
    }

    /**
     * @return Returns the intMsisdnNotFound.
     */
    public String getIntMsisdnNotFound() {
        return _intMsisdnNotFound;
    }

    /**
     * @param intMsisdnNotFound
     *            The intMsisdnNotFound to set.
     */
    public void setIntMsisdnNotFound(String intMsisdnNotFound) {
        _intMsisdnNotFound = intMsisdnNotFound;
    }

    /**
     * Returns topUPReceiverRequestSent
     * 
     * @return Returns the topUPReceiverRequestSent.
     */
    public long getTopUPReceiverRequestSent() {
        return this._topUPReceiverRequestSent;
    }

    /**
     * Sets topUPReceiverRequestSent
     * 
     * @param topUPReceiverRequestSent
     *            long
     */
    public void setTopUPReceiverRequestSent(long topUPReceiverRequestSent) {
        this._topUPReceiverRequestSent = topUPReceiverRequestSent;
    }

    /**
     * Returns topUPReceiverResponseReceived
     * 
     * @return Returns the topUPReceiverResponseReceived.
     */
    public long getTopUPReceiverResponseReceived() {
        return this._topUPReceiverResponseReceived;
    }

    /**
     * Sets topUPReceiverResponseReceived
     * 
     * @param topUPReceiverResponseReceived
     *            long
     */
    public void setTopUPReceiverResponseReceived(long topUPReceiverResponseReceived) {
        this._topUPReceiverResponseReceived = topUPReceiverResponseReceived;
    }

    /**
     * Returns topUPSenderRequestSent
     * 
     * @return Returns the topUPSenderRequestSent.
     */
    public long getTopUPSenderRequestSent() {
        return this._topUPSenderRequestSent;
    }

    /**
     * Sets topUPSenderRequestSent
     * 
     * @param topUPSenderRequestSent
     *            long
     */
    public void setTopUPSenderRequestSent(long topUPSenderRequestSent) {
        this._topUPSenderRequestSent = topUPSenderRequestSent;
    }

    /**
     * Returns topUPSenderResponseReceived
     * 
     * @return Returns the topUPSenderResponseReceived.
     */
    public long getTopUPSenderResponseReceived() {
        return this._topUPSenderResponseReceived;
    }

    /**
     * Sets topUPSenderResponseReceived
     * 
     * @param topUPSenderResponseReceived
     *            long
     */
    public void setTopUPSenderResponseReceived(long topUPSenderResponseReceived) {
        this._topUPSenderResponseReceived = topUPSenderResponseReceived;
    }

    /**
     * Returns validationReceiverRequestSent
     * 
     * @return Returns the validationReceiverRequestSent.
     */
    public long getValidationReceiverRequestSent() {
        return this._validationReceiverRequestSent;
    }

    /**
     * Sets validationReceiverRequestSent
     * 
     * @param validationReceiverRequestSent
     *            long
     */
    public void setValidationReceiverRequestSent(long validationReceiverRequestSent) {
        this._validationReceiverRequestSent = validationReceiverRequestSent;
    }

    /**
     * Returns validationReceiverResponseReceived
     * 
     * @return Returns the validationReceiverResponseReceived.
     */
    public long getValidationReceiverResponseReceived() {
        return this._validationReceiverResponseReceived;
    }

    /**
     * Sets validationReceiverResponseReceived
     * 
     * @param validationReceiverResponseReceived
     *            long
     */
    public void setValidationReceiverResponseReceived(long validationReceiverResponseReceived) {
        this._validationReceiverResponseReceived = validationReceiverResponseReceived;
    }

    /**
     * Returns validationSenderRequestSent
     * 
     * @return Returns the validationSenderRequestSent.
     */
    public long getValidationSenderRequestSent() {
        return this._validationSenderRequestSent;
    }

    /**
     * Sets validationSenderRequestSent
     * 
     * @param validationSenderRequestSent
     *            long
     */
    public void setValidationSenderRequestSent(long validationSenderRequestSent) {
        this._validationSenderRequestSent = validationSenderRequestSent;
    }

    /**
     * Returns validationSenderResponseReceived
     * 
     * @return Returns the validationSenderResponseReceived.
     */
    public long getValidationSenderResponseReceived() {
        return this._validationSenderResponseReceived;
    }

    /**
     * Sets validationSenderResponseReceived
     * 
     * @param validationSenderResponseReceived
     *            long
     */
    public void setValidationSenderResponseReceived(long validationSenderResponseReceived) {
        this._validationSenderResponseReceived = validationSenderResponseReceived;
    }

    public Locale getGifterLocale() {
        return _gifterLocale;
    }

    public void setGifterLocale(Locale gifterLocale) {
        _gifterLocale = gifterLocale;
    }

    /**
     * @return Returns the gifterMSISDN.
     */
    public String getGifterMSISDN() {
        return _gifterMSISDN;
    }

    /**
     * @param filteredMSISDN
     *            The filteredMSISDN to set.
     */
    public void setGifterMSISDN(String gifterMSISDN) {
        _gifterMSISDN = gifterMSISDN;
    }

    public void setGifterName(String gifterName) {
        _gifterName = gifterName;
    }

    public String getGifterName() {
        return _gifterName;
    }

    /**
     * @return Returns the receiverExtCode.
     */
    public String getReceiverExtCode() {
        return _receiverExtCode;
    }

    /**
     * @return Returns the receiverLoginID.
     */
    public String getReceiverLoginID() {
        return _receiverLoginID;
    }

    /**
     * @param receiverExtCode
     *            The receiverExtCode to set.
     */
    public void setReceiverExtCode(String receiverExtCode) {
        _receiverExtCode = receiverExtCode;
    }

    /**
     * @param receiverLoginID
     *            The receiverLoginID to set.
     */
    public void setReceiverLoginID(String receiverLoginID) {
        _receiverLoginID = receiverLoginID;
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

    public String getMobileLineQty() {
        return _mobileLineQty;
    }

    public void setMobileLineQty(String mobileLineQty) {
        _mobileLineQty = mobileLineQty;
    }

    public String getFixedLineQty() {
        return _fixedLineQty;
    }

    public void setFixedLineQty(String fixedLineQty) {
        _fixedLineQty = fixedLineQty;
    }

    public String getReqAmount() {
        return _ramount;
    }

    public void setReqAmount(String rAmount) {
        _ramount = rAmount;
    }

    /**
     * @return Returns the requestLoginId.
     */
    public String getRequestLoginId() {
        return _requestLoginId;
    }

    /**
     * @param requestLoginId
     *            The requestLoginId to set.
     */
    public void setRequestLoginId(String requestLoginId) {
        _requestLoginId = requestLoginId;
    }

    /**
     * @return Returns the activerUserId.
     */
    public String getActiverUserId() {
        return _activerUserId;
    }

    /**
     * @param activerUserId
     *            The activerUserId to set.
     */
    public void setActiverUserId(String activerUserId) {
        _activerUserId = activerUserId;
    }

    /**
     * @return Returns the messageSentMsisdn.
     */
    public String getMessageSentMsisdn() {
        return _messageSentMsisdn;
    }

    /**
     * @param messageSentMsisdn
     *            The messageSentMsisdn to set.
     */
    public void setMessageSentMsisdn(String messageSentMsisdn) {
        _messageSentMsisdn = messageSentMsisdn;
    }

    /**
     * @return Returns the parentMsisdnPOS.
     */
    public String getParentMsisdnPOS() {
        return _parentMsisdnPOS;
    }

    /**
     * @param parentMsisdnPOS
     *            The parentMsisdnPOS to set.
     */
    public void setParentMsisdnPOS(String parentMsisdnPOS) {
        _parentMsisdnPOS = parentMsisdnPOS;
    }

    /**
     * @return Returns the posMSISDN.
     */
    public String getPosMSISDN() {
        return _posMSISDN;
    }

    /**
     * @param posMSISDN
     *            The posMSISDN to set.
     */
    public void setPosMSISDN(String posMSISDN) {
        _posMSISDN = posMSISDN;
    }

    /**
     * @return Returns the posUserMSISDN.
     */
    public String getPosUserMSISDN() {
        return _posUserMSISDN;
    }

    /**
     * @param posUserMSISDN
     *            The posUserMSISDN to set.
     */
    public void setPosUserMSISDN(String posUserMSISDN) {
        _posUserMSISDN = posUserMSISDN;
    }

    /**
     * @return Returns the songCode
     */
    public String getSongCode() {
        return _songCode;
    }

    /**
     * @param songCode
     *            The songCode to set.
     */
    public void setSongCode(String songCode) {
        _songCode = songCode;
    }

    /**
     * @return Returns the _cellId.
     */
    public String getCellId() {
        return _cellId;
    }

    /**
     * @param id
     *            The _cellId to set.
     */
    public void setCellId(String id) {
        _cellId = id;
    }

    /**
     * @return Returns the _switchId.
     */
    public String getSwitchId() {
        return _switchId;
    }

    /**
     * @param id
     *            The _switchId to set.
     */
    public void setSwitchId(String id) {
        _switchId = id;
    }

    public String getServiceKeyword() {
        return _serviceKeyword;
    }

    public void setServiceKeyword(String serviceKeyword) {
        _serviceKeyword = serviceKeyword;
    }

    public String getUssdSessionID() {
        return _ussdSessionID;
    }

    public void setUssdSessionID(String ussdSessionID) {
        _ussdSessionID = ussdSessionID;
    }

    /**
     * @return Returns the txnType
     */
    public String getTxnType() {
        return _txntype;
    }

    /**
     * @param txntype
     *            The txnType to set.
     */
    public void setTxnType(String txntype) {
        _txntype = txntype;
    }

    /**
     * @return Returns the purpose
     */
    public String getPurpose() {
        return _purpose;
    }

    /**
     * @param txntype
     *            The purpose to set.
     */
    public void setPurpose(String purpose) {
        _purpose = purpose;
    }

    /**
     * @return the consumed
     */
    public String getConsumed() {
        return _consumed;
    }

    /**
     * @param consumed
     *            the consumed to set
     */
    public void setConsumed(String consumed) {
        _consumed = consumed;
    }

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return _serialNo;
    }

    /**
     * @param serialNo
     *            the serialNo to set
     */
    public void setSerialNo(String serialNo) {
        _serialNo = serialNo;
    }

    /**
     * @return the vomsError
     */
    public String getVomsError() {
        return _vomsError;
    }

    /**
     * @param vomsError
     *            the vomsError to set
     */
    public void setVomsError(String vomsError) {
        _vomsError = vomsError;
    }

    /**
     * @return the vomsMessage
     */
    public String getVomsMessage() {
        return _vomsMessage;
    }

    /**
     * @param vomsMessage
     *            the vomsMessage to set
     */
    public void setVomsMessage(String vomsMessage) {
        _vomsMessage = vomsMessage;
    }

    /**
     * @return the vomsRegion
     */
    public String getVomsRegion() {
        return _vomsRegion;
    }

    /**
     * @param vomsRegion
     *            the vomsRegion to set
     */
    public void setVomsRegion(String vomsRegion) {
        _vomsRegion = vomsRegion;
    }

    /**
     * @return the vomsValid
     */
    public String getVomsValid() {
        return _vomsValid;
    }

    /**
     * @param vomsValid
     *            the vomsValid to set
     */
    public void setVomsValid(String vomsValid) {
        _vomsValid = vomsValid;
    }

    /**
     * @return the voucherAmount
     */
    public long getVoucherAmount() {
        return _voucherAmount;
    }

    /**
     * @param voucherAmount
     *            the voucherAmount to set
     */
    public void setVoucherAmount(long voucherAmount) {
        _voucherAmount = voucherAmount;
    }

    /**
     * @return the _postValidationTimeTaken
     */

    public long getPostValidationTimeTaken() {
        return _postValidationTimeTaken;
    }

    /**
     * @param _postValidationTimeTaken
     *            the _postValidationTimeTaken to set
     */
    public void setPostValidationTimeTaken(long validationTimeTaken) {
        _postValidationTimeTaken = validationTimeTaken;
    }

    /**
     * @return the userCategory
     */
    public String getUserCategory() {
        return _userCategory;
    }

    /**
     * @param userCategory
     *            the userCategory to set
     */
    public void setUserCategory(String userCategory) {
        _userCategory = userCategory;
    }

    public String getMcdListName() {
        return _mcdListName;
    }

    public void setMcdListName(String mcdListName) {
        _mcdListName = mcdListName;
    }

    public int getMcdListAddCount() {
        return _mcdListAddCount;
    }

    public void setMcdListAddCount(int mcdListAddCount) {
        _mcdListAddCount = mcdListAddCount;
    }

    public String getMcdPIn() {
        return _mcdPIn;
    }

    public void setMcdPIn(String mcdPIn) {
        _mcdPIn = mcdPIn;
    }

    public String getTxnAuthStatus() {
        return _txnAuthStatus;
    }

    public void setTxnAuthStatus(String authStatus) {
        _txnAuthStatus = authStatus;
    }

    /**
     * Returns topUPReceiverRequestSent
     * 
     * @return Returns the topUPReceiverRequestSent.
     */
    public long getCreditTime() {
        return this._creditTime;
    }

    /**
     * Sets topUPReceiverRequestSent
     * 
     * @param topUPReceiverRequestSent
     *            long
     */
    public void setCreditTime(long p_creditTime) {
        this._creditTime = p_creditTime;
    }

    /**
     * Returns topUPReceiverRequestSent
     * 
     * @return Returns the topUPReceiverRequestSent.
     */
    public long getPromoTime() {
        return this._promoTime;
    }

    /**
     * Sets topUPReceiverRequestSent
     * 
     * @param topUPReceiverRequestSent
     *            long
     */
    public void setPromoTime(long p_promoTime) {
        this._promoTime = p_promoTime;
    }

    /**
     * Returns topUPReceiverRequestSent
     * 
     * @return Returns the topUPReceiverRequestSent.
     */
    public long getCosTime() {
        return this._cosTime;
    }

    /**
     * Sets topUPReceiverRequestSent
     * 
     * @param topUPReceiverRequestSent
     *            long
     */
    public void setCosTime(long p_cosTime) {
        this._cosTime = p_cosTime;
    }

    /**
     * Returns topUPReceiverRequestSent
     * 
     * @return Returns the topUPReceiverRequestSent.
     */
    public long getCreditValTime() {
        return this._creditValTime;
    }

    /**
     * Sets topUPReceiverRequestSent
     * 
     * @param topUPReceiverRequestSent
     *            long
     */
    public void setCreditValTime(long p_creditValTime) {
        this._creditValTime = p_creditValTime;
    }

    /**
     * Returns topUPReceiverRequestSent
     * 
     * @return Returns the topUPReceiverRequestSent.
     */
    public long getPromoValTime() {
        return this._promoValTime;
    }

    /**
     * Sets topUPReceiverRequestSent
     * 
     * @param topUPReceiverRequestSent
     *            long
     */
    public void setPromoValTime(long p_promoValTime) {
        this._promoValTime = p_promoValTime;
    }

    /**
     * Returns topUPReceiverRequestSent
     * 
     * @return Returns the topUPReceiverRequestSent.
     */
    public long getCosValTime() {
        return this._cosValTime;
    }

    /**
     * Sets topUPReceiverRequestSent
     * 
     * @param topUPReceiverRequestSent
     *            long
     */
    public void setCosValTime(long p_cosValTime) {
        this._cosValTime = p_cosValTime;
    }

    // added by shashank for channel user authentication
    public String getValidatePassword() {
        return _validatePassword;
    }

    public void setValidatePassword(String password) {
        _validatePassword = password;
    }

    public String getValidatePin() {
        return _validatePin;
    }

    public void setValidatePin(String pin) {
        _validatePin = pin;
    }

    public String getValidMSISDN() {
        return _validMSISDN;
    }

    public void setValidMSISDN(String _validmsisdn) {
        _validMSISDN = _validmsisdn;
    }

    /**
     * @return the externalTransactionNum
     * @author sonali.garg
     *         Unique id of transaction in External Transaction System
     */
    public String getExternalTransactionNum() {
        return _externalTransactionNum;
    }

    /**
     * @param externalTransactionNum
     * @author sonali.garg
     */
    public void setExternalTransactionNum(String externalTransactionNum) {
        _externalTransactionNum = externalTransactionNum;
    }

    /**
     * @return the remarks
     * @author sonali.garg
     */
    public String getRemarks() {
        return _remarks;
    }

    /**
     * @param remarks
     * @author sonali.garg
     */
    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    public boolean isToBeProcessedFromQueue() {
        return _toBeProcessedFromQueue;
    }

    public void setToBeProcessedFromQueue(boolean toBeProcessedFromQueue) {
        _toBeProcessedFromQueue = toBeProcessedFromQueue;
    }

    public String getMcdSenderProfile() {
        return _mcdSenderProfile;
    }

    public void setMcdSenderProfile(String mcdSenderProfile) {
        _mcdSenderProfile = mcdSenderProfile;
    }

    public String getMcdScheduleType() {
        return _mcdScheduleType;
    }

    public void setMcdScheduleType(String mcdScheduleType) {
        _mcdScheduleType = mcdScheduleType;
    }

    public String getMcdListStatus() {
        return _mcdListStatus;
    }

    public void setMcdListStatus(String mcdListStatus) {
        _mcdListStatus = mcdListStatus;
    }

    public String getMcdFailRecords() {
        return _mcdFailRecords;
    }

    public void setMcdFailRecords(String mcdFailRecords) {
        _mcdFailRecords = mcdFailRecords;
    }

    public String getMcdNoOfSchedules() {
        return _mcdNoOfSchedules;
    }

    public void setMcdNoOfSchedules(String mcdNoOfSchedules) {
        _mcdNoOfSchedules = mcdNoOfSchedules;
    }

    public String getMcdNextScheduleDate() {
        return _mcdNextScheduleDate;
    }

    public void setMcdNextScheduleDate(String mcdNextScheduleDate) {
        _mcdNextScheduleDate = mcdNextScheduleDate;
    }

    public Date getExecutedUpto() {
        return _executedUpto;
    }

    public void setExecutedUpto(Date executedUpto) {
        _executedUpto = executedUpto;
    }

    // Diwakar
    /**
     * @return Returns the password.
     */
    public String getNewPassword() {
        return _newPassword;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setNewPassword(String newPassword) {
        _newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return _confirmNewPassword;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setConfirmNewPassword(String confirmNewPassword) {
        _confirmNewPassword = confirmNewPassword;
    }

    /**
     * @return Returns the password.
     */
    public String getReqDate() {
        return _reqDate;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setReqDate(String reqDate) {
        _reqDate = reqDate;
    }

    /**
     * @param password
     *            The password to set.
     */
    public String getEmployeeCode() {
        return _employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        _employeeCode = employeeCode;
    }

    public String getSsn() {
        return _ssn;
    }

    public void setSsn(String ssn) {
        _ssn = ssn;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return the transaction Date
     * @author akanksha.gupta
     */
    public String getTxnDate() {
        return _txnDate;
    }

    /**
     * @param transaction
     *            Date
     * @author akanksha.gupta
     */
    public void setTxnDate(String txnDate) {
        _txnDate = txnDate;
    }

    /**
     * @return the External Transaction Date
     * @author akanksha.gupta
     */
    public String getExternalTransactionDate() {
        return _extTxnDate;
    }

    /**
     * @param External
     *            transaction Date
     * @author akanksha.gupta
     */
    public void setExternalTransactionDate(String extTxnDate) {
        _extTxnDate = extTxnDate;
    }

    /**
     * @return the enquiryItemList
     * @author akanksha.gupta
     */
    public ArrayList getEnquiryItemList() {
        return _enquiryItemList;
    }

    /**
     * @param itemList
     * @author akanksha.gupta
     */
    public void setEnquiryItemList(ArrayList itemList) {
        _enquiryItemList = itemList;
    }

    /**
     * @return the invoiceSize
     * @author akanksha.gupta
     */
    public int getInvoiceSize() {
        return _invoiceSize;
    }

    /**
     * @param size
     * @author akanksha.gupta
     */
    public void setInvoiceSize(int size) {
        _invoiceSize = size;
    }

    /**
     * @return the xMontoDeudaTotal
     * @author akanksha.gupta
     */
    public String getXMontoDeudaTotal() {
        return _xMontoDeudaTotal;
    }

    /**
     * @param montoDeudaTotal
     * @author akanksha.gupta
     */
    public void setXMontoDeudaTotal(String montoDeudaTotal) {
        _xMontoDeudaTotal = montoDeudaTotal;
    }

    /**
     * @return the xOpcionRecaudacion
     * @author akanksha.gupta
     */
    public String getXOpcionRecaudacion() {
        return _xOpcionRecaudacion;
    }

    /**
     * @param opcionRecaudacion
     * @author akanksha.gupta
     */
    public void setXOpcionRecaudacion(String opcionRecaudacion) {
        _xOpcionRecaudacion = opcionRecaudacion;
    }

    /**
     * @return the xCodTipoServicio
     * @author akanksha.gupta
     */
    public String getxCodTipoServicio() {
        return _xCodTipoServicio;
    }

    /**
     * @param codTipoServicio
     * @author akanksha.gupta
     */
    public void setXCodTipoServicio(String codTipoServicio) {
        _xCodTipoServicio = codTipoServicio;
    }

    /**
     * @return the invoiceno
     * @author akanksha.gupta
     */
    public String getInvoiceno() {
        return _invoiceno;
    }

    /**
     * @param invoiceno
     * @author akanksha.gupta
     */
    public void setInvoiceno(String invoiceno) {
        _invoiceno = invoiceno;
    }

    // VFE 6 CR
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
     * @return _requestType
     * @author sonali.garg
     *         here requestType is used to differentiate whether it's for
     *         prepaid or postpaid
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param _requestType
     * @author sonali.garg
     * 
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    public String getRecmsg() {

        return _recmsg;
    }

    public void setRecmsg(String recmsg) {
        _recmsg = recmsg;
    }

    /**
     * @return the dueAmt
     */
    public String getDueAmt() {
        return _dueAmt;
    }

    /**
     * @param dueAmt
     *            the dueAmt to set
     */
    public void setDueAmt(String dueAmt) {
        _dueAmt = dueAmt;
    }

    /**
     * @return the dueDate
     */
    public String getDueDate() {
        return _dueDate;
    }

    /**
     * @param dueDate
     *            the dueDate to set
     */
    public void setDueDate(String dueDate) {
        _dueDate = dueDate;
    }

    /**
     * @return the minAmtDue
     */
    public String getMinAmtDue() {
        return _minAmtDue;
    }

    /**
     * @param minAmtDue
     *            the minAmtDue to set
     */
    public void setMinAmtDue(String minAmtDue) {
        _minAmtDue = minAmtDue;
    }

    /**
     * @param _imei
     * @author Vikas Singh
     * 
     */
    public String getImei() {
        return _imei;
    }

    public void setImei(String _imei) {
        this._imei = _imei;
    }

    public String getNickName() {
        return _nickName;
    }

    public void setNickName(String _nickName) {
        this._nickName = _nickName;
    }

    public String getNewNickName() {
        return _newNickName;
    }

    public void setNewNickName(String _newNickName) {
        this._newNickName = _newNickName;
    }

    public void setPin(String _pin) {
        this._pin = _pin;
    }

    public String getPin() {
        return this._pin;
    }

    public void setCvv(String _cvv) {
        this._cvv = _cvv;
    }

    public String getEmailId() {
        return _emailId;
    }

    public void setEmailId(String id) {
        _emailId = id;
    }

    public int getNumberOfRegisteredCards() {
        return _numberOfRegisteredCards;
    }

    public void setNumberOfRegisteredCards(int ofRegisteredCards) {
        _numberOfRegisteredCards = ofRegisteredCards;
    }

    public String getUserLoginId() {
        return _userLoginId;
    }

    public void setUserLoginId(String loginId) {
        _userLoginId = loginId;
    }

    public String getEncryptionKey() {
        return _encryptionKey;
    }

    public void setEncryptionKey(String key) {
        _encryptionKey = key;
    }
}