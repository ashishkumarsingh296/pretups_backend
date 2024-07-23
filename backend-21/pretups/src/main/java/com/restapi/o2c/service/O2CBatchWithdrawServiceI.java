package com.restapi.o2c.service;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.util.OperatorUtilI;

@Service
public interface O2CBatchWithdrawServiceI {
	public O2CBatchWithdrawFileResponse processRequest(O2CBatchWithdrawFileRequest requestVO,String msisdn,OperatorUtilI calculatorI,Locale locale,Connection con,String geoDomain,String channelDomain,String userCategoryName,String product,String walletType, String serviceKeyword, String requestIDStr, HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)  throws BTSLBaseException;


}
