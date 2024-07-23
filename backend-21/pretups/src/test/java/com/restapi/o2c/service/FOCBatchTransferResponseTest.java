package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FOCBatchTransferResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FOCBatchTransferResponse}
     *   <li>{@link FOCBatchTransferResponse#setBatchID(String)}
     *   <li>{@link FOCBatchTransferResponse#setFileAttachment(String)}
     *   <li>{@link FOCBatchTransferResponse#setFileName(String)}
     *   <li>{@link FOCBatchTransferResponse#toString()}
     *   <li>{@link FOCBatchTransferResponse#getBatchID()}
     *   <li>{@link FOCBatchTransferResponse#getFileAttachment()}
     *   <li>{@link FOCBatchTransferResponse#getFileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FOCBatchTransferResponse actualFocBatchTransferResponse = new FOCBatchTransferResponse();
        actualFocBatchTransferResponse.setBatchID("Batch ID");
        actualFocBatchTransferResponse.setFileAttachment("File Attachment");
        actualFocBatchTransferResponse.setFileName("foo.txt");
        String actualToStringResult = actualFocBatchTransferResponse.toString();
        assertEquals("Batch ID", actualFocBatchTransferResponse.getBatchID());
        assertEquals("File Attachment", actualFocBatchTransferResponse.getFileAttachment());
        assertEquals("foo.txt", actualFocBatchTransferResponse.getFileName());
        assertEquals("FOCBatchTransferResponse [fileAttachment=File Attachment, batchID=Batch ID, fileName=foo.txt]",
                actualToStringResult);
    }
}

