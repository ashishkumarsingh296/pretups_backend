package com.client.pretups.channel.logging.vietnam;

import java.util.Date;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/*
 * @(#)O2CTransferProcessLog.java Name Date History
 * ------------------------------------------------------------------------
 * Trasha Dewan            10/12/2017            Initial Creation
 * ------------------------------------------------------------------------
 * Class for logging all the ERP O2C request
 */

public class O2CTransferProcessLog {
    private static Log LOG = LogFactory.getLog(O2CTransferProcessLog.class.getName());

    public static void log(RequestVO requestVO) {
        final String methodName = "O2CTransferProcessLog.log";
        LogFactory.printLog(methodName, "Entered...", LOG);
        final StringBuilder strBldr = new StringBuilder();
        try {
        	Date date = (Date) requestVO.getRequestMap().get("PAYMENTDATE");
            strBldr.append(" [OrderNumber_LineId:" + requestVO.getRequestMap().get("EXTTXNNUMBER") + "]");
            strBldr.append(" [Requested Qty:" +requestVO.getReqAmount() + "]");
            strBldr.append(" [Payment Date:" +BTSLDateUtil.getSystemLocaleDate(date, true) + "]");
            strBldr.append(" [Remarks:" +requestVO.getRequestMap().get("REMARKS") + "]");
            strBldr.append(" [Error Code:" +requestVO.getMessageCode()+ "]");
            strBldr.append(" [Transaction ID:" +BTSLUtil.NullToString(requestVO.getTransactionID())+ "]");
            if (requestVO.getMessageArguments().length>1){
            	strBldr.append(" [Post Balance:" +((String[])requestVO.getMessageArguments())[1]+ "]");
            }else{
            	strBldr.append(" [Post Balance:]");
            }
            strBldr.append(" [Receiver MSISDN:" +BTSLUtil.NullToString(requestVO.getFilteredMSISDN()) +"]");
            strBldr.append(" [External Code:" +requestVO.getSenderExternalCode()+ "]");
            LOG.info(methodName, strBldr.toString());
            LogFactory.printLog(methodName, strBldr.toString(),LOG);
            LogFactory.printLog(methodName,"Exiting...",LOG);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            LOG.error(methodName,   requestVO.getRequestMap().get("EXTTXNNUMBER") , " Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferProcessLog[" + methodName + "]", (String)requestVO.getRequestMap().get("EXTTXNNUMBER"), "", "",
                "Not able to log info for Transfer ID:" +  requestVO.getRequestMap().get("EXTTXNNUMBER")  + " ,getting Exception=" + e
                    .getMessage());
        }
    }
}
