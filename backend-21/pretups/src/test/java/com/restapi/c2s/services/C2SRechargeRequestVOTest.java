package com.restapi.c2s.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class C2SRechargeRequestVOTest {
    /**
     * Method under test: {@link C2SRechargeRequestVO#getData()}
     */
    @Test
    public void testGetData() {
        assertNull((new C2SRechargeRequestVO()).getData());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SRechargeRequestVO}
     *   <li>{@link C2SRechargeRequestVO#setData(C2SRechargeDetails)}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SRechargeRequestVO actualC2sRechargeRequestVO = new C2SRechargeRequestVO();
        C2SRechargeDetails data = new C2SRechargeDetails();
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
        data.setNotifMsisdn("Notif Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setQty("Qty");
        data.setSelector("Selector");
        data.setUserid("Userid");
        actualC2sRechargeRequestVO.setData(data);
        assertSame(data, actualC2sRechargeRequestVO.getData());
    }
}

