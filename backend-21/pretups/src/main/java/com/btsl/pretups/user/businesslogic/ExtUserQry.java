package com.btsl.pretups.user.businesslogic;

public interface ExtUserQry {
	public String loadUsersDetailsforExtReqQry(String p_userID, String p_statusUsed, String p_status);
	public String  loadUsersDetailsforExtCodeReqQry(String p_userID, String p_statusUsed, String p_status);

}
