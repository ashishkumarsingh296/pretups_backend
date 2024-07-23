package com.web.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.requestVO.TransferProfileProductReqVO;

public class TransferProfileWebDAO {
	private TransferProfileWebQry transferProfileWebQry;

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for TransferProfileWebDAO.
     */
    public TransferProfileWebDAO() {
        super();
        transferProfileWebQry=(TransferProfileWebQry)ObjectProducer.getObject(QueryConstants.TRANSFER_PROFILE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Method for add the transfer control profile detail in transfer_profile
     * table.
     * Method :addTransferControlProfile
     * param p_con java.sql.Connection
     * 
     * @param p_profileVO
     *            ProfileVO
     * @param p_profileID
     *            long
     * @return updateCount int
     * @throws BTSLBaseException
     *             modified by Manisha: Insert default profile details at the
     *             time of ading profile details
     */
    public int addTransferControlProfile(Connection p_con, TransferProfileVO p_profileVO, long p_profileID) throws BTSLBaseException {
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        ResultSet rs = null;
        int updateCount = 0;
        final String methodName = "addTransferControlProfile";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:= p_profileVO=" + p_profileVO.toString() + ",p_profileID=" + p_profileID);
        }
        try {
            final StringBuilder strBuff = new StringBuilder("SELECT 1 FROM transfer_profile");
            strBuff.append(" WHERE profile_id =?");
            final String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlSelect:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, String.valueOf(p_profileID));
            rs = pstmtSelect.executeQuery();
            pstmtSelect.clearParameters();
            final StringBuilder insertBuff = new StringBuilder();
            String insertQuery = null;
            if (!rs.next()) {
                insertBuff.append("INSERT INTO transfer_profile (profile_id,");
                insertBuff.append(" short_name,profile_name,status,description,");
                insertBuff.append(" daily_transfer_in_count,daily_transfer_in_value,");
                insertBuff.append("  weekly_transfer_in_count,weekly_transfer_in_value,");
                insertBuff.append("  monthly_transfer_in_count,monthly_transfer_in_value,");
                insertBuff.append("  daily_transfer_out_count,daily_transfer_out_value,");
                insertBuff.append("  weekly_transfer_out_count,weekly_transfer_out_value,");
                insertBuff.append("  monthly_transfer_out_count,monthly_transfer_out_value,");
                insertBuff.append("  daily_subscriber_out_count , daily_subscriber_out_value,");
                insertBuff.append("  weekly_subscriber_out_count , weekly_subscriber_out_value,");
                insertBuff.append("  monthly_subscriber_out_count , monthly_subscriber_out_value,");
                insertBuff.append("  outside_daily_in_count,outside_daily_in_value,");
                insertBuff.append("  outside_weekly_in_count,outside_weekly_in_value,");
                insertBuff.append("  outside_monthly_in_count,outside_monthly_in_value,");
                insertBuff.append("  outside_daily_out_count,outside_daily_out_value,");
                insertBuff.append("  outside_weekly_out_count,outside_weekly_out_value,");
                insertBuff.append("  outside_monthly_out_count,outside_monthly_out_value, ");
                insertBuff.append("  created_by,created_on,modified_by,modified_on,network_code,category_code, ");
                insertBuff.append("alt_daily_transfer_in_count, alt_daily_transfer_in_value, ");
                insertBuff.append("alt_weekly_transfer_in_count, alt_weekly_transfer_in_value, ");
                insertBuff.append("alt_monthly_transfer_in_count, alt_monthly_transfer_in_value, ");
                insertBuff.append("alt_daily_transfer_out_count, alt_daily_transfer_out_value, ");
                insertBuff.append("alt_weekly_transfer_out_count, alt_weekly_transfer_out_value, ");
                insertBuff.append("alt_monthly_transfer_out_count, alt_monthly_transfer_out_value, ");
                insertBuff.append("alt_outside_daily_in_count, alt_outside_daily_in_value, ");
                insertBuff.append("alt_outside_weekly_in_count, alt_outside_weekly_in_value, ");
                insertBuff.append("alt_outside_monthly_in_count, alt_outside_monthly_in_value, ");
                insertBuff.append("alt_outside_daily_out_count, alt_outside_daily_out_value, ");
                insertBuff.append("alt_outside_weekly_out_count, alt_outside_weekly_out_value, ");
                insertBuff.append("alt_outside_monthly_out_count, alt_outside_monthly_out_value, ");
                insertBuff.append("alt_daily_subs_out_count, alt_daily_subs_out_value, alt_weekly_subs_out_count, ");
                insertBuff.append("alt_weekly_subs_out_value, alt_monthly_subs_out_count, ");
                insertBuff.append("alt_monthly_subs_out_value, parent_profile_id, is_default ,");

                // 6.4 changes
                insertBuff.append(" daily_subscriber_in_count, daily_subscriber_in_value,weekly_subscriber_in_count, ");
                insertBuff.append(" weekly_subscriber_in_value,  monthly_subscriber_in_count, monthly_subscriber_in_value, ");
                insertBuff.append("alt_daily_subs_in_count, alt_daily_subs_in_value, ");
                insertBuff.append("alt_weekly_subs_in_count, alt_weekly_subs_in_value, ");
                insertBuff.append("alt_monthly_subs_in_count, alt_monthly_subs_in_value) ");
                insertBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ");
                insertBuff.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insertQuery = insertBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
                }
                // commented for DB2 pstmtInsert= (OraclePreparedStatement)
                pstmtInsert = p_con.prepareStatement(insertBuff.toString());
                int i = 1;
                pstmtInsert.setString(i, String.valueOf(p_profileID));
                i++;
                // commented for DB2pstmtInsert.setFormOfUse(i,
                pstmtInsert.setString(i, p_profileVO.getShortName());
                i++;
                // commented for DB2 pstmtInsert.setFormOfUse(i,
                pstmtInsert.setString(i, p_profileVO.getProfileName());
                i++;
                pstmtInsert.setString(i, p_profileVO.getStatus());
                i++;
                // commented for DB2 pstmtInsert.setFormOfUse(i,
                pstmtInsert.setString(i, p_profileVO.getDescription());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailyInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailyInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailyOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailyOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyOutValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyOutCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyOutValue());
                i++;
                pstmtInsert.setString(i, p_profileVO.getCreatedBy());
                i++;
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_profileVO.getCreatedOn()));
                i++;
                pstmtInsert.setString(i, p_profileVO.getModifiedBy());
                i++;
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_profileVO.getModifiedOn()));
                i++;
                pstmtInsert.setString(i, p_profileVO.getNetworkCode());
                i++;
                pstmtInsert.setString(i, p_profileVO.getCategory());
                i++;
                // alerting count and variables
                pstmtInsert.setLong(i, p_profileVO.getDailyInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailyInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailyOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailyOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklyOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlyOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlDailyOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlWeeklyOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getUnctrlMonthlyOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberOutAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberOutAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberOutAltValue());
                i++;
                pstmtInsert.setString(i, p_profileVO.getParentProfileID());
                i++;
                pstmtInsert.setString(i, p_profileVO.getIsDefault());
                i++;
                // 6.4 changes
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberInCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberInValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getDailySubscriberInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getWeeklySubscriberInAltValue());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberInAltCount());
                i++;
                pstmtInsert.setLong(i, p_profileVO.getMonthlySubscriberInAltValue());
                i++;
                updateCount = pstmtInsert.executeUpdate();
            } else {
                throw new BTSLBaseException(this, methodName, "error.transferprofileid.sql.exist");
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[addTransferControlProfile]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[addTransferControlProfile]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
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
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method for modify the transfer controL profile detail in transfer_profile
     * table.
     * Method :modifyTransferControlProfile
     * param p_con java.sql.Connection
     * _profileVO ProfileVO
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     * @throws BTSLBaseException
     *             modified by Manisha: modify default profile details at the
     *             time of modifying profile details
     */
    public int modifyTransferControlProfile(Connection p_con, TransferProfileVO p_profileVO) throws BTSLBaseException {
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final String methodName = "modifyTransferControlProfile";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_profileVO= " + p_profileVO.toString());
        }
        try {
            final StringBuilder updateBuff = new StringBuilder();
            updateBuff.append("UPDATE  transfer_profile ");
            updateBuff.append(" SET short_name=?,profile_name=?,status=?,description=?,");
            updateBuff.append("  daily_transfer_in_count=?,daily_transfer_in_value=?,");
            updateBuff.append("  weekly_transfer_in_count=?,weekly_transfer_in_value=?,");
            updateBuff.append("  monthly_transfer_in_count=?,monthly_transfer_in_value=?,");
            updateBuff.append("  daily_transfer_out_count=?,daily_transfer_out_value=?,");
            updateBuff.append("  weekly_transfer_out_count=?,weekly_transfer_out_value=?,");
            updateBuff.append("  monthly_transfer_out_count=?,monthly_transfer_out_value=?,");
            updateBuff.append("  daily_subscriber_out_count=? , daily_subscriber_out_value=?,");
            updateBuff.append("  weekly_subscriber_out_count=? , weekly_subscriber_out_value=?,");
            updateBuff.append("  monthly_subscriber_out_count=? , monthly_subscriber_out_value=?,");
            updateBuff.append("  outside_daily_in_count=?,outside_daily_in_value=?,");
            updateBuff.append("  outside_weekly_in_count=?,outside_weekly_in_value=?,");
            updateBuff.append("  outside_monthly_in_count=?,outside_monthly_in_value=?,");
            updateBuff.append("  outside_daily_out_count=?,outside_daily_out_value=?,");
            updateBuff.append("  outside_weekly_out_count=?,outside_weekly_out_value=?,");
            updateBuff.append("  outside_monthly_out_count=?,outside_monthly_out_value=?, ");
            updateBuff.append("  modified_by=?,modified_on=?,alt_daily_transfer_in_count=? , ");
            updateBuff.append("alt_daily_transfer_in_value=? , alt_weekly_transfer_in_count=? , ");
            updateBuff.append("alt_weekly_transfer_in_value=? , alt_monthly_transfer_in_count=? , ");
            updateBuff.append("alt_monthly_transfer_in_value=? , alt_daily_transfer_out_count=? , ");
            updateBuff.append("alt_daily_transfer_out_value=? , alt_weekly_transfer_out_count=? , ");
            updateBuff.append("alt_weekly_transfer_out_value=? , alt_monthly_transfer_out_count=? , ");
            updateBuff.append("alt_monthly_transfer_out_value=? , alt_outside_daily_in_count=? , ");
            updateBuff.append("alt_outside_daily_in_value=? , alt_outside_weekly_in_count=? , ");
            updateBuff.append("alt_outside_weekly_in_value=? , alt_outside_monthly_in_count=? , ");
            updateBuff.append("alt_outside_monthly_in_value=? , alt_outside_daily_out_count=? , ");
            updateBuff.append("alt_outside_daily_out_value=? , alt_outside_weekly_out_count=? , ");
            updateBuff.append("alt_outside_weekly_out_value=? , alt_outside_monthly_out_count=? , ");
            updateBuff.append("alt_outside_monthly_out_value=? , alt_daily_subs_out_count=? , ");
            updateBuff.append("alt_daily_subs_out_value=? , alt_weekly_subs_out_count=? , ");
            updateBuff.append("alt_weekly_subs_out_value=? , alt_monthly_subs_out_count=? , ");
            updateBuff.append("alt_monthly_subs_out_value=?, is_default=?,  ");

            // 6.4 changes
            updateBuff.append(" daily_subscriber_in_count=?, daily_subscriber_in_value=?,  weekly_subscriber_in_count=?, ");
            updateBuff.append(" weekly_subscriber_in_value=?, monthly_subscriber_in_count=?, monthly_subscriber_in_value=?, ");
            updateBuff.append("alt_daily_subs_in_count=?, alt_daily_subs_in_value=?, ");
            updateBuff.append("alt_weekly_subs_in_count=?, alt_weekly_subs_in_value=?, ");
            updateBuff.append("alt_monthly_subs_in_count=?, alt_monthly_subs_in_value=? ");

            updateBuff.append("WHERE profile_id=? ");
            final String updateQuery = updateBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlUpdate:" + updateQuery);
            }
            // commented for DB2pstmtUpdate=
            pstmtUpdate = p_con.prepareStatement(updateBuff.toString());
            int i = 1;
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            pstmtUpdate.setString(i, p_profileVO.getShortName());
            i++;
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            pstmtUpdate.setString(i, p_profileVO.getProfileName());
            i++;
            pstmtUpdate.setString(i, p_profileVO.getStatus());
            i++;
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            pstmtUpdate.setString(i, p_profileVO.getDescription());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyOutValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyOutCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyOutValue());
            i++;
            pstmtUpdate.setString(i, p_profileVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_profileVO.getModifiedOn()));
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailyOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklyOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlyOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlDailyOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlWeeklyOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getUnctrlMonthlyOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberOutAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberOutAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberOutAltValue());
            i++;
            pstmtUpdate.setString(i, p_profileVO.getIsDefault());
            i++;
            // 6.4 changes
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberInCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberInValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getDailySubscriberInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getWeeklySubscriberInAltValue());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberInAltCount());
            i++;
            pstmtUpdate.setLong(i, p_profileVO.getMonthlySubscriberInAltValue());
            i++;
            //

            pstmtUpdate.setString(i, p_profileVO.getProfileId());
            i++;
            final boolean isRecordModified = isRecordModified(p_con, p_profileVO.getLastModifiedTime(), p_profileVO.getProfileId());
            if (isRecordModified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        }// end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[modifyTransferControlProfile]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[modifyTransferControlProfile]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method : isRecordModified
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_oldlastModified
     *            long
     * @param p_profileID
     *            String
     * @return modified boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_profileID) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_profileID=" + p_profileID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuilder selectQuery = new StringBuilder("SELECT modified_on FROM transfer_profile");
        selectQuery.append("  WHERE profile_id=? ");
        Long newlastModified = 0l;
        try {
            if (p_oldlastModified == 0) {
                return false;
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect" + selectQuery);
            }
            final String query = selectQuery.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_profileID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on").getTime();
                // The record is not present because the record is modified by
                // other person and the
                // modification is done on the value of the primary key.
            } else {
                modified = true;
                return true;
            }
            if (newlastModified != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[isRecordModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[isRecordModified]", "", "", "",
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
     * Method for delete the transfer control profile from transfer_profile .
     * Method :deleteTransferControlProfile
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_profileID
     *            java.lang.String
     * @param p_profileVO
     *            ProfileVO
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int deleteTransferControlProfile(Connection p_con, String p_profileID, TransferProfileVO p_profileVO) throws BTSLBaseException {
        PreparedStatement pstmtDelet = null;
        final String methodName = "deleteTransferControlProfile";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_profileID=" + p_profileID + ",p_profileVO=" + p_profileVO.toString());
        }
        final StringBuilder deleteBuff = new StringBuilder("UPDATE transfer_profile  SET status=?,modified_on=?,modified_by=? ");
        deleteBuff.append(" WHERE profile_id=? ");
        int updateCount = 0;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlDelete:=" + deleteBuff);
            }
            pstmtDelet = p_con.prepareStatement(deleteBuff.toString());
            pstmtDelet.setString(1, PretupsI.TRANSFER_PROFILE_STATUS_DELETE);
            pstmtDelet.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_profileVO.getModifiedOn()));
            pstmtDelet.setString(3, p_profileVO.getModifiedBy());
            pstmtDelet.setString(4, p_profileID);
            final boolean isRecordModified = isRecordModified(p_con, p_profileVO.getLastModifiedTime(), p_profileID);
            if (isRecordModified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtDelet.executeUpdate();
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[deleteTransferControlProfile]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[deleteTransferControlProfile]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtDelet != null) {
                    pstmtDelet.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method for Modify the transfer control profile product balance.
     * Method :modifyTransferControlProfileProduct
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_transferBalanceList
     *            ArrayList
     * @param p_profileID
     *            String
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int modifyTransferControlProfileProduct(Connection p_con, ArrayList p_transferBalanceList, String p_profileID) throws BTSLBaseException {
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtDelet = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        TransferProfileProductVO transferProfileProductVO = null;
        final String methodName = "modifyTransferControlProfileProduct";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entring: p_transferBalanceList=" + p_transferBalanceList.size() + ",p_profileID=" + p_profileID);
        }
       
        String insertQuery=transferProfileWebQry.modifyTransferControlProfileProductQry();
        final StringBuilder deleteBuff = new StringBuilder("DELETE FROM transfer_profile_products WHERE");
        deleteBuff.append(" profile_id=? ");
        int updateCount = 0;
        final StringBuilder selectBuff = new StringBuilder("SELECT 1 FROM transfer_profile_products WHERE profile_id=? ");

        try {
            int listSize = 0;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect:=" + selectBuff.toString());
            }
            if (p_transferBalanceList.size() > 0) {
                listSize = p_transferBalanceList.size();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlDelete:=" + deleteBuff.toString());
            }
            pstmtSelect = p_con.prepareStatement(selectBuff.toString());
            pstmtSelect.clearParameters();
            pstmtSelect.setString(1, p_profileID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                pstmtDelet = p_con.prepareStatement(deleteBuff.toString());
                pstmtDelet.setString(1, p_profileID);
                final int count = pstmtDelet.executeUpdate();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlInsert:=" + insertQuery);
            }

            // commented for DB2 pstmtInsert= (OraclePreparedStatement)
            pstmtInsert = p_con.prepareStatement(insertQuery);
            for (int i = 0; i < listSize; i++) {
                transferProfileProductVO = (TransferProfileProductVO) p_transferBalanceList.get(i);
                pstmtInsert.setString(1, p_profileID);
                pstmtInsert.setString(2, transferProfileProductVO.getProductCode());
                pstmtInsert.setLong(3, PretupsBL.getSystemAmount(transferProfileProductVO.getMinBalance()));
                pstmtInsert.setLong(4, PretupsBL.getSystemAmount(transferProfileProductVO.getMaxBalance()));
                pstmtInsert.setLong(5, PretupsBL.getSystemAmount(transferProfileProductVO.getAltBalance()));
                pstmtInsert.setString(6, transferProfileProductVO.getAllowedMaxPercentage());
                pstmtInsert.setLong(7, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMinTxnAmt()));
                pstmtInsert.setLong(8, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMaxTxnAmt()));
                updateCount = pstmtInsert.executeUpdate();
                pstmtInsert.clearParameters();
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[ modifyTransferControlProfileProduct]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[ modifyTransferControlProfileProduct]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
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
                if (pstmtDelet != null) {
                    pstmtDelet.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method :loadTransferProfileDetailList
     * this method load the transfer profile List from transfer_profile table
     * behalf of category_code and active status
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            java.lang.String
     * @param p_parentProfileID
     *            String
     * @param p_network_code
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferProfileDetailList(Connection p_con, String p_categoryCode, String p_parentProfileID, String p_network_code) throws BTSLBaseException {
        final String methodName = "loadTransferProfileDetailList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_categoryCode" + p_categoryCode + ", p_parentProfileID=" + p_parentProfileID + ",p_network_code=" + p_network_code);
        }
        final ArrayList transferProlfilList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        TransferProfileVO profileVO = null;
        final StringBuilder strBuff = new StringBuilder("SELECT TP.profile_id,TP.profile_name ,TP.description,TP.status, ");
        strBuff.append("TP.short_name,TP.network_code,TP.category_code ,TP.modified_on ,LK.lookup_name,TP.is_default ");
        strBuff.append("FROM transfer_profile TP,lookups LK  ");
        strBuff.append("WHERE TP.category_code =? AND TP.PARENT_PROFILE_ID=? AND TP.status <>?  AND LK.lookup_code=TP.status AND ");
        strBuff.append("LK.lookup_type=? AND TP.network_code=? ");
        strBuff.append("ORDER BY TP.profile_id");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, p_categoryCode);
            i++;
            pstmtSelect.setString(i, p_parentProfileID);
            i++;
            pstmtSelect.setString(i, PretupsI.TRANSFER_PROFILE_STATUS_DELETE);
            i++;
            pstmtSelect.setString(i, PretupsI.STATUS_TYPE);
            i++;
            pstmtSelect.setString(i, p_network_code);
            i++;
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                profileVO = new TransferProfileVO();
                profileVO.setProfileId(rs.getString("profile_id"));
                profileVO.setProfileName(rs.getString("profile_name"));
                profileVO.setDescription(rs.getString("description"));
                profileVO.setShortName(rs.getString("short_name"));
                profileVO.setStatus(rs.getString("lookup_name"));
                profileVO.setNetworkCode(rs.getString("network_code"));
                profileVO.setCategory(rs.getString("category_code"));
                profileVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                profileVO.setIsDefault(rs.getString("is_default"));
                final LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, profileVO.getIsDefault());
                profileVO.setIsDefaultDesc(lookupsVO.getLookupName());
                transferProlfilList.add(profileVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[loadTransferProfileDetailList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[loadTransferProfileDetailList]",
                "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: transferProlfilList size =" + transferProlfilList.size());
            }
        }
        return transferProlfilList;
    }

    /**
     * Method for checking Is Transfer Profile Name already exist or not.
     * Method: isTransferProfileNameExist
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_profileName
     *            java.lang.String
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferProfileNameExist(Connection p_con, String p_profileName) throws BTSLBaseException {
        final String methodName = "isTransferProfileNameExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_shortName=" + p_profileName + ".........");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder("SELECT 1  FROM transfer_profile WHERE  profile_name= ?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            // commented for DB2 pstmt
            pstmt = p_con.prepareStatement(sqlSelect);
            // commented for DB2
            pstmt.setString(1, p_profileName.trim());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[isTransferProfileNameExist]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[isTransferProfileNameExist]",
                "", "", "", "Exception:" + ex.getMessage());
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
        return existFlag;
    }

    /**
     * Method for checking Is Transfer Profile short name already exist or not.
     * Method: isTransferProfileShortNameExist
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_shortName
     *            java.lang.String
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferProfileShortNameExist(Connection p_con, String p_shortName) throws BTSLBaseException {
        final String methodName = "isTransferProfileShortNameExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_shortName=" + p_shortName);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder("SELECT 1  FROM transfer_profile WHERE  short_name= ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            // commented for DB2 pstmt
            pstmt = p_con.prepareStatement(sqlSelect);
            // commented for DB2
            pstmt.setString(1, p_shortName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileShortNameExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileShortNameExist]", "", "", "", "Exception:" + ex.getMessage());
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
        return existFlag;
    }

    /**
     * if any profile default profile and user is making any other profile as
     * default profile then first make
     * previous profile not default profile of category
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int updateDefaultProfile(Connection p_con, String p_categoryCode, String p_networkCode) throws BTSLBaseException {
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updateDefaultProfile";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_categoryCode= " + p_categoryCode + " p_networkCode: " + p_networkCode);
        }
        try {
            final String updateQuery = "UPDATE TRANSFER_PROFILE set is_default=? WHERE network_code=? AND category_code=? AND is_default=? ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlUpdate:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            int i = 1;
            pstmtUpdate.setString(i, PretupsI.NO);
            i++;
            pstmtUpdate.setString(i, p_networkCode);
            i++;
            pstmtUpdate.setString(i, p_categoryCode);
            i++;
            pstmtUpdate.setString(i, PretupsI.YES);
            i++;
            updateCount = pstmtUpdate.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[updateDefaultProfile]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[updateDefaultProfile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method for insert the transfer control profile product balance in
     * transfer_profile_product.
     * Method : addTransferControlProfileProduct
     * param p_con java.sql.Connection
     * _transferControlList java.util.ArrayList
     * 
     * @param p_profileID
     *            long
     * @return addCount int
     * @throws BTSLBaseException
     * @throws BTSLBaseException
     */
    public int addTransferControlProfileProduct(Connection p_con, ArrayList p_transferControlList, long p_profileID) throws BTSLBaseException {
        final String methodName = "addTransferControlProfileProduct";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_transferControlList= " + p_transferControlList + ",p_profileID=" + p_profileID);
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertBuff = new StringBuilder("INSERT INTO transfer_profile_products (profile_id,");
        insertBuff.append(" product_code,min_residual_balance,max_balance,alerting_balance, ");
        insertBuff.append(" max_pct_transfer_allowed,c2s_min_txn_amt, c2s_max_txn_amt)");
        insertBuff.append(" VALUES(?,?,?,?,?,?,?,?)");
        final String insertQuery = insertBuff.toString();
        TransferProfileProductVO transferProfileProductVO = null;
        int addCount = 0;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlInsert:=" + insertQuery + ",p_transferControlList=" + p_transferControlList.size() + ",p_profileID=" + p_profileID);
            }
            int listSize = 0;
            if (p_transferControlList.size() > 0) {
                listSize = p_transferControlList.size();
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            for (int i = 0; i < listSize; i++) {
                transferProfileProductVO = (TransferProfileProductVO) p_transferControlList.get(i);
                pstmtInsert.setString(1, String.valueOf(p_profileID));
                pstmtInsert.setString(2, transferProfileProductVO.getProductCode());
                pstmtInsert.setLong(3, PretupsBL.getSystemAmount(transferProfileProductVO.getMinBalance()));
                pstmtInsert.setLong(4, PretupsBL.getSystemAmount(transferProfileProductVO.getMaxBalance()));
                pstmtInsert.setLong(5, PretupsBL.getSystemAmount(transferProfileProductVO.getAltBalance()));
                pstmtInsert.setLong(6, (Long.parseLong(transferProfileProductVO.getAllowedMaxPercentage())));
                pstmtInsert.setLong(7, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMinTxnAmt()));
                pstmtInsert.setLong(8, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMaxTxnAmt()));
                addCount = pstmtInsert.executeUpdate();
                pstmtInsert.clearParameters();
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[addTransferControlProfileProduct]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, " addTransferControlProfileProduct", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[addTransferControlProfileProduct]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally
        return addCount;
    }

    /**
     * Method for checking Is Transfer Profile short name already exist or not.
     * for modifying transfer profile
     * Method: isTransferProfileShortNameExistForModify
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_shortName
     *            java.lang.String
     * @param p_profileID
     *            java.lang.String
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferProfileShortNameExistForModify(Connection p_con, String p_shortName, String p_profileID) throws BTSLBaseException {
        final String methodName = "isTransferProfileShortNameExistForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_shortName=" + p_shortName + "...p_profileID=" + p_profileID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder("SELECT 1  FROM transfer_profile WHERE  short_name= ? AND profile_id <>? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_shortName);
            pstmt.setString(2, p_profileID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileShortNameExistForModify]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileShortNameExistForModify]", "", "", "", "Exception:" + ex.getMessage());
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
        return existFlag;
    }

    /**
     * Method for checking Is Transfer Profile Name already exist or not.
     * Method: isTransferProfileNameExistForModify
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_profileName
     *            java.lang.String
     * @param p_profileID
     *            java.lang.String
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferProfileNameExistForModify(Connection p_con, String p_profileName, String p_profileID) throws BTSLBaseException {
        final String methodName = "isTransferProfileNameExistForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_shortName=" + p_profileName + "..p_profileID=" + p_profileID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder("SELECT 1  FROM transfer_profile WHERE  profile_name= ? AND profile_id!=? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            // commented for DB2 pstmt
            pstmt = p_con.prepareStatement(sqlSelect);
            // commented for DB2
            pstmt.setString(1, p_profileName.trim());
            pstmt.setString(2, p_profileID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileNameExistForModify]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileNameExistForModify]", "", "", "", "Exception:" + ex.getMessage());
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
        return existFlag;
    }

    /**
     * 
     * @param p_con
     * @param p_categoryCode
     * @param p_networkCode
     *            String
     * @param p_parentProfileID
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferProfileExistForCategoryCode(Connection p_con, String p_categoryCode, String p_networkCode, String p_parentProfileID) throws BTSLBaseException {
        final String methodName = "isTransferProfileExistForCategoryCode";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:  p_categoryCode=" + p_categoryCode + ",p_networkCode=" + p_networkCode + ",p_parentProfileID=" + p_parentProfileID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isExist = false;
        final StringBuilder strBuff = new StringBuilder("SELECT 1  FROM  transfer_profile WHERE ");
        strBuff.append(" category_code=?  AND network_code = ? AND status <>'N' AND parent_profile_id=? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_parentProfileID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileExistForCategoryCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[isTransferProfileExistForCategoryCode]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: isExist=" + isExist);
            }
        }
        return isExist;
    }
    
    
    
    
    public ArrayList loadTransferProfileDetailListByStatus(Connection p_con, String p_categoryCode, String p_parentProfileID, String p_network_code,String status) throws BTSLBaseException {
        final String methodName = "loadTransferProfileDetailListByStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_categoryCode" + p_categoryCode + ", p_parentProfileID=" + p_parentProfileID + ",p_network_code=" + p_network_code);
        }
        final ArrayList transferProlfilList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        TransferProfileVO profileVO = null;
        final StringBuilder strBuff = new StringBuilder("SELECT TP.profile_id,TP.profile_name ,TP.description,TP.status, ");
        strBuff.append("TP.short_name,TP.network_code,TP.category_code ,TP.modified_on ,LK.lookup_name,TP.is_default ");
        strBuff.append("FROM transfer_profile TP,lookups LK  ");
        strBuff.append("WHERE TP.category_code =? AND TP.PARENT_PROFILE_ID=? AND TP.status = ?  AND LK.lookup_code=TP.status AND ");
        strBuff.append("LK.lookup_type=? AND TP.network_code=? ");
        strBuff.append("ORDER BY TP.profile_id");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, p_categoryCode);
            i++;
            pstmtSelect.setString(i, p_parentProfileID);
            i++;
            pstmtSelect.setString(i, status);
            i++;
            pstmtSelect.setString(i, PretupsI.STATUS_TYPE);
            i++;
            pstmtSelect.setString(i, p_network_code);
            i++;
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                profileVO = new TransferProfileVO();
                profileVO.setProfileId(rs.getString("profile_id"));
                profileVO.setProfileName(rs.getString("profile_name"));
                profileVO.setDescription(rs.getString("description"));
                profileVO.setShortName(rs.getString("short_name"));
                profileVO.setStatus(rs.getString("lookup_name"));
                profileVO.setNetworkCode(rs.getString("network_code"));
                profileVO.setCategory(rs.getString("category_code"));
                profileVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                profileVO.setIsDefault(rs.getString("is_default"));
                final LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, profileVO.getIsDefault());
                profileVO.setIsDefaultDesc(lookupsVO.getLookupName());
                transferProlfilList.add(profileVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[loadTransferProfileDetailList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[loadTransferProfileDetailList]",
                "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: transferProlfilList size =" + transferProlfilList.size());
            }
        }
        return transferProlfilList;
    }



    /**
     * Method for insert the transfer control profile product balance in
     * transfer_profile_product.
     * Method : addTransferControlProfileProduct
     * param p_con java.sql.Connection
     * _transferControlList java.util.ArrayList
     * 
     * @param p_profileID
     *            long
     * @return addCount int
     * @throws BTSLBaseException
     * @throws BTSLBaseException
     */
    public int addTransferControlProfileProductVOs(Connection p_con, ArrayList p_transferControlList, long p_profileID) throws BTSLBaseException {
        final String methodName = "addTransferControlProfileProduct";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_transferControlList= " + p_transferControlList + ",p_profileID=" + p_profileID);
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertBuff = new StringBuilder("INSERT INTO transfer_profile_products (profile_id,");
        insertBuff.append(" product_code,min_residual_balance,max_balance,alerting_balance, ");
        insertBuff.append(" max_pct_transfer_allowed,c2s_min_txn_amt, c2s_max_txn_amt)");
        insertBuff.append(" VALUES(?,?,?,?,?,?,?,?)");
        final String insertQuery = insertBuff.toString();
        TransferProfileProductReqVO transferProfileProductVO = null;
        int addCount = 0;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlInsert:=" + insertQuery + ",p_transferControlList=" + p_transferControlList.size() + ",p_profileID=" + p_profileID);
            }
            int listSize = 0;
            if (p_transferControlList.size() > 0) {
                listSize = p_transferControlList.size();
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            for (int i = 0; i < listSize; i++) {
                transferProfileProductVO = (TransferProfileProductReqVO) p_transferControlList.get(i);
                pstmtInsert.setString(1, String.valueOf(p_profileID));
                pstmtInsert.setString(2, transferProfileProductVO.getProductCode());
                pstmtInsert.setLong(3, PretupsBL.getSystemAmount(transferProfileProductVO.getMinBalance()));
                pstmtInsert.setLong(4, PretupsBL.getSystemAmount(transferProfileProductVO.getMaxBalance()));
                pstmtInsert.setLong(5, PretupsBL.getSystemAmount(transferProfileProductVO.getAltBalance()));
                pstmtInsert.setLong(6, (Long.parseLong(transferProfileProductVO.getAllowedMaxPercentage())));
                pstmtInsert.setLong(7, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMinTxnAmt()));
                pstmtInsert.setLong(8, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMaxTxnAmt()));
                addCount = pstmtInsert.executeUpdate();
                pstmtInsert.clearParameters();
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[addTransferControlProfileProduct]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, " addTransferControlProfileProduct", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[addTransferControlProfileProduct]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally
        return addCount;
    }
    
    
    
    
    
    
    /**
     * Method for Modify the transfer control profile product balance.
     * Method :modifyTransferControlProfileProduct
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_transferBalanceList
     *            ArrayList
     * @param p_profileID
     *            String
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int modifyTransferControlProfileProductBallist(Connection p_con, ArrayList p_transferBalanceList, String p_profileID) throws BTSLBaseException {
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtDelet = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        TransferProfileProductReqVO transferProfileProductVO = null;
        final String methodName = "modifyTransferControlProfileProduct";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entring: p_transferBalanceList=" + p_transferBalanceList.size() + ",p_profileID=" + p_profileID);
        }
       
        String insertQuery=transferProfileWebQry.modifyTransferControlProfileProductQry();
        final StringBuilder deleteBuff = new StringBuilder("DELETE FROM transfer_profile_products WHERE");
        deleteBuff.append(" profile_id=? ");
        int updateCount = 0;
        final StringBuilder selectBuff = new StringBuilder("SELECT 1 FROM transfer_profile_products WHERE profile_id=? ");

        try {
            int listSize = 0;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect:=" + selectBuff.toString());
            }
            if (p_transferBalanceList.size() > 0) {
                listSize = p_transferBalanceList.size();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlDelete:=" + deleteBuff.toString());
            }
            pstmtSelect = p_con.prepareStatement(selectBuff.toString());
            pstmtSelect.clearParameters();
            pstmtSelect.setString(1, p_profileID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                pstmtDelet = p_con.prepareStatement(deleteBuff.toString());
                pstmtDelet.setString(1, p_profileID);
                final int count = pstmtDelet.executeUpdate();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlInsert:=" + insertQuery);
            }

            // commented for DB2 pstmtInsert= (OraclePreparedStatement)
            pstmtInsert = p_con.prepareStatement(insertQuery);
            for (int i = 0; i < listSize; i++) {
                transferProfileProductVO = (TransferProfileProductReqVO) p_transferBalanceList.get(i);
                pstmtInsert.setString(1, p_profileID);
                pstmtInsert.setString(2, transferProfileProductVO.getProductCode());
                pstmtInsert.setLong(3, PretupsBL.getSystemAmount(transferProfileProductVO.getMinBalance()));
                pstmtInsert.setLong(4, PretupsBL.getSystemAmount(transferProfileProductVO.getMaxBalance()));
                pstmtInsert.setLong(5, PretupsBL.getSystemAmount(transferProfileProductVO.getAltBalance()));
                pstmtInsert.setString(6, transferProfileProductVO.getAllowedMaxPercentage());
                pstmtInsert.setLong(7, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMinTxnAmt()));
                pstmtInsert.setLong(8, PretupsBL.getSystemAmount(transferProfileProductVO.getC2sMaxTxnAmt()));
                updateCount = pstmtInsert.executeUpdate();
                pstmtInsert.clearParameters();
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[ modifyTransferControlProfileProduct]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileWebDAO[ modifyTransferControlProfileProduct]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
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
                if (pstmtDelet != null) {
                    pstmtDelet.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }
    
    
    
    
    public Long getLastModifiedTimeinmilliseconds(Connection p_con,String p_profileID) throws BTSLBaseException {
        final String methodName = "getLastModifiedTimeinmilliseconds";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_oldlastModified=,p_profileID=" + p_profileID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuilder selectQuery = new StringBuilder("SELECT modified_on FROM transfer_profile");
        selectQuery.append("  WHERE profile_id=? ");
        Long newlastModified = 0l;
        try {
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect" + selectQuery);
            }
            final String query = selectQuery.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_profileID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on").getTime();
            } 
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[getLastModifiedTimeinmilliseconds]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[getLastModifiedTimeinmilliseconds]", "", "", "",
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
        return newlastModified;
    }// end recordModified



    public boolean isTransferProfileIDExist(Connection p_con, String p_profileID) throws BTSLBaseException {
        final String methodName = "isTransferProfileIDExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_shortName=" + p_profileID + ".........");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder("SELECT 1  FROM transfer_profile WHERE  profile_id= ?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            // commented for DB2 pstmt
            pstmt = p_con.prepareStatement(sqlSelect);
            // commented for DB2
            pstmt.setString(1, p_profileID.trim());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[isTransferProfileNameExist]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileWebDAO[isTransferProfileNameExist]",
                "", "", "", "Exception:" + ex.getMessage());
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
        return existFlag;
    }

    
}
