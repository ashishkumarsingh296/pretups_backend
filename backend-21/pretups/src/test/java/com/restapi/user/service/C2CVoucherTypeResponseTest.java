package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2CVoucherTypeResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CVoucherTypeResponse}
     *   <li>{@link C2CVoucherTypeResponse#setCode(String)}
     *   <li>{@link C2CVoucherTypeResponse#setValue(String)}
     *   <li>{@link C2CVoucherTypeResponse#toString()}
     *   <li>{@link C2CVoucherTypeResponse#getCode()}
     *   <li>{@link C2CVoucherTypeResponse#getValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CVoucherTypeResponse actualC2cVoucherTypeResponse = new C2CVoucherTypeResponse();
        actualC2cVoucherTypeResponse.setCode("Code");
        actualC2cVoucherTypeResponse.setValue("42");
        String actualToStringResult = actualC2cVoucherTypeResponse.toString();
        assertEquals("Code", actualC2cVoucherTypeResponse.getCode());
        assertEquals("42", actualC2cVoucherTypeResponse.getValue());
        assertEquals("{key=Code, value=42}", actualToStringResult);
    }
}

