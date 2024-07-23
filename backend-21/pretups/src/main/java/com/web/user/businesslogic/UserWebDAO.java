package com.web.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;

public class UserWebDAO {

    private static boolean flag = false;

    /**
     * Commons Logging instance.
     */
    private final Log _log = LogFactory.getLog(this.getClass().getName());
    private UserWebQry userWebQry = (UserWebQry)ObjectProducer.getObject(QueryConstants.USER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    /************************** Methods Used while Editing a user **************************/

    /**
     * Method for loading Users List.
     * This method call from the UserAction and ChannelUserAction
     * UserAction class pass username, pass p_sessionUserID if Network Admin
     * loggedIn else null(if superadmin)
     * ChannelUserAction class pass a)userId b) userName and ownerID, pass
     * p_sessionUserID if Channel User loggedIn else null(if Channel Admin)
     * 
     * Use p_sessionUserID for connect by prior
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_userName
     *            String
     * @param p_userID
     *            String
     * @param p_ownerID
     *            String
     * @param p_sessionUserID
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadUsersList(Connection p_con, String p_networkCode, String p_categoryCode, String p_userName, String p_userID, String p_ownerID, String p_sessionUserID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadUsersList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", User Name= ");
        	msg.append(p_userName);
        	msg.append(", p_userID= ");
        	msg.append(p_userID);        	
        	msg.append(", p_sessionUserID= ");
        	msg.append(p_sessionUserID);
        	msg.append(", p_ownerID= ");
        	msg.append(p_ownerID);
        	msg.append(", p_statusUsed= ");
        	msg.append(p_statusUsed);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;O
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = userWebQry.loadUsersListQry(p_networkCode, p_categoryCode, p_userName, p_userID, p_ownerID, p_sessionUserID, p_statusUsed, p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(++i, p_status);
            }
            if (!BTSLUtil.isNullString(p_categoryCode)) {
                pstmt.setString(++i, p_categoryCode);
            }
            if (!BTSLUtil.isNullString(p_userID)) {
                pstmt.setString(++i, p_userID);
            }
            if (!BTSLUtil.isNullString(p_ownerID)) {
                pstmt.setString(++i, p_ownerID);
            }
            if (!BTSLUtil.isNullString(p_userName)) {
                // commented for DB2pstmt.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                pstmt.setString(++i, p_userName);
            }
            pstmt.setString(++i, PretupsI.USER_STATUS_TYPE);
            if (p_sessionUserID != null) {
                pstmt.setString(++i, p_sessionUserID);
                pstmt.setString(++i, p_sessionUserID);
            }

            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new ChannelUserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("usr_user_id"));
                userVO.setUserName(rs.getString("usr_user_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("passwd"));
                userVO.setCategoryCode(rs.getString("usr_category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setContactPerson(rs.getString("contact_person"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setPaymentTypes(rs.getString("payment_type"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("usr_status"));
                userVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                userVO.setCompany(rs.getString("company"));
                userVO.setFax(rs.getString("fax"));
                userVO.setFirstName(rs.getString("firstname"));
                userVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("usr_msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setRsaFlag(rs.getString("rsaflag"));
                userVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setPreviousStatus(rs.getString("previous_status"));

                final CategoryVO categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setProductTypeAllowed(rs.getString("PRODUCT_TYPES_ALLOWED"));
                categoryVO.setServiceAllowed(rs.getString("SERVICES_ALLOWED"));
                categoryVO.setMaxTxnMsisdn(rs.getString("MAX_TXN_MSISDN"));
                // Adde for Authetication Type
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                userVO.setCategoryVO(categoryVO);

                userVO.setParentName(rs.getString("parent_name"));
                userVO.setParentMsisdn(rs.getString("parent_msisdn"));
                userVO.setParentCategoryName(rs.getString("parent_cat"));

                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                userVO.setOwnerCategoryName(rs.getString("owner_cat"));
                userVO.setStatusDesc(rs.getString("lookup_name"));
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ))).booleanValue()) {
                    userVO.setAssoMsisdn(rs.getString("ASSOCIATED_MSISDN"));
                    userVO.setAssType(rs.getString("ASSOCIATED_MSISDN_TYPE"));
                    userVO.setAssociationCreatedOn(rs.getTimestamp("ASSOCIATED_MSISDN_CDATE"));
                    userVO.setAssociationModifiedOn(rs.getTimestamp("ASSOCIATED_MSISDN_MDATE"));
                }
                
                if((PretupsI.YES).equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION)))) 
					 userVO.setAllowedUserTypeCreation(rs.getString("ALLOWD_USR_TYP_CREATION"));
				 

                list.add(userVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersList]", "", "", "", "Exception:" + ex
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for Updating User Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            UserVO
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateUserForAssociate(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        
        int updateCount = 0;
        final String methodName = "updateUserForAssociate";
        UserDAO userDao = null;
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userVO= ");
        	loggerValue.append(p_userVO);
            _log.debug(methodName, loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE users SET modified_by = ?,modified_on = ? ");
            strBuff.append(" WHERE user_id = ?");

            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            psmtUpdate.setString(1, p_userVO.getModifiedBy());
            psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));
            psmtUpdate.setString(3, p_userVO.getUserID());

            // check wehther the record already updated or not
            userDao = new UserDAO();
            final boolean modified = userDao.recordModified(p_con, p_userVO.getUserID(), p_userVO.getLastModified());
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }

            updateCount = psmtUpdate.executeUpdate();
        }
        }// end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUserForAssociate]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUserForAssociate]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method for deleting User Info.
     * It Deletes the info form the following Tables
     * user_phones,user_geographies,user_roles,user_domains,user_services,
     * user_products
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int deleteUserInfo(Connection p_con, String p_userId) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        PreparedStatement psmtDelete1 = null;
        PreparedStatement psmtDelete2 = null;
        PreparedStatement psmtDelete3 = null;
        PreparedStatement psmtDelete4 = null;
        PreparedStatement psmtDelete5 = null;
        PreparedStatement psmtDelete6 = null;
        int deleteCount = 0;
        final String methodName = "deleteUserInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userId= " + p_userId);
        }
        String deleteQuery = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff = new StringBuilder("delete from user_geographies where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_userId);
            deleteCount = psmtDelete.executeUpdate();

            // delete from the user_roles table
            strBuff = new StringBuilder("delete from user_roles where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete1 = p_con.prepareStatement(deleteQuery);
            psmtDelete1.setString(1, p_userId);
            deleteCount = psmtDelete1.executeUpdate();

            // delete from the user_domains table
            strBuff = new StringBuilder("delete from user_domains where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete2 = p_con.prepareStatement(deleteQuery);
            psmtDelete2.setString(1, p_userId);
            deleteCount = psmtDelete2.executeUpdate();

            // delete from the user_services table
            strBuff = new StringBuilder("delete from user_services where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete3 = p_con.prepareStatement(deleteQuery);
            psmtDelete3.setString(1, p_userId);
            deleteCount = psmtDelete3.executeUpdate();

            // delete from the user_products table
            strBuff = new StringBuilder("delete from user_product_types where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete4 = p_con.prepareStatement(deleteQuery);
            psmtDelete4.setString(1, p_userId);
            deleteCount = psmtDelete4.executeUpdate();
            // delete from USER_VOUCHERTYPES table
            strBuff = new StringBuilder("delete from USER_VOUCHERTYPES where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete5 = p_con.prepareStatement(deleteQuery);
            psmtDelete5.setString(1, p_userId);
            deleteCount = psmtDelete5.executeUpdate();
            
            strBuff = new StringBuilder("delete from USER_VOUCHER_SEGMENTS where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete6 = p_con.prepareStatement(deleteQuery);
            psmtDelete6.setString(1, p_userId);
            deleteCount = psmtDelete6.executeUpdate();

        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deleteUserInfo]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deleteUserInfo]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtDelete1 != null) {
                    psmtDelete1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtDelete2 != null) {
                    psmtDelete2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtDelete3 != null) {
                    psmtDelete3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtDelete4 != null) {
                    psmtDelete4.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtDelete5 != null) {
                    psmtDelete5.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtDelete6 != null) {
                    psmtDelete6.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally
        return deleteCount;
    }

    /********************************** CHANNEL USER RELATED METHODS *******************************/

    /**
     * Method for loading Root level User List.
     * 
     * e.g suppose BCU add Retailer, we need to load the list of all users
     * reside at root level (where sequence no = 1) of that category
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentGraphDomainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadOwnerUserList(Connection p_con, String parentGraphDomainCode, String username, String domainCode, String statusUsed, String status) throws BTSLBaseException {
        final String methodName = "loadOwnerUserList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_parentGraphDomainCode= ");
        	msg.append(parentGraphDomainCode);
        	msg.append(", p_username= ");
        	msg.append(username);
        	msg.append(", p_domainCode= ");
        	msg.append(domainCode);
        	msg.append(", p_stausUsed= ");
        	msg.append(statusUsed);        	
        	msg.append(", p_staus= ");
        	msg.append(status);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        String p_parentGraphDomainCode = SqlParameterEncoder.encodeParams(parentGraphDomainCode);
        String p_username = SqlParameterEncoder.encodeParams(username);
        String p_domainCode = SqlParameterEncoder.encodeParams(domainCode);
        String p_statusUsed = SqlParameterEncoder.encodeParams(statusUsed);
        String p_status = SqlParameterEncoder.encodeParams(status);
        final String sqlSelect = userWebQry.loadOwnerUserListQry(p_statusUsed, p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            
            int i = 1;
            // commented for DB2pstmt.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmt.setString(i++, p_username);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(i++, p_status);
            }
            pstmt.setString(i++, p_domainCode);
            pstmt.setString(i++, p_parentGraphDomainCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                userVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                userVO.setOwnerID(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
                userVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
                list.add(userVO);
            }

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOwnerUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOwnerUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * Method for loading Root level User List.
     * 
     * e.g suppose BCU add Retailer, we need to load the list of all users
     * reside at root level (where sequence no = 1) of that category
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentGraphDomainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList checkOwnerUserList(Connection p_con, String parentGraphDomainCode, String username, String domainCode, String statusUsed, String status,String ownerLoginID) throws BTSLBaseException {
        final String methodName = "loadOwnerUserList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_parentGraphDomainCode= ");
        	msg.append(parentGraphDomainCode);
        	msg.append(", p_username= ");
        	msg.append(username);
        	msg.append(", p_domainCode= ");
        	msg.append(domainCode);
        	msg.append(", p_stausUsed= ");
        	msg.append(statusUsed);        	
        	msg.append(", p_staus= ");
        	msg.append(status);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        String p_parentGraphDomainCode = SqlParameterEncoder.encodeParams(parentGraphDomainCode);
        String p_username = SqlParameterEncoder.encodeParams(username);
        String p_domainCode = SqlParameterEncoder.encodeParams(domainCode);
        String p_statusUsed = SqlParameterEncoder.encodeParams(statusUsed);
        String p_status = SqlParameterEncoder.encodeParams(status);
        final String sqlSelect = userWebQry.checkOwnerListQuery(p_statusUsed, p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            
            int i = 1;
            // commented for DB2pstmt.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmt.setString(i++, p_username);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(i++, p_status);
            }
            pstmt.setString(i++, p_domainCode);
            pstmt.setString(i++, ownerLoginID);
            pstmt.setString(i++, p_parentGraphDomainCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                userVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                userVO.setOwnerID(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
                userVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
                list.add(userVO);
            }

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOwnerUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOwnerUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }


    /**
     * By Mayank
     * Method load owner user according to (logined user zone)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentGraphDomainCode
     *            String
     * @param p_userID
     *            String
     * @param p_username
     *            String
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadReportOwnerUserList(Connection p_con, String p_parentGraphDomainCode, String p_userID, String p_username, String p_domainCode, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadReportOwnerUserList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_parentGraphDomainCode= ");
        	msg.append(p_parentGraphDomainCode);
        	msg.append(", p_username= ");
        	msg.append(p_username);
        	msg.append(", p_domainCode= ");
        	msg.append(p_domainCode);
        	msg.append(", p_userID= ");
        	msg.append(p_userID);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList list = new ArrayList();
        try {
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
           pstmt=userWebQry.loadReportOwnerUserListQry(p_con, p_parentGraphDomainCode, p_userID, p_username, p_domainCode, p_networkCode);
            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setOwnerID(rs.getString("owner_id"));
                list.add(userVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadReportOwnerUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadReportOwnerUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Users List.
     * This method called to load the channel user details
     * 
     * If Operator User logged in and this method called at this time p_userID
     * is null
     * But if channel user logged in at this time p_userID = logged in user
     * id(session user id)
     * if p_userID != null apply Connect By Prior
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            String
     * @param p_userName
     *            String
     * @param p_ownerId
     *            String
     * @param p_userID
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersListByNameAndOwnerId(Connection p_con, String p_categoryCode, String p_userName, String p_ownerId, String p_userID, String p_statusUsed, String p_status, String p_userType) throws BTSLBaseException {
        final String methodName = "loadUsersListByNameAndOwnerId";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", User Name= ");
        	msg.append(p_userName);
        	msg.append(", p_ownerId= ");
        	msg.append(p_ownerId);
        	msg.append(", p_userID= ");
        	msg.append(p_userID);        	
        	msg.append(", p_statusUsed= ");
        	msg.append(p_statusUsed);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList list = new ArrayList();
        try {
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = userWebQry.loadUsersListByNameAndOwnerIdQry(p_con, p_categoryCode,p_userName,p_ownerId,
            		p_userID,p_statusUsed,p_status, p_userType);
           /*String var1 = " @oooooooo%oooooooo,oooooooo&ooooooo<ooooo";
            String var2 = SqlParameterEncoder.encodeParams(var1);
            System.out.println(var2);*/
            /* String var3 = SqlParameterEncoder.decodeparams(var2);*/
            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                userVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                userVO.setNetworkID(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
                userVO.setLoginID(SqlParameterEncoder.encodeParams(rs.getString("login_id")));
                userVO.setPassword(SqlParameterEncoder.encodeParams(rs.getString("password")));
                userVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                userVO.setParentID(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
                userVO.setOwnerID(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
                userVO.setAllowedIps(SqlParameterEncoder.encodeParams(rs.getString("allowed_ip")));
                userVO.setAllowedDays(SqlParameterEncoder.encodeParams(rs.getString("allowed_days")));
                userVO.setFromTime(SqlParameterEncoder.encodeParams(rs.getString("from_time")));
                userVO.setToTime(SqlParameterEncoder.encodeParams(rs.getString("to_time")));
                userVO.setEmpCode(SqlParameterEncoder.encodeParams(rs.getString("employee_code")));
                userVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                userVO.setEmail(SqlParameterEncoder.encodeParams(rs.getString("email")));
                // Added by deepika aggarwal
                userVO.setCompany(SqlParameterEncoder.encodeParams(rs.getString("company")));
                userVO.setFax(SqlParameterEncoder.encodeParams(rs.getString("fax")));
                userVO.setFirstName(SqlParameterEncoder.encodeParams(rs.getString("firstname")));
                userVO.setLastName(SqlParameterEncoder.encodeParams(rs.getString("lastname")));
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(SqlParameterEncoder.encodeParams(rs.getString("contact_no")));
                userVO.setDesignation(SqlParameterEncoder.encodeParams(rs.getString("designation")));
                userVO.setDivisionCode(SqlParameterEncoder.encodeParams(rs.getString("division")));
                userVO.setDepartmentCode(SqlParameterEncoder.encodeParams(rs.getString("department")));
                userVO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("msisdn")));
                userVO.setUserType(SqlParameterEncoder.encodeParams(rs.getString("user_type")));
                userVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                userVO.setAddress1(SqlParameterEncoder.encodeParams(rs.getString("address1")));
                userVO.setAddress2(SqlParameterEncoder.encodeParams(rs.getString("address2")));
                userVO.setCity(SqlParameterEncoder.encodeParams(rs.getString("city")));
                userVO.setState(SqlParameterEncoder.encodeParams(rs.getString("state")));
                userVO.setCountry(SqlParameterEncoder.encodeParams(rs.getString("country")));
                userVO.setSsn(SqlParameterEncoder.encodeParams(rs.getString("ssn")));
                userVO.setUserNamePrefix(SqlParameterEncoder.encodeParams(rs.getString("user_name_prefix")));
                userVO.setExternalCode(SqlParameterEncoder.encodeParams(rs.getString("external_code")));
                userVO.setShortName(SqlParameterEncoder.encodeParams(rs.getString("short_name")));
                userVO.setLevel1ApprovedBy(SqlParameterEncoder.encodeParams(rs.getString("level1_approved_by")));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(SqlParameterEncoder.encodeParams(rs.getString("level2_approved_by")));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setUserCode(SqlParameterEncoder.encodeParams(rs.getString("user_code")));

                list.add(userVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersListByNameAndOwnerId]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersListByNameAndOwnerId]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }

    /*
     * Added by Shaina
     * Method for loading Commision Profile for a particular category and
     * geography
     * 
     * @param p_con java.sql.Connection
     * 
     * @param p_categoryCode String
     * 
     * @param p_networkCode String
     * 
     * @param p_graphDomainTypeName
     * 
     * @return java.util.ArrayList
     * 
     * @throws BTSLBaseException
     */
    public ArrayList loadCommisionProfileListByCategoryIDandGeography(Connection p_con, String p_categoryCode, String p_networkCode, String p_graphDomainName) throws BTSLBaseException {
        flag = true;
        final String methodName = "loadCommisionProfileListByCategoryIDandGeography";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_graphDomainName= ");
        	msg.append(p_graphDomainName);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
    
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT comm_profile_set_id, comm_profile_set_name,grade_code,status");
        strBuff.append(" FROM commission_profile_set WHERE category_code = ? ");
        if (BTSLUtil.isNullString(p_graphDomainName)) {
            strBuff.append(" AND network_code = ? AND status != 'N' ");
        } else {
            strBuff.append(" AND network_code = ? AND status != 'N' AND (geography_code = ? OR geography_code = 'ALL')");
        }
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)  {

            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);
            if (!BTSLUtil.isNullString(p_graphDomainName)) {
                pstmt.setString(3, p_graphDomainName);
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            CommissionProfileSetVO commProfSetVO = null;
            while (rs.next()) {
                commProfSetVO = new CommissionProfileSetVO();
                commProfSetVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                if(rs.getString("status").equalsIgnoreCase(PretupsI.COMMISSION_PROFILE_STATUS_SUSPEND)){
					commProfSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name")+BTSLUtil.NullToString(Constants.getProperty("COMM_PROFILE_SUSPENDED")));
				}
				else
					commProfSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name"));
               
                commProfSetVO.setGradeCode(rs.getString("grade_code"));
                list.add(commProfSetVO);
            }

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCommisionProfileListByCategoryID]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCommisionProfileListByCategoryID]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: commissionProfileList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Users(for level one approval) List.
     * 
     * 
     * on
     * 
     * @param p_categoryCode
     *            String
     * @param p_lookupType
     *            String
     * @param p_sequenceNo
     *            int
     * @param p_grphDomainType
     *            String
     * @param p_networkCode
     *            String
     * @param p_parentGrphDomainCode
     *            String
     * @param p_status
     *            String
     * 
     *            * @return java.util.ArrayList
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadApprovalUsersList(Connection p_con, String p_categoryCode, String p_lookupType, int p_sequenceNo, String p_grphDomainType, String p_networkCode, String p_parentGrphDomainCode, String p_status, String p_userType) throws BTSLBaseException {
        final String methodName = "loadApprovalUsersList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", p_lookupType= ");
        	msg.append(p_lookupType);
        	msg.append(", p_sequenceNo= ");
        	msg.append(p_sequenceNo);
        	msg.append(", p_grphDomainType= ");
        	msg.append(p_grphDomainType);        	
        	msg.append(", p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_parentGrphDomainCode= ");
        	msg.append(p_parentGrphDomainCode);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
        final ArrayList list = new ArrayList();
        try {
            pstmt = userWebQry.loadApprovalUsersListQry(p_con, p_categoryCode, p_lookupType, p_sequenceNo, p_grphDomainType, p_networkCode, p_parentGrphDomainCode, p_status, p_userType);

            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name")); 
                userVO.setParentName(rs.getString("parent_name"));
                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("password"));
                userVO.setCategoryCode(rs.getString("category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("status"));
                userVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                userVO.setCompany(rs.getString("company"));
                userVO.setFax(rs.getString("fax"));
                userVO.setFirstName(rs.getString("firstname"));
                userVO.setLastName(rs.getString("lastname"));
                userVO.setLanguage(rs.getString("phone_language") + "_" + rs.getString("ctry"));
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                // Added for RSA Authentication
                userVO.setRsaFlag(rs.getString("rsaflag"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setStatusDesc(rs.getString("lookup_name"));
                userVO.setCreatedByUserName(rs.getString("parent_user_name"));
                userVO.setPreviousStatus(rs.getString("previous_status"));

                userVO.setRequetedByUserName(rs.getString("request_user_name"));
				
				if(!BTSLUtil.isNullString(rs.getString("request_user_name")))
                userVO.setSuspendedByUserName(rs.getString("request_user_name"));
                else
                userVO.setSuspendedByUserName(PretupsI.SYSTEM);
               
			   list.add(userVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalUsersList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalUsersList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: usersList size=" + list.size());
            }
        }
        return list;
    }

    /************************** Methods for Channel User View ************************/

    /**
     * Method for checking the user in same domain or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_userGrphDomainType
     *            String
     * @param p_sessionUserId
     *            String
     * @param p_sessionUserGrphDomainType
     *            String
     * 
     * @return CategoryVO
     * @exception BTSLBaseException
     */
    public boolean isUserInSameGRPHDomain(Connection p_con, String p_userId, String p_userGrphDomainType, String p_sessionUserId, String p_sessionUserGrphDomainType) throws BTSLBaseException {
        final String methodName = "isUserInSameGRPHDomain";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_userId= ");
        	msg.append(p_userId);
        	msg.append(", p_userGrphDomainType= ");
        	msg.append(p_userGrphDomainType);
        	msg.append(", p_sessionUserId= ");
        	msg.append(p_sessionUserId);
        	msg.append(", p_sessionUserGrphDomainType= ");
        	msg.append(p_sessionUserGrphDomainType);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean isDomainFlag = false;
        try {
            pstmt = userWebQry.isUserInSameGRPHDomainQry(p_con, p_userId, p_sessionUserId, p_userGrphDomainType, p_sessionUserGrphDomainType);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                isDomainFlag = true;
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserInSameGRPHDomain]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserInSameGRPHDomain]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: isDomainFlag=" + isDomainFlag);
            }
        }
        return isDomainFlag;
    }

    /**
     * Method for Updating self User Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            UserVO
     * @return updateCount int
     * @throws BTSLBaseException
     *             ved.sharma
     */
    public int updateUserDetails(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        // commented for DB2OraclePreparedStatement psmtUpdate = null;
        
        int updateCount = 0;
        final String methodName = "updateUserDetails";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userVO= ");
        	loggerValue.append(p_userVO);
            _log.debug(methodName, loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder("UPDATE users SET login_id = ?, password = ?,");
            strBuff.append(" email = ?, pswd_modified_on = ?, contact_no = ?,");
            strBuff.append(" designation = ?, modified_by = ?, modified_on = ?, address1 = ?, ");
            strBuff.append(" address2 = ?, city = ?, state = ?, country = ?, user_name_prefix = ?, ");
            strBuff.append(" short_name = ?, user_name = ? ,company=?,fax=?,firstname=?,lastname=? WHERE user_id = ?"); // firstname,lastname,company
            // fax
            // added
            // by
            // deepika
            // aggarwal

            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            // commented for DB2psmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(updateQuery);
            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            int i = 0;
            psmtUpdate.setString(++i, p_userVO.getLoginID());
            psmtUpdate.setString(++i, p_userVO.getPassword());
            psmtUpdate.setString(++i, p_userVO.getEmail());

            if (p_userVO.getPasswordModifiedOn() != null) {
                psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_userVO.getPasswordModifiedOn()));
            } else {
                psmtUpdate.setString(++i, null);
            }

            psmtUpdate.setString(++i, p_userVO.getContactNo());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getDesignation());

            psmtUpdate.setString(++i, p_userVO.getModifiedBy());
            psmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getAddress1());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getAddress2());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getCity());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getState());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getCountry());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getUserNamePrefix());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getShortName());

            // commented for DB2psmtUpdate.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(++i, p_userVO.getUserName());
            // Added by Deepika aggarwal
            psmtUpdate.setString(++i, p_userVO.getCompany());
            psmtUpdate.setString(++i, p_userVO.getFax());
            psmtUpdate.setString(++i, p_userVO.getFirstName());
            psmtUpdate.setString(++i, p_userVO.getLastName());
            // end added by deepika aggarwal
            psmtUpdate.setString(++i, p_userVO.getUserID());

            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final boolean modified = channelUserDAO.isRecordModified(p_con, p_userVO.getUserID(), p_userVO.getLastModified());
            if (modified) {
                throw new BTSLBaseException(this, "resumeChannelUser", "error.modify.true");
            }

            // check wehther the record already updated or not
            updateCount = psmtUpdate.executeUpdate();
        }
        }// end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException: " + be.getMessage());
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUserDetails]", "", "", "",
                "BTSLBaseException :" + be.getMessage());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUserDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUserDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    // added by Siddhartha for staffUser

    /**
     * This method counts the number of staff users under the channel user
     * 
     * @param userid
     * @param userType
     * @returns int
     */
    public int staffUserCount(Connection p_con, String p_userId, String p_userType) throws BTSLBaseException {
        final String methodName = "staffUserCount";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_userId= ");
        	msg.append(p_userId);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	
        	String message=msg.toString();
        	_log.debug(methodName, message);
        }
        int userCount = 0;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT COUNT(1) FROM users WHERE parent_id = ? AND user_type = ? AND status NOT IN('N','C')");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("staffUserCount ", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);
            pstmt.setString(2, p_userType);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                userCount = rs.getInt(1);
            }
            return userCount;
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[staffUserCount]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[staffUserCount]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userCount=" + userCount);
            }
        }
    }

    /**
     * Date : May 2, 2007
     * Discription :
     * Method : loadChildUserList
     * 
     * @param p_con
     * @param p_userId
     * @param p_userType
     * @throws BTSLBaseException
     * @return ArrayList
     * @author ved.sharma
     */
    public ArrayList loadChildUserList(Connection p_con, String p_userId, String p_userType, String p_userName) throws BTSLBaseException {
        final String methodName = "loadChildUserList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_userId= ");
        	msg.append(BTSLUtil.maskParam(p_userId));
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	msg.append(", p_userName= ");
        	msg.append(p_userName);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        final ArrayList userList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT user_id, user_name, login_id,status,msisdn FROM users");
        strBuff.append(" WHERE parent_id = ? AND user_type = ? AND status NOT IN('N','C')");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadChildUserList ", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userId);
            pstmt.setString(2, p_userType);
            pstmt.setString(3, p_userName);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("user_name"), rs.getString("user_id"));
                listValueVO.setOtherInfo(rs.getString("login_id"));
                userList.add(listValueVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList.size()=" + userList.size());
            }
        }
        return userList;
    }
    public ArrayList<ChannelUserVO> loadStaffUserList(Connection p_con, String p_userId, String p_userType, String p_userName,String userType) throws BTSLBaseException {
        final String methodName = "loadStaffUserList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_userId= ");
        	msg.append(p_userId);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	msg.append(", p_userName= ");
        	msg.append(p_userName);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        final ArrayList userList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT user_id, user_name, login_id,status,msisdn FROM users WHERE ");
        if (!userType.equalsIgnoreCase(PretupsI.STAFF_USER_TYPE))
            strBuff.append("parent_id = ? AND ");
        strBuff.append("user_type = ? AND status NOT IN('N','C')");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadChildUserList ", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            int i = 1;
            if (!userType.equalsIgnoreCase(PretupsI.STAFF_USER_TYPE))
                pstmt.setString(i++, p_userId);
            pstmt.setString(i++, p_userType);
            pstmt.setString(i++, p_userName);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            ChannelUserVO channelUserVO = null;
            while (rs.next()) {
            	channelUserVO = new ChannelUserVO();
            	channelUserVO.setUserID(rs.getString("user_id"));
            	channelUserVO.setUserName(rs.getString("user_name"));
            	channelUserVO.setLoginID(rs.getString("login_id"));
            	channelUserVO.setStatus(rs.getString("status"));
            	channelUserVO.setMsisdn(rs.getString("msisdn"));
            	userList.add(channelUserVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList.size()=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * This methods used to update PIN of the channel user's msisdn.
     * 
     * @param p_con
     * @param p_phoneList
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSmsPin(Connection p_con, ArrayList p_phoneList) throws BTSLBaseException {
        
        int updateCount = 0;
        final String methodName = "updateSmsPin";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_phoneList size = ");
        	loggerValue.append(p_phoneList.size());
            _log.debug(methodName, loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE user_phones SET sms_pin = ?,modified_on = ?, modified_by=?, pin_modified_on=?, pin_reset=?");
            strBuff.append(" WHERE user_phones_id = ?");
            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }
            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            UserPhoneVO phoneVO = null;
            final Iterator itr = p_phoneList.iterator();
            while (itr.hasNext()) {
                phoneVO = (UserPhoneVO) itr.next();
                psmtUpdate.setString(1, BTSLUtil.encryptText(phoneVO.getShowSmsPin()));
                psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(phoneVO.getModifiedOn()));
                psmtUpdate.setString(3, phoneVO.getModifiedBy());
                psmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(phoneVO.getPinModifiedOn()));
                psmtUpdate.setString(5, PretupsI.NO);
                psmtUpdate.setString(6, phoneVO.getUserPhonesId());

                updateCount += psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateSmsPin]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateSmsPin]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    // added for operator user approval
    public ArrayList loadApprovalOPTUsersList(Connection p_con, String p_categoryCode, String p_lookupType, String p_networkCode, String p_status) throws BTSLBaseException {
        final String methodName = "loadApprovalOPTUsersList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", p_lookupType= ");
        	msg.append(p_lookupType);
        	msg.append(", p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        

        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT U.batch_id, U.creation_type, u.user_id,u.user_name,u.network_code,");
        strBuff.append("u.login_id,u.password,u.category_code,u.parent_id,U.company,U.fax,U.firstname,U.lastname, ");// firstname,lastname,company,fax
        // added
        // by
        // deepika
        // aggarwal
        strBuff.append("OUSR.user_name owner_name, PUSR.user_name parent_name,");
        strBuff.append("u.owner_id,u.allowed_ip,u.allowed_days,");
        strBuff.append("u.from_time,u.to_time,u.employee_code,");
        strBuff.append("u.status,u.email,u.pswd_modified_on,u.contact_no,");
        strBuff.append("u.designation,u.division,u.department,u.msisdn,u.user_type,");
        strBuff.append("u.created_by,u.created_on,u.modified_by,u.modified_on,u.address1, ");
        strBuff.append("u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix, ");
        strBuff.append("u.external_code,u.short_name,u.level1_approved_by,u.level1_approved_on,");
        strBuff.append("u.level2_approved_by,u.level2_approved_on,u.user_code,u.appointment_date,");
        strBuff.append("u.previous_status,l.lookup_name,u1.user_name parent_user_name,u2.user_name request_user_name ");
        strBuff.append("FROM users u1,users u2,categories c,lookups l,users OUSR,users PUSR right outer join users u on PUSR.user_id = u.parent_id WHERE ");
        strBuff.append(" u.category_code = ? AND u.category_code = c.category_code ");
        strBuff.append(" AND OUSR.user_id = u.owner_id ");
        strBuff.append(" AND u1.user_id = u.created_by ");
        strBuff.append(" AND u2.user_id = u.modified_by ");
        strBuff.append(" AND u.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND u.status in (" + p_status + ") ");
        strBuff.append(" AND u.network_code = ? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_lookupType);
            pstmt.setString(3, p_networkCode);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setParentName(rs.getString("parent_name"));
                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("password"));
                userVO.setCategoryCode(rs.getString("category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("status"));
                userVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                userVO.setCompany(rs.getString("company"));
                userVO.setFax(rs.getString("fax"));
                userVO.setFirstName(rs.getString("firstname"));
                userVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setStatusDesc(rs.getString("lookup_name"));
                userVO.setCreatedByUserName(rs.getString("parent_user_name"));
                userVO.setPreviousStatus(rs.getString("previous_status"));

                userVO.setRequetedByUserName(rs.getString("request_user_name"));
                userVO.setSuspendedByUserName(rs.getString("request_user_name"));

                list.add(userVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalOPTUsersList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalOPTUsersList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: usersList size=" + list.size());
            }
        }
        return list;
    }

    // added for operator user approval(this method used at the time of
    // view/modify operator user).
    public ArrayList loadOperatorUsersList(Connection p_con, String p_networkCode, String p_categoryCode, String p_userName, String p_userID, String p_ownerID, String p_sessionUserID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadOperatorUsersList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", User Name= ");
        	msg.append(p_userName);
        	msg.append(", p_userID= ");
        	msg.append(p_userID);
        	msg.append(", p_sessionUserID= ");
        	msg.append(p_sessionUserID);
        	msg.append(", p_ownerID= ");
        	msg.append(p_ownerID);
        	msg.append(", p_statusUsed= ");
        	msg.append(p_statusUsed);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
        strBuff.append("USR.login_id,USR.password passwd,USR.category_code usr_category_code,USR.parent_id,");
        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,");
        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,USR.company,USR.fax,USR.firstname,USR.lastname, ");// firstname,lastname,company,fax
        // added
        // by
        // deepika
        // aggarwal
        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,USR.contact_person,");
        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
        strBuff.append("USR.created_by,USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
        strBuff.append("USR.previous_status,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name,USR_CAT.domain_allowed,USR_CAT.fixed_domains,USR_CAT.PRODUCT_TYPES_ALLOWED,USR_CAT.SERVICES_ALLOWED,USR_CAT.MAX_TXN_MSISDN,");
        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, ");
        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, ");
        strBuff
            .append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns, ");
        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, ");
        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, ");
        strBuff.append("ONR_CAT.category_name owner_cat,l.lookup_name, USR_CAT.transfertolistonly, USR_CAT.outlets_allowed ");
        // added by shashank for bug fix
        strBuff.append(",USR.rsaflag,USR.authentication_allowed,USR_CAT.authentication_type,USR1.USER_NAME AS modified_by_username ");
        
        if((PretupsI.YES).equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION))))
			 strBuff.append(",USR.ALLOWD_USR_TYP_CREATION ");
        
        strBuff.append("FROM users USR1,users USR left join (users PRNT_USR left join  categories  PRNT_CAT on PRNT_USR.category_code=PRNT_CAT.category_code )on USR.parent_id=PRNT_USR.user_id,users ONR_USR,categories USR_CAT, ");
        strBuff.append("categories ONR_CAT, lookups l ");
        strBuff.append("WHERE USR.network_code = ? ");

        if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND USR.status NOT IN (" + p_status + ")");
        }

        if (!BTSLUtil.isNullString(p_categoryCode)) {
            strBuff.append(" AND USR.category_code =?");
        }
        if (!BTSLUtil.isNullString(p_userID)) {
            strBuff.append(" AND USR.user_id =?");
        }
        if (!BTSLUtil.isNullString(p_ownerID)) {
            strBuff.append(" AND USR.owner_id =?");
        }
        if (!BTSLUtil.isNullString(p_userName)) {
            strBuff.append(" AND UPPER(USR.user_name) LIKE UPPER(?) ");
        }
        strBuff.append("AND USR.owner_id=ONR_USR.user_id ");
        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
        strBuff.append(" AND USR.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append("AND USR1.user_id = USR.modified_by ");
        if (p_sessionUserID != null) {
            strBuff.append(" AND (USR.created_by= ? OR USR.level1_approved_by= ?) ");
        }
        strBuff.append(" ORDER BY USR.user_name ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            if (!BTSLUtil.isNullString(p_categoryCode)) {
                pstmt.setString(++i, p_categoryCode);
            }
            if (!BTSLUtil.isNullString(p_userID)) {
                pstmt.setString(++i, p_userID);
            }
            if (!BTSLUtil.isNullString(p_ownerID)) {
                pstmt.setString(++i, p_ownerID);
            }
            if (!BTSLUtil.isNullString(p_userName)) {
                // commented for DB2 pstmt.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                pstmt.setString(++i, p_userName);
            }
            pstmt.setString(++i, PretupsI.USER_STATUS_TYPE);
            if (p_sessionUserID != null) {
                pstmt.setString(++i, p_sessionUserID);
                pstmt.setString(++i, p_sessionUserID);
            }

            rs = pstmt.executeQuery();
            
            
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new ChannelUserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("usr_user_id"));
                userVO.setUserName(rs.getString("usr_user_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("passwd"));
                userVO.setCategoryCode(rs.getString("usr_category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setContactPerson(rs.getString("contact_person"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("usr_status"));
                userVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                userVO.setCompany(rs.getString("company"));
                userVO.setFax(rs.getString("fax"));
                userVO.setFirstName(rs.getString("firstname"));
                userVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("usr_msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                //userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedBy(rs.getString("modified_by_username"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setRsaFlag(rs.getString("rsaflag"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setPreviousStatus(rs.getString("previous_status"));
                userVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
                final CategoryVO categoryVO = new CategoryVO();
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
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setProductTypeAllowed(rs.getString("PRODUCT_TYPES_ALLOWED"));
                categoryVO.setServiceAllowed(rs.getString("SERVICES_ALLOWED"));
                categoryVO.setMaxTxnMsisdn(rs.getString("MAX_TXN_MSISDN"));
                userVO.setCategoryVO(categoryVO);

                userVO.setParentName(rs.getString("parent_name"));
                userVO.setParentMsisdn(rs.getString("parent_msisdn"));
                userVO.setParentCategoryName(rs.getString("parent_cat"));

                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                userVO.setOwnerCategoryName(rs.getString("owner_cat"));
                userVO.setStatusDesc(rs.getString("lookup_name"));
                
                if((PretupsI.YES).equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION)))) 
					 userVO.setAllowedUserTypeCreation(rs.getString("ALLOWD_USR_TYP_CREATION"));

                list.add(userVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOperatorUsersList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadOperatorUsersList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_userName
     *            String
     * @param p_userType
     *            String
     * @param p_sessionUserID
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @param p_isChannelUser
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersListForUserType(Connection p_con, String p_networkCode, String p_categoryCode, String p_userName, String p_userType, String p_sessionUserID, String p_statusUsed, String p_status, boolean p_isChannelUser) throws BTSLBaseException {
        final String methodName = "loadUsersListForUserType";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", User Name= ");
        	msg.append(p_userName);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	msg.append(", p_sessionUserID= ");
        	msg.append(p_sessionUserID);
        	msg.append(", p_isChannelUser= ");
        	msg.append(p_isChannelUser);
        	msg.append(", p_statusUsed= ");
        	msg.append(p_statusUsed);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = userWebQry.loadUsersListForUserTypeQry(p_statusUsed, p_status, p_isChannelUser);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            if (p_isChannelUser) {
                pstmt.setString(++i, p_sessionUserID);
            }

            pstmt.setString(++i, p_userType);
            pstmt.setString(++i, p_userType);
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_categoryCode);
            // commented for DB2pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmt.setString(++i, p_userName);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(++i, p_status);
            }
            pstmt.setString(++i, p_sessionUserID);

            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new ChannelUserVO();

                userVO.setUserID(rs.getString("usr_user_id"));
                userVO.setUserName(rs.getString("usr_user_name"));
                userVO.setLoginID(rs.getString("login_id"));
                list.add(userVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersListForUserType]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersListForUserType]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * loads all the staff users along with the parent user
     * Date : Dec ,1 2009
     * Discription :
     * Method : loadUserList
     * 
     * @param p_con
     * @param p_userId
     *            /p_parentId
     * @param p_userType
     * @throws BTSLBaseException
     * @return ArrayList
     * @author vikram.kumar
     */
    public ArrayList loadUserListByLogin(Connection p_con, String p_userId, String p_userType, String p_userName) throws BTSLBaseException {
        final String methodName = "loadUserListByLogin";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_userId= ");
        	msg.append(p_userId);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	msg.append(", User Name= ");
        	msg.append(p_userName);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        ArrayList userList = null;
        final StringBuilder strBuff = new StringBuilder(
            " SELECT user_id, user_name, login_id, user_type, msisdn,u.status,u.category_code,cat.domain_code FROM users u, CATEGORIES cat");
        strBuff.append(" WHERE u.status NOT IN('N','C','D') AND (user_type = ?");
        if (!p_userId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)) {
            strBuff.append(" AND parent_id = ? ");
        }
        strBuff.append(" AND UPPER(login_id) LIKE UPPER( ? ) OR user_id = ?) AND u.category_code=cat.category_code AND cat.status=?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserListByLogin ", "QUERY sqlSelect=" + sqlSelect);
        }
        int i = 1;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(i++, p_userType);
            if (!p_userId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)) {
                pstmt.setString(i++, p_userId);
            }
            pstmt.setString(i++, p_userName);
            pstmt.setString(i++, p_userId);
            pstmt.setString(i++, PretupsI.STATUS_ACTIVE);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            ListValueVO listValueVO = null;
            userList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("login_id"), rs.getString("user_id"));
                listValueVO.setOtherInfo(rs.getString("user_name")); // user
                // name for
                // staff /
                // channel
                // user
                listValueVO.setType(rs.getString("user_type")); // adds type
                // staff/
                // channel user
                listValueVO.setOtherInfo2(rs.getString("msisdn")); // msisdn of
                // the staff
                // / channel
                // user
                listValueVO.setStatus(rs.getString("status"));
                listValueVO.setCodeName(rs.getString("category_code"));
                listValueVO.setIDValue(rs.getString("domain_code"));
                userList.add(listValueVO);
            }
        }
            } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList.size()=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * @author vikram.kumar
     *         Method for Resuming/Suspending User Information from Users Table
     *         (This is soft delete just update the status, set status =
     *         N = delete.
     *         S= Suspend.
     *         Y=Active
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            ArrayList(consist of UserVO)
     * @return deleteCount int
     * @throws BTSLBaseException
     */
    public int suspendResumeStaffUser(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        final String methodName = "suspendResumeStaffUser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: userVO= " + p_userVO);
        }
        PreparedStatement pstmtSuspend = null;
        UserDAO userDao = null;
        int deleteCount = 0;
        try {
            boolean modified = false;
            userDao = new UserDAO();
            modified = userDao.recordModified(p_con, p_userVO.getUserID(), p_userVO.getLastModified());
            // if modified = true means record modified by another user
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE users SET status = ?, previous_status = ? , modified_by = ?,");
            strBuff.append(" modified_on = ? WHERE user_id = ?");

            final String sqlQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlUpdate:" + sqlQuery);
            }
            pstmtSuspend = p_con.prepareStatement(sqlQuery);
            pstmtSuspend.setString(1, p_userVO.getStatus());
            pstmtSuspend.setString(2, p_userVO.getPreviousStatus());
            pstmtSuspend.setString(3, p_userVO.getModifiedBy());
            pstmtSuspend.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));
            pstmtSuspend.setString(5, p_userVO.getUserID());
            deleteCount = pstmtSuspend.executeUpdate();
            // check the status of the update
            if (deleteCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            }
        } // end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[suspendResumeStaffUser]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[suspendResumeStaffUser]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtSuspend != null) {
                    pstmtSuspend.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally
        return deleteCount;
    }

    /**
     * Date : Dec 16, 2009
     * Discription :
     * Method : loadUserList
     * 
     * @param p_con
     * @param p_userId
     *            /p_parentId
     * @param p_userType
     * @throws BTSLBaseException
     * @return ArrayList
     * @author vikram.kumar
     */
    public ArrayList loadSuspendedUserListByLogin(Connection p_con, String p_userId, String p_userType, String p_userName) throws BTSLBaseException {
        final String methodName = "loadSuspendedUserListByLogin";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_userId= ");
        	msg.append(p_userId);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	msg.append(", p_userName= ");
        	msg.append(p_userName);        	
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        ArrayList userList = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT user_id, user_name, login_id, user_type, msisdn, status FROM users ");
        strBuff.append(" WHERE parent_id = ? AND user_type = ? AND status IN('S') ");
        strBuff.append(" AND UPPER(login_id) LIKE UPPER( ? ) OR ( user_id = ? AND user_type = ? )");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSuspendedUserListByLogin ", "QUERY sqlSelect=" + sqlSelect);
        }
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
            
            pstmt.setString(1, p_userId);
            pstmt.setString(2, p_userType);
            pstmt.setString(3, p_userName);
            pstmt.setString(4, p_userId);
            pstmt.setString(5, p_userName);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            ListValueVO listValueVO = null;
            userList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("login_id"), rs.getString("user_id"));
                listValueVO.setOtherInfo(rs.getString("user_name")); // user
                // name for
                // staff /
                // channel
                // user
                listValueVO.setType(rs.getString("user_type")); // adds type
                // staff/
                // channel user
                listValueVO.setOtherInfo2(rs.getString("msisdn")); // msisdn of
                // the staff
                // / channel
                // user
                listValueVO.setStatus(rs.getString("status"));
                userList.add(listValueVO);
            }
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadChildUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList.size()=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * Method for loading Users(for level one approval) List.
     * 
     * @param p_categoryCode
     *            String
     * @param p_lookupType
     *            String
     * @param p_networkCode
     *            String
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadApprovalUsersListByDomain(Connection p_con, String p_domainCode, String p_lookupType, String p_networkCode, String p_status, String p_userType) throws BTSLBaseException {
        final String methodName = "loadApprovalUsersListByDomain";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_lookupType= ");
        	msg.append(p_lookupType);
        	msg.append(", p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT U.batch_id, U.creation_type, u.user_id,u.user_name,u.network_code,");
        strBuff.append("u.login_id,u.password,u.category_code,u.parent_id,");
        strBuff.append("OUSR.user_name owner_name, PUSR.user_name parent_name,");
        strBuff.append("u.owner_id,u.allowed_ip,u.allowed_days,");
        strBuff.append("u.from_time,u.to_time,u.employee_code,u.company,u.fax,u.firstname,u.lastname,UP.phone_language,UP.country CTRY, ");// language,country,firstname,lastname,company,fax
        // added
        // by
        // deepika
        // aggarwal
        strBuff.append("u.status,u.email,u.pswd_modified_on,u.contact_no,");
        strBuff.append("u.designation,u.division,u.department,u.msisdn,u.user_type,");
        strBuff.append("u.created_by,u.created_on,u.modified_by,u.modified_on,u.address1, ");
        strBuff.append("u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix, ");
        strBuff.append("u.external_code,u.short_name,u.level1_approved_by,u.level1_approved_on,");
        strBuff.append("u.level2_approved_by,u.level2_approved_on,u.user_code,u.appointment_date,");
        strBuff.append("u.previous_status,l.lookup_name,u1.user_name parent_user_name,u2.user_name request_user_name ");
        // added for rsa
        strBuff.append(",u.RSAFLAG ");
        strBuff.append("FROM users u1,users u2,categories c,lookups l,users OUSR,users PUSR right outer join users u on (PUSR.user_id= u.parent_id), user_phones UP WHERE "); // userphones
        // added
        // by
        // deepika
        strBuff.append(" u.category_code IN (select category_code from categories where domain_code=?) ");
        strBuff.append(" AND u.category_code = c.category_code ");
        strBuff.append(" AND OUSR.user_id = u.owner_id ");
        strBuff.append(" AND u1.user_id = u.created_by ");
        strBuff.append(" AND u.user_id = UP.user_id ");// added by deepika
        // aggarwal
        strBuff.append(" AND u2.user_id = u.modified_by ");
        strBuff.append(" AND u.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND u.status in (" + p_status + ") ");
        strBuff.append(" AND u.NETWORK_CODE=? ");
        // for excluding staff users(Praveen Kumar)
        strBuff.append(" AND u.user_type=? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_domainCode);
            pstmt.setString(2, p_lookupType);
            pstmt.setString(3, p_networkCode);
            // for excluding staff users(Praveen Kumar)
            pstmt.setString(4, p_userType);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserVO userVO = null;
            while (rs.next())

            {
                userVO = new UserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setParentName(rs.getString("parent_name"));
                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("password"));
                userVO.setCategoryCode(rs.getString("category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("status"));
                userVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                userVO.setCompany(rs.getString("company"));
                userVO.setFax(rs.getString("fax"));
                userVO.setFirstName(rs.getString("firstname"));
                userVO.setLastName(rs.getString("lastname"));
                userVO.setLanguage(rs.getString("phone_language") + "_" + rs.getString("ctry"));// added
                // by
                // deepika
                // aggarwal
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setStatusDesc(rs.getString("lookup_name"));
                userVO.setCreatedByUserName(rs.getString("parent_user_name"));
                userVO.setPreviousStatus(rs.getString("previous_status"));
                userVO.setRequetedByUserName(rs.getString("request_user_name"));
                userVO.setSuspendedByUserName(rs.getString("request_user_name"));
                userVO.setRsaFlag(rs.getString("RSAFLAG"));

                list.add(userVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalUsersList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalUsersList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: usersList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Users List.
     * This method call from CSadmin viewEditUserAction
     * Use p_sessionUserID for connect by prior
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_userName
     *            String
     * @param p_userID
     *            String
     * @param p_ownerID
     *            String
     * @param p_sessionUserID
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @param p_userType
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadUsersListByUserType(Connection p_con, String p_networkCode, String p_categoryCode, String p_userName, String p_userID, String p_ownerID, String p_sessionUserID, String p_statusUsed, String p_status, String p_userType) throws BTSLBaseException {
        final String methodName = "loadUsersListByUserType";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", UserName= ");
        	msg.append(p_userName);
        	msg.append(", p_userID= ");
        	msg.append(p_userID);
        	msg.append(", p_sessionUserID= ");
        	msg.append(p_sessionUserID);        	
        	msg.append(", p_ownerID= ");
        	msg.append(p_ownerID);
        	msg.append(", p_statusUsed= ");
        	msg.append(p_statusUsed);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	msg.append(", p_userType= ");
        	msg.append(p_userType);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = userWebQry.loadUsersListByUserTypeQry(p_categoryCode, p_userName, p_userID, p_ownerID, p_sessionUserID, p_statusUsed, p_status, p_userType);
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersList", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(++i, p_status);
            }
            if (!BTSLUtil.isNullString(p_categoryCode)) {
                pstmt.setString(++i, p_categoryCode);
            }
            if (!BTSLUtil.isNullString(p_userID)) {
                pstmt.setString(++i, p_userID);
            }
            if (!BTSLUtil.isNullString(p_ownerID)) {
                pstmt.setString(++i, p_ownerID);
            }
            if (!BTSLUtil.isNullString(p_userName)) {
                // commented for DB2pstmt.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                pstmt.setString(++i, p_userName);
            }
            pstmt.setString(++i, PretupsI.USER_STATUS_TYPE);
            if (p_sessionUserID != null) {
                pstmt.setString(++i, p_sessionUserID);
                pstmt.setString(++i, p_sessionUserID);
            }
            pstmt.setString(++i, p_userType);
            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new ChannelUserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("usr_user_id"));
                userVO.setUserName(rs.getString("usr_user_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("passwd"));
                userVO.setCategoryCode(rs.getString("usr_category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("usr_status"));
                userVO.setEmail(rs.getString("email"));
                // Added by deepika aggarwal
                userVO.setCompany(rs.getString("company"));
                userVO.setFax(rs.getString("fax"));
                userVO.setFirstName(rs.getString("firstname"));
                userVO.setLastName(rs.getString("lastname"));
                // end added by deepika aggarwal
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("usr_msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setPreviousStatus(rs.getString("previous_status"));
                userVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
                final CategoryVO categoryVO = new CategoryVO();
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
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
                userVO.setCategoryVO(categoryVO);

                userVO.setParentName(rs.getString("parent_name"));
                userVO.setParentMsisdn(rs.getString("parent_msisdn"));
                userVO.setParentCategoryName(rs.getString("parent_cat"));

                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                userVO.setOwnerCategoryName(rs.getString("owner_cat"));
                userVO.setStatusDesc(rs.getString("lookup_name"));

                list.add(userVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersListByUserType]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUsersList", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUsersListByUserType]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * by santanu
     * Method :loadUserListOnZoneCategoryHierarchy
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userCategory
     *            String
     * @param p_zoneCode
     *            String
     * @param p_fromUserID
     *            String
     * @param p_userName
     *            String
     * @param p_userID
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListOnZoneCategoryHierarchy(Connection p_con, String p_userCategory, String p_zoneCode, String p_userName, String p_loginuserID, String domainCode) throws BTSLBaseException {
        final String methodName = "loadUserListOnZoneCategoryHierarchy";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_userCategory= ");
        	msg.append(p_userCategory);
        	msg.append(", p_zoneCode= ");
        	msg.append(p_zoneCode);
        	msg.append(", p_userName= ");
        	msg.append(p_userName);
        	msg.append(", p_loginuserID= ");
        	msg.append(p_loginuserID);
        	msg.append(", domainCode= ");
        	msg.append(domainCode);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        final ArrayList userList = new ArrayList();
        // commented for DB2OraclePreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        try {
            // commented for DB2 pstmtSelect =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmtSelect = userWebQry.loadUserListOnZoneCategoryHierarchyQry(p_con, p_userCategory, p_zoneCode, p_userName, p_loginuserID, domainCode);
            rs = pstmtSelect.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new ChannelUserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setLoginID(rs.getString("login_id"));
                userList.add(userVO);

            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListOnZoneCategoryHierarchy]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserReportDAO[loadUserListOnZoneDomainCategory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
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
                _log.debug(methodName, "Exiting: userList size =" + userList.size());
            }
        }
        return userList;
    }

    // added by rahuls to get the user name on the basis of user id as earler it
    // was not there.

    public String userNameFromId(Connection con, String userId) {
        final String methodName = "userNameFromId";
        String username = "";
        final String query = "SELECT USER_NAME FROM USERS WHERE USER_ID =?";
        try(PreparedStatement pstm=con.prepareStatement(query);) {
            
            pstm.setString(1, userId);
            try(ResultSet res = pstm.executeQuery();)
            {
            if (res.next()) {
                username = res.getString("USER_NAME");
            }
            }
        } catch (SQLException e) {
            _log.errorTrace(methodName, e);
        } 
        return username;
    }

    /**
     * Method to load the MIS executed upto and from dates from database and
     * putting in map (to overcome the multiple hit issue.)
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    public void loadMisExecutedDates(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String methodName = "loadMisExecutedDates";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_channelUserVO.getMsisdn() =" + p_channelUserVO.getMsisdn());
        }
        Date _misExecutedUpto = null;
        final String query = "SELECT EXECUTED_UPTO FROM PROCESS_STATUS WHERE PROCESS_ID=? ";
        
        ResultSet rs = null;
        ResultSet rs1 = null;
        try (PreparedStatement pstmt = p_con.prepareStatement(query);){
            
            pstmt.setString(1, ProcessI.C2SMIS);
           
            rs = pstmt.executeQuery();
            // For C2S MIS.
            if (rs.next()) {
                _misExecutedUpto = rs.getDate("EXECUTED_UPTO");
            }
            p_channelUserVO.setC2sMisToDate(_misExecutedUpto);
            p_channelUserVO.setC2sMisFromDate(BTSLUtil.getDifferenceDate(_misExecutedUpto, -366));
            pstmt.clearParameters();
            _misExecutedUpto = null;
            // For P2P MIS.
            pstmt.setString(1, ProcessI.P2PMIS);
          
            rs1 = pstmt.executeQuery();
            if (rs1.next()) {
                _misExecutedUpto = rs1.getDate("EXECUTED_UPTO");
            }
            p_channelUserVO.setP2pMisToDate(_misExecutedUpto);
            p_channelUserVO.setP2pMisFromDate(BTSLUtil.getDifferenceDate(_misExecutedUpto, -366));
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: ");
            }
        }
    }

    /**
     * Method to insert user remarks in User_Event_Remarks Table for
     * deleteing/suspending/resuming/Approval
     * 
     * @param p_con
     * @param p_userRemarklist
     * @return
     * @throws BTSLBaseException
     */
    public int insertEventRemark(Connection p_con, List<UserEventRemarksVO> p_userRemarklist) throws BTSLBaseException {//ArrayList p_userRemarklist
        int insertCount = 0;
        // commented for DB2OraclePreparedStatement psmtInsert = null;
        
        final String methodName = "insertEventRemark";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: userRemarklist Size= ");
        	msg.append(p_userRemarklist.size());
        	msg.append(", userRemarklist= ");
        	msg.append(p_userRemarklist);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        try {
            UserEventRemarksVO userEventRemarksVO = null;
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO USER_EVENT_REMARKS (USER_ID,EVENT_TYPE,REMARKS,CREATED_BY,CREATED_ON,MODULE,MSISDN,USER_TYPE )");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?)");
            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            if (p_userRemarklist != null) {

                // commented for DB2psmtInsert =
                // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
                try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
                {
                userEventRemarksVO = (UserEventRemarksVO) p_userRemarklist.get(0);
                psmtInsert.setString(1, userEventRemarksVO.getUserID());
                psmtInsert.setString(2, userEventRemarksVO.getEventType());
                psmtInsert.setString(3, userEventRemarksVO.getRemarks());
                psmtInsert.setString(4, userEventRemarksVO.getCreatedBy());
                psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(userEventRemarksVO.getCreatedOn()));
                psmtInsert.setString(6, userEventRemarksVO.getModule());
                psmtInsert.setString(7, userEventRemarksVO.getMsisdn());
                psmtInsert.setString(8, userEventRemarksVO.getUserType());
                insertCount = psmtInsert.executeUpdate();
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[insertEventRemark]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[insertEventRemark]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for checking Is Child User Exist for parent Association.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_fromCategory
     *            String
     * @param p_toCategory
     *            String
     * 
     * @return flag boolean
     * @exception BTSLBaseException
     * @author nilesh.kumar
     */

    public boolean isUserExistForParentAssociation(Connection p_con, String p_fromCategory, String p_toCategory) throws BTSLBaseException {
        final String methodName = "isUserExistForParentAssociation";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_fromCategory= ");
        	msg.append(p_fromCategory);
        	msg.append(", p_toCategory= ");
        	msg.append(p_toCategory);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("select 1 from users U, users UP, chnl_transfer_rules CTR ");
        strBuff.append("where U.parent_id=UP.user_id and CTR.PARENT_ASSOCIATION_ALLOWED=? ");
        strBuff.append("and U.status not in ('N','C') and CTR.status=? ");
        strBuff.append("and CTR.from_category=? and CTR.to_category=? and UP.category_code=CTR.from_category");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, PretupsI.YES);
            pstmt.setString(2, PretupsI.YES);
            pstmt.setString(3, p_fromCategory);
            pstmt.setString(4, p_toCategory);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * @param p_con
     * @param p_domainCode
     * @param p_lookupType
     * @param p_networkCode
     * @param p_status
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public ArrayList loadSTKApprovalUsersListByDomain(Connection p_con, String p_domainCode, String p_lookupType, String p_networkCode, String p_status) throws BTSLBaseException {
        final String methodName = "loadSTKApprovalUsersListByDomain";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_lookupType= ");
        	msg.append(p_lookupType);
        	msg.append(", p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
       
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT U.batch_id, U.creation_type, u.user_id,u.user_name,u.network_code,");
        strBuff.append("u.login_id,u.password,u.category_code,u.parent_id,");
        strBuff.append("OUSR.user_name owner_name, PUSR.user_name parent_name,");
        strBuff.append("u.owner_id,u.allowed_ip,u.allowed_days,");
        strBuff.append("u.from_time,u.to_time,u.employee_code,");
        strBuff.append("u.status,u.email,u.pswd_modified_on,u.contact_no,");
        strBuff.append("u.designation,u.division,u.department,u.msisdn,u.user_type,");
        strBuff.append("u.created_by,u.created_on,u.modified_by,u.modified_on,u.address1, ");
        strBuff.append("u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix, ");
        strBuff.append("u.external_code,u.short_name,u.level1_approved_by,u.level1_approved_on,");
        strBuff.append("u.level2_approved_by,u.level2_approved_on,u.user_code,u.appointment_date,");
        strBuff.append("u.previous_status,l.lookup_name,u1.user_name parent_user_name,u2.user_name request_user_name ");
        strBuff.append("FROM users u1,users u2,categories c,lookups l,users OUSR,users PUSR right outer join users u on PUSR.user_id = u.parent_id WHERE ");
        strBuff.append(" u.category_code IN (select category_code from categories where domain_code=?) ");
        strBuff.append(" AND u.category_code = c.category_code ");
        strBuff.append(" AND OUSR.user_id = u.owner_id ");
        strBuff.append(" AND u1.user_id = u.created_by ");
        strBuff.append(" AND u2.user_id = u.modified_by ");
        strBuff.append(" AND u.status = l.lookup_code ");
        strBuff.append(" AND l.lookup_type= ? ");
        strBuff.append(" AND u.status in (" + p_status + ") ");
        strBuff.append(" AND u.NETWORK_CODE=?  AND u.CREATION_TYPE=? and u.LEVEL1_APPROVED_BY is null and u.LEVEL1_APPROVED_ON is null ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_domainCode);
            pstmt.setString(2, p_lookupType);
            pstmt.setString(3, p_networkCode);
            pstmt.setString(4, PretupsI.STK_SYSTEM_USR_CREATION_TYPE);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserVO userVO = null;
            while (rs.next())

            {
                userVO = new UserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setParentName(rs.getString("parent_name"));
                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("password"));
                userVO.setCategoryCode(rs.getString("category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("status"));
                userVO.setEmail(rs.getString("email"));
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setStatusDesc(rs.getString("lookup_name"));
                userVO.setCreatedByUserName(rs.getString("parent_user_name"));
                userVO.setPreviousStatus(rs.getString("previous_status"));
                userVO.setRequetedByUserName(rs.getString("request_user_name"));
                userVO.setSuspendedByUserName(rs.getString("request_user_name"));

                list.add(userVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadSTKApprovalUsersListByDomain]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadSTKApprovalUsersListByDomain]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: usersList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * @param p_con
     * @param p_categoryCode
     * @param p_lookupType
     * @param p_sequenceNo
     * @param p_grphDomainType
     * @param p_networkCode
     * @param p_parentGrphDomainCode
     * @param p_status
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public ArrayList loadSTKApprovalUsersList(Connection p_con, String p_categoryCode, String p_lookupType, int p_sequenceNo, String p_grphDomainType, String p_networkCode, String p_parentGrphDomainCode, String p_status) throws BTSLBaseException {
        final String methodName = "loadSTKApprovalUsersList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", p_lookupType= ");
        	msg.append(p_lookupType);
        	msg.append(", p_sequenceNo= ");
        	msg.append(p_sequenceNo);
        	
        	msg.append(", p_grphDomainType= ");
        	msg.append(p_grphDomainType);
        	msg.append(", p_networkCode= ");
        	msg.append(p_networkCode);
        	msg.append(", p_parentGrphDomainCode= ");
        	msg.append(p_parentGrphDomainCode);
        	msg.append(", p_status= ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final ArrayList list = new ArrayList();
        try {
            pstmt = userWebQry.loadSTKApprovalUsersListQry(p_con, p_categoryCode, p_lookupType, p_sequenceNo, p_grphDomainType, p_networkCode, p_parentGrphDomainCode, p_status);

            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                userVO.setCreationType(rs.getString("creation_type"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setParentName(rs.getString("parent_name"));
                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("password"));
                userVO.setCategoryCode(rs.getString("category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("status"));
                userVO.setEmail(rs.getString("email"));
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified((rs.getTimestamp("modified_on").getTime()));
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setStatusDesc(rs.getString("lookup_name"));
                userVO.setCreatedByUserName(rs.getString("parent_user_name"));
                userVO.setPreviousStatus(rs.getString("previous_status"));

                userVO.setRequetedByUserName(rs.getString("request_user_name"));
                userVO.setSuspendedByUserName(rs.getString("request_user_name"));

                list.add(userVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadSTKApprovalUsersList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadSTKApprovalUsersList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: usersList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * @param p_con
     * @param p_userId
     * @return int
     * @throws BTSLBaseException
     * @author vikas.jauhari
     */

    public int deleteOptUserPhoneInfo(Connection p_con, String p_userId) throws BTSLBaseException {
        
        int deleteCount = 0;
        final String methodName = "deleteOptUserPhoneInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userId= " + p_userId);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            String deleteQuery = null;
            // Delete from the user_phones table
            strBuff.append("delete from user_phones where user_id = ?");
            deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, p_userId);
            deleteCount = psmtDelete.executeUpdate();
            psmtDelete.clearParameters();

        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deleteOptUserPhoneInfo]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[deleteOptUserPhoneInfo]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally
        return deleteCount;
    }

    /**
     * Method for Updating User Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            UserVO
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateUserForRsaAuthentication(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        UserDAO userDao = null;
        int updateCount = 0;
        final String methodName = "updateUserForRsaAuthentication";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userVO= " + p_userVO);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE users SET ssn = ?,rsaflag = ? ");
            strBuff.append(" WHERE user_id = ?");
            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            psmtUpdate = p_con.prepareStatement(updateQuery);
            psmtUpdate.setString(1, p_userVO.getSsn());
            psmtUpdate.setString(2, p_userVO.getRsaFlag());
            psmtUpdate.setString(3, p_userVO.getUserID());
            // check wehther the record already updated or not
            userDao = new UserDAO();
            final boolean modified = userDao.recordModified(p_con, p_userVO.getUserID(), p_userVO.getLastModified());
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }

            updateCount = psmtUpdate.executeUpdate();
        } // end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUserForRsaAuthentication]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[updateUserForRsaAuthentication]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

    /**
     * Date : July, 2011
     * Discription :
     * Method : loadApprovalStaffUserList
     * 
     * @param p_con
     * @param p_userType
     * @param p_userName
     * @param p_status
     * @throws BTSLBaseException
     * @return ArrayList
     */
    public ArrayList loadApprovalStaffUserList(Connection p_con, String p_userType, String p_userName, String p_status) throws BTSLBaseException {
        final String methodName = "loadApprovalStaffUserList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_userType= ");
        	msg.append(p_userType);
        	msg.append(", p_userName=");
        	msg.append(p_userName);
        	msg.append(", Status= ");
        	msg.append(p_status);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

         
        final ArrayList userList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT user_id, user_name, login_id FROM users");
        strBuff.append(" WHERE user_type = ? AND status IN (" + p_status + ") ");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadApprovalStaffUserList ", "QUERY sqlSelect=" + sqlSelect);
        }
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
           
            pstmt.setString(1, p_userType);
            pstmt.setString(2, p_userName);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("user_name"), rs.getString("user_id"));
                listValueVO.setOtherInfo(rs.getString("login_id"));
                userList.add(listValueVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalStaffUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadApprovalStaffUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList.size()=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * Description :
     * Method : loadStaffUserList
     * 
     * @param p_con
     * @param p_userId
     * @param p_userType
     * @throws BTSLBaseException
     * @return ArrayList
     */
    public ArrayList loadStaffUserList(Connection p_con, String p_userType, String p_userName) throws BTSLBaseException {
        final String methodName = "loadStaffUserList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_userType= ");
        	msg.append(p_userType);
        	msg.append(", p_userName= ");
        	msg.append(p_userName);

        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        final ArrayList userList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT user_id, user_name, login_id, status FROM users");
        strBuff.append(" WHERE user_type = ? AND status NOT IN('N','C')");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadStaffUserList ", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userType);
            pstmt.setString(2, p_userName);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            ListValueVO listValueVO = null;
            while (rs.next())

            {
                listValueVO = new ListValueVO(rs.getString("user_name"), rs.getString("user_id"));
                listValueVO.setOtherInfo(rs.getString("login_id"));
                listValueVO.setStatus(rs.getString("status"));
                userList.add(listValueVO);
            }
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadStaffUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadChildUserList", "error.general.sql.processing");
        } catch (Exception ex)

        {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadStaffUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadChildUserList", "error.general.processing");
        } finally

        {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList.size()=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * @author shashank.gaur
     *         Method for barring for deletion User Information from Users Table
     *         (update the status, set status =
     *         BD = Barred.
     *         BR = Barred Request
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            (consist of UserVO)
     * 
     * @return barCount int
     * @throws BTSLBaseException
     */
    public int barForDelUser(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        PreparedStatement pstmtBar = null;
        PreparedStatement selectBar = null;
        ResultSet rsselectBar = null;
        int barCount = 0;
        final String methodName = "barForDelUser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userVO");
        }

        try {
             boolean modified = false;
            String status=null;
            
            

            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE users SET status = ?, previous_status = ? , modified_by = ?,");
            strBuff.append(" modified_on = ?, barred_deletion_batchid=? WHERE user_id = ? and status = ? and user_type = ? and network_code = ?");

            final StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("select status from users ");
            strBuilder.append(" where user_id = ?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuilder.toString());
            }
            selectBar =p_con.prepareStatement(strBuilder.toString());
            selectBar.setString(1,p_userVO.getUserID());
            rsselectBar = selectBar.executeQuery();
            if(rsselectBar.next()){
               status=rsselectBar.getString("status");
               if(!((!status.equals(PretupsI.YES)) ||(!status.equals(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST))||(!status.equals(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE)))){
            	 
                   modified=true;
                   barCount=10;
               }else{
                   modified=false;
                   
               }
              
            }
           if(!modified){
               if (_log.isDebugEnabled()) {
                   _log.debug(methodName, " OtherQuery sqlUpdate QUERY :" + strBuff.toString());
               }
            pstmtBar = p_con.prepareStatement(strBuff.toString());

            pstmtBar.setString(1, p_userVO.getStatus());
            pstmtBar.setString(2, p_userVO.getPreviousStatus());
            pstmtBar.setString(3, p_userVO.getModifiedBy());
            pstmtBar.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));
//            pstmtBar.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));
            pstmtBar.setString(5, p_userVO.getBatchID());
            pstmtBar.setString(6, p_userVO.getUserID());
			pstmtBar.setString(7,status);
            pstmtBar.setString(8, PretupsI.CHANNEL_USER_TYPE);
            pstmtBar.setString(9, p_userVO.getNetworkID());
            barCount = pstmtBar.executeUpdate();
            pstmtBar.clearParameters();

            // check the status of the update
            if (barCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            }
           }
        } // end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[barForDelUser]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[barForDelUser]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	try {
                if (rsselectBar != null) {
                	rsselectBar.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtBar != null) {
                    pstmtBar.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (selectBar != null) {
                	selectBar.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: barCount=" + barCount);
            }
        } // end of finally

        return barCount;
    }

    /**
     * @author shashank.gaur
     *         Method for checking the limit of barred users per day
     *         (select count(*) from users where status =
     *         BD=barred
     * 
     * @param p_con
     *            java.sql.Connection
     * 
     * @return boolean
     * @throws BTSLBaseException
     */
    public int checkBarLimit(Connection p_con) throws BTSLBaseException {
        int limit = 0;
      
        final String methodName = "checkBarLimit";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        final Date currentDate = new Date();
        try {

            final String strBuff = userWebQry.checkBarLimitQry();
            try(PreparedStatement pstmtBar = p_con.prepareStatement(strBuff);)
            {
            pstmtBar.setDate(1, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            pstmtBar.setString(2, PretupsI.BARRED_REQUEST_EVENT);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuff);
            }

            try(ResultSet rs = pstmtBar.executeQuery();)
            {
            while (rs.next()) {
                limit = rs.getInt("LIMIT");
            }

            limit = Integer.parseInt(Constants.getProperty("PER_DAY_LIMIT_BAR_FOR_DEL")) - limit;

            return limit;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkBarLimit]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkBarLimit]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        } // end of finally
    }

    /**
     * @author shashank.gaur
     *         Method for editing the roles of barred users
     *         (delete from user_roles)
     *         (insert into user_roles)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            (consist of UserVO)
     * 
     * @throws BTSLBaseException
     */
    public void editRoles(Connection p_con, String p_userID) throws BTSLBaseException {
        PreparedStatement pstmtBar = null;
        PreparedStatement pstmtBarDel = null;

        String roles = null;
        final String methodName = "editRoles";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

        try {
            roles = Constants.getProperty("ROLES_FOR_BAR_USERS");
            final String[] role = roles.split(",");
            final StringBuilder strBuff = new StringBuilder();
            final StringBuilder strBuffBar = new StringBuilder();
            strBuff.append("DELETE FROM user_roles where user_id= ? ");

            strBuffBar.append("INSERT INTO user_roles (user_id,role_code,gateway_types) VALUES (?,?,?)");

            try{
            	pstmtBar = p_con.prepareStatement(strBuff.toString());
            	pstmtBar.setString(1, p_userID);
            	pstmtBar.executeQuery();

            try{
            	pstmtBarDel = p_con.prepareStatement(strBuffBar.toString());
	            for (final String count : role) {
	                pstmtBarDel.setString(1, p_userID);
	                pstmtBarDel.setString(2, count);
	                pstmtBarDel.setString(3, PretupsI.DEFAULT_GATEWAY_FOR_BAR);
	                pstmtBarDel.addBatch();
	            }
	            pstmtBarDel.executeBatch();
	
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuff.toString());
	            }
	
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuffBar.toString());
	            }


            }
            catch (SQLException sqle) {
                _log.error(methodName, "SQLException: " + sqle.getMessage());
                _log.errorTrace(methodName, sqle);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[editRoles]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            catch (Exception e) {
                _log.error(methodName, "Exception: " + e.getMessage());
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[editRoles]", "", "", "", "Exception:" + e
                    .getMessage());
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            }
           
            }
            catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
        }// end of try
        catch (Exception e) {
			
        	_log.errorTrace(methodName, e);
		}
        
        finally {
        	try {
				pstmtBar.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
            
            try {
				pstmtBarDel.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        } // end of finally
    }

    /**
     * @author shashank.gaur
     *         Method for processing batch users
     *         (update status from users)
     * 
     * @param con
     *            java.sql.Connection
     * @param map
     *            (consist of row number as key and user credential as values)
     * @param flag
     * @param rows
     * @return userID_List
     * @throws BTSLBaseException
     */

    public HashMap loadUserForBarring(Connection con, HashMap map, int rows, boolean flag) throws BTSLBaseException {
        PreparedStatement pstmtBar = null;
        ResultSet rs = null;
        final HashMap userID_List = new HashMap();
        final String methodName = "loadUserForBarring";
        try {
            for (int i = 1; i < rows; i++) {
                int j = 1;
                if (map.containsKey(i)) {
                    final HashMap val = (HashMap) map.get(i);
                    final StringBuilder strBuff = new StringBuilder();
                    strBuff.append("SELECT U.category_code, U.external_code, U.login_id,");
                    strBuff.append(" U.modified_by, U.modified_on, U.network_code, U.status,");
                    strBuff.append(" U.user_id, U.user_name, U.user_type, C.category_type, C.restricted_msisdns,");
                    strBuff.append(" UP.msisdn");
                    strBuff.append(" FROM users U left join user_phones UP on (U.msisdn=UP.msisdn AND UP.primary_number='Y'), categories C WHERE u.USER_TYPE='CHANNEL' and ");

                    if (!BTSLUtil.isNullString((String) val.get("loginId"))) {
                        strBuff.append("U.login_id=?");
                    }
                    if (!BTSLUtil.isNullString((String) val.get("loginId")) && (!BTSLUtil.isNullString((String) val.get("msisdn")) || !BTSLUtil.isNullString((String) val
                        .get("extcode")))) {
                        strBuff.append(" and ");
                    }
                    if (!BTSLUtil.isNullString((String) val.get("msisdn"))) {
                        strBuff.append("U.msisdn=?");
                    }
                    if (!BTSLUtil.isNullString((String) val.get("msisdn")) && !BTSLUtil.isNullString((String) val.get("extcode"))) {
                        strBuff.append(" and ");
                    }
                    if (!BTSLUtil.isNullString((String) val.get("extcode"))) {
                        strBuff.append("U.external_code=?");
                    }
                    strBuff.append(" AND U.category_code=C.category_code");
                    if(flag)
                    	strBuff.append(" AND  U.status NOT IN (?,?,?,?,?,?,?,?)");
                    else
                    	strBuff.append(" AND  U.status NOT IN (?,?,?,?,?)");
                   
                    pstmtBar = con.prepareStatement(strBuff.toString());
                    LogFactory.printLog(methodName, strBuff.toString(), _log);
                    
                    if (!BTSLUtil.isNullString((String) val.get("loginId"))) {
                        pstmtBar.setString(j++, (String) val.get("loginId"));
                    }
                    
                    if (!BTSLUtil.isNullString((String) val.get("msisdn"))) {
                        pstmtBar.setString(j++, (String) val.get("msisdn"));
                    }
                    
                    if (!BTSLUtil.isNullString((String) val.get("extcode"))) {
                        pstmtBar.setString(j++, (String) val.get("extcode"));
                    }
                    if(flag){
	                    pstmtBar.setString(j++,PretupsI.USER_STATUS_DELETED);
	                    pstmtBar.setString(j++,PretupsI.USER_STATUS_CANCELED);
	                    pstmtBar.setString(j++,PretupsI.USER_STATUS_DELETE_REQUEST);
	                    pstmtBar.setString(j++,PretupsI.USER_STATUS_NEW);
	                    pstmtBar.setString(j++,PretupsI.USER_STATUS_SUSPEND_REQUEST);
	                    pstmtBar.setString(j++,PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST);
	                    pstmtBar.setString(j++,PretupsI.USER_STATUS_BARRED);
	                    pstmtBar.setString(j,PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE);
                    }
                    else{
                    	pstmtBar.setString(j++,PretupsI.USER_STATUS_DELETED);
                        pstmtBar.setString(j++,PretupsI.USER_STATUS_CANCELED);
                        pstmtBar.setString(j++,PretupsI.USER_STATUS_DELETE_REQUEST);
                        pstmtBar.setString(j++,PretupsI.USER_STATUS_NEW);
                        pstmtBar.setString(j++,PretupsI.USER_STATUS_SUSPEND_REQUEST);
                    }
                    
                    rs = pstmtBar.executeQuery();
                    while (rs.next()) {
                        final UserVO userVO = new UserVO();
                        final CategoryVO categoryVO = new CategoryVO();
                        userVO.setUserID(rs.getString("user_id"));
                        userVO.setStatus(rs.getString("status"));
                        userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                        userVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                        userVO.setCategoryCode(rs.getString("category_code"));
                        userVO.setExternalCode(rs.getString("external_code"));
                        userVO.setLoginID(rs.getString("login_id"));
                        userVO.setModifiedBy(rs.getString("modified_by"));
                        userVO.setNetworkID(rs.getString("network_code"));
                        userVO.setUserName(rs.getString("user_name"));
                        userVO.setUserType(rs.getString("user_type"));
                        userVO.setRemarks((String) val.get("remarks"));
                        categoryVO.setCategoryType(rs.getString("user_id"));
                        categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                        userVO.setMsisdn(rs.getString("msisdn"));
                        userVO.setCategoryVO(categoryVO);
                        userID_List.put(i, userVO);
                    }
                }
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserForBarring]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserForBarring]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtBar != null) {
                    pstmtBar.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        } // end of finally
        return userID_List;
    }

    public int addUserStatusDetails(Connection con, UserStatusVO userStatusVO) throws BTSLBaseException {

        int addCount = 0;
        
        final String methodName = "addUserStatusDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlInsert=" + userStatusVO);
        }
        try {

            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT into USER_ALLOWED_STATUS(GATEWAY_ALLOWED,USER_TYPE,CATEGORY_CODE,USER_SENDER_ALLOWED,");
            strBuff.append("USER_SENDER_SUSPENDED,USER_SENDER_DENIED,");
            strBuff.append("USER_RECEIVER_ALLOWED,USER_RECEIVER_SUSPENDED,USER_RECEIVER_DENIED,");
            strBuff.append("NETWORK_CODE,WEB_LOGIN_ALLOWED,WEB_LOGIN_DENIED)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            try(PreparedStatement pstmtInsert = con.prepareStatement(insertQuery);)
            {
            pstmtInsert.setString(1, userStatusVO.getGatewayType());
            pstmtInsert.setString(2, userStatusVO.getUserType());
            pstmtInsert.setString(3, userStatusVO.getCategoryCode());
            pstmtInsert.setString(4, userStatusVO.getUserSenderAllowed());
            pstmtInsert.setString(5, userStatusVO.getUserSenderSuspended());
            pstmtInsert.setString(6, userStatusVO.getUserSenderDenied());
            pstmtInsert.setString(7, userStatusVO.getUserReceiverAllowed());
            pstmtInsert.setString(8, userStatusVO.getUserReceiverSuspended());
            pstmtInsert.setString(9, userStatusVO.getUserReceiverDenied());
            pstmtInsert.setString(10, userStatusVO.getNetworkCode());
            pstmtInsert.setString(11, userStatusVO.getWebLoginAllowed());
            pstmtInsert.setString(12, userStatusVO.getWebLoginDenied());
            addCount = pstmtInsert.executeUpdate();

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserStatusDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserStatusDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (con != null) {
                    con.commit();
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userVO=" + userStatusVO);
            }
        }
        return addCount;
    }

    public int modifyUserStatusDetails(Connection con, UserStatusVO userStatusVO) throws BTSLBaseException {

        final ResultSet rs = null;
        int updateCount = 0;
        final String methodName = "modifyUserStatusDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate");
        }
        try {

            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE USER_ALLOWED_STATUS SET USER_SENDER_ALLOWED=?,USER_SENDER_SUSPENDED=?,");
            strBuff.append("USER_SENDER_DENIED=?,USER_RECEIVER_ALLOWED=?,");
            strBuff.append("USER_RECEIVER_SUSPENDED=?,USER_RECEIVER_DENIED=?,WEB_LOGIN_ALLOWED=?,WEB_LOGIN_DENIED=?");
            strBuff.append(" WHERE GATEWAY_ALLOWED=? AND USER_TYPE=?");
            strBuff.append(" AND CATEGORY_CODE=? AND NETWORK_CODE=?");
            final String updateQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            try(PreparedStatement pstmtModify = con.prepareStatement(updateQuery);)
            {
            pstmtModify.setString(1, userStatusVO.getUserSenderAllowed());
            pstmtModify.setString(2, userStatusVO.getUserSenderSuspended());
            pstmtModify.setString(3, userStatusVO.getUserSenderDenied());
            pstmtModify.setString(4, userStatusVO.getUserReceiverAllowed());
            pstmtModify.setString(5, userStatusVO.getUserReceiverSuspended());
            pstmtModify.setString(6, userStatusVO.getUserReceiverDenied());
            pstmtModify.setString(7, userStatusVO.getWebLoginAllowed());
            pstmtModify.setString(8, userStatusVO.getWebLoginDenied());
            pstmtModify.setString(9, userStatusVO.getGatewayType());
            pstmtModify.setString(10, userStatusVO.getUserType());
            pstmtModify.setString(11, userStatusVO.getCategoryCode());
            pstmtModify.setString(12, userStatusVO.getNetworkCode());
            updateCount = pstmtModify.executeUpdate();
        }
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            
            try {
                if (con != null) {
                    con.commit();
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userVO=" + userStatusVO);
            }
        }
        return updateCount;
    }

    public int changeUserStatus(Connection con, UserVO userVO) throws BTSLBaseException {

        
        final ResultSet rs = null;
        int updateCount = 0;
        final String methodName = "changeUserStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate");
        }
        try {

            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE USERS SET STATUS=?, previous_status=? ,REMARKS=?");
            strBuff.append(" WHERE MSISDN=? ");
            final String updateQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            try(PreparedStatement pstmtModify = con.prepareStatement(updateQuery);)
            {
            pstmtModify.setString(1, userVO.getStatus());
            pstmtModify.setString(2, userVO.getPreviousStatus());
            pstmtModify.setString(3, userVO.getRemarks());
            pstmtModify.setString(4, userVO.getMsisdn());
            updateCount = pstmtModify.executeUpdate();
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            
            try {
                if (con != null) {
                    con.commit();
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userVO=" + userVO);
            }
        }
        return updateCount;
    }

    // //////

    public int changeUserStatusForBatch(Connection con, List<UserVO> list) throws BTSLBaseException {

        
        final ResultSet rs = null;
        int updateCount = 0;
        UserVO userVO = null;
        final String methodName = "changeUserStatusForBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate");
        }
        try {

            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE USERS SET STATUS=?,previous_status=?,REMARKS=? ");
            strBuff.append(" WHERE MSISDN=? ");
            final String updateQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            try(PreparedStatement pstmtModify = con.prepareStatement(updateQuery);)
            {
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                pstmtModify.clearParameters();
                userVO = new UserVO();
                userVO = (list.get(i));
                pstmtModify.setString(1, userVO.getStatus());
                pstmtModify.setString(2, userVO.getPreviousStatus());
                pstmtModify.setString(3, userVO.getRemarks());
                pstmtModify.setString(4, userVO.getMsisdn());
                updateCount += pstmtModify.executeUpdate();
            }
        }
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            
            try {
                if (con != null) {
                    con.commit();
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userVO=" + userVO);
            }
        }
        return updateCount;
    }


    /********************************** CHANNEL USER RELATED METHODS *******************************/

    /**
     * Method for loading Root level User List.
     * 
     * e.g suppose BCU add Retailer, we need to load the list of all users
     * reside at root level (where sequence no = 1) of that category
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentGraphDomainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadOwnerUserListForUserTransfer(Connection p_con, String p_parentGraphDomainCode, String p_username, String p_domainCode, String p_statusUsed, String p_status,String p_loggedinUserID) throws BTSLBaseException {
        final String methodName = "loadOwnerUserListForUserTransfer";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_parentGraphDomainCode= ");
        	msg.append(p_parentGraphDomainCode);
        	msg.append(", p_username= ");
        	msg.append(p_username);
        	msg.append(", p_domainCode= ");
        	msg.append(p_domainCode);
        	msg.append(", p_stausUsed= ");
        	msg.append(p_statusUsed);        	
        	msg.append(", p_staus= ");
        	msg.append(p_status);
        	msg.append(", p_loggedinUserID= ");
        	msg.append(p_loggedinUserID);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // commented for DB2OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String sqlSelect = userWebQry.loadOwnerUserListForUserTransferQry(p_statusUsed, p_status);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            // commented for DB2pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 1;
            // commented for DB2pstmt.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmt.setString(i++, p_username);
            if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmt.setString(i++, p_status);
            }
            pstmt.setString(i++, p_loggedinUserID);
            pstmt.setString(i++, p_domainCode);
            pstmt.setString(i++, p_parentGraphDomainCode);
            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setLoginID(rs.getString("login_id"));
                list.add(userVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserWebDAO[loadOwnerUserListForUserTransfer]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserWebDAO[loadOwnerUserListForUserTransfer]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }
    
 public int deleteUserVoucherTypes(Connection con, String userID) throws BTSLBaseException {

        final ResultSet rs = null;
        int deleteCount = 0;
        UserVO userVO = null;
        final String methodName = "deleteUserVoucherTypes";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqldelete");
        }
        try {
        	// delete from USER_VOUCHERTYPES table
            StringBuilder strBuff = new StringBuilder("delete from USER_VOUCHERTYPES where user_id = ?");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            PreparedStatement psmtDelete5 = con.prepareStatement(deleteQuery);
            psmtDelete5.setString(1, userID);
            deleteCount = psmtDelete5.executeUpdate();
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[modifyUserStatusDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            
            try {
            	if (rs != null) {
            		rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userVO=" + userVO);
            }
        }
        return deleteCount;
    }
 
 
 
 /*
  * Added by Shaina
  * Method for loading Commision Profile for a particular category and
  * geography
  * 
  * @param p_con java.sql.Connection
  * 
  * @param p_categoryCode String
  * 
  * @param p_networkCode String
  * 
  * @param p_graphDomainTypeName
  * 
  * @return java.util.ArrayList
  * 
  * @throws BTSLBaseException
  */
 public ArrayList loadCommisionProfileListByGradeGeography(Connection p_con, String p_categoryCode, String p_networkCode, String p_graphDomainName,String userGrade) throws BTSLBaseException {
     flag = true;
     final String methodName = "loadCommisionProfileListByGradeGeography";
     if (_log.isDebugEnabled()) {
     	StringBuffer msg=new StringBuffer("");
     	msg.append("Entered p_categoryCode= ");
     	msg.append(p_categoryCode);
     	msg.append(", p_networkCode= ");
     	msg.append(p_networkCode);
     	msg.append(", p_graphDomainName= ");
     	msg.append(p_graphDomainName);
     	
     	String message=msg.toString();
         _log.debug(methodName, message);
     }
 
     final StringBuilder strBuff = new StringBuilder();
     strBuff.append(" SELECT comm_profile_set_id, comm_profile_set_name,grade_code,status");
     strBuff.append(" FROM commission_profile_set WHERE category_code = ? ");
     if (BTSLUtil.isNullString(p_graphDomainName)) {
         strBuff.append(" AND network_code = ? AND status != 'N' ");
     } else {
         strBuff.append(" AND network_code = ? AND status != 'N' AND (geography_code = ? OR geography_code = 'ALL')");
     }
     
     if (!BTSLUtil.isNullString(userGrade)) {
         strBuff.append(" AND (grade_code = ? OR grade_code = 'ALL')");
     }
     final String sqlSelect = strBuff.toString();
     if (_log.isDebugEnabled()) {
         _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
     }
     final ArrayList list = new ArrayList();
     try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {

         pstmt.setString(1, p_categoryCode);
         pstmt.setString(2, p_networkCode);
         if (!BTSLUtil.isNullString(p_graphDomainName)) {
             pstmt.setString(3, p_graphDomainName);
         }
         if (!BTSLUtil.isNullString(userGrade)) {
        	   pstmt.setString(4, userGrade);
         }
         try(ResultSet rs = pstmt.executeQuery();)
         {
         CommissionProfileSetVO commProfSetVO = null;
         while (rs.next()) {
             commProfSetVO = new CommissionProfileSetVO();
             commProfSetVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
             if(rs.getString("status").equalsIgnoreCase(PretupsI.COMMISSION_PROFILE_STATUS_SUSPEND)){
					commProfSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name")+BTSLUtil.NullToString(Constants.getProperty("COMM_PROFILE_SUSPENDED")));
				}
				else
					commProfSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name"));
            
             commProfSetVO.setGradeCode(rs.getString("grade_code"));
             list.add(commProfSetVO);
         }

     } 
     }catch (SQLException sqe) {
         _log.error(methodName, "SQLException : " + sqe);
         _log.errorTrace(methodName, sqe);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCommisionProfileListByGradeGeography]", "",
             "", "", "SQL Exception:" + sqe.getMessage());
         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
     } catch (Exception ex) {
         _log.error(methodName, "Exception : " + ex);
         _log.errorTrace(methodName, ex);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCommisionProfileListByGradeGeography]", "",
             "", "", "Exception:" + ex.getMessage());
         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
     } finally {
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Exiting: loadCommisionProfileListByGradeGeography size=" + list.size());
         }
     }
     return list;
 }
    

}
