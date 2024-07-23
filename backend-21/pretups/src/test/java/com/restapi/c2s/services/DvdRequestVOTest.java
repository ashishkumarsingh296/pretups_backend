package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class DvdRequestVOTest {
    /**
     * Method under test: {@link DvdRequestVO#getData()}
     */
    @Test
    public void testGetData() {
        assertNull((new DvdRequestVO()).getData());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DvdRequestVO}
     *   <li>{@link DvdRequestVO#setData(DvdDetails)}
     *   <li>{@link DvdRequestVO#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DvdRequestVO actualDvdRequestVO = new DvdRequestVO();
        DvdDetails data = new DvdDetails();
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
        data.setQuantity("Quantity");
        data.setRowCount(3);
        data.setRowSize(3);
        data.setSelector("Selector");
        data.setSendSms("Send Sms");
        data.setUserid("Userid");
        data.setVoucherprofile("Voucherprofile");
        data.setVouchersegment("Vouchersegment");
        data.setVouchertype("Vouchertype");
        actualDvdRequestVO.setData(data);
        String actualToStringResult = actualDvdRequestVO.toString();
        assertSame(data, actualDvdRequestVO.getData());
        assertEquals("DvdRequestVO [data=DvdDetails [date=2020-03-01, language1=en, language2=en, extnwcode=Extnwcode,"
                + " extrefnum=Extrefnum, msisdn2=Msisdn2, pin=Pin, selector=Selector, vouchertype=Vouchertype,"
                + " vouchersegment=Vouchersegment, voucherprofile=Voucherprofile, amount=10, quantity=Quantity, rowSize=3,"
                + " rowCount=3, sendSms=Send Sms]]", actualToStringResult);
    }
}

