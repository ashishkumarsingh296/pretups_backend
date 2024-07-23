package com.web.pretups.forgotpassword.web;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class PasswordFormValidator implements Validator {

    public boolean supports(Class aClass) {
        return PasswordForm.class.equals(aClass);
    }

    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "loginId", "forgot.password.loginid.required", "Required field");

    }

    public void validateOtpForm(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otp", "forgot.password.otp.required", "Required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "j_captcha_response", "forgot.password.j_captcha_response.required", "Required field");

    }

    public void validateResetForm(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "forgot.password.newPassword.required", "Required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "forgot.password.confirmPassword.required", "Required field");

    }
}
