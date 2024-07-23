package com.restapi.networkadmin.service;

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

import com.btsl.common.ListValueVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.util.JUnitConfig;
import com.restapi.networkadmin.responseVO.GeoDomainTypeListResponseVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {GeographicalDomainServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class GeographicalDomainServiceImplTest {
    @Autowired
    private GeographicalDomainServiceImpl geographicalDomainServiceImpl;

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadGeoDomainTypeList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadGeoDomainTypeList() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadGeoDomainTypeList(GeographicalDomainServiceImpl.java:83)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        //doNothing().when(con).close();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        geographicalDomainServiceImpl.loadGeoDomainTypeList(JUnitConfig.getConnection(), locale, response1);
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadGeoDomainTypeList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadGeoDomainTypeList2() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadGeoDomainTypeList(GeographicalDomainServiceImpl.java:79)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       // doNothing().when(con).close();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        geographicalDomainServiceImpl.loadGeoDomainTypeList(JUnitConfig.getConnection(), locale, response1);
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadGeoDomainTypeList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadGeoDomainTypeList3() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadGeoDomainTypeList(GeographicalDomainServiceImpl.java:83)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        //doNothing().when(con).close();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        geographicalDomainServiceImpl.loadGeoDomainTypeList(JUnitConfig.getConnection(), null, response1);
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadGeoDomainTypeList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    public void testLoadGeoDomainTypeList4() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      //  doNothing().when(con).close();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        GeoDomainTypeListResponseVO actualLoadGeoDomainTypeListResult = geographicalDomainServiceImpl
                .loadGeoDomainTypeList(JUnitConfig.getConnection(), locale, responseSwag);
        assertEquals(200, actualLoadGeoDomainTypeListResult.getStatus());
        assertEquals("9020", actualLoadGeoDomainTypeListResult.getMessageCode());
        assertNull(actualLoadGeoDomainTypeListResult.getMessage());
        assertEquals(2, actualLoadGeoDomainTypeListResult.getGeoDomTypeList().size());
     //   verify(con).prepareStatement(Mockito.<String>any());
      //  verify(con).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
        assertEquals(200, responseSwag.getStatus());
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadGeoDomainTypeList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadGeoDomainTypeList5() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadGeoDomainTypeList(GeographicalDomainServiceImpl.java:83)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       // doNothing().when(con).close();
        geographicalDomainServiceImpl.loadGeoDomainTypeList(JUnitConfig.getConnection(), Locale.getDefault(), null);
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadGeoDomainTypeList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    public void testLoadGeoDomainTypeList6() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
    //    doNothing().when(con).close();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        GeoDomainTypeListResponseVO actualLoadGeoDomainTypeListResult = geographicalDomainServiceImpl
                .loadGeoDomainTypeList(JUnitConfig.getConnection(), locale, responseSwag);
        assertEquals(400, actualLoadGeoDomainTypeListResult.getStatus());
        assertEquals("error.general.sql.processing", actualLoadGeoDomainTypeListResult.getMessageCode());
        assertNull(actualLoadGeoDomainTypeListResult.getMessage());
        assertTrue(actualLoadGeoDomainTypeListResult.getGeoDomTypeList().isEmpty());
      //  verify(con).prepareStatement(Mockito.<String>any());
    //    verify(con).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
        assertEquals(400, responseSwag.getStatus());
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadGeoDomainTypeList(Connection, Locale, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadGeoDomainTypeList7() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadGeoDomainTypeList(GeographicalDomainServiceImpl.java:79)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
   //     when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
    //    doNothing().when(con).close();
        geographicalDomainServiceImpl.loadGeoDomainTypeList(JUnitConfig.getConnection(), Locale.getDefault(), null);
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadParentGeoDomainTypeList(Connection, Locale, HttpServletResponse, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadParentGeoDomainTypeList() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadParentGeoDomainTypeList(GeographicalDomainServiceImpl.java:128)
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
    //    when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      //  doNothing().when(con).close();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        geographicalDomainServiceImpl.loadParentGeoDomainTypeList(JUnitConfig.getConnection(), locale, response1,
                "Geo Domain Type");
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadParentGeoDomainTypeList(Connection, Locale, HttpServletResponse, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadParentGeoDomainTypeList2() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadParentGeoDomainTypeList(GeographicalDomainServiceImpl.java:124)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
  //      when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
   //     doNothing().when(con).close();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        geographicalDomainServiceImpl.loadParentGeoDomainTypeList(JUnitConfig.getConnection(), locale, response1,
                "Geo Domain Type");
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadParentGeoDomainTypeList(Connection, Locale, HttpServletResponse, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadParentGeoDomainTypeList3() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.networkadmin.service.GeographicalDomainServiceImpl.loadParentGeoDomainTypeList(GeographicalDomainServiceImpl.java:128)
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
    //    when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      //  doNothing().when(con).close();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        geographicalDomainServiceImpl.loadParentGeoDomainTypeList(JUnitConfig.getConnection(), null, response1,
                "Geo Domain Type");
    }

    /**
     * Method under test: {@link GeographicalDomainServiceImpl#loadParentGeoDomainTypeList(Connection, Locale, HttpServletResponse, String)}
     */
    @Test
    public void testLoadParentGeoDomainTypeList4() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
    //    when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      //  doNothing().when(con).close();
        Locale locale = Locale.getDefault();
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        GeoDomainTypeListResponseVO actualLoadParentGeoDomainTypeListResult = geographicalDomainServiceImpl
                .loadParentGeoDomainTypeList(JUnitConfig.getConnection(), locale, responseSwag, "Geo Domain Type");
        assertEquals(200, actualLoadParentGeoDomainTypeListResult.getStatus());
        assertEquals("9020", actualLoadParentGeoDomainTypeListResult.getMessageCode());
        assertNull(actualLoadParentGeoDomainTypeListResult.getMessage());
        ArrayList<ListValueVO> geoDomTypeList = actualLoadParentGeoDomainTypeListResult.getGeoDomTypeList();
        assertEquals(1, geoDomTypeList.size());
   //     verify(con).prepareStatement(Mockito.<String>any());
    //    verify(con).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
        assertEquals(200, responseSwag.getStatus());
        assertSame(geoDomTypeList, geographicalDomainServiceImpl.parentTypeList);
    }
}

