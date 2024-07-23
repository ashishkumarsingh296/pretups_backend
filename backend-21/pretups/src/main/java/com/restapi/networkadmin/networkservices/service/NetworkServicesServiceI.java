package com.restapi.networkadmin.networkservices.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.networkservices.requestVO.UpdateNetworkServicesRequestVO;
import com.restapi.networkadmin.networkservices.responseVO.NetworkServicesDataResponseVO;
import com.restapi.networkadmin.networkservices.responseVO.ServiceTypeListResponseVO;

public interface NetworkServicesServiceI {

	ServiceTypeListResponseVO loadServiceTypeList(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceTypeListResponseVO response) throws BTSLBaseException;

	NetworkServicesDataResponseVO loadNetworkServicesData(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			NetworkServicesDataResponseVO response, String moduleCode, String serviceTypeCode) throws BTSLBaseException;

	BaseResponse updateNetworkServices(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			UpdateNetworkServicesRequestVO updateNetworkServicesRequestVO) throws BTSLBaseException,SQLException;

}
