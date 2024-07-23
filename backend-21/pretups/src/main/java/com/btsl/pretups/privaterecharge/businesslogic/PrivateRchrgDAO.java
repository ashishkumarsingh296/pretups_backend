/**
 * @(#)PrivateRchrgDAO.java
 *                          Copyright(c) 2009, Comviva technologies Ltd.
 *                          All Rights Reserved
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Created On History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Babu Kunwar 05-Sep-2011 Initital Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 */

package com.btsl.pretups.privaterecharge.businesslogic;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

public class PrivateRchrgDAO {

    private Log _log = LogFactory.getLog(PrivateRchrgDAO.class.getName());

    /**
     * 
     * @param p_con
     * @param p_subscriberSID
     * @return
     * @throws BTSLBaseException
     */
    public boolean checkIfSIDAlreadyExsist(Connection p_con, String p_subscriberSID) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkIfSIDAlreadyExsist ", "Entered subscriberID " + p_subscriberSID);
        }
        final String METHOD_NAME = "checkIfSIDAlreadyExsist";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        try {
            StringBuffer selectQryBuffer = new StringBuffer("select DISTINCT USER_SID");
            selectQryBuffer.append(" FROM  subscriber_msisdn_alias");
            selectQryBuffer.append(" WHERE  USER_SID = ? ");
            String selectQuery = selectQryBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("checkIfSIDAlreadyExsist ", "select query: " + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_subscriberSID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
            return isExist;
        } catch (SQLException sqle) {
            _log.error("checkIfSIDAlreadyExsist", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[checkIfSIDAlreadyExsist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "checkIfSIDAlreadyExsist", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("checkIfSIDAlreadyExsist", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[checkIfSIDAlreadyExsist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "checkIfSIDAlreadyExsist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("checkIfSIDAlreadyExsist", "Exiting with : " + isExist);
            }
        }
    }

    /**
     * 
     * @param p_con
     * @param p_privateRchrgVO
     * @return
     * @throws BTSLBaseException
     */
    public int saveSubscriberSID(Connection p_con, PrivateRchrgVO p_privateRchrgVO) throws BTSLBaseException {

        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String METHOD_NAME = "saveSubscriberSID";
        if (_log.isDebugEnabled()) {
            _log.debug("saveSubscriberSID", "Entered: p_requestVO= " + p_privateRchrgVO.toString());
        }
        try {
            StringBuffer strBuff = new StringBuffer("INSERT INTO subscriber_msisdn_alias(MSISDN, USER_SID, CREATED_ON, CREATED_BY, MODIFIED_ON,");
            strBuff.append("MODIFIED_BY, USER_NAME, REQUEST_GATEWAY_CODE, REQUEST_GATEWAY_TYPE)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("saveSubscriberSID", "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(1, p_privateRchrgVO.getMsisdn());
            psmtInsert.setString(2, p_privateRchrgVO.getUserSID());
            if (p_privateRchrgVO.getCreatedOn() != null) {
                psmtInsert.setTimestamp(3, BTSLUtil.getSQLDateTimeFromUtilDate(p_privateRchrgVO.getCreatedOn()));
            } else {
                psmtInsert.setTimestamp(3, BTSLUtil.getSQLDateTimeFromUtilDate(new Date()));
            }
            psmtInsert.setString(4, p_privateRchrgVO.getCreatedBy());
            if (p_privateRchrgVO.getModifyOn() != null) {
                psmtInsert.setTimestamp(5, BTSLUtil.getSQLDateTimeFromUtilDate(p_privateRchrgVO.getModifyOn()));
            } else {
                psmtInsert.setTimestamp(5, BTSLUtil.getSQLDateTimeFromUtilDate(new Date()));
            }
            psmtInsert.setString(6, p_privateRchrgVO.getModifyBy());
            psmtInsert.setString(7, p_privateRchrgVO.getUserName());
            psmtInsert.setString(8, p_privateRchrgVO.getRequestGatewayCode());
            psmtInsert.setString(9, p_privateRchrgVO.getRequestGatewayType());

            insertCount = psmtInsert.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("saveSubscriberSID", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[saveSubscriberSID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "saveSubscriberSID", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("saveSubscriberSID", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[saveSubscriberSID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "saveSubscriberSID", "error.general.processing");
        } finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("saveSubscriberSID", "Exiting: insertCount=" + insertCount);
            }
        }
        return insertCount;
    }

    /**
     * 
     * @param p_con
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public PrivateRchrgVO loadSubscriberSIDDetails(Connection p_con, String p_msisdn) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadSubscriberSIDDetails", "Entered p_msisdn: " + p_msisdn);
        }
        final String METHOD_NAME = "loadSubscriberSIDDetails";
        PreparedStatement pstmtSelect = null;
        PrivateRchrgVO privateRchrgVO = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT MSISDN, USER_SID ,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE ,");
            selectQueryBuff.append("CREATED_ON,CREATED_BY,MODIFIED_ON ,MODIFIED_BY ,USER_NAME");
            selectQueryBuff.append(" FROM  subscriber_msisdn_alias   ");
            selectQueryBuff.append(" WHERE  msisdn = ? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberSIDDetails ", "select query: " + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msisdn);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                privateRchrgVO = new PrivateRchrgVO();
                privateRchrgVO.setMsisdn(rs.getString("MSISDN"));
                privateRchrgVO.setUserSID(rs.getString("USER_SID"));
                privateRchrgVO.setRequestGatewayCode(rs.getString("REQUEST_GATEWAY_CODE"));
                privateRchrgVO.setRequestGatewayType(rs.getString("REQUEST_GATEWAY_TYPE"));
                privateRchrgVO.setCreatedOn(rs.getTimestamp("CREATED_ON"));
                privateRchrgVO.setCreatedBy(rs.getString("CREATED_BY"));
                privateRchrgVO.setModifyOn(rs.getTimestamp("MODIFIED_ON"));
                privateRchrgVO.setModifyBy(rs.getString("MODIFIED_BY"));
                privateRchrgVO.setUserName(rs.getString("USER_NAME"));
            }
            return privateRchrgVO;
        } catch (SQLException sqle) {
            _log.error("loadSubscriberSIDDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[loadSubscriberSIDDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberSIDDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadSubscriberSIDDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[loadSubscriberSIDDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberSIDDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberSIDDetails", "Exiting loadSubscriberSIDDetails");
            }
        }
    }

    /**
     * 
     * @param p_con
     * @param p_subscriberSID
     * @param p_subscriberMsisdn
     * @return
     * @throws BTSLBaseException
     */
    public int deactivateSubscriberSID(Connection p_con, String p_subscriberSID, String p_subscriberMsisdn) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("deactivateSubscriberSID", "Entered with SubscriberSID " + p_subscriberSID + " MSISDN " + p_subscriberMsisdn);
        }
        final String METHOD_NAME = "deactivateSubscriberSID";
        int deletedSID = 0;
        PreparedStatement pstmtDeactivate = null;
        try {
            String deactivateQuery = "delete from subscriber_msisdn_alias where MSISDN=? and USER_SID=?";
            if (_log.isDebugEnabled()) {
                _log.debug("deactivateSubscriberSID", "Deactivation Confirm Query " + deactivateQuery);
            }
            pstmtDeactivate = p_con.prepareStatement(deactivateQuery);
            pstmtDeactivate.setString(1, p_subscriberMsisdn);
            pstmtDeactivate.setString(2, p_subscriberSID);
            deletedSID = pstmtDeactivate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("deactivateSubscriberSID", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deactivateSubscriberSID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deactivateSubscriberSID", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deactivateSubscriberSID", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deactivateSubscriberSID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deactivateSubscriberSID", "error.general.processing");
        } finally {
            try {
                if (pstmtDeactivate != null) {
                    pstmtDeactivate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deactivateSubscriberSID", "Exiting: deleteCount=" + deletedSID);
            }
        }
        return deletedSID;
    }

    public int deleteUserDetails(Connection p_con, String p_subscriberSID) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("deleteUserDetails", "Entered with SubscriberSID " + p_subscriberSID);
        }
        final String METHOD_NAME = "deleteUserDetails";
        int deletedSID = 0;
        PreparedStatement pstmtDeactivate = null;
        try {
            String deactivateQuery = "delete from subscriber_msisdn_alias where USER_SID=?";
            if (_log.isDebugEnabled()) {
                _log.debug("deleteUserDetails", "Deletion Confirm Query " + deactivateQuery);
            }
            pstmtDeactivate = p_con.prepareStatement(deactivateQuery);
            String subscriberSID;
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
            	subscriberSID= BTSLUtil.encrypt3DesAesText(p_subscriberSID);
            else
            	subscriberSID = p_subscriberSID;
            pstmtDeactivate.setString(1, subscriberSID);
            deletedSID = pstmtDeactivate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("deleteUserDetails", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deactivateSubscriberSID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteUserDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deactivateSubscriberSID", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deactivateSubscriberSID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteUserDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtDeactivate != null) {
                    pstmtDeactivate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deleteUserDetails", "Exiting: deleteCount=" + deletedSID);
            }
        }
        return deletedSID;
    }

    public PrivateRchrgVO loadUserDetailsBySID(Connection p_con, String p_subscriberSID) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserDetailsBySID ", "Entered subscriberID " + p_subscriberSID);
        }
        final String METHOD_NAME = "loadUserDetailsBySID";
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        PrivateRchrgVO privateRchrgVO = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT MSISDN, USER_SID ,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE ,");
            selectQueryBuff.append("CREATED_ON,CREATED_BY,MODIFIED_ON ,MODIFIED_BY ,USER_NAME");
            selectQueryBuff.append(" FROM  subscriber_msisdn_alias");
            selectQueryBuff.append(" WHERE  USER_SID = ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserDetailsBySID ", "select query: " + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            String subscriberSid;
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
            	subscriberSid = BTSLUtil.encrypt3DesAesText(p_subscriberSID);
            else
            	subscriberSid = p_subscriberSID;
            	pstmtSelect.setString(1, subscriberSid);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                privateRchrgVO = new PrivateRchrgVO();
                privateRchrgVO.setMsisdn(rs.getString("MSISDN"));
                privateRchrgVO.setUserSID(rs.getString("USER_SID"));
                privateRchrgVO.setRequestGatewayCode(rs.getString("REQUEST_GATEWAY_CODE"));
                privateRchrgVO.setRequestGatewayType(rs.getString("REQUEST_GATEWAY_TYPE"));
                privateRchrgVO.setCreatedOn(rs.getTimestamp("CREATED_ON"));
                privateRchrgVO.setCreatedBy(rs.getString("CREATED_BY"));
                privateRchrgVO.setModifyOn(rs.getTimestamp("MODIFIED_ON"));
                privateRchrgVO.setModifyBy(rs.getString("MODIFIED_BY"));
                privateRchrgVO.setUserName(rs.getString("USER_NAME"));
                // added by rahuld for updating last access on field
                selectQuery = "UPDATE subscriber_msisdn_alias SET last_access_on=? WHERE  USER_SID = ?";
                if (_log.isDebugEnabled()) {
                    _log.debug("loadUserDetailsBySID ", "update query: " + selectQuery);
                }
                pstmtSelect1 = p_con.prepareStatement(selectQuery);
                pstmtSelect1.setDate(1, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                pstmtSelect1.setString(2, subscriberSid);
                if (pstmtSelect1.executeUpdate() > 0) {
                    p_con.commit();
                } else {
                    p_con.rollback();
                }
            }
            return privateRchrgVO;
        } catch (SQLException sqle) {
            _log.error("loadUserDetailsBySID", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[checkIfSIDAlreadyExsist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserDetailsBySID", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadUserDetailsBySID", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeDAO[checkIfSIDAlreadyExsist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadUserDetailsBySID", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserDetailsBySID", "Exiting ");
            }
        }
    }
}
