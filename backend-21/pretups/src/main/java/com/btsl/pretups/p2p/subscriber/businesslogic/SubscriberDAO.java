package com.btsl.pretups.p2p.subscriber.businesslogic;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.p2p.logging.UnregisterSubscribersFileProcessLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.subscriber.businesslogic.PostPaidControlParametersVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 */
public class SubscriberDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static final String SQL_EXCEPTION = "SQL Exception: ";
    private static final String EXCEPTION = "Exception: ";
    private static final String QUERY_KEY = "Query:";
    private SubscriberQry subscriberQry = (SubscriberQry) ObjectProducer.getObject(QueryConstants.SUBSCRIBER_QRY, QueryConstants.QUERY_PRODUCER);

    /**
     * Constructor for SubscriberDAO.
     */
    public SubscriberDAO() {
        super();
        // TODO Auto-generated constructor stub
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
    public SenderVO loadSubscriberDetailsByMsisdn(Connection p_con, String p_msisdn, String serviceType ) throws BTSLBaseException {
        final String methodName = "loadSubscriberDetailsByMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append("serviceType=");
        	loggerValue.append(serviceType);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        SenderVO senderVO = null;
        ResultSet rs = null;
        try {
          
            pstmtSelect = subscriberQry.loadSubscriberDetailsByMsisdnQry(p_con, p_msisdn, serviceType);
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
                
                if(serviceType!=null){
                senderVO.setServiceType(rs.getString("service_type"));	
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
                senderVO.setInvalidVoucherPinCount(rs.getLong("VPIN_INVALID_COUNT"));
                }
                
                senderVO.setServiceClassCode(rs.getString("service_class_code"));
                senderVO.setLastSuccessTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_transfer_date")));
                senderVO.setServiceClassID(rs.getString("service_class_id"));
                senderVO.setLanguage(rs.getString("language"));
                senderVO.setCountry(rs.getString("country"));
                if (BTSLUtil.isNullString(senderVO.getLanguage()) || BTSLUtil.isNullString(senderVO.getCountry())) {
                    senderVO.setLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                } else {
                    senderVO.setLocale(new Locale(senderVO.getLanguage(), senderVO.getCountry()));
                }
                senderVO.setEmailId(rs.getString("email_id"));
                senderVO.setImei(rs.getString("imei"));
            }
            return senderVO;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByMsisdn]", "",
                p_msisdn, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByMsisdn]", "",
                p_msisdn, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: senderVO:");
             	loggerValue.append(senderVO);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_senderVO.getMsisdn());
        	loggerValue.append("User ID=");
        	loggerValue.append(p_senderVO.getUserID());
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final String selectQuery = "UPDATE p2p_subscribers set request_status=?,modified_by=?, modified_on=? WHERE user_id=? AND msisdn=?";
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtUpdate = p_con.prepareStatement(selectQuery);
            pstmtUpdate.setString(1, PretupsI.TXN_STATUS_UNDER_PROCESS);
            pstmtUpdate.setString(2, p_senderVO.getModifiedBy());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_senderVO.getUserID());
            pstmtUpdate.setString(5, p_senderVO.getMsisdn());
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[markRequestUnderProcess]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[markRequestUnderProcess]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_senderVO.getMsisdn());
        	loggerValue.append("User ID=");
        	loggerValue.append(p_senderVO.getUserID());
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final String selectQuery = "UPDATE p2p_subscribers SET request_status=?,modified_by=?, modified_on=?  WHERE user_id=? AND msisdn=?";
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtUpdate = p_con.prepareStatement(selectQuery);
            pstmtUpdate.setString(1, PretupsI.TXN_STATUS_COMPLETED);
            pstmtUpdate.setString(2, p_senderVO.getModifiedBy());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_senderVO.getUserID());
            pstmtUpdate.setString(5, p_senderVO.getMsisdn());
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[unmarkRequestUnderProcess]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[unmarkRequestUnderProcess]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: UpdateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_senderVO.getMsisdn());
        	loggerValue.append("User ID=");
        	loggerValue.append(p_senderVO.getUserID());
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE  p2p_subscribers set pin = ? ");
            if (p_senderVO.isActivateStatusReqd()) {
                strBuff.append("  , status=?,activated_on=? ");
            }
            strBuff.append("  , modified_by = ? , modified_on = ? , pin_modified_on = ? WHERE msisdn = ? AND status = ? AND subscriber_type = ? ");
            final String query = strBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(query);
    			_log.debug(methodName, loggerValue);
    		}
            psmt = p_con.prepareStatement(query);
            int i = 1;
            psmt.setString(i, p_senderVO.getPin());
            i++;
            if (p_senderVO.isActivateStatusReqd()) {
                psmt.setString(i, p_senderVO.getStatus());
                i++;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                i++;
            }
            psmt.setString(i, p_senderVO.getModifiedBy());
            i++;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            i++;
            // pin modified on should be set while updating the pin field. Added
            // by Ashish- 27-10-06
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPinModifiedOn()));
            i++;
            psmt.setString(i, p_senderVO.getMsisdn());
            i++;
            if (p_senderVO.isActivateStatusReqd()) {
                psmt.setString(i, PretupsI.USER_STATUS_NEW);
                i++;
            } else {
                psmt.setString(i, PretupsI.USER_STATUS_ACTIVE);
                i++;
            }
            psmt.setString(i, p_senderVO.getSubscriberType());
            i++;
            updateCount = psmt.executeUpdate();

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changePin]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changePin]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: UpdateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_senderVO.getMsisdn());
        	loggerValue.append("User ID=");
        	loggerValue.append(p_senderVO.getUserID());
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer sbf = new StringBuffer();

            sbf.append("UPDATE p2p_subscribers set status = ?,modified_on = ? , ");
            sbf.append(" modified_by = ? WHERE msisdn=? AND subscriber_type = ?");
            final String updateQuery = sbf.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(updateQuery);
    			_log.debug(methodName, loggerValue);
    		}

            pstmtUpdate = p_con.prepareStatement(updateQuery);
            int i = 1;
            pstmtUpdate.setString(i, p_senderVO.getStatus());
            i++;
            if (p_senderVO.getModifiedOn() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, p_senderVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getMsisdn());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getSubscriberType());
            i++;
            final boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());

            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }

        }// end of try
        catch (BTSLBaseException e) {
            throw e;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberStatus]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberStatus]", "", "", "",
                "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: UpdateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	loggerValue.append("p_isStatusUpdate=");
        	loggerValue.append(p_isStatusUpdate);
        	_log.debug(methodName, loggerValue);
        }
        final String method = methodName;
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer(
                "UPDATE p2p_subscribers SET pin_block_count = ?, first_invalid_pin_time=?,modified_by = ? , modified_on =?   ");
            if (p_isStatusUpdate) {
                updateQueryBuff.append(" ,status=? ");
            }
            updateQueryBuff.append("WHERE msisdn=? and subscriber_type = ? ");
            final String selectUpdate = updateQueryBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectUpdate);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtUpdate = p_con.prepareStatement(selectUpdate);
            pstmtUpdate.setInt(i, p_senderVO.getPinBlockCount());
            i++;
            if (p_senderVO.getFirstInvalidPinTime() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getFirstInvalidPinTime()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.TIMESTAMP);
                i++;
            }
            pstmtUpdate.setString(i, p_senderVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            i++;
            if (p_isStatusUpdate) {
                pstmtUpdate.setString(i, p_senderVO.getStatus());
                i++;
            }
            pstmtUpdate.setString(i, p_senderVO.getMsisdn());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getSubscriberType());
            i++;
            final boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, method, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
            return updateCount;
        }// end of try
        catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updatePinStatus]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updatePinStatus]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: UpdateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            // insert query
            strBuff.append(" INSERT INTO p2p_subscribers  ");
            strBuff.append(" (user_id,msisdn, subscriber_type, prefix_id,status, network_code, pin, request_status, ");
            strBuff.append(" service_class_code,service_class_id,activated_on,registered_on,  created_on, created_by, modified_on, modified_by,language, country ) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            
       
            final String query = strBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(query);
    			_log.debug(methodName, loggerValue);
    		}

            psmt = p_con.prepareStatement(query);
            int m = 0;
            ++m;
            psmt.setString(m, p_senderVO.getUserID());
            ++m;
            psmt.setString(m, p_senderVO.getMsisdn());
            ++m;
            psmt.setString(m, p_senderVO.getSubscriberType());
            ++m;
            psmt.setLong(m, p_senderVO.getPrefixID());
            ++m;
            psmt.setString(m, p_senderVO.getStatus());
            ++m;
            psmt.setString(m, p_senderVO.getNetworkCode());
            ++m;
            psmt.setString(m, BTSLUtil.encryptText(p_senderVO.getPin()));
            ++m;
            psmt.setString(m, PretupsI.TXN_STATUS_COMPLETED);
            ++m;
            psmt.setString(m, p_senderVO.getServiceClassCode());
            ++m;
            psmt.setString(m, p_senderVO.getServiceClassID());
            if (p_senderVO.getActivatedOn() != null) {
                ++m;
                psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
            } else {
                ++m;
                psmt.setNull(m, Types.DATE);
            }
            ++m;
            psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getRegisteredOn()));
            ++m;
            psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getCreatedOn()));
            ++m;
            psmt.setString(m, p_senderVO.getCreatedBy());
            ++m;
            psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            ++m;
            psmt.setString(m, p_senderVO.getModifiedBy());
            ++m;
            psmt.setString(m, p_senderVO.getLanguage());
            ++m;
            psmt.setString(m, p_senderVO.getCountry());
            updateCount = psmt.executeUpdate();

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSubscriber]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSubscriber]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: UpdateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
	public BuddyVO loadBuddyDetails(Connection p_con, String p_parentID, String p_buddy) throws BTSLBaseException
	{
		if (_log.isDebugEnabled())
			_log.debug("loadBuddyDetails", "Entered p_parentID:" + p_parentID + " p_buddy:" + p_buddy);
		
		PreparedStatement pstmtSelect = null;
		BuddyVO buddyVO = null;
		ResultSet rs = null;
		try
		{
			StringBuffer selectQueryBuff = new StringBuffer();
			selectQueryBuff.append("SELECT buddy_msisdn, parent_id, buddy_seq_num,buddy_name,status, ");
			selectQueryBuff.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type, ");
			selectQueryBuff.append("buddy_total_transfer, buddy_total_transfer_amt, created_on, created_by,modified_on, ");
			selectQueryBuff.append("modified_by,last_transfer_amount,prefix_id,preferred_amount ");
			selectQueryBuff.append("FROM p2p_buddies ");
			selectQueryBuff.append("WHERE parent_id=? AND (upper(buddy_name)=?  OR buddy_msisdn=?) ");
			String selectQuery = selectQueryBuff.toString();
			if (_log.isDebugEnabled())
				_log.debug("loadBuddyDetails", "select query:" + selectQuery);
			pstmtSelect = p_con.prepareStatement(selectQuery);
			pstmtSelect.setString(1, p_parentID);
			pstmtSelect.setString(2, p_buddy.toUpperCase());
			pstmtSelect.setString(3, p_buddy);
			rs = pstmtSelect.executeQuery();
			if (rs.next())
			{
				buddyVO = new BuddyVO();
				buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
				buddyVO.setOwnerUser(rs.getString("parent_id"));
				buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
				buddyVO.setName(rs.getString("buddy_name"));
				buddyVO.setPrefixID(rs.getLong("prefix_id"));
				buddyVO.setStatus(rs.getString("status"));
				buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
				if(rs.getTimestamp("buddy_last_transfer_on")!= null)
					buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
				buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
				buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
				buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
				if(rs.getTimestamp("created_on")!= null)
					buddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				buddyVO.setCreatedBy(rs.getString("created_by"));
				if(rs.getTimestamp("modified_on")!= null)
					buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
				buddyVO.setModifiedBy(rs.getString("modified_by"));
				buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
				buddyVO.setPrefixID(rs.getLong("prefix_id"));
				buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
			}
			return buddyVO;
		}// end of try
		catch (SQLException sqle)
		{
			_log.error("loadBuddyDetails", "SQLException " + sqle.getMessage());
			_log.errorTrace("loadBuddyDetails", sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[loadBuddyDetails]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "loadBuddyDetails", "error.general.sql.processing");
		}// end of catch
		catch (Exception e)
		{
			_log.error("loadBuddyDetails", "Exception " + e.getMessage());
			_log.errorTrace("loadBuddyDetails", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[loadBuddyDetails]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "loadBuddyDetails", "error.general.processing");
		}// end of catch
		finally
		{
			try{if (rs != null)	rs.close();} catch (Exception e){}
			try	{if (pstmtSelect != null)pstmtSelect.close();} catch (Exception e){}
			if (_log.isDebugEnabled())
				_log.debug("loadBuddyDetails", "Exiting buddyVO:" + buddyVO);
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement psmtDelete = null;
        PreparedStatement psmtDeleteCounters = null;
        int deleteCount = 0;
        int deleteCountCounters = 0;
        try {
            final StringBuilder strBuff = new StringBuilder();
            final StringBuilder strBuffdeletefromCounters = new StringBuilder();
            // added by harsh to delete schedule batch on deleting subscriber
            this.deleteScheduleBatch(p_con, p_senderVO.getUserID());
            // end added by
            strBuff.append("DELETE FROM p2p_subscribers WHERE msisdn = ? AND subscriber_type=? AND network_code= ? ");
            strBuffdeletefromCounters.append(" DELETE FROM p2p_subscribers_counters where msisdn=? and user_id=?");
            final String deleteQuery = strBuff.toString();
            final String deleteQuerycounters = strBuffdeletefromCounters.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(deleteQuery);
    			_log.debug(methodName, loggerValue);
    		}
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_senderVO.getMsisdn());
            psmtDelete.setString(2, p_senderVO.getSubscriberType());
            psmtDelete.setString(3, p_senderVO.getNetworkCode());
            final boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            
            psmtDeleteCounters = p_con.prepareStatement(deleteQuerycounters);
            psmtDeleteCounters.setString(1, p_senderVO.getMsisdn());
            psmtDeleteCounters.setString(2, p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                deleteCount = psmtDelete.executeUpdate();
                
                if (deleteCount > 0) {
                	deleteCountCounters = psmtDeleteCounters.executeUpdate();
                    deleteCount = this.addSubscriberHistory(p_con, p_senderVO);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriber]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriber]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtDeleteCounters != null) {
                	psmtDeleteCounters.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: deleteCount:");
             	loggerValue.append(deleteCount);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtInsert = null;
        int insertCount = 0;
        try {
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO p2p_subscribers_history(user_id, msisdn, subscriber_type, user_name, ");
            insertQuery.append("status, network_code, pin, pin_block_count, last_transfer_amount, last_transfer_on, ");
            insertQuery.append("last_transfer_type, last_transfer_status, buddy_seq_number, total_transfers, ");
            insertQuery.append("total_transfer_amount, request_status, billing_type, billing_cycle_date, ");
            insertQuery.append("credit_limit, activated_on, registered_on, created_on, created_by, modified_on, ");
            insertQuery.append("modified_by, last_transfer_id, consecutive_failures, last_transfer_msisdn, ");
            insertQuery.append("skey_required,service_class_code,last_success_transfer_date, prefix_id, remarks) ");

            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query:" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);
            int i = 1;
            pstmtInsert.setString(i, p_senderVO.getUserID());
            i++;
            pstmtInsert.setString(i, p_senderVO.getMsisdn());
            i++;
            pstmtInsert.setString(i, p_senderVO.getSubscriberType());
            i++;
            pstmtInsert.setString(i, p_senderVO.getUserName());
            i++;
            pstmtInsert.setString(i, p_senderVO.getStatus());
            i++;
            pstmtInsert.setString(i, p_senderVO.getNetworkCode());
            i++;
            pstmtInsert.setString(i, p_senderVO.getPin());
            i++;
            pstmtInsert.setInt(i, p_senderVO.getPinBlockCount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getLastTransferAmount());
            i++;
            if (p_senderVO.getLastTransferOn() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }
            pstmtInsert.setString(i, p_senderVO.getLastTransferType());
            i++;
            pstmtInsert.setString(i, p_senderVO.getLastTransferStatus());
            i++;
            pstmtInsert.setInt(i, p_senderVO.getBuddySeqNumber());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getTotalTransfers());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getTotalTransferAmount());
            i++;
            pstmtInsert.setString(i, p_senderVO.getRequestStatus());
            i++;
            pstmtInsert.setString(i, p_senderVO.getBillingType());
            i++;
            if (p_senderVO.getBillingCycleDate() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getBillingCycleDate()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }
            pstmtInsert.setLong(i, p_senderVO.getCreditLimit());
            i++;
            if (p_senderVO.getActivatedOn() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }
            if (p_senderVO.getRegisteredOn() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getRegisteredOn()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getCreatedOn()));
            i++;
            pstmtInsert.setString(i, p_senderVO.getCreatedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            i++;
            pstmtInsert.setString(i, p_senderVO.getModifiedBy());
            i++;
            pstmtInsert.setString(i, p_senderVO.getLastTransferID());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getConsecutiveFailures());
            i++;
            pstmtInsert.setString(i, p_senderVO.getLastTransferMSISDN());
            i++;
            pstmtInsert.setString(i, p_senderVO.getSkeyRequired());
            i++;
          /*  pstmtInsert.setLong(i, p_senderVO.getDailyTransferCount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getMonthlyTransferCount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getWeeklyTransferCount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getPrevDailyTransferCount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getPrevWeeklyTransferCount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getPrevMonthlyTransferCount());
            i++;*/
            pstmtInsert.setString(i, p_senderVO.getServiceClassCode());
            i++;
      /*      pstmtInsert.setLong(i, p_senderVO.getDailyTransferAmount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getWeeklyTransferAmount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getMonthlyTransferAmount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getPrevDailyTransferAmount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getPrevWeeklyTransferAmount());
            i++;
            pstmtInsert.setLong(i, p_senderVO.getPrevMonthlyTransferAmount());
            i++;*/
           /* if (p_senderVO.getPrevTransferDate() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPrevTransferDate()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }
            if (p_senderVO.getPrevTransferWeekDate() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPrevTransferWeekDate()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }

            if (p_senderVO.getPrevTransferMonthDate() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getPrevTransferMonthDate()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }*/
            if (p_senderVO.getLastSuccessTransferDate() != null) {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastSuccessTransferDate()));
                i++;
            } else {
                pstmtInsert.setTimestamp(i, null);
                i++;
            }
            pstmtInsert.setLong(i, p_senderVO.getPrefixID());
            i++;
            if (p_senderVO.getRemarks() != null && p_senderVO.getRemarks().length() > 100) {
                pstmtInsert.setString(i, p_senderVO.getRemarks().substring(99));
                i++;
            } else {
                pstmtInsert.setString(i, p_senderVO.getRemarks());
                i++;
            }
            insertCount = pstmtInsert.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addSubscriberHistory]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addSubscriberHistory]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: InsertCount:");
             	loggerValue.append(insertCount);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
        return insertCount;
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_filteredMsisdn=");
        	loggerValue.append(p_filteredMsisdn);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        boolean status = false;
        ResultSet rs = null;
        try {
            final String selectQuery = "SELECT last_transfer_status FROM p2p_subscribers WHERE msisdn=?";
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_filteredMsisdn);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                if (rs.getString("last_transfer_status") != null && rs.getString("last_transfer_status").equals(PretupsI.TXN_STATUS_UNDER_PROCESS)) {
                    status = true;
                }
            } else {
                status = true;
                throw new BTSLBaseException(this, methodName, "p2psubscriber.msg.subsdeleted");
            }
        }// end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: status:");
             	loggerValue.append(status);
             	_log.debug(methodName, loggerValue);
             }
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
    public ArrayList loadSubscriberDetails(Connection p_con, String p_msisdn, Date p_fromDate, Date p_toDate, String p_status, String serviceType) throws BTSLBaseException {
        final String methodName = "loadSubscriberDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append("p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_status=");
        	loggerValue.append(p_status);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        SenderVO senderVO = null;
        final ArrayList subscriberList = new ArrayList();
        ResultSet rs = null;
        try {
            pstmtSelect = subscriberQry.loadSubscriberDetailsQry(p_con, p_msisdn, serviceType, p_fromDate, p_toDate, p_status);
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
                if ("SHA".equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    senderVO.setPin(rs.getString("pin"));
                } else {
                    senderVO.setPin(BTSLUtil.decryptText(rs.getString("pin")));
                }
                senderVO.setPinBlockCount(rs.getInt("pin_block_count"));
                senderVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                senderVO.setLastTransferOn(rs.getDate("last_transfer_on"));
                if (rs.getDate("last_transfer_on") != null) {
                    senderVO.setLastTxnOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("last_transfer_on"))));
                }
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
                if (rs.getTimestamp("billing_cycle_date") != null) {
                    senderVO.setBillingCycleDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("billing_cycle_date")));
                }
                senderVO.setCreditLimit(rs.getLong("credit_limit"));
                senderVO.setCreditLimitStr(PretupsBL.getDisplayAmount(rs.getLong("credit_limit")));
                senderVO.setActivatedOn(rs.getDate("activated_on"));
                if (rs.getDate("activated_on") != null) {
                    senderVO.setActivatedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("activated_on"))));
                }
                senderVO.setRegisteredOn(rs.getDate("registered_on"));
                if (rs.getDate("registered_on") != null) {
                    senderVO.setRegisteredOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("registered_on"))));
                }
                senderVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                senderVO.setCreatedBy(rs.getString("created_by"));
                senderVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                senderVO.setModifiedBy(rs.getString("modified_by"));
                senderVO.setConsecutiveFailures(rs.getLong("consecutive_failures"));
                senderVO.setSkeyRequired(rs.getString("skey_required"));
                if(PretupsI.ALL.equals(serviceType)){
                	StringBuilder strBuff = new StringBuilder();
                	strBuff.append(" SELECT SUM(P2P_SUB_CNT.daily_transfer_count) daily_transfer_count,SUM(P2P_SUB_CNT.monthly_transfer_count) monthly_transfer_count,SUM(P2P_SUB_CNT.weekly_transfer_count) weekly_transfer_count,SUM(P2P_SUB_CNT.daily_transfer_amount) daily_transfer_amount, ");
                	strBuff.append(" SUM(P2P_SUB_CNT.monthly_transfer_amount) monthly_transfer_amount,SUM(P2P_SUB_CNT.weekly_transfer_amount) weekly_transfer_amount,SUM(P2P_SUB_CNT.prev_daily_transfer_count) prev_daily_transfer_count,SUM(P2P_SUB_CNT.prev_monthly_transfer_count) prev_monthly_transfer_count, ");
                	strBuff.append(" SUM(P2P_SUB_CNT.prev_weekly_transfer_count) prev_weekly_transfer_count,SUM(P2P_SUB_CNT.prev_daily_transfer_amount) prev_daily_transfer_amount,SUM(P2P_SUB_CNT.prev_monthly_transfer_amount) prev_monthly_transfer_amount,SUM(P2P_SUB_CNT.prev_weekly_transfer_amount) prev_weekly_transfer_amount ");
                	strBuff.append("FROM p2p_subscribers_counters P2P_SUB_CNT WHERE P2P_SUB_CNT.user_id =? group BY P2P_SUB_CNT.user_id");
                	String sqlSelect = strBuff.toString();
                	if (_log.isDebugEnabled()) {
                		_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
                	}
                	ArrayList list = new ArrayList();
                	try(PreparedStatement pstmt1 = p_con.prepareStatement(sqlSelect);) {
                		pstmt1.setString(1, senderVO.getUserID());
                		try(ResultSet rs1 = pstmt1.executeQuery();)
                		{
                			if (rs1.next()) {                        
                				senderVO.setDailyTransferCount(rs1.getLong("daily_transfer_count"));
                				senderVO.setMonthlyTransferCount(rs1.getLong("monthly_transfer_count"));
                				senderVO.setWeeklyTransferCount(rs1.getLong("weekly_transfer_count"));
                				senderVO.setDailyTransferAmount(rs1.getLong("daily_transfer_amount"));
                				senderVO.setMonthlyTransferAmount(rs1.getLong("monthly_transfer_amount"));
                				senderVO.setWeeklyTransferAmount(rs1.getLong("weekly_transfer_amount"));
                				senderVO.setPrevDailyTransferCount(rs1.getLong("prev_daily_transfer_count"));
                				senderVO.setPrevMonthlyTransferCount(rs1.getLong("prev_monthly_transfer_count"));
                				senderVO.setPrevWeeklyTransferCount(rs1.getLong("prev_weekly_transfer_count"));
                				senderVO.setPrevDailyTransferAmount(rs1.getLong("prev_daily_transfer_amount"));
                				senderVO.setPrevMonthlyTransferAmount(rs1.getLong("prev_monthly_transfer_amount"));
                				senderVO.setPrevWeeklyTransferAmount(rs1.getLong("prev_weekly_transfer_amount"));
                			}
                		}
                	} 
                }else if(serviceType!=null){
                	senderVO.setServiceType(rs.getString("service_type"));
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
                	if (rs.getTimestamp("prev_transfer_date") != null) {
                		senderVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_date")));
                	}
                	if (rs.getTimestamp("prev_transfer_week_date") != null) {
                		senderVO.setPrevTransferWeekDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_week_date")));
                	}
                	if (rs.getTimestamp("prev_transfer_month_date") != null) {
                		senderVO.setPrevTransferMonthDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_month_date")));
                	}
                }
                senderVO.setServiceClassCode(rs.getString("service_class_code"));
                if (rs.getTimestamp("last_success_transfer_date") != null) {
                    senderVO.setLastSuccessTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_transfer_date")));
                }
                senderVO.setServiceClassID(rs.getString("service_class_id"));
                senderVO.setLastTransferStatusDesc(rs.getString("txnstatus"));
                subscriberList.add(senderVO);
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetails]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetails]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: subscriberList:");
             	loggerValue.append(subscriberList.size());
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_oldlastModified=");
        	loggerValue.append(p_oldlastModified);
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuffer sqlRecordModified = new StringBuffer();
        sqlRecordModified.append("SELECT modified_on FROM p2p_subscribers ");
        sqlRecordModified.append("WHERE user_id=? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
        	 if(_log.isDebugEnabled()){
     			loggerValue.setLength(0);
     			loggerValue.append(QUERY_KEY);
     			loggerValue.append(sqlRecordModified);
     			_log.debug(methodName, loggerValue);
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
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isRecordModified]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[isRecordModified]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: modified:");
             	loggerValue.append(modified);
             	_log.debug(methodName, loggerValue);
             }
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
        final String methodName = "checkAmbiguousTransfer";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_filteredMsisdn=");
        	loggerValue.append(p_filteredMsisdn);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        boolean status = false;
        ResultSet rs = null;
        try {
            final String selectQuery = "SELECT transfer_status FROM subscriber_transfers  WHERE sender_msisdn=? AND (transfer_status=? OR transfer_status=?) ";
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_filteredMsisdn);
            pstmtSelect.setString(2, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtSelect.setString(3, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                status = true;
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[checkRequestUnderProcess]", "", "", "",
                "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: status:");
             	loggerValue.append(status);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
        return status;
    }
    
    /**
	 * Method to update the control parameters of the subscriber
	 * @param p_con
	 * @param p_senderVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int updateSubscriberCountersDetails(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException
	{
		final String methodName = "updateSubscriberCountersDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtUpdate = null;
		int updateCount = 0;
		try
		{
			boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
			if (modified)
			{
				throw new BTSLBaseException(this, methodName, "error.modify.true");
			} 
			else
			{
				
				StringBuffer sbf = new StringBuffer();
	
				sbf.append(" UPDATE p2p_subscribers SET last_transfer_amount=?,last_transfer_on=?,last_transfer_type=?, ");
				sbf.append(" last_transfer_status=?,last_transfer_id=?,last_transfer_msisdn=?,total_transfers=?,total_transfer_amount=?, ");
				sbf.append(" consecutive_failures=?,daily_transfer_count=?,monthly_transfer_count=?,weekly_transfer_count=?,daily_transfer_amount=?, ");
				sbf.append(" monthly_transfer_amount=?,weekly_transfer_amount=?,prev_daily_transfer_count=?,prev_monthly_transfer_count=?, ");
				sbf.append(" prev_weekly_transfer_count=?,prev_daily_transfer_amount=?,prev_monthly_transfer_amount=?,prev_weekly_transfer_amount=?, ");
				sbf.append(" prev_transfer_date=?,prev_transfer_week_date=?,prev_transfer_month_date=?,last_success_transfer_date=?,modified_on=?,modified_by=? ");
				sbf.append(" WHERE user_id=? ");
				String updateQuery = sbf.toString();
				 if(_log.isDebugEnabled()){
		    			loggerValue.setLength(0);
		    			loggerValue.append(QUERY_KEY);
		    			loggerValue.append(updateQuery);
		    			_log.debug(methodName, loggerValue);
		    		}
	
				int i = 1;
				pstmtUpdate = p_con.prepareStatement(updateQuery);
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
				pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferDate()));
				pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferWeekDate()));
				pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferMonthDate()));
				pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getLastSuccessTransferDate()));
				
				if (p_senderVO.getModifiedOn() != null)
					pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
				else
					pstmtUpdate.setNull(i++, Types.DATE);
				pstmtUpdate.setString(i++, p_senderVO.getModifiedBy());
				pstmtUpdate.setString(i++, p_senderVO.getUserID());
				updateCount = pstmtUpdate.executeUpdate();
			}

		}// end of try
		catch (BTSLBaseException be)
		{
			throw be;
		} 
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[updateSubscriberCountersDetails]","",p_senderVO.getMsisdn(),"","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
		}// end of catch
		catch (Exception e)
		{
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[updateSubscriberCountersDetails]","",p_senderVO.getMsisdn(),"","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",e);
		}// end of catch
		finally
		{
			try
			{
				if (pstmtUpdate != null)
					pstmtUpdate.close();
			} catch (Exception e){
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
		}// end of finally
		return updateCount;
	}

    /**
     * Method to update the control parameters of the subscriber
     * 
     * @param p_con
     * @param p_senderVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubscriberCountersDetails(Connection p_con, SenderVO p_senderVO, String servicetype) throws BTSLBaseException {
        final String methodName = "updateSubscriberCountersDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        int updateservicecount = 0;
        int insertservicecount = 0;
        try {
            final boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {

                final StringBuffer sbf = new StringBuffer();

                sbf.append(" UPDATE p2p_subscribers  SET last_transfer_amount=?,last_transfer_on=?,last_transfer_type=?, ");
                sbf.append(" last_transfer_status=?,last_transfer_id=?,last_transfer_msisdn=?,total_transfers=?,total_transfer_amount=?, ");
                sbf.append(" consecutive_failures=?,last_success_transfer_date=?,modified_on=?,modified_by=? ");
                sbf.append(" WHERE user_id=? ");
                
                if(servicetype!=null){
                	updateservicecount = this.updateSubscriberCountersDetailsService(p_con,p_senderVO,servicetype);
                }
                if(updateservicecount<=0&&servicetype!=null){
                	// need to Insert data for that Service
                	insertservicecount = this.insertSubscriberCountersDetailsService(p_con,p_senderVO,servicetype);
                }
                
                final String updateQuery = sbf.toString();
                if(_log.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateQuery);
        			_log.debug(methodName, loggerValue);
        		}

                int i = 1;
                pstmtUpdate = p_con.prepareStatement(updateQuery);
                pstmtUpdate.setLong(i, p_senderVO.getLastTransferAmount());
                i++;
                if (p_senderVO.getLastTransferOn() != null) {
                    pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
                    i++;
                } else {
                    pstmtUpdate.setNull(i, Types.DATE);
                    i++;
                }
                pstmtUpdate.setString(i, p_senderVO.getLastTransferType());
                i++;
                pstmtUpdate.setString(i, p_senderVO.getLastTransferStatus());
                i++;
                pstmtUpdate.setString(i, p_senderVO.getLastTransferID());
                i++;
                pstmtUpdate.setString(i, p_senderVO.getLastTransferMSISDN());
                i++;
                pstmtUpdate.setLong(i, p_senderVO.getTotalTransfers());
                i++;
                pstmtUpdate.setLong(i, p_senderVO.getTotalTransferAmount());
                i++;
                pstmtUpdate.setLong(i, p_senderVO.getConsecutiveFailures());
                i++;
                
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getLastSuccessTransferDate()));
                i++;

                if (p_senderVO.getModifiedOn() != null) {
                    pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                    i++;
                } else {
                    pstmtUpdate.setNull(i, Types.DATE);
                    i++;
                }
                pstmtUpdate.setString(i, p_senderVO.getModifiedBy());
                i++;
                pstmtUpdate.setString(i, p_senderVO.getUserID());
                i++;
                updateCount = pstmtUpdate.executeUpdate();
            }
                   }// end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "",
                p_senderVO.getMsisdn(), "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "",
                p_senderVO.getMsisdn(), "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PostPaidControlParametersVO postPaidControlParametersVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer(
                " SELECT daily_transfers_allowed,daily_transfer_amt_allowed,weekly_transfers_allowed,weekly_transfer_amt_allowed, ");
            selectQueryBuff.append(" monthly_transfers_allowed,monthly_transfer_amt_allowed, ");
            // Added on 07/02/08
            selectQueryBuff.append(" date_time ");
            selectQueryBuff.append(" FROM postpaid_control_parameters ");
            selectQueryBuff.append(" WHERE msisdn=? ");
            final String selectQuery = selectQueryBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
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
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPostPaidControlParameters]",
                "", p_msisdn, "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberControlDAO[loadPostPaidControlParameters]",
                "", p_msisdn, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: postPaidControlParametersVO:");
             	loggerValue.append(postPaidControlParametersVO);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
    }
    /**
	 * Method to load the subscriber details and locking the record before update
	 * @param p_con
	 * @param p_requestID
	 * @param p_userID
	 * @return SenderVO
	 * @throws BTSLBaseException
	 */
	public SenderVO loadSubscriberDetailsByIDForUpdate(Connection p_con, String p_requestID,String p_userID) throws BTSLBaseException
	{
		final String methodName = "loadSubscriberDetailsByIDForUpdate";
		 StringBuilder loggerValue= new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: p_userID=");
	        	loggerValue.append(p_userID);
	        	_log.debug(methodName, loggerValue);
	        }
		PreparedStatement pstmtSelect = null;
		SenderVO senderVO = null;
		ResultSet rs = null;
		try
		{
			StringBuffer selectQueryBuff = new StringBuffer("SELECT user_id,msisdn,subscriber_type,prefix_id,network_code,last_transfer_amount,last_transfer_on,last_transfer_type,last_transfer_status,last_transfer_id,last_transfer_msisdn,");
			selectQueryBuff.append(" total_transfers,total_transfer_amount, credit_limit,consecutive_failures,");
			selectQueryBuff.append(" daily_transfer_count,monthly_transfer_count,weekly_transfer_count,daily_transfer_amount,");
			selectQueryBuff.append(" monthly_transfer_amount,weekly_transfer_amount,prev_daily_transfer_count,prev_monthly_transfer_count,");
			selectQueryBuff.append(" prev_weekly_transfer_count,prev_daily_transfer_amount,prev_monthly_transfer_amount,prev_weekly_transfer_amount,");
			selectQueryBuff.append(" prev_transfer_date,prev_transfer_week_date,prev_transfer_month_date,service_class_code,last_success_transfer_date");
			//DB220120123for update WITH RS
			if(PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
			selectQueryBuff.append(" FROM P2P_SUBSCRIBERS WHERE user_id=? FOR UPDATE With RS");
			else
				selectQueryBuff.append(" FROM P2P_SUBSCRIBERS WHERE user_id=? FOR UPDATE ");
			String selectQuery = selectQueryBuff.toString();
			 if(_log.isDebugEnabled()){
	    			loggerValue.setLength(0);
	    			loggerValue.append(QUERY_KEY);
	    			loggerValue.append(selectQuery);
	    			_log.debug(methodName, loggerValue);
	    		}
			pstmtSelect = p_con.prepareStatement(selectQuery);
			pstmtSelect.setString(1, p_userID);
			rs = pstmtSelect.executeQuery();
			if (rs.next())
			{
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
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[loadSubscriberDetailsByIDForUpdate]",p_requestID,"","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
		}// end of catch
		catch (Exception e)
		{
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[loadSubscriberDetailsByIDForUpdate]",p_requestID,"","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",e);
		}// end of catch
		finally
		{
			try
			{
				if (rs != null) rs.close();
			} catch (Exception e) {_log.errorTrace(methodName, e);}
			try
			{
				if (pstmtSelect != null) pstmtSelect.close();
			} catch (Exception e) {_log.errorTrace(methodName, e);}
			if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: senderVO:");
             	loggerValue.append(senderVO);
             	_log.debug(methodName, loggerValue);
             }
		}// end of finally
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
    public SenderVO loadSubscriberDetailsByIDForUpdate(Connection p_con, String p_requestID, String p_userID,String serviceType) throws BTSLBaseException {
        final String methodName = "loadSubscriberDetailsByIDForUpdate";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID=");
        	loggerValue.append(p_userID);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        SenderVO senderVO = null;
        ResultSet rs = null;
        try {
            pstmtSelect = subscriberQry.loadSubscriberDetailsByIDForUpdateQry(p_con, p_userID, serviceType);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
            	
                senderVO = new SenderVO();
                senderVO.setUserID(rs.getString("user_id"));
                senderVO.setMsisdn(rs.getString("msisdn"));
                senderVO.setSubscriberType(rs.getString("subscriber_type"));
                senderVO.setPrefixID(rs.getLong("prefix_id"));
                senderVO.setNetworkCode(rs.getString("network_code"));
                senderVO.setStatus(rs.getString("status"));
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
                if(serviceType!=null){
               	senderVO.setServiceType(rs.getString("service_type"));
                senderVO.setDailyTransferCount(rs.getLong("daily_transfer_count"));
                senderVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));
                senderVO.setWeeklyTransferCount(rs.getLong("weekly_transfer_count"));
                senderVO.setDailyTransferAmount(rs.getLong("daily_transfer_amount"));
                senderVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                senderVO.setWeeklyTransferAmount(rs.getLong("weekly_transfer_amount"));
                senderVO.setInvalidVoucherPinCount(rs.getLong("VPIN_INVALID_COUNT"));

                senderVO.setPrevDailyTransferCount(rs.getLong("prev_daily_transfer_count"));
                senderVO.setPrevMonthlyTransferCount(rs.getLong("prev_monthly_transfer_count"));
                senderVO.setPrevWeeklyTransferCount(rs.getLong("prev_weekly_transfer_count"));
                senderVO.setPrevDailyTransferAmount(rs.getLong("prev_daily_transfer_amount"));
                senderVO.setPrevMonthlyTransferAmount(rs.getLong("prev_monthly_transfer_amount"));
                senderVO.setPrevWeeklyTransferAmount(rs.getLong("prev_weekly_transfer_amount"));
                senderVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_date")));
                senderVO.setPrevTransferWeekDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_week_date")));
                senderVO.setPrevTransferMonthDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("prev_transfer_month_date")));
                senderVO.setlastSuccessTransferService(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_date")));
                
                }
                senderVO.setServiceClassCode(rs.getString("service_class_code"));
                senderVO.setLastSuccessTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_success_transfer_date")));
            }
            	
            
            return senderVO;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByIDForUpdate]",
                p_requestID, "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByIDForUpdate]",
                p_requestID, "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: senderVO:");
             	loggerValue.append(senderVO);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer sbf = new StringBuffer();

            sbf.append(" UPDATE p2p_subscribers SET status=? ");
            if (p_senderVO.isPinUpdateReqd()) {
                sbf.append(" , pin=?  ");
            }
            sbf.append(" ,last_transfer_amount=?,last_transfer_on=?,last_transfer_type=?, activated_on=?, ");
            sbf.append(" last_transfer_status=?,last_transfer_id=?,last_transfer_msisdn=?, ");
            sbf.append(" consecutive_failures=?, last_success_transfer_date=?,modified_on=?,modified_by=? ");
            sbf.append(" WHERE user_id=? ");
            final String updateQuery = sbf.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(updateQuery);
    			_log.debug(methodName, loggerValue);
    		}

            int i = 1;
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_senderVO.getStatus());
            i++;
            if (p_senderVO.isPinUpdateReqd()) {
                pstmtUpdate.setString(i, p_senderVO.getPin());
                i++;
            }
            pstmtUpdate.setLong(i, p_senderVO.getLastTransferAmount());
            i++;
            if (p_senderVO.getLastTransferOn() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, p_senderVO.getLastTransferType());
            i++;
            if (p_senderVO.getActivatedOn() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, p_senderVO.getLastTransferStatus());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getLastTransferID());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getLastTransferMSISDN());
            i++;
            pstmtUpdate.setLong(i, p_senderVO.getConsecutiveFailures());
            i++;
            if (p_senderVO.getLastSuccessTransferDate() != null) {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getLastSuccessTransferDate()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.TIMESTAMP);
                i++;
            }
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_senderVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getUserID());
            i++;
            updateCount = pstmtUpdate.executeUpdate();

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberLastDetails]", "",
                p_senderVO.getMsisdn(), "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberLastDetails]", "",
                p_senderVO.getMsisdn(), "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
        final String methodName = "updateLanguageAndCountry";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append("p_country=");
        	loggerValue.append(p_country);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        final String qry = "UPDATE p2p_subscribers SET language =?,country=? WHERE msisdn=?";
        int updCount = -1;
        try {
        	 if(_log.isDebugEnabled()){
     			loggerValue.setLength(0);
     			loggerValue.append(QUERY_KEY);
     			loggerValue.append(qry);
     			_log.debug(methodName, loggerValue);
     		}
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_lang);
            pstmt.setString(2, p_country);
            pstmt.setString(3, p_msisdn);
            // Execute Query
            updCount = pstmt.executeUpdate();
        }// end of try

        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateLanguageAndCountry]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateLanguageAndCountry]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _log.error(" getRoamLocationList ", "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updCount);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer sbf = new StringBuffer();

            sbf.append("UPDATE p2p_subscribers set buddy_seq_number = ?,modified_on = ? , ");
            sbf.append(" modified_by = ? WHERE msisdn=? AND subscriber_type = ?");
            final String updateQuery = sbf.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(updateQuery);
    			_log.debug(methodName, loggerValue);
    		}

            pstmtUpdate = p_con.prepareStatement(updateQuery);
            int i = 1;
            pstmtUpdate.setInt(i, p_senderVO.getBuddySeqNumber());
            i++;
            if (p_senderVO.getModifiedOn() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, p_senderVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getMsisdn());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getSubscriberType());
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        }// end of try
        catch (BTSLBaseException e) {
            throw e;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberBuddySequenceNum]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberBuddySequenceNum]", "",
                "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally

        return updateCount;
    }

    /**
     * 
     * @return
     * @throws BTSLBaseException
     */
    public HashMap loadRegisterationControlCache() throws BTSLBaseException {

        final String methodName = "loadRegisterationControlCache";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final HashMap registerationControlMap = new HashMap();

        final StringBuffer strBuff = new StringBuffer("SELECT network_code , registration_type, validation_required, ");
        strBuff.append(" validation_interface, alternate_interface_check, alternate_interface, ");
        strBuff.append(" registration_to_be_done, default_registration_type,created_by , modified_by , created_on , modified_on   ");
        strBuff.append(" FROM registration_control ");

        final String sqlSelect = strBuff.toString();
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
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
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadRegisterationControlCache]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadRegisterationControlCache]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
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
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: registerationControlMap:");
             	loggerValue.append(registerationControlMap.size());
             	_log.debug(methodName, loggerValue);
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdnList=");
        	loggerValue.append(p_msisdnList.size());
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement psmtSelect = null;
        ResultSet rsSelect = null;
        PreparedStatement psmtDelete = null;
        PreparedStatement psmtDelete1 = null;
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtSelectBuddy = null;
        ResultSet rsSelectBuddy = null;
        PreparedStatement psmtDeleteBuddy = null;
        PreparedStatement psmtInsertBuddy = null;
        int updateCount = 0;
        final StringBuffer invalidMsisdn = new StringBuffer();
        try {
            // select information according to the exist MSISDN in the list
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT user_id, msisdn, subscriber_type, user_name, status, network_code, pin, ");
            selectQuery.append("pin_block_count, last_transfer_amount, last_transfer_on, last_transfer_type, ");
            selectQuery.append("last_transfer_status, buddy_seq_number, total_transfers, total_transfer_amount, ");
            selectQuery.append("request_status, billing_type, billing_cycle_date, credit_limit, activated_on, ");
            selectQuery.append("registered_on, created_on, created_by, modified_on, modified_by, last_transfer_id, ");
            selectQuery.append("consecutive_failures, last_transfer_msisdn, skey_required,service_class_code, ");
           
            selectQuery.append("last_success_transfer_date, prefix_id, pin_modified_on, first_invalid_pin_time, ");
            selectQuery.append("language, country, service_class_id ");
            selectQuery.append("FROM p2p_subscribers  ");
            
            selectQuery.append("WHERE msisdn=? ");
           
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            psmtSelect = p_con.prepareStatement(selectQuery.toString());

            // insert data into p2p_subscribers_history before delete
            final StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("INSERT INTO p2p_subscribers_history(user_id, msisdn, subscriber_type, user_name, ");
            insertQuery.append("status, network_code, pin, pin_block_count, last_transfer_amount, last_transfer_on, ");
            insertQuery.append("last_transfer_type, last_transfer_status, buddy_seq_number, total_transfers, ");
            insertQuery.append("total_transfer_amount, request_status, billing_type, billing_cycle_date, ");
            insertQuery.append("credit_limit, activated_on, registered_on, created_on, created_by, modified_on, ");
            insertQuery.append("modified_by, last_transfer_id, consecutive_failures, last_transfer_msisdn, ");
            insertQuery.append("skey_required,service_class_code, ");
            insertQuery.append("last_success_transfer_date, prefix_id, remarks)");
            insertQuery.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertQuery);
    			_log.debug(methodName, loggerValue);
    		}
            psmtInsert = p_con.prepareStatement(insertQuery.toString());

            // delete data from p2p_subscribers on the basis of MSISDN
            final StringBuffer deleteQuery = new StringBuffer();
            final StringBuffer deleteQuery1 = new StringBuffer();
            deleteQuery.append("DELETE FROM p2p_subscribers  WHERE user_id = ? ");
            deleteQuery1.append("DELETE FROM p2p_subscribers_counters  WHERE user_id = ? ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(deleteQuery);
    			_log.debug(methodName, loggerValue);
    		}
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(deleteQuery1);
    			_log.debug(methodName, loggerValue);
    		}
            psmtDelete = p_con.prepareStatement(deleteQuery.toString());
            psmtDelete1 = p_con.prepareStatement(deleteQuery1.toString());

            // select information of buddies for the MSISDN
            final StringBuffer selectBuddyQuery = new StringBuffer();
            selectBuddyQuery.append("SELECT buddy_msisdn,parent_id, buddy_seq_num,buddy_name, status,  ");
            selectBuddyQuery.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type,");
            selectBuddyQuery.append("buddy_total_transfer,buddy_total_transfer_amt,created_on, created_by,modified_on, ");
            selectBuddyQuery.append("modified_by,preferred_amount, last_transfer_amount, prefix_id ");
            selectBuddyQuery.append("FROM p2p_buddies ");
            selectBuddyQuery.append("WHERE parent_id=? ");
            selectBuddyQuery.append("ORDER BY buddy_seq_num ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectBuddyQuery);
    			_log.debug(methodName, loggerValue);
    		}
            psmtSelectBuddy = p_con.prepareStatement(selectBuddyQuery.toString());

            // delete buddies
            final StringBuffer deleteBuddyQuery = new StringBuffer();
            deleteBuddyQuery.append(" DELETE FROM p2p_buddies  ");
            deleteBuddyQuery.append(" WHERE parent_id=? ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(deleteBuddyQuery);
    			_log.debug(methodName, loggerValue);
    		}
            psmtDeleteBuddy = p_con.prepareStatement(deleteBuddyQuery.toString());

            // insert data into p2p_buddies_history before delete
            final StringBuffer insertBuddyQuery = new StringBuffer();
            insertBuddyQuery.append(" INSERT INTO p2p_buddies_history ( buddy_msisdn, parent_id, buddy_seq_num, buddy_name, status, buddy_last_transfer_id, ");
            insertBuddyQuery.append(" buddy_last_transfer_on, buddy_last_transfer_type, buddy_total_transfer,  ");
            insertBuddyQuery.append(" buddy_total_transfer_amt, created_on, created_by, modified_on, modified_by,  ");
            insertBuddyQuery.append(" preferred_amount, last_transfer_amount, prefix_id ) ");
            insertBuddyQuery.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertBuddyQuery);
    			_log.debug(methodName, loggerValue);
    		}
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
                Date currentDate = new Date();
                if (rsSelect.next()) {
                    userID = rsSelect.getString("user_id");
                    lastTransferStatus = rsSelect.getString("last_transfer_status");
                    if (!BTSLUtil.isNullString(lastTransferStatus) && PretupsI.TXN_STATUS_UNDER_PROCESS.equals(lastTransferStatus)) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Transaction is under process " + listValueVO.getValue());
                        }
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
                    psmtInsert.setTimestamp(20, rsSelect.getTimestamp("activated_on")); //changed by satakshi so that time along with date can be inserted
                    psmtInsert.setTimestamp(21, rsSelect.getTimestamp("registered_on")); //changed by satakshi so that time along with date can be inserted
                    psmtInsert.setTimestamp(22, rsSelect.getTimestamp("created_on")); //changed by satakshi so that time along with date can be inserted
                    psmtInsert.setString(23, rsSelect.getString("created_by"));
                    psmtInsert.setTimestamp(24, BTSLUtil.getTimestampFromUtilDate(currentDate)); //changed by satakshi so that modified time will be deletion time
                    psmtInsert.setString(25, rsSelect.getString("modified_by"));
                    psmtInsert.setString(26, rsSelect.getString("last_transfer_id"));
                    psmtInsert.setLong(27, rsSelect.getLong("consecutive_failures"));
                    psmtInsert.setString(28, rsSelect.getString("last_transfer_msisdn"));
                    psmtInsert.setString(29, rsSelect.getString("skey_required"));
                    psmtInsert.setString(30, rsSelect.getString("service_class_code"));
                    psmtInsert.setDate(31, rsSelect.getDate("last_success_transfer_date"));
                    psmtInsert.setInt(32, rsSelect.getInt("prefix_id"));
                    psmtInsert.setString(33, "Subscriber is Deregistered in Bulk Operation");

                    updateCount = psmtInsert.executeUpdate();
                    if (updateCount <= 0) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Data Can not move to history " + listValueVO.getValue());
                        }
                        UnregisterSubscribersFileProcessLog.log("MOVE TO HISTORY", "", listValueVO.getValue(), i, "Data Can not move to history", "Fail", "");
                        invalidMsisdn.append(listValueVO.getValue());
                        invalidMsisdn.append(",");
                        psmtInsert.clearParameters();
                        p_con.rollback();
                        continue;
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Mobile number moved to history" + listValueVO.getValue());
                    }
                    UnregisterSubscribersFileProcessLog.log("MSISDN MOVE TO HISTORY", "", listValueVO.getValue(), i, "Mobile number moved to history", "PASS",
                        "User id = " + userID);
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Mobile number does not exist " + listValueVO.getValue());
                    }
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
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Buddy moved to history " + buddyMsisdn);
                    }
                    UnregisterSubscribersFileProcessLog.log("BUDDY MOVE TO HISTORY", "", buddyMsisdn, i, "Buddy move to history", "PASS", "User id = " + userID);
                }
                if (updateCount <= 0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Buddy can not move to history" + listValueVO.getValue());
                    }
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
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Buddy can not be deleted" + listValueVO.getValue());
                    }
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
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Buddy deleted of UserID = " + userID + " Msisdn = " + listValueVO.getValue());
                }
                UnregisterSubscribersFileProcessLog.log("BUDDY DELETED OF MSISDN", "", listValueVO.getValue(), i, "Buddy deleted of userID = " + userID, "PASS",
                    "User id = " + userID);

                // delete the suibscriber from the table
                psmtDelete.setString(1, userID);
                
                updateCount = psmtDelete.executeUpdate();
                if (updateCount <= 0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Data for this MSISDN can not be deleted" + listValueVO.getValue());
                    }
                    UnregisterSubscribersFileProcessLog.log("DATA CAN NOT BE DELETED", "", listValueVO.getValue(), i, "Data for this MSISDN can not be deleted.", "Fail", "");
                    invalidMsisdn.append(listValueVO.getValue());
                    invalidMsisdn.append(",");
                    psmtDelete.clearParameters();
                    p_con.rollback();
                    continue;
                }
                psmtDelete.clearParameters();
                
                psmtDelete1.setString(1, userID);
                updateCount= psmtDelete1.executeUpdate();
                if (updateCount <= 0) { //changing this block of code since it may happen that user was newly registered and no transactions were executed with this msisdn
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Data for this MSISDN can not be deleted from P2P_subscriber_counters since user id not found in this table " + listValueVO.getValue());
                    }
                  //  UnregisterSubscribersFileProcessLog.log("DATA CAN NOT BE DELETED", "", listValueVO.getValue(), i, "Data for this MSISDN can not be deleted.", "Fail", "");
                  //  invalidMsisdn.append(listValueVO.getValue());
                  //  invalidMsisdn.append(",");
                  //  psmtDelete1.clearParameters();
                 //   p_con.rollback();
                 //  continue;
                }
                psmtDelete1.clearParameters();
                p_con.commit();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "MSISDN DELETED OF = " + userID + " Msisdn = " + listValueVO.getValue());
                }
                UnregisterSubscribersFileProcessLog
                    .log("MSISDN DELETED", "", listValueVO.getValue(), i, "MSISDN deleted of userID = " + userID, "PASS", "User id = " + userID);

            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriberBulk]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteSubscriberBulk]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (psmtDeleteBuddy != null) {
                    psmtDeleteBuddy.close();
                }
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
                if (psmtDelete1 != null) {
                    psmtDelete1.close();
                }
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
                if (psmtInsertBuddy != null) {
                    psmtInsertBuddy.close();
                }
                if (rsSelect != null) {
                    rsSelect.close();
                }
                if (psmtSelect != null) {
                    psmtSelect.close();
                }
                if (rsSelectBuddy != null) {
                    rsSelectBuddy.close();
                }
                if (psmtSelectBuddy != null) {
                    psmtSelectBuddy.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: invalidMsisdn:");
             	loggerValue.append(invalidMsisdn);
             	_log.debug(methodName, loggerValue);
             }
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
        final String methodName = "updateSubscriberPrefixID";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	loggerValue.append("p_prefixID=");
        	loggerValue.append(p_prefixID);
        	_log.debug(methodName, loggerValue);
        }
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuffer sbf = new StringBuffer();
            int i = 1;

            sbf.append("UPDATE p2p_subscribers SET user_name=?, prefix_id=? ");
            sbf.append("WHERE msisdn=? AND subscriber_type =?");
            final String updateQuery = sbf.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(updateQuery);
    			_log.debug(methodName, loggerValue);
    		}

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(updateQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_senderVO.getModifiedBy());
            i++;
            pstmtUpdate.setLong(i, p_prefixID);
            i++;
            pstmtUpdate.setString(i, p_senderVO.getMsisdn());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getSubscriberType());
            i++;
            updateCount = pstmtUpdate.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
    public int updateSubscriberDetailsByMSISDN(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "updateSubscriberDetailsByMSISDN";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        final String method = "updateSubscriberDetails";
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;

        try {
            final StringBuffer sbf = new StringBuffer();

            sbf.append("UPDATE p2p_subscribers SET activated_on=?, last_transfer_on=?,total_transfers=?,total_transfer_amount=?, ");
            sbf.append(" status = ?,user_name=?,billing_type=?, ");
            sbf.append(" billing_cycle_date=?,credit_limit=? ,modified_on = ? , ");
            sbf.append(" modified_by = ?,prefix_id=? ,subscriber_type = ? ,service_class_id=?,service_class_code=? WHERE msisdn=? ");
            final String updateQuery = sbf.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(updateQuery);
    			_log.debug(methodName, loggerValue);
    		}

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(updateQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            int i = 1;
            if (p_senderVO.getModifiedOn() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            if (p_senderVO.getLastTransferOn() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getLastTransferOn()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setLong(i, p_senderVO.getTotalTransfers());
            i++;
            pstmtUpdate.setLong(i, p_senderVO.getTotalTransferAmount());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getStatus());
            i++;
            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i, p_senderVO.getUserName());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getBillingType());
            i++;
            if (p_senderVO.getBillingCycleDate() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getBillingCycleDate()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setLong(i, p_senderVO.getMonthlyTransferAmount());
            i++;
            if (p_senderVO.getModifiedOn() != null) {
                pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, p_senderVO.getModifiedBy());
            i++;
            pstmtUpdate.setLong(i, p_senderVO.getPrefixID());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getSubscriberType());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getServiceClassID());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getServiceClassCode());
            i++;
            pstmtUpdate.setString(i, p_senderVO.getMsisdn());
            i++;
            final boolean modified = this.isRecordModified(p_con, p_senderVO.getLastModifiedTime(), p_senderVO.getUserID());
            if (modified) {
                throw new BTSLBaseException(this, method, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }

        }// end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberDetailsByMSISDN]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberDetailsByMSISDN]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userId=");
        	loggerValue.append(p_userId);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(100);
            // insert query
            strBuff.append(" DELETE FROM p2p_batches  ");
            strBuff.append(" WHERE parent_id=? ");

            final String query = strBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(query);
    			_log.debug(methodName, loggerValue);
    		}

            psmt = p_con.prepareStatement(query);
            psmt.setString(1, p_userId);
            updateCount = psmt.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteScheduleBatch]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, "deleteBuddiesList", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[deleteScheduleBatch]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
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
     * @author sonali.garg
     */
    public int registerSelfTopUpSubscriber(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {

        final String methodName = "registerSelfTopUpSubscriber";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_senderVO=");
        	loggerValue.append(p_senderVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            // insert query
            strBuff.append(" INSERT INTO p2p_subscribers  ");
            strBuff.append(" (user_id,msisdn, subscriber_type, prefix_id,status, network_code, pin, request_status, ");
            strBuff
                .append(" service_class_code,service_class_id,activated_on,registered_on,  created_on, created_by, modified_on, modified_by,language, country, login_id,password,imei,email_id,ENCRYPTION_KEY ) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            final String query = strBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(query);
    			_log.debug(methodName, loggerValue);
    		}

            psmt = p_con.prepareStatement(query);
            int m = 0;
            ++m;
            psmt.setString(m, p_senderVO.getUserID());
            ++m;
            psmt.setString(m, p_senderVO.getMsisdn());
            ++m;
            psmt.setString(m, p_senderVO.getSubscriberType());
            ++m;
            psmt.setLong(m, p_senderVO.getPrefixID());
            ++m;
            psmt.setString(m, p_senderVO.getStatus());
            ++m;
            psmt.setString(m, p_senderVO.getNetworkCode());
            ++m;
            psmt.setString(m, BTSLUtil.encryptText(p_senderVO.getPin()));
            ++m;
            psmt.setString(m, PretupsI.TXN_STATUS_COMPLETED);
            ++m;
            psmt.setString(m, p_senderVO.getServiceClassCode());
            ++m;
            psmt.setString(m, p_senderVO.getServiceClassID());
            if (p_senderVO.getActivatedOn() != null) {
                ++m;
                psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getActivatedOn()));
            } else {
                ++m;
                psmt.setNull(m, Types.DATE);
            }
            ++m;
            psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getRegisteredOn()));
            ++m;
            psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getCreatedOn()));
            ++m;
            psmt.setString(m, p_senderVO.getCreatedBy());
            ++m;
            psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_senderVO.getModifiedOn()));
            ++m;
            psmt.setString(m, p_senderVO.getModifiedBy());
            ++m;
            psmt.setString(m, p_senderVO.getLanguage());
            ++m;
            psmt.setString(m, p_senderVO.getCountry());
            ++m;
            psmt.setString(m, p_senderVO.getLogin());
            ++m;
            psmt.setString(m, p_senderVO.getPassword());
            ++m;
            psmt.setString(m, p_senderVO.getImei());
            ++m;
            psmt.setString(m, p_senderVO.getEmailId());
            ++m;
            psmt.setString(m, p_senderVO.getEncryptionKey());
            updateCount = psmt.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSelfTopUpSubscriber]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[registerSelfTopUpSubscriber]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally

        return updateCount;

    }
    
    public int updateSubscriberCountersDetailsService(Connection p_con, SenderVO p_senderVO, String servicetype) throws BTSLBaseException {
    	
    	 final String methodName = "updateSubscriberCountersDetails";
    	 StringBuilder loggerValue= new StringBuilder();
         if (_log.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Entered: p_senderVO=");
         	loggerValue.append(p_senderVO);
         	_log.debug(methodName, loggerValue);
         }
         PreparedStatement pstmtUpdate = null;
         PreparedStatement pstmtUpdate2 = null;
         int updateCount=0;
        Date previousDate = p_senderVO.getLastSuccessTransferDate();
		Date currentDate = new Date();
		if(previousDate==null)
			previousDate=currentDate;		
		try{
        
		P2PSubscriberCounterVO p2PSubscriberCounterVO = new P2PSubscriberCounterVO();
		if(p_senderVO.getDailyTransferCount()==1){
			p2PSubscriberCounterVO.setDailyTransferCount(0);
        	p2PSubscriberCounterVO.setDailyTransferAmount(0);
			p2PSubscriberCounterVO.setPrevTransferDate(p_senderVO.getPrevTransferDate());
		}
		if(p_senderVO.getWeeklyTransferCount()==1){
			p2PSubscriberCounterVO.setWeeklyTransferCount(0);
        	p2PSubscriberCounterVO.setWeeklyTransferAmount(0);
			p2PSubscriberCounterVO.setPrevTransferWeekDate(p_senderVO.getPrevTransferWeekDate());
		}
		if(p_senderVO.getMonthlyTransferCount()==1){
			p2PSubscriberCounterVO.setMonthlyTransferCount(0);
        	p2PSubscriberCounterVO.setMonthlyTransferAmount(0);
			p2PSubscriberCounterVO.setPrevTransferMonthlyDate(p_senderVO.getPrevTransferMonthDate());
		}
		         
         
         int i=1;
         final StringBuffer sbf = new StringBuffer();
         sbf.append(" UPDATE p2p_subscribers_counters  SET daily_transfer_count=?,monthly_transfer_count=?, ");
         sbf.append("weekly_transfer_count=?,daily_transfer_amount=?,monthly_transfer_amount=?,weekly_transfer_amount=?,");
         sbf.append(" prev_daily_transfer_count=?,prev_monthly_transfer_count=?, prev_weekly_transfer_count=?,prev_daily_transfer_amount=?, ");
         sbf.append(" prev_monthly_transfer_amount=?,prev_weekly_transfer_amount=?, prev_transfer_date=?,prev_transfer_week_date=?,prev_transfer_month_date=?,Last_success_date=?,VPIN_INVALID_COUNT=? ");
         sbf.append(" where user_id =? AND service_type=? ");
        
         String updateQuery = sbf.toString();
         pstmtUpdate = p_con.prepareStatement(updateQuery);
         
         pstmtUpdate.setLong(i, p_senderVO.getDailyTransferCount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getMonthlyTransferCount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getWeeklyTransferCount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getDailyTransferAmount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getMonthlyTransferAmount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getWeeklyTransferAmount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getPrevDailyTransferCount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getPrevMonthlyTransferCount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getPrevWeeklyTransferCount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getPrevDailyTransferAmount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getPrevMonthlyTransferAmount());
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getPrevWeeklyTransferAmount());
         i++;
         pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferDate()));
         i++;
         pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferWeekDate()));
         i++;
         pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferMonthDate()));
         i++;
         pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getlastSuccessTransferService()));
         i++;
         pstmtUpdate.setLong(i, p_senderVO.getInvalidVoucherPinCount());
         i++;
         pstmtUpdate.setString(i, p_senderVO.getUserID());
         i++;
         pstmtUpdate.setString(i, servicetype);
         i++;
         
         updateCount = pstmtUpdate.executeUpdate();
		 
		 // initialize all other 
		if(p_senderVO.getDailyTransferCount()==1 && BTSLUtil.getDifferenceInUtilDates(previousDate , currentDate)!=0)
		{
			i=1;
			sbf.setLength(0);
			sbf.append(" UPDATE p2p_subscribers_counters  SET ");
			
		   
			if(p_senderVO.getDailyTransferCount()==1){
				sbf.append(" daily_transfer_count=?,daily_transfer_amount=?,prev_daily_transfer_count=daily_transfer_count,prev_daily_transfer_amount=daily_transfer_amount,prev_transfer_date=?");
			}
			if(p_senderVO.getWeeklyTransferCount()==1){
				sbf.append(" ,weekly_transfer_count=?,weekly_transfer_amount=?,prev_weekly_transfer_count=weekly_transfer_count,prev_weekly_transfer_amount=weekly_transfer_amount,prev_transfer_week_date=?");
			
			}
			if(p_senderVO.getMonthlyTransferCount()==1){
				sbf.append(" ,monthly_transfer_count=?,monthly_transfer_amount=?,prev_monthly_transfer_count=monthly_transfer_count,prev_monthly_transfer_amount=monthly_transfer_amount,prev_transfer_month_date=?");
			
			}
			sbf.append(" ,VPIN_INVALID_COUNT=? where user_id =? and service_type <> ? ");
			   
			updateQuery = sbf.toString();
			 if(_log.isDebugEnabled()){
	    			loggerValue.setLength(0);
	    			loggerValue.append(QUERY_KEY);
	    			loggerValue.append(updateQuery);
	    			_log.debug(methodName, loggerValue);
	    		}
			 pstmtUpdate2 = p_con.prepareStatement(updateQuery);
			
			if(p_senderVO.getDailyTransferCount()==1){
				pstmtUpdate2.setLong(i, p2PSubscriberCounterVO.getDailyTransferCount());
				i++;
				pstmtUpdate2.setLong(i, p2PSubscriberCounterVO.getDailyTransferAmount());
				i++;
				pstmtUpdate2.setDate(i,  BTSLUtil.getSQLDateFromUtilDate(p2PSubscriberCounterVO.getPrevTransferDate()));
				i++;
			}
			
			if(p_senderVO.getWeeklyTransferCount()==1){
				pstmtUpdate2.setLong(i, p2PSubscriberCounterVO.getWeeklyTransferCount());
				i++;
				pstmtUpdate2.setLong(i, p2PSubscriberCounterVO.getWeeklyTransferAmount());
				i++;
				pstmtUpdate2.setDate(i,  BTSLUtil.getSQLDateFromUtilDate(p2PSubscriberCounterVO.getPrevTransferWeekDate()));
				i++;
			}
			
			if(p_senderVO.getMonthlyTransferCount()==1){
				pstmtUpdate2.setLong(i, p2PSubscriberCounterVO.getMonthlyTransferCount());
				i++;
				pstmtUpdate2.setLong(i, p2PSubscriberCounterVO.getMonthlyTransferAmount());
				i++;
				pstmtUpdate2.setDate(i,  BTSLUtil.getSQLDateFromUtilDate(p2PSubscriberCounterVO.getPrevTransferMonthlyDate()));
				i++;
			}
			
			pstmtUpdate2.setLong(i, p_senderVO.getInvalidVoucherPinCount());
	         i++;
	         pstmtUpdate2.setString(i, p_senderVO.getUserID());
			i++;
			pstmtUpdate2.setString(i, servicetype);
			i++;
			
			pstmtUpdate2.executeUpdate();
			
			}
         }
          catch (SQLException sqle) {
        	  loggerValue.setLength(0);
  			loggerValue.append(SQL_EXCEPTION);
  			loggerValue.append(sqle.getMessage());
  			_log.error(methodName, loggerValue);
             _log.errorTrace(methodName, sqle);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "",
                 p_senderVO.getMsisdn(), "", loggerValue.toString());
             throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
         }// end of catch
         catch (Exception e) {
        	 loggerValue.setLength(0);
 			loggerValue.append(EXCEPTION);
 			loggerValue.append(e.getMessage());
 			_log.error(methodName, loggerValue);
             _log.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "",
                 p_senderVO.getMsisdn(), "", loggerValue.toString());
             throw new BTSLBaseException(this, methodName, "error.general.processing",e);
         }// end of catch
         finally {
             try {
                 if (pstmtUpdate != null) {
                     pstmtUpdate.close();
                 }
                 if(pstmtUpdate2 !=null) {
                	 pstmtUpdate2.close();
                 }
             } catch (Exception e) {
                 _log.errorTrace(methodName, e);
             }
             if (_log.isDebugEnabled()) {
              	loggerValue.setLength(0);
              	loggerValue.append("Exiting: updateCount:");
              	loggerValue.append(updateCount);
              	_log.debug(methodName, loggerValue);
              }
         }// end of finally
         return updateCount;
     }
    
    public int insertSubscriberCountersDetailsService(Connection p_con, SenderVO p_senderVO, String servicetype) throws BTSLBaseException {
    	
   	 final String methodName = "insertSubscriberCountersDetailsService";
   	StringBuilder loggerValue= new StringBuilder();
    if (_log.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered: p_senderVO=");
    	loggerValue.append(p_senderVO);
    	_log.debug(methodName, loggerValue);
    }
        PreparedStatement pstmtInsert = null;
        int insertCount=0;
        try{
        
        
        int i=1;
        final StringBuffer sbf = new StringBuffer();
        sbf.append("Insert into P2P_SUBSCRIBERS_COUNTERS(daily_transfer_count,monthly_transfer_count, ");
         sbf.append("weekly_transfer_count,daily_transfer_amount,monthly_transfer_amount,weekly_transfer_amount,");
         sbf.append(" prev_daily_transfer_count,prev_monthly_transfer_count, prev_weekly_transfer_count,prev_daily_transfer_amount, ");
         sbf.append(" prev_monthly_transfer_amount,prev_weekly_transfer_amount, prev_transfer_date,prev_transfer_week_date,prev_transfer_month_date,user_id,service_type,msisdn,last_success_date,VPIN_INVALID_COUNT )");
         sbf.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        String insertQuery = sbf.toString();
        pstmtInsert = p_con.prepareStatement(insertQuery);
        
        pstmtInsert.setLong(i, p_senderVO.getDailyTransferCount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getMonthlyTransferCount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getWeeklyTransferCount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getDailyTransferAmount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getMonthlyTransferAmount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getWeeklyTransferAmount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getPrevDailyTransferCount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getPrevMonthlyTransferCount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getPrevWeeklyTransferCount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getPrevDailyTransferAmount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getPrevMonthlyTransferAmount());
        i++;
        pstmtInsert.setLong(i, p_senderVO.getPrevWeeklyTransferAmount());
        i++;
        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferDate()));
        i++;
        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferWeekDate()));
        i++;
        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getPrevTransferMonthDate()));
        i++;
        pstmtInsert.setString(i, p_senderVO.getUserID());
        i++;
        pstmtInsert.setString(i, servicetype);
        i++;
        pstmtInsert.setString(i, p_senderVO.getMsisdn());
        i++;
        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_senderVO.getlastSuccessTransferService()));
        i++;
        pstmtInsert.setLong(i, p_senderVO.getInvalidVoucherPinCount());
        
        insertCount = pstmtInsert.executeUpdate();
        }
         catch (SQLException sqle) {
        	 loggerValue.setLength(0);
 			loggerValue.append(SQL_EXCEPTION);
 			loggerValue.append(sqle.getMessage());
 			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "",
                p_senderVO.getMsisdn(), "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[updateSubscriberCountersDetails]", "",
                p_senderVO.getMsisdn(), "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                	pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: insertCount:");
             	loggerValue.append(insertCount);
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
        return insertCount;
    }
   
   
     /**
	 * This method is used to load p2p subscriber user information for all services.
	 * @param p_con
	 * @param p_msisdn String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ArrayList loadSubscriberTransferCounterList(Connection p_con, String p_msisdn ) throws BTSLBaseException
	{
        final String methodName = "loadSubscriberDetailsByMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        P2PSubscriberCounterVO p2PSubscriberCounterVO = null;
        ResultSet rs = null;
		ArrayList transferList = new ArrayList();
        try {
            final StringBuffer selectQueryBuff = new StringBuffer(" select s.name service_name , psc.SERVICE_TYPE,DAILY_TRANSFER_COUNT,MONTHLY_TRANSFER_COUNT,WEEKLY_TRANSFER_COUNT,PREV_DAILY_TRANSFER_COUNT,PREV_WEEKLY_TRANSFER_COUNT,PREV_MONTHLY_TRANSFER_COUNT,");
            selectQueryBuff.append(" DAILY_TRANSFER_AMOUNT,WEEKLY_TRANSFER_AMOUNT,MONTHLY_TRANSFER_AMOUNT,PREV_DAILY_TRANSFER_AMOUNT,PREV_WEEKLY_TRANSFER_AMOUNT,PREV_MONTHLY_TRANSFER_AMOUNT,");
            selectQueryBuff.append("  PREV_TRANSFER_DATE,PREV_TRANSFER_WEEK_DATE,PREV_TRANSFER_MONTH_DATE from p2p_subscribers_counters psc,service_type s where msisdn=? and psc.service_type=s.service_type ");
            
            final String selectQuery = selectQueryBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msisdn);
         
            
            rs = pstmtSelect.executeQuery();
			p2PSubscriberCounterVO = new P2PSubscriberCounterVO();
            while (rs.next()) {
                p2PSubscriberCounterVO = new P2PSubscriberCounterVO();
				p2PSubscriberCounterVO.setServiceName(rs.getString("service_name"));
				p2PSubscriberCounterVO.setServiceType(rs.getString("service_type"));
                p2PSubscriberCounterVO.setDailyTransferCount(rs.getLong("daily_transfer_count"));
                p2PSubscriberCounterVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));
                p2PSubscriberCounterVO.setWeeklyTransferCount(rs.getLong("weekly_transfer_count"));
                p2PSubscriberCounterVO.setDailyTransferAmount(rs.getLong("daily_transfer_amount"));
                p2PSubscriberCounterVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                p2PSubscriberCounterVO.setWeeklyTransferAmount(rs.getLong("weekly_transfer_amount"));

                p2PSubscriberCounterVO.setPrevDailyTransferCount(rs.getLong("prev_daily_transfer_count"));
                p2PSubscriberCounterVO.setPrevMonthlyTransferCount(rs.getLong("prev_monthly_transfer_count"));
                p2PSubscriberCounterVO.setPrevWeeklyTransferCount(rs.getLong("prev_weekly_transfer_count"));
                p2PSubscriberCounterVO.setPrevDailyTransferAmount(rs.getLong("prev_daily_transfer_amount"));
                p2PSubscriberCounterVO.setPrevMonthlyTransferAmount(rs.getLong("prev_monthly_transfer_amount"));
                p2PSubscriberCounterVO.setPrevWeeklyTransferAmount(rs.getLong("prev_weekly_transfer_amount"));
                p2PSubscriberCounterVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("PREV_TRANSFER_DATE")));
                p2PSubscriberCounterVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("PREV_TRANSFER_WEEK_DATE")));
				p2PSubscriberCounterVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("PREV_TRANSFER_MONTH_DATE")));
                
				transferList.add(p2PSubscriberCounterVO);
                
            }
            return transferList;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByMsisdn]", "",
                p_msisdn, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubscriberDetailsByMsisdn]", "",
                p_msisdn, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: transferList:");
             	loggerValue.append(transferList.size());
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
    }

	
	
	public P2PSubscriberCounterVO loadP2PsubscriberCounterBasedOnService(Connection p_con, String p_msisdn,String serviceType) throws BTSLBaseException{

        final String methodName = "loadP2PsubscriberCounterBasedOnService";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append("Entered: ServiceType=");
        	loggerValue.append(serviceType);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        P2PSubscriberCounterVO p2PSubscriberCounterVO = null;
        ResultSet rs = null;
		ArrayList transferList = new ArrayList();
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" select s.name service_name , psc.SERVICE_TYPE,DAILY_TRANSFER_COUNT,MONTHLY_TRANSFER_COUNT,WEEKLY_TRANSFER_COUNT,PREV_DAILY_TRANSFER_COUNT,PREV_WEEKLY_TRANSFER_COUNT,PREV_MONTHLY_TRANSFER_COUNT,");
            selectQueryBuff.append(" DAILY_TRANSFER_AMOUNT,WEEKLY_TRANSFER_AMOUNT,MONTHLY_TRANSFER_AMOUNT,PREV_DAILY_TRANSFER_AMOUNT,PREV_WEEKLY_TRANSFER_AMOUNT,PREV_MONTHLY_TRANSFER_AMOUNT,");
            selectQueryBuff.append("  PREV_TRANSFER_DATE,PREV_TRANSFER_WEEK_DATE,PREV_TRANSFER_MONTH_DATE,VPIN_INVALID_COUNT from p2p_subscribers_counters psc,service_type s where msisdn=? and psc.service_type=s.service_type and psc.service_type = ?  ");
            
            final String selectQuery = selectQueryBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msisdn);
            pstmtSelect.setString(2, serviceType);
         
            
            rs = pstmtSelect.executeQuery();
			
            while (rs.next()) {
            	
                p2PSubscriberCounterVO = new P2PSubscriberCounterVO();
				p2PSubscriberCounterVO.setServiceName(rs.getString("service_name"));
				p2PSubscriberCounterVO.setServiceType(rs.getString("service_type"));
                p2PSubscriberCounterVO.setDailyTransferCount(rs.getLong("daily_transfer_count"));
                p2PSubscriberCounterVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));
                p2PSubscriberCounterVO.setWeeklyTransferCount(rs.getLong("weekly_transfer_count"));
                p2PSubscriberCounterVO.setDailyTransferAmount(rs.getLong("daily_transfer_amount"));
                p2PSubscriberCounterVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                p2PSubscriberCounterVO.setWeeklyTransferAmount(rs.getLong("weekly_transfer_amount"));

                p2PSubscriberCounterVO.setPrevDailyTransferCount(rs.getLong("prev_daily_transfer_count"));
                p2PSubscriberCounterVO.setPrevMonthlyTransferCount(rs.getLong("prev_monthly_transfer_count"));
                p2PSubscriberCounterVO.setPrevWeeklyTransferCount(rs.getLong("prev_weekly_transfer_count"));
                p2PSubscriberCounterVO.setPrevDailyTransferAmount(rs.getLong("prev_daily_transfer_amount"));
                p2PSubscriberCounterVO.setPrevMonthlyTransferAmount(rs.getLong("prev_monthly_transfer_amount"));
                p2PSubscriberCounterVO.setPrevWeeklyTransferAmount(rs.getLong("prev_weekly_transfer_amount"));
                p2PSubscriberCounterVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("PREV_TRANSFER_DATE")));
                p2PSubscriberCounterVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("PREV_TRANSFER_WEEK_DATE")));
				p2PSubscriberCounterVO.setPrevTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("PREV_TRANSFER_MONTH_DATE")));
				p2PSubscriberCounterVO.setInvalidVoucherPinCount(rs.getLong("VPIN_INVALID_COUNT"));
				
                
            }
            return p2PSubscriberCounterVO;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadP2PsubscriberCounterBasedOnService]", "",
                p_msisdn, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadP2PsubscriberCounterBasedOnService]", "",
                p_msisdn, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: transferList:");
             	loggerValue.append(transferList.size());
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
    
	}
	
	public boolean isMSISDNExist(Connection p_con, String p_filteredMsisdn, String p_subscriberType) throws BTSLBaseException
	{

		if (_log.isDebugEnabled())
			_log.debug("isMSISDNExist", "Entered p_filteredMsisdn:" + p_filteredMsisdn + "  subscriberType  " + p_subscriberType);
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		boolean exist = false;
		try
		{
			String selectQuery = "SELECT msisdn FROM p2p_subscribers WHERE msisdn=? AND subscriber_type = ? ";
			if (_log.isDebugEnabled())
				_log.debug("isMSISDNExist", "select query:" + selectQuery);
			pstmtSelect = p_con.prepareStatement(selectQuery);
			pstmtSelect.setString(1, p_filteredMsisdn);
			pstmtSelect.setString(2, p_subscriberType);
			rs = pstmtSelect.executeQuery();
			if (rs.next())
			{
				exist = true;
			}
		}// end of try
		catch (SQLException sqle)
		{
			_log.error("isMSISDNExist", "SQLException " + sqle.getMessage());
			_log.errorTrace("isMSISDNExist", sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[isMSISDNExist]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "isMSISDNExist", "error.general.sql.processing");
		}// end of catch
		catch (Exception e)
		{
			_log.error("isMSISDNExist", "Exception " + e.getMessage());
			_log.errorTrace("isMSISDNExist", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberDAO[isMSISDNExist]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "isMSISDNExist", "error.general.processing");
		}// end of catch
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
			} catch (Exception e)
			{
			}
			try
			{
				if (pstmtSelect != null)
					pstmtSelect.close();
			} catch (Exception e)
			{
			}
			if (_log.isDebugEnabled())
				_log.debug("isMSISDNExist", "Exiting  MSISIDN EXIST: " + exist);
		}// end of finally
		return exist;
	}


}
