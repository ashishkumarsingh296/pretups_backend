package com.btsl.pretups.logging;

import java.util.ArrayList;

/*
 * @(#)MultiCurrencyLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Zeeshan Aleem 03/12/2016 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the MultiCurrency log
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;

public class MultiCurrencyLog {
    private static Log _log = LogFactory.getFactory().getInstance(MultiCurrencyLog.class.getName());

    private MultiCurrencyLog() {
		// TODO Auto-generated constructor stub
	}
    /**
     * Method to log the info in MultiCurrency log
     * 
     * @param p_transferID
     * @param p_referenceID
     * @param p_msisdn
     * @param p_network
     * @param p_time
     * @param p_reqType
     * @param p_MultiCurrencyStage
     * @param p_message
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(ArrayList currencyData) {
    	final String METHOD_NAME = "log";
    	CurrencyConversionVO  currencyConversionVO=null;
    	try {
    		StringBuffer strBuff = new StringBuffer();
    		int currencyDatas=currencyData.size();
    		for(int count=0;count<currencyDatas;count++){
    			currencyConversionVO = (CurrencyConversionVO)currencyData.get(count);
    			strBuff.append("[SOURCE_CURRENCY_CODE:" + currencyConversionVO.getSourceCurrencyCode() + "]");            
    			strBuff.append("[TARGET_CURRENCY_CODE:" + currencyConversionVO.getTargetCurrencyCode() + "]");
    			strBuff.append("[CONVERSION:" + currencyConversionVO.getDisplayAmount() + "]\n");            	
    		}

    		_log.info("", strBuff.toString());
    	} catch (Exception e) {
    		_log.errorTrace(METHOD_NAME, e);   
    		_log.error("log", "", " Not able to log info, getting Exception :" + e.getMessage());
    	}
    }
    
    /**
     * Method to log the info in MultiCurrency log
     * 
     * @param p_transferID
     * @param p_referenceID
     * @param p_msisdn
     * @param p_network
     * @param p_time
     * @param p_reqType
     * @param p_MultiCurrencyStage
     * @param p_message
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(String thirdPartyData) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[SOURCE_CURRENCY_CODE:" + thirdPartyData.split(":")[0] + "]");            
            strBuff.append("[TARGET_CURRENCY_CODE:" + thirdPartyData.split(":")[1] + "]");
            strBuff.append("[CONVERSION:" + thirdPartyData.split(":")[2] + "]");
            
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log","Third Party Data", " Not able to log info, getting Exception :" + e.getMessage());
        }
    }
    
    public static Log getLogger() {
        return _log;
    }
    
    /**
     * Method to log the info in MultiCurrency log
     * 
     * @param p_transferID
     * @param p_referenceID
     * @param p_msisdn
     * @param p_network
     * @param p_time
     * @param p_reqType
     * @param p_MultiCurrencyStage
     * @param p_message
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(String stage,ArrayList currencyData) {
    	final String METHOD_NAME = "log";
    	CurrencyConversionVO  currencyConversionVO=null;
    	try {
    		StringBuffer strBuff = new StringBuffer();
    		for(int count=0;count<currencyData.size();count++){
    			String record=(String)currencyData.get(count);
    			strBuff.append("[SOURCE_CURRENCY_CODE:" + record.split(":")[0] + "]");            
    			strBuff.append("[TARGET_CURRENCY_CODE:" + record.split(":")[1] + "]");
    			strBuff.append("[CONVERSION:" + record.split(":")[2] + "]\n");            	
    		}

    		_log.info("", strBuff.toString());
    	} catch (Exception e) {
    		_log.errorTrace(METHOD_NAME, e);   
    		_log.error("log", stage, " Not able to log info, getting Exception :" + e.getMessage());
    	}
    }
}

