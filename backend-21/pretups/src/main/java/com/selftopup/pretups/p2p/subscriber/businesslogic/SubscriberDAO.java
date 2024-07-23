package com.selftopup.pretups.p2p.subscriber.businesslogic;

/*
 * SubscriberDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/06/2005 Initial Creation
 * Avinash 17/06/2005 Added some more methods
 * Gurjeet 02/07/2005 Changed all the methods
 * Ankit Zindal 20/11/2006 ChangeID=LOCALEMASTER
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.cp2p.registration.businesslogic.CP2PRegistrationDAO;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.p2p.logging.UnregisterSubscribersFileProcessLog;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.selftopup.pretups.subscriber.businesslogic.PostPaidControlParametersVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

/**
 */
public class SubscriberDAO {

    /**
     * Field _log.
     */
    private static final Log _log = LogFactory.getLog(SubscriberDAO.class.getName());

    /**
     * Constructor for SubscriberDAO.
     */
    public SubscriberDAO() {
        super();
    }

    /**
     * This method is used to load p2p subscriber user information.
     * 
     * @param p_con
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public SenderVO loadSubscriberDetailsByMsisdn(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadSubscriberDetailsByMsisdn";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_msisdn:" + p_msisdn);
        PreparedStatement pstmtSelect = null;
        SenderVO senderVO = null;
        ResultSet rs = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder("SELECT user_id,subscriber_type,prefix_id,user_name,status,network_code,pin,pin_block_count,");
            selectQueryBuff.append(" last_transfer_amount,last_transfer_on,last_transfer_type,last_transfer_status,last_transfer_id,last_transfer_msisdn,");
            selectQueryBuff.append(" pin_modified_on,first_invalid_pin_time,buddy_seq_number,total_transfers,total_transfer_amount,request_status,billing_type,billing_cycle_date, ");
            selectQueryBuff.append(" credit_limit,activated_on,registered_on,created_on,created_by,modified_on,modified_by,consecutive_failures,");
            selectQueryBuff.append(" skey_required,daily_transfer_count,monthly_transfer_count,weekly_transfer_count,daily_transfer_amount,");
            selectQueryBuff.append(" monthly_transfer_amount,weekly_transfer_amount,prev_daily_transfer_count,prev_monthly_transfer_count,");
            selectQueryBuff.append(" prev_weekly_transfer_count,prev_daily_transfer_amount,prev_monthly_transfer_amount,prev_weekly_transfer_amount,");
            selectQueryBuff.append(" prev_transfer_date,prev_transfer_week_date,prev_transfer_month_date,service_class_code,last_success_transfer_date,service_class_id,language, country,email_id,imei ");
            selectQueryBuff.append(" FROM P2P_SUBSCRIBERS WHERE msisdn=? AND status <> ? AND status <> ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msisdn);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(3, PretupsI.USER_STATUS_CANCELED);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                senderVO = new SenderVO();
                senderVO.setUserID(rs.getString("user_id"));
                senderVO.setMsisdn(p_msisdn);
                senderVO.setSubscriberType(rs.getString("subscriber_type"));
                senderVO.setPrefixID(rs.getLong("prefix_id"));
                senderVO.setUserName(rs.getString("user_name"));
                senderVO.setStatus(rs.getString("status"));
                senderVO.setNetworkCode(rs.getString("network_code"));
                senderVO.setPin(rs.getString("pin"));
                senderVO.setPinBlockCount(rs.getInt("pin_block_count"));
                senderVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                senderVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transfer_on")));
                senderVO.setLastTransferID(rs.getString("last_transfer_id"));
                senderVO.setLastTransferType(rs.getString("last_transfer_type"));
                senderVO.setLastTransferStatus(rs.getString("last_transfer_status"));
                senderVO.setLastTransferMSISDN(rs.getString("last_transfer_msisdn"));
                senderVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
                senderVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
                senderVO.setBuddySeqNumber(rs.getInt("buddy_seq_number"));
                senderVO.setTotalTransfers(rs.getLong("total_transfers"));
                senderVO.setTotalTransferAmount(rs.getLong("total_transfer_amount"));
                senderVO.setRequestStatus(rs.getString("request_status"));
                senderVO.setBillingType(rs.getString("billing_type"));
                senderVO.setBillingCycleDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("billing_cycle_date")));
                senderVO.setCreditLimit(rs.getLong("credit_limit"));
                senderVO.setActivatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("activated_on")));
                senderVO.setRegisteredOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("registered_on")));
                senderVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                senderVO.setCreatedBy(rs.getString("created_by"));
                senderVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                senderVO.setModifiedBy(rs.getString("modified_by"));
                senderVO.setConsecutiveFailures(rs.getLong("consecutive_failures"));
                senderVO.setSkeyRequired(rs.getString("skey_required"));
                senderVO.setDailyTransferCount(rs.getLong("daily_transfer_count"));
                senderVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));
                senderVO.setWeeklyTransferCount(rs.getLong("weekly_transfer_count"));
                senderVO.setDailyTransferAmount(rs.getLong("daily_transfer_amount"));
                senderVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                senderVO.setWeeklyTransferAmount(rs.getLong("weekly_transfer_amount"));

                senderVO.setPrevDailyTransferCount(rs.getLong("prev_daily_transfer_count"));
                senderVO.setPrevMonthlyTransferCount(rs.getLong("prev_monthly_transfer_count"));
                senderVO.setPrevWeeklyTransferCount(rs.getLong("prev_weekly_transfer_count"));
                senderVO.setPrevDailyTransferAmount(rs.getLong("prev_daily_transfer_amount"));
                senderVO.setPrevMonthlyTransferAmount(rs.getLong("prev_monthly_transfer_amount"));
                senderVO.setPrevWeeklyTransferAmount(rs.getLong("prev_weekly_transfer_amount"));
                senderVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_date")));
                senderVO.setPrevTransferWeekDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_week_date")));
                senderVO.setPrevTransferMonthDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_month_date")));
                senderVO.setServiceClassCode(rs.getString("service_class_code"));
                senderVO.setLastSuccessTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_transfer_date")));
                senderVO.setServiceClassID(rs.getString("service_class_id"));
                senderVO.setLanguage(rs.getString("language"));
                senderVO.setCountry(rs.getString("country"));
                if (BTSLUtil.isNullString(senderVO.getLanguage()) || BTSLUtil.isNullString(senderVO.getCountry()))
                    senderVO.setLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                else
                    senderVO.setLocale(new Locale(senderVO.getLanguage(), senderVO.getCountry()));
                senderVO.setEmailId(rs.getString("email_id"));
                senderVO.setImei(rs.getString("imei"));
            }
            return senderVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByMsisdn]", "", p_msisdn, "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByMsisdn]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmtSelect);
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberDetails", "Exiting senderVO:" + senderVO);
        }// end of finally
    }

    /**
     * Method markRequestUnderProcess.
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     */
    public int markRequestUnderProcess(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "markRequestUnderProcess";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered User ID=" + p_senderVO.getUserID() + " Msisdn:" + p_senderVO.getMsisdn());
       
        int updateCount = 0;
        String selectQuery = "UPDATE p2p_subscribers set request_status=?,modified_by=?, modified_on=? WHERE user_id=? AND msisdn=?";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "select query:" + selectQuery);
        try (PreparedStatement pstmtUpdate = p_con.prepareStatement(selectQuery);){
            pstmtUpdate.setString(1, PretupsI.TXN_STATUS_UNDER_PROCESS);
            pstmtUpdate.setString(2, p_senderVO.getModifiedBy());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_senderVO.getUserID());
            pstmtUpdate.setString(5, p_senderVO.getMsisdn());
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[markRequestUnderProcess]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[markRequestUnderProcess]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally
    }

    /**
     * Unmarks the under process request to complete
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     */
    public int unmarkRequestUnderProcess(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "unmarkRequestUnderProcess";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered User ID=" + p_senderVO.getUserID() + " Msisdn:" + p_senderVO.getMsisdn());
       
        int updateCount = 0;
        String selectQuery = "UPDATE p2p_subscribers SET request_status=?,modified_by=?, modified_on=?  WHERE user_id=? AND msisdn=?";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "select query:" + selectQuery);
        try(PreparedStatement pstmtUpdate = p_con.prepareStatement(selectQuery);) {
       
            pstmtUpdate.setString(1, PretupsI.TXN_STATUS_COMPLETED);
            pstmtUpdate.setString(2, p_senderVO.getModifiedBy());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_senderVO.getUserID());
            pstmtUpdate.setString(5, p_senderVO.getMsisdn());
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[unmarkRequestUnderProcess]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[unmarkRequestUnderProcess]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally
    }

    /**
     * To check whether user registered in our System or not
     * 
     * @param p_con
     * @param p_filteredMsisdn
     *            userMSISIDN
     * @param p_subscriberType
     *            subscriberType postPiad or PrePaid
     * @return boolean if msisdn exist the return true else return false
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public boolean isMSISDNExist(Connection p_con, String p_filteredMsisdn, String p_subscriberType) throws BTSLBaseException {

        final String methodName = "isMSISDNExist";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_filteredMsisdn:" + p_filteredMsisdn + "  subscriberType  " + p_subscriberType);
        
        boolean exist = false;
        String selectQuery = "SELECT msisdn FROM p2p_subscribers WHERE msisdn=? AND subscriber_type = ? ";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "select query:" + selectQuery);
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
         
            pstmtSelect.setString(1, p_filteredMsisdn);
            pstmtSelect.setString(2, p_subscriberType);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                exist = true;
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isMSISDNExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isMSISDNExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting  MSISIDN EXIST: " + exist);
        }// end of finally

        return exist;

    }

    /**
     * Change the user pin
     * 
     * @param p_con
     * @param p_senderVO
     *            SenderVO
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int changePin(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "changePin";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered subscriberVO : " + p_senderVO);
         
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder(" UPDATE  p2p_subscribers set pin = ? ");
            if (p_senderVO.isActivateStatusReqd())
                strBuff.append("  , status=?,activated_on=? ");
            strBuff.append("  , modified_by = ? , modified_on = ? , pin_modified_on = ? WHERE msisdn = ? AND status = ? AND subscriber_type = ? ");
            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + query);
            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            int i = 1;
            psmt.setString(i++, BTSLUtil.encryptText(p_senderVO.getPin()));
            if (p_senderVO.isActivateStatusReqd()) {
                psmt.setString(i++, p_senderVO.getStatus());
                psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            }
            psmt.setString(i++, p_senderVO.getModifiedBy());
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            // pin modified on should be set while updating the pin field. Added
            // by Ashish- 27-10-06
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPinModifiedOn()));
            psmt.setString(i++, p_senderVO.getMsisdn());
            if (p_senderVO.isActivateStatusReqd())
                psmt.setString(i++, PretupsI.USER_STATUS_NEW);
            else
                psmt.setString(i++, PretupsI.USER_STATUS_ACTIVE);

            psmt.setString(i++, p_senderVO.getSubscriberType());

            updateCount = psmt.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changePin]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changePin]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:updateCount=" + updateCount);
        }// end of finally
        return updateCount;
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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_subscriber :" + p_senderVO);
        String method = methodName;
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
       
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_subscribers SET activated_on=?, last_transfer_on=?,total_transfers=?,total_transfer_amount=?, ");
            sbf.append(" status = ?,user_name=?,billing_type=?, ");
            sbf.append(" billing_cycle_date=?,credit_limit=? ,modified_on = ? , ");
            sbf.append(" modified_by = ?,prefix_id=? WHERE msisdn=? AND subscriber_type = ?");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + updateQuery);

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(updateQuery);
            try(PreparedStatement pstmtUpdate =  p_con.prepareStatement(updateQuery);)
            {
            int i = 1;
            if (p_senderVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);

            if (p_senderVO.getLastTransferOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setLong(i++, p_senderVO.getTotalTransfers());
            pstmtUpdate.setLong(i++, p_senderVO.getTotalTransferAmount());
            pstmtUpdate.setString(i++, p_senderVO.getStatus());

            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_senderVO.getUserName());

            pstmtUpdate.setString(i++, p_senderVO.getBillingType());
            if (p_senderVO.getBillingCycleDate() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getBillingCycleDate()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setLong(i++, p_senderVO.getMonthlyTransferAmount());
            if (p_senderVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
            pstmtUpdate.setLong(i++, p_senderVO.getPrefixID());
            pstmtUpdate.setString(i++, p_senderVO.getMsisdn());
            pstmtUpdate.setString(i++, p_senderVO.getSubscriberType());
            boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, method, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }

        }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally

        return updateCount;
    }

    /**
     * Update the user status, it can be suspended or active
     * 
     * @param p_con
     * @param p_senderVO
     * @return int status
     * @throws BTSLBaseException
     */
    public int updateSubscriberStatus(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberStatus";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_subscriber :" + p_senderVO);
        
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_subscribers set status = ?,modified_on = ? , ");
            sbf.append(" modified_by = ? WHERE msisdn=? AND subscriber_type = ?");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + updateQuery);

            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            int i = 1;
            pstmtUpdate.setString(i++, p_senderVO.getStatus());

            if (p_senderVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());

            pstmtUpdate.setString(i++, p_senderVO.getMsisdn());
            pstmtUpdate.setString(i++, p_senderVO.getSubscriberType());

            boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());

            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }

        }
        }// end of try
        catch (BTSLBaseException e) {
            throw e;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberStatus]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally

        return updateCount;
    }

    /**
     * Method to update the PIN Count with the set values and also to update the
     * status of the user if required
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_isStatusUpdate
     *            boolean
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int updatePinStatus(Connection p_con, SenderVO p_senderVO, boolean p_isStatusUpdate) throws BTSLBaseException {
        final String methodName = "updatePinStatus";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_senderVO :" + p_senderVO + " p_isStatusUpdate:" + p_isStatusUpdate);
        String method = methodName;
        
        int updateCount = 0;
        try {
            int i = 1;
            StringBuilder updateQueryBuff = new StringBuilder("UPDATE p2p_subscribers SET pin_block_count = ?, first_invalid_pin_time=?,modified_by = ? , modified_on =?   ");
            if (p_isStatusUpdate)
                updateQueryBuff.append(" ,status=? ");
            updateQueryBuff.append("WHERE msisdn=? and subscriber_type = ? ");
            String selectUpdate = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectUpdate);
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(selectUpdate);)
            {
            pstmtUpdate.setInt(i++, p_senderVO.getPinBlockCount());
            if (p_senderVO.getFirstInvalidPinTime() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getFirstInvalidPinTime()));
            else
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            if (p_isStatusUpdate)
                pstmtUpdate.setString(i++, p_senderVO.getStatus());
            pstmtUpdate.setString(i++, p_senderVO.getMsisdn());
            pstmtUpdate.setString(i++, p_senderVO.getSubscriberType());
            boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, method, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
            return updateCount;
        }
        }// end of try
        catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updatePinStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updatePinStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(p_con);
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally
    }

    /**
     * To update the postpaid information of the user which have its billing
     * cycle, billing date and credit limit
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int updatePostPaidInfo(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        boolean success = true;
        final String methodName = "updatePostPaidInfo";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered subscriberVO : " + p_senderVO);
        
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder(" UPDATE  p2p_subscribers set billing_type = ?,billing_cycle_date =?,");
            strBuff.append(" credit_limit = ? , modified_by = ? , modified_on = ? WHERE msisdn = ?  AND user_type = ? ");
            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Update  query:" + query);
           try(PreparedStatement psmt = p_con.prepareStatement(query);)
           {
            psmt.setString(1, p_senderVO.getBillingType());
            psmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getBillingCycleDate()));
            psmt.setLong(3, p_senderVO.getCreditLimit());
            psmt.setString(4, p_senderVO.getModifiedBy());
            psmt.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));

            psmt.setString(6, p_senderVO.getMsisdn());
            psmt.setString(7, p_senderVO.getSubscriberType());

            updateCount = psmt.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updatePostPaidInfo]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updatePostPaidInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Sucess :" + success);
        }// end of finally

        return updateCount;

    }

    /**
     * Register the subscriber in our system
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int registerSubscriber(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "registerSubscriber";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered subscriberVO : " + p_senderVO);
         
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            // insert query
            strBuff.append(" INSERT INTO p2p_subscribers  ");
            strBuff.append(" (user_id,msisdn, subscriber_type, prefix_id,status, network_code, pin, request_status, ");
            strBuff.append(" service_class_code,service_class_id,activated_on,registered_on,  created_on, created_by, modified_on, modified_by,language, country ) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "insert query:" + query);

            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            int m = 0;

            psmt.setString(++m, p_senderVO.getUserID());
            psmt.setString(++m, p_senderVO.getMsisdn());
            psmt.setString(++m, p_senderVO.getSubscriberType());
            psmt.setLong(++m, p_senderVO.getPrefixID());
            psmt.setString(++m, p_senderVO.getStatus());
            psmt.setString(++m, p_senderVO.getNetworkCode());
            psmt.setString(++m, BTSLUtil.encryptText(p_senderVO.getPin()));
            psmt.setString(++m, PretupsI.TXN_STATUS_COMPLETED);
            psmt.setString(++m, p_senderVO.getServiceClassCode());
            psmt.setString(++m, p_senderVO.getServiceClassID());
            if (p_senderVO.getActivatedOn() != null)
                psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
            else
                psmt.setNull(++m, Types.DATE);
            psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getRegisteredOn()));
            psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getCreatedOn()));
            psmt.setString(++m, p_senderVO.getCreatedBy());
            psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            psmt.setString(++m, p_senderVO.getModifiedBy());
            psmt.setString(++m, p_senderVO.getLanguage());
            psmt.setString(++m, p_senderVO.getCountry());
            updateCount = psmt.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSubscriber]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Success :" + updateCount);
        }// end of finally

        return updateCount;
    }

    /**
     * Returns an object of BuddVO that will contain Buddy information if the
     * passed buddy belong to the parent
     * 
     * @param p_con
     * @param p_parentID
     * @param p_buddy
     * @return BuddyVO
     * @throws BTSLBaseException
     */
    public BuddyVO loadBuddyDetails(Connection p_con, String p_parentID, String p_buddy) throws BTSLBaseException {
        final String methodName = "loadBuddyDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_parentID:" + p_parentID + " p_buddy:" + p_buddy);
        BuddyVO buddyVO = null;
        
        try {
            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("SELECT buddy_msisdn, parent_id, buddy_seq_num,buddy_name,status, ");
            selectQueryBuff.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type, ");
            selectQueryBuff.append("buddy_total_transfer, buddy_total_transfer_amt, created_on, created_by,modified_on, ");
            selectQueryBuff.append("modified_by,last_transfer_amount,prefix_id,preferred_amount ");
            selectQueryBuff.append("FROM p2p_buddies ");
            selectQueryBuff.append("WHERE parent_id=? AND (upper(buddy_name)=?  OR buddy_msisdn=?) ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_parentID);
            pstmtSelect.setString(2, p_buddy.toUpperCase());
            pstmtSelect.setString(3, p_buddy);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setStatus(rs.getString("status"));
                buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                if (rs.getTimestamp("buddy_last_transfer_on") != null)
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
                if (rs.getTimestamp("created_on") != null)
                    buddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                buddyVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("modified_on") != null)
                    buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                buddyVO.setModifiedBy(rs.getString("modified_by"));
                buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
            }
            return buddyVO;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting buddyVO:" + buddyVO);
        }// end of finally
    }

    /**
     * Method deleteSubscriber. Added by Sandeep Goel Created On 22-06-2005 This
     * method delete the data of subscriber and call another method which add
     * the history of that subscriber.
     * 
     * @param p_con
     *            Connection
     * @param p_senderVO
     *            SenderVO
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteSubscriber(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "deleteSubscriber";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_senderVO : " + p_senderVO);
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            // added by harsh to delete schedule batch on deleting subscriber
            this.deleteScheduleBatch(p_con, p_senderVO.getUserID());
            // end added by
            strBuff.append("DELETE FROM p2p_subscribers WHERE msisdn = ? AND subscriber_type=? AND network_code= ? ");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query:" + deleteQuery);
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_senderVO.getMsisdn());
            psmtDelete.setString(2, p_senderVO.getSubscriberType());
            psmtDelete.setString(3, p_senderVO.getNetworkCode());
            boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified)
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            else {
                deleteCount = psmtDelete.executeUpdate();
                if (deleteCount > 0)
                    deleteCount = this.addSubscriberHistory(p_con, p_senderVO);
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriber]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(psmtDelete);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:deleteCount=" + deleteCount);
        }// end of finally
        return deleteCount;
    }

    /**
     * Method addSubscriberHistory. Added by Sandeep Goel Created On 22-06-2005
     * This method is used to insert the data of subscriber to the history table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_senderVO
     *            SenderVO
     * @return int
     * @throws BTSLBaseException
     */
    private int addSubscriberHistory(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "addSubscriberHistory";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_senderVO:" + p_senderVO);
        
        int insertCount = 0;
        try {
            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO p2p_subscribers_history(user_id, msisdn, subscriber_type, user_name, ");
            insertQuery.append("status, network_code, pin, pin_block_count, last_transfer_amount, last_transfer_on, ");
            insertQuery.append("last_transfer_type, last_transfer_status, buddy_seq_number, total_transfers, ");
            insertQuery.append("total_transfer_amount, request_status, billing_type, billing_cycle_date, ");
            insertQuery.append("credit_limit, activated_on, registered_on, created_on, created_by, modified_on, ");
            insertQuery.append("modified_by, last_transfer_id, consecutive_failures, last_transfer_msisdn, ");
            insertQuery.append("skey_required, daily_transfer_count, monthly_transfer_count, weekly_transfer_count, ");
            insertQuery.append("prev_daily_transfer_count, prev_weekly_transfer_count, prev_monthly_transfer_count, ");
            insertQuery.append("service_class_code, daily_transfer_amount, weekly_transfer_amount, ");
            insertQuery.append("monthly_transfer_amount, prev_daily_transfer_amount, prev_weekly_transfer_amount, ");
            insertQuery.append("prev_monthly_transfer_amount, prev_transfer_date, prev_transfer_week_date, ");
            insertQuery.append(" prev_transfer_month_date, last_success_transfer_date, prefix_id, remarks)");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            String query = insertQuery.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Query:" + query);
           try (PreparedStatement pstmtInsert = p_con.prepareStatement(query);)
           {
            int i = 1;
            pstmtInsert.setString(i++, p_senderVO.getUserID());
            pstmtInsert.setString(i++, p_senderVO.getMsisdn());
            pstmtInsert.setString(i++, p_senderVO.getSubscriberType());
            pstmtInsert.setString(i++, p_senderVO.getUserName());

            pstmtInsert.setString(i++, p_senderVO.getStatus());
            pstmtInsert.setString(i++, p_senderVO.getNetworkCode());
            pstmtInsert.setString(i++, p_senderVO.getPin());
            pstmtInsert.setInt(i++, p_senderVO.getPinBlockCount());
            pstmtInsert.setLong(i++, p_senderVO.getLastTransferAmount());
            if (p_senderVO.getLastTransferOn() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
            else
                pstmtInsert.setTimestamp(i++, null);

            pstmtInsert.setString(i++, p_senderVO.getLastTransferType());
            pstmtInsert.setString(i++, p_senderVO.getLastTransferStatus());
            pstmtInsert.setInt(i++, p_senderVO.getBuddySeqNumber());
            pstmtInsert.setLong(i++, p_senderVO.getTotalTransfers());

            pstmtInsert.setLong(i++, p_senderVO.getTotalTransferAmount());
            pstmtInsert.setString(i++, p_senderVO.getRequestStatus());
            pstmtInsert.setString(i++, p_senderVO.getBillingType());
            if (p_senderVO.getBillingCycleDate() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getBillingCycleDate()));
            else
                pstmtInsert.setTimestamp(i++, null);

            pstmtInsert.setLong(i++, p_senderVO.getCreditLimit());
            if (p_senderVO.getActivatedOn() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
            else
                pstmtInsert.setTimestamp(i++, null);
            if (p_senderVO.getRegisteredOn() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getRegisteredOn()));
            else
                pstmtInsert.setTimestamp(i++, null);
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_senderVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));

            pstmtInsert.setString(i++, p_senderVO.getModifiedBy());
            pstmtInsert.setString(i++, p_senderVO.getLastTransferID());
            pstmtInsert.setLong(i++, p_senderVO.getConsecutiveFailures());
            pstmtInsert.setString(i++, p_senderVO.getLastTransferMSISDN());

            pstmtInsert.setString(i++, p_senderVO.getSkeyRequired());
            pstmtInsert.setLong(i++, p_senderVO.getDailyTransferCount());
            pstmtInsert.setLong(i++, p_senderVO.getMonthlyTransferCount());
            pstmtInsert.setLong(i++, p_senderVO.getWeeklyTransferCount());

            pstmtInsert.setLong(i++, p_senderVO.getPrevDailyTransferCount());
            pstmtInsert.setLong(i++, p_senderVO.getPrevWeeklyTransferCount());
            pstmtInsert.setLong(i++, p_senderVO.getPrevMonthlyTransferCount());

            pstmtInsert.setString(i++, p_senderVO.getServiceClassCode());
            pstmtInsert.setLong(i++, p_senderVO.getDailyTransferAmount());
            pstmtInsert.setLong(i++, p_senderVO.getWeeklyTransferAmount());

            pstmtInsert.setLong(i++, p_senderVO.getMonthlyTransferAmount());
            pstmtInsert.setLong(i++, p_senderVO.getPrevDailyTransferAmount());
            pstmtInsert.setLong(i++, p_senderVO.getPrevWeeklyTransferAmount());

            pstmtInsert.setLong(i++, p_senderVO.getPrevMonthlyTransferAmount());
            if (p_senderVO.getPrevTransferDate() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPrevTransferDate()));
            else
                pstmtInsert.setTimestamp(i++, null);
            if (p_senderVO.getPrevTransferWeekDate() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPrevTransferWeekDate()));
            else
                pstmtInsert.setTimestamp(i++, null);

            if (p_senderVO.getPrevTransferMonthDate() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPrevTransferMonthDate()));
            else
                pstmtInsert.setTimestamp(i++, null);
            if (p_senderVO.getLastSuccessTransferDate() != null)
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastSuccessTransferDate()));
            else
                pstmtInsert.setTimestamp(i++, null);
            pstmtInsert.setLong(i++, p_senderVO.getPrefixID());
            if (p_senderVO.getRemarks() != null && p_senderVO.getRemarks().length() > 100)
                pstmtInsert.setString(i++, p_senderVO.getRemarks().substring(99));
            else
                pstmtInsert.setString(i++, p_senderVO.getRemarks());

            insertCount = pstmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addSubscriberHistory]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addSubscriberHistory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting insertCount :" + insertCount);
        }// end of finally
        return insertCount;
    }

    /**
     * To add buddy in the subscriber List
     * 
     * @param p_con
     * @param p_buddyVO
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int addBuddy(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "addBuddy";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered buddy VO : " + p_buddyVO);
        
        int insertCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            // insert query
            strBuff.append(" INSERT INTO p2p_buddies  ");
            strBuff.append(" (buddy_msisdn, parent_id, buddy_seq_num, buddy_name,prefix_id, status, ");
            strBuff.append(" preferred_amount, created_on, created_by, modified_on, modified_by) ");
            strBuff.append(" VALUES ");
            strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?) ");
            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "insert query:" + query);

            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            psmt.setString(1, p_buddyVO.getMsisdn());
            psmt.setString(2, p_buddyVO.getOwnerUser());
            psmt.setInt(3, p_buddyVO.getSeqNumber());
            psmt.setString(4, p_buddyVO.getName());
            psmt.setLong(5, p_buddyVO.getPrefixID());
            psmt.setString(6, p_buddyVO.getStatus());
            psmt.setLong(7, p_buddyVO.getPreferredAmount());
            psmt.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getCreatedOn()));
            psmt.setString(9, p_buddyVO.getCreatedBy());
            psmt.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getModifiedOn()));
            psmt.setString(11, p_buddyVO.getModifiedBy());
            insertCount = psmt.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addBuddy]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addBuddy]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting insertCount  :" + insertCount);
        }// end of finally

        return insertCount;

    }

    /**
     * Delete the buddy from user buddy list
     * 
     * @param p_con
     * @param p_buddyVO
     * @return update Count
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int deleteBuddy(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "deleteBuddy";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered buddy VO : " + p_buddyVO);

        int deleteCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            // insert query
            strBuff.append(" DELETE FROM p2p_buddies  ");
            strBuff.append(" WHERE parent_id=? AND  buddy_msisdn=? ");

            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "insert query:" + query);

            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            psmt.setString(1, p_buddyVO.getOwnerUser());
            psmt.setString(2, p_buddyVO.getMsisdn());
            deleteCount = psmt.executeUpdate();

            // entry in the history table
            if (deleteCount > 0) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(p_buddyVO);
                // deleteCount = this.addBuddyHistory(p_con, arrayList);
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteBuddy]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteBuddy]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting update Count :" + deleteCount);
        }// end of finally
        return deleteCount;
    }

    /**
     * To delete all the buddies of the user and move them into history table
     * 
     * @param p_con
     * @param p_senderVO
     *            SenderVO
     * @return int status
     * @throws BTSLBaseException
     */
    public int deleteBuddiesList(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "deleteBuddiesList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered Sender VO  : " + p_senderVO);
         
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder(100);
            // insert query
            strBuff.append(" DELETE FROM p2p_buddies  ");
            strBuff.append(" WHERE parent_id=? ");

            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "delete query:" + query);

            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            psmt.setString(1, p_senderVO.getUserID());
            updateCount = psmt.executeUpdate();
            /*
             * if (updateCount > 0)
             * {
             * updateCount = this.addBuddyHistory(p_con,
             * p_senderVO.getVoList());
             * }
             */
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteBuddiesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteBuddiesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting update Count :" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * Returns Buddy List information if the passed buddyList belong to the
     * parent
     * 
     * @param p_con
     * @param p_listname
     * @param p_sendermsisdn
     * @return list
     * @throws BTSLBaseException
     * @author harsh dixit
     * @date 10 Aug 12
     */
    public ArrayList loadBuddyListDetails(Connection p_con, String p_listname, String p_sendermsisdn) throws BTSLBaseException {
        final String methodName = "loadBuddyListDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered senderMsisdn:" + p_sendermsisdn + " p_buddyListName:" + p_listname);

        BuddyVO buddyVO = null;
        ArrayList list = new ArrayList();
        try {
            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("SELECT buddy_msisdn, parent_id, buddy_seq_num,buddy_name,status, ");
            selectQueryBuff.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type, ");
            selectQueryBuff.append("buddy_total_transfer, buddy_total_transfer_amt, created_on, created_by,modified_on, ");
            selectQueryBuff.append("modified_by,last_transfer_amount,prefix_id,preferred_amount,list_name,selector_code ");
            selectQueryBuff.append("FROM p2p_buddies WHERE parent_id IN (Select user_id from P2P_Subscribers where msisdn=?) ");
            if (!BTSLUtil.isNullString(p_listname))
                selectQueryBuff.append(" AND list_name=?");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_sendermsisdn);
            if (!BTSLUtil.isNullString(p_listname))
                pstmtSelect.setString(2, p_listname);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setStatus(rs.getString("status"));
                buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                if (rs.getTimestamp("buddy_last_transfer_on") != null)
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
                if (rs.getTimestamp("created_on") != null)
                    buddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                buddyVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("modified_on") != null)
                    buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                buddyVO.setModifiedBy(rs.getString("modified_by"));
                buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                buddyVO.setListName(rs.getString("list_name"));
                buddyVO.setSelectorCode(rs.getString("selector_code"));
                list.add(buddyVO);
            }

        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyListDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyListDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting list size:" + list.size());
        }// end of finally
        return list;
    }

    // end of loadBuddyListDetails by harsh

    /**
     * To delete buddyList of the user and move them into history table
     * 
     * @param p_con
     * @param String
     *            p_listname
     * @param String
     *            p_sendermsisdn
     * @return int updateCount
     * @throws BTSLBaseException
     * @author Harsh Dixit
     * @date 10 Aug 12
     */
    public int delMultCreditTrfList(Connection p_con, String p_listname, String p_sendermsisdn) throws BTSLBaseException {
        final String methodName = "delMultCreditTrfList";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered Sender Msisdn  : " + p_sendermsisdn);
        
        int updateCount = 0;
        ArrayList list = new ArrayList();
        list = this.loadBuddyListDetails(p_con, p_listname, p_sendermsisdn);
        try {

            StringBuilder strBuff = new StringBuilder(100);
            // insert query
            strBuff.append("DELETE FROM p2p_buddies  ");
            strBuff.append("WHERE parent_id IN (SELECT user_id FROM P2P_Subscribers WHERE msisdn=?)");
            if (!BTSLUtil.isNullString(p_listname))
                strBuff.append(" AND list_name=? ");

            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "delete query:" + query);

            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            psmt.setString(1, p_sendermsisdn);
            if (!BTSLUtil.isNullString(p_listname))
                psmt.setString(2, p_listname);
            updateCount = psmt.executeUpdate();
            if (updateCount > 0) {
                // updateCount = this.addBuddyHistory(p_con, list);
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[delMultCreditTrfList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[delMultCreditTrfList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting update Count :" + updateCount);
        }// end of finally
        return updateCount;
    }

    // end of delMultCreditTrfList by harsh
    /**
     * @param p_con
     * @param p_buddyList
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    private int addBuddyHistory(Connection p_con, ArrayList p_buddyList) throws BTSLBaseException {

        final String methodName = "addBuddyHistory";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered buddy List : " + p_buddyList.size());
 
        int updateCount = 0;
        BuddyVO buddyVO = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            // insert query

            strBuff.append(" INSERT INTO p2p_buddies_history ( buddy_msisdn, parent_id, buddy_seq_num, buddy_name, status, buddy_last_transfer_id, ");
            strBuff.append(" buddy_last_transfer_on, buddy_last_transfer_type, buddy_total_transfer,  ");
            strBuff.append(" buddy_total_transfer_amt, created_on, created_by, modified_on, modified_by,  ");
            strBuff.append(" preferred_amount, last_transfer_amount, prefix_id,");
            strBuff.append("list_name,selector_code,action)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'D') ");

            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "insert query:" + query);
            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            int m = 0;
            for (int i = 0, k = p_buddyList.size(); i < k; i++) {
                buddyVO = (BuddyVO) p_buddyList.get(i);
                m = 0;

                psmt.setString(++m, buddyVO.getMsisdn());
                psmt.setString(++m, buddyVO.getOwnerUser());
                psmt.setInt(++m, buddyVO.getSeqNumber());
                psmt.setString(++m, buddyVO.getName());
                psmt.setString(++m, buddyVO.getStatus());
                psmt.setString(++m, buddyVO.getLastTransferID());
                psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(buddyVO.getLastTransferOn()));
                psmt.setString(++m, buddyVO.getLastTransferType());
                psmt.setLong(++m, buddyVO.getBuddyTotalTransfers());
                psmt.setLong(++m, buddyVO.getBuddyTotalTransferAmount());
                psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(buddyVO.getCreatedOn()));
                psmt.setString(++m, buddyVO.getCreatedBy());
                psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(buddyVO.getModifiedOn()));
                psmt.setString(++m, buddyVO.getModifiedBy());
                psmt.setLong(++m, buddyVO.getLastTransferAmount());
                psmt.setLong(++m, buddyVO.getPreferredAmount());
                psmt.setLong(++m, buddyVO.getPrefixID());
                psmt.setString(++m, buddyVO.getListName());
                psmt.setString(++m, buddyVO.getSelectorCode());

                updateCount = psmt.executeUpdate();

                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.processing");
                }
                // clearing the parameters
                psmt.clearParameters();
                updateCount++;
            }
        }
        }// end of try
        catch (BTSLBaseException e) {
            _log.error(methodName, "SQLException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addBuddyHistory]", "", "", "", "BTSL Exception:" + e.getMessage());
            throw e;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addBuddyHistory]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addBuddyHistory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting update Count :" + updateCount);
        }// end of finally

        return updateCount;
    }

    /**
     * Load the buddy list of the P2P Subscriber
     * 
     * @param p_con
     * @param p_parentID
     * @return ArrayList which have the object of BuddyVO
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public ArrayList loadBuddyList(Connection p_con, String p_parentID) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("getBuddyList", "Entered p_parentID:" + p_parentID);
       
        ArrayList list = new ArrayList();
        BuddyVO buddyVO = null;
        final String methodName = "loadBuddyList";
        try {

            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("SELECT buddy_msisdn,parent_id, buddy_seq_num,buddy_name, prefix_id,status,  ");
            selectQueryBuff.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type,");
            selectQueryBuff.append("buddy_total_transfer,buddy_total_transfer_amt,created_on, created_by,modified_on, ");
            selectQueryBuff.append("modified_by,last_transfer_amount,preferred_amount ");
            selectQueryBuff.append("FROM p2p_buddies ");
            selectQueryBuff.append("WHERE parent_id=? ");
            selectQueryBuff.append("ORDER BY buddy_seq_num ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadBuddyDetails", "select query:" + selectQuery);
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_parentID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setStatus(rs.getString("status"));
                buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                if (rs.getTimestamp("buddy_last_transfer_on") != null)
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                if (rs.getTimestamp("buddy_last_transfer_on") != null)
                    buddyVO.setLastTxnOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("buddy_last_transfer_on")));
                buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
                if (rs.getTimestamp("created_on") != null)
                    buddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                if (rs.getTimestamp("created_on") != null)
                    buddyVO.setCreatedOnAsString(BTSLUtil.getDateStringFromDate(rs.getTimestamp("created_on")));
                buddyVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("modified_on") != null)
                    buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                buddyVO.setModifiedBy(rs.getString("modified_by"));
                buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                list.add(buddyVO);
            }

        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
            if (_log.isDebugEnabled())
                _log.debug("getBuddyList", "Exiting buddyVO:" + list.size());
        }// end of finally
        return list;
    }

    /**
     * Method checkRequestUnderProcess. This method is used to check is the
     * transaction under process?
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_filteredMsisdn
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean checkRequestUnderProcess(Connection p_con, String p_filteredMsisdn) throws BTSLBaseException {
        final String methodName = "checkRequestUnderProcess";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_filteredMsisdn:" + p_filteredMsisdn);
        
        boolean status = false;
         
        String selectQuery = "SELECT last_transfer_status FROM p2p_subscribers WHERE msisdn=?";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "selectquery:" + selectQuery);
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
           
            pstmtSelect.setString(1, p_filteredMsisdn);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                if (rs.getString("last_transfer_status") != null && rs.getString("last_transfer_status").equals(PretupsI.TXN_STATUS_UNDER_PROCESS))
                    status = true;
            } else {
                status = true;
                throw new BTSLBaseException(this, methodName, "p2psubscriber.msg.subsdeleted");
            }
        }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:status:" + status);
        }// end of finally
        return status;
    }

    /**
     * Method loadSubscriberDetails. This method loads the details of the
     * subscribers based on the MSISDN or/and the DATE RANGE
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_status
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @throws throws
     *         BTSLBaseException
     */
    public ArrayList loadSubscriberDetails(Connection p_con, String p_msisdn, Date p_fromDate, Date p_toDate, String p_status) throws BTSLBaseException {
        final String methodName = "loadSubscriberDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_msisdn:" + p_msisdn + ",p_fromDate=" + p_fromDate + ",p_toDate=" + p_toDate + ",p_status=" + p_status);
        PreparedStatement pstmtSelect = null;
        SenderVO senderVO = null;
        ArrayList subscriberList = new ArrayList();
        ResultSet rs = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append(" SELECT KV.value txnstatus,P2P_SUB.user_id,P2P_SUB.msisdn,P2P_SUB.subscriber_type,P2P_SUB.prefix_id,P2P_SUB.user_name,P2P_SUB.status,P2P_SUB.network_code,P2P_SUB.pin,P2P_SUB.pin_block_count,");
            selectQueryBuff.append(" P2P_SUB.last_transfer_amount,P2P_SUB.last_transfer_on,P2P_SUB.last_transfer_type,P2P_SUB.last_transfer_status,P2P_SUB.last_transfer_id,P2P_SUB.last_transfer_msisdn,");
            selectQueryBuff.append(" P2P_SUB.buddy_seq_number,P2P_SUB.total_transfers,P2P_SUB.total_transfer_amount,P2P_SUB.request_status,P2P_SUB.billing_type,P2P_SUB.billing_cycle_date, ");
            selectQueryBuff.append(" P2P_SUB.credit_limit,P2P_SUB.activated_on,P2P_SUB.registered_on,P2P_SUB.created_on,P2P_SUB.created_by,P2P_SUB.modified_on,P2P_SUB.modified_by,P2P_SUB.consecutive_failures,L.lookup_name, ");
            selectQueryBuff.append(" P2P_SUB.skey_required,P2P_SUB.daily_transfer_count,P2P_SUB.monthly_transfer_count,P2P_SUB.weekly_transfer_count,P2P_SUB.daily_transfer_amount, ");
            selectQueryBuff.append(" P2P_SUB.monthly_transfer_amount,P2P_SUB.weekly_transfer_amount,P2P_SUB.prev_daily_transfer_count,P2P_SUB.prev_monthly_transfer_count, ");
            selectQueryBuff.append(" P2P_SUB.prev_weekly_transfer_count,P2P_SUB.prev_daily_transfer_amount,P2P_SUB.prev_monthly_transfer_amount,P2P_SUB.prev_weekly_transfer_amount, ");
            selectQueryBuff.append(" P2P_SUB.prev_transfer_date,P2P_SUB.prev_transfer_week_date,P2P_SUB.prev_transfer_month_date,P2P_SUB.service_class_code,P2P_SUB.last_success_transfer_date,P2P_SUB.language,P2P_SUB.country,P2P_SUB.service_class_id ");
            selectQueryBuff.append(" FROM p2p_subscribers P2P_SUB, lookups L , key_values KV ");
            selectQueryBuff.append("WHERE P2P_SUB.subscriber_type=L.lookup_code AND L.lookup_type=? AND KV.key(+)=P2P_SUB.last_transfer_status AND KV.type(+)=? ");

            if (!p_status.equals(PretupsI.ALL))
                selectQueryBuff.append("AND P2P_SUB.status=? ");
            if (p_msisdn != null && p_msisdn.length() > 0)
                selectQueryBuff.append("AND msisdn=? ");
            if (p_fromDate != null && p_toDate != null)
                selectQueryBuff.append("AND TRUNC(registered_on) >= ? AND TRUNC(registered_on) <=? ");
            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);

            int i = 1;
            pstmtSelect.setString(i++, PretupsI.SUBSRICBER_TYPE);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_P2P_STATUS);
            if (!p_status.equals(PretupsI.ALL))
                pstmtSelect.setString(i++, p_status);

            if (p_msisdn != null && p_msisdn.length() > 0)
                pstmtSelect.setString(i++, p_msisdn);

            if (p_fromDate != null && p_toDate != null) {
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            }

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                senderVO = new SenderVO();
                senderVO.setLanguage(rs.getString("language"));
                senderVO.setCountry(rs.getString("country"));
                senderVO.setUserID(rs.getString("user_id"));
                senderVO.setMsisdn(rs.getString("msisdn"));
                senderVO.setSubscriberType(rs.getString("subscriber_type"));
                senderVO.setUserName(rs.getString("user_name"));
                senderVO.setStatus(rs.getString("status"));
                senderVO.setNetworkCode(rs.getString("network_code"));
                senderVO.setPin(BTSLUtil.decryptText(rs.getString("pin")));
                senderVO.setPinBlockCount(rs.getInt("pin_block_count"));

                senderVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                senderVO.setLastTransferOn(rs.getDate("last_transfer_on"));
                if (rs.getDate("last_transfer_on") != null)
                    senderVO.setLastTxnOnAsString(BTSLUtil.getDateStringFromDate(rs.getDate("last_transfer_on")));
                senderVO.setSubscriberTypeDescription(rs.getString("lookup_name"));
                senderVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());

                senderVO.setLastTransferID(rs.getString("last_transfer_id"));
                senderVO.setLastTransferType(rs.getString("last_transfer_type"));
                senderVO.setLastTransferStatus(rs.getString("last_transfer_status"));
                senderVO.setLastTransferMSISDN(rs.getString("last_transfer_msisdn"));
                senderVO.setBuddySeqNumber(rs.getInt("buddy_seq_number"));
                senderVO.setTotalTransfers(rs.getLong("total_transfers"));
                senderVO.setTotalTransferAmount(rs.getLong("total_transfer_amount"));
                senderVO.setRequestStatus(rs.getString("request_status"));
                senderVO.setBillingType(rs.getString("billing_type"));
                if (rs.getTimestamp("billing_cycle_date") != null)
                    senderVO.setBillingCycleDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("billing_cycle_date")));
                senderVO.setCreditLimit(rs.getLong("credit_limit"));
                senderVO.setCreditLimitStr(PretupsBL.getDisplayAmount(rs.getLong("credit_limit")));
                senderVO.setActivatedOn(rs.getDate("activated_on"));
                if (rs.getDate("activated_on") != null)
                    senderVO.setActivatedOnAsString(BTSLUtil.getDateStringFromDate(rs.getDate("activated_on")));
                senderVO.setRegisteredOn(rs.getDate("registered_on"));
                if (rs.getDate("registered_on") != null)
                    senderVO.setRegisteredOnAsString(BTSLUtil.getDateStringFromDate(rs.getDate("registered_on")));

                senderVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                senderVO.setCreatedBy(rs.getString("created_by"));
                senderVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                senderVO.setModifiedBy(rs.getString("modified_by"));
                senderVO.setConsecutiveFailures(rs.getLong("consecutive_failures"));
                senderVO.setSkeyRequired(rs.getString("skey_required"));

                senderVO.setDailyTransferCount(rs.getLong("daily_transfer_count"));
                senderVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));
                senderVO.setWeeklyTransferCount(rs.getLong("weekly_transfer_count"));

                senderVO.setDailyTransferAmount(rs.getLong("daily_transfer_amount"));
                senderVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                senderVO.setWeeklyTransferAmount(rs.getLong("weekly_transfer_amount"));

                senderVO.setPrevDailyTransferCount(rs.getLong("prev_daily_transfer_count"));
                senderVO.setPrevMonthlyTransferCount(rs.getLong("prev_monthly_transfer_count"));
                senderVO.setPrevWeeklyTransferCount(rs.getLong("prev_weekly_transfer_count"));
                senderVO.setPrevDailyTransferAmount(rs.getLong("prev_daily_transfer_amount"));
                senderVO.setPrevMonthlyTransferAmount(rs.getLong("prev_monthly_transfer_amount"));
                senderVO.setPrevWeeklyTransferAmount(rs.getLong("prev_weekly_transfer_amount"));
                if (rs.getTimestamp("prev_transfer_date") != null)
                    senderVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_date")));
                if (rs.getTimestamp("prev_transfer_week_date") != null)
                    senderVO.setPrevTransferWeekDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_week_date")));
                if (rs.getTimestamp("prev_transfer_month_date") != null)
                    senderVO.setPrevTransferMonthDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_month_date")));
                senderVO.setServiceClassCode(rs.getString("service_class_code"));
                if (rs.getTimestamp("last_success_transfer_date") != null)
                    senderVO.setLastSuccessTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_transfer_date")));
                senderVO.setServiceClassID(rs.getString("service_class_id"));
                senderVO.setLastTransferStatusDesc(rs.getString("txnstatus"));
                subscriberList.add(senderVO);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmtSelect);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting list.size=" + subscriberList.size());
        }// end of finally
        return subscriberList;
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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_userId=" + p_userId);
        
        boolean modified = false;
        StringBuilder sqlRecordModified = new StringBuilder();
        sqlRecordModified.append("SELECT modified_on FROM p2p_subscribers ");
        sqlRecordModified.append("WHERE user_id=? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY=" + sqlRecordModified);
        String query = sqlRecordModified.toString();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(query);){
        
            pstmtSelect.setString(1, p_userId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record may be deleted
            // during the transaction .
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified)
                modified = true;
        }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isRecordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isRecordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exititng:modified=" + modified);
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method checkAmbiguousTransfer. This method is used to check is any
     * transaction status is ambiguous?
     * 
     * @param p_con
     * @param p_filteredMsisdn
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean checkAmbiguousTransfer(Connection p_con, String p_filteredMsisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkAmbiguousTransfer", "Entered p_filteredMsisdn:" + p_filteredMsisdn);

         
        boolean status = false;
        
        final String methodName = "checkRequestUnderProcess";
        String selectQuery = "SELECT transfer_status FROM subscriber_transfers  WHERE sender_msisdn=? AND (transfer_status=? OR transfer_status=?) ";
        if (_log.isDebugEnabled())
            _log.debug("checkAmbiguousTransfer", "selectquery:" + selectQuery);
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, p_filteredMsisdn);
            pstmtSelect.setString(2, SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtSelect.setString(3, SelfTopUpErrorCodesI.TXN_STATUS_UNDER_PROCESS);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                status = true;
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "checkAmbiguousTransfer", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "checkAmbiguousTransfer", "error.general.processing");
        }// end of catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug("checkAmbiguousTransfer", "Exiting: status:" + status);
        }// end of finally
        return status;
    }

    /**
     * Method to update the control parameters of the subscriber
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubscriberCountersDetails(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberCountersDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_subscriber :" + p_senderVO);
       
        int updateCount = 0;
        try {
            boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {

                StringBuilder sbf = new StringBuilder();

                sbf.append(" UPDATE p2p_subscribers SET last_transfer_amount=?,last_transfer_on=?,last_transfer_type=?, ");
                sbf.append(" last_transfer_status=?,last_transfer_id=?,last_transfer_msisdn=?,total_transfers=?,total_transfer_amount=?, ");
                sbf.append(" consecutive_failures=?,daily_transfer_count=?,monthly_transfer_count=?,weekly_transfer_count=?,daily_transfer_amount=?, ");
                sbf.append(" monthly_transfer_amount=?,weekly_transfer_amount=?,prev_daily_transfer_count=?,prev_monthly_transfer_count=?, ");
                sbf.append(" prev_weekly_transfer_count=?,prev_daily_transfer_amount=?,prev_monthly_transfer_amount=?,prev_weekly_transfer_amount=?, ");
                sbf.append(" prev_transfer_date=?,prev_transfer_week_date=?,prev_transfer_month_date=?,last_success_transfer_date=?,modified_on=?,modified_by=? ");
                sbf.append(" WHERE user_id=? ");
                String updateQuery = sbf.toString();
                if (_log.isDebugEnabled())
                    _log.debug(methodName, "select query:" + updateQuery);

                int i = 1;
                try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
                {
                pstmtUpdate.setLong(i++, p_senderVO.getLastTransferAmount());
                if (p_senderVO.getLastTransferOn() != null)
                    pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
                else
                    pstmtUpdate.setNull(i++, Types.DATE);
                pstmtUpdate.setString(i++, p_senderVO.getLastTransferType());
                pstmtUpdate.setString(i++, p_senderVO.getLastTransferStatus());
                pstmtUpdate.setString(i++, p_senderVO.getLastTransferID());
                pstmtUpdate.setString(i++, p_senderVO.getLastTransferMSISDN());
                pstmtUpdate.setLong(i++, p_senderVO.getTotalTransfers());
                pstmtUpdate.setLong(i++, p_senderVO.getTotalTransferAmount());
                pstmtUpdate.setLong(i++, p_senderVO.getConsecutiveFailures());
                pstmtUpdate.setLong(i++, p_senderVO.getDailyTransferCount());
                pstmtUpdate.setLong(i++, p_senderVO.getMonthlyTransferCount());
                pstmtUpdate.setLong(i++, p_senderVO.getWeeklyTransferCount());
                pstmtUpdate.setLong(i++, p_senderVO.getDailyTransferAmount());
                pstmtUpdate.setLong(i++, p_senderVO.getMonthlyTransferAmount());
                pstmtUpdate.setLong(i++, p_senderVO.getWeeklyTransferAmount());
                pstmtUpdate.setLong(i++, p_senderVO.getPrevDailyTransferCount());
                pstmtUpdate.setLong(i++, p_senderVO.getPrevMonthlyTransferCount());
                pstmtUpdate.setLong(i++, p_senderVO.getPrevWeeklyTransferCount());
                pstmtUpdate.setLong(i++, p_senderVO.getPrevDailyTransferAmount());
                pstmtUpdate.setLong(i++, p_senderVO.getPrevMonthlyTransferAmount());
                pstmtUpdate.setLong(i++, p_senderVO.getPrevWeeklyTransferAmount());
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferDate()));
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferWeekDate()));
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferMonthDate()));
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getLastSuccessTransferDate()));

                if (p_senderVO.getModifiedOn() != null)
                    pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                else
                    pstmtUpdate.setNull(i++, Types.DATE);
                pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
                pstmtUpdate.setString(i++, p_senderVO.getUserID());
                updateCount = pstmtUpdate.executeUpdate();
            }

        }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "", p_senderVO.getMsisdn(), "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateSubscriberDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "", p_senderVO.getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * Method to update the buddy details
     * 
     * @param p_con
     * @param p_buddyVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateBuddyDetails(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "updateBuddyDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_buddyVO :" + p_buddyVO);
        
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder(" UPDATE p2p_buddies SET buddy_last_transfer_id=?,buddy_last_transfer_on=?,buddy_last_transfer_type=?, ");
            sbf.append(" last_transfer_amount=?,buddy_total_transfer=?,buddy_total_transfer_amt=?,modified_on=?, modified_by=? ");
            sbf.append(" WHERE buddy_msisdn=? AND  parent_id=? ");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + updateQuery);

            int i = 1;

            try( PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            pstmtUpdate.setString(i++, p_buddyVO.getLastTransferID());
            if (p_buddyVO.getLastTransferOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getLastTransferOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_buddyVO.getLastTransferType());
            pstmtUpdate.setLong(i++, p_buddyVO.getLastTransferAmount());
            pstmtUpdate.setLong(i++, p_buddyVO.getBuddyTotalTransfers());
            pstmtUpdate.setLong(i++, p_buddyVO.getBuddyTotalTransferAmount());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_buddyVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_buddyVO.getMsisdn());
            pstmtUpdate.setString(i++, p_buddyVO.getOwnerUser());
            updateCount = pstmtUpdate.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateBuddyDetails]", "", p_buddyVO.getMsisdn(), "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateBuddyDetails]", "", p_buddyVO.getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * Method loadPostPaidControlParameters.
     * method to load controlling parameters of the postpaid subscriber.
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return PostPaidControlParametersVO
     * @throws BTSLBaseException
     */
    public PostPaidControlParametersVO loadPostPaidControlParameters(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadPostPaidControlParameters";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_msisdn:" + p_msisdn);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PostPaidControlParametersVO postPaidControlParametersVO = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT daily_transfers_allowed,daily_transfer_amt_allowed,weekly_transfers_allowed,weekly_transfer_amt_allowed, ");
            selectQueryBuff.append(" monthly_transfers_allowed,monthly_transfer_amt_allowed, ");
            // Added on 07/02/08
            selectQueryBuff.append(" date_time ");
            selectQueryBuff.append(" FROM postpaid_control_parameters ");
            selectQueryBuff.append(" WHERE msisdn=? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msisdn);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                postPaidControlParametersVO = new PostPaidControlParametersVO();
                postPaidControlParametersVO.setDailyTransferAllowed(rs.getLong("daily_transfers_allowed"));
                postPaidControlParametersVO.setDailyTransferAmountAllowed(rs.getLong("daily_transfer_amt_allowed"));
                postPaidControlParametersVO.setWeeklyTransferAllowed(rs.getLong("weekly_transfers_allowed"));
                postPaidControlParametersVO.setWeeklyTransferAmountAllowed(rs.getLong("weekly_transfer_amt_allowed"));
                postPaidControlParametersVO.setMonthlyTransferAllowed(rs.getLong("monthly_transfers_allowed"));
                postPaidControlParametersVO.setMonthlyTransferAmountAllowed(rs.getLong("monthly_transfer_amt_allowed"));
                // Added on 07/02/08
                postPaidControlParametersVO.setDateTime(rs.getDate("date_time"));
            }
            return postPaidControlParametersVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPostPaidControlParameters]", "", p_msisdn, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPostPaidControlParameters]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmtSelect);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting postPaidControlParametersVO:" + postPaidControlParametersVO);
        }// end of finally
    }

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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered: p_senderVO=" + p_senderVO);
        
        int insertCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            // insert query
            strBuff.append("INSERT INTO postpaid_control_parameters (msisdn, daily_transfers_allowed, ");
            strBuff.append("daily_transfer_amt_allowed, weekly_transfers_allowed, weekly_transfer_amt_allowed,");
            strBuff.append("monthly_transfers_allowed, monthly_transfer_amt_allowed, date_time)");
            strBuff.append("VALUES (?,?,?,?,?,?,?,?)");
            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "insert query:" + query);

            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
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
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addPostpaidControlParameters]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addPostpaidControlParameters]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting insertCount  :" + insertCount);
        }// end of finally

        return insertCount;
    }

    /**
     * Method to load the subscriber details and locking the record before
     * update
     * 
     * @param p_con
     * @param p_requestID
     * @param p_userID
     * @return SenderVO
     * @throws BTSLBaseException
     */
    public SenderVO loadSubscriberDetailsByIDForUpdate(Connection p_con, String p_requestID, String p_userID) throws BTSLBaseException {
        final String methodName = "loadSubscriberDetailsByIDForUpdate";
        if (_log.isDebugEnabled())
            _log.debug(methodName, p_requestID, "Entered p_userID:" + p_userID);
        PreparedStatement pstmtSelect = null;
        SenderVO senderVO = null;
        ResultSet rs = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder("SELECT user_id,msisdn,subscriber_type,prefix_id,network_code,last_transfer_amount,last_transfer_on,last_transfer_type,last_transfer_status,last_transfer_id,last_transfer_msisdn,");
            selectQueryBuff.append(" total_transfers,total_transfer_amount, credit_limit,consecutive_failures,");
            selectQueryBuff.append(" daily_transfer_count,monthly_transfer_count,weekly_transfer_count,daily_transfer_amount,");
            selectQueryBuff.append(" monthly_transfer_amount,weekly_transfer_amount,prev_daily_transfer_count,prev_monthly_transfer_count,");
            selectQueryBuff.append(" prev_weekly_transfer_count,prev_daily_transfer_amount,prev_monthly_transfer_amount,prev_weekly_transfer_amount,");
            selectQueryBuff.append(" prev_transfer_date,prev_transfer_week_date,prev_transfer_month_date,service_class_code,last_success_transfer_date");
            // DB220120123for update WITH RS
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
                selectQueryBuff.append(" FROM P2P_SUBSCRIBERS WHERE user_id=? FOR UPDATE With RS");
            else
                selectQueryBuff.append(" FROM P2P_SUBSCRIBERS WHERE user_id=? FOR UPDATE ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberDetailsByMsisdn", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                senderVO = new SenderVO();
                senderVO.setUserID(rs.getString("user_id"));
                senderVO.setMsisdn(rs.getString("msisdn"));
                senderVO.setSubscriberType(rs.getString("subscriber_type"));
                senderVO.setPrefixID(rs.getLong("prefix_id"));
                senderVO.setNetworkCode(rs.getString("network_code"));
                senderVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                senderVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transfer_on")));
                senderVO.setLastTransferID(rs.getString("last_transfer_id"));
                senderVO.setLastTransferType(rs.getString("last_transfer_type"));
                senderVO.setLastTransferStatus(rs.getString("last_transfer_status"));
                senderVO.setLastTransferMSISDN(rs.getString("last_transfer_msisdn"));
                senderVO.setTotalTransfers(rs.getLong("total_transfers"));
                senderVO.setTotalTransferAmount(rs.getLong("total_transfer_amount"));
                senderVO.setCreditLimit(rs.getLong("credit_limit"));
                senderVO.setConsecutiveFailures(rs.getLong("consecutive_failures"));
                senderVO.setDailyTransferCount(rs.getLong("daily_transfer_count"));
                senderVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));
                senderVO.setWeeklyTransferCount(rs.getLong("weekly_transfer_count"));
                senderVO.setDailyTransferAmount(rs.getLong("daily_transfer_amount"));
                senderVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                senderVO.setWeeklyTransferAmount(rs.getLong("weekly_transfer_amount"));

                senderVO.setPrevDailyTransferCount(rs.getLong("prev_daily_transfer_count"));
                senderVO.setPrevMonthlyTransferCount(rs.getLong("prev_monthly_transfer_count"));
                senderVO.setPrevWeeklyTransferCount(rs.getLong("prev_weekly_transfer_count"));
                senderVO.setPrevDailyTransferAmount(rs.getLong("prev_daily_transfer_amount"));
                senderVO.setPrevMonthlyTransferAmount(rs.getLong("prev_monthly_transfer_amount"));
                senderVO.setPrevWeeklyTransferAmount(rs.getLong("prev_weekly_transfer_amount"));
                senderVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_date")));
                senderVO.setPrevTransferWeekDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_week_date")));
                senderVO.setPrevTransferMonthDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_month_date")));
                senderVO.setServiceClassCode(rs.getString("service_class_code"));
                senderVO.setLastSuccessTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_transfer_date")));
            }
            return senderVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByIDForUpdate]", p_requestID, "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByIDForUpdate]", p_requestID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmtSelect);
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberDetails", p_requestID, "Exiting senderVO:" + senderVO);
        }// end of finally
    }

    /**
     * Returns an object of BuddVO that will contain Buddy information if the
     * buddy is exist for BuddynName and Mobli
     * for the parent
     * 
     * @param p_con
     * @param p_parentID
     * @param p_buddyName
     * @param p_buddyMobileNo
     * @return BuddyVO
     * @throws BTSLBaseException
     */
    public BuddyVO subscriberBuddyExist(Connection p_con, String p_parentID, String p_buddyName, String p_buddyMobileNo) throws BTSLBaseException {
        final String methodName = "subscriberBuddyExist";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_parentID:" + p_parentID + " p_buddyName:" + p_buddyName + "p_buddyMobileNo:" + p_buddyMobileNo);

        BuddyVO buddyVO = null;
        
        try {
            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append(" SELECT buddy_msisdn, parent_id, buddy_seq_num,buddy_name,status ");
            selectQueryBuff.append(" FROM p2p_buddies ");
            selectQueryBuff.append(" WHERE parent_id = ? AND ( upper(buddy_name) = upper(?)  OR buddy_msisdn = ? ) ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);

            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_parentID);
            pstmtSelect.setString(2, p_buddyName);
            pstmtSelect.setString(3, p_buddyMobileNo);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setStatus(rs.getString("status"));
            }
            return buddyVO;

        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting buddyVO:" + buddyVO);
        }// end of finally
    }// end subscriberBuddyExist

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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_msisdn:" + p_msisdn + ",p_serviceClassID=" + p_serviceClassID + ",p_networkCode=" + p_networkCode);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs1 = null;

        ArrayList parameterList = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("SELECT SP.preference_code , SP.name,SP.default_value , SP.value_type  ");
            selectQueryBuff.append("FROM system_preferences SP ");
            selectQueryBuff.append("WHERE SP.display='Y' AND  SP.module=? AND SP.type=? ");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQueryBuff);
            pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.P2P_MODULE);
            pstmtSelect.setString(i++, PreferenceI.SERVICE_CLASS_LEVEL);
            rs = pstmtSelect.executeQuery();
            parameterList = new ArrayList();
            HashMap preferenceMap = new HashMap();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                if (PreferenceI.TYPE_AMOUNT.equals(rs.getString("value_type")))
                    listValueVO = new ListValueVO(rs.getString("name"), PretupsBL.getDisplayAmount(rs.getLong("default_value")));
                else
                    listValueVO = new ListValueVO(rs.getString("name"), rs.getString("default_value"));
                preferenceMap.put(rs.getString("preference_code"), listValueVO);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "preferenceMap:" + preferenceMap);
            if (!BTSLUtil.isNullString(p_serviceClassID)) {
                StringBuilder selectQueryBuff1 = new StringBuilder();
                selectQueryBuff1.append("SELECT SP.preference_code ,SP.name,SCP.value,SP.value_type ");
                selectQueryBuff1.append("FROM service_class_preferences SCP,system_preferences SP,p2p_subscribers PSUB ");
                selectQueryBuff1.append("WHERE SCP.preference_code=SP.preference_code AND SP.display='Y'AND ");
                selectQueryBuff1.append("SCP.network_code=? AND SP.module=? AND PSUB.msisdn=? ");
                selectQueryBuff1.append("AND PSUB.service_class_id=SCP.service_class_id ");
                if (_log.isDebugEnabled())
                    _log.debug(methodName, "select query:" + selectQueryBuff1);
                pstmtSelect1 = p_con.prepareStatement(selectQueryBuff1.toString());
                i = 1;
                pstmtSelect1.setString(i++, p_networkCode);
                pstmtSelect1.setString(i++, PretupsI.P2P_MODULE);
                pstmtSelect1.setString(i++, p_msisdn);
                rs1 = pstmtSelect1.executeQuery();
                while (rs1.next()) {
                    if (PreferenceI.TYPE_AMOUNT.equals(rs1.getString("value_type")))
                        listValueVO = new ListValueVO(rs1.getString("name"), PretupsBL.getDisplayAmount(rs1.getLong("value")));
                    else
                        listValueVO = new ListValueVO(rs1.getString("name"), rs1.getString("value"));
                    preferenceMap.put(rs1.getString("preference_code"), listValueVO);
                }
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "preferenceMap:" + preferenceMap);
            Iterator iterator = preferenceMap.keySet().iterator();
            String key = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                parameterList.add(preferenceMap.get(key));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPrePaidControlParameters]", "", p_msisdn, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPrePaidControlParameters]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmtSelect);
        	OracleUtil.closeQuietly(rs1);
        	OracleUtil.closeQuietly(pstmtSelect1);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting parameterList.size()=" + parameterList.size());
        }// end of finally
        return parameterList;
    }

    /**
     * Method to update the last records of the subscriber
     * 
     * @param p_con
     * @param p_senderVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateSubscriberLastDetails(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberLastDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_subscriber :" + p_senderVO);
       
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append(" UPDATE p2p_subscribers SET status=? ");
            if (p_senderVO.isPinUpdateReqd())
                sbf.append(" , pin=?  ");
            sbf.append(" ,last_transfer_amount=?,last_transfer_on=?,last_transfer_type=?, activated_on=?, ");
            sbf.append(" last_transfer_status=?,last_transfer_id=?,last_transfer_msisdn=?, ");
            sbf.append(" consecutive_failures=?, last_success_transfer_date=?,modified_on=?,modified_by=? ");
            sbf.append(" WHERE user_id=? ");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateSubscriberCountersDetails", "select query:" + updateQuery);

            int i = 1;
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            pstmtUpdate.setString(i++, p_senderVO.getStatus());
            if (p_senderVO.isPinUpdateReqd())
                pstmtUpdate.setString(i++, p_senderVO.getPin());
            pstmtUpdate.setLong(i++, p_senderVO.getLastTransferAmount());
            if (p_senderVO.getLastTransferOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_senderVO.getLastTransferType());
            if (p_senderVO.getActivatedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);

            pstmtUpdate.setString(i++, p_senderVO.getLastTransferStatus());
            pstmtUpdate.setString(i++, p_senderVO.getLastTransferID());
            pstmtUpdate.setString(i++, p_senderVO.getLastTransferMSISDN());
            pstmtUpdate.setLong(i++, p_senderVO.getConsecutiveFailures());
            if (p_senderVO.getLastSuccessTransferDate() != null)
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getLastSuccessTransferDate()));
            else
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_senderVO.getUserID());
            updateCount = pstmtUpdate.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberLastDetails]", "", p_senderVO.getMsisdn(), "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberLastDetails]", "", p_senderVO.getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug("updateSubscriberCountersDetails", "Exiting updateCount:" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * Method updateLanguageAndCountry
     * This method will update the language and country in p2p subscriber table
     * according to msisdn
     * 
     * @param p_con
     *            Connection
     * @param p_lang
     *            String
     * @param p_country
     *            String
     * @param p_msisdn
     *            String
     * @return the number of records updated
     * @throws BTSLBaseException
     */

    public int updateLanguageAndCountry(Connection p_con, String p_lang, String p_country, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateLanguageAndCountry ", " Entered p_usrMsisdn:" + p_msisdn);

        String qry = "UPDATE p2p_subscribers SET language =?,country=? WHERE msisdn=?";
        int updCount = -1;
        final String methodName = "updateLanguageAndCountry";
        if (_log.isDebugEnabled())
            _log.debug(" updateLanguageAndCountry ", " Query :: " + qry);
        try( PreparedStatement pstmt = p_con.prepareStatement(qry);) {
            pstmt.setString(1, p_lang);
            pstmt.setString(2, p_country);
            pstmt.setString(3, p_msisdn);
            // Execute Query
            updCount = pstmt.executeUpdate();
        }// end of try

        catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateLanguageAndCountry]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateLanguageAndCountry]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug("updateLanguageAndCountry ", " Exiting updCount ==" + updCount);
        }

        return updCount;
    }

    /**
     * 
     * @param p_con
     * @param p_senderVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateSubscriberBuddySequenceNum(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberBuddySequenceNum";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_subscriber :" + p_senderVO);
        
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_subscribers set buddy_seq_number = ?,modified_on = ? , ");
            sbf.append(" modified_by = ? WHERE msisdn=? AND subscriber_type = ?");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + updateQuery);

            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            int i = 1;
            pstmtUpdate.setInt(i++, p_senderVO.getBuddySeqNumber());

            if (p_senderVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());

            pstmtUpdate.setString(i++, p_senderVO.getMsisdn());
            pstmtUpdate.setString(i++, p_senderVO.getSubscriberType());

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        }
        }// end of try
        catch (BTSLBaseException e) {
            throw e;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberBuddySequenceNum]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberBuddySequenceNum]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally

        return updateCount;
    }

    /**
     * To check the existance of the language code
     * 
     * @author sandeep.goel
     * @param p_con
     * @param p_languageCode
     * @return
     * @throws BTSLBaseException
     *             ChangeLocaleVO
     */
    public ChangeLocaleVO loadLanguageDetails(Connection p_con, String p_languageCode) throws BTSLBaseException {

        final String methodName = "loadLanguageDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_languageCode:" + p_languageCode);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChangeLocaleVO changeLocaleVO = null;
        try {
            // ChangeID=LOCALEMASTER
            // Query is changed so that language of type SMS or BOTH are loaded
            String selectQuery = "SELECT language, country, name FROM  locale_master WHERE  language_code=? AND status!='N' AND (type=? OR type=?) ";
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_languageCode);
            pstmtSelect.setString(2, PretupsI.SMS_LOCALE);
            pstmtSelect.setString(3, PretupsI.BOTH_LOCALE);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                changeLocaleVO = new ChangeLocaleVO();
                changeLocaleVO.setCountry(rs.getString("country"));
                changeLocaleVO.setLanguageCode(rs.getString("language"));
                changeLocaleVO.setLanguageName(rs.getString("name"));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isLanguageCodeExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isLanguageCodeExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmtSelect);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting  ChangeLocaleVO " + changeLocaleVO);
        }// end of finally
        return changeLocaleVO;
    }

    /**
     * 
     * @return
     * @throws BTSLBaseException
     */
    public HashMap loadRegisterationControlCache() throws BTSLBaseException {

        final String methodName = "loadRegisterationControlCache()";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap registerationControlMap = new HashMap();

        StringBuilder strBuff = new StringBuilder("SELECT network_code , registration_type, validation_required, ");
        strBuff.append(" validation_interface, alternate_interface_check, alternate_interface, ");
        strBuff.append(" registration_to_be_done, default_registration_type,created_by , modified_by , created_on , modified_on   ");
        strBuff.append(" FROM registration_control ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadRegisterationControlCache", "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            RegistrationControlVO registrationControlVO = null;
            while (rs.next()) {
                registrationControlVO = new RegistrationControlVO();

                registrationControlVO.setNetworkCode(rs.getString("network_code"));
                registrationControlVO.setRegistrationType(rs.getString("registration_type"));
                registrationControlVO.setValidationReqd(rs.getString("validation_required"));
                registrationControlVO.setValidationInterface(rs.getString("validation_interface"));
                registrationControlVO.setAltInterfaceCheck(rs.getString("alternate_interface_check"));
                registrationControlVO.setAlternateInterface(rs.getString("alternate_interface"));
                registrationControlVO.setRegistrationToBedone(rs.getString("registration_to_be_done"));
                registrationControlVO.setDefRegistrationType(rs.getString("default_registration_type"));
                registrationControlVO.setCreatedBy(rs.getString("created_by"));
                registrationControlVO.setModifiedBy(rs.getString("modified_by"));
                registrationControlVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getDate("created_on")));
                registrationControlVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getDate("modified_on")));

                if (PretupsI.YES.equals(registrationControlVO.getValidationReqd())) {
                    registrationControlVO.setValidationReqdBool(true);
                }

                if (PretupsI.YES.equals(registrationControlVO.getAltInterfaceCheck())) {
                    registrationControlVO.setAltInterfaceCheckBool(true);
                }

                if (PretupsI.YES.equals(registrationControlVO.getRegistrationToBedone())) {
                    registrationControlVO.setRegistrationToBedoneBool(true);
                }

                registerationControlMap.put(registrationControlVO.getNetworkCode() + "_" + registrationControlVO.getRegistrationType(), registrationControlVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadRegisterationControlCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadRegisterationControlCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
        	OracleUtil.closeQuietly(con);
        	if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: registerationControlMap size=" + registerationControlMap.size());
            }
        }
        return registerationControlMap;
    }

    /**
     * To delete the subscriber in bulk
     * 
     * @author Amit Singh
     * @param p_con
     * @param p_msisdnList
     * @return
     */
    public String deleteSubscriberBulk(Connection p_con, ArrayList p_msisdnList) throws BTSLBaseException {
        final String methodName = "deleteSubscriberBulk";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_msisdnList size : " + p_msisdnList.size());
        PreparedStatement psmtSelect = null;
        ResultSet rsSelect = null;
        PreparedStatement psmtDelete = null;
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtSelectBuddy = null;
        ResultSet rsSelectBuddy = null;
        PreparedStatement psmtDeleteBuddy = null;
        PreparedStatement psmtInsertBuddy = null;
        int updateCount = 0;
        StringBuilder invalidMsisdn = new StringBuilder();
        try {
            // select information according to the exist MSISDN in the list
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT user_id, msisdn, subscriber_type, user_name, status, network_code, pin, ");
            selectQuery.append("pin_block_count, last_transfer_amount, last_transfer_on, last_transfer_type, ");
            selectQuery.append("last_transfer_status, buddy_seq_number, total_transfers, total_transfer_amount, ");
            selectQuery.append("request_status, billing_type, billing_cycle_date, credit_limit, activated_on, ");
            selectQuery.append("registered_on, created_on, created_by, modified_on, modified_by, last_transfer_id, ");
            selectQuery.append("consecutive_failures, last_transfer_msisdn, skey_required, daily_transfer_count, ");
            selectQuery.append("monthly_transfer_count, weekly_transfer_count, prev_daily_transfer_count, ");
            selectQuery.append("prev_weekly_transfer_count, prev_monthly_transfer_count, service_class_code, ");
            selectQuery.append("daily_transfer_amount, weekly_transfer_amount, monthly_transfer_amount, ");
            selectQuery.append("prev_daily_transfer_amount, prev_weekly_transfer_amount, prev_monthly_transfer_amount, ");
            selectQuery.append("prev_transfer_date, prev_transfer_week_date, prev_transfer_month_date, ");
            selectQuery.append("last_success_transfer_date, prefix_id, pin_modified_on, first_invalid_pin_time, ");
            selectQuery.append("language, country, service_class_id ");
            selectQuery.append("FROM p2p_subscribers WHERE msisdn=? ");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "selectQuery = " + selectQuery);
            psmtSelect = p_con.prepareStatement(selectQuery.toString());

            // insert data into p2p_subscribers_history before delete
            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO p2p_subscribers_history(user_id, msisdn, subscriber_type, user_name, ");
            insertQuery.append("status, network_code, pin, pin_block_count, last_transfer_amount, last_transfer_on, ");
            insertQuery.append("last_transfer_type, last_transfer_status, buddy_seq_number, total_transfers, ");
            insertQuery.append("total_transfer_amount, request_status, billing_type, billing_cycle_date, ");
            insertQuery.append("credit_limit, activated_on, registered_on, created_on, created_by, modified_on, ");
            insertQuery.append("modified_by, last_transfer_id, consecutive_failures, last_transfer_msisdn, ");
            insertQuery.append("skey_required, daily_transfer_count, monthly_transfer_count, weekly_transfer_count, ");
            insertQuery.append("prev_daily_transfer_count, prev_weekly_transfer_count, prev_monthly_transfer_count, ");
            insertQuery.append("service_class_code, daily_transfer_amount, weekly_transfer_amount, ");
            insertQuery.append("monthly_transfer_amount, prev_daily_transfer_amount, prev_weekly_transfer_amount, ");
            insertQuery.append("prev_monthly_transfer_amount, prev_transfer_date, prev_transfer_week_date, ");
            insertQuery.append(" prev_transfer_month_date, last_success_transfer_date, prefix_id, remarks)");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "insertQuery = " + insertQuery);
            psmtInsert = p_con.prepareStatement(insertQuery.toString());

            // delete data from p2p_subscribers on the basis of MSISDN
            StringBuilder deleteQuery = new StringBuilder();
            deleteQuery.append("DELETE FROM p2p_subscribers WHERE user_id = ? ");
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriber", "deleteQuery = " + deleteQuery);
            psmtDelete = p_con.prepareStatement(deleteQuery.toString());

            // select information of buddies for the MSISDN
            StringBuilder selectBuddyQuery = new StringBuilder();
            selectBuddyQuery.append("SELECT buddy_msisdn,parent_id, buddy_seq_num,buddy_name, status,  ");
            selectBuddyQuery.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type,");
            selectBuddyQuery.append("buddy_total_transfer,buddy_total_transfer_amt,created_on, created_by,modified_on, ");
            selectBuddyQuery.append("modified_by,preferred_amount, last_transfer_amount, prefix_id ");
            selectBuddyQuery.append("FROM p2p_buddies ");
            selectBuddyQuery.append("WHERE parent_id=? ");
            selectBuddyQuery.append("ORDER BY buddy_seq_num ");
            if (_log.isDebugEnabled())
                _log.debug("loadBuddyDetails", "selectBuddyQuery = " + selectBuddyQuery);
            psmtSelectBuddy = p_con.prepareStatement(selectBuddyQuery.toString());

            // delete buddies
            StringBuilder deleteBuddyQuery = new StringBuilder();
            deleteBuddyQuery.append(" DELETE FROM p2p_buddies  ");
            deleteBuddyQuery.append(" WHERE parent_id=? ");
            if (_log.isDebugEnabled())
                _log.debug("deleteBuddy", "deleteBuddyQuery = " + deleteBuddyQuery);
            psmtDeleteBuddy = p_con.prepareStatement(deleteBuddyQuery.toString());

            // insert data into p2p_buddies_history before delete
            StringBuilder insertBuddyQuery = new StringBuilder();
            insertBuddyQuery.append(" INSERT INTO p2p_buddies_history ( buddy_msisdn, parent_id, buddy_seq_num, buddy_name, status, buddy_last_transfer_id, ");
            insertBuddyQuery.append(" buddy_last_transfer_on, buddy_last_transfer_type, buddy_total_transfer,  ");
            insertBuddyQuery.append(" buddy_total_transfer_amt, created_on, created_by, modified_on, modified_by,  ");
            insertBuddyQuery.append(" preferred_amount, last_transfer_amount, prefix_id ) ");
            insertBuddyQuery.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            if (_log.isDebugEnabled())
                _log.debug("addBuddyHistory", "insertBuddyQuery = " + insertBuddyQuery);
            psmtInsertBuddy = p_con.prepareStatement(insertBuddyQuery.toString());
            ListValueVO listValueVO = null;
            String userID;
            boolean isBuddyExist = false;
            String buddyMsisdn;
            String lastTransferStatus = null;
            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                listValueVO = (ListValueVO) p_msisdnList.get(i);
                // select information according to the exist MSISDN in the list
                psmtSelect.setString(1, listValueVO.getValue());
                rsSelect = psmtSelect.executeQuery();
                psmtSelect.clearParameters();
                lastTransferStatus = null;
                // insert data in the subscriber_history for the MSISDN
                if (rsSelect.next()) {
                    userID = rsSelect.getString("user_id");
                    lastTransferStatus = rsSelect.getString("last_transfer_status");
                    if (!BTSLUtil.isNullString(lastTransferStatus) && PretupsI.TXN_STATUS_UNDER_PROCESS.equals(lastTransferStatus)) {
                        if (_log.isDebugEnabled())
                            _log.debug(methodName, "Transaction is under process " + listValueVO.getValue());
                        UnregisterSubscribersFileProcessLog.log("Transaction is under process", "", listValueVO.getValue(), i, "Transaction is under process", "Fail", "");
                        invalidMsisdn.append(listValueVO.getValue());
                        invalidMsisdn.append(",");
                        psmtInsert.clearParameters();
                        continue;
                    }
                    psmtInsert.setString(1, userID);
                    psmtInsert.setString(2, rsSelect.getString("msisdn"));
                    psmtInsert.setString(3, rsSelect.getString("subscriber_type"));
                    psmtInsert.setString(4, rsSelect.getString("user_name"));
                    psmtInsert.setString(5, rsSelect.getString("status"));
                    psmtInsert.setString(6, rsSelect.getString("network_code"));
                    psmtInsert.setString(7, rsSelect.getString("pin"));
                    psmtInsert.setInt(8, rsSelect.getInt("pin_block_count"));
                    psmtInsert.setLong(9, rsSelect.getLong("last_transfer_amount"));
                    psmtInsert.setDate(10, rsSelect.getDate("last_transfer_on"));
                    psmtInsert.setString(11, rsSelect.getString("last_transfer_type"));
                    psmtInsert.setString(12, lastTransferStatus);
                    psmtInsert.setInt(13, rsSelect.getInt("buddy_seq_number"));
                    psmtInsert.setLong(14, rsSelect.getLong("total_transfers"));
                    psmtInsert.setLong(15, rsSelect.getLong("total_transfer_amount"));
                    psmtInsert.setString(16, rsSelect.getString("request_status"));
                    psmtInsert.setString(17, rsSelect.getString("billing_type"));
                    psmtInsert.setDate(18, rsSelect.getDate("billing_cycle_date"));
                    psmtInsert.setLong(19, rsSelect.getLong("credit_limit"));
                    psmtInsert.setDate(20, rsSelect.getDate("activated_on"));
                    psmtInsert.setDate(21, rsSelect.getDate("registered_on"));
                    psmtInsert.setDate(22, rsSelect.getDate("created_on"));
                    psmtInsert.setString(23, rsSelect.getString("created_by"));
                    psmtInsert.setDate(24, rsSelect.getDate("modified_on"));
                    psmtInsert.setString(25, rsSelect.getString("modified_by"));
                    psmtInsert.setString(26, rsSelect.getString("last_transfer_id"));
                    psmtInsert.setLong(27, rsSelect.getLong("consecutive_failures"));
                    psmtInsert.setString(28, rsSelect.getString("last_transfer_msisdn"));
                    psmtInsert.setString(29, rsSelect.getString("skey_required"));
                    psmtInsert.setLong(30, rsSelect.getLong("daily_transfer_count"));
                    psmtInsert.setLong(31, rsSelect.getLong("monthly_transfer_count"));
                    psmtInsert.setLong(32, rsSelect.getLong("weekly_transfer_count"));
                    psmtInsert.setLong(33, rsSelect.getLong("prev_daily_transfer_count"));
                    psmtInsert.setLong(34, rsSelect.getLong("prev_weekly_transfer_count"));
                    psmtInsert.setLong(35, rsSelect.getLong("prev_monthly_transfer_count"));
                    psmtInsert.setString(36, rsSelect.getString("service_class_code"));
                    psmtInsert.setLong(37, rsSelect.getLong("daily_transfer_amount"));
                    psmtInsert.setLong(38, rsSelect.getLong("weekly_transfer_amount"));
                    psmtInsert.setLong(39, rsSelect.getLong("monthly_transfer_amount"));
                    psmtInsert.setLong(40, rsSelect.getLong("prev_daily_transfer_amount"));
                    psmtInsert.setLong(41, rsSelect.getLong("prev_weekly_transfer_amount"));
                    psmtInsert.setLong(42, rsSelect.getLong("prev_monthly_transfer_amount"));
                    psmtInsert.setDate(43, rsSelect.getDate("prev_transfer_date"));
                    psmtInsert.setDate(44, rsSelect.getDate("prev_transfer_week_date"));
                    psmtInsert.setDate(45, rsSelect.getDate("prev_transfer_month_date"));
                    psmtInsert.setDate(46, rsSelect.getDate("last_success_transfer_date"));
                    psmtInsert.setInt(47, rsSelect.getInt("prefix_id"));
                    psmtInsert.setString(48, "Subscriber is Deregistered in Bulk Operation");

                    updateCount = psmtInsert.executeUpdate();
                    if (updateCount <= 0) {
                        if (_log.isDebugEnabled())
                            _log.debug(methodName, "Data Can not move to history " + listValueVO.getValue());
                        UnregisterSubscribersFileProcessLog.log("MOVE TO HISTORY", "", listValueVO.getValue(), i, "Data Can not move to history", "Fail", "");
                        invalidMsisdn.append(listValueVO.getValue());
                        invalidMsisdn.append(",");
                        psmtInsert.clearParameters();
                        p_con.rollback();
                        continue;
                    }
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Mobile number moved to history" + listValueVO.getValue());
                    UnregisterSubscribersFileProcessLog.log("MSISDN MOVE TO HISTORY", "", listValueVO.getValue(), i, "Mobile number moved to history", "PASS", "User id = " + userID);
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Mobile number does not exist " + listValueVO.getValue());
                    UnregisterSubscribersFileProcessLog.log("CHECK EXISTANCE OF MSISDN", "", listValueVO.getValue(), i, "No data found.", "Fail", "");
                    invalidMsisdn.append(listValueVO.getValue());
                    invalidMsisdn.append(",");
                    continue;
                }
                psmtInsert.clearParameters();
                // load the informations of buddies for the MSISDN
                psmtSelectBuddy.setString(1, userID);
                rsSelectBuddy = psmtSelectBuddy.executeQuery();
                psmtSelectBuddy.clearParameters();
                isBuddyExist = false;
                // insert data in the buddy_history for the userID
                while (rsSelectBuddy.next()) {
                    isBuddyExist = true;
                    buddyMsisdn = rsSelectBuddy.getString("buddy_msisdn");
                    psmtInsertBuddy.setString(1, buddyMsisdn);
                    psmtInsertBuddy.setString(2, rsSelectBuddy.getString("parent_id"));
                    psmtInsertBuddy.setInt(3, rsSelectBuddy.getInt("buddy_seq_num"));
                    psmtInsertBuddy.setString(4, rsSelectBuddy.getString("buddy_name"));
                    psmtInsertBuddy.setString(5, rsSelectBuddy.getString("status"));
                    psmtInsertBuddy.setString(6, rsSelectBuddy.getString("buddy_last_transfer_id"));
                    psmtInsertBuddy.setDate(7, rsSelectBuddy.getDate("buddy_last_transfer_on"));
                    psmtInsertBuddy.setString(8, rsSelectBuddy.getString("buddy_last_transfer_type"));
                    psmtInsertBuddy.setInt(9, rsSelectBuddy.getInt("buddy_total_transfer"));
                    psmtInsertBuddy.setInt(10, rsSelectBuddy.getInt("buddy_total_transfer_amt"));
                    psmtInsertBuddy.setDate(11, rsSelectBuddy.getDate("created_on"));
                    psmtInsertBuddy.setString(12, rsSelectBuddy.getString("created_by"));
                    psmtInsertBuddy.setDate(13, rsSelectBuddy.getDate("modified_on"));
                    psmtInsertBuddy.setString(14, rsSelectBuddy.getString("modified_by"));
                    psmtInsertBuddy.setInt(15, rsSelectBuddy.getInt("preferred_amount"));
                    psmtInsertBuddy.setInt(16, rsSelectBuddy.getInt("last_transfer_amount"));
                    psmtInsertBuddy.setInt(17, rsSelectBuddy.getInt("prefix_id"));
                    /*
                     * updateCount=psmtInsertBuddy.executeUpdate();
                     * if (updateCount <= 0)
                     * break;
                     */
                    psmtInsertBuddy.clearParameters();
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Buddy moved to history " + buddyMsisdn);
                    UnregisterSubscribersFileProcessLog.log("BUDDY MOVE TO HISTORY", "", buddyMsisdn, i, "Buddy move to history", "PASS", "User id = " + userID);
                }
                if (updateCount <= 0) {
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Buddy can not move to history" + listValueVO.getValue());
                    UnregisterSubscribersFileProcessLog.log("BUDDY CAN NOT MOVE TO HISTORY", "", listValueVO.getValue(), i, "Buddy can not move to history.", "Fail", "");
                    invalidMsisdn.append(listValueVO.getValue());
                    invalidMsisdn.append(",");
                    psmtInsertBuddy.clearParameters();
                    p_con.rollback();
                    continue;
                }
                // delete the suibscriber's buddies from the table
                psmtDeleteBuddy.setString(1, userID);
                updateCount = psmtDeleteBuddy.executeUpdate();
                if (updateCount <= 0 && isBuddyExist) {
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Buddy can not be deleted" + listValueVO.getValue());
                    UnregisterSubscribersFileProcessLog.log("BUDDY CAN NOT BE DELETED", "", listValueVO.getValue(), i, "Buddy can not be deleted.", "Fail", "");
                    invalidMsisdn.append(listValueVO.getValue());
                    invalidMsisdn.append(",");
                    psmtDeleteBuddy.clearParameters();
                    p_con.rollback();
                    continue;
                }
                // added by harsh to delete schedule batch for un-register
                // subscriber
                this.deleteScheduleBatch(p_con, userID);
                // end added by
                psmtDeleteBuddy.clearParameters();
                if (_log.isDebugEnabled())
                    _log.debug(methodName, "Buddy deleted of UserID = " + userID + " Msisdn = " + listValueVO.getValue());
                UnregisterSubscribersFileProcessLog.log("BUDDY DELETED OF MSISDN", "", listValueVO.getValue(), i, "Buddy deleted of userID = " + userID, "PASS", "User id = " + userID);

                // delete the suibscriber from the table
                psmtDelete.setString(1, userID);
                updateCount = psmtDelete.executeUpdate();
                if (updateCount <= 0) {
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Data for this MSISDN can not be deleted" + listValueVO.getValue());
                    UnregisterSubscribersFileProcessLog.log("DATA CAN NOT BE DELETED", "", listValueVO.getValue(), i, "Data for this MSISDN can not be deleted.", "Fail", "");
                    invalidMsisdn.append(listValueVO.getValue());
                    invalidMsisdn.append(",");
                    psmtDelete.clearParameters();
                    p_con.rollback();
                    continue;
                }
                psmtDelete.clearParameters();

                p_con.commit();
                if (_log.isDebugEnabled())
                    _log.debug(methodName, "MSISDN DELETED OF = " + userID + " Msisdn = " + listValueVO.getValue());
                UnregisterSubscribersFileProcessLog.log("MSISDN DELETED", "", listValueVO.getValue(), i, "MSISDN deleted of userID = " + userID, "PASS", "User id = " + userID);

            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriberBulk]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriberBulk]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(psmtDeleteBuddy);
        	OracleUtil.closeQuietly(psmtDelete);
        	OracleUtil.closeQuietly(psmtInsert);
        	OracleUtil.closeQuietly(psmtInsertBuddy);
        	OracleUtil.closeQuietly(rsSelect);
        	OracleUtil.closeQuietly(psmtSelect);
        	OracleUtil.closeQuietly(rsSelectBuddy);
        	OracleUtil.closeQuietly(psmtSelectBuddy);
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:invalidMsisdn=" + invalidMsisdn);
        }// end of finally
        return invalidMsisdn.toString();
    }

    /**
     * Update network prefix ID of the registered P2P subscriber in
     * P2PSUBSCRIBER table
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_prefixID
     * @throws BTSLBaseException
     * @author sanjeew.kumar
     * @Date 25/01/08
     */
    public int updateSubscriberPrefixID(Connection p_con, SenderVO p_senderVO, long p_prefixID) throws BTSLBaseException {
        int updateCount = 0;
        if (_log.isDebugEnabled())
            _log.debug("SubscriberDAO[updateSubscriberPrefixID]", "Entered with Msisdn=" + p_senderVO.getMsisdn() + " p_prefixID=" + p_prefixID);
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
       
        final String methodName = "updateSubscriberPrefixID";
        try {
            StringBuilder sbf = new StringBuilder();
            int i = 1;

            sbf.append("UPDATE p2p_subscribers SET user_name=?, prefix_id=? ");
            sbf.append("WHERE msisdn=? AND subscriber_type =?");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("SubscriberDAO[updateSubscriberPrefixID]", "select query:" + updateQuery);

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(updateQuery);
            try(PreparedStatement pstmtUpdate =  p_con.prepareStatement(updateQuery);)
            {
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
            pstmtUpdate.setLong(i++, p_prefixID);
            pstmtUpdate.setString(i++, p_senderVO.getMsisdn());
            pstmtUpdate.setString(i++, p_senderVO.getSubscriberType());
            updateCount = pstmtUpdate.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, "SubscriberDAO[updateSubscriberPrefixID]", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, "SubscriberDAO[updateSubscriberPrefixID]", "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug("SubscriberDAO[updateSubscriberPrefixID]", "Exiting updateCount:" + updateCount);
        }// end of finally

        return updateCount;
    }

    public BuddyVO loadBuddyAmount(Connection p_con, String Parent_id, String MSISDN_Name, String Req_msisdn) throws BTSLBaseException {
        final String methodName = "loadBuddyAmount";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered MSISDN_Name:" + MSISDN_Name + "Parent_id:" + Parent_id);
        BuddyVO buddyVO = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("SELECT BUDDY_MSISDN,preferred_amount FROM P2P_BUDDIES ");
            selectQueryBuff.append("  WHERE parent_id=(SELECT user_id FROM P2P_SUBSCRIBERS WHERE msisdn=(SELECT BUDDY_MSISDN FROM P2P_BUDDIES ");
            selectQueryBuff.append("  WHERE status= ? AND PARENT_ID= ? AND (buddy_name=?  OR  buddy_msisdn=? ))) AND buddy_msisdn=? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(2, Parent_id);
            pstmtSelect.setString(3, MSISDN_Name);
            pstmtSelect.setString(4, MSISDN_Name);
            pstmtSelect.setString(5, Req_msisdn);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            int multiplicationFactor = SystemPreferences.AMOUNT_MULT_FACTOR;
            if (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setBuddyMsisdn(rs.getString("BUDDY_MSISDN"));
                buddyVO.setPreferredAmount(rs.getLong("preferred_amount") / multiplicationFactor);
            }
            return buddyVO;
        }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyAmount]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyAmount]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting preferredAmt:" + buddyVO);
        }// end of finally
    }

    public BuddyVO loadReciverMSISDN(Connection p_con, String Parent_id, String MSISDN_Name) throws BTSLBaseException {
        final String methodName = "loadReciverMSISDN";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered MSISDN_Name:" + MSISDN_Name + "Parent_id:" + Parent_id);

        BuddyVO buddyVO = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("SELECT BUDDY_MSISDN FROM P2P_BUDDIES ");
            selectQueryBuff.append("  WHERE status= ? AND PARENT_ID= ? AND (buddy_name=?  OR  buddy_msisdn=? )");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(2, Parent_id);
            pstmtSelect.setString(3, MSISDN_Name);
            pstmtSelect.setString(4, MSISDN_Name);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("BUDDY_MSISDN"));
            }
            return buddyVO;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadReciverMSISDN]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadReciverMSISDN]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting RECIEVER MSISDN:" + buddyVO);
        }// end of finally
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
    public int updateSubscriberDetailsByMSISDN(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberDetails", "Entered p_subscriber :" + p_senderVO);
        String method = "updateSubscriberDetails";
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        
        int updateCount = 0;
        final String methodName = "updateSubscriberDetailsByMSISDN";
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_subscribers SET activated_on=?, last_transfer_on=?,total_transfers=?,total_transfer_amount=?, ");
            sbf.append(" status = ?,user_name=?,billing_type=?, ");
            sbf.append(" billing_cycle_date=?,credit_limit=? ,modified_on = ? , ");
            sbf.append(" modified_by = ?,prefix_id=? ,subscriber_type = ? ,service_class_id=?,service_class_code=? WHERE msisdn=? ");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + updateQuery);

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(updateQuery);
           try(PreparedStatement pstmtUpdate =  p_con.prepareStatement(updateQuery);)
           {
            int i = 1;
            if (p_senderVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);

            if (p_senderVO.getLastTransferOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setLong(i++, p_senderVO.getTotalTransfers());
            pstmtUpdate.setLong(i++, p_senderVO.getTotalTransferAmount());
            pstmtUpdate.setString(i++, p_senderVO.getStatus());

            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_senderVO.getUserName());

            pstmtUpdate.setString(i++, p_senderVO.getBillingType());
            if (p_senderVO.getBillingCycleDate() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getBillingCycleDate()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setLong(i++, p_senderVO.getMonthlyTransferAmount());
            if (p_senderVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
            pstmtUpdate.setLong(i++, p_senderVO.getPrefixID());
            pstmtUpdate.setString(i++, p_senderVO.getSubscriberType());
            pstmtUpdate.setString(i++, p_senderVO.getServiceClassID());
            pstmtUpdate.setString(i++, p_senderVO.getServiceClassCode());
            pstmtUpdate.setString(i++, p_senderVO.getMsisdn());

            boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, method, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }

        }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberDetailsByMSISDN]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberDetailsByMSISDN]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug("updateSubscriberDetails", "Exiting updateCount:" + updateCount);
        }// end of finally

        return updateCount;
    }

    /**
     * It loads details of list and data in the list on the basis of schedule
     * type i.e. M/W/D in
     * scheduled multiple credit transfer
     * 
     * @param p_con
     * @param p_scheduleType
     * @return
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public ArrayList<Object> loadBuddyDetailsByScheduleType(Connection p_con, String p_scheduleType) throws BTSLBaseException {
        final String methodName = "loadBuddyDetailsByScheduleType";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_scheduleType:" + p_scheduleType);

        PreparedStatement pstmtSelect = null;
        BuddyVO buddyVO = null;
        P2PBatchesVO batchBuddyVO = null;
        ResultSet rs = null;
        ArrayList<Object> listBuddyBatchListDetails = new ArrayList<Object>();
        ArrayList<Object> listBuddyListDetails = new ArrayList<>();
        boolean checkData = false;
        try {
            StringBuilder selectQueryBuff = new StringBuilder();

            selectQueryBuff.append("SELECT batch.batch_id, batch.parent_id, batch.list_name, batch.status batch_status,");
            selectQueryBuff.append("batch.schedule_type, batch.no_of_schedule,");
            selectQueryBuff.append("batch.sender_service_class, batch.batch_total_record,");
            selectQueryBuff.append("batch.created_on, batch.modified_on, batch.execution_count,");
            selectQueryBuff.append("batch.created_by, batch.modified_by,");
            // added by harsh for scheduling credit transfer list
            selectQueryBuff.append("batch.schedule_date ,");

            selectQueryBuff.append("p2psub.msisdn, p2psub.LANGUAGE,p2psub.pin,");
            selectQueryBuff.append(" p2psub.country,p2psub.network_code,pb.buddy_msisdn, pb.status buddy_status, pb.preferred_amount,");
            selectQueryBuff.append(" pb.successive_failure_count, pb.selector_code,");
            // entries fetch for writing into relevant logs by harsh
            selectQueryBuff.append(" pb.BUDDY_LAST_TRANSFER_ID, pb.BUDDY_LAST_TRANSFER_ON,");
            selectQueryBuff.append(" pb.BUDDY_LAST_TRANSFER_TYPE, pb.LAST_TRANSFER_AMOUNT");
            // end added by harsh
            selectQueryBuff.append(" FROM p2p_buddies pb, p2p_batches batch, p2p_subscribers p2psub");
            // added by harsh for scheduling credit transfer list
            selectQueryBuff.append(" WHERE trunc(batch.schedule_date) <= trunc(sysdate)");
            selectQueryBuff.append(" AND p2psub.status in ('Y','S')");
            selectQueryBuff.append(" AND batch.schedule_type = ?");
            selectQueryBuff.append(" AND batch.status = 'Y'");
            selectQueryBuff.append(" AND batch.parent_id = p2psub.user_id");
            selectQueryBuff.append(" AND pb.parent_id = batch.parent_id");
            selectQueryBuff.append(" AND pb.list_name = batch.list_name");
            selectQueryBuff.append(" AND batch.execution_count < batch.no_of_schedule");
            selectQueryBuff.append(" ORDER BY batch.batch_id");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_scheduleType);
            rs = pstmtSelect.executeQuery();

            String batchIDPrevious = null;
            String BatchIDCurrent = null;
            batchBuddyVO = new P2PBatchesVO();
            while (rs.next()) {
                checkData = true;
                BatchIDCurrent = rs.getString("batch_id");
                if (rs.isFirst()) {
                    batchIDPrevious = rs.getString("batch_id");
                }
                if (BatchIDCurrent.equals(batchIDPrevious)) {
                    batchBuddyVO.setBatchID(rs.getString("batch_id"));
                    batchBuddyVO.setParentID(rs.getString("parent_id"));
                    batchBuddyVO.setListName(rs.getString("list_name"));
                    batchBuddyVO.setStatus(rs.getString("batch_status"));
                    batchBuddyVO.setScheduleType(rs.getString("schedule_type"));
                    batchBuddyVO.setNoOfSchedule(rs.getLong("no_of_schedule"));
                    batchBuddyVO.setBatchTotalRecords(rs.getLong("batch_total_record"));
                    batchBuddyVO.setExecutionCount(rs.getLong("execution_count"));
                    if (rs.getTimestamp("created_on") != null)
                        batchBuddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                    batchBuddyVO.setCreatedBy(rs.getString("created_by"));
                    if (rs.getTimestamp("modified_on") != null)
                        batchBuddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                    batchBuddyVO.setModifiedBy(rs.getString("modified_by"));
                    batchBuddyVO.setSenderMSISDN(rs.getString("msisdn"));
                    batchBuddyVO.setSenderLocale(rs.getString("language"));
                    batchBuddyVO.setSenderPin(rs.getString("pin"));
                    batchBuddyVO.setSenderCountry(rs.getString("country"));
                    batchBuddyVO.setNetworkCode(rs.getString("network_code"));
                    batchBuddyVO.setScheduleDate(rs.getDate("schedule_date"));

                    buddyVO = new BuddyVO();
                    buddyVO.setBuddyMsisdn(rs.getString("buddy_msisdn"));
                    buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                    buddyVO.setSuccessiveFailCount(rs.getLong("successive_failure_count"));
                    buddyVO.setSelectorCode(rs.getString("selector_code"));
                    // entries fetch for writing into relevant logs by harsh
                    buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                    buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                    buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));

                    // added by harsh for handling consecutive failure case
                    if (rs.getTimestamp("modified_on") != null)
                        buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                    buddyVO.setModifiedBy(rs.getString("modified_by"));
                    buddyVO.setListName(rs.getString("list_name"));
                    buddyVO.setOwnerUser(rs.getString("parent_id"));

                    listBuddyListDetails.add(buddyVO);
                }

                if (!(BatchIDCurrent.equals(batchIDPrevious))) {
                    batchIDPrevious = BatchIDCurrent;
                    batchBuddyVO.setBuddyList(listBuddyListDetails);
                    listBuddyBatchListDetails.add(batchBuddyVO);

                    batchBuddyVO = new P2PBatchesVO();
                    listBuddyListDetails = new ArrayList<Object>();
                    batchBuddyVO.setBatchID(rs.getString("batch_id"));
                    batchBuddyVO.setParentID(rs.getString("parent_id"));
                    batchBuddyVO.setListName(rs.getString("list_name"));
                    batchBuddyVO.setStatus(rs.getString("buddy_status"));
                    batchBuddyVO.setScheduleType(rs.getString("schedule_type"));
                    batchBuddyVO.setNoOfSchedule(rs.getLong("no_of_schedule"));
                    batchBuddyVO.setBatchTotalRecords(rs.getLong("batch_total_record"));
                    batchBuddyVO.setExecutionCount(rs.getLong("execution_count"));
                    if (rs.getTimestamp("created_on") != null)
                        batchBuddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                    batchBuddyVO.setCreatedBy(rs.getString("created_by"));
                    if (rs.getTimestamp("modified_on") != null)
                        batchBuddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                    batchBuddyVO.setModifiedBy(rs.getString("modified_by"));
                    batchBuddyVO.setSenderMSISDN(rs.getString("msisdn"));
                    batchBuddyVO.setSenderLocale(rs.getString("language"));
                    batchBuddyVO.setSenderPin(rs.getString("pin"));
                    batchBuddyVO.setSenderCountry(rs.getString("country"));
                    batchBuddyVO.setNetworkCode(rs.getString("network_code"));
                    batchBuddyVO.setScheduleDate(rs.getDate("schedule_date"));

                    buddyVO = new BuddyVO();
                    buddyVO.setBuddyMsisdn(rs.getString("buddy_msisdn"));
                    buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                    buddyVO.setSuccessiveFailCount(rs.getLong("successive_failure_count"));
                    buddyVO.setSelectorCode(rs.getString("selector_code"));
                    listBuddyListDetails.add(buddyVO);

                }

            }
            if (checkData) {
                batchBuddyVO.setBuddyList(listBuddyListDetails);
                listBuddyBatchListDetails.add(batchBuddyVO);
            }

            return listBuddyBatchListDetails;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyDetailsByScheduleType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadBuddyDetailsByScheduleType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmtSelect);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting listBuddyBatchListDetails.size:" + listBuddyBatchListDetails.size());
        }// end of finally
    }

    /**
     * Soft delete the list if it's execution count reaches the no of schedules
     * or batch total records reaches 0.
     * 
     * @param p_con
     * @param p_buddyVO
     * @return
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public int updateBatchStatus(Connection p_con, P2PBatchesVO p_batchBuddyVO) throws BTSLBaseException {
        final String methodName = "updateBatchStatus";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_buddyVO :" + p_batchBuddyVO);
       
        int updateCount = 0;
        try {
            int i = 1;
            StringBuilder updateQueryBuff = new StringBuilder("delete from p2p_batches ");
            updateQueryBuff.append("  WHERE list_name =? AND parent_id =? AND batch_id =?");
            updateQueryBuff.append("  and batch_total_record = '0' OR execution_count = no_of_schedule  ");
            String selectUpdate = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select Update:" + selectUpdate);
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(selectUpdate);)
            {
            pstmtUpdate.setString(i++, p_batchBuddyVO.getListName());
            pstmtUpdate.setString(i++, p_batchBuddyVO.getParentID());
            pstmtUpdate.setString(i++, p_batchBuddyVO.getBatchID());

            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateBatchStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateBatchStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }// end of finally
    }

    /**
     * It updates the execution count in scheduled multiple credit transfer
     * after processing the list
     * 
     * @param p_con
     * @param p_buddyVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateExecutionCount(Connection p_con, P2PBatchesVO p_batchBuddyVO) throws BTSLBaseException {
        final String methodName = "updateExecutionCount";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_batchBuddyVO :" + p_batchBuddyVO);
       
        // added by harsh on 01 Aug 13 to add entry in executed_upto field on
        // each schedule credit transfer
        Date currentDateTime = new Date();
        Calendar cal = Calendar.getInstance();
        currentDateTime = cal.getTime(); // Current Date
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_batches SET  execution_count = execution_count + 1,modified_on =? , ");
            sbf.append(" modified_by =? , execution_upto = ?, schedule_date = ?  WHERE");
            sbf.append(" list_name =? AND parent_id =? AND batch_id =?");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "update query:" + updateQuery);

            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            int i = 1;
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_batchBuddyVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_batchBuddyVO.getModifiedBy());
            // added by harsh on 01 Aug 13 to add entry in executed_upto field
            // on each schedule credit transfer
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(currentDateTime));
            // pstmtUpdate.setDate(i++,
            // BTSLUtil.getSQLDateFromUtilDate(getNextScheduleDate(p_batchBuddyVO.getScheduleType())));
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString((getNextScheduleDate(p_batchBuddyVO.getScheduleDate(), p_batchBuddyVO.getScheduleType())), PretupsI.DATE_FORMAT_DDMMYYYY)));
            pstmtUpdate.setString(i++, p_batchBuddyVO.getListName());
            pstmtUpdate.setString(i++, p_batchBuddyVO.getParentID());
            pstmtUpdate.setString(i++, p_batchBuddyVO.getBatchID());

            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }
        }// end of try

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateExecutionCount]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateExecutionCount]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }
    }// end of finally

    /**
     * @param p_con
     * @param p_buddyVO
     * @return
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public int updateSuccessiveFailCount(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "updateSuccessiveFailCount";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_buddyMsisdn:" + p_buddyVO.getBuddyMsisdn() + "List_Name:" + p_buddyVO.getListName() + "Parent Id:" + p_buddyVO.getOwnerUser() + "Selector Code" + p_buddyVO.getSelectorCode());
       
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_buddies SET successive_failure_count = successive_failure_count + 1,modified_on = ? , ");
            sbf.append(" modified_by = ? WHERE");
            sbf.append(" buddy_msisdn =? AND list_name =? AND parent_id =? AND selector_code =?");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "update query:" + updateQuery);

           try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
           {
            int i = 1;

            if (p_buddyVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_buddyVO.getModifiedBy());

            pstmtUpdate.setString(i++, p_buddyVO.getBuddyMsisdn());
            pstmtUpdate.setString(i++, p_buddyVO.getListName());
            pstmtUpdate.setString(i++, p_buddyVO.getOwnerUser());
            pstmtUpdate.setString(i++, p_buddyVO.getSelectorCode());

            updateCount = pstmtUpdate.executeUpdate();

        }
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSuccessiveFailCount]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSuccessiveFailCount]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * This method is used to get Next Schedule Date for credit transfer after
     * saving buddy list
     * 
     * @param schType
     *            String
     * @return nextScheduleDate Date
     * @author Harsh.Dixit
     * @date 06-08-2013
     */
    public Date getNextScheduleDate(String schType) {
        Calendar calendar = Calendar.getInstance();
        Date nextScheduleDate = null;
        if (schType.equals(PretupsI.SCHEDULE_TYPE_DAILY_FILTER)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            calendar.add(Calendar.MONTH, 1);
            nextScheduleDate = calendar.getTime();
        }
        return nextScheduleDate;
    }

    /**
     * Method checkAmbiguousTransfer. This method is used to check is any
     * transaction status is ambiguous?
     * 
     * @param p_con
     * @param p_filteredMsisdn
     * @return boolean
     * @throws BTSLBaseException
     */
    public String getFinalTxnStatus(Connection p_con, String p_txnId) throws BTSLBaseException {
        final String methodName = "getFinalTxnStatus";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_txnId" + p_txnId);

        String status = null;
        String selectQuery = "SELECT error_code FROM subscriber_transfers  WHERE TRANSFER_ID=? ";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "selectquery:" + selectQuery);
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
           
            pstmtSelect.setString(1, p_txnId);
            try(ResultSet rs = pstmtSelect.executeQuery();){
            if (rs.next()) {
                status = rs.getString("error_code");
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[getFinalTxnStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[getFinalTxnStatus]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting: status:" + status);
        }// end of finally
        return status;
    }

    /**
     * @param p_con
     * @param p_buddyVO
     * @return
     * @throws BTSLBaseException
     * @author harsh.dixit
     */
    public int resetSuccessiveFailCount(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "resetSuccessiveFailCount";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_buddyMsisdn:" + p_buddyVO.getBuddyMsisdn() + "List_Name:" + p_buddyVO.getListName() + "Parent Id:" + p_buddyVO.getOwnerUser() + "Selector Code" + p_buddyVO.getSelectorCode());
        
        int updateCount = 0;
        try {
            StringBuilder sbf = new StringBuilder();

            sbf.append("UPDATE p2p_buddies SET successive_failure_count = '0' ,modified_on = ? , ");
            sbf.append(" modified_by = ? WHERE");
            sbf.append(" buddy_msisdn =? AND list_name =? AND parent_id =? AND selector_code =?");
            String updateQuery = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "update query:" + updateQuery);

            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            int i = 1;

            if (p_buddyVO.getModifiedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getModifiedOn()));
            else
                pstmtUpdate.setNull(i++, Types.DATE);
            pstmtUpdate.setString(i++, p_buddyVO.getModifiedBy());

            pstmtUpdate.setString(i++, p_buddyVO.getBuddyMsisdn());
            pstmtUpdate.setString(i++, p_buddyVO.getListName());
            pstmtUpdate.setString(i++, p_buddyVO.getOwnerUser());
            pstmtUpdate.setString(i++, p_buddyVO.getSelectorCode());

            updateCount = pstmtUpdate.executeUpdate();

        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[resetSuccessiveFailCount]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[resetSuccessiveFailCount]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * To delete all the schedule batch of the user on de-registration
     * 
     * @param p_con
     * @param p_senderVO
     *            SenderVO
     * @return int status
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     */
    public int deleteScheduleBatch(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "deleteScheduleBatch";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered Sender VO  : " + p_senderVO);
         
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder(100);
            // insert query
            strBuff.append(" DELETE FROM p2p_batches  ");
            strBuff.append(" WHERE parent_id=? ");

            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "delete query:" + query);

            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            psmt.setString(1, p_senderVO.getUserID());
            updateCount = psmt.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteScheduleBatch]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteBuddiesList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteScheduleBatch]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting update Count :" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * To delete all the schedule batch of the user on de-registration
     * 
     * @param p_con
     * @param p_userId
     *            String
     * @return int status
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     */
    public int deleteScheduleBatch(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "deleteScheduleBatch";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered User ID  : " + p_userId);
        
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder(100);
            // insert query
            strBuff.append(" DELETE FROM p2p_batches  ");
            strBuff.append(" WHERE parent_id=? ");

            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "delete query:" + query);

           try(PreparedStatement psmt = p_con.prepareStatement(query);)
           {
            psmt.setString(1, p_userId);
            updateCount = psmt.executeUpdate();
           }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteScheduleBatch]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteBuddiesList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteScheduleBatch]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting update Count :" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * This method is used to get Next Schedule Date for credit transfer after
     * saving buddy list
     * 
     * @param schType
     *            String
     * @return nextScheduleDate Date
     * @author Harsh.Dixit
     * @date 06-08-2013
     */
    public String getNextScheduleDate(Date schDate, String schType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schDate);
        Date nextScheduleDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        if (schType.equals(PretupsI.SCHEDULE_TYPE_DAILY_FILTER)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            calendar.add(Calendar.MONTH, 1);
            nextScheduleDate = calendar.getTime();
        }
        return sdf.format(nextScheduleDate);
    }

    /**
     * Method to update the check imei exist or not
     * 
     * @param p_con
     * @param p_msisdn
     * @param imei
     * @return boolean
     * @throws BTSLBaseException
     * @author vikas.singh
     */

    public boolean checkImei(Connection p_con, String p_msisdn, String p_imei, String p_userId) throws BTSLBaseException, Exception {
        boolean isExist = false;
        final String methodName = "checkImei";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_msisdn: " + p_msisdn + "p_imei: " + p_imei + "p_userId:" + p_userId);
       
        String selectQuery = "SELECT msisdn from p2p_subscribers where  msisdn= ? and imei=? and status=? and user_id=? ";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "select query:" + selectQuery);
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
           
            pstmtSelect.setString(1, p_msisdn);
            pstmtSelect.setString(2, p_imei);
            pstmtSelect.setString(3, PretupsI.YES);
            pstmtSelect.setString(4, p_userId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkImei]", "", p_msisdn, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting :isExist" + isExist);
        }
        return isExist;
    }

    /**
     * Register the subscriber in our system
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public int registerSelfTopUpSubscriber(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "registerSelfTopUpSubscriber";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered subscriberVO : " + p_senderVO);
        PreparedStatement psmt = null;
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            boolean msisdnExist = (new CP2PRegistrationDAO()).isSubscriberMobileNumberExist(p_con, p_senderVO);
            if (!msisdnExist) {
                StringBuilder strBuff = new StringBuilder();
                strBuff.append(" INSERT INTO p2p_subscribers  ");
                strBuff.append(" (user_id,msisdn, subscriber_type, prefix_id,status, network_code, pin, request_status, ");
                strBuff.append(" service_class_code,service_class_id,activated_on,registered_on,  created_on, created_by, modified_on, modified_by,language, country ");
                // if(p_senderVO.getImei().length() == 4)
                if (PretupsI.DEFAULT_P2P_WEB_IMEI.equals(p_senderVO.getImei()))
                    strBuff.append(" ,login_id,password ");
                strBuff.append(" ,imei,email_id,ENCRYPTION_KEY ) ");
                strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
                // if(p_senderVO.getImei().length()== 4)
                if (PretupsI.DEFAULT_P2P_WEB_IMEI.equals(p_senderVO.getImei()))
                    strBuff.append(",?,? ");
                strBuff.append(")");
                String query = strBuff.toString();
                if (_log.isDebugEnabled())
                    _log.debug(methodName, "insert query:" + query);

                psmt = p_con.prepareStatement(query);
                int m = 0;

                psmt.setString(++m, p_senderVO.getUserID());
                psmt.setString(++m, p_senderVO.getMsisdn());
                psmt.setString(++m, p_senderVO.getSubscriberType());
                psmt.setLong(++m, p_senderVO.getPrefixID());
                psmt.setString(++m, p_senderVO.getStatus());
                psmt.setString(++m, p_senderVO.getNetworkCode());
                psmt.setString(++m, BTSLUtil.encryptText(p_senderVO.getPin()));
                psmt.setString(++m, PretupsI.TXN_STATUS_COMPLETED);
                psmt.setString(++m, p_senderVO.getServiceClassCode());
                psmt.setString(++m, p_senderVO.getServiceClassID());
                if (p_senderVO.getActivatedOn() != null)
                    psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
                else
                    psmt.setNull(++m, Types.DATE);
                psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getRegisteredOn()));
                psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getCreatedOn()));
                psmt.setString(++m, p_senderVO.getCreatedBy());
                psmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                psmt.setString(++m, p_senderVO.getModifiedBy());
                psmt.setString(++m, p_senderVO.getLanguage());
                psmt.setString(++m, p_senderVO.getCountry());
                // if(p_senderVO.getImei().length()== 4){
                if (PretupsI.DEFAULT_P2P_WEB_IMEI.equals(p_senderVO.getImei())) {
                    psmt.setString(++m, p_senderVO.getLogin());
                    psmt.setString(++m, p_senderVO.getPassword());
                }
                psmt.setString(++m, p_senderVO.getImei());
                psmt.setString(++m, p_senderVO.getEmailId());
                psmt.setString(++m, p_senderVO.getEncryptionKey());
                updateCount = psmt.executeUpdate();
            } else {
                if (p_senderVO.getImei().length() == 15 && !PretupsI.DEFAULT_P2P_WEB_IMEI.equals(p_senderVO.getImei())) {
                    String queryUpdate = "UPDATE p2p_subscribers set modified_on= ?, imei=?, ENCRYPTION_KEY=? WHERE msisdn = ? and status = 'Y'";
                    String query = queryUpdate;
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "insert query:" + query);
                    pstmtUpdate = p_con.prepareStatement(queryUpdate);
                    pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                    pstmtUpdate.setString(2, p_senderVO.getImei());
                    pstmtUpdate.setString(3, p_senderVO.getEncryptionKey());
                    pstmtUpdate.setString(4, p_senderVO.getMsisdn());
                    updateCount = pstmtUpdate.executeUpdate();
                }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSelfTopUpSubscriber]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSelfTopUpSubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(psmt);
        	OracleUtil.closeQuietly(pstmtUpdate);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Success :" + updateCount);
        }// end of finally

        return updateCount;
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
            _log.debug(methodName, "Entered: p_modification_type=" + p_modificationType + "p_userId=" + p_userId + "p_Msisdn= " + p_Msisdn + " p_newPassword= " + p_newPassword);
        }

        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

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
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){

            
            pstmt.setString(1, p_modificationType);
            pstmt.setString(2, p_userId);
            if (p_modificationType.equalsIgnoreCase(PretupsI.USER_PASSWORD_MANAGEMENT)) {
                pstmt.setInt(3, SystemPreferences.PREV_PASS_NOT_ALLOW);
            } else {
                pstmt.setString(3, p_Msisdn);
                pstmt.setInt(4, SystemPreferences.PREV_PIN_NOT_ALLOW);
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                if (rs.getString("Pin_or_Password").equals(p_newPassword)) {
                    existFlag = true;
                    break;
                }
            }
            return existFlag;
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered: p_userId= " + p_userId + " p_modifiedBy=" + p_modifiedBy);
         
        int updateCount = 0;
        String queryUpdate = "UPDATE p2p_subscribers SET pswd_modified_on=?,password=?,modified_on= ?, modified_by= ?, PSWD_RESET=? WHERE user_id = ? ";
        try(PreparedStatement pstmtUpdate = p_con.prepareStatement(queryUpdate);) {

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[changePassword]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug("changePassword()", " Exiting with updateCount=" + updateCount);
        }
        return updateCount;
    }

    /**
     * Method to insert add auto top up subscribers
     * 
     * @param p_con
     * @param p_type
     * @param p_scheduleType
     * @param p_amount
     * @param p_scheduleDate
     *            java.sql.Date
     * @param p_userId
     * @param p_nickName
     * @return boolean
     * @throws BTSLBaseException
     * @author vikas.singh
     */
    public int addAutoTopupUserDetails(Connection p_con, String p_userId, String p_type, String p_scheduleType, Double p_amount, Date p_scheduleDate, String p_nickName, Date p_deactivationDate) throws BTSLBaseException {
        final String methodName = "addAutoTopupUserDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered: p_userId:" + p_userId + " p_type: " + p_type + "p_scheduleType:" + p_scheduleType + "p_amount:" + p_amount + " p_scheduleDate:" + BTSLUtil.getTimestampFromUtilDate(p_scheduleDate) + " p_nickName:" + p_nickName + " p_deactivationDate:" + BTSLUtil.getTimestampFromUtilDate(p_deactivationDate));
       
        int insertCount = 0;
        int i = 1;
        try {
            StringBuilder strBuff = new StringBuilder();
            // insert query
            strBuff.append("INSERT INTO SCHEDULE_TOPUP_DETAILS (USER_ID,SERVICE_TYPE,SCHEDULE_TYPE,");
            strBuff.append("AMOUNT,CREATED_ON,MODIFIED_ON,SCHEDULE_DATE,DE_ACTIVATION_DATE,NICK_NAME,STATUS) ");
            strBuff.append("VALUES (?,?,?,?,?,?,?,?,?,?)");
            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "insert query:" + query);
            try(PreparedStatement psmt = p_con.prepareStatement(query);)
            {
            psmt.setString(i++, p_userId);
            psmt.setString(i++, p_type);
            psmt.setString(i++, p_scheduleType);
            psmt.setDouble(i++, p_amount);
            psmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            psmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            psmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_scheduleDate));
            psmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_deactivationDate));
            psmt.setString(i++, p_nickName);
            psmt.setString(i++, PretupsI.YES);
            insertCount = psmt.executeUpdate();
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addAutoTopupUserDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addPostpaidControlParameters", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addAutoTopupUserDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting insertCount  :" + insertCount);
        }// end of finally

        return insertCount;
    }

    /**
     * Method to check whether the subscriber is already have card's schedule
     * top-up enabled
     * 
     * @param p_con
     * @param p_userId
     * @return boolean
     * @throws BTSLBaseException
     * @author vikas.singh
     */
    public boolean checkAlreadyEnabled(Connection p_con, String p_userID) throws BTSLBaseException {
        boolean isRegistered = false;
        final String methodName = "checkAlreadyEnabled";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_userID: " + p_userID);
       
        String selectQuery = "SELECT 1 from SCHEDULE_TOPUP_DETAILS where status=? and user_id=? ";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "select query:" + selectQuery);
        try (PreparedStatement  pstmtSelect = p_con.prepareStatement(selectQuery);){

            pstmtSelect.setString(1, PretupsI.YES);
            pstmtSelect.setString(2, p_userID);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                isRegistered = true;
            }
        }
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkAlreadyEnabled]", "", p_userID, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting :isRegistered:" + isRegistered);
        }
        return isRegistered;
    }

    /**
     * Change the user pin
     * 
     * @param p_con
     * @param p_senderVO
     *            SenderVO
     * @return int
     * @throws BTSLBaseException
     */
    public int changePinFromWeb(Connection p_con, CP2PSubscriberVO p_cp2pSubscriberVO) throws BTSLBaseException {
        final String methodName = "changePin";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered subscriberVO : " + p_cp2pSubscriberVO);
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder(" UPDATE  p2p_subscribers set pin = ? ");
            strBuff.append("  , status=?,activated_on=? ");
            strBuff.append("  , modified_by = ? , modified_on = ? , pin_modified_on = ? WHERE msisdn = ? AND status = ? AND subscriber_type = ? ");
            String query = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + query);
           try(PreparedStatement psmt = p_con.prepareStatement(query);)
           {
            int i = 1;
            psmt.setString(i++, BTSLUtil.encryptText(p_cp2pSubscriberVO.getSmsPin()));
            psmt.setString(i++, p_cp2pSubscriberVO.getStatus());
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_cp2pSubscriberVO.getModifiedOn()));
            psmt.setString(i++, p_cp2pSubscriberVO.getModifiedBy());
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_cp2pSubscriberVO.getModifiedOn()));
            // pin modified on should be set while updating the pin field. Added
            // by Ashish- 27-10-06
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_cp2pSubscriberVO.getPinModifiedOn()));
            psmt.setString(i++, p_cp2pSubscriberVO.getMsisdn());
            psmt.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            psmt.setString(i++, p_cp2pSubscriberVO.getSubscriberType());

            updateCount = psmt.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changePin]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changePin]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
        	if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:updateCount=" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * Method to check whether the subscriber is already have card's schedule
     * top-up enabled
     * 
     * @param p_con
     * @param p_userId
     * @return boolean
     * @throws BTSLBaseException
     * @author vikas.singh
     */
    public boolean checkAlreadyEnabledCard(Connection p_con, String p_userID, String p_cardNickName) throws BTSLBaseException {
        boolean isRegistered = false;
        final String methodName = "checkAlreadyEnabledCard";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_userID: " + p_userID + "p_cardNickName=" + p_cardNickName);
        
        String selectQuery = "SELECT 1 from SCHEDULE_TOPUP_DETAILS where status=? and user_id=? and nick_name=? ";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "select query:" + selectQuery);
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
          
            pstmtSelect.setString(1, PretupsI.YES);
            pstmtSelect.setString(2, p_userID);
            pstmtSelect.setString(3, p_cardNickName);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isRegistered = true;
            }
        }
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkAlreadyEnabled]", "", p_userID, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting :isRegistered:" + isRegistered);
        }
        return isRegistered;
    }

    /**
     * Method to check whether the subscriber is already have card's schedule
     * top-up enabled
     * 
     * @param p_con
     * @param p_userId
     * @return boolean
     * @throws BTSLBaseException
     * @author vikas.singh
     */
    public boolean checkAlreadyEnabledInDeactivation(Connection p_con, String p_userID) throws BTSLBaseException {
        boolean isRegistered = false;
        final String methodName = "checkAlreadyEnabledInDeactivation";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_userID: " + p_userID);
        
        String selectQuery = "SELECT 1 from SCHEDULE_TOPUP_DETAILS where user_id=? ";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "select query:" + selectQuery);
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
          
            pstmtSelect.setString(1, p_userID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isRegistered = true;
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkAlreadyEnabled]", "", p_userID, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting :isRegistered:" + isRegistered);
        }
        return isRegistered;
    }

}
