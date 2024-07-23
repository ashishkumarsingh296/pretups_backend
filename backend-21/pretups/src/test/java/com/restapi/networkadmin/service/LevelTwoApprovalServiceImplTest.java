package com.restapi.networkadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.networkadmin.requestVO.ApprovaLevelTwoStockTxnRequestVO;
import com.restapi.networkadmin.responseVO.DisplayStockLevelTwoResponseVO;
import com.restapi.networkadmin.responseVO.LevelTwoApprovalListResponseVO;
import com.restapi.networkadminVO.DisplayStockVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {LevelTwoApprovalServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LevelTwoApprovalServiceImplTest {
    @Autowired
    private LevelTwoApprovalServiceImpl levelTwoApprovalServiceImpl;

    /**
     * Method under test: {@link LevelTwoApprovalServiceImpl#levelTwoApprovalList(MultiValueMap, HttpServletResponse, Connection, Locale, UserVO, LevelTwoApprovalListResponseVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLevelTwoApprovalList() throws SQLException {
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.levelTwoApprovalList(LevelTwoApprovalServiceImpl.java:93)
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.levelTwoApprovalList(LevelTwoApprovalServiceImpl.java:93)
        //   See https://diff.blue/R013 to resolve this issue.

        LevelTwoApprovalServiceImpl levelTwoApprovalServiceImpl = new LevelTwoApprovalServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
        //CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = mock(CustomResponseWrapper.class);
        doNothing().when(response1).setStatus(Mockito.anyInt());

/*

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getUserID()).thenReturn("User ID");
        LevelTwoApprovalListResponseVO response = mock(LevelTwoApprovalListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setNetworkCode(Mockito.<String>any());
        doNothing().when(response).setUserID(Mockito.<String>any());
        levelTwoApprovalServiceImpl.levelTwoApprovalList(headers, response1, JUnitConfig.getConnection(), locale, userVO, response);
    }

    /**
     * Method under test: {@link LevelTwoApprovalServiceImpl#levelTwoApprovalList(MultiValueMap, HttpServletResponse, Connection, Locale, UserVO, LevelTwoApprovalListResponseVO)}
     */
    @Test
    public void testLevelTwoApprovalList2() throws SQLException {
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.levelTwoApprovalList(LevelTwoApprovalServiceImpl.java:93)
        //   See https://diff.blue/R013 to resolve this issue.

        LevelTwoApprovalServiceImpl levelTwoApprovalServiceImpl = new LevelTwoApprovalServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
       // CustomResponseWrapper response1 = new CustomResponseWrapper(new MockHttpServletResponse());
        CustomResponseWrapper response1 = mock(CustomResponseWrapper.class);

        doNothing().when(response1).setStatus(Mockito.anyInt());

        /*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getUserID()).thenReturn("User ID");
        LevelTwoApprovalListResponseVO response = mock(LevelTwoApprovalListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setNetworkCode(Mockito.<String>any());
        doNothing().when(response).setUserID(Mockito.<String>any());
        assertSame(response,
                levelTwoApprovalServiceImpl.levelTwoApprovalList(headers, response1, JUnitConfig.getConnection(), locale, userVO, response));
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet).close();
*/
        verify(userVO, atLeast(1)).getNetworkID();
        verify(userVO).getUserID();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setNetworkCode(Mockito.<String>any());
        verify(response).setUserID(Mockito.<String>any());
        assertEquals(400, ((MockHttpServletResponse) response1.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link LevelTwoApprovalServiceImpl#levelTwoApprovalList(MultiValueMap, HttpServletResponse, Connection, Locale, UserVO, LevelTwoApprovalListResponseVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLevelTwoApprovalList3() throws SQLException {
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.levelTwoApprovalList(LevelTwoApprovalServiceImpl.java:93)
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.levelTwoApprovalList(LevelTwoApprovalServiceImpl.java:93)
        //   See https://diff.blue/R013 to resolve this issue.

        LevelTwoApprovalServiceImpl levelTwoApprovalServiceImpl = new LevelTwoApprovalServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
       //CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        CustomResponseWrapper response1 = mock(CustomResponseWrapper.class);

        doNothing().when(response1).setStatus(Mockito.anyInt());

        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getUserID()).thenReturn("User ID");
        LevelTwoApprovalListResponseVO response = mock(LevelTwoApprovalListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setNetworkCode(Mockito.<String>any());
        doNothing().when(response).setUserID(Mockito.<String>any());
        levelTwoApprovalServiceImpl.levelTwoApprovalList(headers, response1, JUnitConfig.getConnection(), null, userVO, response);
    }

    /**
     * Method under test: {@link LevelTwoApprovalServiceImpl#displayStockLevelTwo(MultiValueMap, HttpServletResponse, Connection, Locale, UserVO, DisplayStockLevelTwoResponseVO, DisplayStockVO, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testDisplayStockLevelTwo() throws SQLException {
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.displayStockLevelTwo(LevelTwoApprovalServiceImpl.java:264)
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.displayStockLevelTwo(LevelTwoApprovalServiceImpl.java:264)
        //   See https://diff.blue/R013 to resolve this issue.

        LevelTwoApprovalServiceImpl levelTwoApprovalServiceImpl = new LevelTwoApprovalServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
       //CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        CustomResponseWrapper response1 = mock(CustomResponseWrapper.class);

        doNothing().when(response1).setStatus(Mockito.anyInt());

        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        DisplayStockLevelTwoResponseVO response = mock(DisplayStockLevelTwoResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        levelTwoApprovalServiceImpl.displayStockLevelTwo(headers, response1, JUnitConfig.getConnection(), locale, userVO, response,
                mock(DisplayStockVO.class), "Txn No");
    }

    /**
     * Method under test: {@link LevelTwoApprovalServiceImpl#displayStockLevelTwo(MultiValueMap, HttpServletResponse, Connection, Locale, UserVO, DisplayStockLevelTwoResponseVO, DisplayStockVO, String)}
     */
    @Test
    public void testDisplayStockLevelTwo2() throws SQLException {
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.displayStockLevelTwo(LevelTwoApprovalServiceImpl.java:264)
        //   See https://diff.blue/R013 to resolve this issue.

        LevelTwoApprovalServiceImpl levelTwoApprovalServiceImpl = new LevelTwoApprovalServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
        //CustomResponseWrapper response1 = new CustomResponseWrapper(new MockHttpServletResponse());
        CustomResponseWrapper response1 = mock(CustomResponseWrapper.class);

        doNothing().when(response1).setStatus(Mockito.anyInt());

        /*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        DisplayStockLevelTwoResponseVO response = mock(DisplayStockLevelTwoResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        assertSame(response, levelTwoApprovalServiceImpl.displayStockLevelTwo(headers, response1, JUnitConfig.getConnection(), locale, userVO,
                response, mock(DisplayStockVO.class), "Txn No"));
    /*    verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet).close();
    */    verify(userVO).getNetworkID();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        assertEquals(400, ((MockHttpServletResponse) response1.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link LevelTwoApprovalServiceImpl#displayStockLevelTwo(MultiValueMap, HttpServletResponse, Connection, Locale, UserVO, DisplayStockLevelTwoResponseVO, DisplayStockVO, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testDisplayStockLevelTwo3() throws SQLException {
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.displayStockLevelTwo(LevelTwoApprovalServiceImpl.java:264)
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
        //       at com.restapi.networkadmin.service.LevelTwoApprovalServiceImpl.displayStockLevelTwo(LevelTwoApprovalServiceImpl.java:264)
        //   See https://diff.blue/R013 to resolve this issue.

        LevelTwoApprovalServiceImpl levelTwoApprovalServiceImpl = new LevelTwoApprovalServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
//       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        CustomResponseWrapper response1 = mock(CustomResponseWrapper.class);

        doNothing().when(response1).setStatus(Mockito.anyInt());


/*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        DisplayStockLevelTwoResponseVO response = mock(DisplayStockLevelTwoResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        levelTwoApprovalServiceImpl.displayStockLevelTwo(headers, response1, JUnitConfig.getConnection(), null, userVO, response,
                mock(DisplayStockVO.class), "Txn No");
    }

    /**
     * Method under test: {@link LevelTwoApprovalServiceImpl#approvaLevelTwoStockTxn(MultiValueMap, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, BaseResponse, ApprovaLevelTwoStockTxnRequestVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testApprovaLevelTwoStockTxn() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        MultiValueMap<String, String> headers = null;
        HttpServletResponse response1 = null;
        //Connection con = null;
        //MComConnectionI mcomCon = mock(MComConnectionI.class);

        //JUnitConfig.init();

     try {
        // when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
     }catch(Exception e){}

        Locale locale = null;
        UserVO userVO = JUnitConfig.getUserVO();;
        BaseResponse response = null;
        ApprovaLevelTwoStockTxnRequestVO approvaLevelTwoStockTxnRequestVO = null;
//jdk21
        // Act
        BaseResponse actualApprovaLevelTwoStockTxnResult = this.levelTwoApprovalServiceImpl.approvaLevelTwoStockTxn(
                headers, response1, JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, userVO, null, approvaLevelTwoStockTxnRequestVO);

        // Assert
        // TODO: Add assertions on result
    }
}

