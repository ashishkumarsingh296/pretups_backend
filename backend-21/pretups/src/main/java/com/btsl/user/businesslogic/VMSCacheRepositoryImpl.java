/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayMappingCacheVO;
import com.btsl.user.businesslogic.entity.ControlPreferences;
import com.btsl.user.businesslogic.entity.LocaleMaster;
import com.btsl.user.businesslogic.entity.NetworkPreferences;
import com.btsl.user.businesslogic.entity.SysMessages;
import com.btsl.user.businesslogic.entity.SysMessagesId;
import com.btsl.user.businesslogic.entity.SystemPreferences;
import com.btsl.util.BTSLUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Data base operations for System tables.
 * 
 * @author Subesh KCV
 * @date : 13-Aug-2019
 */
@Component
@CacheConfig(cacheNames = "systemdata_cache")
public class VMSCacheRepositoryImpl implements VMSCacheRepository {



	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VMSCacheRepositoryImpl.class);

	/** The entity manager. */
/*	@PersistenceContext
	private EntityManager entityManager;
*/
	@Autowired
	private RedisUtil<Object> redisUtilSystemPreferences;

	@Autowired
	@Qualifier("systemPreferencesRepository")
	private SystemPreferencesRepository systemPreferencesRepository;
	@Autowired
	private SysMessagesRepository sysMessagesRepository;
	@Autowired
	private MessageGatewayRepository messageGatewayRepository;
	/*@Autowired
	private MessageReqRespMappingRepository messageReqRespMappingRepository;
	*/
	
	@Autowired
	private  LocaleMasterRepository localeMasterRepository;

	@Autowired
	private ProductTypeRepository productTypeRepository;
	
	@Autowired
	private NetworkPrefRepository networkPrefRepository;
	
	@Autowired
	private ControlPreferenceRepo controlPreferenceRepo;


	@Value(PropertiesConstant.AEKE_CLIENT_LAYER)
	private String aeesKay;

	protected Jedis jedis = null;

	/** The redis hostname. */
	@Value("${spring.redis.host}")
	private String redisHost;

	/** The redis port. */
	@Value("${spring.redis.port}")
	private Integer redisPort;

	/**
	 * Get the system preference based on code.
	 *
	 * @param value - value
	 * @return Object
	 */
	@Override
	public String getSystemPreferenceValue(String preferenceCode) {
		
		SystemPreferences systemPreferences = null;
		Boolean isRedisConnected = isRedisConnected();
		if (Boolean.TRUE.equals(isRedisConnected)) {
			if (redisUtilSystemPreferences != null) {
				systemPreferences = (SystemPreferences) redisUtilSystemPreferences.getMapAsSingleEntry(
						Constants.VMS_SYSTEM_PREFERENCES.getStrValue(), Constants.VMS_SYSTEM_PREFERENCES.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() + preferenceCode);
			}
		}
		if (systemPreferences == null) {
			systemPreferences = systemPreferencesRepository.getSystemPreferencesByCode(preferenceCode);
			if (Boolean.TRUE.equals(isRedisConnected) && systemPreferences != null
					&& redisUtilSystemPreferences != null) {
				redisUtilSystemPreferences.putMap(
						Constants.VMS_SYSTEM_PREFERENCES.getStrValue(), Constants.VMS_SYSTEM_PREFERENCES.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() + systemPreferences.getPreferenceCode(),
						systemPreferences);
			} 
		}

		return systemPreferences.getDefaultValue();
	}

	@Override
	public void loadSystemPreferncetoRedis(List<SystemPreferences> listsytemPreference) {
		Boolean isRedisConnected = isRedisConnected();
		if (isRedisConnected && redisUtilSystemPreferences != null) {
			for (SystemPreferences syspref : listsytemPreference) {
				LOGGER.debug( MessageFormat.format("Loading {0} -> {1}", syspref.getPreferenceCode(),
						syspref.getDefaultValue()));
				redisUtilSystemPreferences.putMap(Constants.VMS_SYSTEM_PREFERENCES.getStrValue(),
						Constants.VMS_SYSTEM_PREFERENCES.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
								+ syspref.getPreferenceCode(),
						syspref);
			}
			LOGGER.debug( "Sytem Preference loading completed ********************************");
		}
	}
	
	
	@Override
	public void loadNetworkPreferncetoRedis(List<NetworkPreferences> listNetworkPreferences) {
		Boolean isRedisConnected = isRedisConnected();
		if (isRedisConnected && redisUtilSystemPreferences != null) {
			for (NetworkPreferences netwrkPref : listNetworkPreferences) {
				
				LOGGER.debug( MessageFormat.format("Loading {0} & {1} - >{2}", netwrkPref.getNetworkCode(),netwrkPref.getPreferenceCode(),
						netwrkPref.getPreferenceValue()));
				redisUtilSystemPreferences.putMap(Constants.VMS_NETWORK_PREFERENCES.getStrValue(),
						Constants.VMS_NETWORK_PREFERENCES.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
								+netwrkPref.getNetworkCode() + netwrkPref.getPreferenceCode() ,
								netwrkPref);
			}
		
		}
	}

	

	@Override
	public String loadMessageByMessageCodeAndLangCode(String messageCode, int languageCode) {
		
		ResponseMsg responseMsg = null;
		Boolean isRedisConnected = isRedisConnected();
		String message = null;
		if (Boolean.TRUE.equals(isRedisConnected)) {
			responseMsg = (ResponseMsg) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.VMS_RESPONSE_MSG.getStrValue(),
					Constants.VMS_RESPONSE_MSG.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue() + messageCode
							+ Constants.APPEND_TO_SEQ.getStrValue() + languageCode);
			message = (responseMsg != null ? responseMsg.getRespMsg() : null);
		}
		if (responseMsg == null) {
			SysMessages sysMessages = sysMessagesRepository.findById(new SysMessagesId(messageCode, languageCode))
					.orElse(null);
			if (sysMessages != null) {
				putReponseMsg(messageCode, sysMessages.getMessage(),languageCode);
				message = sysMessages.getMessage();
			}
		}

		return message;
	}

	private ResponseMsg putReponseMsg(String messageCode, String message,int languageCode) {
		ResponseMsg responseMsg = new ResponseMsg();
		responseMsg.setRespCode(messageCode);
		responseMsg.setRespMsg(message);
		if (Boolean.TRUE.equals(isRedisConnected())) {
			redisUtilSystemPreferences.putMap(Constants.VMS_RESPONSE_MSG.getStrValue(),
					Constants.VMS_RESPONSE_MSG.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
							+ responseMsg.getRespCode()+ Constants.APPEND_TO_SEQ.getStrValue() + languageCode,
					responseMsg);
		}
		return responseMsg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getPreferListbyModule(String moduleCode) {
		Boolean isRedisConnected = isRedisConnected();
		Map<String, String> keyValMap = new HashMap<String, String>();
		if (Boolean.TRUE.equals(isRedisConnected)) {
			keyValMap = (HashMap<String, String>) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.VMS_MODULE_PREFMAP.getStrValue(),
					Constants.VMS_MODULE_PREFMAP.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue() + moduleCode);

		}

		return keyValMap;
	}

	public void putModulueCodePrefMaps(String moduleCode, Map<String, String> subMapdata) {
		redisUtilSystemPreferences.putMap(Constants.VMS_MODULE_PREFMAP.getStrValue(),
				Constants.VMS_MODULE_PREFMAP.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue() + moduleCode,
				subMapdata);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LocaleMasterModal> loadLocaleMaster() {

		Boolean isRedisConnected = isRedisConnected();
		List<LocaleMasterModal> listLocalMaster = null;
		listLocalMaster = getLocalMasterListFromRedis(isRedisConnected);
		if (listLocalMaster == null) {
			List<LocaleMaster> listLocalMasterlist = localeMasterRepository.getLocalMasterList();
			listLocalMaster = new ArrayList<>();
			
			try {
				for (LocaleMaster lm : listLocalMasterlist) {
					LocaleMasterModal localMasterModel = new LocaleMasterModal();
					BeanUtilsBean.getInstance().copyProperties(localMasterModel, lm);
					LOGGER.debug(MessageFormat.format("LocaleMaster LanguageCode {0} -> {1} -> {2}",lm.getId().getLanguage(),lm.getName(),lm.getId().getCountry()));
					localMasterModel.setCountry(lm.getId().getCountry());
					localMasterModel.setLanugage(lm.getId().getLanguage());
					setRightToLeftPerfInLocaleMasterModel(localMasterModel);
					  
					localMasterModel.setMessage(lm.getMessage());
					listLocalMaster.add(localMasterModel);
					putLocaleMasterModalInRedis(isRedisConnected, lm, localMasterModel);
					putLocaleMasterModalInRedisCache(isRedisConnected, lm, localMasterModel);

				}
				if (Boolean.TRUE.equals(isRedisConnected)) {
					redisUtilSystemPreferences.putMap(Constants.VMS_LOCAL_MASTER_LIST.getStrValue(),
							Constants.VMS_LOCAL_MASTER_LIST.getStrValue(), listLocalMaster);
				}

			} catch (IllegalAccessException | InvocationTargetException e) {
				LOGGER.error("error ", e);
			}

		}
		return listLocalMaster;

	}

	private void setRightToLeftPerfInLocaleMasterModel(LocaleMasterModal localMasterModel) {
		if(righttoLeftTrue(localMasterModel.getLanugage())){
			 
			 localMasterModel.setRightToLeftAllow(true);
		 }else {
			 localMasterModel.setRightToLeftAllow(false);
		 }
	}

	private void putLocaleMasterModalInRedis(Boolean isRedisConnected, LocaleMaster lm,
			LocaleMasterModal localMasterModel) {
		if (isRedisConnected) {
			LocaleMasterModal localmastermodelCache = (LocaleMasterModal) redisUtilSystemPreferences
					.getMapAsSingleEntry(Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue(),
							Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue()
									+ Constants.APPEND_TO_SEQ.getStrValue() + lm.getId().getLanguage()
									);
			if (localmastermodelCache == null) {
				redisUtilSystemPreferences.putMap(Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue(),
						Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() + lm.getId().getLanguage()
								+ Constants.APPEND_TO_SEQ.getStrValue() + lm.getId().getCountry(),
						localMasterModel);
			}

		}
	}
	
	private void putLocaleMasterModalInRedisCache(Boolean isRedisConnected, LocaleMaster lm,
			LocaleMasterModal localMasterModel) {
		if (isRedisConnected) {
				redisUtilSystemPreferences.putMap(Constants.VMS_LOCAL_MASTER_BYLANGCODE.getStrValue(),
						Constants.VMS_LOCAL_MASTER_BYLANGCODE.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() + lm.getLanguageCode(),
						localMasterModel);
		}
	}
	
	public LocaleMasterModal getLocaleMasterModalByLangCode(int languageCode) {
		LocaleMasterModal localmastermodelCache = (LocaleMasterModal) redisUtilSystemPreferences
				.getMapAsSingleEntry(Constants.VMS_LOCAL_MASTER_BYLANGCODE.getStrValue(),
						Constants.VMS_LOCAL_MASTER_BYLANGCODE.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() + languageCode
								);
		if(localmastermodelCache==null) {
			loadLocaleMaster();
			 localmastermodelCache = (LocaleMasterModal) redisUtilSystemPreferences
					.getMapAsSingleEntry(Constants.VMS_LOCAL_MASTER_BYLANGCODE.getStrValue(),
							Constants.VMS_LOCAL_MASTER_BYLANGCODE.getStrValue()
									+ Constants.APPEND_TO_SEQ.getStrValue() + languageCode
									);
			
		}
		return localmastermodelCache;
		
	}
	
	
	
	

	@SuppressWarnings("unchecked")
	private List<LocaleMasterModal> getLocalMasterListFromRedis(Boolean isRedisConnected) {
		List<LocaleMasterModal> 	localeMasterModalList=null;
		if (Boolean.TRUE.equals(isRedisConnected)) {
			localeMasterModalList = (List<LocaleMasterModal>) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.VMS_LOCAL_MASTER_LIST.getStrValue(), Constants.VMS_LOCAL_MASTER_LIST.getStrValue());

		}
		return localeMasterModalList;
	}
	
	private boolean righttoLeftTrue(String language) {
		String rightToLeftLangs=  com.btsl.util.Constants.getProperty(Constants.LANGUAGES_RTL_DIRECTION.getStrValue());
		String [] righttoLeftArray = rightToLeftLangs.split(",");
		boolean retVal=false;
		for(int i=0;i<righttoLeftArray.length;i++) {
		     if(righttoLeftArray[i].equalsIgnoreCase(language)) {
		    	 retVal=true; 
		    	 break;
		     }
		}
		return retVal;
		
	}
	

	/**
	 * Get the MessageGateway based on gatewayCode.
	 *
	 * @param gatewayCode - request
	 * 
	 * @return MessageGateway
	 */

	@Override
	public MessageGatewayVONew loadMessageGatewayCacheQry(String gatewayCode) {
		Boolean isRedisConnected = isRedisConnected();
		MessageGatewayVONew messageGateway = null;
		if (Boolean.TRUE.equals(isRedisConnected)) {
			messageGateway = (MessageGatewayVONew) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.VMS_LOCAL_MESSAGEGATEWAY.getStrValue(), Constants.VMS_LOCAL_MESSAGEGATEWAY.getStrValue()
							+ Constants.APPEND_TO_SEQ.getStrValue() + gatewayCode);
		}
		if (CommonUtils.isNullorEmpty(messageGateway)) {
			List<MessageGatewayVONew> listMessageGateway = messageGatewayRepository.loadMessageGatewayCacheQry(gatewayCode);
			if(!CommonUtils.isNullorEmpty(listMessageGateway)) {
				messageGateway=listMessageGateway.get(0);
				if (Boolean.TRUE.equals(isRedisConnected)) {
					loadAllMessageGateway();
					redisUtilSystemPreferences.putMap(Constants.VMS_LOCAL_MESSAGEGATEWAY.getStrValue(),
							Constants.VMS_LOCAL_MESSAGEGATEWAY.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
									+ gatewayCode,
									listMessageGateway.get(0));
				}
			}
			
		}
		
		if (CommonUtils.isNullorEmpty(messageGateway)) {
			messageGateway = new MessageGatewayVONew();
		}
		
		return messageGateway;

	}

	@Override
	public LocaleMasterModal loadLocaleMasterByLangCntry(String langCode, String countryCode) {
		Boolean isRedisConnected = isRedisConnected();
		LocaleMasterModal localmastermodelCache=null;
		if (isRedisConnected) {
			 localmastermodelCache = (LocaleMasterModal) redisUtilSystemPreferences
					.getMapAsSingleEntry(Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue(),
							Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
									+ langCode + Constants.APPEND_TO_SEQ.getStrValue() + countryCode);

			if (localmastermodelCache != null)
				return localmastermodelCache;
			else {
				loadLocaleMaster();
				 localmastermodelCache = (LocaleMasterModal) redisUtilSystemPreferences
						.getMapAsSingleEntry(Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue(),
								Constants.VMS_LOCAL_MASTER_BYCNTRYLANG.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
										+ langCode + Constants.APPEND_TO_SEQ.getStrValue() + countryCode);

			}

		}

		return localmastermodelCache;
	}

	/**
	 * Checks if is redis connected.
	 *
	 * @param REDIS_HOSTNAME the redis hostname
	 * @param REDIS_PORT     the redis port
	 * @return true, if is redis connected
	 */
	public boolean isRedisConnected() {
		boolean isRedisConnected = false;
		String redisEnable = com.btsl.util.Constants.getProperty("REDIS_ENABLE");
		if(PretupsI.REDIS_ENABLE.equalsIgnoreCase(redisEnable.trim())) {
			try (Jedis jedis = new Jedis(redisHost, redisPort)) {

				if (Constants.PONG.getStrValue().equals(jedis.ping())) {
					isRedisConnected = true;
				}
			} catch (JedisConnectionException e) {
				LOGGER.error("isRedisConnected:{}", e);
			}
		}
		return isRedisConnected;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List loadLookupDropDown(String lookupType) {
		ArrayList loadLookupslist = new ArrayList<>();
		Boolean isRedisConnected = isRedisConnected();
		if (Boolean.TRUE.equals(isRedisConnected)) {
			loadLookupslist = (ArrayList) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.VMS_LOCAL_LOADLOOKUPS.getStrValue(),
					Constants.VMS_LOCAL_LOADLOOKUPS.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue() + lookupType);
		}
		
		//check commented earlier
		if (CommonUtils.isNullorEmpty(loadLookupslist)) {
			loadLookupslist = productTypeRepository.loadLookupsList(lookupType);
			if (Boolean.TRUE.equals(isRedisConnected)) {
				redisUtilSystemPreferences.putMap(Constants.VMS_LOCAL_LOADLOOKUPS.getStrValue(),
						Constants.VMS_LOCAL_LOADLOOKUPS.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
								+ lookupType,
						loadLookupslist);
			}
		}
		return loadLookupslist;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public MessageGatewayMappingCacheVO loadMessageGatewayMappingCache(String requestCode) {
		Boolean isRedisConnected = isRedisConnected();
		MessageGatewayMappingCacheVO messageGatewayMapping = new MessageGatewayMappingCacheVO();
		if (Boolean.TRUE.equals(isRedisConnected)) {
			messageGatewayMapping = (MessageGatewayMappingCacheVO) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.VMS_LOCAL_MESSAGEGATEWAYMAPPING.getStrValue(),
					Constants.VMS_LOCAL_MESSAGEGATEWAYMAPPING.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
							+ requestCode);
		}
		/*if (CommonUtils.isNullorEmpty(messageGatewayMapping)) {
			messageGatewayMapping = messageReqRespMappingRepository.loadMessageGatewayMappingCache(requestCode);
			if (Boolean.TRUE.equals(isRedisConnected)) {
				redisUtilSystemPreferences.putMap(Constants.VMS_LOCAL_MESSAGEGATEWAYMAPPING.getStrValue(),
						Constants.VMS_LOCAL_MESSAGEGATEWAYMAPPING.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
								+ messageGatewayMapping.getRequestCode(),
						messageGatewayMapping);
			}
		}*/
		return messageGatewayMapping;

	}
	
	
	/**
	 * Get the Network preference based on networkCode and preferenceCode.
	 *
	 * @param value - value
	 * @return Object
	 */
	@Override
	public String getNetworkPreferenceValue(String networkCode,String preferenceCode) {
		NetworkPreferences networkPrferences = null;
		String returnValue=null;
		Boolean isRedisConnected = isRedisConnected();
		if (Boolean.TRUE.equals(isRedisConnected)) {
			if (redisUtilSystemPreferences != null) {
				networkPrferences = (NetworkPreferences) redisUtilSystemPreferences.getMapAsSingleEntry(
						Constants.VMS_NETWORK_PREFERENCES.getStrValue(), Constants.VMS_NETWORK_PREFERENCES.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() + networkCode+preferenceCode);
				
			}
		}
		
		if (networkPrferences == null) {
			LOGGER.debug( "************* After clearing cache..........");
			networkPrferences = networkPrefRepository.getNetworkPreferenceCodeByCode(networkCode, preferenceCode);
			if (Boolean.TRUE.equals(isRedisConnected) && networkPrferences != null
					&& redisUtilSystemPreferences != null) {
				redisUtilSystemPreferences.putMap(
						Constants.VMS_NETWORK_PREFERENCES.getStrValue(), Constants.VMS_NETWORK_PREFERENCES.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() + networkCode+preferenceCode,
								networkPrferences);
			} else {
				return null;
			}
			
			
			returnValue =networkPrferences.getPreferenceValue();
			
		    if(CommonUtils.isNullorEmpty(returnValue)) {
		    	returnValue =getSystemPreferenceValue(preferenceCode);
		    }
		}else {
			returnValue=networkPrferences.getPreferenceValue();
		}
	   LOGGER.debug( MessageFormat.format( "************* Network Preference value {0} -> {1}",preferenceCode,networkPrferences.getPreferenceValue() ));
		return returnValue;
	}


	@Override
	public void loadSysMessages() {
		List<SysMessages> listSysMessages =sysMessagesRepository.loadAllSysMessages();
		for(SysMessages sysmsg: listSysMessages) {
			putReponseMsg(sysmsg.getId().getMessageCode(),sysmsg.getMessage(),sysmsg.getId().getLanguagecode());
		}
		
	}
	
	
	public void  putRegExPropsinRedisCache(Properties props,String language) {
		Boolean isRedisConnected = isRedisConnected();
		if (Boolean.TRUE.equals(isRedisConnected)  	&& redisUtilSystemPreferences != null) {
			redisUtilSystemPreferences.putMap(
					Constants.VMS_REGEX_LANGUAGE_PROPS.getStrValue(), Constants.VMS_REGEX_LANGUAGE_PROPS.getStrValue()
							+ Constants.APPEND_TO_SEQ.getStrValue() +language,
							props);
		} 
		
	}
	
	public void loadAllRegExSecurityProperties() {
		List<LocaleMaster> listLocalMasterlist = localeMasterRepository.getLocalMasterList();
		
		for (LocaleMaster lm : listLocalMasterlist) {
			LOGGER.debug( MessageFormat.format("Loading security properties for {0} " ,lm.getId().getLanguage()));
			Properties prop =CommonUtils.getRegExSecuriyProperties(lm.getId().getLanguage());
			LOGGER.debug( MessageFormat.format("putting  security properties in redis cache {0} " ,prop.get("userLoginId"))); 
			putRegExPropsinRedisCache(prop,lm.getId().getLanguage());
		}
	}
	
	
	public Properties  getRegExPropsinRedisCache(String language) {
		Boolean isRedisConnected = isRedisConnected();
		Properties prop=null;
		if (Boolean.TRUE.equals(isRedisConnected)  	&& redisUtilSystemPreferences != null) {
		 prop =	(Properties) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.VMS_REGEX_LANGUAGE_PROPS.getStrValue(), Constants.VMS_REGEX_LANGUAGE_PROPS.getStrValue()
							+ Constants.APPEND_TO_SEQ.getStrValue() +language);
		} 
		  if(prop==null) {
			  loadAllRegExSecurityProperties();
			  prop =	(Properties) redisUtilSystemPreferences.getMapAsSingleEntry(
						Constants.VMS_REGEX_LANGUAGE_PROPS.getStrValue(), Constants.VMS_REGEX_LANGUAGE_PROPS.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() +language);
		  }
		
		return prop;
	}

	@Override
	public void loadControlPreferncetoRedis(List<ControlPreferences> listControlPreferences) {
		// TODO Auto-generated method stub
		Boolean isRedisConnected = isRedisConnected();
		if (isRedisConnected && redisUtilSystemPreferences != null) {
			for (ControlPreferences controlPref : listControlPreferences) {
				
				LOGGER.debug( MessageFormat.format("Loading {0}  {1} {2} - >", controlPref.getControlCode(),controlPref.getNetworkCode(),controlPref.getPreferenceCode(),
						controlPref.getValue()));
				redisUtilSystemPreferences.putMap(Constants.VMS_CONTROL_PREFERENCES.getStrValue(),
						Constants.VMS_CONTROL_PREFERENCES.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue() +
						controlPref.getControlCode()+controlPref.getNetworkCode() + controlPref.getPreferenceCode() ,
						controlPref);
			}
		
		}
		
	}

	
	@Override
	public String getControlPreferenceValue(String controlCode, String networkCode, String preferenceCode) {
		
		String returnValue=null;
		ControlPreferences controlPreferences = null;
		Boolean isRedisConnected = isRedisConnected();
		if (Boolean.TRUE.equals(isRedisConnected)) {
			if (redisUtilSystemPreferences != null) {
				controlPreferences = (ControlPreferences) redisUtilSystemPreferences.getMapAsSingleEntry(
						Constants.VMS_CONTROL_PREFERENCES.getStrValue(), Constants.VMS_CONTROL_PREFERENCES.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() +controlCode+ networkCode+preferenceCode);
		
			}
		}
		
		if (controlPreferences == null) {
			controlPreferences = controlPreferenceRepo.getControlPreferenceCodeByCode(controlCode,networkCode, preferenceCode);
			if (Boolean.TRUE.equals(isRedisConnected) && controlPreferences != null
					&& redisUtilSystemPreferences != null) {
				redisUtilSystemPreferences.putMap(
						Constants.VMS_CONTROL_PREFERENCES.getStrValue(), Constants.VMS_CONTROL_PREFERENCES.getStrValue()
								+ Constants.APPEND_TO_SEQ.getStrValue() +controlCode+ networkCode+preferenceCode,controlPreferences
								);
			    returnValue =controlPreferences.getValue();
			    if(CommonUtils.isNullorEmpty(controlPreferences.getValue())) {
			    	returnValue =getSystemPreferenceValue(preferenceCode);
			    }
				
			} else {
				if (!BTSLUtil.isNullObject(controlPreferences))
					return controlPreferences.getValue();
				else return null;
			}
		}else {
			returnValue =controlPreferences.getValue();
		}
	
		return returnValue;
		
	}
	
	@Override
	public Void loadAllMessageGateway() {
		List<MessageGatewayVONew> listMessageGateway = messageGatewayRepository.loadMessageGatewayCacheQry(null);
		for(MessageGatewayVONew msgGateWay : listMessageGateway) {
          LOGGER.debug( MessageFormat.format("Message gateway {0} -> {1}",msgGateWay.getGatewayCode(),msgGateWay.getGatewayType() ));
  			redisUtilSystemPreferences.putMap(Constants.VMS_LOCAL_MESSAGEGATEWAY.getStrValue(),
					Constants.VMS_LOCAL_MESSAGEGATEWAY.getStrValue() + Constants.APPEND_TO_SEQ.getStrValue()
							+ msgGateWay.getGatewayCode(),
							msgGateWay);
			
		}
		return null;
	}

	
	
	@Override
	public String  getChecksumKey() {
		return aeesKay;
	}

	@Override
	public void blackListJWTToken(String jwtToken) {
		Boolean isRedisConnected = isRedisConnected();
		if (Boolean.TRUE.equals(isRedisConnected) && redisUtilSystemPreferences != null) {
			redisUtilSystemPreferences.putMap(
					Constants.BLACKLIST_JWT_TOKEN.getStrValue(), Constants.BLACKLIST_JWT_TOKEN.getStrValue()
							+ Constants.APPEND_TO_SEQ.getStrValue() +jwtToken,jwtToken);
			
		}
		
		
	}
	
	@Override
	public boolean isLoggedOutToken(String jwtToken) {
		Boolean isRedisConnected = isRedisConnected();
		boolean loggedOutToken =false;
		String logoutToken=null;
		if (Boolean.TRUE.equals(isRedisConnected) && redisUtilSystemPreferences != null) {
			logoutToken =(String) redisUtilSystemPreferences.getMapAsSingleEntry(
					Constants.BLACKLIST_JWT_TOKEN.getStrValue(), Constants.BLACKLIST_JWT_TOKEN.getStrValue()
							+ Constants.APPEND_TO_SEQ.getStrValue() +jwtToken);
			 if(!CommonUtils.isNullorEmpty(logoutToken)) {
				 loggedOutToken=true; 
			 }
		}
		return loggedOutToken;
	}
	
	
	
}
