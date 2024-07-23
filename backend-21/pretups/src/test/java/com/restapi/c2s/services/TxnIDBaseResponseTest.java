package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TxnIDBaseResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TxnIDBaseResponse}
     *   <li>{@link TxnIDBaseResponse#setMessage(String)}
     *   <li>{@link TxnIDBaseResponse#setProfileID(String)}
     *   <li>{@link TxnIDBaseResponse#setProfileName(String)}
     *   <li>{@link TxnIDBaseResponse#setRow(String)}
     *   <li>{@link TxnIDBaseResponse#setTransactionDateTime(String)}
     *   <li>{@link TxnIDBaseResponse#setTransactionID(String)}
     *   <li>{@link TxnIDBaseResponse#setVoucherList(List)}
     *   <li>{@link TxnIDBaseResponse#toString()}
     *   <li>{@link TxnIDBaseResponse#getMessage()}
     *   <li>{@link TxnIDBaseResponse#getProfileID()}
     *   <li>{@link TxnIDBaseResponse#getProfileName()}
     *   <li>{@link TxnIDBaseResponse#getRow()}
     *   <li>{@link TxnIDBaseResponse#getTransactionDateTime()}
     *   <li>{@link TxnIDBaseResponse#getTransactionID()}
     *   <li>{@link TxnIDBaseResponse#getVoucherList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TxnIDBaseResponse actualTxnIDBaseResponse = new TxnIDBaseResponse();
        actualTxnIDBaseResponse.setMessage("Not all who wander are lost");
        actualTxnIDBaseResponse.setProfileID("Profile ID");
        actualTxnIDBaseResponse.setProfileName("foo.txt");
        actualTxnIDBaseResponse.setRow("Row");
        actualTxnIDBaseResponse.setTransactionDateTime("2020-03-01");
        actualTxnIDBaseResponse.setTransactionID("Transaction ID");
        ArrayList<String> voucherList = new ArrayList<>();
        actualTxnIDBaseResponse.setVoucherList(voucherList);
        String actualToStringResult = actualTxnIDBaseResponse.toString();
        assertEquals("Not all who wander are lost", actualTxnIDBaseResponse.getMessage());
        assertEquals("Profile ID", actualTxnIDBaseResponse.getProfileID());
        assertEquals("foo.txt", actualTxnIDBaseResponse.getProfileName());
        assertEquals("Row", actualTxnIDBaseResponse.getRow());
        assertEquals("2020-03-01", actualTxnIDBaseResponse.getTransactionDateTime());
        assertEquals("Transaction ID", actualTxnIDBaseResponse.getTransactionID());
        assertSame(voucherList, actualTxnIDBaseResponse.getVoucherList());
        assertEquals("TxnIDBaseResponse [row=Row, transactionID=Transaction ID, message=Not all who wander are lost,"
                + " profileID=Profile ID, voucherList=[], transactionDateTime=2020-03-01]", actualToStringResult);
    }
}

