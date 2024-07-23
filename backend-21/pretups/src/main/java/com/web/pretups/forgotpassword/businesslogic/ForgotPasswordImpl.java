package com.web.pretups.forgotpassword.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.forgotpassword.web.PasswordForm;

@Service
@Lazy
@Scope("session")
public class ForgotPasswordImpl {

    private static final Log LOG = LogFactory.getLog(ForgotPasswordImpl.class.getName());

    ForgotPasswordImpl() {
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("sendRandomPassword", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ForgotPasswordImpl[sendRandomPassword]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    @Autowired
    private PasswordDAO passwordDAO;
    private String email = null;
    private String msisdn = null;
    private String networkCode = null;
    private String languageCode = null;
    private String country = null;
    private String userName = null;
    private String otp = null;
    private String userId = null;
    private String mode = null;
    private ArrayList<UserVO> userList = null;
    Connection con = null;
    MComConnectionI mcomCon = null;
    private int incorrectOtpCount = 0;
    private int maxOtpAttempts = Integer.parseInt(Constants.getProperty("OTP_MAX_ATTEMPTS"));

    private OperatorUtilI operatorUtili = null;

    public void validateMsisdnEmail(PasswordForm passwordForm) throws BTSLBaseException, SQLException {
        final String methodName = "validateMsisdnEmail";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered loginId :" + passwordForm.getLoginId() + "mode :" + passwordForm.getMode());
        }
        try{
        mcomCon = new MComConnection();
	    con=mcomCon.getConnection();
		
        userList = passwordDAO.loadUserDetails(passwordForm.getLoginId(), con);
        if (userList.isEmpty()) {
            throw new BTSLBaseException("ForgotPasswordImpl", "loadDetails", "forgot.password.loginId.not.found","forgotpassword/forgotPassword");

        } else if (userList.size() > 1) {
            throw new BTSLBaseException("ForgotPasswordImpl", "loadDetails", "forgot.password.multiple.loginId.found","forgotpassword/forgotPassword");
        }

        email = (String) userList.get(0).getEmail();
        msisdn = (String) userList.get(0).getMsisdn();
        networkCode = (String) userList.get(0).getNetworkID();
        languageCode = (String) userList.get(0).getLanguage();
        country = (String) userList.get(0).getCountry();
        userName = (String) userList.get(0).getUserName();
        userId = (String) userList.get(0).getUserID();

        switch (passwordForm.getMode()) {

        case PretupsI.FORGOT_PASSWORD_MODE_EMAIL:
            if (BTSLUtil.isNullString(email)) {
                throw new BTSLBaseException("ForgotPasswordImpl", "loadDetails", "forgot.password.mode.email.not.found","forgotpassword/forgotPassword");
            }
            break;
        case PretupsI.FORGOT_PASSWORD_MODE_SMS:
            if (BTSLUtil.isNullString(msisdn)) {
                throw new BTSLBaseException("ForgotPasswordImpl", "loadDetails", "forgot.password.mode.sms.not.found","forgotpassword/forgotPassword");
            }
            break;

        }
        }
        finally
        {
        if (mcomCon != null) {
			mcomCon.close("ForgotPasswordImpl#validateMsisdnEmail");
			mcomCon = null;
		}
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
        }

    }

    public void sendRandomPassword(String mode) {
        final String methodName = "sendRandomPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered mode :" + mode);
        }
        try {
            otp = operatorUtili.generateOTP();
        } catch (Exception e) {
            LOG.errorTrace("sendRandomPassword", e);
        }
        this.mode = mode;
        send();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }

    }

    public void validateOtpCaptcha(String inputOtp, String captcha, HttpServletRequest request) throws BTSLBaseException {
        final String methodName = "validateOtpCaptcha";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        
		if(!inputOtp.equals(otp)){
			request.getSession().setAttribute(com.btsl.common.ImageCaptcha.CAPTCHA_KEY, null);
			incorrectOtpCount++;
			if(incorrectOtpCount>maxOtpAttempts){
				incorrectOtpCount=0;
				throw new BTSLBaseException( "forgot.password.otp.wrong.attempts", 
					new String[] { Constants.getProperty("OTP_MAX_ATTEMPTS") },"forgotpassword/forgotPassword");
			}else{
				throw new BTSLBaseException( this, methodName,"forgot.password.otp.not.valid", "forgotpassword/changePassword");
			}
		}
		String parm = request.getParameter("j_captcha_response");
        String jcaptchaCode1 = (String) request.getSession().getAttribute(com.btsl.common.ImageCaptcha.CAPTCHA_KEY);
        
        if (parm != null && jcaptchaCode1 != null) {
            if (!parm.equals(jcaptchaCode1)) {
            	request.getSession().setAttribute(com.btsl.common.ImageCaptcha.CAPTCHA_KEY, null);
            	throw new BTSLBaseException("ForgotPasswordImpl", "validateOtpCaptcha", "captcha.error.wrongentry","forgotpassword/changePassword");   
            }
        }
        if (parm == null || BTSLUtil.isNullString(parm) || jcaptchaCode1 == null || BTSLUtil.isNullString(jcaptchaCode1)) {
        		request.getSession().setAttribute(com.btsl.common.ImageCaptcha.CAPTCHA_KEY, null);
            	throw new BTSLBaseException("ForgotPasswordImpl", "validateOtpCaptcha","captcha.error.wrongentry","forgotpassword/changePassword");
        }
        request.getSession().setAttribute(com.btsl.common.ImageCaptcha.CAPTCHA_KEY, null);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
    }

    public void validateNewPassword(String newPassword, String confirmPassword, String loginId) throws BTSLBaseException, SQLException {
        final String methodName = "validateNewPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try{
        mcomCon = new MComConnection();
	    con=mcomCon.getConnection();
        if (!newPassword.equals(confirmPassword)) {
            throw new BTSLBaseException("ForgotPasswordImpl", "validateNewPassword", "forgot.password.newconfrim.mismatch","forgotpassword/resetPassword");
        }
        if (newPassword.substring(0, 1).equals("*")) {
            throw new BTSLBaseException("ForgotPasswordImpl", "validateNewPassword", "user.changepassword.label.password.invalid","forgotpassword/resetPassword");
        }
        final int result = BTSLUtil.isSMSPinValid(newPassword);// for consecutive
        // and
        if(result == -1){
        	 throw new BTSLBaseException("ForgotPasswordImpl", "validateNewPassword", "operatorutil.validatepassword.error.passwordsamedigit","forgotpassword/resetPassword");
        }
        else if(result == 1){
        	 throw new BTSLBaseException("ForgotPasswordImpl", "validateNewPassword", "operatorutil.validatepassword.error.passwordconsecutive","forgotpassword/resetPassword");
        	
        }
        
        final HashMap messageMap = operatorUtili.validatePassword(loginId, newPassword);
        if (!messageMap.isEmpty()) {
            throw new BTSLBaseException("ForgotPasswordImpl", "validateNewPassword", messageMap,"forgotpassword/resetPassword");
        }

        final boolean passwordExist = passwordDAO.checkPasswordHistory(newPassword, userId, con);
        if (passwordExist) {
            throw new BTSLBaseException( "user.modifypwd.error.newpasswordexistcheck",new String[] { String
                .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()) },"forgotpassword/resetPassword");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
        }
        finally
        {
            if (mcomCon != null) {
    			mcomCon.close("ForgotPasswordImpl#validateNewPassword");
    			mcomCon = null;
    		}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
            }
    }

    public void updateNewPassword(String newPassword) throws BTSLBaseException, SQLException {
        final String methodName = "updateNewPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try{
        mcomCon = new MComConnection();
	    con=mcomCon.getConnection();
        final int count = passwordDAO.updatePassword(newPassword, userId, con);
        if (count <= 0) {
            throw new BTSLBaseException("ForgotPasswordImpl", "updateNewPassword", "error.general.processing");
        }else{
        	mcomCon.finalCommit();
        }
        }
        finally
        {
            if (mcomCon != null) {
    			mcomCon.close("ForgotPasswordImpl#updateNewPassword");
    			mcomCon = null;
    		}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
            }

    }

    public void send() {
        final String methodName = "send";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        switch (mode) {
        case PretupsI.FORGOT_PASSWORD_MODE_EMAIL:
            final String to = email;
            final String from = Constants.getProperty("mail_from_admin");
            final String subject = Constants.getProperty("FORGOT_PASSWORD_SUBJECT");
            final String message = "Dear " + userName + "," + Constants.getProperty("FORGOT_PASSWORD_MESSAGE") + "    " + otp;
            EMailSender.sendMail(to, from, "", "", subject, message, false, "", "");
            break;

        case PretupsI.FORGOT_PASSWORD_MODE_SMS:
            PushMessage pushMessage = null;
            BTSLMessages btslPushMessage = null;
            Locale locale = null;
            if (!BTSLUtil.isNullString(languageCode) && !BTSLUtil.isNullString(country)) {
                locale = new Locale(languageCode, country);
            } else {
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }
            final String[] arr = { userName, otp };
            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_OTP, arr);
            pushMessage = new PushMessage(msisdn, btslPushMessage, "", "", locale, networkCode);
            pushMessage.push();
            break;

        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
    }
}
