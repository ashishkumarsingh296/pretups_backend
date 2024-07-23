package com.btsl.pretups.requesthandler;

//added by shashank for channel user authentication

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.oauth.businesslogic.OAuthDao;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.common.TokenRequestVO;
import com.restapi.common.TokenResponseVO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

/**
 * com.btsl.pretups.requesthandler.UserAuthorizationHandler.java
 * 
 * This handler works to handle XML request of user Authorization. when XML
 * request for Authentication of channel user on the basis of MSISDN ,LOGIN
 * ID,PIN and PASSWORD. 1.process() this method is called the 2.
 * validateUserAuthorization() method
 * 
 * which decide the calls and authentication of user for various combination
 * MSISDN-PIN,LOGIN-PASSWORD,LOGIN-PASSWORD-PIN-MSISDN,OTHERS Combinations
 * 
 * 
 * 3.validateUserMsisdn() 4.validateUserMsisdnPin()
 * 5.validateUserMsisdnPswdPin(() 6.validateUserForOtherCase()
 * 7.validateUserLoginPassword()
 * 
 */

public class UserAuthorizationHandler implements ServiceKeywordControllerI {

	private  static Log _log = LogFactory.getLog(UserAuthorizationHandler.class.getName());
	private HashMap<String, String> _requestMap = null;
	private static final String LOGINID_STR = "USERLOGINID";
	private static final String PIN_STR = "PIN";
	private static final String PASSWORD_STR = "USERPASSWORD";
	private static boolean otpRequired=false;
	private static OperatorUtilI _operatorUtil=null;
	
	
	static
    {
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	    try
		{
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		}
		catch(Exception e)
		{
			_log.errorTrace("static",e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserAuthorizationHandler[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
    }	

	/**
	 * This method is the entry point in the class and is declared in the
	 * Interface ServiceKeywordControllerI This method processes the request for
	 * the MSISDN OR USERLOGINID calls the validate() for validating MSISDN,
	 * USERLOGINID calls the loadChannelUserDetails() that sets the Channel User
	 * details in the channelUserVO and sets the Channel User details in the
	 * p_requestVO
	 * 
	 * @param p_requestVO
	 *            RequestVO
	 */
	public void process(RequestVO p_requestVO) {
		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled())
		{
			_log.debug("process", "Entered.....p_requestVO=" + p_requestVO);
		}
		// Connection con =null;

		_requestMap = p_requestVO.getRequestMap();
		
		
		// Flag based OTP Message 
		if(!BTSLUtil.isNullString(Constants.getProperty("OTP_ALLOWED")) && PretupsI.NO.equalsIgnoreCase(Constants.getProperty("OTP_ALLOWED")))
            p_requestVO.setSenderMessageRequired(false);

		try {
			// con = OracleUtil.getConnection();

			this.validateUserAuthorization(p_requestVO);

		} catch (BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			// try{if (con != null){con.rollback();}} catch (Exception e){}
			_log.error("process", p_requestVO.getRequestIDStr(),
					"BTSLBaseException " + be.getMessage());
			if (be.isKey()) {
				p_requestVO.setMessageCode(be.getMessageKey());
				String[] args = be.getArgs();
				p_requestVO.setMessageArguments(args);
				p_requestVO.setTxnAuthStatus(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				p_requestVO
						.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}

		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			// try{if (con != null){con.rollback();}} catch (Exception ee){}
			_log.error("process", p_requestVO.getRequestIDStr(), "Exception "
					+ e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserAuthorizationHandler[process]", "", "", "",
					"Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
		} finally {

			// try {if (con != null)con.close();} catch (Exception e){}

			p_requestVO.setRequestMap(_requestMap);
			p_requestVO.setSenderMessageRequired(false);
			if (_log.isDebugEnabled())
				_log.debug("process", p_requestVO.getRequestIDStr(),
						"Exited.....p_requestVO=" + p_requestVO);
		}
	}

	/**
	 * @param p_con
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @author vikas.kumar validateUserAuthorization() is used for authorization
	 *         and implement the encryption and decryption logic as per
	 *         requirement
	 */
	private void validateUserAuthorization(RequestVO p_requestVO)
			throws BTSLBaseException {
		final String METHOD_NAME = "validateUserAuthorization";
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserAuthorization", "Entered.....");
		}
		String msisdn = null;
		String loginId = null;
		String pin = null;
		String pinUser = null;
		String password = null;
		boolean isValidMSISDN = false;
		String IMEI=null;
		String Otp=null;
		String mHash = null;
		String token = null;
		String gentoken= null;
		String otp_flow=null;
		int isUpdateChUserInfo = -1;
		ChannelUserTxnDAO channelUserTxnDAO= new ChannelUserTxnDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		int OtpUpdateCount=0;

		try {
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			msisdn = p_requestVO.getFilteredMSISDN();
			ChannelUserVO channeluserVO = (ChannelUserVO) p_requestVO
					.getSenderVO();
			pinUser = channeluserVO.getUserPhoneVO().getSmsPin();
			loginId = (String) _requestMap.get(LOGINID_STR);
			password = (String) _requestMap.get(PASSWORD_STR);
			pin = (String) _requestMap.get(PIN_STR);
			IMEI = (String) _requestMap.get("IMEI");
			mHash = (String) _requestMap.get("MHASH");
			token = _requestMap.get("TOKEN");
			otp_flow=(String)_requestMap.get("OTP_FLOW");
			if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(loginId)
					&& BTSLUtil.isNullString(password)
					&& BTSLUtil.isNullString(pin)) {
				p_requestVO
						.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
				p_requestVO
						.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			} else if (BTSLUtil.isNullString(msisdn)
					&& BTSLUtil.isNullString(loginId)) {
				p_requestVO
						.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
				p_requestVO
						.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			} else if (BTSLUtil.isNullString(pinUser)
					&& BTSLUtil.isNullString(password)) {
				p_requestVO
						.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
				p_requestVO
						.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			} else if(BTSLUtil.isNullString(otp_flow)){
				p_requestVO
				.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
				p_requestVO
				.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			}else {
				if (!BTSLUtil.isNullString(msisdn))
					isValidMSISDN = this
							.validateUserMsisdn(msisdn, p_requestVO);

				if (isValidMSISDN) {
					if ((!BTSLUtil.isNullString(msisdn))
							&& (!BTSLUtil.isNullString(pin))
							&& (!BTSLUtil.isNullString(loginId))
							&& (!BTSLUtil.isNullString(password))) {

						this.validateUserMsisdnPswdPin(loginId, password,
								msisdn, pin, p_requestVO);
					} else if ((!BTSLUtil.isNullString(msisdn) && (!BTSLUtil
							.isNullString(pin)))) {
						this.validateUserMsisdnPin(con,msisdn, pin, p_requestVO);
					} else if ((!BTSLUtil.isNullString(loginId) && (!BTSLUtil
							.isNullString(password)))) {
						this.validateUserForOtherCase(loginId, password,
								msisdn, pin, p_requestVO);
					} else {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);

					}
				} else if ((!BTSLUtil.isNullString(loginId))
						&& (!BTSLUtil.isNullString(password))) {
					this.validateUserLoginPassword(loginId, password,
							p_requestVO);
				}
				
				// Flag based OTP Message, IMEI Validation
				
//				String otpAllowed = Constants.getProperty("OTP_ALLOWED");
				String otpAllowed = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_REQUIRED));
				
//                if(!BTSLUtil.isNullString(otpAllowed) && PretupsI.YES.equalsIgnoreCase(otpAllowed))				
//                	this.validateUserIMEI(msisdn, IMEI, p_requestVO);
                
                if(!BTSLUtil.isNullString(otpAllowed)) {
                	if(PretupsI.ALWAYS_OTP_REQUIRED.equalsIgnoreCase(otpAllowed)) {
                		otpRequired=true;
                	}else if(PretupsI.ONE_TIME_OTP_REQUIRED.equalsIgnoreCase(otpAllowed)) {
                		this.validateUserIMEIWithOTPFLOWFlag(msisdn, IMEI, p_requestVO,otp_flow);
                	}else if(PretupsI.NO_OTP_REQUIRED.equalsIgnoreCase(otpAllowed)) {
                		otpRequired=false;
                	}
                }
				if(otpRequired)
				{
					Otp=_operatorUtil.generateOTP();
					p_requestVO.setOTP(true);
					OtpUpdateCount=channelUserTxnDAO.updateUserOTPDeatils(con, p_requestVO.getServiceType(), Otp,channeluserVO);
					if(OtpUpdateCount>0)
					{
						gentoken = generateToken();
						mcomCon.finalCommit();
						try
						{
							 BTSLMessages btslPushMessage = null;
							 PushMessage pushMessage=null;
							 String[] arr = {channeluserVO.getUserName(),Otp};
							 btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_OTP_APP,arr);
						     pushMessage=new PushMessage(msisdn,btslPushMessage,"","",p_requestVO.getLocale(),channeluserVO.getNetworkID());
					         pushMessage.push();
						}
						catch(Exception e)
						{
							_log.errorTrace(METHOD_NAME, e);	
						}
					}
					else
					{
						mcomCon.finalRollback();
						throw new BTSLBaseException(UserAuthorizationHandler.class,METHOD_NAME,PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
					}
					
				}else{
					
					if (BTSLUtil.isNullString(mHash) || !mHash.equals(p_requestVO.getmHash())) {
	                    _log.error("parseChannelRequest", "Mhash is not of length 15 or is not numeric or null or not valid");
	                    throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.MAPP_INVALID_MHASH);
					}
					
					 gentoken = generateToken();
					 
					if (mcomCon != null) {
						mcomCon.close("UserAuthorizationHandler#validateUserAuthorization");
						mcomCon = null;
					}
					 mcomCon = new MComConnection();con=mcomCon.getConnection();
					 isUpdateChUserInfo = channelUserTxnDAO.updateMhashToken(con, channeluserVO, mHash, gentoken);
					 ChannelUserBL.loadAllowedTransferForCategory(con, ((ChannelUserVO)(p_requestVO.getSenderVO())).getCategoryCode(), p_requestVO);
					 
					 // Added for Default transfer category for MAPPGW, 
					 // transfer category will only be picked on the basis of System Preference
					 if(PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayType())) {
						 updateDefaultTransferAllowed(p_requestVO);
					 }
					 
					 int isResetInvalidCount= -1;
					 isResetInvalidCount = channelUserTxnDAO.resetInvalidCount(con, channeluserVO);
					 if(isUpdateChUserInfo>0 && isResetInvalidCount>0){
						 mcomCon.finalCommit();
					}else{
						mcomCon.finalRollback();
						throw new BTSLBaseException(UserAuthorizationHandler.class,METHOD_NAME,PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
					}
					 
				}
				
			}
			
		
			String msgarr[] ={channeluserVO.getUserName().toString().trim(),channeluserVO.getCategoryVO().getCategoryName().toString().trim(),channeluserVO.getCategoryCode().toString().trim()};
			p_requestVO.setMessageArguments(msgarr);
			p_requestVO.setToken(gentoken);
			if (!BTSLUtil.isNullString(mHash) && !BTSLUtil.isNullString(IMEI)) {
				//handle forceLogout
				LoginDAO _loginDAO = new LoginDAO();
				ChannelUserVO channelUserVO2 = _loginDAO.loadUserDetails(con, channeluserVO.getLoginID(), "",
						null);
				if ((TypesI.NO).equalsIgnoreCase(channelUserVO2.getCategoryVO().getMultipleLoginAllowed())) {
					dealForceLogout(con, channeluserVO.getLoginID());
					con.commit();
				}
				
				
				TokenResponseVO tokenResponseVO = generateJwtToken(channeluserVO, "", "", "", "", "");
				p_requestVO.setJwtToken(tokenResponseVO.getToken());
				p_requestVO.setRefreshToken(tokenResponseVO.getRefreshToken());
			}
			p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_USER_LOGIN);
			if (PretupsI.MAPP_LOGIN_REQ.equals(p_requestVO.getServiceType())) {
				p_requestVO.setSenderReturnMessage(BTSLUtil.getMessage(
						p_requestVO.getLocale(),
						PretupsErrorCodesI.MAPP_USER_LOGIN, msgarr));
			}
		} catch (BTSLBaseException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("validateUserAuthorization",
					"BTSLBaseException " + e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
			_log.error("validateUserAuthorization",
					"Exception " + e.getMessage());

			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserAuthorizationHandler[validateUserAuthorization]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "validateUserAuthorization",
					PretupsErrorCodesI.REQ_NOT_PROCESS);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("UserAuthorizationHandler#validateUserAuthorization");
				mcomCon = null;
			}
		}
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserAuthorization", "Exiting  ********");
		}

	}
	
	private void dealForceLogout(Connection p_con, String loginId) throws BTSLBaseException, SQLException {
		
		OAuthDao oauthDao = new OAuthDao();
		oauthDao.deleteToken(p_con, loginId, null, null);
		
	}

	/**
	 * @param p_con
	 * @param p_loginId
	 * @param p_password
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @author vikas.kumar Method validateUserLoginPassword() is used to
	 *         validate loginid and password
	 * 
	 */
	private void validateUserLoginPassword(String p_loginId, String p_password,
			RequestVO p_requestVO) throws BTSLBaseException {
		final String METHOD_NAME = "validateUserLoginPassword";
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserLoginPassword", "Entered.....p_loginId::"
					+ p_loginId + "p_requestVO ::" + p_requestVO.toString());
		}

		ChannelUserVO channelUserVO = null;
		try {

			channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
			if (channelUserVO == null) {
				p_requestVO
						.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
				p_requestVO
						.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
				throw new BTSLBaseException(this, "validateUserLoginPassword",
						PretupsErrorCodesI.ERROR_INVALID_LOGIN);
			} else {
				if (BTSLUtil.isNullString(channelUserVO.getPassword())) {
					p_requestVO
							.setTxnAuthStatus(PretupsErrorCodesI.XML_PASSWORD_NOT_FOUND);
					p_requestVO
							.setMessageCode(PretupsErrorCodesI.XML_PASSWORD_NOT_FOUND);
					throw new BTSLBaseException(this,
							"validateUserLoginPassword",
							PretupsErrorCodesI.XML_PASSWORD_NOT_FOUND);
				} else {
					if (channelUserVO.getPassword().equals(p_password)) {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					} else {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
						throw new BTSLBaseException(this,
								"validateUserLoginPassword",
								PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
					}
				}
			}
		} catch (BTSLBaseException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("validateUserLoginPassword",
					"BTSLBaseException " + e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
			_log.error("validateUserLoginPassword",
					"Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserAuthorizationHandler[validateUserLoginPassword]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "validateUserLoginPassword",
					PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserLoginPassword", "Exiting  ********");
		}
	}

	/**
	 * @param p_con
	 * @param p_msisdn
	 * @param p_pin
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @author vikas.kumar validateUserMsisdnPin() method is used for validation
	 *         pin and msisdn
	 * 
	 */
	private void validateUserMsisdnPin(Connection p_con,String p_msisdn, String p_pin,
			RequestVO p_requestVO) throws BTSLBaseException {
		final String METHOD_NAME = "validateUserMsisdnPin";
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserMsisdnPin", "Entered.....p_msisdn::"
					+ p_msisdn + "p_requestVO ::" + p_requestVO.toString());
		}

		ChannelUserVO channelUserVO = null;
		try {

			channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
			if (channelUserVO == null) {
				p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.USER_NOT_EXIST);
				p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_EXIST);
				throw new BTSLBaseException(this, "validateUserMsisdnPin",
						PretupsErrorCodesI.USER_NOT_EXIST);
			} else {

				if (BTSLUtil.isNullString(channelUserVO.getUserPhoneVO()
						.getSmsPin())) {
					p_requestVO
							.setTxnAuthStatus(PretupsErrorCodesI.PIN_NOT_FOUND);
					p_requestVO
							.setMessageCode(PretupsErrorCodesI.PIN_NOT_FOUND);
					throw new BTSLBaseException(this, "validateUserMsisdnPin",
							PretupsErrorCodesI.PIN_NOT_FOUND);
				} else {
					// if(BTSLUtil.decryptText(channelUserVO.getPinRequired()).equals(p_pin))
					try {
						_operatorUtil.validatePIN(p_con, channelUserVO, p_pin);
					}
					catch (BTSLBaseException e)
					{
							p_requestVO
							.setTxnAuthStatus(e.getMessage());
							p_requestVO
							.setMessageCode(e.getMessage());
							p_requestVO.setSuccessTxn(false);
							throw new BTSLBaseException(this,"validateUserMsisdnPin",e.getMessage(),e.getArgs());
					}
						catch (Exception e)
					{
							p_requestVO
							.setTxnAuthStatus(e.getMessage());
							p_requestVO
							.setMessageCode(e.getMessage());
							p_requestVO.setSuccessTxn(false);
							throw new BTSLBaseException(this,"validateUserMsisdnPin",e.getMessage());
					}
				
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					
				}
			}
		} catch (BTSLBaseException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("validateUserMsisdnPin",
					"BTSLBaseException " + e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
			_log.error("validateUserMsisdnPin", "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserAuthorizationHandler[validateUserMsisdnPin]", "", "",
					"", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "validateUserMsisdnPin",
					PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserMsisdnPin", "Exiting  ********");
		}
	}

	/**
	 * @param p_msisdn
	 * @param p_requestVO
	 * @return
	 * @throws BTSLBaseException
	 * @author vikas.kumar Method validateUserMsisdnPswdPin() is used for
	 *         Validation check for all cases
	 */
	private void validateUserMsisdnPswdPin(String p_loginId, String p_password,
			String p_msisdn, String p_pin, RequestVO p_requestVO)
			throws BTSLBaseException {
		final String METHOD_NAME = "validateUserMsisdnPswdPin";
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserMsisdnPswdPin", "Entered.....p_loginId::"
					+ p_loginId + "p_msisdn" + p_msisdn);
		}

		ChannelUserVO channeluserVO = null;
		try {

			channeluserVO = (ChannelUserVO) p_requestVO.getSenderVO();

			if (channeluserVO == null) {
				p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.USER_NOT_EXIST);
				p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_EXIST);
				if (_log.isDebugEnabled())
					_log.debug("validateUserMsisdnPswdPin",
							"authentication failed login_id or password .Login ID:: ="
									+ p_loginId);
				throw new BTSLBaseException(this, "validateUserMsisdnPswdPin",
						PretupsErrorCodesI.USER_NOT_EXIST);
			} else {
				if (BTSLUtil.isNullString(channeluserVO.getPassword())) {
					p_requestVO
							.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
					p_requestVO
							.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
					throw new BTSLBaseException(this,
							"validateUserMsisdnPswdPin",
							PretupsErrorCodesI.ERROR_INVALID_PSWD);
				} else {
					if (channeluserVO.getPassword().equals(
							BTSLUtil.encryptText(p_password))) {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					} else {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
						throw new BTSLBaseException(this,
								"validateUserMsisdnPswdPin",
								PretupsErrorCodesI.ERROR_INVALID_PSWD);
					}
				}
				if (BTSLUtil.isNullString(channeluserVO.getMsisdn())) {
					p_requestVO
							.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
					p_requestVO
							.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
					throw new BTSLBaseException(this,
							"validateUserMsisdnPswdPin",
							PretupsErrorCodesI.ERROR_INVALID_MSISDN);
				} else {
					if (channeluserVO.getMsisdn().equals(p_msisdn)) {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					} else {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
						throw new BTSLBaseException(this,
								"validateUserMsisdnPswdPin",
								PretupsErrorCodesI.ERROR_INVALID_MSISDN);
					}
				}
				if (BTSLUtil.isNullString(channeluserVO.getUserPhoneVO()
						.getPinRequired()))// changed by Shashank for bug
											// removal
				{
					p_requestVO
							.setTxnAuthStatus(PretupsErrorCodesI.PIN_NOT_FOUND);
					p_requestVO
							.setMessageCode(PretupsErrorCodesI.PIN_NOT_FOUND);
					throw new BTSLBaseException(this,
							"validateUserMsisdnPswdPin",
							PretupsErrorCodesI.PIN_NOT_FOUND);
				} else {
					if (channeluserVO.getUserPhoneVO().getSmsPin()
							.equals(BTSLUtil.encryptText(p_pin)))// changed by
																	// Shashank
																	// for bug
																	// removal
					{
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

					} else {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.INVALID_PIN);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.INVALID_PIN);
						throw new BTSLBaseException(this,
								"validateUserMsisdnPswdPin",
								PretupsErrorCodesI.INVALID_PIN);
					}
				}
				if (BTSLUtil.isNullString(channeluserVO.getLoginID())) {
					p_requestVO
							.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					p_requestVO
							.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					throw new BTSLBaseException(this,
							"validateUserMsisdnPswdPin",
							PretupsErrorCodesI.ERROR_INVALID_LOGIN);
				} else {
					if (channeluserVO.getLoginID().equals(p_loginId)) {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					} else {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						throw new BTSLBaseException(this,
								"validateUserMsisdnPswdPin",
								PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					}
				}
			}

		} catch (BTSLBaseException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("validateUserMsisdnPswdPin",
					"BTSLBaseException " + e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
			_log.error("validateUserMsisdnPswdPin",
					"Exception " + e.getMessage());

			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserAuthorizationHandler[validateUserMsisdnPswdPin]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "validateUserMsisdnPswdPin",
					PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserMsisdnPswdPin", "Exiting  ********");
		}

	}

	/**
	 * @param p_msisdn
	 * @param p_requestVO
	 * @return
	 * @throws BTSLBaseException
	 * @author vikas.kumar Method validateUserMsisdn() for validate userMsisdn
	 *         belongs to particular network or not .
	 */
	private boolean validateUserMsisdn(String p_msisdn, RequestVO p_requestVO)
			throws BTSLBaseException {
		final String METHOD_NAME = "validateUserMsisdn";
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserMsisdn", "Entered.....p_msisdn::"
					+ p_msisdn);
		}
		String filteredMsisdn = null;
		String msisdnPrefix = null;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode = null;
		boolean isMSISDNValid = true;

		try {
			if (!BTSLUtil.isValidMSISDN(p_msisdn)) {
				_requestMap.put("RES_ERR_KEY", p_requestVO.getFilteredMSISDN());
				isMSISDNValid = false;
				throw new BTSLBaseException(this, "validateUserMsisdn",
						PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
			}
			filteredMsisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
			// get prefix of the MSISDN
			msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get the
																		// prefix
																		// of
																		// the
																		// MSISDN
			networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
					.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				isMSISDNValid = false;
				if (_log.isDebugEnabled())
					_log.debug("validateUserMsisdn",
							"No Network prefix found for msisdn=" + p_msisdn);
				throw new BTSLBaseException(this, "validateUserMsisdn",
						PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
			}
			// check network support of the MSISDN
			networkCode = networkPrefixVO.getNetworkCode();
			if (!networkCode.equals(((UserVO) p_requestVO.getSenderVO())
					.getNetworkID())) {
				isMSISDNValid = false;
				if (_log.isDebugEnabled())
					_log.debug("validateUserMsisdn",
							"No supporting Network for msisdn=" + p_msisdn);
				throw new BTSLBaseException(
						this,
						"validateUserMsisdn",
						PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
			}
		} catch (BTSLBaseException e) {
			_log.errorTrace(METHOD_NAME, e);
			isMSISDNValid = false;
			_log.error("validateUserMsisdn",
					"BTSLBaseException " + e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			isMSISDNValid = false;
			_log.error("validateUserMsisdn", "Exception " + e.getMessage());

			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserAuthorizationHandler[validateUserMsisdn]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "validateUserMsisdn",
					PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserMsisdn", "Exiting  ::::isMSISDNValid "
					+ isMSISDNValid);
		}

		return isMSISDNValid;
	}

	/**
	 * @param p_con
	 * @param p_loginId
	 * @param p_password
	 * @param p_msisdn
	 * @param p_pin
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @author vikas.kumar Method validateUserForOtherCase() for other cases if
	 *         Msisdn is present
	 */
	private void validateUserForOtherCase(String p_loginId, String p_password,
			String p_msisdn, String p_pin, RequestVO p_requestVO)
			throws BTSLBaseException {
		final String METHOD_NAME = "validateUserForOtherCase";
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserForOtherCase", "Entered.....p_loginId::"
					+ p_loginId + "p_msisdn" + p_msisdn + "p_password"
					+ p_password + "p_pin" + p_pin);
		}

		ChannelUserVO channeluserVO = null;
		try {

			channeluserVO = (ChannelUserVO) p_requestVO.getSenderVO();

			if (channeluserVO == null) {
				p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.USER_NOT_EXIST);
				p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_EXIST);
				if (_log.isDebugEnabled())
					_log.debug("validateUserForOtherCase",
							"authentication failed login_id or password .Login ID:: ="
									+ p_loginId);
				throw new BTSLBaseException(this, "validateUserMsisdn",
						PretupsErrorCodesI.USER_NOT_EXIST);
			} else {
				if (!BTSLUtil.isNullString(p_password)) {

					if (BTSLUtil.isNullString(channeluserVO.getPassword())) {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
						throw new BTSLBaseException(this,
								"validateUserForOtherCase",
								PretupsErrorCodesI.ERROR_INVALID_PSWD);
					} else {
						if (channeluserVO.getPassword().equals(
								BTSLUtil.encryptText(p_password))) {
							p_requestVO
									.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
							p_requestVO
									.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						} else {
							if (_log.isDebugEnabled())
								_log.debug("validateUserForOtherCase",
										"authentication failed invalid  password "
												+ channeluserVO.getPassword()
												+ ".p_password:: ="
												+ p_password);
							p_requestVO
									.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
							p_requestVO
									.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
							throw new BTSLBaseException(this,
									"validateUserForOtherCase",
									PretupsErrorCodesI.ERROR_INVALID_PSWD);

						}
					}
				}
				if (!BTSLUtil.isNullString(p_loginId)) {
					if (BTSLUtil.isNullString(channeluserVO.getLoginID())) {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						throw new BTSLBaseException(this,
								"validateUserForOtherCase",
								PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					} else {
						if (channeluserVO.getLoginID().equals(p_loginId)) {
							p_requestVO
									.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
							p_requestVO
									.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						} else {
							p_requestVO
									.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
							p_requestVO
									.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);

							throw new BTSLBaseException(this,
									"validateUserForOtherCase",
									PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						}
					}
				}

				if (!BTSLUtil.isNullString(p_pin)) {
					if (BTSLUtil.isNullString(channeluserVO.getPinRequired())) {
						p_requestVO
								.setTxnAuthStatus(PretupsErrorCodesI.PIN_NOT_FOUND);
						p_requestVO
								.setMessageCode(PretupsErrorCodesI.PIN_NOT_FOUND);
						throw new BTSLBaseException(this,
								"validateUserForOtherCase",
								PretupsErrorCodesI.PIN_NOT_FOUND);
					} else {
						if (channeluserVO.getPinRequired().equals(
								BTSLUtil.encryptText(p_pin))) {
							p_requestVO
									.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
							p_requestVO
									.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

						} else {
							p_requestVO
									.setTxnAuthStatus(PretupsErrorCodesI.INVALID_PIN);
							p_requestVO
									.setMessageCode(PretupsErrorCodesI.INVALID_PIN);
							throw new BTSLBaseException(this,
									"validateUserForOtherCase",
									PretupsErrorCodesI.INVALID_PIN);
						}
					}

				}
			}

		} catch (BTSLBaseException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("validateUserForOtherCase",
					"BTSLBaseException " + e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
			_log.error("validateUserForOtherCase",
					"Exception " + e.getMessage());

			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserAuthorizationHandler[validateUserForOtherCase]", "",
					"", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "validateUserForOtherCase",
					PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
		{
			_log.debug("validateUserForOtherCase", "Exiting  ********");
		}

	}
	
	
	private void validateUserIMEIWithOTPFLOWFlag(String p_msisdn,String IMEI,RequestVO p_requestVO,String OTP_FLOW)throws BTSLBaseException
	{
		final String METHOD_NAME = "validateUserIMEIWithOTPFLOWFlag";
		  if(_log.isDebugEnabled()){_log.debug("validateUserIMEIWithOTPFLOWFlag","Entered.....p_msisdn::"+p_msisdn+"IMEI"+IMEI);}
		  ChannelUserTxnDAO channelUserTxnDAO= new ChannelUserTxnDAO();
		  Connection con=null;MComConnectionI mcomCon = null;
		  String USERIMEI=null;
		  try
		  {
			  if(PretupsI.TRUE.equals(OTP_FLOW)) {
				  otpRequired=true;
			  }else {
				  mcomCon = new MComConnection();con=mcomCon.getConnection();
				  USERIMEI= channelUserTxnDAO.loadUserIMEIDeatils(con, p_msisdn);
				  if(!BTSLUtil.isNullString(USERIMEI))
				  {
					if(!BTSLUtil.NullToString(USERIMEI).equals(BTSLUtil.NullToString(IMEI)))  
					{
						otpRequired=true;
						
					}
					else
					{
						otpRequired=false;
					}
				  }
				  else
				  {
					  otpRequired=true;
				  }
			  }
			  
		  }
		 
	    catch(Exception e)
	    {
	  	  _log.errorTrace(METHOD_NAME,e);
	  	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
	  	  _log.error("validateUserOTP", "Exception " + e.getMessage()); 
	        
	        EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "validateUserOTP[validateUserOTP]", "", "", "", "Exception:" + e.getMessage());
	        throw new BTSLBaseException(this, "validateUserForOtherCase", PretupsErrorCodesI.REQ_NOT_PROCESS);
	    }
	    finally
	    {
			if (mcomCon != null) {
				mcomCon.close("UserAuthorizationHandler#validateUserIMEI");
				mcomCon = null;
			}
	    }
	    if(_log.isDebugEnabled()){_log.debug("validateUserOTP","Exiting  ********");}
		  
	}
	private void validateUserIMEI(String p_msisdn,String IMEI,RequestVO p_requestVO)throws BTSLBaseException
	{
		final String METHOD_NAME = "validateUserIMEI";
		  if(_log.isDebugEnabled()){_log.debug("validateUserIMEI","Entered.....p_msisdn::"+p_msisdn+"IMEI"+IMEI);}
		  ChannelUserTxnDAO channelUserTxnDAO= new ChannelUserTxnDAO();
		  Connection con=null;MComConnectionI mcomCon = null;
		  String USERIMEI=null;
		 
		  try
		  {
			  mcomCon = new MComConnection();con=mcomCon.getConnection();
			  USERIMEI= channelUserTxnDAO.loadUserIMEIDeatils(con, p_msisdn);
			  if(!BTSLUtil.isNullString(USERIMEI))
			  {
				if(!BTSLUtil.NullToString(USERIMEI).equals(BTSLUtil.NullToString(IMEI)))  
				{
					otpRequired=true;
					
				}
				else
				{
					otpRequired=false;
				}
			  }
			  else
			  {
				  otpRequired=true;
			  }
		  }
		 
	    catch(Exception e)
	    {
	  	  _log.errorTrace(METHOD_NAME,e);
	  	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
	  	  _log.error("validateUserOTP", "Exception " + e.getMessage()); 
	        
	        EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "validateUserOTP[validateUserOTP]", "", "", "", "Exception:" + e.getMessage());
	        throw new BTSLBaseException(this, "validateUserForOtherCase", PretupsErrorCodesI.REQ_NOT_PROCESS);
	    }
	    finally
	    {
			if (mcomCon != null) {
				mcomCon.close("UserAuthorizationHandler#validateUserIMEI");
				mcomCon = null;
			}
	    }
	    if(_log.isDebugEnabled()){_log.debug("validateUserOTP","Exiting  ********");}
		  
	}
	
	private String generateToken()
	{
		String token=null;
		try
		{
		String imei=(String)_requestMap.get("IMEI");
		String mHash=(String)_requestMap.get("MHASH");
		Date date= new Date();
		
		String timestamp = BTSLUtil.getDateTimeStringFromDate(date);
		
		StringBuilder hashInput=new StringBuilder(imei);
		hashInput.append(mHash).append(timestamp); 
		MessageDigest mdigest = MessageDigest.getInstance("SHA-256");
		byte[] mdbytes = mdigest.digest(hashInput.toString().getBytes("UTF-8"));
		
		StringBuilder tokenString = new StringBuilder();
    	for (int i=0;i<mdbytes.length;i++) {
    		tokenString.append(Integer.toHexString(0xFF & mdbytes[i]));
    	}
		
    	token = tokenString.toString();
		
		}		
		catch(Exception e)
		{
			_log.error("generateToken",e);
			
		}
		
		return token;
		
	}

	private TokenResponseVO generateJwtToken(ChannelUserVO channelUser,String requestGatewayType,String requestGatewayCode,String requestGatewayLoginId,
            String requestGatewayPassword,String servicePort){
		    
//			final String uri = "http://172.30.38.232:9879/pretups/rstapi/v1/generateTokenAPI";
//			final String uri = "http://localhost:8080/pretups/rstapi/v1/generateTokenAPI";
			
			final String uri = com.btsl.util.Constants.getProperty("GENERATE_TOKEN_URL");
			
			RestTemplate restTemplate = new RestTemplate();
			TokenRequestVO tokenRequestVO = new TokenRequestVO();
			tokenRequestVO.setIdentifierType("msisdn");
			tokenRequestVO.setIdentifierValue(channelUser.getMsisdn());
			tokenRequestVO.setPasswordOrSmspin(BTSLUtil.decryptText(channelUser.getUserPhoneVO().getSmsPin()));
			HttpHeaders headers = new HttpHeaders();
			
//			headers.set("CLIENT_ID", "a");
//			headers.set("CLIENT_SECRET", "a");
//			headers.set("requestGatewayCode", "REST");
//			headers.set("requestGatewayLoginId","pretups123");
//			headers.set("requestGatewayPsecure", "1357");
//			headers.set("requestGatewayType", "REST");
//			headers.set("servicePort","190");
//			headers.set("scope","All");
			
			headers.set("CLIENT_ID", com.btsl.util.Constants.getProperty("CLIENT_ID"));
			headers.set("CLIENT_SECRET", com.btsl.util.Constants.getProperty("CLIENT_SECRET"));
			headers.set("requestGatewayCode", com.btsl.util.Constants.getProperty("REQ_GATEWAY_CODE"));
			headers.set("requestGatewayLoginId","pretups123");
			headers.set("requestGatewayPsecure", com.btsl.util.Constants.getProperty("REQ_GATEWAY_PSECURE"));
			headers.set("requestGatewayType", com.btsl.util.Constants.getProperty("REQ_GATEWAY_TYPE"));
			headers.set("servicePort","190");
			headers.set("scope","All");
			
			HttpEntity<TokenRequestVO> request = new HttpEntity<>(tokenRequestVO, headers);
			TokenResponseVO tokenResponse = restTemplate.postForObject(uri, request, TokenResponseVO.class);
			if (_log.isDebugEnabled())
			{
				_log.debug("generateJwtToken", "JWT Token = " + tokenResponse.getToken() + "Refresh Token = " + tokenResponse.getRefreshToken());
			}			
			return tokenResponse;
}
	/**
	 * 
	 * @param p_requestVO
	 */
	private void updateDefaultTransferAllowed(RequestVO p_requestVO) {
		String transferAllowed = p_requestVO.getInfo1();
		 String[] defTransferAllowedArr = null;
		 if( !BTSLUtil.isNullString(SystemPreferences.DEF_CHNL_TRANSFER_ALLOWED) &&  !BTSLUtil.isNullString(transferAllowed)) {
			 defTransferAllowedArr = SystemPreferences.DEF_CHNL_TRANSFER_ALLOWED.split(",");
			 for(String service: defTransferAllowedArr) {
				 if(!transferAllowed.contains(service)) {
					 transferAllowed.concat(","+ service);
				 }
			 }
		 } else if( !BTSLUtil.isNullString(SystemPreferences.DEF_CHNL_TRANSFER_ALLOWED) &&  BTSLUtil.isNullString(transferAllowed)) {
			 transferAllowed = SystemPreferences.DEF_CHNL_TRANSFER_ALLOWED;
		 }
		 
		 p_requestVO.setInfo1(transferAllowed);
	}

}
