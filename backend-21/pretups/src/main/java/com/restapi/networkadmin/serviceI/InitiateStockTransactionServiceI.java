package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.AddStockRequestVO;
import com.restapi.networkadmin.requestVO.ConfirmStockRequestVO;
import com.restapi.networkadmin.responseVO.AddStockFinalResponseVO;
import com.restapi.networkadmin.responseVO.ConfirmStockResponseVO;
import com.restapi.networkadmin.responseVO.InitiateStockTransactionResponseVO;

public interface InitiateStockTransactionServiceI {

	 InitiateStockTransactionResponseVO initiateStock(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con,  Locale locale,UserVO userVO, InitiateStockTransactionResponseVO response, String walletType) throws Exception;

	ConfirmStockResponseVO confirmStock(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, Locale locale, UserVO userVO, ConfirmStockResponseVO response,
			ConfirmStockRequestVO confirmStockRequestVO);

	AddStockFinalResponseVO addStockFinal(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con,MComConnectionI mcomCon,
			Locale locale, UserVO userVO, AddStockFinalResponseVO response, AddStockRequestVO addStockRequestVO); 

}
