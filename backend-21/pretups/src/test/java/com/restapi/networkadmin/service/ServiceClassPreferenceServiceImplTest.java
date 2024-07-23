package com.restapi.networkadmin.service;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.networkadmin.requestVO.UpdateServiceClassPreferenceReqVO;
import com.restapi.networkadmin.requestVO.UpdateServiceClassPreferenceVO;
import com.restapi.networkadmin.responseVO.ServiceClassListResponseVO;
import com.restapi.networkadmin.responseVO.ServiceClassPreferenceListResponseVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class ServiceClassPreferenceServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#loadServiceClassList(Connection, Locale, HttpServletResponse, UserVO, ServiceClassListResponseVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadServiceClassList() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.ServiceClassPreferenceServiceImpl.loadServiceClassList(ServiceClassPreferenceServiceImpl.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
      //JUnitConfig.getConnection()  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        ServiceClassListResponseVO response = mock(ServiceClassListResponseVO.class);
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setServiceClassList(Mockito.<ArrayList<Object>>any());
        serviceClassPreferenceServiceImpl.loadServiceClassList(JUnitConfig.getConnection(), locale, response1, userVO, response);
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#loadServiceClassPreferenceList(Connection, Locale, HttpServletResponse, UserVO, ServiceClassPreferenceListResponseVO, String)}
     */
    @Test
    public void testLoadServiceClassPreferenceList() throws SQLException {
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
        //       at com.restapi.networkadmin.service.ServiceClassPreferenceServiceImpl.loadServiceClassPreferenceList(ServiceClassPreferenceServiceImpl.java:156)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        ServiceClassPreferenceListResponseVO response = mock(ServiceClassPreferenceListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setPreferenceList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setServiceDescription(Mockito.<String>any());
        assertSame(response, serviceClassPreferenceServiceImpl.loadServiceClassPreferenceList(JUnitConfig.getConnection(), locale, response1,
                userVO, response, "Service Code"));
       // verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(userVO, atLeast(1)).getNetworkID();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setPreferenceList(Mockito.<ArrayList<Object>>any());
        verify(response).setServiceDescription(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#loadServiceClassPreferenceList(Connection, Locale, HttpServletResponse, UserVO, ServiceClassPreferenceListResponseVO, String)}
     */
    @Test
    public void testLoadServiceClassPreferenceList2() throws SQLException {
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
        //       at com.restapi.networkadmin.service.ServiceClassPreferenceServiceImpl.loadServiceClassPreferenceList(ServiceClassPreferenceServiceImpl.java:156)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        ServiceClassPreferenceListResponseVO response = mock(ServiceClassPreferenceListResponseVO.class);
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setPreferenceList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setServiceDescription(Mockito.<String>any());
        assertSame(response, serviceClassPreferenceServiceImpl.loadServiceClassPreferenceList(JUnitConfig.getConnection(), null, response1,
                userVO, response, "Service Code"));
      //  verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(userVO, atLeast(1)).getNetworkID();
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setMessageCode(Mockito.<String>any());
        verify(response).setStatus(anyInt());
        verify(response).setPreferenceList(Mockito.<ArrayList<Object>>any());
        verify(response).setServiceDescription(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList() throws Exception {

        JUnitConfig.init();
        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUpdateServiceClassPreferenceByList2() throws Exception {
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
        //   com.btsl.common.BTSLBaseException: error.modify.true
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.updateServiceClassPreference(PreferenceWebDAO.java:993)
        //       at com.restapi.networkadmin.service.ServiceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(ServiceClassPreferenceServiceImpl.java:264)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = new UpdateServiceClassPreferenceVO();
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList3() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        doNothing().when(mcomCon).finalRollback();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = new UpdateServiceClassPreferenceVO();
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
       // verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
        verify(mcomCon).finalRollback();
        verify(userVO).getUserID();
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList4() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        //MComConnectionI mcomCon = mock(MComConnectionI.class);
     //   doNothing().when(mcomCon).finalRollback();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = new UpdateServiceClassPreferenceVO();
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
      //  verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
        //verify(mcomCon).finalRollback();
        verify(userVO).getUserID();
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUpdateServiceClassPreferenceByList5() throws Exception {
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
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.isRecordModified(PreferenceWebDAO.java:436)
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.updateServiceClassPreference(PreferenceWebDAO.java:991)
        //       at com.restapi.networkadmin.service.ServiceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(ServiceClassPreferenceServiceImpl.java:264)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        //MComConnectionI mcomCon = mock(MComConnectionI.class);
       // doNothing().when(mcomCon).finalRollback();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = new UpdateServiceClassPreferenceVO();
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUpdateServiceClassPreferenceByList6() throws Exception {
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
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.isRecordModified(PreferenceWebDAO.java:436)
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.updateServiceClassPreference(PreferenceWebDAO.java:991)
        //       at com.restapi.networkadmin.service.ServiceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(ServiceClassPreferenceServiceImpl.java:264)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        doNothing().when(mcomCon).finalRollback();
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = new UpdateServiceClassPreferenceVO();
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO2 = new UpdateServiceClassPreferenceVO();
        updateServiceClassPreferenceVO2.setAllowAction("DEFAULT_LANGUAGE");
        updateServiceClassPreferenceVO2.setLastModifiedTime(0L);
        updateServiceClassPreferenceVO2.setModuleCode("DEFAULT_LANGUAGE");
        updateServiceClassPreferenceVO2.setNetworkCode("DEFAULT_LANGUAGE");
        updateServiceClassPreferenceVO2.setPreferenceCode("DEFAULT_LANGUAGE");
        updateServiceClassPreferenceVO2.setPreferenceValue("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO2.setPreferenceValueType("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO2.setServiceCode("DEFAULT_LANGUAGE");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO2);
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList7() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = mock(UpdateServiceClassPreferenceVO.class);
        doNothing().when(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO2 = new UpdateServiceClassPreferenceVO();
        updateServiceClassPreferenceVO2.setAllowAction("");
        updateServiceClassPreferenceVO2.setLastModifiedTime(Long.MAX_VALUE);
        updateServiceClassPreferenceVO2.setModuleCode("");
        updateServiceClassPreferenceVO2.setNetworkCode("");
        updateServiceClassPreferenceVO2.setPreferenceCode("");
        updateServiceClassPreferenceVO2.setPreferenceValue("");
        updateServiceClassPreferenceVO2.setPreferenceValueType("");
        updateServiceClassPreferenceVO2.setServiceCode("");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO2);
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList8() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = mock(UpdateServiceClassPreferenceVO.class);
        when(updateServiceClassPreferenceVO.getPreferenceValueType()).thenReturn("");
        when(updateServiceClassPreferenceVO.getPreferenceValue()).thenReturn("42");
        when(updateServiceClassPreferenceVO.getPreferenceCode()).thenReturn("Preference Code");
        when(updateServiceClassPreferenceVO.getModuleCode()).thenReturn("Module Code");
        when(updateServiceClassPreferenceVO.getServiceCode()).thenReturn("Service Code");
        when(updateServiceClassPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateServiceClassPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateServiceClassPreferenceVO).getAllowAction();
        verify(updateServiceClassPreferenceVO).getModuleCode();
        verify(updateServiceClassPreferenceVO).getNetworkCode();
        verify(updateServiceClassPreferenceVO).getPreferenceCode();
        verify(updateServiceClassPreferenceVO).getPreferenceValue();
        verify(updateServiceClassPreferenceVO).getPreferenceValueType();
        verify(updateServiceClassPreferenceVO).getServiceCode();
        verify(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList9() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = mock(UpdateServiceClassPreferenceVO.class);
        when(updateServiceClassPreferenceVO.getPreferenceValue()).thenReturn("");
        when(updateServiceClassPreferenceVO.getPreferenceCode()).thenReturn("Preference Code");
        when(updateServiceClassPreferenceVO.getModuleCode()).thenReturn("Module Code");
        when(updateServiceClassPreferenceVO.getServiceCode()).thenReturn("Service Code");
        when(updateServiceClassPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateServiceClassPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateServiceClassPreferenceVO).getAllowAction();
        verify(updateServiceClassPreferenceVO).getModuleCode();
        verify(updateServiceClassPreferenceVO).getNetworkCode();
        verify(updateServiceClassPreferenceVO).getPreferenceCode();
        verify(updateServiceClassPreferenceVO).getPreferenceValue();
        verify(updateServiceClassPreferenceVO).getServiceCode();
        verify(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList10() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = mock(UpdateServiceClassPreferenceVO.class);
        when(updateServiceClassPreferenceVO.getPreferenceCode()).thenReturn("");
        when(updateServiceClassPreferenceVO.getModuleCode()).thenReturn("Module Code");
        when(updateServiceClassPreferenceVO.getServiceCode()).thenReturn("Service Code");
        when(updateServiceClassPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateServiceClassPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateServiceClassPreferenceVO).getAllowAction();
        verify(updateServiceClassPreferenceVO).getModuleCode();
        verify(updateServiceClassPreferenceVO).getNetworkCode();
        verify(updateServiceClassPreferenceVO).getPreferenceCode();
        verify(updateServiceClassPreferenceVO).getServiceCode();
        verify(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList11() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = mock(UpdateServiceClassPreferenceVO.class);
        when(updateServiceClassPreferenceVO.getModuleCode()).thenReturn("");
        when(updateServiceClassPreferenceVO.getServiceCode()).thenReturn("Service Code");
        when(updateServiceClassPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateServiceClassPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateServiceClassPreferenceVO).getAllowAction();
        verify(updateServiceClassPreferenceVO).getModuleCode();
        verify(updateServiceClassPreferenceVO).getNetworkCode();
        verify(updateServiceClassPreferenceVO).getServiceCode();
        verify(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList12() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = mock(UpdateServiceClassPreferenceVO.class);
        when(updateServiceClassPreferenceVO.getServiceCode()).thenReturn("");
        when(updateServiceClassPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateServiceClassPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateServiceClassPreferenceVO).getAllowAction();
        verify(updateServiceClassPreferenceVO).getNetworkCode();
        verify(updateServiceClassPreferenceVO).getServiceCode();
        verify(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ServiceClassPreferenceServiceImpl#updateServiceClassPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateServiceClassPreferenceReqVO)}
     */
    @Test
    public void testUpdateServiceClassPreferenceByList13() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        ServiceClassPreferenceServiceImpl serviceClassPreferenceServiceImpl = new ServiceClassPreferenceServiceImpl();
       //Connection con = mock(Connection.class);
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateServiceClassPreferenceVO updateServiceClassPreferenceVO = mock(UpdateServiceClassPreferenceVO.class);
        when(updateServiceClassPreferenceVO.getNetworkCode()).thenReturn("");
        when(updateServiceClassPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        doNothing().when(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
        updateServiceClassPreferenceVO.setAllowAction("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setLastModifiedTime(1L);
        updateServiceClassPreferenceVO.setModuleCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setNetworkCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceCode("updateServiceClassPreferenceByList");
        updateServiceClassPreferenceVO.setPreferenceValue("42");
        updateServiceClassPreferenceVO.setPreferenceValueType("42");
        updateServiceClassPreferenceVO.setServiceCode("updateServiceClassPreferenceByList");

        ArrayList<UpdateServiceClassPreferenceVO> updateServiceClassPreferenceVOList = new ArrayList<>();
        updateServiceClassPreferenceVOList.add(updateServiceClassPreferenceVO);
        UpdateServiceClassPreferenceReqVO requestVO = mock(UpdateServiceClassPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateServiceClassPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        serviceClassPreferenceServiceImpl.updateServiceClassPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO,
                response, requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateServiceClassPreferenceVO).getAllowAction();
        verify(updateServiceClassPreferenceVO).getNetworkCode();
        verify(updateServiceClassPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateServiceClassPreferenceVO).setModuleCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        verify(updateServiceClassPreferenceVO).setServiceCode(Mockito.<String>any());
    }
}

