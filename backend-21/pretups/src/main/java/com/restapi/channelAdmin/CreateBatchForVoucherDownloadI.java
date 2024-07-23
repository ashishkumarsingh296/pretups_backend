package com.restapi.channelAdmin;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;

@Service
public interface CreateBatchForVoucherDownloadI {

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param voucherType
	 * @param voucherSegment
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public DenominationResponse getMrpList(Connection con, String loginId, String voucherType, String voucherSegment,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param denomination
	 * @param voucherType
	 * @param voucherSegment
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BatchListResponseVO getBatchIdDetails(Connection con, String loginId, String denomination,
			String voucherType, String voucherSegment, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param fromSerialNo
	 * @param toSerialNo
	 * @param voucherType
	 * @param downloadType
	 * @param productId
	 * @param Quantity
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse createBatchIdbyAdmin(Connection con, String loginId, String fromSerialNo, String toSerialNo,
			String voucherType, String downloadType, String productId, String Quantity,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

}
