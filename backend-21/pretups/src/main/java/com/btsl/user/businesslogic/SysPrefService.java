package com.btsl.user.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btsl.user.businesslogic.entity.ControlPreferences;
import com.btsl.user.businesslogic.entity.NetworkPreferences;
import com.btsl.user.businesslogic.entity.SystemPreferences;

@Service
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SysPrefService extends CommonService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SysPrefService.class);

	@Autowired
	private VMSCacheRepository vmsCacheRepository;
	
	@Autowired
	private SystemPreferencesRepository systemPreferencesRepository;
	
	@Autowired
	private NetworkPrefRepository networkPrefRepository;
	
	@Autowired
	private ControlPreferenceRepo controlPreferenceRepo;
	

	private SysPreferenceReq sysPreferenceReq;

	
	private Map<String, String> preMapData;

	@Transactional
	public SysPreferenceResp execute(SysPreferenceReq sysPreferenceReq, String userSessionID) {
		boolean success = false;

		List<String> params = new ArrayList<>();
		LOGGER.debug( "Get System preference request processing started.....");
		this.sysPreferenceReq = sysPreferenceReq;

		CachedObject cachedObj = (CachedObject) CacheManager.getCache(userSessionID);
		LoggedInUserInfo loggedinUserInfo = (LoggedInUserInfo) cachedObj.getObjectVal();
		super.initiate(sysPreferenceReq, serviceRoleCode,null, loggedinUserInfo);
		validateInputs();
		
		if(sysPreferenceReq.getModuleCode().equalsIgnoreCase(Constants.ALL.getStrValue())) {
			preMapData=loadSysPrefUIData();
			if(this.preMapData.size()>0 || this.preMapData.isEmpty() ) {
				LOGGER.info("Reloading System UI data , as Redis cache got cleared...........");
				this.preMapData=callReloadSysprefUIData();
			}
		}else {
		String prefereneCodes = com.btsl.util.Constants.getProperty(sysPreferenceReq.getModuleCode());
			if (prefereneCodes == null || prefereneCodes.trim().length() <= 0) {
				throw new ApplicationException(Constants.SYSPREF001.getStrValue());
			}
			this.preMapData = getPrefMapbyModuleCode(sysPreferenceReq.getModuleCode());
		}

		if (!CommonUtils.isNullorEmpty(preMapData)) {
			success = true;
			responseMessageCode = MessageCodes.MOD_PREF_SUCCESS.getStrValue();
		} else {
			responseMessageCode = MessageCodes.MOD_PREF_FAILURE.getStrValue();
			responseStatus = Constants.BAD_REQUEST.getStrValue();
		}

		if (success) {
			responseStatus = Constants.SUCCESS_RESPONSE.getStrValue();
		}

		generateReponseMessage(params);
		return createResponse();
	}

	@Override
	public SysPreferenceResp createResponse() {
		SysPreferenceResp response = new SysPreferenceResp();
		response.setStatus(responseStatus);
		response.setMessage(responseMessage);
		response.setMessageCode(responseMessageCode);
		response.setSysPrefMap(preMapData);
		LOGGER.debug( "Get System Preference call completed.....");
		return response;
	}

	public Map<String, String> getPrefMapbyModuleCode(String moduleCode) {
		return vmsCacheRepository.getPreferListbyModule(moduleCode);

	}
	
	
	
	
	
	private Map callReloadSysprefUIData() {
		loadSysPrefForModules();
		return loadSysPrefUIData();
	}
	
	
	private Map loadSysPrefUIData() {
		Map preMapDataNew= new HashMap<>();
		String syskeys = com.btsl.util.Constants.getProperty(Constants.SYSPREFKEYS.getStrValue());
		String moduleCode[] =syskeys.split(",");
		for(int i=0;i<moduleCode.length;i++) {
		 Map modulePrefdata =	getPrefMapbyModuleCode(moduleCode[i]);
		 if(modulePrefdata!=null) {
			 preMapDataNew.putAll(getPrefMapbyModuleCode(moduleCode[i]));
		 }
		}
	return preMapDataNew;
	}
	


	public void loadAllSystemPreferences() {
	  LOGGER.info("Loading all System Preferences to Redis Cache........");
	  List<SystemPreferences> listSystemPrefenes =systemPreferencesRepository.loadAllSystemPreferences();
	  vmsCacheRepository.loadSystemPreferncetoRedis(listSystemPrefenes);
	}

	
	public void loadSysPrefForModules() {
		String syskeys = com.btsl.util.Constants.getProperty(Constants.SYSPREFKEYS.getStrValue());
		LOGGER.debug( "Sys preference keys :: " + syskeys);
		if (syskeys == null || syskeys.trim().length() <= 0) {
			throw new ApplicationException(Constants.SYSPREF001.getStrValue());
		}
		String[] syskeyList = syskeys.split(",");
		for (int i = 0; i < syskeyList.length; i++) {

			String prefCodeslist = com.btsl.util.Constants.getProperty(syskeyList[i]);
			LOGGER.debug( "Module Code :: " + syskeyList[i] + "->" + prefCodeslist);
			String[] modulePrelist = prefCodeslist.split(",");
			loadsysPreferencetoRedis(syskeyList[i], modulePrelist);
		}

	}

	private void loadsysPreferencetoRedis(String moduleCode, String[] preferenceList) {
		Map<String, String> mpdata = new HashMap<String, String>();
		String prefValue = null;
		for (int i = 0; i < preferenceList.length; i++) {
			prefValue = vmsCacheRepository.getSystemPreferenceValue(preferenceList[i]);
			LOGGER.debug( preferenceList[i] + "->" + prefValue);
			mpdata.put(preferenceList[i], prefValue);
		}
		vmsCacheRepository.putModulueCodePrefMaps(moduleCode, mpdata);
	}

	
	
	
	public void loadAllnetworkPreferences() {
		LOGGER.debug( "Network Preference loading started ********************************");
		List<NetworkPreferences> listNetworkPrefs =networkPrefRepository.getAllNetworkPreferences();
		vmsCacheRepository.loadNetworkPreferncetoRedis(listNetworkPrefs);
		LOGGER.debug( "Network Preference loading completed ********************************");
		
	}
	
	
	
	public void loadAllcontrolPreferences() {
		LOGGER.debug( "Control Preference loading started ********************************");
		List<ControlPreferences> listControlPrefs =controlPreferenceRepo.loadAllControlPreferences();
		vmsCacheRepository.loadControlPreferncetoRedis(listControlPrefs);
		LOGGER.debug( "Control Preference loading completed ********************************");
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void validateInputs() {
		CommonUtils.checkValidation(sysPreferenceReq.getModuleCode(), Constants.ROWNO.getStrValue(),
				MessageCodes.FIELD_MANDATORY.getStrValue(), "Row no is empty");
	}

}
