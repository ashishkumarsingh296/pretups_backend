package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class O2CBatchWithdrawFileResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchWithdrawFileResponse}
     *   <li>{@link O2CBatchWithdrawFileResponse#setBatchID(String)}
     *   <li>{@link O2CBatchWithdrawFileResponse#setFileAttachment(String)}
     *   <li>{@link O2CBatchWithdrawFileResponse#setFileName(String)}
     *   <li>{@link O2CBatchWithdrawFileResponse#setFileValidationErrorList(ArrayList)}
     *   <li>{@link O2CBatchWithdrawFileResponse#toString()}
     *   <li>{@link O2CBatchWithdrawFileResponse#getBatchID()}
     *   <li>{@link O2CBatchWithdrawFileResponse#getFileAttachment()}
     *   <li>{@link O2CBatchWithdrawFileResponse#getFileName()}
     *   <li>{@link O2CBatchWithdrawFileResponse#getFileValidationErrorList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchWithdrawFileResponse actualO2cBatchWithdrawFileResponse = new O2CBatchWithdrawFileResponse();
        actualO2cBatchWithdrawFileResponse.setBatchID("Batch ID");
        actualO2cBatchWithdrawFileResponse.setFileAttachment("File Attachment");
        actualO2cBatchWithdrawFileResponse.setFileName("foo.txt");
        ArrayList<String> fileValidationErrorList = new ArrayList<>();
        actualO2cBatchWithdrawFileResponse.setFileValidationErrorList(fileValidationErrorList);
        String actualToStringResult = actualO2cBatchWithdrawFileResponse.toString();
        assertEquals("Batch ID", actualO2cBatchWithdrawFileResponse.getBatchID());
        assertEquals("File Attachment", actualO2cBatchWithdrawFileResponse.getFileAttachment());
        assertEquals("foo.txt", actualO2cBatchWithdrawFileResponse.getFileName());
        assertSame(fileValidationErrorList, actualO2cBatchWithdrawFileResponse.getFileValidationErrorList());
        assertEquals("C2CFileUploadApiResponse [ fileValidationErrorList=[]]", actualToStringResult);
    }
}

