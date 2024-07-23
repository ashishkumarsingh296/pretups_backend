package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class VoucherSegmentResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherSegmentResponse}
     *   <li>{@link VoucherSegmentResponse#setDenominations(List)}
     *   <li>{@link VoucherSegmentResponse#setSegmentType(String)}
     *   <li>{@link VoucherSegmentResponse#setSegmentValue(String)}
     *   <li>{@link VoucherSegmentResponse#toString()}
     *   <li>{@link VoucherSegmentResponse#getDenominations()}
     *   <li>{@link VoucherSegmentResponse#getSegmentType()}
     *   <li>{@link VoucherSegmentResponse#getSegmentValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherSegmentResponse actualVoucherSegmentResponse = new VoucherSegmentResponse();
        ArrayList<String> denominations = new ArrayList<>();
        actualVoucherSegmentResponse.setDenominations(denominations);
        actualVoucherSegmentResponse.setSegmentType("Segment Type");
        actualVoucherSegmentResponse.setSegmentValue("42");
        String actualToStringResult = actualVoucherSegmentResponse.toString();
        assertSame(denominations, actualVoucherSegmentResponse.getDenominations());
        assertEquals("Segment Type", actualVoucherSegmentResponse.getSegmentType());
        assertEquals("42", actualVoucherSegmentResponse.getSegmentValue());
        assertEquals("VoucherSegmentResponse [denominations=[], segmentType=Segment Type, segmentValue=42]",
                actualToStringResult);
    }
}

