package com.restapi.channelAdmin;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;

@Service
public interface BulkAutoC2CSOSCreditLimitServiceI {

	/**
	 * 
	 * @param con
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BulkAutoC2CSOSCreditLimitResponseVO downloadTemplate(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param responseSwag
	 * @param loginID
	 * @param domain
	 * @param category
	 * @param geoDomain
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BulkAutoC2CSOSCreditLimitResponseVO downloadUserList(Connection con, HttpServletResponse responseSwag,
			String loginID, String domain, String category, String geoDomain) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param response1
	 * @param loginID
	 * @param domain
	 * @param category
	 * @param geoDomain
	 * @param request
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BulkAutoC2CSOSCreditLimitFileResponseVO processFile(Connection con, HttpServletResponse response1,
			String loginID, String domain, String category, String geoDomain,
			BulkAutoC2CSOSCreditLimitFileRequestVO request) throws BTSLBaseException, SQLException;

}
