package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BatchC2cTransferResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchC2cTransferResponseVO}
     *   <li>{@link BatchC2cTransferResponseVO#setTransferList(List)}
     *   <li>{@link BatchC2cTransferResponseVO#toString()}
     *   <li>{@link BatchC2cTransferResponseVO#getTransferList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchC2cTransferResponseVO actualBatchC2cTransferResponseVO = new BatchC2cTransferResponseVO();
        ArrayList<C2CBatchMasterVO> transferList = new ArrayList<>();
        actualBatchC2cTransferResponseVO.setTransferList(transferList);
        String actualToStringResult = actualBatchC2cTransferResponseVO.toString();
        assertSame(transferList, actualBatchC2cTransferResponseVO.getTransferList());
        assertEquals("BatchC2cTransferResponseVO [transferList=[]]", actualToStringResult);
    }
}

