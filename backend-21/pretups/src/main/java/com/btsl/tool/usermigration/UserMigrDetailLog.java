package com.btsl.tool.usermigration;

/**
 * @(#)UserMigrDetailLog.java
 *                            Copyright(c) 2010, Comviva Technologies Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Vinay Singh June 05,2010 Initial Creation
 *                            --------------------------------------------------
 *                            ----------------------------------------------
 */
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class UserMigrDetailLog {
    private static Log _log = LogFactory.getFactory().getInstance(UserMigrDetailLog.class.getName());
    
    /**
	 * to ensure no class instantiation 
	 */
    private UserMigrDetailLog() {
        
    }

    /**
     * @param String
     *            p_lineNo
     * @param String
     *            p_msisdn
     * @param String
     *            p_oldUserID
     * @param String
     *            p_newUserID
     * @param String
     *            p_toGeoDomCode
     * @param String
     *            p_fromGeoDomCode
     */
    public static void log(String p_info1, String p_info2, String p_info3) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(p_info1 + " " + p_info2 + " " + p_info3);

            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * @param String
     *            p_lineNo
     * @param String
     *            p_msisdn
     * @param String
     *            p_oldUserID
     * @param String
     *            p_newUserID
     * @param String
     *            p_toGeoDomCode
     * @param String
     *            p_fromGeoDomCode
     */
    public static void log(int p_lineNo, String p_msisdn, String p_oldUserID, String p_newUserID, String p_toGeoDomCode, String p_fromGeoDomCode, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[LINE_NO: " + p_lineNo + "]");
            strBuff.append("[MSISDN: " + p_msisdn + "]");
            strBuff.append("[OLD_USER_ID: " + p_oldUserID + "]");
            strBuff.append("[OLD_GEODOMAINCODE: " + p_fromGeoDomCode + "]");
            strBuff.append("[NEW_USER_ID: " + p_newUserID + "]");
            strBuff.append("[NEW_GEODOMAINCODE: " + p_toGeoDomCode + "]");
            strBuff.append("[MIGRaTION_STATUS: " + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_lineNo, " Not able to log info, getting Exception :" + e.getMessage());
        }
    }
}
