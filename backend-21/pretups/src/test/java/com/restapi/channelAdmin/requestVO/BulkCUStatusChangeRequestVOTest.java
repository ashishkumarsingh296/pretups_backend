package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkCUStatusChangeRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkCUStatusChangeRequestVO}
     *   <li>{@link BulkCUStatusChangeRequestVO#setFile(String)}
     *   <li>{@link BulkCUStatusChangeRequestVO#setFileName(String)}
     *   <li>{@link BulkCUStatusChangeRequestVO#setFileType(String)}
     *   <li>{@link BulkCUStatusChangeRequestVO#toString()}
     *   <li>{@link BulkCUStatusChangeRequestVO#getFile()}
     *   <li>{@link BulkCUStatusChangeRequestVO#getFileName()}
     *   <li>{@link BulkCUStatusChangeRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkCUStatusChangeRequestVO actualBulkCUStatusChangeRequestVO = new BulkCUStatusChangeRequestVO();
        actualBulkCUStatusChangeRequestVO.setFile("File");
        actualBulkCUStatusChangeRequestVO.setFileName("foo.txt");
        actualBulkCUStatusChangeRequestVO.setFileType("File Type");
        String actualToStringResult = actualBulkCUStatusChangeRequestVO.toString();
        assertEquals("File", actualBulkCUStatusChangeRequestVO.getFile());
        assertEquals("foo.txt", actualBulkCUStatusChangeRequestVO.getFileName());
        assertEquals("File Type", actualBulkCUStatusChangeRequestVO.getFileType());
        assertEquals("BulkCUStatusChangeRequestVO [file=File, fileName=foo.txt, fileType=File Type]", actualToStringResult);
    }
}

