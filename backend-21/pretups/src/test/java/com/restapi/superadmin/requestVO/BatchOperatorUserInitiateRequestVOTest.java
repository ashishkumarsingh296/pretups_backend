package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BatchOperatorUserInitiateRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchOperatorUserInitiateRequestVO}
     *   <li>{@link BatchOperatorUserInitiateRequestVO#setFileAttachment(String)}
     *   <li>{@link BatchOperatorUserInitiateRequestVO#setFileName(String)}
     *   <li>{@link BatchOperatorUserInitiateRequestVO#setFileType(String)}
     *   <li>{@link BatchOperatorUserInitiateRequestVO#toString()}
     *   <li>{@link BatchOperatorUserInitiateRequestVO#getFileAttachment()}
     *   <li>{@link BatchOperatorUserInitiateRequestVO#getFileName()}
     *   <li>{@link BatchOperatorUserInitiateRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchOperatorUserInitiateRequestVO actualBatchOperatorUserInitiateRequestVO = new BatchOperatorUserInitiateRequestVO();
        actualBatchOperatorUserInitiateRequestVO.setFileAttachment("File Attachment");
        actualBatchOperatorUserInitiateRequestVO.setFileName("foo.txt");
        actualBatchOperatorUserInitiateRequestVO.setFileType("File Type");
        String actualToStringResult = actualBatchOperatorUserInitiateRequestVO.toString();
        assertEquals("File Attachment", actualBatchOperatorUserInitiateRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualBatchOperatorUserInitiateRequestVO.getFileName());
        assertEquals("File Type", actualBatchOperatorUserInitiateRequestVO.getFileType());
        assertEquals("BatchOperatorUserInitiateRequestVO [fileType=File Type, fileName=foo.txt, fileAttachment=File"
                + " Attachment]", actualToStringResult);
    }
}

