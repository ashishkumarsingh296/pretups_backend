package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class VoucherInfoRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherInfoRequestVO}
     *   <li>{@link VoucherInfoRequestVO#setData(InfoData)}
     *   <li>{@link VoucherInfoRequestVO#setIdentifierType(String)}
     *   <li>{@link VoucherInfoRequestVO#setIdentifierValue(String)}
     *   <li>{@link VoucherInfoRequestVO#toString()}
     *   <li>{@link VoucherInfoRequestVO#getData()}
     *   <li>{@link VoucherInfoRequestVO#getIdentifierType()}
     *   <li>{@link VoucherInfoRequestVO#getIdentifierValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherInfoRequestVO actualVoucherInfoRequestVO = new VoucherInfoRequestVO();
        InfoData data = new InfoData();
        actualVoucherInfoRequestVO.setData(data);
        actualVoucherInfoRequestVO.setIdentifierType("Identifier Type");
        actualVoucherInfoRequestVO.setIdentifierValue("42");
        String actualToStringResult = actualVoucherInfoRequestVO.toString();
        assertSame(data, actualVoucherInfoRequestVO.getData());
        assertEquals("Identifier Type", actualVoucherInfoRequestVO.getIdentifierType());
        assertEquals("42", actualVoucherInfoRequestVO.getIdentifierValue());
        assertEquals(
                "VoucherInfoRequestVO [identifierType=Identifier Type, identifierValue=42, data=InfoData [loginId=null,"
                        + " msisdn=null]]",
                actualToStringResult);
    }
}

