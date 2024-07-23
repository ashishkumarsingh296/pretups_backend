package com.btsl.pretups.p2p.subscriber.processes;

/**
 * 
 * @(#)ScheduledMultipleCreditTransferProcess
 *                                            Copyright(c) 2006, Bharti Telesoft
 *                                            Ltd.
 *                                            All Rights Reserved
 * @author sonali.garg
 *         --------------------------------------------------------------------
 *         -----------------------------
 *         Author Date History
 *         --------------------------------------------------------------------
 *         -----------------------------
 *         Sonali Garg 22/04/2013 Initial Creation
 *         --------------------------------------------------------------------
 *         -----------------------------
 *         This class initiates the schedules transfer of P2P batches
 *         Daily/Weekly or Monthly.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.p2p.logging.ScheduleCreditTransferLogSummary;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBatchesVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;
import com.txn.pretups.p2p.subscriber.businesslogic.SubscriberTxnDAO;
import com.txn.pretups.p2p.transfer.businesslogic.MCDTxnDAO;

public class ScheduledMultipleCreditTransferProcess {

    private String transId = null;
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static SubscriberTxnDAO _subscriberTxnDAO = new SubscriberTxnDAO();
    private static Log _logger = LogFactory.getLog(ScheduledMultipleCreditTransferProcess.class.getName());

    public static void main(String arg[]) {
        String pushMessageEnabled = null;
        final String METHOD_NAME = "main";
        try

        {
            if (arg.length != 3)

            {
                if (arg.length != 2)

                {
                    _logger.info(METHOD_NAME, "Usage : ScheduledMultipleCreditTransferProcess [Constants file] [LogConfig file] [Message Flag y/n]");
                    return;

                }
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())

            {
                _logger.info(METHOD_NAME, "ScheduledMultipleCreditTransferProcess" + " Constants File Not Found .............");
                return;

            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())

            {
                _logger.info(METHOD_NAME, "ScheduledMultipleCreditTransferProcess" + " Logconfig File Not Found .............");
                return;

            }

            pushMessageEnabled = arg[2];
            if (BTSLUtil.isNullString(pushMessageEnabled))

            {
                _logger.info(METHOD_NAME, "ScheduledMultipleCreditTransferProcess" + " pushMessageEnabled argument Not Found .............");
                return;

            } else if (!("y".equalsIgnoreCase(pushMessageEnabled) || "n".equalsIgnoreCase(pushMessageEnabled)))

            {
                throw new BTSLBaseException("ScheduledMultipleCreditTransferProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);

            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        } catch (Exception e)

        {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;

        }
        try

        {
        	if(_subscriberTxnDAO == null){
        		_subscriberTxnDAO = new SubscriberTxnDAO();
        	}
            new ScheduledMultipleCreditTransferProcess().process(pushMessageEnabled);

        } catch (BTSLBaseException be)

        {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);

        } finally

        {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();

        }
    }

    /**
     * @throws BTSLBaseException
     * @throws SQLException
     */
    private void process(String pushMessageEnabled) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", "Entered pushMessageEnabled=" + pushMessageEnabled);
        }

        Date processedUpto = null;
        Date currentDateTime = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        long startTime = 0;
        long endTime = 0;
        try

        {
            _logger.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            final Calendar cal = BTSLDateUtil.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            startTime = new Date().getTime();
            _logger.debug("process", "Start Time ::" + startTime);
            con = OracleUtil.getSingleConnection();
            if (con == null)

            {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ScheduledMultipleCreditTransferProcess[process]", "", "", "", "DATABASE Connection is NULL");
                return;

            }
            processId = PretupsI.PROCESS_NAME_SCHEDULED_MULTIPLE_CREDIT_TRANSFER;
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {

                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    ArrayList<Object> batchList = new ArrayList<Object>();
                    batchList = null;
                    batchList = _subscriberTxnDAO.loadBuddyDetailsByScheduleType(con, PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER);

                    if (batchList != null) {
                        scheduleRecords(con, batchList, pushMessageEnabled);

                    }
                    con.commit();
                    batchList = _subscriberTxnDAO.loadBuddyDetailsByScheduleType(con, PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER);
                    if (batchList != null && !batchList.isEmpty()) {
                        scheduleRecords(con, batchList, pushMessageEnabled);
                    }
                    con.commit();

                    _processStatusVO.setExecutedUpto(currentDateTime);
                    _processStatusVO.setExecutedOn(currentDateTime);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                        "ScheduledMultipleCreditTransferProcess[process]", "", "", "", " ScheduledMultipleCreditTransferProcess process has been executed successfully.");

                }
            } else {
                throw new BTSLBaseException("ScheduledMultipleCreditTransferProcess", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }

        } catch (BTSLBaseException be)

        {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;

        } catch (Exception e)

        {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);

        } finally

        {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk)

            {
                try

                {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }

                } catch (Exception e)

                {
                    _logger.errorTrace(METHOD_NAME, e);

                }
                try

                {
                    if (con != null) {
                        con.close();
                    }

                } catch (Exception ex)

                {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("process", "Exception closing connection ");
                    }
                    _logger.errorTrace(METHOD_NAME, ex);

                }
            }
            _logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
            endTime = new Date().getTime();
            _logger.debug("process", "End Time::" + endTime);
            _logger.debug("process", "interval Time::" + (endTime - startTime) / 1000 * 60);

        }

    }

    /**
     * @param list
     */
    private void scheduleRecords(Connection p_con, ArrayList<Object> p_batchList, String p_pushMessageEnabled) throws BTSLBaseException {
        final String METHOD_NAME = "scheduleRecords";
        if (_logger.isDebugEnabled()) {
            _logger.debug("scheduleRecords", "Entered p_batchList::" + p_batchList + "Entered p_pushMessageEnabled:" + p_pushMessageEnabled);
        }
        P2PBatchesVO batchBuddyVO = null;
        final BuddyVO receiverVO = null;
        ArrayList<Object> buddyList = null;
        String key = null;
        int failCount = 0;
        int successCount = 0;
        int ambiguousCount = 0;
        int _totalCount = 0;
        int listCount = 0;
        String[] array = null;
        final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
        boolean _barred = false;
        int listDeleted = 0;
        PreparedStatement pstmtSelectBarred = null;
        PreparedStatement pstmtSelectFailCount = null;
        PreparedStatement pstmtResetFailCount = null;
        PreparedStatement pstmtUpdateFailCount = null;
        PreparedStatement pstmtDeleteBuddy = null;
        PreparedStatement pstmtUpdateExecCount = null;
        PreparedStatement pstmtDeleteBatch = null;
        PreparedStatement pstmtSelectExecCount = null;
        PreparedStatement pstmtSelectErrList = null;
        PreparedStatement pstmtSelectNextSchDate = null;
        PreparedStatement pstmtSelectListSize = null;
        PreparedStatement pstmtDeleteEmptyList = null;
        ResultSet rsSelectBarred = null;
        ResultSet rsSelectFailCount = null;
        ResultSet rsSelectExecCount = null;
        ResultSet rsSelectErrList = null;
        ResultSet rsSelectNxtSchDat = null;
        ResultSet rsSelectListSize = null;

        int succFailCount = 0;
        int delBuddyCount = 0;
        Date currentDateTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        final Calendar cal = BTSLDateUtil.getInstance();
        currentDateTime = cal.getTime(); // Current Date
        String[] succArray = null;
        String[] ambiArray = null;
        String[] failArray = null;
        String errorCode = null;
        String status = null;
        BuddyVO buddyVO = null;

        Date nextScheduleDate = null;
        String nextSchDate = null;
        try {
            // To check whether subscriber is barred & if found then just update
            // its Schedule Date
        	ScheduledMultipleCreditTransferProcessQry creditTransferProcessQry = (ScheduledMultipleCreditTransferProcessQry)
        			ObjectProducer.getObject(QueryConstants.SCHEDULE_MULTIPLE_CREDIT_TRANSFER_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
        	
            
            final String selectBarredQuery = creditTransferProcessQry.scheduleRecordsSelectFromBarredMsisdn();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "selectBarredQuery::" + selectBarredQuery);
            }
            pstmtSelectBarred = p_con.prepareStatement(selectBarredQuery);

            // If Transaction is successful then reset Successive Failure Count
            // to 0
            final StringBuffer resFCnt = new StringBuffer("UPDATE p2p_buddies SET successive_failure_count = '0' ,modified_on = ? ,");
            resFCnt.append(" modified_by = ? WHERE");
            resFCnt.append(" buddy_msisdn =? AND list_name =? AND parent_id =? AND selector_code =?");
            final String resetFailCountQuery = resFCnt.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "resetFailCountQuery:" + resetFailCountQuery);
            }
            pstmtResetFailCount = p_con.prepareStatement(resetFailCountQuery);

            // If Transaction is fail then increase the Successive Failure Count
            final StringBuffer updFCnt = new StringBuffer();
            updFCnt.append("UPDATE p2p_buddies SET successive_failure_count = successive_failure_count + 1,modified_on = ? , ");
            updFCnt.append(" modified_by = ? WHERE");
            updFCnt.append(" buddy_msisdn =? AND list_name =? AND parent_id =? AND selector_code =?");
            final String updateFailCountQuery = updFCnt.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "updateFailCountQuery:" + updateFailCountQuery);
            }
            pstmtUpdateFailCount = p_con.prepareStatement(updateFailCountQuery);

            // To check the Successive Failure Count & delete the Buddy if
            // Successive Failure Count is greater than 3
            final StringBuffer getFCnt = new StringBuffer();
            getFCnt.append("SELECT successive_failure_count FROM p2p_buddies  ");
            getFCnt.append("WHERE parent_id=? AND buddy_msisdn=? AND list_name=?");
            final String selectSuccFailCountQuery = getFCnt.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "selectSuccFailCountQuery:" + selectSuccFailCountQuery);
            }
            pstmtSelectFailCount = p_con.prepareStatement(selectSuccFailCountQuery);

            final StringBuffer delBuddy = new StringBuffer();
            delBuddy.append(" DELETE FROM p2p_buddies  ");
            delBuddy.append(" WHERE parent_id=? AND list_name=? AND buddy_msisdn=? AND selector_code=? ");
            final String deleteBuddyQuery = delBuddy.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "deleteBuddyQuery:" + deleteBuddyQuery);
            }
            pstmtDeleteBuddy = p_con.prepareStatement(deleteBuddyQuery);

            // Increase Execution Count on each Success/Failure Transaction
            final StringBuffer updBatch = new StringBuffer();
            updBatch.append("UPDATE p2p_batches SET  execution_count = execution_count + 1,modified_on =? , ");
            updBatch.append("modified_by =? , execution_upto = ?, schedule_date = ?  WHERE ");
            updBatch.append("list_name =? AND parent_id =? AND batch_id =?");
            final String updateBatchQuery = updBatch.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "updateBatchQuery:" + updateBatchQuery);
            }
            pstmtUpdateExecCount = p_con.prepareStatement(updateBatchQuery);

            // Check Execution count & If found equals to Schedule Frequency,
            // then delete the Schedule Batch
            final StringBuffer selExecCnt = new StringBuffer();
            selExecCnt.append("SELECT execution_count FROM p2p_batches WHERE ");
            selExecCnt.append("parent_id =? and list_name=? and batch_id =? and execution_count = no_of_schedule");
            final String selectExecCountQuery = selExecCnt.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "selectExecCountQuery:" + selectExecCountQuery);
            }
            pstmtSelectExecCount = p_con.prepareStatement(selectExecCountQuery);

            final StringBuffer delBatchCnt = new StringBuffer();
            delBatchCnt.append("delete from p2p_batches WHERE ");
            delBatchCnt.append("parent_id =? and list_name=? and batch_id =?");
            final String deleteBatchQuery = delBatchCnt.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "deleteBatchQuery:" + deleteBatchQuery);
            }
            pstmtDeleteBatch = p_con.prepareStatement(deleteBatchQuery);

            // Select Final Transaction Status & error_code on each credit
            // transfer
            final StringBuffer selTxnRcrd = new StringBuffer();
            selTxnRcrd.append("SELECT error_code,transfer_status from subscriber_transfers ");
            selTxnRcrd.append("WHERE TRANSFER_ID = ?");
            final String selectTransDetailQuery = selTxnRcrd.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "selectTransDetailQuery:" + selectTransDetailQuery);
            }
            pstmtSelectErrList = p_con.prepareStatement(selectTransDetailQuery);

            final StringBuffer selectQueryBuff = new StringBuffer("SELECT SCHEDULE_DATE FROM p2p_batches  ");
            selectQueryBuff.append(" WHERE  parent_id=? and list_name=? and status = ? and SCHEDULE_TYPE = ? ");
            final String selectNextSchDateQuery = selectQueryBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "selectNextSchDateQuery:" + selectNextSchDateQuery);
            }
            pstmtSelectNextSchDate = p_con.prepareStatement(selectNextSchDateQuery);

            final StringBuffer selectListSize = new StringBuffer(" select count(pbu.buddy_msisdn) count from p2p_buddies pbu WHERE pbu.parent_id=? and pbu.list_name=?");
            selectQueryBuff.append(" and pbu.parent_id in (select pba.parent_id from p2p_batches pba where pba.schedule_type=? and pba.status=? )");
            final String selectListSizeQuery = selectListSize.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "selectListSizeQuery:" + selectListSizeQuery);
            }
            pstmtSelectListSize = p_con.prepareStatement(selectListSizeQuery);

            final StringBuffer deleteEmptyList = new StringBuffer("delete from p2p_batches ");
            deleteEmptyList.append("where parent_id=? and list_name=?");
            final String deleteEmptyListQuery = deleteEmptyList.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "deleteEmptyListQuery:" + deleteEmptyListQuery);
            }
            pstmtDeleteEmptyList = p_con.prepareStatement(deleteEmptyListQuery);
            // Load language code from locale master cache and set into
            // batchBuddyVO
            final LocaleMasterCache localeMasterCache = new LocaleMasterCache();
            LocaleMasterVO localeMasterVO = new LocaleMasterVO();
            localeMasterVO = localeMasterCache.getLocaleDetailsFromlocale(locale);
            // process each subscriber one by one
            int batchLists=p_batchList.size();
            for (int i = 0; i < batchLists; i++) {
                _totalCount = 0;
                successCount = 0;
                failCount = 0;
                batchBuddyVO = (P2PBatchesVO) p_batchList.get(i);
                try {
                    pstmtSelectBarred.clearParameters();
                    pstmtSelectBarred.setString(1, PretupsI.P2P_MODULE);
                    pstmtSelectBarred.setString(2, batchBuddyVO.getNetworkCode());
                    pstmtSelectBarred.setString(3, getSystemFilteredMSISDN(batchBuddyVO.getSenderMSISDN()));
                    pstmtSelectBarred.setString(4, PretupsI.USER_TYPE_SENDER);
                    rsSelectBarred = pstmtSelectBarred.executeQuery();
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("scheduleRecords", "Bared Checked ListName=" + batchBuddyVO.getListName() + " MSISDN=" + batchBuddyVO.getSenderMSISDN());
                    }
                    if (rsSelectBarred.next()) {
                        _barred = true;
                    }
                    if (_barred) {
                        (new PushMessage(batchBuddyVO.getSenderMSISDN(), BTSLUtil.getMessage(locale, PretupsErrorCodesI.ERROR_USERBARRED, null), null, (String) Constants
                            .getProperty("SCHEDULED_MCLD_RESPONSE_GATEWAY_CODE"), locale)).push();
                        throw new BTSLBaseException("ScheduledMultipleCreditTransferProcess", "checkMSISDNBarred", PretupsErrorCodesI.ERROR_USERBARRED);
                    }
                } catch (SQLException sqle) {
                    _logger.error("checkMSISDNBarred", "Exception while checking for barred dial : " + batchBuddyVO.getSenderMSISDN() + "ListName" + batchBuddyVO
                        .getListName());
                    _logger.errorTrace(METHOD_NAME, sqle);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduledMultipleCreditTransferProcess[checkMSISDNBarred]", "", "", "", "SQL Exception:" + sqle.getMessage());

                } catch (BTSLBaseException be) {
                    _logger.error("checkMSISDNBarred", "Exception while checking for barred dial : " + batchBuddyVO.getSenderMSISDN() + "ListName" + batchBuddyVO
                        .getListName());
                    _logger.errorTrace(METHOD_NAME, be);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduledMultipleCreditTransferProcess[checkMSISDNBarred]", "", "", "", "Exception:" + be.getMessage());
                }

                batchBuddyVO.setSenderLangCode(localeMasterVO.getLanguage_code());

                buddyList = batchBuddyVO.getBuddyList();
                if (!_barred) {
                    for (int j = 0; j < buddyList.size(); j++) {

                        buddyVO = (BuddyVO) buddyList.get(j);
                        transId = generateRequestResponse(p_con, batchBuddyVO, buddyVO, batchBuddyVO.getSenderMSISDN());
                        buddyVO.setTransId(transId);
                    }
                }
                // Update Execution Count in the list on each Success/Fail
                // transaction
                try {
                    pstmtUpdateExecCount.clearParameters();
                    pstmtUpdateExecCount.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(batchBuddyVO.getModifiedOn()));
                    pstmtUpdateExecCount.setString(2, batchBuddyVO.getModifiedBy());
                    pstmtUpdateExecCount.setDate(3, BTSLUtil.getSQLDateFromUtilDate(currentDateTime));
                    pstmtUpdateExecCount.setDate(4, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString((getNextScheduleDate(batchBuddyVO.getScheduleDate(),
                        batchBuddyVO.getScheduleType())), PretupsI.DATE_FORMAT_DDMMYYYY)));
                    pstmtUpdateExecCount.setString(5, batchBuddyVO.getListName());
                    pstmtUpdateExecCount.setString(6, batchBuddyVO.getParentID());
                    pstmtUpdateExecCount.setString(7, batchBuddyVO.getBatchID());
                    pstmtUpdateExecCount.executeUpdate();

                    if (_logger.isDebugEnabled()) {
                        _logger.debug("scheduleRecords",
                            "Update Execution counter ListName=" + batchBuddyVO.getListName() + " BatchID=" + batchBuddyVO.getBatchID() + " ParentID=" + batchBuddyVO
                                .getParentID());
                    }

                } catch (SQLException sqle) {
                    _logger.error("updateExecutionCount", "Exception while updating execution count for  dial : " + batchBuddyVO.getSenderMSISDN() + "ListName" + batchBuddyVO
                        .getListName());
                    _logger.errorTrace(METHOD_NAME, sqle);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduledMultipleCreditTransferProcess[updateExecutionCount]", "", "", "", "SQL Exception:" + sqle.getMessage());
                }// end of catch
                catch (Exception e) {
                    _logger.error("updateExecutionCount", "Exception while updating execution count for  dial : " + batchBuddyVO.getSenderMSISDN() + "ListName" + batchBuddyVO
                        .getListName());
                    _logger.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduledMultipleCreditTransferProcess[updateExecutionCount]", "", "", "", "Exception:" + e.getMessage());
                }// end of catch
                 // Delete the Batch if execution count reaches to no. of
                 // schedules provided in the list
                try {
                    pstmtSelectExecCount.clearParameters();
                    pstmtSelectExecCount.setString(1, batchBuddyVO.getListName());
                    pstmtSelectExecCount.setString(2, batchBuddyVO.getParentID());
                    pstmtSelectExecCount.setString(3, batchBuddyVO.getBatchID());
                    rsSelectExecCount = pstmtSelectExecCount.executeQuery();
                    if (rsSelectExecCount.next()) {
                        pstmtDeleteBatch.clearParameters();
                        pstmtDeleteBatch.setString(1, batchBuddyVO.getListName());
                        pstmtDeleteBatch.setString(2, batchBuddyVO.getParentID());
                        pstmtDeleteBatch.setString(3, batchBuddyVO.getBatchID());
                        pstmtDeleteBatch.executeUpdate();
                    }
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("scheduleRecords",
                            "Delete batch ListName=" + batchBuddyVO.getListName() + " BatchID=" + batchBuddyVO.getBatchID() + " ParentID=" + batchBuddyVO.getParentID());
                    }

                } catch (SQLException sqle) {
                    _logger.error("deleteBatch", "Exception while selecting execution count if(=no of schedule) then deleting batch for dial : " + batchBuddyVO
                        .getSenderMSISDN() + "ListName" + batchBuddyVO.getListName());
                    _logger.errorTrace(METHOD_NAME, sqle);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduledMultipleCreditTransferProcess[deleteBatch]", "", "", "", "SQL Exception:" + sqle.getMessage());
                }// end of catch
                catch (Exception e) {
                    _logger.error("deleteBatch", "Exception while selecting execution count if(=no of schedule) then deleting batch for dial : " + batchBuddyVO
                        .getSenderMSISDN() + "ListName" + batchBuddyVO.getListName());
                    _logger.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduledMultipleCreditTransferProcess[deleteBatch]", "", "", "", "Exception:" + e.getMessage());
                }// end of catch

            }
            p_con.commit();
            // Checking the Transactions Status for individual sender & its
            // Buddies, update the count accordingly & push the message
            Thread.sleep(200);
            String message = null;
            final String respGW = (String) Constants.getProperty("SCHEDULED_MCLD_RESPONSE_GATEWAY_CODE");
            ArrayList<Object> successList = new ArrayList<Object>();
            ArrayList<Object> ambiguousList = new ArrayList<Object>();
            ArrayList<Object> failList = new ArrayList<Object>();
            KeyArgumentVO keyArgumentVO = null;
            for (int i = 0; i < p_batchList.size(); i++) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("scheduleRecords", "Message push Start");
                }

                _totalCount = 0;
                successCount = 0;
                ambiguousCount = 0;
                failCount = 0;
                successList = new ArrayList<Object>();
                ambiguousList = new ArrayList<Object>();
                failList = new ArrayList<Object>();

                try {
                    batchBuddyVO = null;
                    buddyList = null;
                    batchBuddyVO = (P2PBatchesVO) p_batchList.get(i);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("scheduleRecords", "Message push for BtachID=" + batchBuddyVO.getBatchID());
                    }
                    buddyList = batchBuddyVO.getBuddyList();
                    for (int j = 0; j < buddyList.size(); j++) {
                        keyArgumentVO = null;
                        succArray = new String[2];
                        ambiArray = new String[2];
                        failArray = new String[2];

                        buddyVO = (BuddyVO) buddyList.get(j);
                        pstmtSelectErrList.clearParameters();
                        pstmtSelectErrList.setString(1, buddyVO.getTransId());
                        rsSelectErrList = pstmtSelectErrList.executeQuery();
                        if (rsSelectErrList.next()) {
                            errorCode = rsSelectErrList.getString("error_code");
                            status = rsSelectErrList.getString("transfer_status");

                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("scheduleRecords", "Message Prepare for TXN ID=" + buddyVO.getTransId());
                        }

                        if (!BTSLUtil.isNullString(status) && PretupsI.TXN_STATUS_SUCCESS.equals(status)) {
                            _totalCount++;
                            successCount++;
                            keyArgumentVO = new KeyArgumentVO();
                            keyArgumentVO.setKey(PretupsErrorCodesI.P2P_SENDER_SCT_SUCCESS_SUBKEY);
                            succArray[0] = buddyVO.getBuddyMsisdn();
                            succArray[1] = String.valueOf(buddyVO.getPreferredAmount());
                            keyArgumentVO.setArguments(succArray);
                            successList.add(keyArgumentVO);

                            try {
                                pstmtResetFailCount.clearParameters();
                                if (buddyVO.getModifiedOn() != null) {
                                    pstmtResetFailCount.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(buddyVO.getModifiedOn()));
                                } else {
                                    pstmtResetFailCount.setNull(1, Types.DATE);
                                }
                                pstmtResetFailCount.setString(2, buddyVO.getModifiedBy());

                                pstmtResetFailCount.setString(3, buddyVO.getBuddyMsisdn());
                                pstmtResetFailCount.setString(4, buddyVO.getListName());
                                pstmtResetFailCount.setString(5, buddyVO.getOwnerUser());
                                pstmtResetFailCount.setString(6, buddyVO.getSelectorCode());
                                pstmtResetFailCount.executeUpdate();
                            } catch (SQLException sqle) {
                                _logger
                                    .error(
                                        "resetSuccessiveFailCount",
                                        "Exceptiong while re-initialize successive failure count of buddy " + buddyVO.getBuddyMsisdn() + "ListName:" + buddyVO.getListName() + "of dial:" + batchBuddyVO
                                            .getSenderMSISDN());
                                _logger.errorTrace(METHOD_NAME, sqle);
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                    "ScheduledMultipleCreditTransferProcess[resetSuccessiveFailCount]", "", "", "", "SQL Exception:" + sqle.getMessage());
                            }// end of catch
                            catch (Exception e) {
                                _logger
                                    .error(
                                        "resetSuccessiveFailCount",
                                        "Exceptiong while re-initialize successive failure count of buddy " + buddyVO.getBuddyMsisdn() + "ListName:" + buddyVO.getListName() + "of dial:" + batchBuddyVO
                                            .getSenderMSISDN());
                                _logger.errorTrace(METHOD_NAME, e);
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                    "ScheduledMultipleCreditTransferProcess[resetSuccessiveFailCount]", "", "", "", "Exception:" + e.getMessage());
                            }// end of catch
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("scheduleRecords", "Success counter reset");
                            }
                        }

                        else if (!BTSLUtil.isNullString(errorCode) && (errorCode.equals(PretupsErrorCodesI.P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_W) || errorCode
                            .equals(PretupsErrorCodesI.P2P_SENDER_MAX_PCT_TRANS_FAILED_W) || errorCode.equals(PretupsErrorCodesI.P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_M) || errorCode
                            .equals(PretupsErrorCodesI.P2P_SENDER_MAX_PCT_TRANS_FAILED_M))) {
                            {
                                _totalCount++;
                                ambiguousCount++;
                                failCount++;
                                keyArgumentVO = new KeyArgumentVO();
                                keyArgumentVO.setKey(PretupsErrorCodesI.P2P_SENDER_SCT_RECORDS_SUBKEY);
                                ambiArray[0] = buddyVO.getBuddyMsisdn();
                                ambiArray[1] = String.valueOf(buddyVO.getPreferredAmount());
                                keyArgumentVO.setArguments(ambiArray);
                                ambiguousList.add(keyArgumentVO);
                            }
                        } else if (!BTSLUtil.isNullString(errorCode)) {
                            _totalCount++;
                            failCount++;
                            keyArgumentVO = new KeyArgumentVO();
                            keyArgumentVO.setKey(PretupsErrorCodesI.P2P_SENDER_SCT_FAIL_SUBKEY);
                            failArray[0] = buddyVO.getBuddyMsisdn();
                            failArray[1] = String.valueOf(buddyVO.getPreferredAmount());
                            keyArgumentVO.setArguments(failArray);
                            failList.add(keyArgumentVO);

                            try {
                                pstmtUpdateFailCount.clearParameters();
                                if (buddyVO.getModifiedOn() != null) {
                                    pstmtUpdateFailCount.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(buddyVO.getModifiedOn()));
                                } else {
                                    pstmtUpdateFailCount.setNull(1, Types.DATE);
                                }
                                pstmtUpdateFailCount.setString(2, buddyVO.getModifiedBy());

                                pstmtUpdateFailCount.setString(3, buddyVO.getBuddyMsisdn());
                                pstmtUpdateFailCount.setString(4, buddyVO.getListName());
                                pstmtUpdateFailCount.setString(5, buddyVO.getOwnerUser());
                                pstmtUpdateFailCount.setString(6, buddyVO.getSelectorCode());

                                pstmtUpdateFailCount.executeUpdate();
                            } catch (SQLException sqle) {
                                _logger.error("updateSuccessiveFailCount",
                                    "Update fail count for buddy:" + buddyVO.getBuddyMsisdn() + "ListName:" + buddyVO.getListName() + "of dial:" + batchBuddyVO
                                        .getSenderMSISDN());
                                _logger.errorTrace(METHOD_NAME, sqle);
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                    "ScheduledMultipleCreditTransferProcess[updateSuccessiveFailCount]", "", "", "", "SQL Exception:" + sqle.getMessage());
                            }// end of catch
                            catch (Exception e) {
                                _logger.error("updateSuccessiveFailCount",
                                    "Update fail count for buddy:" + buddyVO.getBuddyMsisdn() + "ListName:" + buddyVO.getListName() + "of dial:" + batchBuddyVO
                                        .getSenderMSISDN());
                                _logger.errorTrace(METHOD_NAME, e);
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                    "ScheduledMultipleCreditTransferProcess[updateSuccessiveFailCount]", "", "", "", "Exception:" + e.getMessage());
                            }// end of catch
                            try {
                                pstmtSelectFailCount.clearParameters();
                                pstmtSelectFailCount.setString(1, batchBuddyVO.getParentID());
                                pstmtSelectFailCount.setString(2, buddyVO.getBuddyMsisdn());
                                pstmtSelectFailCount.setString(3, batchBuddyVO.getListName());
                                rsSelectFailCount = pstmtSelectFailCount.executeQuery();
                                if (rsSelectFailCount.next()) {
                                    succFailCount = rsSelectFailCount.getInt("successive_failure_count");
                                }
                            } catch (SQLException sqle) {
                                _logger.error("getSuccessiveFailCount", "getting successive fail count for buddy:" + buddyVO.getBuddyMsisdn() + "ListName:" + buddyVO
                                    .getListName() + "of dial:" + batchBuddyVO.getSenderMSISDN());
                                _logger.errorTrace(METHOD_NAME, sqle);
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                    "ScheduledMultipleCreditTransferProcess[getSuccessiveFailCount]", "", "", "", "SQL Exception:" + sqle.getMessage());
                            }// end of catch
                            catch (Exception e) {
                                _logger.error("getSuccessiveFailCount", "getting successive fail count for buddy:" + buddyVO.getBuddyMsisdn() + "ListName:" + buddyVO
                                    .getListName() + "of dial:" + batchBuddyVO.getSenderMSISDN());
                                _logger.errorTrace(METHOD_NAME, e);
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                    "ScheduledMultipleCreditTransferProcess[getSuccessiveFailCount]", "", "", "", "Exception:" + e.getMessage());
                            }// end of catch
                            if (succFailCount > 3) {
                                try {
                                    pstmtDeleteBuddy.clearParameters();
                                    pstmtDeleteBuddy.setString(1, buddyVO.getOwnerUser());
                                    pstmtDeleteBuddy.setString(2, buddyVO.getListName());
                                    pstmtDeleteBuddy.setString(3, buddyVO.getMsisdn());
                                    pstmtDeleteBuddy.setString(4, buddyVO.getSelectorCode());
                                    delBuddyCount = pstmtDeleteBuddy.executeUpdate();
                                } catch (SQLException sqle) {
                                    _logger.error("deleteBuddyFromList", "on successive failure count > 3,deleting buddy:" + buddyVO.getMsisdn() + "ListName:" + buddyVO
                                        .getListName() + "of dial:" + batchBuddyVO.getSenderMSISDN());
                                    _logger.errorTrace(METHOD_NAME, sqle);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                        "ScheduledMultipleCreditTransferProcess[deleteBuddyFromList]", "", "", "", "SQL Exception:" + sqle.getMessage());
                                }// end of catch
                                catch (Exception e) {
                                    _logger.error("deleteBuddyFromList", "on successive failure count > 3,deleting buddy:" + buddyVO.getMsisdn() + "ListName:" + buddyVO
                                        .getListName() + "of dial:" + batchBuddyVO.getSenderMSISDN());
                                    _logger.errorTrace(METHOD_NAME, e);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                        "ScheduledMultipleCreditTransferProcess[deleteBuddyFromList]", "", "", "", "Exception:" + e.getMessage());
                                }// end of catch
                                if (delBuddyCount > 0) {

                                    message = null;
                                    array = new String[2];
                                    array[0] = String.valueOf(receiverVO.getPreferredAmount());
                                    array[1] = receiverVO.getMsisdn();
                                    key = PretupsErrorCodesI.P2P_SCT_SUCC_FAIL_DEL_BUDDY;
                                    message = BTSLUtil.getMessage(locale, key, array);
                                    try {
                                        (new PushMessage(batchBuddyVO.getSenderMSISDN(), message, null, respGW, locale)).push();
                                    } catch (Exception e) {
                                        _logger.error("scheduleRecords:", "Exception while pushing messgae to" + batchBuddyVO.getSenderMSISDN() + "ListName:" + batchBuddyVO
                                            .getListName());
                                        _logger.errorTrace(METHOD_NAME, e);
                                    }
                                    try {
                                        pstmtSelectListSize.clearParameters();
                                        pstmtSelectListSize.setString(1, batchBuddyVO.getParentID());
                                        pstmtSelectListSize.setString(2, batchBuddyVO.getListName());
                                        pstmtSelectListSize.setString(3, batchBuddyVO.getScheduleType());
                                        pstmtSelectListSize.setString(4, PretupsI.YES);
                                        rsSelectListSize = pstmtSelectListSize.executeQuery();
                                        if (rsSelectListSize.next()) {
                                            listCount = rsSelectListSize.getInt("count");
                                        }
                                        if (listCount == 0) {
                                            pstmtDeleteEmptyList.clearParameters();
                                            pstmtDeleteEmptyList.setString(1, batchBuddyVO.getParentID());
                                            pstmtDeleteEmptyList.setString(2, batchBuddyVO.getListName());
                                            listDeleted = pstmtDeleteEmptyList.executeUpdate();
                                            if (listDeleted > 0) {
                                                _logger.error("scheduleRecords", "Empty List:" + batchBuddyVO.getListName() + " deleted of dial:" + batchBuddyVO
                                                    .getSenderMSISDN());
                                            }
                                        }
                                    } catch (SQLException sqle) {
                                        _logger.error("scheduleRecords",
                                            "Selecting List Size & deleting from batch if listSize=0 of dial:" + batchBuddyVO.getSenderMSISDN() + "ListName:" + buddyVO
                                                .getListName() + "schedule type:" + batchBuddyVO.getScheduleType());
                                        _logger.errorTrace(METHOD_NAME, sqle);
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                            "ScheduledMultipleCreditTransferProcess[deleteBuddyFromList]", "", "", "", "SQL Exception:" + sqle.getMessage());
                                    }// end of catch
                                    catch (Exception e) {
                                        _logger.error("scheduleRecords",
                                            "Selecting List Size & deleting from batch if listSize=0 of dial:" + batchBuddyVO.getSenderMSISDN() + "ListName:" + buddyVO
                                                .getListName() + "schedule type:" + batchBuddyVO.getScheduleType());
                                        _logger.errorTrace(METHOD_NAME, e);
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                            "ScheduledMultipleCreditTransferProcess[deleteBuddyFromList]", "", "", "", "Exception:" + e.getMessage());
                                    }// end of catch
                                }
                            }
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("scheduleRecords", "Fail counter reset");
                            }
                        }
                    }
                    p_con.commit();
                    try {
                        pstmtSelectNextSchDate.clearParameters();
                        pstmtSelectNextSchDate.setString(1, batchBuddyVO.getParentID());
                        pstmtSelectNextSchDate.setString(2, batchBuddyVO.getListName());
                        pstmtSelectNextSchDate.setString(3, PretupsI.YES);
                        pstmtSelectNextSchDate.setString(4, batchBuddyVO.getScheduleType());
                        rsSelectNxtSchDat = pstmtSelectNextSchDate.executeQuery();
                        if (rsSelectNxtSchDat.next()) {
                            if (!(rsSelectNxtSchDat.getDate("SCHEDULE_DATE") == null)) {
                                nextScheduleDate = rsSelectNxtSchDat.getDate("SCHEDULE_DATE");
                                cal.setTime(nextScheduleDate);
                            }
                        }
                        nextSchDate = sdf.format(nextScheduleDate).toString();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("scheduleRecords", "Next Schedule date=" + nextSchDate + " BatchID" + batchBuddyVO.getBatchID() + " ScheduleType" + batchBuddyVO
                                .getScheduleType());
                        }
                    } catch (SQLException sqle) {
                        _logger.error("scheduleRecords", "selecting next schedule date for dial:" + batchBuddyVO.getSenderMSISDN() + "ListName:" + batchBuddyVO.getListName());
                        _logger.errorTrace(METHOD_NAME, sqle);
                    }// end of catch

                    if ("y".equalsIgnoreCase(p_pushMessageEnabled)) {

                        if (_totalCount > 0 && _totalCount == successCount) {
                            array = new String[2];
                            if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_SUCCESS_W;
                            } else if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_SUCCESS_M;
                            }
                            array[0] = BTSLUtil.getMessage(locale, successList);
                            if (!BTSLUtil.isNullString(nextSchDate)) {
                                array[1] = nextSchDate;
                            }
                        } else if (_totalCount > 0 && _totalCount == failCount && ambiguousCount == 0) {
                            array = new String[1];
                            if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_FAIL_W;
                            } else if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_FAIL_M;
                            }
                            array[0] = BTSLUtil.getMessage(locale, failList);
                        } else if (_totalCount > 0 && _totalCount == failCount && failCount == ambiguousCount) {
                            array = new String[1];
                            if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_ALLINSUFF_SUBKEY_W;
                            } else if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_ALLINSUFF_SUBKEY_M;
                            }
                            array[0] = BTSLUtil.getMessage(locale, ambiguousList);
                        } else if (_totalCount > 0 && _totalCount == failCount) {
                            array = new String[2];
                            array[0] = BTSLUtil.getMessage(locale, ambiguousList);
                            if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_MIXINSUFF_SUBKEY_W;
                            } else if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                                key = PretupsErrorCodesI.P2P_SENDER_SCT_MIXINSUFF_SUBKEY_M;
                            }
                            array[1] = BTSLUtil.getMessage(locale, failList);
                        } else if (_totalCount > 0 && failCount > 0 && successCount > 0) {

                            if (failCount != ambiguousCount && ambiguousCount > 0) {
                                array = new String[4];
                                if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                                    key = PretupsErrorCodesI.P2P_SENDER_SCT_RECORDS_W;
                                } else if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                                    key = PretupsErrorCodesI.P2P_SENDER_SCT_RECORDS_M;
                                }
                                array[0] = BTSLUtil.getMessage(locale, successList);
                                if (!BTSLUtil.isNullString(nextSchDate)) {
                                    array[1] = nextSchDate;
                                }
                                array[2] = BTSLUtil.getMessage(locale, ambiguousList);
                                array[3] = BTSLUtil.getMessage(locale, failList);
                            }
                            if (failCount == ambiguousCount) {
                                array = new String[3];
                                if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                                    key = PretupsErrorCodesI.P2P_SENDER_SCT_INSUFF_SUBKEY_W;
                                }
                                if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                                    key = PretupsErrorCodesI.P2P_SENDER_SCT_INSUFF_SUBKEY_M;
                                }
                                array[0] = BTSLUtil.getMessage(locale, successList);
                                if (!BTSLUtil.isNullString(nextSchDate)) {
                                    array[1] = nextSchDate;
                                }
                                array[2] = BTSLUtil.getMessage(locale, ambiguousList);
                            }
                            if (ambiguousCount == 0) {
                                array = new String[3];
                                array[0] = BTSLUtil.getMessage(locale, successList);
                                if (!BTSLUtil.isNullString(nextSchDate)) {
                                    array[1] = nextSchDate;
                                }
                                if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                                    key = PretupsErrorCodesI.P2P_SENDER_SCT_ALLFAIL_SUBKEY_W;
                                }
                                if (batchBuddyVO.getScheduleType().equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                                    key = PretupsErrorCodesI.P2P_SENDER_SCT_ALLFAIL_SUBKEY_M;
                                }
                                array[2] = BTSLUtil.getMessage(locale, failList);
                            }
                        }
                        message = null;
                        message = BTSLUtil.getMessage(locale, key, array);
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("scheduleRecords", "PushMessageEnabled:" + message);
                        }
                        try {
                            (new PushMessage(batchBuddyVO.getSenderMSISDN(), message, null, respGW, locale)).push();
                        } catch (Exception e) {
                            _logger
                                .error("scheduleRecords:", "Exception while pushing messgae to" + batchBuddyVO.getSenderMSISDN() + "ListName:" + batchBuddyVO.getListName());
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                        Thread.sleep(50);
                    }
                } catch (SQLException sqle) {
                    _logger.error("scheduleRecords", "SQLException " + sqle.getMessage());
                    _logger.errorTrace(METHOD_NAME, sqle);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduleMultipleCreditTransferProcess[scheduleRecords]", "", "", "", "SQL Exception:" + sqle.getMessage());
                }// end of catch
                catch (Exception e) {
                    _logger.error("scheduleRecords", "Exception " + e.getMessage());
                    _logger.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ScheduleMultipleCreditTransferProcess[scheduleRecords]", "", "", "", "SQL Exception:" + e.getMessage());
                }// end of catch
                if (_logger.isDebugEnabled()) {
                    _logger
                        .debug(
                            "scheduleRecords",
                            "Detailed summary of :" + "List Count:" + +_totalCount + "Success Count:" + successCount + "Fail Count:" + failCount + "Ambiguous Count:" + ambiguousCount);
                }
                ScheduleCreditTransferLogSummary.log(batchBuddyVO, _totalCount, successCount, failCount, ambiguousCount);

            }
        } catch (BTSLBaseException be) {
            _logger.error("scheduleRecords", "BTSLBaseException be:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("scheduleRecords", "Exception= " + e.getMessage());
        }

        finally {
        	 try {
                 if (p_con != null) {
                     p_con.commit();
                 }
             } catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
             }
        	try {
                 if (rsSelectBarred != null) {
                     rsSelectBarred.close();
                 }
             } catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
             }
             try {
                 if (rsSelectFailCount != null) {
                     rsSelectFailCount.close();
                 }
             } catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
             }
             try {
                 if (rsSelectExecCount != null) {
                     rsSelectExecCount.close();
                 }
             } catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
             }
             try {
                 if (rsSelectErrList != null) {
                     rsSelectErrList.close();
                 }
             } catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
             }
             try {
                 if (rsSelectNxtSchDat != null) {
                     rsSelectNxtSchDat.close();
                 }
             } catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
             }
             try {
                 if (rsSelectListSize != null) {
                     rsSelectListSize.close();
                 }
             } catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
             }
        	try {
                if (pstmtSelectBarred != null) {
                    pstmtSelectBarred.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectFailCount != null) {
                    pstmtSelectFailCount.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtResetFailCount != null) {
                    pstmtResetFailCount.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdateFailCount != null) {
                    pstmtUpdateFailCount.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtDeleteBuddy != null) {
                    pstmtDeleteBuddy.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdateExecCount != null) {
                    pstmtUpdateExecCount.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtDeleteBatch != null) {
                    pstmtDeleteBatch.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectExecCount != null) {
                    pstmtSelectExecCount.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectErrList != null) {
                    pstmtSelectErrList.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectNextSchDate != null) {
                    pstmtSelectNextSchDate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectListSize != null) {
                    pstmtSelectListSize.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtDeleteEmptyList != null) {
                    pstmtDeleteEmptyList.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
           
            if (_logger.isDebugEnabled()) {
                _logger.debug("scheduleRecords", "Exiting scheduleRecords Total Record Processed::" + p_batchList.size());
            }
        }
    }

    public String generateRequestResponse(Connection p_con, P2PBatchesVO p_batchBuddyVO, BuddyVO p_buddyVO, String p_senderMsisdn) throws BTSLBaseException

    {
        final String METHOD_NAME = "generateRequestResponse";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_batchBuddyVO" + p_batchBuddyVO + "Entered p_buddyVO:" + p_buddyVO + "Entered p_senderMsisdn" + p_senderMsisdn);
        }
        final StringBuffer sbf = new StringBuffer("http://");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_IP"));
        sbf.append(":");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_PORT"));
        sbf.append("/pretups/");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_SERVICE_NAME"));
        sbf.append("?REQUEST_GATEWAY_CODE=");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_REQUEST_GATEWAY_CODE"));
        sbf.append("&REQUEST_GATEWAY_TYPE=");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_REQUEST_GATEWAY_TYPE"));
        sbf.append("&LOGIN=");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_LOGIN"));
        sbf.append("&PASSWORD=");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_PASSWORD"));
        sbf.append("&SOURCE_TYPE=");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_SOURCE_TYPE"));
        sbf.append("&SERVICE_PORT=");
        sbf.append((String) Constants.getProperty("SCHEDULED_MCLD_SERVICE_PORT"));
        final String urlString = sbf.toString();
        HttpURLConnection con = null;
        String responseXML = null;
        try {
            final String requestXML = generateMultipleCreditTransferRequest(p_con, p_batchBuddyVO, p_buddyVO, p_senderMsisdn);
            final URL url = new URL(urlString);
            final URLConnection uc = url.openConnection();
            con = (HttpURLConnection) uc;
            con.addRequestProperty("Content-Type", "text/xml");
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            try(final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));){
            

            // Send data
            wr.write(requestXML);
            wr.flush();
            // Get response
            final InputStream rd = con.getInputStream();
            int c = 0;
            while ((c = rd.read()) != -1) {
                // Process line...
                // responseXML += (char) c;
            	 responseXML += String.valueOf(Character.toChars( c ));
            }
            _logger.debug(METHOD_NAME, "Exiting responseXML ::" + responseXML);
            final int index = responseXML.indexOf("<TXNID>");
            transId = responseXML.substring(index + "<TXNID>".length(), responseXML.indexOf("</TXNID>", index));
            wr.close();
            rd.close();
            }
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ScheduledMultipleCreditTransferProcess[generateRequestResponse]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ScheduledMultipleCreditTransferProcess", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
        }

        finally {
            if (con != null) {
                con.disconnect();
            }
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Exiting:transId::" + transId);
        }
        return transId;
    }

    public String generateMultipleCreditTransferRequest(Connection p_con, P2PBatchesVO p_batchBuddyVO, BuddyVO p_buddyVO, String p_senderMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "generateMultipleCreditTransferRequest";
        String requestStr = null;
        final MCDTxnDAO mcdtxnDAO = new MCDTxnDAO();
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("generateMultipleCreditTransferRequest",
                    "Entered p_batchBuddyVO" + p_batchBuddyVO + "Entered p_buddyVO:" + p_buddyVO + "Entered p_senderMsisdn:" + p_senderMsisdn);
            }

            StringBuffer stringBuffer = null;
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version=\"1.0\"?>");
            stringBuffer.append("<COMMAND>");

            stringBuffer.append("<TYPE>SHCCTRFREQ</TYPE>");
            stringBuffer.append("<MSISDN1>" + getSystemFilteredMSISDN(p_batchBuddyVO.getSenderMSISDN()) + "</MSISDN1>");
            if ("SHA".equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                stringBuffer.append("<PIN>" + p_batchBuddyVO.getSenderPin() + "</PIN>");
            } else {
                stringBuffer.append("<PIN>" + BTSLUtil.decryptText(p_batchBuddyVO.getSenderPin()) + "</PIN>");
            }
            stringBuffer.append("<MSISDN2>" + p_buddyVO.getBuddyMsisdn() + "</MSISDN2>");
            stringBuffer.append("<AMOUNT>" + p_buddyVO.getPreferredAmount() + "</AMOUNT>");
            stringBuffer.append("<LANGUAGE1>" + p_batchBuddyVO.getSenderLangCode() + "</LANGUAGE1>");
            stringBuffer.append("<LANGUAGE2>" + p_batchBuddyVO.getSenderLangCode() + "</LANGUAGE2>");
            stringBuffer.append("<SELECTOR>" + p_buddyVO.getSelectorCode() + "</SELECTOR>");

            if (!BTSLUtil.isNullString(p_batchBuddyVO.getScheduleType())) {
                stringBuffer.append("<SCTYPE>" + p_batchBuddyVO.getScheduleType() + "</SCTYPE>");
            }
            if (!BTSLUtil.isNullString(p_batchBuddyVO.getScheduleDate().toString())) {
                stringBuffer.append("<SDATE>" + mcdtxnDAO.getNextScheduleDate(p_batchBuddyVO.getScheduleDate(), p_batchBuddyVO.getScheduleType()) + "</SDATE>");
            }
            stringBuffer.append("</COMMAND>");
            requestStr = stringBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("generateMultipleCreditTransferRequest", "Exiting requestStr::" + requestStr);
            }

        } catch (BTSLBaseException be) {
            _logger.error("generateMultipleCreditTransferRequest", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("generateMultipleCreditTransferRequest", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("generateMultipleCreditTransferRequest", "Exiting requestStr:" + requestStr);
            }
        }
        return requestStr;
    }

    /**
     * @param p_con
     * @param p_processId
     * @return
     * @throws BTSLBaseException
     */
    private int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
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
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ScheduledMultipleCreditTransferProcess[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ScheduledMultipleCreditTransferProcess", "markProcessStatusAsComplete", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

    private String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "getSystemFilteredMSISDN";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getSystemFilteredMSISDN", "Entered p_msisdn:" + p_msisdn);
        }
        String msisdn = null;
        boolean prefixFound = false;
        String prefix = null;
        String old_prefix = null;
        String new_prefix = null;
        String new_old_mapping = null;
        try {
            if (p_msisdn.length() >= ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getSystemFilteredMSISDN", "((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)):" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)));
                }

                final StringTokenizer strTok = new StringTokenizer(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)), ",");
                while (strTok.hasMoreTokens()) {
                    prefix = strTok.nextToken();
                    if (p_msisdn.startsWith(prefix, 0)) {
                        prefixFound = true;
                        break;
                    } else {
                        continue;
                    }
                }
                if (prefixFound) {
                    msisdn = p_msisdn.substring(prefix.length());
                } else {
                    msisdn = p_msisdn;
                }

                final StringTokenizer strToken = new StringTokenizer(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_MIGRATION_LIST_CODE)), ",");
                while (strToken.hasMoreTokens()) {
                    new_old_mapping = strToken.nextToken();
                    old_prefix = new_old_mapping.substring(0, new_old_mapping.indexOf(':'));
                    new_prefix = new_old_mapping.substring(new_old_mapping.indexOf(':') + 1);

                    if (msisdn.startsWith(old_prefix, 0) && ((old_prefix.length() == new_prefix.length() && msisdn.length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) || (old_prefix
                        .length() < new_prefix.length() && msisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()))) {
                        msisdn = msisdn.replaceFirst(new_prefix, old_prefix);
                        break;
                    }
                }

            } else {
                msisdn = p_msisdn;
            }
        } catch (Exception e) {
            _logger.error("getSystemFilteredMSISDN", "Exception while getting the mobile no from passed no=" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[getSystemFilteredMSISDN]", "", p_msisdn,
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getSystemFilteredMSISDN", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getSystemFilteredMSISDN", "Exiting Filtered msisdn=" + msisdn);
            }
        }
        return msisdn;
    }

    public String getNextScheduleDate(Date schDate, String schType) {
        final Calendar calendar = BTSLDateUtil.getInstance();
        calendar.setTime(schDate);
        Date nextScheduleDate = null;
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        if (schType.equals(PretupsI.SCHEDULE_TYPE_DAILY_FILTER)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            calendar.add(Calendar.MONTH, 1);
            nextScheduleDate = calendar.getTime();
        }
        return sdf.format(nextScheduleDate).toString();
    }

}
