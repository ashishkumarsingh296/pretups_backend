package com.restapi.channelAdmin.service;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;

@Service
public interface ChangeUserStatusByAdminService {

/**
 * 
 * @param con
 * @param loginId
 * @param msisdn
 * @param responseSwag
 * @param status
 * @param remarks
 * @return
 * @throws BTSLBaseException
 * @throws SQLException
 */

	public BaseResponse changeUserStatusAdmin(Connection con,String loginId, String msisdn, HttpServletResponse responseSwag,
			String status ,String remarks) throws BTSLBaseException, SQLException;

}
