package com.restapi.superadmin;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;

@Service
public interface DeleteOperatorUserI {
	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	
	public BaseResponse deleteOperatorUser(Connection con, String loginId, DeleteOperatorRequestVO requestVO, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException;

}
