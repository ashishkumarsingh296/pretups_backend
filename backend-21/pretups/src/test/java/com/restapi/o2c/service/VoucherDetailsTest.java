package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherDetails}
     *   <li>{@link VoucherDetails#setDenomination(String)}
     *   <li>{@link VoucherDetails#setFromSerialNo(String)}
     *   <li>{@link VoucherDetails#setToSerialNo(String)}
     *   <li>{@link VoucherDetails#setVoucherType(String)}
     *   <li>{@link VoucherDetails#setVouchersegment(String)}
     *   <li>{@link VoucherDetails#toString()}
     *   <li>{@link VoucherDetails#getDenomination()}
     *   <li>{@link VoucherDetails#getFromSerialNo()}
     *   <li>{@link VoucherDetails#getToSerialNo()}
     *   <li>{@link VoucherDetails#getVoucherType()}
     *   <li>{@link VoucherDetails#getVouchersegment()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherDetails actualVoucherDetails = new VoucherDetails();
        actualVoucherDetails.setDenomination("Denomination");
        actualVoucherDetails.setFromSerialNo("jane.doe@example.org");
        actualVoucherDetails.setToSerialNo("To Serial No");
        actualVoucherDetails.setVoucherType("Voucher Type");
        actualVoucherDetails.setVouchersegment("Vouchersegment");
        String actualToStringResult = actualVoucherDetails.toString();
        assertEquals("Denomination", actualVoucherDetails.getDenomination());
        assertEquals("jane.doe@example.org", actualVoucherDetails.getFromSerialNo());
        assertEquals("To Serial No", actualVoucherDetails.getToSerialNo());
        assertEquals("Voucher Type", actualVoucherDetails.getVoucherType());
        assertEquals("Vouchersegment", actualVoucherDetails.getVouchersegment());
        assertEquals(
                "VoucherDetails [denomination=Denomination, fromSerialNo=jane.doe@example.org, toSerialNo=To Serial No,"
                        + " voucherType=Voucher Type, vouchersegment=Vouchersegment]",
                actualToStringResult);
    }
}

