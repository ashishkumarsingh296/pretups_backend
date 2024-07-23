package com.restapi.users.logiid;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.pretups.util.OperatorUtilI;
import com.web.pretups.forgotpassword.web.ChangePasswordVO;
import com.web.pretups.forgotpassword.web.ForgotPasswordVO;
import com.web.pretups.forgotpassword.web.PasswordForm;

@Service
public interface LoginService {
	
	/**
	 * @author sarthak.saini
	 * @param passwordForm
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
    public void validateMsisdnEmail(OTPRequestVO passwordForm) throws BTSLBaseException, SQLException ;

    /**
     * 
     * @param mode
     * @throws BTSLBaseException 
     */
    public void sendRandomPassword(String mode,OperatorUtilI operatorUtili,BaseResponse response,HttpServletResponse responseSwag,OTPRequestVO requestVO) throws BTSLBaseException;

    /**
     * 
     * @param response
     * @param OTP
     */
    public void validateOTP(ValidateOTPResponseVO response, String OTP,HttpServletResponse responseSwag);
    
    /**
     * 
     * @param loginId
     * @param con
     * @param responseSwag
     * @param requestVO
     * @return
     * @throws BTSLBaseException
     */
    public PasswordChangeResponseVO validateNewPassword(String loginId, Connection con,HttpServletResponse responseSwag,ChangePasswordVO requestVO) throws BTSLBaseException;
    
    /**
     * 
     * @param con
     * @param responseSwag
     * @param requestVO
     * @return
     * @throws BTSLBaseException
     */
    public PasswordChangeResponseVO forgotPassword(Connection con, HttpServletResponse responseSwag,ForgotPasswordVO requestVO) throws BTSLBaseException;
    
    /**
     * 
     * @param loginId
     * @param con
     * @param responseSwag
     * @param requestVO
     * @return
     * @throws BTSLBaseException
     */
   public PasswordChangeOnLoginVO validatePasswordOnLogin(String loginId, Connection con,HttpServletResponse responseSwag) throws BTSLBaseException;
}
