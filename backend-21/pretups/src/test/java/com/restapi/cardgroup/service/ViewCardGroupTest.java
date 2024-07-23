package com.restapi.cardgroup.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsReqVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.LoadVersionListRequestVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class ViewCardGroupTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link ViewCardGroup#loadVersionList(LoadVersionListRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadVersionList() throws BTSLBaseException, IOException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.cardgroup.service.ViewCardGroup.loadVersionList(ViewCardGroup.java:81)

        // Arrange
        // TODO: Populate arranged inputs
        ViewCardGroup viewCardGroup = null;
        LoadVersionListRequestVO requestVO = null;

        // Act
        PretupsResponse<List<CardGroupSetVersionVO>> actualLoadVersionListResult = viewCardGroup.loadVersionList(requestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ViewCardGroup#deleteCardGroup(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testDeleteCardGroup() throws BTSLBaseException, IOException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.cardgroup.service.ViewCardGroup.deleteCardGroup(ViewCardGroup.java:170)

        // Arrange
        // TODO: Populate arranged inputs
        ViewCardGroup viewCardGroup = null;
        String requestData = "";

        // Act
        PretupsResponse<JsonNode> actualDeleteCardGroupResult = viewCardGroup.deleteCardGroup(requestData);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ViewCardGroup#viewCardGroupSetDetails(CardGroupDetailsReqVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testViewCardGroupSetDetails() throws BTSLBaseException, IOException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.cardgroup.service.ViewCardGroup.viewCardGroupSetDetails(ViewCardGroup.java:385)

        // Arrange
        // TODO: Populate arranged inputs
        ViewCardGroup viewCardGroup = null;
        CardGroupDetailsReqVO requestData = null;

        // Act
        PretupsResponse<List<CardGroupDetailsVO>> actualViewCardGroupSetDetailsResult = viewCardGroup
                .viewCardGroupSetDetails(requestData);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ViewCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses() throws BTSLBaseException {
        ViewCardGroup viewCardGroup = new ViewCardGroup();
        assertNull(viewCardGroup.arrangeBonuses(new ArrayList(), "P card Group Sub Service ID", true));
    }

    /**
     * Method under test: {@link ViewCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses2() throws BTSLBaseException {
        ViewCardGroup viewCardGroup = new ViewCardGroup();
        ArrayList p_bonusBundleList = new ArrayList();
        ArrayList actualArrangeBonusesResult = viewCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
        assertSame(p_bonusBundleList, actualArrangeBonusesResult);
        assertTrue(actualArrangeBonusesResult.isEmpty());
    }

    /**
     * Method under test: {@link ViewCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses3() throws BTSLBaseException {
        ViewCardGroup viewCardGroup = new ViewCardGroup();

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add("42");
        thrown.expect(BTSLBaseException.class);
        viewCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
    }

    /**
     * Method under test: {@link ViewCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses4() throws BTSLBaseException {
        ViewCardGroup viewCardGroup = new ViewCardGroup();

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add("42");
        p_bonusBundleList.add("42");
        thrown.expect(BTSLBaseException.class);
        viewCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
    }

    /**
     * Method under test: {@link ViewCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses5() throws BTSLBaseException {
        ViewCardGroup viewCardGroup = new ViewCardGroup();

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add("42");
        thrown.expect(BTSLBaseException.class);
        viewCardGroup.arrangeBonuses(p_bonusBundleList, null, true);
    }

    /**
     * Method under test: {@link ViewCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testArrangeBonuses6() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        ViewCardGroup viewCardGroup = null;

        ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
        serviceSelectorMappingVO.setAmountStr("10");
        serviceSelectorMappingVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        serviceSelectorMappingVO.setCreatedOn(mock(java.sql.Date.class));
        serviceSelectorMappingVO.setDefaultCode(true);
        serviceSelectorMappingVO.setDescription("The characteristics of someone or something");
        serviceSelectorMappingVO.setDisplayOrder(":");
        serviceSelectorMappingVO.setDisplayOrderList(-1);
        serviceSelectorMappingVO.setIsDefaultCodeStr(":");
        serviceSelectorMappingVO.setMappingStatus(":");
        serviceSelectorMappingVO.setMappingType(":");
        serviceSelectorMappingVO.setModifiedAllowed(true);
        serviceSelectorMappingVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        serviceSelectorMappingVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        serviceSelectorMappingVO.setNewOrder(":");
        serviceSelectorMappingVO.setNewOrderList(new ArrayList());
        serviceSelectorMappingVO.setRadioIndex(1);
        serviceSelectorMappingVO.setReceiverBundleID(":");
        serviceSelectorMappingVO.setReceiverSubscriberType(":");
        serviceSelectorMappingVO.setSelectorCode(":");
        serviceSelectorMappingVO.setSelectorCount(new ArrayList());
        serviceSelectorMappingVO.setSelectorName(":");
        serviceSelectorMappingVO.setSenderBundleID(":");
        serviceSelectorMappingVO.setSenderSubscriberType(":");
        serviceSelectorMappingVO.setServiceName(":");
        serviceSelectorMappingVO.setServiceType(":");
        serviceSelectorMappingVO.setSno(":");
        serviceSelectorMappingVO.setStatus(":");
        serviceSelectorMappingVO.setStatusDesc(":");
        serviceSelectorMappingVO.setType(":");

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add(serviceSelectorMappingVO);
        viewCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
    }

    /**
     * Method under test: {@link ViewCardGroup#loadVoucherData(Connection, CardGroupSetVO, CardGroupDetailsVO)}
     */
    @Test
    public void testLoadVoucherData() throws BTSLBaseException, SQLException {
        ViewCardGroup viewCardGroup = new ViewCardGroup();
        /*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        CardGroupSetVO cardGroupSetVO = mock(CardGroupSetVO.class);
        CardGroupDetailsVO cardDetailVO = mock(CardGroupDetailsVO.class);
        when(cardDetailVO.getVoucherType()).thenReturn("Voucher Type");
        thrown.expect(BTSLBaseException.class);
        viewCardGroup.loadVoucherData(com.btsl.util.JUnitConfig.getConnection(), cardGroupSetVO, cardDetailVO);
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp, atLeast(1)).getTime();
        */verify(cardDetailVO).getVoucherType();
    }
}

