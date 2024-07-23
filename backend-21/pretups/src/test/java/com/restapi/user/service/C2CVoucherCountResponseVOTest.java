package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class C2CVoucherCountResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CVoucherCountResponseVO}
     *   <li>{@link C2CVoucherCountResponseVO#setSegment(List)}
     *   <li>{@link C2CVoucherCountResponseVO#setVoucherName(String)}
     *   <li>{@link C2CVoucherCountResponseVO#setVoucherType(String)}
     *   <li>{@link C2CVoucherCountResponseVO#toString()}
     *   <li>{@link C2CVoucherCountResponseVO#getSegment()}
     *   <li>{@link C2CVoucherCountResponseVO#getVoucherName()}
     *   <li>{@link C2CVoucherCountResponseVO#getVoucherType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CVoucherCountResponseVO actualC2cVoucherCountResponseVO = new C2CVoucherCountResponseVO();
        ArrayList<VoucherSegmentCountResponse> segment = new ArrayList<>();
        actualC2cVoucherCountResponseVO.setSegment(segment);
        actualC2cVoucherCountResponseVO.setVoucherName("Voucher Name");
        actualC2cVoucherCountResponseVO.setVoucherType("Voucher Type");
        String actualToStringResult = actualC2cVoucherCountResponseVO.toString();
        assertSame(segment, actualC2cVoucherCountResponseVO.getSegment());
        assertEquals("Voucher Name", actualC2cVoucherCountResponseVO.getVoucherName());
        assertEquals("Voucher Type", actualC2cVoucherCountResponseVO.getVoucherType());
        assertEquals("C2CVoucherCountResponseVO [voucherType=Voucher Type, voucherName=Voucher Name, segment=[]]",
                actualToStringResult);
    }
}

