package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CBulkApprovalOrRejectRequestDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBulkApprovalOrRejectRequestData}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setBatchId(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setBatchName(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setLanguage1(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setLanguage2(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setPin(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setProduct(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setRemarks(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setRequest(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setRequestType(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#setService(String)}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#toString()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getBatchId()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getBatchName()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getLanguage1()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getLanguage2()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getPin()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getProduct()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getRemarks()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getRequest()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getRequestType()}
     *   <li>{@link O2CBulkApprovalOrRejectRequestData#getService()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBulkApprovalOrRejectRequestData actualO2cBulkApprovalOrRejectRequestData = new O2CBulkApprovalOrRejectRequestData();
        actualO2cBulkApprovalOrRejectRequestData.setBatchId("42");
        actualO2cBulkApprovalOrRejectRequestData.setBatchName("Batch Name");
        actualO2cBulkApprovalOrRejectRequestData.setLanguage1("en");
        actualO2cBulkApprovalOrRejectRequestData.setLanguage2("en");
        actualO2cBulkApprovalOrRejectRequestData.setPin("Pin");
        actualO2cBulkApprovalOrRejectRequestData.setProduct("Product");
        actualO2cBulkApprovalOrRejectRequestData.setRemarks("Remarks");
        actualO2cBulkApprovalOrRejectRequestData.setRequest("Request");
        actualO2cBulkApprovalOrRejectRequestData.setRequestType("Request Type");
        actualO2cBulkApprovalOrRejectRequestData.setService("Service");
        String actualToStringResult = actualO2cBulkApprovalOrRejectRequestData.toString();
        assertEquals("42", actualO2cBulkApprovalOrRejectRequestData.getBatchId());
        assertEquals("Batch Name", actualO2cBulkApprovalOrRejectRequestData.getBatchName());
        assertEquals("en", actualO2cBulkApprovalOrRejectRequestData.getLanguage1());
        assertEquals("en", actualO2cBulkApprovalOrRejectRequestData.getLanguage2());
        assertEquals("Pin", actualO2cBulkApprovalOrRejectRequestData.getPin());
        assertEquals("Product", actualO2cBulkApprovalOrRejectRequestData.getProduct());
        assertEquals("Remarks", actualO2cBulkApprovalOrRejectRequestData.getRemarks());
        assertEquals("Request", actualO2cBulkApprovalOrRejectRequestData.getRequest());
        assertEquals("Request Type", actualO2cBulkApprovalOrRejectRequestData.getRequestType());
        assertEquals("Service", actualO2cBulkApprovalOrRejectRequestData.getService());
        assertEquals("O2CBulkApprovalOrRejectRequestData [service=Service, language1=en, language2=en, product=Product,"
                + " pin=Pin, batchName=Batch Name, requestType=Request Type, request=Request, BatchId=42,"
                + " Remarks=Remarks]", actualToStringResult);
    }
}

