package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * AdditionalCommisionDeduction.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ankit Singhal 01/05/2008 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Main class for deducting additional commision given from network stock
 */

public class AdditionalCommisionDeduction {
    private static final Log LOGGER = LogFactory.getLog(AdditionalCommisionDeduction.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;

    /**
     * ensures no instantiation
     */
    private AdditionalCommisionDeduction(){
    	
    }
    public static void main(String[] args) {
        final String methodName = "main";
        try {
            if (args.length != 2) {
                System.out.println("Usage : AdditionalCommisionDeduction [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants file not found on provided location.");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig file not found on provided location.");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            LOGGER.error(methodName, "Error in Loading Configuration files ...........................: " + ex);
            LOGGER.errorTrace("main", ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            LOGGER.error("main", "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace("main", be);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("main", " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        Date processedUpto = null;
        Date dateCount = null;
        Date currentDate = null;
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        int maxDoneDateUpdateCount = 0;
        ProcessStatusDAO processStatusDAO = null;
        boolean isDaySuccess = false;
        final String METHOD_NAME = "process";
        try {
            LOGGER.debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = new Date();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "AdditionalCommisionDeduction[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.ADDITIONAL_COMMISION_DEDUCTION;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24) );
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto dtae as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    _processStatusVO.setStartDate(currentDate);
                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed and executes one day before current date
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        if (!checkUnderprocessTransaction(con, dateCount)) {
                            // Get all adjustment data
                            isDaySuccess = getAndProcessAdjustmentData(con, dateCount);

                            if (isDaySuccess) {
                                // method call to update maximum date till which
                                // process has been executed
                                _processStatusVO.setExecutedUpto(dateCount);
                                _processStatusVO.setExecutedOn(currentDate);
                                processStatusDAO = new ProcessStatusDAO();
                                maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                                // if the process is successful, transaction is
                                // commit, else rollback
                                if (maxDoneDateUpdateCount > 0) {
                                    con.commit();
                                } else {
                                    con.rollback();
                                    throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_COULD_NOT_UPDATE_MAX_DONE_DATE);
                                }
                            } else {
                                con.rollback();
                            }
                        }// end if

                        Thread.sleep(500);
                    }// end date loop
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AdditionalCommisionDeduction[process]", "", "",
                        "", " AdditionalCommisionDeduction process has been executed successfully.");
                } else {
                    throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            LOGGER.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);

        } catch (Exception e) {
            /*


             */LOGGER.error(METHOD_NAME, "Exception : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AdditionalCommisionDeduction[process]", "", "", "",
                " AdditionalCommisionDeduction process could not be executed successfully.");
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(METHOD_NAME, "Exception closing connection ");
                    }
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            LOGGER.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * This method will check the existance of under process and/or ambiguous
     * transaction for the given date
     * for the date for which method is called
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @return boolean
     * @throws BTSLBaseException
     */
    private static boolean checkUnderprocessTransaction(Connection p_con, Date p_beingProcessedDate) throws BTSLBaseException {
    	//local_index_implemented
        final String METHOD_NAME = "checkUnderprocessTransaction";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        String selectQuery = null;
        try {
            selectQuery = new String(
                "SELECT 1 FROM c2s_transfers WHERE transfer_date=? AND transfer_status IN(?,?) ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "select query:" + selectQuery);
            }
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectPstmt.setString(2, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            selectPstmt.setString(3, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "AdditionalCommisionDeduction[checkUnderprocessTransaction]", "", "", "",
                    "Message: AdditionalCommisionDeduction process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            LOGGER.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);

        } catch (SQLException sqe) {
            LOGGER.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            LOGGER.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[checkUnderprocessTransaction]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            LOGGER.error(METHOD_NAME, "Exception : " + ex.getMessage());
            LOGGER.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[checkUnderprocessTransaction]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Exiting transactionFound=" + transactionFound);
            }
        }// end of finally
        return transactionFound;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, " Entered:  p_processId:" + p_processId);
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
            LOGGER.errorTrace(METHOD_NAME, e);
            LOGGER.error(METHOD_NAME, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * This method will fetch all the required transactions data from database
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @return void
     * @throws BTSLBaseException
     */
    private static boolean getAndProcessAdjustmentData(Connection p_con, Date p_beingProcessedDate) throws BTSLBaseException {
    	//local_index_implemented
        final String METHOD_NAME = "getAndProcessAdjustmentData";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        }

        NetworkStockTxnVO debitNetworkStockTxnVO = null;
        NetworkStockTxnVO creditNetworkStockTxnVO = null;
        Date currentDate = null;
        String networkCode = null;
        String networkCodeFor = null;
        String productCode = null;
        String serviceType = null;
        long negative_add_comm = 0;
        long positive_add_comm = 0;
        long negative_add_comm_rv = 0;
        long positive_add_comm_rv = 0;
        String entryType = null;
        String chnlTxnId = null;


        boolean isSuccess = true;
        int updateCount = 0;
        ResultSet _adjustmentDataRst = null;
        PreparedStatement adjSelectPstmt = null;

        final StringBuilder adjSelectQueryBuf = new StringBuilder();
        //local index implemented
        adjSelectQueryBuf.append(" SELECT ADJ.entry_type,ADJ.network_code,ADJ.network_code_for,ADJ.product_code,ADj.SERVICE_TYPE,ADJ.reference_id,");
        adjSelectQueryBuf.append(" SUM(CASE WHEN ADJ.transfer_value<0 AND ADJ.SERVICE_TYPE not in ('RCREV') THEN -ADJ.transfer_value ELSE 0 END) negative_add_comm,");
        adjSelectQueryBuf.append(" SUM(CASE WHEN ADJ.transfer_value>0 AND ADJ.SERVICE_TYPE not in ('RCREV') THEN ADJ.transfer_value ELSE 0 END) posotive_add_comm,");
        adjSelectQueryBuf.append(" SUM(CASE WHEN ADJ.transfer_value<0 AND ADJ.SERVICE_TYPE='RCREV' THEN -ADJ.transfer_value ELSE 0 END) negative_add_comm_rv,");
        adjSelectQueryBuf.append(" SUM(CASE WHEN ADJ.transfer_value>0 AND ADJ.SERVICE_TYPE='RCREV' THEN ADJ.transfer_value ELSE 0 END) posotive_add_comm_rv");
        adjSelectQueryBuf.append(" FROM ADJUSTMENTS ADJ,C2S_TRANSFERS CT ");
        adjSelectQueryBuf.append(" WHERE CT.transfer_date=? AND ADJ.adjustment_date=? AND CT.transfer_id=ADJ.reference_id and ADJ.STOCK_UPDATED='N'");
        adjSelectQueryBuf.append(" GROUP BY ADJ.entry_type,ADJ.network_code,ADJ.network_code_for,ADJ.product_code,ADJ.SERVICE_TYPE,ADJ.reference_id");

        final String adjSelectQuery = adjSelectQueryBuf.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, "Adj Select query:" + adjSelectQuery);
        }

        try {
            currentDate = new Date();
            adjSelectPstmt = p_con.prepareStatement(adjSelectQuery);
            adjSelectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            adjSelectPstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));


            _adjustmentDataRst = adjSelectPstmt.executeQuery();

            while (_adjustmentDataRst.next()) {
                networkCode = _adjustmentDataRst.getString("network_code");
                networkCodeFor = _adjustmentDataRst.getString("network_code_for");
                productCode = _adjustmentDataRst.getString("product_code");
                serviceType = _adjustmentDataRst.getString("service_type");
                negative_add_comm = _adjustmentDataRst.getLong("negative_add_comm");
                positive_add_comm = _adjustmentDataRst.getLong("posotive_add_comm");
                negative_add_comm_rv = _adjustmentDataRst.getLong("negative_add_comm_rv");
                positive_add_comm_rv = _adjustmentDataRst.getLong("posotive_add_comm_rv");
                chnlTxnId = _adjustmentDataRst.getString("reference_id");
                entryType = _adjustmentDataRst.getString("entry_type");

                LOGGER
                    .debug(
                        METHOD_NAME,
                        "networkCode:" + networkCode + " networkCodeFor:" + networkCodeFor + " productCode:" + productCode + " serviceType:" + serviceType + " negative_add_comm:" + negative_add_comm + " positive_add_comm:" + positive_add_comm + " entryType:" + entryType);

                if (positive_add_comm > 0 || negative_add_comm_rv > 0) {
                    if (negative_add_comm_rv > 0 && positive_add_comm == 0) {
                        positive_add_comm = negative_add_comm_rv;
                    }
                    // Generate transaction id and format it
                    debitNetworkStockTxnVO = NetworkStockTxnVO.getInstance();
                    debitNetworkStockTxnVO.setCreatedOn(currentDate);
                    debitNetworkStockTxnVO.setNetworkCode(networkCode);
                    final String txnId = NetworkStockBL.genrateStockTransctionID(debitNetworkStockTxnVO);

                    updateCount = prepareNetworkStockListAndCreditDebitStock(p_con, networkCode, networkCodeFor, productCode, serviceType, txnId, p_beingProcessedDate,
                        PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION, positive_add_comm, true);

                    if (updateCount <= 0) {
                        throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_CREDIT_DEBIT_STOCK);
                    } else {
                        updateCount = updateNetworkStockTransactionDetails(p_con, networkCode, networkCodeFor, productCode, serviceType, txnId, p_beingProcessedDate,
                            PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION, positive_add_comm, chnlTxnId, true);
                    }

                    if (updateCount <= 0) {
                        throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_UPDATING_NW_STOCK_TXN);
                    } else {
                        updateCount = updateAdjustmentData(p_con, p_beingProcessedDate, serviceType, true, entryType);
                    }

                    if (updateCount <= 0) {
                        throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_UPDATING_ADJ_DATA);
                    }
                }
                if (negative_add_comm > 0 || positive_add_comm_rv > 0) {
                    if (positive_add_comm_rv > 0 && negative_add_comm == 0) {
                        negative_add_comm = positive_add_comm_rv;
                    }
                    // Generate transaction id and format it
                    creditNetworkStockTxnVO = NetworkStockTxnVO.getInstance();
                    creditNetworkStockTxnVO.setCreatedOn(currentDate);
                    creditNetworkStockTxnVO.setNetworkCode(networkCode);
                    final String txnId = NetworkStockBL.genrateStockTransctionID(creditNetworkStockTxnVO);

                    updateCount = prepareNetworkStockListAndCreditDebitStock(p_con, networkCode, networkCodeFor, productCode, serviceType, txnId, p_beingProcessedDate,
                        PretupsI.NETWORK_STOCK_TRANSACTION_RETURN, negative_add_comm, false);

                    if (updateCount <= 0) {
                        throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_CREDIT_DEBIT_STOCK);
                    } else {
                        updateCount = updateNetworkStockTransactionDetails(p_con, networkCode, networkCodeFor, productCode, serviceType, txnId, p_beingProcessedDate,
                            PretupsI.NETWORK_STOCK_TRANSACTION_RETURN, negative_add_comm, chnlTxnId, false);
                    }

                    if (updateCount <= 0) {
                        throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_UPDATING_NW_STOCK_TXN);
                    } else {
                        updateCount = updateAdjustmentData(p_con, p_beingProcessedDate, serviceType, false, entryType);
                    }

                    if (updateCount <= 0) {
                        throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_UPDATING_ADJ_DATA);
                    }
                }

            }// end while loop
        } catch (SQLException sqe) {
            isSuccess = false;
            LOGGER.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            LOGGER.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[getAndProcessAdjustmentData]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (BTSLBaseException be) {
            isSuccess = false;
            LOGGER.error(METHOD_NAME, "Exception : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[getAndProcessAdjustmentData]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            isSuccess = false;
            LOGGER.error(METHOD_NAME, "Exception : " + ex.getMessage());
            LOGGER.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[getAndProcessAdjustmentData]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (adjSelectPstmt != null) {
                try {
                    adjSelectPstmt.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_adjustmentDataRst != null) {
                try {
                    _adjustmentDataRst.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Exiting ");
            }
        }// end of finally
        return isSuccess;
    }

    /**
     * This method will fetch all the required transactions data from database
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @param p_serviceType
     * @param p_isPositive
     * @param p_entryType
     *            TODO
     * @return int
     * @throws BTSLBaseException
     */
    private static int updateAdjustmentData(Connection p_con, Date p_beingProcessedDate, String p_serviceType, boolean p_isPositive, String p_entryType) throws BTSLBaseException {
        final String METHOD_NAME = "updateAdjustmentData";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME,
                " Entered: p_beingProcessedDate:" + p_beingProcessedDate + " p_serviceType:" + p_serviceType + " p_isPositive:" + p_isPositive + "p_entryType:" + p_entryType);
        }

        int updateCount = 0;
        final StringBuilder adjUpdateQueryBuf = new StringBuilder();
        //local index implemented
        adjUpdateQueryBuf.append(" UPDATE ADJUSTMENTS SET stock_updated=? WHERE adjustment_date=? and reference_id IN");
        adjUpdateQueryBuf.append(" (SELECT transfer_id FROM C2S_TRANSFERS WHERE transfer_date=?) AND entry_type=? AND service_type=?");

        final String adjUpdateQuery = adjUpdateQueryBuf.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fetchChannelupdateAdjustmentDataTransactionData", "Adj Update query:" + adjUpdateQuery);
        }

        PreparedStatement adjUpdatePstmt = null;

        try {
            int i = 0;
            adjUpdatePstmt = p_con.prepareStatement(adjUpdateQuery);
            adjUpdatePstmt.setString(++i, PretupsI.YES);
            adjUpdatePstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            adjUpdatePstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            adjUpdatePstmt.setString(++i, p_entryType);
            adjUpdatePstmt.setString(++i, p_serviceType);
            updateCount = adjUpdatePstmt.executeUpdate();
        } catch (SQLException sqe) {
            LOGGER.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            LOGGER.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdditionalCommisionDeduction[updateAdjustmentData]",
                "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", "fetchChannelTransactionData", PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            LOGGER.error(METHOD_NAME, "Exception : " + ex.getMessage());
            LOGGER.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AdditionalCommisionDeduction[updateAdjustmentData]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (adjUpdatePstmt != null) {
                try {
                    adjUpdatePstmt.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(METHOD_NAME, "Exiting ");
            }
        }// end of finally
        return updateCount;
    }

    /**
     * It Preapre the network stock from channel transfer vo and debit the
     * network stock
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_productCode
     * @param p_serviceType
     * @param p_txn_id
     * @param p_beingProcessedDate
     * @param p_txnType
     * @param p_stock
     * @param p_isDebit
     * @return int
     * @throws BTSLBaseException
     */
    public static int prepareNetworkStockListAndCreditDebitStock(Connection p_con, String p_networkCode, String p_networkCodeFor, String p_productCode, String p_serviceType, String p_txn_id, Date p_beingProcessedDate, String p_txnType, long p_stock, boolean p_isDebit) throws BTSLBaseException {
        final String METHOD_NAME = "prepareNetworkStockListAndCreditDebitStock";
        if (LOGGER.isDebugEnabled()) {
            LOGGER
                .debug(
                    METHOD_NAME,
                    "Entered p_networkCode:" + p_networkCode + " p_networkCodeFor:" + p_networkCodeFor + " p_productCode:" + p_productCode + " p_serviceType:" + p_serviceType + " p_txn_id:" + p_txn_id + " p_beingProcessedDate:" + p_beingProcessedDate + " p_txnType:" + p_txnType + " p_stock:" + p_stock + " isDebit  " + p_isDebit);
        }

        int updateCount = 0;
        NetworkStockDAO networkStockDAO = null;
        ArrayList networkStockList = null;
        NetworkStockVO networkStocksVO = null;
        try {
            networkStockDAO = new NetworkStockDAO();
            networkStockList = new ArrayList();
            networkStocksVO = NetworkStockVO.getInstance();
            networkStocksVO.setNetworkCode(p_networkCode);
            networkStocksVO.setNetworkCodeFor(p_networkCodeFor);
            networkStocksVO.setProductCode(p_productCode);
            networkStocksVO.setLastTxnNum(p_txn_id);
            networkStocksVO.setLastTxnType(p_txnType);
            networkStocksVO.setLastTxnBalance(p_stock);
            networkStocksVO.setWalletBalance(p_stock);
            networkStocksVO.setModifiedBy("SYSTEM");
            networkStocksVO.setModifiedOn(p_beingProcessedDate);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                networkStocksVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);
            }else{
            	networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
            }
            networkStockList.add(networkStocksVO);
            updateCount = networkStockDAO.updateNetworkDailyStock(p_con, networkStocksVO);

            if (p_isDebit) {
                updateCount = networkStockDAO.debitNetworkStock(p_con, networkStockList, true);
            } else {
                updateCount = networkStockDAO.creditNetworkStock(p_con, networkStockList, true);
            }
        } catch (BTSLBaseException be) {
            LOGGER.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[prepareNetworkStockListAndCreditDebitStock]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } catch (Exception e) {
            LOGGER.error("checkUnderprocessTransaction", "BTSLBaseException : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[prepareNetworkStockListAndCreditDebitStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, "Exited  updateCount " + updateCount);
        }
        return updateCount;
    }

    /**
     * update the Network Stock Transaction
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_productCode
     * @param p_serviceType
     * @param p_beingProcessedDate
     * @param p_stock
     * @param p_isDebit
     * @return int
     * @throws BTSLBaseException
     */
    public static int updateNetworkStockTransactionDetails(Connection p_con, String p_networkCode, String p_networkCodeFor, String p_productCode, String p_serviceType, String p_txnId, Date p_beingProcessedDate, String p_txnType, long p_stock, String p_chnlTxnId, boolean p_isDebit) throws BTSLBaseException {
        final String METHOD_NAME = "updateNetworkStockTransactionDetails";
        if (LOGGER.isDebugEnabled()) {
            LOGGER
                .debug(
                    METHOD_NAME,
                    "Entered p_networkCode:" + p_networkCode + " p_networkCodeFor:" + p_networkCodeFor + " p_productCode:" + p_productCode + " p_serviceType:" + p_serviceType + " p_txnId:" + p_txnId + " p_beingProcessedDate:" + p_beingProcessedDate + " p_txnType:" + p_txnType + " p_stock:" + p_stock + " p_isDebit:" + p_isDebit + " p_chnlTxnId:" + p_chnlTxnId);
        }
        int updateCount = 0;
        NetworkStockTxnItemsVO networkItemsVO = null;
        ArrayList arrayList = null;

        try {
            final NetworkStockTxnVO networkStockTxnVO = NetworkStockTxnVO.getInstance();
            networkStockTxnVO.setNetworkCode(p_networkCode);
            networkStockTxnVO.setNetworkFor(p_networkCodeFor);
            if (p_networkCode.equals(p_networkCodeFor)) {
                networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
            } else {
                networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
            }
            if (p_isDebit) {
                networkStockTxnVO.setReferenceNo("ADDCOMMDDT-" + p_serviceType + "-" + PretupsI.DEBIT);
            } else {
                networkStockTxnVO.setReferenceNo("ADDCOMMDDT-" + p_serviceType + "-" + PretupsI.CREDIT);
            }
            networkStockTxnVO.setTxnDate(p_beingProcessedDate);
            networkStockTxnVO.setRequestedQuantity(p_stock);
            networkStockTxnVO.setApprovedQuantity(p_stock);
            networkStockTxnVO.setInitiaterRemarks("");
            networkStockTxnVO.setFirstApprovedRemarks("");
            networkStockTxnVO.setSecondApprovedRemarks("");
            networkStockTxnVO.setFirstApprovedBy("SYSTEM");
            networkStockTxnVO.setSecondApprovedBy("SYSTEM");
            networkStockTxnVO.setFirstApprovedOn(p_beingProcessedDate);
            networkStockTxnVO.setSecondApprovedOn(p_beingProcessedDate);


            networkStockTxnVO.setCreatedBy("SYSTEM");
            networkStockTxnVO.setCreatedOn(p_beingProcessedDate);
            // TBD: User should be hardcoded as "SYSTEM" or
            // "CHANNEL_TRANSFER_LEVEL_SYSTEM"
            networkStockTxnVO.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            networkStockTxnVO.setModifiedOn(p_beingProcessedDate);

            networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE);
            networkStockTxnVO.setTxnNo(p_txnId);
            networkStockTxnVO.setRefTxnID(p_chnlTxnId);



            if (p_isDebit) {
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
                networkStockTxnVO.setTxnType(PretupsI.DEBIT);
            }
            // else

            else {
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
                networkStockTxnVO.setTxnType(PretupsI.CREDIT);
            }

            networkStockTxnVO.setInitiatedBy("SYSTEM");

            networkStockTxnVO.setUserID("SYSTEM");
            // TBD
            // networkStockTxnVO.setTxnMrp(


            networkItemsVO = new NetworkStockTxnItemsVO();
            networkItemsVO.setSNo(1);
            networkItemsVO.setTxnNo(p_txnId);
            networkItemsVO.setProductCode(p_productCode);
            networkItemsVO.setRequiredQuantity(p_stock);
            networkItemsVO.setApprovedQuantity(p_stock);
            networkItemsVO.setMrp(p_stock);
            networkItemsVO.setAmount(p_stock);
            networkItemsVO.setDateTime(p_beingProcessedDate);
            networkItemsVO.setStock(p_stock);

            arrayList = new ArrayList();
            arrayList.add(networkItemsVO);
            networkStockTxnVO.setNetworkStockTxnItemsList(arrayList);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                networkStockTxnVO.setTxnWallet(PretupsI.INCENTIVE_WALLET_TYPE);
            }else {
    			networkStockTxnVO.setTxnWallet(PretupsI.SALE_WALLET_TYPE);
    		}
            final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
            // call the dao to update the newtorkstoock tarnsaction
            updateCount = networkStockDAO.addNetworkStockTransaction(p_con, networkStockTxnVO);
        } catch (BTSLBaseException be) {
            updateCount = 0;
            LOGGER.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[updateNetworkStockTransactionDetails]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } catch (Exception e) {
            updateCount = 0;
            LOGGER.error(METHOD_NAME, "BTSLBaseException : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AdditionalCommisionDeduction[updateNetworkStockTransactionDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AdditionalCommisionDeduction", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, "Exited  updateCount " + updateCount);
        }

        return updateCount;
    }
}
