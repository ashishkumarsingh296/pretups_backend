package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CVoucherTransferReqDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CVoucherTransferReqData}
     *   <li>{@link O2CVoucherTransferReqData#setExtcode2(String)}
     *   <li>{@link O2CVoucherTransferReqData#setLanguage(String)}
     *   <li>{@link O2CVoucherTransferReqData#setLoginid2(String)}
     *   <li>{@link O2CVoucherTransferReqData#setMsisdn2(String)}
     *   <li>{@link O2CVoucherTransferReqData#setPaymentDetails(List)}
     *   <li>{@link O2CVoucherTransferReqData#setPin(String)}
     *   <li>{@link O2CVoucherTransferReqData#setRefnumber(String)}
     *   <li>{@link O2CVoucherTransferReqData#setRemarks(String)}
     *   <li>{@link O2CVoucherTransferReqData#setVoucherDetails(List)}
     *   <li>{@link O2CVoucherTransferReqData#toString()}
     *   <li>{@link O2CVoucherTransferReqData#getExtcode2()}
     *   <li>{@link O2CVoucherTransferReqData#getLanguage()}
     *   <li>{@link O2CVoucherTransferReqData#getLoginid2()}
     *   <li>{@link O2CVoucherTransferReqData#getMsisdn2()}
     *   <li>{@link O2CVoucherTransferReqData#getPaymentDetails()}
     *   <li>{@link O2CVoucherTransferReqData#getPin()}
     *   <li>{@link O2CVoucherTransferReqData#getRefnumber()}
     *   <li>{@link O2CVoucherTransferReqData#getRemarks()}
     *   <li>{@link O2CVoucherTransferReqData#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CVoucherTransferReqData actualO2cVoucherTransferReqData = new O2CVoucherTransferReqData();
        actualO2cVoucherTransferReqData.setExtcode2("Extcode2");
        actualO2cVoucherTransferReqData.setLanguage("en");
        actualO2cVoucherTransferReqData.setLoginid2("Loginid2");
        actualO2cVoucherTransferReqData.setMsisdn2("Msisdn2");
        ArrayList<PaymentDetailsO2C> paymentdetails = new ArrayList<>();
        actualO2cVoucherTransferReqData.setPaymentDetails(paymentdetails);
        actualO2cVoucherTransferReqData.setPin("Pin");
        actualO2cVoucherTransferReqData.setRefnumber("42");
        actualO2cVoucherTransferReqData.setRemarks("Remarks");
        ArrayList<VoucherDetails> voucherDetails = new ArrayList<>();
        actualO2cVoucherTransferReqData.setVoucherDetails(voucherDetails);
        String actualToStringResult = actualO2cVoucherTransferReqData.toString();
        assertEquals("Extcode2", actualO2cVoucherTransferReqData.getExtcode2());
        assertEquals("en", actualO2cVoucherTransferReqData.getLanguage());
        assertEquals("Loginid2", actualO2cVoucherTransferReqData.getLoginid2());
        assertEquals("Msisdn2", actualO2cVoucherTransferReqData.getMsisdn2());
        assertSame(paymentdetails, actualO2cVoucherTransferReqData.getPaymentDetails());
        assertEquals("Pin", actualO2cVoucherTransferReqData.getPin());
        assertEquals("42", actualO2cVoucherTransferReqData.getRefnumber());
        assertEquals("Remarks", actualO2cVoucherTransferReqData.getRemarks());
        assertSame(voucherDetails, actualO2cVoucherTransferReqData.getVoucherDetails());
        assertEquals("O2CVoucherTransferReqData [msisdn2=Msisdn2, loginid2=Loginid2, extcode2=Extcode2, voucherDetails=[],"
                + " paymentDetails=[], pin=Pin, refnumber=42, remarks=Remarks, language=en]", actualToStringResult);
    }
}

