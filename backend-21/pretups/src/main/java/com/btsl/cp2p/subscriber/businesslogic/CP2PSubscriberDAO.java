/**
 * @(#)CP2PSubscriberDAO.java
 *                            Copyright(c) 2009, Comvivia Technologies Ltd.
 *                            All Rights Reserved
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Gopal 22/06/2009 Initial Creation
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            This class is used for User Insertion/Updation
 * 
 */
package com.btsl.cp2p.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

/**
 * 
 * CP2PSubscriberDAO
 *
 */
public class CP2PSubscriberDAO {

    /**
     * Commons Logging instance.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method to change the password of a user
     * 
     * @param conn
     * @param puserId
     * @param pnewPassword
     * @param ppswdModifiedOn
     * @param pmodifiedBy
     * @return int
     * @throws BTSLBaseException
     */
    public int changePassword(Connection conn, String puserId, String pnewPassword, Date ppswdModifiedOn, String pmodifiedBy) throws BTSLBaseException {
    	
    	 final String methodName = "changePassword";
    	 StringBuilder loggerValue= new StringBuilder(); 
    	loggerValue.setLength(0);
     	loggerValue.append("Entered: p_userId= ");
     	loggerValue.append(puserId);
		loggerValue.append(" p_modifiedBy=");
     	loggerValue.append(pmodifiedBy);
     	String logVal=loggerValue.toString();
    	LogFactory.printLog(methodName, logVal, log);
    	
        
        int updateCount = 0;
        try {

            String queryUpdate = "UPDATE p2p_subscribers SET pswd_modified_on=?,password=?,modified_on= ?, modified_by= ?, PSWD_RESET=? WHERE user_id = ? ";
            try(PreparedStatement pstmtUpdate = conn.prepareStatement(queryUpdate);)
            {
            pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(ppswdModifiedOn));
            pstmtUpdate.setString(2, pnewPassword);
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(ppswdModifiedOn));
            pstmtUpdate.setString(4, pmodifiedBy);
            pstmtUpdate.setString(5, "N");
            pstmtUpdate.setString(6, puserId);
            updateCount = pstmtUpdate.executeUpdate();
        } 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
         	loggerValue.append("SQLException: ");
         	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
         	loggerValue.append("Exception: ");
         	loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing",e);
        } finally {
           
            
            loggerValue.setLength(0);
         	loggerValue.append( " Exiting with updateCount=");
         	loggerValue.append(updateCount);
        	String logVal1=loggerValue.toString();
            LogFactory.printLog(methodName, logVal1 + updateCount, log);
            
        }
        return updateCount;
    }

    /********************************** CHANNEL USER RELATED METHODS *******************************/

    /**
     * Check for existance of fixed role
     * 
     * @param conn
     * @param pcategoryCode
     * @param proleCode
     * @param pdomainType
     * @return boolean
     */
    public boolean isFixedRoleAndExist(Connection conn, String pcategoryCode, String proleCode, String pdomainType) {
    	final String methodName = "isFixedRoleAndExist";
    	StringBuilder loggerValue= new StringBuilder();
     	loggerValue.append("Entered p_categoryCode:");
     	loggerValue.append(pcategoryCode);
		loggerValue.append(pcategoryCode);
     	loggerValue.append(proleCode);
     	loggerValue.append(" p_domainType=");
     	loggerValue.append(pdomainType);
     	String logVal=loggerValue.toString();
    	LogFactory.printLog(methodName, logVal, log);
    
        
        boolean roleStatus = false;
        try {
            StringBuilder queryBuff = new StringBuilder("SELECT 1 FROM category_roles CR,roles R WHERE CR.category_code=? AND CR.role_code=?");
            queryBuff.append(" AND R.domain_type=? AND CR.role_code=R.role_code AND (R.status IS NULL OR R.status='Y')");
            LogFactory.printLog(methodName, " select query:" + queryBuff.toString(), log);
        
           try(PreparedStatement psmt = conn.prepareStatement(queryBuff.toString());)
           {
            psmt.setString(1, pcategoryCode);
            psmt.setString(2, proleCode);
            psmt.setString(3, pdomainType);
            try(ResultSet rs = psmt.executeQuery();)
            {
            while (rs.next()) {
                roleStatus = true;
            }
        }
           }
        }catch (Exception ex2) {
            log.errorTrace(methodName, ex2);
            roleStatus = false;
        } 
        loggerValue.setLength(0);
     	loggerValue.append("Exiting role status=");
     	loggerValue.append(roleStatus);
     	String logVal1=loggerValue.toString();
     	
       LogFactory.printLog(methodName, logVal1, log);
        
        return roleStatus;
    }

    /**
     * Method for checking Pasword or already exist in Pin_Password_history
     * table or not.
     * 
     * @author santanu.mohanty
     * @param conn
     *            java.sql.Connection
     * @param pmodificationType
     *            String
     * @param puserId
     *            String
     * @param pMsisdn
     *            String
     * @param pnewPassword
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean checkPasswordHistory(Connection conn, String pmodificationType, String puserId, String pMsisdn, String pnewPassword) throws BTSLBaseException {
    	final String methodName = "checkPasswordHistory";
    	StringBuilder loggerValue= new StringBuilder(); 
     	loggerValue.append("Entered: p_modification_type=");
     	loggerValue.append(pmodificationType);
		loggerValue.append("p_userId=");
     	loggerValue.append(puserId);
     	loggerValue.append("p_Msisdn= ");
     	loggerValue.append(pMsisdn);
     	String logVal=loggerValue.toString();
    	LogFactory.printLog(methodName, logVal, log);
    	
        
       
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        if (pmodificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
            strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? )qry WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        } else {
            strBuff.append(" SELECT pin_or_password,modified_on  FROM (SELECT pin_or_password,modified_on, row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? and msisdn_or_loginid= ? )qry WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        }
        String sqlSelect = strBuff.toString();
        loggerValue.setLength(0);
     	loggerValue.append("QUERY sqlSelect=");
     	loggerValue.append(sqlSelect);
     	String logVal1=loggerValue.toString();
        LogFactory.printLog(methodName, logVal1, log);
       
        try(PreparedStatement pstmt = conn.prepareStatement(sqlSelect);) {

            
            pstmt.setString(1, pmodificationType);
            pstmt.setString(2, puserId);
            if (pmodificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
                pstmt.setInt(3, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue());
            } else {
                pstmt.setString(3, pMsisdn);
                pstmt.setInt(4, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue());
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                if (rs.getString("Pin_or_Password").equals(pnewPassword)) {
                    existFlag = true;
                    break;
                }
            }
            return existFlag;
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
         	loggerValue.append("SQLException : ");
         	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
         	loggerValue.append("Exception : ");
         	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing",ex);
        } finally {
           
            
            
            loggerValue.setLength(0);
         	loggerValue.append("Exiting: existFlag=");
         	loggerValue.append(existFlag);
         	String logVal2=loggerValue.toString();
            LogFactory.printLog(methodName, logVal2, log);
            
        }
    }
}
