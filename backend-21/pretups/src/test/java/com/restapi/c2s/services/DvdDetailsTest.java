package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DvdDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DvdDetails}
     *   <li>{@link DvdDetails#setAmount(String)}
     *   <li>{@link DvdDetails#setDate(String)}
     *   <li>{@link DvdDetails#setExtnwcode(String)}
     *   <li>{@link DvdDetails#setExtrefnum(String)}
     *   <li>{@link DvdDetails#setLanguage1(String)}
     *   <li>{@link DvdDetails#setLanguage2(String)}
     *   <li>{@link DvdDetails#setMsisdn2(String)}
     *   <li>{@link DvdDetails#setPin(String)}
     *   <li>{@link DvdDetails#setQuantity(String)}
     *   <li>{@link DvdDetails#setRowCount(Integer)}
     *   <li>{@link DvdDetails#setRowSize(Integer)}
     *   <li>{@link DvdDetails#setSelector(String)}
     *   <li>{@link DvdDetails#setSendSms(String)}
     *   <li>{@link DvdDetails#setVoucherprofile(String)}
     *   <li>{@link DvdDetails#setVouchersegment(String)}
     *   <li>{@link DvdDetails#setVouchertype(String)}
     *   <li>{@link DvdDetails#toString()}
     *   <li>{@link DvdDetails#getAmount()}
     *   <li>{@link DvdDetails#getDate()}
     *   <li>{@link DvdDetails#getExtnwcode()}
     *   <li>{@link DvdDetails#getExtrefnum()}
     *   <li>{@link DvdDetails#getLanguage1()}
     *   <li>{@link DvdDetails#getLanguage2()}
     *   <li>{@link DvdDetails#getMsisdn2()}
     *   <li>{@link DvdDetails#getPin()}
     *   <li>{@link DvdDetails#getQuantity()}
     *   <li>{@link DvdDetails#getRowCount()}
     *   <li>{@link DvdDetails#getRowSize()}
     *   <li>{@link DvdDetails#getSelector()}
     *   <li>{@link DvdDetails#getSendSms()}
     *   <li>{@link DvdDetails#getVoucherprofile()}
     *   <li>{@link DvdDetails#getVouchersegment()}
     *   <li>{@link DvdDetails#getVouchertype()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DvdDetails actualDvdDetails = new DvdDetails();
        actualDvdDetails.setAmount("10");
        actualDvdDetails.setDate("2020-03-01");
        actualDvdDetails.setExtnwcode("Extnwcode");
        actualDvdDetails.setExtrefnum("Extrefnum");
        actualDvdDetails.setLanguage1("en");
        actualDvdDetails.setLanguage2("en");
        actualDvdDetails.setMsisdn2("Msisdn2");
        actualDvdDetails.setPin("Pin");
        actualDvdDetails.setQuantity("Quantity");
        actualDvdDetails.setRowCount(3);
        actualDvdDetails.setRowSize(3);
        actualDvdDetails.setSelector("Selector");
        actualDvdDetails.setSendSms("Send Sms");
        actualDvdDetails.setVoucherprofile("Voucherprofile");
        actualDvdDetails.setVouchersegment("Vouchersegment");
        actualDvdDetails.setVouchertype("Vouchertype");
        String actualToStringResult = actualDvdDetails.toString();
        assertEquals("10", actualDvdDetails.getAmount());
        assertEquals("2020-03-01", actualDvdDetails.getDate());
        assertEquals("Extnwcode", actualDvdDetails.getExtnwcode());
        assertEquals("Extrefnum", actualDvdDetails.getExtrefnum());
        assertEquals("en", actualDvdDetails.getLanguage1());
        assertEquals("en", actualDvdDetails.getLanguage2());
        assertEquals("Msisdn2", actualDvdDetails.getMsisdn2());
        assertEquals("Pin", actualDvdDetails.getPin());
        assertEquals("Quantity", actualDvdDetails.getQuantity());
        assertEquals(3, actualDvdDetails.getRowCount().intValue());
        assertEquals(3, actualDvdDetails.getRowSize().intValue());
        assertEquals("Selector", actualDvdDetails.getSelector());
        assertEquals("Send Sms", actualDvdDetails.getSendSms());
        assertEquals("Voucherprofile", actualDvdDetails.getVoucherprofile());
        assertEquals("Vouchersegment", actualDvdDetails.getVouchersegment());
        assertEquals("Vouchertype", actualDvdDetails.getVouchertype());
        assertEquals("DvdDetails [date=2020-03-01, language1=en, language2=en, extnwcode=Extnwcode, extrefnum=Extrefnum,"
                        + " msisdn2=Msisdn2, pin=Pin, selector=Selector, vouchertype=Vouchertype, vouchersegment=Vouchersegment,"
                        + " voucherprofile=Voucherprofile, amount=10, quantity=Quantity, rowSize=3, rowCount=3, sendSms=Send" + " Sms]",
                actualToStringResult);
    }
}

