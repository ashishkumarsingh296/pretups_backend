package com.btsl.pretups.logging;

/*
 * @(#)ChannelUserTxnMapCityLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Vipan Kumar 27/03/2014 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the transaction log
 */

import org.apache.log4j.Logger;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;

public class ChannelUserTxnLog {
    private static Logger _logC2S = Logger.getLogger(ChannelUserTxnLog.class.getName());
    private static Log _log = LogFactory.getLog(ChannelUserTxnLog.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTxnLog[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * ensures no instantiation
     */
    private ChannelUserTxnLog(){
    	
    }
    public static void log(C2STransferVO p_c2sTransferVO, TransferItemVO p_senderTransferItemVO, TransferItemVO p_receiverTransferItemVO) {
        final String METHOD_NAME = "log";
        try {
            String usrTdr = _operatorUtil.c2sTransferTDRLog(p_c2sTransferVO, p_senderTransferItemVO, p_receiverTransferItemVO);
            if(!BTSLUtil.isNullString(usrTdr)){
            	if(_logC2S.isDebugEnabled()){
            		_logC2S.debug(usrTdr);
            	}
            }

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserTxnLog[log]", p_c2sTransferVO.getTransferID(), "", "", "Not able to log info for ChannelUserTxnLog:" + p_c2sTransferVO.getTransferID() + " ,getting Exception=" + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return _logC2S;
    }
}
