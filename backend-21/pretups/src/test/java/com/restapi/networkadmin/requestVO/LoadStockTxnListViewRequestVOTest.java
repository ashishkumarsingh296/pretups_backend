package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoadStockTxnListViewRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LoadStockTxnListViewRequestVO}
     *   <li>{@link LoadStockTxnListViewRequestVO#setEntryType(String)}
     *   <li>{@link LoadStockTxnListViewRequestVO#setFromDateStr(String)}
     *   <li>{@link LoadStockTxnListViewRequestVO#setNetworkCode(String)}
     *   <li>{@link LoadStockTxnListViewRequestVO#setNetworkCodeFor(String)}
     *   <li>{@link LoadStockTxnListViewRequestVO#setTmpTxnNo(String)}
     *   <li>{@link LoadStockTxnListViewRequestVO#setToDateStr(String)}
     *   <li>{@link LoadStockTxnListViewRequestVO#setTxnStatus(String)}
     *   <li>{@link LoadStockTxnListViewRequestVO#getEntryType()}
     *   <li>{@link LoadStockTxnListViewRequestVO#getFromDateStr()}
     *   <li>{@link LoadStockTxnListViewRequestVO#getNetworkCode()}
     *   <li>{@link LoadStockTxnListViewRequestVO#getNetworkCodeFor()}
     *   <li>{@link LoadStockTxnListViewRequestVO#getTmpTxnNo()}
     *   <li>{@link LoadStockTxnListViewRequestVO#getToDateStr()}
     *   <li>{@link LoadStockTxnListViewRequestVO#getTxnStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LoadStockTxnListViewRequestVO actualLoadStockTxnListViewRequestVO = new LoadStockTxnListViewRequestVO();
        actualLoadStockTxnListViewRequestVO.setEntryType("Entry Type");
        actualLoadStockTxnListViewRequestVO.setFromDateStr("2020-03-01");
        actualLoadStockTxnListViewRequestVO.setNetworkCode("Network Code");
        actualLoadStockTxnListViewRequestVO.setNetworkCodeFor("Network Code For");
        actualLoadStockTxnListViewRequestVO.setTmpTxnNo("Tmp Txn No");
        actualLoadStockTxnListViewRequestVO.setToDateStr("2020-03-01");
        actualLoadStockTxnListViewRequestVO.setTxnStatus("Txn Status");
        assertEquals("Entry Type", actualLoadStockTxnListViewRequestVO.getEntryType());
        assertEquals("2020-03-01", actualLoadStockTxnListViewRequestVO.getFromDateStr());
        assertEquals("Network Code", actualLoadStockTxnListViewRequestVO.getNetworkCode());
        assertEquals("Network Code For", actualLoadStockTxnListViewRequestVO.getNetworkCodeFor());
        assertEquals("Tmp Txn No", actualLoadStockTxnListViewRequestVO.getTmpTxnNo());
        assertEquals("2020-03-01", actualLoadStockTxnListViewRequestVO.getToDateStr());
        assertEquals("Txn Status", actualLoadStockTxnListViewRequestVO.getTxnStatus());
    }
}

