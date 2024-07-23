package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidateVoucherInfoRequestTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ValidateVoucherInfoRequest}
     *   <li>{@link ValidateVoucherInfoRequest#setCount(int)}
     *   <li>{@link ValidateVoucherInfoRequest#setFromSerialNumber(String)}
     *   <li>{@link ValidateVoucherInfoRequest#setToSerialNumber(String)}
     *   <li>{@link ValidateVoucherInfoRequest#getCount()}
     *   <li>{@link ValidateVoucherInfoRequest#getFromSerialNumber()}
     *   <li>{@link ValidateVoucherInfoRequest#getToSerialNumber()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ValidateVoucherInfoRequest actualValidateVoucherInfoRequest = new ValidateVoucherInfoRequest();
        actualValidateVoucherInfoRequest.setCount(3);
        actualValidateVoucherInfoRequest.setFromSerialNumber("42");
        actualValidateVoucherInfoRequest.setToSerialNumber("42");
        assertEquals(3, actualValidateVoucherInfoRequest.getCount());
        assertEquals("42", actualValidateVoucherInfoRequest.getFromSerialNumber());
        assertEquals("42", actualValidateVoucherInfoRequest.getToSerialNumber());
    }
}

