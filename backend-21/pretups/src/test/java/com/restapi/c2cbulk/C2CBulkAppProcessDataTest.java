package com.restapi.c2cbulk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2CBulkAppProcessDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CBulkAppProcessData}
     *   <li>{@link C2CBulkAppProcessData#setBatchId(String)}
     *   <li>{@link C2CBulkAppProcessData#setFile(String)}
     *   <li>{@link C2CBulkAppProcessData#setFileName(String)}
     *   <li>{@link C2CBulkAppProcessData#setFileType(String)}
     *   <li>{@link C2CBulkAppProcessData#setLanguage1(String)}
     *   <li>{@link C2CBulkAppProcessData#setLanguage2(String)}
     *   <li>{@link C2CBulkAppProcessData#getBatchId()}
     *   <li>{@link C2CBulkAppProcessData#getFile()}
     *   <li>{@link C2CBulkAppProcessData#getFileName()}
     *   <li>{@link C2CBulkAppProcessData#getFileType()}
     *   <li>{@link C2CBulkAppProcessData#getLanguage1()}
     *   <li>{@link C2CBulkAppProcessData#getLanguage2()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CBulkAppProcessData actualC2cBulkAppProcessData = new C2CBulkAppProcessData();
        actualC2cBulkAppProcessData.setBatchId("42");
        actualC2cBulkAppProcessData.setFile("File");
        actualC2cBulkAppProcessData.setFileName("foo.txt");
        actualC2cBulkAppProcessData.setFileType("File Type");
        actualC2cBulkAppProcessData.setLanguage1("en");
        actualC2cBulkAppProcessData.setLanguage2("en");
        assertEquals("42", actualC2cBulkAppProcessData.getBatchId());
        assertEquals("File", actualC2cBulkAppProcessData.getFile());
        assertEquals("foo.txt", actualC2cBulkAppProcessData.getFileName());
        assertEquals("File Type", actualC2cBulkAppProcessData.getFileType());
        assertEquals("en", actualC2cBulkAppProcessData.getLanguage1());
        assertEquals("en", actualC2cBulkAppProcessData.getLanguage2());
    }
}

