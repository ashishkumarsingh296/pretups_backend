package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherDetailsApprvTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherDetailsApprv}
     *   <li>{@link VoucherDetailsApprv#setDenomination(String)}
     *   <li>{@link VoucherDetailsApprv#setFromSerialNo(String)}
     *   <li>{@link VoucherDetailsApprv#setReqQuantity(String)}
     *   <li>{@link VoucherDetailsApprv#setToSerialNo(String)}
     *   <li>{@link VoucherDetailsApprv#setVoucherProfileId(String)}
     *   <li>{@link VoucherDetailsApprv#setVoucherType(String)}
     *   <li>{@link VoucherDetailsApprv#setVouchersegment(String)}
     *   <li>{@link VoucherDetailsApprv#toString()}
     *   <li>{@link VoucherDetailsApprv#getDenomination()}
     *   <li>{@link VoucherDetailsApprv#getFromSerialNo()}
     *   <li>{@link VoucherDetailsApprv#getReqQuantity()}
     *   <li>{@link VoucherDetailsApprv#getToSerialNo()}
     *   <li>{@link VoucherDetailsApprv#getVoucherProfileId()}
     *   <li>{@link VoucherDetailsApprv#getVoucherType()}
     *   <li>{@link VoucherDetailsApprv#getVouchersegment()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherDetailsApprv actualVoucherDetailsApprv = new VoucherDetailsApprv();
        actualVoucherDetailsApprv.setDenomination("Denomination");
        actualVoucherDetailsApprv.setFromSerialNo("jane.doe@example.org");
        actualVoucherDetailsApprv.setReqQuantity("Req Quantity");
        actualVoucherDetailsApprv.setToSerialNo("To Serial No");
        actualVoucherDetailsApprv.setVoucherProfileId("42");
        actualVoucherDetailsApprv.setVoucherType("Voucher Type");
        actualVoucherDetailsApprv.setVouchersegment("Vouchersegment");
        String actualToStringResult = actualVoucherDetailsApprv.toString();
        assertEquals("Denomination", actualVoucherDetailsApprv.getDenomination());
        assertEquals("jane.doe@example.org", actualVoucherDetailsApprv.getFromSerialNo());
        assertEquals("Req Quantity", actualVoucherDetailsApprv.getReqQuantity());
        assertEquals("To Serial No", actualVoucherDetailsApprv.getToSerialNo());
        assertEquals("42", actualVoucherDetailsApprv.getVoucherProfileId());
        assertEquals("Voucher Type", actualVoucherDetailsApprv.getVoucherType());
        assertEquals("Vouchersegment", actualVoucherDetailsApprv.getVouchersegment());
        assertEquals(
                "VoucherDetails [denomination=Denomination, fromSerialNo=jane.doe@example.org, toSerialNo=To Serial No,"
                        + " voucherType=Voucher Type, vouchersegment=Vouchersegment]",
                actualToStringResult);
    }
}

