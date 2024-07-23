package com.restapi.o2c.service;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public interface O2CServiceI {
	
	public void processWithdrawRequest(ChannelUserVO loginUserVO, O2CWithdrawlRequestVO o2CWithdrawlRequestVO,
		BaseResponseMultiple<JsonNode> apiResponse, HttpServletResponse response1)  throws BTSLBaseException;
	
	public void processVoucherApprvRequest(ChannelUserVO loginUserVO, O2CVoucherApprovalRequestVO o2CVoucherApprovalRequestVO,
			BaseResponseMultiple<JsonNode> apiResponse, HttpServletResponse response1)  throws BTSLBaseException;

	public void processO2CProductDownlaod(Connection p_con , ChannelUserVO p_userVO , O2CProductsResponseVO p_response) throws BTSLBaseException;

}
