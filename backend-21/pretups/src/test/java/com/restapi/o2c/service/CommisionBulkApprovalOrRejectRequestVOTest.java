package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class CommisionBulkApprovalOrRejectRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommisionBulkApprovalOrRejectRequestVO}
     *   <li>{@link CommisionBulkApprovalOrRejectRequestVO#setCommisionBulkApprovalOrRejectRequestData(CommisionBulkApprovalOrRejectRequestData)}
     *   <li>{@link CommisionBulkApprovalOrRejectRequestVO#toString()}
     *   <li>{@link CommisionBulkApprovalOrRejectRequestVO#getCommisionBulkApprovalOrRejectRequestData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommisionBulkApprovalOrRejectRequestVO actualCommisionBulkApprovalOrRejectRequestVO = new CommisionBulkApprovalOrRejectRequestVO();
        CommisionBulkApprovalOrRejectRequestData commisionBulkApprovalOrRejectRequestData = new CommisionBulkApprovalOrRejectRequestData();
        actualCommisionBulkApprovalOrRejectRequestVO
                .setCommisionBulkApprovalOrRejectRequestData(commisionBulkApprovalOrRejectRequestData);
        String actualToStringResult = actualCommisionBulkApprovalOrRejectRequestVO.toString();
        assertSame(commisionBulkApprovalOrRejectRequestData,
                actualCommisionBulkApprovalOrRejectRequestVO.getCommisionBulkApprovalOrRejectRequestData());
        assertEquals("commisionBulkApprovalOrRejectRequestData [commisionBulkApprovalOrRejectRequestData=CommisionBulkAppr"
                + "ovalOrRejectRequestData [language1=null, language2=null, pin=null, batchName=null, requestType=null,"
                + " request=null, BatchId=null, Remarks=null]]", actualToStringResult);
    }
}

