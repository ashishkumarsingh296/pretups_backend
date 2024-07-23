package com.selftopup.cp2p.registration.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.subscriber.businesslogic.SubscriberVO;
import com.selftopup.util.BTSLUtil;

public class CP2PRegistrationDAO {
    private Log _log = LogFactory.getFactory().getInstance(CP2PRegistrationDAO.class.getName());

    /*
     * Method to check whether Mobile Number provided for registration is
     * already registered By SMS or not.
     */

    public boolean isSubscriberMobileNumberExist(Connection p_con, SubscriberVO p_senderVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("isSubscriberMobileNumberExist", "Entered: p_senderVO=" + p_senderVO);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT msisdn,pin FROM p2p_subscribers WHERE msisdn = ? and STATUS = 'Y'");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("isSubscriberMobileNumberExist", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_senderVO.getMsisdn());

            rs = pstmt.executeQuery();
            if (rs.next()) {
                p_senderVO.setPin(BTSLUtil.decryptText(rs.getString("pin")));
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error("isSubscriberMobileNumberExist", "SQLException : " + sqe);
            _log.errorTrace("isSubscriberMobileNumberExist: Exception print stack trace: ", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isUserLoginExist", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isSubscriberMobileNumberExist", "Exception : " + ex);
            _log.errorTrace("isSubscriberMobileNumberExist: Exception print stack trace: ", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isSubscriberMobileNumberExist", "error.general.processing");
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
                _log.debug("isSubscriberMobileNumberExist", "Exiting: existFlag=" + existFlag);
            }
        }
    }

    public boolean isSubscriebrLoginIdExist(Connection p_con, String p_loginId) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("isLoginIdExist", "Entered: p_loginId=" + p_loginId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT login_id FROM p2p_subscribers WHERE login_id = ? and status = 'Y'");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("isLoginIdExist", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_loginId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error("isLoginIdExist", "SQLException : " + sqe);
            _log.errorTrace("isSubscriebrLoginIdExist: Exception print stack trace: ", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isUserLoginExist", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isLoginIdExist", "Exception : " + ex);
            _log.errorTrace("isSubscriebrLoginIdExist: Exception print stack trace: ", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isLoginIdExist", "error.general.processing");
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
                _log.debug("isLoginIdExist", "Exiting: existFlag=" + existFlag);
            }
        }
    }

    public int addSubscriber(Connection p_con, String p_msisdn, String p_loginID, String p_subscriberPassword, CP2PSubscriberVO cp2pSubscriberVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addSubscriber", "Entered: p_msisdn= " + p_msisdn + " p_loginID=" + p_loginID);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;

        try {
            String queryUpdate = "UPDATE p2p_subscribers SET login_id=?,password=? WHERE msisdn = ? and status ='Y'";
            pstmtUpdate = p_con.prepareStatement(queryUpdate);
            pstmtUpdate.setString(1, p_loginID);
            pstmtUpdate.setString(2, p_subscriberPassword);
            pstmtUpdate.setString(3, p_msisdn);

            updateCount = pstmtUpdate.executeUpdate();
            // remove the push message from here and added in Action class.

        } catch (SQLException sqle) {
            _log.error("addSubscriber", "SQLException: " + sqle.getMessage());
            _log.errorTrace("addSubscriber: Exception print stack trace: ", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[addSubscriber]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addSubscriber", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("addSubscriber", "Exception: " + e.getMessage());
            _log.errorTrace("addSubscriber: Exception print stack trace: ", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[changePassword]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSubscriber", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addSubscriber()", " Exiting with updateCount=" + updateCount);
        }
        return updateCount;
    }

    /**
     * Method to load the user details for login info
     * 
     * @param p_con
     * @param p_loginID
     * @param p_password
     * @param localeLanguage
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public CP2PSubscriberVO loadCP2PSubscriberDetails(java.sql.Connection p_con, String p_msisdn) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadCP2PSubscriberDetails", "loadInterfaceTypeId():: Entered with p_msisdn:" + p_msisdn);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sqlBuffer = new StringBuffer(" SELECT p2ps.ACTIVATED_ON, p2ps.BILLING_CYCLE_DATE, p2ps.BILLING_TYPE, p2ps.BUDDY_SEQ_NUMBER, p2ps.CONSECUTIVE_FAILURES, l.network_name,l.report_header_name,");
        sqlBuffer.append(" p2ps.COUNTRY, p2ps.CREATED_BY, p2ps.CREATED_ON, p2ps.CREDIT_LIMIT, p2ps.DAILY_TRANSFER_AMOUNT, p2ps.DAILY_TRANSFER_COUNT,p2ps.pswd_reset, ");
        sqlBuffer.append(" p2ps.FIRST_INVALID_PIN_TIME, p2ps.INVALID_PASSWORD_COUNT, p2ps.LANGUAGE, p2ps.LAST_LOGIN_ON, p2ps.LAST_SUCCESS_TRANSFER_DATE, ");
        sqlBuffer.append(" p2ps.LAST_TRANSFER_AMOUNT, p2ps.LAST_TRANSFER_ID, p2ps.LAST_TRANSFER_MSISDN, p2ps.LAST_TRANSFER_ON, p2ps.LAST_TRANSFER_STATUS, ");
        sqlBuffer.append(" p2ps.LAST_TRANSFER_TYPE, p2ps.LOGIN_ID, p2ps.MODIFIED_BY, p2ps.MODIFIED_ON, p2ps.MONTHLY_TRANSFER_AMOUNT, p2ps.MONTHLY_TRANSFER_COUNT, ");
        sqlBuffer.append(" p2ps.MSISDN, p2ps.NETWORK_CODE, p2ps.PASSWORD, p2ps.PIN, p2ps.PIN_BLOCK_COUNT, p2ps.PIN_MODIFIED_ON, p2ps.PREFIX_ID, l.status networkstatus,l.language_1_message,l.language_2_message,  ");
        sqlBuffer.append(" p2ps.PREV_DAILY_TRANSFER_AMOUNT, p2ps.PREV_DAILY_TRANSFER_COUNT, p2ps.PREV_MONTHLY_TRANSFER_AMOUNT,");
        sqlBuffer.append(" p2ps.PREV_MONTHLY_TRANSFER_COUNT, p2ps.PREV_TRANSFER_DATE, p2ps.PREV_TRANSFER_MONTH_DATE, p2ps.PREV_TRANSFER_WEEK_DATE, ");
        sqlBuffer.append(" p2ps.PREV_WEEKLY_TRANSFER_AMOUNT, p2ps.PREV_WEEKLY_TRANSFER_COUNT, p2ps.PSWD_MODIFIED_ON, p2ps.REGISTERED_ON, p2ps.REQUEST_STATUS,");
        sqlBuffer.append(" p2ps.SERVICE_CLASS_CODE, p2ps.SERVICE_CLASS_ID, p2ps.SKEY_REQUIRED, p2ps.STATUS, p2ps.SUBSCRIBER_TYPE, p2ps.TOTAL_TRANSFER_AMOUNT, ");
        sqlBuffer.append(" p2ps.TOTAL_TRANSFERS, p2ps.USER_ID, p2ps.USER_NAME, p2ps.WEEKLY_TRANSFER_AMOUNT, p2ps.WEEKLY_TRANSFER_COUNT,p2ps.PASSWORD_COUNT_UPDATED_ON");
        sqlBuffer.append(" FROM  P2P_SUBSCRIBERS p2ps, NETWORKS l");
        sqlBuffer.append(" WHERE  msisdn=?");
        sqlBuffer.append(" AND p2ps.status <> ? AND p2ps.status <> ? AND p2ps.network_code=L.network_code(+) ");
        CP2PSubscriberVO cp2pSubscriberVO = null;
        try {
            if (_log.isDebugEnabled())
                _log.debug("loadCP2PSubscriberDetails", " Query : " + sqlBuffer.toString());
            pstmt = p_con.prepareStatement(sqlBuffer.toString());
            int i = 0;
            pstmt.setString(++i, p_msisdn);
            // pstmt.setString(++i,BTSLUtil.encryptText(p_password));
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);

            rs = pstmt.executeQuery();
            String userID;
            if (rs.next()) {
                cp2pSubscriberVO = new CP2PSubscriberVO();
                userID = rs.getString("user_id");
                cp2pSubscriberVO.setUserId(userID);
                cp2pSubscriberVO.setUserName(rs.getString("user_name"));
                cp2pSubscriberVO.setPasswordReset(rs.getString("pswd_reset"));
                cp2pSubscriberVO.setNetworkCode("network_code");
                cp2pSubscriberVO.setNetworkName(rs.getString("network_name"));
                cp2pSubscriberVO.setReportHeaderName(rs.getString("report_header_name"));
                cp2pSubscriberVO.setNetworkStatus(rs.getString("networkstatus"));
                cp2pSubscriberVO.setLoginId(rs.getString("login_id"));
                cp2pSubscriberVO.setPassword(rs.getString("password"));
                cp2pSubscriberVO.setPassword(BTSLUtil.decryptText(rs.getString("pin")));
                cp2pSubscriberVO.setCategory("CP2P");
                cp2pSubscriberVO.setDomainId("CP2P");
                if (rs.getTimestamp("activated_on") != null)
                    cp2pSubscriberVO.setActivatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("activated_on")));
                cp2pSubscriberVO.setBillingCycleDate(rs.getString("billing_cycle_date"));
                cp2pSubscriberVO.setBillingType(rs.getString("billing_type"));
                cp2pSubscriberVO.setBuddySeqNumber(rs.getString("buddy_seq_number"));
                cp2pSubscriberVO.setConsecutiveFailures(rs.getString("consecutive_failures"));
                cp2pSubscriberVO.setCountry(rs.getString("country"));
                cp2pSubscriberVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("created_on") != null)
                    cp2pSubscriberVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                if (rs.getTimestamp("last_login_on") != null)
                    cp2pSubscriberVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                cp2pSubscriberVO.setCreditLimit(rs.getString("credit_limit"));
                cp2pSubscriberVO.setDailyTxrAmount(rs.getString("daily_transfer_amount"));
                cp2pSubscriberVO.setDailyTxrCount(rs.getString("daily_transfer_count"));
                if (rs.getTimestamp("first_invalid_pin_time") != null)
                    cp2pSubscriberVO.setFirstInvalidPinTime(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("first_invalid_pin_time")));
                cp2pSubscriberVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
                if (rs.getTimestamp("last_success_transfer_date") != null)
                    cp2pSubscriberVO.setLastSuccessTransferDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_success_transfer_date")));
                cp2pSubscriberVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                cp2pSubscriberVO.setLastTransferID(rs.getString("last_transfer_id"));
                cp2pSubscriberVO.setLastTransferMSISDN(rs.getString("last_transfer_msisdn"));
                if (rs.getTimestamp("last_transfer_on") != null)
                    cp2pSubscriberVO.setLastTransferOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_transfer_on")));
                cp2pSubscriberVO.setLastTransferStatus(rs.getString("last_transfer_status"));
                cp2pSubscriberVO.setLastTransferType(rs.getString("last_transfer_type"));
                cp2pSubscriberVO.setModifiedBy(rs.getString("modified_by"));
                if (rs.getTimestamp("modified_on") != null)
                    cp2pSubscriberVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("modified_on")));
                cp2pSubscriberVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                cp2pSubscriberVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));

                cp2pSubscriberVO.setMsisdn(rs.getString("msisdn"));
                cp2pSubscriberVO.setPinBlockCount(rs.getString("pin_block_count"));
                if (rs.getTimestamp("pin_modified_on") != null)
                    cp2pSubscriberVO.setPinModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pin_modified_on")));
                cp2pSubscriberVO.setPrefixID(rs.getLong("prefix_id"));
                cp2pSubscriberVO.setPreviousDailyTxrAmount(rs.getLong("prev_daily_transfer_amount"));
                cp2pSubscriberVO.setPreviousDailyTxrCount(rs.getLong("prev_daily_transfer_count"));
                cp2pSubscriberVO.setPreviousMonthlyTxrAmount(rs.getLong("prev_monthly_transfer_amount"));
                cp2pSubscriberVO.setPreviousMonthlyTxrCount(rs.getLong("prev_monthly_transfer_count"));
                if (rs.getTimestamp("prev_transfer_date") != null)
                    cp2pSubscriberVO.setPreviousTxrDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("prev_transfer_date")));

                if (rs.getTimestamp("prev_transfer_month_date") != null)
                    cp2pSubscriberVO.setPreviousTxrMonthDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("prev_transfer_month_date")));
                if (rs.getTimestamp("prev_transfer_week_date") != null)
                    cp2pSubscriberVO.setPreviousTxrWeekDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("prev_transfer_week_date")));

                cp2pSubscriberVO.setPreviousMonthlyTxrAmount(rs.getLong("prev_monthly_transfer_amount"));
                cp2pSubscriberVO.setPreviousWeeklyTxrAmount(rs.getLong("prev_weekly_transfer_amount"));

                cp2pSubscriberVO.setLanguage(rs.getString("language"));
                if (!BTSLUtil.isNullString(rs.getString("language_1_message"))) {
                    cp2pSubscriberVO.setMessage(rs.getString("language_1_message"));
                } else {
                    cp2pSubscriberVO.setMessage(rs.getString("language_2_message"));
                }

                if (rs.getTimestamp("registered_on") != null)
                    cp2pSubscriberVO.setRegisteredOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("registered_on")));

                cp2pSubscriberVO.setRequestStatus(rs.getString("request_status"));
                cp2pSubscriberVO.setServiceClassCode(rs.getString("service_class_code"));
                cp2pSubscriberVO.setServiceClassId(rs.getString("service_class_id"));
                cp2pSubscriberVO.setStatus(rs.getString("status"));
                cp2pSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                cp2pSubscriberVO.setTotalTransferAmount(rs.getLong("total_transfer_amount"));
                cp2pSubscriberVO.setTotalTransfers(rs.getLong("total_transfers"));
                cp2pSubscriberVO.setWeeklyTxrAmount(rs.getLong("weekly_transfer_amount"));
                cp2pSubscriberVO.setWeeklyTxrCount(rs.getLong("weekly_transfer_count"));

                if (rs.getTimestamp("password_count_updated_on") != null)
                    cp2pSubscriberVO.setPswdCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));

                try {
                    cp2pSubscriberVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    cp2pSubscriberVO.setPasswordModifiedOn(null);
                }

            }
        }

        catch (SQLException sqe) {
            _log.error("loadUserDetails", " SQLException : " + sqe);
            _log.errorTrace("loadCP2PSubscriberDetails: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserDetails", " Exception : " + ex);
            _log.errorTrace("loadCP2PSubscriberDetails: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.error("loadUserDetails::", " Exception : in closing resultset" + ex);
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (Exception ex) {
                _log.error("loadUserDetails ::", " Exception : in closing preparedstatement" + ex);
            }
        }
        _log.debug("loadUserDetails ::", " Exiting channelUserVO=" + cp2pSubscriberVO);
        return cp2pSubscriberVO;
    }

    /**
     * Method to load the user details for login info
     * 
     * @param p_con
     * @param p_loginID
     * @param p_password
     * @param localeLanguage
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public CP2PSubscriberVO loadCP2PSubscriberDetails(java.sql.Connection p_con, String p_msisdn, String p_loginId) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadCP2PSubscriberDetails", "loadInterfaceTypeId():: Entered with p_msisdn:" + p_msisdn + "p_loginId" + p_loginId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sqlBuffer = new StringBuffer(" SELECT p2ps.ACTIVATED_ON, p2ps.BILLING_CYCLE_DATE, p2ps.BILLING_TYPE, p2ps.BUDDY_SEQ_NUMBER, p2ps.CONSECUTIVE_FAILURES, l.network_name,l.report_header_name,");
        sqlBuffer.append(" p2ps.COUNTRY, p2ps.CREATED_BY, p2ps.CREATED_ON, p2ps.CREDIT_LIMIT, p2ps.DAILY_TRANSFER_AMOUNT, p2ps.DAILY_TRANSFER_COUNT,p2ps.pswd_reset, ");
        sqlBuffer.append(" p2ps.FIRST_INVALID_PIN_TIME, p2ps.INVALID_PASSWORD_COUNT, p2ps.LANGUAGE, p2ps.LAST_LOGIN_ON, p2ps.LAST_SUCCESS_TRANSFER_DATE, ");
        sqlBuffer.append(" p2ps.LAST_TRANSFER_AMOUNT, p2ps.LAST_TRANSFER_ID, p2ps.LAST_TRANSFER_MSISDN, p2ps.LAST_TRANSFER_ON, p2ps.LAST_TRANSFER_STATUS, ");
        sqlBuffer.append(" p2ps.LAST_TRANSFER_TYPE, p2ps.LOGIN_ID, p2ps.MODIFIED_BY, p2ps.MODIFIED_ON, p2ps.MONTHLY_TRANSFER_AMOUNT, p2ps.MONTHLY_TRANSFER_COUNT, ");
        sqlBuffer.append(" p2ps.MSISDN, p2ps.NETWORK_CODE, p2ps.PASSWORD, p2ps.PIN, p2ps.PIN_BLOCK_COUNT, p2ps.PIN_MODIFIED_ON, p2ps.PREFIX_ID, l.status networkstatus,l.language_1_message,l.language_2_message,  ");
        sqlBuffer.append(" p2ps.PREV_DAILY_TRANSFER_AMOUNT, p2ps.PREV_DAILY_TRANSFER_COUNT, p2ps.PREV_MONTHLY_TRANSFER_AMOUNT,");
        sqlBuffer.append(" p2ps.PREV_MONTHLY_TRANSFER_COUNT, p2ps.PREV_TRANSFER_DATE, p2ps.PREV_TRANSFER_MONTH_DATE, p2ps.PREV_TRANSFER_WEEK_DATE, ");
        sqlBuffer.append(" p2ps.PREV_WEEKLY_TRANSFER_AMOUNT, p2ps.PREV_WEEKLY_TRANSFER_COUNT, p2ps.PSWD_MODIFIED_ON, p2ps.REGISTERED_ON, p2ps.REQUEST_STATUS,");
        sqlBuffer.append(" p2ps.SERVICE_CLASS_CODE, p2ps.SERVICE_CLASS_ID, p2ps.SKEY_REQUIRED, p2ps.STATUS, p2ps.SUBSCRIBER_TYPE, p2ps.TOTAL_TRANSFER_AMOUNT, ");
        sqlBuffer.append(" p2ps.TOTAL_TRANSFERS, p2ps.USER_ID, p2ps.USER_NAME, p2ps.WEEKLY_TRANSFER_AMOUNT, p2ps.WEEKLY_TRANSFER_COUNT,p2ps.PASSWORD_COUNT_UPDATED_ON");
        sqlBuffer.append(" FROM  P2P_SUBSCRIBERS p2ps, NETWORKS l");
        sqlBuffer.append(" WHERE ( p2ps.msisdn=? or p2ps.login_id=? )");
        sqlBuffer.append(" AND p2ps.status <> ? AND p2ps.status <> ? AND p2ps.network_code=L.network_code(+) ");
        CP2PSubscriberVO cp2pSubscriberVO = null;
        try {
            if (_log.isDebugEnabled())
                _log.debug("loadCP2PSubscriberDetails", " Query : " + sqlBuffer.toString());
            pstmt = p_con.prepareStatement(sqlBuffer.toString());
            int i = 0;
            pstmt.setString(++i, p_msisdn);
            pstmt.setString(++i, p_loginId);
            // pstmt.setString(++i,BTSLUtil.encryptText(p_password));
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);

            rs = pstmt.executeQuery();
            String userID;
            if (rs.next()) {
                cp2pSubscriberVO = new CP2PSubscriberVO();
                userID = rs.getString("user_id");
                cp2pSubscriberVO.setUserId(userID);
                cp2pSubscriberVO.setUserName(rs.getString("user_name"));
                cp2pSubscriberVO.setPasswordReset(rs.getString("pswd_reset"));
                cp2pSubscriberVO.setNetworkCode("network_code");
                cp2pSubscriberVO.setNetworkName(rs.getString("network_name"));
                cp2pSubscriberVO.setReportHeaderName(rs.getString("report_header_name"));
                cp2pSubscriberVO.setNetworkStatus(rs.getString("networkstatus"));
                cp2pSubscriberVO.setLoginId(rs.getString("login_id"));
                cp2pSubscriberVO.setPassword(rs.getString("password"));
                cp2pSubscriberVO.setCategory("CP2P");
                cp2pSubscriberVO.setDomainId("CP2P");
                if (rs.getTimestamp("activated_on") != null)
                    cp2pSubscriberVO.setActivatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("activated_on")));
                cp2pSubscriberVO.setBillingCycleDate(rs.getString("billing_cycle_date"));
                cp2pSubscriberVO.setBillingType(rs.getString("billing_type"));
                cp2pSubscriberVO.setBuddySeqNumber(rs.getString("buddy_seq_number"));
                cp2pSubscriberVO.setConsecutiveFailures(rs.getString("consecutive_failures"));
                cp2pSubscriberVO.setCountry(rs.getString("country"));
                cp2pSubscriberVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("created_on") != null)
                    cp2pSubscriberVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                if (rs.getTimestamp("last_login_on") != null)
                    cp2pSubscriberVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                cp2pSubscriberVO.setCreditLimit(rs.getString("credit_limit"));
                cp2pSubscriberVO.setDailyTxrAmount(rs.getString("daily_transfer_amount"));
                cp2pSubscriberVO.setDailyTxrCount(rs.getString("daily_transfer_count"));
                if (rs.getTimestamp("first_invalid_pin_time") != null)
                    cp2pSubscriberVO.setFirstInvalidPinTime(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("first_invalid_pin_time")));
                cp2pSubscriberVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
                if (rs.getTimestamp("last_success_transfer_date") != null)
                    cp2pSubscriberVO.setLastSuccessTransferDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_success_transfer_date")));
                cp2pSubscriberVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                cp2pSubscriberVO.setLastTransferID(rs.getString("last_transfer_id"));
                cp2pSubscriberVO.setLastTransferMSISDN(rs.getString("last_transfer_msisdn"));
                if (rs.getTimestamp("last_transfer_on") != null)
                    cp2pSubscriberVO.setLastTransferOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_transfer_on")));
                cp2pSubscriberVO.setLastTransferStatus(rs.getString("last_transfer_status"));
                cp2pSubscriberVO.setLastTransferType(rs.getString("last_transfer_type"));
                cp2pSubscriberVO.setModifiedBy(rs.getString("modified_by"));
                if (rs.getTimestamp("modified_on") != null)
                    cp2pSubscriberVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("modified_on")));
                cp2pSubscriberVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                cp2pSubscriberVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));

                cp2pSubscriberVO.setMsisdn(rs.getString("msisdn"));
                cp2pSubscriberVO.setPinBlockCount(rs.getString("pin_block_count"));
                if (rs.getTimestamp("pin_modified_on") != null)
                    cp2pSubscriberVO.setPinModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pin_modified_on")));
                cp2pSubscriberVO.setPrefixID(rs.getLong("prefix_id"));
                cp2pSubscriberVO.setPreviousDailyTxrAmount(rs.getLong("prev_daily_transfer_amount"));
                cp2pSubscriberVO.setPreviousDailyTxrCount(rs.getLong("prev_daily_transfer_count"));
                cp2pSubscriberVO.setPreviousMonthlyTxrAmount(rs.getLong("prev_monthly_transfer_amount"));
                cp2pSubscriberVO.setPreviousMonthlyTxrCount(rs.getLong("prev_monthly_transfer_count"));
                if (rs.getTimestamp("prev_transfer_date") != null)
                    cp2pSubscriberVO.setPreviousTxrDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("prev_transfer_date")));

                if (rs.getTimestamp("prev_transfer_month_date") != null)
                    cp2pSubscriberVO.setPreviousTxrMonthDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("prev_transfer_month_date")));
                if (rs.getTimestamp("prev_transfer_week_date") != null)
                    cp2pSubscriberVO.setPreviousTxrWeekDate(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("prev_transfer_week_date")));

                cp2pSubscriberVO.setPreviousMonthlyTxrAmount(rs.getLong("prev_monthly_transfer_amount"));
                cp2pSubscriberVO.setPreviousWeeklyTxrAmount(rs.getLong("prev_weekly_transfer_amount"));

                cp2pSubscriberVO.setLanguage(rs.getString("language"));
                if (!BTSLUtil.isNullString(rs.getString("language_1_message"))) {
                    cp2pSubscriberVO.setMessage(rs.getString("language_1_message"));
                } else {
                    cp2pSubscriberVO.setMessage(rs.getString("language_2_message"));
                }

                if (rs.getTimestamp("registered_on") != null)
                    cp2pSubscriberVO.setRegisteredOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("registered_on")));

                cp2pSubscriberVO.setRequestStatus(rs.getString("request_status"));
                cp2pSubscriberVO.setServiceClassCode(rs.getString("service_class_code"));
                cp2pSubscriberVO.setServiceClassId(rs.getString("service_class_id"));
                cp2pSubscriberVO.setStatus(rs.getString("status"));
                cp2pSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                cp2pSubscriberVO.setTotalTransferAmount(rs.getLong("total_transfer_amount"));
                cp2pSubscriberVO.setTotalTransfers(rs.getLong("total_transfers"));
                cp2pSubscriberVO.setWeeklyTxrAmount(rs.getLong("weekly_transfer_amount"));
                cp2pSubscriberVO.setWeeklyTxrCount(rs.getLong("weekly_transfer_count"));

                if (rs.getTimestamp("password_count_updated_on") != null)
                    cp2pSubscriberVO.setPswdCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));

                try {
                    cp2pSubscriberVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    cp2pSubscriberVO.setPasswordModifiedOn(null);
                }

            }
        }

        catch (SQLException sqe) {
            _log.error("loadUserDetails", " SQLException : " + sqe);
            _log.errorTrace("CP2PSubscriberVO: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserDetails", " Exception : " + ex);
            _log.errorTrace("CP2PSubscriberVO: Exception print stack trace:e=", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.error("loadUserDetails::", " Exception : in closing resultset" + ex);
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (Exception ex) {
                _log.error("loadUserDetails ::", " Exception : in closing preparedstatement" + ex);
            }
        }
        _log.debug("loadUserDetails ::", " Exiting channelUserVO=" + cp2pSubscriberVO);
        return cp2pSubscriberVO;
    }

    /**
     * Date : May 11, 2007
     * Discription :
     * Method : updateUserLoginDetails
     * 
     * @param p_con
     * @param p_userVO
     * @throws SQLException
     * @throws Exception
     * @return int
     * @author
     */
    public int updateSubscriberPasswordDetails(java.sql.Connection p_con, String p_password, Date p_pswdModifiedOn, String p_msisdn, String p_loginID, CP2PSubscriberVO cp2pSubscriberVO) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberPasswordDetails() ::", " Entered..............");
        PreparedStatement pstmtU = null;
        int count = 0;
        String[] argsArr = null;
        try {
            String updateUsers = "UPDATE p2p_subscribers SET password=?,pswd_modified_on=?,modified_on= ?, modified_by= ?, PSWD_RESET=?,INVALID_PASSWORD_COUNT=? WHERE msisdn = ? and login_id=? ";
            if (_log.isDebugEnabled())
                _log.info("updateUserLoginDetails ::", " Query updateUsers : " + updateUsers);
            pstmtU = p_con.prepareStatement(updateUsers);
            pstmtU.setString(1, p_password);
            pstmtU.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_pswdModifiedOn));
            pstmtU.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_pswdModifiedOn));
            pstmtU.setString(4, cp2pSubscriberVO.getUserId());
            pstmtU.setString(5, "Y");
            pstmtU.setInt(6, 0);
            pstmtU.setString(7, p_msisdn);
            pstmtU.setString(8, p_loginID);

            count = pstmtU.executeUpdate();
            // remove the push message from here and added in Action class.
            if (_log.isDebugEnabled())
                _log.debug("updateUserLoginDetails() ::", " update last_login_on count=" + count + "     for user id=" + cp2pSubscriberVO.getUserId());
        } catch (SQLException sqe) {
            _log.error("updateUserLoginDetails() ::", " Exception : " + sqe);
            _log.errorTrace("updateSubscriberPasswordDetails: Exception print stack trace:e=", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updateUserLoginDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberPasswordDetails", "error.general.processing");
        } catch (Exception ex) {
            _log.error("updateUserLoginDetails() ::", " Exception : " + ex);
            _log.errorTrace("updateSubscriberPasswordDetails: Exception print stack trace:e=", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updateUserLoginDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateUserLoginDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtU != null)
                    pstmtU.close();
            } catch (Exception ex) {
                _log.error("updateSubscriberPasswordDetails() ::", " Exception : in closing preparedstatement for Update" + ex);
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("updateUserLoginDetails() ::", " Exiting count=" + count);
        return count;
    }

    public boolean isSubscriberBarred(Connection p_con, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("isSubscriberBarred", "Entered: p_loginId=" + p_msisdn);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT msisdn FROM barred_msisdns WHERE msisdn = ? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("isSubscriberBarred", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_msisdn);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error("isSubscriberBarred", "SQLException : " + sqe);
            _log.errorTrace("isSubscriberBarred: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberBarred]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isSubscriberBarred", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isLoginIdExist", "Exception : " + ex);
            _log.errorTrace("isSubscriberBarred: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberBarred]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isSubscriberBarred", "error.general.processing");
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
                _log.debug("isSubscriberBarred", "Exiting: existFlag=" + existFlag);
            }
        }
    }

}
