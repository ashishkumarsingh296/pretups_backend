package com.restapi.users.logiid;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.pretups.iat.util.IATCommonUtil;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.util.JUnitConfig;
import com.web.pretups.forgotpassword.web.ChangePasswordVO;

import java.sql.Connection;

import java.util.ArrayList;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {LoginServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginServiceImplTest {
    @Autowired
    private LoginServiceImpl loginServiceImpl;

    /**
     * Method under test: {@link LoginServiceImpl#sendRandomPassword(String, OperatorUtilI, BaseResponse, HttpServletResponse, OTPRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendRandomPassword() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.users.logiid.LoginServiceImpl.sendRandomPassword(LoginServiceImpl.java:140)
        //   See https://diff.blue/R013 to resolve this issue.

        IATCommonUtil operatorUtili = new IATCommonUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        OTPRequestVO requestVO = new OTPRequestVO();
        requestVO.setLoginId("42");
        requestVO.setMode("Mode");
        requestVO.setReSend("Re Send");
        loginServiceImpl.sendRandomPassword("Mode", operatorUtili, response, responseSwag, requestVO);
    }

    /**
     * Method under test: {@link LoginServiceImpl#sendRandomPassword(String, OperatorUtilI, BaseResponse, HttpServletResponse, OTPRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendRandomPassword2() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.users.logiid.LoginServiceImpl.sendRandomPassword(LoginServiceImpl.java:140)
        //   See https://diff.blue/R013 to resolve this issue.

        OperatorUtil operatorUtili = new OperatorUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        OTPRequestVO requestVO = new OTPRequestVO();
        requestVO.setLoginId("42");
        requestVO.setMode("Mode");
        requestVO.setReSend("Re Send");
        loginServiceImpl.sendRandomPassword("Mode", operatorUtili, response, responseSwag, requestVO);
    }

    /**
     * Method under test: {@link LoginServiceImpl#sendRandomPassword(String, OperatorUtilI, BaseResponse, HttpServletResponse, OTPRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendRandomPassword3() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.users.logiid.LoginServiceImpl.sendRandomPassword(LoginServiceImpl.java:140)
        //   See https://diff.blue/R013 to resolve this issue.

        OperatorUtilI operatorUtili = mock(OperatorUtilI.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        OTPRequestVO requestVO = new OTPRequestVO();
        requestVO.setLoginId("42");
        requestVO.setMode("Mode");
        requestVO.setReSend("Re Send");
        loginServiceImpl.sendRandomPassword("Mode", operatorUtili, response, responseSwag, requestVO);
    }

    /**
     * Method under test: {@link LoginServiceImpl#validateOTP(ValidateOTPResponseVO, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateOTP() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.users.logiid.LoginServiceImpl.validateOTP(LoginServiceImpl.java:300)

        // Arrange
        // TODO: Populate arranged inputs
        ValidateOTPResponseVO response = new ValidateOTPResponseVO();

        String OTP = "1357";
        HttpServletResponse responseSwag = mock(HttpServletResponse.class);

        doNothing().when(responseSwag).setStatus(Mockito.anyInt());

        // Act
        this.loginServiceImpl.validateOTP(response, OTP, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link LoginServiceImpl#validateNewPassword(String, Connection, HttpServletResponse, ChangePasswordVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateNewPassword() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.users.logiid.LoginServiceImpl.validateNewPassword(LoginServiceImpl.java:421)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        ChangePasswordVO requestVO = new ChangePasswordVO();
        requestVO.setConfirmPassword("iloveyou");
        requestVO.setNewPassword("iloveyou");
        requestVO.setOldPassword("iloveyou");
        loginServiceImpl.validateNewPassword("42", JUnitConfig.getConnection(), responseSwag, requestVO);
    }

    /**
     * Method under test: {@link LoginServiceImpl#validateNewPassword(String, Connection, HttpServletResponse, ChangePasswordVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateNewPassword2() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.users.logiid.LoginServiceImpl.validateNewPassword(LoginServiceImpl.java:421)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        ChangePasswordVO requestVO = new ChangePasswordVO();
        requestVO.setConfirmPassword("iloveyou");
        requestVO.setNewPassword("iloveyou");
        requestVO.setOldPassword("iloveyou");
        loginServiceImpl.validateNewPassword("42", JUnitConfig.getConnection(), responseSwag, requestVO);
    }

    /**
     * Method under test: {@link LoginServiceImpl#validatePasswordOnLogin(String, Connection, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidatePasswordOnLogin() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.users.logiid.LoginServiceImpl.validatePasswordOnLogin(LoginServiceImpl.java:662)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        loginServiceImpl.validatePasswordOnLogin("42", JUnitConfig.getConnection(), new CustomResponseWrapper(new Response()));
    }
}

