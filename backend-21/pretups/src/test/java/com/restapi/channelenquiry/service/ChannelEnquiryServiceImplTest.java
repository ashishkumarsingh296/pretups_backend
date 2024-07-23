/*
package com.restapi.channelenquiry.service;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.user.service.HeaderColumn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ChannelEnquiryServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelEnquiryServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
  //  private ChannelEnquiryServiceImpl channelEnquiryServiceImpl;

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#processChannelEnquiryC2c(C2cAndO2cEnquiryRequestVO, C2cAndO2cEnquiryResponseVO, HttpServletResponse, OAuthUser, Locale, String, String)}
     *//*

    @Test
    public void testProcessChannelEnquiryC2c() throws Exception {
        C2cAndO2cEnquiryRequestVO requestVO = new C2cAndO2cEnquiryRequestVO();
        requestVO.setCategory("Category");
        requestVO.setDistributionType("Distribution Type");
        requestVO.setDomain("Domain");
        requestVO.setFromDate("2020-03-01");
        requestVO.setGeography("Geography");
        requestVO.setOrderStatus("Order Status");
        requestVO.setReceiverMsisdn("Receiver Msisdn");
        requestVO.setSenderMsisdn("Sender Msisdn");
        requestVO.setStaffLoginID("Staff Login ID");
        requestVO.setToDate("2020-03-01");
        requestVO.setTransactionID("Transaction ID");
        requestVO.setTransferCategory("Transfer Category");
        requestVO.setTransferSubType("Transfer Sub Type");
        requestVO.setUserID("User ID");
        requestVO.setUserType("User Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        C2cAndO2cEnquiryResponseVO response = new C2cAndO2cEnquiryResponseVO();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        response.setTransferList(new ArrayList<>());
        response.setTransferListSize(3);
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        OAuthUser oAuthUserData = new OAuthUser();
        oAuthUserData.setData(data);
        oAuthUserData.setLoginId("42");
        oAuthUserData.setMsisdn("Msisdn");
        oAuthUserData.setPassword("iloveyou");
        oAuthUserData.setPin("Pin");
        oAuthUserData.setReqGatewayCode("Req Gateway Code");
        oAuthUserData.setReqGatewayLoginId("42");
        oAuthUserData.setReqGatewayPassword("iloveyou");
        oAuthUserData.setReqGatewayType("Req Gateway Type");
        oAuthUserData.setServicePort("Service Port");
        oAuthUserData.setSourceType("Source Type");
        thrown.expect(BTSLBaseException.class);
       // channelEnquiryServiceImpl.processChannelEnquiryC2c(requestVO, response, responseSwag, oAuthUserData,
        //        Locale.getDefault(), "Enquiry Type", "Search By");
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadClosingBalanceData() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("User Type");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
        verify(sessionUser).getCategoryCode();
        verify(sessionUser, atLeast(1)).getDomainID();
        verify(sessionUser, atLeast(1)).getDomainName();
        verify(sessionUser).getUserID();
        verify(sessionUser, atLeast(1)).getUserType();
        verify(sessionUser).getDomainList();
        verify(sessionUser).getGeographicalAreaList();
        verify(requestVO, atLeast(1)).getCatCode();
        verify(requestVO, atLeast(1)).getDomainCode();
        verify(requestVO, atLeast(1)).getUserName();
        verify(requestVO, atLeast(1)).getZoneCode();
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadClosingBalanceData2() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.sql.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:108)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("User Type");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadClosingBalanceData3() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("User Type");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
        verify(sessionUser).getCategoryCode();
        verify(sessionUser, atLeast(1)).getDomainID();
        verify(sessionUser, atLeast(1)).getDomainName();
        verify(sessionUser).getUserID();
        verify(sessionUser, atLeast(1)).getUserType();
        verify(sessionUser).getDomainList();
        verify(sessionUser).getGeographicalAreaList();
        verify(requestVO, atLeast(1)).getCatCode();
        verify(requestVO, atLeast(1)).getDomainCode();
        verify(requestVO, atLeast(1)).getUserName();
        verify(requestVO, atLeast(1)).getZoneCode();
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadClosingBalanceData4() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("User Type");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getFromDate()).thenReturn("2020-03-01");
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
        verify(sessionUser).getCategoryCode();
        verify(sessionUser, atLeast(1)).getDomainID();
        verify(sessionUser, atLeast(1)).getDomainName();
        verify(sessionUser).getUserID();
        verify(sessionUser, atLeast(1)).getUserType();
        verify(sessionUser).getDomainList();
        verify(sessionUser).getGeographicalAreaList();
        verify(requestVO, atLeast(1)).getCatCode();
        verify(requestVO, atLeast(1)).getDomainCode();
        verify(requestVO).getFromDate();
        verify(requestVO, atLeast(1)).getUserName();
        verify(requestVO, atLeast(1)).getZoneCode();
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadClosingBalanceData5() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("OPERATOR");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getFromDate()).thenReturn("2020-03-01");
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
        verify (com.btsl.util.JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(sessionUser).getCategoryCode();
        verify(sessionUser, atLeast(1)).getDomainID();
        verify(sessionUser, atLeast(1)).getDomainName();
        verify(sessionUser).getUserID();
        verify(sessionUser).getUserType();
        verify(sessionUser).getDomainList();
        verify(sessionUser).getGeographicalAreaList();
        verify(requestVO, atLeast(1)).getCatCode();
        verify(requestVO, atLeast(1)).getDomainCode();
        verify(requestVO).getFromDate();
        verify(requestVO, atLeast(1)).getUserName();
        verify(requestVO, atLeast(1)).getZoneCode();
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadClosingBalanceData6() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).setInt(anyInt(), anyInt());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        categoryVO.setAgentAllowed("Agent Allowed");
        categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        categoryVO.setAgentCategoryCode("Agent Category Code");
        categoryVO.setAgentCategoryName("Agent Category Name");
        categoryVO.setAgentCategoryStatus("Agent Category Status");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("Agent Category Type");
        categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        categoryVO.setAgentCp2pPayee("Cp2p Payee");
        categoryVO.setAgentCp2pPayer("Cp2p Payer");
        categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        categoryVO.setAgentDomainName("Agent Domain Name");
        categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        categoryVO.setAgentGatewayName("Agent Gateway Name");
        categoryVO.setAgentGatewayType("Agent Gateway Type");
        categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        categoryVO.setAgentRoleName("Agent Role Name");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("Type");
        categoryVO.setCategoryCode("Category Code");
        categoryVO.setCategoryName("Category Name");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("Category Status");
        categoryVO.setCategoryType("Category Type");
        categoryVO.setCategoryTypeCode("Category Type Code");
        categoryVO.setCp2pPayee("Payee");
        categoryVO.setCp2pPayer("Payer");
        categoryVO.setCp2pWithinList("Within List");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("Display Allowed");
        categoryVO.setDomainAllowed("Domain Allowed");
        categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        categoryVO.setDomainName("Domain Name");
        categoryVO.setDomainTypeCode("Domain Type Code");
        categoryVO.setFixedDomains("Fixed Domains");
        categoryVO.setFixedRoles("Fixed Roles");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("Grph Domain Type");
        categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("Modify Allowed");
        categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("Outlets Allowed");
        categoryVO.setParentCategoryCode("Parent Category Code");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("Product Type Allowed");
        categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("By Parent Only");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("Service Allowed");
        categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        categoryVO.setTransferToListOnly("Transfer To List Only");
        categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        categoryVO.setUserIdPrefix("User Id Prefix");
        categoryVO.setViewOnNetworkBlock("View On Network Block");
        categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getCategoryVO()).thenReturn(categoryVO);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("CHANNEL");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getFromDate()).thenReturn("2020-03-01");
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
        verify (com.btsl.util.JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement).setInt(anyInt(), anyInt());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(sessionUser).getCategoryVO();
        verify(sessionUser).getCategoryCode();
        verify(sessionUser, atLeast(1)).getDomainID();
        verify(sessionUser, atLeast(1)).getDomainName();
        verify(sessionUser).getUserID();
        verify(sessionUser, atLeast(1)).getUserType();
        verify(sessionUser).getDomainList();
        verify(sessionUser).getGeographicalAreaList();
        verify(requestVO, atLeast(1)).getCatCode();
        verify(requestVO, atLeast(1)).getDomainCode();
        verify(requestVO).getFromDate();
        verify(requestVO, atLeast(1)).getUserName();
        verify(requestVO, atLeast(1)).getZoneCode();
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadClosingBalanceData7() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.sql.processing
        //       at com.web.pretups.domain.businesslogic.CategoryWebDAO.loadCategoryReporSeqtList(CategoryWebDAO.java:3492)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1182)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).setInt(anyInt(), anyInt());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        categoryVO.setAgentAllowed("Agent Allowed");
        categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        categoryVO.setAgentCategoryCode("Agent Category Code");
        categoryVO.setAgentCategoryName("Agent Category Name");
        categoryVO.setAgentCategoryStatus("Agent Category Status");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("Agent Category Type");
        categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        categoryVO.setAgentCp2pPayee("Cp2p Payee");
        categoryVO.setAgentCp2pPayer("Cp2p Payer");
        categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        categoryVO.setAgentDomainName("Agent Domain Name");
        categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        categoryVO.setAgentGatewayName("Agent Gateway Name");
        categoryVO.setAgentGatewayType("Agent Gateway Type");
        categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        categoryVO.setAgentRoleName("Agent Role Name");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("Type");
        categoryVO.setCategoryCode("Category Code");
        categoryVO.setCategoryName("Category Name");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("Category Status");
        categoryVO.setCategoryType("Category Type");
        categoryVO.setCategoryTypeCode("Category Type Code");
        categoryVO.setCp2pPayee("Payee");
        categoryVO.setCp2pPayer("Payer");
        categoryVO.setCp2pWithinList("Within List");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("Display Allowed");
        categoryVO.setDomainAllowed("Domain Allowed");
        categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        categoryVO.setDomainName("Domain Name");
        categoryVO.setDomainTypeCode("Domain Type Code");
        categoryVO.setFixedDomains("Fixed Domains");
        categoryVO.setFixedRoles("Fixed Roles");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("Grph Domain Type");
        categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("Modify Allowed");
        categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("Outlets Allowed");
        categoryVO.setParentCategoryCode("Parent Category Code");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("Product Type Allowed");
        categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("By Parent Only");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("Service Allowed");
        categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        categoryVO.setTransferToListOnly("Transfer To List Only");
        categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        categoryVO.setUserIdPrefix("User Id Prefix");
        categoryVO.setViewOnNetworkBlock("View On Network Block");
        categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getCategoryVO()).thenReturn(categoryVO);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("CHANNEL");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getFromDate()).thenReturn("2020-03-01");
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadClosingBalanceData(Connection, UserVO, ClosingBalanceEnquiryRequestVO, ClosingBalanceEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadClosingBalanceData8() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO.loadUserListBasisOfZoneDomainCategory(ChannelUserReportDAO.java:120)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.loadClosingBalanceData(ChannelEnquiryServiceImpl.java:1195)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).setInt(anyInt(), anyInt());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        categoryVO.setAgentAllowed("Agent Allowed");
        categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        categoryVO.setAgentCategoryCode("Agent Category Code");
        categoryVO.setAgentCategoryName("Agent Category Name");
        categoryVO.setAgentCategoryStatus("Agent Category Status");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("Agent Category Type");
        categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        categoryVO.setAgentCp2pPayee("Cp2p Payee");
        categoryVO.setAgentCp2pPayer("Cp2p Payer");
        categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        categoryVO.setAgentDomainName("Agent Domain Name");
        categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        categoryVO.setAgentGatewayName("Agent Gateway Name");
        categoryVO.setAgentGatewayType("Agent Gateway Type");
        categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        categoryVO.setAgentRoleName("Agent Role Name");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("Type");
        categoryVO.setCategoryCode("Category Code");
        categoryVO.setCategoryName("Category Name");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("Category Status");
        categoryVO.setCategoryType("Category Type");
        categoryVO.setCategoryTypeCode("Category Type Code");
        categoryVO.setCp2pPayee("Payee");
        categoryVO.setCp2pPayer("Payer");
        categoryVO.setCp2pWithinList("Within List");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("Display Allowed");
        categoryVO.setDomainAllowed("Domain Allowed");
        categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        categoryVO.setDomainName("Domain Name");
        categoryVO.setDomainTypeCode("Domain Type Code");
        categoryVO.setFixedDomains("Fixed Domains");
        categoryVO.setFixedRoles("Fixed Roles");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("Grph Domain Type");
        categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("Modify Allowed");
        categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("Outlets Allowed");
        categoryVO.setParentCategoryCode("Parent Category Code");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("Product Type Allowed");
        categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("By Parent Only");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("Service Allowed");
        categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        categoryVO.setTransferToListOnly("Transfer To List Only");
        categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        categoryVO.setUserIdPrefix("User Id Prefix");
        categoryVO.setViewOnNetworkBlock("View On Network Block");
        categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        UserVO sessionUser = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUser.getCategoryVO()).thenReturn(categoryVO);
        when(sessionUser.getUserID()).thenReturn("User ID");
        when(sessionUser.getUserType()).thenReturn("CHANNEL");
        when(sessionUser.getDomainID()).thenReturn("Domain ID");
        when(sessionUser.getDomainName()).thenReturn("Domain Name");
        when(sessionUser.getDomainList()).thenReturn(new ArrayList());
        when(sessionUser.getCategoryCode()).thenReturn("Category Code");
        when(sessionUser.getGeographicalAreaList()).thenReturn(new ArrayList<>());
        ClosingBalanceEnquiryRequestVO requestVO = mock(ClosingBalanceEnquiryRequestVO.class);
        when(requestVO.getFromDate()).thenReturn("2020-03-01");
        when(requestVO.getDomainCode()).thenReturn("Domain Code");
        when(requestVO.getUserName()).thenReturn("janedoe");
        when(requestVO.getCatCode()).thenReturn("Cat Code");
        when(requestVO.getZoneCode()).thenReturn("Zone Code");
        ClosingBalanceEnquiryResponseVO response = mock(ClosingBalanceEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelEnquiryServiceImpl.loadClosingBalanceData (com.btsl.util.JUnitConfig.getConnection(), sessionUser, requestVO, response, response1,
                Locale.getDefault());
        verify (com.btsl.util.JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement).setInt(anyInt(), anyInt());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(sessionUser).getCategoryVO();
        verify(sessionUser).getCategoryCode();
        verify(sessionUser, atLeast(1)).getDomainID();
        verify(sessionUser, atLeast(1)).getDomainName();
        verify(sessionUser).getUserID();
        verify(sessionUser, atLeast(1)).getUserType();
        verify(sessionUser).getDomainList();
        verify(sessionUser).getGeographicalAreaList();
        verify(requestVO, atLeast(1)).getCatCode();
        verify(requestVO, atLeast(1)).getDomainCode();
        verify(requestVO).getFromDate();
        verify(requestVO, atLeast(1)).getUserName();
        verify(requestVO, atLeast(1)).getZoneCode();
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createExcelFile(String, ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: sheetName must not be null
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:945)
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:131)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createExcelFile(ChannelEnquiryServiceImpl.java:1360)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        ArrayList<HeaderColumn> editColumns = new ArrayList<>();
        channelEnquiryServiceImpl.createExcelFile(null, dataList, editColumns, new ArrayList<>());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createExcelFile(String, ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile2() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: sheetName '' is invalid - character count MUST be greater than or equal to 1 and less than or equal to 31
        //       at org.apache.poi.ss.util.WorkbookUtil.validateSheetName(WorkbookUtil.java:136)
        //       at org.apache.poi.hssf.record.BoundSheetRecord.setSheetname(BoundSheetRecord.java:98)
        //       at org.apache.poi.hssf.model.InternalWorkbook.setSheetName(InternalWorkbook.java:605)
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:954)
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:131)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createExcelFile(ChannelEnquiryServiceImpl.java:1360)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        ArrayList<HeaderColumn> editColumns = new ArrayList<>();
        channelEnquiryServiceImpl.createExcelFile("", dataList, editColumns, new ArrayList<>());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createExcelFile(String, ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile3() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //       at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        //       at java.util.ArrayList.get(ArrayList.java:435)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createExcelFile(ChannelEnquiryServiceImpl.java:1372)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        ArrayList<HeaderColumn> editColumns = new ArrayList<>();

        ArrayList<ArrayList<String>> filterData = new ArrayList<>();
        filterData.add(new ArrayList<>());
        channelEnquiryServiceImpl.createExcelFile("foo.txt", dataList, editColumns, filterData);
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createExcelFile(String, ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile4() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //       at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        //       at java.util.ArrayList.get(ArrayList.java:435)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createExcelFile(ChannelEnquiryServiceImpl.java:1406)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        dataList.add(new ArrayList<>());

        ArrayList<HeaderColumn> editColumns = new ArrayList<>();
        editColumns.add(new HeaderColumn("Column Name", "Display Name"));
        channelEnquiryServiceImpl.createExcelFile("foo.txt", dataList, editColumns, new ArrayList<>());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createExcelFile(String, ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile5() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
        //       at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1384)
        //       at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
        //       at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        //       at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
        //       at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        //       at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createExcelFile(ChannelEnquiryServiceImpl.java:1381)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();

        ArrayList<HeaderColumn> editColumns = new ArrayList<>();
        editColumns.add(null);
        channelEnquiryServiceImpl.createExcelFile("foo.txt", dataList, editColumns, new ArrayList<>());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createCSVFile(ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateCSVFile() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //       at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        //       at java.util.ArrayList.get(ArrayList.java:435)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createCSVFile(ChannelEnquiryServiceImpl.java:1445)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        dataList.add(new ArrayList<>());

        ArrayList<HeaderColumn> editColumns = new ArrayList<>();
        editColumns.add(new HeaderColumn("\n", "\n"));
        channelEnquiryServiceImpl.createCSVFile(dataList, editColumns, new ArrayList<>());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createCSVFile(ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateCSVFile2() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
        //       at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1384)
        //       at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
        //       at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        //       at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
        //       at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        //       at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createCSVFile(ChannelEnquiryServiceImpl.java:1434)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        dataList.add(new ArrayList<>());

        ArrayList<HeaderColumn> editColumns = new ArrayList<>();
        editColumns.add(null);
        channelEnquiryServiceImpl.createCSVFile(dataList, editColumns, new ArrayList<>());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createCSVFile(ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateCSVFile3() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //       at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        //       at java.util.ArrayList.get(ArrayList.java:435)
        //       at com.restapi.channelenquiry.service.ChannelEnquiryServiceImpl.createCSVFile(ChannelEnquiryServiceImpl.java:1445)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        dataList.add(new ArrayList<>());
        HeaderColumn headerColumn = mock(HeaderColumn.class);
        when(headerColumn.getColumnName()).thenReturn("Column Name");
        when(headerColumn.getDisplayName()).thenReturn("Display Name");

        ArrayList<HeaderColumn> editColumns = new ArrayList<>();
        editColumns.add(headerColumn);
        channelEnquiryServiceImpl.createCSVFile(dataList, editColumns, new ArrayList<>());
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#createExcelXFile(String, ArrayList, List, ArrayList)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelXFile() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R005 Unable to load class.
        //   Class: org.apache.xmlbeans.XmlObject
        //   Please check that the class is available on your test runtime classpath.
        //   See https://diff.blue/R005 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        String fileName = "";
        ArrayList<ArrayList<String>> data = null;
        List<HeaderColumn> editColumns = null;
        ArrayList<ArrayList<String>> filterData = null;

        // Act
        String actualCreateExcelXFileResult = this.channelEnquiryServiceImpl.createExcelXFile(fileName, data, editColumns,
                filterData);

        // Assert
        // TODO: Add assertions on result
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#getDayDiffFromUserClosingBalance(Date, String)}
     *//*

    @Test
    public void testGetDayDiffFromUserClosingBalance() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        channelEnquiryServiceImpl.getDayDiffFromUserClosingBalance(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()), "This Data");
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#getAlertCounterSummaryData(Connection, UserVO, AlertCounterSummaryRequestVO, AlertCounterSummaryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetAlertCounterSummaryData() throws BTSLBaseException, ParseException {

        JUnitConfig.init();

        Connection con = null;
        UserVO userVO = JUnitConfig.getUserVO();

        AlertCounterSummaryRequestVO requestVO = JUnitConfig.getAlertCounterSummaryRequestVO();
        AlertCounterSummaryResponseVO response = mock(AlertCounterSummaryResponseVO.class);

        HttpServletResponse response1 = mock(HttpServletResponse.class);
        Locale locale = null;

        // Act
        this.channelEnquiryServiceImpl.getAlertCounterSummaryData(JUnitConfig.getConnection(), userVO, requestVO, response, response1, locale);

        // Assert
        // TODO: Add assertions on result
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#getBatchC2cTransferdetails(BatchC2cTransferRequestVO, BatchC2cTransferResponseVO, HttpServletResponse, OAuthUser, Locale, String)}
     *//*

    @Test
    public void testGetBatchC2cTransferdetails() throws Exception {
        // Arrange
        // TODO: Populate arranged inputs
        BatchC2cTransferRequestVO batchC2cTransferRequestVO = null;
        BatchC2cTransferResponseVO response = null;
        HttpServletResponse responseSwag = null;
        OAuthUser oAuthUserData = null;
        Locale locale = null;
        String searchBy = "";

        // Act
        this.channelEnquiryServiceImpl.getBatchC2cTransferdetails(batchC2cTransferRequestVO, response, responseSwag,
                oAuthUserData, locale, searchBy);

        // Assert
        // TODO: Add assertions on result
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadC2STransferEnquiryListStaff(Connection, UserVO, C2SEnquiryRequestVO, C2SEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadC2STransferEnquiryListStaff() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        Connection con = mock(Connection.class);
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        C2SEnquiryRequestVO c2sEnquiryRequestVO = mock(C2SEnquiryRequestVO.class);
        when(c2sEnquiryRequestVO.getFromDate()).thenReturn("2020-03-01");
        C2SEnquiryResponseVO response = mock(C2SEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        channelEnquiryServiceImpl.loadC2STransferEnquiryListStaff (com.btsl.util.JUnitConfig.getConnection(), userVO, c2sEnquiryRequestVO, response, response1,
                Locale.getDefault());
        verify(c2sEnquiryRequestVO).getFromDate();
    }

    */
/**
     * Method under test: {@link ChannelEnquiryServiceImpl#loadC2STransferEnquiryListStaff(Connection, UserVO, C2SEnquiryRequestVO, C2SEnquiryResponseVO, HttpServletResponse, Locale)}
     *//*

    @Test
    public void testLoadC2STransferEnquiryListStaff2() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        ChannelEnquiryServiceImpl channelEnquiryServiceImpl = new ChannelEnquiryServiceImpl();
        Connection con = mock(Connection.class);
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        C2SEnquiryRequestVO c2sEnquiryRequestVO = mock(C2SEnquiryRequestVO.class);
        when(c2sEnquiryRequestVO.getFromDate()).thenReturn(" Entered");
        C2SEnquiryResponseVO response = mock(C2SEnquiryResponseVO.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        channelEnquiryServiceImpl.loadC2STransferEnquiryListStaff (com.btsl.util.JUnitConfig.getConnection(), userVO, c2sEnquiryRequestVO, response, response1,
                Locale.getDefault());
        verify(c2sEnquiryRequestVO).getFromDate();
    }
}

*/
