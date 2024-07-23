package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherTypeVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherTypeVO}
     *   <li>{@link VoucherTypeVO#setVoucherCode(String)}
     *   <li>{@link VoucherTypeVO#setVoucherName(String)}
     *   <li>{@link VoucherTypeVO#toString()}
     *   <li>{@link VoucherTypeVO#getVoucherCode()}
     *   <li>{@link VoucherTypeVO#getVoucherName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherTypeVO actualVoucherTypeVO = new VoucherTypeVO();
        actualVoucherTypeVO.setVoucherCode("Voucher Code");
        actualVoucherTypeVO.setVoucherName("Voucher Name");
        actualVoucherTypeVO.toString();
        assertEquals("Voucher Code", actualVoucherTypeVO.getVoucherCode());
        assertEquals("Voucher Name", actualVoucherTypeVO.getVoucherName());
    }
}

