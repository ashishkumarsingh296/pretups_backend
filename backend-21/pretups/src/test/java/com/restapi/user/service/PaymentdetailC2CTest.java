package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PaymentdetailC2CTest {
    /**
     * Method under test: {@link PaymentdetailC2C#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        PaymentdetailC2C paymentdetailC2C = new PaymentdetailC2C();
        paymentdetailC2C.setAdditionalProperty("Name", "Value");
        assertEquals(1, paymentdetailC2C.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PaymentdetailC2C}
     *   <li>{@link PaymentdetailC2C#setPaymentdate(String)}
     *   <li>{@link PaymentdetailC2C#setPaymentinstnumber(String)}
     *   <li>{@link PaymentdetailC2C#setPaymenttype(String)}
     *   <li>{@link PaymentdetailC2C#getPaymentdate()}
     *   <li>{@link PaymentdetailC2C#getPaymentinstnumber()}
     *   <li>{@link PaymentdetailC2C#getPaymenttype()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PaymentdetailC2C actualPaymentdetailC2C = new PaymentdetailC2C();
        actualPaymentdetailC2C.setPaymentdate("2020-03-01");
        actualPaymentdetailC2C.setPaymentinstnumber("42");
        actualPaymentdetailC2C.setPaymenttype("Paymenttype");
        assertEquals("2020-03-01", actualPaymentdetailC2C.getPaymentdate());
        assertEquals("42", actualPaymentdetailC2C.getPaymentinstnumber());
        assertEquals("Paymenttype", actualPaymentdetailC2C.getPaymenttype());
    }
}

