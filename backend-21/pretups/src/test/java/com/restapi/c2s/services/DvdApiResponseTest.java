package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DvdApiResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DvdApiResponse}
     *   <li>{@link DvdApiResponse#setTxnBatchId(String)}
     *   <li>{@link DvdApiResponse#setTxnDetailsList(List)}
     *   <li>{@link DvdApiResponse#toString()}
     *   <li>{@link DvdApiResponse#getTxnBatchId()}
     *   <li>{@link DvdApiResponse#getTxnDetailsList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DvdApiResponse actualDvdApiResponse = new DvdApiResponse();
        actualDvdApiResponse.setTxnBatchId("42");
        ArrayList<TxnIDBaseResponse> txnDetailsList = new ArrayList<>();
        actualDvdApiResponse.setTxnDetailsList(txnDetailsList);
        String actualToStringResult = actualDvdApiResponse.toString();
        assertEquals("42", actualDvdApiResponse.getTxnBatchId());
        assertSame(txnDetailsList, actualDvdApiResponse.getTxnDetailsList());
        assertEquals("DvdApiResponse [txnBatchId=42, txnDetailsList=[]]", actualToStringResult);
    }
}

