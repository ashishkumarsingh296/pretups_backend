package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;

public class UserLevelTrfRulePostgresQry implements UserLevelTrfRuleQry{
	@Override
	public PreparedStatement loadUsersListInSelfHierarchyQry(String receiverStatusAllowed,Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID,String p_receiverUserID, boolean p_isFromWeb)throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
		if(p_isFromWeb){
			strBuff.append(" with recursive q as (SELECT user_id, user_name, network_code,status,category_code, user_type  FROM USERS ");
			strBuff.append(" WHERE ");
	    	strBuff.append(" user_id = ? ");
	    	strBuff.append(" union all SELECT m.user_id, m.user_name, m.network_code,m.status,m.category_code, m.user_type  FROM USERS m join q on q.user_id=m.parent_id ");
	    	strBuff.append(" ) select q.user_id, q.user_name from q where ");
	    	strBuff.append(" network_code = ?   ");
	    	strBuff.append(" and status IN (" + receiverStatusAllowed + ") AND  category_code = ?  AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "'");
	    	strBuff.append(" AND user_id = ? AND UPPER(user_name) LIKE UPPER(?)  ");
	    	strBuff.append(" ORDER BY user_name ");
	    }
	    else {
	    	strBuff.append(" SELECT user_id, user_name FROM USERS ");
	 	    strBuff.append(" WHERE network_code = ?");
	 	    strBuff.append(" AND status IN (" + receiverStatusAllowed + ") AND category_code = ?  AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "'");
	    	strBuff.append(" AND user_id = ?  AND status IN (" + receiverStatusAllowed + ") AND category_code = ?  AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
	    }
		final String sqlSelect = strBuff.toString();
	    if (_log.isDebugEnabled()) {
	        _log.debug("loadUsersListInSelfHierarchy", "QUERY sqlSelect=" + sqlSelect);
	    }
	    pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
	    int i = 0;
	   
	    if(p_isFromWeb){
	    	pstmt.setString(++i, p_userID);
	    	pstmt.setString(++i, p_networkCode);
	 	    pstmt.setString(++i, p_toCategoryCode);
	    	pstmt.setString(++i, p_userID);
	    	pstmt.setString(++i, p_userName);
	    }
	    else{
	    	 pstmt.setString(++i, p_networkCode);
	 	    pstmt.setString(++i, p_toCategoryCode);
	    	pstmt.setString(++i, p_receiverUserID);
	    }
	    return pstmt;
	}
	@Override
	public PreparedStatement loadBatchUsersListInSelfHierarchyQry(Connection pcon, String pnetworkCode, String ptoCategoryCode, String puserID) throws SQLException{
		PreparedStatement pstmt = null;
		final StringBuilder strBuff = new StringBuilder(
	            "with recursive q as (	SELECT U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,CPSV.APPLICABLE_FROM ");
		strBuff.append(" ,C.CATEGORY_CODE C_CATEGORY_CODE , u.CATEGORY_CODE m_CATEGORY_CODE , CU.USER_ID CU_USER_ID, CU.COMM_PROFILE_SET_ID CU_COMM_PROFILE_SET_ID, ");
		strBuff.append(" CPS.COMM_PROFILE_SET_ID CPS_COMM_PROFILE_SET_ID, CPSV.COMM_PROFILE_SET_ID CPSV_COMM_PROFILE_SET_ID ,");
		strBuff.append(" CU.TRANSFER_PROFILE_ID CU_TRANSFER_PROFILE_ID, TP.PROFILE_ID TP_PROFILE_ID, u.network_code, u.status , U.user_type ");
			strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV  ");
			strBuff.append(" where  U.user_id =?  ");
			strBuff.append("union all SELECT m.USER_ID,m.MSISDN, m.LOGIN_ID, C.CATEGORY_NAME,m.EXTERNAL_CODE, m.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,CPSV.APPLICABLE_FROM ");
			strBuff.append(" ,C.CATEGORY_CODE C_CATEGORY_CODE , m.CATEGORY_CODE m_CATEGORY_CODE , CU.USER_ID CU_USER_ID, CU.COMM_PROFILE_SET_ID CU_COMM_PROFILE_SET_ID, ");
			strBuff.append(" CPS.COMM_PROFILE_SET_ID CPS_COMM_PROFILE_SET_ID, CPSV.COMM_PROFILE_SET_ID CPSV_COMM_PROFILE_SET_ID ,");
			strBuff.append(" CU.TRANSFER_PROFILE_ID CU_TRANSFER_PROFILE_ID, TP.PROFILE_ID TP_PROFILE_ID, m.network_code, m.status , m.user_type ");
			strBuff.append("FROM  CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV,USERS m join q on  q.user_id=m.parent_id ");
			strBuff.append(" ) select USER_ID,MSISDN,LOGIN_ID, CATEGORY_NAME,EXTERNAL_CODE, USER_STATUS, ");
			strBuff.append("IN_SUSPEND,PROFILE_STATUS,COMM_PROF_STATUS, max(APPLICABLE_FROM) FROM  q  ");
			strBuff.append("WHERE C_CATEGORY_CODE=m_CATEGORY_CODE AND CU_USER_ID=USER_ID AND CU_COMM_PROFILE_SET_ID=CPS_COMM_PROFILE_SET_ID ");
			strBuff.append("AND CU_TRANSFER_PROFILE_ID=TP_PROFILE_ID AND CPS_COMM_PROFILE_SET_ID = CPSV_COMM_PROFILE_SET_ID  ");
			strBuff.append("AND network_code =? AND status = 'Y' AND m_CATEGORY_CODE =? ");
			strBuff.append("AND user_id !=? AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' AND APPLICABLE_FROM <= CURRENT_TIMESTAMP");
			strBuff.append("GROUP BY USER_ID,MSISDN, LOGIN_ID, CATEGORY_NAME,EXTERNAL_CODE, USER_STATUS,IN_SUSPEND,PROFILE_STATUS, COMM_PROF_STATUS ORDER BY USER_ID ");
			final String selectQuery = strBuff.toString();
			if (_log.isDebugEnabled()) {
	            _log.debug("loadBatchUsersListInSelfHierarchy", "Query selectQuery = " + selectQuery);
	        }
			 pstmt = (PreparedStatement) pcon.prepareStatement(selectQuery);
	         int i = 0;
	         pstmt.setString(++i, puserID);
	         pstmt.setString(++i, pnetworkCode);
	         pstmt.setString(++i, ptoCategoryCode);
	         pstmt.setString(++i, puserID);
	       
			return pstmt;
		}
	@Override
	public String loadBatchUsersListByOwnerQry(){
			 final StringBuilder strBuff = new StringBuilder(
		             "SELECT U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS USER_STATUS,CU.IN_SUSPEND,TP.STATUS PROFILE_STATUS, CPS.STATUS COMM_PROF_STATUS,MAX(CPSV.APPLICABLE_FROM) APPLICABLE_FROM ");
		strBuff.append("FROM USERS U, CATEGORIES C, CHANNEL_USERS CU,TRANSFER_PROFILE TP,COMMISSION_PROFILE_SET CPS, COMMISSION_PROFILE_SET_VERSION CPSV ");
		strBuff.append("WHERE C.CATEGORY_CODE=U.CATEGORY_CODE AND CU.USER_ID=U.USER_ID AND CU.COMM_PROFILE_SET_ID=CPS.COMM_PROFILE_SET_ID AND CU.TRANSFER_PROFILE_ID=TP.PROFILE_ID AND CPS.COMM_PROFILE_SET_ID = CPSV.COMM_PROFILE_SET_ID ");
		
		strBuff.append("AND U.network_code =? AND U.status = 'Y' AND U.category_code =? ");
		strBuff.append("AND U.category_code <> ? AND U.user_id != ? AND U.owner_id=? AND U.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
		
		strBuff.append("AND CPSV.APPLICABLE_FROM <= current_timestamp ");
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
			
			strBuff.append("AND CPSV.APPLICABLE_FROM <= current_timestamp ");
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
			
			strBuff.append("AND CPSV.APPLICABLE_FROM <= current_timestamp ");
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
		
		strBuff.append("AND CPSV.APPLICABLE_FROM <= current_timestamp ");
		strBuff.append("GROUP BY U.USER_ID,U.MSISDN, U.LOGIN_ID, C.CATEGORY_NAME,U.EXTERNAL_CODE, U.STATUS,CU.IN_SUSPEND,TP.STATUS, CPS.STATUS ");
		strBuff.append("ORDER BY U.USER_ID ");
		return strBuff.toString();
	}
	
	@Override
	public PreparedStatement loadUsersListInSelfHierarchyQryForLoginID(String receiverStatusAllowed,Connection p_con, String p_networkCode, String p_toCategoryCode, String p_loginID, String p_userID,String p_receiverUserID, boolean p_isFromWeb)throws SQLException{
		final StringBuilder strBuff = new StringBuilder();
		PreparedStatement pstmt = null;
	    strBuff.append(" SELECT user_id, login_id FROM USERS");
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
