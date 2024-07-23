package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class C2CVoucherTransferRequestVOTest {
    /**
     * Method under test: {@link C2CVoucherTransferRequestVO#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        C2CVoucherTransferRequestVO c2cVoucherTransferRequestVO = new C2CVoucherTransferRequestVO();
        c2cVoucherTransferRequestVO.setAdditionalProperty("Name", "Value");
        assertEquals(1, c2cVoucherTransferRequestVO.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CVoucherTransferRequestVO}
     *   <li>{@link C2CVoucherTransferRequestVO#setDatabuyvcr(DataVcrTrf)}
     *   <li>{@link C2CVoucherTransferRequestVO#setReqGatewayCode(String)}
     *   <li>{@link C2CVoucherTransferRequestVO#setReqGatewayLoginId(String)}
     *   <li>{@link C2CVoucherTransferRequestVO#setReqGatewayPassword(String)}
     *   <li>{@link C2CVoucherTransferRequestVO#setReqGatewayType(String)}
     *   <li>{@link C2CVoucherTransferRequestVO#setServicePort(String)}
     *   <li>{@link C2CVoucherTransferRequestVO#setSourceType(String)}
     *   <li>{@link C2CVoucherTransferRequestVO#toString()}
     *   <li>{@link C2CVoucherTransferRequestVO#getDatabuyvcr()}
     *   <li>{@link C2CVoucherTransferRequestVO#getReqGatewayCode()}
     *   <li>{@link C2CVoucherTransferRequestVO#getReqGatewayLoginId()}
     *   <li>{@link C2CVoucherTransferRequestVO#getReqGatewayPassword()}
     *   <li>{@link C2CVoucherTransferRequestVO#getReqGatewayType()}
     *   <li>{@link C2CVoucherTransferRequestVO#getServicePort()}
     *   <li>{@link C2CVoucherTransferRequestVO#getSourceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CVoucherTransferRequestVO actualC2cVoucherTransferRequestVO = new C2CVoucherTransferRequestVO();
        DataVcrTrf data = new DataVcrTrf();
        actualC2cVoucherTransferRequestVO.setDatabuyvcr(data);
        actualC2cVoucherTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        actualC2cVoucherTransferRequestVO.setReqGatewayLoginId("42");
        actualC2cVoucherTransferRequestVO.setReqGatewayPassword("iloveyou");
        actualC2cVoucherTransferRequestVO.setReqGatewayType("Req Gateway Type");
        actualC2cVoucherTransferRequestVO.setServicePort("Service Port");
        actualC2cVoucherTransferRequestVO.setSourceType("Source Type");
        String actualToStringResult = actualC2cVoucherTransferRequestVO.toString();
        assertSame(data, actualC2cVoucherTransferRequestVO.getDatabuyvcr());
        assertEquals("Req Gateway Code", actualC2cVoucherTransferRequestVO.getReqGatewayCode());
        assertEquals("42", actualC2cVoucherTransferRequestVO.getReqGatewayLoginId());
        assertEquals("iloveyou", actualC2cVoucherTransferRequestVO.getReqGatewayPassword());
        assertEquals("Req Gateway Type", actualC2cVoucherTransferRequestVO.getReqGatewayType());
        assertEquals("Service Port", actualC2cVoucherTransferRequestVO.getServicePort());
        assertEquals("Source Type", actualC2cVoucherTransferRequestVO.getSourceType());
        assertEquals("reqGatewayLoginId = 42sourceTypeSource TypereqGatewayType = Req Gateway TypereqGatewayPassword ="
                + " iloveyouservicePort = Service PortreqGatewayCode = Req Gateway Codedata = language1nullpaymentinstcode"
                + " = nullpaymentinstdate = nullpaymentinstnum = nullvoucherDetails = nullremarksnullextnwcode = nullmsisdn"
                + " = nullpin = nullloginid = nullpassword = nullextcode = nullmsisdn2 = nullloginid2 = nullextcode2 ="
                + " nullfileType = nullfileName = nullfileAttachment = nullfileUploaded = null", actualToStringResult);
    }
}

