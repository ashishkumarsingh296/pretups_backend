package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class C2CStockApprovalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CStockApprovalRequestVO}
     *   <li>{@link C2CStockApprovalRequestVO#setData(DataStApp)}
     *   <li>{@link C2CStockApprovalRequestVO#setReqGatewayCode(String)}
     *   <li>{@link C2CStockApprovalRequestVO#setReqGatewayLoginId(String)}
     *   <li>{@link C2CStockApprovalRequestVO#setReqGatewayPassword(String)}
     *   <li>{@link C2CStockApprovalRequestVO#setReqGatewayType(String)}
     *   <li>{@link C2CStockApprovalRequestVO#setServicePort(String)}
     *   <li>{@link C2CStockApprovalRequestVO#setSourceType(String)}
     *   <li>{@link C2CStockApprovalRequestVO#toString()}
     *   <li>{@link C2CStockApprovalRequestVO#getData()}
     *   <li>{@link C2CStockApprovalRequestVO#getReqGatewayCode()}
     *   <li>{@link C2CStockApprovalRequestVO#getReqGatewayLoginId()}
     *   <li>{@link C2CStockApprovalRequestVO#getReqGatewayPassword()}
     *   <li>{@link C2CStockApprovalRequestVO#getReqGatewayType()}
     *   <li>{@link C2CStockApprovalRequestVO#getServicePort()}
     *   <li>{@link C2CStockApprovalRequestVO#getSourceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CStockApprovalRequestVO actualC2cStockApprovalRequestVO = new C2CStockApprovalRequestVO();
        DataStApp data = new DataStApp();
        actualC2cStockApprovalRequestVO.setData(data);
        actualC2cStockApprovalRequestVO.setReqGatewayCode("Req Gateway Code");
        actualC2cStockApprovalRequestVO.setReqGatewayLoginId("42");
        actualC2cStockApprovalRequestVO.setReqGatewayPassword("iloveyou");
        actualC2cStockApprovalRequestVO.setReqGatewayType("Req Gateway Type");
        actualC2cStockApprovalRequestVO.setServicePort("Service Port");
        actualC2cStockApprovalRequestVO.setSourceType("Source Type");
        String actualToStringResult = actualC2cStockApprovalRequestVO.toString();
        assertSame(data, actualC2cStockApprovalRequestVO.getData());
        assertEquals("Req Gateway Code", actualC2cStockApprovalRequestVO.getReqGatewayCode());
        assertEquals("42", actualC2cStockApprovalRequestVO.getReqGatewayLoginId());
        assertEquals("iloveyou", actualC2cStockApprovalRequestVO.getReqGatewayPassword());
        assertEquals("Req Gateway Type", actualC2cStockApprovalRequestVO.getReqGatewayType());
        assertEquals("Service Port", actualC2cStockApprovalRequestVO.getServicePort());
        assertEquals("Source Type", actualC2cStockApprovalRequestVO.getSourceType());
        assertEquals(
                "reqGatewayLoginId = 42data = txnid = nullproducts = nulllanguage1 = nullrefnumber = nullpaymentdetails"
                        + " = nullcurrentstatus = nullremarks = nullstatus = nullextnwcode = nullmsisdn = nullpin = nullloginid"
                        + " = nullpassword = nullextcode = nullsourceTypeSource TypereqGatewayType = Req Gateway TypereqGatewayPassword"
                        + " = iloveyouservicePort = Service PortreqGatewayCode = Req Gateway Code",
                actualToStringResult);
    }

    /**
     * Method under test: default or parameterless constructor of {@link C2CStockApprovalRequestVO}
     */
    @Test
    public void testConstructor2() {
        DataStApp data = (new C2CStockApprovalRequestVO()).getData();
        assertNull(data.getPaymentdetails());
        assertNull(data.getProducts());
    }
}

