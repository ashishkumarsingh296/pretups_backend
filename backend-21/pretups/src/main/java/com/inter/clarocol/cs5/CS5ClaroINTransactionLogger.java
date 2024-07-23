package com.inter.clarocol.cs5;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author vipan.kumar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CS5ClaroINTransactionLogger {
    
	private static Log LOG = LogFactory.getFactory().getInstance(CS5ClaroINTransactionLogger.class.getName());




	public static void logInfo(String message)
	{
		final  String METHOD_NAME = "log";
		try
		{
			if(LOG.isDebugEnabled()){
				LOG.debug("",message);
			}
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
