package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CDataStAppTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CDataStApp}
     *   <li>{@link O2CDataStApp#setCurrentStatus(String)}
     *   <li>{@link O2CDataStApp#setExtNwCode(String)}
     *   <li>{@link O2CDataStApp#setExtTxnDate(String)}
     *   <li>{@link O2CDataStApp#setExtTxnNumber(String)}
     *   <li>{@link O2CDataStApp#setPaymentDetails(O2CPaymentdetailAppr)}
     *   <li>{@link O2CDataStApp#setPin(String)}
     *   <li>{@link O2CDataStApp#setProducts(List)}
     *   <li>{@link O2CDataStApp#setRefNumber(String)}
     *   <li>{@link O2CDataStApp#setRemarks(String)}
     *   <li>{@link O2CDataStApp#setStatus(String)}
     *   <li>{@link O2CDataStApp#setToMsisdn(String)}
     *   <li>{@link O2CDataStApp#setTxnId(String)}
     *   <li>{@link O2CDataStApp#toString()}
     *   <li>{@link O2CDataStApp#getCurrentStatus()}
     *   <li>{@link O2CDataStApp#getExtNwCode()}
     *   <li>{@link O2CDataStApp#getExtTxnDate()}
     *   <li>{@link O2CDataStApp#getExtTxnNumber()}
     *   <li>{@link O2CDataStApp#getPaymentDetails()}
     *   <li>{@link O2CDataStApp#getPin()}
     *   <li>{@link O2CDataStApp#getProducts()}
     *   <li>{@link O2CDataStApp#getRefNumber()}
     *   <li>{@link O2CDataStApp#getRemarks()}
     *   <li>{@link O2CDataStApp#getStatus()}
     *   <li>{@link O2CDataStApp#getToMsisdn()}
     *   <li>{@link O2CDataStApp#getTxnId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CDataStApp actualO2cDataStApp = new O2CDataStApp();
        actualO2cDataStApp.setCurrentStatus("Current Status");
        actualO2cDataStApp.setExtNwCode("Ext Nw Code");
        actualO2cDataStApp.setExtTxnDate("2020-03-01");
        actualO2cDataStApp.setExtTxnNumber("42");
        O2CPaymentdetailAppr paymentDetails = new O2CPaymentdetailAppr();
        actualO2cDataStApp.setPaymentDetails(paymentDetails);
        actualO2cDataStApp.setPin("Pin");
        ArrayList<O2CProductAppr> products = new ArrayList<>();
        actualO2cDataStApp.setProducts(products);
        actualO2cDataStApp.setRefNumber("42");
        actualO2cDataStApp.setRemarks("Remarks");
        actualO2cDataStApp.setStatus("Status");
        actualO2cDataStApp.setToMsisdn("To Msisdn");
        actualO2cDataStApp.setTxnId("42");
        String actualToStringResult = actualO2cDataStApp.toString();
        assertEquals("Current Status", actualO2cDataStApp.getCurrentStatus());
        assertEquals("Ext Nw Code", actualO2cDataStApp.getExtNwCode());
        assertEquals("2020-03-01", actualO2cDataStApp.getExtTxnDate());
        assertEquals("42", actualO2cDataStApp.getExtTxnNumber());
        assertSame(paymentDetails, actualO2cDataStApp.getPaymentDetails());
        assertEquals("Pin", actualO2cDataStApp.getPin());
        assertSame(products, actualO2cDataStApp.getProducts());
        assertEquals("42", actualO2cDataStApp.getRefNumber());
        assertEquals("Remarks", actualO2cDataStApp.getRemarks());
        assertEquals("Status", actualO2cDataStApp.getStatus());
        assertEquals("To Msisdn", actualO2cDataStApp.getToMsisdn());
        assertEquals("42", actualO2cDataStApp.getTxnId());
        assertEquals("O2CDataStApp [currentStatus=Current Status, extNwCode=Ext Nw Code, status=Status, txnId=42, pin=Pin,"
                        + " remarks=Remarks, extTxnNumber=42, extTxnDate=2020-03-01, products=[], paymentDetails=O2CPaymentdetailAppr"
                        + " [paymentType=null, paymentInstNumber=null, paymentDate=null], refNumber=42, toMsisdn=To Msisdn]",
                actualToStringResult);
    }
}

