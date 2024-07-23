package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DisplayStockTxnDetailsViewResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DisplayStockTxnDetailsViewResponseVO}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setEntryType(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setFirstLevelRemarks(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setLastModifiedTime(long)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setNetworkCodeFor(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setNetworkForName(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setReferenceNumber(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setRemarks(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setRequesterName(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setSecondLevelRemarks(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setStockDateStr(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setStockItemsList(ArrayList)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setStockType(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setTotalMrpStr(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setTxnNo(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setTxnStatusDesc(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setTxnType(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#setWalletType(String)}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getEntryType()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getFirstLevelApprovedBy()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getFirstLevelRemarks()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getLastModifiedTime()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getNetworkCodeFor()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getNetworkForName()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getReferenceNumber()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getRemarks()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getRequesterName()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getSecondLevelApprovedBy()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getSecondLevelRemarks()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getStockDateStr()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getStockItemsList()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getStockType()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getTotalMrpStr()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getTxnNo()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getTxnStatusDesc()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getTxnType()}
     *   <li>{@link DisplayStockTxnDetailsViewResponseVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DisplayStockTxnDetailsViewResponseVO actualDisplayStockTxnDetailsViewResponseVO = new DisplayStockTxnDetailsViewResponseVO();
        actualDisplayStockTxnDetailsViewResponseVO.setEntryType("Entry Type");
        actualDisplayStockTxnDetailsViewResponseVO.setFirstLevelApprovedBy("First Level Approved By");
        actualDisplayStockTxnDetailsViewResponseVO.setFirstLevelRemarks("First Level Remarks");
        actualDisplayStockTxnDetailsViewResponseVO.setLastModifiedTime(1L);
        actualDisplayStockTxnDetailsViewResponseVO.setNetworkCodeFor("Network Code For");
        actualDisplayStockTxnDetailsViewResponseVO.setNetworkForName("Network For Name");
        actualDisplayStockTxnDetailsViewResponseVO.setReferenceNumber("42");
        actualDisplayStockTxnDetailsViewResponseVO.setRemarks("Remarks");
        actualDisplayStockTxnDetailsViewResponseVO.setRequesterName("Requester Name");
        actualDisplayStockTxnDetailsViewResponseVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualDisplayStockTxnDetailsViewResponseVO.setSecondLevelRemarks("Second Level Remarks");
        actualDisplayStockTxnDetailsViewResponseVO.setStockDateStr("2020-03-01");
        ArrayList stockItemsList = new ArrayList();
        actualDisplayStockTxnDetailsViewResponseVO.setStockItemsList(stockItemsList);
        actualDisplayStockTxnDetailsViewResponseVO.setStockType("Stock Type");
        actualDisplayStockTxnDetailsViewResponseVO.setTotalMrpStr("Total Mrp Str");
        actualDisplayStockTxnDetailsViewResponseVO.setTxnNo("Txn No");
        actualDisplayStockTxnDetailsViewResponseVO.setTxnStatusDesc("Txn Status Desc");
        actualDisplayStockTxnDetailsViewResponseVO.setTxnType("Txn Type");
        actualDisplayStockTxnDetailsViewResponseVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualDisplayStockTxnDetailsViewResponseVO.getEntryType());
        assertEquals("First Level Approved By", actualDisplayStockTxnDetailsViewResponseVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualDisplayStockTxnDetailsViewResponseVO.getFirstLevelRemarks());
        assertEquals(1L, actualDisplayStockTxnDetailsViewResponseVO.getLastModifiedTime());
        assertEquals("Network Code For", actualDisplayStockTxnDetailsViewResponseVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualDisplayStockTxnDetailsViewResponseVO.getNetworkForName());
        assertEquals("42", actualDisplayStockTxnDetailsViewResponseVO.getReferenceNumber());
        assertEquals("Remarks", actualDisplayStockTxnDetailsViewResponseVO.getRemarks());
        assertEquals("Requester Name", actualDisplayStockTxnDetailsViewResponseVO.getRequesterName());
        assertEquals("Second Level Approved By", actualDisplayStockTxnDetailsViewResponseVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualDisplayStockTxnDetailsViewResponseVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualDisplayStockTxnDetailsViewResponseVO.getStockDateStr());
        assertSame(stockItemsList, actualDisplayStockTxnDetailsViewResponseVO.getStockItemsList());
        assertEquals("Stock Type", actualDisplayStockTxnDetailsViewResponseVO.getStockType());
        assertEquals("Total Mrp Str", actualDisplayStockTxnDetailsViewResponseVO.getTotalMrpStr());
        assertEquals("Txn No", actualDisplayStockTxnDetailsViewResponseVO.getTxnNo());
        assertEquals("Txn Status Desc", actualDisplayStockTxnDetailsViewResponseVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualDisplayStockTxnDetailsViewResponseVO.getTxnType());
        assertEquals("Wallet Type", actualDisplayStockTxnDetailsViewResponseVO.getWalletType());
    }
}

