package com.btsl.logging;

public class DBConActivityLog {
	private static Log _log = LogFactory.getFactory().getInstance(DBConActivityLog.class.getName());

	/**
	 * ensures no instantiation
	 */
	private DBConActivityLog(){
		
	}
	
    public static void log(String str) {
        final String METHOD_NAME = "log";
        StringBuffer strBuff = new StringBuffer();
        try {
            strBuff.append(" Exception : Dirty Connection found in - ");
            strBuff.append(" [ClassName#MethodName:" + str + "]");
             _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", " Exception :" + e.getMessage());
        }
    }
}
