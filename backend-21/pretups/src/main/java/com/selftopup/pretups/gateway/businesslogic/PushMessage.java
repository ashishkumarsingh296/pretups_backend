package com.selftopup.pretups.gateway.businesslogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.MessageSentLog;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.MessagesCaches;

/*
 * PushMessage.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 12/07/2005 Initial Creation
 * Ankit Zindal 20/11/2006 ChangeID=LOCALEMASTER
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for pushing the message based on the request code and type of the
 * response required
 */

public class PushMessage implements Runnable {

    private static Log _log = LogFactory.getLog(PushMessage.class.getName());
    private String _msisdn = null;
    private String _requestCode = null;
    private Locale _locale = null;
    private String _message = null;
    private String _messageKey = null;
    private String[] _args = null;
    private String _transactionID = null;
    private String _messageType = null;
    private String _messageClass = null;
    private String _pid = null;
    private String _networkCode = null;
    private boolean _entryDoneInLog = false;
    public String TIMEOUT = "TIMEOUT";
    public String FAILED = "FAILED";
    public String _messageCode = null;
    private LocaleMasterVO _localeMasterVO = null;
    private MessageGatewayMappingCacheVO _messageGatewayMappingCacheVO = null;
    public static OperatorUtilI _operatorUtil = null;
    private String _tempMessage = null;
    public static ExecutorService executor = null;

    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[OperatorUtil initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
            executor = Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE")).intValue()));
        } catch (Exception e) {
            executor = Executors.newFixedThreadPool(30);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[thread pool initialize]", "", "", "", "Exception while initilizing the thread pool :" + e.getMessage());
        }

    }

    /**
     * PushMessage Constructor (This constructor will be used for pushing SMS
     * message in WEB and SMS Interface)
     * 
     * @param p_msisdn
     * @param btslMessages
     * @param p_transactionID
     * @param p_requestCode
     * @param p_locale
     * @param p_networkCode
     */
    public PushMessage(String p_msisdn, BTSLMessages btslMessages, String p_transactionID, String p_requestCode, Locale p_locale, String p_networkCode) {
        if (_log.isDebugEnabled())
            _log.debug("PushMessage[pushMessage] at line 92", "");
        _msisdn = p_msisdn;
        _requestCode = p_requestCode;
        _locale = p_locale;
        _messageKey = btslMessages.getMessageKey();
        _args = btslMessages.getArgs();
        _transactionID = p_transactionID;
        _networkCode = p_networkCode;
        // ChangeID=LOCALEMASTER
        // populate the localemasterVO from the LocaleMasterCache for the
        // requested locale
        _localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
    }// end of PushMessage

    /**
     * 
     * @param p_msisdn
     * @param p_message
     * @param p_transactionID
     * @param p_requestCode
     * @param p_locale
     */
    public PushMessage(String p_msisdn, String p_message, String p_transactionID, String p_requestCode, Locale p_locale) {
        if (_log.isDebugEnabled())
            _log.debug("PushMessage[pushMessage] at line 115", "");

        _msisdn = p_msisdn;
        _requestCode = p_requestCode;
        _locale = p_locale;
        _message = p_message;
        _transactionID = p_transactionID;
        // ChangeID=LOCALEMASTER
        // populate the localemasterVO from the LocaleMasterCache for the
        // requested locale
        _localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
    }// end of PushMessage

    /**
     * Method:push
     * This method will be called when we have to send message using charged
     * gateway.
     * It will spawn a new thread internally
     * 
     * @param p_gatewayCode
     * @param p_altGatewayCode
     */
    public void push(String p_gatewayCode, String p_altGatewayCode) {
        if (_log.isDebugEnabled())
            _log.debug("push", "Entered with p_gatewayCode=" + p_gatewayCode + " p_altGatewayCode=" + p_altGatewayCode);
        // Thread pushMessage = new Thread (this);
        try {
            // Starting thread to send message
            _messageType = "PLAIN";
            _messageGatewayMappingCacheVO = new MessageGatewayMappingCacheVO();
            _messageGatewayMappingCacheVO.setResponseCode(p_gatewayCode);
            _messageGatewayMappingCacheVO.setAlternateCode(p_altGatewayCode);
            // pushMessage.start();
            if (_log.isDebugEnabled())
                _log.debug("push", "ThreadPoolExecutor Start");
            executor.execute(this);
            if (_log.isDebugEnabled())
                _log.debug("push", "ThreadPoolExecutor End");
        } catch (Exception ex) {
            _log.error("push", "Getting Exception =" + ex.getMessage());
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[push]", _transactionID, _msisdn, "", "Exception " + ex.getMessage());
        }
    }// end of Push

    /**
     * Method that will be called frpm the application to send message. It will
     * spawn a new thread internally
     */
    public void push() {
        if (_log.isDebugEnabled())
            _log.debug("push", "Entered with _msisdn=" + _msisdn + " _requestCode=" + _requestCode + " _transactionID=" + _transactionID + "_messageKey=" + _messageKey + " _args=" + _args + " _locale=" + _locale + " _message=" + _message);
        // Thread pushMessage = new Thread (this);
        try {
            // Starting thread to send message
            _messageType = "PLAIN";
            // pushMessage.start();
            if (_log.isDebugEnabled())
                _log.debug("push", "ThreadPoolExecutor Start");
            executor.execute(this);
            if (_log.isDebugEnabled())
                _log.debug("push", "ThreadPoolExecutor End");

        } catch (Exception ex) {
            _log.error("push", "Getting Exception =" + ex.getMessage());
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[push]", _transactionID, _msisdn, "", "Exception " + ex.getMessage());
        }
    }// end of Push

    /**
     * Method to push Binary Message
     * 
     */
    public boolean pushBinary() {
        if (_log.isDebugEnabled())
            _log.debug("pushBinary", "Entered with _msisdn=" + _msisdn + " _requestCode=" + _requestCode + " _transactionID=" + _transactionID + " _message=" + _message);
        boolean status = false;
        String statusStr = null;
        try {
            _messageType = "BIN";
            if (_log.isDebugEnabled())
                _log.debug("pushBinary", _transactionID, "Entered _requestCode with " + _requestCode);
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled())
                    _log.debug("pushBinary", "Picked default message gateway for network: " + _networkCode + " _requestCode:" + _requestCode);
            }
            _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            if (_messageGatewayMappingCacheVO != null) {
                statusStr = sendSMSMessage(false);
                _entryDoneInLog = true;
                if (!BTSLUtil.isNullString(statusStr) && statusStr.equalsIgnoreCase(PretupsI.GATEWAY_MESSAGE_SUCCESS))
                    status = true;
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushBinary]", _transactionID, _msisdn, "", SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found ");
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "pushBinary", SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            _log.error("pushBinary", "Getting Exception =" + be.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage());
            be.printStackTrace();
            status = false;
        } catch (Exception ex) {
            _log.error("pushBinary", "Getting Exception =" + ex.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + ex.getMessage());
            ex.printStackTrace();
            status = false;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushBinary]", _transactionID, _msisdn, "", "Exception " + ex.getMessage());
        } finally {
            _messageGatewayMappingCacheVO = null;
        }
        return status;
    }// pushBinary

    public void run() {
        try {
            if (_log.isDebugEnabled())
                _log.debug("run", _transactionID, "Entered _requestCode with " + _requestCode);
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled())
                    _log.debug("run", "Picked default message gateway for network: " + _networkCode + " _requestCode:" + _requestCode);
            }
            if (_messageGatewayMappingCacheVO == null)
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            if (_log.isDebugEnabled())
                _log.debug("run", "after message cache for network: " + _networkCode + " _requestCode:" + _requestCode);

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled())
                    _log.debug("run", "after message cache not null for network: " + _networkCode + " _requestCode:" + _requestCode);

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled())
                    _log.debug("run after checking message key equal to null", _transactionID, "_messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid + " _locale: " + _locale + " _locale language: " + (_locale == null ? "" : _locale.getLanguage()));
                if (_message.indexOf("mclass^") == 0) {
                    int colonIndex = _message.indexOf(":");
                    String messageClassPID = _message.substring(0, colonIndex);
                    String[] messageClassPIDArray = messageClassPID.split("&");
                    _messageClass = messageClassPIDArray[0].split("\\^")[1];
                    _pid = messageClassPIDArray[1].split("\\^")[1];
                    _message = _message.substring(colonIndex + 1);
                    int endIndexForMessageCode;
                    // The block below is used to find the message code from the
                    // message.
                    // In case of arabic colon will be encoded so we find the
                    // end index as 00%3A which
                    // is encoded value of colon.
                    // ChangeID=LOCALEMASTER
                    // check the language from the localeMasterVO
                    if (("ar".equals(_localeMasterVO.getLanguage())) || ("ru".equals(_localeMasterVO.getLanguage()))) {
                        endIndexForMessageCode = _message.indexOf("%00%3A");
                        if (endIndexForMessageCode != -1)
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1)
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if ((("ar".equals(_locale.getLanguage())) || ("ru".equals(_locale.getLanguage()))) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled())
                        _log.debug("run1", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);

                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled())
                        _log.debug("run2", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);

                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }

                // encoding special for arabic
                // if(!(_locale.getLanguage().equals("en")||_locale.getLanguage().equals("fr")))
                // _message =BTSLUtil.encodeSpecial(_message);
                if (_log.isDebugEnabled())
                    _log.debug("run", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);
                sendSMSMessage(false);
                _entryDoneInLog = true;
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[run]", _transactionID, _msisdn, "", "Gateway Code not found , Exception=" + SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "run", SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            _log.error("PushMessage[run]", "Base Exception while sending message=" + be.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage() + " Message code=" + _messageCode);
            be.printStackTrace();
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConnectThread[run]","",_msisdn,"","Message Sending Exception:"+be.getMessage());
        } catch (Exception e) {
            _log.error("PushMessage[run]", "Exception while sending message=" + e.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[run]", "", _msisdn, "", "Message Sending Exception:" + e.getMessage());
        } finally {
            _messageGatewayMappingCacheVO = null;
        }
    }// end of run

    /**
     * Method that will create the url string to be pushed and push the same
     * using URL Connection
     * 
     * @param p_msisdn
     * @param p_message
     * @param p_transactionID
     * @param p_locale
     * @param p_messageGatewayMappingCacheVO
     * @param p_useAlternate
     *            : Whether to use alternate gateway or not
     * @throws Exception
     */
    private String sendSMSMessage(boolean p_useAlternate) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("sendSMSMessage", "Entered with _msisdn=" + _msisdn + " p_message=" + _message + " p_transactionID=" + _transactionID + " _messageType=" + _messageType);
        StringBuffer urlBuff = null;
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        ResponseGatewayVO responseGatewayVO = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            if (p_useAlternate)
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getAlternateCode());
            else
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getResponseCode());

            if (messageGatewayVO != null) {
                responseGatewayVO = messageGatewayVO.getResponseGatewayVO();
                if (_log.isDebugEnabled())
                    _log.debug("sendSMSMessage", "Using Gateway for _msisdn=" + _msisdn + " Details=" + responseGatewayVO.toString());
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "sendSMSMessage", SelfTopUpErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
                else if (!PretupsI.STATUS_ACTIVE.equals(responseGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "sendSMSMessage", SelfTopUpErrorCodesI.RES_MESSAGE_GATEWAY_NOT_ACTIVE);

                String protocol = messageGatewayVO.getProtocol();
                String ip = messageGatewayVO.getHost();
                String port = BTSLUtil.NullToString(responseGatewayVO.getPort());

                if (messageGatewayVO.getGatewayType().equals(PretupsI.GATEWAY_TYPE_SMSC)) {
                    if (_messageType.equalsIgnoreCase("PLAIN"))
                        urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    else
                        urlBuff = getBinaryMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message);

                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage))
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    else
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                } else {
                    // For USSD Push message String
                    if (_messageType.equalsIgnoreCase("PLAIN"))
                        urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    else
                        urlBuff = getBinaryMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message);

                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage))
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    else
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                }
            } else {
                if (p_useAlternate)
                    throw new Exception("PushMessage[sendSMSMessage] Not able to send message for msisdn=" + _msisdn + " Transaction ID=" + _transactionID + " as no alternate gateway found");
                else
                    throw new BTSLBaseException(this, "sendSMSMessage", SelfTopUpErrorCodesI.NO_RES_MAPPING_FOUND_FORREQ);
            }
        } catch (BTSLBaseException be) {
            _log.debug("sendSMSMessage", "_transactionID=" + _transactionID + "Exception =" + be.getMessage());
            if (messageGatewayVO != null && responseGatewayVO != null)
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage() + " Gateway code=" + responseGatewayVO.getGatewayCode() + " Gateway type=" + messageGatewayVO.getGatewayType());
            else
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage());
            // Use the alternate
            // This condition is modified by ankit Zindal for prevent alternate
            // gateway check if alternate gateway code is null.
            if (!p_useAlternate && _messageGatewayMappingCacheVO != null && !BTSLUtil.isNullString(_messageGatewayMappingCacheVO.getAlternateCode()))
                sendSMSMessage(true);
            else {
                // Log in Message Sent Log
                if (messageGatewayVO != null && responseGatewayVO != null)
                    MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                else
                    MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw be;
            }
        } catch (Exception e) {
            _log.debug("sendSMSMessage", "_transactionID=" + _transactionID + " Exception =" + e.getMessage());
            e.printStackTrace();
            // Log in Message Sent Log
            if (messageGatewayVO != null && responseGatewayVO != null)
                MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            else
                MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            _entryDoneInLog = true;
            throw e;
        } finally {
            responseGatewayVO = null;
            messageGatewayVO = null;
        }
        return status;
    }// sendSMSMessage

    /**
     * Method that will connect to the IP and port and will send the message
     * 
     * @param p_messageString
     * @param p_timeout
     * @throws BTSLBaseException
     * @throws Exception
     */
    private String pushGatewayMessage(String p_messageString, int p_timeout) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("pushGatewayMessage", "Entered with _transaction ID=" + _transactionID + " p_messageString=" + p_messageString);
        String status = null;
        try {
            String msgResponse = getResponse(p_messageString, p_timeout);
            status = PretupsI.GATEWAY_MESSAGE_SUCCESS;
            if (_log.isDebugEnabled())
                _log.debug("pushGatewayMessage", "_transaction ID=" + _transactionID + "msgResponse from connector=" + msgResponse);
        } catch (BTSLBaseException bex) {
            _log.error("pushGatewayMessage", "_transaction ID=" + _transactionID + "Exception =" + bex.getMessage());
            bex.printStackTrace();
            status = PretupsI.GATEWAY_MESSAGE_FAILED;
            throw bex;
        } catch (Exception e) {
            _log.error("pushGatewayMessage", "_transaction ID=" + _transactionID + "Exception =" + e.getMessage());
            e.printStackTrace();
            status = PretupsI.GATEWAY_MESSAGE_FAILED;
            throw e;
        }
        return status;
    }// end pushGatewayMessage

    /**
     * Method to construct the plain SMS String
     * 
     * @param p_responseGatewayVO
     * @param p_ipaddress
     * @param p_msisdn
     * @param p_locale
     * @param p_message
     * @return StringBuffer
     * @throws BTSLBaseException
     */
    private StringBuffer getPlainMessageURL(ResponseGatewayVO p_responseGatewayVO, String p_ipaddress, String p_msisdn, Locale p_locale, String p_message, String p_messageClass, String p_pid) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getPlainMessageURL", "Entered with p_ipaddress=" + p_ipaddress + " p_msisdn=" + p_msisdn + "p_message=" + p_message + "p_locale=" + p_locale);
        StringBuffer urlBuff = null;
        String path = BTSLUtil.NullToString(p_responseGatewayVO.getPath());
        String loginID = BTSLUtil.NullToString(p_responseGatewayVO.getLoginID());
        String password = BTSLUtil.NullToString(p_responseGatewayVO.getPassword());

        if (!BTSLUtil.isNullString(password))
            password = BTSLUtil.decryptText(password);
        try {
            urlBuff = new StringBuffer(p_ipaddress + "/" + path);
            urlBuff.append("user=" + loginID);
            urlBuff.append("&pass=" + password);
            // ChangeID=LOCALEMASTER
            // Coding to be used with push message will be done based on locale
            // master entry for the requested locale.
            // If it have value blank or N then no coding will be used otherwise
            // the value from locale master table will be used as coding.
            if (!BTSLUtil.isNullString(_localeMasterVO.getCoding()) && !"N".equals(_localeMasterVO.getCoding()))
                urlBuff.append("&coding=" + _localeMasterVO.getCoding());

            if (_operatorUtil == null) {
                String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

                try {
                    _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
                }
            }

            urlBuff.append("&to=" + _operatorUtil.getOperatorFilteredMSISDN(p_msisdn));
            urlBuff.append("&text=" + p_message);
            if (_log.isDebugEnabled())
                _log.debug("getPlainMessageURL", "p_message After encoding=" + p_message);
            urlBuff.append("&smsc=" + p_responseGatewayVO.getGatewayCode());
            urlBuff.append("&from=" + p_responseGatewayVO.getDestNo());
            if (p_messageClass != null)
                urlBuff.append("&mclass=" + p_messageClass);
            if (p_pid != null)
                urlBuff.append("&pid=" + p_pid);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("PushMessage[getPlainMessageURL]", "_transaction ID=" + _transactionID + " Not able to get the message String Exception =" + e.getMessage());
            throw new BTSLBaseException(this, "getPlainMessageURL", "Not able to get the message String=" + p_msisdn + " Creating Plain URL String Exception:" + e.getMessage());
        }
        if (_log.isDebugEnabled())
            _log.debug("getPlainMessageURL", "Exiting for p_msisdn=" + p_msisdn + " urlBuff=" + urlBuff);
        return urlBuff;
    }// end getPlainMessageURL

    /**
     * Method to construct Binary SMS String
     * 
     * @param p_responseGatewayVO
     * @param p_ipaddress
     * @param p_msisdn
     * @param p_locale
     * @param p_message
     * @return StringBuffer
     * @throws BTSLBaseException
     */
    private StringBuffer getBinaryMessageURL(ResponseGatewayVO p_responseGatewayVO, String p_ipaddress, String p_msisdn, Locale p_locale, String p_message) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getBinaryMessageURL", "Entered with p_ipaddress=" + p_ipaddress + " p_msisdn=" + p_msisdn);
        StringBuffer urlBuff = null;
        String path = BTSLUtil.NullToString(p_responseGatewayVO.getPath());
        String loginID = BTSLUtil.NullToString(p_responseGatewayVO.getLoginID());
        String password = BTSLUtil.NullToString(p_responseGatewayVO.getDecryptedPassword());
        try {
            urlBuff = new StringBuffer(p_ipaddress + "/" + path);
            urlBuff.append("user=" + loginID);
            urlBuff.append("&pass=" + password);
            if (_operatorUtil == null) {
                String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

                try {
                    _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
                }
            }
            urlBuff.append("&to=" + _operatorUtil.getOperatorFilteredMSISDN(p_msisdn));
            // urlBuff.append("&to="+Constants.getProperty("COUNTRY_CODE")+p_msisdn);
            urlBuff.append("&udh=" + Constants.getProperty("BINARY_UDH_MSG_STRING_UDH"));
            urlBuff.append("&pid=" + Constants.getProperty("BINARY_UDH_MSG_STRING_PID"));
            urlBuff.append("&coding=" + Constants.getProperty("BINARY_UDH_MSG_STRING_CODING"));
            urlBuff.append("&alt-dcs=" + Constants.getProperty("BINARY_UDH_MSG_STRING_ALTDCS"));
            urlBuff.append("&mclass=" + Constants.getProperty("BINARY_UDH_MSG_STRING_MCLASS"));
            urlBuff.append("&text=" + p_message);
            urlBuff.append("&smsc=" + p_responseGatewayVO.getGatewayCode());
            urlBuff.append("&from=" + p_responseGatewayVO.getDestNo());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException(this, "getBinaryMessageURL", "Not able to get the binary message URL String=" + p_msisdn + " Creating Binary URL String Exception:" + e.getMessage());
        }
        if (_log.isDebugEnabled())
            _log.debug("getBinaryMessageURL", "Exiting for p_msisdn=" + p_msisdn + " urlBuff=" + urlBuff);
        return urlBuff;
    }// end getBinaryMessageURL

    public static void main(String[] args) {
        String[] arg = { "a", "b" };
        PropertyConfigurator.configure("C:\\ECLIPSE_WORKSPACE\\pretups\\WebRoot\\WEB-INF\\classes\\configfiles\\LogConfig.props");
        ArrayList list = new ArrayList();
        Locale locale = new Locale("en");
        list.add(locale);
        MessagesCaches.load(list);
        BTSLMessages btslMessage = new BTSLMessages("4300", arg);
        new PushMessage("9810821454", btslMessage, "1", "33", locale, "DL").run();
    }// end main

    private String getResponse(String p_url, int p_timeout) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getResponse", "Entered p_url:" + p_url + " p_timeout:" + p_timeout);
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader in = null;
        StringBuffer strBuff = null;
        try {
            url = new URL(p_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setConnectTimeout(Integer.parseInt(Constants.getProperty("CONNECT_TIMEOUT")));
                urlConnection.setReadTimeout(Integer.parseInt(Constants.getProperty("READ_TIMEOUT")));
            } catch (Exception e) {
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
            }
            String line = null;
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            long startTimeinMills = System.currentTimeMillis();
            strBuff = new StringBuffer();
            while (true) {
                if (_log.isDebugEnabled())
                    _log.debug("getResponse", "Inside While Loop Entered");
                if (System.currentTimeMillis() - startTimeinMills > (p_timeout * 1000)) {
                    throw new BTSLBaseException(TIMEOUT);
                }
                line = in.readLine();
                if (line != null) {
                    strBuff.append(line);
                    if (_log.isDebugEnabled())
                        _log.debug("getResponse", "line:" + line);
                    break;
                } else {
                    throw new BTSLBaseException(FAILED);
                }
            }// end of while
             // MessageSentLog.logMessage(strBuff.toString());
            if (_log.isDebugEnabled())
                _log.debug("getResponse", "Exiting While Loop");
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getResponse", "Exeption e:" + e.getMessage());
            e.printStackTrace();
            // MessageSentLog.logMessage(msisdn+"   "+msgStr+"  "+ce.getMessage());
            throw new BTSLBaseException(FAILED);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception ex) {
            }
            try {
                if (urlConnection != null)
                    urlConnection.disconnect();
            } catch (Exception ex) {
            }
            urlConnection = null;
            url = null;
            if (_log.isDebugEnabled())
                _log.debug("getResponse", "Exiting response str:" + (strBuff == null ? "" : strBuff.toString()));
        }// end of finally
        return strBuff.toString();
    }

    /**
     * Message with status tracking
     * 
     * @param p_gatewayCode
     * @param p_altGatewayCode
     * @return
     */
    public String pushMessageWithStatus(String p_gatewayCode, String p_altGatewayCode) {

        String status = PretupsI.GATEWAY_MESSAGE_FAILED;

        try {
            if (_log.isDebugEnabled())
                _log.debug("pushMessageWithStatus", _transactionID, "Entered _requestCode with " + _requestCode);

            if (p_gatewayCode != null && p_altGatewayCode != null) {
                _messageGatewayMappingCacheVO = new MessageGatewayMappingCacheVO();
                _messageGatewayMappingCacheVO.setResponseCode(p_gatewayCode);
                _messageGatewayMappingCacheVO.setAlternateCode(p_altGatewayCode);
            }
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled())
                    _log.debug("pushMessageWithStatus", "Picked default message gateway for network: " + _networkCode + " _requestCode:" + _requestCode);
            }
            if (_messageGatewayMappingCacheVO == null)
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            if (_log.isDebugEnabled())
                _log.debug("pushMessageWithStatus", "after message cache for network: " + _networkCode + " _requestCode:" + _requestCode);

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled())
                    _log.debug("pushMessageWithStatus", "after message cache not null for network: " + _networkCode + " _requestCode:" + _requestCode);

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled())
                    _log.debug("run after checking message key equal to null", _transactionID, "_messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid + " _locale: " + _locale + " _locale language: " + (_locale == null ? "" : _locale.getLanguage()));
                if (_message.indexOf("mclass^") == 0) {
                    int colonIndex = _message.indexOf(":");
                    String messageClassPID = _message.substring(0, colonIndex);
                    String[] messageClassPIDArray = messageClassPID.split("&");
                    _messageClass = messageClassPIDArray[0].split("\\^")[1];
                    _pid = messageClassPIDArray[1].split("\\^")[1];
                    _message = _message.substring(colonIndex + 1);
                    int endIndexForMessageCode;
                    // The block below is used to find the message code from the
                    // message.
                    // In case of arabic colon will be encoded so we find the
                    // end index as 00%3A which
                    // is encoded value of colon.
                    // ChangeID=LOCALEMASTER
                    // check the language from the localeMasterVO
                    if (("ar".equals(_localeMasterVO.getLanguage())) || ("ru".equals(_localeMasterVO.getLanguage()))) {
                        endIndexForMessageCode = _message.indexOf("%00%3A");
                        if (endIndexForMessageCode != -1)
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1)
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if ((("ar".equals(_locale.getLanguage())) || ("ru".equals(_locale.getLanguage()))) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled())
                        _log.debug("run1", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);
                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled())
                        _log.debug("run2", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);
                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }

                // encoding special for arabic
                // if(!(_locale.getLanguage().equals("en")||_locale.getLanguage().equals("fr")))
                // _message =BTSLUtil.encodeSpecial(_message);
                if (_log.isDebugEnabled())
                    _log.debug("pushMessageWithStatus", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);
                _messageType = "PLAIN";
                status = sendSMSMessage(false);
                _entryDoneInLog = true;
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushMessageWithStatus]", _transactionID, _msisdn, "", "Gateway Code not found , Exception=" + SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "pushMessageWithStatus", SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            _log.error("PushMessage[pushMessageWithStatus]", "Base Exception while sending message=" + be.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage() + " Message code=" + _messageCode);
            be.printStackTrace();
        } catch (Exception e) {
            _log.error("PushMessage[pushMessageWithStatus]", "Exception while sending message=" + e.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushMessageWithStatus]", "", _msisdn, "", "Message Sending Exception:" + e.getMessage());
        } finally {
            _messageGatewayMappingCacheVO = null;
        }
        return status;
    }

    /**
     * This method is used to send the SMS and return the delivery reciept
     * status
     * 
     * @param boolean p_useAlternate
     * @param String
     *            p_transferID
     * @return String status
     */

    public String pushSmsUrlWithReceipt(boolean p_useAlternate, String p_transferID, String p_gatewayCode, String p_altGatewayCode) {
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        boolean _entryDoneInLog = false;
        try {
            if (p_gatewayCode != null && p_altGatewayCode != null) {
                _messageGatewayMappingCacheVO = new MessageGatewayMappingCacheVO();
                _messageGatewayMappingCacheVO.setResponseCode(p_gatewayCode);
                _messageGatewayMappingCacheVO.setAlternateCode(p_altGatewayCode);
            }
            if (_log.isDebugEnabled())
                _log.debug("pushSmsUrlWithReceipt", _transactionID, "Entered p_useAlternate = " + p_useAlternate + "p_transferID=" + p_transferID);
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled())
                    _log.debug("pushSmsUrlWithReceipt", "Picked default message gateway for network: " + _networkCode + " _requestCode:" + _requestCode);
            }
            if (_messageGatewayMappingCacheVO == null)
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            if (_log.isDebugEnabled())
                _log.debug("pushSmsUrlWithReceipt", "after message cache for network: " + _networkCode + " _requestCode:" + _requestCode);

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled())
                    _log.debug("pushSmsUrlWithReceipt", "after message cache not null for network: " + _networkCode + " _requestCode:" + _requestCode);

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled())
                    _log.debug("run after checking message key equal to null", _transactionID, "_messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid + " _locale: " + _locale + " _locale language: " + (_locale == null ? "" : _locale.getLanguage()));
                if (_message.indexOf("mclass^") == 0) {
                    int colonIndex = _message.indexOf(":");
                    String messageClassPID = _message.substring(0, colonIndex);
                    String[] messageClassPIDArray = messageClassPID.split("&");
                    _messageClass = messageClassPIDArray[0].split("\\^")[1];
                    _pid = messageClassPIDArray[1].split("\\^")[1];
                    _message = _message.substring(colonIndex + 1);
                    int endIndexForMessageCode;
                    // The block below is used to find the message code from the
                    // message.
                    // In case of arabic colon will be encoded so we find the
                    // end index as 00%3A which
                    // is encoded value of colon.
                    // ChangeID=LOCALEMASTER
                    // check the language from the localeMasterVO
                    if (("ar".equals(_localeMasterVO.getLanguage())) || ("ru".equals(_localeMasterVO.getLanguage()))) {
                        endIndexForMessageCode = _message.indexOf("%00%3A");
                        if (endIndexForMessageCode != -1)
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1)
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if ((("ar".equals(_locale.getLanguage())) || ("ru".equals(_locale.getLanguage()))) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled())
                        _log.debug("run1", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);

                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled())
                        _log.debug("run2", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);

                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }
                if (_log.isDebugEnabled())
                    _log.debug("pushMessageWithStatus", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);
                status = sendSMSMessageWithReceipt(false, p_transferID);
                _entryDoneInLog = true;
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushSmsUrlWithReceipt]", _transactionID, _msisdn, "", "Gateway Code not found , Exception=" + SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "pushSmsUrlWithReceipt", SelfTopUpErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            _log.error("PushMessage[pushSmsUrlWithReceipt]", "Base Exception while sending message=" + be.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage() + " Message code=" + _messageCode);
            be.printStackTrace();
        } catch (Exception e) {
            _log.error("PushMessage[pushSmsUrlWithReceipt]", "Exception while sending message=" + e.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushMessageWithStatus]", "", _msisdn, "", "Message Sending Exception:" + e.getMessage());
        } finally {
            _messageGatewayMappingCacheVO = null;
        }
        return status;
    }// end of sendSmsUrlWithReceipt

    /**
     * Method that will create the url string to be pushed and push the same
     * using URL Connection
     * 
     * @param p_msisdn
     * @param p_message
     * @param p_transactionID
     * @param p_locale
     * @param p_messageGatewayMappingCacheVO
     * @param p_useAlternate
     *            : Whether to use alternate gateway or not
     * @throws Exception
     */
    private String sendSMSMessageWithReceipt(boolean p_useAlternate, String p_transferID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("sendSMSMessageWithReceipt", "Entered with _msisdn=" + _msisdn + " p_message=" + _message + " p_transactionID=" + _transactionID + " _messageType=" + _messageType);
        StringBuffer urlBuff = null;
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        ResponseGatewayVO responseGatewayVO = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            if (p_useAlternate)
                messageGatewayVO = MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getAlternateCode());
            else
                messageGatewayVO = MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getResponseCode());

            if (messageGatewayVO != null) {
                responseGatewayVO = messageGatewayVO.getResponseGatewayVO();
                if (_log.isDebugEnabled())
                    _log.debug("sendSMSMessage", "Using Gateway for _msisdn=" + _msisdn + " Details=" + responseGatewayVO.toString());
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "sendSMSMessage", SelfTopUpErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
                else if (!PretupsI.STATUS_ACTIVE.equals(responseGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "sendSMSMessage", SelfTopUpErrorCodesI.RES_MESSAGE_GATEWAY_NOT_ACTIVE);
                String protocol = messageGatewayVO.getProtocol();
                String ip = messageGatewayVO.getHost();
                String port = BTSLUtil.NullToString(responseGatewayVO.getPort());

                if (messageGatewayVO.getGatewayType().equals(PretupsI.GATEWAY_TYPE_SMSC)) {
                    urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    urlBuff.append("&validity=" + Constants.getProperty("MESSAGE_VALIDITY_PERIOD"));
                    urlBuff.append("&valtype=1");
                    urlBuff.append("&dlrmask=19");
                    urlBuff.append("&dlrid=" + p_transferID);
                    String temp = Constants.getProperty("VOMS_DELRCT_TRACK_URL");
                    if (!BTSLUtil.isNullString(temp)) {
                        urlBuff.append("&");
                        urlBuff.append("dlrurl=" + temp);
                        urlBuff.append("msisdn=" + _msisdn);
                        urlBuff.append("&dlrid=" + p_transferID + "&type=");
                        urlBuff.append("%d");
                    }
                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage))
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    else
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                } else {
                    // For USSD Push message String
                    urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage))
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    else
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                }
            } else {
                if (p_useAlternate)
                    throw new Exception("PushMessage[sendSMSMessage] Not able to send message for msisdn=" + _msisdn + " Transaction ID=" + _transactionID + " as no alternate gateway found");
                throw new BTSLBaseException(this, "sendSMSMessage", SelfTopUpErrorCodesI.NO_RES_MAPPING_FOUND_FORREQ);
            }
        } catch (BTSLBaseException be) {
            _log.debug("sendSMSMessage", "_transactionID=" + _transactionID + "Exception =" + be.getMessage());
            if (messageGatewayVO != null && responseGatewayVO != null)
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage() + " Gateway code=" + responseGatewayVO.getGatewayCode() + " Gateway type=" + messageGatewayVO.getGatewayType());
            else
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage());
            // Use the alternate
            // This condition is modified by ankit Zindal for prevent alternate
            // gateway check if alternate gateway code is null.
            if (!p_useAlternate && _messageGatewayMappingCacheVO != null && !BTSLUtil.isNullString(_messageGatewayMappingCacheVO.getAlternateCode()))
                sendSMSMessageWithReceipt(true, p_transferID);
            else {
                // Log in Message Sent Log
                if (messageGatewayVO != null && responseGatewayVO != null)
                    MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                else
                    MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw be;
            }
        } catch (Exception e) {
            _log.debug("sendSMSMessage", "_transactionID=" + _transactionID + " Exception =" + e.getMessage());
            e.printStackTrace();
            // Log in Message Sent Log
            if (messageGatewayVO != null && responseGatewayVO != null)
                MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            else
                MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            _entryDoneInLog = true;
            throw e;
        } finally {
            responseGatewayVO = null;
            messageGatewayVO = null;
        }
        return status;
    }// sendSMSMessage

    /**
     * Method that will be called from alarm sender to push the alarm on admin
     * numbers.
     */
    public void pushAlarm() {
        if (_log.isDebugEnabled())
            _log.debug("pushAlarm", "Entered with _msisdn=" + _msisdn + " _requestCode=" + _requestCode + " _transactionID=" + _transactionID + "_messageKey=" + _messageKey + " _args=" + _args + " _locale=" + _locale + " _message=" + _message);

        // Starting thread to send message
        _messageType = "PLAIN";

        try {
            if (_log.isDebugEnabled())
                _log.debug("pushAlarm", _transactionID, "Entered _requestCode with " + _requestCode);
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled())
                    _log.debug("pushAlarm", "Picked default message gateway for network: " + _networkCode + " _requestCode:" + _requestCode);
            }
            if (_messageGatewayMappingCacheVO == null)
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            if (_log.isDebugEnabled())
                _log.debug("pushAlarm", "after message cache for network: " + _networkCode + " _requestCode:" + _requestCode);

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled())
                    _log.debug("pushAlarm", "after message cache not null for network: " + _networkCode + " _requestCode:" + _requestCode);

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled())
                    _log.debug("pushAlarm after checking message key equal to null", _transactionID, "_messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid + " _locale: " + _locale + " _locale language: " + (_locale == null ? "" : _locale.getLanguage()));
                if (_message.indexOf("mclass^") == 0) {
                    int colonIndex = _message.indexOf(":");
                    String messageClassPID = _message.substring(0, colonIndex);
                    String[] messageClassPIDArray = messageClassPID.split("&");
                    _messageClass = messageClassPIDArray[0].split("\\^")[1];
                    _pid = messageClassPIDArray[1].split("\\^")[1];
                    _message = _message.substring(colonIndex + 1);
                    int endIndexForMessageCode;
                    // The block below is used to find the message code from the
                    // message.
                    // In case of arabic colon will be encoded so we find the
                    // end index as 00%3A which
                    // is encoded value of colon.
                    // ChangeID=LOCALEMASTER
                    // check the language from the localeMasterVO
                    if (("ar".equals(_localeMasterVO.getLanguage())) || ("ru".equals(_localeMasterVO.getLanguage()))) {
                        endIndexForMessageCode = _message.indexOf("%00%3A");
                        if (endIndexForMessageCode != -1)
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1)
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if ((("ar".equals(_locale.getLanguage())) || ("ru".equals(_locale.getLanguage()))) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled())
                        _log.debug("pushAlarm1", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);

                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled())
                        _log.debug("pushAlarm2", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);

                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }

                // encoding special for arabic
                // if(!(_locale.getLanguage().equals("en")||_locale.getLanguage().equals("fr")))
                // _message =BTSLUtil.encodeSpecial(_message);
                if (_log.isDebugEnabled())
                    _log.debug("pushAlarm", _transactionID, "_message: " + _message + " _messageKey: " + _messageKey + "  _messageClass: " + _messageClass + " _pid: " + _pid);
                sendAlarmMessage(false);
                _entryDoneInLog = true;
            } else {
                _log.error("PushMessage[pushAlarm]", "Message Gateway Code not found");
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
            }
        } catch (Exception e) {
            _log.error("PushMessage[pushAlarm]", "Exception while sending message=" + e.getMessage());
            if (!_entryDoneInLog)
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
        } finally {
            _messageGatewayMappingCacheVO = null;
        }
    }// end of PushAlarm

    /**
     * Method that will create the url string to be pushed and push the same
     * using URL Connection
     * 
     * @param p_msisdn
     * @param p_message
     * @param p_transactionID
     * @param p_locale
     * @param p_messageGatewayMappingCacheVO
     * @param p_useAlternate
     *            : Whether to use alternate gateway or not
     * @throws Exception
     */
    private String sendAlarmMessage(boolean p_useAlternate) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("sendAlarmMessage", "Entered with _msisdn=" + _msisdn + " p_message=" + _message + " p_transactionID=" + _transactionID + " _messageType=" + _messageType);
        StringBuffer urlBuff = null;
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        ResponseGatewayVO responseGatewayVO = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            if (p_useAlternate)
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getAlternateCode());
            else
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getResponseCode());

            if (messageGatewayVO != null) {
                responseGatewayVO = messageGatewayVO.getResponseGatewayVO();
                if (_log.isDebugEnabled())
                    _log.debug("sendAlarmMessage", "Using Gateway for _msisdn=" + _msisdn + " Details=" + responseGatewayVO.toString());
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "sendAlarmMessage", SelfTopUpErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
                else if (!PretupsI.STATUS_ACTIVE.equals(responseGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "sendAlarmMessage", SelfTopUpErrorCodesI.RES_MESSAGE_GATEWAY_NOT_ACTIVE);

                String protocol = messageGatewayVO.getProtocol();
                String ip = messageGatewayVO.getHost();
                String port = BTSLUtil.NullToString(responseGatewayVO.getPort());

                if (messageGatewayVO.getGatewayType().equals(PretupsI.GATEWAY_TYPE_SMSC)) {
                    if (_messageType.equalsIgnoreCase("PLAIN"))
                        urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    else
                        urlBuff = getBinaryMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message);

                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage))
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    else
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                } else {
                    // For USSD Push message String
                    if (_messageType.equalsIgnoreCase("PLAIN"))
                        urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    else
                        urlBuff = getBinaryMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message);

                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage))
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    else
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                }
            } else {
                if (p_useAlternate)
                    throw new Exception("PushMessage[sendAlarmMessage] Not able to send message for msisdn=" + _msisdn + " Transaction ID=" + _transactionID + " as no alternate gateway found");
                else
                    throw new BTSLBaseException(this, "sendSMSMessage", SelfTopUpErrorCodesI.NO_RES_MAPPING_FOUND_FORREQ);
            }
        } catch (BTSLBaseException be) {
            _log.debug("sendAlarmMessage", "_transactionID=" + _transactionID + "Exception =" + be.getMessage());
            /*
             * if(messageGatewayVO!=null && responseGatewayVO!=null)
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.FATAL,"PushMessage[pushGatewayMessage]"
             * ,"",_msisdn
             * ,"","Message Sending Exception:"+be.getMessage()+" Gateway code="
             * +
             * responseGatewayVO.getGatewayCode()+" Gateway type="+messageGatewayVO
             * .getGatewayType());
             * else
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.FATAL,"PushMessage[pushGatewayMessage]"
             * ,"",_msisdn,"","Message Sending Exception:"+be.getMessage());
             */
            // Use the alternate
            // This condition is modified by ankit Zindal for prevent alternate
            // gateway check if alternate gateway code is null.
            if (!p_useAlternate && _messageGatewayMappingCacheVO != null && !BTSLUtil.isNullString(_messageGatewayMappingCacheVO.getAlternateCode()))
                sendAlarmMessage(true);
            else {
                // Log in Message Sent Log
                if (messageGatewayVO != null && responseGatewayVO != null)
                    MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                else
                    MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw be;
            }
        } catch (Exception e) {
            _log.debug("sendAlarmMessage", "_transactionID=" + _transactionID + " Exception =" + e.getMessage());
            e.printStackTrace();
            // Log in Message Sent Log
            if (messageGatewayVO != null && responseGatewayVO != null)
                MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            else
                MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            _entryDoneInLog = true;
            throw e;
        } finally {
            responseGatewayVO = null;
            messageGatewayVO = null;
        }
        return status;
    }// sendAlarmMessage

    // for hiding PIN password in message sent log
    /**
     * PushMessage Constructor (This constructor will be used for pushing SMS
     * message in WEB and SMS Interface)
     * 
     * @param p_msisdn
     * @param btslMessages
     * @param p_transactionID
     * @param p_requestCode
     * @param p_locale
     * @param p_networkCode
     */
    public PushMessage(String p_msisdn, BTSLMessages btslMessages, String p_transactionID, String p_requestCode, Locale p_locale, String p_networkCode, String p_tempMessage) {
        if (_log.isDebugEnabled())
            _log.debug("PushMessage[pushMessage] at line 92", "");
        _msisdn = p_msisdn;
        _requestCode = p_requestCode;
        _locale = p_locale;
        _messageKey = btslMessages.getMessageKey();
        _args = btslMessages.getArgs();
        _transactionID = p_transactionID;
        _networkCode = p_networkCode;
        // ChangeID=LOCALEMASTER
        // populate the localemasterVO from the LocaleMasterCache for the
        // requested locale
        _localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
        _tempMessage = p_tempMessage;
    }// end of PushMessage

}
