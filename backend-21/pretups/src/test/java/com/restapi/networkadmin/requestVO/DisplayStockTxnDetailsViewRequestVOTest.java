package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DisplayStockTxnDetailsViewRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DisplayStockTxnDetailsViewRequestVO}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#setEntryType(String)}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#setFromDateStr(String)}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#setNetworkCode(String)}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#setNetworkCodeFor(String)}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#setTmpTxnNo(String)}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#setToDateStr(String)}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#setTxnStatus(String)}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#getEntryType()}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#getFromDateStr()}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#getNetworkCode()}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#getNetworkCodeFor()}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#getTmpTxnNo()}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#getToDateStr()}
     *   <li>{@link DisplayStockTxnDetailsViewRequestVO#getTxnStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DisplayStockTxnDetailsViewRequestVO actualDisplayStockTxnDetailsViewRequestVO = new DisplayStockTxnDetailsViewRequestVO();
        actualDisplayStockTxnDetailsViewRequestVO.setEntryType("Entry Type");
        actualDisplayStockTxnDetailsViewRequestVO.setFromDateStr("2020-03-01");
        actualDisplayStockTxnDetailsViewRequestVO.setNetworkCode("Network Code");
        actualDisplayStockTxnDetailsViewRequestVO.setNetworkCodeFor("Network Code For");
        actualDisplayStockTxnDetailsViewRequestVO.setTmpTxnNo("Tmp Txn No");
        actualDisplayStockTxnDetailsViewRequestVO.setToDateStr("2020-03-01");
        actualDisplayStockTxnDetailsViewRequestVO.setTxnStatus("Txn Status");
        assertEquals("Entry Type", actualDisplayStockTxnDetailsViewRequestVO.getEntryType());
        assertEquals("2020-03-01", actualDisplayStockTxnDetailsViewRequestVO.getFromDateStr());
        assertEquals("Network Code", actualDisplayStockTxnDetailsViewRequestVO.getNetworkCode());
        assertEquals("Network Code For", actualDisplayStockTxnDetailsViewRequestVO.getNetworkCodeFor());
        assertEquals("Tmp Txn No", actualDisplayStockTxnDetailsViewRequestVO.getTmpTxnNo());
        assertEquals("2020-03-01", actualDisplayStockTxnDetailsViewRequestVO.getToDateStr());
        assertEquals("Txn Status", actualDisplayStockTxnDetailsViewRequestVO.getTxnStatus());
    }
}

