package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;

public class DailySubsCountSummary {
    private static Log _logger = LogFactory.getLog(DailySubsCountSummary.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;

    /**
     * ensures no instantiation
     */
    private DailySubsCountSummary(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        String module = null;
        try {
            if (arg.length != 3) {
                if (arg.length != 2) {
                    System.out.println("Usage : DailySubsCountSummary [Constants file] [LogConfig file] [Module(C2S/P2P/BOTH)]");
                    return;
                }
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("DailySubsCountSummary" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("DailySubsCountSummary" + " Logconfig File Not Found .............");
                return;
            }
            // if third argument is not set then by default data fro both C2S
            // and P2P will be fetced
            if (arg.length > 2 && !BTSLUtil.isNullString(arg[2])) {
                module = arg[2].toUpperCase();
            } else {
                module = PretupsI.MODULE_TYPE_BOTH;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process(module);
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * This method will control the whole process for fetching the required data
     * and
     * inserting the distinct subscriber count, along with total transactions
     * count and amount.
     * 
     * @param p_module
     *            String
     * @return void
     * @throws SQLException
     */
    private static void process(String p_module) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Date processedUpto = null;
        final Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        Date fromdate = null;
        Date toDate = null;

        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySubsCountSummary[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }

            processId = ProcessI.DAILY_SUBS_COUNT;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                toDate = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    if (BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate) <= 1) {
                        throw new BTSLBaseException("DailySubsCountSummary", "process", PretupsErrorCodesI.DLYSUBCNT_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    fromdate = processedUpto;
                    toDate = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);

                    if (PretupsI.C2S_MODULE.equalsIgnoreCase(p_module)) {
                        fetchAndInsertC2SData(con, fromdate, toDate);
                    } else if (PretupsI.P2P_MODULE.equalsIgnoreCase(p_module)) {
                        fetchAndInsertP2PData(con, fromdate, toDate);
                    } else {
                        fetchAndInsertC2SData(con, fromdate, toDate);
                        fetchAndInsertP2PData(con, fromdate, toDate);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailySubsCountSummary[process]", "", "", "",
                        " DailySubsCountSummary process has been executed successfully.");
                } else {
                    throw new BTSLBaseException("DailySubsCountSummary", "process", PretupsErrorCodesI.DLYSUBCNT_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailySubsCountSummary[process]", "", "", "",
                " DailySubsCountSummary process could not be executed successfully.");
            throw new BTSLBaseException("DailySubsCountSummary", "process", PretupsErrorCodesI.DLYSUBCNT_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId, toDate) == 1) {
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
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
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
        }
    }

    /**
     * This method will insert the C2S transaction data in destined table.
     * 
     * @param p_con
     *            Connection
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @throws BTSLBaseException
     * @return void
     */
    private static void fetchAndInsertC2SData(Connection p_con, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
    	//local_index_implemented
        final String METHOD_NAME = "fetchAndInsertC2SData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchAndInsertC2SData", " Entered:  p_fromDate=" + p_fromDate + " p_toDate=" + p_toDate);
        }
        int updateCount = 0;

        final StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(" INSERT INTO daily_subs_count (network_code, module, trans_date, service_type, subs_count, succ_txn_count, succ_txn_amount) ");
        queryBuf.append(" (SELECT network_code,'C2S',transfer_date,service_type,");
        queryBuf.append(" count(distinct(receiver_msisdn)) SUBS_COUNT,count(1) TXN_COUNT,SUM(transfer_value) TXN_AMOUNT");
        queryBuf.append(" FROM c2s_transfers");
        queryBuf.append(" WHERE (transfer_date BETWEEN ? and ?) AND transfer_status=?");
        queryBuf.append(" GROUP BY network_code,transfer_date,service_type)");

        final String query = queryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchAndInsertC2SData", "query:" + query);
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = p_con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            final java.sql.Date d1 = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            final java.sql.Date d2 = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            pstmt.setDate(1, d1);
            pstmt.setDate(2, d2);
            pstmt.setString(3, PretupsI.TXN_STATUS_SUCCESS);
            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            _logger.error("fetchAndInsertC2SData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySubsCountSummary[fetchAndInsertC2SData]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DailySubsCountSummary", "fetchAndInsertC2SData", PretupsErrorCodesI.DLYSUBCNT_ERROR_EXCEPTION);
        } catch (Exception ex) {
            _logger.error("fetchAndInsertC2SData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySubsCountSummary[fetchAndInsertC2SData]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("DailySubsCountSummary", "fetchAndInsertC2SData", PretupsErrorCodesI.DLYSUBCNT_ERROR_EXCEPTION);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchAndInsertC2SData", "Exiting with updateCount=" + updateCount);
            }
        }
    }

    /**
     * This method will insert the P2P transaction data in destined table.
     * 
     * @param p_con
     *            Connection
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @throws BTSLBaseException
     * @return void
     */
    private static void fetchAndInsertP2PData(Connection p_con, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "fetchAndInsertP2PData";
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchAndInsertP2PData", " Entered:  p_fromDate=" + p_fromDate + " p_toDate=" + p_toDate);
        }
        int updateCount = 0;

        final StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(" INSERT INTO daily_subs_count(network_code, module, trans_date, service_type, subs_count, succ_txn_count, succ_txn_amount) ");
        queryBuf.append(" (SELECT receiver_network_code,'P2P',transfer_date,service_type,");
        queryBuf.append(" count(distinct(receiver_msisdn)) SUBS_COUNT,count(1) TXN_COUNT,SUM(transfer_value) TXN_AMOUNT");
        queryBuf.append(" FROM subscriber_transfers");
        queryBuf.append(" WHERE (transfer_date BETWEEN ? and ?) AND transfer_status=?");
        queryBuf.append(" GROUP BY receiver_network_code,transfer_date,service_type)");

        final String query = queryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchAndInsertP2PData", "query:" + query);
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = p_con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            pstmt.setString(3, PretupsI.TXN_STATUS_SUCCESS);
            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            _logger.error("fetchAndInsertP2PData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySubsCountSummary[fetchAndInsertP2PData]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DailySubsCountSummary", "fetchAndInsertP2PData", PretupsErrorCodesI.DLYSUBCNT_ERROR_EXCEPTION);
        } catch (Exception ex) {
            _logger.error("fetchAndInsertP2PData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySubsCountSummary[fetchAndInsertP2PData]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("DailySubsCountSummary", "fetchAndInsertP2PData", PretupsErrorCodesI.DLYSUBCNT_ERROR_EXCEPTION);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchAndInsertC2SData", "Exiting with updateCount=" + updateCount);
            }
        }
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId, Date p_date) throws BTSLBaseException {
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
        _processStatusVO.setExecutedUpto(p_date);
        _processStatusVO.setExecutedOn(currentDate);

        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailySubsCountSummary[markProcessStatusAsComplete]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailySubsCountSummary", "markProcessStatusAsComplete", PretupsErrorCodesI.DLYSUBCNT_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }
}
