package com.restapi.networkadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnectionI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.networkadmin.requestVO.AddO2CTransferRuleReqVO;
import com.restapi.networkadmin.requestVO.UpdateO2CTransferRuleReqVO;
import com.restapi.networkadmin.responseVO.CategoryDomainListResponseVO;
import com.restapi.networkadmin.responseVO.ToCategoryListResponseVO;
import com.restapi.networkadmin.responseVO.TransferRulesListResponseVO;
import com.restapi.networkadminVO.AddO2CTransferRuleVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {AssociateO2CTransferRuleServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AssociateO2CTransferRuleServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl;

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadDomainListForOperator(Connection, Locale, HttpServletResponse, UserVO, CategoryDomainListResponseVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadDomainListForOperator() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadDomainListForOperator(AssociateO2CTransferRuleServiceImpl.java:105)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadDomainListForOperator(AssociateO2CTransferRuleServiceImpl.java:105)
        //   See https://diff.blue/R013 to resolve this issue.


        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        */

        JUnitConfig.init();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getNetworkName()).thenReturn("Network Name");
        CategoryDomainListResponseVO response = mock(CategoryDomainListResponseVO.class);


        ArrayList categoryDomainList = new ArrayList();

        ListValueVO listValueVO = new ListValueVO("domain_name", "domain_code");
        listValueVO.setType("restricted_msisdn");
        listValueVO.setOtherInfo("domain_type_code");
        listValueVO.setStatusType("display_allowed");

        categoryDomainList.add(listValueVO);

        when(response.getCategoryDomainList()).thenReturn(categoryDomainList);

        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setCategoryDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setNetworkCode(Mockito.<String>any());
        doNothing().when(response).setNetworkDescription(Mockito.<String>any());
        doNothing().when(response).setType(Mockito.<String>any());
        doNothing().when(response).setUserCategory(Mockito.<String>any());
        associateO2CTransferRuleServiceImpl.loadDomainListForOperator(JUnitConfig.getConnection(), locale, response1, userVO, response);
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadDomainListForOperator(Connection, Locale, HttpServletResponse, UserVO, CategoryDomainListResponseVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadDomainListForOperator2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadDomainListForOperator(AssociateO2CTransferRuleServiceImpl.java:105)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadDomainListForOperator(AssociateO2CTransferRuleServiceImpl.java:105)
        //   See https://diff.blue/R013 to resolve this issue.

        JUnitConfig.init();
        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        */
        //CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getNetworkName()).thenReturn("Network Name");
        CategoryDomainListResponseVO response = mock(CategoryDomainListResponseVO.class);


        ArrayList categoryDomainList = new ArrayList();

        ListValueVO listValueVO = new ListValueVO("domain_name", "domain_code");
        listValueVO.setType("restricted_msisdn");
        listValueVO.setOtherInfo("domain_type_code");
        listValueVO.setStatusType("display_allowed");

        categoryDomainList.add(listValueVO);


        when(response.getCategoryDomainList()).thenReturn(categoryDomainList);

        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setCategoryDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setNetworkCode(Mockito.<String>any());
        doNothing().when(response).setNetworkDescription(Mockito.<String>any());
        doNothing().when(response).setType(Mockito.<String>any());
        doNothing().when(response).setUserCategory(Mockito.<String>any());
        associateO2CTransferRuleServiceImpl.loadDomainListForOperator(JUnitConfig.getConnection(), null, response1, userVO, response);
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadDomainListForOperator(Connection, Locale, HttpServletResponse, UserVO, CategoryDomainListResponseVO)}
     */
    @Test
    public void testLoadDomainListForOperator3() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadDomainListForOperator(AssociateO2CTransferRuleServiceImpl.java:105)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
/*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
*/
    JUnitConfig.init();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = new CustomResponseWrapper(new MockHttpServletResponse());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getNetworkName()).thenReturn("Network Name");
        CategoryDomainListResponseVO response = mock(CategoryDomainListResponseVO.class);


        ArrayList categoryDomainList = new ArrayList();

        ListValueVO listValueVO = new ListValueVO("domain_name", "domain_code");
        listValueVO.setType("restricted_msisdn");
        listValueVO.setOtherInfo("domain_type_code");
        listValueVO.setStatusType("display_allowed");

        categoryDomainList.add(listValueVO);


        when(response.getCategoryDomainList()).thenReturn(categoryDomainList);

        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setCategoryDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setNetworkCode(Mockito.<String>any());
        doNothing().when(response).setNetworkDescription(Mockito.<String>any());
        doNothing().when(response).setType(Mockito.<String>any());
        doNothing().when(response).setUserCategory(Mockito.<String>any());
        assertSame(response,
                associateO2CTransferRuleServiceImpl.loadDomainListForOperator(JUnitConfig.getConnection(), locale, response1, userVO, response));
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
        verify(userVO).getNetworkID();
        verify(userVO).getNetworkName();
        verify(response).getCategoryDomainList();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setCategoryDomainList(Mockito.<ArrayList<Object>>any());
        verify(response).setNetworkCode(Mockito.<String>any());
        verify(response).setNetworkDescription(Mockito.<String>any());
        verify(response).setType(Mockito.<String>any());
        verify(response).setUserCategory(Mockito.<String>any());
        assertEquals(400, ((MockHttpServletResponse) response1.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadDomainListForOperator(Connection, Locale, HttpServletResponse, UserVO, CategoryDomainListResponseVO)}
     */
    @Test
    public void testLoadDomainListForOperator4() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadDomainListForOperator(AssociateO2CTransferRuleServiceImpl.java:105)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
/*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
*/
        JUnitConfig.init();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getNetworkName()).thenReturn("Network Name");

        ArrayList arrayList = new ArrayList();
        arrayList.add("42");
        CategoryDomainListResponseVO response = mock(CategoryDomainListResponseVO.class);
        when(response.getCategoryDomainList()).thenReturn(arrayList);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setCategoryDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setNetworkCode(Mockito.<String>any());
        doNothing().when(response).setNetworkDescription(Mockito.<String>any());
        doNothing().when(response).setType(Mockito.<String>any());
        doNothing().when(response).setUserCategory(Mockito.<String>any());
        assertSame(response,
                associateO2CTransferRuleServiceImpl.loadDomainListForOperator(JUnitConfig.getConnection(), locale, response1, userVO, response));
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
*/
        verify(userVO).getNetworkID();
        verify(userVO).getNetworkName();
        verify(response).getCategoryDomainList();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setCategoryDomainList(Mockito.<ArrayList<Object>>any());
        verify(response).setNetworkCode(Mockito.<String>any());
        verify(response).setNetworkDescription(Mockito.<String>any());
        verify(response).setType(Mockito.<String>any());
        verify(response).setUserCategory(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadTransferRuleslist(Connection, Locale, HttpServletResponse, UserVO, TransferRulesListResponseVO, String, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadTransferRuleslist() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadTransferRuleslist(AssociateO2CTransferRuleServiceImpl.java:190)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadTransferRuleslist(AssociateO2CTransferRuleServiceImpl.java:190)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
/*
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
*/
        JUnitConfig.init();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        TransferRulesListResponseVO response = mock(TransferRulesListResponseVO.class);


        when(response.getTransferRulesList()).thenReturn(new ArrayList());


        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setDomainName(Mockito.<String>any());
        doNothing().when(response).setToDomainName(Mockito.<String>any());
        doNothing().when(response).setTransferRulesList(Mockito.<ArrayList<Object>>any());
        associateO2CTransferRuleServiceImpl.loadTransferRuleslist(JUnitConfig.getConnection(), locale, response1, userVO, response,
                "User Category", "Domain Code", "Type");
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadTransferRuleslist(Connection, Locale, HttpServletResponse, UserVO, TransferRulesListResponseVO, String, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadTransferRuleslist2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadTransferRuleslist(AssociateO2CTransferRuleServiceImpl.java:190)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadTransferRuleslist(AssociateO2CTransferRuleServiceImpl.java:190)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
/*        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);*/

        JUnitConfig.init();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        TransferRulesListResponseVO response = mock(TransferRulesListResponseVO.class);
        when(response.getTransferRulesList()).thenReturn(new ArrayList());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setDomainName(Mockito.<String>any());
        doNothing().when(response).setToDomainName(Mockito.<String>any());
        doNothing().when(response).setTransferRulesList(Mockito.<ArrayList<Object>>any());
        associateO2CTransferRuleServiceImpl.loadTransferRuleslist(JUnitConfig.getConnection(), null, response1, userVO, response, "User Category",
                "Domain Code", "Type");
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadTransferRuleslist(Connection, Locale, HttpServletResponse, UserVO, TransferRulesListResponseVO, String, String, String)}
     */
    @Test
    public void testLoadTransferRuleslist3() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadTransferRuleslist(AssociateO2CTransferRuleServiceImpl.java:190)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
    /*    Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);*/

        JUnitConfig.init();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = new CustomResponseWrapper(new MockHttpServletResponse());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        TransferRulesListResponseVO response = mock(TransferRulesListResponseVO.class);
        when(response.getTransferRulesList()).thenReturn(new ArrayList());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setDomainName(Mockito.<String>any());
        doNothing().when(response).setToDomainName(Mockito.<String>any());
        doNothing().when(response).setTransferRulesList(Mockito.<ArrayList<Object>>any());
        assertSame(response, associateO2CTransferRuleServiceImpl.loadTransferRuleslist(JUnitConfig.getConnection(), locale, response1, userVO,
                response, "User Category", "Domain Code", "Type"));
       /* verify(JunitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp, atLeast(1)).getTime();
       */ verify(userVO).getNetworkID();
        verify(response).getTransferRulesList();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setDomainName(Mockito.<String>any());
        verify(response).setToDomainName(Mockito.<String>any());
        verify(response).setTransferRulesList(Mockito.<ArrayList<Object>>any());
        assertEquals(400, ((MockHttpServletResponse) response1.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadTransferRuleslist(Connection, Locale, HttpServletResponse, UserVO, TransferRulesListResponseVO, String, String, String)}
     */
    @Test
    public void testLoadTransferRuleslist4() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadTransferRuleslist(AssociateO2CTransferRuleServiceImpl.java:190)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
       /* Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);*/
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");

        ArrayList arrayList = new ArrayList();
        arrayList.add("42");
        TransferRulesListResponseVO response = mock(TransferRulesListResponseVO.class);
        when(response.getTransferRulesList()).thenReturn(arrayList);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setDomainName(Mockito.<String>any());
        doNothing().when(response).setToDomainName(Mockito.<String>any());
        doNothing().when(response).setTransferRulesList(Mockito.<ArrayList<Object>>any());
        assertSame(response, associateO2CTransferRuleServiceImpl.loadTransferRuleslist(JUnitConfig.getConnection(), locale, response1, userVO,
                response, "User Category", "Domain Code", "Type"));
     /*   verify(JunitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp, atLeast(1)).getTime();
  */      verify(userVO).getNetworkID();
        verify(response).getTransferRulesList();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setDomainName(Mockito.<String>any());
        verify(response).setToDomainName(Mockito.<String>any());
        verify(response).setTransferRulesList(Mockito.<ArrayList<Object>>any());
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadToCategoryList(Connection, Locale, HttpServletResponse, UserVO, ToCategoryListResponseVO, String, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadToCategoryList() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadToCategoryList(AssociateO2CTransferRuleServiceImpl.java:298)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadToCategoryList(AssociateO2CTransferRuleServiceImpl.java:298)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       */
        JUnitConfig.init();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        ToCategoryListResponseVO response = mock(ToCategoryListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setProductList(Mockito.<ArrayList<Object>>any());
        associateO2CTransferRuleServiceImpl.loadToCategoryList(JUnitConfig.getConnection(), locale, response1, userVO, response, "User Category",
                "Domain Code", "Type");
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadToCategoryList(Connection, Locale, HttpServletResponse, UserVO, ToCategoryListResponseVO, String, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadToCategoryList2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadToCategoryList(AssociateO2CTransferRuleServiceImpl.java:298)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadToCategoryList(AssociateO2CTransferRuleServiceImpl.java:298)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
    /*    ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      */
        JUnitConfig.init();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        ToCategoryListResponseVO response = mock(ToCategoryListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setProductList(Mockito.<ArrayList<Object>>any());
        associateO2CTransferRuleServiceImpl.loadToCategoryList(JUnitConfig.getConnection(), null, response1, userVO, response, "User Category",
                "Domain Code", "Type");
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#loadToCategoryList(Connection, Locale, HttpServletResponse, UserVO, ToCategoryListResponseVO, String, String, String)}
     */
    @Test
    public void testLoadToCategoryList3() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.loadToCategoryList(AssociateO2CTransferRuleServiceImpl.java:298)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
    */
        JUnitConfig.init();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = new CustomResponseWrapper(new MockHttpServletResponse());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        ToCategoryListResponseVO response = mock(ToCategoryListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setProductList(Mockito.<ArrayList<Object>>any());
        assertSame(response, associateO2CTransferRuleServiceImpl.loadToCategoryList(JUnitConfig.getConnection(), locale, response1, userVO,
                response, "User Category", "Domain Code", "Type"));
     /*   verify(JunitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
    */    verify(userVO, atLeast(1)).getNetworkID();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setProductList(Mockito.<ArrayList<Object>>any());
        assertEquals(400, ((MockHttpServletResponse) response1.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#addO2CTransferRule(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, AddO2CTransferRuleReqVO, AddO2CTransferRuleVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testAddO2CTransferRule() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
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
        HttpServletResponse response1 = null;
        UserVO userVO = JUnitConfig.getUserVO();;
        BaseResponse response = null;
        AddO2CTransferRuleReqVO request = new AddO2CTransferRuleReqVO();
        request.setDomainCode("String");
        request.setDpAllowed("Y");
        request.setTransferAllowed("Y");
        request.setDomainCode("String");
        AddO2CTransferRuleVO addO2CTransferRuleVO = null;

        // Act
        BaseResponse actualAddO2CTransferRuleResult = this.associateO2CTransferRuleServiceImpl.addO2CTransferRule(JUnitConfig.getConnection(),
                JUnitConfig.getMComConnection(), locale, response1, userVO, response, request, addO2CTransferRuleVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#updateO2CTransferRule(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateO2CTransferRuleReqVO, AddO2CTransferRuleVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testUpdateO2CTransferRule() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 1
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.updateO2CTransferRule(AssociateO2CTransferRuleServiceImpl.java:700)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     */    MComConnectionI mcomCon = mock(MComConnectionI.class);
        JUnitConfig.init();
     when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());


        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        BaseResponse response = mock(BaseResponse.class);
        UpdateO2CTransferRuleReqVO request = mock(UpdateO2CTransferRuleReqVO.class);
        when(request.getFirstApprovalLimit()).thenReturn(42L);
        when(request.getSecondApprovalLimit()).thenReturn(42L);
        when(request.getDomainCode()).thenReturn("Domain Code");
        when(request.getDpAllowed()).thenReturn("Dp Allowed");
        when(request.getFocAllowed()).thenReturn("Foc Allowed");
        when(request.getReturnAllowed()).thenReturn("Return Allowed");
        when(request.getToCategory()).thenReturn("To Category");
        when(request.getToCategoryDes()).thenReturn("To Category Des");
        when(request.getTransferAllowed()).thenReturn("Transfer Allowed");
        when(request.getWithdrawAllowed()).thenReturn("Withdraw Allowed");
        when(request.getProductArray()).thenReturn(new String[]{"Product Array"});
        when(request.getLastModifiedTime()).thenReturn(1L);
        AddO2CTransferRuleVO addO2CTransferRuleVO = mock(AddO2CTransferRuleVO.class);
        when(addO2CTransferRuleVO.getApprovalRequired()).thenReturn("Approval Required");
        when(addO2CTransferRuleVO.getCntrlReturnLevel()).thenReturn("Cntrl Return Level");
        when(addO2CTransferRuleVO.getCntrlTransferLevel()).thenReturn("Cntrl Transfer Level");
        when(addO2CTransferRuleVO.getCntrlWithdrawLevel()).thenReturn("Cntrl Withdraw Level");
        when(addO2CTransferRuleVO.getCreatedBy()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(addO2CTransferRuleVO.getDirectTransferAllowed()).thenReturn("Direct Transfer Allowed");
        when(addO2CTransferRuleVO.getFixedReturnLevel()).thenReturn("Fixed Return Level");
        when(addO2CTransferRuleVO.getFixedTransferLevel()).thenReturn("Fixed Transfer Level");
        when(addO2CTransferRuleVO.getFixedWithdrawLevel()).thenReturn("Fixed Withdraw Level");
        when(addO2CTransferRuleVO.getFocTransferType()).thenReturn("Foc Transfer Type");
        when(addO2CTransferRuleVO.getFromCategory()).thenReturn("jane.doe@example.org");
        when(addO2CTransferRuleVO.getFromCategoryDes()).thenReturn("jane.doe@example.org");
        when(addO2CTransferRuleVO.getModifiedBy()).thenReturn("Jan 1, 2020 9:00am GMT+0100");
        when(addO2CTransferRuleVO.getRestrictedMsisdnAccess()).thenReturn("Restricted Msisdn Access");
        when(addO2CTransferRuleVO.getRestrictedRechargeAccess()).thenReturn("Restricted Recharge Access");
        when(addO2CTransferRuleVO.getReturnChnlBypassAllowed()).thenReturn("Return Chnl Bypass Allowed");
        when(addO2CTransferRuleVO.getTransferChnlBypassAllowed()).thenReturn("Transfer Chnl Bypass Allowed");
        when(addO2CTransferRuleVO.getTransferRuleID()).thenReturn("Transfer Rule ID");
        when(addO2CTransferRuleVO.getTransferType()).thenReturn("Transfer Type");
        when(addO2CTransferRuleVO.getType()).thenReturn("Type");
        when(addO2CTransferRuleVO.getUncntrlReturnAllowed()).thenReturn("Uncntrl Return Allowed");
        when(addO2CTransferRuleVO.getUncntrlReturnLevel()).thenReturn("Uncntrl Return Level");
        when(addO2CTransferRuleVO.getUncntrlTransferAllowed()).thenReturn("Uncntrl Transfer Allowed");
        when(addO2CTransferRuleVO.getUncntrlTransferAllowedTmp()).thenReturn("Uncntrl Transfer Allowed Tmp");
        when(addO2CTransferRuleVO.getUncntrlTransferLevel()).thenReturn("Uncntrl Transfer Level");
        when(addO2CTransferRuleVO.getUncntrlWithdrawAllowed()).thenReturn("Uncntrl Withdraw Allowed");
        when(addO2CTransferRuleVO.getUncntrlWithdrawLevel()).thenReturn("Uncntrl Withdraw Level");
        when(addO2CTransferRuleVO.getWithdrawChnlBypassAllowed()).thenReturn("Withdraw Chnl Bypass Allowed");
        when(addO2CTransferRuleVO.getCreatedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        when(addO2CTransferRuleVO.getModifiedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        doNothing().when(addO2CTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFocTransferType(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFromCategory(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setTransferType(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setType(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
        associateO2CTransferRuleServiceImpl.updateO2CTransferRule(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                request, addO2CTransferRuleVO);
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#updateO2CTransferRule(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateO2CTransferRuleReqVO, AddO2CTransferRuleVO)}
     */
    @Test
    public void testUpdateO2CTransferRule2() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     */
        JUnitConfig.init();

        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());



        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        thrown.expect(BTSLBaseException.class);
        associateO2CTransferRuleServiceImpl.updateO2CTransferRule(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                mock(BaseResponse.class), mock(UpdateO2CTransferRuleReqVO.class), mock(AddO2CTransferRuleVO.class));
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
        */verify(userVO).getNetworkID();
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#deleteO2CTransferRule(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateO2CTransferRuleReqVO, AddO2CTransferRuleVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testDeleteO2CTransferRule() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 1
        //       at com.restapi.networkadmin.service.AssociateO2CTransferRuleServiceImpl.deleteO2CTransferRule(AssociateO2CTransferRuleServiceImpl.java:914)
        //   See https://diff.blue/R013 to resolve this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       */

        JUnitConfig.init();

        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());


        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        BaseResponse response = mock(BaseResponse.class);
        UpdateO2CTransferRuleReqVO request = mock(UpdateO2CTransferRuleReqVO.class);
        when(request.getFirstApprovalLimit()).thenReturn(42L);
        when(request.getSecondApprovalLimit()).thenReturn(42L);
        when(request.getDomainCode()).thenReturn("Domain Code");
        when(request.getDpAllowed()).thenReturn("Dp Allowed");
        when(request.getFocAllowed()).thenReturn("Foc Allowed");
        when(request.getReturnAllowed()).thenReturn("Return Allowed");
        when(request.getToCategory()).thenReturn("To Category");
        when(request.getToCategoryDes()).thenReturn("To Category Des");
        when(request.getTransferAllowed()).thenReturn("Transfer Allowed");
        when(request.getWithdrawAllowed()).thenReturn("Withdraw Allowed");
        when(request.getProductArray()).thenReturn(new String[]{"Product Array"});
        when(request.getLastModifiedTime()).thenReturn(1L);
        AddO2CTransferRuleVO addO2CTransferRuleVO = mock(AddO2CTransferRuleVO.class);
        when(addO2CTransferRuleVO.getApprovalRequired()).thenReturn("Approval Required");
        when(addO2CTransferRuleVO.getCntrlReturnLevel()).thenReturn("Cntrl Return Level");
        when(addO2CTransferRuleVO.getCntrlTransferLevel()).thenReturn("Cntrl Transfer Level");
        when(addO2CTransferRuleVO.getCntrlWithdrawLevel()).thenReturn("Cntrl Withdraw Level");
        when(addO2CTransferRuleVO.getCreatedBy()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(addO2CTransferRuleVO.getDirectTransferAllowed()).thenReturn("Direct Transfer Allowed");
        when(addO2CTransferRuleVO.getFixedReturnLevel()).thenReturn("Fixed Return Level");
        when(addO2CTransferRuleVO.getFixedTransferLevel()).thenReturn("Fixed Transfer Level");
        when(addO2CTransferRuleVO.getFixedWithdrawLevel()).thenReturn("Fixed Withdraw Level");
        when(addO2CTransferRuleVO.getFocTransferType()).thenReturn("Foc Transfer Type");
        when(addO2CTransferRuleVO.getFromCategory()).thenReturn("String:String");
        when(addO2CTransferRuleVO.getFromCategoryDes()).thenReturn("jane.doe@example.org");
        when(addO2CTransferRuleVO.getModifiedBy()).thenReturn("Jan 1, 2020 9:00am GMT+0100");
        when(addO2CTransferRuleVO.getRestrictedMsisdnAccess()).thenReturn("Restricted Msisdn Access");
        when(addO2CTransferRuleVO.getRestrictedRechargeAccess()).thenReturn("Restricted Recharge Access");
        when(addO2CTransferRuleVO.getReturnChnlBypassAllowed()).thenReturn("Return Chnl Bypass Allowed");
        when(addO2CTransferRuleVO.getTransferChnlBypassAllowed()).thenReturn("Transfer Chnl Bypass Allowed");
        when(addO2CTransferRuleVO.getTransferRuleID()).thenReturn("Transfer Rule ID");
        when(addO2CTransferRuleVO.getTransferType()).thenReturn("Transfer Type");
        when(addO2CTransferRuleVO.getType()).thenReturn("Type");
        when(addO2CTransferRuleVO.getUncntrlReturnAllowed()).thenReturn("Uncntrl Return Allowed");
        when(addO2CTransferRuleVO.getUncntrlReturnLevel()).thenReturn("Uncntrl Return Level");
        when(addO2CTransferRuleVO.getUncntrlTransferAllowed()).thenReturn("Uncntrl Transfer Allowed");
        when(addO2CTransferRuleVO.getUncntrlTransferAllowedTmp()).thenReturn("Uncntrl Transfer Allowed Tmp");
        when(addO2CTransferRuleVO.getUncntrlTransferLevel()).thenReturn("Uncntrl Transfer Level");
        when(addO2CTransferRuleVO.getUncntrlWithdrawAllowed()).thenReturn("Uncntrl Withdraw Allowed");
        when(addO2CTransferRuleVO.getUncntrlWithdrawLevel()).thenReturn("Uncntrl Withdraw Level");
        when(addO2CTransferRuleVO.getWithdrawChnlBypassAllowed()).thenReturn("Withdraw Chnl Bypass Allowed");
        when(addO2CTransferRuleVO.getCreatedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        when(addO2CTransferRuleVO.getModifiedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        doNothing().when(addO2CTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFocTransferType(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFromCategory(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setTransferType(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setType(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(addO2CTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
        associateO2CTransferRuleServiceImpl.deleteO2CTransferRule(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                request, addO2CTransferRuleVO);
    }

    /**
     * Method under test: {@link AssociateO2CTransferRuleServiceImpl#deleteO2CTransferRule(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateO2CTransferRuleReqVO, AddO2CTransferRuleVO)}
     */
    @Test
    public void testDeleteO2CTransferRule2() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        AssociateO2CTransferRuleServiceImpl associateO2CTransferRuleServiceImpl = new AssociateO2CTransferRuleServiceImpl();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     */

        JUnitConfig.init();

        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());


        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        thrown.expect(BTSLBaseException.class);
        associateO2CTransferRuleServiceImpl.deleteO2CTransferRule(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                mock(BaseResponse.class), mock(UpdateO2CTransferRuleReqVO.class), mock(AddO2CTransferRuleVO.class));
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
        */verify(userVO).getNetworkID();
    }
}

