package com.btsl.pretups.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.util.BTSLUtil;
// commented for DB2
import com.btsl.util.Constants;

/*
 * SubscriberControlDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 24/06/2005 Initial Creation
 * Ashish K 03/10/2007 BTRC implementation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

public class SubscriberControlDAO {

    private static final Log _log = LogFactory.getLog(SubscriberControlDAO.class.getName());

    /**
     * Method to load the subscriber control parameters of the current date
     * 
     * @param p_con
     * @param p_receiverVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean loadSubscriberControlDetails(Connection p_con, ReceiverVO p_receiverVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadSubscriberControlDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubscriberControlDetails", "Entered p_receiverVO:" + p_receiverVO);
        }
        
        boolean isRecordFound = false;
        try {

            StringBuilder selectQueryBuff = new StringBuilder(" SELECT	prefix_id, failed_count, consecutive_failures, last_failure_on, success_count, ");
            selectQueryBuff.append("last_success_on, total_transfer_amount, last_transfer_id, last_transfer_status, last_transfer_stage, ");
            selectQueryBuff.append("last_transfer_on,  prev_day_success_counts,prev_day_transfer_amount,weekly_success_counts,weekly_transfer_amount, ");
            selectQueryBuff.append("prev_week_success_counts, prev_week_transfer_amount, monthly_success_counts, monthly_transfer_amount, ");
            selectQueryBuff.append("prev_month_success_counts, prev_month_transfer_amount, last_transfer_amount, last_service_type, VPIN_INVALID_COUNT");
            selectQueryBuff.append(" FROM subscriber_control WHERE module=? AND msisdn=? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberControlDetails", "select query:" + selectQuery);
            }
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
           {
            pstmtSelect.setString(1, p_receiverVO.getModule());
            pstmtSelect.setString(2, p_receiverVO.getMsisdn());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                p_receiverVO.setTotalFailCount(rs.getLong("failed_count"));
                p_receiverVO.setTotalConsecutiveFailCount(rs.getLong("consecutive_failures"));
                p_receiverVO.setLastFailedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_failure_on")));
                p_receiverVO.setTotalSuccessCount(rs.getLong("success_count"));
                p_receiverVO.setLastSuccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_on")));
                p_receiverVO.setTotalTransferAmount(rs.getLong("total_transfer_amount"));
                p_receiverVO.setLastTransferID(rs.getString("last_transfer_id"));
                p_receiverVO.setLastTransferStatus(rs.getString("last_transfer_status"));
                p_receiverVO.setLastTransferStage(rs.getString("last_transfer_stage"));
                p_receiverVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transfer_on")));
                // updated to fetch the weekly monthly threholds- Ashish
                // 05/08/2007
                p_receiverVO.setPrevDaySuccCount(rs.getLong("prev_day_success_counts"));
                p_receiverVO.setPrevDayTrasferAmount(rs.getLong("prev_day_transfer_amount"));
                p_receiverVO.setWeeklySuccCount(rs.getLong("weekly_success_counts"));
                p_receiverVO.setWeeklyTransferAmount(rs.getLong("weekly_transfer_amount"));
                p_receiverVO.setPrevWeekSuccCount(rs.getLong("prev_week_success_counts"));
                p_receiverVO.setPrevWeekTransferAmount(rs.getLong("prev_week_transfer_amount"));
                p_receiverVO.setMonthlySuccCount(rs.getLong("monthly_success_counts"));
                p_receiverVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                p_receiverVO.setPrevMonthSuccCount(rs.getLong("prev_month_success_counts"));
                p_receiverVO.setPrevMonthTransferAmount(rs.getLong("prev_month_transfer_amount"));
                // added by nilesh: for MRP block time
                p_receiverVO.setLastMRP(rs.getLong("last_transfer_amount"));
                p_receiverVO.setLastServiceType(rs.getString("last_service_type"));
                p_receiverVO.setInvalidPINcount(rs.getLong("VPIN_INVALID_COUNT"));
                // end for MRP block time
                isRecordFound = true;
            }
            return isRecordFound;
        }
           }
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadSubscriberControlDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberControlDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadSubscriberControlDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberControlDetails", "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberControlDetails", "Exiting isRecordFound:" + isRecordFound);
            }
        }// end of finally
    }

    /**
     * Method to add the subscriber control details for the current date if not
     * available
     * 
     * @param p_con
     * @param p_receiverVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addSubscriberControlDetails(Connection p_con, ReceiverVO p_receiverVO) throws BTSLBaseException {
        final String METHOD_NAME = "addSubscriberControlDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("addSubscriberControlDetails", "Entered p_receiverVO:" + p_receiverVO.toString());
        }
         
        int addCount = 0;
        try {
            int i = 1;
            StringBuilder insertQueryBuff = new StringBuilder(" INSERT INTO subscriber_control (module,msisdn,subscriber_type, ");
            insertQueryBuff.append(" prefix_id,total_transfer_amount,last_transfer_id, last_transfer_status, last_transfer_stage, ");
            insertQueryBuff.append(" last_transfer_on,created_date,last_transfer_amount,last_service_type,VPIN_INVALID_COUNT) ");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addSubscriberControlDetails", "insert query:" + insertQuery);
            }
            try(PreparedStatement pstmtInsert = p_con.prepareStatement(insertQuery);)
            {
            pstmtInsert.setString(i++, p_receiverVO.getModule());
            pstmtInsert.setString(i++, p_receiverVO.getMsisdn());
            pstmtInsert.setString(i++, p_receiverVO.getSubscriberType());
            pstmtInsert.setLong(i++, p_receiverVO.getPrefixID());
            pstmtInsert.setLong(i++, p_receiverVO.getTotalTransferAmount());
            pstmtInsert.setString(i++, p_receiverVO.getLastTransferID());
            pstmtInsert.setString(i++, p_receiverVO.getLastTransferStatus());
            pstmtInsert.setString(i++, p_receiverVO.getLastTransferStage());
            if (p_receiverVO.getLastTransferOn() != null) {
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_receiverVO.getLastTransferOn()));
            } else {
                pstmtInsert.setNull(i++, Types.TIMESTAMP);
            }
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_receiverVO.getCreatedDate()));
            pstmtInsert.setLong(i++, p_receiverVO.getRequestedMRP());
            pstmtInsert.setString(i++, p_receiverVO.getRequestedServiceType());
            pstmtInsert.setLong(i++, p_receiverVO.getInvalidPINcount());
            addCount = pstmtInsert.executeUpdate();
            return addCount;
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error("addSubscriberControlDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[addSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addSubscriberControlDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addSubscriberControlDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[addSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSubscriberControlDetails", "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("addSubscriberControlDetails", "Exiting addCount=" + addCount);
            }
        }// end of finally
    }

    /**
     * Method to update the daily subscriber control for the day
     * 
     * @param p_con
     * @param p_receiverVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubscriberControlDetails(Connection p_con, ReceiverVO p_receiverVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateSubscriberControlDetails";
        LogFactory.printLog(METHOD_NAME, "Entered p_receiverVO:" + p_receiverVO.toString(), _log);
        
        int updateCount = 0;
        try {
            int i = 1;
            StringBuilder updateQueryBuff = new StringBuilder(" UPDATE subscriber_control SET subscriber_type=?,prefix_id=?,total_transfer_amount=?,failed_count=?,consecutive_failures=?,");
            updateQueryBuff.append("last_failure_on=? ,success_count=? ,last_success_on=?, last_transfer_status=?, last_transfer_id=?, last_transfer_stage=?,last_transfer_on=?, ");
            updateQueryBuff.append("  prev_day_success_counts=?,  prev_day_transfer_amount=?, weekly_transfer_amount=?, weekly_success_counts=?, ");
            updateQueryBuff.append(" prev_week_success_counts=?, prev_week_transfer_amount=?, monthly_success_counts=?, ");
            updateQueryBuff.append(" monthly_transfer_amount=?, prev_month_success_counts=?, prev_month_transfer_amount=?, last_transfer_amount=?, last_service_type=? ");
            if("VCN".equals(p_receiverVO.getRequestedServiceType()))
            	updateQueryBuff.append(", VPIN_INVALID_COUNT=?  ");
            updateQueryBuff.append("WHERE module=? AND msisdn=?  ");

            String updateQuery = updateQueryBuff.toString();
            LogFactory.printLog(METHOD_NAME, "update query:" + updateQuery, _log);
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            pstmtUpdate.setString(i++, p_receiverVO.getSubscriberType());
            pstmtUpdate.setLong(i++, p_receiverVO.getPrefixID());
            pstmtUpdate.setLong(i++, p_receiverVO.getTotalTransferAmount());
            pstmtUpdate.setLong(i++, p_receiverVO.getTotalFailCount());
            pstmtUpdate.setLong(i++, p_receiverVO.getTotalConsecutiveFailCount());
            if (p_receiverVO.getLastFailedOn() != null) {
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_receiverVO.getLastFailedOn()));
            } else {
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);
            }
            pstmtUpdate.setLong(i++, p_receiverVO.getTotalSuccessCount());
            if (p_receiverVO.getLastSuccessOn() != null) {
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_receiverVO.getLastSuccessOn()));
            } else {
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);
            }
            pstmtUpdate.setString(i++, BTSLUtil.NullToString(p_receiverVO.getLastTransferStatus()));
            if (!BTSLUtil.isNullString(p_receiverVO.getLastTransferID())) {
                pstmtUpdate.setString(i++, p_receiverVO.getLastTransferID());
            } else {
                pstmtUpdate.setNull(i++, Types.VARCHAR);
            }
            pstmtUpdate.setString(i++, BTSLUtil.NullToString(p_receiverVO.getLastTransferStage()));
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_receiverVO.getLastTransferOn()));
            // Updating the receivers weekly and monthly thresholds counts &
            // amount - Ashish [03-10-07]
            pstmtUpdate.setLong(i++, p_receiverVO.getPrevDaySuccCount());
            pstmtUpdate.setLong(i++, p_receiverVO.getPrevDayTrasferAmount());
            pstmtUpdate.setLong(i++, p_receiverVO.getWeeklyTransferAmount());
            pstmtUpdate.setLong(i++, p_receiverVO.getWeeklySuccCount());
            pstmtUpdate.setLong(i++, p_receiverVO.getPrevWeekSuccCount());
            pstmtUpdate.setLong(i++, p_receiverVO.getPrevWeekTransferAmount());
            pstmtUpdate.setLong(i++, p_receiverVO.getMonthlySuccCount());
            pstmtUpdate.setLong(i++, p_receiverVO.getMonthlyTransferAmount());
            pstmtUpdate.setLong(i++, p_receiverVO.getPrevMonthSuccCount());
            pstmtUpdate.setLong(i++, p_receiverVO.getPrevMonthTransferAmount());
            // added by nilesh: MRP block time
            pstmtUpdate.setLong(i++, p_receiverVO.getRequestedMRP());
           	pstmtUpdate.setString(i++, p_receiverVO.getRequestedServiceType());
            if("VCN".equals(p_receiverVO.getRequestedServiceType()))
            	pstmtUpdate.setLong(i++, p_receiverVO.getInvalidPINcount());
            // end
            pstmtUpdate.setString(i++, p_receiverVO.getModule());
            pstmtUpdate.setString(i++, p_receiverVO.getMsisdn());

            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateSubscriberControlDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[updateSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberControlDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateSubscriberControlDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[updateSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberControlDetails", "error.general.processing");
        }// end of catch
        finally {
        	
        	LogFactory.printLog(METHOD_NAME, "Exiting updateCount=" + updateCount, _log);
        }// end of finally
    }

    /**
     * Method to lock the subscriber control table
     * 
     * @param p_con
     * @param p_receiverVO
     * @return void
     * @throws BTSLBaseException
     */
    public void lockSubscriberControlTable(Connection p_con, ReceiverVO p_receiverVO) throws BTSLBaseException {
        final String METHOD_NAME = "lockSubscriberControlTable";
        if (_log.isDebugEnabled()) {
            _log.debug("lockSubscriberControlTable", "Entered");
        }
        
        try {
            StringBuilder selectQueryBuff = new StringBuilder("SELECT	last_transfer_status ");
            // DB220120123for update WITH RS
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                selectQueryBuff.append(" FROM subscriber_control WHERE module=? AND msisdn=? for update WITH RS");
            } else {
                selectQueryBuff.append(" FROM subscriber_control WHERE module=? AND msisdn=? for update NOWAIT");
            }
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("lockSubscriberControlTable", "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_receiverVO.getModule());
            pstmtSelect.setString(2, p_receiverVO.getMsisdn());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            rs.next();
        }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error("lockSubscriberControlTable", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + sqle.getMessage());
            String[] strArr = new String[] { p_receiverVO.getMsisdn() };
            throw new BTSLBaseException("PretupsBL", "lockSubscriberControlTable", PretupsErrorCodesI.RECEIVER_LAST_REQ_UNDERPROCESS_S, 0, strArr, null);
        }// end of catch
        catch (Exception e) {
            _log.error("lockSubscriberControlTable", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadSubscriberControlDetails]", "", p_receiverVO.getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "lockSubscriberControlTable", "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("lockSubscriberControlTable", "Exited");
            }
        }// end of finally
    }

    /**
     * Method to load the subscriber control parameters like
     * last_transfer_amount and last_service_type
     * 
     * @param p_con
     * @param p_receiverVO
     * @return boolean
     * @throws BTSLBaseException
     * @author nilesh.kumar
     */
    public void loadSubscriberLastDetails(Connection p_con, String p_module, String p_msisdn, TransferVO p_transferVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadSubscriberLastDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubscriberLastDetails", "Entered p_module:" + p_module + "p_msisdn:" + p_msisdn);
        }
         
         
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT	last_transfer_amount, last_service_type");
            selectQueryBuff.append(" FROM subscriber_control WHERE module=? AND msisdn=? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberLastDetails", "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_module);
            pstmtSelect.setString(2, p_msisdn);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                p_transferVO.setLastMRP(rs.getLong("last_transfer_amount"));
                p_transferVO.setLastServiceType(rs.getString("last_service_type"));

            }
        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadSubscriberLastDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadSubscriberLastDetails]", "", p_msisdn, "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberLastDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadSubscriberControlDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadSubscriberLastDetails]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberLastDetails", "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberLastDetails", "Exiting p_transferVO:" + p_transferVO);
            }
        }// end of finally
    }
}
