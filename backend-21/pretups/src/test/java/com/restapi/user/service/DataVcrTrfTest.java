package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DataVcrTrfTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DataVcrTrf}
     *   <li>{@link DataVcrTrf#setExtcode2(String)}
     *   <li>{@link DataVcrTrf#setExtcode(String)}
     *   <li>{@link DataVcrTrf#setExtnwcode(String)}
     *   <li>{@link DataVcrTrf#setExtrefnum(String)}
     *   <li>{@link DataVcrTrf#setFileAttachment(String)}
     *   <li>{@link DataVcrTrf#setFileName(String)}
     *   <li>{@link DataVcrTrf#setFileType(String)}
     *   <li>{@link DataVcrTrf#setFileUploaded(String)}
     *   <li>{@link DataVcrTrf#setLanguage1(String)}
     *   <li>{@link DataVcrTrf#setLanguage2(String)}
     *   <li>{@link DataVcrTrf#setLoginid2(String)}
     *   <li>{@link DataVcrTrf#setLoginid(String)}
     *   <li>{@link DataVcrTrf#setMsisdn2(String)}
     *   <li>{@link DataVcrTrf#setMsisdn(String)}
     *   <li>{@link DataVcrTrf#setPassword(String)}
     *   <li>{@link DataVcrTrf#setPaymentinstcode(String)}
     *   <li>{@link DataVcrTrf#setPaymentinstdate(String)}
     *   <li>{@link DataVcrTrf#setPaymentinstnum(String)}
     *   <li>{@link DataVcrTrf#setPin(String)}
     *   <li>{@link DataVcrTrf#setRemarks(String)}
     *   <li>{@link DataVcrTrf#setVoucherDetails(List)}
     *   <li>{@link DataVcrTrf#toString()}
     *   <li>{@link DataVcrTrf#getExtcode2()}
     *   <li>{@link DataVcrTrf#getExtcode()}
     *   <li>{@link DataVcrTrf#getExtnwcode()}
     *   <li>{@link DataVcrTrf#getExtrefnum()}
     *   <li>{@link DataVcrTrf#getFileAttachment()}
     *   <li>{@link DataVcrTrf#getFileName()}
     *   <li>{@link DataVcrTrf#getFileType()}
     *   <li>{@link DataVcrTrf#getFileUploaded()}
     *   <li>{@link DataVcrTrf#getLanguage1()}
     *   <li>{@link DataVcrTrf#getLanguage2()}
     *   <li>{@link DataVcrTrf#getLoginid2()}
     *   <li>{@link DataVcrTrf#getLoginid()}
     *   <li>{@link DataVcrTrf#getMsisdn2()}
     *   <li>{@link DataVcrTrf#getMsisdn()}
     *   <li>{@link DataVcrTrf#getPassword()}
     *   <li>{@link DataVcrTrf#getPaymentinstcode()}
     *   <li>{@link DataVcrTrf#getPaymentinstdate()}
     *   <li>{@link DataVcrTrf#getPaymentinstnum()}
     *   <li>{@link DataVcrTrf#getPin()}
     *   <li>{@link DataVcrTrf#getRemarks()}
     *   <li>{@link DataVcrTrf#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DataVcrTrf actualDataVcrTrf = new DataVcrTrf();
        actualDataVcrTrf.setExtcode2("Extcode2");
        actualDataVcrTrf.setExtcode("Extcode");
        actualDataVcrTrf.setExtnwcode("Extnwcode");
        actualDataVcrTrf.setExtrefnum("Extrefnum");
        actualDataVcrTrf.setFileAttachment("File Attachment");
        actualDataVcrTrf.setFileName("foo.txt");
        actualDataVcrTrf.setFileType("File Type");
        actualDataVcrTrf.setFileUploaded("File Uploaded");
        actualDataVcrTrf.setLanguage1("en");
        actualDataVcrTrf.setLanguage2("en");
        actualDataVcrTrf.setLoginid2("Loginid2");
        actualDataVcrTrf.setLoginid("Loginid");
        actualDataVcrTrf.setMsisdn2("Msisdn2");
        actualDataVcrTrf.setMsisdn("Msisdn");
        actualDataVcrTrf.setPassword("iloveyou");
        actualDataVcrTrf.setPaymentinstcode("Paymentinstcode");
        actualDataVcrTrf.setPaymentinstdate("2020-03-01");
        actualDataVcrTrf.setPaymentinstnum("Paymentinstnum");
        actualDataVcrTrf.setPin("Pin");
        actualDataVcrTrf.setRemarks("Remarks");
        ArrayList<VoucherDetailTrf> voucherDetails = new ArrayList<>();
        actualDataVcrTrf.setVoucherDetails(voucherDetails);
        String actualToStringResult = actualDataVcrTrf.toString();
        assertEquals("Extcode2", actualDataVcrTrf.getExtcode2());
        assertEquals("Extcode", actualDataVcrTrf.getExtcode());
        assertEquals("Extnwcode", actualDataVcrTrf.getExtnwcode());
        assertEquals("Extrefnum", actualDataVcrTrf.getExtrefnum());
        assertEquals("File Attachment", actualDataVcrTrf.getFileAttachment());
        assertEquals("foo.txt", actualDataVcrTrf.getFileName());
        assertEquals("File Type", actualDataVcrTrf.getFileType());
        assertEquals("File Uploaded", actualDataVcrTrf.getFileUploaded());
        assertEquals("en", actualDataVcrTrf.getLanguage1());
        assertEquals("en", actualDataVcrTrf.getLanguage2());
        assertEquals("Loginid2", actualDataVcrTrf.getLoginid2());
        assertEquals("Loginid", actualDataVcrTrf.getLoginid());
        assertEquals("Msisdn2", actualDataVcrTrf.getMsisdn2());
        assertEquals("Msisdn", actualDataVcrTrf.getMsisdn());
        assertEquals("iloveyou", actualDataVcrTrf.getPassword());
        assertEquals("Paymentinstcode", actualDataVcrTrf.getPaymentinstcode());
        assertEquals("2020-03-01", actualDataVcrTrf.getPaymentinstdate());
        assertEquals("Paymentinstnum", actualDataVcrTrf.getPaymentinstnum());
        assertEquals("Pin", actualDataVcrTrf.getPin());
        assertEquals("Remarks", actualDataVcrTrf.getRemarks());
        assertSame(voucherDetails, actualDataVcrTrf.getVoucherDetails());
        assertEquals("language1enpaymentinstcode = Paymentinstcodepaymentinstdate = 2020-03-01paymentinstnum = Paymentinst"
                + "numvoucherDetails = []remarksRemarksextnwcode = Extnwcodemsisdn = Msisdnpin = Pinloginid = Loginidpassword"
                + " = iloveyouextcode = Extcodemsisdn2 = Msisdn2loginid2 = Loginid2extcode2 = Extcode2fileType = File"
                + " TypefileName = foo.txtfileAttachment = File AttachmentfileUploaded = File Uploaded", actualToStringResult);
    }
}

