package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidateVoucherInfoResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ValidateVoucherInfoResponseVO}
     *   <li>{@link ValidateVoucherInfoResponseVO#setVoucherCount(int)}
     *   <li>{@link ValidateVoucherInfoResponseVO#getVoucherCount()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ValidateVoucherInfoResponseVO actualValidateVoucherInfoResponseVO = new ValidateVoucherInfoResponseVO();
        actualValidateVoucherInfoResponseVO.setVoucherCount(3);
        assertEquals(3, actualValidateVoucherInfoResponseVO.getVoucherCount());
    }
}

