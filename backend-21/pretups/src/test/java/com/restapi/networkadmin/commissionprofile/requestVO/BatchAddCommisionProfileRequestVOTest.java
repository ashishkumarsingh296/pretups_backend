package com.restapi.networkadmin.commissionprofile.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BatchAddCommisionProfileRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchAddCommisionProfileRequestVO}
     *   <li>{@link BatchAddCommisionProfileRequestVO#setFileAttachment(String)}
     *   <li>{@link BatchAddCommisionProfileRequestVO#setFileName(String)}
     *   <li>{@link BatchAddCommisionProfileRequestVO#setFileType(String)}
     *   <li>{@link BatchAddCommisionProfileRequestVO#getFileAttachment()}
     *   <li>{@link BatchAddCommisionProfileRequestVO#getFileName()}
     *   <li>{@link BatchAddCommisionProfileRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchAddCommisionProfileRequestVO actualBatchAddCommisionProfileRequestVO = new BatchAddCommisionProfileRequestVO();
        actualBatchAddCommisionProfileRequestVO.setFileAttachment("File Attachment");
        actualBatchAddCommisionProfileRequestVO.setFileName("foo.txt");
        actualBatchAddCommisionProfileRequestVO.setFileType("File Type");
        assertEquals("File Attachment", actualBatchAddCommisionProfileRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualBatchAddCommisionProfileRequestVO.getFileName());
        assertEquals("File Type", actualBatchAddCommisionProfileRequestVO.getFileType());
    }
}

