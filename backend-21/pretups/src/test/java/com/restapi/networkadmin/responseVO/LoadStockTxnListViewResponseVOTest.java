package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class LoadStockTxnListViewResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LoadStockTxnListViewResponseVO}
     *   <li>{@link LoadStockTxnListViewResponseVO#setNetworkCode(String)}
     *   <li>{@link LoadStockTxnListViewResponseVO#setStockTxnList(ArrayList)}
     *   <li>{@link LoadStockTxnListViewResponseVO#setUserID(String)}
     *   <li>{@link LoadStockTxnListViewResponseVO#getNetworkCode()}
     *   <li>{@link LoadStockTxnListViewResponseVO#getStockTxnList()}
     *   <li>{@link LoadStockTxnListViewResponseVO#getUserID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LoadStockTxnListViewResponseVO actualLoadStockTxnListViewResponseVO = new LoadStockTxnListViewResponseVO();
        actualLoadStockTxnListViewResponseVO.setNetworkCode("Network Code");
        ArrayList stockTxnList = new ArrayList();
        actualLoadStockTxnListViewResponseVO.setStockTxnList(stockTxnList);
        actualLoadStockTxnListViewResponseVO.setUserID("User ID");
        assertEquals("Network Code", actualLoadStockTxnListViewResponseVO.getNetworkCode());
        assertSame(stockTxnList, actualLoadStockTxnListViewResponseVO.getStockTxnList());
        assertEquals("User ID", actualLoadStockTxnListViewResponseVO.getUserID());
    }
}

