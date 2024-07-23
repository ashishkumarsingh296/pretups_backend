package com.btsl.pretups.processes;

/**
 * @(#)DailyTransferDetailAlert.java
 * 
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Manisha Jain 19/02/2008 Initial Creation
 *                                   ------------------------------------------
 *                                   ------------------------------
 *                                   Copyright (c) 2008 Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 */
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.DailyTransferDetailsVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;

// This class is used to send sms to the channel user's primary mobile number
// according to the transaction done by user in the previous day.
public class DailyTransferDetailAlert {
    private static final Log _logger = LogFactory.getLog(DailyTransferDetailAlert.class.getName());

    /**
     * ensures no instantiation
     */
    private DailyTransferDetailAlert(){
    	
    }
    
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2) {
                System.out.println("Usage : DailyTransferDetailAlert [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("DailyTransferDetailAlert" + " Constants File Not Found .............");
                _logger.error("DailyTransferDetailAlert[main]", "Constants file not found on location: " + constantsFile.toString());
                return;
            }
            final File logconfigFile = Constants.validateFilePath(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("DailyTransferDetailAlert" + " Logconfig File Not Found .............");
                _logger.error("DailyTransferDetailAlert[main]", "Logconfig File not found on location: " + logconfigFile.toString());
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            return;
        } catch (Exception e) {
            _logger.error("main", " Exception : ...........................: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            return;
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", "Exiting..... ");
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
        ProcessBL processBL = null;
        ProcessStatusDAO processStatusDAO = null;
        Connection con = null;
        MComConnectionI mcomCon = null; // to get connection
        String processId = null; // to check process is under process or not
        int beforeInterval = 0; // check from which date process will be
        // executed
        Date processedUpto = null; // to which date process will be executed
        boolean statusOk = false; // to check process is under process or not
        Date currentDate = null; // current date
        boolean isDataProcessed = false; // check date is processed successfully
        // or not
        ProcessStatusVO processStatusVO = null;
        int updateCount = 0; // check process details are updated or not
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Entered");
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            processId = ProcessI.DAILY_TRANSFER_PROCESSID;
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt(processStatusVO.getBeforeInterval() / (60 * 24));
            if (statusOk) {
                // method call to find maximum date till which process has been
                // executed
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    currentDate = new Date();
                    currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
                    processedUpto = BTSLUtil.getSQLDateFromUtilDate(processedUpto);
                    // to check whether process has been executed till one day
                    // before current date or not
                    final String oldDate = BTSLUtil.getDateStringFromDate(processedUpto);
                    final String currDate = BTSLUtil.getDateStringFromDate(currentDate);
                    final int dateDiff = BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(oldDate), BTSLUtil.getDateFromDateString(currDate));
                    if (dateDiff <= 1) {
                        throw new BTSLBaseException("DailyTransferDetailAlert", "process", PretupsErrorCodesI.DAILY_MESSAGE_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    mcomCon.partialCommit();
                    processedUpto = BTSLUtil.addDaysInUtilDate(currentDate, -(beforeInterval + 1));
                    // call process for uploading transfer details
                    isDataProcessed = transferDetail(con, processedUpto);
                    if (isDataProcessed) {
                        processStatusVO.setExecutedUpto(processedUpto);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyTransferDetailAlert[process]", "", "",
                            "", " Daily transfer detail alert process has been executed successfully.");
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("process", "message sent successfully");
                        }
                    }
                } else {
                    throw new BTSLBaseException("DailyTransferDetailAlert", "process", PretupsErrorCodesI.DAILY_ALERT_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            } else {
                throw new BTSLBaseException("DailyTransferDetailAlert", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
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
            throw be;
        } catch (Exception e) {
            try {
                mcomCon.finalRollback();
            } catch (SQLException e1) {
                _logger.errorTrace(METHOD_NAME, e1);
            }
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyTransferDetailAlert[process]", "", "", "",
                " DailyTransferDetailAlert process could not be executed successfully.");
            throw new BTSLBaseException("DailyTransferDetailAlert", "process", PretupsErrorCodesI.ERROR_IN_DAILY_ALERT);
        } finally {
            try {
                if (statusOk) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    processStatusDAO = new ProcessStatusDAO();
                    updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
                    if (updateCount > 0) {
                       mcomCon.finalCommit();
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("DailyTransferDetailAlert#process");
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
     * @return boolean
     * @param Connection
     *            con
     * @param Date
     *            p_processingDate
     * @throws BTSLBaseException
     *             methods load user data (c2s transfers, channel transfers,
     *             opening ang closing balance )from database
     */
    private static boolean transferDetail(Connection p_con, Date p_processingDate) // throws
    // BTSLBaseException
    {
        final String METHOD_NAME = "transferDetail";
        if (_logger.isDebugEnabled()) {
            _logger.debug("transferDetail", " Entered with p_processingDate: " + p_processingDate);
        }
        PreparedStatement pstmtChannel = null; // for channel transferes
        PreparedStatement pstmtC2S = null; // for C2S transferes
        PreparedStatement pstmtOpen = null; // for opening and closing balance
        // of the day
        ResultSet rst = null;
        ResultSet rst2 = null;
        ResultSet rst3 = null;
        ArrayList channelList = null; // for channel transferes
        ArrayList C2SList = null; // for C2S transferes
        ArrayList balanceList = null; // for opening and closing balance of the
        // day
        ArrayList finalarray = null; // store information user id wise
        Locale locale = null; // language of the user
        DailyTransferDetailsVO userVO = null; // for opening and closing balance
        // of the day
        DailyTransferDetailsVO channelVO = null; // for channel transferes
        DailyTransferDetailsVO c2sVO = null; // for C2S transferes
        HashMap map = null;
        int messageCount = 0;
        try {
            final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
            final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            // user balance
            final StringBuffer queryBufOpen = new StringBuffer(" SELECT P.product_name ,UDB.balance,UDB.balance_date, UP.msisdn,");
            queryBufOpen.append(" UP.phone_language,UP.country,UDB.prev_balance,UDB.product_code,UDB.user_id");
            queryBufOpen.append(" FROM products P,user_daily_balances UDB, user_phones UP, users U WHERE UDB.balance_date=?");
            queryBufOpen.append(" AND UDB.user_id=UP.user_id AND UDB.product_code=P.product_code AND UP.primary_number='Y' AND U.user_id =UDB.user_id AND U.status='Y' ");

            final String query = queryBufOpen.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("transferDetail", "balance Query:  " + query);
            }
            pstmtOpen = p_con.prepareStatement(query.toString());
            pstmtOpen.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            rst = pstmtOpen.executeQuery();
            balanceList = new ArrayList();
            while (rst.next()) {
                userVO = new DailyTransferDetailsVO();
                userVO.setBalance(rst.getLong("balance"));
                userVO.setPreviousBalance(rst.getLong("prev_balance"));
                userVO.setProductCode(rst.getString("product_code"));
                userVO.setUserId(rst.getString("user_id"));
                userVO.setMsisdn(rst.getString("msisdn"));
                userVO.setBalanceDate(rst.getDate("balance_date"));
                userVO.setProductName(rst.getString("product_name"));
                try {
                    locale = new Locale(rst.getString("phone_language"), rst.getString("country"));
                }
                // setting default locale if no locale defined
                catch (Exception e) {
                    locale = new Locale(defaultLanguage, defaultCountry);
                    _logger.errorTrace(METHOD_NAME, e);
                }
                userVO.setLocale(locale);
                balanceList.add(userVO);

            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("transferDetail", "balance list:  " + balanceList.size());
            }
            // Channel to channel tranfers
            final StringBuffer queryBufChannel = new StringBuffer(" SELECT  CT.type, CT.transfer_category,");
            queryBufChannel.append(" CT.transfer_type,CT.transfer_sub_type,");
            queryBufChannel.append(" CT.to_user_id,CT.from_user_id,CTI.product_code,P.product_name,");
            queryBufChannel.append(" SUM(CT.payable_amount)payable_amount,SUM(CT.net_payable_amount)net_payable_amount,SUM(CT.transfer_mrp)transfer_mrp");
            queryBufChannel.append(" FROM channel_transfers CT,channel_transfers_items CTI,products P");
            queryBufChannel.append(" WHERE CT.status='CLOSE' AND CTI.transfer_id=CT.transfer_id");
            queryBufChannel.append(" AND CTI.s_no=1 AND P.product_code=CTI.product_code AND CT.transfer_date=?");
            queryBufChannel.append(" GROUP BY CT.type,CT.transfer_category,CT.transfer_type,");
            queryBufChannel.append(" CT.transfer_sub_type,CT.to_user_id,CT.from_user_id,CTI.product_code,P.product_name");

            final String queryChannel = queryBufChannel.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("transferDetail", "C2CQuery:  " + queryChannel);
            }
            pstmtChannel = p_con.prepareStatement(queryChannel.toString());
            pstmtChannel.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            rst2 = pstmtChannel.executeQuery();
            channelList = new ArrayList();
            while (rst2.next()) {
                channelVO = new DailyTransferDetailsVO();
                channelVO.setFromUserId(rst2.getString("from_user_id"));
                channelVO.setToUserId(rst2.getString("to_user_id"));
                channelVO.setType(rst2.getString("type"));
                channelVO.setTransferCategory(rst2.getString("transfer_category"));
                channelVO.setTransferType(rst2.getString("transfer_type"));
                channelVO.setTransferSubType(rst2.getString("transfer_sub_type"));
                channelVO.setProductCode(rst2.getString("product_code"));
                channelVO.setProductName(rst2.getString("product_name"));
                channelVO.setSumPayableAmount(rst2.getLong("payable_amount"));
                channelVO.setSumNetPayableAmount(rst2.getLong("net_payable_amount"));
                channelVO.setTransferMrp(rst2.getLong("transfer_mrp"));
                channelList.add(channelVO);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("transferDetail", "channelList list:  " + channelList.size());
            }
            //local_index_implemented
            // Channel to subscriber transfers
            final StringBuffer queryBufC2S = new StringBuffer(" SELECT CT.sender_id,U.user_name,CT.product_code, P.product_name,");
            queryBufC2S.append(" CT.service_type,ST.name,CT.transfer_status, count(1) nooftxn,");
            queryBufC2S.append(" SUM(CT.transfer_value)transfer_value");
            queryBufC2S.append(" FROM c2s_transfers CT, users U, products P,service_type ST");
            queryBufC2S.append(" WHERE CT.transfer_date=? AND CT.sender_id=U.user_id AND CT.product_code=P.product_code");
            queryBufC2S.append(" AND ST.service_type=CT.service_type AND CT.transfer_status IN ('200','205','250')");
            queryBufC2S.append(" GROUP BY CT.sender_id,U.user_name, CT.product_code, P.product_name,");
            queryBufC2S.append(" CT.service_type,ST.name,CT.transfer_status ORDER BY CT.sender_id");

            final String queryC2S = queryBufC2S.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("transferDetail", "queryC2S:  " + queryC2S);
            }
            pstmtC2S = p_con.prepareStatement(queryC2S.toString());
            pstmtC2S.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            rst3 = pstmtC2S.executeQuery();
            C2SList = new ArrayList();
            while (rst3.next()) {
                c2sVO = new DailyTransferDetailsVO();
                c2sVO.setSenderId(rst3.getString("sender_id"));
                c2sVO.setUserName(rst3.getString("user_name"));
                c2sVO.setProductName(rst3.getString("product_name"));
                c2sVO.setProductCode(rst3.getString("product_code"));
                c2sVO.setServiceType(rst3.getString("service_type"));
                c2sVO.setName(rst3.getString("name"));
                c2sVO.setTransferStatus(rst3.getString("transfer_status"));
                c2sVO.setNoOfTxn(rst3.getLong("nooftxn"));
                c2sVO.setTransferValue(rst3.getLong("transfer_value"));
                C2SList.add(c2sVO);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("transferDetail", "C2SList list:  " + C2SList.size());
            }
            if (balanceList.isEmpty()) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("transferDetail", "balance list:  " + balanceList.size());
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "DailyTransferDetailAlert[process]", "", "", "",
                    " User daily balance process is not executed, Please excute the user daily balance process first.");
                return false;
            }
            finalarray = new ArrayList();
            map = new HashMap();
            for (int i = 0; i < balanceList.size(); i++) {
                userVO = (DailyTransferDetailsVO) balanceList.get(i);
                int c2sListsizes=C2SList.size();
                for (int k = 0; k <c2sListsizes ; k++) {
                    if (_logger.isDebugEnabled()) {
                        _logger
                            .debug("transferDetail",
                                "userVO.getUserId()  " + userVO.getUserId() + "c2sVO.getSenderId()" + c2sVO.getSenderId() + "userVO.getProductCode()" + userVO
                                    .getProductCode() + "c2sVO.getProductCode()" + c2sVO.getProductCode());
                    }
                    c2sVO = (DailyTransferDetailsVO) C2SList.get(k);
                    if (userVO.getUserId().equals(c2sVO.getSenderId()) && userVO.getProductCode().equals(c2sVO.getProductCode())) {
                        finalarray.add(c2sVO);
                    }
                }
                int channelListsizes=channelList.size();
                for (int j = 0; j <channelListsizes ; j++) {
                    channelVO = (DailyTransferDetailsVO) channelList.get(j);
                    if (("R").equals(channelVO.getTransferSubType()) && "C2C".equals(channelVO.getType()) && userVO.getUserId().equals(channelVO.getFromUserId()) && userVO
                        .getProductCode().equals(channelVO.getProductCode())) {
                        finalarray.add(channelVO);
                    }
                    if (("W".equals(channelVO.getTransferSubType()) || ("T").equals(channelVO.getTransferSubType())) && ("C2C").equals(channelVO.getType()) && userVO
                        .getUserId().equals(channelVO.getToUserId()) && userVO.getProductCode().equals(channelVO.getProductCode())) {
                        finalarray.add(channelVO);
                    }
                    if (("R").equals(channelVO.getTransferSubType()) && ("C2C").equals(channelVO.getType()) && userVO.getUserId().equals(channelVO.getToUserId()) && userVO
                        .getProductCode().equals(channelVO.getProductCode())) {
                        finalarray.add(channelVO);
                    }
                    if ((("W").equals(channelVO.getTransferSubType()) || ("T").equals(channelVO.getTransferSubType())) && ("C2C").equals(channelVO.getType()) && userVO
                        .getUserId().equals(channelVO.getFromUserId()) && userVO.getProductCode().equals(channelVO.getProductCode())) {
                        finalarray.add(channelVO);
                    }
                    if (("R").equals(channelVO.getTransferSubType()) && ("O2C").equals(channelVO.getType()) && userVO.getUserId().equals(channelVO.getFromUserId()) && userVO
                        .getProductCode().equals(channelVO.getProductCode())) {
                        finalarray.add(channelVO);
                    }
                    if ((("W").equals(channelVO.getTransferSubType()) || ("T").equals(channelVO.getTransferSubType())) && ("O2C").equals(channelVO.getType()) && userVO
                        .getUserId().equals(channelVO.getToUserId()) && userVO.getProductCode().equals(channelVO.getProductCode())) {
                        finalarray.add(channelVO);
                    }
                }
                map.put(userVO.getUserId() + "_" + userVO.getProductCode(), finalarray);
                // for adding information about next user
                finalarray = new ArrayList();
            }
            messageCount = sendMessage(map, balanceList);
            if (messageCount > 0) {
                _logger.debug("process", "number of message sent: " + messageCount);
            }
            return true;
        }// end of try
        catch (Exception e) {
            _logger.error("DailyTransferDetailAlert[transferDetail]", "Exception  : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyTransferDetailAlert[process]", "", "", "",
                " DailyTransferDetailAlert process =" + e.getMessage());
            return false;
        }// end of catch
        finally {
        	try{
                if (rst!= null){
                	rst.close();
                }
              }
              catch (SQLException e){
            	  _logger.error("An error occurred closing statement.", e);
              }
        	try{
                if (rst2!= null){
                	rst2.close();
                }
              }
              catch (SQLException e){
            	  _logger.error("An error occurred closing statement.", e);
              }
        	try{
                if (rst3!= null){
                	rst3.close();
                }
              }
              catch (SQLException e){
            	  _logger.error("An error occurred closing statement.", e);
              }
            if (pstmtChannel != null) {
                try {
                    pstmtChannel.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }
            if (pstmtC2S != null) {
                try {
                    pstmtC2S.close();
                } catch (SQLException e4) {
                    _logger.errorTrace(METHOD_NAME, e4);
                }
            }
            if (pstmtOpen != null) {
                try {
                    pstmtOpen.close();
                } catch (SQLException e5) {
                    _logger.errorTrace(METHOD_NAME, e5);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("transferDetail", " Exiting ");
            }
        }
    }

    /**
     * This method sends the alerts to users one-by-one .
     * 
     * @param ArrayList
     *            p_balanceList
     * @param HashMap
     *            p_map
     * @return int
     *         method will send message to the user on the basis of user id and
     *         product code
     */
    public static int sendMessage(HashMap p_map, ArrayList p_balanceList) {
        final String METHOD_NAME = "sendMessage";
        if (_logger.isDebugEnabled()) {
            _logger.info("sendMessage", "Entered with size of map " + p_map.size() + " size of arraylist " + p_balanceList.size());
        }
        DailyTransferDetailsVO userVO = null; // for opening and closing balance
        // of the day and user id
        DailyTransferDetailsVO transferVO = null; // for transfer details
        String[] messageArray = null; // for message
        int messageCount = 0; // number of message sent
        KeyArgumentVO keyArgumentVO = null; // for multiple submessages
        ArrayList transferList = null; // for transfer details
        ArrayList messageList = null; // for the mesaage of multiple services
        long successAmount = 0L; // for sucessfull transactions amount
        long ambiguousAmount = 0L; // for ambiguous and underprocess
        // transactions amount
        try {
            for (int i = 0; i < p_map.size(); i++) {
                long focAmount = 0L;
                long channelTransfer = 0L;
                long saleAmount = 0L;
                userVO = (DailyTransferDetailsVO) p_balanceList.get(i);
                transferList = (ArrayList) (p_map.get(userVO.getUserId() + "_" + userVO.getProductCode()));
                if (_logger.isDebugEnabled()) {
                    _logger.info("sendMessage", "transferList " + transferList.size() + " userVO=" + userVO);
                }
                messageList = new ArrayList();
                int transferListsizes=transferList.size();
                for (int j = 0; j < transferListsizes; j++) {
                    transferVO = (DailyTransferDetailsVO) transferList.get(j);
                    // if type is C2C
                    if ((!BTSLUtil.isNullString(transferVO.getType())) && ("C2C").equals(transferVO.getType())) {
                        if (_logger.isDebugEnabled()) {
                            _logger.info("sendMessage", "transferVO.getType()" + transferVO.getType());
                        }
                        if (("T").equals(transferVO.getTransferSubType())) {
                            channelTransfer = channelTransfer + transferVO.getTransferMrp();
                        }
                        if (("W").equals(transferVO.getTransferSubType()) || ("R").equals(transferVO.getTransferSubType())) {
                            channelTransfer = channelTransfer - transferVO.getTransferMrp();
                        }
                    }
                    // if type is O2C
                    else if ((!BTSLUtil.isNullString(transferVO.getType())) && ("O2C").equals(transferVO.getType())) {
                        if (("T").equals(transferVO.getTransferSubType()) && ("TRF").equals(transferVO.getTransferCategory())) {
                            focAmount = focAmount + transferVO.getTransferMrp();
                        }
                        if (("T").equals(transferVO.getTransferSubType()) && ("SALE").equals(transferVO.getTransferCategory())) {
                            saleAmount = saleAmount + transferVO.getTransferMrp();
                        }
                        if (("W").equals(transferVO.getTransferSubType()) || ("R").equals(transferVO.getTransferSubType())) {
                            saleAmount = saleAmount - transferVO.getTransferMrp();
                        }
                    }
                    // if type is C2S
                    else {
                        // for successful transactions
                        if ((!BTSLUtil.isNullString(transferVO.getTransferStatus())) && ("200").equals(transferVO.getTransferStatus())) {
                            successAmount = successAmount + transferVO.getTransferValue();
                        }
                        // for ambiguous transactions and under process
                        // transactions
                        else if ((!BTSLUtil.isNullString(transferVO.getTransferStatus())) && ((("250").equals(transferVO.getTransferStatus()) || ("205").equals(transferVO
                            .getTransferStatus())))) {
                            ambiguousAmount = ambiguousAmount + transferVO.getTransferValue();
                        }
                        if ((j <= (transferList.size() - 2)) && (!(transferVO.getServiceType()).equals(((DailyTransferDetailsVO) transferList.get(j + 1)).getServiceType()))) {
                            messageArray = new String[] { transferVO.getName(), PretupsBL.getDisplayAmount(successAmount), PretupsBL.getDisplayAmount(ambiguousAmount) };
                            keyArgumentVO = new KeyArgumentVO();
                            keyArgumentVO.setKey(PretupsErrorCodesI.MESSAGE_MULTIPLE_SEVICES_SUBKEY);
                            keyArgumentVO.setArguments(messageArray);
                            messageList.add(keyArgumentVO);
                            ambiguousAmount = 0;
                            successAmount = 0;
                        }
                        if (j == (transferList.size() - 1)) {
                            messageArray = new String[] { transferVO.getName(), PretupsBL.getDisplayAmount(successAmount), PretupsBL.getDisplayAmount(ambiguousAmount) };
                            keyArgumentVO = new KeyArgumentVO();
                            keyArgumentVO.setKey(PretupsErrorCodesI.MESSAGE_MULTIPLE_SEVICES_SUBKEY);
                            keyArgumentVO.setArguments(messageArray);
                            messageList.add(keyArgumentVO);
                            ambiguousAmount = 0;
                            successAmount = 0;
                        }
                    }
                }
                final String[] arr = { userVO.getProductName(), PretupsBL.getDisplayAmount(userVO.getPreviousBalance()), PretupsBL.getDisplayAmount(userVO.getBalance()), PretupsBL
                    .getDisplayAmount(focAmount), PretupsBL.getDisplayAmount(saleAmount), PretupsBL.getDisplayAmount(channelTransfer), BTSLUtil.getMessage(userVO.getLocale(),
                    messageList) };
                final String senderMessage = BTSLUtil.getMessage(userVO.getLocale(), PretupsErrorCodesI.MESSAGE_FOR_MULTIPLE_SERVICE_MAIN, arr);
                final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), senderMessage, null, null, userVO.getLocale());
                pushMessage.push();
                Thread.sleep(100);
                messageCount++;
            }
            // return number of message of sent
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