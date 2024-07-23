package com.selftopup.cp2p.login.businesslogic;

/**
 * @(#)LoginDAO.java
 *                   Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                   All Rights Reserved
 *                   Data access object class for interaction with backend
 *                   tables
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Gurjeet Singh Bedi 24/06/2005 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.domain.businesslogic.CategoryVO;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterVO;
import com.selftopup.pretups.user.businesslogic.ChannelUserDAO;
import com.selftopup.pretups.user.businesslogic.ChannelUserVO;
import com.selftopup.util.BTSLUtil;

public class CP2PLoginDAO {

    private Log _log = LogFactory.getFactory().getInstance(CP2PLoginDAO.class.getName());

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
    public CP2PSubscriberVO loadCP2PSubscriberDetails(java.sql.Connection p_con, String p_loginID, String p_password, Locale locale) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadCP2PSubscriberDetails", "loadInterfaceTypeId():: Entered with p_loginID:" + p_loginID + " p_password=" + p_password + " locale=" + locale);
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
        sqlBuffer.append(" WHERE  UPPER(login_id)=UPPER(?)");
        sqlBuffer.append(" AND p2ps.status <> ? AND p2ps.status <> ? AND p2ps.network_code=L.network_code(+) ");
        CP2PSubscriberVO cp2pSubscriberVO = null;
        try {
            if (_log.isDebugEnabled())
                _log.debug("loadCP2PSubscriberDetails", " Query : " + sqlBuffer.toString());
            pstmt = p_con.prepareStatement(sqlBuffer.toString());
            int i = 0;
            pstmt.setString(++i, p_loginID);
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
                cp2pSubscriberVO.setNetworkCode(rs.getString("network_code"));
                cp2pSubscriberVO.setNetworkName(rs.getString("network_name"));
                cp2pSubscriberVO.setReportHeaderName(rs.getString("report_header_name"));
                cp2pSubscriberVO.setNetworkStatus(rs.getString("networkstatus"));
                cp2pSubscriberVO.setLoginId(p_loginID);
                cp2pSubscriberVO.setPassword(rs.getString("password"));
                cp2pSubscriberVO.setCategory("CP2P");
                cp2pSubscriberVO.setDomainId("CP2P");
                cp2pSubscriberVO.setSmsPin(rs.getString("pin"));
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
                cp2pSubscriberVO.setLanguage(rs.getString("Language"));
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
            _log.errorTrace("loadCP2PSubscriberDetails: Exception print stack trace:e=", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserDetails", " Exception : " + ex);
            _log.errorTrace("CP2PSubscriberVO Exception print stack trace: ", ex);
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
    public int updateUserLoginDetails(java.sql.Connection p_con, CP2PSubscriberVO cp2pSubscriberVO) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("updateUserLoginDetails() ::", " Entered..............");
        PreparedStatement pstmtU = null;
        int count = 0;
        try {
            String updateUsers = "UPDATE p2p_subscribers SET last_login_on=? WHERE user_id = ?";
            if (_log.isDebugEnabled())
                _log.info("updateUserLoginDetails ::", " Query updateUsers : " + updateUsers);
            pstmtU = p_con.prepareStatement(updateUsers);
            pstmtU.setDate(1, BTSLUtil.getSQLDateFromUtilDate(cp2pSubscriberVO.getLastLoginOn()));
            pstmtU.setString(2, cp2pSubscriberVO.getActiveUserId());
            count = pstmtU.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("updateUserLoginDetails() ::", " update last_login_on count=" + count + "     for user id=" + cp2pSubscriberVO.getUserId());
        } catch (SQLException sqe) {
            _log.error("updateUserLoginDetails() ::", " Exception : " + sqe);
            _log.errorTrace("updateUserLoginDetails: Exception print stack trace:e=", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updateUserLoginDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateUserLoginDetails", "error.general.processing");
        } catch (Exception ex) {
            _log.error("updateUserLoginDetails() ::", " Exception : " + ex);
            _log.errorTrace("updateUserLoginDetails: Exception print stack trace:e=", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updateUserLoginDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateUserLoginDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtU != null)
                    pstmtU.close();
            } catch (Exception ex) {
                _log.error("updateUserLoginDetails() ::", " Exception : in closing preparedstatement for Update" + ex);
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("updateUserLoginDetails() ::", " Exiting count=" + count);
        return count;
    }

    /**
     * Method updatePasswordCounter.
     * 
     * @param p_con
     *            Connection
     * @param p_userVO
     *            UserVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updatePasswordCounter(Connection p_con, CP2PSubscriberVO p_cp2pSubscriberVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updatePasswordCounter", "Entered p_userVO :" + p_cp2pSubscriberVO);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer("UPDATE p2p_subscribers SET invalid_password_count = ?, password_count_updated_on=? ,modified_by =?, modified_on =?   ");
            if (p_cp2pSubscriberVO.getPasswordReset() != null)
                updateQueryBuff.append(", PSWD_RESET=? ");
            updateQueryBuff.append("WHERE user_id=? ");
            String selectUpdate = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updatePasswordCounter", "select query:" + selectUpdate);
            pstmtUpdate = p_con.prepareStatement(selectUpdate);
            pstmtUpdate.setInt(i++, p_cp2pSubscriberVO.getInvalidPasswordCount());
            if (p_cp2pSubscriberVO.getPswdCountUpdatedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_cp2pSubscriberVO.getPswdCountUpdatedOn()));
            else
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);

            pstmtUpdate.setString(i++, p_cp2pSubscriberVO.getModifiedBy());

            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_cp2pSubscriberVO.getModifiedOn()));

            if (p_cp2pSubscriberVO.getPasswordReset() != null)
                pstmtUpdate.setString(i++, p_cp2pSubscriberVO.getPasswordReset());

            pstmtUpdate.setString(i++, p_cp2pSubscriberVO.getUserId());
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("updatePasswordCounter", "SQLException " + sqle.getMessage());
            _log.errorTrace("updatePasswordCounter Exception print stack trace:e=", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updatePasswordCounter]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updatePasswordCounter", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updatePasswordCounter", "Exception " + e.getMessage());
            _log.errorTrace("updatePasswordCounter Exception print stack trace:e=", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updatePasswordCounter]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updatePasswordCounter", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updatePasswordCounter", "Exiting updateCount:" + updateCount);
        }// end of finally
    }

    /**
     * Method to load the user details by mobile number or login id
     * 
     * @param p_con
     * @param p_loginID
     * @param p_msisdn
     * @param p_password
     * @param localeLanguage
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public ChannelUserVO loadUserDetailsByMsisdnOrLoginId(java.sql.Connection p_con, String p_msisdn, String p_loginId, String p_password, Locale locale) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadUserDetailsByMsisdnOrLoginId", "loadUserDetailsByMsisdnOrLoginId():: Entered with p_msisdn:" + p_msisdn + " p_password=" + p_password + " locale=" + locale);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sqlBuffer = new StringBuffer(" SELECT uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        sqlBuffer.append(" u.category_code,u.company,u.fax, u.parent_id, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time,u.firstname,u.lastname, "); // company
                                                                                                                                                                               // ,
                                                                                                                                                                               // fax
                                                                                                                                                                               // ,firstname,lastname
                                                                                                                                                                               // added
                                                                                                                                                                               // by
                                                                                                                                                                               // deepika
                                                                                                                                                                               // aggarwal
        sqlBuffer.append(" u.last_login_on,u.employee_code,u.status userstatus,u.email,u.created_by,u.created_on,u.modified_by, ");
        sqlBuffer.append(" u.modified_on,u.pswd_modified_on,  cusers.contact_person,u.contact_no,u.designation,u.division,u.department, ");
        sqlBuffer.append(" u.msisdn,u.user_type,cusers.in_suspend,cusers.out_suspend,u.address1,u.address2,u.city,u.state,u.country, ");
        sqlBuffer.append(" u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,u.invalid_password_count,u.password_count_updated_on, ");
        sqlBuffer.append(" l.status networkstatus,l.language_1_message,l.language_2_message, cat.category_code,cat.category_name, ");
        sqlBuffer.append(" cat.domain_code,cat.sequence_no,cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
        sqlBuffer.append(" cat.status catstatus, cat.max_txn_msisdn, cat.uncntrl_transfer_allowed, cat.scheduled_transfer_allowed, cat.restricted_msisdns, ");
        sqlBuffer.append(" cat.parent_category_code, cat.product_types_allowed,cat.category_type,cat.hierarchy_allowed, cat.transfertolistonly, ");
        sqlBuffer.append(" cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix,cat.web_interface_allowed, ");
        sqlBuffer.append(" cat.services_allowed,cat.domain_allowed,cat.fixed_domains,cat.outlets_allowed,cat.status categorystatus,cusers.comm_profile_set_id,cusers.transfer_profile_id,cusers.user_grade,gdt.sequence_no grph_sequence_no, ");
        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,up.sms_pin,up.pin_required,up.invalid_pin_count,dt.restricted_msisdn restricted_msisdn_allow,  ");
        // for Zebra and Tango by sanjeew date 06/07/07
        sqlBuffer.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, up.access_type ");
        // end of Zebra and Tango
        // added for loading password reset info
        sqlBuffer.append(", u.PSWD_RESET ");
        sqlBuffer.append(" FROM users u,users uowner,users uparent,networks l,categories cat,channel_users cusers,geographical_domain_types gdt ,domains dm,user_phones up,domain_types dt ");
        sqlBuffer.append(" WHERE ");
        if (!BTSLUtil.isNullString(p_msisdn))
            sqlBuffer.append("u.msisdn=? AND ");
        if (!BTSLUtil.isNullString(p_loginId))
            sqlBuffer.append("u.login_id=? AND ");

        sqlBuffer.append("  u.owner_id=uowner.user_id AND u.parent_id=uparent.user_id(+) AND gdt.grph_domain_type = cat.grph_domain_type ");
        sqlBuffer.append(" AND u.status <> ? AND u.status <> ? AND U.network_code=L.network_code(+)  AND u.user_id=cusers.user_id(+) ");
        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
        sqlBuffer.append(" AND u.msisdn = up.msisdn(+) ");
        sqlBuffer.append(" AND u.user_id=up.user_id(+) ");
        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
        ChannelUserVO channelUserVO = null;
        try {
            if (_log.isDebugEnabled())
                _log.debug("loadUserDetailsByMsisdnOrLoginId", " Query : " + sqlBuffer.toString());
            pstmt = p_con.prepareStatement(sqlBuffer.toString());
            int i = 0;

            if (!BTSLUtil.isNullString(p_msisdn))
                pstmt.setString(++i, p_msisdn);
            if (!BTSLUtil.isNullString(p_loginId))
                pstmt.setString(++i, p_loginId);
            // pstmt.setString(++i,BTSLUtil.encryptText(p_password));
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(++i, PretupsI.STATUS_DELETE);

            rs = pstmt.executeQuery();
            String userID;
            if (rs.next()) {
                channelUserVO = new ChannelUserVO();
                userID = rs.getString("user_id");
                channelUserVO.setUserID(userID);
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setNetworkName(rs.getString("network_name"));
                channelUserVO.setReportHeaderName(rs.getString("report_header_name"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setValidRequestURLs(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                if (rs.getTimestamp("last_login_on") != null)
                    channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setEmail(rs.getString("email"));

                // Added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal

                channelUserVO.setCreatedBy(userID);
                channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(userID);
                if (rs.getTimestamp("created_on") != null)
                    channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setNetworkStatus(rs.getString("networkstatus"));
                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    channelUserVO.setMessage(rs.getString("language_1_message"));
                } else {
                    channelUserVO.setMessage(rs.getString("language_2_message"));
                }
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
                channelUserVO.setDomainName(rs.getString("domain_name"));
                channelUserVO.setDomainStatus(rs.getString("domainstatus"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));

                // for Zebra and Tango by sanjeew date 06/07/07
                channelUserVO.setApplicationID(rs.getString("application_id"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
                channelUserVO.setAccessType(rs.getString("access_type"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                // End Zebra and Tango
                channelUserVO.setPasswordReset(rs.getString("PSWD_RESET"));
                if (rs.getTimestamp("password_count_updated_on") != null)
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                channelUserVO.setSmsPin(rs.getString("sms_pin"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                CategoryVO categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setCategoryStatus(rs.getString("categorystatus"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setMaxLoginCount(rs.getLong("max_login_count"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));

                categoryVO.setCategoryStatus(rs.getString("catstatus"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));

                channelUserVO.setCategoryVO(categoryVO);
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(p_con, channelUserVO.getUserID()));
            }
        } catch (SQLException sqe) {
            _log.error("loadUserDetailsByMsisdnOrLoginId", " SQLException : " + sqe);
            _log.errorTrace("loadUserDetailsByMsisdnOrLoginId: Exception print stack trace: ", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserDetailsByMsisdnOrLoginId", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserDetailsByMsisdnOrLoginId", " Exception : " + ex);
            _log.errorTrace("loadUserDetailsByMsisdnOrLoginId: Exception print stack trace: ", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserDetailsByMsisdnOrLoginId", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.error("loadUserDetailsByMsisdnOrLoginId::", " Exception : in closing resultset" + ex);
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (Exception ex) {
                _log.error("loadUserDetailsByMsisdnOrLoginId ::", " Exception : in closing preparedstatement" + ex);
            }
        }
        _log.debug("loadUserDetailsByMsisdnOrLoginId ::", " Exiting channelUserVO=" + channelUserVO);
        return channelUserVO;
    }

    /**
     * Method for loading Users Assigned Services List(means Services that are
     * assigned to the user).
     * From the table USER_SERVICES
     * 
     * Used in(userAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadSubscriberServicesList(Connection p_con, String p_category) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubscriberServicesList", "Entered p_userId=" + p_category);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        // Modification for Service Management [by Vipul]
        // strBuff.append(" SELECT US.service_type,ST.name FROM user_services US,service_type ST,users U,category_service_type CST");
        // strBuff.append(" WHERE US.user_id = ? AND US.service_type = ST.service_type");
        // strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type ");
        strBuff.append(" SELECT CST.SERVICE_TYPE,ST.NAME FROM SERVICE_TYPE ST,CATEGORY_SERVICE_TYPE CST ");
        strBuff.append(" WHERE CST.Category_code = ? AND CST.SERVICE_TYPE = ST.SERVICE_TYPE ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadSubscriberServicesList", "QUERY sqlSelect=" + sqlSelect);
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_category);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            _log.error("loadSubscriberServicesList", "SQLException : " + sqe);
            _log.errorTrace("loadSubscriberServicesList: Exception print stack trace: ", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadSubscriberServicesList", "Exception : " + ex);
            _log.errorTrace("loadSubscriberServicesList: Exception print stack trace: ", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberServicesList", "error.general.processing");
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
                _log.debug("loadSubscriberServicesList", "Exiting: userServicesList size=" + list.size());
            }
        }
        return list;
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
            _log.errorTrace("isSubscriberBarred: Exception print stack trace: ", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[isSubscriberBarred]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isSubscriberBarred", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isLoginIdExist", "Exception : " + ex);
            _log.errorTrace("isSubscriberBarred: Exception print stack trace: ", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[isSubscriberBarred]", "", "", "", "Exception:" + ex.getMessage());
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
