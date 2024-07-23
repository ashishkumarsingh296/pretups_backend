package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.profile.businesslogic.ServiceTypeobjVO;
import com.btsl.pretups.channel.transfer.businesslogic.AddServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceKeywordListResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceTypeListResp;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.ServiceKeywordResp;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordVO;
import com.btsl.user.businesslogic.UserVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import com.btsl.util.JUnitConfig;
import org.junit.Ignore;
import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ServiceKeywordServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceKeywordServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ServiceKeywordServiceImpl serviceKeywordServiceImpl;

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#getServiceTypeList(Connection)}
     */
    @Test
    public void testGetServiceTypeList() throws BTSLBaseException, SQLException {
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();*/
        //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        com.btsl.util.JUnitConfig.init();
        GetServiceTypeListResp actualServiceTypeList = serviceKeywordServiceImpl.getServiceTypeList(com.btsl.util.JUnitConfig.getConnection());
        assertTrue(actualServiceTypeList.getSuccessList().isEmpty());
        List<ServiceTypeobjVO> listServiceListObj = actualServiceTypeList.getListServiceListObj();
     //   assertEquals(2, listServiceListObj.size());
        ServiceTypeobjVO getResult = listServiceListObj.get(0);
        assertEquals("N", getResult.getSubKeyWordApplicable());
        ServiceTypeobjVO getResult2 = listServiceListObj.get(1);
        assertEquals("N", getResult2.getSubKeyWordApplicable());
        assertEquals("String", getResult2.getServiceTypeName());
        assertEquals("String", getResult2.getServiceType());
        assertEquals("String", getResult2.getRequest_param());
        assertEquals("String", getResult2.getModule());
        assertEquals("String", getResult.getServiceTypeName());
        assertEquals("String", getResult.getServiceType());
        assertEquals("String", getResult.getRequest_param());
        assertEquals("String", getResult.getModule());
     /*   verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#getServiceTypeList(Connection)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetServiceTypeList2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.loadServiceTypeListData(ServiceKeywordDAO.java:108)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.getServiceTypeList(ServiceKeywordServiceImpl.java:52)
        //   java.sql.SQLException
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.loadServiceTypeListData(ServiceKeywordDAO.java:93)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.getServiceTypeList(ServiceKeywordServiceImpl.java:52)
        //   See https://diff.blue/R013 to resolve this issue.

/*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
*/
        JUnitConfig.init();
        serviceKeywordServiceImpl.getServiceTypeList(JUnitConfig.getConnection());
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#getServiceTypeList(Connection)}
     */
    @Test
    public void testGetServiceTypeList3() throws BTSLBaseException, SQLException {
      /*  ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      */
        JUnitConfig.init();
        GetServiceTypeListResp actualServiceTypeList = serviceKeywordServiceImpl.getServiceTypeList(JUnitConfig.getConnection());
        assertTrue(actualServiceTypeList.getSuccessList().isEmpty());
        List<ServiceTypeobjVO> listServiceListObj = actualServiceTypeList.getListServiceListObj();
//        assertEquals(2, listServiceListObj.size());
        ServiceTypeobjVO getResult = listServiceListObj.get(0);
        assertEquals("N", getResult.getSubKeyWordApplicable());
        ServiceTypeobjVO getResult2 = listServiceListObj.get(1);
        assertEquals("N", getResult2.getSubKeyWordApplicable());
   /*     assertNull(getResult2.getServiceTypeName());
        assertNull(getResult2.getServiceType());
        assertNull(getResult2.getRequest_param());
        assertNull(getResult2.getModule());
        assertNull(getResult.getServiceTypeName());
        assertNull(getResult.getServiceType());
        assertNull(getResult.getRequest_param());
        assertNull(getResult.getModule());*/
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
*/
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#getServiceTypeList(Connection)}
     */
    @Test
    public void testGetServiceTypeList4() throws BTSLBaseException, SQLException {
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();*/
        //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       // thrown.expect(BTSLBaseException.class);
        com.btsl.util.JUnitConfig.init();
        serviceKeywordServiceImpl.getServiceTypeList(com.btsl.util.JUnitConfig.getConnection());
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#searchServiceKeywordbyServiceType(Connection, String)}
     */
    @Test
    public void testSearchServiceKeywordbyServiceType() throws BTSLBaseException, SQLException {
        /*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();*/
        //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        com.btsl.util.JUnitConfig.init();


        GetServiceKeywordListResp actualSearchServiceKeywordbyServiceTypeResult = serviceKeywordServiceImpl
                .searchServiceKeywordbyServiceType(com.btsl.util.JUnitConfig.getConnection(), "Input Service Type");
        assertTrue(actualSearchServiceKeywordbyServiceTypeResult.getSuccessList().isEmpty());
//        assertEquals(2, actualSearchServiceKeywordbyServiceTypeResult.getListServiceListObj().size());
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp, atLeast(1)).getTime();
*/
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#searchServiceKeywordbyServiceType(Connection, String)}
     */
    @Test
    public void testSearchServiceKeywordbyServiceType2() throws BTSLBaseException, SQLException {
      /*  ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
      */
        JUnitConfig.init();
        serviceKeywordServiceImpl.searchServiceKeywordbyServiceType(JUnitConfig.getConnection(), "Input Service Type");
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#addServiceKeyword(Connection, AddServiceKeywordReq, UserVO, Locale)}
     */
    @Test
    public void testAddServiceKeyword() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.getServiceTypeDetails(ServiceKeywordDAO.java:1901)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.addServiceKeyword(ServiceKeywordServiceImpl.java:96)
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.getServiceTypeDetails(ServiceKeywordDAO.java:1881)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.addServiceKeyword(ServiceKeywordServiceImpl.java:96)
        //   See https://diff.blue/R013 to resolve this issue.
        com.btsl.util.JUnitConfig.init();

        ServiceKeywordServiceImpl serviceKeywordServiceImpl = new ServiceKeywordServiceImpl();
      /*  ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      */  AddServiceKeywordReq addServiceKeywordReq = mock(AddServiceKeywordReq.class);
        when(addServiceKeywordReq.getAllowedVersion()).thenReturn("1.0.2");
        when(addServiceKeywordReq.getGatewayRequestParameter()).thenReturn("Gateway Request Parameter");
        when(addServiceKeywordReq.getKeyword()).thenReturn("Keyword");
        when(addServiceKeywordReq.getKeywordModifyAllow()).thenReturn("Keyword Modify Allow");
        when(addServiceKeywordReq.getMenu()).thenReturn("Menu");
        when(addServiceKeywordReq.getMessageGatewayType()).thenReturn("Message Gateway Type");
        when(addServiceKeywordReq.getName()).thenReturn("Name");
        when(addServiceKeywordReq.getReceivePort()).thenReturn("Receive Port");
        when(addServiceKeywordReq.getServiceType()).thenReturn("Service Type");
        when(addServiceKeywordReq.getStatus()).thenReturn("Status");
        when(addServiceKeywordReq.getSubKeyWord()).thenReturn("Sub Key Word");
        when(addServiceKeywordReq.getSubmenu()).thenReturn("Submenu");
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        thrown.expect(BTSLBaseException.class);
        serviceKeywordServiceImpl.addServiceKeyword(com.btsl.util.JUnitConfig.getConnection(), addServiceKeywordReq, userVO, Locale.getDefault());
        verify(com.btsl.util.JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
      /*  verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();*/
        verify(addServiceKeywordReq, atLeast(1)).getAllowedVersion();
        verify(addServiceKeywordReq).getGatewayRequestParameter();
        verify(addServiceKeywordReq, atLeast(1)).getKeyword();
        verify(addServiceKeywordReq).getKeywordModifyAllow();
        verify(addServiceKeywordReq, atLeast(1)).getMenu();
        verify(addServiceKeywordReq).getMessageGatewayType();
        verify(addServiceKeywordReq, atLeast(1)).getName();
        verify(addServiceKeywordReq).getReceivePort();
        verify(addServiceKeywordReq, atLeast(1)).getServiceType();
        verify(addServiceKeywordReq).getStatus();
        verify(addServiceKeywordReq, atLeast(1)).getSubKeyWord();
        verify(addServiceKeywordReq, atLeast(1)).getSubmenu();
        verify(userVO).getUserID();
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#addServiceKeyword(Connection, AddServiceKeywordReq, UserVO, Locale)}
     */
    @Test
    public void testAddServiceKeyword2() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.getServiceTypeDetails(ServiceKeywordDAO.java:1901)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.addServiceKeyword(ServiceKeywordServiceImpl.java:96)
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.getServiceTypeDetails(ServiceKeywordDAO.java:1881)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.addServiceKeyword(ServiceKeywordServiceImpl.java:96)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceKeywordServiceImpl serviceKeywordServiceImpl = new ServiceKeywordServiceImpl();
       /* Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       */ //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        com.btsl.util.JUnitConfig.init();
        AddServiceKeywordReq addServiceKeywordReq = mock(AddServiceKeywordReq.class);
        when(addServiceKeywordReq.getAllowedVersion()).thenReturn("1.0.2");
        when(addServiceKeywordReq.getGatewayRequestParameter()).thenReturn("Gateway Request Parameter");
        when(addServiceKeywordReq.getKeyword()).thenReturn("Keyword");
        when(addServiceKeywordReq.getKeywordModifyAllow()).thenReturn("Keyword Modify Allow");
        when(addServiceKeywordReq.getMenu()).thenReturn("Menu");
        when(addServiceKeywordReq.getMessageGatewayType()).thenReturn("Message Gateway Type");
        when(addServiceKeywordReq.getName()).thenReturn("Name");
        when(addServiceKeywordReq.getReceivePort()).thenReturn("Receive Port");
        when(addServiceKeywordReq.getServiceType()).thenReturn("Service Type");
        when(addServiceKeywordReq.getStatus()).thenReturn("Status");
        when(addServiceKeywordReq.getSubKeyWord()).thenReturn("Sub Key Word");
        when(addServiceKeywordReq.getSubmenu()).thenReturn("Submenu");
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        thrown.expect(BTSLBaseException.class);
        serviceKeywordServiceImpl.addServiceKeyword(com.btsl.util.JUnitConfig.getConnection(), addServiceKeywordReq, userVO, Locale.getDefault());
      /*  verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();*/
        verify(addServiceKeywordReq, atLeast(1)).getAllowedVersion();
        verify(addServiceKeywordReq).getGatewayRequestParameter();
        verify(addServiceKeywordReq, atLeast(1)).getKeyword();
        verify(addServiceKeywordReq).getKeywordModifyAllow();
        verify(addServiceKeywordReq, atLeast(1)).getMenu();
        verify(addServiceKeywordReq, atLeast(1)).getMessageGatewayType();
        verify(addServiceKeywordReq, atLeast(1)).getName();
        verify(addServiceKeywordReq).getReceivePort();
        verify(addServiceKeywordReq, atLeast(1)).getServiceType();
        verify(addServiceKeywordReq).getStatus();
        verify(addServiceKeywordReq, atLeast(1)).getSubKeyWord();
        verify(addServiceKeywordReq, atLeast(1)).getSubmenu();
        verify(userVO, atLeast(1)).getUserID();
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#searchServiceKeywordbyID(Connection, String)}
     */
    @Test
    public void testSearchServiceKeywordbyID() throws BTSLBaseException, SQLException {
        /*Timestamp timestamp = mock(Timestamp.class);
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
        *///Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        com.btsl.util.JUnitConfig.init();
        ServiceKeywordResp actualSearchServiceKeywordbyIDResult = serviceKeywordServiceImpl.searchServiceKeywordbyID(com.btsl.util.JUnitConfig.getConnection(),
                "Servicekeyword ID");
        assertTrue(actualSearchServiceKeywordbyIDResult.getSuccessList().isEmpty());
        ServiceKeywordVO serviceKeywordVO = actualSearchServiceKeywordbyIDResult.getServiceKeywordVO();
        assertEquals("String", serviceKeywordVO.getStatus());
        assertEquals("String", serviceKeywordVO.getServiceType());
        assertEquals("String", serviceKeywordVO.getServiceRequestParam());
        assertEquals("String", serviceKeywordVO.getServiceKeywordID());
        assertNull(serviceKeywordVO.getResponseCode());
        assertEquals("String", serviceKeywordVO.getReceivePort());
        assertEquals("String", serviceKeywordVO.getName());
        assertEquals("String", serviceKeywordVO.getModuleDesc());
        assertEquals("String", serviceKeywordVO.getModifyAllowed());
        assertNull(serviceKeywordVO.getModifiedOn());
        assertNull(serviceKeywordVO.getModifiedBy());
        assertEquals("String", serviceKeywordVO.getMenu());
   //     assertEquals(10L, serviceKeywordVO.getLastModifiedTime());
        assertEquals("String", serviceKeywordVO.getKeyword());
        assertEquals("String", serviceKeywordVO.getInterface());
        assertEquals("String", serviceKeywordVO.getGatewayRequestParam());
        assertNull(serviceKeywordVO.getCreatedOn());
        assertNull(serviceKeywordVO.getCreatedBy());
        assertEquals("String", serviceKeywordVO.getAllowedVersion());
        assertFalse(serviceKeywordVO.isSubKeywordApplicable());
        assertEquals("String", serviceKeywordVO.getSubKeyword());
        assertEquals("String", serviceKeywordVO.getSubMenu());
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp, atLeast(1)).getTime();
*/
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#searchServiceKeywordbyID(Connection, String)}
     */
    @Test
    public void testSearchServiceKeywordbyID2() throws BTSLBaseException, SQLException {
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        //thrown.expect(BTSLBaseException.class);
        com.btsl.util.JUnitConfig.init();

        serviceKeywordServiceImpl.searchServiceKeywordbyID(com.btsl.util.JUnitConfig.getConnection(), "Servicekeyword ID");
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#modifyServiceKeyword(Connection, ModifyServiceKeywordReq, UserVO, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testModifyServiceKeyword() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.getServiceTypeDetails(ServiceKeywordDAO.java:1901)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.modifyServiceKeyword(ServiceKeywordServiceImpl.java:398)
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.getServiceTypeDetails(ServiceKeywordDAO.java:1881)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.modifyServiceKeyword(ServiceKeywordServiceImpl.java:398)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        JUnitConfig.init();

        ModifyServiceKeywordReq modifyServiceKeywordReq = new ModifyServiceKeywordReq();
        modifyServiceKeywordReq.setServiceKeywordID("TEST");
        modifyServiceKeywordReq.setKeyword("TESt");
        modifyServiceKeywordReq.setServiceType("String");
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getCategoryCode()).thenReturn("Category Code");
        when(userVO.getLoginID()).thenReturn("Login ID");
        when(userVO.getMsisdn()).thenReturn("Msisdn");
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getUserID()).thenReturn("User ID");


        Locale locale = null;

        // Act
        ModifyServiceKeywordResp actualModifyServiceKeywordResult = this.serviceKeywordServiceImpl
                .modifyServiceKeyword(JUnitConfig.getConnection(), modifyServiceKeywordReq, userVO, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#deleteServiceKeywordbyID(Connection, String, UserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteServiceKeywordbyID() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.fetchServicekeywordByID(ServiceKeywordDAO.java:1761)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.searchServiceKeywordbyID(ServiceKeywordServiceImpl.java:363)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.deleteServiceKeywordbyID(ServiceKeywordServiceImpl.java:531)
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.fetchServicekeywordByID(ServiceKeywordDAO.java:1721)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.searchServiceKeywordbyID(ServiceKeywordServiceImpl.java:363)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.deleteServiceKeywordbyID(ServiceKeywordServiceImpl.java:531)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.deleteServiceKeywordbyID(ServiceKeywordServiceImpl.java:565)
        //   See https://diff.blue/R013 to resolve this issue.

/*
        ServiceKeywordServiceImpl serviceKeywordServiceImpl = new ServiceKeywordServiceImpl();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        doNothing().when(con).commit();
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
*/
        JUnitConfig.init();
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getCategoryCode()).thenReturn("Category Code");
        when(userVO.getLoginID()).thenReturn("Login ID");
        when(userVO.getMsisdn()).thenReturn("Msisdn");
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getUserID()).thenReturn("User ID");
        serviceKeywordServiceImpl.deleteServiceKeywordbyID(JUnitConfig.getConnection(), "Servicekeyword ID", userVO);
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#deleteServiceKeywordbyID(Connection, String, UserVO)}
     */
    @Test
    public void testDeleteServiceKeywordbyID2() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.fetchServicekeywordByID(ServiceKeywordDAO.java:1761)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.searchServiceKeywordbyID(ServiceKeywordServiceImpl.java:363)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.deleteServiceKeywordbyID(ServiceKeywordServiceImpl.java:531)
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.fetchServicekeywordByID(ServiceKeywordDAO.java:1721)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.searchServiceKeywordbyID(ServiceKeywordServiceImpl.java:363)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.deleteServiceKeywordbyID(ServiceKeywordServiceImpl.java:531)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceKeywordServiceImpl serviceKeywordServiceImpl = new ServiceKeywordServiceImpl();
      /*  Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
      */  //Connection con = mock(Connection.class);
    //    when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        com.btsl.util.JUnitConfig.init();

        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getCategoryCode()).thenReturn("Category Code");
        when(userVO.getLoginID()).thenReturn("Login ID");
        when(userVO.getMsisdn()).thenReturn("Msisdn");
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getUserID()).thenReturn("User ID");
        thrown.expect(BTSLBaseException.class);
        serviceKeywordServiceImpl.deleteServiceKeywordbyID(com.btsl.util.JUnitConfig.getConnection(), "Servicekeyword ID", userVO);
        /*verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp, atLeast(1)).getTime();
        */verify(userVO).getCategoryCode();
        verify(userVO).getLoginID();
        verify(userVO).getMsisdn();
        verify(userVO).getNetworkID();
        verify(userVO, atLeast(1)).getUserID();
    }

    /**
     * Method under test: {@link ServiceKeywordServiceImpl#deleteServiceKeywordbyID(Connection, String, UserVO)}
     */
    @Test
    public void testDeleteServiceKeywordbyID3() throws BTSLBaseException, SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.fetchServicekeywordByID(ServiceKeywordDAO.java:1761)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.searchServiceKeywordbyID(ServiceKeywordServiceImpl.java:363)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.deleteServiceKeywordbyID(ServiceKeywordServiceImpl.java:531)
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO.fetchServicekeywordByID(ServiceKeywordDAO.java:1721)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.searchServiceKeywordbyID(ServiceKeywordServiceImpl.java:363)
        //       at com.restapi.c2s.services.ServiceKeywordServiceImpl.deleteServiceKeywordbyID(ServiceKeywordServiceImpl.java:531)
        //   See https://diff.blue/R013 to resolve this issue.

        ServiceKeywordServiceImpl serviceKeywordServiceImpl = new ServiceKeywordServiceImpl();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getUserID()).thenReturn("User ID");
        thrown.expect(BTSLBaseException.class);
        serviceKeywordServiceImpl.deleteServiceKeywordbyID(com.btsl.util.JUnitConfig.getConnection(), "Servicekeyword ID", userVO);
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
        */verify(userVO).getUserID();
    }
}

