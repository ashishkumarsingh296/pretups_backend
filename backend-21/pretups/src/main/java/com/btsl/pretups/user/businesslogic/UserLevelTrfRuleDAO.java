package com.btsl.pretups.user.businesslogic;

/**
 * @(#)UserLevelTrfRuleDAO.java
 *                              Copyright(c) 2009, Comviva Technologies.
 *                              All Rights Reserved
 * 
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              gaurav pandey Sep 30 2009 Initial Creation for
 *                              Transfer Rule type at User level
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

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
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsResponseVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;

public class UserLevelTrfRuleDAO {
    private Log _log = LogFactory.getLog(UserLevelTrfRuleDAO.class.getName());
    private UserLevelTrfRuleQry userLevelTrfRuleQry = (UserLevelTrfRuleQry)ObjectProducer.getObject(QueryConstants.USER_LEVEL_TRF_RULE_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * @author gaurav pandey
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersListInSelfHierarchy(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" To Category Code: ");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(" User Name: ");
        	loggerValue.append(p_userName);
        	loggerValue.append(" ,p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(" ,p_receiverUserID=");
        	loggerValue.append(p_receiverUserID);
        	loggerValue.append(" ,p_isFromWeb=");
        	loggerValue.append(p_isFromWeb);
            _log.debug("loadUsersListInSelfHierarchy",loggerValue);
        }
        final String METHOD_NAME = "loadUsersListInSelfHierarchy";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersListAtSameLevel", "error.status.processing");
        }
        final ArrayList arrayList = new ArrayList();
        try {
        	pstmt=userLevelTrfRuleQry.loadUsersListInSelfHierarchyQry(receiverStatusAllowed, p_con, p_networkCode, p_toCategoryCode, p_userName, p_userID, p_receiverUserID, p_isFromWeb);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
            _log.error("loadUsersListInSelfHierarchy", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListInSelfHierarchy]",
                            "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersListInSelfHierarchy", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersListInSelfHierarchy", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListInSelfHierarchy]",
                            "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersListInSelfHierarchy", "error.general.processing",ex);
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
                _log.debug("loadUsersListInSelfHierarchy", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

    /**
     * @author gaurav pandey
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersListByOwner(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_ownerId, String p_userName, String p_userID, String p_sessionCatCode,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListByOwner",
                            "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + "p_ownerId: " + p_ownerId + 
                            " User Name: " + p_userName + " ,p_userID=" + p_userID + ", p_sessionCatCode = " + p_sessionCatCode+ " ,p_receiverUserID=" + p_receiverUserID +" ,p_isFromWeb=" + p_isFromWeb);
        }
        final String METHOD_NAME = "loadUsersListByOwner";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersListByOwner", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT user_id, user_name FROM USERS");
        strBuff.append(" WHERE network_code = ? AND status IN(" + receiverStatusAllowed + ") AND category_code = ? AND category_code <> ? AND owner_id=? AND user_type='"+ PretupsI.CHANNEL_USER_TYPE + "'");
        if (p_isFromWeb){
        	strBuff.append("  AND user_id != ? " );
        	strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ORDER BY user_name");
        }
        else{
        	strBuff.append("  AND user_id = ?");
        }

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListByOwner", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_sessionCatCode);
            pstmt.setString(++i, p_ownerId);
            if (p_isFromWeb){
            	pstmt.setString(++i, p_userID);

            	pstmt.setString(++i, p_userName);
            }
            else{
            	pstmt.setString(++i, p_receiverUserID);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
            _log.error("loadUsersListByOwner", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListByOwner]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersListByOwner", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersListByOwner", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListByOwner]", "", "",
                            "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersListByOwner", "error.general.processing",ex);
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
                _log.debug("loadUsersListByOwner", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

    /**
     * @author gaurav pandey
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID
     * @param p_ownerID
     *            TODO
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersListAtSameLevel(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID, String p_ownerID,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListAtSameLevel",
                            "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + " User Name: " + p_userName 
                            + " ,p_userID=" + p_userID + " ,p_ownerID=" + p_ownerID+ " ,p_receiverUserID=" + p_receiverUserID +" ,p_isFromWeb=" + p_isFromWeb);
        }
        final String METHOD_NAME = "loadUsersListAtSameLevel";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersListAtSameLevel", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT user_id, user_name FROM USERS");
        strBuff.append(" WHERE network_code = ? AND status IN (" + receiverStatusAllowed + ") AND category_code = ? AND owner_id=? AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "'");
        if (p_isFromWeb){
        	strBuff.append(" AND user_id != ? ");
        	strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ORDER BY user_name ");
        }
        else{
        	strBuff.append(" AND user_id = ? ");
        }

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListAtSameLevel", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_ownerID);
            if(p_isFromWeb){
            	pstmt.setString(++i, p_userID);

            	pstmt.setString(++i, p_userName);
            }
            else{
            	pstmt.setString(++i, p_receiverUserID);
            }
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
            _log.error("loadUsersListAtSameLevel", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListAtSameLevel]", "",
                            "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersListAtSameLevel", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersListAtSameLevel", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListAtSameLevel]", "",
                            "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersListAtSameLevel", "error.general.processing",ex);
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
                _log.debug("loadUsersListAtSameLevel", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

    /**
     * loadUsersByParentIDRecursive
     * This method load the users by Category Code without session category
     * code.
     * 
     * @author gaurav pandey
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_parentID
     * @param p_userName
     * @param p_userID
     *            String
     * @param p_sessionCatCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersByCatCodeAndWithoutSessionCatCode(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_sessionCatCode) throws BTSLBaseException {
        // Rule Type B (Rule Type C + within same Domain)
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersByCatCodeAndWithoutSessionCatCode",
                            "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + " p_parentID: " + p_parentID + "User Name: " + p_userName + " ,p_userID=" + p_userID + ", p_sessionCatCode = " + p_sessionCatCode);
        }
        final String METHOD_NAME = "loadUsersByCatCodeAndWithoutSessionCatCode";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCode", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT user_id, user_name FROM users  ");
        strBuff.append(" WHERE network_code = ? AND status IN (" + receiverStatusAllowed + ") AND category_code = ? AND (parent_id= ? OR category_code <> ?) AND user_id != ?");
        strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        strBuff.append(" ORDER BY user_name ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersByCatCodeAndWithoutSessionCatCode", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_parentID);
            pstmt.setString(++i, p_sessionCatCode);
            pstmt.setString(++i, p_userID);

            pstmt.setString(++i, p_userName);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
            _log.error("loadUsersByCatCodeAndWithoutSessionCatCode", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserLevelTrfRuleDAO[loadUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCode", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersByCatCodeAndWithoutSessionCatCode", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserLevelTrfRuleDAO[loadUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCode", "error.general.processing",ex);
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
                _log.debug("loadUsersByCatCodeAndWithoutSessionCatCode", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

    /**
     * This method load the users by Category Code
     * 
     * @author gaurav pandey
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_parentID
     * @param p_userName
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadUsersByCategoryCode(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException {
        // Rule Type B (Rule Type C + within same Domain)
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" To Category Code: ");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(" p_parentID: ");
        	loggerValue.append(p_parentID);
        	loggerValue.append("User Name: ");
        	loggerValue.append(p_userName);
        	loggerValue.append(" ,p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_receiverUserID=");
        	loggerValue.append(p_receiverUserID);
        	loggerValue.append(",p_isFromWeb=");
        	loggerValue.append(p_isFromWeb);
            _log.debug("loadUsersByCategoryCode",loggerValue);
        }
        final String METHOD_NAME = "loadUsersByCategoryCode";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersByCategoryCode", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT user_id, user_name FROM users  ");
        strBuff.append(" WHERE network_code = ? AND status IN (" + receiverStatusAllowed + ") AND category_code = ? ");
        strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        if(p_isFromWeb){
        	strBuff.append(" AND user_id != ? AND UPPER(user_name) LIKE UPPER(?) ");
        	strBuff.append(" ORDER BY user_name ");
        }
        else{
        	strBuff.append(" AND user_id = ?" );
        }
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersByCategoryCode", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);

            if(p_isFromWeb){
            	pstmt.setString(++i, p_userID);
            	pstmt.setString(++i, p_userName);
            }
            else{
            	pstmt.setString(++i, p_receiverUserID);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
            _log.error("loadUsersByCategoryCode", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersByCategoryCode]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCategoryCode", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersByCategoryCode", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersByCategoryCode]", "", "",
                            "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCategoryCode", "error.general.processing",ex);
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
                _log.debug("loadUsersByCategoryCode", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

    // Changes made for transfer rule at user level

    /**
     * @author ankur.dhawan
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID
     * @return LinkedHashMap
     * @throws BTSLBaseException
     */

    public LinkedHashMap loadBatchUsersListInSelfHierarchy(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersListInSelfHierarchy", "Entered with p_networkCode=" + p_networkCode + " p_toCategoryCode=" + p_toCategoryCode + " p_userID=" + p_userID);
        }
        final String METHOD_NAME = "loadBatchUsersListInSelfHierarchy";

        final LinkedHashMap userMap = new LinkedHashMap();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

			String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
			boolean tcpOn = false;
			Set<String> uniqueTransProfileId = new HashSet();

			if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
				tcpOn = true;
			}
			String sqlSelect = null;

			if (tcpOn) {
				/*pstmt = userLevelTrfRuleQry.loadBatchUsersListInSelfHierarchyTcpQry(p_con, p_networkCode, p_toCategoryCode,
						p_userID);*/
			} else {

				pstmt = userLevelTrfRuleQry.loadBatchUsersListInSelfHierarchyQry(p_con, p_networkCode, p_toCategoryCode,
						p_userID);
			}
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setStatus(rs.getString("user_status"));
                
                if(!tcpOn) {
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                }else {
                	
                	//TRANSFER_PROFILE_ID
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                	
                }
                channelVO.setCommissionProfileStatus(rs.getString("comm_prof_status"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileApplicableFrom(rs.getTimestamp("applicable_from"));
                userMap.put(channelVO.getUserID(), channelVO);
            }
            

			if (tcpOn) {
				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
						ValueType.STRING);
				BTSLUtil.updateMapViaMicroServiceResultSet(userMap, BTSLUtil.fetchMicroServiceTCPDataByKey(
						new HashSet<String>(Arrays.asList("profile_id", "profile_Name", "status")), searchCriteria));

			}				
        } catch (SQLException sqle) {
            _log.error("loadBatchUsersListInSelfHierarchy", "SQLException :" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "SQL Exception : " + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersListInSelfHierarchy", "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error("loadBatchUsersListInSelfHierarchy", "Exception :" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "Exception : " + e.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersListInSelfHierarchy", "error.general.sql.processing",e);

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
                _log.debug("loadBatchUsersListInSelfHierarchy", "Exiting userMap Size =" + userMap.size());
            }
        }
        return userMap;
    }

    /**
     * @author ankur.dhawan
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_ownerId
     * @param p_userName
     * @param p_userID
     * @param p_sessionCatCode
     * @return LinkedHashMap
     * @throws BTSLBaseException
     */

    public LinkedHashMap loadBatchUsersListByOwner(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_ownerId, String p_userName, String p_userID, String p_sessionCatCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered with p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_toCategoryCode=");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(" p_ownerId=");
        	loggerValue.append(p_ownerId);
        	loggerValue.append(" p_userName=");
        	loggerValue.append(p_userName);
        	loggerValue.append(" p_sessionCatCode=");
        	loggerValue.append(p_sessionCatCode);
        	loggerValue.append(" p_userID=");
        	loggerValue.append(p_userID);
            _log.debug("loadBatchUsersListByOwner",loggerValue);
        }
        final String METHOD_NAME = "loadBatchUsersListByOwner";
        final LinkedHashMap userMap = new LinkedHashMap();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String selectQuery = userLevelTrfRuleQry.loadBatchUsersListByOwnerQry();
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersListByOwner", "Query selectQuery = " + selectQuery);
        }
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(selectQuery);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_sessionCatCode);
            pstmt.setString(++i, p_userID);
            pstmt.setString(++i, p_ownerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setStatus(rs.getString("user_status"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileStatus(rs.getString("comm_prof_status"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileApplicableFrom(rs.getTimestamp("applicable_from"));
                userMap.put(channelVO.getUserID(), channelVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadBatchUsersListByOwner", "SQLException :" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "SQL Exception : " + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersListByOwner", "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error("loadBatchUsersListByOwner", "Exception :" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "Exception : " + e.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersListByOwner", "error.general.sql.processing",e);

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
                _log.debug("loadBatchUsersListByOwner", "Exiting userMap Size =" + userMap.size());
            }
        }
        return userMap;
    }

    /**
     * @author ankur.dhawan
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID
     * @param p_ownerID
     * @return LinkedHashMap
     * @throws BTSLBaseException
     */

    public LinkedHashMap loadBatchUsersListAtSameLevel(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID, String p_ownerId) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersListAtSameLevel",
                            "Entered with p_networkCode=" + p_networkCode + " p_toCategoryCode=" + p_toCategoryCode + " p_ownerId=" + p_ownerId + " p_userName=" + p_userName + " p_userID=" + p_userID);
        }
        final String METHOD_NAME = "loadBatchUsersListAtSameLevel";
        final LinkedHashMap userMap = new LinkedHashMap();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String selectQuery = userLevelTrfRuleQry.loadBatchUsersListAtSameLevelQry();
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersListAtSameLevel", "Query selectQuery = " + selectQuery);
        }
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(selectQuery);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_userID);
            pstmt.setString(++i, p_ownerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setStatus(rs.getString("user_status"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileStatus(rs.getString("comm_prof_status"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileApplicableFrom(rs.getTimestamp("applicable_from"));
                userMap.put(channelVO.getUserID(), channelVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadBatchUsersListAtSameLevel", "SQLException :" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "SQL Exception : " + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersListAtSameLevel", "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error("loadBatchUsersListAtSameLevel", "Exception :" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "Exception : " + e.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersListAtSameLevel", "error.general.sql.processing",e);

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
                _log.debug("loadBatchUsersListAtSameLevel", "Exiting userMap Size =" + userMap.size());
            }
        }
        return userMap;
    }

    /**
     * loadUsersByParentIDRecursive
     * This method load the users by Category Code without session category
     * code.
     * 
     * @author ankur.dhawan
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_parentID
     * @param p_userName
     * @param p_userID
     *            String
     * @param p_sessionCatCode
     *            String
     * @return LinkedHashMap
     * @throws BTSLBaseException
     */

    public LinkedHashMap loadBatchUsersByCatCodeAndWithoutSessionCatCode(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_sessionCatCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersByCatCodeAndWithoutSessionCatCode",
                            "Entered with p_networkCode=" + p_networkCode + " p_toCategoryCode=" + p_toCategoryCode + " p_parentID=" + p_parentID + " p_userName=" + p_userName + " p_userID=" + p_userID + " p_sessionCatCode=" + p_sessionCatCode);
        }
        final String METHOD_NAME = "loadBatchUsersByCatCodeAndWithoutSessionCatCode";
        final LinkedHashMap userMap = new LinkedHashMap();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String selectQuery = userLevelTrfRuleQry.loadBatchUsersByCatCodeAndWithoutSessionCatCodeQry();
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersByCatCodeAndWithoutSessionCatCode", "Query selectQuery = " + selectQuery);
        }
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(selectQuery);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_parentID);
            pstmt.setString(++i, p_sessionCatCode);
            pstmt.setString(++i, p_userID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setStatus(rs.getString("user_status"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileStatus(rs.getString("comm_prof_status"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileApplicableFrom(rs.getTimestamp("applicable_from"));
                userMap.put(channelVO.getUserID(), channelVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadBatchUsersByCatCodeAndWithoutSessionCatCode", "SQLException :" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "SQL Exception : " + sqle.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCode", "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error("loadBatchUsersByCatCodeAndWithoutSessionCatCode", "Exception :" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "Exception : " + e.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCode", "error.general.sql.processing",e);

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
                _log.debug("loadBatchUsersByCatCodeAndWithoutSessionCatCode", "Exiting userMap Size =" + userMap.size());
            }
        }
        return userMap;
    }

    /**
     * This method load the users by Category Code
     * 
     * @author ankur.dhawan
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID
     * @return LinkedHashMap
     * @throws BTSLBaseException
     */

    public LinkedHashMap loadBatchUsersByCategoryCode(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered with p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_toCategoryCode=");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(" p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(" p_userName=");
        	loggerValue.append(p_userName);
            _log.debug("loadBatchUsersByCategoryCode",loggerValue);
        }
        final String METHOD_NAME = "loadBatchUsersByCategoryCode";
        final LinkedHashMap userMap = new LinkedHashMap();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String selectQuery = userLevelTrfRuleQry.loadBatchUsersByCategoryCodeQry();
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersByCategoryCode", "Query selectQuery = " + selectQuery);
        }
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(selectQuery);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_userID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setStatus(rs.getString("user_status"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileStatus(rs.getString("comm_prof_status"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileApplicableFrom(rs.getTimestamp("applicable_from"));
                userMap.put(channelVO.getUserID(), channelVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadBatchUsersByCategoryCode", "SQLException :" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "SQL Exception : " + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersByCategoryCode", "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error("loadBatchUsersByCategoryCode", "Exception :" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "C2CBatchTransferDAO[loadBatchUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "Exception : " + e.getMessage());
            throw new BTSLBaseException(this, "loadBatchUsersByCategoryCode", "error.general.sql.processing",e);

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
                _log.debug("loadBatchUsersByCategoryCode", "Exiting userMap Size =" + userMap.size());
            }
        }
        return userMap;
    }

    public ArrayList loadUsersByCategoryCodeForLoginID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_loginID, String p_userID,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException{
		// Rule Type B (Rule Type C + within same Domain)
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" To Category Code: ");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(" p_parentID: ");
        	loggerValue.append(p_parentID);
        	loggerValue.append("Login ID: ");
        	loggerValue.append(p_loginID);
        	loggerValue.append(" ,p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(",p_receiverUserID=");
        	loggerValue.append(p_receiverUserID);
        	loggerValue.append(",p_isFromWeb=");
        	loggerValue.append(p_isFromWeb);
            _log.debug("loadUsersByCategoryCode",loggerValue);
        }
        final String METHOD_NAME = "loadUsersByCategoryCodeForLoginID";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersByCategoryCode", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT user_id, login_id, user_name,msisdn FROM users  ");
        strBuff.append(" WHERE network_code = ? AND status IN (" + receiverStatusAllowed + ") AND category_code = ? ");
        strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        if(p_isFromWeb){
        	strBuff.append(" AND user_id != ? AND UPPER(login_id) LIKE UPPER(?) ");
        	strBuff.append(" ORDER BY login_id ");
        }
        else{
        	strBuff.append(" AND user_id = ?" );
        }
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersByCategoryCode", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);

            if(p_isFromWeb){
            	pstmt.setString(++i, p_userID);
            	pstmt.setString(++i, p_loginID);
            }
            else{
            	pstmt.setString(++i, p_receiverUserID);
            }
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
            _log.error("loadUsersByCategoryCode", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersByCategoryCode]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCategoryCode", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersByCategoryCode", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersByCategoryCode]", "", "",
                            "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCategoryCode", "error.general.processing",ex);
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
                _log.debug("loadUsersByCategoryCodeForLoginID", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
	}
	
	public ArrayList loadUsersByCatCodeAndWithoutSessionCatCodeForLoginID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_loginID, String p_userID, String p_sessionCatCode) throws BTSLBaseException {
        // Rule Type B (Rule Type C + within same Domain)
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersByCatCodeAndWithoutSessionCatCode",
                            "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + " p_parentID: " + p_parentID + "Login ID: " + p_loginID + " ,p_userID=" + p_userID + ", p_sessionCatCode = " + p_sessionCatCode);
        }
        final String METHOD_NAME = "loadUsersByCatCodeAndWithoutSessionCatCodeForLoginID";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCodeForLoginID", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT user_id, login_id,user_name,msisdn FROM users  ");
        strBuff.append(" WHERE network_code = ? AND status IN (" + receiverStatusAllowed + ") AND category_code = ? AND (parent_id= ? OR category_code <> ?) AND user_id != ?");
        strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        strBuff.append(" AND UPPER(login_id) LIKE UPPER(?) ");
        strBuff.append(" ORDER BY login_id ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersByCatCodeAndWithoutSessionCatCodeForLoginID", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_parentID);
            pstmt.setString(++i, p_sessionCatCode);
            pstmt.setString(++i, p_userID);

            pstmt.setString(++i, p_loginID);
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
            _log.error("loadUsersByCatCodeAndWithoutSessionCatCode", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserLevelTrfRuleDAO[loadUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCode", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersByCatCodeAndWithoutSessionCatCode", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserLevelTrfRuleDAO[loadUsersByCatCodeAndWithoutSessionCatCode]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersByCatCodeAndWithoutSessionCatCode", "error.general.processing",ex);
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
                _log.debug("loadUsersByCatCodeAndWithoutSessionCatCodeForLoginID", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

	public ArrayList loadUsersListAtSameLevelForLoginID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_loginID, String p_userID, String p_ownerID,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListAtSameLevel",
                            "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + " Login ID: " + p_loginID 
                            + " ,p_userID=" + p_userID + " ,p_ownerID=" + p_ownerID+ " ,p_receiverUserID=" + p_receiverUserID +" ,p_isFromWeb=" + p_isFromWeb);
        }
        final String METHOD_NAME = "loadUsersListAtSameLevelForLoginID";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersListAtSameLevelForLoginID", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT user_id, login_id,msisdn, user_name FROM USERS");
        strBuff.append(" WHERE network_code = ? AND status IN (" + receiverStatusAllowed + ") AND category_code = ? AND owner_id=? AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "'");
        if (p_isFromWeb){
        	strBuff.append(" AND user_id != ? ");
        	strBuff.append(" AND UPPER(login_id) LIKE UPPER(?) ORDER BY login_id ");
        }
        else{
        	strBuff.append(" AND user_id = ? ");
        }

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListAtSameLevelForLoginID", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_ownerID);
            if(p_isFromWeb){
            	pstmt.setString(++i, p_userID);

            	pstmt.setString(++i, p_loginID);
            }
            else{
            	pstmt.setString(++i, p_receiverUserID);
            }
            
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
            _log.error("loadUsersListAtSameLevel", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListAtSameLevel]", "",
                            "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersListAtSameLevel", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersListAtSameLevel", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListAtSameLevel]", "",
                            "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersListAtSameLevel", "error.general.processing",ex);
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
                _log.debug("loadUsersListAtSameLevelForLoginID", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }
	
	public ArrayList loadUsersListByOwnerForLoginID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_ownerId, String p_loginID, String p_userID, String p_sessionCatCode,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListByOwner",
                            "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + "p_ownerId: " + p_ownerId + 
                            " Login ID: " + p_loginID + " ,p_userID=" + p_userID + ", p_sessionCatCode = " + p_sessionCatCode+ " ,p_receiverUserID=" + p_receiverUserID +" ,p_isFromWeb=" + p_isFromWeb);
        }
        final String METHOD_NAME = "loadUsersListByOwnerForLoginID";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersListByOwnerForLoginID", "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT user_id, login_id,user_name,msisdn FROM USERS");
        strBuff.append(" WHERE network_code = ? AND status IN(" + receiverStatusAllowed + ") AND category_code = ? AND category_code <> ? AND owner_id=? AND user_type='"+ PretupsI.CHANNEL_USER_TYPE + "'");
        if (p_isFromWeb){
        	strBuff.append("  AND user_id != ? " );
        	strBuff.append(" AND UPPER(login_id) LIKE UPPER(?) ORDER BY login_id");
        }
        else{
        	strBuff.append("  AND user_id = ?");
        }

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadUsersListByOwnerForLoginId", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_toCategoryCode);
            pstmt.setString(++i, p_sessionCatCode);
            pstmt.setString(++i, p_ownerId);
            if (p_isFromWeb){
            	pstmt.setString(++i, p_userID);

            	pstmt.setString(++i, p_loginID);
            }
            else{
            	pstmt.setString(++i, p_receiverUserID);
            }
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
            _log.error("loadUsersListByOwner", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListByOwner]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersListByOwnerForLoginID", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersListByOwner", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListByOwner]", "", "",
                            "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersListByOwnerForLoginID", "error.general.processing",ex);
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
                _log.debug("loadUsersListByOwnerForLoginID", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }
	
	public ArrayList loadUsersListInSelfHierarchyForLoginID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_loginID, String p_userID,String p_receiverUserID, boolean p_isFromWeb) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  Network Code: ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" To Category Code: ");
        	loggerValue.append(p_toCategoryCode);
        	loggerValue.append(" Login ID: ");
        	loggerValue.append(p_loginID);
        	loggerValue.append(" ,p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(" ,p_receiverUserID=");
        	loggerValue.append(p_receiverUserID);
        	loggerValue.append(" ,p_isFromWeb=");
        	loggerValue.append(p_isFromWeb);
            _log.debug("loadUsersListInSelfHierarchyForLoginID",loggerValue);
        }
        final String METHOD_NAME = "loadUsersListInSelfHierarchyForLoginID";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        // user life cycle
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
                        PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersListInSelfHierarchyForLoginID", "error.status.processing");
        }
        final ArrayList arrayList = new ArrayList();
        try {
        	pstmt=userLevelTrfRuleQry.loadUsersListInSelfHierarchyQryForLoginID(receiverStatusAllowed, p_con, p_networkCode, p_toCategoryCode, p_loginID, p_userID, p_receiverUserID, p_isFromWeb);
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
            _log.error("loadUsersListInSelfHierarchyForLoginID", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListInSelfHierarchy]",
                            "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUsersListInSelfHierarchyForLoginID", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            _log.error("loadUsersListInSelfHierarchyForLoginID", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLevelTrfRuleDAO[loadUsersListInSelfHierarchy]",
                            "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUsersListInSelfHierarchyForLoginID", "error.general.processing",ex);
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
                _log.debug("loadUsersListInSelfHierarchyForLoginID", "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }
    
}
