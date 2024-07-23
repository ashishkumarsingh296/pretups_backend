package com.btsl.pretups.channel.user.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.logging.AmbigousSettlementErrorLog;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

/**
 * @(#)HandleUnsettledCases.java
 *                               Copyright(c) 2006, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 * 
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Gurjeet Bedi Sep 26, 2006 Initial Creation
 * 
 */

public class HandleUnsettledCases {
    private static ArrayList _failList = new ArrayList();
    private static ArrayList _successList = new ArrayList();
    private static Log _log = LogFactory.getLog(HandleUnsettledCases.class.getName());
    private static long sleepTime = 100;

    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        BufferedReader in = null;
        Connection con = null;
        if (args.length != 4) {
            System.out.println("Usage : HandleUnsettledCases [Constants file] [LogConfig file] [File Name] [Sleep Time] ");
            return;
        }
        final File constantsFile = new File(args[0]);
        if (!constantsFile.exists()) {
            System.out.println("HandleUnsettledCases main() Constants file not found on location:: " + constantsFile.toString());
            return;
        }
        final File logconfigFile = new File(args[1]);
        if (!logconfigFile.exists()) {
            System.out.println("HandleUnsettledCases main() Logconfig file not found on location:: " + logconfigFile.toString());
            return;
        }
        final File sourceFile = new File(args[2]);
        if (!sourceFile.exists()) {
            System.out.println("HandleUnsettledCases main() sourceFile file not found on location:: " + sourceFile.toString());
            return;
        }
        String sleepTimeStr = args[3];
        if (BTSLUtil.isNullString(sleepTimeStr)) {
            sleepTimeStr = "1000";
        }
        try {
            sleepTime = Long.parseLong(sleepTimeStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            sleepTime = 1000;
        }
        try {
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            NetworkProductServiceTypeCache.refreshNetworkProductMapping();
            NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            System.out.println("HandleUnsettledCases main() Not able to load Process Cache");
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final HandleUnsettledCases handleUnsettledCases = new HandleUnsettledCases();
            in = new BufferedReader(new FileReader(args[2]));
            handleUnsettledCases.readDataAndPutInObject(in, args[2], ",");
            in.close();
            con = OracleUtil.getSingleConnection();
            handleUnsettledCases.handleChannelAmbigousCases(con, _failList, _successList);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "[main]", "", "", "", "Exception:" + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                in = null;
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
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
            _log.debug("readDataAndPutInObject", "Entered with p_fileName=" + p_fileName + "p_separator=" + p_separator);
        }
        String str = null;
        String transID = null;
        String status = null;
        try {
        	int counter=1;
        	String otherInfo="";
            while (p_br.ready()) {
                str = p_br.readLine();
                if (BTSLUtil.isNullString(str)) {
                    continue;
                }
                if (str.indexOf(p_separator) == -1) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("readDataAndPutInObject", "Skipping entry (" + str + ") from " + p_fileName + " as separator (" + p_separator + ") not found");
                    }
                    otherInfo="Invalid Format for entry (" + str + ") from " + p_fileName + " as separator (" + p_separator + ") not found.";
                    AmbigousSettlementErrorLog.logFormat(str, p_separator, otherInfo, counter);
                    continue;
                }
                if (new StringTokenizer(str, p_separator).countTokens() < 2) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("readDataAndPutInObject", "Skipping entry (" + str + ") from " + p_fileName + " as Less than 2 tokens found");
                    }
                    otherInfo="Invalid Format for entry (" + str + ") from " + p_fileName + " as Less than 2 tokens found.";
                    AmbigousSettlementErrorLog.logFormat(str, p_separator, otherInfo, counter);
                    continue;
                }

                transID = (str.substring(0, str.indexOf(p_separator))).trim();
                status = (str.substring(str.lastIndexOf(p_separator) + 1, str.trim().length())).trim();
                if (!"F".equalsIgnoreCase(status) && !"S".equalsIgnoreCase(status)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("readDataAndPutInObject", "Skipping entry (" + str + ") from " + p_fileName + " as status can be only S or F");
                    }
                    AmbigousSettlementErrorLog.log(transID, status, counter, "Invalid Status for entry as status can be only S or F.");
                    continue;
                }
                if ("F".equalsIgnoreCase(status.trim())) {
                    _failList.add(transID.trim());
                } else {
                    _successList.add(transID.trim());
                }
               counter++;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[readDataAndPutInObject]", "", "",
                "", "Exception:" + e.getMessage());
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
        final String METHOD_NAME = "handleChannelAmbigousCases";
        if (_log.isDebugEnabled()) {
            _log.debug("handleChannelAmbigousCases", "Entered with p_failList.size=" + p_failList.size() + " p_successList.size()=" + p_successList.size());
        }
        try {
            reconcileTransactionList(p_con, PretupsErrorCodesI.TXN_STATUS_FAIL, "Fail", p_failList);
            reconcileTransactionList(p_con, PretupsErrorCodesI.TXN_STATUS_SUCCESS, "Success", p_successList);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[handleChannelAmbigousCases]", "",
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
            _log.debug("reconcileTransactionList", "Entered with p_status=" + p_status + " p_statusTxt=" + p_statusTxt + " p_list.size=" + p_list.size());
        }
        try {
            String transactionID = null;
            C2STransferVO c2sTransferVO = null;
            C2STransferItemVO receiverItemVO = null;
            int updateCount = 0;
            final java.util.Date currentDate = new Date();
            final C2STransferDAO c2STransferDAO = new C2STransferDAO();
            int listsSize=p_list.size();
            for (int i = 0; i < listsSize; i++) {
                transactionID = (String) p_list.get(i);
                if (_log.isDebugEnabled()) {
                    _log.debug("reconcileTransactionList", "Got transactionID=" + transactionID);
                }
                updateCount = 0;
                receiverItemVO = null;
                try {

                    if (i > 2000) {
                        // Issue.
                        Thread.sleep(100);
                    }
                    c2sTransferVO = loadC2STransferVO(p_con, transactionID);
                    if (c2sTransferVO == null) {
                        _log.info("reconcileTransactionList", "For transactionID=" + transactionID + " No information available in transfers table or already settled");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "HandleUnsettledCases[reconcileTransactionList]", transactionID, "", "", "No information available in transfers table or already settled");
                        continue;
                    }
                    if (c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
                        if (p_statusTxt.equalsIgnoreCase("Success")) {
                            p_statusTxt = "Fail";
                        } else if (p_statusTxt.equalsIgnoreCase("Fail")) {
                            p_statusTxt = "Success";
                        }
                    }
                    if (PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(c2sTransferVO.getTxnStatus())) {
                        updateCount = c2STransferDAO.markC2SReceiverAmbiguous(p_con, c2sTransferVO.getTransferID());
                        receiverItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(1);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "HandleUnsettledCases[reconcileTransactionList]", c2sTransferVO.getTransferID(), "", "",
                            "Receiver transfer status changed to '250' from " + receiverItemVO.getTransferStatus());
                        receiverItemVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
                    }
                    c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    c2sTransferVO.setTransferStatus(p_status);
                    c2sTransferVO.setModifiedOn(currentDate);
                    final ArrayList newEntries = ChannelUserBL.prepareNewC2SReconList(p_con, c2sTransferVO, p_statusTxt, null);
                    c2sTransferVO.setTransferItemList(newEntries);
                    // added by jasmine as discussed with rahul
                    if (c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVD) || c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                        final VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
                        final VomsVoucherVO vomsVoucherVO = vomsVoucherDAO.loadVomsVoucherVO(p_con, c2sTransferVO);
                        updateCount = vomsVoucherDAO.updateVoucherStatus(p_con, p_statusTxt, c2sTransferVO, vomsVoucherVO);
                    }
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

                        PushMessage pushMessage1=null;
                        if(!BTSLUtil.isNullString(c2sTransferVO.getSenderReturnMessage())){
					       	pushMessage1=new PushMessage(c2sTransferVO.getSenderMsisdn(),c2sTransferVO.getSenderReturnMessage(),null,null,(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale());
							pushMessage1.push();
					    }
                        if(!BTSLUtil.isNullString(c2sTransferVO.getSenderRoamReconDebitMessage())){
                        	pushMessage1=new PushMessage(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getMsisdn(),c2sTransferVO.getSenderRoamReconDebitMessage(),null,null,((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale()); 
                        	pushMessage1.push();
    					}
    					if(!BTSLUtil.isNullString(c2sTransferVO.getSenderRoamReconCreditMessage())){
    						pushMessage1=new PushMessage(((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getMsisdn(),c2sTransferVO.getSenderRoamReconCreditMessage(),null,null,((ChannelUserVO)c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale()); 
    						pushMessage1.push();
    					}
                        if(!BTSLUtil.isNullString(c2sTransferVO.getSenderOwnerRoamReconDebitMessage())){
    						Locale ownerLocale = new Locale(c2sTransferVO.getOwnerUserVO().getLanguage(), c2sTransferVO.getOwnerUserVO().getCountryCode());
    						pushMessage1=new PushMessage(c2sTransferVO.getOwnerUserVO().getMsisdn(),c2sTransferVO.getSenderOwnerRoamReconDebitMessage(),null,null,ownerLocale); 
    						pushMessage1.push();
    					}
                        if(!BTSLUtil.isNullString(c2sTransferVO.getSenderOwnerRoamReconCreditMessage())){	                   
    						Locale ownerLocale = new Locale(c2sTransferVO.getOwnerUserVO().getLanguage(), c2sTransferVO.getOwnerUserVO().getCountryCode());
	                    	pushMessage1=new PushMessage(c2sTransferVO.getOwnerUserVO().getMsisdn(),c2sTransferVO.getSenderOwnerRoamReconCreditMessage(),null,null,ownerLocale);
							pushMessage1.push();
	                    }
                        
                        if (_log.isDebugEnabled()) {
                            _log.debug("reconcileTransactionList", "TransactionID=" + transactionID + " Succesfully Settled to status=" + p_status);
                        }
                    } else {
                        p_con.rollback();
                        _log.error("reconcileTransactionList", "TransactionID=" + transactionID + " Not able Settled to status=" + p_status);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "HandleUnsettledCases[reconcileTransactionList]", transactionID, "", "", "Not able Settled to status=" + p_status);
                    }
                    p_con.rollback();
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    p_con.rollback();
                    _log.error("reconcileTransactionList", "TransactionID=" + transactionID + " Not able Settled to status=" + p_status + " getting Exception=" + be
                        .getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HandleUnsettledCases[reconcileTransactionList]",
                        transactionID, "", "", "Not able Settled to status=" + p_status + " getting Exception=" + be.getMessage());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    p_con.rollback();
                    _log.error("reconcileTransactionList", "TransactionID=" + transactionID + " Not able Settled to status=" + p_status + " getting Exception=" + e
                        .getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HandleUnsettledCases[reconcileTransactionList]",
                        transactionID, "", "", "Not able Settled to status=" + p_status + " getting Exception=" + e.getMessage());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[reconcileTransactionList]", "",
                "", "", "Exception:" + e.getMessage());
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
        final String METHOD_NAME = "loadC2STransferVO";
        if (_log.isDebugEnabled()) {
            _log.debug("loadC2STransferVO", "Entered p_transferID=" + p_transferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;
        try {
            final C2STransferDAO c2STransferDAO = new C2STransferDAO();
            
            HandleUnsettledCasesQry handleUnsettledQry=(HandleUnsettledCasesQry)ObjectProducer.getObject(QueryConstants.HANDLE_UNSETTLED_CASES_QRY, QueryConstants.QUERY_PRODUCER);
            
            final String selectQuery = handleUnsettledQry.loadC2STransferVO();
            if (_log.isDebugEnabled()) {
                _log.debug("loadC2STransferVO", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_transferID);
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
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
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setSenderNetworkCode(rs.getString("network_code"));
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
                c2sTransferVO.setReverseTransferID(rs.getString("reversal_id"));
                channelUserVO = new ChannelUserVO();
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("commission_profile_id"));
                channelUserVO.setCategoryCode(rs.getString("sender_category"));
                channelUserVO.setUserID(rs.getString("sender_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                c2sTransferVO.setSenderVO(channelUserVO);
                c2sTransferVO.setTransferItemList(c2STransferDAO.loadC2STransferItemsVOList(p_con, p_transferID));
                c2sTransferVO.setRoamPenalty(rs.getLong("penalty"));
                c2sTransferVO.setRoamPenaltyOwner(rs.getLong("owner_penalty"));
                c2sTransferVO.setPenaltyDetails(rs.getString("penalty_details"));
                RequestVO requestVO = new RequestVO();
                requestVO.setSenderVO(c2sTransferVO.getSenderVO());
                c2sTransferVO.setRequestVO(requestVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadC2STransferVO", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[loadC2STransferVO]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
        }// end of catch
        catch (Exception e) {
            _log.error("loadC2STransferVO", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandleUnsettledCases[loadC2STransferVO]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug("loadC2STransferVO", "Exiting ");
            }
        }// end of finally

        return c2sTransferVO;
    }
}
