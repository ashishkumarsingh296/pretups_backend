package com.restapi.o2c.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.util.OperatorUtilI;

@Service
public interface O2CBatchApproveRejectServiceI {

	/**
	 * 
	 * @param bulkO2CApprovalRequestVO
	 * @param msisdn
	 * @param locale
	 * @param response
	 * @param responseSwag
	 * @return 
	 * @throws BTSLBaseException
	 */
	public O2CBatchApRejTransferResponse processO2CApproveOrReject(O2CBulkApprovalOrRejectRequestVO o2CBulkApprovalOrRejectRequestVO,String msisdn,OperatorUtilI calculator,Locale locale,Connection con, String serviceType,
			String requestIDStr, HttpServletRequest httprequest,
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException;
	

	/**
	 * 
	 * @param commisionBulkApprovalOrRejectRequestVO
	 * @param msisdn
	 * @param locale
	 * @param con
	 * @param httprequest
	 * @param headers
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 */
	public O2CBatchApRejTransferResponse processBulkCommApproveOrReject(CommisionBulkApprovalOrRejectRequestVO commisionBulkApprovalOrRejectRequestVO
			,String msisdn,Locale locale,Connection con, HttpServletRequest httprequest,
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

}
