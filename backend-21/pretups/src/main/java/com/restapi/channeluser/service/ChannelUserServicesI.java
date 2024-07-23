package com.restapi.channeluser.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchAdminRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.SAPResponseVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUserData;
import com.restapi.users.logiid.LoginIdResponseVO;


public interface ChannelUserServicesI {

	
	/**
	 * 
	 * @param loginId
	 * @param con
	 * @param requestVO 
	 * @param responseSwag
	 * @return response
	 */
	public AreaSearchResponseVO searchArea(String loginId, Connection con, AreaSearchRequestVO requestVO, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param loginId
	 * @param con
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 */
	public AreaSearchResponseVO searchAreaAdmin(String loginId, Connection con, AreaSearchAdminRequestVO requestVO, HttpServletResponse responseSwag) throws BTSLBaseException , Exception;
	
	/**
	 * 
	 * @param network
	 * @param extCode
	 * @param responseSwag
	 * @return
	 */
	public SAPResponseVO fetchUserData(String network, String extCode, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param con
	 * @param userId
	 * @return
	 * @throws BTSLBaseException
	 */
	public LoginIdResponseVO getLoginID(Connection con, String userId) throws BTSLBaseException;
	
	/**
	 * 
	 * @param loginId
	 * @param requestVO 
	 * @param response1 
	 * @return response
	 */
	public BatchUserInitiateResponseVO batchUserInitiateProcess(String loginId, BatchUserInitiateRequestVO requestVO, HttpServletResponse response1);
	/**
	 * 
	 * @param con
	 * @param response
	 * @param responseSwag
	 * @param userDomain
	 * @param parentCategory
	 * @param userCategory
	 * @param geography
	 * @param status
	 * @param loginId
	 * @param msisdn
	 * @param channelUserVO
	 * @return
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	public UserHierarchyResponseVO fetchUserHierarchy(Connection con, UserHierarchyResponseVO response,
			HttpServletResponse responseSwag, String userDomain, String parentCategory, String userCategory,
			String geography, String status, String loginId, String msisdn,ChannelUserVO channelUserVO)throws SQLException, BTSLBaseException;

	
	public  List<ChannelUserVO>  fetchChannelUsersByStatusForSRAndDelReq(Connection con,ChannelUserSearchReqVo requestVo)throws SQLException, BTSLBaseException;

    public boolean approvalOrRejectSuspendUser(Connection con,ActionOnUserReqVo actionReqVo,OAuthUserData oauthUserData)throws SQLException, BTSLBaseException;

    public boolean approvalOrRejectDeleteUser(Connection con,ActionOnUserReqVo actionReqVo,OAuthUserData oauthUserData)throws SQLException, BTSLBaseException, Exception;

	AreaSearchResponseVO searchRegion(String loginId, Connection con, String geoDomainCode, HttpServletResponse response1);

}
