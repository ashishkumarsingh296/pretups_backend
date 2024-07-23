package com.btsl.pretups.gateway.util;

/*
 * @(#)WMLStringParser.java
 * Copyright(c) 2007, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * vikas yadav Jan 04, 2007 Initital Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Response generation class to handle WML requests
 */

import java.util.Locale;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLUtil;

public class WMLStringParser {
    public static final Log _log = LogFactory.getLog(WMLStringParser.class.getName());

    /**
     * generateChannelResponse is used to generate the generic response.
     * 
     * @param p_requestVO
     *            RequestVO
     */
    private static final String _STATIC_WML_HEADER = "<?xml version=\"1.0\"encoding=\"ISO-8859-1\"?>";
    private static final String _TONE_STR = "";

    /**
	 * ensures no instantiation
	 */
    private WMLStringParser(){
    	
    }
    
    public static void generateChannelResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateChannelResponse", "Entered p_requestVO: ");
        }
        final String METHOD_NAME = "generateChannelResponse";
        String wml = null;
        final String[] arg = null;
        try {
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            if (p_requestVO.isSuccessTxn()) {
                wml = BTSLUtil.getMessage(locale, PretupsErrorCodesI.WML_SUCCESS_RESPONSE_DP6, arg);
            } else if (!BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                wml = getMessage(locale, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            } else {
                wml = BTSLUtil.getMessage(locale, PretupsErrorCodesI.WML_SUCCESS_RESPONSE_DP6, arg);
            }
            wml = _STATIC_WML_HEADER + "<wml><card id=\"Main\"><p>" + wml + "</p></card></wml>";
            final int indx = wml.indexOf("<card>");
            final int gretindx = wml.indexOf(">", indx);
            final StringBuffer strBuff = new StringBuffer(wml);
            strBuff.insert(gretindx + 1, _TONE_STR);
            wml = strBuff.toString();
            p_requestVO.setSenderReturnMessage(wml);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateChannelResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WMLStringParser[generateChannelResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateChannelCreditTransferResponse", "Exiting responseStr: " + wml);
            }
        }

    }

    /**
     * generateFailureResponse is used to generate the failure response.
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_con
     *            Connection
     */

    public static void generateFailureResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateFailureResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateFailureResponse";
        String wml = null;
        final String[] arg = null;
        try {
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            wml = BTSLUtil.getMessage(locale, PretupsErrorCodesI.WML_FAILURE_RESPONSE_DP6, arg);
            wml = _STATIC_WML_HEADER + "<wml><card id=\"Main\"><p>" + wml + "</p></card></wml>";
            final int indx = wml.indexOf("<card>");
            final int gretindx = wml.indexOf(">", indx);
            final StringBuffer strBuff = new StringBuffer(wml);
            strBuff.insert(gretindx + 1, _TONE_STR);
            wml = strBuff.toString();
            p_requestVO.setSenderReturnMessage(wml);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateFailureResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WMLStringParser[generateFailureResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateFailureResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateFailureResponse", "Exiting wml: " + wml);
            }
        }
    }

    private static String getMessage(Locale locale, String key, String[] p_args) {
        String message = BTSLUtil.getMessage(locale, key, p_args);
        final String METHOD_NAME = "getMessage";
        try {
            final LocaleMasterVO localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            if (message.indexOf("mclass^") == 0) {
                final int colonIndex = message.indexOf(":");
                final String messageClassPID = message.substring(0, colonIndex);
                final String[] messageClassPIDArray = messageClassPID.split("&");
                message = message.substring(colonIndex + 1);
                int endIndexForMessageCode;
                if ("ar".equals(localeMasterVO.getLanguage())) {
                    endIndexForMessageCode = message.indexOf("%00%3A");
                    if (endIndexForMessageCode != -1) {
                        message = message.substring(endIndexForMessageCode + 1);
                    }
                } else {
                    endIndexForMessageCode = message.indexOf(":");
                    if (endIndexForMessageCode != -1) {
                        message = message.substring(endIndexForMessageCode + 1);
                    }
                }
                if ("ar".equals(locale.getLanguage()) && !message.startsWith("%")) {
                    message = BTSLUtil.encodeSpecial(message, true, localeMasterVO.getEncoding());
                    /*
                     * else if(!"ar".equals(locale.getLanguage()))
                     * message=URLEncoder.encode(message,localeMasterVO.getEncoding
                     * ()
                     * );
                     */// commented because it will show + sign between words.
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getMessage", "Exception e: " + e);
        }
        return message;
    }
}
