package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ConfirmStockLevelOneResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ConfirmStockLevelOneResponseVO}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setEntryType(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setFirstLevelAppLimit(long)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setFirstLevelRemarks(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setLastModifiedTime(long)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setNetworkCodeFor(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setNetworkForName(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setReferenceNumber(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setRemarks(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setRequesterName(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setSecondLevelRemarks(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setStockDateStr(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setStockItemsList(ArrayList)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setStockType(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setTotalMrp(long)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setTotalMrpStr(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setTotalQty(double)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setTxnNo(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setTxnStatusDesc(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setTxnType(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#setWalletType(String)}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getEntryType()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getFirstLevelAppLimit()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getFirstLevelApprovedBy()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getFirstLevelRemarks()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getLastModifiedTime()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getNetworkCodeFor()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getNetworkForName()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getReferenceNumber()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getRemarks()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getRequesterName()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getSecondLevelApprovedBy()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getSecondLevelRemarks()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getStockDateStr()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getStockItemsList()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getStockType()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getTotalMrp()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getTotalMrpStr()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getTotalQty()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getTxnNo()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getTxnStatusDesc()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getTxnType()}
     *   <li>{@link ConfirmStockLevelOneResponseVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ConfirmStockLevelOneResponseVO actualConfirmStockLevelOneResponseVO = new ConfirmStockLevelOneResponseVO();
        actualConfirmStockLevelOneResponseVO.setEntryType("Entry Type");
        actualConfirmStockLevelOneResponseVO.setFirstLevelAppLimit(1L);
        actualConfirmStockLevelOneResponseVO.setFirstLevelApprovedBy("First Level Approved By");
        actualConfirmStockLevelOneResponseVO.setFirstLevelRemarks("First Level Remarks");
        actualConfirmStockLevelOneResponseVO.setLastModifiedTime(1L);
        actualConfirmStockLevelOneResponseVO.setNetworkCodeFor("Network Code For");
        actualConfirmStockLevelOneResponseVO.setNetworkForName("Network For Name");
        actualConfirmStockLevelOneResponseVO.setReferenceNumber("42");
        actualConfirmStockLevelOneResponseVO.setRemarks("Remarks");
        actualConfirmStockLevelOneResponseVO.setRequesterName("Requester Name");
        actualConfirmStockLevelOneResponseVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualConfirmStockLevelOneResponseVO.setSecondLevelRemarks("Second Level Remarks");
        actualConfirmStockLevelOneResponseVO.setStockDateStr("2020-03-01");
        ArrayList stockItemsList = new ArrayList();
        actualConfirmStockLevelOneResponseVO.setStockItemsList(stockItemsList);
        actualConfirmStockLevelOneResponseVO.setStockType("Stock Type");
        actualConfirmStockLevelOneResponseVO.setTotalMrp(1L);
        actualConfirmStockLevelOneResponseVO.setTotalMrpStr("Total Mrp Str");
        actualConfirmStockLevelOneResponseVO.setTotalQty(10.0d);
        actualConfirmStockLevelOneResponseVO.setTxnNo("Txn No");
        actualConfirmStockLevelOneResponseVO.setTxnStatusDesc("Txn Status Desc");
        actualConfirmStockLevelOneResponseVO.setTxnType("Txn Type");
        actualConfirmStockLevelOneResponseVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualConfirmStockLevelOneResponseVO.getEntryType());
        assertEquals(1L, actualConfirmStockLevelOneResponseVO.getFirstLevelAppLimit());
        assertEquals("First Level Approved By", actualConfirmStockLevelOneResponseVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualConfirmStockLevelOneResponseVO.getFirstLevelRemarks());
        assertEquals(1L, actualConfirmStockLevelOneResponseVO.getLastModifiedTime());
        assertEquals("Network Code For", actualConfirmStockLevelOneResponseVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualConfirmStockLevelOneResponseVO.getNetworkForName());
        assertEquals("42", actualConfirmStockLevelOneResponseVO.getReferenceNumber());
        assertEquals("Remarks", actualConfirmStockLevelOneResponseVO.getRemarks());
        assertEquals("Requester Name", actualConfirmStockLevelOneResponseVO.getRequesterName());
        assertEquals("Second Level Approved By", actualConfirmStockLevelOneResponseVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualConfirmStockLevelOneResponseVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualConfirmStockLevelOneResponseVO.getStockDateStr());
        assertSame(stockItemsList, actualConfirmStockLevelOneResponseVO.getStockItemsList());
        assertEquals("Stock Type", actualConfirmStockLevelOneResponseVO.getStockType());
        assertEquals(1L, actualConfirmStockLevelOneResponseVO.getTotalMrp());
        assertEquals("Total Mrp Str", actualConfirmStockLevelOneResponseVO.getTotalMrpStr());
        assertEquals(10.0d, actualConfirmStockLevelOneResponseVO.getTotalQty(), 0.0);
        assertEquals("Txn No", actualConfirmStockLevelOneResponseVO.getTxnNo());
        assertEquals("Txn Status Desc", actualConfirmStockLevelOneResponseVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualConfirmStockLevelOneResponseVO.getTxnType());
        assertEquals("Wallet Type", actualConfirmStockLevelOneResponseVO.getWalletType());
    }
}

