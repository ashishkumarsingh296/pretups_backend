package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import com.restapi.networkadmin.requestVO.UpdateNetworkPreferenceReqVO;
//import com.restapi.networkadmin.requestVO.UpdateNetworkPreferenceReqVO.PreferenceDetails;
import com.restapi.networkadmin.responseVO.NetworkPreferenceListResponseVO;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;

public interface NetworkPreferenceServiceI {
	
	NetworkPreferenceListResponseVO loadNetworkPreferenceList(Connection con,Locale locale,
			HttpServletResponse response1,UserVO userVO,NetworkPreferenceListResponseVO  response);
	
	
	BaseResponse updateNetworkPreferenceByList(Connection con,MComConnectionI mcomCon, Locale locale,
			HttpServletResponse response1,UserVO userVO,BaseResponse  response,UpdateNetworkPreferenceReqVO requestVO) throws Exception;
	
}
