package com.restapi.users.logiid;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
////import org.apache.struts.action.ActionForm;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EMailSender;
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
import com.btsl.login.LoginForm;
import com.btsl.login.UserOtpDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOtpVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;
import com.web.pretups.forgotpassword.businesslogic.PasswordDAO;
import com.web.pretups.forgotpassword.web.ChangePasswordVO;
import com.web.pretups.forgotpassword.web.ForgotPasswordVO;
import com.web.pretups.forgotpassword.web.PasswordForm;

@Service("LoginService")
public class LoginServiceImpl implements LoginService{
	public static final Log LOG = LogFactory.getLog(LoginServiceImpl.class.getName());
    public static final String  classname = "LoginServiceImpl";

   
    private PasswordDAO passwordDAO;
    private String loginId =null;
    private String languageCode = null;
    private String country = null;
    private String userName = null;
    private String msisdn = null;
    private String networkCode = null;

   
    
    @Override
    public void validateMsisdnEmail(OTPRequestVO passwordForm) throws BTSLBaseException, SQLException{
    	  final String methodName = "validateMsisdnEmail";
          if (LOG.isDebugEnabled()) {
              LOG.debug(methodName, "Entered loginId :" + passwordForm.getLoginId() + "mode :" + passwordForm.getMode());
          }
          Connection con = null;
          MComConnectionI mcomCon = null;
          ArrayList<UserVO> userList = null;
          String email = null;
          try{
          mcomCon = new MComConnection();
  	      con=mcomCon.getConnection();
  	      passwordDAO = new PasswordDAO();
          userList = passwordDAO.loadUserDetails(passwordForm.getLoginId(), con);
          if (userList.isEmpty()) {
              throw new BTSLBaseException(classname,methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID,0 ,null);

          } else if (userList.size() > 1) {
              throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.MULTIPLE_USER_EXIST,"forgotpassword/forgotPassword");
          }
          networkCode = (String) userList.get(0).getNetworkID();

          email = (String) userList.get(0).getEmail();
          msisdn = (String) userList.get(0).getMsisdn();
          loginId = passwordForm.getLoginId();
          languageCode = (String) userList.get(0).getLanguage();
          country = (String) userList.get(0).getCountry();
          userName = (String) userList.get(0).getUserName();
         switch (passwordForm.getMode()) {

          case PretupsI.FORGOT_PASSWORD_MODE_EMAIL:
              if (BTSLUtil.isNullString(email)) {
                  throw new BTSLBaseException(classname, methodName,PretupsErrorCodesI.INVALID_EMAIL,"forgotpassword/forgotPassword");
              }
              break;
          case PretupsI.FORGOT_PASSWORD_MODE_SMS:
              if (BTSLUtil.isNullString(msisdn)) {
                  throw new BTSLBaseException(classname,methodName, PretupsErrorCodesI.MSISDN_NULL,"forgotpassword/forgotPassword");
              }
              break;

          }
          }
          finally
          {
          if (mcomCon != null) {
  			mcomCon.close(classname+"#"+methodName);
  			mcomCon = null;
  		}
          if (LOG.isDebugEnabled()) {
              LOG.debug(methodName, "Exited");
          }
          }

    }
    
    
    @Override
    public void sendRandomPassword(String mode , OperatorUtilI operatorUtili,BaseResponse response,HttpServletResponse responseSwag,OTPRequestVO requestVO) throws BTSLBaseException {
        final String methodName = "sendRandomPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered mode :" + mode);
        }
		Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));

        Connection con = null;
        MComConnectionI mcomCon = null;
        int otpValidityPeriodInPreference = (Integer) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_PERIOD);
		String validForMessage = BTSLDateUtil.getTimeFromSeconds((int) otpValidityPeriodInPreference);

//      switch(mode) {
//      case PretupsI.FORGOT_PASSWORD_MODE_EMAIL:

        try {
        	  if(BTSLUtil.isNullString(msisdn)) {
      			response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
      			response.setMessage("Please Send OTP first.");
      			throw new BTSLBaseException(classname, methodName,
      					PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
      		}
        	UserDAO userDAO = new UserDAO();
        	UserVO userVO = new UserVO();
        	mcomCon = new MComConnection();
       	    con=mcomCon.getConnection();
        	UserOtpDAO userOtpDAO = new UserOtpDAO();
        	
        

			int otpResendTImesInPreference = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_RESEND_TIMES)));
			int otpResendDurationInPreference = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_RESEND_DURATION)));
		
			userVO = userDAO.loadUsersDetails(con, msisdn);
			if(BTSLUtil.isNullObject(userVO)) {
        		throw new BTSLBaseException(classname, methodName,
      					PretupsErrorCodesI.PARENT_USER_GEOGRAPHY_NOT_FOUND, 0, null);
        	}
        
			Date currDate = new Date();
			ChannelUserVO channelUserVO = (ChannelUserVO) userVO;

			//sending otp
			Boolean otpsent = false;
			if(recentOtpGeneration(channelUserVO, otpValidityPeriodInPreference, 
					otpResendDurationInPreference, otpResendTImesInPreference)) {
				LOG.info(methodName, "send otp:  "+channelUserVO.getOTP());
				
				if(mode.equalsIgnoreCase(PretupsI.FORGOT_PASSWORD_MODE_EMAIL)) {
					LOG.info(methodName, "sending email otp");
					new UserOtpDAO().sendOTPForForgotPassword(con, channelUserVO, "", otpValidityPeriodInPreference);
					response.setStatus(HttpStatus.SC_OK);
					response.setMessageCode(PretupsErrorCodesI.OTP_SENT_ON_EMAIL);
				 	String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_SENT_ON_EMAIL,new String[]{String.valueOf(validForMessage)});
					response.setMessage(msg);
					otpsent = true;
					
					
				}else if(mode.equalsIgnoreCase(PretupsI.FORGOT_PASSWORD_MODE_SMS)){
					LOG.info(methodName, "sending sms otp");
					 PushMessage pushMessage = null;
		             BTSLMessages btslPushMessage = null;
		             if (!BTSLUtil.isNullString(languageCode) && !BTSLUtil.isNullString(country)) {
		                 locale = new Locale(languageCode, country);
		             } else {
		                 locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		             }
		             final String[] arr = { userName, channelUserVO.getOTP()};
		             btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_OTP, arr);
		             pushMessage = new PushMessage(msisdn, btslPushMessage, "", "", locale, networkCode);
		             pushMessage.push();
		             
		             response.setStatus(HttpStatus.SC_OK);
		             response.setMessageCode(PretupsErrorCodesI.OTP_SENT_ON_SMS);
		    		 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_SENT_ON_SMS,new String[]{String.valueOf(validForMessage)});
		    			response.setMessage(msg);
		    			otpsent = true;
				}
				
				//update invalid count to zero
				if(otpsent)
				userOtpDAO.updateInvalidCountOfOtp(con, msisdn, 0,currDate);
					
			}else {
				LOG.info(methodName, "don't send otp, throw error");
//				//
//				response.setStatus(HttpStatus.SC_OK);
//	             response.setMessageCode(PretupsErrorCodesI.OTP_SENT_ON_SMS);
//	    		 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MUTLIPLE_OTP_SEND_ATTEMPTS_BLOCK,
//	    				 new String[]{BTSLDateUtil.getTimeFromSeconds((int) otpResendDurationInPreference)});
//	    			response.setMessage(msg);
	    			throw new BTSLBaseException(classname,methodName, PretupsErrorCodesI.MUTLIPLE_OTP_SEND_ATTEMPTS_BLOCK,0,
	    					new String[]{BTSLDateUtil.getTimeFromSeconds((int) otpResendDurationInPreference)} ,null);
			}
			

			
			
        } catch(BTSLBaseException be) {
        	throw be;
        }
        catch (Exception e) {
            LOG.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
            if(response.getMessage()==null) {	
        		String msg=RestAPIStringParser.getMessage(locale, e.getMessage(),null);
		        response.setMessageCode(e.getMessage());
		        response.setMessage(msg);
        	}
        }
        finally {
        	 if (mcomCon != null) {
       			mcomCon.close(classname+"#"+methodName);
       			mcomCon = null;
       		}
               if (LOG.isDebugEnabled()) {
                   LOG.debug(methodName, "Exited");
               }
             
		}
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }

    }
    
   
    private Boolean recentOtpGeneration(ChannelUserVO p_userVO, int validity, int duration, int times) throws BTSLBaseException, Exception {

		final String methodName = "recentOtpGeneration";
		UserOtpDAO userOtpDAO = new UserOtpDAO();
		int otpResendDuration = 0;
		 Connection con = null;
		    MComConnectionI mcomCon = null;


		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			// calling function to generate otp and send it
			return userOtpDAO.generateSendOTPForForgotPassword(con, p_userVO, "",  validity, duration, times);

		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Error occured", "Exception " + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
		}

	}

    @Override
    public void validateOTP(ValidateOTPResponseVO response, String OTP,HttpServletResponse responseSwag) {
        final String methodName = "validateOTP";
        UserOtpDAO userOtpDAO =null;
        UserOtpVO userOtpVO =null;
        Connection con = null;
        MComConnectionI mcomCon = null;
		Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));

        try {
        	userOtpDAO = new UserOtpDAO();
        	mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		if(BTSLUtil.isNullString(msisdn)) {
    			response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				response.setMessage("Please Send OTP first.");
    			throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
    		}
        	userOtpVO = userOtpDAO.getDetailsOfUser(con, msisdn);
        	int validityPeriodOtp = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_PERIOD));  //in minutes
			int invalidCountLimit = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_INVALID_OTP));
			int blockTime = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.BLOCK_TIME_INVALID_OTP));   //in minutes
			Date generatedTime = null;
			Date barredDate = null;
			Date consumedOn = null;
			barredDate = userOtpVO.getBarredDate();
			generatedTime = userOtpVO.getGeneratedOn();
			consumedOn = userOtpVO.getConsumedOn();
			Date currDate = new Date();
			String correctOtp = null;
			int invalidCount = 0;
			if(!BTSLUtil.isNullString(userOtpVO.getInvalidCount())){
				invalidCount = Integer.parseInt(userOtpVO.getInvalidCount());
				}
			if(!BTSLUtil.isNullString(userOtpVO.getOtppin())){	
				//correctOtp = BTSLUtil.decryptText(userOtpVO.getOtppin());
				correctOtp = userOtpVO.getOtppin();
				}
			int updateCnt = 0;
			if((BTSLUtil.getDifferenceInUtilDatesinSeconds(generatedTime, currDate) > validityPeriodOtp)){
				response.setDisbaleResend(false);
				response.setMessageCode(PretupsErrorCodesI.OTP_EXPIRED);
				throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.OTP_EXPIRED, 0, null);
			}
			else {
				if((invalidCountLimit == invalidCount) && (BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate) < blockTime)){
					int blockTimeRem = BTSLUtil.parseLongToInt((blockTime - BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate)));
					String blocktimeConverted = BTSLDateUtil.getTimeFromSeconds(blockTimeRem);
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_MAX_INVALID,new String[]{blocktimeConverted});
					response.setMessage(msg);
					response.setMessageCode(PretupsErrorCodesI.OTP_MAX_INVALID);
					response.setDisbaleResend(true);
					throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
							PretupsErrorCodesI.OTP_MAX_INVALID, 0, null);
				}
				else {
					if( BTSLUtil.encryptText(OTP).equals(correctOtp) && consumedOn==null){
						response.setStatus(HttpStatus.SC_OK);
						response.setMessageCode(PretupsErrorCodesI.SUCCESS);
						response.setMessage("OTP validation successful.");
						if(loginId==null) {
							response.setMessage("Please Send OTP first.");
			        		throw new BTSLBaseException(classname, methodName,
			      					PretupsErrorCodesI.FAILED, 0, null);
						}
						response.setLoginId(loginId);
					}
					else {
						invalidCount++;
						
					
					if(invalidCount > invalidCountLimit){
						invalidCount = 1;
					}
					updateCnt = userOtpDAO.updateInvalidCountOfOtp(con, msisdn, invalidCount,currDate);
					if(invalidCount == invalidCountLimit){
						
						String blocktimeConverted = BTSLDateUtil.getTimeFromSeconds(blockTime);
						
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_MAX_INVALID,new String[]{blocktimeConverted});
						response.setMessage(msg);
						response.setMessageCode(PretupsErrorCodesI.OTP_MAX_INVALID);
						response.setDisbaleResend(true);
						throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
								PretupsErrorCodesI.OTP_MAX_INVALID, 0, null);
					}
					int attemptsLeft = invalidCountLimit-invalidCount;
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_OTP_ATTEMPT,new String[]{String.valueOf(attemptsLeft)});
					response.setMessage(msg);
					response.setMessageCode(PretupsErrorCodesI.INVALID_OTP_ATTEMPT);
					throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
							PretupsErrorCodesI.INVALID_OTP_ATTEMPT, 0, null);
					}
				}// end of unbarred user
			}//end of inexpired otp
			
			
        	
        }catch(Exception e) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	if(response.getMessage()==null) {	
        		String msg=RestAPIStringParser.getMessage(locale, e.getMessage(),null);
		        response.setMessageCode(e.getMessage());
		        response.setMessage(msg);
        	}
        	try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
        	}
				catch (SQLException esql) {
					LOG.error(methodName,"SQLException : ", esql.getMessage());
				}
			
        }
        finally {
        	if (mcomCon != null) {
				mcomCon.close(classname+"#"+methodName);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, " Exited ");
			}
        }
        	
        }
    
   
	@Override
	public PasswordChangeResponseVO validateNewPassword(String loginId, Connection con,
			HttpServletResponse responseSwag, ChangePasswordVO requestVO) throws BTSLBaseException {
		PasswordDAO passwordDAO = new PasswordDAO();
		PasswordChangeResponseVO response = new PasswordChangeResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		final String METHOD_NAME = "changePassword";
		OperatorUtilI operatorUtili = null;
		try {
			requestVO.setOldPassword(AESEncryptionUtil.aesDecryptor(requestVO.getOldPassword(), Constants.A_KEY));
			requestVO.setNewPassword(AESEncryptionUtil.aesDecryptor(requestVO.getNewPassword(), Constants.A_KEY));
			requestVO.setConfirmPassword(
					AESEncryptionUtil.aesDecryptor(requestVO.getConfirmPassword(), Constants.A_KEY));

			String oldP = requestVO.getOldPassword();

			List<String> storedPasswordDec = new ArrayList<String>();
			ChannelUserVO userVO = new ChannelUserVO();
			UserDAO userDAO = new UserDAO();
			String newP = requestVO.getNewPassword();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			String oldUserPassword = BTSLUtil.decryptText(userVO.getPassword());
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        
			// IF old password is correct
			String pinPasswordEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
			if("SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
				if(!BTSLUtil.encryptText(oldP).equals(userVO.getPassword())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OLD_PASSWORD_ENTERED_IS_WRONG, 0,
							null);
				}
			}
			else if (!oldP.equals(BTSLUtil.decryptText(userVO.getPassword()))) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OLD_PASSWORD_ENTERED_IS_WRONG, 0,
						null);
			}
			

			if (oldP.equals(newP)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CHANGE_PASSWORD_OLD_NEW_SAME, 0,
						null);
			}

			if (oldUserPassword.equals(newP)) {
				String msg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD,
						new String[] { Integer.toString(SystemPreferences.PREV_PIN_NOT_ALLOW) });
				response.setMessageCode(PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD);
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;

			}

			boolean prevFlag = passwordDAO.checkPasswordHistory(newP, userVO.getUserID(), con);

			if (prevFlag) {
				String msg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD,
						new String[] { Integer.toString(SystemPreferences.PREV_PIN_NOT_ALLOW) });
				response.setMessageCode(PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD);
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;
			}

			boolean flag = true;
			Integer minLoginPwdLength =  (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH);
			Integer maxLoginPwdLength =  (Integer)PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH);
			if (!BTSLUtil.isNullString(newP)) {
//				if (newP.length() >= 4) {

					for (String s1 : storedPasswordDec) {
						if (newP.equals(s1)) {
							flag = false;
							break;
						}

					}

					if (flag == true) {
						final Map errorMessageMap = operatorUtili.validatePassword(loginId, newP);
							if (null != errorMessageMap && errorMessageMap.size() > 0) {
								Integer minLoginPwdLength1 = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH);
								Integer maxLoginPwdLength1 = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH);
								final String[] argsArray = { minLoginPwdLength1.toString(), maxLoginPwdLength1.toString()};
								throw new BTSLBaseException("LoginImpl", METHOD_NAME,
										PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID_MODIFIED,argsArray);

							}
						
						String confirmP = requestVO.getConfirmPassword();
						if (confirmP.equals(newP)) {
							newP = BTSLUtil.encryptText(newP);
							final Date currentDate = new Date();
							int count;
							count = userDAO.changePassword(con, userVO.getUserID(), newP, currentDate,
									userVO.getUserID(), null);

							if (count > 0) {
								con.commit();
								response.setStatus((HttpStatus.SC_OK));
								String resmsg = RestAPIStringParser.getMessage(
										new Locale(SystemPreferences.DEFAULT_LANGUAGE,
												SystemPreferences.DEFAULT_COUNTRY),
										PretupsErrorCodesI.CHANGE_PASSWORD_SUCCESS, null);
								response.setMessage(resmsg);
								response.setMessageCode(PretupsErrorCodesI.CHANGE_PASSWORD_SUCCESS);

							} else {
								con.rollback();
								throw new BTSLBaseException(classname, METHOD_NAME,
										PretupsErrorCodesI.CHANGE_PASSWORD_FAILED, 0, null);
							}

						} else {
							throw new BTSLBaseException(classname, METHOD_NAME,
									PretupsErrorCodesI.CHANGE_PASSWORD_NEW_CONFIRM_NOTSAME, 0, null);
						}
					} else {
						String msg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD,
								new String[] { Integer.toString(SystemPreferences.PREV_PIN_NOT_ALLOW) });
						response.setMessageCode(PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD);
						response.setMessage(msg);
						response.setStatus(HttpStatus.SC_BAD_REQUEST);
						return response;
					}
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NEW_PASSWORD_IS_NULL, 0, null);
			}
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.CHANGE_PASSWORD_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CHANGE_PASSWORD_FAILED);
		}

		return response;

	}

	@Override
	public PasswordChangeResponseVO forgotPassword(Connection con, HttpServletResponse responseSwag,
			ForgotPasswordVO requestVO) throws BTSLBaseException {
		PasswordDAO passwordDAO = new PasswordDAO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		PasswordChangeResponseVO response = new PasswordChangeResponseVO();
		final String METHOD_NAME = "forgotPassword";
		OperatorUtilI operatorUtili = null;
		try {

			ChannelUserVO userVO = new ChannelUserVO();
			UserDAO userDAO = new UserDAO();
			String newP = AESEncryptionUtil.aesDecryptor(requestVO.getNewPassword(), Constants.A_KEY);
			userVO = userDAO.loadAllUserDetailsByLoginID(con, requestVO.getConfirmloginId());
			boolean prevFlag = passwordDAO.checkPasswordHistory(newP, userVO.getUserID(), con);
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();

			if (prevFlag) {
				String msg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD,
						new String[] { Integer.toString(SystemPreferences.PREV_PIN_NOT_ALLOW) });
				response.setMessageCode(PretupsErrorCodesI.NEW_PASSWORD_MATCHES_OLD_PASSWORD);
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;

			}

			if (!BTSLUtil.isNullString(newP)) {
					
					final Map errorMessageMap = operatorUtili.validatePassword(requestVO.getConfirmloginId(), newP);
	                if (null != errorMessageMap && errorMessageMap.size() > 0) {
						Integer minLoginPwdLength1 = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH);
						Integer maxLoginPwdLength1 = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH);
						final String[] argsArray = { minLoginPwdLength1.toString(), maxLoginPwdLength1.toString()};
						throw new BTSLBaseException("LoginImpl", METHOD_NAME,
								PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID_MODIFIED,argsArray);

					}

					String confirmP = AESEncryptionUtil.aesDecryptor(requestVO.getConfirmPassword(), Constants.A_KEY);
					if (confirmP.equals(newP)) {
						newP = BTSLUtil.encryptText(newP);
						final Date currentDate = new Date();
						int count;
						count = userDAO.changePassword(con, userVO.getUserID(), newP, currentDate, userVO.getUserID(),
								null);
						if (count > 0) {
							con.commit();
							response.setStatus((HttpStatus.SC_OK));
							String resmsg = RestAPIStringParser.getMessage(
									new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
									PretupsErrorCodesI.CHANGE_PASSWORD_SUCCESS, null);
							response.setMessage(resmsg);
							response.setMessageCode(PretupsErrorCodesI.CHANGE_PASSWORD_SUCCESS);
						} else {
							con.rollback();
							throw new BTSLBaseException(classname, METHOD_NAME,
									PretupsErrorCodesI.CHANGE_PASSWORD_FAILED, 0, null);
						}
					} else {
						throw new BTSLBaseException(classname, METHOD_NAME,
								PretupsErrorCodesI.CHANGE_PASSWORD_NEW_CONFIRM_NOTSAME, 0, null);
					}
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NEW_PASSWORD_IS_NULL, 0, null);
			}

		} catch (BTSLBaseException be) {

			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(resmsg);
			}

			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.CHANGE_PASSWORD_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CHANGE_PASSWORD_FAILED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}
	
	
	
	@Override
	public PasswordChangeOnLoginVO validatePasswordOnLogin(String loginId, Connection con,
			HttpServletResponse responseSwag) throws BTSLBaseException {
		PasswordChangeOnLoginVO response = new PasswordChangeOnLoginVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		final String METHOD_NAME = "passwordChangeOnLogin";
		boolean isFirstTimeLogin = false;
		OperatorUtilI operatorUtil = null;
		try {
			Date modifiedOn = null;
			ChannelUserVO userVO = null;
			LoginDAO loginDAO = new LoginDAO();
			Date currentDate = new Date();
			userVO = loginDAO.loadUserDetails(con, loginId, METHOD_NAME, locale);
			Date resetPasswordExpiredTime = null;
			String password = BTSLUtil.decryptText(userVO.getPassword());
			if (userVO != null) {
				modifiedOn = userVO.getModifiedOn();
			}
			if (userVO.getLastLoginOn() == null && !isFirstTimeLogin) {
				isFirstTimeLogin = true;
			}

			if (userVO.getPasswordModifiedOn() != null) {
				Calendar cal = BTSLDateUtil.getInstance();
				cal.setTime(userVO.getPasswordModifiedOn());
				int resetPasswordExpiredInHours = ((Integer) PreferenceCache.getControlPreference(
						PreferenceI.RESET_PASSWORD_EXPIRED_TIME_IN_HOURS, userVO.getNetworkID(),
						userVO.getCategoryCode())).intValue();
				cal.add(Calendar.HOUR, resetPasswordExpiredInHours);
				resetPasswordExpiredTime = cal.getTime();
			}
			boolean resetPwdOnCreationflag = false;
			if(userVO.getAuthTypeAllowed().equals(PretupsI.STATUS_ACTIVE)){
				response.setIsPasswordChange(false);
				throw new BTSLBaseException(this, METHOD_NAME,
						PretupsErrorCodesI.CANNOT_REDIRECT_TO_CHANGE_PASSWORD_SCREEN, PretupsI.RESPONSE_FAIL, null);
			}
			if (userVO.getLastLoginOn() == null || isFirstTimeLogin || userVO.getPasswordModifiedOn() == null) {
				try {
					String utilClass = (String) PreferenceCache
							.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
					operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
				} catch (Exception e) {
					LOG.errorTrace(METHOD_NAME, e);
				}
				resetPwdOnCreationflag = operatorUtil.checkPasswordPeriodToResetAfterCreation(modifiedOn, userVO);
				if (resetPwdOnCreationflag && "Y".equals(userVO.getPasswordReset())) {
					response.setIsPasswordChange(false);
					throw new BTSLBaseException(this, METHOD_NAME,
							PretupsErrorCodesI.CANNOT_REDIRECT_TO_CHANGE_PASSWORD_SCREEN, PretupsI.RESPONSE_FAIL, null);
				}

				response.setIsPasswordChange(true);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN);

			} else if ("Y".equals(userVO.getPasswordReset()) && currentDate.before(resetPasswordExpiredTime)) {
				response.setIsPasswordChange(true);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN);
			} else if ("Y".equals(userVO.getPasswordReset()) && currentDate.after(resetPasswordExpiredTime)) {
				response.setIsPasswordChange(false);
				throw new BTSLBaseException(this, METHOD_NAME,
						PretupsErrorCodesI.CANNOT_REDIRECT_TO_CHANGE_PASSWORD_SCREEN, PretupsI.RESPONSE_FAIL, null);

			} else if (SystemPreferences.C2S_DEFAULT_PASSWORD.equals(password))

			{
				response.setIsPasswordChange(true);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN);
			} else {
				Date date1 = userVO.getPasswordModifiedOn();
				Date date2 = new Date();

				long dt1 = date1.getTime();
				long dt2 = date2.getTime();
				long nodays = ((dt2 - dt1) / (1000 * 60 * 60 * 24));
				long noPasswordTimeOutDays = 0;
				try {
					noPasswordTimeOutDays = ((Integer) PreferenceCache.getControlPreference(
							PreferenceI.DAYS_AFTER_CHANGE_PASSWORD, userVO.getNetworkID(), userVO.getCategoryCode()))
									.intValue();
				} catch (Exception e) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(METHOD_NAME, "noPasswordTimeOutDays not found in Constants.props");
					}
					LOG.errorTrace(METHOD_NAME, e);
				}
				if (!BTSLUtil.isStringIn(userVO.getCategoryCode(),
						SystemPreferences.CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY) && nodays > noPasswordTimeOutDays) {
					response.setIsPasswordChange(true);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(
							new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.REDIRECT_TO_CHANGE_PASSWORD_SCREEN);
				} else {
					response.setIsPasswordChange(false);
					throw new BTSLBaseException(this, METHOD_NAME,
							PretupsErrorCodesI.CANNOT_REDIRECT_TO_CHANGE_PASSWORD_SCREEN, PretupsI.RESPONSE_FAIL, null);
				}
			}

		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				response.setIsPasswordChange(false);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setIsPasswordChange(false);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.CANNOT_REDIRECT_TO_CHANGE_PASSWORD_SCREEN, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CANNOT_REDIRECT_TO_CHANGE_PASSWORD_SCREEN);
		}

		return response;

	}

}
