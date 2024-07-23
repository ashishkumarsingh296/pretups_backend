package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;

import java.util.ArrayList;

import org.junit.Test;

public class ApprovaLevelOneStockTxnRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ApprovaLevelOneStockTxnRequestVO}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setEntryType(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setFirstLevelAppLimit(long)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setFirstLevelRemarks(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setLastModifiedTime(long)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setNetworkCodeFor(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setNetworkForName(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setReferenceNumber(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setRemarks(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setRequesterName(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setSecondLevelRemarks(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setStockDateStr(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setStockItemsList(ArrayList)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setStockType(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setTotalMrp(long)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setTotalMrpStr(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setTotalQty(double)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setTxnNo(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setTxnStatusDesc(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setTxnType(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#setWalletType(String)}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getEntryType()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getFirstLevelAppLimit()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getFirstLevelApprovedBy()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getFirstLevelRemarks()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getLastModifiedTime()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getNetworkCodeFor()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getNetworkForName()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getReferenceNumber()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getRemarks()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getRequesterName()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getSecondLevelApprovedBy()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getSecondLevelRemarks()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getStockDateStr()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getStockItemsList()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getStockType()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getTotalMrp()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getTotalMrpStr()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getTotalQty()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getTxnNo()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getTxnStatusDesc()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getTxnType()}
     *   <li>{@link ApprovaLevelOneStockTxnRequestVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ApprovaLevelOneStockTxnRequestVO actualApprovaLevelOneStockTxnRequestVO = new ApprovaLevelOneStockTxnRequestVO();
        actualApprovaLevelOneStockTxnRequestVO.setEntryType("Entry Type");
        actualApprovaLevelOneStockTxnRequestVO.setFirstLevelAppLimit(1L);
        actualApprovaLevelOneStockTxnRequestVO.setFirstLevelApprovedBy("First Level Approved By");
        actualApprovaLevelOneStockTxnRequestVO.setFirstLevelRemarks("First Level Remarks");
        actualApprovaLevelOneStockTxnRequestVO.setLastModifiedTime(1L);
        actualApprovaLevelOneStockTxnRequestVO.setNetworkCodeFor("Network Code For");
        actualApprovaLevelOneStockTxnRequestVO.setNetworkForName("Network For Name");
        actualApprovaLevelOneStockTxnRequestVO.setReferenceNumber("42");
        actualApprovaLevelOneStockTxnRequestVO.setRemarks("Remarks");
        actualApprovaLevelOneStockTxnRequestVO.setRequesterName("Requester Name");
        actualApprovaLevelOneStockTxnRequestVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualApprovaLevelOneStockTxnRequestVO.setSecondLevelRemarks("Second Level Remarks");
        actualApprovaLevelOneStockTxnRequestVO.setStockDateStr("2020-03-01");
        ArrayList<NetworkStockTxnItemsVO> stockItemsList = new ArrayList<>();
        actualApprovaLevelOneStockTxnRequestVO.setStockItemsList(stockItemsList);
        actualApprovaLevelOneStockTxnRequestVO.setStockType("Stock Type");
        actualApprovaLevelOneStockTxnRequestVO.setTotalMrp(1L);
        actualApprovaLevelOneStockTxnRequestVO.setTotalMrpStr("Total Mrp Str");
        actualApprovaLevelOneStockTxnRequestVO.setTotalQty(10.0d);
        actualApprovaLevelOneStockTxnRequestVO.setTxnNo("Txn No");
        actualApprovaLevelOneStockTxnRequestVO.setTxnStatusDesc("Txn Status Desc");
        actualApprovaLevelOneStockTxnRequestVO.setTxnType("Txn Type");
        actualApprovaLevelOneStockTxnRequestVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualApprovaLevelOneStockTxnRequestVO.getEntryType());
        assertEquals(1L, actualApprovaLevelOneStockTxnRequestVO.getFirstLevelAppLimit());
        assertEquals("First Level Approved By", actualApprovaLevelOneStockTxnRequestVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualApprovaLevelOneStockTxnRequestVO.getFirstLevelRemarks());
        assertEquals(1L, actualApprovaLevelOneStockTxnRequestVO.getLastModifiedTime());
        assertEquals("Network Code For", actualApprovaLevelOneStockTxnRequestVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualApprovaLevelOneStockTxnRequestVO.getNetworkForName());
        assertEquals("42", actualApprovaLevelOneStockTxnRequestVO.getReferenceNumber());
        assertEquals("Remarks", actualApprovaLevelOneStockTxnRequestVO.getRemarks());
        assertEquals("Requester Name", actualApprovaLevelOneStockTxnRequestVO.getRequesterName());
        assertEquals("Second Level Approved By", actualApprovaLevelOneStockTxnRequestVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualApprovaLevelOneStockTxnRequestVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualApprovaLevelOneStockTxnRequestVO.getStockDateStr());
        assertSame(stockItemsList, actualApprovaLevelOneStockTxnRequestVO.getStockItemsList());
        assertEquals("Stock Type", actualApprovaLevelOneStockTxnRequestVO.getStockType());
        assertEquals(1L, actualApprovaLevelOneStockTxnRequestVO.getTotalMrp());
        assertEquals("Total Mrp Str", actualApprovaLevelOneStockTxnRequestVO.getTotalMrpStr());
        assertEquals(10.0d, actualApprovaLevelOneStockTxnRequestVO.getTotalQty(), 0.0);
        assertEquals("Txn No", actualApprovaLevelOneStockTxnRequestVO.getTxnNo());
        assertEquals("Txn Status Desc", actualApprovaLevelOneStockTxnRequestVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualApprovaLevelOneStockTxnRequestVO.getTxnType());
        assertEquals("Wallet Type", actualApprovaLevelOneStockTxnRequestVO.getWalletType());
    }
}

