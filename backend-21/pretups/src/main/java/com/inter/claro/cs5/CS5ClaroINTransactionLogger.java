package com.inter.claro.cs5;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public class CS5ClaroINTransactionLogger {
	
	private static final Log log = LogFactory.getLog(CS5ClaroINTransactionLogger.class);
	
	CS5ClaroINTransactionLogger() {
		/*
		 * 
		 * */
	}


	public static void logInfo(String message)
	{
		final  String methodName = "log";
		try
		{
			if(log.isDebugEnabled()){
				log.debug("",message);
			}
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
		}
	}

	public static void errorlog(String message)
	{
		final String methodName = "errorlog";
		try
		{
		
			log.error("",message);
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
		}
	}



}
