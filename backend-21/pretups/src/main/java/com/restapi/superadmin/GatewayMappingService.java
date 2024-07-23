package com.restapi.superadmin;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;

@Service
public interface GatewayMappingService {

	/**
	 * 
	 * @param con
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public GatewayMappingResponseVO viewGatewayList(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public BaseResponse ModifyGatewayAdmin(Connection con,UserVO userVO, GatewayMappingRequestVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public BaseResponse deleteGatewayAdmin(Connection con,UserVO userVO, GatewayMappingRequestVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public MessageGatewayResponseVO viewMessageGatewayList(Connection con, String loginID,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param gatewayCode
	 * @param loginID
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public BaseResponse deleteMessageGatweway(Connection con,UserVO userVO, String gatewayCode,String loginID, HttpServletResponse response1)
			throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param gatewayCode
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	
	public MessageModifyResponseVO ModifyMessageGatweway(Connection con, String gatewayCode,HttpServletResponse response1)
			throws BTSLBaseException, SQLException;

}
