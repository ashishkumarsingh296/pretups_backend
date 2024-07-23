package com.restapi.networkadminVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DisplayStockVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DisplayStockVO}
     *   <li>{@link DisplayStockVO#setEntryType(String)}
     *   <li>{@link DisplayStockVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link DisplayStockVO#setFirstLevelRemarks(String)}
     *   <li>{@link DisplayStockVO#setLastModifiedTime(long)}
     *   <li>{@link DisplayStockVO#setNetworkCodeFor(String)}
     *   <li>{@link DisplayStockVO#setNetworkForName(String)}
     *   <li>{@link DisplayStockVO#setReferenceNumber(String)}
     *   <li>{@link DisplayStockVO#setRemarks(String)}
     *   <li>{@link DisplayStockVO#setRequesterName(String)}
     *   <li>{@link DisplayStockVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link DisplayStockVO#setSecondLevelRemarks(String)}
     *   <li>{@link DisplayStockVO#setStockDateStr(String)}
     *   <li>{@link DisplayStockVO#setStockType(String)}
     *   <li>{@link DisplayStockVO#setTxnNo(String)}
     *   <li>{@link DisplayStockVO#setTxnStatusDesc(String)}
     *   <li>{@link DisplayStockVO#setTxnType(String)}
     *   <li>{@link DisplayStockVO#setWalletType(String)}
     *   <li>{@link DisplayStockVO#getEntryType()}
     *   <li>{@link DisplayStockVO#getFirstLevelApprovedBy()}
     *   <li>{@link DisplayStockVO#getFirstLevelRemarks()}
     *   <li>{@link DisplayStockVO#getLastModifiedTime()}
     *   <li>{@link DisplayStockVO#getNetworkCodeFor()}
     *   <li>{@link DisplayStockVO#getNetworkForName()}
     *   <li>{@link DisplayStockVO#getReferenceNumber()}
     *   <li>{@link DisplayStockVO#getRemarks()}
     *   <li>{@link DisplayStockVO#getRequesterName()}
     *   <li>{@link DisplayStockVO#getSecondLevelApprovedBy()}
     *   <li>{@link DisplayStockVO#getSecondLevelRemarks()}
     *   <li>{@link DisplayStockVO#getStockDateStr()}
     *   <li>{@link DisplayStockVO#getStockType()}
     *   <li>{@link DisplayStockVO#getTxnNo()}
     *   <li>{@link DisplayStockVO#getTxnStatusDesc()}
     *   <li>{@link DisplayStockVO#getTxnType()}
     *   <li>{@link DisplayStockVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DisplayStockVO actualDisplayStockVO = new DisplayStockVO();
        actualDisplayStockVO.setEntryType("Entry Type");
        actualDisplayStockVO.setFirstLevelApprovedBy("First Level Approved By");
        actualDisplayStockVO.setFirstLevelRemarks("First Level Remarks");
        actualDisplayStockVO.setLastModifiedTime(1L);
        actualDisplayStockVO.setNetworkCodeFor("Network Code For");
        actualDisplayStockVO.setNetworkForName("Network For Name");
        actualDisplayStockVO.setReferenceNumber("42");
        actualDisplayStockVO.setRemarks("Remarks");
        actualDisplayStockVO.setRequesterName("Requester Name");
        actualDisplayStockVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualDisplayStockVO.setSecondLevelRemarks("Second Level Remarks");
        actualDisplayStockVO.setStockDateStr("2020-03-01");
        actualDisplayStockVO.setStockType("Stock Type");
        actualDisplayStockVO.setTxnNo("Txn No");
        actualDisplayStockVO.setTxnStatusDesc("Txn Status Desc");
        actualDisplayStockVO.setTxnType("Txn Type");
        actualDisplayStockVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualDisplayStockVO.getEntryType());
        assertEquals("First Level Approved By", actualDisplayStockVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualDisplayStockVO.getFirstLevelRemarks());
        assertEquals(1L, actualDisplayStockVO.getLastModifiedTime());
        assertEquals("Network Code For", actualDisplayStockVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualDisplayStockVO.getNetworkForName());
        assertEquals("42", actualDisplayStockVO.getReferenceNumber());
        assertEquals("Remarks", actualDisplayStockVO.getRemarks());
        assertEquals("Requester Name", actualDisplayStockVO.getRequesterName());
        assertEquals("Second Level Approved By", actualDisplayStockVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualDisplayStockVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualDisplayStockVO.getStockDateStr());
        assertEquals("Stock Type", actualDisplayStockVO.getStockType());
        assertEquals("Txn No", actualDisplayStockVO.getTxnNo());
        assertEquals("Txn Status Desc", actualDisplayStockVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualDisplayStockVO.getTxnType());
        assertEquals("Wallet Type", actualDisplayStockVO.getWalletType());
    }
}

