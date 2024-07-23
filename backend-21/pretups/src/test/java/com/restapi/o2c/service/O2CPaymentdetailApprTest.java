package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CPaymentdetailApprTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CPaymentdetailAppr}
     *   <li>{@link O2CPaymentdetailAppr#setPaymentDate(String)}
     *   <li>{@link O2CPaymentdetailAppr#setPaymentInstNumber(String)}
     *   <li>{@link O2CPaymentdetailAppr#setPaymentType(String)}
     *   <li>{@link O2CPaymentdetailAppr#toString()}
     *   <li>{@link O2CPaymentdetailAppr#getPaymentDate()}
     *   <li>{@link O2CPaymentdetailAppr#getPaymentInstNumber()}
     *   <li>{@link O2CPaymentdetailAppr#getPaymentType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CPaymentdetailAppr actualO2cPaymentdetailAppr = new O2CPaymentdetailAppr();
        actualO2cPaymentdetailAppr.setPaymentDate("2020-03-01");
        actualO2cPaymentdetailAppr.setPaymentInstNumber("42");
        actualO2cPaymentdetailAppr.setPaymentType("Payment Type");
        String actualToStringResult = actualO2cPaymentdetailAppr.toString();
        assertEquals("2020-03-01", actualO2cPaymentdetailAppr.getPaymentDate());
        assertEquals("42", actualO2cPaymentdetailAppr.getPaymentInstNumber());
        assertEquals("Payment Type", actualO2cPaymentdetailAppr.getPaymentType());
        assertEquals("O2CPaymentdetailAppr [paymentType=Payment Type, paymentInstNumber=42, paymentDate=2020-03-01]",
                actualToStringResult);
    }
}

