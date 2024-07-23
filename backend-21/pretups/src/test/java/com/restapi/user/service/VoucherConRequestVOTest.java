package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherConRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link VoucherConRequestVO#VoucherConRequestVO(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)}
     *   <li>{@link VoucherConRequestVO#setAmount(String)}
     *   <li>{@link VoucherConRequestVO#setExternalRefId(String)}
     *   <li>{@link VoucherConRequestVO#setExtnwcode(String)}
     *   <li>{@link VoucherConRequestVO#setExtrefnum(String)}
     *   <li>{@link VoucherConRequestVO#setInfo1(String)}
     *   <li>{@link VoucherConRequestVO#setInfo2(String)}
     *   <li>{@link VoucherConRequestVO#setInfo3(String)}
     *   <li>{@link VoucherConRequestVO#setInfo4(String)}
     *   <li>{@link VoucherConRequestVO#setInfo5(String)}
     *   <li>{@link VoucherConRequestVO#setLanguage1(String)}
     *   <li>{@link VoucherConRequestVO#setLanguage2(String)}
     *   <li>{@link VoucherConRequestVO#setMsisdn2(String)}
     *   <li>{@link VoucherConRequestVO#setMsisdn(String)}
     *   <li>{@link VoucherConRequestVO#setPin(String)}
     *   <li>{@link VoucherConRequestVO#setSelector(String)}
     *   <li>{@link VoucherConRequestVO#setSerialnumber(String)}
     *   <li>{@link VoucherConRequestVO#setVouchercode(String)}
     *   <li>{@link VoucherConRequestVO#getAmount()}
     *   <li>{@link VoucherConRequestVO#getExternalRefId()}
     *   <li>{@link VoucherConRequestVO#getExtnwcode()}
     *   <li>{@link VoucherConRequestVO#getExtrefnum()}
     *   <li>{@link VoucherConRequestVO#getInfo1()}
     *   <li>{@link VoucherConRequestVO#getInfo2()}
     *   <li>{@link VoucherConRequestVO#getInfo3()}
     *   <li>{@link VoucherConRequestVO#getInfo4()}
     *   <li>{@link VoucherConRequestVO#getInfo5()}
     *   <li>{@link VoucherConRequestVO#getLanguage1()}
     *   <li>{@link VoucherConRequestVO#getLanguage2()}
     *   <li>{@link VoucherConRequestVO#getMsisdn2()}
     *   <li>{@link VoucherConRequestVO#getMsisdn()}
     *   <li>{@link VoucherConRequestVO#getPin()}
     *   <li>{@link VoucherConRequestVO#getSelector()}
     *   <li>{@link VoucherConRequestVO#getSerialnumber()}
     *   <li>{@link VoucherConRequestVO#getVouchercode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherConRequestVO actualVoucherConRequestVO = new VoucherConRequestVO("42", "Extnwcode", "Msisdn", "Pin",
                "Selector", "Msisdn2", "en", "en", "Vouchercode", "42", "Info1", "Info2", "Info3", "Info4", "Info5", "10",
                "Extrefnum");
        actualVoucherConRequestVO.setAmount("10");
        actualVoucherConRequestVO.setExternalRefId("42");
        actualVoucherConRequestVO.setExtnwcode("Extnwcode");
        actualVoucherConRequestVO.setExtrefnum("Extrefnum");
        actualVoucherConRequestVO.setInfo1("Info1");
        actualVoucherConRequestVO.setInfo2("Info2");
        actualVoucherConRequestVO.setInfo3("Info3");
        actualVoucherConRequestVO.setInfo4("Info4");
        actualVoucherConRequestVO.setInfo5("Info5");
        actualVoucherConRequestVO.setLanguage1("en");
        actualVoucherConRequestVO.setLanguage2("en");
        actualVoucherConRequestVO.setMsisdn2("Msisdn2");
        actualVoucherConRequestVO.setMsisdn("Msisdn");
        actualVoucherConRequestVO.setPin("Pin");
        actualVoucherConRequestVO.setSelector("Selector");
        actualVoucherConRequestVO.setSerialnumber("42");
        actualVoucherConRequestVO.setVouchercode("Vouchercode");
        assertEquals("10", actualVoucherConRequestVO.getAmount());
        assertEquals("42", actualVoucherConRequestVO.getExternalRefId());
        assertEquals("Extnwcode", actualVoucherConRequestVO.getExtnwcode());
        assertEquals("Extrefnum", actualVoucherConRequestVO.getExtrefnum());
        assertEquals("Info1", actualVoucherConRequestVO.getInfo1());
        assertEquals("Info2", actualVoucherConRequestVO.getInfo2());
        assertEquals("Info3", actualVoucherConRequestVO.getInfo3());
        assertEquals("Info4", actualVoucherConRequestVO.getInfo4());
        assertEquals("Info5", actualVoucherConRequestVO.getInfo5());
        assertEquals("en", actualVoucherConRequestVO.getLanguage1());
        assertEquals("en", actualVoucherConRequestVO.getLanguage2());
        assertEquals("Msisdn2", actualVoucherConRequestVO.getMsisdn2());
        assertEquals("Msisdn", actualVoucherConRequestVO.getMsisdn());
        assertEquals("Pin", actualVoucherConRequestVO.getPin());
        assertEquals("Selector", actualVoucherConRequestVO.getSelector());
        assertEquals("42", actualVoucherConRequestVO.getSerialnumber());
        assertEquals("Vouchercode", actualVoucherConRequestVO.getVouchercode());
    }
}

