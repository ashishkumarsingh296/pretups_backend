package com.restapi.superadmin.domainmanagement.service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BaseResponse;
import com.restapi.superadmin.domainmanagement.requestVO.AddAgentCategoryRequestVO;
import com.restapi.superadmin.domainmanagement.requestVO.AddDomainDetailRequestVO;
import com.restapi.superadmin.domainmanagement.responseVO.AddDomainDetailResponseVO;
import com.restapi.superadmin.domainmanagement.responseVO.DomainManagementListResponseVO;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.domainmanagement.responseVO.DomainmanagementRolesResponseVO;
public interface DomainManagementServiceI {
	DomainmanagementRolesResponseVO getRolesForDomainManagement(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale,String domainCodeType) throws Exception,BTSLBaseException;

	DomainManagementListResponseVO getList(MultiValueMap<String, String> headers,
										   HttpServletResponse response1, Connection con, MComConnectionI mcomCon) throws Exception, BTSLBaseException;

	BaseResponse deleteDomain(MultiValueMap<String, String> headers,
							  HttpServletResponse response1, Connection con, MComConnectionI mcomCon,String domainCode,String loginId) throws BTSLBaseException , SQLException;
	
	BaseResponse validateChannelCategory(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale,String domainCodeType,String domainCodeForDomain,String domainNaim,String numberOfCategories) throws Exception,BTSLBaseException;

	AddDomainDetailResponseVO addCategory(MultiValueMap<String, String> headers,
			Connection con, MComConnectionI mcomCon, Locale locale,AddDomainDetailRequestVO addDomainDetailRequestVO,UserVO userVO) throws Exception,BTSLBaseException;
	
	AddDomainDetailResponseVO addAgent(MultiValueMap<String, String> headers,
			Connection con, MComConnectionI mcomCon, Locale locale,AddAgentCategoryRequestVO addDomainDetailRequestVO,UserVO userVO) throws Exception,BTSLBaseException;

}
