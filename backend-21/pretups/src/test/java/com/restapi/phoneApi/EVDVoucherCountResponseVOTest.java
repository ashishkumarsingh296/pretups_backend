package com.restapi.phoneApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class EVDVoucherCountResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link EVDVoucherCountResponseVO}
     *   <li>{@link EVDVoucherCountResponseVO#setVoucherDetails(List)}
     *   <li>{@link EVDVoucherCountResponseVO#setVoucherName(String)}
     *   <li>{@link EVDVoucherCountResponseVO#setVoucherType(String)}
     *   <li>{@link EVDVoucherCountResponseVO#getVoucherDetails()}
     *   <li>{@link EVDVoucherCountResponseVO#getVoucherName()}
     *   <li>{@link EVDVoucherCountResponseVO#getVoucherType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        EVDVoucherCountResponseVO actualEvdVoucherCountResponseVO = new EVDVoucherCountResponseVO();
        ArrayList<VoucherProfile> voucherDetails = new ArrayList<>();
        actualEvdVoucherCountResponseVO.setVoucherDetails(voucherDetails);
        actualEvdVoucherCountResponseVO.setVoucherName("Voucher Name");
        actualEvdVoucherCountResponseVO.setVoucherType("Voucher Type");
        assertSame(voucherDetails, actualEvdVoucherCountResponseVO.getVoucherDetails());
        assertEquals("Voucher Name", actualEvdVoucherCountResponseVO.getVoucherName());
        assertEquals("Voucher Type", actualEvdVoucherCountResponseVO.getVoucherType());
    }
}

