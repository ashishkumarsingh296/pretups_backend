package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CancelBatchC2SRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CancelBatchC2SRequestVO}
     *   <li>{@link CancelBatchC2SRequestVO#setBatchIDS(ArrayList)}
     *   <li>{@link CancelBatchC2SRequestVO#toString()}
     *   <li>{@link CancelBatchC2SRequestVO#getBatchIDS()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CancelBatchC2SRequestVO actualCancelBatchC2SRequestVO = new CancelBatchC2SRequestVO();
        ArrayList<String> batchIDS = new ArrayList<>();
        actualCancelBatchC2SRequestVO.setBatchIDS(batchIDS);
        String actualToStringResult = actualCancelBatchC2SRequestVO.toString();
        assertSame(batchIDS, actualCancelBatchC2SRequestVO.getBatchIDS());
        assertEquals("CancelBatchC2SRequestVO [data=[]]", actualToStringResult);
    }
}

