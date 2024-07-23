package com.btsl.pretups.logging;

import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class SOSSettlementLog {
	
	private void SOSSettlementLog()
	{
		
	}
			private static Log log = LogFactory.getFactory().getInstance(SOSSettlementLog.class.getName());

		    public static void log( ChannelTransferVO channelTransferVO, String response) {
		        final String METHOD_NAME = "log";
		        try {
		    
		        	StringBuilder strBuff = new StringBuilder();
		            strBuff.append(" [ Settled  On: " +BTSLUtil.getDateTimeStringFromDate(new Date(), "dd/MM/yyyy HH:mm:ss")+ "]" );
		            strBuff.append(" [ Settled To:" + channelTransferVO.getReceiverLoginID()+ "]");
		            strBuff.append(" [ SOS Txn ID:" + channelTransferVO.getReferenceNum()+ "]");
		            strBuff.append(" [ Settlement Txn ID:" + channelTransferVO.getSosTxnId());
		            strBuff.append(" [ Settlement Txn Amount:" + PretupsBL.getDisplayAmount(channelTransferVO.getSosRequestAmount())+ "]");
			        strBuff.append(" [ Settlement Withdraw ID:" + channelTransferVO.getTransferID()+ "]");
		            strBuff.append(" [ Settlement Network Stock ID:" + channelTransferVO.getReferenceID()+ "]");
		            strBuff.append(" [ Settlement From Account ID:" + channelTransferVO.getFromUserCode()+ "]");
		            strBuff.append(" [ Settlement From Msisdn:" + channelTransferVO.getFromUserCode()+ "]");
		            strBuff.append(" [ Settlement SOS Amount:" + PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity())+ "]");
		            if(!BTSLUtil.isNullString(response))
		            {
		            	  int index = response.indexOf("<STATUS>");
		                  final String status = response.substring(index + "<STATUS>".length(), response.indexOf("</STATUS>", index));
		               strBuff.append(" [ Settlement Response Status :" + status + "]");
		               
		                index = response.indexOf("<MESSAGE>");
		                  final String message = response.substring(index + "<MESSAGE>".length(), response.indexOf("</MESSAGE>", index));
		               strBuff.append(" [ Settlement Response message :" + message + "]");
		            }
		            
		           log.info("", strBuff.toString());
		    } catch (Exception e) {
		            log.errorTrace(METHOD_NAME, e);
		            log.error("log",  " Not able to log info, getting Exception :" + e);
		            
		        }
		    }
	
}
