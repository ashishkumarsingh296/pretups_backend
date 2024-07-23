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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import com.btsl.login.LoginLoggerVO;
import com.btsl.pretups.common.PretupsI;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.login.LoginDAO;
import com.btsl.oauth.businesslogic.OAuthDao;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
//import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.entity.Networks;
import com.btsl.user.businesslogic.entity.UsersLoginInfo;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
//import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;
import com.restapi.common.TokenRequestVO;
import com.restapi.common.TokenResponseVO;
import com.restapi.oauth.services.LocaleRequest;
import com.restapi.oauth.services.LocaleResponse;
import com.restapi.oauth.services.NonceValidatorService;

/**
 * This service used for User Login.
 *
 * @author VENKATESAN.S
 */
@Service
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoginService  /*extends CommonService*/  {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
	/*
	 * @Value(PropertiesConstant.AEKE_CLIENT_LAYER) private String aeesKay;
	 */ 
	/* @Value(PropertiesConstant.GENERATE_TOKEN_URL) private String
	 generateTokenUrl;*/

	private LoginRequest loginRequest;
	private ChannelUserVO channelUser;

	/** The response language. */
	protected Integer responseLanguage;


	/** The response status. */
	protected String responseStatus;


	@Autowired
	private LocaleTranslator localeTranslator;


	@Autowired private LoginUsersRespository loginUsersRespository;

	@Autowired private VMSCacheRepository vmsCacheRepository;


	 @Autowired private UsersRepository usersRepository;

	 @Autowired private GeographicalDomainRepository geographicalDomainRepository;

	  @Autowired private DomainRepository domainRepository;

	 @Autowired private ProductTypeRepository productTypeRepository;

	 @Autowired private MenuRespository menuRespository;

	 @Autowired private NetworkRepository networkRepository;


	 /* @Autowired private TokenProcessor tokenProcessor;
	 */
	 @Autowired private ServicesTypeRepository servicesTypeRepository;


	 @Autowired private UsersLoginQueryRep usersLoginRepository;

	 @Autowired private NetworksRespository networksRepository;

	// private String roleAssignment;
	// private TokenData jwtToken;
	private String sessionIdentifier;
	private String genToken;
	private String refToken;
	@SuppressWarnings("rawtypes")
	private ArrayList menuItemsResult = new ArrayList();

	private String requestGatewayType;
	private String requestGatewayCode;
	private String requestGatewayLoginId;
	private String requestGatewayPassword;
	private String servicePort;
	private String language;
	private boolean pWDBlocedkExpired = false;
	private boolean isFirstTimeLogin = false;
	private Date modifiedOn = null;
	private boolean changePassword = false;

	/** The reference id. */
	protected String referenceId;

	/** The response message. */
	protected String responseMessage;

	/** The response message code. */
	protected String responseMessageCode;
	/**
	 * Construct LoginService.
	 *
	 * @param loginUsersRespository
	 *            - loginUsersRespository
	 * @param vmsCacheRepository
	 *            - vmsCacheRepository
	 * @param usersRepository
	 *            - usersRepository
	 * @param geographicalDomainRepository
	 *            - geographicalDomainRepository
	 * @param domainRepository
	 *            - domainRepository
	 * @param productTypeRepository
	 *            - productTypeRepository
	 * @param servicesTypeRepository
	 *            - servicesTypeRepository
	 */
	/*
	 * @Autowired public LoginService(LoginUsersRespository loginUsersRespository,
	 * VMSCacheRepository vmsCacheRepository, UsersRepository usersRepository,
	 * GeographicalDomainRepository geographicalDomainRepository, DomainRepository
	 * domainRepository, ProductTypeRepository productTypeRepository,
	 * ServicesTypeRepository servicesTypeRepository) { this.loginUsersRespository =
	 * loginUsersRespository; this.vmsCacheRepository = vmsCacheRepository;
	 * this.usersRepository = usersRepository; this.geographicalDomainRepository =
	 * geographicalDomainRepository; this.domainRepository = domainRepository;
	 * this.productTypeRepository = productTypeRepository;
	 * this.servicesTypeRepository = servicesTypeRepository; }
	 */

	/**
	 * Operation of LoginService
	 *
	 * @param loginRequest
	 *            - request
	 * @return LoginResponse
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("rawtypes")
	public LoginResponse execute(String requestGatewayType, String requestGatewayCode, String requestGatewayLoginId,
								 String requestGatewayPassword, String servicePort, LoginRequest loginRequest, String language, HttpServletRequest httpServletRequest, LoginLoggerVO loginLoggerVO) throws BTSLBaseException {
		LOGGER.info("LoginService.execute start");
		this.loginRequest = loginRequest;
		this.requestGatewayType = requestGatewayType;
		this.requestGatewayCode = requestGatewayCode;
		this.requestGatewayLoginId = requestGatewayLoginId;
		this.requestGatewayPassword = requestGatewayPassword;
		this.language = language;
		this.servicePort = servicePort;
		validateInputs();
		Locale locale = LocaleContextHolder.getLocale();
		locale.getLanguage();
		UUID sessionID = UUID.randomUUID();
		MessageGatewayVONew messageGateway = parseRequest(loginRequest, sessionID);

		// removing nonce from login id - joined by "."
		String encryptedLoginId = Cryptojs.decrypt(loginRequest.getUserLoginId(),
				com.btsl.util.Constants.getProperty("AEKE_CLIENT_LAYER"));
		String encryptedLoginIdSplit[] = encryptedLoginId.split("\\.");
		if (encryptedLoginIdSplit.length > 1) {
			NonceValidatorService nonceValidatorService = (NonceValidatorService) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST").getBean(NonceValidatorService.class);
	        nonceValidatorService.validateNonce(encryptedLoginIdSplit[1]);
			loginRequest.setUserLoginId(encryptedLoginIdSplit[0]);
		}else {
			loginRequest.setUserLoginId("");
		}
		loginLoggerVO.setLoginID(loginRequest.getUserLoginId());
		channelUser = loadUserDetails(loginRequest.getUserLoginId());
		loginLoggerVO=prepareLoginLoggerVO(channelUser, loginLoggerVO);
		Connection con = null;
		MComConnectionI mcomCon = null;

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

		UserDAO userDAO = new UserDAO();
		String pass = AESEncryptionUtil.aesDecryptor(loginRequest.getUserPassword(), com.btsl.util.Constants.A_KEY);
		pass = AESEncryptionUtil.aesDecryptor(pass.substring(0, pass.lastIndexOf(".")), com.btsl.util.Constants.A_KEY);


		UserVO userVO = userDAO.loadUsersDetailsByidentifierType(con, "loginId", loginRequest.getUserLoginId(), pass, httpServletRequest);


		if (!BTSLUtil.isNullString(userVO.getValidRequestURLs())) {
            String requestUrl = httpServletRequest.getRemoteAddr();
            StringTokenizer requestUrlTokens = new StringTokenizer(requestUrl, ",");
            boolean found = false;
            String tokenValue = null;
            while (requestUrlTokens.hasMoreTokens()) {
                tokenValue = requestUrlTokens.nextToken();
                if (userVO.getValidRequestURLs().indexOf(tokenValue) != -1) {
                    found = true;
                    break;
                }
            }
            if (!found) {
            	String[] errorMsgParam = { loginRequest.getUserLoginId() };

                throw new BTSLBaseException(LoginService.class, "LoginService", PretupsErrorCodesI.AUTHENDICATION_ERROR_ALLOWED_IP, errorMsgParam );
            }
        }

		}catch(BTSLBaseException be) {
			throw be;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(mcomCon !=null) {
				try {mcomCon.close("Closing connection.");}catch(Exception e) {}
			}
		}


		// removing nonce from password - joined by "."
				String doubleEncryptedPassword = Cryptojs.decrypt(loginRequest.getUserPassword(),
						com.btsl.util.Constants.getProperty("AEKE_CLIENT_LAYER"));
				String doubleEncryptedPasswordSplit[] = doubleEncryptedPassword.split("\\.");
				if (doubleEncryptedPasswordSplit.length > 1) {
					NonceValidatorService nonceValidatorService = (NonceValidatorService) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST").getBean(NonceValidatorService.class);
			        nonceValidatorService.validateNonce(doubleEncryptedPasswordSplit[1]);
					loginRequest.setUserPassword(doubleEncryptedPasswordSplit[0]);
				}else {
					loginRequest.setUserPassword("");
				}

		validateUserLoginDetails();
		channelUser.setMessageGateway(messageGateway);

		String uniqueSessionID = channelUser.getUserId() + "-" + sessionID;

		/*
		 * ConfigParams configParams = new ConfigParams();
		 * configParams.setUniqueSessionId(uniqueSessionID);
		 * configParams.setLanguageSelected(language.toLowerCase());
		 * VMSSessionHolder.put(configParams);
		 */

		validateUserCommonLoginDetails(channelUser, loginRequest);
		checkUserSuspendedGroupRole(channelUser.getUserId());

		generateToken(channelUser, requestGatewayType, requestGatewayCode, requestGatewayLoginId,
				requestGatewayPassword, servicePort);

		// TokenData tokenData = null;
		// try {
		// tokenData = tokenProcessor.generateToken(uniqueSessionID);
		// } catch (UnsupportedEncodingException e) {
		// LOGGER.error("LoginService.execute start", e);
		// throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		//
		// }
		LoggedInUserInfo loggedInUserInfo = new LoggedInUserInfo();
		loggedInUserInfo.setUserID(channelUser.getUserId());
		loggedInUserInfo.setLoginIdentifier(uniqueSessionID);
		loggedInUserInfo.setLanguage(language.toLowerCase());
		// loggedInUserInfo.setJwttoken(tokenData.getAccess_token());
		// loggedInUserInfo.setRefreshToken(tokenData.getRefresh_token());
		loggedInUserInfo.setNetworkCode(channelUser.getNetworkCode());
		loggedInUserInfo.setCategoryCode(channelUser.getCategoryCode());
		loggedInUserInfo.setUserName(channelUser.getUserName());

//check		CachedObject co = new CachedObject(loggedInUserInfo, uniqueSessionID, NumberConstants.ZERO.getIntValue());
//check		CacheManager.putCache(co);

		// jwtToken = tokenData;
		sessionIdentifier = uniqueSessionID;

		 UsersLoginInfo userLoginInfo = new UsersLoginInfo();
		userLoginInfo.setFirstLoginJti(uniqueSessionID);
		userLoginInfo.setCategoryCode(channelUser.getCategoryCode());
		userLoginInfo.setLoginID(channelUser.getLoginId());
		userLoginInfo.setUserID(channelUser.getUserId());
		userLoginInfo.setNetworkCode(channelUser.getNetworkCode());

		Calendar calendar = Calendar.getInstance();
		Date createdOn = calendar.getTime();
		calendar.add(Calendar.SECOND, Integer.parseInt("3000"));
		Date expireTime = calendar.getTime();
		userLoginInfo.setCreatedOn(createdOn);
		userLoginInfo.setExpiryTokenTime(expireTime);
		saveUserLoginInfo(userLoginInfo);

		Date lastLoginOn = Calendar.getInstance().getTime();
		updateuserLastloginInfo(lastLoginOn, channelUser.getLoginId());


		// getMenuItemList(channelUser);
		/*
		 * Roles and menu set in User
		 */

		//ArrayList menuList = getMenuItemList(channelUser);
		ArrayList menuList = getMenuItemListVms(channelUser);



		Map<String, HashMap<String, ArrayList<UserRolesVO>>> menuListNew = new LinkedHashMap<>();
		menuListNew = menuRespository.getNewMenuItemList(channelUser);

		Boolean isMenuListEmpty = (menuList != null && menuList.size() == 0) || menuList == null;
		Boolean isMenuListNewEmpty = (menuListNew != null && menuListNew.size() == 0) || menuListNew == null;

		if (isMenuListEmpty && isMenuListNewEmpty) {
//			throw new ApplicationException(MessageCodes.NO_ROLE_DEFINED.getStrValue());//priyank:coz for no role assigned will show home
			//throw new Exception("MessageCodes.NO_ROLE_DEFINED.getStrValue()");
		}

		channelUser.setMenuItemList(menuList);
		channelUser.setMenuItemListNew(menuListNew);

		/*
		 * load the geographies info from the userGeographies
		 */

		channelUser.setGeographicalAreaList(geographicalDomainRepository.loadUserGeographyList(channelUser.getUserId(),
				channelUser.getNetworkCode()));


		/*
		 * load the domain of the user that are associated with it
		 */
		ArrayList<ListValues> domainlist = domainRepository.loadDomainListByUserId(channelUser.getUserId());
		channelUser.setDomainList(domainlist);
		/*
		 * load the services info from the user_services table that are assigned to the
		 * user
		 */
		channelUser.setServiceList(servicesTypeRepository.loadUserServicesList(channelUser.getUserId()));


		if (Constants.YES.getStrValue().equals(channelUser.getCategories().getProductTypesAllowed())) {

			ArrayList associatedProductlist = productTypeRepository
					.loadUserProductsListForLogin(channelUser.getUserId());
			channelUser.setAssociatedProductTypeList(associatedProductlist);
		} else {
			channelUser.setAssociatedProductTypeList(
					vmsCacheRepository.loadLookupDropDown(Constants.PRODUCT_TYPE.getStrValue()));

		}
		List<NetworksVO> ntwrkLst = null;
		// superAdminCheck
		channelUser.setIssuperUser(false);
		if (Constants.SUPER_ADMIN.getStrValue().equals(channelUser.getCategoryCode())
				|| Constants.SUPER_NETWORK_ADMIN.getStrValue().equals(channelUser.getCategoryCode())
				|| Constants.SUPER_CHANNEL_ADMIN.getStrValue().equals(channelUser.getCategoryCode())
				|| Constants.SUPER_CUSTOMER_CARE.getStrValue().equals(channelUser.getCategoryCode())) {
			channelUser.setIssuperUser(true);
			ntwrkLst = superadminCheck(channelUser);
		} else {
			Networks networks = networksRepository.getDataById(channelUser.getNetworkCode());
			channelUser.setHomeNetwork(channelUser.getNetworkCode());
			channelUser.setHomeNetworkName(networks.getNetworkName());
		}

		channelUser.setCommanNetworkList(ntwrkLst);
		/* check channelUser.setRightClickEnable(ConstantProperties.getProperty(Constants.RIGHT_CLICK_ENABLE.getStrValue()));
		channelUser.setCalendarType(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.CALENDAR_TYPE.getType()));*/
		responseStatus = MessageCodes.SUCCESS.toString();//Constants.SUCCESS_RESPONSE.getStrValue();
		responseMessageCode = MessageCodes.SUCCESS.toString();//.getStrValue();

		responseLanguage = CacheManager.getLanguageCode(loggedInUserInfo.getLanguage());
		List<String> params = new ArrayList<>();
		generateReponseMessage(params);
		LOGGER.info("LoginService.execute end");
		return createResponse();

	}


	public Boolean updateuserLastloginInfo(Date lastLoginOn, String loginId) {
		try {
			usersRepository.updateuserLastlogin(lastLoginOn, loginId);
		} catch (ApplicationException ex) {
			LOGGER.error("Unable to save Users last login time info", ex);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return true;
	}

	public Boolean saveUserLoginInfo(UsersLoginInfo userloginInfo) {
		try {
			usersLoginRepository.saveUsersLoginInfo(userloginInfo);
		} catch (ApplicationException ex) {
			LOGGER.error("Unable to save Users Login info", ex);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	private ArrayList getMenuItemList(String userId, String categoryCode, String roleAssignment, String roleType,
									  String domainType) {
		StringBuilder msg = new StringBuilder("");
		msg.append("Entered with userId=");
		msg.append(userId);
		msg.append(" categoryCode=");
		msg.append(categoryCode);
		msg.append(" roleAssignment=");
		msg.append(roleAssignment);
		msg.append(" roleType=");
		msg.append(roleType);
		msg.append(" domainType=");
		msg.append(domainType);
		String message = msg.toString();
		LOGGER.debug(MessageFormat.format("removeRoles {0}", message));
		ArrayList menuItemList = new ArrayList<>();
		try {
			if (roleAssignment.equals(Constants.FIXED.getStrValue())) {
				menuItemList = menuRespository.loadFixedMenuItemList(categoryCode, roleType, domainType);
			} else if (roleAssignment.equals(Constants.ASSIGNED.getStrValue())) {
				menuItemList = menuRespository.loadAssignedMenuItemList(categoryCode, userId, roleType, domainType);
			}
			if (menuItemList != null) {
				menuItemList = updateLevel1MenuURLs(menuItemList);
			}

		} catch (ApplicationException e) {
			LOGGER.error("Exception occurs at getMenuItemList {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return menuItemList;

	}

	@SuppressWarnings("rawtypes")
	private ArrayList getMenuItemListVms(String userId, String categoryCode, String roleAssignment, String roleType,
									  String domainType) {
		StringBuilder msg = new StringBuilder("");
		msg.append("Entered with userId=");
		msg.append(userId);
		msg.append(" categoryCode=");
		msg.append(categoryCode);
		msg.append(" roleAssignment=");
		msg.append(roleAssignment);
		msg.append(" roleType=");
		msg.append(roleType);
		msg.append(" domainType=");
		msg.append(domainType);
		String message = msg.toString();
		LOGGER.debug(MessageFormat.format("removeRoles {0}", message));
		ArrayList menuItemList = new ArrayList<>();
		try {
			if (roleAssignment.equals(Constants.FIXED.getStrValue())) {
				menuItemList = menuRespository.loadAssignedMenuItemListVms(categoryCode, roleType, domainType);
			} else if (roleAssignment.equals(Constants.ASSIGNED.getStrValue())) {

				menuItemList = menuRespository.loadAssignedMenuItemListVms(categoryCode, userId, roleType, domainType);
			}


		} catch (ApplicationException e) {
			LOGGER.error("Exception occurs at getMenuItemList {}", e);
			throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
		}
		return menuItemList;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList updateLevel1MenuURLs(ArrayList menuItemList) {
		LOGGER.debug(MessageFormat.format("updateLevel1MenuURLs {0}", "Entered"));
		int sizeOfMenuItems = 0;
		if (!CommonUtils.isNullorEmpty(menuItemList)) {
			sizeOfMenuItems = menuItemList.size();
			for (int loop1 = 0; loop1 < sizeOfMenuItems; loop1++) {
				MenuItem menuLevel1 = (MenuItem) menuItemList.get(loop1);
				if (menuLevel1.isMenuItems() && menuLevel1.getLevel().equals(Constants.ONE.getStrValue())) {
					generateLevel2Menu(sizeOfMenuItems, menuItemList, menuLevel1);
				}
				this.menuItemsResult.add(menuLevel1);
			}
		} else {
			menuItemList = null;
		}
		LOGGER.debug(MessageFormat.format("updateLevel1MenuURLs {0}", "Exiting"));
		return (ArrayList) menuItemsResult.clone();
	}

	@SuppressWarnings("rawtypes")
	private MenuItem generateLevel2Menu(int sizeOfMenuItems, ArrayList menuItemList, MenuItem menuLevel1) {
		for (int loop2 = 0; loop2 < sizeOfMenuItems; loop2++) {
			MenuItem menuLevel2 = (MenuItem) menuItemList.get(loop2);
			if (menuLevel2.isMenuItems() && menuLevel1.getModuleCode().equals(menuLevel2.getModuleCode())) {
				menuLevel1.setUrl(menuLevel2.getUrl());
				break;
			}
		}
		return menuLevel1;
	}



	@SuppressWarnings("rawtypes")
	private ArrayList getMenuItemListVms(ChannelUserVO channelUser) {
		String roleAssignment = null;
		if (Constants.YES.getStrValue().equalsIgnoreCase(channelUser.getCategories().getFixedRoles())) {
			roleAssignment = Constants.MENUBL_ROLE_ASSIGNMENT_FIXED.getStrValue();
		} else {
			roleAssignment = Constants.MENUBL_ROLE_ASSIGNMENT_ASSIGNED.getStrValue();
		}
		ArrayList menuItemList = getMenuItemListVms(channelUser.getActiveUserID(), channelUser.getCategoryCode(),
				roleAssignment, com.btsl.util.Constants.getProperty("ROLE_TYPE"), channelUser.getDomainTypeCode());
		if (CommonUtils.isNullorEmpty(menuItemList)) {
			menuItemList = null;
		}
		if (channelUser.isStaffUser()) {
			ArrayList menuItemListParent = getMenuItemList(channelUser.getUserId(), channelUser.getCategoryCode(),
					roleAssignment, com.btsl.util.Constants.getProperty("ROLE_TYPE"), channelUser.getDomainTypeCode());
			if (CommonUtils.isNullorEmpty(menuItemListParent)) {
				menuItemListParent = null;
			}
			if (menuItemListParent != null && menuItemList != null) {
				removeRoles(menuItemList, menuItemListParent);
			}

		}
		return menuItemList;
	}



	@SuppressWarnings("rawtypes")
	private ArrayList getMenuItemList(ChannelUserVO channelUser) {
		String roleAssignment = null;
		if (Constants.YES.getStrValue().equalsIgnoreCase(channelUser.getCategories().getFixedRoles())) {
			roleAssignment = Constants.MENUBL_ROLE_ASSIGNMENT_FIXED.getStrValue();
		} else {
			roleAssignment = Constants.MENUBL_ROLE_ASSIGNMENT_ASSIGNED.getStrValue();
		}
		ArrayList menuItemList = getMenuItemList(channelUser.getActiveUserID(), channelUser.getCategoryCode(),
				roleAssignment, com.btsl.util.Constants.getProperty("ROLE_TYPE"), channelUser.getDomainTypeCode());
		if (CommonUtils.isNullorEmpty(menuItemList)) {
			menuItemList = null;
		}
		if (channelUser.isStaffUser()) {
			ArrayList menuItemListParent = getMenuItemList(channelUser.getUserId(), channelUser.getCategoryCode(),
					roleAssignment, com.btsl.util.Constants.getProperty("ROLE_TYPE"), channelUser.getDomainTypeCode());
			if (CommonUtils.isNullorEmpty(menuItemListParent)) {
				menuItemListParent = null;
			}
			if (menuItemListParent != null && menuItemList != null) {
				removeRoles(menuItemList, menuItemListParent);
			}

		}
		return menuItemList;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void removeRoles(ArrayList menuItemList, ArrayList menuItemListParent) {
		if (LOGGER.isDebugEnabled()) {
			StringBuilder msg = new StringBuilder();
			msg.append("Entered p_menuItemList.size()=");
			msg.append(menuItemList.size());
			msg.append("p_menuItemListParent.size()=");
			msg.append(menuItemListParent.size());

			String message = msg.toString();
			LOGGER.debug(MessageFormat.format("removeRoles {0}", message));
		}
		MenuItem itemParent = null;
		MenuItem item = null;
		List removeList = new ArrayList<Integer>();
		boolean isExist = false;
		for (int i = 0, j = menuItemList.size(); i < j; i++) {
			isExist = false;
			item = (MenuItem) menuItemList.get(i);
			for (int k = 0, l = menuItemListParent.size(); k < l; k++) {
				itemParent = (MenuItem) menuItemListParent.get(k);
				if (itemParent.getRoleCode().equals(item.getRoleCode())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				removeList.add(i);
			}
		}
		for (int i = 0; i < removeList.size(); i++) {
			menuItemList.remove(removeList.get(i));
		}

		if (LOGGER.isDebugEnabled()) {
			StringBuilder msg = new StringBuilder();
			msg.append("Exited menuItemList.size()=");
			msg.append(menuItemList.size());
			msg.append("menuItemListParent.size()=");
			msg.append(menuItemListParent.size());
			String message = msg.toString();
			LOGGER.debug(MessageFormat.format("removeRoles {0}", message));
		}
	}


	/**
	 * Construct for LoginResponse
	 *
	 * @return LoginResponse
	 */
	//@Override
	public LoginResponse createResponse() {
		LoginResponse response = new LoginResponse();

		response.setStatus(responseStatus);
		response.setExternalRefId(loginRequest.getExternalRefId());

		ChannelUserInfo uiResponse = new ChannelUserInfo();
		try {
			uiResponse.setMenuItemListNew(channelUser.getMenuItemListNew());
			ChannelUserVO channelUserCopy = (ChannelUserVO) SerializationUtils.clone(channelUser);

			BeanUtils.copyProperties(uiResponse, channelUserCopy);

			uiResponse.setModifiedOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getModifiedOn()));
			uiResponse.setCreatedOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getCreatedOn()));
			uiResponse.setLastLoginOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getLastLoginOn()));
			uiResponse.setPswdModifiedOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getPswdModifiedOn()));
			uiResponse
					.setLevel1ApprovedOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getLevel1ApprovedOn()));
			uiResponse
					.setLevel2ApprovedOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getLevel2ApprovedOn()));
			uiResponse.setAppointmentDate(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getAppointmentDate()));
			uiResponse.setPasswordCountUpdatedOn(
					BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getPasswordCountUpdatedOn()));
			uiResponse.setActivatedOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getActivatedOn()));
			//uiResponse.setUserBalanceList(channelUserCopy.getUserBalanceList());
			List<NetworksInfo> listNetworkInfo = new ArrayList<>();
			if (channelUser.isIssuperUser()) {
				for (int i = 0; i < channelUser.getCommanNetworkList().size(); i++) { // Networklist is transient
					NetworksVO networkVo = channelUser.getCommanNetworkList().get(i);
					NetworksInfo networkInfo = new NetworksInfo();
					BeanUtils.copyProperties(networkInfo, networkVo);
					networkInfo.setCreatedOn(BTSLDateUtil.getLocaleDateTimeFromDate(networkVo.getCreatedOn()));
					networkInfo.setModifiedOn(BTSLDateUtil.getLocaleDateTimeFromDate(networkVo.getModifiedOn()));
					networkInfo.setMisDoneDate(BTSLDateUtil.getLocaleDateTimeFromDate(networkVo.getMisDoneDate()));
					listNetworkInfo.add(networkInfo);
				}
				uiResponse.setCommanNetworkList(listNetworkInfo);
			}
			MessageGatewayInfo messageGateWayInfo = new MessageGatewayInfo();

			BeanUtils.copyProperties(messageGateWayInfo, channelUserCopy.getMessageGateway());
			messageGateWayInfo.setCreatedOn(
					BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getMessageGateway().getCreatedOn()));
			messageGateWayInfo.setModifiedOn(
					BTSLDateUtil.getLocaleDateTimeFromDate(channelUserCopy.getMessageGateway().getCreatedOn()));
			messageGateWayInfo.setModifiedOnTimestamp(BTSLDateUtil
					.getLocaleDateTimeFromDate(channelUserCopy.getMessageGateway().getModifiedOnTimestamp()));



			uiResponse.setMessageGatewayinfo(messageGateWayInfo);
			response.setChannelUserInfo(uiResponse);
			response.setReferenceId(referenceId);
			response.setMessage(responseMessage);
			response.setMessageCode(responseMessageCode);
			//response.setJwtaccessToken(jwtToken);
			response.setGenToken(genToken);
			response.setRefreshToken(refToken);
			response.setClientDateFormat(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.SYSTEM_DATE_FORMAT.getType()));
			response.setClientDateTimeFormat(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.SYSTEM_DTTIME_FORMAT.getType()));
			response.setCalendarType(
					(vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.CALENDAR_TYPE.getType())));

			response.setUniqueSessionIDforlogin(sessionIdentifier);
			response.setChangePassword(changePassword);
			response.setServerTime(new Date());
		} catch (IllegalAccessException e) {
			LOGGER.error("Error occured in login service", e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occured in login service", e);
		}

		return response;
	}

	/**
	 * Input validation
	 */




	/**
	 * Operation of LoginService
	 *
	 * @param GetAllDenomRequest - request
	 * @return LoginResponse
	 */
	public LocaleResponse getLocaleList(LocaleRequest localeRequest) {
		LOGGER.debug(
				MessageFormat.format("LoginService.getLocaleList start {0}", localeRequest.getExternalRefId()));
		List<String> params = new ArrayList<>();
		LocaleResponse localeResponse = new LocaleResponse();
		List<LocaleMasterModal> listlocaleMaster = vmsCacheRepository.loadLocaleMaster();
		localeResponse.setListlocaleMaster(listlocaleMaster);
		responseStatus = Constants.SUCCESS_RESPONSE.getStrValue();
		responseMessageCode = MessageCodes.SUCCESS.getStrValue(); // MessageCodes.LOCALE_LIST_SUCCESS.getStrValue();
		responseLanguage = 1;
		generateReponseMessage(params);
		LOGGER.info("LoginService.getLocaleList end");
		localeResponse.setMessageCode(responseMessageCode);
		localeResponse.setStatus(responseStatus);
		localeResponse.setMessage(responseMessage);
		return localeResponse;

	}


	//@Override
	public void validateInputs() {
		if (CommonUtils.isNullorEmpty(loginRequest.getUserLoginId())) {
			LOGGER.error("userLoginID. User LoginID field invalid");

			throw new ValidationException(Constants.USER_LOGIN_ID.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}
		if (CommonUtils.isNullorEmpty(loginRequest.getUserPassword())) {
			LOGGER.error("userPassword. User Password field invalid");
			throw new ValidationException(Constants.USER_PASSWORD.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}

		if (CommonUtils.isNullorEmpty(language)) {
			LOGGER.error("userLang. Language field invalid");
			throw new ValidationException(Constants.USER_LANGUAGE.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}

		if (CommonUtils.isNullorEmpty(requestGatewayType)) {
			LOGGER.error("requestGatewayType. Request Gateway Type field invalid");
			throw new ValidationException(Constants.REQUEST_GATEWAY_TYPE.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}
		if (CommonUtils.isNullorEmpty(requestGatewayCode)) {
			LOGGER.error("requestGatewayCode. Request Gateway Code field invalid");
			throw new ValidationException(Constants.REQUEST_GATEWAY_CODE.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}
		if (CommonUtils.isNullorEmpty(requestGatewayLoginId)) {
			LOGGER.error("requestGatewayCode. Request Gateway Code field invalid");
			throw new ValidationException(Constants.REQUEST_GATEWAY_LOGING_ID.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}

		if (CommonUtils.isNullorEmpty(requestGatewayPassword)) {
			LOGGER.error("requestGatewayCode. Request Gateway Code field invalid");
			throw new ValidationException(Constants.REQUEST_GATEWAY_PASSWORD.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}

		if (CommonUtils.isNullorEmpty(servicePort)) {
			LOGGER.error("requestGatewayCode. Request Gateway Code field invalid");
			throw new ValidationException(Constants.REQUEST_GATEWAY_SERVERPORT.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}

	}

	/**
	 * MessageGateway validation for Input request
	 */
	private MessageGatewayVONew parseRequest(LoginRequest loginRequest, UUID sessionID) {
		MessageGatewayVONew messageGt = new MessageGatewayVONew();
		messageGt = vmsCacheRepository.loadMessageGatewayCacheQry(requestGatewayCode);
		if (CommonUtils.isNullorEmpty(messageGt)) {
			LOGGER.error("messageGateway. User LoginID field invalid");
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
			throw new ValidationException(Constants.REQUEST_GATEWAY_CODE.getStrValue(),
					MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		} else {
			if (!(Constants.ACTIVE_STATUS.getStrValue().equals(messageGt.getStatus()))) {
				LOGGER.error("messageGateway. User LoginID field invalid");
				responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
				throw new ValidationException(Constants.REQUEST_GATEWAY_CODE.getStrValue(),
						MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
			}
		}

		ReqMessageGatewayVO requestGatewayVO = messageGt.getReqMessageGatewayVO();
		if (requestGatewayVO == null || !(Constants.ACTIVE_STATUS.getStrValue().equals(requestGatewayVO.getStatus()))) {
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
			throw new ValidationException(Constants.REQUEST_GATEWAY_TYPE.getStrValue(),
					MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		}

		if (!requestGatewayType.equalsIgnoreCase(messageGt.getGatewayType())) {
			LOGGER.error("messageGateway. User LoginID field invalid");
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
			throw new ValidationException(Constants.REQUEST_GATEWAY_TYPE.getStrValue(),
					MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		}
		ResMessageGatewayVO responseGatewayVO = parseRequest1(messageGt, requestGatewayVO);

		StringBuilder msg = new StringBuilder("");
		msg.append("http://");
		msg.append(messageGt.getHost());
		msg.append(":");
		msg.append(responseGatewayVO.getPort());
		msg.append("/");
		msg.append(responseGatewayVO.getPath());
		String responseUrl = msg.toString();
		File file = new File(com.btsl.util.Constants.getProperty("COMMON_LOGIN_FILE_PATH"));
		System.out.println("abc" + com.btsl.util.Constants.getProperty("COMMON_LOGIN_FILE_PATH"));
		if (!file.isDirectory()) {
			boolean flag;
			flag = file.mkdirs();
			if (!flag) {
				throw new ValidationException("COMMON_LOGIN_FILE_PATH",
						MessageCodes.DIRECTORY_NOT_CREATED.getStrValue());
			}
		}

		try (ObjectOutput out = new ObjectOutputStream(
				new FileOutputStream(com.btsl.util.Constants.getProperty("COMMON_LOGIN_FILE_PATH") + sessionID + ".txt"))) {
			out.writeObject(responseUrl);
		} catch (IOException er) {
			LOGGER.info(" Exceptin:e=" + er);
			LOGGER.debug("LoginService", " Exceptin:e=" + er);
			LOGGER.error("LoginService", "Exceptin:e=" + er);
			LOGGER.trace("LoginService", er);
		}

		return messageGt;
	}

	public List<NetworksVO> superadminCheck(ChannelUserVO channelUser) {

		StringBuilder sAdmins = new StringBuilder();
		List<NetworksVO> networkList = null;
		sAdmins.append(Constants.SUPER_ADMIN.getStrValue());
		sAdmins.append("|");
		sAdmins.append(Constants.SUPER_NETWORK_ADMIN.getStrValue());
		sAdmins.append("|");
		sAdmins.append(Constants.SUPER_CHANNEL_ADMIN.getStrValue());
		sAdmins.append("|");
		sAdmins.append(Constants.SUPER_CUSTOMER_CARE.getStrValue());
		if (channelUser.getCategoryCode() != null && sAdmins.toString().contains(channelUser.getCategoryCode())) {
			StringBuilder status = new StringBuilder();
			status.append("'");
			status.append(Constants.STATUS_DELETE.getStrValue());
			status.append("'");
			if (Constants.NO.getStrValue().equals(channelUser.getCategories().getViewOnNetworkBlock())) {
				status.deleteCharAt(status.length() - 1);
				status.append("','");
				status.append(Constants.STATUS_SUSPEND.getStrValue());
				status.append("'");
			}

			networkList = getNetworkList(channelUser, status.toString());

			if (networkList == null || networkList.isEmpty()) {
				channelUser.setNetworkName(Constants.NETWORK_NAME_DEFAULT.getStrValue());
				channelUser.setNetworkId("");
			} else if (networkList.size() == 1 && !PretupsI.SUPER_ADMIN.equalsIgnoreCase(channelUser.getCategoryCode())) {
				NetworksVO networksVO = networkList.get(0);
				channelUser.setNetworkCode(networksVO.getNetworkCode());
				channelUser.setNetworkName(networksVO.getNetworkName());
				channelUser.setReportHeaderName(networksVO.getReportHeaderName());
				channelUser.setNetworkstatus(networksVO.getStatus());
				LocaleMasterModal localMasterModal = vmsCacheRepository.loadLocaleMasterByLangCntry(
						Constants.DEFAULT_LANGUAGE.getStrValue(), Constants.DEFAULT_COUNTRY.getStrValue());
				getUserGeoGraphies(channelUser, localMasterModal, networksVO);
			}

		} else {
			channelUser.setNetworkName(Constants.NETWORK_NAME_DEFAULT.getStrValue());
			channelUser.setNetworkId("");
		}
		return networkList;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getUserGeoGraphies(ChannelUserVO channelUser, LocaleMasterModal localMasterModal,
									NetworksVO networksVO) {
		if (Constants.LANG1_MESSAGE.getStrValue().equals(localMasterModal.getMessage())) {
			channelUser.setMessage(networksVO.getLanguage1Message());
		} else {
			channelUser.setMessage(networksVO.getLanguage2Message());
		}
		/*
		 * while change the network location also change the user geographical list becs
		 * while adding user we check the domain type of the loginUser and AddedUser if
		 * domain type is same of both the user then the geographical list of the added
		 * user is same as the geographical list of the login user
		 */
		UserGeographiesVO geographyVO = null;
		ArrayList geographyList = new ArrayList();
		geographyVO = new UserGeographiesVO();
		geographyVO.setGraphDomainCode(networksVO.getNetworkCode());
		geographyVO.setGraphDomainName(networksVO.getNetworkName());
		geographyList.add(geographyVO);
		channelUser.setUserGeographiesList(geographyList);

		if (Constants.SUPER_CHANNEL_ADMIN.getStrValue().equals(channelUser.getCategoryCode())) {
			List userGeoList = networkRepository.loadUserGeographyList(channelUser.getUserId(),
					channelUser.getNetworkCode());
			channelUser.setUserGeographiesList(userGeoList);

		}

	}


	private List<NetworksVO> getNetworkList(ChannelUserVO channelUser, String status) {
		List<NetworksVO> networkList = null;
		if (channelUser.getCategoryCode().equals(Constants.SUPER_NETWORK_ADMIN.getStrValue())
				|| channelUser.getCategoryCode().equals(Constants.SUPER_CUSTOMER_CARE.getStrValue())) {

			networkList = networkRepository.loadNetworkListForSuperOperatorUsers(channelUser.getUserId(), status);
		} else if (channelUser.getCategoryCode().equals(Constants.SUPER_CHANNEL_ADMIN.getStrValue())) {
			networkList = networkRepository.loadNetworkListForSuperChannelAdm(channelUser.getUserId(), status);
		} else {
			networkList = networkRepository.loadNetworkList(status);
		}

		return networkList;
	}

	/**
	 * Operation of loadUserDetails
	 *
	 * @param userLoginID
	 *            - request
	 * @param userPassword
	 *            - request
	 *
	 * @return ChannelUser
	 */
	private ChannelUserVO loadUserDetails(String userLoginID) {
		channelUser = loginUsersRespository.getloadUserDetails("loginid", userLoginID);

		channelUser.setLoginTime(new Date());
		channelUser.setLogOutTime(new Date());
		if (CommonUtils.isNullorEmpty(channelUser.getUserId())) {
			LOGGER.debug("Channel user is empty");
			throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
					MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());
		}
		// channelUser.setUserBalanceList(loginUsersRespository.getUserBalance(channelUser.getUserId()));
		channelUser.setAssociatedServiceTypeList(loginUsersRespository.loadUserServicesList(channelUser.getUserId()));
		if (!CommonUtils.isNullorEmpty(channelUser)) {
			channelUser.setActiveUserID(channelUser.getUserId());
			if (Constants.STAFF_USER_TYPE.getStrValue().equals(channelUser.getUserType())) {
				ChannelUserVO parentChannelUserVO = usersRepository
						.loadUserDetailsFormUserID(channelUser.getParentId());
				staffUserDetails(channelUser, parentChannelUserVO);
			}
		}
		if (!CommonUtils.isNullorEmpty(channelUser.getModifiedOn())) {
			modifiedOn = channelUser.getModifiedOn();
		}
		return channelUser;
	}

	/**
	 * Construct for LoginResponse
	 *
	 */
	private void staffUserDetails(ChannelUserVO channelUser, ChannelUserVO parentChannelUserVO) {
		channelUser.setUserId(channelUser.getParentId());
		channelUser.setParentId(parentChannelUserVO.getParentId());
		channelUser.setOwnerId(parentChannelUserVO.getParentId());
		channelUser.setStatus(channelUser.getStatus());
		channelUser.setUserType(parentChannelUserVO.getUserType());
		channelUser.setStaffMsisdn(channelUser.getMsisdn());
		channelUser.setStaffUser(true);
		channelUser.setMsisdn(parentChannelUserVO.getMsisdn());
		channelUser.setPinRequired(parentChannelUserVO.getPinRequired());
		channelUser.setSmsPin(parentChannelUserVO.getSmsPin());
		channelUser.setParentLoginId(parentChannelUserVO.getLoginId());
		channelUser.setUserCode(parentChannelUserVO.getUserCode());
	}

	private void validateUserLoginDetails() throws BTSLBaseException {
		String methodname = "validateUserLoginDetails";
		LOGGER.debug("hello");
		int invalidCount;
		try {
			invalidCount = Integer.parseInt(vmsCacheRepository.getControlPreferenceValue(channelUser.getCategoryCode(),
					channelUser.getNetworkId(), SystemPreferenceConstants.MAX_PASSWORD_BLOCK_COUNT.getType()));
		} catch (Exception e) {

			invalidCount = Integer.parseInt(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.MAX_PASSWORD_BLOCK_COUNT.getType()));
		}

		// int invalidCount = Integer.parseInt(vmsCacheRepository
		// .getSystemPreferenceValue(SystemPreferenceConstants.MAX_PASSWORD_BLOCK_COUNT.getType()));

		if (channelUser.getInvalidPasswordCount() == invalidCount) {
			/*
			 * long expiryTime =
			 * Long.parseLong(vmsCacheRepository.getControlPreferenceValue(channelUser.
			 * getCategoryCode(), channelUser.getNetworkId(),
			 * SystemPreferenceConstants.C2S_PWD_BLK_EXP_DURATION.getType()));
			 */

			long expiryTime = Long.parseLong(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.C2S_PWD_BLK_EXP_DURATION.getType()));

			if (BTSLUtil.isTimeExpired(channelUser.getPasswordCountUpdatedOn(), expiryTime)) {
				channelUser.setInvalidPasswordCount(1l);
				pWDBlocedkExpired = true;
			} else {
//				throw new ValidationException(Constants.PASSWORD_BLOCKED.getStrValue(),
//						MessageCodes.USER_PASSWORD_BLOCKED_ERROR.getStrValue());
				throw new BTSLBaseException(this, methodname,PretupsErrorCodesI.PASSWORD_BLOCKED);
			}
		}

		if (updatePasswordInvalidCount(channelUser, loginRequest)) {
			if (channelUser.getInvalidPasswordCount() == invalidCount) {
				throw new BTSLBaseException(this, methodname,PretupsErrorCodesI.PASSWORD_BLOCKED);
//				throw new ValidationException(Constants.PASSWORD_BLOCKED.getStrValue(),
//						MessageCodes.USER_PASSWORD_BLOCKED_ERROR.getStrValue());
			}
			throw new BTSLBaseException(this, methodname,PretupsErrorCodesI.AUTHENDICATION_ERROR);
//			throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
//					MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());
		} else if (channelUser.getInvalidPasswordCount() == invalidCount) {
			throw new BTSLBaseException(this, methodname,PretupsErrorCodesI.PASSWORD_BLOCKED);
//			throw new ValidationException(Constants.PASSWORD_BLOCKED.getStrValue(),
//					MessageCodes.USER_PASSWORD_BLOCKED_ERROR.getStrValue());
		}

        try {
			if (!BTSLUtil.isDayTimeValid(channelUser.getAllowedDays(), channelUser.getFromTime(), channelUser.getToTime())) {
				throw new BTSLBaseException(this, methodname,PretupsErrorCodesI.LOGIN_TIME_NOT_ALLOWED);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}


	private boolean updatePasswordInvalidCount(ChannelUserVO channelUser, LoginRequest loginRequest) throws BTSLBaseException {
		boolean passwordStatus = false;
		boolean passwordFlag;
		String encryptedPassword = channelUser.getPword();
		Date currentDate = new Date();
		channelUser.setModifiedOn(currentDate);
		if (LOGGER.isDebugEnabled()) {
			StringBuilder msg = new StringBuilder();
			msg.append("User Login Id:");
			msg.append(loginRequest.getUserLoginId());
			msg.append(" encrypted Password=");
			msg.append(encryptedPassword);
			msg.append(" entered Password=");
			msg.append(loginRequest.getUserPassword());
			String message = msg.toString();
			LOGGER.debug(MessageFormat.format("updatePasswordInvalidCount: {0} ", message));
		}

		String cryptionType = vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.PINPAS_EN_DE_CRYPTION_TYPE.getType());

		String plainPawd = Cryptojs.decrypt(loginRequest.getUserPassword(), com.btsl.util.Constants.getProperty("AEKE_CLIENT_LAYER"));
		String encryptedPawd = CommonUtils.encryptText(plainPawd, cryptionType);
		passwordFlag = (encryptedPawd.equals(channelUser.getPword()) || channelUser.getAuthenticationAllowed().equals(PretupsI.STATUS_ACTIVE));

		if (!passwordFlag) {
			passwordStatus = isPasswordStatus(channelUser);
		}else if(passwordFlag && !channelUser.getInvalidPasswordCount().equals(0L)){//code for password count reset
//			Long oldVal = channelUser.getInvalidPasswordCount();
			//loginUsersRespository.updatePasswordCounter(channelUser);
			channelUser.setInvalidPasswordCount(0L);;
			updatePasswordcou(channelUser);
//			channelUser.setInvalidPasswordCount(oldVal);
		}

		return passwordStatus;
	}


	public boolean isPasswordStatus(ChannelUserVO channelUser) throws BTSLBaseException {
		boolean passwordStatus = false;
		int hoursInday = NumberConstants.N24.getIntValue();
		int minutesInHour = NumberConstants.N60.getIntValue();
		long mintInDay = (long) (hoursInday * minutesInHour);
		if (channelUser.getPasswordCountUpdatedOn() != null) {
			String caltype = vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.CALENDAR_TYPE.getType());
			Calendar cal = CommonUtils.getInstance(caltype);
			cal.setTime(channelUser.getModifiedOn());
			int d1 = cal.get(Calendar.DAY_OF_YEAR);
			cal.setTime(channelUser.getPasswordCountUpdatedOn());
			int d2 = cal.get(Calendar.DAY_OF_YEAR);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(MessageFormat.format(
						"updatePasswordInvalidCount , Day Of year of Modified On= {0} ,Day Of year of PasswordCountUpdatedOn= {1}",
						d1, d2));
			}

			analyzedates(channelUser, d1, d2, mintInDay);
		} else {
			channelUser.setInvalidPasswordCount(1l);
			channelUser.setPasswordCountUpdatedOn(channelUser.getModifiedOn());
		}
		int updateStatus = updatePasswordcou(channelUser);
		if (updateStatus <= 0) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error(
						"Authentication Failure. Please try again. In case you continue having troubles accessing the platform please contact Customer Care");
			}
			throw new BTSLBaseException(this, "isPasswordStatus",PretupsErrorCodesI.AUTHENDICATION_ERROR);
//			throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
//					MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());
		}
		passwordStatus = true;
		return passwordStatus;
	}



	private int updatePasswordcou(ChannelUserVO channelUser) throws BTSLBaseException {
//		int updateStatus = loginUsersRespository.updatePasswordCounter(channelUser);
//		return updateStatus;
		return new LoginDAO().updateInvalidPassCount(channelUser);

	}


	private void analyzedates(ChannelUserVO channelUser, int d1, int d2, Long mintInDay) {
		final int sec = 60;
		final int millisec = 1000;

		if (d1 != d2 && Long.parseLong(vmsCacheRepository.getSystemPreferenceValue(
				SystemPreferenceConstants.PASSWORD_BLK_RST_DURATION.getType())) <= mintInDay) {
			channelUser.setInvalidPasswordCount(1l);
			channelUser.setPasswordCountUpdatedOn(channelUser.getModifiedOn());
		} else if (d1 != d2
				&& Long.parseLong(vmsCacheRepository.getSystemPreferenceValue(
				SystemPreferenceConstants.PASSWORD_BLK_RST_DURATION.getType())) >= mintInDay
				&& (d1 - d2) >= Long.parseLong(vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.PASSWORD_BLK_RST_DURATION.getType()))
				/ mintInDay) {
			channelUser.setInvalidPasswordCount(1l);
			channelUser.setPasswordCountUpdatedOn(channelUser.getModifiedOn());
		} else if (((channelUser.getModifiedOn().getTime() - channelUser.getPasswordCountUpdatedOn().getTime())
				/ (sec * millisec)) < Long
				.parseLong(vmsCacheRepository.getSystemPreferenceValue(
						SystemPreferenceConstants.PASSWORD_BLK_RST_DURATION.getType()))) {
			int invalidCount;
			try{
				invalidCount =
						Integer.parseInt(vmsCacheRepository.getControlPreferenceValue(channelUser.
										getCategoryCode(), channelUser.getNetworkId(),
								SystemPreferenceConstants.MAX_PASSWORD_BLOCK_COUNT.getType()));
			}catch(Exception e){
				invalidCount = Integer.parseInt(vmsCacheRepository
						.getSystemPreferenceValue(SystemPreferenceConstants.MAX_PASSWORD_BLOCK_COUNT.getType()));
			}

			if (channelUser.getInvalidPasswordCount() - invalidCount == 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("Your password has been blocked, please contact customer care");
				}
				throw new ValidationException(Constants.USER_PASSWORD_BLOCKED.getStrValue(),
						MessageCodes.USER_PASSWD_BLOCKED.getStrValue());
			}
			channelUser.setPasswordCountUpdatedOn(channelUser.getModifiedOn());
			if (!pWDBlocedkExpired) {
				channelUser.setInvalidPasswordCount(channelUser.getInvalidPasswordCount() + 1);
			} else {
				channelUser.setInvalidPasswordCount(channelUser.getInvalidPasswordCount());
			}
		} else {
			channelUser.setInvalidPasswordCount(1l);
			channelUser.setPasswordCountUpdatedOn(channelUser.getModifiedOn());
		}
	}


	private ResMessageGatewayVO parseRequest1(MessageGatewayVONew messageGateway, ReqMessageGatewayVO requestGatewayVO) {

		String encryptType = vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.PINPAS_EN_DE_CRYPTION_TYPE.getType());

		if (CommonUtils.isNullorEmpty(messageGateway)
				|| !(Constants.ACTIVE_STATUS.getStrValue().equals(requestGatewayVO.getStatus()))) {
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
			throw new ValidationException(Constants.REQUEST_GATEWAY_TYPE.getStrValue(),
					MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		}

		if (encryptType.equals(Constants.SHA.getStrValue())) {
			requestGatewayPassword = CommonUtils.encryptText(requestGatewayPassword, encryptType);
		}

		System.out.println(requestGatewayPassword + ">>>>>>>>>>>>>>" + requestGatewayVO.getDecryptedPassword());

		System.out.println("requestGatewayVO "+requestGatewayVO.toString());



		if (!requestGatewayPassword.equals(requestGatewayVO.getDecryptedPassword())) {
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
		//	throw new ValidationException(Constants.REQUEST_GATEWAY_TYPE.getStrValue(),
		//			MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		}
		ResMessageGatewayVO responseGatewayVO = messageGateway.getResMessageGatewayVO();

		if (CommonUtils.isNullorEmpty(responseGatewayVO)) {
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
			throw new ValidationException(Constants.REQUEST_GATEWAY_TYPE.getStrValue(),
					MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		}
		if (!BTSLUtil.NullToString(requestGatewayVO.getLoginID()).equals(requestGatewayLoginId)) {
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
			throw new ValidationException(Constants.REQUEST_GATEWAY_TYPE.getStrValue(),
					MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		}
		if (!requestGatewayVO.getServicePort().equals(servicePort)) {
			responseStatus = Constants.INTERNAL_SERVER_ERROR.getStrValue();
			throw new ValidationException(Constants.REQUEST_GATEWAY_SERVERPORT.getStrValue(),
					MessageCodes.GATWAY_USER_IS_NOT_ACTIVE.getStrValue());
		}
		return responseGatewayVO;
	}

	/*
	 * In the methos below we will validate the common details of user. The
	 * description is as: 1. Password is check if it is correct or not. If not
	 * correct then increse the invalid pin count. 2. Check if password is block. 3.
	 * Check the status of user. 4. Check the status of Domain and category. 5.
	 * Check if web interface is allowed to user or not. 6. Check for duplicate
	 * login. If duplicate login is not allowed then set the parameter in form bean.
	 */
	private void validateUserCommonLoginDetails(ChannelUserVO channelUser, LoginRequest loginRequest) throws BTSLBaseException {
		// int validStatus = 0;
		String methodname = "validateUserCommonLoginDetails";
		LOGGER.debug(MessageFormat.format("validateUserCommonLoginDetails {0}", "Entered"));

		int invalidCount = Integer.parseInt(vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.MAX_PASSWORD_BLOCK_COUNT.getType()));
		if (CommonUtils.isNullorEmpty(channelUser.getInvalidPasswordCount())) {
			channelUser.setInvalidPasswordCount(0L);
		}
		if (channelUser.getInvalidPasswordCount() == (invalidCount)
				|| updatePasswordInvalidCount(channelUser, loginRequest)) {
			if (channelUser.getInvalidPasswordCount() == (invalidCount)) {
				LOGGER.debug("Your password has been blocked");
				throw new BTSLBaseException(this, methodname,PretupsErrorCodesI.PASSWORD_BLOCKED);
//				throw new ValidationException(Constants.PASSWORD_BLOCKED.getStrValue(),
//						MessageCodes.USER_PASSWORD_BLOCKED_ERROR.getStrValue());
			}
			LOGGER.debug(
					"Authentication Failure. Please try again. In case you continue having troubles accessing the platform please contact Customer Care");
			throw new BTSLBaseException(this, methodname,PretupsErrorCodesI.AUTHENDICATION_ERROR);
//			throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
//					MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());
		} else if (Constants.PARTY_STATUS_SUSPEND.getStrValue().equals(channelUser.getNetworkstatus())
				&& Constants.NO.getStrValue().equalsIgnoreCase(channelUser.getCategories().getViewOnNetworkBlock())) {
			LOGGER.debug(channelUser.getMessage() + "Network Suspended");
			throw new ValidationException(Constants.PARTY_STATUS_SUSPEND_NETWORK.getStrValue(),
					MessageCodes.PARTY_STATUS_SUSPEND.getStrValue(), channelUser.getMessage());
		} else if (Constants.NO.getStrValue().equals(channelUser.getCategories().getWebInterfaceAllowed())) {
			LOGGER.debug(
					MessageFormat.format("can not access web interface: {0}", channelUser.getUserName()));
			throw new ValidationException(Constants.WEB_INTERFACE_NOT_ALLOWED.getStrValue(),
					MessageCodes.USER_PASSWORD_BLOCKED_ERROR.getStrValue(), channelUser.getUserName());
		} else {
			checkUserStaus(channelUser);
		}

	}

	private void checkUserStaus(ChannelUserVO channelUser) {
		String cryptionType = vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.PINPAS_EN_DE_CRYPTION_TYPE.getType());
		channelUser.setPword(/*CommonUtils.encryptText(*/loginRequest.getUserPassword()/*, cryptionType)*/);
		if ((Constants.NO.getStrValue()).equalsIgnoreCase(channelUser.getCategories().getMultipleLoginAllowed())) {
			// channelUser = checkDuplicateLogin(channelUser, loginRequest);
		}
		if (!Constants.YES.getStrValue().equals(channelUser.getDomainStatus())) {
			LOGGER.debug("User domain is suspended");
			throw new ValidationException(Constants.DOMAIN_SUSPENDED.getStrValue(),
					MessageCodes.DOMAIN_SUSPENDED.getStrValue());
		} else if (!Constants.YES.getStrValue().equals(channelUser.getCategories().getStatus())) {
			LOGGER.debug("User category is suspended");
			throw new ValidationException(Constants.USER_CATEGORY_SUSPENDED.getStrValue(),
					MessageCodes.CATEGORY_SUSPENDED.getStrValue());
		} else if (Constants.USER_STATUS_NEW.getStrValue().equals(channelUser.getStatus())
				|| Constants.USER_STATUS_APPROVED.getStrValue().equals(channelUser.getStatus())) {
			LOGGER.debug("User approval is still pending");
			throw new ValidationException(Constants.USER_APPROVAL_PENDING.getStrValue(),
					MessageCodes.USER_APPROVAL_PENDING.getStrValue());
		} else if (Constants.USER_STATUS_SUSPEND.getStrValue().equals(channelUser.getStatus())) {
			LOGGER.debug(
					"User status is Suspend request (Parent user has sent a request for suspension)");
			throw new ValidationException(Constants.PARENT_USER_REQUESTED_SUSPEND.getStrValue(),
					MessageCodes.PARENT_USER_REQUESTED_SUSPEND.getStrValue());
		} else if (Constants.USER_STATUS_BLOCKED.getStrValue().equals(channelUser.getStatus())) {
			LOGGER.debug("User status is blocked");
			throw new ValidationException(Constants.USER_STATUS_BLOCK.getStrValue(),
					MessageCodes.USER_STATUS_BLOCKED.getStrValue());
		} else if (Constants.USER_STATUS_DEREGISTERED.getStrValue().equals(channelUser.getStatus())) {
			LOGGER.debug("User status is de-registered");
			throw new ValidationException(Constants.USER_STATUS_BLOCK.getStrValue(),
					MessageCodes.USER_STATUS_DEREGISTER.getStrValue());
		} else if (Constants.USER_STATUS_SUSPEND_REQUEST.getStrValue().equals(channelUser.getStatus())) {
			LOGGER.debug("User status is blocked");
			throw new ValidationException(Constants.PARENT_USER_REQUESTED_SUSPEND.getStrValue(),
					MessageCodes.PARENT_USER_REQUESTED_SUSPEND.getStrValue());
		} else {
			checkUserStaus1(channelUser);
		}
	}

	private void checkUserStaus1(ChannelUserVO channelUser) {
		OperatorUtilI operatorUtil = null;
		checkMaxLocationTypeUsers(channelUser);
		if (channelUser.getLastLoginOn() == null && !isFirstTimeLogin) {
			isFirstTimeLogin = true;
		}
		usersLoginRepository.updateUserLoginDetails(channelUser.getUserId());
		Date resetPasswordExpiredTime = null;
		if (!CommonUtils.isNullorEmpty(channelUser.getPswdModifiedOn())) {
			/*int resetPasswordExpiredInHours = Integer.parseInt(vmsCacheRepository.getControlPreferenceValue(
					channelUser.getCategoryCode(), channelUser.getNetworkId(),
					SystemPreferenceConstants.RESET_PASSWORD_EXPIRED_TIME_IN_HOURS.getType())); */
			int resetPasswordExpiredInHours = Integer.parseInt(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.RESET_PASSWORD_EXPIRED_TIME_IN_HOURS.getType()));
			Calendar cal = BTSLDateUtil.getInstance();
			cal.setTime(channelUser.getPswdModifiedOn());
			cal.add(Calendar.HOUR, resetPasswordExpiredInHours);
			resetPasswordExpiredTime = cal.getTime();
		}
		boolean resetPwdOnCreationflag = false;
		String utilClass = vmsCacheRepository
				.getSystemPreferenceValue(SystemPreferenceConstants.OPERATOR_UTIL_CLASS.getType());
		try {
			operatorUtil = (OperatorUtilI) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST").getBean(utilClass);
			String c2sDefaultPass = vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.C2S_DEFAULT_PASSWORD.getType());
			if (CommonUtils.isNullorEmpty(channelUser.getLastLoginOn()) || isFirstTimeLogin
					|| CommonUtils.isNullorEmpty(channelUser.getPswdModifiedOn())) {
				resetPwdOnCreationflag = operatorUtil.checkPasswordPeriodToResetAfterCreation(modifiedOn, channelUser);
				if (resetPwdOnCreationflag && "Y".equals(channelUser.getPswdReset())) {
					throw new ValidationException("passwordExpired", MessageCodes.PASSWORD_EXPIRED.getStrValue());
				}
				changePassword = true;
			} else if ("Y".equals(channelUser.getPswdReset())
					&& channelUser.getLoginTime().before(resetPasswordExpiredTime)) {
				changePassword = true;
			} else if (c2sDefaultPass.equalsIgnoreCase(loginRequest.getUserPassword())) {
				changePassword = true;
			} else {
				checkUserStaus2(channelUser);

			}
		} catch (Exception e) {
			LOGGER.trace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.FATAL, "OnlineVoucherGenerator[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}

	}

	private boolean checkMaxLocationTypeUsers(ChannelUserVO channelUserVO) {
		boolean check = false;
		MComConnection mcomCon = null;
		Connection con = null;
		String methodName = "checkMaxLocationTypeUsers";


		try {
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();

		final String disableLocationTypeCheck = com.btsl.util.Constants.getProperty("DISABLE_MULTIPLE_LOGINS_CHECK");
		final boolean forcelogout = Boolean.valueOf(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.FORCE_LOGOUT_1STUSER.getType()));
		String maxloginLocationNetwork = null;
		String maxloginCateogryType = null;


		com.btsl.pretups.user.businesslogic.ChannelUserVO channelUserVO2 = null;
		if (channelUserVO != null) {



				LoginDAO _loginDAO = new LoginDAO();
				channelUserVO2 = _loginDAO.loadUserDetails(con, channelUserVO.getLoginId(), "",
						CacheManager.getLanguageLocale(responseLanguage));


			if (disableLocationTypeCheck.indexOf(channelUserVO.getCategoryCode()) > 0) {

				maxloginLocationNetwork = vmsCacheRepository.getNetworkPreferenceValue(channelUserVO.getNetworkCode(),
						SystemPreferenceConstants.MAX_LOGINS_LOCATION.getType());

				if (CommonUtils.isNullorEmpty(maxloginLocationNetwork)) {
					maxloginLocationNetwork = vmsCacheRepository
							.getSystemPreferenceValue(SystemPreferenceConstants.MAX_LOGINS_LOCATION.getType());
				}

				maxloginCateogryType = vmsCacheRepository.getControlPreferenceValue(channelUserVO.getCategoryCode(),
						channelUserVO.getNetworkCode(), SystemPreferenceConstants.MAX_LOGINS_TYPE.getType());
				if (CommonUtils.isNullorEmpty(maxloginLocationNetwork)) {
					maxloginCateogryType = vmsCacheRepository
							.getSystemPreferenceValue(SystemPreferenceConstants.MAX_LOGINS_TYPE.getType());
				}

				Long maxuserPNetwork = usersLoginRepository.getMaxUserPerNetwork(channelUserVO.getNetworkCode());
				Long maxuserPNetworkCategory = usersLoginRepository
						.getMaxUserPerNetworkCategory(channelUserVO.getNetworkCode(), channelUserVO.getCategoryCode());


				if ((TypesI.NO).equalsIgnoreCase(channelUserVO2.getCategoryVO().getMultipleLoginAllowed())) {
					Long maxuserP = usersLoginRepository
							.getMaxUserPerUser(channelUserVO.getLoginId());
					if (maxuserP > 0) {
						dealForceLogout(con, channelUserVO.getLoginId());
						con.commit();
					}


				}



				if (Long.valueOf(maxloginLocationNetwork) <= maxuserPNetwork) {
					dealForceLogout(Constants.NetowkLevel.getStrValue(), channelUserVO, forcelogout);
				}
				if (Long.valueOf(maxloginCateogryType) <= maxuserPNetworkCategory) {
					dealForceLogout(Constants.CategoryLevel.getStrValue(), channelUserVO, forcelogout);
				}
			}
		}

		} catch (Exception e) {
			LOGGER.debug("Error occured "+e);
		}finally {

			mcomCon.close(methodName);

		}
		return check;
	}

	private void dealForceLogout(Connection p_con, String loginId) throws BTSLBaseException, SQLException {

		OAuthDao oauthDao = new OAuthDao();
		oauthDao.deleteToken(p_con, loginId, null, null);


	}



	private void dealForceLogout(String level, ChannelUserVO channelUserVO, boolean forcelogout) {
		UsersLoginInfo userLoginInfo = null;
		String messageCodeVal = null;
		if (level.equals(Constants.NetowkLevel.getStrValue())) {
			userLoginInfo = usersLoginRepository.getFirstUserPerNetwork(channelUserVO.getNetworkCode());
			messageCodeVal = MessageCodes.MAX_LOGIN_NETWORK_REACHED.getStrValue();
		} else {
			userLoginInfo = usersLoginRepository.getFirstUserPerNetworkCategory(channelUserVO.getNetworkCode(),
					channelUserVO.getCategoryCode());
			messageCodeVal = MessageCodes.MAX_LOGIN_NETWORK_CATG_REACHED.getStrValue();
		}
		if (forcelogout) {
			usersLoginRepository.deleteUsersLoginInfo(userLoginInfo);

			CacheManager.getCacheHashMap().forEach((key, value) -> {
				CachedObject cacheableObj = (CachedObject) value;
				LOGGER.debug(
						MessageFormat.format("Cache object data {0} -> {1}", key, cacheableObj.getIdentifier()));
				LoggedInUserInfo loggedinUserInfo = (LoggedInUserInfo) cacheableObj.getObjectVal();
				LOGGER.debug(MessageFormat.format("Cache object data {0} -> {1}", key,
						loggedinUserInfo.getLoginIdentifier()));
				if (cacheableObj.isExpired()) {
					LOGGER.debug("ThreadCleanerUpper Running. Found an Expired Object in the Cache.");
				}

			});

//check			CachedObject cachedObj = (CachedObject) CacheManager.getCache(userLoginInfo.getFirstLoginJti());
//			cachedObj = null;// priyank -> to prevent issue while logging in pg: will check abt it later
		/* Check	if (cachedObj != null) {
				LoggedInUserInfo loggedinUserInfo = (LoggedInUserInfo) cachedObj.getObjectVal();
				tokenProcessor.deleteToken(loggedinUserInfo.getJwttoken());
			}
*/		} else {
			LOGGER.error("Max logins exceeded........ ");
			throw new ApplicationException(messageCodeVal);
		}
	}

	private void checkUserStaus2(ChannelUserVO channelUser) {
		Date date1 = channelUser.getPswdModifiedOn();
		Date date2 = new Date();
		long dt1 = date1.getTime();
		long dt2 = date2.getTime();
		long nodays = ((dt2 - dt1) / (1000 * 60 * 60 * 24));
		int noPasswordTimeOutDays = 0;
		try {
			/*noPasswordTimeOutDays = Integer.parseInt(vmsCacheRepository.getControlPreferenceValue(
					channelUser.getCategoryCode(), channelUser.getNetworkId(),
					SystemPreferenceConstants.DAYS_AFTER_CHANGE_PASSWORD.getType())); */
			noPasswordTimeOutDays = Integer.parseInt(vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.DAYS_AFTER_CHANGE_PASSWORD.getType()));
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Login", "loginController.jsp noPasswordTimeOutDays not found in Constants.props");
			}
			LOGGER.trace("Login", e);
		}
		String inString = vmsCacheRepository.getSystemPreferenceValue(
				SystemPreferenceConstants.CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY.getType());
		if (!BTSLUtil.isStringIn(channelUser.getCategoryCode(), inString) && nodays > noPasswordTimeOutDays) {
			changePassword = true;
		}
	}



	private void generateToken(ChannelUserVO channelUser, String requestGatewayType, String requestGatewayCode,
			String requestGatewayLoginId, String requestGatewayPassword, String servicePort) {
		final String uri = com.btsl.util.Constants.getProperty("GENERATE_TOKEN_URL");//generateTokenUrl;// "http://172.30.24.113:9879/pretups/rstapi/v1/generateTokenAPI";

		RestTemplate restTemplate = new RestTemplate();
		TokenRequestVO tokenRequestVO = new TokenRequestVO();
		tokenRequestVO.setIdentifierType("loginId");
		tokenRequestVO.setIdentifierValue(channelUser.getLoginId());
		tokenRequestVO.setPasswordOrSmspin(Cryptojs.decrypt(channelUser.getPword(), com.btsl.util.Constants.getProperty("AEKE_CLIENT_LAYER")));
		HttpHeaders headers = new HttpHeaders();

		headers.set("CLIENT_ID", com.btsl.util.Constants.getProperty("CLIENT_ID"));
		;
		headers.set("CLIENT_SECRET", com.btsl.util.Constants.getProperty("CLIENT_SECRET"));
		headers.set("requestGatewayCode", com.btsl.util.Constants.getProperty("REQ_GATEWAY_CODE"));
		headers.set("requestGatewayLoginId", requestGatewayLoginId);
		headers.set("requestGatewayPsecure", com.btsl.util.Constants.getProperty("REQ_GATEWAY_PSECURE"));//1357
		headers.set("requestGatewayType", com.btsl.util.Constants.getProperty("REQ_GATEWAY_TYPE"));
		headers.set("servicePort", servicePort);
		headers.set("scope", "All");

		HttpEntity<TokenRequestVO> request = new HttpEntity<>(tokenRequestVO, headers);
		TokenResponseVO tokenResponse = restTemplate.postForObject(uri, request, TokenResponseVO.class);

		// TokenResponseVO tokenResponse = restTemplate.postForObject( uri,
		// tokenRequestVO, TokenResponseVO.class);
		genToken = tokenResponse.getToken();
		refToken = tokenResponse.getRefreshToken();
		System.out.println(tokenResponse);
	}


	/**
	 * Construct for CommonService
	 *
	 * @param params - List<String>
	 */
	public void generateReponseMessage(List<String> params) {
		String message = vmsCacheRepository.loadMessageByMessageCodeAndLangCode(responseMessageCode, responseLanguage);

		if (CommonUtils.isNullorEmpty(message)) {
			Locale locale = CacheManager.getLanguageLocale(responseLanguage);
			message = localeTranslator.toLocale(responseMessageCode, locale);
		}

		if (message != null) {
			if (params != null) {
				responseMessage = new MessageFormat(message).format(params.toArray());
			}
			if (responseStatus.equals(Constants.SUCCESS_RESPONSE.getStrValue())) {
				responseMessageCode = "";
			}
		}
	}

	/**

	 *
	 * @param params - userId
	 */
	private void checkUserSuspendedGroupRole(String userId) {
		String status=usersLoginRepository.isUserGroupRoleSupended(userId);
		if (Constants.YES.getStrValue().equals(status)) {
			LOGGER.debug("User GroupRole is suspended");
			throw new ValidationException(Constants.GROUPROLE_SUSPENDED.getStrValue(),
					MessageCodes.GROUPROLE_SUSPENDED.getStrValue());
		}
	}
	private LoginLoggerVO prepareLoginLoggerVO(ChannelUserVO p_channelUservo,LoginLoggerVO loggerVO) {
		try{
			loggerVO.setUserID(p_channelUservo.getUserId());
			loggerVO.setNetworkID(p_channelUservo.getNetworkId());
			loggerVO.setUserName(p_channelUservo.getUserName());
			loggerVO.setUserType(p_channelUservo.getUserType());
			loggerVO.setDomainID(p_channelUservo.getDomainID());
			loggerVO.setCategoryCode(p_channelUservo.getCategoryCode());

		}catch(Exception e){

		}

		return loggerVO;
	}


}
