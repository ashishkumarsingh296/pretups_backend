package simulator.loadtest.loadgenerator;
/**
 * @(#)DetailLog.java
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


public class DetailLog {
	
	private static Log _log = LogFactory.getLog(DetailLog.class.getName());
	
	public static void info(String className,String message)
	{
		_log.info(className,message);
	}//end of log

	public static void debug(String className,String message)
	{
		_log.debug(className,message);
	}//end of log
	public static void error(String className,String message)
	{
		_log.error(className,message);
	}//end of log
}
