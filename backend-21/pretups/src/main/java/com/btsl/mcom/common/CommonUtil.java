package com.btsl.mcom.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/*
 * CommonUtil.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 03/07/2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.util.ValidatorUtils;
/*import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;*/
import org.springframework.util.ReflectionUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.user.businesslogic.MessageCodes;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class CommonUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * loads Mpay Transfer Profiles available on the basis of category code.
     * On the basis of grade code selected Mpay profile would be selected using
     * javascript.
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @return List (consists of Map containing
     *         keys("profile_id,profile_name,grade_code,category_code) and
     *         corersponding values
     * @throws BTSLBaseException
     *             (consisting of general error message code and arguments)
     */
    public List getMPayProfileList(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        // load list from MTX_TRF_CNTRL_PROFILE

        ArrayList list = new ArrayList();
        
        final String METHOD_NAME = "getMPayProfileList";
        try {
            StringBuffer selectQuery = new StringBuffer("SELECT profile_id, profile_name, profile_short_name, category_code, grade_code, grph_domain_code, status_id, profile_type, description");
            selectQuery.append(" FROM mtx_trf_cntrl_profile WHERE category_code = ? AND status_id= ? AND profile_type='ROLE'");
            selectQuery.append(" ORDER BY grade_code,profile_name ");

            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, "Y");
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                Map map = new HashMap();
                map.put("profile_id", rs.getString("profile_id"));
                map.put("profile_name", rs.getString("profile_name"));
                map.put("grade_code", rs.getString("grade_code"));
                map.put("category_code", rs.getString("category_code"));
                list.add(map);
            }
        } 
            }
        }catch (SQLException sqe) {

            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "getMPayProfileList", "USER_PROFILE_DOES_NOT_EXIST",sqe);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getMPayProfileList", "tango.error.general.processing",e);
        } finally {
            _log.debug(METHOD_NAME, "Exiting");

        }
        return list;
    }

    /**
     * loads Mpay Transfer Profiles available on the basis of category code.
     * On the basis of grade code selected Mpay profile would be selected using
     * javascript.
     * 
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @return List (consists of Map containing
     *         keys("profile_id,profile_name,grade_code,category_code) and
     *         corersponding values
     * @throws BTSLBaseException
     *             (consisting of general error message code and arguments)
     */
    public List getMPayProfileDomainList(Connection p_con, String p_domainCode) throws BTSLBaseException {
        // load list from MTX_TRF_CNTRL_PROFILE,MTX_CATEGORIES

        ArrayList list = new ArrayList();
        
        ResultSet rs = null;
        final String METHOD_NAME = "getMPayProfileDomainList";
        try {
            StringBuffer selectQuery = new StringBuffer("SELECT MP.profile_id, MP.profile_name, MP.profile_short_name, MP.category_code, MP.grade_code, MP.grph_domain_code, MP.status_id, MP.profile_type, MP.description ");
            selectQuery.append(" FROM mtx_trf_cntrl_profile MP,categories C  WHERE C.domain_code= ? AND MP.category_code = C.category_code AND MP.status_id= ?  AND MP.profile_type='ROLE'");
            selectQuery.append(" ORDER BY grade_code,profile_name ");

            try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, p_domainCode);
            pstmtSelect.setString(2, "Y");
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                Map map = new HashMap();
                map.put("profile_id", rs.getString("profile_id"));
                map.put("profile_name", rs.getString("profile_name"));
                map.put("grade_code", rs.getString("grade_code"));
                map.put("category_code", rs.getString("category_code"));
                list.add(map);
            }
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "getMPayProfileDomainList", "USER_PROFILE_DOES_NOT_EXIST",sqe);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getMPayProfileDomainList", "tango.error.general.processing",e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
           

        }
        return list;
    }

    /**
     * Converts UserVO to Hashmap.
     * 
     * @param p_userVO
     *            UserVO
     * @return Map
     */
    public Map getMapfromUserVO(UserVO p_userVO) {
        final String METHOD_NAME = "getMapfromUserVO";
        Map mainMap = new HashMap();

        Map uMap = new HashMap();
        uMap.put("userId", p_userVO.getUserID());
        uMap.put("msisdn", p_userVO.getMsisdn());
        uMap.put("loginId", p_userVO.getLoginID());
        uMap.put("userName", p_userVO.getUserName());
        uMap.put("userType", p_userVO.getUserType());
        uMap.put("categoryCode", p_userVO.getCategoryCode());
        try {
            if (p_userVO.isStaffUser())
                uMap.put("categoryName", p_userVO.getCategoryVO().getCategoryName() + "(Staff)");
            else
                uMap.put("categoryName", p_userVO.getCategoryVO().getCategoryName());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        uMap.put("networkId", p_userVO.getNetworkID());
        uMap.put("networkCode", p_userVO.getNetworkID());
        uMap.put("networkNameCode", p_userVO.getNetworkNamewithNetworkCode());
        uMap.put("networkName", p_userVO.getNetworkName());

        Map gdMap = new HashMap();
        gdMap.put("geoList", p_userVO.getGeographicalAreaList());
        gdMap.put("geoID", p_userVO.getNetworkID());
        gdMap.put("grphDomainName", p_userVO.getNetworkName());
        gdMap.put("networkCode", p_userVO.getNetworkID());

        Map partyAccessMap = new HashMap();
        partyAccessMap.put("msisdn", p_userVO.getMsisdn());

        Map categoryMap = new HashMap();
        categoryMap.put("categoryCode", p_userVO.getCategoryCode());
        categoryMap.put("categoryCode", p_userVO.getCategoryCode());
        Map domainTypeCodeMap = new HashMap();
        domainTypeCodeMap.put("divDeptAllowed", Character.valueOf('N'));
        Map domainCodeMap = new HashMap();
        domainCodeMap.put("domainTypeCode", domainTypeCodeMap);
        categoryMap.put("domainCode", domainCodeMap);

        mainMap.put("party", uMap);
        mainMap.put("partyAccess", partyAccessMap);

        mainMap.put("geo", gdMap);
        mainMap.put("categoryMap", categoryMap);
        mainMap.put("menuItemList", p_userVO.getMenuItemList());

        return mainMap;
    }

    /**
     * Converts UserVO to Hashmap.
     * 
     * @param p_userVO
     *            UserVO
     * @return Map
     */
    public void setSessionParametes(HttpSession p_session, UserVO p_userVO) {

        Map mainMap = getMapfromUserVO(p_userVO);

        p_session.setAttribute("party", mainMap.get("party"));
        p_session.setAttribute("partyAccess", mainMap.get("partyAccess"));

        p_session.setAttribute("geo", mainMap.get("geo"));
        p_session.setAttribute("categoryMap", mainMap.get("categoryMap"));
        p_session.setAttribute("menuItemList", mainMap.get("menuItemList"));
    }

    /**
     * Checks if user is active(i.e not ready for deletion)
     * 
     * @param p_con
     *            Connection
     * @param p_userMap
     *            Map
     * @return boolean,true if the user is active
     * @throws BTSLBaseException
     *             (cosisting of general error message code and arguments)
     */
    public boolean isUserActive(Connection p_con, Map p_userMap) throws BTSLBaseException {
        // check from MTX_PAYMENT_METHODS, check if payment method exists for
        // the user
        
        final String METHOD_NAME = "isUserActive";
        try {
            StringBuffer selectQuery = new StringBuffer("SELECT 1 FROM mtx_payment_methods ");
            selectQuery.append(" WHERE party_user_id = ? AND (status_id = ? OR status_id = ?)");
            // added by mohit for common msisdn usage on 15 july,2008
            selectQuery.append("AND USER_TYPE=?");
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, (String) ((Map) p_userMap.get("party")).get("userId"));
            pstmtSelect.setString(2, "Y");
            pstmtSelect.setString(3, "S");
            // added by mohit for common msisdn usage on 15 july,2008
            pstmtSelect.setString(4, "CHANNEL");
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                return true;
            }
        }
            }
        }catch (SQLException sqe) {

            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "isUserActive", "PAYMENT_METHOD_DOES_NOT_EXIST",sqe);
        } catch (Exception e) {

            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "isUserActive", "tango.error.general.processing",e);
        } finally {
            _log.debug(METHOD_NAME, "Exiting..");

        }
        return false;
    }

    /**
     * isUserBalance
     * 
     * @param p_con
     *            Connection
     * @param p_userMap
     *            Map
     * @return boolean,true if the user is active
     * @throws BTSLBaseException
     *             (cosisting of general error message code and arguments)
     */
    public boolean isUserBalance(Connection p_con, Map p_userMap) throws BTSLBaseException {
        // check from MTX_PAYMENT_METHODS, check if payment method exists for
        // the user

        int balance = 0;
        
        final String METHOD_NAME = "isUserBalance";
        try {
            CommonUtilQry commonUtilQry=(CommonUtilQry)ObjectProducer.getObject(QueryConstants.COMMON_UTIL_QRY, QueryConstants.QUERY_PRODUCER);
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(commonUtilQry.isUserBalance());)
           {
            pstmtSelect.setString(1, (String) ((Map) p_userMap.get("party")).get("userId"));
            // added by mohit for common msisdn usage on 15 july,2008
            pstmtSelect.setString(2, "CHANNEL");
            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                balance = rs.getInt(1);
                if (balance > 0)
                    throw new BTSLBaseException(this, "isUserBalance", "BALANCE_NON_ZERO");
            }
            return false;
        }
           }
        }catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "isUserBalance", "ERROR_WHILE_CHECKING_BALANCE",sqe);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "isUserBalance", "tango.error.general.processing",e);
        } finally {
            _log.debug(METHOD_NAME, "Exiting..");
    }
    }

    public boolean isPendingTransaction(Connection p_con, Map p_userMap) throws BTSLBaseException {
        // check from MTX_PAYMENT_METHODS, check if payment method exists for
        // the user
        
        final String METHOD_NAME = "isPendingTransaction";
        try {
            StringBuffer selectQuery = new StringBuffer("SELECT 1 FROM mtx_transaction_header MTH, mtx_transaction_items MTI ");
            selectQuery.append(" WHERE MTH.transfer_id=MTI.transfer_id ");
            selectQuery.append(" AND MTI.party_id = ? ");
            selectQuery.append(" AND MTH.transfer_status NOT IN ('CL','FL','CN')");
            try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, (String) ((Map) p_userMap.get("party")).get("userId"));
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                throw new BTSLBaseException(this, "isPendingTransaction", "PENDING_TRANSACTION_EXIST");
            } else
                return false;
        } 
            }
        }catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "isPendingTransaction", "ERROR_WHILE_CHECKING_PENDING_TRANSACTIONS",sqe);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "isPendingTransaction", "tango.error.general.processing",e);
        } finally {
           _log.debug(METHOD_NAME, "Exiting..");
        }
    }

    /**
     * Deletes user
     * 
     * @param p_con
     *            Connection
     * @param p_userMap
     *            Map
     * @return boolean,true if the user is successfully deleted
     * @throws BTSLBaseException
     *             (cosisting of general error message code and arguments)
     */
    public int deleteUser(Connection p_con, Map p_userMap) throws BTSLBaseException {
        /*
         * update entries in following tables
         * MTX_PAYMENT_METHODS
         * MTX_WALLET
         */
        // check if balance or any pending transaction exists in the system
        if (!isUserBalance(p_con, p_userMap) && !isPendingTransaction(p_con, p_userMap)) {

            
            int updateCount = 0;
            final String METHOD_NAME = "deleteUser";
            try {
                StringBuffer UpdateQuery = new StringBuffer();
                UpdateQuery.append("UPDATE mtx_payment_methods SET  status_id='N'");
                UpdateQuery.append(" WHERE party_user_id=? AND payment_method_number= ?");
                // added by mohit for common msisdn usage on 15 july,2008
                UpdateQuery.append("AND user_type=?");

                String query = UpdateQuery.toString();

               try(PreparedStatement pstmtUpdate = p_con.prepareStatement(query);)
               {

                // for multilanguage support
                pstmtUpdate.setString(1, (String) ((Map) p_userMap.get("party")).get("userId"));
                pstmtUpdate.setString(2, (String) ((Map) p_userMap.get("partyAccess")).get("msisdn"));
                // added by mohit for common msisdn usage on 15 july,2008
                pstmtUpdate.setString(3, "CHANNEL");

                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount > 0)
                    return 1;
            } 
            }catch (SQLException sqe) {
                _log.errorTrace(METHOD_NAME, sqe);
                throw new BTSLBaseException(this, "deleteUser", "ERROR_WHILE_UPDATING_PAYMENT_METHOD_STATUS",sqe);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, "deleteUser", "tango.error.general.processing",e);
            } finally {
            	_log.debug(METHOD_NAME, "Exiting..");
            }
            return -1;
        } else
            return -1;
    }

    /**
     * Transfer user
     * 
     * @param p_con
     *            Connection
     * @param p_newUserMap
     *            Map
     * @param p_oldUserMap
     *            Map
     * @return boolean,true if the user is successfully transfered
     * @throws BTSLBaseException
     *             (cosisting of general error message code and arguments)
     */
    public boolean transferUser(Connection p_con, Map p_newUserMap, Map p_oldUserMap) throws BTSLBaseException {
        /*
         * update entries in following tables
         * MTX_PAYMENT_METHODS
         * MTX_WALLET
         */
        
        int updateCount = 0;
        final String METHOD_NAME = "transferUser";
        try {
            StringBuffer UpdateQuery = new StringBuffer();
            UpdateQuery.append("UPDATE mtx_payment_methods SET  party_user_id=? ");
            UpdateQuery.append(" WHERE party_user_id=? AND payment_method_number= ?");
            // added by mohit for common msisdn usage on 15 july,2008
            UpdateQuery.append("AND user_type=?");
            String query = UpdateQuery.toString();
            try(PreparedStatement pstmtUpdate = p_con.prepareStatement(query);)
            {

            // for multilanguage support
            pstmtUpdate.setString(1, (String) ((Map) p_newUserMap.get("party")).get("userId"));
            pstmtUpdate.setString(2, (String) ((Map) p_oldUserMap.get("party")).get("userId"));
            pstmtUpdate.setString(3, (String) ((Map) p_oldUserMap.get("partyAccess")).get("msisdn"));
            pstmtUpdate.setString(4, "CHANNEL");

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0)
                return true;
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "transferUser", "ERROR_WHILE_UPDATING_PAYMENT_METHOD_DETAILS",sqe);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "transferUser", "tango.error.general.processing",e);
        } finally {
        	_log.debug(METHOD_NAME, "Exiting..");
        }
        return false;
    }

    /**
     * Transfer user
     * 
     * @param p_con
     *            Connection
     * @param p_newUserMap
     *            Map
     * @param p_oldUserMap
     *            Map
     * @return boolean,true if the user is successfully transfered
     * @throws BTSLBaseException
     *             (cosisting of general error message code and arguments)
     */
    public boolean updateUser(Connection p_con, Map p_newUserMap) throws BTSLBaseException {
        /*
         * update entries in following tables
         * MTX_PAYMENT_METHODS
         * MTX_WALLET
         */
       
        int updateCount = 0;
        final String METHOD_NAME = "updateUser";
        try {
            StringBuilder updateQuery = new StringBuilder();
            StringBuilder strBlackListUpdate = new StringBuilder();
            updateQuery.append("UPDATE mtx_payment_methods SET  payment_method_number=? ");
            updateQuery.append(" WHERE party_user_id=? ");
            // added by mohit for common msisdn usage on 15 july,2008
            updateQuery.append("AND user_type=?");
            String query = updateQuery.toString();
            

            try(PreparedStatement  pstmtUpdate = p_con.prepareStatement(query);)
            {
            String paymentMethodNumber = (String) ((Map) p_newUserMap.get("partyAccess")).get("msisdn");
            String strUserId = (String) ((Map) p_newUserMap.get("party")).get("userId");
            pstmtUpdate.setString(1, paymentMethodNumber);
            pstmtUpdate.setString(2, strUserId);
            // added by mohit for common msisdn usage on 15 july,2008
            pstmtUpdate.setString(3, "CHANNEL");

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                // update WALLET table
                updateQuery = new StringBuilder();
                updateQuery.append("UPDATE mtx_wallet SET  wallet_number=? ");
                updateQuery.append(" WHERE payment_method_id = ? ");
                query = updateQuery.toString();

                try(PreparedStatement  pstmtUpdate1 = p_con.prepareStatement(query);)
                {
                pstmtUpdate1.setString(1, paymentMethodNumber);
                pstmtUpdate1.setString(2, (String) p_newUserMap.get("paymentMethodId"));

                updateCount = pstmtUpdate1.executeUpdate();
                if (updateCount > 0) {
                	strBlackListUpdate.append("UPDATE MTX_PARTY_BLACK_LIST SET ACCOUNT_NUMBER=? " );
                    strBlackListUpdate.append(" WHERE PARTY_ID=?")  ;
                    String query2 = strBlackListUpdate.toString();
                    try(PreparedStatement pstmtUpdate2 = p_con.prepareStatement(query2);)
                    {
                    pstmtUpdate2.setString(1, paymentMethodNumber);
                    pstmtUpdate2.setString(2,strUserId);  
                    pstmtUpdate2.executeUpdate();
                    return true;
                }
                }
            }
            }
        }
        }catch (SQLException sqe) {

            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "updateUser", "ERROR_WHILE_UPDATING_WALLET",sqe);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "updateUser", "tango.error.general.processing",e);
        } finally {
        	_log.debug(METHOD_NAME, "Exiting..");
        }
        return false;
    }

    /**
     * Date : Sep 25, 2007
     * Discription :
     * Method : getPaymentMethodID
     * 
     * @param p_networkCode
     * @param p_currentDate
     * @return String
     */
    public String getPaymentMethodID(Connection con, String p_networkCode, Date p_currentDate) throws BTSLBaseException {
        String returnStr = null;
        final String METHOD_NAME = "getPaymentMethodID";
        try {
            long newTransferID = getNextID(con, "PM", BTSLUtil.getFinancialYearLastDigits(4), p_networkCode, p_currentDate);
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(newTransferID), 6);
            returnStr = "PM" + currentDateTimeFormatString(p_currentDate) + currentTimeFormatString(p_currentDate) + paddedTransferIDStr;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ConfigUtil[]", "", "", "", "Not able to generate Party ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method currentDateTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    private String currentDateTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    /**
     * Method currentTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    private String currentTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    public long getNextID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        long seriesNum = 0;
        long last_mod_date_minut = 0;
        long currentDateinMinut = 0;
        String frequency = null;
        Date moddate = null;
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean isInitialised = false;
        boolean isRecordFound = false;
        String sqlQuery = null;
        final String METHOD_NAME = "getNextID";
        // get the last_no, frequency and last_initialised_date from the
        // database wrt the id_year,id_type and network_code passed to it.
        // the frequency field is used to take deceision about to reset the
        // last_no field.
        // last_initialised_date is used to get the time of last initialisation
        // of the last_on field.

        // DB220120123for update WITH RS
        
        CommonUtilQry commonUtilQry=(CommonUtilQry)ObjectProducer.getObject(QueryConstants.COMMON_UTIL_QRY, QueryConstants.QUERY_PRODUCER);
              
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
            sqlQuery = "SELECT last_no,frequency,last_initialised_date FROM sys_ids ids WHERE id_year=? AND id_type=? AND grph_domain_code=? FOR UPDATE OF last_no WITH RS";
        else
            sqlQuery = commonUtilQry.getNextID();

        try {
            ps = p_con.prepareStatement(sqlQuery);
            ps.setString(1, p_year);
            ps.setString(2, p_idType);
            ps.setString(3, p_networkID);
            rs = ps.executeQuery();
            long currentTime = new Date().getTime();
            if (p_currentDate != null)
                p_currentDate.setTime(currentTime);
            else
                p_currentDate = new Date();
            if (rs.next()) {
                isRecordFound = true;
                seriesNum = rs.getLong(1);
                frequency = rs.getString(2);
                moddate = rs.getTimestamp(3);

            } // end of if rs.next()
            if (!isRecordFound) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }
            if (moddate != null) {
                Calendar cal = BTSLDateUtil.getInstance();
                cal.setTime(moddate);
                // get the time in minuts. here cal.getTime().getTime() return
                // the milli second
                // So we divide by 1000*60 to convert it into minuts.
                last_mod_date_minut = cal.getTime().getTime() / (1000 * 60);
                currentDateinMinut = p_currentDate.getTime() / (1000 * 60);
                // get the month of the date in the database for
                // last_initialised_date field.
                int initialisedMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of the date.
                int initialisedYear = cal.get(Calendar.YEAR);
                // set the calender for the current date
                cal.setTime(p_currentDate);
                // get the month of current date
                int curMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of current date.
                int curYear = cal.get(Calendar.YEAR);
                // if the frequency is minuts then we have to reset the last_no
                // on minuts basis.
                if (frequency.equals(PretupsI.FREQUENCY_MINUTS)) {
                    if ((currentDateinMinut - last_mod_date_minut) < 1)
                        seriesNum++;
                    else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is hours then we have to reset the last_no
                // on hourly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_HOUR)) {
                    long lastIntialisationHrs = last_mod_date_minut / 60;
                    long currtimeHrs = currentDateinMinut / 60;
                    if ((currtimeHrs - lastIntialisationHrs) < 1)
                        seriesNum++;
                    else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is day then we have to reset the last_no on
                // daily basis.
                else if (frequency.equals(PretupsI.FREQUENCY_DAY)) {
                    long lastIntialisationDay = last_mod_date_minut / (60 * 24);
                    long currtimeDay = currentDateinMinut / (60 * 24);
                    if ((currtimeDay - lastIntialisationDay) < 1)
                        seriesNum++;
                    else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is month then we have to reset the last_no
                // on monthly basis
                // here we first check the year of dates. if the year is same
                // then we check the month field
                else if (frequency.equals(PretupsI.FREQUENCY_MONTH)) {
                    if (curYear == initialisedYear) {
                        if (curMonth - initialisedMonth < 1)
                            seriesNum++;
                        else {
                            seriesNum = 1;
                            isInitialised = true;
                        }
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is year then we have to reset the last_no
                // on yearly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_YEAR)) {
                    if (curYear == initialisedYear) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else frequency is NA so we have not reset the last_no .
                else
                    seriesNum++;
            } else {
                isInitialised = true;
                seriesNum = 1;
            }

            StringBuffer query = new StringBuffer("UPDATE sys_ids SET last_no=?");
            // if initialisation is to be performed then we also update the
            // last_initialised_date in the ids table in database.
            if (isInitialised)
                query.append(", last_initialised_date=? ");
            query.append(" WHERE id_year=? AND id_type=? AND grph_domain_code=?");
            ps1 = p_con.prepareStatement(query.toString());
            int i = 0;
            ps1.setLong(++i, seriesNum);
            if (isInitialised)
                ps1.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            ps1.setString(++i, p_year);
            ps1.setString(++i, p_idType);
            ps1.setString(++i, p_networkID);
            int updateNum = ps1.executeUpdate();
            if (updateNum == 0) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }// end of updateNum==0
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "getNextID", "idgenerator.creation.error.norecord",sqle);
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getNextID", "tango.error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try{
                if (ps!= null){
                	ps.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            try{
                if (ps1!= null){
                	ps1.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
        } // end of finally
        return seriesNum;
    }
    
    
    public  String getUserTypeLookupCode(String userType){
        String userTypelookupCode="";
  
        // switch statement with int data type
        switch (userType) {
        case "CHANNEL":
        	userTypelookupCode = "CHU";
            break;
        case "STAFF":
        	userTypelookupCode = "STAFF";
            break;
        case "OPERATOR":
        	userTypelookupCode = "OPTU";
            break;
        default :
        	userTypelookupCode = "";
        }
        
        return userTypelookupCode;
        
    }
    
    
    
    
    
public void validateNetworkCode(String networkCode) throws BTSLBaseException {
	final String methodName="validateNetworkCode";
	if (networkCode != null && !networkCode.trim().equals(PretupsI.ALL.trim())) {
		NetworkVO networkVO = (NetworkVO) NetworkCache.getObject(networkCode);
		if (BTSLUtil.isNullObject(networkVO)) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
		}
	}

	
}
    
public void validateCategoryCode(String categoryCode,Connection con) throws BTSLBaseException {
	final String methodName="validateCategoryCode";
	CategoryDAO categoryDAO = new CategoryDAO();
	if (categoryCode != null
			&& !categoryCode.trim().toUpperCase().equals(PretupsI.ALL)) {
		CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
				categoryCode);
		if (BTSLUtil.isNullObject(categoryVO)) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
		}
	
	}
	
}


public void validateDomain(String domain,Connection con) throws BTSLBaseException {
	final String methodName="validateDomain";
		DomainDAO domainDAO = new DomainDAO();
	if (domain != null
	&& !domain.trim().equals(PretupsI.ALL)) {
	DomainVO domainVO = domainDAO.loadDomainVO(con, domain);
	if (BTSLUtil.isNullObject(domainVO)) {
	throw new BTSLBaseException("PretupsUIReportsController", methodName,
			PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
	}
	}
}



public void validateGeography(String geography,Connection con) throws BTSLBaseException {
final String methodName="validateGeography";
	  if(geography!=null && !geography.trim().equals(PretupsI.ALL)) {
		    GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
		    String geographyName =geoDAO.getGeographyName(con, geography, true);
		    
		    if (geographyName==null) {
		 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
		    }
	  }
}
	

    
	
    
    public void  validateInputs(Connection con,AddtnlCommSummryReqDTO addtnlCommSummryReqDTO) throws BTSLBaseException {
		final String methodName ="validateInputs";
		HashMap<String,String> reportInputKeyValMap= new HashMap<String,String>();
		Date currentDate = new Date();
		CategoryDAO categoryDAO = new CategoryDAO();
		DomainDAO domainDAO = new DomainDAO();
		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
		sdf.setLenient(false);

		Date frDate = new Date();
		Date tDate = new Date();
		String fromDate = addtnlCommSummryReqDTO.getFromDate();
		String toDate = addtnlCommSummryReqDTO.getToDate();
		String extNwCode = addtnlCommSummryReqDTO.getExtnwcode();
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_NETWORKCODE.getReportValues(), extNwCode);
		
		
	
		
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues(), addtnlCommSummryReqDTO.getCategoryCode());
		if (addtnlCommSummryReqDTO.getCategoryCode() != null
				&& !addtnlCommSummryReqDTO.getCategoryCode().trim().equals(PretupsI.ALL)) {
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
					addtnlCommSummryReqDTO.getCategoryCode());
			if (BTSLUtil.isNullObject(categoryVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues(), categoryVO.getCategoryName());
		}
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_SERVICE.getReportValues(), addtnlCommSummryReqDTO.getService());
		
	
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues(), addtnlCommSummryReqDTO.getDomain());
		if (addtnlCommSummryReqDTO.getDomain() != null
				&& !addtnlCommSummryReqDTO.getDomain().trim().equals(PretupsI.ALL)) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, addtnlCommSummryReqDTO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues(), domainVO.getDomainName());
		}
		
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_GEOGRAPHY.getReportValues(), addtnlCommSummryReqDTO.getDomain());
		if (addtnlCommSummryReqDTO.getDomain() != null
				&& !addtnlCommSummryReqDTO.getDomain().trim().equals(PretupsI.ALL)) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, addtnlCommSummryReqDTO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_GEOGRAPHY.getReportValues(), domainVO.getDomainName());
		}
		
	}
	
   public ListValueVO validationDistributionType(String distributionType) { 
	ArrayList lookUpList = LookupsCache.loadLookupDropDown(PretupsI.DISTRIBUTION_TYPE, true); //TRFT
	boolean found =false;
	ListValueVO  listValueVO=null;
	if(lookUpList!=null) { 
		for(int i=0;i<lookUpList.size();i++) {
			 if(((ListValueVO) lookUpList.get(i)).getValue().equals(distributionType)){
				 listValueVO = (ListValueVO) lookUpList.get(i);
		         break;
			 }
			
		}
		
	}
    return listValueVO;
   }
   
   public String createQueryINclause(int length) {
       String query = " (";
       StringBuilder queryBuilder = new StringBuilder(query);
       for (int i = 0; i < length; i++) {
           queryBuilder.append(" ?");
           if (i != length - 1)
               queryBuilder.append(",");
       }
       queryBuilder.append(")");
       return queryBuilder.toString();
   }
   
   
   public Object getFieldValue(Object instance, String fieldName) {
	   Field field = ReflectionUtils.findField(instance.getClass(), fieldName);
	   field.setAccessible(true);
	   try {
	     return field.get(instance);
	   }
	   catch (IllegalAccessException e) {
	     // ignore
		   
		   _log.errorTrace("getFieldValue", e);
	   }
	   return null;
	 }
   
   
   public String invokeMethodofClass(Object instance, String methodName) throws IllegalAccessException {
	   String returnData= null;
	   Method method = ReflectionUtils.findMethod(instance.getClass(), methodName);
	     if(ReflectionUtils.invokeMethod(method, instance)!=null) {
	    	 returnData=String.valueOf(ReflectionUtils.invokeMethod(method, instance));
	     }
	   
	   return returnData;
	 }
   
   public int getCurrentApprovalLevel(String approvalLevel) {
	   int returnVal;
		   switch (approvalLevel) {
		case PretupsI.CHANNEL_USER_APPROVE1:
			returnVal=1;
			break;
		case PretupsI.CHANNEL_USER_APPROVE2:
			returnVal=2;
			break;			
		case PretupsI.CHANNEL_USER_APPROVE3:
			returnVal=3;
			break;
		default:
			returnVal=0;
			break;
		}
		   
	   return returnVal;
	   
   }

	public  byte[] decodeFile(String base64value) throws BTSLBaseException {
		byte[] base64Bytes = null;
		try {
			_log.debug("decodeFile: ", base64value);
			base64Bytes = Base64.getMimeDecoder().decode(base64value);
			_log.debug("base64Bytes: ", base64Bytes);
		} catch (IllegalArgumentException il) {
			_log.debug("Invalid file format", il);
			_log.error("Invalid file format", il);
			_log.errorTrace("Invalid file format", il);
			throw new BTSLBaseException(this, "decodeFile", PretupsErrorCodesI.INVALID_FILE_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}
		return base64Bytes;
	}
	

	public String getNumberWord(int approvalLevel) {
		   String returnVal;
			   switch (approvalLevel) {
			case 0 :
				returnVal="Zero";
				break;
			case 1:
				returnVal="One";
				break;			
			case 2:
				returnVal="Two";
				break;
			case 3:
				returnVal="Three";
				break;	
			default:
				returnVal="Zero";
				break;
			}
			   
		   return returnVal;
		   
	   }

	
public void  ipAddressValidation(String allowedIPs) throws BTSLBaseException {
	final String METHOD_NAME ="ipAddressValidation";
	if (!BTSLUtil.isNullString(allowedIPs)) {
			String[] allowedIPAddress = allowedIPs.split(",");
			for (int i = 0; i < allowedIPAddress.length; i++) {
				String splitAllowedIP = allowedIPAddress[i];
				if (!BTSLUtil.isValidateIpAddress(splitAllowedIP)) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_IP_INVALID);
				}
			}
			
		}
}

public boolean validLookupCodeByLookupType(String lookupCode,String lookupType) {
	ArrayList outLetList = LookupsCache.loadLookupDropDown(lookupType, true);
	boolean flag = false;
	if(!BTSLUtil.isNullOrEmptyList(outLetList)) {
		for (int k = 0; k < outLetList.size(); k++) {
			if (((ListValueVO) outLetList.get(k)).getValue().equals(lookupCode)) {
				flag = true;
				break;
			}
		}
	}
	return flag;
}

public long timeDifference(String fromTime, String toTime) throws BTSLBaseException {
	final String methodName = "timeDifference";
	String startTime;
	String endTime;
	long difference = 0l;
	SimpleDateFormat format = new SimpleDateFormat(PretupsI.TIME_FORMAT);
	startTime = fromTime + PretupsI.TIME_SECONDS_SUFFIX;
	endTime = toTime +  PretupsI.TIME_SECONDS_SUFFIX;
	Date date1 = null;
	try {
		date1 = format.parse(startTime);
	} catch (ParseException pe) {
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FROMTIME_INVALID);
	}
	Date date2 = null;
	try {
		date2 = format.parse(endTime);
	} catch (ParseException pe) {
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_TOTIME_INVALID);
	}
	difference = date2.getTime() - date1.getTime();

	return difference / 1000;

}


public void checkDomainValues(String inputDomainCode,ArrayList<ListValueVO> listArrayList) throws BTSLBaseException {
	final String methodName="checkDomainValues";
	 ListValueVO listVO = listArrayList.stream()
			  .filter(listlVO -> listlVO.getValue().equals(inputDomainCode))
			  .findAny()
			  .orElse(null);
	  if(null==listVO) {
		  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_DOMAIN_CODE);  
	  }
	
}

public  boolean isSameDay(Date date1, Date date2) {
	String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
    SimpleDateFormat fmt = new SimpleDateFormat(systemDateFormat);
    return fmt.format(date1).equals(fmt.format(date2));
}


public  ArrayList getFileContentsList(LinkedHashMap<String, List<String>> bulkDataMap) {
	ArrayList<String> fileDataList = new ArrayList<String>();
	if(bulkDataMap != null && bulkDataMap.size()>0) {
		int size = bulkDataMap.size();
		int count = 0;
		String str;
		for (Entry<String, List<String>> entry : bulkDataMap.entrySet()) {
			count++;
			String key = entry.getKey();
			if(BTSLUtil.isNullString(key)) {
				break;
			}
		    ArrayList<String> list = (ArrayList<String>) entry.getValue();
		    
		    if(size == count ) {
		    for(int i=0;i<list.size();i++) {
		    	str = fileDataList.get(i)+list.get(i);
		    	fileDataList.remove(i);
		    	fileDataList.add(i,str );
		     } 
		   } else if(count == 1 ){
			   for(int i=0;i<list.size();i++) {
				   str =  list.get(i)+",";
			    	//fileDataList.remove(i);
			    	fileDataList.add(i,str );
			    } 
		   }else {
			   for(int i=0;i<list.size();i++) {
			    	str =  fileDataList.get(i)+list.get(i)+",";
			    	fileDataList.remove(i);
			    	fileDataList.add(i,str );
			    } 
		   }
		    
		}
	}

	return fileDataList;
}


/**
 * Method isContain.
 * This method checks that passed list contains passed msisdn or not
 * 
 * @param p_finalList
 *            ArrayList
 * @param p_msisdn
 *            String
 * @return boolean
 */
public boolean isContain(ArrayList p_finalList, String p_msisdn) {
    if (_log.isDebugEnabled()) {
    	_log.debug("isContain", "Entered p_msisdn=" + p_msisdn + ", p_finalList=" + p_finalList);
    }
    boolean flag = false;
    if (p_finalList != null) {
        RestrictedSubscriberVO resVO;
        int size = p_finalList.size();
        String arr[] = null;
        for (int i = 0; i < size; i++) {
            resVO = (RestrictedSubscriberVO) p_finalList.get(i);
            arr = resVO.getMsisdn().split(",");
            if (arr[0].equals(p_msisdn)) {
                flag = true;
                break;
            }
        }
    }
    if (_log.isDebugEnabled()) {
    	_log.debug("isContain", "Exit:flag=" + flag);
    }
    return flag;

}


public HashMap containsDuplicatePort(String portCommaSeperated) throws BTSLBaseException {
	final String methodName ="containsDuplicatePort";
	boolean duplicate=false;
	final StringTokenizer value = new StringTokenizer(portCommaSeperated, ",");
	final HashMap map = new HashMap();
    String series = null;
    while (value.hasMoreTokens()) {
        series = value.nextToken().trim();
        if (map.containsKey(series)) {
        	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DUPLICATE_PORT);
         }else {
        	map.put(series,series);
        }
    }  
	
	return map;
}



public boolean validatePortSeries(String commaSeperatedPort) throws BTSLBaseException {
	final String METHOD_NAME = "validatePortSeries";
	String value = null;
	boolean validate = true;
	value = commaSeperatedPort;
	if (value == null || value.length() == 0) {
		return true;
	}

	String portValue = null;
	try {

		String fieldname[] = { "Receiver Port" };
		if (value.charAt(0) == ',') {
			// errors.add(field.getKey(), new ActionMessage("error.firstvalueiscomma",
			// message.getValues())); reference
			throw new BTSLBaseException(this, "validatePortSeries", "error.firstvalueiscomma", fieldname);
		}
		if (value.charAt(value.length() - 1) == ',') {
			// errors.add(field.getKey(), new ActionMessage("error.lastvalueiscomma",
			// message.getValues()));
			throw new BTSLBaseException(this, "validatePortSeries", "error.lastvalueiscomma", fieldname);
		}
		if (value.contains(",,")) {
			// errors.add(field.getKey(), new ActionMessage("error.blankvalueincommas",
			// message.getValues()));
			throw new BTSLBaseException(this, "validatePortSeries", "error.blankvalueincommas", fieldname);

		}
		final StringTokenizer portParseString = new StringTokenizer(value, ",");
		while (portParseString.hasMoreTokens()) {
			portValue = (portParseString.nextToken());
			if (BTSLUtil.isNullString(portValue)) {
				validate = false;
				throw new BTSLBaseException(this, "validatePortSeries", "error.blankvalueinPort");
			} else {
				try {
					Long.parseLong(portValue);
				} catch (Exception e) {
					validate = false;
					throw new BTSLBaseException(this, "validatePortSeries", "error.invalidvalueinPort");
				}
			}
		}
	} catch (Exception e) {
		validate = false;
		throw e;
	}



	return validate;
}





public void validateversion(String version) throws BTSLBaseException {
	final String method_name = "validateversion";
	String value = null;
	value = version;
	if (value != null && value.trim().length() > 0) {
		try {

			String fieldname[] = { "Allowed Version" };
			if (value.charAt(0) == ',') {
				// errors.add(field.getKey(), new ActionMessage("error.firstvalueiscomma",
				// message.getValues())); reference
				throw new BTSLBaseException(this, method_name, "error.firstvalueiscomma", fieldname);
			}
			if (value.charAt(value.length() - 1) == ',') {
				// errors.add(field.getKey(), new ActionMessage("error.lastvalueiscomma",
				// message.getValues()));
				throw new BTSLBaseException(this, method_name, "error.lastvalueiscomma", fieldname);
			}
			if (value.contains(",,")) {
				// errors.add(field.getKey(), new ActionMessage("error.blankvalueincommas",
				// message.getValues()));
				throw new BTSLBaseException(this, method_name, "error.blankvalueincommas", fieldname);

			}

		} catch (Exception ex) {
			throw ex;
		}

	}

}

public void validateRegex(String actualRegExpression,String  fieldData,String fieldName ,String RegexName ) throws BTSLBaseException {
	if (fieldData != null && fieldData.trim().length() > 0) {
Pattern p = Pattern.compile(actualRegExpression);
Matcher m = p.matcher(fieldData);
String errorCode =null;
String placeHolders[] = { fieldName,RegexName };
if (!m.matches()) {
	throw new BTSLBaseException(this, "validateRegex", "error.invalidRegExinFieldValue",placeHolders);
}

	}
}




public String validateRegexWithMessage(String actualRegExpression,String  fieldData,String fieldName ,String RegexName ) throws BTSLBaseException {
	String errorMsg=null;
	if (fieldData != null && fieldData.trim().length() > 0) {
		Pattern p = Pattern.compile(actualRegExpression);
		Matcher m = p.matcher(fieldData);

		String placeHolders[] = { fieldName,RegexName };
		String errorCode ="error.invalidRegExinFieldValue";
		if (!m.matches()) {
			errorMsg =PretupsRestUtil.getMessageString(errorCode, placeHolders);
		}

	}
return errorMsg;
}



public void validateFieldComma(String fieldDataValue,String fieldName) throws BTSLBaseException {
	final String method_name = "validateFieldComma";
	String value = null;
	value = fieldDataValue;
	if (value != null && value.trim().length() > 0) {
		try {

			String fieldnameArry[] = { fieldName };
			if (value.charAt(0) == ',') {
				// errors.add(field.getKey(), new ActionMessage("error.firstvalueiscomma",
				// message.getValues())); reference
				throw new BTSLBaseException(this, method_name, "error.firstvalueiscomma", fieldnameArry);
			}
			if (value.charAt(value.length() - 1) == ',') {
				// errors.add(field.getKey(), new ActionMessage("error.lastvalueiscomma",
				// message.getValues()));
				throw new BTSLBaseException(this, method_name, "error.lastvalueiscomma", fieldnameArry);
			}
			if (value.contains(",,")) {
				// errors.add(field.getKey(), new ActionMessage("error.blankvalueincommas",
				// message.getValues()));
				throw new BTSLBaseException(this, method_name, "error.blankvalueincommas", fieldnameArry);

			}

		} catch (Exception ex) {
			throw ex;
		}

	}

}



public  String  compareMap1PortExistInDestMap(Map<String,String> srcMap, Map<String,String> destMap) {
	String portAlreadyUsed =null;
	 for(String ports :srcMap.values()) {
		 if(destMap.containsValue(ports)) {
			 portAlreadyUsed= ports;
			 break;
		 }
	 }
	
	return portAlreadyUsed;
	
}

    
}
