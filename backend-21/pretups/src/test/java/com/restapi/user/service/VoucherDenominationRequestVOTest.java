package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class VoucherDenominationRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherDenominationRequestVO}
     *   <li>{@link VoucherDenominationRequestVO#setData(DenominationData)}
     *   <li>{@link VoucherDenominationRequestVO#setIdentifierType(String)}
     *   <li>{@link VoucherDenominationRequestVO#setIdentifierValue(String)}
     *   <li>{@link VoucherDenominationRequestVO#getData()}
     *   <li>{@link VoucherDenominationRequestVO#getIdentifierType()}
     *   <li>{@link VoucherDenominationRequestVO#getIdentifierValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherDenominationRequestVO actualVoucherDenominationRequestVO = new VoucherDenominationRequestVO();
        DenominationData data = new DenominationData();
        actualVoucherDenominationRequestVO.setData(data);
        actualVoucherDenominationRequestVO.setIdentifierType("Identifier Type");
        actualVoucherDenominationRequestVO.setIdentifierValue("42");
        assertSame(data, actualVoucherDenominationRequestVO.getData());
        assertEquals("Identifier Type", actualVoucherDenominationRequestVO.getIdentifierType());
        assertEquals("42", actualVoucherDenominationRequestVO.getIdentifierValue());
    }
}

