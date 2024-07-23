package com.btsl.pretups.p2p.reconciliation.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.transfer.businesslogic.TransferDAO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;

/**
 * @(#)HandleUnsettledP2PCases
 *                             Copyright(c) 2006, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Gurjeet Bedi Sep 26, 2006 Initial Creation
 * 
 */

public class HandleUnsettledP2PCases {
    private static ArrayList _failList = new ArrayList();
    private static ArrayList _successList = new ArrayList();
    private static Log _log = LogFactory.getLog(HandleUnsettledP2PCases.class.getName());
    private static long sleepTime = 100;

    public static void main(String args[]) {
        final String methodName = "main";
        BufferedReader in = null;
        Connection con = null;
        if (args.length != 4) {
            _log.info(methodName, "Usage : HandleUnsettledP2PCases [Constants file] [LogConfig file] [File Name] [Sleep Time] ");
            return;
        }
        final File constantsFile = new File(args[0]);
        if (!constantsFile.exists()) {
            _log.debug(methodName, "HandleUnsettledP2PCases main() Constants file not found on location:: " + constantsFile.toString());
            return;
        }
        final File logconfigFile = new File(args[1]);
        if (!logconfigFile.exists()) {
            _log.debug(methodName, "HandleUnsettledP2PCases main() Logconfig file not found on location:: " + logconfigFile.toString());
            return;
        }
        final File sourceFile = new File(args[2]);
        if (!sourceFile.exists()) {
            _log.debug(methodName, "HandleUnsettledP2PCases main() sourceFile file not found on location:: " + sourceFile.toString());
            return;
        }
        String sleepTimeStr = args[3];
        if (BTSLUtil.isNullString(sleepTimeStr)) {
            sleepTimeStr = "1000";
        }
        try {
            sleepTime = Long.parseLong(sleepTimeStr);
        } catch (Exception e) {
            sleepTime = 1000;
            _log.errorTrace(methodName, e);
        }
        try {
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final HandleUnsettledP2PCases handleUnsettledCases = new HandleUnsettledP2PCases();
            in = new BufferedReader(new FileReader(args[2]));
            handleUnsettledCases.readDataAndPutInObject(in, args[2], ",");
            in.close();
            con = OracleUtil.getSingleConnection();
            handleUnsettledCases.handleP2PAmbigousCases(con, _failList, _successList);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "[main]", "", "", "", "Exception:" + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                in = null;
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method to read the file and store in the list
     * 
     * @param p_br
     * @param p_fileName
     * @param p_separator
     */
    public void readDataAndPutInObject(BufferedReader p_br, String p_fileName, String p_separator) {
    	final String METHOD_NAME = "readDataAndPutInObject";
    	if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	
        	loggerValue.append("Entered with p_fileName=");
        	loggerValue.append(p_fileName);
        	loggerValue.append("p_separator=");
        	loggerValue.append(p_separator);
        	
            _log.debug(METHOD_NAME, loggerValue);
        }
        
        String str = null;
        String transID = null;
        String status = null;
        try {
            while (p_br.ready()) {
                str = p_br.readLine();
                if (BTSLUtil.isNullString(str)) {
                    continue;
                }
                if (str.indexOf(p_separator) == -1) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("readDataAndPutInObject", "Skipping entry (" + str + ") from " + p_fileName + " as separator (" + p_separator + ") not found");
                    }
                    continue;
                }
                if (new StringTokenizer(str, p_separator).countTokens() < 2) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("readDataAndPutInObject", "Skipping entry (" + str + ") from " + p_fileName + " as Less than 2 tokens found");
                    }
                    continue;
                }

                transID = str.substring(0, str.indexOf(p_separator));
                status = str.substring(str.lastIndexOf(p_separator) + 1, str.trim().length());
                if (!"F".equalsIgnoreCase(status) && !"S".equalsIgnoreCase(status)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("readDataAndPutInObject", "Skipping entry (" + str + ") from " + p_fileName + " as status can be only S or F");
                    }
                    continue;
                }
                if ("F".equalsIgnoreCase(status.trim())) {
                    _failList.add(transID.trim());
                } else {
                    _successList.add(transID.trim());
                }

            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledP2PCases[readDataAndPutInObject]", "",
                "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to handle Cases : Success as well as Fail
     * 
     * @param p_con
     * @param p_failList
     * @param p_successList
     */

    public void handleP2PAmbigousCases(Connection p_con, ArrayList p_failList, ArrayList p_successList) {
        if (_log.isDebugEnabled()) {
            _log.debug("handleP2PAmbigousCases", "Entered with p_failList.size=" + p_failList.size() + " p_successList.size()=" + p_successList.size());
        }
        final String METHOD_NAME = "handleP2PAmbigousCases";
        try {
            reconcileTransactionList(p_con, PretupsErrorCodesI.TXN_STATUS_FAIL, "Fail", p_failList);
            reconcileTransactionList(p_con, PretupsErrorCodesI.TXN_STATUS_SUCCESS, "Success", p_successList);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledP2PCases[handleP2PAmbigousCases]", "",
                "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to perform reconcilation process
     * 
     * @param p_con
     * @param p_status
     * @param p_list
     */
    public void reconcileTransactionList(Connection p_con, String p_status, String p_statusTxt, ArrayList p_list) {
    	 final String METHOD_NAME = "reconcileTransactionList";
    	if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered with p_status=");
        	loggerValue.append(p_status);
        	loggerValue.append(" p_statusTxt=");
        	loggerValue.append(p_statusTxt);
        	loggerValue.append(" p_list.size=");
        	loggerValue.append(p_list.size());
            _log.debug(METHOD_NAME, loggerValue);
        }
       
        try {
            String transactionID = null;
            P2PTransferVO p2pTransferVO = null;
            TransferItemVO receiverItemVO = null;
            int updateCount = 0;
            final TransferDAO transferDAO = new TransferDAO();
            final java.util.Date currentDate = new Date();
            int  lists= p_list.size();
            for (int i = 0; i <lists; i++) {
                transactionID = (String) p_list.get(i);
                if (_log.isDebugEnabled()) {
                    _log.debug("reconcileTransactionList", "Got transactionID=" + transactionID);
                }
                updateCount = 0;
                receiverItemVO = null;
                try {

                    if (i > 2000) {
                        Thread.sleep(100);
                    }
                    p2pTransferVO = loadP2PReconciliationVO(p_con, transactionID);
                    if (p2pTransferVO == null) {
                        _log.info("reconcileTransactionList", "For transactionID=" + transactionID + " No information available in transfers table or already settled");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "HandleUnsettledP2PCases[reconcileTransactionList]", transactionID, "", "", "No information available in transfers table or already settled");
                        continue;
                    }
                    if (PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(p2pTransferVO.getTxnStatus())) {
                        updateCount = transferDAO.markP2PReceiverAmbiguous(p_con, transactionID);
                        receiverItemVO = (TransferItemVO) p2pTransferVO.getTransferItemList().get(1);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "HandleUnsettledP2PCases[reconcileTransactionList]", transactionID, "", "", "P2P Receiver transfer status changed to '250' from " + receiverItemVO
                                .getTransferStatus());
                        receiverItemVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
                    }

                    p2pTransferVO.setTransferStatus(p_status);
                    p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    p2pTransferVO.setModifiedOn(currentDate);

                    final ArrayList newEntries = ReconciliationBL.prepareNewList(p2pTransferVO, p2pTransferVO.getTransferItemList(), p_statusTxt, null);
                    p2pTransferVO.setTransferItemList(newEntries);

                    updateCount = transferDAO.updateReconcilationStatus(p_con, p2pTransferVO);
                    if (updateCount > 0) {
                        p_con.commit();
                        _log.info("reconcileTransactionList", "TransactionID=" + transactionID + " Succesfully Settled to status=" + p_status);
                    } else {
                        p_con.rollback();
                        if (_log.isDebugEnabled()) {
                            _log.debug("reconcileTransactionList", "TransactionID=" + transactionID + " Not able Settled to status=" + p_status);
                        }
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "HandleUnsettledP2PCases[reconcileTransactionList]", transactionID, "", "", "Not able Settled to status=" + p_status);
                    }
                    // Sleep for some time and then continue
                    p_con.rollback();
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    p_con.rollback();
                    _log.info("reconcileTransactionList", "TransactionID=" + transactionID + " Not able Settled to status=" + p_status + " getting Exception=" + be
                        .getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                        "HandleUnsettledP2PCases[reconcileTransactionList]", transactionID, "", "", "Not able Settled to status=" + p_status + " getting Exception=" + be
                            .getMessage());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    p_con.rollback();
                    _log.info("reconcileTransactionList", "TransactionID=" + transactionID + " Not able Settled to status=" + p_status + " getting Exception=" + e
                        .getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                        "HandleUnsettledP2PCases[reconcileTransactionList]", transactionID, "", "", "Not able Settled to status=" + p_status + " getting Exception=" + e
                            .getMessage());
                } finally {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledP2PCases[reconcileTransactionList]",
                "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to load the TransferVO based on transfer ID
     * 
     * @param p_con
     * @param p_transferID
     * @return
     */

    public P2PTransferVO loadP2PReconciliationVO(Connection p_con, String p_transferID) {
    	 final String METHOD_NAME = "loadP2PReconciliationVO";
    	if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_transferID);
            _log.debug(METHOD_NAME,loggerValue);
        }
       
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        P2PTransferVO p2pTransferVO = null;
        try {
           
            final TransferDAO transferDAO = new TransferDAO();
            
            HandleUnsettledP2PCasesQry handleUnsettleP2PQry=(HandleUnsettledP2PCasesQry)ObjectProducer.getObject(QueryConstants.HANDLE_UNSETTLED_P2P_CASES_QRY, QueryConstants.QUERY_PRODUCER);
            

            final String selectQuery = handleUnsettleP2PQry.loadP2PReconciliationVO();
            if (_log.isDebugEnabled()) {
                _log.debug("loadP2PReconciliationVO", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.P2P_ERRCODE_VALUS);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            pstmtSelect.setString(i++, p_transferID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                p2pTransferVO = new P2PTransferVO();

                p2pTransferVO.setProductName(rs.getString("short_name"));
                p2pTransferVO.setServiceName(rs.getString("name"));
                p2pTransferVO.setSenderName(rs.getString("user_name"));
                p2pTransferVO.setErrorMessage(rs.getString("value"));
                p2pTransferVO.setTransferID(rs.getString("transfer_id"));
                p2pTransferVO.setTransferDate(rs.getDate("transfer_date"));
                p2pTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                p2pTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                p2pTransferVO.setNetworkCode(rs.getString("network_code"));
                p2pTransferVO.setSenderID(rs.getString("sender_id"));
                p2pTransferVO.setProductCode(rs.getString("product_code"));
                p2pTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                p2pTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                p2pTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                p2pTransferVO.setTransferValue(rs.getLong("transfer_value"));
                p2pTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                p2pTransferVO.setErrorCode(rs.getString("error_code"));
                p2pTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                p2pTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                p2pTransferVO.setReferenceID(rs.getString("reference_id"));
                p2pTransferVO.setPaymentMethodType(rs.getString("payment_method_type"));
                p2pTransferVO.setServiceType(rs.getString("service_type"));
                p2pTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                p2pTransferVO.setLanguage(rs.getString("language"));
                p2pTransferVO.setCountry(rs.getString("country"));
                p2pTransferVO.setSkey(rs.getLong("skey"));
                p2pTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                p2pTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                p2pTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                p2pTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                p2pTransferVO.setQuantity(rs.getLong("quantity"));
                p2pTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                p2pTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                p2pTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                p2pTransferVO.setCreatedOn(rs.getDate("created_on"));
                p2pTransferVO.setCreatedBy(rs.getString("created_by"));
                p2pTransferVO.setModifiedOn(rs.getDate("modified_on"));
                p2pTransferVO.setModifiedBy(rs.getString("modified_by"));
                p2pTransferVO.setTransferStatus(rs.getString("txn_status"));
                p2pTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                p2pTransferVO.setVersion(rs.getString("version"));
                p2pTransferVO.setCardGroupID(rs.getString("card_group_id"));
                p2pTransferVO.setSenderAccessFee(rs.getLong("sender_access_fee"));
                p2pTransferVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                p2pTransferVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                p2pTransferVO.setSenderTax1Value(rs.getLong("sender_tax1_value"));
                p2pTransferVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                p2pTransferVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                p2pTransferVO.setSenderTax2Value(rs.getLong("sender_tax2_value"));
                p2pTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                p2pTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                p2pTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                p2pTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                p2pTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                p2pTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                p2pTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                p2pTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                p2pTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                p2pTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                p2pTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                p2pTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                p2pTransferVO.setTransferCategory(rs.getString("transfer_category"));
                p2pTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                p2pTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                p2pTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                p2pTransferVO.setTxnStatus(rs.getString("transfer_status"));
                p2pTransferVO.setTransferItemList(transferDAO.loadP2PReconciliationItemsList(p_con, p_transferID));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadP2PReconciliationVO", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledP2PCases[loadP2PReconciliationVO]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
        }// end of catch
        catch (Exception e) {
            _log.error("loadP2PReconciliationVO", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledP2PCases[loadP2PReconciliationVO]", "",
                "", "", "Exception:" + e.getMessage());
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadP2PReconciliationVO", "Exiting ");
            }
        }// end of finally

        return p2pTransferVO;
    }
}
