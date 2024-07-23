package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CBatchApRejTransferResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchApRejTransferResponse}
     *   <li>{@link O2CBatchApRejTransferResponse#setBatchID(String)}
     *   <li>{@link O2CBatchApRejTransferResponse#setFileAttachment(String)}
     *   <li>{@link O2CBatchApRejTransferResponse#setFileName(String)}
     *   <li>{@link O2CBatchApRejTransferResponse#setFileType(String)}
     *   <li>{@link O2CBatchApRejTransferResponse#setNoOfRecords(String)}
     *   <li>{@link O2CBatchApRejTransferResponse#setProcessedRecs(String)}
     *   <li>{@link O2CBatchApRejTransferResponse#getBatchID()}
     *   <li>{@link O2CBatchApRejTransferResponse#getFileAttachment()}
     *   <li>{@link O2CBatchApRejTransferResponse#getFileName()}
     *   <li>{@link O2CBatchApRejTransferResponse#getFileType()}
     *   <li>{@link O2CBatchApRejTransferResponse#getNoOfRecords()}
     *   <li>{@link O2CBatchApRejTransferResponse#getProcessedRecs()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchApRejTransferResponse actualO2cBatchApRejTransferResponse = new O2CBatchApRejTransferResponse();
        actualO2cBatchApRejTransferResponse.setBatchID("Batch ID");
        actualO2cBatchApRejTransferResponse.setFileAttachment("File Attachment");
        actualO2cBatchApRejTransferResponse.setFileName("foo.txt");
        actualO2cBatchApRejTransferResponse.setFileType("File Type");
        actualO2cBatchApRejTransferResponse.setNoOfRecords(" no Of Records");
        actualO2cBatchApRejTransferResponse.setProcessedRecs(" processed Recs");
        assertEquals("Batch ID", actualO2cBatchApRejTransferResponse.getBatchID());
        assertEquals("File Attachment", actualO2cBatchApRejTransferResponse.getFileAttachment());
        assertEquals("foo.txt", actualO2cBatchApRejTransferResponse.getFileName());
        assertEquals("File Type", actualO2cBatchApRejTransferResponse.getFileType());
        assertEquals(" no Of Records", actualO2cBatchApRejTransferResponse.getNoOfRecords());
        assertEquals(" processed Recs", actualO2cBatchApRejTransferResponse.getProcessedRecs());
    }
}

