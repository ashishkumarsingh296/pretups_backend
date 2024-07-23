package com.btsl.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldReportDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;
import com.btsl.pretups.channeluser.businesslogic.ApplistReqVO;
import com.restapi.channelAdmin.StaffUserListByParntReqVO;

public interface UserQry {
	public String loadUsersDetailsQry();
	public String loadUserDetailsByEmpcodeQry(String p_catCode);
	public String loadUserDetailsFormUserIDQry();
	public String loadUserDetailsByEmpcodeQuery();
	public String loadUserDetailsByLoginIdQry();
	public String loadUserDetailsByMsisdnQry();
	public String loadAllUserDetailsByLoginIDQry();
	public String loadAllUserDetailsByExternalCodeQry();
	public String isChildUserActiveQry();
	public String fetchUserHierarchy(String searchCriteria);
	public String fetchRecentC2cTxn(String data);
	public String fetchC2cTrfData();
	public String fetchTotalTrans();
	public String fetchUserDetails( AutoCompleteUserDetailsRequestVO request, String identifierType);
	public String fetchDomainCat();
	public String fetchDomainCatFrOpt();
	public PreparedStatement getUsersInHierachyWithCatQry(Connection con,String catCode,String userId) throws SQLException;
	public PreparedStatement getChannelUsersListQry(Connection con,String userDomain,String userCategoryCode,String userGeography,String userId,String status, boolean selfAllowed) throws SQLException;
	public PreparedStatement getChannelUsersListTcpQry(Connection con,String userDomain,String userCategoryCode,String userGeography,String userId,String status) throws SQLException;
	public PreparedStatement getPinPassword(Connection con,String login_id) throws SQLException;
	public PreparedStatement loadUserDetailsByLoginId(Connection con,String login_id) throws SQLException;
	public PreparedStatement loadUserDetailsBydentifierType(Connection con, String identifierType, String identifierValue, String pinOrPass) throws SQLException ;
	public String loadTransactionData();
	public String loadUserIncomeC2CandO2CQry();
	public String loadUserTotalIncomeDetailsBetweenRangeC2CAndO2CQry();
	public String loadUserIncomeC2SQry();
	public String loadUserTotalIncomeDetailsBetweenRangeC2SQry();
	public String getLowthreshHoldReportQry(LowThreshHoldReportDTO lowThreshHoldReportDTO);
	public String getPinPassHistReportQry(PinPassHistoryReqDTO pinPassHistoryReqDTO);
	public String getParentOwnerInfo();
	public String getParentOwnerInfoForAllUsers();
	public String loadUserDetailsCompletelyByMsisdnQry();
	public String checkChildUserUnderLoggedInUserQry();
	public String loadOwnerUserListQry(String statusUsed, String status);
	public PreparedStatement getChannelUsersListQry1(Connection con, String userDomain, String userCategoryCode,String userGeography, String userId, String status, boolean selfAllowed) throws SQLException;
	public PreparedStatement getChannelUserListByParentQry1(Connection con, String userDomain, String userCategoryCode,String userGeography, String parentUserID,String userName,String ownerUserID) throws SQLException;
	public PreparedStatement getStaffUserListByParentQry1(Connection con,StaffUserListByParntReqVO requestVO) throws SQLException;
	public PreparedStatement checkChannelUserUnderParent(Connection con,String channelUserLoginID,String parentUserID) throws SQLException;
	public String fetchPagesUIRoles(String pageType, String tabName , boolean groupRole);
	public String fetchPagesUIRolesFixed(String pageType, String tabName , boolean groupRole);
	public String loadUsersDetailsQryFromLoginID();
	public String loadApprovalListbyCreater(ApplistReqVO applistReqVO);
	public PreparedStatement getChannelUsersListQry2(Connection con, String userDomain, String userCategoryCode,
			String userGeography, String userId, String status, boolean selfAllowed,boolean onlyChannelUser) throws SQLException;

	public PreparedStatement getChannelUsersListQryCCE(Connection con, String userDomain, String userCategoryCode,
													   String userGeography, String userId, String status, boolean selfAllowed,boolean onlyChannelUser) throws SQLException;
	public String loadApprovalListbyCreaterAdvance(ApplistReqVO applistReqVO);
	public String loadApprovalListbyCreaterMob(ApplistReqVO applistReqVO);

}
