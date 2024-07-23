package com.txn.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.ChannelTransfrsReturnsVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class ChannelUserTxnDAO {

    private static final String AND_USER12 = "AND USR.category_code=USR_CAT.category_code ";
    private static final String AND_USER11 = " SELECT user_id from users where user_id != ? ";
    private static final String AND_USER10 = " AND USR.user_id IN ( ";
    private static final String AND_USER9 = " AND USR_CAT.domain_code=D.domain_code ";
    private static final String AND_USER8 = " AND USR_CRBY.user_id(+) = USR.created_by ";
    private static final String AND_USER7 = " AND MOD_USR.user_id(+) = USR.modified_by ";
    private static final String AND_USER6 = " AND USR.user_id = UG.user_id ";
    private static final String AND_USER5 = " AND l.lookup_type= ? ";
    private static final String AND_USER4 = " AND USR.status = l.lookup_code ";
    private static final String AND_USER3 = " AND ONR_CAT.category_code=ONR_USR.category_code ";
    private static final String AND_USER2 = " AND USR.category_code=USR_CAT.category_code ";
    private static final String AND_USER1 = " AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ";
    private static final String USER_STATUS3 = " AND USR.status <> ? ";
    private static final String USER_STATUS2 = " AND USR.status =? ";
    private static final String USER_STATUS1 = " AND USR.barred_deletion_batchid IS NULL";
    private static final String USER_AUTHENTICATE2 = "WHERE USR_PHONE.msisdn=? AND USR_PHONE.user_id=USR.user_id ";
    private static final String USER_AUTHENTICATE1 = "FROM user_phones USR_PHONE,users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, users MOD_USR, ";
    private static final String USER_DETAILS7 = "PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn,USR_CAT.category_type, ";
    private static final String USER_DETAILS6 = "PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,UG.grph_domain_code, GD.grph_domain_name, ";
    private static final String USER_DETAILS5 = "USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed, USR_CAT.restricted_msisdns, ";
    private static final String USER_DETAILS4 = "USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ";
    private static final String USER_DETAILS3 = "USR.owner_id,USR.allowed_ip,USR.allowed_days, D.domain_name, ";
    private static final String USER_DETAILS2 = "USR.login_id,USR.password password,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ";
    private static final String USER_DETAILS1 = "USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ";
    private static final String PHONE_PROFILE = "phone_profile";

    private ChannelUserTxnQry channelUserTxnQry;
    /**
     * Field LOG.
     */
    private static final Log LOG = LogFactory.getLog(ChannelUserTxnDAO.class.getName());
    private static OperatorUtilI operatorUtilI = null;
    static {
        try {
            operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("static block :", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "", "", "", "", "Exception while loading the operator util class in class :" + ChannelUserDAO.class.getName() + ":" + e.getMessage());
        }
    }
    
    public ChannelUserTxnDAO(){
    	channelUserTxnQry = (ChannelUserTxnQry)ObjectProducer.getObject(QueryConstants.CHANNEL_USER_TXN_QRY, QueryConstants.QUERY_PRODUCER);

    }

    /*
     * if the request thru external system for c2c transfer ,return,withdraw.
     * then the transaction can be performed thru either receiver
     * msisdn,receiver external code, receiver loginid
     */

    public ChannelUserVO loadChannelUserDetailsForTransferIfReqExtgw(Connection p_con, String p_extCode, String p_loginId, Date p_applicableFromDate) throws Exception {
        final String METHOD_NAME = "loadChannelUserDetailsForTransferIfReqExtgw";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, " Entered p_extCode = " + p_extCode + ", p_loginId = " + p_loginId + ",p_applicableFromDate=" + p_applicableFromDate);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        Boolean isTrfRuleUserlevelAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        ChannelUserVO channelUserVO = null;
        try {
        	String selQuery = channelUserTxnQry.loadChannelUserDetailsForTransferIfReqExtgwQry(p_extCode);
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " Query : " + selQuery);
            }
            pstmt = p_con.prepareStatement(selQuery);
            int i = 0;
            if (!BTSLUtil.isNullString(p_extCode)) {
                pstmt.setString(++i, p_extCode);
            } else {
                pstmt.setString(++i, p_loginId);
            }

            pstmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_applicableFromDate));
            rs = pstmt.executeQuery();
            String userID;
            if (rs.next()) {
                channelUserVO = new ChannelUserVO();
                userID = rs.getString("user_id");
                channelUserVO.setUserID(userID);
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setNetworkName(rs.getString("network_name"));
                channelUserVO.setLoginID(rs.getString("login_id"));
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
                channelUserVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));

                channelUserVO.setTransferProfileName(rs.getString("profile_name"));
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
                channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelUserVO.setDualCommissionType(rs.getString("dual_comm_type"));
                if(isChannelSOSEnable){
                	channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
                	channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
                	channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
                }

                CategoryVO categoryVO = new CategoryVO();
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
                if (isTrfRuleUserlevelAllow) {
                    channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                }
                channelUserVO.setCategoryVO(categoryVO);
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, " SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(METHOD_NAME, " Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }
            LOG.debug("loadChannelUserDetailsForTransferIfReqExtgw ::", " Exiting channelUserVO=" + channelUserVO);
        }
        return channelUserVO;
    }

    /**
     * Method loadOtherUserBalanceVO This method will return true if balance
     * check allowed to other user
     * 
     * @param p_userCode
     *            java.lang.String
     * @param p_channelUserVO
     *            ChannelUserVO *
     * @return channelUserVO
     * @return channelUserVO
     * @throws BTSLBaseException
     * @author manoj kumar
     */

    public ChannelUserVO loadOtherUserBalanceVO(Connection p_con, String p_userCode, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadOtherUserBalanceVO";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_userCode=" + p_userCode + ",p_channelUserVO=" + p_channelUserVO);
        }
        PreparedStatement pstmt = null;
        ChannelUserVO channelUserVO = new ChannelUserVO();
        ResultSet rs = null;
        
        try {
           pstmt = channelUserTxnQry.loadOtherUserBalanceVOQry(p_con, p_userCode, p_channelUserVO);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQL Exception" + sqe.getMessage());
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadOtherUserBalanceVO]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, " Exception" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadOtherUserBalanceVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting channelUserVO :" + channelUserVO.toString());
            }
        }

        return channelUserVO;
    }

    /**
     * this method calulate the daily returns and transfer from channel
     * 
     * @param p_con
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_date
     * @return arrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserChannelInTransferList(Connection p_con, ChannelUserVO p_channelUserVO, java.util.Date p_date) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserChannelInTransferList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered  p_channelUserVO " + p_channelUserVO.toString() + ", p_date" + p_date);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

       String sqlSelect =channelUserTxnQry.loadUserChannelInTransferListQry(p_channelUserVO);
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList transfrsList = new ArrayList();
        try {
            int i = 0;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
            pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_date));
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(++i, p_channelUserVO.getUserID());
            if (p_channelUserVO.isStaffUser()) {
                pstmt.setString(++i, p_channelUserVO.getActiveUserID());
            }
            pstmt.setString(++i, p_channelUserVO.getNetworkID());
            pstmt.setString(++i, PretupsI.C2S_MODULE);
            rs = pstmt.executeQuery();
            ChannelTransfrsReturnsVO transfersVO = null;
            while (rs.next()) {
                transfersVO = new ChannelTransfrsReturnsVO();
                transfersVO.setProductCode(rs.getString("product_code"));
                transfersVO.setProductShortCode(rs.getLong("product_short_code"));
                transfersVO.setShortName(rs.getString("short_name"));
                transfersVO.setTransfes(rs.getLong("transfers"));
                transfersVO.setReturns(rs.getLong("returns1"));
                transfrsList.add(transfersVO);
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserChannelInTransferList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error("", "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserChannelInTransferList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:   transfrsList Size =" + transfrsList.size());
            }
        }
        return transfrsList;
    }

    /**
     * this method calulate the daily returns and transfer to the channel
     * 
     * @param p_con
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_date
     * @return arrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserChannelOutTransferList(Connection p_con, ChannelUserVO p_channelUserVO, java.util.Date p_date) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserChannelOutTransferList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered  p_channelUserVO " + p_channelUserVO.toString() + ", p_date" + p_date);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlSelect =channelUserTxnQry.loadUserChannelOutTransferListQry(p_channelUserVO);
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList transfrsOutList = new ArrayList();
        try {
            int i = 0;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
            pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_date));
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            // if(!p_channelUserVO.isStaffUser())
            pstmt.setString(++i, p_channelUserVO.getUserID());
            if (p_channelUserVO.isStaffUser()) {
                pstmt.setString(++i, p_channelUserVO.getActiveUserID());
            }
            pstmt.setString(++i, p_channelUserVO.getNetworkID());
            pstmt.setString(++i, PretupsI.C2S_MODULE);

            rs = pstmt.executeQuery();
            ChannelTransfrsReturnsVO transfersVO = null;
            while (rs.next()) {
                transfersVO = new ChannelTransfrsReturnsVO();
                transfersVO.setProductCode(rs.getString("product_code"));
                transfersVO.setProductShortCode(rs.getLong("product_short_code"));
                transfersVO.setShortName(rs.getString("short_name"));
                transfersVO.setTransfes(rs.getLong("transfers"));
                transfersVO.setReturns(rs.getLong("returns1"));
                transfrsOutList.add(transfersVO);
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserChannelOutTransferList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error("", "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserChannelOutTransferList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("", "Exiting:   transfrsList Size =" + transfrsOutList.size());
            }
        }
        return transfrsOutList;
    }

    /**
     * this method calulate the daily transfer to the subscriber Method
     * loadUserSubscriberOutTransferList() This method load the subscribers (as
     * sender) transaction list.
     * 
     * @param p_con
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_date
     * @return arrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserSubscriberOutTransferList(Connection p_con, ChannelUserVO p_channelUserVO, java.util.Date p_date) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserSubscriberOutTransferList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered  p_channelUserVO " + p_channelUserVO.toString() + ", p_date" + p_date);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
        ArrayList subscriberTransfrsOutList = new ArrayList();
        try {
        	
           pstmt = channelUserTxnQry.loadUserSubscriberOutTransferListQry(p_con,operatorUtilI,p_channelUserVO,p_date);
            rs = pstmt.executeQuery();
            ChannelTransfrsReturnsVO transfersVO = null;
            while (rs.next()) {
                transfersVO = new ChannelTransfrsReturnsVO();
                transfersVO.setProductCode(rs.getString("product_code"));
                transfersVO.setProductShortCode(rs.getLong("product_short_code"));
                transfersVO.setShortName(rs.getString("short_name"));
                transfersVO.setTransfes(rs.getLong("transfers"));
                transfersVO.setServiceName(rs.getString("name"));
                subscriberTransfrsOutList.add(transfersVO);
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserSubscriberOutTransferList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error("", "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserSubscriberOutTransferList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("", "Exiting:   subscriberTransfrsOutList Size =" + subscriberTransfrsOutList.size());
            }
        }
        return subscriberTransfrsOutList;
    }

    /**
     * update the user phones information with the last transfer information
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_msisdn
     * @param p_userID
     *            String
     * @param p_checkPrimaryMsisdn
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int updateUserPhoneAfterTxn(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_msisdn, String p_userID, boolean p_checkPrimaryMsisdn) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        final String METHOD_NAME = "updateUserPhoneAfterTxn";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered: p_channelTransferVO= " + p_channelTransferVO + ",p_msisdn=" + p_msisdn + ",p_userID=" + p_userID + ",p_checkPrimaryMsisdn=" + p_checkPrimaryMsisdn);
        }
        try {
            StringBuffer strBuff = new StringBuffer(" UPDATE user_phones SET last_transaction_status = ?, last_transaction_on = ?, ");
            strBuff.append(" last_transfer_id =?, last_transfer_type = ?,modified_by = ? , modified_on = ? ");
            strBuff.append(" WHERE user_id = ?  ");
            if (!p_checkPrimaryMsisdn) {
                strBuff.append("AND msisdn = ? ");
            } else {
                strBuff.append("AND primary_number='Y' ");
            }

            String updateQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Query sqlUpdate:" + updateQuery);
            }

            psmtUpdate = p_con.prepareStatement(updateQuery);
            int i = 0;
            psmtUpdate.setString(++i, p_channelTransferVO.getStatus());
            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            psmtUpdate.setString(++i, p_channelTransferVO.getTransferID());
            psmtUpdate.setString(++i, p_channelTransferVO.getType());
            psmtUpdate.setString(++i, p_channelTransferVO.getModifiedBy());
            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            // where
            psmtUpdate.setString(++i, p_userID);
            if (!p_checkPrimaryMsisdn) {
                psmtUpdate.setString(++i, p_msisdn);
            }

            updateCount = psmtUpdate.executeUpdate();

            if (updateCount <= 0) {
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
            }
        } catch (BTSLBaseException be) {
            LOG.error(METHOD_NAME, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException: " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserPhoneAfterTxn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception: " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserPhoneAfterTxn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * 
     * @param p_con
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     */

    public ChannelUserVO loadUserMsisdnAndStatus(Connection p_con, String p_userID) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserMsisdnAndStatus";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered with: userID=" + p_userID);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;

        try {

            StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append(" SELECT u.msisdn,u.status, up.phone_language, up.country FROM USERS u,USER_PHONES up ");
            selectQueryBuff.append(" WHERE u.user_id=? AND u.user_id = up.user_id and up.primary_number='Y' ");
            String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "select query:" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
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
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[loadUserMsisdnAndStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[loadUserMsisdnAndStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (channelUserVO != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exited MSISDN=" + channelUserVO.getMsisdn() + " Status=" + channelUserVO.getStatus());
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exited User Not Found " + p_userID);
                }
            }
        }
        return channelUserVO;
    }

    public String product(Connection p_con, String p_code) throws BTSLBaseException {
        final String METHOD_NAME = "product";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered with: Product Code=" + p_code);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String pro_name = null;
        try {

            StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append(" SELECT product_code from products ");
            selectQueryBuff.append(" where product_short_code= ? and status='Y' ");
            String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "select query:" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_code);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                pro_name = rs.getString("product_code");

            }
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[product]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_REQ_UNDERPROCESS);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[product]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (pro_name != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exited Product Name=" + pro_name);
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exited Product Not Found " + p_code);
                }
            }
        }
        return pro_name;
    }

    /**
     * Method updateLanguageAndCountry This method will update the language and
     * country in user phones according to msisdn
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

    public int updateLanguageInfo(Connection p_con, String p_lang, String p_country, String p_userPhoneId) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("updateLanguageInfo ", " Entered p_userPhoneId:" + p_userPhoneId + ",p_lang=" + p_lang + ",p_country" + p_country);
        }

        PreparedStatement pstmt = null;
        String qry = "UPDATE user_phones SET phone_language =?,country=? WHERE USER_PHONES_ID=? ";
        int updCount = -1;
        final String METHOD_NAME = "updateLanguageInfo";
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(" updateLanguageInfo ", " Query :: " + qry);
            }
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_lang);
            pstmt.setString(2, p_country);
            pstmt.setString(3, p_userPhoneId);
            // Execute Query
            updCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateLanguageInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateLanguageInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.error(" getRoamLocationList ", "  Exception Closing Prepared Stmt: " + ex.getMessage());
                LOG.errorTrace("getRoamLocationList ", ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("updateLanguageInfo ", " Exiting updCount ==" + updCount);
            }
        }

        return updCount;
    }

    /**
     * New Method Method loadERPChnlUserDetailsByExtCode() This method loads the
     * channel users as well as OPT user on basis of external code Author@Puneet
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
    public ChannelUserVO loadERPChnlUserDetailsByExtCode(Connection p_con, String p_extCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadERPChnlUserDetailsByExtCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_extCode:" + p_extCode);
        }
        PreparedStatement pstmtSelect = null;
        ChannelUserVO channelUserVO = null;
        ChannelUserDAO channelUserDAO = null;
        ResultSet rs = null;
        try {
            channelUserDAO = new ChannelUserDAO();
            pstmtSelect=channelUserTxnQry.loadERPChnlUserDetailsByExtCodeQry(p_con, p_extCode);
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
                channelUserVO.setCreationType(rs.getString("creation_type"));
                channelUserVO.setPreviousStatus(rs.getString("previous_status"));
                channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
                channelUserVO.setOutletCode(rs.getString("outlet_code"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setUserProfileID(rs.getString("user_profile_id"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setTransferProfileStatus(rs.getString("tpstatus"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setCommissionProfileStatus(rs.getString("csetstatus"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                CategoryVO categoryVO = new CategoryVO();
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
                UserPhoneVO userPhoneVO = new UserPhoneVO();
                userPhoneVO.setUserPhonesId(rs.getString("user_phones_id"));
                userPhoneVO.setMsisdn(rs.getString("prmsisdn"));
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
                userPhoneVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("uphones_created")));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                channelUserVO.setNetworkCode(rs.getString("reference_network_code"));
                channelUserVO.setAssociatedServiceTypeList(channelUserDAO.loadUserServicesList(p_con, channelUserVO.getUserID()));
            }
            return channelUserVO;
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadERPChnlUserDetailsByExtCode]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadERPChnlUserDetailsByExtCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting channelUserVO:" + channelUserVO);
            }
        }// end of finally
    } // loadERPChnlUserDetailsByExtCode Method Ends

    /**
     * Method loadUsersListForExtApi. This method is used to load all the
     * information of the parent user,owner user and th creator user on the
     * basis of LoginId and msisdn
     * 
     * @author ankur.dhawan
     * @param p_con
     *            Connection
     * @param p_loginId
     *            String
     * @param p_parentMsisdn
     *            String
     * @param p_ownerMsisdn
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @return ArrayList<ChannelUserVO>
     * @throws BTSLBaseException
     */
    public ArrayList<ChannelUserVO> loadUsersListForExtApi(Connection p_con, String p_loginId, String p_parentMsisdn, String p_ownerMsisdn, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String METHOD_NAME = "loadUsersListForExtApi";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_loginId= " + p_loginId + " p_parentMsisdn=" + p_parentMsisdn + " p_ownerMsisdn=" + p_ownerMsisdn + " p_stausUsed=" + p_statusUsed + ",p_staus=" + p_status);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
        ArrayList<ChannelUserVO> usersList = new ArrayList<ChannelUserVO>();
       
        try {
        	pstmtSelect =channelUserTxnQry.loadUsersListForExtApiQry(p_con, p_loginId, p_parentMsisdn, p_ownerMsisdn, p_statusUsed, p_status);
           
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelUserVO = new ChannelUserVO();
                channelUserVO.setBatchID(rs.getString("batch_id"));
                channelUserVO.setCreationType(rs.getString("creation_type"));
                channelUserVO.setUserID(rs.getString("usr_user_id"));
                channelUserVO.setUserName(rs.getString("usr_user_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
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

                CategoryVO categoryVO = CategoryVO.getInstance();
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
                usersList.add(channelUserVO);
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersListForExtApi]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        } catch (Exception ex) {
            LOG.error(METHOD_NAME, "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersListForExtApi]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting: usersList=" + usersList);
            }
        }
        return usersList;

    }

    /**
     * @param p_con
     *            Connection
     * @param p_ownerVO
     *            ChannelUserVO
     * @param p_parentVO
     *            ChannelUserVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean validateParentAndOwner(Connection p_con, ChannelUserVO p_ownerVO, ChannelUserVO p_parentVO, String p_userCategory, String p_userGeography) throws BTSLBaseException {
        final String METHOD_NAME = "validateParentAndOwner";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered  p_ownerVO" + p_ownerVO + " p_parentVO" + p_parentVO + " p_userCategory" + p_userCategory + " p_userGeography" + p_userGeography);
        }

        boolean isValid = false;
        String selectParentHierarchy = null;
        String selectOwnerHierarchy = null;
        String selectGeography = null;
        String selectGeographyP = null;
        PreparedStatement pstmtSelectParentHierarchy = null;
        PreparedStatement pstmtSelectOwnerHierarchy = null;
        PreparedStatement pstmtSelectGeography = null;
        ResultSet rsSelectParentHierarchy = null;
        ResultSet rsSelectOwnerHierarchy = null;
        ResultSet rsSelectGeography = null;

        StringBuffer strBuff = new StringBuffer("select (1) from CHNL_TRANSFER_RULES ctr");
        strBuff.append(" WHERE ctr.from_category=? AND ctr.to_category=? AND ctr.status='Y' ");
        strBuff.append(" AND ctr.parent_association_allowed='Y' ");
        selectParentHierarchy = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Parent hierarchy Query =" + selectParentHierarchy);
        }

        strBuff.delete(0, strBuff.length());

        strBuff.append("SELECT (1) FROM users u ");
        strBuff.append("WHERE u.user_id=? AND u.status=? AND u.owner_id=? ");
        selectOwnerHierarchy = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Owner hierarchy Query =" + selectOwnerHierarchy);
        }

        strBuff.delete(0, strBuff.length());


        strBuff.append("SELECT (1) FROM geographical_domains gd WHERE gd.parent_grph_domain_code=? ");
        selectGeographyP = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Parent Geography Query 2=" + selectGeographyP);
        }

        try {
            pstmtSelectParentHierarchy = p_con.prepareStatement(selectParentHierarchy.toString());
            pstmtSelectParentHierarchy.setString(1, p_parentVO.getCategoryCode());
            pstmtSelectParentHierarchy.setString(2, p_userCategory);
            rsSelectParentHierarchy = pstmtSelectParentHierarchy.executeQuery();
            if (!rsSelectParentHierarchy.next()) {
                throw new BTSLBaseException(this, "ChannelUserDAO[validateParentAndOwner]", PretupsErrorCodesI.EXT_USRADD_PARENT_HIERARCY_INVALID);
            }

            pstmtSelectOwnerHierarchy = p_con.prepareStatement(selectOwnerHierarchy.toString());
            pstmtSelectOwnerHierarchy.setString(1, p_parentVO.getUserID());
            pstmtSelectOwnerHierarchy.setString(2, PretupsI.YES);
            pstmtSelectOwnerHierarchy.setString(3, p_ownerVO.getUserID());
            rsSelectOwnerHierarchy = pstmtSelectOwnerHierarchy.executeQuery();
            if (!rsSelectOwnerHierarchy.next()) {
                throw new BTSLBaseException(this, "ChannelUserDAO[validateParentAndOwner]", PretupsErrorCodesI.EXT_USRADD_OWNER_HIERARCY_INVALID);
            }

            if (p_userGeography.equals(p_parentVO.getGeographicalCode())) {
                pstmtSelectGeography = p_con.prepareStatement(selectGeographyP.toString());
                pstmtSelectGeography.setString(1, p_userGeography);
                rsSelectGeography = pstmtSelectGeography.executeQuery();
                if (rsSelectGeography.next()) {
                    throw new BTSLBaseException(this, "ChannelUserDAO[validateParentAndOwner]", PretupsErrorCodesI.EXT_USRADD_PARENT_GEOGRAPHY_INVALID);
                }
            } else {
            	
            	
            	pstmtSelectGeography = channelUserTxnQry.validateParentAndOwnerQry(p_con, p_userGeography, p_parentVO);
                rsSelectGeography = pstmtSelectGeography.executeQuery();
                if (!rsSelectGeography.next()) {
                    throw new BTSLBaseException(this, "ChannelUserDAO[validateParentAndOwner]", PretupsErrorCodesI.EXT_USRADD_PARENT_GEOGRAPHY_INVALID);
                }
            }

            // rsSelectGeography=null;
            // pstmtSelectGeography.setString(1, p_userGeography);
            // pstmtSelectGeography.setString(2,
            // p_ownerVO.getGeographicalCode());
            // rsSelectGeography=pstmtSelectGeography.executeQuery();
            // if(!rsSelectGeography.next())
            // throw new BTSLBaseException(this,
            // "ChannelUserDAO[validateParentAndOwner]",
            // PretupsErrorCodesI.EXT_USRADD_OWNER_GEOGRAPHY_INVALID);

            // If all the validations are true return true
            isValid = true;
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateParentAndOwner]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "ChannelUserDAO[validateParentAndOwner]", PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        } catch (BTSLBaseException be) {
            LOG.error(METHOD_NAME, "Exception " + be.getMessage());
            LOG.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateParentAndOwner]", "", "", "", "Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[validateParentAndOwner]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "ChannelUserDAO[validateParentAndOwner]", PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        } finally {
            try {
                if (rsSelectGeography != null) {
                    rsSelectGeography.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectOwnerHierarchy != null) {
                    rsSelectOwnerHierarchy.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectParentHierarchy != null) {
                    rsSelectParentHierarchy.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectParentHierarchy != null) {
                    pstmtSelectParentHierarchy.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectOwnerHierarchy != null) {
                    pstmtSelectOwnerHierarchy.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectGeography != null) {
                    pstmtSelectGeography.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("validateParentAndOwner ", "isValid: " + isValid);
            }
        }
        return isValid;
    }

    /**
     * Method updateImeiAndEncKey This method will update the Imei And
     * Encryption Key in user phones according to msisdn
     * 
     * @param p_con
     *            Connection
     * @param p_channelUserVO
     *            ChannelUserVO
     * @return the number of records updated
     * @throws BTSLBaseException
     */

    public int updateImeiAndEncKey(Connection p_con, ChannelUserVO p_channelUserVO,String mHash,String token) throws BTSLBaseException {
        final String METHOD_NAME = "updateImeiAndEncKey";
        if (LOG.isDebugEnabled()) {
            LOG.debug("updateLanguageAndCountry ", " Entered p_UserID:" + p_channelUserVO.getUserID());
        }
        
        PreparedStatement pstmt = null;
        String qry = "UPDATE USER_PHONES SET IMEI =?,DECRYPTION_KEY=?,MHASH=?,TOKEN=?,TOKEN_LASTUSED_DATE=? WHERE USER_ID=? ";
        int updCount = -1;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(" updateLanguageAndCountry ", " Query :: " + qry);
            }
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_channelUserVO.getImei());
            pstmt.setString(2, p_channelUserVO.getDecryptionKey());
            pstmt.setString(3, mHash);
            pstmt.setString(4, token);
            pstmt.setTimestamp(5,BTSLUtil.getSQLDateTimeFromUtilDate(new Date()));
            pstmt.setString(6, p_channelUserVO.getUserID());
            // Execute Query
            updCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateImeiAndEncKey]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateImeiAndEncKey]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " Exiting updCount ==" + updCount);
            }
        }
        return updCount;
    }
    
    public int updateMhashToken(Connection p_con,ChannelUserVO p_channelUserVO,String mHash,String token) throws BTSLBaseException {
    	 final String METHOD_NAME = "updateMhashToken";
         if (LOG.isDebugEnabled()) {
             LOG.debug("updateMhashToken ", " Entered p_UserID:" + p_channelUserVO.getUserID());
         }
         PreparedStatement pstmt = null;
         String qry = "UPDATE USER_PHONES SET MHASH=?,TOKEN=?,TOKEN_LASTUSED_DATE=? WHERE USER_ID=? ";
         int updCount = -1;
         try {
             if (LOG.isDebugEnabled()) {
                 LOG.debug(" updateMhashToken ", " Query :: " + qry);
             }
             // Get Prepared Statement
             pstmt = p_con.prepareStatement(qry);        
             pstmt.setString(1, mHash);
             pstmt.setString(2, token);
             pstmt.setTimestamp(3,BTSLUtil.getSQLDateTimeFromUtilDate(new Date()));
             pstmt.setString(4, p_channelUserVO.getUserID());
             // Execute Query
             updCount = pstmt.executeUpdate();
         } catch (SQLException sqe) {
             LOG.error(METHOD_NAME, "SQLException " + sqe.getMessage());
             LOG.errorTrace(METHOD_NAME, sqe);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTxnDAO[updateMhashToken]", "", "", "", "SQL Exception:" + sqe.getMessage());
             throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
         } catch (Exception e) {
             LOG.error(METHOD_NAME, "Exception " + e.getMessage());
             LOG.errorTrace(METHOD_NAME, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTxnDAO[updateMhashToken]", "", "", "", "Exception:" + e.getMessage());
             throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
         } finally {
             try {
                 if (pstmt != null) {
                     pstmt.close();
                 }
             } catch (Exception ex) {
                 LOG.errorTrace(METHOD_NAME, ex);
             }
             if (LOG.isDebugEnabled()) {
                 LOG.debug(METHOD_NAME, " Exiting updCount ==" + updCount);
             }
         }
         return updCount;
    }

    public ChannelUserVO getMyNumber(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "getMyNumber";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_msisdn=" + p_msisdn);
        }
        
        ChannelUserVO channelUserVO = new ChannelUserVO();
        

        try {
            String str = "Select MSISDN from USERS where MSISDN=? ";
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "select query:" + str);
            }
            try(PreparedStatement psmt = p_con.prepareStatement(str);)
            {
            psmt.setString(1, p_msisdn);
            try(ResultSet rs = psmt.executeQuery();)
            {
            while (rs.next()) {
                channelUserVO.setMsisdn(rs.getString("MSISDN"));
            }
            }
            }
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changePin]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }
        return channelUserVO;
    }

    /**
     * this method used to Change the msisdn on the basis of msisdn and user_id
     * 
     * @param p_con
     * @param newMsisdn
     *            String
     * @param p_channelUserVO
     *            ChannelUserVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateUserPhonePrimaryMsisdn(Connection p_con, String newMsisdn, ChannelUserVO p_channelUserVO) throws BTSLBaseException {

        final String METHOD_NAME = "updateUserPhonePrimaryMsisdn";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered newMsisdn = " + newMsisdn + ", subscriberVO : = " + p_channelUserVO);
        }
        PreparedStatement psmt = null;
        PreparedStatement psmt1 = null;
        int updateCount = 0;
        try {
            StringBuffer strBuff = new StringBuffer(" UPDATE user_phones set msisdn = ?,  ");
            strBuff.append("  modified_by = ? , modified_on = ? WHERE user_id =? and msisdn=? and PRIMARY_NUMBER='Y'");
            String query = strBuff.toString();

            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "update query for user_phones:" + query);
            }

            psmt = p_con.prepareStatement(query);
            psmt.setString(1, newMsisdn);
            psmt.setString(2, p_channelUserVO.getModifiedBy());
            psmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_channelUserVO.getModifiedOn()));
            psmt.setString(4, p_channelUserVO.getUserPhoneVO().getUserId());
            psmt.setString(5, p_channelUserVO.getUserPhoneVO().getMsisdn());
            updateCount = psmt.executeUpdate();

            updateCount = 0;

            strBuff = new StringBuffer(" UPDATE users set msisdn = ?,  ");
            strBuff.append("  modified_by = ? , modified_on = ? WHERE msisdn =? and user_id =? ");
            query = strBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "update query for users:" + query);
            }
            psmt1 = p_con.prepareStatement(query);
            psmt1.setString(1, newMsisdn);
            psmt1.setString(2, p_channelUserVO.getModifiedBy());
            psmt1.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_channelUserVO.getModifiedOn()));
            psmt1.setString(4, p_channelUserVO.getUserPhoneVO().getMsisdn());
            psmt1.setString(5, p_channelUserVO.getUserPhoneVO().getUserId());
            updateCount = psmt1.executeUpdate();

        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserPhonePrimaryMsisdn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[updateUserPhonePrimaryMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmt1 != null) {
                    psmt1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * this method used to Change ETop Up Recharge Status
     * 
     * @param p_con
     * @param status
     *            String
     * @param p_requestVO
     *            RequestVO
     * @return int
     * @throws BTSLBaseException
     */
    public int changeETopUpRechargeStatus(Connection p_con, String status, RequestVO p_requestVO) throws BTSLBaseException {

        final String METHOD_NAME = "changeETopUpRechargeStatus";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered newMsisdn=" + status + ", subscriberVO : =" + p_requestVO);
        }
        PreparedStatement psmtInsert = null;
        Statement stmt = null;
        ResultSet rset = null;
        int count = 0;
        try {
            ChannelUserVO channelUserVO = null;
            StringBuffer strBuff;

            ChannelUserVO userVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!userVO.isStaffUser()) {
                channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            } else {
                channelUserVO = ((ChannelUserVO) p_requestVO.getSenderVO()).getStaffUserDetails();
            }

            if ("1".equalsIgnoreCase(status)) {
                strBuff = new StringBuffer(" SELECT * FROM  BARRED_MSISDNS where MSISDN =" + channelUserVO.getUserPhoneVO().getMsisdn());
                String selectQuery = strBuff.toString();
                stmt = p_con.createStatement();
                rset = stmt.executeQuery(selectQuery);
                while (rset.next()) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_MSISDN_RECHARGE_ALREADY);
                    p_requestVO.setSuccessTxn(false);
                    return count;
                }

                strBuff = new StringBuffer(" INSERT into BARRED_MSISDNS (MODULE, NETWORK_CODE, MSISDN, ");
                strBuff.append("  NAME, USER_TYPE, BARRED_TYPE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, BARRED_REASON, CREATED_DATE) ");
                strBuff.append("  VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");

                String insertQuery = strBuff.toString();
                Date today = new Date();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Query sqlInsert:" + insertQuery);
                }
                int rCount = 1;
                psmtInsert = p_con.prepareStatement(insertQuery);
                psmtInsert.setString(rCount, p_requestVO.getModule());
                rCount++;
                psmtInsert.setString(rCount, channelUserVO.getNetworkID());
                rCount++;
                psmtInsert.setString(rCount, channelUserVO.getUserPhoneVO().getMsisdn());
                rCount++;
                psmtInsert.setString(rCount, null);
                rCount++;
                psmtInsert.setString(rCount, PretupsI.USER_TYPE_SENDER);
                rCount++;
                psmtInsert.setString(rCount, PretupsI.BARRED_TYPE_SYSTEM);
                rCount++;
                psmtInsert.setTimestamp(rCount, BTSLUtil.getTimestampFromUtilDate(today));
                rCount++;
                psmtInsert.setString(rCount, PretupsI.BARRED_TYPE_SYSTEM);
                rCount++;
                psmtInsert.setTimestamp(rCount, BTSLUtil.getTimestampFromUtilDate(today));
                rCount++;
                psmtInsert.setString(rCount, PretupsI.BARRED_TYPE_SYSTEM);
                rCount++;
                psmtInsert.setString(rCount, PretupsI.BARRED_TYPE_SYSTEM);
                rCount++;
                psmtInsert.setTimestamp(rCount, BTSLUtil.getTimestampFromUtilDate(today));
                rCount++;

                count = psmtInsert.executeUpdate();
            } else {
                strBuff = new StringBuffer(" SELECT * FROM  BARRED_MSISDNS where MSISDN =" + channelUserVO.getUserPhoneVO().getMsisdn());
                String selectQuery = strBuff.toString();
                stmt = p_con.createStatement();
                rset = stmt.executeQuery(selectQuery);
                while (!rset.next()) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_MSISDN_RECHARGE_NOT);
                    p_requestVO.setSuccessTxn(false);
                    return count;
                }

                strBuff = new StringBuffer(" DELETE FROM BARRED_MSISDNS where MSISDN = ?");
                String deleteQuery = strBuff.toString();
                psmtInsert = p_con.prepareStatement(deleteQuery);
                psmtInsert.setString(1, channelUserVO.getUserPhoneVO().getMsisdn());
                count = psmtInsert.executeUpdate();
            }
        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changeETopUpRechargeStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[changeETopUpRechargeStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (stmt != null) {
                	stmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:updateCount=" + count);
            }
        }
        return count;

    }

    public ArrayList getChannelUserInfo(Connection p_con, String p_msisdn) throws BTSLBaseException {

        final String METHOD_NAME = "getChannelUserInfo";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_msisdn=" + p_msisdn);
        }
        PreparedStatement pstmt = null;
        ArrayList list = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = new ChannelUserVO();

        try {
            list = new ArrayList();
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT DISTINCT u.employee_code,u.pswd_reset,up.pin_reset,u.category_code,u.user_id, cu.associated_msisdn, u.category_code,");
            strBuff.append("u.external_code, u.created_on, u.status,cu.associated_msisdn, cu.associated_msisdn_mdate,UP.phone_language ");
            strBuff.append(" FROM users u, channel_users cu, user_phones UP  ");
            strBuff.append(" WHERE u.USER_ID=cu.USER_ID AND u.USER_ID=up.USER_ID AND u.MSISDN=? AND u.STATUS='Y'");

            String sqlSelect = strBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "select query:" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_msisdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                channelUserVO.setCategoryCode(rs.getString("CATEGORY_CODE"));
                channelUserVO.setUserID(rs.getString("USER_ID"));
                channelUserVO.setAsscMsisdnDate(rs.getDate("ASSOCIATED_MSISDN_MDATE"));
                channelUserVO.setExternalCode(rs.getString("EXTERNAL_CODE"));
                channelUserVO.setCreatedOn(rs.getTimestamp("CREATED_ON"));
                channelUserVO.setLanguage(rs.getString("PHONE_LANGUAGE"));
                channelUserVO.setStatus(rs.getString("STATUS"));
                channelUserVO.setPasswordReset(rs.getString("pswd_reset"));
                channelUserVO.setPinReset(rs.getString("pin_reset"));
                channelUserVO.setCategoryCode(rs.getString("CATEGORY_CODE"));
                channelUserVO.setEmpCode(rs.getString("EMPLOYEE_CODE"));
                channelUserVO.setAssociatedServiceTypeList(loadUserServicesNameList(p_con, channelUserVO.getUserID()));
                channelUserVO.setAsscMsisdnList(getAssociatedMsisdn(p_con, p_msisdn, channelUserVO.getCategoryCode()));
                list.add(channelUserVO);
            }

        } catch (SQLException sqle) {
            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[ChannelUserInfo]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting: channelUserVO=" + channelUserVO);
            }
        }
        return list;
        /*
         * ArrayList list=null;
         * list=loadUserBalances(p_con,p_networkCode,p_roamNetworkCode,p_userID);
         */
    }

    public ArrayList getAssociatedMsisdn(Connection p_con, String msisdn, String p_categoryCode) throws BTSLBaseException {
        final String METHOD_NAME = "getAssociatedMSISDN";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_msisdn=" + msisdn);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        // Modification for Service Management [by Vipul]

        if ("MSUSR".equalsIgnoreCase(p_categoryCode)) {
            strBuff.append(" SELECT cu.ASSOCIATED_MSISDN,cu.ASSOCIATED_MSISDN_TYPE,cu.ASSOCIATED_MSISDN_CDATE FROM users u,CHANNEL_USERS cu");
            strBuff.append(" WHERE cu.USER_ID=u.USER_ID and u.MSISDN=?");
        } else {
            strBuff.append(" SELECT u.MSISDN,cu.ASSOCIATED_MSISDN_TYPE,cu.ASSOCIATED_MSISDN_CDATE FROM users u,CHANNEL_USERS cu");
            strBuff.append(" WHERE cu.USER_ID=u.USER_ID and cu.ASSOCIATED_MSISDN=?");
        }
        String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, msisdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if ("MSUSR".equalsIgnoreCase(p_categoryCode)) {
                    list.add(new ListValueVO(rs.getString("ASSOCIATED_MSISDN"), rs.getString("ASSOCIATED_MSISDN_TYPE") + "," + rs.getString("ASSOCIATED_MSISDN_CDATE")));
                } else {
                    list.add(new ListValueVO(rs.getString("MSISDN"), rs.getString("ASSOCIATED_MSISDN_TYPE") + "," + rs.getString("ASSOCIATED_MSISDN_CDATE")));
                }
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(METHOD_NAME, "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting: userServicesList size=" + list.size());
            }
        }
        return list;
    }

    public ArrayList loadUserServicesNameList(Connection p_con, String p_userId) throws BTSLBaseException {

        final String METHOD_NAME = "loadUserServicesNameList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_userId=" + p_userId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        // Modification for Service Management [by Vipul]
        strBuff.append(" SELECT S.name,US.status FROM user_services US,users U,category_service_type CST,service_type S ");
        strBuff.append(" WHERE US.user_id = ? AND US.status <> 'N'");
        strBuff.append(" AND U.user_id=US.user_id AND cst.service_type = S.service_type AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");
        String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            int o = 0;
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("status"), rs.getString("name")));
                System.out.println(((ListValueVO) list.get(o)).getValue());
                o++;
            }
        } catch (SQLException sqe) {
            LOG.error(METHOD_NAME, "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(METHOD_NAME, "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting: userServicesList size=" + list.size());
            }
        }
        return list;
    }
	
	/**
 * this method is use to load user IMEI details for Mobile App
 * @param p_con
 * @param msisdn
 * @return
 * @throws BTSLBaseException
 */	
	public String loadUserIMEIDeatils(Connection p_con, String msisdn) throws BTSLBaseException {

		final String METHOD_NAME = "loadUserIMEIDeatils";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered msisdn=" + msisdn);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String IMEI=null;
		
		StringBuffer strBuff = new StringBuffer();
		// Modification for Service Management [by Vipul]
		strBuff.append(" SELECT up.imei from user_phones up,users u where u.user_id=up.user_id and u.status<>'N' and u.msisdn=? ");
			String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
		}
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, msisdn);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				IMEI=rs.getString("imei");
				
			}
			
		} catch (SQLException sqe) {
			LOG.error(METHOD_NAME, "SQLException : " + sqe);
			LOG.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserTXNDAO[loadUserIMEIDeatils]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(METHOD_NAME, "Exception : " + ex);
			LOG.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserTXNDAO[loadUserIMEIDeatils]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting: IMEI is  =" + IMEI);
			}
		}
		return IMEI;
	}
	
/**
 * This method is use to update user IMEI details.
 * @param p_con
 * @param serviceType
 * @param otp
 * @param channelUserVO
 * @return
 * @throws BTSLBaseException
 */	
	public int updateUserOTPDeatils(Connection p_con, String serviceType, String otp,ChannelUserVO channelUserVO) throws BTSLBaseException {

		final String METHOD_NAME = "updateUserOTPDeatils";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered msisdn=" + channelUserVO.getMsisdn());
		}
		PreparedStatement pstmt = null;
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		String IMEI=null;
		Date date;
		int updateCount=0;
		StringBuffer strBuff = new StringBuffer();
		StringBuffer strBuffSelect = new StringBuffer();
		StringBuffer strBuffUpdate = new StringBuffer();
		
		strBuffSelect.append("select USER_ID from user_otp where msisdn=? and service_types=? ");
		      String sqlselect=strBuffSelect.toString();
		      
		strBuff.append(" insert into user_otp (USER_ID,MSISDN,OTP_PIN,STATUS,GENERATED_ON,CREATED_BY,CREATED_ON,MODIFIED_BY,MODIFIED_ON,SERVICE_TYPES,invalid_counts )");
		strBuff.append(" values(?,?,?,?,?,?,?,?,?,?,?)");
			String sqlinsert = strBuff.toString();
			
			strBuffUpdate.append("update user_otp set USER_ID=?,OTP_PIN=?,STATUS=?,GENERATED_ON=?,CREATED_BY=?,CREATED_ON=?,MODIFIED_BY=?,MODIFIED_ON=?,invalid_counts=? where MSISDN=? and service_types=?  ");
			String sqlupdate=strBuffUpdate.toString();
			
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "QUERY sqlselect=" + sqlselect);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "QUERY sqlinsert=" + sqlinsert);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "QUERY sqlupdate=" + sqlupdate);
		}
		try {
			date= new Date();
			pstmt = p_con.prepareStatement(sqlinsert);
			pstmt.setString(1, channelUserVO.getUserID());
			pstmt.setString(2, channelUserVO.getMsisdn());
			pstmt.setString(3, BTSLUtil.encryptText(otp));
			pstmt.setString(4, PretupsI.YES);
			pstmt.setTimestamp(5, BTSLUtil.getSQLDateTimeFromUtilDate(date));
			pstmt.setString(6, channelUserVO.getUserID());
			pstmt.setTimestamp(7, BTSLUtil.getSQLDateTimeFromUtilDate(date));
			pstmt.setString(8, channelUserVO.getUserID());
			pstmt.setTimestamp(9, BTSLUtil.getSQLDateTimeFromUtilDate(date));
			pstmt.setString(10, serviceType);
			pstmt.setInt(11, 0);
			
			pstmtSelect=p_con.prepareStatement(sqlselect); 
			pstmtSelect.setString(1, channelUserVO.getMsisdn());
			pstmtSelect.setString(2, serviceType);
			rs=pstmtSelect.executeQuery(); // EXECUTE SELECT TO CHECK WHETHER USER IS ALREADY EXISTS OR NOT
			
			if(rs.next())
			{
				pstmtUpdate=p_con.prepareStatement(sqlupdate);
				pstmtUpdate.setString(1, channelUserVO.getUserID());
				
				pstmtUpdate.setString(2, BTSLUtil.encryptText(otp));
				pstmtUpdate.setString(3, PretupsI.YES);
				pstmtUpdate.setTimestamp(4, BTSLUtil.getSQLDateTimeFromUtilDate(date));
				pstmtUpdate.setString(5, channelUserVO.getUserID());
				pstmtUpdate.setTimestamp(6, BTSLUtil.getSQLDateTimeFromUtilDate(date));
				pstmtUpdate.setString(7, channelUserVO.getUserID());
				pstmtUpdate.setTimestamp(8, BTSLUtil.getSQLDateTimeFromUtilDate(date));
				pstmtUpdate.setInt(9, 0);
				pstmtUpdate.setString(10, channelUserVO.getMsisdn());
				pstmtUpdate.setString(11, serviceType);
				updateCount = pstmtUpdate.executeUpdate(); // IF USER EXISTS UPDATE THE DATA.
				
			}
			else
			{
			updateCount = pstmt.executeUpdate(); // IF USER DOES NOT EXISTS INSERT THE DATA.
			}
			
			
		} catch (SQLException sqe) {
			LOG.error(METHOD_NAME, "SQLException : " + sqe);
			LOG.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserTXNDAO[updateUserOTPDeatils]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(METHOD_NAME, "Exception : " + ex);
			LOG.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserTXNDAO[updateUserOTPDeatils]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
					if (pstmtSelect != null) {
						pstmtSelect.close();	
					
				}
					if (pstmtUpdate != null) {
						pstmtUpdate.close();	
					
				}
					if (rs != null) {
						rs.close();	
					
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting: IMEI =" + IMEI);
			}
		}
		return updateCount;
	}
	
	/**
	 * This Method is use to load user OTP details for OTP validation in mobile app
	 * @param p_con
	 * @param channelUserVO
	 * @return
	 * @throws BTSLBaseException
	 */
	
	public ChannelUserVO loadUserOTPDeatils(Connection p_con, ChannelUserVO channelUserVO, String p_serviceType) throws BTSLBaseException {

		final String METHOD_NAME = "loadUserOTPDeatils";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered userID=" + channelUserVO.getUserID()+"p_serviceType="+p_serviceType);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strBuff = new StringBuffer();
		// Modification for Service Management [by Vipul]
		strBuff.append("select * from ( ");
		strBuff.append(" SELECT otp_pin,generated_on,invalid_counts from user_otp where user_id=? and status='Y'" );
		if(!BTSLUtil.isNullString(p_serviceType)) 
			strBuff.append(" and  service_types = ? "); 
		strBuff.append(" ORDER BY generated_on desc ");
		strBuff.append(") subquery_alias");
        strBuff.append(" FETCH FIRST 1 ROWS ONLY");
		
		
		String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
		}
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, channelUserVO.getUserID());
		       if(!BTSLUtil.isNullString(p_serviceType))
		           pstmt.setString(2, p_serviceType);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				channelUserVO.setOTP(rs.getString("otp_pin"));
				channelUserVO.setOtpModifiedOn(rs.getTimestamp("generated_on"));
				channelUserVO.setOtpInvalidCount(rs.getInt("invalid_counts"));
				
			}
			
		} catch (SQLException sqe) {
			LOG.error(METHOD_NAME, "SQLException : " + sqe);
			LOG.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserTXNDAO[loadUserOTPDeatils]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(METHOD_NAME, "Exception : " + ex);
			LOG.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelUserTXNDAO[loadUserOTPDeatils]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
			
		}
		return channelUserVO;
	}
	

	/**
	 * This method is use to update user OTP invalid count for Mobile app
	 * @param p_con
	 * @param p_channelUserVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public int updateOTPDetails(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
		final String METHOD_NAME = "updateOTPDetails";
		if (LOG.isDebugEnabled()) {
			LOG.debug("updateOTPDetails ", " Entered p_UserID:" + p_channelUserVO.getUserID());
		}
		PreparedStatement pstmt = null;
		Date date=null;
		String qry = "UPDATE user_otp set invalid_counts=invalid_counts+1,modified_on=?,modified_by=? where user_id=? and service_types=? ";
		int updCount = -1;
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(" updateLanguageAndCountry ", " Query :: " + qry);
			}
			// Get Preapared Statement
			date=new Date();
			pstmt = p_con.prepareStatement(qry);
			pstmt.setTimestamp(1, BTSLUtil.getSQLDateTimeFromUtilDate(date));
			pstmt.setString(2, p_channelUserVO.getUserID());
			pstmt.setString(3, p_channelUserVO.getUserID());
			pstmt.setString(4, PretupsI.SERVICE_TYPE_USER_AUTH);
			// Execute Query
			updCount = pstmt.executeUpdate();
		} catch (SQLException sqe) {
			LOG.error(METHOD_NAME, "SQLException " + sqe.getMessage());
			LOG.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelChannelUserDAO[updateOTPDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception " + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"ChannelChannelUserDAO[updateOTPDetails]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception ex) {
				LOG.error(" updateOTPDetails ", "  Exception Closing Prepared Stmt: " + ex.getMessage());
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, " Exiting updCount ==" + updCount);
			}
		}
		return updCount;
	}
	
	/**
	 * @param pCon
	 * @param pChannelUserVO
	 * @param pDate
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList loadChannelUserOutChildTransferList(Connection pCon , ChannelUserVO pChannelUserVO , java.util.Date pDate) throws BTSLBaseException {
		
		final String methodName = "loadChannelUserOutChildTransferList";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered  p_channelUserVO " + pChannelUserVO.toString() + ", p_date" + pDate);
	        }
	       
	               
	       	String sqlSelect = channelUserTxnQry.loadChannelUserOutChildTransferListQry(pChannelUserVO);

	
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }

	        ArrayList transfrsOutListChilds = new ArrayList();
	        int i = 0;
	        try (PreparedStatement  pstmt = pCon.prepareStatement(sqlSelect);){
	            
	           
	            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
	            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
	            pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(pDate));
	            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	           
	            pstmt.setString(++i, pChannelUserVO.getUserID());
	            pstmt.setString(++i, PretupsI.STATUS_ACTIVE);
	            
	            if (pChannelUserVO.isStaffUser()) {
	                pstmt.setString(++i, pChannelUserVO.getActiveUserID());
	                pstmt.setString(++i, PretupsI.STATUS_ACTIVE);
	                
	            }
	            pstmt.setString(++i, pChannelUserVO.getNetworkID());
	            pstmt.setString(++i, PretupsI.C2S_MODULE);

	            try(ResultSet rs = pstmt.executeQuery();)
	            {
	            ChannelTransfrsReturnsVO transfersVO = null;
	            while (rs.next()) {
	                transfersVO = new ChannelTransfrsReturnsVO();
	                transfersVO.setProductCode(rs.getString("product_code"));
	                transfersVO.setProductShortCode(rs.getLong("product_short_code"));
	                transfersVO.setShortName(rs.getString("short_name"));
	                transfersVO.setTransfes(rs.getLong("transfers"));
	                transfersVO.setReturns(rs.getLong("returns1"));
	                transfrsOutListChilds.add(transfersVO);
	            }
	        } 
	        }catch (SQLException sqe) {
	            LOG.error(methodName, "SQLException : " + sqe);
	            LOG.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTxnDAO[loadChannelUserOutChildTransferList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "", "error.general.sql.processing");
	        } catch (Exception ex) {
	            LOG.error("", "Exception : " + ex);
	            LOG.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTxnDAO[loadChannelUserOutChildTransferList]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, "", "error.general.processing");
	        } finally {
	           
	            	LOG.debug(methodName, "Exiting..");
	            
	           
	        }
	        return transfrsOutListChilds;	
	}
	
	/**
	 * For updating the invalid count to 0 on successful login
	 * @param p_con
	 * @param p_channelUserVO
	 * @return
	 * @throws BTSLBaseException
	 */
	 public int resetInvalidCount(Connection p_con,ChannelUserVO p_channelUserVO) throws BTSLBaseException {
    	 final String METHOD_NAME = "resetInvalidCount";
         if (LOG.isDebugEnabled()) {
             LOG.debug("resetInvalidCount ", " Entered p_UserID:" + p_channelUserVO.getUserID());
         }
         PreparedStatement pstmt = null;
         String qry = "UPDATE USER_OTP SET INVALID_COUNTS = ? WHERE USER_ID = ?";
         int updCount = -1;
         try {
             if (LOG.isDebugEnabled()) {
                 LOG.debug(" resetInvalidCount ", " Query :: " + qry);
             }
             // Get Prepared Statement
             pstmt = p_con.prepareStatement(qry);  
             int i =0;
             pstmt.setInt(++i, 0);
             pstmt.setString(++i, p_channelUserVO.getUserID());
             // Execute Query
             updCount = pstmt.executeUpdate();
         } catch (SQLException sqe) {
             LOG.error(METHOD_NAME, "SQLException " + sqe.getMessage());
             LOG.errorTrace(METHOD_NAME, sqe);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTxnDAO[updateMhashToken]", "", "", "", "SQL Exception:" + sqe.getMessage());
             throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
         } catch (Exception e) {
             LOG.error(METHOD_NAME, "Exception " + e.getMessage());
             LOG.errorTrace(METHOD_NAME, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTxnDAO[updateMhashToken]", "", "", "", "Exception:" + e.getMessage());
             throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
         } finally {
             try {
                 if (pstmt != null) {
                     pstmt.close();
                 }
             } catch (Exception ex) {
                 LOG.errorTrace(METHOD_NAME, ex);
             }
             if (LOG.isDebugEnabled()) {
                 LOG.debug(METHOD_NAME, " Exiting updCount ==" + updCount);
             }
         }
         return updCount;
    }
}
