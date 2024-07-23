package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2CStockInitiateRequestVOTest {
    /**
     * Method under test: {@link C2CStockInitiateRequestVO#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        C2CStockInitiateRequestVO c2cStockInitiateRequestVO = new C2CStockInitiateRequestVO();
        c2cStockInitiateRequestVO.setAdditionalProperty("Name", "Value");
        assertEquals(1, c2cStockInitiateRequestVO.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CStockInitiateRequestVO}
     *   <li>{@link C2CStockInitiateRequestVO#setData(C2CStockInitiateData)}
     *   <li>{@link C2CStockInitiateRequestVO#setReqGatewayCode(String)}
     *   <li>{@link C2CStockInitiateRequestVO#setReqGatewayLoginId(String)}
     *   <li>{@link C2CStockInitiateRequestVO#setReqGatewayPassword(String)}
     *   <li>{@link C2CStockInitiateRequestVO#setReqGatewayType(String)}
     *   <li>{@link C2CStockInitiateRequestVO#setServicePort(String)}
     *   <li>{@link C2CStockInitiateRequestVO#setSourceType(String)}
     *   <li>{@link C2CStockInitiateRequestVO#getData()}
     *   <li>{@link C2CStockInitiateRequestVO#getReqGatewayCode()}
     *   <li>{@link C2CStockInitiateRequestVO#getReqGatewayLoginId()}
     *   <li>{@link C2CStockInitiateRequestVO#getReqGatewayPassword()}
     *   <li>{@link C2CStockInitiateRequestVO#getReqGatewayType()}
     *   <li>{@link C2CStockInitiateRequestVO#getServicePort()}
     *   <li>{@link C2CStockInitiateRequestVO#getSourceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CStockInitiateRequestVO actualC2cStockInitiateRequestVO = new C2CStockInitiateRequestVO();
        C2CStockInitiateData data = new C2CStockInitiateData();
        data.setDate("2020-03-01");
        data.setExtcode("Extcode");
        data.setExtcode2("Extcode2");
        data.setExtnwcode("Extnwcode");
        data.setFileAttachment("File Attachment");
        data.setFileName("foo.txt");
        data.setFileType("File Type");
        data.setFileUploaded("File Uploaded");
        data.setLanguage1("en");
        data.setLoginid("Loginid");
        data.setLoginid2("Loginid2");
        data.setMsisdn("Msisdn");
        data.setMsisdn2("Msisdn2");
        data.setPassword("iloveyou");
        data.setPaymentdetails(new ArrayList<>());
        data.setPin("Pin");
        data.setProducts(new ArrayList<>());
        data.setRefnumber("42");
        data.setRemarks("Remarks");
        actualC2cStockInitiateRequestVO.setData(data);
        actualC2cStockInitiateRequestVO.setReqGatewayCode("Req Gateway Code");
        actualC2cStockInitiateRequestVO.setReqGatewayLoginId("42");
        actualC2cStockInitiateRequestVO.setReqGatewayPassword("iloveyou");
        actualC2cStockInitiateRequestVO.setReqGatewayType("Req Gateway Type");
        actualC2cStockInitiateRequestVO.setServicePort("Service Port");
        actualC2cStockInitiateRequestVO.setSourceType("Source Type");
        assertSame(data, actualC2cStockInitiateRequestVO.getData());
        assertEquals("Req Gateway Code", actualC2cStockInitiateRequestVO.getReqGatewayCode());
        assertEquals("42", actualC2cStockInitiateRequestVO.getReqGatewayLoginId());
        assertEquals("iloveyou", actualC2cStockInitiateRequestVO.getReqGatewayPassword());
        assertEquals("Req Gateway Type", actualC2cStockInitiateRequestVO.getReqGatewayType());
        assertEquals("Service Port", actualC2cStockInitiateRequestVO.getServicePort());
        assertEquals("Source Type", actualC2cStockInitiateRequestVO.getSourceType());
    }
}

