package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GiftRechargeDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GiftRechargeDetails}
     *   <li>{@link GiftRechargeDetails#setAmount(String)}
     *   <li>{@link GiftRechargeDetails#setDate(String)}
     *   <li>{@link GiftRechargeDetails#setExtcode(String)}
     *   <li>{@link GiftRechargeDetails#setExtnwcode(String)}
     *   <li>{@link GiftRechargeDetails#setExtrefnum(String)}
     *   <li>{@link GiftRechargeDetails#setGifterLang(String)}
     *   <li>{@link GiftRechargeDetails#setGifterMsisdn(String)}
     *   <li>{@link GiftRechargeDetails#setGifterName(String)}
     *   <li>{@link GiftRechargeDetails#setLanguage1(String)}
     *   <li>{@link GiftRechargeDetails#setLanguage2(String)}
     *   <li>{@link GiftRechargeDetails#setLoginid(String)}
     *   <li>{@link GiftRechargeDetails#setMsisdn2(String)}
     *   <li>{@link GiftRechargeDetails#setMsisdn(String)}
     *   <li>{@link GiftRechargeDetails#setPassword(String)}
     *   <li>{@link GiftRechargeDetails#setPin(String)}
     *   <li>{@link GiftRechargeDetails#setSelector(String)}
     *   <li>{@link GiftRechargeDetails#getAmount()}
     *   <li>{@link GiftRechargeDetails#getDate()}
     *   <li>{@link GiftRechargeDetails#getExtcode()}
     *   <li>{@link GiftRechargeDetails#getExtnwcode()}
     *   <li>{@link GiftRechargeDetails#getExtrefnum()}
     *   <li>{@link GiftRechargeDetails#getGifterLang()}
     *   <li>{@link GiftRechargeDetails#getGifterMsisdn()}
     *   <li>{@link GiftRechargeDetails#getGifterName()}
     *   <li>{@link GiftRechargeDetails#getLanguage1()}
     *   <li>{@link GiftRechargeDetails#getLanguage2()}
     *   <li>{@link GiftRechargeDetails#getLoginid()}
     *   <li>{@link GiftRechargeDetails#getMsisdn2()}
     *   <li>{@link GiftRechargeDetails#getMsisdn()}
     *   <li>{@link GiftRechargeDetails#getPassword()}
     *   <li>{@link GiftRechargeDetails#getPin()}
     *   <li>{@link GiftRechargeDetails#getSelector()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GiftRechargeDetails actualGiftRechargeDetails = new GiftRechargeDetails();
        actualGiftRechargeDetails.setAmount("10");
        actualGiftRechargeDetails.setDate("2020-03-01");
        actualGiftRechargeDetails.setExtcode("Extcode");
        actualGiftRechargeDetails.setExtnwcode("Extnwcode");
        actualGiftRechargeDetails.setExtrefnum("Extrefnum");
        actualGiftRechargeDetails.setGifterLang("Gifter Lang");
        actualGiftRechargeDetails.setGifterMsisdn("Gifter Msisdn");
        actualGiftRechargeDetails.setGifterName("Gifter Name");
        actualGiftRechargeDetails.setLanguage1("en");
        actualGiftRechargeDetails.setLanguage2("en");
        actualGiftRechargeDetails.setLoginid("Loginid");
        actualGiftRechargeDetails.setMsisdn2("Msisdn2");
        actualGiftRechargeDetails.setMsisdn("Msisdn");
        actualGiftRechargeDetails.setPassword("iloveyou");
        actualGiftRechargeDetails.setPin("Pin");
        actualGiftRechargeDetails.setSelector("Selector");
        assertEquals("10", actualGiftRechargeDetails.getAmount());
        assertEquals("2020-03-01", actualGiftRechargeDetails.getDate());
        assertEquals("Extcode", actualGiftRechargeDetails.getExtcode());
        assertEquals("Extnwcode", actualGiftRechargeDetails.getExtnwcode());
        assertEquals("Extrefnum", actualGiftRechargeDetails.getExtrefnum());
        assertEquals("Gifter Lang", actualGiftRechargeDetails.getGifterLang());
        assertEquals("Gifter Msisdn", actualGiftRechargeDetails.getGifterMsisdn());
        assertEquals("Gifter Name", actualGiftRechargeDetails.getGifterName());
        assertEquals("en", actualGiftRechargeDetails.getLanguage1());
        assertEquals("en", actualGiftRechargeDetails.getLanguage2());
        assertEquals("Loginid", actualGiftRechargeDetails.getLoginid());
        assertEquals("Msisdn2", actualGiftRechargeDetails.getMsisdn2());
        assertEquals("Msisdn", actualGiftRechargeDetails.getMsisdn());
        assertEquals("iloveyou", actualGiftRechargeDetails.getPassword());
        assertEquals("Pin", actualGiftRechargeDetails.getPin());
        assertEquals("Selector", actualGiftRechargeDetails.getSelector());
    }
}

