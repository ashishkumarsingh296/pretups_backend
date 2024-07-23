package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CTxnReversalListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CTxnReversalListRequestVO}
     *   <li>{@link O2CTxnReversalListRequestVO#setCategory(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setDomain(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setFromDate(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setGeography(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setMsisdn(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setOwnerUserId(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setOwnerUsername(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setToDate(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setTransactionID(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setTransferCategory(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setUserId(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#setUserName(String)}
     *   <li>{@link O2CTxnReversalListRequestVO#toString()}
     *   <li>{@link O2CTxnReversalListRequestVO#getCategory()}
     *   <li>{@link O2CTxnReversalListRequestVO#getDomain()}
     *   <li>{@link O2CTxnReversalListRequestVO#getFromDate()}
     *   <li>{@link O2CTxnReversalListRequestVO#getGeography()}
     *   <li>{@link O2CTxnReversalListRequestVO#getMsisdn()}
     *   <li>{@link O2CTxnReversalListRequestVO#getOwnerUserId()}
     *   <li>{@link O2CTxnReversalListRequestVO#getOwnerUsername()}
     *   <li>{@link O2CTxnReversalListRequestVO#getToDate()}
     *   <li>{@link O2CTxnReversalListRequestVO#getTransactionID()}
     *   <li>{@link O2CTxnReversalListRequestVO#getTransferCategory()}
     *   <li>{@link O2CTxnReversalListRequestVO#getUserId()}
     *   <li>{@link O2CTxnReversalListRequestVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CTxnReversalListRequestVO actualO2cTxnReversalListRequestVO = new O2CTxnReversalListRequestVO();
        actualO2cTxnReversalListRequestVO.setCategory("Category");
        actualO2cTxnReversalListRequestVO.setDomain("Domain");
        actualO2cTxnReversalListRequestVO.setFromDate("2020-03-01");
        actualO2cTxnReversalListRequestVO.setGeography("Geography");
        actualO2cTxnReversalListRequestVO.setMsisdn("Msisdn");
        actualO2cTxnReversalListRequestVO.setOwnerUserId("42");
        actualO2cTxnReversalListRequestVO.setOwnerUsername("janedoe");
        actualO2cTxnReversalListRequestVO.setToDate("2020-03-01");
        actualO2cTxnReversalListRequestVO.setTransactionID("Transaction ID");
        actualO2cTxnReversalListRequestVO.setTransferCategory("Transfer Category");
        actualO2cTxnReversalListRequestVO.setUserId("42");
        actualO2cTxnReversalListRequestVO.setUserName("janedoe");
        String actualToStringResult = actualO2cTxnReversalListRequestVO.toString();
        assertEquals("Category", actualO2cTxnReversalListRequestVO.getCategory());
        assertEquals("Domain", actualO2cTxnReversalListRequestVO.getDomain());
        assertEquals("2020-03-01", actualO2cTxnReversalListRequestVO.getFromDate());
        assertEquals("Geography", actualO2cTxnReversalListRequestVO.getGeography());
        assertEquals("Msisdn", actualO2cTxnReversalListRequestVO.getMsisdn());
        assertEquals("42", actualO2cTxnReversalListRequestVO.getOwnerUserId());
        assertEquals("janedoe", actualO2cTxnReversalListRequestVO.getOwnerUsername());
        assertEquals("2020-03-01", actualO2cTxnReversalListRequestVO.getToDate());
        assertEquals("Transaction ID", actualO2cTxnReversalListRequestVO.getTransactionID());
        assertEquals("Transfer Category", actualO2cTxnReversalListRequestVO.getTransferCategory());
        assertEquals("42", actualO2cTxnReversalListRequestVO.getUserId());
        assertEquals("janedoe", actualO2cTxnReversalListRequestVO.getUserName());
        assertEquals("O2CTxnReversalListRequestVO [transactionID=Transaction ID, msisdn=Msisdn, transferCategory=Transfer"
                + " Category, fromDate=2020-03-01, toDate=2020-03-01, geography=Geography, domain=Domain, category=Category,"
                + " ownerUsername=janedoe, userID=42]", actualToStringResult);
    }
}

