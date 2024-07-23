package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CTxnReversalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CTxnReversalRequestVO}
     *   <li>{@link O2CTxnReversalRequestVO#setRemarks(String)}
     *   <li>{@link O2CTxnReversalRequestVO#setTransactionID(String)}
     *   <li>{@link O2CTxnReversalRequestVO#toString()}
     *   <li>{@link O2CTxnReversalRequestVO#getRemarks()}
     *   <li>{@link O2CTxnReversalRequestVO#getTransactionID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CTxnReversalRequestVO actualO2cTxnReversalRequestVO = new O2CTxnReversalRequestVO();
        actualO2cTxnReversalRequestVO.setRemarks("Remarks");
        actualO2cTxnReversalRequestVO.setTransactionID("Transaction ID");
        String actualToStringResult = actualO2cTxnReversalRequestVO.toString();
        assertEquals("Remarks", actualO2cTxnReversalRequestVO.getRemarks());
        assertEquals("Transaction ID", actualO2cTxnReversalRequestVO.getTransactionID());
        assertEquals("O2CTxnReversalRequestVO [transactionID=Transaction ID, remarks=Remarks]", actualToStringResult);
    }
}

