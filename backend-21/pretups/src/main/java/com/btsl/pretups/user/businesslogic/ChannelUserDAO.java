/**
 * @(#)ChannelUserDAO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Gurjeet Bedi 5/08/2005 Initial Creation
 *                         Sandeep Goel 22/07/2006 Modification
 *                         Sandeep Goel 05/08/2006 Modification ID USD001updateChannelUserInfo
 *                         Ankit Zindal 20/11/2006 ChangeID=LOCALEMASTER
 *                         Harpreet Kaur 03/10/2011 Modification
 *                         Chhaya Sikheria 02/11/2011 Modification
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */

package com.btsl.pretups.user.businesslogic; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.btsl.pretups.channel.transfer.businesslogic.*;
import com.btsl.util.MessageResources;
import org.springframework.stereotype.Repository;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.transfer.requesthandler.BarredUserDetailsVO;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentModeDetailsDto;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.logging.BatchC2CFileProcessLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.user.service.PaymentdetailC2C;
import com.btsl.login.LoginQry;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
/**
 * 
 */

@Repository
public class ChannelUserDAO {

	/**
	 * Field LOG.
	 */
	private static final Log LOG = LogFactory.getLog(ChannelUserDAO.class.getName());
	private ChannelUserQry channelUserQry = (ChannelUserQry) ObjectProducer.getObject(QueryConstants.CHANNEL_USER_QRY, QueryConstants.QUERY_PRODUCER);
	private static final String SQL_EXCEPTION = "SQL Exception : ";
	private static final String EXCEPTION = " Exception : ";
	final Integer amountMultFactor =SystemPreferences.AMOUNT_MULT_FACTOR; 

	/**
	 * Method loadChannelUserDetails. This method load user information by his
	 * msisdn
	 * 
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChannelUserDetails(Connection con, String msisdn) throws BTSLBaseException {

		final String methodName = "loadChannelUserDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Msisdn =");
        	loggerValue.append(msisdn);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtSelect = null;
		ChannelUserVO channelUserVO = null;
		final Date curDate = new Date();
		ResultSet rs1 = null;
		ResultSet rs = null;
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		try {
			
			 String selectQuery = null;
			  String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
		        boolean tcpOn = false;
		        Set<String> uniqueTransProfileId = new HashSet();
		        
			if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
				tcpOn = true;
			}
			String sqlSelect = null;
			

			
			if (tcpOn) {
				selectQuery = channelUserQry.loadChannelUserDetailsTcpQry();
	        	
			} else {

				selectQuery = channelUserQry.loadChannelUserDetailsQry();
			}
			/*final String select = new StringBuilder("select user_id from users where msisdn=? and user_type='CHANNEL' AND status <> ? AND status <> ? ").toString();
			int i =1;
			final String selectQuery = channelUserQry.loadChannelUserDetailsQry();
			try(PreparedStatement pstmtSelect1 = con.prepareStatement(select);)
			{
			pstmtSelect1.setString(i++, msisdn);
			pstmtSelect1.setString(i++, PretupsI.USER_STATUS_DELETED);
			pstmtSelect1.setString(i++, PretupsI.USER_STATUS_CANCELED);
			rs1 = pstmtSelect1.executeQuery();
			String user_id=null; 
			if(rs1.next()){
				user_id=rs1.getString(1);
			}*/
			int i=1;
			pstmtSelect = con.prepareStatement(selectQuery);
			pstmtSelect.setString(i++, msisdn);
			pstmtSelect.setString(i++, PretupsI.USER_TYPE_CHANNEL);
			pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(i++, PretupsI.USER_STATUS_CANCELED);
			pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(curDate));
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("Before Result =");
	        	loggerValue.append(msisdn);
	            LOG.debug(methodName, loggerValue);
			}
			rs = pstmtSelect.executeQuery();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("After Result =");
	        	loggerValue.append(msisdn);
	            LOG.debug(methodName, loggerValue);
			}
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("webpassword"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setDomainName(rs.getString("domain_name"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				if(tcpOn) {
				
				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.EQUALS, rs.getString("transfer_profile_id"),
						ValueType.STRING, null);
				java.util.List<HashMap<String, String>>  resultSet  = BTSLUtil.invokeService("TRANSFER_PROFILE", new HashSet<String>(Arrays.asList("profile_id","profile_name","status")), searchCriteria);
				
				
				
				channelUserVO.setTransferProfileName(resultSet.get(0).get("profileName"));
				channelUserVO.setTransferProfileStatus(resultSet.get(0).get("status"));
				}else {
				
				channelUserVO.setTransferProfileName(rs.getString("profile_name"));
				channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
				}
				
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
				channelUserVO.setDualCommissionType(rs.getString("last_dual_comm_type"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));

				// for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setAccessType(rs.getString("access_type"));
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				// end Zebra and Tango
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				// added by deepika aggarwal
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				// added by Praveen for autoc2cweb
				channelUserVO.setAutoc2callowed(rs.getString("auto_c2c_allow"));
				channelUserVO.setAutoo2callowed(rs.getString("auto_o2c_allow"));
				channelUserVO.setAutoO2CTxnValue(rs.getLong("autoo2c_transaction_amt"));
				channelUserVO.setAutoO2CThresholdLimit(rs.getLong("autoo2c_threshold_value"));
				if(rs.getLong("auto_c2c_quantity")==0l) {
					channelUserVO.setAutoc2cquantity(PretupsI.ZERO);	
				}else {	
				channelUserVO.setAutoc2cquantity(rs.getString("auto_c2c_quantity"));
				}
				if(channelSosEnable){
					channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
					channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
					channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));

				}
				
                if(SystemPreferences.USERWISE_LOAN_ENABLE){
                	
               	 PreparedStatement pstmtLoan = null;
                    ResultSet rsLoan = null;
                    LoginQry loginQry = (LoginQry)ObjectProducer.getObject(QueryConstants.LOGIN_QRY, QueryConstants.QUERY_PRODUCER);
                    
               	try {
               	  String sqlLoanBuffer = loginQry.loadUserLoanDetailsQry();
               	     pstmtLoan = con.prepareStatement(sqlLoanBuffer);
                        if (LOG.isDebugEnabled())
                            LOG.debug(methodName, "query:" + sqlLoanBuffer.toString());
                        int j = 0;
                        pstmtLoan.setString(++j, PretupsI.USER_STATUS_DELETED);
                        pstmtLoan.setString(++j, PretupsI.USER_STATUS_CANCELED);
                        pstmtLoan.setString(++j,channelUserVO.getUserID() );

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
                        OracleUtil.closeQuietly(pstmtLoan);
                        OracleUtil.closeQuietly(rsLoan);
               	}
               }

				
				
				
				
				if(lrEnabled){
					channelUserVO.setLrAllowed(rs.getString("lr_allowed"));
					channelUserVO.setLrMaxAmount(rs.getLong("lr_max_amount"));
				}
				//added for owner commission
				channelUserVO.setOwnerCategoryName(rs.getString("own_category_code"));
				channelUserVO.setOwnerMsisdn(rs.getString("own_msisdn"));
				// end added by deepika aggarwal
				channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
				channelUserVO.setSosAllowedAmount(rs.getInt("sos_allowed_amount"));
				channelUserVO.setSosThresholdLimit(rs.getInt("sos_threshold_limit"));
				channelUserVO.setGeographicalDesc(rs.getString("GRPH_DOMAIN_NAME"));
				channelUserVO.setEmail(rs.getString("email"));

				final Date passwordModifiedDate = rs.getTimestamp("pswd_modified_on");
				setPasswordModifiedDate(channelUserVO, passwordModifiedDate);

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("catseq"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setUserIdPrefix(rs.getString("USER_ID_PREFIX"));

				channelUserVO.setCategoryVO(categoryVO);

				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
				userPhoneVO.setMsisdn(msisdn);
				userPhoneVO.setUserId(rs.getString("user_id"));
				userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setPinRequired(rs.getString("pin_required"));
				userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				channelUserVO.setLanguage(rs.getString("phlang") + "_" + rs.getString("phcountry"));// added
				// by
				// deepika
				// aggarwal
				userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
				userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
				userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
				userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
				userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("userphone_created_on")));
				userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				userPhoneVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
				userPhoneVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setUserPhoneVO(userPhoneVO);
				// added for user level transfer rule
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setLongitude(rs.getString("LONGITUDE"));   
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(con, channelUserVO.getUserID()));
				channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setOwnerCompany(rs.getString("owner_company"));
                channelUserVO.setCommissionProfileApplicableFrom(rs.getDate("applicable_from"));
                channelUserVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
			}
			return channelUserVO;
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
        	loggerValue.append(SQL_EXCEPTION);
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);			
            LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
					"", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}

	}


	/**
	 * Method loadChannelUserDetails. This method load user information by 
	 * loginId
	 * 
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChannelUserDetailsByLoginId(Connection con, String loginId) throws BTSLBaseException {

		final String methodName = "loadChannelUserDetailsByLoginId";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: loginId =");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtSelect = null;
		ChannelUserVO channelUserVO = null;
		final Date curDate = new Date();
		ResultSet rs1 = null;
		ResultSet rs = null;
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		try {
			final String select = new StringBuilder("select user_id from users where login_id=? and user_type='CHANNEL' AND status <> ? AND status <> ? ").toString();
			int i =1;
			String selectQuery = null;
			
			String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
			boolean tcpOn = false;
			Set<String> uniqueTransProfileId = new HashSet();

			if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
				tcpOn = true;
			}
			String sqlSelect = null;

			if (tcpOn) {
				selectQuery = channelUserQry.loadChannelUserDetailsQryLoginIDTcp();
			} else {
				selectQuery = channelUserQry.loadChannelUserDetailsQryLoginID();
			}
			
			
			try(PreparedStatement pstmtSelect1 = con.prepareStatement(select);)
			{
			pstmtSelect1.setString(i++, loginId);
			pstmtSelect1.setString(i++, PretupsI.USER_STATUS_DELETED);
			pstmtSelect1.setString(i++, PretupsI.USER_STATUS_CANCELED);
			rs1 = pstmtSelect1.executeQuery();
			String user_id=null; 
			if(rs1.next()){
				user_id=rs1.getString(1);
			}
			i=1;
			pstmtSelect = con.prepareStatement(selectQuery);
			pstmtSelect.setString(i++, user_id);
			pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(i++, PretupsI.USER_STATUS_CANCELED);
			pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(curDate));
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("Before Result =");
	        	loggerValue.append(loginId);
	            LOG.debug(methodName, loggerValue);
			}
			rs = pstmtSelect.executeQuery();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("After Result =");
	        	loggerValue.append(loginId);
	            LOG.debug(methodName, loggerValue);
			}
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("webpassword"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));

					if (tcpOn) {

						SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.EQUALS,
								new HashSet<String>(Arrays.asList(rs.getString("transfer_profile_id"))),
								ValueType.STRING);
						java.util.List<HashMap<String, String>> resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE",
								new HashSet<String>(Arrays.asList("profile_id","profile_name", "status")), searchCriteria);

						
						channelUserVO.setTransferProfileStatus(resultSet.get(0).get("status"));
					} else {
						channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
					}
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setDualCommissionType(rs.getString("last_dual_comm_type"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));

				// for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setAccessType(rs.getString("access_type"));
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				// end Zebra and Tango
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				// added by deepika aggarwal
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				// added by Praveen for autoc2cweb
				channelUserVO.setAutoc2callowed(rs.getString("auto_c2c_allow"));
				channelUserVO.setAutoc2cquantity(rs.getString("auto_c2c_quantity"));
				if(channelSosEnable){
					channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
					channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
					channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
				}
				if(lrEnabled){
					channelUserVO.setLrAllowed(rs.getString("lr_allowed"));
					channelUserVO.setLrMaxAmount(rs.getLong("lr_max_amount"));
				}
				//added for owner commission
				channelUserVO.setOwnerCategoryName(rs.getString("own_category_code"));
				channelUserVO.setOwnerMsisdn(rs.getString("own_msisdn"));
				// end added by deepika aggarwal
				channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
				channelUserVO.setSosAllowedAmount(rs.getInt("sos_allowed_amount"));
				channelUserVO.setSosThresholdLimit(rs.getInt("sos_threshold_limit"));
				channelUserVO.setGeographicalDesc(rs.getString("GRPH_DOMAIN_NAME"));
				channelUserVO.setEmail(rs.getString("email"));

				final Date passwordModifiedDate = rs.getTimestamp("pswd_modified_on");
				setPasswordModifiedDate(channelUserVO, passwordModifiedDate);

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("catseq"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setUserIdPrefix(rs.getString("USER_ID_PREFIX"));

				channelUserVO.setCategoryVO(categoryVO);

				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
				//userPhoneVO.setMsisdn(msisdn);
				userPhoneVO.setUserId(rs.getString("user_id"));
				userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setPinRequired(rs.getString("pin_required"));
				userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				channelUserVO.setLanguage(rs.getString("phlang") + "_" + rs.getString("phcountry"));// added
				// by
				// deepika
				// aggarwal
				userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
				userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
				userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
				userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
				userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("userphone_created_on")));
				userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				userPhoneVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
				userPhoneVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setUserPhoneVO(userPhoneVO);
				// added for user level transfer rule
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setLongitude(rs.getString("LONGITUDE"));   
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(con, channelUserVO.getUserID()));
				channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setOwnerCompany(rs.getString("owner_company"));
                channelUserVO.setCommissionProfileApplicableFrom(rs.getDate("applicable_from"));
                channelUserVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
			}
			return channelUserVO;
		}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
        	loggerValue.append(SQL_EXCEPTION);
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);			
            LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
					"", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}

	}

	/**
	 * @param channelUserVO
	 * @param passwordModifiedDate
	 */
	private void setPasswordModifiedDate(ChannelUserVO channelUserVO, Date passwordModifiedDate) {
		final String methodName = "setPasswordModifiedDate";
		try {
			channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(passwordModifiedDate));
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
			channelUserVO.setPasswordModifiedOn(null);
		}
	}
	
	/**
	 * Method for loading Users Assigned Services List(means Services that are
	 * assigned to the user). From the table USER_SERVICES
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param userId
	 *            String
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 * 
	 */
	public ArrayList loadUserServicesList(Connection con, String userId) throws BTSLBaseException {

		final String methodName = "loadUserServicesList";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:UserId =");
        	loggerValue.append(BTSLUtil.maskParam(userId));
            LOG.debug(methodName, loggerValue);
        }
		
		final StringBuilder strBuff = new StringBuilder();
		// Modification for Service Management [by Vipul]
		strBuff.append(" SELECT US.service_type,US.status FROM user_services US,users U,category_service_type CST");
		strBuff.append(" WHERE US.user_id = ? AND US.status <> 'N'");
		strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");

		final String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Select Query=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName, loggerValue);
		}
		final ArrayList list = new ArrayList();
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			pstmt.setString(1, userId);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				list.add(new ListValueVO(rs.getString("status"), rs.getString("service_type")));
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
        	loggerValue.append(SQL_EXCEPTION);
        	loggerValue.append(sqe.getMessage());
            LOG.error(methodName, loggerValue);			
            LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userServicesList size:");
				loggerValue.append(list.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return list;
	}

	/**
	 * Method markRequestUnderProcess. This method mark Request as under process
	 * for the sender MSISDN.
	 * 
	 * @param con
	 *            Connection
	 * @param requestID
	 *            String
	 * @param userPhonesVO
	 *            UserPhoneVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int markRequestUnderProcess(Connection con, String requestID, UserPhoneVO userPhonesVO) throws BTSLBaseException {
		final String methodName = "markRequestUnderProcess";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: requestID =");
        	loggerValue.append(requestID);
        	loggerValue.append(" p_userphonesID:");
        	loggerValue.append(userPhonesVO.getUserPhonesId());
        	loggerValue.append(" MSISDN:");
        	loggerValue.append(userPhonesVO.getMsisdn());
            LOG.debug(methodName, loggerValue);
        }
		
		int updateCount = 0;
		final String updateQuery = "UPDATE user_phones SET last_transaction_status=?,modified_by=?, modified_on=?, last_access_on=? WHERE user_phones_id=? ";
		try(PreparedStatement pstmtUpdate = con.prepareStatement(updateQuery);) {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("Update Query=");
	        	loggerValue.append(updateQuery);
	            LOG.debug(methodName, loggerValue);
			}
			
			pstmtUpdate.setString(1, PretupsI.TXN_STATUS_UNDER_PROCESS);
			pstmtUpdate.setString(2, userPhonesVO.getModifiedBy());
			pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(userPhonesVO.getModifiedOn()));
			pstmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(userPhonesVO.getLastAccessOn()));
			pstmtUpdate.setString(5, userPhonesVO.getUserPhonesId());
			updateCount = pstmtUpdate.executeUpdate();
			return updateCount;
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
        	loggerValue.append(SQL_EXCEPTION);
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[markRequestUnderProcess]",
					requestID, userPhonesVO.getMsisdn(), "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[markRequestUnderProcess]",
					requestID, userPhonesVO.getMsisdn(), "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting requestID:");
				loggerValue.append(requestID);
				loggerValue.append(" updateCount:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
	}

	/**
	 * Method unmarkRequestUnderProcess. This method marks Request as completed
	 * for the sender MSISDN.
	 * 
	 * @param con
	 *            Connection
	 * @param requestID
	 *            String
	 * @param userPhonesVO
	 *            UserPhoneVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int unmarkRequestUnderProcess(Connection con, String requestID, UserPhoneVO userPhonesVO) throws BTSLBaseException {
		final String methodName = "unmarkRequestUnderProcess";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: requestID =");
        	loggerValue.append(requestID);
        	loggerValue.append(" MSISDN:");
        	loggerValue.append(userPhonesVO.getMsisdn());
            LOG.debug(methodName, loggerValue);
        }
		
		int updateCount = 0;
		try {
			final String updateQuery = "UPDATE user_phones SET last_transfer_id=?,last_transfer_type=?, last_transaction_on=?,last_transaction_status=?,phone_language=?,country=?,temp_transfer_id=?,modified_by=?, modified_on=? WHERE user_phones_id=? ";
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}
			try (PreparedStatement pstmtUpdate = con.prepareStatement(updateQuery);)
			{
			if (!BTSLUtil.isNullString(userPhonesVO.getLastTransferID())) {
				pstmtUpdate.setString(1, userPhonesVO.getLastTransferID());
			} else {
				pstmtUpdate.setNull(1, Types.VARCHAR);
			}
			if (!BTSLUtil.isNullString(userPhonesVO.getLastTransferType())) {
				pstmtUpdate.setString(2, userPhonesVO.getLastTransferType());
			} else {
				pstmtUpdate.setNull(2, Types.VARCHAR);
			}
			pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(userPhonesVO.getLastTransactionOn()));
			pstmtUpdate.setString(4, PretupsI.TXN_STATUS_COMPLETED);
			pstmtUpdate.setString(5, userPhonesVO.getPhoneLanguage());
			pstmtUpdate.setString(6, userPhonesVO.getCountry());
			pstmtUpdate.setString(7, userPhonesVO.getTempTransferID());
			pstmtUpdate.setString(8, userPhonesVO.getModifiedBy());
			pstmtUpdate.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(userPhonesVO.getModifiedOn()));
			pstmtUpdate.setString(10, userPhonesVO.getUserPhonesId());
			updateCount = pstmtUpdate.executeUpdate();
			return updateCount;
		} 
	}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);			
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[unmarkRequestUnderProcess]",
					requestID, userPhonesVO.getMsisdn(), "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[unmarkRequestUnderProcess]",
					requestID, userPhonesVO.getMsisdn(), "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
		
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Exiting requestID:");
				loggerValue.append(requestID);
				loggerValue.append(" updateCount:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
	}

	/**
	 * Method updateSmsPinCounter. This method updates the Invalid PIN counters
	 * for the MSISDN
	 * 
	 * @param con
	 *            Connection
	 * @param userPhoneVO
	 *            UserPhoneVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int updateSmsPinCounter(Connection con, UserPhoneVO userPhoneVO) throws BTSLBaseException {
		final String methodName = "updateSmsPinCounter";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userPhoneVO =");
        	loggerValue.append(userPhoneVO);
            LOG.debug(methodName, loggerValue);
        }
		 
		int updateCount = 0;
		try {
			int i = 1;
			final StringBuilder updateQueryBuff = new StringBuilder("UPDATE user_phones SET invalid_pin_count = ?, first_invalid_pin_time=? ,modified_by =?, modified_on =?   ");
			if (userPhoneVO.getPinReset() != null) {
				updateQueryBuff.append(", PIN_RESET=? ");
			}
			updateQueryBuff.append("WHERE user_phones_id=? ");
			final String selectUpdate = updateQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectUpdate);
				LOG.debug(methodName, loggerValue);
			}
			try(PreparedStatement pstmtUpdate = con.prepareStatement(selectUpdate);)
			{
			pstmtUpdate.setInt(i, userPhoneVO.getInvalidPinCount());
			i++;
			if (userPhoneVO.getFirstInvalidPinTime() != null) {
				pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getFirstInvalidPinTime()));
				i++;
			} else {
				pstmtUpdate.setNull(i, Types.TIMESTAMP);
				i++;
			}
			pstmtUpdate.setString(i, userPhoneVO.getModifiedBy());
			i++;
			pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getModifiedOn()));
			i++;
			if (userPhoneVO.getPinReset() != null) {
				pstmtUpdate.setString(i, userPhoneVO.getPinReset());
				i++;
			}

			pstmtUpdate.setString(i, userPhoneVO.getUserPhonesId());
			i++;
			updateCount = pstmtUpdate.executeUpdate();
			return updateCount;
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);

			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateSmsPinCounter]", "",
					userPhoneVO.getMsisdn(), "", "Not able to update the Invalid PIN Count SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateSmsPinCounter]", "",
					userPhoneVO.getMsisdn(), "", "Not able to update the Invalid PIN Count Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append(" updateCount:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
	}

	/**
	 * Method loadUsersDetails. This method is used to load all the information
	 * used to display in the ICCID MSISDN KEY MANAGEMENT Module
	 * 
	 * @author sandeep.goel
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @param userId
	 *            String
	 * @param statusUsed
	 *            String
	 * @param status
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadUsersDetails(Connection con, String msisdn, String userId, String statusUsed, String status) throws BTSLBaseException {
		final String methodName = "loadUsersDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:msisdn =");
        	loggerValue.append(msisdn);
        	loggerValue.append("userId=");
        	loggerValue.append(userId);
        	loggerValue.append(" stausUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" staus=");
        	loggerValue.append(status);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		ChannelUserVO channelUserVO = null;
		boolean isMsisdnAssociationReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ);
		try {
			pstmtSelect = channelUserQry.loadUsersDetailsQry(con, status, userId, statusUsed, msisdn);
			rs = pstmtSelect.executeQuery();
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setBatchID(rs.getString("batch_id"));
				channelUserVO.setCreationType(rs.getString("creation_type"));
				channelUserVO.setUserID(rs.getString("usr_user_id"));
				channelUserVO.setUserName(rs.getString("usr_user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password1"));
				channelUserVO.setCategoryCode(rs.getString("usr_category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setReferenceID(rs.getString("reference_id"));
				channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("usr_status"));
				channelUserVO.setEmail(rs.getString("email"));
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				channelUserVO.setLanguage(rs.getString("planguage") + "_" + rs.getString("pcountry"));
				// end added by deepika aggarwal
				channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
				channelUserVO.setContactNo(rs.getString("contact_no"));
				channelUserVO.setDesignation(rs.getString("designation"));
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("usr_msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(rs.getTimestamp("created_on"));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
				channelUserVO.setLastModified(rs.getTimestamp("modified_on").getTime());
				channelUserVO.setAddress1(rs.getString("address1"));
				channelUserVO.setAddress2(rs.getString("address2"));
				channelUserVO.setCity(rs.getString("city"));
				channelUserVO.setState(rs.getString("state"));
				channelUserVO.setCountry(rs.getString("country"));
				channelUserVO.setRsaFlag(rs.getString("rsaflag"));
				channelUserVO.setSsn(rs.getString("ssn"));
				channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
				channelUserVO.setExternalCode(rs.getString("external_code"));
				channelUserVO.setShortName(rs.getString("short_name"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
				channelUserVO.setUserCode(rs.getString("user_code"));
				channelUserVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				channelUserVO.setLongitude(rs.getString("LONGITUDE")); 
				// 5.1.3
				channelUserVO.setDomainName(rs.getString("domain_name"));
				channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
				categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setFixedRoles(rs.getString("fixed_roles"));
				categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
				categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setMaxTxnMsisdn(rs.getInt("max_txn_msisdn") + "");
				categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
				categoryVO.setServiceAllowed(rs.getString("services_allowed"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				// Added for Authentication Type
				categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
				channelUserVO.setCategoryVO(categoryVO);

				channelUserVO.setParentName(rs.getString("parent_name"));
				channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
				channelUserVO.setParentCategoryName(rs.getString("parent_cat"));

				channelUserVO.setOwnerName(rs.getString("owner_name"));
				channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
				channelUserVO.setOwnerCategoryName(rs.getString("owner_cat"));

				channelUserVO.setSmsPin(rs.getString("user_sms_pin"));
				channelUserVO.setPinRequired(rs.getString("required"));
				channelUserVO.setStatusDesc(rs.getString("lookup_name"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
				channelUserVO.setRequetedByUserName(rs.getString("request_user_name"));
				if(!BTSLUtil.isNullString(rs.getString("request_user_name")))
					channelUserVO.setSuspendedByUserName(rs.getString("request_user_name"));
				else
					channelUserVO.setSuspendedByUserName(PretupsI.SYSTEM);

				channelUserVO.setCreatedByUserName(rs.getString("created_by_name"));

				// for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setAccessType(rs.getString("user_access_type"));
				// end Zebra and Tango

				channelUserVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn())));
				if (isMsisdnAssociationReq) {
					channelUserVO.setAssoMsisdn(rs.getString("ASSOCIATED_MSISDN"));
					channelUserVO.setAssType(rs.getString("ASSOCIATED_MSISDN_TYPE"));
					channelUserVO.setAssociationCreatedOn(rs.getTimestamp("ASSOCIATED_MSISDN_CDATE"));
					channelUserVO.setAssociationModifiedOn(rs.getTimestamp("ASSOCIATED_MSISDN_MDATE"));
				}
				channelUserVO.setCategoryName(rs.getString("category_name"));
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	/**
	 * Method loadUsersDetailsByLoginId. This method is used to load all the
	 * information of the user on the basis of LoginId
	 * 
	 * @author mohit.goel
	 * @param con
	 *            Connection
	 * @param loginId
	 *            String
	 * @param userId
	 *            String(If operator user userId = null else userId = session
	 *            user id(in case of channel user)
	 * @param statusUsed
	 *            String
	 * @param status
	 *            String
	 * 
	 * 
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadUsersDetailsByLoginId(Connection con, String loginId, String userId, String statusUsed, String status) throws BTSLBaseException {
		final String methodName = "loadUsersDetailsByLoginId";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: LoginId =");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
        	loggerValue.append(" userId=");
        	loggerValue.append(BTSLUtil.maskParam(userId));
        	loggerValue.append(" stausUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" staus=");
        	loggerValue.append(status);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmt1 = null;
		boolean isMsisdnAssociationReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ);
		ChannelUserVO channelUserVO = null;

		StringBuilder strBuffer = new StringBuilder("select user_id,user_name,login_id,msisdn,u.category_code,u.status,cat.category_name,cat.category_code as parentCategoryCode from users u,categories cat ");
		strBuffer.append("where user_id in (?,?,?,?) and u.category_code=cat.category_code");
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(strBuffer.toString());
			LOG.debug(methodName, loggerValue);
		}

		try {
			pstmtSelect = channelUserQry.loadUsersDetailsByLoginIdQry(con, status, userId, statusUsed, loginId);
			try (ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setBatchID(rs.getString("batch_id"));
				channelUserVO.setCreationType(rs.getString("creation_type"));
				channelUserVO.setUserID(rs.getString("usr_user_id"));
				channelUserVO.setUserName(rs.getString("usr_user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password1"));
				channelUserVO.setCategoryCode(rs.getString("usr_category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setReferenceID(rs.getString("reference_id"));
				channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("usr_status"));
				channelUserVO.setEmail(rs.getString("email"));
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				channelUserVO.setLanguage(rs.getString("phone_language") + "_" + rs.getString("phcountry"));
				// end added by deepika aggarwal

				channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
				channelUserVO.setContactNo(rs.getString("contact_no"));
				channelUserVO.setContactPerson(rs.getString("contact_person"));
				channelUserVO.setDesignation(rs.getString("designation"));
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("usr_msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(rs.getTimestamp("created_on"));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
				channelUserVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
				channelUserVO.setAddress1(rs.getString("address1"));
				channelUserVO.setAddress2(rs.getString("address2"));
				channelUserVO.setCity(rs.getString("city"));
				channelUserVO.setState(rs.getString("state"));
				channelUserVO.setCountry(rs.getString("country"));
				channelUserVO.setRsaFlag(rs.getString("rsaflag"));
				channelUserVO.setSsn(rs.getString("ssn"));
				channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
				channelUserVO.setExternalCode(rs.getString("external_code"));
				channelUserVO.setShortName(rs.getString("short_name"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
				channelUserVO.setUserCode(rs.getString("user_code"));
				channelUserVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				// Added for Authetication Type
				channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
				channelUserVO.setDomainName(rs.getString("domain_name"));
				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
				categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setFixedRoles(rs.getString("fixed_roles"));
				categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
				categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setMaxTxnMsisdn(rs.getInt("max_txn_msisdn") + "");
				categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
				categoryVO.setServiceAllowed(rs.getString("services_allowed"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				// Added For authentication Type
				categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
				channelUserVO.setCategoryVO(categoryVO);

				pstmt1 = con.prepareStatement(strBuffer.toString());
				pstmt1.setString(1, channelUserVO.getParentID());
				pstmt1.setString(2, channelUserVO.getOwnerID());
				pstmt1.setString(3, channelUserVO.getCreatedBy());
				pstmt1.setString(4, channelUserVO.getModifiedBy());
				try(ResultSet rs1 = pstmt1.executeQuery();)
				{
				while(rs1.next()) {
					String uid = rs1.getString("user_id");
					if(PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentID())){
						channelUserVO.setParentName(PretupsI.ROOT_PARENT_ID);
					}
					if(PretupsI.SYSTEM.equals(channelUserVO.getParentID()))
						channelUserVO.setParentName(PretupsI.SYSTEM);
					if(channelUserVO.getParentID().equals(uid)){
						channelUserVO.setParentName(rs1.getString("user_name"));
						channelUserVO.setParentMsisdn(rs1.getString("msisdn"));	
						channelUserVO.setParentCategoryName(rs1.getString("category_name"));
						channelUserVO.setParentCategoryCode(rs1.getString("parentCategoryCode"));
						channelUserVO.setParentLoginId(rs1.getString("login_id"));

					}						
					if(channelUserVO.getOwnerID().equals(uid)) {
						channelUserVO.setOwnerName(rs1.getString("user_name"));
						channelUserVO.setOwnerMsisdn(rs1.getString("msisdn"));
						channelUserVO.setOwnerCategoryName(rs1.getString("category_name"));
						channelUserVO.setOwnerCategoryCode(rs1.getString("parentCategoryCode"));
						channelUserVO.setOwnerLoginId(rs1.getString("login_id"));

					}
					if(channelUserVO.getCreatedBy().equals(uid)) {
						channelUserVO.setCreatedByUserName(rs1.getString("user_name"));  
					}
					if(channelUserVO.getModifiedBy().equals(uid)) {
						channelUserVO.setRequetedByUserName(rs1.getString("user_name"));
						channelUserVO.setSuspendedByUserName(rs1.getString("user_name"));
					}
					else if(channelUserVO.getModifiedBy().equals(PretupsI.SYSTEM)) {
						channelUserVO.setSuspendedByUserName(PretupsI.SYSTEM);
					}

				}
				channelUserVO.setStatusDesc(rs.getString("lookup_name"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
				channelUserVO.setParentGeographyCode(rs.getString("parent_grph_domain_code"));
				channelUserVO.setAllowedUserTypeCreation(rs.getString("allowd_usr_typ_creation"));

				channelUserVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn())));
				if (isMsisdnAssociationReq) {
					channelUserVO.setAssoMsisdn(rs.getString("ASSOCIATED_MSISDN"));
					channelUserVO.setAssType(rs.getString("ASSOCIATED_MSISDN_TYPE"));
					channelUserVO.setAssociationCreatedOn(rs.getTimestamp("ASSOCIATED_MSISDN_CDATE"));
					channelUserVO.setAssociationModifiedOn(rs.getTimestamp("ASSOCIATED_MSISDN_MDATE"));
				}
			}
		} 
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsByLoginId]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsByLoginId]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
		
			try {
                if (pstmt1 != null) {
                	pstmt1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmtSelect != null) {
                	pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	/**
	 * Method isPhoneExists This method will return true or false, if mobile
	 * number is available in the database
	 * 
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @return boolean
	 * @throws BTSLBaseException
	 */

	public boolean isPhoneExists(Connection con, String msisdn) throws BTSLBaseException {
		final String methodName = "isPhoneExists";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn=");
        	loggerValue.append(msisdn);
            LOG.debug(methodName, loggerValue);
        }

		
		boolean found = false;

		final StringBuilder strBuff = new StringBuilder("select UP.MSISDN,U.status");
		strBuff.append(" FROM user_phones UP,users U WHERE UP.msisdn = ?");
		strBuff.append(" AND U.user_id=UP.user_id AND (U.status <> 'N' AND U.status <> 'C')");

		try(PreparedStatement pstmt = con.prepareStatement(strBuff.toString());) {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(strBuff.toString());
				LOG.debug(methodName, loggerValue);
			}
			
			pstmt.setString(1, msisdn);
			try(ResultSet rs = pstmt.executeQuery();){
			if (rs.next()) {
				found = true;
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			found = false;
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[isPhoneExists]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "loadLatestSIMServiceListForSearch", "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			found = false;
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[isPhoneExists]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting found:");
				loggerValue.append(found);
				LOG.debug(methodName, loggerValue);
			}
		}

		return found;
	}

	/**
	 * This method will update the last transaction id filed of the user_phones
	 * table
	 * 
	 * @param con
	 *            Connection
	 * @param p_transactionId
	 *            String
	 * @param p_usrMsisdn
	 *            String
	 * @return the number of records updated
	 * @throws BTSLBaseException
	 */

	public int updateTransactionId(Connection con, String p_transactionId, String p_usrMsisdn) throws BTSLBaseException {
		final String methodName = "updateTransactionId";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_usrMsisdn =");
        	loggerValue.append(p_usrMsisdn);
        	loggerValue.append(" p_transactionId=");
        	loggerValue.append(p_transactionId);
            LOG.debug(methodName, loggerValue);
        }
		 
		final String qry = "UPDATE user_phones SET temp_transfer_id =? WHERE msisdn=? ";
		int updCount = 0;
		try (PreparedStatement pstmt = con.prepareStatement(qry);) {
			
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(qry);
				LOG.debug(methodName, loggerValue);
			}
			
			// Get Preapared Statement
			
			pstmt.setString(1, p_transactionId);
			pstmt.setString(2, p_usrMsisdn);
			// Execute Query
			updCount = pstmt.executeUpdate();
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateTransactionId]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateTransactionId]", "", "",
					"", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting update count:");
				loggerValue.append(updCount);
				LOG.debug(methodName, loggerValue);
			}
		}

		return updCount;
	}

	/**
	 * this method used to Change the user pin basis of msisdn and user_id
	 * 
	 * @param con
	 * @param p_smsPin
	 *            String
	 * @param channelUserVO
	 *            ChannelUserVO
	 * @return int
	 * @throws BTSLBaseException
	 * @author manoj kumar
	 */
	public int changePin(Connection con, String p_smsPin, ChannelUserVO channelUserVO) throws BTSLBaseException {
		final String methodName = "changePin";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_smsPin =");
        	loggerValue.append(p_smsPin);
        	loggerValue.append(" subscriberVO=");
        	loggerValue.append(channelUserVO);
            LOG.debug(methodName, loggerValue);
        }
		
		int updateCount = 0;
		try {
			final StringBuilder strBuff = new StringBuilder(" UPDATE user_phones set sms_pin = ?,  ");
			strBuff.append("  modified_by = ? , modified_on = ?, pin_modified_on=?, PIN_RESET=?,last_access_on=? WHERE msisdn = ? AND user_id =? ");
			final String query = strBuff.toString();
			if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Query =");
					loggerValue.append(query);
					LOG.debug(methodName, loggerValue);
			}
			try(PreparedStatement psmt = con.prepareStatement(query);)
			{
			psmt.setString(1, BTSLUtil.encryptText(p_smsPin));
			psmt.setString(2, channelUserVO.getModifiedBy());
			psmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
			psmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
			psmt.setString(5, PretupsI.NO);
			psmt.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getUserPhoneVO().getLastAccessOn()));
			psmt.setString(7, channelUserVO.getUserPhoneVO().getMsisdn());
			psmt.setString(8, channelUserVO.getUserPhoneVO().getUserId());
			updateCount = psmt.executeUpdate();
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changePin]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changePin]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting update count:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updateCount;
	}

	/**
	 * This method load the channelUserInformation on the basis of UserId
	 * 
	 * @author mohit.goel
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param userId
	 *            String
	 * 
	 * @return ChannelUserVO channelUserVO
	 * @throws BTSLBaseException
	 * 
	 */
	public ChannelUserVO loadChannelUser(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadChannelUser";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId =");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);

		ResultSet rs = null;
		ChannelUserVO channelUserVO = new ChannelUserVO();

		final StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT cu.user_grade,");
		strBuff.append("cu.contact_person,cu.transfer_profile_id, cu.comm_profile_set_id,");
		strBuff.append("cu.in_suspend, cu.out_suspend,cu.outlet_code,cu.suboutlet_code, ");

		// for Zebra and Tango by sanjeew date 06/07/07
		strBuff.append("cu.application_id, cu.mpay_profile_id, cu.user_profile_id, ");
		strBuff.append(" cu.is_primary, cu.mcommerce_service_allow, cu.low_bal_alert_allow, ");
		strBuff.append(" u.category_code,c.low_bal_alert_allow catlowbalallow,c.outlets_allowed");
		strBuff.append(" ,u.level1_approved_by,u.level1_approved_on,u.level2_approved_by,u.level2_approved_on");
		// added by nilesh:for user profile updation based on langitude and
		// latitude
		strBuff.append(" ,u.longitude,u.latitude,u.document_type,u.document_no,u.payment_type");
		// Added by Amit Raheja for alert type
		strBuff.append(" ,cu.alert_type,cu.alert_email,u.authentication_allowed, u.msisdn ");
		// added by gaurav for transfer rule type
		if (isTrfRuleUserLevelAllow) {
			strBuff.append(", cu.trf_rule_type");
		}
		// Added by Aatif
		if (lmsAppl) {
			strBuff.append(", cu.lms_profile ");
		}
		if (optInOutAllow) {
			strBuff.append(" , cu.OPT_IN_OUT_STATUS ");
		}
		strBuff.append(" , cu.CONTROL_GROUP, cu.SOS_ALLOWED_AMOUNT, u.PARENT_ID, u.OWNER_ID, u.STATUS, u.user_id, (SELECT cg.grade_name FROM CHANNEL_GRADES cg WHERE cg.GRADE_CODE=cu.USER_GRADE) AS grade_name");
		strBuff.append(" FROM channel_users cu,users u,categories c ");
		// end Zebra and Tango

		strBuff.append(" WHERE cu.user_id = ? and cu.user_id=u.user_id and u.category_code=c.category_code");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		try(PreparedStatement  pstmt = con.prepareStatement(sqlSelect);) {
			
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setUserGradeName(rs.getString("grade_name"));
				channelUserVO.setContactPerson(rs.getString("contact_person"));
				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setInSuspend(rs.getString("in_suspend"));
				channelUserVO.setOutSuspened(rs.getString("out_suspend"));
				channelUserVO.setOutletCode(rs.getString("outlet_code"));
				channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
				channelUserVO.setCatLowBalanceAlertAllow(rs.getString("catlowbalallow"));
				channelUserVO.setCatOutletAllowed(rs.getString("outlets_allowed"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setLongitude(rs.getString("longitude"));
				channelUserVO.setLatitude(rs.getString("latitude"));
				channelUserVO.setDocumentType(rs.getString("document_type"));
				channelUserVO.setDocumentNo(rs.getString("document_no"));
				channelUserVO.setPaymentType(rs.getString("payment_type"));
				channelUserVO.setPaymentTypes(rs.getString("payment_type"));
				channelUserVO.setAlertType(rs.getString("alert_type"));
				channelUserVO.setAlertEmail(rs.getString("alert_email"));
				channelUserVO.setOutletCode(rs.getString("outlet_code"));
				channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
				channelUserVO.setSosAllowedAmount(rs.getLong("SOS_ALLOWED_AMOUNT"));
				channelUserVO.setParentID(rs.getString("PARENT_ID"));
				channelUserVO.setOwnerID(rs.getString("OWNER_ID"));
				channelUserVO.setStatus(rs.getString("STATUS"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				channelUserVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));;
				
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUser]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUser]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	/**
	 * Method for inserting Channel User Info in channelUsers table.
	 * 
	 * @author mohit.goel
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param channelUserVO
	 *            ChannelUserVO
	 * @return insertCount int
	 * @throws BTSLBaseException
	 * 
	 */
	public int addChannelUser(Connection con, ChannelUserVO channelUserVO) throws BTSLBaseException {
		
		int insertCount = 0;
		final String methodName = "addChannelUser";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelUserVO =");
        	loggerValue.append(channelUserVO);
            LOG.debug(methodName, loggerValue);
        }
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("INSERT INTO channel_users (user_id,user_grade,");
			strBuff.append("contact_person,transfer_profile_id, comm_profile_set_id,");
			strBuff.append("in_suspend, out_suspend,outlet_code,suboutlet_code,activated_on, ");

			// for Zebra and Tango by sanjeew date 06/07/07
			strBuff.append(" user_profile_id, mcommerce_service_allow, mpay_profile_id, low_bal_alert_allow, ");
			// Added by Amit Raheja for alerts
			strBuff.append(" alert_email,alert_type");
			// added by gaurav for trf rule type
			if (lmsAppl) {
				strBuff.append(", lms_profile ");
				strBuff.append(", lms_profile_updated_on ");
				strBuff.append(", CONTROL_GROUP ");
			}
			if (isTrfRuleUserLevelAllow) {
				strBuff.append(", trf_rule_type ) ");
			} else {
				strBuff.append(")");
			}

			strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
			if (lmsAppl) {
				strBuff.append(",? ");
				strBuff.append(",? ");
				strBuff.append(",? ");
			}
			if (isTrfRuleUserLevelAllow) {
				strBuff.append(",?)");
			} else {
				strBuff.append(")");
				// End Zebra and Tango
			}

			final String insertQuery = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(insertQuery);
				LOG.debug(methodName, loggerValue);
			}
			int rCount = 1;
			try(PreparedStatement psmtInsert = con.prepareStatement(insertQuery);)
			{
			psmtInsert.setString(rCount, channelUserVO.getUserID());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getUserGrade());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getContactPerson());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getTransferProfileID());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getCommissionProfileSetID());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getInSuspend());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getOutSuspened());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getOutletCode());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getSubOutletCode());
			rCount++;
			if (channelUserVO.getActivatedOn() != null) {
				psmtInsert.setTimestamp(rCount, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
				rCount++;
			} else {
				psmtInsert.setTimestamp(rCount, null);
				rCount++;
			}

			// for Zebra and Tango by sanjeew date 06/07/07
			psmtInsert.setString(rCount, channelUserVO.getUserProfileID());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getMcommerceServiceAllow());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getMpayProfileID());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getLowBalAlertAllow());
			rCount++;
			// End Zebra and Tango
			psmtInsert.setString(rCount, channelUserVO.getAlertEmail());
			rCount++;
			psmtInsert.setString(rCount, channelUserVO.getAlertType());
			rCount++;
			if (lmsAppl) {
				psmtInsert.setString(rCount, channelUserVO.getLmsProfile());
				rCount++;
				if (BTSLUtil.isNullString(channelUserVO.getLmsProfile())) {
					psmtInsert.setTimestamp(rCount, null);
					rCount++;
				} else {
					psmtInsert.setTimestamp(rCount, BTSLUtil.getTimestampFromUtilDate(new Date()));
					rCount++;
				}
				psmtInsert.setString(rCount, channelUserVO.getControlGroup());
				rCount++;
			}
			// added for transfer rule type
			if (isTrfRuleUserLevelAllow) {
				psmtInsert.setString(rCount, channelUserVO.getTrannferRuleTypeId());
				rCount++;
			}

			insertCount = psmtInsert.executeUpdate();

		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[addChannelUser]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[addChannelUser]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
		
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting insert count:");
				loggerValue.append(insertCount);
				LOG.debug(methodName, loggerValue);
			}
		}

		return insertCount;
	}

	/**
	 * Method for Updating Channel User Info in channelUsers table.
	 * 
	 * @author mohit.goel
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param p_channleUserVO
	 *            ChannelUserVO
	 * @return updateCount int
	 * @throws BTSLBaseException
	 * 
	 */
	public int updateChannelUserInfo(Connection con, ChannelUserVO channelUserVO) throws BTSLBaseException {
		 
		int updateCount = 0;
		final String methodName = "updateChannelUserInfo";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_channleUserVO =");
        	loggerValue.append(channelUserVO);
            LOG.debug(methodName, loggerValue);
        }
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		try {
			//Handling of Expired profile, Channel user should not be de-associated automatically from lms profile
			boolean isActiveProfile =  true;
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "If value of isActiveProfile is false then it means that profile is expired.");
			}
			if(BTSLUtil.isNullString(channelUserVO.getLmsProfile())){
				ChannelUserVO channelUserLMSVO = loadChannelUser(con, channelUserVO.getUserID());
				channelUserVO.setControlGroup(channelUserLMSVO.getControlGroup());
				if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile())){
					isActiveProfile = isProfileActive(channelUserVO.getMsisdn(),channelUserLMSVO.getLmsProfile());
					if(isActiveProfile){
						channelUserVO.setLmsProfile(channelUserLMSVO.getLmsProfile());
					}
					if(LOG.isDebugEnabled()){
						loggerValue.setLength(0);
						loggerValue.append("isActiveProfile =");
						loggerValue.append(isActiveProfile);
						LOG.debug(methodName, loggerValue);
					}
				}
			} else {
				isActiveProfile=false;
			}
			//
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE channel_users SET ");
			strBuff.append("contact_person = ?,");
			strBuff.append("in_suspend = ?, out_suspend = ?,outlet_code = ?,suboutlet_code = ?, ");

			// for Zebra and Tango by sanjeew date 06/07/07
			strBuff.append("mpay_profile_id=?, user_profile_id=?, mcommerce_service_allow=?, low_bal_alert_allow=? ");
			// Added by Amit Raheja for alerts
			strBuff.append(" ,alert_email=? ,alert_type=? ");
			// End Zebra and Tango
			// Added by Aatif
			if (lmsAppl  && isActiveProfile) {
				strBuff.append(" ,lms_profile=? ");
				strBuff.append(" ,lms_profile_updated_on=? ");
				strBuff.append(", CONTROL_GROUP=? ");
			}
			strBuff.append(" ,user_grade=?");
			strBuff.append(" WHERE user_id = ?");

			final String insertQuery = strBuff.toString();
			if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Query =");
					loggerValue.append(insertQuery);
					LOG.debug(methodName, loggerValue);
			}
			try (PreparedStatement psmtUpdate = con.prepareStatement(insertQuery);)
			{
			int rno = 1;
			psmtUpdate.setString(rno, channelUserVO.getContactPerson());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getInSuspend());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getOutSuspened());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getOutletCode());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getSubOutletCode());
			rno++;

			// for Zebra and Tango by sanjeew date 06/07/07
			psmtUpdate.setString(rno, channelUserVO.getMpayProfileID());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getUserProfileID());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getMcommerceServiceAllow());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getLowBalAlertAllow());
			rno++;
			// End Zebra and Tango
			psmtUpdate.setString(rno, channelUserVO.getAlertEmail());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getAlertType());
			rno++;
			// Added by Aatif
			if (lmsAppl  && isActiveProfile) {
				psmtUpdate.setString(rno, channelUserVO.getLmsProfile());
				rno++;
				psmtUpdate.setTimestamp(rno, BTSLUtil.getTimestampFromUtilDate(new Date()));
				rno++;
				psmtUpdate.setString(rno, channelUserVO.getControlGroup());
				rno++;                
			}
			psmtUpdate.setString(rno, channelUserVO.getUserGrade());
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getUserID());
			rno++;

			updateCount = psmtUpdate.executeUpdate();

		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserInfo]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserInfo]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
	
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Update count:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updateCount;
	}

	/**
	 * Method loadChannelUserDetailsForTransfer() This method loads the user
	 * information on the basis of his userCode/userID(p_userIDCode) and by the
	 * boolean variable isUserCode (provided as input)
	 * 
	 * @param con
	 * @param p_userIDCode
	 * @param isUserCode
	 *            TODO
	 * @param p_applicableFromDate
	 *            Date
	 * @return ChannelUserVO
	 * @throws Exception
	 * @throws SQLException
	 */
	public ChannelUserVO loadChannelUserDetailsForTransfer(Connection con, String p_userIDCode, boolean isUserCode, Date p_applicableFromDate,boolean _isParentOwnerMsisdnRequired) throws Exception {
		final String methodName = "loadChannelUserDetailsForTransfer";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userIDCode =");
        	loggerValue.append(p_userIDCode);
        	loggerValue.append(" isUserCode=");
        	loggerValue.append(isUserCode);
        	loggerValue.append(" p_applicableFromDate=");
        	loggerValue.append(p_applicableFromDate);
        	loggerValue.append(" _isParentOwnerMsisdnRequired=");
        	loggerValue.append(_isParentOwnerMsisdnRequired);
            LOG.debug(methodName, loggerValue);
        }
		 
		 
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		ChannelUserVO channelUserVO = null;
		ArrayList<String> paymentTypesList = new ArrayList<>();
		HashMap<String, String> paymentLists=new HashMap<>();
		
		String qry = null;
		  String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
	        boolean tcpOn = false;
	        Set<String> uniqueTransProfileId = new HashSet();
	        
	        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
	        	tcpOn = true;
	        }
	        String sqlSelect = null;
	        
	        if(tcpOn) {
	        	qry = channelUserQry.loadChannelUserDetailsForTransferTcpQry(_isParentOwnerMsisdnRequired, isUserCode);
	        }else {
	        	qry = channelUserQry.loadChannelUserDetailsForTransferQry(_isParentOwnerMsisdnRequired, isUserCode);
	        }
		
		try(PreparedStatement pstmt = con.prepareStatement(qry);) {
			
			
			int i = 0;
			pstmt.setString(++i, p_userIDCode);
			pstmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_applicableFromDate));
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Befor Result =");
				loggerValue.append(p_userIDCode);
				LOG.debug(methodName, loggerValue);
			}
			try(ResultSet rs = pstmt.executeQuery();)
			{
				if(LOG.isDebugEnabled()){
					loggerValue.setLength(0);
					loggerValue.append("After Result =");
					loggerValue.append(p_userIDCode);
					LOG.debug(methodName, loggerValue);
				}
			String userID;
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				userID = rs.getString("user_id");
				channelUserVO.setUserID(userID);
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setNetworkName(rs.getString("network_name"));
				channelUserVO.setLoginID(p_userIDCode);
				channelUserVO.setWebLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setValidRequestURLs(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setLastLoginOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_login_on")));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setEmail(rs.getString("email"));
				channelUserVO.setPaymentTypes(rs.getString("payment_type"));;
				ArrayList paymentList=LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
                if(!BTSLUtil.isNullOrEmptyList(paymentList)&&!BTSLUtil.isNullString(channelUserVO.getPaymentTypes()))
                {
                	ArrayList<String> payList=new ArrayList<>();
                for(int l=0;l<paymentList.size();l++)
                {
                	payList.add(((ListValueVO)paymentList.get(l)).getValue());
                	paymentLists.put(((ListValueVO)paymentList.get(l)).getValue(), ((ListValueVO)paymentList.get(l)).getLabel());
                }
                String paymentTypes=channelUserVO.getPaymentTypes();
                if(paymentTypes!=null)
                {
                String []payTypes=paymentTypes.split(",");
                for(int k=0;k<payTypes.length;k++)
                {
                	if(payList.contains(payTypes[k]))
                	{
                		paymentTypesList.add(paymentLists.get(payTypes[k]));
                	}
                	}
                }
                }
                channelUserVO.setPaymentTypesList(paymentTypesList);
				if(_isParentOwnerMsisdnRequired)
				{
					channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
					channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
				}channelUserVO.setEmail(rs.getString("email"));
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				// end added by deepika aggarwal
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy("modified_by");
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
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
				channelUserVO.setMessage(rs.getString("language_1_message"));
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setDomainName(rs.getString("domain_name"));
				channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
				
				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				
				
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
				channelUserVO.setUserGradeName(rs.getString("grade_name"));
				channelUserVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
				channelUserVO.setOthCommSetId(rs.getString("OTH_COMM_PRF_SET_ID"));
				channelUserVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));

				
				
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
				channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
				
				
				
					if (tcpOn) {

						SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.EQUALS,
								rs.getString("transfer_profile_id"),
								ValueType.STRING, null);
						java.util.List<HashMap<String, String>> resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE",
								new HashSet<String>(Arrays.asList("profile_id","profile_name", "status")), searchCriteria);

						channelUserVO.setTransferProfileName(resultSet.get(0).get("profileName"));
						channelUserVO.setTransferProfileStatus(resultSet.get(0).get("status"));
					} else {
						channelUserVO.setTransferProfileName(rs.getString("profile_name"));
						channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
					}
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
				channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
				channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
				channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
				channelUserVO.setDualCommissionType(rs.getString("dual_comm_type"));
				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
				categoryVO.setFixedRoles(rs.getString("fixed_roles"));
				categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
				categoryVO.setMaxLoginCount(rs.getLong("max_login_count"));
				categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
				categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
			      if(SystemPreferences.USERWISE_LOAN_ENABLE){
	                	
	                	 PreparedStatement pstmtLoan = null;
	                     ResultSet rsLoan = null;
	                     LoginQry loginQry = (LoginQry)ObjectProducer.getObject(QueryConstants.LOGIN_QRY, QueryConstants.QUERY_PRODUCER);
	                     
	                	try {
	                	  String sqlLoanBuffer = loginQry.loadUserLoanDetailsQry();
	                	     pstmtLoan = con.prepareStatement(sqlLoanBuffer);
	                         if (LOG.isDebugEnabled())
	                             LOG.debug(methodName, "query:" + sqlLoanBuffer.toString());
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
	                         OracleUtil.closeQuietly(pstmtLoan);
	                         OracleUtil.closeQuietly(rsLoan);
	                	}
	                }

				
				channelUserVO.setCategoryVO(categoryVO);
			}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	/**
	 * Method creditUserBalances() This method check user maximum balance limits
	 * and Credit the user Balances if limit does not cross by the new balance
	 * (existing balance+new requested credit balance)
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param isFromWeb
	 *            boolean
	 * @param forwardPath
	 *            String
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int creditUserBalances(Connection con, ChannelTransferVO channelTransferVO, boolean isFromWeb, String forwardPath) throws BTSLBaseException {
		final String methodName = "creditUserBalances";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelTransferVO =");
        	loggerValue.append(channelTransferVO);
        	loggerValue.append(" isFromWeb=");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(" ForwardPath=");
        	loggerValue.append(forwardPath);
            LOG.debug(methodName, loggerValue);
        }
		int userBalanceUpdateInsertCount = 0;
		int insertUserThresCount = 0;
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);

		final StringBuilder strBuffUpdate = new StringBuilder();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
		strBuffUpdate.append(" WHERE ");
		strBuffUpdate.append(" user_id = ? ");
		strBuffUpdate.append(" AND ");
		strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");

		/** START: Birendra: 28JAN2015 */
		strBuffUpdate.append(" AND balance_type = ?");
		/** STOP: Birendra: 28JAN2015 */

		final StringBuilder strBuffInsert = new StringBuilder();
		strBuffInsert.append(" INSERT ");
		strBuffInsert.append(" INTO user_balances ");
		strBuffInsert.append(" ( prev_balance, daily_balance_updated_on, balance, last_transfer_type, last_transfer_no, last_transfer_on, ");
		strBuffInsert.append(" user_id, product_code , network_code, network_code_for,");

		/** START: Birendra: 28JAN2015 */
		strBuffInsert.append(" balance_type )");
		/** STOP: Birendra: 28JAN2015 */

		strBuffInsert.append(" VALUES ");
		strBuffInsert.append(" (?,?,?,?,?,?,?,?,?,?,?) ");

		// added two new colums by nilesh: threshold_type and remark
		final StringBuilder strBuffThresholdInsert = new StringBuilder();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert.append("  type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		final String updateQuery = strBuffUpdate.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String insertQuery = strBuffInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Insert Query =");
			loggerValue.append(insertQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String sqlSelect = channelUserQry.creditUserBalancesQry();
		if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlSelect);
				LOG.debug(methodName, loggerValue);
		}

		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("inserUserThresgold =");
			loggerValue.append(insertUserThreshold);
			LOG.debug(methodName, loggerValue);
		}

		try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);PreparedStatement psmtUpdateUserBalance = con.prepareStatement(updateQuery);PreparedStatement psmtInsertUserBalance = con.prepareStatement(insertQuery);PreparedStatement psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);){

			PreparedStatement handlerStmt = null;

			TransferProfileProductVO transferProfileProductVO = null;
			long maxBalance = 0;
			// added by vikram
			final Date currentDate = new Date();
			long thresholdValue = -1;

			// thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,channelTransferVO.getNetworkCode(),
			// channelTransferVO.getReceiverCategoryCode()); //threshold value

			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			ChannelTransferItemsVO channelTransferItemsVO = null;
			String userID = null;
			String profileID = null;
			userID = channelTransferVO.getToUserID();
			profileID = channelTransferVO.getReceiverTxnProfile();
			boolean isNotToExecuteQuery = false;
			final ArrayList errorList = new ArrayList();
			KeyArgumentVO keyArgumentVO = null;
			for (int i = 0, k = itemsList.size(); i < k; i++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);

				/** START: Birendra: 28JAN2015 */
				if (userProductMultipleWallet) {
					pstmt.setString(1, channelTransferItemsVO.getUserWallet());
				} else {
					pstmt.setString(1, defaultWallet);
				}
				/** STOP: Birendra: 28JAN2015 */

				pstmt.setString(2, userID);
				pstmt.setString(3, channelTransferItemsVO.getProductCode());
				pstmt.setString(4, channelTransferVO.getNetworkCode());
				pstmt.setString(5, channelTransferVO.getNetworkCodeFor());

				try (ResultSet rs = pstmt.executeQuery();)
				{
				long balance = -1;
				if (rs.next()) {
					balance = rs.getLong("balance");
					channelTransferItemsVO.setBalance(balance);
				}

				if (balance > -1) {
					channelTransferItemsVO.setPreviousBalance(balance);
					channelTransferItemsVO.setAfterTransReceiverPreviousStock(balance);
					// set receiver previous stock.
					channelTransferItemsVO.setReceiverPreviousStock(balance);
					if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
						balance += channelTransferItemsVO.getReceiverCreditQty();
					} else {
						balance += channelTransferItemsVO.getApprovedQuantity();
					}
				} else {
					channelTransferItemsVO.setPreviousBalance(0);
					channelTransferItemsVO.setAfterTransReceiverPreviousStock(0);
				}
				pstmt.clearParameters();

				// in the case of return we have not to check the max balance
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
					/*
					 * check for the max balance for the product
					 */
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					maxBalance = transferProfileProductVO.getMaxBalanceAsLong();

					if (maxBalance < balance) {
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						keyArgumentVO = new KeyArgumentVO();
						if (isFromWeb) {
							final String arg[] = { channelTransferItemsVO.getShortName() };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey("error.transfer.maxbalance.reached");
						} else {
							final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(transferProfileProductVO
									.getMaxBalanceAsLong()) };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY);
						}
						errorList.add(keyArgumentVO);
					}
					// check for the very first txn of the user containg the
					// order value larger than maxBalance
					else if (balance == -1 && maxBalance < channelTransferItemsVO.getApprovedQuantity()) {
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						keyArgumentVO = new KeyArgumentVO();
						if (isFromWeb) {
							final String arg[] = { channelTransferItemsVO.getShortName() };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey("error.transfer.maxbalance.reached");
						} else {
							final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(transferProfileProductVO
									.getMaxBalanceAsLong()) };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY);
						}
						errorList.add(keyArgumentVO);
					}
				}
				if (!isNotToExecuteQuery) {
					int m = 0;
					// update
					if (balance > -1) {
						handlerStmt = psmtUpdateUserBalance;
					} else {
						// insert
						handlerStmt = psmtInsertUserBalance;
						if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
							balance = channelTransferItemsVO.getReceiverCreditQty();
						} else {
							balance = channelTransferItemsVO.getApprovedQuantity();
						}
						channelTransferItemsVO.setPreviousBalance(0);
						handlerStmt.setLong(++m, 0);// previous balance
						handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));// updated
						// on
						// date
					}

					handlerStmt.setLong(++m, balance);
					handlerStmt.setString(++m, channelTransferVO.getTransferType());
					handlerStmt.setString(++m, channelTransferVO.getTransferID());
					handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
					handlerStmt.setString(++m, userID);

					// where
					handlerStmt.setString(++m, channelTransferItemsVO.getProductCode());
					handlerStmt.setString(++m, channelTransferVO.getNetworkCode());
					handlerStmt.setString(++m, channelTransferVO.getNetworkCodeFor());

					/** START: Birendra: 30JAN2015 */
					if (userProductMultipleWallet) {
						handlerStmt.setString(++m, channelTransferItemsVO.getUserWallet());
					} else {
						handlerStmt.setString(++m, defaultWallet);
					}
					/** STOP: Birendra: 30JAN2015 */
					userBalanceUpdateInsertCount = handlerStmt.executeUpdate();
					handlerStmt.clearParameters();

					if (userBalanceUpdateInsertCount <= 0) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[creditUserBalances]", "",
								"", "", "BTSLBaseException: update count <=0");
						throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
					}
					// added by nilesh
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());

					thresholdValue=transferProfileProductVO.getAltBalanceLong();
					String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					final String remark = null;
					if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
						thresholdValue = transferProfileProductVO.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					}
					// end of nilesh
					// for zero balance counter added by vikram
					try {


						if ((channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance >= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue && !channelTransferVO.getSosFlag() && !channelTransferVO.getLRFlag())) {
							psmtInsertUserThreshold.clearParameters();
							//shashi :changes start here for Threshold balance alert
							if (balance < thresholdValue)
							{
								UserBalancesVO vo = new UserBalancesVO();
								vo.setUserID(userID);
								vo.setProductCode(channelTransferItemsVO.getProductCode());
								vo.setNetworkCode(channelTransferVO.getNetworkCode());
								vo.setLastTransferID(channelTransferVO.getTransferID());
								if(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equalsIgnoreCase(channelTransferVO.getTransferType()) ||  PretupsI.C2C_MODULE.equalsIgnoreCase(channelTransferVO.getTransferType()))
									new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,channelTransferVO.getReceiverCategoryCode());
							}//end here

							m = 0;
							m++;
							psmtInsertUserThreshold.setString(m, userID);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferID());
							m++;
							psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getNetworkCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferItemsVO.getProductCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getType());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferType());
							if (balance >= thresholdValue) {
								m++;
								psmtInsertUserThreshold.setString(m, PretupsI.ABOVE_THRESHOLD_TYPE);
							}

							else {
								m++;
								psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
							}
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getReceiverCategoryCode());
							m++;
							psmtInsertUserThreshold.setLong(m, channelTransferItemsVO.getPreviousBalance());
							m++;
							psmtInsertUserThreshold.setLong(m, balance);
							m++;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							// added by nilesh
							m++;
							psmtInsertUserThreshold.setString(m, threshold_type);
							m++;
							psmtInsertUserThreshold.setString(m, remark);
							insertUserThresCount = psmtInsertUserThreshold.executeUpdate();
							LOG.debug(methodName, "No of Rows Inserted in table : user_threshold_counter is : " + insertUserThresCount);
						}
					} catch (SQLException sqle) {
						loggerValue.setLength(0);
						loggerValue.append(SQL_EXCEPTION);
						loggerValue.append(sqle.getMessage());
						LOG.error(methodName, loggerValue);
						LOG.errorTrace(methodName, sqle);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[creditUserBalances]",
								channelTransferVO.getTransferID(), "", channelTransferVO.getNetworkCode(),
								"Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
					}
				}
			}// for
			if (!errorList.isEmpty()) {
				if (isFromWeb) {
					throw new BTSLBaseException(this, methodName, errorList, forwardPath);
				}
				throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE, errorList);
			}

			channelTransferVO.setEntryType(PretupsI.CREDIT);
		} 
		}catch (BTSLBaseException bbe) {
			throw bbe;
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[creditUserBalances]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[creditUserBalances]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Exiting userBalanceUpdateInsertCount:");
				loggerValue.append(userBalanceUpdateInsertCount);
				loggerValue.append(" ,insertUserThresCount:");
				loggerValue.append(insertUserThresCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return userBalanceUpdateInsertCount;
	}

	/**
	 * Method debitUserBalances() This method check user minimum balance limits
	 * and Debit the user Balances if limit does not cross by the new balance
	 * (existing balance-new requested credit balance)
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param isFromWeb
	 *            boolean
	 * @param forwardPath
	 *            String
	 * @return int
	 * @throws BTSLBaseException
	 */

	public int debitUserBalances(Connection con, ChannelTransferVO channelTransferVO, boolean isFromWeb, String forwardPath) throws BTSLBaseException {
		final String methodName = "debitUserBalances";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelTransferVO =");
        	loggerValue.append(channelTransferVO);
        	loggerValue.append(" isFromWeb=");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(" forward Path=");
        	loggerValue.append(forwardPath);
            LOG.debug(methodName, loggerValue);
        }
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		int updateCount = 0;
		PreparedStatement pstmt = null;
		PreparedStatement psmtUpdate = null;
		PreparedStatement psmtInsertUserThreshold = null;
		ResultSet rs = null;

		final StringBuilder strBuffUpdate = new StringBuilder();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
		strBuffUpdate.append(" WHERE ");
		strBuffUpdate.append(" user_id = ? ");
		strBuffUpdate.append(" AND ");
		strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");

		// added by vikram
		final StringBuilder strBuffThresholdInsert = new StringBuilder();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		final String updateQuery = strBuffUpdate.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String sqlSelect = channelUserQry.debitUserBalancesQry();


		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("insertUserThreshold Query =");
			loggerValue.append(insertUserThreshold);
			LOG.debug(methodName, loggerValue);
		}
		try {
			psmtUpdate = con.prepareStatement(updateQuery);
			psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);

			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			TransferProfileProductVO transferProfileProductVO = null;
			long minBalance = 0;
			int maxAllowPct = 0;
			long maxAllowBalance = 0;
			// added by vikram
			final Date currentDate = new Date();
			long thresholdValue = -1;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			String userID = null;
			String profileID = null;
			String userCode = null;

			/*
			 * In case of transfer : debit the from user which is logined user
			 * In case of retutn : debit the from user which is logined user In
			 * case of Withdraw : debit the To user which is Searched user
			 */
			userID = channelTransferVO.getFromUserID();
			profileID = channelTransferVO.getSenderTxnProfile();
			userCode = channelTransferVO.getFromUserCode();
			boolean isNotToExecuteQuery = false;
			final ArrayList errorBalanceNotExistList = new ArrayList();
			final ArrayList errorBalanceLessList = new ArrayList();
			final ArrayList errorBalanceMinList = new ArrayList();
			final ArrayList errorList = new ArrayList();

			KeyArgumentVO keyArgumentVO = null;
			int itemsListSize = itemsList.size();
			for (int i = 0, k = itemsListSize; i < k; i++) {
				boolean toAddMoreError = true;
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				pstmt = con.prepareStatement(sqlSelect);
				pstmt.setString(1, userID);
				pstmt.setString(2, channelTransferItemsVO.getProductCode());
				pstmt.setString(3, channelTransferVO.getNetworkCode());
				pstmt.setString(4, channelTransferVO.getNetworkCodeFor());
				rs = pstmt.executeQuery();
				long balance = -1;
				if (rs.next()) {
					balance = rs.getLong("balance");
				} else {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}

					keyArgumentVO = new KeyArgumentVO();
					if (isFromWeb) {
						keyArgumentVO.setKey("error.transfer.userbalance.notexist");
						final String arg[] = { channelTransferItemsVO.getShortName() };
						keyArgumentVO.setArguments(arg);
						errorList.add(keyArgumentVO);
					} else {
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY);
						final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()) };
						keyArgumentVO.setArguments(arg);
						errorBalanceNotExistList.add(keyArgumentVO);
					}

				}
				channelTransferItemsVO.setBalance(balance);
				if (balance < channelTransferItemsVO.getRequiredQuantity() && toAddMoreError) {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}
					keyArgumentVO = new KeyArgumentVO();
					if (isFromWeb) {
						keyArgumentVO.setKey("error.transfer.userbalance.less");
						final String arg[] = { channelTransferItemsVO.getShortName() };
						keyArgumentVO.setArguments(arg);
						errorList.add(keyArgumentVO);
					} else {
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE_SUBKEY);
						final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };
						keyArgumentVO.setArguments(arg);
						errorBalanceLessList.add(keyArgumentVO);
					}

				}
				channelTransferItemsVO.setPreviousBalance(balance);// set the
				// previous
				// balance
				channelTransferItemsVO.setAfterTransSenderPreviousStock(balance);
				// in the case of return we have not to check the min balance
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType()) && toAddMoreError) {
					/*
					 * check for the min balance for the product
					 */
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();
					maxAllowBalance = (balance * maxAllowPct) / 100;
					if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
						toAddMoreError = false;
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						keyArgumentVO = new KeyArgumentVO();
						if (isFromWeb) {
							keyArgumentVO.setKey("error.transfer.allowedmaxpct.isless");
							final String arg[] = { channelTransferItemsVO.getShortName(), String.valueOf(maxAllowPct) };
							keyArgumentVO.setArguments(arg);
							errorList.add(keyArgumentVO);
						} else {
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT);
							final String arg[] = { String.valueOf(maxAllowPct), String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO
									.getRequestedQuantity() };
							keyArgumentVO.setArguments(arg);
							errorBalanceMinList.add(keyArgumentVO);
						}
					}
					if (toAddMoreError) {
						if (balance > -1) {
							balance -= channelTransferItemsVO.getRequiredQuantity();
						}
						minBalance = transferProfileProductVO.getMinResidualBalanceAsLong();
						if (minBalance > balance) {
							toAddMoreError = false;
							if (!isNotToExecuteQuery) {
								isNotToExecuteQuery = true;
							}

							keyArgumentVO = new KeyArgumentVO();
							if (isFromWeb) {
								keyArgumentVO.setKey("error.transfer.minbalance.reached");
								final String arg[] = { channelTransferItemsVO.getShortName() };
								keyArgumentVO.setArguments(arg);
								errorList.add(keyArgumentVO);
							} else {
								keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS);
								final String arg[] = { PretupsBL.getDisplayAmount(minBalance), String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO
										.getRequestedQuantity() };
								keyArgumentVO.setArguments(arg);
								errorBalanceMinList.add(keyArgumentVO);
							}

						}
					}// if to add more errors
				}// if for transfer check
				if (!PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {

					if (balance > -1) {
						balance -= channelTransferItemsVO.getRequiredQuantity();
					}
				}

				if (!isNotToExecuteQuery) {
					int m = 0;
					m++;
					psmtUpdate.setLong(m, balance);
					m++;
					psmtUpdate.setString(m, channelTransferVO.getTransferType());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getTransferID());
					m++;
					psmtUpdate.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
					m++;
					psmtUpdate.setString(m, userID);
					m++;
					psmtUpdate.setString(m, channelTransferItemsVO.getProductCode());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getNetworkCode());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getNetworkCodeFor());

					updateCount = psmtUpdate.executeUpdate();
					if (updateCount <= 0) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[debitUserBalances]", "", "",
								"", "BTSLBaseException: update count <=0");
						throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
					}

					psmtUpdate.clearParameters();
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
					String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					final String remark = null;

					if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
						thresholdValue = transferProfileProductVO.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					}

					try {
						if ((channelTransferItemsVO.getPreviousBalance() >= thresholdValue && balance <= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue)) {

							if (balance < thresholdValue)
							{
								UserBalancesVO vo = new UserBalancesVO();
								vo.setUserID(userID);
								vo.setProductCode(channelTransferItemsVO.getProductCode());
								vo.setNetworkCode(channelTransferVO.getNetworkCode());
								vo.setLastTransferID(channelTransferVO.getTransferID());
								if(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equalsIgnoreCase(channelTransferVO.getTransferType()) || PretupsI.C2C_MODULE.equalsIgnoreCase(channelTransferVO.getTransferType()))
									new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,channelTransferVO.getCategoryCode());
							}//end here

							m = 0;
							m++;
							psmtInsertUserThreshold.setString(m, userID);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferID());
							m++;
							psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getNetworkCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferItemsVO.getProductCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getType());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferType());
							m++;
							psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getCategoryCode());
							m++;
							psmtInsertUserThreshold.setLong(m, channelTransferItemsVO.getPreviousBalance());
							m++;
							psmtInsertUserThreshold.setLong(m, balance);
							m++;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							m++;
							psmtInsertUserThreshold.setString(m, threshold_type);
							m++;
							psmtInsertUserThreshold.setString(m, remark);
							updateCount = psmtInsertUserThreshold.executeUpdate();
							psmtInsertUserThreshold.clearParameters();
						}
						if(SystemPreferences.USERWISE_LOAN_ENABLE && channelTransferVO.getUserLoanVOList()!=null ) {
							try {
								ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
								for(int index=0;index<channelTransferVO.getUserLoanVOList().size();index++){
									userLoanVOList.add(channelTransferVO.getUserLoanVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(userLoanVOList, userID, balance,channelTransferItemsVO.getPreviousBalance(),channelTransferItemsVO.getProductCode(),channelTransferItemsVO.getProductType());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
							
							
						}
						else if (SystemPreferences.CHANNEL_SOS_ENABLE&&channelTransferVO.getChannelSoSVOList()!=null)
						{
							try {
								ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
								for(int index=0;index<channelTransferVO.getChannelSoSVOList().size();index++){
									channeluserList.add(channelTransferVO.getChannelSoSVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, userID, balance,channelTransferItemsVO.getPreviousBalance());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
						}//end here
					} catch (SQLException sqle) {
						loggerValue.setLength(0);
						loggerValue.append(SQL_EXCEPTION);
						loggerValue.append(sqle.getMessage());
						LOG.error(methodName, loggerValue);
						LOG.errorTrace(methodName, sqle);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalances]",
								channelTransferVO.getTransferID(), "", channelTransferVO.getNetworkCode(),
								"Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
					}
				}// if to execute query
			}// for loop
			if (isFromWeb) {
				if (!errorList.isEmpty()) {
					throw new BTSLBaseException(this, methodName, errorList, forwardPath);
				}
			} else {
				if (!errorBalanceNotExistList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE, userCode);
				} else if (!errorBalanceLessList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE, errorBalanceLessList);
				} else if (!errorBalanceMinList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG,
							errorBalanceMinList);
				}
			}

			// for balance logger
			channelTransferVO.setEntryType(PretupsI.DEBIT);
		} catch (BTSLBaseException bbe) {
			throw bbe;
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalances]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalances]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtUpdate != null) {
                	psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtInsertUserThreshold != null) {
                	psmtInsertUserThreshold.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting update Count:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updateCount;
	}

	
	public int debitUserBalancesO2C(Connection con, ChannelTransferVO channelTransferVO, boolean isFromWeb, String forwardPath) throws BTSLBaseException {

		final String methodName = "debitUserBalances";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelTransferVO =");
        	loggerValue.append(channelTransferVO);
        	loggerValue.append(" isFromWeb=");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(" forward Path=");
        	loggerValue.append(forwardPath);
            LOG.debug(methodName, loggerValue);
        }
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		int updateCount = 0;
		PreparedStatement pstmt = null;
		PreparedStatement psmtUpdate = null;
		PreparedStatement psmtInsertUserThreshold = null;
		ResultSet rs = null;

		final StringBuilder strBuffUpdate = new StringBuilder();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
		strBuffUpdate.append(" WHERE ");
		strBuffUpdate.append(" user_id = ? ");
		strBuffUpdate.append(" AND ");
		strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");

		// added by vikram
		final StringBuilder strBuffThresholdInsert = new StringBuilder();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		final String updateQuery = strBuffUpdate.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String sqlSelect = channelUserQry.debitUserBalancesQry();


		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("insertUserThreshold Query =");
			loggerValue.append(insertUserThreshold);
			LOG.debug(methodName, loggerValue);
		}
		try {
			psmtUpdate = con.prepareStatement(updateQuery);
			psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);

			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			TransferProfileProductVO transferProfileProductVO = null;
			long minBalance = 0;
			int maxAllowPct = 0;
			long maxAllowBalance = 0;
			// added by vikram
			final Date currentDate = new Date();
			long thresholdValue = -1;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			String userID = null;
			String profileID = null;
			String userCode = null;

			/*
			 * In case of transfer : debit the from user which is logined user
			 * In case of retutn : debit the from user which is logined user In
			 * case of Withdraw : debit the To user which is Searched user
			 */
			userID = channelTransferVO.getFromUserID();
			profileID = channelTransferVO.getSenderTxnProfile();
			userCode = channelTransferVO.getFromUserCode();
			boolean isNotToExecuteQuery = false;
			final ArrayList errorBalanceNotExistList = new ArrayList();
			final ArrayList errorBalanceLessList = new ArrayList();
			final ArrayList errorBalanceMinList = new ArrayList();
			final ArrayList errorList = new ArrayList();
			boolean userBalanceLess = false;

			KeyArgumentVO keyArgumentVO = null;
			int itemsListSize = itemsList.size();
			for (int i = 0, k = itemsListSize; i < k; i++) {
				boolean toAddMoreError = true;
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				pstmt = con.prepareStatement(sqlSelect);
				pstmt.setString(1, userID);
				pstmt.setString(2, channelTransferItemsVO.getProductCode());
				pstmt.setString(3, channelTransferVO.getNetworkCode());
				pstmt.setString(4, channelTransferVO.getNetworkCodeFor());
				rs = pstmt.executeQuery();
				long balance = -1;
				if (rs.next()) {
					balance = rs.getLong("balance");
				} else {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}

					keyArgumentVO = new KeyArgumentVO();
					if (isFromWeb) {
						keyArgumentVO.setKey("error.transfer.userbalance.notexist");
						final String arg[] = { channelTransferItemsVO.getShortName() };
						keyArgumentVO.setArguments(arg);
						errorList.add(keyArgumentVO);
					} else {
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY);
						final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()) };
						keyArgumentVO.setArguments(arg);
						errorBalanceNotExistList.add(keyArgumentVO);
					}

				}
				channelTransferItemsVO.setBalance(balance);
				if (balance < channelTransferItemsVO.getRequiredQuantity() && toAddMoreError) {
					userBalanceLess = true;
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}
					keyArgumentVO = new KeyArgumentVO();
					if (isFromWeb) {
						keyArgumentVO.setKey("error.transfer.userbalance.less");
						final String arg[] = { channelTransferItemsVO.getShortName() };
						keyArgumentVO.setArguments(arg);
						errorList.add(keyArgumentVO);
					} else {
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE_SUBKEY);
						final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };
						keyArgumentVO.setArguments(arg);
						errorBalanceLessList.add(keyArgumentVO);
					}

				}
				channelTransferItemsVO.setPreviousBalance(balance);// set the
				// previous
				// balance
				channelTransferItemsVO.setAfterTransSenderPreviousStock(balance);
				// in the case of return we have not to check the min balance
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType()) && toAddMoreError) {
					/*
					 * check for the min balance for the product
					 */
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();
					maxAllowBalance = (balance * maxAllowPct) / 100;
					if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
						toAddMoreError = false;
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						keyArgumentVO = new KeyArgumentVO();
						if (isFromWeb) {
							keyArgumentVO.setKey("error.transfer.allowedmaxpct.isless");
							final String arg[] = { channelTransferItemsVO.getShortName(), String.valueOf(maxAllowPct) };
							keyArgumentVO.setArguments(arg);
							errorList.add(keyArgumentVO);
						} else {
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT);
							final String arg[] = { String.valueOf(maxAllowPct), String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO
									.getRequestedQuantity() };
							keyArgumentVO.setArguments(arg);
							errorBalanceMinList.add(keyArgumentVO);
						}
					}
					if (toAddMoreError) {
						if (balance > -1) {
							balance -= channelTransferItemsVO.getRequiredQuantity();
						}
						minBalance = transferProfileProductVO.getMinResidualBalanceAsLong();
						if (minBalance > balance) {
							toAddMoreError = false;
							if (!isNotToExecuteQuery) {
								isNotToExecuteQuery = true;
							}

							keyArgumentVO = new KeyArgumentVO();
							if (isFromWeb) {
								keyArgumentVO.setKey("error.transfer.minbalance.reached");
								final String arg[] = { channelTransferItemsVO.getShortName() };
								keyArgumentVO.setArguments(arg);
								errorList.add(keyArgumentVO);
							} else {
								keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS);
								final String arg[] = { PretupsBL.getDisplayAmount(minBalance), String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO
										.getRequestedQuantity() };
								keyArgumentVO.setArguments(arg);
								errorBalanceMinList.add(keyArgumentVO);
							}

						}
					}// if to add more errors
				}// if for transfer check
				if (!PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {

					if (balance > -1) {
						balance -= channelTransferItemsVO.getRequiredQuantity();
					}
				}

				if (!isNotToExecuteQuery) {
					int m = 0;
					m++;
					psmtUpdate.setLong(m, balance);
					m++;
					psmtUpdate.setString(m, channelTransferVO.getTransferType());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getTransferID());
					m++;
					psmtUpdate.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
					m++;
					psmtUpdate.setString(m, userID);
					m++;
					psmtUpdate.setString(m, channelTransferItemsVO.getProductCode());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getNetworkCode());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getNetworkCodeFor());

					updateCount = psmtUpdate.executeUpdate();
					if (updateCount <= 0) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[debitUserBalances]", "", "",
								"", "BTSLBaseException: update count <=0");
						throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
					}

					psmtUpdate.clearParameters();
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
					String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					final String remark = null;

					if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
						thresholdValue = transferProfileProductVO.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					}

					try {
						if ((channelTransferItemsVO.getPreviousBalance() >= thresholdValue && balance <= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue)) {

							if (balance < thresholdValue)
							{
								UserBalancesVO vo = new UserBalancesVO();
								vo.setUserID(userID);
								vo.setProductCode(channelTransferItemsVO.getProductCode());
								vo.setNetworkCode(channelTransferVO.getNetworkCode());
								vo.setLastTransferID(channelTransferVO.getTransferID());
								if(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equalsIgnoreCase(channelTransferVO.getTransferType()) || PretupsI.C2C_MODULE.equalsIgnoreCase(channelTransferVO.getTransferType()))
									new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,channelTransferVO.getCategoryCode());
							}//end here

							m = 0;
							m++;
							psmtInsertUserThreshold.setString(m, userID);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferID());
							m++;
							psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getNetworkCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferItemsVO.getProductCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getType());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferType());
							m++;
							psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getCategoryCode());
							m++;
							psmtInsertUserThreshold.setLong(m, channelTransferItemsVO.getPreviousBalance());
							m++;
							psmtInsertUserThreshold.setLong(m, balance);
							m++;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							m++;
							psmtInsertUserThreshold.setString(m, threshold_type);
							m++;
							psmtInsertUserThreshold.setString(m, remark);
							updateCount = psmtInsertUserThreshold.executeUpdate();
							psmtInsertUserThreshold.clearParameters();
						}
						if(SystemPreferences.USERWISE_LOAN_ENABLE && channelTransferVO.getUserLoanVOList()!=null ) {
							try {
								ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
								for(int index=0;index<channelTransferVO.getUserLoanVOList().size();index++){
									userLoanVOList.add(channelTransferVO.getUserLoanVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(userLoanVOList, userID, balance,channelTransferItemsVO.getPreviousBalance(),channelTransferItemsVO.getProductCode(),channelTransferItemsVO.getProductType());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
							
							
						}
						else if (SystemPreferences.CHANNEL_SOS_ENABLE&&channelTransferVO.getChannelSoSVOList()!=null)
						{
							try {
								ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
								for(int index=0;index<channelTransferVO.getChannelSoSVOList().size();index++){
									channeluserList.add(channelTransferVO.getChannelSoSVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, userID, balance,channelTransferItemsVO.getPreviousBalance());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
						}//end here
					} catch (SQLException sqle) {
						loggerValue.setLength(0);
						loggerValue.append(SQL_EXCEPTION);
						loggerValue.append(sqle.getMessage());
						LOG.error(methodName, loggerValue);
						LOG.errorTrace(methodName, sqle);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalances]",
								channelTransferVO.getTransferID(), "", channelTransferVO.getNetworkCode(),
								"Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
					}
				}// if to execute query
			}// for loop
			if (isFromWeb) {
				if (!errorList.isEmpty()) {
					if (userBalanceLess) {						
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_TRANSFER_PRODUCT_LESS_BALANCE);
					}
					throw new BTSLBaseException(this, methodName, errorList, forwardPath);
				}
			} else {
				if (!errorBalanceNotExistList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE, userCode);
				} else if (!errorBalanceLessList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE, errorBalanceLessList);
				} else if (!errorBalanceMinList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG,
							errorBalanceMinList);
				}
			}

			// for balance logger
			channelTransferVO.setEntryType(PretupsI.DEBIT);
		} catch (BTSLBaseException bbe) {
			throw bbe;
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalances]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalances]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtUpdate != null) {
                	psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtInsertUserThreshold != null) {
                	psmtInsertUserThreshold.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting update Count:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updateCount;
	
	}
	
	/**
	 * Method loadUserBalances() This method loads the userBalancesVO list.
	 * 
	 * @param con
	 * @param networkCode
	 * @param p_roamNetworkCode
	 * @param userId
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadUserBalances(Connection con, String networkCode, String p_roamNetworkCode, String userId) throws BTSLBaseException {

		final String methodName = "loadUserBalances";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_roamNetworkCode =");
        	loggerValue.append(p_roamNetworkCode);
        	loggerValue.append(" networkCode=");
        	loggerValue.append(networkCode);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }

		
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
		final StringBuilder strBuff = new StringBuilder();

		strBuff.append(" SELECT ");
		strBuff.append(" UB.product_code, UB.balance, UB.prev_balance,UB.last_transfer_type, UB.last_transfer_no, ");
		strBuff.append(" UB.last_transfer_on,P.product_short_code,P.product_name,P.short_name,P.product_type ");
		if (userProductMultipleWallet) {
			strBuff.append(" ,ub.balance_type  ");
		}
		strBuff.append(" FROM  ");
		strBuff.append(" user_balances UB,products P");
		strBuff.append(" WHERE  ");
		strBuff.append(" network_code = ? AND network_code_for = ? AND user_id =? AND  UB.product_code=P.product_code ");

		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		final ArrayList arrayList = new ArrayList();
		try ( PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			pstmt.setString(1, networkCode);
			pstmt.setString(2, p_roamNetworkCode);
			pstmt.setString(3, userId);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			UserBalancesVO balancesVO = null;
			while (rs.next()) {
				balancesVO = new UserBalancesVO();
				balancesVO.setProductCode(rs.getString("product_code"));
				balancesVO.setProductShortCode(rs.getString("product_short_code"));
				balancesVO.setProductType(rs.getString("product_type"));
				balancesVO.setBalance(rs.getLong("balance"));
				balancesVO.setBalanceStr(PretupsBL.getDisplayAmount(rs.getLong("balance")));
				balancesVO.setPreviousBalance(rs.getLong("prev_balance"));
				balancesVO.setLastTransferType(rs.getString("last_transfer_type"));
				balancesVO.setLastTransferID(rs.getString("last_transfer_no"));
				balancesVO.setLastTransferOn(rs.getDate("last_transfer_on"));
				balancesVO.setProductShortName(rs.getString("short_name"));
				balancesVO.setProductName(rs.getString("product_name"));
				balancesVO.setNetworkCode(networkCode);
				balancesVO.setNetworkFor(p_roamNetworkCode);
				balancesVO.setUserID(userId);
				if (userProductMultipleWallet) {
					balancesVO.setBalanceType(rs.getString("balance_type"));
				}
				arrayList.add(balancesVO);
			}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserBalances]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "", "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserBalances]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "", "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * Method loadUserAgentsBalance.
	 * 
	 * @param con
	 *            Connection
	 * @param parentID
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadUserAgentsBalance(Connection con, String parentID) throws BTSLBaseException {
		final String methodName = "loadUserAgentsBalance";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: parentID =");
        	loggerValue.append(parentID);
            LOG.debug(methodName, loggerValue);
        }
		UserBalancesVO userBalanceVO = null;
		 
		 
		final long balance = 0;
		final StringBuilder strBuff = new StringBuilder();
		ArrayList balanceList = null;

		strBuff.append(" SELECT sum(UB.balance) balance,P.product_short_code,P.short_name ");
		strBuff.append(" FROM products P,user_balances UB ");
		strBuff.append(" WHERE UB.product_code=P.product_code  ");
		strBuff.append(" AND EXISTS (SELECT U.user_id FROM users U ,categories C WHERE U.user_id=UB.user_id ");
		strBuff.append(" AND U.parent_id=? AND NOT U.status IN ('N','C') AND U.category_code =C.category_code AND C.category_type=? ) ");
		strBuff.append(" GROUP BY (P.product_short_code,P.short_name) ");

		final String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			pstmt.setString(1, parentID);
			pstmt.setString(2, PretupsI.AGENTCATEGORY);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			balanceList = new ArrayList();
			while (rs.next()) {
				userBalanceVO = new UserBalancesVO();
				userBalanceVO.setProductShortCode(rs.getString("product_short_code"));
				userBalanceVO.setBalance(rs.getLong("balance"));
				userBalanceVO.setProductShortName(rs.getString("short_name"));
				balanceList.add(userBalanceVO);
			}
		}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserAgentsBalance]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "", "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserAgentsBalance]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "", "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Balance:");
				loggerValue.append(balance);
				LOG.debug(methodName, loggerValue);
			}
		}
		return balanceList;
	}

	/**
	 * loadUserForChannelByPass This method loads all the users under the user
	 * which is passed as argument and users which are not direct child of that
	 * user.
	 * 
	 * @author sandeep.goel
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param parentID
	 * @param userName
	 * @param userId
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */

	public ArrayList loadUserForChannelByPass(Connection con, String networkCode, String toCategoryCode, String parentID, String userName, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUserForChannelByPass";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: network Code =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code =");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" parentID =");
        	loggerValue.append(parentID);
        	loggerValue.append(" UserName=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2OraclePreparedStatement pstmt = null commented
		PreparedStatement pstmt = null;
		
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}

		final ArrayList arrayList = new ArrayList();
		try {
			pstmt = channelUserQry.loadUserForChannelByPassQry(con, statusAllowed, networkCode, toCategoryCode, userId, userName, parentID);

			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserForChannelByPass]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserForChannelByPass]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * loadUsersByParentID This method loads all the users which are the direct
	 * child of the parent users which is passed as the argument.
	 * 
	 * @author sandeep.goel
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param parentID
	 * @param userName
	 * @param userId
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadUsersByParentID(Connection con, String networkCode, String toCategoryCode, String parentID, String userName, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersByParentID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: NetworkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" parent ID=");
        	loggerValue.append(parentID);
        	loggerValue.append(" UserName=");
        	loggerValue.append(userName);
        	loggerValue.append(" user Id=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt = null commented
		
		
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT login_id,msisdn,user_id, user_name FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_id != ?");
		strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) AND ( parent_id = ? OR user_id = ?) ORDER BY user_name ");
		// here parent_id = ? OR user_id = ? check is to load the parent also if
		// transaciton is done only to parent
		final String sqlSelect = strBuff.toString();
		LogFactory.printLog(methodName,"QUERY sqlSelect=" + sqlSelect, LOG);
		final ArrayList arrayList = new ArrayList();
		try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);){

			
			int i = 0;
			i++;
			pstmt.setString(i, networkCode);
			i++;
			pstmt.setString(i, toCategoryCode);
			i++;
			pstmt.setString(i, userId);
			i++;
			pstmt.setString(i, userName);
			i++;
			pstmt.setString(i, parentID);
			i++;
			pstmt.setString(i, parentID);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("login_id"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("user_name"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("msisdn"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("user_id"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}

		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * loadUsersByParentIDRecursive This method loads all the user under the
	 * parent user which is passed as argurment.
	 * 
	 * @author sandeep.goel
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param parentID
	 * @param userName
	 * @param userId
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadUsersByParentIDRecursive(Connection con, String networkCode, String toCategoryCode, String parentID, String userName, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersByParentIDRecursive";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" parent Id=");
        	loggerValue.append(parentID);
        	loggerValue.append(" User name=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt =null commented
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}


		final ArrayList arrayList = new ArrayList();
		try {

			pstmt = channelUserQry.loadUsersByParentIDRecursiveQry(con, statusAllowed, networkCode, toCategoryCode, parentID, userName, userId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}

		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * Method loadUsersByOwnerID. This method loads all the users under the
	 * owner user passed as argument.
	 * 
	 * @author sandeep.goel
	 * @param con
	 *            Connection
	 * @param networkCode
	 *            String
	 * @param toCategoryCode
	 *            String
	 * @param p_ownerID
	 *            String
	 * @param userName
	 *            String
	 * @param userId
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadUsersByOwnerID(Connection con, String networkCode, String toCategoryCode, String p_ownerID, String userName, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersByOwnerID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" ToCategory Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" ownerID=");
        	loggerValue.append(p_ownerID);
        	loggerValue.append(" Username=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt = null commented
		 
		
		String statusAllowed = null;
		// user life cycle
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT user_id, user_name,msisdn,login_id FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_id != ?");
		strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) AND ( owner_id = ? OR user_id = ? ) ORDER BY user_name ");
		// here owner_id = ? OR user_id = ? check is to load the owner also if
		// transaction is to owner only.
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		final ArrayList arrayList = new ArrayList();
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			int i = 0;
			i++;
			pstmt.setString(i, networkCode);
			i++;
			pstmt.setString(i, toCategoryCode);
			i++;
			pstmt.setString(i, userId);
			i++;
			pstmt.setString(i, userName);
			i++;
			pstmt.setString(i, p_ownerID);
			i++;
			pstmt.setString(i, p_ownerID);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByOwnerID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByOwnerID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;

	}

	/**
	 * This method loads all users of the specific category. In the case of
	 * outSide hierarchy all users have to be loaded.
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param userName
	 * @param userId
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 * @author sandeep.goel
	 */
	public ArrayList loadUsersOutsideHireacrhy(Connection con, String networkCode, String toCategoryCode, String userName, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersOutsideHireacrhy";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code =");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" User Name =");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt = null
		 
		
		String statusAllowed = null;
		// user life cycle
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		final StringBuilder strBuff = new StringBuilder(" SELECT u.login_id,u.msisdn,u.user_id, u.user_name ");
		strBuff.append(" FROM users u ");
		strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND category_code = ? AND user_id != ?");
		strBuff.append(" AND u.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ORDER BY u.user_name ");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		final ArrayList arrayList = new ArrayList();
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

			
			int i = 0;
			i++;
			pstmt.setString(i, networkCode);
			i++;
			pstmt.setString(i, toCategoryCode);
			i++;
			pstmt.setString(i, userId);
			i++;
			pstmt.setString(i, userName);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersOutsideHireacrhy]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersOutsideHireacrhy]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * This method load the user hirarchy in tree structure and also load the
	 * product balance list for each user_id
	 * 
	 * @author manoj kumar
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param userId
	 *            String Array
	 * @param mode
	 *            String
	 * @param statusUsed
	 *            String
	 * @param status
	 *            String
	 * @param p_userCategory
	 *            String
	 * @return userDetailList java.util.ArrayList
	 * @throws BTSLBaseException
	 * 
	 */
	public ArrayList loadUserHierarchyList(Connection con, String userId[], String mode, String statusUsed, String status, String p_userCategory) throws BTSLBaseException {
		final String methodName = "loadUserHierarchyList";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId[0]=");
        	loggerValue.append(userId[0]);
        	loggerValue.append(" userId.length =");
        	loggerValue.append(userId.length);
        	loggerValue.append(" mode =");
        	loggerValue.append(mode);
        	loggerValue.append(" statusUsed =");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" status =");
        	loggerValue.append(status);
        	loggerValue.append(" p_userCategory =");
        	loggerValue.append(p_userCategory);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		int maxLevel = 0;
		int tempLevel = 0;
		ChannelUserVO channelUserVO = null;
		final ArrayList userDetailList = new ArrayList();
		final StringBuilder strBuff = new StringBuilder();
		final StringBuilder strBuff1 = new StringBuilder();
		try {

			status = status + ",'" + PretupsI.USER_STATUS_NEW + "'";
			status = formatStatus(status);
			
			pstmt = channelUserQry.loadUserHierarchyListQry(con, statusUsed, mode, status, userId, p_userCategory, null);
			rs = pstmt.executeQuery();

			strBuff1.append("Select lookup_name from lookups ");
			strBuff1.append("where lookup_type= ? AND lookup_code= ? ");
			pstmt1 = con.prepareStatement(strBuff1.toString());

			while (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserlevel(rs.getString("l"));
				tempLevel = Integer.parseInt(channelUserVO.getUserlevel());
				if (tempLevel > maxLevel) {
					maxLevel = tempLevel;
				}
				channelUserVO.setUserIDPrefix(rs.getString("user_id_prefix"));
				channelUserVO.setUserID(rs.getString("user_id"));
				if ("STAFF".equals(rs.getString("user_type"))) {
					channelUserVO.setUserName(rs.getString("user_name") + "[Staff]");
				} else {
					channelUserVO.setUserName(rs.getString("user_name"));
				}
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				// channelUserVO.setPassword(rs.getString("password")) commented
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				// channelUserVO.setAllowedIps(rs.getString("allowed_ip"))
				// commented
				// channelUserVO.setAllowedDays(rs.getString("allowed_days"))
				// commented
				// channelUserVO.setFromTime(rs.getString("from_time"))
				// commented
				// channelUserVO.setToTime(rs.getString("to_time")) commented
				channelUserVO.setLastLoginOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_login_on")));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("status"));
				// channelUserVO.setEmail(rs.getString("email")) commented
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				// end added by deepika aggarwal
				channelUserVO.setPasswordModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pswd_modified_on")));
				// channelUserVO.setContactPerson(rs.getString("contact_person"))
				// commented
				channelUserVO.setContactNo(rs.getString("contact_no"));
				// channelUserVO.setDesignation(rs.getString("designation"))
				// commented
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setLastModified(rs.getTimestamp("modified_on").getTime());
				// channelUserVO.setAddress1(rs.getString("address1")) commented
				// channelUserVO.setAddress2(rs.getString("address2")) commented
				// channelUserVO.setCity(rs.getString("city")) commented
				// channelUserVO.setState(rs.getString("state")) commented
				// channelUserVO.setCountry(rs.getString("country")) commented
				// channelUserVO.setSsn(rs.getString("ssn")) commented
				// channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"))
				// commented
				channelUserVO.setExternalCode(rs.getString("external_code"));
				channelUserVO.setUserCode(rs.getString("user_code"));
				channelUserVO.setShortName(rs.getString("short_name"));
				channelUserVO.setReferenceID(rs.getString("reference_id"));
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("level1_approved_on")));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("level2_approved_on")));
				// channelUserVO.setUserIDPrefix(rs.getString("user_id_prefix"))
				// commented
				channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("password_count_updated_on")));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				channelUserVO.setAppointmentDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("appointment_date")));

				channelUserVO.setStatus(rs.getString("status"));
				channelUserVO.setUserBalanceList(this.loadUserBalances(con, channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelUserVO.getUserID()));
				// for tango implementation
				// channelUserVO.setApplicationID(rs.getString("application_id"))
				// commented
				// channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"))
				// commented
				// channelUserVO.setUserProfileID(rs.getString("user_profile_id"))
				// commented
				// channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"))
				// commented
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setMaxUserLevel(maxLevel);
				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				channelUserVO.setCategoryVO(categoryVO);
				pstmt1.setString(1, PretupsI.USER_STATUS_TYPE);
				pstmt1.setString(2, channelUserVO.getStatus());

				rs1 = pstmt1.executeQuery();
				while (rs1.next()) {
					channelUserVO.setStatusDesc(rs1.getString("lookup_name"));
				}

				if (Integer.parseInt(channelUserVO.getUserlevel()) == 1) {
					if (channelUserVO.getUserType().equals("CHANNEL")) {
						if ("CHANNEL".equals(channelUserVO.getUserType())) {
							userDetailList.add(0, channelUserVO);
						}
					}
				} else {
					userDetailList.add(channelUserVO);
				}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserHierarchyList]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserHierarchyList]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting UserDetails List Size:");
				loggerValue.append(userDetailList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return userDetailList;
	}

	/*  *//**
	 * Method for Updating Channel User's status,modified_on and
	 * modified_by for resumeing the channel user
	 * 
	 * @author manoj kumar
	 * @param con
	 *            java.sql.Connection
	 * @param p_userList
	 *            java.util.ArrayList
	 * @param p_channleUserVO
	 *            ChannelUserVO
	 * @return updateCount int
	 * @throws BTSLBaseException
	 * 
	 */

	/**
	 * This method is used to check whether the record in the database is
	 * modified or not by other user If record is modified then throws the
	 * BTSLBaseException Method:isRecordModified
	 * 
	 * @param con
	 *            Connection
	 * @param userId
	 *            String
	 * @param p_oldLastModified
	 *            long
	 * @return boolean
	 * @throws BTSLBaseException
	 * 
	 */
	public boolean isRecordModified(Connection con, String userId, long p_oldLastModified) throws BTSLBaseException {
		final String methodName = "isRecordModified";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId:");
        	loggerValue.append(userId);
        	loggerValue.append(" oldLastModified=");
        	loggerValue.append(p_oldLastModified);
            LOG.debug(methodName, loggerValue);
        }

		
		 
		boolean modified = false;
		final String sqlRecordModified = "SELECT modified_on FROM users WHERE user_id=? ";
		Timestamp newLastModified = null;
		if ((p_oldLastModified) == 0) {
			return false;
		}

		try(PreparedStatement pstmt = con.prepareStatement(sqlRecordModified);) {
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlRecordModified);
				LOG.debug(methodName, loggerValue);
			}

			
			pstmt.setString(1, userId);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				newLastModified = rs.getTimestamp("modified_on");
			}
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("old =");
				loggerValue.append(p_oldLastModified);
				LOG.debug(methodName, loggerValue);
			}
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("new =");
				loggerValue.append(newLastModified.getTime());
				LOG.debug(methodName, loggerValue);
			}
			if (newLastModified.getTime() != p_oldLastModified) {
				modified = true;
			}

			return modified;
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isRecordModified]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isRecordModified]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting modified:");
				loggerValue.append(modified);
				LOG.debug(methodName, loggerValue);
			}
		}
	} // end recordModified

	/**
	 * Method loadLanguageListForUser This method load the list of languages
	 * from the locale master table
	 * 
	 * @param con
	 *            Connection
	 * @return ArrayList
	 * @throws BTSLBaseException
	 * @author Amit Ruwali
	 */

	public ArrayList loadLanguageListForUser(Connection con) throws BTSLBaseException {
		final String methodName = "loadLanguageListForUser";
		StringBuilder loggerValue= new StringBuilder();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
		
		
		final ArrayList languageListVO = new ArrayList();
		// ChangeID=LOCALEMASTER
		// Query is changed so that only language associated with SMS or BOTH
		// can be loaded

		final StringBuilder strBuff = new StringBuilder("SELECT language,country,name FROM locale_master WHERE status!='N' AND (type=? OR type=?)");
		final String selectQuery = strBuff.toString();
		try(PreparedStatement pstmt = con.prepareStatement(selectQuery);) {
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}
			
			pstmt.setString(1, PretupsI.SMS_LOCALE);
			pstmt.setString(2, PretupsI.BOTH_LOCALE);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			ChangeLocaleVO localeVO = null;
			while (rs.next()) {
				localeVO = new ChangeLocaleVO();
				localeVO.setLanguageCode(rs.getString("language"));
				localeVO.setLanguageName(rs.getString("name"));
				localeVO.setCountry(rs.getString("country"));
				languageListVO.add(localeVO);
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadLanguageListForUser]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadLanguageListForUser]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting languageListVo Size:");
				loggerValue.append(languageListVO.size());
				LOG.debug(methodName, loggerValue);
			}
		}

		return languageListVO;
	}

	/**
	 * Method updateLanguageAndCountry This method will update the language and
	 * country in user phones according to msisdn
	 * 
	 * @param con
	 *            Connection
	 * @param p_lang
	 *            String
	 * @param p_country
	 *            String
	 * @param msisdn
	 *            String
	 * @return the number of records updated
	 * @throws BTSLBaseException
	 */

	public int updateLanguageAndCountry(Connection con, String p_lang, String p_country, String msisdn) throws BTSLBaseException {
		final String methodName = "updateLanguageAndCountry";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_usrMsisdn=");
        	loggerValue.append(msisdn);
        	loggerValue.append(" p_lang=");
        	loggerValue.append(p_lang);
        	loggerValue.append(" p_country=");
        	loggerValue.append(p_country);
            LOG.debug(methodName, loggerValue);
        }

		
		final String qry = "UPDATE user_phones SET phone_language =?,country=? WHERE msisdn=? ";
		int updCount = -1;
		try(PreparedStatement pstmt = con.prepareStatement(qry);) {
			if (LOG.isDebugEnabled()) {
				if(LOG.isDebugEnabled()){
					loggerValue.setLength(0);
					loggerValue.append("Query =");
					loggerValue.append(qry);
					LOG.debug(methodName, loggerValue);
				}
			}
			// Get Preapared Statement
			
			pstmt.setString(1, p_lang);
			pstmt.setString(2, p_country);
			pstmt.setString(3, msisdn);
			// Execute Query
			updCount = pstmt.executeUpdate();
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateLanguageAndCountry]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateLanguageAndCountry]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Update Count:");
				loggerValue.append(updCount);
				LOG.debug(methodName, loggerValue);
			}
		}

		return updCount;
	}

	/**
	 * Method isUserExistForChannelByPass This method is to check that is user
	 * exit in the channel by pass hierarchy of the sender user id.
	 * 
	 * @author sandeep.goel
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param parentID
	 * @param p_userCode
	 * @return boolean
	 * @throws BTSLBaseException
	 */

	public boolean isUserExistForChannelByPass(Connection con, String networkCode, String toCategoryCode, String parentID, String p_userCode, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUserExistForChannelByPass";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Net work Code=");
        	loggerValue.append(networkCode);
        	loggerValue.append(" to Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" ParentID=");
        	loggerValue.append(parentID);
        	loggerValue.append(" userCode=");
        	loggerValue.append(p_userCode);
            LOG.debug(methodName, loggerValue);
        }
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		boolean isExist = false;
		try {
			pstmt = channelUserQry.isUserExistForChannelByPassQry(con, networkCode, toCategoryCode, parentID, p_userCode,statusAllowed);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				isExist = true;
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistForChannelByPass]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistForChannelByPass]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * Method isUserExistByParentID This method is to check that is user Exist
	 * in the hierarchy of the user passed as argument
	 * 
	 * @author sandeep.goel
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param parentID
	 * @param p_userCode
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public boolean isUserExistByParentID(Connection con, String networkCode, String toCategoryCode, String parentID, String p_userCode, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUserExistByParentID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Network Code=");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" parentID=");
        	loggerValue.append(parentID);
        	loggerValue.append(" p_userCode=");
        	loggerValue.append(p_userCode);
            LOG.debug(methodName, loggerValue);
        }
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		PreparedStatement pstmt = null;
		
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT 1 FROM users");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_code = ?");
		strBuff.append(" AND ( parent_id = ? OR user_id = ? )");
		// here parent_id = ? OR user_id = ? check is done if transaction is
		// done only to the parent user.
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		boolean isExist = false;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 0;
			i++;
			pstmt.setString(i, networkCode);
			i++;
			pstmt.setString(i, toCategoryCode);
			i++;
			pstmt.setString(i, p_userCode);
			i++;
			pstmt.setString(i, parentID);
			i++;
			pstmt.setString(i, parentID);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				isExist = true;
			}
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByParentID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByParentID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
		
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}
	
	/**
	 * isUserExistByParentIDRecursive This method is to check that is user exist
	 * in the hierarchy of the passeduser
	 * 
	 * @author sandeep.goel
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param parentID
	 * @param p_userCode
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public boolean isUserExistByParentIDRecursive(Connection con, String networkCode, String toCategoryCode, String parentID, String p_userCode, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUserExistByParentIDRecursive";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Network Code=");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" parentID=");
        	loggerValue.append(parentID);
        	loggerValue.append(" p_userCode=");
        	loggerValue.append(p_userCode);
            LOG.debug(methodName, loggerValue);
        }
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		PreparedStatement pstmt = null;
		


		boolean isExist = false;
		try {
			pstmt = channelUserQry.isUserExistByParentIDRecursiveQry(con, networkCode, toCategoryCode, p_userCode, parentID, statusAllowed);
			try (ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				isExist = true;
			}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByParentIDRecursive]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByParentIDRecursive]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * Method isUserExistByOwnerID. This method is used to check that is user
	 * exist under the owner user passed as argument
	 * 
	 * @author sandeep.goel
	 * @param con
	 *            Connection
	 * @param networkCode
	 *            String
	 * @param toCategoryCode
	 *            String
	 * @param p_ownerID
	 *            String
	 * @param p_userCode
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public boolean isUserExistByOwnerID(Connection con, String networkCode, String toCategoryCode, String p_ownerID, String p_userCode, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUserExistByOwnerID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Network Code=");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" p_ownerID=");
        	loggerValue.append(p_ownerID);
        	loggerValue.append(" p_userCode=");
        	loggerValue.append(p_userCode);
            LOG.debug(methodName, loggerValue);
        }
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		 
		
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT 1 FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_code = ?");
		strBuff.append(" AND ( owner_id = ? OR user_id = ? )");
		// hrer owner_id = ? OR user_id = ? check is done for the transaction
		// only to the owner user only
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		boolean isExist = false;
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			// commented for DB2 pstmt = (OraclePreparedStatement)
			// con.prepareStatement(sqlSelect)commented
			
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, p_userCode);
			pstmt.setString(++i, p_ownerID);
			pstmt.setString(++i, p_ownerID);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				isExist = true;
			}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByOwnerID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByOwnerID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * Method isUserExist. This method check that "is user exist in the system?"
	 * no matter what is the domain and who is the parent or owner.
	 * 
	 * @author sandeep.goel
	 * @param con
	 *            Connection
	 * @param networkCode
	 *            String
	 * @param toCategoryCode
	 *            String
	 * @param p_userCode
	 *            String
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean isUserExist(Connection con, String networkCode, String toCategoryCode, String p_userCode, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUserExist";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Network Code=");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" p_userCode=");
        	loggerValue.append(p_userCode);
            LOG.debug(methodName, loggerValue);
        }
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}

		 
		
		final StringBuilder strBuff = new StringBuilder(" SELECT 1");
		strBuff.append(" FROM users u ");
		strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND category_code = ? AND user_code = ? ");
		final String sqlSelect = strBuff.toString();

		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		boolean isExist = false;
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, p_userCode);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				isExist = true;
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExist]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExist]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * This method is used to load the Channel User Details on the basis of the
	 * User_ID(Initiated_id) and it is used in ScheduledTopUp Process.
	 * 
	 * @author Ashish K
	 * @param Connection
	 *            con
	 * @param String
	 *            userId
	 * @return ChannelUserVO channelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChannelUserByUserID(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadChannelUserByUserID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: User ID=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		 
		ChannelUserVO channelUserVO = null;
		
		try {

			String selectQueryBuff = channelUserQry.loadChannelUserByUserIDQry();
			try (PreparedStatement pstmtSelect = con.prepareStatement(selectQueryBuff);)
			{
			pstmtSelect.setString(1, userId);
			pstmtSelect.setString(2, PretupsI.USER_STATUS_ACTIVE);
			pstmtSelect.setString(3, PretupsI.USER_PHONE_PRIM_STATUS);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password"));
				channelUserVO.setMsisdn(rs.getString("umsisdn"));
				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setMsisdn(rs.getString("upmsisdn"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				channelUserVO.setBalance(rs.getLong("balance"));
				channelUserVO.setPaymentTypes(rs.getString("payment_type"));
				channelUserVO.setUserPhoneVO(userPhoneVO);
				  if(SystemPreferences.USERWISE_LOAN_ENABLE){
	                	
	                	 PreparedStatement pstmtLoan = null;
	                     ResultSet rsLoan = null;
	                     LoginQry loginQry = (LoginQry)ObjectProducer.getObject(QueryConstants.LOGIN_QRY, QueryConstants.QUERY_PRODUCER);
	                     
	                	try {
	                	  String sqlLoanBuffer = loginQry.loadUserLoanDetailsQry();
	                	     pstmtLoan = con.prepareStatement(sqlLoanBuffer);
	                         if (LOG.isDebugEnabled())
	                             LOG.debug(methodName, "query:" + sqlLoanBuffer.toString());
	                         int i = 0;
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
	                         OracleUtil.closeQuietly(pstmtLoan);
	                         OracleUtil.closeQuietly(rsLoan);
	                	}
	                }

			}
		}
			}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserByUserID]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserByUserID]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	/**
	 * Method loadUsersForParentFixedCat This method loads all the users which
	 * are the direct child of the users of the p_fixedCat category.
	 * 
	 * @param con
	 * @param networkCode
	 * @param p_parentUserID
	 * @param toCategoryCode
	 * @param p_parentUserID
	 * @param userName
	 * @param userId
	 * @param p_fixedCat
	 * @param p_ctrlLvl
	 *            here if value of this parameter is 1 then check will be done
	 *            by parentID if value of this parameter is 2 then check will be
	 *            done by ownerID other wise no check will be required.
	 * @return ArrayList
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public ArrayList loadUsersForParentFixedCat(Connection con, String networkCode, String toCategoryCode, String p_parentUserID, String userName, String userId, String p_fixedCat, int p_ctrlLvl, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersForParentFixedCat";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" to Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append("ParentUserID=");
        	loggerValue.append(p_parentUserID);
        	loggerValue.append(" User Name::");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
        	loggerValue.append(" p_fixedCat:");
        	loggerValue.append(p_fixedCat);
        	loggerValue.append(" p_ctrlLvl:");
        	loggerValue.append(p_ctrlLvl);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt = null commented
		
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}


		final String sqlSelect = channelUserQry.loadUsersForParentFixedCatQry(statusAllowed, p_fixedCat, p_ctrlLvl);

		final ArrayList arrayList = new ArrayList();
		try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);){
			
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, userId);
			pstmt.setString(++i, userName);
			if (p_ctrlLvl == 1) {
				pstmt.setString(++i, p_parentUserID);
				pstmt.setString(++i, p_parentUserID);
			} else if (p_ctrlLvl == 2) {
				pstmt.setString(++i, p_parentUserID);
			}

			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("login_id"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("user_name"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("msisdn"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("user_id"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}

		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForParentFixedCat]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForParentFixedCat]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting arrayList.size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * Method loadUsersForHierarchyFixedCat This method loads all the users in
	 * the hierarchy of the users of the p_fixedCat categories.
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param p_parentUserID
	 * @param userName
	 * @param userId
	 * @param p_fixedCat
	 * @param p_ctrlLvl
	 *            here if value of this parameter is 1 then check will be done
	 *            by parentID if value of this parameter is 2 then check will be
	 *            done by ownerID other wise no check will be required.
	 * @return ArrayList
	 * @throws BTSLBaseException
	 *             ArrayList
	 * @author sandeep.goel
	 */
	public ArrayList loadUsersForHierarchyFixedCat(Connection con, String networkCode, String toCategoryCode, String p_parentUserID, String userName, String userId, String p_fixedCat, int p_ctrlLvl, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersForHierarchyFixedCat";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" p_parentUserID=");
        	loggerValue.append(p_parentUserID);
        	loggerValue.append(" User Name=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
        	loggerValue.append(" p_fixedCat=");
        	loggerValue.append(p_fixedCat);
        	loggerValue.append(" p_ctrlLvl=");
        	loggerValue.append(p_ctrlLvl);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt = null commented
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String statusAllowed = null;
		// user life cycle
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}


		final ArrayList arrayList = new ArrayList();
		try {

			pstmt =  channelUserQry.loadUsersForHierarchyFixedCatQry(con, statusAllowed, p_fixedCat, p_ctrlLvl, networkCode, toCategoryCode, userId, userName, p_parentUserID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}

		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForHierarchyFixedCat]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForHierarchyFixedCat]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting arrayList.size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * Method isUserExistForParentFixedCat This method is to check the existance
	 * of the user under the fixed parent categories for the passed values.
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param p_parentUserID
	 * @param p_userCode
	 *            String
	 * @param p_fixedCat
	 * @param p_ctrlLvl
	 *            here if value of this parameter is 1 then check will be done
	 *            by parentID if value of this parameter is 2 then check will be
	 *            done by ownerID other wise no check will be required.
	 * @return boolean
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public boolean isUserExistForParentFixedCat(Connection con, String networkCode, String toCategoryCode, String p_parentUserID, String p_userCode, String p_fixedCat, int p_ctrlLvl, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUserExistForParentFixedCat";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" p_parentUserID=");
        	loggerValue.append(p_parentUserID);
        	loggerValue.append(" p_userCode=");
        	loggerValue.append(p_userCode);
        	loggerValue.append(" p_fixedCat=");
        	loggerValue.append(p_fixedCat);
        	loggerValue.append(" p_ctrlLvl=");
        	loggerValue.append(p_ctrlLvl);
            LOG.debug(methodName, loggerValue);
        }
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		
		 
		boolean isExist = false;
		final StringBuilder strBuff = new StringBuilder("SELECT 1 FROM users u  ,users pu ");
		strBuff.append("WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ") AND u.category_code = ? AND u.user_code = ?");
		strBuff.append("AND u.parent_id=pu.user_id  AND pu.category_code IN (" + p_fixedCat + ") ");
		if (p_ctrlLvl == 1) {
			// strBuff.append(" AND ( pu.parent_id = ? OR u.user_id= ? )")
			strBuff.append(" AND pu.parent_id = case pu.parent_id when 'ROOT' then pu.parent_id else ? end ");
			strBuff.append(" AND u.parent_id = case pu.parent_id when'ROOT' then ? else u.parent_id end ");
			// here pu.parent_id = ? check by pu is done since pu.parent_id is
			// the parent of selected user's parent
			// for example POS to POSA and only to POSA which are child of POS,
			// under the hierarchy of POS's parent.
		} else if (p_ctrlLvl == 2) {
			// strBuff.append(" AND (pu.owner_id = ? OR u.user_id= ? ) ")
			strBuff.append(" AND pu.owner_id = ? ");
			// here pu.owner_id = ? or u.owner_id =? any can be used since owner
			// is same for all.
		}
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, p_userCode);
			if (p_ctrlLvl == 1) {
				pstmt.setString(++i, p_parentUserID);
				pstmt.setString(++i, p_parentUserID);
			} else if (p_ctrlLvl == 2) {
				pstmt.setString(++i, p_parentUserID);
			}

			try(ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				isExist = true;
			}

		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistForParentFixedCat]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistForParentFixedCat]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * Method isUserExistForHierarchyFixedCat This method is to check that "is
	 * user exist" in the hieerarchy of the passed category.
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param p_parentUserID
	 * @param p_userCode
	 *            String
	 * @param p_fixedCat
	 * @param p_ctrlLvl
	 *            here if value of this parameter is 1 then check will be done
	 *            by parentID if value of this parameter is 2 then check will be
	 *            done by ownerID other wise no check will be required.
	 * @return boolean
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public boolean isUserExistForHierarchyFixedCat(Connection con, String networkCode, String toCategoryCode, String p_parentUserID, String p_userCode, String p_fixedCat, int p_ctrlLvl, String p_txnSubType) throws BTSLBaseException {
		final String methodName = "isUserExistForHierarchyFixedCat";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" p_parentUserID=");
        	loggerValue.append(p_parentUserID);
        	loggerValue.append(" p_userCode=");
        	loggerValue.append(p_userCode);
        	loggerValue.append(" p_fixedCat=");
        	loggerValue.append(p_fixedCat);
        	loggerValue.append(" p_ctrlLvl=");
        	loggerValue.append(p_ctrlLvl);
            LOG.debug(methodName, loggerValue);
        }

		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isExist = false;


		try {
			pstmt = channelUserQry.isUserExistForHierarchyFixedCatQry(con, statusAllowed, p_fixedCat, p_ctrlLvl, networkCode, toCategoryCode, p_userCode, p_parentUserID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				isExist = true;
			}

		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistForHierarchyFixedCat]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistForHierarchyFixedCat]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * method loadUsersByDomainID This method load all the users of the
	 * specified category which are the direct child of the owner. This will be
	 * called to download users list at domain level and have direct T/R/W
	 * allowed
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param domainID
	 * @param userName
	 * @param userId
	 * @return
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public ArrayList loadUsersByDomainID(Connection con, String networkCode, String toCategoryCode, String domainID, String userName, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersByDomainID";
		ResultSet rs = null;
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" User Name=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }

		// commented for DB2 OraclePreparedStatement pstmt = null
		PreparedStatement pstmt = null;
		
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT user_id, user_name,msisdn,login_id FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_id != ? ");
		strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		// Sandeep goel ID USD001
		// query is changed to optimization and to remove the problem as owner
		// was not coming in the list.
		// and login user is also coming in the list
		strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) AND  (( parent_id IN  ( ");
		strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
		strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ) ");
		strBuff.append(" OR (parent_id ='ROOT'))ORDER BY user_name ");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		final ArrayList arrayList = new ArrayList();
		try {
			// commented for DB2 pstmt = (OraclePreparedStatement)
			// con.prepareStatement(sqlSelect)
			pstmt = con.prepareStatement(sqlSelect);
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, userId);
			pstmt.setString(++i, userName);
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, domainID);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}

		
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByDomainID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByDomainID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting arrayList Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}
	/**
	 * method loadUsersChnlBypassByDomainID This method load all the users of
	 * the specified category which are not the direct child of the owner. This
	 * will be called to download users list at domain level and have channel by
	 * pass T/R/W allowed
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param domainID
	 * @param userName
	 * @param userId
	 * @return
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public ArrayList loadUsersChnlBypassByDomainID(Connection con, String networkCode, String toCategoryCode, String domainID, String userName, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersChnlBypassByDomainID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" User Name=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }

		// commented for DB2 OraclePreparedStatement pstmt = null
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT user_id, user_name,login_id,msisdn FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_id != ? AND user_type='CHANNEL' ");
		strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) AND parent_id NOT IN  ( ");
		strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
		strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ");
		strBuff.append(" ORDER BY user_name ");
		final String sqlSelect = strBuff.toString();

		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}		final ArrayList arrayList = new ArrayList();
		try {
			
			pstmt = con.prepareStatement(sqlSelect);
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, userId);
			pstmt.setString(++i, userName);
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, domainID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}

		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersChnlBypassByDomainID]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersChnlBypassByDomainID]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting arrayList Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	/**
	 * method isUserExistsByDomainID This method check the user of the specified
	 * category which are the direct child of the owner. This will be called to
	 * check user at domain level and have direct T/R/W allowed
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param domainID
	 * @param userId
	 * @return
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public boolean isUserExistsByDomainID(Connection con, String networkCode, String toCategoryCode, String domainID, String userId, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUserExistsByDomainID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		CategoryDAO  categoryDao = new CategoryDAO();
		int seq=0;
		seq=categoryDao.loadSequenceNo(con, toCategoryCode, domainID);
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}

		 
		
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT 1 FROM users  ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_code = ? AND ");
		// Sandeep goel ID USD001
		// query is changed to optimization and to remove the problem as owner
		// was not coming.
		if(seq>1){
			strBuff.append("  ( (parent_id IN  ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ))  ) ORDER BY user_name ");
		}
		else if (seq==1){
			strBuff.append("  parent_id ='ROOT'  ORDER BY user_name ");
		}
		else{
			strBuff.append("  ( (parent_id IN  ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? )) OR (parent_id ='ROOT') ) ORDER BY user_name ");

		}
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		boolean isExist = false;
		try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, userId);
			if(seq>1){
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, domainID);
			}else if(seq==1){

			}else{
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, domainID);
			}
			try (ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				isExist = true;
			}

		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistsByDomainID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistsByDomainID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * method  This method check the user of the
	 * specified category which are not the direct child of the owner. This will
	 * be called to check user at domain level and have channel by pass T/R/W
	 * allowed
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param domainID
	 * @param userId
	 * @return
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public boolean isUsersExistChnlBypassByDomainID(Connection con, String networkCode, String toCategoryCode, String domainID, String userId, String p_txnSubType) throws BTSLBaseException {

		final String methodName = "isUsersExistChnlBypassByDomainID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }

		ResultSet rs = null;
		final StringBuilder strBuff = new StringBuilder();
		// user life cycle
		String statusAllowed = null;
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnSubType)) {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		strBuff.append(" SELECT user_id FROM users ");
		strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? ");
		strBuff.append(" AND user_code = ? ");
		strBuff.append(" AND parent_id<>owner_id ");
		strBuff.append(" ORDER BY user_name ");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}		boolean isExist = false;
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			int i = 0;
			pstmt.setString(++i, networkCode);
			pstmt.setString(++i, toCategoryCode);
			pstmt.setString(++i, userId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				isExist = true;
			}

		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUsersExistChnlBypassByDomainID]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUsersExistChnlBypassByDomainID]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * Method to load Channel User Details by External Code
	 * 
	 * @param con
	 * @param p_extCode
	 * @return
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChnlUserDetailsByExtCode(Connection con, String p_extCode) throws BTSLBaseException {
		final String methodName = "loadChnlUserDetailsByExtCode";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_extCode =");
        	loggerValue.append(p_extCode);
            LOG.debug(methodName, loggerValue);
        }
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		ChannelUserVO channelUserVO = null;
		ResultSet rs = null;
		try {
			final StringBuilder selectQueryBuff = new StringBuilder(
					" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
			selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on, u.pswd_modified_on,");
			selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
			selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
			selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
			selectQueryBuff.append(" uphones.msisdn prmsisdn, uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
			selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
			selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
			selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
			selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, uphones.created_on uphones_created  ");
			selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on,u.LONGITUDE,cusers.sos_allowed ,cusers.sos_allowed_amount, cusers.sos_threshold_limit  ");
			if (isTrfRuleUserLevelAllow) {
				selectQueryBuff.append(" , cusers.trf_rule_type ");
			}
			if (lmsAppl) {
				selectQueryBuff.append(" , cusers.lms_profile  ");
			}
			if (optInOutAllow) {
				selectQueryBuff.append(" , cusers.OPT_IN_OUT_STATUS ");
			}
			selectQueryBuff.append(" , cusers.CONTROL_GROUP ");
			selectQueryBuff.append(" FROM channel_users cusers right join users u on u.user_id=cusers.user_id");
			selectQueryBuff.append(" left join transfer_profile tp on cusers.transfer_profile_id=tp.profile_id");
			selectQueryBuff.append(" left join commission_profile_set cset on cusers.comm_profile_set_id=cset.comm_profile_set_id ");
			selectQueryBuff.append(" ,user_geographies geo,categories cat,domains dom,user_phones uphones,geographical_domains gdomains,geographical_domain_types gdt ");
			selectQueryBuff.append(" WHERE u.external_code=? AND uphones.user_id=u.user_id AND uphones.primary_number=? AND u.status <> ? AND u.status <> ? ");
			selectQueryBuff.append(" AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
			selectQueryBuff.append(" AND cat.domain_code= dom.domain_code   AND gdt.grph_domain_type=gdomains.grph_domain_type ");

			final String selectQuery = selectQueryBuff.toString();

			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}
			try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.setString(1, p_extCode);
			pstmtSelect.setString(2, PretupsI.YES);
			pstmtSelect.setString(3, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(4, PretupsI.USER_STATUS_CANCELED);
			rs = pstmtSelect.executeQuery();
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("webpassword"));
				channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
				channelUserVO.setLongitude(rs.getString("LONGITUDE"));
				if(channelSosEnable){
					channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
					channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
					channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
				}


				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("catseq"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				channelUserVO.setCategoryVO(categoryVO);

				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
				userPhoneVO.setMsisdn(rs.getString("prmsisdn"));
				userPhoneVO.setUserId(rs.getString("user_id"));
				userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setPinRequired(rs.getString("pin_required"));
				userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
				userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
				userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
				userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
				userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("uphones_created")));
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));

				channelUserVO.setUserPhoneVO(userPhoneVO);

				channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(con, channelUserVO.getUserID()));
			}
			return channelUserVO;
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChnlUserDetailsByExtCode]", "", "",
					"", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChnlUserDetailsByExtCode]", "", "",
					"", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
	}

	/**
	 * Method to load Channel User Details by Both MSISDN And External Code
	 * 
	 * @param con
	 * @param msisdn
	 * @param p_extCode
	 * @return
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChnlUserDetailsByMsisdnExtCode(Connection con, String msisdn, String p_extCode) throws BTSLBaseException {
		final String methodName = "loadChnlUserDetailsByMsisdnExtCode";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
        	loggerValue.append(" p_extCode=");
        	loggerValue.append(p_extCode);
            LOG.debug(methodName, loggerValue);
        }
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		ChannelUserVO channelUserVO = null;
		ResultSet rs = null;
		try {
			final StringBuilder selectQueryBuff = new StringBuilder(
					" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
			selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on,");
			selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
			selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
			selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
			selectQueryBuff.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
			selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
			selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
			selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
			selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, uphones.created_on uphones_created,cusers.sos_allowed ,cusers.sos_allowed_amount, cusers.sos_threshold_limit ");
			selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on  ");
			if (isTrfRuleUserLevelAllow) {
				selectQueryBuff.append(" , cusers.trf_rule_type  ");
			}
			if (lmsAppl) {
				selectQueryBuff.append(" , cusers.lms_profile  ");
			}
			if (optInOutAllow) {
				selectQueryBuff.append(" , cusers.OPT_IN_OUT_STATUS ");
			}
			selectQueryBuff.append(" , cusers.CONTROL_GROUP ");
			selectQueryBuff.append(" FROM channel_users cusers right join  users u on u.user_id=cusers.user_id ");
			selectQueryBuff.append(" left join transfer_profile tp on cusers.transfer_profile_id=tp.profile_id ");
			selectQueryBuff.append(" left join commission_profile_set cset on cusers.comm_profile_set_id=cset.comm_profile_set_id ");
			selectQueryBuff.append(" ,user_geographies geo,categories cat,domains dom,user_phones uphones,geographical_domains gdomains,geographical_domain_types gdt ");
			selectQueryBuff.append(" WHERE uphones.msisdn=? AND u.external_code=? AND uphones.user_id=u.user_id AND u.status <> ? AND u.status <> ? ");
			selectQueryBuff.append("  AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
			selectQueryBuff.append(" AND cat.domain_code= dom.domain_code  AND gdt.grph_domain_type=gdomains.grph_domain_type ");
			final String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}			try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.setString(1, msisdn);
			pstmtSelect.setString(2, p_extCode);
			pstmtSelect.setString(3, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(4, PretupsI.USER_STATUS_CANCELED);
			rs = pstmtSelect.executeQuery();
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("webpassword"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
				if(channelSosEnable){
					channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
					channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
					channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
				}

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("catseq"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				channelUserVO.setCategoryVO(categoryVO);

				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
				userPhoneVO.setMsisdn(msisdn);
				userPhoneVO.setUserId(rs.getString("user_id"));
				userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setPinRequired(rs.getString("pin_required"));
				userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
				userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
				userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
				userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
				userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("uphones_created")));
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				channelUserVO.setUserPhoneVO(userPhoneVO);

				channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(con, channelUserVO.getUserID()));
			}
			return channelUserVO;
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChnlUserDetailsByMsisdnExtCode]",
					"", "", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChnlUserDetailsByMsisdnExtCode]",
					"", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}

		}
	}

	/**
	 * Method used for loading the information of network admin
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param p_netCode
	 *            String
	 * @return UserVO userVO
	 * @exception BTSLBaseException
	 */
	public UserVO loadOptUserForO2C(Connection con, String p_netCode) throws BTSLBaseException {
		final String methodName = "loadOptUserForO2C";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(p_netCode);
            LOG.debug(methodName, loggerValue);
        }

		
		 
		final StringBuilder strBuff = new StringBuilder();
		UserVO userVO = null;

		strBuff.append("SELECT u.user_id ");
		strBuff.append(" FROM users u");
		strBuff.append(" WHERE u.network_code=? AND u.category_code=? AND u.user_type=?");

		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);){
			
			pstmt.setString(1, p_netCode);
			pstmt.setString(2, PretupsI.CATEGORY_CODE_NETWORK_ADMIN);
			pstmt.setString(3, PretupsI.CATEGORY_USER_TYPE);
			try (ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				userVO = new UserVO();
				userVO.setUserID(rs.getString("user_id"));
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadOptUserForO2C]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadOptUserForO2C]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userVO:");
				loggerValue.append(userVO);
				LOG.debug(methodName, loggerValue);
			}
		}

		return userVO;
	}

	/**
	 * Method to get the channel user details based on Login ID
	 * 
	 * @param con
	 * @param loginId
	 * @return
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChnlUserDetailsByLoginID(Connection con, String loginId) throws BTSLBaseException {
		final String methodName = "loadChnlUserDetailsByLoginID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: loginId =");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
            LOG.debug(methodName, loggerValue);
        }
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		ChannelUserVO channelUserVO = null;
			try {
			final StringBuilder selectQueryBuff = new StringBuilder(
					" SELECT u.LEVEL1_APPROVED_BY , u.LEVEL1_APPROVED_ON,u.LEVEL2_APPROVED_BY,u.LEVEL2_APPROVED_ON, u.user_id, u.password webpassword, u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
			selectQueryBuff.append(" u.employee_code,u.email,u.invalid_password_count,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on,");
			selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");

			// for Zebra and Tango by sanjeew date 06/07/07
			selectQueryBuff.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, cusers.low_bal_alert_allow, uphones.access_type, ");
			// End of Zebra and Tango

			selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
			selectQueryBuff.append(" cat.domain_code,dom.domain_name,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
			selectQueryBuff.append(" uphones.msisdn prmsisdn, uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
			selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on, cusers.auto_c2c_allow , cusers.auto_c2c_quantity,");
			selectQueryBuff.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
			selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
			selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, uphones.created_on uphones_created  ");
			selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on,u.LONGITUDE ");
			if (isTrfRuleUserLevelAllow) {
				selectQueryBuff.append(" , cusers.trf_rule_type  ");
			}
			if (lmsAppl) {
				selectQueryBuff.append(" , cusers.lms_profile  ");
			}
			if (optInOutAllow) {
				selectQueryBuff.append(" , cusers.OPT_IN_OUT_STATUS ");
			}

			selectQueryBuff.append(" , cusers.CONTROL_GROUP ,cusers.sos_allowed ,cusers.sos_allowed_amount,cusers.auto_o2c_allow ,cusers.autoo2c_transaction_amt,cusers.sos_threshold_limit,cusers.autoo2c_threshold_value,lr_allowed,lr_max_amount, gdomains.GRPH_DOMAIN_NAME");
			selectQueryBuff.append(" FROM channel_users cusers right join users u on u.user_id=cusers.user_id ");
			selectQueryBuff.append("left join transfer_profile tp on  cusers.transfer_profile_id=tp.profile_id  ");
			selectQueryBuff.append("left join commission_profile_set cset on cusers.comm_profile_set_id=cset.comm_profile_set_id ");
			selectQueryBuff.append( ",user_geographies geo,categories cat,domains dom,user_phones uphones,geographical_domains gdomains,geographical_domain_types gdt ");
			selectQueryBuff.append(" WHERE UPPER(u.login_id)=UPPER(?) AND uphones.user_id=u.user_id AND uphones.primary_number=? AND u.status <> ? AND u.status <> ? ");
			selectQueryBuff.append("  AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
			selectQueryBuff.append(" AND cat.domain_code= dom.domain_code  AND gdt.grph_domain_type=gdomains.grph_domain_type ");
			final String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}			try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.setString(1, loginId);
			pstmtSelect.setString(2, PretupsI.YES);
			pstmtSelect.setString(3, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(4, PretupsI.USER_STATUS_CANCELED);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setLevel1ApprovedBy(rs.getString("LEVEL1_APPROVED_BY"));
				channelUserVO.setLevel1ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("LEVEL1_APPROVED_ON")));
				channelUserVO.setLevel2ApprovedBy(rs.getString("LEVEL2_APPROVED_BY"));
				channelUserVO.setLevel2ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("LEVEL2_APPROVED_ON")));
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setEmail(rs.getString("email"));
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				channelUserVO.setPassword(rs.getString("webpassword"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setDomainName(rs.getString("domain_name"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				if(lrEnabled){
					channelUserVO.setLrAllowed(rs.getString("lr_allowed"));
					channelUserVO.setLrMaxAmount(rs.getLong("lr_max_amount"));
				}
				channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
				channelUserVO.setGeographicalDesc(rs.getString("GRPH_DOMAIN_NAME"));


				// for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setAccessType(rs.getString("access_type"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				// added by Praveen for autoc2c
				channelUserVO.setAutoc2callowed(rs.getString("auto_c2c_allow"));
				channelUserVO.setAutoc2cquantity(rs.getString("auto_c2c_quantity"));
				channelUserVO.setAutoo2callowed(rs.getString("auto_o2c_allow"));
				channelUserVO.setAutoO2CTxnValue(rs.getLong("autoo2c_transaction_amt"));
				channelUserVO.setAutoO2CThresholdLimit(rs.getLong("autoo2c_threshold_value"));
				if(channelSosEnable){
					channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
					channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
					channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
				}
				// End Zebra and Tango

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("catseq"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				channelUserVO.setCategoryVO(categoryVO);

				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
				userPhoneVO.setMsisdn(rs.getString("prmsisdn"));
				userPhoneVO.setUserId(rs.getString("user_id"));
				userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setPinRequired(rs.getString("pin_required"));
				userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
				userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
				userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
				userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
				userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("uphones_created")));
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				channelUserVO.setLongitude(rs.getString("LONGITUDE"));
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				if(channelSosEnable){
					channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
					channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
					channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
				}
				channelUserVO.setUserPhoneVO(userPhoneVO);

				channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(con, channelUserVO.getUserID()));
			}
			return channelUserVO;
		} 
			}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChnlUserDetailsByLoginID]", "", "",
					"", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadChnlUserDetailsByLoginIDloadChnlUserDetailsByLoginID", "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChnlUserDetailsByLoginID]", "", "",
					"", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
	}

	/**
	 * Update user status for MNP process
	 * 
	 * @param con
	 *            Connection
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public int updateUserStatus(Connection con, String msisdn, String status, String modifiy_by, Date p_modify_on) throws BTSLBaseException {

		final String methodName = "updateUserStatus";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
        	loggerValue.append(" modifiy_by=");
        	loggerValue.append(modifiy_by);
        	loggerValue.append(" modified_on=");
        	loggerValue.append(p_modify_on);
            LOG.debug(methodName, loggerValue);
        }


		 
		int updateCount = 0;
		final StringBuilder updateSql = new StringBuilder("UPDATE users SET previous_status=status, status=?, modified_on=?, modified_by=? ");
		updateSql.append("WHERE user_id=(SELECT UP.user_id FROM user_phones UP, users U WHERE U.user_id=UP.user_id AND U.status NOT IN('N','C') AND UP.msisdn=?) ");
		final String updateQuery = updateSql.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
		try(PreparedStatement pstmtUpdate = con.prepareStatement(updateQuery);) {
			
			pstmtUpdate.setString(1, status);
			pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_modify_on));
			pstmtUpdate.setString(3, modifiy_by);
			pstmtUpdate.setString(4, msisdn);
			updateCount = pstmtUpdate.executeUpdate();

		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserStatus]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserStatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting updateCount:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updateCount;
	}

	/**
	 * load Phone Exists With Status for MNP process
	 * 
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @return array List
	 * @throws BTSLBaseException
	 */
	public UserVO loadPhoneExistsWithStatus(Connection con, String msisdn) throws BTSLBaseException {
		final String methodName = "loadPhoneExistsWithStatus";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
            LOG.debug(methodName, loggerValue);
        }
		 
		final StringBuilder selectSql = new StringBuilder("select UP.msisdn,U.status ");
		selectSql.append(" FROM user_phones UP,users U WHERE UP.msisdn = ? ");
		selectSql.append(" AND U.user_id=UP.user_id AND (U.status <> 'N' AND U.status <> 'C')");
		final String selectQuery = selectSql.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
		UserVO userVO = null;
		try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
			
			pstmtSelect.setString(1, msisdn);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				userVO = new UserVO();
				userVO.setStatus(rs.getString("status"));
				userVO.setMsisdn(rs.getString("msisdn"));
			}
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadPhoneExistsWithStatus]", "", "",
					"", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadPhoneExistsWithStatus]", "", "",
					"", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting UserVO:");
				loggerValue.append(userVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return userVO;
	}

	/**
	 * Lock USER_PHONE table here
	 * 
	 * @param con
	 *            Connection
	 * @param userPhoneVO
	 *            UserPhoneVO
	 * @return void
	 * @throws BTSLBaseException
	 */
	public void lockUserPhonesTable(Connection con, UserPhoneVO userPhoneVO) throws BTSLBaseException {
		final String methodName = "lockUserPhonesTable";
		StringBuilder loggerValue= new StringBuilder();
		LogFactory.printLog(methodName,  "Entered", LOG);
		
		
		try {
			final StringBuilder selectQueryBuff = new StringBuilder("select last_transaction_status, modified_on ");
			selectQueryBuff.append(" FROM user_phones");
			// DB220120123for update WITH RS
			if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
				selectQueryBuff.append(" WHERE user_phones_id = ? for update with RS");
			} else {
				selectQueryBuff.append(" WHERE user_phones_id = ? for update NOWAIT");
			}

			final String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}			try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.setString(1, userPhoneVO.getUserPhonesId());
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
			}

		}
			}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[lockUserPhonesTable]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[lockUserPhonesTable]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			LogFactory.printLog(methodName,  "Exited", LOG);

		}

	}

	/**
	 * Load the category user. according to category id
	 * 
	 * @param con
	 * @param p_categoryCode
	 * @param networkCode
	 * @param userName
	 * @param p_ownerUserID
	 * @param statusUsed
	 * @param status
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public LinkedHashMap loadCategoryUsersForC2C(Connection con, String p_categoryCode, String networkCode, String p_productType, String p_senderId) throws BTSLBaseException {
		final String methodName = "loadCategoryUsers";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Category Code =");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" Network Code=");
        	loggerValue.append(networkCode);
            LOG.debug(methodName, loggerValue);
        }
		
		 
		final StringBuilder strBuff = new StringBuilder();
		final LinkedHashMap linkedHashMap = new LinkedHashMap();

		strBuff.append(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
		strBuff.append(" u.CATEGORY_CODE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
		strBuff.append(" FROM USERS u,CHANNEL_GRADES cg ,CATEGORIES cat WHERE u.user_id<> ? AND u.network_code = ? AND u.category_code = ? ");
		strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS= ? AND u.STATUS= ? ");

		final String sqlSelect = strBuff.toString();

		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

			
			int i = 0;
			i++;
			pstmt.setString(i, p_senderId);
			i++;
			pstmt.setString(i, networkCode);
			i++;
			pstmt.setString(i, p_categoryCode);
			i++;
			pstmt.setString(i, PretupsI.YES);
			i++;
			pstmt.setString(i, PretupsI.YES);

			try(ResultSet rs = pstmt.executeQuery();){
			while (rs.next()) {

				final ChannelUserVO channelVO = new ChannelUserVO();
				channelVO.setUserID(rs.getString("user_id"));
				channelVO.setUserName(rs.getString("user_name"));
				channelVO.setLoginID(rs.getString("login_id"));
				channelVO.setMsisdn(rs.getString("msisdn"));
				channelVO.setExternalCode(rs.getString("external_code"));
				channelVO.setCategoryCode(rs.getString("category_code"));
				channelVO.setCategoryName(rs.getString("category_name"));
				channelVO.setUserGrade(rs.getString("grade_code"));
				channelVO.setUserGradeName(rs.getString("grade_name"));
				channelVO.setStatus(rs.getString("status"));
				linkedHashMap.put(channelVO.getUserID(), channelVO);

			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUsers]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUsers]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting linkedHashMap size:");
				loggerValue.append(linkedHashMap.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return linkedHashMap;
	}

	/**
	 * @param con
	 *            Connection
	 * @param userId
	 *            String
	 * @return UserPhoneVO
	 * @throws BTSLBaseException
	 */
	public UserPhoneVO loadUserPhoneDetails(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadUserPhoneDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId =");
        	loggerValue.append(BTSLUtil.maskParam(userId));
            LOG.debug(methodName, loggerValue);
        }

		 
		UserPhoneVO phoneVO = null;
		try {

			final StringBuilder selectQueryBuff = new StringBuilder();
			selectQueryBuff.append(" SELECT USER_PHONES_ID, MSISDN, DESCRIPTION, SMS_PIN, INVALID_PIN_COUNT,USER_ID, PHONE_PROFILE, ");
			selectQueryBuff.append(" COUNTRY,PHONE_LANGUAGE FROM USER_PHONES WHERE USER_ID=? ");
			final String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}

			try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.setString(1, userId);
			try(ResultSet rs = pstmtSelect.executeQuery();){

			if (rs.next()) {
				phoneVO = new UserPhoneVO();
				phoneVO.setUserPhonesId(rs.getString("USER_PHONES_ID"));
				phoneVO.setMsisdn(rs.getString("MSISDN"));
				phoneVO.setDescription(rs.getString("DESCRIPTION"));
				phoneVO.setShowSmsPin(BTSLUtil.decryptText(rs.getString("SMS_PIN")));
				phoneVO.setInvalidPinCount(rs.getInt("INVALID_PIN_COUNT"));
				phoneVO.setUserId(rs.getString("USER_ID"));
				phoneVO.setCountry(rs.getString("COUNTRY"));
				phoneVO.setPhoneLanguage(rs.getString("PHONE_LANGUAGE"));
				phoneVO.setPhoneProfile(rs.getString("PHONE_PROFILE"));
			}
		} 
			}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[loadUserPhoneDetails]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[loadUserPhoneDetails]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			LogFactory.printLog(methodName, "Exited loadUserPhoneDetails ", LOG);
		}
		return phoneVO;
	}

	/**
	 * Method loadChannelUserDetailsByUserId. This method load user information
	 * by his msisdn
	 * 
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChannelUserDetailsByUserId(Connection con, String userId, String parentID) throws BTSLBaseException {
		final String methodName = "loadChannelUserDetailsByUserId";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId =");
        	loggerValue.append(userId);
        	loggerValue.append(" parentID=");
        	loggerValue.append(parentID);
            LOG.debug(methodName, loggerValue);
        }
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		ChannelUserVO channelUserVO = null;
		
		try {
			final StringBuilder selectQueryBuff = new StringBuilder(
					" SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
			selectQueryBuff.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on,");
			selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
			selectQueryBuff.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
			selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus, ");
			selectQueryBuff.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
			selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
			selectQueryBuff.append(" uphones.pin_modified_on,uphones.modified_on phone_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
			selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
			selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, ");

			selectQueryBuff.append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on ");
			selectQueryBuff.append(" ,uphones.PIN_RESET,uphones.last_access_on, uphones.msisdn user_msisdn,u.from_time,u.to_time,u.allowed_days ");
			if (lmsAppl) {
				selectQueryBuff.append(" , cusers.lms_profile  ");
			}
			if (optInOutAllow) {
				selectQueryBuff.append("  , cusers.OPT_IN_OUT_STATUS ");
			}
			selectQueryBuff.append(" , cusers.CONTROL_GROUP ");
			selectQueryBuff.append(" FROM channel_users cusers right join ( users u left join user_phones uphones on uphones.user_id=u.user_id AND uphones. primary_number='Y' ) on u.user_id=cusers.user_id ");
			selectQueryBuff.append("left join transfer_profile tp on cusers.transfer_profile_id=tp.profile_id ");
			selectQueryBuff.append("left join commission_profile_set cset on cusers.comm_profile_set_id=cset.comm_profile_set_id");
			selectQueryBuff.append( ",user_geographies geo,categories cat,domains dom,geographical_domains gdomains,geographical_domain_types gdt ");
			selectQueryBuff.append(" WHERE u.user_id=?  AND u.parent_id=?  AND u.status <> ? AND u.status <> ? ");
			selectQueryBuff.append("  AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code ");
			selectQueryBuff.append(" AND cat.domain_code= dom.domain_code  AND gdt.grph_domain_type=gdomains.grph_domain_type ");
			final String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}			try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.setString(1, userId);
			pstmtSelect.setString(2, parentID);
			pstmtSelect.setString(3, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(4, PretupsI.USER_STATUS_CANCELED);
			LogFactory.printLog(methodName, "Before result:" + userId, LOG);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			LogFactory.printLog(methodName, "After result:" + userId, LOG);

			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("webpassword"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));

				// for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setAccessType(rs.getString("access_type"));
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				// end Zebra and Tango
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("catseq"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));

				channelUserVO.setCategoryVO(categoryVO);

				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
				userPhoneVO.setMsisdn(rs.getString("user_msisdn"));
				userPhoneVO.setUserId(rs.getString("user_id"));
				userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setPinRequired(rs.getString("pin_required"));
				userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
				userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
				userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
				userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
				userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("phone_modified_on")));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("userphone_created_on")));
				userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				channelUserVO.setUserPhoneVO(userPhoneVO);

				channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(con, channelUserVO.getUserID()));
			}
			return channelUserVO;
		}
		}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserDetailsByUserId]", "",
					"", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserDetailsByUserId]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVo:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}

		}
	}

	/**
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @return String
	 * @throws BTSLBaseException
	 */
	public String loadParentUserMsisdn(Connection con, String p_info, String p_type) throws BTSLBaseException {
		final String methodName = "loadParentUserMsisdn";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_info =");
        	loggerValue.append(p_info);
            LOG.debug(methodName, loggerValue);
        }

		String MSISDN = null;
		 
		
		StringBuilder str = null;
		try {
			if ("MSISDN".equals(p_type)) {
				str = new StringBuilder(" SELECT UP1.MSISDN FROM USERS U, USER_PHONES UP, USER_PHONES UP1 WHERE ");
				str.append(" UP.MSISDN=? AND UP.USER_ID=U.USER_ID AND U.USER_TYPE=? AND U.PARENT_ID=UP1.user_id AND  U.STATUS <> 'N' AND  U.STATUS <> 'C' ");
			} else if ("LOGINID".equals(p_type)) {
				str = new StringBuilder(" SELECT U1.MSISDN FROM USERS U,  USERS U1 WHERE ");
				str.append(" U.login_id =?   AND U.parent_id=U1.USER_ID AND U.USER_TYPE=? AND  U.STATUS <> 'N' AND  U.STATUS <> 'C' ");
			} else if ("EXTGWCODE".equals(p_type)) {
				str = new StringBuilder(" SELECT U1.MSISDN FROM USERS U,  USERS U1 WHERE ");
				str.append("  U.EXTERNAL_CODE=?  AND U.parent_id=U1.USER_ID AND U.USER_TYPE=? AND  U.STATUS <> 'N' AND  U.STATUS <> 'C' ");
			}

			final String query = str.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}			try (PreparedStatement pstm = con.prepareStatement(query);)
			{
			if ("MSISDN".equals(p_type)) {
				pstm.setString(1, p_info);
				pstm.setString(2, PretupsI.USER_TYPE_STAFF);
			} else if ("LOGINID".equals(p_type)) {
				// pstm.setString(1,p_info)
				pstm.setString(1, p_info);
				pstm.setString(2, PretupsI.USER_TYPE_STAFF);
			} else if ("EXTGWCODE".equals(p_type)) {
				pstm.setString(1, p_info);
				pstm.setString(2, PretupsI.USER_TYPE_STAFF);
			}
			try( ResultSet rst = pstm.executeQuery();)
			{
			if (rst.next()) {
				MSISDN = rst.getString("MSISDN");
			}
		} 
			}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadParentUserMsisdn]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadParentUserMsisdn]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting MSISDN:");
				loggerValue.append(MSISDN);
				LOG.debug(methodName, loggerValue);
			}

		}
		return MSISDN;
	}

	/**
	 * @param con
	 *            Connection
	 * @param p_info
	 *            String
	 * @return String
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadActiveUserId(Connection con, String p_info, String p_type) throws BTSLBaseException {
		final String methodName = "loadActiveUserId";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_info =");
        	loggerValue.append(p_info);
            LOG.debug(methodName, loggerValue);
        }

		String userId = null;
		
		
		ChannelUserVO channelUserVO = null;
		try {
			final StringBuilder buf = new StringBuilder("SELECT U.USER_ID,U.MSISDN,U.CATEGORY_CODE,U.USER_TYPE,U.EMAIL , UP.DECRYPTION_KEY,UP.IMEI,UP.MHASH,UP.TOKEN,UP.TOKEN_LASTUSED_DATE,UP.SMS_PIN, U.NETWORK_CODE ");
			buf.append(" FROM USERS U,USER_PHONES UP WHERE U.USER_ID=UP.USER_ID AND ");

			if ("MSISDN".equals(p_type)) {
				buf.append(" U.MSISDN=? AND ");
			} else if ("LOGINID".equals(p_type)) {
				buf.append(" U.LOGIN_ID=? AND  ");
			} else if ("EXTGWCODE".equals(p_type)) {
				buf.append(" U.EXTERNAL_CODE=? AND  ");
			} else if("USERID".equals(p_type)) {
				buf.append("U.USER_ID=? AND");
			}
			
			buf.append(" U.STATUS <> 'N' AND ");
			buf.append(" U.STATUS <> 'C' AND U.USER_TYPE IN ('CHANNEL', 'STAFF','OPERATOR')"); // As

			final String query = buf.toString();

			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}			try (PreparedStatement pstm = con.prepareStatement(query);)
			{

			pstm.setString(1, p_info);

			try(ResultSet  rst = pstm.executeQuery();)
			{
			if (rst.next()) {
				channelUserVO = new ChannelUserVO();
				userId = rst.getString("USER_ID");
				channelUserVO.setUserID(userId);
				channelUserVO.setMsisdn(rst.getString("MSISDN"));
				channelUserVO.setUserType(rst.getString("USER_TYPE"));
				channelUserVO.setInfo1(rst.getString("DECRYPTION_KEY"));
				channelUserVO.setInfo2(rst.getString("IMEI"));
				channelUserVO.setInfo3(rst.getString("MHASH"));
				channelUserVO.setInfo4(rst.getString("TOKEN"));
				channelUserVO.setTokenLastUsedDate(rst.getTimestamp("TOKEN_LASTUSED_DATE"));
				channelUserVO.setEmail(rst.getString("EMAIL"));
				channelUserVO.setSmsPin(rst.getString("SMS_PIN"));
				channelUserVO.setCategoryCode(rst.getString("CATEGORY_CODE"));
				channelUserVO.setNetworkCode(rst.getString("NETWORK_CODE"));
			}

		} 
		}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadActiveUserId]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadActiveUserId]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVo:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	/**
	 * Method loadUsersDetails. This method is used to load all the information
	 * used to display in the ICCID MSISDN KEY MANAGEMENT Module
	 * 
	 * @author sandeep.goel
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @param userId
	 *            String
	 * @param statusUsed
	 *            String
	 * @param status
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadUsersDetailsForC2CReport(Connection con, String msisdn, String userId, String statusUsed, String status) throws BTSLBaseException {
		final String methodName="loadUsersDetailsForC2CReport";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
        	loggerValue.append(" p_stausUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }

		ChannelUserVO channelUserVO = null;
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
		strBuff.append(" USR.login_id,USR.password password1,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
		strBuff.append(" USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ");
		strBuff.append(" USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// firstname,lastname,company,fax
		// added
		// by
		// deepika
		// aggarwal
		strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
		strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
		strBuff.append("USR.created_by, USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
		strBuff.append(" USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
		strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
		strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
		strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
		strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type,MOD_USR.user_name request_user_name, USR_CAT.low_bal_alert_allow, ");
		strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed,USR_CAT.transfertolistonly, ");
		strBuff.append(" USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ");
		strBuff.append(" PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ");
		strBuff.append(" PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ");
		strBuff.append("ONR_CAT.category_name owner_cat,USR_PHONE.sms_pin user_sms_pin, USR_PHONE.pin_required required,l.lookup_name, D.domain_type_code, ");

		// for Zebra and Tango by sanjeew date 18/07/07
		strBuff.append(" USR_PHONE.access_type user_access_type ");
		// end of Zebra and Tango

		strBuff.append(" FROM users USR right join users MOD_USR on MOD_USR.user_id = USR.modified_by ");
		strBuff.append(" right join users USR_CRBY on USR_CRBY.user_id = USR.created_by ");
		strBuff.append( ",user_phones USR_PHONE, users PRNT_USR,users ONR_USR,categories USR_CAT,  ");
		strBuff.append("categories ONR_CAT, categories  PRNT_CAT,lookups l,user_geographies UG, geographical_domains GD, domains D ");
		strBuff.append(" WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ");

		if (statusUsed.equals(PretupsI.STATUS_IN)) {
			strBuff.append(" AND USR.status IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			strBuff.append(" AND USR.status NOT IN (" + status + ") ");
		} else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			strBuff.append("  AND USR.status =?  ");
		} else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			strBuff.append("  AND USR.status <> ?  ");
		}

		strBuff.append(" AND USR.category_code=USR_CAT.category_code  ");
		strBuff.append("  AND USR.status = l.lookup_code ");
		strBuff.append("  AND l.lookup_type= ? ");
		strBuff.append("  AND USR.user_id = UG.user_id  ");
		strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append(" AND USR_CAT.domain_code=D.domain_code ");

		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
			
			int i = 1;
			pstmtSelect.setString(i++, msisdn);
			if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
				pstmtSelect.setString(i++, status);
			}
			pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setBatchID(rs.getString("batch_id"));
				channelUserVO.setCreationType(rs.getString("creation_type"));
				channelUserVO.setUserID(rs.getString("usr_user_id"));
				channelUserVO.setUserName(rs.getString("usr_user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password1"));
				channelUserVO.setCategoryCode(rs.getString("usr_category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setReferenceID(rs.getString("reference_id"));
				channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("usr_status"));
				channelUserVO.setEmail(rs.getString("email"));
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				// end added by deepika aggarwal
				channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
				channelUserVO.setContactNo(rs.getString("contact_no"));
				channelUserVO.setDesignation(rs.getString("designation"));
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("usr_msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(rs.getTimestamp("created_on"));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
				channelUserVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
				channelUserVO.setAddress1(rs.getString("address1"));
				channelUserVO.setAddress2(rs.getString("address2"));
				channelUserVO.setCity(rs.getString("city"));
				channelUserVO.setState(rs.getString("state"));
				channelUserVO.setCountry(rs.getString("country"));
				channelUserVO.setSsn(rs.getString("ssn"));
				channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
				channelUserVO.setExternalCode(rs.getString("external_code"));
				channelUserVO.setShortName(rs.getString("short_name"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
				channelUserVO.setUserCode(rs.getString("user_code"));
				channelUserVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				// 5.1.3
				channelUserVO.setDomainName(rs.getString("domain_name"));

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
				categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setFixedRoles(rs.getString("fixed_roles"));
				categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
				categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setMaxTxnMsisdn(rs.getInt("max_txn_msisdn") + "");
				categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
				categoryVO.setServiceAllowed(rs.getString("services_allowed"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				channelUserVO.setCategoryVO(categoryVO);

				channelUserVO.setParentName(rs.getString("parent_name"));
				channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
				channelUserVO.setParentCategoryName(rs.getString("parent_cat"));

				channelUserVO.setOwnerName(rs.getString("owner_name"));
				channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
				channelUserVO.setOwnerCategoryName(rs.getString("owner_cat"));

				channelUserVO.setSmsPin(rs.getString("user_sms_pin"));
				channelUserVO.setPinRequired(rs.getString("required"));
				channelUserVO.setStatusDesc(rs.getString("lookup_name"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
				channelUserVO.setRequetedByUserName(rs.getString("request_user_name"));
				channelUserVO.setSuspendedByUserName(rs.getString("request_user_name"));
				channelUserVO.setCreatedByUserName(rs.getString("created_by_name"));

				// for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setAccessType(rs.getString("user_access_type"));
				// end Zebra and Tango

				channelUserVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn()));
			}
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsForC2CReport]",
					"", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "loadUsersDetailsForC2CReport", "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsForC2CReport]",
					"", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "loadUsersDetailsForC2CReport", "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVo:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	public int unmarkRequestUnderProcessPOS(Connection con, String requestID, UserPhoneVO userPhonesVO) throws BTSLBaseException {
		final String methodName = "unmarkRequestUnderProcessPOS";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: requestID =");
        	loggerValue.append(requestID);
        	loggerValue.append(" msisdn=");
        	loggerValue.append(userPhonesVO.getMsisdn());
            LOG.debug(methodName, loggerValue);
        }
	
		int updateCount = 0;
		
			final String updateQuery = "UPDATE user_phones SET last_transfer_id=?,last_transfer_type=?, last_transaction_on=?,last_transaction_status=?,temp_transfer_id=?,modified_by=?, modified_on=? WHERE user_phones_id=? ";
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}
			try(PreparedStatement pstmtUpdate = con.prepareStatement(updateQuery);){
			if (!BTSLUtil.isNullString(userPhonesVO.getLastTransferID())) {
				pstmtUpdate.setString(1, userPhonesVO.getLastTransferID());
			} else {
				pstmtUpdate.setNull(1, Types.VARCHAR);
			}
			if (!BTSLUtil.isNullString(userPhonesVO.getLastTransferType())) {
				pstmtUpdate.setString(2, userPhonesVO.getLastTransferType());
			} else {
				pstmtUpdate.setNull(2, Types.VARCHAR);
			}
			pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(userPhonesVO.getLastTransactionOn()));
			pstmtUpdate.setString(4, PretupsI.TXN_STATUS_COMPLETED);
			pstmtUpdate.setString(5, userPhonesVO.getTempTransferID());
			pstmtUpdate.setString(6, userPhonesVO.getModifiedBy());
			pstmtUpdate.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(userPhonesVO.getModifiedOn()));
			pstmtUpdate.setString(8, userPhonesVO.getUserPhonesId());
			updateCount = pstmtUpdate.executeUpdate();
			return updateCount;
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[unmarkRequestUnderProcessPOS]",
					requestID, userPhonesVO.getMsisdn(), "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[unmarkRequestUnderProcessPOS]",
					requestID, userPhonesVO.getMsisdn(), "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting requestID:");
				loggerValue.append(requestID);
				loggerValue.append(" update Count:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}// end of finally
	}

	/**
	 * @param con
	 *            Connection
	 * @param parentID
	 *            String
	 * @param userType
	 *            String
	 * @param loginId
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadStaffUserListByLoginforSuspResume(Connection con, String parentID, String userType, String loginId) throws BTSLBaseException {
		final String methodName = "loadStaffUserListByLogin";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: parentID =");
        	loggerValue.append(parentID);
        	loggerValue.append(" loginId=");
        	loggerValue.append(loginId);
            LOG.debug(methodName, loggerValue);
        }
		 
		ArrayList userList = null;
		UserVO channelUserTransferVO = null;
		try {
			final StringBuilder strBuff = new StringBuilder(" SELECT user_id, user_name, login_id, user_type, msisdn FROM users ");
			strBuff.append(" WHERE parent_id = ? and user_type=? AND status NOT IN('N','C') ");
			strBuff.append(" AND UPPER(login_id) LIKE UPPER(?) ");

			final String query = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}			try (PreparedStatement pstm = con.prepareStatement(query);)
			{
			pstm.setString(1, parentID);
			pstm.setString(2, userType);
			pstm.setString(3, loginId);
			try(ResultSet rst = pstm.executeQuery();)
			{
			userList = new ArrayList();
			while (rst.next()) {
				channelUserTransferVO = new UserVO();
				channelUserTransferVO.setUserID(rst.getString("user_id"));
				channelUserTransferVO.setLoginID(rst.getString("login_id"));
				channelUserTransferVO.setUserName(rst.getString("user_name"));
				userList.add(channelUserTransferVO);
			}
		} 
		}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadStaffUserListByLoginforSuspResume]", "", "", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadStaffUserListByLoginforSuspResume", "error.general.sql.processing");
		} catch (Exception e) {

			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadStaffUserListByLoginforSuspResume]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "loadStaffUserListByLoginforSuspResume", "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userList:");
				loggerValue.append(userList);
				LOG.debug(methodName, loggerValue);
			}
		}
		return userList;
	}

	/**
	 * user is barred or not
	 * 
	 * @param con
	 * @param msisdn
	 * @return boolean
	 * @throws BTSLBaseException
	 * @author Nilesh kumar
	 */

	public boolean isBarredUserExists(Connection con, String msisdn) throws BTSLBaseException {

		final String methodName = "isBarredUserExists";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
            LOG.debug(methodName, loggerValue);
        }
		
		boolean found = false;
		final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM barred_msisdns ");
		sqlBuff.append("WHERE msisdn=? AND user_type=? ");
		final String selectQuery = sqlBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
		try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
			
			pstmtSelect.setString(1, msisdn);
			pstmtSelect.setString(2, PretupsI.USER_TYPE_RECEIVER);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				found = true;
			}
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[isBarredUserExists]", "", "",
					"", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[isBarredUserExists]", "", "",
					"", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting found:");
				loggerValue.append(found);
				LOG.debug(methodName, loggerValue);
			}
		}
		return found;
	}

	/**
	 * load Parent Msisdn
	 * 
	 * @param con
	 * @param userId
	 * @return String
	 * @throws BTSLBaseException
	 * @author Nilesh kumar
	 */
	public String loadParentMsisdn(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadParentMsisdn";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: =");
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		
		
		final StringBuilder strBuff = new StringBuilder();
		String parentMsisdn = null;

		
			strBuff.append(" SELECT UP.msisdn FROM user_phones UP WHERE UP.user_id=? ");
			strBuff.append(" AND UP.primary_number='Y' ");
			final String qry = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(qry);
				LOG.debug(methodName, loggerValue);
			}			try (PreparedStatement pstmt = con.prepareStatement(qry);)
			{
			pstmt.setString(1, userId);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				parentMsisdn = rs.getString("MSISDN");
			}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadParentMsisdn]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadParentMsisdn]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting parentMsisdn:");
				loggerValue.append(parentMsisdn);
				LOG.debug(methodName, loggerValue);
			}
		}
		return parentMsisdn;
	}

	/**
	 * Method for deleting User from table user_threshold_counter.
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param p_childID
	 *            String
	 * @return updateCount integer
	 * @throws BTSLBaseException
	 * @author Nilesh kumar
	 */

	public void deleteChildUser(Connection con, String userId) throws BTSLBaseException

	{
		final String methodName = "countChildUser";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }

		
		final ResultSet rs = null;
		int updateCount = 0;

		final StringBuilder strBuff = new StringBuilder();
		strBuff.append("DELETE ");
		strBuff.append("FROM user_threshold_counter ");
		strBuff.append("WHERE user_id=? ");

		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}		try(PreparedStatement pstmtUpdate = con.prepareStatement(sqlSelect);) {
			
			pstmtUpdate.setString(1, userId);
			updateCount = pstmtUpdate.executeUpdate();
			if (updateCount > 0) {
				con.commit();
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDeleteHandler[countChildUser]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDeleteHandler[countChildUser]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");

		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting update Count:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		// return updateCount commented
	}

	/**
	 * Method for updating User in table user_threshold_counter.
	 * 
	 * @param con
	 *            java.sql.Connection
	 * @param p_childID
	 *            String
	 * @return updateCount integer
	 * @throws BTSLBaseException
	 * @author Nilesh kumar
	 */

	public void updateChildUser(Connection con, LowBalanceAlertVO lowBalanceAlertVo, Long autoC2CAmount) throws BTSLBaseException {
		final String methodName = "updateChildUser";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: lowBalanceAlertVo =");
        	loggerValue.append(lowBalanceAlertVo);
            LOG.debug(methodName, loggerValue);
        }

		 
		final ResultSet rs = null;
		int UpCount = 0;

		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE user_threshold_counter ");
			strBuff.append("SET previous_balance = ?,current_balance = ? ");
			strBuff.append("WHERE user_id=? ");

			final String sqlSelect = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlSelect);
				LOG.debug(methodName, loggerValue);
			}
			try(PreparedStatement pstmtUpdate = con.prepareStatement(sqlSelect);)
			{
			pstmtUpdate.setLong(1, lowBalanceAlertVo.getBalance());
			pstmtUpdate.setLong(2, (lowBalanceAlertVo.getBalance() + (autoC2CAmount)));
			pstmtUpdate.setString(3, lowBalanceAlertVo.getUserId());
			UpCount = pstmtUpdate.executeUpdate();
			if (UpCount > 0) {
				con.commit();
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDeleteHandler[countChildUser]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "countChildUser", "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDeleteHandler[countChildUser]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");

		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting updateCount:");
				loggerValue.append(UpCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		// return UpCount //modified by deepika aggarwal while pretups 6.0 code
		// optimisation

	}

	/**
	 * Method debitUserBalances() This method check user minimum balance limits
	 * and Debit the user Balances if limit does not cross by the new balance
	 * (existing balance-new requested credit balance)
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param isFromWeb        boolean
	 * @param forwardPath      String
	 * @return int
	 * @throws BTSLBaseException
	 */

	public ArrayList debitUserBalancesForO2C(Connection con, ChannelTransferVO channelTransferVO, String msisdn, int recordNumber, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
		final String methodName = "debitUserBalancesForO2C";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelTransferVO =");
        	loggerValue.append(channelTransferVO);
        	loggerValue.append(" msisdn=");
        	loggerValue.append(msisdn);
        	loggerValue.append(" recordNumber=");
        	loggerValue.append(recordNumber);
            LOG.debug(methodName, loggerValue);
        }
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        String walletForAdnlCmsn = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN);
		int updateCount = 0;
		final ArrayList errorList = new ArrayList();
		PreparedStatement pstmt = null;
		PreparedStatement psmtUpdate = null;
		PreparedStatement psmtInsertUserThreshold = null;
		ResultSet rs = null;
		StringBuilder strBuffSelect= channelUserQry.debitUserBalancesForO2CQry();

		final StringBuilder strBuffUpdate = new StringBuilder();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
		strBuffUpdate.append(" WHERE ");
		strBuffUpdate.append(" user_id = ? ");
		strBuffUpdate.append(" AND ");
		strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");

		if (userProductMultipleWallet) {
			strBuffUpdate.append("  and balance_type=? ");
		}

		// added by vikram
		final StringBuilder strBuffThresholdInsert = new StringBuilder();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		final String updateQuery = strBuffUpdate.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String sqlSelect = strBuffSelect.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Select Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("insertUserThreshold Query =");
			loggerValue.append(insertUserThreshold);
			LOG.debug(methodName, loggerValue);
		}
		try {
			psmtUpdate = con.prepareStatement(updateQuery);
			psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);

			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			TransferProfileProductVO transferProfileProductVO = null;
			long minBalance = 0;
			int maxAllowPct = 0;
			long maxAllowBalance = 0;
			// added by vikram
			final Date currentDate = new Date();
			long thresholdValue = -1;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			String userID = null;
			String profileID = null;
			String userCode = null;
			boolean bonusDeducted = false;
			boolean mainDeducted = false;
			/*
			 * In case of transfer : debit the from user which is logined user
			 * In case of retutn : debit the from user which is logined user In
			 * case of Withdraw : debit the To user which is Searched user
			 */
			userID = channelTransferVO.getFromUserID();
			profileID = channelTransferVO.getSenderTxnProfile();
			userCode = channelTransferVO.getFromUserCode();
			boolean isNotToExecuteQuery = false;
			final ArrayList errorBalanceNotExistList = new ArrayList();
			final ArrayList errorBalanceLessList = new ArrayList();
			final ArrayList errorBalanceMinList = new ArrayList();

			ListValueVO errorVO = null;
			errorVO = new ListValueVO();
			int itemsListSize = itemsList.size();
			for (int i = 0, k = itemsListSize; i < k; i++) {
				boolean toAddMoreError = true;
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				if(LOG.isDebugEnabled()){
					LOG.debug(methodName, "Loading details for userID="+userID+",channelTransferItemsVO.getProductCode()="+channelTransferItemsVO.getProductCode());
				}
				pstmt = con.prepareStatement(sqlSelect);
				pstmt.setString(1, userID);
				pstmt.setString(2, channelTransferItemsVO.getProductCode());
				pstmt.setString(3, channelTransferVO.getNetworkCode());
				pstmt.setString(4, channelTransferVO.getNetworkCodeFor());
				rs = pstmt.executeQuery();
				long balance = -1;
				long bonusBalance = -1;
				long checkBalance = 0;
				while (rs.next()) {
					if (walletForAdnlCmsn.equals(rs.getString("balance_type"))) {
						bonusBalance = rs.getLong("balance");
					} else {
						balance = rs.getLong("balance");
					}
				}
				if(LOG.isDebugEnabled()){
					LOG.debug(methodName, "After Fecthing  Balance="+balance+",bonusBalance="+bonusBalance);
				}
				if (balance == -1 && bonusBalance == -1) {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}

					errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.withdraw.userbalance.notexist"));
					errorList.add(errorVO);

				}

				if (userProductMultipleWallet) {
					channelTransferItemsVO.setPreviousBonusBalance(bonusBalance);
					channelTransferItemsVO.setAfterTransSenderPreviousBonusStock(bonusBalance);
					checkBalance = balance + bonusBalance;
					if (balance > 0) {
						mainDeducted = true;
					}
				} else {
					checkBalance = balance;
				}
				if(LOG.isDebugEnabled()){
					LOG.debug(methodName, "For userID="+userID+",checkBalance="+checkBalance+",channelTransferItemsVO.getRequiredQuantity()="+channelTransferItemsVO.getRequiredQuantity());
				}
				channelTransferItemsVO.setBalance(checkBalance);
				if (checkBalance < channelTransferItemsVO.getRequiredQuantity() && toAddMoreError) {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}

					errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.withdraw.userbalance.less"));
					errorList.add(errorVO);

				}
				channelTransferItemsVO.setPreviousBalance(balance);// set the
				// previous
				// balance
				channelTransferItemsVO.setAfterTransSenderPreviousStock(balance);

				// in the case of return we have not to check the min balance
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType()) && toAddMoreError) {
					/*
					 * check for the min balance for the product
					 */
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();
					maxAllowBalance = (balance * maxAllowPct) / 100;
					if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
						toAddMoreError = false;
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}

						errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.withdraw.allowedmaxpct.isless"));
						errorList.add(errorVO);

					}
					if (toAddMoreError) {
						if (balance > -1) {
							balance -= channelTransferItemsVO.getRequiredQuantity();
						}
						minBalance = transferProfileProductVO.getMinResidualBalanceAsLong();
						if (minBalance > balance) {
							toAddMoreError = false;
							if (!isNotToExecuteQuery) {
								isNotToExecuteQuery = true;
							}

							errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.withdraw.minbalance.reached"));
							errorList.add(errorVO);

						}
					}// if to add more errors
				}// if for transfer check
				if (!PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {

					if (userProductMultipleWallet) {
						if (balance > -1 && balance >= channelTransferItemsVO.getRequiredQuantity()) {
							balance -= channelTransferItemsVO.getRequiredQuantity();

						} else if (bonusBalance > -1) {
							if (!mainDeducted) {
								channelTransferVO.setReceiverCrQty(0);
							}
							bonusBalance -= (channelTransferItemsVO.getRequiredQuantity() - balance);
							channelTransferVO.setCommQty(channelTransferItemsVO.getRequiredQuantity() - balance);
							balance = 0;
							bonusDeducted = true;

						}
					}

					else if (balance > -1) {
						balance -= channelTransferItemsVO.getRequiredQuantity();
					}
				}

				if (!isNotToExecuteQuery) {
					int m = 0;
					psmtUpdate.setLong(++m, balance);
					psmtUpdate.setString(++m, channelTransferVO.getTransferType());
					psmtUpdate.setString(++m, channelTransferVO.getTransferID());
					psmtUpdate.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
					psmtUpdate.setString(++m, userID);
					psmtUpdate.setString(++m, channelTransferItemsVO.getProductCode());
					psmtUpdate.setString(++m, channelTransferVO.getNetworkCode());
					psmtUpdate.setString(++m, channelTransferVO.getNetworkCodeFor());
					if (userProductMultipleWallet) {
						psmtUpdate.setString(++m, defaultWallet);
						if (mainDeducted) {
							updateCount = psmtUpdate.executeUpdate();
						} else {
							updateCount = 1;
						}
						if (updateCount > 0 && bonusDeducted) {
							m = 0;
							psmtUpdate.clearParameters();
							psmtUpdate.setLong(++m, bonusBalance);
							psmtUpdate.setString(++m, channelTransferVO.getTransferType());
							psmtUpdate.setString(++m, channelTransferVO.getTransferID());
							psmtUpdate.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
							psmtUpdate.setString(++m, userID);
							psmtUpdate.setString(++m, channelTransferItemsVO.getProductCode());
							psmtUpdate.setString(++m, channelTransferVO.getNetworkCode());
							psmtUpdate.setString(++m, channelTransferVO.getNetworkCodeFor());
							psmtUpdate.setString(++m, walletForAdnlCmsn);
							updateCount = psmtUpdate.executeUpdate();
						}
					} else {
						updateCount = psmtUpdate.executeUpdate();
					}
					if (updateCount <= 0) {
						errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale,
								"batcho2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);

					}

					psmtUpdate.clearParameters();
					// added by nilesh
					// transferProfileProductVO
					// =(TransferProfileProductVO)profileDAO.loadTransferProfileProducts(con,profileID,channelTransferVO.getProductCode())
					// commented
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
					String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					final String remark = null;

					if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
						thresholdValue = transferProfileProductVO.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					}
					// end of nilesh

					// for zero balance counter added by vikram
					try {
						if ((channelTransferItemsVO.getPreviousBalance() >= thresholdValue && balance <= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue)) {
							//shashi :changes start here for Threshold balance alert
							UserBalancesVO vo = new UserBalancesVO();
							vo.setUserID(userID);
							vo.setProductCode(channelTransferItemsVO.getProductCode());
							vo.setNetworkCode(channelTransferVO.getNetworkCode());
							vo.setLastTransferID(channelTransferVO.getTransferID());
							new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,channelTransferVO.getCategoryCode());
							//end here

							m = 0;
							m++;
							psmtInsertUserThreshold.setString(m, userID);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferID());
							m++;
							psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getNetworkCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferItemsVO.getProductCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getType());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferType());
							m++;
							psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getCategoryCode());
							m++;
							psmtInsertUserThreshold.setLong(m, channelTransferItemsVO.getPreviousBalance());
							m++;
							psmtInsertUserThreshold.setLong(m, balance);
							m++;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							m++;
							psmtInsertUserThreshold.setString(m, threshold_type);
							m++;
							psmtInsertUserThreshold.setString(m, remark);
							updateCount = psmtInsertUserThreshold.executeUpdate();
							psmtInsertUserThreshold.clearParameters();
							if(LOG.isDebugEnabled()){
								LOG.debug(methodName, "psmtInsertUserThreshold="+updateCount);
							}
						}
						if(SystemPreferences.USERWISE_LOAN_ENABLE && channelTransferVO.getUserLoanVOList()!=null ) {
							try {
								ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
								for(int index=0;index<channelTransferVO.getUserLoanVOList().size();index++){
									userLoanVOList.add(channelTransferVO.getUserLoanVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(userLoanVOList, userID, balance,channelTransferItemsVO.getPreviousBalance(),channelTransferItemsVO.getProductCode(),channelTransferItemsVO.getProductType());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
							
							
						}
						//shishupal :changes start here for SoS eligibility alert
						else if (channelSosEnable&&channelTransferVO.getChannelSoSVOList()!=null)
						{
							try {
								ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
								for(int index=0;index<channelTransferVO.getChannelSoSVOList().size();index++){
									channeluserList.add(channelTransferVO.getChannelSoSVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, userID, balance,channelTransferItemsVO.getPreviousBalance());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
						}
					} catch (SQLException sqle) {
						loggerValue.setLength(0);
						loggerValue.append(SQL_EXCEPTION);
						loggerValue.append(sqle.getMessage());
						LOG.error(methodName, loggerValue);
						LOG.errorTrace(methodName, sqle);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForO2C]",
								channelTransferVO.getTransferID(), "", channelTransferVO.getNetworkCode(),
								"Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
					}// end of catch
				}// if to execute query
			}// for loop

			// for balance logger
			channelTransferVO.setEntryType(PretupsI.DEBIT);
		} catch (BTSLBaseException bbe) {
			throw bbe;
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			// EventHandle\r.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[debitUserBalances]","","","","SQL
			// Exception:"+sqe.getMessage()) commented
			// throw new BTSLBaseException(this, "debitUserBalances",
			// "error.general.sql.processing") commented
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			// EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[debitUserBalances]","","","","Exception:"+ex.getMessage())commented
			// throw new BTSLBaseException(this, "debitUserBalances",
			// "error.general.processing") commented
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtUpdate != null) {
                	psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtInsertUserThreshold != null) {
                	psmtInsertUserThreshold.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting updateCount:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return errorList;
	}

	/**
	 * Method loadAuthenticateUserDetails() is used to authenticate the user for
	 * XML Authentication
	 * 
	 * @param con
	 * @param loginId
	 * @param msisdn
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 * @author priyanka.goel
	 */
	public ChannelUserVO loadAuthenticateUserDetails(Connection con, String loginId, String msisdn) throws BTSLBaseException {
		final String methodName = "loadAuthenticateUserDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: loginId =");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
        	loggerValue.append(" msisdn=");
        	loggerValue.append(msisdn);
            LOG.debug(methodName, loggerValue);
        }
		ChannelUserVO channelUserVO = null;
		 
		
		final StringBuilder strBuff = new StringBuilder();

		strBuff.append(" SELECT u.user_id,u.login_id,u.user_name,u.status,u.msisdn,u.password,up.sms_pin FROM users u ,user_phones up");
		strBuff.append(" WHERE ");
		if (!BTSLUtil.isNullString(loginId)) {
			strBuff.append(" u.login_id = ? ");
		}
		if (!BTSLUtil.isNullString(msisdn)) {
			strBuff.append(" u.user_id = up.user_id AND up.msisdn = ? ");
		}
		strBuff.append(" AND u.status NOT IN (?,?,?)");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			if (!BTSLUtil.isNullString(loginId)) {
				pstmt.setString(1, loginId);
			}

			if (!BTSLUtil.isNullString(msisdn)) {
				pstmt.setString(1, msisdn);
			}
			pstmt.setString(2, PretupsI.USER_STATUS_CANCELED);
			pstmt.setString(3, PretupsI.USER_STATUS_DELETED);
			pstmt.setString(4, PretupsI.USER_STATUS_NEW);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setStatus(rs.getString("status"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password"));
				channelUserVO.setPinRequired(rs.getString("sms_pin"));
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadAuthenticateUserDetails]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadAuthenticateUserDetails]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			LogFactory.printLog(methodName, "Exiting: ************* ", LOG);
		}
		return channelUserVO;
	}

	/**
	 * Added by Ashutosh
	 * Method UpdateUserStateToActive
	 * This method changes user state from "preactive" or "churn" to active
	 * after a successfule transaction
	 * 
	 * @param con
	 *            Connection
	 * @param channelUserVO
	 *            ChannelUserVO
	 * @return the number of records updated
	 * @throws BTSLBaseException
	 */
	public int UpdateUserStateToActive(Connection con, ChannelUserVO channelUserVO) throws BTSLBaseException {
		final String methodName = "UpdateUserStateToActive";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId =");
        	loggerValue.append(channelUserVO.getUserID());
            LOG.debug(methodName, loggerValue);
        }
		final String currentStatus = channelUserVO.getStatus();

		final String qry = "UPDATE USERS SET STATUS=?,PREVIOUS_STATUS=? WHERE USER_ID=?";
		int updCount = -1;
		try(PreparedStatement pstmt = con.prepareStatement(qry);) {
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(qry);
				LOG.debug(methodName, loggerValue);
			}
			// Get Preapared Statement
			
			pstmt.setString(1, PretupsI.USER_STATUS_ACTIVE);
			pstmt.setString(2, channelUserVO.getStatus());
			pstmt.setString(3, channelUserVO.getUserID());
			// Execute Query
			updCount = pstmt.executeUpdate();
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[UpdateUserStateToActive]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[UpdateUserStateToActive]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting updateCount:");
				loggerValue.append(updCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updCount;
	}

	public ArrayList getActiveOrSuspendesAssoMsisdnList(Connection con, String msisdn) throws BTSLBaseException {

		ChannelUserTypeVO channelUserTypeVO = null;
		final ArrayList list = new ArrayList();
		StringBuilder loggerValue = new StringBuilder(); 
		 
		final String selectQuery = "SELECT CU.ASSOCIATED_MSISDN, CU.ASSOCIATED_MSISDN from CHANNEL_USERS CU, USERS U where CU.USER_ID=U.USER_ID and  (U.STATUS='Y' OR U.STATUS='S') and CU.ASSOCIATED_MSISDN= ? ";

		try(PreparedStatement preparedStatement = con.prepareStatement(selectQuery);) {

			

			preparedStatement.setString(1, msisdn);
			try(ResultSet resultSet = preparedStatement.executeQuery();)
			{
			while (resultSet.next()) {
				channelUserTypeVO = new ChannelUserTypeVO();
				channelUserTypeVO.setAssMsisdn(resultSet.getString("ASSOCIATED_MSISDN"));
				list.add(channelUserTypeVO);
			}
		}
		}

		catch (SQLException sqlException) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqlException.getMessage());
			LOG.error("getActiveOrSuspendesAssoMsisdnList", loggerValue);
			LOG.errorTrace("loadChannelUserTypeList", sqlException);
			throw new BTSLBaseException(this, "loadChannelUserTypeList", "error.general.processing",sqlException);
		}

		return list;

	}

	/**
	 * method loadUsersChnlBypassByGeo
	 * This method load all the users of the specified category which are not
	 * the direct child of the owner.
	 * This will be called to download users list at geogpraphy level and have
	 * channel by pass T/R/W allowed
	 * 
	 * @param con
	 * @param networkCode
	 * @param toCategoryCode
	 * @param domainID
	 * @param userName
	 * @param userId
	 * @return
	 * @throws BTSLBaseException
	 *             ArrayList
	 */
	public ArrayList loadUsersChnlBypassByGeo(Connection con, String networkCode, String toCategoryCode, String domainID, String userName, String userId) throws BTSLBaseException {
		final String methodName = "loadUsersChnlBypassByGeo";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" User Name=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmt = null;
		 
		ResultSet rs1 = null;
		StringBuilder qrySelect = null;
		final ArrayList arrayList = new ArrayList();
		final List list = new ArrayList();

		// fetch the Hierarchy details of the user
		try {

			String qry = channelUserQry.loadUsersChnlBypassByGeoQry();
			try(PreparedStatement prepSelect = con.prepareStatement(qry);)
			{
			prepSelect.setString(1, userId);
			try(ResultSet rs = prepSelect.executeQuery();)
			{
			while (rs.next()) {
				list.add(new ListValueVO(rs.getString("grph_domain_code"), rs.getString("GRPH_DOMAIN_NAME")));
			}

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT u.user_id, u.user_name, u.msisdn, u.login_id FROM users u,user_geographies ug  ");
			strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y' AND u.category_code = ? AND u.user_id != ? AND u.user_type='CHANNEL' ");
			strBuff.append(" AND u.user_id=ug.user_id AND ug.GRPH_DOMAIN_CODE = ?");
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND u.parent_id NOT IN  ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ");
			strBuff.append(" ORDER BY u.user_name ");
			final String sqlSelect = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlSelect);
				LOG.debug(methodName, loggerValue);
			}
			pstmt = con.prepareStatement(sqlSelect);
			int listSize = list.size();
			for (int k = 0; k < listSize; k++) {
				int i = 0;
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, userId);
				pstmt.setString(++i, ((ListValueVO) list.get(k)).getLabel());
				pstmt.setString(++i, userName);
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, domainID);
				rs1 = pstmt.executeQuery();
				while (rs1.next()) {
					AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
					autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
					autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
					autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
					autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
					arrayList.add(autoCompleteUserDetailsResponseVO);
				}
			}
		} 
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersChnlBypassByGeo]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersChnlBypassByGeo]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try{
            	if (rs1!= null){
            		rs1.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
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
				loggerValue.setLength(0);
				loggerValue.append("Exiting ArrayList size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	public ArrayList loadUsersByGeo(Connection con, String networkCode, String toCategoryCode, String domainID, String userName, String userId) throws BTSLBaseException {
		final String methodName = "loadUsersByGeo";
		StringBuilder loggerValue = new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" User Name=");
        	loggerValue.append(userName);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		 
		ResultSet rs = null;
		ResultSet rs1 = null;
		StringBuilder qrySelect = null;
		final ArrayList arrayList = new ArrayList();
		final ArrayList list = new ArrayList();

		// fetch the Hierarchy details of the user
		try {
			String qry = channelUserQry.loadUsersChnlBypassByGeoQry();

			try(PreparedStatement prepSelect = con.prepareStatement(qry);)
			{
			prepSelect.setString(1, userId);
			rs = prepSelect.executeQuery();
			while (rs.next()) {
				list.add(new ListValueVO(rs.getString("grph_domain_code"), rs.getString("GRPH_DOMAIN_NAME")));
			}

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT u.user_id, u.user_name,u.msisdn,u.login_id FROM users u,user_geographies ug  ");
			strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y' AND u.category_code = ? AND u.user_id != ? AND u.user_type='CHANNEL' ");
			strBuff.append(" AND u.user_id=ug.user_id AND ug.GRPH_DOMAIN_CODE = ?");
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND u.parent_id IN  ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ");
			strBuff.append(" ORDER BY u.user_name ");
			final String sqlSelect = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlSelect);
				LOG.debug(methodName, loggerValue);
			}
			try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
			{
		    int listSize = list.size();
			for (int k = 0; k < listSize; k++) {
				int i = 0;
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, userId);
				pstmt.setString(++i, ((ListValueVO) list.get(k)).getLabel());
				pstmt.setString(++i, userName);
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, domainID);
				rs1 = pstmt.executeQuery();
				while (rs1.next()) {
					AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
					autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
					autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
					autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
					autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
					arrayList.add(autoCompleteUserDetailsResponseVO);
				}
			}
		} 
		}
	}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[methodName]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[methodName]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try{
            	if (rs!= null){
            		rs.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
			try{
            	if (rs1!= null){
            		rs1.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting ArrayList size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}

	public boolean isUserExistsByGeo(Connection con, String networkCode, String toCategoryCode, String domainID, String msisdn, String userId) throws BTSLBaseException {

		final String methodName = "isUserExistsByGeo";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		 
		
		ResultSet rs = null;
		ResultSet rs1 = null;
		StringBuilder qrySelect = null;
		final ArrayList arrayList = new ArrayList();
		final ArrayList list = new ArrayList();
		boolean isExist = false;

		// fetch the Hierarchy details of the user
		try {

			String sql = channelUserQry.isUserExistsByGeoQry();
			try(PreparedStatement prepSelect = con.prepareStatement(sql);)
			{
			prepSelect.setString(1, userId);
			rs = prepSelect.executeQuery();
			while (rs.next()) {
				list.add(new ListValueVO(rs.getString("grph_domain_code"), rs.getString("GRPH_DOMAIN_NAME")));
			}

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT 1 FROM users u,user_geographies ug  ");
			strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y' AND u.category_code = ? AND u.user_code=? ");
			strBuff.append(" AND u.user_id=ug.user_id AND ug.GRPH_DOMAIN_CODE = ?");
			strBuff.append(" AND u.parent_id IN  ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id ");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ");
			final String sqlSelect = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlSelect);
				LOG.debug(methodName, loggerValue);
			}
			try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
			{
		    int listSize = list.size();
			for (int k = 0; k < listSize; k++) {
				int i = 0;
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, msisdn);
				pstmt.setString(++i, ((ListValueVO) list.get(k)).getLabel());
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, domainID);
				rs1 = pstmt.executeQuery();
				if (rs1.next()) {
					isExist = true;
				}
			}
		} 
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[methodName]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[methodName]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	public boolean isUserExistsChnlByPassByGeo(Connection con, String networkCode, String toCategoryCode, String domainID, String msisdn, String userId) throws BTSLBaseException {

		final String methodName = "isUserExistsChnlByPassByGeo";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code=");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" domainID=");
        	loggerValue.append(domainID);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmt = null;
		PreparedStatement prepSelect = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		StringBuilder qrySelect = null;
		final ArrayList arrayList = new ArrayList();
		final ArrayList list = new ArrayList();
		boolean isExist = false;

		// fetch the Hierarchy details of the user
		try {
			String sql = channelUserQry.isUserExistsByGeoQry(); 
			prepSelect = con.prepareStatement(sql);
			prepSelect.setString(1, userId);
			rs = prepSelect.executeQuery();
			while (rs.next()) {
				list.add(new ListValueVO(rs.getString("grph_domain_code"), rs.getString("GRPH_DOMAIN_NAME")));
			}

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(" SELECT 1 FROM users u,user_geographies ug  ");
			strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y' AND u.category_code = ? AND u.user_code=? ");
			strBuff.append(" AND u.user_id=ug.user_id AND ug.GRPH_DOMAIN_CODE = ?");
			strBuff.append(" AND u.parent_id NOT IN  ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ");
			final String sqlSelect = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlSelect);
				LOG.debug(methodName, loggerValue);
			}
			pstmt = con.prepareStatement(sqlSelect);
			int listSize = list.size();
			for (int k = 0; k < listSize; k++) {
				int i = 0;
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, msisdn);
				pstmt.setString(++i, ((ListValueVO) list.get(k)).getLabel());
				pstmt.setString(++i, networkCode);
				pstmt.setString(++i, toCategoryCode);
				pstmt.setString(++i, domainID);
				rs1 = pstmt.executeQuery();
				if (rs1.next()) {
					isExist = true;
				}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[methodName]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[methodName]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (prepSelect != null) {
                	prepSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}

	/**
	 * Method creditUserBalancesForMultipleWallet() This method check user
	 * minimum balance limits
	 * and Debit the user Balances if limit does not cross by the new balance
	 * and it makes debit in both wallets if partial allowed is configured.
	 * (existing balance-new requested credit balance)
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param isFromWeb
	 *            boolean
	 * @param forwardPath
	 *            String
	 * @return int
	 * @throws BTSLBaseException
	 */

	public int creditUserBalancesForMultipleWallet(Connection con, ChannelTransferVO channelTransferVO, boolean isFromWeb, String forwardPath) throws BTSLBaseException {
		final String methodName = "creditUserBalancesForMultipleWallet";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelTransferVO =");
        	loggerValue.append(channelTransferVO);
        	loggerValue.append(" isFromWeb=");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(" forwardPath=");
        	loggerValue.append(forwardPath);
            LOG.debug(methodName, loggerValue);
        }
		int userBalanceUpdateInsertCount = 0;
		int insertUserThresCount = 0;
		String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET); 
		String walletForAdnlCmsn = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN);
		ResultSet rs = null;

		final StringBuilder strBuffUpdate = new StringBuilder();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
		strBuffUpdate.append(" WHERE ");
		strBuffUpdate.append(" user_id = ? ");
		strBuffUpdate.append(" AND ");
		strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
		strBuffUpdate.append(" AND balance_type = ?");
		final StringBuilder strBuffInsert = new StringBuilder();
		strBuffInsert.append(" INSERT ");
		strBuffInsert.append(" INTO user_balances ");
		strBuffInsert.append(" ( prev_balance, daily_balance_updated_on, balance, last_transfer_type, last_transfer_no, last_transfer_on, ");
		strBuffInsert.append(" user_id, product_code , network_code, network_code_for,");
		strBuffInsert.append(" balance_type )");
		strBuffInsert.append(" VALUES ");
		strBuffInsert.append(" (?,?,?,?,?,?,?,?,?,?,?) ");
		final StringBuilder strBuffThresholdInsert = new StringBuilder();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert.append("  type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		final String updateQuery = strBuffUpdate.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String insertQuery = strBuffInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Insert Query =");
			loggerValue.append(insertQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String sqlSelect = channelUserQry.creditUserBalancesForMultipleWalletQry();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Select Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("insertUserThreshold =");
			loggerValue.append(insertUserThreshold);
			LOG.debug(methodName, loggerValue);
		}

		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);
				PreparedStatement psmtUpdateUserBalance = con.prepareStatement(updateQuery);
				PreparedStatement psmtInsertUserBalance = con.prepareStatement(insertQuery);
				PreparedStatement psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);) {

			

			PreparedStatement handlerStmt = null;
			boolean insert = false;
			TransferProfileProductVO transferProfileProductVO = null;
			long maxBalance = 0;
			final Date currentDate = new Date();
			long thresholdValue = -1;
			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			ChannelTransferItemsVO channelTransferItemsVO = null;
			String userID = null;
			String profileID = null;
			userID = channelTransferVO.getToUserID();
			profileID = channelTransferVO.getReceiverTxnProfile();
			boolean isNotToExecuteQuery = false;
			final ArrayList errorList = new ArrayList();
			KeyArgumentVO keyArgumentVO = null;
			for (int i = 0, k = itemsList.size(); i < k; i++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				pstmt.setString(1, userID);
				pstmt.setString(2, channelTransferItemsVO.getProductCode());
				pstmt.setString(3, channelTransferVO.getNetworkCode());
				pstmt.setString(4, channelTransferVO.getNetworkCodeFor());

				rs = pstmt.executeQuery();
				long balance = -1;
				long bonusBalance = -1;
				long commQty = 0;
				long recCreditQty = 0;
				while (rs.next()) {
					if (rs.getString("balance_type").equals(defaultWallet)) {
						balance = rs.getLong("balance");
					} else {
						bonusBalance = rs.getLong("balance");
					}

				}

				if (balance > -1) {
					channelTransferItemsVO.setPreviousBalance(balance);
					channelTransferItemsVO.setAfterTransReceiverPreviousStock(balance);
					channelTransferItemsVO.setReceiverPreviousStock(balance);
					if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
						recCreditQty = channelTransferItemsVO.getReceiverCreditQty();
						balance += channelTransferItemsVO.getReceiverCreditQty();
					} else {
						recCreditQty = channelTransferItemsVO.getApprovedQuantity();
						balance += channelTransferItemsVO.getApprovedQuantity();
					}

				} else {
					recCreditQty = channelTransferItemsVO.getApprovedQuantity();
					channelTransferItemsVO.setPreviousBalance(0);
					channelTransferItemsVO.setAfterTransReceiverPreviousStock(0);
				}

				pstmt.clearParameters();

				// in the case of return we have not to check the max balance
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
					/*
					 * check for the max balance for the product
					 */
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					maxBalance = transferProfileProductVO.getMaxBalanceAsLong();
					commQty = channelTransferItemsVO.getCommQuantity();
					if (maxBalance < balance) {
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						keyArgumentVO = new KeyArgumentVO();
						if (isFromWeb) {
							final String arg[] = { channelTransferItemsVO.getShortName() };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey("error.transfer.maxbalance.reached");
						} else {
							final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(transferProfileProductVO
									.getMaxBalanceAsLong()) };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY);
						}
						errorList.add(keyArgumentVO);
					}
					// check for the very first txn of the user containg the
					// order value larger than maxBalance
					else if (balance == -1 && maxBalance < channelTransferItemsVO.getApprovedQuantity()) {
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						keyArgumentVO = new KeyArgumentVO();
						if (isFromWeb) {
							final String arg[] = { channelTransferItemsVO.getShortName() };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey("error.transfer.maxbalance.reached");
						} else {
							final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(transferProfileProductVO
									.getMaxBalanceAsLong()) };
							keyArgumentVO.setArguments(arg);
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY);
						}
						errorList.add(keyArgumentVO);
					}
				}
				if (!PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {
					recCreditQty = channelTransferItemsVO.getMainDebitQty();
					commQty = channelTransferItemsVO.getBonusDebtQty();
				}
				if (!isNotToExecuteQuery) {
					int m = 0;
					// update
					if (balance > -1) {
						handlerStmt = psmtUpdateUserBalance;
					} else {
						// insert
						handlerStmt = psmtInsertUserBalance;
						insert = true;
						if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
							balance = channelTransferItemsVO.getReceiverCreditQty();
						} else {
							balance = channelTransferItemsVO.getApprovedQuantity();
						}
						channelTransferItemsVO.setPreviousBalance(0);
						handlerStmt.setLong(++m, 0);// previous balance
						handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));// updated
						// on
						// date
					}
					handlerStmt.setLong(++m, balance - commQty);
					handlerStmt.setString(++m, channelTransferVO.getTransferType());
					handlerStmt.setString(++m, channelTransferVO.getTransferID());
					handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
					handlerStmt.setString(++m, userID);

					// where
					handlerStmt.setString(++m, channelTransferItemsVO.getProductCode());
					handlerStmt.setString(++m, channelTransferVO.getNetworkCode());
					handlerStmt.setString(++m, channelTransferVO.getNetworkCodeFor());
					handlerStmt.setString(++m, defaultWallet);
					// handlerStmt1=handlerStmt commented
					if ((BTSLUtil.isNullString(channelTransferVO.getUserWalletCode()) || channelTransferVO.getUserWalletCode().equals(defaultWallet)) && recCreditQty > 0) {
						userBalanceUpdateInsertCount = handlerStmt.executeUpdate();
						if (bonusBalance > -1 && insert) {
							handlerStmt = psmtUpdateUserBalance;
							insert = false;
						} else if (bonusBalance == -1) {
							handlerStmt = psmtInsertUserBalance;
							insert = true;
						}

					} else {
						userBalanceUpdateInsertCount = 1;
						if (commQty == 0 && (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType()))) {
							commQty = recCreditQty;
						}
					}
					handlerStmt.clearParameters();
					if (userBalanceUpdateInsertCount > 0 && commQty > 0) {
						m = 0;
						if (insert) {
							if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
								balance = channelTransferItemsVO.getReceiverCreditQty();
							} else {
								balance = channelTransferItemsVO.getApprovedQuantity();
							}
							channelTransferItemsVO.setPreviousBalance(0);
							handlerStmt.setLong(++m, 0);// previous balance
							handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
						}
						if (bonusBalance > -1) {

							if (BTSLUtil.isNullString(channelTransferVO.getUserWalletCode()) || channelTransferVO.getUserWalletCode().equals(
									defaultWallet)) {
								handlerStmt.setLong(++m, (commQty + bonusBalance));
							} else {
								handlerStmt.setLong(++m, (channelTransferItemsVO.getApprovedQuantity() + bonusBalance));
							}
						} else {
							if (BTSLUtil.isNullString(channelTransferVO.getUserWalletCode()) || channelTransferVO.getUserWalletCode().equals(
									defaultWallet)) {
								handlerStmt.setLong(++m, commQty);
							} else {
								handlerStmt.setLong(++m, channelTransferItemsVO.getApprovedQuantity());
							}
						}
						handlerStmt.setString(++m, channelTransferVO.getTransferType());
						handlerStmt.setString(++m, channelTransferVO.getTransferID());
						handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
						handlerStmt.setString(++m, userID);

						// where
						handlerStmt.setString(++m, channelTransferItemsVO.getProductCode());
						handlerStmt.setString(++m, channelTransferVO.getNetworkCode());
						handlerStmt.setString(++m, channelTransferVO.getNetworkCodeFor());
						handlerStmt.setString(++m, walletForAdnlCmsn);
						userBalanceUpdateInsertCount = handlerStmt.executeUpdate();
					}

					handlerStmt.clearParameters();

					if (userBalanceUpdateInsertCount <= 0) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
								"ChannelUserDAO[creditUserBalancesForMultipleWallet]", "", "", "", "BTSLBaseException: update count <=0");
						throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
					}
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
					String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					final String remark = null;
					if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
						thresholdValue = transferProfileProductVO.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					}

					try {

						if ((channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance >= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue)) {
							psmtInsertUserThreshold.clearParameters();
							m = 0;
							m++;
							psmtInsertUserThreshold.setString(m, userID);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferID());
							m++;
							psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getNetworkCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferItemsVO.getProductCode());

							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getType());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferType());
							if (balance >= thresholdValue) {
								m++;
								psmtInsertUserThreshold.setString(m, PretupsI.ABOVE_THRESHOLD_TYPE);
							}

							else {
								m++;
								psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
							}
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getReceiverCategoryCode());
							m++;
							psmtInsertUserThreshold.setLong(m, channelTransferItemsVO.getPreviousBalance());
							m++;
							psmtInsertUserThreshold.setLong(m, balance);
							m++;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							// added by nilesh
							m++;
							psmtInsertUserThreshold.setString(m, threshold_type);
							m++;
							psmtInsertUserThreshold.setString(m, remark);
							insertUserThresCount = psmtInsertUserThreshold.executeUpdate();
							LOG.debug(methodName, "No of Rows Inserted in table : user_threshold_counter is : " + insertUserThresCount);
						}
					} catch (SQLException sqle) {
						LOG.error(methodName, "SQLException " + sqle.getMessage());
						LOG.errorTrace(methodName, sqle);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
								"ChannelUserDAO[creditUserBalancesForMultipleWallet]", channelTransferVO.getTransferID(), "", channelTransferVO.getNetworkCode(),
								"Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
					}
				}
			}// for
			if (!errorList.isEmpty()) {
				if (isFromWeb) {
					throw new BTSLBaseException(this, methodName, errorList, forwardPath);
				}
				throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE, errorList);
			}

			channelTransferVO.setEntryType(PretupsI.CREDIT);
		} catch (BTSLBaseException bbe) {
			throw bbe;
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[creditUserBalancesForMultipleWallet]",
					"", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[creditUserBalancesForMultipleWallet]",
					"", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userBalanceUpdateInsertCount:");
				loggerValue.append(userBalanceUpdateInsertCount);
				loggerValue.append(" ,insertUserThresCount:");
				loggerValue.append(insertUserThresCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return userBalanceUpdateInsertCount;
	}

	/**
	 * Method debitUserBalancesForMultipleWallet() This method check user
	 * minimum balance limits
	 * and Debit the user Balances if limit does not cross by the new balance
	 * and it makes debit in both wallets if partial allowed is configured.
	 * (existing balance-new requested credit balance)
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param isFromWeb
	 *            boolean
	 * @param forwardPath
	 *            String
	 * @return int
	 * @throws BTSLBaseException
	 */

	public int debitUserBalancesForMultipleWallet(Connection con, ChannelTransferVO channelTransferVO, boolean isFromWeb, String forwardPath) throws BTSLBaseException {
		final String methodName = "debitUserBalancesForMultipleWallet";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelTransferVO =");
        	loggerValue.append(channelTransferVO);
        	loggerValue.append(" isFromWeb=");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(" forwardPath=");
        	loggerValue.append(forwardPath);
            LOG.debug(methodName, loggerValue);
        }
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        String walletForAdnlCmsn = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN);
		int updateCount = 0;
		PreparedStatement pstmt = null;
		PreparedStatement psmtUpdate = null;
		PreparedStatement psmtInsertUserThreshold = null;
		ResultSet rs = null;

		final StringBuilder strBuffUpdate = new StringBuilder();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ?");
		strBuffUpdate.append(" WHERE ");
		strBuffUpdate.append(" user_id = ? ");
		strBuffUpdate.append(" AND ");
		strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? and balance_type=? ");

		final StringBuilder strBuffThresholdInsert = new StringBuilder();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		final String updateQuery = strBuffUpdate.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String sqlSelect = channelUserQry.creditUserBalancesForMultipleWalletQry();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Select Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query insertUserThreshold=");
			loggerValue.append(insertUserThreshold);
			LOG.debug(methodName, loggerValue);
		}
		try {
			psmtUpdate = con.prepareStatement(updateQuery);
			psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);

			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			TransferProfileProductVO transferProfileProductVO = null;
			long minBalance = 0;
			int maxAllowPct = 0;
			long maxAllowBalance = 0;
			// added by vikram
			final Date currentDate = new Date();
			long thresholdValue = -1;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			String userID = null;
			String profileID = null;
			String userCode = null;

			/*
			 * In case of transfer : debit the from user which is logined user
			 * In case of retutn : debit the from user which is logined user In
			 * case of Withdraw : debit the To user which is Searched user
			 */
			userID = channelTransferVO.getFromUserID();
			profileID = channelTransferVO.getSenderTxnProfile();
			userCode = channelTransferVO.getFromUserCode();
			boolean isNotToExecuteQuery = false;
			final ArrayList errorBalanceNotExistList = new ArrayList();
			final ArrayList errorBalanceLessList = new ArrayList();
			final ArrayList errorBalanceMinList = new ArrayList();
			final ArrayList errorList = new ArrayList();
			KeyArgumentVO keyArgumentVO = null;
			int itemsListSize = itemsList.size();
			for (int i = 0, k = itemsListSize; i < k; i++) {
				boolean toAddMoreError = true;
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				pstmt = con.prepareStatement(sqlSelect);
				pstmt.setString(1, userID);
				pstmt.setString(2, channelTransferItemsVO.getProductCode());
				pstmt.setString(3, channelTransferVO.getNetworkCode());
				pstmt.setString(4, channelTransferVO.getNetworkCodeFor());
				rs = pstmt.executeQuery();
				long balance = -1;
				long bonusBalance = -1;
				long netBalance = 0;
				long checkBalance = 0;
				boolean dataFound = false;
				boolean bonusDeducted = false;
				boolean mainDeducted = false;
				while (rs.next()) {
					if (rs.getString("balance_type").equals(defaultWallet)) {
						balance = rs.getLong("balance");
					} else {
						bonusBalance = rs.getLong("balance");
					}
					dataFound = true;
				}
				if (balance > -1) {
					mainDeducted = true;
				}

				if (!dataFound) {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}

					keyArgumentVO = new KeyArgumentVO();
					if (isFromWeb) {
						keyArgumentVO.setKey("error.transfer.userbalance.notexist");
						final String arg[] = { channelTransferItemsVO.getShortName() };
						keyArgumentVO.setArguments(arg);
						errorList.add(keyArgumentVO);
					} else {
						keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY);
						final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()) };
						keyArgumentVO.setArguments(arg);
						errorBalanceNotExistList.add(keyArgumentVO);
					}

				}
				//Handling of total balance transfer 
				if(bonusBalance > 0){
					netBalance = balance + bonusBalance;
				} else {
					netBalance = balance;
				}
				channelTransferItemsVO.setBalance(netBalance);
				if (netBalance < channelTransferItemsVO.getRequiredQuantity() && toAddMoreError) {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}
					keyArgumentVO = new KeyArgumentVO();
					if (isFromWeb) {
						keyArgumentVO.setKey("error.transfer.userbalance.less");
						final String arg[] = { channelTransferItemsVO.getShortName() };
						keyArgumentVO.setArguments(arg);
						errorList.add(keyArgumentVO);
					} else {
						keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE_SUBKEY);
						final String arg[] = { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };
						keyArgumentVO.setArguments(arg);
						errorBalanceLessList.add(keyArgumentVO);
					}

				}
				channelTransferItemsVO.setPreviousBalance(balance);// set the

				channelTransferItemsVO.setAfterTransSenderPreviousStock(balance);
				channelTransferItemsVO.setPreviousBonusBalance(bonusBalance);
				channelTransferItemsVO.setAfterTransSenderPreviousBonusStock(bonusBalance);
				// in the case of return we have not to check the min balance
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType()) && toAddMoreError) {
					/*
					 * check for the min balance for the product
					 */
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();

					maxAllowBalance = (netBalance * maxAllowPct) / 100;

					if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
						toAddMoreError = false;
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						keyArgumentVO = new KeyArgumentVO();
						if (isFromWeb) {
							keyArgumentVO.setKey("error.transfer.allowedmaxpct.isless");
							final String arg[] = { channelTransferItemsVO.getShortName(), String.valueOf(maxAllowPct) };
							keyArgumentVO.setArguments(arg);
							errorList.add(keyArgumentVO);
						} else {
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT);
							final String arg[] = { String.valueOf(maxAllowPct), String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO
									.getRequestedQuantity() };
							keyArgumentVO.setArguments(arg);
							errorBalanceMinList.add(keyArgumentVO);
						}
					}
					if (toAddMoreError) {

						checkBalance = netBalance;
						if (balance > -1 && balance >= channelTransferItemsVO.getRequiredQuantity()) {
							balance -= channelTransferItemsVO.getRequiredQuantity();

						} else if (bonusBalance > -1) {
							bonusBalance -= (channelTransferItemsVO.getRequiredQuantity() - balance);
							balance = 0;
							bonusDeducted = true;
						}
						minBalance = transferProfileProductVO.getMinResidualBalanceAsLong();
						if (minBalance > checkBalance) {
							toAddMoreError = false;
							if (!isNotToExecuteQuery) {
								isNotToExecuteQuery = true;
							}

							keyArgumentVO = new KeyArgumentVO();
							if (isFromWeb) {
								keyArgumentVO.setKey("error.transfer.minbalance.reached");
								final String arg[] = { channelTransferItemsVO.getShortName() };
								keyArgumentVO.setArguments(arg);
								errorList.add(keyArgumentVO);
							} else {
								keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS);
								final String arg[] = { PretupsBL.getDisplayAmount(minBalance), String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO
										.getRequestedQuantity() };
								keyArgumentVO.setArguments(arg);
								errorBalanceMinList.add(keyArgumentVO);
							}

						}
					}// if to add more errors
				}// if for transfer check
				if (!PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {


					if(!(channelTransferVO.isReversalFlag()))
					{
						if (balance > -1 && balance >= channelTransferItemsVO.getRequiredQuantity() ) {
							balance -= channelTransferItemsVO.getRequiredQuantity();
							channelTransferItemsVO.setMainDebitQty(channelTransferItemsVO.getRequiredQuantity());

						} 
						else if (bonusBalance > -1) {
							if (!mainDeducted) {
								channelTransferItemsVO.setMainDebitQty(0);
								channelTransferItemsVO.setBonusDebtQty(channelTransferItemsVO.getRequiredQuantity());
							} else {
								channelTransferItemsVO.setMainDebitQty(balance);
								channelTransferItemsVO.setBonusDebtQty((channelTransferItemsVO.getRequiredQuantity() - balance));
							}
							bonusBalance -= (channelTransferItemsVO.getRequiredQuantity() - balance);
							balance = 0;
							bonusDeducted = true;

						}
					}
					else
					{
						//this is for o2c reverse for multi wallet

						if (bonusBalance < channelTransferItemsVO.getCommQuantity()) {
							updateCount= -2;
							return updateCount;
						}

						if (balance > -1  && bonusBalance > -1 ) {
							if(PretupsI.FOC_WALLET_TYPE.equalsIgnoreCase(channelTransferItemsVO.getWalletType()))
							{
								bonusBalance -= channelTransferItemsVO.getRequiredQuantity();
							}
							else
							{	
								bonusBalance -= channelTransferItemsVO.getCommQuantity();
								if(!PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()))
									balance -= (channelTransferItemsVO.getApprovedQuantity() - channelTransferItemsVO.getCommQuantity());
								else
									balance -= channelTransferItemsVO.getApprovedQuantity();
							}
							channelTransferItemsVO.setMainDebitQty(balance);
							channelTransferItemsVO.setBonusDebtQty(bonusBalance);
							bonusDeducted = true;

						} 

					}

				}

				if (!isNotToExecuteQuery) {
					int m = 0;
					m++;
					psmtUpdate.setLong(m, balance);
					m++;
					psmtUpdate.setString(m, channelTransferVO.getTransferType());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getTransferID());
					m++;
					psmtUpdate.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
					m++;
					psmtUpdate.setString(m, userID);
					m++;
					psmtUpdate.setString(m, channelTransferItemsVO.getProductCode());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getNetworkCode());
					m++;
					psmtUpdate.setString(m, channelTransferVO.getNetworkCodeFor());
					m++;
					psmtUpdate.setString(m, defaultWallet);
					if (mainDeducted) {
						updateCount = psmtUpdate.executeUpdate();
					} else {
						updateCount = 1;
					}
					if (updateCount <= 0) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[debitUserBalances]", "", "",
								"", "BTSLBaseException: update count <=0");
						throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
					}

					psmtUpdate.clearParameters();

					if (updateCount > 0 && bonusDeducted) {
						int n = 0;
						n++;
						psmtUpdate.setLong(n, bonusBalance);
						n++;
						psmtUpdate.setString(n, channelTransferVO.getTransferType());
						n++;
						psmtUpdate.setString(n, channelTransferVO.getTransferID());
						n++;
						psmtUpdate.setTimestamp(n, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
						n++;
						psmtUpdate.setString(n, userID);
						n++;
						psmtUpdate.setString(n, channelTransferItemsVO.getProductCode());
						n++;
						psmtUpdate.setString(n, channelTransferVO.getNetworkCode());
						n++;
						psmtUpdate.setString(n, channelTransferVO.getNetworkCodeFor());
						n++;
						psmtUpdate.setString(n, walletForAdnlCmsn);

						updateCount = psmtUpdate.executeUpdate();
						if (updateCount <= 0) {
							EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
									"ChannelUserDAO[debitUserBalancesForMultipleWallet]", "", "", "", "BTSLBaseException: update count <=0");
							throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
						}

						psmtUpdate.clearParameters();

					}

					// added by nilesh
					// transferProfileProductVO
					// =(TransferProfileProductVO)profileDAO.loadTransferProfileProducts(con,profileID,channelTransferVO.getProductCode())
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
					String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					final String remark = null;

					if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
						thresholdValue = transferProfileProductVO.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					}
					try {

						if ((channelTransferItemsVO.getPreviousBalance() >= thresholdValue && balance <= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue)) {

							if (balance < thresholdValue)
							{
								UserBalancesVO vo = new UserBalancesVO();
								vo.setUserID(userID);
								vo.setProductCode(channelTransferItemsVO.getProductCode());
								vo.setNetworkCode(channelTransferVO.getNetworkCode());
								vo.setLastTransferID(channelTransferVO.getTransferID());
								if(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equalsIgnoreCase(channelTransferVO.getTransferType()))
									new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,channelTransferVO.getCategoryCode());
							}//end here

							m = 0;
							m++;
							psmtInsertUserThreshold.setString(m, userID);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferID());
							m++;
							psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getNetworkCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferItemsVO.getProductCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getType());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferType());
							m++;
							psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getCategoryCode());
							m++;
							psmtInsertUserThreshold.setLong(m, channelTransferItemsVO.getPreviousBalance());
							m++;
							psmtInsertUserThreshold.setLong(m, balance);
							m++;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							m++;
							psmtInsertUserThreshold.setString(m, threshold_type);
							m++;
							psmtInsertUserThreshold.setString(m, remark);
							updateCount = psmtInsertUserThreshold.executeUpdate();
							psmtInsertUserThreshold.clearParameters();
						}
						LOG.debug(methodName, "SystemPreferences.USERWISE_LOAN_ENABLE"+SystemPreferences.USERWISE_LOAN_ENABLE+" channelTransferVO.getUserLoanVOList()"+ channelTransferVO.getUserLoanVOList());
						if(SystemPreferences.USERWISE_LOAN_ENABLE && channelTransferVO.getUserLoanVOList()!=null ) {
							try {
								ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
								for(int index=0;index<channelTransferVO.getUserLoanVOList().size();index++){
									userLoanVOList.add(channelTransferVO.getUserLoanVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(userLoanVOList, userID, balance,channelTransferItemsVO.getPreviousBalance(),channelTransferItemsVO.getProductCode(),channelTransferItemsVO.getProductType());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
							
							
						}
						else if (SystemPreferences.CHANNEL_SOS_ENABLE&&channelTransferVO.getChannelSoSVOList()!=null)
						{
							try {
								ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
								for(int index=0;index<channelTransferVO.getChannelSoSVOList().size();index++){
									channeluserList.add(channelTransferVO.getChannelSoSVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, userID, balance,channelTransferItemsVO.getPreviousBalance());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
						}//end here
					} catch (SQLException sqle) {
						loggerValue.setLength(0);
						loggerValue.append(SQL_EXCEPTION);
						loggerValue.append(sqle.getMessage());
						LOG.error(methodName, loggerValue);
						LOG.errorTrace(methodName, sqle);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
								"ChannelUserDAO[debitUserBalancesForMultipleWallet]", channelTransferVO.getTransferID(), "", channelTransferVO.getNetworkCode(),
								"Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
					}
				}// if to execute query
			}// for loop
			if (isFromWeb) {
				if (!errorList.isEmpty()) {
					throw new BTSLBaseException(this, methodName, errorList, forwardPath);
				}
			} else {
				if (!errorBalanceNotExistList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE, userCode);
				} else if (!errorBalanceLessList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE, errorBalanceLessList);
				} else if (!errorBalanceMinList.isEmpty()) {
					throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG,
							errorBalanceMinList);
				}
			}

			// for balance logger
			channelTransferVO.setEntryType(PretupsI.DEBIT);
		} catch (BTSLBaseException bbe) {
			throw bbe;
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForMultipleWallet]",
					"", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForMultipleWallet]",
					"", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtUpdate != null) {
                	psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtInsertUserThreshold != null) {
                	psmtInsertUserThreshold.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting updateCount:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updateCount;
	}

	/**
	 * This method selects the security question answer for User PIN Reset on
	 * the basis of MSISDN
	 * 
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            MSISDN
	 * @return Security Question Ans
	 * @throws BTSLBaseException
	 */
	public String securityQuesAns(Connection con, String msisdn) throws BTSLBaseException {
		final String methodName = "securityQuesAns";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: MSISDN =");
        	loggerValue.append(msisdn);
            LOG.debug(methodName, loggerValue);
        }

		 
		 
		final ChannelUserVO channelUserVO = new ChannelUserVO();
		String securityAns = null;

		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("SELECT ");
			strBuff.append(Constants.getProperty("SECURITY_QUESTION_FIELD").toString());

			strBuff.append(" FROM USERS WHERE MSISDN=?");

			final String selectQuery = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}

			try(PreparedStatement psmt = con.prepareStatement(selectQuery);)
			{

			psmt.setString(1, msisdn);
			try(ResultSet rs = psmt.executeQuery();)
			{
			if (rs.next()) {
				securityAns = rs.getString(Constants.getProperty("SECURITY_QUESTION_FIELD").toString());
			}
			}
		}// end of try
		}
		catch (SQLException e) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[securityQuesAns]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[securityQuesAns]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Security Ques Answer:");
				loggerValue.append(securityAns);
				LOG.debug(methodName, loggerValue);
			}
		}
		return securityAns;
	}

	/**
	 * This method updates the user information on the basis of msisdn for self
	 * pin reset security question
	 * 
	 * @param con
	 *            Connection
	 * @param channelUserVO
	 * @return update count
	 * @throws BTSLBaseException
	 */
	public int updateChannelUserInfoForPinReset(Connection con, ChannelUserVO channelUserVO) throws BTSLBaseException {
		
		int updateCount = 0;
		final String methodName = "updateChannelUserInfoForPinReset";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: MSISDN =");
        	loggerValue.append(channelUserVO.getMsisdn());
        	loggerValue.append(" CONTACT_PERSON=");
        	loggerValue.append(channelUserVO.getContactPerson());
        	loggerValue.append(" SHORT_NAME=");
        	loggerValue.append(channelUserVO.getShortName());
        	loggerValue.append(" APPOINTMENT_DATE=");
        	loggerValue.append(channelUserVO.getAppointmentDate());
        	loggerValue.append(" EMPLOYEE_CODE=");
        	loggerValue.append(channelUserVO.getEmpCode());
        	loggerValue.append(" SSN=");
        	loggerValue.append(channelUserVO.getSsn());
            LOG.debug(methodName, loggerValue);
        }
		try {
			StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE USERS SET ");
			if (!BTSLUtil.isNullString(channelUserVO.getContactPerson())) {
				strBuff.append("CONTACT_PERSON = ?,");
			}
			if (channelUserVO.getAppointmentDate() != null) {
				strBuff.append("APPOINTMENT_DATE = ?,");
			}
			if (!BTSLUtil.isNullString(channelUserVO.getShortName())) {
				strBuff.append("SHORT_NAME = ?,");
			}
			if (!BTSLUtil.isNullString(channelUserVO.getEmpCode())) {
				strBuff.append("EMPLOYEE_CODE = ?, ");
			}
			if (!BTSLUtil.isNullString(channelUserVO.getSsn())) {
				strBuff.append("SSN = ?, ");
			}

			final String temp = strBuff.toString();
			final String temp2 = temp.substring(0, temp.lastIndexOf(','));

			strBuff = new StringBuilder();

			strBuff.append(", MODIFIED_BY= ?, MODIFIED_ON = ?");
			strBuff.append(" WHERE MSISDN = ?");

			final String updateQuery = temp2 + strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}
			try(PreparedStatement psmtUpdate = con.prepareStatement(updateQuery);)
			{
			int rno = 1;
			if (!BTSLUtil.isNullString(channelUserVO.getContactPerson())) {
				psmtUpdate.setString(rno, channelUserVO.getContactPerson());
				rno++;
			}
			if (channelUserVO.getAppointmentDate() != null) {
				psmtUpdate.setDate(rno, BTSLUtil.getSQLDateFromUtilDate(channelUserVO.getAppointmentDate()));
				rno++;
			}
			if (!BTSLUtil.isNullString(channelUserVO.getShortName())) {
				psmtUpdate.setString(rno, channelUserVO.getShortName());
				rno++;
			}
			if (!BTSLUtil.isNullString(channelUserVO.getEmpCode())) {
				psmtUpdate.setString(rno, channelUserVO.getEmpCode());
				rno++;
			}
			if (!BTSLUtil.isNullString(channelUserVO.getSsn())) {
				psmtUpdate.setString(rno, channelUserVO.getSsn());
				rno++;
			}

			psmtUpdate.setString(rno, channelUserVO.getModifiedBy());
			rno++;
			psmtUpdate.setTimestamp(rno, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
			rno++;
			psmtUpdate.setString(rno, channelUserVO.getMsisdn());
			rno++;
			updateCount = psmtUpdate.executeUpdate();

		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserInfoForPinReset]", "",
					"", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserInfoForPinReset]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Update Count:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return updateCount;
	}

	/**
	 * this method used to Check whether request for Opt-In/Opt-Out to LMS
	 * profile lies
	 * between profile's created_on and applicable_from
	 * 
	 * @param con
	 * @param profileId
	 *            String
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean isValidTimeForOptInOut(Connection con, String profileId) throws BTSLBaseException {
		boolean isValidRequestTime = false;
		final String methodName = "isValidTimeForOptInOut";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profileId =");
        	loggerValue.append(profileId);
            LOG.debug(methodName, loggerValue);
        }
		
		
		final String selectQuery = channelUserQry.isValidTimeForOptInOutQry();
		int k = 0;
		try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);){
			
			
			k++;
			pstmtSelect.setString(k, PretupsI.YES);
			k++;
			pstmtSelect.setString(k, profileId);
			try(ResultSet resultSet = pstmtSelect.executeQuery();)
			{
			if (resultSet.next()) {
				isValidRequestTime = true;
			}
		}
		}
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}

		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}

		finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isValidRequestTime:");
				loggerValue.append(isValidRequestTime);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isValidRequestTime;
	}

	/**
	 * this method used to Check whether Opt-In/Opt-Out feature is applicable
	 * for
	 * channel user's associated LMS profile
	 * 
	 * @param con
	 * @param userId
	 *            String
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean isLMSProfileAppForOptInOut(Connection con, String userId) throws BTSLBaseException {
		{
			boolean isValidLMSProfile = false;
			final String methodName = "isLMSProfileAppForOptInOut";
			StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: userId =");
	        	loggerValue.append(userId);
	            LOG.debug(methodName, loggerValue);
	        }

			final StringBuilder selectQueryBuff = new StringBuilder("SELECT 1 ");
			selectQueryBuff.append("  FROM users u, channel_users cu, profile_set_version psv, profile_set ps ");
			selectQueryBuff.append("  WHERE u.user_id = cu.user_id ");
			selectQueryBuff.append("  AND u.status IN ('Y', 'S') ");
			selectQueryBuff.append("  AND cu.lms_profile is not null ");
			selectQueryBuff.append("  AND ps.set_id = psv.set_id ");
			selectQueryBuff.append("  AND cu.lms_profile=ps.set_id ");
			selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
			selectQueryBuff.append("  AND ps.opt_in_out_enabled = ? ");
			selectQueryBuff.append("  AND cu.user_id = ? ");
			//Control group user is not allowded for optin/optout into/from the profile.
			selectQueryBuff.append("  AND cu.CONTROL_GROUP <> ? ");
			final String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}

			try {
				int k = 0;
				try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
				{
				pstmtSelect.setString(++k, PretupsI.YES);
				pstmtSelect.setString(++k, userId);
				pstmtSelect.setString(++k, PretupsI.YES);
				try(ResultSet resultSet = pstmtSelect.executeQuery();)
				{
				if (resultSet.next()) {
					isValidLMSProfile = true;
				}
			}
				}
			}
			catch (SQLException sqle) {
				loggerValue.setLength(0);
				loggerValue.append(SQL_EXCEPTION);
				loggerValue.append(sqle.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, sqle);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"SQL Exception:" + sqle.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}

			catch (Exception e) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(e.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"Exception:" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}

			finally {
				if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Exiting isValidLMSProfile:");
					loggerValue.append(isValidLMSProfile);
					LOG.debug(methodName, loggerValue);
				}
			}
			return isValidLMSProfile;
		}
	}

	/**
	 * this method used to Check whether channel user has already opted-in for
	 * LMS profile
	 * 
	 * @param con
	 * @param userId
	 *            String
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean isLMSProfileAlreadyOptIn(Connection con, String userId) throws BTSLBaseException {
		{
			boolean isAlreadyOptIn = false;
			final String methodName = "isLMSProfileAlreadyOptIn";
			StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: userId =");
	        	loggerValue.append(userId);
	            LOG.debug(methodName, loggerValue);
	        }
			 
			ResultSet resultSet = null;

			final StringBuilder selectQueryBuff = new StringBuilder("SELECT u.user_id,cu.lms_profile, cu.opt_in_out_status ");
			selectQueryBuff.append("  FROM users u, channel_users cu, profile_set_version psv, profile_set ps ");
			selectQueryBuff.append("  WHERE u.user_id = cu.user_id ");
			selectQueryBuff.append("  AND u.status IN ('Y', 'S') ");
			selectQueryBuff.append("  AND cu.lms_profile is not null ");
			selectQueryBuff.append("  AND ps.set_id = psv.set_id ");
			selectQueryBuff.append("  AND cu.lms_profile=ps.set_id ");
			selectQueryBuff.append("  AND psv.status IN ('Y', 'S') ");
			selectQueryBuff.append("  AND ps.opt_in_out_enabled = ? ");
			selectQueryBuff.append("  AND cu.user_id = ? ");
			final String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}			int k = 0;
			try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);){
				
				
				pstmtSelect.clearParameters();
				pstmtSelect.setString(++k, PretupsI.YES);
				pstmtSelect.setString(++k, userId);
				resultSet = pstmtSelect.executeQuery();
				if (resultSet.next()) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(methodName, "Entered : User ID = " + userId + " , user_id = " + resultSet.getString("user_id") + " , lms_profile = " + resultSet
								.getString("lms_profile") + " , opt_in_out_flag = " + resultSet.getString("opt_in_out_status"));
					}
					if (PretupsI.OPT_IN.equals(resultSet.getString("opt_in_out_status"))) {
						isAlreadyOptIn = true;
					}
				} else {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_PROFILE_OPT_IN_OUT_NA);
				}
			}

			catch (SQLException sqle) {
				loggerValue.setLength(0);
				loggerValue.append(SQL_EXCEPTION);
				loggerValue.append(sqle.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, sqle);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"SQL Exception:" + sqle.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}

			catch (Exception e) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(e.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"Exception:" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}

			finally {
				try{
	            	if (resultSet!= null){
	            		resultSet.close();
	            	}
	            }
	            catch (SQLException e){
	            	LOG.error("An error occurred closing statement.", e);
	            }
				if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Exiting isAlreadyOptIn:");
					loggerValue.append(isAlreadyOptIn);
					LOG.debug(methodName, loggerValue);
				}
			}
			return isAlreadyOptIn;
		}
	}

	/**
	 * this method used to Check update channel user's response to Opt-In for
	 * LMS profile
	 * 
	 * @param con
	 * @param userId
	 *            String
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int updateLMSProfileForOptIn(Connection con, String userId) throws BTSLBaseException {
		{
			final String methodName = "updateLMSProfileForOptIn";
			StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: userId =");
	        	loggerValue.append(userId);
	            LOG.debug(methodName, loggerValue);
	        }
			int updateCount = 0;
		

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE channel_users SET opt_in_out_status = ?, opt_in_out_response_date = ? ");
			strBuff.append(" WHERE user_id = ?");
			final String updateQuery = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}

			try(PreparedStatement psmtUpdate = con.prepareStatement(updateQuery);) {
				
				psmtUpdate.setString(1, PretupsI.OPT_IN);
				psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new Date()));
				psmtUpdate.setString(3, userId);
				updateCount = psmtUpdate.executeUpdate();
			}

			catch (SQLException sqle) {
				loggerValue.setLength(0);
				loggerValue.append(SQL_EXCEPTION);
				loggerValue.append(sqle.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, sqle);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"SQL Exception:" + sqle.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}

			catch (Exception e) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(e.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"Exception:" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}

			finally {
				if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Exiting updateCount:");
					loggerValue.append(updateCount);
					LOG.debug(methodName, loggerValue);
				}
			}
			return updateCount;
		}
	}

	/**
	 * this method used to Check update channel user's response to Opt-Out for
	 * LMS profile
	 * 
	 * @param con
	 * @param userId
	 *            String
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int updateLMSProfileForOptOut(Connection con, String userId) throws BTSLBaseException {
		{
			final String methodName = "updateLMSProfileForOptOut";
			StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: userId =");
	        	loggerValue.append(userId);
	            LOG.debug(methodName, loggerValue);
	        }
			int updateCount = 0;
			 

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE channel_users SET opt_in_out_status = ?, opt_in_out_response_date = ? ");
			strBuff.append(" WHERE user_id = ?");
			final String updateQuery = strBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}

			try (PreparedStatement psmtUpdate = con.prepareStatement(updateQuery);) {
				
				psmtUpdate.setString(1, PretupsI.OPT_OUT);
				psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new Date()));
				psmtUpdate.setString(3, userId);
				updateCount = psmtUpdate.executeUpdate();
			}

			catch (SQLException sqle) {
				loggerValue.setLength(0);
				loggerValue.append(SQL_EXCEPTION);
				loggerValue.append(sqle.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, sqle);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"SQL Exception:" + sqle.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}

			catch (Exception e) {
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(e.getMessage());
				LOG.error(methodName, loggerValue);
				LOG.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
						"Exception:" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}

			finally {
				if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Exiting updateCount:");
					loggerValue.append(updateCount);
					LOG.debug(methodName, loggerValue);
				}
			}
			return updateCount;
		}
	}

	// Handling of controlled profile
	public boolean isProfileActive(String msisdn, String setId) throws BTSLBaseException {
		final String methodName = "isProfileActive";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
        	loggerValue.append(" setId");
        	loggerValue.append(setId);
            LOG.debug(methodName, loggerValue);
        }
		boolean returnVal = false;
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		ResultSet resultSet = null;

		final String selectQuery = channelUserQry.isProfileActiveQry();

		try {
			int k = 0;
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.clearParameters();
			pstmtSelect.setString(++k, setId);
			pstmtSelect.setString(++k, PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
			pstmtSelect.setString(++k, PretupsI.LMS_PROMOTION_TYPE_STOCK);
			resultSet = pstmtSelect.executeQuery();
			while (resultSet.next()) {
				if (resultSet.getInt("total") > 0) {
					returnVal = true;
				}
			}
		}
		}
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}

		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}

		finally {
			
			if(mcomCon != null)
			{
				mcomCon.close("ChannelUserDAO#isProfileActive");
				mcomCon=null;
			}
			try{
            	if (resultSet!= null){
            		resultSet.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting returnVal:");
				loggerValue.append(returnVal);
				LOG.debug(methodName, loggerValue);
			}
		}
		return returnVal;

	}

	public Map<String, Double> countOfUsersInTargetControlGroup(Connection con, String lmsPorfile) throws BTSLBaseException {
		final String methodName = "countOfUsersInTargetControlGroup";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: lmsPorfile =");
        	loggerValue.append(lmsPorfile);
            LOG.debug(methodName, loggerValue);
        }
		final Map<String, Double> countOfUsersInTargetControlGroup = new HashMap<String, Double>(100);
		 
		

		final StringBuilder selectQueryBuff = new StringBuilder(" select control_count, target_count ");
		selectQueryBuff.append(" from ");
		selectQueryBuff.append("( ");
		selectQueryBuff.append("select count(user_id) as control_count from channel_users ");
		selectQueryBuff.append("where lms_profile= ? ");
		selectQueryBuff.append("and control_group= ?  ");
		selectQueryBuff.append(") X ");
		selectQueryBuff.append(", ");
		selectQueryBuff.append("( ");
		selectQueryBuff.append("select count(user_id) as target_count from channel_users ");
		selectQueryBuff.append("where lms_profile= ? ");
		selectQueryBuff.append("and control_group= ? ");
		selectQueryBuff.append(") Y ");

		final String selectQuery = selectQueryBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
		int k = 0;
		try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
			
			
			pstmtSelect.clearParameters();
			pstmtSelect.setString(++k, lmsPorfile);
			pstmtSelect.setString(++k, PretupsI.YES);
			pstmtSelect.setString(++k, lmsPorfile);
			pstmtSelect.setString(++k, PretupsI.NO);
			try(ResultSet  resultSet = pstmtSelect.executeQuery();)
			{
			while (resultSet.next()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "Entered : lmsPorfile = " + lmsPorfile);
				}
				countOfUsersInTargetControlGroup.put("control_count", resultSet.getDouble("control_count"));
				countOfUsersInTargetControlGroup.put("target_count", resultSet.getDouble("target_count"));
			}
		}
		}

		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}

		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}

		finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting countOfUsersInTargetControlGroup size:");
				loggerValue.append(countOfUsersInTargetControlGroup.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return countOfUsersInTargetControlGroup;

	}
	// added for channel user transfer
	public ArrayList loadUserHierarchyListForTransfer(Connection con, String userId[], String mode,
			String statusUsed, String status, String p_userCategory) throws BTSLBaseException {
		final String methodName = "loadUserHierarchyListForTransfer";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId[0] =");
        	loggerValue.append(userId[0]);
        	loggerValue.append(" userId.length=");
        	loggerValue.append(userId.length);
        	loggerValue.append(" mode=");
        	loggerValue.append(mode);
        	loggerValue.append(" statusUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
        	loggerValue.append(" p_userCategory=");
        	loggerValue.append(p_userCategory);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmt = null;
		
		int maxLevel = 0;
		int tempLevel = 0;
		ChannelUserVO channelUserVO = null;
		ArrayList userDetailList = new ArrayList();
		try {
			pstmt = channelUserQry.loadUserHierarchyListForTransferQry(con, statusUsed, status, mode, userId, p_userCategory,null);
			try(ResultSet rs = pstmt.executeQuery();)
			{

			while (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserlevel(rs.getString("l"));
				tempLevel = Integer.parseInt(channelUserVO.getUserlevel());
				if (tempLevel > maxLevel) {
					maxLevel = tempLevel;
				}
				channelUserVO.setUserIDPrefix(rs.getString("user_id_prefix"));
				channelUserVO.setUserID(rs.getString("user_id"));
				if ("STAFF".equals(rs.getString("user_type"))) {
					channelUserVO.setUserName(rs.getString("user_name") + "[Staff]");
				} else {
					channelUserVO.setUserName(rs.getString("user_name"));
				}
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setLastLoginOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_login_on")));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("status"));
				channelUserVO.setEmail(rs.getString("email"));
				//channelUserVO.setOriginId(rs.getString("origin_id")); // added origin id by Naveen for channel transfer.
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				// end added by deepika aggarwal
				channelUserVO.setPasswordModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("pswd_modified_on")));
				channelUserVO.setContactPerson(rs.getString("contact_person"));
				channelUserVO.setContactNo(rs.getString("contact_no"));
				channelUserVO.setDesignation(rs.getString("designation"));
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setLastModified(rs.getTimestamp("modified_on").getTime());
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
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("level1_approved_on")));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("level2_approved_on")));
				channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("password_count_updated_on")));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				channelUserVO
				.setAppointmentDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("appointment_date")));
				channelUserVO.setStatusDesc(rs.getString("lookup_name"));
				channelUserVO.setUserBalanceList(this.loadUserBalances(con, channelUserVO.getNetworkID(),
						channelUserVO.getNetworkID(), channelUserVO.getUserID()));
				// for tango implementation
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setMaxUserLevel(maxLevel);
				CategoryVO categoryVO = new CategoryVO();
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				channelUserVO.setCategoryVO(categoryVO);
				if (Integer.parseInt(channelUserVO.getUserlevel()) == 1) {
					if (channelUserVO.getUserType().equals("CHANNEL")) {
						userDetailList.add(channelUserVO);
					}
				} else {
					userDetailList.add(channelUserVO);
				}
			}
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserHierarchyListForTransfer]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserHierarchyListForTransfer]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userDetailList size:");
				loggerValue.append(userDetailList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return userDetailList;
	}
	// added for channel user transfer
	/**
	 * 
	 * @param con
	 * @param userId
	 * @return
	 * @throws BTSLBaseException
	 */

	public ChannelUserVO loadUserMsisdnAndStatus(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadUserMsisdnAndStatus";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId =");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }

		ResultSet rs = null;
		ChannelUserVO channelUserVO = null;

		try {

			StringBuilder selectQueryBuff = new StringBuilder();
			selectQueryBuff
			.append(" SELECT u.msisdn,u.status, up.phone_language, up.country FROM USERS u,USER_PHONES up ");
			selectQueryBuff.append(" WHERE u.user_id=? AND u.user_id = up.user_id and up.primary_number='Y' ");
			String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}

			try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
			{
			pstmtSelect.setString(1, userId);
			rs = pstmtSelect.executeQuery();

			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				UserPhoneVO phoneVO = new UserPhoneVO();
				channelUserVO.setUserPhoneVO(phoneVO);
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setStatus(rs.getString("status"));
				String language = rs.getString("phone_language");
				String country = rs.getString("country");

				phoneVO.setLocale(new Locale(language, country));

			}
		} 
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
					"ChannelUserDAO[loadUserMsisdnAndStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
					"ChannelUserDAO[loadUserMsisdnAndStatus]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (channelUserVO != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "Exited MSISDN=" + channelUserVO.getMsisdn() + " Status="
							+ channelUserVO.getStatus());
				} else if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "Exited User Not Found " + userId);
				}
			}
		}
		return channelUserVO;
	}

	/**
	 * Method to load Sender User List with no transfer rule 
	 * for C2C withdraw via channel admin
	 * loadUserHierarchyList
	 * 
	 * @param con
	 * @param userId
	 * @param mode
	 * @param statusUsed
	 * @param status
	 * @param p_userCategory
	 * @return
	 * @throws BTSLBaseException
	 * @author vikas.kumar
	 */

	public ArrayList loadUserHierarchyListForTransferByCatergory(Connection con, String userId[], String mode,
			String statusUsed, String status, String p_userCategory, String category,String userName) throws BTSLBaseException {
		final String methodName = "loadUserHierarchyListForTransferByCatergory";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId[0] =");
        	loggerValue.append(userId[0]);
        	loggerValue.append(" userId.length=");
        	loggerValue.append(userId.length);
        	loggerValue.append(" mode=");
        	loggerValue.append(mode);
        	loggerValue.append(" statusUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
        	loggerValue.append(" p_userCategory=");
        	loggerValue.append(p_userCategory);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int maxLevel = 0;
		int tempLevel = 0;
		ChannelUserVO channelUserVO = null;
		ArrayList userDetailList = new ArrayList();
		try {
			pstmt = channelUserQry.loadUserHierarchyListForTransferByCatergoryQry(con, statusUsed, status, mode, userId, p_userCategory, category, userName, null);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserlevel(rs.getString("l"));
				tempLevel = Integer.parseInt(channelUserVO.getUserlevel());
				if (tempLevel > maxLevel) {
					maxLevel = tempLevel;
				}
				channelUserVO.setUserIDPrefix(rs.getString("user_id_prefix"));
				channelUserVO.setUserID(rs.getString("user_id"));
				if ("STAFF".equals(rs.getString("user_type"))) {
					channelUserVO.setUserName(rs.getString("user_name") + "[Staff]");
				} else {
					channelUserVO.setUserName(rs.getString("user_name"));
				}
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setLastLoginOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_login_on")));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("status"));
				channelUserVO.setEmail(rs.getString("email"));
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				// end added by deepika aggarwal
				channelUserVO.setPasswordModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pswd_modified_on")));
				channelUserVO.setContactPerson(rs.getString("contact_person"));
				channelUserVO.setContactNo(rs.getString("contact_no"));
				channelUserVO.setDesignation(rs.getString("designation"));
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setLastModified(rs.getTimestamp("modified_on").getTime());
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
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("level1_approved_on")));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("level2_approved_on")));
				channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("password_count_updated_on")));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				channelUserVO.setAppointmentDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("appointment_date")));
				channelUserVO.setStatusDesc(rs.getString("lookup_name"));
				channelUserVO.setUserBalanceList(this.loadUserBalances(con, channelUserVO.getNetworkID(),channelUserVO.getNetworkID(), channelUserVO.getUserID()));
				// for tango implementation
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setMaxUserLevel(maxLevel);
				CategoryVO categoryVO = new CategoryVO();
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				channelUserVO.setCategoryVO(categoryVO);
				if (Integer.parseInt(channelUserVO.getUserlevel()) == 1) {
					if (channelUserVO.getUserType().equals("CHANNEL")) {
						userDetailList.add(channelUserVO);
					}
				} else {
					userDetailList.add(channelUserVO);
				}
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserHierarchyListForTransfer]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserHierarchyListForTransfer]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userDetailList size:");
				loggerValue.append(userDetailList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return userDetailList;
	}

	public String product(Connection con, String p_code) throws BTSLBaseException {
		final String methodName = "product";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Product Code =");
        	loggerValue.append(p_code);
            LOG.debug(methodName, loggerValue);
        }
		LogFactory.printLog(methodName, "Entered with: Product Code=" + p_code, LOG);

		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		String pro_name = null;
		try {

			StringBuilder selectQueryBuff = new StringBuilder();
			selectQueryBuff.append(" SELECT product_code from products ");
			selectQueryBuff.append(" where product_short_code= ? and status='Y' ");
			String selectQuery = selectQueryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQuery);
				LOG.debug(methodName, loggerValue);
			}
			pstmtSelect = con.prepareStatement(selectQuery);
			pstmtSelect.setInt(1, Integer.parseInt(p_code));
			rs = pstmtSelect.executeQuery();

			if (rs.next()) {
				pro_name = rs.getString("product_code");

			}
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
					"ChannelUserDAO[product]", "", "", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
					"ChannelUserDAO[product]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmtSelect != null) {
                	pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (pro_name != null) {
				if (LOG.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Exiting Product:");
					loggerValue.append(p_code);
					LOG.debug(methodName, loggerValue);
				}
			}
		}
		return pro_name;
	}

	public ChannelUserVO loadChannelUserForSOS(Connection con, String userId, String productType, String productCode,String requestType) throws BTSLBaseException {
		final String methodName = "loadChannelUserForSOS";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(userId);
        	loggerValue.append(" productType=");
        	loggerValue.append(productType);
        	loggerValue.append(" requestType=");
        	loggerValue.append(requestType);
            LOG.debug(methodName, loggerValue);
        }

		ChannelUserVO channelUserVO = null;

		final StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT CU.SOS_ALLOWED, CU.SOS_ALLOWED_AMOUNT, CU.SOS_THRESHOLD_LIMIT, UB.BALANCE, UB.PRODUCT_CODE, P.PRODUCT_TYPE, UTC.LAST_SOS_TXN_STATUS, UTC.LAST_SOS_TXN_ID ");
		strBuff.append(",UTC.LAST_SOS_PRODUCT_CODE,CT.TRANSFER_MRP,CT.STOCK_UPDATED ");
		strBuff.append("FROM CHANNEL_USERS CU, USER_BALANCES UB, PRODUCTS P, USER_TRANSFER_COUNTS UTC,CHANNEL_TRANSFERS CT ");
		strBuff.append(" WHERE cu.user_id = ? ");
		strBuff.append(" AND UB.USER_ID = CU.USER_ID ");
		strBuff.append("AND UB.PRODUCT_CODE = P.PRODUCT_CODE ");
		strBuff.append(" AND UTC.USER_ID = CU.USER_ID ");
		if(BTSLUtil.isNullString(productType)){
			strBuff.append(" AND P.PRODUCT_CODE = ? ");
		}else{
			strBuff.append(" AND P.PRODUCT_TYPE = ? ");
		}
		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
			strBuff.append("AND UTC.LAST_SOS_PRODUCT_CODE = UB.PRODUCT_CODE ");
			strBuff.append(" AND CT.TRANSFER_ID=UTC.LAST_SOS_TXN_ID ");
		}
		else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE)){
			strBuff.append(" AND CT.TRANSFER_ID=UTC.LAST_LR_TXNID ");
			strBuff.append(" AND UB.PRODUCT_CODE IN (SELECT P.PRODUCT_CODE from PRODUCTS P where P.PRODUCT_TYPE=CT.PRODUCT_TYPE)");
		}
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(strBuff.toString());
			LOG.debug(methodName, loggerValue);
		}

		try (PreparedStatement pstmt = con.prepareStatement(strBuff.toString());){
			
			pstmt.setString(1, userId);
			if(BTSLUtil.isNullString(productType)){
				pstmt.setString(2, productCode);
			}else{
				pstmt.setString(2, productType);
			}

			try(ResultSet rs = pstmt.executeQuery();)
			{

			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setSosAllowed(rs.getString("SOS_ALLOWED"));
				channelUserVO.setSosAllowedAmount(rs.getLong("SOS_ALLOWED_AMOUNT"));
				channelUserVO.setSosThresholdLimit(rs.getLong("SOS_THRESHOLD_LIMIT"));
				channelUserVO.setBalance(Long.parseLong(rs.getString("BALANCE")));
				channelUserVO.setProductCode(rs.getString("PRODUCT_CODE"));
				channelUserVO.setLastSosStatus(rs.getString("LAST_SOS_TXN_STATUS"));
				channelUserVO.setLastSosTransactionId(rs.getString("LAST_SOS_TXN_ID"));
				channelUserVO.setLastSosProductCode(rs.getString("LAST_SOS_PRODUCT_CODE"));
				channelUserVO.setLrTransferAmount(rs.getLong("TRANSFER_MRP"));

			}
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUser]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUser]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	public ChannelUserVO loadChannelUserForSOS(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadChannelUserForSOS";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		ChannelUserVO channelUserVO = new ChannelUserVO();

		final StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT c.domain_code,cu.user_grade,");
		strBuff.append("cu.contact_person,cu.transfer_profile_id, cu.comm_profile_set_id,");
		strBuff.append("cu.in_suspend, cu.out_suspend,cu.outlet_code,cu.suboutlet_code,");

		// for Zebra and Tango by sanjeew date 06/07/07
		strBuff.append("cu.application_id, cu.mpay_profile_id, cu.user_profile_id, ");
		strBuff.append(" cu.is_primary, cu.mcommerce_service_allow, cu.low_bal_alert_allow, ");
		strBuff.append(" u.category_code,c.low_bal_alert_allow catlowbalallow,c.outlets_allowed");
		// added by nilesh:for user profile updation based on langitude and
		// latitude
		strBuff.append(" ,u.longitude,u.latitude,u.document_type,u.document_no,u.payment_type");
		// Added by Amit Raheja for alert type
		strBuff.append(" ,cu.alert_type,cu.alert_email,u.authentication_allowed, u.msisdn ");
		// added by gaurav for transfer rule type
		if (isTrfRuleUserLevelAllow) {
			strBuff.append(", cu.trf_rule_type");
		}
		// Added by Aatif
		if (lmsAppl) {
			strBuff.append(", cu.lms_profile ");
		}
		if (optInOutAllow) {
			strBuff.append(" , cu.OPT_IN_OUT_STATUS ");
		}
		strBuff.append(" , cu.CONTROL_GROUP, cu.SOS_ALLOWED_AMOUNT, u.PARENT_ID, u.OWNER_ID, u.STATUS, u.user_id, ug.GRPH_DOMAIN_CODE ,u.login_id, u.external_code ");
		strBuff.append(" FROM channel_users cu,users u,categories c,  USER_GEOGRAPHIES ug  ");
		// end Zebra and Tango

		strBuff.append(" WHERE cu.user_id = ? and cu.user_id=u.user_id and u.category_code=c.category_code and ug.USER_ID =u.user_id");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			pstmt.setString(1, userId);
			try (ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setContactPerson(rs.getString("contact_person"));
				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setInSuspend(rs.getString("in_suspend"));
				channelUserVO.setOutSuspened(rs.getString("out_suspend"));
				channelUserVO.setOutletCode(rs.getString("outlet_code"));
				channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
				channelUserVO.setCatLowBalanceAlertAllow(rs.getString("catlowbalallow"));
				channelUserVO.setCatOutletAllowed(rs.getString("outlets_allowed"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setLongitude(rs.getString("longitude"));
				channelUserVO.setLatitude(rs.getString("latitude"));
				channelUserVO.setDocumentType(rs.getString("document_type"));
				channelUserVO.setDocumentNo(rs.getString("document_no"));
				channelUserVO.setPaymentType(rs.getString("payment_type"));
				channelUserVO.setAlertType(rs.getString("alert_type"));
				channelUserVO.setAlertEmail(rs.getString("alert_email"));
				channelUserVO.setOutletCode(rs.getString("outlet_code"));
				channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
				channelUserVO.setSosAllowedAmount(rs.getLong("SOS_ALLOWED_AMOUNT"));
				channelUserVO.setParentID(rs.getString("PARENT_ID"));
				channelUserVO.setOwnerID(rs.getString("OWNER_ID"));
				channelUserVO.setStatus(rs.getString("STATUS"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setGeographicalCode(rs.getString("GRPH_DOMAIN_CODE"));
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				channelUserVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setExternalCode(rs.getString("external_code"));
			
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUser]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUser]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}


	/**
	 * Method loadAllChildUserBalance.
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_userID
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public List loadAllChildUserBalance(Connection pCon, String pUserID ) throws BTSLBaseException {
		final String methodName = "loadChildUserBalanceByCategoryCode";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId=");
        	loggerValue.append(pUserID);
        }
		UserBalancesVO userBalanceVO = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		ArrayList balanceList = null;
		final String sqlSelect = channelUserQry.loadAllChildUserBalanceQry();


		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		try {
			pstmt = pCon.prepareStatement(sqlSelect);
			pstmt.setString(1, pUserID);
			pstmt.setString(2, pUserID);

			rs = pstmt.executeQuery();
			balanceList = new ArrayList();
			while (rs.next()) {
				userBalanceVO = new UserBalancesVO();
				userBalanceVO.setProductShortCode(rs.getString("product_short_code"));
				userBalanceVO.setBalance(rs.getLong("balance"));
				userBalanceVO.setProductShortName(rs.getString("short_name"));
				balanceList.add(userBalanceVO);
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserAgentsBalance]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "", "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserAgentsBalance]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "", "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				LOG.debug("", "Exiting: ");
			}
		}
		return balanceList;
	}

	public String loadTransferRuleFlagForCategory(Connection con, String p_categoryCode) throws BTSLBaseException {
                             final String methodName = "loadTransferRuleFlagForCategory";
                             LogFactory.printLog(methodName,   "Entered  p_categoryCode" + p_categoryCode, LOG);
                             String userId = null;
                             PreparedStatement pstm = null;
                             ResultSet rst = null;
                             String transferAllowed = null;
                             try {
                                           final StringBuilder buf = new StringBuilder("select case  max(length(case when RETURN_ALLOWED='Y' then 'R' end || case when TRANSFER_ALLOWED='Y' then 'TT' end || case when WITHDRAW_ALLOWED='Y' then 'WXYX' end)) ");
                                           buf.append(" when 1 THen 'RET' when 2 THen 'TRF' when 3 Then 'TRF,RET,C2CVOMSTRF,C2CVOMSTRFINI,C2CVOUCHERAPPROVAL,C2CVTAPLST,TRFINI,SIMACT' when 4 then 'WD' WHEN 5 then 'RET,WD' when 6 THen 'TRF,WD,C2CVOMSTRF,C2CVOMSTRFINI,C2CVOUCHERAPPROVAL,C2CVTAPLST,TRFINI,SIMACT' when 7 THen 'TRF,WD,RET,C2CVOMSTRF,C2CVOMSTRFINI,C2CVOUCHERAPPROVAL,C2CVTAPLST,TRFINI,SIMACT'  end  transferAllowed ");
                                           buf.append(" from CHNL_TRANSFER_RULES ct, categories c ");
                                           buf.append(" where ct.STATUS='Y' and c.CATEGORY_CODE=ct.FROM_CATEGORY  ");
                                           buf.append(" and c.CATEGORY_CODE= ? ");
                                           final String query = buf.toString();
                                           LogFactory.printLog(methodName,  "Query: " + query, LOG);
                                           pstm = con.prepareStatement(query);
                                           pstm.setString(1, p_categoryCode);
                                           rst = pstm.executeQuery();
                                           if (rst.next()) {
                                                          transferAllowed=rst.getString("transferAllowed");
                                           }
                             } catch (SQLException sqle) {
                                           LOG.error(methodName, "SQLException " + sqle.getMessage());
                                           LOG.errorTrace(methodName, sqle);
                                           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferRuleFlagForCategory]", "", "", "",
                                                                        "SQL Exception:" + sqle.getMessage());
                                           throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                             } catch (Exception e) {
                                           LOG.error(methodName, "Exception " + e.getMessage());
                                           LOG.errorTrace(methodName, e);
                                           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferRuleFlagForCategory]", "", "", "",
                                                                        "Exception:" + e.getMessage());
                                           throw new BTSLBaseException(this, methodName, "error.general.processing");
                             } finally {
                                           try {
                                               if (rst != null) {
                                            	   rst.close();
                                               }
                                           } catch (SQLException e) {
                                               LOG.errorTrace(methodName, e);
                                           } catch (Exception e) {
                                               LOG.errorTrace(methodName, e);
                                           }
                                           try {
                                               if (pstm != null) {
                                            	   pstm.close();
                                               }
                                           } catch (SQLException e) {
                                               LOG.errorTrace(methodName, e);
                                           } catch (Exception e) {
                                               LOG.errorTrace(methodName, e);
                                           }
                                           LogFactory.printLog(methodName,  "transferAllowed " + transferAllowed, LOG);
                             }
                             return transferAllowed;
              }

	/**
	 * This method is used to load the Channel User Details on the basis of the User_ID. It is created to fetch some specific fields for CLARO.
	 * @author	Ashish K
	 * @param 	Connection p_con
	 * @param 	String p_userID
	 * @return	ChannelUserVO channelUserVO
	 * @throws 	BTSLBaseException
	 */
	public ChannelUserVO loadOwnerChannelUserByUserID(Connection p_con, String p_userID) throws BTSLBaseException
    {
		final String methodName = "loadChannelUserByUserID";
		
        if (LOG.isDebugEnabled()) 	{
        	LOG.debug(methodName, "Entered p_userID:" + p_userID);
        }
        PreparedStatement pstmtSelect = null;
        ChannelUserVO channelUserVO = null;
        ResultSet rs = null;
        try
        {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT U.user_id, U.msisdn umsisdn, U.user_name, U.login_id, U.password, UPHONES.sms_pin,");
            selectQueryBuff.append(" UPHONES.phone_language phlang, UPHONES.country phcountry, UPHONES.msisdn upmsisdn, UPHONES.TEMP_TRANSFER_ID TEMP_TRANSFER_ID, ");
            selectQueryBuff.append(" U.EMPLOYEE_CODE EMPCODE, uphones.user_phones_id  FROM USERS U, USER_PHONES UPHONES ");
            selectQueryBuff.append(" WHERE U.USER_ID=? AND U.STATUS=? AND U.USER_ID=UPHONES.USER_ID AND PRIMARY_NUMBER=?");
            
            if (LOG.isDebugEnabled()) 	{
            	LOG.debug(methodName, "select query := " + selectQueryBuff.toString());
            }
            pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
            pstmtSelect.setString(1, p_userID);
			pstmtSelect.setString(2,PretupsI.USER_STATUS_ACTIVE);
			pstmtSelect.setString(3,PretupsI.USER_PHONE_PRIM_STATUS);
            rs = pstmtSelect.executeQuery();
            if(rs.next())
            {
                channelUserVO = new ChannelUserVO();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setMsisdn(rs.getString("umsisdn"));
                
                UserPhoneVO userPhoneVO=new UserPhoneVO();
                userPhoneVO.setSmsPin(rs.getString("sms_pin"));
                userPhoneVO.setMsisdn(rs.getString("upmsisdn"));
                userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
                userPhoneVO.setCountry(rs.getString("phcountry"));
                channelUserVO.setEmpCode(rs.getString("EMPCODE"));
                userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
                userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
                channelUserVO.setUserPhoneVO(userPhoneVO);
            }
        }
        catch (SQLException sqle)
        {
            if(LOG.isErrorEnabled()){
            	LOG.error(methodName, "SQLException " + sqle.getMessage());
            }
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadChannelUserByUserID]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
        catch (Exception e)
        {
            if(LOG.isErrorEnabled()){
            	LOG.error(methodName, "Exception " + e.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadChannelUserByUserID]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }
        finally
        {
            try{if (rs != null)rs.close();} catch (Exception e){
            	 LOG.error(methodName, "Exception " + e.getMessage());
                 LOG.errorTrace(methodName, e);
            }
            try{if (pstmtSelect != null)pstmtSelect.close();} catch (Exception e){
            	 LOG.error(methodName, "Exception " + e.getMessage());
                 LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) 	{
            	LOG.debug(methodName, "Exiting channelUserVO := " + channelUserVO);
            }
        }
        return channelUserVO;
    }
	

	/**
	 * @author birendra.mishra
	 * @param p_con
	 * @param p_userPhoneVO
	 * @throws BTSLBaseException
	 */
	public int setTempTransferIdOfOwner(Connection p_con, String p_temp_transfer_id, String p_user_id) throws BTSLBaseException {

		final String methodName = "setTempTransferIDofOwner";
		
		ResultSet rs = null;
		PreparedStatement pstmtSelect = null;
		String tempTransferIDOfOwner = "";
		int rowsUpdated = 0;
        
		String selectQuery = "UPDATE USER_PHONES SET TEMP_TRANSFER_ID = ? WHERE USER_ID = ?"; 
		try
        {
			pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_temp_transfer_id);
            pstmtSelect.setString(2, p_user_id);
            rowsUpdated = pstmtSelect.executeUpdate();
            
        }
        catch (SQLException sqle)
        {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"ChannelUserDAO[lockUserPhonesTable]","","","","SQL Exception:"+sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
        }// end of catch
        catch (Exception e)
        {
            LOG.error(methodName, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"ChannelUserDAO[lockUserPhonesTable]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally
        {
            try{if (rs != null)rs.close();} catch (Exception e){
            	 LOG.error(methodName, "Exception " + e.getMessage());
                 LOG.errorTrace(methodName, e);
            }
            try{if (pstmtSelect != null)pstmtSelect.close();} catch (Exception e){
            	 LOG.error(methodName, "Exception " + e.getMessage());
                 LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "Exited...");
        }// end of finally
        
        return rowsUpdated;
		
	}
	
	/**
	 * Method isUserActive() 
	 * This method verify that the user
	 * is active or not on the basis of his userCode/userID(p_userIDCode) 
	 * 
	 * @param p_con
	 * @param p_userID
	 * @param isUserCode
	 * @return boolean
	 * @throws Exception
	 * @throws SQLException
	 */
	
	public boolean isUserActive(Connection p_con, String p_userID, boolean isUserCode) throws Exception
	{
		final String METHOD_NAME = "isUserActive";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered...");
		}
		boolean isActive = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status, u.user_id userstatus FROM users u WHERE u.status <> 'N' AND u.status <> 'C' ");
		if (isUserCode) {
			sqlBuffer.append(" AND u.user_code = ? ");
		} else {
			sqlBuffer.append(" AND u.user_id = ? ");
		}
			
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, " Query : " + sqlBuffer.toString());
			}
			pstmt = p_con.prepareStatement(sqlBuffer.toString());
			int i = 0;
			pstmt.setString(++i, p_userID);
			
			rs = pstmt.executeQuery();
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Before result:" + isActive);
			}
			
			if(rs.next())
			{
				isActive = true ;
			}
			
		}
		catch (SQLException sqe) {
			LOG.error(METHOD_NAME, " SQLException : " + sqe);
			sqe.printStackTrace();
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(METHOD_NAME, " Exception : " + ex);
			ex.printStackTrace();
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				LOG.error(METHOD_NAME, " Exception : " + ex);
				ex.printStackTrace();
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception ex) {
				LOG.error(METHOD_NAME, " Exception : " + ex);
				ex.printStackTrace();
			}
			LOG.debug(METHOD_NAME, "Exit...");
		}
	
		return isActive;
	}
	
	public ArrayList getVoucherFileFormats(Connection con) throws BTSLBaseException {
		final String methodName = "getVoucherFileFormats";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList voucherFormatList = new ArrayList();
		ListValueVO listValueVO = null;
		try {
			StringBuffer sbq = new StringBuffer("select key, value from key_values where type=? ");
			String query = sbq.toString();
			pstmt=con.prepareStatement(query);
			pstmt.setString(1, "VCH_FRMT");
			rs=pstmt.executeQuery();
			while(rs.next()){
				listValueVO = new ListValueVO(rs.getString("key"), rs.getString("value"));
				voucherFormatList.add(listValueVO);
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserHierarchyListForTransfer]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserHierarchyListForTransfer]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userDetailList size:");
				loggerValue.append(voucherFormatList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return voucherFormatList;
	}
	public HashMap<String,String> getUsersBasicDetails(Connection con, String identifierType, String identifierValue ) throws BTSLBaseException {
		final String methodName = "getUsersBasicDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap<String,String> basicDetails = new HashMap<String,String>();
		ListValueVO listValueVO = null;
		try {
			StringBuffer sbq = new StringBuffer("SELECT U.MSISDN, U.LOGIN_ID, U.USER_ID FROM USERS U WHERE U." + identifierType + " = ? ");
			String query = sbq.toString();
			pstmt=con.prepareStatement(query);
			pstmt.setString(1, identifierValue);
			rs=pstmt.executeQuery();
			while(rs.next()){
			    basicDetails.put("MSISDN", rs.getString("MSISDN"));
			    basicDetails.put("LOGIN_ID", rs.getString("LOGIN_ID"));
			    basicDetails.put("USER_ID", rs.getString("USER_ID"));
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[getUsersBasicDetails]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[getUsersBasicDetails]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userDetailList size:");
				loggerValue.append(basicDetails.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return basicDetails;
	}
	
	/*
	 * This method is used to get Payment Modes assigned to user
	 * @param con
	 * @param identifierType
	 * @param identifierValue
	 * @return paymentModes  ie. List of all payment modes associated with user 
	 * @throws BTSLBaseException
	 *          
	 */
	public ArrayList getUserPaymentModes(Connection con, String identifierType, String identifierValue ) throws BTSLBaseException {
		final String methodName = "getUserPaymentModes";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		//HashMap paymentModes = new HashMap<>();
		String paymentType=null;
		String[] paymentModesArray = null;
		ArrayList paymentModes = new ArrayList<>();
		try {
			StringBuffer sbq = new StringBuffer("SELECT U.PAYMENT_TYPE FROM USERS U WHERE U.").append(identifierType).append(" = ? ");
			String query = sbq.toString();
			pstmt=con.prepareStatement(query);
			pstmt.setString(1, identifierValue);
			rs=pstmt.executeQuery();
			while(rs.next()){
				paymentType=SqlParameterEncoder.encodeParams(rs.getString("PAYMENT_TYPE"));
			}
			if(!BTSLUtil.isNullString(paymentType)) {
				paymentModesArray= paymentType.split(",");
			
			StringBuffer strBuild = new StringBuffer();
                strBuild.append("SELECT lu.LOOKUP_CODE, lu.LOOKUP_NAME");
                strBuild.append(" FROM LOOKUPS lu WHERE lu.LOOKUP_TYPE='PMTYP' ");
                strBuild.append(" AND lu.LOOKUP_CODE IN (");
                if (paymentModesArray != null && paymentModesArray.length > 0) {

                    for (int i = 0; i < paymentModesArray.length - 1; i++) {
                        strBuild.append('\'' + paymentModesArray[i] + '\'' + ",");
                    }
                    strBuild.append('\'' + paymentModesArray[paymentModesArray.length - 1] + '\'');
                    strBuild.append(" ) ");
			}
			String query1 = strBuild.toString();
			pstmt1 = con.prepareStatement(query1);
			rs1 = pstmt1.executeQuery();
			while (rs1.next()) {
				paymentModes.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs1.getString("LOOKUP_CODE")), SqlParameterEncoder.encodeParams(rs1.getString("LOOKUP_NAME"))));
			}
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[getUserPaymentModes]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[getUserPaymentModes]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, ex.toString());
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt1 != null) {
                	pstmt1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs1 != null) {
                	rs1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userDetailList size:");
				loggerValue.append(paymentModes.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return paymentModes;
	}
	
	/*
	 * This method will return RoleName with GroupName assigned to a user
	 */
	public HashMap loadUserRoles(Connection con, String userId, String domainType, String categoryCode ) throws BTSLBaseException {
		final String methodName = "loadUserRoles";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count =0;

		 UserRolesVO rolesVO = null;
         ArrayList list = null;
         HashMap<String, ArrayList<String>> map = null;
         
		try {
			StringBuffer sbq = new StringBuffer(" SELECT R.ROLE_NAME, R.GROUP_NAME, R.GROUP_ROLE FROM ROLES R  ");
			sbq.append(" INNER JOIN USER_ROLES UR ON UR.ROLE_CODE = R.ROLE_CODE INNER JOIN CATEGORY_ROLES CR ON UR.ROLE_CODE= CR.ROLE_CODE  ");
			sbq.append(" WHERE UR.USER_ID =? AND R.DOMAIN_TYPE= ? AND CR.CATEGORY_CODE = ? ");
			sbq.append(" ORDER BY R.GROUP_NAME,R.ROLE_NAME ");
			String query = sbq.toString();
			pstmt=con.prepareStatement(query);
			int i=1;
			pstmt.setString(i++, userId);
			pstmt.setString(i++, domainType);
			pstmt.setString(i++, categoryCode);
			rs=pstmt.executeQuery();
			if (rs != null) {
                map = new HashMap<String, ArrayList<String>>();
                String roles=null;
                
			while(rs.next()){
				roles =rs.getString("GROUP_ROLE");
  
                if (map.get(rs.getString("GROUP_NAME")) ==null) 
                    map.put(rs.getString("GROUP_NAME"), new ArrayList<String>() );
                
                map.get(rs.getString("GROUP_NAME")).add(rs.getString("ROLE_NAME"));

                count++;
            }
			if("N".equalsIgnoreCase(roles)) {

            	map.put("role",  new ArrayList<String>(Arrays.asList("System Role")) );
			}else {
            	map.put("role", new ArrayList<String>(Arrays.asList("Group Role")));
            }
            	
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserRoles]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserRoles]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting loadUserRoles size:");
				loggerValue.append(count);
				LOG.debug(methodName, loggerValue);
			}
		}
		return map;
	}
	/*
	 * This method will return commission profile name;
	 */
	
	public String loadUsersCommissionProfile(Connection con, String cpmmProfileId ) throws BTSLBaseException {
		final String methodName = "loadUsersCommissionProfile";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	    String commProfileName =null;
		try {
			StringBuffer sbq = new StringBuffer("  SELECT CPS.COMM_PROFILE_SET_NAME FROM COMMISSION_PROFILE_SET CPS  WHERE CPS.COMM_PROFILE_SET_ID = ? ");
			
			String query = sbq.toString();
			pstmt=con.prepareStatement(query);
			int i=1;
			pstmt.setString(i++, cpmmProfileId);
			rs=pstmt.executeQuery();
			while(rs.next()){
				commProfileName = rs.getString("COMM_PROFILE_SET_NAME");
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUsersCommissionProfile]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUsersCommissionProfile]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting loadUsersCommissionProfile :");
				loggerValue.append(commProfileName);
				LOG.debug(methodName, loggerValue);
			}
		}
		return commProfileName;
	}
	/**
	 * This method check if the user exist in the parent user hieraracy or not
	 * 
	 * @param con
	 * @param p_userId
	 * @param c_identifierType(login_id, or msisdn)
	 * @param c_identifierValue(exclude this user, value)
	 * @return boolean
	 * @throws BTSLBaseException
	 * @author md.sohail
	 */
	public boolean isUserInHierarchy(Connection con, String p_userId, String c_identifierType, String c_identifierValue ) throws BTSLBaseException {
		final String methodName = "isUserInHierarchy";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	   ArrayList<String> p_identifierValueList = new ArrayList<String>();
	   if(c_identifierType.equalsIgnoreCase("LOGINID"))
		{
			c_identifierType = "LOGIN_ID";
		}
		try {
			
			String query = channelUserQry.isUserInHierarchyQry( c_identifierType );
			pstmt=con.prepareStatement(query);
			int i=1;
			pstmt.setString(i++, p_userId);
			rs=pstmt.executeQuery();
			while(rs.next()){
				p_identifierValueList.add(rs.getString(c_identifierType));
			}
			
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[isUserInHierarchy]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[isUserInHierarchy]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isUserInHierarchy :");
				loggerValue.append(p_identifierValueList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return p_identifierValueList.contains(c_identifierValue);
	}
	/**
	 * This method Barred User Details 
	 * 
	 * @param con
	 * @param userId
	 * @param eventType (eg. SUSPND)
	 * @param lookupType (for lookupName)
	 * @param networkCode (for Network Name)
	 * @return barredUserList
	 * @throws BTSLBaseException
	 * @author md.sohail
	 */
	public ArrayList<BarredUserDetailsVO> loadBarredUserDetails(Connection con, String userId, String eventType, String lookupType, String networkCode ) throws BTSLBaseException {
		final String methodName = "loadBarredUserDetails";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	   ArrayList<BarredUserDetailsVO> barredUserList = new ArrayList<BarredUserDetailsVO>();
		try {
			StringBuffer sbq = new StringBuffer(" SELECT UER.USER_ID, UER.EVENT_TYPE, UER.REMARKS, UER.CREATED_BY,UER.CREATED_ON, ");
			sbq.append("  UER.MSISDN, UER.USER_TYPE , L.LOOKUP_NAME AS MODULE_NAME ");
			
			if( !BTSLUtil.isNullString(networkCode)) {
				sbq.append("  ,N.NETWORK_NAME ");
			}
	        sbq.append(" FROM USER_EVENT_REMARKS UER LEFT JOIN LOOKUPS L ON UER.MODULE = L.LOOKUP_CODE ");
			
	        if( !BTSLUtil.isNullString(networkCode)) {
	        	sbq.append("  CROSS JOIN NETWORKS N ");
	        }
	        sbq.append("  WHERE  UER.USER_ID= ? AND UER.EVENT_TYPE = ? AND L.LOOKUP_TYPE = ? ");
	        if( !BTSLUtil.isNullString(networkCode)) {
	        	sbq.append(" AND N.NETWORK_CODE = ? ");
	        }
	        
	        sbq.append(" ORDER BY UER.CREATED_ON DESC ");
	        
			String query = sbq.toString();
			pstmt=con.prepareStatement(query);
			int i=1;
			pstmt.setString(i++, userId);
			pstmt.setString(i++, eventType);
			pstmt.setString(i++, lookupType);
			if( !BTSLUtil.isNullString(networkCode)) {
				pstmt.setString(i++, networkCode);
			}
		
			
			rs=pstmt.executeQuery();
			while(rs.next()){
				BarredUserDetailsVO barredUserDetails = new BarredUserDetailsVO();
				barredUserDetails.setUserId(rs.getString("USER_ID"));
				barredUserDetails.setReasonOfBarring(rs.getString("REMARKS"));      //remarks
				barredUserDetails.setBarredBy(rs.getString("CREATED_BY"));
				
				BTSLUtil.getUtilDateFromSQLDate(rs.getDate("CREATED_ON"));
				String date = new SimpleDateFormat("dd/MM/yy") .format(rs.getDate("CREATED_ON"));
				
				barredUserDetails.setBarredOn(date);
				barredUserDetails.setModule(rs.getString("MODULE_NAME"));          //set moduleName
				barredUserDetails.setMsisdn(rs.getString("MSISDN"));
				barredUserDetails.setUserType(rs.getString("USER_TYPE"));
			    if( !BTSLUtil.isNullString(networkCode)) {
			    	barredUserDetails.setNetworkName(rs.getString("NETWORK_NAME"));
			    }
			    barredUserDetails.setBaredType(rs.getString("EVENT_TYPE"));             //setting event type : confirm it
			    barredUserList.add(barredUserDetails);
			}
			
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadBarredUserDetails]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadBarredUserDetails]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting loadBarredUserDetails :");
				loggerValue.append(barredUserList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return barredUserList;
	}
	
	/**
	 *
	 */
	public ArrayList<String> loadUserWigets(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadUserWigets";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<String> widgetlist = new ArrayList<String>();
	    String result = "INC";
	    String output = "";
		try {
			StringBuffer sbq = new StringBuffer(" SELECT  USER_WIDGET_LIST");	
			sbq.append(" from USER_WIDGETS Where  USER_ID = ? ");
			String query = sbq.toString();
			pstmt=con.prepareStatement(query);
			int i=1;
			pstmt.setString(i, userId);
			rs=pstmt.executeQuery();
			while(rs.next()){
			output = rs.getString("USER_WIDGET_LIST");
			}
			if(output == null || output.length() == 0) {
				return widgetlist;
			}
			result = output;
			String[] str1 = result.split(",");
			for(int j =0;j< str1.length;j++) {
				widgetlist.add(str1[j]);
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadBarredUserDetails]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			//throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadBarredUserDetails]", "", "", "", "Exception:" + ex.getMessage());
			//throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting loadBarredUserDetails :");
				loggerValue.append(result);
				LOG.debug(methodName, loggerValue);
			}
		}
		return widgetlist;
	}
	
	/**
	 *
	 */
	public int updateUserWigets(Connection con, String userId,String widgets) throws BTSLBaseException {
		final String methodName = "updateUserWigets";
		StringBuilder loggerValue= new StringBuilder();
       if (LOG.isDebugEnabled()) {
           LOG.debug(methodName, "Entered");
       }
       
		PreparedStatement pstmtS = null;
		PreparedStatement pstmtI = null;
		PreparedStatement pstmtU = null;
		ResultSet rsS = null;
		try {
			
			StringBuffer select = new StringBuffer(" Select * from USER_WIDGETS where USER_ID = ? ");	
			String queryselect = select.toString();
			pstmtS=con.prepareStatement(queryselect);
			pstmtS.setString(1, userId);
			rsS=pstmtS.executeQuery();
			if(!rsS.next() && widgets.length() != 0){
				StringBuffer sbqIns = new StringBuffer(" INSERT INTO USER_WIDGETS (USER_ID,USER_WIDGET_LIST)  VALUES(?, ?)  ");	
				String queryInsert = sbqIns.toString();
				
				pstmtI=con.prepareStatement(queryInsert);
				int i=1;
				pstmtI.setString(i++, userId);
				pstmtI.setString(i++, widgets);
				int ans =pstmtI.executeUpdate();
				return ans;
			}else {
				if( widgets.length() != 0) {
				StringBuffer sbqUpdate = new StringBuffer(" UPDATE USER_WIDGETS SET USER_WIDGET_LIST = ?  WHERE USER_ID = ? ");	
				String queryUpdate = sbqUpdate.toString();
				pstmtU=con.prepareStatement(queryUpdate);
				int i=1;
				pstmtU.setString(i++, widgets);
				pstmtU.setString(i++, userId);
				int ans =pstmtU.executeUpdate();
				return ans;
				}else {
					StringBuffer sbqUpdate = new StringBuffer(" DELETE FROM USER_WIDGETS WHERE USER_ID = ? ");	
					String queryUpdate = sbqUpdate.toString();
					pstmtU=con.prepareStatement(queryUpdate);
					int i=1;
					pstmtU.setString(i++, userId);
					int ans =pstmtU.executeUpdate();
					return ans;
				
				}
			}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler
			.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[updateUserWigets]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[updateUserWigets]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			try {
               if (pstmtS != null) {
            	   pstmtS.close();
               }
               if (pstmtU != null) {
            	   pstmtU.close();
               }
               if (pstmtI != null) {
            	   pstmtI.close();
               }
           } catch (SQLException e) {
               LOG.errorTrace(methodName, e);
           } catch (Exception e) {
               LOG.errorTrace(methodName, e);
           }
			try {
               if (rsS != null) {
               	rsS.close();
               }
           } catch (SQLException e) {
               LOG.errorTrace(methodName, e);
           } catch (Exception e) {
               LOG.errorTrace(methodName, e);
           }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting updateUserWigets :");
				LOG.debug(methodName, loggerValue);
			}
		}
	}
	

	/**
	 * Method debitUserBalances() This method check user minimum balance limits
	 * and Debit the user Balances if limit does not cross by the new balance
	 * (existing balance-new requested credit balance)
	 * 
	 * @param con
	 * @param channelTransferVO
	 * @param isFromWeb
	 *            boolean
	 * @param forwardPath
	 *            String
	 * @return int
	 * @throws BTSLBaseException
	 */

	public ArrayList debitUserBalancesForO2CRest(Connection con, ChannelTransferVO channelTransferVO, String msisdn, int recordNumber, Locale p_locale) throws BTSLBaseException {
		final String methodName = "debitUserBalancesForO2CRest";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: channelTransferVO =");
        	loggerValue.append(channelTransferVO);
        	loggerValue.append(" msisdn=");
        	loggerValue.append(msisdn);
        	loggerValue.append(" recordNumber=");
        	loggerValue.append(recordNumber);
            LOG.debug(methodName, loggerValue);
        }
		int updateCount = 0;
		final ArrayList errorList = new ArrayList();
		PreparedStatement pstmt = null;
		PreparedStatement psmtUpdate = null;
		PreparedStatement psmtInsertUserThreshold = null;
		ResultSet rs = null;
		StringBuilder strBuffSelect= channelUserQry.debitUserBalancesForO2CQry();
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        String walletForAdnlCmsn = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN);		

		final StringBuilder strBuffUpdate = new StringBuilder();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
		strBuffUpdate.append(" WHERE ");
		strBuffUpdate.append(" user_id = ? ");
		strBuffUpdate.append(" AND ");
		strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");

		if (userProductMultipleWallet) {
			strBuffUpdate.append("  and balance_type=? ");
		}

		// added by vikram
		final StringBuilder strBuffThresholdInsert = new StringBuilder();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		final String updateQuery = strBuffUpdate.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

		final String sqlSelect = strBuffSelect.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Select Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("insertUserThreshold Query =");
			loggerValue.append(insertUserThreshold);
			LOG.debug(methodName, loggerValue);
		}
		try {
			psmtUpdate = con.prepareStatement(updateQuery);
			psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);

			final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
			TransferProfileProductVO transferProfileProductVO = null;
			long minBalance = 0;
			int maxAllowPct = 0;
			long maxAllowBalance = 0;
			// added by vikram
			final Date currentDate = new Date();
			long thresholdValue = -1;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			String userID = null;
			String profileID = null;
			String userCode = null;
			boolean bonusDeducted = false;
			boolean mainDeducted = false;
			/*
			 * In case of transfer : debit the from user which is logined user
			 * In case of retutn : debit the from user which is logined user In
			 * case of Withdraw : debit the To user which is Searched user
			 */
			userID = channelTransferVO.getFromUserID();
			profileID = channelTransferVO.getSenderTxnProfile();
			userCode = channelTransferVO.getFromUserCode();
			boolean isNotToExecuteQuery = false;
			final ArrayList errorBalanceNotExistList = new ArrayList();
			final ArrayList errorBalanceLessList = new ArrayList();
			final ArrayList errorBalanceMinList = new ArrayList();

			ListValueVO errorVO = null;
			errorVO = new ListValueVO();
			int itemsListSize = itemsList.size();
			for (int i = 0, k = itemsListSize; i < k; i++) {
				boolean toAddMoreError = true;
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				if(LOG.isDebugEnabled()){
					LOG.debug(methodName, "Loading details for userID="+userID+",channelTransferItemsVO.getProductCode()="+channelTransferItemsVO.getProductCode());
				}
				pstmt = con.prepareStatement(sqlSelect);
				pstmt.setString(1, userID);
				pstmt.setString(2, channelTransferItemsVO.getProductCode());
				pstmt.setString(3, channelTransferVO.getNetworkCode());
				pstmt.setString(4, channelTransferVO.getNetworkCodeFor());
				rs = pstmt.executeQuery();
				long balance = -1;
				long bonusBalance = -1;
				long checkBalance = 0;
				while (rs.next()) {
					if (walletForAdnlCmsn.equals(rs.getString("balance_type"))) {
						bonusBalance = rs.getLong("balance");
					} else {
						balance = rs.getLong("balance");
					}
				}
				if(LOG.isDebugEnabled()){
					LOG.debug(methodName, "After Fecthing  Balance="+balance+",bonusBalance="+bonusBalance);
				}
				if (balance == -1 && bonusBalance == -1) {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}

					errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.withdraw.userbalance.notexist"));
					errorList.add(errorVO);

				}

				if (userProductMultipleWallet) {
					channelTransferItemsVO.setPreviousBonusBalance(bonusBalance);
					channelTransferItemsVO.setAfterTransSenderPreviousBonusStock(bonusBalance);
					checkBalance = balance + bonusBalance;
					if (balance > 0) {
						mainDeducted = true;
					}
				} else {
					checkBalance = balance;
				}
				if(LOG.isDebugEnabled()){
					LOG.debug(methodName, "For userID="+userID+",checkBalance="+checkBalance+",channelTransferItemsVO.getRequiredQuantity()="+channelTransferItemsVO.getRequiredQuantity());
				}
				channelTransferItemsVO.setBalance(checkBalance);
				if (checkBalance < channelTransferItemsVO.getRequiredQuantity() && toAddMoreError) {
					toAddMoreError = false;
					if (!isNotToExecuteQuery) {
						isNotToExecuteQuery = true;
					}

					errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString( "batcho2c.withdraw.userbalance.less"));
					errorList.add(errorVO);

				}
				channelTransferItemsVO.setPreviousBalance(balance);// set the
				// previous
				// balance
				channelTransferItemsVO.setAfterTransSenderPreviousStock(balance);

				// in the case of return we have not to check the min balance
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType()) && toAddMoreError) {
					/*
					 * check for the min balance for the product
					 */
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();
					maxAllowBalance = (balance * maxAllowPct) / 100;
					if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
						toAddMoreError = false;
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}

						errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.withdraw.allowedmaxpct.isless"));
						errorList.add(errorVO);

					}
					if (toAddMoreError) {
						if (balance > -1) {
							balance -= channelTransferItemsVO.getRequiredQuantity();
						}
						minBalance = transferProfileProductVO.getMinResidualBalanceAsLong();
						if (minBalance > balance) {
							toAddMoreError = false;
							if (!isNotToExecuteQuery) {
								isNotToExecuteQuery = true;
							}

							errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.withdraw.minbalance.reached"));
							errorList.add(errorVO);

						}
					}// if to add more errors
				}// if for transfer check
				if (!PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(channelTransferVO.getTransferType())) {

					if (userProductMultipleWallet) {
						if (balance > -1 && balance >= channelTransferItemsVO.getRequiredQuantity()) {
							balance -= channelTransferItemsVO.getRequiredQuantity();

						} else if (bonusBalance > -1) {
							if (!mainDeducted) {
								channelTransferVO.setReceiverCrQty(0);
							}
							bonusBalance -= (channelTransferItemsVO.getRequiredQuantity() - balance);
							channelTransferVO.setCommQty(channelTransferItemsVO.getRequiredQuantity() - balance);
							balance = 0;
							bonusDeducted = true;

						}
					}

					else if (balance > -1) {
						balance -= channelTransferItemsVO.getRequiredQuantity();
					}
				}

				if (!isNotToExecuteQuery) {
					int m = 0;
					psmtUpdate.setLong(++m, balance);
					psmtUpdate.setString(++m, channelTransferVO.getTransferType());
					psmtUpdate.setString(++m, channelTransferVO.getTransferID());
					psmtUpdate.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
					psmtUpdate.setString(++m, userID);
					psmtUpdate.setString(++m, channelTransferItemsVO.getProductCode());
					psmtUpdate.setString(++m, channelTransferVO.getNetworkCode());
					psmtUpdate.setString(++m, channelTransferVO.getNetworkCodeFor());
					if (userProductMultipleWallet) {
						psmtUpdate.setString(++m, defaultWallet);
						if (mainDeducted) {
							updateCount = psmtUpdate.executeUpdate();
						} else {
							updateCount = 1;
						}
						if (updateCount > 0 && bonusDeducted) {
							m = 0;
							psmtUpdate.clearParameters();
							psmtUpdate.setLong(++m, bonusBalance);
							psmtUpdate.setString(++m, channelTransferVO.getTransferType());
							psmtUpdate.setString(++m, channelTransferVO.getTransferID());
							psmtUpdate.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
							psmtUpdate.setString(++m, userID);
							psmtUpdate.setString(++m, channelTransferItemsVO.getProductCode());
							psmtUpdate.setString(++m, channelTransferVO.getNetworkCode());
							psmtUpdate.setString(++m, channelTransferVO.getNetworkCodeFor());
							psmtUpdate.setString(++m, walletForAdnlCmsn);
							updateCount = psmtUpdate.executeUpdate();
						}
					} else {
						updateCount = psmtUpdate.executeUpdate();
					}
					if (updateCount <= 0) {
						errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString(
								"batcho2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);

					}

					psmtUpdate.clearParameters();
					// added by nilesh
					// transferProfileProductVO
					// =(TransferProfileProductVO)profileDAO.loadTransferProfileProducts(con,profileID,channelTransferVO.getProductCode())
					// commented
					transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(profileID, channelTransferItemsVO.getProductCode());
					thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
					String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					final String remark = null;

					if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
						thresholdValue = transferProfileProductVO.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					}
					// end of nilesh

					// for zero balance counter added by vikram
					try {
						if ((channelTransferItemsVO.getPreviousBalance() >= thresholdValue && balance <= thresholdValue) || (channelTransferItemsVO.getPreviousBalance() <= thresholdValue && balance <= thresholdValue)) {
							//shashi :changes start here for Threshold balance alert
							UserBalancesVO vo = new UserBalancesVO();
							vo.setUserID(userID);
							vo.setProductCode(channelTransferItemsVO.getProductCode());
							vo.setNetworkCode(channelTransferVO.getNetworkCode());
							vo.setLastTransferID(channelTransferVO.getTransferID());
							new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,channelTransferVO.getCategoryCode());
							//end here

							m = 0;
							m++;
							psmtInsertUserThreshold.setString(m, userID);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferID());
							m++;
							psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(currentDate));
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getNetworkCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferItemsVO.getProductCode());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getType());
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getTransferType());
							m++;
							psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
							m++;
							psmtInsertUserThreshold.setString(m, channelTransferVO.getCategoryCode());
							m++;
							psmtInsertUserThreshold.setLong(m, channelTransferItemsVO.getPreviousBalance());
							m++;
							psmtInsertUserThreshold.setLong(m, balance);
							m++;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							m++;
							psmtInsertUserThreshold.setString(m, threshold_type);
							m++;
							psmtInsertUserThreshold.setString(m, remark);
							updateCount = psmtInsertUserThreshold.executeUpdate();
							psmtInsertUserThreshold.clearParameters();
							if(LOG.isDebugEnabled()){
								LOG.debug(methodName, "psmtInsertUserThreshold="+updateCount);
							}
						}
						//shishupal :changes start here for SoS eligibility alert
						if (channelSosEnable&&channelTransferVO.getChannelSoSVOList()!=null)
						{
							try {
								ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
								for(int index=0;index<channelTransferVO.getChannelSoSVOList().size();index++){
									channeluserList.add(channelTransferVO.getChannelSoSVOList().get(index));
								}
								new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, userID, balance,channelTransferItemsVO.getPreviousBalance());
							} catch (BTSLBaseException ex) {
								LOG.errorTrace(methodName, ex);
							}
						}
					} catch (SQLException sqle) {
						loggerValue.setLength(0);
						loggerValue.append(SQL_EXCEPTION);
						loggerValue.append(sqle.getMessage());
						LOG.error(methodName, loggerValue);
						LOG.errorTrace(methodName, sqle);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForO2C]",
								channelTransferVO.getTransferID(), "", channelTransferVO.getNetworkCode(),
								"Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
					}// end of catch
				}// if to execute query
			}// for loop

			// for balance logger
			channelTransferVO.setEntryType(PretupsI.DEBIT);
		} catch (BTSLBaseException bbe) {
			throw bbe;
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			// EventHandle\r.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[debitUserBalances]","","","","SQL
			// Exception:"+sqe.getMessage()) commented
			// throw new BTSLBaseException(this, "debitUserBalances",
			// "error.general.sql.processing") commented
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			// EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[debitUserBalances]","","","","Exception:"+ex.getMessage())commented
			// throw new BTSLBaseException(this, "debitUserBalances",
			// "error.general.processing") commented
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtUpdate != null) {
                	psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (psmtInsertUserThreshold != null) {
                	psmtInsertUserThreshold.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting updateCount:");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		}
		return errorList;
	}
	public boolean searchByUserId(Connection con, String userId,List<String> listLoginId1 ) throws Exception {
 
		boolean existFlag = false;
		StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT DISTINCT LOGIN_ID FROM USERS u WHERE LOWER(LOGIN_ID) LIKE LOWER(?) ");

		String sqlSelect = strBuff.toString();

		try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
        String userIdNew=userId+"%";
			pstmt.setString(1, userIdNew);

			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					
					listLoginId1.add(rs.getString("LOGIN_ID"));
				}
			existFlag = listLoginId1.contains(userId);

				return existFlag;
			}
		} catch (SQLException sqe) {

			return false;

		}
	}
	
	
	public boolean isUserExistByUserName(Connection con, String userName,String networkCode) throws BTSLBaseException {

		final String methodName = "isUserExistByUserName";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode=");
        	loggerValue.append(networkCode);
        	loggerValue.append(" userName=");
        	loggerValue.append(userName);
            LOG.debug(methodName, loggerValue);
        }
		
		PreparedStatement pstmt = null;
		
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT 1 FROM users");
		strBuff.append(" WHERE network_code = ? AND user_name = ? ");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		boolean isExist = false;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++, networkCode);
			pstmt.setString(i++, userName);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			if (rs.next()) {
				isExist = true;
			}
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByParentID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserExistByParentID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
		
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting isExist:");
				loggerValue.append(isExist);
				LOG.debug(methodName, loggerValue);
			}
		}
		return isExist;
	}
	

	
	public List<UserMsisdnUserIDVO> fetchStaffUserdetailsUnderChannelUser(
			 Connection pCon,String pUserCategory,String domainCode,String channelUserID,String pZoneCode ,String checkUserID )throws SQLException, BTSLBaseException {
		
		final String  methodName= "fetchStaffUserdetailsUnderChannelUser";
		List<UserMsisdnUserIDVO> listMsisdnUserID = new ArrayList();
		
		String sqlBaseQry =channelUserQry.loadStaffUserDetailbyCHUser();
		final StringBuilder strBuff = new StringBuilder(sqlBaseQry);
        
        
        if(!BTSLUtil.isNullString(checkUserID)) {  // Same API  to Validate UserID.,extra parameters  checkUserID
        	strBuff.append(" AND U.user_id = ? ");
        }
        
        strBuff.append(" ORDER BY U.user_name");
        
        if (LOG.isDebugEnabled()) {
        	LOG.debug("fetchStaffUserdetailsUnderChannelUser", "QUERY sqlSelect= " + strBuff.toString());
        }
        
        StringBuilder msg = new StringBuilder();
        PreparedStatement pstmtSelect =null;
        try {
        	pstmtSelect=  pCon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, channelUserID);
        pstmtSelect.setString(++i, channelUserID);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, channelUserID);
        pstmtSelect.setString(++i, channelUserID);
        
        if(!BTSLUtil.isNullString(checkUserID)) {  // Same API  to Validate UserID.,extra parameters  checkUserID
        	pstmtSelect.setString(++i, checkUserID);
        }
        
        
                
        try(ResultSet rs = pstmtSelect.executeQuery();)
    	{
        while (rs.next()) {
        	UserMsisdnUserIDVO    userMsisdnUserIDVO = new UserMsisdnUserIDVO();
        	userMsisdnUserIDVO.setUserID(rs.getString("USERID"));
        	userMsisdnUserIDVO.setUserName(rs.getString("USER_NAME"));
        	userMsisdnUserIDVO.setMsisdn(rs.getString("MSISDN"));
        	userMsisdnUserIDVO.setLoginID(rs.getString("LOGINID"));
        	if(rs.getString("MSISDN")==null) {
        	userMsisdnUserIDVO.setUserNameMsisdn(rs.getString("USER_NAME") +" (NA)");
        	}else {
        		userMsisdnUserIDVO.setUserNameMsisdn(rs.getString("USER_NAME") +" ("+ rs.getString("MSISDN") + ")");	
        	}
        	listMsisdnUserID.add(userMsisdnUserIDVO);
        }

    	}
    	}catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[fetchStaffUserdetailsUnderChannelUser]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[fetchStaffUserdetailsUnderChannelUser]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "List of User Msisdn:" + listMsisdnUserID);
            }

        
        
        
        
        return listMsisdnUserID;

	}

	

	public List<UserMsisdnUserIDVO> loadUserNameAutoSearchOnZoneDomainCategoryQry(
			String pFromUserID, String pUserName,Connection pCon,String pUserCategory,String domainCode,String ploginuserID,String pZoneCode ,String checkUserID )throws SQLException, BTSLBaseException {
		
		final String  methodName= "loadUserPhoneListOnZoneDomainCategoryQry";
		List<UserMsisdnUserIDVO> listMsisdnUserID = new ArrayList();
		
		String sqlBaseQry =channelUserQry.loadUserNameAutoSearchOnZoneDomainCategoryQry();
		final StringBuilder strBuff = new StringBuilder(sqlBaseQry);
        
        if (!BTSLUtil.isNullString(pFromUserID)) {
            strBuff.append(" AND U.user_id <> ?");
        }
        
        if(!BTSLUtil.isNullString(checkUserID)) {  // Same API  to Validate UserID.,extra parameters  checkUserID
        	strBuff.append(" AND U.user_id = ? ");
        }
        
        if (!BTSLUtil.isNullString(pUserName)) {
            strBuff.append("  AND UPPER(U.user_name) LIKE UPPER(?) ");
        }
        strBuff.append(" ORDER BY U.user_name");
        
        if (LOG.isDebugEnabled()) {
        	LOG.debug("loadUserPhoneListOnZoneDomainCategoryQry", "QUERY sqlSelect= " + strBuff.toString());
        }
        
        StringBuilder msg = new StringBuilder();
        PreparedStatement pstmtSelect =null;
        try {
        	pstmtSelect=  pCon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, domainCode);
        //pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        //pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, ploginuserID);
        if (!BTSLUtil.isNullString(pFromUserID)) {
            pstmtSelect.setString(++i, pFromUserID);
        }
        
        if(!BTSLUtil.isNullString(checkUserID)) {  // Same API  to Validate UserID.,extra parameters  checkUserID
        	pstmtSelect.setString(++i, checkUserID);
        }
        
        
        if (!BTSLUtil.isNullString(pUserName)) {
        	String userName =pUserName+"%";
            pstmtSelect.setString(++i, userName );
        }
        
        try(ResultSet rs = pstmtSelect.executeQuery();)
    	{
        while (rs.next()) {
        	UserMsisdnUserIDVO    userMsisdnUserIDVO = new UserMsisdnUserIDVO();
        	userMsisdnUserIDVO.setUserID(rs.getString("USERID"));
        	userMsisdnUserIDVO.setUserName(rs.getString("USER_NAME"));
        	userMsisdnUserIDVO.setMsisdn(rs.getString("MSISDN"));
        	userMsisdnUserIDVO.setUserNameMsisdn(rs.getString("USER_NAME") +"("+ rs.getString("MSISDN") + ")");
        	listMsisdnUserID.add(userMsisdnUserIDVO);
        }

    	}
    	}catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "List of User Msisdn:" + listMsisdnUserID);
            }

        
        
        
        
        return listMsisdnUserID;

	}
	
	public ArrayList loadUserServicesList1(Connection con, String userId) throws BTSLBaseException {

		final String methodName = "loadUserServicesList";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:UserId =");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT US.service_type,US.status,ST.name FROM user_services US,users U,category_service_type CST,service_type ST");
		strBuff.append(" WHERE US.user_id = ? AND US.status <> 'N'");
		strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code and US.SERVICE_TYPE = ST.SERVICE_TYPE");
		final String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Select Query=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName, loggerValue);
		}
		final ArrayList list = new ArrayList();
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
			
			pstmt.setString(1, userId);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
//				list.add(new ListValueVO(rs.getString("status"), rs.getString("service_type")));
				ListValueVO vo=new ListValueVO(rs.getString("name"), rs.getString("service_type"));
				vo.setStatus(rs.getString("status"));
				list.add(vo);

			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
        	loggerValue.append(SQL_EXCEPTION);
        	loggerValue.append(sqe.getMessage());
            LOG.error(methodName, loggerValue);			
            LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting userServicesList size:");
				loggerValue.append(list.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param p_con
	 * @param p_categoryCode
	 * @param userName
	 * @param p_networkCode
	 * @param p_ownerUserID
	 * @param p_statusUsed
	 * @param p_status
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList loadCategoryUsers(Connection p_con, String p_categoryCode, String userName, String p_networkCode, String p_ownerUserID, String p_statusUsed, String p_status) throws BTSLBaseException {

        final String methodName = "loadCategoryUsers";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Category Code =");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append("Entered: Username =");
        	loggerValue.append(userName);
        	loggerValue.append(" Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" ownerUserID: ");
        	loggerValue.append(p_ownerUserID);
        	loggerValue.append(" p_statusUsed: ");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(" p_status: ");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }

        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT u.user_id,u.user_name,u.login_id FROM users u WHERE u.network_code = ? AND  ");
        strBuff.append(" u.category_code = ? ");
        if(userName!=null && !userName.trim().equals("%%") ) {
        	strBuff.append("AND UPPER(u.user_name) LIKE UPPER(?) ");
        }
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND u.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND u.status NOT IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND u.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND u.status <> ? ");
        }

        if (p_ownerUserID != null) {
            strBuff.append(" AND  u.owner_id = ?  ");
        }
        strBuff.append(" AND  u.USER_TYPE = ?  ");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Query =");
        	loggerValue.append(sqlSelect);
        	LOG.debug(methodName, loggerValue);
        }

        final ArrayList arrayList = new ArrayList();
        try {
            // commented for DB2 pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_categoryCode);
            if(userName!=null && !userName.trim().equals("%%") ) {
	            ++i;
	            pstmt.setString(i, userName);
            }
            
            // commented for DB2 pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);

            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                ++i;
                pstmt.setString(i, p_status);

            }

            if (p_ownerUserID != null) {
                ++i;
                pstmt.setString(i, p_ownerUserID);

            }
            ++i;
            pstmt.setString(i, PretupsI.USER_TYPE_CHANNEL);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                //arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id"),rs.getString("user_id"),rs.getString("login_id")));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUsers]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUsers]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: arrayList.Size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);            }
        }
        return arrayList;
    }


             
	/**
	 * 
	 * @param p_con
	 * @param channelUserId
	 * @return
	 * @throws BTSLBaseException
	 */
	public List<PaymentModeDetailsDto> loadPaymentModesAndRanges(Connection p_con, String channelUserId) throws BTSLBaseException {
		final String methodName = "loadPaymentModesAndRanges";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Entered: channelUserId  =");
        	loggerValue.append(channelUserId);
		}
		 List<PaymentModeDetailsDto> listPaymentDetilsdto=null;
		 PreparedStatement pstmt = null;
	     ResultSet rs = null;
	     final StringBuffer strBuff = new StringBuffer();
	     strBuff.append("		\r\n"
	     		+ "  SELECT DISTINCT cu.USER_ID,cps.COMM_PROFILE_SET_ID ,cps.COMM_PROFILE_SET_NAME ,cps.COMM_LAST_VERSION ,cpp.PAYMENT_MODE,\r\n"
	     		+ "  cpp.PRODUCT_CODE,cpp.MIN_TRANSFER_VALUE,cpp.MAX_TRANSFER_VALUE ,cpp.TRANSFER_MULTIPLE_OFF,cpp.TRANSACTION_TYPE \r\n"
	     		+ "  FROM CHANNEL_USERS cu ,COMMISSION_PROFILE_SET cps ,COMMISSION_PROFILE_PRODUCTS cpp \r\n"
	     		+ "  WHERE cu.USER_ID = ?\r\n"
	     		+ "  AND cu.COMM_PROFILE_SET_ID =cps.COMM_PROFILE_SET_ID  \r\n"
	     		+ "  AND cpp.COMM_PROFILE_SET_ID =cps.COMM_PROFILE_SET_ID\r\n"
	     		+ "  AND cpp.COMM_PROFILE_SET_VERSION =cps.COMM_LAST_VERSION \r\n"
	     		+ "  ");
	     final String sqlSelect = strBuff.toString();
	     if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("Select Query=");
	        	loggerValue.append(sqlSelect);
	            LOG.debug(methodName, loggerValue);
			}
	     try {
	    	  pstmt = p_con.prepareStatement(sqlSelect);
	            int i = 0;
	            ++i;
	            pstmt.setString(i, channelUserId);
	            rs = pstmt.executeQuery();
	            listPaymentDetilsdto=new ArrayList<>();
	            while (rs.next()) {
	               PaymentModeDetailsDto dto=null;
			              dto=new PaymentModeDetailsDto();
			              dto.setUserId(rs.getString("USER_ID"));
			              dto.setCommProfileSetId(rs.getString("COMM_PROFILE_SET_ID"));
			              dto.setCommProfileSetName(rs.getString("COMM_PROFILE_SET_NAME"));
			              dto.setCommLastVersion(rs.getString("COMM_LAST_VERSION"));
			              dto.setPaymentMode(rs.getString("PAYMENT_MODE"));
			              dto.setProductcode(rs.getString("PRODUCT_CODE"));
			              dto.setMinTransferValue(rs.getLong("MIN_TRANSFER_VALUE"));
			              dto.setMaxTransferValue(rs.getLong("MAX_TRANSFER_VALUE"));
			              dto.setTransferMultipleOff(rs.getLong("TRANSFER_MULTIPLE_OFF"));
			              dto.setTransferType(rs.getString("TRANSACTION_TYPE"));
			              listPaymentDetilsdto.add(dto);
	            }
	     } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
				loggerValue.append(SQL_EXCEPTION);
				loggerValue.append(sqe.getMessage());
				LOG.error(methodName, loggerValue);
	            LOG.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUsers]", "", "", "",
	                loggerValue.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	        	loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(ex.getMessage());
				LOG.error(methodName, loggerValue);
	            LOG.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUsers]", "", "", "",
	                loggerValue.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (SQLException ex) {
	                LOG.errorTrace(methodName, ex);
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            try {
	                if (pstmt != null) {
	                    pstmt.close();
	                }
	            } catch (SQLException ex) {
	                LOG.errorTrace(methodName, ex);
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }

	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: arrayList.Size:");
	            	loggerValue.append(listPaymentDetilsdto.size());
	            	LOG.debug(methodName, loggerValue);            }
	        }
		return listPaymentDetilsdto;
	}
	
	/**
	 * This method is used to load the Channel User Details on the basis of the
	 * User_ID(Initiated_id) and it is used in ScheduledTopUp Process.
	 * 
	 * @author Ashish K
	 * @param Connection
	 *            con
	 * @param String
	 *            userId
	 * @return ChannelUserVO channelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadChannelUserByUserIDAnyStatus(Connection con, String userId) throws BTSLBaseException {
		final String methodName = "loadChannelUserByUserID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: User ID=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		 
		ChannelUserVO channelUserVO = null;
		
		try {

			String selectQueryBuff = channelUserQry.loadChannelUserByUserIDAnyStatusQry();
			try (PreparedStatement pstmtSelect = con.prepareStatement(selectQueryBuff);)
			{
			pstmtSelect.setString(1, userId);
			pstmtSelect.setString(2, PretupsI.USER_PHONE_PRIM_STATUS);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password"));
				channelUserVO.setMsisdn(rs.getString("umsisdn"));
				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setMsisdn(rs.getString("upmsisdn"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				channelUserVO.setBalance(rs.getLong("balance"));
				channelUserVO.setPaymentTypes(rs.getString("payment_type"));
				channelUserVO.setUserPhoneVO(userPhoneVO);
			}
		}
			}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserByUserID]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserByUserID]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}
    
	
	/**
	 * Method loadUsersDetails. This method is used to load all the information
	 * used to display in the ICCID MSISDN KEY MANAGEMENT Module
	 * 
	 * @author sandeep.goel
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @param userId
	 *            String
	 * @param statusUsed
	 *            String
	 * @param status
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadUsersDetailsByMsisdnOrLogin(Connection con, String msisdn,String LoginId , String statusUsed, String status,String networkCode) throws BTSLBaseException {
		final String methodName = "loadUsersDetailsByMsisdnOrLogin";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:msisdn =");
        	loggerValue.append(msisdn);
        	loggerValue.append(" stausUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" staus=");
        	loggerValue.append(status);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		ChannelUserVO channelUserVO = null;
		
		try {
			pstmtSelect = channelUserQry.loadUsersDetailsByLoginOrMsisdnQry(con, msisdn, LoginId, status, statusUsed,networkCode);
			rs = pstmtSelect.executeQuery();
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setBatchID(rs.getString("batch_id"));
				channelUserVO.setCreationType(rs.getString("creation_type"));
				channelUserVO.setUserID(rs.getString("usr_user_id"));
				channelUserVO.setUserName(rs.getString("usr_user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password1"));
				channelUserVO.setCategoryCode(rs.getString("usr_category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setReferenceID(rs.getString("reference_id"));
				channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("usr_status"));
				channelUserVO.setEmail(rs.getString("email"));
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				channelUserVO.setLanguage(rs.getString("planguage") + "_" + rs.getString("pcountry"));
				// end added by deepika aggarwal
				channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
				channelUserVO.setContactNo(rs.getString("contact_no"));
				channelUserVO.setDesignation(rs.getString("designation"));
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("usr_msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(rs.getTimestamp("created_on"));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
				channelUserVO.setLastModified(rs.getTimestamp("modified_on").getTime());
				channelUserVO.setAddress1(rs.getString("address1"));
				channelUserVO.setAddress2(rs.getString("address2"));
				channelUserVO.setCity(rs.getString("city"));
				channelUserVO.setState(rs.getString("state"));
				channelUserVO.setCountry(rs.getString("country"));
				channelUserVO.setRsaFlag(rs.getString("rsaflag"));
				channelUserVO.setSsn(rs.getString("ssn"));
				channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
				channelUserVO.setExternalCode(rs.getString("external_code"));
				channelUserVO.setShortName(rs.getString("short_name"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
				channelUserVO.setUserCode(rs.getString("user_code"));
				channelUserVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				channelUserVO.setLongitude(rs.getString("LONGITUDE")); 
				// 5.1.3
				channelUserVO.setDomainName(rs.getString("domain_name"));
				channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
				categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setFixedRoles(rs.getString("fixed_roles"));
				categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
				categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setMaxTxnMsisdn(rs.getInt("max_txn_msisdn") + "");
				categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
				categoryVO.setServiceAllowed(rs.getString("services_allowed"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				// Added for Authentication Type
				categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
				channelUserVO.setCategoryVO(categoryVO);

				channelUserVO.setParentName(rs.getString("parent_name"));
				channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
				channelUserVO.setParentCategoryName(rs.getString("parent_cat"));

				channelUserVO.setOwnerName(rs.getString("owner_name"));
				channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
				channelUserVO.setOwnerCategoryName(rs.getString("owner_cat"));

				channelUserVO.setSmsPin(rs.getString("user_sms_pin"));
				channelUserVO.setPinRequired(rs.getString("required"));
				channelUserVO.setStatusDesc(rs.getString("lookup_name"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
				channelUserVO.setRequetedByUserName(rs.getString("request_user_name"));
				if(!BTSLUtil.isNullString(rs.getString("request_user_name")))
					channelUserVO.setSuspendedByUserName(rs.getString("request_user_name"));
				else
					channelUserVO.setSuspendedByUserName(PretupsI.SYSTEM);
				channelUserVO.setCreatedByUserName(rs.getString("created_by_name"));
				channelUserVO.setAccessType(rs.getString("user_access_type"));
				channelUserVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn())));
				channelUserVO.setCategoryName(rs.getString("category_name"));
				  long balance= rs.getLong("etopup") +
				   rs.getLong("postetopup");
				  
				Double bal=(double)balance/amountMultFactor;
				channelUserVO.setBalanceStr(bal.toString());   
				channelUserVO.setBalance(balance/amountMultFactor);
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	
	public ChannelUserVO loadUsersDetailsByExtcode(Connection con, String extcode, String userID,String statusUsed, String status) throws BTSLBaseException{
		final String methodName = "loadUserDetailsByExtCode";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:extcode=");
        	loggerValue.append(extcode);
        	loggerValue.append("userID=");
        	loggerValue.append(userID);
        	loggerValue.append(",statusUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(",status=");
        	loggerValue.append(status);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
        try {
        	pstmtSelect = channelUserQry.loadUsersDetailsByExtcode(con,status,statusUsed,userID,extcode);
        	rs = pstmtSelect.executeQuery();
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setBatchID(rs.getString("batch_id"));
                channelUserVO.setCreationType(rs.getString("creation_type"));
                channelUserVO.setUserID(rs.getString("usr_user_id"));
                channelUserVO.setUserName(rs.getString("usr_user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("passwd"));
                channelUserVO.setCategoryCode(rs.getString("usr_category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("usr_status"));
                channelUserVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setMsisdn(rs.getString("usr_msisdn"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setCreatedBy(rs.getString("created_by"));
                channelUserVO.setCreatedOn(rs.getTimestamp("created_on"));
                channelUserVO.setModifiedBy(rs.getString("modified_by"));
                channelUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
                channelUserVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                channelUserVO.setPreviousStatus(rs.getString("previous_status"));
                channelUserVO.setDomainName(rs.getString("domain_name"));
                // Added for Authetication Type
                channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
                final CategoryVO categoryVO = new CategoryVO();
                categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
                categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setMaxTxnMsisdn(rs.getInt("max_txn_msisdn") + "");
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
                channelUserVO.setCategoryVO(categoryVO);
                channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
                channelUserVO.setParentCategoryName(rs.getString("parent_cat"));
                channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                channelUserVO.setOwnerCategoryName(rs.getString("owner_cat"));
                channelUserVO.setSmsPin(rs.getString("user_sms_pin"));
                channelUserVO.setPinRequired(rs.getString("required"));
                channelUserVO.setStatusDesc(rs.getString("lookup_name"));
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
                channelUserVO.setRequetedByUserName(rs.getString("request_user_name"));
                channelUserVO.setSuspendedByUserName(rs.getString("request_user_name"));
                channelUserVO.setCreatedByUserName(rs.getString("created_by_name"));
                channelUserVO.setAccessType(rs.getString("user_access_type"));
                channelUserVO.setActivatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("activated_on")));
                channelUserVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn()));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUserDetailsByExtCode]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUserDetailsByExtCode]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: channelUserVO:");
            	loggerValue.append(channelUserVO);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return channelUserVO;
        
	}
	public List<ChannelUserVO> loadApprovalUsersList(Connection p_con, String p_categoryCode, String p_lookupType,
			String p_networkCode, String p_parentGrphDomainCode, String p_status, String p_userType)
			throws BTSLBaseException {
		
		final String methodName = "loadApprovalUsersList";
		StringBuffer msg=new StringBuffer("");
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		if (LOG.isDebugEnabled()) {
	    	msg.append("Entered p_categoryCode= ");
	    	msg.append(p_categoryCode);
	    	msg.append(", p_lookupType= ");
	    	msg.append(p_lookupType);
	    	msg.append(", p_sequenceNo= ");        	
	    	msg.append(", p_networkCode= ");
	    	msg.append(p_networkCode);
	    	msg.append(", p_parentGrphDomainCode= ");
	    	msg.append(p_parentGrphDomainCode);
	    	msg.append(", p_status= ");
	    	msg.append(p_status);
	    	msg.append(", p_userType= ");
	    	msg.append(p_userType);
	    	
	    	
	        LOG.debug(methodName,msg);
	    }
		 final List list = new ArrayList<ChannelUserVO>();
		 ChannelUserVO channelUserVO = null;
		 
		 try {
			 pstmtSelect = channelUserQry.loadApprovalUsersListQry(p_con, p_categoryCode, p_lookupType, p_networkCode, p_parentGrphDomainCode, p_status, p_userType);
		 	  rs = pstmtSelect.executeQuery();
		 	  while (rs.next()) {
		 		    channelUserVO = null;
		 		    channelUserVO =  new ChannelUserVO();
					channelUserVO.setBatchID(rs.getString("batch_id"));
					channelUserVO.setCreationType(rs.getString("creation_type"));
					channelUserVO.setUserID(rs.getString("usr_user_id"));
					channelUserVO.setUserName(rs.getString("usr_user_name"));
					channelUserVO.setNetworkID(rs.getString("network_code"));
					channelUserVO.setLoginID(rs.getString("login_id"));
					channelUserVO.setPassword(rs.getString("password1"));
					channelUserVO.setCategoryCode(rs.getString("usr_category_code"));
					channelUserVO.setParentID(rs.getString("parent_id"));
					channelUserVO.setOwnerID(rs.getString("owner_id"));
					channelUserVO.setReferenceID(rs.getString("reference_id"));
					channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
					channelUserVO.setAllowedDays(rs.getString("allowed_days"));
					channelUserVO.setFromTime(rs.getString("from_time"));
					channelUserVO.setToTime(rs.getString("to_time"));
					channelUserVO.setEmpCode(rs.getString("employee_code"));
					channelUserVO.setStatus(rs.getString("usr_status"));
					channelUserVO.setEmail(rs.getString("email"));
					// Added by deepika aggarwal
					channelUserVO.setCompany(rs.getString("company"));
					channelUserVO.setFax(rs.getString("fax"));
					channelUserVO.setFirstName(rs.getString("firstname"));
					channelUserVO.setLastName(rs.getString("lastname"));
					channelUserVO.setLanguage(rs.getString("planguage") + "_" + rs.getString("pcountry"));
					// end added by deepika aggarwal
					channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
					channelUserVO.setContactNo(rs.getString("contact_no"));
					channelUserVO.setDesignation(rs.getString("designation"));
					channelUserVO.setDivisionCode(rs.getString("division"));
					channelUserVO.setDepartmentCode(rs.getString("department"));
					channelUserVO.setMsisdn(rs.getString("usr_msisdn"));
					channelUserVO.setUserType(rs.getString("user_type"));
					channelUserVO.setCreatedBy(rs.getString("created_by"));
					channelUserVO.setCreatedOn(rs.getTimestamp("created_on"));
					channelUserVO.setModifiedBy(rs.getString("modified_by"));
					channelUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
					channelUserVO.setLastModified(rs.getTimestamp("modified_on").getTime());
					channelUserVO.setAddress1(rs.getString("address1"));
					channelUserVO.setAddress2(rs.getString("address2"));
					channelUserVO.setCity(rs.getString("city"));
					channelUserVO.setState(rs.getString("state"));
					channelUserVO.setCountry(rs.getString("country"));
					channelUserVO.setRsaFlag(rs.getString("rsaflag"));
					channelUserVO.setSsn(rs.getString("ssn"));
					channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
					channelUserVO.setExternalCode(rs.getString("external_code"));
					channelUserVO.setShortName(rs.getString("short_name"));
					channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
					channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
					channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
					channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
					channelUserVO.setUserCode(rs.getString("user_code"));
					channelUserVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
					channelUserVO.setPreviousStatus(rs.getString("previous_status"));
					channelUserVO.setLongitude(rs.getString("LONGITUDE")); 
					// 5.1.3
					channelUserVO.setDomainName(rs.getString("domain_name"));
					channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));

					final CategoryVO categoryVO = new CategoryVO();
					categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
					categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
					categoryVO.setCategoryName(rs.getString("category_name"));
					categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
					categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
					categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
					categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
					categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
					categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
					categoryVO.setFixedRoles(rs.getString("fixed_roles"));
					categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
					categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
					categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
					categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
					categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
					categoryVO.setMaxTxnMsisdn(rs.getInt("max_txn_msisdn") + "");
					categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
					categoryVO.setServiceAllowed(rs.getString("services_allowed"));
					categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
					categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
					categoryVO.setCategoryType(rs.getString("category_type"));
					// Added for Authentication Type
					categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
					channelUserVO.setCategoryVO(categoryVO);

					channelUserVO.setParentName(rs.getString("parent_name"));
					channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
					channelUserVO.setParentCategoryName(rs.getString("parent_cat"));

					channelUserVO.setOwnerName(rs.getString("owner_name"));
					channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
					channelUserVO.setOwnerCategoryName(rs.getString("owner_cat"));

					channelUserVO.setSmsPin(rs.getString("user_sms_pin"));
					channelUserVO.setPinRequired(rs.getString("required"));
					channelUserVO.setStatusDesc(rs.getString("lookup_name"));
					channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
					channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
					channelUserVO.setRequetedByUserName(rs.getString("request_user_name"));
					if(!BTSLUtil.isNullString(rs.getString("request_user_name")))
						channelUserVO.setSuspendedByUserName(rs.getString("request_user_name"));
					else
						channelUserVO.setSuspendedByUserName(PretupsI.SYSTEM);
					channelUserVO.setCreatedByUserName(rs.getString("created_by_name"));
					channelUserVO.setAccessType(rs.getString("user_access_type"));
					channelUserVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn())));
					channelUserVO.setCategoryName(rs.getString("category_name"));
					  long balance= rs.getLong("etopup") +
					   rs.getLong("postetopup");
					channelUserVO.setBalance(balance/amountMultFactor);
					Double bal=(double)balance/amountMultFactor;
					channelUserVO.setBalanceStr(bal.toString());
					list.add(channelUserVO);
		 	  }
		 } catch (SQLException sqe) {
			   msg.setLength(0);
			   msg.append(SQL_EXCEPTION);
			   msg.append(sqe.getMessage());
				LOG.error(methodName, msg);
				LOG.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
						"SQL Exception:" + sqe.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			} catch (Exception ex) {
				msg.setLength(0);
				msg.append(EXCEPTION);
				msg.append(ex.getMessage());
				LOG.error(methodName, msg);
				LOG.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
						"Exception:" + ex.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			} finally {
				try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (SQLException e) {
	                LOG.errorTrace(methodName, e);
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }

	            try {
	                if (pstmtSelect != null) {
	                    pstmtSelect.close();
	                }
	            } catch (SQLException e) {
	                LOG.errorTrace(methodName, e);
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
				if(LOG.isDebugEnabled()){
					msg.setLength(0);
					msg.append("Exiting channelUserVO:");
					msg.append(list);
					LOG.debug(methodName, msg);
				}
		    return list;		
	}
		 
  }
	
	
	/**
	 * Method loadStaffUserDetails. This method load user information by 
	 * loginId
	 * 
	 * @param con
	 *            Connection
	 * @param msisdn
	 *            String
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadStaffUserDetailsByLoginId(Connection con, String loginId) throws BTSLBaseException {

		final String methodName = "loadChannelUserDetailsByLoginId";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: loginId =");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtSelect = null;
		ChannelUserVO channelUserVO = null;
		final Date curDate = new Date();
		ResultSet rs1 = null;
		ResultSet rs = null;
		boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
		boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		try {
			final String select = new StringBuilder("select user_id from users where login_id=? and user_type='STAFF' AND status <> ? AND status <> ? ").toString();
			
			int i =1;
			String selectQuery = null;
			
			String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
			boolean tcpOn = false;
			Set<String> uniqueTransProfileId = new HashSet();

			if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
				tcpOn = true;
			}
			String sqlSelect = null;

			if (tcpOn) {
				selectQuery = channelUserQry.loadChannelUserDetailsQryLoginIDTcp();
			} else {
				selectQuery = channelUserQry.loadChannelUserDetailsQryLoginID();
			}
			
			
			try(PreparedStatement pstmtSelect1 = con.prepareStatement(select);)
			{
			pstmtSelect1.setString(i++, loginId);
			pstmtSelect1.setString(i++, PretupsI.USER_STATUS_DELETED);
			pstmtSelect1.setString(i++, PretupsI.USER_STATUS_CANCELED);
			rs1 = pstmtSelect1.executeQuery();
			String user_id=null; 
			if(rs1.next()){
				user_id=rs1.getString(1);
			}
			i=1;
			pstmtSelect = con.prepareStatement(selectQuery);
			pstmtSelect.setString(i++, user_id);
			pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(i++, PretupsI.USER_STATUS_CANCELED);
			pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(curDate));
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("Before Result =");
	        	loggerValue.append(loginId);
	            LOG.debug(methodName, loggerValue);
			}
			rs = pstmtSelect.executeQuery();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
	        	loggerValue.append("After Result =");
	        	loggerValue.append(loginId);
	            LOG.debug(methodName, loggerValue);
			}
			if (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserID(rs.getString("user_id"));
				channelUserVO.setUserName(rs.getString("user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("webpassword"));
				channelUserVO.setCategoryCode(rs.getString("category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setMsisdn(rs.getString("msisdn"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("userstatus"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
				channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));

				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));

					if (tcpOn) {

						SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.EQUALS,
								new HashSet<String>(Arrays.asList(rs.getString("transfer_profile_id"))),
								ValueType.STRING);
						java.util.List<HashMap<String, String>> resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE",
								new HashSet<String>(Arrays.asList("profile_id","profile_name", "status")), searchCriteria);

						
						channelUserVO.setTransferProfileStatus(resultSet.get(0).get("status"));
					} else {
						channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
					}
				channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setDualCommissionType(rs.getString("last_dual_comm_type"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
				channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));

				// for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setAccessType(rs.getString("access_type"));
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				// end Zebra and Tango
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				// added by deepika aggarwal
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				// added by Praveen for autoc2cweb
				channelUserVO.setAutoc2callowed(rs.getString("auto_c2c_allow"));
				channelUserVO.setAutoc2cquantity(rs.getString("auto_c2c_quantity"));
				if(channelSosEnable){
					channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
					channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
					channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
				}
				if(lrEnabled){
					channelUserVO.setLrAllowed(rs.getString("lr_allowed"));
					channelUserVO.setLrMaxAmount(rs.getLong("lr_max_amount"));
				}
				//added for owner commission
				channelUserVO.setOwnerCategoryName(rs.getString("own_category_code"));
				channelUserVO.setOwnerMsisdn(rs.getString("own_msisdn"));
				// end added by deepika aggarwal
				channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
				channelUserVO.setSosAllowedAmount(rs.getInt("sos_allowed_amount"));
				channelUserVO.setSosThresholdLimit(rs.getInt("sos_threshold_limit"));
				channelUserVO.setGeographicalDesc(rs.getString("GRPH_DOMAIN_NAME"));
				channelUserVO.setEmail(rs.getString("email"));

				final Date passwordModifiedDate = rs.getTimestamp("pswd_modified_on");
				setPasswordModifiedDate(channelUserVO, passwordModifiedDate);

				final CategoryVO categoryVO = new CategoryVO();
				categoryVO.setCategoryCode(rs.getString("category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("catseq"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setUserIdPrefix(rs.getString("USER_ID_PREFIX"));

				channelUserVO.setCategoryVO(categoryVO);

				final UserPhoneVO userPhoneVO = new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
				//userPhoneVO.setMsisdn(msisdn);
				userPhoneVO.setUserId(rs.getString("user_id"));
				userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
				userPhoneVO.setSmsPin(rs.getString("sms_pin"));
				userPhoneVO.setPinRequired(rs.getString("pin_required"));
				userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
				userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
				userPhoneVO.setCountry(rs.getString("phcountry"));
				channelUserVO.setLanguage(rs.getString("phlang") + "_" + rs.getString("phcountry"));// added
				// by
				// deepika
				// aggarwal
				userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
				userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
				userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
				userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
				userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
				userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
				userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("userphone_created_on")));
				userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				userPhoneVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
				userPhoneVO.setModifiedBy(rs.getString("modified_by"));
				userPhoneVO.setMsisdn(rs.getString("msisdn"));//priyank
				channelUserVO.setUserPhoneVO(userPhoneVO);
				// added for user level transfer rule
				if (isTrfRuleUserLevelAllow) {
					channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
				}
				if (lmsAppl) {
					channelUserVO.setLmsProfile(rs.getString("lms_profile"));
				}
				if (optInOutAllow) {
					channelUserVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				}
				channelUserVO.setLongitude(rs.getString("LONGITUDE"));   
				channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
				channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(con, channelUserVO.getUserID()));
				channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setOwnerCompany(rs.getString("owner_company"));
                channelUserVO.setCommissionProfileApplicableFrom(rs.getDate("applicable_from"));
                channelUserVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
			}
			return channelUserVO;
		}
		}catch (SQLException sqle) {
			loggerValue.setLength(0);
        	loggerValue.append(SQL_EXCEPTION);
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);			
            LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
					"", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
	}
	
	/**
	 * Method loadUserDetailsByExtCode. This method is used to load all the
	 * information of the user on the basis of extCode
	 * 
	 * @author mohit.goel
	 * @param con
	 *            Connection
	 * @param extCode
	 *            String
	 * @param userId
	 *            String(If operator user userId = null else userId = session
	 *            user id(in case of channel user)
	 * @param statusUsed
	 *            String
	 * @param status
	 *            String
	 * 
	 * 
	 * @return ChannelUserVO
	 * @throws BTSLBaseException
	 */
	public ChannelUserVO loadUserDetailsByExtCode(Connection con, String extCode, String userId, String statusUsed, String status) throws BTSLBaseException {
		final String methodName = "loadUserDetailsByExtCode";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: extCode =");
        	loggerValue.append(extCode);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
        	loggerValue.append(" stausUsed=");
        	loggerValue.append(statusUsed);
        	loggerValue.append(" staus=");
        	loggerValue.append(status);
            LOG.debug(methodName, loggerValue);
        }
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmt1 = null;
		boolean isMsisdnAssociationReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ);
		ChannelUserVO channelUserVO = null;

		StringBuilder strBuffer = new StringBuilder("select user_id,user_name,msisdn,u.category_code,cat.category_name from users u,categories cat ");
		strBuffer.append("where user_id in (?,?,?,?) and u.category_code=cat.category_code");
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(strBuffer.toString());
			LOG.debug(methodName, loggerValue);
		}

		try {
			pstmtSelect = channelUserQry.loadUsersDetailsByExtCodeQry(con, status, userId, statusUsed, extCode);
			try (ResultSet rs = pstmtSelect.executeQuery();)
			{
			if (rs.next()) {
				channelUserVO = ChannelUserVO.getInstance();
				channelUserVO.setBatchID(rs.getString("batch_id"));
				channelUserVO.setCreationType(rs.getString("creation_type"));
				channelUserVO.setUserID(rs.getString("usr_user_id"));
				channelUserVO.setUserName(rs.getString("usr_user_name"));
				channelUserVO.setNetworkID(rs.getString("network_code"));
				channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("password1"));
				channelUserVO.setCategoryCode(rs.getString("usr_category_code"));
				channelUserVO.setParentID(rs.getString("parent_id"));
				channelUserVO.setOwnerID(rs.getString("owner_id"));
				channelUserVO.setReferenceID(rs.getString("reference_id"));
				channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setEmpCode(rs.getString("employee_code"));
				channelUserVO.setStatus(rs.getString("usr_status"));
				channelUserVO.setEmail(rs.getString("email"));
				// Added by deepika aggarwal
				channelUserVO.setCompany(rs.getString("company"));
				channelUserVO.setFax(rs.getString("fax"));
				channelUserVO.setFirstName(rs.getString("firstname"));
				channelUserVO.setLastName(rs.getString("lastname"));
				channelUserVO.setLanguage(rs.getString("phone_language") + "_" + rs.getString("phcountry"));
				// end added by deepika aggarwal

				channelUserVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
				channelUserVO.setContactNo(rs.getString("contact_no"));
				channelUserVO.setDesignation(rs.getString("designation"));
				channelUserVO.setDivisionCode(rs.getString("division"));
				channelUserVO.setDepartmentCode(rs.getString("department"));
				channelUserVO.setMsisdn(rs.getString("usr_msisdn"));
				channelUserVO.setUserType(rs.getString("user_type"));
				channelUserVO.setCreatedBy(rs.getString("created_by"));
				channelUserVO.setCreatedOn(rs.getTimestamp("created_on"));
				channelUserVO.setModifiedBy(rs.getString("modified_by"));
				channelUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
				channelUserVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
				channelUserVO.setAddress1(rs.getString("address1"));
				channelUserVO.setAddress2(rs.getString("address2"));
				channelUserVO.setCity(rs.getString("city"));
				channelUserVO.setState(rs.getString("state"));
				channelUserVO.setCountry(rs.getString("country"));
				channelUserVO.setRsaFlag(rs.getString("rsaflag"));
				channelUserVO.setSsn(rs.getString("ssn"));
				channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
				channelUserVO.setExternalCode(rs.getString("external_code"));
				channelUserVO.setShortName(rs.getString("short_name"));
				channelUserVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
				channelUserVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
				channelUserVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
				channelUserVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
				channelUserVO.setUserCode(rs.getString("user_code"));
				channelUserVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
				channelUserVO.setPreviousStatus(rs.getString("previous_status"));
				// Added for Authetication Type
				channelUserVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
				channelUserVO.setDomainName(rs.getString("domain_name"));
				final CategoryVO categoryVO = CategoryVO.getInstance();
				categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
				categoryVO.setCategoryName(rs.getString("category_name"));
				categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
				categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
				categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
				categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
				categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
				categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
				categoryVO.setFixedRoles(rs.getString("fixed_roles"));
				categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
				categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
				categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setMaxTxnMsisdn(rs.getInt("max_txn_msisdn") + "");
				categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
				categoryVO.setServiceAllowed(rs.getString("services_allowed"));
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setCategoryType(rs.getString("category_type"));
				categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				// Added For authentication Type
				categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
				channelUserVO.setCategoryVO(categoryVO);

				pstmt1 = con.prepareStatement(strBuffer.toString());
				pstmt1.setString(1, channelUserVO.getParentID());
				pstmt1.setString(2, channelUserVO.getOwnerID());
				pstmt1.setString(3, channelUserVO.getCreatedBy());
				pstmt1.setString(4, channelUserVO.getModifiedBy());
				try(ResultSet rs1 = pstmt1.executeQuery();)
				{
				while(rs1.next()) {
					String uid = rs1.getString("user_id");
					if(PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentID())){
						channelUserVO.setParentName(PretupsI.ROOT_PARENT_ID);
					}
					if(PretupsI.SYSTEM.equals(channelUserVO.getParentID()))
						channelUserVO.setParentName(PretupsI.SYSTEM);
					if(channelUserVO.getParentID().equals(uid)){
						channelUserVO.setParentName(rs1.getString("user_name"));
						channelUserVO.setParentMsisdn(rs1.getString("msisdn"));	
						channelUserVO.setParentCategoryName(rs1.getString("category_name"));					   
					}						
					if(channelUserVO.getOwnerID().equals(uid)) {
						channelUserVO.setOwnerName(rs1.getString("user_name"));
						channelUserVO.setOwnerMsisdn(rs1.getString("msisdn"));
						channelUserVO.setOwnerCategoryName(rs1.getString("category_name"));
					}
					if(channelUserVO.getCreatedBy().equals(uid)) {
						channelUserVO.setCreatedByUserName(rs1.getString("user_name"));  
					}
					if(channelUserVO.getModifiedBy().equals(uid)) {
						channelUserVO.setRequetedByUserName(rs1.getString("user_name"));
						channelUserVO.setSuspendedByUserName(rs1.getString("user_name"));
					}
					else if(channelUserVO.getModifiedBy().equals(PretupsI.SYSTEM)) {
						channelUserVO.setSuspendedByUserName(PretupsI.SYSTEM);
					}

				}
				channelUserVO.setStatusDesc(rs.getString("lookup_name"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));


				channelUserVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn())));
				if (isMsisdnAssociationReq) {
					channelUserVO.setAssoMsisdn(rs.getString("ASSOCIATED_MSISDN"));
					channelUserVO.setAssType(rs.getString("ASSOCIATED_MSISDN_TYPE"));
					channelUserVO.setAssociationCreatedOn(rs.getTimestamp("ASSOCIATED_MSISDN_CDATE"));
					channelUserVO.setAssociationModifiedOn(rs.getTimestamp("ASSOCIATED_MSISDN_MDATE"));
				}
			}
		} 
		}
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsByLoginId]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsByLoginId]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
		
			try {
                if (pstmt1 != null) {
                	pstmt1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmtSelect != null) {
                	pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting channelUserVO:");
				loggerValue.append(channelUserVO);
				LOG.debug(methodName, loggerValue);
			}
		}
		return channelUserVO;
	}

	/**
     * Method validateUsersForBatch.
     * This method the loads the user list for Batch transfer
     * @param p_con Connection
     * @param p_batchFOCItemsVOList ArrayList
     * @param p_domainCode String
     * @param p_categoryCode String
     * @param p_networkCode String
     * @param p_geographicalDomainCode String
     * @param p_comPrfApplicableDate Date
     * @param p_messages MessageResources
	 * @param p_locale Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    
    public ArrayList validateUsersForBatchC2C(Connection p_con,ArrayList p_batchC2CItemsVOList ,String p_domainCode,String p_categoryCode,String p_networkCode,Date p_comPrfApplicableDate,MessageResources p_messages,Locale p_locale) throws BTSLBaseException
    { 
    	final String method_name = "validateUsersForBatchC2C";
        if (LOG.isDebugEnabled())
            LOG.debug(method_name, "Entered p_batchC2CItemsVOList.size()="+p_batchC2CItemsVOList.size()+"Category Code "+p_categoryCode+" Network Code "+p_networkCode+", p_comPrfApplicableDate="+p_comPrfApplicableDate);
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sqlSelect = channelUserQry.validateUsersForBatchC2CQry(p_categoryCode);
        if (LOG.isDebugEnabled())
            LOG.debug(method_name, "QUERY sqlSelect=" + sqlSelect);
		ArrayList errorList = new ArrayList();
        try
        {
        	//commented for DB2 pstmt = (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
        	pstmt = (PreparedStatement)p_con.prepareStatement(sqlSelect);
        	int index = 0 ;
			C2CBatchItemsVO c2cBatchItemVO =null;
			ListValueVO errorVO=null;
			boolean fileValidationErrorExists=false;
			String msisdnOrLoginID=null;
			for(int i=0,j=p_batchC2CItemsVOList.size();i<j;i++)
			{
				msisdnOrLoginID=null;
				c2cBatchItemVO=(C2CBatchItemsVO)p_batchC2CItemsVOList.get(i);
				index=0;
				if(c2cBatchItemVO.getLoginID()!=null)
				{
					pstmt.setString(++index,c2cBatchItemVO.getLoginID());
					msisdnOrLoginID=c2cBatchItemVO.getLoginID();
				}
				else {
					pstmt.setString(++index,"X");					
				}
				
				if(c2cBatchItemVO.getMsisdn()!=null)
					pstmt.setString(++index,c2cBatchItemVO.getMsisdn());
				else
					++index;
	            
				if(msisdnOrLoginID=="" || msisdnOrLoginID==null)
					msisdnOrLoginID=c2cBatchItemVO.getMsisdn();
				
				pstmt.setString(++index,p_networkCode);
	            //pstmt.setString(++index,p_domainCode);
				pstmt.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
				LOG.debug(method_name, "QUERY sqlSelect=" +c2cBatchItemVO.getMsisdn() +", p_networkCode="+ p_networkCode +",p_domainCode="+ p_domainCode);
				rs = pstmt.executeQuery();
				pstmt.clearParameters();
	            if (rs.next())
	            {
	            	if(!BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn())&&!BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()))
	            	{
		            	if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id"))|| !(c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn")))||!(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code"))))
		            	{
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.invalidlgidormsisdnorextCode"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Valid Msisdn, Login ID  and External Code are required","Batch C2C Initiate");
	    					continue;
		            	}
	            	}
	            	
	            	else if(BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn())&& !BTSLUtil.isNullString(c2cBatchItemVO.getLoginID())&& !BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()))
	            	{
	            		if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id"))|| !(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code"))))
		            	{
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.invalidloginidorextCode"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Valid  Login ID and External Code are required","Batch C2C Initiate");
	    					continue;
		            	}
	            	
	            	}
	            	
	            	else if(BTSLUtil.isNullString(c2cBatchItemVO.getLoginID())&& !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn())&& !BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()))
	            	{
	            		if (!c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn"))|| !(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code"))))
		            	{
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.invalidmsisdnorextCode"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Valid Msisdn ard External Code are required","Batch C2C Initiate");
	    					continue;
		            	}
	            	
	            	}
	            	
	            	else if(BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode())&& !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn())&& !BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()))
	            	{
	            		if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id"))|| !(c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn"))))
		            	{
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.invalidmsisdnorloginid"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Valid  Login ID ard Msisdn Code are required","Batch C2C Initiate");
	    					continue;
		            	}
	            	
	            	}
	            	
	            	else if(BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn())&& BTSLUtil.isNullString(c2cBatchItemVO.getLoginID())&& !BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()))
	            	{
	            		if ( !(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code"))))
		            	{
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.invalidexternalcode"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Valid  External Code is required","Batch C2C Initiate");
	    					continue;
		            	}
	            	
	            	}
	            	
	            	else if(BTSLUtil.isNullString(c2cBatchItemVO.getLoginID())&& BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode())&& !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()))
	            	{
	            		if (!c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn")))
		            	{
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.invalidmsisdn"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Valid Msisdn is required","Batch C2C Initiate");
	    					continue;
		            	}
	            	
	            	}
	            	
	            	else if(BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode())&& BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn())&& !BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()))
	            	{
	            		if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id")))
		            	{
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.invalidloginid"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Valid  Login ID  is required","Batch C2C Initiate");
	    					continue;
		            	}
	            	
	            	}
	            	
	            	else if(BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn())&& BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()))
	            	{
		            	
		            	
		            		errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.nomsisdnloginidextcode"));
	    					errorList.add(errorVO);
	    					fileValidationErrorExists=true;
	    					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL :  Msisdn, Login ID and External Code are required","Batch C2C Initiate");
	    					continue;
		            	
	            	}
	            	
	            	
	            	
	            	
	            	
				if(!PretupsI.YES.equals(rs.getString("status")))
				{
					//put error user is not active
				    errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.usernotactive"));
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : User is not active","Batch C2C Initiate");
					continue;
				}
				if(!PretupsI.NO.equals(rs.getString("in_suspend")))
				{
					//put error user is in suspended
				    errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.userinsuspend"));
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : User is IN suspended","Batch C2C Initiate");
					continue;
				}
				
				if(!PretupsI.YES.equals(rs.getString("profile_status")))
				{
					//put transfer profile is not active
				    errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.trfprfsuspended"));
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Transfer profile is suspended","Batch C2C Initiate");
					continue;
				}
				if(!PretupsI.YES.equals(rs.getString("commprofilestatus")))
				{
					//put commission profile is not active
					// with reason
				    errorVO=new ListValueVO();
					errorVO.setCodeName(msisdnOrLoginID);
					errorVO.setOtherInfo(String.valueOf(c2cBatchItemVO.getRecordNumber()));
					//ChangeID=LOCALEMASTER
					//which language message to be set is determined from the locale master table for the requested locale
					if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_locale)).getMessage()))								
					{
						// reason is rs.getString("comprf_lang_1_msg")
					    errorVO.setOtherInfo2(p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.comprfinactive",new String[]{rs.getString("comprf_lang_1_msg")}));
					}
					else
					{
						//reason is rs.getString("comprf_lang_2_msg")
					    errorVO.setOtherInfo2(p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.comprfinactive",new String[]{rs.getString("comprf_lang_2_msg")}));
					}
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Commision profile is inactive","Batch C2C Initiate");
					continue;
				}
				if(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate))
				{
					// no commission profile is associated till today.
				    errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.nocomprfassociated"));
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : No commission profile is associated till today","Batch C2C Initiate");
					continue;
				}
				if(!fileValidationErrorExists)
					{
					c2cBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
   					c2cBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
   					c2cBatchItemVO.setOthCommSetId(rs.getString("OTH_COMM_PRF_SET_ID"));   					
   					c2cBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
   					c2cBatchItemVO.setCategoryCode(rs.getString("category_code"));
   					c2cBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
   					c2cBatchItemVO.setUserId(rs.getString("user_id"));
					}
				}
	            else if(c2cBatchItemVO.getLoginID()!="" && c2cBatchItemVO.getMsisdn()!="" )
				{
	            	errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.msisdnandloginidnotfound"));
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Msisdn and Login ID detail not found","Batch C2C Initiate");
					continue;
				}
	            else if(c2cBatchItemVO.getLoginID()!="")
				{
	            	errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.loginidnotfound"));
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Login ID detail not found","Batch C2C Initiate");
					continue;
				}
	            else
	            {
	            	errorVO=new ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.processuploadedfile.error.msisdnnotfound"));
					errorList.add(errorVO);
					fileValidationErrorExists=true;
					BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",c2cBatchItemVO,"FAIL : Msisdn not datail found","Batch C2C Initiate");
					continue;
				}
			}
        }
		catch (SQLException sqe)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[validateUsersForBatchC2C]","","","","SQL Exception:"+sqe.getMessage());
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",null,"FAIL : SQL Exception:"+sqe.getMessage(),"Batch C2C Initiate");
			throw new BTSLBaseException(this, method_name, "error.general.sql.processing");
        } 
		catch (Exception ex)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[validateUsersForBatchC2C]","","","","Exception:"+ex.getMessage());
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",null,"FAIL : Exception:"+ex.getMessage(),"Batch C2C Initiate");
			throw new BTSLBaseException(this, method_name, "error.general.processing");
        }
		finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile",null,"FINALLY BLOCK TOTAL RECORDS ="+p_batchC2CItemsVOList.size()+", ERROR RECORDS = "+errorList.size(),"Batch FOC Initiate");
			if (LOG.isDebugEnabled())
                LOG.debug(method_name, "Exiting:  errorList Size =" + errorList.size());
        }
        return errorList;
    }
	
	/**New Method 
     * Method loadChannelUserDetailsByLoginIDANDORMSISDN()
     * This method loads the channel users based on login_id and/or msisdn 
     * @author harsh dixit
     * @param p_con
     * @param p_msisdn String
     * @param p_loginid String
     * @return channelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadChannelUserDetailsByLoginIDANDORMSISDN(Connection p_con, String p_msisdn, String p_loginid) throws BTSLBaseException
    {
    	final String methodName = "loadChannelUserDetailsByLoginIDANDORMSISDN";
        if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "Entered p_msisdn:" + p_msisdn ,"Entered p_loginid:" + p_loginid);
        PreparedStatement pstmtSelect = null;
        ChannelUserVO channelUserVO = null;
        ResultSet rs = null;
        try
        {
            String selectQuery = channelUserQry.loadChannelUserDetailsByLoginIDANDORMSISDNQry(p_msisdn,p_loginid);
            if (LOG.isDebugEnabled()) 	LOG.debug("loadChannelUserDetails", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.USER_STATUS_DELETED);
			pstmtSelect.setString(2,PretupsI.USER_STATUS_CANCELED);
			//modifed by harsh
			if(!BTSLUtil.isNullString(p_msisdn) && !BTSLUtil.isNullString(p_loginid))
			{
			pstmtSelect.setString(3, p_msisdn);
			pstmtSelect.setString(4, p_loginid);
			}
			
			else if(!BTSLUtil.isNullString(p_msisdn) )
				pstmtSelect.setString(3, p_msisdn);
			
			else if(!BTSLUtil.isNullString(p_loginid) )
				 pstmtSelect.setString(3, p_loginid);
			//end modified by
			if (LOG.isDebugEnabled())
                LOG.debug(methodName, "Before result:" + p_msisdn);
            rs = pstmtSelect.executeQuery();
			if (LOG.isDebugEnabled())
                LOG.debug(methodName, "After result:" + p_msisdn);
            if (rs.next())
            {
                channelUserVO = new ChannelUserVO();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setLoginID(rs.getString("login_id"));
				channelUserVO.setPassword(rs.getString("webpassword"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setCreatedBy(rs.getString("created_by"));
                channelUserVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(rs.getString("modified_by"));
                channelUserVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
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
                channelUserVO.setDomainID(rs.getString("domain_code"));
				channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
				channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));
				
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
				channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
				channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
				channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
				
				//for Zebra and Tango by sanjeew date 06/07/07
				channelUserVO.setAccessType(rs.getString("access_type"));
				channelUserVO.setApplicationID(rs.getString("application_id"));
				channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
				channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
				channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
				channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
				channelUserVO.setFromTime(rs.getString("from_time"));
				channelUserVO.setToTime(rs.getString("to_time"));
				channelUserVO.setAllowedDays(rs.getString("allowed_days"));
				//end Zebra and Tango
				channelUserVO.setPinReset(rs.getString("PIN_RESET"));
				channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
				try{
					channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
				}catch(Exception e){
					channelUserVO.setPasswordModifiedOn(null);
				}
				
                CategoryVO categoryVO =new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("catseq"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
				categoryVO.setAllowedGatewayTypes(new CategoryDAO().loadMessageGatewayTypeListForCategory(p_con,categoryVO.getCategoryCode()));
				categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
				categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
				categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
				categoryVO.setGrphDomainSequenceNo(rs.getInt("grphSeq")); 
				categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
				categoryVO.setUserIdPrefix(rs.getString("USER_ID_PREFIX"));
				
				channelUserVO.setCategoryVO(categoryVO);
                
                UserPhoneVO userPhoneVO=new UserPhoneVO();
				userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
                userPhoneVO.setMsisdn(p_msisdn);
                userPhoneVO.setUserId(rs.getString("user_id"));
                userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
                userPhoneVO.setSmsPin(rs.getString("sms_pin"));
                userPhoneVO.setPinRequired(rs.getString("pin_required"));
                userPhoneVO.setPhoneProfile(rs.getString("phone_profile"));
                userPhoneVO.setPhoneLanguage(rs.getString("phlang"));
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                userPhoneVO.setLastTransactionStatus(rs.getString("last_transaction_status"));
                userPhoneVO.setLastTransactionOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_transaction_on")));
				userPhoneVO.setPinModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("pin_modified_on")));
                userPhoneVO.setLastTransferID(rs.getString("last_transfer_id"));
                userPhoneVO.setLastTransferType(rs.getString("last_transfer_type"));
                userPhoneVO.setPrefixID(rs.getLong("prefix_id"));
                userPhoneVO.setTempTransferID(rs.getString("temp_transfer_id"));
				userPhoneVO.setFirstInvalidPinTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_invalid_pin_time")));
				userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("userphone_created_on")));
                userPhoneVO.setLastAccessOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_access_on")));
				channelUserVO.setUserPhoneVO(userPhoneVO);
                
                channelUserVO.setAssociatedServiceTypeList(loadUserServicesList(p_con,channelUserVO.getUserID()));
            }
            return channelUserVO;
        }// end of try
        catch (SQLException sqle)
        {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelChannelUserDAO[loadChannelUserDetails]","","","","SQL Exception:"+sqle.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetailsByLoginIDANDORMSISDN", "error.general.sql.processing");
        }// end of catch
        catch (Exception e)
        {
            LOG.error(methodName, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelChannelUserDAO[loadChannelUserDetails]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetailsByLoginIDANDORMSISDN", "error.general.processing");
        }// end of catch
        finally
        {
            try{if (rs != null)rs.close();} catch (Exception e){}
            try{if (pstmtSelect != null)pstmtSelect.close();} catch (Exception e){}
            if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "Exiting channelUserVO:" + channelUserVO);
        }// end of finally
    
	}
    
    public ArrayList loadUsersOutsideHireacrhyForLoginID(Connection con, String networkCode, String toCategoryCode, String loginID, String userId, String p_txnType) throws BTSLBaseException {

		final String methodName = "loadUsersOutsideHireacrhyForLoginID";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode =");
        	loggerValue.append(networkCode);
        	loggerValue.append(" To Category Code =");
        	loggerValue.append(toCategoryCode);
        	loggerValue.append(" Login ID =");
        	loggerValue.append(loginID);
        	loggerValue.append(" userId=");
        	loggerValue.append(userId);
            LOG.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt = null
		 
		
		String statusAllowed = null;
		// user life cycle
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
				PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(p_txnType)) {
				statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
			} else {
				statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
			}
		} else {
			throw new BTSLBaseException(this, methodName, "error.status.processing");
		}
		final StringBuilder strBuff = new StringBuilder(" SELECT u.user_id, u.login_id,u.msisdn,u.user_name ");
		strBuff.append(" FROM users u ");
		strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND category_code = ? AND user_id != ?");
		strBuff.append(" AND u.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		// here user_id != ? check is for not to load the sender user in the
		// query for the same level transactions
		strBuff.append(" AND UPPER(u.login_id) LIKE UPPER(?) ORDER BY u.login_id ");
		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		final ArrayList arrayList = new ArrayList();
		try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

			
			int i = 0;
			i++;
			pstmt.setString(i, networkCode);
			i++;
			pstmt.setString(i, toCategoryCode);
			i++;
			pstmt.setString(i, userId);
			i++;
			pstmt.setString(i, loginID);
			try(ResultSet rs = pstmt.executeQuery();)
			{
			while (rs.next()) {
				AutoCompleteUserDetailsResponseVO autoCompleteUserDetailsResponseVO = new AutoCompleteUserDetailsResponseVO();
				autoCompleteUserDetailsResponseVO.setLoginId(rs.getString("LOGIN_ID"));
				autoCompleteUserDetailsResponseVO.setUserName(rs.getString("USER_NAME"));
				autoCompleteUserDetailsResponseVO.setMsisdn(rs.getString("MSISDN"));
				autoCompleteUserDetailsResponseVO.setUserID(rs.getString("USER_ID"));
				arrayList.add(autoCompleteUserDetailsResponseVO);
			}
		} 
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersOutsideHireacrhy]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersOutsideHireacrhy]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return arrayList;
	}
    
    
    
    
    public String loadUserTypeByLoginID(Connection con, String loginId) throws BTSLBaseException {
		final String methodName = "loadUserTypeByLoginID";
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		String userType=null;
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: loginId =");
        	loggerValue.append(BTSLUtil.maskParam(loginId));
            LOG.debug(methodName, loggerValue);
        }

		
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT user_id,user_type,user_name FROM users  ");
		strBuff.append(" WHERE login_id = ?");

		final String sqlSelect = strBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
		final ArrayList arrayList = new ArrayList();
		try {
			// commented for DB2 pstmt = (OraclePreparedStatement)
			// con.prepareStatement(sqlSelect)
			pstmt = con.prepareStatement(sqlSelect);
			int i = 0;
			pstmt.setString(++i, loginId);
			
			rs = pstmt.executeQuery();
			
//			while (rs.next()) {
//				arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id"), rs.getString("user_type")));
//			}
			
			while (rs.next()) {
				userType=rs.getString("user_type");
			}

		
		}catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByDomainID]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByDomainID]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting arrayList Size:");
				loggerValue.append(arrayList.size());
				LOG.debug(methodName, loggerValue);
			}
		}
		return userType;
	}


	public UserVO loadUserEventDetails(Connection pCon, UserVO pChannelUserVO,String pEventType) throws BTSLBaseException {
		
	final String methodName = "loadUserEventDetails";
	if (LOG.isDebugEnabled()) {
		LOG.debug(methodName, "Entered ChannelUserVO : "+pChannelUserVO.toString());
	}
	UserBalancesVO userBalanceVO = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	final StringBuilder sqlBuff = new StringBuilder();
	sqlBuff.append("select u.user_name created_by,uer.created_on from user_event_remarks uer ,users u where uer.created_by=u.user_id ");
	sqlBuff.append("and uer.user_id=? and uer.event_type=? order by uer.created_on desc");
	
	String sqlSelect = 	sqlBuff.toString();	
	if (LOG.isDebugEnabled()) {
		LOG.debug(methodName, "QUERY sql Select = " + sqlSelect);
	}
	try {
		pstmt = pCon.prepareStatement(sqlSelect);
		pstmt.setString(1, pChannelUserVO.getUserID());
		pstmt.setString(2, pEventType);

		rs = pstmt.executeQuery();
		
		if (rs.next()) {
			pChannelUserVO.setSuspendedByUserName(rs.getString("created_by"));
			pChannelUserVO.setSuspendedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
			
		}
	} catch (SQLException sqe) {
		LOG.error(methodName, "SQLException : " + sqe);
		LOG.errorTrace(methodName, sqe);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserAgentsBalance]", "", "", "",
				"SQL Exception:" + sqe.getMessage());
		throw new BTSLBaseException(this, "", "error.general.sql.processing");
	} catch (Exception ex) {
		LOG.error("", "Exception : " + ex);
		LOG.errorTrace(methodName, ex);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserAgentsBalance]", "", "", "",
				"Exception:" + ex.getMessage());
		throw new BTSLBaseException(this, "", "error.general.processing");
	} finally {
        try{if (rs != null)rs.close();} catch (Exception e){}
        try{if (pstmt != null)pstmt.close();} catch (Exception e){}
		if (LOG.isDebugEnabled()) {
			LOG.debug("", "Exiting: ");
		}
	}
	
	return pChannelUserVO;
}
	
    
    
    
    /**
     * this method used to Change the user pin to temp/default pin based on msisdn and user_id
     * 
     * @param p_con
     * @param p_smsPin
     *            String
     * @param p_channelUserVO
     *            ChannelUserVO
     * @return int
     * @throws BTSLBaseException
     * @author manoj kumar
     */
    public int resetPin(Connection p_con, String p_smsPin, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "resetPin";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_smsPin=" + p_smsPin + ", subscriberVO : =" + p_channelUserVO);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuilder strBuff = new StringBuilder(" UPDATE user_phones set sms_pin = ?,  ");
            strBuff.append("  modified_by = ? , modified_on = ?, pin_modified_on=?, PIN_RESET=?,last_access_on=? WHERE msisdn = ? AND user_id =? ");
            final String query = strBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "select query:" + query);
            }
            int i=1;
            psmt = p_con.prepareStatement(query);
            psmt.setString(i++, BTSLUtil.encryptText(p_smsPin));
            psmt.setString(i++, p_channelUserVO.getModifiedBy());
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_channelUserVO.getModifiedOn()));
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_channelUserVO.getModifiedOn()));
            psmt.setString(i++, PretupsI.YES);
            psmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_channelUserVO.getUserPhoneVO().getLastAccessOn()));
            psmt.setString(i++, p_channelUserVO.getUserPhoneVO().getMsisdn());
            psmt.setString(i++, p_channelUserVO.getUserPhoneVO().getUserId());
            updateCount = psmt.executeUpdate();
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changePin]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changePin]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
        	OracleUtil.closeQuietly(psmt);
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

	public static String formatStatus(String status) {
		status = status.trim();
		String[] statuses = status.split(",");
		StringBuilder result = new StringBuilder();
		for (String s : statuses) {
			s = s.replaceAll("^'+|'+$", "").trim();
			String[] subStatuses = s.split("(?<=\\S)'(?=\\S)");
			for (String subStatus : subStatuses) {
				subStatus = subStatus.replaceAll("^'+|'+$", "").trim();
				if (!subStatus.isEmpty()) {
					if (result.length() > 0) {
						result.append(",");
					}
					result.append("'").append(subStatus).append("'");
				}
			}
		}
		return result.toString();
	}



}
