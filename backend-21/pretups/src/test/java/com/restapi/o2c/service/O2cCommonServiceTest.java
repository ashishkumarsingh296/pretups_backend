package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.MasterErrorList;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.util.JUnitConfig;
import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.junit.Ignore;
import org.junit.Test;

public class O2cCommonServiceTest {
    /**
     * Method under test: {@link O2cCommonService#validatePaymentDetails(PaymentDetailsO2C, ArrayList)}
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
        //       at com.restapi.o2c.service.O2cCommonService.validatePaymentDetails(O2cCommonService.java:63)
        //   See https://diff.blue/R013 to resolve this issue.

        PaymentDetailsO2C paymentDetails = new PaymentDetailsO2C();
        paymentDetails.setPaymentdate("2020-03-01");
        paymentDetails.setPaymentgatewayType("Paymentgatewaytype");
        paymentDetails.setPaymentinstnumber("42");
        paymentDetails.setPaymenttype("Paymenttype");
        O2cCommonService.validatePaymentDetails(paymentDetails, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2cCommonService#validatePaymentDetails(PaymentDetailsO2C, ArrayList)}
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
        //       at com.restapi.o2c.service.O2cCommonService.validatePaymentDetails(O2cCommonService.java:63)
        //   See https://diff.blue/R013 to resolve this issue.

        PaymentDetailsO2C paymentDetails = new PaymentDetailsO2C();
        paymentDetails.setPaymentdate("2020-03-01");
        paymentDetails.setPaymentgatewayType("Paymentgatewaytype");
        paymentDetails.setPaymentinstnumber("42");
        paymentDetails.setPaymenttype("Paymenttype");

        MasterErrorList masterErrorList = new MasterErrorList();
        masterErrorList.setErrorCode("An error occurred");
        masterErrorList.setErrorMsg("An error occurred");

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        masterErrorListMain.add(masterErrorList);
        O2cCommonService.validatePaymentDetails(paymentDetails, masterErrorListMain);
    }

    /**
     * Method under test: {@link O2cCommonService#prepareChannelTransferProfileVO(List, Connection, ChannelUserVO, ArrayList, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPrepareChannelTransferProfileVO() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //       at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        //       at java.util.ArrayList.get(ArrayList.java:435)
        //       at com.restapi.o2c.service.O2cCommonService.prepareChannelTransferProfileVO(O2cCommonService.java:199)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<PaymentDetailsO2C> paymentDetails = new ArrayList<>();
       //Connection con = mock(Connection.class);
        ChannelUserVO p_receiverVO = ChannelUserVO.getInstance();
        ArrayList<ChannelTransferItemsVO> channelTransferItemsList = new ArrayList<>();
        O2cCommonService.prepareChannelTransferProfileVO(paymentDetails, JUnitConfig.getConnection(), p_receiverVO, channelTransferItemsList,
                new ArrayList<>());
    }

    /**
     * Method under test: {@link O2cCommonService#prepareChannelTransferProfileVO(List, Connection, ChannelUserVO, ArrayList, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPrepareChannelTransferProfileVO2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.util.PretupsBL.getFilteredMSISDN(PretupsBL.java:347)
        //       at com.restapi.o2c.service.O2cCommonService.prepareChannelTransferProfileVO(O2cCommonService.java:234)
        //   See https://diff.blue/R013 to resolve this issue.

        PaymentDetailsO2C paymentDetailsO2C = new PaymentDetailsO2C();
        paymentDetailsO2C.setPaymentdate("2020-03-01");
        paymentDetailsO2C.setPaymentgatewayType("Paymentgatewaytype");
        paymentDetailsO2C.setPaymentinstnumber("42");
        paymentDetailsO2C.setPaymenttype("Paymenttype");

        ArrayList<PaymentDetailsO2C> paymentDetails = new ArrayList<>();
        paymentDetails.add(paymentDetailsO2C);
       //Connection con = mock(Connection.class);
        ChannelUserVO p_receiverVO = ChannelUserVO.getInstance();
        ArrayList<ChannelTransferItemsVO> channelTransferItemsList = new ArrayList<>();
        O2cCommonService.prepareChannelTransferProfileVO(paymentDetails, JUnitConfig.getConnection(), p_receiverVO, channelTransferItemsList,
                new ArrayList<>());
    }

    /**
     * Method under test: {@link O2cCommonService#prepareChannelTransferProfileVO(List, Connection, ChannelUserVO, ArrayList, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPrepareChannelTransferProfileVO3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.util.PretupsBL.getFilteredMSISDN(PretupsBL.java:347)
        //       at com.restapi.o2c.service.O2cCommonService.prepareChannelTransferProfileVO(O2cCommonService.java:234)
        //   See https://diff.blue/R013 to resolve this issue.

        PaymentDetailsO2C paymentDetailsO2C = new PaymentDetailsO2C();
        paymentDetailsO2C.setPaymentdate("2020-03-01");
        paymentDetailsO2C.setPaymentgatewayType("Paymentgatewaytype");
        paymentDetailsO2C.setPaymentinstnumber("42");
        paymentDetailsO2C.setPaymenttype("Paymenttype");

        PaymentDetailsO2C paymentDetailsO2C2 = new PaymentDetailsO2C();
        paymentDetailsO2C2.setPaymentdate("2020/03/01");
        paymentDetailsO2C2.setPaymentgatewayType("prepareTransferProfileVO");
        paymentDetailsO2C2.setPaymentinstnumber("dd/MM/yy");
        paymentDetailsO2C2.setPaymenttype("prepareTransferProfileVO");

        ArrayList<PaymentDetailsO2C> paymentDetails = new ArrayList<>();
        paymentDetails.add(paymentDetailsO2C2);
        paymentDetails.add(paymentDetailsO2C);
       //Connection con = mock(Connection.class);
        ChannelUserVO p_receiverVO = ChannelUserVO.getInstance();
        ArrayList<ChannelTransferItemsVO> channelTransferItemsList = new ArrayList<>();
        O2cCommonService.prepareChannelTransferProfileVO(paymentDetails, JUnitConfig.getConnection(), p_receiverVO, channelTransferItemsList,
                new ArrayList<>());
    }

    /**
     * Method under test: {@link O2cCommonService#prepareChannelTransferProfileVO(List, Connection, ChannelUserVO, ArrayList, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPrepareChannelTransferProfileVO4() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.prepareChannelTransferProfileVO(O2cCommonService.java:217)
        //   See https://diff.blue/R013 to resolve this issue.

        PaymentDetailsO2C paymentDetailsO2C = new PaymentDetailsO2C();
        paymentDetailsO2C.setPaymentdate("2020-03-01");
        paymentDetailsO2C.setPaymentgatewayType("Paymentgatewaytype");
        paymentDetailsO2C.setPaymentinstnumber("42");
        paymentDetailsO2C.setPaymenttype("Paymenttype");

        ArrayList<PaymentDetailsO2C> paymentDetails = new ArrayList<>();
        paymentDetails.add(paymentDetailsO2C);
       //Connection con = mock(Connection.class);
        ArrayList<ChannelTransferItemsVO> channelTransferItemsList = new ArrayList<>();
        O2cCommonService.prepareChannelTransferProfileVO(paymentDetails, JUnitConfig.getConnection(), null, channelTransferItemsList,
                new ArrayList<>());
    }

    /**
     * Method under test: {@link O2cCommonService#prepareSMSMessageListForVoucher(Connection, ChannelTransferVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPrepareSMSMessageListForVoucher() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.prepareSMSMessageListForVoucher(O2cCommonService.java:354)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        O2cCommonService.prepareSMSMessageListForVoucher(JUnitConfig.getConnection(), ChannelTransferVO.getInstance());
    }

    /**
     * Method under test: {@link O2cCommonService#prepareSMSMessageListForVoucher(Connection, ChannelTransferVO)}
     */
    @Test
    public void testPrepareSMSMessageListForVoucher2() {
       //Connection con = mock(Connection.class);
        ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
        channelTransferVO.setChannelVoucherItemsVoList(new ArrayList<>());
        assertEquals(1, O2cCommonService.prepareSMSMessageListForVoucher(JUnitConfig.getConnection(), channelTransferVO).length);
    }

    /**
     * Method under test: {@link O2cCommonService#prepareSMSMessageListForVoucher(Connection, ChannelTransferVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPrepareSMSMessageListForVoucher3() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.prepareSMSMessageListForVoucher(O2cCommonService.java:352)
        //   See https://diff.blue/R013 to resolve this issue.

        O2cCommonService.prepareSMSMessageListForVoucher(mock(Connection.class), null);
    }

    /**
     * Method under test: {@link O2cCommonService#filterProductWithTransferRule(ArrayList, ArrayList)}
     */
    @Test
    public void testFilterProductWithTransferRule() {
        ArrayList p_productList = new ArrayList();
        assertTrue(O2cCommonService.filterProductWithTransferRule(p_productList, new ArrayList()).isEmpty());
    }

    /**
     * Method under test: {@link O2cCommonService#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.validateRequestData(O2cCommonService.java:436)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        Locale locale = Locale.getDefault();
        O2cCommonService.validateRequestData(masterErrorListMain, locale, new HashMap<>());
    }

    /**
     * Method under test: {@link O2cCommonService#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.validateRequestData(O2cCommonService.java:436)
        //   See https://diff.blue/R013 to resolve this issue.

        MasterErrorList masterErrorList = new MasterErrorList();
        masterErrorList.setErrorCode("An error occurred");
        masterErrorList.setErrorMsg("An error occurred");

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        masterErrorListMain.add(masterErrorList);
        Locale locale = Locale.getDefault();
        O2cCommonService.validateRequestData(masterErrorListMain, locale, new HashMap<>());
    }

    /**
     * Method under test: {@link O2cCommonService#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData3() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.validateRequestData(O2cCommonService.java:436)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        O2cCommonService.validateRequestData(masterErrorListMain, null, new HashMap<>());
    }

    /**
     * Method under test: {@link O2cCommonService#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData4() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.validateRequestData(O2cCommonService.java:436)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        Locale locale = Locale.getDefault();

        HashMap<String, Object> reqData = new HashMap<>();
        reqData.put("remarks", "42");
        O2cCommonService.validateRequestData(masterErrorListMain, locale, reqData);
    }

    /**
     * Method under test: {@link O2cCommonService#validateRequestData(ArrayList, Locale, HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateRequestData5() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2cCommonService.validateRequestData(O2cCommonService.java:436)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<>();
        Locale locale = Locale.getDefault();

        HashMap<String, Object> reqData = new HashMap<>();
        reqData.put("refNumber", "42");
        reqData.put("remarks", "42");
        O2cCommonService.validateRequestData(masterErrorListMain, locale, reqData);
    }

    /**
     * Method under test: {@link O2cCommonService#sendEmailNotification(Connection, ChannelTransferVO, ChannelTransferDAO, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendEmailNotification() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.btsl.util.BTSLUtil.getSystemLocale(BTSLUtil.java:5375)
        //       at com.btsl.util.BTSLUtil.getSystemLocaleForEmail(BTSLUtil.java:5369)
        //       at com.restapi.o2c.service.O2cCommonService.sendEmailNotification(O2cCommonService.java:460)
        //   See https://diff.blue/R013 to resolve this issue.

       // Connection p_con = mock(Connection.class);
        ChannelTransferVO p_channelTransferVO = ChannelTransferVO.getInstance();
        O2cCommonService.sendEmailNotification(JUnitConfig.getConnection(), p_channelTransferVO, new ChannelTransferDAO(), "P role Code",
                "Hello from the Dreaming Spires");
    }

    /**
     * Method under test: {@link O2cCommonService#sendEmailNotification(Connection, ChannelTransferVO, ChannelTransferDAO, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendEmailNotification2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.btsl.util.BTSLUtil.getSystemLocale(BTSLUtil.java:5375)
        //       at com.btsl.util.BTSLUtil.getSystemLocaleForEmail(BTSLUtil.java:5369)
        //       at com.restapi.o2c.service.O2cCommonService.sendEmailNotification(O2cCommonService.java:460)
        //   See https://diff.blue/R013 to resolve this issue.

       // oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy p_con = mock(
        //        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);


        ChannelTransferVO p_channelTransferVO = ChannelTransferVO.getInstance();
        O2cCommonService.sendEmailNotification(JUnitConfig.getConnection(), p_channelTransferVO, new ChannelTransferDAO(), "P role Code",
                "Hello from the Dreaming Spires");
    }
}

