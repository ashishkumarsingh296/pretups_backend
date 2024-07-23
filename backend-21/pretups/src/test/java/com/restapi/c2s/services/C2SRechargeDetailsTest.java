package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2SRechargeDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SRechargeDetails}
     *   <li>{@link C2SRechargeDetails#setAmount(String)}
     *   <li>{@link C2SRechargeDetails#setDate(String)}
     *   <li>{@link C2SRechargeDetails#setExtcode(String)}
     *   <li>{@link C2SRechargeDetails#setExtnwcode(String)}
     *   <li>{@link C2SRechargeDetails#setExtrefnum(String)}
     *   <li>{@link C2SRechargeDetails#setGifterLang(String)}
     *   <li>{@link C2SRechargeDetails#setGifterMsisdn(String)}
     *   <li>{@link C2SRechargeDetails#setGifterName(String)}
     *   <li>{@link C2SRechargeDetails#setLanguage1(String)}
     *   <li>{@link C2SRechargeDetails#setLanguage2(String)}
     *   <li>{@link C2SRechargeDetails#setLoginid(String)}
     *   <li>{@link C2SRechargeDetails#setMsisdn2(String)}
     *   <li>{@link C2SRechargeDetails#setMsisdn(String)}
     *   <li>{@link C2SRechargeDetails#setNotifMsisdn(String)}
     *   <li>{@link C2SRechargeDetails#setPassword(String)}
     *   <li>{@link C2SRechargeDetails#setPin(String)}
     *   <li>{@link C2SRechargeDetails#setQty(String)}
     *   <li>{@link C2SRechargeDetails#setSelector(String)}
     *   <li>{@link C2SRechargeDetails#getAmount()}
     *   <li>{@link C2SRechargeDetails#getDate()}
     *   <li>{@link C2SRechargeDetails#getExtcode()}
     *   <li>{@link C2SRechargeDetails#getExtnwcode()}
     *   <li>{@link C2SRechargeDetails#getExtrefnum()}
     *   <li>{@link C2SRechargeDetails#getGifterLang()}
     *   <li>{@link C2SRechargeDetails#getGifterMsisdn()}
     *   <li>{@link C2SRechargeDetails#getGifterName()}
     *   <li>{@link C2SRechargeDetails#getLanguage1()}
     *   <li>{@link C2SRechargeDetails#getLanguage2()}
     *   <li>{@link C2SRechargeDetails#getLoginid()}
     *   <li>{@link C2SRechargeDetails#getMsisdn2()}
     *   <li>{@link C2SRechargeDetails#getMsisdn()}
     *   <li>{@link C2SRechargeDetails#getNotifMsisdn()}
     *   <li>{@link C2SRechargeDetails#getPassword()}
     *   <li>{@link C2SRechargeDetails#getPin()}
     *   <li>{@link C2SRechargeDetails#getQty()}
     *   <li>{@link C2SRechargeDetails#getSelector()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SRechargeDetails actualC2sRechargeDetails = new C2SRechargeDetails();
        actualC2sRechargeDetails.setAmount("10");
        actualC2sRechargeDetails.setDate("2020-03-01");
        actualC2sRechargeDetails.setExtcode("Extcode");
        actualC2sRechargeDetails.setExtnwcode("Extnwcode");
        actualC2sRechargeDetails.setExtrefnum("Extrefnum");
        actualC2sRechargeDetails.setGifterLang("Gifter Lang");
        actualC2sRechargeDetails.setGifterMsisdn("Gifter Msisdn");
        actualC2sRechargeDetails.setGifterName("Gifter Name");
        actualC2sRechargeDetails.setLanguage1("en");
        actualC2sRechargeDetails.setLanguage2("en");
        actualC2sRechargeDetails.setLoginid("Loginid");
        actualC2sRechargeDetails.setMsisdn2("Msisdn2");
        actualC2sRechargeDetails.setMsisdn("Msisdn");
        actualC2sRechargeDetails.setNotifMsisdn("Notif Msisdn");
        actualC2sRechargeDetails.setPassword("iloveyou");
        actualC2sRechargeDetails.setPin("Pin");
        actualC2sRechargeDetails.setQty("Qty");
        actualC2sRechargeDetails.setSelector("Selector");
        assertEquals("10", actualC2sRechargeDetails.getAmount());
        assertEquals("2020-03-01", actualC2sRechargeDetails.getDate());
        assertEquals("Extcode", actualC2sRechargeDetails.getExtcode());
        assertEquals("Extnwcode", actualC2sRechargeDetails.getExtnwcode());
        assertEquals("Extrefnum", actualC2sRechargeDetails.getExtrefnum());
        assertEquals("Gifter Lang", actualC2sRechargeDetails.getGifterLang());
        assertEquals("Gifter Msisdn", actualC2sRechargeDetails.getGifterMsisdn());
        assertEquals("Gifter Name", actualC2sRechargeDetails.getGifterName());
        assertEquals("en", actualC2sRechargeDetails.getLanguage1());
        assertEquals("en", actualC2sRechargeDetails.getLanguage2());
        assertEquals("Loginid", actualC2sRechargeDetails.getLoginid());
        assertEquals("Msisdn2", actualC2sRechargeDetails.getMsisdn2());
        assertEquals("Msisdn", actualC2sRechargeDetails.getMsisdn());
        assertEquals("Notif Msisdn", actualC2sRechargeDetails.getNotifMsisdn());
        assertEquals("iloveyou", actualC2sRechargeDetails.getPassword());
        assertEquals("Pin", actualC2sRechargeDetails.getPin());
        assertEquals("Qty", actualC2sRechargeDetails.getQty());
        assertEquals("Selector", actualC2sRechargeDetails.getSelector());
    }
}

