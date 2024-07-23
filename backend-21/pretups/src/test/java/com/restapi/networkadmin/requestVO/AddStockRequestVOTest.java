package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;

import java.util.ArrayList;

import org.junit.Test;

public class AddStockRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddStockRequestVO}
     *   <li>{@link AddStockRequestVO#setEntryType(String)}
     *   <li>{@link AddStockRequestVO#setFirstLevelAppLimit(long)}
     *   <li>{@link AddStockRequestVO#setFromDateStr(String)}
     *   <li>{@link AddStockRequestVO#setLastModifiedTime(long)}
     *   <li>{@link AddStockRequestVO#setMaxAmountLimit(long)}
     *   <li>{@link AddStockRequestVO#setNetworkCode(String)}
     *   <li>{@link AddStockRequestVO#setNetworkCodeFor(String)}
     *   <li>{@link AddStockRequestVO#setNetworkForName(String)}
     *   <li>{@link AddStockRequestVO#setNetworkName(String)}
     *   <li>{@link AddStockRequestVO#setReferenceNumber(String)}
     *   <li>{@link AddStockRequestVO#setRemarks(String)}
     *   <li>{@link AddStockRequestVO#setRequesterName(String)}
     *   <li>{@link AddStockRequestVO#setStockDateStr(String)}
     *   <li>{@link AddStockRequestVO#setStockProductList(ArrayList)}
     *   <li>{@link AddStockRequestVO#setStockType(String)}
     *   <li>{@link AddStockRequestVO#setToDateStr(String)}
     *   <li>{@link AddStockRequestVO#setTotalMrp(long)}
     *   <li>{@link AddStockRequestVO#setTotalMrpStr(String)}
     *   <li>{@link AddStockRequestVO#setTotalQty(double)}
     *   <li>{@link AddStockRequestVO#setTxnStatus(String)}
     *   <li>{@link AddStockRequestVO#setTxnType(String)}
     *   <li>{@link AddStockRequestVO#setUserID(String)}
     *   <li>{@link AddStockRequestVO#setWalletType(String)}
     *   <li>{@link AddStockRequestVO#getEntryType()}
     *   <li>{@link AddStockRequestVO#getFirstLevelAppLimit()}
     *   <li>{@link AddStockRequestVO#getFromDateStr()}
     *   <li>{@link AddStockRequestVO#getLastModifiedTime()}
     *   <li>{@link AddStockRequestVO#getMaxAmountLimit()}
     *   <li>{@link AddStockRequestVO#getNetworkCode()}
     *   <li>{@link AddStockRequestVO#getNetworkCodeFor()}
     *   <li>{@link AddStockRequestVO#getNetworkForName()}
     *   <li>{@link AddStockRequestVO#getNetworkName()}
     *   <li>{@link AddStockRequestVO#getReferenceNumber()}
     *   <li>{@link AddStockRequestVO#getRemarks()}
     *   <li>{@link AddStockRequestVO#getRequesterName()}
     *   <li>{@link AddStockRequestVO#getStockDateStr()}
     *   <li>{@link AddStockRequestVO#getStockProductList()}
     *   <li>{@link AddStockRequestVO#getStockType()}
     *   <li>{@link AddStockRequestVO#getToDateStr()}
     *   <li>{@link AddStockRequestVO#getTotalMrp()}
     *   <li>{@link AddStockRequestVO#getTotalMrpStr()}
     *   <li>{@link AddStockRequestVO#getTotalQty()}
     *   <li>{@link AddStockRequestVO#getTxnStatus()}
     *   <li>{@link AddStockRequestVO#getTxnType()}
     *   <li>{@link AddStockRequestVO#getUserID()}
     *   <li>{@link AddStockRequestVO#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddStockRequestVO actualAddStockRequestVO = new AddStockRequestVO();
        actualAddStockRequestVO.setEntryType("Entry Type");
        actualAddStockRequestVO.setFirstLevelAppLimit(1L);
        actualAddStockRequestVO.setFromDateStr("2020-03-01");
        actualAddStockRequestVO.setLastModifiedTime(1L);
        actualAddStockRequestVO.setMaxAmountLimit(1L);
        actualAddStockRequestVO.setNetworkCode("Network Code");
        actualAddStockRequestVO.setNetworkCodeFor("Network Code For");
        actualAddStockRequestVO.setNetworkForName("Network For Name");
        actualAddStockRequestVO.setNetworkName("Network Name");
        actualAddStockRequestVO.setReferenceNumber("42");
        actualAddStockRequestVO.setRemarks("Remarks");
        actualAddStockRequestVO.setRequesterName("Requester Name");
        actualAddStockRequestVO.setStockDateStr("2020-03-01");
        ArrayList<NetworkStockTxnItemsVO> stockProductList = new ArrayList<>();
        actualAddStockRequestVO.setStockProductList(stockProductList);
        actualAddStockRequestVO.setStockType("Stock Type");
        actualAddStockRequestVO.setToDateStr("2020-03-01");
        actualAddStockRequestVO.setTotalMrp(1L);
        actualAddStockRequestVO.setTotalMrpStr("Total Mrp Str");
        actualAddStockRequestVO.setTotalQty(10.0d);
        actualAddStockRequestVO.setTxnStatus("Txn Status");
        actualAddStockRequestVO.setTxnType("Txn Type");
        actualAddStockRequestVO.setUserID("User ID");
        actualAddStockRequestVO.setWalletType("Wallet Type");
        assertEquals("Entry Type", actualAddStockRequestVO.getEntryType());
        assertEquals(1L, actualAddStockRequestVO.getFirstLevelAppLimit());
        assertEquals("2020-03-01", actualAddStockRequestVO.getFromDateStr());
        assertEquals(1L, actualAddStockRequestVO.getLastModifiedTime());
        assertEquals(1L, actualAddStockRequestVO.getMaxAmountLimit());
        assertEquals("Network Code", actualAddStockRequestVO.getNetworkCode());
        assertEquals("Network Code For", actualAddStockRequestVO.getNetworkCodeFor());
        assertEquals("Network For Name", actualAddStockRequestVO.getNetworkForName());
        assertEquals("Network Name", actualAddStockRequestVO.getNetworkName());
        assertEquals("42", actualAddStockRequestVO.getReferenceNumber());
        assertEquals("Remarks", actualAddStockRequestVO.getRemarks());
        assertEquals("Requester Name", actualAddStockRequestVO.getRequesterName());
        assertEquals("2020-03-01", actualAddStockRequestVO.getStockDateStr());
        assertSame(stockProductList, actualAddStockRequestVO.getStockProductList());
        assertEquals("Stock Type", actualAddStockRequestVO.getStockType());
        assertEquals("2020-03-01", actualAddStockRequestVO.getToDateStr());
        assertEquals(1L, actualAddStockRequestVO.getTotalMrp());
        assertEquals("Total Mrp Str", actualAddStockRequestVO.getTotalMrpStr());
        assertEquals(10.0d, actualAddStockRequestVO.getTotalQty(), 0.0);
        assertEquals("Txn Status", actualAddStockRequestVO.getTxnStatus());
        assertEquals("Txn Type", actualAddStockRequestVO.getTxnType());
        assertEquals("User ID", actualAddStockRequestVO.getUserID());
        assertEquals("Wallet Type", actualAddStockRequestVO.getWalletType());
    }
}

