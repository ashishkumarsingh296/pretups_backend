package com.btsl.pretups.processes;

/**
 * @(#)AutoProcessBL.java
 *                        Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        BL Class for Process and other activities.
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Gurjeet Singh Bedi 10/08/07 Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerDAO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.BTSLUtil;

public class AutoProcessBL {
    private static Log _log = LogFactory.getLog(AutoProcessBL.class.getName());

    /**
     * Method responsible for updating cache for instances
     * 
     * @param p_con
     * @param p_serviceType
     * @param p_locale
     * @return
     */
    public String updateCache(Connection p_con, String p_serviceType, String p_service, Locale p_locale) {
        final String METHOD_NAME = "updateCache";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entering with _serviceType = " + p_serviceType + " p_service = " + p_service + " p_locale=" + p_locale);
        }
        String retMessage = "";
        final String cacheParam = "cacheParam";
        StringBuffer urlString = new StringBuffer();
        final LoadControllerDAO controlDAO = new LoadControllerDAO();
        InstanceLoadVO instanceLoadVO = null;
        String ip = null;
        String port = null;
        String successInstances = "";
        String failedInstances = "";
        try {
            final ArrayList instanceList = controlDAO.loadInstanceLoadDetails(p_con);
            urlString = new StringBuffer();
            for (int i = 0, k = instanceList.size(); i < k; i++) {
                instanceLoadVO = (InstanceLoadVO) instanceList.get(i);
                try {
                	urlString.setLength(0);
                    ip = instanceLoadVO.getHostAddress();
                    port = instanceLoadVO.getHostPort();
                    urlString.append("http://");
                    urlString.append(ip);
                    urlString.append(":");
                    urlString.append(port);
                    urlString.append("/pretups/UpdateCacheServlet?");
                    urlString.append(cacheParam);
                    urlString.append("=");
                    urlString.append(p_serviceType);

                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, " For Instance ID=" + instanceLoadVO.getInstanceID() + " URL=" + urlString.toString());
                    }
                    try {
                        hitInstance(urlString.toString());
                        successInstances = successInstances + instanceLoadVO.getInstanceID() + ",";
                        _log.info(METHOD_NAME,
                            "For " + p_service + " service, successful cache update for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "",
                            "For " + p_service + " service, Successful cache update for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                    } catch (BTSLBaseException be) {
                        // If in first try cache is not updated (Exception is
                        // thrown by hitWebInstance method),
                        // we have to Try once again to update cache.So again
                        // calling hitWebInstance.
                        // If it also fails then process will throw exception .
                        _log.error(
                            METHOD_NAME,
                            "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Trying Retry........");
                        _log.errorTrace(METHOD_NAME, be);
                        EventHandler
                            .handle(
                                EventIDI.SYSTEM_ERROR,
                                EventComponentI.SYSTEM,
                                EventStatusI.RAISED,
                                EventLevelI.FATAL,
                                "AutoProcessBL[updateCache]",
                                "",
                                "",
                                "",
                                "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Trying Retry........");
                        try {
                            hitInstance(urlString.toString());
                            successInstances = successInstances + instanceLoadVO.getInstanceID() + ",";
                            _log.info(
                                METHOD_NAME,
                                "For " + p_service + " service, successful cache update after retry for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                            EventHandler
                                .handle(
                                    EventIDI.SYSTEM_ERROR,
                                    EventComponentI.SYSTEM,
                                    EventStatusI.RAISED,
                                    EventLevelI.FATAL,
                                    "AutoProcessBL[updateCache]",
                                    "",
                                    "",
                                    "",
                                    "For " + p_service + " service, Successful cache update after retry for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                        } catch (Exception ex) {
                        	throw new BTSLBaseException(this, METHOD_NAME, "Exception in updating cache for instances",ex);
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error(
                            METHOD_NAME,
                            "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Not Trying Retry........");
                        failedInstances = failedInstances + instanceLoadVO.getInstanceID() + ",";
                        EventHandler
                            .handle(
                                EventIDI.SYSTEM_ERROR,
                                EventComponentI.SYSTEM,
                                EventStatusI.RAISED,
                                EventLevelI.FATAL,
                                "AutoProcessBL[updateCache]",
                                "",
                                "",
                                "",
                                "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Exception:" + e
                                    .getMessage());
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error(METHOD_NAME,
                        "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                    failedInstances = failedInstances + instanceLoadVO.getInstanceID() + ",";
                    EventHandler
                        .handle(
                            EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM,
                            EventStatusI.RAISED,
                            EventLevelI.FATAL,
                            "AutoProcessBL[updateCache]",
                            "",
                            "",
                            "",
                            "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Exception:" + e
                                .getMessage());
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            retMessage = BTSLUtil.getMessage(p_locale, PretupsErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { p_service });
            _log.error(METHOD_NAME, "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "",
                "For " + p_service + " service, Error while Updating Cache and got Exception:" + e.getMessage());
        }
        if (!BTSLUtil.isNullString(retMessage) && retMessage.trim().length() > 0) {
            return retMessage;
        }
        if (BTSLUtil.isNullString(successInstances) && BTSLUtil.isNullString(failedInstances)) {
            retMessage = BTSLUtil.getMessage(p_locale, PretupsErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { p_service });
        }
        if (!BTSLUtil.isNullString(successInstances) && successInstances.trim().length() > 0 && successInstances.endsWith(",")) {
            successInstances = successInstances.substring(0, successInstances.length() - 1);
        } else {
            successInstances = "-";
        }
        if (!BTSLUtil.isNullString(failedInstances) && failedInstances.trim().length() > 0 && failedInstances.endsWith(",")) {
            failedInstances = failedInstances.substring(0, failedInstances.length() - 1);
        } else {
            failedInstances = "-";
        }
        retMessage = BTSLUtil.getMessage(p_locale, PretupsErrorCodesI.AUTO_CACHEUPDATE_STATUS, new String[] { p_service, successInstances, failedInstances });
        return retMessage;
    }

    /**
     * Method to hit the instance with the URL for updating cache
     * 
     * @param p_url
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void hitInstance(String p_url) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "hitInstance";
        if (_log.isDebugEnabled()) {
            _log.debug("hitInstance", " Entering with hitInstance URL = " + p_url);
        }
        HttpURLConnection con = null;
        BufferedReader br = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("hitInstance", "Entered, URL STING= " + p_url);
            }
            final URL url = new URL(p_url);
            final URLConnection uc = url.openConnection();
            con = (HttpURLConnection) uc;
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String str = null;
            final StringBuffer message = new StringBuffer();
            while ((str = br.readLine()) != null) {
                message.append(str);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("hitInstance", "Response received from UpdateCacheServlet =" + message.toString());
            }
            if (message.toString().contains("not been updated succssfully")) {
                throw new BTSLBaseException("AutoProcessBL", "hitInstance", PretupsErrorCodesI.PROCESS_RESUMESUSPEND_CACHE_NOT_UPDATED);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("hitInstance", " Exit after successful execution of hitInstance");
            }
        } catch (BTSLBaseException bte) {
            _log.error("hitInstance", "Cache not updated." + bte.getMessage());
            if (_log.isDebugEnabled()) {
                _log.debug("hitInstance", " Exit after unsuccessful execution of hitInstance");
            }
            throw bte;
        } catch (Exception e) {
            _log.error("hitInstance", " Fail to connect the instance ");
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
