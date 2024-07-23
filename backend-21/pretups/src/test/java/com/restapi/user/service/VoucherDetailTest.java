package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherDetailTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherDetail}
     *   <li>{@link VoucherDetail#setFromSerialNum(String)}
     *   <li>{@link VoucherDetail#setToSerialNum(String)}
     *   <li>{@link VoucherDetail#toString()}
     *   <li>{@link VoucherDetail#getFromSerialNum()}
     *   <li>{@link VoucherDetail#getToSerialNum()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherDetail actualVoucherDetail = new VoucherDetail();
        actualVoucherDetail.setFromSerialNum("jane.doe@example.org");
        actualVoucherDetail.setToSerialNum("To Serial Num");
        String actualToStringResult = actualVoucherDetail.toString();
        assertEquals("jane.doe@example.org", actualVoucherDetail.getFromSerialNum());
        assertEquals("To Serial Num", actualVoucherDetail.getToSerialNum());
        assertEquals("fromSerialNum = jane.doe@example.orgtoSerialNum = To Serial Num", actualToStringResult);
    }
}

