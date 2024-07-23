package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

import java.util.ArrayList;

import org.junit.Test;

public class BatchListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchListResponseVO}
     *   <li>{@link BatchListResponseVO#setBatchIdList(ArrayList)}
     *   <li>{@link BatchListResponseVO#toString()}
     *   <li>{@link BatchListResponseVO#getBatchIdList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchListResponseVO actualBatchListResponseVO = new BatchListResponseVO();
        ArrayList<VomsVoucherVO> batchIdList = new ArrayList<>();
        actualBatchListResponseVO.setBatchIdList(batchIdList);
        String actualToStringResult = actualBatchListResponseVO.toString();
        assertSame(batchIdList, actualBatchListResponseVO.getBatchIdList());
        assertEquals("BatchListResponseVO [batchIdList=[]]", actualToStringResult);
    }
}

