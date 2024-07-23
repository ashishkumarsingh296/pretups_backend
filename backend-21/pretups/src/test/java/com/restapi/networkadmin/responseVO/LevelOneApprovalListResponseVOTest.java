package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class LevelOneApprovalListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LevelOneApprovalListResponseVO}
     *   <li>{@link LevelOneApprovalListResponseVO#setNetworkCode(String)}
     *   <li>{@link LevelOneApprovalListResponseVO#setStockTxnList(ArrayList)}
     *   <li>{@link LevelOneApprovalListResponseVO#setUserID(String)}
     *   <li>{@link LevelOneApprovalListResponseVO#getNetworkCode()}
     *   <li>{@link LevelOneApprovalListResponseVO#getStockTxnList()}
     *   <li>{@link LevelOneApprovalListResponseVO#getUserID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LevelOneApprovalListResponseVO actualLevelOneApprovalListResponseVO = new LevelOneApprovalListResponseVO();
        actualLevelOneApprovalListResponseVO.setNetworkCode("Network Code");
        ArrayList stockTxnList = new ArrayList();
        actualLevelOneApprovalListResponseVO.setStockTxnList(stockTxnList);
        actualLevelOneApprovalListResponseVO.setUserID("User ID");
        assertEquals("Network Code", actualLevelOneApprovalListResponseVO.getNetworkCode());
        assertSame(stockTxnList, actualLevelOneApprovalListResponseVO.getStockTxnList());
        assertEquals("User ID", actualLevelOneApprovalListResponseVO.getUserID());
    }
}

