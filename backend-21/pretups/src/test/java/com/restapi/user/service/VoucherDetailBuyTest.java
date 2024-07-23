package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherDetailBuyTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherDetailBuy}
     *   <li>{@link VoucherDetailBuy#setDenomination(String)}
     *   <li>{@link VoucherDetailBuy#setQuantity(String)}
     *   <li>{@link VoucherDetailBuy#setVoucherType(String)}
     *   <li>{@link VoucherDetailBuy#setVouchersegment(String)}
     *   <li>{@link VoucherDetailBuy#toString()}
     *   <li>{@link VoucherDetailBuy#getDenomination()}
     *   <li>{@link VoucherDetailBuy#getQuantity()}
     *   <li>{@link VoucherDetailBuy#getVoucherType()}
     *   <li>{@link VoucherDetailBuy#getVouchersegment()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherDetailBuy actualVoucherDetailBuy = new VoucherDetailBuy();
        actualVoucherDetailBuy.setDenomination("Denomination");
        actualVoucherDetailBuy.setQuantity("Quantity");
        actualVoucherDetailBuy.setVoucherType("Voucher Type");
        actualVoucherDetailBuy.setVouchersegment("Vouchersegment");
        String actualToStringResult = actualVoucherDetailBuy.toString();
        assertEquals("Denomination", actualVoucherDetailBuy.getDenomination());
        assertEquals("Quantity", actualVoucherDetailBuy.getQuantity());
        assertEquals("Voucher Type", actualVoucherDetailBuy.getVoucherType());
        assertEquals("Vouchersegment", actualVoucherDetailBuy.getVouchersegment());
        assertEquals(
                "language1Denominationpaymentinstcode = Quantityvouchersegment = Vouchersegmentvouchertype =" + " Voucher Type",
                actualToStringResult);
    }
}

