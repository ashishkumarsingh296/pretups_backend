package com.selftopup.pretups.processes;

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
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.loadcontroller.InstanceLoadVO;
import com.selftopup.loadcontroller.LoadControllerDAO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.util.BTSLUtil;

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
        if (_log.isDebugEnabled())
            _log.debug("updateCache", " Entering with _serviceType = " + p_serviceType + " p_service = " + p_service + " p_locale=" + p_locale);
        String retMessage = "";
        String cacheParam = "cacheParam";
        StringBuffer urlString = new StringBuffer();
        LoadControllerDAO controlDAO = new LoadControllerDAO();
        InstanceLoadVO instanceLoadVO = null;
        String ip = null;
        String port = null;
        String successInstances = "";
        String failedInstances = "";
        try {
            ArrayList instanceList = controlDAO.loadInstanceLoadDetails(p_con);
            for (int i = 0, k = instanceList.size(); i < k; i++) {
                instanceLoadVO = (InstanceLoadVO) instanceList.get(i);
                try {
                    ip = instanceLoadVO.getHostAddress();
                    port = instanceLoadVO.getHostPort();
                    urlString = new StringBuffer();
                    urlString.append("http://");
                    urlString.append(ip);
                    urlString.append(":");
                    urlString.append(port);
                    urlString.append("/pretups/UpdateCacheServlet?");
                    urlString.append(cacheParam);
                    urlString.append("=");
                    urlString.append(p_serviceType);

                    if (_log.isDebugEnabled())
                        _log.debug("updateCache", " For Instance ID=" + instanceLoadVO.getInstanceID() + " URL=" + urlString.toString());
                    try {
                        hitInstance(urlString.toString());
                        successInstances = successInstances + instanceLoadVO.getInstanceID() + ",";
                        _log.info("updateCache", "For " + p_service + " service, successful cache update for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "", "For " + p_service + " service, Successful cache update for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                    } catch (BTSLBaseException be) {
                        // If in first try cache is not updated (Exception is
                        // thrown by hitWebInstance method),
                        // we have to Try once again to update cache.So again
                        // calling hitWebInstance.
                        // If it also fails then process will throw exception .
                        _log.error("updateCache", "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Trying Retry........");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "", "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Trying Retry........");
                        try {
                            hitInstance(urlString.toString());
                            successInstances = successInstances + instanceLoadVO.getInstanceID() + ",";
                            _log.info("updateCache", "For " + p_service + " service, successful cache update after retry for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "", "For " + p_service + " service, Successful cache update after retry for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                        } catch (Exception ex) {
                            throw ex;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        _log.error("updateCache", "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Not Trying Retry........");
                        failedInstances = failedInstances + instanceLoadVO.getInstanceID() + ",";
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "", "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Exception:" + e.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("updateCache", "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port);
                    failedInstances = failedInstances + instanceLoadVO.getInstanceID() + ",";
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "", "For " + p_service + " service, Error while Updating Cache for Instance=" + instanceLoadVO.getInstanceID() + " IP=" + ip + " port=" + port + " Exception:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            retMessage = BTSLUtil.getMessage(p_locale, SelfTopUpErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { p_service });
            _log.error("updateCache", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoProcessBL[updateCache]", "", "", "", "For " + p_service + " service, Error while Updating Cache and got Exception:" + e.getMessage());
        }
        if (!BTSLUtil.isNullString(retMessage) && retMessage.trim().length() > 0)
            return retMessage;
        if (BTSLUtil.isNullString(successInstances) && BTSLUtil.isNullString(failedInstances))
            retMessage = BTSLUtil.getMessage(p_locale, SelfTopUpErrorCodesI.AUTO_CACHEUPDATE_FAIL, new String[] { p_service });
        if (!BTSLUtil.isNullString(successInstances) && successInstances.trim().length() > 0 && successInstances.endsWith(","))
            successInstances = successInstances.substring(0, successInstances.length() - 1);
        else
            successInstances = "-";
        if (!BTSLUtil.isNullString(failedInstances) && failedInstances.trim().length() > 0 && failedInstances.endsWith(","))
            failedInstances = failedInstances.substring(0, failedInstances.length() - 1);
        else
            failedInstances = "-";
        retMessage = BTSLUtil.getMessage(p_locale, SelfTopUpErrorCodesI.AUTO_CACHEUPDATE_STATUS, new String[] { p_service, successInstances, failedInstances });
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
        if (_log.isDebugEnabled())
            _log.debug("hitInstance", " Entering with hitInstance URL = " + p_url);
        HttpURLConnection con = null;
        BufferedReader br = null;
        try {
            if (_log.isDebugEnabled())
                _log.debug("hitInstance", "Entered, URL STING= " + p_url);
            URL url = new URL(p_url);
            URLConnection uc = url.openConnection();
            con = (HttpURLConnection) uc;
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String str = null;
            StringBuffer message = new StringBuffer();
            while ((str = br.readLine()) != null) {
                message.append(str);
            }
            if (_log.isDebugEnabled())
                _log.debug("hitInstance", "Response received from UpdateCacheServlet =" + message.toString());
            if (message.toString().contains("not been updated succssfully")) {
                throw new BTSLBaseException("AutoProcessBL", "hitInstance", SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_CACHE_NOT_UPDATED);
            }
            if (_log.isDebugEnabled())
                _log.debug("hitInstance", " Exit after successful execution of hitInstance");
        } catch (BTSLBaseException bte) {
            _log.error("hitInstance", "Cache not updated." + bte.getMessage());
            if (_log.isDebugEnabled())
                _log.debug("hitInstance", " Exit after unsuccessful execution of hitInstance");
            throw bte;
        } catch (Exception e) {
            _log.error("hitInstance", " Fail to connect the instance ");
            throw new BTSLBaseException(SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
