package com.restapi.o2c.service;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.JUnitConfig;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;

public class O2CVoucherTransferServiceTest {
    /**
     * Method under test: {@link O2CVoucherTransferService#o2CValidate(Connection, O2CVoucherTransferReqData, ChannelUserVO, ChannelUserVO, ErrorMap, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2CValidate() throws Exception {
        com.btsl.util.JUnitConfig.init();
       //Connection con = mock(Connection.class);
        O2CVoucherTransferReqData o2CVoucherTransferReqData = new O2CVoucherTransferReqData();
        o2CVoucherTransferReqData.setLoginid2("Test");
        o2CVoucherTransferReqData.setMsisdn2("9999999999");

        List<PaymentDetailsO2C> listt =  new ArrayList<>();
        PaymentDetailsO2C payment = new PaymentDetailsO2C();
        payment.setPaymenttype("CASH");
        payment.setPaymentdate("01/01/23");
        payment.setPaymentinstnumber("12345");

        listt.add(payment) ;

        o2CVoucherTransferReqData.setPaymentDetails(listt);
        o2CVoucherTransferReqData.setRemarks("String");
        o2CVoucherTransferReqData.setPin("1357");

        List<VoucherDetails> vList =  new ArrayList<>();

        VoucherDetails voucher = new VoucherDetails();
        voucher.setVoucherType("String");
        voucher.setDenomination("1000");
        voucher.setFromSerialNo("12345678");
        voucher.setToSerialNo("12345679");
        voucher.setVouchersegment("String");

        vList.add(voucher) ;
        o2CVoucherTransferReqData.setVoucherDetails(vList);

        o2CVoucherTransferReqData.setRefnumber("12345");
        ChannelUserVO senderVO = ChannelUserVO.getInstance();

        UserPhoneVO userPhoneVO= new UserPhoneVO() ;
        userPhoneVO.setCountry("US");
        userPhoneVO.setPhoneLanguage("en");
        senderVO.setUserPhoneVO(userPhoneVO);


        ChannelUserVO receiverVO = ChannelUserVO.getInstance();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        O2CVoucherTransferService.o2CValidate(JUnitConfig.getConnection(), o2CVoucherTransferReqData, senderVO, receiverVO, errorMap,
                new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CVoucherTransferService#o2CValidate(Connection, O2CVoucherTransferReqData, ChannelUserVO, ChannelUserVO, ErrorMap, ArrayList)}
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
        //       at com.restapi.o2c.service.O2CVoucherTransferService.o2CValidate(O2CVoucherTransferService.java:319)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        O2CVoucherTransferReqData o2CVoucherTransferReqData = new O2CVoucherTransferReqData();
        ChannelUserVO senderVO = ChannelUserVO.getInstance();
        senderVO.setUserPhoneVO(UserPhoneVO.getInstance());
        ChannelUserVO receiverVO = ChannelUserVO.getInstance();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        O2CVoucherTransferService.o2CValidate(JUnitConfig.getConnection(), o2CVoucherTransferReqData, senderVO, receiverVO, errorMap,
                new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CVoucherTransferService#o2cService(Connection, ChannelUserVO, O2CVoucherTransferRequestVO, ChannelUserVO, ArrayList, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2cService() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CVoucherTransferService.o2cService(O2CVoucherTransferService.java:619)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        ChannelUserVO receiverVo = mock(ChannelUserVO.class);
        O2CVoucherTransferRequestVO o2cVoucherTransferRequestVO = mock(O2CVoucherTransferRequestVO.class);
        ChannelUserVO senderVO = mock(ChannelUserVO.class);
        O2CVoucherTransferService.o2cService(JUnitConfig.getConnection(), receiverVo, o2cVoucherTransferRequestVO, senderVO, new ArrayList<>(),
                mock(RequestVO.class));
    }

    /**
     * Method under test: {@link O2CVoucherTransferService#processVoucherTransferRequest(O2CVoucherTransferRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessVoucherTransferRequest() {
        com.btsl.util.JUnitConfig.init();
        // Arrange
        // TODO: Populate arranged inputs
        O2CVoucherTransferService o2cVoucherTransferService = new O2CVoucherTransferService();
        O2CVoucherTransferRequestVO o2CVoucherTransferRequestVO = new O2CVoucherTransferRequestVO();
        o2CVoucherTransferRequestVO.setO2CTrasfereReqData(null);
        o2CVoucherTransferRequestVO.setPin("1357");
        o2CVoucherTransferRequestVO.setMsisdn("9999999999");

        HttpServletResponse response1 = mock(HttpServletResponse.class);

        // Act
        BaseResponseMultiple<JsonNode> actualProcessVoucherTransferRequestResult = o2cVoucherTransferService
                .processVoucherTransferRequest(o2CVoucherTransferRequestVO, response1);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link O2CVoucherTransferService#validateVoucher(Connection, ArrayList, List, ArrayList, String, String)}
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
        //       at com.restapi.o2c.service.O2CVoucherTransferService.validateVoucher(O2CVoucherTransferService.java:374)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        ArrayList<VomsBatchVO> vomsBatchlist = new ArrayList<>();
        ArrayList<VoucherDetails> voucherDetailsList = new ArrayList<>();
        O2CVoucherTransferService.validateVoucher(JUnitConfig.getConnection(), vomsBatchlist, voucherDetailsList, new ArrayList<>(), "42",
                "Network Code");
    }
}

