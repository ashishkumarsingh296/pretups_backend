/*@(#)HandleUnsettledP2PCasesLog.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Mahindra Comviva              17/03/2015         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2005 Mahindra Comviva Pvt Ltd.
 * Class for logging all the HandleUnsettledP2PCases log
 */

package com.btsl.pretups.p2p.logging;

import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public class HandleUnsettledP2PCasesLog {

	
	
	private static Log _Filelogger = LogFactory.getLog(HandleUnsettledP2PCasesLog.class.getName());
	
	private HandleUnsettledP2PCasesLog() {
		// TODO Auto-generated constructor stub
	}
	    
	    /**
		 * log
		 * @param p_msisdn
		 * @param p_batchName
		 * @param p_message
		 */
		 public static void successLog(String ClassName, String methodName, String List)
	    {
	        StringBuffer str= new StringBuffer();
	        Date currentDate=new Date(); 
	        str.append("ClassName: "+ClassName);
	        str.append("methodName: "+methodName);
	        str.append("AmbiguousList:"+List);
	        str.append("[DATE: "+currentDate+"] ");
	        _Filelogger.info(" ",str.toString());
	    }
		 
		 public static void log(String ClassName, String methodName, String arguments)
		    {
			   StringBuffer str= new StringBuffer();
		        Date currentDate=new Date(); 
		        str.append("ClassName: "+ClassName);
		        str.append("methodName: "+methodName);
		        str.append("AmbiguousList:"+arguments);
		        str.append("[DATE: "+currentDate+"] ");
		        _Filelogger.info(" ",str.toString());
		    }
	
	
}