package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DisplayStockLevelOneResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DisplayStockLevelOneResponseVO}
     *   <li>{@link DisplayStockLevelOneResponseVO#setEntryType(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setFirstLevelRemarks(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setLastModifiedTime(long)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setNetworkCodeFor(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setNetworkForName(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setReferenceNumber(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setRemarks(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setRequesterName(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setSecondLevelRemarks(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setStockDateStr(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setStockItemsList(ArrayList)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setStockType(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setTotalMrpStr(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setTxnNo(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setTxnStatusDesc(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setTxnType(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#setWalletType(String)}
     *   <li>{@link DisplayStockLevelOneResponseVO#getEntryType()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getFirstLevelApprovedBy()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getFirstLevelRemarks()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getLastModifiedTime()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getNetworkCodeFor()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getNetworkForName()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getReferenceNumber()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getRemarks()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getRequesterName()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getSecondLevelApprovedBy()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getSecondLevelRemarks()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getStockDateStr()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getStockItemsList()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getStockType()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getTotalMrpStr()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getTxnNo()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getTxnStatusDesc()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getTxnType()}
     *   <li>{@link DisplayStockLevelOneResponseVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DisplayStockLevelOneResponseVO actualDisplayStockLevelOneResponseVO = new DisplayStockLevelOneResponseVO();
        actualDisplayStockLevelOneResponseVO.setEntryType("Entry Type");
        actualDisplayStockLevelOneResponseVO.setFirstLevelApprovedBy("First Level Approved By");
        actualDisplayStockLevelOneResponseVO.setFirstLevelRemarks("First Level Remarks");
        actualDisplayStockLevelOneResponseVO.setLastModifiedTime(1L);
        actualDisplayStockLevelOneResponseVO.setNetworkCodeFor("Network Code For");
        actualDisplayStockLevelOneResponseVO.setNetworkForName("Network For Name");
        actualDisplayStockLevelOneResponseVO.setReferenceNumber("42");
        actualDisplayStockLevelOneResponseVO.setRemarks("Remarks");
        actualDisplayStockLevelOneResponseVO.setRequesterName("Requester Name");
        actualDisplayStockLevelOneResponseVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualDisplayStockLevelOneResponseVO.setSecondLevelRemarks("Second Level Remarks");
        actualDisplayStockLevelOneResponseVO.setStockDateStr("2020-03-01");
        ArrayList stockItemsList = new ArrayList();
        actualDisplayStockLevelOneResponseVO.setStockItemsList(stockItemsList);
        actualDisplayStockLevelOneResponseVO.setStockType("Stock Type");
        actualDisplayStockLevelOneResponseVO.setTotalMrpStr("Total Mrp Str");
        actualDisplayStockLevelOneResponseVO.setTxnNo("Txn No");
        actualDisplayStockLevelOneResponseVO.setTxnStatusDesc("Txn Status Desc");
        actualDisplayStockLevelOneResponseVO.setTxnType("Txn Type");
        actualDisplayStockLevelOneResponseVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualDisplayStockLevelOneResponseVO.getEntryType());
        assertEquals("First Level Approved By", actualDisplayStockLevelOneResponseVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualDisplayStockLevelOneResponseVO.getFirstLevelRemarks());
        assertEquals(1L, actualDisplayStockLevelOneResponseVO.getLastModifiedTime());
        assertEquals("Network Code For", actualDisplayStockLevelOneResponseVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualDisplayStockLevelOneResponseVO.getNetworkForName());
        assertEquals("42", actualDisplayStockLevelOneResponseVO.getReferenceNumber());
        assertEquals("Remarks", actualDisplayStockLevelOneResponseVO.getRemarks());
        assertEquals("Requester Name", actualDisplayStockLevelOneResponseVO.getRequesterName());
        assertEquals("Second Level Approved By", actualDisplayStockLevelOneResponseVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualDisplayStockLevelOneResponseVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualDisplayStockLevelOneResponseVO.getStockDateStr());
        assertSame(stockItemsList, actualDisplayStockLevelOneResponseVO.getStockItemsList());
        assertEquals("Stock Type", actualDisplayStockLevelOneResponseVO.getStockType());
        assertEquals("Total Mrp Str", actualDisplayStockLevelOneResponseVO.getTotalMrpStr());
        assertEquals("Txn No", actualDisplayStockLevelOneResponseVO.getTxnNo());
        assertEquals("Txn Status Desc", actualDisplayStockLevelOneResponseVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualDisplayStockLevelOneResponseVO.getTxnType());
        assertEquals("Wallet Type", actualDisplayStockLevelOneResponseVO.getWalletType());
    }
}

