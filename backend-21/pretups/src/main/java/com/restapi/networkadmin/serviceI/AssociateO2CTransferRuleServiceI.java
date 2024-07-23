package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.AddO2CTransferRuleReqVO;
import com.restapi.networkadmin.requestVO.UpdateO2CTransferRuleReqVO;
import com.restapi.networkadmin.responseVO.CategoryDomainListResponseVO;
import com.restapi.networkadmin.responseVO.ToCategoryListResponseVO;
import com.restapi.networkadmin.responseVO.TransferRulesListResponseVO;
import com.restapi.networkadminVO.AddO2CTransferRuleVO;


public interface AssociateO2CTransferRuleServiceI {

	CategoryDomainListResponseVO loadDomainListForOperator(Connection con, Locale locale, HttpServletResponse response1, UserVO userVO,
			CategoryDomainListResponseVO response);
	
	TransferRulesListResponseVO loadTransferRuleslist(Connection con, Locale locale, HttpServletResponse response1, UserVO userVO,
			TransferRulesListResponseVO response, String userCategory, String domainCode, String type);
	
	ToCategoryListResponseVO loadToCategoryList(Connection con, Locale locale, HttpServletResponse response1, UserVO userVO,
			ToCategoryListResponseVO response, String userCategory, String domainCode, String type);

	BaseResponse addO2CTransferRule(Connection con, MComConnectionI mcomCon, Locale locale, HttpServletResponse response1,
			UserVO userVO, BaseResponse response, AddO2CTransferRuleReqVO request,
			AddO2CTransferRuleVO addO2CTransferRuleVO) throws Exception;
	
	BaseResponse updateO2CTransferRule(Connection con, MComConnectionI mcomCon, Locale locale, HttpServletResponse response1,
			UserVO userVO, BaseResponse response, UpdateO2CTransferRuleReqVO request,
			AddO2CTransferRuleVO addO2CTransferRuleVO) throws Exception;
	
	BaseResponse deleteO2CTransferRule(Connection con, MComConnectionI mcomCon, Locale locale, HttpServletResponse response1,
			UserVO userVO, BaseResponse response, UpdateO2CTransferRuleReqVO request,
			AddO2CTransferRuleVO addO2CTransferRuleVO) throws Exception;
}
