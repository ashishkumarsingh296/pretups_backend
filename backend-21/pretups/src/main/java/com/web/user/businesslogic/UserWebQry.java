package com.web.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UserWebQry {
	
	
	
	public String loadUsersListQry( String p_networkCode, String p_categoryCode, String p_userName, String p_userID, String p_ownerID, String p_sessionUserID, String p_statusUsed, String p_status);
	public String loadOwnerUserListQry(String p_statusUsed, String p_status);
	public PreparedStatement loadReportOwnerUserListQry(Connection p_con, String p_parentGraphDomainCode, String p_userID, String p_username, String p_domainCode, String p_networkCode) throws SQLException;
	public PreparedStatement loadUsersListByNameAndOwnerIdQry(Connection p_con, String p_categoryCode, String p_userName, String p_ownerId, String p_userID, String p_statusUsed, String p_status, String p_userType)throws SQLException;
	public PreparedStatement loadApprovalUsersListQry(Connection p_con, String p_categoryCode, String p_lookupType, int p_sequenceNo, String p_grphDomainType, String p_networkCode, String p_parentGrphDomainCode, String p_status, String p_userType)throws SQLException;
	public PreparedStatement isUserInSameGRPHDomainQry(Connection p_con, String p_userId, String p_sessionUserId,String p_userGrphDomainType, String p_sessionUserGrphDomainType) throws SQLException;
	public String loadUsersListForUserTypeQry( String p_statusUsed,String p_status, boolean p_isChannelUser) ;
	public String loadUsersListByUserTypeQry(String p_categoryCode, String p_userName, String p_userID, String p_ownerID, String p_sessionUserID, String p_statusUsed, String p_status, String p_userType) ;
	public PreparedStatement loadUserListOnZoneCategoryHierarchyQry(Connection p_con, String p_userCategory, String p_zoneCode, String p_userName, String p_loginuserID, String domainCode)throws SQLException;
	public PreparedStatement loadSTKApprovalUsersListQry(Connection p_con, String p_categoryCode, String p_lookupType, int p_sequenceNo, String p_grphDomainType, String p_networkCode, String p_parentGrphDomainCode, String p_status)throws SQLException;
	public String checkBarLimitQry();
	public String loadOwnerUserListForUserTransferQry(String p_statusUsed, String p_status);
	public String checkOwnerListQuery(String p_statusUsed, String p_status);
}
