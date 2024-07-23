package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;

import java.util.ArrayList;

import org.junit.Test;

public class ApprovaLevelTwoStockTxnRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ApprovaLevelTwoStockTxnRequestVO}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setEntryType(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setFirstLevelAppLimit(long)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setFirstLevelRemarks(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setLastModifiedTime(long)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setNetworkCodeFor(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setNetworkForName(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setReferenceNumber(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setRemarks(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setRequesterName(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setSecondLevelRemarks(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setStockDateStr(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setStockItemsList(ArrayList)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setStockType(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setTotalMrp(long)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setTotalMrpStr(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setTotalQty(double)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setTxnNo(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setTxnStatusDesc(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setTxnType(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#setWalletType(String)}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getEntryType()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getFirstLevelAppLimit()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getFirstLevelApprovedBy()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getFirstLevelRemarks()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getLastModifiedTime()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getNetworkCodeFor()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getNetworkForName()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getReferenceNumber()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getRemarks()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getRequesterName()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getSecondLevelApprovedBy()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getSecondLevelRemarks()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getStockDateStr()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getStockItemsList()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getStockType()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getTotalMrp()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getTotalMrpStr()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getTotalQty()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getTxnNo()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getTxnStatusDesc()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getTxnType()}
     *   <li>{@link ApprovaLevelTwoStockTxnRequestVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ApprovaLevelTwoStockTxnRequestVO actualApprovaLevelTwoStockTxnRequestVO = new ApprovaLevelTwoStockTxnRequestVO();
        actualApprovaLevelTwoStockTxnRequestVO.setEntryType("Entry Type");
        actualApprovaLevelTwoStockTxnRequestVO.setFirstLevelAppLimit(1L);
        actualApprovaLevelTwoStockTxnRequestVO.setFirstLevelApprovedBy("First Level Approved By");
        actualApprovaLevelTwoStockTxnRequestVO.setFirstLevelRemarks("First Level Remarks");
        actualApprovaLevelTwoStockTxnRequestVO.setLastModifiedTime(1L);
        actualApprovaLevelTwoStockTxnRequestVO.setNetworkCodeFor("Network Code For");
        actualApprovaLevelTwoStockTxnRequestVO.setNetworkForName("Network For Name");
        actualApprovaLevelTwoStockTxnRequestVO.setReferenceNumber("42");
        actualApprovaLevelTwoStockTxnRequestVO.setRemarks("Remarks");
        actualApprovaLevelTwoStockTxnRequestVO.setRequesterName("Requester Name");
        actualApprovaLevelTwoStockTxnRequestVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualApprovaLevelTwoStockTxnRequestVO.setSecondLevelRemarks("Second Level Remarks");
        actualApprovaLevelTwoStockTxnRequestVO.setStockDateStr("2020-03-01");
        ArrayList<NetworkStockTxnItemsVO> stockItemsList = new ArrayList<>();
        actualApprovaLevelTwoStockTxnRequestVO.setStockItemsList(stockItemsList);
        actualApprovaLevelTwoStockTxnRequestVO.setStockType("Stock Type");
        actualApprovaLevelTwoStockTxnRequestVO.setTotalMrp(1L);
        actualApprovaLevelTwoStockTxnRequestVO.setTotalMrpStr("Total Mrp Str");
        actualApprovaLevelTwoStockTxnRequestVO.setTotalQty(10.0d);
        actualApprovaLevelTwoStockTxnRequestVO.setTxnNo("Txn No");
        actualApprovaLevelTwoStockTxnRequestVO.setTxnStatusDesc("Txn Status Desc");
        actualApprovaLevelTwoStockTxnRequestVO.setTxnType("Txn Type");
        actualApprovaLevelTwoStockTxnRequestVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualApprovaLevelTwoStockTxnRequestVO.getEntryType());
        assertEquals(1L, actualApprovaLevelTwoStockTxnRequestVO.getFirstLevelAppLimit());
        assertEquals("First Level Approved By", actualApprovaLevelTwoStockTxnRequestVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualApprovaLevelTwoStockTxnRequestVO.getFirstLevelRemarks());
        assertEquals(1L, actualApprovaLevelTwoStockTxnRequestVO.getLastModifiedTime());
        assertEquals("Network Code For", actualApprovaLevelTwoStockTxnRequestVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualApprovaLevelTwoStockTxnRequestVO.getNetworkForName());
        assertEquals("42", actualApprovaLevelTwoStockTxnRequestVO.getReferenceNumber());
        assertEquals("Remarks", actualApprovaLevelTwoStockTxnRequestVO.getRemarks());
        assertEquals("Requester Name", actualApprovaLevelTwoStockTxnRequestVO.getRequesterName());
        assertEquals("Second Level Approved By", actualApprovaLevelTwoStockTxnRequestVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualApprovaLevelTwoStockTxnRequestVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualApprovaLevelTwoStockTxnRequestVO.getStockDateStr());
        assertSame(stockItemsList, actualApprovaLevelTwoStockTxnRequestVO.getStockItemsList());
        assertEquals("Stock Type", actualApprovaLevelTwoStockTxnRequestVO.getStockType());
        assertEquals(1L, actualApprovaLevelTwoStockTxnRequestVO.getTotalMrp());
        assertEquals("Total Mrp Str", actualApprovaLevelTwoStockTxnRequestVO.getTotalMrpStr());
        assertEquals(10.0d, actualApprovaLevelTwoStockTxnRequestVO.getTotalQty(), 0.0);
        assertEquals("Txn No", actualApprovaLevelTwoStockTxnRequestVO.getTxnNo());
        assertEquals("Txn Status Desc", actualApprovaLevelTwoStockTxnRequestVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualApprovaLevelTwoStockTxnRequestVO.getTxnType());
        assertEquals("Wallet Type", actualApprovaLevelTwoStockTxnRequestVO.getWalletType());
    }
}

