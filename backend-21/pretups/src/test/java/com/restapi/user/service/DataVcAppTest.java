package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DataVcAppTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DataVcApp}
     *   <li>{@link DataVcApp#setExtcode(String)}
     *   <li>{@link DataVcApp#setExtnwcode(String)}
     *   <li>{@link DataVcApp#setLanguage1(String)}
     *   <li>{@link DataVcApp#setLanguage2(String)}
     *   <li>{@link DataVcApp#setLoginid(String)}
     *   <li>{@link DataVcApp#setMsisdn(String)}
     *   <li>{@link DataVcApp#setPassword(String)}
     *   <li>{@link DataVcApp#setPaymentinstcode(String)}
     *   <li>{@link DataVcApp#setPaymentinstdate(String)}
     *   <li>{@link DataVcApp#setPaymentinstnum(String)}
     *   <li>{@link DataVcApp#setPin(String)}
     *   <li>{@link DataVcApp#setRemarks(String)}
     *   <li>{@link DataVcApp#setStatus(String)}
     *   <li>{@link DataVcApp#setTransferId(String)}
     *   <li>{@link DataVcApp#setType(String)}
     *   <li>{@link DataVcApp#setVoucherDetails(List)}
     *   <li>{@link DataVcApp#toString()}
     *   <li>{@link DataVcApp#getExtcode()}
     *   <li>{@link DataVcApp#getExtnwcode()}
     *   <li>{@link DataVcApp#getLanguage1()}
     *   <li>{@link DataVcApp#getLanguage2()}
     *   <li>{@link DataVcApp#getLoginid()}
     *   <li>{@link DataVcApp#getMsisdn()}
     *   <li>{@link DataVcApp#getPassword()}
     *   <li>{@link DataVcApp#getPaymentinstcode()}
     *   <li>{@link DataVcApp#getPaymentinstdate()}
     *   <li>{@link DataVcApp#getPaymentinstnum()}
     *   <li>{@link DataVcApp#getPin()}
     *   <li>{@link DataVcApp#getRemarks()}
     *   <li>{@link DataVcApp#getStatus()}
     *   <li>{@link DataVcApp#getTransferId()}
     *   <li>{@link DataVcApp#getType()}
     *   <li>{@link DataVcApp#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DataVcApp actualDataVcApp = new DataVcApp();
        actualDataVcApp.setExtcode("Extcode");
        actualDataVcApp.setExtnwcode("Extnwcode");
        actualDataVcApp.setLanguage1("en");
        actualDataVcApp.setLanguage2("en");
        actualDataVcApp.setLoginid("Loginid");
        actualDataVcApp.setMsisdn("Msisdn");
        actualDataVcApp.setPassword("iloveyou");
        actualDataVcApp.setPaymentinstcode("Paymentinstcode");
        actualDataVcApp.setPaymentinstdate("2020-03-01");
        actualDataVcApp.setPaymentinstnum("Paymentinstnum");
        actualDataVcApp.setPin("Pin");
        actualDataVcApp.setRemarks("Remarks");
        actualDataVcApp.setStatus("Status");
        actualDataVcApp.setTransferId("42");
        actualDataVcApp.setType("Type");
        ArrayList<VoucherDetail> voucherDetails = new ArrayList<>();
        actualDataVcApp.setVoucherDetails(voucherDetails);
        String actualToStringResult = actualDataVcApp.toString();
        assertEquals("Extcode", actualDataVcApp.getExtcode());
        assertEquals("Extnwcode", actualDataVcApp.getExtnwcode());
        assertEquals("en", actualDataVcApp.getLanguage1());
        assertEquals("en", actualDataVcApp.getLanguage2());
        assertEquals("Loginid", actualDataVcApp.getLoginid());
        assertEquals("Msisdn", actualDataVcApp.getMsisdn());
        assertEquals("iloveyou", actualDataVcApp.getPassword());
        assertEquals("Paymentinstcode", actualDataVcApp.getPaymentinstcode());
        assertEquals("2020-03-01", actualDataVcApp.getPaymentinstdate());
        assertEquals("Paymentinstnum", actualDataVcApp.getPaymentinstnum());
        assertEquals("Pin", actualDataVcApp.getPin());
        assertEquals("Remarks", actualDataVcApp.getRemarks());
        assertEquals("Status", actualDataVcApp.getStatus());
        assertEquals("42", actualDataVcApp.getTransferId());
        assertEquals("Type", actualDataVcApp.getType());
        assertSame(voucherDetails, actualDataVcApp.getVoucherDetails());
        assertEquals(
                "transferId = 42language2 = enlanguage1entype = Typepaymentinstcode = Paymentinstcodepaymentinstdate ="
                        + " 2020-03-01paymentinstnum = PaymentinstnumvoucherDetails = []remarksRemarksstatus = Statusextnwcode ="
                        + " Extnwcodemsisdn = Msisdnpin = Pinloginid = Loginidpassword = iloveyouextcode = Extcode",
                actualToStringResult);
    }
}

