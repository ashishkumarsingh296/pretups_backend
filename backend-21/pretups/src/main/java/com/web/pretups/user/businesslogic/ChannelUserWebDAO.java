package com.web.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.pretups.channel.transfer.businesslogic.*;
import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryReqGtwTypeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.lms.businesslogic.LoyalityVO;
import com.btsl.pretups.logging.BatchC2CFileProcessLog;
import com.btsl.pretups.logging.BatchFocFileProcessLog;
import com.btsl.pretups.logging.DirectPayOutErrorLog;
import com.btsl.pretups.logging.DirectPayOutSuccessLog;
import com.btsl.pretups.logging.UnregisterChUsersFileProcessLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetLMSVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserTypeVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
//import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.c2s.services.UserBalanceVO;
import com.restapi.channeluser.service.NotificationLanguageResponseVO;

public class ChannelUserWebDAO {

    private static final String PHONE_PROFILE = "phone_profile";
    private static final String SQL_EXCEPTION = "SQL Exception : ";
    private static final String EXCEPTION = "Exception :";

    private ChannelUserWebQry channelUserWebQry;
    /**
     * Field LOG.
     */
    private static final Log LOG = LogFactory.getLog(ChannelUserWebDAO.class.getName());
//    private static OperatorUtilI operatorUtilI = null;
//    static {
//        try {
//            operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
//        } catch (Exception e) {
//        	StringBuffer msg=new StringBuffer("");
//        	msg.append("Exception while loading the operator util class in class : ");
//        	msg.append(ChannelUserDAO.class.getName());
//        	msg.append(":");
//        	msg.append(e.getMessage());
//      
//        	String message=msg.toString();
//            LOG.errorTrace("static block :", e);
//            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "", "", "", "",message);
//        }
//    }
    public ChannelUserWebDAO(){
    	channelUserWebQry = (ChannelUserWebQry)ObjectProducer.getObject(QueryConstants.CHANNEL_USER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    }
    
    /**
     * This method will get information about the requirement of pin for user in
     * the secondary_users and SECONDARY_USER_PHONES table depending on the
     * mobile number
     * 
     * @param p_conn
     *            Connection
     * @param p_userMsisdn
     *            String
     * @return boolean
     * @throws BTSLBaseException
     * 
     */
    public boolean isPinRequired(Connection p_conn, String p_userMsisdn) throws BTSLBaseException {
        final String methodName = "isPinRequired";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userMsisdn =");
        	loggerValue.append(p_userMsisdn);
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = null;
        boolean pinRequired = false;
        try {
            strBuff = new StringBuffer("SELECT a.pin_required pin_required FROM user_phones a,users b");
            strBuff.append(" WHERE a.msisdn=? AND b.status='Y' AND a.user_id=b.user_id");
            pstmt = p_conn.prepareStatement(strBuff.toString());
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query =");
            	loggerValue.append(strBuff.toString());
            	LOG.debug(methodName, loggerValue);
            }
            pstmt.setString(1, p_userMsisdn);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("PIN_REQUIRED") == null || "Y".equalsIgnoreCase(rs.getString("PIN_REQUIRED"))) {
                    pinRequired = true;
                }
            }
            return pinRequired;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[isPinRequired]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[isPinRequired]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: pinRequired:");
            	loggerValue.append(pinRequired);
            	LOG.debug(methodName, loggerValue);
            }
        }
    }

    /**
     * Method getLastTransactionId This method will return the last transaction
     * id from the user_phones table,
     * 
     * @param p_con
     *            Connection
     * @param p_usrMsisdn
     *            String
     * @return string
     * @throws BTSLBaseException
     */

    public String getLastTransactionId(Connection p_con, String p_usrMsisdn) throws BTSLBaseException {
        final String methodName = "getLastTransactionId";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userMsisdn =");
        	loggerValue.append(p_usrMsisdn);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String ltId = null;
        final String qry = "SELECT last_transfer_id FROM user_phones WHERE msisdn=?";
        try {
        	if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query =");
            	loggerValue.append(qry);
            	LOG.debug(methodName, loggerValue);
            }
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_usrMsisdn);
            // Execute Query
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ltId = rs.getString("last_transfer_id");
            } else {
                ltId = "1111111";
            }

            if (ltId == null) {
                ltId = "1111111";
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[getLastTransactionId]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[getLastTransactionId]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                LOG.error(" getRoamLocationList", "  Exception Closing RS : " + ex.getMessage());
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                LOG.error(" getRoamLocationList ", "  Exception Closing Prepared Stmt: " + ex.getMessage());
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: bared:");
            	loggerValue.append(ltId);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return ltId;
    }

    /**
     * Method for Updating Channel User Info in channelUsers table for level One
     * approval.
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_channelUserVO
     *            ChannelUserVO
     * @return updateCount int
     * @throws BTSLBaseException
     * 
     */
    public int updateChannelUserApprovalInfo(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updateChannelUserApprovalInfo";
        ChannelUserDAO channelUserDAO = null;
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_channelUserVO =");
        	loggerValue.append(p_channelUserVO);
            LOG.debug(methodName, loggerValue);
        }
        Boolean isTrfRuleUserLevelAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        Boolean isLmsAppl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        try {
        	//Handling of Expired profile, Channel user should not be de-associated automatically from lms profile
			boolean isActiveProfile =  true;
			channelUserDAO = new ChannelUserDAO();
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "If value of isActiveProfile is false then it means that profile is expired.");
			}	
			
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "If value of isActiveProfile is false then it means that profile is expired.");
			}
			if(BTSLUtil.isNullString(p_channelUserVO.getLmsProfile())){
				ChannelUserVO channelUserLMSVO = channelUserDAO.loadChannelUser(p_con, p_channelUserVO.getUserID());
				if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile())){
					isActiveProfile = channelUserDAO.isProfileActive(p_channelUserVO.getMsisdn(),channelUserLMSVO.getLmsProfile());
					if (LOG.isDebugEnabled()) {
		            	loggerValue.setLength(0);
		            	loggerValue.append("isActiveProfile =");
		            	loggerValue.append(isActiveProfile);
		            	LOG.debug(methodName, loggerValue);
		            }
				} else {
					isActiveProfile =  false;
				}
			}
			//
			
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE channel_users SET user_grade = ?,");
            strBuff.append("contact_person = ?, transfer_profile_id = ?, comm_profile_set_id = ?,");
            strBuff.append("in_suspend = ?, out_suspend = ?,outlet_code = ?,suboutlet_code = ?, ");

            // for Zebra and Tango by sanjeew date 06/07/07
            strBuff.append(" mpay_profile_id=?, user_profile_id=?, mcommerce_service_allow=?, low_bal_alert_allow=? ");
            // Added by Amit Raheja for alerts
            strBuff.append(" ,alert_email=? ,alert_type=?, control_group=? ");
            // End Zebra and Tango
            if (isTrfRuleUserLevelAllow) {
                strBuff.append(", trf_rule_type=?  ");
            }
            if (isLmsAppl && isActiveProfile && !BTSLUtil.isNullString(p_channelUserVO.getLmsProfile())) {
                strBuff.append(" ,lms_profile=? ");
                strBuff.append(" ,lms_profile_updated_on=? ");
            }
            strBuff.append(" WHERE user_id = ?");

            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query =");
            	loggerValue.append(insertQuery);
            	LOG.debug(methodName, loggerValue);
            }
            psmtUpdate = p_con.prepareStatement(insertQuery);

            int rNo = 1;
            psmtUpdate.setString(rNo, p_channelUserVO.getUserGrade());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getContactPerson());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getTransferProfileID());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getCommissionProfileSetID());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getInSuspend());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getOutSuspened());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getOutletCode());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getSubOutletCode());
            rNo++;

            // for Zebra and Tango by sanjeew date 06/07/07
            psmtUpdate.setString(rNo, p_channelUserVO.getMpayProfileID());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getUserProfileID());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getMcommerceServiceAllow());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getLowBalAlertAllow());
            rNo++;
            // End Zebra and Tango
            psmtUpdate.setString(rNo, p_channelUserVO.getAlertEmail());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getAlertType());
            rNo++;
            psmtUpdate.setString(rNo, p_channelUserVO.getControlGroup());
            rNo++;
            // added for user level transfer rule type
            if (isTrfRuleUserLevelAllow) {
                psmtUpdate.setString(rNo, p_channelUserVO.getTrannferRuleTypeId());
                rNo++;
            }

            if (isLmsAppl && isActiveProfile && !BTSLUtil.isNullString(p_channelUserVO.getLmsProfile())) {
                psmtUpdate.setString(rNo, p_channelUserVO.getLmsProfile());
                rNo++;
                psmtUpdate.setTimestamp(rNo, BTSLUtil.getTimestampFromUtilDate(new Date()));
                rNo++;
            }
            psmtUpdate.setString(rNo, p_channelUserVO.getUserID());
            rNo++;

            updateCount = psmtUpdate.executeUpdate();

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserApprovalInfo]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserApprovalInfo]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }

    /**
     * Method for Updating Channel User Info in channelUsers table for Activated
     * On.
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_channelUserVO
     *            ChannelUserVO
     * @return updateCount int
     * @throws BTSLBaseException
     * 
     */
    public int updateChannelUserActivatedOn(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updateChannelUserActivatedOn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_channelUserVO =");
        	loggerValue.append(p_channelUserVO);
            LOG.debug(methodName, loggerValue);
        }
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE channel_users SET activated_on = ? ");
            strBuff.append(" WHERE user_id = ?");

            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query =");
            	loggerValue.append(insertQuery);
            	LOG.debug(methodName, loggerValue);
            }
            psmtUpdate = p_con.prepareStatement(insertQuery);
            psmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_channelUserVO.getActivatedOn()));
            psmtUpdate.setString(2, p_channelUserVO.getUserID());

            updateCount = psmtUpdate.executeUpdate();

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserActivatedOn]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserActivatedOn]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);            }
        }

        return updateCount;
    }

    /**
     * Method for Updating Channel User Info in channelUsers table for level One
     * approval.
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_channleUserVO
     *            ChannelUserVO
     * @return updateCount int
     * @throws BTSLBaseException
     * 
     */
    public int updateChannelUserForAssociate(Connection p_con, ChannelUserVO p_channleUserVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        PreparedStatement pstmtSelect = null;
        ProfileSetLMSVO profileSetLMSVO = null;
        
        Boolean isLmsAppl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        Boolean isOptInOutAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
        Boolean isTrfRuleUserLevelAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        
        ResultSet rs = null;
        int updateCount = 0;
        final String methodName = "updateChannelUserForAssociate";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_channelUserVO =");
        	loggerValue.append(p_channleUserVO);
        	loggerValue.append("p_channleUserVO.getLmsProfile()");
        	loggerValue.append(p_channleUserVO.getLmsProfile());
            LOG.debug(methodName, loggerValue);
        }
        try {
            final StringBuffer strBuff1 = new StringBuffer();

            Date date = new java.util.Date();
            if (isOptInOutAllow) {

                strBuff1.append("SELECT SET_NAME,STATUS,REF_BASED_ALLOWED,PROMOTION_TYPE,PROFILE_TYPE,OPT_IN_OUT_ENABLED,MESSAGE_MANAGEMENT_ENABLED,LAST_VERSION ");
                strBuff1.append("from PROFILE_SET where SET_ID=?");
                final String selectQuery = strBuff1.toString();
                pstmtSelect = p_con.prepareStatement(selectQuery);
                int index = 1;
                pstmtSelect.setString(index, p_channleUserVO.getLmsProfile());
                index++;
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    profileSetLMSVO = new ProfileSetLMSVO();
                    profileSetLMSVO.setSetName(rs.getString("SET_NAME"));
                    profileSetLMSVO.setStatus(rs.getString("STATUS"));
                    profileSetLMSVO.setRefBasedAllow(rs.getString("REF_BASED_ALLOWED"));
                    profileSetLMSVO.setPromotionType(rs.getString("PROMOTION_TYPE"));
                    profileSetLMSVO.setProfileType(rs.getString("PROFILE_TYPE"));
                    profileSetLMSVO.setOptInOut(rs.getString("OPT_IN_OUT_ENABLED"));
                    profileSetLMSVO.setMsgConfEnableFlag(rs.getString("MESSAGE_MANAGEMENT_ENABLED"));
                    profileSetLMSVO.setLastVersion(rs.getString("LAST_VERSION"));
                }

            }

            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE channel_users SET user_grade = ?,");
            strBuff.append("transfer_profile_id = ?, comm_profile_set_id = ? ");
            // tango changes start by sanjeew date 06/07/07
            strBuff.append(", mpay_profile_id =?, mcommerce_service_allow=? ");

            // tango changes end
            if (isLmsAppl) {
                strBuff.append(", lms_profile=? ");
                strBuff.append(", lms_profile_updated_on=? ");
                if (isOptInOutAllow) {
                    strBuff.append(", OPT_IN_OUT_STATUS=? ");
                }
                strBuff.append(", CONTROL_GROUP=? ");
            }
            // / added for user level transfer rule type
            if (isTrfRuleUserLevelAllow) {
                strBuff.append(", trf_rule_type=? ");
            }
            strBuff.append(" WHERE user_id = ?");

            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query =");
            	loggerValue.append(insertQuery);
            	LOG.debug(methodName, loggerValue);
            }
            psmtUpdate = p_con.prepareStatement(insertQuery);
            int i = 1;
            psmtUpdate.setString(i, p_channleUserVO.getUserGrade());
            i++;
            psmtUpdate.setString(i, p_channleUserVO.getTransferProfileID());
            i++;
            psmtUpdate.setString(i, p_channleUserVO.getCommissionProfileSetID());
            i++;
            // tango changes start by sanjeew date 06/07/07
            psmtUpdate.setString(i, p_channleUserVO.getMpayProfileID());
            i++;
            psmtUpdate.setString(i, p_channleUserVO.getMcommerceServiceAllow());
            i++;
            if (isLmsAppl) {
                psmtUpdate.setString(i, p_channleUserVO.getLmsProfile());
                i++;
                psmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(date));
                i++;
                if (isOptInOutAllow) {
                   psmtUpdate.setString(i, PretupsI.NORMAL);
                    
					                i++;
                }
				if(profileSetLMSVO!=null && p_channleUserVO.getControlGroup()!=null) {
                   psmtUpdate.setString(i, p_channleUserVO.getControlGroup());
                } else {
                   psmtUpdate.setString(i, PretupsI.NO);
                }
				i++;
            }
            if (isTrfRuleUserLevelAllow) {
                psmtUpdate.setString(i, p_channleUserVO.getTrannferRuleTypeId());
                i++;
            }
            psmtUpdate.setString(i, p_channleUserVO.getUserID());
            i++;

            // tango changes end

            updateCount = psmtUpdate.executeUpdate();

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserForAssociate]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChannelUserForAssociate]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                	pstmtSelect.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);            }
        }

        return updateCount;
    }

    /**
     * Load the category user. according to category id
     * 
     * @param p_con
     * @param p_categoryCode
     * @param p_networkCode
     * @param p_userName
     * @param p_ownerUserID
     * @param p_statusUsed
     *            TODO
     * @param p_status
     *            TODO
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryUsers(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_statusUsed, String p_status) throws BTSLBaseException {

        final String methodName = "loadCategoryUsers";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Category Code =");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" User Name: ");
        	loggerValue.append(p_userName);
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

        strBuff.append(" SELECT u.user_id,u.user_name FROM users u WHERE u.network_code = ? AND  ");
        strBuff.append(" u.category_code = ? AND upper(u.user_name) LIKE upper(?) ");
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
            // commented for DB2 pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            pstmt.setString(i, p_userName);

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
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
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
     * Method loadCategoryUsersWithinGeoDomainHirearchy. This method the loads
     * the user list with userID and UserName, for the search screen .
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_userName
     *            String
     * @param p_ownerUserID
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_loginUserID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryUsersWithinGeoDomainHirearchy(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode, String p_loginUserID,String p_userId) throws BTSLBaseException {

        final String methodName = "loadCategoryUsersWithinGeoDomainHirearchy";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Category Code =");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" User Name: ");
        	loggerValue.append(p_userName);
        	loggerValue.append(" ownerUserID: ");
        	loggerValue.append(p_ownerUserID);
        	loggerValue.append(" p_geographicalDomainCode: ");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(" p_loginUserID: ");
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(" p_userId: ");
        	loggerValue.append(p_userId);
            LOG.debug(methodName, loggerValue);
        }

        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        String receiverStatusAllowed = null;
        String statusAllowed = null;

        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache
            .getObject(p_networkCode, p_categoryCode, PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            statusAllowed = receiverStatusAllowed + ",'" + PretupsI.USER_STATUS_SUSPEND + "'";
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }

        
        final ArrayList arrayList = new ArrayList();
        try {
            // commented for DB2
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
            pstmt = channelUserWebQry.loadCategoryUsersWithinGeoDomainHirearchyQry( p_con, p_networkCode, p_categoryCode, p_geographicalDomainCode, p_userName, p_loginUserID, p_ownerUserID, statusAllowed, receiverStatusAllowed,p_userId );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", loggerValue.toString());
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
            	LOG.debug(methodName, loggerValue);             }
        }
        return arrayList;
    }

    /**
     * Method for checking User Code is already exist with in the Netwrok or
     * not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_userCode
     *            String
     * @param p_userID
     *            String
     * 
     * @return flag boolean
     * @throws BTSLBaseException
     * 
     */
    public boolean isUserCodeExist(Connection p_con, String p_networkCode, String p_userCode, String p_userID) throws BTSLBaseException {
        final String methodName = "isUserCodeExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userCode=");
        	loggerValue.append(p_userCode);
        	loggerValue.append(" Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_userID: ");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuffer strBuff = new StringBuffer();
        /*
         * In add mode setId is null but in edit mode setId is not null beca we
         * have tp apply the where claue
         */
        if (BTSLUtil.isNullString(p_userID)) {
            strBuff.append("SELECT user_code FROM users ");
            strBuff.append("WHERE network_code = ? AND user_code = ? AND status not in ('N','C')");
        } else {
            strBuff.append("SELECT user_code FROM users ");
            strBuff.append("WHERE network_code = ? AND user_code = ? AND user_id != ? AND status not in ('N','C')");
        }
        final String sqlSelect = strBuff.toString();

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Query =");
        	loggerValue.append(sqlSelect);
        	LOG.debug(methodName, loggerValue);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            if (BTSLUtil.isNullString(p_userID)) {
                pstmt.setString(1, p_networkCode);
                pstmt.setString(2, p_userCode);
            } else {
                pstmt.setString(1, p_networkCode);
                pstmt.setString(2, p_userCode);
                pstmt.setString(3, p_userID);
            }
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserCodeExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isUserCodeExist]", "", "", "",
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
            	loggerValue.append("Exiting: existFlag:");
            	loggerValue.append(existFlag);
            	LOG.debug(methodName, loggerValue);
            }
        }
    }

    /**
     * Method for Updating Channel User's status,modified_on and modified_by for
     * suspending the channel user
     * 
     * @author manoj kumar
     * @param p_con
     *            java.sql.Connection
     * @param p_userList
     *            java.util.ArrayList
     * @param p_channleUserVO
     *            ChannelUserVO
     * @return updateCount int
     * @throws BTSLBaseException
     * 
     */
    public int changeChannelUserStatus(Connection p_con, ArrayList p_userList, ChannelUserVO p_channleUserVO, String userStatus) throws BTSLBaseException {
        final String methodName = "changeChannelUserStatus";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userList=");
        	loggerValue.append(p_userList);
        	loggerValue.append(" p_channleUserVO: ");
        	loggerValue.append(p_channleUserVO.toString());
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement psmtUpdate = null;
        ChannelUserVO channelUserVO = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        Timestamp newLastModified = null;
        int updateCount = 0;
        long p_oldLastModified = 0;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE users SET ");
            strBuff.append(" status = ?,modified_by=?,modified_on=?,previous_status=status ");
            strBuff.append(" WHERE user_id = ?");
            final String updateQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query =");
            	loggerValue.append(updateQuery);
            	LOG.debug(methodName, loggerValue);
            }
            final String sqlRecordModified = "SELECT modified_on,status  FROM users WHERE user_id=? ";

            psmtUpdate = p_con.prepareStatement(updateQuery);
            pstmt = p_con.prepareStatement(sqlRecordModified);

            for (int i = 0, j = p_userList.size(); i < j; i++) {
                modified = false;
                psmtUpdate.setString(1, userStatus);
                psmtUpdate.setString(2, p_channleUserVO.getModifiedBy());
                psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_channleUserVO.getModifiedOn()));
                channelUserVO = (ChannelUserVO) p_userList.get(i);
                psmtUpdate.setString(4, channelUserVO.getUserID());

                p_oldLastModified = channelUserVO.getLastModified();
                pstmt.setString(1, channelUserVO.getUserID());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    newLastModified = rs.getTimestamp("modified_on");
                    channelUserVO.setPreviousStatus(rs.getString("status"));
                }
                if (newLastModified.getTime() != p_oldLastModified) {
                    modified = true;
                }
                if (modified) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();
                pstmt.clearParameters();
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changeChannelUserStatus]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changeChannelUserStatus]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }

    /**
     * Method loadParentUserDetailsForTransfer() This method
     * 
     * @param p_con
     * @param p_userID
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @throws Exception
     * @throws SQLException
     */
    public ChannelUserVO loadParentUserDetailsForTransfer(Connection p_con, String p_userID) throws BTSLBaseException {
        final String methodName = "loadParentUserDetailsForTransfer";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID=");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Boolean isTrfRuleUserLevelAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        final StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" SELECT u.user_id,u.status u_status, u.user_name,u.category_code, u.parent_id,u.owner_id, u.msisdn, ");
        sqlBuffer.append(" u.designation,u.division,u.department,cusers.in_suspend,cusers.out_suspend, ");
        sqlBuffer.append(" u.address1,u.address2,u.city,u.state,u.country,u.user_code,u.short_name, ");
        sqlBuffer.append(" cat.domain_code,cusers.comm_profile_set_id,cusers.transfer_profile_id, ");
        sqlBuffer.append(" cusers.user_grade, cps.comm_profile_set_name , cg.grade_name ,tp.profile_name , ");
        sqlBuffer.append(" cps.status,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append(" cps.language_2_message comprf_lang_2_msg, cat.category_code,cat.category_name ");
        if (isTrfRuleUserLevelAllow) {
            sqlBuffer.append(" , cusers.trf_rule_type ");
        }
        sqlBuffer.append(" FROM users u, networks l,categories cat,channel_users cusers,");
        sqlBuffer.append(" commission_profile_set cps, channel_grades cg, ");
        sqlBuffer.append(" transfer_profile tp ");
        sqlBuffer.append(" WHERE   ");
        sqlBuffer.append(" u.user_id = ? ");
        sqlBuffer.append(" AND cat.status= ?  ");
        
        sqlBuffer.append(" AND u.user_id=cusers.user_id ");
        sqlBuffer.append(" AND cat.category_code=u.category_code ");
        sqlBuffer.append(" AND cps.comm_profile_set_id = cusers.comm_profile_set_id ");
        sqlBuffer.append(" AND cg.grade_code = cusers.user_grade  ");
        sqlBuffer.append(" AND tp.profile_id = cusers.transfer_profile_id   ");
        ChannelUserVO channelUserVO = null;
        try {
        	 if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Query =");
             	loggerValue.append(sqlBuffer.toString());
             	LOG.debug(methodName, loggerValue);
             }
            pstmt = p_con.prepareStatement(sqlBuffer.toString());
            int i = 0;
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            pstmt.setString(i, TypesI.YES);
          
            rs = pstmt.executeQuery();
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setDivisionCode(rs.getString("division"));
                channelUserVO.setDepartmentCode(rs.getString("department"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelUserVO.setUserGradeName(rs.getString("grade_name"));
                channelUserVO.setTransferProfileName(rs.getString("profile_name"));
                channelUserVO.setCommissionProfileStatus(rs.getString("status"));
                channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelUserVO.setStatus(rs.getString("u_status"));
                final CategoryVO categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                if (isTrfRuleUserLevelAllow) {
                    channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                }
                channelUserVO.setCategoryVO(categoryVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.error("loadParentUserDetailsForTransfer::", " Exception : in closing resultset" + ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                LOG.errorTrace(methodName, ex);
            } catch (Exception ex) {
                LOG.error("loadParentUserDetailsForTransfer ::", " Exception : in closing preparedstatement" + ex);
            }
        }
        if(LOG.isDebugEnabled()){
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting: channelUserVO:");
        	loggerValue.append(channelUserVO);
        	LOG.debug(methodName, loggerValue);
        }
        return channelUserVO;
    }

    /**
     * This method load the user hierarchy (including himself also) by the
     * userCode
     * 
     * @param p_con
     *            Connection
     * @param p_userIDCode
     *            String
     * @param p_isUserCode
     *            TODO
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadChannelUserHierarchy(Connection p_con, String p_userIDCode, boolean p_isUserCode) throws BTSLBaseException {
        final String methodName = "loadChannelUserHierarchy";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userIDCode=");
        	loggerValue.append(p_userIDCode);
        	loggerValue.append(",p_isUserCode=");
        	loggerValue.append(p_isUserCode);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        final ArrayList list = new ArrayList();
        try {

            /*
             * strBuff.append(" SELECT
             * UP.msisdn,U.user_id,U.user_name,U.user_code,U.network_code,
             * U.login_id "); strBuff.append(" FROM users U,user_phones UP ");
             * strBuff.append(" WHERE U.user_id=UP.user_id AND U.user_id<>
             * U.user_code AND UP.primary_number='Y' "); strBuff.append(" AND
             * U.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' "); strBuff.append("
             * AND U.status <> 'N' AND U.status <> 'C' "); strBuff.append("
             * CONNECT BY PRIOR U.user_id=U.parent_id "); strBuff.append(" START
             * WITH "); if(p_isUserCode) strBuff.append(" U.user_code=? "); else
             * strBuff.append(" U.user_id=? ");
             */

           String strBuffQuery=channelUserWebQry.loadChannelUserHierarchyQry(p_isUserCode);
           if (LOG.isDebugEnabled()) {
           	loggerValue.setLength(0);
           	loggerValue.append("Query =");
           	loggerValue.append(strBuffQuery);
           	LOG.debug(methodName, loggerValue);
           }
            pstmt = p_con.prepareStatement(strBuffQuery);
            pstmt.setString(1, p_userIDCode);
            rs = pstmt.executeQuery();
            ChannelUserVO channelUserVO = null;
            while (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                list.add(channelUserVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserHierarchy]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserHierarchy]", "", "", "",
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: List Size:");
            	loggerValue.append(list.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return list;
    }

    /**
     * This method is used to Delete or Suspend the channel users on the basis
     * of MSISDN Author Amit Singh Method
     * deleteOrSuspendChnlUsersInBulkForMsisdn
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_deleteOrSuspendorResume
     * @param p_chlildExistList
     *            ArrayList
     * @param p_modifiedBy
     * @param p_countStr
     *            int
     * @return String
     * @throws BTSLBaseException
     */
    public boolean deleteOrSuspendChnlUsersInBulkForMsisdn(Connection p_con, String p_msisdn, String p_deleteOrSuspendorResume, List p_chlildExistList, String p_modifiedBy, int p_countStr, Map prepareStatementMap) throws BTSLBaseException {
        final String methodName = "deleteOrSuspendChnlUsersInBulkForMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_deleteOrSuspendorResume=");
        	loggerValue.append(p_deleteOrSuspendorResume);
        	loggerValue.append(",p_chlildExistList=");
        	loggerValue.append(p_chlildExistList);
        	loggerValue.append(",p_modifiedBy=");
        	loggerValue.append(p_modifiedBy);
            LOG.debug(methodName, loggerValue);
        }

        Boolean isUsrBtchDelAppvl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USR_BTCH_SUS_DEL_APRVL);
        
        ResultSet rsIsExist = null;
        ResultSet rsUserID = null;
        UserDAO userDAO = null;
        ChannelTransferDAO channelTransferDAO = null;
        boolean invalidStringInDao = false;
        final ChannelUserVO chnlUserVO = ChannelUserVO.getInstance();
        // Added for resume bulk user
        // PreparedStatement psmtResumeExist = null;
        ResultSet rsResumeExist = null;
        ResultSet rs = null;
        PreparedStatement psmtIsExist = null;
        PreparedStatement psmtUserID = null;
        PreparedStatement psmtDelete = null;
        PreparedStatement psmtResumeExist = null;
        PreparedStatement psmtChildExist = null;
        PreparedStatement psmtUserBalanceExist = null;
        PreparedStatement psmtChnlTrnsfrPendingTransactionExist = null;
        PreparedStatement psmtfocPendingTransactionExist = null;
        int balance = 0;
        try {
            // To resume the mobile number, it must be suspended.
            final StringBuffer isExistQueryMsisdn = new StringBuffer();
            final StringBuffer userIDQueryMsisdn = new StringBuffer();
            final StringBuffer deleteQueryMsisdn = new StringBuffer();
            final StringBuffer isResumeQueryMsisdn = new StringBuffer();
            final StringBuffer childExist = new StringBuffer();
            final StringBuffer userBalanceExist = new StringBuffer();
            final StringBuffer chnlTrnsfrPendingTransactionExist = new StringBuffer();
            final StringBuffer focPendingTransactionExist = new StringBuffer();
            
            try {
//                psmtIsExist = (PreparedStatement) prepareStatementMap.get("psmtIsExist");
//                psmtUserID = (PreparedStatement) prepareStatementMap.get("psmtUserID");
//                psmtDelete = (PreparedStatement) prepareStatementMap.get("psmtDelete");
//                psmtResumeExist = (PreparedStatement) prepareStatementMap.get("psmtResumeExist");
//                psmtChildExist = (PreparedStatement) prepareStatementMap.get("psmtChildExist");
//                psmtUserBalanceExist = (PreparedStatement) prepareStatementMap.get("psmtUserBalanceExist");
//                psmtChnlTrnsfrPendingTransactionExist = (PreparedStatement) prepareStatementMap.get("psmtChnlTrnsfrPendingTransactionExist");
//                psmtfocPendingTransactionExist = (PreparedStatement) prepareStatementMap.get("psmtfocPendingTransactionExist");

                // Preparing Prepared Statement for MSISDN
                isExistQueryMsisdn.append(" SELECT UP.msisdn FROM user_phones UP, users U WHERE U.user_id=UP.user_id ");
                isExistQueryMsisdn.append(" AND U.status<>'N' AND U.status<>'C' AND UP.msisdn= ?");
                if (psmtIsExist == null) {
                	 if (LOG.isDebugEnabled()) {
                     	loggerValue.setLength(0);
                     	loggerValue.append("isExistQueryMSISDN =");
                     	loggerValue.append(isExistQueryMsisdn);
                     	LOG.debug(methodName, loggerValue);
                     }
                    psmtIsExist = p_con.prepareStatement(isExistQueryMsisdn.toString());
                    prepareStatementMap.put("psmtIsExist", psmtIsExist);
                }

                userIDQueryMsisdn.append("SELECT UP.user_id , C.sequence_no, U.status, U.login_id FROM user_phones UP, users U ,");
                userIDQueryMsisdn.append("categories C WHERE U.user_id=UP.user_id AND U.status<>'N' AND U.status<>'C' ");
                userIDQueryMsisdn.append("AND UP.msisdn= ? AND U.category_code=C.category_code ");
                if (psmtUserID == null) {
                	 if (LOG.isDebugEnabled()) {
                     	loggerValue.setLength(0);
                     	loggerValue.append("userIDQueryMSISDN =");
                     	loggerValue.append(userIDQueryMsisdn);
                     	LOG.debug(methodName, loggerValue);
                     }
                    psmtUserID = p_con.prepareStatement(userIDQueryMsisdn.toString());
                    prepareStatementMap.put("psmtUserID", psmtUserID);
                }

                deleteQueryMsisdn.append("UPDATE users SET status=?, modified_by=?, modified_on=?, ");
                deleteQueryMsisdn.append("previous_status=?, login_id=? WHERE user_id =? and user_type='CHANNEL' ");
                if (psmtDelete == null) {
                	 if (LOG.isDebugEnabled()) {
                     	loggerValue.setLength(0);
                     	loggerValue.append("deleteQuery =");
                     	loggerValue.append(deleteQueryMsisdn);
                     	LOG.debug(methodName, loggerValue);
                     }
                    psmtDelete = p_con.prepareStatement(deleteQueryMsisdn.toString());
                    prepareStatementMap.put("psmtDelete", psmtDelete);
                }

                isResumeQueryMsisdn.append(" SELECT UP.msisdn FROM user_phones UP, users U WHERE U.user_id=UP.user_id ");
                isResumeQueryMsisdn.append("AND U.status='S' AND UP.msisdn= ?");
                if (psmtResumeExist == null) {
                	 if (LOG.isDebugEnabled()) {
                     	loggerValue.setLength(0);
                     	loggerValue.append("isResumeQuery =");
                     	loggerValue.append(isResumeQueryMsisdn);
                     	LOG.debug(methodName, loggerValue);
                     }
                    psmtResumeExist = p_con.prepareStatement(isResumeQueryMsisdn.toString());
                    prepareStatementMap.put("psmtResumeExist", psmtResumeExist);
                }

                //Parul left todo
                /*childExist.append("SELECT 1 FROM users ");
                childExist.append(" WHERE status <> 'N' AND status <> 'C' ");
                childExist.append("AND user_id != ? and user_type=? ");
                childExist.append("start with  user_id = ? ");
                childExist.append("connect by  prior user_id = parent_id");
                if (psmtChildExist == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processUploadedFileForUnReg", "psmtChildExist=" + childExist);
                    }
                    psmtChildExist = p_con.prepareStatement(childExist.toString());
                    prepareStatementMap.put("psmtChildExist", psmtChildExist);
                }*/

                userBalanceExist.append("SELECT balance FROM user_balances ");
                userBalanceExist.append(" WHERE  user_id = ? ");
                if (psmtUserBalanceExist == null) {
                	 if (LOG.isDebugEnabled()) {
                     	loggerValue.setLength(0);
                     	loggerValue.append("userBalExists =");
                     	loggerValue.append(userBalanceExist);
                     	LOG.debug(methodName, loggerValue);
                     }
                    psmtUserBalanceExist = p_con.prepareStatement(userBalanceExist.toString());
                    prepareStatementMap.put("psmtUserBalanceExist", psmtUserBalanceExist);
                }

                chnlTrnsfrPendingTransactionExist.append(" SELECT 1  ");
                chnlTrnsfrPendingTransactionExist.append(" FROM channel_transfers ");
                chnlTrnsfrPendingTransactionExist.append(" WHERE (from_user_id=? OR to_user_id =?) AND ");
                chnlTrnsfrPendingTransactionExist.append(" (status <> ? AND status <> ? )");
                if (psmtChnlTrnsfrPendingTransactionExist == null) {
                	 if (LOG.isDebugEnabled()) {
                     	loggerValue.setLength(0);
                     	loggerValue.append("chnlTransferPendingTransactionExist =");
                     	loggerValue.append(chnlTrnsfrPendingTransactionExist);
                     	LOG.debug(methodName, loggerValue);
                     }
                    psmtChnlTrnsfrPendingTransactionExist = p_con.prepareStatement(chnlTrnsfrPendingTransactionExist.toString());
                    prepareStatementMap.put("psmtChnlTrnsfrPendingTransactionExist", psmtChnlTrnsfrPendingTransactionExist);
                }

                focPendingTransactionExist.append(" SELECT 1  ");
                focPendingTransactionExist.append(" FROM foc_batch_items ");
                focPendingTransactionExist.append(" WHERE user_id=? AND ");
                focPendingTransactionExist.append(" (status <> ? AND status <> ? )");
                if (psmtfocPendingTransactionExist == null) {
                	 if (LOG.isDebugEnabled()) {
                     	loggerValue.setLength(0);
                     	loggerValue.append("focPendingTransactionExist =");
                     	loggerValue.append(focPendingTransactionExist);
                     	LOG.debug(methodName, loggerValue);
                     }
                    psmtfocPendingTransactionExist = p_con.prepareStatement(focPendingTransactionExist.toString());
                    prepareStatementMap.put("psmtfocPendingTransactionExist", psmtfocPendingTransactionExist);
                }

            } catch (Exception e) {
                LOG.errorTrace("processUploadedFileForUnReg", e);
            }

            String userID;
            String loginID;
            int updateCount = 0;

            // checking that mobile no. exists or not
            psmtIsExist.setString(1, p_msisdn);
            rsIsExist = psmtIsExist.executeQuery();
            psmtIsExist.clearParameters();
            // Checking whether the mobile number is suspended for the resume or
            // not.
            psmtResumeExist.setString(1, p_msisdn);
            rsResumeExist = psmtResumeExist.executeQuery();
            psmtResumeExist.clearParameters();

            if ((p_deleteOrSuspendorResume.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || (p_deleteOrSuspendorResume.equals(PretupsI.USER_STATUS_SUSPEND_BATCH)) || PretupsI.USER_STATUS_DELETE_REQUEST.equals(p_deleteOrSuspendorResume) ||  PretupsI.USER_STATUS_DELETE_BATCH.equals(p_deleteOrSuspendorResume)) && !rsIsExist.next()) {
            	 if (LOG.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("Mobile number does not exist =");
                 	loggerValue.append(p_msisdn);
                 	LOG.debug(methodName, loggerValue);
                 }
                UnregisterChUsersFileProcessLog.log("User existence", "", "MSISDN :" + p_msisdn, p_countStr, "Mobile number does not exist", "Fail", "");
                invalidStringInDao = true;
                if (rsIsExist != null) {
                    rsIsExist.close();
                }
            } else if (PretupsI.USER_STATUS_RESUME_REQUEST.equals(p_deleteOrSuspendorResume) && !rsResumeExist.next()) {
            	if (LOG.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("Mobile number is not in suspended state to resume=");
                 	loggerValue.append(p_msisdn);
                 	LOG.debug(methodName, loggerValue);
                 }
                UnregisterChUsersFileProcessLog.log("User existence", "", "MSISDN :" + p_msisdn, p_countStr, "Mobile number can't be resumed.", "Fail", "");
                invalidStringInDao = true;
                if (rsResumeExist != null) {
                    rsResumeExist.close();
                }
            } else {
                final boolean isBalanceFlag = false;
                final boolean isO2CPendingFlag = false;
                final boolean isBatchFOCPendingFlag = false;
                final boolean isChildFlag = false;
                boolean isC2SPending = false;

                psmtUserID.setString(1, p_msisdn);
                rsUserID = psmtUserID.executeQuery();
                psmtUserID.clearParameters();
                userDAO = new UserDAO();
                channelTransferDAO = new ChannelTransferDAO();
                final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
                if (rsUserID.next()) {
                    userID = rsUserID.getString("user_id");
                    loginID = rsUserID.getString("login_id");

                    // Suspension or Resume of cahnnel user without any check,
                    // i.e. change user status to 'S' or 'Y' respectively.
                    if (p_deleteOrSuspendorResume.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || PretupsI.USER_STATUS_RESUME_REQUEST.equals(p_deleteOrSuspendorResume) || p_deleteOrSuspendorResume.equals(PretupsI.USER_STATUS_SUSPEND_BATCH) ) {
                        String userStatus = null;
                        if (isUsrBtchDelAppvl && p_deleteOrSuspendorResume.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                        	userStatus = PretupsI.USER_STATUS_SUSPEND_BATCH;
                        }else if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(p_deleteOrSuspendorResume)) {
                            userStatus = PretupsI.USER_STATUS_SUSPEND;
                        } else if (PretupsI.USER_STATUS_RESUME_REQUEST.equals(p_deleteOrSuspendorResume)) {
                            userStatus = PretupsI.USER_STATUS_RESUMED;
                        }else if (PretupsI.USER_STATUS_SUSPEND_BATCH.equals(p_deleteOrSuspendorResume)) {
                        	  userStatus = PretupsI.USER_STATUS_SUSPEND;
                        }

                        if (LOG.isDebugEnabled()) {
                         	loggerValue.setLength(0);
                         	loggerValue.append("This is the request for status=");
                         	loggerValue.append(userStatus);
                         	loggerValue.append(", userID=");
                         	loggerValue.append(userID);
                         	LOG.debug(methodName, loggerValue);
                         }
                        psmtDelete.setString(1, userStatus);
                        psmtDelete.setString(2, p_modifiedBy);
                        psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
                        psmtDelete.setString(4, rsUserID.getString("status"));
                        psmtDelete.setString(5, loginID);
                        psmtDelete.setString(6, userID);
                        updateCount = psmtDelete.executeUpdate();
                        if (updateCount <= 0) {
                            invalidStringInDao = true;
                        }
                        psmtDelete.clearParameters();
                    }
                    /*
                     * deletion of cahnnel user, i.e. change user status to 'N'
                     * but with some checks as- a)Check whether the child user
                     * is active or not b)Check the balance of the deleted user
                     * c)Check for no O2C Transfer pending (closed and canceled
                     * Txn)
                     */
                    else {            
                    	psmtChildExist = channelUserWebQry.deleteOrSuspendChnlUsersInBulkForMsisdn(p_con ,userID,prepareStatementMap);
                      
                        rs = psmtChildExist.executeQuery();
                        if (rs.next()) {
                            chnlUserVO.setUserID(userID);
                            chnlUserVO.setCategoryVO(new CategoryVO());
                            chnlUserVO.getCategoryVO().setCategorySequenceNumber(rsUserID.getInt("sequence_no"));
                            chnlUserVO.setMsisdn(p_msisdn);
                            chnlUserVO.setStatus(rsUserID.getString("status"));
                            chnlUserVO.getCategoryVO().setSequenceNumber(p_countStr);
                            p_chlildExistList.add(chnlUserVO);
                            invalidStringInDao = true;
                            /*
                             * if(LOG.isDebugEnabled())
                             * LOG.debug("deleteOrSuspendChnlUsersInBulkForMsisdn"
                             * ,"This
                             * user has childs down the hierarchy, so can't be
                             * deleted "+p_msisdn); invalidStringInDao = true;
                             * UnregisterChUsersFileProcessLog.log("CHILD
                             * EXISTS",userID,"MSISDN :
                             * "+p_msisdn,p_countStr,"Child exists for this
                             * user","Fail","");
                             */} /*
                                  * else {
                                  * // isBalanceFlag =
                                  * // userDAO.isUserBalanceExist(p_con,userID);
                                  * psmtUserBalanceExist.clearParameters();
                                  * psmtUserBalanceExist.setString(1, userID);
                                  * rs = psmtUserBalanceExist.executeQuery();
                                  * if (rs.next()) {
                                  * balance = rs.getInt("balance");
                                  * }
                                  * 
                                  * if (balance > 0) {
                                  * if (LOG.isDebugEnabled())
                                  * LOG.debug(
                                  * "deleteOrSuspendChnlUsersInBulkForMsisdn",
                                  * "This user has some balance, so can't be deleted="
                                  * + p_msisdn);
                                  * invalidStringInDao = true;
                                  * UnregisterChUsersFileProcessLog.log(
                                  * "IS USER BALANCE EXISTS", userID,
                                  * "MSISDN : " + p_msisdn, p_countStr,
                                  * "User balance exists", "Fail", "");
                                  * }
                                  */else {
                            // Checking O2C Pending transactions
                            // isO2CPendingFlag =
                            // channelTransferDAO.isPendingTransactionExist(p_con,userID);
                                	  if(!isUsrBtchDelAppvl || p_deleteOrSuspendorResume.equals(PretupsI.USER_STATUS_DELETE_BATCH)){
                            psmtChnlTrnsfrPendingTransactionExist.clearParameters();
                            int i = 1;
                            psmtChnlTrnsfrPendingTransactionExist.setString(i, userID);
                            i++;
                            psmtChnlTrnsfrPendingTransactionExist.setString(i, userID);
                            i++;
                            psmtChnlTrnsfrPendingTransactionExist.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                            i++;
                            psmtChnlTrnsfrPendingTransactionExist.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                            i++;
                            if(rs!=null)
                            rs.close();
                            rs=null;
                            rs = psmtChnlTrnsfrPendingTransactionExist.executeQuery();
                            if (rs.next()) {
                            	if (LOG.isDebugEnabled()) {
                                 	loggerValue.setLength(0);
                                 	loggerValue.append("This user has pending transactions, so can't be deleted ");
                                 	loggerValue.append(p_msisdn);
                                 	LOG.debug(methodName, loggerValue);
                                 }
                                UnregisterChUsersFileProcessLog.log("IS PENDING TRANSACTION EXISTS", userID, "MSISDN : " + p_msisdn, p_countStr, "Pending User's transaction",
                                    "Fail", "");
                                invalidStringInDao = true;
                            } else {
                                // Checking if any batch FOC pending
                                // transaction exists Ved - 07/08/2006
                                // isBatchFOCPendingFlag=batchTransferDAO.isPendingTransactionExist(p_con,userID);
                                psmtfocPendingTransactionExist.clearParameters();
                                int j = 1;
                                psmtfocPendingTransactionExist.setString(j, userID);
                                j++;
                                psmtfocPendingTransactionExist.setString(j, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                                j++;
                                psmtfocPendingTransactionExist.setString(j, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                                j++;
                                if(rs!=null)
                                rs.close();
                                rs=null;
                                rs = psmtfocPendingTransactionExist.executeQuery();
                                if (rs.next()) {

                                	if (LOG.isDebugEnabled()) {
                                     	loggerValue.setLength(0);
                                     	loggerValue.append("This user has pending foc batch transactions, so can't be deleted ");
                                     	loggerValue.append(p_msisdn);
                                     	LOG.debug(methodName, loggerValue);
                                     }
                                    UnregisterChUsersFileProcessLog.log("IS PENDING FOC BATCH TRANSACTION EXISTS", userID, "MSISDN : " + p_msisdn, p_countStr,
                                        "Pending User's foc batch transaction", "Fail", "");
                                    invalidStringInDao = true;

                                } else {
                                	
                                	isC2SPending=channelTransferDAO.isC2SPendingTransactionExist(p_con, userID);
                                	if(isC2SPending)
                                	{
                                		if (LOG.isDebugEnabled()) {
                                         	loggerValue.setLength(0);
                                         	loggerValue.append("This user has pending c2s transactions, so can't be deleted ");
                                         	loggerValue.append(p_msisdn);
                                         	LOG.debug(methodName, loggerValue);
                                         }
                                        UnregisterChUsersFileProcessLog.log("IS PENDING C2S TRANSACTION EXISTS", userID, "Login ID : " + p_msisdn, p_countStr,
                                            "Pending User's c2s transaction", "Fail", "");
                                        invalidStringInDao = true;
                                	}
                                	else
                                	{  
                                		if (LOG.isDebugEnabled()) {
                                			loggerValue.setLength(0);
                                			loggerValue.append("This is the request for deletion, MSISDN=");
                                			loggerValue.append(p_msisdn);
                                			LOG.debug(methodName, loggerValue);
                                		}
                                    psmtDelete.setString(1, PretupsI.USER_STATUS_DELETED);
                                    psmtDelete.setString(2, p_modifiedBy);
                                    psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    psmtDelete.setString(4, rsUserID.getString("status"));
                                    psmtDelete.setString(5, userID);
                                    psmtDelete.setString(6, userID);
                                    updateCount = psmtDelete.executeUpdate();

                                    if (updateCount <= 0) {
                                        invalidStringInDao = true;
                                    }
                                    psmtDelete.clearParameters();
                                }//end of else
                              }//end of else
                            } // end of else
                                	  } else {
                                		  if (LOG.isDebugEnabled()) {
                                  			loggerValue.setLength(0);
                                  			loggerValue.append("This is the request for deletion, MSISDN=");
                                  			loggerValue.append(p_msisdn);
                                  			LOG.debug(methodName, loggerValue);
                                  		}
                    					  psmtDelete.setString(1, PretupsI.USER_STATUS_DELETE_BATCH);
                    					  psmtDelete.setString(2, p_modifiedBy);
                    					  psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
                    					  psmtDelete.setString(4, rsUserID.getString("status"));
                    					  psmtDelete.setString(5, userID);
                    					  psmtDelete.setString(6, userID);
                    					  updateCount = psmtDelete.executeUpdate();
                    					  if (updateCount <= 0) {
                    						  invalidStringInDao = true;
                    					  }
                    					  psmtDelete.clearParameters();
                            } // end of else
                        } // end of else
                        // } // end of else
                        balance = 0;
                    } // end of else
                } // end of if
                else {
                	if (LOG.isDebugEnabled()) {
            			loggerValue.setLength(0);
            			loggerValue.append("Mobile number already deleted or suspended or resumed for the msisdn=");
            			loggerValue.append(p_msisdn);
            			LOG.debug(methodName, loggerValue);
            		}
                    UnregisterChUsersFileProcessLog.log("User existence", "", "MSISDN :" + p_msisdn, p_countStr, "Mobile number already deleted or suspended or resumed",
                        "Fail", "");

                }
            } // end of else
        } // end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[deleteOrSuspendChnlUsersInBulkForMsisdn]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "deleteOrSuspendChnlUsersInBulkForMsisdn", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[deleteOrSuspendChnlUsersInBulkForMsisdn]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "deleteOrSuspendChnlUsersInBulkForMsisdn", "error.general.processing",ex);
        } finally {
            try {
            	if(rs!=null){
            		rs.close();
            	}
            	if (rsResumeExist != null) {
            		rsResumeExist.close();
                }
            	
                if (rsIsExist != null) {
                    rsIsExist.close();
                }
                if (rsUserID != null) {
                    rsUserID.close();
                }
                if(psmtIsExist!=null){
                	psmtIsExist.close();
                }
                if(psmtUserID!=null){
                	psmtUserID.close();
                }
                if(psmtDelete!=null){
                	psmtDelete.close();
                }
                if(psmtResumeExist!=null){
                	psmtResumeExist.close();
                }
                if(psmtChildExist!=null){
                	psmtChildExist.close();
                }
                if(psmtUserBalanceExist!=null){
                	psmtUserBalanceExist.close();
                }
                if(psmtChnlTrnsfrPendingTransactionExist!=null){
                	psmtChnlTrnsfrPendingTransactionExist.close();
                }
                if(psmtfocPendingTransactionExist!=null){
                	psmtfocPendingTransactionExist.close();
                }
                }
            catch (SQLException ex) {
                LOG.error("deleteOrSuspendChnlUsersInBulkForMsisdn", "Exception : " + ex);
            } catch (Exception e) {
                LOG.error("deleteOrSuspendChnlUsersInBulkForMsisdn", "Exception : " + e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: MSISDN:");
            	loggerValue.append(p_msisdn);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return invalidStringInDao;
    }

    /**
     * This method is used to Delete or Suspend the channel users on the basis
     * of Login ID Author Amit Singh Method
     * deleteOrSuspendChnlUsersInBulkForLoginID
     * 
     * @param p_con
     * @param p_loginID
     *            String
     * @param p_deleteOrSuspend
     * @param p_chlildExistList
     *            ArrayList
     * @param p_modifiedBy
     * @param p_countStr
     *            int
     * @return String
     * @throws BTSLBaseException
     */
    public boolean deleteOrSuspendChnlUsersInBulkForLoginID(Connection con, String p_loginID, String p_deleteOrSuspend, List p_chlildExistList, String p_modifiedBy, int p_countStr, Map prepareStatementMap) throws BTSLBaseException {
        final String methodName = "deleteOrSuspendChnlUsersInBulkForLoginID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(",p_deleteOrSuspend=");
        	loggerValue.append(p_deleteOrSuspend);
        	loggerValue.append(",p_chlildExistList=");
        	loggerValue.append(p_chlildExistList);
        	loggerValue.append(",p_modifiedBy=");
        	loggerValue.append(p_modifiedBy);
            LOG.debug(methodName, loggerValue);
        }
        
        Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        Boolean isLREnabled = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
        Boolean isUsrBtchDelAppvl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USR_BTCH_SUS_DEL_APRVL);
        
        ResultSet rsIsExist = null;
        ResultSet rsUserID = null;
        UserDAO userDAO = null;
        ChannelTransferDAO channelTransferDAO = null;
        boolean invalidStringInDao = false;
        final ChannelUserVO chnlUserVO = ChannelUserVO.getInstance();
        PreparedStatement psmtIsExist = null;
        PreparedStatement psmtUserID = null;
        PreparedStatement psmtDelete = null;
        final StringBuffer userIDQueryLoginId = new StringBuffer();
        final StringBuffer deleteQueryLoginId = new StringBuffer();
        final StringBuffer isExistQueryLoginId = new StringBuffer();

        try {

            // Preparing Prepared Statement for LOGIN_ID

//            psmtIsExist = (PreparedStatement) prepareStatementMap.get("psmtIsExist");
//            psmtUserID = (PreparedStatement) prepareStatementMap.get("psmtUserID");
//            psmtDelete = (PreparedStatement) prepareStatementMap.get("psmtDelete");
        	

            isExistQueryLoginId.append("SELECT login_id FROM users WHERE UPPER(login_id) = UPPER(?)");

            if (psmtIsExist == null) {
            	if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(isExistQueryLoginId);
        			LOG.debug(methodName, loggerValue);
        		}
                psmtIsExist = con.prepareStatement(isExistQueryLoginId.toString());
                prepareStatementMap.put("psmtIsExist", psmtIsExist);
            }
            userIDQueryLoginId.append("SELECT U.user_id , C.sequence_no, U.status,u.msisdn  FROM users U, categories C ");
            userIDQueryLoginId.append("WHERE UPPER(U.login_id) = UPPER(?) AND U.category_code=C.category_code AND UPPER(U.status) ");
            userIDQueryLoginId.append("NOT IN ('N','C') AND U.category_code=C.category_code ");

            if (psmtUserID == null) {
            	if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(userIDQueryLoginId);
        			LOG.debug(methodName, loggerValue);
        		}
                psmtUserID = con.prepareStatement(userIDQueryLoginId.toString());
                prepareStatementMap.put("psmtUserID", psmtUserID);
            }

            deleteQueryLoginId.append("UPDATE users SET status = UPPER(?), modified_by=?, modified_on=?, ");
            deleteQueryLoginId.append("previous_status=?, login_id = ? WHERE UPPER(user_id) = UPPER(?) and user_type='CHANNEL' ");

            if (psmtDelete == null) {
            	if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(deleteQueryLoginId);
        			LOG.debug(methodName, loggerValue);
        		}
                psmtDelete = con.prepareStatement(deleteQueryLoginId.toString());
                prepareStatementMap.put("psmtDelete", psmtDelete);
            }
            String userID;
            int updateCount = 0;

            // checking that login ID exists or not
            psmtIsExist.setString(1, p_loginID);
            rsIsExist = psmtIsExist.executeQuery();
            psmtIsExist.clearParameters();
            if (!rsIsExist.next()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID", "Mobile number does not exist " + p_loginID);
                }
                UnregisterChUsersFileProcessLog.log("User existence", "", "Login ID :" + p_loginID, p_countStr, "Login ID does not exist", "Fail", "");
                invalidStringInDao = true;
                if (rsIsExist != null) {
                    rsIsExist.close();
                }
            } else {
                final boolean isBalanceFlag = false;
                boolean isO2CPendingFlag = false;
                boolean isBatchFOCPendingFlag = false;
                boolean isChildFlag = false;
                boolean isC2SPending = false;

                psmtUserID.setString(1, p_loginID);
                rsUserID = psmtUserID.executeQuery();
                psmtUserID.clearParameters();
                userDAO = new UserDAO();
                channelTransferDAO = new ChannelTransferDAO();
                final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
                if (rsUserID.next()) {
                    userID = rsUserID.getString("user_id");

                    // suspension of cahnnel user without any check, i.e. change
                    // user status to 'S'
                    // updation by akanksha
                    if (p_deleteOrSuspend.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || p_deleteOrSuspend.equals(PretupsI.USER_STATUS_SUSPEND_BATCH) ||(p_deleteOrSuspend.equals(PretupsI.USER_STATUS_RESUME_REQUEST))) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID", "This is the request for suspend" + userID);
                        }
                        if (isUsrBtchDelAppvl && p_deleteOrSuspend.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                            psmtDelete.setString(1, PretupsI.USER_STATUS_SUSPEND_BATCH);
                        } else if(p_deleteOrSuspend.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)){
                            psmtDelete.setString(1, PretupsI.USER_STATUS_SUSPEND);
                        } else {
                            psmtDelete.setString(1, PretupsI.USER_STATUS_RESUMED);
                        }
                        psmtDelete.setString(2, p_modifiedBy);
                        psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
                        psmtDelete.setString(4, rsUserID.getString("status"));
                        psmtDelete.setString(5, p_loginID);
                        psmtDelete.setString(6, userID);
                        updateCount = psmtDelete.executeUpdate();
                        if (updateCount <= 0) {
                            invalidStringInDao = true;
                        }
                        psmtDelete.clearParameters();
                    }
                    /*
                     * deletion of cahnnel user, i.e. change user status to 'N'
                     * but with some checks as- a)Check whether the child user
                     * is active or not b)Check the balance of the deleted user
                     * c)Check for no O2C Transfer pending (closed and canceled
                     * Txn)
                     */
                    else {
                        isChildFlag = userDAO.isChildUserActive(con, userID);
                        if (isChildFlag) {
                            chnlUserVO.setUserID(userID);
                            chnlUserVO.setCategoryVO(new CategoryVO());
                            chnlUserVO.getCategoryVO().setCategorySequenceNumber(rsUserID.getInt("sequence_no"));
                            chnlUserVO.setLoginID(p_loginID);
                            chnlUserVO.setStatus(rsUserID.getString("status"));
                            chnlUserVO.getCategoryVO().setSequenceNumber(p_countStr);
                            chnlUserVO.setMsisdn(rsUserID.getString("msisdn"));
                            p_chlildExistList.add(chnlUserVO);
                            invalidStringInDao = true;
                            /*
                             * if(LOG.isDebugEnabled())
                             * LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID"
                             * ,
                             * "This user has childs down the hierarchy, so can't be deleted "
                             * +p_loginID);
                             * UnregisterChUsersFileProcessLog.log("CHILD EXISTS"
                             * ,userID,"Login ID : "+p_loginID,p_countStr,
                             * "Child exists for this user","Fail","");
                             * invalidStringInDao = true;
                             */} else {
                            /*
                             * isBalanceFlag = userDAO.isUserBalanceExist(con,
                             * userID);
                             * if (isBalanceFlag) {
                             * if (LOG.isDebugEnabled())
                             * LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID"
                             * ,
                             * "This user has some balance, so can't be deleted "
                             * + p_loginID);
                             * UnregisterChUsersFileProcessLog.log(
                             * "IS USER BALANCE EXISTS", userID, "Login ID : " +
                             * p_loginID, p_countStr, "User balance exists",
                             * "Fail", "");
                             * invalidStringInDao = true;
                             * } else {
                             */
                            // Checing O2C Pending transactions
                    			 if(!isUsrBtchDelAppvl){
                    				boolean isSOSPendingFlag = false;
                    				// Checking SOS Pending transactions
                    				if(isChannelSOSEnable){
	             				        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
	             				        isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, userID);
	                                 	}
                    				if(isSOSPendingFlag){
                                        LogFactory.printLog(methodName, "This user has pending SOS transactions, so can't be deleted " + p_loginID, LOG);
                                        UnregisterChUsersFileProcessLog.log("UNSETTLED SOS TRANSACTION EXISTS", userID, "Login ID : " + p_loginID, p_countStr,
                                            "Pending User's SOS transaction", "Fail", "");
                                        invalidStringInDao = true;
                                    }else{
                                    	boolean isLRPendingFlag = false;
                                    	// checking Pending Last recharge transaction
                                    	if(isLREnabled){
                                 		   UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
                                				UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
                                				userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(userID, con, false, null);
                                				if (userTrfCntVO!=null) 
                                					isLRPendingFlag = true;
                     					}
                        				if(isLRPendingFlag){
                                            LogFactory.printLog(methodName, "This user has pending Last Recharge transactions, so can't be deleted " + p_loginID, LOG);
                                            UnregisterChUsersFileProcessLog.log("UNSETTLED LAST RECHARGE TRANSACTION EXISTS", userID, "Login ID : " + p_loginID, p_countStr,
                                                "Pending User's Last recharge credit request transaction", "Fail", "");
                                            invalidStringInDao = true;
                                        }else{
	                                         isO2CPendingFlag = channelTransferDAO.isPendingTransactionExist(con, userID);
	                                         if (isO2CPendingFlag) {
	                                             if (LOG.isDebugEnabled()) {
	                                                 LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID", "This user has pending transactions, so can't be deleted " + p_loginID);
	                                             }
	                                             UnregisterChUsersFileProcessLog.log("IS PENDING TRANSACTION EXISTS", userID, "Login ID : " + p_loginID, p_countStr,
	                                                 "Pending User's transaction", "Fail", "");
	                                             invalidStringInDao = true;
	                                         } else {
	                                             // Checking Batch Foc Pending transactions
	                                             isBatchFOCPendingFlag = batchTransferDAO.isPendingTransactionExist(con, userID);
	                                             if (isBatchFOCPendingFlag) {
	                                                 if (LOG.isDebugEnabled()) {
	                                                     LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID", "This user has pending batch foc transactions, so can't be deleted " + p_loginID);
	                                                 }
	                                                 UnregisterChUsersFileProcessLog.log("IS PENDING BATCH FOC TRANSACTION EXISTS", userID, "Login ID : " + p_loginID, p_countStr,
	                                                     "Pending User's batch foc transaction", "Fail", "");
	                                                 invalidStringInDao = true;
	                                             } else {                                	
	                                             	isC2SPending=channelTransferDAO.isC2SPendingTransactionExist(con, userID);
	                                             	if(isC2SPending){
	                                             		if (LOG.isDebugEnabled()) {
	                                                         LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID", "This user has pending c2s transactions, so can't be deleted " + p_loginID);
	                                                     }
	                                                     UnregisterChUsersFileProcessLog.log("IS PENDING C2S TRANSACTION EXISTS", userID, "Login ID : " + p_loginID, p_countStr,
	                                                         "Pending User's c2s transaction", "Fail", "");
	                                                     invalidStringInDao = true;
	                                                 } else {
	                                                 if (LOG.isDebugEnabled()) {
	                                                     LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID", "This is the request for deletion" + p_loginID);
	                                                 }
	                                                 psmtDelete.setString(1, PretupsI.USER_STATUS_DELETED);
	                                                 psmtDelete.setString(2, p_modifiedBy);
	                                                 psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
	                                                 psmtDelete.setString(4, rsUserID.getString("status"));
	                                                 psmtDelete.setString(5, p_loginID);
	                                                 psmtDelete.setString(6, userID);
	                                                 updateCount = psmtDelete.executeUpdate();
	
	                                                 if (updateCount <= 0) {
	                                                     invalidStringInDao = true;
	                                                 }
	                                                 psmtDelete.clearParameters();
	                                             }
	                                            }// end of else
                                         } // end of else of O2C Condition
                                        }// end of else of LR condition
                                 		}// end of else of SOS condition
                    			 } else {
        							 if (LOG.isDebugEnabled()) {
        								 LOG.debug("deleteOrSuspendChnlUsersInBulkForLoginID", "This is the request for deletion" + p_loginID);
        							 }
        							 psmtDelete.setString(1, PretupsI.USER_STATUS_DELETED);
        							 psmtDelete.setString(2, p_modifiedBy);
        							 psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
        							 psmtDelete.setString(4, rsUserID.getString("status"));
        							 psmtDelete.setString(5, userID);
        							 psmtDelete.setString(6, userID);
        							 updateCount = psmtDelete.executeUpdate();
        							 if (updateCount <= 0) {
        								 invalidStringInDao = true;
        							 }
        							 psmtDelete.clearParameters();                    				 
                            } // end of else
                        }// end of else
                    } // end of else
                } // end of else
                  // } // end of if
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Login ID already deleted or suspended  " + p_loginID);
                    }
                    UnregisterChUsersFileProcessLog.log("User existence", "", "Login ID :" + p_loginID, p_countStr, "Login ID already deleted or suspended", "Fail", "");

                }
            } // end of else
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[deleteOrSuspendChnlUsersInBulkForLoginID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[deleteOrSuspendChnlUsersInBulkForLoginID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rsIsExist != null) {
                    rsIsExist.close();
                }
                if (rsUserID != null) {
                    rsUserID.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
//            try {
//                if (psmtIsExist != null) {
//                	psmtIsExist.close();
//                }
//            } catch (Exception e) {
//                LOG.errorTrace(methodName, e);
//            }
//            try {
//                if (psmtUserID != null) {
//                	psmtUserID.close();
//                }
//            } catch (Exception e) {
//                LOG.errorTrace(methodName, e);
//            }
//            try {
//                if (psmtDelete != null) {
//                	psmtDelete.close();
//                }
//            } catch (Exception e) {
//                LOG.errorTrace(methodName, e);
//            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: p_loginID:");
            	loggerValue.append(p_loginID);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return invalidStringInDao;

    }

    /**
     * Method for check is profileID associate whith the user or not Method
     * :userExistForTransferProfile
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_profileID
     *            String
     * @return selectCount int
     * @throws BTSLBaseException
     * 
     */
    public int userExistForTransferProfile(Connection p_con, String p_profileID) throws BTSLBaseException {
        final String methodName = "userExistForTransferProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileID=");
        	loggerValue.append(p_profileID);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer selectBuff = new StringBuffer("SELECT count(U.user_id)  FROM users U ,channel_users CU ");
        selectBuff.append("  WHERE U.user_id=CU.user_id AND CU.transfer_profile_id=? AND U.status <>'N' AND U.status <>'C'");
        final String selectQuery = selectBuff.toString();
        int selectCount = 0;
        try {
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_profileID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                selectCount = rs.getInt(1);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[userExistForTransferProfile]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[userExistForTransferProfile]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            	loggerValue.append("Exiting: selectCount:");
            	loggerValue.append(selectCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return selectCount;
    }

    /**
     * This method is used to Delete or Suspend the channel users Author Method
     * deleteOrSuspendChnlUsers
     * 
     * @param p_con
     * @param p_userID
     *            String
     * @param p_status
     *            String
     * @param p_modifiedBy
     * @param p_preStatus
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public boolean deleteOrSuspendChnlUsers(Connection p_con, String p_userID, String p_status, String p_modifiedBy, String p_preStatus) throws BTSLBaseException {
        final String methodName = "deleteOrSuspendChnlUsers";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
        	loggerValue.append(",p_modifiedBy=");
        	loggerValue.append(p_modifiedBy);
            LOG.debug(methodName, loggerValue);
        }
        boolean invalidStringInDao = false;
        int updateCount = 0;
        PreparedStatement psmt = null;
        try {
            final String suspendedQuery = "UPDATE users SET status = UPPER(?), modified_by=?, modified_on=?, previous_status=? WHERE UPPER(user_id) = UPPER(?)";

            final String deleteQuery = "UPDATE users SET status = UPPER(?), modified_by=?, modified_on=?, previous_status=?,login_id=user_id WHERE UPPER(user_id) = UPPER(?)";

            if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(p_status)) {
            	if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(suspendedQuery);
        			LOG.debug(methodName, loggerValue);
        		}
                psmt = p_con.prepareStatement(suspendedQuery);
                psmt.setString(1, PretupsI.USER_STATUS_SUSPEND);
                psmt.setString(2, p_modifiedBy);
                psmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
                psmt.setString(4, p_preStatus);
                psmt.setString(5, p_userID);
                updateCount = psmt.executeUpdate();
            } else {
            	if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(deleteQuery);
        			LOG.debug(methodName, loggerValue);
        		}
                psmt = p_con.prepareStatement(deleteQuery);
                psmt.setString(1, PretupsI.USER_STATUS_DELETED);
                psmt.setString(2, p_modifiedBy);
                psmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new Date()));
                psmt.setString(4, p_preStatus);
                psmt.setString(5, p_userID);
                updateCount = psmt.executeUpdate();
            }// end if

            if (updateCount <= 0) {
                invalidStringInDao = true;
            }
            psmt.clearParameters();
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[deleteOrSuspendChnlUsersInBulkForLoginID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[deleteOrSuspendChnlUsersInBulkForLoginID]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        return invalidStringInDao;
    }

    /**
     * Method loadChannelUserList. This method load the user details on the
     * basis of the category code and the domain code and the geographical
     * domain code here geographical domains are checked hierarchylyby the
     * connect by prior .
     * 
     * @param p_con
     *            Connection
     * @param p_userCategory
     *            String
     * @param p_domainCode
     *            String
     * @param p_zoneCode
     *            String
     * @param p_userName
     *            String
     * @param p_userID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    public ArrayList loadChannelUserList(Connection p_con, String p_userCategory, String p_domainCode, String p_zoneCode, String p_userName, String p_userID) throws BTSLBaseException {
        final String methodName = "loadChannelUserList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userCategory=");
        	loggerValue.append(p_userCategory);
        	loggerValue.append(",p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_zoneCode=");
        	loggerValue.append(p_zoneCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }
        final ArrayList userList = new ArrayList();
        // commented for DB2 OraclePreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        UserVO userVO = null;
               try {
            // commented for DB2 pstmtSelect = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
            pstmtSelect=channelUserWebQry.loadChannelUserListQry(p_con, p_userCategory, p_domainCode, p_userName, p_userID, p_zoneCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserName(rs.getString("user_name"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setOwnerName(rs.getString("owner_name"));
                userList.add(userVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserList]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserList]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.append("Exiting: userList.size():");
            	loggerValue.append(userList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userList;
    }

    /**
     * Method loadChannelUserListHierarchy. This method load the user details on
     * the basis of the category code and the domain code and the geographical
     * domain code here geographical domains are checked hierarchyly. This
     * method also checks user informaiton hierarchyly by the connect by prior
     * 
     * @param p_con
     *            Connection
     * @param p_userCategory
     *            String
     * @param p_domainCode
     *            String
     * @param p_zoneCode
     *            String
     * @param p_userName
     *            String
     * @param p_userID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    public ArrayList loadChannelUserListHierarchy(Connection p_con, String p_userCategory, String p_domainCode, String p_zoneCode, String p_userName, String p_userID) throws BTSLBaseException {
        final String methodName = "loadChannelUserListHierarchy";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userCategory=");
        	loggerValue.append(p_userCategory);
        	loggerValue.append(",p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_zoneCode=");
        	loggerValue.append(p_zoneCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }
        final ArrayList userList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        UserVO userVO = null;
       
        try {
        	 pstmtSelect = channelUserWebQry.loadChannelUserListHierarchyQry(p_con,p_domainCode,p_userCategory,p_userName,p_userID,p_zoneCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserName(rs.getString("user_name"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setLoginID(rs.getString("login_id"));
                userList.add(userVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserListHierarchy]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserListHierarchy]", "", "",
                "", loggerValue.toString());
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
            	loggerValue.append("Exiting: userList.size():");
            	loggerValue.append(userList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userList;
    }

    /**
     * Method loadCategoryUserHierarchy. This method load the list of the user
     * on the basis of the selected category in the selfInformation.jsp and
     * other logged-in user's details and entered user name
     * 
     * @author amit.singh
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_userName
     *            String
     * @param p_loginUserID
     *            String
     * @return userList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryUserHierarchy(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_loginUserID) throws BTSLBaseException {
        final String methodName = "loadCategoryUserHierarchy";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_loginUserID=");
        	loggerValue.append(p_loginUserID);
            LOG.debug(methodName, loggerValue);
        }
        final ArrayList userList = new ArrayList();
        // commented for DB2OraclePreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        UserVO userVO = null;
                try {
            // commented for DB2 pstmtSelect = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
           pstmtSelect=channelUserWebQry.loadCategoryUserHierarchyQry(p_con,p_networkCode,p_categoryCode,p_loginUserID,p_userName);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                userVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));

                userList.add(userVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUserHierarchy]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUserHierarchy]", "", "",
                "", loggerValue.toString());
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
            	loggerValue.append("Exiting: userList.size():");
            	loggerValue.append(userList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userList;
    }// end of loadCategoryUserHierarchy

    /**
     * Method loadUserPhoneDetailsList. This method load the user details from
     * the user_phones on the basis of the searched user.
     * 
     * @author amit.singh
     * @param p_con
     *            Connection
     * @param p_channelUserID
     *            String
     * @return userList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserPhoneDetailsList(Connection p_con, String p_channelUserID) throws BTSLBaseException {
        final String methodName = "loadUserPhoneDetailsList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_channelUserID=");
        	loggerValue.append(p_channelUserID);
            LOG.debug(methodName, loggerValue);
        }
        final ArrayList userList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;
        final StringBuffer strBuff = new StringBuffer(" SELECT UP.msisdn, UP.phone_language, U.employee_code, UP.primary_number ");
        strBuff.append("FROM user_phones UP, users U ");
        strBuff.append("WHERE U.user_id = ? AND UP.user_id = U.user_id ");
        strBuff.append("AND U.status IN ('Y','S','SR') ");

        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmtSelect.setString(i, p_channelUserID);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                channelUserVO =  ChannelUserVO.getInstance();
                userPhoneVO = UserPhoneVO.getInstance();

                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                channelUserVO.setEmpCode(rs.getString("employee_code"));

                userList.add(channelUserVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserPhoneDetailsList]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserPhoneDetailsList]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.append("Exiting: userList.size():");
            	loggerValue.append(userList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userList;
    }// end of loadUserPhoneDetailsList

    /**
     * Method :updateLanguage Method for Updating the the language info for the
     * user
     * 
     * @author Amit Singh
     * @param p_con
     *            java.sql.Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_modifiedOn
     *            String
     * @param p_modifiedBy
     *            String
     * @param p_userID
     *            String
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateLanguage(Connection p_con, ArrayList p_updatedList, Date p_modifiedOn, String p_modifiedBy, String p_userID) throws BTSLBaseException {
        final String methodName = "updateLanguage";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_updatedList.size()=");
        	loggerValue.append(p_updatedList.size());
        	loggerValue.append(",p_modifiedOn=");
        	loggerValue.append(p_modifiedOn);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_modifiedBy=");
        	loggerValue.append(p_modifiedBy);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement psmtUpdate = null;
        ChannelUserVO channelUserVO = null;
        int updateCount = 0;

        try {
            final StringBuffer strBuff = new StringBuffer("UPDATE user_phones SET country = ?, phone_language = ?, ");
            strBuff.append("modified_by = ?, modified_on = ? WHERE user_id=? AND msisdn = ?");
            final String strUpdate = strBuff.toString();

            psmtUpdate = p_con.prepareStatement(strUpdate);

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(strUpdate);
    			LOG.debug(methodName, loggerValue);
    		}
            for (int i = 0, j = p_updatedList.size(); i < j; i++) {
                channelUserVO = (ChannelUserVO) p_updatedList.get(i);
                updateCount = 0;

                psmtUpdate.setString(1, channelUserVO.getCountry());
                psmtUpdate.setString(2, channelUserVO.getLanguageCode());
                psmtUpdate.setString(3, p_modifiedBy);
                psmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                psmtUpdate.setString(5, p_userID);
                psmtUpdate.setString(6, channelUserVO.getUserPhoneVO().getMsisdn());

                updateCount = psmtUpdate.executeUpdate();

                if (updateCount <= 0) {
                    break;
                }

                psmtUpdate.clearParameters();

            }// end of for(int i = 0; i < p_updatedList.size(); i++)
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateLanguage]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateLanguage]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }// end of updateLanguage

    /**
     * Method :updateChnlUserDetails Method for Updating the channel user
     * details (this method used in self details modify)
     * 
     * @author ved.sharma
     * @param p_con
     *            java.sql.Connection
     * @param p_chnluserVO
     *            ChannelUserVO
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateChnlUserDetails(Connection p_con, ChannelUserVO p_chnluserVO) throws BTSLBaseException {
        final String methodName = "updateChnlUserDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: ");
        }
        // commented for DB2OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer("UPDATE channel_users SET contact_person =?, outlet_code = ?, ");
            strBuff.append(" suboutlet_code = ? WHERE user_id = ?");

            final String strUpdate = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(strUpdate);
    			LOG.debug(methodName, loggerValue);
    		}

            // commented for DB2 psmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(strUpdate);
            psmtUpdate = p_con.prepareStatement(strUpdate);
            int i = 0;
            // commented for DB2 psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmtUpdate.setString(i, p_chnluserVO.getContactPerson());
            ++i;
            psmtUpdate.setString(i, p_chnluserVO.getOutletCode());
            ++i;
            psmtUpdate.setString(i, p_chnluserVO.getSubOutletCode());
            ++i;
            psmtUpdate.setString(i, p_chnluserVO.getUserID());

            updateCount = psmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChnlUserDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateChnlUserDetails]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	loggerValue.append("p_chnluserVO.getUserID() :");
            	loggerValue.append(p_chnluserVO.getUserID());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }// end of updateChnlUserDetails

    /**
     * Method :updateUserPhonesPin Method for Updating phone pin (this method
     * used in self details modify)
     * 
     * @author ved.sharma
     * @param p_con
     *            java.sql.Connection
     * @param p_userPhoneList
     *            ArrayList
     * @return updateCount int
     * @throws BTSLBaseException
     */
    // public int updateUserPhonesPin(Connection p_con, ArrayList
    // p_userPhoneList)throws BTSLBaseException
    public void updateUserPhonesPin(Connection p_con, ArrayList p_userPhoneList) throws BTSLBaseException

    {
        final String methodName = "updateUserPhonesPin";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userPhoneList=");
        	loggerValue.append(p_userPhoneList);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement psmtUpdate = null;
        UserPhoneVO phoneVO = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE user_phones SET sms_pin=?, modified_by = ?,");
            strBuff.append(" modified_on = ?, pin_modified_on=?, invalid_pin_count=?  ,PIN_RESET = ? WHERE user_id=? AND msisdn=? ");
            final String strUpdate = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(strUpdate);
    			LOG.debug(methodName, loggerValue);
    		}
            psmtUpdate = p_con.prepareStatement(strUpdate);
            int k = 0;
            for (int i = 0, j = p_userPhoneList.size(); i < j; i++) {
                k = 0;
                phoneVO = (UserPhoneVO) p_userPhoneList.get(i);
                ++k;
                psmtUpdate.setString(k, phoneVO.getSmsPin());
                ++k;
                psmtUpdate.setString(k, phoneVO.getModifiedBy());
                ++k;
                psmtUpdate.setTimestamp(k, BTSLUtil.getTimestampFromUtilDate(phoneVO.getModifiedOn()));
                ++k;
                psmtUpdate.setTimestamp(k, BTSLUtil.getTimestampFromUtilDate(phoneVO.getPinModifiedOn()));
                ++k;
                psmtUpdate.setInt(k, phoneVO.getInvalidPinCount());
                ++k;
                psmtUpdate.setString(k, PretupsI.NO);
                ++k;
                psmtUpdate.setString(k, phoneVO.getUserId());
                ++k;
                psmtUpdate.setString(k, phoneVO.getMsisdn());

                updateCount = updateCount + psmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    break;
                }
                psmtUpdate.clearParameters();
            }// end of for(int i = 0, j = p_userPhoneList.size(); i < j ;
             // i++)
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserPhonesPin]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserPhonesPin]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	loggerValue.append("phoneVO.getUserID() :");
            	loggerValue.append(phoneVO.getUserId());
            	LOG.debug(methodName, loggerValue);
            }
        }        
    }

    /**
     * Method loadUsersForEnquiry. This method the loads the user list with
     * userID and UserName, for the search screen. status is checked on the
     * basis of the parameter p_isOnlyActiveUser
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_userName
     *            String
     * @param p_ownerUserID
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_loginUserID
     *            String
     * @param p_isOnlyActiveUser
     *            boolean
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersForEnquiry(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode, String p_loginUserID, boolean p_isOnlyActiveUser) throws BTSLBaseException {

        final String methodName = "loadUsersForEnquiry";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_ownerUserID=");
        	loggerValue.append(p_ownerUserID);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(",p_loginUserID=");
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(",p_isOnlyActiveUser=");
        	loggerValue.append(p_isOnlyActiveUser);
            LOG.debug(methodName, loggerValue);
        }

        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
                final ArrayList arrayList = new ArrayList();
        try {
            // commented for DB2 pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
           pstmt=channelUserWebQry.loadUsersForEnquiryQry(p_con, p_networkCode,p_categoryCode,
        		   p_loginUserID, p_userName,p_ownerUserID,p_isOnlyActiveUser,p_geographicalDomainCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("user_name")),SqlParameterEncoder.encodeParams( rs.getString("user_id"))));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForEnquiry]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForEnquiry]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: arrayList Size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return arrayList;
    }

    /**
     * Method loadUsersForBatchFOC. This method the loads the user list for
     * Batch FOC transfer
     * 
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @return LinkedHashMap
     * @throws BTSLBaseException
     * @author sandeep.goel
     */

    public LinkedHashMap loadUsersForBatchFOC(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate) throws BTSLBaseException {
        final String methodName = "loadUsersForBatchFOC";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }
        Boolean isTrfRuleUserLevelAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer receiverStatusAllowed = new StringBuffer();
        String[] catArray = null;
         if(p_categoryCode!=null && p_categoryCode.indexOf(",")>0) {
        	  catArray =p_categoryCode.split(",");
        	  if(catArray!=null) {
        		 for(int i=0;i<catArray.length;i++) {
        					 UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode,catArray[i].replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB);
        					  receiverStatusAllowed.append( "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'");
        					  if(i<=catArray.length-2) {
        						  receiverStatusAllowed.append(",");        					  }
        		 				}  
        		 }
        		  
        	  }
         else {
        	 
        	 UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode,p_categoryCode.replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB);
        	 receiverStatusAllowed.append("'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'");
         }
        
         
        		
        		
            
//        if (userStatusVO != null) {
//            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
//        } else {
//            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
//        }
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
             
             String sqlSelect=channelUserWebQry.loadUsersForBatchFOCQry(p_categoryCode, p_geographicalDomainCode, receiverStatusAllowed.toString());
             if(LOG.isDebugEnabled()){
     			loggerValue.setLength(0);
     			loggerValue.append("Query =");
     			loggerValue.append(sqlSelect);
     			LOG.debug(methodName, loggerValue);
     		}
            
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_domainCode);
            ++i;
            pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
            rs = pstmt.executeQuery();
            ChannelUserVO channelVO = null;
            while (rs.next()) {
                channelVO = ChannelUserVO.getInstance();
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setExternalCode(rs.getString("external_code"));

                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                if (isTrfRuleUserLevelAllow) {
                    channelVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                }
                linkedHashMap.put(channelVO.getMsisdn(), channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: HashMap Size:");
            	loggerValue.append(linkedHashMap.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return linkedHashMap;
    }

    /**
     * Method validateUsersForBatchFOC. This method the loads the user list for
     * Batch FOC transfer
     * 
     * @param p_con
     *            Connection
     * @param p_batchFOCItemsVOList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */

    public ArrayList validateUsersForBatchFOC(Connection p_con, ArrayList p_batchFOCItemsVOList, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate, MessageResources p_messages, Locale p_locale, ArrayList<FocListValueVO> focFileErrorList) throws BTSLBaseException {
        final String methodName = "validateUsersForBatchFOC";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(",p_batchFOCItemsVOList.size()=");
        	loggerValue.append(p_batchFOCItemsVOList.size());
            LOG.debug(methodName, loggerValue);
        }
        Boolean isPartialBatchAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED);
        Boolean isExternalCodeMandatoryForFOC = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORFOC);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;

        final String categoryCode = p_categoryCode.replaceAll("'", "");
        final String ss = categoryCode.replaceAll("\" ", "");
        final String m_categoryCode[] = ss.split(",");

        String m_receiverStatusAllowed[] = null;

        final String geographicalDomainCode = p_geographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String m_geographicalDomainCode[] = gg.split(",");
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_categoryCode.replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            // receiverStatusAllowed =
            // "'"+(userStatusVO.getUserReceiverAllowed()).replaceAll(",",
            // "','")+"'";

            receiverStatusAllowed = userStatusVO.getUserReceiverAllowed();
            final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
            final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
            m_receiverStatusAllowed = sa.split(",");
        } else {
            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
        }
        
        String sqlSelect = channelUserWebQry.validateUsersForBatchFOC(m_categoryCode, m_receiverStatusAllowed, m_geographicalDomainCode);
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        final ArrayList errorList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int index = 0;
            FOCBatchItemsVO focBatchItemVO = null;
            ListValueVO errorVO = null;
            boolean fileValidationErrorExists = false;
            for (int i = 0, j = p_batchFOCItemsVOList.size(); i < j; i++) {
                if (isPartialBatchAllowed) {
                    fileValidationErrorExists = false;
                }
                focBatchItemVO = (FOCBatchItemsVO) p_batchFOCItemsVOList.get(i);
                index = 0;
                ++index;
                pstmt.setString(index, focBatchItemVO.getMsisdn());
                ++index;
                pstmt.setString(index, p_networkCode);
                for (int x = 0; x < m_categoryCode.length; x++) {
                    ++index;
                    pstmt.setString(index, m_categoryCode[x]);
                }
                ++index;
                pstmt.setString(index, p_domainCode);
                for (int x = 0; x < m_receiverStatusAllowed.length; x++) {
                    ++index;
                    pstmt.setString(index, m_receiverStatusAllowed[x]);
                }
                for (int x = 0; x < m_geographicalDomainCode.length; x++) {
                    ++index;
                    pstmt.setString(index, m_geographicalDomainCode[x]);
                }
                ++index;
                pstmt.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
                rs = pstmt.executeQuery();
                pstmt.clearParameters();
                if (rs.next()) {
                    /*
                     * if(!PretupsI.YES.equals(rs.getString("status")))
                     * {
                     * //put error user is not active
                     * errorVO=new
                     * ListValueVO(focBatchItemVO.getMsisdn(),String.
                     * valueOf(focBatchItemVO
                     * .getRecordNumber()),p_messages.getMessage
                     * (p_locale,"batchfoc.processuploadedfile.error.usernotactive"
                     * ));
                     * errorList.add(errorVO);
                     * fileValidationErrorExists=true;
                     * BatchFocFileProcessLog.focBatchItemLog("processUploadedFile"
                     * ,focBatchItemVO,"FAIL : User is not active",
                     * "Batch FOC Initiate");
                     * if(isPartialBatchAllowed){
                     * addErrorList(errorVO,focBatchItemVO,focFileErrorList);
                     * }
                     * continue;
                     * }
                     */
                    if (!PretupsI.NO.equals(rs.getString("in_suspend"))) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.processuploadedfile.error.userinsuspend"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : User is IN suspended", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;

                    }
                    if (isExternalCodeMandatoryForFOC && !BTSLUtil.isNullString(focBatchItemVO.getExternalCode()) && !focBatchItemVO.getExternalCode()
                        .equals(rs.getString("external_code"))) {
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.processuploadedfile.error.externalcodeisinvalid"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : External code is invalid", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("profile_status"))) {
                        // put transfer profile is not active
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.processuploadedfile.error.trfprfsuspended"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : Transfer profile is suspended", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("commprofilestatus"))) {
                        // put commission profile is not active
                        // with reason
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(focBatchItemVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(focBatchItemVO.getRecordNumber()));
                        // ChangeID=LOCALEMASTER
                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_locale)).getMessage())) {
                            // reason is rs.getString("comprf_lang_1_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchfoc.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_1_msg") }));
                        } else {
                            // reason is rs.getString("comprf_lang_2_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchfoc.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_2_msg") }));
                        }
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : Commision profile is inactive", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }
                    if (BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate)) {
                        // no commission profile is associated till today.
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.processuploadedfile.error.nocomprfassociated"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : No commission profile is associated till today",
                            "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }

                    if (!fileValidationErrorExists) {
                        focBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
                        focBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
                        focBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
                        focBatchItemVO.setCategoryCode(rs.getString("category_code"));
                        focBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
                        focBatchItemVO.setUserId(rs.getString("user_id"));
                        focBatchItemVO.setDualCommissionType(rs.getString("dual_comm_type"));

                    } else {
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                    }
                } else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.processuploadedfile.error.msisdnnotfound"));
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, focBatchItemVO, focFileErrorList);

                    }
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : Msisdn not found", "Batch FOC Initiate");
                    continue;
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", null, "FAIL : SQL Exception:" + sqe.getMessage(), "Batch FOC Initiate");
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", null, "FAIL : Exception:" + ex.getMessage(), "Batch FOC Initiate");
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", null,
                "FINALLY BLOCK TOTAL RECORDS =" + p_batchFOCItemsVOList.size() + ", ERROR RECORDS = " + errorList.size(), "Batch FOC Initiate");
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: erroList Size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }
    
    
    
    /**
     * Method validateUsersForBatchFOCREST. This method the loads the user list for
     * Batch FOC transfer
     * 
     * @param p_con
     *            Connection
     * @param p_batchFOCItemsVOList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */

    public ArrayList validateUsersForBatchFOCREST(Connection p_con, ArrayList p_batchFOCItemsVOList, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate, Locale p_locale, ArrayList<FocListValueVO> focFileErrorList) throws BTSLBaseException {
        final String methodName = "validateUsersForBatchFOC";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(",p_batchFOCItemsVOList.size()=");
        	loggerValue.append(p_batchFOCItemsVOList.size());
            LOG.debug(methodName, loggerValue);
        }
        Boolean isPartialBatchAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED);
        Boolean isExternalCodeMandatoryForFOC = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORFOC);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
       

        final String categoryCode = p_categoryCode.replaceAll("'", "");
        final String ss = categoryCode.replaceAll("\" ", "");
        final String m_categoryCode[] = ss.split(",");

        String m_receiverStatusAllowed[] = null;

        final String geographicalDomainCode = p_geographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String m_geographicalDomainCode[] = gg.split(",");
//        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_categoryCode.replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,
//            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        
        StringBuffer receiverStatusAllowed = new StringBuffer();
        String[] catArray = null;
         if(p_categoryCode!=null && p_categoryCode.indexOf(",")>0) {
        	  catArray =p_categoryCode.split(",");
        	  if(catArray!=null) {
        		 for(int i=0;i<catArray.length;i++) {
        					 UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode,catArray[i].replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB);
        					 if(userStatusVO!=null) {
        					  receiverStatusAllowed.append( "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'");
        					  }
        					  if(i<=catArray.length-2) {
        						  receiverStatusAllowed.append(",");        					  }
        		 				}  
        		 }
        		  
        	  }
         else {
        	 UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode,p_categoryCode.replace("'", ""), PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB);
        	 receiverStatusAllowed.append("'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'");
         }
        
       
        
        /*
        if (userStatusVO != null) {
            // receiverStatusAllowed =
            // "'"+(userStatusVO.getUserReceiverAllowed()).replaceAll(",",
            // "','")+"'";

            receiverStatusAllowed = userStatusVO.getUserReceiverAllowed();
            final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
            final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
            m_receiverStatusAllowed = sa.split(",");
        } else {
            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
        }*/
         m_receiverStatusAllowed=receiverStatusAllowed.toString().split(",");
        String sqlSelect = channelUserWebQry.validateUsersForBatchFOC(m_categoryCode, m_receiverStatusAllowed, m_geographicalDomainCode);
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        final ArrayList errorList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int index = 0;
            FOCBatchItemsVO focBatchItemVO = null;
            ListValueVO errorVO = null;
            boolean fileValidationErrorExists = false;
            for (int i = 0, j = p_batchFOCItemsVOList.size(); i < j; i++) {
                if (isPartialBatchAllowed) {
                    fileValidationErrorExists = false;
                }
                focBatchItemVO = (FOCBatchItemsVO) p_batchFOCItemsVOList.get(i);
                index = 0;
                ++index;
                pstmt.setString(index, focBatchItemVO.getMsisdn());
                ++index;
                pstmt.setString(index, p_networkCode);
                for (int x = 0; x < m_categoryCode.length; x++) {
                    ++index;
                    pstmt.setString(index, m_categoryCode[x]);
                }
                ++index;
                pstmt.setString(index, p_domainCode);
                for (int x = 0; x < m_receiverStatusAllowed.length; x++) {
                    ++index;
                    pstmt.setString(index,  m_receiverStatusAllowed[x].replaceAll("'", ""));
                }
                for (int x = 0; x < m_geographicalDomainCode.length; x++) {
                    ++index;
                    pstmt.setString(index, m_geographicalDomainCode[x]);
                }
                ++index;
                pstmt.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
                rs = pstmt.executeQuery();
                pstmt.clearParameters();
                if (rs.next()) {
                    /*
                     * if(!PretupsI.YES.equals(rs.getString("status")))
                     * {
                     * //put error user is not active
                     * errorVO=new
                     * ListValueVO(focBatchItemVO.getMsisdn(),String.
                     * valueOf(focBatchItemVO
                     * .getRecordNumber()),p_messages.getMessage
                     * (p_locale,"batchfoc.processuploadedfile.error.usernotactive"
                     * ));
                     * errorList.add(errorVO);
                     * fileValidationErrorExists=true;
                     * BatchFocFileProcessLog.focBatchItemLog("processUploadedFile"
                     * ,focBatchItemVO,"FAIL : User is not active",
                     * "Batch FOC Initiate");
                     * if(isPartialBatchAllowed){
                     * addErrorList(errorVO,focBatchItemVO,focFileErrorList);
                     * }
                     * continue;
                     * }
                     */
                    if (!PretupsI.NO.equals(rs.getString("in_suspend"))) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.processuploadedfile.error.userinsuspend"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : User is IN suspended", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;

                    }
                    if (isExternalCodeMandatoryForFOC && !BTSLUtil.isNullString(focBatchItemVO.getExternalCode()) && !focBatchItemVO.getExternalCode()
                        .equals(rs.getString("external_code"))) {
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchfoc.processuploadedfile.error.externalcodeisinvalid"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : External code is invalid", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("profile_status"))) {
                        // put transfer profile is not active
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchfoc.processuploadedfile.error.trfprfsuspended"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : Transfer profile is suspended", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("commprofilestatus"))) {
                        // put commission profile is not active
                        // with reason
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(focBatchItemVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(focBatchItemVO.getRecordNumber()));
                        // ChangeID=LOCALEMASTER
                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_locale)).getMessage())) {
                            // reason is rs.getString("comprf_lang_1_msg")
                            errorVO.setOtherInfo2(PretupsRestUtil.getMessageString( "batchfoc.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_1_msg") }));
                        } else {
                            // reason is rs.getString("comprf_lang_2_msg")
                            errorVO.setOtherInfo2(PretupsRestUtil.getMessageString("batchfoc.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_2_msg") }));
                        }
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : Commision profile is inactive", "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }
                    if (BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate)) {
                        // no commission profile is associated till today.
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchfoc.processuploadedfile.error.nocomprfassociated"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : No commission profile is associated till today",
                            "Batch FOC Initiate");
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                        continue;
                    }

                    if (!fileValidationErrorExists) {
                        focBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
                        focBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
                        focBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
                        focBatchItemVO.setCategoryCode(rs.getString("category_code"));
                        focBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
                        focBatchItemVO.setUserId(rs.getString("user_id"));
                        focBatchItemVO.setDualCommissionType(rs.getString("dual_comm_type"));

                    } else {
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, focBatchItemVO, focFileErrorList);
                        }
                    }
                } else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.processuploadedfile.error.msisdnnotfound"));
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, focBatchItemVO, focFileErrorList);

                    }
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", focBatchItemVO, "FAIL : Msisdn not found", "Batch FOC Initiate");
                    continue;
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", null, "FAIL : SQL Exception:" + sqe.getMessage(), "Batch FOC Initiate");
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", null, "FAIL : Exception:" + ex.getMessage(), "Batch FOC Initiate");
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            BatchFocFileProcessLog.focBatchItemLog("processUploadedFile", null,
                "FINALLY BLOCK TOTAL RECORDS =" + p_batchFOCItemsVOList.size() + ", ERROR RECORDS = " + errorList.size(), "Batch FOC Initiate");
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: erroList Size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }
    
    
    

    private void addErrorList(ListValueVO errorVO, FOCBatchItemsVO focBatchItemVO, ArrayList<FocListValueVO> arrayFocListValueVO) {
        final String methodName = "addErrorList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: focBatchItemVO.getMsisdn()=");
        	loggerValue.append(focBatchItemVO.getMsisdn());
            LOG.debug(methodName, loggerValue);
        }
        FocListValueVO focListValueVO = new FocListValueVO();
        focListValueVO.setCodeName(errorVO.getCodeName());
        focListValueVO.setOtherInfo(errorVO.getOtherInfo());
        focListValueVO.setOtherInfo2(errorVO.getOtherInfo2());
        focListValueVO.setMsisdn(focBatchItemVO.getMsisdn());
        focListValueVO.setLoginID(focBatchItemVO.getLoginID());
        focListValueVO.setUserCategory(focBatchItemVO.getCategoryName());
        focListValueVO.setUserGrade(focBatchItemVO.getGradeName());
        focListValueVO.setExtTXNNumber(focBatchItemVO.getExtTxnNo());
        try {
            focListValueVO.setExtTXNDate(BTSLUtil.getDateTimeStringFromDate(focBatchItemVO.getExtTxnDate(), "dd-MM-yyyy"));
        } catch (ParseException e) {
            LOG.errorTrace(methodName, e);
        } catch (Exception e) {
            LOG.error(methodName, e);
        }
        focListValueVO.setExtCode(focBatchItemVO.getExternalCode());
       focListValueVO.setQuantity(focBatchItemVO.getRequestedQuantity());
        focListValueVO.setRemarks(errorVO.getOtherInfo2());
        //Added for File writing
		focListValueVO.setUserName(focBatchItemVO.getUserName());
		focListValueVO.setPointAction(focBatchItemVO.getPointAction());
        arrayFocListValueVO.add(focListValueVO);
        focListValueVO = null;
    }

    /**
     * method isExternalCodeExist
     * 
     * @param p_con
     * @param p_externalCode
     * @param p_userId
     * @return
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public boolean isExternalCodeExist(Connection p_con, String p_externalCode, String p_userId) throws BTSLBaseException {
        final String methodName = "isExternalCodeExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_externalCode=");
        	loggerValue.append(p_externalCode);
        	loggerValue.append(",p_userId=");
        	loggerValue.append(p_userId);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuffer strBuff = new StringBuffer();

        if (BTSLUtil.isNullString(p_userId)) {
            strBuff.append("SELECT 1 FROM users WHERE external_code= ? AND status <> 'N' AND status <> 'C'");
        } else {
            strBuff.append("SELECT 1 FROM users WHERE external_code=? and user_id != ? AND status <> 'N' AND status <> 'C'");
        }

        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            if (BTSLUtil.isNullString(p_userId)) {
                pstmt.setString(1, p_externalCode);
            } else {
                pstmt.setString(1, p_externalCode);
                pstmt.setString(2, p_userId);
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isExternalCodeExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isExternalCodeExist]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag:");
            	loggerValue.append(existFlag);
            	LOG.debug(methodName, loggerValue);
            }
        }
    }

    /**
     * Method validateUsersForBatch. This method the loads the user list for
     * Batch transfer
     * 
     * @param p_con
     *            Connection
     * @param p_batchFOCItemsVOList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */

    public ArrayList validateUsersForBatchC2C(Connection p_con, ArrayList p_batchC2CItemsVOList, String p_domainCode, String p_categoryCode, String p_networkCode, Date p_comPrfApplicableDate, MessageResources p_messages, Locale p_locale, String p_txnType) throws BTSLBaseException {
        final String methodName = "validateUsersForBatchC2C";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_batchC2CItemsVOList.size()=");
        	loggerValue.append(p_batchC2CItemsVOList.size());
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        String StatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache
            .getObject(p_networkCode, p_categoryCode, PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.endsWith(p_txnType)) {
                StatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                StatusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
        }
        
        String sqlSelect=channelUserWebQry.validateUsersForBatchC2CQry(p_categoryCode, StatusAllowed);
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        final ArrayList errorList = new ArrayList();
        try {
            // commented for DB2 pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            int index = 0;
            C2CBatchItemsVO c2cBatchItemVO = null;
            ListValueVO errorVO = null;
            //boolean fileValidationErrorExists = false;
            String msisdnOrLoginID = null;
            for (int i = 0, j = p_batchC2CItemsVOList.size(); i < j; i++) {
            	boolean fileValidationErrorExists = false;
                msisdnOrLoginID = null;
                c2cBatchItemVO = (C2CBatchItemsVO) p_batchC2CItemsVOList.get(i);
                index = 0;
                if (c2cBatchItemVO.getLoginID() != null) {
                    ++index;
                    pstmt.setString(index, c2cBatchItemVO.getLoginID());
                    msisdnOrLoginID = c2cBatchItemVO.getLoginID();
                } else {
                    ++index;
                }

                if (c2cBatchItemVO.getMsisdn() != null) {
                    ++index;
                    pstmt.setString(index, c2cBatchItemVO.getMsisdn());
                } else {
                    ++index;
                }

                if (msisdnOrLoginID.equals("")) {
                    msisdnOrLoginID = c2cBatchItemVO.getMsisdn();
                }
                ++index;
                pstmt.setString(index, p_networkCode);
                ++index;
                pstmt.setString(index, p_domainCode);
                ++index;
                pstmt.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
                LOG.debug(methodName, "QUERY sqlSelect=" + c2cBatchItemVO.getMsisdn() + p_networkCode + p_domainCode);
                rs = pstmt.executeQuery();
                pstmt.clearParameters();
                if (rs.next()) {
                    if (!BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id")) || !(c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn"))) || !(c2cBatchItemVO
                            .getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidlgidormsisdnorextCode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid Msisdn, Login ID  and External Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }
                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id")) || !(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidloginidorextCode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  Login ID and External Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn")) || !(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidmsisdnorextCode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid Msisdn ard External Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()) && !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil
                        .isNullString(c2cBatchItemVO.getLoginID())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id")) || !(c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn")))) {
                            errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidmsisdnorloginid"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  Login ID ard Msisdn Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidexternalcode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  External Code is required", "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()) && !BTSLUtil
                        .isNullString(c2cBatchItemVO.getMsisdn())) {
                        if (!c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn"))) {
                            errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidmsisdn"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid Msisdn is required", "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()) && BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil
                        .isNullString(c2cBatchItemVO.getLoginID())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id"))) {
                            errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidloginid"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  Login ID  is required", "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {

                        errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.nomsisdnloginidextcode"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL :  Msisdn, Login ID and External Code are required",
                            "Batch C2C Initiate");
                        continue;

                    }

                    /*
                     * if(!PretupsI.YES.equals(rs.getString("status")))
                     * {
                     * //put error user is not active
                     * errorVO=new
                     * ListValueVO(msisdnOrLoginID,String.valueOf(c2cBatchItemVO
                     * .getRecordNumber()),p_messages.getMessage(p_locale,
                     * "batchc2c.processuploadedfile.error.usernotactive"));
                     * errorList.add(errorVO);
                     * fileValidationErrorExists=true;
                     * BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile"
                     * ,c2cBatchItemVO,"FAIL : User is not active",
                     * "Batch C2C Initiate");
                     * continue;
                     * }
                     */
                    if (!PretupsI.NO.equals(rs.getString("in_suspend"))) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.userinsuspend"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : User is IN suspended", "Batch C2C Initiate");
                        continue;
                    }

                    if (!PretupsI.YES.equals(rs.getString("profile_status"))) {
                        // put transfer profile is not active
                        errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.trfprfsuspended"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Transfer profile is suspended", "Batch C2C Initiate");
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("commprofilestatus"))) {
                        // put commission profile is not active
                        // with reason
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(msisdnOrLoginID);
                        errorVO.setOtherInfo(String.valueOf(c2cBatchItemVO.getRecordNumber()));
                        // ChangeID=LOCALEMASTER
                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_locale)).getMessage())) {
                            // reason is rs.getString("comprf_lang_1_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchc2c.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_1_msg") }));
                        } else {
                            // reason is rs.getString("comprf_lang_2_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchc2c.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_2_msg") }));
                        }
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Commision profile is inactive", "Batch C2C Initiate");
                        continue;
                    }
                    if (BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate)) {
                        // no commission profile is associated till today.
                        errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.nocomprfassociated"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : No commission profile is associated till today",
                            "Batch C2C Initiate");
                        continue;
                    }
                    if (!fileValidationErrorExists) {
                        c2cBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
                        c2cBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
                        c2cBatchItemVO.setOthCommSetId(rs.getString("OTH_COMM_PRF_SET_ID"));
                        c2cBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
                        c2cBatchItemVO.setCategoryCode(rs.getString("category_code"));
                        c2cBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
                        c2cBatchItemVO.setUserId(rs.getString("user_id"));
                        c2cBatchItemVO.setDualCommissionType(rs.getString("dual_comm_type"));
                    }
                } else if (!c2cBatchItemVO.getLoginID().equals("") && !c2cBatchItemVO.getMsisdn().equals("")) {
                    errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.processuploadedfile.error.msisdnandloginidnotfound"));
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Msisdn and Login ID detail not found", "Batch C2C Initiate");
                    continue;
                } else if (!c2cBatchItemVO.getLoginID().equals("")) {
                    errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.processuploadedfile.error.loginidnotfound"));
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Login ID detail not found", "Batch C2C Initiate");
                    continue;
                } else {
                    errorVO = new ListValueVO(msisdnOrLoginID, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.processuploadedfile.error.msisdnnotfound"));
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Msisdn not datail found", "Batch C2C Initiate");
                    continue;
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchC2C]", "", "", "",
                loggerValue.toString());
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", null, "FAIL : SQL Exception:" + sqe.getMessage(), "Batch C2C Initiate");
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchC2C]", "", "", "",
                loggerValue.toString());
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", null, "FAIL : Exception:" + ex.getMessage(), "Batch C2C Initiate");
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", null,
                "FINALLY BLOCK TOTAL RECORDS =" + p_batchC2CItemsVOList.size() + ", ERROR RECORDS = " + errorList.size(), "Batch FOC Initiate");
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList Size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }

    /**
     * Method loadTransferredUserPrevHierarchy() This method loads the
     * transferred user hierarchy list.
     * 
     * @param p_con
     *            Connection
     * @param p_userID
     *            String[]
     * @param p_fromDate
     *            java.sql.Date
     * @param p_toDate
     *            java.sql.Date
     * @param p_isSearchOnDate
     *            boolean
     * @param p_mode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferredUserPrevHierarchy(Connection p_con, String p_userId[], Date p_fromDate, Date p_toDate, boolean p_isSearchOnDate, String p_mode) throws BTSLBaseException {
        final String methodName = "loadTransferredUserPrevHierarchy";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userId[0]=");
        	loggerValue.append(p_userId[0]);
        	loggerValue.append(",p_userId.length=");
        	loggerValue.append(p_userId.length);
        	loggerValue.append(",p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append(",p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append(",p_isSearchOnDate=");
        	loggerValue.append(p_isSearchOnDate);
        	loggerValue.append(",p_mode=");
        	loggerValue.append(p_mode);
            LOG.debug(methodName, loggerValue);
        }
        String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
        ArrayList arrayList = null;
        ChannelUserVO channelVO = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlSelect = null;
        final StringBuffer strBuff = new StringBuffer();
        try {
        	pstmt=channelUserWebQry.loadTransferredUserPrevHierarchyQry(p_con, p_mode, p_userId, p_isSearchOnDate, p_fromDate, p_toDate);
            
            rs = pstmt.executeQuery();
            arrayList = new ArrayList();
            while (rs.next()) {
                channelVO = ChannelUserVO.getInstance();
                channelVO.setUserlevel(rs.getString("l"));
                channelVO.setUserIDPrefix(rs.getString("user_id_prefix"));
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setCreatedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("MODIFIED_ON"), systemDateFormat)));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setParentName(rs.getString("parent_name"));
                channelVO.setStatusDesc(rs.getString("lookup_name"));
                channelVO.setReferenceID(rs.getString("reference_id"));
                channelVO.setPreviousBalance(rs.getLong("prev_balance"));
                channelVO.setPrevBalanceStr((PretupsBL.getDisplayAmount(rs.getLong("prev_balance"))));
                channelVO.setPrevUserId(rs.getString("prev_user_id"));
                channelVO.setPrevUserName(rs.getString("prev_user_name"));
                channelVO.setPrevParentName(rs.getString("prev_parent_name"));
                channelVO.setPrevCategoryCode(rs.getString("prev_cat_code"));

                arrayList.add(channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferredUserPrevHierarchy]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadTransferredUserPrevHierarchy]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.processing",ex);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: arrayList Size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return arrayList;
    }

    /**
     * Method to load Channel User Details by External Code
     * 
     * @param p_con
     * @param p_extCode
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList ValidateChnlUserDetailsByExtCode(Connection p_con, ArrayList p_batchC2CItemsVOList, Date p_comPrfApplicableDate, String p_extCode, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String methodName = "loadChnlUserDetailsByExtCode";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_extCode=");
        	loggerValue.append(p_extCode);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;

        final ArrayList errorList = new ArrayList();
        ResultSet rs = null;
        String selectQuery=channelUserWebQry.ValidateChnlUserDetailsByExtCodeQry();
               if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "select query:" + selectQuery);
        }

        try {
            C2CBatchItemsVO c2cBatchItemVO = null;
            ListValueVO errorVO = null;
           
            String externalCode = null;
            for (int i = 0, j = p_batchC2CItemsVOList.size(); i < j; i++) {

            	 boolean fileValidationErrorExists = false;
                c2cBatchItemVO = (C2CBatchItemsVO) p_batchC2CItemsVOList.get(i);
                pstmtSelect = p_con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, p_extCode);
                externalCode = c2cBatchItemVO.getExternalCode();
                pstmtSelect.setString(2, PretupsI.YES);
                pstmtSelect.setString(3, PretupsI.USER_STATUS_DELETED);
                pstmtSelect.setString(4, PretupsI.USER_STATUS_CANCELED);
                pstmtSelect.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    if (!BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id")) || !(c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn"))) || !(c2cBatchItemVO
                            .getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidlgidormsisdnorextCode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid Msisdn, Login ID  and External Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }
                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id")) || !(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidloginidorextCode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  Login ID and External Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn")) || !(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidmsisdnorextCode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid Msisdn ard External Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()) && !BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil
                        .isNullString(c2cBatchItemVO.getLoginID())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id")) || !(c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn")))) {
                            errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidmsisdnorloginid"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  Login ID ard Msisdn Code are required",
                                "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && !BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {
                        if (!(c2cBatchItemVO.getExternalCode().equals(rs.getString("external_code")))) {
                            errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.externalcode"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  External Code is required", "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()) && !BTSLUtil
                        .isNullString(c2cBatchItemVO.getMsisdn())) {
                        if (!c2cBatchItemVO.getMsisdn().equals(rs.getString("msisdn"))) {
                            errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidmsisdn"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid Msisdn is required", "Batch C2C Initiate");
                            continue;
                        }

                    }

                    else if (BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()) && BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && !BTSLUtil
                        .isNullString(c2cBatchItemVO.getLoginID())) {
                        if (!c2cBatchItemVO.getLoginID().equals(rs.getString("login_id"))) {
                            errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.processuploadedfile.error.invalidloginid"));
                            errorList.add(errorVO);
                            fileValidationErrorExists = true;
                            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Valid  Login ID  is required", "Batch C2C Initiate");
                            continue;
                        }

                    }

                    if (BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()) && BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()) && BTSLUtil.isNullString(c2cBatchItemVO
                        .getExternalCode())) {

                        errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.nomsisdnloginidextcode"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL :  Msisdn, Login ID and External Code is required",
                            "Batch C2C Initiate");
                        continue;

                    }

                    // user life cycle
                    boolean StatusAllowed = false;
                    String userStatusAllowed = null;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(rs.getString("network_code"), rs.getString("category_code"),
                        PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    if (userStatusVO != null) {
                        if (PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(c2cBatchItemVO.getTransferType())) {
                            userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                        } else {
                            userStatusAllowed = userStatusVO.getUserSenderAllowed();
                        }

                        final String status[] = userStatusAllowed.split(",");
                        //retrieving status of the user in batchitemvo for comparison with allowed status(s)
                        String userStatus = rs.getString("status");
                        for (int k = 0; k < status.length; k++) {
                            if (status[k].equals(userStatus)) {
                                StatusAllowed = true;
                            }

                        }
                    } else {
                        throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
                    }

                    if (!StatusAllowed) {
                        // put error user is not active
                        errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.usernotactive"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : User is not active", "Batch C2C Initiate");
                        continue;
                    }
                    if (!PretupsI.NO.equals(rs.getString("in_suspend"))) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.userinsuspend"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : User is IN suspended", "Batch C2C Initiate");
                        continue;
                    }

                    if (!PretupsI.YES.equals(rs.getString("profile_status"))) {
                        // put transfer profile is not active
                        errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.trfprfsuspended"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Transfer profile is suspended", "Batch C2C Initiate");
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("commprofilestatus"))) {
                        // put commission profile is not active
                        // with reason
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(externalCode);
                        errorVO.setOtherInfo(String.valueOf(c2cBatchItemVO.getRecordNumber()));
                        // ChangeID=LOCALEMASTER
                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_locale)).getMessage())) {
                            // reason is rs.getString("comprf_lang_1_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchc2c.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_1_msg") }));
                        } else {
                            // reason is rs.getString("comprf_lang_2_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchc2c.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_2_msg") }));
                        }
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : Commision profile is inactive", "Batch C2C Initiate");
                        continue;
                    }
                    if (BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate)) {
                        // no commission profile is associated till today.
                        errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.processuploadedfile.error.nocomprfassociated"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : No commission profile is associated till today",
                            "Batch C2C Initiate");
                        continue;
                    }
                    if (!fileValidationErrorExists) {
                        c2cBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
                        c2cBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
                        c2cBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
                        c2cBatchItemVO.setCategoryCode(rs.getString("category_code"));
                        c2cBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
                        c2cBatchItemVO.setUserId(rs.getString("user_id"));
                    }
                } else {
                    errorVO = new ListValueVO(externalCode, String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.processuploadedfile.error.extcodenotfound"));
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", c2cBatchItemVO, "FAIL : External Code datail not found", "Batch C2C Initiate");
                    continue;
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchC2C]", "", "", "",
                loggerValue.toString());
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", null, "FAIL : SQL Exception:" + sqe.getMessage(), "Batch C2C Initiate");
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchC2C]", "", "", "",
                loggerValue.toString());
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", null, "FAIL : Exception:" + ex.getMessage(), "Batch C2C Initiate");
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
            StringBuilder sb=new StringBuilder("");
            sb.append("FINALLY BLOCK TOTAL RECORDS =");
            sb.append(p_batchC2CItemsVOList.size());
            sb.append(", ERROR RECORDS = ");
            sb.append(errorList.size());
            BatchC2CFileProcessLog.c2cBatchItemLog("processUploadedFile", null,sb.toString(), "Batch FOC Initiate");
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList Size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }

    /**
     * load Category Users Within GeoDomain Hirearchy
     * 
     * @param p_con
     * @param p_categoryCode
     * @param p_networkCode
     * @param p_userName
     * @param p_loginUserID
     * @param p_isLoginChannelUsr
     *            TODO
     * @param p_ownerUserID
     * @param p_geographicalDomainCode
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryUsersWithinGeoDomainHirearchy(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_loginUserID, String p_userStatusIN, String p_msisdn, String p_loginID, boolean p_isLoginChannelUsr) throws BTSLBaseException {
        final String methodName = "loadCategoryUsersWithinGeoDomainHirearchy";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userStatusIN=");
        	loggerValue.append(p_userStatusIN);
        	loggerValue.append(",p_loginUserID=");
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(",p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(",p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(",p_isLoginChannelUsr=");
        	loggerValue.append(p_isLoginChannelUsr);
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        
        String sqlSelect = channelUserWebQry.loadCategoryUsersWithinGeoDomainHirearchy(p_userName,p_isLoginChannelUsr,p_loginID,p_msisdn,p_userStatusIN);
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            if (p_isLoginChannelUsr) {
                ++i;
                pstmt.setString(i, p_loginUserID);
            }
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_categoryCode);
            ++i;
            pstmt.setString(i, p_loginUserID);
            if (!BTSLUtil.isNullString(p_userName)) {
                ++i;
                pstmt.setString(i, p_userName);
            }
            if (!BTSLUtil.isNullString(p_loginID)) {
                ++i;
                pstmt.setString(i, p_loginID);
            }
            if (!BTSLUtil.isNullString(p_msisdn)) {
                ++i;
                pstmt.setString(i, p_msisdn);
            }
            rs = pstmt.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("user_name"), rs.getString("user_id"));
                listValueVO.setOtherInfo(rs.getString("msisdn"));
                arrayList.add(listValueVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: arrayList Size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return arrayList;
    }

    /**
     * Method debitUserBalancesForRevTxn() This method check user minimum
     * balance limits and Debit the user Balances if limit does not cross by the
     * new balance (existing balance-new requested credit balance)
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param isFromWeb
     *            boolean
     * @param p_forwardPath
     *            String
     * @return int
     * @throws BTSLBaseException
     */

    public int debitUserBalancesForRevTxn(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isFromWeb, String p_forwardPath) throws BTSLBaseException {
        final String methodName = "debitUserBalancesForRevTxn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_channelTransferVO=");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append(",fromWeb=");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(",p_forwardPath=");
        	loggerValue.append(p_forwardPath);
            LOG.debug(methodName, loggerValue);
        }
        int updateCount = 0;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement psmtUpdate = null;
        ResultSet rs = null;
        PreparedStatement psmtDeletUserThreshold = null;
     
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE ");
        strBuffUpdate.append(" user_id = ? and balance_type=?");
        strBuffUpdate.append(" AND ");
        strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        final String updateQuery = strBuffUpdate.toString();

        final String deletUserThreshold = channelUserWebQry.debitUserBalancesForRevTxnDeleteQry();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Update Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
        final String sqlSelect = channelUserWebQry.debitUserBalancesForRevTxnQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Select Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Delete Query =");
			loggerValue.append(deletUserThreshold);
			LOG.debug(methodName, loggerValue);
		}
        try {
            psmtUpdate = p_con.prepareStatement(updateQuery);
            psmtDeletUserThreshold = p_con.prepareStatement(deletUserThreshold);

            final ArrayList itemsList = p_channelTransferVO.getChannelTransferitemsVOList();

            final int maxAllowPct = 0;
            final long maxAllowBalance = 0;
            ChannelTransferItemsVO channelTransferItemsVO = null;
            String userID = null;
            String profileID = null;
            String userCode = null;
            userID = p_channelTransferVO.getToUserID();
            profileID = p_channelTransferVO.getSenderTxnProfile();
            userCode = p_channelTransferVO.getFromUserCode();
            boolean isNotToExecuteQuery = false;
            final ArrayList errorBalanceNotExistList = new ArrayList();
            final ArrayList errorBalanceLessList = new ArrayList();
            final ArrayList errorBalanceMinList = new ArrayList();
            final ArrayList errorList = new ArrayList();
            KeyArgumentVO keyArgumentVO = null;
            for (int i = 0, k = itemsList.size(); i < k; i++) {
                boolean toAddMoreError = true;
                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                pstmt = p_con.prepareStatement(sqlSelect);
                pstmt.setString(1, userID);
                pstmt.setString(2, channelTransferItemsVO.getProductCode());
                pstmt.setString(3, p_channelTransferVO.getNetworkCode());
                pstmt.setString(4, p_channelTransferVO.getNetworkCodeFor());
                rs = pstmt.executeQuery();
                long balance = -1;
                String balanceType = null;
                final long comm = channelTransferItemsVO.getCommQuantity();
                long bonusBalance = 0;
                boolean rsEmpty = true;
                boolean isMain = false;
                boolean isBonus = false;
                long unitprice = 1;
                final long unitValue = channelTransferItemsVO.getUnitValue();
                unitprice = getUnitPrice(unitprice, unitValue);
                while (rs.next()) {
                    rsEmpty = false;
                    balance = rs.getLong("balance");
                    balanceType = rs.getString("balance_type");

                    if (!balanceType.equalsIgnoreCase(defaultWallet)) {
                        bonusBalance = balance;
                        isBonus = true;
                    } else {
                        isMain = true;
                    }
                    long debitamt = channelTransferItemsVO.getReceiverCreditQty() * unitprice;
					 if(PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType()))
                    {
                    debitamt=debitamt+channelTransferItemsVO.getCommQuantity();
                    }

                    channelTransferItemsVO.setBalance(balance);

                    if ((isMain && balance < debitamt) || (isBonus && bonusBalance < comm) && toAddMoreError) {
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
                    channelTransferItemsVO.setPreviousBalance(balance);// set
                    // the
                    // previous
                    // balance
                    channelTransferItemsVO.setAfterTransSenderPreviousStock(balance);
                    if (balance > -1) {
                        if (isUserProductMultipleWallet) {

                            debitamt -= comm;
                            if (balanceType.equalsIgnoreCase(defaultWallet)) {
                                balance -= debitamt;
                            } else {
                                balance -= comm;
                            }

                        } else {
                            balance -= debitamt;
                        }
                    }
                    if (!isNotToExecuteQuery) {
                        int m = 0;
                        ++m;
                        psmtUpdate.setLong(m, balance);
                        ++m;
                        psmtUpdate.setString(m, p_channelTransferVO.getTransferType());
                        ++m;
                        psmtUpdate.setString(m, p_channelTransferVO.getTransferID());
                        ++m;
                        psmtUpdate.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getTransferDate()));
                        ++m;
                        psmtUpdate.setString(m, userID);
                        ++m;
                        psmtUpdate.setString(m, balanceType);
                        ++m;
                        psmtUpdate.setString(m, channelTransferItemsVO.getProductCode());
                        ++m;
                        psmtUpdate.setString(m, p_channelTransferVO.getNetworkCode());
                        ++m;
                        psmtUpdate.setString(m, p_channelTransferVO.getNetworkCodeFor());
                        updateCount = psmtUpdate.executeUpdate();
                        if (updateCount <= 0) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                                "ChannelUserDAO[debitUserBalancesForRevTxn]", "", "", "", "BTSLBaseException: update count <=0");
                            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                        }
                        psmtUpdate.clearParameters();
						//Set the value for sending the details into the SMS for Receiver
						p_channelTransferVO.setReceiverCrQty(debitamt);
						p_channelTransferVO.setReceiverPostStock(String.valueOf(balance));
						p_channelTransferVO.setProductCode(channelTransferItemsVO.getProductName());
					
                        final String TransferID = p_channelTransferVO.getTransferID();

                        getTransferID(psmtDeletUserThreshold, userID, m, TransferID);
                        
                        p_channelTransferVO.setRecieverPostBalance(balance);
                        
                    }
                }

                if (rsEmpty) {

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

            }
            if (isFromWeb) {
                if (!errorList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, errorList, p_forwardPath);
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
            p_channelTransferVO.setEntryType(PretupsI.DEBIT);
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForRevTxn]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForRevTxn]", "", "",
                "", loggerValue.toString());
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
                if (psmtDeletUserThreshold != null) {
                    psmtDeletUserThreshold.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }

    /**
     * @param methodName
     * @param unitprice
     * @param unitValue
     * @return
     */
    private long getUnitPrice(long unitprice, long unitValue) {
        final String methodName = "getUnitPrice";
        try {
            unitprice = Long.parseLong(PretupsBL.getDisplayAmount(unitValue));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        return unitprice;
    }

    /**
     * @param methodName
     * @param psmtDeletUserThreshold
     * @param userID
     * @param m
     * @param TransferID
     */
    private void getTransferID(PreparedStatement psmtDeletUserThreshold, String userID, int m, String TransferID) {
        final String methodName = "getTransferID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: psmtDeletUserThreshold=");
        	loggerValue.append(psmtDeletUserThreshold);
        	loggerValue.append(",userID=");
        	loggerValue.append(userID);
        	loggerValue.append(",TransferID=");
        	loggerValue.append(TransferID);
            LOG.debug(methodName, loggerValue);
        }
        try {
            psmtDeletUserThreshold.clearParameters();
            m = 0;
            ++m;
            psmtDeletUserThreshold.setString(m, TransferID);
            ++m;
            psmtDeletUserThreshold.setString(m, userID);
            psmtDeletUserThreshold.executeUpdate();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForRevTxn]",
                TransferID, "", userID, "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
        } catch (Exception sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[debitUserBalancesForRevTxn]",
                TransferID, "", userID, "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
        }
    }

    /**
     * Method creditUserBalancesForRevTxn() This method check user maximum
     * balance limits and Credit the user Balances if limit does not cross by
     * the new balance (existing balance+new requested credit balance)
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param isFromWeb
     *            boolean
     * @param p_forwardPath
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int creditUserBalancesForRevTxn(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isFromWeb, String p_forwardPath) throws BTSLBaseException {
        final String methodName = "creditUserBalancesForRevTxn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_channelTransferVO=");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append(",fromWeb=");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(",p_forwardPath=");
        	loggerValue.append(p_forwardPath);
            LOG.debug(methodName, loggerValue);
        }
        int updateCount = 0;
        PreparedStatement pstmt = null;
        PreparedStatement psmtUpdate = null;
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtDeletUserThreshold = null;
        ResultSet rs = null;
        
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        Boolean isMultipleWalletApply = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
        Boolean isProcessFeeRevAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROCESS_FEE_REV_ALLOWED);
        Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE ");
        strBuffUpdate.append(" user_id = ? and balance_type=?");
        strBuffUpdate.append(" AND ");
        strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");

        final StringBuffer strBuffInsert = new StringBuffer();
        strBuffInsert.append(" INSERT ");
        strBuffInsert.append(" INTO user_balances ");
        strBuffInsert.append(" ( prev_balance,daily_balance_updated_on , balance,  last_transfer_type, last_transfer_no, last_transfer_on , ");
        strBuffInsert.append(" user_id,balance_type, product_code , network_code, network_code_for ) ");
        strBuffInsert.append(" VALUES ");
        strBuffInsert.append(" (?,?,?,?,?,?,?,?,?,?) ");

        final String updateQuery = strBuffUpdate.toString();

        final String deletUserThreshold = channelUserWebQry.creditUserBalancesForRevTxnDeleteQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

        final String insertQuery = strBuffInsert.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(insertQuery);
			LOG.debug(methodName, loggerValue);
		}

        final String sqlSelect = channelUserWebQry.creditUserBalancesForRevTxnQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(deletUserThreshold);
			LOG.debug(methodName, loggerValue);
		}

        NetworkStockVO networkStockVO = null;

        try {

            pstmt = p_con.prepareStatement(sqlSelect);
            psmtUpdate = p_con.prepareStatement(updateQuery);
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtDeletUserThreshold = p_con.prepareStatement(deletUserThreshold);

            PreparedStatement handlerStmt = null;

            final TransferProfileProductVO transferProfileProductVO = null;
            final long maxBalance = 0;
            final ArrayList itemsList = p_channelTransferVO.getChannelTransferitemsVOList();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            String userID = null;

            userID = p_channelTransferVO.getFromUserID();

            final boolean isNotToExecuteQuery = false;
            final ArrayList errorList = new ArrayList();
            final KeyArgumentVO keyArgumentVO = null;

            final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
            ArrayList networkStockList = null;
            NetworkStockTxnItemsVO networkItemsVO = null;
            NetworkStockTxnVO networkStockTxnVO = null;
            ArrayList networkItemList = null;
            long approvedQty = 0;

            for (int i = 0, k = itemsList.size(); i < k; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);

                pstmt.setString(1, userID);
                pstmt.setString(2, channelTransferItemsVO.getProductCode());
                pstmt.setString(3, p_channelTransferVO.getNetworkCode());
                pstmt.setString(4, p_channelTransferVO.getNetworkCodeFor());

                rs = pstmt.executeQuery();
                long balance = -1;
                long comm = 0;
                String balanceType = null;
                long unitprice = 1;
                try {
                    unitprice = Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                Date date = new java.util.Date();
                while (rs.next()) {

                    balance = rs.getLong("balance");
                    balanceType = rs.getString("balance_type");
                    long creditqty = channelTransferItemsVO.getSenderDebitQty() * unitprice;
                    if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType()) && isProcessFeeRevAllowed == false) {
                        creditqty = creditqty - channelTransferItemsVO.getTax3Value();// tax
                        // on
                        // TDS
                    }
                    long creditamt = creditqty * unitprice;

                    if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {

                        long ntwkcrdtamt = 0;

                        if (isProcessFeeRevAllowed) {
                            ntwkcrdtamt = channelTransferItemsVO.getCommValue();
                        } else {
                                 ntwkcrdtamt = channelTransferItemsVO.getCommQuantity();
                     
                        }

                        networkStockVO = new NetworkStockVO();
                        networkStockVO.setProductCode(channelTransferItemsVO.getProductCode());
                        networkStockVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                        networkStockVO.setNetworkCodeFor(p_channelTransferVO.getNetworkCodeFor());
                        networkStockVO.setLastTxnNum(p_channelTransferVO.getTransferID());
                        networkStockVO.setLastTxnType(p_channelTransferVO.getTransferType());
                        networkStockVO.setModifiedBy(p_channelTransferVO.getModifiedBy());
                        networkStockVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(date));
                        if (isMultipleWalletApply) {
                        	networkStockVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);
                        } else {
                        	networkStockVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                        }

                        networkStockTxnVO =  NetworkStockTxnVO.getInstance();
                        networkStockTxnVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                        networkStockTxnVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
                        if (p_channelTransferVO.getNetworkCode().equals(p_channelTransferVO.getNetworkCodeFor())) {
                            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                        } else {
                            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                        }
                        networkStockTxnVO.setReferenceNo(p_channelTransferVO.getReferenceNum());
                        networkStockTxnVO.setTxnDate(BTSLUtil.getTimestampFromUtilDate(date));
                        networkStockTxnVO.setRequestedQuantity(channelTransferItemsVO.getCommQuantity());
                        networkStockTxnVO.setInitiaterRemarks(p_channelTransferVO.getChannelRemarks());
                        networkStockTxnVO.setFirstApprovedRemarks(p_channelTransferVO.getFirstApprovalRemark());
                        networkStockTxnVO.setSecondApprovedRemarks(p_channelTransferVO.getSecondApprovalRemark());
                        networkStockTxnVO.setFirstApprovedBy(p_channelTransferVO.getFirstApprovedBy());
                        networkStockTxnVO.setSecondApprovedBy(p_channelTransferVO.getSecondApprovedBy());
                        networkStockTxnVO.setFirstApprovedOn(p_channelTransferVO.getFirstApprovedOn());
                        networkStockTxnVO.setSecondApprovedOn(p_channelTransferVO.getSecondApprovedOn());
                        networkStockTxnVO.setCancelledBy(p_channelTransferVO.getCanceledBy());
                        networkStockTxnVO.setCancelledOn(p_channelTransferVO.getCanceledOn());
                        networkStockTxnVO.setCreatedBy(p_channelTransferVO.getModifiedBy());
                        networkStockTxnVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(date));
                        networkStockTxnVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(date));
                        networkStockTxnVO.setModifiedBy(p_channelTransferVO.getModifiedBy());
                        networkStockTxnVO.setTax3value(p_channelTransferVO.getTotalTax3());
                        networkStockTxnVO.setTxnStatus(p_channelTransferVO.getStatus());
                        networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID(p_con, networkStockTxnVO));
                        networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
                        networkStockTxnVO.setTxnType(PretupsI.CREDIT);
                        networkStockTxnVO.setInitiatedBy(p_channelTransferVO.getModifiedBy());
                        networkStockTxnVO.setFirstApproverLimit(p_channelTransferVO.getFirstApproverLimit());
                        networkStockTxnVO.setUserID(p_channelTransferVO.getFromUserID());
                        networkStockTxnVO.setTxnMrp(p_channelTransferVO.getTransferMRP());
                        networkItemList = new ArrayList();

                        networkItemsVO = new NetworkStockTxnItemsVO();
                        networkItemsVO.setSNo(i + 1);
                        networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                        networkItemsVO.setRequiredQuantity(channelTransferItemsVO.getRequiredQuantity());
                        networkItemsVO.setApprovedQuantity(channelTransferItemsVO.getRequiredQuantity());
                        networkItemsVO
                            .setMrp(channelTransferItemsVO.getRequiredQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue())));
                        networkItemsVO.setAmount(channelTransferItemsVO.getPayableAmount());
                        networkItemsVO.setDateTime(BTSLUtil.getTimestampFromUtilDate(date));
                        networkItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
                        approvedQty += channelTransferItemsVO.getRequiredQuantity();
                        networkItemList.add(networkItemsVO);

                        networkStockVO.setLastTxnBalance(ntwkcrdtamt);
                        networkStockVO.setWalletBalance(ntwkcrdtamt);
                        networkStockList = new ArrayList();
                        networkStockList.add(networkStockVO);
                        networkStockTxnVO.setNetworkStockTxnItemsList(networkItemList);
                        networkStockTxnVO.setApprovedQuantity(channelTransferItemsVO.getCommQuantity());
                        if (isMultipleWalletApply) {
                        	networkStockTxnVO.setTxnWallet(PretupsI.INCENTIVE_WALLET_TYPE);
                        } else {
                        	networkStockTxnVO.setTxnWallet(PretupsI.SALE_WALLET_TYPE);
                        }
                        networkStockTxnVO.setRefTxnID(p_channelTransferVO.getTransferID());
                        networkStockTxnVO.setInitiatedBy(p_channelTransferVO.getModifiedBy());
                        networkStockTxnVO.setFirstApproverLimit(p_channelTransferVO.getFirstApproverLimit());
                        networkStockTxnVO.setUserID(p_channelTransferVO.getFromUserID());
                        networkStockTxnVO.setTxnMrp(channelTransferItemsVO.getCommQuantity());
                        updateCount = networkStockDAO.creditNetworkStock(p_con, networkStockList);
                        updateCount = networkStockDAO.addNetworkStockTransaction(p_con, networkStockTxnVO);

                    }
                    if (balance > -1) {
                        channelTransferItemsVO.setPreviousBalance(balance);// used
                        // for
                        // send
                        // sms
                        channelTransferItemsVO.setAfterTransReceiverPreviousStock(balance);

                        if (isUserProductMultipleWallet) {
                        	if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())){
                        		creditamt=channelTransferItemsVO.getRequiredQuantity();
                        		comm=0;
                        	}
                        	else{
                        		comm = channelTransferItemsVO.getCommQuantity();
                                creditamt -= comm;
                        	}
                            
                            if (balanceType.equalsIgnoreCase(defaultWallet)) {
                                balance += creditamt;
                            } else {
                                balance += comm;
                            }

                        } else {
						  if(PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType()))
                        	{
                        		creditamt=creditamt-channelTransferItemsVO.getCommQuantity();
                        	}
                            balance += creditamt;
                        }

                    } else {
                        channelTransferItemsVO.setPreviousBalance(0);
                        channelTransferItemsVO.setAfterTransReceiverPreviousStock(0);
                    }
                    pstmt.clearParameters();

                    // in the case of return we have not to check the max
                    // balance

                    if (!isNotToExecuteQuery) {
                        int m = 0;
                        // update
                        if (balance > -1) {
                            handlerStmt = psmtUpdate;
                        } else {
                            // insert
                            handlerStmt = psmtInsert;
                            balance = channelTransferItemsVO.getRequiredQuantity();
                            m++;
                            handlerStmt.setLong(m, 0);// previous balance
                            m++;
                            handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                            // on
                            // date
                        }

                        m++;
                        handlerStmt.setLong(m, balance);
                        m++;
                        handlerStmt.setString(m, p_channelTransferVO.getTransferType());
                        m++;
                        handlerStmt.setString(m, p_channelTransferVO.getTransferID());// new
                        // transfer
                        // id
                        m++;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getTransferDate()));
                        m++;
                        handlerStmt.setString(m, userID);
                        m++;
                        handlerStmt.setString(m, balanceType);
                        // where
                        m++;
                        handlerStmt.setString(m, channelTransferItemsVO.getProductCode());
                        m++;
                        handlerStmt.setString(m, p_channelTransferVO.getNetworkCode());
                        m++;
                        handlerStmt.setString(m, p_channelTransferVO.getNetworkCodeFor());

                        updateCount = handlerStmt.executeUpdate();
                        handlerStmt.clearParameters();
                        p_channelTransferVO.setSenderPostbalance(balance);

                        if (updateCount <= 0) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                                "ChannelUserDAO[creditUserBalancesForRevTxn]", "", "", "", "BTSLBaseException: update count <=0");
                            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                        }
						//Set the value for sending the details into the SMS for Sender
						p_channelTransferVO.setSenderDrQty(creditamt);
						p_channelTransferVO.setSenderPostStock(String.valueOf(balance));
						p_channelTransferVO.setProductCode(channelTransferItemsVO.getProductName());
					
                        try {
                            psmtDeletUserThreshold.clearParameters();
                            m = 0;
                            ++m;
                            psmtDeletUserThreshold.setString(m, p_channelTransferVO.getTransferID());
                            ++m;
                            psmtDeletUserThreshold.setString(m, userID);
                            psmtDeletUserThreshold.executeUpdate();
                        } catch (SQLException sqle) {
                        	loggerValue.setLength(0);
                			loggerValue.append(SQL_EXCEPTION);
                			loggerValue.append(sqle.getMessage());
                			LOG.error(methodName, loggerValue);
                            LOG.errorTrace(methodName, sqle);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                "ChannelUserDAO[creditUserBalancesForRevTxn]", p_channelTransferVO.getTransferID(), "", userID,
                                "Error while updating user_threshold_counter table  SQL Exception:" + sqle.getMessage());
                        }

                    }// for

                }
            }
            if (!errorList.isEmpty()) {
                if (isFromWeb) {
                    throw new BTSLBaseException(this, methodName, errorList, p_forwardPath);
                }
                throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE, errorList);
            }

            p_channelTransferVO.setEntryType(PretupsI.CREDIT);
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[creditUserBalancesForRevTxn]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[creditUserBalancesForRevTxn]", "", "",
                "", loggerValue.toString());
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
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtDeletUserThreshold != null) {
                    psmtDeletUserThreshold.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_parentId
     *            String
     * @param p_status
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String loadStaffUsersDetails(Connection p_con, String p_msisdn, String p_parentId, String p_status) throws BTSLBaseException {
        final String methodName = "loadStaffUsersDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_parentId=");
        	loggerValue.append(p_parentId);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        String userId = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        int index = 1;
        try {
            final StringBuffer str = new StringBuffer(" SELECT U.USER_ID FROM USERS U, USER_PHONES UP WHERE UP.MSISDN=? AND ");
            str.append(" up.user_id=u.USER_ID  ");
            if (!BTSLUtil.isNullString(p_parentId)) {
                str.append("AND U.parent_id=? ");
            }
            str.append("AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");

            final String query = str.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            pstm = p_con.prepareStatement(query);
            pstm.setString(index, p_msisdn);
            index++;
            if (!BTSLUtil.isNullString(p_parentId)) {
                pstm.setString(index, p_parentId);
                index++;
            }
            pstm.setString(index, PretupsI.USER_TYPE_STAFF);
            index++;
            rst = pstm.executeQuery();
            if (rst.next()) {
                userId = rst.getString("USER_ID");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetails]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userId:");
            	loggerValue.append(userId);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userId;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_loginID
     *            String
     * @param p_parentId
     *            String
     * @param p_status
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String loadStaffUsersDetailsbyLoginID(Connection p_con, String p_loginID, String p_parentId, String p_status) throws BTSLBaseException {
        final String methodName = "loadStaffUsersDetailsbyLoginID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(",p_parentId=");
        	loggerValue.append(p_parentId);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        String userId = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        int index = 1;
        try {
            final StringBuffer str = new StringBuffer(" SELECT U.USER_ID FROM USERS U WHERE U.LOGIN_ID=? ");
            if (!BTSLUtil.isNullString(p_parentId)) {
                str.append(" AND U.parent_id=? ");
            }
            str.append("AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");

            final String query = str.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadStaffUsersDetailsbyLoginID ", "Query: " + query);
            }
            pstm = p_con.prepareStatement(query);
            pstm.setString(index, p_loginID);
            index++;
            if (!BTSLUtil.isNullString(p_parentId)) {
                pstm.setString(index, p_parentId);
            }
            pstm.setString(index, PretupsI.USER_TYPE_STAFF);
            rst = pstm.executeQuery();
            if (rst.next()) {
                userId = rst.getString("USER_ID");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetailsbyLoginID]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetailsbyLoginID]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: Userid:");
            	loggerValue.append(userId);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userId;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_parentId
     *            String
     * @param p_userType
     *            String
     * @param p_loginID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadStaffUserListByLogin(Connection p_con, String parentId, String userType, String loginID) throws BTSLBaseException {
        final String methodName = "loadStaffUserListByLogin";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginID=");
        	loggerValue.append(loginID);
        	loggerValue.append(",p_parentId=");
        	loggerValue.append(parentId);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstm = null;
        ResultSet rst = null;
        ArrayList userList = null;
        ChannelUserTransferVO channelUserTransferVO = null;
        try {
            final StringBuffer strBuff = new StringBuffer(" SELECT user_id, user_name, login_id, user_type, msisdn FROM users ");
            strBuff.append(" WHERE parent_id = ? and user_type=? AND status NOT IN('N','C') ");
            strBuff.append(" AND UPPER(login_id) LIKE UPPER(?) ");
            strBuff.append(" UNION ");
            strBuff.append(" SELECT user_id, user_name, login_id, user_type, msisdn FROM USERS ");
            strBuff.append("  WHERE user_id = ?  AND user_type=?  AND status NOT IN('N','C')  AND UPPER(login_id) LIKE UPPER(?) ");

            final String query = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            pstm = p_con.prepareStatement(query);
            pstm.setString(1, parentId);
            pstm.setString(2, userType);
            pstm.setString(3, loginID);
            pstm.setString(4, parentId);
            pstm.setString(5, PretupsI.USER_TYPE_CHANNEL);
            pstm.setString(6, loginID);
            rst = pstm.executeQuery();
            userList = new ArrayList();
            while (rst.next()) {
                channelUserTransferVO = new ChannelUserTransferVO();
                channelUserTransferVO.setUserID(SqlParameterEncoder.encodeParams(rst.getString("user_id")));
                channelUserTransferVO.setLoginId(SqlParameterEncoder.encodeParams(rst.getString("login_id")));
                userList.add(channelUserTransferVO);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUserListByLogin]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUserListByLogin]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userList.size:");
            	loggerValue.append(userList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userList;
    }

    /**
     * Method validateUsersForBatchDP. This method the loads the user list for
     * Batch Direct Payout transfer
     * 
     * @param p_con
     *            Connection
     * @param p_batchFOCItemsVOList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Lohit Audhkhasi
     */

    public ArrayList validateUsersForBatchDP(Connection p_con, ArrayList p_batchFOCItemsVOList, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String methodName = "validateUsersForBatchDP";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_batchFOCItemsVOList.size()=");
        	loggerValue.append(p_batchFOCItemsVOList.size());
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlSelect=channelUserWebQry.validateUsersForBatchDP(p_categoryCode, p_geographicalDomainCode);
        
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        final ArrayList errorList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int index = 0;
            FOCBatchItemsVO focBatchItemVO = null;
            ListValueVO errorVO = null;
            boolean fileValidationErrorExists = false;
            for (int i = 0, j = p_batchFOCItemsVOList.size(); i < j; i++) {
                focBatchItemVO = (FOCBatchItemsVO) p_batchFOCItemsVOList.get(i);
                index = 0;
                ++index;
                pstmt.setString(index, focBatchItemVO.getMsisdn());
                ++index;
                pstmt.setString(index, p_networkCode);
                ++index;
                pstmt.setString(index, p_domainCode);
                ++index;
                pstmt.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
                rs = pstmt.executeQuery();
                pstmt.clearParameters();
                if (rs.next()) {
                    if (!PretupsI.YES.equals(rs.getString("status"))) {
                        // put error user is not active
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.processuploadedfile.error.usernotactive"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is not active", "Batch Direct Payout Initiate");
                        continue;
                    }

                    if (!PretupsI.NO.equals(rs.getString("in_suspend"))) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.processuploadedfile.error.userinsuspend"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is IN suspended", "Batch Direct Payout Initiate");
                        continue;
                    }

                    if (!PretupsI.YES.equals(rs.getString("profile_status"))) {
                        // put transfer profile is not active
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.processuploadedfile.error.trfprfsuspended"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Transfer profile is suspended", "Batch Direct Payout Initiate");
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("commprofilestatus"))) {
                        // put commission profile is not active
                        // with reason
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(focBatchItemVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(focBatchItemVO.getRecordNumber()));
                        // ChangeID=LOCALEMASTER
                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_locale)).getMessage())) {
                            // reason is rs.getString("comprf_lang_1_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchdirectpayout.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_1_msg") }));
                        } else {
                            // reason is rs.getString("comprf_lang_2_msg")
                            errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchdirectpayout.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_2_msg") }));
                        }
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Commision profile is inactive", "Batch Direct Payout Initiate");
                        continue;
                    }
                    if (BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate)) {
                        // no commission profile is associated till today.
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.processuploadedfile.error.nocomprfassociated"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : No commission profile is associated till today",
                            "Batch Direct Payout Initiate");
                        continue;
                    }
                    if (!fileValidationErrorExists) {
                        focBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
                        focBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
                        focBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
                        focBatchItemVO.setCategoryCode(rs.getString("category_code"));
                        focBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
                        focBatchItemVO.setUserId(rs.getString("user_id"));
                        focBatchItemVO.setDualCommissionType(rs.getString("dual_comm_type"));
                    }
                } else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.processuploadedfile.error.msisdnnotfound"));
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Msisdn not found", "Batch Direct Payout Initiate");
                    continue;
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : SQL Exception:" + sqe.getMessage(), "Batch FOC Initiate");
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : Exception:" + ex.getMessage(), "Batch FOC Initiate");
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            DirectPayOutSuccessLog.dpBatchItemLog(methodName, null, "FINALLY BLOCK TOTAL RECORDS =" + p_batchFOCItemsVOList.size() + ", ERROR RECORDS = " + errorList.size(),
                "Batch Direct Payout Initiate");
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList.size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }
    
    
    
    /**
     * Method validateUsersForBatchDPREST. This method the loads the user list for
     * Batch Direct Payout transfer
     * 
     * @param p_con
     *            Connection
     * @param p_batchFOCItemsVOList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList validateUsersForBatchDPREST(Connection p_con, ArrayList p_batchFOCItemsVOList, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate, Locale p_locale) throws BTSLBaseException {
        final String methodName = "validateUsersForBatchDP";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_batchFOCItemsVOList.size()=");
        	loggerValue.append(p_batchFOCItemsVOList.size());
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlSelect=channelUserWebQry.validateUsersForBatchDP(p_categoryCode, p_geographicalDomainCode);
        
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        final ArrayList errorList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int index = 0;
            FOCBatchItemsVO focBatchItemVO = null;
            ListValueVO errorVO = null;
            boolean fileValidationErrorExists = false;
            for (int i = 0, j = p_batchFOCItemsVOList.size(); i < j; i++) {
                focBatchItemVO = (FOCBatchItemsVO) p_batchFOCItemsVOList.get(i);
                index = 0;
                ++index;
                pstmt.setString(index, focBatchItemVO.getMsisdn());
                ++index;
                pstmt.setString(index, p_networkCode);
                ++index;
                pstmt.setString(index, p_domainCode);
                ++index;
                pstmt.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
                rs = pstmt.executeQuery();
                pstmt.clearParameters();
                if (rs.next()) {
                    if (!PretupsI.YES.equals(rs.getString("status"))) {
                        // put error user is not active
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.processuploadedfile.error.usernotactive"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is not active", "Batch Direct Payout Initiate");
                        continue;
                    }

                    if (!PretupsI.NO.equals(rs.getString("in_suspend"))) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchdirectpayout.processuploadedfile.error.userinsuspend"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is IN suspended", "Batch Direct Payout Initiate");
                        continue;
                    }

                    if (!PretupsI.YES.equals(rs.getString("profile_status"))) {
                        // put transfer profile is not active
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.processuploadedfile.error.trfprfsuspended"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Transfer profile is suspended", "Batch Direct Payout Initiate");
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("commprofilestatus"))) {
                        // put commission profile is not active
                        // with reason
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(focBatchItemVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(focBatchItemVO.getRecordNumber()));
                        // ChangeID=LOCALEMASTER
                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_locale)).getMessage())) {
                            // reason is rs.getString("comprf_lang_1_msg")
                            errorVO.setOtherInfo2(PretupsRestUtil.getMessageString( "batchdirectpayout.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_1_msg") }));
                        } else {
                            // reason is rs.getString("comprf_lang_2_msg")
                            errorVO.setOtherInfo2(PretupsRestUtil.getMessageString( "batchdirectpayout.processuploadedfile.error.comprfinactive", new String[] { rs
                                .getString("comprf_lang_2_msg") }));
                        }
                        errorList.add(errorVO);
                        
                        
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Commision profile is inactive", "Batch Direct Payout Initiate");
                        continue;
                    }
                    if (BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate)) {
                        // no commission profile is associated till today.
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchdirectpayout.processuploadedfile.error.nocomprfassociated"));
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : No commission profile is associated till today",
                            "Batch Direct Payout Initiate");
                        continue;
                    }
                    if (!fileValidationErrorExists) {
                        focBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
                        focBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
                        focBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
                        focBatchItemVO.setCategoryCode(rs.getString("category_code"));
                        focBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
                        focBatchItemVO.setUserId(rs.getString("user_id"));
                        focBatchItemVO.setDualCommissionType(rs.getString("dual_comm_type"));
                    }
                } else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchdirectpayout.processuploadedfile.error.msisdnnotfound"));
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Msisdn not found", "Batch Direct Payout Initiate");
                    continue;
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : SQL Exception:" + sqe.getMessage(), "Batch FOC Initiate");
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : Exception:" + ex.getMessage(), "Batch FOC Initiate");
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            DirectPayOutSuccessLog.dpBatchItemLog(methodName, null, "FINALLY BLOCK TOTAL RECORDS =" + p_batchFOCItemsVOList.size() + ", ERROR RECORDS = " + errorList.size(),
                "Batch Direct Payout Initiate");
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList.size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }

    /**
     * Method loadUsersForBatchDP. This method the loads the user list for Batch
     * Direct Payout transfer
     * 
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @return LinkedHashMap
     * @throws BTSLBaseException
     * @author Lohit Audhkhasi
     */

    public LinkedHashMap loadUsersForBatchDP(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate) throws BTSLBaseException {
        final String methodName = "loadUsersForBatchDP";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }

        final String categoryCode = p_categoryCode.replaceAll("'", "");
        final String ss = categoryCode.replaceAll("\" ", "");
        final String m_categoryCode[] = ss.split(",");

        final String geographicalDomainCode = p_geographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String m_geographicalDomainCode[] = gg.split(",");

        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
         String sqlSelect = channelUserWebQry.loadUsersForBatchDP(m_categoryCode,m_geographicalDomainCode);
         if(LOG.isDebugEnabled()){
 			loggerValue.setLength(0);
 			loggerValue.append("Query =");
 			loggerValue.append(sqlSelect);
 			LOG.debug(methodName, loggerValue);
 		}
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, p_networkCode);
            for (int x = 0; x < m_categoryCode.length; x++) {
                ++i;
                pstmt.setString(i, m_categoryCode[x]);
            }
            ++i;
            pstmt.setString(i, p_domainCode);
            for (int x = 0; x < m_geographicalDomainCode.length; x++) {
                ++i;
                pstmt.setString(i, m_geographicalDomainCode[x]);
            }
            ++i;
            pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
            rs = pstmt.executeQuery();
            ChannelUserVO channelVO = null;
            while (rs.next()) {
                channelVO = ChannelUserVO.getInstance();

                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setExternalCode(rs.getString("external_code"));

                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                linkedHashMap.put(channelVO.getMsisdn(), channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: HashMap.size:");
            	loggerValue.append(linkedHashMap.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return linkedHashMap;
    }
    
    /**
     * 
     * @param p_con
     * @param p_domainCode
     * @param p_categoryCode
     * @param p_networkCode
     * @param p_geographicalDomainCode
     * @param p_comPrfApplicableDate
     * @return
     * @throws BTSLBaseException
     */
    public LinkedHashMap loadUsersForBatchDP(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate, String p_productCode) throws BTSLBaseException {
        final String methodName = "loadUsersForBatchDP";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }

        final String categoryCode = p_categoryCode.replaceAll("'", "");
        final String ss = categoryCode.replaceAll("\" ", "");
        final String m_categoryCode[] = ss.split(",");

        final String geographicalDomainCode = p_geographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String m_geographicalDomainCode[] = gg.split(",");

        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
         String sqlSelect = channelUserWebQry.loadUsersForBatchDP(m_categoryCode,m_geographicalDomainCode,p_productCode);
         if(LOG.isDebugEnabled()){
 			loggerValue.setLength(0);
 			loggerValue.append("Query =");
 			loggerValue.append(sqlSelect);
 			LOG.debug(methodName, loggerValue);
 		}
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, p_networkCode);
            for (int x = 0; x < m_categoryCode.length; x++) {
                ++i;
                pstmt.setString(i, m_categoryCode[x]);
            }
            ++i;
            pstmt.setString(i, p_domainCode);
            for (int x = 0; x < m_geographicalDomainCode.length; x++) {
                ++i;
                pstmt.setString(i, m_geographicalDomainCode[x]);
            }
            ++i;
            pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
            ++i;
            pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
			if (!BTSLUtil.isNullString(p_productCode)) {
				++i;
				pstmt.setString(i, p_productCode);
			}
            rs = pstmt.executeQuery();
            ChannelUserVO channelVO = null;
            while (rs.next()) {
                channelVO = ChannelUserVO.getInstance();

                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setExternalCode(rs.getString("external_code"));

                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                linkedHashMap.put(channelVO.getMsisdn(), channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchFOC]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: HashMap.size:");
            	loggerValue.append(linkedHashMap.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return linkedHashMap;
    }

    /**
     * Load balance of products those are associated with channel user.
     * 
     * @param p_userId
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public String loadChannelUserBalanceServiceWise(String p_userId, ArrayList<UserBalanceVO> userballist) throws BTSLBaseException {
        final String methodName = "loadChannelUserBalanceServiceWise";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
            LOG.debug(methodName, loggerValue);
        }
        
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        PreparedStatement pstmtSelect2 = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String showBalance = "";
        long bonusBalance = 0;
        long balance = 0;
        boolean mappingChecked = false;
        UserProductWalletMappingVO userProductWalletMappingVO = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            final StringBuffer selectQueryBuff = new StringBuffer(" SELECT distinct PS.SERVICE_TYPE,UB.BALANCE,P.PRODUCT_CODE,UB.balance_type,UB.network_code ");
            selectQueryBuff.append(" FROM USER_BALANCES UB, PRODUCT_SERVICE_TYPE_MAPPING PS,PRODUCTS P, USER_SERVICES US ");
            selectQueryBuff.append(" WHERE UB.user_id=? AND UB.user_id=US.user_id AND US.SERVICE_TYPE=PS.SERVICE_TYPE AND ");
            selectQueryBuff.append(" PS.PRODUCT_TYPE = P.PRODUCT_TYPE AND PS.PRODUCT_CODE=P.PRODUCT_CODE AND P.PRODUCT_CODE=UB.PRODUCT_CODE ");

            final StringBuffer selectQueryBuff1 = new StringBuffer(" SELECT balance,balance_type from user_balances where user_id=? ");

            final StringBuffer selectQueryBuff2 = new StringBuffer(" SELECT ub.balance,p.product_Name,p.product_Code ");
            										selectQueryBuff2.append("FROM User_Balances ub,Products p WHERE ub.product_Code=p.product_Code ");
            										selectQueryBuff2.append( "AND ub.user_Id = ? ");
            final String balanceQuery = selectQueryBuff1.toString();
            final String selectQuery = selectQueryBuff.toString();
            final String selectQuery1 = selectQueryBuff2.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect1 = con.prepareStatement(balanceQuery);
            pstmtSelect2 = con.prepareStatement(selectQuery1);
            pstmtSelect.setString(1, p_userId);
            pstmtSelect1.setString(1, p_userId);
            pstmtSelect2.setString(1, p_userId);
            if (isUserProductMultipleWallet) {
                rs1 = pstmtSelect1.executeQuery();
                while (rs1.next()) {
                    if (rs1.getString("balance_type").equals(defaultWallet)) {
                        balance = rs1.getLong("BALANCE");
                    } else {
                        bonusBalance = rs1.getLong("BALANCE");
                    }
                }
            }
            rs = pstmtSelect.executeQuery();
            StringBuffer show_bal=new StringBuffer("");
            while (rs.next()) {
                if (isUserProductMultipleWallet) {
                	
                    if (!mappingChecked) {
                        final List<UserProductWalletMappingVO> productMappingList = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(rs.getString("network_code"), rs
                            .getString("PRODUCT_CODE"));
                        userProductWalletMappingVO = productMappingList.get(0);
                        mappingChecked = true;

                        if (userProductWalletMappingVO.getPartialDedAlwd().equals("Y")) {
                            balance = balance + bonusBalance;
                        } else if (bonusBalance > balance) {
                            balance = bonusBalance;

                        }

                    }
                    
                    show_bal.setLength(0);
                    show_bal.append(showBalance);
                    show_bal.append(rs.getString("SERVICE_TYPE"));
                    show_bal.append(":");
                    show_bal.append(PretupsBL.getDisplayAmount(balance));
                    show_bal.append(",");
     
                    showBalance = show_bal.toString();

                } else {
                	show_bal.setLength(0);
                    show_bal.append(showBalance);
                    show_bal.append(rs.getString("SERVICE_TYPE"));
                    show_bal.append(":");
                    show_bal.append(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("BALANCE"))));
                    show_bal.append(",");
                    
                    showBalance = show_bal.toString();
                }
            }// end while
            if (!showBalance.equals("")) {
                showBalance = showBalance.substring(0, showBalance.length() - 1);
            }
            rs=pstmtSelect2.executeQuery();
            while(rs.next()) {
            	UserBalanceVO userBalanceVO = new UserBalanceVO();
            	userBalanceVO.setProductCode(rs.getString("product_Code"));
            	userBalanceVO.setProductName(rs.getString("product_Name"));
            	userBalanceVO.setBalance(PretupsBL.getDisplayAmount(rs.getDouble("balance")));
            	userballist.add(userBalanceVO);
            }
            
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserBalanceServiceWise]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ChannelUserDAO", methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserBalanceServiceWise]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException("ChannelUserDAO", methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
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
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
                if (pstmtSelect2 != null) {
                    pstmtSelect2.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(mcomCon != null)
            {
            	mcomCon.close("ChannelUserWebDAO#loadChannelUserBalanceServiceWise");
            	mcomCon=null;
            	}
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: showbalance:");
            	loggerValue.append(showBalance);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return showBalance;
    }
    /**
     * Load balance of products those are associated with channel user.
     * 
     * @param p_userId
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public String loadChannelUserBalanceServiceWise(String p_userId) throws BTSLBaseException {
        final String methodName = "loadChannelUserBalanceServiceWise";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_userId=");
        	loggerValue.append(p_userId);
            LOG.debug(methodName, loggerValue);
        }
        
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String showBalance = "";
        long bonusBalance = 0;
        long balance = 0;
        boolean mappingChecked = false;
        UserProductWalletMappingVO userProductWalletMappingVO = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            final StringBuffer selectQueryBuff = new StringBuffer(" SELECT distinct PS.SERVICE_TYPE,UB.BALANCE,P.PRODUCT_CODE,UB.balance_type,UB.network_code ");
            selectQueryBuff.append(" FROM USER_BALANCES UB, PRODUCT_SERVICE_TYPE_MAPPING PS,PRODUCTS P, USER_SERVICES US ");
            selectQueryBuff.append(" WHERE UB.user_id=? AND UB.user_id=US.user_id AND US.SERVICE_TYPE=PS.SERVICE_TYPE AND ");
            selectQueryBuff.append(" PS.PRODUCT_TYPE = P.PRODUCT_TYPE AND PS.PRODUCT_CODE=P.PRODUCT_CODE AND P.PRODUCT_CODE=UB.PRODUCT_CODE ");

            final StringBuffer selectQueryBuff1 = new StringBuffer(" SELECT balance,balance_type from user_balances where user_id=? ");

            final String balanceQuery = selectQueryBuff1.toString();
            final String selectQuery = selectQueryBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect1 = con.prepareStatement(balanceQuery);

            pstmtSelect.setString(1, p_userId);
            pstmtSelect1.setString(1, p_userId);

            if (isUserProductMultipleWallet) {
                rs1 = pstmtSelect1.executeQuery();
                while (rs1.next()) {
                    if (rs1.getString("balance_type").equals(defaultWallet)) {
                        balance = rs1.getLong("BALANCE");
                    } else {
                        bonusBalance = rs1.getLong("BALANCE");
                    }
                }
            }
            rs = pstmtSelect.executeQuery();
            StringBuffer show_bal=new StringBuffer("");
            while (rs.next()) {
                if (isUserProductMultipleWallet) {
                	
                    if (!mappingChecked) {
                        final List<UserProductWalletMappingVO> productMappingList = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(rs.getString("network_code"), rs
                            .getString("PRODUCT_CODE"));
                        userProductWalletMappingVO = productMappingList.get(0);
                        mappingChecked = true;

                        if (userProductWalletMappingVO.getPartialDedAlwd().equals("Y")) {
                            balance = balance + bonusBalance;
                        } else if (bonusBalance > balance) {
                            balance = bonusBalance;

                        }

                    }
                    
                    show_bal.setLength(0);
                    show_bal.append(showBalance);
                    show_bal.append(rs.getString("SERVICE_TYPE"));
                    show_bal.append(":");
                    show_bal.append(PretupsBL.getDisplayAmount(balance));
                    show_bal.append(",");
     
                    showBalance = show_bal.toString();

                } else {
                	show_bal.setLength(0);
                    show_bal.append(showBalance);
                    show_bal.append(rs.getString("SERVICE_TYPE"));
                    show_bal.append(":");
                    show_bal.append(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("BALANCE"))));
                    show_bal.append(",");
                    
                    showBalance = show_bal.toString();
                }
            }// end while
            if (!showBalance.equals("")) {
                showBalance = showBalance.substring(0, showBalance.length() - 1);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserBalanceServiceWise]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ChannelUserDAO", methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadChannelUserBalanceServiceWise]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException("ChannelUserDAO", methodName, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
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
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(mcomCon != null)
            {
            	mcomCon.close("ChannelUserWebDAO#loadChannelUserBalanceServiceWise");
            	mcomCon=null;
            	}
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: showbalance:");
            	loggerValue.append(showBalance);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return showBalance;
    }
    /**
     * @param p_con
     *            Connection
     * @param p_loginID
     *            String
     * @param p_parentId
     *            String
     * @param p_status
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String loadStaffUsersDetailsbyLoginIDforSuspend(Connection p_con, String p_loginID, String p_chusrid, String p_status) throws BTSLBaseException {
        final String methodName = "loadStaffUsersDetailsbyLoginIDforSuspend";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(",p_chusrid=");
        	loggerValue.append(p_chusrid);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        String userId = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        int index = 1;
        try {
            pstm= channelUserWebQry.loadStaffUsersDetailsbyLoginIDforSuspend(p_con, p_chusrid, p_loginID, p_status);
        	rst = pstm.executeQuery();
            if (rst.next()) {
                userId = rst.getString("USER_ID");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadStaffUsersDetailsbyLoginIDforSuspend]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadStaffUsersDetailsbyLoginIDforSuspend]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userId:");
            	loggerValue.append(userId);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userId;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_parentId
     *            String
     * @param p_status
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String loadStaffUsersDetailsForSuspend(Connection p_con, String p_msisdn, String p_chuserid, String p_status) throws BTSLBaseException {
        final String methodName = "loadStaffUsersDetailsForSuspend";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_chuserid=");
        	loggerValue.append(p_chuserid);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        String userId = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        int index = 1;
        try {
        	pstm = channelUserWebQry.loadStaffUsersDetailsForSuspend(p_con, p_status, p_msisdn, p_chuserid);
        	rst = pstm.executeQuery();
            if (rst.next()) {
                userId = rst.getString("USER_ID");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetailsForSuspend]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetailsForSuspend]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userId:");
            	loggerValue.append(userId);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userId;
    }

    public String loadStaffUsersDetailsReport(Connection p_con, String p_msisdn, String p_parentId, String p_status) throws BTSLBaseException {
        final String methodName = "loadStaffUsersDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_parentId=");
        	loggerValue.append(p_parentId);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        String userId = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        int index = 1;
        try {
            final StringBuffer str = new StringBuffer(" SELECT U.USER_ID FROM USERS U, USER_PHONES UP WHERE UP.MSISDN=? AND ");
            str.append(" up.user_id=u.USER_ID AND U.parent_id=? ");
            str.append("AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");
            str.append(" UNION ");
            str.append(" SELECT U.USER_ID FROM USERS U, USER_PHONES UP WHERE UP.MSISDN=? AND ");
            str.append(" up.user_id=u.USER_ID AND U.user_id=? ");
            str.append(" AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");

            final String query = str.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            pstm = p_con.prepareStatement(query);
            pstm.setString(index, p_msisdn);
            index++;
            pstm.setString(index, p_parentId);
            index++;
            pstm.setString(index, PretupsI.USER_TYPE_STAFF);
            index++;
            pstm.setString(index, p_msisdn);
            index++;
            pstm.setString(index, p_parentId);
            index++;
            pstm.setString(index, PretupsI.USER_TYPE_CHANNEL);
            index++;
            rst = pstm.executeQuery();
            if (rst.next()) {
                userId = rst.getString("USER_ID");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetails]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userId:");
            	loggerValue.append(userId);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userId;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_loginID
     *            String
     * @param p_parentId
     *            String
     * @param p_status
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String loadStaffUsersDetailsbyLoginIDReport(Connection p_con, String p_loginID, String p_parentId, String p_status) throws BTSLBaseException {
        final String methodName = "loadStaffUsersDetailsbyLoginID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(",p_parentId=");
        	loggerValue.append(p_parentId);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        String userId = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        int index = 1;
        try {
            final StringBuffer str = new StringBuffer(" SELECT U.USER_ID FROM USERS U WHERE U.LOGIN_ID=? ");
            str.append(" AND U.parent_id=? ");
            str.append(" AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");
            str.append(" UNION ");
            str.append(" SELECT U.USER_ID FROM USERS U WHERE U.LOGIN_ID=? AND U.USER_ID=? ");
            str.append(" AND u.user_type=? AND u.status NOT IN(" + p_status + ") ");

            final String query = str.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            pstm = p_con.prepareStatement(query);
            pstm.setString(index, p_loginID);
            index++;
            pstm.setString(index, p_parentId);
            index++;
            pstm.setString(index, PretupsI.USER_TYPE_STAFF);
            index++;
            pstm.setString(index, p_loginID);
            index++;
            pstm.setString(index, p_parentId);
            index++;
            pstm.setString(index, PretupsI.USER_TYPE_CHANNEL);
            index++;
            rst = pstm.executeQuery();
            if (rst.next()) {
                userId = rst.getString("USER_ID");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetailsbyLoginID]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUsersDetailsbyLoginID]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userId:");
            	loggerValue.append(userId);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userId;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_parentId
     *            String
     * @param p_userType
     *            String
     * @param p_loginID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadChannelUserDetailsByUserName(Connection p_con, String p_userName, String p_categoryCode, String p_loginUserId, String p_networkCode, String p_domainCode, String p_zoneCode) throws BTSLBaseException {
        final String methodName = "loadStaffUserListByLogin";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userName=");
        	loggerValue.append(p_userName);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstm = null;
        ResultSet rst = null;
        ArrayList userList = null;
        ChannelUserTransferVO channelUserTransferVO = null;
        try {
           String query=channelUserWebQry.loadChannelUserDetailsByUserNameQry();
           if(LOG.isDebugEnabled()){
   			loggerValue.setLength(0);
   			loggerValue.append("Query =");
   			loggerValue.append(query);
   			LOG.debug(methodName, loggerValue);
   		}
            pstm = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            pstm.setString(i, p_networkCode);
            ++i;
            pstm.setString(i, p_userName);
            ++i;
            pstm.setString(i, PretupsI.USER_TYPE_CHANNEL);
            ++i;
            pstm.setString(i, p_categoryCode);
            ++i;
            pstm.setString(i, p_zoneCode);
            ++i;
            pstm.setString(i, p_loginUserId);

            rst = pstm.executeQuery();
            userList = new ArrayList();
            while (rst.next()) {
                channelUserTransferVO = new ChannelUserTransferVO();
                channelUserTransferVO.setUserID(rst.getString("user_id"));
                channelUserTransferVO.setLoginId(rst.getString("login_id"));
                channelUserTransferVO.setUserName(rst.getString("user_name"));
                channelUserTransferVO.setMsisdn(rst.getString("msisdn"));
                userList.add(channelUserTransferVO);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUserListByLogin]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadStaffUserListByLogin]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userList:");
            	loggerValue.append(userList);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userList;
    }

    /**
     * Method loadUsersDetails. This method is used to load all the information
     * used to display in the ICCID MSISDN KEY MANAGEMENT Module
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_userID
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadUsersDetailsForStaff(Connection p_con, String p_msisdn, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadUsersDetailsForStaff";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_stausUsed=");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
        
        try {
           channelUserWebQry.loadUsersDetailsForStaff(p_con, p_status, p_statusUsed, p_userID, p_msisdn);
            rs = pstmtSelect.executeQuery();
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
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
                loggerValue.toString());
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

    /**
     * Method loadUsersDetailsByLoginId. This method is used to load all the
     * information of the user on the basis of LoginId
     * 
     * @author mohit.goel
     * @param p_con
     *            Connection
     * @param p_loginId
     *            String
     * @param p_userID
     *            String(If operator user userId = null else userId = session
     *            user id(in case of channel user)
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * 
     * 
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadUsersDetailsByLoginIdForStaff(Connection p_con, String p_loginId, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadUsersDetailsByLoginIdForStaff";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(p_loginId);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_stausUsed=");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
       
        try {
            
            pstmtSelect=channelUserWebQry.loadUsersDetailsByLoginIdForStaff(p_con,p_status,p_statusUsed,p_userID,p_loginId);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));

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
                channelUserVO.setCategoryVO(categoryVO);

                channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
                channelUserVO.setParentCategoryName(rs.getString("parent_cat"));

                channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                channelUserVO.setOwnerCategoryName(rs.getString("owner_cat"));

                channelUserVO.setStatusDesc(rs.getString("lookup_name"));
                channelUserVO.setRequetedByUserName(rs.getString("request_user_name"));
                channelUserVO.setSuspendedByUserName(rs.getString("request_user_name"));
                channelUserVO.setCreatedByUserName(rs.getString("created_by_name"));
                channelUserVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn()));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsByLoginId]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsByLoginId]", "",
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

    public ChannelUserVO loadUsersDetailsForC2C(Connection p_con, String p_msisdn, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadUsersDetailsForC2C";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_stausUsed=");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;

         String sqlSelect = channelUserWebQry.loadUsersDetailsForC2C(p_status, p_statusUsed);
         if(LOG.isDebugEnabled()){
 			loggerValue.setLength(0);
 			loggerValue.append("Query =");
 			loggerValue.append(sqlSelect);
 			LOG.debug(methodName, loggerValue);
 		}
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, p_msisdn);
            i++;
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmtSelect.setString(i, p_status);
                i++;
            }
            pstmtSelect.setString(i, PretupsI.USER_STATUS_TYPE);
            i++;
           
            pstmtSelect.setString(i, p_userID);
            i++;
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsForC2C]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetailsForC2C]", "",
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

    /**
     * load email id of channel user
     * 
     * @param p_con
     * @param p_userID
     * @return String
     * @throws BTSLBaseException
     * @author Nilesh kumar
     */
    public String loadUserEmail(Connection p_con, String p_userID) throws BTSLBaseException {
        final String methodName = "loadUserEmail";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_userID=");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }
        String email = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        final int index = 1;
        try {
            final StringBuffer str = new StringBuffer(" SELECT email FROM users WHERE user_id=? ");

            final String query = str.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(query);
    			LOG.debug(methodName, loggerValue);
    		}
            pstm = p_con.prepareStatement(query);
            pstm.setString(index, p_userID);
            rst = pstm.executeQuery();
            if (rst.next()) {
                email = rst.getString("EMAIL");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserEmail]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserEmail]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: email:");
            	loggerValue.append(email);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return email;
    }

    /**
     * Method updateAlertMsisdn. This method load channel user's Alert MSISDN
     * for UserListing Template.
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author vikas.jauhari Updated by Harpreet for Alert Type
     */

    public ArrayList updateAlertMsisdn(Connection p_con, ArrayList p_fileDataList, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        final int updateRecord = 0;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ChannelUserVO filedata = null;
        String userId = null;
        String alertMsisdn = null;
        String alertType = null;
        String alertEmail = null;

        final String methodName = "updateAlertMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fileDataList.size()=");
        	loggerValue.append(p_fileDataList.size());
            LOG.debug(methodName, loggerValue);
        }
        try {

            if (p_fileDataList != null) {
                errorList = new ArrayList();
                final String updateQuery = "UPDATE channel_users SET alert_msisdn=? , alert_Type=? ,alert_Email=? ,low_bal_alert_allow=? WHERE user_id=?";
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateQuery);
        			LOG.debug(methodName, loggerValue);
        		}

                psmtUpdate = p_con.prepareStatement(updateQuery);

                for (int i = 0, j = p_fileDataList.size(); i < j; i++) {
                    filedata = (ChannelUserVO) p_fileDataList.get(i);
                    userId = filedata.getUserID();
                    alertMsisdn = filedata.getAlertMsisdn();
                    alertType = filedata.getAlertType();
                    alertEmail = filedata.getAlertEmail();
                    psmtUpdate.setString(1, alertMsisdn);
                    psmtUpdate.setString(2, alertType);
                    // Added for Email
                    psmtUpdate.setString(3, alertEmail);
                    if(alertType!=null){
                    	 psmtUpdate.setString(4, "Y");
                    }else{
                    	 psmtUpdate.setString(4, "N");
                    }
                    
                    psmtUpdate.setString(5, userId);
                    updateCount = psmtUpdate.executeUpdate();
                    if (updateCount == 0) {
                        errorVO = new ListValueVO(filedata.getRecordNumber(), p_messages.getMessage(p_locale, "channel.processUploadedFile.error.updateusertable",
                            new String[] { filedata.getUserID() }));
                        errorList.add(errorVO);
                    }

                }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdn]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdn]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateRecord:");
            	loggerValue.append(updateRecord);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;

    }

    /**
     * Method updateAlertMsisdnTemplate. This method load channel user's Alert
     * MSISDN for Sample Template.
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author vikas.jauhari Updated by Harpreet for Alert Type
     */
    public ArrayList updateAlertMsisdnTemplate(Connection p_con, ArrayList p_fileDataList, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        PreparedStatement psmtSelect = null;
        int updateCount = 0;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ChannelUserVO filedata = null;
        String Msisdn = null;
        String alertMsisdn = null;
        String alertType = null;
        ResultSet rs = null;
        String userId = null;
        String alertEmail = null;

        final String methodName = "updateAlertMsisdnTemplate";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fileDataList.size()=");
        	loggerValue.append(p_fileDataList.size());
            LOG.debug(methodName, loggerValue);
        }
        try {
            errorList = new ArrayList();
            if (p_fileDataList != null) {
                final String selectQuery = "select up.user_id user_id from USER_PHONES up, users u where	u.user_id=up.user_id AND up.PRIMARY_NUMBER='Y' AND u.STATUS ='Y' AND up.MSISDN=?";
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Query selectQuery:" + selectQuery);
                }
                final String updateQuery = "UPDATE channel_users SET alert_msisdn=? ,alert_type=? ,alert_email=?, low_bal_alert_allow=? WHERE user_id=?";
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateQuery);
        			LOG.debug(methodName, loggerValue);
        		}
                psmtSelect = p_con.prepareStatement(selectQuery);
                psmtUpdate = p_con.prepareStatement(updateQuery);

                for (int i = 0, j = p_fileDataList.size(); i < j; i++) {
                    filedata = (ChannelUserVO) p_fileDataList.get(i);
                    Msisdn = filedata.getMsisdn();
                    alertMsisdn = filedata.getAlertMsisdn();
                    alertType = filedata.getAlertType();
                    alertEmail = filedata.getAlertEmail();
                    psmtSelect.clearParameters();
                    psmtSelect.setString(1, Msisdn);
                    rs = psmtSelect.executeQuery();
                    if (rs.next()) {
                        filedata.setUserID(rs.getString("user_id"));
                    } else {
                        errorVO = new ListValueVO(filedata.getRecordNumber(), p_messages.getMessage(p_locale, "channel.processUploadedFile.error.useridnotexist",
                            new String[] { filedata.getMsisdn() }));
                        errorList.add(errorVO);
                        continue;
                    }
                }
                if (errorList.isEmpty()) {
                    for (int i = 0, j = p_fileDataList.size(); i < j; i++) {
                        filedata = (ChannelUserVO) p_fileDataList.get(i);
                        userId = filedata.getUserID();
                        alertMsisdn = filedata.getAlertMsisdn();
                        psmtUpdate.setString(1, alertMsisdn);
                        psmtUpdate.setString(2, filedata.getAlertType());
                        psmtUpdate.setString(3, filedata.getAlertEmail());
                        psmtUpdate.setString(4, filedata.getLowBalAlertAllow());
                        psmtUpdate.setString(5, userId);
                        updateCount = psmtUpdate.executeUpdate();
                        if (updateCount == 0) {
                            errorVO = new ListValueVO(filedata.getRecordNumber(), p_messages.getMessage(p_locale, "channel.processUploadedFile.error.updateusertable",
                                new String[] { filedata.getMsisdn() }));
                            errorList.add(errorVO);
                        }

                    }
                }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdnTemplate]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdnTemplate]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
                if (psmtSelect != null) {
                    psmtSelect.close();
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }

    /**
     * As discussed with ved sir a new Method similar to existing method
     * loadUserHierarchyList
     * 
     * @param p_con
     * @param p_userId
     * @param p_mode
     * @param p_statusUsed
     * @param p_status
     * @param p_userCategory
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public ArrayList loadUserHierarchyListForTransfer(Connection p_con, String p_userId[], String p_mode, String p_statusUsed, String p_status, String p_userCategory) throws BTSLBaseException {
        final String methodName = "loadUserHierarchyListForTransfer";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userId[0]=");
        	loggerValue.append(p_userId[0]);
        	loggerValue.append(",p_userId.length=");
        	loggerValue.append(p_userId.length);
        	loggerValue.append(",p_mode=");
        	loggerValue.append(p_mode);
        	loggerValue.append(",p_statusUsed=");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
        	loggerValue.append(",p_userCategory=");
        	loggerValue.append(p_userCategory);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int maxLevel = 0;
        int tempLevel = 0;
        ChannelUserVO channelUserVO = null;
        ChannelUserDAO channelUserDAO = null;
        final ArrayList userDetailList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer();
        try {
            channelUserDAO = new ChannelUserDAO();
           pstmt=channelUserWebQry.loadUserHierarchyListForTransfer(p_con, p_status, p_statusUsed, p_userId, p_mode, p_userCategory);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
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
                channelUserVO
                    .setUserBalanceList(channelUserDAO.loadUserBalances(p_con, channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelUserVO.getUserID()));
                // for tango implementation
                channelUserVO.setApplicationID(rs.getString("application_id"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                channelUserVO.setMaxUserLevel(maxLevel);
                final CategoryVO categoryVO =  CategoryVO.getInstance();
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserHierarchyListForTransfer]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserHierarchyListForTransfer]", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userDetailsList size:");
            	loggerValue.append(userDetailList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userDetailList;
    }

    /**
     * Method loadUserDetailsByExtCode. This method is used to load all the
     * information of user on basis of External Code in case of channel enquiry
     * 
     * @author babu.kunwar
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_userID
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadUserDetailsByExtCode(Connection p_con, String p_extCode, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadUserDetailsByExtCode";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_extCode=");
        	loggerValue.append(p_extCode);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_statusUsed=");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
       
        try {
        	pstmtSelect = channelUserWebQry.loadUserDetailsByExtCode(p_con, p_status, p_statusUsed, p_userID, p_extCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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

    /**
     * Method loadUsersForBatchO2C. This method the loads the user list for
     * Batch FOC transfer
     * 
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @return LinkedHashMap
     * @throws BTSLBaseException
     * @author sandeep.goel
     */

    public Map loadUsersForBatchO2C(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate,String p_productCode) throws BTSLBaseException {
        final String methodName = "loadUsersForBatchO2C";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_productCode=");
        	loggerValue.append(p_productCode);
            LOG.debug(methodName, loggerValue);
        }

        final String categoryCode = p_categoryCode.replaceAll("'", "");
        final String ss = categoryCode.replaceAll("\" ", "");
        final String m_categoryCode[] = ss.split(",");

        final String geographicalDomainCode = p_geographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String m_geographicalDomainCode[] = gg.split(",");
        String m_senderStatusAllowed[] = null;
        Map hashMap = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        String senderStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_categoryCode.replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            // senderStatusAllowed =
            // "'"+(userStatusVO.getUserSenderAllowed()).replaceAll(",",
            // "','")+"'";
            senderStatusAllowed = userStatusVO.getUserSenderAllowed();
            final String senderStatusAllowed1 = senderStatusAllowed.replaceAll("'", "");
            final String sa = senderStatusAllowed1.replaceAll("\" ", "");
            m_senderStatusAllowed = sa.split(",");
        } else {
            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
        }
        /*
         * strBuff.append("SELECT
         * U.user_id,U.user_code,U.msisdn,U.login_id,U.category_code,C.category_name
         * ,CG.grade_code,U.status,");
         * strBuff.append(
         * "CG.grade_name,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend,U.external_code,
         * "); strBuff.append("CPSV.applicable_from,CPS.comm_profile_set_name
         * ,CPSV.comm_profile_set_version, TP.profile_name, ");
         * strBuff.append("CPS.status commprofilestatus,TP.status
         * profile_status,CPS.language_1_message comprf_lang_1_msg, ");
         * strBuff.append("CPS.language_2_message comprf_lang_2_msg ");
         * strBuff.append("FROM users U,channel_users CU,channel_grades
         * CG,categories C,user_geographies UG, ");
         * strBuff.append("commission_profile_set CPS,
         * commission_profile_set_version CPSV,transfer_profile TP ");
         * strBuff.append("WHERE U.network_code=? AND U.user_id=CU.user_id AND
         * U.user_id=UG.user_id AND ");
         * strBuff.append("U.category_code=C.category_code AND
         * U.category_code=CG.category_code AND CU.user_grade=CG.grade_code ");
         * strBuff.append(" AND U.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
         * strBuff.append("AND CPS.comm_profile_set_id = CU.comm_profile_set_id
         * AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
         * strBuff.append("AND TP.profile_id = CU.transfer_profile_id AND
         * C.category_code IN ("+p_categoryCode+") "); strBuff.append("AND
         * C.domain_code =? AND U.status <> 'N' AND U.status <> 'C' AND
         * C.status='Y' "); strBuff.append("AND UG.grph_domain_code IN (SELECT
         * grph_domain_code FROM geographical_domains GD1 ");
         * strBuff.append("WHERE status = 'Y' CONNECT BY PRIOR grph_domain_code
         * =
         * parent_grph_domain_code "); strBuff.append("START WITH
         * grph_domain_code IN("+p_geographicalDomainCode+")) ");
         * strBuff.append("AND CPSV.applicable_from =nvl ( (SELECT
         * MAX(applicable_from) FROM ");
         * strBuff.append("commission_profile_set_version WHERE applicable_from
         * <= ?
         * AND ");
         * strBuff.append(
         * "comm_profile_set_id=CU.comm_profile_set_id),CPSV.applicable_from)
         * "); strBuff.append("ORDER BY
         * C.sequence_no,CU.user_grade,U.login_id");
         */

                try {
            hashMap = new HashMap();
          pstmt = channelUserWebQry.loadUsersForBatchO2CQry(p_con, m_categoryCode, m_senderStatusAllowed, m_geographicalDomainCode, p_comPrfApplicableDate, p_domainCode, p_networkCode,p_productCode);
            rs = pstmt.executeQuery();
            ChannelUserVO channelVO = null;
            while (rs.next()) {
                channelVO = ChannelUserVO.getInstance();

                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setExternalCode(rs.getString("external_code"));

                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setNetworkCode(p_networkCode);
                channelVO.setDomainID(p_domainCode);

                hashMap.put(channelVO.getMsisdn(), channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchO2C]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchO2C]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: HashMap size:");
            	loggerValue.append(hashMap.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return hashMap;
    }

    /**
     * @param p_con
     * @param p_msisdn
     * @param p_userID
     * @param p_statusUsed
     * @param p_status
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar Method load the details of users which is created by
     *         STK or SMS
     */
    public ChannelUserVO loadSTKUsersDetails(Connection p_con, String p_msisdn, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadSTKUsersDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_stausUsed=");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;

        try {
           pstmtSelect=channelUserWebQry.loadSTKUsersDetails(p_con, p_status, p_statusUsed, p_userID, p_msisdn);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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
                channelUserVO.setAccessType(rs.getString("user_access_type"));
                channelUserVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn()));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadSTKUsersDetails]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadSTKUsersDetails]", "", "",
                "", loggerValue.toString());
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

    /**
     * @param p_con
     * @param p_loginId
     * @param p_userID
     * @param p_statusUsed
     * @param p_status
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar Method load the details of users which is created by
     *         STK or SMS
     */
    public ChannelUserVO loadSTKUsersDetailsByLoginId(Connection p_con, String p_loginId, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadSTKUsersDetailsByLoginId";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginId=");
        	loggerValue.append(p_loginId);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_stausUsed=");
        	loggerValue.append(p_statusUsed);
        	loggerValue.append(",p_status=");
        	loggerValue.append(p_status);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
       
        try {
           pstmtSelect=channelUserWebQry.loadSTKUsersDetailsByLoginId(p_con, p_status, p_statusUsed, p_userID, p_loginId);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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
                channelUserVO.setCategoryVO(categoryVO);

                channelUserVO.setParentName(rs.getString("parent_name"));
                channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
                channelUserVO.setParentCategoryName(rs.getString("parent_cat"));

                channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                channelUserVO.setOwnerCategoryName(rs.getString("owner_cat"));

                channelUserVO.setStatusDesc(rs.getString("lookup_name"));
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
                channelUserVO.setRequetedByUserName(rs.getString("request_user_name"));
                channelUserVO.setSuspendedByUserName(rs.getString("request_user_name"));
                channelUserVO.setCreatedByUserName(rs.getString("created_by_name"));
                channelUserVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(channelUserVO.getCreatedOn()));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadSTKUsersDetailsByLoginId]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadSTKUsersDetailsByLoginId]",
                "", "", "", loggerValue.toString());
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

    /**
     * Method to update new group role, transfer control profile, commission
     * control profile and grade as default and previous as normal
     * 
     * @param p_con
     * @param p_userDetailList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @param p_userVO
     *            UserVO
     * @param p_fileName
     *            String
     * @return errorList ArrayList;
     * @throws BTSLBaseException
     */
    public ArrayList updateAsDefault(Connection p_con, ArrayList p_userDetailList, String p_domainCode, MessageResources p_messages, Locale p_locale, UserVO p_userVO, String p_fileName) throws BTSLBaseException {

    	final String methodName = "updateAsDefault";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userDetailList=");
        	loggerValue.append(p_userDetailList);
        	loggerValue.append(",p_messages=");
        	loggerValue.append(p_messages);
        	loggerValue.append(",p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(",p_userVO=");
        	loggerValue.append(p_userVO);
        	loggerValue.append(",p_fileName=");
        	loggerValue.append(p_fileName);
            LOG.debug(methodName, loggerValue);
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        int recordsToBeProcessed = 0;
        int index = 0;
        final boolean flag = true;
        PreparedStatement pstmtUpdateTransferCtrlPrevious = null;
        PreparedStatement pstmtUpdateTransferCtrlNew = null;
        PreparedStatement pstmtUpdateGradeCodePrevious = null;
        PreparedStatement pstmtUpdateGradeCodeNew = null;
        PreparedStatement pstmtUpdateCommPrfSetIdPrevious = null;
        PreparedStatement pstmtUpdateCommPrfSetIdNew = null;
        PreparedStatement pstmtUpdateGroupRoleCodePrevious = null;
        PreparedStatement pstmtUpdateGroupRoleCodeNew = null;

        final StringBuffer updateTransferCtrlPrevious = new StringBuffer();
        updateTransferCtrlPrevious.append("UPDATE transfer_profile SET IS_DEFAULT = ?  ");
        updateTransferCtrlPrevious.append("WHERE IS_DEFAULT =? AND parent_profile_id='USER' ");
        updateTransferCtrlPrevious.append("AND category_code = ? AND status IN('Y','S') AND network_code=? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateTransferCtrlPrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateTransferCtrlNew = new StringBuffer();
        updateTransferCtrlNew.append("UPDATE transfer_profile SET IS_DEFAULT = ?  ");
        updateTransferCtrlNew.append("WHERE category_code=? AND profile_id=? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateTransferCtrlNew);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGradeCodePrevious = new StringBuffer();
        updateGradeCodePrevious.append("UPDATE CHANNEL_GRADES SET IS_DEFAULT_GRADE = ? ");
        updateGradeCodePrevious.append("WHERE IS_DEFAULT_GRADE = ? AND status='Y' AND category_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGradeCodePrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGradeCodeNew = new StringBuffer();
        updateGradeCodeNew.append("UPDATE CHANNEL_GRADES SET IS_DEFAULT_GRADE = ? ");
        updateGradeCodeNew.append("WHERE grade_code = ? AND category_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGradeCodeNew);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateCommPrfSetIdPrevious = new StringBuffer();
        updateCommPrfSetIdPrevious.append("UPDATE COMMISSION_PROFILE_SET  SET IS_DEFAULT = ? ");
        updateCommPrfSetIdPrevious.append("WHERE IS_DEFAULT = ? AND status!='N' AND category_code= ? AND network_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateCommPrfSetIdPrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateCommPrfSetIdNew = new StringBuffer();
        updateCommPrfSetIdNew.append("UPDATE COMMISSION_PROFILE_SET  SET IS_DEFAULT = ? ");
        updateCommPrfSetIdNew.append("WHERE comm_profile_set_id = ? AND category_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateCommPrfSetIdNew);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGroupRoleCodePrevious = new StringBuffer();
        updateGroupRoleCodePrevious.append("UPDATE ROLES  SET IS_DEFAULT = ? ");
        updateGroupRoleCodePrevious.append("WHERE IS_DEFAULT = ? AND role_code IN (SELECT R.role_code FROM CATEGORY_ROLES CR, ROLES R WHERE  R.status='Y' ");
        updateGroupRoleCodePrevious.append("AND CR.category_code = ? AND CR.role_code=R.role_code AND R.group_role='Y') ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGroupRoleCodePrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGroupRoleCodeNew = new StringBuffer();
        updateGroupRoleCodeNew.append("UPDATE ROLES  SET IS_DEFAULT = ? ");
        updateGroupRoleCodeNew.append("WHERE role_code = ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGroupRoleCodeNew);
			LOG.debug(methodName, loggerValue);
		}

        try {
            pstmtUpdateTransferCtrlPrevious = p_con.prepareStatement(updateTransferCtrlPrevious.toString());
            pstmtUpdateTransferCtrlNew = p_con.prepareStatement(updateTransferCtrlNew.toString());
            pstmtUpdateGradeCodePrevious = p_con.prepareStatement(updateGradeCodePrevious.toString());
            pstmtUpdateGradeCodeNew = p_con.prepareStatement(updateGradeCodeNew.toString());
            pstmtUpdateCommPrfSetIdPrevious = p_con.prepareStatement(updateCommPrfSetIdPrevious.toString());
            pstmtUpdateCommPrfSetIdNew = p_con.prepareStatement(updateCommPrfSetIdNew.toString());
            pstmtUpdateGroupRoleCodePrevious = p_con.prepareStatement(updateGroupRoleCodePrevious.toString());
            pstmtUpdateGroupRoleCodeNew = p_con.prepareStatement(updateGroupRoleCodeNew.toString());
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            int count = 0;
            recordsToBeProcessed = p_userDetailList.size();
            int commitNumber = 0;
            final String gettingProperty = Constants.getProperty("USER_DEFAULT_CONFIG_COMMIT_NUMBER");
            commitNumber = commitNumber1(gettingProperty);
            Collections.sort(p_userDetailList);
            for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
                if (recordsToBeProcessed < commitNumber) {
                    commitNumber = recordsToBeProcessed;
                }
                channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
                categoryVO = channelUserVO.getCategoryVO();

                pstmtUpdateTransferCtrlPrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, channelUserVO.getNetworkID());
                count = pstmtUpdateTransferCtrlPrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateTransferCtrlNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateTransferCtrlNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateTransferCtrlNew.setString(index, channelUserVO.getCategoryCode());
                    ++index;
                    pstmtUpdateTransferCtrlNew.setString(index, channelUserVO.getTransferProfileID());
                    if (pstmtUpdateTransferCtrlNew.executeUpdate() <= 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatetransferprofiletable"));
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                            "userdefaultconfig.msg.error.updatetransferprofiletable"));
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatetransferprofiletable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updatetransferprofiletable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateGradeCodePrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateGradeCodePrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateGradeCodePrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateGradeCodePrevious.setString(index, channelUserVO.getCategoryCode());
                count = pstmtUpdateGradeCodePrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateGradeCodeNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateGradeCodeNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateGradeCodeNew.setString(index, channelUserVO.getUserGrade());
                    ++index;
                    pstmtUpdateGradeCodeNew.setString(index, channelUserVO.getCategoryCode());
                    if (pstmtUpdateGradeCodeNew.executeUpdate() <= 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatechannelgradestable"));
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                            "userdefaultconfig.msg.error.updatechannelgradestable"));
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatechannelgradestable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updatechannelgradestable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateCommPrfSetIdPrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, channelUserVO.getNetworkID());
                count = pstmtUpdateCommPrfSetIdPrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateCommPrfSetIdNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateCommPrfSetIdNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateCommPrfSetIdNew.setString(index, channelUserVO.getCommissionProfileSetID());
                    ++index;
                    pstmtUpdateCommPrfSetIdNew.setString(index, channelUserVO.getCategoryCode());
                    if (pstmtUpdateCommPrfSetIdNew.executeUpdate() <= 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatecommissionprofilesetidtable"));
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                            "userdefaultconfig.msg.error.updatecommissionprofilesetidtable"));
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatecommissionprofilesetidtable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updatecommissionprofilesetidtable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateGroupRoleCodePrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateGroupRoleCodePrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateGroupRoleCodePrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateGroupRoleCodePrevious.setString(index, channelUserVO.getCategoryCode());
                count = pstmtUpdateGroupRoleCodePrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateGroupRoleCodeNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateGroupRoleCodeNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateGroupRoleCodeNew.setString(index, channelUserVO.getGroupRoleCode());
                    if (pstmtUpdateGroupRoleCodeNew.executeUpdate() <= 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updaterolestable"));
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                            "userdefaultconfig.msg.error.updaterolestable"));
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updaterolestable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updaterolestable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                commitCounter++;
                if (commitCounter == commitNumber) {
                    p_con.commit();
                    commitCounter = 0;// reset commit counter
                    recordsToBeProcessed = recordsToBeProcessed - commitNumber;
                }
            }
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isErrorEnabled()) {
            	loggerValue.setLength(0);
    			loggerValue.append(SQL_EXCEPTION);
    			loggerValue.append(sqe.getMessage());
    			LOG.error(methodName, loggerValue);
            }
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAsDefault]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isErrorEnabled()) {
            	loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(ex.getMessage());
    			LOG.error(methodName, loggerValue);
            }
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAsDefault]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdateTransferCtrlPrevious != null) {
                    pstmtUpdateTransferCtrlPrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateTransferCtrlNew != null) {
                    pstmtUpdateTransferCtrlNew.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGradeCodePrevious != null) {
                    pstmtUpdateGradeCodePrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGradeCodeNew != null) {
                    pstmtUpdateGradeCodeNew.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateCommPrfSetIdPrevious != null) {
                    pstmtUpdateCommPrfSetIdPrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateCommPrfSetIdNew != null) {
                    pstmtUpdateCommPrfSetIdNew.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGroupRoleCodePrevious != null) {
                    pstmtUpdateGroupRoleCodePrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGroupRoleCodeNew != null) {
                    pstmtUpdateGroupRoleCodeNew.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }

    /**
     * @param methodName
     * @param gettingProperty
     * @return
     */
    private int commitNumber1(String gettingProperty) {
        int commitNumber;
        final String methodName = "commitNumber1";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: gettingProperty=");
        	loggerValue.append(gettingProperty);
            LOG.debug(methodName, loggerValue);
        }
        try {
            commitNumber = Integer.parseInt(gettingProperty);
        } catch (Exception e) {
            commitNumber = 10;
            if (LOG.isErrorEnabled()) {
            	loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(e.getMessage());
    			LOG.error(methodName, loggerValue);
            }
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAsDefault]", "", "", "",
                loggerValue.toString());
        }
        return commitNumber;
    }

    /**
     * @param methodName
     * @param ConfigCommitNumber
     * @return
     */
    private int commitNumber2(String ConfigCommitNumber) {
        int commitNumber;
        final String methodName = "commitNumber2";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ConfigCommitNumber=");
        	loggerValue.append(ConfigCommitNumber);
            LOG.debug(methodName, loggerValue);
        }
        try {
            commitNumber = Integer.parseInt(ConfigCommitNumber);
        } catch (Exception e) {
            commitNumber = 10;
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAsDefault]", "", "", "",
                loggerValue.toString());
        }
        return commitNumber;
    }

    /**
     * Method to add oe delete new group role, transfer control profile,
     * commission control profile and grade as default and previous as normal
     * 
     * @param p_con
     * @param p_userDetailList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @param p_userVO
     *            UserVO
     * @param p_fileName
     *            String
     * @return errorList ArrayList;
     * @throws BTSLBaseException
     */

    public ArrayList addDeleteAsDefault(Connection p_con, ArrayList p_userDetailList, String p_action, String p_domainCode, MessageResources p_messages, Locale p_locale, UserVO p_userVO, String p_fileName) throws BTSLBaseException {
    	final String methodName = "addDeleteAsDefault";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userDetailList=");
        	loggerValue.append(p_userDetailList);
        	loggerValue.append(",p_action=");
        	loggerValue.append(p_action);
        	loggerValue.append(",p_messages=");
        	loggerValue.append(p_messages);
        	loggerValue.append(",p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(",p_userVO=");
        	loggerValue.append(p_userVO);
        	loggerValue.append(",p_fileName=");
        	loggerValue.append(p_fileName);
            LOG.debug(methodName, loggerValue);
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        int recordsToBeProcessed = 0;
        int index = 0;
        final boolean flag = true;
        PreparedStatement pstmtUpdateTransferCtrlProfile = null;
        PreparedStatement pstmtUpdateGradeCode = null;
        PreparedStatement pstmtUpdateCommPrfSetId = null;
        PreparedStatement pstmtUpdateGroupRoleCode = null;

        final StringBuffer updateTransferCtrlProfile = new StringBuffer();
        updateTransferCtrlProfile.append("UPDATE transfer_profile SET IS_DEFAULT = ?  ");
        updateTransferCtrlProfile.append("WHERE parent_profile_id='USER' ");
        updateTransferCtrlProfile.append("AND category_code = ? AND status IN('Y','S') AND network_code=? ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateTransferCtrlProfile.append("AND profile_id=? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateTransferCtrlProfile);
			LOG.debug(methodName, loggerValue);
		}
        final StringBuffer updateGradeCode = new StringBuffer();
        updateGradeCode.append("UPDATE CHANNEL_GRADES  SET IS_DEFAULT_GRADE = ? ");
        updateGradeCode.append("WHERE status='Y' AND category_code= ? ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateGradeCode.append("AND grade_code = ? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGradeCode);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateCommPrfSetId = new StringBuffer();
        updateCommPrfSetId.append("UPDATE COMMISSION_PROFILE_SET  SET IS_DEFAULT = ? ");
        updateCommPrfSetId.append("WHERE status!='N' AND category_code= ? AND network_code= ? ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateCommPrfSetId.append("AND comm_profile_set_id = ? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateCommPrfSetId);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGroupRoleCode = new StringBuffer();
        updateGroupRoleCode.append("UPDATE ROLES SET IS_DEFAULT = ? ");
        updateGroupRoleCode.append("WHERE role_code IN (SELECT R.role_code FROM CATEGORY_ROLES CR, ROLES R WHERE  R.status='Y' ");
        updateGroupRoleCode.append("AND CR.category_code = ? AND CR.role_code=R.role_code AND R.group_role='Y') ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateGroupRoleCode.append("AND role_code = ? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGroupRoleCode);
			LOG.debug(methodName, loggerValue);
		}

        try {
            pstmtUpdateTransferCtrlProfile = p_con.prepareStatement(updateTransferCtrlProfile.toString());
            pstmtUpdateGradeCode = p_con.prepareStatement(updateGradeCode.toString());
            pstmtUpdateCommPrfSetId = p_con.prepareStatement(updateCommPrfSetId.toString());
            pstmtUpdateGroupRoleCode = p_con.prepareStatement(updateGroupRoleCode.toString());
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            recordsToBeProcessed = p_userDetailList.size();
            int commitNumber = 0;
            final String ConfigCommitNumber = Constants.getProperty("USER_DEFAULT_CONFIG_COMMIT_NUMBER");
            commitNumber = commitNumber2(ConfigCommitNumber);
            for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
                if (recordsToBeProcessed < commitNumber) {
                    commitNumber = recordsToBeProcessed;
                }
                Collections.sort(p_userDetailList);
                channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
                categoryVO = channelUserVO.getCategoryVO();

                pstmtUpdateTransferCtrlProfile.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateTransferCtrlProfile.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateTransferCtrlProfile.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateTransferCtrlProfile.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateTransferCtrlProfile.setString(index, channelUserVO.getNetworkID());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateTransferCtrlProfile.setString(index, channelUserVO.getTransferProfileID());
                }
                if (pstmtUpdateTransferCtrlProfile.executeUpdate() <= 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatetransferprofiletable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updatetransferprofiletable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateGradeCode.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGradeCode.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateGradeCode.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateGradeCode.setString(index, channelUserVO.getCategoryCode());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGradeCode.setString(index, channelUserVO.getUserGrade());
                }
                if (pstmtUpdateGradeCode.executeUpdate() <= 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatechannelgradestable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updatechannelgradestable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                pstmtUpdateCommPrfSetId.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateCommPrfSetId.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateCommPrfSetId.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateCommPrfSetId.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateCommPrfSetId.setString(index, channelUserVO.getNetworkID());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateCommPrfSetId.setString(index, channelUserVO.getCommissionProfileSetID());
                }
                if (pstmtUpdateCommPrfSetId.executeUpdate() <= 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updatecommissionprofilesetidtable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updatecommissionprofilesetidtable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                pstmtUpdateGroupRoleCode.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGroupRoleCode.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateGroupRoleCode.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateGroupRoleCode.setString(index, channelUserVO.getCategoryCode());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGroupRoleCode.setString(index, channelUserVO.getGroupRoleCode());
                }
                if (pstmtUpdateGroupRoleCode.executeUpdate() <= 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userdefaultconfig.msg.error.updaterolestable"));
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userdefaultconfig.msg.error.updaterolestable"));
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                commitCounter++;
                if (commitCounter == commitNumber) {
                    p_con.commit();
                    commitCounter = 0;// reset commit counter
                    recordsToBeProcessed = recordsToBeProcessed - commitNumber;
                }
            }
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[addDeleteAsDefault]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[addDeleteAsDefault]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (pstmtUpdateTransferCtrlProfile != null) {
                    pstmtUpdateTransferCtrlProfile.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGradeCode != null) {
                    pstmtUpdateGradeCode.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateCommPrfSetId != null) {
                    pstmtUpdateCommPrfSetId.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGroupRoleCode != null) {
                    pstmtUpdateGroupRoleCode.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }

    /**
     * Method to get the parent User details based on User ID
     * 
     * @param p_con
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadParentUserDetailsByUserID(Connection p_con, String p_userID) throws BTSLBaseException {
        final String methodName = "loadParentUserDetailsByUserID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID=");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ChannelUserVO channelUserVO = null;

        ResultSet rs = null;
        try {
            // changed for staff user approval
            String selectQuery = channelUserWebQry.loadParentUserDetailsByUserID();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setDomainID(rs.getString("domain_code"));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setOutletCode(rs.getString("outlet_code"));
                channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setPhoneProfile(rs.getString("PHONE_PROFILE"));

                final CategoryVO categoryVO = new CategoryVO();
                categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
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
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));

                channelUserVO.setCategoryVO(categoryVO);
            }
            return channelUserVO;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadParentUserDetailsByUserID]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadParentUserDetailsByUserID]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
    }

    /**
     * New Method Method loadChannelUserDetailsByLoginIDANDORMSISDN() This
     * method loads the channel users based on login_id and/or msisdn
     * 
     * @author harsh dixit
     * @param p_con
     * @param p_msisdn
     *            String
     * @param p_loginid
     *            String
     * @return channelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadChannelUserDetailsByLoginIDANDORMSISDN(Connection p_con, String p_msisdn, String p_loginid) throws BTSLBaseException {

        final String methodName = "loadChannelUserDetailsByLoginIDANDORMSISDN";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_loginid=");
        	loggerValue.append(p_loginid);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ChannelUserVO channelUserVO = null;
        ChannelUserDAO channelUserDAO = null;
        ResultSet rs = null;
        try {
            channelUserDAO = new ChannelUserDAO();
            String selectQuery = channelUserWebQry.loadChannelUserDetailsByLoginIDANDORMSISDN(p_msisdn, p_loginid);
                       if (LOG.isDebugEnabled()) {
                LOG.debug("loadChannelUserDetails", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_CANCELED);
            // modifed by harsh
            if (!BTSLUtil.isNullString(p_msisdn) && !BTSLUtil.isNullString(p_loginid)) {
                pstmtSelect.setString(3, p_msisdn);
                pstmtSelect.setString(4, p_loginid);
            }

            else if (!BTSLUtil.isNullString(p_msisdn)) {
                pstmtSelect.setString(3, p_msisdn);
            } else if (!BTSLUtil.isNullString(p_loginid)) {
                pstmtSelect.setString(3, p_loginid);
            }
            // end modified by
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Before result:" + p_msisdn);
            }
            rs = pstmtSelect.executeQuery();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "After result:" + p_msisdn);
            }
            if (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
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
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                // end Zebra and Tango
                channelUserVO.setPinReset(rs.getString("PIN_RESET"));
                channelUserVO.setInvalidPasswordCount(rs.getInt("invalid_password_count"));
                final Timestamp pswd_modify_date = BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on"));
                password_Modify_Date(channelUserVO, pswd_modify_date);

                final CategoryVO categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("catseq"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
                categoryVO.setAllowedGatewayTypes(new CategoryReqGtwTypeDAO().loadMessageGatewayTypeListForCategory(p_con, categoryVO.getCategoryCode()));
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
                userPhoneVO.setMsisdn(p_msisdn);
                userPhoneVO.setUserId(rs.getString("user_id"));
                userPhoneVO.setPrimaryNumber(rs.getString("primary_number"));
                userPhoneVO.setSmsPin(rs.getString("sms_pin"));
                userPhoneVO.setPinRequired(rs.getString("pin_required"));
                userPhoneVO.setPhoneProfile(rs.getString(PHONE_PROFILE));
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

                channelUserVO.setAssociatedServiceTypeList(channelUserDAO.loadUserServicesList(p_con, channelUserVO.getUserID()));
            }
            return channelUserVO;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetails", "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadChannelUserDetails", "error.general.processing",e);
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

    }

    /**
     * @param channelUserVO
     * @param pswd_modify_date
     */
    private void password_Modify_Date(ChannelUserVO channelUserVO, Timestamp pswd_modify_date) {

        final String methodName = "password_Modify_Date";
        try {
            channelUserVO.setPasswordModifiedOn(pswd_modify_date);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            channelUserVO.setPasswordModifiedOn(null);
        }
    }

    /**
     * Method loadUsersForAdditionalDetail. This method the loads the user list
     * for Batch User Additional Details
     * 
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @return LinkedHashMap
     * @throws BTSLBaseException
     * @author sandeep.goel
     */

    public Map loadUsersForAdditionalDetail(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode) throws BTSLBaseException {
        final String methodName = "loadUsersForAdditionalDetail";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }

        Map hashMap = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        final String geographicalDomainCode = p_geographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String m_geographicalDomainCode[] = gg.split(",");

        String sqlSelect= channelUserWebQry.loadUsersForAdditionalDetail(m_geographicalDomainCode);
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

        try {
            hashMap = new HashMap();
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_domainCode);
            for (int x = 0; x < m_geographicalDomainCode.length; x++) {
                ++i;
                pstmt.setString(i, m_geographicalDomainCode[x]);
            }
            rs = pstmt.executeQuery();
            ChannelUserVO channelVO = null;
            while (rs.next()) {
                channelVO = ChannelUserVO.getInstance();
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelVO.setNetworkCode(p_networkCode);
                channelVO.setDomainID(p_domainCode);

                hashMap.put(channelVO.getMsisdn(), channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForAdditionalDetail]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForAdditionalDetail]", "", "",
                "", loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: hashmap size:");
            	loggerValue.append(hashMap.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return hashMap;
    }

    /**
     * This method is used to associate restricted msisdn Method
     * :associateRestrictedMsisdn
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_restrictedList
     *            ArrayList
     * @return String
     * @throws BTSLBaseException
     */
    public int autoc2cupdate(Connection p_con, ChannelUserVO chnlUserVO) throws BTSLBaseException {
    	final String methodName = "autoc2cupdate";
        if (LOG.isDebugEnabled()) {
            LOG.debug("autoc2cupdate", "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = 0;
        final String unprocessedMsisdn = null;
        final StringBuffer invalidDataStrBuff = new StringBuffer();
        final String updateQuery = "UPDATE channel_users SET auto_c2c_allow=?,auto_c2c_quantity=? WHERE user_id=?";

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
        try {
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            int i = 0;
            ++i;
            pstmtUpdate.setString(i, chnlUserVO.getAutoc2callowed());
            ++i;
            pstmtUpdate.setLong(i, chnlUserVO.getMaxTxnAmount());
            ++i;
            pstmtUpdate.setString(i, chnlUserVO.getChannelUserID());

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("autoc2callowed", "Exiting: autoc2callowed=" + unprocessedMsisdn);
                }
                p_con.rollback();

            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[associateRestrictedMsisdn]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: unprocessedMsisdn:");
            	loggerValue.append(unprocessedMsisdn);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }
    public int sosUpdate(Connection p_con, ChannelUserVO chnlUserVO) throws BTSLBaseException {
    	final String methodName = "sosUpdate";
        StringBuilder loggerValue = new StringBuilder();
        
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
			loggerValue.append("Entered : SosAllowed= ="+chnlUserVO.getSosAllowed());
			loggerValue.append("SosAllowedAmount= ="+chnlUserVO.getSosAllowedAmount());
			loggerValue.append("SosThresholdLimit= ="+chnlUserVO.getSosThresholdLimit());
			loggerValue.append("ChannelUserID ="+chnlUserVO.getChannelUserID());
			LOG.debug(methodName, loggerValue);
			
            LOG.debug(methodName, "Entered : SosAllowed="+ chnlUserVO.getSosAllowed()+",SosAllowedAmount="+chnlUserVO.getSosAllowedAmount()+",SosThresholdLimit=" +chnlUserVO.getSosThresholdLimit()+",ChannelUserID="+chnlUserVO.getChannelUserID());
        }
        
        
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = 0;
        final String unprocessedMsisdn = null;
        final StringBuilder invalidDataStrBuff = new StringBuilder();
        final String updateQuery = "UPDATE channel_users SET sos_allowed=?,sos_allowed_amount=? , sos_threshold_limit=? WHERE user_id=?";

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
        try {
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            int i = 0;
            ++i;
            pstmtUpdate.setString(i, chnlUserVO.getSosAllowed());
            ++i;
            pstmtUpdate.setLong(i, chnlUserVO.getSosAllowedAmount());
            ++i;
            pstmtUpdate.setLong(i, chnlUserVO.getSosThresholdLimit());
            ++i;
            pstmtUpdate.setString(i, chnlUserVO.getChannelUserID());

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Exiting:  updateCount=" + updateCount);
                }
                p_con.rollback();

            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[associateRestrictedMsisdn]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
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
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
           if(LOG.isDebugEnabled()){
           	loggerValue.setLength(0);
           	loggerValue.append("Exiting: sosUpdate=" + updateCount);
           	loggerValue.append(unprocessedMsisdn);
           	LOG.debug(methodName, loggerValue);
           }
        }
        return updateCount;
    }

    /**
     * Method loadUsersForBatchO2C. This method the loads the user list for
     * Batch FOC transfer
     * 
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @return LinkedHashMap
     * @throws BTSLBaseException
     * @author sandeep.goel
     */

    public Map loadUsersForBulkAutoC2C(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, Date p_comPrfApplicableDate) throws BTSLBaseException {
        final String methodName = "loadUsersForBulkAutoC2C";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_comPrfApplicableDate=");
        	loggerValue.append(p_comPrfApplicableDate);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }
        
        Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        Boolean isLREnabled = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
        
        Map hashMap = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String categoryCode = p_categoryCode.replaceAll("'", "");
        final String ss = categoryCode.replaceAll("\" ", "");
        final String m_categoryCode[] = ss.split(",");

        final String geographicalDomainCode = p_geographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String m_geographicalDomainCode[] = gg.split(",");
        final StringBuffer strBuff = new StringBuffer();

        try {
            hashMap = new HashMap();
            
            String sqlSelect = channelUserWebQry.loadUsersForBulkAutoC2C(m_categoryCode, m_geographicalDomainCode);
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, p_networkCode);
            for (int x = 0; x < m_categoryCode.length; x++) {
                ++i;
                pstmt.setString(i, m_categoryCode[x]);
            }
            ++i;
            pstmt.setString(i, p_domainCode);
            for (int x = 0; x < m_geographicalDomainCode.length; x++) {
                ++i;
                pstmt.setString(i, m_geographicalDomainCode[x]);
            }
            ++i;
            pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
            rs = pstmt.executeQuery();
            ChannelUserVO channelVO = null;
            while (rs.next()) {
                channelVO = ChannelUserVO.getInstance();

                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setAutoc2cquantity(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("auto_c2c_quantity"))));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setExternalCode(rs.getString("external_code"));

                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setNetworkCode(p_networkCode);
                channelVO.setDomainID(p_domainCode);
                channelVO.setAutoc2callowed(rs.getString("auto_c2c_allow"));
                if(isChannelSOSEnable){
                channelVO.setSosAllowed(rs.getString("sos_allowed"));
                channelVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
                channelVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
                }
                if(isLREnabled){
                    channelVO.setLrAllowed(rs.getString("lr_allowed"));
                    channelVO.setLrMaxAmount(rs.getLong("lr_max_amount"));
                   }
                hashMap.put(channelVO.getMsisdn(), channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchO2C]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchO2C]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: hashmap size:");
            	loggerValue.append(hashMap.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return hashMap;
    }

    /**
     * Method initiateBatchO2CTransfer This method used for the batch foc order
     * initiation. The main purpose of this method is to insert the records in
     * foc_batches,foc_batch_geographies & foc_batch_items table.
     * 
     * @param p_con
     *            Connection
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_batchItemsList
     *            ArrayList
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList initiateBulkAutoC2CAndSOSAllowed(Connection p_con, FOCBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
    	
    	final String methodName = "initiateBulkAutoC2CAllowed";
                
    	String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
    	String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
    	Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
    	Boolean isLREnabled = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
    	
    	final ArrayList errorList = new ArrayList();
        O2CBatchItemsVO batchItemsVO = null;
        int queryExecutionCount = 0;
        PushMessage push =null;
        String message = null;
        String arr[] = new String[]{"0","0"};
        StringBuilder loggerValue= new StringBuilder();
       	Locale locale = new Locale(defaultLanguage, defaultCountry);
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder(
            "UPDATE channel_users SET auto_c2c_allow =? , auto_c2c_quantity =? ");
        if(isChannelSOSEnable){
        strBuffUpdateBatchMaster.append(" ,sos_allowed= ? , sos_allowed_amount = ?, sos_threshold_limit =? ");
        }
        if(isLREnabled){
            strBuffUpdateBatchMaster.append(" ,lr_allowed= ? , lr_max_amount = ? ");
        }
        strBuffUpdateBatchMaster.append("WHERE user_id IN(Select user_id from users where msisdn =?)");
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster.toString());
        }
        
        try {

            pstmtUpdateBatchMaster = p_con.prepareStatement(strBuffUpdateBatchMaster.toString());

            int index = 0;

            for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
                batchItemsVO = (O2CBatchItemsVO) p_batchItemsList.get(i);
                index = 0;
                ++index;
                pstmtUpdateBatchMaster.setString(index, batchItemsVO.getAutoc2callowed());
                ++index;
                pstmtUpdateBatchMaster.setLong(index, batchItemsVO.getRequestedQuantity());
                
                if(isChannelSOSEnable){
                	++index;
                	pstmtUpdateBatchMaster.setString(index, batchItemsVO.getSosAllowed());
                	++index;
                	pstmtUpdateBatchMaster.setLong(index,batchItemsVO.getSosAllowedAmount());
                	++index;
                	pstmtUpdateBatchMaster.setLong(index,batchItemsVO.getSosThresholdLimit());
                }
                if(isLREnabled){
                	++index;
                	pstmtUpdateBatchMaster.setString(index, batchItemsVO.getLrAllowed());
                	++index;
                	pstmtUpdateBatchMaster.setLong(index,batchItemsVO.getLrMaxAmount());
                }
                ++index;
                pstmtUpdateBatchMaster.setString(index, batchItemsVO.getMsisdn());
                queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                if (queryExecutionCount <= 0) // Means No Records Updated
                {
                    LOG.error(methodName, "Unable to Update the batch size in master table..");
                    p_con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[initiateBulkAutoC2CAllowed]",
                        "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + p_batchMasterVO.getBatchId());
                }

                else {
                	arr[0]=PretupsBL.getDisplayAmount(batchItemsVO.getSosThresholdLimit());
                   	arr[1]=PretupsBL.getDisplayAmount(batchItemsVO.getSosAllowedAmount());
                	if(isChannelSOSEnable && batchItemsVO.getSosAllowed().equals(PretupsI.YES)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.SOS_ENABLE_SUCCESS,arr);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}else if(isChannelSOSEnable && batchItemsVO.getSosAllowed().equals(PretupsI.NO)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.SOS_DISABLE_SUCCESS,null);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}
                	arr[0]=PretupsBL.getDisplayAmount(batchItemsVO.getLrMaxAmount());
                	if(isLREnabled && batchItemsVO.getSosAllowed().equals(PretupsI.YES)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.LAST_RECHARGE_ENABLE_SUCCESS,arr);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}else if(isLREnabled && batchItemsVO.getSosAllowed().equals(PretupsI.NO)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.LAST_RECHARGE_DISABLE_SUCCESS,null);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}
                    p_con.commit();
                }

            }

        }

        catch (SQLException e) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[initiateBulkAutoC2CAllowed]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }finally {
            try {
                if (pstmtUpdateBatchMaster != null) {
                	pstmtUpdateBatchMaster.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }

    /**
     * Method viewAutoC2CUser() is used to authenticate the user for XML
     * Authentication
     * 
     * @param p_con
     * @param p_loginID
     * @param p_msisdn
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author priyanka.goel
     */
    public ChannelUserVO viewAutoC2CUser(Connection p_con, String p_userID) throws BTSLBaseException {
        final String methodName = "viewAutoC2CUser";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userID=");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }
        ChannelUserVO channelUserVO = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT cu.auto_c2c_allow , cu.auto_c2c_quantity , cu.sos_allowed, cu.sos_allowed_amount,cu.sos_threshold_limit, u.msisdn FROM channel_users cu , users u");
        strBuff.append(" WHERE ");
        strBuff.append(" cu.user_id	 = ? and u.user_id=cu.user_id ");

        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setAutoc2callowed(rs.getString("auto_c2c_allow"));
                channelUserVO.setAutoc2cquantity(rs.getString("auto_c2c_quantity"));
                if(isChannelSOSEnable){
                	channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
                	channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
                	channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
                }
                channelUserVO.setMsisdn(rs.getString("msisdn"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadAuthenticateUserDetails]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadAuthenticateUserDetails]", "", "",
                "", loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ************* ");
            }
        }
        return channelUserVO;
    }

    /**
     * method verifyAutoC2CCategory This method check the user of the specified
     * category which are the direct child of the owner. This will be called to
     * check user at domain level and have direct T/R/W allowed
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_domainID
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public boolean verifyAutoC2CCategory(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_msisdn) throws BTSLBaseException {

        final String methodName = "verifyAutoc2cCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_toCategoryCode=");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT 1 FROM users  ");
        strBuff.append(" WHERE network_code = ? AND status <> 'N' AND status <> 'C' AND status <> 'W' AND category_code = ? ");
        // Sandeep goel ID USD001
        // query is changed to optimization and to remove the problem as owner
        // was not coming.
        strBuff.append(" AND msisdn = ? ");

        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        boolean isExist = false;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            i++;
            pstmt.setString(i, p_networkCode);
            i++;
            pstmt.setString(i, p_toCategoryCode);
            i++;
            pstmt.setString(i, p_msisdn);

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[verifyAutoc2cCategory]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[verifyAutoc2cCategory]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isExist:");
            	loggerValue.append(isExist);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return isExist;
    }

    /**
     * method verifyAutoC2CCategory This method check the user of the specified
     * category which are the direct child of the owner. This will be called to
     * check user at domain level and have direct T/R/W allowed
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_domainID
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public boolean verifyCategory(Connection p_con, String p_toCategoryCode) throws BTSLBaseException {

        final String methodName = "verifyCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_toCategoryCode=");
        	loggerValue.append(p_toCategoryCode);
            LOG.debug(methodName, loggerValue);
        }        
        Boolean isAutoC2CSOSCatAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_C2C_SOS_CAT_ALLOWED);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT 1 FROM control_preferences  ");
        strBuff.append(" WHERE preference_code = ? AND control_code = ? ");
        if(isAutoC2CSOSCatAllowed){
        	
        	strBuff.append(" AND upper(value) = ? ");
        }else{
        strBuff.append(" AND value = ? ");
        }
        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        boolean isExist = false;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            i++;
            pstmt.setString(i, PretupsI.AUTO_C2C_SOS_CAT_ALLOWED);
            i++;
            pstmt.setString(i, p_toCategoryCode);
            if(isAutoC2CSOSCatAllowed){
            i++;
            pstmt.setString(i, PretupsI.FALSE);
            }else{
            	 i++;
                 pstmt.setString(i, PretupsI.AUTO_C2C_TRUE);
            }

            rs = pstmt.executeQuery();
            if(isAutoC2CSOSCatAllowed){
            	   isExist = true;
            	if (rs.next()) {
                isExist = false;
            }
            }else{
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[verifyCategory]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[verifyCategory]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isExist:");
            	loggerValue.append(isExist);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return isExist;
    }

    /**
     * Added by Aatif
     * 
     * This method gets the list of Promotion Name as LMS Profile.
     * 
     * @param con
     * @return
     */
    public ArrayList getLmsProfileList(Connection con, String networkCode) throws BTSLBaseException {
        final String methodName = "getLmsProfileList";
        StringBuilder loggerValue= new StringBuilder();
        final ArrayList lmsProfileList = new ArrayList();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        LoyalityVO loyalityVO = null;
        // Changed for not associating expired profile
        final Date date = new Date();
        /*
         * Modified by lalit
         * Changed in query for network code
         */
        final String selectQuery = " SELECT distinct ps.set_name,ps.set_id from profile_set ps,profile_set_version psv where ps.network_code=? and ps.status not in ('N','R') and ps.profile_type=? and psv.applicable_to>=? and ps.set_id=psv.set_id and psv.status='Y' ";
        try {
            preparedStatement = con.prepareStatement(selectQuery);
            preparedStatement.setString(1, networkCode);
            preparedStatement.setString(2, PretupsI.LMS);
            preparedStatement.setDate(3, BTSLUtil.getSQLDateFromUtilDate(date));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                loyalityVO = new LoyalityVO();
                String set_id = resultSet.getString("set_id");
				Map<String,Integer> countOftotalVersionSuspendActive = new HashMap<String,Integer>(1);
				countOftotalVersionSuspendActive = countOfTotalVersionSuspendActive(con, set_id);
				int totalVersion = 0;
				int totalSuspendedVersion = 0;
				int totalActiveVersion = 0; 
				if(countOftotalVersionSuspendActive!=null && countOftotalVersionSuspendActive.size()>0){
					totalVersion = countOftotalVersionSuspendActive.get("totalVersion");
					totalSuspendedVersion = countOftotalVersionSuspendActive.get("totalSuspendedVersion");
					totalActiveVersion = countOftotalVersionSuspendActive.get("totalActiveVersion");
					if(LOG.isDebugEnabled()){
						StringBuffer msg=new StringBuffer("");
			        	msg.append("totalVersion= ");
			        	msg.append(totalVersion);
			        	msg.append(", totalSuspendedVersion= ");
			        	msg.append(totalSuspendedVersion);
			        	msg.append(", totalActiveVersion= ");
			        	msg.append(totalActiveVersion);
			        	
			        	String message=msg.toString();
						LOG.debug(methodName ,message);
					}
				}
				if(totalSuspendedVersion == totalVersion){
					continue;
				} else {
	                loyalityVO.setPromotionName(resultSet.getString("set_name"));
	                loyalityVO.setPromotionID(resultSet.getString("set_id"));
	                lmsProfileList.add(new ListValueVO(resultSet.getString("set_name"), resultSet.getString("set_id")));
				}
            }
        } catch (SQLException sqlException) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqlException.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace("getLmsProfileList", sqlException);
            throw new BTSLBaseException(this, "getLmsProfileList", "error.general.processing",sqlException);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: lmsprofile:");
            	loggerValue.append(lmsProfileList);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return lmsProfileList;
    }

    public ChannelUserVO parentGeographyDetails(Connection con, String geographyCode) throws BTSLBaseException {

        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ChannelUserVO channelUserVO = null;
        final String methodName = "parentGeographyDetails";
        StringBuilder loggerValue= new StringBuilder();
        final String selectQuery = "SELECT PARENT_GRPH_DOMAIN_CODE,GRPH_DOMAIN_NAME from GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_CODE=? ";
        try {
            preparedStatement = con.prepareStatement(selectQuery);
            preparedStatement.setString(1, geographyCode);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setParentGeographyCode(resultSet.getString("PARENT_GRPH_DOMAIN_CODE"));
                channelUserVO.setGeographicalDesc(resultSet.getString("GRPH_DOMAIN_NAME"));

            }
        } catch (SQLException sqlException) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqlException.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqlException);
            throw new BTSLBaseException(this, "getLmsProfileList", "error.general.processing",sqlException);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ChannelUserVO:");
            	loggerValue.append(channelUserVO);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return channelUserVO;
    }

    public ArrayList loadCategoryUsersWithinGeoDomainHirearchyForWithdraw(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode, String p_loginUserID) throws BTSLBaseException {

        final String methodName = "loadCategoryUsersWithinGeoDomainHirearchyForWithdraw";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(",p_loginUserID=");
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
            LOG.debug(methodName, loggerValue);
        }

        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        String senderStatusAllowed = null;
        String statusAllowed = null;

        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache
            .getObject(p_networkCode, p_categoryCode, PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            senderStatusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            statusAllowed = senderStatusAllowed + ",'" + (userStatusVO.getUserSenderSuspended()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }


        final ArrayList arrayList = new ArrayList();
        try {
            // commented for DB2
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
        	
        	
        	
        	pstmt= channelUserWebQry.loadCategoryUsersWithinGeoDomainHirearchyForWithdraw(p_con, p_networkCode, p_categoryCode, p_geographicalDomainCode, p_userName, p_loginUserID, p_ownerUserID, statusAllowed, senderStatusAllowed);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchyForWithdraw]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchyForWithdraw]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ArrayList size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return arrayList;
    }

    public ArrayList loadUsersByParentID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID) throws BTSLBaseException {

        final String methodName = "loadUsersByParentID";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_parentID=");
        	loggerValue.append(p_parentID);
        	loggerValue.append(",p_toCategoryCode=");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_userID=");
        	loggerValue.append(p_userID);
            LOG.debug(methodName, loggerValue);
        }
        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT user_id, user_name FROM users  ");
        strBuff.append(" WHERE network_code = ? AND status = 'Y' AND category_code = ? AND user_id != ?");
        strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) AND ( parent_id = ? OR user_id = ?) ORDER BY user_name ");
        // here parent_id = ? OR user_id = ? check is to load the parent also if
        // transaciton is done only to parent
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
            // p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            i++;
            pstmt.setString(i, p_networkCode);
            i++;
            pstmt.setString(i, p_toCategoryCode);
            i++;
            pstmt.setString(i, p_userID);
            // commented for DB2 pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            i++;
            pstmt.setString(i, p_userName);
            i++;
            pstmt.setString(i, p_parentID);
            i++;
            pstmt.setString(i, p_parentID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentID]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentID]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ArrayList size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return arrayList;
    }

    public ArrayList loadChannelUserTypeList(Connection con) throws BTSLBaseException {

    	final String methodName = "loadChannelUserTypeList";
        ChannelUserTypeVO channelUserTypeVO = null;
        final ArrayList list = new ArrayList();
        StringBuilder loggerValue= new StringBuilder();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        final String selectQuery = "SELECT LOOKUP_CODE,LOOKUP_NAME from LOOKUPS where LOOKUP_TYPE='CTYPE' ";

        try {

            preparedStatement = con.prepareStatement(selectQuery);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                channelUserTypeVO = new ChannelUserTypeVO();
                channelUserTypeVO.setLookupCode(resultSet.getString("LOOKUP_CODE"));
                channelUserTypeVO.setLookupName(resultSet.getString("LOOKUP_NAME"));
                list.add(channelUserTypeVO);
            }
        }

        catch (SQLException sqlException) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqlException.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace("loadChannelUserTypeList", sqlException);
            throw new BTSLBaseException(this, "loadChannelUserTypeList", "error.general.processing",sqlException);
        }finally {
            try {
                if (resultSet != null) {
                	resultSet.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (preparedStatement != null) {
                	preparedStatement.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	LOG.debug(methodName, loggerValue);
            }
        }

        return list;

    }

    // Created By Mithlesh Kumar
    public int associateUserAndNo(Connection con, String msisdn, String type, String userid) throws BTSLBaseException {
    	final String methodName = "associateUserAndNo";
        PreparedStatement preparedStatement = null;
        StringBuilder loggerValue= new StringBuilder();
        final String updateQuery = "UPDATE channel_users SET ASSOCIATED_MSISDN=?,ASSOCIATED_MSISDN_TYPE=?, ASSOCIATED_MSISDN_CDATE=?, ASSOCIATED_MSISDN_MDATE=? WHERE USER_ID=?";
        int updateCount = 0;
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
        System.out.println("userid" + userid + " msisdn ** " + msisdn + " type $$ " + type);
        try {
            preparedStatement = con.prepareStatement(updateQuery);
            int i = 0;
            ++i;
            preparedStatement.setString(i, msisdn);
            ++i;
            preparedStatement.setString(i, type);
            ++i;
            preparedStatement.setDate(i, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            ++i;
            preparedStatement.setDate(i, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            ++i;
            preparedStatement.setString(i, userid);

            updateCount = preparedStatement.executeUpdate();
            if (updateCount <= 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Exiting: associateUserAndNo");
                }
                con.rollback();

            }

        } catch (SQLException sqlException) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqlException.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqlException);
            throw new BTSLBaseException(this, methodName, "error.general.processing",sqlException);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: associateUserAndNo");
            }
        }
        return updateCount;

    }

    public int deAssociateUserAndNo(Connection con, String type, String userid) throws BTSLBaseException {
    	final String methodName = "deAssociateUserAndNo";
        PreparedStatement preparedStatement = null;
        StringBuilder loggerValue= new StringBuilder();
        final String updateQuery = "UPDATE channel_users SET ASSOCIATED_MSISDN=?,ASSOCIATED_MSISDN_TYPE=?,ASSOCIATED_MSISDN_MDATE=?WHERE USER_ID=?";
        int updateCount = 0;
        System.out.println("updateQuery" + updateQuery + "userid" + userid);
        System.out.println("userid &&" + userid + " type $$ " + type);
        try {
            preparedStatement = con.prepareStatement(updateQuery);
            int i = 0;
            ++i;
            preparedStatement.setString(i, null);
            ++i;
            preparedStatement.setString(i, type);
            ++i;
            preparedStatement.setDate(i, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            ++i;
            preparedStatement.setString(i, userid);

            updateCount = preparedStatement.executeUpdate();
            if (updateCount <= 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("deAssociateUserAndNo", "Exiting: deAssociateUserAndNo");
                }
                con.rollback();

            }
        } catch (SQLException sqlException) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqlException.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace("deAssociateUserAndNo", sqlException);
            throw new BTSLBaseException(this, "deAssociateUserAndNo", "error.general.processing",sqlException);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                LOG.errorTrace("deAssociateUserAndNo", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("deAssociateUserAndNo", "Exiting: deAssociateUserAndNo");
            }
        }
        return updateCount;

    }

    // Handling of controlled profile
    /*public boolean isControlledProfileAlreadyAssociated(Connection con, String msisdn) throws BTSLBaseException {
        final String methodName = "isControlledProfileAlreadyAssociated";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered :" + msisdn);
        }
        boolean returnVal = false;
        PreparedStatement pstmtSelect = null;
        ResultSet resultSet = null;

       String selectQuery = channelUserWebQry.isControlledProfileAlreadyAssociated();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "selectQuery= " + selectQuery);
        }

        try {
            int k = 0;
            con = OracleUtil.getConnection();
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.clearParameters();
            ++k;
            pstmtSelect.setString(k, PretupsI.LMS_PROFILE_TYPE_CONTROLLED);
            ++k;
            pstmtSelect.setString(k, msisdn);
            resultSet = pstmtSelect.executeQuery();
            while (resultSet.next()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Entered : msisdn = " + msisdn + " , user_id = " + resultSet.getString("user_id") + " , lms_profile = " + resultSet
                        .getString("lms_profile") + " , promotion_type = " + resultSet.getString("promotion_type"));
                }
                returnVal = true;
            }
        }

        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            LOG.error(methodName, " Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[" + methodName + "]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(methodName, " Exception Closing prepared statement in " + methodName + ": " + e.getMessage());
                }
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exiting returnVal " + returnVal);
            }
        }
        return returnVal;

    }*/

    /**
     * Method getUserMsisdnExists This method will return true if the user does
     * not exists
     * 
     * @author akanksha
     * @param p_con
     *            Connection
     * @param p_loginID
     *            String
     * @param p_countStr
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean getUserMsisdnExists(Connection p_con, String p_usrMsisdn, int p_countStr) throws BTSLBaseException {
        final String methodName = "getUserMsisdnExists";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_usrMsisdn=");
        	loggerValue.append(p_usrMsisdn);
        	loggerValue.append(",p_countStr=");
        	loggerValue.append(p_countStr);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean invalidStringInDao = false;
        final String qry = "SELECT UP.msisdn FROM user_phones UP, users U WHERE U.user_id=UP.user_id AND U.status<>'N' AND U.status<>'C' AND UP.msisdn= ? ";
        try {
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(qry);
    			LOG.debug(methodName, loggerValue);
    		}
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_usrMsisdn);
            // Execute Query
            rs = pstmt.executeQuery();
            if (!rs.next()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Mobile number does not exist " + p_usrMsisdn);
                }
                UnregisterChUsersFileProcessLog.log("User existence", "", "MSISDN :" + p_usrMsisdn, p_countStr, "Mobile number does not exist", "Fail", "");
                invalidStringInDao = true;
                if (rs != null) {
                    rs.close();
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserWebDAO[getUserMsisdnExists]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserWebDAO[getUserMsisdnExists]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                LOG.error(methodName, "  Exception Closing RS : " + ex.getMessage());
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                LOG.error(methodName, "  Exception Closing Prepared Stmt: " + ex.getMessage());
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: invalidStringInDao:");
            	loggerValue.append(invalidStringInDao);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return invalidStringInDao;
    }

    /**
     * Method getUserLoginIdExists This method will return true if the user does
     * not exists
     * 
     * @author akanksha
     * @param p_con
     *            Connection
     * @param p_loginID
     *            String
     * @param p_countStr
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean getUserLoginIdExists(Connection p_con, String p_loginID, int p_countStr) throws BTSLBaseException {
        final String methodName = "getUserLoginIdExists";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(",p_countStr=");
        	loggerValue.append(p_countStr);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean invalidStringInDao = false;
        final String qry = "SELECT login_id FROM users WHERE UPPER(login_id) = UPPER(?) ";
        try {
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(qry);
    			LOG.debug(methodName, loggerValue);
    		}
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_loginID);
            // Execute Query
            rs = pstmt.executeQuery();
            if (!rs.next()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Login id does not exist " + p_loginID);
                }
                UnregisterChUsersFileProcessLog.log("User existence", "", "Login ID :" + p_loginID, p_countStr, "Login ID does not exist", "Fail", "");
                invalidStringInDao = true;
                if (rs != null) {
                    rs.close();
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserWebDAO[getUserLoginIdExists]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserWebDAO[getUserLoginIdExists]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                LOG.error(methodName, "  Exception Closing RS : " + ex.getMessage());
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                LOG.error(methodName, "  Exception Closing Prepared Stmt: " + ex.getMessage());
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: invalidStringInDao:");
            	loggerValue.append(invalidStringInDao);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return invalidStringInDao;
    }
    
    public ArrayList loadCategoryUsersWithinGeoDomainHirearchy(Connection p_con, String p_categoryCode,
			String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode,
			String p_loginUserID) throws BTSLBaseException {

		final String methodName = "loadCategoryUsersWithinGeoDomainHirearchy";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_ownerUserID=");
        	loggerValue.append(p_ownerUserID);
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(",p_loginUserID=");
        	loggerValue.append(p_loginUserID);
            LOG.debug(methodName, loggerValue);
        }

		// commented for DB2 OraclePreparedStatement pstmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strBuff = new StringBuffer();
        String receiverStatusAllowed =null;
        String statusAllowed=null;
        
        UserStatusVO userStatusVO = (UserStatusVO)UserStatusCache.getObject(p_networkCode, p_categoryCode, PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if(userStatusVO!=null){
        receiverStatusAllowed = "'"+(userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','")+"'";
        statusAllowed = receiverStatusAllowed+",'"+PretupsI.USER_STATUS_SUSPEND+"'";
        }else{
        	 throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        

		ArrayList arrayList = new ArrayList();
		try {
			// commented for DB2
			// pstmt = (OraclePreparedStatement)
			// p_con.prepareStatement(sqlSelect);
			pstmt = channelUserWebQry.loadCategoryUsersWithinGeoDomainHirearchyQry(p_con, p_networkCode, p_categoryCode, p_geographicalDomainCode, p_userName, p_loginUserID, p_ownerUserID, statusAllowed, receiverStatusAllowed );
			rs = pstmt.executeQuery();
			while (rs.next()) {
				arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
			}

		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", "Exception:"
							+ ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}

			if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: arrayList.size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);
            }
		}
		return arrayList;
	}

    public ArrayList loadCategoryUsersWithinGeoDomainHirearchyAutoComplete(Connection p_con, String p_categoryCode,
                                                               String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode,
                                                               String p_loginUserID) throws BTSLBaseException {

        final String methodName = "loadCategoryUsersWithinGeoDomainHirearchyAutoComplete";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_ownerUserID=");
            loggerValue.append(p_ownerUserID);
            loggerValue.append(",p_categoryCode=");
            loggerValue.append(p_categoryCode);
            loggerValue.append(",p_networkCode=");
            loggerValue.append(p_networkCode);
            loggerValue.append(",p_userName=");
            loggerValue.append(p_userName);
            loggerValue.append(",p_geographicalDomainCode=");
            loggerValue.append(p_geographicalDomainCode);
            loggerValue.append(",p_loginUserID=");
            loggerValue.append(p_loginUserID);
            LOG.debug(methodName, loggerValue);
        }

        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        String receiverStatusAllowed =null;
        String statusAllowed=null;

        UserStatusVO userStatusVO = (UserStatusVO)UserStatusCache.getObject(p_networkCode, p_categoryCode, PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if(userStatusVO!=null){
            receiverStatusAllowed = "'"+(userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','")+"'";
            statusAllowed = receiverStatusAllowed+",'"+PretupsI.USER_STATUS_SUSPEND+"'";
        }else{
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }


        ArrayList arrayList = new ArrayList();
        try {
            // commented for DB2
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
            pstmt = channelUserWebQry.loadCategoryUsersWithinGeoDomainHirearchyQry(p_con, p_networkCode, p_categoryCode, p_geographicalDomainCode, p_userName, p_loginUserID, p_ownerUserID, statusAllowed, receiverStatusAllowed );
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", "SQL Exception:"
                            + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(ex.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", "Exception:"
                            + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                loggerValue.append("Exiting: arrayList.size:");
                loggerValue.append(arrayList.size());
                LOG.debug(methodName, loggerValue);
            }
        }
        return arrayList;
    }

    public ArrayList loadCategoryUsersWithinGeoDomainHirearchyMsisdn(Connection p_con, String p_categoryCode,
			String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode,
			String p_loginUserID) throws BTSLBaseException {

		final String methodName = "loadCategoryUsersWithinGeoDomainHirearchyMsisdn";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_ownerUserID=");
        	loggerValue.append(p_ownerUserID);
        	loggerValue.append(",p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_geographicalDomainCode=");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(",p_loginUserID=");
        	loggerValue.append(p_loginUserID);
            LOG.debug(methodName, loggerValue);
        }

		// commented for DB2 OraclePreparedStatement pstmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strBuff = new StringBuffer();
        String receiverStatusAllowed =null;
        String statusAllowed=null;
        
        UserStatusVO userStatusVO = (UserStatusVO)UserStatusCache.getObject(p_networkCode, p_categoryCode, PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if(userStatusVO!=null){
        receiverStatusAllowed = "'"+(userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','")+"'";
        statusAllowed = receiverStatusAllowed+",'"+PretupsI.USER_STATUS_SUSPEND+"'";
        }else{
        	 throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        //statusAllowed="'"+PretupsI.YES+"'";
        
   	ArrayList arrayList = new ArrayList();
		try {
			// commented for DB2
			// pstmt = (OraclePreparedStatement)
			// p_con.prepareStatement(sqlSelect);
			pstmt = channelUserWebQry.loadCategoryUsersWithinGeoDomainHirearchyQryForAutoO2C(p_con, p_categoryCode, p_geographicalDomainCode, p_userName, p_loginUserID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("msisdn"),rs.getString("user_id"),""));
			}

		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchyMsisdn]", "", "", "", "SQL Exception:"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchyMsisdn]", "", "", "", "Exception:"
							+ ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}

			if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: arrayList.size:");
            	loggerValue.append(arrayList.size());
            	LOG.debug(methodName, loggerValue);
            }
		}
		return arrayList;
	}
    
    public Map<String,Integer>  countOfTotalVersionSuspendActive(Connection p_con,String p_setID) throws BTSLBaseException
	{
		final String methodName = "countOfTotalVersionSuspendActive";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID=");
        	loggerValue.append(p_setID);
            LOG.debug(methodName, loggerValue);
        }
		Map<String,Integer> countOftotalVersionSuspendActive = new HashMap<String,Integer>(1);
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try
		{
			StringBuffer selectQueryBuff =	new StringBuffer(" select totalVersion, totalSuspendedVersion, totalActiveVersion ");
			selectQueryBuff.append(" from "); 
			selectQueryBuff.append("( ");
			selectQueryBuff.append("select count(set_id) as totalVersion from PROFILE_SET_VERSION "); 
			selectQueryBuff.append("where SET_ID= ? ");
			selectQueryBuff.append(") X ");
			selectQueryBuff.append(", ");
			selectQueryBuff.append("( ");
			selectQueryBuff.append("select count(set_id) as totalSuspendedVersion from PROFILE_SET_VERSION "); 
			selectQueryBuff.append("where SET_ID= ? ");
			selectQueryBuff.append("and status= ? ");
			selectQueryBuff.append(") Y ");
			selectQueryBuff.append(", ");
			selectQueryBuff.append("( ");
			selectQueryBuff.append("select count(set_id) as totalActiveVersion from PROFILE_SET_VERSION "); 
			selectQueryBuff.append("where SET_ID= ? ");
			selectQueryBuff.append("and status= ? ");
			selectQueryBuff.append(") Z ");
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(selectQueryBuff.toString());
				LOG.debug(methodName, loggerValue);
			}
			String selectQuery=selectQueryBuff.toString();
			pstmt=p_con.prepareStatement(selectQuery);
			pstmt.setString(1,p_setID);
			pstmt.setString(2, p_setID);
			pstmt.setString(3, PretupsI.SUSPEND);
			pstmt.setString(4, p_setID);
			pstmt.setString(5, PretupsI.YES);
			rs=pstmt.executeQuery();
			if(rs.next())
			{
				countOftotalVersionSuspendActive.put("totalVersion", rs.getInt("totalVersion"));
				countOftotalVersionSuspendActive.put("totalSuspendedVersion", rs.getInt("totalSuspendedVersion"));
				countOftotalVersionSuspendActive.put("totalActiveVersion", rs.getInt("totalActiveVersion"));
			}
		}
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ActivationBonusDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
		} // end of catch
		catch (Exception e)
		{
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ActivationBonusDAO["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",e);
		} // end of catch
		finally
		{
			try 
			{
				if (rs != null)
				{
					rs.close();
				}
			} 
			catch (Exception e)
			{
				LOG.errorTrace(methodName,e);
			}
			try
			{
				if (pstmt != null)
				{
					pstmt.close();
				}
			} 
			catch (Exception e)
			{
				LOG.errorTrace(methodName,e);
			}
			if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: countOftotalVersionSuspendActive:");
            	loggerValue.append(countOftotalVersionSuspendActive);
            	LOG.debug(methodName, loggerValue);
            }
		}
		return countOftotalVersionSuspendActive;
	}

	public ArrayList loadUserListOnZoneDomainCategoryWithMSISDN(Connection p_con, String p_userCategory,  String p_zoneCode, String p_fromUserID, String p_userName, String pLOGinuserID, String domainCode) throws BTSLBaseException
	{
		final String methodName = "loadUserListOnZoneDomainCategoryWithMSISDN";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userCategory=");
        	loggerValue.append(p_userCategory);
        	loggerValue.append(",p_fromUserID=");
        	loggerValue.append(p_fromUserID);
        	loggerValue.append(",p_zoneCode=");
        	loggerValue.append(p_zoneCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",pLOGinuserID=");
        	loggerValue.append(pLOGinuserID);
        	loggerValue.append(",domainCode=");
        	loggerValue.append(domainCode);
            LOG.debug(methodName, loggerValue);
        }
	    	ArrayList userList =new ArrayList();
	    	//commented for DB2 OraclePreparedStatement pstmtSelect = null;
	    	PreparedStatement pstmtSelect = null;
	    	ResultSet rs = null;	    	
	       
	    	try
	    	{
	    		//commented for DB2 pstmtSelect = (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
	    		pstmtSelect = channelUserWebQry.loadUserListOnZoneDomainCategoryWithMSISDN(p_con, p_fromUserID, p_userName, domainCode, p_userCategory, p_zoneCode, pLOGinuserID);
	    	    rs = pstmtSelect.executeQuery();
	    	    while(rs.next())
	    	    {         
				    userList.add(new  ListValueVO(rs.getString("user_name"), rs.getString("MSISDN")));
	    	    }	    	   
	    	} 
	    	catch (SQLException sqe)
	    	{
	    		loggerValue.setLength(0);
				loggerValue.append(SQL_EXCEPTION);
				loggerValue.append(sqe.getMessage());
				LOG.error(methodName, loggerValue);
	    	   
	    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserReportDAO[loadUserListOnZoneDomainCategoryWithMSISDN]","","","","SQL Exception:"+sqe.getMessage());
	    	    throw new BTSLBaseException(this, "loadUserListOnZoneDomainCategoryWithMSISDN", "error.general.sql.processing",sqe);
	    	} 
	    	catch (Exception ex)
	    	{
	    		loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(ex.getMessage());
				LOG.error(methodName, loggerValue);
	    	   
	    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserReportDAO[loadUserListOnZoneDomainCategoryWithMSISDN]","","","","Exception:"+ex.getMessage());
	    	    throw new BTSLBaseException(this, "loadUserListOnZoneDomainCategoryWithMSISDN", "error.general.processing",ex);
	    	}
	    	finally
			{
			try {
				if (rs != null) {
					rs.close();
				}
			}
			
			catch (Exception e) {
				LOG.error(methodName, "Exception: ", e.getMessage());
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} 
			catch (Exception e) {
				LOG.error(methodName,"Exception : ", e.getMessage());
			}
	    	    if(LOG.isDebugEnabled()){
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting: userList size:");
	            	loggerValue.append(userList.size());
	            	LOG.debug(methodName, loggerValue);
	            }
			}
	    	return userList;
	}	
	
	/**This method is used to update channel users for the last recharge service association/deassociation
	 * @param pCon
	 * @param chnlUserVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public int lastRechargeUpdate(Connection pCon, ChannelUserVO chnlUserVO) throws BTSLBaseException {
		final String methodName = "lastRechargeUpdate";
		StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: chnlUserVO=");
        	loggerValue.append(chnlUserVO);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = 0;
        final String unprocessedMsisdn = null;
        final StringBuilder invalidDataStrBuff = new StringBuilder();
        final String updateQuery = "UPDATE channel_users SET lr_allowed=?,lr_max_amount=?  WHERE user_id=?";

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
        try {
            pstmtUpdate = pCon.prepareStatement(updateQuery);

            int i = 0;
            ++i;
            pstmtUpdate.setString(i, chnlUserVO.getLrAllowed());
            ++i;
            pstmtUpdate.setLong(i, chnlUserVO.getLrMaxAmount());
            ++i;
            pstmtUpdate.setString(i, chnlUserVO.getChannelUserID());

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("LastRechargeUpdate", "Exiting: Last recharge updated=" + unprocessedMsisdn);
                }
                pCon.rollback();

            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserWebDAO[LastRechargeUpdate]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
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
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
           if(LOG.isDebugEnabled()){
           	loggerValue.setLength(0);
           	loggerValue.append("Exiting: unprocessedMsisdn:");
           	loggerValue.append(unprocessedMsisdn);
           	LOG.debug(methodName, loggerValue);
           }
        }
        return updateCount;
    }
	
	public ArrayList loadUserNameAndEmail(Connection p_con, String p_userID) throws BTSLBaseException {
		final String methodName = "loadUserNameAndEmail";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: ");
			loggerValue.append("p_userID=");
			loggerValue.append(p_userID);
			LOG.debug(methodName, loggerValue);
		}
		PreparedStatement pstm = null;
		ResultSet rst = null;
		final int index = 1;
		final ArrayList arrayList = new ArrayList();
		try {
			final StringBuffer str = new StringBuffer(
					" SELECT user_name,msisdn,email FROM users WHERE user_id=? ");

			final String query = str.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}
			pstm = p_con.prepareStatement(query);
			pstm.setString(index, p_userID);
			rst = pstm.executeQuery();

			if (rst.next()) {
				arrayList.add(rst.getString("USER_NAME"));
				arrayList.add(rst.getString("MSISDN"));
				arrayList.add(rst.getString("EMAIL"));
			}
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserNameAndEmail]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing", sqle);
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserDAO[loadUserNameAndEmail]", "", "", "",
					loggerValue.toString());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing", e);
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
		}
		return arrayList;
	}
	
	
	 /**
     * Method loadCategoryUserHierarchy. This method load the list of the user
     * on the basis of the selected category and
     * other logged-in user's details and entered user name
     * 
     * @author amit.singh
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_userName
     *            String
     * @param p_loginUserID
     *            String
     * @return userList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserHierarchyByCategory(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_loginUserID) throws BTSLBaseException {
        final String methodName = "loadCategoryUserHierarchy";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(",p_loginUserID=");
        	loggerValue.append(p_loginUserID);
            LOG.debug(methodName, loggerValue);
        }
        final ArrayList userList = new ArrayList();
        // commented for DB2OraclePreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        NotificationLanguageResponseVO responseVO = null;
                try {
            // commented for DB2 pstmtSelect = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
           pstmtSelect=channelUserWebQry.userHierarchyQryByCategory(p_con,p_networkCode,p_categoryCode,p_loginUserID,p_userName);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
            	responseVO = new NotificationLanguageResponseVO();
            	responseVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
            	responseVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
            	responseVO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("msisdn")));
            	responseVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
                userList.add(responseVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUserHierarchy]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadCategoryUserHierarchy]", "", "",
                "", loggerValue.toString());
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
            	loggerValue.append("Exiting: userList.size():");
            	loggerValue.append(userList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return userList;
    }// end of loadCategoryUserHierarchy
    
    
    /**
     * Method updateAlertMsisdnTemplate. This method load channel user's Alert
     * MSISDN for Sample Template.
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author vikas.jauhari Updated by Harpreet for Alert Type
     */
    public ArrayList updateAlertMsisdnTemplateNew(Connection p_con, ArrayList p_fileDataList, Locale p_locale) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        PreparedStatement psmtSelect = null;
        int updateCount = 0;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ChannelUserVO filedata = null;
        String Msisdn = null;
        String alertMsisdn = null;
        String alertType = null;
        ResultSet rs = null;
        String userId = null;
        String alertEmail = null;
        ArrayList fileDataListFinal = null;

        final String methodName = "updateAlertMsisdnTemplate";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fileDataList.size()=");
        	loggerValue.append(p_fileDataList.size());
            LOG.debug(methodName, loggerValue);
        }
        try {
            errorList = new ArrayList();
            fileDataListFinal = new ArrayList();
            if (p_fileDataList != null) {
                final String selectQuery = "select up.user_id user_id from USER_PHONES up, users u where	u.user_id=up.user_id AND up.PRIMARY_NUMBER='Y' AND u.STATUS ='Y' AND up.MSISDN=?";
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Query selectQuery:" + selectQuery);
                }
                final String updateQuery = "UPDATE channel_users SET alert_msisdn=? ,alert_type=? ,alert_email=?, low_bal_alert_allow=? WHERE user_id=?";
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateQuery);
        			LOG.debug(methodName, loggerValue);
        		}
                psmtSelect = p_con.prepareStatement(selectQuery);
                psmtUpdate = p_con.prepareStatement(updateQuery);

                for (int i = 0, j = p_fileDataList.size(); i < j; i++) {
                    filedata = (ChannelUserVO) p_fileDataList.get(i);
                    Msisdn = filedata.getMsisdn();
                    alertMsisdn = filedata.getAlertMsisdn();
                    alertType = filedata.getAlertType();
                    alertEmail = filedata.getAlertEmail();
                    psmtSelect.clearParameters();
                    psmtSelect.setString(1, Msisdn);
                    rs = psmtSelect.executeQuery();
                    if (rs.next()) {
                        filedata.setUserID(rs.getString("user_id"));
                        //
                        fileDataListFinal.add(filedata);
                        //
                    } else {
//                        errorVO = new ListValueVO(filedata.getRecordNumber(), p_messages.getMessage(p_locale, "channel.processUploadedFile.error.useridnotexist",
//                            new String[] { filedata.getMsisdn() }));
                        errorVO = new ListValueVO(filedata.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, "channel.processUploadedFile.error.useridnotexist",
                        		new String[] { filedata.getMsisdn() } ));
                        errorList.add(errorVO);
                        
                        continue;
                    }
                }
                
                //if (errorList.isEmpty()) {
                    //for (int i = 0, j = p_fileDataList.size(); i < j; i++) {
                	for (int i = 0, j = fileDataListFinal.size(); i < j; i++) {
                        filedata = (ChannelUserVO) fileDataListFinal.get(i);
                        userId = filedata.getUserID();
                        alertMsisdn = filedata.getAlertMsisdn();
                        psmtUpdate.setString(1, alertMsisdn);
                        psmtUpdate.setString(2, filedata.getAlertType());
                        psmtUpdate.setString(3, filedata.getAlertEmail());
                        psmtUpdate.setString(4, filedata.getLowBalAlertAllow());
                        psmtUpdate.setString(5, userId);
                        updateCount = psmtUpdate.executeUpdate();
                        if (updateCount == 0) {
//                            errorVO = new ListValueVO(filedata.getRecordNumber(), p_messages.getMessage(p_locale, "channel.processUploadedFile.error.updateusertable",
//                                new String[] { filedata.getMsisdn() }));
                        	errorVO = new ListValueVO(filedata.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, "channel.processUploadedFile.error.updateusertable",
                            		new String[] { filedata.getMsisdn() } ));
                            errorList.add(errorVO);
                        }

                    }
                //}
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdnTemplate]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdnTemplate]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
                if (psmtSelect != null) {
                    psmtSelect.close();
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
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }
    
    /**
     * Method updateAlertMsisdn. This method load channel user's Alert MSISDN
     * for UserListing Template.
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author vikas.jauhari Updated by Harpreet for Alert Type
     */

    public ArrayList updateAlertMsisdnNew(Connection p_con, ArrayList p_fileDataList, Locale p_locale) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        final int updateRecord = 0;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ChannelUserVO filedata = null;
        String userId = null;
        String alertMsisdn = null;
        String alertType = null;
        String alertEmail = null;

        final String methodName = "updateAlertMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fileDataList.size()=");
        	loggerValue.append(p_fileDataList.size());
            LOG.debug(methodName, loggerValue);
        }
        try {

            if (p_fileDataList != null) {
                errorList = new ArrayList();
                final String updateQuery = "UPDATE channel_users SET alert_msisdn=? , alert_Type=? ,alert_Email=? ,low_bal_alert_allow=? WHERE user_id=?";
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateQuery);
        			LOG.debug(methodName, loggerValue);
        		}

                psmtUpdate = p_con.prepareStatement(updateQuery);

                for (int i = 0, j = p_fileDataList.size(); i < j; i++) {
                    filedata = (ChannelUserVO) p_fileDataList.get(i);
                    userId = filedata.getUserID();
                    alertMsisdn = filedata.getAlertMsisdn();
                    alertType = filedata.getAlertType();
                    alertEmail = filedata.getAlertEmail();
                    psmtUpdate.setString(1, alertMsisdn);
                    psmtUpdate.setString(2, alertType);
                    // Added for Email
                    psmtUpdate.setString(3, alertEmail);
                    if(alertType!=null){
                    	 psmtUpdate.setString(4, "Y");
                    }else{
                    	 psmtUpdate.setString(4, "N");
                    }
                    
                    psmtUpdate.setString(5, userId);
                    updateCount = psmtUpdate.executeUpdate();
                    if (updateCount == 0) {
//                        errorVO = new ListValueVO(filedata.getRecordNumber(), p_messages.getMessage(p_locale, "channel.processUploadedFile.error.updateusertable",
//                            new String[] { filedata.getUserID() }));
                    	errorVO = new ListValueVO(filedata.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, "channel.processUploadedFile.error.updateusertable",
                        		new String[] { filedata.getMsisdn() } ));
                        errorList.add(errorVO);
                    }

                }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdn]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAlertMsisdn]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateRecord:");
            	loggerValue.append(updateRecord);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;

    }
  public ArrayList initiateBulkAutoC2CAndSOSAllowedNew(Connection p_con, FOCBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList) throws BTSLBaseException {
    	
    	final String methodName = "initiateBulkAutoC2CAllowed";
                
    	String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
    	String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
    	Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
    	Boolean isLREnabled = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
    	
    	final ArrayList errorList = new ArrayList();
        O2CBatchItemsVO batchItemsVO = null;
        int queryExecutionCount = 0;
        PushMessage push =null;
        String message = null;
        String arr[] = new String[]{"0","0"};
        StringBuilder loggerValue= new StringBuilder();
       	Locale locale = new Locale(defaultLanguage, defaultCountry);
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder(
            "UPDATE channel_users SET auto_c2c_allow =? , auto_c2c_quantity =? ");
        if(isChannelSOSEnable){
        strBuffUpdateBatchMaster.append(" ,sos_allowed= ? , sos_allowed_amount = ?, sos_threshold_limit =? ");
        }
        if(isLREnabled){
            strBuffUpdateBatchMaster.append(" ,lr_allowed= ? , lr_max_amount = ? ");
        }
        strBuffUpdateBatchMaster.append("WHERE user_id IN(Select user_id from users where msisdn =?)");
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster.toString());
        }
        
        try {

            pstmtUpdateBatchMaster = p_con.prepareStatement(strBuffUpdateBatchMaster.toString());

            int index = 0;

            for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
                batchItemsVO = (O2CBatchItemsVO) p_batchItemsList.get(i);
                index = 0;
                ++index;
                pstmtUpdateBatchMaster.setString(index, batchItemsVO.getAutoc2callowed());
                ++index;
                pstmtUpdateBatchMaster.setLong(index, batchItemsVO.getRequestedQuantity());
                
                if(isChannelSOSEnable){
                	++index;
                	pstmtUpdateBatchMaster.setString(index, batchItemsVO.getSosAllowed());
                	++index;
                	pstmtUpdateBatchMaster.setLong(index,batchItemsVO.getSosAllowedAmount());
                	++index;
                	pstmtUpdateBatchMaster.setLong(index,batchItemsVO.getSosThresholdLimit());
                }
                if(isLREnabled){
                	++index;
                	pstmtUpdateBatchMaster.setString(index, batchItemsVO.getLrAllowed());
                	++index;
                	pstmtUpdateBatchMaster.setLong(index,batchItemsVO.getLrMaxAmount());
                }
                ++index;
                pstmtUpdateBatchMaster.setString(index, batchItemsVO.getMsisdn());
                queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                if (queryExecutionCount <= 0) // Means No Records Updated
                {
                    LOG.error(methodName, "Unable to Update the batch size in master table..");
                    p_con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[initiateBulkAutoC2CAllowed]",
                        "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + p_batchMasterVO.getBatchId());
                }

                else {
                	arr[0]=PretupsBL.getDisplayAmount(batchItemsVO.getSosThresholdLimit());
                   	arr[1]=PretupsBL.getDisplayAmount(batchItemsVO.getSosAllowedAmount());
                	if(isChannelSOSEnable && batchItemsVO.getSosAllowed().equals(PretupsI.YES)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.SOS_ENABLE_SUCCESS,arr);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}else if(isChannelSOSEnable && batchItemsVO.getSosAllowed().equals(PretupsI.NO)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.SOS_DISABLE_SUCCESS,null);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}
                	arr[0]=PretupsBL.getDisplayAmount(batchItemsVO.getLrMaxAmount());
                	if(isLREnabled && batchItemsVO.getLrAllowed().equals(PretupsI.YES)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.LAST_RECHARGE_ENABLE_SUCCESS,arr);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}else if(isLREnabled && batchItemsVO.getLrAllowed().equals(PretupsI.NO)){
                		message	= BTSLUtil.getMessage(locale,PretupsErrorCodesI.LAST_RECHARGE_DISABLE_SUCCESS,null);
                       	push = new PushMessage(batchItemsVO.getMsisdn(), message, "", "", locale);
                       	push.push();
                	}
                    p_con.commit();
                }

            }

        }

        catch (SQLException e) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[initiateBulkAutoC2CAllowed]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }finally {
            try {
                if (pstmtUpdateBatchMaster != null) {
                	pstmtUpdateBatchMaster.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }
    
    
	public List<ListValueVO> getLoanProfileList(Connection con, String categoryCode) throws BTSLBaseException {
		final String methodName = "getLoanProfileList";
		
		if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: categoryCode=" + categoryCode);
        }
        List<ListValueVO> loanProfileList = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        String selectQuery = " select lp.profile_id,lp.profile_name from loan_profiles lp inner join categories c on lp.category_code=c.category_code where lp.category_code=? and c.status='Y' and lp.status='Y' ";
        try {
            preparedStatement = con.prepareStatement(selectQuery);
            preparedStatement.setString(1, categoryCode);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
            	loanProfileList.add(new ListValueVO(resultSet.getString("profile_name"), resultSet.getString("profile_id")));
            }
        } catch (SQLException sqlException) {
            LOG.error(methodName, "SQLException : " + sqlException);
            LOG.errorTrace(methodName, sqlException);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting :" + loanProfileList);
            }
        }
        return loanProfileList;
	}

	public String fetchLoanProfileId(Connection con, String userID) throws BTSLBaseException {
		final String methodName = "fetchLoanProfileId";
		
		if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: userID=" + userID);
        }
		String profileID = null;
        List<ListValueVO> loanProfileList = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        String selectQuery = " select profile_id from channel_user_loan_info where user_id=? and product_code=? ";
        try {
            preparedStatement = con.prepareStatement(selectQuery);
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, PretupsI.PRODUCT_ETOPUP);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
            	profileID=resultSet.getString("profile_id");
            }
        } catch (SQLException sqlException) {
            LOG.error(methodName, "SQLException : " + sqlException);
            LOG.errorTrace(methodName, sqlException);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting :" + profileID);
            }
        }
        return profileID;
	}

	/**
     * Method to update new group role, transfer control profile, commission
     * control profile and grade as default and previous as normal
     * 
     * @param p_con
     * @param p_userDetailList
     *            ArrayList
     * @param p_locale
     *            Locale
     * @param p_userVO
     *            UserVO
     * @param p_fileName
     *            String
     * @return errorList ArrayList;
     * @throws BTSLBaseException
     */
   

	public ArrayList updateAsDefaultFromRest(Connection p_con, ArrayList p_userDetailList,
			Locale p_locale, UserVO p_userVO, String p_fileName) throws BTSLBaseException {
	   	final String methodName = "updateAsDefaultFromRest";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userDetailList=");
        	loggerValue.append(p_userDetailList);
        	loggerValue.append(",p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(",p_userVO=");
        	loggerValue.append(p_userVO);
        	loggerValue.append(",p_fileName=");
        	loggerValue.append(p_fileName);
            LOG.debug(methodName, loggerValue);
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        int recordsToBeProcessed = 0;
        int index = 0;
        final boolean flag = true;
        PreparedStatement pstmtUpdateTransferCtrlPrevious = null;
        PreparedStatement pstmtUpdateTransferCtrlNew = null;
        PreparedStatement pstmtUpdateGradeCodePrevious = null;
        PreparedStatement pstmtUpdateGradeCodeNew = null;
        PreparedStatement pstmtUpdateCommPrfSetIdPrevious = null;
        PreparedStatement pstmtUpdateCommPrfSetIdNew = null;
        PreparedStatement pstmtUpdateGroupRoleCodePrevious = null;
        PreparedStatement pstmtUpdateGroupRoleCodeNew = null;

        final StringBuffer updateTransferCtrlPrevious = new StringBuffer();
        updateTransferCtrlPrevious.append("UPDATE transfer_profile SET IS_DEFAULT = ?  ");
        updateTransferCtrlPrevious.append("WHERE IS_DEFAULT =? AND parent_profile_id='USER' ");
        updateTransferCtrlPrevious.append("AND category_code = ? AND status IN('Y','S') AND network_code=? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateTransferCtrlPrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateTransferCtrlNew = new StringBuffer();
        updateTransferCtrlNew.append("UPDATE transfer_profile SET IS_DEFAULT = ?  ");
        updateTransferCtrlNew.append("WHERE category_code=? AND profile_id=? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateTransferCtrlNew);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGradeCodePrevious = new StringBuffer();
        updateGradeCodePrevious.append("UPDATE CHANNEL_GRADES SET IS_DEFAULT_GRADE = ? ");
        updateGradeCodePrevious.append("WHERE IS_DEFAULT_GRADE = ? AND status='Y' AND category_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGradeCodePrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGradeCodeNew = new StringBuffer();
        updateGradeCodeNew.append("UPDATE CHANNEL_GRADES SET IS_DEFAULT_GRADE = ? ");
        updateGradeCodeNew.append("WHERE grade_code = ? AND category_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGradeCodeNew);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateCommPrfSetIdPrevious = new StringBuffer();
        updateCommPrfSetIdPrevious.append("UPDATE COMMISSION_PROFILE_SET  SET IS_DEFAULT = ? ");
        updateCommPrfSetIdPrevious.append("WHERE IS_DEFAULT = ? AND status!='N' AND category_code= ? AND network_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateCommPrfSetIdPrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateCommPrfSetIdNew = new StringBuffer();
        updateCommPrfSetIdNew.append("UPDATE COMMISSION_PROFILE_SET  SET IS_DEFAULT = ? ");
        updateCommPrfSetIdNew.append("WHERE comm_profile_set_id = ? AND category_code= ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateCommPrfSetIdNew);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGroupRoleCodePrevious = new StringBuffer();
        updateGroupRoleCodePrevious.append("UPDATE ROLES  SET IS_DEFAULT = ? ");
        updateGroupRoleCodePrevious.append("WHERE IS_DEFAULT = ? AND role_code IN (SELECT R.role_code FROM CATEGORY_ROLES CR, ROLES R WHERE  R.status='Y' ");
        updateGroupRoleCodePrevious.append("AND CR.category_code = ? AND CR.role_code=R.role_code AND R.group_role='Y') ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGroupRoleCodePrevious);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGroupRoleCodeNew = new StringBuffer();
        updateGroupRoleCodeNew.append("UPDATE ROLES  SET IS_DEFAULT = ? ");
        updateGroupRoleCodeNew.append("WHERE role_code = ? ");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGroupRoleCodeNew);
			LOG.debug(methodName, loggerValue);
		}

        try {
            pstmtUpdateTransferCtrlPrevious = p_con.prepareStatement(updateTransferCtrlPrevious.toString());
            pstmtUpdateTransferCtrlNew = p_con.prepareStatement(updateTransferCtrlNew.toString());
            pstmtUpdateGradeCodePrevious = p_con.prepareStatement(updateGradeCodePrevious.toString());
            pstmtUpdateGradeCodeNew = p_con.prepareStatement(updateGradeCodeNew.toString());
            pstmtUpdateCommPrfSetIdPrevious = p_con.prepareStatement(updateCommPrfSetIdPrevious.toString());
            pstmtUpdateCommPrfSetIdNew = p_con.prepareStatement(updateCommPrfSetIdNew.toString());
            pstmtUpdateGroupRoleCodePrevious = p_con.prepareStatement(updateGroupRoleCodePrevious.toString());
            pstmtUpdateGroupRoleCodeNew = p_con.prepareStatement(updateGroupRoleCodeNew.toString());
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            int count = 0;
            recordsToBeProcessed = p_userDetailList.size();
            int commitNumber = 0;
            final String gettingProperty = Constants.getProperty("USER_DEFAULT_CONFIG_COMMIT_NUMBER");
            commitNumber = commitNumber1(gettingProperty);
            Collections.sort(p_userDetailList);
            for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
                if (recordsToBeProcessed < commitNumber) {
                    commitNumber = recordsToBeProcessed;
                }
                channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
                categoryVO = channelUserVO.getCategoryVO();

                pstmtUpdateTransferCtrlPrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateTransferCtrlPrevious.setString(index, channelUserVO.getNetworkID());
                count = pstmtUpdateTransferCtrlPrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateTransferCtrlNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateTransferCtrlNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateTransferCtrlNew.setString(index, channelUserVO.getCategoryCode());
                    ++index;
                    pstmtUpdateTransferCtrlNew.setString(index, channelUserVO.getTransferProfileID());
                    if (pstmtUpdateTransferCtrlNew.executeUpdate() <= 0) {
                    	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPADTETRANSFERPROFILETABLE_TABLES,null);

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("error_desc ", " DESC :" + error);
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPADTETRANSFERPROFILETABLE_TABLES,null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + error);
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateGradeCodePrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateGradeCodePrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateGradeCodePrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateGradeCodePrevious.setString(index, channelUserVO.getCategoryCode());
                count = pstmtUpdateGradeCodePrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateGradeCodeNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateGradeCodeNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateGradeCodeNew.setString(index, channelUserVO.getUserGrade());
                    ++index;
                    pstmtUpdateGradeCodeNew.setString(index, channelUserVO.getCategoryCode());
                    if (pstmtUpdateGradeCodeNew.executeUpdate() <= 0) {
                    	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPGRADESCHANNELTABLES,null);

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("error_desc ", " DESC :" + error);
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPGRADESCHANNELTABLES,null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + error);
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(),error );
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateCommPrfSetIdPrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateCommPrfSetIdPrevious.setString(index, channelUserVO.getNetworkID());
                count = pstmtUpdateCommPrfSetIdPrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateCommPrfSetIdNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateCommPrfSetIdNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateCommPrfSetIdNew.setString(index, channelUserVO.getCommissionProfileSetID());
                    ++index;
                    pstmtUpdateCommPrfSetIdNew.setString(index, channelUserVO.getCategoryCode());
                    if (pstmtUpdateCommPrfSetIdNew.executeUpdate() <= 0) {
                    	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_COMMISSIONPROFILEsetid_TABLES,null);
                        if (LOG.isDebugEnabled()) {

                            LOG.debug("error_desc ", " DESC :" + error);
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_COMMISSIONPROFILEsetid_TABLES, null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + error);
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(),error);
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateGroupRoleCodePrevious.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateGroupRoleCodePrevious.setString(index, PretupsI.NO);
                ++index;
                pstmtUpdateGroupRoleCodePrevious.setString(index, PretupsI.YES);
                ++index;
                pstmtUpdateGroupRoleCodePrevious.setString(index, channelUserVO.getCategoryCode());
                count = pstmtUpdateGroupRoleCodePrevious.executeUpdate();
                if (count >= 0) {
                    pstmtUpdateGroupRoleCodeNew.clearParameters();
                    index = 0;
                    ++index;
                    pstmtUpdateGroupRoleCodeNew.setString(index, PretupsI.YES);
                    ++index;
                    pstmtUpdateGroupRoleCodeNew.setString(index, channelUserVO.getGroupRoleCode());
                    if (pstmtUpdateGroupRoleCodeNew.executeUpdate() <= 0) {
                    	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPADTEROLE_TABLES,null);

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("error_desc ", " DESC :" +  error);
                        }
                        errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                        errorList.add(errorVO);
                        p_con.rollback();
                        continue;
                    }
                } else {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPADTEROLE_TABLES,null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + error);
                    }
                    
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(),error);
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                commitCounter++;
                if (commitCounter == commitNumber) {
                    p_con.commit();
                    commitCounter = 0;// reset commit counter
                    recordsToBeProcessed = recordsToBeProcessed - commitNumber;
                }
            }
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isErrorEnabled()) {
            	loggerValue.setLength(0);
    			loggerValue.append(SQL_EXCEPTION);
    			loggerValue.append(sqe.getMessage());
    			LOG.error(methodName, loggerValue);
            }
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAsDefault]", "", "", "",
                loggerValue.toString());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TECHNICAL_ERROR, 0,null);

        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isErrorEnabled()) {
            	loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(ex.getMessage());
    			LOG.error(methodName, loggerValue);
            }
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateAsDefault]", "", "", "",
                loggerValue.toString());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TECHNICAL_ERROR, 0,null);
        } finally {
            try {
                if (pstmtUpdateTransferCtrlPrevious != null) {
                    pstmtUpdateTransferCtrlPrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateTransferCtrlNew != null) {
                    pstmtUpdateTransferCtrlNew.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGradeCodePrevious != null) {
                    pstmtUpdateGradeCodePrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGradeCodeNew != null) {
                    pstmtUpdateGradeCodeNew.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateCommPrfSetIdPrevious != null) {
                    pstmtUpdateCommPrfSetIdPrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateCommPrfSetIdNew != null) {
                    pstmtUpdateCommPrfSetIdNew.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGroupRoleCodePrevious != null) {
                    pstmtUpdateGroupRoleCodePrevious.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGroupRoleCodeNew != null) {
                    pstmtUpdateGroupRoleCodeNew.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
	}

	
	
	
	 /**
     * Method to add oe delete new group role, transfer control profile,
     * commission control profile and grade as default and previous as normal
     * 
     * @param p_con
     * @param p_userDetailList
     *            ArrayList
     * @param p_action 
     *            String
     * @param p_domainCode
     *            String
     * @param p_locale
     *            Locale
     * @param p_userVO
     *            UserVO
     * @param p_fileName
     *            String
     * @return errorList ArrayList;
     * @throws BTSLBaseException
     */
	public ArrayList addDeleteAsDefaultFromRest(Connection p_con, ArrayList p_userDetailList, String p_action, String p_domainCode, Locale p_locale, UserVO p_userVO, String p_fileName) throws BTSLBaseException {
		final String methodName = "addDeleteAsDefaultFromRest";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userDetailList=");
        	loggerValue.append(p_userDetailList);
        	loggerValue.append(",p_action=");
        	loggerValue.append(p_action);
        	loggerValue.append(",p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(",p_userVO=");
        	loggerValue.append(p_userVO);
        	loggerValue.append(",p_fileName=");
        	loggerValue.append(p_fileName);
            LOG.debug(methodName, loggerValue);
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        int recordsToBeProcessed = 0;
        int index = 0;
        final boolean flag = true;
        PreparedStatement pstmtUpdateTransferCtrlProfile = null;
        PreparedStatement pstmtUpdateGradeCode = null;
        PreparedStatement pstmtUpdateCommPrfSetId = null;
        PreparedStatement pstmtUpdateGroupRoleCode = null;

        final StringBuffer updateTransferCtrlProfile = new StringBuffer();
        updateTransferCtrlProfile.append("UPDATE transfer_profile SET IS_DEFAULT = ?  ");
        updateTransferCtrlProfile.append("WHERE parent_profile_id='USER' ");
        updateTransferCtrlProfile.append("AND category_code = ? AND status IN('Y','S') AND network_code=? ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateTransferCtrlProfile.append("AND profile_id=? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateTransferCtrlProfile);
			LOG.debug(methodName, loggerValue);
		}
        final StringBuffer updateGradeCode = new StringBuffer();
        updateGradeCode.append("UPDATE CHANNEL_GRADES  SET IS_DEFAULT_GRADE = ? ");
        updateGradeCode.append("WHERE status='Y' AND category_code= ? ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateGradeCode.append("AND grade_code = ? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGradeCode);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateCommPrfSetId = new StringBuffer();
        updateCommPrfSetId.append("UPDATE COMMISSION_PROFILE_SET  SET IS_DEFAULT = ? ");
        updateCommPrfSetId.append("WHERE status!='N' AND category_code= ? AND network_code= ? ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateCommPrfSetId.append("AND comm_profile_set_id = ? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateCommPrfSetId);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuffer updateGroupRoleCode = new StringBuffer();
        updateGroupRoleCode.append("UPDATE ROLES SET IS_DEFAULT = ? ");
        updateGroupRoleCode.append("WHERE role_code IN (SELECT R.role_code FROM CATEGORY_ROLES CR, ROLES R WHERE  R.status='Y' ");
        updateGroupRoleCode.append("AND CR.category_code = ? AND CR.role_code=R.role_code AND R.group_role='Y') ");
        if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
            updateGroupRoleCode.append("AND role_code = ? ");
        }
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateGroupRoleCode);
			LOG.debug(methodName, loggerValue);
		}

        try {
            pstmtUpdateTransferCtrlProfile = p_con.prepareStatement(updateTransferCtrlProfile.toString());
            pstmtUpdateGradeCode = p_con.prepareStatement(updateGradeCode.toString());
            pstmtUpdateCommPrfSetId = p_con.prepareStatement(updateCommPrfSetId.toString());
            pstmtUpdateGroupRoleCode = p_con.prepareStatement(updateGroupRoleCode.toString());
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            recordsToBeProcessed = p_userDetailList.size();
            int commitNumber = 0;
            final String ConfigCommitNumber = Constants.getProperty("USER_DEFAULT_CONFIG_COMMIT_NUMBER");
            commitNumber = commitNumber2(ConfigCommitNumber);
            for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
                if (recordsToBeProcessed < commitNumber) {
                    commitNumber = recordsToBeProcessed;
                }
                Collections.sort(p_userDetailList);
                channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
                categoryVO = channelUserVO.getCategoryVO();

                pstmtUpdateTransferCtrlProfile.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateTransferCtrlProfile.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateTransferCtrlProfile.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateTransferCtrlProfile.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateTransferCtrlProfile.setString(index, channelUserVO.getNetworkID());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateTransferCtrlProfile.setString(index, channelUserVO.getTransferProfileID());
                }
                if (pstmtUpdateTransferCtrlProfile.executeUpdate() <= 0) {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPADTETRANSFERPROFILETABLE_TABLES,null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" +error);
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }

                pstmtUpdateGradeCode.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGradeCode.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateGradeCode.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateGradeCode.setString(index, channelUserVO.getCategoryCode());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGradeCode.setString(index, channelUserVO.getUserGrade());
                }
                if (pstmtUpdateGradeCode.executeUpdate() <= 0) {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPGRADESCHANNELTABLES,null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + error);
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                pstmtUpdateCommPrfSetId.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateCommPrfSetId.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateCommPrfSetId.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateCommPrfSetId.setString(index, channelUserVO.getCategoryCode());
                ++index;
                pstmtUpdateCommPrfSetId.setString(index, channelUserVO.getNetworkID());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateCommPrfSetId.setString(index, channelUserVO.getCommissionProfileSetID());
                }
                if (pstmtUpdateCommPrfSetId.executeUpdate() <= 0) {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_COMMISSIONPROFILEsetid_TABLES,null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + error);
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(), error);
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                pstmtUpdateGroupRoleCode.clearParameters();
                index = 0;
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGroupRoleCode.setString(index, PretupsI.YES);
                } else if (PretupsI.USR_DEF_CONFIG_DELETE.equals(p_action)) {
                    ++index;
                    pstmtUpdateGroupRoleCode.setString(index, PretupsI.NO);
                }
                ++index;
                pstmtUpdateGroupRoleCode.setString(index, channelUserVO.getCategoryCode());
                if (PretupsI.USR_DEF_CONFIG_ADD.equals(p_action)) {
                    ++index;
                    pstmtUpdateGroupRoleCode.setString(index, channelUserVO.getGroupRoleCode());
                }
                if (pstmtUpdateGroupRoleCode.executeUpdate() <= 0) {
                	String error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USERDEFAULCONFIG_MSG_ERROR_UPADTEROLE_TABLES,null);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("error_desc ", " DESC :" + error);
                    }
                    errorVO = new ListValueVO("", (new Integer(channelUserVO.getRecordNumber())).toString(),error);
                    errorList.add(errorVO);
                    p_con.rollback();
                    continue;
                }
                commitCounter++;
                if (commitCounter == commitNumber) {
                    p_con.commit();
                    commitCounter = 0;// reset commit counter
                    recordsToBeProcessed = recordsToBeProcessed - commitNumber;
                }
            }
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                	p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[addDeleteAsDefault]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                	p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[addDeleteAsDefault]", "", "", "",
                loggerValue.toString());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TECHNICAL_ERROR, 0,null);

        } finally {
            try {
                if (pstmtUpdateTransferCtrlProfile != null) {
                    pstmtUpdateTransferCtrlProfile.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGradeCode != null) {
                    pstmtUpdateGradeCode.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateCommPrfSetId != null) {
                    pstmtUpdateCommPrfSetId.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateGroupRoleCode != null) {
                    pstmtUpdateGroupRoleCode.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size:");
            	loggerValue.append(errorList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return errorList;
	}
 
	/**
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_restrictedList
     *            ArrayList
     * @return String
     * @throws BTSLBaseException
     */
    public int autoo2cupdate(Connection p_con, ChannelUserVO chnlUserVO) throws BTSLBaseException {
    	final String methodName = "autoo2cupdate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = 0;
        final String unprocessedMsisdn = null;
        final StringBuffer invalidDataStrBuff = new StringBuffer();
      final String updateQuery = "UPDATE channel_users SET auto_o2c_allow=? , autoo2c_threshold_value=?, autoo2c_transaction_amt=? WHERE user_id=?";

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
        try {
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            int i = 0;
            ++i;
            pstmtUpdate.setString(i, chnlUserVO.getAutoo2callowed());
            ++i;
            pstmtUpdate.setLong(i, chnlUserVO.getAutoO2CThresholdLimit());
            ++i;
            pstmtUpdate.setLong(i, chnlUserVO.getAutoO2CTxnValue());
            ++i; 
            pstmtUpdate.setString(i, chnlUserVO.getChannelUserID());

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Exiting: autoo2callowed=" + unprocessedMsisdn);
                }
                p_con.rollback();

            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[associateRestrictedMsisdn]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.GENERAL_ERROR_PROCESSING,sqe);
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: unprocessedMsisdn:");
            	loggerValue.append(unprocessedMsisdn);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }	
	
	
}

