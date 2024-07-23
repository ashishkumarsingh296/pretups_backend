package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.ApprovaLevelOneStockTxnRequestVO;
import com.restapi.networkadmin.requestVO.ConfirmStockLevelOneRequestVO;
import com.restapi.networkadmin.requestVO.RejectStockTxnRequestVO;
import com.restapi.networkadmin.responseVO.ApprovaStockTxnResponseVO;
import com.restapi.networkadmin.responseVO.ConfirmStockLevelOneResponseVO;
import com.restapi.networkadmin.responseVO.DisplayStockLevelOneResponseVO;
import com.restapi.networkadmin.responseVO.LevelOneApprovalListResponseVO;
import com.restapi.networkadmin.responseVO.RejectStockTxnResponseVO;
import com.restapi.networkadminVO.DisplayStockVO;

public interface LevelOneApprovalServiceI {

	LevelOneApprovalListResponseVO levelOneApprovalList(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			LevelOneApprovalListResponseVO response);

	DisplayStockLevelOneResponseVO displayStockLevelOne(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			DisplayStockLevelOneResponseVO response, DisplayStockVO displayStockVO,String txnNo);

	ConfirmStockLevelOneResponseVO confirmStockLevelOne(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			ConfirmStockLevelOneResponseVO response, ConfirmStockLevelOneRequestVO confirmStockLevelOneRequestVO);

	ApprovaStockTxnResponseVO approvaLevelOneStockTxn(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ApprovaStockTxnResponseVO response,
			ApprovaLevelOneStockTxnRequestVO approvaLevelOneStockTxnRequestVO);

	RejectStockTxnResponseVO rejectStockTxn(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, RejectStockTxnResponseVO response,
			RejectStockTxnRequestVO rejectStockTxnRequestVO);
	
	

}
