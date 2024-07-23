package com.restapi.channelAdmin;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;

@Service
public interface VoucherPinResendService {

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param response1
	 * @param requestVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public VoucherPinResendResponseVO viewVoucherList(Connection con,String loginID, HttpServletResponse response1,VoucherPinResendRequestVO requestVO)throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param response1
	 * @param requestVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public VoucherPinResetDetailResponseVO viewVoucherDetailList(Connection con,String loginID, HttpServletResponse response1,VoucherPinResendRequestVO requestVO)throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param response1
	 * @param request
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse sendPin(Connection con, String loginID, HttpServletResponse response1,
			VoucherPinResendRequestVO request)throws BTSLBaseException, SQLException;
}
