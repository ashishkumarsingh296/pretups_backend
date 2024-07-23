package com.restapi.c2s.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.XssWrapper;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {C2SServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SServiceImplTest {
    @Autowired
    private C2SServiceImpl c2SServiceImpl;

    /**
     * Method under test: {@link C2SServiceImpl#loadDenomination(MvdDenominationResponseVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadDenomination() throws BTSLBaseException {
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
        //       at com.btsl.pretups.inter.util.VOMSVoucherDAO.loadDenominationForBulkVoucherDistribution(VOMSVoucherDAO.java:812)
        //       at com.restapi.c2s.services.C2SServiceImpl.loadDenomination(C2SServiceImpl.java:132)

        // Arrange
        // TODO: Populate arranged inputs
        MvdDenominationResponseVO response = null;

        // Act
        this.c2SServiceImpl.loadDenomination(response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServiceImpl#processRequestDVD(DvdSwaggRequestVO, String, MultiValueMap, HttpServletResponse, HttpServletRequest)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequestDVD() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.C2SServiceImpl.processRequestDVD(C2SServiceImpl.java:209)

        // Arrange
        // TODO: Populate arranged inputs
        DvdSwaggRequestVO requestVO = null;
        String requestIDStr = "";
        MultiValueMap<String, String> headers = null;
        HttpServletResponse responseSwag = null;
        HttpServletRequest httpServletRequest = null;

        // Act
        DvdApiResponse actualProcessRequestDVDResult = this.c2SServiceImpl.processRequestDVD(requestVO, requestIDStr,
                headers, responseSwag, httpServletRequest);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "Ext Nw Code", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode2() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("xx", "Receiver MSISDN", "Ext Nw Code", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode3() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Receiver MSISDN", "Receiver MSISDN", "Ext Nw Code", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode4() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:336)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode(null, "Receiver MSISDN", "Ext Nw Code", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode5() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:336)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "xx", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode6() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "Sender MSISDN", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode7() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "Receiver MSISDN", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode8() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN",
                "C2SServiceImpl:validateMsisdn2 = ", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode9() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", " | getArgs = ", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode10() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "DEFAULT_LANGUAGE", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode11() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "DEFAULT_COUNTRY", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode12() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", null, true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode13() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:336)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "42", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode14() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:336)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", ",", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode15() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:336)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", ".", true);
    }

    /**
     * Method under test: {@link C2SServiceImpl#validateMsisdn2AndNetworkCode(String, String, String, boolean)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2AndNetworkCode16() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.C2SServiceImpl.validateMsisdn2AndNetworkCode(C2SServiceImpl.java:325)
        //   See https://diff.blue/R013 to resolve this issue.

        c2SServiceImpl.validateMsisdn2AndNetworkCode("Sender MSISDN", "Receiver MSISDN", "Ext Nw Code", false);
    }

    /**
     * Method under test: {@link C2SServiceImpl#createCommonReqToProcess(DvdSwaggRequestVO, DvdSwaggVoucherDetails, int)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateCommonReqToProcess() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServiceImpl.createCommonReqToProcess(C2SServiceImpl.java:539)
        //   See https://diff.blue/R013 to resolve this issue.

        DvdSwaggRequestVO requestVO = new DvdSwaggRequestVO();
        requestVO.setDate("2020-03-01");
        requestVO.setExtnwcode("Extnwcode");
        requestVO.setExtrefnum("Extrefnum");
        requestVO.setLanguage1("en");
        requestVO.setLanguage2("en");
        requestVO.setMsisdn2("Msisdn2");
        requestVO.setPin("Pin");
        requestVO.setSelector("Selector");
        requestVO.setSendSms("Send Sms");
        requestVO.setVoucherDetails(new ArrayList<>());

        DvdSwaggVoucherDetails voucherDetails = new DvdSwaggVoucherDetails();
        voucherDetails.setDenomination("Denomination");
        voucherDetails.setQuantity("Quantity");
        voucherDetails.setVoucherProfile("Voucher Profile");
        voucherDetails.setVoucherSegment("Voucher Segment");
        voucherDetails.setVoucherType("Voucher Type");
        c2SServiceImpl.createCommonReqToProcess(requestVO, voucherDetails, 3);
    }

    /**
     * Method under test: {@link C2SServiceImpl#processRequestChannel(DvdRequestVO, HttpServletResponse, HashMap, HttpServletRequest, ArrayList)}
     */
    @Test
    public void testProcessRequestChannel() {
        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        DvdDetails data2 = new DvdDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQuantity("Quantity");
        data2.setRowCount(3);
        data2.setRowSize(3);
        data2.setSelector("Selector");
        data2.setSendSms("Send Sms");
        data2.setUserid("Userid");
        data2.setVoucherprofile("Voucherprofile");
        data2.setVouchersegment("Vouchersegment");
        data2.setVouchertype("Vouchertype");

        DvdRequestVO requestVO1 = new DvdRequestVO();
        requestVO1.setData(data);
        requestVO1.setData(data2);
        requestVO1.setLoginId("42");
        requestVO1.setMsisdn("Msisdn");
        requestVO1.setPassword("iloveyou");
        requestVO1.setPin("Pin");
        requestVO1.setReqGatewayCode("Req Gateway Code");
        requestVO1.setReqGatewayLoginId("42");
        requestVO1.setReqGatewayPassword("iloveyou");
        requestVO1.setReqGatewayType("Req Gateway Type");
        requestVO1.setServicePort("Service Port");
        requestVO1.setSourceType("Source Type");
        //CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        HashMap<String, String> responseBasicDetails = new HashMap<>();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        XssWrapper httpServletRequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        assertNull(c2SServiceImpl.processRequestChannel(requestVO1, response1, responseBasicDetails,
                httpServletRequest, new ArrayList<>()));
    }

    /**
     * Method under test: {@link C2SServiceImpl#processRequestChannel(DvdRequestVO, HttpServletResponse, HashMap, HttpServletRequest, ArrayList)}
     */
    @Test
    public void testProcessRequestChannel2() {
        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        DvdDetails data2 = new DvdDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQuantity("Quantity");
        data2.setRowCount(3);
        data2.setRowSize(3);
        data2.setSelector("Selector");
        data2.setSendSms("Send Sms");
        data2.setUserid("Userid");
        data2.setVoucherprofile("Voucherprofile");
        data2.setVouchersegment("Vouchersegment");
        data2.setVouchertype("Vouchertype");
        DvdRequestVO requestVO1 = mock(DvdRequestVO.class);
        doNothing().when(requestVO1).setData(Mockito.<OAuthUserData>any());
        doNothing().when(requestVO1).setLoginId(Mockito.<String>any());
        doNothing().when(requestVO1).setMsisdn(Mockito.<String>any());
        doNothing().when(requestVO1).setPassword(Mockito.<String>any());
        doNothing().when(requestVO1).setPin(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayType(Mockito.<String>any());
        doNothing().when(requestVO1).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO1).setSourceType(Mockito.<String>any());
        doNothing().when(requestVO1).setData(Mockito.<DvdDetails>any());
        requestVO1.setData(data);
        requestVO1.setData(data2);
        requestVO1.setLoginId("42");
        requestVO1.setMsisdn("Msisdn");
        requestVO1.setPassword("iloveyou");
        requestVO1.setPin("Pin");
        requestVO1.setReqGatewayCode("Req Gateway Code");
        requestVO1.setReqGatewayLoginId("42");
        requestVO1.setReqGatewayPassword("iloveyou");
        requestVO1.setReqGatewayType("Req Gateway Type");
        requestVO1.setServicePort("Service Port");
        requestVO1.setSourceType("Source Type");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        HashMap<String, String> responseBasicDetails = new HashMap<>();
        XssWrapper httpServletRequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        assertNull(c2SServiceImpl.processRequestChannel(requestVO1, response1, responseBasicDetails,
                httpServletRequest, new ArrayList<>()));
        verify(requestVO1).setData(Mockito.<OAuthUserData>any());
        verify(requestVO1).setLoginId(Mockito.<String>any());
        verify(requestVO1).setMsisdn(Mockito.<String>any());
        verify(requestVO1).setPassword(Mockito.<String>any());
        verify(requestVO1).setPin(Mockito.<String>any());
        verify(requestVO1).setReqGatewayCode(Mockito.<String>any());
        verify(requestVO1).setReqGatewayLoginId(Mockito.<String>any());
        verify(requestVO1).setReqGatewayPassword(Mockito.<String>any());
        verify(requestVO1).setReqGatewayType(Mockito.<String>any());
        verify(requestVO1).setServicePort(Mockito.<String>any());
        verify(requestVO1).setSourceType(Mockito.<String>any());
        verify(requestVO1).setData(Mockito.<DvdDetails>any());
    }

    /**
     * Method under test: {@link C2SServiceImpl#processRequestChannel(DvdRequestVO, HttpServletResponse, HashMap, HttpServletRequest, ArrayList)}
     */
    @Test
    public void testProcessRequestChannel3() {
        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        DvdDetails data2 = new DvdDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQuantity("Quantity");
        data2.setRowCount(3);
        data2.setRowSize(3);
        data2.setSelector("Selector");
        data2.setSendSms("Send Sms");
        data2.setUserid("Userid");
        data2.setVoucherprofile("Voucherprofile");
        data2.setVouchersegment("Vouchersegment");
        data2.setVouchertype("Vouchertype");
        DvdRequestVO requestVO1 = mock(DvdRequestVO.class);
        doNothing().when(requestVO1).setData(Mockito.<OAuthUserData>any());
        doNothing().when(requestVO1).setLoginId(Mockito.<String>any());
        doNothing().when(requestVO1).setMsisdn(Mockito.<String>any());
        doNothing().when(requestVO1).setPassword(Mockito.<String>any());
        doNothing().when(requestVO1).setPin(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayType(Mockito.<String>any());
        doNothing().when(requestVO1).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO1).setSourceType(Mockito.<String>any());
        doNothing().when(requestVO1).setData(Mockito.<DvdDetails>any());
        requestVO1.setData(data);
        requestVO1.setData(data2);
        requestVO1.setLoginId("42");
        requestVO1.setMsisdn("Msisdn");
        requestVO1.setPassword("iloveyou");
        requestVO1.setPin("Pin");
        requestVO1.setReqGatewayCode("Req Gateway Code");
        requestVO1.setReqGatewayLoginId("42");
        requestVO1.setReqGatewayPassword("iloveyou");
        requestVO1.setReqGatewayType("Req Gateway Type");
        requestVO1.setServicePort("Service Port");
        requestVO1.setSourceType("Source Type");
       // CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        HashMap<String, String> responseBasicDetails = new HashMap<>();
        XssWrapper httpServletRequest = new XssWrapper(new OwnHttpRequestWrapper(new MockMultipartHttpServletRequest()));
        assertNull(c2SServiceImpl.processRequestChannel(requestVO1, response1, responseBasicDetails,
                httpServletRequest, new ArrayList<>()));
        verify(requestVO1).setData(Mockito.<OAuthUserData>any());
        verify(requestVO1).setLoginId(Mockito.<String>any());
        verify(requestVO1).setMsisdn(Mockito.<String>any());
        verify(requestVO1).setPassword(Mockito.<String>any());
        verify(requestVO1).setPin(Mockito.<String>any());
        verify(requestVO1).setReqGatewayCode(Mockito.<String>any());
        verify(requestVO1).setReqGatewayLoginId(Mockito.<String>any());
        verify(requestVO1).setReqGatewayPassword(Mockito.<String>any());
        verify(requestVO1).setReqGatewayType(Mockito.<String>any());
        verify(requestVO1).setServicePort(Mockito.<String>any());
        verify(requestVO1).setSourceType(Mockito.<String>any());
        verify(requestVO1).setData(Mockito.<DvdDetails>any());
    }

    /**
     * Method under test: {@link C2SServiceImpl#processRequestChannel(DvdRequestVO, HttpServletResponse, HashMap, HttpServletRequest, ArrayList)}
     */
    @Test
    public void testProcessRequestChannel4() {
        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        DvdDetails data2 = new DvdDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQuantity("Quantity");
        data2.setRowCount(3);
        data2.setRowSize(3);
        data2.setSelector("Selector");
        data2.setSendSms("Send Sms");
        data2.setUserid("Userid");
        data2.setVoucherprofile("Voucherprofile");
        data2.setVouchersegment("Vouchersegment");
        data2.setVouchertype("Vouchertype");
        DvdRequestVO requestVO1 = mock(DvdRequestVO.class);
        doNothing().when(requestVO1).setData(Mockito.<OAuthUserData>any());
        doNothing().when(requestVO1).setLoginId(Mockito.<String>any());
        doNothing().when(requestVO1).setMsisdn(Mockito.<String>any());
        doNothing().when(requestVO1).setPassword(Mockito.<String>any());
        doNothing().when(requestVO1).setPin(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(requestVO1).setReqGatewayType(Mockito.<String>any());
        doNothing().when(requestVO1).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO1).setSourceType(Mockito.<String>any());
        doNothing().when(requestVO1).setData(Mockito.<DvdDetails>any());
        requestVO1.setData(data);
        requestVO1.setData(data2);
        requestVO1.setLoginId("42");
        requestVO1.setMsisdn("Msisdn");
        requestVO1.setPassword("iloveyou");
        requestVO1.setPin("Pin");
        requestVO1.setReqGatewayCode("Req Gateway Code");
        requestVO1.setReqGatewayLoginId("42");
        requestVO1.setReqGatewayPassword("iloveyou");
        requestVO1.setReqGatewayType("Req Gateway Type");
        requestVO1.setServicePort("Service Port");
        requestVO1.setSourceType("Source Type");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        HashMap<String, String> responseBasicDetails = new HashMap<>();
        assertNull(c2SServiceImpl.processRequestChannel(requestVO1, response1, responseBasicDetails, null,
                new ArrayList<>()));
        verify(requestVO1).setData(Mockito.<OAuthUserData>any());
        verify(requestVO1).setLoginId(Mockito.<String>any());
        verify(requestVO1).setMsisdn(Mockito.<String>any());
        verify(requestVO1).setPassword(Mockito.<String>any());
        verify(requestVO1).setPin(Mockito.<String>any());
        verify(requestVO1).setReqGatewayCode(Mockito.<String>any());
        verify(requestVO1).setReqGatewayLoginId(Mockito.<String>any());
        verify(requestVO1).setReqGatewayPassword(Mockito.<String>any());
        verify(requestVO1).setReqGatewayType(Mockito.<String>any());
        verify(requestVO1).setServicePort(Mockito.<String>any());
        verify(requestVO1).setSourceType(Mockito.<String>any());
        verify(requestVO1).setData(Mockito.<DvdDetails>any());
    }

    /**
     * Method under test: {@link C2SServiceImpl#parseRequestfromJson(JsonNode, RequestVO, HttpServletRequest)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestfromJson() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:991)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:991)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServiceImpl c2sServiceImpl = new C2SServiceImpl();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO requestVO = mock(RequestVO.class);
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        c2sServiceImpl.parseRequestfromJson(request, requestVO,
                new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())));
    }

    /**
     * Method under test: {@link C2SServiceImpl#parseRequestfromJson(JsonNode, RequestVO, HttpServletRequest)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestfromJson2() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:991)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:991)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServiceImpl c2sServiceImpl = new C2SServiceImpl();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(null);
        RequestVO requestVO = mock(RequestVO.class);
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        c2sServiceImpl.parseRequestfromJson(request, requestVO,
                new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())));
    }

    /**
     * Method under test: {@link C2SServiceImpl#parseRequestfromJson(JsonNode, RequestVO, HttpServletRequest)}
     */
    @Test
    public void testParseRequestfromJson3() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:991)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServiceImpl c2sServiceImpl = new C2SServiceImpl();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO requestVO = mock(RequestVO.class);
        doNothing().when(requestVO).setLogin(Mockito.<String>any());
        doNothing().when(requestVO).setPassword(Mockito.<String>any());
        doNothing().when(requestVO).setRemoteIP(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayType(Mockito.<String>any());
        doNothing().when(requestVO).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO).setSourceType(Mockito.<String>any());
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        PretupsResponse<JsonNode> actualParseRequestfromJsonResult = c2sServiceImpl.parseRequestfromJson(request,
                requestVO, new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())));
        assertTrue(actualParseRequestfromJsonResult.getStatus());
        assertNull(actualParseRequestfromJsonResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode, atLeast(1)).textValue();
        verify(requestVO).getReqContentType();
        verify(requestVO).setLogin(Mockito.<String>any());
        verify(requestVO).setPassword(Mockito.<String>any());
        verify(requestVO).setRemoteIP(Mockito.<String>any());
        verify(requestVO).setRequestGatewayCode(Mockito.<String>any());
        verify(requestVO).setRequestGatewayType(Mockito.<String>any());
        verify(requestVO).setServicePort(Mockito.<String>any());
        verify(requestVO).setSourceType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServiceImpl#parseRequestfromJson(JsonNode, RequestVO, HttpServletRequest)}
     */
    @Test
    public void testParseRequestfromJson4() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:991)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServiceImpl c2sServiceImpl = new C2SServiceImpl();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO requestVO = mock(RequestVO.class);
        doNothing().when(requestVO).setLogin(Mockito.<String>any());
        doNothing().when(requestVO).setPassword(Mockito.<String>any());
        doNothing().when(requestVO).setRemoteIP(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayType(Mockito.<String>any());
        doNothing().when(requestVO).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO).setSourceType(Mockito.<String>any());
        when(requestVO.getReqContentType()).thenReturn(null);
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestfromJsonResult = c2sServiceImpl.parseRequestfromJson(request,
                requestVO, new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())));
        assertTrue(actualParseRequestfromJsonResult.getStatus());
        assertNull(actualParseRequestfromJsonResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode, atLeast(1)).textValue();
        verify(requestVO).getReqContentType();
        verify(requestVO).setLogin(Mockito.<String>any());
        verify(requestVO).setPassword(Mockito.<String>any());
        verify(requestVO).setRemoteIP(Mockito.<String>any());
        verify(requestVO).setReqContentType(Mockito.<String>any());
        verify(requestVO).setRequestGatewayCode(Mockito.<String>any());
        verify(requestVO).setRequestGatewayType(Mockito.<String>any());
        verify(requestVO).setServicePort(Mockito.<String>any());
        verify(requestVO).setSourceType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServiceImpl#parseRequestfromJson(JsonNode, RequestVO, HttpServletRequest)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestfromJson5() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:991)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServiceImpl.parseRequestfromJson(C2SServiceImpl.java:1008)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServiceImpl c2sServiceImpl = new C2SServiceImpl();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO requestVO = mock(RequestVO.class);
        doNothing().when(requestVO).setLogin(Mockito.<String>any());
        doNothing().when(requestVO).setPassword(Mockito.<String>any());
        doNothing().when(requestVO).setRemoteIP(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayType(Mockito.<String>any());
        doNothing().when(requestVO).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO).setSourceType(Mockito.<String>any());
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        c2sServiceImpl.parseRequestfromJson(request, requestVO, null);
    }
}

