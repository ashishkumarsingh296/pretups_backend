package com.btsl.pretups.channel.logging;

/*
 * @(#)DiffCreditLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the Messages that are sent from the system
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;

public class DiffCreditLog {
    private static Log _log = LogFactory.getFactory().getInstance(DiffCreditLog.class.getName());

    /**
	 * ensures no instantiation
	 */
    private DiffCreditLog(){
    	
    }
    public static void log(C2STransferVO p_c2sTransferVO, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            final StringBuilder strBuild = new StringBuilder();
            strBuild.append(" [Transfer ID:");
            strBuild.append(p_c2sTransferVO.getTransferID());
            strBuild.append("]");
            strBuild.append(" [MSISDN:");
            strBuild.append(p_c2sTransferVO.getSenderMsisdn());
            strBuild.append("]");
            strBuild.append(" [Network:");
            strBuild.append(p_c2sTransferVO.getSenderNetworkCode());
            strBuild.append("]");
            strBuild.append(" [User ID:");
            strBuild.append(p_c2sTransferVO.getSenderID());
            strBuild.append("]");
            strBuild.append(" [Diff Applicable:");
            strBuild.append(p_c2sTransferVO.getDifferentialApplicable());
            strBuild.append("]");
            strBuild.append(" [Diff Given:");
            strBuild.append(p_c2sTransferVO.getDifferentialGiven());
            strBuild.append("]");
            strBuild.append(" [Other Info:");
            strBuild.append(p_otherInfo);
            strBuild.append("]");
            _log.info("", strBuild.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "", " Not able to log info in Diff Credit Log, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCreditLog[log]", p_c2sTransferVO.getTransferID(),
                p_c2sTransferVO.getSenderMsisdn(), "",
                "Not able to log info in Diff credit Log for Transfer ID:" + p_c2sTransferVO.getTransferID() + " ,getting Exception=" + e.getMessage());
        }
    }
	public static void bonusLog(C2STransferVO p_c2sTransferVO,String p_otherInfo)
	{
		final String METHOD_NAME = "bonusLog";
		try
		{
			StringBuilder strBuild = new StringBuilder();
			strBuild.append(" [Transfer ID:");
			strBuild.append(p_c2sTransferVO.getTransferID() );
			strBuild.append("]");
			strBuild.append(" [MSISDN:");
			strBuild.append(p_c2sTransferVO.getSenderMsisdn()); 
			strBuild.append("]");
			strBuild.append(" [Network:");
			strBuild.append(p_c2sTransferVO.getSenderNetworkCode() );
			strBuild.append("]");
			strBuild.append(" [User ID:");
			strBuild.append(p_c2sTransferVO.getSenderID() );
			strBuild.append("]");
			strBuild.append(" [Bonus Applicable:");
			strBuild.append(p_c2sTransferVO.getCommissionApplicable() );
			strBuild.append("]");
			strBuild.append(" [Bonus Given:");
			strBuild.append(p_c2sTransferVO.getCommissionGiven()); 
			strBuild.append("]");
			strBuild.append(" [Other Info:");
			strBuild.append(p_otherInfo );
			strBuild.append("]");
			_log.info("",strBuild.toString());
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			_log.error("log",""," Not able to log info in Diff Credit Log, getting Exception :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DiffCreditLog[log]",p_c2sTransferVO.getTransferID(),p_c2sTransferVO.getSenderMsisdn(),"","Not able to log info in Diff credit Log for Transfer ID:"+p_c2sTransferVO.getTransferID()+" ,getting Exception="+e.getMessage());
		}
	}
}
