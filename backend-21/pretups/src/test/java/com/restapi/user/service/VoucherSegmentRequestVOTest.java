package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class VoucherSegmentRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherSegmentRequestVO}
     *   <li>{@link VoucherSegmentRequestVO#setData(SegmentData)}
     *   <li>{@link VoucherSegmentRequestVO#setIdentifierType(String)}
     *   <li>{@link VoucherSegmentRequestVO#setIdentifierValue(String)}
     *   <li>{@link VoucherSegmentRequestVO#toString()}
     *   <li>{@link VoucherSegmentRequestVO#getData()}
     *   <li>{@link VoucherSegmentRequestVO#getIdentifierType()}
     *   <li>{@link VoucherSegmentRequestVO#getIdentifierValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherSegmentRequestVO actualVoucherSegmentRequestVO = new VoucherSegmentRequestVO();
        SegmentData data = new SegmentData();
        actualVoucherSegmentRequestVO.setData(data);
        actualVoucherSegmentRequestVO.setIdentifierType("Identifier Type");
        actualVoucherSegmentRequestVO.setIdentifierValue("42");
        String actualToStringResult = actualVoucherSegmentRequestVO.toString();
        assertSame(data, actualVoucherSegmentRequestVO.getData());
        assertEquals("Identifier Type", actualVoucherSegmentRequestVO.getIdentifierType());
        assertEquals("42", actualVoucherSegmentRequestVO.getIdentifierValue());
        assertEquals("VoucherSegmentRequestVO [identifierType=Identifier Type, identifierValue=42, segmentData=Data"
                + " [loginId=null, msisdn=null, voucherType=null]]", actualToStringResult);
    }
}

