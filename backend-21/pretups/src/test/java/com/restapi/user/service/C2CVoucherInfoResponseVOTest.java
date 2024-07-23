package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class C2CVoucherInfoResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CVoucherInfoResponseVO}
     *   <li>{@link C2CVoucherInfoResponseVO#setDisplayValue(String)}
     *   <li>{@link C2CVoucherInfoResponseVO#setSegment(List)}
     *   <li>{@link C2CVoucherInfoResponseVO#setValue(String)}
     *   <li>{@link C2CVoucherInfoResponseVO#toString()}
     *   <li>{@link C2CVoucherInfoResponseVO#getDisplayValue()}
     *   <li>{@link C2CVoucherInfoResponseVO#getSegment()}
     *   <li>{@link C2CVoucherInfoResponseVO#getValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CVoucherInfoResponseVO actualC2cVoucherInfoResponseVO = new C2CVoucherInfoResponseVO();
        actualC2cVoucherInfoResponseVO.setDisplayValue("42");
        ArrayList<VoucherSegmentResponse> segment = new ArrayList<>();
        actualC2cVoucherInfoResponseVO.setSegment(segment);
        actualC2cVoucherInfoResponseVO.setValue("42");
        String actualToStringResult = actualC2cVoucherInfoResponseVO.toString();
        assertEquals("42", actualC2cVoucherInfoResponseVO.getDisplayValue());
        assertSame(segment, actualC2cVoucherInfoResponseVO.getSegment());
        assertEquals("42", actualC2cVoucherInfoResponseVO.getValue());
        assertEquals("C2CVoucherInfoResponseVO [value=42, displayValue=42, segment=[]]", actualToStringResult);
    }
}

