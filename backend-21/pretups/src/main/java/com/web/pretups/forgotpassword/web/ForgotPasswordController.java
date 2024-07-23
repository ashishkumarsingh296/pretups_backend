package com.web.pretups.forgotpassword.web;


import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.security.csrf.CSRFTokenUtil;
import com.web.pretups.forgotpassword.businesslogic.ForgotPasswordImpl;

@Controller
@Lazy
@Scope("session")
public class ForgotPasswordController extends CommonController{

    private static final Log LOG = LogFactory.getLog(ForgotPasswordController.class.getName());
    private boolean sent = false;
    private PasswordForm passwordForm = null;

    @Autowired
    private PasswordFormValidator PasswordFormValidator;
    @Autowired
    private ForgotPasswordImpl ForgotPasswordImpl;

    @RequestMapping(value = "/ForgotPassword/getPasswordPage.form", method = RequestMethod.GET)
    public String loadPasswordPage(Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "loadPasswordPage";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        final HttpSession session = request.getSession();
        session.setAttribute("forgotpassword", "true");
        session.setAttribute("formName", "passwordForm");
        passwordForm = new PasswordForm();
        // passwordForm.setMode("SMS");
        model.put("passwordForm", passwordForm);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
        return "forgotpassword/forgotPassword";

    }

    @RequestMapping(value = "/ForgotPassword/processForgotPassword.form", method = RequestMethod.POST, params = "submit")
    public String processForgotPassword(@ModelAttribute("passwordForm") PasswordForm passwordForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "processForgotPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        boolean flag;
        try {
                 flag = CSRFTokenUtil.isValid(request);
                 if (!flag) {
                      if (LOG.isDebugEnabled()) {
                      LOG.debug("CSRF", "ATTACK!");
                       }
                       return "security/unAuthorisedAccessF";
                  }
            }catch (NoSuchAlgorithmException | ServletException e1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSRF", "ATTACK!");
            }
        }
        final HttpSession session = request.getSession();
        if (session.getAttribute("forgotpassword") == null) {
            session.invalidate();
            return "security/unAuthorisedAccessF";
        }

        PasswordFormValidator.validate(passwordForm, result);

        if (result.hasErrors()) {
            // modelList(model);
            return "forgotpassword/forgotPassword";
        } else {
            /*
             * if(passwordForm.getMode().equalsIgnoreCase(PretupsI.
             * FORGOT_PASSWORD_MODE_QUESTIONS)){
             * 
             * if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))).booleanValue())
             * passwordForm.setJ_captcha_response(null);
             * return null;
             * }
             * else{
             */
            try {
                ForgotPasswordImpl.validateMsisdnEmail(passwordForm);
            } catch (BTSLBaseException e) {
                LOG.errorTrace("processForgotPassword", e);
                return super.handleError(this,methodName, e, result);
                //return "forgotpassword/forgotPassword";
            } catch (SQLException e) {
            	LOG.errorTrace("processForgotPassword", e);
                return super.handleError(this,methodName, e, result);
			}

            ForgotPasswordImpl.sendRandomPassword(passwordForm.getMode());

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))).booleanValue()) {
                passwordForm.setJ_captcha_response(null);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
			 session.setAttribute("loginId", passwordForm.getLoginId());
            return "forgotpassword/changePassword";
        }

        // }

    }

    @RequestMapping(value = "/ForgotPassword/processForgotPassword.form", method = RequestMethod.POST, params = "back")
    public String redirectForgotPassword(@ModelAttribute("passwordForm") PasswordForm passwordForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "processForgotPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        final HttpSession session = request.getSession();
        if (session.getAttribute("forgotpassword") == null) {
            session.invalidate();
            return "security/unAuthorisedAccessF";
        } else {

            return "login/index";
        }

    }

    @RequestMapping(value = "/ForgotPassword/resetPassword.form", method = RequestMethod.POST, params = "next")
    public String resetPassword(@ModelAttribute("passwordForm") PasswordForm passwordForm, BindingResult result, HttpServletRequest request) {
        final String methodName = "resetPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        boolean flag;
        try {
                 flag = CSRFTokenUtil.isValid(request);
                 if (!flag) {
                      if (LOG.isDebugEnabled()) {
                      LOG.debug("CSRF", "ATTACK!");
                       }
                       return "security/unAuthorisedAccessF";
                  }
            }catch (NoSuchAlgorithmException | ServletException e1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSRF", "ATTACK!");
            }
        }
        final HttpSession session = request.getSession();
        if (session.getAttribute("forgotpassword") == null) {
            session.invalidate();
            return "security/unAuthorisedAccessF";
        }
        PasswordFormValidator.validateOtpForm(passwordForm, result);

        if (result.hasErrors()) {

            return "forgotpassword/changePassword";

        } else {

            try {
                ForgotPasswordImpl.validateOtpCaptcha(passwordForm.getOtp(), passwordForm.getJ_captcha_response(), request);
            } catch (BTSLBaseException e) {
                LOG.errorTrace("resetPassword", e);
			return super.handleError(this,methodName,
				         e, result);
			//return e.getForwardPath();
                }
            

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
            return "forgotpassword/resetPassword";
        }

    }

    @RequestMapping(value = { "/ForgotPassword/resetPassword.form", "/ForgotPassword/confirmResetPassword.form" }, method = RequestMethod.POST, params = "cancel")
    public String cancelPasswordProcessing(@ModelAttribute("passwordForm") PasswordForm passwordForm, HttpServletRequest request) {
        final String methodName = "cancelPasswordProcessing";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        final HttpSession session = request.getSession();
        session.invalidate();
        return "security/unAuthorisedAccessF";

    }

    @RequestMapping(value = "/ForgotPassword/confirmResetPassword.form", method = RequestMethod.POST, params = "confirm")
    public String confirmResetPassword(@ModelAttribute("passwordForm") PasswordForm passwordForm, BindingResult result, HttpServletRequest request) {
        final String methodName = "confirmResetPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        boolean flag;
        try {
                 flag = CSRFTokenUtil.isValid(request);
                 if (!flag) {
                      if (LOG.isDebugEnabled()) {
                      LOG.debug("CSRF", "ATTACK!");
                       }
                       return "security/unAuthorisedAccessF";
                  }
            }catch (NoSuchAlgorithmException | ServletException e1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSRF", "ATTACK!");
            }
        }
        final HttpSession session = request.getSession();
        if (session.getAttribute("forgotpassword") == null) {
            session.invalidate();
            return "security/unAuthorisedAccessF";
        }
		if(session.getAttribute("loginId")!=null){
        	passwordForm.setLoginId((String)session.getAttribute("loginId"));
        
        }
        PasswordFormValidator.validateResetForm(passwordForm, result);

        if (result.hasErrors()) {
            return "forgotpassword/resetPassword";
        } else {
            try {
                ForgotPasswordImpl.validateNewPassword(passwordForm.getNewPassword(), passwordForm.getConfirmPassword(), passwordForm.getLoginId());
                ForgotPasswordImpl.updateNewPassword(passwordForm.getNewPassword());

            } catch (BTSLBaseException e) {
                LOG.errorTrace("confirmResetPassword", e);
                return super.handleError(this,methodName,
			         e, result);
	       //  return "forgotpassword/resetPassword";
            } catch (SQLException e) {
            	LOG.errorTrace("confirmResetPassword", e);
                return super.handleError(this,methodName, e, result);
			}
            

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
            session.removeAttribute("forgotpassword");
            return "forgotpassword/passwordSuccess";
        }

    }

    @RequestMapping(value = "/ForgotPassword/resetPassword.form", method = RequestMethod.POST, params = "resend")
    public String resendPassword(@ModelAttribute("passwordForm") PasswordForm passwordForm, BindingResult result, HttpServletRequest request) {
        final String methodName = "resendPassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        if (!sent) {
            ForgotPasswordImpl.send();
            sent = true;
            result.reject("forgot.password.resend.otp");
        } else {
            result.reject("forgot.password.resend.notallowed");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
        return "forgotpassword/changePassword";

    }

}
