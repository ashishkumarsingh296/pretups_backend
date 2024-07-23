package com.restapi.preferences;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.preferences.requestVO.UpdateSystemPreferencesRequestVO;
import com.restapi.preferences.responseVO.SystemPreferencesResponseVO;

@Service
public interface PreferencesServiceI {

	/**
	 * 
	 * @param module
	 * @param type
	 * @param con
	 * @param response
	 * @param responseSwag
	 * @param sessionUserVO
	 * @throws BTSLBaseException
	 */
	public void getSystemPreferences(String module, String type, Connection con, SystemPreferencesResponseVO response,
			HttpServletResponse responseSwag, UserVO sessionUserVO) throws BTSLBaseException;

	/**
	 * 
	 * @param con
	 * @param mcomCon
	 * @param response
	 * @param responseSwag
	 * @param sessionUserVO
	 * @param requestVO
	 * @throws Exception
	 */
	public void updateSystemPreferences(Connection con, MComConnectionI mcomCon, BaseResponse response,
			HttpServletResponse responseSwag, UserVO sessionUserVO, UpdateSystemPreferencesRequestVO requestVO)
			throws Exception;

}
