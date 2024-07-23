package com.restapi.o2c.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.restapi.o2c.service.bulko2capprovalrequestvo.BulkO2CApprovalRequestVO;

@Service
public interface O2CBatchApprovalServiceI {

	/**
	 * 
	 * @param bulkO2CApprovalRequestVO
	 * @param msisdn
	 * @param locale
	 * @param response
	 * @param responseSwag
	 * @param con 
	 * @throws BTSLBaseException
	 */
	public void getBulkO2CApprovalList(BulkO2CApprovalRequestVO bulkO2CApprovalRequestVO, String msisdn, Locale locale,
			O2CApprovalListVO response, HttpServletResponse responseSwag, Connection con) throws BTSLBaseException;
	
	/**
	 * 
	 * @param batchapprovalDetailsRequest
	 * @param msisdn
	 * @param locale
	 * @param response
	 * @param response1
	 * @throws BTSLBaseException 
	 * @throws SQLException 
	 */
	public O2CBatchApprovalDetailsResponse processO2CBatchApprovalDetails(Connection con,O2CBatchApprovalDetailsRequestVO batchapprovalDetailsRequest,
			String msisdn, Locale locale, O2CBatchApprovalDetailsResponse response, HttpServletResponse response1,HttpServletRequest httprequest) throws BTSLBaseException, SQLException;
	
}
