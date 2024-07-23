package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class O2CBulkApprovalOrRejectRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBulkApprovalOrRejectRequestVO}
     *   <li>{@link O2CBulkApprovalOrRejectRequestVO#setO2CBulkApprovalOrRejectRequestData(O2CBulkApprovalOrRejectRequestData)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestVO#toString()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestVO#getO2CBulkApprovalOrRejectRequestData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBulkApprovalOrRejectRequestVO actualO2cBulkApprovalOrRejectRequestVO = new O2CBulkApprovalOrRejectRequestVO();
        O2CBulkApprovalOrRejectRequestData o2cBulkApprovalOrRejectRequestData = new O2CBulkApprovalOrRejectRequestData();
        actualO2cBulkApprovalOrRejectRequestVO.setO2CBulkApprovalOrRejectRequestData(o2cBulkApprovalOrRejectRequestData);
        String actualToStringResult = actualO2cBulkApprovalOrRejectRequestVO.toString();
        assertSame(o2cBulkApprovalOrRejectRequestData,
                actualO2cBulkApprovalOrRejectRequestVO.getO2CBulkApprovalOrRejectRequestData());
        assertEquals(
                "O2CBulkApprovalOrRejectRequestVO [o2CBulkApprovalOrRejectRequestData=O2CBulkApprovalOrRejectRequestData"
                        + " [service=null, language1=null, language2=null, product=null, pin=null, batchName=null, requestType=null,"
                        + " request=null, BatchId=null, Remarks=null]]",
                actualToStringResult);
    }
}

