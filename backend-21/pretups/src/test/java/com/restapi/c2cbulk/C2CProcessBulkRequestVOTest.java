package com.restapi.c2cbulk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2CProcessBulkRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CProcessBulkRequestVO}
     *   <li>{@link C2CProcessBulkRequestVO#setBatchId(String)}
     *   <li>{@link C2CProcessBulkRequestVO#setFile(String)}
     *   <li>{@link C2CProcessBulkRequestVO#setFileName(String)}
     *   <li>{@link C2CProcessBulkRequestVO#setFileType(String)}
     *   <li>{@link C2CProcessBulkRequestVO#setLanguage1(String)}
     *   <li>{@link C2CProcessBulkRequestVO#setLanguage2(String)}
     *   <li>{@link C2CProcessBulkRequestVO#getBatchId()}
     *   <li>{@link C2CProcessBulkRequestVO#getFile()}
     *   <li>{@link C2CProcessBulkRequestVO#getFileName()}
     *   <li>{@link C2CProcessBulkRequestVO#getFileType()}
     *   <li>{@link C2CProcessBulkRequestVO#getLanguage1()}
     *   <li>{@link C2CProcessBulkRequestVO#getLanguage2()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CProcessBulkRequestVO actualC2cProcessBulkRequestVO = new C2CProcessBulkRequestVO();
        actualC2cProcessBulkRequestVO.setBatchId("42");
        actualC2cProcessBulkRequestVO.setFile("File");
        actualC2cProcessBulkRequestVO.setFileName("foo.txt");
        actualC2cProcessBulkRequestVO.setFileType("File Type");
        actualC2cProcessBulkRequestVO.setLanguage1("en");
        actualC2cProcessBulkRequestVO.setLanguage2("en");
        assertEquals("42", actualC2cProcessBulkRequestVO.getBatchId());
        assertEquals("File", actualC2cProcessBulkRequestVO.getFile());
        assertEquals("foo.txt", actualC2cProcessBulkRequestVO.getFileName());
        assertEquals("File Type", actualC2cProcessBulkRequestVO.getFileType());
        assertEquals("en", actualC2cProcessBulkRequestVO.getLanguage1());
        assertEquals("en", actualC2cProcessBulkRequestVO.getLanguage2());
    }
}

