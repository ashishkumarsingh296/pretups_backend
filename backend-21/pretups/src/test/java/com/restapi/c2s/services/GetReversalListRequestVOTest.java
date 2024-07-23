package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GetReversalListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GetReversalListRequestVO}
     *   <li>{@link GetReversalListRequestVO#setReceiverMsisdn(String)}
     *   <li>{@link GetReversalListRequestVO#setSenderMsisdn(String)}
     *   <li>{@link GetReversalListRequestVO#setTxnID(String)}
     *   <li>{@link GetReversalListRequestVO#toString()}
     *   <li>{@link GetReversalListRequestVO#getReceiverMsisdn()}
     *   <li>{@link GetReversalListRequestVO#getSenderMsisdn()}
     *   <li>{@link GetReversalListRequestVO#getTxnID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetReversalListRequestVO actualGetReversalListRequestVO = new GetReversalListRequestVO();
        actualGetReversalListRequestVO.setReceiverMsisdn("Receiver Msisdn");
        actualGetReversalListRequestVO.setSenderMsisdn("Sender Msisdn");
        actualGetReversalListRequestVO.setTxnID("Txn ID");
        String actualToStringResult = actualGetReversalListRequestVO.toString();
        assertEquals("Receiver Msisdn", actualGetReversalListRequestVO.getReceiverMsisdn());
        assertEquals("Sender Msisdn", actualGetReversalListRequestVO.getSenderMsisdn());
        assertEquals("Txn ID", actualGetReversalListRequestVO.getTxnID());
        assertEquals("GetReversalListRequestVO [senderMsisdn=Sender Msisdn, receiverMsisdnReceiver Msisdn, txnID=Txn ID",
                actualToStringResult);
    }
}

