package com.selftopup.pretups.p2p.logging;

/*
 * @(#)P2PGatewayRequestLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the p2P request
 */

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.util.BTSLUtil;

public class P2PGatewayRequestLog {
    private static Log _log = LogFactory.getLog(P2PGatewayRequestLog.class.getName());

    /**
     * Method that prepares the the string to be written in log file
     * 
     * @param p_requestVO
     * @return
     */
    private static String generateMessageString(RequestVO p_requestVO) {
        StringBuffer strBuff = new StringBuffer();
        try {
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            strBuff.append(" [Request ID:" + p_requestVO.getRequestIDStr() + "]");
            strBuff.append(" [Creation Date:" + p_requestVO.getCreatedOn() + "]");
            strBuff.append(" [Service Type:" + p_requestVO.getServiceType() + "]");
            strBuff.append(" [Source:" + p_requestVO.getRequestGatewayType() + "]");
            strBuff.append(" [Request Code:" + BTSLUtil.NullToString(p_requestVO.getMessageCode()) + "]");
            if (senderVO != null)
                strBuff.append(" [UName:" + BTSLUtil.NullToString(senderVO.getUserName()) + "]");
            else
                strBuff.append(" [UName:Not available ]");
            strBuff.append(" [MSISDN:" + p_requestVO.getRequestMSISDN() + "]");
            if (senderVO != null) {
                strBuff.append(" [UStatus:" + senderVO.getStatus() + "]");
                strBuff.append(" [U N/W:" + senderVO.getNetworkCode() + "]");
            } else {
                strBuff.append(" [UStatus:Not available]");
                strBuff.append(" [U N/W:Not available]");
            }
            strBuff.append(" [Incoming SMS:" + p_requestVO.getRequestMessage() + "]");
            if (!BTSLUtil.isNullString(p_requestVO.getIncomingSmsStr()))
                strBuff.append(" [Decrypted SMS:" + p_requestVO.getIncomingSmsStr() + "]");
            else
                strBuff.append(" [Decrypted SMS:Not applicable]");
            strBuff.append(" [UDH:" + BTSLUtil.NullToString(p_requestVO.getUDH()) + "]");
            strBuff.append(" [Source Type:" + p_requestVO.getSourceType() + "]");
            strBuff.append(" [Service Port:" + p_requestVO.getServicePort() + "]");
            if (p_requestVO.getRequestGatewayCode() != null)
                strBuff.append(" [Other Info:" + p_requestVO.getRequestGatewayCode() + ",Msg Required=" + p_requestVO.isUnmarkSenderUnderProcess() + ", Flow Type=" + ((p_requestVO.getMessageGatewayVO() == null) ? "" : p_requestVO.getMessageGatewayVO().getFlowType()) + " Resp Type=" + p_requestVO.getMsgResponseType() + " Req Content Type=" + p_requestVO.getReqContentType() + "]");
            else
                strBuff.append(" [Other Info: Msg Required=" + p_requestVO.isUnmarkSenderUnderProcess() + "Resp Type=" + p_requestVO.getMsgResponseType() + " Req Content Type=" + p_requestVO.getReqContentType() + "]");
            if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()))
                strBuff.append(" [RETMSG:" + p_requestVO.getSenderReturnMessage() + "]");
            else {
                strBuff.append(" [RETMSG:" + BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "]");
            }
            strBuff.append(" [TT:" + (System.currentTimeMillis() - p_requestVO.getCreatedOn().getTime()) + " ms]");
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("generateMessageString", p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PGatewayRequestLog[generateMessageString]", "", p_requestVO.getFilteredMSISDN(), "", "Not able to write in Request Log for Request ID:" + p_requestVO.getRequestID() + " and MSISDN:" + p_requestVO.getFilteredMSISDN() + " ,getting Exception=" + e.getMessage());
        }
        return strBuff.toString();
    }// end of generateMessageString

    /**
     * Method to log the details in Request Log
     * 
     * @param p_requestVO
     */
    public static void log(RequestVO p_requestVO) {
        /*
         * try
         * {
         */_log.info("", generateMessageString(p_requestVO));
        // }//end of try
        /*
         * catch(Exception e)
         * {
         * e.printStackTrace();
         * _log.error("log",p_requestVO.getRequestIDStr()," MSISDN="+p_requestVO.
         * getFilteredMSISDN()+" Exception :"+e.getMessage());
         * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
         * EventStatusI
         * .RAISED,EventLevelI.FATAL,"P2PGatewayRequestLog[log]","",p_requestVO
         * .getFilteredMSISDN
         * (),"","Not able to write in Request Log for Request ID:"
         * +p_requestVO.getRequestID
         * ()+" and MSISDN:"+p_requestVO.getFilteredMSISDN
         * ()+" ,getting Exception="+e.getMessage());
         * }//end of catch
         */}// end of log
}
