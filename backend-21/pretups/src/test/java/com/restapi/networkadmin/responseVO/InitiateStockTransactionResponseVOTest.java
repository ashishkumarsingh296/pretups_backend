package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class InitiateStockTransactionResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link InitiateStockTransactionResponseVO}
     *   <li>{@link InitiateStockTransactionResponseVO#setEntryType(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setNetworkCode(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setNetworkCodeFor(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setNetworkForName(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setNetworkName(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setRequesterName(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setStockDateStr(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setStockProductList(ArrayList)}
     *   <li>{@link InitiateStockTransactionResponseVO#setStockType(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setTxnStatus(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setTxnType(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#setUserID(String)}
     *   <li>{@link InitiateStockTransactionResponseVO#getEntryType()}
     *   <li>{@link InitiateStockTransactionResponseVO#getNetworkCode()}
     *   <li>{@link InitiateStockTransactionResponseVO#getNetworkCodeFor()}
     *   <li>{@link InitiateStockTransactionResponseVO#getNetworkForName()}
     *   <li>{@link InitiateStockTransactionResponseVO#getNetworkName()}
     *   <li>{@link InitiateStockTransactionResponseVO#getRequesterName()}
     *   <li>{@link InitiateStockTransactionResponseVO#getStockDateStr()}
     *   <li>{@link InitiateStockTransactionResponseVO#getStockProductList()}
     *   <li>{@link InitiateStockTransactionResponseVO#getStockType()}
     *   <li>{@link InitiateStockTransactionResponseVO#getTxnStatus()}
     *   <li>{@link InitiateStockTransactionResponseVO#getTxnType()}
     *   <li>{@link InitiateStockTransactionResponseVO#getUserID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        InitiateStockTransactionResponseVO actualInitiateStockTransactionResponseVO = new InitiateStockTransactionResponseVO();
        actualInitiateStockTransactionResponseVO.setEntryType("Entry Type");
        actualInitiateStockTransactionResponseVO.setNetworkCode("Network Code");
        actualInitiateStockTransactionResponseVO.setNetworkCodeFor("Network Code For");
        actualInitiateStockTransactionResponseVO.setNetworkForName("Network For Name");
        actualInitiateStockTransactionResponseVO.setNetworkName("Network Name");
        actualInitiateStockTransactionResponseVO.setRequesterName("Requester Name");
        actualInitiateStockTransactionResponseVO.setStockDateStr("2020-03-01");
        ArrayList stockProductList = new ArrayList();
        actualInitiateStockTransactionResponseVO.setStockProductList(stockProductList);
        actualInitiateStockTransactionResponseVO.setStockType("Stock Type");
        actualInitiateStockTransactionResponseVO.setTxnStatus("Txn Status");
        actualInitiateStockTransactionResponseVO.setTxnType("Txn Type");
        actualInitiateStockTransactionResponseVO.setUserID("User ID");
        assertEquals("Entry Type", actualInitiateStockTransactionResponseVO.getEntryType());
        assertEquals("Network Code", actualInitiateStockTransactionResponseVO.getNetworkCode());
        assertEquals("Network Code For", actualInitiateStockTransactionResponseVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualInitiateStockTransactionResponseVO.getNetworkForName());
        assertEquals("Network Name", actualInitiateStockTransactionResponseVO.getNetworkName());
        assertEquals("Requester Name", actualInitiateStockTransactionResponseVO.getRequesterName());
        assertEquals("2020-03-01", actualInitiateStockTransactionResponseVO.getStockDateStr());
        assertSame(stockProductList, actualInitiateStockTransactionResponseVO.getStockProductList());
        assertEquals("Stock Type", actualInitiateStockTransactionResponseVO.getStockType());
        assertEquals("Txn Status", actualInitiateStockTransactionResponseVO.getTxnStatus());
        assertEquals("Txn Type", actualInitiateStockTransactionResponseVO.getTxnType());
        assertEquals("User ID", actualInitiateStockTransactionResponseVO.getUserID());
    }
}

