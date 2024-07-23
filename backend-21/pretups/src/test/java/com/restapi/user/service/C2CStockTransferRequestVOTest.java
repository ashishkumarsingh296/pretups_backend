package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class C2CStockTransferRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CStockTransferRequestVO}
     *   <li>{@link C2CStockTransferRequestVO#setData(DataStkTrf)}
     *   <li>{@link C2CStockTransferRequestVO#setReqGatewayCode(String)}
     *   <li>{@link C2CStockTransferRequestVO#setReqGatewayLoginId(String)}
     *   <li>{@link C2CStockTransferRequestVO#setReqGatewayPassword(String)}
     *   <li>{@link C2CStockTransferRequestVO#setReqGatewayType(String)}
     *   <li>{@link C2CStockTransferRequestVO#setServicePort(String)}
     *   <li>{@link C2CStockTransferRequestVO#setSourceType(String)}
     *   <li>{@link C2CStockTransferRequestVO#toString()}
     *   <li>{@link C2CStockTransferRequestVO#getData()}
     *   <li>{@link C2CStockTransferRequestVO#getReqGatewayCode()}
     *   <li>{@link C2CStockTransferRequestVO#getReqGatewayLoginId()}
     *   <li>{@link C2CStockTransferRequestVO#getReqGatewayPassword()}
     *   <li>{@link C2CStockTransferRequestVO#getReqGatewayType()}
     *   <li>{@link C2CStockTransferRequestVO#getServicePort()}
     *   <li>{@link C2CStockTransferRequestVO#getSourceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CStockTransferRequestVO actualC2cStockTransferRequestVO = new C2CStockTransferRequestVO();
        DataStkTrf data = new DataStkTrf();
        actualC2cStockTransferRequestVO.setData(data);
        actualC2cStockTransferRequestVO.setReqGatewayCode("Req Gateway Code");
        actualC2cStockTransferRequestVO.setReqGatewayLoginId("42");
        actualC2cStockTransferRequestVO.setReqGatewayPassword("iloveyou");
        actualC2cStockTransferRequestVO.setReqGatewayType("Req Gateway Type");
        actualC2cStockTransferRequestVO.setServicePort("Service Port");
        actualC2cStockTransferRequestVO.setSourceType("Source Type");
        String actualToStringResult = actualC2cStockTransferRequestVO.toString();
        assertSame(data, actualC2cStockTransferRequestVO.getData());
        assertEquals("Req Gateway Code", actualC2cStockTransferRequestVO.getReqGatewayCode());
        assertEquals("42", actualC2cStockTransferRequestVO.getReqGatewayLoginId());
        assertEquals("iloveyou", actualC2cStockTransferRequestVO.getReqGatewayPassword());
        assertEquals("Req Gateway Type", actualC2cStockTransferRequestVO.getReqGatewayType());
        assertEquals("Service Port", actualC2cStockTransferRequestVO.getServicePort());
        assertEquals("Source Type", actualC2cStockTransferRequestVO.getSourceType());
        assertEquals("reqGatewayLoginId = 42sourceTypeSource TypereqGatewayType = Req Gateway TypereqGatewayPassword ="
                + " iloveyouservicePort = Service PortreqGatewayCode = Req Gateway Codedata = language1nullproducts ="
                + " nullremarksnullextnwcode = nullmsisdn = nullpin = nullloginid = nullpassword = nullextcode = nullmsisdn2"
                + " = nullloginid2 = nullextcode2 = nullpaymentdetails = nullfileName = nullfileAttachment = nullfileType"
                + " = nullfileUploaded = null", actualToStringResult);
    }
}

