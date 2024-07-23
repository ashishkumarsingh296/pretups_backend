package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class VoucherTypeRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherTypeRequestVO}
     *   <li>{@link VoucherTypeRequestVO#setData(TypeData)}
     *   <li>{@link VoucherTypeRequestVO#setIdentifierType(String)}
     *   <li>{@link VoucherTypeRequestVO#setIdentifierValue(String)}
     *   <li>{@link VoucherTypeRequestVO#toString()}
     *   <li>{@link VoucherTypeRequestVO#getData()}
     *   <li>{@link VoucherTypeRequestVO#getIdentifierType()}
     *   <li>{@link VoucherTypeRequestVO#getIdentifierValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherTypeRequestVO actualVoucherTypeRequestVO = new VoucherTypeRequestVO();
        TypeData data = new TypeData();
        actualVoucherTypeRequestVO.setData(data);
        actualVoucherTypeRequestVO.setIdentifierType("Identifier Type");
        actualVoucherTypeRequestVO.setIdentifierValue("42");
        String actualToStringResult = actualVoucherTypeRequestVO.toString();
        assertSame(data, actualVoucherTypeRequestVO.getData());
        assertEquals("Identifier Type", actualVoucherTypeRequestVO.getIdentifierType());
        assertEquals("42", actualVoucherTypeRequestVO.getIdentifierValue());
        assertEquals(
                "VoucherTypeRequestVO [identifierType=Identifier Type, identifierValue=42, data=TypeData [loginId=null,"
                        + " msisdn=null]]",
                actualToStringResult);
    }
}

