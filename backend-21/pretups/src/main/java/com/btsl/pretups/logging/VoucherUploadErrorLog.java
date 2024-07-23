package com.btsl.pretups.logging;

import java.text.ParseException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;

public class VoucherUploadErrorLog {


	/*
	 * @#DirectPayOutErrorLog.java
	 * Name Date History
	 * ------------------------------------------------------------------------
	 * Manisha Jain 26/10/09 Initial Creation
	 * ------------------------------------------------------------------------
	 * Copyright (c) 2009 Comviva Technologies Ltd.
	 */



	    private static Log _Filelogger = LogFactory.getLog(VoucherUploadErrorLog.class.getName());

	    /**
		 * ensures no instantiation
		 */
	    private VoucherUploadErrorLog() {
	        
	    }

	    
	    /**
	     * log
	     * 
	     * @param p_serialNo
	     * @param p_fileName
	     * @param p_message
	     * @throws ParseException 
	     */
	    public static void logGenMsg(String p_fileName, String p_message){
	        StringBuffer str = new StringBuffer();
	        Date currentDate = new Date();
	        str.append("[FILE NAME: " + p_fileName + "] ");
	        str.append("[MESSAGE: " + p_message + "] ");
	        str.append("[DATE: " + BTSLDateUtil.getSystemLocaleDateTime() + "] ");
	        _Filelogger.info(" ", str.toString());
	    }
	


}
