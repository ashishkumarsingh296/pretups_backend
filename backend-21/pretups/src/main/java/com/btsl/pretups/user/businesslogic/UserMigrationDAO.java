package com.btsl.pretups.user.businesslogic;

/*
 * @# UserMigrationDAO.java
 * ------------------------------------------------------------------------------
 * ----------------------
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * ----------------------
 * Puneet Raj Srivastava 09_Oct 2010 Initial Creation
 * Vinay Kumar Singh 14-FEB-2013 Bug fixing raised in MOLDOVA 5.7 version
 * upgrade.
 * ------------------------------------------------------------------------------
 * ----------------------
 * Copyright(c) 2010 Comviva Technologies Ltd.
 */
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
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class UserMigrationDAO {
    /**
     * Commons Logging instance.
     */
    public static final  Log log = LogFactory.getLog(UserMigrationDAO.class.getName());
    private static String exception = "Exception: ";
    private static String  errorGeneralSqlProcessing = "error.general.sql.processing";
    private static String  errorGeneralProcessing =  "error.general.processing";
    private static String  exiting= "Exiting ";
    private static String  entered="Entered ";
    private static String  errorMessage="ERROR_MSG: ";
    
    private static String count =  "Count: ";
    private static String query = "QUERY:";
    private UserMigrationQry userMigrationQry = (UserMigrationQry)ObjectProducer.getObject(QueryConstants.USER_MIGRATION_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * Method for checking Is MSISDN exist or not.
     * 
     * @param p_con
     *            Connection
     * @param p_userMigrationList
     *            ArrayList<FromUserVO>
     * @param p_validUserList
     *            ArrayList<FromUserVO>
     * @param p_invalidUserList
     *            ArrayList<FromUserVO>
     * @throws BTSLBaseException
     */
    public ArrayList<UserMigrationVO> validateFromUsers(Connection p_con, List<UserMigrationVO> p_userMigrationList, ArrayList<ListValueVO> p_invalidUserList) throws BTSLBaseException {
        final String methodName = "validateFromUsers";
        log.debug(methodName, "", "Entered: p_userMigrationList.size()=" + p_userMigrationList.size());

        PreparedStatement pstmt = null;
        PreparedStatement pendingTxnPstmt = null;
        PreparedStatement ambC2STxnPstmt = null;
        PreparedStatement operatorUserPstmt = null;
        ResultSet rs = null;
        ResultSet rsPendingTxn = null;
        ResultSet rsAmbC2STxn = null;
        ResultSet rsOperatorUser = null;
        StringBuilder strBuff = null;
        StringBuilder strPendingTxnBuff = null;
        StringBuilder strC2SAmbiTxnBuff = null;
        UserMigrationVO userMigrationVO = null;
        String sqlSelectParent = null;
        PreparedStatement pstmtSelectParent = null;
        ListValueVO errorVO = new ListValueVO();
        ResultSet rsSelectParent = null;
        StringBuilder strBuffSelectParent = null;
        final ArrayList<UserMigrationVO> validUserList = new ArrayList<>();
        PreparedStatement psmtCheckDomain = null;
        ResultSet rsCheckDomain = null;
        try {
            // Validate from parent geographical domain code, from parent
            // category code and from user MSISDN
            strBuff = new StringBuilder();
            strBuff.append(" SELECT u.user_name,u.user_id, u.login_id, u.network_code,u.status,");
            strBuff.append(" up.user_name,p.phone_language,p.country,pct.sequence_no, pct.grph_domain_type,u.category_code ");
            strBuff.append(" FROM USERS U,CATEGORIES CT,USERS UP,USER_PHONES P,CATEGORIES PCT ,user_phones uup");
            strBuff.append(" WHERE u.category_code=ct.category_code AND P.USER_ID=U.USER_ID");
            strBuff.append(" AND u.user_type='CHANNEL' AND u.status<>'N'and u.status<>'C'");
            strBuff.append(" AND ct.category_code=? AND u.msisdn=? ");
            strBuff.append(" AND pct.category_code=?");
            strBuff.append(" AND (up.USER_ID=Case  WHEN u.parent_id = 'ROOT' then u.user_id else u.parent_id end)");
            strBuff.append(" AND up.user_id = uup.user_id  AND uup.msisdn = ? ");
            final String sqlSelect = strBuff.toString();
            log.debug(methodName, "", query + sqlSelect);

            // Query for pending C2C or O2C transaction.
            strPendingTxnBuff = new StringBuilder();
            strPendingTxnBuff.append(" SELECT 1  FROM CHANNEL_TRANSFERS");
            strPendingTxnBuff.append(" WHERE (from_user_id=? OR to_user_id =?) AND ");
            strPendingTxnBuff.append(" (status <> ? AND status <> ? )");
            final String sqlPendingSelect = strPendingTxnBuff.toString();
            log.debug(methodName, "", query + sqlPendingSelect);

            // Query for any C2S ambiguous transaction
            strC2SAmbiTxnBuff = new StringBuilder();
            //local_index_missing
            strC2SAmbiTxnBuff.append("SELECT 1  FROM C2S_TRANSFERS ");
            strC2SAmbiTxnBuff.append("WHERE transfer_date > (select executed_upto from process_status where process_id = 'C2SMIS') and sender_id=? AND (TRANSFER_STATUS =? OR TRANSFER_STATUS =?)");
            final String sqlC2SAmbiTxnSelect = strC2SAmbiTxnBuff.toString();
            log.debug(methodName, "", query + sqlC2SAmbiTxnSelect);

            // Check for OPERATOR user
            final StringBuilder sqlOptSelBuff = new StringBuilder();
            sqlOptSelBuff.append(" SELECT 1 FROM CATEGORIES cat");
            sqlOptSelBuff.append(" WHERE  cat.status<> 'N' AND cat.domain_code <>'OPT' AND cat.category_code=?");
            final String sqlOptSelect = sqlOptSelBuff.toString();
            log.debug(methodName, "", query + sqlOptSelect);

            // Check parent info.
            strBuffSelectParent = new StringBuilder();
            strBuffSelectParent.append(" SELECT U.user_id,U.parent_id, U.owner_id, U.status, U.user_name,");
            strBuffSelectParent.append(" U.category_code,CAT.domain_code");
            strBuffSelectParent.append(" FROM USERS U, CATEGORIES CAT");
            strBuffSelectParent.append(" WHERE U.status <>'N' and U.status <>'C'");
            strBuffSelectParent.append(" AND U.category_code=CAT.category_code");
            strBuffSelectParent.append(" AND U.msisdn=?");
            sqlSelectParent = strBuffSelectParent.toString();
            log.debug(methodName, "", query + sqlSelectParent);

            // Check Domain info.
            final String checkDomain = "select DOMAIN_CODE from categories where STATUS='Y' and CATEGORY_CODE=?";
            log.debug(methodName, "", query + checkDomain);

            psmtCheckDomain = p_con.prepareStatement(checkDomain);
           
            String toUserDomain = null;
            String toParentDomain = null;

            pstmt = p_con.prepareStatement(sqlSelect);
            pendingTxnPstmt = p_con.prepareStatement(sqlPendingSelect);
            ambC2STxnPstmt = p_con.prepareStatement(sqlC2SAmbiTxnSelect);
            operatorUserPstmt = p_con.prepareStatement(sqlOptSelect);
            pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);

            final int migrationListSize = p_userMigrationList.size();
            for (int i = 0, j = migrationListSize; i < j; i++) {
                userMigrationVO =  p_userMigrationList.get(i);
                log.debug(methodName,
                                "",
                                "FromUserCatCode=" + userMigrationVO.getFromUserCatCode() + ", FromUserMsisdn=" + userMigrationVO.getFromUserMsisdn() + ", ToUserCatCode=" + userMigrationVO
                                                .getToUserCatCode());

                pstmt.setString(1, userMigrationVO.getFromUserCatCode());
                pstmt.setString(2, userMigrationVO.getFromUserMsisdn());
                pstmt.setString(3, userMigrationVO.getToUserCatCode());
                pstmt.setString(4, userMigrationVO.getFromParentMsisdn());

                rs = pstmt.executeQuery();
                if (rs.next()) {
                    final String categoryCode = rs.getString("category_code");
                    log.debug(methodName, "DB CategoryCode=" + categoryCode + ", Provided CategoryCode=" + userMigrationVO.getFromUserCatCode());

                    userMigrationVO.setFromUserID(rs.getString("user_id"));
                    userMigrationVO.setFromUserLoginID(rs.getString("login_id"));
                    userMigrationVO.setNetworkCode(rs.getString("network_code"));
                    userMigrationVO.setFromUserStatus(rs.getString("status"));
                    userMigrationVO.setFromUserParentName(rs.getString("USER_NAME"));
                    userMigrationVO.setPhoneLang(rs.getString("PHONE_LANGUAGE"));
                    userMigrationVO.setCountry(rs.getString("COUNTRY"));
                    userMigrationVO.setFromUserName(rs.getString("user_name"));
                    userMigrationVO.setToUserCatCodeSeqNo(rs.getString("sequence_no"));
                    userMigrationVO.setToGeoDomainType(rs.getString("grph_domain_type"));

                    // Check whether there is any pending C2C or O2C transaction
                    pendingTxnPstmt.setString(1, userMigrationVO.getFromUserID());
                    pendingTxnPstmt.setString(2, userMigrationVO.getFromUserID());
                    pendingTxnPstmt.setString(3, "CLOSE");
                    pendingTxnPstmt.setString(4, "CNCL");
                    rsPendingTxn = pendingTxnPstmt.executeQuery();

                    // Check whether there is any C2S ambiguous transaction.
                    ambC2STxnPstmt.setString(1, userMigrationVO.getFromUserID());
                    ambC2STxnPstmt.setString(2, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                    ambC2STxnPstmt.setString(3, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
                    rsAmbC2STxn = ambC2STxnPstmt.executeQuery();

                    if (rsPendingTxn.next()) {
                        userMigrationVO.setMessage("Pending O2C or C2C transaction is found for the user.");
                        userMigrationVO.setLineNumber(i);
                        errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), "Pending O2C or C2C transaction is found for the user.");
                        p_invalidUserList.add(errorVO);
                    } else if (rsAmbC2STxn.next()) {
                        userMigrationVO.setMessage("C2S ambiguous or underprocess transaction is found for the user.");
                        userMigrationVO.setLineNumber(i);
                        errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), "C2S ambiguous or underprocess transaction is found for the user.");
                        p_invalidUserList.add(errorVO);
                    } else {
                        log.debug(methodName, "", "Operator user check Outer ToUserCatCode=" + userMigrationVO.getToUserCatCode());
                        operatorUserPstmt.setString(1, userMigrationVO.getToUserCatCode());
                        rsOperatorUser = operatorUserPstmt.executeQuery();
                        if (rsOperatorUser.next()) {
                            log.debug(methodName, "", "Operator user check Inner ToParentMsisdn=" + userMigrationVO.getToParentMsisdn());
                            // Now validate the to parent user
                            pstmtSelectParent.setString(1, userMigrationVO.getToParentMsisdn());
                            rsSelectParent = pstmtSelectParent.executeQuery();
                            if (rsSelectParent.next()) {
                                // If To user category sequence is "1", then
                                // after migration, user's parent will be ROOT
                                if ("1".equals(userMigrationVO.getToUserCatCodeSeqNo())) {
                                    userMigrationVO.setToParentID("ROOT");
                                } else {
                                    userMigrationVO.setToParentID(rsSelectParent.getString("user_id"));
                                }
                                userMigrationVO.setToOwnerID(rsSelectParent.getString("owner_id"));
                                userMigrationVO.setToUserParentName(rsSelectParent.getString("USER_NAME"));

                                log.debug(methodName, "", "Check Domain ToUserCatCode=" + userMigrationVO.getToUserCatCode());

                                psmtCheckDomain.setString(1, userMigrationVO.getToUserCatCode());
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
                                if (null!= toUserDomain && toUserDomain.equalsIgnoreCase(toParentDomain)) {
                                    validUserList.add(userMigrationVO);
                                } else {
                                    userMigrationVO.setLineNumber(i);
                                    userMigrationVO.setMessage("User can't be migrated as To parent Domain does not match To user Domain.");
                                    userMigrationVO.setParentExist(false);
                                    errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(),
                                                    "User can't be migrated as To parent Domain does not match To user Domain.");
                                    p_invalidUserList.add(errorVO);
                                }
                            } else {
                                userMigrationVO.setLineNumber(i);
                                userMigrationVO.setMessage("Parent user does not found in the system.");
                                userMigrationVO.setParentExist(false);
                                errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), "Parent user does not found in the system.");
                                p_invalidUserList.add(errorVO);
                            }
                        } else {
                            userMigrationVO.setLineNumber(i);
                            userMigrationVO.setMessage("User catn't be migrated as an Operator user.");
                            userMigrationVO.setParentExist(false);
                            errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), "User catn't be migrated as an Operator user.");
                            p_invalidUserList.add(errorVO);
                        }
                    }
                } else {
                    userMigrationVO.setLineNumber(i);
                    userMigrationVO.setMessage("User not found.");
                    errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), "User not found.");
                    p_invalidUserList.add(errorVO);
                }

                pendingTxnPstmt.clearParameters();
                ambC2STxnPstmt.clearParameters();
                pstmt.clearParameters();
                pstmtSelectParent.clearParameters();
                psmtCheckDomain.clearParameters();
            }
        } catch (SQLException sqe) {
            errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), sqe.getMessage());
            p_invalidUserList.add(errorVO);
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), ex.getMessage());
            p_invalidUserList.add(errorVO);
            log.errorTrace(methodName, ex);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsPendingTxn != null) {
                    rsPendingTxn.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pendingTxnPstmt != null) {
                    pendingTxnPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsAmbC2STxn != null) {
                    rsAmbC2STxn.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (ambC2STxnPstmt != null) {
                    ambC2STxnPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectParent != null) {
                    pstmtSelectParent.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectParent != null) {
                    rsSelectParent.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (operatorUserPstmt != null) {
                    operatorUserPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsOperatorUser != null) {
                    rsOperatorUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsCheckDomain != null) {
                	rsCheckDomain.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtCheckDomain != null) {
                	psmtCheckDomain.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug(methodName, "", "Exiting with validUserList=" + validUserList.size() + " p_invalidUserList.size=" + p_invalidUserList.size());
        }
        return validUserList;
    }

    /**
     * Method for loading the category in to a hash map.
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */
    public HashMap<String, UserCategoryVO> loadCategoryMap(Connection p_con, HashMap<String, HashMap<String, UserMessageVO>> p_profileGradeMap, String p_networkid) throws BTSLBaseException {
        final String methodName = "loadCategoryMap";
        log.debug(methodName, "", entered);
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
        UserCategoryVO userCatVO = null;
        UserMessageVO userMsgVO = null;
        HashMap<String, UserCategoryVO> catCodeMap = null;
        HashMap<String, UserMessageVO> commProfMap = null;
        HashMap<String, UserMessageVO> trfProfMap = null;
        HashMap<String, UserMessageVO> usrGrdMap = null;
        try {
            strBuff = new StringBuilder();
            strBuff.append(" SELECT category_code, category_name, domain_code, sequence_no, grph_domain_type,");
            strBuff.append(" user_id_prefix, low_bal_alert_allow");
            strBuff.append(" FROM CATEGORIES ");
            strBuff.append(" WHERE status<>'N' ORDER BY category_code");

            final String sqlSelect = strBuff.toString();
            log.debug(methodName, "", query + sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);

            final StringBuilder comprosetBuff = new StringBuilder();
            comprosetBuff.append(" SELECT category_code, comm_profile_set_id, comm_profile_set_name");
            comprosetBuff.append(" FROM COMMISSION_PROFILE_SET");
            comprosetBuff.append(" WHERE network_code = ? AND status<>? AND is_default=?"); // Network
            // code
            // from
            // property
            // file

            final String sqlCommPro = comprosetBuff.toString();
            log.debug("loadCategoryList", "", "QUERY sqlCommPro=" + sqlCommPro);
            commProfSetPstmt = p_con.prepareStatement(sqlCommPro);

            final StringBuilder trfProfBuff = new StringBuilder();
            trfProfBuff.append(" SELECT category_code, profile_id, profile_name");
            trfProfBuff.append(" FROM TRANSFER_PROFILE");
            trfProfBuff.append(" WHERE network_code = ? AND status<>? "); // Network
            // code
            // from
            // property
            // file
            trfProfBuff.append(" and parent_profile_id=? and is_default=?");
            trfProfBuff.append(" ORDER BY category_code");

            final String trfPro = trfProfBuff.toString();
            log.debug(methodName, "", "QUERY trfPro=" + trfPro);
            trfProfPstmt = p_con.prepareStatement(trfPro);

            final StringBuilder usrGrdBuff = new StringBuilder();
            usrGrdBuff.append(" SELECT category_code, grade_code, grade_name");
            usrGrdBuff.append(" FROM CHANNEL_GRADES ");
            usrGrdBuff.append(" WHERE status<>? and is_default_grade=?");

            final String userGrade = usrGrdBuff.toString();
            log.debug(methodName, "", "QUERY userGrade=" + userGrade);
            usrGrdPstmt = p_con.prepareStatement(userGrade);

            // Load the category details
            rs = pstmt.executeQuery();
            catCodeMap = new HashMap<>();
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

            // Load the default commission profile details.
            commProfSetPstmt.setString(1, p_networkid);
            commProfSetPstmt.setString(2, PretupsI.NO);
            commProfSetPstmt.setString(3, PretupsI.YES);

            rsCommProfSet = commProfSetPstmt.executeQuery();
            commProfMap = new HashMap<>();
            while (rsCommProfSet.next()) {
                catCode = rsCommProfSet.getString("category_code");
                userMsgVO = new UserMessageVO(rsCommProfSet.getString("comm_profile_set_id"), rsCommProfSet.getString("comm_profile_set_name"));
                commProfMap.put(catCode, userMsgVO);
            }
            p_profileGradeMap.put("COMM_PROFILE", commProfMap);
            // Load transfer profile details
            trfProfPstmt.setString(1, p_networkid);
            trfProfPstmt.setString(2, PretupsI.NO);
            trfProfPstmt.setString(3, PretupsI.PARENT_PROFILE_ID_USER);
            trfProfPstmt.setString(4, PretupsI.YES);

            rsTrfProf = trfProfPstmt.executeQuery();
            trfProfMap = new HashMap<>();
            catCode = null;
            while (rsTrfProf.next()) {
                catCode = rsTrfProf.getString("category_code");
                userMsgVO = new UserMessageVO(rsTrfProf.getString("profile_id"), rsTrfProf.getString("profile_name"));
                trfProfMap.put(catCode, userMsgVO);
            }
            p_profileGradeMap.put("TRF_PROFILE", trfProfMap);

            // Load user grades details
            usrGrdPstmt.setString(1, PretupsI.NO);
            usrGrdPstmt.setString(2, PretupsI.YES);

            rsUsrGrd = usrGrdPstmt.executeQuery();
            usrGrdMap = new HashMap<>();
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
            log.debug(methodName, "", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.debug(methodName, "", exception + ex);
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "loadCategoryList", errorGeneralProcessing);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (commProfSetPstmt != null) {
                    commProfSetPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (trfProfPstmt != null) {
                    trfProfPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (usrGrdPstmt != null) {
                    usrGrdPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsCommProfSet != null) {
                    rsCommProfSet.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsTrfProf != null) {
                    rsTrfProf.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsUsrGrd != null) {
                    rsUsrGrd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug("loadCategoryList", "", "Exiting with catCodeMap.size=" + catCodeMap.size());
        }
    }

    public void markNpUsers(Connection p_con, String p_userID, String p_msisdn) throws BTSLBaseException {
        final String methodName = "markNpUsers";
        log.debug(methodName, "", entered);
        PreparedStatement pstmt = null;
        try {
            // For Marking status NP of users in migration list

            final String sqlSelect = userMigrationQry.markNpUsersQry();
            log.debug(methodName, query, sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userID);
            pstmt.executeUpdate();
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, "markNpUsers", errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.debug("markNpUsers", "", exception + ex);
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "markNpUsers", errorGeneralProcessing);
        } finally {
           
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug("markNpUsers", "", "Exiting from markNpUsers");
        }
    }

    /**
     * Method to mark NP users Ends here
     * 
     * @param p_con
     * @param p_userID
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     *             This method selects users, of not parent status(NP).
     */
    public ArrayList<String> getNpUsers(Connection p_con, String p_userID, String p_msisdn) throws BTSLBaseException {
        final String methodName = "getNpUsers";
        log.debug(methodName, "", entered);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuffgetNpUsers = new StringBuilder();
        ArrayList<String> npUsers = new ArrayList<>();
        try {
            // Get All NP users
            strBuffgetNpUsers.append("SELECT user_id,msisdn ");
            strBuffgetNpUsers.append("FROM USERS WHERE status='NP'");
            strBuffgetNpUsers.append("AND user_type='CHANNEL'");
            final String getNPusers = strBuffgetNpUsers.toString();
            pstmt = p_con.prepareStatement(getNPusers);
            rs = pstmt.executeQuery();
            npUsers = new ArrayList<>();
            while (rs.next()) {
                npUsers.add(rs.getString("user_id") + rs.getString("msisdn"));
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.debug(methodName, "", exception + ex);
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug(methodName, "", "Exiting with getNpUsers.size=" + npUsers.size());
        }
        return npUsers;
    }

    
    public HashMap getNpUsersRevamp(Connection p_con, String p_userID, String p_msisdn,String domain) throws BTSLBaseException {
        final String methodName = "getNpUsersRevamp";
        log.debug(methodName, "", entered);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserVO userVO = null; 
        final StringBuilder strBuffgetNpUsers = new StringBuilder();
       HashMap hashmap = new HashMap<>();
        try {
            // Get All NP users
            strBuffgetNpUsers.append("SELECT u.MSISDN ,u.USER_ID, u.USER_NAME ,u.NETWORK_CODE, d.DOMAIN_NAME , c.CATEGORY_NAME , ug.GRPH_DOMAIN_CODE , up.USER_NAME parent_name, up.MSISDN parent_msisdn, up.CATEGORY_CODE parent_category, ugp.GRPH_DOMAIN_CODE parent_geography, uo.MSISDN owner_msisdn, uo.CATEGORY_CODE owner_category, ugo.GRPH_DOMAIN_CODE owner_geography ");
            strBuffgetNpUsers.append("FROM USERS u, DOMAINS d , GEOGRAPHICAL_DOMAIN_TYPES gdt , CATEGORIES c , DOMAIN_TYPES dt , USER_GEOGRAPHIES ug , USERS up , USER_GEOGRAPHIES ugp, USERS uo, USER_GEOGRAPHIES ugo ");
            strBuffgetNpUsers.append("WHERE u.STATUS ='NP' AND d.DOMAIN_CODE = ? AND gdt.GRPH_DOMAIN_TYPE = c.GRPH_DOMAIN_TYPE AND c.CATEGORY_CODE = u.CATEGORY_CODE AND d.DOMAIN_CODE = c.DOMAIN_CODE AND dt.DOMAIN_TYPE_CODE = d.DOMAIN_TYPE_CODE AND ug.USER_ID = u.USER_ID AND up.USER_ID = u.PARENT_ID AND ugp.USER_ID = up.USER_ID AND uo.USER_ID = u.OWNER_ID AND ugo.USER_ID = uo.USER_ID");
            final String getNPusers = strBuffgetNpUsers.toString();
            pstmt = p_con.prepareStatement(getNPusers);
            pstmt.setString(1, domain);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	userVO = new UserVO();
            	userVO.setMsisdn(rs.getString("msisdn"));
            	userVO.setUserID(rs.getString("user_id"));
            	userVO.setUserName(rs.getString("user_name"));
            	userVO.setNetworkID(rs.getString("network_code"));
            	userVO.setDomainName(rs.getString("domain_name"));
            	userVO.setCategoryCode(rs.getString("category_name"));
            	userVO.setGeographicalCode(rs.getString("grph_domain_code"));
            	userVO.setParentName(rs.getString("parent_name"));
            	userVO.setParentMsisdn(rs.getString("parent_msisdn"));
            	userVO.setParentCategoryName(rs.getString("parent_category"));
            	GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO(); 
            	ArrayList geographyList = _geographyDAO.loadUserGeographyList(p_con, userVO.getUserID(), userVO.getNetworkID());
                userVO.setGeographicalAreaList(geographyList); 
                userVO.getGeographicalAreaList().get(0).setParentGraphDomainCode(rs.getString("parent_geography"));
                userVO.getGeographicalAreaList().get(0).setGraphDomainName(rs.getString("owner_geography")); // owner geo set in grp_domain_name as no field present for owner geo
                userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                userVO.setOwnerCategoryName(rs.getString("owner_category"));
                
                hashmap.put(rs.getString("msisdn"), userVO);//.add(rs.getString("user_id") + ","+rs.getString("msisdn"));
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.debug(methodName, "", exception + ex);
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug(methodName, "", "Exiting with getNpUsers.size=" + hashmap.size());
        }
        return hashmap;
    }

    /**
     * Method for loading the category in to a hash map.
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */
    public HashMap<String, UserGeoDomainVO> loadGeoDomainCode(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadGeoDomainCode";
        log.debug(methodName, "", entered);
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

            final String sqlSelect = strBuff.toString();
            log.debug(methodName, query, sqlSelect);
            geoDomCodeMap = new HashMap<>();
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
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.debug(methodName, "", exception + ex);
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "loadCategoryList", errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug(methodName, "", "Exiting with geoDomCodeMap.size=" + (geoDomCodeMap!=null? geoDomCodeMap.size():0 ));
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
    public ArrayList<ListValueVO> userMigrationProcess(Connection p_con, ArrayList<UserMigrationVO> p_finalUserMigrList, HashMap<String, UserCategoryVO> p_catCodeMap, HashMap<String, UserGeoDomainVO> p_userGeoDomCodeMap, HashMap<String, HashMap<String, UserMessageVO>> p_profileGradeMap, HashMap<String, String> p_migrationDetailMap) throws BTSLBaseException {
        final String methodName = "userMigrationProcess";
        log.debug(methodName, "", "Entered p_finalUserMigrList.size=" + p_finalUserMigrList.size());
        int addCount = 0;
        int sucMigUser = 0;
        int seqNo = 1;
        PreparedStatement pstmtCheckGeoType = null;
        ResultSet rsCheckGeoType = null;
        PreparedStatement pstmtSelectGeo = null;
        PreparedStatement pstmtSelectGeoWithCheck = null;
        ResultSet rsSelectGeo = null;
        PreparedStatement pstmtUpdateUser = null; // prepared statement for
        // updating the current user.
        PreparedStatement pstmtInsertUser = null; // prepared statement for
        // inserting the new user.
        PreparedStatement pstmtSelectUserBalance = null;
        ResultSet rsSelectUserBalance = null;
        PreparedStatement pstmtUpdateUserBalance = null;
        PreparedStatement pstmtInsertUserBalance = null;
        PreparedStatement pstmtInsertUserGeographies = null;
        PreparedStatement pstmtInsertUserPhones = null;
        PreparedStatement pstmtInsertUserProductTypes = null;
        PreparedStatement pstmtSelectUserRoles = null;
        ResultSet rsSelectUserRoles = null;
        PreparedStatement pstmtInsertUserRoles = null;
        PreparedStatement pstmtInsertUserServices = null;
        PreparedStatement pstmtSelectChannelUserInfo = null;
         ResultSet rsSelectChannelUserInfo = null;
        PreparedStatement pstmtInsertChannelUserInfo = null;
        PreparedStatement pstmtChannelTrnf = null;
        PreparedStatement pstmtupdateTransferRules = null;
        PreparedStatement pstmtChnlTrnfItems = null;
        final ArrayList<ListValueVO> userMsgErrorList = new ArrayList<>();
        ChannelUserVO chnluservo = null;
        ListValueVO errorVO = null;

        PreparedStatement psmtselectPrntId = null;
        PreparedStatement psmtupdateUserStatus = null;
        ResultSet rsselectprntId = null;
        long selectUserBalance = 0L;
        String selectUserProductCode = null;
        String isMultipleWallet = null;
        String isActiveUserIdApplied = null;
        long unitValue = 0l;
        String sqlSelectParent = null;
        PreparedStatement pstmtSelectParent = null;
        ResultSet rsSelectParent = null;
        StringBuilder strBuffSelectParent = null;
        String parentCat = null;
        String parentGeo = null;
        PreparedStatement psmtSelectbal = null;
        ResultSet rsSelectbal = null;
        final ArrayList<ListValueVO> p_errorUserList = new ArrayList<>();
        try {
            final String selectBalanceCount = "select count(*) from user_balances where user_id=?";
            psmtSelectbal = p_con.prepareStatement(selectBalanceCount);
            log.debug(methodName, "", "selectBalanceCount = " + selectBalanceCount);
            final StringBuilder selectParentId = new StringBuilder("SELECT parent_id,user_id,owner_id FROM USERS WHERE msisdn=? and status='Y' and user_type='CHANNEL'");
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
            log.debug(methodName, "", query + sqlSelectParent);
            pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);

            // Query to verify the new category and geo_domain code, if same
            // then migrate the user else not
            // Parameter: toCatCode, toGeoDomCode
            final StringBuilder checkGeoTypeBffr = new StringBuilder(" select 1");
            checkGeoTypeBffr.append("  from CATEGORIES cat, GEOGRAPHICAL_DOMAINS gd");
            checkGeoTypeBffr.append(" where cat.GRPH_DOMAIN_TYPE= gd.GRPH_DOMAIN_TYPE");
            checkGeoTypeBffr.append(" and category_code=? and gd.grph_domain_code=?");
            log.debug(methodName, "", "checkGeoTypeBffr = " + checkGeoTypeBffr.toString());
            pstmtCheckGeoType = p_con.prepareStatement(checkGeoTypeBffr.toString());

            // Query to check whether the user geography exist or not.
           String selectBffr = userMigrationQry.userMigrationProcessQry();
            
            log.debug(methodName, "", "selectBffr = " + selectBffr);
            pstmtSelectGeo = p_con.prepareStatement(selectBffr);

            String selectBffrCheck = userMigrationQry.userMigrationProcessQry2();
            log.debug(methodName, "", "selectBffrCheck = " + selectBffrCheck);
            pstmtSelectGeoWithCheck = p_con.prepareStatement(selectBffrCheck);

            // Insert the new user details
            final StringBuilder insertUserBuff = new StringBuilder("INSERT INTO users(user_id, user_name, network_code,");
            insertUserBuff.append(" login_id, password, category_code, parent_id, owner_id, allowed_ip, allowed_days,");
            insertUserBuff.append(" from_time, to_time, last_login_on, employee_code, status, email, pswd_modified_on,");
            insertUserBuff.append(" contact_person, contact_no, designation, division, department, msisdn, user_type,");
            insertUserBuff.append(" created_by, created_on, modified_by, modified_on,");
            insertUserBuff.append(" address1, address2, city, state, country, ssn, user_name_prefix, external_code,");
            insertUserBuff.append(" user_code, short_name,firstname,lastname, reference_id, invalid_password_count, level1_approved_by,");
            insertUserBuff.append(" level1_approved_on, level2_approved_by, level2_approved_on, appointment_date,");
            insertUserBuff.append(" password_count_updated_on,previous_status,PSWD_RESET)");
            insertUserBuff.append(" (SELECT ?, user_name, network_code,?, PASSWORD,");
            insertUserBuff.append(" ?, ?, ?, allowed_ip, allowed_days,");
            insertUserBuff.append(" from_time, to_time, last_login_on, employee_code, status, email, pswd_modified_on,");
            insertUserBuff.append(" contact_person, contact_no, designation, division, department, msisdn, user_type,");
            insertUserBuff.append(" created_by, created_on, 'SYSTEM', ?,");
            insertUserBuff.append(" address1, address2, city, state, country, ssn, user_name_prefix, external_code,");
            insertUserBuff.append(" msisdn, short_name,firstname,lastname,?, invalid_password_count, level1_approved_by,");
            insertUserBuff.append(" level1_approved_on, level2_approved_by, level2_approved_on, appointment_date,");
            insertUserBuff.append(" password_count_updated_on, status, PSWD_RESET");
            insertUserBuff.append(" FROM USERS WHERE user_id=? AND status IN ('Y','S') )");
            pstmtInsertUser =p_con.prepareStatement(insertUserBuff.toString());
            log.debug(methodName, "", "insertUser query=" + insertUserBuff);

            // Soft delete the old user
            final StringBuilder updateUserBuff = new StringBuilder("UPDATE users SET user_code=?,reference_id=?,status=?, ");
            updateUserBuff.append("modified_by='SYSTEM', modified_on=?,login_id=?, to_moved_user_id=? WHERE user_id=? and user_type='CHANNEL'");
            pstmtUpdateUser = p_con.prepareStatement(updateUserBuff.toString());
            log.debug(methodName, "", "updateUser query=" + updateUserBuff);

            final String updateStatus = "UPDATE USERS SET status=? WHERE user_id=?";
            psmtupdateUserStatus = p_con.prepareStatement(updateStatus);

            // Query for updating the user balance
            final StringBuilder selectUserBalanceBuff = new StringBuilder("SELECT network_code, network_code_for, ");
            selectUserBalanceBuff.append("U.product_code, balance, prev_balance, last_transfer_type, last_transfer_no,");
            selectUserBalanceBuff.append("last_transfer_on,P.unit_value,daily_balance_updated_on ");
            selectUserBalanceBuff.append("FROM user_balances U,products P ");
            selectUserBalanceBuff.append("WHERE U.user_id=? AND U.product_code=P.product_code");
            pstmtSelectUserBalance = p_con.prepareStatement(selectUserBalanceBuff.toString());
            log.debug(methodName, "", "selectUserBalance query=" + selectUserBalanceBuff);

            // Update the user balance for the old user
            final StringBuilder updateUserBalanceBuff = new StringBuilder("UPDATE user_balances SET balance=?, prev_balance=? ");
            updateUserBalanceBuff.append("WHERE user_id=? AND product_code=? ");
            pstmtUpdateUserBalance = p_con.prepareStatement(updateUserBalanceBuff.toString());
            log.debug(methodName, "", "updateUserBalance query=" + updateUserBalanceBuff);

            // Insert in to user balance for new user
            final StringBuilder insertUserBalanceBuff = new StringBuilder("INSERT INTO user_balances(user_id, network_code, ");
            insertUserBalanceBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type,");
            insertUserBalanceBuff.append("last_transfer_no, last_transfer_on) VALUES(?,?,?,?,?,?,?,?,?)");
            pstmtInsertUserBalance = p_con.prepareStatement(insertUserBalanceBuff.toString());
            log.debug(methodName, "", "insertUserBalance query=" + insertUserBalanceBuff);

            // Insertion in user_domains table
            final StringBuilder insertUserGeographiesBuff = new StringBuilder("INSERT INTO user_geographies(user_id,");
            insertUserGeographiesBuff.append(" grph_domain_code)");
            insertUserGeographiesBuff.append(" values(?,?)");
            pstmtInsertUserGeographies = p_con.prepareStatement(insertUserGeographiesBuff.toString());
            log.debug(methodName, "", "insertUserGeographies query=" + insertUserGeographiesBuff);

            // Insert in to user phones
            final StringBuilder insertUserPhonesBuff = new StringBuilder(" INSERT INTO user_phones(user_id, user_phones_id,");
            insertUserPhonesBuff.append(" msisdn, description, primary_number, sms_pin, pin_required, phone_profile,");
            insertUserPhonesBuff.append(" phone_language, country, invalid_pin_count, last_transaction_status,");
            insertUserPhonesBuff.append(" last_transaction_on, pin_modified_on, created_by, created_on, modified_by,");
            insertUserPhonesBuff.append(" modified_on, last_transfer_id, last_transfer_type, prefix_id,");
            insertUserPhonesBuff.append(" temp_transfer_id, first_invalid_pin_time,");
            insertUserPhonesBuff.append(" access_type, from_time, to_time, allowed_days, allowed_ip, last_login_on,PIN_RESET)");
            insertUserPhonesBuff.append(" (SELECT ?,?, msisdn, description, primary_number, sms_pin,");
            insertUserPhonesBuff.append(" pin_required, ?, phone_language, country, invalid_pin_count,");
            insertUserPhonesBuff.append(" last_transaction_status, last_transaction_on, pin_modified_on, created_by,");
            insertUserPhonesBuff.append(" created_on, modified_by, modified_on, last_transfer_id, last_transfer_type,");
            insertUserPhonesBuff.append(" prefix_id, temp_transfer_id,first_invalid_pin_time, access_type, from_time, to_time, allowed_days,");
            insertUserPhonesBuff.append(" allowed_ip, last_login_on,PIN_RESET ");
            insertUserPhonesBuff.append(" FROM user_phones WHERE user_id=?)");
            pstmtInsertUserPhones = p_con.prepareStatement(insertUserPhonesBuff.toString());
            log.debug(methodName, "", "insertUserPhones query=" + insertUserPhonesBuff);

            // Insert into user_product_types table for new user
            final StringBuilder insertUserProductTypesBuff = new StringBuilder();
            insertUserProductTypesBuff.append(" INSERT INTO user_product_types(user_id,product_type) ");
            insertUserProductTypesBuff.append(" (SELECT ?, product_type FROM user_product_types");
            insertUserProductTypesBuff.append(" WHERE user_id=? )");
            pstmtInsertUserProductTypes = p_con.prepareStatement(insertUserProductTypesBuff.toString());
            log.debug(methodName, "", "insertUserProductTypes query=" + insertUserProductTypesBuff);

            // Insert in to user_roles for new user
            // new_user_id, new_user_category_code,WEB
            final StringBuffer selectUserRolesBuff = new StringBuffer("SELECT role_code, gateway_types FROM user_roles WHERE user_id = ?");
            pstmtSelectUserRoles = p_con.prepareStatement(selectUserRolesBuff.toString());
            if (log.isDebugEnabled()) {
                log.debug(methodName, "selectUserRoles query=" + selectUserRolesBuff);
            }
            
            final StringBuilder insertUserRolesBuff = new StringBuilder();
            insertUserRolesBuff.append(" INSERT INTO user_roles(user_id ,role_code,gateway_types)");
            insertUserRolesBuff.append(" Values (?,?,?)");
            pstmtInsertUserRoles = p_con.prepareStatement(insertUserRolesBuff.toString());
            log.debug(methodName, "", "insertUserRoles query=" + insertUserRolesBuff);

            // Insert in to user services for new user
            // new_user_id, old_user_id, new_category_code
            final StringBuilder insertUserServicesBuff = new StringBuilder("");
            insertUserServicesBuff.append(" INSERT INTO user_services(user_id,service_type, status)");
            insertUserServicesBuff.append(" (SELECT ?, US.service_type,US.status");
            insertUserServicesBuff.append(" FROM user_services US,users U,category_service_type CST");
            insertUserServicesBuff.append(" WHERE US.user_id=? AND U.user_id=US.user_id");
            insertUserServicesBuff.append(" AND U.category_code=CST.category_code");
            insertUserServicesBuff.append(" AND CST.service_type=US.service_type and CST.network_code=U.network_code");
            insertUserServicesBuff.append(" AND U.category_code=?)"); // New
            // user's
            // category
            // code
            pstmtInsertUserServices = p_con.prepareStatement(insertUserServicesBuff.toString());
            log.debug(methodName, "", "insertUserServices query=" + insertUserServicesBuff);

            // Insert in to channel users for new user
            final StringBuilder insertChannelUserInfoBuff = new StringBuilder(" INSERT INTO channel_users(USER_ID,USER_GRADE,");
            insertChannelUserInfoBuff.append(" TRANSFER_PROFILE_ID,COMM_PROFILE_SET_ID,IN_SUSPEND, OUT_SUSPEND,");
            insertChannelUserInfoBuff.append(" ACTIVATED_ON,APPLICATION_ID, MPAY_PROFILE_ID,USER_PROFILE_ID,");
            insertChannelUserInfoBuff.append(" IS_PRIMARY,MCOMMERCE_SERVICE_ALLOW,LOW_BAL_ALERT_ALLOW,outlet_code, suboutlet_code)");
            insertChannelUserInfoBuff.append(" (select ?, ?, ?, ?, IN_SUSPEND, OUT_SUSPEND,ACTIVATED_ON, ?, ?, ?, ?, ?, LOW_BAL_ALERT_ALLOW, outlet_code, suboutlet_code");
            insertChannelUserInfoBuff.append(" from channel_users where USER_ID=? )");
            pstmtInsertChannelUserInfo =  p_con.prepareStatement(insertChannelUserInfoBuff.toString());
            log.debug(methodName, "", "insertChannelUserInfoBuff query=" + insertChannelUserInfoBuff);

            // Insert into channel_transfers for new user
            final StringBuilder chnlTrnfBuff = new StringBuilder(" INSERT INTO  channel_transfers ( transfer_id, network_code, network_code_for, grph_domain_code, ");
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
			chnlTrnfBuff.append(" first_approved_by, first_approved_on, second_approved_by, second_approved_on, third_approved_by, third_approved_on,sms_default_lang,sms_second_lang" );

			StringBuilder selectChannelUserInfoBuff =new StringBuilder("SELECT user_grade, contact_person, ");
			selectChannelUserInfoBuff.append("transfer_profile_id, comm_profile_set_id, in_suspend, out_suspend, ");
			selectChannelUserInfoBuff.append("outlet_code, suboutlet_code ");
			selectChannelUserInfoBuff.append(" ,activated_on,application_id, mpay_profile_id, user_profile_id, is_primary, mcommerce_service_allow, low_bal_alert_allow ");
			selectChannelUserInfoBuff.append("FROM channel_users ");
			selectChannelUserInfoBuff.append("WHERE user_id=? ");
			pstmtSelectChannelUserInfo= p_con.prepareStatement(selectChannelUserInfoBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectChannelUserInfoBuff query="+selectChannelUserInfoBuff);
			}
			StringBuilder updatePromotionalTransferRulesBuff =new StringBuilder("UPDATE transfer_rules SET SENDER_SUBSCRIBER_TYPE=? ");
	    	updatePromotionalTransferRulesBuff.append("WHERE SENDER_SUBSCRIBER_TYPE=? and RULE_TYPE=?");
	    	pstmtupdateTransferRules = p_con.prepareStatement(updatePromotionalTransferRulesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"updatePromotionalTransferRules query="+updatePromotionalTransferRulesBuff);
			}
			
			isMultipleWallet=Constants.getProperty("IS_MULTIPLE_WALLET");
			isActiveUserIdApplied=Constants.getProperty("IS_ACTIVE_USERID_APPLIED");
			//isMultipleWallet applicable.
            if (PretupsI.YES.equalsIgnoreCase(isMultipleWallet)) {
                // both isMultipleWallet and isActiveUserIdApplied applied.
                if (PretupsI.YES.equalsIgnoreCase(isActiveUserIdApplied)) {
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
            final String chnlTrfQuery = chnlTrnfBuff.toString();
            log.debug(methodName, "", "chnlTrfQuery insert query:" + chnlTrfQuery);
            pstmtChannelTrnf =  p_con.prepareStatement(chnlTrfQuery);

            // Query for CHANNEL_TRANSFER_ITEMS
            final StringBuilder chnlTrfItemsBuff = new StringBuilder(" INSERT INTO channel_transfers_items ( ");
            chnlTrfItemsBuff.append(" s_no,transfer_id,product_code,required_quantity,approved_quantity,user_unit_price, ");
            chnlTrfItemsBuff.append(" commission_profile_detail_id,commission_type, commission_rate, commission_value, ");
            chnlTrfItemsBuff.append(" tax1_type, tax1_rate, tax1_value, tax2_type,tax2_rate, tax2_value , tax3_type, ");
            chnlTrfItemsBuff.append(" tax3_rate, tax3_value, payable_amount, net_payable_amount,mrp,");
            chnlTrfItemsBuff.append(" sender_previous_stock, receiver_previous_stock,transfer_date )");// ,sender_post_stock,
            // receiver_post_stock)
            chnlTrfItemsBuff.append(" VALUES  ");
            chnlTrfItemsBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )"); // ,?,?)
            final String chnlTrfItemsBuffQuery = chnlTrfItemsBuff.toString();
            log.debug(methodName, "", "chnlTrfItemsBuffQuery Insert query:" + chnlTrfItemsBuffQuery);
            pstmtChnlTrnfItems = p_con.prepareStatement(chnlTrfItemsBuffQuery);

           if (p_finalUserMigrList != null && !p_finalUserMigrList.isEmpty()) {
            		 
                String newUserID;
                String userIDPrifix;
                String p_networkid;
                UserMigrationVO usrMigrVO = null;
                int lineNo = 0;
                int finalMigrListSize = p_finalUserMigrList.size();
                for (int i = 0; i < finalMigrListSize; i++) {
                    try {
                        try {
                            usrMigrVO =  p_finalUserMigrList.get(i);
                            ArrayList<UserMigrationVO> currentUserMigList= new ArrayList<>();
							currentUserMigList.add(usrMigrVO);					
							currentUserMigList = this.validateFromUsers(p_con,currentUserMigList,userMsgErrorList);//SuccessUserList Contains Users list after validation .
								if(currentUserMigList.isEmpty())
							{
									continue;
							}
                            lineNo = usrMigrVO.getLineNumber();
                            p_networkid = usrMigrVO.getNetworkID();
                            userIDPrifix = p_catCodeMap.get(usrMigrVO.getToUserCatCode()).getUserIdPrefix();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                            log.debug(lineNo, usrMigrVO.getToUserCatCode(), " is not defined in the system.");
                            errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Category code is not defined in the system.");
                            p_errorUserList.add(errorVO);
                            throw new BTSLBaseException(this, methodName, " Category code is not currect.");
                        }
                        try {
                            // Now validate the to parent user
                            log.debug(methodName, "From Parent Msisdn= " + usrMigrVO.getFromParentMsisdn(), "From User Msisdn=" + usrMigrVO.getFromUserMsisdn());
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
                                	if(usrMigrVO.getFromUserMsisdn().equals(usrMigrVO.getToParentMsisdn()))
                                	{
                                		parentCat = usrMigrVO.getToParentCatCode();
                                		parentGeo = usrMigrVO.getToParentGeoCode();
                                	}
                                	else
                                	{
                                		parentCat = rsSelectParent.getString("category_code");
                                		parentGeo = rsSelectParent.getString("GRPH_DOMAIN_CODE");
                                	}
                                    if (!(parentCat.equals(usrMigrVO.getToParentCatCode()) && parentGeo.equals(usrMigrVO.getToParentGeoCode()))) {
                                        log.debug(methodName, " Parent info is in-currect.", " count= " + addCount);
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Parent info is in-correct.");// PRS
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Parent info is in-correct.");
                                    }
                                }
                            }
                        } catch (SQLException sqe) {
                            log.errorTrace(methodName, sqe);
                            errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Parent info is in-correct.");
                            userMsgErrorList.add(errorVO);
                            throw new BTSLBaseException(this, methodName, "Parent info is in-correct.");
                        }
                        try {
                            pstmtCheckGeoType.setString(1, usrMigrVO.getToUserCatCode());
                            pstmtCheckGeoType.setString(2, usrMigrVO.getToUserGeoCode());
                            rsCheckGeoType = pstmtCheckGeoType.executeQuery();
                            if (rsCheckGeoType.next()) {
                                log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "MIGRATION STARTS FROM HERE");
                                // Generate the new user id
                                newUserID = this.generateUserId(p_con, p_networkid, userIDPrifix);

                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on USERS table");
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
                                    pstmtUpdateUser.setString(6, newUserID);
                                    // new
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
                                        log.debug(methodName, "Old user update failed.", " count= " + addCount);
                                        errorVO = new ListValueVO(String.valueOf(lineNo), " Old user update failed.", "lineNo");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Old user update failed.");
                                    } else {
        
                                        log.debug(methodName, "Old user updated successfully.", " count= " + addCount);
                                    }

                                    pstmtUpdateUser.clearParameters();
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), " Update for old record is failed in USERS table.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, " Update for old record is failed in USERS table.");
                                }
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on USERS table for Inserting  the new user.");
                                    psmtselectPrntId.setString(1, usrMigrVO.getToParentMsisdn());
                                    rsselectprntId = psmtselectPrntId.executeQuery();
                                    String userParentId = null;
                                    String ownerId = null;
                                    psmtselectPrntId.clearParameters();
                                    if (rsselectprntId.next()) {
                                    	if(usrMigrVO.getFromUserMsisdn().equals(usrMigrVO.getToParentMsisdn()))
                                    	{
                                    		userParentId = "ROOT";
                                    		ownerId = newUserID;
                                    	}
                                    	else
                                    	{
                                    		userParentId = rsselectprntId.getString("USER_ID");
                                    		ownerId = rsselectprntId.getString("OWNER_ID");
                                    	}
                                    	log.debug(methodName, "UserParentId" + userParentId);
                                    	log.debug(methodName, "OwnerId" + ownerId);
                                        pstmtInsertUser.setString(1, newUserID);
                                        pstmtInsertUser.setString(2, usrMigrVO.getFromUserLoginID());
//                                        pstmtInsertUser.setString(2, newUserID);
                                         pstmtInsertUser.setString(3, usrMigrVO.getToUserCatCode());
                                            pstmtInsertUser.setString(4, userParentId);
                                            pstmtInsertUser.setString(5, ownerId);

                                        pstmtInsertUser.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                        pstmtInsertUser.setString(7, usrMigrVO.getFromUserID()); // @@reference_id.
                                        pstmtInsertUser.setString(8, usrMigrVO.getFromUserID());
                                        addCount = pstmtInsertUser.executeUpdate();
                                        if (addCount <= 0) {
                                            log.debug(methodName, " User insertion failed.", count + addCount);
                                            errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insertion for new user is Failed in USERS table.");
                                            userMsgErrorList.add(errorVO);
                                            throw new BTSLBaseException(this, methodName, "Insertion for new user is Failed in USERS table.");
                                        } else {
                                            log.debug(methodName, "User inserted successfully.", count + addCount);
                                        }

                                        pstmtInsertUser.clearParameters();
                                    }
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "User insertion failed.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "User insertion failed.");
                                }
                                finally{
                                	if(rsselectprntId!=null)
                                		rsselectprntId.close();
                                }
                                // update the old user status ='N' after
                                // inserting the entry for new user.
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Updating old user status to deleted in USERS table.");
                                    psmtupdateUserStatus.setString(1, PretupsI.USER_STATUS_DELETED);
                                    psmtupdateUserStatus.setString(2, usrMigrVO.getFromUserID());
                                    addCount = psmtupdateUserStatus.executeUpdate();
                                    psmtupdateUserStatus.clearParameters();
                                    if (addCount <= 0) {
                                        log.debug(methodName, "Old user status update failed.", count + addCount);
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Updation of User Status to delete(N) failed.");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Old user status update failed.");
                                    } else {
                                        log.debug(methodName, "Old user status updated successfully.", count + addCount);
                                    }
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Updation of User Status to delete(N) failed ");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "User insertion failed.");
                                }

                                // Update the old user balance and new entry for
                                // the new user id in USER_BALANCES table
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on USER_BALANCES table.");
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
                                            log.debug(methodName, " User balance=", "selectUserBalance =" + selectUserBalance);
                                            if (BTSLUtil.isNullString(String.valueOf(selectUserBalance))) {
                                                selectUserBalance = 0L;
                                            }
                                            pstmtInsertUserBalance.setLong(5, selectUserBalance);
                                            pstmtInsertUserBalance.setLong(6, 0L);
                                            pstmtInsertUserBalance.setString(7, rsSelectUserBalance.getString("last_transfer_type"));
                                            pstmtInsertUserBalance.setString(8, rsSelectUserBalance.getString("last_transfer_no"));
                                            unitValue = rsSelectUserBalance.getLong("unit_value");
                                            pstmtInsertUserBalance.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(rsSelectUserBalance.getDate("last_transfer_on")));
                                            addCount = pstmtInsertUserBalance.executeUpdate();
                                            if (addCount <= 0) {
                                                log.debug(methodName, "Insertion for new user in USER_BALANCES is failed", count + addCount);
                                                errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert for user balance is failed in USERS_BALANCES table ");
                                                userMsgErrorList.add(errorVO);
                                                throw new BTSLBaseException(this, methodName, "Insertion for new user in USER_BALANCES is failed.");
                                            } else {
                                                log.debug(methodName, "Insertion for new user in USER_BALANCES is sucessfull", count + addCount);
                                            }

                                            // Clear the parameter
                                            pstmtInsertUserBalance.clearParameters();
                                            log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Update for old user in USER_BALANCES");

                                            pstmtUpdateUserBalance.setLong(1, 0);
                                            pstmtUpdateUserBalance.setLong(2, selectUserBalance);
                                            pstmtUpdateUserBalance.setString(3, usrMigrVO.getFromUserID());
                                            selectUserProductCode = rsSelectUserBalance.getString("product_code");
                                            if (BTSLUtil.isNullString(selectUserProductCode)) {
                                                selectUserProductCode = "ETOPUP";
                                            }
                                            pstmtUpdateUserBalance.setString(4, selectUserProductCode);
                                            addCount = pstmtUpdateUserBalance.executeUpdate();
                                            if (addCount <= 0) {
                                                log.debug(methodName, "Update for old user in USER_BALANCES is failed.", count + addCount);
                                                errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Update for user balance is failed in USERS_BALANCES table");
                                                userMsgErrorList.add(errorVO);
                                                throw new BTSLBaseException(this, methodName, "Update for old user in USER_BALANCES is failed.");
                                            } else {
                                                log.debug(methodName, "Update for old user in USER_BALANCES is sucessfull.", " count =" + addCount);
                                            }

                                            // Clear the parameter
                                            pstmtUpdateUserBalance.clearParameters();
                                        }
                                    }
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert or Update for user balance is failed in USERS_BALANCES table.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Insert or Update for user balance is failed in USERS_BALANCES table.");
                                }
                                finally
                                {
                                	if(rsSelectbal!=null)
                                		rsSelectbal.close();
                                }
                                // Update in USER_DOMAINS table
                                // parameter: new_user_id, new_domain_code

                                try {
                                    log.debug(lineNo, "Operation on USER_GEOGRAPHIES table.");
                                    psmtselectPrntId.setString(1, usrMigrVO.getToParentMsisdn());
                                    rsselectprntId = psmtselectPrntId.executeQuery();

                                    String userParentId = null;
                                    psmtselectPrntId.clearParameters();
                                    while (rsselectprntId.next()) {
                                        userParentId = rsselectprntId.getString("USER_ID"); //
                                    }
                                    // If user is at top level
                                    if ("1".equals(usrMigrVO.getToUserCatCodeSeqNo()) && p_catCodeMap.get(usrMigrVO.getToUserCatCode()).getGrphDomainType().equals(
                                                    usrMigrVO.getToGeoDomainType())) {
                                        pstmtInsertUserGeographies.setString(1, newUserID);
                                        pstmtInsertUserGeographies.setString(2, usrMigrVO.getToUserGeoCode());
                                    } else {
                                        pstmtSelectGeo.setString(1, userParentId);
                                        pstmtSelectGeo.setString(2, usrMigrVO.getToUserGeoCode());
                                        try{
                                        rsSelectGeo = pstmtSelectGeo.executeQuery();
                                        // Parameter: new_user_id, new_geo_code
                                        if (rsSelectGeo.next()) {
                                            pstmtInsertUserGeographies.setString(1, newUserID);
                                            pstmtInsertUserGeographies.setString(2, usrMigrVO.getToUserGeoCode());
                                        } else if ("Y".equals(Constants.getProperty("ALLOW_MIGR_WITHOUT_GEODOMCODE"))) {
                                            pstmtSelectGeoWithCheck.setString(1, usrMigrVO.getToParentMsisdn());
                                            pstmtSelectGeoWithCheck.setString(2, usrMigrVO.getToParentMsisdn());
                                            try{
	                                            rsSelectGeo = null;
	                                            rsSelectGeo = pstmtSelectGeoWithCheck.executeQuery();
	                                            if (rsSelectGeo.next()) {
	                                                final String grphDomainCode = rsSelectGeo.getString("grph_domain_code");
	                                                pstmtInsertUserGeographies.setString(1, newUserID);
	                                                pstmtInsertUserGeographies.setString(2, grphDomainCode);
	                                            } else {
	                                                errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(),
	                                                                "Insert in USER_GEOGRAPHIES is failed because no geo_domain_code found for the new parent.");
	                                                userMsgErrorList.add(errorVO);
	                                                throw new BTSLBaseException(this, methodName,
	                                                                "Insert in USER_GEOGRAPHIES is failed because no geo_domain_code found for the new parent.");
	                                            }
                                            }
                                            finally{
                                            	if(rsSelectGeo!=null)
                                            		rsSelectGeo.close();
                                            }
                                        } else {
                                            errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Given geo_domain_code is not defined in the system.");
                                            userMsgErrorList.add(errorVO);
                                            throw new BTSLBaseException(this, methodName, "Given geo_domain_code is not defined in the system.");
                                        }
                                        }
                                        finally{
                                        	if(rsSelectGeo!=null)
                                        		rsSelectGeo.close();
                                        }
                                    }

                                    // Check--skip validation at parent user.
                                    // Insert the record
                                    addCount = pstmtInsertUserGeographies.executeUpdate();
                                    if (addCount <= 0) {
                                        log.debug(methodName, "Insertion for new user in USER_GEOGRAPHIES is failed.", count + addCount);
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in USER_GEOGRAPHIES is failed.");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Insertion for new user in USER_GEOGRAPHIES is failed.");
                                    } else {
                                        log.debug(methodName, "Insertion for new user in USER_GEOGRAPHIES is sucessfull.", count + addCount);
                                    }

                                    // Clear the parameter
                                    pstmtInsertUserGeographies.clearParameters();
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in USER_GEOGRAPHIES is failed.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Insert in USER_GEOGRAPHIES is failed.");
                                }
                                finally{
                                	if(rsselectprntId!=null)
                                		rsselectprntId.close();
                                }
                                // Insert in to USER_PHONES
                                // Parameter: new_user_id, old_user_id
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on USER_PHONES table.");
                                    pstmtInsertUserPhones.setString(1, newUserID);
                                    pstmtInsertUserPhones.setString(2, generatePhoneId(p_con));
                                    pstmtInsertUserPhones.setString(3, usrMigrVO.getToUserCatCode());
                                    pstmtInsertUserPhones.setString(4, usrMigrVO.getFromUserID());

                                    // Insert the record
                                    addCount = pstmtInsertUserPhones.executeUpdate();
                                    if (addCount <= 0) {
                                        log.debug(methodName, "Insertion for new user in USER_PHONES is failed.", count + addCount);
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in USER_PHONES is failed ");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Insertion for new user in USER_PHONES is failed.");
                                    } else {
                                        log.debug(methodName, "Insertion for new user in USER_PHONES is sucessfull .", count + addCount);
                                    }

                                    // Clear the parameter
                                    pstmtInsertUserPhones.clearParameters();
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in USER_PHONES is failed.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Insert in USER_PHONES is failed.");
                                }

                                
                                //User roles entries
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on USER_ROLES table.");
                                    
                                    pstmtSelectUserRoles.setString(1, usrMigrVO.getFromUserID());
                                    rsSelectUserRoles = pstmtSelectUserRoles.executeQuery();
                                    
                                    while(rsSelectUserRoles.next()) {
                                    pstmtInsertUserRoles.setString(1, newUserID);
                                    pstmtInsertUserRoles.setString(2, rsSelectUserRoles.getString("role_code"));
                                    pstmtInsertUserRoles.setString(3,  rsSelectUserRoles.getString("gateway_types"));
                                    // Insert the record
                                    addCount = pstmtInsertUserRoles.executeUpdate();
                                    if (addCount <= 0) {
                                        log.debug(methodName, "Insertion for new user in USER_ROLES is failed.", count + addCount);
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in USER_ROLES is failed ");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Insertion for new user in USER_ROLES is failed.");
                                    } else {
                                        log.debug(methodName, "Insertion for new user in USER_ROLES is sucessfull.", count + addCount);
                                    }
                                    pstmtInsertUserRoles.clearParameters();
                                    }
                                    
                                    // Clear the parameter
                                    pstmtSelectUserRoles.clearParameters();
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in USER_ROLES is failed.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Insert in USER_ROLES is failed.");
                                }

                                // Insert in to USER_SERVICES for new user
                                // Parameter: new_user_id, old_user_id,
                                // new_category_code
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on USER_SERVICES table.");
                                    pstmtInsertUserServices.setString(1, newUserID);
                                    pstmtInsertUserServices.setString(2, usrMigrVO.getFromUserID());
                                    pstmtInsertUserServices.setString(3, usrMigrVO.getFromUserCatCode());
                                    // Insert the record
                                    addCount = pstmtInsertUserServices.executeUpdate();

                                    if (addCount <= 0) {
                                        log.debug(methodName, "User has no service assigned previously, so after migration there will be no service for the user=", usrMigrVO
                                                        .getFromUserMsisdn());
                                    } else {
                                        log.debug(methodName, "Insertion for new user in USER_SERVICES is sucessfull.", count + addCount);
                                    }

                                    // Clear the parameter
                                    pstmtInsertUserServices.clearParameters();
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in USER_SERVICES is failed.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Insert in USER_SERVICES is failed.");
                                }
                                // Insert in to CHANNEL_USERS for new user
                                // Parameters: user_id, user_grade,
                                // transfer_profile_id, comm_profile_set_id,
                                // in_suspend,
                                // out_suspend, activated_on,application_id,
                                // mpay_profile_id, user_profile_id, is_primary,
                                // mcommerce_service_allow, low_bal_alert_allow
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on CHANNEL_USERS table.");
                                    final String catCode = usrMigrVO.getToUserCatCode();
                                    final String commProfId = p_profileGradeMap.get("COMM_PROFILE").get(catCode).getMsg1();
                                    final String trfProfileId = p_profileGradeMap.get("TRF_PROFILE").get(catCode).getMsg1();
                                    final String userGrd = p_profileGradeMap.get("USER_GRADE").get(catCode).getMsg1();
                                    
                                    if(log.isDebugEnabled()) {
                                    log.debug(methodName,"before selecting channel User information");
                                    }
                                    pstmtSelectChannelUserInfo.setString(1,usrMigrVO.getFromUserID());
                                    rsSelectChannelUserInfo=pstmtSelectChannelUserInfo.executeQuery();
                                     chnluservo = new ChannelUserVO();
                                    	if(rsSelectChannelUserInfo.next())
                                    {
                                    if(log.isDebugEnabled())
                                    {
                                    log.debug(methodName,"after selecting channel User information for same category movement");
                                    log.debug(methodName,"before inserting channel User information");
                                    }
                                    							
                                    chnluservo.setUserGrade(rsSelectChannelUserInfo.getString("user_grade"));
                                    chnluservo.setTransferProfileID(rsSelectChannelUserInfo.getString("transfer_profile_id"));
                                   	chnluservo.setCommissionProfileSetID(rsSelectChannelUserInfo.getString("comm_profile_set_id"));
                                    chnluservo.setCommissionProfileSetVersion(rsSelectChannelUserInfo.getString("application_id"));
                                    chnluservo.setMcommerceServiceAllow(rsSelectChannelUserInfo.getString("mcommerce_service_allow"));
                                    											
                        								}

                                    if (commProfId == null) {
                                        log.debug(methodName, "", "Default COMM_PROFILE not found.");
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Default COMM_PROFILE is not found.");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Default COMM_PROFILE is not found.");
                                    }

                                    if (trfProfileId == null) {
                                        log.debug(methodName, "", "Default TRF_PROFILE not found.");
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Default TRF_PROFILE is not found.");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Default TRF_PROFILE is not found.");
                                    }

                                    if (userGrd == null) {
                                        log.debug(methodName, "", "Default USER_GRADE not found.");
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Default USER_GRADE is not found.");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Default USER_GRADE is not found.");
									}

									pstmtInsertChannelUserInfo.setString(1, newUserID);
									if(usrMigrVO.getToUserCatCode().equalsIgnoreCase(usrMigrVO.getFromUserCatCode()))
									{
										pstmtInsertChannelUserInfo.setString(2, chnluservo.getUserGrade());
										pstmtInsertChannelUserInfo.setString(3, chnluservo.getTransferProfileID());
										pstmtInsertChannelUserInfo.setString(4, chnluservo.getCommissionProfileSetID());
										
									}
									else
									{
										pstmtInsertChannelUserInfo.setString(2, userGrd);
										pstmtInsertChannelUserInfo.setString(3, trfProfileId);
										pstmtInsertChannelUserInfo.setString(4, commProfId);
										
									}
									pstmtInsertChannelUserInfo.setString(5, "1");
									pstmtInsertChannelUserInfo.setString(6, " ");
									pstmtInsertChannelUserInfo.setString(7, newUserID);
                                    pstmtInsertChannelUserInfo.setString(8, "Y");
                                    pstmtInsertChannelUserInfo.setString(9, "N");
                                    pstmtInsertChannelUserInfo.setString(10, usrMigrVO.getFromUserID());

                                    // Insert the record
                                    addCount = pstmtInsertChannelUserInfo.executeUpdate();
                                    if (addCount <= 0) {
                                        log.debug(methodName, "Insertion for new user in CHANNEL_USERS is failed.", count + addCount);
                                        errorVO = new ListValueVO(String.valueOf(lineNo), "userMsg", "lineNo");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Insertion for new user in CHANNEL_USERS is failed.");
                                    } else {
                                        log.debug(methodName, "Insertion for new user in CHANNEL_USERS is sucessfull.", count + addCount);
                                    }

                                    // Clear the parameter
                                    pstmtInsertChannelUserInfo.clearParameters();
                                    int UpdatetransferRuleCount=0;
                                  pstmtupdateTransferRules.clearParameters();
                                  if(usrMigrVO.getToUserCatCode().equalsIgnoreCase(usrMigrVO.getFromUserCatCode()))
                                    {
                                    pstmtupdateTransferRules.setString(1, newUserID);
                                    pstmtupdateTransferRules.setString(2, usrMigrVO.getFromUserID());
                                    pstmtupdateTransferRules.setString(3, PretupsI.TRANSFER_RULE_PROMOTIONAL);
                                    UpdatetransferRuleCount=pstmtupdateTransferRules.executeUpdate();
                                   }
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert in CHANNEL_USERS is failed.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Insert in CHANNEL_USERS is failed.");
                                } catch (Exception e) {
                                    log.errorTrace(methodName, e);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), e.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Failed because of Default Commission Profile, Default Transfer profile  or Default User Grade is not found!!!!");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Failed because of Default Commission Profile, Default Transfer profile  or Default User Grade is not found!!!!");
                                }

                                // Entry in CHANNEL_TRANSFERS and
                                // CHANNEL_TRANSFER_ITEMS
                                try {
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Operation on CHANNEL_TRANSFERS and CHANNEL_TRANSFER_ITEMS table.");
                                    final Date curDate = new Date();
                                    final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                                    channelTransferVO.setNetworkCode(p_networkid);
                                    channelTransferVO.setNetworkCodeFor(p_networkid);
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
                                    pstmtChannelTrnf.setLong(++m, 0);
                                    pstmtChannelTrnf.setLong(++m, 0);
                                    pstmtChannelTrnf.setString(++m, "SYSTEM");
                                    pstmtChannelTrnf.setString(++m, PretupsI.CHANNEL_TYPE_C2C);// Type
                                    pstmtChannelTrnf.setString(++m, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);// Sub
                                    // type
                                    if (PretupsI.CHANNEL_TYPE_O2C.equals(PretupsI.CHANNEL_TYPE_C2C) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
                                                    .equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER) && PretupsI.CHANNEL_TRANSFER_ORDER_NEW
                                                    .equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                                        pstmtChannelTrnf.setTimestamp(++m, null);
                                    } else {
                                        pstmtChannelTrnf.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(new Date()));
                                    }

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
                                            // isMultipleWallet not applicable
                                            // isActiveUserIdApplied not
                                            // applicable.
                                        }
                                    }

                                    // Insert the record
                                    addCount = pstmtChannelTrnf.executeUpdate();
                                    addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres
                                    if (addCount <= 0) {
                                        log.debug(methodName, "Insertion for C2C in CHANNEL_TRANSFERS is failed.", count + addCount);
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert for C2C transfer in Channel Transfer is failed.");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Insertion for C2C in CHANNEL_TRANSFERS is failed.");
                                    } else {
                                        log.debug(methodName, "Insertion for C2C in CHANNEL_TRANSFERS is successful.");
                                    }
                                    // Clear the parameter
                                    pstmtChannelTrnf.clearParameters();

                                    // Set the parameters
                                    m = 0;
                                    if (BTSLUtil.isNullString(selectUserProductCode)) {
                                        selectUserProductCode = "ETOPUP";
                                    }

                                    if (seqNo > 999) {
                                        seqNo = 1;
                                    }

                                    pstmtChnlTrnfItems.setInt(++m, seqNo + 1);
                                    pstmtChnlTrnfItems.setString(++m, channelTransferVO.getTransferID());
                                    pstmtChnlTrnfItems.setString(++m, selectUserProductCode);// Product
                                    // code
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);// Requested
                                    // quantity
                                    pstmtChnlTrnfItems.setLong(++m, selectUserBalance);// Approved
                                    // quantity

                                    if (BTSLUtil.isNullString(String.valueOf(unitValue)) || unitValue == 0) {
                                        unitValue = 100;
                                    }
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
                                        log.debug(methodName, "Insertion for C2C transfer in CHANNEL_TRANSFERS_ITEMS is failed.", count + addCount);
                                        errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert for C2C transfer in CHANNEL_TRANSFERS_ITEMS is failed.");
                                        userMsgErrorList.add(errorVO);
                                        throw new BTSLBaseException(this, methodName, "Insert for C2C in CHANNEL_TRANSFERS_ITEMS is failed.");
                                    } else {
                                        log.debug(methodName, "Insertion for C2C in CHANNEL_TRANSFERS_ITEMS is sucessfull.", count + addCount);
                                        p_con.commit();
                                        sucMigUser++;
                                        log.debug(methodName, "", "User Migrated Sucessfully.");
                                    }
                                    // Clear the parameter
                                    pstmtChnlTrnfItems.clearParameters();
                                } catch (SQLException sqe) {
                                    log.errorTrace(methodName, sqe);
                                    log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), sqe.getMessage());
                                    errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "Insert for C2C transfer is failed.");
                                    userMsgErrorList.add(errorVO);
                                    throw new BTSLBaseException(this, methodName, "Insert for C2C transfer is failed.");
                                }

                            } else {
                                log.debug(lineNo, "New category code: " + usrMigrVO.getToUserCatCode(), " and new geo_domain_code are different=" + usrMigrVO
                                                .getToUserGeoCode());
                                errorVO = new ListValueVO("", usrMigrVO.getRecordNumber(), "geo_domain_code does not match.");
                                userMsgErrorList.add(errorVO);
                                continue;
                            }
                        } catch (BTSLBaseException be) {

                            log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "BTSLBaseException In Migration, so rollback.");
                            log.errorTrace(methodName, be);
                            p_con.rollback();
                            continue;
                        }
                    } catch (BTSLBaseException be) {
                        log.errorTrace(methodName, be);
                        log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "BTSLBaseException Operation RollBacked.");
                        log.errorTrace(methodName, be);
                        p_con.rollback();
                        continue;
                    } catch (Exception e) {
                        log.debug(lineNo, usrMigrVO.getFromUserMsisdn(), "Exception Operation RollBacked.");
                        p_con.rollback();
                        log.errorTrace(methodName, e);
                        continue;
                    }
                    final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_MSG_SUCCESS_WITHOUTPRODUCT, new String[] { usrMigrVO.getFromParentMsisdn(),usrMigrVO.getToParentMsisdn() });
                    PushMessage pushMessage = new PushMessage(usrMigrVO.getFromUserMsisdn(), btslMessage, null, null, locale, usrMigrVO.getNetworkCode());
                    pushMessage.push();
                    btslMessage = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_SENDER_PARENT, new String[] { usrMigrVO.getFromUserMsisdn() });
                    pushMessage = new PushMessage(usrMigrVO.getFromParentMsisdn(), btslMessage, null, null, locale, usrMigrVO.getNetworkCode());
                    pushMessage.push();
                    btslMessage = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_RECEIVER_PARENT, new String[] { usrMigrVO.getFromUserMsisdn() });
                    pushMessage = new PushMessage(usrMigrVO.getToParentMsisdn(), btslMessage, null, null, locale, usrMigrVO.getNetworkCode());
                    pushMessage.push();

                }
            }
            return userMsgErrorList;
        } catch (Exception e) {
            log.debug(methodName, "", "Exception " + e.getMessage());
            addCount = 0;
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        }// end of catch
        finally {
        	try {
                if (psmtSelectbal != null) {
                	psmtSelectbal.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (psmtselectPrntId != null) {
                	psmtselectPrntId.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtCheckGeoType != null) {
                	pstmtCheckGeoType.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (psmtupdateUserStatus != null) {
                	psmtupdateUserStatus.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtupdateTransferRules != null) {
                	pstmtupdateTransferRules.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtChannelTrnf != null) {
                	pstmtChannelTrnf.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	
        	try {
                if (pstmtChnlTrnfItems != null) {
                	pstmtChnlTrnfItems.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtInsertUserGeographies != null) {
                	pstmtInsertUserGeographies.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectGeo != null) {
                    pstmtSelectGeo.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectGeoWithCheck != null) {
                    pstmtSelectGeoWithCheck.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUser != null) {
                    pstmtUpdateUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUser != null) {
                    pstmtInsertUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserGeographies != null) {
                    pstmtInsertUserGeographies.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserProductTypes != null) {
                    pstmtInsertUserProductTypes.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserRoles != null) {
                    pstmtInsertUserRoles.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserServices != null) {
                    pstmtInsertUserServices.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectUserBalance != null) {
                    rsSelectUserBalance.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectUserBalance != null) {
                    pstmtSelectUserBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserBalance != null) {
                    pstmtInsertUserBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalance != null) {
                    pstmtUpdateUserBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserPhones != null) {
                    pstmtInsertUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectChannelUserInfo != null) {
                    rsSelectChannelUserInfo.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectChannelUserInfo != null) {
                    pstmtSelectChannelUserInfo.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertChannelUserInfo != null) {
                    pstmtInsertChannelUserInfo.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectParent != null) {
                    pstmtSelectParent.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectParent != null) {
                    rsSelectParent.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (rsCheckGeoType != null) {
                	rsCheckGeoType.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }

            p_migrationDetailMap.put("MIGRATED_USER", String.valueOf(sucMigUser));
            p_migrationDetailMap.put("FAILED_USER_WHILE_MIGRATION", String.valueOf(userMsgErrorList.size()));

            log.debug(methodName, "", "Exiting with succCount=" + sucMigUser + ", failCount=" + userMsgErrorList.size() + ", totalCount=" + p_finalUserMigrList.size());
        }// end of finally
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
        log.debug("generateUserId", "", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft( Long.toString(UserMigrationDAO.getNextID(p_con, TypesI.USERID, TypesI.ALL, p_networkCode, null)) , length);
        id = p_networkCode + p_prefix + id;
        log.debug("generateUserId",exiting , "id =" + id);

        return id;
    }

    /**
     * This method generated the phone Id .
     * 
     * @param p_con
     * @return
     * @throws Exception
     */
    private String generatePhoneId(Connection p_con) throws BTSLBaseException {
        log.debug("generatePhoneId", "", entered);
        String phoneId ;
        final IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
        final long id = _idGeneratorDAO.getNextID(p_con, "PHONE_ID", TypesI.ALL, TypesI.ALL, null);
        phoneId = String.valueOf(id);
        log.debug("generatePhoneId", exiting, "phoneId : " + phoneId);
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
        final Log _log = LogFactory.getLog(UserMigrationDAO.class.getName());
        _log.debug("getNextID", "", entered);
        final IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
        final long id = _idGeneratorDAO.getNextID(p_con, p_idType, p_year, p_networkID, p_currentDate);
        _log.debug("getNextID", exiting, "Id : " + id);
        return id;
    }

    /**
     * Method :loadMasterGeographyList
     * this method load list of Geographies on the basis of geographical code.
     * 
     * @param p_con
     *            Connection
     * @param p_loginUserID
     *            String
     * @return ArrayList<UserGeographiesVO>
     * @throws BTSLBaseException
     * @author vinay.singh
     */
    public ArrayList<UserGeographiesVO> loadMasterGeographyList(Connection p_con, String p_loginUserID) throws BTSLBaseException {
        final String methodName = "loadMasterGeographyList";
        log.debug(methodName, "Entered: p_loginUserID=" + p_loginUserID);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList<UserGeographiesVO> geographicesList = null;
        UserGeographiesVO userGeographiesVO = null;
        try {
            final String sqlSelect = userMigrationQry.loadMasterGeographyListQry();

            log.debug(methodName, query + sqlSelect);
            geographicesList = new ArrayList<>();

            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_loginUserID);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                userGeographiesVO = new UserGeographiesVO();
                userGeographiesVO.setGraphDomainCode(rs.getString("geography_code"));
                userGeographiesVO.setGraphDomainName(rs.getString("geography_name"));
                userGeographiesVO.setGraphDomainType(rs.getString("grph_domain_type"));
                userGeographiesVO.setGraphDomainTypeName(rs.getString("grph_domain_type_name"));
                userGeographiesVO.setGraphDomainSequenceNumber(rs.getInt("sequence_no"));
                geographicesList.add(userGeographiesVO);
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserMigrationDAO[loadMasterGeographyList]", "", "",
                            "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserMigrationDAO[loadMasterGeographyList]", "", "",
                            "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug(methodName, "Exiting: geographicesList size =" + (geographicesList!= null? geographicesList.size():0) );
        }
        return geographicesList;
    }

    /**
     * Method :loadMasterCategoryList
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            Connection
     * @return ArrayList<CategoryVO>
     * @throws BTSLBaseException
     * @author vinay.singh
     */
    public ArrayList<CategoryVO> loadMasterCategoryList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadMasterCategoryList";
        log.debug(methodName, entered);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CategoryVO categoryVO = null;
        StringBuilder strBuff = null;
        ArrayList<CategoryVO> categoryList = null;
        try {
            strBuff = new StringBuilder();
            strBuff.append("SELECT C.category_code,C.category_name,C.domain_code,D.domain_name,C.grph_domain_type,C.web_interface_allowed, ");
            strBuff.append("C.sms_interface_allowed,C.low_bal_alert_allow,C.services_allowed ");
            strBuff.append("FROM categories C,domains D ");
            strBuff.append("WHERE  C.status=? and C.domain_code!=? and C.domain_code=D.domain_code ");
            strBuff.append("ORDER BY domain_code,sequence_no");

            final String sqlSelect = strBuff.toString();

            log.debug(methodName, query + sqlSelect);
            categoryList = new ArrayList<>();

            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.YES);
            pstmtSelect.setString(2, PretupsI.OPERATOR_TYPE_OPT);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setDomainName(rs.getString("domain_name"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));

                categoryList.add(categoryVO);
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserMigrationDAO[loadMasterCategoryList]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserMigrationDAO[loadMasterCategoryList]", "", "", "",
                            "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryList size =" + (categoryList!=null? categoryList.size():0 ));
            }
        }
        return categoryList;
    }
    // added for channel user transfer
    /**
     * Method for checking Is MSISDN exist or not.
     * @param p_con Connection
     * @param p_userMigrationList ArrayList<FromUserVO>
     * @param p_validUserList ArrayList<FromUserVO>
     * @param p_invalidUserList ArrayList<FromUserVO>
     * @throws Exception 
     */
    	public ArrayList<UserMigrationVO> validateFromUsers(Connection p_con, List<UserMigrationVO> p_userMigrationList,RequestVO p_requestVO) throws Exception
    	{	
    		final String methodName = "validateFromUsers";
    		log.debug(methodName,"", "Entered: p_userMigrationList.size()=" + p_userMigrationList.size());

    		PreparedStatement pstmt = null;
    		PreparedStatement pendingTxnPstmt = null;
    		PreparedStatement ambC2STxnPstmt = null;
    		PreparedStatement operatorUserPstmt = null;
    		PreparedStatement psmtCheckCategoryGrphDomainType=null;
    		PreparedStatement psmtCheckInputGrphDomainType=null;
    		ResultSet rsCheckCatGrphDomainType=null;
    		ResultSet rsCheckInputGrphDomainType=null;
    		String toUserCatGRPHDomainType=null;
    		String toUserInputGRPHDomainType=null;
    		ResultSet rs = null;
    		ResultSet rsPendingTxn = null;
    		ResultSet rsAmbC2STxn = null;
    		ResultSet rsOperatorUser = null;
    		StringBuilder strBuff =null;
    		StringBuilder strPendingTxnBuff =null;
    		StringBuilder strC2SAmbiTxnBuff =null;
    		UserMigrationVO userMigrationVO=null;
    		String sqlSelectParent =null;
    		PreparedStatement pstmtSelectParent=null;
    		ResultSet rsSelectParent=null;
    		StringBuilder strBuffSelectParent =null;
    		ArrayList<UserMigrationVO> validUserList = new  ArrayList<>();
    		HashMap requestHashMap =new HashMap();
    		PreparedStatement psmtCheckDomain=null;
    		ResultSet rsCheckDomain=null;
    		try
    		{   
    			//Validate from parent geographical domain code, from parent category code and from user MSISDN
    			strBuff = new StringBuilder();
    			strBuff.append(" SELECT u.user_name,u.user_id, u.login_id, u.network_code,u.status,");
    			strBuff.append(" up.user_name,p.phone_language,p.country,pct.sequence_no, pct.grph_domain_type,u.category_code ");
    			strBuff.append(" FROM USERS U,CATEGORIES CT,USERS UP,USER_PHONES P,CATEGORIES PCT");
    			strBuff.append(" WHERE u.category_code=ct.category_code AND P.USER_ID=U.USER_ID");
    			strBuff.append(" AND u.user_type='CHANNEL' AND u.status<>'N'and u.status<>'C'");
    			strBuff.append(" AND ct.category_code=? AND u.msisdn=? ");
    			strBuff.append(" AND pct.category_code=?");
    			strBuff.append(" AND (up.USER_ID=Case  WHEN u.parent_id = 'ROOT' then u.user_id else u.parent_id end)");
    			String sqlSelect = strBuff.toString();
    			log.debug(methodName,"", query + sqlSelect);

    			//Query for pending C2C or O2C transaction.
    			strPendingTxnBuff=new StringBuilder();
    			strPendingTxnBuff.append(" SELECT 1  FROM CHANNEL_TRANSFERS");
    			strPendingTxnBuff.append(" WHERE (from_user_id=? OR to_user_id =?) AND ");
    			strPendingTxnBuff.append(" (status <> ? AND status <> ? )");
    			String sqlPendingSelect = strPendingTxnBuff.toString();
    			log.debug(methodName,"", query+sqlPendingSelect);
    			
    			//Query for any C2S ambiguous transaction
    			strC2SAmbiTxnBuff=new StringBuilder();
    			//local_index_missing
    			strC2SAmbiTxnBuff.append("SELECT 1  FROM C2S_TRANSFERS ");
    			strC2SAmbiTxnBuff.append("WHERE transfer_date > (select executed_upto from process_status where process_id = 'C2SMIS') and sender_id=? AND (TRANSFER_STATUS =? OR TRANSFER_STATUS =?)");
    			String sqlC2SAmbiTxnSelect = strC2SAmbiTxnBuff.toString();
    			log.debug(methodName,"", query+sqlC2SAmbiTxnSelect);

    			//Check for OPERATOR user
    			StringBuilder sqlOptSelBuff=new StringBuilder();
    			sqlOptSelBuff.append(" SELECT 1 FROM CATEGORIES cat");
    			sqlOptSelBuff.append(" WHERE  cat.status<> 'N' AND cat.domain_code <>'OPT' AND cat.category_code=?");
    			String sqlOptSelect=sqlOptSelBuff.toString();
    			log.debug(methodName,"", query+sqlOptSelect);

    			//Check parent info.
    			strBuffSelectParent = new StringBuilder();
    			strBuffSelectParent.append(" SELECT U.user_id,U.parent_id, U.owner_id, U.status, U.user_name,");
    			strBuffSelectParent.append(" U.category_code,CAT.domain_code");
    			strBuffSelectParent.append(" FROM USERS U, CATEGORIES CAT");
    			strBuffSelectParent.append(" WHERE U.status <>'N' and U.status <>'C'");
    			strBuffSelectParent.append(" AND U.category_code=CAT.category_code");
    			strBuffSelectParent.append(" AND U.msisdn=?");
    			sqlSelectParent = strBuffSelectParent.toString();
    			log.debug(methodName,"", query+sqlSelectParent);

    			//Check Domain info.
    			String checkDomain ="select DOMAIN_CODE from categories where STATUS='Y' and CATEGORY_CODE=?";
    			log.debug(methodName,"", query+checkDomain);

    			String checkCategoryGRPHDomainType ="select GRPH_DOMAIN_TYPE from categories where STATUS='Y' and CATEGORY_CODE=?";
    			log.debug(methodName,"", "QUERY checkCategoryGRPHDomainType="+checkCategoryGRPHDomainType);

    			String checkInputGRPHDomainType ="select GRPH_DOMAIN_TYPE from GEOGRAPHICAL_DOMAINS where STATUS='Y' and GRPH_DOMAIN_CODE=?";
    			log.debug(methodName,"", "QUERY checkInputGRPHDomainType="+checkInputGRPHDomainType);
    			
    			
    			psmtCheckDomain=p_con.prepareStatement(checkDomain);
    			
    			String toUserDomain=null;
    			String toParentDomain=null;

    			pstmt = p_con.prepareStatement(sqlSelect);
    			pendingTxnPstmt=p_con.prepareStatement(sqlPendingSelect);
    			ambC2STxnPstmt=p_con.prepareStatement(sqlC2SAmbiTxnSelect);
    			operatorUserPstmt=p_con.prepareStatement(sqlOptSelect);
    			pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);
    			
    			psmtCheckCategoryGrphDomainType=p_con.prepareStatement(checkCategoryGRPHDomainType);
    			psmtCheckInputGrphDomainType=p_con.prepareStatement(checkInputGRPHDomainType);							

    			
    			
    			int migrationListSize=p_userMigrationList.size();
    			for(int i=0, j=migrationListSize; i<j; i++)
    			{
    				userMigrationVO=p_userMigrationList.get(i);
    				log.debug(methodName,"", "FromUserCatCode="+userMigrationVO.getFromUserCatCode()+", FromUserMsisdn="+userMigrationVO.getFromUserMsisdn()+", ToUserCatCode="+userMigrationVO.getToUserCatCode());

    				pstmt.setString(1, userMigrationVO.getFromUserCatCode());
    				pstmt.setString(2, userMigrationVO.getFromUserMsisdn());
    				pstmt.setString(3, userMigrationVO.getToUserCatCode());

    				rs = pstmt.executeQuery();
    				if (rs.next())
    				{	
    					String categoryCode = rs.getString("category_code");
    					log.debug(methodName,"DB CategoryCode="+categoryCode+", Provided CategoryCode="+userMigrationVO.getFromUserCatCode());					

    					userMigrationVO.setFromUserID(rs.getString("user_id"));
    					userMigrationVO.setFromUserLoginID(rs.getString("login_id"));
    					userMigrationVO.setNetworkCode(rs.getString("network_code"));
    					userMigrationVO.setFromUserStatus(rs.getString("status"));
    					userMigrationVO.setFromUserParentName(rs.getString("USER_NAME"));
    					userMigrationVO.setPhoneLang(rs.getString("PHONE_LANGUAGE"));
    					userMigrationVO.setCountry(rs.getString("COUNTRY"));
    					userMigrationVO.setFromUserName(rs.getString("user_name"));
    					userMigrationVO.setToUserCatCodeSeqNo(rs.getString("sequence_no"));
    					userMigrationVO.setToGeoDomainType(rs.getString("grph_domain_type"));

    					//Check whether there is any pending C2C or O2C transaction
    					pendingTxnPstmt.setString(1, userMigrationVO.getFromUserID());
    					pendingTxnPstmt.setString(2, userMigrationVO.getFromUserID());
    					pendingTxnPstmt.setString(3, "CLOSE");
    					pendingTxnPstmt.setString(4, "CNCL");
    					rsPendingTxn=pendingTxnPstmt.executeQuery();
    					
    					//Check whether there is any C2S ambiguous transaction.
    					ambC2STxnPstmt.setString(1, userMigrationVO.getFromUserID());
    					ambC2STxnPstmt.setString(2, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
    					ambC2STxnPstmt.setString(3, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
    					rsAmbC2STxn=ambC2STxnPstmt.executeQuery();
    					
    					if(rsPendingTxn.next())
    					{
    						userMigrationVO.setMessage("Pending O2C or C2C transaction is found for the user.");
    						userMigrationVO.setLineNumber(i);
    						requestHashMap.put(errorMessage,"Pending O2C or C2C transaction is found for the user.");
    						
    					}
    					else if (rsAmbC2STxn.next())
    					{
    						userMigrationVO.setMessage("C2S ambiguous or underprocess transaction is found for the user.");
    						userMigrationVO.setLineNumber(i);
    						requestHashMap.put(errorMessage,"C2S ambiguous or underprocess transaction is found for the user.");
    						
    					}
    					else
    					{
    						log.debug(methodName,"", "Operator user check Outer ToUserCatCode="+userMigrationVO.getToUserCatCode());
    						operatorUserPstmt.setString(1, userMigrationVO.getToUserCatCode());
    						rsOperatorUser=operatorUserPstmt.executeQuery();
    						if(rsOperatorUser.next())
    						{
    							log.debug(methodName,"", "Operator user check Inner ToParentMsisdn="+userMigrationVO.getToParentMsisdn());
    							//Now validate the to parent user
    							pstmtSelectParent.setString(1, userMigrationVO.getToParentMsisdn());
    							rsSelectParent=pstmtSelectParent.executeQuery();
    							if(rsSelectParent.next())
    							{
    								//If To user category sequence is "1", then after migration, user's parent will be ROOT
    								if("1".equals(userMigrationVO.getToUserCatCodeSeqNo())) {
    									userMigrationVO.setToParentID("ROOT");
    								} else {
    									userMigrationVO.setToParentID(rsSelectParent.getString("user_id"));
    								}                    
    								userMigrationVO.setToOwnerID(rsSelectParent.getString("owner_id"));
    								userMigrationVO.setToUserParentName(rsSelectParent.getString("USER_NAME"));

    								log.debug(methodName,"", "Check Domain ToUserCatCode="+userMigrationVO.getToUserCatCode());					

    								psmtCheckDomain.setString(1, userMigrationVO.getToUserCatCode()); 
    								rsCheckDomain=psmtCheckDomain.executeQuery();
    								while(rsCheckDomain.next())
    								{
    									toUserDomain=rsCheckDomain.getString("DOMAIN_CODE");
    								}

    								psmtCheckDomain.setString(1, userMigrationVO.getToParentCatCode());
    								rsCheckDomain=psmtCheckDomain.executeQuery();
    								while(rsCheckDomain.next())
    								{
    									toParentDomain=rsCheckDomain.getString("DOMAIN_CODE");
    								}
    								psmtCheckCategoryGrphDomainType.setString(1, userMigrationVO.getToUserCatCode());
    								rsCheckCatGrphDomainType=psmtCheckCategoryGrphDomainType.executeQuery();
    								while(rsCheckCatGrphDomainType.next())
    								{
    									toUserCatGRPHDomainType=rsCheckCatGrphDomainType.getString("GRPH_DOMAIN_TYPE");
    								}

    								psmtCheckInputGrphDomainType.setString(1, userMigrationVO.getToUserGeoCode());
    								rsCheckInputGrphDomainType=psmtCheckInputGrphDomainType.executeQuery();
    								while(rsCheckInputGrphDomainType.next())
    								{
    									toUserInputGRPHDomainType=rsCheckInputGrphDomainType.getString("GRPH_DOMAIN_TYPE");
    								}
    								
    								if(toUserDomain.equalsIgnoreCase(toParentDomain)) {
    									if(null!=toUserCatGRPHDomainType && toUserCatGRPHDomainType.equalsIgnoreCase(toUserInputGRPHDomainType))
    									validUserList.add(userMigrationVO);
    									else
    									{
    										userMigrationVO.setLineNumber(i);
    										userMigrationVO.setMessage("User can't be migrated as no mapping found for To geography code and To user category code.");
    										userMigrationVO.setParentExist(false);
    										p_requestVO.setMessageCode(PretupsErrorCodesI.FROM_USER_VALIDATION_FAIL);
    										requestHashMap.put(errorMessage,"User can't be migrated as no mapping found for To geography code and To user category code.");
    									}
    								} else
    								{
    									userMigrationVO.setLineNumber(i);
    									userMigrationVO.setMessage("User can't be migrated as To parent Domain does not match To user Domain.");
    									userMigrationVO.setParentExist(false);
    									p_requestVO.setMessageCode(PretupsErrorCodesI.FROM_USER_VALIDATION_FAIL);
    									requestHashMap.put(errorMessage,"User can't be migrated as To parent Domain does not match To user Domain.");
    								}
    							}
    							else
    							{		
    								userMigrationVO.setLineNumber(i);
    								userMigrationVO.setMessage("Parent user does not found in the system.");
    								userMigrationVO.setParentExist(false);
    								p_requestVO.setMessageCode(PretupsErrorCodesI.FROM_USER_VALIDATION_FAIL);
    								requestHashMap.put(errorMessage,"Parent user does not found in the system.");
    							}
    						}
    						else
    						{		
    							userMigrationVO.setLineNumber(i);
    							userMigrationVO.setMessage("User catn't be migrated as an Operator user.");
    							userMigrationVO.setParentExist(false);
    							p_requestVO.setMessageCode(PretupsErrorCodesI.FROM_USER_VALIDATION_FAIL);
    							requestHashMap.put(errorMessage,"User catn't be migrated as an Operator user.");
    						}
    					}
    				}
    				else
    				{		
    					userMigrationVO.setLineNumber(i);
    					userMigrationVO.setMessage("User not found.");
    					p_requestVO.setMessageCode(PretupsErrorCodesI.FROM_USER_VALIDATION_FAIL);
    					requestHashMap.put(errorMessage,"User not found.");
    				}

    				pendingTxnPstmt.clearParameters();
    				ambC2STxnPstmt.clearParameters();
    				pstmt.clearParameters();
    				pstmtSelectParent.clearParameters();
    				psmtCheckDomain.clearParameters();
    				p_requestVO.setRequestMap(requestHashMap);
    			}
    		}
    		catch (SQLException sqe)
    		{
    			log.errorTrace(methodName, sqe);
    			throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
    		}
    		catch (Exception ex)
    		{
    			log.errorTrace(methodName, ex);
    			throw new BTSLBaseException(ex);
    		}
    		finally	{
    			
    			try {
    				if (psmtCheckDomain != null) {
    					psmtCheckDomain.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (pstmt != null) {
    					pstmt.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rs != null) {
    					rs.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsPendingTxn != null) {
    					rsPendingTxn.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (pendingTxnPstmt != null) {
    					pendingTxnPstmt.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsAmbC2STxn != null) {
    					rsAmbC2STxn.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (ambC2STxnPstmt != null) {
    					ambC2STxnPstmt.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (pstmtSelectParent != null) {
    					pstmtSelectParent.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsSelectParent != null) {
    					rsSelectParent.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (operatorUserPstmt != null) {
    					operatorUserPstmt.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsOperatorUser != null) {
    					rsOperatorUser.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (psmtCheckCategoryGrphDomainType != null) {
    					psmtCheckCategoryGrphDomainType.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (psmtCheckInputGrphDomainType != null) {
    					psmtCheckInputGrphDomainType.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsCheckDomain != null) {
    					rsCheckDomain.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsCheckCatGrphDomainType != null) {
    					rsCheckCatGrphDomainType.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsCheckInputGrphDomainType != null) {
    					rsCheckInputGrphDomainType.close();
    				}
    			} catch (Exception e) {
    				log.errorTrace(methodName, e);
    			}
    			log.debug(methodName,"", "Exiting with validUserList="+validUserList.size());
    		}
    		return validUserList;
    	}
    
	/**
     * Method for user migration validate user.
     * 
     * @param p_con
     *            Connection
     * @param p_userMigrationList
     *            ArrayList<FromUserVO>
     * @param p_validUserList
     *            ArrayList<FromUserVO>
     * @param p_invalidUserList
     *            ArrayList<FromUserVO>
     * @throws BTSLBaseException
     */
    public ArrayList<UserMigrationVO> validateFromUsersMigration(Connection p_con, List<UserMigrationVO> p_userMigrationList, ArrayList<ListValueVO> p_invalidUserList) throws BTSLBaseException {
        final String methodName = "validateFromUsersMigration";
        log.debug(methodName, "", "Entered: p_userMigrationList.size()=" + p_userMigrationList.size() + " p_invalidUserList.size=" + p_invalidUserList.size());

        PreparedStatement pstmt = null;
        PreparedStatement pendingTxnPstmt = null;
        PreparedStatement ambC2STxnPstmt = null;
        PreparedStatement operatorUserPstmt = null;
        ResultSet rs = null;
        ResultSet rsPendingTxn = null;
        ResultSet rsAmbC2STxn = null;
        ResultSet rsOperatorUser = null;
        StringBuilder strBuff = null;
        StringBuilder strPendingTxnBuff = null;
        StringBuilder strC2SAmbiTxnBuff = null;
        UserMigrationVO userMigrationVO = null;
        String sqlSelectParent = null;
        PreparedStatement pstmtSelectParent = null;
        ListValueVO errorVO = new ListValueVO();
        ResultSet rsSelectParent = null;
        StringBuilder strBuffSelectParent = null;
        final ArrayList<UserMigrationVO> validUserList = new ArrayList<>();
        PreparedStatement psmtCheckDomain = null;
        ResultSet rsCheckDomain = null;
        try {
            // Validate from parent geographical domain code, from parent category code and from user MSISDN
            
            strBuff = new StringBuilder();
            strBuff.append(" SELECT u.user_name,u.user_id, u.login_id,u.parent_id, u.network_code,u.status,ct.category_code,up.msisdn,");
            strBuff.append(" up.user_name,p.phone_language,p.country,pct.sequence_no, pct.grph_domain_type,u.category_code ");
            strBuff.append(" FROM USERS U,CATEGORIES CT,USERS UP,USER_PHONES P,CATEGORIES PCT,user_phones uup");
            strBuff.append(" WHERE u.category_code=ct.category_code AND P.USER_ID=U.USER_ID");
            strBuff.append(" AND u.user_type='CHANNEL' AND u.status<>'N'and u.status<>'C'");
            strBuff.append(" AND u.msisdn=? ");
            strBuff.append(" AND pct.category_code=?");
            strBuff.append(" AND (up.USER_ID=Case  WHEN u.parent_id = 'ROOT' then u.user_id else u.parent_id end)");
            strBuff.append("  AND up.user_id = uup.user_id ");
            String sqlSelect = strBuff.toString();
            log.debug(methodName,"", query + sqlSelect);
            
            // Query for pending C2C or O2C transaction.
            
            strPendingTxnBuff = new StringBuilder();
            strPendingTxnBuff.append(" SELECT 1  FROM CHANNEL_TRANSFERS");
            strPendingTxnBuff.append(" WHERE (from_user_id=? OR to_user_id =?) AND ");
            strPendingTxnBuff.append(" (status <> ? AND status <> ? )");
            final String sqlPendingSelect = strPendingTxnBuff.toString();
            log.debug(methodName, "", query + sqlPendingSelect);
           

            // Query for any C2S ambiguous transaction
            strC2SAmbiTxnBuff = new StringBuilder();
            //local_index_missing
            strC2SAmbiTxnBuff.append("SELECT 1  FROM C2S_TRANSFERS ");
            strC2SAmbiTxnBuff.append("WHERE transfer_date > (select executed_upto from process_status where process_id = 'C2SMIS') and sender_id=? AND (TRANSFER_STATUS =? OR TRANSFER_STATUS =?)");
            final String sqlC2SAmbiTxnSelect = strC2SAmbiTxnBuff.toString();
            log.debug(methodName, "", query + sqlC2SAmbiTxnSelect);

            // Check for OPERATOR user
            final StringBuilder sqlOptSelBuff = new StringBuilder();
            sqlOptSelBuff.append(" SELECT 1 FROM CATEGORIES cat");
            sqlOptSelBuff.append(" WHERE  cat.status<> 'N' AND cat.domain_code <>'OPT' AND cat.category_code=?");
            final String sqlOptSelect = sqlOptSelBuff.toString();
            log.debug(methodName, "", query + sqlOptSelect);

            // Check parent info.
            strBuffSelectParent = new StringBuilder();
            strBuffSelectParent.append(" SELECT U.user_id,U.parent_id, U.owner_id, U.status, U.user_name,UG.GRPH_DOMAIN_CODE ,");
            strBuffSelectParent.append(" U.category_code,CAT.domain_code,CAT.grph_domain_type");
            strBuffSelectParent.append(" FROM USERS U, CATEGORIES CAT,USER_GEOGRAPHIES UG");
            strBuffSelectParent.append(" WHERE U.status <>'N' and U.status <>'C'");
            strBuffSelectParent.append(" AND U.category_code=CAT.category_code");
            strBuffSelectParent.append(" AND U.msisdn=? AND UG.user_id=U.user_id");
            sqlSelectParent = strBuffSelectParent.toString();
            log.debug(methodName, "", query + sqlSelectParent);

            // Check Domain info.
            final String checkDomain = "select DOMAIN_CODE from categories where STATUS='Y' and CATEGORY_CODE=?";
            log.debug(methodName, "", query + checkDomain);

           
            psmtCheckDomain = p_con.prepareStatement(checkDomain);
            String toUserDomain = null;
            String toParentDomain = null;
 
            pstmt = p_con.prepareStatement(sqlSelect);
            pendingTxnPstmt = p_con.prepareStatement(sqlPendingSelect);
            ambC2STxnPstmt = p_con.prepareStatement(sqlC2SAmbiTxnSelect);
            operatorUserPstmt = p_con.prepareStatement(sqlOptSelect);
            pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);
            

            final int migrationListSize = p_userMigrationList.size();
            for (int i = 0, j = migrationListSize; i < j; i++) {
                    
                    userMigrationVO=p_userMigrationList.get(i);
                    log.debug(methodName,"", "FromUserMsisdn="+userMigrationVO.getFromUserMsisdn()+", ToUserCatCode="+userMigrationVO.getToUserCatCode()+
                            ", To parent msisdn: "+userMigrationVO.getToParentMsisdn());
                pstmt.setString(1, userMigrationVO.getFromUserMsisdn());
                pstmt.setString(2, userMigrationVO.getToUserCatCode());

                rs = pstmt.executeQuery();
                if (rs.next()) {
                    final String categoryCode = rs.getString("category_code");
                    log.debug(methodName,"", "FromUserMsisdn="+userMigrationVO.getFromUserMsisdn()+", ToUserCatCode="+userMigrationVO.getToUserCatCode()+
                            ", To parent msisdn: "+rs.getString("msisdn")+" , Category Code"+categoryCode);
                    if(userMigrationVO.getToUserCatCode().equals(categoryCode) && (rs.getString("parent_id").equalsIgnoreCase(PretupsI.ROOT_PARENT_ID) || 
                                                                                (rs.getString("msisdn").equalsIgnoreCase(userMigrationVO.getToParentMsisdn()))))
                    {
                            userMigrationVO.setLineNumber(i+1);
                            userMigrationVO.setMessage("User already migrated.");
                            errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "User already migrated.");
                            p_invalidUserList.add(errorVO);
                            continue;
                    }
                else
                {
                    userMigrationVO.setFromUserCatCode(rs.getString("category_code"));
                    userMigrationVO.setFromUserID(rs.getString("user_id"));
                    userMigrationVO.setFromUserLoginID(rs.getString("login_id"));
                    userMigrationVO.setNetworkCode(rs.getString("network_code"));
                    userMigrationVO.setFromUserStatus(rs.getString("status"));
                    userMigrationVO.setFromUserParentName(rs.getString("USER_NAME"));
                    userMigrationVO.setFromParentMsisdn(rs.getString("msisdn"));
                    userMigrationVO.setPhoneLang(rs.getString("PHONE_LANGUAGE"));
                    userMigrationVO.setCountry(rs.getString("COUNTRY"));
                    userMigrationVO.setFromUserName(rs.getString("user_name"));
                    userMigrationVO.setToUserCatCodeSeqNo(rs.getString("sequence_no"));
                    userMigrationVO.setToGeoDomainType(rs.getString("grph_domain_type"));
                    
                    // Check whether there is any pending C2C or O2C transaction
                    pendingTxnPstmt.setString(1, userMigrationVO.getFromUserID());
                    pendingTxnPstmt.setString(2, userMigrationVO.getFromUserID());
                    pendingTxnPstmt.setString(3, "CLOSE");
                    pendingTxnPstmt.setString(4, "CNCL");
                    rsPendingTxn = pendingTxnPstmt.executeQuery();
                    
                    UserTransferCountsVO userTransferCountsVO = new UserTransferCountsVO();
                    UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
                    userTransferCountsVO = userTransferCountsDAO.loadTransferCounts( p_con, userMigrationVO.getFromUserID(),false);

                    // Check whether there is any C2S ambiguous transaction.
                    ambC2STxnPstmt.setString(1, userMigrationVO.getFromUserID());
                    ambC2STxnPstmt.setString(2, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                    ambC2STxnPstmt.setString(3, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
                    rsAmbC2STxn = ambC2STxnPstmt.executeQuery();

                    if(userTransferCountsVO!=null && PretupsI.SOS_PENDING_STATUS.equals(userTransferCountsVO.getLastSOSTxnStatus())) {
                        userMigrationVO.setMessage("Pending SOS transaction is found for the user.");
                        userMigrationVO.setLineNumber(i+1);
                        errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "Pending SOS transaction is found for the user.");
                        p_invalidUserList.add(errorVO);
                        continue;
                    }else if(userTransferCountsVO!=null && PretupsI.LAST_LR_PENDING_STATUS.equals(userTransferCountsVO.getLastLrStatus())){
                        userMigrationVO.setMessage("Pending Last Recharge transaction is found for the user.");
                        userMigrationVO.setLineNumber(i+1);
                        errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "Pending Last Recharge transaction is found for the user.");
                        p_invalidUserList.add(errorVO);
                        continue;
                    }else if (rsPendingTxn.next()) {
                        userMigrationVO.setMessage("Pending O2C or C2C transaction is found for the user.");
                        userMigrationVO.setLineNumber(i+1);
                        errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "Pending O2C or C2C transaction is found for the user.");
                        p_invalidUserList.add(errorVO);
                        continue;                        
                    }else if (rsAmbC2STxn.next()) {
                        userMigrationVO.setMessage("C2S ambiguous or underprocess transaction is found for the user.");
                        userMigrationVO.setLineNumber(i+1);
                        errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "C2S ambiguous or underprocess transaction is found for the user.");
                        p_invalidUserList.add(errorVO);
                        continue;
                    } 
                    else {
                        log.debug(methodName, "", "Operator user check Outer ToUserCatCode=" + userMigrationVO.getToUserCatCode());
                        operatorUserPstmt.setString(1, userMigrationVO.getToUserCatCode());
                        rsOperatorUser = operatorUserPstmt.executeQuery();
                        if (rsOperatorUser.next()) {
                            log.debug(methodName, "", "Operator user check Inner ToParentMsisdn=" + userMigrationVO.getToParentMsisdn());
                            // Now validate the to parent user
                            pstmtSelectParent.setString(1, userMigrationVO.getToParentMsisdn());
                           
                 
                            rsSelectParent = pstmtSelectParent.executeQuery();
                            if (rsSelectParent.next()) {
                                // If To user category sequence is "1", then
                                // after migration, user's parent will be ROOT
                                if ("1".equals(userMigrationVO.getToUserCatCodeSeqNo())) {
                                    userMigrationVO.setToParentID("ROOT");
                                } else {
                                    userMigrationVO.setToParentID(rsSelectParent.getString("user_id"));
                                }
                                userMigrationVO.setToUserParentName(rsSelectParent.getString("USER_NAME"));
                                if(userMigrationVO.getFromUserMsisdn().equals(userMigrationVO.getToParentMsisdn()))
                                {
                                    userMigrationVO.setToParentCatCode(userMigrationVO.getToUserCatCode());
                                    userMigrationVO.setToParentGeoCode(userMigrationVO.getToUserGeoCode());
                                }
                                else
                                {
                                        userMigrationVO.setToOwnerID(rsSelectParent.getString("owner_id"));
                                        userMigrationVO.setToParentCatCode(rsSelectParent.getString("CATEGORY_CODE"));
                                        userMigrationVO.setToParentGeoCode(rsSelectParent.getString("GRPH_DOMAIN_CODE"));
                                }
                                
                                log.debug(methodName, "", "Parent Id=" + userMigrationVO.getToParentID());
                                log.debug(methodName, "", "Check Domain ToUserCatCode=" + userMigrationVO.getToUserCatCode());
                                log.debug(methodName, "", "Check Domain ToParentCatCode=" + userMigrationVO.getToParentCatCode());
                                log.debug(methodName, "", "Check Domain ToParentGeoCode=" + userMigrationVO.getToParentGeoCode());
                                    
                                if(userMigrationVO.getToUserCatCode().equals(rsSelectParent.getString("CATEGORY_CODE")))
                                {
                                    userMigrationVO.setLineNumber(i+1);
                                    userMigrationVO.setMessage("User cannot be at Parent level.");
                                    errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "User cannot be at Parent level.");
                                    p_invalidUserList.add(errorVO);
                                    continue;
                                }
                                else
                                {
                                    psmtCheckDomain.setString(1, userMigrationVO.getToUserCatCode());
                                    rsCheckDomain = psmtCheckDomain.executeQuery();
                                    while (rsCheckDomain.next()) {
                                        toUserDomain = rsCheckDomain.getString("DOMAIN_CODE");
                                    }
    
                                    psmtCheckDomain.setString(1, userMigrationVO.getToParentCatCode());
                                    rsCheckDomain = psmtCheckDomain.executeQuery();
                                    while (rsCheckDomain.next()) {
                                        toParentDomain = rsCheckDomain.getString("DOMAIN_CODE");
                                    }
                                    if (null!=toUserDomain && toUserDomain.equalsIgnoreCase(toParentDomain)) {
                                        validUserList.add(userMigrationVO);
                                    } else {
                                        userMigrationVO.setLineNumber(i+1);
                                        userMigrationVO.setMessage("User can't be migrated as To parent Domain does not match To user Domain.");
                                        userMigrationVO.setParentExist(false);
                                        errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(),
                                                        "User can't be migrated as To parent Domain does not match To user Domain.");
                                        p_invalidUserList.add(errorVO);
                                        continue;
                                    }
                                } 
                            }
                                else {
                                    userMigrationVO.setLineNumber(i+1);
                                    userMigrationVO.setMessage("Parent user does not found in the system.");
                                    userMigrationVO.setParentExist(false);
                                    errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "Parent user does not found in the system.");
                                    p_invalidUserList.add(errorVO);
                                    continue;
                                }
                            
                        } else {
                            userMigrationVO.setLineNumber(i+1);
                            userMigrationVO.setMessage("User catn't be migrated as an Operator user.");
                            userMigrationVO.setParentExist(false);
                            errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "User catn't be migrated as an Operator user.");
                            p_invalidUserList.add(errorVO);
                            continue;
                        }
                    }
                    }
                } else {
                    userMigrationVO.setLineNumber(i+1);
                    userMigrationVO.setMessage("User not found.");
                    errorVO = new ListValueVO(userMigrationVO.getFromUserMsisdn(), userMigrationVO.getRecordNumber(), "User not found.");
                    p_invalidUserList.add(errorVO);
                    continue;
                }

                pendingTxnPstmt.clearParameters();
                ambC2STxnPstmt.clearParameters();
                pstmt.clearParameters();
                pstmtSelectParent.clearParameters();
                psmtCheckDomain.clearParameters();
            }
        } catch (SQLException sqe) {
            errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), sqe.getMessage());
            p_invalidUserList.add(errorVO);
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            errorVO = new ListValueVO("", userMigrationVO.getRecordNumber(), ex.getMessage());
            p_invalidUserList.add(errorVO);
            
            log.errorTrace(methodName, ex);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsPendingTxn != null) {
                    rsPendingTxn.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pendingTxnPstmt != null) {
                    pendingTxnPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsAmbC2STxn != null) {
                    rsAmbC2STxn.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (ambC2STxnPstmt != null) {
                    ambC2STxnPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectParent != null) {
                    pstmtSelectParent.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectParent != null) {
                    rsSelectParent.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (operatorUserPstmt != null) {
                    operatorUserPstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsOperatorUser != null) {
                    rsOperatorUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtCheckDomain != null) {
                	psmtCheckDomain.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsCheckDomain != null) {
                	rsCheckDomain.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.debug(methodName, "", "Exiting with validUserList=" + validUserList.size() + " p_invalidUserList.size=" + p_invalidUserList.size());
        }
        return validUserList;
    }
    
}
