package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;

import java.util.ArrayList;

import org.junit.Test;

public class RejectStockTxnRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link RejectStockTxnRequestVO}
     *   <li>{@link RejectStockTxnRequestVO#setEntryType(String)}
     *   <li>{@link RejectStockTxnRequestVO#setFirstLevelAppLimit(long)}
     *   <li>{@link RejectStockTxnRequestVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link RejectStockTxnRequestVO#setFirstLevelRemarks(String)}
     *   <li>{@link RejectStockTxnRequestVO#setLastModifiedTime(long)}
     *   <li>{@link RejectStockTxnRequestVO#setNetworkCodeFor(String)}
     *   <li>{@link RejectStockTxnRequestVO#setNetworkForName(String)}
     *   <li>{@link RejectStockTxnRequestVO#setReferenceNumber(String)}
     *   <li>{@link RejectStockTxnRequestVO#setRemarks(String)}
     *   <li>{@link RejectStockTxnRequestVO#setRequesterName(String)}
     *   <li>{@link RejectStockTxnRequestVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link RejectStockTxnRequestVO#setSecondLevelRemarks(String)}
     *   <li>{@link RejectStockTxnRequestVO#setStockDateStr(String)}
     *   <li>{@link RejectStockTxnRequestVO#setStockItemsList(ArrayList)}
     *   <li>{@link RejectStockTxnRequestVO#setStockType(String)}
     *   <li>{@link RejectStockTxnRequestVO#setTotalMrp(long)}
     *   <li>{@link RejectStockTxnRequestVO#setTotalMrpStr(String)}
     *   <li>{@link RejectStockTxnRequestVO#setTotalQty(double)}
     *   <li>{@link RejectStockTxnRequestVO#setTxnNo(String)}
     *   <li>{@link RejectStockTxnRequestVO#setTxnStatusDesc(String)}
     *   <li>{@link RejectStockTxnRequestVO#setTxnType(String)}
     *   <li>{@link RejectStockTxnRequestVO#setWalletType(String)}
     *   <li>{@link RejectStockTxnRequestVO#getEntryType()}
     *   <li>{@link RejectStockTxnRequestVO#getFirstLevelAppLimit()}
     *   <li>{@link RejectStockTxnRequestVO#getFirstLevelApprovedBy()}
     *   <li>{@link RejectStockTxnRequestVO#getFirstLevelRemarks()}
     *   <li>{@link RejectStockTxnRequestVO#getLastModifiedTime()}
     *   <li>{@link RejectStockTxnRequestVO#getNetworkCodeFor()}
     *   <li>{@link RejectStockTxnRequestVO#getNetworkForName()}
     *   <li>{@link RejectStockTxnRequestVO#getReferenceNumber()}
     *   <li>{@link RejectStockTxnRequestVO#getRemarks()}
     *   <li>{@link RejectStockTxnRequestVO#getRequesterName()}
     *   <li>{@link RejectStockTxnRequestVO#getSecondLevelApprovedBy()}
     *   <li>{@link RejectStockTxnRequestVO#getSecondLevelRemarks()}
     *   <li>{@link RejectStockTxnRequestVO#getStockDateStr()}
     *   <li>{@link RejectStockTxnRequestVO#getStockItemsList()}
     *   <li>{@link RejectStockTxnRequestVO#getStockType()}
     *   <li>{@link RejectStockTxnRequestVO#getTotalMrp()}
     *   <li>{@link RejectStockTxnRequestVO#getTotalMrpStr()}
     *   <li>{@link RejectStockTxnRequestVO#getTotalQty()}
     *   <li>{@link RejectStockTxnRequestVO#getTxnNo()}
     *   <li>{@link RejectStockTxnRequestVO#getTxnStatusDesc()}
     *   <li>{@link RejectStockTxnRequestVO#getTxnType()}
     *   <li>{@link RejectStockTxnRequestVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        RejectStockTxnRequestVO actualRejectStockTxnRequestVO = new RejectStockTxnRequestVO();
        actualRejectStockTxnRequestVO.setEntryType("Entry Type");
        actualRejectStockTxnRequestVO.setFirstLevelAppLimit(1L);
        actualRejectStockTxnRequestVO.setFirstLevelApprovedBy("First Level Approved By");
        actualRejectStockTxnRequestVO.setFirstLevelRemarks("First Level Remarks");
        actualRejectStockTxnRequestVO.setLastModifiedTime(1L);
        actualRejectStockTxnRequestVO.setNetworkCodeFor("Network Code For");
        actualRejectStockTxnRequestVO.setNetworkForName("Network For Name");
        actualRejectStockTxnRequestVO.setReferenceNumber("42");
        actualRejectStockTxnRequestVO.setRemarks("Remarks");
        actualRejectStockTxnRequestVO.setRequesterName("Requester Name");
        actualRejectStockTxnRequestVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualRejectStockTxnRequestVO.setSecondLevelRemarks("Second Level Remarks");
        actualRejectStockTxnRequestVO.setStockDateStr("2020-03-01");
        ArrayList<NetworkStockTxnItemsVO> stockItemsList = new ArrayList<>();
        actualRejectStockTxnRequestVO.setStockItemsList(stockItemsList);
        actualRejectStockTxnRequestVO.setStockType("Stock Type");
        actualRejectStockTxnRequestVO.setTotalMrp(1L);
        actualRejectStockTxnRequestVO.setTotalMrpStr("Total Mrp Str");
        actualRejectStockTxnRequestVO.setTotalQty(10.0d);
        actualRejectStockTxnRequestVO.setTxnNo("Txn No");
        actualRejectStockTxnRequestVO.setTxnStatusDesc("Txn Status Desc");
        actualRejectStockTxnRequestVO.setTxnType("Txn Type");
        actualRejectStockTxnRequestVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualRejectStockTxnRequestVO.getEntryType());
        assertEquals(1L, actualRejectStockTxnRequestVO.getFirstLevelAppLimit());
        assertEquals("First Level Approved By", actualRejectStockTxnRequestVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualRejectStockTxnRequestVO.getFirstLevelRemarks());
        assertEquals(1L, actualRejectStockTxnRequestVO.getLastModifiedTime());
        assertEquals("Network Code For", actualRejectStockTxnRequestVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualRejectStockTxnRequestVO.getNetworkForName());
        assertEquals("42", actualRejectStockTxnRequestVO.getReferenceNumber());
        assertEquals("Remarks", actualRejectStockTxnRequestVO.getRemarks());
        assertEquals("Requester Name", actualRejectStockTxnRequestVO.getRequesterName());
        assertEquals("Second Level Approved By", actualRejectStockTxnRequestVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualRejectStockTxnRequestVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualRejectStockTxnRequestVO.getStockDateStr());
        assertSame(stockItemsList, actualRejectStockTxnRequestVO.getStockItemsList());
        assertEquals("Stock Type", actualRejectStockTxnRequestVO.getStockType());
        assertEquals(1L, actualRejectStockTxnRequestVO.getTotalMrp());
        assertEquals("Total Mrp Str", actualRejectStockTxnRequestVO.getTotalMrpStr());
        assertEquals(10.0d, actualRejectStockTxnRequestVO.getTotalQty(), 0.0);
        assertEquals("Txn No", actualRejectStockTxnRequestVO.getTxnNo());
        assertEquals("Txn Status Desc", actualRejectStockTxnRequestVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualRejectStockTxnRequestVO.getTxnType());
        assertEquals("Wallet Type", actualRejectStockTxnRequestVO.getWalletType());
    }
}

