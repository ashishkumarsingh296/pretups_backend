package com.restapi.networkadmin;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.restapi.networkadmin.requestVO.ChannelTransferDeleteRequestVO;

@Service
public interface C2STransferRuleService {

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param domainCode
	 * @param categoryCode
	 * @param gradeCode
	 * @param statusCode
	 * @param gatewayCode
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	
	public C2STransferRuleResponseVO viewC2SList(Connection con, String loginID, String domainCode, String categoryCode,
			String gradeCode, String statusCode, String gatewayCode, HttpServletResponse response1) throws BTSLBaseException, SQLException;
	
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	
	public C2STransferRuleResponseVO1 viewC2SDropdownList(Connection con, String loginID,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public C2STransferListAddResponseVO addTransfer(Connection con, String loginID, C2STransferRuleRequestVO requestVO,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse modifyTransfer(Connection con, String loginID, ChannelTransferModifyRequestVO requestVO,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse deleteTransfer(Connection con, String loginID, ChannelTransferDeleteRequestVO requestVO,
			HttpServletResponse response1)throws BTSLBaseException, SQLException;
}
