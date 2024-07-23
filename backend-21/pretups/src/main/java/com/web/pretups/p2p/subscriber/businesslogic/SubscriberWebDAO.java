package com.web.pretups.p2p.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class SubscriberWebDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for SubscriberWebDAO.
     */
    public SubscriberWebDAO() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Update the subscriber staus.Status can be active,suspended , deregistered
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int updateSubscriberDetails(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_subscriber :" + p_senderVO);
        }
        final String method = methodName;

        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_subscribers SET activated_on=?, last_transfer_on=?,total_transfers=?,total_transfer_amount=?, ");
            sbf.append(" status = ?,user_name=?,billing_type=?, ");
            sbf.append(" billing_cycle_date=?,credit_limit=? ,modified_on = ? , ");
            sbf.append(" modified_by = ?,prefix_id=? WHERE msisdn=? AND subscriber_type = ?");
            final String updateQuery = sbf.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + updateQuery);
            }

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)

            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            int i = 1;
            if (p_senderVO.getModifiedOn() != null) {
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            } else {
                pstmtUpdate.setNull(i++, Types.DATE);
            }

            if (p_senderVO.getLastTransferOn() != null) {
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
            } else {
                pstmtUpdate.setNull(i++, Types.DATE);
            }
            pstmtUpdate.setLong(i++, p_senderVO.getTotalTransfers());
            pstmtUpdate.setLong(i++, p_senderVO.getTotalTransferAmount());
            pstmtUpdate.setString(i++, p_senderVO.getStatus());

            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,

            pstmtUpdate.setString(i++, p_senderVO.getUserName());

            pstmtUpdate.setString(i++, p_senderVO.getBillingType());
            if (p_senderVO.getBillingCycleDate() != null) {
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getBillingCycleDate()));
            } else {
                pstmtUpdate.setNull(i++, Types.DATE);
            }
            pstmtUpdate.setLong(i++, p_senderVO.getMonthlyTransferAmount());
            if (p_senderVO.getModifiedOn() != null) {
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            } else {
                pstmtUpdate.setNull(i++, Types.DATE);
            }
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
            pstmtUpdate.setLong(i++, p_senderVO.getPrefixID());
            pstmtUpdate.setString(i++, p_senderVO.getMsisdn());
            pstmtUpdate.setString(i++, p_senderVO.getSubscriberType());
            final boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, method, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }

        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberWebDAO[updateSubscriberDetails]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberWebDAO[updateSubscriberDetails]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
            }
        }// end of finally

        return updateCount;
    }

    /**
     * Method isRecordModified. This method is used to check that is the record
     * modified during the processing.
     * 
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_userId
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_userId) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_oldlastModified = ");
        	msg.append(p_oldlastModified);
        	msg.append(", p_userId = ");
        	msg.append(p_userId);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuilder sqlRecordModified = new StringBuilder();
        sqlRecordModified.append("SELECT modified_on FROM p2p_subscribers ");
        sqlRecordModified.append("WHERE user_id=? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            final String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_userId);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record may be deleted
            // during the transaction .
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberWebDAO[isRecordModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberWebDAO[isRecordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method addPostpaidControlParameters.
     * 
     * @param p_con
     *            Connection
     * @param p_senderVO
     *            SenderVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addPostpaidControlParameters(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "addPostpaidControlParameters";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_senderVO=" + p_senderVO);
        }
        PreparedStatement psmt = null;
        int insertCount = 0;
        try {
            final StringBuilder strBuff = new StringBuilder();
            // insert query
            strBuff.append("INSERT INTO postpaid_control_parameters (msisdn, daily_transfers_allowed, ");
            strBuff.append("daily_transfer_amt_allowed, weekly_transfers_allowed, weekly_transfer_amt_allowed,");
            strBuff.append("monthly_transfers_allowed, monthly_transfer_amt_allowed, date_time)");
            strBuff.append("VALUES (?,?,?,?,?,?,?,?)");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            psmt.setString(1, p_senderVO.getMsisdn());
            psmt.setLong(2, p_senderVO.getDailyTransferCount());
            psmt.setLong(3, PretupsBL.getSystemAmount(p_senderVO.getDailyTransferAmount()));
            psmt.setLong(4, p_senderVO.getWeeklyTransferCount());
            psmt.setLong(5, PretupsBL.getSystemAmount(p_senderVO.getWeeklyTransferAmount()));
            psmt.setLong(6, p_senderVO.getMonthlyTransferCount());
            psmt.setLong(7, PretupsBL.getSystemAmount(p_senderVO.getMonthlyTransferAmount()));
            // Added on 07/02/08
            psmt.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            insertCount = psmt.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberWebDAO[addPostpaidControlParameters]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberWebDAO[addPostpaidControlParameters]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting insertCount  :" + insertCount);
            }
        }// end of finally

        return insertCount;
    }

    /**
     * Method loadPrePaidControlParameters.
     * method to load the control parameters of the prepaid subscriber.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_networkCode
     *            String
     * @param p_serviceClassID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadPrePaidControlParameters(Connection p_con, String p_msisdn, String p_networkCode, String p_serviceClassID) throws BTSLBaseException {
        final String methodName = "loadPrePaidControlParameters";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_msisdn = ");
        	msg.append(p_msisdn);
        	msg.append(", p_serviceClassID = ");
        	msg.append(p_serviceClassID);
        	msg.append(", p_networkCode = ");
        	msg.append(p_networkCode);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs1 = null;

        ArrayList parameterList = null;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("SELECT SP.preference_code , SP.name,SP.default_value , SP.value_type  ");
            selectQueryBuff.append("FROM system_preferences SP ");
            selectQueryBuff.append("WHERE SP.display='Y' AND  SP.module=? AND SP.type=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQueryBuff);
            }
            pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.P2P_MODULE);
            pstmtSelect.setString(i++, PreferenceI.SERVICE_CLASS_LEVEL);
            rs = pstmtSelect.executeQuery();
            parameterList = new ArrayList();
            final HashMap preferenceMap = new HashMap();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                if (PreferenceI.TYPE_AMOUNT.equals(rs.getString("value_type"))) {
                    listValueVO = new ListValueVO(rs.getString("name"), PretupsBL.getDisplayAmount(rs.getLong("default_value")));
                } else {
                    listValueVO = new ListValueVO(rs.getString("name"), rs.getString("default_value"));
                }
                preferenceMap.put(rs.getString("preference_code"), listValueVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "preferenceMap:" + preferenceMap);
            }
            if (!BTSLUtil.isNullString(p_serviceClassID)) {
                final StringBuilder selectQueryBuff1 = new StringBuilder();
                selectQueryBuff1.append("SELECT SP.preference_code ,SP.name,SCP.value,SP.value_type ");
                selectQueryBuff1.append("FROM service_class_preferences SCP,system_preferences SP,p2p_subscribers PSUB ");
                selectQueryBuff1.append("WHERE SCP.preference_code=SP.preference_code AND SP.display='Y'AND ");
                selectQueryBuff1.append("SCP.network_code=? AND SP.module=? AND PSUB.msisdn=? ");
                selectQueryBuff1.append("AND PSUB.service_class_id=SCP.service_class_id ");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + selectQueryBuff1);
                }
                pstmtSelect1 = p_con.prepareStatement(selectQueryBuff1.toString());
                i = 1;
                pstmtSelect1.setString(i++, p_networkCode);
                pstmtSelect1.setString(i++, PretupsI.P2P_MODULE);
                pstmtSelect1.setString(i++, p_msisdn);
                rs1 = pstmtSelect1.executeQuery();
                while (rs1.next()) {
                    if (PreferenceI.TYPE_AMOUNT.equals(rs1.getString("value_type"))) {
                        listValueVO = new ListValueVO(rs1.getString("name"), PretupsBL.getDisplayAmount(rs1.getLong("value")));
                    } else {
                        listValueVO = new ListValueVO(rs1.getString("name"), rs1.getString("value"));
                    }
                    preferenceMap.put(rs1.getString("preference_code"), listValueVO);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "preferenceMap:" + preferenceMap);
            }
            final Iterator iterator = preferenceMap.keySet().iterator();
            String key = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                parameterList.add(preferenceMap.get(key));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPrePaidControlParameters]",
                "", p_msisdn, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPrePaidControlParameters]",
                "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting parameterList.size()=" + parameterList.size());
            }
        }// end of finally
        return parameterList;
    }

    /**
     * Method for checking Pasword or already exist in Pin_Password_history
     * table or not.
     * 
     * @author sonali.garg
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
        final String methodName = "checkPasswordHistory";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_modification_type = ");
        	msg.append(p_modificationType);
        	msg.append(", p_userId = ");
        	msg.append(p_userId);
        	msg.append(", p_Msisdn = ");
        	msg.append(p_Msisdn);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();

        if (p_modificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
            strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? ) qry WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        } else {
            strBuff.append(" SELECT pin_or_password,modified_on  FROM (SELECT pin_or_password,modified_on, row_number()  over (ORDER BY modified_on DESC) rn  ");
            strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND user_id=? and msisdn_or_loginid= ? ) qry  WHERE rn <= ? ");
            strBuff.append(" ORDER BY modified_on DESC ");
        }
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_modificationType);
            pstmt.setString(2, p_userId);
            if (p_modificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
                pstmt.setInt(3, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue());
            } else {
                pstmt.setString(3, p_Msisdn);
                pstmt.setInt(4, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue());
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
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method to change the password of a user
     * 
     * @param p_con
     * @param p_userId
     * @param p_newPassword
     * @param p_pswdModifiedOn
     * @return int
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public int changePassword(Connection p_con, String p_userId, String p_newPassword, Date p_pswdModifiedOn, String p_modifiedBy) throws BTSLBaseException {
        final String methodName = "changePassword";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_userId = ");
        	msg.append(p_userId);
        	msg.append(", p_modifiedBy = ");
        	msg.append(p_modifiedBy);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {

            final String queryUpdate = "UPDATE p2p_subscribers SET pswd_modified_on=?,password=?,modified_on= ?, modified_by= ?, PSWD_RESET=? WHERE user_id = ? ";
            pstmtUpdate = p_con.prepareStatement(queryUpdate);
            pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_pswdModifiedOn));
            pstmtUpdate.setString(2, p_newPassword);
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_pswdModifiedOn));
            pstmtUpdate.setString(4, p_modifiedBy);
            pstmtUpdate.setString(5, "N");
            pstmtUpdate.setString(6, p_userId);
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("changePassword()", " Exiting with updateCount=" + updateCount);
            }
        }
        return updateCount;
    }
}
