package com.restapi.superadminVO;

import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * @(#)AdminOperationLog.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Mohit Goel 26/08/2005 Initial Creation
 * 
 *                            This class is used to log the User and Operation
 *                            Information
 *                            while any operation performed on
 *                            Service keyword
 *                            msisdn prefix
 *                            STK service add
 *                            STK service modification
 *                            STK service category association
 *                            Interface Management
 *                            Add SubLookUp
 *                            Network Management
 *                            Modify SubLookUp
 *                            Network Status
 *                            Grade Management
 *                            Service Class Management
 *                            Division Management
 *                            Department Management
 *                            Group Role Management
 *                            System Preferences
 *                            Domain Management
 *                            Category Management
 *                            Add Gateway
 *                            Modify Message Gateway
 *                            Modify Gateway Mapping
 *                            User Password Mgmt
 *                            PIN Manage Management
 */

public class AdminOperationLog {
		/**
		 * ensures no instantiation
		 */
		private AdminOperationLog(){
			
		}
		
	    public static void log(AdminOperationVO p_adminOperationVO) {
	        final Log _log = LogFactory.getFactory().getInstance(AdminOperationLog.class.getName());
	        final String METHOD_NAME = "log";
	        StringBuffer strBuff = new StringBuffer();
	        try {
	            strBuff.append(" [Source :" + BTSLUtil.NullToString(p_adminOperationVO.getSource()) + "]");
	            strBuff.append(" [Operation:" + BTSLUtil.NullToString(p_adminOperationVO.getOperation()) + "]");
	            if (p_adminOperationVO.getDate() != null) {
	                strBuff.append(" [Date & Time:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_adminOperationVO.getDate()) + "]");
	            } else {
	                strBuff.append(" [Date & Time:" + BTSLDateUtil.getLocaleDateTimeFromDate(new Date()) + "]");
	            }
	            strBuff.append(" [Network Code:" + BTSLUtil.NullToString(p_adminOperationVO.getNetworkCode()) + "]");
	            strBuff.append(" [Category Code:" + BTSLUtil.NullToString(p_adminOperationVO.getCategoryCode()) + "]");
	            strBuff.append(" [Login ID:" + BTSLUtil.NullToString(p_adminOperationVO.getLoginID()) + "]");
	            strBuff.append(" [User ID:" + BTSLUtil.NullToString(p_adminOperationVO.getUserID()) + "]");
	            strBuff.append(" [Mobile No:" + BTSLUtil.NullToString(p_adminOperationVO.getMsisdn()) + "]");
	            strBuff.append(" [Info:" + BTSLUtil.NullToString(p_adminOperationVO.getInfo()) + "]");

	            _log.info("", strBuff.toString());
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("log", p_adminOperationVO.getNetworkCode(), " Exception :" + e.getMessage());
	        }
	    }


}
