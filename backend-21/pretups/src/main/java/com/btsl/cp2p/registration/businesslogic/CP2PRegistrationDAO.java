package com.btsl.cp2p.registration.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.util.BTSLUtil;

public class CP2PRegistrationDAO {
    private Log log = LogFactory.getFactory().getInstance(CP2PRegistrationDAO.class.getName());
    private String exception =  " Exception : ";
    private String sqlException =  " SQL Exception : ";

    /*
     * Method to check whether Mobile Number provided for registration is
     * already registered By SMS or not.
     */

    public boolean isSubscriberMobileNumberExist(Connection con, String msisdn) throws BTSLBaseException {
    	  final String methodName = "isSubscriberMobileNumberExist";
    	  StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn=");
        	loggerValue.append(msisdn);
            log.debug(methodName, loggerValue);
        }
      
         
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT msisdn FROM p2p_subscribers WHERE msisdn = ? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
           

            pstmt.setString(1, msisdn);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append(sqlException);
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, "isUserLoginExist", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(exception);
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
          
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=" );
            	loggerValue.append(existFlag);
                log.debug(methodName, loggerValue);
            }
        }
    }

    public boolean isSubscriebrLoginIdExist(Connection con, String loginId) throws BTSLBaseException {
        final String methodName = "isSubscriebrLoginIdExist";
        StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(loginId);
            log.debug(methodName, loggerValue);
        }
       
         
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT login_id FROM p2p_subscribers WHERE login_id = ? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
           

            pstmt.setString(1, loginId);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append(sqlException);
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", sqlException+ sqe.getMessage());
            throw new BTSLBaseException(this, "isUserLoginExist", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(exception);
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberMobileNumberExist]", "", "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, "isLoginIdExist", "error.general.processing",ex);
        } finally {
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=");
            	loggerValue.append(existFlag);
                log.debug(methodName, loggerValue);
            }
        }
    }

    public int addSubscriber(Connection con, String msisdn, String loginID, String subscriberPassword, CP2PSubscriberVO cp2pSubscriberVO) throws BTSLBaseException {
    	final String methodName = "addSubscriber";
    	StringBuilder loggerValue= new StringBuilder();
    	if (log.isDebugEnabled()){
    		loggerValue.append("Entered: p_msisdn= ");
        	loggerValue.append(msisdn);
        	loggerValue.append(" p_loginID=");
        	loggerValue.append(loginID);
            log.debug(methodName, loggerValue);
    	}
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        
        try {
            String queryUpdate = "UPDATE p2p_subscribers SET login_id=?,password=? WHERE msisdn = ? ";
            pstmtUpdate = con.prepareStatement(queryUpdate);
            pstmtUpdate.setString(1, loginID);
            pstmtUpdate.setString(2, subscriberPassword);
            pstmtUpdate.setString(3, msisdn);

            updateCount = pstmtUpdate.executeUpdate();
            // remove the push message from here and added in Action class.

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append(sqlException);
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[addSubscriber]", "", "", "", sqlException + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(exception);
        	loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[changePassword]", "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName ,"error.general.processing",e);
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append(" Exiting with updateCount=");
            	loggerValue.append(updateCount);
                log.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }

    /**
     * Method to load the user details for login info
     * 
     * @param con
     * @param p_loginID
     * @param p_password
     * @param localeLanguage
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public CP2PSubscriberVO loadCP2PSubscriberDetails(java.sql.Connection con, String msisdn,String servicetype) throws  BTSLBaseException {
    	 final String methodName = "loadCP2PSubscriberDetails";
    	 StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()){
    		loggerValue.setLength(0);
        	loggerValue.append("loadInterfaceTypeId():: Entered with p_msisdn:");
        	loggerValue.append(msisdn);
            log.debug(methodName, loggerValue);
    	}
         
       
        CP2PSubscriberVO cp2pSubscriberVO = null;
        try {
                CP2PRegistrationQry cp2pRegistrationQry = (CP2PRegistrationQry) ObjectProducer.getObject(QueryConstants.CP2P_REGISTRATION_QRY, QueryConstants.QUERY_PRODUCER);
            	String sqlBuffer = cp2pRegistrationQry.loadCP2PSubscriberDetails(servicetype);
            try(PreparedStatement pstmt = con.prepareStatement(sqlBuffer);)
            {
            int i = 1;
            pstmt.setString(i, msisdn);
			if (servicetype != null) {
				i++;
				pstmt.setString(i, servicetype);
			}
			i++;
            pstmt.setString(i, PretupsI.USER_STATUS_DELETED);
            i++;
            pstmt.setString(i, PretupsI.USER_STATUS_CANCELED);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            String userID;
            if (rs.next()) {
                cp2pSubscriberVO = new CP2PSubscriberVO();
                userID = rs.getString("user_id");
                cp2pSubscriberVO.setUserID(userID);
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
            }
        }

        catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append(sqlException);
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "",sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(exception);
        	loggerValue.append(ex);
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	log.debug(methodName, "inside finally");
        }
        loggerValue.setLength(0);
    	loggerValue.append(" Exiting channelUserVO=");
    	loggerValue.append(cp2pSubscriberVO);
        log.debug(methodName, loggerValue);
        return cp2pSubscriberVO;
    }

    /**
     * Method to load the user details for login info
     * 
     * @param con
     * @param p_loginID
     * @param p_password
     * @param localeLanguage
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public CP2PSubscriberVO loadCP2PSubscriberDetails(java.sql.Connection con, String msisdn, String loginId,String servicetype) throws  BTSLBaseException {
    	  final String methodName = "loadCP2PSubscriberDetails";
    	  StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()){
    		loggerValue.setLength(0);
        	loggerValue.append("loadInterfaceTypeId():: Entered with p_msisdn:");
        	loggerValue.append(msisdn);
        	loggerValue.append(" p_loginId");
        	loggerValue.append(loginId);
            log.debug(methodName, loggerValue);
    	}
        
        
        CP2PSubscriberVO cp2pSubscriberVO = null;
        try {
            CP2PRegistrationQry cp2pRegistrationQry = (CP2PRegistrationQry) ObjectProducer.getObject(QueryConstants.CP2P_REGISTRATION_QRY, QueryConstants.QUERY_PRODUCER);
            String sqlBuffer = cp2pRegistrationQry.loadCP2PSubscriberDetails1(servicetype);
            try(PreparedStatement pstmt = con.prepareStatement(sqlBuffer);)
            {
            int i = 1;
            pstmt.setString(i, msisdn);
            i++;
            pstmt.setString(i, loginId);
			if (servicetype != null) {
				i++;
				pstmt.setString(i, servicetype);
			}
			i++;
            pstmt.setString(i, PretupsI.USER_STATUS_DELETED);
            i++;
            pstmt.setString(i, PretupsI.USER_STATUS_CANCELED);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            String userID;
            if (rs.next()) {
                cp2pSubscriberVO = new CP2PSubscriberVO();
                userID = rs.getString("user_id");
                cp2pSubscriberVO.setUserID(userID);
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
            }
        }

        catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append(sqlException);
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(exception);
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[loadUserDetails]", "", "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            log.debug(methodName, "inside finally");
        }
        loggerValue.setLength(0);
    	loggerValue.append(" Exiting channelUserVO=");
    	loggerValue.append(cp2pSubscriberVO);
        log.debug(methodName, " Exiting channelUserVO=" + cp2pSubscriberVO);
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
    public int updateSubscriberPasswordDetails(java.sql.Connection con, String password, Date pswdModifiedOn, String msisdn, String loginID, CP2PSubscriberVO cp2pSubscriberVO) throws  BTSLBaseException {
    	 final String methodName = "updateSubscriberPasswordDetails";
    	if (log.isDebugEnabled())
            log.debug(methodName, " Entered..............");
        PreparedStatement pstmtU = null;
        int count = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        try {
            String updateUsers = "UPDATE p2p_subscribers SET password=?,pswd_modified_on=?,modified_on= ?, modified_by= ?, PSWD_RESET=? WHERE msisdn = ? and login_id=? ";
            if (log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append(" Query updateUsers : ");
            	loggerValue.append(updateUsers);
                log.info(methodName, loggerValue);
            }
            pstmtU = con.prepareStatement(updateUsers);
            pstmtU.setString(1, password);
            pstmtU.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pswdModifiedOn));
            pstmtU.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pswdModifiedOn));
            pstmtU.setString(4, cp2pSubscriberVO.getUserID());
            pstmtU.setString(5, "Y");
            pstmtU.setString(6, msisdn);
            pstmtU.setString(7, loginID);

            count = pstmtU.executeUpdate();
            // remove the push message from here and added in Action class.
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
        	loggerValue.append(sqlException);
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PLoginDAO[updateUserLoginDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(exception);
        	loggerValue.append(ex);
            log.error(methodName,loggerValue);
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

    public boolean isSubscriberBarred(Connection con, String msisdn) throws BTSLBaseException {
        final String methodName = "isSubscriberBarred";
        StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(msisdn);
            log.debug(methodName, loggerValue);
        }
        
        boolean existFlag = false;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT msisdn FROM barred_msisdns WHERE msisdn = ? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug("isSubscriberBarred", loggerValue);
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
        	loggerValue.setLength(0);
        	loggerValue.append(sqlException);
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberBarred]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(exception);
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PRegistrationDAO[isSubscriberBarred]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=");
            	loggerValue.append(existFlag);
                log.debug(methodName, loggerValue);
            }
        }
    }

}
