package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DatabuyvcrTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link Databuyvcr}
     *   <li>{@link Databuyvcr#setExtcode2(String)}
     *   <li>{@link Databuyvcr#setExtcode(String)}
     *   <li>{@link Databuyvcr#setExtnwcode(String)}
     *   <li>{@link Databuyvcr#setExtrefnum(String)}
     *   <li>{@link Databuyvcr#setFileAttachment(String)}
     *   <li>{@link Databuyvcr#setFileName(String)}
     *   <li>{@link Databuyvcr#setFileType(String)}
     *   <li>{@link Databuyvcr#setFileUploaded(String)}
     *   <li>{@link Databuyvcr#setLanguage1(String)}
     *   <li>{@link Databuyvcr#setLoginid2(String)}
     *   <li>{@link Databuyvcr#setLoginid(String)}
     *   <li>{@link Databuyvcr#setMsisdn2(String)}
     *   <li>{@link Databuyvcr#setMsisdn(String)}
     *   <li>{@link Databuyvcr#setPassword(String)}
     *   <li>{@link Databuyvcr#setPaymentinstcode(String)}
     *   <li>{@link Databuyvcr#setPaymentinstdate(String)}
     *   <li>{@link Databuyvcr#setPaymentinstnum(String)}
     *   <li>{@link Databuyvcr#setPin(String)}
     *   <li>{@link Databuyvcr#setRemarks(String)}
     *   <li>{@link Databuyvcr#setVoucherDetails(List)}
     *   <li>{@link Databuyvcr#toString()}
     *   <li>{@link Databuyvcr#getExtcode2()}
     *   <li>{@link Databuyvcr#getExtcode()}
     *   <li>{@link Databuyvcr#getExtnwcode()}
     *   <li>{@link Databuyvcr#getExtrefnum()}
     *   <li>{@link Databuyvcr#getFileAttachment()}
     *   <li>{@link Databuyvcr#getFileName()}
     *   <li>{@link Databuyvcr#getFileType()}
     *   <li>{@link Databuyvcr#getFileUploaded()}
     *   <li>{@link Databuyvcr#getLanguage1()}
     *   <li>{@link Databuyvcr#getLoginid2()}
     *   <li>{@link Databuyvcr#getLoginid()}
     *   <li>{@link Databuyvcr#getMsisdn2()}
     *   <li>{@link Databuyvcr#getMsisdn()}
     *   <li>{@link Databuyvcr#getPassword()}
     *   <li>{@link Databuyvcr#getPaymentinstcode()}
     *   <li>{@link Databuyvcr#getPaymentinstdate()}
     *   <li>{@link Databuyvcr#getPaymentinstnum()}
     *   <li>{@link Databuyvcr#getPin()}
     *   <li>{@link Databuyvcr#getRemarks()}
     *   <li>{@link Databuyvcr#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        Databuyvcr actualDatabuyvcr = new Databuyvcr();
        actualDatabuyvcr.setExtcode2("Extcode2");
        actualDatabuyvcr.setExtcode("Extcode");
        actualDatabuyvcr.setExtnwcode("Extnwcode");
        actualDatabuyvcr.setExtrefnum("Extrefnum");
        actualDatabuyvcr.setFileAttachment("File Attachment");
        actualDatabuyvcr.setFileName("foo.txt");
        actualDatabuyvcr.setFileType("File Type");
        actualDatabuyvcr.setFileUploaded("File Uploaded");
        actualDatabuyvcr.setLanguage1("en");
        actualDatabuyvcr.setLoginid2("Loginid2");
        actualDatabuyvcr.setLoginid("Loginid");
        actualDatabuyvcr.setMsisdn2("Msisdn2");
        actualDatabuyvcr.setMsisdn("Msisdn");
        actualDatabuyvcr.setPassword("iloveyou");
        actualDatabuyvcr.setPaymentinstcode("Paymentinstcode");
        actualDatabuyvcr.setPaymentinstdate("2020-03-01");
        actualDatabuyvcr.setPaymentinstnum("Paymentinstnum");
        actualDatabuyvcr.setPin("Pin");
        actualDatabuyvcr.setRemarks("Remarks");
        ArrayList<VoucherDetailBuy> voucherDetails = new ArrayList<>();
        actualDatabuyvcr.setVoucherDetails(voucherDetails);
        String actualToStringResult = actualDatabuyvcr.toString();
        assertEquals("Extcode2", actualDatabuyvcr.getExtcode2());
        assertEquals("Extcode", actualDatabuyvcr.getExtcode());
        assertEquals("Extnwcode", actualDatabuyvcr.getExtnwcode());
        assertEquals("Extrefnum", actualDatabuyvcr.getExtrefnum());
        assertEquals("File Attachment", actualDatabuyvcr.getFileAttachment());
        assertEquals("foo.txt", actualDatabuyvcr.getFileName());
        assertEquals("File Type", actualDatabuyvcr.getFileType());
        assertEquals("File Uploaded", actualDatabuyvcr.getFileUploaded());
        assertEquals("en", actualDatabuyvcr.getLanguage1());
        assertEquals("Loginid2", actualDatabuyvcr.getLoginid2());
        assertEquals("Loginid", actualDatabuyvcr.getLoginid());
        assertEquals("Msisdn2", actualDatabuyvcr.getMsisdn2());
        assertEquals("Msisdn", actualDatabuyvcr.getMsisdn());
        assertEquals("iloveyou", actualDatabuyvcr.getPassword());
        assertEquals("Paymentinstcode", actualDatabuyvcr.getPaymentinstcode());
        assertEquals("2020-03-01", actualDatabuyvcr.getPaymentinstdate());
        assertEquals("Paymentinstnum", actualDatabuyvcr.getPaymentinstnum());
        assertEquals("Pin", actualDatabuyvcr.getPin());
        assertEquals("Remarks", actualDatabuyvcr.getRemarks());
        assertSame(voucherDetails, actualDatabuyvcr.getVoucherDetails());
        assertEquals("language1enpaymentinstcode = Paymentinstcodepaymentinstdate = 2020-03-01paymentinstnum = Paymentinst"
                + "numvoucherDetails = []remarksRemarksextnwcode = Extnwcodemsisdn = Msisdnpin = Pinloginid = Loginidpassword"
                + " = iloveyouextcode = Extcodemsisdn2 = Msisdn2loginid2 = Loginid2extcode2 = Extcode2fileType = File"
                + " TypefileName = foo.txtfileAttachment = File AttachmentfileUploaded = File Uploaded", actualToStringResult);
    }
}

