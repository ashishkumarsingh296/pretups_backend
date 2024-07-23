package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2CReturnRequestVOTest {
    /**
     * Method under test: {@link C2CReturnRequestVO#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        C2CReturnRequestVO c2cReturnRequestVO = new C2CReturnRequestVO();
        c2cReturnRequestVO.setAdditionalProperty("Name", "Value");
        assertEquals(1, c2cReturnRequestVO.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CReturnRequestVO}
     *   <li>{@link C2CReturnRequestVO#setData(C2CReturnWithdrawData)}
     *   <li>{@link C2CReturnRequestVO#setReqGatewayCode(String)}
     *   <li>{@link C2CReturnRequestVO#setReqGatewayLoginId(String)}
     *   <li>{@link C2CReturnRequestVO#setReqGatewayPassword(String)}
     *   <li>{@link C2CReturnRequestVO#setReqGatewayType(String)}
     *   <li>{@link C2CReturnRequestVO#setServicePort(String)}
     *   <li>{@link C2CReturnRequestVO#setSourceType(String)}
     *   <li>{@link C2CReturnRequestVO#getData()}
     *   <li>{@link C2CReturnRequestVO#getReqGatewayCode()}
     *   <li>{@link C2CReturnRequestVO#getReqGatewayLoginId()}
     *   <li>{@link C2CReturnRequestVO#getReqGatewayPassword()}
     *   <li>{@link C2CReturnRequestVO#getReqGatewayType()}
     *   <li>{@link C2CReturnRequestVO#getServicePort()}
     *   <li>{@link C2CReturnRequestVO#getSourceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CReturnRequestVO actualC2cReturnRequestVO = new C2CReturnRequestVO();
        C2CReturnWithdrawData data = new C2CReturnWithdrawData();
        data.setDate("2020-03-01");
        data.setExtcode("Extcode");
        data.setExtcode2("Extcode2");
        data.setExtnwcode("Extnwcode");
        data.setExtrefnum("Extrefnum");
        data.setLanguage1("en");
        data.setLoginid("Loginid");
        data.setLoginid2("Loginid2");
        data.setMsisdn("Msisdn");
        data.setMsisdn2("Msisdn2");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setProducts(new ArrayList<>());
        actualC2cReturnRequestVO.setData(data);
        actualC2cReturnRequestVO.setReqGatewayCode("Req Gateway Code");
        actualC2cReturnRequestVO.setReqGatewayLoginId("42");
        actualC2cReturnRequestVO.setReqGatewayPassword("iloveyou");
        actualC2cReturnRequestVO.setReqGatewayType("Req Gateway Type");
        actualC2cReturnRequestVO.setServicePort("Service Port");
        actualC2cReturnRequestVO.setSourceType("Source Type");
        assertSame(data, actualC2cReturnRequestVO.getData());
        assertEquals("Req Gateway Code", actualC2cReturnRequestVO.getReqGatewayCode());
        assertEquals("42", actualC2cReturnRequestVO.getReqGatewayLoginId());
        assertEquals("iloveyou", actualC2cReturnRequestVO.getReqGatewayPassword());
        assertEquals("Req Gateway Type", actualC2cReturnRequestVO.getReqGatewayType());
        assertEquals("Service Port", actualC2cReturnRequestVO.getServicePort());
        assertEquals("Source Type", actualC2cReturnRequestVO.getSourceType());
    }
}

