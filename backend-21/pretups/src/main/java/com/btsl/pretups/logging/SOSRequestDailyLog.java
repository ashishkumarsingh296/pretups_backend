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
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.PretupsBL;

/**
 * @(#) SOSRequestDailyLog.java
 *      Name Date History
 *      ------------------------------------------------------------------------
 *      Abhay 20/01/2010 Initial Creation
 *      ------------------------------------------------------------------------
 *      Copyright (c) 2010 Comviva.
 *      Logger used to track the transaction details and time taken by IN during
 *      validation and topup for SOS service.
 */

public class SOSRequestDailyLog {
    private static Log _log = LogFactory.getFactory().getInstance(SOSRequestDailyLog.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private SOSRequestDailyLog(){
    	
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

            strBuff.append("[InstanceID:" + p_requestDailyLogVO.getInstanceID() + "]");
            strBuff.append("[SenderNetworkCode:" + p_requestDailyLogVO.getSenderNetworkCode() + "]");
            strBuff.append("[RequestReceivedTime:" + p_requestDailyLogVO.getRequestRecivedTime() + "]");
            strBuff.append("[Request Log Time:" + p_requestDailyLogVO.getRequestExitTime() + "]");
            strBuff.append("[RequestID:" + p_requestDailyLogVO.getRequestId() + "]");
            strBuff.append("[ServiceType:" + p_requestDailyLogVO.getServiceType() + "]");
            strBuff.append("[RequestSourceType:" + p_requestDailyLogVO.getRequestSourceType() + "]");
            strBuff.append("[RequestExitTime:" + p_requestDailyLogVO.getRequestExitTime() + "]");
            strBuff.append("[SenderMSISDN:" + p_requestDailyLogVO.getSenderMSISDN() + "]");
            strBuff.append("[TransactionStatus:" + p_requestDailyLogVO.getTransactionStatus() + "]");
            strBuff.append("[TransactionID:" + p_requestDailyLogVO.getTransactionID() + "]");
            strBuff.append("[ErrorCode:" + p_requestDailyLogVO.getErrorCode() + "]");
            strBuff.append("[Amount:" + p_requestDailyLogVO.getAmount() + "]");
            strBuff.append("[SubServiceType:" + p_requestDailyLogVO.getSubServiceType() + "]");
            strBuff.append("[SenderInterfaceID:" + p_requestDailyLogVO.getSenderInterfaceID() + "]");
            strBuff.append("[senderValidateTime:" + p_requestDailyLogVO.getSenderValidateTime() + "]");
            strBuff.append("[senderToPUPTime:" + p_requestDailyLogVO.getSenderToPUPTime() + "]");
            strBuff.append("[RequestTotalTime:" + p_requestDailyLogVO.getTotalTime() + "]");
            strBuff.append("[PreTUPSProcessingTime:" + p_requestDailyLogVO.getPreTUPSProcessingTime() + "]");

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
    public static RequestDailyLogVO populateSOSRequestDailyLogVO(RequestVO p_requestVO, P2PTransferVO p_p2pTransferVO) {
        RequestDailyLogVO requestDailyLogVO = new RequestDailyLogVO();
        requestDailyLogVO.setInstanceID(LoadControllerCache.getInstanceID());
        if (p_p2pTransferVO.getSenderVO() != null) {
            requestDailyLogVO.setSenderMSISDN(((SenderVO) p_p2pTransferVO.getSenderVO()).getMsisdn());
            requestDailyLogVO.setSenderNetworkCode(((SenderVO) p_p2pTransferVO.getSenderVO()).getNetworkCode());
        }
        requestDailyLogVO.setRequestRecivedTime(p_p2pTransferVO.getRequestStartTime());
        // requestDailyLogVO.setRequestId(p_requestVO.getRequestIDStr());
        // requestDailyLogVO.setServiceType(p_requestVO.getServiceType());
        requestDailyLogVO.setRequestSourceType(p_requestVO.getSourceType());
        requestDailyLogVO.setRequestExitTime(System.currentTimeMillis());// TBD
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

        requestDailyLogVO.setSenderValidateTime(p_requestVO.getValidationSenderResponseReceived() - p_requestVO.getValidationSenderRequestSent());
        requestDailyLogVO.setSenderToPUPTime(p_requestVO.getTopUPSenderResponseReceived() - p_requestVO.getTopUPSenderRequestSent());
        requestDailyLogVO.setTotalTime(requestDailyLogVO.getRequestExitTime() - requestDailyLogVO.getRequestRecivedTime());
        requestDailyLogVO.setPreTUPSProcessingTime(requestDailyLogVO.getRequestExitTime() - requestDailyLogVO.getRequestRecivedTime() - requestDailyLogVO.getReceiverValidateTime() - requestDailyLogVO.getReceiverToPUPTime() - requestDailyLogVO.getSenderValidateTime() - requestDailyLogVO.getSenderToPUPTime());
        return requestDailyLogVO;
    }

}
