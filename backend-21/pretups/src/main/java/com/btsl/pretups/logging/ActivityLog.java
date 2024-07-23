package com.btsl.pretups.logging;

import java.sql.Connection;
import java.util.HashMap;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;

/*
 * @(#)ActivityLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Shishu Pal Singh 03/08/07 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Class for maintain log for traversing any pageCode by the web user.
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.restapi.loggers.ElementCodeDetailsVO;
import com.restapi.loggers.LogVO;
import com.restapi.loggers.LoggerDAO;

public class ActivityLog {
    private static Log _log = LogFactory.getFactory().getInstance(ActivityLog.class.getName());
    private static java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);

    /**
     * ensures no instantiation
     */
    private ActivityLog(){
    	
    }
    private static HashMap<String , ElementCodeDetailsVO> map = new HashMap<String , ElementCodeDetailsVO>();
    static {
    	String METHOD_NAME ="StaticBlockActivityLogger";
    	Connection con = null;
		MComConnectionI mcomCon = null;
    	try {
    		mcomCon = new MComConnection();
			con = mcomCon.getConnection();
    		LoggerDAO loggerDAO = new LoggerDAO();
    		map = loggerDAO.getElementCodeDetailsMap(con);
    	}catch(Exception e){
    		_log.errorTrace(METHOD_NAME, e);
    	}
    }
    
    /**
     * log
     * 
     * @param p_pageCode
     * @param p_userVO
     */
    public static void log(String p_pageCode, UserVO p_userVO) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            if (p_userVO != null) {
                strBuff.append("[Action :ActivityLog ");
                strBuff.append(" # User ID:").append((p_userVO.getUserID()!= null)?p_userVO.getUserID():"");
                strBuff.append(" # Login id:").append((p_userVO.getLoginID()!= null)?p_userVO.getLoginID():"");
                strBuff.append(" # User name:").append((p_userVO.getUserName()!= null)?p_userVO.getUserName():"");
                strBuff.append(" # Mobile no.:").append((p_userVO.getMsisdn()!= null)?p_userVO.getMsisdn():""); 
                sdf.setLenient(false);
                strBuff.append(" # Date & Time:" + BTSLDateUtil.getLocaleTimeStamp(sdf.format(new java.util.Date())));
                strBuff.append(" # Network Code:").append((p_userVO.getNetworkID()!= null)?p_userVO.getNetworkID():"") ;
                strBuff.append(" # Network Name:").append((p_userVO.getNetworkName()!= null)?p_userVO.getNetworkName():"");
                strBuff.append(" # Domain Code:").append((p_userVO.getDomainID()!= null)?p_userVO.getDomainID():"");
                strBuff.append(" # Domain Name:").append((p_userVO.getDomainName()!= null)?p_userVO.getDomainName():"");
                strBuff.append(" # Category Code:").append((p_userVO.getCategoryCode()!= null)?p_userVO.getCategoryCode():"") ;
                strBuff.append(" # Category Name:").append((p_userVO.getCategoryVO()!=null)?((p_userVO.getCategoryVO().getCategoryName()!= null)?p_userVO.getCategoryVO().getCategoryName():""):"");
                if (_log.isDebugEnabled()) {
                    strBuff.append(" # Session Id:" + p_userVO.getSessionInfoVO().getSessionID());
                }
                strBuff.append(" # Remote Host:").append((p_userVO.getSessionInfoVO()!=null)?((p_userVO.getSessionInfoVO().getRemoteHost()!= null)?p_userVO.getSessionInfoVO().getRemoteHost():""):"") ;
                strBuff.append(" # Remote Address:").append((p_userVO.getSessionInfoVO()!=null)?((p_userVO.getSessionInfoVO().getRemoteAddr()!= null)?p_userVO.getSessionInfoVO().getRemoteAddr():""):"") ;
                strBuff.append(" # Module Code:").append((p_userVO.getCurrentModule()!= null)?p_userVO.getCurrentModule():"");
                strBuff.append(" # Role Code:").append((p_userVO.getCurrentRoleCode()!= null)?p_userVO.getCurrentRoleCode():"") ;
                strBuff.append(" # Page Code:"+ p_pageCode);
            } else {
                strBuff.append("Noting to add in Log, as userVo is null");
            }
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : ActivityLog # PageCode:" + p_pageCode + " Modify By :  Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivityLog[log]", "", "", "", "Action :ActivityLog  Modify By :  Not able to log info in ActivityLog for User ID:" + p_userVO.getUserID() + " ,getting Exception=" + e.getMessage());
        }
    }
    
    /**
     * 
     * @param p_logVO
     * @param remoteAddress
     * @param remoteHost
     */
    public static void webUILog(LogVO p_logVO , String remoteAddress , String remoteHost) {
        final String METHOD_NAME = "webUILog";
    	String roleCode = "";
    	String groupRoleName = "";
        try {
        	
        	if(map.size() > 0) {
        		ElementCodeDetailsVO elementCodeDetailsVO  = map.get(p_logVO.getElementCode());
        		if(elementCodeDetailsVO != null) {
        			roleCode = (elementCodeDetailsVO.getRoleCode() != null)? elementCodeDetailsVO.getRoleCode(): "";
        			if(!BTSLUtil.isNullString(p_logVO.getDomainType())) {
            			groupRoleName = (elementCodeDetailsVO.getGroupNameMap().get(p_logVO.getDomainType()) != null)?elementCodeDetailsVO.getGroupNameMap().get(p_logVO.getDomainType()):"";
        			}
        		}
        	}
        	
            StringBuffer strBuff = new StringBuffer();
                strBuff.append("[Action :WebUI-ActivityLog ");
                strBuff.append(" # UserId:").append((p_logVO.getUserID() != null)?p_logVO.getUserID():"");
                strBuff.append(" # Login id:").append((p_logVO.getLoginID()!= null)?p_logVO.getLoginID():"");
                strBuff.append(" # User name:").append((p_logVO.getUserName()!= null)?p_logVO.getUserName():"");
                strBuff.append(" # Mobile no.:").append((p_logVO.getMsisdn()!= null)?p_logVO.getMsisdn():"");
                strBuff.append(" # Date & Time:").append((p_logVO.getTimeStamp()!= null)?p_logVO.getTimeStamp():"");
                strBuff.append(" # Network Code:").append((p_logVO.getNetworkCode()!= null)?p_logVO.getNetworkCode():"") ;
                strBuff.append(" # Network Name:").append((p_logVO.getNetworkName()!= null)?p_logVO.getNetworkName():"");
                strBuff.append(" # Domain Code:").append((p_logVO.getDomainCode()!= null)?p_logVO.getDomainCode():"");
                strBuff.append(" # Domain Name:").append((p_logVO.getDomainName()!= null)?p_logVO.getDomainName():"");
                strBuff.append(" # Category Code:").append((p_logVO.getCategoryCode()!= null)?p_logVO.getCategoryCode():"") ;
                strBuff.append(" # Category Name:").append((p_logVO.getCategoryName()!=null)?p_logVO.getCategoryName():"");
                strBuff.append(" # Remote Address:").append((remoteAddress!=null)?remoteAddress:"");
                strBuff.append(" # Remote Host:").append((remoteHost!=null)?remoteHost:"");
                strBuff.append(" # Component Name:").append((p_logVO.getComponentName()!=null)?p_logVO.getComponentName():"");
                strBuff.append(" # Element Code:").append((p_logVO.getElementCode()!=null)?p_logVO.getElementCode():"");
                strBuff.append(" # Role Code:").append(roleCode);
                strBuff.append(" # Group Role Name:").append(groupRoleName);
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : WebUI-ActivityLog # ElementCode:" + p_logVO.getElementCode() + " Modify By :  Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivityLog[log]", "", "", "", "Action :ActivityLog  Modify By :  Not able to log info in ActivityLog for User ID:" + p_logVO.getUserID() + " ,getting Exception=" + e.getMessage());
        }
    }


}
