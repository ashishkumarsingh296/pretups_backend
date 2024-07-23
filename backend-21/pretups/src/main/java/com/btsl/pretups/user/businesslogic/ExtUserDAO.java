package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.BooleanOperator;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;

/**
 * 
 */
public class ExtUserDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(ExtUserDAO.class.getName());
    private ExtUserQry extUserQry= (ExtUserQry)ObjectProducer.getObject(QueryConstants.EXT_USER_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * Method loadChannelUserDetails.
     * 
     * @author puneet.rs
     *         This method load user information by either msisdn,external code,
     *         or loginid
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_loginId
     *            String
     *            * @param p_extCode String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadChannelUserDetailsByMsisdnLoginIdExt(Connection p_con, String p_msisdn, String p_loginId, String p_password, String p_extCode, Locale locale) throws BTSLBaseException {
        final String METHOD_NAME = "loadChannelUserDetailsByMsisdnLoginIdExt";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME,
                            "loadUserDetailsByMsisdnOrLoginId():: Entered with p_msisdn:" + p_msisdn + " p_password=" + p_password + " locale=" + locale + "p_loginId" + p_loginId);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder sqlBuffer = new StringBuilder(
                        " SELECT uowner.user_name owner_name, uparent.user_name parent_name,uparent.msisdn parent_msisdn, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        sqlBuffer.append(" u.category_code, u.parent_id, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time, ");
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
        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,up.sms_pin,up.pin_required,up.invalid_pin_count,dt.restricted_msisdn restricted_msisdn_allow, ");
        // append
        // for Zebra and Tango by sanjeew date 06/07/07
        sqlBuffer.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, cusers.low_bal_alert_allow ");
        // end of Zebra and Tango
        // added for loading password reset info
        sqlBuffer.append(", u.PSWD_RESET ");
        sqlBuffer.append(", gdomains.status geostatus ");// a1
        sqlBuffer.append(" FROM users u left join users uparent on u.parent_id=uparent.user_id ");
        sqlBuffer.append(" left join networks l on U.network_code=L.network_code  left join channel_users cusers on u.user_id=cusers.user_id ");
        sqlBuffer.append(" left join user_phones up on (u.msisdn = up.msisdn and u.user_id=up.user_id ) ");
        sqlBuffer.append(" ,users uowner,categories cat,geographical_domain_types gdt ,domains dm,domain_types dt, ");
        // geographical_domains gdomains,user_geographies geo
        sqlBuffer.append(" geographical_domains gdomains,user_geographies geo ");
        sqlBuffer.append(" WHERE ");
        if (!BTSLUtil.isNullString(p_msisdn)) {
            sqlBuffer.append("u.msisdn=? AND ");
        } else if (!BTSLUtil.isNullString(p_loginId)) {
            sqlBuffer.append("u.login_id=? AND ");
        } else if (!BTSLUtil.isNullString(p_extCode)) {
            sqlBuffer.append("u.EXTERNAL_CODE=? AND ");
        }
        sqlBuffer.append("  u.owner_id=uowner.user_id AND gdt.grph_domain_type = cat.grph_domain_type ");
        sqlBuffer.append(" AND u.status <> ? AND u.status <> ? ");
        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
        sqlBuffer.append(" AND gdt.grph_domain_type=gdomains.grph_domain_type ");
        sqlBuffer.append(" AND cat.domain_code= dm.domain_code ");
        sqlBuffer.append(" AND u.user_id=geo.user_id ");
        // sqlBuffer.append(" AND geo.grph_domain_code=gdomains.grph_domain_code ");
        ChannelUserVO channelUserVO = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Query : " + sqlBuffer.toString());
            }
            pstmt = p_con.prepareStatement(sqlBuffer.toString());
            int i = 0;
            if (!BTSLUtil.isNullString(p_msisdn)) {
                pstmt.setString(++i, p_msisdn);
            } else if (!BTSLUtil.isNullString(p_loginId)) {
                pstmt.setString(++i, p_loginId);
            } else if (!BTSLUtil.isNullString(p_extCode)) {
                pstmt.setString(++i, p_extCode);
            }
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
                channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                channelUserVO.setOwnerName(rs.getString("owner_name"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setValidRequestURLs(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setFromTime(rs.getString("from_time"));
                channelUserVO.setToTime(rs.getString("to_time"));
                if (rs.getTimestamp("last_login_on") != null) {
                    channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                }
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setEmail(rs.getString("email"));
                channelUserVO.setCreatedBy(userID);
                channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(userID);
                if (rs.getTimestamp("created_on") != null) {
                    channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                }
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
                final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    channelUserVO.setMessage(rs.getString("language_1_message"));
                } else {
                    channelUserVO.setMessage(rs.getString("language_2_message"));
                }
                channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));
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
                // channelUserVO.setAccessType(rs.getString("access_type"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End Zebra and Tango
                channelUserVO.setPasswordReset(rs.getString("PSWD_RESET"));
                if (rs.getTimestamp("password_count_updated_on") != null) {
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                }
                channelUserVO.setSmsPin(rs.getString("sms_pin"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                final CategoryVO categoryVO = new CategoryVO();
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
                categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
                channelUserVO.setCategoryVO(categoryVO);
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(p_con, channelUserVO.getUserID()));
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, " Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoginDAO[loadUserDetailsByMsisdnOrLoginId]", "", "",
                            "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                _log.error("loadUserDetailsByMsisdnOrLoginIdExt::", " Exception : in closing resultset" + ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                _log.error("loadUserDetailsByMsisdnOrLoginIdExt::", " Exception : in closing preparedstatement" + ex);
            }
        }
        _log.debug("loadUserDetailsByMsisdnOrLoginIdExt::", " Exiting channelUserVO=" + channelUserVO);
        return channelUserVO;
    }// loadUserDetailsByMsisdnOrLoginIdExt Method Ends

    /**
     * Method loadUsersDetailsforExtReq.
     * This method is used to load all the information for Parent Channel user
     * 
     * @author puneet.rs
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
    public ChannelUserVO loadUsersDetailsforExtReq(Connection p_con, String p_msisdn, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersDetailsforExtReq", "Entered p_msisdn= " + p_msisdn + " p_userID=" + p_userID + " p_stausUsed=" + p_statusUsed + ",p_staus=" + p_status);
        }
        final String METHOD_NAME = "loadUsersDetailsforExtReq";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;

        final String sqlSelect = extUserQry.loadUsersDetailsforExtReqQry(p_userID, p_statusUsed, p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmtSelect.setString(i++, p_status);
            }
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
            if (p_userID != null) {
                pstmtSelect.setString(i++, p_userID);
                pstmtSelect.setString(i++, p_userID);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));// check
                // for
                // default
                // geo
                // code
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
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting: channelUserVO=" + channelUserVO);
            }
        }
        return channelUserVO;
    }

    // Added For fetching default profile
    /**
     * Created By Puneet
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_categoryCode
     * @return
     */
    public String getDefaultProfileForOthAssociation(Connection p_con, String p_networkCode, String p_categoryCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultProfileForOthAssociation", "Entered: p_networkCode= " + p_networkCode + " p_categoryCode " + p_categoryCode);
        }
        final String METHOD_NAME = "getDefaultProfileForOthAssociation";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String defaultProfileType = null;
        final StringBuilder sbf = new StringBuilder("SELECT ps.profile_type c_profile_type  FROM PROFILE_SET ps,PROFILE_MAPPING pm ");
        sbf.append(" WHERE pm.is_default IN ('Y','N') AND ps.set_id = pm.SET_ID AND ps.NETWORK_CODE = pm.NETWORK_CODE ");
        sbf.append(" AND pm.SRV_CLASS_OR_CATEGORY_CODE= ? AND pm.NETWORK_CODE= ? AND pm.IS_DEFAULT='Y' ");
        final String selectQuery = sbf.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultProfileForOthAssociation", "SQL Query :" + selectQuery);
        }
        try {
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                defaultProfileType = rs.getString(1);
            }
        } catch (SQLException sqe) {
            _log.error("getDefaultProfileForOthAssociation", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusDAO[getDefaultProfileForOthAssociation]", "", "", "", "SQL Exception:" + sqe.getMessage());
        } catch (Exception ex) {
            _log.error("getDefaultProfileForOthAssociation", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileForOthAssociation]", "",
                            "", "", "Exception:" + ex.getMessage());

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultProfileForOthAssociation", "Exiting: Default Activation Profile  " + defaultProfileType);
            }
        }
        return defaultProfileType;
    }

    // Method End

    // Method to get Default Transfer profile

    /**
     * Created By Puneet
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_categoryCode
     * @param p_parentProfileId
     * @return
     * @throws BTSLBaseException
     */
    public String getDefaultTransferProfileIDByCategoryID(Connection p_con, String p_networkCode, String p_categoryCode, String p_parentProfileId) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultTransferProfileIDByCategoryID",
                            "Entered p_networkCode=" + p_networkCode + " p_categoryCode=" + p_categoryCode + " p_parentProfileId=" + p_parentProfileId);
        }
        final String METHOD_NAME = "getDefaultTransferProfileIDByCategoryID";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String defaultTransferProfileID = null;
        
        StringBuilder strBuff = null;
        
		String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
		boolean tcpOn = false;
		Set<String> uniqueTransProfileId = new HashSet();

		if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
			tcpOn = true;
		}
		

		java.util.List<HashMap<String, String>> resultSet =null;
				
		if (tcpOn) {
			SearchCriteria searchCriteria = new SearchCriteria("network_code", Operator.EQUALS, p_networkCode,
					ValueType.STRING, null).addCriteria(new SearchCriteria("category_code", Operator.EQUALS, p_categoryCode,
					ValueType.STRING, null), BooleanOperator.AND)
					.addCriteria(new SearchCriteria("parent_profile_id", Operator.EQUALS, p_parentProfileId,
							ValueType.STRING, null), BooleanOperator.AND)
					.addCriteria(new SearchCriteria("status", Operator.NOT_EQUALS, PretupsI.STATUS_DELETE,
							ValueType.STRING, null), BooleanOperator.AND)
					.addCriteria(new SearchCriteria("IS_DEFAULT", Operator.EQUALS, "Y",
							ValueType.STRING, null), BooleanOperator.AND);
			
			resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE", new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
        	
        	
		} else {

			strBuff = new StringBuilder("SELECT profile_id from transfer_profile ");
			strBuff.append(" WHERE network_code =? ");
			strBuff.append(" AND category_code =? ");
			strBuff.append(" AND parent_profile_id =? ");
			strBuff.append(" AND status<>? AND IS_DEFAULT ='Y'");// change against
		}
        
        // ID=
        // TRFPROFILESTATUS(This
        // change is done
        // to make status
        // check on
        // transfer
        // profile, so that
        // only non deleted
        // transfer profile
        // can be
        // associated with
        // user
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultTransferProfileIDByCategoryID", "QUERY sqlSelect=" + sqlSelect);
        }
        
        
        try {
        	
        	if(!tcpOn) {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_categoryCode);
            pstmt.setString(3, p_parentProfileId);
            pstmt.setString(4, PretupsI.STATUS_DELETE);// change against ID=
            // TRFPROFILESTATUS(This
            // change is done to make
            // status check on
            // transfer profile, so
            // that only non deleted
            // transfer profile can
            // be associated with
            // user
            rs = pstmt.executeQuery();
            while (rs.next()) {
                defaultTransferProfileID = rs.getString("profile_id");
            }
            
        
        }else {
        	defaultTransferProfileID = resultSet.get(0).get("profileId");
        }
            
        } catch (SQLException sqe) {
            _log.error("getDefaultTransferProfileIDByCategoryID", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "TransferProfileDAO[getDefaultTransferProfileIDByCategoryID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getDefaultTransferProfileIDByCategoryID", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("getDefaultTransferProfileIDByCategoryID", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "TransferProfileDAO[getDefaultTransferProfileIDByCategoryID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getDefaultTransferProfileIDByCategoryID", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultTransferProfileIDByCategoryID", "Transfer Profile ID: " + defaultTransferProfileID);
            }
        }
        return defaultTransferProfileID;
    }// Method Ends

    // For Default Grade
    /**
     * Method getDefaultGradeCode.
     * This method is used to get channel grade details from channel_grade table
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @return String gradeCode
     * @throws BTSLBaseException
     * @author Puneet.rs
     */

    public String getDefaultGradeCode(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultGradeCode", "Entered p_categoryCode=" + p_categoryCode);
        }
        final String METHOD_NAME = "getDefaultGradeCode";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String defaultGradeCode = null;
        final StringBuilder strBuff = new StringBuilder("SELECT G.grade_code ");
        strBuff.append(" FROM channel_grades G");
        strBuff.append(" WHERE G.category_code=? AND G.status=? AND is_default_grade='Y' ORDER BY grade_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultGradeCode", "Select Query= " + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.GRADE_STATUS_ACTIVE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                defaultGradeCode = rs.getString("grade_code");
            }
        } catch (SQLException sqe) {
            _log.error("getDefaultGradeCode", "SQL Exception" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[getDefaultGradeCode]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getDefaultGradeCode", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("getDefaultGradeCode", " Exception" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryGradeDAO[getDefaultGradeCode]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getDefaultGradeCode", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultGradeCode", "Channel User Default Grade Code: " + defaultGradeCode);
            }
        }
        return defaultGradeCode;
    }// End Default Grade: Puneet

    // Created by Puneet
    // For default commission profile

    /**
     * Method fetches Default Commision Profile
     * 
     * @param p_con
     * @param p_categoryCode
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException
     */
    public String getDefaultCommisionProfileSetIDByCategoryID(Connection p_con, String p_categoryCode, String p_networkCode, String p_gradeCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultCommisionProfileSetIDByCategoryID",
                            "Entered p_categoryCode=" + p_categoryCode + " p_networkCode=" + p_networkCode + " p_gradeCode=" + p_gradeCode);
        }
        final String METHOD_NAME = "getDefaultCommisionProfileSetIDByCategoryID";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String commProfileSetID = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT comm_profile_set_id ");
        strBuff.append(" FROM commission_profile_set WHERE category_code = ? ");
        strBuff.append(" AND network_code = ? AND status != 'N' AND IS_DEFAULT='Y'");
        strBuff.append(" AND grade_code in (?,?) ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("getDefaultCommisionProfileSetIDByCategoryID", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, PretupsI.ALL);
            pstmt.setString(4, p_gradeCode);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                commProfileSetID = rs.getString("comm_profile_set_id");
            }
        } catch (SQLException sqe) {
            _log.error("getDefaultCommisionProfileSetIDByCategoryID", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getDefaultCommisionProfileSetIDByCategoryID]",
                            "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getDefaultCommisionProfileSetIDByCategoryID", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("getDefaultCommisionProfileSetIDByCategoryID", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[getDefaultCommisionProfileSetIDByCategoryID]",
                            "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getDefaultCommisionProfileSetIDByCategoryID", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultCommisionProfileSetIDByCategoryID", "Default Commission Profile Set ID" + commProfileSetID);
            }
        }
        return commProfileSetID;

    }

    // For Geo Code
    /**
     * @author puneet.rs
     * @param p_con
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     *             Method Check for user default geo code on basis of category
     *             code in request
     */
    public String getDefaultGeoCodeDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {

        // get the usertype on the basis of login_id and msisdn
        final String METHOD_NAME = "getDefaultGeoCodeDetails";

        final String category = p_requestVO.getUserCategory();
        String userGeographicalCode = null;
        ;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            final StringBuilder buf = new StringBuilder("SELECT GRPH_DOMAIN_CODE FROM GEOGRAPHICAL_DOMAINS  ");

            buf.append(" WHERE GRPH_DOMAIN_TYPE= ");
            buf.append(" (SELECT GRPH_DOMAIN_TYPE FROM CATEGORIES WHERE CATEGORY_CODE= ? ");
            buf.append(" AND STATUS='Y') ");
            buf.append(" AND IS_DEFAULT='Y' ");

            final String query = buf.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultGeoCodeDetails ", "Query: " + query);
            }
            pstm = p_con.prepareStatement(query);

            pstm.setString(1, category);

            rst = pstm.executeQuery();
            if (rst.next()) {
                userGeographicalCode = rst.getString("GRPH_DOMAIN_CODE");
            }

        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadActiveUserId]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "getOptUserDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadActiveUserId]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getOptUserDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultGeoCodeDetails ", "Category Code: " + category);
            }
        }
        return userGeographicalCode;
    }

    /**
     * Method loadChannelUserDetails.
     * 
     * @author : Diwakar
     *         This method load user information by either msisdn,external code,
     *         or loginid
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_loginId
     *            String
     * @param p_extNwCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_extCode
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(Connection p_con, String p_msisdn, String p_smsPin, String p_loginId, String p_password, String empCode, String p_extNwCode, String p_categoryCode, String p_extCode, Locale locale) throws BTSLBaseException {
        final String METHOD_NAME = "loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode";
        if (_log.isDebugEnabled()) {
            _log.debug("loadChannelUserDetailsByMsisdnLoginIdExtNwCode",
                            "loadChannelUserDetailsByMsisdnLoginIdExtNwCode():: Entered with p_msisdn:" + p_msisdn + " p_password=" + p_password + " locale=" + locale + "p_loginId" + p_loginId + " p_extNwCode=" + p_extNwCode + " empCode=" + empCode + " p_categoryCode=" + p_categoryCode + " p_extCode = " + p_extCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder sqlBuffer = new StringBuilder(
                        " SELECT distinct uowner.user_name owner_name, uparent.user_name parent_name, u.user_id, u.user_name, u.network_code,l.network_name,l.report_header_name, u.login_id, u.password, ");
        sqlBuffer.append(" u.category_code, u.parent_id, u.owner_id, u.msisdn, u.allowed_ip,  u.allowed_days,u.from_time,u.to_time, ");
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
        sqlBuffer.append(" gdt.grph_domain_type_name,dm.domain_name,dm.status domainstatus,dm.domain_type_code,up.sms_pin,up.pin_required,up.invalid_pin_count,dt.restricted_msisdn restricted_msisdn_allow,up.user_phones_id,  ");
        // append
        // for Zebra and Tango by sanjeew date 06/07/07
        sqlBuffer.append(" cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow, cusers.low_bal_alert_allow ");
        // end of Zebra and Tango
        // added for loading password reset info
        sqlBuffer.append(", u.PSWD_RESET ");
        sqlBuffer.append(", gdomains.status geostatus ");// a1
        sqlBuffer.append(", geo.GRPH_DOMAIN_CODE ");// a1
        sqlBuffer.append(" FROM users u left join users uparent on u.parent_id=uparent.user_id ");
        sqlBuffer.append(" left join networks l on U.network_code=L.network_code ");
        sqlBuffer.append(" left join channel_users cusers on u.user_id=cusers.user_id ");
        sqlBuffer.append(" left join user_phones up on (u.msisdn = up.msisdn and u.user_id=up.user_id ) ");
        sqlBuffer.append(" ");
        sqlBuffer.append(" ,users uowner,categories cat,geographical_domain_types gdt ,domains dm,domain_types dt, ");
        // geographical_domains gdomains,user_geographies geo
        sqlBuffer.append(" geographical_domains gdomains,user_geographies geo ");
        sqlBuffer.append(" WHERE ");
          if(!BTSLUtil.isNullString(p_msisdn))
         {
          sqlBuffer.append("u.msisdn=? AND ");
          sqlBuffer.append("up.sms_pin=? AND "); //03-MAR-2014
          }
        if (!BTSLUtil.isNullString(p_loginId)) {
            sqlBuffer.append("u.login_id=? AND ");
            sqlBuffer.append("u.password=? AND ");
        }
        if (!BTSLUtil.isNullString(empCode)) {
            sqlBuffer.append("u.EMPLOYEE_CODE=? AND ");
            // Ended Here
        }

        if (!BTSLUtil.isNullString(p_extNwCode)) {
            sqlBuffer.append("u.NETWORK_CODE=? AND ");
        }

        if (!BTSLUtil.isNullString(p_categoryCode)) {
            sqlBuffer.append("u.category_code=? AND ");
        }

        if (!BTSLUtil.isNullString(p_extCode)) {
            sqlBuffer.append("u.EXTERNAL_CODE=? AND ");
        }

        sqlBuffer.append("  u.owner_id=uowner.user_id  AND gdt.grph_domain_type = cat.grph_domain_type ");
        sqlBuffer.append(" AND u.status <> ? AND u.status <> ?  ");
        sqlBuffer.append(" AND cat.category_code=U.category_code AND cat.status <> ? AND dm.domain_code = cat.domain_code ");
        sqlBuffer.append(" AND dt.domain_type_code = dm.domain_type_code ");
        sqlBuffer.append(" AND gdt.grph_domain_type=gdomains.grph_domain_type ");
        sqlBuffer.append(" AND cat.domain_code= dm.domain_code ");
        sqlBuffer.append(" AND u.user_id=geo.user_id ");
        // sqlBuffer.append(" AND geo.grph_domain_code=gdomains.grph_domain_code ");
        ChannelUserVO channelUserVO = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode", " Query : " + sqlBuffer.toString());
            }
            pstmt = p_con.prepareStatement(sqlBuffer.toString());
            int i = 0;
            
              
              if(!BTSLUtil.isNullString(p_msisdn))
              {
              pstmt.setString(++i,p_msisdn);
              pstmt.setString(++i,BTSLUtil.encryptText(p_smsPin));
              }

            // Changed on 21-02-2014
            if (!BTSLUtil.isNullString(p_loginId)) {
                pstmt.setString(++i, p_loginId);
                final String decriptPassword = BTSLUtil.encryptText(p_password);
                pstmt.setString(++i, decriptPassword);
            }
            if (!BTSLUtil.isNullString(empCode)) {
                pstmt.setString(++i, empCode);
                // Changed Ended on 21-02-2014
            }

            if (!BTSLUtil.isNullString(p_extNwCode)) {
                pstmt.setString(++i, p_extNwCode);
            }

            if (!BTSLUtil.isNullString(p_categoryCode)) {
                pstmt.setString(++i, p_categoryCode);
            }

            if (!BTSLUtil.isNullString(p_extCode)) {
                pstmt.setString(++i, p_extCode);
            }

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
                if (rs.getTimestamp("last_login_on") != null) {
                    channelUserVO.setLastLoginOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("last_login_on")));
                }
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("userstatus"));
                channelUserVO.setEmail(rs.getString("email"));
                channelUserVO.setCreatedBy(userID);
                channelUserVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                channelUserVO.setModifiedBy(userID);
                if (rs.getTimestamp("created_on") != null) {
                    channelUserVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("created_on")));
                }
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
                // Diwakar
                channelUserVO.setGeographicalCode(rs.getString("GRPH_DOMAIN_CODE"));
                // End
                final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    channelUserVO.setMessage(rs.getString("language_1_message"));
                } else {
                    channelUserVO.setMessage(rs.getString("language_2_message"));
                }
                channelUserVO.setGeographicalCodeStatus(rs.getString("geostatus"));
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
                // channelUserVO.setAccessType(rs.getString("access_type"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End Zebra and Tango
                channelUserVO.setPasswordReset(rs.getString("PSWD_RESET"));
                if (rs.getTimestamp("password_count_updated_on") != null) {
                    channelUserVO.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("password_count_updated_on")));
                }
                channelUserVO.setSmsPin(rs.getString("sms_pin"));
                channelUserVO.setPinRequired(rs.getString("pin_required"));
                channelUserVO.setInvalidPinCount(rs.getInt("invalid_pin_count"));
                channelUserVO.setRestrictedMsisdnAllow(rs.getString("restricted_msisdn_allow"));
                channelUserVO.setUserPhonesId(rs.getString("user_phones_id"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                final CategoryVO categoryVO = new CategoryVO();
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

                categoryVO.setAllowedGatewayTypes(MessageGatewayForCategoryCache.getMessagegatewayforcategoryList(categoryVO.getCategoryCode()));
                channelUserVO.setCategoryVO(categoryVO);
                try {
                    channelUserVO.setPasswordModifiedOn(BTSLUtil.getTimestampFromUtilDate(rs.getTimestamp("pswd_modified_on")));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    channelUserVO.setPasswordModifiedOn(null);
                }
                _log.debug("loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode : ", "channelUserVO.getUserID() = " + channelUserVO.getUserID());
                channelUserVO.setAssociatedServiceTypeList(new ChannelUserDAO().loadUserServicesList(p_con, channelUserVO.getUserID()));
                // channelUserVO.set
                _log.debug("loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode : ", "setAssociatedServiceTypeList = " + channelUserVO
                                .getAssociatedServiceTypeList());
            }
        } catch (SQLException sqe) {
            _log.error("loadChannelUserDetailsByMsisdnLoginIdExtNwCode", " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoginDAO[loadChannelUserDetailsByMsisdnLoginIdExtNwCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetailsByMsisdnLoginIdExtNwCode", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadChannelUserDetailsByMsisdnLoginIdExtNwCode", " Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoginDAO[loadChannelUserDetailsByMsisdnLoginIdExtNwCode]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetailsByMsisdnLoginIdExtNwCode", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                _log.error("loadChannelUserDetailsByMsisdnLoginIdExtNwCode::", " Exception : in closing resultset" + ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                _log.error("loadChannelUserDetailsByMsisdnLoginIdExtNwCode::", " Exception : in closing preparedstatement" + ex);
            }
        }
        _log.debug("loadChannelUserDetailsByMsisdnLoginIdExtNwCode::", " Exiting channelUserVO=" + channelUserVO);
        return channelUserVO;
    }// loadUserDetailsByMsisdnOrLoginIdExt Method Ends

    /**
     * Method loadUsersDetailsforExtCodeReq.
     * This method is used to load all the information for Parent Channel user
     * 
     * @author diwakar
     * @param p_con
     *            Connection
     * @param p_externalCode
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
    public ChannelUserVO loadUsersDetailsforExtCodeReq(Connection p_con, String p_externalCode, String p_userID, String p_statusUsed, String p_status) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersDetailsforExtCodeReq",
                            "Entered p_externalCode= " + p_externalCode + " p_userID=" + p_userID + " p_stausUsed=" + p_statusUsed + ",p_staus=" + p_status);
        }
        final String METHOD_NAME = "loadUsersDetailsforExtCodeReq";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelUserVO channelUserVO = null;
        
        final String sqlSelect = extUserQry.loadUsersDetailsforExtCodeReqQry(p_userID, p_statusUsed, p_status);
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersDetailsforExtCodeReq", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_externalCode);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmtSelect.setString(i++, p_status);
            }
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
            if (p_userID != null) {
                pstmtSelect.setString(i++, p_userID);
                pstmtSelect.setString(i++, p_userID);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));// check
                // for
                // default
                // geo
                // code
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
            _log.error("loadUsersDetailsforExtCodeReq", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersDetailsforExtCodeReq", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUsersDetailsforExtCodeReq", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadUsersDetails]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersDetailsforExtCodeReq", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUsersDetailsforExtCodeReq", "Exiting: channelUserVO=" + channelUserVO);
            }
        }
        return channelUserVO;
    }
    
	//For Geo Code 
	/**@author Naveen
	 * @param p_con
	 * @param p_requestVO
	 * @return
	 * @throws BTSLBaseException
	 * Method Check for user default geo code on basis of category code in request
	 */
	public String getDefaultGeoCodeDtlBasedOnNetwork(Connection p_con,RequestVO p_requestVO,String network_code) throws BTSLBaseException{

		final String METHOD_NAME="getDefaultGeoCodeDtlBasedOnNetwork";
		String category=p_requestVO.getUserCategory();
		if (_log.isDebugEnabled())
		    _log.debug(METHOD_NAME, "Network_code ="+network_code +" category_code"+category);
		
		//String newwork_code=p_requestVO.getExternalNetworkCode();
		String userGeographicalCode=null;;
		PreparedStatement pstm=null;
		ResultSet rst=null;
		try
		{
			StringBuffer buf = new StringBuffer("SELECT GRPH_DOMAIN_CODE FROM GEOGRAPHICAL_DOMAINS  ");
			
			buf.append(" WHERE GRPH_DOMAIN_TYPE= ");	
			buf.append(" (SELECT GRPH_DOMAIN_TYPE FROM CATEGORIES WHERE CATEGORY_CODE= ? ");	
			buf.append(" AND STATUS='Y') ");
			buf.append(" AND NETWORK_CODE= ? ");
			buf.append(" AND IS_DEFAULT='Y' ");
			
			
			
			String query= buf.toString();

			if(_log.isDebugEnabled())
				_log.debug("getDefaultGeoCodeDetails ","Query: "+query);
			pstm=p_con.prepareStatement(query);
			
			pstm.setString(1,category);	
			pstm.setString(2,network_code);	
			
			rst=pstm.executeQuery();
			if(rst.next())
			{
				userGeographicalCode=rst.getString("GRPH_DOMAIN_CODE");
			}

		}
		catch (SQLException sqle)
		{
			_log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadActiveUserId]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "getOptUserDetails", "error.general.sql.processing");
		}// end of catch
		catch (Exception e)
		{
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadActiveUserId]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "getOptUserDetails", "error.general.processing");
		}// end of catch
		finally
		{
			try{if (rst != null)rst.close();} catch (Exception e){
				_log.errorTrace(METHOD_NAME, e);
			}
			try{if (pstm != null)pstm.close();} catch (Exception e){
				_log.errorTrace(METHOD_NAME, e);
			}
			
			if(_log.isDebugEnabled())
				_log.debug("getDefaultGeoCodeDetails ","Category Code: "+category);
		}
		return userGeographicalCode ;
	}
    
	/**
     * @author puneet.rs
     * @param p_con
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     *             Method Check for user default geo code on basis of category
     *             code in request
     */
    public String getDefaultGeoCodeDetailsForParent(Connection p_con, RequestVO p_requestVO, ChannelUserVO p_channelUserVO) throws BTSLBaseException {

        // get the usertype on the basis of login_id and msisdn
        final String METHOD_NAME = "getDefaultGeoCodeDetailsForParent";

        final String category = p_requestVO.getUserCategory();
        final String networCode = p_channelUserVO.getNetworkID();
        final String parentGeographicalDomainCode = p_channelUserVO.getGeographicalCode();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "category= " + category+", networCode= "+networCode+", parentGeographicalDomainCode= "+parentGeographicalDomainCode);
        }
        String userGeographicalCode = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            final StringBuilder buf = new StringBuilder("SELECT GRPH_DOMAIN_CODE,grph_domain_name FROM GEOGRAPHICAL_DOMAINS  ");

            buf.append(" WHERE network_code=? and GRPH_DOMAIN_TYPE= ");
            buf.append(" (SELECT GRPH_DOMAIN_TYPE FROM CATEGORIES WHERE CATEGORY_CODE= ? ");
            buf.append(" AND STATUS='Y') ");
            buf.append(" AND IS_DEFAULT='Y' ");
            buf.append(" AND parent_grph_domain_code=? ");
            
            final String query = buf.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Query: " + query);
            }
            pstm = p_con.prepareStatement(query);
            pstm.setString(1, networCode);
            pstm.setString(2, category);
            pstm.setString(3, parentGeographicalDomainCode);
            rst = pstm.executeQuery();
            if (rst.next()) {
                userGeographicalCode = rst.getString("GRPH_DOMAIN_CODE");
            }

        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadActiveUserId]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadActiveUserId]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getOptUserDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getDefaultGeoCodeDetails ", "Category Code: " + category);
            }
        }
        return userGeographicalCode;
    }
}
