package com.btsl.pretups.util.clientutils;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;



public class VHAUtil extends OperatorUtil {

	private static final Log _log = LogFactory.getLog(OperatorUtil.class.getName()); //made log final
	
	 @Override
	    public String formatVoucherTransferID(TransferVO p_transferVO, long p_tempTransferID) {
	    	final String methodName = "class = VHAUtil: method= formatVoucherTransferID";
	    	String returnStr = null;
	    	try {
	    		
	    		 if (_log.isDebugEnabled()) {
	    	            _log.debug(methodName, "Entered  : ");
	    	        }
	    		// ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO()
	    		// String currentYear=BTSLUtil.getFinancialYearLastDigits(2)
	    		final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), P2P_TRANSFER_ID_PAD_LENGTH);
	    		// returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr
	    		// returnStr="C"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+paddedTransferIDStr
	    		returnStr = "V" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
	    				.getProperty("INSTANCE_ID") + paddedTransferIDStr;
	    		p_transferVO.setTransferID(returnStr);
	    	} catch (Exception e) {
	    		_log.errorTrace(methodName, e);
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[]", "", "", "",
	    				"Not able to generate Transfer ID:" + e.getMessage());
	    		returnStr = null;
	    	}
	    	
	    	 if (_log.isDebugEnabled()) {
	             _log.debug(methodName, "exited  : ");
	         }
	    	return returnStr;

	    }
	

}
