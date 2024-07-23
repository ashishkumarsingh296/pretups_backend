package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface UserLevelTrfRuleQry {
	public Log _log = LogFactory.getLog(UserLevelTrfRuleDAO.class.getName());
	public PreparedStatement loadUsersListInSelfHierarchyQry(String receiverStatusAllowed,Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID,String p_receiverUserID, boolean p_isFromWeb)throws SQLException;
	public PreparedStatement loadBatchUsersListInSelfHierarchyQry(Connection pcon, String pnetworkCode, String ptoCategoryCode,  String puserID)throws SQLException;
	public String loadBatchUsersListByOwnerQry();
	public String loadBatchUsersListAtSameLevelQry();
	public String  loadBatchUsersByCatCodeAndWithoutSessionCatCodeQry();
	public String loadBatchUsersByCategoryCodeQry();
	PreparedStatement loadUsersListInSelfHierarchyQryForLoginID(String receiverStatusAllowed, Connection p_con,
			String p_networkCode, String p_toCategoryCode, String p_loginID, String p_userID, String p_receiverUserID,
			boolean p_isFromWeb) throws SQLException;
	
}
