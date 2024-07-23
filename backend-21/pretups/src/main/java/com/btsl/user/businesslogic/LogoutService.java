package com.btsl.user.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.login.LoginLoggerVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.oauth.businesslogic.OAuthDao;
import com.btsl.user.businesslogic.entity.LocaleMaster;
import com.btsl.user.businesslogic.entity.UsersLoginInfo;



/**
 * This service used UserLogout.
 *
 * @author VENKATESAN.S
 */
@Service
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LogoutService extends CommonService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

	private LogoutRequest logoutRequest;

	@Autowired
	private UsersLoginQueryRep usersLoginRepository;

	@Autowired
	private UsersLoginRepository usrLoginRepository;

	/*@Autowired
	private TokenProcessor tokenProcessor;
*/
	@Autowired
	private VMSCacheRepository vmsCacheRepository;

	@Autowired
	private LocaleMasterRepository localeMasterRepository;

	private boolean succes = false;

	/**
	 * Construct LogoutService.
	 *
	 * @param UsersLoginQueryRep - usersLoginQueryRep
	 */
	@Autowired
	public LogoutService(UsersLoginQueryRep usersLoginRepository, LocaleMasterRepository localeMasterRepository) {
		this.usersLoginRepository = usersLoginRepository;
		this.localeMasterRepository = localeMasterRepository;
	}

	/**
	 * Operation of LogoutService
	 * 
	 * @param LogoutRequest - request
	 * @return LogoutResponse
	 */
	@Transactional
	public LogoutResponse execute(LogoutRequest logoutRequest,LoginLoggerVO loginLogger) {
		LOGGER.info("LogoutService.execute start");
		List<String> params = new ArrayList<>();
		this.logoutRequest = logoutRequest;
		validateInputs();
		logoutprocess(logoutRequest,loginLogger);
		if (succes) {
			responseStatus = Constants.SUCCESS_RESPONSE.getStrValue();
			responseMessageCode = MessageCodes.USER_LOGOUT_SUCCESS.getStrValue();
		} else {
			responseStatus = Constants.BAD_REQUEST.getStrValue();
			responseMessageCode = MessageCodes.NO_RECORDS_FOUND.getStrValue();
		}

		responseLanguage = 1;
		generateReponseMessage(params);
		LOGGER.info("LogoutService.execute end");
		return createResponse();
	}

	@Override
	public void validateInputs() {
		if (CommonUtils.isNullorEmpty(logoutRequest.getUserLang())) {
			LOGGER.error("userLang. Language field invalid");
			throw new ValidationException(Constants.USER_LANGUAGE.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}
		if (!CommonUtils.isNullorEmpty(logoutRequest.getUserLang())) {

			List<LocaleMaster> localeMasterlist = localeMasterRepository.getLocalMasterList();
			LocaleMaster localeMaster = localeMasterlist.stream().
					filter(locale -> logoutRequest.getUserLang().equalsIgnoreCase(locale.getId().getLanguage()))
					.findFirst().orElse(null);
			if (CommonUtils.isNullorEmpty(localeMaster)) {
				throw new ValidationException(Constants.USER_LANGUAGE.getStrValue(),
						MessageCodes.FIELD_INVALID.getStrValue());
			}
		}

		if (CommonUtils.isNullorEmpty(logoutRequest.getUniqueSessionId())) {
			LOGGER.error("Firstloginjti. firstLoginJTI field invalid");
			throw new ValidationException(Constants.USER_FIRSTLOGINJTI.getStrValue(),
					MessageCodes.FIELD_MANDATORY.getStrValue());
		}

	}
	
	private void dealForceLogout(Connection p_con, String token) throws BTSLBaseException, SQLException {

		OAuthDao oauthDao = new OAuthDao();
		oauthDao.deleteToken(p_con, null, null, token);

	}

	//@Override
	public void logoutprocess(LogoutRequest logoutRequest, LoginLoggerVO loggerVO) {
		//UsersLoginInfo userLoginInf = new UsersLoginInfo();
		//userLoginInf.setFirstLoginJti(logoutRequest.getUniqueSessionId());
		//UsersLoginInfo usrlog = usrLoginRepository.getDataById(logoutRequest.getUniqueSessionId());
		MComConnection mcomCon = null;
		Connection con = null;

		
		
		//if (!CommonUtils.isNullorEmpty(usrlog)) {
			succes = true;
			//usersLoginRepository.deleteUsersLoginInfo(userLoginInf);
			
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				OAuthUser oAuthreqVo=new OAuthUser();
				OAuthDao oAuthDao = new OAuthDao();;
				OAuthUserData data=new OAuthUserData();
				oAuthreqVo.setData(data);
				String token = null;
				String headerValue = logoutRequest.getUniqueSessionId();
				token = headerValue;
				oAuthDao.validateToken(con, oAuthreqVo, token);
				loggerVO.setLoginID(oAuthreqVo.getData().getLoginid());
				loggerVO.setUserID(oAuthreqVo.getData().getUserid());
				dealForceLogout(con, logoutRequest.getUniqueSessionId());
				con.commit();
			}catch(Exception e) {
				
			}finally {
				try {
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				mcomCon.close("logoutprocess");
			}
/*			
			
			CacheManager.getCacheHashMap().forEach((key, value) -> {
				CachedObject cacheObj = (CachedObject) value;
				LOGGER.debug(
						MessageFormat.format("Cache object data {0} -> {1}", key, cacheObj.getIdentifier()));
				LoggedInUserInfo loggedinUserInfo = (LoggedInUserInfo) cacheObj.getObjectVal();
				LOGGER.debug( MessageFormat.format("Cache object data {0} -> {1}", key,
						loggedinUserInfo.getLoginIdentifier()));
				if (cacheObj.isExpired()) {
					LOGGER.debug("ThreadCleanerUpper Running. Found an Expired Object in the Cache.");
				}

			});

			CachedObject cachedObj = (CachedObject) CacheManager.getCache(userLoginInf.getFirstLoginJti());
			if (cachedObj != null) {
				LoggedInUserInfo loggedinUserInfo = (LoggedInUserInfo) cachedObj.getObjectVal();
				vmsCacheRepository.blackListJWTToken(loggedinUserInfo.getJwttoken());
				tokenProcessor.deleteToken(loggedinUserInfo.getJwttoken());
			}*/
		//}

	}

	@Override
	public LogoutResponse createResponse() {
		LogoutResponse response = new LogoutResponse();
		response.setExternalRefId(externalRefId);
		response.setStatus(responseStatus);
		response.setMessage(responseMessage);
		response.setMessageCode(responseMessageCode);
		return response;
	}

}
