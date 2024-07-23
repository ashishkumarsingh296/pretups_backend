/**
 * @(#)UserDAO.java
 *                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 * 
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Mohit Goel 22/06/2005 Initial Creation
 *                  Sandeep Goel 12/12/2005 Modification
 *                  Shashank Gaur 29/03/2013 Modification(Barred For Deletion)
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  This class is used for User Insertion/Updation
 * 
 */
package com.selftopup.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;

/**
 * 
 */
public class UserDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @param p_oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection p_con, String p_userId, long p_oldLastModified) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("recordModified", "Entered: userId= " + p_userId + "oldLastModified= " + p_oldLastModified);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM users WHERE user_id = ?";
        Timestamp newLastModified = null;
        if (p_oldLastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled())
                _log.debug("recordModified", "QUERY: sqlselect= " + sqlRecordModified);
            // create a prepared statement and execute it
            pstmt = p_con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old=" + p_oldLastModified);
                _log.debug("recordModified", " new=" + newLastModified.getTime());
            }
            if (newLastModified.getTime() != p_oldLastModified) {
                modified = true;
            }
            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error("recordModified", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.processing");
        } // end of catch

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method for checking Pasword or already exist in Pin_Password_history
     * table or not.
     * 
     * @author santanu.mohanty
     * @param p_con
     *            java.sql.Connection
     * @param p_modificationType
     *            String
     * @param p_userId
     *            String
     * @param p_Msisdn
     *            String
     * @param p_newPassword
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean checkPasswordHistory(Connection p_con, String p_modificationType, String p_userId, String p_Msisdn, String p_newPassword) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkPasswordHistory", "Entered: p_modification_type=" + p_modificationType + "p_userId=" + p_userId + "p_Msisdn= " + p_Msisdn + " p_newPassword= " + p_newPassword);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        if (p_modificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
            strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? )  WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        } else {
            strBuff.append(" SELECT pin_or_password,modified_on  FROM (SELECT pin_or_password,modified_on, row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? and msisdn_or_loginid= ? )  WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        }
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("checkPasswordHistory", "QUERY sqlSelect=" + sqlSelect);
        }
        try {

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_modificationType);
            pstmt.setString(2, p_userId);
            if (p_modificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
                pstmt.setInt(3, SystemPreferences.PREV_PASS_NOT_ALLOW);
            } else {
                pstmt.setString(3, p_Msisdn);
                pstmt.setInt(4, SystemPreferences.PREV_PIN_NOT_ALLOW);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("Pin_or_Password").equals(p_newPassword)) {
                    existFlag = true;
                    break;
                }
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error("checkPasswordHistory", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "checkPasswordHistory", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("checkPasswordHistory", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "checkPasswordHistory", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("checkPasswordHistory", "Exiting: existFlag=" + existFlag);
            }
        }
    }
}
