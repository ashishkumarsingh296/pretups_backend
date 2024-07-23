package com.btsl.pretups.processes;

/**
 * @(#)SendSMSToChannelAdmin4HourlyTrans.java
 *                                            Copyright(c) 2014, Comviva
 *                                            technologies Ltd.
 *                                            All Rights Reserved
 * 
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -------------------------
 *                                            Author Date History
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -------------------------
 *                                            Diwakar Jan 13 2014 Initial
 *                                            Creation
 *                                            This class will be used to start
 *                                            sending the SMS to Channel/Area
 *                                            Admin users and it will be
 *                                            configured at cron end that should
 *                                            be scheduled to execute on hourly
 *                                            wise.
 * 
 */
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.businesslogic.SendSMSToChannelAdmin4HourlyTransDAO;
import com.btsl.pretups.processes.businesslogic.SendSMSToChannelAdmin4HourlyTransVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class SendSMSToChannelAdmin4HourlyTrans {

    private static Log _logger = LogFactory.getLog(SendSMSToChannelAdmin4HourlyTrans.class.getName());
    private static Locale _locale = null;
    private static String _reportDateHour = null;
    private static String _fromDateHour = null;
    private static int _reportDateHour1 = 0;
    private static int _fromDateHour1 = 0;
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;

    private SendSMSToChannelAdmin4HourlyTransDAO sendSMSToChannelAdmin4HourlyTransDAO = null;

    public SendSMSToChannelAdmin4HourlyTrans() {
        sendSMSToChannelAdmin4HourlyTransDAO = new SendSMSToChannelAdmin4HourlyTransDAO();
    }

    /**
     * @param args
     *            arg[0]=Conatnsts.props
     *            arg[1]=ProcessLogconfig.props
     *            arg[2]=Locale(0 or 1)
     */
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length > 3 || arg.length < 3)// check the argument length
            {
                _logger.info(METHOD_NAME,
                    "SendSMSToChannelAdmin4HourlyTrans :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props Locale Network ReportDate");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                _logger.debug(METHOD_NAME, "SendSMSToChannelAdmin4HourlyTrans" + " Constants File Not Found at the path : " + arg[0]);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug(METHOD_NAME, "SendSMSToChannelAdmin4HourlyTrans" + " ProcessLogConfig File Not Found at the path : " + arg[1]);
                return;
            }
            if (BTSLUtil.isNullString(arg[2]))// Locale check
            {
                _logger.info(METHOD_NAME, "SendSMSToChannelAdmin4HourlyTrans :: Locale is missing ");
                return;
            }
            if (!BTSLUtil.isNumeric(arg[2]))// check the Process Interval is
            // numeric
            {
                _logger.debug(METHOD_NAME, "SendSMSToChannelAdmin4HourlyTrans :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (Integer.parseInt(arg[2]) > 1 && Integer.parseInt(arg[2]) < 0) {
                _logger.debug(METHOD_NAME, "SendSMSToChannelAdmin4HourlyTrans :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            // _reportDate=new Date();
            // _reportDate=BTSLUtil.addDaysInUtilDate(_reportDate,-1);
            // GregorianCalendar gc = new
            // GregorianCalendar(_reportDate.getYear()+1900,_reportDate.getMonth(),1);
            // _fromDate = gc.getTime();

            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SendSMSToChannelAdmin4HourlyTrans[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            _locale = LocaleMasterCache.getLocaleFromCodeDetails(arg[2]);
            if (_locale == null) {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Error : Invalid Locale " + arg[2] + " It should be 0 or 1, thus using default locale code 0");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "SendSMSToChannelAdmin4HourlyTrans[main]", "", "",
                    "", "  Message:  Invalid Locale " + arg[2] + " It should be 0 (EN) or 1 (OTH) ");
            }
        } catch (Exception e) {
            _logger.error(METHOD_NAME, " Invalid locale : " + arg[5] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "SendSMSToChannelAdmin4HourlyTrans[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }
        String processId = null;
        boolean statusOk = false;
        Connection conProcessStatus = null;
        try {
            processId = ProcessI.SMS_TO_CHANNEL_ADMIN_USERS_HOURLY;
            // Make Connection
            conProcessStatus = OracleUtil.getSingleConnection();
            if (conProcessStatus == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("SendSMSToChannelAdmin4HourlyTrans[main]", "Not able to get Connection for SendSMSToChannelAdmin4HourlyTrans: ");
                }
                throw new SQLException();
            }
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(conProcessStatus, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                conProcessStatus.commit();
            }
            calculatePreviousHour();
            final SendSMSToChannelAdmin4HourlyTrans sendSMSToChannelAdmin4HourlyTrans = new SendSMSToChannelAdmin4HourlyTrans();
            sendSMSToChannelAdmin4HourlyTrans.sendSMS();
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SendSMSToChannelAdmin4HourlyTrans[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception :" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SendSMSToChannelAdmin4HourlyTrans[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(conProcessStatus, processId) == 1) {
                        try {
                            conProcessStatus.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            conProcessStatus.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                }

            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("SendSMSToChannelAdmin4HourlyTrans", "Exception while closing statement in SendSMSToChannelAdmin4HourlyTrans[main] method ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * @descriptions : This method will be used to calculate the previous hour
     *               duration from the current time.
     * @author : diwakar
     * @throws ParseException
     */
    private static void calculatePreviousHour() throws ParseException {
        final String METHOD_NAME = "calculatePreviousHour";
        try {
            Calendar now = BTSLDateUtil.getInstance();
            now = BTSLDateUtil.getInstance();
            now.add(Calendar.MONTH, 1);
            final String todayDate = BTSLUtil.getDateTimeStringFromDate(now.getTime(), "yyyy-MM-dd");
            now.add(Calendar.HOUR, -1);
            final int fromHour = now.get(Calendar.HOUR_OF_DAY);
            String sFromHour;
            String sToHour;
            if (fromHour < 10) {
                sFromHour = todayDate + " 0" + fromHour + ":00:00";
                sToHour = todayDate + " 0" + fromHour + ":59:59";
            } else {
                sFromHour = todayDate + " " + fromHour + ":00:00";
                sToHour = todayDate + " " + fromHour + ":59:59";
            }
            _fromDateHour = sFromHour;
            _reportDateHour = sToHour;
            _fromDateHour1 = fromHour;
            _reportDateHour1 = fromHour + 1;
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculatePreviousHour", "Info : _fromDateHour =  " + _fromDateHour + "  | _reportDateHour = " + _reportDateHour);
            }
        } catch (RuntimeException e) {
            _logger.errorTrace(METHOD_NAME, e);
        }

    }

    /**
     * @descriptions : This method will be used to send the SMS to Channel Admin
     *               Users with configured domain
     * @author : diwakar
     * @throws BTSLBaseException
     */
    public void sendSMS() throws BTSLBaseException {
        final String METHOD_NAME = "sendSMS";
        if (_logger.isDebugEnabled()) {
            _logger.debug("sendSMS", " Hourly: ");
        }

        Connection conDatafatch = null;
        try {
            conDatafatch = OracleUtil.getReportDBSingleConnection();
            if (conDatafatch == null) {
                _logger.error("sendSMS", " DATABASE Connection is NULL for Reporting - ReportDBSingleConnection");
                throw new BTSLBaseException(this, "sendSMS", "Not able to get the connection");
            }

            // Find Transaction details for O2C & C2C Types
            final ArrayList<SendSMSToChannelAdmin4HourlyTransVO> data4O2CAndC2CTxnType = sendSMSToChannelAdmin4HourlyTransDAO.fetchTxnDetailsOnHourly(conDatafatch,
                _fromDateHour, _reportDateHour);
            // Find Transaction details for C2S Type
            final ArrayList<SendSMSToChannelAdmin4HourlyTransVO> data4O2STxnType = sendSMSToChannelAdmin4HourlyTransDAO.fetchTxnDetailsOnHourly4C2S(conDatafatch,
                _fromDateHour, _reportDateHour);
            _logger.error("sendSMS", "data4O2CAndC2CTxnType.size()= " + data4O2CAndC2CTxnType.size() + " | data4O2STxnType.size() = " + data4O2STxnType.size());

            // Find Channel Admin User Details
            final LinkedHashMap<String, ArrayList<UserVO>> channelAdminUsers = sendSMSToChannelAdmin4HourlyTransDAO.fetchChannelAdminUsersDetails(conDatafatch, "BCU");
            if (channelAdminUsers != null && channelAdminUsers.size() > 0) {
                final LinkedHashMap<String, HashMap<String, String>> geographyDomainList = sendSMSToChannelAdmin4HourlyTransDAO.fetchGeographyDomainDetailsPerUserWise(
                    conDatafatch, channelAdminUsers);

                // Prepare And send SMS to Channel Admin Users Only
                _logger.info("SendSMSToChannelAdminTrans", "sendSMS : Prepare And send SMS to Channel Admin Users Only");
                prepareAndSendSMSToChannelAdminUsers(data4O2CAndC2CTxnType, data4O2STxnType, channelAdminUsers, geographyDomainList, "BCU");
                _logger.info("SendSMSToChannelAdminTrans",
                    "sendSMS : The SMS has been sent to all the channel admin  users  _fromDateHour =  " + _fromDateHour + "  | _toDateHour = " + _reportDateHour);
            } else {
                _logger.info("SendSMSToChannelAdminTrans", "sendSMS : No Channel Admin user found into system!");
            }
            // Find Area Admin User Details
            final LinkedHashMap<String, ArrayList<UserVO>> areaAdminUsers = sendSMSToChannelAdmin4HourlyTransDAO.fetchChannelAdminUsersDetails(conDatafatch, "ARADM");
            if (areaAdminUsers != null && areaAdminUsers.size() > 0) {
                final LinkedHashMap<String, HashMap<String, String>> geographyDomainListPerArea = sendSMSToChannelAdmin4HourlyTransDAO.fetchGeographyDomainDetailsPerUserWise(
                    conDatafatch, areaAdminUsers);

                // Prepare And send SMS to Area Admin User Only
                _logger.info("SendSMSToChannelAdminTrans", "sendSMS : Prepare And send SMS to Area Admin Users Only");
                prepareAndSendSMSToChannelAdminUsers(data4O2CAndC2CTxnType, data4O2STxnType, areaAdminUsers, geographyDomainListPerArea, "ARADM");
                _logger.info("SendSMSToChannelAdminTrans",
                    "sendSMS : The SMS has been sent to all the Area admin users  _fromDateHour =  " + _fromDateHour + "  | _toDateHour = " + _reportDateHour);
            } else {

                _logger.info("SendSMSToChannelAdminTrans", "sendSMS : No Area Admin user found into system!");
            }
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("sendSMS", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("sendSMS", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "sendSMS", "Exception=" + e.getMessage());
        } finally {
            try {
                if (conDatafatch != null) {
                    conDatafatch.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("sendSMS", " Exiting");
            }
        }
    }

    /**
     * @descriptions : This method will be used to prepare the text message &
     *               send SMS to Channel Admin Users with configured domain
     * @author : diwakar
     * @param : ArrayList<SendSMSToChannelAdmin4HourlyTransVO> - All the data
     *        for transaction (O2C& C2C)
     * @param : ArrayList<SendSMSToChannelAdmin4HourlyTransVO> - All the data
     *        for transaction (C2S)
     * @param : LinkedHashMap<String, ArrayList<ChannelAdminVO>> - All the
     *        Channel Admin users to whome the SMS will be send.
     * @param : LinkedHashMap<String, HashMap<String,String>>
     *        geographyDomainList - Geography Domain Lists
     * @param : String p_userType - category type for channel/area admin
     * @throws BTSLBaseException
     */
    private void prepareAndSendSMSToChannelAdminUsers(ArrayList<SendSMSToChannelAdmin4HourlyTransVO> sendSMSToChannelAdminUserList4O2CAndC2C, ArrayList<SendSMSToChannelAdmin4HourlyTransVO> sendSMSToChannelAdminUserList4c2s, LinkedHashMap<String, ArrayList<UserVO>> channelAdminUsers, LinkedHashMap<String, HashMap<String, String>> geographyDomainList, String p_userType) {
        final String hour = "HOUR " + _fromDateHour1 + "-" + _reportDateHour1 + ",";
        String smsMessage = "";
        final LinkedHashMap<String, LinkedHashMap<String, String>> mapD = new LinkedHashMap<String, LinkedHashMap<String, String>>();
        String keyToFind = null;

        if (sendSMSToChannelAdminUserList4O2CAndC2C != null && sendSMSToChannelAdminUserList4O2CAndC2C.size() > 0) {
            final Iterator iter = sendSMSToChannelAdminUserList4O2CAndC2C.iterator();
            while (iter.hasNext()) {
                final SendSMSToChannelAdmin4HourlyTransVO sendSMSVO = (SendSMSToChannelAdmin4HourlyTransVO) iter.next();
                final Iterator iterDomain = geographyDomainList.keySet().iterator();
                String domain = null;
                while (iterDomain.hasNext()) {
                    domain = (String) iterDomain.next();
                    final HashMap<String, String> geographyDomainMap = geographyDomainList.get(domain);
                    keyToFind = sendSMSVO.getDomainName() + "-" + geographyDomainMap.get(sendSMSVO.getDomainCode()) + "-" + sendSMSVO.getDomainCode() + "-" + sendSMSVO
                        .getNetworkCode();
                    if (!mapD.containsKey(keyToFind)) {
                        if (geographyDomainMap.containsKey(sendSMSVO.getDomainCode())) {
                            final LinkedHashMap<String, String> mapT = new LinkedHashMap<String, String>();
                            mapT.put(sendSMSVO.getTxnType() + "-" + sendSMSVO.getTrfType() + "-" + sendSMSVO.getDomainCode() + "-" + sendSMSVO.getDomainName(), sendSMSVO
                                .getTrfType() + "|" + sendSMSVO.getTxnCount() + "|" + sendSMSVO.getTxnAmount() + ":");
                            mapD.put(keyToFind, mapT);
                        }
                    } else {
                        if (geographyDomainMap.containsKey(sendSMSVO.getDomainCode())) {
                            final LinkedHashMap<String, String> mapT = mapD.get(keyToFind);
                            mapT.put(sendSMSVO.getTxnType() + "-" + sendSMSVO.getTrfType() + "-" + sendSMSVO.getDomainCode() + "-" + sendSMSVO.getDomainName(), sendSMSVO
                                .getTrfType() + "|" + sendSMSVO.getTxnCount() + "|" + sendSMSVO.getTxnAmount() + ":");
                            mapD.put(keyToFind, mapT);
                        }
                    }
                }

            }
        } else {
            // Added on 28-FEB-2014
            _logger.info("prepareAndSendSMSToChannelAdminUsers", "No record found for transcation type O2C & C2C");
            // Ended Here
        }

        if (sendSMSToChannelAdminUserList4c2s != null && sendSMSToChannelAdminUserList4c2s.size() > 0) {
            final Iterator iter = sendSMSToChannelAdminUserList4c2s.iterator();
            while (iter.hasNext()) {

                final SendSMSToChannelAdmin4HourlyTransVO sendSMSVO = (SendSMSToChannelAdmin4HourlyTransVO) iter.next();
                final Iterator iterDomain = geographyDomainList.keySet().iterator();
                String domain = null;
                while (iterDomain.hasNext()) {
                    domain = (String) iterDomain.next();
                    final HashMap<String, String> geographyDomainMap = geographyDomainList.get(domain);
                    keyToFind = sendSMSVO.getDomainName() + "-" + geographyDomainMap.get(sendSMSVO.getDomainCode()) + "-" + sendSMSVO.getDomainCode() + "-" + sendSMSVO
                        .getNetworkCode();
                    if (!mapD.containsKey(keyToFind)) {
                        if (geographyDomainMap.containsKey(sendSMSVO.getDomainCode())) {
                            final LinkedHashMap<String, String> mapT = new LinkedHashMap<String, String>();
                            mapT.put(sendSMSVO.getTxnType() + "-" + sendSMSVO.getTrfType() + "-" + sendSMSVO.getDomainCode() + "-" + sendSMSVO.getDomainName(), sendSMSVO
                                .getTrfType() + "|" + sendSMSVO.getTxnCount() + "|" + sendSMSVO.getTxnAmount() + ":");
                            mapD.put(keyToFind, mapT);
                        }
                    } else {
                        if (geographyDomainMap.containsKey(sendSMSVO.getDomainCode())) {
                            final LinkedHashMap<String, String> mapT = mapD.get(keyToFind);
                            mapT.put(sendSMSVO.getTxnType() + "-" + sendSMSVO.getTrfType() + "-" + sendSMSVO.getDomainCode() + "-" + sendSMSVO.getDomainName(), sendSMSVO
                                .getTrfType() + "|" + sendSMSVO.getTxnCount() + "|" + sendSMSVO.getTxnAmount() + ":");
                            mapD.put(keyToFind, mapT);
                        }
                    }
                }

            }
        } else {
            // Added on 28-FEB-2014
            _logger.info("prepareAndSendSMSToChannelAdminUsers", "No record found for transcation type C2S");
            // Ended Here
        }

        final Iterator<String> iter4Domain = mapD.keySet().iterator();
        while (iter4Domain.hasNext()) {
            String domain = iter4Domain.next();
            String domainGeo = domain.substring(0, domain.lastIndexOf('-'));
            final String networkCode = domain.substring(domain.lastIndexOf('-') + 1, domain.length());
            domainGeo = domainGeo.substring(0, domain.lastIndexOf('-'));
            smsMessage = domainGeo + "," + hour;
            final HashMap<String, String> mapT = mapD.get(domain);
            final Iterator<String> iter4Value = mapT.keySet().iterator();
            while (iter4Value.hasNext()) {
                final String value4Key = iter4Value.next();
                final String transferType = value4Key.substring(0, (value4Key.indexOf('-')));
                if (!smsMessage.contains(transferType)) {
                    smsMessage += transferType + "-";
                }
                final String value = mapT.get(value4Key);
                smsMessage += value;
            }
            smsMessage = smsMessage.substring(0, smsMessage.length() - 1);
            final String[] arrMessage = { smsMessage };

            if (p_userType.equalsIgnoreCase("BCU")) {
                smsMessage = BTSLUtil.getMessage(_locale, PretupsErrorCodesI.SMS_TO_CHANNEL_ADMIN_USERS_HOURLY, arrMessage);
            } else {
                smsMessage = BTSLUtil.getMessage(_locale, PretupsErrorCodesI.SMS_TO_AREA_ADMIN_USERS_HOURLY, arrMessage);
            }
            // Send SMS to Channel/Area Admin users with domain wise
            domain = domain.substring(0, (domain.indexOf('-')));
            if (channelAdminUsers.containsKey(domain)) {
                final Iterator<UserVO> channelUserListIter = channelAdminUsers.get(domain).iterator();
                while (channelUserListIter.hasNext()) {
                    final UserVO channelAdminVO = (UserVO) channelUserListIter.next();
                    if (channelAdminVO.getNetworkID().equalsIgnoreCase(networkCode)) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("prepareAndSendSMSToChannelAdminUsers", "FINAL SMS Text Message = " + smsMessage + " TO CHANNEL ADMIN USERS = " + channelAdminVO
                                .getMsisdn());
                        }
                        new PushMessage(channelAdminVO.getMsisdn(), smsMessage, null, null, _locale).push();

                        // Added By Diwakar for sending Mail on 23-MAR-2014
                        sendEmailNotification(smsMessage, channelAdminVO.getEmail(), p_userType);
                        // Ended Here
                    }
                }
            }

        }
    }

    /**
     * @descriptions : This method will be used to update the process.
     * @author : diwakar
     * @param : Connection - connection with database
     * @param : String - The process Id that need to update
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetailForMis(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

    /**
     * @description : To send Email to user about Daily Transfer Details.
     * @author diwakar
     * @param userVO
     * @param senderMessage
     * @throws Exception
     */
    private static void sendEmailNotification(String senderMessage, String p_toEmailId, String p_userType) {
        final String METHOD_NAME = "sendEmailNotification";
        try {
            if (!BTSLUtil.isNullString(senderMessage)) {
                if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("SMS_TO_CHANNEL_AREA_ADMIN_HOURLY_MAIL_SEND"))) {
                    final String to = p_toEmailId;
                    if (!BTSLUtil.isNullString(to)) {
                        final String from = Constants.getProperty("SMS_TO_CHANNEL_AREA_ADMIN_HOURLY_MAIL_FROM");
                        String subject = null;
                        if (p_userType.equalsIgnoreCase("BCU")) {
                            subject = Constants.getProperty("SMS_TO_CHANNEL_ADMIN_HOURLY_MAIL_SUBJECT");
                        } else {
                            subject = Constants.getProperty("SMS_TO_AREA_ADMIN_HOURLY_MAIL_SUBJECT");
                        }
                        final String msg = senderMessage;
                        // Send mail
                        EMailSender.sendMail(to, from, null, null, subject, msg, false, null, null);
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("sendEmailNotification", "Email has been send successfully for <" + subject + " > to <" + to + ">.");
                        }
                    } else {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("sendEmailNotification", "Email has not been send successfully as email is <" + to + "> ");
                        }
                    }
                }

            }
        } catch (Exception e) {
            _logger.error("sendEmailNotification", "Email has not been send successfully as execption  = " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
        }

    }
}
