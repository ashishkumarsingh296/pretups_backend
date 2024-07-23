package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;

public class UserLevelTrfRuleOracleQry implements UserLevelTrfRuleQry{
@Override
public PreparedStatement loadUsersListInSelfHierarchyQry(String receiverStatusAllowed,Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID,String p_receiverUserID, boolean p_isFromWeb)throws SQLException{
	final StringBuilder strBuff = new StringBuilder();
	PreparedStatement pstmt = null;
    strBuff.append(" SELECT user_id, user_name FROM USERS");
    strBuff.append(" WHERE network_code = ?");
    strBuff.append(" AND status IN (" + receiverStatusAllowed + ") AND category_code = ?  AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "'");
    if(p_isFromWeb){
    	strBuff.append(" AND user_id = ? AND UPPER(user_name) LIKE UPPER(?)");
    	strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH user_id =? ");
    	strBuff.append(" ORDER BY user_name");
    }
    else {
    	strBuff.append(" AND user_id = ?");
    }
    final String sqlSelect = strBuff.toString();
    if (_log.isDebugEnabled()) {
        _log.debug("loadUsersListInSelfHierarchy", "QUERY sqlSelect=" + sqlSelect);
    }
    pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
    int i = 0;
    pstmt.setString(++i, p_networkCode);
    pstmt.setString(++i, p_toCategoryCode);
    if(p_isFromWeb){
    	pstmt.setString(++i, p_userID);

    	pstmt.setString(++i, p_userName);
    	pstmt.setString(++i, p_userID);
    }
    else{
    	pstmt.setString(++i, p_receiverUserID);
    }
    return pstmt;
}


@Override
public PreparedStatement loadBatchUsersListInSelfHierarchyQry(Connection pcon, String pnetworkCode, String ptoCategoryCode, String puserID) throws SQLException{
	PreparedStatement pstmt = null;
	final StringBuilder strBuff = new StringBuilder("SELECT U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,MAX(CPSV.APPLICABLE_FROM) APPLICABLE_FROM ");
		strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV ");
		strBuff.append("WHERE C.CATEGORY_CODE=U.CATEGORY_CODE AND CU.USER_ID=U.USER_ID AND CU.COMM_PROFILE_SET_ID=CPS.COMM_PROFILE_SET_ID AND CU.TRANSFER_PROFILE_ID=TP.PROFILE_ID AND CPS.COMM_PROFILE_SET_ID = CPSV.COMM_PROFILE_SET_ID ");

		strBuff.append("AND U.network_code =? AND U.status = 'Y' AND U.category_code =? ");
		strBuff.append("AND U.user_id !=? AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' AND CPSV.APPLICABLE_FROM <= TO_DATE(SYSDATE) ");
		strBuff.append("CONNECT BY PRIOR U.user_id=U.parent_id START WITH U.user_id =? ");

		strBuff.append("GROUP BY U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS,CU.IN_SUSPEND,TP.STATUS, CPS.STATUS ");
		strBuff.append("ORDER BY U.USER_ID ");
		final String selectQuery = strBuff.toString();
		if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersListInSelfHierarchy", "Query selectQuery = " + selectQuery);
        }
		 pstmt = (PreparedStatement) pcon.prepareStatement(selectQuery);
         int i = 0;
         pstmt.setString(++i, pnetworkCode);
         pstmt.setString(++i, ptoCategoryCode);
         pstmt.setString(++i, puserID);
         pstmt.setString(++i, puserID);
		return pstmt;
	}


/*@Override
public PreparedStatement loadBatchUsersListInSelfHierarchyTcpQry(Connection pcon, String pnetworkCode, String ptoCategoryCode, String puserID) throws SQLException{
	PreparedStatement pstmt = null;
	final StringBuilder strBuff = new StringBuilder("SELECT CU.TRANSFER_PROFILE_ID, U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND, CPS.STATUS COMM_PROF_STATUS,MAX(CPSV.APPLICABLE_FROM) APPLICABLE_FROM ");
		strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV ");
		strBuff.append("WHERE C.CATEGORY_CODE=U.CATEGORY_CODE AND CU.USER_ID=U.USER_ID AND CU.COMM_PROFILE_SET_ID=CPS.COMM_PROFILE_SET_ID  AND CPS.COMM_PROFILE_SET_ID = CPSV.COMM_PROFILE_SET_ID ");

		strBuff.append("AND U.network_code =? AND U.status = 'Y' AND U.category_code =? ");
		strBuff.append("AND U.user_id !=? AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' AND CPSV.APPLICABLE_FROM <= TO_DATE(SYSDATE) ");
		strBuff.append("CONNECT BY PRIOR U.user_id=U.parent_id START WITH U.user_id =? ");

		strBuff.append("GROUP BY CU.TRANSFER_PROFILE_ID, U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS,CU.IN_SUSPEND, CPS.STATUS ");
		strBuff.append("ORDER BY U.USER_ID ");
		final String selectQuery = strBuff.toString();
		if (_log.isDebugEnabled()) {
            _log.debug("loadBatchUsersListInSelfHierarchy", "Query selectQuery = " + selectQuery);
        }
		 pstmt = (PreparedStatement) pcon.prepareStatement(selectQuery);
         int i = 0;
         pstmt.setString(++i, pnetworkCode);
         pstmt.setString(++i, ptoCategoryCode);
         pstmt.setString(++i, puserID);
         pstmt.setString(++i, puserID);
		return pstmt;
	}
*/
	@Override
	public String loadBatchUsersListByOwnerQry(){
			 final StringBuilder strBuff = new StringBuilder(
		             "SELECT U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,MAX(CPSV.APPLICABLE_FROM) APPLICABLE_FROM ");
		strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV ");
		strBuff.append("WHERE C.CATEGORY_CODE=U.CATEGORY_CODE AND CU.USER_ID=U.USER_ID AND CU.COMM_PROFILE_SET_ID=CPS.COMM_PROFILE_SET_ID AND CU.TRANSFER_PROFILE_ID=TP.PROFILE_ID AND CPS.COMM_PROFILE_SET_ID = CPSV.COMM_PROFILE_SET_ID ");
		
		strBuff.append("AND U.network_code =? AND U.status = 'Y' AND U.category_code =? ");
		strBuff.append("AND U.category_code <> ? AND U.user_id != ? AND U.owner_id=? AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		
		strBuff.append("AND CPSV.APPLICABLE_FROM <= TO_DATE(SYSDATE) ");
		strBuff.append("GROUP BY U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS,CU.IN_SUSPEND,TP.STATUS, CPS.STATUS ");
		strBuff.append("ORDER BY U.USER_ID ");
		return strBuff.toString();
	}
	@Override
	public String loadBatchUsersListAtSameLevelQry(){
					final StringBuilder strBuff = new StringBuilder(
			                "SELECT U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,MAX(CPSV.APPLICABLE_FROM) APPLICABLE_FROM ");
			strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV ");
			strBuff.append("WHERE C.CATEGORY_CODE=U.CATEGORY_CODE AND CU.USER_ID=U.USER_ID AND CU.COMM_PROFILE_SET_ID=CPS.COMM_PROFILE_SET_ID AND CU.TRANSFER_PROFILE_ID=TP.PROFILE_ID AND CPS.COMM_PROFILE_SET_ID = CPSV.COMM_PROFILE_SET_ID ");
			
			strBuff.append("AND U.network_code = ? AND U.status = 'Y' AND U.category_code = ? AND U.user_id != ? ");
			strBuff.append("AND U.owner_id=? AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
			
			strBuff.append("AND CPSV.APPLICABLE_FROM <= TO_DATE(SYSDATE) ");
			strBuff.append("GROUP BY U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS,CU.IN_SUSPEND,TP.STATUS, CPS.STATUS ");
			strBuff.append("ORDER BY U.USER_ID ");
			return strBuff.toString();
	}
	@Override
	public String  loadBatchUsersByCatCodeAndWithoutSessionCatCodeQry(){
					final StringBuilder strBuff = new StringBuilder(
			                "SELECT U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,MAX(CPSV.APPLICABLE_FROM) APPLICABLE_FROM ");
			strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV ");
			strBuff.append("WHERE C.CATEGORY_CODE=U.CATEGORY_CODE AND CU.USER_ID=U.USER_ID AND CU.COMM_PROFILE_SET_ID=CPS.COMM_PROFILE_SET_ID AND CU.TRANSFER_PROFILE_ID=TP.PROFILE_ID AND CPS.COMM_PROFILE_SET_ID = CPSV.COMM_PROFILE_SET_ID ");
			
			strBuff.append("AND U.network_code =? AND U.status ='Y' AND U.category_code =? AND (U.parent_id= ? OR U.category_code <> ?) ");
			strBuff.append("AND U.user_id != ? AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
			
			strBuff.append("AND CPSV.APPLICABLE_FROM <= TO_DATE(SYSDATE) ");
			strBuff.append("GROUP BY U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS,CU.IN_SUSPEND,TP.STATUS, CPS.STATUS ");
			strBuff.append("ORDER BY U.USER_ID ");
			return strBuff.toString();
	}
	@Override
	public String loadBatchUsersByCategoryCodeQry(){
				final StringBuilder strBuff = new StringBuilder(
		                "SELECT U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,MAX(CPSV.APPLICABLE_FROM) APPLICABLE_FROM ");
		strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV ");
		strBuff.append("WHERE C.CATEGORY_CODE=U.CATEGORY_CODE AND CU.USER_ID=U.USER_ID AND CU.COMM_PROFILE_SET_ID=CPS.COMM_PROFILE_SET_ID AND CU.TRANSFER_PROFILE_ID=TP.PROFILE_ID AND CPS.COMM_PROFILE_SET_ID = CPSV.COMM_PROFILE_SET_ID ");
		
		strBuff.append("AND U.network_code = ? AND U.status = 'Y' AND U.category_code = ? ");
		strBuff.append("AND U.user_id != ? AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		
		strBuff.append("AND CPSV.APPLICABLE_FROM <= TO_DATE(SYSDATE) ");
		strBuff.append("GROUP BY U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS,CU.IN_SUSPEND,TP.STATUS, CPS.STATUS ");
		strBuff.append("ORDER BY U.USER_ID ");
		return strBuff.toString();
	}
	
	@Override
	public PreparedStatement loadUsersListInSelfHierarchyQryForLoginID(String receiverStatusAllowed,Connection p_con, String p_networkCode, String p_toCategoryCode, String p_loginID, String p_userID,String p_receiverUserID, boolean p_isFromWeb)throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
	    strBuff.append(" SELECT user_id, login_id,user_name,msisdn FROM USERS");
	    strBuff.append(" WHERE network_code = ?");
	    strBuff.append(" AND status IN (" + receiverStatusAllowed + ") AND category_code = ?  AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "'");
	    if(p_isFromWeb){
	    	strBuff.append(" AND user_id = ? AND UPPER(login_id) LIKE UPPER(?)");
	    	strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH user_id =? ");
	    	strBuff.append(" ORDER BY login_id");
	    }
	    else {
	    	strBuff.append(" AND user_id = ?");
	    }
	    final String sqlSelect = strBuff.toString();
	    if (_log.isDebugEnabled()) {
	        _log.debug("loadUsersListInSelfHierarchyQryForLoginID", "QUERY sqlSelect=" + sqlSelect);
	    }
	    pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
	    int i = 0;
	    pstmt.setString(++i, p_networkCode);
	    pstmt.setString(++i, p_toCategoryCode);
	    if(p_isFromWeb){
	    	pstmt.setString(++i, p_userID);

	    	pstmt.setString(++i, p_loginID);
	    	pstmt.setString(++i, p_userID);
	    }
	    else{
	    	pstmt.setString(++i, p_receiverUserID);
	    }
	    return pstmt;
	}
	
}
