package com.btsl.alm;

/**
 * Class for handling configuration files
 * Date: April 1st, 2004
 * Author: Lovie Minhas
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author ayush.abhijeet
 */
public class GuiConfHandler {
    private static Log _log = LogFactory.getLog(GuiConfHandler.class.getName());

    /**
	 * ensures no instantiation
	 */
    private GuiConfHandler(){
    	
    }
    /**
     * @param strConfigType
     * @return
     */
    public static synchronized Vector getAllConfigParams(String strConfigType) {
        Vector oValues = new Vector(50, 50);
        String strValue = null;
        File propsFile = new File(strConfigType);
        java.util.Properties props = new java.util.Properties();
        FileInputStream fileInputStream = null;
        final String methodName = "getAllConfigParams";

        try {
            fileInputStream = new FileInputStream(propsFile);
            props.load(fileInputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            _log.errorTrace(methodName, fileNotFoundException);
        } catch (IOException ioException) {
            _log.errorTrace(methodName, ioException);
        } finally {
            try {
            	if(fileInputStream!=null)
            	{
                fileInputStream.close();
            	}
            } catch (IOException ioException) {
                _log.errorTrace(methodName, ioException);
            }
        }

        Enumeration enumeration = props.propertyNames();
        while (enumeration.hasMoreElements()) {
            strValue = (String) enumeration.nextElement();
            oValues.add(strValue);
        }
        return oValues;
    }

    /**
     * @param strConfigType
     * @param strKey
     * @return
     */
    public static synchronized String getConfigParam(String strConfigType, String strKey) {
        String strValue ;
        java.util.Properties props = new java.util.Properties();
        GuiConfHandler.getAllConfigParams(strConfigType);
        strValue = (String) props.get(strKey);
        return strValue;
    }

    /**
     * @param strConfigType
     * @return
     */
    public static synchronized boolean checkConfFile(String strConfigType) {
        final String methodName = "checkConfFile";
        FileInputStream fileInputStream = null;
        try {
            String strValue = null;
            File propsFile = new File(strConfigType);
            java.util.Properties props = new java.util.Properties();
            fileInputStream = new FileInputStream(propsFile);
            props.load(fileInputStream);
            strValue = (String) props.get("ALARM_PATH");
            return gettingTempPathandSendAlarmPath(strValue, props);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
        }
    }

	private static boolean gettingTempPathandSendAlarmPath(String strValue,
			java.util.Properties props) {
		if (strValue == null)
		    return false;
		strValue = (String) props.get("TEMP_PATH");
		if (strValue == null)
		    return false;
		strValue = (String) props.get("SEND_ALARM_PATH");
		if (strValue == null)
		    return false;
		return true;
	}
}
