package com.btsl.login;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * @(#)LoginLogger.java
 *                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Mohit Goel 26/08/2005 Initial Creation
 * 
 *                      This class is used to log the user details during login,
 *                      logout and when user session expired.
 * 
 */
public class LoginLogger {
	
	/**
	 * ensures no instantiation
	 */
	private LoginLogger(){
		
	}
	
	
    public static void log(LoginLoggerVO p_loginLogerVO) {
        final Log _log = LogFactory.getFactory().getInstance(LoginLogger.class.getName());
        final String METHOD_NAME = "log";
        StringBuffer strBuff = new StringBuffer();
        try {
            strBuff.append(" [Login ID:" + BTSLUtil.maskParam(BTSLUtil.NullToString(p_loginLogerVO.getLoginID())) + "]");
            strBuff.append(" [User ID:" + BTSLUtil.NullToString(p_loginLogerVO.getUserID()) + "]");
            strBuff.append(" [Network ID:" + BTSLUtil.NullToString(p_loginLogerVO.getNetworkID()) + "]");
            // strBuff.append(" [Network Name:"+BTSLUtil.NullToString(p_loginLogerVO.getNetworkName())
            // +"]");
            strBuff.append(" [User Name:" + BTSLUtil.NullToString(p_loginLogerVO.getUserName()) + "]");
            strBuff.append(" [User Type:" + BTSLUtil.NullToString(p_loginLogerVO.getUserType()) + "]");
            strBuff.append(" [Domain ID:" + BTSLUtil.NullToString(p_loginLogerVO.getDomainID()) + "]");
            strBuff.append(" [Category Code:" + BTSLUtil.NullToString(p_loginLogerVO.getCategoryCode()) + "]");
            strBuff.append(" [Log Type:" + BTSLUtil.NullToString(p_loginLogerVO.getLogType()) + "]");
            if (p_loginLogerVO.getLoginTime() != null)
                strBuff.append(" [Login Time:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_loginLogerVO.getLoginTime()) + "]");
            else
                strBuff.append(" [Login Time: ]");
            if (p_loginLogerVO.getLogoutTime() != null)
                strBuff.append(" [Logout Time:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_loginLogerVO.getLogoutTime()) + "]");
            else
                strBuff.append(" [Logout Time: ]");
            strBuff.append(" [IP Address:" + BTSLUtil.NullToString(p_loginLogerVO.getIpAddress()) + "]");
            strBuff.append(" [Browser Type:" + BTSLUtil.NullToString(p_loginLogerVO.getBrowser()) + "]");
            strBuff.append(" [Other Information:" + BTSLUtil.NullToString(p_loginLogerVO.getOtherInformation()) + "]");

            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", " Exception :" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LoginLogger[log]",p_loginLogerVO.getLastTransferID(),"","","Not able to log info for Transfer ID:"+p_loginLogerVO.getLastTransferID()+" and User ID:"+p_loginLogerVO.getUserID()+" ,getting Exception="+e.getMessage());
        }
    }
}
