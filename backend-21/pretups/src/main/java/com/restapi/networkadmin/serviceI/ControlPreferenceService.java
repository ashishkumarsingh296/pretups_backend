package com.restapi.networkadmin.serviceI;

import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.UpdateControlPreferenceVO;
import com.restapi.networkadmin.responseVO.ControlPreferenceListsResponseVO;

public interface ControlPreferenceService {

	/**
	 * @author sarthak.saini
	 * @param locale
	 * @param moduleCodeString
	 * @param controlCodeString
	 * @param preferenceCodeString 
	 * @param loginId 
	 * @param responseSwag 
	 * @return
	 */
	public ControlPreferenceListsResponseVO fetchCtrlPreferenceLists(Locale locale, String moduleCodeString,
			String controlCodeString, String preferenceCodeString, String loginId, HttpServletResponse responseSwag)throws SQLException ;

	/**
	 * @author sarthak.saini
	 * @param locale 
	 * @param responseSwag
	 * @param userVO
	 * @param updateControlPreferenceVO
	 * @param msisdn 
	 * @return
	 */
	public BaseResponse updateControlPreference(Locale locale, HttpServletResponse responseSwag, 
			UpdateControlPreferenceVO updateControlPreferenceVO, String msisdn)throws SQLException;

}
