package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class O2CBatchApprovalDetailsRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchApprovalDetailsRequestVO}
     *   <li>{@link O2CBatchApprovalDetailsRequestVO#setData(O2CBatchApprovalDetails)}
     *   <li>{@link O2CBatchApprovalDetailsRequestVO#toString()}
     *   <li>{@link O2CBatchApprovalDetailsRequestVO#getData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchApprovalDetailsRequestVO actualO2cBatchApprovalDetailsRequestVO = new O2CBatchApprovalDetailsRequestVO();
        O2CBatchApprovalDetails data = new O2CBatchApprovalDetails();
        actualO2cBatchApprovalDetailsRequestVO.setData(data);
        String actualToStringResult = actualO2cBatchApprovalDetailsRequestVO.toString();
        assertSame(data, actualO2cBatchApprovalDetailsRequestVO.getData());
        assertEquals("O2CBatchApprovalDetailsRequestVO [data=O2CBatchApprovalDetails [approvalType=null, batchId=null,"
                + " approvalLevel=null, approvalSubType=null]]", actualToStringResult);
    }
}

