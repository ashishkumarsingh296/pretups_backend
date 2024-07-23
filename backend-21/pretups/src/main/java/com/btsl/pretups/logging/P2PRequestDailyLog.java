package com.btsl.pretups.logging;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.PretupsBL;

/**
 * @(#) P2PRequestDailyLog.java
 *      Name Date History
 *      ------------------------------------------------------------------------
 *      Divyakant Verma 07/02/2008 Initial Creation
 *      ------------------------------------------------------------------------
 *      Copyright (c) 2005 Bharti Telesoft Ltd.
 *      Logger used to track the time, taken by IN during validation and topup
 *      for CP2P.
 */

public class P2PRequestDailyLog {
    private static Log _log = LogFactory.getFactory().getInstance(P2PRequestDailyLog.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private P2PRequestDailyLog(){
    	
    }
    
    /**
     * Used to log the information.
     * 
     * @param p_requestDailyLogVO
     */
    public static void log(RequestDailyLogVO p_requestDailyLogVO) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();

            strBuff.append("[INSTID:");
            strBuff.append(p_requestDailyLogVO.getInstanceID());
            strBuff.append("]");

            strBuff.append("[S_NW_C:");
            strBuff.append(p_requestDailyLogVO.getSenderNetworkCode());
            strBuff.append("]");

            strBuff.append("[R_NW_C:");
            strBuff.append(p_requestDailyLogVO.getReceiverNetworkCode());
            strBuff.append("]");

            strBuff.append("[RQ_R_T:");
            strBuff.append(p_requestDailyLogVO.getRequestRecivedTime());
            strBuff.append("]");

            strBuff.append("[RQ_L_T:");
            strBuff.append(p_requestDailyLogVO.getRequestExitTime());
            strBuff.append("]");

            strBuff.append("[RQ_ID:");
            strBuff.append(p_requestDailyLogVO.getRequestId());
            strBuff.append("]");

            strBuff.append("[SRV_TP:");
            strBuff.append(p_requestDailyLogVO.getServiceType());
            strBuff.append("]");

            strBuff.append("[RQ_SRC_TP:");
            strBuff.append(p_requestDailyLogVO.getRequestSourceType());
            strBuff.append("]");

            strBuff.append("[RQ_EXT_T:");
            strBuff.append(p_requestDailyLogVO.getRequestExitTime());
            strBuff.append("]");

            strBuff.append("[S_USR_ID:");
            strBuff.append(p_requestDailyLogVO.getSenderUserID());
            strBuff.append("]");

            strBuff.append("[S_MSISDN:");
            strBuff.append(p_requestDailyLogVO.getSenderMSISDN());
            strBuff.append("]");

            strBuff.append("[R_MSISDN:");
            strBuff.append(p_requestDailyLogVO.getReceiverMSISDN());
            strBuff.append("]");

            strBuff.append("[TXN_ST:");
            strBuff.append(p_requestDailyLogVO.getTransactionStatus());
            strBuff.append("]");

            strBuff.append("[TXN_ID:");
            strBuff.append(p_requestDailyLogVO.getTransactionID());
            strBuff.append("]");

            strBuff.append("[ERR_C:");
            strBuff.append(p_requestDailyLogVO.getErrorCode());
            strBuff.append("]");

            strBuff.append("[AMT:");
            strBuff.append(p_requestDailyLogVO.getAmount());
            strBuff.append("]");

            strBuff.append("[S_SRV_TP:");
            strBuff.append(p_requestDailyLogVO.getSubServiceType());
            strBuff.append("]");

            strBuff.append("[R_INTID:");
            strBuff.append(p_requestDailyLogVO.getReceiverInterfaceID());
            strBuff.append("]");

            strBuff.append("[S_INTID:");
            strBuff.append(p_requestDailyLogVO.getSenderInterfaceID());
            strBuff.append("]");

            // for Sender
            strBuff.append("[S_VT:");
            strBuff.append(p_requestDailyLogVO.getSenderValidateTime());
            strBuff.append("]");

            strBuff.append("[S_TUT:");
            strBuff.append(p_requestDailyLogVO.getSenderToPUPTime());
            strBuff.append("]");

            // for Receiver
            strBuff.append("[R_VT:");
            strBuff.append(p_requestDailyLogVO.getReceiverValidateTime());
            strBuff.append("]");

            strBuff.append("[R_TUT:");
            strBuff.append(p_requestDailyLogVO.getReceiverToPUPTime());
            strBuff.append("]");

            strBuff.append("[RQ_TT:");
            strBuff.append(p_requestDailyLogVO.getTotalTime());
            strBuff.append("]");

            strBuff.append("[PPT:");
            strBuff.append(p_requestDailyLogVO.getPreTUPSProcessingTime());
            strBuff.append("]");
//GNOC INTEGRATION - On Rquest of Gopal
		strBuff.append("[SV_IP:");
            strBuff.append(p_requestDailyLogVO.getINValidationURL());
            strBuff.append("]");
            strBuff.append("[SV_RESP:");
            strBuff.append(p_requestDailyLogVO.getINValidationResp());
            strBuff.append("]");
            strBuff.append("[RV_IP:");
            strBuff.append(p_requestDailyLogVO.getINValidationResp());
            strBuff.append("]");
            strBuff.append("[RV_RESP:");
            strBuff.append(p_requestDailyLogVO.getINValidationResp());
            strBuff.append("]");
            strBuff.append("[SD_IP:");
            strBuff.append(p_requestDailyLogVO.getINCreditURL());
            strBuff.append("]");
            strBuff.append("[SD_RESP:");
            strBuff.append(p_requestDailyLogVO.getINCreditResp());
            strBuff.append("]");
            strBuff.append("[RC_IP:");
            strBuff.append(p_requestDailyLogVO.getINCreditURL());
            strBuff.append("]");
            strBuff.append("[RC_RESP:");
            strBuff.append(p_requestDailyLogVO.getINCreditResp());
            strBuff.append("]");
            strBuff.append("[SCB_IP:");
            strBuff.append(p_requestDailyLogVO.getINCreditURL());
            strBuff.append("]");
            strBuff.append("[SCB_RESP:");
            strBuff.append(p_requestDailyLogVO.getINCreditResp());
            strBuff.append("]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_requestDailyLogVO.getInstanceID() + "" + p_requestDailyLogVO.getRequestId(), " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransactionLog[log]", p_requestDailyLogVO.getInstanceID() + "-" + p_requestDailyLogVO.getRequestId(), "", "", "Not able to log info for Transaction ID - Request ID:" + p_requestDailyLogVO.getInstanceID() + "-" + p_requestDailyLogVO.getRequestId() + " ,getting Exception=" + e.getMessage());
        }
    }

    /**
     * Used to set the values in requestDailyLogVO VO.
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_p2pTransferVO
     *            P2PTransferVO
     * @return RequestDailyLogVO
     */
    public static RequestDailyLogVO populateP2PRequestDailyLogVO(RequestVO p_requestVO, P2PTransferVO p_p2pTransferVO) {
        RequestDailyLogVO requestDailyLogVO = new RequestDailyLogVO();
        requestDailyLogVO.setInstanceID(LoadControllerCache.getInstanceID());
        if (p_p2pTransferVO.getSenderVO() != null) {
            requestDailyLogVO.setSenderNetworkCode(((SenderVO) p_p2pTransferVO.getSenderVO()).getNetworkCode());
        }

        if (p_p2pTransferVO.getReceiverVO() != null) {
            requestDailyLogVO.setReceiverNetworkCode(((ReceiverVO) p_p2pTransferVO.getReceiverVO()).getNetworkCode());
            requestDailyLogVO.setReceiverMSISDN(((ReceiverVO) p_p2pTransferVO.getReceiverVO()).getMsisdn());
        }
        requestDailyLogVO.setRequestRecivedTime(p_p2pTransferVO.getRequestStartTime());
        requestDailyLogVO.setRequestId(p_requestVO.getRequestIDStr());
        requestDailyLogVO.setServiceType(p_requestVO.getServiceType());
        requestDailyLogVO.setRequestSourceType(p_requestVO.getSourceType());
        requestDailyLogVO.setRequestExitTime(System.currentTimeMillis());// TBD
        if (p_requestVO.getSenderVO() != null) {
            requestDailyLogVO.setSenderUserID(((SenderVO) p_requestVO.getSenderVO()).getUserID());
            requestDailyLogVO.setSenderMSISDN(((SenderVO) p_requestVO.getSenderVO()).getMsisdn());
            requestDailyLogVO.setSenderCategory(((SenderVO) p_requestVO.getSenderVO()).getCategory());
        }
        requestDailyLogVO.setTransactionStatus(p_p2pTransferVO.getTransferStatus());
        requestDailyLogVO.setTransactionID(p_p2pTransferVO.getTransferID());
        requestDailyLogVO.setErrorCode(p_p2pTransferVO.getErrorCode());
        requestDailyLogVO.setAmount(PretupsBL.getDisplayAmount(p_p2pTransferVO.getRequestedAmount()));
        requestDailyLogVO.setSubServiceType(p_p2pTransferVO.getSubService());
        if (p_p2pTransferVO.getTransferItemList() != null) {
            if (p_p2pTransferVO.getTransferItemList().get(0) != null) {
                requestDailyLogVO.setSenderInterfaceID(((TransferItemVO) (p_p2pTransferVO.getTransferItemList().get(0))).getInterfaceID()); // TBD
            }
        }
        if (p_p2pTransferVO.getTransferItemList() != null) {
            if (p_p2pTransferVO.getTransferItemList().get(1) != null) {
                requestDailyLogVO.setReceiverInterfaceID(((TransferItemVO) (p_p2pTransferVO.getTransferItemList().get(1))).getInterfaceID()); // TBD
            }
        }

        requestDailyLogVO.setSenderValidateTime(p_requestVO.getValidationSenderResponseReceived() - p_requestVO.getValidationSenderRequestSent());
        requestDailyLogVO.setSenderToPUPTime(p_requestVO.getTopUPSenderResponseReceived() - p_requestVO.getTopUPSenderRequestSent());
        requestDailyLogVO.setReceiverValidateTime(p_requestVO.getValidationReceiverResponseReceived() - p_requestVO.getValidationReceiverRequestSent());
        requestDailyLogVO.setReceiverToPUPTime(p_requestVO.getTopUPReceiverResponseReceived() - p_requestVO.getTopUPReceiverRequestSent());
        requestDailyLogVO.setTotalTime(requestDailyLogVO.getRequestExitTime() - requestDailyLogVO.getRequestRecivedTime());
        requestDailyLogVO.setPreTUPSProcessingTime(requestDailyLogVO.getRequestExitTime() - requestDailyLogVO.getRequestRecivedTime() - requestDailyLogVO.getReceiverValidateTime() - requestDailyLogVO.getReceiverToPUPTime() - requestDailyLogVO.getSenderValidateTime() - requestDailyLogVO.getSenderToPUPTime());
        return requestDailyLogVO;
    }

}
