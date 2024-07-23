package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ConfirmStockResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ConfirmStockResponseVO}
     *   <li>{@link ConfirmStockResponseVO#setEntryType(String)}
     *   <li>{@link ConfirmStockResponseVO#setFromDateStr(String)}
     *   <li>{@link ConfirmStockResponseVO#setMaxAmountLimit(long)}
     *   <li>{@link ConfirmStockResponseVO#setNetworkCode(String)}
     *   <li>{@link ConfirmStockResponseVO#setNetworkCodeFor(String)}
     *   <li>{@link ConfirmStockResponseVO#setNetworkForName(String)}
     *   <li>{@link ConfirmStockResponseVO#setNetworkName(String)}
     *   <li>{@link ConfirmStockResponseVO#setReferenceNumber(String)}
     *   <li>{@link ConfirmStockResponseVO#setRemarks(String)}
     *   <li>{@link ConfirmStockResponseVO#setRequesterName(String)}
     *   <li>{@link ConfirmStockResponseVO#setStockDateStr(String)}
     *   <li>{@link ConfirmStockResponseVO#setStockProductList(ArrayList)}
     *   <li>{@link ConfirmStockResponseVO#setStockType(String)}
     *   <li>{@link ConfirmStockResponseVO#setToDateStr(String)}
     *   <li>{@link ConfirmStockResponseVO#setTotalMrp(long)}
     *   <li>{@link ConfirmStockResponseVO#setTotalMrpStr(String)}
     *   <li>{@link ConfirmStockResponseVO#setTotalQty(double)}
     *   <li>{@link ConfirmStockResponseVO#setTxnStatus(String)}
     *   <li>{@link ConfirmStockResponseVO#setTxnType(String)}
     *   <li>{@link ConfirmStockResponseVO#setUserID(String)}
     *   <li>{@link ConfirmStockResponseVO#setWalletType(String)}
     *   <li>{@link ConfirmStockResponseVO#getEntryType()}
     *   <li>{@link ConfirmStockResponseVO#getFromDateStr()}
     *   <li>{@link ConfirmStockResponseVO#getMaxAmountLimit()}
     *   <li>{@link ConfirmStockResponseVO#getNetworkCode()}
     *   <li>{@link ConfirmStockResponseVO#getNetworkCodeFor()}
     *   <li>{@link ConfirmStockResponseVO#getNetworkForName()}
     *   <li>{@link ConfirmStockResponseVO#getNetworkName()}
     *   <li>{@link ConfirmStockResponseVO#getReferenceNumber()}
     *   <li>{@link ConfirmStockResponseVO#getRemarks()}
     *   <li>{@link ConfirmStockResponseVO#getRequesterName()}
     *   <li>{@link ConfirmStockResponseVO#getStockDateStr()}
     *   <li>{@link ConfirmStockResponseVO#getStockProductList()}
     *   <li>{@link ConfirmStockResponseVO#getStockType()}
     *   <li>{@link ConfirmStockResponseVO#getToDateStr()}
     *   <li>{@link ConfirmStockResponseVO#getTotalMrp()}
     *   <li>{@link ConfirmStockResponseVO#getTotalMrpStr()}
     *   <li>{@link ConfirmStockResponseVO#getTotalQty()}
     *   <li>{@link ConfirmStockResponseVO#getTxnStatus()}
     *   <li>{@link ConfirmStockResponseVO#getTxnType()}
     *   <li>{@link ConfirmStockResponseVO#getUserID()}
     *   <li>{@link ConfirmStockResponseVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ConfirmStockResponseVO actualConfirmStockResponseVO = new ConfirmStockResponseVO();
        actualConfirmStockResponseVO.setEntryType("Entry Type");
        actualConfirmStockResponseVO.setFromDateStr("2020-03-01");
        actualConfirmStockResponseVO.setMaxAmountLimit(1L);
        actualConfirmStockResponseVO.setNetworkCode("Network Code");
        actualConfirmStockResponseVO.setNetworkCodeFor("Network Code For");
        actualConfirmStockResponseVO.setNetworkForName("Network For Name");
        actualConfirmStockResponseVO.setNetworkName("Network Name");
        actualConfirmStockResponseVO.setReferenceNumber("42");
        actualConfirmStockResponseVO.setRemarks("Remarks");
        actualConfirmStockResponseVO.setRequesterName("Requester Name");
        actualConfirmStockResponseVO.setStockDateStr("2020-03-01");
        ArrayList stockProductList = new ArrayList();
        actualConfirmStockResponseVO.setStockProductList(stockProductList);
        actualConfirmStockResponseVO.setStockType("Stock Type");
        actualConfirmStockResponseVO.setToDateStr("2020-03-01");
        actualConfirmStockResponseVO.setTotalMrp(1L);
        actualConfirmStockResponseVO.setTotalMrpStr("Total Mrp Str");
        actualConfirmStockResponseVO.setTotalQty(10.0d);
        actualConfirmStockResponseVO.setTxnStatus("Txn Status");
        actualConfirmStockResponseVO.setTxnType("Txn Type");
        actualConfirmStockResponseVO.setUserID("User ID");
        actualConfirmStockResponseVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualConfirmStockResponseVO.getEntryType());
        assertEquals("2020-03-01", actualConfirmStockResponseVO.getFromDateStr());
        assertEquals(1L, actualConfirmStockResponseVO.getMaxAmountLimit());
        assertEquals("Network Code", actualConfirmStockResponseVO.getNetworkCode());
        assertEquals("Network Code For", actualConfirmStockResponseVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualConfirmStockResponseVO.getNetworkForName());
        assertEquals("Network Name", actualConfirmStockResponseVO.getNetworkName());
        assertEquals("42", actualConfirmStockResponseVO.getReferenceNumber());
        assertEquals("Remarks", actualConfirmStockResponseVO.getRemarks());
        assertEquals("Requester Name", actualConfirmStockResponseVO.getRequesterName());
        assertEquals("2020-03-01", actualConfirmStockResponseVO.getStockDateStr());
        assertSame(stockProductList, actualConfirmStockResponseVO.getStockProductList());
        assertEquals("Stock Type", actualConfirmStockResponseVO.getStockType());
        assertEquals("2020-03-01", actualConfirmStockResponseVO.getToDateStr());
        assertEquals(1L, actualConfirmStockResponseVO.getTotalMrp());
        assertEquals("Total Mrp Str", actualConfirmStockResponseVO.getTotalMrpStr());
        assertEquals(10.0d, actualConfirmStockResponseVO.getTotalQty(), 0.0);
        assertEquals("Txn Status", actualConfirmStockResponseVO.getTxnStatus());
        assertEquals("Txn Type", actualConfirmStockResponseVO.getTxnType());
        assertEquals("User ID", actualConfirmStockResponseVO.getUserID());
        assertEquals("Wallet Type", actualConfirmStockResponseVO.getWalletType());
    }
}

