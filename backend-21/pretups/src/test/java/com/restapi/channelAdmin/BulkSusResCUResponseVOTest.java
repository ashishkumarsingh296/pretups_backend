package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkSusResCUResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkSusResCUResponseVO}
     *   <li>{@link BulkSusResCUResponseVO#setFileAttachment(String)}
     *   <li>{@link BulkSusResCUResponseVO#setFileName(String)}
     *   <li>{@link BulkSusResCUResponseVO#toString()}
     *   <li>{@link BulkSusResCUResponseVO#getFileAttachment()}
     *   <li>{@link BulkSusResCUResponseVO#getFileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkSusResCUResponseVO actualBulkSusResCUResponseVO = new BulkSusResCUResponseVO();
        actualBulkSusResCUResponseVO.setFileAttachment("File Attachment");
        actualBulkSusResCUResponseVO.setFileName("foo.txt");
        String actualToStringResult = actualBulkSusResCUResponseVO.toString();
        assertEquals("File Attachment", actualBulkSusResCUResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualBulkSusResCUResponseVO.getFileName());
        assertEquals("bulkSusResCUResponseVO [fileName=foo.txt, fileAttachment=File Attachment]", actualToStringResult);
    }
}

