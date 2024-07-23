package com.restapi.superadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.XssWrapper;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.util.JUnitConfig;
import com.restapi.superadmin.responseVO.MessageGatewayDetailResponseVO;
import com.restapi.superadminVO.AddMessGatewayVO;
import com.restapi.superadminVO.GatewayListResponseVO;
import com.restapi.superadminVO.MessGatewayVO;
import com.restapi.superadminVO.ReqGatewayVO;
import com.restapi.superadminVO.ResGatewayVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {AddMessageGatewayServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AddMessageGatewayServiceImplTest {
    @Autowired
    private AddMessageGatewayServiceImpl addMessageGatewayServiceImpl;

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#loadGatewaysList(Connection, Locale, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadGatewaysList() throws SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.superadmin.service.AddMessageGatewayServiceImpl.loadGatewaysList(AddMessageGatewayServiceImpl.java:102)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        addMessageGatewayServiceImpl.loadGatewaysList(JUnitConfig.getConnection(), locale, response1);
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#loadGatewaysList(Connection, Locale, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadGatewaysList2() throws SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.superadmin.service.AddMessageGatewayServiceImpl.loadGatewaysList(AddMessageGatewayServiceImpl.java:102)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        addMessageGatewayServiceImpl.loadGatewaysList(JUnitConfig.getConnection(), null, response1);
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#loadGatewaysList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    public void testLoadGatewaysList3() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = new CustomResponseWrapper(new MockHttpServletResponse());
        GatewayListResponseVO actualLoadGatewaysListResult = addMessageGatewayServiceImpl.loadGatewaysList(JUnitConfig.getConnection(), locale,
                response1);
        assertEquals(400, actualLoadGatewaysListResult.getStatus());
        assertEquals("1100128", actualLoadGatewaysListResult.getMessageCode());
        assertNull(actualLoadGatewaysListResult.getMessage());
        assertEquals(2, actualLoadGatewaysListResult.getGatewayTypeList().size());
        assertNull(actualLoadGatewaysListResult.getGatewaySubTypeList());
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        assertEquals(400, response1.getStatus());
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#displayMessageGatewayDetail(Connection, MComConnectionI, Locale, HttpServletRequest, HttpServletResponse, AddMessGatewayVO, MessageGatewayDetailResponseVO, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDisplayMessageGatewayDetail() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.superadmin.service.AddMessageGatewayServiceImpl.displayMessageGatewayDetail(AddMessageGatewayServiceImpl.java:187)
        //   See https://diff.blue/R013 to resolve this issue.

        AddMessageGatewayServiceImpl addMessageGatewayServiceImpl = new AddMessageGatewayServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        CustomResponseWrapper responseSwag = response1;

        ResGatewayVO alternateGatewayVO = new ResGatewayVO();
        alternateGatewayVO.setConfirmPassword("iloveyou");
        alternateGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        alternateGatewayVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setDecryptedPassword("iloveyou");
        alternateGatewayVO.setDestNo("Dest No");
        alternateGatewayVO.setGatewayCode("Gateway Code");
        alternateGatewayVO.setLastModifiedTime(1L);
        alternateGatewayVO.setLoginID("Login ID");
        alternateGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        alternateGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setOldPassword("iloveyou");
        alternateGatewayVO.setPassword("iloveyou");
        alternateGatewayVO.setPath("Path");
        alternateGatewayVO.setPort("Port");
        alternateGatewayVO.setServicePort("Service Port");
        alternateGatewayVO.setStatus("Status");
        alternateGatewayVO.setTimeOut(1);
        alternateGatewayVO.setUpdatePassword("iloveyou");

        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        reqGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");

        ResGatewayVO resGatewayVO = new ResGatewayVO();
        resGatewayVO.setConfirmPassword("iloveyou");
        resGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        resGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setDecryptedPassword("iloveyou");
        resGatewayVO.setDestNo("Dest No");
        resGatewayVO.setGatewayCode("Gateway Code");
        resGatewayVO.setLastModifiedTime(1L);
        resGatewayVO.setLoginID("Login ID");
        resGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        resGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setOldPassword("iloveyou");
        resGatewayVO.setPassword("iloveyou");
        resGatewayVO.setPath("Path");
        resGatewayVO.setPort("Port");
        resGatewayVO.setServicePort("Service Port");
        resGatewayVO.setStatus("Status");
        resGatewayVO.setTimeOut(1);
        resGatewayVO.setUpdatePassword("iloveyou");

        MessGatewayVO messGatewayVO = new MessGatewayVO();
        messGatewayVO.setAccessFrom("jane.doe@example.org");
        messGatewayVO.setAltGatewayVO(alternateGatewayVO);
        messGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        messGatewayVO.setCategoryCode("Category Code");
        messGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        messGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setFlowType("Flow Type");
        messGatewayVO.setGatewayCode("Gateway Code");
        messGatewayVO.setGatewayName("Gateway Name");
        messGatewayVO.setGatewaySubType("Gateway Sub Type");
        messGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        messGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        messGatewayVO.setGatewayType("Gateway Type");
        messGatewayVO.setGatewayTypeDes("Gateway Type Des");
        messGatewayVO.setHandlerClass("Handler Class");
        messGatewayVO.setHost("localhost");
        messGatewayVO.setLastModifiedTime(1L);
        messGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        messGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setNetworkCode("Network Code");
        messGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        messGatewayVO.setProtocol("Protocol");
        messGatewayVO.setReqGatewayVO(reqGatewayVO);
        messGatewayVO.setReqpasswordtype(" reqpasswordtype");
        messGatewayVO.setResGatewayVO(resGatewayVO);
        messGatewayVO.setResponseType("Response Type");
        messGatewayVO.setStatus("Status");
        messGatewayVO.setTimeoutValue(10L);
        messGatewayVO.setUserAuthorizationReqd(true);
        AddMessGatewayVO addMessGatewayVO = mock(AddMessGatewayVO.class);
        when(addMessGatewayVO.getMessGatewayVO()).thenReturn(messGatewayVO);
        MessageGatewayDetailResponseVO response = mock(MessageGatewayDetailResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        addMessageGatewayServiceImpl.displayMessageGatewayDetail(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, request, responseSwag,
                addMessGatewayVO, response, mock(ChannelUserVO.class));
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#displayMessageGatewayDetail(Connection, MComConnectionI, Locale, HttpServletRequest, HttpServletResponse, AddMessGatewayVO, MessageGatewayDetailResponseVO, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDisplayMessageGatewayDetail2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.superadmin.service.AddMessageGatewayServiceImpl.displayMessageGatewayDetail(AddMessageGatewayServiceImpl.java:187)
        //   See https://diff.blue/R013 to resolve this issue.

        AddMessageGatewayServiceImpl addMessageGatewayServiceImpl = new AddMessageGatewayServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        CustomResponseWrapper responseSwag = response1;

        ResGatewayVO alternateGatewayVO = new ResGatewayVO();
        alternateGatewayVO.setConfirmPassword("iloveyou");
        alternateGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        alternateGatewayVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setDecryptedPassword("iloveyou");
        alternateGatewayVO.setDestNo("Dest No");
        alternateGatewayVO.setGatewayCode("Gateway Code");
        alternateGatewayVO.setLastModifiedTime(1L);
        alternateGatewayVO.setLoginID("Login ID");
        alternateGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        alternateGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setOldPassword("iloveyou");
        alternateGatewayVO.setPassword("iloveyou");
        alternateGatewayVO.setPath("Path");
        alternateGatewayVO.setPort("Port");
        alternateGatewayVO.setServicePort("Service Port");
        alternateGatewayVO.setStatus("Status");
        alternateGatewayVO.setTimeOut(1);
        alternateGatewayVO.setUpdatePassword("iloveyou");

        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        reqGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");

        ResGatewayVO resGatewayVO = new ResGatewayVO();
        resGatewayVO.setConfirmPassword("iloveyou");
        resGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        resGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setDecryptedPassword("iloveyou");
        resGatewayVO.setDestNo("Dest No");
        resGatewayVO.setGatewayCode("Gateway Code");
        resGatewayVO.setLastModifiedTime(1L);
        resGatewayVO.setLoginID("Login ID");
        resGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        resGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setOldPassword("iloveyou");
        resGatewayVO.setPassword("iloveyou");
        resGatewayVO.setPath("Path");
        resGatewayVO.setPort("Port");
        resGatewayVO.setServicePort("Service Port");
        resGatewayVO.setStatus("Status");
        resGatewayVO.setTimeOut(1);
        resGatewayVO.setUpdatePassword("iloveyou");

        MessGatewayVO messGatewayVO = new MessGatewayVO();
        messGatewayVO.setAccessFrom("jane.doe@example.org");
        messGatewayVO.setAltGatewayVO(alternateGatewayVO);
        messGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        messGatewayVO.setCategoryCode("Category Code");
        messGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        messGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setFlowType("Flow Type");
        messGatewayVO.setGatewayCode("Gateway Code");
        messGatewayVO.setGatewayName("Gateway Name");
        messGatewayVO.setGatewaySubType("Gateway Sub Type");
        messGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        messGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        messGatewayVO.setGatewayType("Gateway Type");
        messGatewayVO.setGatewayTypeDes("Gateway Type Des");
        messGatewayVO.setHandlerClass("Handler Class");
        messGatewayVO.setHost("localhost");
        messGatewayVO.setLastModifiedTime(1L);
        messGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        messGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setNetworkCode("Network Code");
        messGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        messGatewayVO.setProtocol("Protocol");
        messGatewayVO.setReqGatewayVO(reqGatewayVO);
        messGatewayVO.setReqpasswordtype(" reqpasswordtype");
        messGatewayVO.setResGatewayVO(resGatewayVO);
        messGatewayVO.setResponseType("Response Type");
        messGatewayVO.setStatus("Status");
        messGatewayVO.setTimeoutValue(10L);
        messGatewayVO.setUserAuthorizationReqd(true);
        AddMessGatewayVO addMessGatewayVO = mock(AddMessGatewayVO.class);
        when(addMessGatewayVO.getMessGatewayVO()).thenReturn(messGatewayVO);
        MessageGatewayDetailResponseVO response = mock(MessageGatewayDetailResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        addMessageGatewayServiceImpl.displayMessageGatewayDetail(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), null, request, responseSwag,
                addMessGatewayVO, response, mock(ChannelUserVO.class));
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#displayMessageGatewayDetail(Connection, MComConnectionI, Locale, HttpServletRequest, HttpServletResponse, AddMessGatewayVO, MessageGatewayDetailResponseVO, ChannelUserVO)}
     */
    @Test
    public void testDisplayMessageGatewayDetail3() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        AddMessageGatewayServiceImpl addMessageGatewayServiceImpl = new AddMessageGatewayServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());

        ResGatewayVO alternateGatewayVO = new ResGatewayVO();
        alternateGatewayVO.setConfirmPassword("iloveyou");
        alternateGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        alternateGatewayVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setDecryptedPassword("iloveyou");
        alternateGatewayVO.setDestNo("Dest No");
        alternateGatewayVO.setGatewayCode("Gateway Code");
        alternateGatewayVO.setLastModifiedTime(1L);
        alternateGatewayVO.setLoginID("Login ID");
        alternateGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        alternateGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setOldPassword("iloveyou");
        alternateGatewayVO.setPassword("iloveyou");
        alternateGatewayVO.setPath("Path");
        alternateGatewayVO.setPort("Port");
        alternateGatewayVO.setServicePort("Service Port");
        alternateGatewayVO.setStatus("Status");
        alternateGatewayVO.setTimeOut(1);
        alternateGatewayVO.setUpdatePassword("iloveyou");

        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        reqGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");

        ResGatewayVO resGatewayVO = new ResGatewayVO();
        resGatewayVO.setConfirmPassword("iloveyou");
        resGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        resGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setDecryptedPassword("iloveyou");
        resGatewayVO.setDestNo("Dest No");
        resGatewayVO.setGatewayCode("Gateway Code");
        resGatewayVO.setLastModifiedTime(1L);
        resGatewayVO.setLoginID("Login ID");
        resGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        resGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setOldPassword("iloveyou");
        resGatewayVO.setPassword("iloveyou");
        resGatewayVO.setPath("Path");
        resGatewayVO.setPort("Port");
        resGatewayVO.setServicePort("Service Port");
        resGatewayVO.setStatus("Status");
        resGatewayVO.setTimeOut(1);
        resGatewayVO.setUpdatePassword("iloveyou");

        MessGatewayVO messGatewayVO = new MessGatewayVO();
        messGatewayVO.setAccessFrom("jane.doe@example.org");
        messGatewayVO.setAltGatewayVO(alternateGatewayVO);
        messGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        messGatewayVO.setCategoryCode("Category Code");
        messGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        messGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setFlowType("Flow Type");
        messGatewayVO.setGatewayCode("Gateway Code");
        messGatewayVO.setGatewayName("Gateway Name");
        messGatewayVO.setGatewaySubType("Gateway Sub Type");
        messGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        messGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        messGatewayVO.setGatewayType("Gateway Type");
        messGatewayVO.setGatewayTypeDes("Gateway Type Des");
        messGatewayVO.setHandlerClass("Handler Class");
        messGatewayVO.setHost("localhost");
        messGatewayVO.setLastModifiedTime(1L);
        messGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        messGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setNetworkCode("Network Code");
        messGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        messGatewayVO.setProtocol("Protocol");
        messGatewayVO.setReqGatewayVO(reqGatewayVO);
        messGatewayVO.setReqpasswordtype(" reqpasswordtype");
        messGatewayVO.setResGatewayVO(resGatewayVO);
        messGatewayVO.setResponseType("Response Type");
        messGatewayVO.setStatus("Status");
        messGatewayVO.setTimeoutValue(10L);
        messGatewayVO.setUserAuthorizationReqd(true);
        AddMessGatewayVO addMessGatewayVO = mock(AddMessGatewayVO.class);
        when(addMessGatewayVO.getMessGatewayVO()).thenReturn(messGatewayVO);
        MessageGatewayDetailResponseVO response = mock(MessageGatewayDetailResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        assertSame(response, addMessageGatewayServiceImpl.displayMessageGatewayDetail(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, request,
                responseSwag, addMessGatewayVO, response, mock(ChannelUserVO.class)));
      //  verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
        verify(addMessGatewayVO).getMessGatewayVO();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#addMessGateway(Connection, MComConnectionI, Locale, HttpServletRequest, HttpServletResponse, AddMessGatewayVO, BaseResponse, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddMessGateway() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.superadmin.service.AddMessageGatewayServiceImpl.addMessGateway(AddMessageGatewayServiceImpl.java:371)
        //   See https://diff.blue/R013 to resolve this issue.

        AddMessageGatewayServiceImpl addMessageGatewayServiceImpl = new AddMessageGatewayServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        CustomResponseWrapper responseSwag = response1;

        ResGatewayVO alternateGatewayVO = new ResGatewayVO();
        alternateGatewayVO.setConfirmPassword("iloveyou");
        alternateGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        alternateGatewayVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setDecryptedPassword("iloveyou");
        alternateGatewayVO.setDestNo("Dest No");
        alternateGatewayVO.setGatewayCode("Gateway Code");
        alternateGatewayVO.setLastModifiedTime(1L);
        alternateGatewayVO.setLoginID("Login ID");
        alternateGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        alternateGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setOldPassword("iloveyou");
        alternateGatewayVO.setPassword("iloveyou");
        alternateGatewayVO.setPath("Path");
        alternateGatewayVO.setPort("Port");
        alternateGatewayVO.setServicePort("Service Port");
        alternateGatewayVO.setStatus("Status");
        alternateGatewayVO.setTimeOut(1);
        alternateGatewayVO.setUpdatePassword("iloveyou");

        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        reqGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");

        ResGatewayVO resGatewayVO = new ResGatewayVO();
        resGatewayVO.setConfirmPassword("iloveyou");
        resGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        resGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setDecryptedPassword("iloveyou");
        resGatewayVO.setDestNo("Dest No");
        resGatewayVO.setGatewayCode("Gateway Code");
        resGatewayVO.setLastModifiedTime(1L);
        resGatewayVO.setLoginID("Login ID");
        resGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        resGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setOldPassword("iloveyou");
        resGatewayVO.setPassword("iloveyou");
        resGatewayVO.setPath("Path");
        resGatewayVO.setPort("Port");
        resGatewayVO.setServicePort("Service Port");
        resGatewayVO.setStatus("Status");
        resGatewayVO.setTimeOut(1);
        resGatewayVO.setUpdatePassword("iloveyou");

        MessGatewayVO messGatewayVO = new MessGatewayVO();
        messGatewayVO.setAccessFrom("jane.doe@example.org");
        messGatewayVO.setAltGatewayVO(alternateGatewayVO);
        messGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        messGatewayVO.setCategoryCode("Category Code");
        messGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        messGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setFlowType("Flow Type");
        messGatewayVO.setGatewayCode("Gateway Code");
        messGatewayVO.setGatewayName("Gateway Name");
        messGatewayVO.setGatewaySubType("Gateway Sub Type");
        messGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        messGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        messGatewayVO.setGatewayType("Gateway Type");
        messGatewayVO.setGatewayTypeDes("Gateway Type Des");
        messGatewayVO.setHandlerClass("Handler Class");
        messGatewayVO.setHost("localhost");
        messGatewayVO.setLastModifiedTime(1L);
        messGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        messGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setNetworkCode("Network Code");
        messGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        messGatewayVO.setProtocol("Protocol");
        messGatewayVO.setReqGatewayVO(reqGatewayVO);
        messGatewayVO.setReqpasswordtype(" reqpasswordtype");
        messGatewayVO.setResGatewayVO(resGatewayVO);
        messGatewayVO.setResponseType("Response Type");
        messGatewayVO.setStatus("Status");
        messGatewayVO.setTimeoutValue(10L);
        messGatewayVO.setUserAuthorizationReqd(true);
        AddMessGatewayVO addMessGatewayVO = mock(AddMessGatewayVO.class);
        when(addMessGatewayVO.getMessGatewayVO()).thenReturn(messGatewayVO);
        BaseResponse response = mock(BaseResponse.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        addMessageGatewayServiceImpl.addMessGateway(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, request, responseSwag, addMessGatewayVO,
                response, mock(ChannelUserVO.class));
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#addMessGateway(Connection, MComConnectionI, Locale, HttpServletRequest, HttpServletResponse, AddMessGatewayVO, BaseResponse, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddMessGateway2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.superadmin.service.AddMessageGatewayServiceImpl.addMessGateway(AddMessageGatewayServiceImpl.java:371)
        //   See https://diff.blue/R013 to resolve this issue.

        AddMessageGatewayServiceImpl addMessageGatewayServiceImpl = new AddMessageGatewayServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        CustomResponseWrapper responseSwag = response1;

        ResGatewayVO alternateGatewayVO = new ResGatewayVO();
        alternateGatewayVO.setConfirmPassword("iloveyou");
        alternateGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        alternateGatewayVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setDecryptedPassword("iloveyou");
        alternateGatewayVO.setDestNo("Dest No");
        alternateGatewayVO.setGatewayCode("Gateway Code");
        alternateGatewayVO.setLastModifiedTime(1L);
        alternateGatewayVO.setLoginID("Login ID");
        alternateGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        alternateGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setOldPassword("iloveyou");
        alternateGatewayVO.setPassword("iloveyou");
        alternateGatewayVO.setPath("Path");
        alternateGatewayVO.setPort("Port");
        alternateGatewayVO.setServicePort("Service Port");
        alternateGatewayVO.setStatus("Status");
        alternateGatewayVO.setTimeOut(1);
        alternateGatewayVO.setUpdatePassword("iloveyou");

        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        reqGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");

        ResGatewayVO resGatewayVO = new ResGatewayVO();
        resGatewayVO.setConfirmPassword("iloveyou");
        resGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        resGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setDecryptedPassword("iloveyou");
        resGatewayVO.setDestNo("Dest No");
        resGatewayVO.setGatewayCode("Gateway Code");
        resGatewayVO.setLastModifiedTime(1L);
        resGatewayVO.setLoginID("Login ID");
        resGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        resGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setOldPassword("iloveyou");
        resGatewayVO.setPassword("iloveyou");
        resGatewayVO.setPath("Path");
        resGatewayVO.setPort("Port");
        resGatewayVO.setServicePort("Service Port");
        resGatewayVO.setStatus("Status");
        resGatewayVO.setTimeOut(1);
        resGatewayVO.setUpdatePassword("iloveyou");

        MessGatewayVO messGatewayVO = new MessGatewayVO();
        messGatewayVO.setAccessFrom("jane.doe@example.org");
        messGatewayVO.setAltGatewayVO(alternateGatewayVO);
        messGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        messGatewayVO.setCategoryCode("Category Code");
        messGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        messGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setFlowType("Flow Type");
        messGatewayVO.setGatewayCode("Gateway Code");
        messGatewayVO.setGatewayName("Gateway Name");
        messGatewayVO.setGatewaySubType("Gateway Sub Type");
        messGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        messGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        messGatewayVO.setGatewayType("Gateway Type");
        messGatewayVO.setGatewayTypeDes("Gateway Type Des");
        messGatewayVO.setHandlerClass("Handler Class");
        messGatewayVO.setHost("localhost");
        messGatewayVO.setLastModifiedTime(1L);
        messGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        messGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setNetworkCode("Network Code");
        messGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        messGatewayVO.setProtocol("Protocol");
        messGatewayVO.setReqGatewayVO(reqGatewayVO);
        messGatewayVO.setReqpasswordtype(" reqpasswordtype");
        messGatewayVO.setResGatewayVO(resGatewayVO);
        messGatewayVO.setResponseType("Response Type");
        messGatewayVO.setStatus("Status");
        messGatewayVO.setTimeoutValue(10L);
        messGatewayVO.setUserAuthorizationReqd(true);
        AddMessGatewayVO addMessGatewayVO = mock(AddMessGatewayVO.class);
        when(addMessGatewayVO.getMessGatewayVO()).thenReturn(messGatewayVO);
        BaseResponse response = mock(BaseResponse.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        addMessageGatewayServiceImpl.addMessGateway(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), null, request, responseSwag, addMessGatewayVO, response,
                mock(ChannelUserVO.class));
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#addMessGateway(Connection, MComConnectionI, Locale, HttpServletRequest, HttpServletResponse, AddMessGatewayVO, BaseResponse, ChannelUserVO)}
     */
    @Test
    public void testAddMessGateway3() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        AddMessageGatewayServiceImpl addMessageGatewayServiceImpl = new AddMessageGatewayServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());

        ResGatewayVO alternateGatewayVO = new ResGatewayVO();
        alternateGatewayVO.setConfirmPassword("iloveyou");
        alternateGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        alternateGatewayVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setDecryptedPassword("iloveyou");
        alternateGatewayVO.setDestNo("Dest No");
        alternateGatewayVO.setGatewayCode("Gateway Code");
        alternateGatewayVO.setLastModifiedTime(1L);
        alternateGatewayVO.setLoginID("Login ID");
        alternateGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        alternateGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setOldPassword("iloveyou");
        alternateGatewayVO.setPassword("iloveyou");
        alternateGatewayVO.setPath("Path");
        alternateGatewayVO.setPort("Port");
        alternateGatewayVO.setServicePort("Service Port");
        alternateGatewayVO.setStatus("Status");
        alternateGatewayVO.setTimeOut(1);
        alternateGatewayVO.setUpdatePassword("iloveyou");

        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        reqGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");

        ResGatewayVO resGatewayVO = new ResGatewayVO();
        resGatewayVO.setConfirmPassword("iloveyou");
        resGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        resGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setDecryptedPassword("iloveyou");
        resGatewayVO.setDestNo("Dest No");
        resGatewayVO.setGatewayCode("Gateway Code");
        resGatewayVO.setLastModifiedTime(1L);
        resGatewayVO.setLoginID("Login ID");
        resGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        resGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setOldPassword("iloveyou");
        resGatewayVO.setPassword("iloveyou");
        resGatewayVO.setPath("Path");
        resGatewayVO.setPort("Port");
        resGatewayVO.setServicePort("Service Port");
        resGatewayVO.setStatus("Status");
        resGatewayVO.setTimeOut(1);
        resGatewayVO.setUpdatePassword("iloveyou");

        MessGatewayVO messGatewayVO = new MessGatewayVO();
        messGatewayVO.setAccessFrom("jane.doe@example.org");
        messGatewayVO.setAltGatewayVO(alternateGatewayVO);
        messGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        messGatewayVO.setCategoryCode("Category Code");
        messGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        messGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setFlowType("Flow Type");
        messGatewayVO.setGatewayCode("Gateway Code");
        messGatewayVO.setGatewayName("Gateway Name");
        messGatewayVO.setGatewaySubType("Gateway Sub Type");
        messGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        messGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        messGatewayVO.setGatewayType("Gateway Type");
        messGatewayVO.setGatewayTypeDes("Gateway Type Des");
        messGatewayVO.setHandlerClass("Handler Class");
        messGatewayVO.setHost("localhost");
        messGatewayVO.setLastModifiedTime(1L);
        messGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        messGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setNetworkCode("Network Code");
        messGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        messGatewayVO.setProtocol("Protocol");
        messGatewayVO.setReqGatewayVO(reqGatewayVO);
        messGatewayVO.setReqpasswordtype(" reqpasswordtype");
        messGatewayVO.setResGatewayVO(resGatewayVO);
        messGatewayVO.setResponseType("Response Type");
        messGatewayVO.setStatus("Status");
        messGatewayVO.setTimeoutValue(10L);
        messGatewayVO.setUserAuthorizationReqd(true);
        AddMessGatewayVO addMessGatewayVO = mock(AddMessGatewayVO.class);
        when(addMessGatewayVO.getMessGatewayVO()).thenReturn(messGatewayVO);
        BaseResponse response = mock(BaseResponse.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        assertSame(response, addMessageGatewayServiceImpl.addMessGateway(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, request, responseSwag,
                addMessGatewayVO, response, mock(ChannelUserVO.class)));
        //verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
        verify(addMessGatewayVO).getMessGatewayVO();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#updateMessGateway(Connection, MComConnectionI, Locale, HttpServletRequest, HttpServletResponse, AddMessGatewayVO, BaseResponse, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUpdateMessGateway() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        HttpServletRequest request = null;
        HttpServletResponse responseSwag = null;
        AddMessGatewayVO addMessGatewayVO = null;
        BaseResponse response = null;
        ChannelUserVO userVO = null;

        // Act
        BaseResponse actualUpdateMessGatewayResult = this.addMessageGatewayServiceImpl.updateMessGateway(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(),
                locale, request, responseSwag, addMessGatewayVO, response, userVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#loadClassHandlerList(Connection, Locale, HttpServletResponse, String)}
     */
    @Test
    public void testLoadClassHandlerList() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        GatewayListResponseVO actualLoadClassHandlerListResult = addMessageGatewayServiceImpl.loadClassHandlerList(JUnitConfig.getConnection(),
                locale, response1, "Gateway Code");
        assertEquals("String", actualLoadClassHandlerListResult.getClassHandler());
        assertEquals(200, actualLoadClassHandlerListResult.getStatus());
        assertEquals("9020", actualLoadClassHandlerListResult.getMessageCode());
        assertNull(actualLoadClassHandlerListResult.getMessage());
        assertNull(actualLoadClassHandlerListResult.getGatewayTypeList());
        assertNull(actualLoadClassHandlerListResult.getGatewaySubTypeList());
       // verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link AddMessageGatewayServiceImpl#loadClassHandlerList(Connection, Locale, HttpServletResponse, String)}
     */
    @Test
    public void testLoadClassHandlerList2() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        GatewayListResponseVO actualLoadClassHandlerListResult = addMessageGatewayServiceImpl.loadClassHandlerList(JUnitConfig.getConnection(),
                null, response1, "Gateway Code");
        assertEquals("String", actualLoadClassHandlerListResult.getClassHandler());
        assertEquals(200, actualLoadClassHandlerListResult.getStatus());
        assertEquals("9020", actualLoadClassHandlerListResult.getMessageCode());
        assertNull(actualLoadClassHandlerListResult.getMessage());
        assertNull(actualLoadClassHandlerListResult.getGatewayTypeList());
        assertNull(actualLoadClassHandlerListResult.getGatewaySubTypeList());
      //  verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }
}

