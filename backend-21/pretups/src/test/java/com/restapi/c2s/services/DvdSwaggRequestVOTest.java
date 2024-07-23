package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DvdSwaggRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DvdSwaggRequestVO}
     *   <li>{@link DvdSwaggRequestVO#setDate(String)}
     *   <li>{@link DvdSwaggRequestVO#setExtnwcode(String)}
     *   <li>{@link DvdSwaggRequestVO#setExtrefnum(String)}
     *   <li>{@link DvdSwaggRequestVO#setLanguage1(String)}
     *   <li>{@link DvdSwaggRequestVO#setLanguage2(String)}
     *   <li>{@link DvdSwaggRequestVO#setMsisdn2(String)}
     *   <li>{@link DvdSwaggRequestVO#setPin(String)}
     *   <li>{@link DvdSwaggRequestVO#setSelector(String)}
     *   <li>{@link DvdSwaggRequestVO#setSendSms(String)}
     *   <li>{@link DvdSwaggRequestVO#setVoucherDetails(List)}
     *   <li>{@link DvdSwaggRequestVO#toString()}
     *   <li>{@link DvdSwaggRequestVO#getDate()}
     *   <li>{@link DvdSwaggRequestVO#getExtnwcode()}
     *   <li>{@link DvdSwaggRequestVO#getExtrefnum()}
     *   <li>{@link DvdSwaggRequestVO#getLanguage1()}
     *   <li>{@link DvdSwaggRequestVO#getLanguage2()}
     *   <li>{@link DvdSwaggRequestVO#getMsisdn2()}
     *   <li>{@link DvdSwaggRequestVO#getPin()}
     *   <li>{@link DvdSwaggRequestVO#getSelector()}
     *   <li>{@link DvdSwaggRequestVO#getSendSms()}
     *   <li>{@link DvdSwaggRequestVO#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DvdSwaggRequestVO actualDvdSwaggRequestVO = new DvdSwaggRequestVO();
        actualDvdSwaggRequestVO.setDate("2020-03-01");
        actualDvdSwaggRequestVO.setExtnwcode("Extnwcode");
        actualDvdSwaggRequestVO.setExtrefnum("Extrefnum");
        actualDvdSwaggRequestVO.setLanguage1("en");
        actualDvdSwaggRequestVO.setLanguage2("en");
        actualDvdSwaggRequestVO.setMsisdn2("Msisdn2");
        actualDvdSwaggRequestVO.setPin("Pin");
        actualDvdSwaggRequestVO.setSelector("Selector");
        actualDvdSwaggRequestVO.setSendSms("Send Sms");
        ArrayList<DvdSwaggVoucherDetails> voucherDetails = new ArrayList<>();
        actualDvdSwaggRequestVO.setVoucherDetails(voucherDetails);
        String actualToStringResult = actualDvdSwaggRequestVO.toString();
        assertEquals("2020-03-01", actualDvdSwaggRequestVO.getDate());
        assertEquals("Extnwcode", actualDvdSwaggRequestVO.getExtnwcode());
        assertEquals("Extrefnum", actualDvdSwaggRequestVO.getExtrefnum());
        assertEquals("en", actualDvdSwaggRequestVO.getLanguage1());
        assertEquals("en", actualDvdSwaggRequestVO.getLanguage2());
        assertEquals("Msisdn2", actualDvdSwaggRequestVO.getMsisdn2());
        assertEquals("Pin", actualDvdSwaggRequestVO.getPin());
        assertEquals("Selector", actualDvdSwaggRequestVO.getSelector());
        assertEquals("Send Sms", actualDvdSwaggRequestVO.getSendSms());
        assertSame(voucherDetails, actualDvdSwaggRequestVO.getVoucherDetails());
        assertEquals(
                "DvdSwaggRequestVO [date=2020-03-01, language1=en, language2=en, extnwcode=Extnwcode, extrefnum=Extrefnum,"
                        + " msisdn2=Msisdn2, pin=Pin, selector=Selector, voucherDetails=[], sendSms=Send Sms]",
                actualToStringResult);
    }
}

