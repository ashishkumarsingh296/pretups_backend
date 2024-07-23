package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DenominationDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DenominationData}
     *   <li>{@link DenominationData#setLoginId(String)}
     *   <li>{@link DenominationData#setMsisdn(String)}
     *   <li>{@link DenominationData#setVoucherSegment(String)}
     *   <li>{@link DenominationData#setVoucherType(String)}
     *   <li>{@link DenominationData#toString()}
     *   <li>{@link DenominationData#getLoginId()}
     *   <li>{@link DenominationData#getMsisdn()}
     *   <li>{@link DenominationData#getVoucherSegment()}
     *   <li>{@link DenominationData#getVoucherType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DenominationData actualDenominationData = new DenominationData();
        actualDenominationData.setLoginId("42");
        actualDenominationData.setMsisdn("Msisdn");
        actualDenominationData.setVoucherSegment("Voucher Segment");
        actualDenominationData.setVoucherType("Voucher Type");
        String actualToStringResult = actualDenominationData.toString();
        assertEquals("42", actualDenominationData.getLoginId());
        assertEquals("Msisdn", actualDenominationData.getMsisdn());
        assertEquals("Voucher Segment", actualDenominationData.getVoucherSegment());
        assertEquals("Voucher Type", actualDenominationData.getVoucherType());
        assertEquals("Data [loginId=42, msisdn=Msisdn, voucherType=Voucher Type, voucherSegment=Voucher Segment]",
                actualToStringResult);
    }
}

