package com.restapi.c2s.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class GiftRechargeRequestVOTest {
    /**
     * Method under test: {@link GiftRechargeRequestVO#getData()}
     */
    @Test
    public void testGetData() {
        assertNull((new GiftRechargeRequestVO()).getData());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GiftRechargeRequestVO}
     *   <li>{@link GiftRechargeRequestVO#setData(GiftRechargeDetails)}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GiftRechargeRequestVO actualGiftRechargeRequestVO = new GiftRechargeRequestVO();
        GiftRechargeDetails data = new GiftRechargeDetails();
        data.setAmount("10");
        data.setDate("2020-03-01");
        data.setExtcode("Extcode");
        data.setExtnwcode("Extnwcode");
        data.setExtrefnum("Extrefnum");
        data.setGifterLang("Gifter Lang");
        data.setGifterMsisdn("Gifter Msisdn");
        data.setGifterName("Gifter Name");
        data.setLanguage1("en");
        data.setLanguage2("en");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setMsisdn2("Msisdn2");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setSelector("Selector");
        data.setUserid("Userid");
        actualGiftRechargeRequestVO.setData(data);
        assertSame(data, actualGiftRechargeRequestVO.getData());
    }
}

