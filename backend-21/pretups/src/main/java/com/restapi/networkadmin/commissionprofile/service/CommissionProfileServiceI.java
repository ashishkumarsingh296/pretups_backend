package com.restapi.networkadmin.commissionprofile.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.commissionprofile.requestVO.LoadVersionListBasedOnDateRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.SusResCommProfileSetRequestVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileGatewayListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileMainResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileProductListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileSubServiceListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileViewListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileViewResponseVO;
import com.restapi.commissionProfileMainResponseVO.LoadVersionListBasedOnDateResponseVO;
import com.restapi.networkadmin.commissionprofile.requestVO.AddCommissionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.ModifyCommissionProfileRequestVO;


public interface CommissionProfileServiceI {
	
	BaseResponse addCommissionProfile(MultiValueMap<String, String> headers,HttpServletRequest httpServletRequest, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			AddCommissionProfileRequestVO addCommissionProfileRequestVO)  throws Exception;
	
	
	
	BaseResponse modifyCommissionProfile(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			BaseResponse response, ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO) throws Exception;

	
	BaseResponse deleteCommissionProfileSet(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con,MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response, String commProfileSetID, String commProfileName);

	BaseResponse suspendResumeCommissionProfileSet(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			SusResCommProfileSetRequestVO susResCommProfileSetRequestVO);

	LoadVersionListBasedOnDateResponseVO loadVersionListBasedOnDate(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			LoadVersionListBasedOnDateResponseVO response,
			LoadVersionListBasedOnDateRequestVO loadVersionListBasedOnDateRequestVO);
	
	
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public CommissionProfileMainResponseVO viewGeoGradeList(Connection con, String loginID, String categoryCode,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param categoryCode
	 * @param gradeCode
	 * @param geoCode
	 * @param status
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public CommissionProfileViewListResponseVO viewList(Connection con, String loginID, String categoryCode,
			String gradeCode, String geoCode, String status, HttpServletResponse response1)
			throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public CommissionProfileProductListResponseVO viewProductList(Connection con, String loginID,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param categoryCode
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public CommissionProfileGatewayListResponseVO viewGatewayList(Connection con, String loginID, String categoryCode,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param serviceCode
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public CommissionProfileSubServiceListResponseVO viewSubServiceList(Connection con, String loginID,
			String serviceCode, HttpServletResponse response1) throws BTSLBaseException, SQLException;
	
	
	
	BaseResponse chnageStatusForCommissionProfile(String categorCode,String loginUserID,HttpServletResponse response1,
			Connection con,MComConnectionI mcomCon, Locale locale, BaseResponse response, com.restapi.networkadmin.commissionprofile.requestVO.ChangeStatusForCommissionProfileRequestVO requestVO) throws BTSLBaseException, Exception;

	
	BaseResponse makeDefaultCommissionProfile(String loginUserID,HttpServletResponse response1,
			Connection con,MComConnectionI mcomCon, Locale locale, String commissionProfileSetId,String categoryCode, String networkCode, String commissionProfileName) throws BTSLBaseException, Exception;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param response1
	 * @param domainCode
	 * @param gradeCode
	 * @param commProfileSetId
	 * @param grphDomainCode
	 * @param networkCode
	 * @param commProfileSetVersionId
	 * @param categoryCode
	 * @param commissionType
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public CommissionProfileViewResponseVO viewCommissionProfileDetails(Connection con,  Locale locale,String loginID,String domainCode, String commissionType,String categoryCode,
			String commProfileSetId,String gradeCode,String grphDomainCode,String networkCode,String commProfileSetVersionId,HttpServletResponse response1) throws BTSLBaseException,Exception ;
	
}
