package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsResponse;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.receiver.AdditionalInfoVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.JUnitConfig;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;
import org.apache.struts.upload.DiskFile;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {O2CServiceImpl.class,
        BaseResponseMultiple.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CServiceImplTest {
    @Autowired
    private BaseResponseMultiple<JsonNode> baseResponseMultiple;

    @Autowired
    private O2CServiceImpl o2CServiceImpl;


    @Test
    public void testConfirmVoucherProductDetalis() throws BTSLBaseException, ParseException {
       //Connection con = mock(Connection.class);
        ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
        ArrayList slabsList = new ArrayList();
        ArrayList vomsProductList = new ArrayList();
        ArrayList vomsCategoryList = new ArrayList();
        ArrayList voucherTypeList = new ArrayList();
        ArrayList<VoucherDetailsApprv> voucherDetails = new ArrayList<>();
        java.util.Date p_date = java.util.Date
                .from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        o2CServiceImpl.confirmVoucherProductDetalis(JUnitConfig.getConnection(), channelTransferVO, slabsList, vomsProductList, vomsCategoryList,
                voucherTypeList, voucherDetails, p_date, "Approval Level", new ArrayList<>(), "Req Accept Reject");
        assertEquals(0L, channelTransferVO.getCommQty());
        assertEquals("", o2CServiceImpl.gatewayType);
        assertEquals("", o2CServiceImpl.gatewayCode);
    }


    @Test
   //@Ignore("TODO: Complete this test")
    public void testConfirmVoucherProductDetalis2() throws BTSLBaseException, ParseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.confirmVoucherProductDetalis(O2CServiceImpl.java:2076)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();

        ArrayList slabsList = new ArrayList();
        slabsList.add("42");
        ArrayList vomsProductList = new ArrayList();
        ArrayList vomsCategoryList = new ArrayList();
        ArrayList voucherTypeList = new ArrayList();
        ArrayList<VoucherDetailsApprv> voucherDetails = new ArrayList<>();
        java.util.Date p_date = java.util.Date
                .from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        o2CServiceImpl.confirmVoucherProductDetalis(JUnitConfig.getConnection(), channelTransferVO, slabsList, vomsProductList, vomsCategoryList,
                voucherTypeList, voucherDetails, p_date, "Approval Level", new ArrayList<>(), "Req Accept Reject");
    }

    /**
     * Method under test: {@link O2CServiceImpl#constructConfirmDetails(Connection, ArrayList, ChannelTransferVO, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructConfirmDetails() throws BTSLBaseException, ParseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        //Connection p_con = mock(Connection.class);
        ArrayList transferItemList = new ArrayList();
        o2CServiceImpl.constructConfirmDetails(JUnitConfig.getConnection(), transferItemList, ChannelTransferVO.getInstance(), "Approval Level2");
    }

    /**
     * Method under test: {@link O2CServiceImpl#constructConfirmDetails(Connection, ArrayList, ChannelTransferVO, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructConfirmDetails2() throws BTSLBaseException, ParseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy p_con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
        ArrayList transferItemList = new ArrayList();
        o2CServiceImpl.constructConfirmDetails(JUnitConfig.getConnection(), transferItemList, ChannelTransferVO.getInstance(),
                "Approval Level2");
    }

    /**
     * Method under test: {@link O2CServiceImpl#constructConfirmDetails(Connection, ArrayList, ChannelTransferVO, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructConfirmDetails3() throws BTSLBaseException, ParseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        Connection p_con = mock(Connection.class);
        o2CServiceImpl.constructConfirmDetails(JUnitConfig.getConnection(), new ArrayList(), null, "Approval Level2");
    }

    /**
     * Method under test: {@link O2CServiceImpl#constructConfirmDetails(Connection, ArrayList, ChannelTransferVO, String)}
     */
    @Test
    public void testConstructConfirmDetails4() throws BTSLBaseException, ParseException {
        Connection p_con = mock(Connection.class);
        ArrayList transferItemList = new ArrayList();
        ChannelTransferVO channelTransferVO = mock(ChannelTransferVO.class);
        when(channelTransferVO.getCommProfileSetId()).thenReturn("42");
        when(channelTransferVO.getCommProfileVersion()).thenReturn("1.0.2");
        when(channelTransferVO.getChannelTransferitemsVOList()).thenReturn(new ArrayList());
        o2CServiceImpl.constructConfirmDetails(JUnitConfig.getConnection(), transferItemList, channelTransferVO, "Approval Level2");
        verify(channelTransferVO).getCommProfileSetId();
        verify(channelTransferVO).getCommProfileVersion();
        verify(channelTransferVO).getChannelTransferitemsVOList();
    }

    /**
     * Method under test: {@link O2CServiceImpl#o2CValidate(Connection, O2CVoucherApprvData, ChannelUserVO, RowErrorMsgLists, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2CValidate() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.o2CValidate(O2CServiceImpl.java:1432)
        //   See https://diff.blue/R013 to resolve this issue.

     //  //Connection con = mock(Connection.class);
        O2CVoucherApprvData o2CVoucherTransferReqData = new O2CVoucherApprvData();
        ChannelUserVO senderVO = new ChannelUserVO();
        UserPhoneVO userPhoneVO = new UserPhoneVO();

        userPhoneVO.setPhoneLanguage("en");
        userPhoneVO.setCountry("US");

        senderVO.setUserPhoneVO(userPhoneVO);

        RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
        rowErrorMsgLists.setMasterErrorList(new ArrayList<>());
        rowErrorMsgLists.setRowErrorMsgList(new ArrayList<>());
        rowErrorMsgLists.setRowName("Row Name");
        rowErrorMsgLists.setRowValue("42");
        O2CServiceImpl.o2CValidate(com.btsl.util.JUnitConfig.getConnection(), o2CVoucherTransferReqData, senderVO, rowErrorMsgLists, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#o2CValidate(Connection, O2CVoucherApprvData, ChannelUserVO, RowErrorMsgLists, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2CValidate2() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CServiceImpl.o2CValidate(O2CServiceImpl.java:1432)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        O2CVoucherApprvData o2CVoucherTransferReqData = new O2CVoucherApprvData();
        ChannelUserVO senderVO = ChannelUserVO.getInstance();
        senderVO.setUserPhoneVO(UserPhoneVO.getInstance());

        RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
        rowErrorMsgLists.setMasterErrorList(new ArrayList<>());
        rowErrorMsgLists.setRowErrorMsgList(new ArrayList<>());
        rowErrorMsgLists.setRowName("Row Name");
        rowErrorMsgLists.setRowValue("42");
        O2CServiceImpl.o2CValidate(JUnitConfig.getConnection(), o2CVoucherTransferReqData, senderVO, rowErrorMsgLists, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#o2CValidate(Connection, O2CVoucherApprvData, ChannelUserVO, RowErrorMsgLists, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2CValidate3() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.o2CValidate(O2CServiceImpl.java:1458)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        O2CVoucherApprvData o2CVoucherTransferReqData = new O2CVoucherApprvData();
        UserPhoneVO userPhoneVO = mock(UserPhoneVO.class);
        when(userPhoneVO.getCountry()).thenReturn("GB");
        when(userPhoneVO.getPhoneLanguage()).thenReturn("6625550144");
        ChannelUserVO senderVO = ChannelUserVO.getInstance();
        senderVO.setUserPhoneVO(userPhoneVO);

        RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
        rowErrorMsgLists.setMasterErrorList(new ArrayList<>());
        rowErrorMsgLists.setRowErrorMsgList(new ArrayList<>());
        rowErrorMsgLists.setRowName("Row Name");
        rowErrorMsgLists.setRowValue("42");
        O2CServiceImpl.o2CValidate(JUnitConfig.getConnection(), o2CVoucherTransferReqData, senderVO, rowErrorMsgLists, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#o2CValidate(Connection, O2CVoucherApprvData, ChannelUserVO, RowErrorMsgLists, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2CValidate4() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.o2CValidate(O2CServiceImpl.java:1458)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        UserPhoneVO userPhoneVO = mock(UserPhoneVO.class);
        when(userPhoneVO.getCountry()).thenReturn("GB");
        when(userPhoneVO.getPhoneLanguage()).thenReturn("6625550144");
        ChannelUserVO senderVO = ChannelUserVO.getInstance();
        senderVO.setUserPhoneVO(userPhoneVO);

        RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
        rowErrorMsgLists.setMasterErrorList(new ArrayList<>());
        rowErrorMsgLists.setRowErrorMsgList(new ArrayList<>());
        rowErrorMsgLists.setRowName("Row Name");
        rowErrorMsgLists.setRowValue("42");
        O2CServiceImpl.o2CValidate(JUnitConfig.getConnection(), null, senderVO, rowErrorMsgLists, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#o2CValidate(Connection, O2CVoucherApprvData, ChannelUserVO, RowErrorMsgLists, ArrayList)}
     */
    @Test
    public void testO2CValidate5() throws Exception {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(Date.class));
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        O2CVoucherApprvData o2CVoucherTransferReqData = mock(O2CVoucherApprvData.class);
        when(o2CVoucherTransferReqData.getExternalTxnNum()).thenReturn("External Txn Num");
        when(o2CVoucherTransferReqData.getTransferDate()).thenReturn("2020-03-01");
        when(o2CVoucherTransferReqData.getStatus()).thenReturn("Status");
        when(o2CVoucherTransferReqData.getToUserId()).thenReturn("42");
        when(o2CVoucherTransferReqData.getTransactionId()).thenReturn("42");
        when(o2CVoucherTransferReqData.getApprovalLevel()).thenReturn("Approval Level");
        UserPhoneVO userPhoneVO = mock(UserPhoneVO.class);
        when(userPhoneVO.getCountry()).thenReturn("GB");
        when(userPhoneVO.getPhoneLanguage()).thenReturn("6625550144");
        ChannelUserVO senderVO = ChannelUserVO.getInstance();
        senderVO.setUserPhoneVO(userPhoneVO);

        RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
        rowErrorMsgLists.setMasterErrorList(new ArrayList<>());
        rowErrorMsgLists.setRowErrorMsgList(new ArrayList<>());
        rowErrorMsgLists.setRowName("Row Name");
        rowErrorMsgLists.setRowValue("42");
        assertTrue(
                O2CServiceImpl.o2CValidate(JUnitConfig.getConnection(), o2CVoucherTransferReqData, senderVO, rowErrorMsgLists, new ArrayList<>()));
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).getDate(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getLong(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp, atLeast(1)).getTime();
        verify(o2CVoucherTransferReqData, atLeast(1)).getApprovalLevel();
        verify(o2CVoucherTransferReqData).getExternalTxnNum();
        verify(o2CVoucherTransferReqData, atLeast(1)).getStatus();
        verify(o2CVoucherTransferReqData, atLeast(1)).getToUserId();
        verify(o2CVoucherTransferReqData, atLeast(1)).getTransactionId();
        verify(o2CVoucherTransferReqData, atLeast(1)).getTransferDate();
        verify(userPhoneVO).getCountry();
        verify(userPhoneVO).getPhoneLanguage();
    }

    /**
     * Method under test: {@link O2CServiceImpl#o2CValidate(Connection, O2CVoucherApprvData, ChannelUserVO, RowErrorMsgLists, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2CValidate6() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CServiceImpl.o2CValidate(O2CServiceImpl.java:1432)
        //   See https://diff.blue/R013 to resolve this issue.

        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(Date.class));
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        O2CVoucherApprvData o2CVoucherTransferReqData = mock(O2CVoucherApprvData.class);
        when(o2CVoucherTransferReqData.getExternalTxnNum()).thenReturn("External Txn Num");
        when(o2CVoucherTransferReqData.getTransferDate()).thenReturn("2020-03-01");
        when(o2CVoucherTransferReqData.getStatus()).thenReturn("Status");
        when(o2CVoucherTransferReqData.getToUserId()).thenReturn("42");
        when(o2CVoucherTransferReqData.getTransactionId()).thenReturn("42");
        when(o2CVoucherTransferReqData.getApprovalLevel()).thenReturn("Approval Level");
        UserPhoneVO userPhoneVO = mock(UserPhoneVO.class);
        when(userPhoneVO.getCountry()).thenReturn("GB");
        when(userPhoneVO.getPhoneLanguage()).thenReturn("6625550144");
        ChannelUserVO senderVO = mock(ChannelUserVO.class);
        when(senderVO.getUserPhoneVO()).thenReturn(UserPhoneVO.getInstance());
        doNothing().when(senderVO).setUserPhoneVO(Mockito.<UserPhoneVO>any());
        senderVO.setUserPhoneVO(userPhoneVO);

        RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
        rowErrorMsgLists.setMasterErrorList(new ArrayList<>());
        rowErrorMsgLists.setRowErrorMsgList(new ArrayList<>());
        rowErrorMsgLists.setRowName("Row Name");
        rowErrorMsgLists.setRowValue("42");
        O2CServiceImpl.o2CValidate(JUnitConfig.getConnection(), o2CVoucherTransferReqData, senderVO, rowErrorMsgLists, new ArrayList<>());
    }


    @Test
   //@Ignore("TODO: Complete this test")
    public void testOrderReturnedProcessStart() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(ChannelTransferBL.java:633)
        //       at com.restapi.o2c.service.O2CServiceImpl.orderReturnedProcessStart(O2CServiceImpl.java:355)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection p_con = mock(Connection.class);
        ChannelTransferVO p_channelTransferVO = ChannelTransferVO.getInstance();
        o2CServiceImpl.orderReturnedProcessStart(JUnitConfig.getConnection(), p_channelTransferVO, "42",
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()),
                "P forward Path");
    }


    @Test
   //@Ignore("TODO: Complete this test")
    public void testOrderReturnedProcessStart2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(ChannelTransferBL.java:633)
        //       at com.restapi.o2c.service.O2CServiceImpl.orderReturnedProcessStart(O2CServiceImpl.java:355)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy p_con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
        ChannelTransferVO p_channelTransferVO = ChannelTransferVO.getInstance();
        o2CServiceImpl.orderReturnedProcessStart(JUnitConfig.getConnection(), p_channelTransferVO, "42",
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()),
                "P forward Path");
    }


    @Test
   //@Ignore("TODO: Complete this test")
    public void testPrepareChannelTransferVO() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.prepareChannelTransferVO(O2CServiceImpl.java:669)
        //   See https://diff.blue/R013 to resolve this issue.

        AdditionalInfoVO additionalInfoVO = new AdditionalInfoVO();
        additionalInfoVO.setBalance("Balance");
        additionalInfoVO.setCurrency("GBP");
        additionalInfoVO.setGraceDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        additionalInfoVO.setTopupAmount(1L);
        additionalInfoVO.setValidityDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        RequestVO p_requestVO = new RequestVO();
        p_requestVO.setAction("Action");
        p_requestVO.setActionValue(42);
        p_requestVO.setActiverUserId("42");
        p_requestVO.setActualMessageFormat("Actual Message Format");
        p_requestVO.setAdditionalInfoVO(additionalInfoVO);
        p_requestVO.setAmount1(10L);
        p_requestVO.setAmount1String("10");
        p_requestVO.setAmount2(10L);
        p_requestVO.setAmount2String("10");
        p_requestVO.setC2sTotaltxnCount(3L);
        p_requestVO.setCategoryCode("Category Code");
        p_requestVO.setCellId("42");
        p_requestVO.setChannelTransferVO(ChannelTransferVO.getInstance());
        p_requestVO.setChannelTransfersList(new ArrayList<>());
        p_requestVO.setCommission(" commission");
        p_requestVO.setCommissionApplicable("Applicable");
        p_requestVO.setCommissionType("Type");
        p_requestVO.setConfirmNewPassword("iloveyou");
        p_requestVO.setConsumed("Consumed");
        p_requestVO.setCosTime(1L);
        p_requestVO.setCosValTime(42L);
        p_requestVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_requestVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_requestVO.setCreditINRespCode("Resp Code");
        p_requestVO.setCreditTime(1L);
        p_requestVO.setCreditValTime(42L);
        p_requestVO.setCreditedAmount("10");
        p_requestVO.setCurrentLoyaltyPoints("Current Loyalty Points");
        p_requestVO.setCurrentStatus("Current Status");
        p_requestVO.setCvv(" cvv");
        p_requestVO.setDays(1L);
        p_requestVO.setDecreaseGroupTypeCounter(true);
        p_requestVO.setDecreaseLoadCounters(true);
        p_requestVO.setDecreaseNetworkLoadCounters(true);
        p_requestVO.setDecryptedMessage("Decrypted Message");
        p_requestVO.setDueAmt("Due Amt");
        p_requestVO.setDueDate("2020-03-01");
        p_requestVO.setEmailId("42");
        p_requestVO.setEmployeeCode("Employee Code");
        p_requestVO.setEncryptionKey("Key");
        p_requestVO.setEnquiryAmount("10");
        p_requestVO.setEnquiryItemList(new ArrayList());
        p_requestVO.setEnquiryServiceType("Enquiry Service Type");
        p_requestVO.setEnquirySubService(" enquiry Sub Service");
        p_requestVO.setEvdPin("Evd Pin");
        p_requestVO.setExecutedUpto(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_requestVO.setExpiryChangeReason("Just cause");
        p_requestVO.setExpiryDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_requestVO.setExternalNetworkCode("External Network Code");
        p_requestVO.setExternalReferenceNum("Reference Num");
        p_requestVO.setExternalTransactionDate("2020-03-01");
        p_requestVO.setExternalTransactionNum("External Transaction Num");
        p_requestVO.setFilteredMSISDN("Filtered MSISDN");
        p_requestVO.setFixedInformationVO("Fixed Information VO");
        p_requestVO.setFixedLineQty("Fixed Line Qty");
        p_requestVO.setFromDate("2020-03-01");
        p_requestVO.setFromRow("jane.doe@example.org");
        p_requestVO.setGifterLocale(Locale.getDefault());
        p_requestVO.setGifterMSISDN("Gifter MSISDN");
        p_requestVO.setGifterName("Gifter Name");
        p_requestVO.setGroupType("Group Type");
        p_requestVO.setHexUrlEncodedRequired(true);
        p_requestVO.setImei(" imei");
        p_requestVO.setImsi("Imsi");
        p_requestVO.setInCreditURL("https://example.org/example");
        p_requestVO.setInValidateURL("https://example.org/example");
        p_requestVO.setIncomingSmsStr("Incoming Sms Str");
        p_requestVO.setInfo1("Info1");
        p_requestVO.setInfo10("Info10");
        p_requestVO.setInfo2("Info2");
        p_requestVO.setInfo3("Info3");
        p_requestVO.setInfo4("Info4");
        p_requestVO.setInfo5("Info5");
        p_requestVO.setInfo6("Info6");
        p_requestVO.setInfo7("Info7");
        p_requestVO.setInfo8("Info8");
        p_requestVO.setInfo9("Info9");
        p_requestVO.setInstanceID("Instance ID");
        p_requestVO.setIntMsisdnNotFound("Int Msisdn Not Found");
        p_requestVO.setInterfaceCatRoutingDone(true);
        p_requestVO.setInvoiceSize(3);
        p_requestVO.setInvoiceno("Invoiceno");
        p_requestVO.setIsStaffUser(true);
        p_requestVO.setJsonReponse(new PretupsResponse<>());
        p_requestVO.setJwtToken("ABC123");
        p_requestVO.setLanguage1("en");
        p_requestVO.setLastTransfer(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_requestVO.setListName("List Name");
        p_requestVO.setLocale(Locale.getDefault());
        p_requestVO.setLogin("Login");
        p_requestVO.setMcdFailRecords("Mcd Fail Records");
        p_requestVO.setMcdListAddCount(3);
        p_requestVO.setMcdListAmount(1L);
        p_requestVO.setMcdListAmountString("10");
        p_requestVO.setMcdListName("Mcd List Name");
        p_requestVO.setMcdListStatus("Mcd List Status");
        p_requestVO.setMcdNextScheduleDate("2020-03-01");
        p_requestVO.setMcdNoOfSchedules("Mcd No Of Schedules");
        p_requestVO.setMcdPIn("Mcd PIn");
        p_requestVO.setMcdReceiverProfile("Mcd Receiver Profile");
        p_requestVO.setMcdScheduleType("Mcd Schedule Type");
        p_requestVO.setMcdSenderProfile("Mcd Sender Profile");
        p_requestVO.setMessageAlreadyParsed(true);
        p_requestVO.setMessageArguments(new String[]{"Message Arguments"});
        p_requestVO.setMessageCode("Message Code");
        p_requestVO.setMessageGatewayVO(new MessageGatewayVO());
        p_requestVO.setMessageSentMsisdn("Message Sent Msisdn");
        p_requestVO.setMinAmtDue("Min Amt Due");
        p_requestVO.setMobileLineQty("Mobile Line Qty");
        p_requestVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_requestVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_requestVO.setModule("Module");
        p_requestVO.setMsgResponseType("Msg Response Type");
        p_requestVO.setMsisdn("Msisdn");
        p_requestVO.setNetworkCode("Network Code");
        p_requestVO.setNetworkCodeFor("Network Code For");
        p_requestVO.setNewExpiryDate("Expiry");
        p_requestVO.setNewNickName(" new Nick Name");
        p_requestVO.setNewPassword("iloveyou");
        p_requestVO.setNickName(" nick Name");
        p_requestVO.setNotificationMSISDN("Notification MSISDN");
        p_requestVO.setNumberOfRegisteredCards(1);
        p_requestVO.setOTP(true);
        p_requestVO.setParam1("Iccid");
        p_requestVO.setParentID("Parent ID");
        p_requestVO.setParentMsisdnPOS("Parent Msisdn POS");
        p_requestVO.setPassword("iloveyou");
        p_requestVO.setPasswordValidationRequired(true);
        p_requestVO.setPaymentDate("2020-03-01");
        p_requestVO.setPaymentInstNumber("42");
        p_requestVO.setPaymentType("Payment Type");
        p_requestVO.setPerformIntfceCatRoutingBeforeVal(true);
        p_requestVO.setPin(" pin");
        p_requestVO.setPinValidationRequired(true);
        p_requestVO.setPlainMessage(true);
        p_requestVO.setPosMSISDN("Pos MSISDN");
        p_requestVO.setPosUserMSISDN("Pos User MSISDN");
        p_requestVO.setPostValidationTimeTaken(1L);
        p_requestVO.setPrefixID(1L);
        p_requestVO.setPreviousStatus("Previous Status");
        p_requestVO.setPrivateRechBinMsgAllowed(true);
        p_requestVO.setProductCode("Product Code");
        p_requestVO.setProductQuantityList(new ArrayList());
        p_requestVO.setPromoBonus("Promo Bonus");
        p_requestVO.setPromoTime(1L);
        p_requestVO.setPromoValTime(42L);
        p_requestVO.setPurpose("Purpose");
        p_requestVO.setPushMessage(true);
        p_requestVO.setQuantity("Quantity");
        p_requestVO.setReason("Just cause");
        p_requestVO.setReason1("Just cause");
        p_requestVO.setReason2("Just cause");
        p_requestVO.setReceiverDeletionReqFromSubRouting(true);
        p_requestVO.setReceiverExtCode("Receiver Ext Code");
        p_requestVO.setReceiverInterfaceInfoInDBFound(true);
        p_requestVO.setReceiverLocale(Locale.getDefault());
        p_requestVO.setReceiverLoginID("Receiver Login ID");
        p_requestVO.setReceiverMsisdn("Receiver Msisdn");
        p_requestVO.setReceiverServiceClassId("42");
        p_requestVO.setReceiverSubscriberType("Subscriber Type");
        p_requestVO.setRecmsg("Recmsg");
        p_requestVO.setRedemptionId("42");
        p_requestVO.setReferenceNumber("42");
        p_requestVO.setRefreshToken("ABC123");
        p_requestVO.setRemarks("Remarks");
        p_requestVO.setRemoteIP("Remote IP");
        p_requestVO.setRemotePort(8080);
        p_requestVO.setReqAmount("10");
        p_requestVO.setReqContentType("text/plain");
        p_requestVO.setReqDate("2020-03-01");
        p_requestVO.setReqSelector("Req Selector");
        p_requestVO.setRequestGatewayCode("Request Gateway Code");
        p_requestVO.setRequestGatewayType("Request Gateway Type");
        p_requestVO.setRequestIDStr("Request IDStr");
        p_requestVO.setRequestLoginId("42");
        p_requestVO.setRequestMSISDN("Request MSISDN");
        p_requestVO.setRequestMap(new HashMap());
        p_requestVO.setRequestMessage("Request Message");
        p_requestVO.setRequestMessageArray(new String[]{"Request Message Array"});
        p_requestVO.setRequestMessageOrigStr("Request Message Orig Str");
        p_requestVO.setRequestNetworkCode("Request Network Code");
        p_requestVO.setRequestStartTime(1L);
        p_requestVO.setRequestType("Request Type");
        p_requestVO.setRequestorCategoryCode("Requestor Category Code");
        p_requestVO.setRequestorUserId("42");
        p_requestVO.setResponseMap(new HashMap());
        p_requestVO.setResponseMultiPartpath("Response Multi Partpath");
        p_requestVO.setSelector1("Selector1");
        p_requestVO.setSelector2("Selector2");
        p_requestVO.setSendSms("Send Sms");
        p_requestVO.setSenderDeletionReqFromSubRouting(true);
        p_requestVO.setSenderExternalCode("Sender External Code");
        p_requestVO.setSenderInterfaceInfoInDBFound(true);
        p_requestVO.setSenderLocale(Locale.getDefault());
        p_requestVO.setSenderLoginID("Sender Login ID");
        p_requestVO.setSenderMessageRequired(true);
        p_requestVO.setSenderReturnMessage("Sender Return Message");
        p_requestVO.setSenderVO("Sender VO");
        p_requestVO.setSerialNo("Serial No");
        p_requestVO.setSerialnumber("42");
        p_requestVO.setServiceKeyword("Service Keyword");
        p_requestVO.setServicePort("Service Port");
        p_requestVO.setServiceType("Service Type");
        p_requestVO.setSid("Sid");
        p_requestVO.setSimProfileID("Sim Profile ID");
        p_requestVO.setSlab(true);
        p_requestVO.setSlabAmount("10");
        p_requestVO.setSlabDetails(" slab Details");
        p_requestVO.setSmsDefaultLang("Sms Default Lang");
        p_requestVO.setSmsSecondLang("Sms Second Lang");
        p_requestVO.setSongCode("Song Code");
        p_requestVO.setSourceType("Source Type");
        p_requestVO.setSsn("123-45-678");
        p_requestVO.setState(" state");
        p_requestVO.setStatus("Status");
        p_requestVO.setSubscriberType("Subscriber Type");
        p_requestVO.setSuccessTxn(true);
        p_requestVO.setSwitchId("42");
        p_requestVO.setTalkTime("Time");
        p_requestVO.setTempTransID("Temp Trans ID");
        p_requestVO.setToBeProcessedFromQueue(true);
        p_requestVO.setToDate("2020-03-01");
        p_requestVO.setToRow("To Row");
        p_requestVO.setToken("ABC123");
        p_requestVO.setTokenLastUsedDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_requestVO.setTopUPReceiverRequestSent(1L);
        p_requestVO.setTopUPReceiverResponseReceived(1L);
        p_requestVO.setTopUPSenderRequestSent(1L);
        p_requestVO.setTopUPSenderResponseReceived(1L);
        p_requestVO.setTransactionID("Transaction ID");
        p_requestVO.setTransactionStatus("Transaction Status");
        p_requestVO.setTransferVOList(new ArrayList());
        p_requestVO.setTxnAuthStatus("Auth Status");
        p_requestVO.setTxnBatchId("42");
        p_requestVO.setTxnDate("2020-03-01");
        p_requestVO.setTxnType("Txntype");
        p_requestVO.setType("Type");
        p_requestVO.setUDH("Udh");
        p_requestVO.setUDHHex("0123456789ABCDEF");
        p_requestVO.setUnmarkSenderRequired(true);
        p_requestVO.setUnmarkSenderUnderProcess(true);
        p_requestVO.setUseInterfaceLanguage("en");
        p_requestVO.setUserCategory("User Category");
        p_requestVO.setUserLoginId("42");
        p_requestVO.setUssdSessionID("Ussd Session ID");
        p_requestVO.setValINRespCode("Resp Code");
        p_requestVO.setValidMSISDN(" validmsisdn");
        p_requestVO.setValidatePassword("iloveyou");
        p_requestVO.setValidatePin("Pin");
        p_requestVO.setValidationReceiverRequestSent(1L);
        p_requestVO.setValidationReceiverResponseReceived(1L);
        p_requestVO.setValidationSenderRequestSent(1L);
        p_requestVO.setValidationSenderResponseReceived(1L);
        p_requestVO.setValidity(" validity");
        p_requestVO.setValueObject("Value Object");
        p_requestVO.setVomsError("An error occurred");
        p_requestVO.setVomsMessage("Voms Message");
        p_requestVO.setVomsRegion("us-east-2");
        p_requestVO.setVomsValid("Voms Valid");
        p_requestVO.setVomsVoucherList(new ArrayList<>());
        p_requestVO.setVoucherAmount(1L);
        p_requestVO.setVoucherCode("Code");
        p_requestVO.setVoucherProfile("Voucher Profile");
        p_requestVO.setVoucherSegment("Voucher Segment");
        p_requestVO.setVoucherStatus("Voucherstatus");
        p_requestVO.setVoucherType("Type");
        p_requestVO.setXCodTipoServicio("Cod Tipo Servicio");
        p_requestVO.setXOpcionRecaudacion("Opcion Recaudacion");
        p_requestVO.setmHash("M Hash");
        ChannelTransferVO p_channelTransferVO = ChannelTransferVO.getInstance();
        java.util.Date p_curDate = java.util.Date
                .from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        ChannelUserVO loginUserVO = ChannelUserVO.getInstance();
        ChannelUserVO p_channelUserVO = ChannelUserVO.getInstance();
        o2CServiceImpl.prepareChannelTransferVO(p_requestVO, p_channelTransferVO, p_curDate, loginUserVO, p_channelUserVO,
                new ArrayList());
    }

    /**
     * Method under test: {@link O2CServiceImpl#processO2CProductDownlaod(Connection, ChannelUserVO, O2CProductsResponseVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CProductDownlaod() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.processO2CProductDownlaod(O2CServiceImpl.java:1366)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection p_con = mock(Connection.class);
        ChannelUserVO p_userVO = ChannelUserVO.getInstance();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CProductsResponseVO p_response = new O2CProductsResponseVO();
        p_response.setErrorMap(errorMap);
        p_response.setMessage("Not all who wander are lost");
        p_response.setMessageCode("Message Code");
        p_response.setProductsList(new ArrayList<>());
        p_response.setReferenceId(1);
        p_response.setService("Service");
        p_response.setStatus("Status");
        p_response.setSuccessList(new ArrayList<>());
        o2CServiceImpl.processO2CProductDownlaod(JUnitConfig.getConnection(), p_userVO, p_response);
    }

    /**
     * Method under test: {@link O2CServiceImpl#processO2CProductDownlaod(Connection, ChannelUserVO, O2CProductsResponseVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CProductDownlaod2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.processO2CProductDownlaod(O2CServiceImpl.java:1370)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection p_con = mock(Connection.class);

        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setAgentAgentAllowed("'");
        categoryVO.setAgentAllowed("'");
        categoryVO.setAgentAllowedFlag("'");
        categoryVO.setAgentCategoryCode("'");
        categoryVO.setAgentCategoryName("'");
        categoryVO.setAgentCategoryStatus("'");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("'");
        categoryVO.setAgentCheckArray(new String[]{"'"});
        categoryVO.setAgentCp2pPayee("'");
        categoryVO.setAgentCp2pPayer("'");
        categoryVO.setAgentCp2pWithinList("'");
        categoryVO.setAgentDisplayAllowed("'");
        categoryVO.setAgentDomainCodeforCategory("'");
        categoryVO.setAgentDomainName("'");
        categoryVO.setAgentFixedRoles("'");
        categoryVO.setAgentGatewayName("'");
        categoryVO.setAgentGatewayType("'");
        categoryVO.setAgentGeographicalDomainList("'");
        categoryVO.setAgentGrphDomainType("'");
        categoryVO.setAgentHierarchyAllowed("'");
        categoryVO.setAgentLowBalAlertAllow("'");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("'");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("'");
        categoryVO.setAgentMultipleGrphDomains("'");
        categoryVO.setAgentMultipleLoginAllowed("'");
        categoryVO.setAgentOutletsAllowed("'");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("'");
        categoryVO.setAgentRechargeByParentOnly("'");
        categoryVO.setAgentRestrictedMsisdns("'");
        categoryVO.setAgentRoleName("'");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("'");
        categoryVO.setAgentServiceAllowed("'");
        categoryVO.setAgentSmsInterfaceAllowed("'");
        categoryVO.setAgentUnctrlTransferAllowed("'");
        categoryVO.setAgentUserIdPrefix("'");
        categoryVO.setAgentViewOnNetworkBlock("'");
        categoryVO.setAgentWebInterfaceAllowed("'");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("'");
        categoryVO.setCategoryCode("'");
        categoryVO.setCategoryName("'");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("'");
        categoryVO.setCategoryType("'");
        categoryVO.setCategoryTypeCode("'");
        categoryVO.setCp2pPayee("'");
        categoryVO.setCp2pPayer("'");
        categoryVO.setCp2pWithinList("'");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("'");
        categoryVO.setDomainAllowed("'");
        categoryVO.setDomainCodeforCategory("'");
        categoryVO.setDomainName("'");
        categoryVO.setDomainTypeCode("'");
        categoryVO.setFixedDomains("'");
        categoryVO.setFixedRoles("'");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("'");
        categoryVO.setGrphDomainTypeName("'");
        categoryVO.setHierarchyAllowed("'");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("'");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("'");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("'");
        categoryVO.setMultipleGrphDomains("'");
        categoryVO.setMultipleLoginAllowed("'");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("'");
        categoryVO.setParentCategoryCode("'");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("'");
        categoryVO.setProductTypeAssociationAllowed("'");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("'");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("'");
        categoryVO.setScheduledTransferAllowed("'");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("'");
        categoryVO.setSmsInterfaceAllowed("'");
        categoryVO.setTransferToListOnly("'");
        categoryVO.setTxnOutsideHierchy("'");
        categoryVO.setUnctrlTransferAllowed("'");
        categoryVO.setUserIdPrefix("'");
        categoryVO.setViewOnNetworkBlock("'");
        categoryVO.setWebInterfaceAllowed("'");
        ChannelUserVO p_userVO = ChannelUserVO.getInstance();
        p_userVO.setCategoryVO(categoryVO);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CProductsResponseVO p_response = new O2CProductsResponseVO();
        p_response.setErrorMap(errorMap);
        p_response.setMessage("Not all who wander are lost");
        p_response.setMessageCode("Message Code");
        p_response.setProductsList(new ArrayList<>());
        p_response.setReferenceId(1);
        p_response.setService("Service");
        p_response.setStatus("Status");
        p_response.setSuccessList(new ArrayList<>());
        o2CServiceImpl.processO2CProductDownlaod(JUnitConfig.getConnection(), p_userVO, p_response);
    }

    /**
     * Method under test: {@link O2CServiceImpl#processO2CProductDownlaod(Connection, ChannelUserVO, O2CProductsResponseVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CProductDownlaod3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.processO2CProductDownlaod(O2CServiceImpl.java:1370)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection p_con = mock(Connection.class);
        CategoryVO categoryVO = mock(CategoryVO.class);
        when(categoryVO.getProductTypeAllowed()).thenReturn("Product Type Allowed");
        doNothing().when(categoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(categoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(categoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(categoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedOn(Mockito.<java.util.Date>any());
        doNothing().when(categoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(categoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setLastModifiedTime(anyLong());
        doNothing().when(categoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(categoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setModifiedOn(Mockito.<java.util.Date>any());
        doNothing().when(categoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(categoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setRadioIndex(anyInt());
        doNothing().when(categoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setRecordCount(anyInt());
        doNothing().when(categoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSequenceNumber(anyInt());
        doNothing().when(categoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(categoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setWebInterfaceAllowed(Mockito.<String>any());
        categoryVO.setAgentAgentAllowed("'");
        categoryVO.setAgentAllowed("'");
        categoryVO.setAgentAllowedFlag("'");
        categoryVO.setAgentCategoryCode("'");
        categoryVO.setAgentCategoryName("'");
        categoryVO.setAgentCategoryStatus("'");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("'");
        categoryVO.setAgentCheckArray(new String[]{"'"});
        categoryVO.setAgentCp2pPayee("'");
        categoryVO.setAgentCp2pPayer("'");
        categoryVO.setAgentCp2pWithinList("'");
        categoryVO.setAgentDisplayAllowed("'");
        categoryVO.setAgentDomainCodeforCategory("'");
        categoryVO.setAgentDomainName("'");
        categoryVO.setAgentFixedRoles("'");
        categoryVO.setAgentGatewayName("'");
        categoryVO.setAgentGatewayType("'");
        categoryVO.setAgentGeographicalDomainList("'");
        categoryVO.setAgentGrphDomainType("'");
        categoryVO.setAgentHierarchyAllowed("'");
        categoryVO.setAgentLowBalAlertAllow("'");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("'");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("'");
        categoryVO.setAgentMultipleGrphDomains("'");
        categoryVO.setAgentMultipleLoginAllowed("'");
        categoryVO.setAgentOutletsAllowed("'");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("'");
        categoryVO.setAgentRechargeByParentOnly("'");
        categoryVO.setAgentRestrictedMsisdns("'");
        categoryVO.setAgentRoleName("'");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("'");
        categoryVO.setAgentServiceAllowed("'");
        categoryVO.setAgentSmsInterfaceAllowed("'");
        categoryVO.setAgentUnctrlTransferAllowed("'");
        categoryVO.setAgentUserIdPrefix("'");
        categoryVO.setAgentViewOnNetworkBlock("'");
        categoryVO.setAgentWebInterfaceAllowed("'");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("'");
        categoryVO.setCategoryCode("'");
        categoryVO.setCategoryName("'");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("'");
        categoryVO.setCategoryType("'");
        categoryVO.setCategoryTypeCode("'");
        categoryVO.setCp2pPayee("'");
        categoryVO.setCp2pPayer("'");
        categoryVO.setCp2pWithinList("'");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("'");
        categoryVO.setDomainAllowed("'");
        categoryVO.setDomainCodeforCategory("'");
        categoryVO.setDomainName("'");
        categoryVO.setDomainTypeCode("'");
        categoryVO.setFixedDomains("'");
        categoryVO.setFixedRoles("'");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("'");
        categoryVO.setGrphDomainTypeName("'");
        categoryVO.setHierarchyAllowed("'");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("'");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("'");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("'");
        categoryVO.setMultipleGrphDomains("'");
        categoryVO.setMultipleLoginAllowed("'");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("'");
        categoryVO.setParentCategoryCode("'");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("'");
        categoryVO.setProductTypeAssociationAllowed("'");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("'");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("'");
        categoryVO.setScheduledTransferAllowed("'");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("'");
        categoryVO.setSmsInterfaceAllowed("'");
        categoryVO.setTransferToListOnly("'");
        categoryVO.setTxnOutsideHierchy("'");
        categoryVO.setUnctrlTransferAllowed("'");
        categoryVO.setUserIdPrefix("'");
        categoryVO.setViewOnNetworkBlock("'");
        categoryVO.setWebInterfaceAllowed("'");
        ChannelUserVO p_userVO = ChannelUserVO.getInstance();
        p_userVO.setCategoryVO(categoryVO);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CProductsResponseVO p_response = new O2CProductsResponseVO();
        p_response.setErrorMap(errorMap);
        p_response.setMessage("Not all who wander are lost");
        p_response.setMessageCode("Message Code");
        p_response.setProductsList(new ArrayList<>());
        p_response.setReferenceId(1);
        p_response.setService("Service");
        p_response.setStatus("Status");
        p_response.setSuccessList(new ArrayList<>());
        o2CServiceImpl.processO2CProductDownlaod(JUnitConfig.getConnection(), p_userVO, p_response);
    }

    /**
     * Method under test: {@link O2CServiceImpl#processVoucherApprvRequest(ChannelUserVO, O2CVoucherApprovalRequestVO, BaseResponseMultiple, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessVoucherApprvRequest() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       com.btsl.common.BaseResponseMultiple
        //   See https://diff.blue/R027 to resolve this issue.

        ChannelUserVO loginUserVO = ChannelUserVO.getInstance();

        O2CVoucherApprovalRequestVO o2CVoucherApprovalRequestVO = new O2CVoucherApprovalRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        o2CVoucherApprovalRequestVO.setData(data);
        o2CVoucherApprovalRequestVO.setLoginId("42");
        o2CVoucherApprovalRequestVO.setMsisdn("Msisdn");
        o2CVoucherApprovalRequestVO.setO2CInitiateReqData(new ArrayList<>());
        o2CVoucherApprovalRequestVO.setPassword("iloveyou");
        o2CVoucherApprovalRequestVO.setPin("Pin");
        o2CVoucherApprovalRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CVoucherApprovalRequestVO.setReqGatewayLoginId("42");
        o2CVoucherApprovalRequestVO.setReqGatewayPassword("iloveyou");
        o2CVoucherApprovalRequestVO.setReqGatewayType("Req Gateway Type");
        o2CVoucherApprovalRequestVO.setServicePort("Service Port");
        o2CVoucherApprovalRequestVO.setSourceType("Source Type");
        o2CServiceImpl.processVoucherApprvRequest(loginUserVO, o2CVoucherApprovalRequestVO, baseResponseMultiple,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CServiceImpl#processWithdrawRequest(ChannelUserVO, O2CWithdrawlRequestVO, BaseResponseMultiple, HttpServletResponse)}
     */
    @Test
    public void testProcessWithdrawRequest() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       com.btsl.common.BaseResponseMultiple
        //   See https://diff.blue/R027 to resolve this issue.

        O2CServiceImpl o2cServiceImpl = new O2CServiceImpl();
        ChannelUserVO loggedUserVO = ChannelUserVO.getInstance();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        O2CWithdrawlRequestVO o2CWithdrawlRequestVO = new O2CWithdrawlRequestVO();
        o2CWithdrawlRequestVO.setData(data);
        o2CWithdrawlRequestVO.setLoginId("42");
        o2CWithdrawlRequestVO.setMsisdn("Msisdn");


        List<O2CWithdrawData> data2 = new ArrayList<>() ;
        O2CWithdrawData obj =  new O2CWithdrawData();
        obj.setPin("1357");
        obj.setRemarks("String");
        obj.setFromUserId("String");
        obj.setWalletType("String");
        List<Products> prods =  new ArrayList<>();

        Products prod = new Products();
        prod.setProductcode("String");
        prod.setQty("1");
        prods.add(prod);

        obj.setProducts(prods);
        data2.add(obj) ;
        o2CWithdrawlRequestVO.setO2CInitiateReqData(data2);



        o2CWithdrawlRequestVO.setPassword("iloveyou");
        o2CWithdrawlRequestVO.setPin("Pin");
        o2CWithdrawlRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CWithdrawlRequestVO.setReqGatewayLoginId("42");
        o2CWithdrawlRequestVO.setReqGatewayPassword("iloveyou");
        o2CWithdrawlRequestVO.setReqGatewayType("Req Gateway Type");
        o2CWithdrawlRequestVO.setServicePort("Service Port");
        o2CWithdrawlRequestVO.setSourceType("Source Type");
        BaseResponseMultiple<JsonNode> apiResponse = new BaseResponseMultiple<>();
        o2cServiceImpl.processWithdrawRequest(loggedUserVO, o2CWithdrawlRequestVO, apiResponse,
                new CustomResponseWrapper(new Response()));
        assertEquals("Req Gateway Type", o2cServiceImpl.gatewayType);
        assertEquals("Req Gateway Code", o2cServiceImpl.gatewayCode);
    }

    /**
     * Method under test: {@link O2CServiceImpl#processWithdrawRequest(ChannelUserVO, O2CWithdrawlRequestVO, BaseResponseMultiple, HttpServletResponse)}
     */
    @Test
    public void testProcessWithdrawRequest2() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       com.btsl.common.BaseResponseMultiple
        //   See https://diff.blue/R027 to resolve this issue.

        O2CServiceImpl o2cServiceImpl = new O2CServiceImpl();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        O2CWithdrawlRequestVO o2CWithdrawlRequestVO = new O2CWithdrawlRequestVO();
        o2CWithdrawlRequestVO.setData(data);
        o2CWithdrawlRequestVO.setLoginId("42");
        o2CWithdrawlRequestVO.setMsisdn("Msisdn");
        o2CWithdrawlRequestVO.setO2CInitiateReqData(new ArrayList<>());
        o2CWithdrawlRequestVO.setPassword("iloveyou");
        o2CWithdrawlRequestVO.setPin("Pin");
        o2CWithdrawlRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CWithdrawlRequestVO.setReqGatewayLoginId("42");
        o2CWithdrawlRequestVO.setReqGatewayPassword("iloveyou");
        o2CWithdrawlRequestVO.setReqGatewayType("Req Gateway Type");
        o2CWithdrawlRequestVO.setServicePort("Service Port");
        o2CWithdrawlRequestVO.setSourceType("Source Type");
        BaseResponseMultiple<JsonNode> apiResponse = new BaseResponseMultiple<>();
        o2cServiceImpl.processWithdrawRequest(null, o2CWithdrawlRequestVO, apiResponse,
                new CustomResponseWrapper(new Response()));
        assertTrue(apiResponse.getSuccessList().isEmpty());
        ErrorMap errorMap = apiResponse.getErrorMap();
        assertNull(errorMap.getRowErrorMsgLists());
        assertNull(errorMap.getMasterErrorList());
        assertEquals("Req Gateway Type", o2cServiceImpl.gatewayType);
        assertEquals("Req Gateway Code", o2cServiceImpl.gatewayCode);
    }

    /**
     * Method under test: {@link O2CServiceImpl#processWithdrawRequest(ChannelUserVO, O2CWithdrawlRequestVO, BaseResponseMultiple, HttpServletResponse)}
     */
    @Test
    public void testProcessWithdrawRequest3() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       com.btsl.common.BaseResponseMultiple
        //   See https://diff.blue/R027 to resolve this issue.

        O2CServiceImpl o2cServiceImpl = new O2CServiceImpl();
        ChannelUserVO loggedUserVO = mock(ChannelUserVO.class);

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        O2CWithdrawlRequestVO o2CWithdrawlRequestVO = new O2CWithdrawlRequestVO();
        o2CWithdrawlRequestVO.setData(data);
        o2CWithdrawlRequestVO.setLoginId("42");
        o2CWithdrawlRequestVO.setMsisdn("Msisdn");
        o2CWithdrawlRequestVO.setO2CInitiateReqData(new ArrayList<>());
        o2CWithdrawlRequestVO.setPassword("iloveyou");
        o2CWithdrawlRequestVO.setPin("Pin");
        o2CWithdrawlRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CWithdrawlRequestVO.setReqGatewayLoginId("42");
        o2CWithdrawlRequestVO.setReqGatewayPassword("iloveyou");
        o2CWithdrawlRequestVO.setReqGatewayType("Req Gateway Type");
        o2CWithdrawlRequestVO.setServicePort("Service Port");
        o2CWithdrawlRequestVO.setSourceType("Source Type");
        BaseResponseMultiple<JsonNode> apiResponse = new BaseResponseMultiple<>();
        o2cServiceImpl.processWithdrawRequest(loggedUserVO, o2CWithdrawlRequestVO, apiResponse,
                new CustomResponseWrapper(new Response()));
        assertTrue(apiResponse.getSuccessList().isEmpty());
        ErrorMap errorMap = apiResponse.getErrorMap();
        assertNull(errorMap.getRowErrorMsgLists());
        assertNull(errorMap.getMasterErrorList());
        assertEquals("Req Gateway Type", o2cServiceImpl.gatewayType);
        assertEquals("Req Gateway Code", o2cServiceImpl.gatewayCode);
    }

    /**
     * Method under test: {@link O2CServiceImpl#processWithdrawRequest(ChannelUserVO, O2CWithdrawlRequestVO, BaseResponseMultiple, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessWithdrawRequest4() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       com.btsl.common.BaseResponseMultiple
        //   See https://diff.blue/R027 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.processWithdrawRequest(O2CServiceImpl.java:132)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CServiceImpl o2cServiceImpl = new O2CServiceImpl();
        ChannelUserVO loggedUserVO = ChannelUserVO.getInstance();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        O2CWithdrawlRequestVO o2CWithdrawlRequestVO = new O2CWithdrawlRequestVO();
        o2CWithdrawlRequestVO.setData(data);
        o2CWithdrawlRequestVO.setLoginId("42");
        o2CWithdrawlRequestVO.setMsisdn("Msisdn");
        o2CWithdrawlRequestVO.setO2CInitiateReqData(new ArrayList<>());
        o2CWithdrawlRequestVO.setPassword("iloveyou");
        o2CWithdrawlRequestVO.setPin("Pin");
        o2CWithdrawlRequestVO.setReqGatewayCode("Req Gateway Code");
        o2CWithdrawlRequestVO.setReqGatewayLoginId("42");
        o2CWithdrawlRequestVO.setReqGatewayPassword("iloveyou");
        o2CWithdrawlRequestVO.setReqGatewayType("Req Gateway Type");
        o2CWithdrawlRequestVO.setServicePort("Service Port");
        o2CWithdrawlRequestVO.setSourceType("Source Type");
        o2cServiceImpl.processWithdrawRequest(loggedUserVO, o2CWithdrawlRequestVO, null,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CServiceImpl#saveVoucherProductDetalis(ArrayList, ChannelTransferVO, int, O2CVoucherApprvData, PaymentDetailsO2C, List, ChannelUserVO, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSaveVoucherProductDetalis() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.o2c.service.O2CServiceImpl.saveVoucherProductDetalis(O2CServiceImpl.java:2529)

        // Arrange
        // TODO: Populate arranged inputs
        ArrayList slabsList = null;
        ChannelTransferVO channelTransferVO = null;
        int r_approvalLevel = 0;
        O2CVoucherApprvData o2CVoucherTransferReqData = null;
        PaymentDetailsO2C paymentDetailsReq = null;
        List<VoucherDetailsApprv> voucherDetails = null;
        ChannelUserVO loginUserVO = null;
        ArrayList channelVoucherItemListTemp = null;

        // Act
        int actualSaveVoucherProductDetalisResult = this.o2CServiceImpl.saveVoucherProductDetalis(slabsList,
                channelTransferVO, r_approvalLevel, o2CVoucherTransferReqData, paymentDetailsReq, voucherDetails, loginUserVO,
                channelVoucherItemListTemp);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link O2CServiceImpl#validatePaymentDetails(PaymentDetailsO2C, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidatePaymentDetails() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CServiceImpl.validatePaymentDetails(O2CServiceImpl.java:3271)
        //   See https://diff.blue/R013 to resolve this issue.

        PaymentDetailsO2C paymentDetails = new PaymentDetailsO2C();
        paymentDetails.setPaymentdate("2020-03-01");
        paymentDetails.setPaymentgatewayType("Paymentgatewaytype");
        paymentDetails.setPaymentinstnumber("42");
        paymentDetails.setPaymenttype("Paymenttype");
        O2CServiceImpl.validatePaymentDetails(paymentDetails, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#validatePaymentDetails(PaymentDetailsO2C, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidatePaymentDetails2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CServiceImpl.validatePaymentDetails(O2CServiceImpl.java:3271)
        //   See https://diff.blue/R013 to resolve this issue.

        PaymentDetailsO2C paymentDetails = mock(PaymentDetailsO2C.class);
        doNothing().when(paymentDetails).setPaymentdate(Mockito.<String>any());
        doNothing().when(paymentDetails).setPaymentgatewayType(Mockito.<String>any());
        doNothing().when(paymentDetails).setPaymentinstnumber(Mockito.<String>any());
        doNothing().when(paymentDetails).setPaymenttype(Mockito.<String>any());
        paymentDetails.setPaymentdate("2020-03-01");
        paymentDetails.setPaymentgatewayType("Paymentgatewaytype");
        paymentDetails.setPaymentinstnumber("42");
        paymentDetails.setPaymenttype("Paymenttype");
        O2CServiceImpl.validatePaymentDetails(paymentDetails, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.validateRequestData(O2CServiceImpl.java:3243)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        Locale locale = Locale.getDefault();
        O2CServiceImpl.validateRequestData(masterErrorListMain, locale, new HashMap<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.validateRequestData(O2CServiceImpl.java:3243)
        //   See https://diff.blue/R013 to resolve this issue.

        MasterErrorList masterErrorList = new MasterErrorList();
        masterErrorList.setErrorCode("An error occurred");
        masterErrorList.setErrorMsg("An error occurred");

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        masterErrorListMain.add(masterErrorList);
        Locale locale = Locale.getDefault();
        O2CServiceImpl.validateRequestData(masterErrorListMain, locale, new HashMap<>());
    }

    /**
     * Method under test: {@link O2CServiceImpl#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData3() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CServiceImpl.validateRequestData(O2CServiceImpl.java:3243)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        Locale locale = Locale.getDefault();

        HashMap<String, Object> reqData = new HashMap<>();
        reqData.put("refNumber", "42");
        O2CServiceImpl.validateRequestData(masterErrorListMain, locale, reqData);
    }

    /**
     * Method under test: {@link O2CServiceImpl#validateVoucher(Connection, ArrayList, List, ArrayList, String, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateVoucher() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CServiceImpl.validateVoucher(O2CServiceImpl.java:1776)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        ArrayList<VomsBatchVO> vomsBatchlist = new ArrayList<>();
        ArrayList<VoucherDetailsApprv> voucherDetailsList = new ArrayList<>();
        O2CServiceImpl.validateVoucher(JUnitConfig.getConnection(), vomsBatchlist, voucherDetailsList, new ArrayList<>(), "42", "Network Code",
                "42");
    }

    /**
     * Method under test: {@link O2CServiceImpl#validateVoucher(Connection, ArrayList, List, ArrayList, String, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateVoucher2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CServiceImpl.validateVoucher(O2CServiceImpl.java:1776)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);

        VomsBatchVO vomsBatchVO = new VomsBatchVO();
        vomsBatchVO.setApprover1Quantity("validateVoucher");
        vomsBatchVO.setApprover2Quantity("validateVoucher");
        vomsBatchVO.setApprvLvl(1);
        vomsBatchVO.setBatchID("validateVoucher");
        vomsBatchVO.setBatchNo("validateVoucher");
        vomsBatchVO.setBatchType("validateVoucher");
        vomsBatchVO.setBatchTypeDesc("validateVoucher");
        vomsBatchVO.setChannelTransferItemsVO(new ChannelTransferItemsVO());
        vomsBatchVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        vomsBatchVO.setCreatedDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        vomsBatchVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        vomsBatchVO.setCreatedOnStr("validateVoucher");
        vomsBatchVO.setDenomination("validateVoucher");
        vomsBatchVO.setDownloadCount(3);
        vomsBatchVO.setDownloadDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        vomsBatchVO.setDownloadOnStr("validateVoucher");
        vomsBatchVO.setExecuteCount(3);
        vomsBatchVO.setExpiryDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        vomsBatchVO.setExpiryPeriod(1);
        vomsBatchVO.setExtTxnNo("validateVoucher");
        vomsBatchVO.setFailCount(3L);
        vomsBatchVO.setFailureCount(3);
        vomsBatchVO.setFile(new DiskFile("/directory/foo.txt"));
        vomsBatchVO.setFileName("foo.txt");
        vomsBatchVO.setFilePresent(true);
        vomsBatchVO.setFirstApprovedBy("validateVoucher");
        vomsBatchVO.setFirstApprovedOn("validateVoucher");
        vomsBatchVO.setFromSerialNo("jane.doe@example.org");
        vomsBatchVO.setInitiatedQuantity("validateVoucher");
        vomsBatchVO.setItemList(new ArrayList());
        vomsBatchVO.setLocationCode("validateVoucher");
        vomsBatchVO.setMasterBatchNo("validateVoucher");
        vomsBatchVO.setMessage("validateVoucher");
        vomsBatchVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        vomsBatchVO.setModifiedDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        vomsBatchVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        vomsBatchVO.setMrp("validateVoucher");
        vomsBatchVO.setNoOfVoucher(1L);
        vomsBatchVO.setOneTimeUsage("validateVoucher");
        vomsBatchVO.setPreFromSerialNo("jane.doe@example.org");
        vomsBatchVO.setPreProductId("validateVoucher");
        vomsBatchVO.setPreQuantity("validateVoucher");
        vomsBatchVO.setPreToSerialNo("validateVoucher");
        vomsBatchVO.setProcess("validateVoucher");
        vomsBatchVO.setProcessScreen(1);
        vomsBatchVO.setProductID("validateVoucher");
        vomsBatchVO.setProductName("validateVoucher");
        vomsBatchVO.setProductid("validateVoucher");
        vomsBatchVO.setProductlist(new ArrayList());
        vomsBatchVO.setQuantity("validateVoucher");
        vomsBatchVO.setRcAdminDaysAllowed(1);
        vomsBatchVO.setReferenceNo("validateVoucher");
        vomsBatchVO.setReferenceType("validateVoucher");
        vomsBatchVO.setRemarks("validateVoucher");
        vomsBatchVO.setRemarksLevel1("validateVoucher");
        vomsBatchVO.setRemarksLevel2("validateVoucher");
        vomsBatchVO.setRemarksLevel3("validateVoucher");
        vomsBatchVO.setRowIndex(1);
        vomsBatchVO.setScheduleCount(3);
        vomsBatchVO.setSecondApprovedBy("validateVoucher");
        vomsBatchVO.setSecondApprovedOn("validateVoucher");
        vomsBatchVO.setSegment("validateVoucher");
        vomsBatchVO.setSeq_id(1);
        vomsBatchVO.setStartSerialNo("validateVoucher");
        vomsBatchVO.setStatus("validateVoucher");
        vomsBatchVO.setStatusDesc("validateVoucher");
        vomsBatchVO.setSuccessCount(3L);
        vomsBatchVO.setTalktime(1);
        vomsBatchVO.setThirdApprovedBy("validateVoucher");
        vomsBatchVO.setThirdApprovedOn("validateVoucher");
        vomsBatchVO.setToSerialNo("validateVoucher");
        vomsBatchVO.setToUserID("validateVoucher");
        vomsBatchVO.setTotalVoucherPerOrder(1L);
        vomsBatchVO.setTransferId("42");
        vomsBatchVO.setUnUsedBatchExists("0123456789ABCDEF");
        vomsBatchVO.setUnUsedBatchList(new ArrayList());
        vomsBatchVO.setUnUsedFromserialNO("jane.doe@example.org");
        vomsBatchVO.setUnUsedToserialNO("validateVoucher");
        vomsBatchVO.setUnUsedVouchers(1L);
        vomsBatchVO.setUsedFromSerialNo("jane.doe@example.org");
        vomsBatchVO.setUsedToSerialNo("validateVoucher");
        vomsBatchVO.setUserMsisdn("validateVoucher");
        vomsBatchVO.setUserName("janedoe");
        vomsBatchVO.setValidity(1);
        vomsBatchVO.setVcrTypeProductlist(new ArrayList());
        vomsBatchVO.setVoucherType("validateVoucher");
        vomsBatchVO.setVouchersegment("validateVoucher");
        vomsBatchVO.set_NetworkCode("validateVoucher");

        ArrayList<VomsBatchVO> vomsBatchlist = new ArrayList<>();
        vomsBatchlist.add(vomsBatchVO);
        ArrayList<VoucherDetailsApprv> voucherDetailsList = new ArrayList<>();
        O2CServiceImpl.validateVoucher(JUnitConfig.getConnection(), vomsBatchlist, voucherDetailsList, new ArrayList<>(), "42", "Network Code",
                "42");
    }
}

