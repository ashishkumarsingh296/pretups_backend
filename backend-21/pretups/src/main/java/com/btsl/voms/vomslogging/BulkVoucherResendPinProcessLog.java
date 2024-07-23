package com.btsl.voms.vomslogging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/** @# BulkVoucherResendPinProcessLog.java
 *
 *		Created by         Created on 		History
 *	--------------------------------------------------------------------------------
 * 		Hargovind karki   12/01/2017		 Initial Creation
 *	--------------------------------------------------------------------------------
 * Copyright(c) 2010, Comviva Technologies Ltd. 
 */
public class BulkVoucherResendPinProcessLog
{
	private static Log _Filelogger = LogFactory.getLog(BulkVoucherResendPinProcessLog.class.getName());
	/**
	 * ensures no instantiation
	 */
	private BulkVoucherResendPinProcessLog()
	{
		
	}
	public static void log(String p_message,String p_msisdn)
	{
		StringBuffer message=new StringBuffer();
		message.append("[MESSAGE = "+BTSLUtil.NullToString(p_message)+"] ");
		message.append("[TRANSFER_ID = "+BTSLUtil.NullToString(p_msisdn)+"] ");
		_Filelogger.info(" ",message);
	}
	public static void log(String p_message)
	{
		StringBuffer message=new StringBuffer();
		message.append("[MESSAGE = "+BTSLUtil.NullToString(p_message)+"] ");
		_Filelogger.info(" ",message);
	}
}
