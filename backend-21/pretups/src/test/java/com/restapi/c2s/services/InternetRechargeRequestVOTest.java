package com.restapi.c2s.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class InternetRechargeRequestVOTest {
    /**
     * Method under test: {@link InternetRechargeRequestVO#getData()}
     */
    @Test
    public void testGetData() {
        assertNull((new InternetRechargeRequestVO()).getData());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link InternetRechargeRequestVO}
     *   <li>{@link InternetRechargeRequestVO#setData(InternetRechargeDetails)}
     * </ul>
     */
    @Test
    public void testConstructor() {
        InternetRechargeRequestVO actualInternetRechargeRequestVO = new InternetRechargeRequestVO();
        InternetRechargeDetails data = new InternetRechargeDetails();
        data.setAmount("10");
        data.setDate("2020-03-01");
        data.setExtcode("Extcode");
        data.setExtnwcode("Extnwcode");
        data.setExtrefnum("Extrefnum");
        data.setLanguage1("en");
        data.setLanguage2("en");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setMsisdn2("Msisdn2");
        data.setNotifMsisdn("Notif Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setSelector("Selector");
        data.setUserid("Userid");
        actualInternetRechargeRequestVO.setData(data);
        assertSame(data, actualInternetRechargeRequestVO.getData());
    }
}

