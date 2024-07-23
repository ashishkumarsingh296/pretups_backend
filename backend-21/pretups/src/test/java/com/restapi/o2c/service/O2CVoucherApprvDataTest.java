package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CVoucherApprvDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CVoucherApprvData}
     *   <li>{@link O2CVoucherApprvData#setApprovalLevel(String)}
     *   <li>{@link O2CVoucherApprvData#setExternalTxnDate(String)}
     *   <li>{@link O2CVoucherApprvData#setExternalTxnNum(String)}
     *   <li>{@link O2CVoucherApprvData#setPaymentDetails(List)}
     *   <li>{@link O2CVoucherApprvData#setRefrenceNumber(String)}
     *   <li>{@link O2CVoucherApprvData#setRemarks(String)}
     *   <li>{@link O2CVoucherApprvData#setStatus(String)}
     *   <li>{@link O2CVoucherApprvData#setToUserId(String)}
     *   <li>{@link O2CVoucherApprvData#setTransactionId(String)}
     *   <li>{@link O2CVoucherApprvData#setTransferDate(String)}
     *   <li>{@link O2CVoucherApprvData#setVoucherDetails(List)}
     *   <li>{@link O2CVoucherApprvData#getApprovalLevel()}
     *   <li>{@link O2CVoucherApprvData#getExternalTxnDate()}
     *   <li>{@link O2CVoucherApprvData#getExternalTxnNum()}
     *   <li>{@link O2CVoucherApprvData#getPaymentDetails()}
     *   <li>{@link O2CVoucherApprvData#getRefrenceNumber()}
     *   <li>{@link O2CVoucherApprvData#getRemarks()}
     *   <li>{@link O2CVoucherApprvData#getStatus()}
     *   <li>{@link O2CVoucherApprvData#getToUserId()}
     *   <li>{@link O2CVoucherApprvData#getTransactionId()}
     *   <li>{@link O2CVoucherApprvData#getTransferDate()}
     *   <li>{@link O2CVoucherApprvData#getVoucherDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CVoucherApprvData actualO2cVoucherApprvData = new O2CVoucherApprvData();
        actualO2cVoucherApprvData.setApprovalLevel("Approval Level");
        actualO2cVoucherApprvData.setExternalTxnDate("2020-03-01");
        actualO2cVoucherApprvData.setExternalTxnNum("External Txn Num");
        ArrayList<PaymentDetailsO2C> paymentDetails = new ArrayList<>();
        actualO2cVoucherApprvData.setPaymentDetails(paymentDetails);
        actualO2cVoucherApprvData.setRefrenceNumber("42");
        actualO2cVoucherApprvData.setRemarks("Remarks");
        actualO2cVoucherApprvData.setStatus("Status");
        actualO2cVoucherApprvData.setToUserId("42");
        actualO2cVoucherApprvData.setTransactionId("42");
        actualO2cVoucherApprvData.setTransferDate("2020-03-01");
        ArrayList<VoucherDetailsApprv> voucherDetails = new ArrayList<>();
        actualO2cVoucherApprvData.setVoucherDetails(voucherDetails);
        assertEquals("Approval Level", actualO2cVoucherApprvData.getApprovalLevel());
        assertEquals("2020-03-01", actualO2cVoucherApprvData.getExternalTxnDate());
        assertEquals("External Txn Num", actualO2cVoucherApprvData.getExternalTxnNum());
        assertSame(paymentDetails, actualO2cVoucherApprvData.getPaymentDetails());
        assertEquals("42", actualO2cVoucherApprvData.getRefrenceNumber());
        assertEquals("Remarks", actualO2cVoucherApprvData.getRemarks());
        assertEquals("Status", actualO2cVoucherApprvData.getStatus());
        assertEquals("42", actualO2cVoucherApprvData.getToUserId());
        assertEquals("42", actualO2cVoucherApprvData.getTransactionId());
        assertEquals("2020-03-01", actualO2cVoucherApprvData.getTransferDate());
        assertSame(voucherDetails, actualO2cVoucherApprvData.getVoucherDetails());
    }
}

