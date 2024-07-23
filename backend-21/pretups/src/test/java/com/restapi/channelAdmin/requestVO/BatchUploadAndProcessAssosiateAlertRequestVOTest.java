package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BatchUploadAndProcessAssosiateAlertRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchUploadAndProcessAssosiateAlertRequestVO}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertRequestVO#setFileAttachment(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertRequestVO#setFileName(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertRequestVO#setFileType(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertRequestVO#toString()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertRequestVO#getFileAttachment()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertRequestVO#getFileName()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchUploadAndProcessAssosiateAlertRequestVO actualBatchUploadAndProcessAssosiateAlertRequestVO = new BatchUploadAndProcessAssosiateAlertRequestVO();
        actualBatchUploadAndProcessAssosiateAlertRequestVO.setFileAttachment("File Attachment");
        actualBatchUploadAndProcessAssosiateAlertRequestVO.setFileName("foo.txt");
        actualBatchUploadAndProcessAssosiateAlertRequestVO.setFileType("File Type");
        String actualToStringResult = actualBatchUploadAndProcessAssosiateAlertRequestVO.toString();
        assertEquals("File Attachment", actualBatchUploadAndProcessAssosiateAlertRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualBatchUploadAndProcessAssosiateAlertRequestVO.getFileName());
        assertEquals("File Type", actualBatchUploadAndProcessAssosiateAlertRequestVO.getFileType());
        assertEquals("BulkModifyUserRequest [fileType=File Type, fileName=foo.txt, fileAttachment=File Attachment]",
                actualToStringResult);
    }
}

