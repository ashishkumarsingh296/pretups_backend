package com.web.pretups.user.businesslogic;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public interface ChannelUserWebQry {
	Log LOG = LogFactory.getLog(ChannelUserWebQry.class.getName());
	
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQry(Connection con,String networkCode,String categoryCode,String geographicalDomainCode,String userName,String loginUserID,String ownerUserID,String statusAllowed,String receiverStatusAllowed,String userId) throws SQLException;

	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQry(Connection con,String networkCode,String categoryCode,String geographicalDomainCode,String userName,String loginUserID,String ownerUserID,String statusAllowed,String receiverStatusAllowed ) throws SQLException;

	public String loadChannelUserHierarchyQry(boolean isUserCode );
	
	public PreparedStatement deleteOrSuspendChnlUsersInBulkForMsisdn(Connection con,String userID,Map prepareStatementMap) throws SQLException;
	
	public PreparedStatement loadChannelUserListQry(Connection con,String userCategory,String domainCode,String userId,String userName,String zoneCode) throws SQLException;
	
	public PreparedStatement loadCategoryUserHierarchyQry(Connection con,String networkCode,String categoryCode,String loginUserID,String userName)throws SQLException;
	
	public PreparedStatement loadUsersForEnquiryQry(Connection con,String networkCode,String categoryCode,String loginUserID,String userName,String ownerUserID,boolean isOnlyActiveUser,String geographicalDomainCode)throws SQLException;
	
	public String loadUsersForBatchFOCQry(String  categoryCode,String geographicalDomainCode,String receiverStatusAllowed);
	
	public String validateUsersForBatchC2CQry(String categoryCode,String StatusAllowed);
	
	public String ValidateChnlUserDetailsByExtCodeQry();
	
	public String validateUsersForBatchDP(String categoryCode,String geographicalDomainCode);
	
	public PreparedStatement loadStaffUsersDetailsbyLoginIDforSuspend(Connection con,String chusrid,String loginID,String status) throws SQLException;
	
	public PreparedStatement loadStaffUsersDetailsForSuspend(Connection con,String status,String msisdn,String chuserid) throws SQLException;
	
	public String loadChannelUserDetailsByUserNameQry();
	
	public PreparedStatement loadUsersForBatchO2CQry(Connection con,String[] categoryCode,String[] senderStatusAllowed,String[] geographicalDomainCode,Date comPrfApplicableDate,String domainCode,String networkCode,String p_productCode)throws SQLException;
	
	public String loadParentUserDetailsByUserID();
	
	public String loadChannelUserDetailsByLoginIDANDORMSISDN(String msisdn,String  loginID);
	
	public String  loadUsersForAdditionalDetail(String[] geographicalDomainCode);
	
	public String loadUsersForBulkAutoC2C(String[] categoryCode,String[] geographicalDomainCode);
	
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyForWithdraw(Connection con,String networkCode,String categoryCode,String geographicalDomainCode,String userName,String loginUserID,String ownerUserID,String statusAllowed,String senderStatusAllowed ) throws SQLException;
	
	public String isControlledProfileAlreadyAssociated();
	
	public PreparedStatement loadUserListOnZoneDomainCategoryWithMSISDN(Connection con,String fromUserID,String userName,String domainCode,String userCategory,String zoneCode,String pLOGinuserID  ) throws SQLException;
	
	public PreparedStatement loadChannelUserListHierarchyQry(Connection con,String domainCode,String userCategory,String userName,String userId,String zoneCode) throws SQLException;
	
	public PreparedStatement loadTransferredUserPrevHierarchyQry(Connection con,String mode,String[] userId,boolean isSearchOnDate,Date fromDate,Date toDate)throws SQLException;
	
	public String validateUsersForBatchFOC(String[] categoryCode,String[] receiverStatusAllowed,String [] geographicalDomainCode);
	
	public String loadUsersForBatchDP(String[] categoryCode,String [] geographicalDomainCode);
	
	public String loadUsersForBatchDP(String[] categoryCode,String [] geographicalDomainCode, String productCode);
	
    public PreparedStatement loadUsersDetailsForStaff(Connection con,String status,String statusUsed,String userId,String msisdn)throws SQLException;
	
	public PreparedStatement loadUsersDetailsByLoginIdForStaff(Connection con,String status,String statusUsed,String userId,String loginID)throws SQLException;
	
	public String loadUsersDetailsForC2C(String status,String statusUsed);
	
	public PreparedStatement loadUserDetailsByExtCode(Connection con,String status,String statusUsed,String userId,String extCode)throws SQLException;
	
	public PreparedStatement loadSTKUsersDetails(Connection con,String status,String statusUsed,String userId,String msisdn)throws SQLException;
	
	public PreparedStatement loadSTKUsersDetailsByLoginId(Connection con,String status,String statusUsed,String userId,String loginID)throws SQLException;
	
	public PreparedStatement loadUserHierarchyListForTransfer(Connection con,String status,String statusUsed,String[] userId,String mode,String userCategory)throws SQLException;
	
	public String loadCategoryUsersWithinGeoDomainHirearchy(String userName, boolean isLoginChannelUsr,String loginID,String msisdn,String userStatusIN);

	public String debitUserBalancesForRevTxnQry();
	
	public String debitUserBalancesForRevTxnDeleteQry();
	
	public String creditUserBalancesForRevTxnQry();
	
	public String creditUserBalancesForRevTxnDeleteQry();
	
	public PreparedStatement userHierarchyQryByCategory(Connection con,String networkCode,String categoryCode,String loginUserID,String userName)throws SQLException;
	
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchyQryForAutoO2C(Connection con,String categoryCode,String geographicalDomainCode,String userName,String loginUserID ) throws SQLException;

}