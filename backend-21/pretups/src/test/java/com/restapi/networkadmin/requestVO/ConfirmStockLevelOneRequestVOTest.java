package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;

import java.util.ArrayList;

import org.junit.Test;

public class ConfirmStockLevelOneRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ConfirmStockLevelOneRequestVO}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setEntryType(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setFirstLevelApprovedBy(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setFirstLevelRemarks(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setLastModifiedTime(long)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setNetworkCodeFor(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setNetworkForName(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setReferenceNumber(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setRemarks(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setRequesterName(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setSecondLevelApprovedBy(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setSecondLevelRemarks(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setStockDateStr(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setStockItemsList(ArrayList)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setStockType(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setTotalMrpStr(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setTxnNo(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setTxnStatusDesc(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setTxnType(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#setWalletType(String)}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getEntryType()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getFirstLevelApprovedBy()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getFirstLevelRemarks()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getLastModifiedTime()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getNetworkCodeFor()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getNetworkForName()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getReferenceNumber()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getRemarks()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getRequesterName()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getSecondLevelApprovedBy()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getSecondLevelRemarks()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getStockDateStr()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getStockItemsList()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getStockType()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getTotalMrpStr()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getTxnNo()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getTxnStatusDesc()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getTxnType()}
     *   <li>{@link ConfirmStockLevelOneRequestVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ConfirmStockLevelOneRequestVO actualConfirmStockLevelOneRequestVO = new ConfirmStockLevelOneRequestVO();
        actualConfirmStockLevelOneRequestVO.setEntryType("Entry Type");
        actualConfirmStockLevelOneRequestVO.setFirstLevelApprovedBy("First Level Approved By");
        actualConfirmStockLevelOneRequestVO.setFirstLevelRemarks("First Level Remarks");
        actualConfirmStockLevelOneRequestVO.setLastModifiedTime(1L);
        actualConfirmStockLevelOneRequestVO.setNetworkCodeFor("Network Code For");
        actualConfirmStockLevelOneRequestVO.setNetworkForName("Network For Name");
        actualConfirmStockLevelOneRequestVO.setReferenceNumber("42");
        actualConfirmStockLevelOneRequestVO.setRemarks("Remarks");
        actualConfirmStockLevelOneRequestVO.setRequesterName("Requester Name");
        actualConfirmStockLevelOneRequestVO.setSecondLevelApprovedBy("Second Level Approved By");
        actualConfirmStockLevelOneRequestVO.setSecondLevelRemarks("Second Level Remarks");
        actualConfirmStockLevelOneRequestVO.setStockDateStr("2020-03-01");
        ArrayList<NetworkStockTxnItemsVO> stockItemsList = new ArrayList<>();
        actualConfirmStockLevelOneRequestVO.setStockItemsList(stockItemsList);
        actualConfirmStockLevelOneRequestVO.setStockType("Stock Type");
        actualConfirmStockLevelOneRequestVO.setTotalMrpStr("Total Mrp Str");
        actualConfirmStockLevelOneRequestVO.setTxnNo("Txn No");
        actualConfirmStockLevelOneRequestVO.setTxnStatusDesc("Txn Status Desc");
        actualConfirmStockLevelOneRequestVO.setTxnType("Txn Type");
        actualConfirmStockLevelOneRequestVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualConfirmStockLevelOneRequestVO.getEntryType());
        assertEquals("First Level Approved By", actualConfirmStockLevelOneRequestVO.getFirstLevelApprovedBy());
        assertEquals("First Level Remarks", actualConfirmStockLevelOneRequestVO.getFirstLevelRemarks());
        assertEquals(1L, actualConfirmStockLevelOneRequestVO.getLastModifiedTime());
        assertEquals("Network Code For", actualConfirmStockLevelOneRequestVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualConfirmStockLevelOneRequestVO.getNetworkForName());
        assertEquals("42", actualConfirmStockLevelOneRequestVO.getReferenceNumber());
        assertEquals("Remarks", actualConfirmStockLevelOneRequestVO.getRemarks());
        assertEquals("Requester Name", actualConfirmStockLevelOneRequestVO.getRequesterName());
        assertEquals("Second Level Approved By", actualConfirmStockLevelOneRequestVO.getSecondLevelApprovedBy());
        assertEquals("Second Level Remarks", actualConfirmStockLevelOneRequestVO.getSecondLevelRemarks());
        assertEquals("2020-03-01", actualConfirmStockLevelOneRequestVO.getStockDateStr());
        assertSame(stockItemsList, actualConfirmStockLevelOneRequestVO.getStockItemsList());
        assertEquals("Stock Type", actualConfirmStockLevelOneRequestVO.getStockType());
        assertEquals("Total Mrp Str", actualConfirmStockLevelOneRequestVO.getTotalMrpStr());
        assertEquals("Txn No", actualConfirmStockLevelOneRequestVO.getTxnNo());
        assertEquals("Txn Status Desc", actualConfirmStockLevelOneRequestVO.getTxnStatusDesc());
        assertEquals("Txn Type", actualConfirmStockLevelOneRequestVO.getTxnType());
        assertEquals("Wallet Type", actualConfirmStockLevelOneRequestVO.getWalletType());
    }
}

