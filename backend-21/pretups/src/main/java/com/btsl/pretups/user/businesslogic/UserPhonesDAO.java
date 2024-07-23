/**
 * @(#)UserPhonesDAO.java
 *                        Copyright(c) 2011, Comviva Technologies Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Samna Soin 14/11/2011 Initial Creation
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 */

package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * 
 */
public class UserPhonesDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(UserPhonesDAO.class.getName());
    private UserPhonesQry userPhonesQry = (UserPhonesQry)ObjectProducer.getObject(QueryConstants.USER_PHONES_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * This method will first check the MSISDN in DB and if it exists then
     * update the last transaction id field of the user_phones table
     * 
     * @param p_con
     *            Connection
     * @param p_transactionId
     *            String
     * @param p_usrMsisdn
     *            String
     * @return the number of records updated
     * @throws BTSLBaseException
     */

    public String updateBulkTransactionId(Connection p_con, String p_transactionId, ArrayList p_msisdnList) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("updateBulkTransactionId ", " Entered ");
        }
        final String METHOD_NAME = "updateBulkTransactionId";
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rsSelect = null;
        int updateCount = 0;
        final StringBuffer invalidMsisdn = new StringBuffer();
        ListValueVO listValueVO = null;

        try {
            final String updateQry = "UPDATE user_phones SET temp_transfer_id=? WHERE user_id=? AND msisdn=?";
            final String selectQry = "SELECT UP.user_id FROM user_phones UP,users U WHERE UP.msisdn=? and U.user_id=UP.user_id and U.status NOT IN ('N','C','W')";
            if (_log.isDebugEnabled()) {
                _log.debug(" updateBulkTransactionId ", "Update Query :: " + updateQry);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(" updateBulkTransactionId ", "Select Query :: " + selectQry);
            }
            pstmtUpdate = p_con.prepareStatement(updateQry);
            pstmtSelect = p_con.prepareStatement(selectQry);
            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                listValueVO = (ListValueVO) p_msisdnList.get(i);
                pstmtSelect.setString(1, listValueVO.getValue());
                rsSelect = pstmtSelect.executeQuery();
                while (rsSelect.next()) {
                    pstmtUpdate.setString(1, p_transactionId);
                    pstmtUpdate.setString(2, rsSelect.getString("user_id"));
                    pstmtUpdate.setString(3, listValueVO.getValue());
                    updateCount = pstmtUpdate.executeUpdate();
                }
                if (updateCount <= 0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("updateBulkTransactionId", "Data for this MSISDN can not be updated" + listValueVO.getValue());
                    }
                    invalidMsisdn.append(listValueVO.getValue());
                    invalidMsisdn.append(",");
                }
                updateCount = -1;
                pstmtSelect.clearParameters();
                pstmtUpdate.clearParameters();
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error("updateBulkTransactionId", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[updateBulkTransactionId]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateBulkTransactionId", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateBulkTransactionId", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[updateBulkTransactionId]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateBulkTransactionId", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (rsSelect != null) {
                    rsSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            if (_log.isDebugEnabled()) {
                _log.debug("updateBulkTransactionId ", " Exiting:invalidMsisdn=" + invalidMsisdn);
            }
        }
        return invalidMsisdn.toString();
    }

    /**
     * This method will select the MSISDN of those channel_users having
     * autoFocAllow status set and exists in required network and domain
     * 
     * @param p_con
     *            Connection
     * @return list of selected MSISDN
     * @throws BTSLBaseException
     */
    public String getAutoFocAllowedMsisdnList(Connection p_con, String networkId, String domainList) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug(" getAutoFocAllowedmsisdnList ", " Entered ");
        }
        final String METHOD_NAME = "getAutoFocAllowedMsisdnList";
        PreparedStatement pstmtSelect = null;
        ResultSet rsSelect = null;
        PreparedStatement pstmtSelect2 = null;
        ResultSet rsSelect2 = null;
        String autoFocAllowedMsisdnList = null;
        autoFocAllowedMsisdnList = "";
        int flag = 0;
        int index1 = 0;

        try {
            final String domain = domainList.replaceAll("'", "");
            final String d = domain.replaceAll("\" ", "");
            final String m_domain[] = d.split(",");

            networkId = "'" + networkId + "'";
            final StringBuffer strBuff = new StringBuffer();
            final StringBuffer strBuff2 = new StringBuffer();
            // FIRST SELECT QUERY
            strBuff.append("Select CATEGORY_CODE FROM CATEGORIES WHERE DOMAIN_CODE IN(");
            for (int i = 0; i < m_domain.length; i++) {
                strBuff.append(" ?");
                if (i != m_domain.length - 1) {
                    strBuff.append(",");
                }
            }
            strBuff.append(")");
            // String
            // selectQry1="Select CATEGORY_CODE FROM CATEGORIES WHERE DOMAIN_CODE IN("+domainList+")";
            final String selectQry1 = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" getAutoFocAllowedMsisdnList ", "Select Query1 :: " + selectQry1);
            }
            pstmtSelect = p_con.prepareStatement(selectQry1);
            for (int x = 0; x < m_domain.length; x++) {
                pstmtSelect.setString(++index1, m_domain[x]);
            }
            rsSelect = pstmtSelect.executeQuery();
            String categoryList = "";
            while (rsSelect.next()) {
                categoryList += "'" + rsSelect.getString("category_code") + "',";
            }
            categoryList = categoryList.substring(0, categoryList.length() - 1);
            final String categoryList1 = categoryList.replaceAll("'", "");
            final String pc = categoryList1.replaceAll("\" ", "");
            final String m_categoryList[] = pc.split(",");
            pstmtSelect.clearParameters();
            // Resource leak fixed
            // pstmtSelect=null;
            // rsSelect=null;

            // SECOND SELECT QUERY

            strBuff2.append("Select UP.msisdn FROM user_phones UP, users U, channel_users CU ");
            strBuff2.append("WHERE UP.user_id = CU.user_id AND U.user_id = CU.user_id AND U.status='Y' AND CU.AUTO_FOC_ALLOW = 'Y' ");
            strBuff2.append("and U.NETWORK_CODE=?");
            strBuff2.append("and U.CATEGORY_CODE IN(");
            for (int i = 0; i < m_categoryList.length; i++) {
                strBuff2.append(" ?");
                if (i != m_categoryList.length - 1) {
                    strBuff2.append(",");
                }
            }
            strBuff2.append(")");
            // String selectQry2 =
            // "Select UP.msisdn FROM user_phones UP, users U, channel_users CU WHERE UP.user_id = CU.user_id AND U.user_id = CU.user_id AND U.status='Y' AND CU.AUTO_FOC_ALLOW = 'Y' and U.NETWORK_CODE="+networkId+"and U.CATEGORY_CODE IN("+categoryList+")";
            final String selectQry2 = strBuff2.toString();
            index1 = 0;
            if (_log.isDebugEnabled()) {
                _log.debug(" getAutoFocAllowedmsisdnList ", "Select Query2 :: " + selectQry2);
            }

            pstmtSelect2 = p_con.prepareStatement(selectQry2);
            pstmtSelect2.setString(++index1, networkId);
            for (int x = 0; x < m_categoryList.length; x++) {
                pstmtSelect2.setString(++index1, m_categoryList[x]);
            }
            rsSelect2 = pstmtSelect2.executeQuery();
            while (rsSelect2.next()) {
                flag = 1;
                autoFocAllowedMsisdnList += rsSelect2.getString("msisdn") + ",";
            }
            if (flag == 0) {
                if (_log.isDebugEnabled()) {
                    _log.debug(" getAutoFocAllowedmsisdnList ", "No MSISDN has Auto FOC Allow status 'Y' in the database");
                }
            }
            autoFocAllowedMsisdnList = autoFocAllowedMsisdnList.substring(0, autoFocAllowedMsisdnList.length() - 1);
        } catch (SQLException sqe) {
            _log.error("getAutoFocAllowedmsisdnList", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);

        } catch (Exception e) {
            _log.error("getAutoFocAllowedmsisdnList", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (pstmtSelect2 != null) {
                    pstmtSelect2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (rsSelect != null) {
                    rsSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (rsSelect2 != null) {
                    rsSelect2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            if (_log.isDebugEnabled()) {
                _log.debug("getAutoFocAllowedmsisdnList ", " Exiting:AutoFocAllowedmsisdnList=" + autoFocAllowedMsisdnList);
            }
        }
        return autoFocAllowedMsisdnList;
    }

    /**
     * This method will set autoFocAllow status of the channel_users having
     * MSISDN in specified list of MSISDN and exists
     * in required network and domain
     * It also unset autoFocAllow status of the channel_users having MSISDN in
     * not specified list of MSISDN but exists in
     * required network and domain and already have status set
     * 
     * @param p_con
     *            Connection
     * @param p_msisdnList
     *            arraylist
     * @return String:No. & list of MSISDN(s) updated(set & unset)
     * @throws BTSLBaseException
     */
    public String updateAutoFOCChannelUsersList(Connection p_con, ArrayList p_msisdnList, String networkId, String domainList) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("updateAutoFOCChannelUsersList ", " Entered p_msisdnList " + p_msisdnList.size());
        }
        final String METHOD_NAME = "updateAutoFOCChannelUsersList";
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate2 = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect2 = null;
        PreparedStatement pstmtSelect3 = null;
        ResultSet rsSelect = null;
        ResultSet rsSelect2 = null;
        ResultSet rsSelect3 = null;
        int updateCount = 0;
        String updatedMsisdnList = "";
        try {
            String msisdnList = "";
            int flag1 = 0;
            int flag2 = 0;
            int index1 = 0;
            String userid1 = "";
            String userid2 = "";
            final StringBuffer strBuff1 = new StringBuffer();
            final StringBuffer strBuff2 = new StringBuffer();
            final StringBuffer strBuff3 = new StringBuffer();
            final StringBuffer strBuff4 = new StringBuffer();
            final StringBuffer strBuff5 = new StringBuffer();
            final String domain = domainList.replaceAll("'", "");
            final String d = domain.replaceAll("\" ", "");
            final String m_domain[] = d.split(",");
            if (p_msisdnList.size() > 0) {
                for (int i = 0; i < p_msisdnList.size(); i++) {
                    if (i == p_msisdnList.size() - 1) {
                        msisdnList += "'" + (String) p_msisdnList.get(i) + "'";
                    } else {
                        msisdnList += "'" + (String) p_msisdnList.get(i) + "',";
                    }
                }
            } else {
                msisdnList = "'None'";
                // networkId="'"+networkId+"'";
            }

            // FIRST SELECT QUERY
            // String
            // selectQry1="Select CATEGORY_CODE FROM CATEGORIES WHERE DOMAIN_CODE IN("+domainList+")";
            strBuff1.append("Select CATEGORY_CODE FROM CATEGORIES WHERE DOMAIN_CODE IN(");
            for (int i = 0; i < m_domain.length; i++) {
                strBuff1.append(" ?");
                if (i != m_domain.length - 1) {
                    strBuff1.append(",");
                }
            }
            strBuff1.append(")");
            final String selectQry1 = strBuff1.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" updateAutoFOCChannelUsersList ", "Select Query1 :: " + selectQry1);
            }
            pstmtSelect = p_con.prepareStatement(selectQry1);
            for (int x = 0; x < m_domain.length; x++) {
                pstmtSelect.setString(++index1, m_domain[x]);
            }
            rsSelect = pstmtSelect.executeQuery();
            String categoryList = "";
            while (rsSelect.next()) {
                categoryList += "'" + rsSelect.getString("category_code") + "',";
            }
            categoryList = categoryList.substring(0, categoryList.length() - 1);
            final String categoryList1 = categoryList.replaceAll("'", "");
            final String pc = categoryList1.replaceAll("\" ", "");
            final String m_categoryList[] = pc.split(",");
            pstmtSelect.clearParameters();
            // Removal of resource leak
            // pstmtSelect=null;
            // rsSelect=null;

            // SECOND SELECT QUERY
            index1 = 0;
            final String msisdnList1 = msisdnList.replaceAll("'", "");
            final String ml = msisdnList1.replaceAll("\" ", "");
            final String m_msisdnList1[] = ml.split(",");
            // String selectQry2 =
            // "SELECT U.user_id, UP.msisdn FROM user_phones UP, channel_users CU, users U WHERE UP.user_id=CU.user_id and U.user_id=CU.user_id and U.status='Y' and CU.AUTO_FOC_ALLOW='N' and U.NETWORK_CODE="+networkId+"and U.CATEGORY_CODE IN("+categoryList+") and UP.msisdn IN("+msisdnList+")";
            strBuff2.append("SELECT U.user_id, UP.msisdn FROM user_phones UP, channel_users CU, users U ");
            strBuff2.append("WHERE UP.user_id=CU.user_id and U.user_id=CU.user_id and U.status='Y' and CU.AUTO_FOC_ALLOW='N' and U.NETWORK_CODE=?");
            strBuff2.append(" and U.CATEGORY_CODE IN(");
            for (int i = 0; i < m_categoryList.length; i++) {
                strBuff2.append(" ?");
                if (i != m_categoryList.length - 1) {
                    strBuff2.append(",");
                }
            }
            strBuff2.append(")");
            strBuff2.append(" and UP.msisdn IN(");
            for (int i = 0; i < m_msisdnList1.length; i++) {
                strBuff2.append(" ?");
                if (i != m_msisdnList1.length - 1) {
                    strBuff2.append(",");
                }
            }
            strBuff2.append(")");
            final String selectQry2 = strBuff2.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" updatemsisdnList ", "Select Query2 :: " + selectQry2);
            }
            pstmtSelect2 = p_con.prepareStatement(selectQry2);
            pstmtSelect2.setString(++index1, networkId);
            for (int x = 0; x < m_categoryList.length; x++) {
                pstmtSelect2.setString(++index1, m_categoryList[x]);
            }
            for (int x = 0; x < m_msisdnList1.length; x++) {
                pstmtSelect2.setString(++index1, m_msisdnList1[x]);
            }

            rsSelect2 = pstmtSelect2.executeQuery();
            while (rsSelect2.next()) {
                flag1 = 1;
                userid1 += "'" + rsSelect2.getString("user_id") + "',";
                updatedMsisdnList += rsSelect2.getString("msisdn") + ", ";
                p_msisdnList.remove(rsSelect2.getString("msisdn"));
            }

            pstmtSelect2.clearParameters();
            // Resource leak fixed here
            // pstmtSelect=null;
            // rsSelect=null;

            // THIRD SELECT QUERY
            index1 = 0;
            // String selectQry3 =
            // "SELECT U.user_id,UP.msisdn FROM user_phones UP, channel_users CU, users U WHERE UP.user_id=CU.user_id and U.user_id=CU.user_id and U.status='Y' and CU.AUTO_FOC_ALLOW='Y' and U.NETWORK_CODE="+networkId
            // +"and U.CATEGORY_CODE IN("+categoryList+")and UP.msisdn NOT IN("+msisdnList+")";
            strBuff3.append("SELECT U.user_id,UP.msisdn FROM user_phones UP, channel_users CU, users U ");
            strBuff3.append("WHERE UP.user_id=CU.user_id and U.user_id=CU.user_id and U.status='Y' and CU.AUTO_FOC_ALLOW='Y' and U.NETWORK_CODE=?");
            strBuff3.append("and U.CATEGORY_CODE IN(");
            for (int i = 0; i < m_categoryList.length; i++) {
                strBuff3.append(" ?");
                if (i != m_categoryList.length - 1) {
                    strBuff3.append(",");
                }
            }
            strBuff3.append(")");
            strBuff3.append("and UP.msisdn NOT IN(");
            for (int i = 0; i < m_msisdnList1.length; i++) {
                strBuff3.append(" ?");
                if (i != m_msisdnList1.length - 1) {
                    strBuff3.append(",");
                }
            }
            strBuff3.append(")");
            final String selectQry3 = strBuff3.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" updatemsisdnList ", "Select Query3 :: " + selectQry3);
            }

            pstmtSelect3 = p_con.prepareStatement(selectQry3);

            pstmtSelect3.setString(++index1, networkId);
            for (int x = 0; x < m_categoryList.length; x++) {
                pstmtSelect3.setString(++index1, m_categoryList[x]);
            }
            for (int x = 0; x < m_msisdnList1.length; x++) {
                pstmtSelect3.setString(++index1, m_msisdnList1[x]);
            }
            rsSelect3 = pstmtSelect3.executeQuery();
            while (rsSelect3.next()) {
                flag2 = 1;
                userid2 += "'" + rsSelect3.getString("user_id") + "',";
                updatedMsisdnList += rsSelect3.getString("msisdn") + ", ";
                p_msisdnList.remove(rsSelect3.getString("msisdn"));
            }

            if (flag1 == 1) {
                index1 = 0;
                userid1 = userid1.substring(0, userid1.length() - 1);
                final String userid11 = userid1.replaceAll("'", "");
                final String u = userid11.replaceAll("\" ", "");
                final String m_userid1[] = u.split(",");
                // String updateQry =
                // "UPDATE channel_users SET AUTO_FOC_ALLOW = 'Y' WHERE user_id IN ("+
                // userid1 + ")";
                strBuff4.append("UPDATE channel_users SET AUTO_FOC_ALLOW = 'Y' WHERE user_id IN (");
                for (int i = 0; i < m_userid1.length; i++) {
                    strBuff4.append(" ?");
                    if (i != m_userid1.length - 1) {
                        strBuff4.append(",");
                    }
                }
                strBuff4.append(")");
                final String updateQry = strBuff4.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(" updatemsisdnList ", "Update Query :: " + updateQry);
                }
                pstmtUpdate = p_con.prepareStatement(updateQry);
                for (int x = 0; x < m_userid1.length; x++) {
                    pstmtUpdate.setString(++index1, m_userid1[x]);
                }
                updateCount = pstmtUpdate.executeUpdate();
                _log.debug("updatemsisdnList", "Status for " + updateCount + "MSISDN are set to status 'Y' ");
                pstmtUpdate.clearParameters();
                // Resource leak fixed
                // pstmtUpdate=null;
            }
            if (flag2 == 1) {
                index1 = 0;
                userid2 = userid2.substring(0, userid2.length() - 1);
                final String userid12 = userid2.replaceAll("'", "");
                final String u2 = userid12.replaceAll("\" ", "");
                final String m_userid12[] = u2.split(",");
                // String updateQry =
                // "UPDATE channel_users SET AUTO_FOC_ALLOW = 'N' WHERE user_id IN ("+
                // userid2 + ")";
                strBuff5.append("UPDATE channel_users SET AUTO_FOC_ALLOW = 'N' WHERE user_id IN (");
                for (int i = 0; i < m_userid12.length; i++) {
                    strBuff5.append(" ?");
                    if (i != m_userid12.length - 1) {
                        strBuff5.append(",");
                    }
                }
                strBuff5.append(")");
                final String updateQry = strBuff5.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(" updateAutoFOCChannelUsersList ", "Update Query :: " + updateQry);
                }
                pstmtUpdate2 = p_con.prepareStatement(updateQry);
                for (int x = 0; x < m_userid12.length; x++) {
                    pstmtUpdate2.setString(++index1, m_userid12[x]);
                }
                updateCount += pstmtUpdate2.executeUpdate();
                _log.debug("updateAutoFOCChannelUsersList", "Status for " + updateCount + "MSISDN are set to status 'N'");
                pstmtUpdate2.clearParameters();
            }
            pstmtSelect3.clearParameters();
            if (updateCount > 0) {
                updatedMsisdnList = updatedMsisdnList.substring(0, updatedMsisdnList.length() - 2);
                updatedMsisdnList += ":" + Integer.toString(updateCount);
            } else {
                updatedMsisdnList = Integer.toString(updateCount);
            }
            if (p_msisdnList.size() > 0) {
                updatedMsisdnList += "#";
                for (int i = 0; i < p_msisdnList.size(); i++) {
                    updatedMsisdnList += (String) p_msisdnList.get(i) + ", ";
                }
                updatedMsisdnList = updatedMsisdnList.substring(0, updatedMsisdnList.length() - 2);
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error("updatemsisdnList", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[updateAutoFOCChannelUsersList]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateAutoFOCChannelUsersList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updatemsisdnList", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[updateAutoFOCChannelUsersList]", "", "",
                            "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updatemsisdnList", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (pstmtUpdate2 != null) {
                    pstmtUpdate2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (pstmtSelect2 != null) {
                    pstmtSelect2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (pstmtSelect3 != null) {
                    pstmtSelect3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (rsSelect != null) {
                    rsSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (rsSelect2 != null) {
                    rsSelect2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                if (rsSelect3 != null) {
                    rsSelect3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            if (_log.isDebugEnabled()) {
                _log.debug("updateAutoFOCChannelUsersList ", " Exiting updateCount = " + updateCount);
            }
        }
        return updatedMsisdnList;

    }

    /**
     * This method will Update USER_PHONES table with OPT created at the time of
     * User PIN reset service
     * 
     * @param p_con
     *            Connection
     * @param p_channelUserVO
     * @return Update count
     * @throws BTSLBaseException
     */
    public int pinResetOTP(Connection p_con, UserPhoneVO p_userPhoneVO) throws BTSLBaseException {
        final String METHOD_NAME = "pinReserOTP";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered with USER_PHONES_ID=" + p_userPhoneVO.getUserPhonesId());
        }

        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        String otp = null;

        try {

            otp = BTSLUtil.encryptText(p_userPhoneVO.getOTP());

            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE user_phones SET OTP=?,OTP_CREATED_ON=? WHERE USER_PHONES_ID=?");

            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" pinReserOTP ", "Update Query :: " + updateQuery);
            }
            psmtUpdate = p_con.prepareStatement(updateQuery);

            int i = 1;
            psmtUpdate.setString(i++, otp);
            psmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            psmtUpdate.setString(i++, p_userPhoneVO.getUserPhonesId());
            updateCount = psmtUpdate.executeUpdate();

        }// end of try
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[pinReserOTP]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * This method selects OTP on the basis of MSISDN for User PIN Reset OTP
     * verification
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            MSISDN
     * @return OTP
     * @throws BTSLBaseException
     */
    public ArrayList<Object> verifyOTP(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "verifyOTP";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered with MSISDN=" + p_msisdn);
        }

        PreparedStatement psmt = null;
        ResultSet rs = null;
        final ArrayList<Object> list = new ArrayList<Object>();

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT OTP, OTP_CREATED_ON  ");
            strBuff.append(" FROM USER_PHONES WHERE MSISDN=?");

            final String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" verifyOTP ", "Select Query :: " + selectQuery);
            }

            psmt = (PreparedStatement) p_con.prepareStatement(selectQuery);

            psmt.setString(1, p_msisdn);
            rs = psmt.executeQuery();
            while (rs.next()) {
                final Object o1 = BTSLUtil.decryptText(rs.getString("OTP"));
                final Object o2 = rs.getTimestamp("OTP_CREATED_ON");
                list.add(o1);
                list.add(o2);
                // System.out.println("datwe format"
                // +rs.getTimestamp("OTP_CREATED_ON"));
            }

        }// end of try
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[verifyOTP]", "", "", "",
                            "Exception:" + e.getMessage());
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:  arrayList Size =" + list);
            }
        }
        return list;
    }

    /**
     * This method selects PIN on the basis of MSISDN for User data updation API
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            MSISDN
     * @return PIN
     * @throws BTSLBaseException
     */
    public String loadPin(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "loadPin";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered with MSISDN=" + p_msisdn);
        }

        PreparedStatement psmt = null;
        ResultSet rs = null;
        String pin = null;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT SMS_PIN  ");
            strBuff.append(" FROM USER_PHONES WHERE MSISDN=?");

            final String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" loadPin ", "Select Query :: " + selectQuery);
            }

            psmt = (PreparedStatement) p_con.prepareStatement(selectQuery);

            psmt.setString(1, p_msisdn);
            rs = psmt.executeQuery();
            while (rs.next()) {
                pin = BTSLUtil.decryptText(rs.getString("SMS_PIN"));
            }

        }// end of try
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[verifyOTP]", "", "", "",
                            "Exception:" + e.getMessage());
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:  PIN =" + pin);
            }
        }
        return pin;
    }

    /**
     * This method selects PIN on the basis of MSISDN for User data updation API
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            MSISDN
     * @return PIN
     * @throws BTSLBaseException
     */
    public String loadUserPhonesID(Connection p_con, ChannelUserVO p_channeluserVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadPin";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered with p_channeluserVO=" + p_channeluserVO);
        }

        PreparedStatement psmt = null;
        ResultSet rs = null;
        String user_phones_id = null;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT USER_PHONES_ID  ");
            strBuff.append(" FROM USER_PHONES WHERE MSISDN=? AND USER_ID=?");

            final String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(" loadPin ", "Select Query :: " + selectQuery);
            }

            psmt = (PreparedStatement) p_con.prepareStatement(selectQuery);

            int i = 1;
            psmt.setString(i++, p_channeluserVO.getMsisdn());
            psmt.setString(i++, p_channeluserVO.getUserID());
            rs = psmt.executeQuery();

            while (rs.next()) {
                user_phones_id = rs.getString("USER_PHONES_ID");
            }

        }// end of try
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserPhonesDAO[loadUserPhonesID]", "", "", "",
                            "Exception:" + e.getMessage());
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
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:  PIN =" + user_phones_id);
            }
        }
        return user_phones_id;
    }
    /**
     * This method select for update  status_auto_c2c on the basis of MSISDN and user_id
     * @param p_con
     * @param vo
     * @return
     * @throws BTSLBaseException
     */
    public boolean previousAutoC2CStatus(Connection p_con,LowBalanceAlertVO vo)throws BTSLBaseException{
        boolean status = false;
        final String METHOD_NAME = "previousAutoC2CStatus";
        LogFactory.printLog(METHOD_NAME, "Entered", _log);
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try{
            String query = userPhonesQry.previousAutoC2CStatusQry();
            LogFactory.printLog(METHOD_NAME, "Query:" + query, _log);
            pstmt = p_con.prepareStatement(query);
            pstmt.setString(1, vo.getUserId());
            pstmt.setString(2, vo.getMsisdn());
            rst = pstmt.executeQuery();
            while (rst.next()){
                if (ProcessI.STATUS_COMPLETE.equals(rst.getString("status_auto_c2c"))|| BTSLUtil.isNullString(rst.getString("status_auto_c2c"))){
                    status = true;
                }
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"UserPhonesDAO[previousAutoC2CStatus]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("UserPhonesDAO", METHOD_NAME,"error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"UserPhonesDAO[previousAutoC2CStatus]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException("UserPhonesDAO", METHOD_NAME,"error.general.processing");
        } finally {
        	try{
            	if (rst!= null){
            		rst.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
            try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(METHOD_NAME, " Exiting status " + status, _log);
        }
        return status;
    }
    
    /**
     * This method updates status_auto_c2c on the basis of MSISDN and user_id
     * @param p_con
     * @param vo
     * @param p_status
     * @throws BTSLBaseException
     */
    public int updateAutoC2CStatus(Connection p_con,LowBalanceAlertVO vo, String p_status)throws BTSLBaseException{
        final String METHOD_NAME = "updateAutoC2CStatus";
        LogFactory.printLog(METHOD_NAME, "Entered", _log);
        
        ResultSet rst = null;
        int update_count = 0;
        try {
            StringBuilder queryBuf = new StringBuilder("");
            queryBuf.append(" update user_phones UP ");
            queryBuf.append(" set status_auto_c2c= ? ");
            queryBuf.append(" where up.user_id = ? and up.msisdn = ?");
            String query = queryBuf.toString();
            LogFactory.printLog(METHOD_NAME, "Query:" + query, _log);
            try(PreparedStatement pstmt = p_con.prepareStatement(query);)
            {
            pstmt.setString(1, p_status);
            pstmt.setString(2, vo.getUserId());
            pstmt.setString(3, vo.getMsisdn());
            update_count = pstmt.executeUpdate();
        } 
        }catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"UserPhonesDAO[updateAutoC2CStatus]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("UserPhonesDAO", METHOD_NAME,"error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"UserPhonesDAO[updateAutoC2CStatus]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException("UserPhonesDAO", METHOD_NAME,"error.general.processing");
        } finally {
        	try{
            	if (rst!= null){
            		rst.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        
            LogFactory.printLog(METHOD_NAME," Exiting update_Count " + update_count,_log);
        }
        return update_count;
    }

}