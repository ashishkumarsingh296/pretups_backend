package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PaymentdetailApprTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PaymentdetailAppr}
     *   <li>{@link PaymentdetailAppr#setPaymentdate(String)}
     *   <li>{@link PaymentdetailAppr#setPaymentinstnumber(String)}
     *   <li>{@link PaymentdetailAppr#setPaymenttype(String)}
     *   <li>{@link PaymentdetailAppr#toString()}
     *   <li>{@link PaymentdetailAppr#getPaymentdate()}
     *   <li>{@link PaymentdetailAppr#getPaymentinstnumber()}
     *   <li>{@link PaymentdetailAppr#getPaymenttype()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PaymentdetailAppr actualPaymentdetailAppr = new PaymentdetailAppr();
        actualPaymentdetailAppr.setPaymentdate("2020-03-01");
        actualPaymentdetailAppr.setPaymentinstnumber("42");
        actualPaymentdetailAppr.setPaymenttype("Paymenttype");
        String actualToStringResult = actualPaymentdetailAppr.toString();
        assertEquals("2020-03-01", actualPaymentdetailAppr.getPaymentdate());
        assertEquals("42", actualPaymentdetailAppr.getPaymentinstnumber());
        assertEquals("Paymenttype", actualPaymentdetailAppr.getPaymenttype());
        assertEquals("paymenttype = Paymenttypepaymentdate = 2020-03-01paymentinstnumber = 42", actualToStringResult);
    }
}

