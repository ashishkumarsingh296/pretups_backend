package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class LevelTwoApprovalListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LevelTwoApprovalListResponseVO}
     *   <li>{@link LevelTwoApprovalListResponseVO#setNetworkCode(String)}
     *   <li>{@link LevelTwoApprovalListResponseVO#setStockTxnList(ArrayList)}
     *   <li>{@link LevelTwoApprovalListResponseVO#setUserID(String)}
     *   <li>{@link LevelTwoApprovalListResponseVO#getNetworkCode()}
     *   <li>{@link LevelTwoApprovalListResponseVO#getStockTxnList()}
     *   <li>{@link LevelTwoApprovalListResponseVO#getUserID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LevelTwoApprovalListResponseVO actualLevelTwoApprovalListResponseVO = new LevelTwoApprovalListResponseVO();
        actualLevelTwoApprovalListResponseVO.setNetworkCode("Network Code");
        ArrayList stockTxnList = new ArrayList();
        actualLevelTwoApprovalListResponseVO.setStockTxnList(stockTxnList);
        actualLevelTwoApprovalListResponseVO.setUserID("User ID");
        assertEquals("Network Code", actualLevelTwoApprovalListResponseVO.getNetworkCode());
        assertSame(stockTxnList, actualLevelTwoApprovalListResponseVO.getStockTxnList());
        assertEquals("User ID", actualLevelTwoApprovalListResponseVO.getUserID());
    }
}

