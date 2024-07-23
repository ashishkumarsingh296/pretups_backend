package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class VoucherSegmentCountResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherSegmentCountResponse}
     *   <li>{@link VoucherSegmentCountResponse#setSegmentType(String)}
     *   <li>{@link VoucherSegmentCountResponse#setSegmentValue(String)}
     *   <li>{@link VoucherSegmentCountResponse#setVoucherDetails(List)}
     *   <li>{@link VoucherSegmentCountResponse#toString()}
     *   <li>{@link VoucherSegmentCountResponse#getSegmentType()}
     *   <li>{@link VoucherSegmentCountResponse#getSegmentValue()}
     *   <li>{@link VoucherSegmentCountResponse#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherSegmentCountResponse actualVoucherSegmentCountResponse = new VoucherSegmentCountResponse();
        actualVoucherSegmentCountResponse.setSegmentType("Segment Type");
        actualVoucherSegmentCountResponse.setSegmentValue("42");
        ArrayList<VoucherProfile> denominations = new ArrayList<>();
        actualVoucherSegmentCountResponse.setVoucherDetails(denominations);
        String actualToStringResult = actualVoucherSegmentCountResponse.toString();
        assertEquals("Segment Type", actualVoucherSegmentCountResponse.getSegmentType());
        assertEquals("42", actualVoucherSegmentCountResponse.getSegmentValue());
        assertSame(denominations, actualVoucherSegmentCountResponse.getVoucherDetails());
        assertEquals("VoucherSegmentResponse [denominations=[], segmentType=Segment Type, segmentValue=42]",
                actualToStringResult);
    }
}

