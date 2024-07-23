package com.restapi.preferences;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.preferences.requestVO.UpdateSystemPreferenceVO;
import com.restapi.preferences.requestVO.UpdateSystemPreferencesRequestVO;
import com.restapi.preferences.responseVO.SystemPreferencesResponseVO;
import com.web.pretups.preference.businesslogic.PreferenceWebDAO;

@Service("PreferencesServiceI")
public class PreferencesServiceImpl implements PreferencesServiceI {

	public static final Log log = LogFactory.getLog(PreferencesServiceImpl.class.getName());
	public static final String classname = "PreferencesServiceImpl";

	@SuppressWarnings("unchecked")
	@Override
	public void getSystemPreferences(String module, String type, Connection con, SystemPreferencesResponseVO response,
			HttpServletResponse responseSwag, UserVO sessionUserVO) throws BTSLBaseException {
		final String methodName = "getSystemPreferences";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

		if(!PretupsI.SUPER_ADMIN.equals(sessionUserVO.getCategoryCode())) {
			throw new BTSLBaseException(classname, methodName,PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
		}
		
		PreferenceWebDAO preferencewebDAO = new PreferenceWebDAO();
		ArrayList<PreferenceCacheVO> preferenceList = (ArrayList<PreferenceCacheVO>) preferencewebDAO
				.loadSystemPreferenceData(con, type, module);
		if (preferenceList != null && preferenceList.size() > 0) {
			response.setPreferenceList(preferenceList);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		} else {
			response.setPreferenceList(new ArrayList<PreferenceCacheVO>());
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
		}

		String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), PretupsErrorCodesI.SUCCESS, null);
		response.setMessage(resmsg);

		return;
	}

	@Override
	public void updateSystemPreferences(Connection con, MComConnectionI mcomCon, BaseResponse response,
			HttpServletResponse responseSwag, UserVO sessionUserVO, UpdateSystemPreferencesRequestVO requestVO)
			throws Exception {

		final String METHOD_NAME = "updateSystemPreference";
		if (log.isDebugEnabled()) {
			log.debug("updateSystemPreference", "Entered");
		}
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Date currentDate = null;
		PreferenceWebDAO preferencewebDAO = null;
		int updateCount = 0;
		
		if(!PretupsI.SUPER_ADMIN.equals(sessionUserVO.getCategoryCode())) {
			throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
		}
		
		if(requestVO.getPreferenceUpdateList() == null) {
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference List"});
		}else if(requestVO.getPreferenceUpdateList().size() <= 0) {
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference List"});
		}else {
			
			for(UpdateSystemPreferenceVO updateSystemPreferenceVO: requestVO.getPreferenceUpdateList()) {
				if(BTSLUtil.isNullString(updateSystemPreferenceVO.getPreferenceCode())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference Code"});
				}
				
				if(BTSLUtil.isNullString(updateSystemPreferenceVO.getPreferenceValue())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference value"});
				}
				
				if(BTSLUtil.isNullString(updateSystemPreferenceVO.getPreferenceValueType())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference value type"});
				}
				
				if(updateSystemPreferenceVO.getPreferenceCode() == null) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Last modified time"});
				}
			}		
		}
		
		
		try {

			ArrayList<UpdateSystemPreferenceVO> preferenceUpdateList = requestVO.getPreferenceUpdateList();
			ArrayList<PreferenceCacheVO> preferenceList = new ArrayList<PreferenceCacheVO>();
			for(UpdateSystemPreferenceVO updateSystemPreferenceVO : preferenceUpdateList) {
				PreferenceCacheVO preferenceCacheVO = new PreferenceCacheVO();
				preferenceCacheVO.setPreferenceCode(updateSystemPreferenceVO.getPreferenceCode());
				preferenceCacheVO.setValueType(updateSystemPreferenceVO.getPreferenceValueType());
				preferenceCacheVO.setValue(updateSystemPreferenceVO.getPreferenceValue());
				preferenceCacheVO.setLastModifiedTime(updateSystemPreferenceVO.getLastModifiedTime());
				preferenceList.add(preferenceCacheVO);
			}

			currentDate = new Date();

			preferencewebDAO = new PreferenceWebDAO();
			updateCount = preferencewebDAO.updateSystemPreference(con, preferenceList, currentDate,
					sessionUserVO.getUserID());
			if (con != null) {
				if (updateCount == preferenceList.size()) {
					mcomCon.finalCommit();

					// log the data in adminOperationLog.log
					AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(TypesI.LOGGER_PREFERENCE_SYSTEM_SOURCE);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
					adminOperationVO.setLoginID(sessionUserVO.getLoginID());
					adminOperationVO.setUserID(sessionUserVO.getUserID());
					adminOperationVO.setCategoryCode(sessionUserVO.getCategoryCode());
					adminOperationVO.setNetworkCode(sessionUserVO.getNetworkID());
					adminOperationVO.setMsisdn(sessionUserVO.getMsisdn());
					
					PreferenceCacheVO preferenceCacheVO = null;
					for (int i = 0, j = preferenceList.size(); i < j; i++) {
						preferenceCacheVO = (PreferenceCacheVO) preferenceList.get(i);
						adminOperationVO.setInfo("System preference (" + preferenceCacheVO.getPreferenceCode()
								+ ") has modified successfully");
						AdminOperationLog.log(adminOperationVO);
					}
					response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response.setMessageCode(PretupsErrorCodesI.PREFERENCE_UPDATE_SUCCESS);
					String resmsg = RestAPIStringParser.getMessage(new Locale(lang,country),
								PretupsErrorCodesI.PREFERENCE_UPDATE_SUCCESS, null);
					response.setMessage(resmsg);
					
				} else {
					mcomCon.finalRollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PREFERENCE_UPDATE_FAIL, 0, null);
				}
			}

		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting return=" + updateCount);
			}
		}
		return;
	}

}
