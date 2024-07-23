package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2CVoucherApprovalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CVoucherApprovalRequestVO}
     *   <li>{@link C2CVoucherApprovalRequestVO#setData(DataVcApp)}
     *   <li>{@link C2CVoucherApprovalRequestVO#setReqGatewayCode(String)}
     *   <li>{@link C2CVoucherApprovalRequestVO#setReqGatewayLoginId(String)}
     *   <li>{@link C2CVoucherApprovalRequestVO#setReqGatewayPassword(String)}
     *   <li>{@link C2CVoucherApprovalRequestVO#setReqGatewayType(String)}
     *   <li>{@link C2CVoucherApprovalRequestVO#setServicePort(String)}
     *   <li>{@link C2CVoucherApprovalRequestVO#setSourceType(String)}
     *   <li>{@link C2CVoucherApprovalRequestVO#toString()}
     *   <li>{@link C2CVoucherApprovalRequestVO#getData()}
     *   <li>{@link C2CVoucherApprovalRequestVO#getReqGatewayCode()}
     *   <li>{@link C2CVoucherApprovalRequestVO#getReqGatewayLoginId()}
     *   <li>{@link C2CVoucherApprovalRequestVO#getReqGatewayPassword()}
     *   <li>{@link C2CVoucherApprovalRequestVO#getReqGatewayType()}
     *   <li>{@link C2CVoucherApprovalRequestVO#getServicePort()}
     *   <li>{@link C2CVoucherApprovalRequestVO#getSourceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CVoucherApprovalRequestVO actualC2cVoucherApprovalRequestVO = new C2CVoucherApprovalRequestVO();
        DataVcApp data = new DataVcApp();
        data.setExtcode("Extcode");
        data.setExtnwcode("Extnwcode");
        data.setLanguage1("en");
        data.setLanguage2("en");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPaymentinstcode("Paymentinstcode");
        data.setPaymentinstdate("2020-03-01");
        data.setPaymentinstnum("Paymentinstnum");
        data.setPin("Pin");
        data.setRemarks("Remarks");
        data.setStatus("Status");
        data.setTransferId("42");
        data.setType("Type");
        data.setVoucherDetails(new ArrayList<>());
        actualC2cVoucherApprovalRequestVO.setData(data);
        actualC2cVoucherApprovalRequestVO.setReqGatewayCode("Req Gateway Code");
        actualC2cVoucherApprovalRequestVO.setReqGatewayLoginId("42");
        actualC2cVoucherApprovalRequestVO.setReqGatewayPassword("iloveyou");
        actualC2cVoucherApprovalRequestVO.setReqGatewayType("Req Gateway Type");
        actualC2cVoucherApprovalRequestVO.setServicePort("Service Port");
        actualC2cVoucherApprovalRequestVO.setSourceType("Source Type");
        String actualToStringResult = actualC2cVoucherApprovalRequestVO.toString();
        assertSame(data, actualC2cVoucherApprovalRequestVO.getData());
        assertEquals("Req Gateway Code", actualC2cVoucherApprovalRequestVO.getReqGatewayCode());
        assertEquals("42", actualC2cVoucherApprovalRequestVO.getReqGatewayLoginId());
        assertEquals("iloveyou", actualC2cVoucherApprovalRequestVO.getReqGatewayPassword());
        assertEquals("Req Gateway Type", actualC2cVoucherApprovalRequestVO.getReqGatewayType());
        assertEquals("Service Port", actualC2cVoucherApprovalRequestVO.getServicePort());
        assertEquals("Source Type", actualC2cVoucherApprovalRequestVO.getSourceType());
        assertEquals("reqGatewayLoginId = 42data = transferId = 42language2 = enlanguage1entype = Typepaymentinstcode ="
                + " Paymentinstcodepaymentinstdate = 2020-03-01paymentinstnum = PaymentinstnumvoucherDetails ="
                + " []remarksRemarksstatus = Statusextnwcode = Extnwcodemsisdn = Msisdnpin = Pinloginid = Loginidpassword"
                + " = iloveyouextcode = ExtcodesourceTypeSource TypereqGatewayType = Req Gateway TypereqGatewayPassword"
                + " = iloveyouservicePort = Service PortreqGatewayCode = Req Gateway Code", actualToStringResult);
    }

    /**
     * Method under test: default or parameterless constructor of {@link C2CVoucherApprovalRequestVO}
     */
    @Test
    public void testConstructor2() {
        assertNull((new C2CVoucherApprovalRequestVO()).getData().getVoucherDetails());
    }
}

