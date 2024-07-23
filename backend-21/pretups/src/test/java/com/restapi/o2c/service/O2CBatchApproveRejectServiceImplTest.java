package com.restapi.o2c.service;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.XssWrapper;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.iat.util.IATCommonUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {O2CBatchApproveRejectServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class O2CBatchApproveRejectServiceImplTest {
    @Autowired
    private O2CBatchApproveRejectServiceImpl o2CBatchApproveRejectServiceImpl;

    /**
     * Method under test: {@link O2CBatchApproveRejectServiceImpl#processO2CApproveOrReject(O2CBulkApprovalOrRejectRequestVO, String, OperatorUtilI, Locale, Connection, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CApproveOrReject() throws BTSLBaseException, SQLException {

        JUnitConfig.init();
        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        O2CBulkApprovalOrRejectRequestVO o2cBulkApprovalOrRejectRequestVO = new O2CBulkApprovalOrRejectRequestVO();

        o2cBulkApprovalOrRejectRequestVO.setData(data);
        o2cBulkApprovalOrRejectRequestVO.setLoginId("42");
        o2cBulkApprovalOrRejectRequestVO.setMsisdn("9999999999");
        O2CBulkApprovalOrRejectRequestData data2  = new O2CBulkApprovalOrRejectRequestData() ;

        data2.setRequestType("approval1");
        data2.setBatchId("String");
        data2.setProduct("String");
        data2.setBatchName("String");
        data2.setRequest("String");
        data2.setPin("1357");
        data2.setRemarks("String");
        data2.setService("String");

        o2cBulkApprovalOrRejectRequestVO.setO2CBulkApprovalOrRejectRequestData(data2);
        o2cBulkApprovalOrRejectRequestVO.setPassword("Com@1357");
        o2cBulkApprovalOrRejectRequestVO.setPin("1357");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayCode("Req Gateway Code");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayLoginId("42");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayPassword("Com@1357");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayType("Req Gateway Type");
        o2cBulkApprovalOrRejectRequestVO.setServicePort("Service Port");
        o2cBulkApprovalOrRejectRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Locale locale = Locale.getDefault();
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
      // //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response = mock(CustomResponseWrapper.class) ;
        doNothing().when(response).setStatus(Mockito.anyInt());

        o2CBatchApproveRejectServiceImpl.processO2CApproveOrReject(o2cBulkApprovalOrRejectRequestVO, "9999999999", calculator,
                locale, JUnitConfig.getConnection(), "Service Type", "Request IDStr", httprequest, headers, response);


        o2cBulkApprovalOrRejectRequestVO.getO2CBulkApprovalOrRejectRequestData().setRequestType("approval2");
        o2CBatchApproveRejectServiceImpl.processO2CApproveOrReject(o2cBulkApprovalOrRejectRequestVO, "9999999999", calculator,
                locale, JUnitConfig.getConnection(), "Service Type", "Request IDStr", httprequest, headers, response);


        o2cBulkApprovalOrRejectRequestVO.getO2CBulkApprovalOrRejectRequestData().setRequest("reject");
        o2CBatchApproveRejectServiceImpl.processO2CApproveOrReject(o2cBulkApprovalOrRejectRequestVO, "9999999999", calculator,
                locale, JUnitConfig.getConnection(), "Service Type", "Request IDStr", httprequest, headers, response);


    }

    /**
     * Method under test: {@link O2CBatchApproveRejectServiceImpl#processO2CApproveOrReject(O2CBulkApprovalOrRejectRequestVO, String, OperatorUtilI, Locale, Connection, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CApproveOrReject2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CBatchApproveRejectServiceImpl.processO2CApproveOrReject(O2CBatchApproveRejectServiceImpl.java:256)
        //   See https://diff.blue/R013 to resolve this issue.

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        O2CBulkApprovalOrRejectRequestVO o2cBulkApprovalOrRejectRequestVO = mock(O2CBulkApprovalOrRejectRequestVO.class);
        when(o2cBulkApprovalOrRejectRequestVO.getO2CBulkApprovalOrRejectRequestData()).thenReturn(null);
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setPin(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(o2cBulkApprovalOrRejectRequestVO)
                .setO2CBulkApprovalOrRejectRequestData(Mockito.<O2CBulkApprovalOrRejectRequestData>any());
        o2cBulkApprovalOrRejectRequestVO.setData(data);
        o2cBulkApprovalOrRejectRequestVO.setLoginId("42");
        o2cBulkApprovalOrRejectRequestVO.setMsisdn("Msisdn");
        o2cBulkApprovalOrRejectRequestVO.setO2CBulkApprovalOrRejectRequestData(new O2CBulkApprovalOrRejectRequestData());
        o2cBulkApprovalOrRejectRequestVO.setPassword("iloveyou");
        o2cBulkApprovalOrRejectRequestVO.setPin("Pin");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayCode("Req Gateway Code");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayLoginId("42");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayPassword("iloveyou");
        o2cBulkApprovalOrRejectRequestVO.setReqGatewayType("Req Gateway Type");
        o2cBulkApprovalOrRejectRequestVO.setServicePort("Service Port");
        o2cBulkApprovalOrRejectRequestVO.setSourceType("Source Type");
        IATCommonUtil calculator = new IATCommonUtil();
        Locale locale = Locale.getDefault();
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
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2CBatchApproveRejectServiceImpl.processO2CApproveOrReject(o2cBulkApprovalOrRejectRequestVO, "Msisdn", calculator,
                locale, JUnitConfig.getConnection(), "Service Type", "Request IDStr", httprequest, headers,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchApproveRejectServiceImpl#processBulkCommApproveOrReject(CommisionBulkApprovalOrRejectRequestVO, String, Locale, Connection, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessBulkCommApproveOrReject() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Diffblue AI was unable to find a test

        JUnitConfig.init();
        O2CBatchApproveRejectServiceImpl o2cBatchApproveRejectServiceImpl = new O2CBatchApproveRejectServiceImpl();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        CommisionBulkApprovalOrRejectRequestVO commisionBulkApprovalOrRejectRequestVO = new CommisionBulkApprovalOrRejectRequestVO();

        CommisionBulkApprovalOrRejectRequestData data2 =  new CommisionBulkApprovalOrRejectRequestData();


        data2.setRequestType("approval1");
        data2.setBatchId("String");

        data2.setBatchName("String");
        data2.setRequest("approve");
        data2.setPin("1357");
        data2.setRemarks("String");




        commisionBulkApprovalOrRejectRequestVO
                .setCommisionBulkApprovalOrRejectRequestData(data2);


        commisionBulkApprovalOrRejectRequestVO.setData(data);
        commisionBulkApprovalOrRejectRequestVO.setLoginId("42");
        commisionBulkApprovalOrRejectRequestVO.setMsisdn("Msisdn");
        commisionBulkApprovalOrRejectRequestVO.setPassword("iloveyou");
        commisionBulkApprovalOrRejectRequestVO.setPin("Pin");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayCode("Req Gateway Code");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayLoginId("42");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayPassword("iloveyou");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayType("Req Gateway Type");
        commisionBulkApprovalOrRejectRequestVO.setServicePort("Service Port");
        commisionBulkApprovalOrRejectRequestVO.setSourceType("Source Type");
        Locale locale = Locale.getDefault();
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
       ////Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();

        CustomResponseWrapper response = mock(CustomResponseWrapper.class) ;



        o2cBatchApproveRejectServiceImpl.processBulkCommApproveOrReject(commisionBulkApprovalOrRejectRequestVO, "9999999999",
                locale, JUnitConfig.getConnection(), httprequest, headers, response);


        commisionBulkApprovalOrRejectRequestVO
                .getCommisionBulkApprovalOrRejectRequestData().setRequestType("approval2");

        o2cBatchApproveRejectServiceImpl.processBulkCommApproveOrReject(commisionBulkApprovalOrRejectRequestVO, "9999999999",
                locale, JUnitConfig.getConnection(), httprequest, headers, response);





        commisionBulkApprovalOrRejectRequestVO
                .getCommisionBulkApprovalOrRejectRequestData().setRequest("reject");

        o2cBatchApproveRejectServiceImpl.processBulkCommApproveOrReject(commisionBulkApprovalOrRejectRequestVO, "9999999999",
                locale, JUnitConfig.getConnection(), httprequest, headers, response);





    }

    /**
     * Method under test: {@link O2CBatchApproveRejectServiceImpl#processBulkCommApproveOrReject(CommisionBulkApprovalOrRejectRequestVO, String, Locale, Connection, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessBulkCommApproveOrReject2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Diffblue AI was unable to find a test

        O2CBatchApproveRejectServiceImpl o2cBatchApproveRejectServiceImpl = new O2CBatchApproveRejectServiceImpl();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        CommisionBulkApprovalOrRejectRequestVO commisionBulkApprovalOrRejectRequestVO = mock(
                CommisionBulkApprovalOrRejectRequestVO.class);

        CommisionBulkApprovalOrRejectRequestData data2 =  new CommisionBulkApprovalOrRejectRequestData();


        data2.setRequestType("approval1");
        data2.setBatchId("String");

        data2.setBatchName("String");
        data2.setRequest("String");
        data2.setPin("1357");
        data2.setRemarks("String");



        when(commisionBulkApprovalOrRejectRequestVO.getCommisionBulkApprovalOrRejectRequestData()).thenReturn(data2);


        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setLoginId(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setPassword(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setPin(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setServicePort(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO).setSourceType(Mockito.<String>any());
        doNothing().when(commisionBulkApprovalOrRejectRequestVO)
                .setCommisionBulkApprovalOrRejectRequestData(Mockito.<CommisionBulkApprovalOrRejectRequestData>any());
        commisionBulkApprovalOrRejectRequestVO
                .setCommisionBulkApprovalOrRejectRequestData(new CommisionBulkApprovalOrRejectRequestData());
        commisionBulkApprovalOrRejectRequestVO.setData(data);
        commisionBulkApprovalOrRejectRequestVO.setLoginId("42");
        commisionBulkApprovalOrRejectRequestVO.setMsisdn("Msisdn");
        commisionBulkApprovalOrRejectRequestVO.setPassword("iloveyou");
        commisionBulkApprovalOrRejectRequestVO.setPin("Pin");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayCode("Req Gateway Code");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayLoginId("42");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayPassword("iloveyou");
        commisionBulkApprovalOrRejectRequestVO.setReqGatewayType("Req Gateway Type");
        commisionBulkApprovalOrRejectRequestVO.setServicePort("Service Port");
        commisionBulkApprovalOrRejectRequestVO.setSourceType("Source Type");
        Locale locale = Locale.getDefault();
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
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2cBatchApproveRejectServiceImpl.processBulkCommApproveOrReject(commisionBulkApprovalOrRejectRequestVO, "Msisdn",
                locale, JUnitConfig.getConnection(), httprequest, headers, new CustomResponseWrapper(new Response()));
    }
}

