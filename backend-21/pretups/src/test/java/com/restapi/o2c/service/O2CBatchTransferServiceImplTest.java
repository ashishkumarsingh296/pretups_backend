package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.XssWrapper;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.iat.util.IATCommonUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {O2CBatchTransferServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CBatchTransferServiceImplTest {
    @Autowired
    private O2CBatchTransferServiceImpl o2CBatchTransferServiceImpl;

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = new O2CBatchTransferRequestVO();
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(null);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest3() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(new O2CBatchTransferDetails());
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), null, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
    public void testProcessRequest4() throws BTSLBaseException, SQLException {
        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(new O2CBatchTransferDetails());
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        O2CBatchTransferResponse actualProcessRequestResult = o2CBatchTransferServiceImpl.processRequest(
                o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr", JUnitConfig.getConnection(), locale, httprequest, headers,
                new CustomResponseWrapper(new MockHttpServletResponse()));
        assertTrue(actualProcessRequestResult.getSuccessList().isEmpty());
        assertEquals("400", actualProcessRequestResult.getStatus());
        assertEquals("Service TypeRESP", actualProcessRequestResult.getService());
        assertEquals(0, actualProcessRequestResult.getReferenceId().intValue());
        assertEquals("241198", actualProcessRequestResult.getMessageCode());
        assertNull(actualProcessRequestResult.getMessage());
        ErrorMap errorMap = actualProcessRequestResult.getErrorMap();
        List<MasterErrorList> masterErrorList = errorMap.getMasterErrorList();
        assertEquals(8, masterErrorList.size());
        assertNull(errorMap.getRowErrorMsgLists());
        MasterErrorList getResult = masterErrorList.get(0);
        assertNull(getResult.getErrorMsg());
        MasterErrorList getResult2 = masterErrorList.get(1);
        assertNull(getResult2.getErrorMsg());
        assertEquals("11100", getResult2.getErrorCode());
        assertEquals("241047", getResult.getErrorCode());
        MasterErrorList getResult3 = masterErrorList.get(6);
        assertEquals("11100", getResult3.getErrorCode());
        MasterErrorList getResult4 = masterErrorList.get(7);
        assertEquals("241077", getResult4.getErrorCode());
        assertNull(getResult3.getErrorMsg());
        assertNull(getResult4.getErrorMsg());
        verify(o2CBatchTransferRequestVO).getData();
        verify(o2CBatchTransferRequestVO).getO2CBatchTransferDetails();
        verify(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        verify(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        verify(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
        assertSame(masterErrorList, o2CBatchTransferServiceImpl.inputValidations);
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest5() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(new O2CBatchTransferDetails());
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, new HttpHeaders(), null);
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest6() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setLanguage1("en");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest7() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setLanguage2("en");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest8() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setGeographicalDomain("Geo Domain");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest9() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setChannelDomain("Channel Domain");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest10() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setUsercategory("Usercategory");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest11() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setProduct("Product");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest12() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setBatchName("Batch Name");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest13() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setFileAttachment("File Attachment");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest14() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setFileName("foo.txt");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#processRequest(O2CBatchTransferRequestVO, String, OperatorUtilI, String, Connection, Locale, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest15() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchTransferServiceImpl.processRequest(O2CBatchTransferServiceImpl.java:380)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUserData oAuthUserData = new OAuthUserData();
        oAuthUserData.setExtcode("Extcode");
        oAuthUserData.setLoginid("Loginid");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setUserid("Userid");

        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        o2cBatchTransferDetails.setFileType("File Type");
        O2CBatchTransferRequestVO o2CBatchTransferRequestVO = mock(O2CBatchTransferRequestVO.class);
        when(o2CBatchTransferRequestVO.getData()).thenReturn(oAuthUserData);
        when(o2CBatchTransferRequestVO.getO2CBatchTransferDetails()).thenReturn(o2cBatchTransferDetails);
        doNothing().when(o2CBatchTransferRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2CBatchTransferRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2CBatchTransferRequestVO).setO2CBatchTransferDetails(Mockito.<O2CBatchTransferDetails>any());
        o2CBatchTransferRequestVO.setData(data);
        o2CBatchTransferRequestVO.setLoginId("42");
        o2CBatchTransferRequestVO.setMsisdn("Msisdn");
        o2CBatchTransferRequestVO.setO2CBatchTransferDetails(new O2CBatchTransferDetails());
        o2CBatchTransferRequestVO.setPassword("iloveyou");
        o2CBatchTransferRequestVO.setPin("Pin");
        o2CBatchTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CBatchTransferRequestVO.setReqGatewayLoginId("42");
        o2CBatchTransferRequestVO.setReqGatewayPassword("iloveyou");
        o2CBatchTransferRequestVO.setReqGatewayType("Req Gateway Type");
        o2CBatchTransferRequestVO.setServicePort("Service Port");
        o2CBatchTransferRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchTransferServiceImpl.processRequest(o2CBatchTransferRequestVO, "Service Type", calculator, "Request IDStr",
                JUnitConfig.getConnection(), locale, httprequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchTransferServiceImpl#writeFileCSV(List, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testWriteFileCSV() throws IOException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files (file '\directory\foo.txt', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        o2CBatchTransferServiceImpl.writeFileCSV(new ArrayList<>(), "/directory/foo.txt");
    }
}

