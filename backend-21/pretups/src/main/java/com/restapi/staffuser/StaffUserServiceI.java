package com.restapi.staffuser;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.requesthandler.FetchUserDetailsResponseVO;
import com.btsl.user.businesslogic.UserVO;

@Service
public interface StaffUserServiceI {

	/**
	 * 
	 * @param loginId
	 * @param con
	 * @param response 
	 * @param responseSwag
	 * @return 
	 */
	public ServiceListResponse getServiceList(String loginId, Connection con, ServiceListResponse response, HttpServletResponse responseSwag);

	/**
	 * 
	 * @param loginId
	 * @param con
	 * @param requestVO 
	 * @param responseSwag
	 * @return response
	 */
	public BaseResponse addStaffUserDetails(String loginId, Connection con, StaffUserRequestVO requestVO, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param loginId
	 * @param con
	 * @param requestVO 
	 * @param responseSwag
	 * @return response
	 */
	public BaseResponse editStaffUserDetails(String loginId, Connection con, StaffUserEditRequestVO requestVO, HttpServletResponse responseSwag);
	


	/**
	 * 
	 * @param loginId
	 * @param con
	 * @param response
	 * @param responseSwag
	 * @return
	 */
	public FetchStaffUserResponse getRoleList(String loginId, Connection con, FetchStaffUserResponse response, HttpServletResponse responseSwag);

	/**
	 * 
	 * @param p_con
	 * @param p_id
	 * @param p_response
	 * @param p_sessionUserVO
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public void processStaffUserDetailsDownload(Connection p_con , String p_id , FetchUserDetailsResponseVO p_response , UserVO p_sessionUserVO) throws BTSLBaseException , Exception;

}	
