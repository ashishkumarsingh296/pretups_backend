package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.UpdateNetworkPreferenceReqVO;
import com.restapi.networkadmin.requestVO.UpdateServiceClassPreferenceReqVO;
import com.restapi.networkadmin.responseVO.ServiceClassListResponseVO;
import com.restapi.networkadmin.responseVO.ServiceClassPreferenceListResponseVO;

public interface ServiceClassPreferenceServiceI {

	ServiceClassListResponseVO loadServiceClassList(Connection con, Locale locale, HttpServletResponse response1, UserVO userVO,
			ServiceClassListResponseVO response);
	
	ServiceClassPreferenceListResponseVO loadServiceClassPreferenceList(Connection con, Locale locale, HttpServletResponse response1, UserVO userVO,
			ServiceClassPreferenceListResponseVO response, String serviceCode);
	
	BaseResponse updateServiceClassPreferenceByList(Connection con,MComConnectionI mcomCon, Locale locale,
			HttpServletResponse response1,UserVO userVO,BaseResponse  response,UpdateServiceClassPreferenceReqVO requestVO) throws Exception;

}
