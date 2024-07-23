package com.restapi.c2s.services.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.requesthandler.GetParentOwnerProfileRespVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class RestUserDAOTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfo(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfo() throws BTSLBaseException, SQLException {
       RestUserDAO restUserDAO = new RestUserDAO();
  /*      ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
  */      //Connection con = mock(Connection.class);
//        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        com.btsl.util.JUnitConfig.init();
        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        GetParentOwnerProfileRespVO actualParentOwnerProfileInfo = restUserDAO.getParentOwnerProfileInfo(com.btsl.util.JUnitConfig.getConnection(),
                getParentOwnerProfileReq);
        assertEquals("String,String,String,String,String", actualParentOwnerProfileInfo.getAddress());
        assertEquals("String", actualParentOwnerProfileInfo.getUserNamePrefix());
        assertEquals("String", actualParentOwnerProfileInfo.getUserName());
        assertTrue(actualParentOwnerProfileInfo.getSuccessList().isEmpty());
        /*assertEquals("String", actualParentOwnerProfileInfo.getStatus());
        assertEquals("String", actualParentOwnerProfileInfo.getShortName());
        assertEquals("String", actualParentOwnerProfileInfo.getParentUserID());
        assertEquals("String", actualParentOwnerProfileInfo.getParentName());
        assertEquals("String", actualParentOwnerProfileInfo.getParentMobileNumber());
        assertEquals("String", actualParentOwnerProfileInfo.getParentCategoryName());
        assertEquals("String", actualParentOwnerProfileInfo.getOwnerName());
        assertEquals("String", actualParentOwnerProfileInfo.getOwnerMobileNumber());
        assertEquals("String", actualParentOwnerProfileInfo.getOwnerCategoryName());
        assertEquals("String", actualParentOwnerProfileInfo.getMsisdn());
        assertEquals("String", actualParentOwnerProfileInfo.getGrade());
        assertEquals("String", actualParentOwnerProfileInfo.getEmailID());
        *//*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfo(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfo2() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        restUserDAO.getParentOwnerProfileInfo(com.btsl.util.JUnitConfig.getConnection(), getParentOwnerProfileReq);
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfo(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfo3() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        com.btsl.util.JUnitConfig.init();

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        GetParentOwnerProfileRespVO actualParentOwnerProfileInfo = restUserDAO.getParentOwnerProfileInfo(com.btsl.util.JUnitConfig.getConnection(),
                getParentOwnerProfileReq);
 /*       assertEquals("", actualParentOwnerProfileInfo.getAddress());
        assertEquals("", actualParentOwnerProfileInfo.getUserNamePrefix());
        assertEquals("", actualParentOwnerProfileInfo.getUserName());
        assertTrue(actualParentOwnerProfileInfo.getSuccessList().isEmpty());
        assertEquals("", actualParentOwnerProfileInfo.getStatus());
        assertEquals("", actualParentOwnerProfileInfo.getShortName());
        assertEquals("", actualParentOwnerProfileInfo.getParentUserID());
        assertEquals("", actualParentOwnerProfileInfo.getParentName());
        assertEquals("", actualParentOwnerProfileInfo.getParentMobileNumber());
        assertEquals("", actualParentOwnerProfileInfo.getParentCategoryName());
        assertEquals("", actualParentOwnerProfileInfo.getOwnerName());
        assertEquals("", actualParentOwnerProfileInfo.getOwnerMobileNumber());
        assertEquals("", actualParentOwnerProfileInfo.getOwnerCategoryName());
        assertEquals("", actualParentOwnerProfileInfo.getMsisdn());
        assertEquals("", actualParentOwnerProfileInfo.getGrade());
        assertEquals("", actualParentOwnerProfileInfo.getEmailID());
 */    /*   verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfo(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfo4() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        GetParentOwnerProfileRespVO actualParentOwnerProfileInfo = restUserDAO.getParentOwnerProfileInfo(com.btsl.util.JUnitConfig.getConnection(),
                getParentOwnerProfileReq);
/*
        assertEquals("", actualParentOwnerProfileInfo.getAddress());
        assertNull(actualParentOwnerProfileInfo.getUserNamePrefix());
        assertNull(actualParentOwnerProfileInfo.getUserName());
        assertTrue(actualParentOwnerProfileInfo.getSuccessList().isEmpty());
        assertNull(actualParentOwnerProfileInfo.getStatus());
        assertNull(actualParentOwnerProfileInfo.getShortName());
        assertNull(actualParentOwnerProfileInfo.getParentUserID());
        assertNull(actualParentOwnerProfileInfo.getParentName());
        assertNull(actualParentOwnerProfileInfo.getParentMobileNumber());
        assertNull(actualParentOwnerProfileInfo.getParentCategoryName());
        assertNull(actualParentOwnerProfileInfo.getOwnerName());
        assertNull(actualParentOwnerProfileInfo.getOwnerMobileNumber());
        assertNull(actualParentOwnerProfileInfo.getOwnerCategoryName());
        assertNull(actualParentOwnerProfileInfo.getMsisdn());
        assertNull(actualParentOwnerProfileInfo.getGrade());
        assertNull(actualParentOwnerProfileInfo.getEmailID());
*/
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfo(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfo5() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        assertNull(restUserDAO.getParentOwnerProfileInfo(com.btsl.util.JUnitConfig.getConnection(), getParentOwnerProfileReq));
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfoForAllUsers(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfoForAllUsers() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        GetParentOwnerProfileRespVO actualParentOwnerProfileInfoForAllUsers = restUserDAO
                .getParentOwnerProfileInfoForAllUsers(com.btsl.util.JUnitConfig.getConnection(), getParentOwnerProfileReq);
        assertEquals("String,String,String,String,String", actualParentOwnerProfileInfoForAllUsers.getAddress());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getUserNamePrefix());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getUserName());
        assertTrue(actualParentOwnerProfileInfoForAllUsers.getSuccessList().isEmpty());
/*
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getStatus());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getShortName());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getParentUserID());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getParentName());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getParentMobileNumber());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getParentCategoryName());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getOwnerName());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getOwnerMobileNumber());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getOwnerCategoryName());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getMsisdn());
        assertEquals("String", actualParentOwnerProfileInfoForAllUsers.getEmailID());
*/
      /*  verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfoForAllUsers(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfoForAllUsers2() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        restUserDAO.getParentOwnerProfileInfoForAllUsers(com.btsl.util.JUnitConfig.getConnection(), getParentOwnerProfileReq);
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfoForAllUsers(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfoForAllUsers3() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(" ");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       */ //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        GetParentOwnerProfileRespVO actualParentOwnerProfileInfoForAllUsers = restUserDAO
                .getParentOwnerProfileInfoForAllUsers(com.btsl.util.JUnitConfig.getConnection(), getParentOwnerProfileReq);
        assertEquals("", actualParentOwnerProfileInfoForAllUsers.getAddress());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getUserNamePrefix());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getUserName());
        assertTrue(actualParentOwnerProfileInfoForAllUsers.getSuccessList().isEmpty());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getStatus());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getShortName());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getParentUserID());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getParentName());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getParentMobileNumber());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getParentCategoryName());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getOwnerName());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getOwnerMobileNumber());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getOwnerCategoryName());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getMsisdn());
        assertEquals(" ", actualParentOwnerProfileInfoForAllUsers.getEmailID());
      /*  verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfoForAllUsers(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfoForAllUsers4() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        GetParentOwnerProfileRespVO actualParentOwnerProfileInfoForAllUsers = restUserDAO
                .getParentOwnerProfileInfoForAllUsers(com.btsl.util.JUnitConfig.getConnection(), getParentOwnerProfileReq);
        assertEquals("", actualParentOwnerProfileInfoForAllUsers.getAddress());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getUserNamePrefix());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getUserName());
        assertTrue(actualParentOwnerProfileInfoForAllUsers.getSuccessList().isEmpty());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getStatus());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getShortName());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getParentUserID());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getParentName());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getParentMobileNumber());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getParentCategoryName());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getOwnerName());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getOwnerMobileNumber());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getOwnerCategoryName());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getMsisdn());
        assertNull(actualParentOwnerProfileInfoForAllUsers.getEmailID());
     /*   verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerProfileInfoForAllUsers(Connection, GetParentOwnerProfileReq)}
     */
    @Test
    public void testGetParentOwnerProfileInfoForAllUsers5() throws BTSLBaseException, SQLException {
        RestUserDAO restUserDAO = new RestUserDAO();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        com.btsl.util.JUnitConfig.init();
        GetParentOwnerProfileReq getParentOwnerProfileReq = new GetParentOwnerProfileReq();
        getParentOwnerProfileReq.setCategoryCode("Category Code");
        getParentOwnerProfileReq.setDomainCode("Domain Code");
        getParentOwnerProfileReq.setExtnwcode("Extnwcode");
        getParentOwnerProfileReq.setFromDate("2020-03-01");
        getParentOwnerProfileReq.setGeography("Geography");
        getParentOwnerProfileReq.setLocale(Locale.getDefault());
        getParentOwnerProfileReq.setMsisdn("Msisdn");
        getParentOwnerProfileReq.setProductCode("Product Code");
        getParentOwnerProfileReq.setToDate("2020-03-01");
        getParentOwnerProfileReq.setUserId("42");
        assertNull(restUserDAO.getParentOwnerProfileInfoForAllUsers(com.btsl.util.JUnitConfig.getConnection(), getParentOwnerProfileReq));
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link RestUserDAO#getParentOwnerInfoForAllUsers()}
     */
    @Test
    public void testGetParentOwnerInfoForAllUsers() {
        assertEquals(
                " SELECT loggedinUser.user_id loginUserID,  loggedinUser.status  AS status,   loggedinUser.email     "
                        + "     AS emailID,  loggedinUser.address1 as address1, loggedinUser.address2 as address2,  loggedinUser.state"
                        + " as state,  loggedinUser.city  as city,  loggedinUser.country  as country,  loggedinUser.external_code"
                        + "  AS ERPCODE,  loggedinUser.category_code  AS category_Code,  loggedinUser.msisdn         AS msisdn,"
                        + "  loggedinUser.user_name      USER_NAME,  loggedinUser.user_name_prefix      USER_NAME_PREFIX, "
                        + " loggedinUser.short_name      SHORT_NAME,  CASE loggedinUser.PARENT_ID WHEN  'ROOT'   THEN  'ROOT'  "
                        + "  ELSE PU.user_id   END   AS  PARENTUSERID,  PU.user_name    AS parent_name,  PU.msisdn    END   as "
                        + " parent_msisdn,  PC.category_name      AS  Parent_category_name ,  OU.user_id   as    OwnerUserID, "
                        + " OU.user_name     AS owner_name,  OU.msisdn        AS owner_msisdn,  OC.category_name AS Owner_Category"
                        + "  FROM   USERS loggedinUser ,   USERS PU ,  users OU,  categories PC,  categories OC  WHERE "
                        + " loggedinUser.user_id = ?   AND  PU.USER_ID = CASE WHEN loggedinUser.PARENT_ID='ROOT'   THEN  ?   "
                        + " ELSE loggedinUser.PARENT_ID  END  AND OU.user_id = loggedinUser.owner_id  AND PC.category_code ="
                        + " PU.category_code  AND OC.category_code = OU.category_code ",
                (new RestUserDAO()).getParentOwnerInfoForAllUsers());
    }
}

