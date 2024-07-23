package com.restapi.o2c.service;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;

@Service
public interface O2CBatchServiceI {

	public O2CBatchTransferResponse processRequest(O2CBatchTransferRequestVO requestVO, String serviceKeyword, OperatorUtilI calculatorI ,String requestIDStr,Connection con , Locale locale,HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)  throws BTSLBaseException;
	
}
