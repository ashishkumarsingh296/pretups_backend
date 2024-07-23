package com.restapi.channelenquiry.service;

import java.sql.Connection;
import java.text.ParseException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.UserVO;

@Service
public interface ChannelEnquiryService {

	/**
	 * @author sarthak.saini
	 * @param con
	 * @param userVO
	 * @param c2sEnquiryRequestVO
	 * @param response
	 * @param response1
	 * @param locale
	 */
	public void loadC2STransferEnquiryList(Connection con,UserVO userVO,C2SEnquiryRequestVO c2sEnquiryRequestVO, C2SEnquiryResponseVO response, HttpServletResponse response1,Locale locale);
	
	/**
	 * 
	 * @param c2cBulkApprovalRequestVO
	 * @param response
	 * @param responseSwag
	 * @param oAuthUserData
	 * @param locale
	 * @param enquiryType
	 * @param searchBy
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public void processChannelEnquiryO2c(C2cAndO2cEnquiryRequestVO c2cBulkApprovalRequestVO,
			C2cAndO2cEnquiryResponseVO response, HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String enquiryType, String searchBy)
			throws BTSLBaseException, Exception;
	
	/**
	 * 
	 * @param c2cBulkApprovalRequestVO
	 * @param response
	 * @param responseSwag
	 * @param oAuthUserData
	 * @param locale
	 * @param enquiryType
	 * @param searchBy
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public void processChannelEnquiryC2c(C2cAndO2cEnquiryRequestVO c2cBulkApprovalRequestVO,
			C2cAndO2cEnquiryResponseVO response, HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String enquiryType, String searchBy)
			throws BTSLBaseException, Exception;


	public void loadClosingBalanceData(Connection con, UserVO sessionUser, ClosingBalanceEnquiryRequestVO requestVO,
			ClosingBalanceEnquiryResponseVO response, HttpServletResponse response1, Locale locale)
			throws BTSLBaseException;
    
	public void getAlertCounterSummaryData(Connection con, UserVO userVO, AlertCounterSummaryRequestVO requestVO,
			AlertCounterSummaryResponseVO response, HttpServletResponse response1, Locale locale) throws BTSLBaseException, ParseException;
  
	/**
	 * 
	 * @param batchC2cTransferRequestVO
	 * @param response
	 * @param responseSwag
	 * @param oAuthUserData
	 * @param locale
	 * @param searchBy
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	
	public void getBatchC2cTransferdetails(BatchC2cTransferRequestVO batchC2cTransferRequestVO,
			BatchC2cTransferResponseVO response, HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String searchBy)
			throws BTSLBaseException, Exception;

}
