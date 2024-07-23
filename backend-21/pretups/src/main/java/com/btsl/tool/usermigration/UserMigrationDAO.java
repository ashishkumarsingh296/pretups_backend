package com.btsl.tool.usermigration;

/**
 * @(#)UserMigrationDAO.java
 *                           Copyright(c) 2010, Comviva Technologies Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Vinay Singh June 05,2010 Initial Creation
 *                           Ashish Kumar Todia June 14,2010 Modification.
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.user.businesslogic.UserCategoryVO;
import com.btsl.pretups.user.businesslogic.UserGeoDomainVO;
import com.btsl.pretups.user.businesslogic.UserMessageVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class UserMigrationDAO {
    private static String dir = null;
    private static File fileObjectSuccess = null;
    private static File fileObjectFail = null;
    private static File fileObjectParentChildFail = null;
    private static FileWriter fwriterSucess = null;
    private static FileWriter fwriterFail = null;
    private static FileWriter fwriterParentChildFail = null;
    public static final String MESG_CHILD = "191919";
    public static final String MESG_PARENT = "191920";
    /**
     * Commons Logging instance.
     */
    private static Log LOG = LogFactory.getLog(UserMigrationDAO.class.getName());
    private UserMigrationToolQry userMigrationToolQry = (UserMigrationToolQry)ObjectProducer.getObject(QueryConstants.USER_MIGRATION_TOOL_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * Method for checking Is MSISDN exist or not.
     * 
     * @param p_con
     *            Connection
     * @param p_userMigrationList
     *            ArrayList<FromUserVO>
     * @param p_validUserList
     *            ArrayList<FromUserVO>
     * @param p_errorUserList
     *            ArrayList<FromUserVO>
     * @throws BTSLBaseException
     */
    public ArrayList<UserMigrationVO> validateFromUsers(Connection p_con, List<UserMigrationVO> p_userMigrationList, List<UserMigrationVO> p_errorUserList) throws BTSLBaseException {
        final String methodName = "validateFromUsers";
        UserMigrDetailLog.log(methodName, "", "Entered: p_fromUserList.size()=" + p_userMigrationList.size());
        PreparedStatement pstmt = null;
        PreparedStatement pendingTxnPstmt = null;
        PreparedStatement ActiveChildCount = null;
        PreparedStatement operatorUserPstmt = null;
        ResultSet rs = null;
        ResultSet rsPendingTxn = null;
        ResultSet rsOperatorUser = null;
        StringBuilder strBuff = null;
        StringBuilder strPendingTxnBuff = null;
        StringBuilder strActiveChildCount = null;
        UserMigrationVO userMigrationVO = null;
        String sqlSelectParent = null;
        PreparedStatement pstmtSelectParent = null;
        ArrayList<UserMigrationVO> validUsersList = new ArrayList<UserMigrationVO>();
        ResultSet rsSelectParent = null;
        StringBuilder strBuffSelectParent = null;

        try {
            strBuff = new StringBuilder();
            strBuff.append(" SELECT u.user_name,u.user_id, u.login_id,u.parent_id, u.network_code,u.status,");
            strBuff.append(" up.user_name,p.phone_language,p.country,pct.sequence_no, pct.grph_domain_type");
            strBuff.append(" FROM USERS U,CATEGORIES CT,USERS UP,USER_PHONES P,CATEGORIES PCT");
            strBuff.append(" WHERE u.category_code=ct.category_code AND P.USER_ID=U.USER_ID");
            strBuff.append(" AND u.user_type='CHANNEL' AND u.status<>'N'and u.status<>'C'");
            strBuff.append(" AND ct.category_code=? AND u.msisdn=? ");
            strBuff.append(" AND pct.category_code=?");
            strBuff.append(" AND (up.USER_ID=Case  WHEN u.parent_id = 'ROOT' then u.user_id else u.parent_id end)");

            String sqlSelect = strBuff.toString();
            UserMigrDetailLog.log(methodName, "", "QUERY sqlSelect=" + sqlSelect);

            strActiveChildCount = new StringBuilder();
            strActiveChildCount.append(" SELECT count(1) abc ");
            strActiveChildCount.append(" FROM USERS u WHERE ");
            strActiveChildCount.append(" u.user_type='CHANNEL' AND u.status<>'N' and u.status<>'C'");
            strActiveChildCount.append(" AND u.parent_id=? ");
            String sqlActiveChildCount = strActiveChildCount.toString();
            UserMigrDetailLog.log("validateChildCounts", "", "QUERY sqlActiveChildCount=" + sqlActiveChildCount);

            strPendingTxnBuff = new StringBuilder();
            strPendingTxnBuff.append(" SELECT 1  FROM CHANNEL_TRANSFERS");
            strPendingTxnBuff.append(" WHERE (from_user_id=? OR to_user_id =?) AND ");
            strPendingTxnBuff.append(" (status <> ? AND status <> ? )");

            String sqlPendingSelect = strPendingTxnBuff.toString();
            UserMigrDetailLog.log(methodName, "", "QUERY sqlPendingSelect=" + sqlPendingSelect);

            // Check for OPERATOR user
            StringBuilder sqlOptSelBuff = new StringBuilder();
            sqlOptSelBuff.append(" SELECT 1 FROM CATEGORIES cat");
            sqlOptSelBuff.append(" WHERE  cat.status<> 'N' AND cat.domain_code <>'OPT' AND cat.category_code=?");

            String sqlOptSelect = sqlOptSelBuff.toString();
            UserMigrDetailLog.log(methodName, "", "QUERY sqlOptSel=" + sqlOptSelect);

            strBuffSelectParent = new StringBuilder();
            strBuffSelectParent.append(" SELECT U.user_id,U.parent_id, U.owner_id, U.status, U.user_name,");
            strBuffSelectParent.append(" U.category_code,CAT.domain_code");
            strBuffSelectParent.append(" FROM USERS U, CATEGORIES CAT");
            strBuffSelectParent.append(" WHERE U.status <>'N' and U.status <>'C'");
            strBuffSelectParent.append(" AND U.category_code=CAT.category_code");
            strBuffSelectParent.append(" AND U.msisdn=?");

            sqlSelectParent = strBuffSelectParent.toString();
            UserMigrDetailLog.log(methodName, "", "QUERY sqlSelectParent=" + sqlSelectParent);

            // todia
            String checkDomain = "select DOMAIN_CODE from categories where STATUS='Y' and CATEGORY_CODE=?";

            try(PreparedStatement psmtCheckDomain = p_con.prepareStatement(checkDomain)){
            ResultSet rsCheckDomain = null;
            String toUserDomain = null;
            String toParentDomain = null;

            pstmt = p_con.prepareStatement(sqlSelect);
            pendingTxnPstmt = p_con.prepareStatement(sqlPendingSelect);
            ActiveChildCount = p_con.prepareStatement(sqlActiveChildCount);
            operatorUserPstmt = p_con.prepareStatement(sqlOptSelect);
            pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);
            for (int i = 0, j = p_userMigrationList.size(); i < j; i++) {
                userMigrationVO = p_userMigrationList.get(i);
                pstmt.setString(1, userMigrationVO.getFromUserCatCode());
                pstmt.setString(2, userMigrationVO.getFromUserMsisdn());
                pstmt.setString(3, userMigrationVO.getToUserCatCode());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    userMigrationVO.setFromUserID(rs.getString("user_id"));
                    userMigrationVO.setFromUserLoginID(rs.getString("login_id"));
                    userMigrationVO.setNetworkCode(rs.getString("network_code"));
                    userMigrationVO.setFromUserStatus(rs.getString("status"));
                    userMigrationVO.setFromUserParentName(rs.getString("USER_NAME"));
                    userMigrationVO.setFromUserParentId(rs.getString("parent_id"));
                    userMigrationVO.setPhoneLang(rs.getString("PHONE_LANGUAGE"));
                    userMigrationVO.setCountry(rs.getString("COUNTRY"));
                    userMigrationVO.setFromUserName(rs.getString("user_name"));
                    userMigrationVO.setToUserCatCodeSeqNo(rs.getString("sequence_no"));
                    userMigrationVO.setToGeoDomainType(rs.getString("grph_domain_type"));

                    pendingTxnPstmt.setString(1, userMigrationVO.getFromUserID());
                    pendingTxnPstmt.setString(2, userMigrationVO.getFromUserID());
                    pendingTxnPstmt.setString(3, "CLOSE");
                    pendingTxnPstmt.setString(4, "CNCL");
                    rsPendingTxn = pendingTxnPstmt.executeQuery();
                    if (rsPendingTxn.next()) {
                        userMigrationVO.setMessage("Pending transaction is found for the user.");
                        p_errorUserList.add(userMigrationVO);
                    } else {
                        operatorUserPstmt.setString(1, userMigrationVO.getToUserCatCode());
                        rsOperatorUser = operatorUserPstmt.executeQuery();
                        if (rsOperatorUser.next()) {

                            // Now validate the to parent user
                            pstmtSelectParent.setString(1, userMigrationVO.getToParentMsisdn());
                            rsSelectParent = pstmtSelectParent.executeQuery();
                            if (rsSelectParent.next()) {
                                // If To user category sequence is "1", then
                                // after migration, user's parent will be ROOT
                                if ("1".equals(userMigrationVO.getToUserCatCodeSeqNo())) {
                                    userMigrationVO.setToParentID("ROOT");
                                } else
                                    userMigrationVO.setToParentID(rsSelectParent.getString("user_id"));
                                userMigrationVO.setToOwnerID(rsSelectParent.getString("owner_id"));
                                userMigrationVO.setToUserParentName(rsSelectParent.getString("USER_NAME"));
                                psmtCheckDomain.setString(1, userMigrationVO.getToUserCatCode()); // todia
                                try{
                                rsCheckDomain = psmtCheckDomain.executeQuery();
                                while (rsCheckDomain.next()) {
                                    toUserDomain = rsCheckDomain.getString("DOMAIN_CODE");
                                }
                                }
                                finally{
                                	if(rsCheckDomain!=null)
                                		rsCheckDomain.close();
                                }
                                psmtCheckDomain.setString(1, userMigrationVO.getToParentCatCode());
                                try{
                                rsCheckDomain = psmtCheckDomain.executeQuery();
                                while (rsCheckDomain.next()) {
                                    toParentDomain = rsCheckDomain.getString("DOMAIN_CODE");
                                }
                                }
                                finally{
                                	if(rsCheckDomain!=null)
                                		rsCheckDomain.close();
                                }
                                // shishupal
                                ActiveChildCount.setString(1, userMigrationVO.getFromUserID());
                                try{
                                rsCheckDomain = ActiveChildCount.executeQuery();
                                if (rsCheckDomain.next()) {
                                    userMigrationVO.setActiveChildUserCount(rsCheckDomain.getString(1));
                                } else {
                                    userMigrationVO.setActiveChildUserCount("0");
                                }
                                } finally{
                                	if(rsCheckDomain!=null)
                                		rsCheckDomain.close();
                                }
                                UserMigrDetailLog.log(methodName, "", "Exiting with userMigrationVO.setActiveChildUserCount=" + userMigrationVO.getActiveChildUserCount() + "  userMigrationVO.getFromUserID()=" + userMigrationVO.getFromUserID());
                                // shishupal
                                if (toUserDomain.equalsIgnoreCase(toParentDomain))
                                    validUsersList.add(userMigrationVO);
                                else {

                                    userMigrationVO.setMessage("User can't be migrated as To parent Domain does not match To user Domain.");
                                    userMigrationVO.setParentExist(false);
                                    p_errorUserList.add(userMigrationVO);
                                }
                            } else {
                                userMigrationVO.setMessage("Parent user does not found in the system.");
                                userMigrationVO.setParentExist(false);
                                p_errorUserList.add(userMigrationVO);
                            }
                        } else {
                            userMigrationVO.setMessage("User catn't be migrated as an Operator user.");
                            userMigrationVO.setParentExist(false);
                            p_errorUserList.add(userMigrationVO);
                        }
                    }
                } else {
                    userMigrationVO.setMessage("User is not found.");
                    p_errorUserList.add(userMigrationVO);
                }
                pendingTxnPstmt.clearParameters();
                ActiveChildCount.clearParameters();
                pstmt.clearParameters();
                pstmtSelectParent.clearParameters();
                psmtCheckDomain.clearParameters();
            }
            return validUsersList;
        }
        }
        catch (SQLException sqe) {
            UserMigrDetailLog.log(methodName, "", "SQLException : " + sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            UserMigrDetailLog.log(methodName, "", "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (ActiveChildCount != null) {
                    ActiveChildCount.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsPendingTxn != null) {
                    rsPendingTxn.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pendingTxnPstmt != null) {
                    pendingTxnPstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectParent != null) {
                    pstmtSelectParent.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsSelectParent != null) {
                    rsSelectParent.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (operatorUserPstmt != null) {
                    operatorUserPstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsOperatorUser != null) {
                    rsOperatorUser.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            UserMigrDetailLog.log(methodName, "", "Exiting with validUsersList.size=" + validUsersList.size() + " p_errorUserList.size=" + p_errorUserList.size());
        }
    }

    /**
     * Method for loading the category in to a hash map.
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */
    public HashMap<String, UserCategoryVO> loadCategoryMap(Connection p_con, HashMap<String, HashMap<String, UserMessageVO>> p_profileGradeMap) throws BTSLBaseException {
        UserMigrDetailLog.log("loadCategoryMap", "", "Entered");
        final String METHOD_NAME = "loadCategoryMap";
        PreparedStatement pstmt = null;
        PreparedStatement commProfSetPstmt = null;
        PreparedStatement trfProfPstmt = null;
        PreparedStatement usrGrdPstmt = null;
        ResultSet rs = null;
        ResultSet rsCommProfSet = null;
        ResultSet rsTrfProf = null;
        ResultSet rsUsrGrd = null;
        StringBuilder strBuff = null;
        String catCode = null;
        String networkCode = null;
        UserCategoryVO userCatVO = null;
        UserMessageVO userMsgVO = null;
        HashMap<String, UserCategoryVO> catCodeMap = null;
        HashMap<String, UserMessageVO> commProfMap = null;
        HashMap<String, UserMessageVO> trfProfMap = null;
        HashMap<String, UserMessageVO> usrGrdMap = null;
        try {
            // Read the network code from the properties file
            networkCode = Constants.getProperty("NETWORK_CODE");
            if (BTSLUtil.isNullString(networkCode)) {
                UserMigrDetailLog.log(networkCode, "", "is not defined");
                throw new BTSLBaseException(this, "loadCategoryMap", "NETWORK_CODE is not defined in the file.");
            }
            strBuff = new StringBuilder();
            strBuff.append(" SELECT category_code, category_name, domain_code, sequence_no, grph_domain_type,");
            strBuff.append(" user_id_prefix, low_bal_alert_allow");
            strBuff.append(" FROM CATEGORIES ");
            strBuff.append(" WHERE status<>'N' ORDER BY category_code");

            String sqlSelect = strBuff.toString();
            UserMigrDetailLog.log("loadCategoryMap", "", "QUERY sqlSelect=" + sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);

            StringBuilder comprosetBuff = new StringBuilder();
            comprosetBuff.append(" SELECT category_code, comm_profile_set_id, comm_profile_set_name");
            comprosetBuff.append(" FROM COMMISSION_PROFILE_SET");
            comprosetBuff.append(" WHERE network_code = ? AND status<>'N'"); // Network
                                                                             // code
                                                                             // from
                                                                             // property
                                                                             // file

            String sqlCommPro = comprosetBuff.toString();
            UserMigrDetailLog.log("loadCategoryList", "", "QUERY sqlCommPro=" + sqlCommPro);
            commProfSetPstmt = p_con.prepareStatement(sqlCommPro);

            StringBuilder trfProfBuff = new StringBuilder();
            trfProfBuff.append(" SELECT category_code, profile_id, profile_name");
            trfProfBuff.append(" FROM TRANSFER_PROFILE");
            trfProfBuff.append(" WHERE network_code = ? AND status<>'N' and PARENT_PROFILE_ID='USER'"); // Network
                                                                                                        // code
                                                                                                        // from
                                                                                                        // property
                                                                                                        // file
            trfProfBuff.append(" ORDER BY category_code");

            String trfPro = trfProfBuff.toString();
            UserMigrDetailLog.log("loadCategoryMap", "", "QUERY trfPro=" + trfPro);
            trfProfPstmt = p_con.prepareStatement(trfPro);

            StringBuilder usrGrdBuff = new StringBuilder();
            usrGrdBuff.append(" SELECT category_code, grade_code, grade_name");
            usrGrdBuff.append(" FROM CHANNEL_GRADES ");
            usrGrdBuff.append(" WHERE status<>'N'");

            String userGrade = usrGrdBuff.toString();
            UserMigrDetailLog.log("loadCategoryMap", "", "QUERY userGrade=" + userGrade);
            usrGrdPstmt = p_con.prepareStatement(userGrade);
            // Load the category details
            rs = pstmt.executeQuery();
            catCodeMap = new HashMap<String, UserCategoryVO>();
            while (rs.next()) {
                userCatVO = new UserCategoryVO();
                catCode = rs.getString("category_code");
                userCatVO.setCategoryCode(catCode);
                userCatVO.setCategoryName(rs.getString("category_name"));
                userCatVO.setDomainCode(rs.getString("domain_code"));
                userCatVO.setSequenceNo(rs.getInt("sequence_no"));
                userCatVO.setGrphDomainType(rs.getString("grph_domain_type"));
                userCatVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                userCatVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                catCodeMap.put(catCode, userCatVO);
            }
            // Load the commission profile details
            commProfSetPstmt.setString(1, networkCode);
            rsCommProfSet = commProfSetPstmt.executeQuery();
            commProfMap = new HashMap<String, UserMessageVO>();
            while (rsCommProfSet.next()) {
                catCode = rsCommProfSet.getString("category_code");
                userMsgVO = new UserMessageVO(rsCommProfSet.getString("comm_profile_set_id"), rsCommProfSet.getString("comm_profile_set_name"));
                commProfMap.put(catCode, userMsgVO);
            }
            p_profileGradeMap.put("COMM_PROFILE", commProfMap);
            // Load transfer profile details
            trfProfPstmt.setString(1, networkCode);
            rsTrfProf = trfProfPstmt.executeQuery();
            trfProfMap = new HashMap<String, UserMessageVO>();
            catCode = null;
            while (rsTrfProf.next()) {
                catCode = rsTrfProf.getString("category_code");
                userMsgVO = new UserMessageVO(rsTrfProf.getString("profile_id"), rsTrfProf.getString("profile_name"));
                trfProfMap.put(catCode, userMsgVO);
            }
            p_profileGradeMap.put("TRF_PROFILE", trfProfMap);
            // Load user grades details
            rsUsrGrd = usrGrdPstmt.executeQuery();
            usrGrdMap = new HashMap<String, UserMessageVO>();
            catCode = null;
            while (rsUsrGrd.next()) {
           
                catCode = rsUsrGrd.getString("category_code");
                userMsgVO = new UserMessageVO(rsUsrGrd.getString("grade_code"), rsUsrGrd.getString("grade_name"));
                usrGrdMap.put(catCode, userMsgVO);

            }
            p_profileGradeMap.put("USER_GRADE", usrGrdMap);

            // return the category map

            return catCodeMap;
        } catch (SQLException sqe) {
            UserMigrDetailLog.log("loadCategoryMap", "", "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "validateFromUsers", "error.general.sql.processing");
        } catch (Exception ex) {
            UserMigrDetailLog.log("loadCategoryMap", "", "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (commProfSetPstmt != null) {
                    commProfSetPstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (trfProfPstmt != null) {
                    trfProfPstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (usrGrdPstmt != null) {
                    usrGrdPstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsCommProfSet != null) {
                    rsCommProfSet.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsTrfProf != null) {
                    rsTrfProf.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsUsrGrd != null) {
                    rsUsrGrd.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (catCodeMap != null) {
                UserMigrDetailLog.log("loadCategoryList", "", "Exiting with catCodeMap.size=" + catCodeMap.size());
            } else
                UserMigrDetailLog.log("loadCategoryList", "", "Exiting with catCodeMap.size=null");
        }
    }

    /**
     * Method for loading the category in to a hash map.
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */
    public HashMap<String, UserGeoDomainVO> loadGeoDomainCode(Connection p_con) throws BTSLBaseException {
        UserMigrDetailLog.log("loadGeoDomainCode", "", "Entered");
        final String METHOD_NAME = "loadGeoDomainCode";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff = null;
        String geoDomainCode = null;
        UserGeoDomainVO geoDomCodeVO = null;
        HashMap<String, UserGeoDomainVO> geoDomCodeMap = null;
        try {
            strBuff = new StringBuilder();
            strBuff.append(" SELECT grph_domain_code, network_code, grph_domain_name, parent_grph_domain_code,");
            strBuff.append(" grph_domain_short_name, status, grph_domain_type");
            strBuff.append(" FROM GEOGRAPHICAL_DOMAINS");
            strBuff.append(" WHERE status='Y'");

            String sqlSelect = strBuff.toString();
            UserMigrDetailLog.log("loadGeoDomainCode", "QUERY sqlSelect=", sqlSelect);

            geoDomCodeMap = new HashMap<String, UserGeoDomainVO>();
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                geoDomCodeVO = new UserGeoDomainVO();
                geoDomainCode = rs.getString("grph_domain_code");
                geoDomCodeVO.setGrphDomainCode(geoDomainCode);
                geoDomCodeVO.setNetworkCode(rs.getString("network_code"));
                geoDomCodeVO.setGrphDomainName(rs.getString("grph_domain_name"));
                geoDomCodeVO.setParentGrphDomainCode(rs.getString("parent_grph_domain_code"));
                geoDomCodeVO.setGrphDomainShortName(rs.getString("grph_domain_short_name"));
                geoDomCodeVO.setStatus(rs.getString("status"));
                geoDomCodeVO.setGrphDomainType(rs.getString("grph_domain_type"));

                geoDomCodeMap.put(geoDomainCode, geoDomCodeVO);
            }
            return geoDomCodeMap;
        } catch (SQLException sqe) {
            UserMigrDetailLog.log("loadGeoDomainCode", "", "SQLException : " + sqe);
            LOG.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "loadGeoDomainCode", "error.general.sql.processing");
        } catch (Exception ex) {
            UserMigrDetailLog.log("loadGeoDomainCode", "", "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
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
            UserMigrDetailLog.log("loadGeoDomainCode", "", "Exiting with geoDomCodeMap.size=" + geoDomCodeMap.size());
        }
    }

    /**
     * This method will migrate the user one by one as per the below steps:
     * 1. Check whether the new GRPH_DOMAIN_CODE(GEOGRAPHICAL_DOMAINS) is exist
     * in hierarchy, if not, then user
     * migration will be flag based.
     * 2. Soft delete of current user.
     * 3. Insert into USERS table for the migrated data (Created_on=old date,
     * Modified_on=current data).
     * a. Old user: reference_id=new_user_id, login_id=user_id
     * b. New user: reference_id=odl_user_id, login_id=old_login_id
     * 4. Update CHANNEL_USERS table for the below columns:
     * a. TRANSFER_PROFILE_ID
     * b. USER_GRADE
     * c. COMM_PROFILE_SET_ID
     * d. LOW_BAL_ALERT_ALLOW
     * e. ACTIVATED_ON - Old date
     * 5. Insert into USER_PHONES
     * 6. Insert into USER_SERVICES
     * 7. Insert into USER_ROLES (System roles, Group roles"- Default).
     * 8. C2C transfer for new user in CHANNEL_TRANSFERS table
     * 9. Update and insert in USER_BALANCES
     * a. Old user: Update(previous_balance=balance at the time of migration,
     * current balance=0).
     * b. New user: Insert(previous_balance=0, current balance=C2C transfer
     * amount(balance of old user at the time of migration)).
     * Commit will be call only successfully execution of above steps for the
     * users(one by one).
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */
    public void userMigrationProcess(Connection p_con, List<UserMigrationVO> p_finalUserMigrList, HashMap<String, UserCategoryVO> p_catCodeMap, HashMap<String, UserGeoDomainVO> p_userGeoDomCodeMap, HashMap<String, HashMap<String, UserMessageVO>> p_profileGradeMap, HashMap<String, String> p_migrationDetailMap) throws BTSLBaseException {
        UserMigrDetailLog.log("userMigrationProcess", "", "Entered p_validatedUserList.size=" + p_finalUserMigrList.size());
        final String METHOD_NAME = "userMigrationProcess";
        int addCount = 0;
        int sucMigUser = 0;
        int seqNo = 1;
        PreparedStatement pstmtCheckGeoType = null;
        ResultSet rsCheckGeoType = null;
        PreparedStatement pstmtSelectGeo = null;
        PreparedStatement pstmtSelectGeoWithCheck = null;
        ResultSet rsSelectGeo = null;
        PreparedStatement pstmtUpdateUser = null; // prepared statement for
                                                  // updating the current user

        PreparedStatement pstmtInsertUser = null;
        PreparedStatement pstmtSelectUserBalance = null;
        ResultSet rsSelectUserBalance = null;
        PreparedStatement pstmtUpdateUserBalance = null;
        PreparedStatement pstmtInsertUserBalance = null;

        PreparedStatement pstmtInsertUserGeographies = null;
        PreparedStatement pstmtInsertUserPhones = null;
        PreparedStatement pstmtInsertUserProductTypes = null;
        PreparedStatement pstmtInsertUserRoles = null;
        PreparedStatement pstmtInsertUserServices = null;
        PreparedStatement pstmtSelectChannelUserInfo = null;
        ResultSet rsSelectChannelUserInfo = null;
        PreparedStatement pstmtInsertChannelUserInfo = null;

        PreparedStatement pstmtChannelTrnf = null;
        PreparedStatement pstmtChnlTrnfItems = null;
        List<UserMessageVO> userMsgErrorList = new ArrayList<UserMessageVO>();
        List<UserMessageVO> userMsgSuccessList = new ArrayList<UserMessageVO>();
        UserMessageVO userMsg = null;

        PreparedStatement psmtselectPrntId = null;
        PreparedStatement psmtupdateUserStatus = null;
        ResultSet rsselectprntId = null;
        long selectUserBalance = 0L;
        String selectUserProductCode = null;
        String isMultipleWallet = null;
        String isActiveUserIdApplied = null;
        long unitValue = 0l;
       
        String sendSms;

        String sqlSelectParent = null;
        PreparedStatement pstmtSelectParent = null;
        ResultSet rsSelectParent = null;
        StringBuilder strBuffSelectParent = null;
        String parentCat = null;
        String parentGeo = null;
        ResultSet rsSelectbal = null;
       

           


            String selectBalanceCount = "select count(*) from user_balances where user_id=?";
            try(PreparedStatement psmtSelectbal = p_con.prepareStatement(selectBalanceCount)){
            UserMigrDetailLog.log("userMigrationProcess", "", "selectBalanceCount = " + selectBalanceCount);
            // todia

            StringBuilder selectParentId = new StringBuilder("SELECT parent_id,user_id,owner_id FROM USERS WHERE msisdn=? and status='Y' and user_type='CHANNEL'");
            UserMigrDetailLog.log("userMigrationProcess", "", "selectParentId = " + selectParentId.toString());
            psmtselectPrntId = p_con.prepareStatement(selectParentId.toString());

            // Query for parent verification.
            strBuffSelectParent = new StringBuilder();
            strBuffSelectParent.append(" SELECT U.user_id,U.parent_id, U.owner_id, U.status, U.user_name,UG.GRPH_DOMAIN_CODE ,");
            strBuffSelectParent.append(" U.category_code,CAT.domain_code ");
            strBuffSelectParent.append(" FROM USERS U, CATEGORIES CAT,USER_GEOGRAPHIES UG");
            strBuffSelectParent.append(" WHERE U.status <>'N' and U.status <>'C'");
            strBuffSelectParent.append(" AND U.category_code=CAT.category_code ");
            strBuffSelectParent.append(" AND U.msisdn=? and UG.user_id=U.user_id and u.user_type='CHANNEL'");

            sqlSelectParent = strBuffSelectParent.toString();
            UserMigrDetailLog.log("userMigrationProcess", "", "QUERY sqlSelectParent=" + sqlSelectParent);
            pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);

            // Query to verify the new category and geo_domain type, if same
            // then migrate the user else not
            // Parameter: toCatCode, toGeoDomCode
            StringBuilder checkGeoTypeBffr = new StringBuilder(" select count(1) from CATEGORIES cat, GEOGRAPHICAL_DOMAINS gd");
            checkGeoTypeBffr.append(" where cat.GRPH_DOMAIN_TYPE= gd.GRPH_DOMAIN_TYPE");
            checkGeoTypeBffr.append(" and CATEGORY_CODE=? and GRPH_DOMAIN_CODE=?");
            UserMigrDetailLog.log("userMigrationProcess", "", "checkGeoTypeBffr = " + checkGeoTypeBffr.toString());
            pstmtCheckGeoType = p_con.prepareStatement(checkGeoTypeBffr.toString());

            // Query to check whether the user geography exist or not.
            String selectBffr=userMigrationToolQry.userMigrationProcessQry();
            UserMigrDetailLog.log("userMigrationProcess", "", "selectBffr = " + selectBffr);
            pstmtSelectGeo = p_con.prepareStatement(selectBffr);

            // Parameter: new_parent_msisdn, new_parent_msisdn
            String selectBffrCheck =userMigrationToolQry.userMigrationProcessQry2();
            UserMigrDetailLog.log("userMigrationProcess", "", "selectBffr = " + selectBffrCheck);
            pstmtSelectGeoWithCheck = p_con.prepareStatement(selectBffrCheck);

            // Insert the new user details
            // Parameters: user_id=new.user_id, category_code=new.category_code,
            // parent_id=new.parent_id,owner_id=new.owner_id
            // modified_by='SYSTEM',
            // modified_on=CURRENT_DATE,reference_id=OLD.user_id,
            // previous_status=OLD.status
            StringBuilder insertUserBuff = new StringBuilder("INSERT INTO users(user_id, user_name, network_code,");
            insertUserBuff.append(" login_id, password, category_code, parent_id, owner_id, allowed_ip, allowed_days,");
            insertUserBuff.append(" from_time, to_time, last_login_on, employee_code, status, email, pswd_modified_on,");
            insertUserBuff.append(" contact_person, contact_no, designation, division, department, msisdn, user_type,");
            insertUserBuff.append(" created_by, created_on, modified_by, modified_on,");
            insertUserBuff.append(" address1, address2, city, state, country, ssn, user_name_prefix, external_code,");
            insertUserBuff.append(" user_code, short_name, reference_id, invalid_password_count, level1_approved_by,");
            insertUserBuff.append(" level1_approved_on, level2_approved_by, level2_approved_on, appointment_date,");
            insertUserBuff.append(" password_count_updated_on,previous_status,PSWD_RESET )");
            insertUserBuff.append(" (SELECT ?, user_name, network_code,?, PASSWORD,");
            insertUserBuff.append(" ?, ?, ?, allowed_ip, allowed_days,");
            insertUserBuff.append(" from_time, to_time, last_login_on, employee_code, status, email, pswd_modified_on,");
            insertUserBuff.append(" contact_person, contact_no, designation, division, department, msisdn, user_type,");
            insertUserBuff.append(" created_by, created_on, 'SYSTEM', ?,");
            insertUserBuff.append(" address1, address2, city, state, country, ssn, user_name_prefix, external_code,");
            insertUserBuff.append(" msisdn, short_name,?, invalid_password_count, level1_approved_by,");
            insertUserBuff.append(" level1_approved_on, level2_approved_by, level2_approved_on, appointment_date,");
            insertUserBuff.append(" password_count_updated_on, status, PSWD_RESET ");
            insertUserBuff.append(" FROM USERS WHERE user_id=? AND status IN ('Y','S') )");

            // commented for DB2 pstmtInsertUser =(OraclePreparedStatement)

            pstmtInsertUser = (PreparedStatement) p_con.prepareStatement(insertUserBuff.toString());
            UserMigrDetailLog.log("userMigrationProcess", "", "insertUser query=" + insertUserBuff);
            // Soft delete the old user
            // user_code=old.user_id, reference_id=new.user_id,
            // status='N',modified_by='SYSTEM',modified_on=current_date,
            // login_id=old.user_id, user_id=old.user_id
            StringBuilder updateUserBuff = new StringBuilder("UPDATE users SET user_code=?,reference_id=?,status=?, ");
            updateUserBuff.append("modified_by='SYSTEM', modified_on=?,login_id=?,old_login_id=login_id, to_moved_user_id=? WHERE user_id=? and user_type='CHANNEL'");
            pstmtUpdateUser = p_con.prepareStatement(updateUserBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "updateUser query=" + updateUserBuff);
            String updateStatus = "UPDATE USERS SET status=? WHERE user_id=?";
            psmtupdateUserStatus = p_con.prepareStatement(updateStatus);

            UserMigrDetailLog.log("userMigrationProcess", "", "updateStatus query=" + updateStatus);
            // Query for updating the user balance
            StringBuilder selectUserBalanceBuff = new StringBuilder("SELECT network_code, network_code_for, ");
            selectUserBalanceBuff.append("U.product_code, balance, prev_balance, last_transfer_type, last_transfer_no,");
            selectUserBalanceBuff.append("last_transfer_on,P.unit_value,daily_balance_updated_on ");
            selectUserBalanceBuff.append("FROM user_balances U,products P ");
            selectUserBalanceBuff.append("WHERE U.user_id=? AND U.product_code=P.product_code");

            pstmtSelectUserBalance = p_con.prepareStatement(selectUserBalanceBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "selectUserBalance query=" + selectUserBalanceBuff);
            // Update the user balance for the old user
            StringBuilder updateUserBalanceBuff = new StringBuilder("UPDATE user_balances SET balance=?, prev_balance=? ");
            updateUserBalanceBuff.append("WHERE user_id=? AND product_code=? ");
            pstmtUpdateUserBalance = p_con.prepareStatement(updateUserBalanceBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "updateUserBalance query=" + updateUserBalanceBuff);
            // Insert in to user balance for new user
            StringBuilder insertUserBalanceBuff = new StringBuilder("INSERT INTO user_balances(user_id, network_code, ");
            insertUserBalanceBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type,");
            insertUserBalanceBuff.append("last_transfer_no, last_transfer_on) VALUES(?,?,?,?,?,?,?,?,?)");

            pstmtInsertUserBalance = p_con.prepareStatement(insertUserBalanceBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "insertUserBalance query=" + insertUserBalanceBuff);
            /*
             * //Insertion in user_domains table
             * StringBuilder insertUserDomainsBuff =new
             * StringBuilder("INSERT INTO user_domains(user_id ,domain_code)");
             * insertUserDomainsBuff.append(" VALUES(?, ?)");
             * 
             * pstmtInsertUserDomains=
             * p_con.prepareStatement(insertUserDomainsBuff.toString());
             */

            // UserMigrDetailLog.log("userMigrationProcess", "",
            // "insertUserDomains query="+insertUserDomainsBuff);
            // Insert into user_geographies table for the new user
            // new_user_id,new_geo_code,old_user_id
            StringBuilder insertUserGeographiesBuff = new StringBuilder("INSERT INTO user_geographies(user_id,");
            insertUserGeographiesBuff.append(" grph_domain_code)");
            insertUserGeographiesBuff.append(" values(?,?)");

            pstmtInsertUserGeographies = p_con.prepareStatement(insertUserGeographiesBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "insertUserGeographies query=" + insertUserGeographiesBuff);
            // Insert in to user phones
            StringBuilder insertUserPhonesBuff = new StringBuilder(" INSERT INTO user_phones(user_id, user_phones_id,");
            insertUserPhonesBuff.append(" msisdn, description, primary_number, sms_pin, pin_required, phone_profile,");
            insertUserPhonesBuff.append(" phone_language, country, invalid_pin_count, last_transaction_status,");
            insertUserPhonesBuff.append(" last_transaction_on, pin_modified_on, created_by, created_on, modified_by,");
            insertUserPhonesBuff.append(" modified_on, last_transfer_id, last_transfer_type, prefix_id,");
            insertUserPhonesBuff.append(" temp_transfer_id, first_invalid_pin_time,");
            insertUserPhonesBuff.append(" access_type, from_time, to_time, allowed_days, allowed_ip, last_login_on)");
            insertUserPhonesBuff.append(" (SELECT ?,?, msisdn, description, primary_number, sms_pin,");
            insertUserPhonesBuff.append(" pin_required, ?, phone_language, country, invalid_pin_count,");
            insertUserPhonesBuff.append(" last_transaction_status, last_transaction_on, pin_modified_on, created_by,");
            insertUserPhonesBuff.append(" created_on, modified_by, modified_on, last_transfer_id, last_transfer_type,");
            insertUserPhonesBuff.append(" prefix_id, temp_transfer_id,first_invalid_pin_time, access_type, from_time, to_time, allowed_days,");
            insertUserPhonesBuff.append(" allowed_ip, last_login_on ");
            insertUserPhonesBuff.append(" FROM user_phones WHERE user_id=?)");

            pstmtInsertUserPhones = p_con.prepareStatement(insertUserPhonesBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "insertUserPhones query=" + insertUserPhonesBuff);
            // Insert into user_product_types table for new user
            StringBuilder insertUserProductTypesBuff = new StringBuilder();
            insertUserProductTypesBuff.append(" INSERT INTO user_product_types(user_id,product_type) ");
            insertUserProductTypesBuff.append(" (SELECT ?, product_type FROM user_product_types");
            insertUserProductTypesBuff.append(" WHERE user_id=? )");

            pstmtInsertUserProductTypes = p_con.prepareStatement(insertUserProductTypesBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "insertUserProductTypes query=" + insertUserProductTypesBuff);
            // Insert in to user_roles for new user
            // new_user_id, new_user_category_code,WEB
            StringBuilder insertUserRolesBuff = new StringBuilder();
            insertUserRolesBuff.append(" INSERT INTO user_roles(user_id ,role_code,gateway_types)");
            insertUserRolesBuff.append(" Values (?,?,?)");

            pstmtInsertUserRoles = p_con.prepareStatement(insertUserRolesBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "insertUserRoles query=" + insertUserRolesBuff);
            // Insert in to user services for new user
            // new_user_id, old_user_id, new_category_code
            StringBuilder insertUserServicesBuff = new StringBuilder("");
            insertUserServicesBuff.append(" INSERT INTO user_services(user_id,service_type, status)");
            insertUserServicesBuff.append(" (SELECT ?, US.service_type,US.status");
            insertUserServicesBuff.append(" FROM user_services US,users U,category_service_type CST");
            insertUserServicesBuff.append(" WHERE US.user_id=? AND U.user_id=US.user_id");
            insertUserServicesBuff.append(" AND U.category_code=CST.category_code");
            insertUserServicesBuff.append(" AND CST.service_type=US.service_type");
            insertUserServicesBuff.append(" AND U.category_code=?)"); // New
                                                                      // user's
                                                                      // category
                                                                      // code

            pstmtInsertUserServices = p_con.prepareStatement(insertUserServicesBuff.toString());

            UserMigrDetailLog.log("userMigrationProcess", "", "insertUserServices query=" + insertUserServicesBuff);
            // Insert in to channel users for new user
            StringBuilder insertChannelUserInfoBuff = new StringBuilder(" INSERT INTO channel_users(USER_ID,USER_GRADE,");
            insertChannelUserInfoBuff.append(" TRANSFER_PROFILE_ID,COMM_PROFILE_SET_ID,IN_SUSPEND, OUT_SUSPEND,");
            insertChannelUserInfoBuff.append(" ACTIVATED_ON,APPLICATION_ID, MPAY_PROFILE_ID,USER_PROFILE_ID,");
            insertChannelUserInfoBuff.append(" IS_PRIMARY,MCOMMERCE_SERVICE_ALLOW,LOW_BAL_ALERT_ALLOW,outlet_code, suboutlet_code)");
            insertChannelUserInfoBuff.append(" (select ?, ?, ?, ?, IN_SUSPEND, OUT_SUSPEND,ACTIVATED_ON, ?, ?, ?, ?, ?, LOW_BAL_ALERT_ALLOW, outlet_code, suboutlet_code");
            insertChannelUserInfoBuff.append(" from channel_users where USER_ID=? )");

            // commented for DB2
            // pstmtInsertChannelUserInfo=(OraclePreparedStatement)
            // p_con.prepareStatement(insertChannelUserInfoBuff.toString());
            pstmtInsertChannelUserInfo = (PreparedStatement) p_con.prepareStatement(insertChannelUserInfoBuff.toString());
            UserMigrDetailLog.log("userMigrationProcess", "", "insertChannelUserInfoBuff query=" + insertChannelUserInfoBuff);

            StringBuilder chnlTrnfBuff = new StringBuilder(" INSERT INTO  channel_transfers ( transfer_id, network_code, network_code_for, grph_domain_code, ");
            chnlTrnfBuff.append(" domain_code, sender_category_code, sender_grade_code, receiver_grade_code, from_user_id, ");
            chnlTrnfBuff.append(" to_user_id, transfer_date, reference_no, ext_txn_no, ext_txn_date, commission_profile_set_id, ");
            chnlTrnfBuff.append(" commission_profile_ver, requested_quantity, channel_user_remarks,  ");
            chnlTrnfBuff.append(" created_on, created_by, modified_by, modified_on, status, transfer_type, transfer_initiated_by, transfer_mrp, ");
            chnlTrnfBuff.append(" payable_amount, net_payable_amount, pmt_inst_type, pmt_inst_no, pmt_inst_date, ");
            chnlTrnfBuff.append(" pmt_inst_amount, sender_txn_profile, receiver_txn_profile, total_tax1, total_tax2, ");
            chnlTrnfBuff.append(" total_tax3, source, receiver_category_code , product_type , transfer_category ,");
            chnlTrnfBuff.append(" first_approver_limit, second_approver_limit,pmt_inst_source,  ");
            chnlTrnfBuff.append(" type,transfer_sub_type,close_date,control_transfer,request_gateway_code, request_gateway_type, ");
            chnlTrnfBuff.append(" msisdn,to_msisdn,to_grph_domain_code,to_domain_code,  ");
            chnlTrnfBuff.append(" first_approved_by, first_approved_on, second_approved_by, second_approved_on, third_approved_by, third_approved_on,sms_default_lang,sms_second_lang");

            isMultipleWallet = Constants.getProperty("IS_MULTIPLE_WALLET");
            isActiveUserIdApplied = Constants.getProperty("IS_ACTIVE_USERID_APPLIED");
            // isMultipleWallet applicable.
            if (PretupsI.YES.equalsIgnoreCase(isMultipleWallet)) {
                // both isMultipleWallet and isActiveUserIdApplied applied.
                if (PretupsI.YES.equalsIgnoreCase(isActiveUserIdApplied)) {
                    // if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
                    if ("TRUE".equalsIgnoreCase(Constants.getProperty("MULTIPLE_WALLET_APPLY_DEFAULT_VALUE"))) {
                        chnlTrnfBuff.append(",active_user_id,TXN_WALLET");
                        chnlTrnfBuff.append(") VALUES ");
                        chnlTrnfBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                    } else {
                        chnlTrnfBuff.append(",active_user_id");
                        chnlTrnfBuff.append(") VALUES ");
                        chnlTrnfBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                    }
                } // isMultipleWallet applicable and isActiveUserIdApplied not
                  // applicable.
                else {
                    chnlTrnfBuff.append(",TXN_WALLET");
                    chnlTrnfBuff.append(") VALUES ");
                    chnlTrnfBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                }
            }
            // isMultipleWallet not applicable.
            else {
                // isMultipleWallet not applicable isActiveUserIdApplied
                // applicable.
                if (PretupsI.YES.equalsIgnoreCase(isActiveUserIdApplied)) {
                    chnlTrnfBuff.append(",active_user_id");
                    chnlTrnfBuff.append(") VALUES ");
                    chnlTrnfBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                }
                // isMultipleWallet not applicable isActiveUserIdApplied not
                // applicable.
                else {
                    chnlTrnfBuff.append(") VALUES ");
                    chnlTrnfBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                }
            }
            String chnlTrfQuery = chnlTrnfBuff.toString();

            UserMigrDetailLog.log("userMigrationProcess", "", "chnlTrfQuery insert query:" + chnlTrfQuery);
            // commented for DB2
            // pstmtChannelTrnf=(OraclePreparedStatement)p_con.prepareStatement(chnlTrfQuery);
            pstmtChannelTrnf = (PreparedStatement) p_con.prepareStatement(chnlTrfQuery);
            // Query for CHANNEL_TRANSFER_ITEMS
            StringBuilder chnlTrfItemsBuff = new StringBuilder(" INSERT INTO channel_transfers_items ( ");
            chnlTrfItemsBuff.append(" s_no,transfer_id,product_code,required_quantity,approved_quantity,user_unit_price, ");
            chnlTrfItemsBuff.append(" commission_profile_detail_id,commission_type, commission_rate, commission_value, ");
            chnlTrfItemsBuff.append(" tax1_type, tax1_rate, tax1_value, tax2_type,tax2_rate, tax2_value , tax3_type, ");
            chnlTrfItemsBuff.append(" tax3_rate, tax3_value, payable_amount, net_payable_amount,mrp,");
            chnlTrfItemsBuff.append(" sender_previous_stock, receiver_previous_stock,transfer_date )");// ,sender_post_stock,
                                                                                                       // receiver_post_stock)
                                                                                                       // ");
            chnlTrfItemsBuff.append(" VALUES  ");
            chnlTrfItemsBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )"); // ,?,?)
                                                                                              // ");
            String chnlTrfItemsBuffQuery = chnlTrfItemsBuff.toString();

            UserMigrDetailLog.log("userMigrationProcess", "", "chnlTrfItemsBuffQuery Insert query:" + chnlTrfItemsBuffQuery);
            pstmtChnlTrnfItems = p_con.prepareStatement(chnlTrfItemsBuffQuery);

            // Whether SMS is allowed or not.
            sendSms = Constants.getProperty("ALLOW_SMS_ON_MIG");
            if (p_finalUserMigrList != null && p_finalUserMigrList.size() > 0) {
                String newUserID;
                String userIDPrifix;
                String networkCode;
                UserMigrationVO usrMigrVO = null;
                int lineNo = 0;
                // To create the file stream of the success and the fail files.
                createOutPutFiles();
                UserMigrDetailLog.log("userMigrationProcess", " File creation successful.", "");
                for (int i = 0; i < p_finalUserMigrList.size(); i++) {
                    // lineNo=i;

                    try {
                        try {
                            usrMigrVO = p_finalUserMigrList.get(i);
                            lineNo = usrMigrVO.getLineNumber();
                            networkCode = usrMigrVO.getNetworkCode();
                            userIDPrifix = p_catCodeMap.get(usrMigrVO.getToUserCatCode()).getUserIdPrefix();
                        } catch (Exception e) {
                            UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), " Category code: ", usrMigrVO.getToUserCatCode(), " is not defined in the system.", "", "");
                            LOG.errorTrace(METHOD_NAME, e);
                            userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToParentMsisdn(), "Category code is invalid.");
                            userMsgErrorList.add(userMsg);
                            writeOutPutFile(fwriterFail, userMsg);
                            throw new BTSLBaseException(this, "userMigrationProcess", " Category code is not currect.");
                        }
                        try {
                            // Now validate the to parent user
                            // UserMigrDetailLog.log("userMigrationProcess",
                            // "From Parent Msisdn= "+usrMigrVO.getFromParentMsisdn(),

                            if (!((usrMigrVO.getFromParentMsisdn()).equals(usrMigrVO.getFromUserMsisdn()))) // added
                                                                                                            // to
                                                                                                            // skip
                                                                                                            // the
                                                                                                            // parent
                                                                                                            // users.
                            {
                                pstmtSelectParent.setString(1, usrMigrVO.getToParentMsisdn());
                                rsSelectParent = pstmtSelectParent.executeQuery();
                                if (rsSelectParent.next()) {
                                    parentCat = rsSelectParent.getString("category_code");
                                    parentGeo = rsSelectParent.getString("GRPH_DOMAIN_CODE");// parentGeo=rsSelectParent.getString("domain_code");
                                    UserMigrDetailLog.log("userMigrationProcess", " Parent info is in-currect.", " parentCat=" + parentCat + ",parentGeo=" + parentGeo + ",usrMigrVO.getToParentCatCode()=" + usrMigrVO.getToParentCatCode() + ",usrMigrVO.getToParentGeoCode()=" + usrMigrVO.getToParentGeoCode());
                                    if (!(parentCat.equals(usrMigrVO.getToParentCatCode()) && parentGeo.equals(usrMigrVO.getToParentGeoCode())))
                                    // if(!(rsSelectParent.getString("category_code").equals(usrMigrVO.getToParentCatCode())
                                    // &&
                                    // rsSelectParent.getString("domain_code").equals(usrMigrVO.getToParentGeoCode())))
                                    {
                                        UserMigrDetailLog.log("userMigrationProcess", " Parent info is in-currect.", " count= " + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToParentMsisdn(), "Parent info is not matching with the data base record, count= " + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", " Parent info is in-currect.");
                                    }
                                }
                            }
                        } catch (SQLException sqe) {
                            UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToParentMsisdn(), usrMigrVO.getToParentCatCode(), usrMigrVO.getToParentGeoCode(), sqe.getMessage(), "");
                            userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToParentMsisdn(), "Parent info is invalid.");
                            userMsgErrorList.add(userMsg);
                            writeOutPutFile(fwriterFail, userMsg);
                            LOG.errorTrace(METHOD_NAME, sqe);
                            throw new BTSLBaseException(this, "userMigrationProcess", " Parent info is not currect.");
                        }
                        pstmtCheckGeoType.setString(1, usrMigrVO.getToUserCatCode());
                        pstmtCheckGeoType.setString(2, usrMigrVO.getToUserGeoCode());
                        rsCheckGeoType = pstmtCheckGeoType.executeQuery();
                        if (rsCheckGeoType.next()) {
                            // Generate the new user id
                            UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), "", "", "", "", "MIGRATION STARTS FROM HERE");
                            newUserID = this.generateUserId(p_con, networkCode, userIDPrifix);
                            try {
                                // Update the old user
                                // user_code=old.user_id,
                                // reference_id=new.user_id,
                                // status='N',modified_by='SYSTEM',modified_on=current_date,
                                // login_id=old.user_id, user_id=old.user_id
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on USERS table");
                                    pstmtUpdateUser.setString(1, usrMigrVO.getFromUserID()); // Old
                                                                                             // user
                                                                                             // id
                                    pstmtUpdateUser.setString(2, newUserID); // New
                                                                             // user
                                                                             // id
                                    pstmtUpdateUser.setString(3, PretupsI.USER_STATUS_ACTIVE); // Status
                                                                                               // of
                                                                                               // old
                                                                                               // user
                                                                                               // id
                                    pstmtUpdateUser.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(new Date()));// Set
                                                                                                                   // the
                                                                                                                   // modified
                                                                                                                   // on
                                                                                                                   // by
                                                                                                                   // current
                                                                                                                   // date
                                    pstmtUpdateUser.setString(5, usrMigrVO.getFromUserID()); // Set
                                                                                             // the
                                                                                             // login
                                                                                             // id
                                                                                             // of
                                                                                             // old
                                                                                             // user
                                                                                             // by
                                                                                             // its
                                                                                             // old
                                                                                             // user
                                                                                             // id
                                    pstmtUpdateUser.setString(6, newUserID); // New
                                    // user
                                    // id
                                    pstmtUpdateUser.setString(7, usrMigrVO.getFromUserID()); // Search
                                                                                             // criteria
                                                                                             // by
                                                                                             // old
                                                                                             // user
                                                                                             // id
                                    addCount = pstmtUpdateUser.executeUpdate();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "Old user update failed.", " count= " + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Update for old record is failed in USERS table :: count :: " + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", " Old user update failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Old user updated successfully.", " count= " + addCount);
                                    }
                                    pstmtUpdateUser.clearParameters();
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Update for old record is failed in USERS table;");
                                    userMsgErrorList.add(userMsg);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " User insertion failed.");
                                }
                                try { // todia
                                    psmtselectPrntId.setString(1, usrMigrVO.getToParentMsisdn());
                                    rsselectprntId = psmtselectPrntId.executeQuery();
                                    String userParentId = null;
                                    String ownerId = null;
                                    psmtselectPrntId.clearParameters();
                                    if (rsselectprntId.next()) {
                                        /*
                                         * if(PretupsI.ROOT_PARENT_ID.
                                         * equalsIgnoreCase
                                         * (rsselectprntId.getString
                                         * ("PARENT_ID")))
                                         * userParentId=rsselectprntId.getString(
                                         * "USER_ID");
                                         * else
                                         * userParentId=rsselectprntId.getString(
                                         * "PARENT_ID");
                                         */
                                        userParentId = rsselectprntId.getString("USER_ID"); // todia
                                        ownerId = rsselectprntId.getString("OWNER_ID");
                                    }// todia
                                     // Insert in to USERS table for the new
                                     // user
                                     // user_id=new.user_id,
                                     // category_code=new.category_code,
                                     // parent_id=new.parent_id,owner_id=new.owner_id
                                     // modified_by='SYSTEM',
                                     // modified_on=CURRENT_DATE,reference_id=OLD.user_id,
                                     // previous_status=OLD.status
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on USERS table for Inserting  the new user");

                                    pstmtInsertUser.setString(1, newUserID);
                                    pstmtInsertUser.setString(2, usrMigrVO.getFromUserLoginID());
                                    pstmtInsertUser.setString(3, usrMigrVO.getToUserCatCode());
                                    if (!((usrMigrVO.getFromParentMsisdn()).equals(usrMigrVO.getFromUserMsisdn()))) // added
                                                                                                                    // to
                                                                                                                    // skip
                                                                                                                    // the
                                                                                                                    // parent
                                                                                                                    // users.
                                    {
                                        pstmtInsertUser.setString(4, userParentId); // todia
                                        pstmtInsertUser.setString(5, ownerId); // todia
                                    } else {
                                        pstmtInsertUser.setString(4, usrMigrVO.getToParentID()); // todia
                                        pstmtInsertUser.setString(5, newUserID); // todia
                                    }

                                    // //todia

                                    // //todia
                                    pstmtInsertUser.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    pstmtInsertUser.setString(7, usrMigrVO.getFromUserID()); // @@reference_id.
                                    pstmtInsertUser.setString(8, usrMigrVO.getFromUserID());
                                    addCount = pstmtInsertUser.executeUpdate();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", " User insertion failed.", "count =" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserCatCode(), "Insertion for new user is Failed in USERS table :: count :" + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", " User insertion failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "User inserted successfully.", "count =" + addCount);
                                    }
                                    pstmtInsertUser.clearParameters();
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserCatCode(), "Insertion for new user is Failed in USERS table;");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " User insertion failed.");
                                }
                                finally{
                                	if(rsselectprntId!=null)
                                		rsselectprntId.close();
                                }

                                // update the old user status ='N' after
                                // inserting the entry for new user.
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : updating old user status to deleted in users table");
                                    psmtupdateUserStatus.setString(1, PretupsI.USER_STATUS_DELETED);
                                    psmtupdateUserStatus.setString(2, usrMigrVO.getFromUserID());
                                    addCount = psmtupdateUserStatus.executeUpdate();
                                    psmtupdateUserStatus.clearParameters();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", " Old user status update failed.", "count" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserCatCode(), "Updation of User Status to delete(N) failed :: count :" + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", " Old user status update failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Old user status updated successfully.", "count" + addCount);
                                    }
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserCatCode(), "Updation of User Status to delete(N) failed ");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " User insertion failed.");
                                }

                                // Update the old user balance and new entry for
                                // the new user id in USER_BALANCES table
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on USER_BALANCES table.");
                                    psmtSelectbal.setString(1, usrMigrVO.getFromUserID());
                                    rsSelectbal = psmtSelectbal.executeQuery();
                                    if (rsSelectbal.next()) {
                                        pstmtSelectUserBalance.setString(1, usrMigrVO.getFromUserID());
                                        rsSelectUserBalance = pstmtSelectUserBalance.executeQuery();
                                        while (rsSelectUserBalance.next()) {
                                            pstmtInsertUserBalance.setString(1, newUserID);
                                            pstmtInsertUserBalance.setString(2, rsSelectUserBalance.getString("network_code"));
                                            pstmtInsertUserBalance.setString(3, rsSelectUserBalance.getString("network_code_for"));
                                            pstmtInsertUserBalance.setString(4, rsSelectUserBalance.getString("product_code"));
                                            selectUserBalance = rsSelectUserBalance.getLong("balance");
                                            // If user
                                            UserMigrDetailLog.log("userMigrationProcess", " User balance=", "selectUserBalance =" + selectUserBalance);
                                            if (BTSLUtil.isNullString(String.valueOf(selectUserBalance)))
                                                selectUserBalance = 0L;
                                            pstmtInsertUserBalance.setLong(5, selectUserBalance);
                                            pstmtInsertUserBalance.setLong(6, 0L);
                                            pstmtInsertUserBalance.setString(7, rsSelectUserBalance.getString("last_transfer_type"));
                                            pstmtInsertUserBalance.setString(8, rsSelectUserBalance.getString("last_transfer_no"));
                                            unitValue = rsSelectUserBalance.getLong("unit_value");
                                            pstmtInsertUserBalance.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(rsSelectUserBalance.getDate("last_transfer_on")));
                                            addCount = pstmtInsertUserBalance.executeUpdate();
                                            if (addCount <= 0) {
                                                UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_BALANCES is failed", "count =" + addCount);
                                                userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert for user balance is failed in USERS_BALANCES table :: count :" + addCount);
                                                userMsgErrorList.add(userMsg);
                                                writeOutPutFile(fwriterFail, userMsg);
                                                throw new BTSLBaseException(this, "userMigrationProcess", "Insertion for new user in USER_BALANCES is failed");
                                            } else {
                                                UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_BALANCES is sucessfull", "count =" + addCount);
                                            }
                                            // Clear the parameter
                                            pstmtInsertUserBalance.clearParameters();
                                            UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Update for old user in USER_BALANCES");

                                            pstmtUpdateUserBalance.setLong(1, 0);
                                            pstmtUpdateUserBalance.setLong(2, selectUserBalance);
                                            pstmtUpdateUserBalance.setString(3, usrMigrVO.getFromUserID());
                                            selectUserProductCode = rsSelectUserBalance.getString("product_code");
                                            if (BTSLUtil.isNullString(selectUserProductCode))
                                                selectUserProductCode = "ETOPUP";
                                            pstmtUpdateUserBalance.setString(4, selectUserProductCode);
                                            addCount = pstmtUpdateUserBalance.executeUpdate();
                                            if (addCount <= 0) {
                                                UserMigrDetailLog.log("userMigrationProcess", "Update for old user in USER_BALANCES is failed", "count =" + addCount);
                                                userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Update for user balance is failed in USERS_BALANCES table :: count :: " + addCount);
                                                userMsgErrorList.add(userMsg);
                                                writeOutPutFile(fwriterFail, userMsg);
                                                throw new BTSLBaseException(this, "userMigrationProcess", "Update for old user in USER_BALANCES is failed");
                                            } else {
                                                UserMigrDetailLog.log("userMigrationProcess", "Update for old user in USER_BALANCES is sucessfull", "count =" + addCount);
                                            }
                                            // Clear the parameter
                                            pstmtUpdateUserBalance.clearParameters();
                                            // What is the use of this block
                                            /*
                                             * if(unitValue==0l)
                                             * {
                                             * UserMigrDetailLog.log(lineNo,
                                             * usrMigrVO.getFromUserMsisdn(),
                                             * usrMigrVO.getFromUserID(),
                                             * newUserID,
                                             * usrMigrVO.getToUserGeoCode(),
                                             * usrMigrVO.getFromUserGeoCode(),
                                             * "userMigrationProcess : NO RECORD FOUND FOR OLD USER IN USERS_BALANCE. : OPERATION ROLLBACKED."
                                             * );
                                             * userMsg=new UserMessageVO(lineNo,
                                             * usrMigrVO.getFromUserMsisdn(),
                                             * usrMigrVO.getFromUserCatCode(),
                                             * "NO RECORD FOUND FOR OLD USER IN USERS_BALANCE."
                                             * +addCount);
                                             * userMsgErrorList.add(userMsg);
                                             * writeOutPutFile(fwriterFail,
                                             * userMsg);
                                             * continue;
                                             * }
                                             */
                                        }
                                    }// chk
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert or Update for user balance is failed in USERS_BALANCES table;");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " User balance update is failed.");
                                }
                                finally
                                {
                                	if(rsSelectbal!=null)
                                		rsSelectbal.close();
                                }
                           
                                /*
                                 * //Update in USER_DOMAINS table
                                 * //parameter: new_user_id, new_domain_code
                                 * try
                                 * {
                                 * UserMigrDetailLog.log(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserID(), newUserID,
                                 * usrMigrVO.getToUserGeoCode(),
                                 * usrMigrVO.getFromUserGeoCode(),
                                 * "userMigrationProcess : Operation on USER_DOMAINS table"
                                 * );
                                 * String domainCode=p_catCodeMap.get(usrMigrVO.
                                 * getToUserCatCode()).getDomainCode();
                                 * pstmtInsertUserDomains.setString(1,
                                 * newUserID);
                                 * pstmtInsertUserDomains.setString(2,
                                 * domainCode);
                                 * //Insert the record
                                 * addCount =
                                 * pstmtInsertUserDomains.executeUpdate();
                                 * if(addCount<=0)
                                 * {
                                 * UserMigrDetailLog.log("userMigrationProcess",
                                 * "Insertion for new user in USER_DOMAINS is failed."
                                 * , "count= "+addCount);
                                 * userMsg=new UserMessageVO(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserCatCode(),
                                 * "Insert in USER_DOMAINS is failed. :: count ::"
                                 * +addCount);
                                 * userMsgErrorList.add(userMsg);
                                 * writeOutPutFile(fwriterFail, userMsg);
                                 * throw new BTSLBaseException(this,
                                 * "userMigrationProcess",
                                 * "Insertion for new user in USER_DOMAINS is failed."
                                 * );
                                 * }
                                 * else
                                 * {
                                 * UserMigrDetailLog.log("userMigrationProcess",
                                 * "Insertion for new user in USER_DOMAINS is sucessful."
                                 * , "count= "+addCount);
                                 * }
                                 * //Clear the parameter
                                 * pstmtInsertUserDomains.clearParameters();
                                 * }
                                 * catch(SQLException sqe)
                                 * {
                                 * UserMigrDetailLog.log(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserID(), newUserID,
                                 * usrMigrVO.getToUserGeoCode(),
                                 * usrMigrVO.getFromUserGeoCode(),
                                 * sqe.getMessage());
                                 * userMsg=new UserMessageVO(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserCatCode(),
                                 * "Insert in USER_DOMAINS is failed.");
                                 * userMsgErrorList.add(userMsg);
                                 * sqe.printStackTrace();
                                 * writeOutPutFile(fwriterFail, userMsg);
                                 * throw new BTSLBaseException(this,
                                 * "userMigrationProcess",
                                 * " Insertion for new user in USER_DOMAINS is failed."
                                 * );
                                 * }
                                 */
                                // Insert into user_geographies table for the
                                // new user
                                // parameter:
                                // new_user_id,new_geo_code,old_user_id
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on USER_GEOGRAPHIES table");
                                    // psmtselectPrntId.setString(1,
                                    // usrMigrVO.getToUserMsisdn()); //todia
                                    psmtselectPrntId.setString(1, usrMigrVO.getToParentMsisdn());
                                    rsselectprntId = psmtselectPrntId.executeQuery();
                                    String userParentId = null;
                                    psmtselectPrntId.clearParameters();
                                    while (rsselectprntId.next()) {
                                        /*
                                         * if(PretupsI.ROOT_PARENT_ID.
                                         * equalsIgnoreCase
                                         * (rsselectprntId.getString
                                         * ("PARENT_ID")))
                                         * userParentId=rsselectprntId.getString(
                                         * "USER_ID");
                                         * else
                                         * userParentId=rsselectprntId.getString(
                                         * "PARENT_ID");
                                         */
                                        userParentId = rsselectprntId.getString("USER_ID"); // todia
                                    }
                                    // If user is at top level
                                    if ("1".equals(usrMigrVO.getToUserCatCodeSeqNo()) && p_catCodeMap.get(usrMigrVO.getToUserCatCode()).getGrphDomainType().equals(usrMigrVO.getToGeoDomainType())) {
                                        pstmtInsertUserGeographies.setString(1, newUserID);
                                        pstmtInsertUserGeographies.setString(2, usrMigrVO.getToUserGeoCode());
                                    } else {
                                        pstmtSelectGeo.setString(1, userParentId);
                                        pstmtSelectGeo.setString(2, usrMigrVO.getToUserGeoCode());

                                        rsSelectGeo = pstmtSelectGeo.executeQuery();
                                        // Parameter: new_user_id, new_geo_code
                                        if (rsSelectGeo.next()) {
                                            pstmtInsertUserGeographies.setString(1, newUserID);
                                            pstmtInsertUserGeographies.setString(2, usrMigrVO.getToUserGeoCode());
                                        } else if ("Y".equals(Constants.getProperty("ALLOW_MIGR_WITHOUT_GEODOMCODE"))) {
                                            pstmtSelectGeoWithCheck.setString(1, usrMigrVO.getToParentMsisdn());
                                            pstmtSelectGeoWithCheck.setString(2, usrMigrVO.getToParentMsisdn());
                                            rsSelectGeo = pstmtSelectGeoWithCheck.executeQuery();
                                            if (rsSelectGeo.next()) {
                                                String grphDomainCode = rsSelectGeo.getString("grph_domain_code");
                                                pstmtInsertUserGeographies.setString(1, newUserID);
                                                pstmtInsertUserGeographies.setString(2, grphDomainCode);
                                                userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), grphDomainCode, "Insert in USER_GEOGRAPHIES is failed because no geo_domain_code found for the new parent.");
                                            } else {
                                                userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_GEOGRAPHIES is failed because no geo_domain_code found for the new parent.");
                                                writeOutPutFile(fwriterFail, userMsg);
                                                continue;
                                            }
                                        } else {
                                            userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserGeoCode(), " Given geo_domain_code is not defined in the system.");
                                            writeOutPutFile(fwriterFail, userMsg);
                                            continue; // checkit.
                                        }
                                    }

                                    /*
                                     * //chk--skip validation at parent user.
                                     * if(!((usrMigrVO.getFromParentMsisdn()).equals
                                     * (usrMigrVO.getFromUserMsisdn()))) //added
                                     * to skip the parent users.
                                     * {
                                     * pstmtSelectGeo.setString(1,
                                     * userParentId);
                                     * pstmtSelectGeo.setString(2,
                                     * usrMigrVO.getToUserGeoCode());
                                     * 
                                     * rsSelectGeo=pstmtSelectGeo.executeQuery();
                                     * //Parameter: new_user_id, new_geo_code
                                     * if(rsSelectGeo.next())
                                     * {
                                     * pstmtInsertUserGeographies.setString(1,
                                     * newUserID);
                                     * pstmtInsertUserGeographies.setString(2,
                                     * usrMigrVO.getToUserGeoCode());
                                     * }
                                     * else
                                     * {
                                     * userMsg=new UserMessageVO(lineNo,
                                     * usrMigrVO.getFromUserMsisdn(),
                                     * usrMigrVO.getToUserGeoCode(),
                                     * " Given geo_domain_code is not defined in the system."
                                     * );
                                     * writeOutPutFile(fwriterFail, userMsg);
                                     * continue; //checkit.
                                     * }
                                     * }//ashishT
                                     * else
                                     * {
                                     * pstmtInsertUserGeographies.setString(1,
                                     * newUserID);
                                     * pstmtInsertUserGeographies.setString(2,
                                     * usrMigrVO.getToUserGeoCode());
                                     * }
                                     * //Parameter: new_parent_msisdn,
                                     * new_parent_msisdn
                                     * if("Y".equals(Constants.getProperty(
                                     * "ALLOW_MIGR_WITHOUT_GEODOMCODE")))
                                     * {
                                     * pstmtSelectGeoWithCheck.setString(1,
                                     * usrMigrVO.getToParentMsisdn());
                                     * pstmtSelectGeoWithCheck.setString(2,
                                     * usrMigrVO.getToParentMsisdn());
                                     * rsSelectGeo=pstmtSelectGeoWithCheck.
                                     * executeQuery();
                                     * if(rsSelectGeo.next())
                                     * {
                                     * String
                                     * grphDomainCode=rsSelectGeo.getString
                                     * ("grph_domain_code");
                                     * pstmtInsertUserGeographies.setString(1,
                                     * newUserID);
                                     * pstmtInsertUserGeographies.setString(2,
                                     * grphDomainCode);
                                     * userMsg=new UserMessageVO(lineNo,
                                     * usrMigrVO.getFromUserMsisdn(),
                                     * grphDomainCode,
                                     * "Insert in USER_GEOGRAPHIES is failed because no geo_domain_code found for the new parent."
                                     * );
                                     * }
                                     * else
                                     * {
                                     * userMsg=new UserMessageVO(lineNo,
                                     * usrMigrVO.getFromUserMsisdn(),
                                     * usrMigrVO.getFromUserCatCode(),
                                     * "Insert in USER_GEOGRAPHIES is failed because no geo_domain_code found for the new parent."
                                     * );
                                     * writeOutPutFile(fwriterFail, userMsg);
                                     * continue;
                                     * }
                                     * }
                                     */
                                    // Insert the record
                                    addCount = pstmtInsertUserGeographies.executeUpdate();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_GEOGRAPHIES is failed.", "count =" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_GEOGRAPHIES is failed :: count " + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", "Insertion for new user in USER_GEOGRAPHIES is failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_GEOGRAPHIES is sucessfull.", "count =" + addCount);
                                    }
                                    // Clear the parameter
                                    pstmtInsertUserGeographies.clearParameters();
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_GEOGRAPHIES is failed.");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " Insertion for new user in USER_GEOGRAPHIES is failed.");
                                }
                                finally{
                                	if(rsselectprntId!=null)
                                		rsselectprntId.close();
                                }
                                // Insert in to USER_PHONES
                                // Parameter: new_user_id, old_user_id
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on USER_PHONES table.");
                                    pstmtInsertUserPhones.setString(1, newUserID);
                                    pstmtInsertUserPhones.setString(2, generatePhoneId(p_con));
                                    pstmtInsertUserPhones.setString(3, usrMigrVO.getToUserCatCode()); // todia
                                    pstmtInsertUserPhones.setString(4, usrMigrVO.getFromUserID()); // todia
                                    // Insert the record
                                    addCount = pstmtInsertUserPhones.executeUpdate();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_PHONES is failed .", "Count" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_PHONES is failed :: count :" + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", "Insertion for new user in USER_PHONES is failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_PHONES is sucessfull .", "Count" + addCount);
                                    }
                                    // Clear the parameter
                                    pstmtInsertUserPhones.clearParameters();
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_PHONES is failed.");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " Insertion for new user in USER_PHONES is failed.");
                                }
                                // Insert into user_product_types table for new
                                // user
                                // Parameter: new_user_id, old_user_id
                                // done by ashishT
                                /*
                                 * commented as discussed by Ved-Sir
                                 * try
                                 * {
                                 * UserMigrDetailLog.log(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserID(), newUserID,
                                 * usrMigrVO.getToUserGeoCode(),
                                 * usrMigrVO.getFromUserGeoCode(),
                                 * "userMigrationProcess : Operation on USER_PRODUCT_TYPES table."
                                 * );
                                 * pstmtInsertUserProductTypes.setString(1,
                                 * newUserID);
                                 * pstmtInsertUserProductTypes.setString(2,
                                 * usrMigrVO.getFromUserID());
                                 * System.out.println("usrMigrVO.getFromUserID()= "
                                 * +usrMigrVO.getFromUserID()
                                 * +"newUserID= "+newUserID);
                                 * //Insert the record
                                 * addCount =
                                 * pstmtInsertUserProductTypes.executeUpdate();
                                 * if(addCount<=0)
                                 * {
                                 * UserMigrDetailLog.log("userMigrationProcess",
                                 * "Insertion for new user in USER_PRODUCT_TYPES is failed."
                                 * , "count= "+addCount);
                                 * userMsg=new UserMessageVO(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserCatCode(),
                                 * "Insert in USER_PRODUCT_TYPES is failed :: count :"
                                 * +addCount);
                                 * userMsgErrorList.add(userMsg);
                                 * throw new BTSLBaseException(this,
                                 * "userMigrationProcess",
                                 * "Insertion for new user in USER_PRODUCT_TYPES is failed."
                                 * );
                                 * }
                                 * else
                                 * {
                                 * UserMigrDetailLog.log("userMigrationProcess",
                                 * "Insertion for new user in USER_PRODUCT_TYPES is sucessfull."
                                 * , "count= "+addCount);
                                 * }
                                 * //Clear the parameter
                                 * pstmtInsertUserProductTypes.clearParameters();
                                 * }
                                 * catch(SQLException sqe)
                                 * {
                                 * UserMigrDetailLog.log(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserID(), newUserID,
                                 * usrMigrVO.getToUserGeoCode(),
                                 * usrMigrVO.getFromUserGeoCode(),
                                 * sqe.getMessage());
                                 * userMsg=new UserMessageVO(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserCatCode(),
                                 * "Insert in USER_PRODUCT_TYPES is failed.");
                                 * userMsgErrorList.add(userMsg);
                                 * sqe.printStackTrace();
                                 * throw new BTSLBaseException(this,
                                 * "userMigrationProcess",
                                 * " Insertion for new user in USER_PRODUCT_TYPES is failed."
                                 * );
                                 * }
                                 */
                                // Insert in to user_roles for new user
                                // Parameter: //new_user_id,
                                // new_user_category_code,WEB
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on USER_ROLES table.");
                                    pstmtInsertUserRoles.setString(1, newUserID);
                                    pstmtInsertUserRoles.setString(2, usrMigrVO.getToUserCatCode());
                                    pstmtInsertUserRoles.setString(3, "WEB");
                                    // Insert the record
                                    addCount = pstmtInsertUserRoles.executeUpdate();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_ROLES is failed.", "Count =" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_ROLES is failed :: count :" + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", "Insertion for new user in USER_ROLES is failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_ROLES is sucessfull.", "Count =" + addCount);
                                    }
                                    // Clear the parameter
                                    pstmtInsertUserRoles.clearParameters();
                                    /*
                                     * //todia--starts
                                     * userRoles = new ArrayList<String>();
                                     * psmtloadRoles.setString(1,
                                     * usrMigrVO.getToUserCatCode());
                                     * rsloadRoles=psmtloadRoles.executeQuery();
                                     * while(rsloadRoles.next())
                                     * {
                                     * role=rsloadRoles.getString("ROLE_CODE");
                                     * userRoles.add(role);
                                     * 
                                     * }
                                     * UserMigrDetailLog.log(lineNo,
                                     * usrMigrVO.getFromUserMsisdn(),
                                     * usrMigrVO.getFromUserID(), newUserID,
                                     * usrMigrVO.getToUserGeoCode(),
                                     * usrMigrVO.getFromUserGeoCode(),
                                     * "role size ="+userRoles.size());
                                     * int cnt=0;
                                     * for(int kk=0;kk<userRoles.size();kk++)
                                     * {
                                     * role=(String)userRoles.get(kk);
                                     * if(role.equalsIgnoreCase(usrMigrVO.
                                     * getToUserCatCode()))
                                     * continue;
                                     * 
                                     * UserMigrDetailLog.log(usrMigrVO.
                                     * getFromUserMsisdn(), "Roles ***** ",
                                     * role);
                                     * pstmtInsertUserRoles.setString(1,
                                     * newUserID);
                                     * pstmtInsertUserRoles.setString(2, role);
                                     * pstmtInsertUserRoles.setString(3, "WEB");
                                     * cnt=pstmtInsertUserRoles.executeUpdate();
                                     * UserMigrDetailLog.log("", "count =",
                                     * " "+cnt);
                                     * pstmtInsertUserRoles.clearParameters();
                                     * 
                                     * }
                                     * //todia --ends
                                     */} catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_ROLES is failed.");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " Insertion for new user in USER_ROLES is failed.");
                                }
                                // Insert in to USER_SERVICES for new user
                                // Parameter: new_user_id, old_user_id,
                                // new_category_code
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on USER_SERVICES table");
                                    pstmtInsertUserServices.setString(1, newUserID);
                                    pstmtInsertUserServices.setString(2, usrMigrVO.getFromUserID());
                                    // pstmtInsertUserServices.setString(3,
                                    // usrMigrVO.getToUserCatCode());
                                    pstmtInsertUserServices.setString(3, usrMigrVO.getFromUserCatCode()); // ashishT
                                                                                                          // changed
                                                                                                          // "FROM"
                                                                                                          // from
                                                                                                          // "TO"
                                    // Insert the record
                                    addCount = pstmtInsertUserServices.executeUpdate();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "User has no service assigned previously, so after migration there will be no service for the user=", usrMigrVO.getFromUserMsisdn());
                                        /*
                                         * UserMigrDetailLog.log(
                                         * "userMigrationProcess",
                                         * "Insertion for new user in USER_SERVICES is failed."
                                         * , "Count"+addCount);
                                         * userMsg=new UserMessageVO(lineNo,
                                         * usrMigrVO.getFromUserMsisdn(),
                                         * usrMigrVO.getFromUserCatCode(),
                                         * "Insert in USER_SERVICES is failed :: count "
                                         * +addCount);
                                         * userMsgErrorList.add(userMsg);
                                         * writeOutPutFile(fwriterFail,
                                         * userMsg);
                                         * throw new BTSLBaseException(this,
                                         * "userMigrationProcess",
                                         * "Insertion for new user in USER_SERVICES is failed."
                                         * );
                                         */
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in USER_SERVICES is sucessfull.", "Count" + addCount);
                                    }
                                    // Clear the parameter

                                    pstmtInsertUserServices.clearParameters();
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in USER_SERVICES is failed.");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " Insertion for new user in USER_SERVICES is failed.");
                                }
                                // Insert in to CHANNEL_USERS for new user
                                // Parameters: user_id, user_grade,
                                // transfer_profile_id, comm_profile_set_id,
                                // in_suspend,
                                // out_suspend, activated_on,application_id,
                                // mpay_profile_id, user_profile_id, is_primary,
                                // mcommerce_service_allow, low_bal_alert_allow
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on CHANNEL_USERS table");
                                    String catCode = usrMigrVO.getToUserCatCode();
                                    String commProfId = p_profileGradeMap.get("COMM_PROFILE").get(catCode).getMsg1();
                                    String trfProfileId = p_profileGradeMap.get("TRF_PROFILE").get(catCode).getMsg1();
                                    String userGrd = p_profileGradeMap.get("USER_GRADE").get(catCode).getMsg1();

                                    if (commProfId == null) {
                                        UserMigrDetailLog.log("userMigrationProcess", "", "COMM_PROFILE not found.");
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserCatCode(), "COMM_PROFILE is not found");
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", " COMM_PROFILE not found.");
                                    } else if (trfProfileId == null) {
                                        UserMigrDetailLog.log("userMigrationProcess", "", "TRF_PROFILE not found.");
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserCatCode(), "TRF_PROFILE is not found");
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", " TRF_PROFILE not found.");
                                    } else if (userGrd == null) {
                                        UserMigrDetailLog.log("userMigrationProcess", "", "USER_GRADE not found.");
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getToUserCatCode(), "USER_GRADE is not found");
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", " USER_GRADE not found.");
                                    }

                                    pstmtInsertChannelUserInfo.setString(1, newUserID);
                                    pstmtInsertChannelUserInfo.setString(2, userGrd);
                                    pstmtInsertChannelUserInfo.setString(3, trfProfileId);
                                    pstmtInsertChannelUserInfo.setString(4, commProfId);
                                    // pstmtInsertChannelUserInfo.setString(5,
                                    // "N");
                                    // pstmtInsertChannelUserInfo.setString(6,
                                    // "N");
                                    pstmtInsertChannelUserInfo.setString(5, "1");
                                    pstmtInsertChannelUserInfo.setString(6, " ");
                                    pstmtInsertChannelUserInfo.setString(7, newUserID);
                                    pstmtInsertChannelUserInfo.setString(8, "Y");
                                    pstmtInsertChannelUserInfo.setString(9, "N");
                                    // pstmtInsertChannelUserInfo.setString(10,

                                    pstmtInsertChannelUserInfo.setString(10, usrMigrVO.getFromUserID());

                                    // Insert the record
                                    addCount = pstmtInsertChannelUserInfo.executeUpdate();
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in CHANNEL_USERS is failed.", "Count =" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in CHANNEL_USERS is failed :: count " + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", "Insertion for new user in CHANNEL_USERS is failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for new user in CHANNEL_USERS is sucessfull.", "Count =" + addCount);
                                    }
                                    // Clear the parameter
                                    pstmtInsertChannelUserInfo.clearParameters();
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert in CHANNEL_USERS is failed.");
                                    userMsgErrorList.add(userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " Insertion for new user in CHANNEL_USERS is failed.");
                                }
                                // Entry in CHANNEL_TRANSFERS and
                                // CHANNEL_TRANSFER_ITEMS
                                try {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "userMigrationProcess : Operation on CHANNEL_TRANSFERS and CHANNEL_TRANSFER_ITEMS table");
                                    Date curDate = new Date();
                                    ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
                                    channelTransferVO.setNetworkCode(Constants.getProperty("NETWORK_CODE"));
                                    channelTransferVO.setNetworkCodeFor(Constants.getProperty("NETWORK_CODE"));
                                    channelTransferVO.setCreatedOn(curDate);
                                    ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);

                                    // Set the parameters
                                    int m = 0;
                                    pstmtChannelTrnf.setString(++m, channelTransferVO.getTransferID());
                                    pstmtChannelTrnf.setString(++m, channelTransferVO.getNetworkCode());
                                    pstmtChannelTrnf.setString(++m, channelTransferVO.getNetworkCodeFor());
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserGeoCode());
                                    pstmtChannelTrnf.setString(++m, p_catCodeMap.get(usrMigrVO.getFromUserCatCode()).getDomainCode());
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserCatCode());
                                    pstmtChannelTrnf.setString(++m, p_profileGradeMap.get("USER_GRADE").get(usrMigrVO.getFromUserCatCode()).getMsg1());
                                    pstmtChannelTrnf.setString(++m, p_profileGradeMap.get("USER_GRADE").get(usrMigrVO.getToUserCatCode()).getMsg1());
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserID());
                                    pstmtChannelTrnf.setString(++m, newUserID);
                                    pstmtChannelTrnf.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                                    pstmtChannelTrnf.setString(++m, "");
                                    pstmtChannelTrnf.setString(++m, "");
                                    pstmtChannelTrnf.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                                    pstmtChannelTrnf.setString(++m, p_profileGradeMap.get("COMM_PROFILE").get(usrMigrVO.getToUserCatCode()).getMsg1());
                                    pstmtChannelTrnf.setString(++m, "1");// commission_profile_version
                                    pstmtChannelTrnf.setLong(++m, selectUserBalance);
                                    // commented for DB2

                                    // OraclePreparedStatement.FORM_NCHAR);
                                    pstmtChannelTrnf.setString(++m, "Migrated by SYSTEM");
                                    pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserID());// Created
                                                                                               // by
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserID());// Modified
                                                                                               // by
                                    pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    pstmtChannelTrnf.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE); // Status
                                    pstmtChannelTrnf.setString(++m, PretupsI.CHANNEL_TYPE_C2C);
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserID());// Initiated
                                                                                               // by
                                    pstmtChannelTrnf.setLong(++m, selectUserBalance);// Transfer
                                                                                     // amount
                                    pstmtChannelTrnf.setLong(++m, selectUserBalance);// Payable
                                                                                     // amount
                                    pstmtChannelTrnf.setLong(++m, selectUserBalance);// Net
                                                                                     // Payable
                                                                                     // amount
                                    pstmtChannelTrnf.setString(++m, "");
                                    pstmtChannelTrnf.setString(++m, "");
                                    pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    pstmtChannelTrnf.setLong(++m, 0);
                                    pstmtChannelTrnf.setString(++m, p_profileGradeMap.get("TRF_PROFILE").get(usrMigrVO.getFromUserCatCode()).getMsg1()); // Sender
                                                                                                                                                         // transaction
                                                                                                                                                         // profile
                                    pstmtChannelTrnf.setString(++m, p_profileGradeMap.get("TRF_PROFILE").get(usrMigrVO.getToUserCatCode()).getMsg1()); // Receiver
                                                                                                                                                       // transaction
                                                                                                                                                       // profile
                                    pstmtChannelTrnf.setLong(++m, 0);
                                    pstmtChannelTrnf.setLong(++m, 0);
                                    pstmtChannelTrnf.setLong(++m, 0);
                                    pstmtChannelTrnf.setString(++m, PretupsI.REQUEST_SOURCE_WEB);// Source
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getToUserCatCode());// Receiver
                                                                                                  // category
                                                                                                  // code
                                    pstmtChannelTrnf.setString(++m, selectUserProductCode);// Product
                                                                                           // type
                                    pstmtChannelTrnf.setString(++m, PretupsI.TRANSFER_CATEGORY_TRANSFER);// Transfer
                                                                                                         // category
                                    pstmtChannelTrnf.setString(++m, "");
                                    pstmtChannelTrnf.setString(++m, "");
                                    
                                    pstmtChannelTrnf.setString(++m, "SYSTEM");
                                    pstmtChannelTrnf.setString(++m, PretupsI.CHANNEL_TYPE_C2C);// Type
                                    pstmtChannelTrnf.setString(++m, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);// Sub
                                                                                                                 // type
                                    if (PretupsI.CHANNEL_TYPE_O2C.equals(PretupsI.CHANNEL_TYPE_C2C) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER) && PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                                        pstmtChannelTrnf.setTimestamp(++m, null);
                                    } else
                                        pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    pstmtChannelTrnf.setString(++m, PretupsI.CONTROL_LEVEL_ADJ);
                                    pstmtChannelTrnf.setString(++m, PretupsI.REQUEST_SOURCE_TYPE_WEB);
                                    pstmtChannelTrnf.setString(++m, PretupsI.GATEWAY_TYPE_WEB);
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserMsisdn());// From
                                                                                                   // user
                                                                                                   // code
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getFromUserMsisdn());// To
                                                                                                   // user
                                                                                                   // code
                                    pstmtChannelTrnf.setString(++m, usrMigrVO.getToUserGeoCode());// Receiver
                                                                                                  // geographical
                                                                                                  // domain
                                                                                                  // code
                                    pstmtChannelTrnf.setString(++m, p_catCodeMap.get(usrMigrVO.getToUserCatCode()).getDomainCode());// Receiver
                                                                                                                                    // domain
                                                                                                                                    // code
                                    pstmtChannelTrnf.setString(++m, "");// First
                                                                        // approved
                                                                        // by
                                    pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(null)); // first_approved_on
                                    pstmtChannelTrnf.setString(++m, ""); // second_approved_by
                                    pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(null)); // second_approved_on
                                    pstmtChannelTrnf.setString(++m, ""); // third_approved_by
                                    pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(null)); // third_approved_on
                                    pstmtChannelTrnf.setString(++m, ""); // sms_default_lang
                                    pstmtChannelTrnf.setString(++m, ""); // sms_second_lang

                                    if (PretupsI.YES.equalsIgnoreCase(isMultipleWallet)) {
                                        // both isMultipleWallet and
                                        // isActiveUserIdApplied applied.
                                        if (PretupsI.YES.equalsIgnoreCase(isActiveUserIdApplied)) {
                                            // if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
                                            if ("TRUE".equalsIgnoreCase(Constants.getProperty("MULTIPLE_WALLET_APPLY_DEFAULT_VALUE"))) {
                                                pstmtChannelTrnf.setString(++m, "");
                                                pstmtChannelTrnf.setString(++m, "");
                                            } else {
                                                pstmtChannelTrnf.setString(++m, "");
                                            }
                                        } // isMultipleWallet applicable and
                                          // isActiveUserIdApplied not
                                          // applicable.
                                        else {
                                            pstmtChannelTrnf.setString(++m, "");
                                        }
                                    }
                                    // isMultipleWallet not applicable.
                                    else {
                                        // isMultipleWallet not applicable
                                        // isActiveUserIdApplied applicable.
                                        if (PretupsI.YES.equalsIgnoreCase(isActiveUserIdApplied)) {
                                            pstmtChannelTrnf.setString(++m, "");
                                        }
                                        // isMultipleWallet not applicable
                                        // isActiveUserIdApplied not applicable.
                                        /*
                                         * else
                                         * {
                                         * //do nothing.
                                         * }
                                         */
                                    }
                                    // Insert the record
                                    addCount = pstmtChannelTrnf.executeUpdate();
                                    addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for C2C transfer in Channel Transfer is failed.", "Count =" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert for C2C transfer in Channel Transfer is failed :: count :" + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", "Insertion in CHANNEL_TRANSFER is failed.");
                                    }
                                    // Clear the parameter
                                    pstmtChannelTrnf.clearParameters();

                                    // Set the parameters
                                    m = 0;
                                    if (BTSLUtil.isNullString(selectUserProductCode))
                                        selectUserProductCode = "ETOPUP";
                                    if (seqNo > 999)
                                        seqNo = 1;
                                    pstmtChnlTrnfItems.setInt(++m, (seqNo + 1));
                                    pstmtChnlTrnfItems.setString(++m, channelTransferVO.getTransferID());
                                    pstmtChnlTrnfItems.setString(++m, selectUserProductCode);// Product
                                                                                             // code
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);// Requested
                                                                                       // quantity
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);// Approved
                                                                                       // quantity

                                    // String
                                    // convFactor=Constants.getProperty("MUL_FACTOR");
                                    if (BTSLUtil.isNullString(String.valueOf(unitValue)) || unitValue == 0)
                                        unitValue = 100;
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance / unitValue);// Unit
                                                                                                   // value
                                    pstmtChnlTrnfItems.setString(++m, p_profileGradeMap.get("COMM_PROFILE").get(usrMigrVO.getFromUserCatCode()).getMsg1());
                                    pstmtChnlTrnfItems.setString(++m, PretupsI.AMOUNT_TYPE_PERCENTAGE);// Commission
                                                                                                       // type
                                    pstmtChnlTrnfItems.setDouble(++m, 0);// Commission
                                                                         // rate
                                    pstmtChnlTrnfItems.setLong(++m, 0);// Commission
                                                                       // value
                                    pstmtChnlTrnfItems.setString(++m, PretupsI.AMOUNT_TYPE_PERCENTAGE);// Tax
                                                                                                       // one
                                                                                                       // type
                                    pstmtChnlTrnfItems.setDouble(++m, 0);// Tax
                                                                         // one
                                                                         // rate
                                    pstmtChnlTrnfItems.setLong(++m, 0);// Tax
                                                                       // one
                                                                       // value
                                    pstmtChnlTrnfItems.setString(++m, PretupsI.AMOUNT_TYPE_PERCENTAGE);// Tax
                                                                                                       // two
                                                                                                       // type
                                    pstmtChnlTrnfItems.setDouble(++m, 0);// Tax
                                                                         // two
                                                                         // rate
                                    pstmtChnlTrnfItems.setLong(++m, 0);// Tax
                                                                       // two
                                                                       // value
                                    pstmtChnlTrnfItems.setString(++m, PretupsI.AMOUNT_TYPE_PERCENTAGE);// Tax
                                                                                                       // three
                                                                                                       // type
                                    pstmtChnlTrnfItems.setDouble(++m, 0);// Tax
                                                                         // three
                                                                         // rate
                                    pstmtChnlTrnfItems.setLong(++m, 0);// Tax
                                                                       // three
                                                                       // value
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);// Payable
                                                                                       // amount
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);// Net
                                                                                       // payable
                                                                                       // amount
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);// Product
                                                                                       // total
                                                                                       // MRP
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);
                                    pstmtChnlTrnfItems.setLong(++m, 0);
                                    pstmtChnlTrnfItems.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    // Insert the record
                                    addCount = pstmtChnlTrnfItems.executeUpdate();
                                    addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres
                                    if (addCount <= 0) {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for C2C transfer in Channel Transfer Items is failed.", "Count =" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert for C2C transfer in Channel Transfer Items is failed :: count :" + addCount);
                                        userMsgErrorList.add(userMsg);
                                        writeOutPutFile(fwriterFail, userMsg);
                                        throw new BTSLBaseException(this, "userMigrationProcess", "Insertion for C2C transfer is failed.");
                                    } else {
                                        UserMigrDetailLog.log("userMigrationProcess", "Insertion for C2C transfer is sucessfull.", "Count =" + addCount);
                                        userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), " User migrated successfully in the system. ,TO CATEGORY : ," + usrMigrVO.getToUserCatCode() + "WITH NEW USERID :" + newUserID);
                                        userMsgSuccessList.add(userMsg);
                                        p_con.commit();
                                        // p_con.rollback();
                                        sucMigUser++;
                                        writeOutPutFile(fwriterSucess, userMsg);
                                        // based on flag set at props send sms
                                        // to the migrated users.
                                        if (PretupsI.YES.equalsIgnoreCase(sendSms))
                                            sendMessageToMigratedUsers(usrMigrVO);
                                        UserMigrDetailLog.log("userMigrationProcess", "", "User Migrated SucessFully.");
                                    }
                                    // Clear the parameter
                                    pstmtChnlTrnfItems.clearParameters();
                                } catch (SQLException sqe) {
                                    UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), sqe.getMessage());
                                    userMsg = new UserMessageVO(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserCatCode(), "Insert for C2C transfer is failed.");
                                    userMsgErrorList.add(userMsg);
                                    writeOutPutFile(fwriterFail, userMsg);
                                    LOG.errorTrace(METHOD_NAME, sqe);
                                    throw new BTSLBaseException(this, "userMigrationProcess", " Insertion in CHANNEL_TRANSFER_ITEMS failed");
                                }
                                /*
                                 * userMsg=new UserMessageVO(lineNo,
                                 * usrMigrVO.getFromUserMsisdn(),
                                 * usrMigrVO.getFromUserCatCode(),
                                 * " User migrated successfully in the system. ,TO CATEGORY : ,"
                                 * +
                                 * usrMigrVO.getToUserCatCode()+"WITH NEW USERID :"
                                 * +newUserID);
                                 * userMsgSuccessList.add(userMsg);
                                 * p_con.commit();
                                 * //based on flag set at props send sms to the
                                 * migrated users.
                                 * if(PretupsI.YES.equalsIgnoreCase(sendSms))
                                 * sendMessageToMigratedUsers(usrMigrVO);
                                 */
                            } catch (BTSLBaseException be) {
                                UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), usrMigrVO.getFromUserID(), newUserID, usrMigrVO.getToUserGeoCode(), usrMigrVO.getFromUserGeoCode(), "Operation RollBacked");
                                LOG.errorTrace(METHOD_NAME, be);
                                p_con.rollback();
                                continue;
                            }
                        } else {
                            UserMigrDetailLog.log(lineNo, usrMigrVO.getFromUserMsisdn(), " New category code: " + usrMigrVO.getToUserCatCode(), " and new geo_domain_code: " + usrMigrVO.getToUserGeoCode(), " are from different geographical domain type", "", "");
                            throw new BTSLBaseException(this, "userMigrationProcess", " GRPH_DOMAIN_TYPE are different for the new category and geo_domain code, so user can't be migrated.");
                        }
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                        continue;
                    }
                }
            }
        }
        /*
         * catch(BTSLBaseException be)
         * {
         * UserMigrDetailLog.log("Exception occured before entering the loop.",""
         * ,"");
         * throw be;
         * }
         */
        catch (Exception e) {
            UserMigrDetailLog.log("userMigrationProcess", "", "Exception " + e.getMessage());
            addCount = 0;
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "userMigrationProcess", "error.general.processing");
        }// end of catch
        finally {
        	try {
                if (rsCheckGeoType  != null)
                	rsCheckGeoType .close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (pstmtChnlTrnfItems  != null)
                	pstmtChnlTrnfItems .close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectGeo != null)
                    pstmtSelectGeo.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtselectPrntId != null)
                	psmtselectPrntId.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtCheckGeoType != null)
                	pstmtCheckGeoType.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdateUserStatus != null)
                	psmtupdateUserStatus.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtChannelTrnf != null)
                	pstmtChannelTrnf.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectGeoWithCheck != null)
                    pstmtSelectGeoWithCheck.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdateUser != null)
                    pstmtUpdateUser.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsertUser != null)
                    pstmtInsertUser.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsertUserGeographies != null)
                    pstmtInsertUserGeographies.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            // try{if(pstmtInsertUserDomains!=null)
            // pstmtInsertUserDomains.close();}catch(Exception e){}
            try {
                if (pstmtInsertUserProductTypes != null)
                    pstmtInsertUserProductTypes.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsertUserRoles != null)
                    pstmtInsertUserRoles.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsertUserServices != null)
                    pstmtInsertUserServices.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectUserBalance != null)
                    rsSelectUserBalance.close();
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelectUserBalance != null)
                    pstmtSelectUserBalance.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsertUserBalance != null)
                    pstmtInsertUserBalance.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdateUserBalance != null)
                    pstmtUpdateUserBalance.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsertUserPhones != null)
                    pstmtInsertUserPhones.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectChannelUserInfo != null)
                    rsSelectChannelUserInfo.close();
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelectChannelUserInfo != null)
                    pstmtSelectChannelUserInfo.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsertChannelUserInfo != null)
                    pstmtInsertChannelUserInfo.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectParent != null)
                    pstmtSelectParent.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectParent != null)
                    rsSelectParent.close();
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
            }

            try {
                fwriterFail.close();
                fwriterSucess.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            p_migrationDetailMap.put("MIGRATED_USER", String.valueOf(sucMigUser));
            p_migrationDetailMap.put("FAILED_USER_WHILE_MIGRATION", String.valueOf(userMsgErrorList.size()));

        }// end of finally
    }

    /**
     * This method writes the success/fail migrated users in
     * SUCCESSFUL_MigratedUser.csv/ERROR_MigratedUser.csv file respectively.
     * 
     * @param p_fileName
     * @param p_list
     * @throws BTSLBaseException
     */
    private void writeOutPutFile(FileWriter p_filewriter, UserMessageVO p_usermessageVo) throws BTSLBaseException
    // private void writeOutPutFile(String p_fileName,List<UserMessageVO>
    // p_list) throws BTSLBaseException
    {
        UserMigrDetailLog.log("writeOutPutFile", "", "Entered: " + p_usermessageVo);
        final String METHOD_NAME = "writeOutPutFile";
        String migratedUserMsg = null;
        migratedUserMsg = "RECORD NUMBER:" + p_usermessageVo.getLineNo() + ",MSISDN: ," + p_usermessageVo.getMsg1() + ",To_Category_Code: " + p_usermessageVo.getMsg2() + " " + p_usermessageVo.getReason() + "\n";
        try {
            p_filewriter.append(migratedUserMsg);
            p_filewriter.flush();
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
        }
        UserMigrDetailLog.log("writeOutPutFile", "", "Exiting :");
    }

    /**
     * Method generate UserId.
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return String
     * @throws Exception
     */
    private String generateUserId(Connection p_con, String p_networkCode, String p_prefix) throws Exception {
        UserMigrDetailLog.log("generateUserId", "", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((UserMigrationDAO.getNextID(p_con, TypesI.USERID, TypesI.ALL, p_networkCode, null)) + "", length);
        id = p_networkCode + p_prefix + id;
        UserMigrDetailLog.log("generateUserId", "Exiting :", "id =" + id);
        return id;
    }

    /**
     * This method generated the phone Id .
     * 
     * @param p_con
     * @return
     * @throws Exception
     */
    private String generatePhoneId(Connection p_con) throws Exception {
        UserMigrDetailLog.log("generatePhoneId", "", "Entered :");
        String phoneId = null;
        IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
        long id = _idGeneratorDAO.getNextID(p_con, "PHONE_ID", TypesI.ALL, TypesI.ALL, null);
        phoneId = String.valueOf(id);
        UserMigrDetailLog.log("generatePhoneId", "Exiting :", "phoneId : " + phoneId);
        return phoneId;
    }

    /**
     * This method is used by generateUserId() for generating the next id from
     * database.
     * 
     * @param p_con
     * @param p_idType
     * @param p_year
     * @param p_networkID
     * @param p_currentDate
     * @return
     * @throws BTSLBaseException
     */
    public static long getNextID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        UserMigrDetailLog.log("getNextID", "", "Entered :");
        IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
        long id = _idGeneratorDAO.getNextID(p_con, p_idType, p_year, p_networkID, p_currentDate);
        UserMigrDetailLog.log("getNextID", "Exiting :", "Id : " + id);
        return id;
    }

    /**
     * This method pushes the sms to the migrated users /From Parent /To Parent.
     * 
     * @param p_usermigrationVo
     */
    public void sendMessageToMigratedUsers(UserMigrationVO p_usermigrationVo) {
        UserMigrDetailLog.log("sendMessageToMigratedUsers", "Entered :", " networkCode: " + p_usermigrationVo.getNetworkCode() + " user_phone_language : " + p_usermigrationVo.getPhoneLang() + " user_country_code: " + p_usermigrationVo.getCountry() + " FromUserParentName: " + p_usermigrationVo.getFromUserParentName() + " ToUserParentName: " + p_usermigrationVo.getToUserParentName() + " FromParentMsisdn: " + p_usermigrationVo.getFromParentMsisdn() + " FromUserName: " + p_usermigrationVo.getFromUserName() + " ToParentMsisdn: " + p_usermigrationVo.getToParentMsisdn());
        String[] arr = new String[2];
        String networkCode = p_usermigrationVo.getNetworkCode();
        arr[0] = p_usermigrationVo.getFromParentMsisdn();
        // arr[0]=p_usermigrationVo.getFromUserParentName();
        // arr[1]=p_usermigrationVo.getToParentMsisdn();
        arr[1] = p_usermigrationVo.getToUserParentName();
        Locale locale = new Locale(p_usermigrationVo.getPhoneLang(), p_usermigrationVo.getCountry());
        BTSLMessages btslMessage = new BTSLMessages(MESG_CHILD, arr);
        PushMessage pushMessage = new PushMessage(p_usermigrationVo.getToUserMsisdn(), btslMessage, null, null, locale, networkCode);
        pushMessage.push();

        // From parent user
        // BTSLMessages btslMessageFrom = new
        // BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_SENDER_PARENT,new
        // String[]{p_usermigrationVo.getFromUserName()});
        // PushMessage pushMessageFrom=new
        // PushMessage(p_usermigrationVo.getFromParentMsisdn(),btslMessageFrom,null,null,locale,networkCode);
        // pushMessageFrom.push();

        // To Parent User
        // to avoid the replicated sms to same parent .
        // if(!(p_usermigrationVo.getFromParentMsisdn().equalsIgnoreCase(p_usermigrationVo.getToParentMsisdn())))
        // {
        BTSLMessages btslMessageTo = new BTSLMessages(MESG_PARENT, new String[] { p_usermigrationVo.getToUserMsisdn() });
        // BTSLMessages btslMessageTo = new BTSLMessages(MESG_PARENT,new
        // String[]{p_usermigrationVo.getFromUserName()});
        PushMessage pushMessageTo = new PushMessage(p_usermigrationVo.getToParentMsisdn(), btslMessageTo, null, null, locale, networkCode);
        pushMessageTo.push();
        // }
        UserMigrDetailLog.log("sendMessageToMigratedUsers", "", "Exited :");
    }

    /**
     * This method creates the filewriter stream for the sucess and fail outpt
     * files.
     * 
     */
    public static void createOutPutFiles() throws BTSLBaseException {
        dir = Constants.getProperty("DIR_PATH");
        UserMigrDetailLog.log("createOutPutFiles", "Entered :  dir =", dir);
        final String METHOD_NAME = "createOutPutFiles";
        boolean success = false;
        String successOutPutFile = "SUCCESSFUL_MigratedUser.csv";
        String failOutPutFile = "ERROR_MigratedUser.csv";
        if (BTSLUtil.isNullString(dir)) {
            UserMigrDetailLog.log("createOutPutFiles", "", " DIR_PATH is not defined in the property file.");
            throw new BTSLBaseException("createOutPutFiles", "DIR_PATH is not defined in the property file.");
        }
        File newDir = new File(dir);
        if (!newDir.exists())
            success = newDir.mkdirs();
        else
            success = true;

        if (!success)
            throw new BTSLBaseException("createOutPutFiles", "Not Able To Create the" + dir + " Direcoty.");
        else {
            try {
                fileObjectSuccess = new File(dir + successOutPutFile);
                fileObjectFail = new File(dir + failOutPutFile);

                if (fileObjectSuccess == null) {
                    UserMigrDetailLog.log("createOutPutFiles", "Error in creating fileObject sucessoutput file at", dir + successOutPutFile);
                    throw new BTSLBaseException("UserMigrationDAO", "createOutPutFiles", "Error in loading sucessfull-migrated user file..");
                }
                if (fileObjectFail == null) {
                    UserMigrDetailLog.log("createOutPutFiles", "Error in creating fileObject Failoutput file at", dir + failOutPutFile);
                    throw new BTSLBaseException("UserMigrationDAO", "createOutPutFiles", "Error in loading failed-migrated user file..");
                }
                fwriterSucess = new FileWriter(fileObjectSuccess);
                fwriterFail = new FileWriter(fileObjectFail);

                if (fwriterSucess == null) {
                    UserMigrDetailLog.log("createOutPutFiles", "Error in creating fileWriterObject sucessoutput file at", dir + successOutPutFile);
                    throw new BTSLBaseException("UserMigrationDAO", "createOutPutFiles", "Error in loading sucessfull-migrated user file..");
                }
                if (fwriterFail == null) {
                    UserMigrDetailLog.log("createOutPutFiles", "Error in creating fileWriterObject Failoutput file at", dir + failOutPutFile);
                    throw new BTSLBaseException("UserMigrationDAO", "createOutPutFiles", "Error in loading failed-migrated user file..");
                }
            } catch (IOException ioe) {
                LOG.errorTrace(METHOD_NAME, ioe);
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            } finally {
                UserMigrDetailLog.log("createOutPutFiles Exiting with", " fileObjectSuccess=" + fileObjectSuccess, " & fileObjectFail=" + fileObjectFail);
            }
        }
    }

    /*
     * public ArrayList<UserMigrationVO> validateChildCounts(Connection p_con,
     * List<UserMigrationVO> p_userMigrationList, List<UserMigrationVO>
     * p_errorUserList) throws BTSLBaseException
     * {
     * UserMigrDetailLog.log("validateChildCounts","",
     * "Entered: p_fromUserList.size()=" + p_userMigrationList.size());
     * PreparedStatement pstmtActiveChildCount = null;
     * PreparedStatement pendingTxnPstmt = null;
     * ResultSet rs = null;
     * ResultSet rsPendingTxn = null;
     * ResultSet rsOperatorUser = null;
     * StringBuilder strActiveChildUser =null;
     * StringBuilder strActiveChildCount =null;
     * UserMigrationVO userMigrationVO=null;
     * String sqlSelectParent =null;
     * PreparedStatement pstmtSelectParent=null;
     * ArrayList<UserMigrationVO> validUsersList=new
     * ArrayList<UserMigrationVO>();
     * ResultSet rsSelectParent=null;
     * StringBuilder strBuffSelectParent =null;
     * 
     * try
     * {
     * strActiveChildCount=new StringBuilder();
     * strActiveChildCount.append(" SELECT count(1) ");
     * strActiveChildCount.append(" FROM USERS U WHERE ");
     * strActiveChildCount.append(
     * " AND u.user_type='CHANNEL' AND u.status<>'N'and u.status<>'C'");
     * strActiveChildCount.append(" AND u.parent_id=? ");
     * String sqlActiveChildCount = strActiveChildCount.toString();
     * UserMigrDetailLog.log("validateChildCounts","",
     * "QUERY sqlActiveChildCount=" + sqlActiveChildCount);
     * 
     * strActiveChildUser = new StringBuilder();
     * strActiveChildUser.append(
     * " SELECT u.user_name,u.user_id, u.login_id, u.network_code,u.status,");
     * strActiveChildUser.append(
     * " up.user_name,p.phone_language,p.country,pct.sequence_no, pct.grph_domain_type"
     * );
     * strActiveChildUser.append(
     * " FROM USERS U,CATEGORIES CT,USERS UP,USER_PHONES P,CATEGORIES PCT");
     * strActiveChildUser.append(
     * " WHERE u.category_code=ct.category_code AND P.USER_ID=U.USER_ID");
     * strActiveChildUser.append(
     * " AND u.user_type='CHANNEL' AND u.status<>'N'and u.status<>'C'");
     * strActiveChildUser.append(" AND ct.category_code=? AND u.msisdn=? ");
     * 
     * String sqlActiveChildUser = strActiveChildUser.toString();
     * UserMigrDetailLog.log("validateChildCounts","",
     * "QUERY sqlActiveChildUser=" + sqlActiveChildUser);
     * 
     * strBuffSelectParent = new StringBuilder();
     * strBuffSelectParent.append(
     * " SELECT U.user_id,U.parent_id, U.owner_id, U.status, U.user_name,");
     * strBuffSelectParent.append(" U.category_code,CAT.domain_code");
     * strBuffSelectParent.append(" FROM USERS U, CATEGORIES CAT");
     * strBuffSelectParent.append(" WHERE U.status <>'N' and U.status <>'C'");
     * strBuffSelectParent.append(" AND U.category_code=CAT.category_code");
     * strBuffSelectParent.append(" AND U.msisdn=?");
     * 
     * ResultSet rsCheckDomain=null;
     * String toUserDomain=null;
     * String toParentDomain=null;
     * 
     * sqlSelectParent = strBuffSelectParent.toString();
     * UserMigrDetailLog.log("validateChildCounts","", "QUERY sqlSelectParent="
     * + sqlSelectParent);
     * 
     * pstmtActiveChildCount = p_con.prepareStatement(sqlActiveChildCount);
     * pendingTxnPstmt=p_con.prepareStatement(sqlActiveChildCount);
     * pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);
     * 
     * 
     * for(int i=0, j=p_userMigrationList.size(); i<j; i++)
     * {
     * userMigrationVO=p_userMigrationList.get(i);
     * 
     * pstmtActiveChildCount.setString(1, userMigrationVO.getFromUserID());
     * rs = pstmtActiveChildCount.executeQuery();
     * if(rs.next() == )
     * validUsersList.add(userMigrationVO);
     * else
     * {
     * 
     * userMigrationVO.setMessage(
     * "User can't be migrated as To parent Domain does not match To user Domain."
     * );
     * userMigrationVO.setParentExist(false);
     * p_errorUserList.add(userMigrationVO);
     * }
     * 
     * if (rs.next())
     * {
     * userMigrationVO.setFromUserID(rs.getString("user_id"));
     * userMigrationVO.setFromUserLoginID(rs.getString("login_id"));
     * userMigrationVO.setNetworkCode(rs.getString("network_code"));
     * userMigrationVO.setFromUserStatus(rs.getString("status"));
     * userMigrationVO.setFromUserParentName(rs.getString("USER_NAME"));
     * userMigrationVO.setPhoneLang(rs.getString("PHONE_LANGUAGE"));
     * userMigrationVO.setCountry(rs.getString("COUNTRY"));
     * userMigrationVO.setFromUserName(rs.getString("user_name"));
     * userMigrationVO.setToUserCatCodeSeqNo(rs.getString("sequence_no"));
     * userMigrationVO.setToGeoDomainType(rs.getString("grph_domain_type"));
     * 
     * pendingTxnPstmt.setString(1, userMigrationVO.getFromUserID());
     * pendingTxnPstmt.setString(2, userMigrationVO.getFromUserID());
     * pendingTxnPstmt.setString(3, "CLOSE");
     * pendingTxnPstmt.setString(4, "CNCL");
     * rsPendingTxn=pendingTxnPstmt.executeQuery();
     * if(rsPendingTxn.next())
     * {
     * userMigrationVO.setMessage("Pending transaction is found for the user.");
     * p_errorUserList.add(userMigrationVO);
     * }
     * else
     * {
     * operatorUserPstmt.setString(1, userMigrationVO.getToUserCatCode());
     * rsOperatorUser=operatorUserPstmt.executeQuery();
     * if(rsOperatorUser.next())
     * {
     * //Now validate the to parent user
     * pstmtSelectParent.setString(1, userMigrationVO.getToParentMsisdn());
     * rsSelectParent=pstmtSelectParent.executeQuery();
     * if(rsSelectParent.next())
     * {
     * //If To user category sequence is "1", then after migration, user's
     * parent will be ROOT
     * if("1".equals(userMigrationVO.getToUserCatCodeSeqNo()))
     * {
     * userMigrationVO.setToParentID("ROOT");
     * }
     * else
     * userMigrationVO.setToParentID(rsSelectParent.getString("user_id"));
     * userMigrationVO.setToOwnerID(rsSelectParent.getString("owner_id"));
     * userMigrationVO.setToUserParentName(rsSelectParent.getString("USER_NAME"))
     * ;
     * 
     * if(toUserDomain.equalsIgnoreCase(toParentDomain))
     * validUsersList.add(userMigrationVO);
     * else
     * {
     * 
     * userMigrationVO.setMessage(
     * "User can't be migrated as To parent Domain does not match To user Domain."
     * );
     * userMigrationVO.setParentExist(false);
     * p_errorUserList.add(userMigrationVO);
     * }
     * }
     * else
     * {
     * userMigrationVO.setMessage("Parent user does not found in the system.");
     * userMigrationVO.setParentExist(false);
     * p_errorUserList.add(userMigrationVO);
     * }
     * }
     * else
     * {
     * userMigrationVO.setMessage("User catn't be migrated as an Operator user.")
     * ;
     * userMigrationVO.setParentExist(false);
     * p_errorUserList.add(userMigrationVO);
     * }
     * }
     * }
     * else
     * {
     * userMigrationVO.setMessage("User is not found.");
     * p_errorUserList.add(userMigrationVO);
     * }
     * pendingTxnPstmt.clearParameters();
     * pstmtActiveChildCount.clearParameters();
     * pstmtSelectParent.clearParameters();
     * }
     * return validUsersList;
     * }
     * catch (SQLException sqe)
     * {
     * UserMigrDetailLog.log("validateChildCounts","", "SQLException : " + sqe);
     * sqe.printStackTrace();
     * throw new BTSLBaseException(this, "validateChildCounts",
     * "error.general.sql.processing");
     * }
     * catch (Exception ex)
     * {
     * UserMigrDetailLog.log("validateChildCounts","", "Exception : " + ex);
     * ex.printStackTrace();
     * throw new BTSLBaseException(this, "validateChildCounts",
     * "error.general.processing");
     * }
     * finally
     * {
     * try{if (pstmtActiveChildCount != null){pstmtActiveChildCount.close();}}
     * catch (Exception e){}
     * try{if (rs != null){rs.close();}} catch (Exception e){}
     * try{if (rsPendingTxn != null){rsPendingTxn.close();}} catch (Exception
     * e){}
     * try{if (pendingTxnPstmt != null){pendingTxnPstmt.close();}} catch
     * (Exception e){}
     * try{if (pstmtSelectParent != null){pstmtSelectParent.close();}} catch
     * (Exception e){}
     * try{if (rsSelectParent != null){rsSelectParent.close();}} catch
     * (Exception e){}
     * try{if (operatorUserPstmt != null){operatorUserPstmt.close();}} catch
     * (Exception e){}
     * try{if (rsOperatorUser != null){rsOperatorUser.close();}} catch
     * (Exception e){}
     * UserMigrDetailLog.log("validateChildCounts","",
     * "Exiting with validUsersList.size="
     * +validUsersList.size()+" p_errorUserList.size="+p_errorUserList.size());
     * }
     * }
     */

    public void validateChildCounts(List<UserMigrationVO> p_userMigrationList, List<UserMigrationVO> p_errorParentUserList, HashMap<String, String> p_migrationDetailMap) throws BTSLBaseException {
        UserMigrDetailLog.log("validateChildCounts", "", "Entered: p_fromUserList.size()=" + p_userMigrationList.size());
        final String METHOD_NAME = "validateChildCounts";
        UserMigrationVO userMigrationVO = null;
        UserMigrationVO userMigrationVO1 = null;
        try {
            int childCount = 0;
            createOutPutFiles1();
            for (int i = 0, j = p_userMigrationList.size(); i < j; i++) {
                childCount = 0;
                userMigrationVO = p_userMigrationList.get(i);
                if (!userMigrationVO.getActiveChildUserCount().equalsIgnoreCase("0")) {
                    for (int k = 0, l = p_userMigrationList.size(); k < l; k++) {
                        userMigrationVO1 = p_userMigrationList.get(k);
                        UserMigrDetailLog.log("validateChildCounts", "", "userMigrationVO1.getFromUserParentId()=" + userMigrationVO1.getFromUserParentId() + "  userMigrationVO.getFromUserID()=" + userMigrationVO.getFromUserID());
                        if (userMigrationVO1.getFromUserParentId().equalsIgnoreCase(userMigrationVO.getFromUserID())) {
                            childCount++;
                        }
                    }
                    if (!userMigrationVO.getActiveChildUserCount().equalsIgnoreCase(Integer.toString(childCount))) {
                        UserMigrDetailLog.log("validateChildCounts", "", "User catn't be migrated as having more active child user userMigrationVO.getFromUserID()" + userMigrationVO.getFromUserID());
                        userMigrationVO.setMessage("User catn't be migrated as having more active child user");
                        userMigrationVO.setParentExist(false);
                        // userMsgErrorList.add(userMsg);
                        writeOutPutFile(fwriterParentChildFail, new UserMessageVO(userMigrationVO.getLineNumber(), userMigrationVO.getFromUserMsisdn(), userMigrationVO.getFromUserCatCode(), "User catn't be migrated as having more active child user userMigrationVO.getFromUserID()" + userMigrationVO.getFromUserID()));
                        p_errorParentUserList.add(userMigrationVO);
                    }
                }
                UserMigrDetailLog.log("validateChildCounts", "", "userMigrationVO.setActiveChildUserCount=" + userMigrationVO.getActiveChildUserCount() + "  userMigrationVO.getFromUserID()=" + userMigrationVO.getFromUserID() + "childcounts =" + childCount);
            }
        } catch (Exception ex) {
            UserMigrDetailLog.log("validateChildCounts", "", "Exception : " + ex);
            LOG.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "validateChildCounts", "error.general.processing");
        } finally {
            try {
                fwriterParentChildFail.close();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            p_migrationDetailMap.put("FAILED_PARENT_CHILD_DETAILS", String.valueOf(p_errorParentUserList.size()));
            UserMigrDetailLog.log("validateChildCounts", "", "Exiting ");
        }
    }

    public static void createOutPutFiles1() throws BTSLBaseException {
        dir = Constants.getProperty("DIR_PATH");
        UserMigrDetailLog.log("createOutPutFiles", "Entered :  dir =", dir);
        final String METHOD_NAME = "createOutPutFiles1";
        boolean success = false;
        String failParentChildOutPutFile = "ERROR_ParentChildDetails.csv";
        if (BTSLUtil.isNullString(dir)) {
            UserMigrDetailLog.log("createOutPutFiles", "", " DIR_PATH is not defined in the property file.");
            throw new BTSLBaseException("createOutPutFiles", "DIR_PATH is not defined in the property file.");
        }
        File newDir = new File(dir);
        if (!newDir.exists())
            success = newDir.mkdirs();
        else
            success = true;

        if (!success)
            throw new BTSLBaseException("createOutPutFiles", "Not Able To Create the" + dir + " Direcoty.");
        else {
            try {
                fileObjectParentChildFail = new File(dir + failParentChildOutPutFile);

                if (fileObjectParentChildFail == null) {
                    UserMigrDetailLog.log("createOutPutFiles", "Error in creating fileObject Failoutput file at", dir + failParentChildOutPutFile);
                    throw new BTSLBaseException("UserMigrationDAO", "createOutPutFiles", "Error in loading parent child user details file..");
                }
                fwriterParentChildFail = new FileWriter(fileObjectParentChildFail);

                if (fwriterParentChildFail == null) {
                    UserMigrDetailLog.log("createOutPutFiles", "Error in creating fileWriterObject Failoutput file at", dir + failParentChildOutPutFile);
                    throw new BTSLBaseException("UserMigrationDAO", "createOutPutFiles", "Error in loading parent child user details file..");
                }
            } catch (IOException ioe) {
                LOG.errorTrace(METHOD_NAME, ioe);
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            } finally {
                UserMigrDetailLog.log("createOutPutFiles Exiting with", " fileObjectSuccess=" + fileObjectSuccess, " & fileObjectFail=" + fileObjectFail);
            }
        }
    }

}
