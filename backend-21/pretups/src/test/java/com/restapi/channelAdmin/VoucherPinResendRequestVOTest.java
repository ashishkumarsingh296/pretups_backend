package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherPinResendRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherPinResendRequestVO}
     *   <li>{@link VoucherPinResendRequestVO#setCustomerMsisdn(String)}
     *   <li>{@link VoucherPinResendRequestVO#setDate(String)}
     *   <li>{@link VoucherPinResendRequestVO#setRemarks(String)}
     *   <li>{@link VoucherPinResendRequestVO#setRequestGatewayCode(String)}
     *   <li>{@link VoucherPinResendRequestVO#setSerialNo(String)}
     *   <li>{@link VoucherPinResendRequestVO#setSubscriberMsisdn(String)}
     *   <li>{@link VoucherPinResendRequestVO#setTransactionid(String)}
     *   <li>{@link VoucherPinResendRequestVO#getCustomerMsisdn()}
     *   <li>{@link VoucherPinResendRequestVO#getDate()}
     *   <li>{@link VoucherPinResendRequestVO#getRemarks()}
     *   <li>{@link VoucherPinResendRequestVO#getRequestGatewayCode()}
     *   <li>{@link VoucherPinResendRequestVO#getSerialNo()}
     *   <li>{@link VoucherPinResendRequestVO#getSubscriberMsisdn()}
     *   <li>{@link VoucherPinResendRequestVO#getTransactionid()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherPinResendRequestVO actualVoucherPinResendRequestVO = new VoucherPinResendRequestVO();
        actualVoucherPinResendRequestVO.setCustomerMsisdn("Customer Msisdn");
        actualVoucherPinResendRequestVO.setDate("2020-03-01");
        actualVoucherPinResendRequestVO.setRemarks("Remarks");
        actualVoucherPinResendRequestVO.setRequestGatewayCode("Request Gateway Code");
        actualVoucherPinResendRequestVO.setSerialNo("Serial No");
        actualVoucherPinResendRequestVO.setSubscriberMsisdn("Subscriber Msisdn");
        actualVoucherPinResendRequestVO.setTransactionid("Transactionid");
        assertEquals("Customer Msisdn", actualVoucherPinResendRequestVO.getCustomerMsisdn());
        assertEquals("2020-03-01", actualVoucherPinResendRequestVO.getDate());
        assertEquals("Remarks", actualVoucherPinResendRequestVO.getRemarks());
        assertEquals("Request Gateway Code", actualVoucherPinResendRequestVO.getRequestGatewayCode());
        assertEquals("Serial No", actualVoucherPinResendRequestVO.getSerialNo());
        assertEquals("Subscriber Msisdn", actualVoucherPinResendRequestVO.getSubscriberMsisdn());
        assertEquals("Transactionid", actualVoucherPinResendRequestVO.getTransactionid());
    }
}

