package com.btsl.pretups.transfer.businesslogic;

/**
 * @(#)MessageFormater.java
 *                          Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                          All Rights Reserved
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Abhijit Chauhan June 22,2005 Initial Creation
 *                          ----------------------------------------------------
 *                          --------------------------------------------
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class MessageFormater {

    private String serviceKeyword;
    private String paymentMethodKeyword;
    private String receiverMSISDN_NAME;
    private String receiverMSISDN;
    private String receiverName;
    private String amountStr;
    private String pin;
    private String paymentMethodType;
    private SubscriberDAO _subscriberDAO = null;
    private String senderSubscriberType;
    private static Log _log = LogFactory.getLog(MessageFormater.class.getName());
    private String[] _requestMessageArray;
    private String _incomingSmsStr = "";
    private TransferVO _transferVO;
    private RequestVO _requestVO;
    private static String chnl_message_sep = null;
    private static OperatorUtilI _operatorUtil = null;

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageFormater[initialize]", "", "", "",
                            "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public MessageFormater() {
        _subscriberDAO = new SubscriberDAO();
    }

    /**
     * Check If Buddy
     * 
     * @param p_con
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    private boolean checkIfBuddy(Connection p_con) throws BTSLBaseException, Exception {
        boolean cBuddy = false;
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" requestMessageArray length:");
        	loggerValue.append(_requestMessageArray.length);
            _log.debug("checkIfBuddy", loggerValue);
        }

        if (_requestMessageArray.length < 2 || _requestMessageArray.length > 6) {
            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { _requestVO.getActualMessageFormat() }, null);
        }

        // if receiver buddy
        // Validate 2nd Argument for Payment Method Keyword.
        paymentMethodKeyword = _requestMessageArray[1];

        // if paymentMethod invalid , Validate 2nd Argument for Receiver
        // No(MSISDN).
        final PaymentMethodKeywordVO paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, _transferVO.getServiceType(), _transferVO.getNetworkCode());
        if (paymentMethodKeywordVO == null) {
            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }
            _transferVO.setPaymentMethodType(paymentMethodType);
            _transferVO.setDefaultPaymentMethod("Y");
            _incomingSmsStr += paymentMethodType + " ";
            cBuddy = checkAfterPaymentMethodForBuddy(p_con, 1);
        } else {
            paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
            _incomingSmsStr += paymentMethodType + " ";
            // if paymentMethod valid , Validate 3rd Argument for Receiver
            // No(MSISDN).
            if (_requestMessageArray.length < 3) {
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { _requestVO.getActualMessageFormat() }, null);
            }

            cBuddy = checkAfterPaymentMethodForBuddy(p_con, 2);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("checkIfBuddy", " return value:" + cBuddy);
        }
        return cBuddy;
    }

    /**
     * Validate If Not Buddy
     * 
     * @param p_con
     * @param p_senderVO
     *            SenderVO
     * @throws BTSLBaseException
     * @throws Exception
     */

    /*
     * private void validateIfNotBuddy(Connection p_con,SenderVO p_senderVO)
     * throws BTSLBaseException,Exception
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("validateIfNotBuddy"," requestMessageArray length:"+
     * _requestMessageArray);
     * if(_requestMessageArray.length<3 || _requestMessageArray.length>5)
     * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
     * P2P_INVALID_MESSAGEFORMAT);
     * 
     * if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_REQUIRED_CODE))).booleanValue())
     * {
     * //Validate 2nd Argument for PIN.
     * pin=_requestMessageArray[1];
     * 
     * _incomingSmsStr+="****"+" ";
     * 
     * //if pin Invalid return with error(PIN is Mandatory)
     * String actualPin=((SenderVO)_transferVO.getSenderVO()).getPin();
     * if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
     * {
     * 
     * }
     * else
     * {
     * SubscriberBL.validatePIN(p_con,p_senderVO,pin);
     * }
     * //if PIN valid
     * //Validate next Argument for Payment Method.
     * PaymentMethodKeywordVO paymentMethodKeywordVO=null;
     * paymentMethodKeyword=_requestMessageArray[2];
     * //if paymentMethod invalid , Validate next Argument for Receiver
     * No(MSISDN).
     * paymentMethodKeywordVO=PaymentMethodCache.getObject(paymentMethodKeyword,
     * _transferVO.getRequestGatewayType());
     * if(paymentMethodKeywordVO==null)
     * {
     * paymentMethodType=ServicePaymentMappingCache.getDefaultPaymentMethod(
     * _transferVO.getServiceType(),senderSubscriberType);
     * if(paymentMethodType==null)
     * {
     * //return with error message, no default payment method defined
     * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
     * ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
     * }
     * _transferVO.setPaymentMethodType(paymentMethodType);
     * _incomingSmsStr+=paymentMethodType+" ";
     * if(_requestMessageArray.length<4)
     * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
     * P2P_INVALID_MESSAGEFORMAT);
     * checkAfterPaymentMethod(p_con,2);
     * }
     * else
     * {
     * paymentMethodType=paymentMethodKeywordVO.getPaymentMethod();
     * _incomingSmsStr+=paymentMethodType+" ";
     * 
     * _transferVO.setPaymentMethodType(paymentMethodType);
     * _transferVO.setDefaultPaymentMethod("Y");
     * //if paymentMethod valid , Validate next Argument for Receiver
     * No(MSISDN).
     * if(_requestMessageArray.length<5)
     * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
     * P2P_INVALID_MESSAGEFORMAT);
     * checkAfterPaymentMethod(p_con,3);
     * }
     * }
     * else
     * {
     * //Validate next Argument for Payment Method.
     * paymentMethodKeyword=_requestMessageArray[1];
     * //if paymentMethod invalid , Validate next Argument for Receiver
     * No(MSISDN).
     * PaymentMethodKeywordVO
     * paymentMethodKeywordVO=PaymentMethodCache.getObject
     * (paymentMethodKeyword,_transferVO.getRequestGatewayType());
     * if(paymentMethodKeywordVO==null)
     * {
     * paymentMethodType=ServicePaymentMappingCache.getDefaultPaymentMethod(
     * _transferVO.getServiceType(),senderSubscriberType);
     * if(paymentMethodType==null)
     * {
     * //return with error message, no default payment method defined
     * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
     * ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
     * }
     * _transferVO.setPaymentMethodType(paymentMethodType);
     * _incomingSmsStr+=paymentMethodType+" ";
     * if(_requestMessageArray.length<3)
     * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
     * P2P_INVALID_MESSAGEFORMAT);
     * checkAfterPaymentMethod(p_con,1);
     * }
     * else
     * {
     * paymentMethodType=paymentMethodKeywordVO.getPaymentMethod();
     * _incomingSmsStr+=paymentMethodType+" ";
     * //if paymentMethod valid , Validate 3rd Argument for Receiver No(MSISDN).
     * _transferVO.setPaymentMethodType(paymentMethodType);
     * if(_requestMessageArray.length<4)
     * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
     * P2P_INVALID_MESSAGEFORMAT);
     * checkAfterPaymentMethod(p_con,2);
     * }
     * }
     * }
     */
    /**
     * This method will be used to handle transfer message format
     * 
     * @param p_con
     * @param requestMessageArray
     * @param p_transferVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void handleTransferMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException {
        _requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled()) {
            _log.debug("handleTransferMessageFormat", "Entered requestMessageArray length:" + _requestMessageArray.length);
        }
        if (_requestMessageArray.length < 2) {
            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_INVALID_KEYWORDMESSAGEFORMAT);
        }
        _requestVO = p_requestVO;
        _transferVO = p_transferVO;
        serviceKeyword = _requestMessageArray[0];
        senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
        _incomingSmsStr += serviceKeyword + " ";
        try {
            if (!checkIfBuddy(p_con)) {
                validateIfNotBuddy(p_con, (SenderVO) p_transferVO.getSenderVO());
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	throw new BTSLBaseException("MessageFormater", "handleTransferMessageFormat","");
        } finally {
            _transferVO.setIncomingSmsStr(_incomingSmsStr);
            if (_log.isDebugEnabled()) {
                _log.debug("handleTransferMessageFormat", "Exiting :::" + BTSLUtil.maskParam(_incomingSmsStr));
            }
        }
    }

    /**
     * Check After Payment Method For Buddy
     * 
     * @param p_con
     * @param i
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    private boolean checkAfterPaymentMethodForBuddy(Connection p_con, int i) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("checkAfterPaymentMethodForBuddy", " i=" + i + " requestMessageArray length:" + _requestMessageArray.length);
        }
        receiverMSISDN_NAME = _requestMessageArray[i];
        final P2PBuddiesDAO p2PBuddiesDAO = new P2PBuddiesDAO();
        final BuddyVO buddyVO = p2PBuddiesDAO.loadBuddyDetails(p_con, ((SenderVO) _transferVO.getSenderVO()).getUserID(), receiverMSISDN_NAME);
        if (buddyVO == null) {
            return false;
        }
        receiverMSISDN = buddyVO.getMsisdn();
        receiverName = buddyVO.getName();

        _incomingSmsStr += receiverMSISDN_NAME + " ";

        _transferVO.setPaymentMethodType(paymentMethodType);
        long amount = 0;
        if (_requestMessageArray.length == i + 1) {
            // get preferred amount for buddy
            amount = buddyVO.getPreferredAmount();// If amount is not specified
        } else {
            amountStr = _requestMessageArray[i + 1];
            amount = PretupsBL.getSystemAmount(amountStr);
        }
        if (amount < 0) {
            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
        }
        _transferVO.setTransferValue(amount);
        _transferVO.setRequestedAmount(amount);
        _incomingSmsStr += amount + " ";

        final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null) {
            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        }
        buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
        buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
        _transferVO.setReceiverVO(buddyVO);

        if (i == 1) {
            if (_requestMessageArray.length > 3) {
                _requestVO.setReqSelector(_requestMessageArray[3]);
                /*
                 * else
                 * _requestVO.setReqSelector(""+SystemPreferences.
                 * P2P_TRANSFER_DEF_SELECTOR_CODE);
                 */
            }
        }

        if (i == 2) {
            if (_requestMessageArray.length > 4) {
                _requestVO.setReqSelector(_requestMessageArray[4]);
                /*
                 * else
                 * _requestVO.setReqSelector(""+SystemPreferences.
                 * P2P_TRANSFER_DEF_SELECTOR_CODE);
                 */
            }
        }
        _incomingSmsStr += _requestVO.getReqSelector() + " ";
        return true;
    }

    /**
     * Check After Payment Method
     * 
     * @param p_con
     * @param i
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void checkAfterPaymentMethod(Connection p_con, int i) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" i=");
        	loggerValue.append(i);
        	loggerValue.append(" requestMessageArray length:");
        	loggerValue.append(_requestMessageArray.length);
            _log.debug("checkAfterPaymentMethod", loggerValue);
        }
        receiverMSISDN = _requestMessageArray[i];
        if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_INVALID_MSISDN, 0, new String[] { receiverMSISDN }, null);
        }
        final P2PBuddiesDAO p2PBuddiesDAO = new P2PBuddiesDAO();
        // This block will check if the user is sending the PIN but is also a
        // buddy then that request should go through
        final BuddyVO buddyVO = p2PBuddiesDAO.loadBuddyDetails(p_con, ((SenderVO) _transferVO.getSenderVO()).getUserID(), receiverMSISDN);
        if (buddyVO != null) {
            receiverMSISDN = buddyVO.getMsisdn();
            receiverName = buddyVO.getName();

            _incomingSmsStr += receiverMSISDN + " ";

            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
            }
            buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
            buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
            _transferVO.setReceiverVO(buddyVO);
            long amount = 0;
            /*
             * if(_requestMessageArray.length==i+1)
             * {
             * //get preferred amount for buddy
             * amount=buddyVO.getPreferredAmount();//If amount is not specified
             * }
             * else
             * {
             */
            amountStr = _requestMessageArray[i + 1];
            amount = PretupsBL.getSystemAmount(amountStr);
            // }
            if (amount < 0) {
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
            }
            _transferVO.setPaymentMethodType(paymentMethodType);
            _transferVO.setTransferValue(amount);
            _transferVO.setRequestedAmount(amount);
            _incomingSmsStr += amount + " ";

        } else {
            _incomingSmsStr += receiverMSISDN + " ";

            final ReceiverVO _receiverVO = new ReceiverVO();
            _receiverVO.setMsisdn(receiverMSISDN);
            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
            }
            _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
            _transferVO.setReceiverVO(_receiverVO);
            _transferVO.setPaymentMethodType(paymentMethodType);
            long amount = 0;
            /*
             * if(_requestMessageArray.length==i+1)
             * {
             * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
             * ERROR_INVALID_REQUESTFORMAT);
             * }
             * else
             * {
             */
            amountStr = _requestMessageArray[i + 1];
            amount = PretupsBL.getSystemAmount(amountStr);
            // }
            if (amount < 0) {
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
            }
            _transferVO.setTransferValue(amount);
            _transferVO.setRequestedAmount(amount);
            _incomingSmsStr += amount + " ";

        }
    }

    /**
     * This method will be used to handle transfer message format
     * 
     * @param p_con
     * @param requestMessageArray
     * @param p_transferVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void handleAcceptMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException {
        final String METHOD_NAME = "handleAcceptMessageFormat";
        _requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled()) {
            _log.debug("handleAcceptMessageFormat", "Entered requestMessageArray length:" + _requestMessageArray.length);
        }
        _requestVO = p_requestVO;
        try {
            _incomingSmsStr += _requestMessageArray[0] + " ";
            if (_requestMessageArray.length != 2) {
                throw new BTSLBaseException("", "handleAcceptMessageFormat", PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
            }
            try {
                final long skey = Long.parseLong(_requestMessageArray[1]);
                _incomingSmsStr += skey + " ";
                p_transferVO.setSkey(skey);
                PretupsBL.validateSKey(p_con, p_transferVO);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("", "handleAcceptMessageFormat", PretupsErrorCodesI.ERROR_INVALID_SKEY);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	throw new BTSLBaseException("MessageFormater", "handleAcceptMessageFormat","");
        } finally {
            p_transferVO.setIncomingSmsStr(_incomingSmsStr);
            if (_log.isDebugEnabled()) {
                _log.debug("handleTransferMessageFormat", "Exiting");
            }
        }
    }

    /**
     * Method to handle the message coming from channel user request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void handleChannelMessageFormat(RequestVO p_requestVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "handleChannelMessageFormat";
        final String requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug("handleChannelMessageFormat", requestID, "Entered MSISDN: " + p_requestVO.getFilteredMSISDN() + " With Message=" + BTSLUtil.maskParam(p_requestVO.getDecryptedMessage()));
        }
        String userMessage = p_requestVO.getDecryptedMessage();

        boolean isPasswordRequired = false;
        String tokenVal = null;

        int indexOfFixedInfo = 0;
        boolean fixedInfoFound = false;
        final ArrayList aList = new ArrayList();
        String[] newMessageArray = null;
        String mesg = "";
        try {
            chnl_message_sep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(chnl_message_sep)) {
                chnl_message_sep = " ";
            }

            userMessage = p_requestVO.getDecryptedMessage();

            // ChannelUserVO
            // channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO();

            isPasswordRequired = !BTSLUtil.isNullObject(channelUserVO.getUserPhoneVO()) && channelUserVO.getUserPhoneVO().isPinRequiredBool();

            if (_log.isDebugEnabled()) {
                _log.debug("handleChannelMessageFormat", requestID, " Originally password required =" + isPasswordRequired);
            }

            if (!BTSLUtil.isNullString(userMessage)) {
                if (!p_requestVO.isPlainMessage()) {
                    final String[] messageArray = userMessage.split(" ");
                    // String[] messageArray=(String
                    // [])BTSLUtil.split(userMessage," ");
                    final int messageArrayLength = messageArray.length;
                    for (int i = 0; i < messageArrayLength; i++) {
                        tokenVal = messageArray[i];

                        if (BTSLUtil.isNullString(tokenVal)) {
                            continue;
                        }

                        // checking for the REG and ADM
                        if ((i == 0) && (PretupsI.KEYWORD_TYPE_REGISTRATION.equalsIgnoreCase(tokenVal) || PretupsI.KEYWORD_TYPE_ADMIN.equalsIgnoreCase(tokenVal))) {
                            isPasswordRequired = false;
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug("handleChannelMessageFormat", requestID, " Position=" + i + " password required =" + isPasswordRequired);
                        }
                        if (i == 0) {
                            aList.add(tokenVal);
                            // newMessageArray[newArrCounter++]=tokenVal;
                            if (_log.isDebugEnabled()) {
                                _log.debug("handleChannelMessageFormat", requestID, " Position=" + i + " Token Value=" + tokenVal);
                            }
                            continue;
                        }// end if

                        if (isPasswordRequired) {
                            // at the second place PIN should come, so fixed
                            // info will be at 3rd place
                            indexOfFixedInfo = tokenVal.indexOf("F");
                            if (_log.isDebugEnabled()) {
                                _log.debug("handleChannelMessageFormat", requestID,
                                                " Position=" + i + " Password set to required Token Value=" + tokenVal + " indexOfFixedInfo:" + indexOfFixedInfo);
                            }
                            if (indexOfFixedInfo != -1 && i == 1) {
                                fixedInfoFound = true;
                                channelUserVO.setUsingNewSTK(true);
                                channelUserVO.setUpdateSimRequired(true);
                                // p_smsUserVO.setStatus("746"); //Is this the
                                // case of New Registartion or what??
                                channelUserVO.setFxedInfoStr(tokenVal.substring(indexOfFixedInfo + 1));
                                if (_log.isDebugEnabled()) {
                                    _log.debug("handleChannelMessageFormat", requestID,
                                                    "Position=" + i + " Token Value=" + tokenVal + " Fixed Info at place 2 Pin is required but not sending: " + channelUserVO
                                                                    .getFxedInfoStr());
                                }
                            } else if (indexOfFixedInfo != -1 && i == 2) {
                                fixedInfoFound = true;
                                channelUserVO.setUsingNewSTK(true);
                                channelUserVO.setFxedInfoStr(tokenVal.substring(indexOfFixedInfo + 1));
                                if (_log.isDebugEnabled()) {
                                    _log.debug("handleChannelMessageFormat",
                                                    requestID,
                                                    " Position=" + i + " Token Value=" + tokenVal + " Fixed Info at place 3 its ok Pin is required and also sending: " + channelUserVO
                                                                    .getFxedInfoStr());
                                }
                            } else {
                                aList.add(tokenVal);
                                // newMessageArray[newArrCounter++]=tokenVal;
                                if (_log.isDebugEnabled()) {
                                    _log.debug("handleChannelMessageFormat", requestID, " Position=" + i + " Token Value=" + tokenVal + " Message Length=" + aList.size());
                                }
                            }
                        } else {
                            // at the second place fix parameter should come
                            indexOfFixedInfo = tokenVal.indexOf("F");
                            if (_log.isDebugEnabled()) {
                                _log.debug("handleChannelMessageFormat", requestID,
                                                " Position=" + i + " Password Not required Token Value=" + tokenVal + " indexOfFixedInfo:" + indexOfFixedInfo);
                            }
                            if (indexOfFixedInfo != -1 && i == 2) {
                                channelUserVO.setUsingNewSTK(true);
                                channelUserVO.setUpdateSimRequired(true);
                                fixedInfoFound = true;
                                // p_smsUserVO.setStatus("746"); //Is this the
                                // case of New Registartion or what??
                                aList.add(tokenVal);
                                // newMessageArray[newArrCounter++]=tokenVal;
                                channelUserVO.setFxedInfoStr(tokenVal.substring(indexOfFixedInfo + 1));
                                if (_log.isDebugEnabled()) {
                                    _log.debug("handleChannelMessageFormat",
                                                    requestID,
                                                    " Position=" + i + " Token Value=" + tokenVal + " Fixed Info at place 3 its not ok Pin not required but Sending: " + channelUserVO
                                                                    .getFxedInfoStr());
                                }
                            } else if (indexOfFixedInfo != -1 && i == 1) {
                                fixedInfoFound = true;
                                // newMessageArray[newArrCounter++]="passwd";
                                // aList.add(PretupsI.DEFAULT_C2S_PIN);
                                aList.add(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)));
                                channelUserVO.setUsingNewSTK(true);
                                channelUserVO.setFxedInfoStr(tokenVal.substring(indexOfFixedInfo + 1));
                                if (_log.isDebugEnabled()) {
                                    _log.debug("handleChannelMessageFormat",
                                                    requestID,
                                                    " Position=" + i + " Token Value=" + tokenVal + " Fixed Info at place 2 its ok Pin not required and not Sending also: " + channelUserVO
                                                                    .getFxedInfoStr());
                                }
                            } else if (i == 1 && !fixedInfoFound) {
                                // newMessageArray[newArrCounter++]="passwd";
                                // aList.add(PretupsI.DEFAULT_C2S_PIN);
                                aList.add(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)));
                                aList.add(tokenVal);
                                // newMessageArray[newArrCounter++]=tokenVal;
                                if (_log.isDebugEnabled()) {
                                    _log.debug("handleChannelMessageFormat", requestID, " Position=" + i + " Token Value=" + tokenVal + " Pin at the second place its not ok ");
                                }
                            } else {
                                aList.add(tokenVal);
                                // newMessageArray[newArrCounter++]=tokenVal;
                                if (_log.isDebugEnabled()) {
                                    _log.debug("handleChannelMessageFormat", requestID, " Position=" + i + " Token Value=" + tokenVal + " Message Length=" + aList.size());
                                }
                            }
                        }
                    }
                    newMessageArray = new String[aList.size()];
                    String tempVar = null;
                    String tempTxnID = null;
                    boolean isTempTxnFound = false;
                    int j = 0;
                    final int alistSize = aList.size();
                    final String keyword = (String) aList.get(0);
                    for (int i = 0; i < alistSize; i++) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("handleChannelMessageFormat", requestID, " Position=" + i + " List Size=" + aList.size() + " Value=" + (String) aList.get(i));
                        }
                        if (i == aList.size() - 1) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("handleChannelMessageFormat", requestID, " Inside Temp Trans Check");
                            }
                            final int indx = BTSLUtil.NullToString((String) aList.get(i)).indexOf(PretupsI.TEMP_TRANS_ID_START_WITH);
                            if (indx > -1 && ((String) aList.get(i)).length() == PretupsI.TEMP_TRANS_ID_LENGTH) {
                                tempTxnID = (String) aList.get(i);
                                isTempTxnFound = true;
                                continue;
                            }
                        } else if (i == 1 && aList.size() > 2 && !PretupsI.KEYWORD_TYPE_REGISTRATION.equalsIgnoreCase(keyword) && !PretupsI.KEYWORD_TYPE_ADMIN
                                        .equalsIgnoreCase(keyword)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("handleChannelMessageFormat", requestID, " Inside PIN Check");
                            }
                            tempVar = (String) aList.get(i);
                            continue;
                        }
                        newMessageArray[j] = (String) aList.get(i);
                        if (i == 1 && aList.size() == 2) {
                            mesg += "****";
                        } else {
                            mesg += newMessageArray[j] + " ";
                        }
                        j = j + 1;
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("handleChannelMessageFormat", requestID, " isTempTxnFound=======" + isTempTxnFound + " j======" + j);
                    }

                    // Done to Hide the PIN in logs
                    if (isTempTxnFound) {
                        mesg += "**** ";
                        newMessageArray[j] = tempVar;
                        mesg += tempTxnID;
                        newMessageArray[j + 1] = tempTxnID;
                    } else if (aList.size() > 2 && !PretupsI.KEYWORD_TYPE_REGISTRATION.equalsIgnoreCase(keyword) && !PretupsI.KEYWORD_TYPE_ADMIN.equalsIgnoreCase(keyword)) {
                        mesg += "****";
                        newMessageArray[j] = tempVar;
                    }
                } else {
                    // String[]
                    // messageArray=userMessage.split(CHNL_MESSAGE_SEP);
                    final String[] messageArray = (String[]) BTSLUtil.split(userMessage, chnl_message_sep);
                    // Handle Plain SMS Parsing here
                    for (int i = 0; i < messageArray.length; i++) {
                        aList.add(messageArray[i]);
                    }
                    newMessageArray = new String[aList.size()];

                    final int aListSize = aList.size();
                    for (int i = 0; i < aListSize; i++) {
                        newMessageArray[i] = (String) aList.get(i);
                        if (i == aList.size() - 1) {
                            mesg += "****";
                        } else {
                            mesg += newMessageArray[i] + " ";
                        }
                    }
                }

                p_requestVO.setRequestMessageArray(newMessageArray);
                p_requestVO.setIncomingSmsStr(mesg);
            } else {
                EventHandler.handle(
                                EventIDI.SYSTEM_INFO,
                                EventComponentI.SYSTEM,
                                EventStatusI.RAISED,
                                EventLevelI.INFO,
                                "MessageFormater[handleTransferMessageFormat]",
                                requestID,
                                p_requestVO.getFilteredMSISDN(),
                                "",
                                "Not able to translate the message request for Request ID:" + requestID + " and number:" + p_requestVO.getFilteredMSISDN() + " ,getting Base Exception=" + PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE);
                throw new BTSLBaseException("MessageFormater", "handleTransferMessageFormat", PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE);
            }
        } catch (BTSLBaseException be) {
            _log.error("handleTransferMessageFormat", requestID, "BTSLBaseException for Request ID:" + p_requestVO.getRequestID() + " Exception=" + be.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"MessageFormater[handleTransferMessageFormat]",requestID,p_requestVO.getFilteredMSISDN(),"","Not able to translate the message request for Request ID:"+requestID+" and number:"+p_requestVO.getFilteredMSISDN()+" ,getting Base Exception="+be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("handleTransferMessageFormat", requestID, " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Exception :" + e.getMessage());
            EventHandler.handle(
                            EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM,
                            EventStatusI.RAISED,
                            EventLevelI.FATAL,
                            "MessageFormater[handleTransferMessageFormat]",
                            requestID,
                            p_requestVO.getFilteredMSISDN(),
                            "",
                            "Not able to translate the message request for Request ID:" + requestID + " and MSISDN:" + p_requestVO.getFilteredMSISDN() + " ,getting Exception=" + e
                                            .getMessage());
            throw new BTSLBaseException("MessageFormater", "handleTransferMessageFormat", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            try{
            _log.debug("handleTransferMessageFormat", requestID, "Exiting  messageArray="+BTSLUtil.maskParam(Arrays.toString( p_requestVO.getRequestMessageArray())));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Check whether First Keyword is coming or not
     * 
     * @param p_firstKeyword
     * @return
     */
    /*
     * private static boolean checkFirstKeyword(String p_firstKeyword)
     * {
     * if(Constants.getProperty("smsfirstkeyword").equalsIgnoreCase(p_firstKeyword
     * ))
     * return true;
     * else
     * return false;
     * }
     */

    /**
     * Method to validate the request for Non Buddy transfers
     * 
     * @param p_con
     * @param p_senderVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void validateIfNotBuddy(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "validateIfNotBuddy";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" requestMessageArray length:");
        	loggerValue.append(_requestMessageArray);
            _log.debug("validateIfNotBuddy", loggerValue);
        }
        if (_requestMessageArray.length < 3 || _requestMessageArray.length > 6) {
            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { _requestVO.getActualMessageFormat() }, null);
        }

        final int messageLength = _requestMessageArray.length;
        // if pin Invalid return with error(PIN is Mandatory)
        final String actualPin = p_senderVO.getPin();
        if (_log.isDebugEnabled()) {
            _log.debug("validateIfNotBuddy", " actualPin:" + actualPin);
        }

        switch (messageLength) {
        case 3: {
            // if(!actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))))) {
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { _requestVO.getActualMessageFormat() }, null);
            }

            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }
            _transferVO.setPaymentMethodType(paymentMethodType);
            _incomingSmsStr += paymentMethodType + " ";
            checkAfterPaymentMethod(p_con, 1);
            // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
            break;
        }
        case 4: {
            // Validate 2nd Argument for PIN.
            pin = _requestMessageArray[3];

            _incomingSmsStr += "****" + " ";
            // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                if (actualPin.equalsIgnoreCase(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    if (!BTSLUtil.isNullString(_requestMessageArray[3])) {
                        if (BTSLUtil.isNumeric(_requestMessageArray[3]) && _requestMessageArray[3].length() == 1) {
                            _requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(_requestMessageArray[3]));
                            if (_requestVO.getReceiverLocale() == null) {
                                _requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                            }
                        } else {
                            if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                // BTSLUtil.validatePIN(pin);
                                // modify by santanu for operator specification
                                _operatorUtil.validatePINRules(pin);
                                p_senderVO.setPin(BTSLUtil.encryptText(pin));
                                p_senderVO.setPinUpdateReqd(true);
                                p_senderVO.setActivateStatusReqd(true);
                            }

                        }

                    }
                    /*
                     * //if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                     * if(!BTSLUtil.isNullString(pin)&&!pin.equals(SystemPreferences
                     * .P2P_DEFAULT_SMSPIN))
                     * {
                     * BTSLUtil.validatePIN(pin);
                     * p_senderVO.setPin(BTSLUtil.encryptText(pin));
                     * p_senderVO.setPinUpdateReqd(true);
                     * p_senderVO.setActivateStatusReqd(true);
                     * }
                     */
                } else {
                    try {
                        SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }
            }

            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }
            _transferVO.setPaymentMethodType(paymentMethodType);
            _incomingSmsStr += paymentMethodType + " ";
            checkAfterPaymentMethod(p_con, 1);
            // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);

            break;
        }
        case 5: {

            // Validate 2nd Argument for PIN.
            pin = _requestMessageArray[4];

            _incomingSmsStr += "****" + " ";

            // if pin Invalid return with error(PIN is Mandatory)
            // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                if (actualPin.equalsIgnoreCase(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                    if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // BTSLUtil.validatePIN(pin);
                        _operatorUtil.validatePINRules(pin);
                        p_senderVO.setPin(BTSLUtil.encryptText(pin));
                        p_senderVO.setPinUpdateReqd(true);
                        p_senderVO.setActivateStatusReqd(true);
                    }
                } else {
                    try {
                        SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }
            }

            // if PIN valid
            // Validate next Argument for Payment Method.

            // killed by sanjay as payemnt method table does not exists
            PaymentMethodKeywordVO paymentMethodKeywordVO = null;
            paymentMethodKeyword = _requestMessageArray[1];
            // if paymentMethod invalid , Validate next Argument for Receiver
            // No(MSISDN).
            paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, _transferVO.getServiceType(), _transferVO.getNetworkCode());

            if (paymentMethodKeywordVO == null) {
                paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_transferVO.getServiceType(), senderSubscriberType);
                if (paymentMethodType == null) {
                    // return with error message, no default payment method
                    // defined
                    throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                }
                _transferVO.setPaymentMethodType(paymentMethodType);
                _transferVO.setDefaultPaymentMethod("Y");
                _incomingSmsStr += paymentMethodType + " ";
                checkAfterPaymentMethod(p_con, 1);
                try {
                    // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                    if (!BTSLUtil.isNullString(_requestMessageArray[3])) {

                        _requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(_requestMessageArray[3]));
                        if (_requestVO.getReceiverLocale() == null) {
                            // ankit
                            // zindal
                            // 01/08/06
                            // discussed
                            // by AC/GB
                            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            // _requestVO.setReceiverLocale(new
                            // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        }

                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                }
            } else {
                paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                _incomingSmsStr += paymentMethodType + " ";
                checkAfterPaymentMethod(p_con, 2);
                // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
            }

            break;
        }
        /*
         * case 6:
         * {
         * //Validate 2nd Argument for PIN.
         * pin=_requestMessageArray[5];
         * 
         * _incomingSmsStr+="****"+" ";
         * 
         * //if pin Invalid return with error(PIN is Mandatory)
         * if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
         * {
         * if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
         * {
         * BTSLUtil.validatePIN(pin);
         * p_senderVO.setPin(BTSLUtil.encryptText(pin));
         * p_senderVO.setPinUpdateReqd(true);
         * p_senderVO.setActivateStatusReqd(true);
         * }
         * }
         * else
         * {
         * try
         * {
         * SubscriberBL.validatePIN(p_con,p_senderVO,pin);
         * }
         * catch(BTSLBaseException be)
         * {
         * if(be.isKey() &&
         * ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) ||
         * (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK))))
         * p_con.commit();
         * throw be;
         * }
         * }
         * 
         * //if PIN valid
         * //Validate next Argument for Payment Method.
         * 
         * // killed by sanjay as payemnt method table does not exists
         * PaymentMethodKeywordVO paymentMethodKeywordVO=null;
         * paymentMethodKeyword=_requestMessageArray[1];
         * //if paymentMethod invalid , Validate next Argument for Receiver
         * No(MSISDN).
         * paymentMethodKeywordVO=PaymentMethodCache.getObject(paymentMethodKeyword
         * ,_transferVO.getRequestGatewayType());
         * 
         * if(paymentMethodKeywordVO==null)
         * {
         * paymentMethodType=ServicePaymentMappingCache.getDefaultPaymentMethod(
         * _transferVO.getServiceType(),senderSubscriberType);
         * if(paymentMethodType==null)
         * {
         * //return with error message, no default payment method defined
         * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
         * ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
         * }
         * _transferVO.setPaymentMethodType(paymentMethodType);
         * _transferVO.setDefaultPaymentMethod("Y");
         * _incomingSmsStr+=paymentMethodType+" ";
         * checkAfterPaymentMethod(p_con,1);
         * try
         * {
         * if(!BTSLUtil.isNullString(_requestMessageArray[3]))
         * {
         * int selectorValue=Integer.parseInt(_requestMessageArray[3]);
         * _requestVO.setReqSelector(""+selectorValue);
         * }
         * else
         * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
         * ERROR_INVALID_SELECTOR_VALUE);
         * }
         * catch(Exception e)
         * {
         * throw new BTSLBaseException("","parseRequest",PretupsErrorCodesI.
         * ERROR_INVALID_SELECTOR_VALUE);
         * }
         * }
         * else
         * {
         * paymentMethodType=paymentMethodKeywordVO.getPaymentMethod();
         * _incomingSmsStr+=paymentMethodType+" ";
         * checkAfterPaymentMethod(p_con,2);
         * _requestVO.setReqSelector(""+SystemPreferences.
         * P2P_TRANSFER_DEF_SELECTOR_CODE);
         * }
         * 
         * break;
         * }
         */
        case 6: {
            // Validate 2nd Argument for PIN.
            pin = _requestMessageArray[5];

            _incomingSmsStr += "****" + " ";

            // if pin Invalid return with error(PIN is Mandatory)
            // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                if (actualPin.equalsIgnoreCase(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                    if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // BTSLUtil.validatePIN(pin);
                        _operatorUtil.validatePINRules(pin);
                        p_senderVO.setPin(BTSLUtil.encryptText(pin));
                        p_senderVO.setPinUpdateReqd(true);
                        p_senderVO.setActivateStatusReqd(true);
                    }
                } else {
                    try {
                        SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }
            }
            // if PIN valid as
            // Validate next Argument for Payment Method.

            // paymentMethodKeyword=_requestMessageArray[1];
            // if paymentMethod invalid , Validate next Argument for Receiver
            // No(MSISDN).
            // paymentMethodKeywordVO=PaymentMethodCache.getObject(paymentMethodKeyword,_transferVO.getRequestGatewayType());
            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            } else {
                // paymentMethodType=paymentMethodKeywordVO.getPaymentMethod();
                _incomingSmsStr += paymentMethodType + " ";

                _transferVO.setPaymentMethodType(paymentMethodType);
                _transferVO.setDefaultPaymentMethod("Y");

                // if paymentMethod valid , Validate next Argument for Receiver
                // No(MSISDN).
                checkAfterPaymentMethod(p_con, 1);
                try {
                    if (!BTSLUtil.isNullString(_requestMessageArray[3])) {
                        final int selectorValue = Integer.parseInt(_requestMessageArray[3]);
                        _requestVO.setReqSelector("" + selectorValue);
                    }
                    /*
                     * else
                     * _requestVO.setReqSelector(""+SystemPreferences.
                     * P2P_TRANSFER_DEF_SELECTOR_CODE);
                     * //throw new
                     * BTSLBaseException("","parseRequest",PretupsErrorCodesI
                     * .ERROR_INVALID_SELECTOR_VALUE);
                     */
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                }
                try {
                    if (!BTSLUtil.isNullString(_requestMessageArray[4])) {

                        _requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(_requestMessageArray[4]));
                        if (_requestVO.getReceiverLocale() == null) {
                            throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            // _requestVO.setReceiverLocale(new
                            // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        }

                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("", "parseRequest", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                }

            }

            break;
        }
        }
    }

    /**
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */

    public static void handleExtChannelMessageFormat(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "handleExtChannelMessageFormat";
        final String requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug("handleExtChannelMessageFormat", requestID, "Entered MSISDN: " + p_requestVO.getFilteredMSISDN() + " With Message=" + BTSLUtil.maskParam(p_requestVO.getDecryptedMessage()));
        }
        String userMessage = p_requestVO.getDecryptedMessage();
        final String mesg = "";
        try {
            chnl_message_sep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(chnl_message_sep)) {
                chnl_message_sep = " ";
            }
            userMessage = p_requestVO.getDecryptedMessage();
            if (!BTSLUtil.isNullString(userMessage)) {
                final String[] messageArray = (String[]) BTSLUtil.split(userMessage, chnl_message_sep);
                p_requestVO.setRequestMessageArray(messageArray);
                p_requestVO.setIncomingSmsStr(mesg);
            } else {
                EventHandler.handle(
                                EventIDI.SYSTEM_INFO,
                                EventComponentI.SYSTEM,
                                EventStatusI.RAISED,
                                EventLevelI.INFO,
                                "MessageFormater[handleExtChannelMessageFormat]",
                                requestID,
                                p_requestVO.getFilteredMSISDN(),
                                "",
                                "Not able to translate the message request for Request ID:" + requestID + " ,getting Base Exception=" + PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE);
                throw new BTSLBaseException("MessageFormater", "handleExtChannelMessageFormat", PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE);
            }
        } catch (BTSLBaseException be) {
            _log.error("handleExtChannelMessageFormat", requestID, "BTSLBaseException for Request ID:" + p_requestVO.getRequestID() + " Exception=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("handleExtChannelMessageFormat", requestID, " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Exception :" + e.getMessage());
            EventHandler.handle(
                            EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM,
                            EventStatusI.RAISED,
                            EventLevelI.FATAL,
                            "MessageFormater[handleExtChannelMessageFormat]",
                            requestID,
                            p_requestVO.getFilteredMSISDN(),
                            "",
                            "Not able to translate the message request for Request ID:" + requestID + " and MSISDN:" + p_requestVO.getFilteredMSISDN() + " ,getting Exception=" + e
                                            .getMessage());
            throw new BTSLBaseException("MessageFormater", "handleExtChannelMessageFormat", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            try{
            _log.debug("handleExtChannelMessageFormat", requestID, "Exiting  messageArray="+BTSLUtil.maskParam(Arrays.toString( p_requestVO.getRequestMessageArray())));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
