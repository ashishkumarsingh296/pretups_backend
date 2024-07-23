package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkModifyUserRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkModifyUserRequestVO}
     *   <li>{@link BulkModifyUserRequestVO#setFileAttachment(String)}
     *   <li>{@link BulkModifyUserRequestVO#setFileName(String)}
     *   <li>{@link BulkModifyUserRequestVO#setFileType(String)}
     *   <li>{@link BulkModifyUserRequestVO#toString()}
     *   <li>{@link BulkModifyUserRequestVO#getFileAttachment()}
     *   <li>{@link BulkModifyUserRequestVO#getFileName()}
     *   <li>{@link BulkModifyUserRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkModifyUserRequestVO actualBulkModifyUserRequestVO = new BulkModifyUserRequestVO();
        actualBulkModifyUserRequestVO.setFileAttachment("File Attachment");
        actualBulkModifyUserRequestVO.setFileName("foo.txt");
        actualBulkModifyUserRequestVO.setFileType("File Type");
        String actualToStringResult = actualBulkModifyUserRequestVO.toString();
        assertEquals("File Attachment", actualBulkModifyUserRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualBulkModifyUserRequestVO.getFileName());
        assertEquals("File Type", actualBulkModifyUserRequestVO.getFileType());
        assertEquals("BulkModifyUserRequest [fileType=File Type, fileName=foo.txt, fileAttachment=File Attachment]",
                actualToStringResult);
    }
}

