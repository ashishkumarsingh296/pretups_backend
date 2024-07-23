package com.restapi.channelAdmin.service;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.restapi.channelAdmin.requestVO.AutoO2CRequestVO;
import com.restapi.channelAdmin.responseVO.AutoO2CUpdateResponseVO;


public interface AutoO2CCreditLimitServiceI {
	
	public AutoO2CUpdateResponseVO updateAutoO2CCreditLimit(Connection con,
			   HttpServletResponse response1, AutoO2CRequestVO request, String loginId,Locale locale) throws BTSLBaseException, Exception;

}
