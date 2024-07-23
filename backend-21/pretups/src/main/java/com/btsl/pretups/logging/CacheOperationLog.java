package com.btsl.pretups.logging;

import java.util.List;
import java.util.Map;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CacheOperationLog {
    private static Log _log = LogFactory.getFactory().getInstance(CacheOperationLog.class.getName());

    /**
     * ensusres no instantiation
     */
    private CacheOperationLog(){
    	
    }
    
    public static void log(String p_className, String p_logMessage) {
        _log.info(p_className, p_logMessage);
    }
    public static void log(String p_className, Map p_logMessage) {
        _log.info(p_className, p_logMessage);
    }
    public static void log(String p_className, List p_logMessage) {
        _log.info(p_className, p_logMessage);
    }
}
