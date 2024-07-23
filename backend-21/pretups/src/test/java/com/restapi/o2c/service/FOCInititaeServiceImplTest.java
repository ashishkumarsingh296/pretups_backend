package com.restapi.o2c.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;

import java.sql.Connection;

import java.util.ArrayList;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {FOCInititaeServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class FOCInititaeServiceImplTest {
    @Autowired
    private FOCInititaeServiceImpl fOCInititaeServiceImpl;

    /**
     * Method under test: {@link FOCInititaeServiceImpl#processFocInitiateRequest(FocInitiateRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessFocInitiateRequest() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.processFocInitiateRequest(FOCInititaeServiceImpl.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        FocInitiateRequestVO focInitiateRequestVO = new FocInitiateRequestVO();
        focInitiateRequestVO.setData(data);
        focInitiateRequestVO.setDatafoc(new ArrayList<>());
        focInitiateRequestVO.setLoginId("42");
        focInitiateRequestVO.setMsisdn("Msisdn");
        focInitiateRequestVO.setPassword("iloveyou");
        focInitiateRequestVO.setPin("Pin");
        focInitiateRequestVO.setReqGatewayCode("Req Gateway Code");
        focInitiateRequestVO.setReqGatewayLoginId("42");
        focInitiateRequestVO.setReqGatewayPassword("iloveyou");
        focInitiateRequestVO.setReqGatewayType("Req Gateway Type");
        focInitiateRequestVO.setServicePort("Service Port");
        focInitiateRequestVO.setSourceType("Source Type");
        fOCInititaeServiceImpl.processFocInitiateRequest(focInitiateRequestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link FOCInititaeServiceImpl#processFoCInitiate(ChannelUserVO, FocTransferInitaiateReqData, ErrorMap, ArrayList, int)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessFoCInitiate() {
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
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.processFoCInitiate(FOCInititaeServiceImpl.java:222)

        // Arrange
        // TODO: Populate arranged inputs
        ChannelUserVO UserVO = null;
        FocTransferInitaiateReqData focInitiateRequestData = null;
        ErrorMap errorMap = null;
        ArrayList<BaseResponse> baseResponseFinalSucess = null;
        int rownum = 0;

        // Act
        boolean actualProcessFoCInitiateResult = this.fOCInititaeServiceImpl.processFoCInitiate(UserVO,
                focInitiateRequestData, errorMap, baseResponseFinalSucess, rownum);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link FOCInititaeServiceImpl#getOperatorDetails(Connection, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetOperatorDetails() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: java.lang.NullPointerException
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:754)
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:676)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection p_con = mock(Connection.class);
        fOCInititaeServiceImpl.getOperatorDetails(JUnitConfig.getConnection(), ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link FOCInititaeServiceImpl#getOperatorDetails(Connection, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetOperatorDetails2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: java.lang.NullPointerException
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:754)
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:676)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection p_con = mock(Connection.class);
        fOCInititaeServiceImpl.getOperatorDetails(JUnitConfig.getConnection(), new ChannelUserVO());
    }

    /**
     * Method under test: {@link FOCInititaeServiceImpl#getOperatorDetails(Connection, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetOperatorDetails3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: java.lang.NullPointerException
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:754)
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:676)
        //   See https://diff.blue/R013 to resolve this issue.

        fOCInititaeServiceImpl.getOperatorDetails(mock(Connection.class), null);
    }

    /**
     * Method under test: {@link FOCInititaeServiceImpl#getOperatorDetails(Connection, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetOperatorDetails4() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: java.lang.NullPointerException
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:754)
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.FOCInititaeServiceImpl.getOperatorDetails(FOCInititaeServiceImpl.java:676)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection p_con = mock(Connection.class);
        ChannelUserVO sender = mock(ChannelUserVO.class);
        when(sender.getLoginID()).thenReturn("Login ID");
        when(sender.getPassword()).thenReturn("iloveyou");
        fOCInititaeServiceImpl.getOperatorDetails(JUnitConfig.getConnection(), sender);
    }
}

