package com.btsl.pretups.gateway.businesslogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import org.apache.log4j.PropertyConfigurator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.MessageSentLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.MessagesCaches;

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
    public String _serviceType = null;
    private LocaleMasterVO _localeMasterVO = null;
    private MessageGatewayMappingCacheVO _messageGatewayMappingCacheVO = null;
    public static OperatorUtilI _operatorUtil = null;
    private String _tempMessage = null;
    public static ExecutorService executor = null;
    public static String _smsServiceTypesSenderName = null;

    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[OperatorUtil initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        _smsServiceTypesSenderName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_SENDER_NAME_FOR_SERVICE_TYPE);
        try {
            executor = Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE")).intValue()));
        } catch (Exception e) {
            executor = Executors.newFixedThreadPool(30);
            _log.errorTrace("static", e);
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
        final String methodName="PushMessage[pushMessage] at line 105";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_msisdn=").append(p_msisdn);
        	loggerValue.append(" p_requestCode=").append(p_requestCode);
        	loggerValue.append(" p_transactionID=").append(p_transactionID);
        	loggerValue.append(" p_requestCode=").append(p_requestCode);
        	loggerValue.append(" p_locale=").append(p_locale);
        	loggerValue.append(" p_networkCode=").append(p_networkCode);
        	_log.debug(methodName, loggerValue.toString());
    	}
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
        final String methodName="PushMessage[pushMessage]  at line 141";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_msisdn=").append(p_msisdn);
        	loggerValue.append(" p_requestCode=").append(p_requestCode);
        	loggerValue.append(" p_transactionID=").append(p_transactionID);
        	loggerValue.append(" p_requestCode=").append(p_requestCode);
        	loggerValue.append(" p_locale=").append(p_locale);
        	_log.debug(methodName, loggerValue);
    	}
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
        final String METHOD_NAME = "push";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_gatewayCode=").append(p_gatewayCode);
        	loggerValue.append(" p_altGatewayCode=").append(p_altGatewayCode);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        // Thread pushMessage = new Thread (this);
        try {
            // Starting thread to send message
            _messageType = "PLAIN";
            _messageGatewayMappingCacheVO = new MessageGatewayMappingCacheVO();
            _messageGatewayMappingCacheVO.setResponseCode(p_gatewayCode);
            _messageGatewayMappingCacheVO.setAlternateCode(p_altGatewayCode);
            // pushMessage.start();
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
	        	loggerValue.append("ThreadPoolExecutor Start");
	        	_log.debug(METHOD_NAME, loggerValue); 
            }
            executor.execute(this);
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
	        	loggerValue.append("ThreadPoolExecutor End");
	        	_log.debug(METHOD_NAME, loggerValue); 
            }
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Getting Exception =").append(ex.getMessage());
        	_log.error(METHOD_NAME, loggerValue); 
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[push]", _transactionID, _msisdn, "", "Exception " + ex.getMessage());
        }
    }// end of Push

    /**
     * Method that will be called frpm the application to send message. It will
     * spawn a new thread internally
     */
    public void push() {
        final String METHOD_NAME = "push";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" _msisdn=").append(_msisdn);
        	loggerValue.append(" _requestCode=").append(_requestCode);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _messageKey=").append(_messageKey);
        	loggerValue.append(" _args=").append(_args);
        	loggerValue.append(" _locale=").append(_locale);
        	loggerValue.append(" _message=").append(_message);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        // Thread pushMessage = new Thread (this);
        try {
            // Starting thread to send message
            _messageType = "PLAIN";
            // pushMessage.start();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
	        	loggerValue.append("ThreadPoolExecutor Start");
	        	_log.debug(METHOD_NAME, loggerValue);
            }
            executor.execute(this);
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
	        	loggerValue.append("ThreadPoolExecutor End");
	        	_log.debug(METHOD_NAME, loggerValue);
            }

        } catch (Exception ex) {
            loggerValue.setLength(0);
			loggerValue.append("Getting Exception =").append(ex.getMessage());
			_log.error(METHOD_NAME, loggerValue); 
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[push]", _transactionID, _msisdn, "", "Exception " + ex.getMessage());
        }
    }// end of Push

    /**
     * Method to push Binary Message
     * 
     */
    public boolean pushBinary() {
        final String METHOD_NAME = "pushBinary";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" _msisdn=").append(_msisdn);
        	loggerValue.append(" _requestCode=").append(_requestCode);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _message=").append(_message);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        boolean status = false;
        String statusStr = null;
        try {
            _messageType = "BIN";
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
                    loggerValue.append("Picked default message gateway for");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }
            }
            _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            if (_messageGatewayMappingCacheVO != null) {
                statusStr = sendSMSMessage(false);
                _entryDoneInLog = true;
                if (!BTSLUtil.isNullString(statusStr) && statusStr.equalsIgnoreCase(PretupsI.GATEWAY_MESSAGE_SUCCESS)) {
                    status = true;
                }
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushBinary]", _transactionID, _msisdn, "", PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found ");
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "pushBinary", PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            _log.error("pushBinary", "Getting Exception =" + be.getMessage());
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage());
            }
            _log.errorTrace(METHOD_NAME, be);
            status = false;
        } catch (Exception ex) {
            _log.error("pushBinary", "Getting Exception =" + ex.getMessage());
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + ex.getMessage());
            }
            _log.errorTrace(METHOD_NAME, ex);
            status = false;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushBinary]", _transactionID, _msisdn, "", "Exception " + ex.getMessage());
        } finally {
            _messageGatewayMappingCacheVO = null;
        }
        return status;
    }// pushBinary

    public void run() {
        final String METHOD_NAME = "run";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _requestCode=").append(_requestCode);
        	loggerValue.append(" _messageKey=").append(_messageKey);
        	loggerValue.append(" _args=").append(_args);
        	loggerValue.append(" _locale=").append(_locale);
        	loggerValue.append(" _message=").append(_message);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        try {
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("Picked default message gateway for");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }
            }
            if (_messageGatewayMappingCacheVO == null) {
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
                loggerValue.append("after message cache for network");
                loggerValue.append(" _networkCode=").append(_networkCode);
            	loggerValue.append(" _requestCode=").append(_requestCode);
            	_log.debug(METHOD_NAME, loggerValue);
            }

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
                    loggerValue.append("after message cache not null for network");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled()) {
                   loggerValue.setLength(0);
                    loggerValue.append("run after checking message key equal to null");
                    loggerValue.append(" _transactionID=").append(_transactionID);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	loggerValue.append(" _locale=").append(_locale);
                	loggerValue.append(" _locale language=").append((_locale == null ? "" : _locale.getLanguage()));
                	_log.debug(METHOD_NAME, loggerValue); 
                }
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
                    if (("ar".equals(_localeMasterVO.getLanguage())) || ("ru".equals(_localeMasterVO.getLanguage()) || ("fa".equals(_localeMasterVO.getLanguage())))) {
                        endIndexForMessageCode = _message.indexOf("%00%3A");
                        if (endIndexForMessageCode != -1) {
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                        }
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1) {
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                        }
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if (("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage())) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                        loggerValue.append("run1");
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }

                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                        loggerValue.append("run2");
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }

                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }

                // encoding special for arabic
                // if(!(_locale.getLanguage().equals("en")||_locale.getLanguage().equals("fr")))
                // _message =BTSLUtil.encodeSpecial(_message);
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
                    loggerValue.append("run");
                    loggerValue.append(" _transactionID=").append(_transactionID);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	_log.debug(METHOD_NAME, loggerValue);
                }
                sendSMSMessage(false);
                _entryDoneInLog = true;
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[run]", _transactionID, _msisdn, "", "Gateway Code not found , Exception=" + PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "run", PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception while sending message=").append(be.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage() + " Message code=" + _messageCode);
            }
            _log.errorTrace(METHOD_NAME, be);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ConnectThread[run]","",_msisdn,"","Message Sending Exception:"+be.getMessage());
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception while sending message=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
            }
            _log.errorTrace(METHOD_NAME, e);
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
    private String sendSMSMessage(boolean p_useAlternate) throws BTSLBaseException {
        final String METHOD_NAME = "sendSMSMessage";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" _msisdn=").append(_msisdn);
        	loggerValue.append(" _message=").append(_message);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _messageType=").append(_messageType);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        StringBuffer urlBuff = null;
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        ResponseGatewayVO responseGatewayVO = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            if (p_useAlternate) {
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getAlternateCode());
            } else {
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getResponseCode());
            }

            if (messageGatewayVO != null) {
                responseGatewayVO = messageGatewayVO.getResponseGatewayVO();
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
    	        	loggerValue.append("Using Gateway for _msisdn=").append(_msisdn);
    	        	loggerValue.append(" Details=").append(responseGatewayVO.toString());
    	        	_log.debug(METHOD_NAME, loggerValue); 
                }
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
                    throw new BTSLBaseException(this, "sendSMSMessage", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
                } else if (!PretupsI.STATUS_ACTIVE.equals(responseGatewayVO.getStatus())) {
                    throw new BTSLBaseException(this, "sendSMSMessage", PretupsErrorCodesI.RES_MESSAGE_GATEWAY_NOT_ACTIVE);
                }

                String protocol = messageGatewayVO.getProtocol();
                String ip = messageGatewayVO.getHost();
                String port = BTSLUtil.NullToString(responseGatewayVO.getPort());

                /*
                 * if(messageGatewayVO.getGatewayType().equals(PretupsI.
                 * GATEWAY_TYPE_SMSC))
                 * {
                 * if(_messageType.equalsIgnoreCase("PLAIN"))
                 * urlBuff=getPlainMessageURL(responseGatewayVO,protocol+"://"+ip
                 * +":"+port,_msisdn,_locale,_message,_messageClass,_pid);
                 * else
                 * urlBuff=getBinaryMessageURL(responseGatewayVO,protocol+"://"+ip
                 * +":"+port,_msisdn,_locale,_message);
                 * 
                 * status=pushGatewayMessage(urlBuff.toString(),responseGatewayVO
                 * .getTimeOut());
                 * //Log in Message Sent Log
                 * if(!BTSLUtil.isNullString(_tempMessage))
                 * MessageSentLog.log(_msisdn,_locale,messageGatewayVO.
                 * getGatewayType
                 * (),responseGatewayVO.getGatewayCode(),"",status,
                 * _tempMessage,"Message Class="
                 * +_messageClass+" PID="+_pid+" Message code="+_messageCode);
                 * else
                 * MessageSentLog.log(_msisdn,_locale,messageGatewayVO.
                 * getGatewayType
                 * (),responseGatewayVO.getGatewayCode(),"",status,
                 * urlBuff.toString
                 * (),"Message Class="+_messageClass+" PID="+_pid
                 * +" Message code="+_messageCode);
                 * }
                 * else
                 */
                {
                	
                	String neMicroServiceOn = Constants.getProperty("NE.MICROSERVICE.ON");
                    boolean neOn = false;
                    
                    if(neMicroServiceOn != null && neMicroServiceOn.equalsIgnoreCase("Y")) {
                    	neOn = true;
                    }
                    
                    if(!neOn) {
                    // For USSD Push message String
                    if ("PLAIN".equalsIgnoreCase(_messageType)) {
                        urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    } else {
                        urlBuff = getBinaryMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message);
                    }

                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    }else {
                    
                    	PublishtoNotificationQueue.publishNotification(_message, _msisdn);
                    }
                    
                    
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage)) {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    } else {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    }
                }
            } else {
                if (p_useAlternate) {
                    throw new Exception("PushMessage[sendSMSMessage] Not able to send message for msisdn=" + _msisdn + " Transaction ID=" + _transactionID + " as no alternate gateway found");
                } else {
                    throw new BTSLBaseException(this, "sendSMSMessage", PretupsErrorCodesI.NO_RES_MAPPING_FOUND_FORREQ);
                }
            }
        } catch (BTSLBaseException be) {
            loggerValue.setLength(0);
        	loggerValue.append("_transactionID=").append(_transactionID);
        	loggerValue.append(" Exception =").append(be.getMessage());
        	_log.error(METHOD_NAME, loggerValue); 
            if (messageGatewayVO != null && responseGatewayVO != null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage() + " Gateway code=" + responseGatewayVO.getGatewayCode() + " Gateway type=" + messageGatewayVO.getGatewayType());
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage());
            }
            // Use the alternate
            // This condition is modified by ankit Zindal for prevent alternate
            // gateway check if alternate gateway code is null.
            if (!p_useAlternate && _messageGatewayMappingCacheVO != null && !BTSLUtil.isNullString(_messageGatewayMappingCacheVO.getAlternateCode())) {
                sendSMSMessage(true);
            } else {
                // Log in Message Sent Log
                if (messageGatewayVO != null && responseGatewayVO != null) {
                    MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                } else {
                    MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                }
                _entryDoneInLog = true;
                throw be;
            }
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Exception=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            // Log in Message Sent Log
            if (messageGatewayVO != null && responseGatewayVO != null) {
                MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            } else {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            }
            _entryDoneInLog = true;
            //throw e;
            throw new BTSLBaseException(e.getMessage());
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
    private String pushGatewayMessage(String p_messageString, int p_timeout) throws BTSLBaseException {
        final String METHOD_NAME = "pushGatewayMessage";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" p_messageString=").append(p_messageString);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        String status = null;
        try {
            String msgResponse = getResponse(p_messageString, p_timeout);
            status = PretupsI.GATEWAY_MESSAGE_SUCCESS;
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append(" _transactionID=").append(_transactionID);
            	loggerValue.append(" msgResponse=").append(msgResponse);
            	_log.debug(METHOD_NAME, loggerValue);
            }
        } catch (BTSLBaseException bex) {
            loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Exception=").append(bex.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, bex);
            status = PretupsI.GATEWAY_MESSAGE_FAILED;
            throw bex;
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Exception=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            status = PretupsI.GATEWAY_MESSAGE_FAILED;
            //throw e;
            throw new BTSLBaseException(e.getMessage());
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
        final String METHOD_NAME = "getPlainMessageURL";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_ipaddress=").append(p_ipaddress);
        	loggerValue.append(" p_msisdn=").append(p_msisdn);
        	loggerValue.append(" p_message=").append(p_message);
        	loggerValue.append(" p_locale=").append(p_locale);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        StringBuffer urlBuff = null;
        String path = BTSLUtil.NullToString(p_responseGatewayVO.getPath());
        String loginID = BTSLUtil.NullToString(p_responseGatewayVO.getLoginID());
        String password = BTSLUtil.NullToString(p_responseGatewayVO.getPassword());

        if (!BTSLUtil.isNullString(password)) {
            password = BTSLUtil.decryptText(password);
        }
        try {
            urlBuff = new StringBuffer(p_ipaddress + "/" + path);
            urlBuff.append("user=" + loginID);
            if (!BTSLUtil.isNullString(password) && "SHA".equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                password = URLEncoder.encode(password);
            }
            urlBuff.append("&pass=" + password);
            // ChangeID=LOCALEMASTER
            // Coding to be used with push message will be done based on locale
            // master entry for the requested locale.
            // If it have value blank or N then no coding will be used otherwise
            // the value from locale master table will be used as coding.
            if (!BTSLUtil.isNullString(_localeMasterVO.getCoding()) && !"N".equals(_localeMasterVO.getCoding())) {
                urlBuff.append("&coding=" + _localeMasterVO.getCoding());
            }

            if (_operatorUtil == null) {
                String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

                try {
                    _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
                }
            }

            urlBuff.append("&to=" + _operatorUtil.getOperatorFilteredMSISDN(p_msisdn));
            urlBuff.append("&text=" + p_message);
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);  
                loggerValue.append("After encoding : "); 
            	loggerValue.append(" p_message=").append(p_message);
            	_log.debug(METHOD_NAME, loggerValue);
            }
            urlBuff.append("&smsc=" + p_responseGatewayVO.getGatewayCode());
            //Added for SMS Sender Name based on Service Type
            if (_smsServiceTypesSenderName == null) {
            	_smsServiceTypesSenderName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_SENDER_NAME_FOR_SERVICE_TYPE);
            }
            if (_log.isDebugEnabled()) {
    			loggerValue.setLength(0);  
                loggerValue.append(" _smsServiceTypesSenderName=").append(_smsServiceTypesSenderName);
            	_log.debug(METHOD_NAME, loggerValue);
    		}
            if(!BTSLUtil.isNullString(_smsServiceTypesSenderName) && !BTSLUtil.isNullString(_serviceType)){
            	String [] smsServiceTypesSenderNameArr=_smsServiceTypesSenderName.split(",");
            	int smsServiceTypeSenderNameSize = smsServiceTypesSenderNameArr.length;
            	boolean isFound = false;
            	for(int index=0;index<smsServiceTypeSenderNameSize;index++) {
            		String [] smsServiceTypeSenderName=smsServiceTypesSenderNameArr[index].split(":");
            		String [] smsServiceTypes=smsServiceTypeSenderName[0].split("-");
            		String smsServiceName = smsServiceTypeSenderName[1];
            		int smsServiceTypesSize = smsServiceTypes.length;
            		for(int index2=0;index2<smsServiceTypesSize;index2++){
	            		if(smsServiceTypes[index2].equalsIgnoreCase(_serviceType) && !BTSLUtil.isNullString(smsServiceName)){
	            			isFound = true;
	            			p_responseGatewayVO.setDestNo(smsServiceName);
	            			if (_log.isDebugEnabled()) {
	                            loggerValue.setLength(0);  
	                            loggerValue.append("SMS SENDER NAME BASED ON SERVICE TYPE FOUND, _serviceType=").append(_serviceType);
	                        	_log.debug(METHOD_NAME, loggerValue);
	                        }
	            			break;
	            		}
            		}
            		if(isFound){
            			isFound = false;
            			break;
            		}
            	}            	    			
            }
            //Ended Here
           	urlBuff.append("&from=" + p_responseGatewayVO.getDestNo());
            if (p_messageClass != null) {
                urlBuff.append("&mclass=" + p_messageClass);
            }
            if (p_pid != null) {
                urlBuff.append("&pid=" + p_pid);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Not able to get the message String Exception=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getPlainMessageURL", "Not able to get the message String=" + p_msisdn + " Creating Plain URL String Exception:" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);  
            loggerValue.append("Exiting for p_msisdn=").append(p_msisdn);
            loggerValue.append(" urlBuff=").append(urlBuff);
        	_log.debug(METHOD_NAME, loggerValue);
        }
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
        final String METHOD_NAME = "getBinaryMessageURL";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_ipaddress=").append(p_ipaddress);
        	loggerValue.append(" p_msisdn=").append(p_msisdn);
        	loggerValue.append(" p_message=").append(p_message);
        	loggerValue.append(" p_locale=").append(p_locale);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        StringBuffer urlBuff = null;
        String path = BTSLUtil.NullToString(p_responseGatewayVO.getPath());
        String loginID = BTSLUtil.NullToString(p_responseGatewayVO.getLoginID());
        String password = BTSLUtil.NullToString(p_responseGatewayVO.getDecryptedPassword());
        try {
            urlBuff = new StringBuffer(p_ipaddress + "/" + path);
            urlBuff.append("user=" + loginID);
            if (!BTSLUtil.isNullString(password) && "SHA".equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                password = URLEncoder.encode(password);
            }
            urlBuff.append("&pass=" + password);
            if (_operatorUtil == null) {
                String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

                try {
                    _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
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
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getBinaryMessageURL", "Not able to get the binary message URL String=" + p_msisdn + " Creating Binary URL String Exception:" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);  
             loggerValue.append("Exiting for p_msisdn=").append(p_msisdn);
             loggerValue.append(" urlBuff=").append(urlBuff);
         	_log.debug(METHOD_NAME, loggerValue);
        }
        return urlBuff;
    }// end getBinaryMessageURL

    public static void main(String[] args) {
        String[] arg = { "a", "b" };
       // PropertyConfigurator.configure("C:\\ECLIPSE_WORKSPACE\\pretups\\WebRoot\\WEB-INF\\classes\\configfiles\\LogConfig.props");
        ArrayList list = new ArrayList();
        Locale locale = new Locale("en");
        list.add(locale);
        MessagesCaches.load(list);
        BTSLMessages btslMessage = new BTSLMessages("4300", arg);
        new PushMessage("9810821454", btslMessage, "1", "33", locale, "DL").run();
    }// end main

    private String getResponse(String p_url, int p_timeout) throws BTSLBaseException {
        final String METHOD_NAME = "getResponse";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_url=").append(p_url);
        	loggerValue.append(" p_timeout=").append(p_timeout);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
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
                _log.errorTrace(METHOD_NAME, e);
            }
            String line = null;
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            long startTimeinMills = System.currentTimeMillis();
            strBuff = new StringBuffer();
            while (true) {
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
    	        	loggerValue.append("Inside While Loop Entered");
    	        	_log.debug(METHOD_NAME, loggerValue);
                }
                if (System.currentTimeMillis() - startTimeinMills > (p_timeout * 1000)) {
                    throw new BTSLBaseException(TIMEOUT);
                }
                line = in.readLine();
                if (line != null) {
                    strBuff.append(line);
                    if (_log.isDebugEnabled()) {
                        loggerValue.setLength(0);
        	        	loggerValue.append("line=").append(line);
        	        	_log.debug(METHOD_NAME, loggerValue);                        
                    }
                    break;
                } else {
                    throw new BTSLBaseException(FAILED);
                }
            }// end of while
             // MessageSentLog.logMessage(strBuff.toString());
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
	        	loggerValue.append("Exiting While Loop");
	        	_log.debug(METHOD_NAME, loggerValue);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append("Exception=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            // MessageSentLog.logMessage(msisdn+"   "+msgStr+"  "+ce.getMessage());
            throw new BTSLBaseException(FAILED);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            urlConnection = null;
            url = null;
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
	        	loggerValue.append("Exiting response str:").append((strBuff == null ? "" : strBuff.toString()));
	        	_log.debug(METHOD_NAME, loggerValue);
            }
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
        final String METHOD_NAME = "pushMessageWithStatus";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _requestCode=").append(_requestCode);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        try {
            if (p_gatewayCode != null && p_altGatewayCode != null) {
                _messageGatewayMappingCacheVO = new MessageGatewayMappingCacheVO();
                _messageGatewayMappingCacheVO.setResponseCode(p_gatewayCode);
                _messageGatewayMappingCacheVO.setAlternateCode(p_altGatewayCode);
            }
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("Picked default message gateway for");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }
            }
            if (_messageGatewayMappingCacheVO == null) {
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            }
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("after message cache for network");
                loggerValue.append(" _networkCode=").append(_networkCode);
            	loggerValue.append(" _requestCode=").append(_requestCode);
            	_log.debug(METHOD_NAME, loggerValue);
            }

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
                    loggerValue.append("after message cache not null for network");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
                    loggerValue.append("run after checking message key equal to null");
                    loggerValue.append(" _transactionID=").append(_transactionID);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	loggerValue.append(" _locale=").append(_locale);
                	loggerValue.append(" _locale language=").append((_locale == null ? "" : _locale.getLanguage()));
                	_log.debug(METHOD_NAME, loggerValue);
                }
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
                    if ("ar".equals(_localeMasterVO.getLanguage()) || "ru".equals(_localeMasterVO.getLanguage()) || "fa".equals(_locale.getLanguage())) {
                        endIndexForMessageCode = _message.indexOf("%00%3A");
                        if (endIndexForMessageCode != -1) {
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                        }
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1) {
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                        }
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if (("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage())) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled()) {
                        loggerValue.setLength(0);
                        loggerValue.append("run1");
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }
                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled()) {
                        loggerValue.setLength(0);
                        loggerValue.append("run2");
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }
                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }

                // encoding special for arabic
                // if(!(_locale.getLanguage().equals("en")||_locale.getLanguage().equals("fr")))
                // _message =BTSLUtil.encodeSpecial(_message);
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
                    loggerValue.append(" _transactionID=").append(_transactionID);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	_log.debug(METHOD_NAME, loggerValue);
                }
                _messageType = "PLAIN";
                status = sendSMSMessage(false);
                _entryDoneInLog = true;
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushMessageWithStatus]", _transactionID, _msisdn, "", "Gateway Code not found , Exception=" + PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "pushMessageWithStatus", PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            loggerValue.setLength(0);
        	loggerValue.append("_transactionID=").append(_transactionID);
        	loggerValue.append("Base Exception while sending message=").append(be.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage() + " Message code=" + _messageCode);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            loggerValue.setLength(0);
        	loggerValue.append("Exception while sending message=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
            }
            _log.errorTrace(METHOD_NAME, e);
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
        final String METHOD_NAME = "pushSmsUrlWithReceipt";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);
        	loggerValue.append(" p_useAlternate=").append(p_useAlternate);
        	loggerValue.append(" p_transferID=").append(p_transferID);
        	loggerValue.append(" p_gatewayCode=").append(p_gatewayCode);
        	loggerValue.append(" p_altGatewayCode=").append(p_altGatewayCode);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        boolean _entryDoneInLog = false;
        try {
            if (p_gatewayCode != null && p_altGatewayCode != null) {
                _messageGatewayMappingCacheVO = new MessageGatewayMappingCacheVO();
                _messageGatewayMappingCacheVO.setResponseCode(p_gatewayCode);
                _messageGatewayMappingCacheVO.setAlternateCode(p_altGatewayCode);
            }
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("Picked default message gateway for");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }
            }
            if (_messageGatewayMappingCacheVO == null) {
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            }
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("after message cache for network");
                loggerValue.append(" _networkCode=").append(_networkCode);
            	loggerValue.append(" _requestCode=").append(_requestCode);
            	_log.debug(METHOD_NAME, loggerValue);
            }

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("after message cache not null for network");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled()) {
                    loggerValue.append("run after checking message key equal to null");
                    loggerValue.append(" _transactionID=").append(_transactionID);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	loggerValue.append(" _locale=").append(_locale);
                	loggerValue.append(" _locale language=").append((_locale == null ? "" : _locale.getLanguage()));
                	_log.debug(METHOD_NAME, loggerValue);
                }
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
                        if (endIndexForMessageCode != -1) {
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                        }
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1) {
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                        }
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if (("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage())) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled()) {
                        loggerValue.setLength(0);
                        loggerValue.append("run1");
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }

                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                        loggerValue.append("run2");
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }

                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append(" _transactionID=").append(_transactionID);
                    loggerValue.append(" _message=").append(_message);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	_log.debug(METHOD_NAME, loggerValue);
                }
                status = sendSMSMessageWithReceipt(false, p_transferID);
                _entryDoneInLog = true;
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushSmsUrlWithReceipt]", _transactionID, _msisdn, "", "Gateway Code not found , Exception=" + PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
                throw new BTSLBaseException(this, "pushSmsUrlWithReceipt", PretupsErrorCodesI.REQ_RES_MAPPING_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            loggerValue.setLength(0);
        	loggerValue.append("Base Exception while sending message=").append(be.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + be.getMessage() + " Message code=" + _messageCode);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            loggerValue.setLength(0);
        	loggerValue.append("Exception while sending message=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
            }
            _log.errorTrace(METHOD_NAME, e);
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
    private String sendSMSMessageWithReceipt(boolean p_useAlternate, String p_transferID) throws BTSLBaseException {
        final String METHOD_NAME = "sendSMSMessageWithReceipt";
        final String ENTRY_KEY = "Entered";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" _msisdn=").append(_msisdn);
        	loggerValue.append(" _message=").append(_message);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _messageType=").append(_messageType);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        StringBuffer urlBuff = null;
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        ResponseGatewayVO responseGatewayVO = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            if (p_useAlternate) {
                messageGatewayVO = MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getAlternateCode());
            } else {
                messageGatewayVO = MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getResponseCode());
            }

            if (messageGatewayVO != null) {
                responseGatewayVO = messageGatewayVO.getResponseGatewayVO();
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);  
                	loggerValue.append("Using Gateway for _msisdn=").append(_msisdn);
                	loggerValue.append(" Details=").append(responseGatewayVO.toString());
                	_log.debug(METHOD_NAME, loggerValue);
                }
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
                    throw new BTSLBaseException(this, "sendSMSMessage", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
                } else if (!PretupsI.STATUS_ACTIVE.equals(responseGatewayVO.getStatus())) {
                    throw new BTSLBaseException(this, "sendSMSMessage", PretupsErrorCodesI.RES_MESSAGE_GATEWAY_NOT_ACTIVE);
                }
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
                    if (!BTSLUtil.isNullString(_tempMessage)) {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    } else {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    }
                } else {
                    // For USSD Push message String
                    urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage)) {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    } else {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    }
                }
            } else {
                if (p_useAlternate) {
                    throw new Exception("PushMessage[sendSMSMessage] Not able to send message for msisdn=" + _msisdn + " Transaction ID=" + _transactionID + " as no alternate gateway found");
                }
                throw new BTSLBaseException(this, "sendSMSMessage", PretupsErrorCodesI.NO_RES_MAPPING_FOUND_FORREQ);
            }
        } catch (BTSLBaseException be) {
            loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Exception=").append(be.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            if (messageGatewayVO != null && responseGatewayVO != null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage() + " Gateway code=" + responseGatewayVO.getGatewayCode() + " Gateway type=" + messageGatewayVO.getGatewayType());
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[pushGatewayMessage]", "", _msisdn, "", "Message Sending Exception:" + be.getMessage());
            }
            // Use the alternate
            // This condition is modified by ankit Zindal for prevent alternate
            // gateway check if alternate gateway code is null.
            if (!p_useAlternate && _messageGatewayMappingCacheVO != null && !BTSLUtil.isNullString(_messageGatewayMappingCacheVO.getAlternateCode())) {
                sendSMSMessageWithReceipt(true, p_transferID);
            } else {
                // Log in Message Sent Log
                if (messageGatewayVO != null && responseGatewayVO != null) {
                    MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                } else {
                    MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                }
                _entryDoneInLog = true;
                throw be;
            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Exception=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            // Log in Message Sent Log
            if (messageGatewayVO != null && responseGatewayVO != null) {
                MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            } else {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            }
            _entryDoneInLog = true;
            //throw e;
            throw new BTSLBaseException(e.getMessage());
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
        final String METHOD_NAME = "pushAlarm";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" _msisdn=").append(_msisdn);
        	loggerValue.append(" _requestCode=").append(_requestCode);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _messageKey=").append(_messageKey);
        	loggerValue.append(" _args=").append(_args);
        	loggerValue.append(" _locale=").append(_locale);
        	loggerValue.append(" _message=").append(_message);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        // Starting thread to send message
        _messageType = "PLAIN";

        try {
            if (BTSLUtil.isNullString(_requestCode)) {
                _requestCode = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY, _networkCode);
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("Picked default message gateway for");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }
            }
            if (_messageGatewayMappingCacheVO == null) {
                _messageGatewayMappingCacheVO = (MessageGatewayMappingCacheVO) MessageGatewayCache.getMappingObject(_requestCode);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
                loggerValue.append("after message cache for network");
                loggerValue.append(" _networkCode=").append(_networkCode);
            	loggerValue.append(" _requestCode=").append(_requestCode);
            	_log.debug(METHOD_NAME, loggerValue);
            }

            if (_messageGatewayMappingCacheVO != null) {
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("after message cache not null for network");
                    loggerValue.append(" _networkCode=").append(_networkCode);
                	loggerValue.append(" _requestCode=").append(_requestCode);
                	_log.debug(METHOD_NAME, loggerValue);
                }

                if (_messageKey != null) {
                    _message = BTSLUtil.getMessage(_locale, _messageKey, _args);
                }
                if (_log.isDebugEnabled()) {
                	loggerValue.append("run after checking message key equal to null");
                    loggerValue.append(" _transactionID=").append(_transactionID);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	loggerValue.append(" _locale=").append(_locale);
                	loggerValue.append(" _locale language=").append((_locale == null ? "" : _locale.getLanguage()));
                	_log.debug(METHOD_NAME, loggerValue);
                }
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
                    if ("ar".equals(_localeMasterVO.getLanguage()) || "ru".equals(_localeMasterVO.getLanguage()) || "fa".equals(_locale.getLanguage())) {
                        endIndexForMessageCode = _message.indexOf("%00%3A");
                        if (endIndexForMessageCode != -1) {
                            _messageCode = URLDecoder.decode(_message.substring(0, endIndexForMessageCode), "UTF16");
                        }
                    } else {
                        endIndexForMessageCode = _message.indexOf(":");
                        if (endIndexForMessageCode != -1) {
                            _messageCode = _message.substring(0, endIndexForMessageCode);
                        }
                    }
                }
                // ChangeID=LOCALEMASTER
                // Message will be encoded by the encoding scheme defined in the
                // locale master tabel for the requested locale.
                if (("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage())) && !_message.startsWith("%")) {
                    if (_log.isDebugEnabled()) {
                        loggerValue.setLength(0);
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }

                    _message = BTSLUtil.encodeSpecial(_message, true, _localeMasterVO.getEncoding());
                } else if (!("ar".equals(_locale.getLanguage()) || "ru".equals(_locale.getLanguage()) || "fa".equals(_locale.getLanguage()))) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                        loggerValue.append(" _transactionID=").append(_transactionID);
                    	loggerValue.append(" _messageKey=").append(_messageKey);
                    	loggerValue.append(" _messageClass=").append(_messageClass);
                    	loggerValue.append(" _pid=").append(_pid);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }

                    _message = URLEncoder.encode(_message, _localeMasterVO.getEncoding());
                }

                // encoding special for arabic
                // if(!(_locale.getLanguage().equals("en")||_locale.getLanguage().equals("fr")))
                // _message =BTSLUtil.encodeSpecial(_message);
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append(" _transactionID=").append(_transactionID);
                	loggerValue.append(" _messageKey=").append(_messageKey);
                	loggerValue.append(" _messageClass=").append(_messageClass);
                	loggerValue.append(" _pid=").append(_pid);
                	_log.debug(METHOD_NAME, loggerValue);
                }
                sendAlarmMessage(false);
                _entryDoneInLog = true;
            } else {
            	if (_log.isDebugEnabled()) {
	                loggerValue.setLength(0);
	            	loggerValue.append("Message Gateway Code not found");
	            	_log.debug(METHOD_NAME, loggerValue);
            	}
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Message Gateway Code not found " + " Message code=" + _messageCode);
                _entryDoneInLog = true;
            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception while sending message=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            if (!_entryDoneInLog) {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, PretupsI.GATEWAY_MESSAGE_FAILED, "", "Not able to send Message , getting Exception=" + e.getMessage() + " Message code=" + _messageCode);
            }
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
    private String sendAlarmMessage(boolean p_useAlternate) throws BTSLBaseException {
        final String METHOD_NAME = "sendAlarmMessage";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" _msisdn=").append(_msisdn);
        	loggerValue.append(" _requestCode=").append(_requestCode);
        	loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" _messageType=").append(_messageType);
        	loggerValue.append(" _message=").append(_message);
        	loggerValue.append(" p_useAlternate=").append(p_useAlternate);
        	_log.debug(METHOD_NAME, loggerValue);
    	}
        StringBuffer urlBuff = null;
        String status = PretupsI.GATEWAY_MESSAGE_FAILED;
        ResponseGatewayVO responseGatewayVO = null;
        MessageGatewayVO messageGatewayVO = null;
        try {
            if (p_useAlternate) {
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getAlternateCode());
            } else {
                messageGatewayVO = (MessageGatewayVO) MessageGatewayCache.getObject(_messageGatewayMappingCacheVO.getResponseCode());
            }

            if (messageGatewayVO != null) {
                responseGatewayVO = messageGatewayVO.getResponseGatewayVO();
                if (_log.isDebugEnabled()) {
                    loggerValue.setLength(0);
    	        	loggerValue.append("Using Gateway for _msisdn=").append(_msisdn);
    	        	loggerValue.append(" Details=").append(responseGatewayVO.toString());
    	        	_log.debug(METHOD_NAME, loggerValue);
                }
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
                    throw new BTSLBaseException(this, "sendAlarmMessage", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
                } else if (!PretupsI.STATUS_ACTIVE.equals(responseGatewayVO.getStatus())) {
                    throw new BTSLBaseException(this, "sendAlarmMessage", PretupsErrorCodesI.RES_MESSAGE_GATEWAY_NOT_ACTIVE);
                }

                String protocol = messageGatewayVO.getProtocol();
                String ip = messageGatewayVO.getHost();
                String port = BTSLUtil.NullToString(responseGatewayVO.getPort());

                /*
                 * if(messageGatewayVO.getGatewayType().equals(PretupsI.
                 * GATEWAY_TYPE_SMSC))
                 * {
                 * if(_messageType.equalsIgnoreCase("PLAIN"))
                 * urlBuff=getPlainMessageURL(responseGatewayVO,protocol+"://"+ip
                 * +":"+port,_msisdn,_locale,_message,_messageClass,_pid);
                 * else
                 * urlBuff=getBinaryMessageURL(responseGatewayVO,protocol+"://"+ip
                 * +":"+port,_msisdn,_locale,_message);
                 * 
                 * status=pushGatewayMessage(urlBuff.toString(),responseGatewayVO
                 * .getTimeOut());
                 * //Log in Message Sent Log
                 * if(!BTSLUtil.isNullString(_tempMessage))
                 * MessageSentLog.log(_msisdn,_locale,messageGatewayVO.
                 * getGatewayType
                 * (),responseGatewayVO.getGatewayCode(),"",status,
                 * _tempMessage,"Message Class="
                 * +_messageClass+" PID="+_pid+" Message code="+_messageCode);
                 * else
                 * MessageSentLog.log(_msisdn,_locale,messageGatewayVO.
                 * getGatewayType
                 * (),responseGatewayVO.getGatewayCode(),"",status,
                 * urlBuff.toString
                 * (),"Message Class="+_messageClass+" PID="+_pid
                 * +" Message code="+_messageCode);
                 * }
                 * else
                 */
                {
                    // For USSD Push message String
                    if ("PLAIN".equalsIgnoreCase(_messageType)) {
                        urlBuff = getPlainMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message, _messageClass, _pid);
                    } else {
                        urlBuff = getBinaryMessageURL(responseGatewayVO, protocol + "://" + ip + ":" + port, _msisdn, _locale, _message);
                    }

                    status = pushGatewayMessage(urlBuff.toString(), responseGatewayVO.getTimeOut());
                    // Log in Message Sent Log
                    if (!BTSLUtil.isNullString(_tempMessage)) {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, _tempMessage, "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    } else {
                        MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), "", status, urlBuff.toString(), "Message Class=" + _messageClass + " PID=" + _pid + " Message code=" + _messageCode);
                    }
                }
            } else {
                if (p_useAlternate) {
                    throw new Exception("PushMessage[sendAlarmMessage] Not able to send message for msisdn=" + _msisdn + " Transaction ID=" + _transactionID + " as no alternate gateway found");
                } else {
                    throw new BTSLBaseException(this, "sendSMSMessage", PretupsErrorCodesI.NO_RES_MAPPING_FOUND_FORREQ);
                }
            }
        } catch (BTSLBaseException be) {
            loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Exception=").append(be.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
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
            if (!p_useAlternate && _messageGatewayMappingCacheVO != null && !BTSLUtil.isNullString(_messageGatewayMappingCacheVO.getAlternateCode())) {
                sendAlarmMessage(true);
            } else {
                // Log in Message Sent Log
                if (messageGatewayVO != null && responseGatewayVO != null) {
                    MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                } else {
                    MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
                }
                _entryDoneInLog = true;
                throw be;
            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
            loggerValue.append(" _transactionID=").append(_transactionID);
        	loggerValue.append(" Exception=").append(e.getMessage());
        	_log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            // Log in Message Sent Log
            if (messageGatewayVO != null && responseGatewayVO != null) {
                MessageSentLog.log(_msisdn, _locale, messageGatewayVO.getGatewayType(), responseGatewayVO.getGatewayCode(), _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            } else {
                MessageSentLog.log(_msisdn, _locale, "", "", _message, status, "", "Not able to send message " + " Message code=" + _messageCode);
            }
            _entryDoneInLog = true;
            //throw e;
            throw new BTSLBaseException(e.getMessage());
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
    	final String methodName="PushMessage[pushMessage] at line 1799";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_msisdn=").append(p_msisdn);
        	loggerValue.append(" p_transactionID=").append(p_transactionID);
        	loggerValue.append(" p_requestCode=").append(p_requestCode);
        	loggerValue.append(" p_locale=").append(p_locale);
        	loggerValue.append(" p_networkCode=").append(p_networkCode);
        	loggerValue.append(" p_tempMessage=").append(p_tempMessage);
        	_log.debug(methodName, loggerValue);
    	}
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
    
    /**
     * @description PushMessage Constructor (This constructor will be used for pushing SMS message in WEB and SMS Interface) 
     * @param p_msisdn
     * @param p_message
    * @param p_transactionID
     * @param p_requestGatewayCode
     * @param p_locale
     * @param p_networkCode
     * @param p_tempMessage
     * @param p_serviceType
     */
    public PushMessage(String p_msisdn, String p_message, String p_transactionID, String p_requestGatewayCode, Locale p_locale, String p_networkCode, String p_tempMessage, String p_serviceType) {
        final String methodName="PushMessage[pushMessage] at line 1838";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_msisdn=").append(p_msisdn);
        	loggerValue.append(" p_message=").append(p_message);
        	loggerValue.append(" p_transactionID=").append(p_transactionID);
        	loggerValue.append(" p_requestGatewayCode=").append(p_requestGatewayCode);
        	loggerValue.append(" p_locale=").append(p_locale);
        	loggerValue.append(" p_networkCode=").append(p_networkCode);
        	loggerValue.append(" p_tempMessage=").append(p_tempMessage);
        	loggerValue.append(" p_serviceType=").append(p_serviceType);
        	_log.debug(methodName, loggerValue);
    	}
        _msisdn = p_msisdn;
        _message = p_message;        
        _transactionID = p_transactionID;        
        _requestCode = p_requestGatewayCode;
        _locale = p_locale;        
        _localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
        _networkCode = p_networkCode;
        _tempMessage = p_tempMessage;
        _serviceType=p_serviceType;
    }// end of PushMessage

    /**
     * @description PushMessage Constructor (This constructor will be used for pushing SMS message in WEB and SMS Interface) 
     * @param p_msisdn
     * @param btslMessages
     * @param p_transactionID
     * @param p_requestGatewayCode
     * @param p_locale
     * @param p_networkCode
     * @param p_tempMessage
     * @param p_serviceType
     */
    public PushMessage(String p_msisdn, BTSLMessages btslMessages, String p_transactionID, String p_requestGatewayCode, Locale p_locale, String p_networkCode, String p_tempMessage, String p_serviceType) {
        final String methodName="PushMessage[pushMessage] at line 1878";
        final String ENTRY_KEY = "Entered :";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);  
        	loggerValue.append(" p_msisdn=").append(p_msisdn);
        	loggerValue.append(" p_requestGatewayCode=").append(p_requestGatewayCode);
        	loggerValue.append(" p_transactionID=").append(p_transactionID);
        	loggerValue.append(" p_tempMessage=").append(p_tempMessage);
        	loggerValue.append(" p_locale=").append(p_locale);
        	loggerValue.append(" p_networkCode=").append(p_networkCode);
        	loggerValue.append(" p_serviceType=").append(p_serviceType);
        	_log.debug(methodName, loggerValue);
    	}
        _msisdn = p_msisdn;
        _messageKey = btslMessages.getMessageKey();
        _args = btslMessages.getArgs();
        _transactionID = p_transactionID;        
        _requestCode = p_requestGatewayCode;
        _locale = p_locale;        
        _localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
        _networkCode = p_networkCode;
        _tempMessage = p_tempMessage;
        _serviceType=p_serviceType;
    }// end of PushMessage
}
