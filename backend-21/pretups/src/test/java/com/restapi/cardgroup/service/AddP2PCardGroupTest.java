package com.restapi.cardgroup.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.util.JUnitConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class AddP2PCardGroupTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link AddP2PCardGroup#addCardGroup(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddCardGroup() throws BTSLBaseException, IOException, SQLException, ParseException {
        JUnitConfig.init();
        AddP2PCardGroup addP2PCardGroup = null;
        String requestData = "";

        // Act
        PretupsResponse<JsonNode> actualAddCardGroupResult = addP2PCardGroup.addCardGroup(requestData);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link AddP2PCardGroup#add(CardGroupSetVO, Date, Connection, List, JsonNode, ObjectMapper)}
     */
    @Test
    public void testAdd() throws BTSLBaseException, IOException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
        CardGroupSetVO cardGroupSetVO = mock(CardGroupSetVO.class);
        when(cardGroupSetVO.getApplicableFromDate()).thenReturn("2020-03-01");
        when(cardGroupSetVO.getApplicableFromHour()).thenReturn("jane.doe@example.org");
        Date currentDate = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();
        thrown.expect(BTSLBaseException.class);
        addP2PCardGroup.add(cardGroupSetVO, currentDate, com.btsl.util.JUnitConfig.getConnection(), new ArrayList<>(), mock(JsonNode.class),
                mock(ObjectMapper.class));
        verify(cardGroupSetVO).getApplicableFromDate();
        verify(cardGroupSetVO, atLeast(1)).getApplicableFromHour();
    }

    /**
     * Method under test: {@link AddP2PCardGroup#add(CardGroupSetVO, Date, Connection, List, JsonNode, ObjectMapper)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAdd2() throws BTSLBaseException, IOException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: add
        //       at com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO.getApplicableFromHour(CardGroupSetVO.java:481)
        //       at com.restapi.cardgroup.service.AddP2PCardGroup.add(AddP2PCardGroup.java:238)
        //   See https://diff.blue/R013 to resolve this issue.

        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
        CardGroupSetVO cardGroupSetVO = mock(CardGroupSetVO.class);
        when(cardGroupSetVO.getApplicableFromDate()).thenThrow(new NumberFormatException("add"));
        when(cardGroupSetVO.getApplicableFromHour()).thenThrow(new NumberFormatException("add"));
        Date currentDate = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();
        addP2PCardGroup.add(cardGroupSetVO, currentDate, com.btsl.util.JUnitConfig.getConnection(), new ArrayList<>(), mock(JsonNode.class),
                mock(ObjectMapper.class));
    }

    /**
     * Method under test: {@link AddP2PCardGroup#add(CardGroupSetVO, Date, Connection, List, JsonNode, ObjectMapper)}
     */
    @Test
    public void testAdd3() throws BTSLBaseException, IOException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
        CardGroupSetVO cardGroupSetVO = mock(CardGroupSetVO.class);
        when(cardGroupSetVO.getApplicableFromDate()).thenReturn("2020-03-01");
        when(cardGroupSetVO.getApplicableFromHour()).thenReturn(" ");
        Date currentDate = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        //Connection con = mock(Connection.class);
        thrown.expect(BTSLBaseException.class);
        com.btsl.util.JUnitConfig.init();
        addP2PCardGroup.add(cardGroupSetVO, currentDate, com.btsl.util.JUnitConfig.getConnection(), new ArrayList<>(), mock(JsonNode.class),
                mock(ObjectMapper.class));
        verify(cardGroupSetVO).getApplicableFromDate();
        verify(cardGroupSetVO).getApplicableFromHour();
    }

    /**
     * Method under test: {@link AddP2PCardGroup#loadData(Connection, CardGroupSetVO)}
     */
    @Test
    public void testLoadData() throws BTSLBaseException, SQLException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
        cardGroupSetVO.setAmountTypeList(new ArrayList<>());
        cardGroupSetVO.setApplicableFromDate("2020-03-01");
        cardGroupSetVO.setApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setCardGroupID("Card Group ID");
        cardGroupSetVO.setCardGroupSetID("Card Group Set ID");
        cardGroupSetVO.setCardGroupSetName("Card Group Set Name");
        cardGroupSetVO.setCardGroupSetNameList(new ArrayList<>());
        cardGroupSetVO.setCardGroupSetVersionList(new ArrayList<>());
        cardGroupSetVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setCreatedOnStr("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO.setDefaultCardGroup("Default Card Group");
        cardGroupSetVO.setLanguage1Message("en");
        cardGroupSetVO.setLanguage2Message("en");
        cardGroupSetVO.setLastModifiedOn(1L);
        cardGroupSetVO.setLastVersion("1.0.2");
        cardGroupSetVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setModifiedOnStr("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO.setModuleCode("Module Code");
        cardGroupSetVO.setNetworkCode("Network Code");
        cardGroupSetVO.setOldApplicableFromDate("2020-03-01");
        cardGroupSetVO.setOldApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setPreviousDefaultCardGroup("Previous Default Card Group");
        cardGroupSetVO.setServiceType("Service Type");
        cardGroupSetVO.setServiceTypeDesc("Servive Type Desc");
        cardGroupSetVO.setSetType("Set Type");
        cardGroupSetVO.setSetTypeList(new ArrayList<>());
        cardGroupSetVO.setSetTypeName("Set Type Name");
        cardGroupSetVO.setStatus("Status");
        cardGroupSetVO.setSubServiceType("Sub Service Type");
        cardGroupSetVO.setSubServiceTypeDescription("Sub Service Type Description");
        cardGroupSetVO.setSubServiceTypeList(new ArrayList<>());
        cardGroupSetVO.setValidityTypeList(new ArrayList<>());
        cardGroupSetVO.setVersion("1.0.2");
        /*addP2PCardGroup.loadData(JUnitConfig.getConnection(), cardGroupSetVO);
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
        assertTrue(cardGroupSetVO.getAmountTypeList().isEmpty());
        assertTrue(cardGroupSetVO.getValidityTypeList().isEmpty());
        assertTrue(cardGroupSetVO.getSubServiceTypeList().isEmpty());
        assertEquals("String", cardGroupSetVO.getServiceType());
    }

    /**
     * Method under test: {@link AddP2PCardGroup#loadData(Connection, CardGroupSetVO)}
     */
    @Test
    public void testLoadData2() throws BTSLBaseException, SQLException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new NumberFormatException("AMTYP"));
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       */ //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        com.btsl.util.JUnitConfig.init();
        CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
        cardGroupSetVO.setAmountTypeList(new ArrayList<>());
        cardGroupSetVO.setApplicableFromDate("2020-03-01");
        cardGroupSetVO.setApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setCardGroupID("Card Group ID");
        cardGroupSetVO.setCardGroupSetID("Card Group Set ID");
        cardGroupSetVO.setCardGroupSetName("Card Group Set Name");
        cardGroupSetVO.setCardGroupSetNameList(new ArrayList<>());
        cardGroupSetVO.setCardGroupSetVersionList(new ArrayList<>());
        cardGroupSetVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setCreatedOnStr("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO.setDefaultCardGroup("Default Card Group");
        cardGroupSetVO.setLanguage1Message("en");
        cardGroupSetVO.setLanguage2Message("en");
        cardGroupSetVO.setLastModifiedOn(1L);
        cardGroupSetVO.setLastVersion("1.0.2");
        cardGroupSetVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setModifiedOnStr("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO.setModuleCode("Module Code");
        cardGroupSetVO.setNetworkCode("Network Code");
        cardGroupSetVO.setOldApplicableFromDate("2020-03-01");
        cardGroupSetVO.setOldApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setPreviousDefaultCardGroup("Previous Default Card Group");
        cardGroupSetVO.setServiceType("Service Type");
        cardGroupSetVO.setServiceTypeDesc("Servive Type Desc");
        cardGroupSetVO.setSetType("Set Type");
        cardGroupSetVO.setSetTypeList(new ArrayList<>());
        cardGroupSetVO.setSetTypeName("Set Type Name");
        cardGroupSetVO.setStatus("Status");
        cardGroupSetVO.setSubServiceType("Sub Service Type");
        cardGroupSetVO.setSubServiceTypeDescription("Sub Service Type Description");
        cardGroupSetVO.setSubServiceTypeList(new ArrayList<>());
        cardGroupSetVO.setValidityTypeList(new ArrayList<>());
        cardGroupSetVO.setVersion("1.0.2");
        addP2PCardGroup.loadData(com.btsl.util.JUnitConfig.getConnection(), cardGroupSetVO);
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();*/
        assertTrue(cardGroupSetVO.getAmountTypeList().isEmpty());
        assertTrue(cardGroupSetVO.getValidityTypeList().isEmpty());
    }

    /**
     * Method under test: {@link AddP2PCardGroup#loadData(Connection, CardGroupSetVO)}
     */
    @Test
    public void testLoadData3() throws BTSLBaseException, SQLException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       */ //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

//        when(com.btsl.util.JUnitConfig.getConnection().prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
        cardGroupSetVO.setAmountTypeList(new ArrayList<>());
        cardGroupSetVO.setApplicableFromDate("2020-03-01");
        cardGroupSetVO.setApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setCardGroupID("Card Group ID");
        cardGroupSetVO.setCardGroupSetID("Card Group Set ID");
        cardGroupSetVO.setCardGroupSetName("Card Group Set Name");
        cardGroupSetVO.setCardGroupSetNameList(new ArrayList<>());
        cardGroupSetVO.setCardGroupSetVersionList(new ArrayList<>());
        cardGroupSetVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setCreatedOnStr("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO.setDefaultCardGroup("Default Card Group");
        cardGroupSetVO.setLanguage1Message("en");
        cardGroupSetVO.setLanguage2Message("en");
        cardGroupSetVO.setLastModifiedOn(1L);
        cardGroupSetVO.setLastVersion("1.0.2");
        cardGroupSetVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setModifiedOnStr("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO.setModuleCode("Module Code");
        cardGroupSetVO.setNetworkCode("Network Code");
        cardGroupSetVO.setOldApplicableFromDate("2020-03-01");
        cardGroupSetVO.setOldApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setPreviousDefaultCardGroup("Previous Default Card Group");
        cardGroupSetVO.setServiceType("Service Type");
        cardGroupSetVO.setServiceTypeDesc("Servive Type Desc");
        cardGroupSetVO.setSetType("Set Type");
        cardGroupSetVO.setSetTypeList(new ArrayList<>());
        cardGroupSetVO.setSetTypeName("Set Type Name");
        cardGroupSetVO.setStatus("Status");
        cardGroupSetVO.setSubServiceType("Sub Service Type");
        cardGroupSetVO.setSubServiceTypeDescription("Sub Service Type Description");
        cardGroupSetVO.setSubServiceTypeList(new ArrayList<>());
        cardGroupSetVO.setValidityTypeList(new ArrayList<>());
        cardGroupSetVO.setVersion("1.0.2");
        addP2PCardGroup.loadData(com.btsl.util.JUnitConfig.getConnection(), cardGroupSetVO);
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();*/
        assertTrue(cardGroupSetVO.getAmountTypeList().isEmpty());
        assertTrue(cardGroupSetVO.getValidityTypeList().isEmpty());
    }

    /**
     * Method under test: {@link AddP2PCardGroup#loadVoucherData(Connection, CardGroupSetVO, CardGroupDetailsVO)}
     */
    @Test
    public void testLoadVoucherData() throws BTSLBaseException, SQLException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
       /* Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).close();
       */ //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

//        when(com.btsl.util.JUnitConfig.getConnection().prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        CardGroupSetVO cardGroupSetVO = mock(CardGroupSetVO.class);
        CardGroupDetailsVO cardDetailVO = mock(CardGroupDetailsVO.class);
        when(cardDetailVO.getVoucherTypeDesc()).thenReturn("Voucher Type Desc");
        thrown.expect(BTSLBaseException.class);
        addP2PCardGroup.loadVoucherData(com.btsl.util.JUnitConfig.getConnection(), cardGroupSetVO, cardDetailVO);
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        verify(timestamp, atLeast(1)).getTime();*/
        verify(cardDetailVO).getVoucherTypeDesc();
    }

    /**
     * Method under test: {@link AddP2PCardGroup#loadVoucherData(Connection, CardGroupSetVO, CardGroupDetailsVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadVoucherData2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: loadVoucherData
        //       at com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO.getVoucherTypeDesc(CardGroupDetailsVO.java:1660)
        //       at com.restapi.cardgroup.service.AddP2PCardGroup.loadVoucherData(AddP2PCardGroup.java:364)
        //   See https://diff.blue/R013 to resolve this issue.

        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();
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

//        when(com.btsl.util.JUnitConfig.getConnection().prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        CardGroupSetVO cardGroupSetVO = mock(CardGroupSetVO.class);
        CardGroupDetailsVO cardDetailVO = mock(CardGroupDetailsVO.class);
        when(cardDetailVO.getVoucherTypeDesc()).thenThrow(new NumberFormatException("loadVoucherData"));
        addP2PCardGroup.loadVoucherData(com.btsl.util.JUnitConfig.getConnection(), cardGroupSetVO, cardDetailVO);
    }

    /**
     * Method under test: {@link AddP2PCardGroup#modifyCardGroup(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testModifyCardGroup() throws Exception {
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
        //       at com.restapi.cardgroup.service.AddP2PCardGroup.modifyCardGroup(AddP2PCardGroup.java:417)

        // Arrange
        // TODO: Populate arranged inputs
        AddP2PCardGroup addP2PCardGroup = null;
        String requestData = "";

        // Act
        PretupsResponse<JsonNode> actualModifyCardGroupResult = addP2PCardGroup.modifyCardGroup(requestData);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link AddP2PCardGroup#validation(CardGroupSetVO, JsonNode, ObjectMapper, Connection)}
     */
    @Test
    public void testValidation() throws BTSLBaseException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();

        CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
        cardGroupSetVO.setAmountTypeList(new ArrayList<>());
        cardGroupSetVO.setApplicableFromDate("2020-03-01");
        cardGroupSetVO.setApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setCardGroupID("Card Group ID");
        cardGroupSetVO.setCardGroupSetID("Card Group Set ID");
        cardGroupSetVO.setCardGroupSetName("Card Group Set Name");
        cardGroupSetVO.setCardGroupSetNameList(new ArrayList<>());
        cardGroupSetVO.setCardGroupSetVersionList(new ArrayList<>());
        cardGroupSetVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setCreatedOnStr("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO.setDefaultCardGroup("Default Card Group");
        cardGroupSetVO.setLanguage1Message("en");
        cardGroupSetVO.setLanguage2Message("en");
        cardGroupSetVO.setLastModifiedOn(1L);
        cardGroupSetVO.setLastVersion("1.0.2");
        cardGroupSetVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setModifiedOnStr("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO.setModuleCode("Module Code");
        cardGroupSetVO.setNetworkCode("Network Code");
        cardGroupSetVO.setOldApplicableFromDate("2020-03-01");
        cardGroupSetVO.setOldApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setPreviousDefaultCardGroup("Previous Default Card Group");
        cardGroupSetVO.setServiceType("Service Type");
        cardGroupSetVO.setServiceTypeDesc("Servive Type Desc");
        cardGroupSetVO.setSetType("Set Type");
        cardGroupSetVO.setSetTypeList(new ArrayList<>());
        cardGroupSetVO.setSetTypeName("Set Type Name");
        cardGroupSetVO.setStatus("Status");
        cardGroupSetVO.setSubServiceType("Sub Service Type");
        cardGroupSetVO.setSubServiceTypeDescription("Sub Service Type Description");
        cardGroupSetVO.setSubServiceTypeList(new ArrayList<>());
        cardGroupSetVO.setValidityTypeList(new ArrayList<>());
        cardGroupSetVO.setVersion("1.0.2");
        MissingNode cardGroupSetListNode = MissingNode.getInstance();
        thrown.expect(BTSLBaseException.class);
        addP2PCardGroup.validation(cardGroupSetVO, cardGroupSetListNode, new ObjectMapper(), mock(Connection.class));
    }

    /**
     * Method under test: {@link AddP2PCardGroup#validation(CardGroupSetVO, JsonNode, ObjectMapper, Connection)}
     */
    @Test
    public void testValidation2() throws BTSLBaseException {
        AddP2PCardGroup addP2PCardGroup = new AddP2PCardGroup();

        ArrayList<ListValueVO> amountTypeList = new ArrayList<>();
        amountTypeList.add(new ListValueVO("validate", "42"));

        CardGroupSetVO cardGroupSetVO = new CardGroupSetVO();
        cardGroupSetVO.setAmountTypeList(amountTypeList);
        cardGroupSetVO.setApplicableFromDate("2020-03-01");
        cardGroupSetVO.setApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setCardGroupID("Card Group ID");
        cardGroupSetVO.setCardGroupSetID("Card Group Set ID");
        cardGroupSetVO.setCardGroupSetName("Card Group Set Name");
        cardGroupSetVO.setCardGroupSetNameList(new ArrayList<>());
        cardGroupSetVO.setCardGroupSetVersionList(new ArrayList<>());
        cardGroupSetVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setCreatedOnStr("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVO.setDefaultCardGroup("Default Card Group");
        cardGroupSetVO.setLanguage1Message("en");
        cardGroupSetVO.setLanguage2Message("en");
        cardGroupSetVO.setLastModifiedOn(1L);
        cardGroupSetVO.setLastVersion("1.0.2");
        cardGroupSetVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVO.setModifiedOnStr("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVO.setModuleCode("Module Code");
        cardGroupSetVO.setNetworkCode("Network Code");
        cardGroupSetVO.setOldApplicableFromDate("2020-03-01");
        cardGroupSetVO.setOldApplicableFromHour("jane.doe@example.org");
        cardGroupSetVO.setPreviousDefaultCardGroup("Previous Default Card Group");
        cardGroupSetVO.setServiceType("Service Type");
        cardGroupSetVO.setServiceTypeDesc("Servive Type Desc");
        cardGroupSetVO.setSetType("Set Type");
        cardGroupSetVO.setSetTypeList(new ArrayList<>());
        cardGroupSetVO.setSetTypeName("Set Type Name");
        cardGroupSetVO.setStatus("Status");
        cardGroupSetVO.setSubServiceType("Sub Service Type");
        cardGroupSetVO.setSubServiceTypeDescription("Sub Service Type Description");
        cardGroupSetVO.setSubServiceTypeList(new ArrayList<>());
        cardGroupSetVO.setValidityTypeList(new ArrayList<>());
        cardGroupSetVO.setVersion("1.0.2");
        MissingNode cardGroupSetListNode = MissingNode.getInstance();
        thrown.expect(BTSLBaseException.class);
        addP2PCardGroup.validation(cardGroupSetVO, cardGroupSetListNode, new ObjectMapper(), mock(Connection.class));
    }

    /**
     * Method under test: {@link AddP2PCardGroup#checkDeleteVersionAllowed(String, String, StringBuffer)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCheckDeleteVersionAllowed() {
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
        //       at com.restapi.cardgroup.service.AddP2PCardGroup.checkDeleteVersionAllowed(AddP2PCardGroup.java:1222)

        // Arrange
        // TODO: Populate arranged inputs
        AddP2PCardGroup addP2PCardGroup = null;
        String p_cardGroupSetID = "";
        String p_moduleCode = "";
        StringBuffer strBuff = null;

        // Act
        boolean actualCheckDeleteVersionAllowedResult = addP2PCardGroup.checkDeleteVersionAllowed(p_cardGroupSetID,
                p_moduleCode, strBuff);

        // Assert
        // TODO: Add assertions on result
    }
}

