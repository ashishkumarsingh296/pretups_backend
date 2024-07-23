package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherVO}
     *   <li>{@link VoucherVO#setExpiryDate(String)}
     *   <li>{@link VoucherVO#setMrp(int)}
     *   <li>{@link VoucherVO#setPinNo(String)}
     *   <li>{@link VoucherVO#setProductId(long)}
     *   <li>{@link VoucherVO#setSerialNo(String)}
     *   <li>{@link VoucherVO#setTransactionDate(String)}
     *   <li>{@link VoucherVO#setValidity(int)}
     *   <li>{@link VoucherVO#getExpiryDate()}
     *   <li>{@link VoucherVO#getMrp()}
     *   <li>{@link VoucherVO#getPinNo()}
     *   <li>{@link VoucherVO#getProductId()}
     *   <li>{@link VoucherVO#getSerialNo()}
     *   <li>{@link VoucherVO#getTransactionDate()}
     *   <li>{@link VoucherVO#getValidity()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherVO actualVoucherVO = new VoucherVO();
        actualVoucherVO.setExpiryDate("2020-03-01");
        actualVoucherVO.setMrp(1);
        actualVoucherVO.setPinNo("Pin No");
        actualVoucherVO.setProductId(1L);
        actualVoucherVO.setSerialNo("Serial No");
        actualVoucherVO.setTransactionDate("2020-03-01");
        actualVoucherVO.setValidity(1);
        assertEquals("2020-03-01", actualVoucherVO.getExpiryDate());
        assertEquals(1, actualVoucherVO.getMrp());
        assertEquals("Pin No", actualVoucherVO.getPinNo());
        assertEquals(1L, actualVoucherVO.getProductId());
        assertEquals("Serial No", actualVoucherVO.getSerialNo());
        assertEquals("2020-03-01", actualVoucherVO.getTransactionDate());
        assertEquals(1, actualVoucherVO.getValidity());
    }
}

