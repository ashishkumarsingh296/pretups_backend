package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DvdBulkResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DvdBulkResponse}
     *   <li>{@link DvdBulkResponse#setFileAttachment(String)}
     *   <li>{@link DvdBulkResponse#setFileName(String)}
     *   <li>{@link DvdBulkResponse#setTxnBatchId(String)}
     *   <li>{@link DvdBulkResponse#setTxnDetailsList(List)}
     *   <li>{@link DvdBulkResponse#toString()}
     *   <li>{@link DvdBulkResponse#getFileAttachment()}
     *   <li>{@link DvdBulkResponse#getFileName()}
     *   <li>{@link DvdBulkResponse#getTxnBatchId()}
     *   <li>{@link DvdBulkResponse#getTxnDetailsList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DvdBulkResponse actualDvdBulkResponse = new DvdBulkResponse();
        actualDvdBulkResponse.setFileAttachment("File Attachment");
        actualDvdBulkResponse.setFileName("foo.txt");
        actualDvdBulkResponse.setTxnBatchId("42");
        ArrayList<TxnIDBaseResponse> txnDetailsList = new ArrayList<>();
        actualDvdBulkResponse.setTxnDetailsList(txnDetailsList);
        String actualToStringResult = actualDvdBulkResponse.toString();
        assertEquals("File Attachment", actualDvdBulkResponse.getFileAttachment());
        assertEquals("foo.txt", actualDvdBulkResponse.getFileName());
        assertEquals("42", actualDvdBulkResponse.getTxnBatchId());
        assertSame(txnDetailsList, actualDvdBulkResponse.getTxnDetailsList());
        assertEquals("DvdBulkResponse [fileName=foo.txt, fileAttachment=File Attachment, txnBatchId=42, txnDetailsList=[]]",
                actualToStringResult);
    }
}

