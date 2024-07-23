package com.btsl.cp2p.logging;

import com.btsl.cp2p.login.businesslogic.CP2PLoginLoggerVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
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
public class CP2PLoginLogger {
	
	/**
	 * ensures no instantiation
	 */
	private CP2PLoginLogger(){
		
	}

    public static void log(CP2PLoginLoggerVO p_cp2ploginLogerVO) {
        final Log log = LogFactory.getFactory().getInstance(CP2PLoginLogger.class.getName());
        final String METHOD_NAME = "log";
        StringBuffer strBuff = new StringBuffer();
        try {
            strBuff.append(" [Login ID:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getLoginID()) + "]");
            strBuff.append(" [User ID:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getUserID()) + "]");
            strBuff.append(" [Network ID:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getNetworkID()) + "]");
            // strBuff.append(" [Network Name:"+BTSLUtil.NullToString(p_loginLogerVO.getNetworkName())
            // +"]");
            strBuff.append(" [User Name:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getUserName()) + "]");
            strBuff.append(" [User Type:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getUserType()) + "]");
            strBuff.append(" [Domain ID:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getDomainID()) + "]");
            strBuff.append(" [Category Code:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getCategoryCode()) + "]");
            strBuff.append(" [Log Type:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getLogType()) + "]");
            if (p_cp2ploginLogerVO.getLoginTime() != null)
                strBuff.append(" [Login Time:" + BTSLUtil.getDateTimeStringFromDate(p_cp2ploginLogerVO.getLoginTime()) + "]");
            else
                strBuff.append(" [Login Time: ]");
            if (p_cp2ploginLogerVO.getLogoutTime() != null)
                strBuff.append(" [Logout Time:" + BTSLUtil.getDateTimeStringFromDate(p_cp2ploginLogerVO.getLogoutTime()) + "]");
            else
                strBuff.append(" [Logout Time: ]");
            strBuff.append(" [IP Address:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getIpAddress()) + "]");
            strBuff.append(" [Browser Type:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getBrowser()) + "]");
            strBuff.append(" [Other Information:" + BTSLUtil.NullToString(p_cp2ploginLogerVO.getOtherInformation()) + "]");

            log.info("", strBuff.toString());
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("log", " Exception :" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LoginLogger[log]",p_loginLogerVO.getLastTransferID(),"","","Not able to log info for Transfer ID:"+p_loginLogerVO.getLastTransferID()+" and User ID:"+p_loginLogerVO.getUserID()+" ,getting Exception="+e.getMessage());
        }
    }
}
