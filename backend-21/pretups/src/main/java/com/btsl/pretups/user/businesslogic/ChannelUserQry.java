package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ChannelUserQry {

	String loadChannelUserDetailsQry();
	
	String loadChannelUserDetailsTcpQry();
	
	String loadChannelUserDetailsQryLoginID();

	String loadChannelUserDetailsQryLoginIDTcp();
	
	PreparedStatement loadUsersDetailsQry(Connection con,String status, String userId, String statusUsed , String msisdn) throws SQLException;

	PreparedStatement loadUsersDetailsByLoginIdQry(Connection con,String status, String userId, String statusUsed, String loginId) throws SQLException;

	String creditUserBalancesQry();

	String debitUserBalancesQry();

	PreparedStatement loadUserForChannelByPassQry(Connection con, String statusAllowed,
			String networkCode,String toCategoryCode ,String userId,String userName ,String parentID) throws SQLException;

	PreparedStatement loadUsersByParentIDRecursiveQry(Connection con,String statusAllowed, String networkCode, String toCategoryCode,
			String parentID, String userName, String userId) throws SQLException;
	
	PreparedStatement  loadUserHierarchyListQry(Connection con,String statusUsed,String mode, String status, String []userId, String userCategory, PreparedStatement pstmt )throws SQLException;
	
	PreparedStatement isUserExistForChannelByPassQry(Connection con, String networkCode, String toCategoryCode, String parentID, String userCode, String statusAllowed) throws SQLException;
	
	PreparedStatement isUserExistByParentIDRecursiveQry(Connection con, String networkCode, String toCategoryCode, String userCode, String parentID, String statusAllowed) throws SQLException;

	String loadChannelUserByUserIDQry();
	
	String loadUsersForParentFixedCatQry(String statusAllowed,String fixedCat, int ctrlLvl);
	
	PreparedStatement loadUsersForHierarchyFixedCatQry(Connection con,  String statusAllowed, String fixedCat, int ctrlLvl, String networkCode, String toCategoryCode,
			String userId,String userName, String parentUserID ) throws SQLException;
	
	PreparedStatement isUserExistForHierarchyFixedCatQry(Connection con , String statusAllowed, String fixedCat, int ctrlLvl, String networkCode,String toCategoryCode,String userCode, String parentUserID   ) throws SQLException;
	
	String loadUsersChnlBypassByGeoQry();
	
	String isUserExistsByGeoQry();
	
	String creditUserBalancesForMultipleWalletQry();
	
	String isValidTimeForOptInOutQry();
	
	String isProfileActiveQry();
	
	PreparedStatement loadUserHierarchyListForTransferQry(Connection con, String statusUsed, String status, String mode, String[] userId, String userCategory,PreparedStatement pstmt)throws SQLException;
	
	PreparedStatement loadUserHierarchyListForTransferByCatergoryQry(
			Connection con, String statusUsed, String status, String mode,
			String[] userId, String userCategory, String category,String userName,PreparedStatement pstmt )throws SQLException;
	
	String loadChannelUserDetailsForTransferQry(boolean isParentOwnerMsisdnRequired, boolean isUserCode);

	String loadChannelUserDetailsForTransferTcpQry(boolean isParentOwnerMsisdnRequired, boolean isUserCode);
	
	StringBuilder debitUserBalancesForO2CQry();
	String loadAllChildUserBalanceQry();
	String isUserInHierarchyQry(String c_identifierType);
    String loadUserNameAutoSearchOnZoneDomainCategoryQry();
    String loadStaffUserDetailbyCHUser();
    String loadChannelUserByUserIDAnyStatusQry();
    PreparedStatement loadUsersDetailsByLoginOrMsisdnQry(Connection con, String msisdn,  String loginId , String status, String statusUsed,String networkCode) throws SQLException;
	PreparedStatement loadUsersDetailsByExtCodeQry(Connection con,String status, String userId, String statusUsed, String extCode) throws SQLException;

	PreparedStatement loadApprovalUsersListQry(Connection p_con, String p_categoryCode, String p_lookupType, String p_networkCode,
	String p_parentGrphDomainCode, String p_status, String p_userType) throws SQLException;
			
	String validateUsersForBatchC2CQry(String p_categoryCode);
	String loadChannelUserDetailsByLoginIDANDORMSISDNQry(String p_msisdn, String p_loginid);

	PreparedStatement loadUsersDetailsByExtcode(Connection con,String status, String statusUsed, String userID,
			 String extcode) throws SQLException;
}
