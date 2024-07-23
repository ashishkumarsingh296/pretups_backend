package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class OfflineC2STransfersStockDeduction {
    private static Log log = LogFactory.getLog(OfflineC2STransfersStockDeduction.class.getName());
    private static ProcessStatusVO processStatusVO;

    private static final String BTSLBASEEXCEPTION = "BTSLBaseException : ";
    private static final String CLASSNAME = "OfflineC2STransfersStockDeduction";
    private static final String EXCEPTION = "Exception:";
    private static final String SQLEXCEPTION = "SQLException:";
    private static final String SQLPROCESSINGERROR = "error.general.sql.processing";
    private static final String OFFLINEC2STRAMSFERSTKDEDNCHKUP = "OfflineC2STransfersStockDeduction[checkUnderprocessTransaction]";
    private static final String OFFLINEC2STRAMSFERSTKDEDNUPDATE = "OfflineC2STransfersStockDeduction[updateLastSettlementDate]";

    /**
     * to ensure no class instantiation 
     */
    private OfflineC2STransfersStockDeduction(){
    	
    }
    public static void main(String[] args) {
        final String methodName = "main";
        try {
            if (args.length != 2) {
                log.error(methodName, "Usage : OfflineC2STransfersStockDeduction [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                log.error(methodName, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                log.error(methodName, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            log.error(methodName, "Error in Loading Configuration files ...........................: ");
            log.errorTrace("main", ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            log.error("main", BTSLBASEEXCEPTION + be.getMessage());
            log.errorTrace("main", be);
        } finally {
            LogFactory.printLog(methodName, PretupsI.EXITED, log);
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        ProcessBL processBL = null;
        Date processedUpto = null;
        Date currentDate = null;
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        final String methodName = "process";
        try {
            LogFactory.printLog(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576
                    + " Free:" + Runtime.getRuntime().freeMemory() / 1049576, log);
            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);

            con = OracleUtil.getSingleConnection();

            if (con == null) {
                LogFactory.printLog(methodName, " DATABASE Connection is NULL ", log);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED,
                        EventLevelI.FATAL, "OfflineC2STransfersStockDeduction [process]", "", "", "",
                        "DATABASE Connection is NULL");
                return;
            }

            processId = PretupsI.OFFLINEC2S;
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24));
            if (statusOk) {
                con.commit();
                processedUpto = processStatusVO.getExecutedUpto();
                executeTasks(processedUpto, currentDate, con, beforeInterval);
            }
        }// end of try
        catch (BTSLBaseException be) {
            log.error(methodName, BTSLBASEEXCEPTION + be.getMessage());
            log.errorTrace(methodName, be);
        } catch (Exception e) {
            log.error(methodName, EXCEPTION + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "OfflineC2STransfersStockDeduction[process]", "", "", "",
                    " OfflineC2STransfersStockDeduction process could not be executed successfully.");
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.C2STRFDDT_ERROR_EXCEPTION);
        } finally {
            try {
            if (statusOk) {
                
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        OracleUtil.commit(con);
                    } else {
                        OracleUtil.rollbackConnection(con,CLASSNAME,methodName);
                    }
            }
            OracleUtil.close(con);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            LogFactory.printLog(methodName, "Exiting ....Memory at end: Total:" + Runtime.getRuntime().totalMemory()
                    / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576, log);
        }
    }

    private static void executeTasks(Date processedUpto, Date currentDate, Connection con, int beforeInterval)
            throws BTSLBaseException, SQLException, InterruptedException {
        final String methodName = "executeTasks";
        Date dateCount;
        boolean isDaySuccess;
        Date processedUptoNew;
        if (processedUpto != null) {
            if (processedUpto.compareTo(currentDate) == 0) {
                throw new BTSLBaseException(CLASSNAME, methodName,
                        PretupsErrorCodesI.C2STRFDDT_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
            }
            processedUptoNew = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
            processStatusVO.setStartDate(currentDate);
            for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUptoNew); dateCount.before(BTSLUtil
                    .addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil.addDaysInUtilDate(
                    dateCount, 1)) {
                if (!checkUnderprocessTransaction(con, dateCount)) {
                    isDaySuccess = processC2STransfersData(con, dateCount);
                    updateProcessDetail(con, isDaySuccess, dateCount, currentDate);
                    Thread.sleep(500);
                }
            }// end date loop
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "OfflineC2STransfersStockDeduction[process]", "", "", "",
                    " OfflineC2STransfersStockDeduction process has been executed successfully.");
        } else {
            throw new BTSLBaseException(CLASSNAME, methodName,
                    PretupsErrorCodesI.C2STRFDDT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
        }

    }

    private static boolean checkUnderprocessTransaction(Connection con, Date beingProcessedDate)
            throws BTSLBaseException {
    	//local_index_implemented
        final String methodName = "checkUnderprocessTransaction";
        LogFactory.printLog(methodName, " Entered: p_beingProcessedDate=" + beingProcessedDate, log);
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        String selectQuery = null;
        try {
            String[] userId = Constants.getProperty("ONLINE_SETTLE_EXTUSRID_LIST").split(",");
            final StringBuilder strBuffUpdate = new StringBuilder();
            strBuffUpdate.append("SELECT 1 FROM c2s_transfers WHERE transfer_date=? AND transfer_status IN ('"
                    + PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS + "','" + PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS
                    + "')");
            strBuffUpdate.append(" and sender_id IN (");
            for (int i = 0; i < userId.length; i++) {
                if (i != 0)
                    strBuffUpdate.append(",");
                strBuffUpdate.append("?");
            }
            strBuffUpdate.append(")");
            selectQuery = strBuffUpdate.toString();
            LogFactory.printLog(methodName, "select query:" + selectQuery, log);
            selectPstmt = con.prepareStatement(selectQuery);
            int m = 1;
            selectPstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(beingProcessedDate));
            for (int i = 0; i < userId.length; i++) {
                selectPstmt.setString(++m, userId[i]);
            }
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler
                        .handle(EventIDI.SYSTEM_ERROR,
                                EventComponentI.SYSTEM,
                                EventStatusI.RAISED,
                                EventLevelI.FATAL,
                                OFFLINEC2STRAMSFERSTKDEDNCHKUP,
                                "",
                                "",
                                "",
                                "Message: OfflineC2STransfersStockDeduction process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.C2STRFDDT_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException : " + be.getMessage());
            log.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            log.error(methodName, SQLEXCEPTION + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    OFFLINEC2STRAMSFERSTKDEDNCHKUP, "", "", "", SQLEXCEPTION + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            log.error(methodName, "Exception : " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    OFFLINEC2STRAMSFERSTKDEDNCHKUP, "", "", "", EXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try{
                if (selectRst!= null){
                	selectRst.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (selectPstmt!= null){
                	selectPstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting transactionFound=" + transactionFound);
            }
        }
        return transactionFound;
    }

    private static void updateProcessDetail(Connection con, boolean isDaySuccess, Date dateCount, Date currentDate)
            throws BTSLBaseException, SQLException {

        ProcessStatusDAO processStatusDAO;
        int maxDoneDateUpdateCount;
        if (isDaySuccess) {
            processStatusVO.setExecutedUpto(dateCount);
            processStatusVO.setExecutedOn(currentDate);
            processStatusDAO = new ProcessStatusDAO();
            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
            if (maxDoneDateUpdateCount > 0) {
                con.commit();
            } else {
                con.rollback();
                throw new BTSLBaseException(CLASSNAME, "updateProcessDetail",
                        PretupsErrorCodesI.C2STRFDDT_COULD_NOT_UPDATE_MAX_DONE_DATE);
            }
        } else {
            con.rollback();
        }
    }

    private static int markProcessStatusAsComplete(Connection con, String processId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered:  processId:" + processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(processId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "OfflineC2STransfersStockDeduction[markProcessStatusAsComplete]", "", "", "",
                    "Exception :" + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.C2STRFDDT_ERROR_EXCEPTION);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    private static boolean processC2STransfersData(Connection con, Date beingProcessedDate) throws BTSLBaseException {
    	//local_index_implemented
        final String methodName = "processC2STransfersData";
        String serviceType = null;
        String senderCategory = null;
        long trfAmount = 0;
        long trfAdjAmount = 0;
        long totalAmount = 0;
        ResultSet c2sTrfDataRst = null;
        PreparedStatement c2sSelectPstmt = null;
        boolean excecutedUpto =true;
        String msisdn=null;
        Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))); 
        try {
            final StringBuilder c2sTrfSelectQueryBuf = new StringBuilder();
            c2sTrfSelectQueryBuf
            .append(" SELECT CT.network_code, CT.RECEIVER_NETWORK_CODE, CT.product_code, CT.service_type, CT.sender_id, CT.sender_category, CT.sender_msisdn, ");
            c2sTrfSelectQueryBuf
            .append(" SUM(sender_transfer_value) trf_amount, SUM(CASE WHEN ADJ.transfer_value>0 THEN ADJ.transfer_value ELSE 0 END) add_comm_trf_amt");
            c2sTrfSelectQueryBuf.append(" FROM c2s_transfers CT, ADJUSTMENTS ADJ ");
            c2sTrfSelectQueryBuf
            .append(" WHERE CT.transfer_date=?  AND CT.service_type='RC' AND CT.transfer_status='200' ");
            c2sTrfSelectQueryBuf
            .append(" AND CT.REQUEST_GATEWAY_TYPE='EXTGW' AND CT.sender_id = ? AND ct.sender_id = adj.user_id(+) AND CT.transfer_id=ADJ.reference_id(+) ");
            c2sTrfSelectQueryBuf
            .append(" GROUP BY CT.network_code, CT.RECEIVER_NETWORK_CODE, CT.product_code, CT.service_type, CT.sender_id, CT.sender_category, CT.sender_msisdn ");
            final String c2sTrfSelectQuery = c2sTrfSelectQueryBuf.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "c2sTrfSelectQuery query:" + c2sTrfSelectQuery);
            }

            String[] userId = Constants.getProperty("ONLINE_SETTLE_EXTUSRID_LIST").split(",");
            UserBalancesVO userBalancesVO = null;
            UserTransferCountsVO utcvo = null;

            for (int i = 0; i < userId.length; i++) {

                utcvo = loadLastSettlementDate(con, userId[i], false);
                if(utcvo.getModifiedOn()==null){
                    utcvo.setModifiedOn(BTSLUtil.addDaysInUtilDate(beingProcessedDate, -1));
                }
                if (BTSLUtil.getDifferenceInUtilDates(utcvo.getModifiedOn(), beingProcessedDate) == 1){

                    c2sSelectPstmt = con.prepareStatement(c2sTrfSelectQuery);
                    c2sSelectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(beingProcessedDate));
                    c2sSelectPstmt.setString(2, userId[i]);
                    c2sTrfDataRst = c2sSelectPstmt.executeQuery();
                    boolean allRecordUpdated = true;

                    while (c2sTrfDataRst.next()) {
                        userBalancesVO = UserBalancesVO.getInstance();
                        userBalancesVO.setNetworkCode(c2sTrfDataRst.getString("network_code"));
                        msisdn = c2sTrfDataRst.getString("sender_msisdn");
                        userBalancesVO.setNetworkFor(c2sTrfDataRst.getString("RECEIVER_NETWORK_CODE"));
                        userBalancesVO.setProductCode(c2sTrfDataRst.getString("product_code"));
                        serviceType = c2sTrfDataRst.getString("service_type");
                        senderCategory = c2sTrfDataRst.getString("sender_category");
                        userBalancesVO.setUserID(c2sTrfDataRst.getString("sender_id"));
                        userBalancesVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                        userBalancesVO.setLastTransferType(serviceType + "STLMT");
                        userBalancesVO.setLastTransferOn(utcvo.getLastTransferDate());
                        userBalancesVO.setLastTransferID(utcvo.getLastTransferID());
                        trfAmount = c2sTrfDataRst.getLong("trf_amount");
                        trfAdjAmount = c2sTrfDataRst.getLong("add_comm_trf_amt");
                        totalAmount = trfAmount - trfAdjAmount;
                        userBalancesVO.setQuantityToBeUpdated(totalAmount);
                        log.debug(methodName, " serviceType:" + serviceType + ",trf_amount:" + trfAmount + ",trfAdjAmount:"
                                + trfAdjAmount + ",totalAmount:" + totalAmount);

                        try {
                            new UserBalancesDAO().updateUserDailyBalances(con, beingProcessedDate, userBalancesVO);
                            final int updateCount2 = new UserBalancesDAO().debitUserBalances(con, userBalancesVO, null,
                                    userBalancesVO.getProductCode(), true, senderCategory);
                            if (updateCount2 <= 0) {
                                throw new BTSLBaseException("ChannelUserBL", methodName,
                                        PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
                            }
                        } catch (BTSLBaseException   be) {
                            log.error(methodName, EXCEPTION + be.getMessage());
                            log.errorTrace(methodName, be);
                            OracleUtil.rollbackConnection(con,CLASSNAME,methodName);
                            allRecordUpdated=false;
                            
                        } catch (Exception   e) {
                            log.error(methodName, EXCEPTION + e.getMessage());
                            log.errorTrace(methodName, e);
                            OracleUtil.rollbackConnection(con,CLASSNAME,methodName);
                            allRecordUpdated=false;
                          
                        }
                        if(!allRecordUpdated){
                            break;
                        }
                    }
                    if(allRecordUpdated){
                        int updateCount1 = updateLastSettlementDate(con, beingProcessedDate, userId[i]);
                        if (updateCount1 < 1) {
                            con.rollback();
                            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                        }
                        con.commit();
                    }else{
                        OracleUtil.rollbackConnection(con,CLASSNAME,methodName);
                        excecutedUpto=false;
                    }

                }
            }
        } catch (SQLException sqe) {
            excecutedUpto = false;
            OracleUtil.rollbackConnection(con,CLASSNAME,methodName);
            log.error(methodName, SQLEXCEPTION + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "OfflineC2STransfersStockDeduction [processC2STransfersData]", "", "", "",
                    SQLEXCEPTION + sqe.getMessage());

            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.C2STRFDDT_ERROR_EXCEPTION);

        }// end of catch
        catch (BTSLBaseException be) {
            OracleUtil.rollbackConnection(con,CLASSNAME,methodName);
            excecutedUpto = false;
            log.error(methodName, EXCEPTION + be.getMessage());
            log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "OfflineC2STransfersStockDeduction[processC2STransfersData]", "", "", "", "BTSLBaseException: "
                            + be.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.C2STRFDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            excecutedUpto = false;
            OracleUtil.rollbackConnection(con,CLASSNAME,methodName);
            log.error(methodName, EXCEPTION + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "OfflineC2STransfersStockDeduction[processC2STransfersData]", "", "", "",
                    SQLEXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.C2STRFDDT_ERROR_EXCEPTION);
        } finally {
            try{
                if (c2sSelectPstmt!= null){
                	c2sSelectPstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (c2sTrfDataRst!= null){
                	c2sTrfDataRst.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            LogFactory.printLog(methodName, PretupsI.EXITED, log);
        }// end of finally

        return excecutedUpto;
    }

    private static UserTransferCountsVO loadLastSettlementDate(Connection con, String userId,
            Boolean isLockRecordForUpdate) throws BTSLBaseException {

        final String methodName = "loadLastSettlementDate";
        LogFactory.printLog(methodName, "Entered  userId : " + userId + ", isLockRecordForUpdate : "
                + isLockRecordForUpdate, log);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserTransferCountsVO userTransferCountsVO = null;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT user_id, last_transfer_id, last_transfer_date, c2sbalance_settled_date  FROM user_transfer_counts  WHERE user_id = ? "); // IN
                                                                                                                                                         // ('NGD001','NGD002','NGD003')
        if (isLockRecordForUpdate) {
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                strBuff.append(" FOR UPDATE with RS");
            } else {
                strBuff.append(" FOR UPDATE ");
            }
        }
        final String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, log);

        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                userTransferCountsVO = new UserTransferCountsVO();
                userTransferCountsVO.setUserID(rs.getString("user_id"));
                userTransferCountsVO.setLastTransferID(rs.getString("last_transfer_id"));
                userTransferCountsVO.setLastTransferDate(rs.getDate("last_transfer_date"));
                userTransferCountsVO.setModifiedOn(rs.getDate("c2sbalance_settled_date"));
            }
        } catch (SQLException sqe) {
            log.error(methodName, SQLEXCEPTION + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    OFFLINEC2STRAMSFERSTKDEDNUPDATE, "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("UserTransferCountsDAO ", methodName, SQLPROCESSINGERROR);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    OFFLINEC2STRAMSFERSTKDEDNUPDATE, "", "", "",
                    EXCEPTION + ex.getMessage());
            throw new BTSLBaseException(" UserTransferCountsDAO", methodName, "error.general.processing");
        } finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            LogFactory.printLog(methodName, "Exiting:  userTransferCountsVO =" + userTransferCountsVO, log);

        }
        return userTransferCountsVO;
    }

    private static int updateLastSettlementDate(Connection con, Date beingProcessedDate, String userID)
            throws BTSLBaseException {
        final String methodName = "updateLastSettlementDate";
        LogFactory.printLog(methodName, "Entered p_beingProcessedDate : " + beingProcessedDate + ",userID : " + userID,
                log);
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {

            final StringBuilder strBuffUpdate = new StringBuilder();
            strBuffUpdate.append(" UPDATE user_transfer_counts SET c2sbalance_settled_date = ? WHERE user_id = ?  ");
            final String query = strBuffUpdate.toString();
            LogFactory.printLog(methodName, " query:" + query, log);
            psmt = con.prepareStatement(query);
            int m = 1;
            psmt.setDate(m++, BTSLUtil.getSQLDateFromUtilDate(beingProcessedDate));
            psmt.setString(m, userID);

            updateCount = psmt.executeUpdate();
            if (updateCount == 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
                        EventLevelI.INFO, OFFLINEC2STRAMSFERSTKDEDNUPDATE, "", "", "",
                        "BTSLBaseException: update count <=0");
                throw new BTSLBaseException(CLASSNAME, methodName, SQLPROCESSINGERROR);
            }
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            log.error(methodName, SQLEXCEPTION + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    OFFLINEC2STRAMSFERSTKDEDNUPDATE, "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, SQLPROCESSINGERROR);
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    OFFLINEC2STRAMSFERSTKDEDNUPDATE, "", "", "",
                    EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, "error.general.processing");
        } finally {
            try{
                if (psmt!= null){
                	psmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            LogFactory.printLog(methodName, "Exiting Success :" + updateCount, log);

        }
        return updateCount;
    }



}
