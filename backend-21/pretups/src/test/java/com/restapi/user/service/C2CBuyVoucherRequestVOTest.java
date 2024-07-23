package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class C2CBuyVoucherRequestVOTest {
    /**
     * Method under test: {@link C2CBuyVoucherRequestVO#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        C2CBuyVoucherRequestVO c2cBuyVoucherRequestVO = new C2CBuyVoucherRequestVO();
        c2cBuyVoucherRequestVO.setAdditionalProperty("Name", "Value");
        assertEquals(1, c2cBuyVoucherRequestVO.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CBuyVoucherRequestVO}
     *   <li>{@link C2CBuyVoucherRequestVO#setDatabuyvcr(Databuyvcr)}
     *   <li>{@link C2CBuyVoucherRequestVO#setReqGatewayCode(String)}
     *   <li>{@link C2CBuyVoucherRequestVO#setReqGatewayLoginId(String)}
     *   <li>{@link C2CBuyVoucherRequestVO#setReqGatewayPassword(String)}
     *   <li>{@link C2CBuyVoucherRequestVO#setReqGatewayType(String)}
     *   <li>{@link C2CBuyVoucherRequestVO#setServicePort(String)}
     *   <li>{@link C2CBuyVoucherRequestVO#setSourceType(String)}
     *   <li>{@link C2CBuyVoucherRequestVO#toString()}
     *   <li>{@link C2CBuyVoucherRequestVO#getDatabuyvcr()}
     *   <li>{@link C2CBuyVoucherRequestVO#getReqGatewayCode()}
     *   <li>{@link C2CBuyVoucherRequestVO#getReqGatewayLoginId()}
     *   <li>{@link C2CBuyVoucherRequestVO#getReqGatewayPassword()}
     *   <li>{@link C2CBuyVoucherRequestVO#getReqGatewayType()}
     *   <li>{@link C2CBuyVoucherRequestVO#getServicePort()}
     *   <li>{@link C2CBuyVoucherRequestVO#getSourceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CBuyVoucherRequestVO actualC2cBuyVoucherRequestVO = new C2CBuyVoucherRequestVO();
        Databuyvcr data = new Databuyvcr();
        actualC2cBuyVoucherRequestVO.setDatabuyvcr(data);
        actualC2cBuyVoucherRequestVO.setReqGatewayCode("Req Gateway Code");
        actualC2cBuyVoucherRequestVO.setReqGatewayLoginId("42");
        actualC2cBuyVoucherRequestVO.setReqGatewayPassword("iloveyou");
        actualC2cBuyVoucherRequestVO.setReqGatewayType("Req Gateway Type");
        actualC2cBuyVoucherRequestVO.setServicePort("Service Port");
        actualC2cBuyVoucherRequestVO.setSourceType("Source Type");
        String actualToStringResult = actualC2cBuyVoucherRequestVO.toString();
        assertSame(data, actualC2cBuyVoucherRequestVO.getDatabuyvcr());
        assertEquals("Req Gateway Code", actualC2cBuyVoucherRequestVO.getReqGatewayCode());
        assertEquals("42", actualC2cBuyVoucherRequestVO.getReqGatewayLoginId());
        assertEquals("iloveyou", actualC2cBuyVoucherRequestVO.getReqGatewayPassword());
        assertEquals("Req Gateway Type", actualC2cBuyVoucherRequestVO.getReqGatewayType());
        assertEquals("Service Port", actualC2cBuyVoucherRequestVO.getServicePort());
        assertEquals("Source Type", actualC2cBuyVoucherRequestVO.getSourceType());
        assertEquals("reqGatewayLoginId = 42sourceTypeSource TypereqGatewayType = Req Gateway TypereqGatewayPassword ="
                + " iloveyouservicePort = Service PortreqGatewayCode = Req Gateway Codedata = language1nullpaymentinstcode"
                + " = nullpaymentinstdate = nullpaymentinstnum = nullvoucherDetails = nullremarksnullextnwcode = nullmsisdn"
                + " = nullpin = nullloginid = nullpassword = nullextcode = nullmsisdn2 = nullloginid2 = nullextcode2 ="
                + " nullfileType = nullfileName = nullfileAttachment = nullfileUploaded = null", actualToStringResult);
    }
}

