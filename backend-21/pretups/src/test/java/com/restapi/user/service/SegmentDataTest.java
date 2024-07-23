package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SegmentDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SegmentData}
     *   <li>{@link SegmentData#setLoginId(String)}
     *   <li>{@link SegmentData#setMsisdn(String)}
     *   <li>{@link SegmentData#setVoucherType(String)}
     *   <li>{@link SegmentData#toString()}
     *   <li>{@link SegmentData#getLoginId()}
     *   <li>{@link SegmentData#getMsisdn()}
     *   <li>{@link SegmentData#getVoucherType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SegmentData actualSegmentData = new SegmentData();
        actualSegmentData.setLoginId("42");
        actualSegmentData.setMsisdn("Msisdn");
        actualSegmentData.setVoucherType("Voucher Type");
        String actualToStringResult = actualSegmentData.toString();
        assertEquals("42", actualSegmentData.getLoginId());
        assertEquals("Msisdn", actualSegmentData.getMsisdn());
        assertEquals("Voucher Type", actualSegmentData.getVoucherType());
        assertEquals("Data [loginId=42, msisdn=Msisdn, voucherType=Voucher Type]", actualToStringResult);
    }
}

