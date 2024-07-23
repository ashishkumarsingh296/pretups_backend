package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DataStkTrfTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DataStkTrf}
     *   <li>{@link DataStkTrf#setExtcode2(String)}
     *   <li>{@link DataStkTrf#setExtcode(String)}
     *   <li>{@link DataStkTrf#setExtnwcode(String)}
     *   <li>{@link DataStkTrf#setFileAttachment(String)}
     *   <li>{@link DataStkTrf#setFileName(String)}
     *   <li>{@link DataStkTrf#setFileType(String)}
     *   <li>{@link DataStkTrf#setFileUploaded(String)}
     *   <li>{@link DataStkTrf#setLanguage1(String)}
     *   <li>{@link DataStkTrf#setLoginid2(String)}
     *   <li>{@link DataStkTrf#setLoginid(String)}
     *   <li>{@link DataStkTrf#setMsisdn2(String)}
     *   <li>{@link DataStkTrf#setMsisdn(String)}
     *   <li>{@link DataStkTrf#setPassword(String)}
     *   <li>{@link DataStkTrf#setPaymentDetails(List)}
     *   <li>{@link DataStkTrf#setPin(String)}
     *   <li>{@link DataStkTrf#setProducts(List)}
     *   <li>{@link DataStkTrf#setRefnumber(String)}
     *   <li>{@link DataStkTrf#setRemarks(String)}
     *   <li>{@link DataStkTrf#toString()}
     *   <li>{@link DataStkTrf#getExtcode2()}
     *   <li>{@link DataStkTrf#getExtcode()}
     *   <li>{@link DataStkTrf#getExtnwcode()}
     *   <li>{@link DataStkTrf#getFileAttachment()}
     *   <li>{@link DataStkTrf#getFileName()}
     *   <li>{@link DataStkTrf#getFileType()}
     *   <li>{@link DataStkTrf#getFileUploaded()}
     *   <li>{@link DataStkTrf#getLanguage1()}
     *   <li>{@link DataStkTrf#getLoginid2()}
     *   <li>{@link DataStkTrf#getLoginid()}
     *   <li>{@link DataStkTrf#getMsisdn2()}
     *   <li>{@link DataStkTrf#getMsisdn()}
     *   <li>{@link DataStkTrf#getPassword()}
     *   <li>{@link DataStkTrf#getPaymentDetails()}
     *   <li>{@link DataStkTrf#getPin()}
     *   <li>{@link DataStkTrf#getProducts()}
     *   <li>{@link DataStkTrf#getRefnumber()}
     *   <li>{@link DataStkTrf#getRemarks()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DataStkTrf actualDataStkTrf = new DataStkTrf();
        actualDataStkTrf.setExtcode2("Extcode2");
        actualDataStkTrf.setExtcode("Extcode");
        actualDataStkTrf.setExtnwcode("Extnwcode");
        actualDataStkTrf.setFileAttachment("File Attachment");
        actualDataStkTrf.setFileName("foo.txt");
        actualDataStkTrf.setFileType("File Type");
        actualDataStkTrf.setFileUploaded("File Uploaded");
        actualDataStkTrf.setLanguage1("en");
        actualDataStkTrf.setLoginid2("Loginid2");
        actualDataStkTrf.setLoginid("Loginid");
        actualDataStkTrf.setMsisdn2("Msisdn2");
        actualDataStkTrf.setMsisdn("Msisdn");
        actualDataStkTrf.setPassword("iloveyou");
        ArrayList<PaymentDetails> paymentdetails = new ArrayList<>();
        actualDataStkTrf.setPaymentDetails(paymentdetails);
        actualDataStkTrf.setPin("Pin");
        ArrayList<Products> products = new ArrayList<>();
        actualDataStkTrf.setProducts(products);
        actualDataStkTrf.setRefnumber("42");
        actualDataStkTrf.setRemarks("Remarks");
        String actualToStringResult = actualDataStkTrf.toString();
        assertEquals("Extcode2", actualDataStkTrf.getExtcode2());
        assertEquals("Extcode", actualDataStkTrf.getExtcode());
        assertEquals("Extnwcode", actualDataStkTrf.getExtnwcode());
        assertEquals("File Attachment", actualDataStkTrf.getFileAttachment());
        assertEquals("foo.txt", actualDataStkTrf.getFileName());
        assertEquals("File Type", actualDataStkTrf.getFileType());
        assertEquals("File Uploaded", actualDataStkTrf.getFileUploaded());
        assertEquals("en", actualDataStkTrf.getLanguage1());
        assertEquals("Loginid2", actualDataStkTrf.getLoginid2());
        assertEquals("Loginid", actualDataStkTrf.getLoginid());
        assertEquals("Msisdn2", actualDataStkTrf.getMsisdn2());
        assertEquals("Msisdn", actualDataStkTrf.getMsisdn());
        assertEquals("iloveyou", actualDataStkTrf.getPassword());
        assertSame(paymentdetails, actualDataStkTrf.getPaymentDetails());
        assertEquals("Pin", actualDataStkTrf.getPin());
        assertSame(products, actualDataStkTrf.getProducts());
        assertEquals("42", actualDataStkTrf.getRefnumber());
        assertEquals("Remarks", actualDataStkTrf.getRemarks());
        assertEquals("language1enproducts = []remarksRemarksextnwcode = Extnwcodemsisdn = Msisdnpin = Pinloginid ="
                + " Loginidpassword = iloveyouextcode = Extcodemsisdn2 = Msisdn2loginid2 = Loginid2extcode2 ="
                + " Extcode2paymentdetails = []fileName = foo.txtfileAttachment = File AttachmentfileType = File"
                + " TypefileUploaded = File Uploaded", actualToStringResult);
    }
}

