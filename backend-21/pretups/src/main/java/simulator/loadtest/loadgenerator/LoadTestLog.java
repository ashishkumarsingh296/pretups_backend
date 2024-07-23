package simulator.loadtest.loadgenerator;

/*@(#)LoadTestLog.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Chetan Prakash Kothari             18/08/2008         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2008 Bharti Telesoft Ltd.
 * Class for logging all the channel request
 */

import simulator.loadtest.loadgenerator.logging.Log;
import simulator.loadtest.loadgenerator.logging.LogFactory;

public class LoadTestLog {
	private static Log _log = LogFactory.getLog(LoadTestLog.class.getName());
	
	/**
	 * Method that prepares the the string to be written in log file
	 * @param p_requestVO
	 * @return
	 */
	private static String generateMessageString(LoadRequestVO p_requestVO)
	{
		StringBuffer strBuff = new StringBuffer();
		try
		{
			strBuff.append("[Request Start Time :"+p_requestVO.getRequestStartTime()+"]");
			strBuff.append("[Request End Time :"+p_requestVO.getRequestEndTime()+"]");
			strBuff.append("[Total Time :"+(p_requestVO.getRequestEndTime() - p_requestVO.getRequestStartTime()  )+"]");
			strBuff.append("[Mobile Number :"+p_requestVO.getMsisdn()+"]");
			strBuff.append("[Request Transaction ID :"+p_requestVO.getRequestTransactionId()+"]");
			strBuff.append("[Response Transaction ID :"+p_requestVO.getResponseTransactionId()+"]");
			strBuff.append("[Request Type :"+p_requestVO.getRequestType()+"]");
			strBuff.append("[Response Status :"+p_requestVO.getRequestStatus()+"]");
			strBuff.append("[Node Name :"+p_requestVO.getNodeName()+"]");
			strBuff.append("[Http Status :"+p_requestVO.getHttpStatus()+"]");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("generateMessageString",e.getMessage());
		}
		return strBuff.toString();
	}//end of generateMessageString

	/**
	 * Method to log the details in Request Log
	 * @param p_requestVO
	 */
	public static void log(LoadRequestVO p_requestVO)
	{
		_log.info("",generateMessageString(p_requestVO));
	}//end of log
	
}

