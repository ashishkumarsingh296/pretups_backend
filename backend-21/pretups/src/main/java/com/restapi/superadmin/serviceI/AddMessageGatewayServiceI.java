package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.superadmin.responseVO.MessageGatewayDetailResponseVO;
import com.restapi.superadminVO.AddMessGatewayVO;
import com.restapi.superadminVO.GatewayListResponseVO;

public interface AddMessageGatewayServiceI {

	GatewayListResponseVO loadGatewaysList(Connection con, Locale locale, HttpServletResponse response1);

	BaseResponse addMessGateway(Connection con, MComConnectionI mcomCon, Locale locale,HttpServletRequest request, HttpServletResponse responseSwag, AddMessGatewayVO addMessGatewayVO, BaseResponse response, ChannelUserVO userVO);

	MessageGatewayDetailResponseVO displayMessageGatewayDetail(Connection con, MComConnectionI mcomCon, Locale locale,
			HttpServletRequest request, HttpServletResponse responseSwag, AddMessGatewayVO addMessGatewayVO,
			MessageGatewayDetailResponseVO response, ChannelUserVO userVO);

	BaseResponse updateMessGateway(Connection con, MComConnectionI mcomCon, Locale locale, HttpServletRequest request,
			HttpServletResponse responseSwag, AddMessGatewayVO addMessGatewayVO, BaseResponse response,
			ChannelUserVO userVO);

	/**
	 * 
	 * @param con
	 * @param locale
	 * @param response1
	 * @param gatewayCode
	 * @return
	 */
	GatewayListResponseVO loadClassHandlerList(Connection con, Locale locale, HttpServletResponse response1,
			String gatewayCode)throws BTSLBaseException;

	

}
