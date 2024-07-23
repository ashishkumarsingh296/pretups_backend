package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InternetRechargeDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link InternetRechargeDetails}
     *   <li>{@link InternetRechargeDetails#setAmount(String)}
     *   <li>{@link InternetRechargeDetails#setDate(String)}
     *   <li>{@link InternetRechargeDetails#setExtcode(String)}
     *   <li>{@link InternetRechargeDetails#setExtnwcode(String)}
     *   <li>{@link InternetRechargeDetails#setExtrefnum(String)}
     *   <li>{@link InternetRechargeDetails#setLanguage1(String)}
     *   <li>{@link InternetRechargeDetails#setLanguage2(String)}
     *   <li>{@link InternetRechargeDetails#setLoginid(String)}
     *   <li>{@link InternetRechargeDetails#setMsisdn2(String)}
     *   <li>{@link InternetRechargeDetails#setMsisdn(String)}
     *   <li>{@link InternetRechargeDetails#setNotifMsisdn(String)}
     *   <li>{@link InternetRechargeDetails#setPassword(String)}
     *   <li>{@link InternetRechargeDetails#setPin(String)}
     *   <li>{@link InternetRechargeDetails#setSelector(String)}
     *   <li>{@link InternetRechargeDetails#getAmount()}
     *   <li>{@link InternetRechargeDetails#getDate()}
     *   <li>{@link InternetRechargeDetails#getExtcode()}
     *   <li>{@link InternetRechargeDetails#getExtnwcode()}
     *   <li>{@link InternetRechargeDetails#getExtrefnum()}
     *   <li>{@link InternetRechargeDetails#getLanguage1()}
     *   <li>{@link InternetRechargeDetails#getLanguage2()}
     *   <li>{@link InternetRechargeDetails#getLoginid()}
     *   <li>{@link InternetRechargeDetails#getMsisdn2()}
     *   <li>{@link InternetRechargeDetails#getMsisdn()}
     *   <li>{@link InternetRechargeDetails#getNotifMsisdn()}
     *   <li>{@link InternetRechargeDetails#getPassword()}
     *   <li>{@link InternetRechargeDetails#getPin()}
     *   <li>{@link InternetRechargeDetails#getSelector()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        InternetRechargeDetails actualInternetRechargeDetails = new InternetRechargeDetails();
        actualInternetRechargeDetails.setAmount("10");
        actualInternetRechargeDetails.setDate("2020-03-01");
        actualInternetRechargeDetails.setExtcode("Extcode");
        actualInternetRechargeDetails.setExtnwcode("Extnwcode");
        actualInternetRechargeDetails.setExtrefnum("Extrefnum");
        actualInternetRechargeDetails.setLanguage1("en");
        actualInternetRechargeDetails.setLanguage2("en");
        actualInternetRechargeDetails.setLoginid("Loginid");
        actualInternetRechargeDetails.setMsisdn2("Msisdn2");
        actualInternetRechargeDetails.setMsisdn("Msisdn");
        actualInternetRechargeDetails.setNotifMsisdn("Notif Msisdn");
        actualInternetRechargeDetails.setPassword("iloveyou");
        actualInternetRechargeDetails.setPin("Pin");
        actualInternetRechargeDetails.setSelector("Selector");
        assertEquals("10", actualInternetRechargeDetails.getAmount());
        assertEquals("2020-03-01", actualInternetRechargeDetails.getDate());
        assertEquals("Extcode", actualInternetRechargeDetails.getExtcode());
        assertEquals("Extnwcode", actualInternetRechargeDetails.getExtnwcode());
        assertEquals("Extrefnum", actualInternetRechargeDetails.getExtrefnum());
        assertEquals("en", actualInternetRechargeDetails.getLanguage1());
        assertEquals("en", actualInternetRechargeDetails.getLanguage2());
        assertEquals("Loginid", actualInternetRechargeDetails.getLoginid());
        assertEquals("Msisdn2", actualInternetRechargeDetails.getMsisdn2());
        assertEquals("Msisdn", actualInternetRechargeDetails.getMsisdn());
        assertEquals("Notif Msisdn", actualInternetRechargeDetails.getNotifMsisdn());
        assertEquals("iloveyou", actualInternetRechargeDetails.getPassword());
        assertEquals("Pin", actualInternetRechargeDetails.getPin());
        assertEquals("Selector", actualInternetRechargeDetails.getSelector());
    }
}

