package simulator.loadtest.loadgenerator;

/*@(#)LoadTestMemoryLog.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Chetan Prakash Kothari             18/08/2008         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2008 Bharti Telesoft Ltd.
 * Class for logging all the channel request
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class LoadTestMemoryLog {
	private static Log _log = LogFactory.getLog(LoadTestMemoryLog.class.getName());
	
	/**
	 * Method that prepares the the string to be written in log file
	 * @param p_requestVO
	 * @return
	 */
	private static String generateMessageString(double p_totalMemory,double p_freeMemory)
	{
		StringBuffer strBuff = new StringBuffer();
		try
		{
			strBuff.append("[Total Memory : "+p_totalMemory+ " mb]");
			strBuff.append("[Free Memory : "+p_freeMemory+" mb]");
			strBuff.append("[Used Memory : "+(p_totalMemory-p_freeMemory)+" mb]");
			
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
	public static void log(double p_totalMemory,double p_freeMemory)
	{
		_log.info("",generateMessageString(p_totalMemory,p_freeMemory));
	}//end of log
	
}

