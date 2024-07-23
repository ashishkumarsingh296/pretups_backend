package com.restapi.networkadmin.commissionprofile.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.commissionProfileMainResponseVO.LoadVersionListBasedOnDateResponseVO;
import com.restapi.networkadmin.commissionprofile.requestVO.AddCommissionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.ChangeStatusForCommissionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.LoadVersionListBasedOnDateRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.ModifyCommissionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.SusResCommProfileSetRequestVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

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

@ContextConfiguration(classes = {CommissionProfileServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CommissionProfileServiceImplTest {
    @Autowired
    private CommissionProfileServiceImpl commissionProfileServiceImpl;

    /**
     * Method under test: {@link CommissionProfileServiceImpl#addCommissionProfile(MultiValueMap, HttpServletRequest, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, BaseResponse, AddCommissionProfileRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddCommissionProfile() throws Exception {
       com.btsl.util.JUnitConfig.init();
        MultiValueMap<String, String> headers = null;
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse response1 = null;
        //Connection con = null;
       // MComConnectionI mcomCon = null;
        Locale locale = null;
        UserVO userVO = JUnitConfig.getUserVO();;
        BaseResponse response = null;
        AddCommissionProfileRequestVO addCommissionProfileRequestVO = null;

        // Act
        BaseResponse actualAddCommissionProfileResult = this.commissionProfileServiceImpl.addCommissionProfile(headers,
                httpServletRequest, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), locale, userVO, response, addCommissionProfileRequestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#modifyCommissionProfile(MultiValueMap, HttpServletRequest, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, BaseResponse, ModifyCommissionProfileRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testModifyCommissionProfile() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        MultiValueMap<String, String> headers = null;
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse response1 = null;
        //Connection con = null;
        com.btsl.util.JUnitConfig.init();

       // MComConnectionI mcomCon = null;
        Locale locale = null;
        UserVO userVO = JUnitConfig.getUserVO();;
        BaseResponse response = null;
        ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO = null;

        // Act
        BaseResponse actualModifyCommissionProfileResult = this.commissionProfileServiceImpl.modifyCommissionProfile(
                headers, httpServletRequest, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), locale, userVO, response,
                modifyCommissionProfileRequestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#deleteCommissionProfileSet(MultiValueMap, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, BaseResponse, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteCommissionProfileSet() throws SQLException {
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
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.deleteCommissionProfileSet(CommissionProfileServiceImpl.java:1145)
        //   See https://diff.blue/R013 to resolve this issue.

        CommissionProfileServiceImpl commissionProfileServiceImpl = new CommissionProfileServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        */
        com.btsl.util.JUnitConfig.init();

        Locale locale = Locale.getDefault();
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        commissionProfileServiceImpl.deleteCommissionProfileSet(headers, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), locale, userVO,
                response, "Comm Profile Set ID", "Name");



    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#deleteCommissionProfileSet(MultiValueMap, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, BaseResponse, String)}
     */
    @Test
    public void testDeleteCommissionProfileSet2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        CommissionProfileServiceImpl commissionProfileServiceImpl = new CommissionProfileServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
        CustomResponseWrapper response1 = new CustomResponseWrapper(new MockHttpServletResponse());
      /*  ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
      */  Locale locale = Locale.getDefault();
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        assertSame(response, commissionProfileServiceImpl.deleteCommissionProfileSet(headers, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(),
                locale, userVO, response, "Comm Profile Set ID", "Name"));
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        assertEquals(400, ((MockHttpServletResponse) response1.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#deleteCommissionProfileSet(MultiValueMap, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, BaseResponse, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteCommissionProfileSet3() throws SQLException {
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
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.deleteCommissionProfileSet(CommissionProfileServiceImpl.java:1145)
        //   See https://diff.blue/R013 to resolve this issue.

        CommissionProfileServiceImpl commissionProfileServiceImpl = new CommissionProfileServiceImpl();
        MultiValueMap<String, String> headers = mock(MultiValueMap.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        */

        com.btsl.util.JUnitConfig.init();
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        commissionProfileServiceImpl.deleteCommissionProfileSet(headers, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), null, userVO, response,
                "Comm Profile Set ID", "Name");
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#suspendResumeCommissionProfileSet(MultiValueMap, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, BaseResponse, SusResCommProfileSetRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testSuspendResumeCommissionProfileSet() {
       com.btsl.util.JUnitConfig.init();
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
//        MComConnectionI mcomCon = null;
        com.btsl.util.JUnitConfig.init();

        Locale locale = null;
        UserVO userVO = JUnitConfig.getUserVO();;
        BaseResponse response = null;
        SusResCommProfileSetRequestVO susResCommProfileSetRequestVO = null;

        // Act
        BaseResponse actualSuspendResumeCommissionProfileSetResult = this.commissionProfileServiceImpl
                .suspendResumeCommissionProfileSet(headers, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), locale, userVO, response,
                        susResCommProfileSetRequestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#loadVersionListBasedOnDate(MultiValueMap, HttpServletResponse, Connection, MComConnectionI, Locale, UserVO, LoadVersionListBasedOnDateResponseVO, LoadVersionListBasedOnDateRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadVersionListBasedOnDate() {
       com.btsl.util.JUnitConfig.init();
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
        //MComConnectionI mcomCon = null;
        com.btsl.util.JUnitConfig.init();

        Locale locale = null;
        UserVO userVO = JUnitConfig.getUserVO();;
        LoadVersionListBasedOnDateResponseVO response = null;
        LoadVersionListBasedOnDateRequestVO loadVersionListBasedOnDateRequestVO = null;

        // Act
        LoadVersionListBasedOnDateResponseVO actualLoadVersionListBasedOnDateResult = this.commissionProfileServiceImpl
                .loadVersionListBasedOnDate(headers, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), locale, userVO, response,
                        loadVersionListBasedOnDateRequestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#viewGeoGradeList(Connection, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testViewGeoGradeList() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.viewGeoGradeList(CommissionProfileServiceImpl.java:1347)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        commissionProfileServiceImpl.viewGeoGradeList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", "Category Code",
                response1);
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#viewList(Connection, String, String, String, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testViewList() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.viewList(CommissionProfileServiceImpl.java:1414)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        commissionProfileServiceImpl.viewList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", "Category Code", "Grade Code", "Geo Code", "Status",
                response1);
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#viewProductList(Connection, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testViewProductList() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.viewProductList(CommissionProfileServiceImpl.java:1473)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        commissionProfileServiceImpl.viewProductList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1);
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#viewSubServiceList(Connection, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewSubServiceList() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.viewSubServiceList(CommissionProfileServiceImpl.java:1584)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        commissionProfileServiceImpl.viewSubServiceList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", "Service Code",
                response1);
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#chnageStatusForCommissionProfile(String, String, HttpServletResponse, Connection, MComConnectionI, Locale, BaseResponse, ChangeStatusForCommissionProfileRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testChnageStatusForCommissionProfile() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        String categorCode = "";
        String loginUserID = "";
        HttpServletResponse response1 = null;
        //Connection con = null;
        //MComConnectionI mcomCon = null;
        com.btsl.util.JUnitConfig.init();

        Locale locale = null;
        BaseResponse response = null;
        ChangeStatusForCommissionProfileRequestVO requestVO = null;

        // Act
        BaseResponse actualChnageStatusForCommissionProfileResult = this.commissionProfileServiceImpl
                .chnageStatusForCommissionProfile(categorCode, loginUserID, response1, com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), locale, response,
                        requestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#viewCommissionProfileDetails(Connection, Locale, String, String, String, String, String, String, String, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testViewCommissionProfileDetails() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.viewCommissionProfileDetails(CommissionProfileServiceImpl.java:1826)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        commissionProfileServiceImpl.viewCommissionProfileDetails(com.btsl.util.JUnitConfig.getConnection(), locale, "Login ID", "Domain Code",
                "Commission Type", "Category Code", "42", "Grade Code", "Grph Domain Code", "Network Code", "42",
                response1);
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#viewCommissionProfileDetails(Connection, Locale, String, String, String, String, String, String, String, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testViewCommissionProfileDetails2() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.networkadmin.commissionprofile.service.CommissionProfileServiceImpl.viewCommissionProfileDetails(CommissionProfileServiceImpl.java:1826)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        commissionProfileServiceImpl.viewCommissionProfileDetails(JUnitConfig.getConnection(), locale, "Login ID", "Domain Code",
                "Commission Type", "Category Code", "42", "Grade Code", "Grph Domain Code", "Network Code", "42",
                response1);
    }
}

