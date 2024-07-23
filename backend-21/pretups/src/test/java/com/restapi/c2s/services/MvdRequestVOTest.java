package com.restapi.c2s.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class MvdRequestVOTest {
    /**
     * Method under test: {@link MvdRequestVO#getData()}
     */
    @Test
    public void testGetData() {
        assertNull((new MvdRequestVO()).getData());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link MvdRequestVO}
     *   <li>{@link MvdRequestVO#setData(MvdDetails)}
     * </ul>
     */
    @Test
    public void testConstructor() {
        MvdRequestVO actualMvdRequestVO = new MvdRequestVO();
        MvdDetails data = new MvdDetails();
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
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setQty("Qty");
        data.setSelector("Selector");
        data.setUserid("Userid");
        actualMvdRequestVO.setData(data);
        assertSame(data, actualMvdRequestVO.getData());
    }
}

