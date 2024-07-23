package com.btsl.pretups.iat.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.iat.businesslogic.IATDAO;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iat.logging.IATAmbProcessLog;
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.iat.businesslogic.IATTxnDAO;

/**
 * This process will be applicable only for IAT enabled version it will not work
 * for without IAT.
 * 
 * @(#)IATAmbiguousProcess
 *                         Copyright(c) 2009, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         vikascyadav july 06, 2009 Initial Creation
 *                         This class will be used for settle the IAt ambiguous
 *                         transaction in sender as well as reciver zebra
 */

public class IATAmbiguousProcess {

    private static final Log _log = LogFactory.getLog(IATAmbiguousProcess.class.getName());

    private static ArrayList _abgTxnList = new ArrayList();// load all the
                                                          // ambiguous cases.
    private static ArrayList _failList = new ArrayList(); // having all the fail
                                                         // txns after IAT check
    private static ArrayList _successList = new ArrayList();// having all the
                                                           // succees txns txns
                                                           // after IAT check
    private static ArrayList _receiverAbgTxnList = new ArrayList();// having all
                                                                  // asw a
                                                                  // receiver
                                                                  // abg txns
    private static final long sleepTime = 100;
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static String _iatAbgFileNameForTransaction = null;
    private static String _iatRecAbgDirectoryPathAndName = null;
    private static int totalNumberOfIATAbgTxns = 0;
    private static int failedIATTxns = 0;
    private static int successIATTxns = 0;
    private static int noOfSettleTxnInDB = 0;

    /*
     * This class will be used for settle the IAt ambiguous transaction in
     * sender as well as reciver zebra.
     * 1.Load all the IAT transactions whose status is 205 or 250 and
     * gateway_type=EXTGW or iatTransferType
     * is equal to IAT and transfer date time is less than 15 min from current
     * date time.
     * 2. Put all the transactions in a list.
     * 3. Iterate the list take the first transaction
     * 4. If value of IAT Transfer Type is �IAT� then in this transaction zebra
     * behaves like a sender otherwise it will be the receiver. if Transaction
     * belongs from sender zebra then Load IAT transfer item VO also for that
     * transaction.
     * 5. Populate IAT interface VO on the basis of c2s transferVO and
     * IATtransferitemVO.
     * 6. Now check IAT transaction status for ALL picked transactions one by
     * one.
     * 7. If IAT transaction status is fail then add that transaction in fail
     * list or if transaction is success then add it to success list. If IAT
     * status is still ambiguous keep it as it is.
     * 8. Take the success and fail list and settle it in Zebra system.
     * We can get three types of final transaction status from IAT �
     * � Success
     * For success transaction (at IAT) makes success in Zebra for that
     * transaction.
     * 
     * � Fail
     * For Fail call the existing method for settlement
     * 
     * � Ambiguous
     * Keep as it is no change in transaction status.
     * 
     * 9. Write a log contains following information � Transfer ID, Transaction
     * status at IAT, success transactions, failed transactions, Total number of
     * IAT ambiguous transactions, remaining ambiguous transactions after
     * processing.
     * 10. Separate class for searching the IAT and calling check Status API.
     */

    public static void main(String args[]) {
        final String methodName = "main";
        if (args.length != 2) {
            _log.info(methodName, "Usage : IATAmbiguousProcess [Constants file] [Pocess LogConfig file]");
            return;
        }
        File constantsFile = null;
		File logconfigFile = null;
		try {
			constantsFile = Constants.validateFilePath(args[0]);
			if (!constantsFile.exists()) {
			   System.out.println("IATAmbiguousProcess main() Constants file not found on location:: " + constantsFile.toString());
			    return;
			}
			logconfigFile = Constants.validateFilePath(args[1]);
			if (!logconfigFile.exists()) {
			   System.out.println("IATAmbiguousProcess main()  Process Logconfig file not found on location:: " + logconfigFile.toString());
			    return;
			}
		} catch (Exception e) {
			System.out.println("Exception thrown in C2sMisDataProcessingNew: Not able to load files" + e);
            ConfigServlet.destroyProcessCache();
            return;
		}

        try {
            // Load process cache
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            FileCache.loadAtStartUp();
            // below two caches for econciliation process
            NetworkProductServiceTypeCache.refreshNetworkProductMapping();
            NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
            // Iat specific cache
            IATNWServiceCache.loadIATNWServiceCache();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.info(methodName, "IATAmbiguousProcess main() Not able to load Process Cache");
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            // Handle IAT ambiguous transactions
            handleIATAmbiguousTxn();

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "[main]", "", "", "", "Exception:" + e.getMessage());
            _log.info(methodName, "IATAmbiguousProcess main() Not able to settle the ambiguous transactions.");
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method to handleIATAmbiguousTxn
     * 
     * @return void
     */
    public static void handleIATAmbiguousTxn() {
        final String methodName = "handleIATAmbiguousTxn";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered");
        }
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        Date currentDateTime = new Date();
        Date processedUpto = null;
        C2STransferVO c2sTransferVO = null;
        IATTransferItemVO iatTransferItemVO = null;
        IATInterfaceVO iatInterfaceVO = null;
        IATDAO iatDAO = null;
        IATTxnDAO iatTxnDAO = null;
        CheckIATStatus checkIATStatus = null;
        int beforeInterval = 0;
        int updateCount = 0;

        Date currentdate = null;
        Date compDate = null;
        try {
            IATAmbiguousProcess iatAmgProcess = new IATAmbiguousProcess();
            // getting all the required parameters from Constants.props to write
            // IAT Rec abg txns
            loadConstantParameters();
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATAmbiguousProcess[handleIATAmbiguousTxn]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = ProcessI.IAT_AMBIGUOUS_PROCESS;
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt(_processStatusVO.getBeforeInterval());
            if (statusOk) {
                con.commit();
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    iatDAO = new IATDAO();
                    currentdate = new Date();
                    long useTime = currentdate.getTime() - (beforeInterval * 1000 * 60);
                    compDate = new Date(useTime);
                    // Load all the IAT transactions whose status is 205 or 250
                    // and gateway_type=EXTGW or iatTransferType is equal to IAT
                    // and transfer date time is less than 15 min from current
                    // date time.

                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_IAT_RUNNING))).booleanValue()) {
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHECK_REC_TXN_AT_IAT))).booleanValue()) {
                            _abgTxnList = iatTxnDAO.loadIATC2STransaction(con, compDate);
                        } else {
                            _abgTxnList = iatTxnDAO.loadIATC2STransactionForSenderOnly(con, compDate);
                        }
                    } else {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "IAT is not running so this process will not used");
                        }
                        _log.error(methodName, "IAT is not running so this process will not used");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "IATAmbiguousProcess[handleIATAmbiguousTxn]", "", "", "", "IAT is not running so this process will not used");
                        return;
                    }
                    if (_abgTxnList != null) {
                        totalNumberOfIATAbgTxns = _abgTxnList.size();
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "number of ambiguous txn picked from database =" + totalNumberOfIATAbgTxns);
                        }
                        if (_log.isErrorEnabled()) {
                            _log.error(methodName, "number of ambiguous txn picked from database =" + totalNumberOfIATAbgTxns);
                        }
                        IATAmbProcessLog.log("Txn picked from database", "", "NA", 0, 0, totalNumberOfIATAbgTxns, 0, "NA", "NA");
                        if (totalNumberOfIATAbgTxns > 0) {
                            for (int i = 0; i < totalNumberOfIATAbgTxns; i++) {
                                c2sTransferVO = (C2STransferVO) _abgTxnList.get(i);
                                try {
                                    if (PretupsI.IAT_TRANSACTION_TYPE.equalsIgnoreCase(c2sTransferVO.getExtCreditIntfceType())) {
                                        iatTransferItemVO = new IATTransferItemVO();
                                        iatTransferItemVO = iatDAO.loadIATTransferVO(con, c2sTransferVO.getTransferID());
                                    }
                                } catch (Exception be) {
                                    _log.errorTrace(methodName, be);
                                    _log.error(methodName, "not able to load IAT for txn id =" + c2sTransferVO.getTransferID());
                                    continue;
                                }

                                // call check status for getting final status
                                // from IAT
                                try {
                                    // populateIAT interface VO from above vo's
                                    iatInterfaceVO = new IATInterfaceVO();
                                    iatInterfaceVO = iatAmgProcess.populateInterfaceVO(c2sTransferVO, iatTransferItemVO, iatInterfaceVO);
                                    checkIATStatus = new CheckIATStatus();
                                    // the final transaction status at IAT
                                    checkIATStatus.checkIATTxnStatus(iatInterfaceVO, c2sTransferVO.getExtCreditIntfceType(), "PROCESS");
                                } catch (Exception be) {
                                    _log.errorTrace(methodName, be);
                                    _log.error(methodName, "not able to get the status from IAT for txn id =" + c2sTransferVO.getTransferID());
                                    continue;
                                }
                                // if transaction is success on iat then it will
                                // added in successlist if fail then in fail
                                // list other wise leave that transaction.
                                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(iatInterfaceVO.getIatINTransactionStatus())) {
                                    try {
                                        if (PretupsI.IAT_TRANSACTION_TYPE.equalsIgnoreCase(c2sTransferVO.getExtCreditIntfceType())) {
                                            updateCount = iatDAO.updateIATTransferItemForAbgTxns(con, iatInterfaceVO, PretupsI.TXN_STATUS_SUCCESS);
                                            if (updateCount < 0) {
                                                continue;
                                            }
                                        }
                                    } catch (Exception be) {
                                        _log.errorTrace(methodName, be);
                                        _log.error(methodName, "not able to update Iat transfer item table for txn id =" + c2sTransferVO.getTransferID());
                                        continue;
                                    }

                                    _successList.add(c2sTransferVO.getTransferID().trim());
                                    // _receiverAbgTxnList.add(c2sTransferVO);
                                    successIATTxns = _successList.size();
                                    // update iat itemvo table.

                                }
                                // discussed with sanjeev and dhiraj if txn
                                // status not equals to 200 or 250 then mark it
                                // fail.
                                else if (!(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(iatInterfaceVO.getIatINTransactionStatus()))) {
                                    try {
                                        if (PretupsI.IAT_TRANSACTION_TYPE.equalsIgnoreCase(c2sTransferVO.getExtCreditIntfceType())) {
                                            updateCount = iatDAO.updateIATTransferItemForAbgTxns(con, iatInterfaceVO, PretupsErrorCodesI.TXN_STATUS_FAIL);
                                            if (updateCount < 0) {
                                                continue;
                                            }
                                        }
                                    } catch (Exception be) {
                                        _log.errorTrace(methodName, be);
                                        _log.error(methodName, "not able to update Iat transfer item table for txn id =" + c2sTransferVO.getTransferID());
                                        continue;
                                    }
                                    _failList.add(c2sTransferVO.getTransferID().trim());
                                    // _receiverAbgTxnList.add(c2sTransferVO);
                                    failedIATTxns = _failList.size();
                                    // update iat itemvo table.

                                }
                                // check the final status from this VO and
                                // settle accordingly
                                // wite the log
                                IATAmbProcessLog.log("Checking Status from IAT", c2sTransferVO.getTransferID(), iatInterfaceVO.getIatINTransactionStatus(), 0, 0, totalNumberOfIATAbgTxns, totalNumberOfIATAbgTxns - (successIATTxns + failedIATTxns), c2sTransferVO.getServiceType(), c2sTransferVO.getExtCreditIntfceType());
                                Thread.sleep(100);
                            }
                            IATAmbProcessLog.log("After checking IAT Before settle in Zebra", "", "", successIATTxns, failedIATTxns, totalNumberOfIATAbgTxns, totalNumberOfIATAbgTxns - (successIATTxns + failedIATTxns), "", "");
                            // write receiver abg txn in file
                            try {
                                writeReceiverSettledAbgTxnInFile(_iatRecAbgDirectoryPathAndName, _iatAbgFileNameForTransaction, _receiverAbgTxnList);
                            } catch (Exception e) {
                                _log.errorTrace(methodName, e);
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "IATAmbiguousProcess[handleIATAmbiguousTxn]", "", "", "", "Exception while writing in file");
                                _log.error(methodName, "not able to write receiver ambiguous txns in file ");
                            }

                            // Finally settle ambiguous transaction inside Zebra
                            iatAmgProcess.handleChannelAmbigousCases(con, _failList, _successList);
                        }

                    }
                } else {
                    throw new BTSLBaseException("IATAmbiguousProcess", methodName, PretupsErrorCodesI.IAT_AMG_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
            _processStatusVO.setExecutedOn(BTSLUtil.getSQLDateFromUtilDate(currentDateTime));
        } catch (Exception e) {
            _log.error(methodName, "Error:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "IATAmbiguousProcess[handleIATAmbiguousTxn]", "", "", "", "IATAmbiguousProcess fail ");
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
                    }
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                ;
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "IATAmbiguousProcess[handleIATAmbiguousTxn]", "", "", "", "Exception while closing statement in IAT ambiguous process method");
                _log.error(methodName, "Error:" + ex.getMessage());
                _log.info(methodName, "Exception while closing statement in IAT ambiguous process method ");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exited");
            }
        }
    }

    /**
     * Method to handle Cases : Success as well as Fail
     * 
     * @param p_con
     * @param p_failList
     * @param p_successList
     */
    public void handleChannelAmbigousCases(Connection p_con, ArrayList p_failList, ArrayList p_successList) {
        final String methodName = "handleChannelAmbigousCases";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_failList.size=" + p_failList.size() + " p_successList.size()=" + p_successList.size());
        }
        try {
            reconcileTransactionList(p_con, PretupsErrorCodesI.TXN_STATUS_FAIL, "Fail", p_failList);
            IATAmbProcessLog.log("Txn settled as fail in DB", "", "Fail", 0, noOfSettleTxnInDB, 0, 0, "", "");
            noOfSettleTxnInDB = 0;
            reconcileTransactionList(p_con, PretupsErrorCodesI.TXN_STATUS_SUCCESS, "Success", p_successList);
            IATAmbProcessLog.log("Txn settled as success in DB and Exiting", "", "Success", noOfSettleTxnInDB, 0, 0, 0, "", "");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATAmbiguousProcess[handleChannelAmbigousCases]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exited");
            }
        }
    }

    /**
     * Method to populate Interface VO
     * 
     * @param p_c2sTransferVO
     * @param p_iatTransferItemVO
     * @param p_iatInterfaceVO
     */
    public IATInterfaceVO populateInterfaceVO(C2STransferVO p_c2sTransferVO, IATTransferItemVO p_iatTransferItemVO, IATInterfaceVO p_iatInterfaceVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("populateInterfaceVO", " enteredp_c2sTransferVO.getTransferID()" + p_c2sTransferVO.getTransferID());
        }
        C2STransferItemVO senderTransferItemVO = (C2STransferItemVO) (p_c2sTransferVO.getTransferItemList().get(1));

        p_iatInterfaceVO.setIatGatewayCode(p_c2sTransferVO.getRequestGatewayCode());
        p_iatInterfaceVO.setIatInterfaceId(senderTransferItemVO.getInterfaceID());
        p_iatInterfaceVO.setIatSenderNWID(p_c2sTransferVO.getNetworkCode());
        p_iatInterfaceVO.setIatSenderNWTRXID(p_c2sTransferVO.getTransferID());
        p_iatInterfaceVO.setIatServiceType(p_c2sTransferVO.getServiceType());
        p_iatInterfaceVO.setIatSourceType(p_c2sTransferVO.getSourceType());
        if (p_iatTransferItemVO != null) {
            p_iatInterfaceVO.setIatTRXID(p_iatTransferItemVO.getIatTxnId());
        }
        p_iatInterfaceVO.setIatReceiverMSISDN(p_c2sTransferVO.getReceiverMsisdn());
        if (_log.isDebugEnabled()) {
            _log.debug("populateInterfaceVO", " exited" + p_iatInterfaceVO.toString());
        }
        return p_iatInterfaceVO;
    }

    /**
     * Method to perform reconcilation process
     * 
     * @param p_con
     * @param p_status
     * @param p_statusTxt
     * @param p_list
     */

    public void reconcileTransactionList(Connection p_con, String p_status, String p_statusTxt, ArrayList p_list) {
        final String methodName = "reconcileTransactionList";
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append("Entered with p_status=");
        	sb.append( p_status);
        	sb.append(" p_statusTxt=");
        	sb.append(p_statusTxt);
        	sb.append(" p_list.size=");
        	sb.append(p_list.size());
            _log.debug(methodName, sb.toString());
        }
        try {
            String transactionID = null;
            C2STransferVO c2sTransferVO = null;
            C2STransferItemVO receiverItemVO = null;
            int updateCount = 0;
            java.util.Date currentDate = new Date();
            C2STransferDAO c2STransferDAO = new C2STransferDAO();
            int lists=p_list.size();
            for (int i = 0; i < lists; i++) {
                transactionID = (String) p_list.get(i);
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Got transactionID=" + transactionID);
                }
                updateCount = 0;
                receiverItemVO = null;
                try {
                    c2sTransferVO = loadC2STransferVO(p_con, transactionID);
                    if (c2sTransferVO == null) {
                        _log.info(methodName, "For transactionID=" + transactionID + " No information available in transfers table or already settled");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HandleUnsettledCases[reconcileTransactionList]", transactionID, "", "", "No information available in transfers table or already settled");
                        continue;
                    }
                    if (PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(c2sTransferVO.getTxnStatus())) {
                        updateCount = c2STransferDAO.markC2SReceiverAmbiguous(p_con, c2sTransferVO.getTransferID());
                        receiverItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(1);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HandleUnsettledCases[reconcileTransactionList]", c2sTransferVO.getTransferID(), "", "", "Receiver transfer status changed to '250' from " + receiverItemVO.getTransferStatus());
                        receiverItemVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
                    }
                    c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    c2sTransferVO.setTransferStatus(p_status);
                    c2sTransferVO.setModifiedOn(currentDate);
                    ArrayList newEntries = ChannelUserBL.prepareNewC2SReconList(p_con, c2sTransferVO, p_statusTxt, null);
                    c2sTransferVO.setTransferItemList(newEntries);
                    updateCount = c2STransferDAO.updateReconcilationStatus(p_con, c2sTransferVO);
                    if (updateCount > 0) {
                        p_con.commit();
                        if (c2sTransferVO.getOtherInfo1() != null) {
                            BalanceLogger.log((UserBalancesVO) c2sTransferVO.getOtherInfo1());
                        }
                        // if differential commission is given by the
                        // reconciliation then add the balance logger into the
                        // system.
                        if (c2sTransferVO.getOtherInfo2() != null) {
                            BalanceLogger.log((UserBalancesVO) c2sTransferVO.getOtherInfo2());
                        }
                        noOfSettleTxnInDB++;
                        // write in IATAmbProcessLog
                        IATAmbProcessLog.log("Process to settle in DB", c2sTransferVO.getTransferID(), p_status, 0, 0, totalNumberOfIATAbgTxns, totalNumberOfIATAbgTxns - (noOfSettleTxnInDB), c2sTransferVO.getServiceType(), c2sTransferVO.getExtCreditIntfceType());

                        _receiverAbgTxnList.add(c2sTransferVO);// for writing
                                                               // the info in
                                                               // file
                        PushMessage pushMessage = new PushMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), c2sTransferVO.getSenderReturnMessage(), null, null, ((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale());
                        pushMessage.push();

                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "TransactionID=" + transactionID + " Succesfully Settled to status=" + p_status);
                        }
                    } else {
                        p_con.rollback();
                        _log.error(methodName, "TransactionID=" + transactionID + " Not able Settled to status=" + p_status);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HandleUnsettledCases[reconcileTransactionList]", transactionID, "", "", "Not able Settled to status=" + p_status);
                    }
                    p_con.rollback();
                } catch (BTSLBaseException be) {
                    _log.errorTrace(methodName, be);
                    p_con.rollback();
                    StringBuilder sb = new StringBuilder("");
                    sb.append("TransactionID=");
                    sb.append(transactionID);
                    sb.append(" Not able Settled to status=");
                    sb.append(p_status);
                    sb.append(" getting Exception=");
                    sb.append(be.getMessage());
                    _log.error(methodName,sb.toString());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HandleUnsettledCases[reconcileTransactionList]", transactionID, "", "", "Not able Settled to status=" + p_status + " getting Exception=" + be.getMessage());
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    p_con.rollback();
                    StringBuilder sb = new StringBuilder("");
                    sb.append("TransactionID=");
                    sb.append(transactionID);
                    sb.append(" Not able Settled to status=");
                    sb.append(p_status);
                    sb.append(" getting Exception=");
                    sb.append(e.getMessage());
                    _log.error(methodName, sb.toString());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HandleUnsettledCases[reconcileTransactionList]", transactionID, "", "", "Not able Settled to status=" + p_status + " getting Exception=" + e.getMessage());
                } finally {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[reconcileTransactionList]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to load the TransferVO based on transfer ID
     * 
     * @param p_con
     * @param p_transferID
     * @return
     */
    public C2STransferVO loadC2STransferVO(Connection p_con, String p_transferID) {
    	//local_index_implemented
        final String methodName = "loadC2STransferVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID=" + p_transferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;
        try {
            C2STransferDAO c2STransferDAO = new C2STransferDAO();
            StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name, CTRF.transfer_id, ");
            selectQueryBuff.append("CTRF.transfer_date, CTRF.transfer_date_time, CTRF.network_code, sender_id,");
            selectQueryBuff.append("CTRF.sender_category, CTRF.product_code, CTRF.sender_msisdn, CTRF.receiver_msisdn, ");
            selectQueryBuff.append("CTRF.receiver_network_code, CTRF.transfer_value, CTRF.error_code, CTRF.request_gateway_type, ");
            selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
            selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
            selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
            selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
            selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
            selectQueryBuff.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
            selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
            selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
            selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
            selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
            selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,U.owner_id ");
            selectQueryBuff.append(", UP.phone_language, UP.msisdn, UP.country phcountry,CTRF.ext_credit_intfce_type,CTRF.penalty,CTRF.OWNER_PENALTY ");
            selectQueryBuff.append("FROM c2s_transfers CTRF, products PROD,service_type ST,users U,key_values KV,key_values KV1,user_phones UP   ");
            selectQueryBuff.append("WHERE CTRF.transfer_date=? AND CTRF.transfer_id=? AND U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id AND KV.key(+)=CTRF.error_code AND KV.type(+)=? ");
            selectQueryBuff.append("AND KV1.key(+)=CTRF.transfer_status AND KV1.type(+)=? ");
            selectQueryBuff.append("AND CTRF.product_code=PROD.product_code ");
            selectQueryBuff.append("AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
            selectQueryBuff.append("AND ST.service_type=CTRF.service_type ");
            selectQueryBuff.append("AND (CTRF.transfer_status=? OR CTRF.transfer_status=? ) ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
            pstmtSelect.setString(i++, p_transferID);
            pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));
                c2sTransferVO.setOwnerUserID(rs.getString("owner_id"));
                c2sTransferVO.setErrorMessage(rs.getString("value"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setErrorCode(rs.getString("error_code"));
                c2sTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                c2sTransferVO.setReferenceID(rs.getString("reference_id"));
                c2sTransferVO.setServiceType(rs.getString("service_type"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                c2sTransferVO.setLanguage(rs.getString("language"));
                c2sTransferVO.setCountry(rs.getString("country"));
                c2sTransferVO.setSkey(rs.getLong("skey"));
                c2sTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                c2sTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                c2sTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                c2sTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                c2sTransferVO.setQuantity(rs.getLong("quantity"));
                c2sTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                c2sTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                c2sTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                c2sTransferVO.setCreatedOn(rs.getDate("created_on"));
                c2sTransferVO.setCreatedBy(rs.getString("created_by"));
                c2sTransferVO.setModifiedOn(rs.getDate("modified_on"));
                c2sTransferVO.setModifiedBy(rs.getString("modified_by"));
                c2sTransferVO.setTransferStatus(rs.getString("txn_status"));
                c2sTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                c2sTransferVO.setVersion(rs.getString("version"));
                c2sTransferVO.setCardGroupID(rs.getString("card_group_id"));
                c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                c2sTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                c2sTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                c2sTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                c2sTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                c2sTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                c2sTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                c2sTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                c2sTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                c2sTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                c2sTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                c2sTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                c2sTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                c2sTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                c2sTransferVO.setDifferentialGiven(rs.getString("differential_given"));
                c2sTransferVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                c2sTransferVO.setTxnStatus(rs.getString("transfer_status"));
                c2sTransferVO.setSourceType(rs.getString("source_type"));
                c2sTransferVO.setRoamPenalty(rs.getLong("PENALTY"));
                c2sTransferVO.setRoamPenaltyOwner(rs.getLong("OWNER_PENALTY"));
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("commission_profile_id"));
                channelUserVO.setCategoryCode(rs.getString("sender_category"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                c2sTransferVO.setSenderVO(channelUserVO);
                c2sTransferVO.setTransferItemList(c2STransferDAO.loadC2STransferItemsVOList(p_con, p_transferID));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[loadC2STransferVO]", "", "", "", "SQL Exception:" + sqle.getMessage());
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[loadC2STransferVO]", "", "", "", "Exception:" + e.getMessage());
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally

        return c2sTransferVO;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String methodName = "markProcessStatusAsComplete";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exception= " + e.getMessage());
            }
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

    /**
     * writeReceiverSettledAbgTxnInFile
     * 
     * @param p_dirPath
     *            String
     * @param p_fileName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_beingProcessedDate
     *            Date
     * @param p_fileEXT
     *            String
     * @param p_maxFileLength
     *            long
     * @param rst1
     *            ResultSet
     * @param rst2
     *            ResultSet
     * @return void
     * @throws Exception
     */
    private static void writeReceiverSettledAbgTxnInFile(String p_dirPath, String p_fileName, ArrayList _abgTxnList) throws BTSLBaseException {
        final String methodName = "writeReceiverSettledAbgTxnInFile";
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append(" Entered:  p_dirPath=");
        	sb.append(p_dirPath);
        	sb.append(" p_fileName=");
        	sb.append(p_fileName);
            _log.debug(methodName,sb.toString());
        }
        PrintWriter out = null;
        String fileName = p_fileName;
        File newFile = null;
        String fileData = null;
        long noOfTxn = 0;
        C2STransferVO c2sTransferVO = null;

        try {
            // creating file directory if not exisrt
            File parentDir = new File(p_dirPath);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            _log.debug(methodName, "  fileName=" + fileName);
            fileName = p_dirPath + File.separator + fileName;
            newFile = new File(fileName);
            //
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true)));
            if (_abgTxnList != null) {
                noOfTxn = _abgTxnList.size();
                if (_log.isDebugEnabled()) {
                    _log.debug("handleIATAmbiguousTxn", " number of txn written in file " + noOfTxn);
                }
                if (noOfTxn > 0) {
                    for (int i = 0; i < noOfTxn; i++) {
                        c2sTransferVO = (C2STransferVO) _abgTxnList.get(i);
                        if (!(PretupsI.IAT_TRANSACTION_TYPE.equalsIgnoreCase(c2sTransferVO.getExtCreditIntfceType()))) {
                        	StringBuilder sb = new StringBuilder("");
                        	sb.append(c2sTransferVO.getTransferID());
                        	sb.append(",");
                        	sb.append(c2sTransferVO.getNetworkCode());
                        	sb.append(",");
                        	sb.append(c2sTransferVO.getTransferValueStr());
                        	sb.append(",");
                        	sb.append(c2sTransferVO.getReceiverMsisdn());
                        	sb.append(",");
                        	sb.append(c2sTransferVO.getSenderMsisdn());
                            fileData = sb.toString();
                        }
                        out.write(fileData + "\n");
                    }
                }
            }
        } catch (Exception e) {
            _log.debug(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATAmbiguousProcess[writeReceiverSettledAbgTxnInFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("IATAmbiguousProcess", methodName, PretupsErrorCodesI.IAT_ABG_PRS_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }
    }

    /**
     * loadConstantParameters
     * This method will load the Constant parameter from Constant.props File
     * 
     * @return void
     * @throws BTSLBaseException
     */
    private static void loadConstantParameters() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadParameters", " Entered: ");
        }
        final String methodName = "loadConstantParameters";
        try {
            _iatAbgFileNameForTransaction = Constants.getProperty("IAT_REC_AMBIGUOUS_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(_iatAbgFileNameForTransaction)) {
                _log.error(methodName, " Could not find file name for transaction data in the Constants file.");
            } else {
                _log.debug(methodName, " _iatAbgFileNameForTransaction=" + _iatAbgFileNameForTransaction);
            }

            _iatRecAbgDirectoryPathAndName = Constants.getProperty("IAT_REC_AMBIGUOUS_FILE_DIRECTORY");
            if (BTSLUtil.isNullString(_iatRecAbgDirectoryPathAndName)) {
                _log.error(methodName, " Could not find directory path in the Constants file.");
            } else {
                _log.debug(methodName, " _iatRecAbgDirectoryPathAndName=" + _iatRecAbgDirectoryPathAndName);
            }

            // checking that none of the required parameters should be null
            if (BTSLUtil.isNullString(_iatAbgFileNameForTransaction) || BTSLUtil.isNullString(_iatRecAbgDirectoryPathAndName)) {
                throw new BTSLBaseException("IATAmbiguousProcess", methodName, PretupsErrorCodesI.IAT_ABG_PROCESS_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            _log.debug(methodName, " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATAmbiguousProcess[loadConstantParameters]", "", "", "", "Message:" + be.getMessage());
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception : " + e.getMessage());
            _log.errorTrace(methodName, e);
            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.IAT_ABG_PRS_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATAmbiguousProcess[loadConstantParameters]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("IATAmbiguousProcess", methodName, PretupsErrorCodesI.IAT_ABG_PRS_ERROR_EXCEPTION);
        }

    }

}
