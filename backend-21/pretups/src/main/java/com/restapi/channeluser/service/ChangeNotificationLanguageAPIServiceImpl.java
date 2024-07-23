package com.restapi.channeluser.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;


@Service("ChangeNotificationLanguageAPIService")
public class ChangeNotificationLanguageAPIServiceImpl implements ChangeNotificationLanguageAPIService {

	public static final Log log = LogFactory.getLog(ChangeNotificationLanguageAPIController.class.getName());

	@Override
	public NotificationLanguageResponseVO loadUsersDetails(MultiValueMap<String, String> headers,
			HashMap<String, String> requestMap, HttpServletResponse responseSwag) {
		final String methodName = "loadUsersDetails";
		if (log.isDebugEnabled()) {
			log.debug("loadUserList", "Entered");
		}

		NotificationLanguageResponseVO responseVO = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		ArrayList userList = null;
		String userName = null;
		ChannelUserVO sessionUserVO = null;
		UserDAO userDAO = null;
		Locale locale = null;
		try {

			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			responseVO = new NotificationLanguageResponseVO();
			channelUserWebDAO = new ChannelUserWebDAO();
			userDAO = new UserDAO();
			// validate token
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			// validate req param
			if (BTSLUtil.isNullString(requestMap.get("categoryCode"))) {
				String[] msg = new String[] { "Category code" };
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ,
						PretupsI.RESPONSE_FAIL, msg, null);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			sessionUserVO = userDAO.loadUserDetailsByLoginId(con, oAuthUser.getData().getLoginid());

			userName = "%" + requestMap.get("userName") + "%";
			userList = channelUserWebDAO.loadUserHierarchyByCategory(con, requestMap.get("categoryCode"),
					sessionUserVO.getNetworkID(), userName, sessionUserVO.getUserID());

			if (userList.isEmpty()) {
				throw new BTSLBaseException(this, methodName,
						"channeluser.searchuserforselflangsetting.err.msg.nodatafound", PretupsI.RESPONSE_FAIL, null);
			}

			responseVO.setUserList(userList);
			responseVO.setUserListSize(userList.size());
			responseVO.setMessageCode(Integer.toString(HttpStatus.SC_OK));
			responseVO.setMessage(PretupsI.SUCCESS);
			responseSwag.setStatus(HttpStatus.SC_OK);
			responseVO.setStatus(HttpStatus.SC_OK);
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			responseVO.setMessageCode(be.getMessageKey());
			responseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChangeNotificationLanguageAPIServiceImpl#loadUserList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting=" + responseVO);
			}
		}

		return responseVO;
	}

	@Override
	public NotificationLanguageResponseVO loadUserPhoneDetailsByMsisdn(MultiValueMap<String, String> headers,
			HashMap<String, String> requestMap, HttpServletResponse responseSwag) {

		final String methodName = "loadUsersDetails";
		if (log.isDebugEnabled()) {
			log.debug("loadUserList", "Entered");
		}

		NotificationLanguageResponseVO responseVO = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ArrayList userPhoneInfoList = null;
		ArrayList userPhoneInfoListTemp = null;
		ChannelUserVO channelUserVO = null;
		String phoneLang = null;
		ArrayList languageList = null;
		String filteredMsisdn = null;
		ArrayList userMsisdnList = null;
		UserDAO userDAO = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		OAuthUserData sessionUserDataFromAuth = null;
		Locale locale = null;

		UserVO sessionUserVO = null;
		String userLoginID = null;
		String userMsisdn = null;

		try {
			userPhoneInfoList = new ArrayList();
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			responseVO = new NotificationLanguageResponseVO();
			// validate token
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			// requestParam validattion
			this.validateRequestForPhoneDetails(requestMap);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			channelUserWebDAO = new ChannelUserWebDAO();
			userDAO = new UserDAO();
			sessionUserDataFromAuth = oAuthUser.getData();
			
			if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(requestMap.get("searchBy"))) {
				
				// Validating the MSISDN in the current line
				this.validateMsisdn(requestMap.get("msisdn"));
				filteredMsisdn = PretupsBL.getFilteredMSISDN(requestMap.get("msisdn").trim());
				
				
				// More than one MSISDN can be assigned to the logged-In user
				userMsisdnList = userDAO.loadUserPhoneList(con, sessionUserDataFromAuth.getUserid());
				boolean sessionUserMsisdnFlag = false;
				for (int i = 0, j = userMsisdnList.size(); i < j; i++) {
					if (((UserPhoneVO) (userMsisdnList.get(i))).getMsisdn().equals(filteredMsisdn)) {
						sessionUserMsisdnFlag = true;
						break;
					}
				}

				// If the entered MSISDN is of logged-In user
				if (sessionUserMsisdnFlag) {
					userPhoneInfoListTemp = channelUserWebDAO.loadUserPhoneDetailsList(con,
							sessionUserDataFromAuth.getUserid());

					// if the size of list returned from DAO is more than 1
					// then filter this list for the entered MSISDN by the end user
					userPhoneInfoList = this.filterUserPhoneList(userPhoneInfoListTemp, filteredMsisdn);
					userLoginID = sessionUserDataFromAuth.getLoginid();
					userMsisdn = sessionUserDataFromAuth.getMsisdn();
				} else {
					// This DAO's method gives channelUserVO only under the logged-In user
					// hierarchy not the logged_In user's channelUserVO
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMsisdn,
							sessionUserDataFromAuth.getUserid(), PretupsI.STATUS_IN, "'Y','S','SR'");
					if (channelUserVO == null) {
						throw new BTSLBaseException(this, methodName,
								"channeluser.selfinformation.label.msisdnnotinheirarchy", PretupsI.RESPONSE_FAIL, null);
					}

					userPhoneInfoListTemp = channelUserWebDAO.loadUserPhoneDetailsList(con, channelUserVO.getUserID());

					// if the size of list returned from DAO is more than 1
					// then filter this list for the entered MSISDN by the
					// end user
					userPhoneInfoList = this.filterUserPhoneList(userPhoneInfoListTemp, filteredMsisdn);
					userLoginID = channelUserVO.getLoginID();
					userMsisdn = channelUserVO.getMsisdn();
				}
			} else  { // ADVANCE SEARCH: load all phone number associated to users
				
				if (!BTSLUtil.isNullString(requestMap.get("msisdn"))) {
					// Validating the MSISDN in the current line
					this.validateMsisdn(requestMap.get("msisdn"));
					filteredMsisdn = PretupsBL.getFilteredMSISDN(requestMap.get("msisdn").trim());
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMsisdn,
							sessionUserDataFromAuth.getUserid(), PretupsI.STATUS_IN, "'Y','S','SR'");
					if (channelUserVO == null) {
						throw new BTSLBaseException(this, methodName,
								"channeluser.selfinformation.label.msisdnnotinheirarchy", PretupsI.RESPONSE_FAIL, null);
					}
					userPhoneInfoList = channelUserWebDAO.loadUserPhoneDetailsList(con, channelUserVO.getUserID());
					
					userLoginID = channelUserVO.getLoginID();
					userMsisdn = channelUserVO.getMsisdn();
				} else { // search by userName(null check is in validateReq method)
					sessionUserVO = userDAO.loadUserDetailsByLoginId(con, oAuthUser.getData().getLoginid());
					UserVO userVO = this.loadHierarchyUserWithUserNameAndCategory(con, sessionUserVO, requestMap.get("categoryCode").trim(), requestMap.get("userName").trim());
					
					userPhoneInfoList = channelUserWebDAO.loadUserPhoneDetailsList(con, userVO.getUserID());
					
					userLoginID = userVO.getLoginID();
					userMsisdn = userVO.getMsisdn();
				}

				
			} 

			languageList = this.loadLanguageList(con, channelUserDAO);
			// for setting the user's current language in the language combo
			if (!userPhoneInfoList.isEmpty() && !languageList.isEmpty()) {
				for (int i = 0, j = userPhoneInfoList.size(); i < j; i++) {
					channelUserVO = (ChannelUserVO) userPhoneInfoList.get(i);
					phoneLang = channelUserVO.getUserPhoneVO().getPhoneLanguage();
					for (int x = 0, y = languageList.size(); x < y; x++) {
						if (phoneLang.equals(((ChangeLocaleVO) languageList.get(x)).getLanguageCode())) {
							channelUserVO.setLanguageCode(((ChangeLocaleVO) languageList.get(x)).getLanguageCode());
							channelUserVO.setLanguageName(((ChangeLocaleVO) languageList.get(x)).getLanguageName());
							channelUserVO.setCountry(((ChangeLocaleVO) languageList.get(x)).getCountry());
							break;
						}
					}
				}
			}

			responseVO.setLoginID(userLoginID);
			responseVO.setMsisdn(userMsisdn);
			responseVO.setUserList(userPhoneInfoList);
			responseVO.setUserListSize(userPhoneInfoList.size());
			responseVO.setLanguageList(languageList);			
			responseVO.setMessageCode(Integer.toString(HttpStatus.SC_OK));
			responseVO.setMessage(PretupsI.SUCCESS);
			responseSwag.setStatus(HttpStatus.SC_OK);
			responseVO.setStatus(HttpStatus.SC_OK);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			responseVO.setMessageCode(be.getMessageKey());
			responseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChangeNotificationLanguageAPIServiceImpl#loadUserList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting=" + responseVO);
			}
		}

		return responseVO;
	}
	
	
	@Override
	public BaseResponse changeNotificationLanguage(MultiValueMap<String, String> headers,
			NotificationLanguageRequestVO requestVO, HttpServletResponse responseSwag) {
		
		final String methodName= "changeNotificationLanguage";
		
		BaseResponse responseVO = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String phoneLang = null;
		ArrayList updatedPhoneList = null;
		UserDAO userDAO = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		Locale locale = null;

		UserVO userVO = null;
		String sessionUserID = null;
		String updatedPhoneUserID = null;
		int updateCount = 0;
		ChannelUserDAO channelUserDAO = null;
		ArrayList languageList = null;
		try {
			
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			responseVO = new BaseResponse();
			// validate token
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserWebDAO = new ChannelUserWebDAO();
			userDAO = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			sessionUserID = oAuthUser.getData().getUserid();
			
			// 
			if( BTSLUtil.isNullString(requestVO.getUserLoginID())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Login ID"}, null);
			}
			
			if(oAuthUser.getData().getLoginid().equals(requestVO.getUserLoginID())) {
				// if modified phone number belogns to session user
				updatedPhoneUserID = sessionUserID;
			} else {
				userVO = userDAO.loadUsersDetailsByLoginID(con, requestVO.getUserLoginID());
				updatedPhoneUserID = userVO.getUserID();
			}
			
			// VALIDATE USER HIERARCHY 
			boolean isUserInHierachy = channelUserDAO.isUserInHierarchy(con, sessionUserID, "USER_ID", updatedPhoneUserID);
			if(!isUserInHierachy) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NOT_ALLOWED_TO_UPDATE_USER_LANG, PretupsI.RESPONSE_FAIL, null);
			}
			
			
			languageList = this.loadLanguageList(con, channelUserDAO);
			if(BTSLUtil.isNullOrEmptyList(requestVO.getChangedPhoneLanguageList())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Phone details"}, null);
			}
			
			updatedPhoneList = this.createUpdatedPhoneList(languageList, requestVO.getChangedPhoneLanguageList());
			Date currentDate = new Date();
			updateCount = channelUserWebDAO.updateLanguage(con, updatedPhoneList, currentDate, sessionUserID, updatedPhoneUserID);

			// If user is not updated in database due to some problem then give
			// proper msg.
			if (updateCount <= 0) {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName, "channeluser.selfinformation.err.msg.cannotupdate", PretupsI.RESPONSE_FAIL, null);
			}
			// Give the proper success message after changing the language
			mcomCon.finalCommit();
			String msg = RestAPIStringParser.getMessage(locale,"channeluser.selfinformation.msg.succmsg", null);		
			responseVO.setMessageCode("channeluser.selfinformation.msg.succmsg");
			responseVO.setMessage(msg);
			responseSwag.setStatus(HttpStatus.SC_OK);
			responseVO.setStatus(HttpStatus.SC_OK);
			
		} catch(BTSLBaseException be) {
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			responseVO.setMessageCode(be.getMessageKey());
			responseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseVO.setMessageCode(e.toString());
			responseVO.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChangeNotificationLanguageAPIServiceImpl#loadUserList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting=" + responseVO);
			}
		}

		return responseVO;

	}
	
	/**
	 * 
	 * @param languageList
	 * @param phoneList
	 * @return
	 * @throws BTSLBaseException 
	 */
	private ArrayList createUpdatedPhoneList(ArrayList languageList, ArrayList phoneList) throws BTSLBaseException {
		
		final String methodName = "createUpdatedPhoneList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		 ArrayList updatedPhoneList = new ArrayList();
		 ChannelUserVO channelUserVO = null;
		 ChangePhoneLanguage changePhoneLanguage = null;
		 String langCode = null;
		
		for (int i = 0, j = phoneList.size(); i < j; i++) {
			changePhoneLanguage = (ChangePhoneLanguage) phoneList.get(i);
			
			if(BTSLUtil.isNullString(changePhoneLanguage.getUserMsisdn())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Login ID"}, null);
			}
			
			langCode = changePhoneLanguage.getLanguageCode();
			for (int x = 0, y = languageList.size(); x < y; x++) {
				if (langCode.equals(((ChangeLocaleVO) languageList.get(x)).getLanguageCode())) {
					channelUserVO = new ChannelUserVO(); 
					channelUserVO.setUserPhoneVO(new UserPhoneVO());
					channelUserVO.getUserPhoneVO().setMsisdn(changePhoneLanguage.getUserMsisdn());
					channelUserVO.setLanguageCode(langCode);
					channelUserVO.setCountry(((ChangeLocaleVO) languageList.get(x)).getCountry());
					updatedPhoneList.add(channelUserVO);
					break;
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting=" + updatedPhoneList);
		}
		return updatedPhoneList;
	}
	
	/**
	 * 
	 * @param con
	 * @param sessionUserVO
	 * @param categoryCode
	 * @param userName
	 * @return
	 * @throws BTSLBaseException 
	 */
	private UserVO loadHierarchyUserWithUserNameAndCategory(Connection con, UserVO sessionUserVO, String categoryCode, String userName) throws BTSLBaseException {
		// search by userName(null check is in validateReq method)
		String methodName = "loadHierarchyUserWithUserNameAndCategory";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		ArrayList userList = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		UserVO userVO = null;

		try {

			channelUserWebDAO = new ChannelUserWebDAO();

			String searchUserName = "%" + userName + "%";
			userList = channelUserWebDAO.loadUserHierarchyByCategory(con, categoryCode, sessionUserVO.getNetworkID(),
					searchUserName, sessionUserVO.getUserID());

			if (BTSLUtil.isNullOrEmptyList(userList)) {
				if (log.isDebugEnabled()) {
					log.debug("loadUserPhoneDetail", "User not found");
				}
				throw new BTSLBaseException(this, methodName,
						"channeluser.searchuserforselflangsetting.err.msg.nodatafound", PretupsI.RESPONSE_FAIL, null);

			} else if (userList.size() == 1) {
				userVO = (UserVO) userList.get(0);

			} else if (userList.size() > 1) {
				// This is the case when userList size greater than 1
				// if user click the proceed button without performing search through searchUser

				boolean moreThanOneUserflag = true;
				UserVO userNextVO = null;
				for (int i = 0, j = userList.size(); i < j; i++) {
					userVO = (UserVO) userList.get(i);
					if ((userName.compareTo(userVO.getUserName())) == 0) {
						if (((i + 1) < j)) {
							userNextVO = (UserVO) userList.get(i + 1);
							if ((userName.compareTo(userNextVO.getUserName())) == 0) {
								moreThanOneUserflag = true;
								break;
							} else {
								moreThanOneUserflag = false;
								break;
							}
						} else {
							moreThanOneUserflag = false;
							break;
						}
					}
				}

				if (moreThanOneUserflag) {
					throw new BTSLBaseException(this, methodName,
							"channeluser.searchuserforselflangsetting.err.msg.morethanoneuse", PretupsI.RESPONSE_FAIL,
							null);
				}
			}

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exceptin:be=" + be);
			throw be;
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting: " + userVO);
			}
		}
		return userVO;

	}

	/**
	 * 
	 * @param requestMap
	 * @throws BTSLBaseException
	 */
	private void validateRequestForPhoneDetails(HashMap<String, String> requestMap) throws BTSLBaseException {
		String methodName = "validateRequestForPhoneDetails";
		
		if (!PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(requestMap.get("searchBy"))
				&& !PretupsI.SEARCH_BY_ADVANCED.equalsIgnoreCase(requestMap.get("searchBy"))) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ACCOUNT_STATUS_FAILED,
					PretupsI.RESPONSE_FAIL, new String[] { "(MISSDN/ADVANCED)" }, null);
		}
		
		if( PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(requestMap.get("searchBy")) ){
			// msisdn is required
			if (BTSLUtil.isNullString(requestMap.get("msisdn"))) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL,
						PretupsI.RESPONSE_FAIL, new String[] { "Msisdn" }, null);
			}
		} else {
			// msisdn or userName with category is required
			if (BTSLUtil.isNullString(requestMap.get("msisdn"))) {
				if(BTSLUtil.isNullString(requestMap.get("userName")) || BTSLUtil.isNullString(requestMap.get("categoryCode")) )
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MSISDN_OR_USERNAME_WITH_CATEGORY_REQ,
						PretupsI.RESPONSE_FAIL, null);
			}
		}

		
		
	}

	/**
	 * 
	 * @param userPhoneInfoListTemp
	 * @param filteredMsisdn
	 * @return
	 */
	public ArrayList<ChannelUserVO> filterUserPhoneList(ArrayList userPhoneInfoListTemp, String filteredMsisdn) {

		ArrayList<ChannelUserVO> userPhoneInfoList = new ArrayList<ChannelUserVO>();
		ChannelUserVO channelUserVO = null;

		if (userPhoneInfoListTemp.size() > 1) {
			for (int i = 0, j = userPhoneInfoListTemp.size(); i < j; i++) {
				channelUserVO = (ChannelUserVO) userPhoneInfoListTemp.get(i);
				if (channelUserVO.getUserPhoneVO().getMsisdn().equals(filteredMsisdn)) {
					userPhoneInfoList.add(channelUserVO);
					break;
				}
			}
		} else {
			channelUserVO = (ChannelUserVO) userPhoneInfoListTemp.get(0);
			userPhoneInfoList.add(channelUserVO);
		}

		return userPhoneInfoList;
	}

	/**
	 * 
	 * @param con
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList loadLanguageList(Connection con, ChannelUserDAO channelUserDAO) throws BTSLBaseException {

		String methodName = "loadLanguageList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		ArrayList languageList = null;
		try {
			languageList = channelUserDAO.loadLanguageListForUser(con);
			if (BTSLUtil.isNullOrEmptyList(languageList)) {
				throw new BTSLBaseException(this, methodName, "channeluser.selfinformation.err.msg.nolangfound",
						PretupsI.RESPONSE_FAIL, null);
			}

		} catch (BTSLBaseException be) {
			log.error(methodName, be);
			throw be;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:" + languageList);
			}
		}
		return languageList;
	}

	/**
	 * 
	 * @param msisdn
	 * @throws BTSLBaseException
	 */
	public void validateMsisdn(String msisdn) throws BTSLBaseException {

		String methodName = "validateMsisdn";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Processing starts from MSISDN " + msisdn);
		}

		String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn.trim());
		// check for valid MSISDN
		if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Not a valid MSISDN " + filteredMsisdn);
			}
			throw new BTSLBaseException(this, methodName, "channeluser.selfinformation.label.novalidmsisdn",
					PretupsI.RESPONSE_FAIL, null);
		}

		// check prefix of the MSISDN
		String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
		// the prefix of the MSISDN
		NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Not Network prefix found " + filteredMsisdn);
			}
			throw new BTSLBaseException(this, methodName, "channeluser.selfinformation.label.noprefixfound",
					PretupsI.RESPONSE_FAIL, null);
		}
	}


	
	

}
