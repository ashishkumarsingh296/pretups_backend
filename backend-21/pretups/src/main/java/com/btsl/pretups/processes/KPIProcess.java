/*
 * @(#)KPIProcess.java
 * Copyright(c) 2009, Comviva Technologies Ltd.
 * All Rights Reserved
 * Description :-
 * --------------------------------------------------------------------
 * Author Date History
 * --------------------------------------------------------------------
 * ved.sharma Nov 23, 2009 Initial creation
 * --------------------------------------------------------------------
 */
package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class KPIProcess {
    private static Log _logger = LogFactory.getLog(KPIProcess.class.getName());
    private static Date _reportDate = null;
    private Date _fromDate = null;
    private Date _toDate = null;
    private String _frequency = null;

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-main
     * 
     * @param arg
     *            Return :-void
     *            Nov 23, 2009 12:44:05 PM
     */
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length > 4 || arg.length < 3)// check the argument length
            {
                System.out.println("KPIProcess :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props KPIConfig.props ReportDate(dd/MM/yy)");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                System.out.println("KPIProcess" + " Constants File Not Found at the path : " + arg[0]);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                System.out.println("KPIProcess" + " ProcessLogConfig File Not Found at the path : " + arg[1]);
                return;
            }
            final File kpiConfigFile = new File(arg[2]);
            if (!kpiConfigFile.exists()) {
                System.out.println("KPIProcess" + " KPIConfig.props Not Found at the path : " + arg[2]);
                return;
            }

            if (arg.length == 4 && !BTSLUtil.isNullString(arg[3])) {
                try {
                    _reportDate = BTSLUtil.getDateFromDateString(arg[3], PretupsI.DATE_FORMAT);
                } catch (ParseException e1) {
                    System.out.println("KPIProcess :: Report date format should be dd/MM/yy");
                    _logger.errorTrace(METHOD_NAME, e1);
                    return;
                }
            } else {
                _reportDate = new Date();
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            Constants.load(arg[2]);

        } catch (Exception e) {
            _logger.error("main", "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "KPIProcess[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            final KPIProcess KPIProcess = new KPIProcess();
            KPIProcess.process();
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KPIProcess[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error("main", "Exception :" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KPIProcess[main]", "", "", "", "Exception:" + e
                .getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-process
     * 
     * @throws BTSLBaseException
     * @throws Exception
     *             Return :-void
     *             Nov 23, 2009 12:34:47 PM
     */
    private void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", " Entered:  _reportDate=" + _reportDate);
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException(this, "process", "Not able to get the connection");
            }
            _frequency = BTSLUtil.NullToString(Constants.getProperty("REPORT_FREQUENCY")).trim();

            final ArrayList networkList = getNetworkList(con);
            final ArrayList indexList = getKPIIndex();
            int allKPI = 0;
            if (networkList != null && !networkList.isEmpty()) {
                String networkCode = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkCode = (((String) networkList.get(k)).split(":"))[0];
                    HashMap dataMap = new HashMap();
                    if (BTSLUtil.isNullString(_frequency))// default should be
                    // monthly
                    {
                        _frequency = "MONTHLY";
                        setDates(_frequency);
                        if (indexList == null || indexList.isEmpty()) {
                            // kpi
                            fetchKPIData(con, networkCode, _frequency, dataMap, -1);
                        } else {
                            for (int n = 0, m = indexList.size(); n < m; n++) {
                                allKPI = Integer.parseInt((String) indexList.get(n));
                                fetchKPIData(con, networkCode, _frequency, dataMap, allKPI);
                            }

                        }

                        if (dataMap.size() > 0) {
                            dataMap.put("FREQUENCY", _frequency);
                            dataMap.put("FROM_DATE", _fromDate);
                            dataMap.put("TO_DATE", _toDate);

                            new KPIReportWriteInXLS().writeExcel(dataMap, getFileName(_frequency, networkCode));
                            // create work sheet accrding to frequency write in
                            // XLS file
                        } else {
                            throw new BTSLBaseException(this, "process", "KPI dataMap is blank");
                        }
                        sendEMail(getFileName(_frequency, networkCode), getFileName(_frequency, networkCode), networkCode);
                    } else {
                        final String[] fr = _frequency.split(",");
                        for (int i = 0, j = fr.length; i < j; i++) {
                            dataMap = new HashMap();
                            setDates(fr[i]);
                            if (indexList == null || indexList.isEmpty()) {
                                // kpi
                                fetchKPIData(con, networkCode, fr[i], dataMap, -1);
                            } else {
                                for (int n = 0, m = indexList.size(); n < m; n++) {
                                    allKPI = Integer.parseInt((String) indexList.get(n));
                                    fetchKPIData(con, networkCode, fr[i], dataMap, allKPI);
                                }

                            }
                            if (dataMap.size() > 0) {
                                dataMap.put("FREQUENCY", fr[i].trim());
                                dataMap.put("FROM_DATE", _fromDate);
                                dataMap.put("TO_DATE", _toDate);
                                new KPIReportWriteInXLS().writeExcel(dataMap, getFileName(fr[i], networkCode));
                                // create work sheet accrding to frequency write
                                // in XLS file
                            } else {
                                throw new BTSLBaseException(this, "process", "KPI dataMap is blank");
                            }
                            sendEMail(getFileName(_frequency, networkCode), getFileName(fr[i], networkCode), networkCode);
                        }
                    }
                }

            }

            // send mail

        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " Exiting:  fromDate=" + _fromDate + " toDate=" + _toDate + " frequency=" + _frequency);
            }
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-setDates
     * 
     * @throws BTSLBaseException
     * @throws Exception
     *             Return :-void
     *             Nov 23, 2009 1:49:30 PM
     */
    private void setDates(String p_frequency) throws BTSLBaseException {
        final String METHOD_NAME = "setDates";
        if (_logger.isDebugEnabled()) {
            _logger.debug("setDates", " Entered:  _reportDate=" + _reportDate + " p_frequency=" + p_frequency);
        }
        try {
            final Calendar reporCaldate = BTSLDateUtil.getInstance();
            p_frequency = p_frequency.trim().toUpperCase();
            if ("MONTHLY".equals(p_frequency)) {
                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.MONTH, -1);
                final int endday = reporCaldate.getActualMaximum(Calendar.DAY_OF_MONTH);
                final int actualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                final int year = reporCaldate.get(Calendar.YEAR);
                _fromDate = BTSLUtil.getDateFromDateString("01/" + getTwoDigit(actualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY);
                _toDate = BTSLUtil.getDateFromDateString(getTwoDigit(endday) + "/" + getTwoDigit(actualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY);
            } else if ("WEEKLY".equals(p_frequency)) {
                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.WEEK_OF_YEAR, -1);
                reporCaldate.set(Calendar.DAY_OF_WEEK, 1);
                final int startday = reporCaldate.get(Calendar.DATE);
                final int startactualmonth = reporCaldate.get(Calendar.MONTH) + 1;

                reporCaldate.set(Calendar.DAY_OF_WEEK, 7);
                final int endday = reporCaldate.get(Calendar.DATE);
                final int actualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                final int year = reporCaldate.get(Calendar.YEAR);
                _fromDate = BTSLUtil.getDateFromDateString(getTwoDigit(startday) + "/" + getTwoDigit(startactualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY);
                _toDate = BTSLUtil.getDateFromDateString(getTwoDigit(endday) + "/" + getTwoDigit(actualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY);
            } else if ("DAILY".equals(p_frequency)) {
                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.DATE, -1);
                final int startday = reporCaldate.get(Calendar.DATE);
                final int startactualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                final int year = reporCaldate.get(Calendar.YEAR);
                _fromDate = BTSLUtil.getDateFromDateString(getTwoDigit(startday) + "/" + getTwoDigit(startactualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY);
                _toDate = BTSLUtil.getDateFromDateString(getTwoDigit(startday) + "/" + getTwoDigit(startactualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY);
            } else {
                throw new BTSLBaseException(this, "setDates", "REPORT_FREQUENCY value should be DAILY/MONTHLY/WEEKLY =" + p_frequency);
            }
        } catch (BTSLBaseException e) {
            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("setDates", " Exiting:  fromDate=" + _fromDate + " toDate=" + _toDate + " frequency=" + p_frequency);
            }
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-sendEMail
     * 
     * @param p_finalFileName
     * @param p_fileName
     *            Return :-void
     *            Nov 23, 2009 1:43:42 PM
     * @param p_networkCode
     *            TODO
     */
    private void sendEMail(String p_finalFileName, String p_fileName, String p_networkCode) {
        final String METHOD_NAME = "sendEMail";
        if (_logger.isDebugEnabled()) {
            _logger.debug("sendEMail", " Entered:  p_finalFileName=" + p_finalFileName + " p_fileName=" + p_fileName);
        }
        String isRequired = null;
        try {
            isRequired = BTSLUtil.NullToString(Constants.getProperty("KPI_MAIL_SEND_REQUIRED")).trim();
            if (PretupsI.YES.equalsIgnoreCase(isRequired)) {
                // start to send mail sending process
                String to = Constants.getProperty("KPI_REPORT_MAIL_TO_" + p_networkCode);
                if (BTSLUtil.isNullString(to)) {
                    to = Constants.getProperty("KPI_REPORT_MAIL_TO");
                }
                final String from = Constants.getProperty("KPI_REPORT_MAIL_FROM");
                final String subject = Constants.getProperty("KPI_REPORT_MAIL_SUBJECT");
                final String bcc = Constants.getProperty("KPI_REPORT_MAIL_BCC");
                final String cc = Constants.getProperty("KPI_REPORT_MAIL_CC");
                final String msg = Constants.getProperty("KPI_REPORT_MAIL_MESSAGE");
                // Send mail
                EMailSender.sendMail(to, from, bcc, cc, subject, msg, true, p_finalFileName, p_fileName);
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("sendEMail", " Exception:  " + e.getMessage());
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("sendEMail", " Exiting:  p_finalFileName=" + p_finalFileName + " p_fileName=" + p_fileName + " isRequired=" + isRequired);
            }
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-fetchKPIData
     * 
     * @param p_con
     *            Return :-HashMap
     *            Nov 23, 2009 2:23:49 PM
     * @param p_networkCode
     *            TODO
     * @param p_frequency
     *            TODO
     * @param p_kpiID
     *            TODO
     */
    private void fetchKPIData(Connection p_con, String p_networkCode, String p_frequency, HashMap p_map, int p_kpiID) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "fetchKPIData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchKPIData",
                " Entered:  _fromDate=" + _fromDate + " _toDate=" + _toDate + " p_networkCode=" + p_networkCode + " p_frequency=" + p_frequency + " p_kpiID=" + p_kpiID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String selectQuery = null;
        ArrayList list = null;
    	KPIProcessQry kpiProcessQry = (KPIProcessQry)ObjectProducer.getObject(QueryConstants.KPI_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
        /*
         * #This is the KPI Secquence number
         * ## 1) Number of retailers -ok
         * ## 2) Number of new retailers in last month -ok
         * ## 3) Number of end customers -ok
         * ## 4) Number of new customers in last month -ok
         * ## 5) Average balance per retailer in last month -ok
         * 
         * ## 6) Average C2C - Transfer amount in last month -ok
         * ## Average C2C - Return amount in last month -ok
         * ## Average C2C - withdraw amount in last month -ok
         * ## Total number of C2C - Transfer in last month -ok
         * ## Total number of C2C - Return in last month -ok
         * ## Total number of C2C - Withdraw in last month -ok
         * 
         * ## 7) Total number of O2C - Transfer in last month -ok
         * ## Total number of O2C - Withdraw in last month -ok
         * ## Total number of O2C - Return in last month -ok
         * ## 8) Average number of C2S per retailer in last month -ok
         * ## 9) Average number of C2C per retailer in last month -ok
         * ## 10) Concentration of distribution to measure the concentration of
         * air time distribution per country -ok
         * ## #Distribution 60% KPI : number of active RP2P users which
         * contributes to 60% on the RP2P amount per month
         * ## #Distribution 80% KPI : number of active RP2P users which
         * contributes to 80% on the RP2P amount per month
         * ## 11) If more than 1 last month the put the value as comma
         * seperated, value should be positive integer -ok
         * ## #Number of active retailers in last 3 months
         * ## #Number of active retailers in last 6 months
         * ## 12) Average commission per retailer (O2C et C2C) in last month -ok
         * ## 13) Average bonus per C2S transaction in last month -ok
         * ## 14) Average bonus per P2P transaction in last month -ok
         * ## 15) % of active end-customers in last month -(Under contruction)
         * ## 16) Average number of P2P per customer in last month -(Under
         * contruction)
         * ## 17) The total air-time concretely transferred to end-users C2S
         * (with generated bonus). -(Under contruction)
         * ## 18) The sum of the revenues collected on the Head of Channels O2C
         * (PAYABLE AMOUNT = RP2P REVENUE for the Orange affiliate)-(Under
         * contruction)
         * ## 19) The commissions generated within all the domains through O2C
         * and C2C (b)
         */
        try {
            switch (p_kpiID) {

                case -1:
                    {
                        // for All, it will first case of switch;
                    }
                case 1:
                    {
                        // Number of retailers
                        selectQuery = "SELECT COUNT(USER_ID)user_count FROM USERS WHERE STATUS='Y' AND USER_TYPE='CHANNEL'AND network_code=? ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setString(1, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("NO_CHNL_USERS", rs.getString("user_count"));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 2:
                    {
                        // Number of new retailers in last month
                    
                    	selectQuery = kpiProcessQry.fetchKPIDataCountUserUsersQry();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("NO_OF_NEW_CHNL_USERS_DURATION", rs.getString("user_count"));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 3:
                    {
                        // Number of end customers
                        selectQuery = "SELECT COUNT(USER_ID)user_count FROM P2P_SUBSCRIBERS WHERE STATUS='Y' and NETWORK_CODE=?";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setString(1, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("NO_END_CUSTOMERS", rs.getString("user_count"));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 4:
                    {
                        // Number of new customers in last month -ok
                        selectQuery = kpiProcessQry.fetchKPIDataCountUserUsersFromP2PSubscriberQry();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("NO_OF_NEW_CUSTOMERS_DURATION", rs.getString("user_count"));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 5:
                    {
                        // Average balance per retailer in last month
                        selectQuery = "SELECT U.USER_NAME,U.LOGIN_ID,U.MSISDN, AVG(UB.BALANCE)AVG_BAL FROM USER_DAILY_BALANCES UB, USERS U WHERE UB.USER_ID = U.USER_ID AND U.USER_TYPE='CHANNEL' ";
                        selectQuery += " AND U.STATUS='Y' AND BALANCE_DATE >= ? AND BALANCE_DATE <= ? and U.NETWORK_CODE=?  GROUP BY U.USER_NAME,U.LOGIN_ID,U.MSISDN ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(rs.getString("USER_NAME") + ":" + rs.getString("LOGIN_ID") + ":" + rs.getString("MSISDN") + ":" + PretupsBL.getDisplayAmount(rs
                                .getLong("AVG_BAL")));
                        }
                        p_map.put("AVG_CHNL_USERS_BAL", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 6:
                    {
                        // Average C2C - Transfer amount in last month
                        selectQuery = "SELECT AVG(TM.C2C_TRANSFER_IN_AMOUNT) C2C_IN, AVG(TM.C2C_TRANSFER_OUT_AMOUNT) C2C_OUT FROM DAILY_CHNL_TRANS_MAIN TM, USERS U ";
                        selectQuery += " WHERE TM.USER_ID = U.USER_ID  AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND TM.TRANS_DATE >=? AND TM.TRANS_DATE <=? and U.NETWORK_CODE=?";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("AVG_C2C_TRANSFER_BAL", PretupsBL.getDisplayAmount(rs.getLong("C2C_IN")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("C2C_OUT")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;
                        // Average C2C - Return amount in last month
                        selectQuery = "SELECT AVG(TM.C2C_RETURN_IN_AMOUNT) C2C_IN, AVG(TM.C2C_RETURN_OUT_AMOUNT) C2C_OUT FROM DAILY_CHNL_TRANS_MAIN TM, USERS U ";
                        selectQuery += " WHERE TM.USER_ID = U.USER_ID  AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND TM.TRANS_DATE >=? AND TM.TRANS_DATE <=?  and U.NETWORK_CODE=?";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try {
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("AVG_C2C_RETURN_BAL", PretupsBL.getDisplayAmount(rs.getLong("C2C_IN")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("C2C_OUT")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        // Average C2C - WITHDRAW amount in last month
                        selectQuery = "SELECT AVG(TM.C2C_WITHDRAW_IN_AMOUNT) C2C_IN, AVG(TM.C2C_WITHDRAW_OUT_AMOUNT) C2C_OUT FROM DAILY_CHNL_TRANS_MAIN TM, USERS U ";
                        selectQuery += " WHERE TM.USER_ID = U.USER_ID  AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND TM.TRANS_DATE >=? AND TM.TRANS_DATE <=?  and U.NETWORK_CODE=? ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try {
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("AVG_C2C_WITHDRAW_BAL", PretupsBL.getDisplayAmount(rs.getLong("C2C_IN")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("C2C_OUT")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        // Total C2C - Transfer amount in last month
                        selectQuery = "SELECT SUM(TM.C2C_TRANSFER_IN_AMOUNT) C2C_IN, SUM(TM.C2C_TRANSFER_OUT_AMOUNT) C2C_OUT FROM DAILY_CHNL_TRANS_MAIN TM, USERS U ";
                        selectQuery += " WHERE TM.USER_ID = U.USER_ID  AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND TM.TRANS_DATE >=? AND TM.TRANS_DATE <=?  and U.NETWORK_CODE=? ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try {
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("TOTAL_C2C_TRANSFER_BAL", PretupsBL.getDisplayAmount(rs.getLong("C2C_IN")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("C2C_OUT")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        // Total C2C - Return amount in last month
                        selectQuery = "SELECT SUM(TM.C2C_RETURN_IN_AMOUNT) C2C_IN, SUM(TM.C2C_RETURN_OUT_AMOUNT) C2C_OUT FROM DAILY_CHNL_TRANS_MAIN TM, USERS U ";
                        selectQuery += " WHERE TM.USER_ID = U.USER_ID  AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND TM.TRANS_DATE >=? AND TM.TRANS_DATE <=? and U.NETWORK_CODE=? ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try {
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("TOTAL_C2C_RETURN_BAL", PretupsBL.getDisplayAmount(rs.getLong("C2C_IN")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("C2C_OUT")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        
                        rs = null;
                        pstmtSelect = null;

                        // TOTAL C2C - Return amount in last month
                        selectQuery = "SELECT SUM(TM.C2C_WITHDRAW_IN_AMOUNT) C2C_IN, SUM(TM.C2C_WITHDRAW_OUT_AMOUNT) C2C_OUT FROM DAILY_CHNL_TRANS_MAIN TM, USERS U ";
                        selectQuery += " WHERE TM.USER_ID = U.USER_ID  AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND TM.TRANS_DATE >=? AND TM.TRANS_DATE <=? and U.NETWORK_CODE=? ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("TOTAL_C2C_WITHDRAW_BAL", PretupsBL.getDisplayAmount(rs.getLong("C2C_IN")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("C2C_OUT")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 7:
                    {
                        // Total number of O2C - Transfer in last month
                        // Total number of O2C - Withdraw in last month
                        // Total number of O2C - Return in last month
                        selectQuery = "SELECT SUM(TM.O2C_TRANSFER_IN_AMOUNT) O2C_TRF_IN, SUM(TM.O2C_WITHDRAW_OUT_AMOUNT) O2C_WD_OUT,SUM(TM.O2C_RETURN_OUT_AMOUNT) O2C_RET_OUT FROM DAILY_CHNL_TRANS_MAIN TM, USERS U ";
                        selectQuery += " WHERE TM.USER_ID = U.USER_ID  AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND TM.TRANS_DATE >=? AND TM.TRANS_DATE <=? and U.NETWORK_CODE=? ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("TOTAL_O2C_BAL",
                                PretupsBL.getDisplayAmount(rs.getLong("O2C_TRF_IN")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("O2C_WD_OUT")) + ":" + PretupsBL
                                    .getDisplayAmount(rs.getLong("O2C_RET_OUT")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 8:
                    {
                        // Average number of C2S per retailer in last month
                        selectQuery = "SELECT U.USER_NAME,U.LOGIN_ID,U.MSISDN, AVG(C2S_TRANSFER_OUT_COUNT) AVG_C2S  ";
                        selectQuery += " FROM DAILY_CHNL_TRANS_MAIN MC, USERS U WHERE MC.USER_ID = U.USER_ID AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' " + "AND MC.TRANS_DATE >= ? AND MC.TRANS_DATE <= ? and U.NETWORK_CODE=? GROUP BY U.USER_NAME,U.LOGIN_ID,U.MSISDN ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(rs.getString("USER_NAME") + ":" + rs.getString("LOGIN_ID") + ":" + rs.getString("MSISDN") + ":" + rs.getLong("AVG_C2S"));
                        }
                        p_map.put("AVG_NO_C2S_PER_CHNL_USERS", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 9:
                    {
                        // Average number of C2C per retailer in last month
                        selectQuery = "SELECT U.USER_NAME,U.LOGIN_ID,U.MSISDN, AVG(C2C_TRANSFER_IN_COUNT + C2C_TRANSFER_OUT_COUNT + C2C_RETURN_IN_COUNT + C2C_RETURN_OUT_COUNT + C2C_WITHDRAW_IN_COUNT +C2C_WITHDRAW_OUT_COUNT) AVG_C2C  ";
                        selectQuery += " FROM DAILY_CHNL_TRANS_MAIN MC, USERS U WHERE MC.USER_ID = U.USER_ID AND U.USER_TYPE='CHANNEL' AND U.STATUS='Y' AND MC.TRANS_DATE >= ? AND MC.TRANS_DATE <= ? and U.NETWORK_CODE=? GROUP BY U.USER_NAME,U.LOGIN_ID,U.MSISDN ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(rs.getString("USER_NAME") + ":" + rs.getString("LOGIN_ID") + ":" + rs.getString("MSISDN") + ":" + rs.getLong("AVG_C2C"));
                        }
                        p_map.put("AVG_NO_C2C_PER_CHNL_USERS", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 10:
                    {
                        // Concentration of distribution to measure the
                        // concentration of
                        // air time distribution per country -ok
                        // #Distribution 60% KPI : number of active RP2P users
                        // which
                        // contributes to 60% on the RP2P amount per month
                        // #Distribution 80% KPI : number of active RP2P users
                        // which
                        // contributes to 80% on the RP2P amount per month
                        String pctCont = BTSLUtil.NullToString(Constants.getProperty("DISTRIBUTION_PCT")).trim();
                        int pct = 50;
                        if (!BTSLUtil.isNullString(pctCont)) {
                            final String str[] = pctCont.split(",");
                            for (int i = 0, j = str.length; i < j; i++) {
                                pctCont = str[i].trim();
                                if (BTSLUtil.isNumeric(pctCont)) {
                                    pct = Integer.parseInt(pctCont);
                                    if (pct >= 1 && pct <= 100) {
                                        final HashMap pctMap = getDistributionList(p_con, pct, p_networkCode);
                                        p_map.put("PCT_CONTRIBUTION-" + pct, pctMap);
                                    } else {
                                        throw new BTSLBaseException("DISTRIBUTION_PCT value should be 1 to 100");
                                    }
                                } else {
                                    throw new BTSLBaseException("DISTRIBUTION_PCT value should be numaric");
                                }
                            }
                        }

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 11:
                    {
                        // If more than 1 last month the put the value as comma
                        // seperated, value should be positive integer -ok
                        // #Number of active retailers in last 3 months
                        // #Number of active retailers in last 6 months
                        String actUsers = BTSLUtil.NullToString(Constants.getProperty("NO_OF_ACTIVE_CHNL_USER_LAST_DURATION")).trim();
                        int pct = 6;
                        if (!BTSLUtil.isNullString(actUsers)) {
                            final String str[] = actUsers.split(",");
                            for (int i = 0, j = str.length; i < j; i++) {
                                actUsers = str[i].trim();
                                if (BTSLUtil.isNumeric(actUsers)) {
                                    pct = Integer.parseInt(actUsers);
                                    if (pct >= 1 && pct <= 12) {
                                        final HashMap datemap = getDates(p_frequency, pct);
                                        final long activeUsers = getActiveChnlUsrList(p_con, p_networkCode, (Date) datemap.get("FROM_DATE"), (Date) datemap.get("TO_DATE"));
                                        datemap.put("ACTIVE_USERS", activeUsers + "");
                                        p_map.put("ACTIVE_USERS-" + pct, datemap);
                                    } else {
                                        throw new BTSLBaseException("NO_OF_ACTIVE_CHNL_USER_LAST_DURATION value should be 1 to 12");
                                    }
                                } else {
                                    throw new BTSLBaseException("NO_OF_ACTIVE_CHNL_USER_LAST_DURATION value should be numaric");
                                }
                            }
                        }
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 12:
                    {
                        // Average commission per retailer (O2C et C2C) in last
                        // month
                    	selectQuery = kpiProcessQry.fetchKPIDataAvgCommQry();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(rs.getString("USER_NAME") + ":" + rs.getString("LOGIN_ID") + ":" + rs.getString("MSISDN") + ":" + PretupsBL.getDisplayAmount(rs
                                .getLong("avg_com")) + ":" + rs.getString("PRODUCT_CODE"));
                        }
                        p_map.put("AVG_COMM_PER_CHNL_USERS", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 13:
                    {
                        // Average bonus per C2S transaction in last month
                        selectQuery = "SELECT U.USER_NAME,U.LOGIN_ID,U.MSISDN,AVG(DC.RECEIVER_BONUS) avg_bonus ";
                        selectQuery += " FROM DAILY_C2S_TRANS_DETAILS DC,USERS U WHERE U.USER_ID=DC.USER_ID ";
                        selectQuery += " AND U.STATUS='Y' AND DC.TRANS_DATE>=? AND DC.TRANS_DATE<=? and U.NETWORK_CODE=? ";
                        selectQuery += " GROUP BY U.USER_NAME,U.LOGIN_ID,U.MSISDN ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        int i=0;
                        i++;
                        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        i++;
                        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        i++;
                        pstmtSelect.setString(i, p_networkCode);
                        
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(rs.getString("USER_NAME") + ":" + rs.getString("LOGIN_ID") + ":" + rs.getString("MSISDN") + ":" + PretupsBL.getDisplayAmount(rs
                                .getLong("avg_bonus")));
                        }
                        p_map.put("AVG_BONUS_PER_CHNL_USERS", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 14:
                    {
                        // Average bonus per P2P transaction in last month
                        selectQuery = "SELECT avg(BONUS_AMOUNT) avg from DAILY_TRANSACTION_SUMMARY WHERE TRANS_DATE>=? AND TRANS_DATE<=? and SENDER_NETWORK_CODE=? ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            p_map.put("P2P_AVG_BONUS", PretupsBL.getDisplayAmount(rs.getLong("avg")));
                        }
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 15:
                    {
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 16:
                    {
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 17:
                    {
                        // The total air-time concretely transferred to
                        // end-users C2S
                        // (with generated bonus).
                        selectQuery = "SELECT TRANS_DATE, SUM(SENDER_TRANSFER_AMOUNT)SENDER_TRANSFER_AMOUNT, SUM(RECEIVER_CREDIT_AMOUNT)RECEIVER_CREDIT_AMOUNT, ";
                        selectQuery += " SUM(RECEIVER_BONUS)RECEIVER_BONUS, SUM(TRANSACTION_AMOUNT)TRANSACTION_AMOUNT, SUM(TRANSACTION_COUNT)TRANSACTION_COUNT ";
                        selectQuery += " FROM DAILY_C2S_TRANS_DETAILS D, Users U WHERE U.USER_ID=D.USER_ID and D.TRANS_DATE>=? and D.TRANS_DATE<=? and U.NETWORK_CODE=? ";
                        selectQuery += " GROUP BY TRANS_DATE ORDER BY TRANS_DATE ";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        int i=0;
                        i++;
                        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        i++;
                        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        i++;
                        pstmtSelect.setString(i, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(BTSLUtil.getDateStringFromDate(rs.getDate("TRANS_DATE")) + ":" + rs.getLong("TRANSACTION_COUNT") + ":" + PretupsBL.getDisplayAmount(rs
                                .getLong("TRANSACTION_AMOUNT")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("SENDER_TRANSFER_AMOUNT")) + ":" + PretupsBL.getDisplayAmount(rs
                                .getLong("RECEIVER_CREDIT_AMOUNT")) + ":" + PretupsBL.getDisplayAmount(rs.getLong("RECEIVER_BONUS")));
                        }
                        p_map.put("TOTAL_C2S_TXT_DATE_WISE", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 18:
                    {
                        // The sum of the revenues collected on the Head of
                        // Channels O2C
                        // (PAYABLE AMOUNT = RP2P REVENUE for the Orange
                        // affiliate)-ok
                        selectQuery = "SELECT domain_name,SUM(O2C_TRANSFER_IN_AMOUNT) O2C_TRANSFER_IN_AMOUNT from DAILY_CHNL_TRANS_MAIN, domains ";
                        selectQuery += " where domain_code=SENDER_DOMAIN_CODE and TRANS_DATE>=? and TRANS_DATE<=? and NETWORK_CODE=? group by domain_name";
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(rs.getString("domain_name") + ":" + PretupsBL.getDisplayAmount(rs.getLong("O2C_TRANSFER_IN_AMOUNT")));

                        }
                        p_map.put("TOTAL_REVENUES", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 19:
                    {
                    	selectQuery = kpiProcessQry.fetchKPIDataSumCommQry();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchKPIData", " selectQuery=" + selectQuery);
                        }
                        try{
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
                        pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
                        pstmtSelect.setString(3, p_networkCode);
                        rs = pstmtSelect.executeQuery();
                        list = new ArrayList();
                        while (rs.next()) {
                            list.add(rs.getString("domain_name") + ":" + rs.getString("PRODUCT_CODE") + ":" + PretupsBL.getDisplayAmount(rs.getLong("sum_com")));
                        }
                        p_map.put("SUM_COMM_DOMAIN_WISE", list);
                        pstmtSelect.clearParameters();
                        }
                        finally{
                        	if(rs!=null)
                        		rs.close();
                        	if(pstmtSelect!=null)
                        		pstmtSelect.close();
                        }
                        rs = null;
                        pstmtSelect = null;

                        if (p_kpiID != -1) {
                            break;
                        }
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 20:
                    {
                        if (p_kpiID != -1) {
                            break;
                        }
                    }

            }
        } catch (SQLException sqle) {
            _logger.error("fetchKPIData", "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "fetchKPIData", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _logger.error("fetchKPIData", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "fetchKPIData", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchKPIData", " Entered:  _fromDate=" + _fromDate + " _toDate=" + _toDate + " map=" + p_map);
            }
        }// end of finally
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getFileName
     * 
     * @param p_frequency
     * @param p_networkCode
     * @throws BTSLBaseException
     * @throws Exception
     *             Return :-String
     *             Nov 30, 2009 9:11:58 AM
     */
    private String getFileName(String p_frequency, String p_networkCode) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "getFileName";
        String filename = null;
        try {
            final String filePath = Constants.getProperty("DOWNLOAD_KPI_REPORT_PATH") + File.separator + p_networkCode + File.separator;
            try {
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                _logger.error("getFileName", "Exception" + e.getMessage());
                throw new BTSLBaseException(this, "getFileName", "Exception when diretory creation KPI_FILE_DIRECTORY_PATH=" + filePath);

            }
            if (BTSLUtil.isNullString(p_frequency)) {
                filename = filePath + p_networkCode + "-" + BTSLUtil.NullToString(Constants.getProperty("KPI_FILE_NAME_PREFIX")).trim() + "_" + BTSLUtil
                    .getFileNameStringFromDate(new Date()) + ".xls";

            } else {
                filename = filePath + p_networkCode + "-" + BTSLUtil.NullToString(Constants.getProperty("KPI_FILE_NAME_PREFIX")).trim() + "_" + p_frequency + "_" + BTSLUtil
                    .getFileNameStringFromDate(new Date()) + ".xls";
            }
        } catch (BTSLBaseException e) {
            _logger.errorTrace(METHOD_NAME, e);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        }
        return filename;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getTwoDigit
     * 
     * @param p_no
     *            Return :-String
     *            Nov 30, 2009 9:10:50 AM
     */
    private String getTwoDigit(int p_no) {
        String digit = "";
        if (p_no <= 9) {
            digit = "0" + p_no;
        } else {
            digit = p_no + "";
        }
        return digit;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getDistributionList
     * 
     * @param p_con
     * @param p_pct
     * @param p_networkCode
     *            TODO
     * @throws BTSLBaseException
     *             Return :-HashMap
     *             Nov 27, 2009 2:51:16 PM
     */
    private HashMap getDistributionList(Connection p_con, int p_pct, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "getDistributionList";
        ArrayList list = null;
        ArrayList finalList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String selectQuery = null;
        final HashMap map = new HashMap();
        try {
        	String str = null;
        	list = new ArrayList();
            selectQuery = "SELECT U.user_id,U.user_name,U.login_id,U.msisdn, SUM(sender_transfer_amount) sender_transfer_amount from DAILY_C2S_TRANS_DETAILS MC,users U";
            selectQuery += " where TRANS_DATE>=? and TRANS_DATE<=? and U.user_id=MC.user_id and U.status='Y' and U.NETWORK_CODE=?  group by U.user_id,U.user_name,U.msisdn,U.login_id";
            selectQuery += " order by sender_transfer_amount desc";
            if (_logger.isDebugEnabled()) {
                _logger.debug("getDistributionList", " selectQuery=" + selectQuery);
            }
            try{
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int m=0;
            m++;
            pstmtSelect.setDate(m, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
            m++;
            pstmtSelect.setDate(m, BTSLUtil.getSQLDateFromUtilDate(_toDate));
            m++;
            pstmtSelect.setString(m, p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                str = null;
                str = rs.getString("user_name") + ":" + rs.getString("login_id") + ":" + rs.getString("msisdn") + ":" + PretupsBL.getDisplayAmount(rs
                    .getLong("sender_transfer_amount"));
                list.add(str);
             }
            pstmtSelect.clearParameters();
            }
            finally{
            	if(rs!=null)
            		rs.close();
            	if(pstmtSelect!=null)
            		pstmtSelect.close();
            }
            rs = null;
            pstmtSelect = null;
            selectQuery = null;
            str = null;
            selectQuery = "select SUM(SENDER_TRANSFER_AMOUNT) totalMontAmt from DAILY_C2S_TRANS_DETAILS where TRANS_DATE>=? and TRANS_DATE<=?";
            try{
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(_fromDate));
            pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(_toDate));
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                str = PretupsBL.getDisplayAmount(rs.getLong("totalMontAmt"));
            }
            final double pctAmt = (Double.parseDouble(str) * p_pct) / 100;
            final double totalAmt = Double.parseDouble(str);
            finalList = new ArrayList();
            map.put("PCT_AMT", pctAmt + "");
            map.put("TOTAL_RECHARGE_AMT", str);
            String strArr[] = null;
            double amt = 0.0;
            double userAmt = 0.0;
            double contribution = 0.0;
            for (int i = 0, j = list.size(); i < j; i++) {
                str = (String) list.get(i);
                strArr = str.split(":");
                userAmt = Double.parseDouble(strArr[3]);
                amt += userAmt;
                contribution = (100 * userAmt) / totalAmt;
                str += ":" + contribution;
                finalList.add(str);
                if (amt >= pctAmt) {
                    break;
                }
            }
            map.put("PCT", p_pct + "");
            map.put("TOTAL_LIST", list);
            map.put("TOTAL_PCT_CONTRIBUTION_LIST", finalList);
            pstmtSelect.clearParameters();
            }
            finally{
            	if(rs!=null)
            		rs.close();
            	if(pstmtSelect!=null)
            		pstmtSelect.close();
            }
            rs = null;
            pstmtSelect = null;
            selectQuery = null;
            str = null;
        } catch (SQLException e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("getDistributionList", "SQLException" + e.getMessage());
            throw new BTSLBaseException(this, "getDistributionList", "SQLException " + e.getMessage());
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("getDistributionList", "Exception" + e.getMessage());
            throw new BTSLBaseException(this, "getDistributionList", "Exception " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        return map;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getNetworkList
     * 
     * @param p_con
     * @throws BTSLBaseException
     *             Return :-ArrayList
     *             Nov 30, 2009 9:27:50 AM
     */
    private ArrayList getNetworkList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "getNetworkList";
        ArrayList list = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String selectQuery = null;
        try {
            selectQuery = "SELECT NETWORK_CODE, NETWORK_NAME FROM NETWORKS WHERE STATUS='Y'";
            if (_logger.isDebugEnabled()) {
                _logger.debug("getNetworkList", " selectQuery=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            list = new ArrayList();
            String str = null;
            while (rs.next()) {
                str = null;
                str = rs.getString("NETWORK_CODE") + ":" + rs.getString("NETWORK_NAME");
                list.add(str);
            }
        } catch (SQLException e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("getNetworkList", "SQLException" + e.getMessage());
            throw new BTSLBaseException(this, "getNetworkList", "SQLException " + e.getMessage());
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("getNetworkList", "Exception" + e.getMessage());
            throw new BTSLBaseException(this, "getNetworkList", "Exception " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        return list;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getActiveChnlUsrList
     * 
     * @param p_con
     * @param p_frequency
     * @param p_networkCode
     * @throws BTSLBaseException
     *             Return :-long
     *             Nov 30, 2009 9:58:45 AM
     */
    private long getActiveChnlUsrList(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "getActiveChnlUsrList";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String selectQuery = null;
        long count = 0;
        try {
            selectQuery = "SELECT COUNT(user_id) user_count from DAILY_CHNL_TRANS_MAIN WHERE TRANS_DATE>=? AND TRANS_DATE<=? AND NETWORK_CODE=? ";
            selectQuery += " AND (C2C_RETURN_IN_COUNT+C2C_RETURN_OUT_COUNT+C2C_TRANSFER_IN_COUNT+C2C_TRANSFER_OUT_COUNT+C2C_WITHDRAW_IN_COUNT+C2C_WITHDRAW_OUT_COUNT+C2S_TRANSFER_OUT_COUNT)>0";
            if (_logger.isDebugEnabled()) {
                _logger.debug("getActiveChnlUsrList", " selectQuery=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i=0;
            i++;
            pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            i++;
            pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            i++;
            pstmtSelect.setString(i, p_networkCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                count = rs.getLong("user_count");
            }
        } catch (SQLException e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("getActiveChnlUsrList", "SQLException" + e.getMessage());
            throw new BTSLBaseException(this, "getActiveChnlUsrList", "SQLException " + e.getMessage());
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("getActiveChnlUsrList", "Exception" + e.getMessage());
            throw new BTSLBaseException(this, "getActiveChnlUsrList", "Exception " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getActiveChnlUsrList", " Exiting:  count=" + count + " p_fromDate=" + p_fromDate + " p_toDate=" + p_toDate);
            }
        }
        return count;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getDates
     * 
     * @param p_frequency
     * @param p_lastNo
     * @throws BTSLBaseException
     * @throws Exception
     *             Return :-HashMap
     *             Nov 30, 2009 10:35:13 AM
     */
    private HashMap getDates(String p_frequency, int p_lastNo) throws BTSLBaseException {
        final String METHOD_NAME = "getDates";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getDates", " Entered:  _reportDate=" + _reportDate + " p_lastNo=" + p_lastNo + " p_frequency=" + p_frequency);
        }
        final HashMap dateMap = new HashMap();
        try {
            final Calendar reporCaldate = BTSLDateUtil.getInstance();
            p_frequency = p_frequency.trim().toUpperCase();
            if ("MONTHLY".equals(p_frequency)) {
                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.MONTH, -1);
                int endday = reporCaldate.getActualMaximum(Calendar.DAY_OF_MONTH);
                int actualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                int year = reporCaldate.get(Calendar.YEAR);
                dateMap.put("TO_DATE", BTSLUtil.getDateFromDateString(getTwoDigit(endday) + "/" + getTwoDigit(actualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY));

                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.MONTH, -p_lastNo);
                endday = reporCaldate.getActualMaximum(Calendar.DAY_OF_MONTH);
                actualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                year = reporCaldate.get(Calendar.YEAR);
                dateMap.put("FROM_DATE", BTSLUtil.getDateFromDateString("01/" + getTwoDigit(actualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY));
            } else if ("WEEKLY".equals(p_frequency)) {
                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.WEEK_OF_YEAR, -p_lastNo);
                reporCaldate.set(Calendar.DAY_OF_WEEK, 1);
                final int startday = reporCaldate.get(Calendar.DATE);
                final int startactualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                int year = reporCaldate.get(Calendar.YEAR);
                dateMap.put("FROM_DATE", BTSLUtil.getDateFromDateString(getTwoDigit(startday) + "/" + getTwoDigit(startactualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY));

                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.WEEK_OF_YEAR, -1);
                reporCaldate.set(Calendar.DAY_OF_WEEK, 7);
                final int endday = reporCaldate.get(Calendar.DATE);
                final int actualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                year = reporCaldate.get(Calendar.YEAR);
                dateMap.put("TO_DATE", BTSLUtil.getDateFromDateString(getTwoDigit(endday) + "/" + getTwoDigit(actualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY));
            } else if ("DAILY".equals(p_frequency)) {
                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.DATE, -p_lastNo);
                final int startday = reporCaldate.get(Calendar.DATE);
                final int startactualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                int year = reporCaldate.get(Calendar.YEAR);
                dateMap.put("FROM_DATE", BTSLUtil.getDateFromDateString(getTwoDigit(startday) + "/" + getTwoDigit(startactualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY));

                reporCaldate.setTime(_reportDate);
                reporCaldate.add(Calendar.DATE, -1);
                final int endday = reporCaldate.get(Calendar.DATE);
                final int actualmonth = reporCaldate.get(Calendar.MONTH) + 1;
                year = reporCaldate.get(Calendar.YEAR);
                dateMap.put("TO_DATE", BTSLUtil.getDateFromDateString(getTwoDigit(endday) + "/" + getTwoDigit(actualmonth) + "/" + year, PretupsI.DATE_FORMAT_DDMMYYYY));
            } else {
                throw new BTSLBaseException(this, "setDates", "REPORT_FREQUENCY value should be DAILY/MONTHLY/WEEKLY =" + p_frequency);
            }
        } catch (BTSLBaseException e) {
            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("setDates", " Exiting:  dateMap=" + dateMap + " frequency=" + p_frequency);
            }
        }
        return dateMap;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getKPIIndex
     * 
     * @throws BTSLBaseException
     *             Return :-int[]
     *             Dec 1, 2009 3:16:48 PM
     */
    public ArrayList getKPIIndex() throws BTSLBaseException {
        final String METHOD_NAME = "getKPIIndex";
        String kpiIndexs = null;
        ArrayList indexList = null;
        try {

            kpiIndexs = BTSLUtil.NullToString(Constants.getProperty("KPI_GENERATE_SEQUENCE_NO")).trim();
            if (!BTSLUtil.isNullString(kpiIndexs)) {
                indexList = new ArrayList();
                final String[] indexs = kpiIndexs.split(",");

                for (int i = 0, j = indexs.length; i < j; i++) {
                    if (BTSLUtil.isNumeric(indexs[i].trim())) {
                        indexList.add(indexs[i].trim());
                    }
                }
            }
        } catch (Exception e1) {
            _logger.errorTrace(METHOD_NAME, e1);
            throw new BTSLBaseException(this, "getKPIIndex", "KPI_GENERATE_SEQUENCE_NO value should be integer =" + kpiIndexs);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getKPIIndex", " Exiting:  indexList=" + indexList + " kpiIndexs=" + kpiIndexs);
            }
        }
        return indexList;

    }
}
