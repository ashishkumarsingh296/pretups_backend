package com.restapi.o2c.service;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
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

public class O2CVoucherInitiateServiceTest {
    /**
     * Method under test: {@link O2CVoucherInitiateService#o2CValidate(Connection, O2CVoucherInitiateReqData, ChannelUserVO, ErrorMap, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testO2CValidate() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CVoucherInitiateService.o2CValidate(O2CVoucherInitiateService.java:254)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        O2CVoucherInitiateReqData o2CVoucherInitiateReqData = new O2CVoucherInitiateReqData();
        ChannelUserVO receiverVO = ChannelUserVO.getInstance();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        O2CVoucherInitiateService.o2CValidate(JUnitConfig.getConnection(), o2CVoucherInitiateReqData, receiverVO, errorMap, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CVoucherInitiateService#o2CValidate(Connection, O2CVoucherInitiateReqData, ChannelUserVO, ErrorMap, ArrayList)}
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
        //       at com.restapi.o2c.service.O2CVoucherInitiateService.o2CValidate(O2CVoucherInitiateService.java:254)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        O2CVoucherInitiateReqData o2CVoucherInitiateReqData = new O2CVoucherInitiateReqData();
        ChannelUserVO receiverVO = ChannelUserVO.getInstance();
        receiverVO.setUserPhoneVO(UserPhoneVO.getInstance());

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        O2CVoucherInitiateService.o2CValidate(JUnitConfig.getConnection(), o2CVoucherInitiateReqData, receiverVO, errorMap, new ArrayList<>());
    }

    /**
     * Method under test: {@link O2CVoucherInitiateService#o2cService(Connection, ChannelUserVO, O2CVoucherInitiateRequestVO, ArrayList, RequestVO)}
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
        //       at com.restapi.o2c.service.O2CVoucherInitiateService.o2cService(O2CVoucherInitiateService.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        ChannelUserVO receiverChannelUserVO = mock(ChannelUserVO.class);
        O2CVoucherInitiateRequestVO o2cVoucherInitiateRequestVO = mock(O2CVoucherInitiateRequestVO.class);
        O2CVoucherInitiateService.o2cService(JUnitConfig.getConnection(), receiverChannelUserVO, o2cVoucherInitiateRequestVO, new ArrayList<>(),
                mock(RequestVO.class));
    }

    /**
     * Method under test: {@link O2CVoucherInitiateService#processVoucherInitiateRequest(O2CVoucherInitiateRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessVoucherInitiateRequest() {
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
        //       at com.restapi.o2c.service.O2CVoucherInitiateService.processVoucherInitiateRequest(O2CVoucherInitiateService.java:113)

        // Arrange
        // TODO: Populate arranged inputs
        O2CVoucherInitiateService o2cVoucherInitiateService = null;
        O2CVoucherInitiateRequestVO o2CVoucherInitiateRequestVO = null;
        HttpServletResponse response1 = null;

        // Act
        BaseResponseMultiple<JsonNode> actualProcessVoucherInitiateRequestResult = o2cVoucherInitiateService
                .processVoucherInitiateRequest(o2CVoucherInitiateRequestVO, response1);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link O2CVoucherInitiateService#validateVoucher(Connection, ArrayList, List, ArrayList, String, String)}
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
        //       at com.restapi.o2c.service.O2CVoucherInitiateService.validateVoucher(O2CVoucherInitiateService.java:287)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        ArrayList<VomsBatchVO> vomsBatchlist = new ArrayList<>();
        ArrayList<VoucherDetailsIni> voucherDetailsList = new ArrayList<>();
        O2CVoucherInitiateService.validateVoucher(JUnitConfig.getConnection(), vomsBatchlist, voucherDetailsList, new ArrayList<>(), "42",
                "Network Code");
    }
}

