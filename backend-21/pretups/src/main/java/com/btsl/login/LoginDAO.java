package com.btsl.login;

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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class LoginDAO {

	
    private Log log = LogFactory.getFactory().getInstance(LoginDAO.class.getName());
    
    private String errorWhileProccesssing = "error.general.processing";
    private String errorWhileSQLProccesssing = "error.general.sql.processing";
    private String sqlException = "SQL Exception:";
    private String exception = "Exception:";
    private String query = " Query : ";

    /**
     * Method to load the user details for login info
     * 
     * @param con
     * @param loginID
     * @param password
     * @param localeLanguage
     * @return
     * @throws SQLExceptionR
     * @throws Exception
     */
    public ChannelUserVO loadUserDetails(java.sql.Connection con, String loginID, String password, Locale locale) throws BTSLBaseException {
    	 final String methodName = "loadUserDetails";
        if (log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("loadInterfaceTypeId():: Entered with p_loginID:");
        	msg.append(BTSLUtil.maskParam(loginID));
        	msg.append(" p_password=");
        	msg.append(BTSLUtil.maskParam(password));       
        	msg.append(" locale=");
        	msg.append(locale);
        	
        	String message=msg.toString();
            log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        
      	PreparedStatement pstmtLoan = null;
        ResultSet rsLoan = null;
        LoginQry loginQry = (LoginQry)ObjectProducer.getObject(QueryConstants.LOGIN_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlBuffer = loginQry.loadUserDetailsQry();

		StringBuilder strBuffer = new StringBuilder("select user_id,user_name from users where user_id in (?,?)");
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		String allowdUsrTypCreation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION);

        ChannelUserVO channelUserVO = null;
        try {
            if (log.isDebugEnabled())
                log.debug(methodName, query + sqlBuffer);
            pstmt = con.prepareStatement(sqlBuffer);
            if (log.isDebugEnabled())
                log.debug(methodName, query + strBuffer.toString());
    		pstmt1 = con.prepareStatement(strBuffer.toString());
            int i = 0;
            pstmt.setString(++i, loginID.toUpperCase());
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(++i, PretupsI.STATUS_DELETE);

            rs = pstmt.executeQuery();
            String userID;
            String uid;
            if (rs.next()) {
                channelUserVO = new ChannelUserVO();
                userID = rs.getString("user_id");
                channelUserVO.setUserID(userID);
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setNetworkName(rs.getString("network_name"));
                channelUserVO.setReportHeaderName(rs.getString("report_header_name"));
                channelUserVO.setLoginID(loginID);
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setPaymentTypes(rs.getString("payment_type"));
				pstmt1.setString(1, channelUserVO.getParentID());
				pstmt1.setString(2, channelUserVO.getOwnerID());
				rs1 = pstmt1.executeQuery();
				while(rs1.next()) {
					uid = rs1.getString("user_id");
					if(PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentID()))
						channelUserVO.setParentName(PretupsI.ROOT_PARENT_ID);
					else if(PretupsI.SYSTEM.equals(channelUserVO.getParentID()))
						channelUserVO.setParentName(PretupsI.SYSTEM);
					else if(channelUserVO.getParentID().equals(uid))
						channelUserVO.setParentName(rs1.getString("user_name"));
					if(channelUserVO.getOwnerID().equals(uid))
						channelUserVO.setOwnerName(rs1.getString("user_name"));
				}
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
                channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("modified_on")));
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
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                // Authentication Allowed
                channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setNetworkStatus(rs.getString("networkstatus"));
                if(channelSosEnable){
                	channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
                	channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
                	channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
                    }
                if (isTrfRuleUserLevelAllow)
                    channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                if (lmsAppl)
                    channelUserVO.setLmsProfile(rs.getString("lms_profile"));
                if (optInOutAllow) {
                    channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
                }
                channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
                if(locale!=null) {
                	if("en".equalsIgnoreCase(locale.getLanguage()) && !"US".equalsIgnoreCase(locale.getCountry())){
    					locale = new Locale(locale.getLanguage(),locale.getCountry());
    				}
                    LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (localeVO!=null && PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        channelUserVO.setMessage(rs.getString("language_1_message"));
                    } else {
                        channelUserVO.setMessage(rs.getString("language_2_message"));
                    }
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
                channelUserVO.setPhoneProfile(rs.getString("PHONE_PROFILE"));
                // End Zebra and Tango
                channelUserVO.setPasswordReset(rs.getString("PSWD_RESET"));
                if (rs.getTimestamp("password_count_updated_on") != null)
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                channelUserVO.setSmsPin(rs.getString("sms_pin"));
                channelUserVO.setPinReset(rs.getString("pin_reset"));// rahul.d
                                                                     // for
                                                                     // korek
                channelUserVO.setPrefixId(rs.getLong("PREFIX_ID"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                
                if ((PretupsI.YES).equals(allowdUsrTypCreation))
					channelUserVO.setAllowedUserTypeCreation(rs.getString("ALLOWD_USR_TYP_CREATION"));
                
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
                categoryVO.setSmsInterfaceAllowed(rs.getString("SMS_INTERFACE_ALLOWED"));
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                channelUserVO.setCategoryVO(categoryVO);
                
                if(PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)!=null && 
                		(boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)){
                	

                    
               	try {
               	  String sqlLoanBuffer = loginQry.loadUserLoanDetailsQry();
               	     pstmtLoan = con.prepareStatement(sqlLoanBuffer);
                        if (log.isDebugEnabled())
                            log.debug(methodName, query + sqlLoanBuffer.toString());
                        i = 0;
                        pstmtLoan.setString(++i, PretupsI.USER_STATUS_DELETED);
                        pstmtLoan.setString(++i, PretupsI.USER_STATUS_CANCELED);
                        pstmtLoan.setString(++i,channelUserVO.getUserID() );

                        rsLoan = pstmtLoan.executeQuery();
                        
                        while (rsLoan.next()) {      
                       	 UserLoanVO userLoanVO = new UserLoanVO();
                       	 userLoanVO.setUser_id(rsLoan.getString("user_id"));
                       	 userLoanVO.setProfile_id(rsLoan.getInt("profile_id"));
                       	 userLoanVO.setProduct_code(rsLoan.getString("product_code"));
                       	 userLoanVO.setLoan_threhold(rsLoan.getLong("loan_threhold"));
                       	 userLoanVO.setLoan_amount(rsLoan.getLong("loan_amount"));
                       	 userLoanVO.setLoan_given(rsLoan.getString("loan_given"));
                       	 userLoanVO.setLoan_given_amount(rsLoan.getLong("loan_given_amount"));
                       	 userLoanVO.setLast_loan_date(BTSLUtil.getTimestampFromUtilDate(rsLoan.getTimestamp("last_loan_date")));
                       	 userLoanVO.setLast_loan_txn_id(rsLoan.getString("last_loan_txn_id"));
                       	 userLoanVO.setSettlement_id(rsLoan.getString("settlement_id"));
                       	 userLoanVO.setSettlement_date(BTSLUtil.getTimestampFromUtilDate(rsLoan.getTimestamp("settlement_date")));
                       	 userLoanVO.setSettlement_loan_amount(rsLoan.getLong("settlement_loan_amount"));
                       	 userLoanVO.setSettlement_loan_interest(rsLoan.getLong("settlement_loan_interest"));
                       	 userLoanVO.setLoan_taken_from(rsLoan.getString("loan_taken_from"));
                       	 userLoanVO.setSettlement_from(rsLoan.getString("settlement_to"));
                       	 userLoanVO.setOptinout_allowed(rsLoan.getString("optinout_allowed"));
                       	 userLoanVO.setOptinout_on(BTSLUtil.getTimestampFromUtilDate(rsLoan.getTimestamp("optinout_on")));
                       	 userLoanVO.setOptinout_by(rsLoan.getString("optinout_by"));
                       	 if(channelUserVO.getUserLoanVOList() == null)
                       	 {
                       		 channelUserVO.setUserLoanVOList(new ArrayList<UserLoanVO>());
                       		 channelUserVO.getUserLoanVOList().add(userLoanVO);
                       	 }
                       	 else 
                       		 channelUserVO.getUserLoanVOList().add(userLoanVO);

                        }
               	}
               	finally {
               		log.debug( methodName,"SystemPreferences.USERWISE_LOAN_ENABLE"+(boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)+" userVO.getUserLoanVOList()"+ channelUserVO.getUserLoanVOList());
               	}

               }
               
               
               
              
               
                
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(con, channelUserVO.getUserID()));
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetails]", "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileSQLProccesssing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetails]", "", "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProccesssing);
        } finally {
        	try{
                if (rs1!= null){
                	rs1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmt1!= null){
                	pstmt1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	
        	try{
                if (pstmtLoan!= null){
                	pstmtLoan.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	
        	try{
                if (rsLoan!= null){
                	rsLoan.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        }
        log.debug("loadUserDetails ::", " Exiting channelUserVO=" + channelUserVO);
        return channelUserVO;
    }

    /**
     * Date : May 11, 2007
     * Discription :
     * Method : updateUserLoginDetails
     * 
     * @param con
     * @param userVO
     * @throws SQLException
     * @throws Exception
     * @return int
     * @author
     */
    public int updateUserLoginDetails(java.sql.Connection con, UserVO userVO) throws  BTSLBaseException {
    	 final String methodName = "updateUserLoginDetails";
    	if (log.isDebugEnabled())
            log.debug(methodName, " Entered..............");
       
        PreparedStatement pstmtU = null;
        int count = 0;
        try {
            String updateUsers = "UPDATE users SET last_login_on=? WHERE user_id = ?";
            if (log.isDebugEnabled())
                log.info("updateUserLoginDetails ::", " Query updateUsers : " + updateUsers);
            pstmtU = con.prepareStatement(updateUsers);
            pstmtU.setTimestamp(1, BTSLUtil.getSQLDateTimeFromUtilDate(userVO.getLastLoginOn()));
            pstmtU.setString(2, userVO.getActiveUserID());
            count = pstmtU.executeUpdate();
            if (log.isDebugEnabled())
                log.debug(methodName, " update last_login_on count=" + count + "     for user id=" + userVO.getUserID());
        } catch (SQLException sqe) {
            log.error(methodName, exception + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[updateUserLoginDetails]", "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProccesssing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[updateUserLoginDetails]", "", "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProccesssing);
        } finally {
        	try{
                if (pstmtU!= null){
                	pstmtU.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        if (log.isDebugEnabled())
            log.debug(methodName, " Exiting count=" + count);
        return count;
    }

    /**
     * Method updatePasswordCounter.
     * 
     * @param con
     *            Connection
     * @param userVO
     *            UserVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updatePasswordCounter(Connection con, UserVO userVO) throws BTSLBaseException {
    	  final String methodName = "updatePasswordCounter";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_userVO :" + userVO);
      
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            StringBuilder updateQueryBuff = new StringBuilder("UPDATE users SET invalid_password_count = ?, password_count_updated_on=? ,modified_by =?, modified_on =?   ");
            if (userVO.getPasswordReset() != null)
                updateQueryBuff.append(", PSWD_RESET=? ");
            updateQueryBuff.append("WHERE user_id=? ");
            String selectUpdate = updateQueryBuff.toString();
            if (log.isDebugEnabled())
                log.debug(methodName, "select query:" + selectUpdate);
            pstmtUpdate = con.prepareStatement(selectUpdate);
            pstmtUpdate.setInt(i++, userVO.getInvalidPasswordCount());
            if (userVO.getPasswordCountUpdatedOn() != null)
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(userVO.getPasswordCountUpdatedOn()));
            else
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);

            if (userVO.isStaffUser()) {
                if (!BTSLUtil.isNullString(userVO.getActiveUserID()))
                    pstmtUpdate.setString(i++, userVO.getActiveUserID());
                else
                    pstmtUpdate.setString(i++, userVO.getModifiedBy());
            } else
                pstmtUpdate.setString(i++, userVO.getModifiedBy());

            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(userVO.getModifiedOn()));

            if (userVO.getPasswordReset() != null)
                pstmtUpdate.setString(i++, userVO.getPasswordReset());

            if (userVO.isStaffUser()) {
                if (!BTSLUtil.isNullString(userVO.getActiveUserID()))
                    pstmtUpdate.setString(i++, userVO.getActiveUserID());
                else
                    pstmtUpdate.setString(i++, userVO.getUserID());
            } else
                pstmtUpdate.setString(i++, userVO.getUserID());
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, sqlException + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[updatePasswordCounter]", "", "", "", sqlException + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileSQLProccesssing);
        }// end of catch
        catch (Exception e) {
            log.error(methodName,exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[updatePasswordCounter]", "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProccesssing);
        }// end of catch
        finally {
        	try{
                if (pstmtUpdate!= null){
                	pstmtUpdate.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting updateCount:" + updateCount);
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
    public ChannelUserVO loadUserDetailsByMsisdnOrLoginId(java.sql.Connection con, String msisdn, String loginId, String password, Locale locale) throws  BTSLBaseException {
    	  final String methodName = "loadUserDetailsByMsisdnOrLoginId";
    	if (log.isDebugEnabled())
    	{
    		StringBuffer msg=new StringBuffer("");
        	msg.append("loadUserDetailsByMsisdnOrLoginId():: Entered with p_msisdn:");
        	msg.append(msisdn);
        	msg.append(" loginId=");
        	msg.append(BTSLUtil.maskParam(loginId));  
        	msg.append(" p_password=");
        	msg.append(BTSLUtil.maskParam(password));       
        	msg.append(" locale=");
        	msg.append(locale);
        	
        	String message=msg.toString();
            log.debug(methodName, message);
    	}
    	
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoginQry loginQry = (LoginQry)ObjectProducer.getObject(QueryConstants.LOGIN_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlBuffer = loginQry.loadUserDetailsByMsisdnOrLoginIdQry(msisdn, loginId);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        ChannelUserVO channelUserVO = null;
        try {
            if (log.isDebugEnabled())
                log.debug(methodName, query + sqlBuffer);
            pstmt = con.prepareStatement(sqlBuffer);
            int i = 0;

            if (!BTSLUtil.isNullString(msisdn))
                pstmt.setString(++i, msisdn);
            if (!BTSLUtil.isNullString(loginId))
                pstmt.setString(++i, loginId.toUpperCase());
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
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                channelUserVO.setAuthType("AUTHENTICATION_TYPE");
                if (isTrfRuleUserLevelAllow)
                    channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (localeVO!=null && PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
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
                channelUserVO.setLoginID(rs.getString("login_id"));

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
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileSQLProccesssing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProccesssing);
        } finally {
        	 try{
                 if (rs!= null){
                 	rs.close();
                 }
               }
               catch (SQLException e){
             	  log.error("An error occurred closing result set.", e);
               }
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        }   
        log.debug(methodName, " Exiting channelUserVO=" + channelUserVO);
        return channelUserVO;
    }
 
	public GradeVO loadUserDetailsOnTwoFAallowed(java.sql.Connection con, String userid) throws  BTSLBaseException
    {
    	 final String methodName = "loadUserDetailsOnTwoFAallowed";
    	if (log.isDebugEnabled())
            log.debug(methodName,"Entered with userGrade:" + userid);
    	GradeVO gradeVO = null;
    	 PreparedStatement pstmt = null;
    	 ResultSet rs = null;
    	 StringBuilder sqlBuffer = new StringBuilder("SELECT cg.grade_code,cg.grade_name,cg.is_2fa_allowed, cu.user_id from channel_grades cg, channel_users cu where cg.grade_code =cu.user_grade and cu.user_id = ?");
    	 try {
             if (log.isDebugEnabled())
                 log.debug(methodName, query + sqlBuffer.toString());
             pstmt = con.prepareStatement(sqlBuffer.toString());
            int i=0;
            pstmt.setString(++i,userid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	gradeVO = new GradeVO();
            	
            	gradeVO.setTwoFAallowed(rs.getString("is_2fa_allowed"));
            	gradeVO.setGradeName(rs.getString("grade_name"));
            	 
            }
            
    	 } catch (SQLException sqe) {
    		 log.error(methodName, sqlException + sqe);
             log.errorTrace(methodName, sqe);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsOnTwoFAallowed]", "", "", "", sqlException + sqe.getMessage());
             throw new BTSLBaseException(this, methodName, errorWhileSQLProccesssing);
		}
    	 finally {
    		 try{
    		        if (rs!= null){
    		        	rs.close();
    		        }
    		      }
    		      catch (SQLException e){
    		    	  log.error("An error occurred closing result set.", e);
    		      }
    		 try{
 		        if (pstmt!= null){
 		        	pstmt.close();
 		        }
 		      }
 		      catch (SQLException e){
 		    	  log.error("An error occurred closing result set.", e);
 		      }
     }
     log.debug(methodName, " Exiting gradeVO=" + gradeVO);
     
    	 
		return gradeVO;
       
    }
	
	/**
	 * Method updateInvalidPassCount.
	 * 
	 * @param channelUser channelUser
	 * @return int
	 * @throws BTSLBaseException 
	 */

	public int updateInvalidPassCount(com.btsl.user.businesslogic.ChannelUserVO channelUser) throws BTSLBaseException {
		final String methodName = "updateInvalidPassCount";
	      if (log.isDebugEnabled())
	          log.debug(methodName, "Entered ChannelUser :" + channelUser);
	    
	      PreparedStatement pstmtUpdate = null;
	      int updateCount = 0;
	      Connection con = null;
		  MComConnectionI mcomCon = null;
	      try {
	    	  mcomCon = new MComConnection();
		      con= mcomCon.getConnection();
	          int i = 1;
	          StringBuilder updateQueryBuff = new StringBuilder("UPDATE users SET invalid_password_count = ?, password_count_updated_on=? ,modified_by =?, modified_on =?   ");
	          if (channelUser.getPswdReset() != null)
	              updateQueryBuff.append(", PSWD_RESET=? ");
	          updateQueryBuff.append("WHERE user_id=? ");
	          String selectUpdate = updateQueryBuff.toString();
	          if (log.isDebugEnabled())
	              log.debug(methodName, "select query:" + selectUpdate);
	          pstmtUpdate = con.prepareStatement(selectUpdate);
	          pstmtUpdate.setLong(i++, channelUser.getInvalidPasswordCount());
	          if (channelUser.getPasswordCountUpdatedOn() != null)
	              pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(channelUser.getPasswordCountUpdatedOn()));
	          else
	              pstmtUpdate.setNull(i++, Types.TIMESTAMP);

	          if (channelUser.isStaffUser()) {
	              if (!BTSLUtil.isNullString(channelUser.getActiveUserID()))
	                  pstmtUpdate.setString(i++, channelUser.getActiveUserID());
	              else
	                  pstmtUpdate.setString(i++, channelUser.getModifiedBy());
	          } else
	              pstmtUpdate.setString(i++, channelUser.getModifiedBy());

	          pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(channelUser.getModifiedOn()));

	          if (channelUser.getPswdReset() != null)
	              pstmtUpdate.setString(i++, channelUser.getPswdReset());

	          if (channelUser.isStaffUser()) {
	              if (!BTSLUtil.isNullString(channelUser.getActiveUserID()))
	                  pstmtUpdate.setString(i++, channelUser.getActiveUserID());
	              else
	                  pstmtUpdate.setString(i++, channelUser.getUserId());
	          } else
	              pstmtUpdate.setString(i++, channelUser.getUserId());
	          updateCount = pstmtUpdate.executeUpdate();
	          if(updateCount >0)
	        	  con.commit();
	          else
	        	  throw new BTSLBaseException(this, methodName, errorWhileSQLProccesssing);
	          
	      }
	      catch (SQLException sqle) {
	          log.error(methodName, sqlException + sqle.getMessage());
	          log.errorTrace(methodName, sqle);
	          EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[updatePasswordCounter]", "", "", "", sqlException + sqle.getMessage());
	          throw new BTSLBaseException(this, methodName, errorWhileSQLProccesssing);
	      }
	      catch (Exception e) {
	          log.error(methodName,exception + e.getMessage());
	          log.errorTrace(methodName, e);
	          EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[updatePasswordCounter]", "", "", "", exception + e.getMessage());
	          throw new BTSLBaseException(this, methodName, errorWhileProccesssing);
	      }
	      finally {
	    	  
	      	try{
	              if (pstmtUpdate!= null){
	              	pstmtUpdate.close();
	              }
	            }
	            catch (SQLException e){
	          	  log.error("An error occurred closing statement.", e);
	            }
	      	try {
				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
	      }
	      return updateCount;
	}

	
}
