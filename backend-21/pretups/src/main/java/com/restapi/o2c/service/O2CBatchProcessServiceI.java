package com.restapi.o2c.service;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.util.OperatorUtilI;

@Service
public interface O2CBatchProcessServiceI {
	/**
	 * 
	 * @param serviceType
	 * @param requestVO
	 * @param msisdn
	 * @param calculatorI
	 * @param locale
	 * @param con
	 * @param requestType
	 * @param batchID
	 * @param serviceKeyword
	 * @param requestIDStr
	 * @param httprequest
	 * @param headers
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 */
	public O2CBatchWithdrawFileResponse processRequest(String serviceType,O2CBatchWithdrawFileRequest requestVO,String msisdn,OperatorUtilI calculatorI,Locale locale,Connection con,String requestType,String batchID,String serviceKeyword, String requestIDStr, HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)  throws BTSLBaseException;

	/**
	 * 
	 * @param bulkComProcessApiRequest
	 * @param msisdn
	 * @param calculatorI
	 * @param locale
	 * @param con
	 * @param batchID
	 * @param requestIDStr
	 * @param httprequest
	 * @param headers
	 * @param response1
	 * @return
	 */
	public O2CBatchWithdrawFileResponse processBulkComBatchProcessRequest(
			O2CBatchWithdrawFileRequest bulkComProcessApiRequest, String msisdn, OperatorUtilI calculatorI, Locale locale,
			Connection con,String requestType, String batchID, String requestIDStr,
			HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse response1);

	
}
