package com.restapi.c2s.services;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CategoryRespVO;
import com.btsl.common.CategoryResponseVO;
import com.btsl.pretups.domain.businesslogic.CategoryDomainCodeVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.c2sservices.controller.RescheduleBatchRechargeRequestVO;
import com.restapi.c2sservices.controller.RescheduleBatchRechargeResponseVO;
import com.restapi.c2sservices.controller.ViewScheduleDetailsBatchResponseVO;
import com.restapi.c2sservices.controller.ViewScheduleDetailsListResponseVO;


@Service
public interface C2SBulkRcServiceI {


	public ViewC2SBulkRechargeDetailsResponseVO processViewRequest(String batchId,MultiValueMap<String, String> headers, HttpServletResponse responseSwag,String msisdn)  throws BTSLBaseException;
	
	public C2SBulkRechargeResponseVO processRequest(C2SBulkRechargeRequestVO requestVO, String serviceKeyword, String requestIDStr, String requestFor, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)  throws BTSLBaseException;
	public  DvdBulkResponse processRequestBulkDVD(C2SBulkRechargeRequestVO requestVO, String requestIDStr ,MultiValueMap<String, 
			String> headers,HttpServletResponse responseSwag, HttpServletRequest httpServletRequest, String serviceKeyword)  throws BTSLBaseException;
	public  DvdBulkResponse processCancelBatch(CancelBatchC2SRequestVO requestVO, String requestIDStr ,MultiValueMap<String, 
			String> headers,HttpServletResponse responseSwag, HttpServletRequest httpServletRequest, String serviceKeyword)  throws Exception;
	public  CancelSingleMsisdnBatchResponseVO processSingleMsisdnCancelBatch(CancelSingleMsisdnBatchC2SRequestVO requestVO, String requestIDStr ,MultiValueMap<String, 
			String> headers,HttpServletResponse responseSwag, HttpServletRequest httpServletRequest, String serviceKeyword)  throws Exception;
	public  CategoryDomainCodeVO getDomainCode(CategoryVO requestVO,Connection con, String domainCode,HttpServletResponse response1) throws BTSLBaseException;
	
	/**
	 * 
	 * @param rescheduleBatchRechargeRequestVO
	 * @param oAuthUserData
	 * @return
	 */
	public RescheduleBatchRechargeResponseVO processRescheduleFile(RescheduleBatchRechargeRequestVO rescheduleBatchRechargeRequestVO,OAuthUser oAuthUserData,HttpServletRequest httpServletRequest,HttpServletResponse responseSwag);
	
	
	public ViewScheduleDetailsBatchResponseVO processViewBatchScheduleDetails(Connection con, String sessionUserLoginId,
			HttpServletResponse response1, String loginId, String scheduleStatus, String serviceType, String dateRange)
			throws BTSLBaseException;
	/**
	 * 
	 * @param con
	 * @param sessionUserVO
	 * @param loginId
	 * @param msisdn
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public ViewScheduleDetailsListResponseVO processViewScheduleDetails(Connection con ,UserVO sessionUserVO , String loginId, String msisdn) throws BTSLBaseException , Exception;

	/**
	 * 
	 * @param userVO
	 * @param responseVO
	 * @param con
	 * @throws BTSLBaseException
	 */
	public void getCategoryList(UserVO userVO,CategoryResponseVO responseVO, Connection con) throws BTSLBaseException;

	public ViewScheduleDetailsListResponseVO processScheduleReportRequest(Connection con, UserVO sessionUserVO,
			String loginId, String msisdn, String dateRange, String staffFlag) throws BTSLBaseException, Exception;
	
	

	
	/**
	 * 
	 * @author harshita.bajaj
	 * @param con
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 */
    
	public ServiceListResponseVO servicesList(String loginId,Connection con,HttpServletResponse responseSwag) throws BTSLBaseException;
	
	
	
	public void getCategoryListWithoutTransferRules(UserVO userVO, CategoryRespVO responseVO, Connection con,String domainCode,String categoryCode) throws BTSLBaseException;
	
	
	  
	public C2SBulkEvdRechargeResponseVO processRequestBulkEVD(C2SBulkEvdRechargeRequestVO requestVO, String serviceKeyword, String requestIDStr, String requestFor, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)  throws BTSLBaseException;
	
	
	public ReconcileServiceListResponseVO getReconservicesList(String loginId,Connection con,HttpServletResponse responseSwag) throws BTSLBaseException;

}
