package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkUserUploadRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkUserUploadRequestVO}
     *   <li>{@link BulkUserUploadRequestVO#setBatchName(String)}
     *   <li>{@link BulkUserUploadRequestVO#setDomainCode(String)}
     *   <li>{@link BulkUserUploadRequestVO#setFile(String)}
     *   <li>{@link BulkUserUploadRequestVO#setFileName(String)}
     *   <li>{@link BulkUserUploadRequestVO#setFileType(String)}
     *   <li>{@link BulkUserUploadRequestVO#setGeographyCode(String)}
     *   <li>{@link BulkUserUploadRequestVO#getBatchName()}
     *   <li>{@link BulkUserUploadRequestVO#getDomainCode()}
     *   <li>{@link BulkUserUploadRequestVO#getFile()}
     *   <li>{@link BulkUserUploadRequestVO#getFileName()}
     *   <li>{@link BulkUserUploadRequestVO#getFileType()}
     *   <li>{@link BulkUserUploadRequestVO#getGeographyCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkUserUploadRequestVO actualBulkUserUploadRequestVO = new BulkUserUploadRequestVO();
        actualBulkUserUploadRequestVO.setBatchName("Batch Name");
        actualBulkUserUploadRequestVO.setDomainCode("Domain Code");
        actualBulkUserUploadRequestVO.setFile("File");
        actualBulkUserUploadRequestVO.setFileName("foo.txt");
        actualBulkUserUploadRequestVO.setFileType("File Type");
        actualBulkUserUploadRequestVO.setGeographyCode("Geography Code");
        assertEquals("Batch Name", actualBulkUserUploadRequestVO.getBatchName());
        assertEquals("Domain Code", actualBulkUserUploadRequestVO.getDomainCode());
        assertEquals("File", actualBulkUserUploadRequestVO.getFile());
        assertEquals("foo.txt", actualBulkUserUploadRequestVO.getFileName());
        assertEquals("File Type", actualBulkUserUploadRequestVO.getFileType());
        assertEquals("Geography Code", actualBulkUserUploadRequestVO.getGeographyCode());
    }
}

