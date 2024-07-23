package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2CVoucherSegmentResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CVoucherSegmentResponse}
     *   <li>{@link C2CVoucherSegmentResponse#setCode(String)}
     *   <li>{@link C2CVoucherSegmentResponse#setValue(String)}
     *   <li>{@link C2CVoucherSegmentResponse#toString()}
     *   <li>{@link C2CVoucherSegmentResponse#getCode()}
     *   <li>{@link C2CVoucherSegmentResponse#getValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CVoucherSegmentResponse actualC2cVoucherSegmentResponse = new C2CVoucherSegmentResponse();
        actualC2cVoucherSegmentResponse.setCode("Code");
        actualC2cVoucherSegmentResponse.setValue("42");
        String actualToStringResult = actualC2cVoucherSegmentResponse.toString();
        assertEquals("Code", actualC2cVoucherSegmentResponse.getCode());
        assertEquals("42", actualC2cVoucherSegmentResponse.getValue());
        assertEquals("{key=Code, value=42}", actualToStringResult);
    }
}

