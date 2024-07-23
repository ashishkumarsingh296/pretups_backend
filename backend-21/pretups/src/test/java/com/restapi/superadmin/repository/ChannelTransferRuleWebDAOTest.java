package com.restapi.superadmin.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.util.JUnitConfig;
import com.restapi.superadminVO.ChannelTransferRuleVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ChannelTransferRuleWebDAO.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelTransferRuleWebDAOTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ChannelTransferRuleWebDAO channelTransferRuleWebDAO;

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadProductList(Connection, String, String)}
     */
    @Test
    public void testLoadProductList() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(2, channelTransferRuleWebDAO.loadProductList(JUnitConfig.getConnection(), "P network Code", "P module Code").size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadProductList(Connection, String, String)}
     */
    @Test
    public void testLoadProductList2() throws BTSLBaseException, SQLException {
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
        channelTransferRuleWebDAO.loadProductList(JUnitConfig.getConnection(), "P network Code", "P module Code");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTransferRule() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.addChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTransferRule2() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.addChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTransferRule3() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(0, channelTransferRuleWebDAO.addChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTransferRule4() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        ChannelTransferRuleVO p_channelTransferRuleVO = mock(ChannelTransferRuleVO.class);
        when(p_channelTransferRuleVO.getFirstApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getSecondApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getApprovalRequired()).thenReturn("Approval Required");
        when(p_channelTransferRuleVO.getCntrlReturnLevel()).thenReturn("Cntrl Return Level");
        when(p_channelTransferRuleVO.getCntrlTransferLevel()).thenReturn("Cntrl Transfer Level");
        when(p_channelTransferRuleVO.getCntrlWithdrawLevel()).thenReturn("Cntrl Withdraw Level");
        when(p_channelTransferRuleVO.getCreatedBy()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(p_channelTransferRuleVO.getDirectTransferAllowed()).thenReturn("Direct Transfer Allowed");
        when(p_channelTransferRuleVO.getDomainCode()).thenReturn("Domain Code");
        when(p_channelTransferRuleVO.getDpAllowed()).thenReturn("Dp Allowed");
        when(p_channelTransferRuleVO.getFixedReturnCategory()).thenReturn("Fixed Return Category");
        when(p_channelTransferRuleVO.getFixedReturnLevel()).thenReturn("Fixed Return Level");
        when(p_channelTransferRuleVO.getFixedTransferCategory()).thenReturn("Fixed Transfer Category");
        when(p_channelTransferRuleVO.getFixedTransferLevel()).thenReturn("Fixed Transfer Level");
        when(p_channelTransferRuleVO.getFixedWithdrawCategory()).thenReturn("Fixed Withdraw Category");
        when(p_channelTransferRuleVO.getFixedWithdrawLevel()).thenReturn("Fixed Withdraw Level");
        when(p_channelTransferRuleVO.getFocAllowed()).thenReturn("Foc Allowed");
        when(p_channelTransferRuleVO.getFocTransferType()).thenReturn("Foc Transfer Type");
        when(p_channelTransferRuleVO.getFromCategory()).thenReturn("jane.doe@example.org");
        when(p_channelTransferRuleVO.getModifiedBy()).thenReturn("Jan 1, 2020 9:00am GMT+0100");
        when(p_channelTransferRuleVO.getNetworkCode()).thenReturn("Network Code");
        when(p_channelTransferRuleVO.getParentAssocationAllowed()).thenReturn("Parent Assocation Allowed");
        when(p_channelTransferRuleVO.getRestrictedMsisdnAccess()).thenReturn("Restricted Msisdn Access");
        when(p_channelTransferRuleVO.getRestrictedRechargeAccess()).thenReturn("Restricted Recharge Access");
        when(p_channelTransferRuleVO.getReturnAllowed()).thenReturn("Return Allowed");
        when(p_channelTransferRuleVO.getReturnChnlBypassAllowed()).thenReturn("Return Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getToCategory()).thenReturn("To Category");
        when(p_channelTransferRuleVO.getToDomainCode()).thenReturn("To Domain Code");
        when(p_channelTransferRuleVO.getTransferAllowed()).thenReturn("Transfer Allowed");
        when(p_channelTransferRuleVO.getTransferChnlBypassAllowed()).thenReturn("Transfer Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getTransferRuleID()).thenReturn("Transfer Rule ID");
        when(p_channelTransferRuleVO.getTransferType()).thenReturn("Transfer Type");
        when(p_channelTransferRuleVO.getType()).thenReturn("Type");
        when(p_channelTransferRuleVO.getUncntrlReturnAllowed()).thenReturn("Uncntrl Return Allowed");
        when(p_channelTransferRuleVO.getUncntrlReturnLevel()).thenReturn("Uncntrl Return Level");
        when(p_channelTransferRuleVO.getUncntrlTransferAllowed()).thenReturn("Uncntrl Transfer Allowed");
        when(p_channelTransferRuleVO.getUncntrlTransferLevel()).thenReturn("Uncntrl Transfer Level");
        when(p_channelTransferRuleVO.getUncntrlWithdrawAllowed()).thenReturn("Uncntrl Withdraw Allowed");
        when(p_channelTransferRuleVO.getUncntrlWithdrawLevel()).thenReturn("Uncntrl Withdraw Level");
        when(p_channelTransferRuleVO.getWithdrawAllowed()).thenReturn("Withdraw Allowed");
        when(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed()).thenReturn("Withdraw Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getProductArray()).thenReturn(new String[]{});
        when(p_channelTransferRuleVO.getCreatedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        when(p_channelTransferRuleVO.getModifiedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        doNothing().when(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        doNothing().when(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.addChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement).close();
        verify(p_channelTransferRuleVO).getFirstApprovalLimit();
        verify(p_channelTransferRuleVO).getSecondApprovalLimit();
        verify(p_channelTransferRuleVO).getApprovalRequired();
        verify(p_channelTransferRuleVO).getCntrlReturnLevel();
        verify(p_channelTransferRuleVO).getCntrlTransferLevel();
        verify(p_channelTransferRuleVO).getCntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getCreatedBy();
        verify(p_channelTransferRuleVO).getDirectTransferAllowed();
        verify(p_channelTransferRuleVO).getDomainCode();
        verify(p_channelTransferRuleVO).getDpAllowed();
        verify(p_channelTransferRuleVO).getFixedReturnCategory();
        verify(p_channelTransferRuleVO).getFixedReturnLevel();
        verify(p_channelTransferRuleVO).getFixedTransferCategory();
        verify(p_channelTransferRuleVO).getFixedTransferLevel();
        verify(p_channelTransferRuleVO).getFixedWithdrawCategory();
        verify(p_channelTransferRuleVO).getFixedWithdrawLevel();
        verify(p_channelTransferRuleVO).getFocAllowed();
        verify(p_channelTransferRuleVO).getFocTransferType();
        verify(p_channelTransferRuleVO).getFromCategory();
        verify(p_channelTransferRuleVO).getModifiedBy();
        verify(p_channelTransferRuleVO).getNetworkCode();
        verify(p_channelTransferRuleVO).getParentAssocationAllowed();
        verify(p_channelTransferRuleVO).getRestrictedMsisdnAccess();
        verify(p_channelTransferRuleVO).getRestrictedRechargeAccess();
        verify(p_channelTransferRuleVO).getReturnAllowed();
        verify(p_channelTransferRuleVO).getReturnChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getToCategory();
        verify(p_channelTransferRuleVO).getToDomainCode();
        verify(p_channelTransferRuleVO).getTransferAllowed();
        verify(p_channelTransferRuleVO).getTransferChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getTransferRuleID();
        verify(p_channelTransferRuleVO).getTransferType();
        verify(p_channelTransferRuleVO).getType();
        verify(p_channelTransferRuleVO).getUncntrlReturnAllowed();
        verify(p_channelTransferRuleVO).getUncntrlReturnLevel();
        verify(p_channelTransferRuleVO).getUncntrlTransferAllowed();
        verify(p_channelTransferRuleVO).getUncntrlTransferLevel();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawAllowed();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getWithdrawAllowed();
        verify(p_channelTransferRuleVO).getWithdrawChnlBypassAllowed();
        verify(p_channelTransferRuleVO, atLeast(1)).getProductArray();
        verify(p_channelTransferRuleVO).getCreatedOn();
        verify(p_channelTransferRuleVO).getModifiedOn();
        verify(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        verify(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        verify(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTransferRule5() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        ChannelTransferRuleVO p_channelTransferRuleVO = mock(ChannelTransferRuleVO.class);
        when(p_channelTransferRuleVO.getFirstApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getSecondApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getApprovalRequired()).thenReturn("Approval Required");
        when(p_channelTransferRuleVO.getCntrlReturnLevel()).thenReturn("Cntrl Return Level");
        when(p_channelTransferRuleVO.getCntrlTransferLevel()).thenReturn("Cntrl Transfer Level");
        when(p_channelTransferRuleVO.getCntrlWithdrawLevel()).thenReturn("Cntrl Withdraw Level");
        when(p_channelTransferRuleVO.getCreatedBy()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(p_channelTransferRuleVO.getDirectTransferAllowed()).thenReturn("Direct Transfer Allowed");
        when(p_channelTransferRuleVO.getDomainCode()).thenReturn("Domain Code");
        when(p_channelTransferRuleVO.getDpAllowed()).thenReturn("Dp Allowed");
        when(p_channelTransferRuleVO.getFixedReturnCategory()).thenReturn("Fixed Return Category");
        when(p_channelTransferRuleVO.getFixedReturnLevel()).thenReturn("Fixed Return Level");
        when(p_channelTransferRuleVO.getFixedTransferCategory()).thenReturn("Fixed Transfer Category");
        when(p_channelTransferRuleVO.getFixedTransferLevel()).thenReturn("Fixed Transfer Level");
        when(p_channelTransferRuleVO.getFixedWithdrawCategory()).thenReturn("Fixed Withdraw Category");
        when(p_channelTransferRuleVO.getFixedWithdrawLevel()).thenReturn("Fixed Withdraw Level");
        when(p_channelTransferRuleVO.getFocAllowed()).thenReturn("Foc Allowed");
        when(p_channelTransferRuleVO.getFocTransferType()).thenReturn("Foc Transfer Type");
        when(p_channelTransferRuleVO.getFromCategory()).thenReturn("jane.doe@example.org");
        when(p_channelTransferRuleVO.getModifiedBy()).thenReturn("Jan 1, 2020 9:00am GMT+0100");
        when(p_channelTransferRuleVO.getNetworkCode()).thenReturn("Network Code");
        when(p_channelTransferRuleVO.getParentAssocationAllowed()).thenReturn("Parent Assocation Allowed");
        when(p_channelTransferRuleVO.getRestrictedMsisdnAccess()).thenReturn("Restricted Msisdn Access");
        when(p_channelTransferRuleVO.getRestrictedRechargeAccess()).thenReturn("Restricted Recharge Access");
        when(p_channelTransferRuleVO.getReturnAllowed()).thenReturn("Return Allowed");
        when(p_channelTransferRuleVO.getReturnChnlBypassAllowed()).thenReturn("Return Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getToCategory()).thenReturn("To Category");
        when(p_channelTransferRuleVO.getToDomainCode()).thenReturn("To Domain Code");
        when(p_channelTransferRuleVO.getTransferAllowed()).thenReturn("Transfer Allowed");
        when(p_channelTransferRuleVO.getTransferChnlBypassAllowed()).thenReturn("Transfer Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getTransferRuleID()).thenReturn("Transfer Rule ID");
        when(p_channelTransferRuleVO.getTransferType()).thenReturn("Transfer Type");
        when(p_channelTransferRuleVO.getType()).thenReturn("Type");
        when(p_channelTransferRuleVO.getUncntrlReturnAllowed()).thenReturn("Uncntrl Return Allowed");
        when(p_channelTransferRuleVO.getUncntrlReturnLevel()).thenReturn("Uncntrl Return Level");
        when(p_channelTransferRuleVO.getUncntrlTransferAllowed()).thenReturn("Uncntrl Transfer Allowed");
        when(p_channelTransferRuleVO.getUncntrlTransferLevel()).thenReturn("Uncntrl Transfer Level");
        when(p_channelTransferRuleVO.getUncntrlWithdrawAllowed()).thenReturn("Uncntrl Withdraw Allowed");
        when(p_channelTransferRuleVO.getUncntrlWithdrawLevel()).thenReturn("Uncntrl Withdraw Level");
        when(p_channelTransferRuleVO.getWithdrawAllowed()).thenReturn("Withdraw Allowed");
        when(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed()).thenReturn("Withdraw Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getProductArray()).thenReturn(new String[]{"Product Array"});
        when(p_channelTransferRuleVO.getCreatedOn()).thenReturn(null);
        when(p_channelTransferRuleVO.getModifiedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        doNothing().when(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        doNothing().when(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.addChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(p_channelTransferRuleVO).getFirstApprovalLimit();
        verify(p_channelTransferRuleVO).getSecondApprovalLimit();
        verify(p_channelTransferRuleVO).getApprovalRequired();
        verify(p_channelTransferRuleVO).getCntrlReturnLevel();
        verify(p_channelTransferRuleVO).getCntrlTransferLevel();
        verify(p_channelTransferRuleVO).getCntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getCreatedBy();
        verify(p_channelTransferRuleVO).getDirectTransferAllowed();
        verify(p_channelTransferRuleVO).getDomainCode();
        verify(p_channelTransferRuleVO).getDpAllowed();
        verify(p_channelTransferRuleVO).getFixedReturnCategory();
        verify(p_channelTransferRuleVO).getFixedReturnLevel();
        verify(p_channelTransferRuleVO).getFixedTransferCategory();
        verify(p_channelTransferRuleVO).getFixedTransferLevel();
        verify(p_channelTransferRuleVO).getFixedWithdrawCategory();
        verify(p_channelTransferRuleVO).getFixedWithdrawLevel();
        verify(p_channelTransferRuleVO).getFocAllowed();
        verify(p_channelTransferRuleVO).getFocTransferType();
        verify(p_channelTransferRuleVO).getFromCategory();
        verify(p_channelTransferRuleVO).getModifiedBy();
        verify(p_channelTransferRuleVO).getNetworkCode();
        verify(p_channelTransferRuleVO).getParentAssocationAllowed();
        verify(p_channelTransferRuleVO).getRestrictedMsisdnAccess();
        verify(p_channelTransferRuleVO).getRestrictedRechargeAccess();
        verify(p_channelTransferRuleVO).getReturnAllowed();
        verify(p_channelTransferRuleVO).getReturnChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getToCategory();
        verify(p_channelTransferRuleVO).getToDomainCode();
        verify(p_channelTransferRuleVO).getTransferAllowed();
        verify(p_channelTransferRuleVO).getTransferChnlBypassAllowed();
        verify(p_channelTransferRuleVO, atLeast(1)).getTransferRuleID();
        verify(p_channelTransferRuleVO).getTransferType();
        verify(p_channelTransferRuleVO).getType();
        verify(p_channelTransferRuleVO).getUncntrlReturnAllowed();
        verify(p_channelTransferRuleVO).getUncntrlReturnLevel();
        verify(p_channelTransferRuleVO).getUncntrlTransferAllowed();
        verify(p_channelTransferRuleVO).getUncntrlTransferLevel();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawAllowed();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getWithdrawAllowed();
        verify(p_channelTransferRuleVO).getWithdrawChnlBypassAllowed();
        verify(p_channelTransferRuleVO, atLeast(1)).getProductArray();
        verify(p_channelTransferRuleVO).getCreatedOn();
        verify(p_channelTransferRuleVO).getModifiedOn();
        verify(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        verify(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        verify(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#updateChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testUpdateChannelTransferRule() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.updateChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#updateChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testUpdateChannelTransferRule2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.updateChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#updateChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testUpdateChannelTransferRule3() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.updateChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#updateChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testUpdateChannelTransferRule4() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(0, channelTransferRuleWebDAO.updateChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#deleteChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testDeleteChannelTransferRule() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.deleteChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#deleteChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testDeleteChannelTransferRule2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.deleteChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#deleteChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testDeleteChannelTransferRule3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.deleteChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadChannelTransferRuleVOList(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadChannelTransferRuleVOList() throws BTSLBaseException, SQLException {
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
                        .loadChannelTransferRuleVOList(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org", "P to Domain Code",
                                "P rule Type")
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
     * Method under test: {@link ChannelTransferRuleWebDAO#isTransferRuleExists(Connection, CategoryVO)}
     */
    @Test
    public void testIsTransferRuleExists() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        assertTrue(channelTransferRuleWebDAO.isTransferRuleExists(JUnitConfig.getConnection(), p_categoryVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#isTransferRuleExists(Connection, CategoryVO)}
     */
    @Test
    public void testIsTransferRuleExists2() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.isTransferRuleExists(JUnitConfig.getConnection(), p_categoryVO);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#isTransferRuleExists(Connection, CategoryVO)}
     */
    @Test
    public void testIsTransferRuleExists3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        assertFalse(channelTransferRuleWebDAO.isTransferRuleExists(JUnitConfig.getConnection(), p_categoryVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#isUncontrolTransferAllowedTransferRuleExists(Connection, CategoryVO)}
     */
    @Test
    public void testIsUncontrolTransferAllowedTransferRuleExists() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        assertTrue(channelTransferRuleWebDAO.isUncontrolTransferAllowedTransferRuleExists(JUnitConfig.getConnection(), p_categoryVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#isUncontrolTransferAllowedTransferRuleExists(Connection, CategoryVO)}
     */
    @Test
    public void testIsUncontrolTransferAllowedTransferRuleExists2() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.isUncontrolTransferAllowedTransferRuleExists(JUnitConfig.getConnection(), p_categoryVO);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#isUncontrolTransferAllowedTransferRuleExists(Connection, CategoryVO)}
     */
    @Test
    public void testIsUncontrolTransferAllowedTransferRuleExists3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        assertFalse(channelTransferRuleWebDAO.isUncontrolTransferAllowedTransferRuleExists(JUnitConfig.getConnection(), p_categoryVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRuleVOList(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRuleVOList() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
                        .loadTransferRuleVOList(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org", "P to Domain Code",
                                "P rule Type")
                        .size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRuleVOList(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRuleVOList2() throws BTSLBaseException, SQLException {
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
        channelTransferRuleWebDAO.loadTransferRuleVOList(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org",
                "P to Domain Code", "P rule Type");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#setStatus(Connection, String, String)}
     */
    @Test
    public void testSetStatus() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(1, channelTransferRuleWebDAO.setStatus(JUnitConfig.getConnection(), "P status", "P transfer Rule ID"));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#setStatus(Connection, String, String)}
     */
    @Test
    public void testSetStatus2() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doThrow(new SQLException()).when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.setStatus(JUnitConfig.getConnection(), "P status", "P transfer Rule ID");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryListForDP(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryListForDP() throws BTSLBaseException, SQLException {
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
                        .loadTransferRulesCategoryListForDP(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org", "P dp Allowed",
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
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryListForDP(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryListForDP2() throws BTSLBaseException, SQLException {
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
        channelTransferRuleWebDAO.loadTransferRulesCategoryListForDP(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org",
                "P dp Allowed", "P transfer Type");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadChannelTransferRuleNewVOList(Connection, String, String, String, String, String)}
     */
    @Test
    public void testLoadChannelTransferRuleNewVOList() throws BTSLBaseException, SQLException {
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
                        .loadChannelTransferRuleNewVOList(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org", "P to Domain Code",
                                "P status", "P rule Type")
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
     * Method under test: {@link ChannelTransferRuleWebDAO#suspendRequestChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testSuspendRequestChannelTransferRule() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.suspendRequestChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#suspendRequestChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testSuspendRequestChannelTransferRule2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.suspendRequestChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#suspendRequestChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testSuspendRequestChannelTransferRule3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.suspendRequestChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#resumeRequestChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testResumeRequestChannelTransferRule() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.resumeRequestChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#resumeRequestChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testResumeRequestChannelTransferRule2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.resumeRequestChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#resumeRequestChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testResumeRequestChannelTransferRule3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.resumeRequestChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#approveChannelTransferRule(Connection, String, ChannelTransferRuleVO)}
     */
    @Test
    public void testApproveChannelTransferRule() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.approveChannelTransferRule(JUnitConfig.getConnection(), "P status", p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#approveChannelTransferRule(Connection, String, ChannelTransferRuleVO)}
     */
    @Test
    public void testApproveChannelTransferRule2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.approveChannelTransferRule(JUnitConfig.getConnection(), "P status", p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#approveChannelTransferRule(Connection, String, ChannelTransferRuleVO)}
     */
    @Test
    public void testApproveChannelTransferRule3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.approveChannelTransferRule(JUnitConfig.getConnection(), "P status", p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#rejectChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testRejectChannelTransferRule() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.rejectChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#rejectChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testRejectChannelTransferRule2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.rejectChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#rejectChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testRejectChannelTransferRule3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.rejectChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRule() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.modifyChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRule2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(0, channelTransferRuleWebDAO.modifyChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRule3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.modifyChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRule4() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        ChannelTransferRuleVO p_channelTransferRuleVO = mock(ChannelTransferRuleVO.class);
        when(p_channelTransferRuleVO.getFirstApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getLastModifiedTime()).thenReturn(1L);
        when(p_channelTransferRuleVO.getSecondApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getApprovalRequired()).thenReturn("Approval Required");
        when(p_channelTransferRuleVO.getCntrlReturnLevel()).thenReturn("Cntrl Return Level");
        when(p_channelTransferRuleVO.getCntrlTransferLevel()).thenReturn("Cntrl Transfer Level");
        when(p_channelTransferRuleVO.getCntrlWithdrawLevel()).thenReturn("Cntrl Withdraw Level");
        when(p_channelTransferRuleVO.getDirectTransferAllowed()).thenReturn("Direct Transfer Allowed");
        when(p_channelTransferRuleVO.getDpAllowed()).thenReturn("Dp Allowed");
        when(p_channelTransferRuleVO.getFixedReturnCategory()).thenReturn("Fixed Return Category");
        when(p_channelTransferRuleVO.getFixedReturnLevel()).thenReturn("Fixed Return Level");
        when(p_channelTransferRuleVO.getFixedTransferCategory()).thenReturn("Fixed Transfer Category");
        when(p_channelTransferRuleVO.getFixedTransferLevel()).thenReturn("Fixed Transfer Level");
        when(p_channelTransferRuleVO.getFixedWithdrawCategory()).thenReturn("Fixed Withdraw Category");
        when(p_channelTransferRuleVO.getFixedWithdrawLevel()).thenReturn("Fixed Withdraw Level");
        when(p_channelTransferRuleVO.getFocAllowed()).thenReturn("Foc Allowed");
        when(p_channelTransferRuleVO.getFocTransferType()).thenReturn("Foc Transfer Type");
        when(p_channelTransferRuleVO.getFromCategory()).thenReturn("jane.doe@example.org");
        when(p_channelTransferRuleVO.getModifiedBy()).thenReturn("Jan 1, 2020 9:00am GMT+0100");
        when(p_channelTransferRuleVO.getParentAssocationAllowed()).thenReturn("Parent Assocation Allowed");
        when(p_channelTransferRuleVO.getPreviousStatus()).thenReturn("Previous Status");
        when(p_channelTransferRuleVO.getRestrictedMsisdnAccess()).thenReturn("Restricted Msisdn Access");
        when(p_channelTransferRuleVO.getRestrictedRechargeAccess()).thenReturn("Restricted Recharge Access");
        when(p_channelTransferRuleVO.getReturnAllowed()).thenReturn("Return Allowed");
        when(p_channelTransferRuleVO.getReturnChnlBypassAllowed()).thenReturn("Return Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getStatus()).thenReturn("Status");
        when(p_channelTransferRuleVO.getToCategory()).thenReturn("To Category");
        when(p_channelTransferRuleVO.getToDomainCode()).thenReturn("To Domain Code");
        when(p_channelTransferRuleVO.getTransferAllowed()).thenReturn("Transfer Allowed");
        when(p_channelTransferRuleVO.getTransferChnlBypassAllowed()).thenReturn("Transfer Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getTransferRuleID()).thenReturn("Transfer Rule ID");
        when(p_channelTransferRuleVO.getTransferType()).thenReturn("Transfer Type");
        when(p_channelTransferRuleVO.getUncntrlReturnAllowed()).thenReturn("Uncntrl Return Allowed");
        when(p_channelTransferRuleVO.getUncntrlReturnLevel()).thenReturn("Uncntrl Return Level");
        when(p_channelTransferRuleVO.getUncntrlTransferAllowed()).thenReturn("Uncntrl Transfer Allowed");
        when(p_channelTransferRuleVO.getUncntrlTransferLevel()).thenReturn("Uncntrl Transfer Level");
        when(p_channelTransferRuleVO.getUncntrlWithdrawAllowed()).thenReturn("Uncntrl Withdraw Allowed");
        when(p_channelTransferRuleVO.getUncntrlWithdrawLevel()).thenReturn("Uncntrl Withdraw Level");
        when(p_channelTransferRuleVO.getWithdrawAllowed()).thenReturn("Withdraw Allowed");
        when(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed()).thenReturn("Withdraw Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getModifiedOn()).thenReturn(null);
        doNothing().when(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        doNothing().when(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.modifyChannelTransferRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet).next();
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp).getTime();
        verify(p_channelTransferRuleVO).getFirstApprovalLimit();
        verify(p_channelTransferRuleVO).getLastModifiedTime();
        verify(p_channelTransferRuleVO).getSecondApprovalLimit();
        verify(p_channelTransferRuleVO).getApprovalRequired();
        verify(p_channelTransferRuleVO).getCntrlReturnLevel();
        verify(p_channelTransferRuleVO).getCntrlTransferLevel();
        verify(p_channelTransferRuleVO).getCntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getDirectTransferAllowed();
        verify(p_channelTransferRuleVO).getDpAllowed();
        verify(p_channelTransferRuleVO).getFixedReturnCategory();
        verify(p_channelTransferRuleVO).getFixedReturnLevel();
        verify(p_channelTransferRuleVO).getFixedTransferCategory();
        verify(p_channelTransferRuleVO).getFixedTransferLevel();
        verify(p_channelTransferRuleVO).getFixedWithdrawCategory();
        verify(p_channelTransferRuleVO).getFixedWithdrawLevel();
        verify(p_channelTransferRuleVO).getFocAllowed();
        verify(p_channelTransferRuleVO).getFocTransferType();
        verify(p_channelTransferRuleVO).getFromCategory();
        verify(p_channelTransferRuleVO).getModifiedBy();
        verify(p_channelTransferRuleVO).getParentAssocationAllowed();
        verify(p_channelTransferRuleVO).getPreviousStatus();
        verify(p_channelTransferRuleVO).getRestrictedMsisdnAccess();
        verify(p_channelTransferRuleVO).getRestrictedRechargeAccess();
        verify(p_channelTransferRuleVO).getReturnAllowed();
        verify(p_channelTransferRuleVO).getReturnChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getStatus();
        verify(p_channelTransferRuleVO).getToCategory();
        verify(p_channelTransferRuleVO).getToDomainCode();
        verify(p_channelTransferRuleVO).getTransferAllowed();
        verify(p_channelTransferRuleVO).getTransferChnlBypassAllowed();
        verify(p_channelTransferRuleVO, atLeast(1)).getTransferRuleID();
        verify(p_channelTransferRuleVO).getTransferType();
        verify(p_channelTransferRuleVO).getUncntrlReturnAllowed();
        verify(p_channelTransferRuleVO).getUncntrlReturnLevel();
        verify(p_channelTransferRuleVO).getUncntrlTransferAllowed();
        verify(p_channelTransferRuleVO).getUncntrlTransferLevel();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawAllowed();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getWithdrawAllowed();
        verify(p_channelTransferRuleVO).getWithdrawChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getModifiedOn();
        verify(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        verify(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        verify(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTrfRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTrfRule() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.addChannelTrfRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTrfRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTrfRule2() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.addChannelTrfRule(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTrfRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTrfRule3() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(0, channelTransferRuleWebDAO.addChannelTrfRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTrfRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTrfRule4() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        ChannelTransferRuleVO p_channelTransferRuleVO = mock(ChannelTransferRuleVO.class);
        when(p_channelTransferRuleVO.getFirstApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getSecondApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getApprovalRequired()).thenReturn("Approval Required");
        when(p_channelTransferRuleVO.getCntrlReturnLevel()).thenReturn("Cntrl Return Level");
        when(p_channelTransferRuleVO.getCntrlTransferLevel()).thenReturn("Cntrl Transfer Level");
        when(p_channelTransferRuleVO.getCntrlWithdrawLevel()).thenReturn("Cntrl Withdraw Level");
        when(p_channelTransferRuleVO.getCreatedBy()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(p_channelTransferRuleVO.getDirectTransferAllowed()).thenReturn("Direct Transfer Allowed");
        when(p_channelTransferRuleVO.getDomainCode()).thenReturn("Domain Code");
        when(p_channelTransferRuleVO.getDpAllowed()).thenReturn("Dp Allowed");
        when(p_channelTransferRuleVO.getFixedReturnCategory()).thenReturn("Fixed Return Category");
        when(p_channelTransferRuleVO.getFixedReturnLevel()).thenReturn("Fixed Return Level");
        when(p_channelTransferRuleVO.getFixedTransferCategory()).thenReturn("Fixed Transfer Category");
        when(p_channelTransferRuleVO.getFixedTransferLevel()).thenReturn("Fixed Transfer Level");
        when(p_channelTransferRuleVO.getFixedWithdrawCategory()).thenReturn("Fixed Withdraw Category");
        when(p_channelTransferRuleVO.getFixedWithdrawLevel()).thenReturn("Fixed Withdraw Level");
        when(p_channelTransferRuleVO.getFocAllowed()).thenReturn("Foc Allowed");
        when(p_channelTransferRuleVO.getFocTransferType()).thenReturn("Foc Transfer Type");
        when(p_channelTransferRuleVO.getFromCategory()).thenReturn("jane.doe@example.org");
        when(p_channelTransferRuleVO.getModifiedBy()).thenReturn("Jan 1, 2020 9:00am GMT+0100");
        when(p_channelTransferRuleVO.getNetworkCode()).thenReturn("Network Code");
        when(p_channelTransferRuleVO.getParentAssocationAllowed()).thenReturn("Parent Assocation Allowed");
        when(p_channelTransferRuleVO.getPreviousStatus()).thenReturn("Previous Status");
        when(p_channelTransferRuleVO.getRestrictedMsisdnAccess()).thenReturn("Restricted Msisdn Access");
        when(p_channelTransferRuleVO.getRestrictedRechargeAccess()).thenReturn("Restricted Recharge Access");
        when(p_channelTransferRuleVO.getReturnAllowed()).thenReturn("Return Allowed");
        when(p_channelTransferRuleVO.getReturnChnlBypassAllowed()).thenReturn("Return Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getStatus()).thenReturn("Status");
        when(p_channelTransferRuleVO.getToCategory()).thenReturn("To Category");
        when(p_channelTransferRuleVO.getToDomainCode()).thenReturn("To Domain Code");
        when(p_channelTransferRuleVO.getTransferAllowed()).thenReturn("Transfer Allowed");
        when(p_channelTransferRuleVO.getTransferChnlBypassAllowed()).thenReturn("Transfer Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getTransferRuleID()).thenReturn("Transfer Rule ID");
        when(p_channelTransferRuleVO.getTransferType()).thenReturn("Transfer Type");
        when(p_channelTransferRuleVO.getType()).thenReturn("Type");
        when(p_channelTransferRuleVO.getUncntrlReturnAllowed()).thenReturn("Uncntrl Return Allowed");
        when(p_channelTransferRuleVO.getUncntrlReturnLevel()).thenReturn("Uncntrl Return Level");
        when(p_channelTransferRuleVO.getUncntrlTransferAllowed()).thenReturn("Uncntrl Transfer Allowed");
        when(p_channelTransferRuleVO.getUncntrlTransferLevel()).thenReturn("Uncntrl Transfer Level");
        when(p_channelTransferRuleVO.getUncntrlWithdrawAllowed()).thenReturn("Uncntrl Withdraw Allowed");
        when(p_channelTransferRuleVO.getUncntrlWithdrawLevel()).thenReturn("Uncntrl Withdraw Level");
        when(p_channelTransferRuleVO.getWithdrawAllowed()).thenReturn("Withdraw Allowed");
        when(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed()).thenReturn("Withdraw Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getProductArray()).thenReturn(new String[]{});
        when(p_channelTransferRuleVO.getCreatedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        when(p_channelTransferRuleVO.getModifiedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        doNothing().when(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        doNothing().when(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.addChannelTrfRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement).close();
        verify(p_channelTransferRuleVO).getFirstApprovalLimit();
        verify(p_channelTransferRuleVO).getSecondApprovalLimit();
        verify(p_channelTransferRuleVO).getApprovalRequired();
        verify(p_channelTransferRuleVO).getCntrlReturnLevel();
        verify(p_channelTransferRuleVO).getCntrlTransferLevel();
        verify(p_channelTransferRuleVO).getCntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getCreatedBy();
        verify(p_channelTransferRuleVO).getDirectTransferAllowed();
        verify(p_channelTransferRuleVO).getDomainCode();
        verify(p_channelTransferRuleVO).getDpAllowed();
        verify(p_channelTransferRuleVO).getFixedReturnCategory();
        verify(p_channelTransferRuleVO).getFixedReturnLevel();
        verify(p_channelTransferRuleVO).getFixedTransferCategory();
        verify(p_channelTransferRuleVO).getFixedTransferLevel();
        verify(p_channelTransferRuleVO).getFixedWithdrawCategory();
        verify(p_channelTransferRuleVO).getFixedWithdrawLevel();
        verify(p_channelTransferRuleVO).getFocAllowed();
        verify(p_channelTransferRuleVO).getFocTransferType();
        verify(p_channelTransferRuleVO).getFromCategory();
        verify(p_channelTransferRuleVO).getModifiedBy();
        verify(p_channelTransferRuleVO).getNetworkCode();
        verify(p_channelTransferRuleVO).getParentAssocationAllowed();
        verify(p_channelTransferRuleVO).getPreviousStatus();
        verify(p_channelTransferRuleVO).getRestrictedMsisdnAccess();
        verify(p_channelTransferRuleVO).getRestrictedRechargeAccess();
        verify(p_channelTransferRuleVO).getReturnAllowed();
        verify(p_channelTransferRuleVO).getReturnChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getStatus();
        verify(p_channelTransferRuleVO).getToCategory();
        verify(p_channelTransferRuleVO).getToDomainCode();
        verify(p_channelTransferRuleVO).getTransferAllowed();
        verify(p_channelTransferRuleVO).getTransferChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getTransferRuleID();
        verify(p_channelTransferRuleVO).getTransferType();
        verify(p_channelTransferRuleVO).getType();
        verify(p_channelTransferRuleVO).getUncntrlReturnAllowed();
        verify(p_channelTransferRuleVO).getUncntrlReturnLevel();
        verify(p_channelTransferRuleVO).getUncntrlTransferAllowed();
        verify(p_channelTransferRuleVO).getUncntrlTransferLevel();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawAllowed();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getWithdrawAllowed();
        verify(p_channelTransferRuleVO).getWithdrawChnlBypassAllowed();
        verify(p_channelTransferRuleVO, atLeast(1)).getProductArray();
        verify(p_channelTransferRuleVO).getCreatedOn();
        verify(p_channelTransferRuleVO).getModifiedOn();
        verify(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        verify(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        verify(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#addChannelTrfRule(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testAddChannelTrfRule5() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        ChannelTransferRuleVO p_channelTransferRuleVO = mock(ChannelTransferRuleVO.class);
        when(p_channelTransferRuleVO.getFirstApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getSecondApprovalLimit()).thenReturn(42L);
        when(p_channelTransferRuleVO.getApprovalRequired()).thenReturn("Approval Required");
        when(p_channelTransferRuleVO.getCntrlReturnLevel()).thenReturn("Cntrl Return Level");
        when(p_channelTransferRuleVO.getCntrlTransferLevel()).thenReturn("Cntrl Transfer Level");
        when(p_channelTransferRuleVO.getCntrlWithdrawLevel()).thenReturn("Cntrl Withdraw Level");
        when(p_channelTransferRuleVO.getCreatedBy()).thenReturn("Jan 1, 2020 8:00am GMT+0100");
        when(p_channelTransferRuleVO.getDirectTransferAllowed()).thenReturn("Direct Transfer Allowed");
        when(p_channelTransferRuleVO.getDomainCode()).thenReturn("Domain Code");
        when(p_channelTransferRuleVO.getDpAllowed()).thenReturn("Dp Allowed");
        when(p_channelTransferRuleVO.getFixedReturnCategory()).thenReturn("Fixed Return Category");
        when(p_channelTransferRuleVO.getFixedReturnLevel()).thenReturn("Fixed Return Level");
        when(p_channelTransferRuleVO.getFixedTransferCategory()).thenReturn("Fixed Transfer Category");
        when(p_channelTransferRuleVO.getFixedTransferLevel()).thenReturn("Fixed Transfer Level");
        when(p_channelTransferRuleVO.getFixedWithdrawCategory()).thenReturn("Fixed Withdraw Category");
        when(p_channelTransferRuleVO.getFixedWithdrawLevel()).thenReturn("Fixed Withdraw Level");
        when(p_channelTransferRuleVO.getFocAllowed()).thenReturn("Foc Allowed");
        when(p_channelTransferRuleVO.getFocTransferType()).thenReturn("Foc Transfer Type");
        when(p_channelTransferRuleVO.getFromCategory()).thenReturn("jane.doe@example.org");
        when(p_channelTransferRuleVO.getModifiedBy()).thenReturn("Jan 1, 2020 9:00am GMT+0100");
        when(p_channelTransferRuleVO.getNetworkCode()).thenReturn("Network Code");
        when(p_channelTransferRuleVO.getParentAssocationAllowed()).thenReturn("Parent Assocation Allowed");
        when(p_channelTransferRuleVO.getPreviousStatus()).thenReturn("Previous Status");
        when(p_channelTransferRuleVO.getRestrictedMsisdnAccess()).thenReturn("Restricted Msisdn Access");
        when(p_channelTransferRuleVO.getRestrictedRechargeAccess()).thenReturn("Restricted Recharge Access");
        when(p_channelTransferRuleVO.getReturnAllowed()).thenReturn("Return Allowed");
        when(p_channelTransferRuleVO.getReturnChnlBypassAllowed()).thenReturn("Return Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getStatus()).thenReturn("Status");
        when(p_channelTransferRuleVO.getToCategory()).thenReturn("To Category");
        when(p_channelTransferRuleVO.getToDomainCode()).thenReturn("To Domain Code");
        when(p_channelTransferRuleVO.getTransferAllowed()).thenReturn("Transfer Allowed");
        when(p_channelTransferRuleVO.getTransferChnlBypassAllowed()).thenReturn("Transfer Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getTransferRuleID()).thenReturn("Transfer Rule ID");
        when(p_channelTransferRuleVO.getTransferType()).thenReturn("Transfer Type");
        when(p_channelTransferRuleVO.getType()).thenReturn("Type");
        when(p_channelTransferRuleVO.getUncntrlReturnAllowed()).thenReturn("Uncntrl Return Allowed");
        when(p_channelTransferRuleVO.getUncntrlReturnLevel()).thenReturn("Uncntrl Return Level");
        when(p_channelTransferRuleVO.getUncntrlTransferAllowed()).thenReturn("Uncntrl Transfer Allowed");
        when(p_channelTransferRuleVO.getUncntrlTransferLevel()).thenReturn("Uncntrl Transfer Level");
        when(p_channelTransferRuleVO.getUncntrlWithdrawAllowed()).thenReturn("Uncntrl Withdraw Allowed");
        when(p_channelTransferRuleVO.getUncntrlWithdrawLevel()).thenReturn("Uncntrl Withdraw Level");
        when(p_channelTransferRuleVO.getWithdrawAllowed()).thenReturn("Withdraw Allowed");
        when(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed()).thenReturn("Withdraw Chnl Bypass Allowed");
        when(p_channelTransferRuleVO.getProductArray()).thenReturn(new String[]{"Product Array"});
        when(p_channelTransferRuleVO.getCreatedOn()).thenReturn(null);
        when(p_channelTransferRuleVO.getModifiedOn())
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        doNothing().when(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        doNothing().when(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        doNothing().when(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        doNothing().when(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setType(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        doNothing().when(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.addChannelTrfRule(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(p_channelTransferRuleVO).getFirstApprovalLimit();
        verify(p_channelTransferRuleVO).getSecondApprovalLimit();
        verify(p_channelTransferRuleVO).getApprovalRequired();
        verify(p_channelTransferRuleVO).getCntrlReturnLevel();
        verify(p_channelTransferRuleVO).getCntrlTransferLevel();
        verify(p_channelTransferRuleVO).getCntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getCreatedBy();
        verify(p_channelTransferRuleVO).getDirectTransferAllowed();
        verify(p_channelTransferRuleVO).getDomainCode();
        verify(p_channelTransferRuleVO).getDpAllowed();
        verify(p_channelTransferRuleVO).getFixedReturnCategory();
        verify(p_channelTransferRuleVO).getFixedReturnLevel();
        verify(p_channelTransferRuleVO).getFixedTransferCategory();
        verify(p_channelTransferRuleVO).getFixedTransferLevel();
        verify(p_channelTransferRuleVO).getFixedWithdrawCategory();
        verify(p_channelTransferRuleVO).getFixedWithdrawLevel();
        verify(p_channelTransferRuleVO).getFocAllowed();
        verify(p_channelTransferRuleVO).getFocTransferType();
        verify(p_channelTransferRuleVO).getFromCategory();
        verify(p_channelTransferRuleVO).getModifiedBy();
        verify(p_channelTransferRuleVO).getNetworkCode();
        verify(p_channelTransferRuleVO).getParentAssocationAllowed();
        verify(p_channelTransferRuleVO).getPreviousStatus();
        verify(p_channelTransferRuleVO).getRestrictedMsisdnAccess();
        verify(p_channelTransferRuleVO).getRestrictedRechargeAccess();
        verify(p_channelTransferRuleVO).getReturnAllowed();
        verify(p_channelTransferRuleVO).getReturnChnlBypassAllowed();
        verify(p_channelTransferRuleVO).getStatus();
        verify(p_channelTransferRuleVO).getToCategory();
        verify(p_channelTransferRuleVO).getToDomainCode();
        verify(p_channelTransferRuleVO).getTransferAllowed();
        verify(p_channelTransferRuleVO).getTransferChnlBypassAllowed();
        verify(p_channelTransferRuleVO, atLeast(1)).getTransferRuleID();
        verify(p_channelTransferRuleVO).getTransferType();
        verify(p_channelTransferRuleVO).getType();
        verify(p_channelTransferRuleVO).getUncntrlReturnAllowed();
        verify(p_channelTransferRuleVO).getUncntrlReturnLevel();
        verify(p_channelTransferRuleVO).getUncntrlTransferAllowed();
        verify(p_channelTransferRuleVO).getUncntrlTransferLevel();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawAllowed();
        verify(p_channelTransferRuleVO).getUncntrlWithdrawLevel();
        verify(p_channelTransferRuleVO).getWithdrawAllowed();
        verify(p_channelTransferRuleVO).getWithdrawChnlBypassAllowed();
        verify(p_channelTransferRuleVO, atLeast(1)).getProductArray();
        verify(p_channelTransferRuleVO).getCreatedOn();
        verify(p_channelTransferRuleVO).getModifiedOn();
        verify(p_channelTransferRuleVO).setApprovalRequired(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setCreatedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setDirectTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setDpAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFirstApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setFixedReturnCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFixedWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFocTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setFromSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setLastModifiedTime(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setModifiedBy(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setModifiedOn(Mockito.<Date>any());
        verify(p_channelTransferRuleVO).setNetworkCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setParentAssocationAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setPreviousStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setProductArray(Mockito.<String[]>any());
        verify(p_channelTransferRuleVO).setProductVOList(Mockito.<ArrayList<Object>>any());
        verify(p_channelTransferRuleVO).setRestrictedMsisdnAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRestrictedRechargeAccess(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setReturnChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setSecondApprovalLimit(Mockito.<Long>any());
        verify(p_channelTransferRuleVO).setStatus(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setStatusDesc(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategory(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToCategoryDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainCode(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToDomainDes(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setToSeqNo(Mockito.<Integer>any());
        verify(p_channelTransferRuleVO).setTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferChnlBypassAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleID(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferRuleType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setTransferType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setType(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlReturnLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferAllowedTmp(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlTransferLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setUncntrlWithdrawLevel(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawAllowed(Mockito.<String>any());
        verify(p_channelTransferRuleVO).setWithdrawChnlBypassAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadProductVONewList(Connection, String)}
     */
    @Test
    public void testLoadProductVONewList() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(2, channelTransferRuleWebDAO.loadProductVONewList(JUnitConfig.getConnection(), "P transfer Rule ID").size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadProductVONewList(Connection, String)}
     */
    @Test
    public void testLoadProductVONewList2() throws BTSLBaseException, SQLException {
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
        channelTransferRuleWebDAO.loadProductVONewList(JUnitConfig.getConnection(), "P transfer Rule ID");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadApprovalChannelTrfRuleNewVOList(Connection, String, String)}
     */
    @Test
    public void testLoadApprovalChannelTrfRuleNewVOList() throws BTSLBaseException, SQLException {
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
                channelTransferRuleWebDAO.loadApprovalChannelTrfRuleNewVOList(JUnitConfig.getConnection(), "P status", "P rule Type").size());
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
     * Method under test: {@link ChannelTransferRuleWebDAO#loadC2CTransferRuleNewVOList(Connection, String, String, String, String, String, String)}
     */
    @Test
    public void testLoadC2CTransferRuleNewVOList() throws BTSLBaseException, SQLException {
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
                        .loadC2CTransferRuleNewVOList(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org", "P to Domain Code",
                                "P status", "P rule Type", "P return Allowed")
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
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRuleReject(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRuleReject() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.modifyChannelTransferRuleReject(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRuleReject(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRuleReject2() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.modifyChannelTransferRuleReject(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRuleReject(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRuleReject3() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).clearParameters();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.modifyChannelTransferRuleReject(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeUpdate();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement).clearParameters();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRuleReject(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRuleReject4() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(0, channelTransferRuleWebDAO.modifyChannelTransferRuleReject(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRuleReject(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRuleReject5() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(0, channelTransferRuleWebDAO.modifyChannelTransferRuleReject(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#modifyChannelTransferRuleReject(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testModifyChannelTransferRuleReject6() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(mock(Timestamp.class));
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).setLong(anyInt(), anyLong());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.modifyChannelTransferRuleReject(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setLong(anyInt(), anyLong());
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#checkUserUnderToCategory(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testCheckUserUnderToCategory() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertTrue(channelTransferRuleWebDAO.checkUserUnderToCategory(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#checkUserUnderToCategory(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testCheckUserUnderToCategory2() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.checkUserUnderToCategory(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#checkUserUnderToCategory(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testCheckUserUnderToCategory3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertFalse(channelTransferRuleWebDAO.checkUserUnderToCategory(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#requestChannelTransferRuleDeletion(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testRequestChannelTransferRuleDeletion() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals(1, channelTransferRuleWebDAO.requestChannelTransferRuleDeletion(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#requestChannelTransferRuleDeletion(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testRequestChannelTransferRuleDeletion2() throws BTSLBaseException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doThrow(new SQLException()).when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.requestChannelTransferRuleDeletion(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#checkUserUnderFromCategory(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testCheckUserUnderFromCategory() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertTrue(channelTransferRuleWebDAO.checkUserUnderFromCategory(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#checkUserUnderFromCategory(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testCheckUserUnderFromCategory2() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.checkUserUnderFromCategory(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#checkUserUnderFromCategory(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testCheckUserUnderFromCategory3() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertFalse(channelTransferRuleWebDAO.checkUserUnderFromCategory(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#getTransferRuleID(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testGetTransferRuleID() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals("String", channelTransferRuleWebDAO.getTransferRuleID(JUnitConfig.getConnection(), p_channelTransferRuleVO));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#getTransferRuleID(Connection, ChannelTransferRuleVO)}
     */
    @Test
    public void testGetTransferRuleID2() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ChannelTransferRuleVO p_channelTransferRuleVO = new ChannelTransferRuleVO();
        p_channelTransferRuleVO.setApprovalRequired("Approval Required");
        p_channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        p_channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        p_channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        p_channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        p_channelTransferRuleVO.setDomainCode("Domain Code");
        p_channelTransferRuleVO.setDpAllowed("Dp Allowed");
        p_channelTransferRuleVO.setFirstApprovalLimit(42L);
        p_channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        p_channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        p_channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        p_channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        p_channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        p_channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        p_channelTransferRuleVO.setFocAllowed("Foc Allowed");
        p_channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        p_channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        p_channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        p_channelTransferRuleVO.setFromSeqNo(1);
        p_channelTransferRuleVO.setLastModifiedTime(1L);
        p_channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_channelTransferRuleVO.setNetworkCode("Network Code");
        p_channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        p_channelTransferRuleVO.setPreviousStatus("Previous Status");
        p_channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        p_channelTransferRuleVO.setProductVOList(new ArrayList());
        p_channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        p_channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        p_channelTransferRuleVO.setReturnAllowed("Return Allowed");
        p_channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        p_channelTransferRuleVO.setRuleType("Rule Type");
        p_channelTransferRuleVO.setSecondApprovalLimit(42L);
        p_channelTransferRuleVO.setStatus("Status");
        p_channelTransferRuleVO.setStatusDesc("Status Desc");
        p_channelTransferRuleVO.setToCategory("To Category");
        p_channelTransferRuleVO.setToCategoryDes("To Category Des");
        p_channelTransferRuleVO.setToDomainCode("To Domain Code");
        p_channelTransferRuleVO.setToDomainDes("To Domain Des");
        p_channelTransferRuleVO.setToSeqNo(1);
        p_channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        p_channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        p_channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        p_channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        p_channelTransferRuleVO.setTransferType("Transfer Type");
        p_channelTransferRuleVO.setType("Type");
        p_channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        p_channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        p_channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        p_channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        p_channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        p_channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        p_channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        p_channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        p_channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        thrown.expect(BTSLBaseException.class);
        channelTransferRuleWebDAO.getTransferRuleID(JUnitConfig.getConnection(), p_channelTransferRuleVO);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryListForO2C(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryListForO2C() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
                        .loadTransferRulesCategoryListForO2C(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org", "P foc Allowed",
                                "P transfer Type")
                        .size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryListForO2C(Connection, String, String, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryListForO2C2() throws BTSLBaseException, SQLException {
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
        channelTransferRuleWebDAO.loadTransferRulesCategoryListForO2C(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org",
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
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTrfRuleCatListForRestrictedMsisdn(Connection, String, String, boolean, boolean)}
     */
    @Test
    public void testLoadTrfRuleCatListForRestrictedMsisdn() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
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
                        .loadTrfRuleCatListForRestrictedMsisdn(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org", true, true)
                        .size());
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTrfRuleCatListForRestrictedMsisdn(Connection, String, String, boolean, boolean)}
     */
    @Test
    public void testLoadTrfRuleCatListForRestrictedMsisdn2() throws BTSLBaseException, SQLException {
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
        channelTransferRuleWebDAO.loadTrfRuleCatListForRestrictedMsisdn(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org",
                true, true);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryList(Connection, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryList() throws BTSLBaseException, SQLException {
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
                channelTransferRuleWebDAO.loadTransferRulesCategoryList(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org")
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
     * Method under test: {@link ChannelTransferRuleWebDAO#loadTransferRulesCategoryList(Connection, String, String)}
     */
    @Test
    public void testLoadTransferRulesCategoryList2() throws BTSLBaseException, SQLException {
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
        channelTransferRuleWebDAO.loadTransferRulesCategoryList(JUnitConfig.getConnection(), "P network Code", "jane.doe@example.org");
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }
}

