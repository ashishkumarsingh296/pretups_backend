package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DvdSwaggVoucherDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DvdSwaggVoucherDetails}
     *   <li>{@link DvdSwaggVoucherDetails#setDenomination(String)}
     *   <li>{@link DvdSwaggVoucherDetails#setQuantity(String)}
     *   <li>{@link DvdSwaggVoucherDetails#setVoucherProfile(String)}
     *   <li>{@link DvdSwaggVoucherDetails#setVoucherSegment(String)}
     *   <li>{@link DvdSwaggVoucherDetails#setVoucherType(String)}
     *   <li>{@link DvdSwaggVoucherDetails#toString()}
     *   <li>{@link DvdSwaggVoucherDetails#getDenomination()}
     *   <li>{@link DvdSwaggVoucherDetails#getQuantity()}
     *   <li>{@link DvdSwaggVoucherDetails#getVoucherProfile()}
     *   <li>{@link DvdSwaggVoucherDetails#getVoucherSegment()}
     *   <li>{@link DvdSwaggVoucherDetails#getVoucherType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DvdSwaggVoucherDetails actualDvdSwaggVoucherDetails = new DvdSwaggVoucherDetails();
        actualDvdSwaggVoucherDetails.setDenomination("Denomination");
        actualDvdSwaggVoucherDetails.setQuantity("Quantity");
        actualDvdSwaggVoucherDetails.setVoucherProfile("Voucher Profile");
        actualDvdSwaggVoucherDetails.setVoucherSegment("Voucher Segment");
        actualDvdSwaggVoucherDetails.setVoucherType("Voucher Type");
        String actualToStringResult = actualDvdSwaggVoucherDetails.toString();
        assertEquals("Denomination", actualDvdSwaggVoucherDetails.getDenomination());
        assertEquals("Quantity", actualDvdSwaggVoucherDetails.getQuantity());
        assertEquals("Voucher Profile", actualDvdSwaggVoucherDetails.getVoucherProfile());
        assertEquals("Voucher Segment", actualDvdSwaggVoucherDetails.getVoucherSegment());
        assertEquals("Voucher Type", actualDvdSwaggVoucherDetails.getVoucherType());
        assertEquals(
                "DvdSwaggVoucherDetails [voucherType=Voucher Type, voucherSegment=Voucher Segment, voucherProfile=Voucher"
                        + " Profile, denomination=Denomination, quantity=Quantity]",
                actualToStringResult);
    }
}

