package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.ApprovaLevelTwoStockTxnRequestVO;
import com.restapi.networkadmin.responseVO.ApprovaStockTxnResponseVO;
import com.restapi.networkadmin.responseVO.DisplayStockLevelTwoResponseVO;
import com.restapi.networkadmin.responseVO.LevelTwoApprovalListResponseVO;
import com.restapi.networkadminVO.DisplayStockVO;

public interface LevelTwoApprovalServiceI {

	LevelTwoApprovalListResponseVO levelTwoApprovalList(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			LevelTwoApprovalListResponseVO response);

	DisplayStockLevelTwoResponseVO displayStockLevelTwo(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			DisplayStockLevelTwoResponseVO response, DisplayStockVO displayStockVO, String txnNo);

	ApprovaStockTxnResponseVO approvaLevelTwoStockTxn(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ApprovaStockTxnResponseVO response,
			ApprovaLevelTwoStockTxnRequestVO approvaLevelTwoStockTxnRequestVO);

}
