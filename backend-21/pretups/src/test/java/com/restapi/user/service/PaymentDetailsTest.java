package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PaymentDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PaymentDetails}
     *   <li>{@link PaymentDetails#setPaymentdate(String)}
     *   <li>{@link PaymentDetails#setPaymentinstnumber(String)}
     *   <li>{@link PaymentDetails#setPaymenttype(String)}
     *   <li>{@link PaymentDetails#toString()}
     *   <li>{@link PaymentDetails#getPaymentdate()}
     *   <li>{@link PaymentDetails#getPaymentinstnumber()}
     *   <li>{@link PaymentDetails#getPaymenttype()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PaymentDetails actualPaymentDetails = new PaymentDetails();
        actualPaymentDetails.setPaymentdate("2020-03-01");
        actualPaymentDetails.setPaymentinstnumber("42");
        actualPaymentDetails.setPaymenttype("Paymenttype");
        String actualToStringResult = actualPaymentDetails.toString();
        assertEquals("2020-03-01", actualPaymentDetails.getPaymentdate());
        assertEquals("42", actualPaymentDetails.getPaymentinstnumber());
        assertEquals("Paymenttype", actualPaymentDetails.getPaymenttype());
        assertEquals("paymenttypePaymenttypepaymentinstnumber = 42paymentdate = 2020-03-01", actualToStringResult);
    }
}

