/**
 * @(#)EvdUtil.java
 *                  Copyright(c) 2006, Bharti Telesoft Intl. Ltd.
 *                  All Rights Reserved
 *                  DESCRIPTION------
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Amit Ruwali 13/09/06 Initial Creation
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 */

package com.btsl.pretups.channel.transfer.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonClient;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadController;
import com.btsl.loadcontroller.LoadControllerI;
import com.btsl.loadcontroller.ReqNetworkServiceLoadController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.adjustments.businesslogic.DiffCalBL;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class EvdUtil {
    private static final Log LOGGER = LogFactory.getLog("EvdUtil".getClass().getName());

    /**
     * This method checks whether the My SQL Database is up and running
     * 
     * @param p_smsUserVO
     * @return int
     * @throws Exception
     * @author: gurjeet.bedi
     */
    public static int checkMySqlConnUp(C2STransferVO p_c2sTransferVO)  {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checkMySqlConnUp()", " Entered for Transaction No= " + p_c2sTransferVO.getTransferID());
        }
        final String METHOD_NAME = "checkMySqlConnUp";
        int updateCount = 0;
        Connection mySqlConn = null;
        final String strQuery = " SELECT status FROM database_status where ID=1 ";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Getting the database connection again
            LOGGER.debug("[checkMySqlConnUp]", " Request[" + p_c2sTransferVO.getTransferID() + "] Query for update =" + strQuery);
            mySqlConn = MySqlConnectionUtil.getConnection();
            if (mySqlConn == null) {
                LOGGER.error("[checkMySqlConnUp]",
                    " Request[" + p_c2sTransferVO.getTransferID() + "]Transaction No[" + p_c2sTransferVO.getTransferID() + "] MY SQL Database unavailable message");
                // EventHandler.eventEntry("EvdController[checkMySqlConnUp]Request["+p_c2sTransferVO.getTransferID()+"]",EventsI.DESC_CONNECTION_NULL,EventsI.STATUS_CRITICAL_FATAL,"Not able to get Connection to MY SQL Database for "+p_c2sTransferVO.getTransferID(),p_c2sTransferVO.getTransferID(),p_c2sTransferVO.getNetworkCode());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdUtil[checkMySqlConnUp]", "", "", "",
                    "Exception:Not able to get Connection to MY SQL Database");
                throw new BTSLBaseException(
                    "EvdController[checkMySqlConnUp]: Request[" + p_c2sTransferVO.getTransferID() + "] MY SQL Database unavailable message for Transaction No=" + p_c2sTransferVO
                        .getTransferID());
            }// end if mySqlConn==null
            pstmt = mySqlConn.prepareStatement(strQuery);
            rs = pstmt.executeQuery();
            updateCount = 1;
        } catch (Exception e) {
            LOGGER.error("checkMySqlConnUp()", " Exception for Transaction No= " + p_c2sTransferVO.getTransferID() + "Exception=" + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            try {
                if (mySqlConn != null) {
                    mySqlConn.rollback();
                }
            } catch (Exception ex) {
                LOGGER.errorTrace("METHOD_NAME", ex);
            }
            
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOGGER.errorTrace("METHOD_NAME", e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOGGER.errorTrace("METHOD_NAME", e);
            }
            try {
                if (mySqlConn != null) {
                    MySqlConnectionUtil.freeConnection(mySqlConn);
                }
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdUtil[checkMySqlConnUp]", "", "", "",
                    "Exception:" + e.getMessage());
                LOGGER.error("checkMySqlConnUp()", " Not able to free MYSQL connection for Transaction No= " + p_c2sTransferVO.getTransferID());
                LOGGER.errorTrace("METHOD_NAME", e);
            }
            mySqlConn = null;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("checkMySqlConnUp()", " Exiting for Transaction No= " + p_c2sTransferVO.getTransferID() + " with updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method:updateVoucherAndGiveDifferentials
     * This method will make the status of voucher as consume using VOMSHandler
     * aND on success givew the defferentials
     * If voucher is not poroperly updated to consume the sender and voucher
     * will be credit back
     * 
     * @param p_con
     * @param p_receiverVO
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @param p_instanceID
     * @param p_isVoucherStatusTrack
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean updateVoucherAndGiveDifferentials(ReceiverVO p_receiverVO, C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO, String p_instanceID, boolean p_isVoucherStatusTrack) throws BTSLBaseException {
       final String methodName="updateVoucherAndGiveDifferentials";
    	if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateVoucherAndGiveDifferentials", " Eentered for p_transferVO= " + p_transferVO);
        }
        boolean finalTransferStatusUpdate = true;
        try {
            final String receiverMsisdn = p_transferVO.getReceiverMsisdn();
            LoadController.incrementTransactionInterCounts(p_transferVO.getTransferID(), LoadControllerI.SENDER_UNDER_TOP);
            final CommonClient commonClient = new CommonClient();
            // Update the voucher status to consume
            final String vomsConsumeResponse = commonClient.process(getVOMSUpdateRequestStr(PretupsI.INTERFACE_CREDIT_ACTION, p_transferVO, p_networkInterfaceModuleVO,
                p_interfaceVO, VOMSI.VOUCHER_USED, VOMSI.VOUCHER_UNPROCESS), p_transferVO.getTransferID(), p_networkInterfaceModuleVO.getCommunicationType(),
                p_networkInterfaceModuleVO.getIP(), p_networkInterfaceModuleVO.getPort(), p_networkInterfaceModuleVO.getClassName());
            try {
                updateForVOMSUpdationResponse(vomsConsumeResponse, p_receiverVO, p_transferVO, p_isVoucherStatusTrack);
                VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_USED, p_transferVO
                    .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil
                    .getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
                LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
            } catch (BTSLBaseException be) {

                TransactionLog
                    .log(p_transferVO.getTransferID(), null, receiverMsisdn, p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                        "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p_transferVO.getTransferStatus() + " Getting Code=" + p_receiverVO
                            .getInterfaceResponseCode() + " voucher serial number=" + p_transferVO.getSerialNumber());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (p_transferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);

                throw be;
            }// end catch BTSLBaseException
            catch (Exception e) {

                TransactionLog
                    .log(p_transferVO.getTransferID(), null, receiverMsisdn, p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                        "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p_transferVO.getTransferStatus() + " Getting Code=" + p_receiverVO
                            .getInterfaceResponseCode() + " voucher serial number=" + p_transferVO.getSerialNumber());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (p_transferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);

                
            }// end of catch Exception
            p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            p_transferVO.setErrorCode(null);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(p_instanceID, p_transferVO.getRequestGatewayType(), p_transferVO.getNetworkCode(), p_transferVO
                .getServiceType(), p_transferVO.getTransferID(), LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, p_transferVO.getReceiverNetworkCode());
            TransactionLog.log(p_transferVO.getTransferID(), null, receiverMsisdn, p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Success", PretupsI.TXN_LOG_STATUS_SUCCESS,
                "Transfer Status=" + p_transferVO.getTransferStatus() + " voucher serial number=" + p_transferVO.getSerialNumber());
            // validate receiver limits after Interface Updation
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            p_transferVO.setSenderReturnMessage(null);
            // checking whether differential commission is applicable or not
            if (PretupsI.YES.equals(p_transferVO.getDifferentialAllowedForService())) {
                // Caluculate Differential if transaction successful
                try {
                    new DiffCalBL().differentialCalculations(p_transferVO, PretupsI.C2S_MODULE);
                } catch (BTSLBaseException be) {
                    finalTransferStatusUpdate = false;
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                    LOGGER.errorTrace("METHOD_NAME", be);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDUtil[updateVoucherAndGiveDifferentials]",
                        p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Exception:" + be.getMessage());
                } catch (Exception e) {
                    finalTransferStatusUpdate = false;
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                    LOGGER.errorTrace("METHOD_NAME", e);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOGGER.error(methodName, e);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateVoucherAndGiveDifferentials", " Exited for p_transferVO= " + p_transferVO);
            }
        }
        return finalTransferStatusUpdate;
    }

    /**
     * Method:updateForVOMSUpdationResponse
     * This method will check the response from the VOMSHandler and make the
     * entry in itemsVO and transferVO for the
     * status returned from the VOMSHandler
     * 
     * @param str
     * @param p_receiverVO
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public void updateForVOMSUpdationResponse(String str, ReceiverVO p_receiverVO, C2STransferVO p_transferVO, boolean p_isVoucherStatusTrack) throws BTSLBaseException {
        final String METHOD_NAME = "updateForVOMSUpdationResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        final C2STransferItemVO itemVOS = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        final ChannelUserVO channelUserVO = (ChannelUserVO) p_transferVO.getSenderVO();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateForVOMSUpdationResponse", "Mape from response=" + map + " status=" + status);
        }
        itemVOS.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        itemVOS.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        String updateStatus = (String) map.get("UPDATE_STATUS");
        if (BTSLUtil.isNullString(updateStatus)) {
            updateStatus = status;
        }
        itemVOS.setUpdateStatus(updateStatus);
        p_receiverVO.setInterfaceResponseCode(itemVOS.getInterfaceResponseCode());
        // set from IN Module
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                itemVOS.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
        }
        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        if (p_isVoucherStatusTrack) {

            // setting transaction status for restricted subscriber feature
            if (PretupsI.STATUS_ACTIVE.equals((channelUserVO.getCategoryVO()).getRestrictedMsisdns()) && p_transferVO.getReceiverVO() != null && ((ReceiverVO) p_transferVO
                .getReceiverVO()).getRestrictedSubscriberVO() != null) {
                ((RestrictedSubscriberVO) ((ReceiverVO) p_transferVO.getReceiverVO()).getRestrictedSubscriberVO()).setTempStatus(status);
            }

            String[] strArr = null;
            if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
                p_transferVO.setErrorCode(status + "_S");
                p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                p_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                itemVOS.setTransferStatus(status);
                strArr = new String[] { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
                throw new BTSLBaseException(this, "updateForVOMSUpdationResponse", p_transferVO.getErrorCode(), 0, strArr, null);
            } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                p_transferVO.setErrorCode(status + "_S");
                p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                itemVOS.setTransferStatus(status);
                p_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                itemVOS.setUpdateStatus(status);
                strArr = new String[] { p_transferVO.getTransferID(), p_transferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
                throw new BTSLBaseException(this, "updateForVOMSUpdationResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
            } else {
                itemVOS.setTransferStatus(status);
                p_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                itemVOS.setUpdateStatus(status);
            }
        } else {
            final String serialNumber = (String) map.get("SERIAL_NUMBER");
            if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS))) {
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "EvdUtil[updateForVOMSUpdationResponse]", null, null, null,
                    "Voucher can not be updated successfully. Update status=" + status + " Serial number=" + serialNumber);
            }
            itemVOS.setTransferStatus(status);
            p_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            itemVOS.setUpdateStatus(status);
        }
    }

    /**
     * Method :updateSenderForFailedTransaction
     * This method will make credit back the sender
     * 
     * @param p_con
     * @param p_transferVO
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public UserBalancesVO updateSenderForFailedTransaction(Connection p_con, C2STransferVO p_transferVO) throws BTSLBaseException, Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateSenderForFailedTransaction", " Entered for p_transferVO= " + p_transferVO);
        }
        final String METHOD_NAME = "updateSenderForFailedTransaction";
        UserBalancesVO userBalancesVO = null;
        try {
            userBalancesVO = ChannelUserBL.creditUserBalanceForProduct(p_con, p_transferVO.getTransferID(), p_transferVO);
            ChannelTransferBL.decreaseC2STransferOutCounts(p_con, p_transferVO);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getSenderNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to sender", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            LOGGER.errorTrace(METHOD_NAME, be);
            userBalancesVO = null;
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdUtil[updateSenderForFailedTransaction]", "", "",
                "", "Error while credit back the retailer. So leaving the voucher marked as under process. Exception: " + be.getMessage());
            throw new BTSLBaseException(this, "updateSenderForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateSenderForFailedTransaction", " Exited ");
            }
        }
        return userBalancesVO;
    }

    /**
     * Method :updateVoucherForFailedTransaction
     * This method will mark the voucher status to enable
     * 
     * @param p_con
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean updateVoucherForFailedTransaction(C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO) throws BTSLBaseException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateVoucherForFailedTransaction", " Entered for p_transferVO= " + p_transferVO);
        }
        final String METHOD_NAME = "updateVoucherForFailedTransaction";
        boolean finalTransferStatusUpdate = true;
        C2STransferItemVO senderTransferItemVO = senderTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        try {
            final CommonClient commonClient = new CommonClient();
            final String vomsCreditBackResponse = commonClient.process(getVOMSUpdateRequestStr(PretupsI.INTERFACE_CREDIT_ACTION, p_transferVO, p_networkInterfaceModuleVO,
                p_interfaceVO, VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS), p_transferVO.getTransferID(), p_networkInterfaceModuleVO.getCommunicationType(),
                p_networkInterfaceModuleVO.getIP(), p_networkInterfaceModuleVO.getPort(), p_networkInterfaceModuleVO.getClassName());
            // getting the update status from the Response and set in
            // appropriate VO: senderTransferItemVO update Status 1 can be used
            try {
                final HashMap map = BTSLUtil.getStringToHash(vomsCreditBackResponse, "&", "=");
                senderTransferItemVO.setUpdateStatus1((String) map.get("TRANSACTION_STATUS"));
                if (!InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getUpdateStatus1())) {
                    throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
                VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE, p_transferVO
                    .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil
                    .getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
            } catch (Exception e) {
                LOGGER.error("updateVoucherForFailedTransaction", " Exception while updating voucher status= " + e.getMessage());
                LOGGER.errorTrace(METHOD_NAME, e);
                
            }
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getSenderNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to voucher for serial number=" + p_transferVO.getSerialNumber(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            LOGGER.errorTrace(METHOD_NAME, be);
            finalTransferStatusUpdate = false;
            senderTransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back voucher", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateVoucherForFailedTransaction", " Exited for finalTransferStatusUpdate= " + finalTransferStatusUpdate);
            }
        }
        return finalTransferStatusUpdate;
    }

    /**
     * Method: getVOMSCommonString
     * This method will construct the common string that is to be send in
     * request for VOMSHandler
     * 
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @return
     */
    private String getVOMSCommonString(C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO) {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + p_transferVO.getReceiverMsisdn());
        strBuff.append("&TRANSACTION_ID=" + p_transferVO.getTransferID());
        strBuff.append("&NETWORK_CODE=" + p_transferVO.getReceiverNetworkCode());
        strBuff.append("&INTERFACE_ID=" + p_interfaceVO.getInterfaceId());
        strBuff.append("&INTERFACE_HANDLER=" + p_interfaceVO.getHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + p_networkInterfaceModuleVO.getCommunicationType());
        strBuff.append("&INT_MOD_IP=" + p_networkInterfaceModuleVO.getIP());
        strBuff.append("&INT_MOD_PORT=" + p_networkInterfaceModuleVO.getPort());
        strBuff.append("&INT_MOD_CLASSNAME=" + p_networkInterfaceModuleVO.getClassName());
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        strBuff.append("&CARD_GROUP_SELECTOR=" + p_transferVO.getSubService());
        strBuff.append("&REQ_SERVICE=" + p_transferVO.getServiceType());
        return strBuff.toString();
    }

    /**
     * Method:getVOMSUpdateRequestStr
     * This method will construct the detils to be sent in request to
     * VOMSHandler
     * 
     * @param p_interfaceAction
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @param p_updateStatus
     * @param p_previousStatus
     * @return
     */
    public String getVOMSUpdateRequestStr(String p_interfaceAction, C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO, String p_updateStatus, String p_previousStatus) {
        final String METHOD_NAME = "getVOMSUpdateRequestStr";
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getVOMSCommonString(p_transferVO, p_networkInterfaceModuleVO, p_interfaceVO));
        strBuff.append("&INTERFACE_ACTION=" + p_interfaceAction);
        try {
            strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
        } catch (Exception e) {
            LOGGER.errorTrace("METHOD_NAME", e);
        }
        strBuff.append("&INTERFACE_AMOUNT=" + p_transferVO.getRequestedAmount());
        strBuff.append("&UPDATE_STATUS=" + p_updateStatus);
        strBuff.append("&PREVIOUS_STATUS=" + p_previousStatus);
        strBuff.append("&SOURCE=" + p_transferVO.getSourceType());
        strBuff.append("&SENDER_MSISDN=" + p_transferVO.getSenderMsisdn());
        strBuff.append("&SERIAL_NUMBER=" + p_transferVO.getSerialNumber());
        strBuff.append("&SENDER_USER_ID=" + p_transferVO.getSenderID());
        return strBuff.toString();
    }

    /**
     * Method :calulateTransferValue
     * This method will set the voluse from the VOMS VO into intemsVO and
     * transferVO
     * 
     * @param p_transferVO
     * @param p_vomsVO
     */
    public void calulateTransferValue(C2STransferVO p_transferVO, VomsVoucherVO p_vomsVO) {
        p_transferVO.setSenderTransferValue(p_transferVO.getTransferValue());
        final C2STransferItemVO transferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(1);
        final C2STransferItemVO senderTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        p_transferVO.setReceiverGracePeriod(p_vomsVO.getGracePeriod());
        p_transferVO.setReceiverValidity(p_vomsVO.getValidity());
        p_transferVO.setReceiverTransferValue(p_vomsVO.getTalkTime());
        transferItemVO.setTransferValue(p_vomsVO.getTalkTime());
        transferItemVO.setGraceDaysStr(String.valueOf(p_vomsVO.getGracePeriod()));
        transferItemVO.setValidity(p_vomsVO.getValidity());
        senderTransferItemVO.setTransferValue(p_transferVO.getTransferValue());

    }

    /**
     * Method :calculateTransferValue
     * This method will calculate and set the transfer values into intemsVO and
     * transferVO
     * 
     * @param p_transferVO
     * @param p_vomsVO
     * @param p_quantityRequired
     */
    public void calculateTransferValue(C2STransferVO p_transferVO, VomsVoucherVO p_vomsVO, int p_quantityRequired) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("calculateTransferValue", " Entered for p_vomsVO=" + p_vomsVO + " p_quantityRequired=" + p_quantityRequired);
        }
        p_transferVO.setSenderTransferValue(p_transferVO.getTransferValue());
        final C2STransferItemVO receiverTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(1);
        final C2STransferItemVO senderTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        p_transferVO.setReceiverGracePeriod(p_vomsVO.getGracePeriod());
        p_transferVO.setReceiverValidity(p_vomsVO.getValidity());
        p_transferVO.setReceiverTransferValue(p_vomsVO.getTalkTime());
        receiverTransferItemVO.setTransferValue(p_vomsVO.getTalkTime());
        receiverTransferItemVO.setGraceDaysStr(String.valueOf(p_vomsVO.getGracePeriod()));
        receiverTransferItemVO.setValidity(p_vomsVO.getValidity());
        senderTransferItemVO.setTransferValue(p_transferVO.getTransferValue());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("calculateTransferValue", " Exited");
        }
    }

    /**
     * Method:updateVoucherAndGiveDifferentials
     * This method will make the status of voucher as consume and on success
     * give the defferentials
     * If voucher is not poroperly updated to consume the sender and voucher
     * will be credit back
     * 
     * @param p_receiverVO
     * @param p_transferVO
     * @param p_instanceID
     * @param p_quantityRequired
     * @param p_voucherList
     * @return finalTransferStatusUpdate
     * @throws BTSLBaseException
     * @throws ParseException 
     * @throws Exception
     */
    public boolean updateVoucherAndGiveDifferentials(ReceiverVO p_receiverVO, C2STransferVO p_transferVO, String p_instanceID, int p_quantityRequired, ArrayList p_voucherList) throws BTSLBaseException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateVoucherAndGiveDifferentials",
                " Eentered for p_quantityRequired=" + p_quantityRequired + " p_instanceID=" + p_instanceID + "p_voucherList size" + p_voucherList.size());
        }
        boolean finalTransferStatusUpdate = true;
        Connection con = null;MComConnectionI mcomCon = null;
        String receiverMsisdn = null;
        VOMSVoucherDAO vomsDAO = null;
        String tempSaleBatchNumber = null;
        try {
            receiverMsisdn = p_transferVO.getReceiverMsisdn();
            tempSaleBatchNumber = ChannelTransferBL.generateSaleBatchNumberForMVD(p_transferVO);
            for (int i = 0; i < p_quantityRequired; i++) {
                ((VomsVoucherVO) p_voucherList.get(i)).setCurrentStatus(VOMSI.VOUCHER_USED);
                ((VomsVoucherVO) p_voucherList.get(i)).setPreviousStatus(VOMSI.VOUCHER_UNPROCESS);
                ((VomsVoucherVO) p_voucherList.get(i)).SetSaleBatchNo(tempSaleBatchNumber);
		((VomsVoucherVO) p_voucherList.get(i)).setModifiedBy(p_transferVO.getSenderID());
		((VomsVoucherVO) p_voucherList.get(i)).setModifiedOn(new Date());
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            vomsDAO = new VOMSVoucherDAO();
            if (VOMSVoucherDAO.updateVoucherStatus(con, p_voucherList) == p_quantityRequired) {
                if (vomsDAO.insertDetailsInVoucherAudit(con, p_voucherList) == p_quantityRequired) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                    // If vouchers status cannot be updated successfully, no
                    // exception will be thrown
                    EventHandler
                        .handle(
                            EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM,
                            EventStatusI.RAISED,
                            EventLevelI.MAJOR,
                            "EvdUtil[updateForVOMSUpdationResponse]",
                            null,
                            null,
                            null,
                            "Voucher can not be updated successfully from Serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + " to " + ((VomsVoucherVO) p_voucherList
                                .get(p_quantityRequired - 1)).getSerialNo());

                }
            } else {
                // If vouchers status cannot be updated successfully, no
                // exception will be thrown
                EventHandler
                    .handle(
                        EventIDI.SYSTEM_ERROR,
                        EventComponentI.SYSTEM,
                        EventStatusI.RAISED,
                        EventLevelI.MAJOR,
                        "EvdUtil[updateForVOMSUpdationResponse]",
                        null,
                        null,
                        null,
                        "Voucher can not be updated successfully from Serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + " to " + ((VomsVoucherVO) p_voucherList
                            .get(p_quantityRequired - 1)).getSerialNo());

            }

            VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE, p_transferVO
                .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));

            try {
                updateForVOMSUpdationResponse(p_transferVO, p_receiverVO);
            }
            catch (Exception e) {

                TransactionLog.log(p_transferVO.getTransferID() + "-" + p_transferVO.getLastTransferId(), null, receiverMsisdn, p_receiverVO.getNetworkCode(),
                    PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p_transferVO
                        .getTransferStatus() + " Getting Code=" + p_receiverVO.getInterfaceResponseCode() + " voucher serial number=" + ((VomsVoucherVO) p_voucherList.get(0))
                        .getSerialNo() + "-" + ((VomsVoucherVO) p_voucherList.get(p_quantityRequired - 1)).getSerialNo());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);

                throw new BTSLBaseException("EvdUtil", "updateVoucherAndGiveDifferentials", "");
            }// end of catch Exception
            p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            p_transferVO.setErrorCode(null);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(p_instanceID, p_transferVO.getRequestGatewayType(), p_transferVO.getNetworkCode(), p_transferVO
                .getServiceType(), p_transferVO.getTransferID(), LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, p_transferVO.getReceiverNetworkCode());
            TransactionLog.log(p_transferVO.getTransferID() + "-" + p_transferVO.getLastTransferId(), null, receiverMsisdn, p_receiverVO.getNetworkCode(),
                PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Success", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + p_transferVO
                    .getTransferStatus() + " voucher serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + "-" + ((VomsVoucherVO) p_voucherList
                    .get(p_quantityRequired - 1)).getSerialNo());
            // validate receiver limits after Interface Updation
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            p_transferVO.setSenderReturnMessage(null);

            // checking whether differential commission is applicable or not
            if (PretupsI.YES.equals(p_transferVO.getDifferentialAllowedForService())) {
                // Caluculate Differential if transaction successful
                try {
                    new DiffCalBL().differentialCalculations(p_transferVO, PretupsI.C2S_MODULE, p_quantityRequired, p_voucherList);
                } catch (BTSLBaseException be) {
                    finalTransferStatusUpdate = false;
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                    LOGGER.errorTrace("METHOD_NAME", be);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDUtil[updateVoucherAndGiveDifferentials]",
                        p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "BTSLBaseException:" + be.getMessage());
                } catch (Exception e) {
                    finalTransferStatusUpdate = false;
                    LOGGER.errorTrace("METHOD_NAME", e);
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                }
            }
        } catch (BTSLBaseException be) {
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception e) {
                    LOGGER.errorTrace("METHOD_NAME", e);
                }
            }
            throw be;
        } catch (Exception e) {
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception be) {
                    LOGGER.errorTrace("METHOD_NAME", be);
                }
            }
            throw new BTSLBaseException(this, "updateVoucherAndGiveDifferentials", "");
        } finally {
			if (mcomCon != null) {
				mcomCon.close("EvdUtil#updateVoucherAndGiveDifferentials");
				mcomCon = null;
			}
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateVoucherAndGiveDifferentials", " Exited for p_transferVO= " + p_transferVO);
            }
        }
        return finalTransferStatusUpdate;
    }

    /**
     * Method:updateForVOMSUpdationResponse
     * This method will make the entry in itemsVO and transferVO after vouchers
     * are updated.
     * 
     * @param p_receiverVO
     * @param p_transferVO
     */
    public void updateForVOMSUpdationResponse(C2STransferVO p_transferVO, ReceiverVO p_receiverVO) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateForVOMSUpdationResponse", " Entered for p_receiverVO= " + p_receiverVO);
        }
        final String status = PretupsI.TXN_STATUS_SUCCESS;
        final C2STransferItemVO itemVOS = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateForVOMSUpdationResponse", " status=" + status);
        }
        itemVOS.setProtocolStatus(PretupsI.TXN_STATUS_SUCCESS);
        itemVOS.setInterfaceResponseCode(PretupsI.TXN_STATUS_SUCCESS);
        itemVOS.setUpdateStatus(status);
        p_receiverVO.setInterfaceResponseCode(itemVOS.getInterfaceResponseCode());
        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS))) {
            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR,
                "EvdUtil[updateForVOMSUpdationResponse]", null, null, null, "Voucher can not be updated successfully. Update status=" + status);
        }
        itemVOS.setTransferStatus(status);
        p_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        itemVOS.setUpdateStatus(status);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateForVOMSUpdationResponse", " Exited ");
        }
    }

    /**
     * Method:updateVoucherAndGiveDifferentials
     * This method will make the status of voucher as consume using VOMSHandler
     * aND on success givew the defferentials
     * If voucher is not poroperly updated to consume the sender and voucher
     * will be credit back
     * 
     * @param p_con
     * @param p_receiverVO
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @param p_instanceID
     * @param p_isVoucherStatusTrack
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean updateVoucherAndGiveDifferentials(Connection p_con, ReceiverVO p_receiverVO, C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO, String p_instanceID, boolean p_isVoucherStatusTrack) throws BTSLBaseException, ParseException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateVoucherAndGiveDifferentials", " Eentered for p_transferVO= " + p_transferVO);
        }
        boolean finalTransferStatusUpdate = true;
        try {
            final String receiverMsisdn = p_transferVO.getReceiverMsisdn();
            LoadController.incrementTransactionInterCounts(p_transferVO.getTransferID(), LoadControllerI.SENDER_UNDER_TOP);
            final CommonClient commonClient = new CommonClient();
            // Update the voucher status to consume
            final String vomsConsumeResponse = commonClient.process(getVOMSUpdateRequestStr(PretupsI.INTERFACE_CREDIT_ACTION, p_transferVO, p_networkInterfaceModuleVO,
                p_interfaceVO, VOMSI.VOUCHER_USED, VOMSI.VOUCHER_UNPROCESS), p_transferVO.getTransferID(), p_networkInterfaceModuleVO.getCommunicationType(),
                p_networkInterfaceModuleVO.getIP(), p_networkInterfaceModuleVO.getPort(), p_networkInterfaceModuleVO.getClassName());
            try {
                updateForVOMSUpdationResponse(vomsConsumeResponse, p_receiverVO, p_transferVO, p_isVoucherStatusTrack);
                VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_USED, p_transferVO
                    .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil
                    .getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
                LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
            } catch (BTSLBaseException be) {

                TransactionLog
                    .log(p_transferVO.getTransferID(), null, receiverMsisdn, p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                        "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p_transferVO.getTransferStatus() + " Getting Code=" + p_receiverVO
                            .getInterfaceResponseCode() + " voucher serial number=" + p_transferVO.getSerialNumber());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (p_transferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);

                throw be;
            }// end catch BTSLBaseException
            catch (Exception e) {

                TransactionLog
                    .log(p_transferVO.getTransferID(), null, receiverMsisdn, p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                        "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p_transferVO.getTransferStatus() + " Getting Code=" + p_receiverVO
                            .getInterfaceResponseCode() + " voucher serial number=" + p_transferVO.getSerialNumber());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (p_transferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(p_transferVO.getTransferID(), PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);

                throw new BTSLBaseException(this, "updateVoucherAndGiveDifferentials", "");
            }// end of catch Exception
            p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            p_transferVO.setErrorCode(null);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(p_instanceID, p_transferVO.getRequestGatewayType(), p_transferVO.getNetworkCode(), p_transferVO
                .getServiceType(), p_transferVO.getTransferID(), LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, p_transferVO.getReceiverNetworkCode());
            TransactionLog.log(p_transferVO.getTransferID(), null, receiverMsisdn, p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Success", PretupsI.TXN_LOG_STATUS_SUCCESS,
                "Transfer Status=" + p_transferVO.getTransferStatus() + " voucher serial number=" + p_transferVO.getSerialNumber());
            // validate receiver limits after Interface Updation
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            p_transferVO.setSenderReturnMessage(null);
            // checking whether differential commission is applicable or not
            if (PretupsI.YES.equals(p_transferVO.getDifferentialAllowedForService())) {
                // Caluculate Differential if transaction successful
                try {
                    new DiffCalBL().differentialCalculations(p_con, p_transferVO, PretupsI.C2S_MODULE);
                } catch (BTSLBaseException be) {
                    finalTransferStatusUpdate = false;
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                    LOGGER.errorTrace("METHOD_NAME", be);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDUtil[updateVoucherAndGiveDifferentials]",
                        p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Exception:" + be.getMessage());
                } catch (Exception e) {
                    finalTransferStatusUpdate = false;
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                    LOGGER.errorTrace("METHOD_NAME", e);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	throw new BTSLBaseException(this, "updateVoucherAndGiveDifferentials", "");
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateVoucherAndGiveDifferentials", " Exited for p_transferVO= " + p_transferVO);
            }
        }
        return finalTransferStatusUpdate;
    }

    /**
     * Method:updateVoucherAndGiveDifferentials
     * This method will make the status of voucher as consume and on success
     * give the defferentials
     * If voucher is not poroperly updated to consume the sender and voucher
     * will be credit back
     * 
     * @param p_receiverVO
     * @param p_transferVO
     * @param p_instanceID
     * @param p_quantityRequired
     * @param p_voucherList
     * @return finalTransferStatusUpdate
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean updateVoucherAndGiveDifferentials(Connection p_con, ReceiverVO p_receiverVO, C2STransferVO p_transferVO, String p_instanceID, int p_quantityRequired, ArrayList p_voucherList) throws BTSLBaseException, ParseException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateVoucherAndGiveDifferentials",
                " Eentered for p_quantityRequired=" + p_quantityRequired + " p_instanceID=" + p_instanceID + "p_voucherList size" + p_voucherList.size());
        }
        boolean finalTransferStatusUpdate = true;
        String receiverMsisdn = null;
        VOMSVoucherDAO vomsDAO = null;
        try {
            receiverMsisdn = p_transferVO.getReceiverMsisdn();
            // LoadController.incrementTransactionInterCounts(p_transferVO.getTransferID(),LoadControllerI.SENDER_UNDER_TOP);

            for (int i = 0; i < p_quantityRequired; i++) {
                ((VomsVoucherVO) p_voucherList.get(i)).setCurrentStatus(VOMSI.VOUCHER_USED);
                ((VomsVoucherVO) p_voucherList.get(i)).setPreviousStatus(VOMSI.VOUCHER_UNPROCESS);
            }

            vomsDAO = new VOMSVoucherDAO();
            if (VOMSVoucherDAO.updateVoucherStatus(p_con, p_voucherList) == p_quantityRequired) {
                if (vomsDAO.insertDetailsInVoucherAudit(p_con, p_voucherList) == p_quantityRequired) {
                    p_con.commit();
                } else {
                    p_con.rollback();
                    // If vouchers status cannot be updated successfully, no
                    // exception will be thrown
                    EventHandler
                        .handle(
                            EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM,
                            EventStatusI.RAISED,
                            EventLevelI.MAJOR,
                            "EvdUtil[updateForVOMSUpdationResponse]",
                            null,
                            null,
                            null,
                            "Voucher can not be updated successfully from Serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + " to " + ((VomsVoucherVO) p_voucherList
                                .get(p_quantityRequired - 1)).getSerialNo());
                    // throw new
                    // BTSLBaseException(PretupsErrorCodesI.VOMS_ERROR_INSERTION_AUDIT_TABLE);
                }
            } else {
                // If vouchers status cannot be updated successfully, no
                // exception will be thrown
                EventHandler
                    .handle(
                        EventIDI.SYSTEM_ERROR,
                        EventComponentI.SYSTEM,
                        EventStatusI.RAISED,
                        EventLevelI.MAJOR,
                        "EvdUtil[updateForVOMSUpdationResponse]",
                        null,
                        null,
                        null,
                        "Voucher can not be updated successfully from Serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + " to " + ((VomsVoucherVO) p_voucherList
                            .get(p_quantityRequired - 1)).getSerialNo());
                // throw new
                // BTSLBaseException(PretupsErrorCodesI.VOMS_ERROR_UPDATION);
            }

            VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE, p_transferVO
                .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));

            try {
                updateForVOMSUpdationResponse(p_transferVO, p_receiverVO);
                // LoadController.decreaseResponseCounters(p_transferVO.getTransferID(),PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.SENDER_TOP_RESPONSE);
            }
            /*
             * catch(BTSLBaseException be)
             * {
             * 
             * TransactionLog.log(p_transferVO.getTransferID(),null,receiverMsisdn
             * ,
             * p_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI
             * .TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.
             * TXN_LOG_STATUS_FAIL
             * ,"Transfer Status="+p_transferVO.getTransferStatus
             * ()+" Getting Code="
             * +p_receiverVO.getInterfaceResponseCode()+" voucher serial number="
             * +p_transferVO.getSerialNumber());
             * //decreaseing the resposne counters and making it success in case
             * of Ambiguous and Fail in case of fail
             * if(p_transferVO.getTransferStatus().equals(PretupsErrorCodesI.
             * TXN_STATUS_AMBIGUOUS))
             * LoadController.decreaseResponseCounters(p_transferVO.getTransferID
             * (),PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.
             * SENDER_TOP_RESPONSE);
             * else
             * LoadController.decreaseResponseCounters(p_transferVO.getTransferID
             * (),PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.
             * SENDER_TOP_RESPONSE);
             * //Validating the receiver Limits and updating it
             * PretupsBL.validateRecieverLimits(null,p_transferVO,PretupsI.
             * TRANS_STAGE_AFTER_INTOP,PretupsI.C2S_MODULE);
             * 
             * throw be;
             * }//end catch BTSLBaseException
             */catch (Exception e) {

                TransactionLog.log(p_transferVO.getTransferID() + "-" + p_transferVO.getLastTransferId(), null, receiverMsisdn, p_receiverVO.getNetworkCode(),
                    PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p_transferVO
                        .getTransferStatus() + " Getting Code=" + p_receiverVO.getInterfaceResponseCode() + " voucher serial number=" + ((VomsVoucherVO) p_voucherList.get(0))
                        .getSerialNo() + "-" + ((VomsVoucherVO) p_voucherList.get(p_quantityRequired - 1)).getSerialNo());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                /*
                 * if(p_transferVO.getTransferStatus().equals(PretupsErrorCodesI.
                 * TXN_STATUS_AMBIGUOUS))
                 * LoadController.decreaseResponseCounters(p_transferVO.
                 * getTransferID
                 * (),PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI
                 * .SENDER_TOP_RESPONSE);
                 * else
                 * LoadController.decreaseResponseCounters(p_transferVO.
                 * getTransferID
                 * (),PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI
                 * .SENDER_TOP_RESPONSE);
                 */// Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);

                throw new BTSLBaseException(this, "updateVoucherAndGiveDifferentials", "");
            }// end of catch Exception
            p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            p_transferVO.setErrorCode(null);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(p_instanceID, p_transferVO.getRequestGatewayType(), p_transferVO.getNetworkCode(), p_transferVO
                .getServiceType(), p_transferVO.getTransferID(), LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, p_transferVO.getReceiverNetworkCode());
            TransactionLog.log(p_transferVO.getTransferID() + "-" + p_transferVO.getLastTransferId(), null, receiverMsisdn, p_receiverVO.getNetworkCode(),
                PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Success", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + p_transferVO
                    .getTransferStatus() + " voucher serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + "-" + ((VomsVoucherVO) p_voucherList
                    .get(p_quantityRequired - 1)).getSerialNo());
            // validate receiver limits after Interface Updation
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            p_transferVO.setSenderReturnMessage(null);

            // checking whether differential commission is applicable or not
            if (PretupsI.YES.equals(p_transferVO.getDifferentialAllowedForService())) {
                // Caluculate Differential if transaction successful
                try {
                    new DiffCalBL().differentialCalculations(p_con, p_transferVO, PretupsI.C2S_MODULE, p_quantityRequired, p_voucherList);
                } catch (BTSLBaseException be) {
                    finalTransferStatusUpdate = false;
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                    LOGGER.errorTrace("METHOD_NAME", be);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDUtil[updateVoucherAndGiveDifferentials]",
                        p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "BTSLBaseException:" + be.getMessage());
                } catch (Exception e) {
                    finalTransferStatusUpdate = false;
                    LOGGER
                        .error(
                            this,
                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                    LOGGER.errorTrace("METHOD_NAME", e);
                }
            }
        } catch (BTSLBaseException be) {
            if (p_con != null) {
                try {
                    p_con.rollback();
                } catch (Exception e) {
                    LOGGER.errorTrace("METHOD_NAME", e);
                }
            }
            throw be;
        } catch (Exception e) {
            if (p_con != null) {
                try {
                    p_con.rollback();
                } catch (Exception be) {
                    LOGGER.errorTrace("METHOD_NAME", be);
                }
            }
            throw new BTSLBaseException(this, "updateVoucherAndGiveDifferentials", "");
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateVoucherAndGiveDifferentials", " Exited for p_transferVO= " + p_transferVO);
            }
        }
        return finalTransferStatusUpdate;
    }

    /**
     * Method:getVOMSUpdateRequestStr
     * This method will construct the detils to be sent in request to
     * VOMSHandler
     * 
     * @param p_interfaceAction
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @param p_updateStatus
     * @param p_previousStatus
     * @return
     */
    public String getVOMSUpdateRequestStr(String p_interfaceAction, P2PTransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO, String p_updateStatus, String p_previousStatus) {
        final String methodName = "getVOMSUpdateRequestStr";
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getVOMSCommonString(p_transferVO, p_networkInterfaceModuleVO, p_interfaceVO));
        strBuff.append("&INTERFACE_ACTION=" + p_interfaceAction);
        try {
            strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
        } catch (Exception e) {
            LOGGER.errorTrace(methodName, e);
        }
        strBuff.append("&INTERFACE_AMOUNT=" + p_transferVO.getRequestedAmount());
        strBuff.append("&UPDATE_STATUS=" + p_updateStatus);
        strBuff.append("&PREVIOUS_STATUS=" + p_previousStatus);
        strBuff.append("&SOURCE=" + p_transferVO.getSourceType());
        strBuff.append("&SENDER_MSISDN=" + p_transferVO.getSenderMsisdn());
        strBuff.append("&SERIAL_NUMBER=" + p_transferVO.getSerialNumber());
        strBuff.append("&SENDER_USER_ID=" + p_transferVO.getSenderID());
        return strBuff.toString();
    }

    /**
     * Method: getVOMSCommonString
     * This method will construct the common string that is to be send in
     * request for VOMSHandler
     * 
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @return
     */
    private String getVOMSCommonString(P2PTransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO) {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + p_transferVO.getReceiverMsisdn());
        strBuff.append("&TRANSACTION_ID=" + p_transferVO.getTransferID());
        strBuff.append("&NETWORK_CODE=" + p_transferVO.getReceiverNetworkCode());
        strBuff.append("&INTERFACE_ID=" + p_interfaceVO.getInterfaceId());
        strBuff.append("&INTERFACE_HANDLER=" + p_interfaceVO.getHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + p_networkInterfaceModuleVO.getCommunicationType());
        strBuff.append("&INT_MOD_IP=" + p_networkInterfaceModuleVO.getIP());
        strBuff.append("&INT_MOD_PORT=" + p_networkInterfaceModuleVO.getPort());
        strBuff.append("&INT_MOD_CLASSNAME=" + p_networkInterfaceModuleVO.getClassName());
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        strBuff.append("&CARD_GROUP_SELECTOR=" + p_transferVO.getSubService());
        strBuff.append("&VOUCHER_CODE=" + p_transferVO.getVoucherCode());
        strBuff.append("&SERIALNUMBER=" + p_transferVO.getSerialNumber());
        return strBuff.toString();
    }

    /**
     * Method :calulateTransferValue
     * This method will set the values from the VOMS VO into intemsVO and
     * transferVO
     * 
     * @param p_transferVO
     * @param p_vomsVO
     */
    public void calulateTransferValue(P2PTransferVO p_transferVO, VomsVoucherVO p_vomsVO) {
        p_transferVO.setSenderTransferValue(p_transferVO.getTransferValue());
        final TransferItemVO transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
        final TransferItemVO senderTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);
        p_transferVO.setReceiverGracePeriod(p_vomsVO.getGracePeriod());
        p_transferVO.setReceiverValidity(p_vomsVO.getValidity());
        p_transferVO.setReceiverTransferValue(p_vomsVO.getTalkTime());
        transferItemVO.setTransferValue(p_vomsVO.getTalkTime());
        transferItemVO.setGraceDaysStr(String.valueOf(p_vomsVO.getGracePeriod()));
        transferItemVO.setValidity(p_vomsVO.getValidity());
        senderTransferItemVO.setTransferValue(p_transferVO.getTransferValue());

    }

    /**
     * Method :updateVoucherForFailedTransaction
     * This method will mark the voucher status to enable
     * 
     * @param p_con
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean updateVoucherForFailedTransaction(P2PTransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO) throws BTSLBaseException, ParseException {
        final String methodName = "updateVoucherForFailedTransaction";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(methodName, " Entered for p_transferVO= " + p_transferVO);
        }
        boolean finalTransferStatusUpdate = true;
        C2STransferItemVO senderTransferItemVO = senderTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        try {
            final CommonClient commonClient = new CommonClient();
            final String vomsCreditBackResponse = commonClient.process(getVOMSUpdateRequestStr(PretupsI.INTERFACE_CREDIT_ACTION, p_transferVO, p_networkInterfaceModuleVO,
                p_interfaceVO, VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS), p_transferVO.getTransferID(), p_networkInterfaceModuleVO.getCommunicationType(),
                p_networkInterfaceModuleVO.getIP(), p_networkInterfaceModuleVO.getPort(), p_networkInterfaceModuleVO.getClassName());
            // getting the update status from the Response and set in
            // appropriate VO: senderTransferItemVO update Status 1 can be used
            try {
                final HashMap map = BTSLUtil.getStringToHash(vomsCreditBackResponse, "&", "=");
                senderTransferItemVO.setUpdateStatus1((String) map.get("TRANSACTION_STATUS"));
                if (!InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getUpdateStatus1())) {
                    // p_transferVO.setErrorCode(senderTransferItemVO.getUpdateStatus1()+"_S");
                    throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
                VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE, p_transferVO
                    .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil
                    .getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
            } catch (Exception e) {
                LOGGER.error(methodName, " Exception while updating voucher status= " + e.getMessage());
                LOGGER.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Exception while updating voucher status");
            }
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getReceiverNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to voucher for serial number=" + p_transferVO.getSerialNumber(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            LOGGER.errorTrace(methodName, be);
            finalTransferStatusUpdate = false;
            senderTransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back voucher", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            LOGGER.errorTrace(methodName, be);
            throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(methodName, " Exited for finalTransferStatusUpdate= " + finalTransferStatusUpdate);
            }
        }
        return finalTransferStatusUpdate;
    }

    /**
     * Method:getVOMSUpdateRequestStr
     * This method will construct the detils to be sent in request to
     * VOMSHandler
     * 
     * @param p_interfaceAction
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @param p_updateStatus
     * @param p_previousStatus
     * @return
     */
    public String getVCNO2CUpdateRequestStr(String p_interfaceAction, C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO, String p_updateStatus, String p_previousStatus) {
        final String methodName = "getVCNO2CUpdateRequestStr";
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getVCNO2CCommonString(p_transferVO, p_networkInterfaceModuleVO, p_interfaceVO));
        strBuff.append("&INTERFACE_ACTION=" + p_interfaceAction);
        try {
            strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
        } catch (Exception e) {
            LOGGER.errorTrace(methodName, e);
        }
        strBuff.append("&INTERFACE_AMOUNT=" + p_transferVO.getRequestedAmount());
        strBuff.append("&UPDATE_STATUS=" + p_updateStatus);
        strBuff.append("&PREVIOUS_STATUS=" + p_previousStatus);
        strBuff.append("&SOURCE=" + p_transferVO.getSourceType());
        strBuff.append("&SENDER_MSISDN=" + p_transferVO.getSenderMsisdn());
        strBuff.append("&SERIAL_NUMBER=" + p_transferVO.getSerialNumber());
        strBuff.append("&SENDER_USER_ID=" + p_transferVO.getSenderID());
        return strBuff.toString();
    }

    /**
     * Method: getVCNO2CCommonString
     * This method will construct the common string that is to be send in
     * request for VOMSHandler
     * 
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @return
     */
    private String getVCNO2CCommonString(C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO) {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + p_transferVO.getReceiverMsisdn());
        strBuff.append("&TRANSACTION_ID=" + p_transferVO.getTransferID());
        strBuff.append("&NETWORK_CODE=" + p_transferVO.getReceiverNetworkCode());
        strBuff.append("&INTERFACE_ID=" + p_interfaceVO.getInterfaceId());
        strBuff.append("&INTERFACE_HANDLER=" + p_interfaceVO.getHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + p_networkInterfaceModuleVO.getCommunicationType());
        strBuff.append("&INT_MOD_IP=" + p_networkInterfaceModuleVO.getIP());
        strBuff.append("&INT_MOD_PORT=" + p_networkInterfaceModuleVO.getPort());
        strBuff.append("&INT_MOD_CLASSNAME=" + p_networkInterfaceModuleVO.getClassName());
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        strBuff.append("&CARD_GROUP_SELECTOR=" + p_transferVO.getSubService());
        strBuff.append("&VOUCHER_CODE=" + p_transferVO.getVoucherCode());
        strBuff.append("&SERIALNUMBER=" + p_transferVO.getSerialNumber());
        return strBuff.toString();

    }
}
