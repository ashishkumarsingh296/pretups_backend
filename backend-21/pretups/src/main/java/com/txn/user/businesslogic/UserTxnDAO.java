package com.txn.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class UserTxnDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * @param p_con
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public ArrayList loadCategoryServices(Connection p_con, String p_categoryCode, String p_networkCode) throws BTSLBaseException {

        final String methodName = "loadCategoryServices";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;

        String sqlSelect = "select SERVICE_TYPE from CATEGORY_SERVICE_TYPE where CATEGORY_CODE=? and network_code=?";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);
            rs = pstmt.executeQuery();
            list = new ArrayList();
            while (rs.next()) {
                list.add(rs.getString("SERVICE_TYPE"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCategoryServices]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadCategoryServices]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: servicesList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * @param p_con
     * @param p_servicesList
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public int addChannelUserServices(Connection p_con, ArrayList p_servicesList, String p_userID) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addChannelUserServices";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_servicesList Size= ");
        	loggerValue.append(p_servicesList.size());
        	loggerValue.append("p_userID=");
        	loggerValue.append(p_userID);
            _log.debug(methodName, loggerValue);
        }
        try {
            if ((p_servicesList != null)) {
                String insertQuery = " insert into user_services (user_id,service_type,status) values (?,?,?)";
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query sqlInsert:" + insertQuery);
                }

                psmtInsert = p_con.prepareStatement(insertQuery);

                for (int i = 0, j = p_servicesList.size(); i < j; i++) {
                    psmtInsert.setString(1, p_userID);
                    psmtInsert.setString(2, (String) p_servicesList.get(i));
                    psmtInsert.setString(3, PretupsI.YES);

                    insertCount = psmtInsert.executeUpdate();

                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addChannelUserServices]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addChannelUserServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting User craeted through external API.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            UserVO
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addUserForExtApi(Connection p_con, UserVO p_userVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addUserForExtApi";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userVO= ");
        	loggerValue.append(p_userVO);
            _log.debug(methodName, loggerValue);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO users (user_id,user_name,network_code,");
            strBuff.append("login_id,password,category_code,parent_id,");
            strBuff.append("owner_id,allowed_ip,allowed_days,");
            strBuff.append("from_time,to_time,employee_code,");
            strBuff.append("status,email,contact_no,");
            strBuff.append("designation,division,department,msisdn,user_type,");
            strBuff.append("created_by,created_on,modified_by,modified_on,address1, ");
            strBuff.append("address2,city,state,country,ssn,user_name_prefix, ");
            strBuff.append("external_code,short_name,user_code,appointment_date,previous_status,pswd_reset,rsaflag,invalid_password_count,creation_type) ");

            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            int i = 1;
            ;

            psmtInsert.setString(i, p_userVO.getUserID());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getUserName());
            psmtInsert.setString(++i, p_userVO.getNetworkID());
            psmtInsert.setString(++i, p_userVO.getLoginID());
            psmtInsert.setString(++i, p_userVO.getPassword());
            psmtInsert.setString(++i, p_userVO.getCategoryCode());
            psmtInsert.setString(++i, p_userVO.getParentID());
            psmtInsert.setString(++i, p_userVO.getOwnerID());
            psmtInsert.setString(++i, p_userVO.getAllowedIps());
            psmtInsert.setString(++i, p_userVO.getAllowedDays());
            psmtInsert.setString(++i, p_userVO.getFromTime());
            psmtInsert.setString(++i, p_userVO.getToTime());
            psmtInsert.setString(++i, p_userVO.getEmpCode());
            psmtInsert.setString(++i, p_userVO.getStatus());
            psmtInsert.setString(++i, p_userVO.getEmail());
            psmtInsert.setString(++i, p_userVO.getContactNo());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getDesignation());
            psmtInsert.setString(++i, p_userVO.getDivisionCode());
            psmtInsert.setString(++i, p_userVO.getDepartmentCode());
            psmtInsert.setString(++i, p_userVO.getMsisdn());
            psmtInsert.setString(++i, p_userVO.getUserType());
            psmtInsert.setString(++i, p_userVO.getCreatedBy());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_userVO.getCreatedOn()));
            psmtInsert.setString(++i, p_userVO.getModifiedBy());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_userVO.getModifiedOn()));
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getAddress1());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getAddress2());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getCity());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getState());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getCountry());
            psmtInsert.setString(++i, p_userVO.getSsn());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getUserNamePrefix());
            psmtInsert.setString(++i, p_userVO.getExternalCode());
            // psmtInsert.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(++i, p_userVO.getShortName());
            psmtInsert.setString(++i, p_userVO.getUserCode());

            if (p_userVO.getAppointmentDate() != null) {
                psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_userVO.getAppointmentDate()));
            } else {
                psmtInsert.setTimestamp(++i, null);
            }
            psmtInsert.setString(++i, p_userVO.getPreviousStatus());
            psmtInsert.setString(++i, PretupsI.YES);
            psmtInsert.setString(++i, p_userVO.getRsaFlag());
            psmtInsert.setInt(++i, p_userVO.getInvalidPasswordCount());
            psmtInsert.setString(++i, p_userVO.getCreationType());

            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserForExtApi]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addUserForExtApi]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method is used to check the uniqueness for
     *         loginID,msisdn and external code
     *         Method : verifyUniqueDetails
     * @param p_loginID
     * @param p_msisdn
     * @param p_extCode
     * @throws BTSLBaseException
     * @return
     */

    public ChannelUserVO verifyUniqueDetails(Connection p_con, String p_loginID, String p_msisdn, String p_extCode) throws BTSLBaseException {
        final String methodName = "verifyUniqueDetails";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered : p_loginID= ");
        	loggerValue.append(p_loginID);
        	loggerValue.append(" p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(" p_extCode=");
        	loggerValue.append(p_extCode);
            _log.debug(methodName, loggerValue);
        }

        ChannelUserVO channelUserVO = new ChannelUserVO();
        String sqlSelect = null;
        PreparedStatement pstmtSelect = null;

        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer("SELECT u.login_id,u.msisdn,u.external_code FROM users u ");
        strBuff.append("WHERE u.login_id=? OR u.msisdn=? OR u.external_code=? ");
        sqlSelect = strBuff.toString();

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, p_loginID);
            pstmtSelect.setString(++i, p_msisdn);
            pstmtSelect.setString(++i, p_extCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException : " + sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[verifyUniqueDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        } catch (Exception ex) {
            _log.error("loadUserDefaultConfigCache", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDefaultConfigCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserDefaultConfigCache", PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
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
                _log.debug("loadUserDefaultConfigCache", "Exiting: channelUserVO=" + channelUserVO);
            }
        }

        return channelUserVO;

    }

    /**
     * Method loadUsersDetails.
     * This method is used to load all the information used to display Operator
     * User view
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public UserVO loadUsersDetailsByLoginId(Connection p_con, String p_loginId) throws BTSLBaseException {
    	 final String METHOD_NAME = "loadUsersDetailsByLoginId";
    	if (_log.isDebugEnabled()) {
    		StringBuilder loggerValue= new StringBuilder();
    		loggerValue.setLength(0);
    		loggerValue.append("Entered login= ");
    		loggerValue.append(p_loginId);
            _log.debug(METHOD_NAME, loggerValue);
        }
       
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        UserVO userVO = null;
        StringBuilder strBuff = new StringBuilder();
       
        UserTxnQry userTxnQry=(UserTxnQry)ObjectProducer.getObject(QueryConstants.USERTXN_QRY, QueryConstants.QUERY_PRODUCER);
        strBuff= userTxnQry.loadUsersDetailsByLoginId();
        
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersDetailsByLoginId", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_loginId);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                userVO = new ChannelUserVO();
                userVO.setUserID(rs.getString("usr_user_id"));
                userVO.setUserName(rs.getString("usr_user_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("password1"));
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
                userVO.setAuthTypeAllowed(rs.getString("AUTHENTICATION_ALLOWED"));
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

                CategoryVO categoryVO = new CategoryVO();
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

                userVO.setCategoryVO(categoryVO);

                userVO.setParentName(rs.getString("parent_name"));
                userVO.setParentMsisdn(rs.getString("parent_msisdn"));
                userVO.setParentCategoryName(rs.getString("parent_cat"));

                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                userVO.setOwnerCategoryName(rs.getString("owner_cat"));

            }
        } catch (SQLException sqe) {
            _log.error("loadUsersDetailsByLoginId", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetailsByLoginId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersDetailsByLoginId", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUsersDetailsByLoginId", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetailsByLoginId]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersDetailsByLoginId", "error.general.processing");
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
                _log.debug("loadUsersDetailsByLoginId", "Exiting: userVO=" + userVO);
            }
        }
        return userVO;
    }

}
