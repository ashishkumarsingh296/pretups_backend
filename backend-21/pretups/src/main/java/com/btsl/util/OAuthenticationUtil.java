package com.btsl.util;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.oauth.businesslogic.OAuthDao;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthRefTokenReq;
import com.btsl.user.businesslogic.OAuthRefTokenRequest;
import com.btsl.user.businesslogic.OAuthTokenReq;
import com.btsl.user.businesslogic.OAuthTokenRequest;
import com.btsl.user.businesslogic.OAuthTokenRes;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;

public class OAuthenticationUtil {

	
	private static final Log log = LogFactory.getLog(OAuthenticationUtil.class);

	public static void validateToken(OAuthUser oAuthreqVo, MultiValueMap<String, String> headers) throws Exception {

		String methodName = "validateToken";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			if(headers != null ) {
				for(String str: headers.keySet()) {
					log.debug(str, headers.getFirst(str));
				}
				}
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			
			String token = null;
			if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
				token = headers.get("authorization").get(0);
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
			}else {
				throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
			}
			
			if(token != null && token.trim().length() > 0) {
			
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			
			UserDAO userDAO = new UserDAO();
			userDAO.validateToken(con, oAuthreqVo, token);
						
			}
			
		} 
		catch (BTSLBaseException be) {
			throw be;
		}catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#validateToken");
				mcomCon = null;
			}
		}
	}

	
	public static void validateTokenApi(OAuthUser oAuthreqVo, MultiValueMap<String, String> headers) throws Exception {

		String methodName = "validateTokenApi";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			if(headers != null ) {
				for(String str: headers.keySet()) {
					log.debug(str, headers.getFirst(str));
				}
				}
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			
			String token = null;
			if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
				token = headers.get("authorization").get(0);
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
			}else {
				throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
			}
			
			if(token != null && token.trim().length() > 0) {
			
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthDao oAuthDao = new OAuthDao();;
			oAuthDao.validateToken(con, oAuthreqVo, token);
			
			}
			
		} 
		catch (BTSLBaseException be) {
			throw be;
		}catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#validateToken");
				mcomCon = null;
			}
		}
	}

	public static void validateToken(MultiValueMap<String, String> headers) throws Exception {

		String methodName = "validateToken";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			if(headers != null ) {
				for(String str: headers.keySet()) {
					log.debug(str, headers.getFirst(str));
				}
				}
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			
			String token = null;
			if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
				token = headers.get("authorization").get(0);
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
			}else {
				throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
			}
			
			
			if(token != null && token.trim().length() > 0) {
			
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			
			UserDAO userDAO = new UserDAO();
			userDAO.validateToken(con, token);
			
			}
			
		} catch (BTSLBaseException be) {
			throw be;
		}catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#validateToken");
				mcomCon = null;
			}
		}
	}

	public static void validateTokenApi(MultiValueMap<String, String> headers) throws Exception {

		String methodName = "validateTokenApi";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			if(headers != null ) {
				for(String str: headers.keySet()) {
					log.debug(str, headers.getFirst(str));
				}
				}
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			
			String token = null;
			if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
				token = headers.get("authorization").get(0);
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
			}else {
				throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
			}
			
			
			if(token != null && token.trim().length() > 0) {
			
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthDao oAuthDao = new OAuthDao();;
			oAuthDao.validateToken(con, token);
			
			}
			
		} catch (BTSLBaseException be) {
			throw be;
		}catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#validateToken");
				mcomCon = null;
			}
		}
	}

	
	public static void validateUserDetails(ChannelUserVO p_channelUserVO, OAuthTokenReq oAuthTokenReq) throws BTSLBaseException {
		final String methodName = "validateUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		try {
			if (p_channelUserVO == null) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.CHNL_ERROR_NO_SUCH_USER);
			}

			if (p_channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND);
			}

			boolean statusAllowed = false;
			final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_channelUserVO.getNetworkID(),
					p_channelUserVO.getCategoryCode(), p_channelUserVO.getUserType(), oAuthTokenReq.getReqGatewayType());

			if (userStatusVO == null) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
			} else {
				final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
				final String status[] = userStatusAllowed.split(",");
				for (int i = 0; i < status.length; i++) {
					if (status[i].equals(p_channelUserVO.getStatus())) {
						statusAllowed = true;
					}
				}
				if (statusAllowed) {

					if (!p_channelUserVO.getCategoryVO().getAllowedGatewayTypes()
							.contains(oAuthTokenReq.getReqGatewayType())) {

						throw new BTSLBaseException(OAuthenticationUtil.class, methodName,
								PretupsErrorCodesI.CHNL_ERROR_CAT_GATETYPENOTALLOWED);
					} else if (p_channelUserVO.getGeographicalCodeStatus()
							.equals(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND)) {
						throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.CHNL_ERROR_GEODOMAIN_SUSPEND);
					}
				} else {
					throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED);
				}
			}

		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
		}

	}

	public static void validateUserDetailsApi(UserVO userVO, OAuthTokenRequest oAuthTokenReq) throws BTSLBaseException {
		final String methodName = "validateUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		try {
			if (userVO == null) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.CHNL_ERROR_NO_SUCH_USER);
			}

			if (userVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND);
			}



		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
		}

	}

	private static boolean validateUser(Connection con, OAuthTokenReq oAuthTokenReq) throws BTSLBaseException {

		String methodName = "validateUser";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		String password = oAuthTokenReq.getPassword();
		String extCode = oAuthTokenReq.getExtCode();
		//String networkID = oAuthUser.getne;
		String loginID = oAuthTokenReq.getLoginId();
		String msisdn = oAuthTokenReq.getMsisdn();
		String pin = oAuthTokenReq.getPin();
		ChannelUserVO channelUserVO = null;
		ChannelUserDAO _channelUserDAO = new ChannelUserDAO();

		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		OperatorUtilI operatorUtili = null;
		try {
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.FATAL, "OAuthenticationUtil[loadValidateUserDetails]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
		
		if (!BTSLUtil.isNullString(msisdn)) {
			channelUserVO = _channelUserDAO.loadChannelUserDetails(con, msisdn);
		} else if (!BTSLUtil.isNullString(loginID)) {
			channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, loginID);
		} else if (!BTSLUtil.isNullString(extCode)) {
			channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(extCode).trim());
		}

		if (channelUserVO != null) {
			channelUserVO.setActiveUserID(channelUserVO.getUserID());

			validateUserDetails(channelUserVO, oAuthTokenReq);

/*			if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
			}*/

			if (!BTSLUtil.isNullString(password)
					&& !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
			}
			if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
			}
			if (!BTSLUtil.isNullString(loginID) && !loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
				throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
			}
			
		} else {
			throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
		}
		
		
		//
		if (BTSLUtil.isNullString(msisdn)) {
		} else {
			operatorUtili.validatePIN(con, channelUserVO, pin);
		}
		oAuthTokenReq.setExtCode(channelUserVO.getExternalCode());
		oAuthTokenReq.setLoginId(channelUserVO.getLoginID());
		oAuthTokenReq.setPassword(BTSLUtil.decryptText(channelUserVO.getPassword()));
		oAuthTokenReq.setMsisdn(channelUserVO.getMsisdn());
		oAuthTokenReq.setPin(BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));

		return true;
	}

	private static UserVO validateUserApi(Connection con, OAuthTokenRequest oAuthTokenReq, HttpServletRequest req) throws BTSLBaseException {

		UserVO userVO = null;
		
		String methodName = "validateUserApi";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		String identifierType = oAuthTokenReq.getIdentifierType() ; 
	
		UserDAO userDAO = new UserDAO();

		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		OperatorUtilI operatorUtili = null;
		try {
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.FATAL, "OAuthenticationUtil[loadValidateUserDetails]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
		
			userVO = userDAO.loadUsersDetailsByidentifierType(con, identifierType, oAuthTokenReq.getIdentifierValue(), oAuthTokenReq.getPasswordOrSmspin(), req);

			
			
		if (userVO == null) {
			String[] errorMsgParam = { oAuthTokenReq.getIdentifierValue() };
			throw new BTSLBaseException(OAuthenticationUtil.class, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, errorMsgParam );
		}
		
		
		//
		/*if (BTSLUtil.isNullString(msisdn)) {
		} else {
			operatorUtili.validatePIN(con, userVO, pin);
		}*/
	/*	oAuthTokenReq.setExtCode(extCode);
		oAuthTokenReq.setLoginId(channelUserVO.getLoginID());
		oAuthTokenReq.setPassword(BTSLUtil.decryptText(channelUserVO.getPassword()));
		oAuthTokenReq.setMsisdn(channelUserVO.getMsisdn());
		oAuthTokenReq.setPin(BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
    */
		return userVO;
	}

	
	public static OAuthTokenRes refreshToken(OAuthRefTokenReq oAuthRefTokenReq) throws Exception {

		String methodName = "refreshToken";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			long rtExp =(Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.REFRESH_TOKEN_EXPIRE_TIME);
			long tExp = (Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.TOKEN_EXPIRE_TIME);
			
			if (log.isDebugEnabled()) {
				log.debug("TOKEN_EXPIRE_TIME", tExp);
				log.debug("REFRESH_TOKEN_EXPIRE_TIME", rtExp);
			}
		
			JWebTokenUtil jwt1 = new JWebTokenUtil(oAuthRefTokenReq, tExp);
			String encodedTokenNew = jwt1.toString();

			JWebTokenUtil jwt2 = new JWebTokenUtil(oAuthRefTokenReq, rtExp);
			String encodedTokenRefNew = jwt2.toString();
			
			UserDAO userDAO = new UserDAO();
			
			userDAO.refreshToken(con, oAuthRefTokenReq, encodedTokenNew, encodedTokenRefNew);

			OAuthTokenRes oAuthTokenRes= new OAuthTokenRes();

			oAuthTokenRes.setToken(encodedTokenNew);
			oAuthTokenRes.setRefreshToken(encodedTokenRefNew);
			
			
			oAuthTokenRes.setStatus(PretupsI.RESPONSE_SUCCESS);
			oAuthTokenRes.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
					PretupsErrorCodesI.SUCCESS, null);
			oAuthTokenRes.setMessage(resmsg);
			
			return oAuthTokenRes;
		} catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#generateToken");
				mcomCon = null;
			}
		}
	}

	
	public static OAuthTokenRes refreshTokenApi(OAuthRefTokenRequest oAuthRefTokenReq) throws Exception {

		String methodName = "refreshTokenApi";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			Long[] expires = new Long[2];

			OAuthTokenRequest oAuthTokenReq = new OAuthTokenRequest();
			oAuthTokenReq.setClientId(oAuthRefTokenReq.getClientId());
			oAuthTokenReq.setClientSecret(oAuthRefTokenReq.getClientSecret());
			oAuthTokenReq.setScope(oAuthRefTokenReq.getScope());
			OAuthDao oAuthDao = new OAuthDao();;
			boolean validateClient = oAuthDao.validateClient(con, oAuthTokenReq, expires);
			
			
			if (log.isDebugEnabled()) {
				log.debug("TOKEN_EXPIRE_TIME", expires[0]);
				log.debug("REFRESH_TOKEN_EXPIRE_TIME", expires[1]);
			}
		
			Random rand = new Random(); 

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
			String tokenId  = sdf.format(date) +"."+rand.nextInt(10000);
			
			
			JWebTokenUtil jwt1 = new JWebTokenUtil(oAuthRefTokenReq, expires[0], tokenId);
			String encodedTokenNew = jwt1.toString();

			JWebTokenUtil jwt2 = new JWebTokenUtil(oAuthRefTokenReq, expires[1], tokenId);
			String encodedTokenRefNew = jwt2.toString();
			

			
			oAuthDao.refreshToken(con, oAuthRefTokenReq, encodedTokenNew, encodedTokenRefNew, expires);

			OAuthTokenRes oAuthTokenRes= new OAuthTokenRes();

			oAuthTokenRes.setToken(encodedTokenNew);
			oAuthTokenRes.setRefreshToken(encodedTokenRefNew);
			
			
			oAuthTokenRes.setStatus(PretupsI.RESPONSE_SUCCESS);
			oAuthTokenRes.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			oAuthTokenRes.setMessage(resmsg);
			oAuthTokenRes.setServerTime(new Date());
			return oAuthTokenRes;
		} catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#generateToken");
				mcomCon = null;
			}
		}
	}

	public static OAuthTokenRes generateToken(OAuthTokenReq oAuthTokenReq) throws Exception {
	
		String methodName = "generateToken";
	
		Connection con = null;
		MComConnectionI mcomCon = null;
	
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes();
			UserDAO userDAO = new UserDAO();
	
			boolean validUser = validateUser(con, oAuthTokenReq);
			
			
			if (validUser == true) {
				
				long rtExp =(Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REFRESH_TOKEN_EXPIRE_TIME);
				long tExp = (Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TOKEN_EXPIRE_TIME);
				
	
				if (log.isDebugEnabled()) {
					log.debug("TOKEN_EXPIRE_TIME", tExp);
					log.debug("REFRESH_TOKEN_EXPIRE_TIME", rtExp);
				}
				
				JWebTokenUtil jwt1 = new JWebTokenUtil(oAuthTokenReq, tExp);
				String encodedToken = jwt1.toString();
				
				JWebTokenUtil jwt2 = new JWebTokenUtil(oAuthTokenReq, rtExp);
				String encodedTokenEnc = jwt2.toString();
				
				oAuthTokenRes.setToken(encodedToken);
				oAuthTokenRes.setRefreshToken(encodedTokenEnc);
				
				userDAO.generateToken(con, oAuthTokenReq, oAuthTokenRes, tExp+"", rtExp+"");
			}
			oAuthTokenRes.setStatus(PretupsI.RESPONSE_SUCCESS);
			oAuthTokenRes.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
					PretupsErrorCodesI.SUCCESS, null);
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		} catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#generateToken");
				mcomCon = null;
			}
		}
	}


	public static OAuthTokenRes generateToken11(OAuthTokenReq oAuthTokenReq) throws Exception {

		String methodName = "generateToken";

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes();
			UserDAO userDAO = new UserDAO();

			boolean validUser = validateUser(con, oAuthTokenReq);
			
			
			if (validUser == true) {
				
				long rtExp =(Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REFRESH_TOKEN_EXPIRE_TIME);
				long tExp = (Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TOKEN_EXPIRE_TIME);
				

				if (log.isDebugEnabled()) {
					log.debug("TOKEN_EXPIRE_TIME", tExp);
					log.debug("REFRESH_TOKEN_EXPIRE_TIME", rtExp);
				}
				
				JWebTokenUtil jwt1 = new JWebTokenUtil(oAuthTokenReq, tExp);
				String encodedToken = jwt1.toString();
				
				JWebTokenUtil jwt2 = new JWebTokenUtil(oAuthTokenReq, rtExp);
				String encodedTokenEnc = jwt2.toString();
				
				oAuthTokenRes.setToken(encodedToken);
				oAuthTokenRes.setRefreshToken(encodedTokenEnc);
				
				userDAO.generateToken(con, oAuthTokenReq, oAuthTokenRes, tExp+"", rtExp+"");
			}
			oAuthTokenRes.setStatus(PretupsI.RESPONSE_SUCCESS);
			oAuthTokenRes.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
					PretupsErrorCodesI.SUCCESS, null);
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		} catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#generateToken");
				mcomCon = null;
			}
		}
	}
	
	
	public static OAuthTokenRes generateTokenApi(OAuthTokenRequest oAuthTokenReq, String scope, HttpServletRequest req) throws Exception {

		String methodName = "generateTokenApi";

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes();
		
			UserVO userVO = validateUserApi(con, oAuthTokenReq, req);
				
				
				Long[] expires = new Long[2];
				OAuthDao oAuthDao = new OAuthDao();;
				boolean validateClient = oAuthDao.validateClient(con, oAuthTokenReq, expires);
				if(validateClient ==  false) {
					throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);	
				}

				Random rand = new Random(); 

				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
				String tokenId  = sdf.format(date) +"."+rand.nextInt(10000);
				
				
				JWebTokenUtil jwt1 = new JWebTokenUtil(oAuthTokenReq, expires[0], userVO.getLoginID(), userVO.getMsisdn(), tokenId);
				String encodedToken = jwt1.toString();
				
				JWebTokenUtil jwt2 = new JWebTokenUtil(oAuthTokenReq, expires[1],userVO.getLoginID(), userVO.getMsisdn(), tokenId);
				String encodedTokenEnc = jwt2.toString();
				
				oAuthTokenRes.setToken(encodedToken);
				oAuthTokenRes.setRefreshToken(encodedTokenEnc);

				if (log.isDebugEnabled()) {
					log.debug("TOKEN_EXPIRE_TIME", expires[0]);
					log.debug("REFRESH_TOKEN_EXPIRE_TIME", expires[1]);
				}
				
				oAuthDao.generateToken(con, oAuthTokenReq, oAuthTokenRes,expires, userVO.getLoginID(), userVO.getMsisdn(), userVO.getExternalCode(), userVO.getUserID(), scope, oAuthTokenReq.getClientId());
			
			oAuthTokenRes.setStatus(PretupsI.RESPONSE_SUCCESS);
			oAuthTokenRes.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		} catch (BTSLBaseException be) {
			log.error(methodName, "BTSLBaseException occured " + be);
			throw be;
		} catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#generateToken");
				mcomCon = null;
			}
		}
	}
	
	
	
	
	/**
	 * Overloaded method for validatetoken
	 * @param oAuthreqVo
	 * @param headers
	 * @param response1
	 * @throws Exception
	 */
	public static void validateToken(OAuthUser oAuthreqVo, MultiValueMap<String, String> headers,HttpServletResponse response1) throws Exception {

		String methodName = "validateToken";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			if(headers != null ) {
				for(String str: headers.keySet()) {
					log.debug(str, headers.getFirst(str));
				}
				}
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			
			String token = null;
			if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
				token = headers.get("authorization").get(0);
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
			}else {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
			}
			
			if(token != null && token.trim().length() > 0) {
			
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			
			UserDAO userDAO = new UserDAO();
			userDAO.validateToken(con, oAuthreqVo, token);
			
			}
			
		} catch (BTSLBaseException be) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			throw be;
		} 
		catch (Exception e) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#validateToken");
				mcomCon = null;
			}
		}
	}

	/**
	 * Overloaded method for validatetoken
	 * @param oAuthreqVo
	 * @param headers
	 * @param response1
	 * @throws Exception
	 */
	public static void validateTokenApi(OAuthUser oAuthreqVo, MultiValueMap<String, String> headers,HttpServletResponse response1) throws Exception {

		String methodName = "validateTokenApi";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			if(headers != null ) {
				for(String str: headers.keySet()) {
					log.debug(str, headers.getFirst(str));
				}
				}
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			
			String token = null;
			if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
				token = headers.get("authorization").get(0);
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
			}else {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
			}
			
			if(token != null && token.trim().length() > 0) {
			
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			OAuthDao oAuthDao = new OAuthDao();;
			oAuthDao.validateToken(con, oAuthreqVo, token);
			
			}
			
		} catch (BTSLBaseException be) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			throw be;
		} 
		catch (Exception e) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#validateToken");
				mcomCon = null;
			}
		}
	}


	/**
	 * Overloaded method for validatetoken
	 * @param oAuthreqVo
	 * @param headers
	 * @param response1
	 * @throws Exception
	 */
	public static void validateTokenApi(OAuthUser oAuthreqVo, HashMap<String, String> headers,HttpServletResponse response1) throws Exception {

		String methodName = "validateTokenApi";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			
			String token = null;
			if(headers != null && headers.get("authorization") != null) {
				token = headers.get("authorization");
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
			}else {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
			}
			
			if(token != null && token.trim().length() > 0) {
			
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			OAuthDao oAuthDao = new OAuthDao();;
			oAuthDao.validateToken(con, oAuthreqVo, token);
			
			}
			
		} catch (BTSLBaseException be) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			throw be;
		} 
		catch (Exception e) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			log.error(methodName, "Exception occured " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OAuthenticationUtil#validateToken");
				mcomCon = null;
			}
		}
	}

	

/**
 * Overloaded method for validatetoken
 * @param oAuthreqVo
 * @param headers
 * @param response1
 * @throws Exception
 */
public static void validateToken(OAuthUser oAuthreqVo, MultiValueMap<String, String> headers,BaseResponseMultiple response1) throws Exception {

	String methodName = "validateToken";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered ");
		if(headers != null ) {
			for(String str: headers.keySet()) {
				log.debug(str, headers.getFirst(str));
			}
			}
	}

	Connection con = null;
	MComConnectionI mcomCon = null;

	try {
		
		String token = null;
		if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
			token = headers.get("authorization").get(0);
			if(token != null && token.contains("Bearer")) {
				token = token.substring(token.indexOf("Bearer")+6).trim();
			}
		}else {
			response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
		}
		
		if(token != null && token.trim().length() > 0) {
		
		JWebTokenUtil.validateToken(token);	
		
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();

		
		UserDAO userDAO = new UserDAO();
		userDAO.validateToken(con, oAuthreqVo, token);
		
		}
		
	} catch (BTSLBaseException be) {
		response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
		throw be;
	} 
	catch (Exception e) {
		response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
		log.error(methodName, "Exception occured " + e);
		throw e;
	} finally {
		if (mcomCon != null) {
			mcomCon.close("OAuthenticationUtil#validateToken");
			mcomCon = null;
		}
	}
}




/**
 * Overloaded method for validatetoken
 * @param oAuthreqVo
 * @param headers
 * @param response1
 * @throws Exception
 */
public static void validateTokenApi(OAuthUser oAuthreqVo, MultiValueMap<String, String> headers,BaseResponseMultiple response1) throws Exception {

	String methodName = "validateTokenApi";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered ");
		if(headers != null ) {
			for(String str: headers.keySet()) {
				log.debug(str, headers.getFirst(str));
			}
			}
	}

	Connection con = null;
	MComConnectionI mcomCon = null;

	try {
		
		String token = null;
		if(headers != null && headers.get("authorization") != null && headers.get("authorization").size() > 0) {
			token = headers.get("authorization").get(0);
			if(token != null && token.contains("Bearer")) {
				token = token.substring(token.indexOf("Bearer")+6).trim();
			}
		}else {
			response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			throw new BTSLBaseException("OAuthenticationUtil", methodName, PretupsErrorCodesI.UNAUTHORIZED_REQUEST, PretupsI.UNAUTHORIZED_ACCESS,null);
		}
		
		if(token != null && token.trim().length() > 0) {
		
			JWebTokenUtil.validateToken(token);	
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthDao oAuthDao = new OAuthDao();;
			oAuthDao.validateToken(con, oAuthreqVo,token);
		
		}
		
	} catch (BTSLBaseException be) {
		response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
		throw be;
	} 
	catch (Exception e) {
		response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
		log.error(methodName, "Exception occured " + e);
		throw e;
	} finally {
		if (mcomCon != null) {
			mcomCon.close("OAuthenticationUtil#validateToken");
			mcomCon = null;
		}
	}
}



}
