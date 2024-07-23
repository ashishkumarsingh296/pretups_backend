package com.inter.gp.cs5;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author vipan.kumar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CS5GpINTransactionLogger {
    
	private static Log LOG = LogFactory.getFactory().getInstance(CS5GpINTransactionLogger.class.getName());


	private CS5GpINTransactionLogger() {
	  }


	public static void logInfo(String message)
	{
		final  String METHOD_NAME = "log";
		try
		{
			LogFactory.printLog(METHOD_NAME, message, LOG);
		}
		catch(Exception e)
		{
			LOG.errorTrace(METHOD_NAME,e);
		}
	}

	public static void errorlog(String message)
	{
		final String METHOD_NAME = "errorlog";
		try
		{
		
			LOG.error("",message);
		}
		catch(Exception e)
		{
			LOG.errorTrace(METHOD_NAME,e);
		}
	}



}
