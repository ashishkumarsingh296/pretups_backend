package com.restapi.networkadmin.service;

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
import com.restapi.networkadmin.requestVO.UpdateNetworkPreferenceReqVO;
import com.restapi.networkadmin.requestVO.UpdateNetworkPreferenceVO;
import com.restapi.networkadmin.responseVO.NetworkPreferenceListResponseVO;

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

public class NetworkPreferenceServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#loadNetworkPreferenceList(Connection, Locale, HttpServletResponse, UserVO, NetworkPreferenceListResponseVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadNetworkPreferenceList() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.NetworkPreferenceServiceImpl.loadNetworkPreferenceList(NetworkPreferenceServiceImpl.java:89)
        //   See https://diff.blue/R013 to resolve this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
      /*  Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
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
        NetworkPreferenceListResponseVO response = mock(NetworkPreferenceListResponseVO.class);
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setModule(Mockito.<String>any());
        doNothing().when(response).setPreferenceList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setPreferenceType(Mockito.<String>any());
        networkPreferenceServiceImpl.loadNetworkPreferenceList(JUnitConfig.getConnection(), locale, response1, userVO, response);
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#loadNetworkPreferenceList(Connection, Locale, HttpServletResponse, UserVO, NetworkPreferenceListResponseVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadNetworkPreferenceList2() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.NetworkPreferenceServiceImpl.loadNetworkPreferenceList(NetworkPreferenceServiceImpl.java:89)
        //   See https://diff.blue/R013 to resolve this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
       /* Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("ALL");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
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
        NetworkPreferenceListResponseVO response = mock(NetworkPreferenceListResponseVO.class);
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setModule(Mockito.<String>any());
        doNothing().when(response).setPreferenceList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setPreferenceType(Mockito.<String>any());
        networkPreferenceServiceImpl.loadNetworkPreferenceList(JUnitConfig.getConnection(), locale, response1, userVO, response);
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#loadNetworkPreferenceList(Connection, Locale, HttpServletResponse, UserVO, NetworkPreferenceListResponseVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadNetworkPreferenceList3() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.NetworkPreferenceServiceImpl.loadNetworkPreferenceList(NetworkPreferenceServiceImpl.java:89)
        //   See https://diff.blue/R013 to resolve this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
/*
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
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
        NetworkPreferenceListResponseVO response = mock(NetworkPreferenceListResponseVO.class);
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setModule(Mockito.<String>any());
        doNothing().when(response).setPreferenceList(Mockito.<ArrayList<Object>>any());
        doNothing().when(response).setPreferenceType(Mockito.<String>any());
        networkPreferenceServiceImpl.loadNetworkPreferenceList(JUnitConfig.getConnection(), locale, response1, userVO, response);
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
        //Connection con = mock(Connection.class);
        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());

        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testUpdateNetworkPreferenceByList2() throws Exception {
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
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.updateNetworkPreference(PreferenceWebDAO.java:680)
        //       at com.restapi.networkadmin.service.NetworkPreferenceServiceImpl.updateNetworkPreferenceByList(NetworkPreferenceServiceImpl.java:185)
        //   See https://diff.blue/R013 to resolve this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
       /* Timestamp timestamp = mock(Timestamp.class);
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
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       */
        JUnitConfig.init();

        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());


        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = new UpdateNetworkPreferenceVO();
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList3() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
/*
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
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
*/

        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        doNothing().when(mcomCon).finalRollback();
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());


        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = new UpdateNetworkPreferenceVO();
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        /*verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
        */verify(mcomCon).finalRollback();
        verify(userVO).getUserID();
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList4() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
    /*    ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
    */

        JUnitConfig.init();

        MComConnectionI mcomCon = mock(MComConnectionI.class);
        doNothing().when(mcomCon).finalRollback();
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());

        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = new UpdateNetworkPreferenceVO();
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        /*verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
        */verify(mcomCon).finalRollback();
        verify(userVO).getUserID();
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testUpdateNetworkPreferenceByList5() throws Exception {
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
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.updateNetworkPreference(PreferenceWebDAO.java:678)
        //       at com.restapi.networkadmin.service.NetworkPreferenceServiceImpl.updateNetworkPreferenceByList(NetworkPreferenceServiceImpl.java:185)
        //   See https://diff.blue/R013 to resolve this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
 /*       Timestamp timestamp = mock(Timestamp.class);
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
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
 */
        
        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        doNothing().when(mcomCon).finalRollback();
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
        
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = new UpdateNetworkPreferenceVO();
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testUpdateNetworkPreferenceByList6() throws Exception {
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
        //       at com.web.pretups.preference.businesslogic.PreferenceWebDAO.updateNetworkPreference(PreferenceWebDAO.java:678)
        //       at com.restapi.networkadmin.service.NetworkPreferenceServiceImpl.updateNetworkPreferenceByList(NetworkPreferenceServiceImpl.java:185)
        //   See https://diff.blue/R013 to resolve this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
  /*      Timestamp timestamp = mock(Timestamp.class);
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
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
  */
        JUnitConfig.init();
        
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        doNothing().when(mcomCon).finalRollback();
        
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
        
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        BaseResponse response = mock(BaseResponse.class);

        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = new UpdateNetworkPreferenceVO();
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        UpdateNetworkPreferenceVO updateNetworkPreferenceVO2 = new UpdateNetworkPreferenceVO();
        updateNetworkPreferenceVO2.setAllowAction("DEFAULT_LANGUAGE");
        updateNetworkPreferenceVO2.setLastModifiedTime(0L);
        updateNetworkPreferenceVO2.setNetworkCode("DEFAULT_LANGUAGE");
        updateNetworkPreferenceVO2.setPreferenceCode("DEFAULT_LANGUAGE");
        updateNetworkPreferenceVO2.setPreferenceValue("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO2.setPreferenceValueType("updateNetworkPreferenceByList");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO2);
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList7() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
 //      //Connection con = mock(Connection.class);
        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
        
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = mock(UpdateNetworkPreferenceVO.class);
        doNothing().when(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        UpdateNetworkPreferenceVO updateNetworkPreferenceVO2 = new UpdateNetworkPreferenceVO();
        updateNetworkPreferenceVO2.setAllowAction("");
        updateNetworkPreferenceVO2.setLastModifiedTime(Long.MAX_VALUE);
        updateNetworkPreferenceVO2.setNetworkCode("");
        updateNetworkPreferenceVO2.setPreferenceCode("");
        updateNetworkPreferenceVO2.setPreferenceValue("");
        updateNetworkPreferenceVO2.setPreferenceValueType("");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO2);
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList8() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
      // //Connection con = mock(Connection.class);
        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
        
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = mock(UpdateNetworkPreferenceVO.class);
        when(updateNetworkPreferenceVO.getPreferenceValueType()).thenReturn("");
        when(updateNetworkPreferenceVO.getPreferenceValue()).thenReturn("42");
        when(updateNetworkPreferenceVO.getPreferenceCode()).thenReturn("Preference Code");
        when(updateNetworkPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateNetworkPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateNetworkPreferenceVO).getAllowAction();
        verify(updateNetworkPreferenceVO).getNetworkCode();
        verify(updateNetworkPreferenceVO).getPreferenceCode();
        verify(updateNetworkPreferenceVO).getPreferenceValue();
        verify(updateNetworkPreferenceVO).getPreferenceValueType();
        verify(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList9() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
        //Connection con = mock(Connection.class);
        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
        
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = mock(UpdateNetworkPreferenceVO.class);
        when(updateNetworkPreferenceVO.getPreferenceValue()).thenReturn("");
        when(updateNetworkPreferenceVO.getPreferenceCode()).thenReturn("Preference Code");
        when(updateNetworkPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateNetworkPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateNetworkPreferenceVO).getAllowAction();
        verify(updateNetworkPreferenceVO).getNetworkCode();
        verify(updateNetworkPreferenceVO).getPreferenceCode();
        verify(updateNetworkPreferenceVO).getPreferenceValue();
        verify(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList10() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
        //Connection con = mock(Connection.class);
        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
        
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = mock(UpdateNetworkPreferenceVO.class);
        when(updateNetworkPreferenceVO.getPreferenceCode()).thenReturn("");
        when(updateNetworkPreferenceVO.getNetworkCode()).thenReturn("Network Code");
        when(updateNetworkPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateNetworkPreferenceVO).getAllowAction();
        verify(updateNetworkPreferenceVO).getNetworkCode();
        verify(updateNetworkPreferenceVO).getPreferenceCode();
        verify(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link NetworkPreferenceServiceImpl#updateNetworkPreferenceByList(Connection, MComConnectionI, Locale, HttpServletResponse, UserVO, BaseResponse, UpdateNetworkPreferenceReqVO)}
     */
    @Test
    public void testUpdateNetworkPreferenceByList11() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        NetworkPreferenceServiceImpl networkPreferenceServiceImpl = new NetworkPreferenceServiceImpl();
        //Connection con = mock(Connection.class);
        JUnitConfig.init();
        MComConnectionI mcomCon = mock(MComConnectionI.class);
        when(mcomCon.getConnection()).thenReturn(JUnitConfig.getConnection());
        
        Locale locale = Locale.getDefault();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        BaseResponse response = mock(BaseResponse.class);
        UpdateNetworkPreferenceVO updateNetworkPreferenceVO = mock(UpdateNetworkPreferenceVO.class);
        when(updateNetworkPreferenceVO.getNetworkCode()).thenReturn("");
        when(updateNetworkPreferenceVO.getAllowAction()).thenReturn("Allow Action");
        doNothing().when(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        doNothing().when(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
        updateNetworkPreferenceVO.setAllowAction("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setLastModifiedTime(1L);
        updateNetworkPreferenceVO.setNetworkCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceCode("updateNetworkPreferenceByList");
        updateNetworkPreferenceVO.setPreferenceValue("42");
        updateNetworkPreferenceVO.setPreferenceValueType("42");

        ArrayList<UpdateNetworkPreferenceVO> updateNetworkPreferenceVOList = new ArrayList<>();
        updateNetworkPreferenceVOList.add(updateNetworkPreferenceVO);
        UpdateNetworkPreferenceReqVO requestVO = mock(UpdateNetworkPreferenceReqVO.class);
        when(requestVO.getPreferenceUpdateList()).thenReturn(updateNetworkPreferenceVOList);
        thrown.expect(BTSLBaseException.class);
        networkPreferenceServiceImpl.updateNetworkPreferenceByList(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, response1, userVO, response,
                requestVO);
        verify(requestVO, atLeast(1)).getPreferenceUpdateList();
        verify(updateNetworkPreferenceVO).getAllowAction();
        verify(updateNetworkPreferenceVO).getNetworkCode();
        verify(updateNetworkPreferenceVO).setAllowAction(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setLastModifiedTime(Mockito.<Long>any());
        verify(updateNetworkPreferenceVO).setNetworkCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceCode(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValue(Mockito.<String>any());
        verify(updateNetworkPreferenceVO).setPreferenceValueType(Mockito.<String>any());
    }
}

