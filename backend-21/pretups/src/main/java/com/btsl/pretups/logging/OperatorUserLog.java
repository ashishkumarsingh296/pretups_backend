package com.btsl.pretups.logging;

/*
 * @(#)OperatorUserLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ved prakash Sharma 08/06/06 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Class for Channel user modify log
 */

import java.util.ArrayList;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;


public class OperatorUserLog {
    private static Log _log = LogFactory.getFactory().getInstance(OperatorUserLog.class.getName());

    /**
	 * ensures no instantiation
	 */
    private OperatorUserLog(){
    	
    }
    public static void log(String p_action, UserVO p_userVO, UserVO p_sessionUserVO, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[Action: " + p_action);
            strBuff.append(" # Modify By: " + p_sessionUserVO.getModifiedBy());
            strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_userVO.getModifiedOn()));
            strBuff.append(" # Modify By login id:" + p_sessionUserVO.getLoginID());
            if(p_sessionUserVO.getSessionInfoVO()!=null){
            	String modifyByIP = p_sessionUserVO.getSessionInfoVO().getRemoteAddr()==null?"":p_sessionUserVO.getSessionInfoVO().getRemoteAddr();
            	strBuff.append(" # Modify By IP:").append(modifyByIP).append( "]");
            }
//            strBuff.append(" # Modify By IP:" + p_sessionUserVO.getSessionInfoVO().getRemoteAddr()==null?"":p_sessionUserVO.getSessionInfoVO().getRemoteAddr());
            strBuff.append(" [Status:" + p_userVO.getStatus());
            strBuff.append(" # Active User ID:" + p_userVO.getActiveUserID());
            strBuff.append(" # User ID:" + p_userVO.getUserID());
            strBuff.append(" # Login id:" + p_userVO.getLoginID());
            strBuff.append(" # Password Modify:" + p_userVO.isPasswordModifyFlag());
            strBuff.append(" # User name:" + p_userVO.getUserName());
            if( p_userVO.getMsisdn()!=null){
            strBuff.append(" # Mobile no. :" + p_userVO.getMsisdn());
            }
            strBuff.append(" # Contact No.:" + p_userVO.getContactNo());
            strBuff.append(" # User Geographics:");
            ArrayList tempList = p_userVO.getGeographicalAreaList();
            UserGeographiesVO geoVO = null;
            String tempString = "";
            int j = 0;
            if (tempList != null) {
                j = tempList.size();
            }
            if (j > 0) {
                for (int i = 0; i < j; i++) {
                    geoVO = (UserGeographiesVO) tempList.get(i);
                    tempString = tempString + geoVO.getGraphDomainCode() + " ## ";
                }
                strBuff.append(tempString.substring(0, tempString.lastIndexOf("##")));
            }
            tempString = "";
            strBuff.append(" # Domain: ");
            if (p_userVO.getDomainCodes() != null) {
                for (int i = 0, k = p_userVO.getDomainCodes().length; i < k; i++) {
                    tempString = tempString + p_userVO.getDomainCodes()[i] + " ## ";
                }
                if (tempString.length() >= 2) {
                    strBuff.append(tempString.substring(0, tempString.lastIndexOf("##")));
                }
            }else{
                strBuff.append(p_userVO.getDomainName());
            }
            strBuff.append(" # Other Info :" + p_otherInfo);
            strBuff.append("]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : " + p_action + " Modify By : " + p_sessionUserVO.getUserID(), " Not able to log info, getting Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUserLog[log]", p_sessionUserVO.getUserID(), "", "", "Action : " + p_action + " Modify By : " + p_sessionUserVO.getUserID() + " Not able to log info in OperatorUserLog for User ID:" + p_userVO.getUserID() + " ,getting Exception=" + e.getMessage());
        }
    }

    public static void apiLog(String p_action, UserVO p_userVO, UserVO p_senderUserVO, RequestVO p_requestVO) {
        final String METHOD_NAME = "apiLog";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[Action: " + p_action);
            strBuff.append(" # Modify By login id:" + p_senderUserVO.getLoginID());
            strBuff.append(" # Modify By: " + p_userVO.getModifiedBy());
            strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_userVO.getModifiedOn()));
            strBuff.append(" [");
            strBuff.append(" # Status:" + p_userVO.getStatus());
            strBuff.append(" # Login id:" + p_userVO.getLoginID());
            strBuff.append(" # User ID:" + p_userVO.getUserID());
            strBuff.append(" # Password Modify:" + p_userVO.isPasswordModifyFlag());
            strBuff.append(" # User name:" + p_userVO.getUserName());
            strBuff.append(" # Mobile no. :" + p_userVO.getMsisdn());
            strBuff.append(" # Contact No.:" + p_userVO.getContactNo());
            strBuff.append(" # Category Name.:" + p_userVO.getCategoryVO().getCategoryName());
            strBuff.append(" # Email ID:" + p_userVO.getEmail());
            strBuff.append(" # Other Info :" + p_requestVO.getDecryptedMessage());
            strBuff.append("]");

            _log.info("", strBuff.toString());
            
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : " + p_action + " Modify By : " + p_senderUserVO.getUserID(), " Not able to log info, getting Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUserLog[log]", p_senderUserVO.getUserID(), "", "", "Action : " + p_action + " Modify By : " + p_senderUserVO.getUserID() + " Not able to log info in OperatorUserLog for User ID:" + p_userVO.getUserID() + " ,getting Exception=" + e.getMessage());
        }
    }
}
