package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherDetailTrfTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherDetailTrf}
     *   <li>{@link VoucherDetailTrf#seToSerialNo(String)}
     *   <li>{@link VoucherDetailTrf#setDenomination(String)}
     *   <li>{@link VoucherDetailTrf#setFromSerialNo(String)}
     *   <li>{@link VoucherDetailTrf#setVoucherType(String)}
     *   <li>{@link VoucherDetailTrf#setVouchersegment(String)}
     *   <li>{@link VoucherDetailTrf#toString()}
     *   <li>{@link VoucherDetailTrf#getDenomination()}
     *   <li>{@link VoucherDetailTrf#getFromSerialNo()}
     *   <li>{@link VoucherDetailTrf#getToSerialNo()}
     *   <li>{@link VoucherDetailTrf#getVoucherType()}
     *   <li>{@link VoucherDetailTrf#getVouchersegment()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherDetailTrf actualVoucherDetailTrf = new VoucherDetailTrf();
        actualVoucherDetailTrf.seToSerialNo("To Serial No");
        actualVoucherDetailTrf.setDenomination("Denomination");
        actualVoucherDetailTrf.setFromSerialNo("jane.doe@example.org");
        actualVoucherDetailTrf.setVoucherType("Voucher Type");
        actualVoucherDetailTrf.setVouchersegment("Vouchersegment");
        String actualToStringResult = actualVoucherDetailTrf.toString();
        assertEquals("Denomination", actualVoucherDetailTrf.getDenomination());
        assertEquals("jane.doe@example.org", actualVoucherDetailTrf.getFromSerialNo());
        assertEquals("To Serial No", actualVoucherDetailTrf.getToSerialNo());
        assertEquals("Voucher Type", actualVoucherDetailTrf.getVoucherType());
        assertEquals("Vouchersegment", actualVoucherDetailTrf.getVouchersegment());
        assertEquals("language1DenominationfromSerialNo = jane.doe@example.orgtoSerialNo = To Serial Novouchersegment ="
                + " Vouchersegmentvouchertype = Voucher Type", actualToStringResult);
    }
}

