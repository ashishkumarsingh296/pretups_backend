package com.web.pretups.channel.transfer.businesslogic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class ChannelTransferRuleWebDAOTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryListForFOC(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryListForFOC() throws BTSLBaseException, SQLException {
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(2,
                channelTransferRuleWebDAO
                        .loadTransferRulesCategoryListForFOC(p_con, "P network Code", "jane.doe@example.org", "P foc Allowed",
                                "P transfer Type")
                        .size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryListForFOC(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryListForFOC2() throws BTSLBaseException, SQLException {
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.loadTransferRulesCategoryListForFOC(p_con, "P network Code", "jane.doe@example.org",
                "P foc Allowed", "P transfer Type");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadChannelTransferRuleVOList(Connection, String, String, String)}
     */
    @Test
    public void testLoadChannelTransferRuleVOList() throws BTSLBaseException, SQLException {
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
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
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(2,
                channelTransferRuleWebDAO
                        .loadChannelTransferRuleVOList(p_con, "P network Code", "P domain Code", "P rule Type")
                        .size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp, atLeast(1)).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadApprovalChannelTransferRuleNewVOList(Connection, String, String, String)}
     */
    @Test
    public void testLoadApprovalChannelTransferRuleNewVOList() throws BTSLBaseException, SQLException {
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
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
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(2,
                channelTransferRuleWebDAO
                        .loadApprovalChannelTransferRuleNewVOList(p_con, "jane.doe@example.org", "P status", "P rule Type")
                        .size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp, atLeast(1)).getTime();
    }
}

