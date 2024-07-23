package com.web.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class ChannelUserTransferWebDAO {

    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private static Log _log = LogFactory.getLog(ChannelUserTransferWebDAO.class.getName());

    /**
     * Constructor for ChannelUserTransferDAO.
     */
    public ChannelUserTransferWebDAO() {
        super();
    }

    /**
     * by manoj
     * Method :loadChannelUserList
     * this method load list of child user's user_id from users table behalf of
     * parentID
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentID
     *            java.lang.String
     * @param p_userCategory
     *            String
     * @param p_userName
     *            String (modified by sandeeo goel)
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadChannelUserList(Connection p_con, String p_parentID, String p_userCategory, String p_userName, String p_status) throws BTSLBaseException {
        final String methodName = "loadChannelUserList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_parentID=" + p_parentID + ",p_userCategory=" + p_userCategory + ",p_userName=" + p_userName + ",p_status=" + p_status);
        }
        final ArrayList userList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        Timestamp chkmodifiedon = null;
        ResultSet rs = null;
        ChannelUserTransferVO channelUserTransferVO = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT U.user_id,U.user_name,U.msisdn,CAT.category_code,U.modified_on,CAT.category_name ");
        strBuff.append("FROM users U,categories CAT ");
        strBuff.append("WHERE (U.parent_id=? OR U.user_id=? )AND U.category_code=? AND U.category_code=CAT.category_code AND U.user_type='CHANNEL' ");
        if (!p_status.equals(PretupsI.ALL)) {
            strBuff.append("AND U.status = ? ");
        } else {
            strBuff.append("AND U.status IN (" + PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_NEW + "')");
        }
        if (p_userName != null) {
            strBuff.append("AND UPPER(U.user_name) LIKE UPPER(?) ");
        }
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_parentID);
            pstmtSelect.setString(i++, p_parentID);
            pstmtSelect.setString(i++, p_userCategory);
            if (!p_status.equals(PretupsI.ALL)) {
                pstmtSelect.setString(i++, p_status);
            }
            if (p_userName != null) {
                pstmtSelect.setString(i++, p_userName);
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelUserTransferVO = new ChannelUserTransferVO();
                channelUserTransferVO.setUserName(rs.getString("user_name"));
                channelUserTransferVO.setUserID(rs.getString("user_id"));
                channelUserTransferVO.setUserCategoryCode(rs.getString("category_code"));
                channelUserTransferVO.setUserCategoryDesc(rs.getString("category_name"));
                channelUserTransferVO.setMsisdn(rs.getString("msisdn"));
                chkmodifiedon = rs.getTimestamp("modified_on");
                if (chkmodifiedon != null) {
                    channelUserTransferVO.setLastModifiedTime(chkmodifiedon.getTime());
                }

                userList.add(channelUserTransferVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[loadChannelUserList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[loadChannelUserList]", "", "",
                "", "Exception:" + ex.getMessage());
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

    /**
     * by manoj
     * Method :loadTransferCategoryList
     * this method load list of transfer category's child category's detail
     * behalf of domaincode
     * and transfer categoryCode
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @param p_sequenceNo
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferCategoryList(Connection p_con, String p_domainCode, int p_sequenceNo) throws BTSLBaseException {

        final String methodName = "loadTransferCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_domainCode + ",p_sequenceNo=" + p_sequenceNo);
        }
        final ArrayList categoryList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CategoryVO categoryVO = null;
        final StringBuffer strBuff = new StringBuffer("SELECT sequence_no,category_code,category_name,hierarchy_allowed,agent_allowed ");
        strBuff.append("FROM categories ");
        strBuff.append("WHERE domain_code=? AND sequence_no >=? AND (hierarchy_allowed<>'N' OR agent_allowed<>'N' OR sequence_no =?)");
        strBuff.append("ORDER BY sequence_no ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadChannelUserList", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            pstmtSelect.setInt(2, p_sequenceNo);
            pstmtSelect.setInt(3, p_sequenceNo);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                //final String hierarchyAllowed = rs.getString("hierarchy_allowed");
                //final String agentAllowed = rs.getString("agent_allowed");
                categoryVO = new CategoryVO();
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryList.add(categoryVO);
               /* if ("N".equals(hierarchyAllowed) && "N".equals(agentAllowed)) {
                    
                     * do nothing means there is no user under the selected
                     * category
                     
                } else {
                    do {
                        categoryVO = new CategoryVO();
                        categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                        categoryVO.setCategoryCode(rs.getString("category_code"));
                        categoryVO.setCategoryName(rs.getString("category_name"));
                        categoryList.add(categoryVO);
                    } while (rs.next());
                }*/
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[loadTransferCategoryList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[loadTransferCategoryList]", "",
                "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: categoryList size =" + categoryList.size());
            }
        }
        return categoryList;
    }

    /**
     * Method transferChannelUser.
     * 
     * @param p_con
     *            Connection
     * @param p_channelUserTransferVO
     *            ChannelUserTransferVO
     * @return int
     * @throws BTSLBaseException
     */
    public int transferChannelUser(Connection p_con, ChannelUserTransferVO p_channelUserTransferVO) throws BTSLBaseException {
        final String methodName = "transferChannelUser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_channelUserTransferVO=" + p_channelUserTransferVO);
        }
        int addCount = 0;
        final ArrayList userList = p_channelUserTransferVO.getUserHierarchyList();

        PreparedStatement pstmtUpdateUser = null;
        // commented for DB2 OraclePreparedStatement pstmtInsertUser= null;
        PreparedStatement pstmtInsertUser = null;
        PreparedStatement pstmtSelectUserBalance = null;
        ResultSet rsSelectUserBalance = null;
        PreparedStatement pstmtUpdateUserBalance = null;
        PreparedStatement pstmtInsertUserBalance = null;

        /*
         * PreparedStatement pstmtSelectUserDailyBalances= null;
         * ResultSet rsSelectUserDailyBalances = null;
         * PreparedStatement pstmtInsertUserDailyBalances= null;
         */
        PreparedStatement pstmtSelectUserDomains = null;
        ResultSet rsSelectUserDomains = null;
        PreparedStatement pstmtInsertUserDomains = null;

        PreparedStatement pstmtSelectUserGeographies = null;
        ResultSet rsSelectUserGeographies = null;
        PreparedStatement pstmtInsertUserGeographies = null;

        PreparedStatement pstmtSelectUserPhones = null;
        ResultSet rsSelectUserPhones = null;
        PreparedStatement pstmtInsertUserPhones = null;

        PreparedStatement pstmtSelectUserProductTypes = null;
        ResultSet rsSelectUserProductTypes = null;
        PreparedStatement pstmtInsertUserProductTypes = null;

        PreparedStatement pstmtSelectUserRoles = null;
        ResultSet rsSelectUserRoles = null;
        PreparedStatement pstmtInsertUserRoles = null;

        PreparedStatement pstmtSelectUserServices = null;
        ResultSet rsSelectUserServices = null;
        PreparedStatement pstmtInsertUserServices = null;

        PreparedStatement pstmtSelectUserTransferCounts = null;
        ResultSet rsSelectUserTransferCounts = null;
        PreparedStatement pstmtInsertUserTransferCounts = null;

        PreparedStatement pstmtSelectChannelUserInfo = null;
        ResultSet rsSelectChannelUserInfo = null;
        // commented for DB2 OraclePreparedStatement pstmtInsertChannelUserInfo=
        // null;
        PreparedStatement pstmtInsertChannelUserInfo = null;
        // added by manisha
        PreparedStatement pstmtSelectStaff = null;
        PreparedStatement pstmtupdateStaffPhones = null;
        ChannelUserVO channelUserVO = null;
        CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
        final Date currentDate = new Date();
        try {
            // Query to update old user
            final StringBuffer updateUserBuff = new StringBuffer("UPDATE users SET user_code=?,reference_id=?,status=?, ");
            updateUserBuff.append("modified_by=?, modified_on=?,login_id=?,migration_status=?, to_moved_user_id=? WHERE user_id=?");
            pstmtUpdateUser = p_con.prepareStatement(updateUserBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "updateUser query=" + updateUserBuff);
            }

            final StringBuffer insertUserBuff = new StringBuffer("INSERT INTO users(user_id, user_name, network_code, ");
            insertUserBuff.append("login_id, password, category_code, parent_id, owner_id, allowed_ip, allowed_days,");
            insertUserBuff.append("from_time, to_time, last_login_on, employee_code, status, email,company,fax, pswd_modified_on,"); // company,fax
            // added
            // by
            // deepika
            // aggarwal
            insertUserBuff.append("contact_person, contact_no, designation, division, department, msisdn, user_type, ");
            insertUserBuff.append("created_by, created_on, modified_by, modified_on, ");
            insertUserBuff.append("address1, address2, city, state, country, ssn, user_name_prefix, external_code, ");
            insertUserBuff.append("user_code, short_name, reference_id, invalid_password_count, level1_approved_by, ");
            insertUserBuff.append("level1_approved_on, level2_approved_by, level2_approved_on, appointment_date, ");
            insertUserBuff.append("password_count_updated_on,previous_status,firstname,lastname,migration_status, PSWD_RESET ) ");// firstname,lastname
            // added
            // by
            // deepika
            // aggarwal
            insertUserBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            // commented for DB2 pstmtInsertUser =(OraclePreparedStatement)
            // p_con.prepareStatement(insertUserBuff.toString());
            pstmtInsertUser = (PreparedStatement) p_con.prepareStatement(insertUserBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUser query=" + insertUserBuff);
            }

            // ends here

            // Query for user balance updation
            final StringBuffer selectUserBalanceBuff = new StringBuffer("SELECT network_code, network_code_for, ");
            selectUserBalanceBuff.append("U.product_code, balance, prev_balance, last_transfer_type, last_transfer_no,");
            selectUserBalanceBuff.append("last_transfer_on,P.unit_value,daily_balance_updated_on FROM user_balances U,products P  ");
            selectUserBalanceBuff.append("WHERE user_id=? AND U.product_code=P.product_code");
            pstmtSelectUserBalance = p_con.prepareStatement(selectUserBalanceBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserBalance query=" + selectUserBalanceBuff);
            }

            final StringBuffer updateUserBalanceBuff = new StringBuffer("UPDATE user_balances SET balance=?, prev_balance=? ");
            updateUserBalanceBuff.append("WHERE user_id=? AND product_code=? ");
            pstmtUpdateUserBalance = p_con.prepareStatement(updateUserBalanceBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "updateUserBalance query=" + updateUserBalanceBuff);
            }

            final StringBuffer insertUserBalanceBuff = new StringBuffer("INSERT INTO user_balances(user_id, network_code, ");
            insertUserBalanceBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type,");
            insertUserBalanceBuff.append("last_transfer_no, last_transfer_on) VALUES(?,?,?,?,?,?,?,?,?)");
            pstmtInsertUserBalance = p_con.prepareStatement(insertUserBalanceBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserBalance query=" + insertUserBalanceBuff);
                // ends here
            }

            /*
             * //query for user daily balance
             * StringBuffer selectUserDailyBalancesBuff =new
             * StringBuffer("SELECT balance_date, network_code, ");
             * selectUserDailyBalancesBuff.append(
             * "network_code_for, product_code, balance, prev_balance,");
             * selectUserDailyBalancesBuff.append(
             * "last_transfer_type, last_transfer_no, last_transfer_on, created_on "
             * );
             * selectUserDailyBalancesBuff.append(
             * "FROM user_daily_balances WHERE user_id=? ");
             * pstmtSelectUserDailyBalances =
             * p_con.prepareStatement(selectUserDailyBalancesBuff.toString());
             * if(_log.isDebugEnabled())
             * _log.debug("transferChannelUser","selectUserDailyBalances query="+
             * selectUserDailyBalancesBuff);
             * 
             * 
             * StringBuffer insertUserDailyBalancesBuff =new
             * StringBuffer("INSERT INTO user_daily_balances(user_id, ");
             * insertUserDailyBalancesBuff.append(
             * "balance_date, network_code, network_code_for, product_code, ");
             * insertUserDailyBalancesBuff.append(
             * "balance, prev_balance, last_transfer_type, last_transfer_no, ");
             * insertUserDailyBalancesBuff.append(
             * "last_transfer_on, created_on)VALUES(?,?,?,?,?,?,?,?,?,?,?)");
             * pstmtInsertUserDailyBalances =
             * p_con.prepareStatement(insertUserDailyBalancesBuff.toString());
             * if(_log.isDebugEnabled())
             * _log.debug("transferChannelUser","insertUserDailyBalances query="+
             * insertUserDailyBalancesBuff);
             * //ends here
             */
            // Query for user domain transfer
            final StringBuffer selectUserDomainsBuff = new StringBuffer("SELECT domain_code FROM user_domains WHERE user_id = ?");
            pstmtSelectUserDomains = p_con.prepareStatement(selectUserDomainsBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserDomains query=" + selectUserDomainsBuff);
            }

            final StringBuffer insertUserDomainsBuff = new StringBuffer("INSERT INTO user_domains(user_id ,domain_code) ");
            insertUserDomainsBuff.append("VALUES(?,?)");
            pstmtInsertUserDomains = p_con.prepareStatement(insertUserDomainsBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserDomains query=" + insertUserDomainsBuff);
                // ends here
            }

            // Query for user Geographics transfer
            final StringBuffer selectUserGeographiesBuff = new StringBuffer("SELECT grph_domain_code, application_id FROM user_geographies ");
            selectUserGeographiesBuff.append("WHERE user_id = ?");
            pstmtSelectUserGeographies = p_con.prepareStatement(selectUserGeographiesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserGeographies query=" + selectUserGeographiesBuff);
            }

            final StringBuffer insertUserGeographiesBuff = new StringBuffer("INSERT INTO user_geographies(user_id ,");

            // for Zebra and Tango By sanjeew date 05/07/07
            insertUserGeographiesBuff.append(" application_id, ");
            // end Zebra and Tango

            insertUserGeographiesBuff.append("grph_domain_code) VALUES(?,?,?)");
            pstmtInsertUserGeographies = p_con.prepareStatement(insertUserGeographiesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserGeographies query=" + insertUserGeographiesBuff);
                // ends here
            }

            // query for user phones updation
            final StringBuffer selectUserPhonesBuff = new StringBuffer("SELECT user_phones_id, msisdn, description, ");
            selectUserPhonesBuff.append("primary_number, sms_pin, pin_required, phone_profile, phone_language, ");
            selectUserPhonesBuff.append("country, invalid_pin_count, last_transaction_status, last_transaction_on, ");
            selectUserPhonesBuff.append("pin_modified_on, created_by, created_on, modified_by, modified_on, ");
            selectUserPhonesBuff.append("last_transfer_id, last_transfer_type, prefix_id, temp_transfer_id, ");

            // for Zebra and Tango By sanjeew date 05/07/07
            selectUserPhonesBuff.append(" access_type, from_time, to_time, allowed_days, allowed_ip, last_login_on, ");
            // end Zebra and Tango
          //PIN RESET VALUE as same as old
			selectUserPhonesBuff.append(" PIN_RESET,");

            selectUserPhonesBuff.append("first_invalid_pin_time FROM user_phones WHERE user_id=?");
            pstmtSelectUserPhones = p_con.prepareStatement(selectUserPhonesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserPhones query=" + selectUserPhonesBuff);
            }

            final StringBuffer insertUserPhonesBuff = new StringBuffer("INSERT INTO user_phones(user_id, user_phones_id, ");
            insertUserPhonesBuff.append("msisdn, description, primary_number, sms_pin, pin_required, phone_profile, ");
            insertUserPhonesBuff.append("phone_language, country, invalid_pin_count, last_transaction_status, ");
            insertUserPhonesBuff.append("last_transaction_on, pin_modified_on, created_by, created_on, modified_by,");
            insertUserPhonesBuff.append("modified_on, last_transfer_id, last_transfer_type, prefix_id, ");
            insertUserPhonesBuff.append("temp_transfer_id, first_invalid_pin_time, ");

            // for Zebra and Tango By sanjeew date 05/07/07
            insertUserPhonesBuff.append(" access_type, from_time, to_time, allowed_days, allowed_ip, last_login_on,PIN_RESET ");
            insertUserPhonesBuff.append(") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            // end Zebra and Tango

            pstmtInsertUserPhones = p_con.prepareStatement(insertUserPhonesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserPhones query=" + insertUserPhonesBuff);
                // ends here
            }

            // Query for user product type transfer
            final StringBuffer selectUserProductTypesBuff = new StringBuffer("SELECT product_type FROM user_product_types ");
            selectUserProductTypesBuff.append("WHERE user_id = ?");
            pstmtSelectUserProductTypes = p_con.prepareStatement(selectUserProductTypesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserProductTypes query=" + selectUserProductTypesBuff);
            }

            final StringBuffer insertUserProductTypesBuff = new StringBuffer("INSERT INTO user_product_types(user_id ,");
            insertUserProductTypesBuff.append("product_type) VALUES(?,?)");
            pstmtInsertUserProductTypes = p_con.prepareStatement(insertUserProductTypesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserProductTypes query=" + insertUserProductTypesBuff);
                // ends here
            }

            // Query for roles transfer
            final StringBuffer selectUserRolesBuff = new StringBuffer("SELECT role_code, gateway_types FROM user_roles WHERE user_id = ?");
            pstmtSelectUserRoles = p_con.prepareStatement(selectUserRolesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserRoles query=" + selectUserRolesBuff);
            }

            final StringBuffer insertUserRolesBuff = new StringBuffer("INSERT INTO user_roles(user_id ,role_code, gateway_types) VALUES(?,?,?)");
            pstmtInsertUserRoles = p_con.prepareStatement(insertUserRolesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserRoles query=" + insertUserRolesBuff);
                // ends here
            }

            // Query for user services transfer
            // Modification for Service Management [by Vipul]
            final StringBuffer selectUserServicesBuff = new StringBuffer("SELECT US.service_type,US.status");
            selectUserServicesBuff.append(" FROM user_services US,users U,category_service_type CST");
            selectUserServicesBuff
                .append(" WHERE US.user_id=? AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");

            pstmtSelectUserServices = p_con.prepareStatement(selectUserServicesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserServices query=" + selectUserServicesBuff);
            }

            final StringBuffer insertUserServicesBuff = new StringBuffer("INSERT INTO user_services(user_id,service_type,");
            insertUserServicesBuff.append("status) VALUES(?,?,?)");
            pstmtInsertUserServices = p_con.prepareStatement(insertUserServicesBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserServices query=" + insertUserServicesBuff);
                // ends here
            }

            // query for user transfer counts update
            final StringBuffer selectUserTransferCountsBuff = new StringBuffer("SELECT daily_in_count, daily_in_value, ");
            selectUserTransferCountsBuff.append("weekly_in_count, weekly_in_value, monthly_in_count, ");
            selectUserTransferCountsBuff.append("monthly_in_value, daily_out_count, daily_out_value, ");
            selectUserTransferCountsBuff.append("weekly_out_count, weekly_out_value, monthly_out_count, ");
            selectUserTransferCountsBuff.append("monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
            selectUserTransferCountsBuff.append("outside_weekly_in_count, outside_weekly_in_value, ");
            selectUserTransferCountsBuff.append("outside_monthly_in_count, outside_monthly_in_value, ");
            selectUserTransferCountsBuff.append("outside_last_in_time, last_in_time, last_out_time, ");
            selectUserTransferCountsBuff.append("outside_last_out_time, outside_daily_out_count, ");
            selectUserTransferCountsBuff.append("outside_daily_out_value, outside_weekly_out_count, ");
            selectUserTransferCountsBuff.append("outside_weekly_out_value, outside_monthly_out_count, ");
            selectUserTransferCountsBuff.append("outside_monthly_out_value, daily_subscriber_out_count, ");
            selectUserTransferCountsBuff.append("daily_subscriber_out_value, weekly_subscriber_out_count, ");
            selectUserTransferCountsBuff.append("weekly_subscriber_out_value, monthly_subscriber_out_count,");
            selectUserTransferCountsBuff.append("monthly_subscriber_out_value, last_transfer_id, ");
            selectUserTransferCountsBuff.append("last_transfer_date FROM user_transfer_counts WHERE user_id=?");
            pstmtSelectUserTransferCounts = p_con.prepareStatement(selectUserTransferCountsBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectUserTransferCounts query=" + selectUserTransferCountsBuff);
            }

            final StringBuffer insertUserTransferCountsBuff = new StringBuffer("INSERT INTO user_transfer_counts(user_id, ");
            insertUserTransferCountsBuff.append("daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
            insertUserTransferCountsBuff.append("monthly_in_count, monthly_in_value, daily_out_count, ");
            insertUserTransferCountsBuff.append("daily_out_value, weekly_out_count, weekly_out_value, ");
            insertUserTransferCountsBuff.append("monthly_out_count, monthly_out_value, outside_daily_in_count, ");
            insertUserTransferCountsBuff.append("outside_daily_in_value, outside_weekly_in_count, ");
            insertUserTransferCountsBuff.append("outside_weekly_in_value, outside_monthly_in_count, ");
            insertUserTransferCountsBuff.append("outside_monthly_in_value, outside_last_in_time, last_in_time, ");
            insertUserTransferCountsBuff.append("last_out_time, outside_last_out_time, outside_daily_out_count, ");
            insertUserTransferCountsBuff.append("outside_daily_out_value, outside_weekly_out_count, ");
            insertUserTransferCountsBuff.append("outside_weekly_out_value, outside_monthly_out_count, ");
            insertUserTransferCountsBuff.append("outside_monthly_out_value, daily_subscriber_out_count, ");
            insertUserTransferCountsBuff.append("daily_subscriber_out_value, weekly_subscriber_out_count, ");
            insertUserTransferCountsBuff.append("weekly_subscriber_out_value, monthly_subscriber_out_count, ");
            insertUserTransferCountsBuff.append("monthly_subscriber_out_value, last_transfer_id, last_transfer_date)");
            insertUserTransferCountsBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmtInsertUserTransferCounts = p_con.prepareStatement(insertUserTransferCountsBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertUserTransferCounts query=" + insertUserTransferCountsBuff);
            }

            // query for user transfer counts update
            final StringBuffer selectChannelUserInfoBuff = new StringBuffer("SELECT user_grade, contact_person, ");
            selectChannelUserInfoBuff.append("transfer_profile_id, comm_profile_set_id, in_suspend, out_suspend, ");
            selectChannelUserInfoBuff.append("outlet_code, suboutlet_code ");

            // for Zebra and Tango By sanjeew date 05/07/07
            selectChannelUserInfoBuff.append(" ,activated_on,application_id, mpay_profile_id, user_profile_id, is_primary, mcommerce_service_allow, low_bal_alert_allow ");
            // end Zebra and Tango

            selectChannelUserInfoBuff.append("FROM channel_users ");
            selectChannelUserInfoBuff.append("WHERE user_id=? ");
            pstmtSelectChannelUserInfo = p_con.prepareStatement(selectChannelUserInfoBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectChannelUserInfoBuff query=" + selectChannelUserInfoBuff);
            }

            final StringBuffer insertChannelUserInfoBuff = new StringBuffer("INSERT INTO channel_users(user_id, user_grade, ");
            insertChannelUserInfoBuff.append("contact_person, transfer_profile_id, comm_profile_set_id, in_suspend, ");
            insertChannelUserInfoBuff.append("out_suspend, outlet_code, suboutlet_code, activated_on ");

            // for Zebra and Tango By sanjeew date 05/07/07
            insertChannelUserInfoBuff.append(", application_id, mpay_profile_id, user_profile_id, is_primary, mcommerce_service_allow, low_bal_alert_allow ");
            // end Zebra and Tango

            insertChannelUserInfoBuff.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            // commented for DB2
            // pstmtInsertChannelUserInfo=(OraclePreparedStatement)
            // p_con.prepareStatement(insertChannelUserInfoBuff.toString());
            pstmtInsertChannelUserInfo = (PreparedStatement) p_con.prepareStatement(insertChannelUserInfoBuff.toString());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insertChannelUserInfoBuff query=" + insertChannelUserInfoBuff);
            }

            // ends here

            // added by manisha
            final StringBuffer staffUserBuffer = new StringBuffer(" UPDATE users SET parent_id=? , owner_id=?, modified_by=?, modified_on=?, ");
            staffUserBuffer.append("created_by =? , created_on =?  WHERE parent_id=? and user_type=? and status not in('N','C') ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "staffUserBuffer query=" + staffUserBuffer.toString());
            }
            pstmtSelectStaff = p_con.prepareStatement(staffUserBuffer.toString());

            final StringBuffer staffPhonesBuffer = new StringBuffer(" update user_phones set modified_by=?, modified_on=?, created_by =? , created_on = ? ");
            staffPhonesBuffer.append("where user_id in (select user_id from users where parent_id= ? and user_type= ? and status not in ('N','C'))");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "staffPhonesBuffer query=" + staffPhonesBuffer.toString());
            }
            pstmtupdateStaffPhones = p_con.prepareStatement(staffPhonesBuffer.toString());

            if (userList != null) {
                final String networkCode = p_channelUserTransferVO.getNetworkCode();
                String newUserID;
                String userIDPrifix;
                int k = 0;
                final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                ChannelTransferItemsVO transferItemsVO = null;
                ArrayList transferItemsVOList = null;
                final HashMap parentKeyMap = new HashMap();
                String parentID;
                final CommonUtil commonUtil = new CommonUtil();
                final Map oldUserMap = null;
                final Map newUserMap = null;
                for (int i = 0, j = userList.size(); i < j; i++) {
                    transferItemsVOList = new ArrayList();
                    channelUserVO = (ChannelUserVO) userList.get(i);
                    userIDPrifix = channelUserVO.getUserIDPrefix();
                    newUserID = this.generateUserId(networkCode, userIDPrifix);
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before starting transfer:::: channelUserVO=" + channelUserVO);
                    }

                    // user information updation
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before updating user information");
                    }
                    pstmtUpdateUser.setString(1, channelUserVO.getUserID());
                    pstmtUpdateUser.setString(2, newUserID);
                    pstmtUpdateUser.setString(3, PretupsI.USER_STATUS_DELETED);
                    pstmtUpdateUser.setString(4, p_channelUserTransferVO.getModifiedBy());
                    pstmtUpdateUser.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
                    pstmtUpdateUser.setString(6, channelUserVO.getUserID());
                    pstmtUpdateUser.setString(7,PretupsI.USER_MIGRATION_MOVED_STATUS);
                    pstmtUpdateUser.setString(8, newUserID);
                    pstmtUpdateUser.setString(9, channelUserVO.getUserID());
                    addCount = pstmtUpdateUser.executeUpdate();
                    if (addCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                    }
                    pstmtUpdateUser.clearParameters();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After updating user information");
                        // ends here
                    }

                    // new user infomation insertion
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user information");
                    }
                    k = 1;

                    // setting the old and new parentID in the map for the
                    // future user
                    parentKeyMap.put(channelUserVO.getUserID(), newUserID);

                    // getting the parentID of the user.
                    parentID = (String) parentKeyMap.get(channelUserVO.getParentID());
                    if (BTSLUtil.isNullString(parentID)) {
                        parentID = p_channelUserTransferVO.getToParentID();
                    }

                    pstmtInsertUser.setString(k++, newUserID);
                    // for multilanguage support
                    // commented for DB2 pstmtInsertUser.setFormOfUse(k,
                    // OraclePreparedStatement.FORM_NCHAR);
                    pstmtInsertUser.setString(k++, channelUserVO.getUserName());

                    pstmtInsertUser.setString(k++, channelUserVO.getNetworkID());
                    pstmtInsertUser.setString(k++, channelUserVO.getLoginID());
                    pstmtInsertUser.setString(k++, channelUserVO.getPassword());
                    pstmtInsertUser.setString(k++, channelUserVO.getCategoryCode());
                    pstmtInsertUser.setString(k++, parentID);
                    pstmtInsertUser.setString(k++, p_channelUserTransferVO.getToOwnerID());
                    pstmtInsertUser.setString(k++, channelUserVO.getAllowedIps());
                    pstmtInsertUser.setString(k++, channelUserVO.getAllowedDays());
                    pstmtInsertUser.setString(k++, channelUserVO.getFromTime());
                    pstmtInsertUser.setString(k++, channelUserVO.getToTime());
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLastLoginOn()));
                    pstmtInsertUser.setString(k++, channelUserVO.getEmpCode());
                    pstmtInsertUser.setString(k++, PretupsI.USER_STATUS_ACTIVE);
                    pstmtInsertUser.setString(k++, channelUserVO.getEmail());
                    // Added by Deepika aggarwal
                    pstmtInsertUser.setString(k++, channelUserVO.getCompany());
                    pstmtInsertUser.setString(k++, channelUserVO.getFax());
                    // end added by deepika aggarwal
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getPasswordModifiedOn()));
                    pstmtInsertUser.setString(k++, channelUserVO.getContactPerson());
                    pstmtInsertUser.setString(k++, channelUserVO.getContactNo());
                    pstmtInsertUser.setString(k++, channelUserVO.getDesignation());
                    pstmtInsertUser.setString(k++, channelUserVO.getDivisionCode());
                    pstmtInsertUser.setString(k++, channelUserVO.getDepartmentCode());
                    pstmtInsertUser.setString(k++, channelUserVO.getMsisdn());
                    pstmtInsertUser.setString(k++, channelUserVO.getUserType());
                    pstmtInsertUser.setString(k++, channelUserVO.getCreatedBy());
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                    pstmtInsertUser.setString(k++, p_channelUserTransferVO.getModifiedBy());
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
                    pstmtInsertUser.setString(k++, channelUserVO.getAddress1());
                    pstmtInsertUser.setString(k++, channelUserVO.getAddress2());
                    pstmtInsertUser.setString(k++, channelUserVO.getCity());
                    pstmtInsertUser.setString(k++, channelUserVO.getState());
                    pstmtInsertUser.setString(k++, channelUserVO.getCountry());
                    pstmtInsertUser.setString(k++, channelUserVO.getSsn());
                    pstmtInsertUser.setString(k++, channelUserVO.getUserNamePrefix());
                    pstmtInsertUser.setString(k++, channelUserVO.getExternalCode());
                    pstmtInsertUser.setString(k++, channelUserVO.getUserCode());
                    pstmtInsertUser.setString(k++, channelUserVO.getShortName());
                    pstmtInsertUser.setString(k++, channelUserVO.getUserID());
                    pstmtInsertUser.setInt(k++, channelUserVO.getInvalidPasswordCount());
                    pstmtInsertUser.setString(k++, channelUserVO.getLevel1ApprovedBy());
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel1ApprovedOn()));
                    pstmtInsertUser.setString(k++, channelUserVO.getLevel2ApprovedBy());
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel2ApprovedOn()));
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getAppointmentDate()));
                    pstmtInsertUser.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getPasswordCountUpdatedOn()));
                    pstmtInsertUser.setString(k++, PretupsI.SUSPEND);
                    // added by deepika aggarwal
                    pstmtInsertUser.setString(k++, channelUserVO.getFirstName());
                    pstmtInsertUser.setString(k++, channelUserVO.getLastName());
                 	pstmtInsertUser.setString(k++,PretupsI.USER_MIGRATION_COMPLETE_STATUS);
                 	pstmtInsertUser.setString(k++,PretupsI.YES);
                    // end added by deepika aggarwal
                    addCount = pstmtInsertUser.executeUpdate();
                    if (addCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                    }
                    pstmtInsertUser.clearParameters();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After inserting new user information");
                        // ends here
                    }

                    // user phones transfer
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user phones information");
                    }
                    pstmtSelectUserPhones.setString(1, channelUserVO.getUserID());
                    rsSelectUserPhones = pstmtSelectUserPhones.executeQuery();
                    UserPhoneVO userPhoneVO = null;
                    while (rsSelectUserPhones.next()) {
                        k = 1;
                        pstmtInsertUserPhones.setString(k++, newUserID);
                        pstmtInsertUserPhones.setString(k++, String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("msisdn"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("description"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("primary_number"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("sms_pin"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("pin_required"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("phone_profile"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("phone_language"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("country"));
                        pstmtInsertUserPhones.setInt(k++, rsSelectUserPhones.getInt("invalid_pin_count"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("last_transaction_status"));
                        pstmtInsertUserPhones.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("last_transaction_on")));
                        pstmtInsertUserPhones.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("pin_modified_on")));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("created_by"));
                        pstmtInsertUserPhones.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("created_on")));
                        pstmtInsertUserPhones.setString(k++, p_channelUserTransferVO.getModifiedBy());
                        pstmtInsertUserPhones.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("last_transfer_id"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("last_transfer_type"));
                        pstmtInsertUserPhones.setInt(k++, rsSelectUserPhones.getInt("prefix_id"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("temp_transfer_id"));
                        pstmtInsertUserPhones.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("first_invalid_pin_time")));

                        // for Zebra and Tango By sanjeew date 05/07/07
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("access_type"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("from_time"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("to_time"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("allowed_days"));
                        pstmtInsertUserPhones.setString(k++, rsSelectUserPhones.getString("allowed_ip"));
                        pstmtInsertUserPhones.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("last_login_on")));
                        // end Zebra and Tango
                        
                      //PIN_RESET Value
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("PIN_RESET"));

                        addCount = pstmtInsertUserPhones.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }

                        if (rsSelectUserPhones.getString("msisdn").equals(channelUserVO.getMsisdn())) {
                            userPhoneVO = new UserPhoneVO();
                            userPhoneVO.setCountry(rsSelectUserPhones.getString("country"));
                            userPhoneVO.setPhoneLanguage(rsSelectUserPhones.getString("phone_language"));
                            channelUserVO.setUserPhoneVO(userPhoneVO);
                        }
                        pstmtInsertUserPhones.clearParameters();
                    }
                    pstmtSelectUserPhones.clearParameters();
                    try {
                        if (rsSelectUserPhones != null) {
                            rsSelectUserPhones.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After inserting new user phones information");
                        // ends here
                    }

                    /*
                     * //user DailyBalances transfer
                     * if(_log.isDebugEnabled())
                     * _log.debug("transferChannelUser",
                     * "Before inserting new user daily balances information");
                     * pstmtSelectUserDailyBalances.setString(1,channelUserVO.
                     * getUserID());
                     * rsSelectUserDailyBalances
                     * =pstmtSelectUserDailyBalances.executeQuery();
                     * while(rsSelectUserDailyBalances.next())
                     * {
                     * pstmtInsertUserDailyBalances.setString(1,newUserID);
                     * pstmtInsertUserDailyBalances.setTimestamp(2,BTSLUtil.
                     * getTimestampFromUtilDate
                     * (rsSelectUserDailyBalances.getDate("balance_date")));
                     * pstmtInsertUserDailyBalances.setString(3,
                     * rsSelectUserDailyBalances.getString("network_code"));
                     * pstmtInsertUserDailyBalances.setString(4,
                     * rsSelectUserDailyBalances.getString("network_code_for"));
                     * pstmtInsertUserDailyBalances.setString(5,
                     * rsSelectUserDailyBalances.getString("product_code"));
                     * pstmtInsertUserDailyBalances.setLong(6,
                     * rsSelectUserDailyBalances.getLong("balance"));
                     * pstmtInsertUserDailyBalances.setLong(7,
                     * rsSelectUserDailyBalances.getLong("prev_balance"));
                     * pstmtInsertUserDailyBalances.setString(8,
                     * rsSelectUserDailyBalances
                     * .getString("last_transfer_type"));
                     * pstmtInsertUserDailyBalances.setString(9,
                     * rsSelectUserDailyBalances.getString("last_transfer_no"));
                     * pstmtInsertUserDailyBalances.setTimestamp(10,BTSLUtil.
                     * getTimestampFromUtilDate
                     * (rsSelectUserDailyBalances.getDate("last_transfer_on")));
                     * pstmtInsertUserDailyBalances.setTimestamp(11,BTSLUtil.
                     * getTimestampFromUtilDate
                     * (rsSelectUserDailyBalances.getDate("created_on")));
                     * addCount = pstmtInsertUserDailyBalances.executeUpdate();
                     * if(addCount<=0)
                     * throw new BTSLBaseException(this, "transferChannelUser",
                     * "channeluser.viewuserhierarchy.msg.trfunsuccess");
                     * pstmtInsertUserDailyBalances.clearParameters();
                     * }
                     * pstmtSelectUserDailyBalances.clearParameters();
                     * try{if (rsSelectUserDailyBalances !=
                     * null)rsSelectUserDailyBalances.close();}catch (Exception
                     * ex){}
                     * if(_log.isDebugEnabled())
                     * _log.debug("transferChannelUser",
                     * "After inserting new user daily balances information");
                     * // ends here
                     */
                    // user domains transfer
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user domains information");
                    }
                    pstmtSelectUserDomains.setString(1, channelUserVO.getUserID());
                    rsSelectUserDomains = pstmtSelectUserDomains.executeQuery();
                    while (rsSelectUserDomains.next()) {
                        pstmtInsertUserDomains.setString(1, newUserID);
                        pstmtInsertUserDomains.setString(2, rsSelectUserDomains.getString("domain_code"));
                        addCount = pstmtInsertUserDomains.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "After inserting new user domains information");
                        }
                        pstmtInsertUserDomains.clearParameters();
                    }
                    pstmtSelectUserDomains.clearParameters();
                    try {
                        if (rsSelectUserDomains != null) {
                            rsSelectUserDomains.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    // ends here

                    // user geographics transfer
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user geographics information");
                    }
                    pstmtSelectUserGeographies.setString(1, channelUserVO.getUserID());
                    rsSelectUserGeographies = pstmtSelectUserGeographies.executeQuery();
                    while (rsSelectUserGeographies.next()) {
                        pstmtInsertUserGeographies.setString(1, newUserID);

                        // for Zebra and Tango By sanjeew date 05/07/07
                        pstmtInsertUserGeographies.setString(2, rsSelectUserGeographies.getString("application_id"));
                        // end Zebra and Tango

                        /*
                         * pstmtInsertUserGeographies.setString(3,
                         * rsSelectUserGeographies
                         * .getString("grph_domain_code"));
                         * // for channel transfer table
                         * channelUserVO.setGeographicalCode(rsSelectUserGeographies
                         * .getString("grph_domain_code"));
                         */
                        pstmtInsertUserGeographies.setString(3, channelUserVO.getGeographicalCode());
                        //Handling of message in case of geographical code is null
                        if(BTSLUtil.isNullString(channelUserVO.getGeographicalCode())) {
                        	throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        // ends here
                        addCount = pstmtInsertUserGeographies.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        pstmtInsertUserGeographies.clearParameters();
                    }
                    pstmtSelectUserGeographies.clearParameters();
                    try {
                        if (rsSelectUserGeographies != null) {
                            rsSelectUserGeographies.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After inserting new user geographics information");
                        // ends here
                    }

                    // user product types transfer
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user product types information");
                    }
                    pstmtSelectUserProductTypes.setString(1, channelUserVO.getUserID());
                    rsSelectUserProductTypes = pstmtSelectUserProductTypes.executeQuery();
                    while (rsSelectUserProductTypes.next()) {
                        pstmtInsertUserProductTypes.setString(1, newUserID);
                        pstmtInsertUserProductTypes.setString(2, rsSelectUserProductTypes.getString("product_type"));
                        addCount = pstmtInsertUserProductTypes.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        pstmtInsertUserProductTypes.clearParameters();
                    }
                    pstmtSelectUserProductTypes.clearParameters();
                    try {
                        if (rsSelectUserProductTypes != null) {
                            rsSelectUserProductTypes.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After inserting new user product types information");
                        // ends here
                    }

                    // user Roles transfer
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user roles information");
                    }
                    pstmtSelectUserRoles.setString(1, channelUserVO.getUserID());
                    rsSelectUserRoles = pstmtSelectUserRoles.executeQuery();
                    while (rsSelectUserRoles.next()) {
                        pstmtInsertUserRoles.setString(1, newUserID);
                        pstmtInsertUserRoles.setString(2, rsSelectUserRoles.getString("role_code"));

                        // for Zebra and Tango By sanjeew date 05/07/07
                        pstmtInsertUserRoles.setString(3, rsSelectUserRoles.getString("gateway_types"));
                        // end Zebra and Tango

                        addCount = pstmtInsertUserRoles.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        pstmtInsertUserRoles.clearParameters();
                    }
                    pstmtSelectUserRoles.clearParameters();
                    try {
                        if (rsSelectUserRoles != null) {
                            rsSelectUserRoles.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After inserting new user roles information");
                        // ends here
                    }

                    // user Services transfer
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user services information");
                    }
                    pstmtSelectUserServices.setString(1, channelUserVO.getUserID());
                    rsSelectUserServices = pstmtSelectUserServices.executeQuery();
                    while (rsSelectUserServices.next()) {
                        pstmtInsertUserServices.setString(1, newUserID);
                        pstmtInsertUserServices.setString(2, rsSelectUserServices.getString("service_type"));
                        pstmtInsertUserServices.setString(3, rsSelectUserServices.getString("status"));
                        addCount = pstmtInsertUserServices.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        pstmtInsertUserServices.clearParameters();
                    }
                    pstmtSelectUserServices.clearParameters();
                    try {
                        if (rsSelectUserServices != null) {
                            rsSelectUserServices.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After inserting new user services information");
                        // ends here
                    }

                    // user transfer counts update
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before inserting new user transfer counts information");
                    }
                    pstmtSelectUserTransferCounts.setString(1, channelUserVO.getUserID());
                    rsSelectUserTransferCounts = pstmtSelectUserTransferCounts.executeQuery();
                    while (rsSelectUserTransferCounts.next()) {
                        k = 1;
                        pstmtInsertUserTransferCounts.setString(k++, newUserID);
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("daily_in_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("daily_in_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("weekly_in_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("weekly_in_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("monthly_in_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("monthly_in_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("daily_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("daily_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("weekly_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("weekly_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("monthly_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("monthly_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_daily_in_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_daily_in_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_weekly_in_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_weekly_in_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_monthly_in_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_monthly_in_value"));
                        pstmtInsertUserTransferCounts.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("outside_last_in_time")));
                        pstmtInsertUserTransferCounts.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("last_in_time")));
                        pstmtInsertUserTransferCounts.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("last_out_time")));
                        pstmtInsertUserTransferCounts.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("outside_last_out_time")));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_daily_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_daily_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_weekly_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_weekly_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_monthly_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("outside_monthly_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("daily_subscriber_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("daily_subscriber_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("weekly_subscriber_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("weekly_subscriber_out_value"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("monthly_subscriber_out_count"));
                        pstmtInsertUserTransferCounts.setLong(k++, rsSelectUserTransferCounts.getLong("monthly_subscriber_out_value"));
                        pstmtInsertUserTransferCounts.setString(k++, rsSelectUserTransferCounts.getString("last_transfer_id"));
                        pstmtInsertUserTransferCounts.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("last_transfer_date")));
                        addCount = pstmtInsertUserTransferCounts.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        pstmtInsertUserTransferCounts.clearParameters();
                    }
                    pstmtSelectUserTransferCounts.clearParameters();
                    try {
                        if (rsSelectUserTransferCounts != null) {
                            rsSelectUserTransferCounts.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After inserting new user transfer counts information");
                        // ends here
                    }

                    // user balance transfer
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before updating old user balances information");
                    }
                    pstmtSelectUserBalance.setString(1, channelUserVO.getUserID());
                    rsSelectUserBalance = pstmtSelectUserBalance.executeQuery();
                    k = 0;
                    while (rsSelectUserBalance.next()) {
                        pstmtInsertUserBalance.setString(1, newUserID);
                        pstmtInsertUserBalance.setString(2, rsSelectUserBalance.getString("network_code"));
                        pstmtInsertUserBalance.setString(3, rsSelectUserBalance.getString("network_code_for"));
                        pstmtInsertUserBalance.setString(4, rsSelectUserBalance.getString("product_code"));
                        pstmtInsertUserBalance.setLong(5, rsSelectUserBalance.getLong("balance"));
                        pstmtInsertUserBalance.setLong(6, 0L);
                        pstmtInsertUserBalance.setString(7, rsSelectUserBalance.getString("last_transfer_type"));
                        pstmtInsertUserBalance.setString(8, rsSelectUserBalance.getString("last_transfer_no"));
                        pstmtInsertUserBalance.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(rsSelectUserBalance.getDate("last_transfer_on")));
                        addCount = pstmtInsertUserBalance.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "After inserting new user balances information");
                        }

                        pstmtInsertUserBalance.clearParameters();

                        pstmtUpdateUserBalance.setLong(1, 0);
                        pstmtUpdateUserBalance.setLong(2, rsSelectUserBalance.getLong("balance"));
                        pstmtUpdateUserBalance.setString(3, channelUserVO.getUserID());
                        pstmtUpdateUserBalance.setString(4, rsSelectUserBalance.getString("product_code"));
                        addCount = pstmtUpdateUserBalance.executeUpdate();
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }
                        pstmtUpdateUserBalance.clearParameters();
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Before transferItemsVO construction");
                        }
                        transferItemsVO = new ChannelTransferItemsVO();
                        transferItemsVO.setSerialNum(++k);
                        transferItemsVO.setProductCode(rsSelectUserBalance.getString("product_code"));
                        transferItemsVO.setRequiredQuantity(rsSelectUserBalance.getLong("balance"));
                        transferItemsVO.setRequestedQuantity(rsSelectUserBalance.getString("balance"));
                        transferItemsVO.setApprovedQuantity(rsSelectUserBalance.getLong("balance"));
                        transferItemsVO.setUnitValue(rsSelectUserBalance.getLong("unit_value"));
                        transferItemsVO.setNetworkCode(p_channelUserTransferVO.getNetworkCode());
                        transferItemsVOList.add(transferItemsVO);
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "transferItemsVO " + transferItemsVO);
                        }

                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After updating old user balances information");
                        // ends here
                    }

                    // channel user insertion
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "before selecting channel User information");
                    }
                    pstmtSelectChannelUserInfo.setString(1, channelUserVO.getUserID());
                    rsSelectChannelUserInfo = pstmtSelectChannelUserInfo.executeQuery();
                    if (rsSelectChannelUserInfo.next()) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "after selecting channel User information");
                            _log.debug(methodName, "before inserting channel User information");
                        }
                        k = 1;
                        pstmtInsertChannelUserInfo.setString(k++, newUserID);
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("user_grade"));
                        // for multilanguage support
                        // commented for DB2
                        // pstmtInsertChannelUserInfo.setFormOfUse(k,
                        // OraclePreparedStatement.FORM_NCHAR);
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("contact_person"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("transfer_profile_id"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("comm_profile_set_id"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("in_suspend"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("out_suspend"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("outlet_code"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("suboutlet_code"));
                        pstmtInsertChannelUserInfo.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(rsSelectChannelUserInfo.getTimestamp("activated_on")));
                        // for Zebra and Tango By sanjeew date 05/07/07
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("application_id"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("mpay_profile_id"));
                        pstmtInsertChannelUserInfo.setString(k++, newUserID);
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("is_primary"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("mcommerce_service_allow"));
                        pstmtInsertChannelUserInfo.setString(k++, rsSelectChannelUserInfo.getString("low_bal_alert_allow"));
                        // end Zebra and Tango

                        addCount = pstmtInsertChannelUserInfo.executeUpdate();
                        pstmtInsertChannelUserInfo.clearParameters();

                        // for channel transfer table
                        channelUserVO.setUserGrade(rsSelectChannelUserInfo.getString("user_grade"));
                        channelUserVO.setTransferProfileID(rsSelectChannelUserInfo.getString("transfer_profile_id"));
                        channelUserVO.setCommissionProfileSetID(rsSelectChannelUserInfo.getString("comm_profile_set_id"));
                        
                        channelUserVO.setMpayProfileID(rsSelectChannelUserInfo.getString("mpay_profile_id"));
                        channelUserVO.setFxedInfoStr(newUserID);
                        // ends here
                        if (addCount <= 0) {
                            throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                        }

                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "after inserting channel User information");
                        }
                    }
                    // ends here
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "before updating channel transfer information");
                    }

                    final CommissionProfileSetVO  commissionProfileSetReceiverVO = commissionProfileTxnDAO.loadCommProfileSetDetails(p_con,channelUserVO.getCommissionProfileSetID(), currentDate); 
                    
                    channelTransferVO.setCreatedBy(p_channelUserTransferVO.getCreatedBy());
                    channelTransferVO.setCreatedOn(p_channelUserTransferVO.getCreatedOn());
                    channelTransferVO.setModifiedBy(p_channelUserTransferVO.getModifiedBy());
                    channelTransferVO.setModifiedOn(p_channelUserTransferVO.getModifiedOn());
                    channelTransferVO.setTransferInitatedBy(p_channelUserTransferVO.getCreatedBy());
                    channelTransferVO.setFromUserID(channelUserVO.getUserID());
                    channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                    channelTransferVO.setSenderGradeCode(channelUserVO.getUserGrade());
                    channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
                    channelTransferVO.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
                    channelTransferVO.setDualCommissionType(commissionProfileSetReceiverVO.getDualCommissionType());
                    channelTransferVO.setSenderTxnProfile(channelUserVO.getTransferProfileID());
                    channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
                    channelTransferVO.setTransferDate(p_channelUserTransferVO.getCreatedOn());
                    channelTransferVO.setToUserID(newUserID);
                    channelTransferVO.setNetworkCode(p_channelUserTransferVO.getNetworkCode());
                    channelTransferVO.setNetworkCodeFor(p_channelUserTransferVO.getNetworkCode());
                    channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                    channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
                    channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                    channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                    channelTransferVO.setDomainCode(p_channelUserTransferVO.getDomainCode());
                    channelTransferVO.setCategoryCode(channelUserVO.getCategoryCode());
                    channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
                    channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
                    channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                    channelTransferVO.setRequestGatewayCode(PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    channelTransferVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
                    channelTransferVO.setControlTransfer(PretupsI.CONTROL_LEVEL_ADJ);
                    // By Sandeep Goel ID CUT001
                    // Some new field added for the sender and receiver
                    // informaiton and some new
                    // constraints added in the table.
                    channelTransferVO.setCommProfileVersion(commissionProfileSetReceiverVO.getCommProfileVersion());
                    channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
                    channelTransferVO.setReceiverDomainCode(p_channelUserTransferVO.getDomainCode());
                    channelTransferVO.setFromUserCode(channelUserVO.getUserCode());
                    channelTransferVO.setToUserCode(channelUserVO.getUserCode());
                    channelTransferVO.setActiveUserId(p_channelUserTransferVO.getCreatedBy());
                    // ends here
                    
                    ChannelTransferItemsVO trfrItemsVO = null;
                    int balance = 0;
                    channelTransferVO.setChannelTransferitemsVOList(transferItemsVOList);
                    for (int bal=0; bal<transferItemsVOList.size(); bal++) {
                    	trfrItemsVO = (ChannelTransferItemsVO) transferItemsVOList.get(bal);
                    	balance += trfrItemsVO.getApprovedQuantity();
                    }
                    channelTransferVO.setTransferMRP(balance);
                    channelTransferVO.setRequestedQuantity(balance);
                    ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);
                    channelTransferDAO.addChannelTransfer(p_con, channelTransferVO);
                    pstmtSelectUserBalance.clearParameters();
                    try {
                        if (rsSelectUserBalance != null) {
                            rsSelectUserBalance.close();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(methodName, ex);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "after updating channel transfer information");
                    }
                    // ends here
                    int staffUpdateCount = 0;
                    pstmtSelectStaff.clearParameters();
                    pstmtSelectStaff.setString(1, newUserID);
                    pstmtSelectStaff.setString(2, p_channelUserTransferVO.getToOwnerID());
                    pstmtSelectStaff.setString(3, p_channelUserTransferVO.getModifiedBy());
                    pstmtSelectStaff.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
                    pstmtSelectStaff.setString(5, newUserID);
                    pstmtSelectStaff.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getCreatedOn()));
                    pstmtSelectStaff.setString(7, channelUserVO.getUserID());
                    pstmtSelectStaff.setString(8, PretupsI.STAFF_USER_TYPE);
                    staffUpdateCount = pstmtSelectStaff.executeUpdate();
                    if (staffUpdateCount < 0) {
                        throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                    }
                    pstmtupdateStaffPhones.clearParameters();
                    pstmtupdateStaffPhones.setString(1, p_channelUserTransferVO.getModifiedBy());
                    pstmtupdateStaffPhones.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
                    pstmtupdateStaffPhones.setString(3, newUserID);
                    pstmtupdateStaffPhones.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getCreatedOn()));
                    pstmtupdateStaffPhones.setString(5, newUserID);
                    pstmtupdateStaffPhones.setString(6, PretupsI.STAFF_USER_TYPE);
                    staffUpdateCount = pstmtupdateStaffPhones.executeUpdate();
                    if (staffUpdateCount < 0) {
                        throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
                    }
                    /*
                     * if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue())
                     * {
                     * if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() &&
                     * PretupsI.SELECT_CHECKBOX
                     * .equals(channelUserVO.getMcommerceServiceAllow()))
                     * {
                     * oldUserMap=commonUtil.getMapfromUserVO(channelUserVO);
                     * channelUserVO.setUserID(newUserID);
                     * newUserMap=commonUtil.getMapfromUserVO(channelUserVO);
                     * if(!commonUtil.transferUser(p_con, newUserMap,
                     * oldUserMap))
                     * throw new BTSLBaseException(this, "transferChannelUser",
                     * "channeluser.viewuserhierarchy.msg.mobiquitychangenotupdate"
                     * );
                     * newUserMap=null;
                     * oldUserMap=null;
                     * }
                     * }
                     */
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            addCount = 0;
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[channelUserTransferDAO]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            addCount = 0;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[channelUserTransferDAO]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdateUser != null) {
                    pstmtUpdateUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUser != null) {
                    pstmtInsertUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectUserGeographies != null) {
                    rsSelectUserGeographies.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserGeographies != null) {
                    pstmtSelectUserGeographies.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserGeographies != null) {
                    pstmtInsertUserGeographies.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectUserDomains != null) {
                    rsSelectUserDomains.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserDomains != null) {
                    pstmtSelectUserDomains.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserDomains != null) {
                    pstmtInsertUserDomains.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectUserProductTypes != null) {
                    rsSelectUserProductTypes.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserProductTypes != null) {
                    pstmtSelectUserProductTypes.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserProductTypes != null) {
                    pstmtInsertUserProductTypes.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectUserRoles != null) {
                    rsSelectUserRoles.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserRoles != null) {
                    pstmtSelectUserRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserRoles != null) {
                    pstmtInsertUserRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectUserServices != null) {
                    rsSelectUserServices.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserServices != null) {
                    pstmtSelectUserServices.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserServices != null) {
                    pstmtInsertUserServices.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectUserBalance != null) {
                    rsSelectUserBalance.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserBalance != null) {
                    pstmtSelectUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserBalance != null) {
                    pstmtInsertUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalance != null) {
                    pstmtUpdateUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectUserPhones != null) {
                    rsSelectUserPhones.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserPhones != null) {
                    pstmtSelectUserPhones.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserPhones != null) {
                    pstmtInsertUserPhones.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            /*
             * try{if (rsSelectUserDailyBalances !=
             * null)rsSelectUserDailyBalances.close();}catch (Exception ex){}
             * try{if(pstmtSelectUserDailyBalances !=null)
             * pstmtSelectUserDailyBalances .close();}catch(Exception e){}
             * try{if(pstmtInsertUserDailyBalances !=null)
             * pstmtInsertUserDailyBalances .close();}catch(Exception e){}
             */
            try {
                if (rsSelectUserTransferCounts != null) {
                    rsSelectUserTransferCounts.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserTransferCounts != null) {
                    pstmtSelectUserTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserTransferCounts != null) {
                    pstmtInsertUserTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (rsSelectChannelUserInfo != null) {
                    rsSelectChannelUserInfo.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectChannelUserInfo != null) {
                    pstmtSelectChannelUserInfo.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertChannelUserInfo != null) {
                    pstmtInsertChannelUserInfo.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectStaff != null) {
                    pstmtSelectStaff.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateStaffPhones != null) {
                    pstmtupdateStaffPhones.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting addCount=" + addCount);
            }
        }// end of finally
        return addCount;
    }

    /**
     * Method generateUserId.
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return String
     * @throws Exception
     */

    private String generateUserId(String p_networkCode, String p_prefix) throws Exception {
        final String methodName = "generateUserId";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        }
        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)) + "", length);

        // id =
        // p_networkCode+Constants.getProperty("SEPARATOR_FORWARD_SLASH")+p_prefix+id;
        id = p_networkCode + p_prefix + id;
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting id=" + id);
        }
        return id;
    }

    /**
     * by ranjana
     * Method :loadGeogphicalHierarchyListByToParentId
     * this method load geographical domain hierarchy list
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentID
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadGeogphicalHierarchyListByToParentId(Connection p_con, String p_toParentID) throws BTSLBaseException {
        final String methodName = "loadGeogphicalHierarchyListByToParentId";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_toParentID=" + p_toParentID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        GeographicalDomainVO geoGraphicaldomainVO = null;
        final ArrayList geoList = new ArrayList();

       try {
    	   
    	    ChannelUserTransferWebQry chnlUserWebQry=(ChannelUserTransferWebQry)ObjectProducer.getObject(QueryConstants.CHANNELUSER_TRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            pstmtSelect = chnlUserWebQry.loadGeogphicalHierarchyListByToParentId(p_con, p_toParentID);
      

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geoGraphicaldomainVO = new GeographicalDomainVO();
                geoGraphicaldomainVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                geoGraphicaldomainVO.setNetworkCode(rs.getString("network_code"));
                geoGraphicaldomainVO.setGrphDomainName(rs.getString("grph_domain_name"));
                geoGraphicaldomainVO.setParentDomainCode(rs.getString("parent_grph_domain_code"));
                geoGraphicaldomainVO.setGrphDomainType(rs.getString("grph_domain_type"));

                geoList.add(geoGraphicaldomainVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserTransferDAO[loadGeogphicalHierarchyListByToParentId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserTransferDAO[loadGeogphicalHierarchyListByToParentId]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: geoList size =" + geoList.size());
            }
        }
        return geoList;
    }

    /**
     * Method for checking Is MSISDN with status Y already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param ChannelUserTransferVO
     * @return flag boolean
     * @throws BTSLBaseException
     * @author priyanka.goel
     */
    public boolean isMSISDNExist(Connection p_con, ChannelUserTransferVO p_channelUserTransferVO) throws BTSLBaseException {

        final String methodName = "isMSISDNExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_channelUserTransferVO=" + p_channelUserTransferVO);
        }
        final ArrayList userList = p_channelUserTransferVO.getUserHierarchyList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        ChannelUserVO channelUserVO = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT U.msisdn,U.status from users U ");
        strBuff.append(" WHERE U.msisdn = ? AND U.status = ?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            if (userList != null) {
                for (int i = 0, j = userList.size(); i < j; i++) {
                    channelUserVO = (ChannelUserVO) userList.get(i);
                    pstmt = p_con.prepareStatement(sqlSelect);
                    pstmt.setString(1, channelUserVO.getMsisdn());
                    pstmt.setString(2, PretupsI.USER_STATUS_ACTIVE);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        existFlag = true;
                        break;
                    }
                }
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "channelUserTransferDAO[isMSISDNExist]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "channelUserTransferDAO[isMSISDNExist]", "", "", "",
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
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
        return existFlag;
    }
	
	public Map transferChannelUserIntermediate(Connection conn,ChannelUserTransferVO channelUserTransferVO) throws BTSLBaseException
	{
		final String METHOD_NAME = "transferChannelUserIntermediate";
		if(_log.isDebugEnabled())
			_log.debug(METHOD_NAME,"Entered channelUserTransferVO="+channelUserTransferVO);

		ChannelUserVO channelUserVO = null;
		PreparedStatement pstmtSelectUser= null;
        ResultSet rs = null;
		Map<String,String>  lockDataMap= new HashMap<String,String>();	
		try
		{
			ArrayList userList = channelUserTransferVO.getUserHierarchyList();

			StringBuffer selectUserBuff =new StringBuffer("SELECT MIGRATION_STATUS FROM USERS WHERE user_id=? AND STATUS=?  AND (MIGRATION_STATUS is null OR MIGRATION_STATUS = ?) FOR UPDATE NOWAIT");
			pstmtSelectUser = conn.prepareStatement(selectUserBuff.toString());

			for(int i=0,j=userList.size();i<j;i++)
			{
				channelUserVO=(ChannelUserVO )userList.get(i);	
				pstmtSelectUser.setString(1, channelUserVO.getUserID());
				pstmtSelectUser.setString(2, PretupsI.USER_STATUS_SUSPEND);
				pstmtSelectUser.setString(3, PretupsI.USER_MIGRATION_COMPLETE_STATUS);			
				rs = pstmtSelectUser.executeQuery();
				while (rs.next())
				{
					lockDataMap.put(channelUserVO.getUserID(), rs.getString("MIGRATION_STATUS"));				
				}
			}
			}
			catch (SQLException sqle)
			{
				_log.error(METHOD_NAME,"SQLException "+sqle.getMessage());	
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserTransferDAO[transferChannelUserIntermediate]","","","","Exception:"+sqle.getMessage());
				if(54 == sqle.getErrorCode()){
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_MIGRATION_IN_PROCESS);
				}
				throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
			}//end of catch
			catch (Exception e)
			{
				_log.error(METHOD_NAME,"Exception"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserTransferDAO[transferChannelUserIntermediate]","","","","Exception:"+e.getMessage());
				throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
			}
		
		finally
		     {
			
			try {
				if (rs != null)
					rs.close();
			} 
			
			catch (Exception e) {
				_log.error(METHOD_NAME,"Exception : ", e.getMessage());
			}
			
			try {
				if (pstmtSelectUser != null)
					pstmtSelectUser.close();
			} 
			
			catch (Exception e) {
				_log.error(METHOD_NAME,"Exception : ", e.getMessage());
			}
			
			}
		
			return lockDataMap;
		}
	
	public int transferChannelUserFinal(Connection conn, String status,ChannelUserTransferVO channelUserTransferVO, Map lockedDataMap) throws BTSLBaseException
	{
		final String METHOD_NAME = "transferChannelUserFinal";
		if(_log.isDebugEnabled())
			_log.debug(METHOD_NAME,"Entered channelUserTransferVO="+channelUserTransferVO);

		int updateCount=-1;
		ChannelUserVO channelUserVO = null;
		PreparedStatement pstmtUpdateUser= null;
		boolean rejectionFlag=false;
		int count=0;

		try
		{
			StringBuffer updateUserBuff =new StringBuffer("UPDATE users SET MIGRATION_STATUS=? WHERE user_id=? AND MIGRATION_STATUS = ?");
			pstmtUpdateUser = conn.prepareStatement(updateUserBuff.toString());
			if(_log.isDebugEnabled())
				_log.debug(METHOD_NAME,"updateUser query="+updateUserBuff);

			if(lockedDataMap!=null && lockedDataMap.size()>0)
			{
				String userID;
				Iterator itr = lockedDataMap.keySet().iterator();
				while(itr.hasNext())
				{
					channelUserVO = (ChannelUserVO)itr.next();					
					userID=channelUserVO.getUserID();
					if(_log.isDebugEnabled())
						_log.debug(METHOD_NAME,"Before starting transfer:::: channelUserVO="+channelUserVO);

					//user information updation
					if(_log.isDebugEnabled())
						_log.debug(METHOD_NAME,"Before updating user information for userID "+userID);
					pstmtUpdateUser.setString(1, status);
					pstmtUpdateUser.setString(2,userID);
					pstmtUpdateUser.setString(3,PretupsI.USER_MIGRATION_UNDER_PROCESS_STATUS);
					
					updateCount =	pstmtUpdateUser.executeUpdate();
					if(updateCount<=0){					
						rejectionFlag = true;
						if(_log.isDebugEnabled())
						_log.debug(METHOD_NAME,"user migration_status is not updated for userID "+userID);

					}
					else count++;			
					pstmtUpdateUser.clearParameters();
					if(_log.isDebugEnabled())
						_log.debug(METHOD_NAME,"After updating user information updateCount"+updateCount);
				}	

				if(rejectionFlag)
					throw new BTSLBaseException(this, METHOD_NAME, "channeluser.viewuserhierarchy.msg.trfunsuccess");
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch (SQLException sqle)
		{
			_log.error(METHOD_NAME,"SQLException "+sqle.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserTransferDAO[transferChannelUserFinal]","","","","Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			_log.error(METHOD_NAME,"Exception "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserTransferDAO[transferChannelUserFinal]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
		}
		finally{
			try {
				if (pstmtUpdateUser != null)
					pstmtUpdateUser.close();
			} 
			
			catch (Exception e) {
				_log.error(METHOD_NAME,"Exception : ", e.getMessage());
			}
		}
		return count;
	}
	
}
