package com.btsl.pretups.logging;

/**
 * @(#)ChannelRequestDailyLog.java
 *                                 Name Date History
 *                                 --------------------------------------------
 *                                 ----------------------------
 *                                 Divyakant Verma 07/02/2008
 *                                 --------------------------------------------
 *                                 ----------------------------
 *                                 Copyright (c) 2005 Bharti Telesoft Ltd.
 *                                 Logger used to track the time, taken by IN
 *                                 during validation and topup for C2S.
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;

public class ChannelRequestDailyLog {
    private static Log _log = LogFactory.getFactory().getInstance(ChannelRequestDailyLog.class.getName());

    /**
     * ensures no instantiation
     */
    private ChannelRequestDailyLog(){
    	
    }
    /**
     * Used to log the information.
     * 
     * @param p_requestDailyLogVO
     */
    public static void log(RequestDailyLogVO p_requestDailyLogVO) {
        final String METHOD_NAME = "log";
        try {
            StringBuilder strBuild = new StringBuilder();

            strBuild.append("[IID:");
            strBuild.append(p_requestDailyLogVO.getInstanceID());
            strBuild.append("]");

            strBuild.append("[RNW:");
            strBuild.append(p_requestDailyLogVO.getReceiverNetworkCode());
            strBuild.append("]");

            strBuild.append("[RQRVT:");
            strBuild.append(p_requestDailyLogVO.getRequestRecivedTime());
            strBuild.append("]");

            strBuild.append("[RQLGT:");
            strBuild.append(p_requestDailyLogVO.getRequestExitTime());
            strBuild.append("]");

            strBuild.append("[RQID:");
            strBuild.append(p_requestDailyLogVO.getRequestId());
            strBuild.append("]");

            strBuild.append("[STV:");
            strBuild.append(p_requestDailyLogVO.getServiceType());
            strBuild.append("]");

            strBuild.append("[RQST:");
            strBuild.append(p_requestDailyLogVO.getRequestSourceType());
            strBuild.append("]");

            strBuild.append("[RQS:");
            strBuild.append(p_requestDailyLogVO.getRequestSources());
            strBuild.append("]");

            strBuild.append("[RQEST:");
            strBuild.append(p_requestDailyLogVO.getRequestExitTime());
            strBuild.append("]");

            strBuild.append("[SUID:");
            strBuild.append(p_requestDailyLogVO.getSenderUserID());
            strBuild.append("]");

            strBuild.append("[SUN:");
            strBuild.append(p_requestDailyLogVO.getSenderUserName());
            strBuild.append("]");

            strBuild.append("[SC:");
            strBuild.append(p_requestDailyLogVO.getSenderCategory());
            strBuild.append("]");

            strBuild.append("[SM:");
            strBuild.append(p_requestDailyLogVO.getSenderMSISDN());
            strBuild.append("]");

            strBuild.append("[SNW:");
            strBuild.append(p_requestDailyLogVO.getSenderNetworkCode());
            strBuild.append("]");

            strBuild.append("[TID:");
            strBuild.append(p_requestDailyLogVO.getTransactionID());
            strBuild.append("]");

            strBuild.append("[TS:");
            strBuild.append(p_requestDailyLogVO.getTransactionStatus());
            strBuild.append("]");

            strBuild.append("[E:");
            strBuild.append(p_requestDailyLogVO.getErrorCode());
            strBuild.append("]");

            strBuild.append("[RM:");
            strBuild.append(p_requestDailyLogVO.getReceiverMSISDN());
            strBuild.append("]");

            strBuild.append("[AMT:");
            strBuild.append(p_requestDailyLogVO.getAmount());
            strBuild.append("]");

            strBuild.append("[SST:");
            strBuild.append(p_requestDailyLogVO.getSubServiceType());
            strBuild.append("]");

            strBuild.append("[RID:");
            strBuild.append(p_requestDailyLogVO.getReceiverInterfaceID());
            strBuild.append("]");
            // for Receiver
            strBuild.append("[VAL:");
            strBuild.append(p_requestDailyLogVO.getReceiverValidateTime());
            strBuild.append("]");

            strBuild.append("[TOP:");
            strBuild.append(p_requestDailyLogVO.getReceiverToPUPTime());
            strBuild.append("]");

            strBuild.append("[RTT:");
            strBuild.append(p_requestDailyLogVO.getTotalTime());
            strBuild.append("]");

            strBuild.append("[PPT:");
            strBuild.append(p_requestDailyLogVO.getPreTUPSProcessingTime());
            strBuild.append("]");
			
			strBuild.append("[INVALURL:");
			strBuild.append(p_requestDailyLogVO.getINValidationURL());
			strBuild.append("]");
			
			strBuild.append("[INVALRESP:");
			strBuild.append(p_requestDailyLogVO.getINValidationResp());
			strBuild.append("]");
			
			strBuild.append("[INRURL:");
			strBuild.append(p_requestDailyLogVO.getINCreditURL());
			strBuild.append("]");
			
			strBuild.append("[INRRESP:");
			strBuild.append(p_requestDailyLogVO.getINCreditResp());
			strBuild.append("]");
			
            _log.info("", strBuild.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_requestDailyLogVO.getInstanceID() + "" + p_requestDailyLogVO.getRequestId(), " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransactionLog[log]", p_requestDailyLogVO.getInstanceID() + "-" + p_requestDailyLogVO.getRequestId(), "", "", "Not able to log info for Transaction ID - Request ID:" + p_requestDailyLogVO.getInstanceID() + "-" + p_requestDailyLogVO.getRequestId() + " ,getting Exception=" + e.getMessage());
        }
    }

    /**
     * User to set the values in requestDailyLogVO VO.
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @return RequestDailyLogVO
     */
    public static RequestDailyLogVO populateChannelRequestDailyLogVO(RequestVO p_requestVO, C2STransferVO p_c2sTransferVO) {
        RequestDailyLogVO requestDailyLogVO = new RequestDailyLogVO();
        requestDailyLogVO.setInstanceID(LoadControllerCache.getInstanceID());
        requestDailyLogVO.setRequestId(p_requestVO.getRequestIDStr());
        requestDailyLogVO.setServiceType(p_requestVO.getServiceType());
        requestDailyLogVO.setSubServiceType(p_c2sTransferVO.getSubService());
        if (p_requestVO.getSenderVO() != null) {
            requestDailyLogVO.setSenderNetworkCode(((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkID());
            requestDailyLogVO.setSenderUserID(((ChannelUserVO) p_requestVO.getSenderVO()).getUserID());
            requestDailyLogVO.setSenderUserName(((ChannelUserVO) p_requestVO.getSenderVO()).getUserName());
            requestDailyLogVO.setSenderMSISDN(((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO().getMsisdn());
            if (((ChannelUserVO) p_requestVO.getSenderVO()).getCategoryVO() != null) {
                requestDailyLogVO.setSenderCategory(((CategoryVO) ((ChannelUserVO) p_requestVO.getSenderVO()).getCategoryVO()).getCategoryName());
            }
        }
        if (p_c2sTransferVO.getReceiverVO() != null) {
            requestDailyLogVO.setReceiverNetworkCode(((ReceiverVO) p_c2sTransferVO.getReceiverVO()).getNetworkCode());
            requestDailyLogVO.setReceiverMSISDN(((ReceiverVO) p_c2sTransferVO.getReceiverVO()).getMsisdn());
        }
        requestDailyLogVO.setAmount(PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()));
        requestDailyLogVO.setRequestSourceType(p_requestVO.getSourceType());
        //requestDailyLogVO.setRequestSources(p_c2sTransferVO.getSourceType());// TBD
		requestDailyLogVO.setRequestSources(p_c2sTransferVO.getRequestGatewayCode());
        requestDailyLogVO.setTransactionID(p_c2sTransferVO.getTransferID());
        requestDailyLogVO.setTransactionStatus(p_c2sTransferVO.getTransferStatus());
        requestDailyLogVO.setErrorCode(p_c2sTransferVO.getErrorCode());
        requestDailyLogVO.setRequestRecivedTime(p_c2sTransferVO.getRequestStartTime());
        requestDailyLogVO.setRequestExitTime(System.currentTimeMillis());// TBD
        if (p_c2sTransferVO.getTransferItemList() != null) {
            if (p_c2sTransferVO.getTransferItemList().get(1) != null) {
                requestDailyLogVO.setReceiverInterfaceID(((C2STransferItemVO) (p_c2sTransferVO.getTransferItemList().get(1))).getInterfaceID()); // TBD
            }
        }

        requestDailyLogVO.setReceiverValidateTime(p_requestVO.getValidationReceiverResponseReceived() - p_requestVO.getValidationReceiverRequestSent());
        requestDailyLogVO.setReceiverToPUPTime(p_requestVO.getTopUPReceiverResponseReceived() - p_requestVO.getTopUPReceiverRequestSent());
        requestDailyLogVO.setTotalTime(requestDailyLogVO.getRequestExitTime() - requestDailyLogVO.getRequestRecivedTime());
        requestDailyLogVO.setPreTUPSProcessingTime(requestDailyLogVO.getRequestExitTime() - requestDailyLogVO.getRequestRecivedTime() - requestDailyLogVO.getReceiverValidateTime() - requestDailyLogVO.getReceiverToPUPTime());
		requestDailyLogVO.setINCreditResp(p_requestVO.getCreditINRespCode());
		requestDailyLogVO.setINValidationResp(p_requestVO.getValINRespCode());
		requestDailyLogVO.setINCreditURL(p_requestVO.getInCreditURL());
		requestDailyLogVO.setINValidationURL(p_requestVO.getInValidateURL());
		
        return requestDailyLogVO;
    }

}
