package com.inter.clarocol.cs5;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ResponseCode100Logger {

	
	private static Log LOG = LogFactory.getFactory().getInstance(ResponseCode100Logger.class.getName());




	public static void logInfo(String message)
	{
		final  String METHOD_NAME = "log";
		try
		{
			//if(LOG.isDebugEnabled()){
				LOG.info("",message);
			//}
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
