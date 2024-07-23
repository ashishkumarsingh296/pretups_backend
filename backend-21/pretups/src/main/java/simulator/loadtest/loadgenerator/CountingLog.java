package simulator.loadtest.loadgenerator;
/**
 * @(#)CountingLog.java
 * Copyright(c) 2008, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * <description>
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            		History
 *-------------------------------------------------------------------------------------------------
 * chetan.kothari             july 2,2008     	Initital Creation
 *-------------------------------------------------------------------------------------------------
 *
 */
import simulator.loadtest.loadgenerator.logging.Log;
import simulator.loadtest.loadgenerator.logging.LogFactory;

public class CountingLog {
	
	private static Log _log = LogFactory.getLog(CountingLog.class.getName());
	
	public static void log(String message)
	{
		_log.info("",message);
	}//end of log

}
