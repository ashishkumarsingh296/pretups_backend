package com.btsl.logging;

public class DBConenctionPoolLog {
	private static Log _log = LogFactory.getFactory().getInstance(DBConenctionPoolLog.class.getName());
	private DBConenctionPoolLog(){
	}
	
    public static void log(int str1,int str2) {
        final String METHOD_NAME = "log";
        StringBuffer strBuff = new StringBuffer();
        try {
            strBuff.append(" BorrowedConnectionsCount: ");
            strBuff.append(str1);
            strBuff.append(" ,AvailableConnectionsCount: ");
            strBuff.append(str2);
             _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", " Exception :" + e.getMessage());
        }
    }
}
