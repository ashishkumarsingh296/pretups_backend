package com.btsl.cp2p.login.businesslogic;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;

public class CP2PLoginDAO {

    private Log log = LogFactory.getFactory().getInstance(CP2PLoginDAO.class.getName());

    /**
     * Method to load the user details for login info
     * 
     * @param con
     * @param loginID
     * @param password
     * @param localeLanguage
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public CP2PSubscriberVO loadCP2PSubscriberDetails(java.sql.Connection con, String loginID, String password, Locale locale,String servicetype) throws  BTSLBaseException {
    	  final String methodName = "loadCP2PSubscriberDetails";
    	  
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()){
    	  
      	  loggerValue.append("loadInterfaceTypeId():: Entered with p_loginID: ");
      	  loggerValue.append(loginID);
      	  loggerValue.append(" p_password=");
      	  loggerValue.append(BTSLUtil.maskParam(password));
      	  loggerValue.append(" locale=");
      	  loggerValue.append(locale);
          log.debug(methodName, loggerValue);
    	}
    	
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        CP2PSubscriberVO cp2pSubscriberVO = null;
        try {
        	CP2PLoginQry cp2pLoginQry = (CP2PLoginQry)ObjectProducer.getObject(QueryConstants.CP2_LOGIN_QRY, QueryConstants.QUERY_PRODUCER);
        	String sqlBuffer = cp2pLoginQry.loadCP2PSubscriberDetailsQry(servicetype);
            pstmt = con.prepareStatement(sqlBuffer);
            int i = 1;
            pstmt.setString(i, loginID);
            if(servicetype!=null){
            	i++;
            	pstmt.setString(i, servicetype);
            }
            i++;
            pstmt.setString(i, PretupsI.USER_STATUS_DELETED);
            i++;
            pstmt.setString(i, PretupsI.USER_STATUS_CANCELED);

            rs = pstmt.executeQuery();
            String userID;
            if (rs.next()) {
                cp2pSubscriberVO = new CP2PSubscriberVO();
                userID = rs.getString("user_id");
                cp2pSubscriberVO.setUserID(userID);
                cp2pSubscriberVO.setUserName(rs.getString("user_name"));
                cp2pSubscriberVO.setPasswordReset(rs.getString("pswd_reset"));
                cp2pSubscriberVO.setNetworkCode(rs.getString("network_code"));
                cp2pSubscriberVO.setNetworkName(rs.getString("network_name"));
                cp2pSubscriberVO.setReportHeaderName(rs.getString("report_header_name"));
                cp2pSubscriberVO.setNetworkStatus(rs.getString("networkstatus"));
                cp2pSubscriberVO.setLoginId(loginID);
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
                if(servicetype!=null){
                    cp2pSubscriberVO.setDailyTxrAmount(rs.getString("daily_transfer_amount"));
                    cp2pSubscriberVO.setDailyTxrCount(rs.getString("daily_transfer_count"));
                    cp2pSubscriberVO.setMonthlyTransferAmount(rs.getLong("monthly_transfer_amount"));
                    cp2pSubscriberVO.setMonthlyTransferCount(rs.getLong("monthly_transfer_count"));
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
                    cp2pSubscriberVO.setPreviousWeeklyTxrCount(rs.getLong("prev_weekly_transfer_count"));
                    cp2pSubscriberVO.setPreviousWeeklyTxrAmount(rs.getLong("prev_weekly_transfer_amount"));
                    cp2pSubscriberVO.setWeeklyTxrAmount(rs.getLong("weekly_transfer_amount"));
                    cp2pSubscriberVO.setWeeklyTxrCount(rs.getLong("weekly_transfer_count"));
                    }
                      
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


                cp2pSubscriberVO.setMsisdn(rs.getString("msisdn"));
                cp2pSubscriberVO.setPinBlockCount(rs.getString("pin_block_count"));
                if (rs.getTimestamp("pin_modified_on") != null)
                    cp2pSubscriberVO.setPinModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pin_modified_on")));
                cp2pSubscriberVO.setPrefixID(rs.getLong("prefix_id"));
               
               

                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
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
               

                if (rs.getTimestamp("password_count_updated_on") != null)
                    cp2pSubscriberVO.setPswdCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));

                try {
                    cp2pSubscriberVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    cp2pSubscriberVO.setPasswordModifiedOn(null);
                }

            }
        }

        catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append(" SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.processing",ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            	loggerValue.setLength(0);
            	loggerValue.append(" Exception : in closing resultset");
            	loggerValue.append(ex);
                log.error(methodName, loggerValue);
            }
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        loggerValue.setLength(0);
    	loggerValue.append(" Exiting channelUserVO=");
    	loggerValue.append(cp2pSubscriberVO);
        log.debug(methodName, loggerValue);
        return cp2pSubscriberVO;
    }

    /**
     * Date : May 11, 2007
     * Discription :
     * Method : updateUserLoginDetails
     * 
     * @param con
     * @param p_userVO
     * @throws SQLException
     * @throws Exception
     * @return int
     * @author
     */
    public int updateUserLoginDetails(java.sql.Connection con, CP2PSubscriberVO cp2pSubscriberVO) throws  BTSLBaseException {
    	 final String methodName = "updateUserLoginDetails";
    	if (log.isDebugEnabled())
            log.debug(methodName, " Entered..............");
    	StringBuilder loggerValue= new StringBuilder();
        PreparedStatement pstmtU = null;
        int count = 0;
        try {
            String updateUsers = "UPDATE p2p_subscribers SET last_login_on=? WHERE user_id = ?";
            if (log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append(" Query updateUsers : ");
            	loggerValue.append(updateUsers);
                log.info(methodName, loggerValue);
            }
            pstmtU = con.prepareStatement(updateUsers);
            pstmtU.setDate(1, BTSLUtil.getSQLDateFromUtilDate(cp2pSubscriberVO.getLastLoginOn()));
            pstmtU.setString(2, cp2pSubscriberVO.getActiveUserId());
            count = pstmtU.executeUpdate();
            if (log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append(" update last_login_on count=");
            	loggerValue.append(count);
            	loggerValue.append("     for user id=");
            	loggerValue.append(cp2pSubscriberVO.getUserID());
                log.debug(methodName, loggerValue);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updateUserLoginDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updateUserLoginDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (pstmtU != null)
                    pstmtU.close();
            } catch (Exception ex) {
            	loggerValue.setLength(0);
            	loggerValue.append(" Exception : in closing preparedstatement for Update");
            	loggerValue.append(ex);
                log.error(methodName, loggerValue);
                log.errorTrace(methodName, ex);
            }
        }
        if (log.isDebugEnabled()){
        	loggerValue.setLength(0);
        	loggerValue.append(" Exiting count=");
        	loggerValue.append(count);
            log.debug(methodName, loggerValue);
        }
        return count;
    }

    /**
     * Method updatePasswordCounter.
     * 
     * @param con
     *            Connection
     * @param p_userVO
     *            UserVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updatePasswordCounter(Connection con, CP2PSubscriberVO cp2pSubscriberVO) throws BTSLBaseException {
    	 final String methodName = "updatePasswordCounter";
    	 StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()){
    		loggerValue.setLength(0);
        	loggerValue.append("Entered p_userVO :");
        	loggerValue.append(cp2pSubscriberVO);
            log.debug(methodName, loggerValue);
    	}
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            StringBuilder updateQueryBuff = new StringBuilder("UPDATE p2p_subscribers SET invalid_password_count = ?, password_count_updated_on=? ,modified_by =?, modified_on =?   ");
            if (cp2pSubscriberVO.getPasswordReset() != null)
                updateQueryBuff.append(", PSWD_RESET=? ");
            updateQueryBuff.append("WHERE user_id=? ");
            String selectUpdate = updateQueryBuff.toString();
            if (log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectUpdate);
                log.debug(methodName, loggerValue);
            }
            pstmtUpdate = con.prepareStatement(selectUpdate);
            pstmtUpdate.setInt(i, cp2pSubscriberVO.getInvalidPasswordCount());
            i++;
            if (cp2pSubscriberVO.getPswdCountUpdatedOn() != null)
            {
            	pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(cp2pSubscriberVO.getPswdCountUpdatedOn()));
            	i++;
            }
            else
            {
            	pstmtUpdate.setNull(i, Types.TIMESTAMP);
            	i++;
            }

            pstmtUpdate.setString(i, cp2pSubscriberVO.getModifiedBy());
            i++;

            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(cp2pSubscriberVO.getModifiedOn()));
            i++;

            if (cp2pSubscriberVO.getPasswordReset() != null)
            {
            	pstmtUpdate.setString(i, cp2pSubscriberVO.getPasswordReset());
            	i++;
            }

            pstmtUpdate.setString(i, cp2pSubscriberVO.getUserID());
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updatePasswordCounter]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updatePasswordCounter", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updatePasswordCounter]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updatePasswordCounter", "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting updateCount:");
            	loggerValue.append(updateCount);
                log.debug(methodName, loggerValue);
            }
        }// end of finally
    }

    /**
     * Method to load the user details by mobile number or login id
     * 
     * @param con
     * @param p_loginID
     * @param msisdn
     * @param password
     * @param localeLanguage
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public ChannelUserVO loadUserDetailsByMsisdnOrLoginId(java.sql.Connection con, String msisdn, String loginId, String password, Locale locale) throws Exception {
    	  final String methodName = "loadUserDetailsByMsisdnOrLoginId";
    	  StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()){
         	loggerValue.append("loadUserDetailsByMsisdnOrLoginId():: Entered with p_msisdn:");
         	loggerValue.append(msisdn);
         	loggerValue.append(" p_password=");
         	loggerValue.append(BTSLUtil.maskParam(password));
         	loggerValue.append(" locale=");
         	loggerValue.append(locale);
            log.debug(methodName, loggerValue);
    	}
        PreparedStatement pstmt = null;
        ResultSet rs = null;
      
        ChannelUserVO channelUserVO = null;
        try {
        	CP2PLoginQry cp2pLoginQry = (CP2PLoginQry) ObjectProducer.getObject(QueryConstants.CP2_LOGIN_QRY, QueryConstants.QUERY_PRODUCER);
        	String sqlBuffer = cp2pLoginQry.loadUserDetailsByMsisdnOrLoginIdQry(msisdn, loginId);
            pstmt = con.prepareStatement(sqlBuffer);
            int i = 0;

            if (!BTSLUtil.isNullString(msisdn))
            {
            	i++;
            	pstmt.setString(i, msisdn);
            }
            
            if (!BTSLUtil.isNullString(loginId))
            {
            	i++;
            	pstmt.setString(i, loginId);
            }
            i++;
            pstmt.setString(i, PretupsI.USER_STATUS_DELETED);
            i++;
            pstmt.setString(i, PretupsI.USER_STATUS_CANCELED);
            i++;
            pstmt.setString(i, PretupsI.STATUS_DELETE);

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
                    log.errorTrace(methodName, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(con, channelUserVO.getUserID()));
            }
        } catch (SQLException sqe) {
        	 loggerValue.setLength(0);
         	loggerValue.append(" SQLException : ");
         	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserDetailsByMsisdnOrLoginId", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
         	loggerValue.append(" Exception : ");
         	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            	 loggerValue.setLength(0);
             	loggerValue.append(" Exception : in closing resultset");
             	loggerValue.append(ex);
                log.error(methodName, loggerValue);
            }
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        loggerValue.setLength(0);
    	loggerValue.append(" Exiting channelUserVO=");
    	loggerValue.append(channelUserVO);
        log.debug(methodName, " Exiting channelUserVO=" + channelUserVO);
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
     * @param con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadSubscriberServicesList(Connection con, String category) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	  final String methodName = "loadSubscriberServicesList";
    	if (log.isDebugEnabled()) {
    		loggerValue.append("Entered p_userId=");
    		loggerValue.append(category);
            log.debug(methodName, loggerValue);
        }
      
       
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT CST.SERVICE_TYPE,ST.NAME FROM SERVICE_TYPE ST,CATEGORY_SERVICE_TYPE CST ");
        strBuff.append(" WHERE CST.Category_code = ? AND CST.SERVICE_TYPE = ST.SERVICE_TYPE ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()){
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug("loadSubscriberServicesList", loggerValue);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, category);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberServicesList", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberServicesList", "error.general.processing",ex);
        } finally {
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userServicesList size=");
            	loggerValue.append(list.size());
                log.debug(methodName, loggerValue);
            }
        }
        return list;
    }

    public boolean isSubscriberBarred(Connection con, String msisdn) throws BTSLBaseException {
    	  final String methodName = "isSubscriberBarred";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_loginId=" + msisdn);
        }
      
        
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT msisdn FROM barred_msisdns WHERE msisdn = ? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("isSubscriberBarred", "QUERY sqlSelect=" + sqlSelect);
        }
        try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);){
            

            pstmt.setString(1, msisdn);

           try(ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[isSubscriberBarred]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isSubscriberBarred", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[isSubscriberBarred]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isSubscriberBarred", "error.general.processing",ex);
        } finally {
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }
}
