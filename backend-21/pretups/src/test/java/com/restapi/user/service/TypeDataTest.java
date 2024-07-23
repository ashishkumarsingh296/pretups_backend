package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TypeData}
     *   <li>{@link TypeData#setLoginId(String)}
     *   <li>{@link TypeData#setMsisdn(String)}
     *   <li>{@link TypeData#setVoucherType(String)}
     *   <li>{@link TypeData#toString()}
     *   <li>{@link TypeData#getLoginId()}
     *   <li>{@link TypeData#getMsisdn()}
     *   <li>{@link TypeData#getVoucherType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TypeData actualTypeData = new TypeData();
        actualTypeData.setLoginId("42");
        actualTypeData.setMsisdn("Msisdn");
        actualTypeData.setVoucherType("Voucher List");
        String actualToStringResult = actualTypeData.toString();
        assertEquals("42", actualTypeData.getLoginId());
        assertEquals("Msisdn", actualTypeData.getMsisdn());
        assertEquals("Voucher List", actualTypeData.getVoucherType());
        assertEquals("TypeData [loginId=42, msisdn=Msisdn]", actualToStringResult);
    }
}

