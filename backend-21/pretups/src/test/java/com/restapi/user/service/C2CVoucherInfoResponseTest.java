package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2CVoucherInfoResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CVoucherInfoResponse}
     *   <li>{@link C2CVoucherInfoResponse#setDenomination(String)}
     *   <li>{@link C2CVoucherInfoResponse#setSegment(String)}
     *   <li>{@link C2CVoucherInfoResponse#setVoucherTypeCode(String)}
     *   <li>{@link C2CVoucherInfoResponse#setVoucherTypeValue(String)}
     *   <li>{@link C2CVoucherInfoResponse#toString()}
     *   <li>{@link C2CVoucherInfoResponse#getDenomination()}
     *   <li>{@link C2CVoucherInfoResponse#getSegment()}
     *   <li>{@link C2CVoucherInfoResponse#getVoucherTypeCode()}
     *   <li>{@link C2CVoucherInfoResponse#getVoucherTypeValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CVoucherInfoResponse actualC2cVoucherInfoResponse = new C2CVoucherInfoResponse();
        actualC2cVoucherInfoResponse.setDenomination("Denomination");
        actualC2cVoucherInfoResponse.setSegment("Segment");
        actualC2cVoucherInfoResponse.setVoucherTypeCode("Voucher Type Code");
        actualC2cVoucherInfoResponse.setVoucherTypeValue("42");
        String actualToStringResult = actualC2cVoucherInfoResponse.toString();
        assertEquals("Denomination", actualC2cVoucherInfoResponse.getDenomination());
        assertEquals("Segment", actualC2cVoucherInfoResponse.getSegment());
        assertEquals("Voucher Type Code", actualC2cVoucherInfoResponse.getVoucherTypeCode());
        assertEquals("42", actualC2cVoucherInfoResponse.getVoucherTypeValue());
        assertEquals(
                "C2CVoucherInfoResponse [voucherTypecode=Voucher Type Code, voucherTypeValue=42, denomination=Denomination,"
                        + " segment=Segment]",
                actualToStringResult);
    }
}

