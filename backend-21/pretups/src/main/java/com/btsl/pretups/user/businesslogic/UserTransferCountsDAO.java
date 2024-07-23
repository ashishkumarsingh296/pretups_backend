package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOTFCountsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class UserTransferCountsDAO {

    // Instance of LOG
    private static final Log LOG = LogFactory.getLog(UserTransferCountsDAO.class.getName());

    /**
     * Method loadTransferCounts()
     * This method Load the Transfer Counts by the userID.
     * 
     * @param p_con
     * @param p_userId
     * @param p_isLockRecordForUpdate
     *            boolean
     * @return UserTransferCountsVO
     * @throws BTSLBaseException
     *             Added p_isLockRecordForUpdate so that same query can be used
     *             just before updating the records also : Gurjeet
     */
    public UserTransferCountsVO loadTransferCounts(Connection p_con, String p_userId, boolean p_isLockRecordForUpdate) throws BTSLBaseException {

        final String METHOD_NAME = "loadTransferCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered  From UserID " + p_userId + " p_isLockRecordForUpdate=" + p_isLockRecordForUpdate);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        strBuff.append(" monthly_in_value, daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, ");
        strBuff.append(" monthly_out_count, monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
        strBuff.append(" outside_weekly_in_count, outside_weekly_in_value, outside_monthly_in_count, ");
        strBuff.append(" outside_monthly_in_value,  ");
        strBuff.append("  outside_daily_out_count, outside_daily_out_value, outside_weekly_out_count, ");
        strBuff.append(" outside_weekly_out_value, outside_monthly_out_count, outside_monthly_out_value, ");
        strBuff.append(" daily_subscriber_out_count, weekly_subscriber_out_count, monthly_subscriber_out_count, ");
        strBuff.append(" daily_subscriber_out_value, weekly_subscriber_out_value, monthly_subscriber_out_value, ");
        strBuff.append(" outside_last_in_time, last_in_time, last_out_time, outside_last_out_time,last_transfer_id,last_transfer_date, ");

        strBuff.append(" daily_subscriber_in_count, weekly_subscriber_in_count, monthly_subscriber_in_count, ");
        strBuff.append(" daily_subscriber_in_value, weekly_subscriber_in_value, monthly_subscriber_in_value, daily_roam_amount,last_sos_txn_id,last_sos_txn_status ");
        strBuff.append(",last_lr_status,last_lr_txnid");
        strBuff.append(" FROM user_transfer_counts ");
        strBuff.append(" WHERE user_id = ? ");
        if (p_isLockRecordForUpdate) {

            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                strBuff.append(" FOR UPDATE with RS");
            } else {
                strBuff.append(" FOR UPDATE ");
            }
        }
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        UserTransferCountsVO countsVO = null;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                countsVO = new UserTransferCountsVO();
                countsVO.setUserID(rs.getString("user_id"));

                countsVO.setDailyInCount(rs.getLong("daily_in_count"));
                countsVO.setDailyInValue(rs.getLong("daily_in_value"));
                countsVO.setWeeklyInCount(rs.getLong("weekly_in_count"));
                countsVO.setWeeklyInValue(rs.getLong("weekly_in_value"));
                countsVO.setMonthlyInCount(rs.getLong("monthly_in_count"));
                countsVO.setMonthlyInValue(rs.getLong("monthly_in_value"));
                countsVO.setDailyOutCount(rs.getLong("daily_out_count"));
                countsVO.setDailyOutValue(rs.getLong("daily_out_value"));
                countsVO.setWeeklyOutCount(rs.getLong("weekly_out_count"));
                countsVO.setWeeklyOutValue(rs.getLong("weekly_out_value"));
                countsVO.setMonthlyOutCount(rs.getLong("monthly_out_count"));
                countsVO.setMonthlyOutValue(rs.getLong("monthly_out_value"));
                countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));
                countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));

                countsVO.setOutsideLastInTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("outside_last_in_time")));
                countsVO.setOutsideLastOutTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("outside_last_out_time")));
                countsVO.setLastInTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_in_time")));
                countsVO.setLastOutTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_out_time")));

                countsVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                countsVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                countsVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                countsVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                countsVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                countsVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));

                countsVO.setDailySubscriberInCount(rs.getLong("daily_subscriber_in_count"));
                countsVO.setDailySubscriberInValue(rs.getLong("daily_subscriber_in_value"));
                countsVO.setWeeklySubscriberInCount(rs.getLong("weekly_subscriber_in_count"));
                countsVO.setWeeklySubscriberInValue(rs.getLong("weekly_subscriber_in_value"));
                countsVO.setMonthlySubscriberInCount(rs.getLong("monthly_subscriber_in_count"));
                countsVO.setMonthlySubscriberInValue(rs.getLong("monthly_subscriber_in_value"));

                countsVO.setLastTransferID(rs.getString("last_transfer_id"));
                countsVO.setLastTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transfer_date")));
                countsVO.setDailyRoamAmount(rs.getLong("daily_roam_amount"));
                countsVO.setLastSOSTxnID(rs.getString("last_sos_txn_id"));
                countsVO.setLastSOSTxnStatus(rs.getString("last_sos_txn_status"));
                countsVO.setLastLrStatus(rs.getString("last_lr_status"));
                countsVO.setLastLRTxnID(rs.getString("last_lr_txnid"));

            }

        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(METHOD_NAME, "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",ex);
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:  UserTransferCountsVO =" + countsVO);
            }
        }
        return countsVO;
    }

    /**
     * update the user transferCounts
     * 
     * @param p_con
     * @param p_countsVO
     * @param p_exist
     * @return int
     * @throws BTSLBaseException
     */
    public int updateUserTransferCounts(Connection p_con, UserTransferCountsVO p_countsVO, boolean p_exist) throws BTSLBaseException {

        final String METHOD_NAME = "updateUserTransferCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered updateUserTransferCounts p_countsVO : " + p_countsVO + " p_exist " + p_exist + "p_countsVO.getUserID()" + p_countsVO.getUserID());
        }

         

        int updateCount = 0;
        try {

            final StringBuffer strBuffUpdate = new StringBuffer();

            if (p_exist) {
                strBuffUpdate.append(" UPDATE user_transfer_counts  SET ");
                strBuffUpdate.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?, monthly_in_count = ?, ");
                strBuffUpdate.append(" monthly_in_value = ?, daily_out_count = ?, daily_out_value = ?, weekly_out_count = ?, weekly_out_value = ?,  ");
                strBuffUpdate.append(" monthly_out_count = ? , monthly_out_value = ? , outside_daily_in_count = ?, outside_daily_in_value = ?, ");
                strBuffUpdate.append(" outside_weekly_in_count = ? , outside_weekly_in_value = ? , outside_monthly_in_count = ? , outside_monthly_in_value = ? , ");
                strBuffUpdate.append(" outside_daily_out_count = ?, ");
                strBuffUpdate.append(" outside_daily_out_value = ? , outside_weekly_out_count = ? , outside_weekly_out_value = ?, ");
                strBuffUpdate.append(" outside_monthly_out_count = ? , outside_monthly_out_value = ? , ");
                strBuffUpdate.append(" daily_subscriber_out_count = ? , weekly_subscriber_out_count = ? , monthly_subscriber_out_count = ?, ");
                strBuffUpdate.append(" daily_subscriber_out_value = ? , weekly_subscriber_out_value = ? , monthly_subscriber_out_value = ?, ");
                strBuffUpdate.append(" outside_last_in_time = ? , last_in_time = ? , last_out_time = ? , outside_last_out_time = ?,LAST_TRANSFER_ID=?,LAST_TRANSFER_DATE=?, ");
                strBuffUpdate.append(" daily_subscriber_in_count = ? , weekly_subscriber_in_count = ? , monthly_subscriber_in_count = ? , ");
                strBuffUpdate.append(" daily_subscriber_in_value = ? , weekly_subscriber_in_value = ? , monthly_subscriber_in_value = ? , daily_roam_amount=? ");
                strBuffUpdate.append("  WHERE user_id = ?  ");
            } else {
                strBuffUpdate.append(" INSERT INTO user_transfer_counts ( ");
                strBuffUpdate.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
                strBuffUpdate.append(" monthly_in_value, daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, ");
                strBuffUpdate.append(" monthly_out_count, monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
                strBuffUpdate.append(" outside_weekly_in_count, outside_weekly_in_value, outside_monthly_in_count, outside_monthly_in_value, ");
                strBuffUpdate.append(" outside_daily_out_count, ");
                strBuffUpdate.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
                strBuffUpdate.append(" outside_monthly_out_count, outside_monthly_out_value ,  ");
                strBuffUpdate.append(" daily_subscriber_out_count , weekly_subscriber_out_count , monthly_subscriber_out_count , ");
                strBuffUpdate.append(" daily_subscriber_out_value , weekly_subscriber_out_value , monthly_subscriber_out_value , ");
                strBuffUpdate.append(" outside_last_in_time, last_in_time, last_out_time, outside_last_out_time,LAST_TRANSFER_ID,LAST_TRANSFER_DATE,");
                strBuffUpdate.append(" daily_subscriber_in_count , weekly_subscriber_in_count , monthly_subscriber_in_count , ");
                strBuffUpdate.append(" daily_subscriber_in_value , weekly_subscriber_in_value , monthly_subscriber_in_value , daily_roam_amount, ");
                strBuffUpdate.append("  user_id  )  ");
                strBuffUpdate.append(" VALUES ");
                strBuffUpdate.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            }

            final String query = strBuffUpdate.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " query:" + query);
            }
            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            this.updateTransferCount(psmt, p_countsVO);
            updateCount = updateUserTransferUpdateCount(METHOD_NAME, psmt);
        }
        }catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } finally {
           
           LogFactory.printLog(METHOD_NAME, "Exiting Success :" + updateCount, LOG);
      
        }

        return updateCount;
    }

	private int updateUserTransferUpdateCount(final String METHOD_NAME,
			PreparedStatement psmt) throws SQLException, BTSLBaseException {
		int updateCount;
		updateCount = psmt.executeUpdate();
		if (updateCount == 0) {
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[updateUserTransferCounts]", "", "",
		                    "", "BTSLBaseException: update count <=0");
		    throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		}
		return updateCount;
	}

    /**
     * update counts
     * 
     * @param psmt
     * @param p_countsVO
     * @throws SQLException
     */
    private void updateTransferCount(PreparedStatement psmt, UserTransferCountsVO p_countsVO) throws SQLException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("updateUserTransferCounts", "Entered p_countsVO : " + p_countsVO);
        }

        int m = 0;
        psmt.setLong(++m, p_countsVO.getDailyInCount());
        psmt.setLong(++m, p_countsVO.getDailyInValue());
        psmt.setLong(++m, p_countsVO.getWeeklyInCount());
        psmt.setLong(++m, p_countsVO.getWeeklyInValue());
        psmt.setLong(++m, p_countsVO.getMonthlyInCount());
        psmt.setLong(++m, p_countsVO.getMonthlyInValue());
        psmt.setLong(++m, p_countsVO.getDailyOutCount());
        psmt.setLong(++m, p_countsVO.getDailyOutValue());
        psmt.setLong(++m, p_countsVO.getWeeklyOutCount());
        psmt.setLong(++m, p_countsVO.getWeeklyOutValue());
        psmt.setLong(++m, p_countsVO.getMonthlyOutCount());
        psmt.setLong(++m, p_countsVO.getMonthlyOutValue());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyInCount());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyInValue());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyInCount());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyInValue());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyInCount());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyInValue());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyOutCount());
        psmt.setLong(++m, p_countsVO.getUnctrlDailyOutValue());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyOutCount());
        psmt.setLong(++m, p_countsVO.getUnctrlWeeklyOutValue());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyOutCount());
        psmt.setLong(++m, p_countsVO.getUnctrlMonthlyOutValue());


        psmt.setLong(++m, p_countsVO.getDailySubscriberOutCount());
        psmt.setLong(++m, p_countsVO.getWeeklySubscriberOutCount());
        psmt.setLong(++m, p_countsVO.getMonthlySubscriberOutCount());
        psmt.setLong(++m, p_countsVO.getDailySubscriberOutValue());
        psmt.setLong(++m, p_countsVO.getWeeklySubscriberOutValue());
        psmt.setLong(++m, p_countsVO.getMonthlySubscriberOutValue());

        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getOutsideLastInTime()));
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getLastInTime()));
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getLastOutTime()));
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getOutsideLastOutTime()));

        psmt.setString(++m, p_countsVO.getLastTransferID());
        psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getLastTransferDate()));

        psmt.setLong(++m, p_countsVO.getDailySubscriberInCount());
        psmt.setLong(++m, p_countsVO.getWeeklySubscriberInCount());
        psmt.setLong(++m, p_countsVO.getMonthlySubscriberInCount());
        psmt.setLong(++m, p_countsVO.getDailySubscriberInValue());
        psmt.setLong(++m, p_countsVO.getWeeklySubscriberInValue());
        psmt.setLong(++m, p_countsVO.getMonthlySubscriberInValue());

        psmt.setLong(++m, p_countsVO.getDailyRoamAmount());
        
        psmt.setString(++m, p_countsVO.getUserID());
        if (LOG.isDebugEnabled()) {
            LOG.debug("updateUserTransferCounts", "Exiting :");
        }
    }

    // roam penalty

    public UserTransferCountsVO loadRoamTransferCount(Connection p_con, String p_userId, boolean p_isLockRecordForUpdate) throws BTSLBaseException {

        final String METHOD_NAME = "loadTransferCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered  From UserID " + p_userId);
        }
         
        
        UserTransferCountsVO userTransferCountsVO = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT  daily_roam_amount, user_id, last_transfer_date, last_transfer_id ");

        strBuff.append(" FROM user_transfer_counts ");
        strBuff.append(" WHERE user_id = ? ");
        if (p_isLockRecordForUpdate) {
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                strBuff.append(" FOR UPDATE with RS");
            } else {
                strBuff.append(" FOR UPDATE ");
            }
        }
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_userId);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                userTransferCountsVO = new UserTransferCountsVO();
                userTransferCountsVO.setDailyRoamAmount(rs.getLong("daily_roam_amount"));
                userTransferCountsVO.setUserID(rs.getString("user_id"));
                userTransferCountsVO.setLastTransferDate(rs.getTimestamp("last_transfer_date"));
                userTransferCountsVO.setLastTransferID(rs.getString("last_transfer_id"));

            }

        } 
        }catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadRoamTransferCount]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(METHOD_NAME, "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadRoamTransferCount]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",ex);
        } finally {
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:  userTransferCountsVO =" + userTransferCountsVO);
            }
        }
        return userTransferCountsVO;
    }

    /**
     * update the user transferCounts
     * 
     * @param p_con
     * @param p_countsVO
     * @param p_exist
     * @return int
     * @throws BTSLBaseException
     */
    public int updateUserTransferCountRoam(Connection p_con, UserTransferCountsVO p_countsVO, boolean p_exist) throws BTSLBaseException {

        final String METHOD_NAME = "updateUserTransferCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered updateUserTransferCounts p_countsVO : " + p_countsVO + " p_exist " + p_exist + "p_countsVO.getUserID()" + p_countsVO.getUserID());
        }

        

        int updateCount = 0;
        try {

            final StringBuffer strBuffUpdate = new StringBuffer();

            if (p_exist) {
                strBuffUpdate.append(" UPDATE user_transfer_counts  SET ");
                strBuffUpdate.append("LAST_TRANSFER_ID=?,LAST_TRANSFER_DATE=?, daily_roam_amount=? ");
                strBuffUpdate.append(" WHERE user_id = ?  ");
            } else {
                strBuffUpdate.append(" INSERT INTO user_transfer_counts ( ");
                strBuffUpdate.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
                strBuffUpdate.append(" monthly_in_value, daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, ");
                strBuffUpdate.append(" monthly_out_count, monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
                strBuffUpdate.append(" outside_weekly_in_count, outside_weekly_in_value, outside_monthly_in_count, outside_monthly_in_value, ");
                strBuffUpdate.append(" outside_daily_out_count, ");
                strBuffUpdate.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
                strBuffUpdate.append(" outside_monthly_out_count, outside_monthly_out_value ,  ");
                strBuffUpdate.append(" daily_subscriber_out_count , weekly_subscriber_out_count , monthly_subscriber_out_count , ");
                strBuffUpdate.append(" daily_subscriber_out_value , weekly_subscriber_out_value , monthly_subscriber_out_value , ");
                strBuffUpdate.append(" outside_last_in_time, last_in_time, last_out_time, outside_last_out_time,LAST_TRANSFER_ID,LAST_TRANSFER_DATE,");
                strBuffUpdate.append(" daily_subscriber_in_count , weekly_subscriber_in_count , monthly_subscriber_in_count , ");
                strBuffUpdate.append(" daily_subscriber_in_value , weekly_subscriber_in_value , monthly_subscriber_in_value , daily_roam_amount, ");
                strBuffUpdate.append("  user_id )  ");
                strBuffUpdate.append(" VALUES ");
                strBuffUpdate.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            }

            final String query = strBuffUpdate.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " query:" + query);
            }
            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            if (p_exist) {
                psmt.setString(1, p_countsVO.getLastTransferID());
                psmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_countsVO.getLastTransferDate()));
                psmt.setLong(3, p_countsVO.getDailyRoamAmount());
                psmt.setString(4, p_countsVO.getUserID());
            } else {
                this.updateTransferCount(psmt, p_countsVO);
            }
            updateCount = updateUserTransferUpdateCount(METHOD_NAME, psmt);
        } 
        }catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } finally {
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting Success :" + updateCount);
            }
        }

        return updateCount;
    }

    /**
     * @param receiverUserID
     * @param con
     * @param isLockRecordForUpdate
     * @return
     * @throws SQLException
     * @throws BTSLBaseException
     */
    public UserTransferCountsVO selectLastSOSTxnID(String receiverUserID, Connection con, Boolean isLockRecordForUpdate,String msisdn) throws BTSLBaseException {

        final String methodName = "selectLastSOSTxnID";
        LogFactory.printLog(methodName, "Entered  Receiver UserID " + receiverUserID, LOG);
         
        
        UserTransferCountsVO userTransferCountsVO = null;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT user_id, last_sos_txn_id, last_sos_txn_status  FROM user_transfer_counts  WHERE ");
        if(receiverUserID!=null){
        strBuff.append("user_id = ? ");	
        }else{
        	strBuff.append("user_id IN (Select user_id from users where msisdn = ?) ");
        }
        strBuff.append("and last_sos_txn_status =?  ");
        if (isLockRecordForUpdate) {
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                strBuff.append(" FOR UPDATE with RS");
            } else {
                strBuff.append(" FOR UPDATE ");
            }
        }
        final String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, LOG);

        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
           
            if(receiverUserID!=null){
            pstmt.setString(1, receiverUserID);
            }else{
            	pstmt.setString(1, msisdn);
            }
            pstmt.setString(2, PretupsI.SOS_PENDING_STATUS);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                userTransferCountsVO = new UserTransferCountsVO();
                userTransferCountsVO.setLastSOSTxnID(rs.getString("last_sos_txn_id"));
                userTransferCountsVO.setLastSOSTxnStatus(rs.getString("last_sos_txn_status"));
                userTransferCountsVO.setUserID(rs.getString("user_id"));
            }

        } 
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadRoamTransferCount]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("UserTransferCountsDAO ", methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadRoamTransferCount]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(" UserTransferCountsDAO", methodName, "error.general.processing",ex);
        } finally {
            
            LogFactory.printLog(methodName, "Exiting:  userTransferCountsVO =" + userTransferCountsVO, LOG);
           
        }
        return userTransferCountsVO;
    }

	/**
	 * @param receiverUserID
	 * @param con
	 * @param status 
	 * @param senderUserID
	 * @return
	 * @throws BTSLBaseException
	 */
	public int updateLastSOSTxnStatus(String receiverUserID, Connection con, String status) throws BTSLBaseException {

        final String methodName = "updateLastSOSTxnStatus";
		LogFactory.printLog(methodName, "Entered userID = "+receiverUserID + "and status : "+status, LOG);
		
		 
        int updateCount = 0;
		try
        {
			StringBuilder updateBuff = new StringBuilder();
            updateBuff.append(" update user_transfer_counts set last_sos_txn_status = ?  where user_id = ? ");
            String sqlUpdate = updateBuff.toString();
            LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlUpdate, LOG);
            try(PreparedStatement pstmt1 = con.prepareStatement(sqlUpdate);)
            {
           	pstmt1.setString(1, status);
            pstmt1.setString(2, receiverUserID);
            updateCount = pstmt1.executeUpdate();
           
            
		}
        }
        catch (SQLException sqe)
        {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName,sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isC2SPendingTransactionExist]", "", "", "", "SQL Exception:"
                    + sqe.getMessage());
            throw new BTSLBaseException("UserTransferCountsDAO", "", "error.general.sql.processing",sqe);
        }
        catch (Exception ex)
        {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName,ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isC2SPendingTransactionExist]", "", "", "", "Exception:"
                    + ex.getMessage());
            throw new BTSLBaseException("UserTransferCountsDAO", methodName, "error.general.processing",ex);
        } 
        finally
        {
			
			if (LOG.isDebugEnabled())
                LOG.debug(methodName, "Exiting:  updateCount=" + updateCount);
        }
        return updateCount;
    
	}
	
	public int updateUserTransferCountsforSOS(Connection p_con, ChannelTransferVO channelTransferVO, String userID) throws BTSLBaseException {

        final String METHOD_NAME = "updateUserTransferCountsforSOS";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered updateUserTransferCountsforSOS channelTransferVO : " + channelTransferVO);
        }

       

        int updateCount = 0;
        try {

            final StringBuffer strBuffUpdate = new StringBuffer();


                strBuffUpdate.append(" UPDATE user_transfer_counts  SET ");
                strBuffUpdate.append(" last_sos_txn_id = ?,last_sos_txn_status = ?, last_sos_product_code = ? WHERE user_id = ?  ");
          

            final String query = strBuffUpdate.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " query:" + query);
            }
            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            int m = 0;
            psmt.setString(++m, channelTransferVO.getTransferID());
            psmt.setString(++m, PretupsI.SOS_PENDING_STATUS);
            psmt.setString(++m, channelTransferVO.getSosProductCode());
            psmt.setString(++m, userID);

            updateCount = updateUserTransferUpdateCount(METHOD_NAME, psmt);
        }
        }catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserTransferCountsDAO[updateUserTransferCountsforSOS]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserTransferCountsDAO[updateUserTransferCountsforSOS]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } finally {
          
           LogFactory.printLog(METHOD_NAME, "Exiting Success :" + updateCount, LOG);
      
        }

        return updateCount;
    }
	/**
	 * @param receiverUserID
	 * @param con
	 * @param status 
	 * @param senderUserID
	 * @return
	 * @throws BTSLBaseException
	 */
	public int updateLastLRTxnStatus(String receiverUserID, Connection con, String status) throws BTSLBaseException {


        final String methodName = "updateLastLRTxnStatus";
		LogFactory.printLog(methodName, "Entered userID = "+receiverUserID + "and status : "+status, LOG);
		
		 
        int updateCount = 0;
		try
        {
			StringBuilder updateBuff = new StringBuilder();
            updateBuff.append(" update user_transfer_counts set last_lr_status = ?  where user_id = ? ");
            String sqlUpdate = updateBuff.toString();
            LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlUpdate, LOG);
           try(PreparedStatement pstmt1 = con.prepareStatement(sqlUpdate);)
           {
           	pstmt1.setString(1, status);
            pstmt1.setString(2, receiverUserID);
            updateCount = pstmt1.executeUpdate();
           
            
		}
        }
        catch (SQLException sqe)
        {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName,sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isC2SPendingTransactionExist]", "", "", "", "SQL Exception:"
                    + sqe.getMessage());
            throw new BTSLBaseException("UserTransferCountsDAO", "", "error.general.sql.processing",sqe);
        }
        catch (Exception ex)
        {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName,ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isC2SPendingTransactionExist]", "", "", "", "Exception:"
                    + ex.getMessage());
            throw new BTSLBaseException("UserTransferCountsDAO", methodName, "error.general.processing",ex);
        } 
        finally
        {
			
			if (LOG.isDebugEnabled())
                LOG.debug(methodName, "Exiting:  updateCount=" + updateCount);
        }
        return updateCount;
    
	}

	public UserTransferCountsVO selectLastLRTxnID(String receiverUserID, Connection con, Boolean isLockRecordForUpdate,String msisdn) throws BTSLBaseException {

        final String methodName = "selectLastLRTxnID";
        LogFactory.printLog(methodName, "Entered  Receiver UserID " + receiverUserID, LOG);
        
        
        UserTransferCountsVO userTransferCountsVO = null;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT user_id, last_lr_status, last_lr_txnid  FROM user_transfer_counts  WHERE ");
        if(receiverUserID!=null){
        strBuff.append("user_id = ? ");	
        }else{
        	strBuff.append("user_id IN (Select user_id from users where msisdn = ?) ");
        }
        strBuff.append("and last_lr_status =?  ");
        if (isLockRecordForUpdate) {
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                strBuff.append(" FOR UPDATE with RS");
            } else {
                strBuff.append(" FOR UPDATE ");
            }
        }
        final String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlSelect, LOG);

        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
           
            if(receiverUserID!=null){
            pstmt.setString(1, receiverUserID);
            }else{
            	pstmt.setString(1, msisdn);
            }
            pstmt.setString(2, PretupsI.LAST_LR_PENDING_STATUS);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                userTransferCountsVO = new UserTransferCountsVO();
                userTransferCountsVO.setLastSOSTxnID(rs.getString("last_lr_txnid"));
                userTransferCountsVO.setLastSOSTxnStatus(rs.getString("last_lr_status"));
            }

        }
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserTransferCountsDAO[selectLastLRTxnID]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("UserTransferCountsDAO ", methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserTransferCountsDAO[selectLastLRTxnID]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(" UserTransferCountsDAO", methodName, "error.general.processing",ex);
        } finally {
            
         
            LogFactory.printLog(methodName, "Exiting:  userTransferCountsVO =" + userTransferCountsVO, LOG);
           
        }
        return userTransferCountsVO;
    }
	
	public int updateUserTransferCountsforLR(Connection p_con, ChannelTransferVO channelTransferVO, String userID) throws BTSLBaseException {

        final String METHOD_NAME = "updateUserTransferCountsforLR";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered updateUserTransferCountsforLR channelTransferVO : " + channelTransferVO);
        }

        

        int updateCount = 0;
        try {

            final StringBuffer strBuffUpdate = new StringBuffer();


                strBuffUpdate.append(" UPDATE user_transfer_counts  SET ");
                strBuffUpdate.append(" last_lr_txnid = ?,last_lr_status = ? WHERE user_id = ?  ");
          

            final String query = strBuffUpdate.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " query:" + query);
            }
            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            int m = 0;
            psmt.setString(++m, channelTransferVO.getTransferID());
            psmt.setString(++m, PretupsI.LAST_LR_PENDING_STATUS);
            psmt.setString(++m, userID);

            updateCount = updateUserTransferUpdateCount(METHOD_NAME, psmt);
        }
        }catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserTransferCountsDAO[updateUserTransferCountsforLR]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserTransferCountsDAO[updateUserTransferCountsforLR]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        } finally {
           
           LogFactory.printLog(METHOD_NAME, "Exiting Success :" + updateCount, LOG);
      
        }

        return updateCount;
    }
	
	 /**
     * Method loadUserOTFCounts()
     * This method Load the Transfer Counts by the userID.
     * 
     * @param p_con
     * @param p_userId
     * @return UserOTFCountsVO
     * @throws BTSLBaseException
     */
    public  UserOTFCountsVO loadUserOTFCounts(Connection con, String userId, String detailId, Boolean addnl) throws BTSLBaseException {

        final String methodName = "loadUserOTFCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered  From UserID " + userId);
        }

         
         
        final StringBuilder strBuff = new StringBuilder();
        UserOTFCountsVO countsVO = null;

        strBuff.append(" SELECT user_id, prfle_otf_detail_id, otf_count, to_number(otf_value, '9999999999') AS otf_value, comm_type");
        strBuff.append(" from user_transfer_otf_count where user_id=? and comm_type=? and prfle_otf_detail_id in (select prfle_otf_detail_id from profile_otf_details where profile_detail_id=?)");
        strBuff.append(" order by otf_value asc");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
     
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, userId);
            if(addnl)
            {
            pstmt.setString(2, PretupsI.COMM_TYPE_ADNLCOMM);
            }
            else
            {
             pstmt.setString(2, PretupsI.COMM_TYPE_BASECOMM);
             }
            pstmt.setString(3, detailId);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            while(rs.next()) {
                countsVO =  UserOTFCountsVO.getInstance();
                countsVO.setUserID(rs.getString("user_id"));
                if(addnl)
                {
                countsVO.setAdnlComOTFDetailId(rs.getString("prfle_otf_detail_id"));
                }
                else
                {
                countsVO.setBaseComOTFDetailId(rs.getString("prfle_otf_detail_id"));
                }
                countsVO.setOtfCount(rs.getInt("otf_count"));
                countsVO.setOtfValue(rs.getLong("otf_value"));
           
 
            }

        } 
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:  countsVO =" + countsVO);
            }
        }
        return countsVO;
    }
    
    /**
     * Method updateUserOTFCounts
     * 
     * @param con
     * @param countsVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateUserOTFCounts(Connection con, UserOTFCountsVO countsVO) throws BTSLBaseException {

        final String methodName = "updateUserOTFCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered updateUserOTFCounts countsVO : " + countsVO + " exist " + countsVO.isUpdateRecord() + "countsVO.getUserID()" + countsVO.getUserID());
        }

         

        int updateCount = 0;
        try {

            final StringBuilder strBuffUpdate = new StringBuilder();
            final StringBuilder strBuffInsert = new StringBuilder();
            
            strBuffUpdate.append(" UPDATE user_transfer_otf_count  SET ");
            strBuffUpdate.append(" otf_count = ?, otf_value = ? where user_id=? and prfle_otf_detail_id=? and comm_type=?");

            final String queryUpdate = strBuffUpdate.toString();
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " queryUpdate:" + queryUpdate);
            }
           
            try(PreparedStatement psmt = con.prepareStatement(queryUpdate);)
            {
            	int m = 0;
            	psmt.setDouble(++m, countsVO.getOtfCount());
            	psmt.setLong(++m, countsVO.getOtfValue());
            	psmt.setString(++m, countsVO.getUserID());

            	if(countsVO.isAddnl())
            	{ 
            		psmt.setString(++m, countsVO.getAdnlComOTFDetailId());
            		psmt.setString(++m, PretupsI.COMM_TYPE_ADNLCOMM);
            	}
            	else
            	{  
            		psmt.setString(++m, countsVO.getBaseComOTFDetailId());
            		psmt.setString(++m, PretupsI.COMM_TYPE_BASECOMM);
            	}
            	updateCount = updateUserOTFUpdateCount(methodName, psmt);
            	if(updateCount <=0){
            		 strBuffInsert.append(" INSERT INTO user_transfer_otf_count ( ");
                     strBuffInsert.append(" otf_count, otf_value, user_id, prfle_otf_detail_id, comm_type) ");
                     strBuffInsert.append(" VALUES ");
                     strBuffInsert.append(" (?,?,?,?,?) ");
                     final String queryInsert = strBuffInsert.toString();
            		 if (LOG.isDebugEnabled()) {
                         LOG.debug(methodName, " queryInsert:" + queryInsert);
                     }
            		try(PreparedStatement psmt1 = con.prepareStatement(queryInsert);)
            		{
            			int m1 = 0;
            			psmt1.setDouble(++m1, countsVO.getOtfCount());
            			psmt1.setLong(++m1, countsVO.getOtfValue());
            			psmt1.setString(++m1, countsVO.getUserID());

            			if(countsVO.isAddnl())
            			{ 
            				psmt1.setString(++m1, countsVO.getAdnlComOTFDetailId());
            				psmt1.setString(++m1, PretupsI.COMM_TYPE_ADNLCOMM);
            			}
            			else
            			{  
            				psmt1.setString(++m1, countsVO.getBaseComOTFDetailId());
            				psmt1.setString(++m1, PretupsI.COMM_TYPE_BASECOMM);
            			}
            			updateCount = psmt1.executeUpdate();
            		}
            	}
            } 
        } catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
           
           LogFactory.printLog(methodName, "Exiting Success :" + updateCount, LOG);
      
        }

        return updateCount;
    }

	private int updateUserOTFUpdateCount(final String methodName, PreparedStatement psmt)  {
		int updateCount=0;
		try{
			updateCount = psmt.executeUpdate();
		}catch(SQLException sqle){
			 LOG.error(methodName, "Exception " + sqle.getMessage());
             LOG.errorTrace(methodName, sqle);
		}
		return updateCount;
	}
	
	 /**
     * Method loadUserOTFCounts()
     * This method Load the Transfer Counts by the userID.
     * 
     * @param p_con
     * @param p_userId
     * @param p_isLockRecordForUpdate
     *            boolean
     * @return UserOTFCountsVO
     * @throws BTSLBaseException
     *             Added p_isLockRecordForUpdate so that same query can be used
     *             just before updating the records also
     */
    public  UserOTFCountsVO loadUserOTFCountsWithFilter(Connection con, String userId,String adnlComOTFDetailId, boolean isLockRecordForUpdate, boolean addnl) throws BTSLBaseException {

        final String methodName = "loadUserOTFCountsWithFilter";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered  From UserID " + userId + " p_isLockRecordForUpdate=" + isLockRecordForUpdate);
        }

      
         
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT user_id, prfle_otf_detail_id, otf_count, otf_value");
        strBuff.append(" from user_transfer_otf_count where user_id=? and prfle_otf_detail_id=? and comm_type=? ");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        UserOTFCountsVO countsVO = null;
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, userId);
            pstmt.setString(2, adnlComOTFDetailId);
            if(addnl)
            {
                pstmt.setString(3, PretupsI.COMM_TYPE_ADNLCOMM);
            }
            else
            {
                pstmt.setString(3, PretupsI.COMM_TYPE_BASECOMM);

            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                countsVO = new UserOTFCountsVO();
                countsVO.setUserID(rs.getString("user_id"));
                if(addnl)
                {
                countsVO.setAdnlComOTFDetailId(rs.getString("prfle_otf_detail_id"));
                }
                else
                {
                    countsVO.setBaseComOTFDetailId(rs.getString("prfle_otf_detail_id"));

                }
                countsVO.setOtfCount(rs.getInt("otf_count"));
                countsVO.setOtfValue(rs.getLong("otf_value"));
                
 
            }
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:  UserTransferCountsVO =" + countsVO);
            }
        }
        return countsVO;
    }

    /**
     * Method loadUserOTFCounts()
     * This method Load the Transfer Counts by the userID.
     * 
     * @param p_con
     * @param p_userId
     * @return UserOTFCountsVO
     * @throws BTSLBaseException
     */
    public  List<UserOTFCountsVO> loadUserOTFCountsList(Connection con, String userId,String detailID, Boolean addnl) throws BTSLBaseException {

        final String methodName = "loadUserOTFCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered  From UserID= " + userId+",detailID= " + detailID,",addnl= " + addnl);
        }

         
       
        final StringBuilder strBuff = new StringBuilder();
        UserOTFCountsVO countsVO = null;
        List<UserOTFCountsVO> list = new ArrayList<UserOTFCountsVO>();

        strBuff.append(" SELECT user_id, prfle_otf_detail_id, otf_count, otf_value, comm_type");
        strBuff.append(" from user_transfer_otf_count where user_id=? and comm_type=? and prfle_otf_detail_id in (select prfle_otf_detail_id from profile_otf_details where profile_detail_id=?)");
        strBuff.append(" order by otf_value asc");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
     
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, userId);
            if(addnl)
            {
            pstmt.setString(2,PretupsI.COMM_TYPE_ADNLCOMM);
            }
            else
            {
                pstmt.setString(2,PretupsI.COMM_TYPE_BASECOMM);
            }
            pstmt.setString(3, detailID);
            
            try( ResultSet rs = pstmt.executeQuery();)
            {
            while(rs.next()) {
                  countsVO = new UserOTFCountsVO();
                countsVO.setUserID(rs.getString("user_id"));
                if(addnl)
                countsVO.setAdnlComOTFDetailId(rs.getString("prfle_otf_detail_id"));
                else
                	countsVO.setBaseComOTFDetailId(rs.getString("prfle_otf_detail_id"));
                countsVO.setOtfCount(rs.getInt("otf_count"));
                countsVO.setOtfValue(rs.getLong("otf_value"));
                countsVO.setCommType(rs.getString("comm_type"));
                list.add(countsVO);
 
            }

        } 
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferCounts]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:  countsVO =" + countsVO);
            }
        }
        return list;
    }
    
    /**
     * Method deleteUserOTFCounts
     * 
     * @param con
     * @param countsVO
     * @param exist
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteUserOTFCounts(Connection con, UserOTFCountsVO countsVO, boolean exist, boolean addnl) throws BTSLBaseException {

        final String methodName = "deleteUserOTFCounts";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered deleteUserOTFCounts countsVO : " + countsVO + " exist " + exist + "countsVO.getUserID()" + countsVO.getUserID());
        }

         

        int deleteCount = 0;
        try {

            final StringBuilder strBuffdelete = new StringBuilder();

       
            strBuffdelete.append(" DELETE from user_transfer_otf_count");
            strBuffdelete.append(" where user_id=? and prfle_otf_detail_id=? and comm_type=? ");


            final String query = strBuffdelete.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " query:" + query);
            }
            try(PreparedStatement psmt = con.prepareStatement(query);)
            {
            int m = 0;
            psmt.setString(++m, countsVO.getUserID());
            
            if(addnl)
            {
            	psmt.setString(++m, countsVO.getAdnlComOTFDetailId());
                psmt.setString(++m, PretupsI.COMM_TYPE_ADNLCOMM);
            }
            else
            {
               	psmt.setString(++m, countsVO.getBaseComOTFDetailId());
                psmt.setString(++m, PretupsI.COMM_TYPE_BASECOMM);

            }
            deleteCount = updateUserOTFUpdateCount(methodName, psmt);
        } 
        } catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserTransferCounts]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
          
           LogFactory.printLog(methodName, "Exiting Success :" + deleteCount, LOG);
      
        }

        return deleteCount;
    }

    
}
