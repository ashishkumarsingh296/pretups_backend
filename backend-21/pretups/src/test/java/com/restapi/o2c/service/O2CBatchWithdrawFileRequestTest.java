package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CBatchWithdrawFileRequestTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchWithdrawFileRequest}
     *   <li>{@link O2CBatchWithdrawFileRequest#setBatchName(String)}
     *   <li>{@link O2CBatchWithdrawFileRequest#setFileAttachment(String)}
     *   <li>{@link O2CBatchWithdrawFileRequest#setFileName(String)}
     *   <li>{@link O2CBatchWithdrawFileRequest#setFileType(String)}
     *   <li>{@link O2CBatchWithdrawFileRequest#setLanguage1(String)}
     *   <li>{@link O2CBatchWithdrawFileRequest#setLanguage2(String)}
     *   <li>{@link O2CBatchWithdrawFileRequest#setPin(String)}
     *   <li>{@link O2CBatchWithdrawFileRequest#toString()}
     *   <li>{@link O2CBatchWithdrawFileRequest#getBatchName()}
     *   <li>{@link O2CBatchWithdrawFileRequest#getFileAttachment()}
     *   <li>{@link O2CBatchWithdrawFileRequest#getFileName()}
     *   <li>{@link O2CBatchWithdrawFileRequest#getFileType()}
     *   <li>{@link O2CBatchWithdrawFileRequest#getLanguage1()}
     *   <li>{@link O2CBatchWithdrawFileRequest#getLanguage2()}
     *   <li>{@link O2CBatchWithdrawFileRequest#getPin()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchWithdrawFileRequest actualO2cBatchWithdrawFileRequest = new O2CBatchWithdrawFileRequest();
        actualO2cBatchWithdrawFileRequest.setBatchName("Batch Name");
        actualO2cBatchWithdrawFileRequest.setFileAttachment("File Attachment");
        actualO2cBatchWithdrawFileRequest.setFileName("foo.txt");
        actualO2cBatchWithdrawFileRequest.setFileType("File Type");
        actualO2cBatchWithdrawFileRequest.setLanguage1("en");
        actualO2cBatchWithdrawFileRequest.setLanguage2("en");
        actualO2cBatchWithdrawFileRequest.setPin("Pin");
        String actualToStringResult = actualO2cBatchWithdrawFileRequest.toString();
        assertEquals("Batch Name", actualO2cBatchWithdrawFileRequest.getBatchName());
        assertEquals("File Attachment", actualO2cBatchWithdrawFileRequest.getFileAttachment());
        assertEquals("foo.txt", actualO2cBatchWithdrawFileRequest.getFileName());
        assertEquals("File Type", actualO2cBatchWithdrawFileRequest.getFileType());
        assertEquals("en", actualO2cBatchWithdrawFileRequest.getLanguage1());
        assertEquals("en", actualO2cBatchWithdrawFileRequest.getLanguage2());
        assertEquals("Pin", actualO2cBatchWithdrawFileRequest.getPin());
        assertEquals("C2CFileUploadApiRequest [fileType=File Type, fileName=foo.txt, fileAttachment=File Attachment]",
                actualToStringResult);
    }
}

