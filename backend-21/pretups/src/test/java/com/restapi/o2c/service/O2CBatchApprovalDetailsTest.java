package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CBatchApprovalDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchApprovalDetails}
     *   <li>{@link O2CBatchApprovalDetails#setApprovalLevel(String)}
     *   <li>{@link O2CBatchApprovalDetails#setApprovalSubType(String)}
     *   <li>{@link O2CBatchApprovalDetails#setApprovalType(String)}
     *   <li>{@link O2CBatchApprovalDetails#setBatchId(String)}
     *   <li>{@link O2CBatchApprovalDetails#toString()}
     *   <li>{@link O2CBatchApprovalDetails#getApprovalLevel()}
     *   <li>{@link O2CBatchApprovalDetails#getApprovalSubType()}
     *   <li>{@link O2CBatchApprovalDetails#getApprovalType()}
     *   <li>{@link O2CBatchApprovalDetails#getBatchId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchApprovalDetails actualO2cBatchApprovalDetails = new O2CBatchApprovalDetails();
        actualO2cBatchApprovalDetails.setApprovalLevel("Approval Level");
        actualO2cBatchApprovalDetails.setApprovalSubType("Approval Sub Type");
        actualO2cBatchApprovalDetails.setApprovalType("Approval Type");
        actualO2cBatchApprovalDetails.setBatchId("42");
        String actualToStringResult = actualO2cBatchApprovalDetails.toString();
        assertEquals("Approval Level", actualO2cBatchApprovalDetails.getApprovalLevel());
        assertEquals("Approval Sub Type", actualO2cBatchApprovalDetails.getApprovalSubType());
        assertEquals("Approval Type", actualO2cBatchApprovalDetails.getApprovalType());
        assertEquals("42", actualO2cBatchApprovalDetails.getBatchId());
        assertEquals("O2CBatchApprovalDetails [approvalType=Approval Type, batchId=42, approvalLevel=Approval Level,"
                + " approvalSubType=Approval Sub Type]", actualToStringResult);
    }
}

