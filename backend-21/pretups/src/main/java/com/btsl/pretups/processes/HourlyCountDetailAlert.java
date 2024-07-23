package com.btsl.pretups.processes;

/**
 * @(#)HourlyCountDetailAlert.java
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Shishupal Singh 24/04/2008 Initial Creation
 *                                 --------------------------------------------
 *                                 ----------------------------
 *                                 Copyright (c) 2008 Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 */
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
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
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.HourlyCountTransactionDetailsVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.ibm.icu.util.Calendar;

// This class is used to send sms to the circle administrator's mobile number
// according to the total transaction done in the last hour or in the current
// day.
public class HourlyCountDetailAlert {
    private static Log _logger = LogFactory.getLog(HourlyCountDetailAlert.class.getName());
    private static String moduleCode = null;

    /**
     * ensures no instantiation
     */
    private HourlyCountDetailAlert(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2 && arg.length != 3) {
                _logger.info(METHOD_NAME, "Usage : HourlyCountDetailAlert [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                _logger.info(METHOD_NAME, "HourlyCountDetailAlert" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                _logger.info(METHOD_NAME, "HourlyCountDetailAlert" + " Logconfig File Not Found .............");
                return;
            }
            if (arg.length == 2) {
                moduleCode = "BOTH";
            } else {
                moduleCode = arg[2];
                if (BTSLUtil.isNullString(moduleCode)) {
                    _logger.info(METHOD_NAME, "HourlyCountDetailAlert" + " Module Code Not Found .............");
                    return;
                }
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            return;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, " Exception : ...........................: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            return;
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * The method processes the sending sms
     * 
     * @return void
     * @throws BTSLBaseException
     *             method is used to process the request first check process is
     *             under process or process
     *             is executed till current date, then call the method user get
     *             user date
     */
    private static void process() throws BTSLBaseException {

        final String METHOD_NAME = "process";
        String adminMSISDN = null;
        String serviceType = null;
        ArrayList keys = null;
        ProcessBL processBL = null;
        ProcessStatusDAO processStatusDAO = null;
        ArrayList networkServiceList = null;
        Connection con = null;
        MComConnectionI mcomCon = null; // to get connection
        String processId = null; // to check process is under process or not
        boolean statusOk = false; // to check process is under process or not
        Date currentDate = null; // current date
        ProcessStatusVO processStatusVO = null;
        int messageCount = 0;
        int updateCount = 0; // check process details are updated or not
        Calendar now = null;
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Entered");
            }

            adminMSISDN = Constants.getProperty("Admin_msisdn_for_hourly_count_trans_process");
            if (BTSLUtil.isNullString(adminMSISDN)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", "Atleast one admin msisdn must be define in the Admin_msisdn_for_hourly_count_trans_process parameter of Constants.props");
                }
                return;
            }
            final String[] adminMSISDNArr = adminMSISDN.split(",");
            serviceType = Constants.getProperty("Service_type_for_hourly_count_trans_process");
            if (BTSLUtil.isNullString(serviceType)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", "Atleast one service type must be define in the Service_type_for_hourly_count_trans_process parameter of Constants.props");
                }
                return;
            }
            final String[] serviceTypeArr = serviceType.split(",");

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            processId = ProcessI.HOURLY_COUNT_DETAIL_PROCESSID;
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            if (statusOk) {
                now = BTSLDateUtil.getInstance();
                currentDate = now.getTime();
                String[] adminNetworkCode = null;
                String[] networkWiseServiceType = null;
                final HashMap _networkMap = NetworkCache.getNetworkMap();

                networkServiceList = new ServiceKeywordDAO().loadServiceTypeListForNetworkServices(con);
                final HashMap hm = new HashMap();
                String module = null;
                ListValueVO listValueVO = null;
                final HashMap adminMsisdnMap = new HashMap();
                for (int i = 0, j = adminMSISDNArr.length; i < j; i++) {
                    adminNetworkCode = adminMSISDNArr[i].split("_");
                    if (adminNetworkCode.length != 2) {
                        continue;
                    }
                    if (!_networkMap.containsKey(adminNetworkCode[0])) {
                        continue;
                    }
                    for (int k = 0, l = serviceTypeArr.length; k < l; k++) {
                        module = null;
                        networkWiseServiceType = serviceTypeArr[k].split("_");
                        if (networkWiseServiceType.length != 2) {
                            continue;
                        }
                        if (hm.containsKey(networkWiseServiceType[1])) {
                            module = (String) hm.get(networkWiseServiceType[1]);
                        } else {
                            for (int m = 0, n = networkServiceList.size(); m < n; m++) {
                                listValueVO = (ListValueVO) networkServiceList.get(m);
                                if (listValueVO.getValue().split(":")[1].equals(networkWiseServiceType[1])) {
                                    if ("BOTH".equals(moduleCode) || moduleCode.equals(listValueVO.getValue().split(":")[0])) {
                                        hm.put(networkWiseServiceType[1], listValueVO.getValue().split(":")[0]);
                                        module = listValueVO.getValue().split(":")[0];
                                        break;
                                    }
                                }
                            }
                        }
                        if (adminNetworkCode[0].equals(networkWiseServiceType[0]) && module != null) {
                            if (adminMsisdnMap.containsKey(adminNetworkCode[1])) {
                                ((ArrayList) adminMsisdnMap.get(adminNetworkCode[1])).add(adminNetworkCode[0] + "_" + module + "_" + networkWiseServiceType[1]);
                            } else {
                                keys = new ArrayList();
                                keys.add(adminNetworkCode[0] + "_" + module + "_" + networkWiseServiceType[1]);
                                adminMsisdnMap.put(adminNetworkCode[1], keys);
                            }
                        }
                    }
                }

                if (adminMsisdnMap.isEmpty()) {
                    if (_logger.isDebugEnabled()) {
                        _logger
                            .debug(
                                "process",
                                "Hourly transaction count process is not executed because there is no valid administrator msisdn or service type define in the Constants.props (Atleast one admin msisdn and one service type define in the Constants.props should be valid to execute this process.)");
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HourlyCountDetailAlert[process]", "", "", "",
                        " Hourly transaction count process is not executed because there is no valid administrator msisdn or service type define in the Constants.props");
                } else {
                    final HashMap transactionDetailMap = transactionDetail(con, now);
                    if (transactionDetailMap.isEmpty()) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("process", " Hourly transaction count process is executed Successfully, But there is no transaction.");
                        }
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HourlyCountDetailAlert[process]", "", "",
                            "", " Hourly transaction count process is executed  Successfully, But there is no transaction.");
                    } else {
                        messageCount = sendMessage(transactionDetailMap, adminMsisdnMap, now);
                        if (messageCount > 0) {
                            _logger.debug("process", "  Hourly transaction count process has been executed Successfully And number of messages sent:" + messageCount);
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HourlyCountDetailAlert[process]", "",
                                "", "", " Hourly transaction count process has been executed Successfully And number of messages sent:" + messageCount);
                        }
                    }
                }
            } else {
                throw new BTSLBaseException("HourlyCountDetailAlert", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
        } catch (BTSLBaseException be) {
            if (con != null) {
                try {
                    mcomCon.finalRollback();
                } catch (SQLException e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            try {
               mcomCon.finalRollback();
            } catch (SQLException e1) {
                _logger.errorTrace(METHOD_NAME, e1);
            }
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HourlyCountDetailAlert[process]", "", "", "",
                " HourlyCountDetailAlert process could not be executed successfully.");
            throw new BTSLBaseException("HourlyCountDetailAlert", "process", PretupsErrorCodesI.ERROR_IN_DAILY_ALERT);
        } finally {
            try {
                if (statusOk) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);
                    processStatusVO.setExecutedUpto(currentDate);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    processStatusDAO = new ProcessStatusDAO();
                    updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
                    if (updateCount > 0) {
                        mcomCon.finalCommit();
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("HourlyCountDetailAlert#process");
					mcomCon = null;
				}
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", "Exception in closing connection ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * The method processes the sending sms
     * 
     * @param p_now
     *            TODO
     * @param Connection
     *            con
     * @param Date
     *            p_processingDate
     * @return boolean
     * @throws BTSLBaseException
     *             methods load user data (c2s transfers, channel transfers,
     *             opening ang closing balance )from database
     */
    private static HashMap transactionDetail(Connection p_con, Calendar p_now) // throws
    // BTSLBaseException
    {
        final String METHOD_NAME = "transactionDetail";
        if (_logger.isDebugEnabled()) {
            _logger.debug("transactionDetail", " Entered ");
        }
        PreparedStatement pstmtC2S = null; // for C2S transaction details
        PreparedStatement pstmtP2P = null; // for P2P transaction details
        ResultSet rsC2S = null;
        ResultSet rsP2P = null;
        HashMap transactionDetailMap = null;

        try {

            final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            final SimpleDateFormat formatter1 = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
            final int hour = p_now.getTime().getHours();
            Calendar working  = (Calendar) p_now.clone();
            if (hour == 0) {
                working.add(Calendar.DAY_OF_YEAR, -(1));
            }

            HourlyCountTransactionDetailsVO hctdVO = null;
            transactionDetailMap = new HashMap();
            
            HourlyCountDetailAlertQry hourlyCountDetailAlertQry = (HourlyCountDetailAlertQry) ObjectProducer.getObject(QueryConstants.HOURLY_COUNT_DETAIL_ALERT_QRY, QueryConstants.QUERY_PRODUCER);

            if (moduleCode.equals(PretupsI.C2S_MODULE) || "BOTH".equals(moduleCode)) {
                
            	final String queryC2STrans = hourlyCountDetailAlertQry.transactionDetailIfC2SModule(formatter, formatter1, p_now, working);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("transactionDetail", "C2S Transaction Query:  " + queryC2STrans);
                }
                pstmtC2S = p_con.prepareStatement(queryC2STrans.toString());

                rsC2S = pstmtC2S.executeQuery();
                while (rsC2S.next()) {
                    hctdVO = new HourlyCountTransactionDetailsVO();
                    hctdVO.setSuccessCount(rsC2S.getLong("success_hour_count"));
                    hctdVO.setFailureCount(rsC2S.getLong("failed_hour_count"));
                    hctdVO.setTotalCount(rsC2S.getLong("total_hour_count"));
                    hctdVO.setTotalAmount(rsC2S.getLong("total_hour_amount"));
                    hctdVO.setSuccessDayCount(rsC2S.getLong("success_day_count"));
                    hctdVO.setFailureDayCount(rsC2S.getLong("failed_day_count"));
                    hctdVO.setTotalDayCount(rsC2S.getLong("total_day_count"));
                    hctdVO.setTotalDayAmount(rsC2S.getLong("total_day_amount"));
                    hctdVO.setNetworkCode(rsC2S.getString("network_code"));
                    hctdVO.setModule(PretupsI.C2S_MODULE);
                    hctdVO.setServiceType(rsC2S.getString("service_type"));
                    transactionDetailMap.put(hctdVO.getNetworkCode() + "_" + hctdVO.getModule() + "_" + hctdVO.getServiceType(), hctdVO);
                }
                pstmtC2S.clearParameters();
            }

            if (moduleCode.equals(PretupsI.P2P_MODULE) || "BOTH".equals(moduleCode)) {
            	String queryP2PTrans = hourlyCountDetailAlertQry.transactionDetailIfP2PModule(formatter, formatter1, p_now, working);
            	if (_logger.isDebugEnabled()) {
                    _logger.debug("transactionDetail", "P2P Transaction Query:  " + queryP2PTrans);
                }
                pstmtP2P = p_con.prepareStatement(queryP2PTrans.toString());

                rsP2P = pstmtP2P.executeQuery();
                while (rsP2P.next()) {
                    hctdVO = new HourlyCountTransactionDetailsVO();
                    hctdVO.setSuccessCount(rsP2P.getLong("success_hour_count"));
                    hctdVO.setFailureCount(rsP2P.getLong("failed_hour_count"));
                    hctdVO.setTotalCount(rsP2P.getLong("total_hour_count"));
                    hctdVO.setTotalAmount(rsP2P.getLong("total_hour_amount"));
                    hctdVO.setSuccessDayCount(rsP2P.getLong("success_day_count"));
                    hctdVO.setFailureDayCount(rsP2P.getLong("failed_day_count"));
                    hctdVO.setTotalDayCount(rsP2P.getLong("total_day_count"));
                    hctdVO.setTotalDayAmount(rsP2P.getLong("total_day_amount"));
                    hctdVO.setNetworkCode(rsP2P.getString("network_code"));
                    hctdVO.setModule(PretupsI.P2P_MODULE);
                    hctdVO.setServiceType(rsP2P.getString("service_type"));
                    transactionDetailMap.put(hctdVO.getNetworkCode() + "_" + hctdVO.getModule() + "_" + hctdVO.getServiceType(), hctdVO);
                }
                pstmtP2P.clearParameters();
            }
        }// end of try
        catch (Exception e) {
            _logger.error("HourlyCountDetailAlert[transactionDetail]", "Exception  : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HourlyCountDetailAlert[process]", "", "", "",
                " HourlyCountDetailAlert process =" + e.getMessage());
            // return false;
        }// end of catch
        finally {
            if (rsC2S != null) {
                try {
                    rsC2S.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (rsP2P != null) {
                try {
                    rsP2P.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmtC2S != null) {
                try {
                    pstmtC2S.close();
                } catch (SQLException e4) {
                    _logger.errorTrace(METHOD_NAME, e4);
                }
            }
            if (pstmtP2P != null) {
                try {
                    pstmtP2P.close();
                } catch (SQLException e5) {
                    _logger.errorTrace(METHOD_NAME, e5);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("transactionDetail", " Exiting ");
            }
        }
        return transactionDetailMap;

    }

    /**
     * This method sends the alerts to users one-by-one .
     * 
     * @param p_adminMsisdnMap
     *            TODO
     * @param p_now
     *            TODO
     * @param ArrayList
     *            p_balanceList
     * @param HashMap
     *            p_map
     * @return int
     *         method will send message to the user on the basis of user id and
     *         product code
     */
    private static int sendMessage(HashMap p_transactionDetailMap, HashMap p_adminMsisdnMap, Calendar p_now) {
        final String METHOD_NAME = "sendMessage";
        if (_logger.isDebugEnabled()) {
            _logger.info("sendMessage", "Entered with p_transactionDetailMap size " + p_transactionDetailMap.size());
        }
        ArrayList keyList = null;
        HourlyCountTransactionDetailsVO hctdVO = null;
        KeyArgumentVO keyArgumentVO = null;
        ArrayList sssC2SList = null;
        ArrayList sssP2PList = null;
        int messageCount = 0;
        Locale locale = null;
        try {

            /*
             * ##Hourly Count Transaction Details.
             * 8885={0}-S={1},F={2},T={3}
             * 8886=Hr({0}) {1}
             * 8887=Till {0} {1}
             * 8888={0}
             * 8889=mclass^2&pid^61:8889:{0}.
             */

            String message = null;
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            final Set set = p_adminMsisdnMap.keySet();
            final Iterator itr = set.iterator();
            String keyMsisdn = null;
            int hour = p_now.getTime().getHours();
            if (hour == 0) {
                hour = 24;
            }

            String[] message1 = null;
            String[] message2 = null;
            String key = null;
            String str = null;
            boolean c2sflag = true, p2pflag = true;
            int j = 0, i = 0, k = 0, l = 0;
            while (itr.hasNext()) {
                keyMsisdn = (String) itr.next();
                keyList = (ArrayList) p_adminMsisdnMap.get(keyMsisdn);
                if (keyList == null) {
                    continue;
                }
                message1 = new String[keyList.size()];
                message2 = new String[keyList.size()];
                for (j = 0, k = keyList.size(); j < k; j++) {
                    key = (String) keyList.get(j);
                    hctdVO = null;
                    hctdVO = (HourlyCountTransactionDetailsVO) p_transactionDetailMap.get(key);
                    if (hctdVO != null) {
                        message1[j] = BTSLUtil.getMessage(locale, PretupsErrorCodesI.SERVICE_TRAN_COUNT_MESSAGE, new String[] { hctdVO.getServiceType(), Long.toString(hctdVO
                            .getSuccessCount()), Long.toString(hctdVO.getFailureCount()), Long.toString(hctdVO.getTotalCount()), Long.toString(hctdVO.getTotalAmount()) });
                        message2[j] = BTSLUtil.getMessage(locale, PretupsErrorCodesI.SERVICE_TRAN_COUNT_MESSAGE, new String[] { hctdVO.getServiceType(), Long.toString(hctdVO
                            .getSuccessDayCount()), Long.toString(hctdVO.getFailureDayCount()), Long.toString(hctdVO.getTotalDayCount()), Long.toString(hctdVO
                            .getTotalDayAmount()) });
                    }
                }
                if (message1.length > 0) {
                    sssC2SList = new ArrayList();
                    sssP2PList = new ArrayList();
                    c2sflag = true;
                    p2pflag = true;
                    for (i = 0, l = keyList.size(); i < l; i++) {
                        str = (String) keyList.get(i);
                        if (message1[i] != null) {
                            keyArgumentVO = new KeyArgumentVO();
                            if ((c2sflag && str.split("_")[1].equals(PretupsI.C2S_MODULE)) || (p2pflag && str.split("_")[1].equals(PretupsI.P2P_MODULE))) {
                                keyArgumentVO.setKey(PretupsErrorCodesI.HOURLY_SERVICE_TRAN_COUNT_MESSAGE);
                                keyArgumentVO.setArguments(new String[] { (hour - 1) + "-" + hour, message1[i] });
                            } else {
                                keyArgumentVO.setKey(PretupsErrorCodesI.COMBINE_HOURLY_DAILY_TRAN_COUNT_MESSAGE);
                                keyArgumentVO.setArguments(message1[i]);
                            }
                            if (str.split("_")[1].equals(PretupsI.C2S_MODULE)) {
                                sssC2SList.add(keyArgumentVO);
                                c2sflag = false;
                            } else if (str.split("_")[1].equals(PretupsI.P2P_MODULE)) {
                                sssP2PList.add(keyArgumentVO);
                                p2pflag = false;
                            }
                        }
                    }
                    c2sflag = true;
                    p2pflag = true;
                    for (i = 0, l = keyList.size(); i < l; i++) {
                        str = (String) keyList.get(i);
                        if (message2[i] != null) {
                            keyArgumentVO = new KeyArgumentVO();
                            if ((c2sflag && str.split("_")[1].equals(PretupsI.C2S_MODULE)) || (p2pflag && str.split("_")[1].equals(PretupsI.P2P_MODULE))) {
                                keyArgumentVO.setKey(PretupsErrorCodesI.DAILY_SERVICE_TRAN_COUNT_MESSAGE);
                                keyArgumentVO.setArguments(new String[] { Integer.toString(hour), message2[i] });
                            } else {
                                keyArgumentVO.setKey(PretupsErrorCodesI.COMBINE_HOURLY_DAILY_TRAN_COUNT_MESSAGE);
                                keyArgumentVO.setArguments(message2[i]);
                            }
                            if (str.split("_")[1].equals(PretupsI.C2S_MODULE)) {
                                sssC2SList.add(keyArgumentVO);
                                c2sflag = false;
                            } else if (str.split("_")[1].equals(PretupsI.P2P_MODULE)) {
                                sssP2PList.add(keyArgumentVO);
                                p2pflag = false;
                            }
                        }
                    }
                    if (sssC2SList != null && !sssC2SList.isEmpty()) {
                        message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.MESSAGE_FOR_HOURLY_TRAN_COUNT, new String[] { BTSLUtil.getMessage(locale, sssC2SList) });
                        final PushMessage pushMessage = new PushMessage(keyMsisdn, message, null, null, locale);
                        pushMessage.push();
                        Thread.sleep(100);
                        messageCount++;
                    }

                    if (sssP2PList != null && !sssP2PList.isEmpty()) {
                        message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.MESSAGE_FOR_HOURLY_TRAN_COUNT, new String[] { BTSLUtil.getMessage(locale, sssP2PList) });
                        final PushMessage pushMessage = new PushMessage(keyMsisdn, message, null, null, locale);
                        pushMessage.push();
                        Thread.sleep(100);
                        messageCount++;
                    }

                }
            }

            return messageCount;
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("sendMessage", "Error:" + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            return messageCount;
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info("sendMessage", " Exiting");
            }
        }
    }

}