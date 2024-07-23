package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PaymentTypeVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PaymentTypeVO}
     *   <li>{@link PaymentTypeVO#setPaymentTypeCode(String)}
     *   <li>{@link PaymentTypeVO#setPaymentTypeName(String)}
     *   <li>{@link PaymentTypeVO#toString()}
     *   <li>{@link PaymentTypeVO#getPaymentTypeCode()}
     *   <li>{@link PaymentTypeVO#getPaymentTypeName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PaymentTypeVO actualPaymentTypeVO = new PaymentTypeVO();
        actualPaymentTypeVO.setPaymentTypeCode("Payment Type Code");
        actualPaymentTypeVO.setPaymentTypeName("Payment Type Name");
        actualPaymentTypeVO.toString();
        assertEquals("Payment Type Code", actualPaymentTypeVO.getPaymentTypeCode());
        assertEquals("Payment Type Name", actualPaymentTypeVO.getPaymentTypeName());
    }
}

