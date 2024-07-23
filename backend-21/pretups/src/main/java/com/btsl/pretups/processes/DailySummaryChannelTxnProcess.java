package com.btsl.pretups.processes;

/*
 * @# DailySummaryChannelTxnProcess.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * -----------------------
 * Shashank Shukla Nov 23, 2010 Airtel Nigeria, of summary report
 * sto the Distributors or channel Users
 * .
 * ------------------------------------------------------------------------------
 * -----------------------
 * Copyright(c) 2005 Comviva Ltd.
 */

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.UserInformationVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class DailySummaryChannelTxnProcess {
    private static Log _log = LogFactory.getLog(DailySummaryChannelTxnProcess.class.getName());
    public static final String USR_SUCCESS_MSG = "15900";
    private static boolean _categoryALL = false;
    private static String _category[];
    private static PreparedStatement _pstmtSelBalance = null;
    private static PreparedStatement _pstmtSelectC2C = null;
    private static PreparedStatement _pstmtSelectTopup = null;
    private static PreparedStatement _pstmtSelectBilling = null;
    private static Date _enteredDate = null;
    private static Date _startDate = null;
    private static Date _lastmonthdate = null;
    private static Date _lastmonthstartDate = null;
    
    /**
     * ensures no instantiation
     */
    private DailySummaryChannelTxnProcess(){
    	
    }

    /**
     * @
     * Description :-
     * Author :Shashank Shukla
     * Method :-Main
     * 
     * @param * arg[0]=Constants.properties file path
     *        arg[1]=Log config file path
     *        arg[3]=User Category
     *        arg[4]=Date entered by the user
     */
    /************************************ main ******************************************/

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        final String METHOD_NAME = "main";
        try {
            if (args.length < 3) {
                _log.error("DailySummaryChannelTxnProcess main()",
                    " Usage : DailySummaryChannelTxnProcess [Constants.props] [ProcessLogConfig.props] [Category Code][transfer date (optional)]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailySummaryChannelTxnProcess[main]", "", "", "",
                    "Improper usage. Usage : DailySummaryChannelTxnProcess [DailySummaryChannel Constants file] [DailySummaryChannel LogConfig file] [Categeory Code] [Date]");
                throw new BTSLBaseException("DailySummaryChannelTxnProcess ", " main ", "Arguments are not sufficient");// change
                // error
                // code
            }

            try {
                if (args.length == 4 && !BTSLUtil.isNullString(args[3])) {
                    _enteredDate = (BTSLUtil.getDateFromDateString(args[3]));
                } else {
                    _enteredDate = BTSLUtil.addDaysInUtilDate(new Date(), -1);
                    _enteredDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(_enteredDate));

                }
            } catch (ParseException pe) {
                _log.error("process", "Error occurred during processing records .Database not updated. BTSLBaseException : " + pe.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[main]", "", "", "",
                    "SQL Exception:" + pe.getMessage());
                _log.errorTrace(METHOD_NAME, pe);
                throw new BTSLBaseException("DailySummaryChannelTxnProcess ", " main ", "Date is not in proper format");// change
                // error
                // code
            }
            // Starting date of current month
            _startDate = BTSLUtil.addDaysInUtilDate(_enteredDate, (-_enteredDate.getDate() + 1));
            final Calendar calen = BTSLDateUtil.getInstance();
            calen.setTime(_enteredDate);
            calen.add(Calendar.MONTH, -1);
            _lastmonthdate = calen.getTime();

            // Starting date of last month
            _lastmonthstartDate = BTSLUtil.addDaysInUtilDate(_lastmonthdate, (-_lastmonthdate.getDate() + 1));

            // Categeory code validation
            if (args[2].contains(",")) {
                _category = args[2].split(",");
            } else if ("ALL".equalsIgnoreCase(args[2].trim())) {
                _categoryALL = true;
            } else {
                _category = new String[1];
                _category[0] = args[2].trim();
            }
            DailySummaryChannelTxnProcess.process(args);
        } catch (BTSLBaseException be) {
            _log.error("main", " : Exiting BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _log.error("main ", ": Exiting Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess: main", "", "", "",
                "Exiting the exception of main");
            _log.errorTrace(METHOD_NAME, e);
        }// end of outer Exception
        finally {
            final long endTime = System.currentTimeMillis();
            _log.info("DailySummaryChannelTxnProcess", "Main method Exiting .......Total time =" + (endTime - startTime));
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Description :-
     * Author :-shashank.shukla
     * Method :-process
     * 
     * @param p_args
     * @throws BTSLBaseException
     *             Return :-void
     *             Nov 25, 2010 11:39:06 AM
     */
    /************************************************** process method ************************************/
    public static void process(String[] p_args) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered p_args: " + p_args);
        }
        Connection con = null;
        ResultSet rs = null;
        try {
            // load Constants.props and ProccessLogConfig file
            loadCachesAndLogFiles(p_args[0], p_args[1]);

            // opening the connection
            con = OracleUtil.getSingleConnection();
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
            if (con == null) {

                _log.error("process ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "DailySummaryChannelTxnProcess[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("DailySummaryChannelTxnProcess", "process", "Error in getting connection");
            }
            try {
                int index = 1;
                DailySummaryChannelTxnProcessQry selectFromUserDailyBalance = (DailySummaryChannelTxnProcessQry) ObjectProducer.getObject(QueryConstants.DAILY_SUMMARY_CHANNEL_TXN_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
                String  balanceQuery = selectFromUserDailyBalance.selectFromUserDailyBalance(_categoryALL, _category);
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Query for opening and closing balance=" + balanceQuery);
                }
                _pstmtSelBalance = con.prepareStatement(balanceQuery.toString());
                _pstmtSelBalance.setString(index++, PretupsI.USER_STATUS_ACTIVE);
                _pstmtSelBalance.setString(index++, PretupsI.USER_TYPE_CHANNEL);
                // changed the query in order to remove SQL injection attack
                if (!_categoryALL) {
                    for (int i = 0; i < _category.length; i++) {
                        _pstmtSelBalance.setString(index++, _category[i]);
                    }
                }
                // end of change
                _pstmtSelBalance.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(_enteredDate, -1)));
                _pstmtSelBalance.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(_enteredDate));
                rs = _pstmtSelBalance.executeQuery();

                // C2C/O2C query
                final StringBuffer selectChnlTxn = new StringBuffer();
                selectChnlTxn.append("SELECT SUM(CTI.approved_quantity)APPROVED_QUANTITY, CT.type ");
                selectChnlTxn.append("FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI");
                selectChnlTxn.append(" WHERE CT.transfer_id=CTI.transfer_id");
                selectChnlTxn.append(" AND CTI.product_code=?");
                selectChnlTxn.append(" AND ((CT.from_user_id=? and CT.type='C2C' and CT.transfer_sub_type='T') or (CT.to_user_id=?");
                selectChnlTxn.append(" AND CT.type='O2C' and ct.transfer_sub_type='T'))");
                selectChnlTxn.append(" AND CT.status='CLOSE'and ct.transfer_date=? GROUP BY CT.TYPE");
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Query for Channel TXN =" + selectChnlTxn.toString());
                }
                _pstmtSelectC2C = con.prepareStatement(selectChnlTxn.toString());

                // Query for retreiving C2S TXN Amount for particular channel
                // USER

                final StringBuffer selectC2Stxn = new StringBuffer();
                //local_index_implemented
                selectC2Stxn.append("SELECT SUM(sender_transfer_value)SENDER_TRANSFER_VALUE, ST.name ");
                selectC2Stxn.append(" FROM C2S_TRANSFERS CT, SERVICE_TYPE ST");
                selectC2Stxn.append(" WHERE CT.transfer_date=? AND ST.service_type=CT.service_type");
                selectC2Stxn.append(" AND CT.sender_id=?");
                selectC2Stxn.append(" AND CT.transfer_status='200'GROUP BY ST.name");
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Query for C2S TXN =" + selectC2Stxn.toString());
                }
                _pstmtSelectTopup = con.prepareStatement(selectC2Stxn.toString());

                // O2C for this month till current date and last month from 1 to
                // current date

                final StringBuffer selectO2CMonthly = new StringBuffer();
                selectO2CMonthly.append("SELECT SUM(CTI.approved_quantity)APPROVED_QUANTITY");
                selectO2CMonthly.append(" FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI");
                selectO2CMonthly.append(" WHERE CT.transfer_id=CTI.transfer_id");
                selectO2CMonthly.append(" AND CTI.product_code=?");
                selectO2CMonthly.append(" AND CT.to_user_id=? ");
                selectO2CMonthly.append(" AND CT.type='O2C'");
                selectO2CMonthly.append(" AND CT.transfer_sub_type='T'");
                selectO2CMonthly.append(" AND CT.status='CLOSE'");
                selectO2CMonthly.append(" AND CT.transfer_date>=? ");
                selectO2CMonthly.append(" AND CT.transfer_date<=?");
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Query for O2C Monthly comparision =" + selectO2CMonthly.toString());
                }
                _pstmtSelectBilling = con.prepareStatement(selectO2CMonthly.toString());

                BTSLMessages messages = null;
                PushMessage pushMessage = null;
                final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                final String[] arr = new String[9];
                UserInformationVO informationVO = null;

                while (rs.next()) {
                    informationVO = new UserInformationVO();
                    informationVO.setUserid(rs.getString("USER_ID"));
                    informationVO.setMsisdn(rs.getString("MSISDN"));
                    informationVO.setOpeningBalance(rs.getLong("OPENING_BALANCE"));
                    informationVO.setClosingBal(rs.getLong("CLOSING_BAL"));
                    informationVO.setProductCode(rs.getString("PRODUCT_CODE"));
                    getC2CFromTable(informationVO);
                    informationVO.setTopupAmt(getTopupAmmount(informationVO.getUserid()));
                    informationVO.setBillingMTD(getBillingAmt(informationVO.getUserid(), informationVO.getProductCode(), _startDate, _enteredDate));
                    informationVO.setBillingLMTD(getBillingAmt(informationVO.getUserid(), informationVO.getProductCode(), _lastmonthstartDate, _lastmonthdate));
                    arr[0] = BTSLUtil.getDateStringFromDate(_enteredDate);
                    arr[1] = informationVO.getMsisdn();
                    arr[2] = PretupsBL.getDisplayAmount(informationVO.getOpeningBalance());
                    arr[3] = PretupsBL.getDisplayAmount(informationVO.getC2CTxn());
                    arr[4] = PretupsBL.getDisplayAmount(informationVO.getTopupAmt());
                    arr[5] = PretupsBL.getDisplayAmount(informationVO.getClosingBal());
                    arr[6] = PretupsBL.getDisplayAmount(informationVO.getBilling());
                    arr[7] = PretupsBL.getDisplayAmount(informationVO.getBillingMTD());
                    arr[8] = PretupsBL.getDisplayAmount(informationVO.getBillingLMTD());
                    messages = new BTSLMessages(DailySummaryChannelTxnProcess.USR_SUCCESS_MSG, arr);
                    pushMessage = new PushMessage(informationVO.getMsisdn(), messages, "", "", locale, "");
                    // push SMS
                    pushMessage.push();
                    informationVO = null;
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailySummaryChannelTxn[process]", "", "", "",
                    " DailySummaryChannelTxnProcess has been executed successfully.");
            } catch (SQLException sqe) {
                _log.error("process", "SQLException:" + sqe.getMessage());
                _log.errorTrace(METHOD_NAME, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[process]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                throw new BTSLBaseException("", "process", "error.general.sql.processing");
            }
        } catch (BTSLBaseException be) {
            _log.error("process", "Error occurred during processing records .Database not updated. BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[process]", "", "", "",
                "Error occurred during processing records. Database not updated. BTSLBaseException : " + be.getMessage());
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.errorTrace(METHOD_NAME, be);
            return;
        } catch (Exception e) {
            _log.error("process", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[process]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailySummaryChannelTxnProcess", "process", "error.general.processing");
        }// end of Exception

        finally {
            // closing database connection
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            	_log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_pstmtSelBalance != null) {
                    _pstmtSelBalance.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting:");
            }
        }// end of finally

    }

    public static void getC2CFromTable(UserInformationVO infoVO) throws BTSLBaseException {
        final String METHOD_NAME = "getC2CFromTable";
        if (_log.isDebugEnabled()) {
            _log.debug(" getC2CFromTable ", " Entered with infoVO=" + infoVO);
        }
        int index = 1;
        ResultSet rs = null;
        try {
            _pstmtSelectC2C.setString(index++, infoVO.getProductCode());
            _pstmtSelectC2C.setString(index++, infoVO.getUserid());
            _pstmtSelectC2C.setString(index++, infoVO.getUserid());
            _pstmtSelectC2C.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(_enteredDate));
            rs = _pstmtSelectC2C.executeQuery();
            String type;
            if (rs != null) {
                while (rs.next()) {
                    // Set C2C value here
                    type = rs.getString("TYPE");
                    if ("C2C".equals(type)) {
                        infoVO.setC2CTxn(rs.getLong("APPROVED_QUANTITY"));
                    } else {
                        infoVO.setBilling(rs.getLong("APPROVED_QUANTITY"));
                    }
                }
            }
            _pstmtSelectC2C.clearParameters();
        } catch (SQLException sqe) {
            // TODO Auto-generated catch block
            _log.error("getC2CFromTable", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[getC2CFromTable]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DailySummaryChannelTxnProcess", "getC2CFromTable", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("getC2CFromTable", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[getC2CFromTable]", "",
                "", "", "Exception:" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            if (_log.isDebugEnabled()) {
                _log.debug(" getC2CFromTable ", " Exit with infoVO=" + infoVO);
            }
        }
    }

    public static Long getTopupAmmount(String userId) throws BTSLBaseException {
        final String METHOD_NAME = "getTopupAmmount";
        if (_log.isDebugEnabled()) {
            _log.debug(" getTopupAmmount ", " Entered with userId=" + userId);
        }
        int index = 1;
        long _topupAmt = 0;
        ResultSet rs = null;
        try {
            _pstmtSelectTopup.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(_enteredDate));
            _pstmtSelectTopup.setString(index++, userId);
            rs = _pstmtSelectTopup.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    // Set C2S value here
                    _topupAmt = rs.getLong("SENDER_TRANSFER_VALUE");
                }
            }
            _pstmtSelectTopup.clearParameters();
        } catch (SQLException sqe) {
            // TODO Auto-generated catch block
            _log.error("getC2CFromTable", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[getTopupAmmount]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DailySummaryChannelTxnProcess", "getTopupAmmount", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("getTopupAmmount", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[getTopupAmmount]", "",
                "", "", "Exception:" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            if (_log.isDebugEnabled()) {
                _log.debug(" getTopupAmmount ", " Exit with userId=" + userId);
            }
        }
        return _topupAmt;

    }

    /**
     * @param userId
     * @param productCode
     * @param fromDate
     * @param toDate
     * @return
     */
    public static long getBillingAmt(String userId, String productCode, Date fromDate, Date toDate) throws BTSLBaseException {
        final String METHOD_NAME = "getBillingAmt";
        if (_log.isDebugEnabled()) {
            _log.debug(" getBillingAmt ", " Entered with userId=" + userId + " productCode=" + productCode + " fromDate=" + fromDate + " toDate=" + toDate);
        }
        int index = 1;
        long _billing = 0;
        ResultSet rs = null;

        try {
            _pstmtSelectBilling.setString(index++, userId);
            _pstmtSelectBilling.setString(index++, productCode);
            _pstmtSelectBilling.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            _pstmtSelectBilling.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(toDate));

            rs = _pstmtSelectBilling.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    // Set C2C value here
                    _billing = (rs.getLong("APPROVED_QUANTITY"));
                }
            }
            _pstmtSelectBilling.clearParameters();
        } catch (SQLException sqe) {
            // TODO Auto-generated catch block
            _log.error("getBillingAmt", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[getBillingAmt]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DailySummaryChannelTxnProcess", "getBillingAmt", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("getBillingAmt", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[getBillingAmt]", "", "",
                "", "Exception:" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            if (_log.isDebugEnabled()) {
                _log.debug(" getBillingAmt ", " Exit with userId=" + userId + " productCode=" + productCode + " fromDate=" + fromDate + " toDate=" + toDate);
            }
        }

        return _billing;

    }

    /**
     * This method loads the Constants.props and ProccessLogConfig file and
     * checks whether the process is already running or not
     * 
     * @param arg1
     * @param arg2
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void loadCachesAndLogFiles(String p_arg1, String p_arg2) throws BTSLBaseException {
        final String METHOD_NAME = "loadCachesAndLogFiles";
        if (_log.isDebugEnabled()) {
            _log.debug(" loadCachesAndLogFiles ", " Entered with p_arg1=" + p_arg1 + " p_arg2=" + p_arg2);
        }
        File logconfigFile = null;
        File constantsFile = null;
        try {
            constantsFile = new File(p_arg1);
            if (!constantsFile.exists()) {

                _log.error("DailySummaryChannelTxnProcess[loadCachesAndLogFiles]", " Constants file not found on location:: " + constantsFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR,
                    "DailySummaryChannelTxnProcess[loadCachesAndLogFiles]", "", "", "", " The Constants file doesn't exists at the path specified. ");
                throw new BTSLBaseException("DailySummaryChannelTxnProcess ", " loadCachesAndLogFiles ", "Constants file not available at given path");
            }

            logconfigFile = new File(p_arg2);
            if (!logconfigFile.exists()) {

                _log.error("DailySummaryChannelTxnProcess[loadCachesAndLogFiles]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "DailySummaryChannelTxnProcess[loadCachesAndLogFiles]", "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
                throw new BTSLBaseException("DailySummaryChannelTxnProcess ", "loadCachesAndLogFiles ", "Log config not available at given path");
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (BTSLBaseException be) {
            _log.error("DailySummaryChannelTxnProcess[loadCachesAndLogFiles]", "BTSLBaseException =" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("DailySummaryChannelTxnProcess[loadCachesAndLogFiles]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySummaryChannelTxnProcess[loadCachesAndLogFiles]",
                "", "", "", "Exception=" + e.getMessage());
            throw new BTSLBaseException("DailySummaryChannelTxnProcess ", " loadCachesAndLogFiles ", "Error in processing daily summary");
        }// end of Exception
        finally {
            if (logconfigFile != null) {
                logconfigFile = null;
            }
            if (constantsFile != null) {
                constantsFile = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("DailySummaryChannelTxnProcess[loadCachesAndLogFiles]", " Exiting..........");
            }
        }// end of finally
    }
}
// end class
