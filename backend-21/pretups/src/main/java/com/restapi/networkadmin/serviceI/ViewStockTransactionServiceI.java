package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.DisplayStockTxnDetailsViewRequestVO;
import com.restapi.networkadmin.requestVO.LoadStockTxnListViewRequestVO;
import com.restapi.networkadmin.responseVO.DisplayStockTxnDetailsViewResponseVO;
import com.restapi.networkadmin.responseVO.LoadStockTxnListViewResponseVO;
import com.restapi.networkadmin.responseVO.ViewStockTxnDropdownsResponseVO;

public interface ViewStockTransactionServiceI {

	ViewStockTxnDropdownsResponseVO viewStockTxnDropdowns(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			ViewStockTxnDropdownsResponseVO response);

	LoadStockTxnListViewResponseVO loadStockTxnListView(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			LoadStockTxnListViewResponseVO response, LoadStockTxnListViewRequestVO loadStockTxnListViewRequestVO);

	DisplayStockTxnDetailsViewResponseVO displayStockTxnDetailsView(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			DisplayStockTxnDetailsViewResponseVO response,
			DisplayStockTxnDetailsViewRequestVO displayStockTxnDetailsViewRequestVO);

}
