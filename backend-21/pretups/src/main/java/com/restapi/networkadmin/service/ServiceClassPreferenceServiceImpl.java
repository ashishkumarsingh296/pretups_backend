package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.requestVO.UpdateNetworkPreferenceReqVO;
import com.restapi.networkadmin.requestVO.UpdateNetworkPreferenceVO;
import com.restapi.networkadmin.requestVO.UpdateServiceClassPreferenceReqVO;
import com.restapi.networkadmin.requestVO.UpdateServiceClassPreferenceVO;
import com.restapi.networkadmin.responseVO.ServiceClassListResponseVO;
import com.restapi.networkadmin.responseVO.ServiceClassPreferenceListResponseVO;
import com.restapi.networkadmin.serviceI.ServiceClassPreferenceServiceI;
import com.web.pretups.preference.businesslogic.PreferenceWebDAO;


@Service("ServiceClassPreferenceServiceI")
public class ServiceClassPreferenceServiceImpl implements ServiceClassPreferenceServiceI{

	public static final Log log = LogFactory.getLog(ServiceClassPreferenceServiceImpl.class.getName());
	public static final String classname = "ServiceClassPreferenceServiceImpl";
	
	@Override
	public ServiceClassListResponseVO loadServiceClassList(Connection con, Locale locale, HttpServletResponse response1,
			UserVO userVO, ServiceClassListResponseVO response) {
		
		
		final String METHOD_NAME = "loadServiceClassList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		PreferenceWebDAO preferencewebDAO = null;
		NetworkVO networkVO = null;
		
		try {
			
			preferencewebDAO = new PreferenceWebDAO();
			
			
			response.setServiceClassList(preferencewebDAO.loadServiceClassList(con, userVO.getNetworkID()));
            // preferenceForm.setModuleList(LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE,true));
            networkVO = (NetworkVO) NetworkCache.getObject(userVO.getNetworkID());
            response.setNetworkDescription(networkVO.getNetworkName());
            if (response.getServiceClassList().isEmpty()) {
                throw new BTSLBaseException(this, "loadServiceClassPreference", "preference.displayserviceclasspreference.msg.noserviceclassdefine", "selectserviceclasspreference");
            }
            response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICE_CLASS_LIST_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_LIST_SUCCESS);
			
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.SERVICE_CLASS_LIST_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;
		
	}
	
	
	
	
	
	
	
	@Override
	public ServiceClassPreferenceListResponseVO loadServiceClassPreferenceList(Connection con, Locale locale, HttpServletResponse response1, UserVO userVO,
			ServiceClassPreferenceListResponseVO response, String serviceCode) {
		
		final String METHOD_NAME = "loadServicePreferenceClassList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		PreferenceWebDAO preferencewebDAO = null;
		NetworkVO networkVO = null;
		
		ArrayList serviceClassList;
		
		try {
			
			preferencewebDAO = new PreferenceWebDAO();
			serviceClassList = new ArrayList();
			
			serviceClassList = preferencewebDAO.loadServiceClassList(con, userVO.getNetworkID());
            if (serviceClassList.isEmpty()) {
                throw new BTSLBaseException(this, "loadServiceClassPreference", "preference.displayserviceclasspreference.msg.noserviceclassdefine", "selectserviceclasspreference");
            }
            
            response.setPreferenceList(preferencewebDAO.loadServiceClassPreferenceData(con, userVO.getNetworkID(), serviceCode));
            ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceCode, serviceClassList);
            response.setServiceDescription(listValueVO.getLabel());
            response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PREFERENCE_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.PREFERENCE_SUCCESS);
			
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.PREFERENCE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.PREFERENCE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;
		
	}
	
	
	
	
	
	
	
	
	@Override
	public BaseResponse updateServiceClassPreferenceByList(Connection con, MComConnectionI mcomCon, Locale locale, HttpServletResponse response1,
			UserVO userVO, BaseResponse response, UpdateServiceClassPreferenceReqVO requestVO) throws Exception {
		
		final String METHOD_NAME = "updateServiceClassPreferenceByList";
		if (log.isDebugEnabled()) {
			log.debug("updateServiceClassPreferenceByList", "Entered");
		}
		
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		
		int updateCount = 0;
        PreferenceWebDAO preferencewebDAO = null;
        Date currentDate = null;
        
        //
        if(requestVO.getPreferenceUpdateList() == null) {
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference List"});
		}else if(requestVO.getPreferenceUpdateList().size() <= 0) {
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference List"});
		}else {
			
			for(UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO: requestVO.getPreferenceUpdateList()) {
				if(BTSLUtil.isNullString(updateServiceClassPreferenceVO.getAllowAction())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"allow action"});
				}
				
				if(BTSLUtil.isNullString(updateServiceClassPreferenceVO.getNetworkCode())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Network Code"});
				}
				
				if(BTSLUtil.isNullString(updateServiceClassPreferenceVO.getServiceCode())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Service Code"});
				}
				
				if(BTSLUtil.isNullString(updateServiceClassPreferenceVO.getModuleCode())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Module Code"});
				}
				
				if(BTSLUtil.isNullString(updateServiceClassPreferenceVO.getPreferenceCode())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference Code"});
				}
				
				if(BTSLUtil.isNullString(updateServiceClassPreferenceVO.getPreferenceValue())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference value"});
				}
				
				if(BTSLUtil.isNullString(updateServiceClassPreferenceVO.getPreferenceValueType())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Preference value type"});
				}
				
				if(updateServiceClassPreferenceVO.getLastModifiedTime() == null) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,new String[] {"Last modified time"});
				}
			}		
		}
        //
		
        try {
        	
        	ArrayList<UpdateServiceClassPreferenceVO> preferenceUpdateList = requestVO.getPreferenceUpdateList();
        	ArrayList<PreferenceCacheVO> preferenceList = new ArrayList<PreferenceCacheVO>();
        	
        	for(UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO : preferenceUpdateList) {
				PreferenceCacheVO preferenceCacheVO = new PreferenceCacheVO();
				preferenceCacheVO.setNetworkCode(updateServiceClassPreferenceVO.getNetworkCode());
				preferenceCacheVO.setPreferenceCode(updateServiceClassPreferenceVO.getPreferenceCode());
				preferenceCacheVO.setValueType(updateServiceClassPreferenceVO.getPreferenceValueType());
				preferenceCacheVO.setValue(updateServiceClassPreferenceVO.getPreferenceValue());
				preferenceCacheVO.setAllowAction(updateServiceClassPreferenceVO.getAllowAction());
				preferenceCacheVO.setLastModifiedTime(updateServiceClassPreferenceVO.getLastModifiedTime());
				preferenceCacheVO.setServiceCode(updateServiceClassPreferenceVO.getServiceCode());
				preferenceCacheVO.setModuleCode(updateServiceClassPreferenceVO.getModuleCode());
				preferenceList.add(preferenceCacheVO);
			}
        	
        	
        	currentDate = new Date();
        	preferencewebDAO = new PreferenceWebDAO();
            updateCount = preferencewebDAO.updateServiceClassPreference(con, preferenceList, currentDate, userVO.getUserID());
            
            //System.out.println(updateCount);
            
            //mcomCon.finalCommit();
            if (con != null) {
				if (updateCount == preferenceList.size()) {
					mcomCon.finalCommit();

					// log the data in adminOperationLog.log
					AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(TypesI.LOGGER_PREFERENCE_SYSTEM_SOURCE);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					
					PreferenceCacheVO preferenceCacheVO = null;
					for (int i = 0, j = preferenceList.size(); i < j; i++) {
						preferenceCacheVO = (PreferenceCacheVO) preferenceList.get(i);
						adminOperationVO.setInfo("Service Class (" + preferenceCacheVO.getPreferenceCode()
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
            
            
        }
        catch(Exception e) {
        	log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			throw e;
        }
        finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting return=" + updateCount);
			}
        }
		
		return response;
		
	}

}
