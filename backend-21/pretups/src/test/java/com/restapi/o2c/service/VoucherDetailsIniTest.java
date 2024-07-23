package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherDetailsIniTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherDetailsIni}
     *   <li>{@link VoucherDetailsIni#setDenomination(String)}
     *   <li>{@link VoucherDetailsIni#setQuantity(String)}
     *   <li>{@link VoucherDetailsIni#setVoucherType(String)}
     *   <li>{@link VoucherDetailsIni#setVouchersegment(String)}
     *   <li>{@link VoucherDetailsIni#toString()}
     *   <li>{@link VoucherDetailsIni#getDenomination()}
     *   <li>{@link VoucherDetailsIni#getQuantity()}
     *   <li>{@link VoucherDetailsIni#getVoucherType()}
     *   <li>{@link VoucherDetailsIni#getVouchersegment()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherDetailsIni actualVoucherDetailsIni = new VoucherDetailsIni();
        actualVoucherDetailsIni.setDenomination("Denomination");
        actualVoucherDetailsIni.setQuantity("Quantity");
        actualVoucherDetailsIni.setVoucherType("Voucher Type");
        actualVoucherDetailsIni.setVouchersegment("Vouchersegment");
        String actualToStringResult = actualVoucherDetailsIni.toString();
        assertEquals("Denomination", actualVoucherDetailsIni.getDenomination());
        assertEquals("Quantity", actualVoucherDetailsIni.getQuantity());
        assertEquals("Voucher Type", actualVoucherDetailsIni.getVoucherType());
        assertEquals("Vouchersegment", actualVoucherDetailsIni.getVouchersegment());
        assertEquals(
                "VoucherDetailsVO [denomination=Denomination, quantity=Quantity, voucherType=Voucher Type, vouchersegment"
                        + "=Vouchersegment]",
                actualToStringResult);
    }
}

