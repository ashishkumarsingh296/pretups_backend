package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CBatchTransferResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchTransferResponse}
     *   <li>{@link O2CBatchTransferResponse#setBatchID(String)}
     *   <li>{@link O2CBatchTransferResponse#setFileAttachment(String)}
     *   <li>{@link O2CBatchTransferResponse#setFileName(String)}
     *   <li>{@link O2CBatchTransferResponse#toString()}
     *   <li>{@link O2CBatchTransferResponse#getBatchID()}
     *   <li>{@link O2CBatchTransferResponse#getFileAttachment()}
     *   <li>{@link O2CBatchTransferResponse#getFileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchTransferResponse actualO2cBatchTransferResponse = new O2CBatchTransferResponse();
        actualO2cBatchTransferResponse.setBatchID("Batch ID");
        actualO2cBatchTransferResponse.setFileAttachment("File Attachment");
        actualO2cBatchTransferResponse.setFileName("foo.txt");
        String actualToStringResult = actualO2cBatchTransferResponse.toString();
        assertEquals("Batch ID", actualO2cBatchTransferResponse.getBatchID());
        assertEquals("File Attachment", actualO2cBatchTransferResponse.getFileAttachment());
        assertEquals("foo.txt", actualO2cBatchTransferResponse.getFileName());
        assertEquals("O2CBatchTransferResponse [fileAttachment=File Attachment, batchID=Batch ID, fileName=foo.txt]",
                actualToStringResult);
    }
}

