
/*@(#)AmbiguousServerUpdateProcesslog.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Mahindra Comviva              17/03/2015         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2015 Mahindra Comviva Pvt Ltd.
 * Class for logging all the AmbiguousServerUpdateProcess log
 */
package com.btsl.pretups.logging;

import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
public class AmbiguousServerC2SUpdateProcesslog {
	
	
private static Log _Filelogger = LogFactory.getLog(AmbiguousServerC2SUpdateProcesslog.class.getName());
	private AmbiguousServerC2SUpdateProcesslog() {
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

