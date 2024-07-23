package com.btsl.xl;

/*
 * ExcelFileConstants.java
 * 
 * 
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit 28/06/2006 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.SqlParameterEncoder;

public class ExcelFileConstants implements java.io.Serializable {
	private static Log _log = LogFactory.getLog(ExcelFileConstants.class.getName());
    private static Properties properties = new Properties(); // to keep the value
                                                            // of propertie

    public static void load(String fileName) throws IOException {
    	FileInputStream fileInputStream = null;
    	try{
    		File file = new File(fileName);
    		fileInputStream = new FileInputStream(file);
    		properties.load(fileInputStream);
    	}
    	
    	finally
    	{
    		try{
        		if(fileInputStream != null){
        			fileInputStream.close();	
        		}
        	}catch(Exception e){
        		_log.errorTrace("ExcelFileConstants:load()", e);
       		}
    	}
        
    }// end of load

    public static String getWriteProperty(String excelID, String propertyName) {
        if (excelID == null)
            return properties.getProperty(propertyName);
        else
            return properties.getProperty(excelID + "_W_" + propertyName);
    }// end of getProperty

    public static String getReadProperty(String excelID, String propertyName) {
        if (excelID == null)
            return SqlParameterEncoder.encodeParams(properties.getProperty(propertyName));
        else
            return SqlParameterEncoder.encodeParams(properties.getProperty(excelID + "_R_" + propertyName));
    }// end of getProperty
}// end of Constants

