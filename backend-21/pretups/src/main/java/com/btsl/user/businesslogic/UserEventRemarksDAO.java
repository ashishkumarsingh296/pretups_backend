package com.btsl.user.businesslogic;

/*
 * UserEventRemarksDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ved Prakash 25/09/2008 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2008 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
/**
 * 
 * UserEventRemarksDAO
 *
 */
public class UserEventRemarksDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * @param conn
     * @param puserEventRemarksVO
     * @return
     * @throws BTSLBaseException
     */
    public int addUserEventRemark(Connection conn, UserEventRemarksVO puserEventRemarksVO) throws BTSLBaseException {
        final String methodName = "addUserEventRemark";
        LogFactory.printLog(methodName, "Entered: p_userEventRemarksVO " + puserEventRemarksVO, log);
    	
        
        int insertCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder("INSERT INTO user_event_remarks (user_id, event_type, remarks, ");
            strBuff.append("created_by, created_on, module,msisdn, user_type) VALUES (?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();
            LogFactory.printLog(methodName, "Query sqlInsert:" + insertQuery, log);
            
            try(PreparedStatement psmtInsert = (PreparedStatement) conn.prepareStatement(insertQuery);)
            {

            psmtInsert.setString(1, puserEventRemarksVO.getUserID());
            psmtInsert.setString(2, puserEventRemarksVO.getEventType());
           
            psmtInsert.setString(3, puserEventRemarksVO.getRemarks());
            psmtInsert.setString(4, puserEventRemarksVO.getCreatedBy());
            psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(puserEventRemarksVO.getCreatedOn()));
            psmtInsert.setString(6, puserEventRemarksVO.getModule());
            psmtInsert.setString(7, puserEventRemarksVO.getMsisdn());
            psmtInsert.setString(8, puserEventRemarksVO.getUserType());

            insertCount = insertCount + psmtInsert.executeUpdate();
            psmtInsert.clearParameters();

        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserEventRemarksDAO[addUserEventRemark]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserEventRemarksDAO[addUserEventRemark]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, log);
          
        } // end of finally
        return insertCount;
    }
/**
 * 
 * @param conn
 * @param plist
 * @return
 * @throws BTSLBaseException
 */
    public int addEventRemarkList(Connection conn, ArrayList plist) throws BTSLBaseException {
    	final String methodName = "addEventRemarkList";
    	LogFactory.printLog(methodName, "Entered: p_list= " + plist.size(), log);
    	
         
        int insertCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder("INSERT INTO user_event_remarks (user_id, event_type, remarks, ");
            strBuff.append("created_by, created_on, module,msisdn, user_type) VALUES (?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("addEventRemarkList", "Query sqlInsert:" + insertQuery);
            }
            try(PreparedStatement psmtInsert =  conn.prepareStatement(insertQuery);)
            {
            UserEventRemarksVO puserEventRemarksVO = null;
            for (int i = 0, j = plist.size(); i < j; i++) {
                puserEventRemarksVO = (UserEventRemarksVO) plist.get(i);
                psmtInsert.setString(1, puserEventRemarksVO.getUserID());
                psmtInsert.setString(2, puserEventRemarksVO.getEventType());

                psmtInsert.setString(3, puserEventRemarksVO.getRemarks());
                psmtInsert.setString(4, puserEventRemarksVO.getCreatedBy());
                psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(puserEventRemarksVO.getCreatedOn()));
                psmtInsert.setString(6, puserEventRemarksVO.getModule());
                psmtInsert.setString(7, puserEventRemarksVO.getMsisdn());
                psmtInsert.setString(8, puserEventRemarksVO.getUserType());
                insertCount = insertCount + psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
            }
        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserEventRemarksDAO[addEventRemarkList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserEventRemarksDAO[addEventRemarkList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, log);
           
        } // end of finally
        return insertCount;
    }
}
