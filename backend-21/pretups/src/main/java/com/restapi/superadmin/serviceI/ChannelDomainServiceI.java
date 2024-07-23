package com.restapi.superadmin.serviceI;

import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BaseResponse;
import com.restapi.superadmin.ChannelDomainListResponseVO;
import com.restapi.superadmin.requestVO.DeleteDomainRequestVO;
import com.restapi.superadmin.requestVO.SaveDomainRequestVO;
import com.restapi.superadmin.requestVO.UpdateDomainRequestVO;

public interface ChannelDomainServiceI {

	ChannelDomainListResponseVO getChannelDomainList(HttpServletResponse responseSwag,
			Locale locale) throws SQLException;
	
	BaseResponse updateChannelDomain(UpdateDomainRequestVO request,String loginId,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;
	
	BaseResponse deleteChannelDomain(DeleteDomainRequestVO request, String loginId,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;
	
	BaseResponse saveChannelDomain(SaveDomainRequestVO request,String loginId,HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;
	
	BaseResponse updateStatusOfDomain(DeleteDomainRequestVO request, String loginId,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;
	
	BaseResponse saveAgentChannelDomain(SaveDomainRequestVO request,String loginId,HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;
}
