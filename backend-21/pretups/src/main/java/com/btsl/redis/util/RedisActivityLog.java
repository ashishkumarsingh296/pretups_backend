package com.btsl.redis.util;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class RedisActivityLog {
	 private static Log _log = LogFactory.getFactory().getInstance(RedisActivityLog.class.getName());
	    private RedisActivityLog(){
	    }
	    
	    public static void log(String str) {
	        final String METHOD_NAME = "log";
	        StringBuffer strBuff = new StringBuffer();
	        try {
	            strBuff.append(str);
	             _log.info("", strBuff.toString());
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("log", " Exception :" + e.getMessage());
	        }
	    }

}
