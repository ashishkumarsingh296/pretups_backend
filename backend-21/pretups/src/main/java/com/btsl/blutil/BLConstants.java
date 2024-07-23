package com.btsl.blutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class BLConstants {  
	private static Log _log = LogFactory.getLog(BLConstants.class.getName());
	public static final String BL_PRODUCER = "BL_PRODUCER";
	
	public static final  Properties properties = new Properties(); 
	public static final  Properties defaultProperties = new Properties(); 

	//public static final String ExtAPIStringParserI = "ExtAPIStringParserI";
	public static final String Client_ExtAPIXMLS_tringParser_Obj = "ClientExtAPIXMLStringParser_obj";
	public static final String Client_ChannelTransferDAO_Obj = "ChannelTransferDAO_obj";
	public static final String Client_C2SRechargeBL_Obj = "C2SRechargeBL_obj";
	
	private BLConstants() {
	}
	
    public static void load(String fileName) throws IOException {
    	FileInputStream fileInputStream = null;
    	try{
    		final File file = new File(fileName);
    		fileInputStream = new FileInputStream(file);
    		properties.load(fileInputStream);
    	}
        finally{
        	if(fileInputStream != null)
        	{
        		try{
        			if(fileInputStream != null){
        				fileInputStream.close();	
        			}
        		}catch(Exception e){
        		 _log.errorTrace("BLConstants:load()", e);
        		}
        	}
        }
    }
    
    public static void loadDefault(String fileName) throws IOException {
    	FileInputStream fileInputStream = null;
    	try{
    		final File file = new File(fileName);
    		fileInputStream = new FileInputStream(file);
    		defaultProperties.load(fileInputStream);
    	}
        finally{
        	if(fileInputStream != null)
        	{
        		try{
        			if(fileInputStream != null){
        				fileInputStream.close();	
        			}
        		}catch(Exception e){
        		 _log.errorTrace("BLConstants:loadDefault()", e);
        		}
        	}
        }
    }
    
    public static String getDefaultBLName(String propertyName) {
    	String value = "";
    	if(!BTSLUtil.isNullString(properties.getProperty(propertyName)))
    		value =  SqlParameterEncoder.encodeParams(defaultProperties.getProperty(propertyName)).trim();
    	return value;
    }

    public static String getBLName(String propertyName) {
    	String value = "";
    	if(!BTSLUtil.isNullString(properties.getProperty(propertyName)))
    		value =  SqlParameterEncoder.encodeParams(properties.getProperty(propertyName)).trim();
    	return value;
    }
}
