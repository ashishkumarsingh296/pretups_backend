package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2CBulkApprovalDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CBulkApprovalData}
     *   <li>{@link C2CBulkApprovalData#setApprovalStatus(String)}
     *   <li>{@link C2CBulkApprovalData#setLanguage1(String)}
     *   <li>{@link C2CBulkApprovalData#setLanguage2(String)}
     *   <li>{@link C2CBulkApprovalData#setRemarks(String)}
     *   <li>{@link C2CBulkApprovalData#setTxnID(String)}
     *   <li>{@link C2CBulkApprovalData#setTxnType(String)}
     *   <li>{@link C2CBulkApprovalData#toString()}
     *   <li>{@link C2CBulkApprovalData#getApprovalStatus()}
     *   <li>{@link C2CBulkApprovalData#getLanguage1()}
     *   <li>{@link C2CBulkApprovalData#getLanguage2()}
     *   <li>{@link C2CBulkApprovalData#getRemarks()}
     *   <li>{@link C2CBulkApprovalData#getTxnID()}
     *   <li>{@link C2CBulkApprovalData#getTxnType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CBulkApprovalData actualC2cBulkApprovalData = new C2CBulkApprovalData();
        actualC2cBulkApprovalData.setApprovalStatus("Approval Status");
        actualC2cBulkApprovalData.setLanguage1("en");
        actualC2cBulkApprovalData.setLanguage2("en");
        actualC2cBulkApprovalData.setRemarks("Remarks");
        actualC2cBulkApprovalData.setTxnID("Txn ID");
        actualC2cBulkApprovalData.setTxnType("Txn Type");
        String actualToStringResult = actualC2cBulkApprovalData.toString();
        assertEquals("Approval Status", actualC2cBulkApprovalData.getApprovalStatus());
        assertEquals("en", actualC2cBulkApprovalData.getLanguage1());
        assertEquals("en", actualC2cBulkApprovalData.getLanguage2());
        assertEquals("Remarks", actualC2cBulkApprovalData.getRemarks());
        assertEquals("Txn ID", actualC2cBulkApprovalData.getTxnID());
        assertEquals("Txn Type", actualC2cBulkApprovalData.getTxnType());
        assertEquals("C2CBulkApprovalRequestVO [txnID=Txn ID, approvalStatus=Approval Status, remarks=Remarks]",
                actualToStringResult);
    }
}

