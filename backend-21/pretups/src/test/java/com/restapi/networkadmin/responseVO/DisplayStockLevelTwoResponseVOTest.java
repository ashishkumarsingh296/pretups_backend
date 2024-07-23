package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DisplayStockLevelTwoResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DisplayStockLevelTwoResponseVO}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setEntryType(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setFirstLevelRemarks(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setLastModifiedTime(long)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setNetworkCodeFor(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setNetworkForName(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setReferenceNumber(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setRemarks(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setRequesterName(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setSecondLevelRemarks(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setStockDateStr(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setStockItemsList(ArrayList)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setStockType(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setTotalMrpStr(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setTxnNo(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setTxnStatusDesc(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setTxnType(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#setWalletType(String)}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getEntryType()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getFirstLevelApprovedBy()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getFirstLevelRemarks()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getLastModifiedTime()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getNetworkCodeFor()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getNetworkForName()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getReferenceNumber()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getRemarks()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getRequesterName()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getSecondLevelApprovedBy()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getSecondLevelRemarks()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getStockDateStr()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getStockItemsList()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getStockType()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getTotalMrpStr()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getTxnNo()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getTxnStatusDesc()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getTxnType()}
     *   <li>{@link DisplayStockLevelTwoResponseVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DisplayStockLevelTwoResponseVO actualDisplayStockLevelTwoResponseVO = new DisplayStockLevelTwoResponseVO();
        actualDisplayStockLevelTwoResponseVO.setEntryType("Entry Type");
        actualDisplayStockLevelTwoResponseVO.setFirstLevelApprovedBy("First Level Approved By");
        actualDisplayStockLevelTwoResponseVO.setFirstLevelRemarks("First Level Remarks");
        actualDisplayStockLevelTwoResponseVO.setLastModifiedTime(1L);
        actualDisplayStockLevelTwoResponseVO.setNetworkCodeFor("Network Code For");
        actualDisplayStockLevelTwoResponseVO.setNetworkForName("Network For Name");
        actualDisplayStockLevelTwoResponseVO.setReferenceNumber("42");
        actualDisplayStockLevelTwoResponseVO.setRemarks("Remarks");
        actualDisplayStockLevelTwoResponseVO.setRequesterName("Requester Name");
        actualDisplayStockLevelTwoResponseVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualDisplayStockLevelTwoResponseVO.setSecondLevelRemarks("Second Level Remarks");
        actualDisplayStockLevelTwoResponseVO.setStockDateStr("2020-03-01");
        ArrayList stockItemsList = new ArrayList();
        actualDisplayStockLevelTwoResponseVO.setStockItemsList(stockItemsList);
        actualDisplayStockLevelTwoResponseVO.setStockType("Stock Type");
        actualDisplayStockLevelTwoResponseVO.setTotalMrpStr("Total Mrp Str");
        actualDisplayStockLevelTwoResponseVO.setTxnNo("Txn No");
        actualDisplayStockLevelTwoResponseVO.setTxnStatusDesc("Txn Status Desc");
        actualDisplayStockLevelTwoResponseVO.setTxnType("Txn Type");
        actualDisplayStockLevelTwoResponseVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualDisplayStockLevelTwoResponseVO.getEntryType());
        assertEquals("First Level Approved By", actualDisplayStockLevelTwoResponseVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualDisplayStockLevelTwoResponseVO.getFirstLevelRemarks());
        assertEquals(1L, actualDisplayStockLevelTwoResponseVO.getLastModifiedTime());
        assertEquals("Network Code For", actualDisplayStockLevelTwoResponseVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualDisplayStockLevelTwoResponseVO.getNetworkForName());
        assertEquals("42", actualDisplayStockLevelTwoResponseVO.getReferenceNumber());
        assertEquals("Remarks", actualDisplayStockLevelTwoResponseVO.getRemarks());
        assertEquals("Requester Name", actualDisplayStockLevelTwoResponseVO.getRequesterName());
        assertEquals("Second Level Approved By", actualDisplayStockLevelTwoResponseVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualDisplayStockLevelTwoResponseVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualDisplayStockLevelTwoResponseVO.getStockDateStr());
        assertSame(stockItemsList, actualDisplayStockLevelTwoResponseVO.getStockItemsList());
        assertEquals("Stock Type", actualDisplayStockLevelTwoResponseVO.getStockType());
        assertEquals("Total Mrp Str", actualDisplayStockLevelTwoResponseVO.getTotalMrpStr());
        assertEquals("Txn No", actualDisplayStockLevelTwoResponseVO.getTxnNo());
        assertEquals("Txn Status Desc", actualDisplayStockLevelTwoResponseVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualDisplayStockLevelTwoResponseVO.getTxnType());
        assertEquals("Wallet Type", actualDisplayStockLevelTwoResponseVO.getWalletType());
    }
}

