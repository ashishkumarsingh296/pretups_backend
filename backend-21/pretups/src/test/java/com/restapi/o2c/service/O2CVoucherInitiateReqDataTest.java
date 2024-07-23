package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CVoucherInitiateReqDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CVoucherInitiateReqData}
     *   <li>{@link O2CVoucherInitiateReqData#setLanguage(String)}
     *   <li>{@link O2CVoucherInitiateReqData#setPaymentDetails(List)}
     *   <li>{@link O2CVoucherInitiateReqData#setPin(String)}
     *   <li>{@link O2CVoucherInitiateReqData#setRefnumber(String)}
     *   <li>{@link O2CVoucherInitiateReqData#setRemarks(String)}
     *   <li>{@link O2CVoucherInitiateReqData#setVoucherDetails(List)}
     *   <li>{@link O2CVoucherInitiateReqData#toString()}
     *   <li>{@link O2CVoucherInitiateReqData#getLanguage()}
     *   <li>{@link O2CVoucherInitiateReqData#getPaymentDetails()}
     *   <li>{@link O2CVoucherInitiateReqData#getPin()}
     *   <li>{@link O2CVoucherInitiateReqData#getRefnumber()}
     *   <li>{@link O2CVoucherInitiateReqData#getRemarks()}
     *   <li>{@link O2CVoucherInitiateReqData#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CVoucherInitiateReqData actualO2cVoucherInitiateReqData = new O2CVoucherInitiateReqData();
        actualO2cVoucherInitiateReqData.setLanguage("en");
        ArrayList<PaymentDetailsO2C> paymentdetails = new ArrayList<>();
        actualO2cVoucherInitiateReqData.setPaymentDetails(paymentdetails);
        actualO2cVoucherInitiateReqData.setPin("Pin");
        actualO2cVoucherInitiateReqData.setRefnumber("42");
        actualO2cVoucherInitiateReqData.setRemarks("Remarks");
        ArrayList<VoucherDetailsIni> voucherDetails = new ArrayList<>();
        actualO2cVoucherInitiateReqData.setVoucherDetails(voucherDetails);
        String actualToStringResult = actualO2cVoucherInitiateReqData.toString();
        assertEquals("en", actualO2cVoucherInitiateReqData.getLanguage());
        assertSame(paymentdetails, actualO2cVoucherInitiateReqData.getPaymentDetails());
        assertEquals("Pin", actualO2cVoucherInitiateReqData.getPin());
        assertEquals("42", actualO2cVoucherInitiateReqData.getRefnumber());
        assertEquals("Remarks", actualO2cVoucherInitiateReqData.getRemarks());
        assertSame(voucherDetails, actualO2cVoucherInitiateReqData.getVoucherDetails());
        assertEquals("o2CInitiateReqData = voucherDetails = []paymentdetails = []refnumber = 42language = enremarks ="
                + " Remarkslanguage = en", actualToStringResult);
    }
}

