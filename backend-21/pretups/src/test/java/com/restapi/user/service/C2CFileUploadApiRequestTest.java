package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2CFileUploadApiRequestTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CFileUploadApiRequest}
     *   <li>{@link C2CFileUploadApiRequest#setBatchName(String)}
     *   <li>{@link C2CFileUploadApiRequest#setFileAttachment(String)}
     *   <li>{@link C2CFileUploadApiRequest#setFileName(String)}
     *   <li>{@link C2CFileUploadApiRequest#setFileType(String)}
     *   <li>{@link C2CFileUploadApiRequest#setLanguage1(String)}
     *   <li>{@link C2CFileUploadApiRequest#setLanguage2(String)}
     *   <li>{@link C2CFileUploadApiRequest#setPin(String)}
     *   <li>{@link C2CFileUploadApiRequest#toString()}
     *   <li>{@link C2CFileUploadApiRequest#getBatchName()}
     *   <li>{@link C2CFileUploadApiRequest#getFileAttachment()}
     *   <li>{@link C2CFileUploadApiRequest#getFileName()}
     *   <li>{@link C2CFileUploadApiRequest#getFileType()}
     *   <li>{@link C2CFileUploadApiRequest#getLanguage1()}
     *   <li>{@link C2CFileUploadApiRequest#getLanguage2()}
     *   <li>{@link C2CFileUploadApiRequest#getPin()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CFileUploadApiRequest actualC2cFileUploadApiRequest = new C2CFileUploadApiRequest();
        actualC2cFileUploadApiRequest.setBatchName("Batch Name");
        actualC2cFileUploadApiRequest.setFileAttachment("File Attachment");
        actualC2cFileUploadApiRequest.setFileName("foo.txt");
        actualC2cFileUploadApiRequest.setFileType("File Type");
        actualC2cFileUploadApiRequest.setLanguage1("en");
        actualC2cFileUploadApiRequest.setLanguage2("en");
        actualC2cFileUploadApiRequest.setPin("Pin");
        String actualToStringResult = actualC2cFileUploadApiRequest.toString();
        assertEquals("Batch Name", actualC2cFileUploadApiRequest.getBatchName());
        assertEquals("File Attachment", actualC2cFileUploadApiRequest.getFileAttachment());
        assertEquals("foo.txt", actualC2cFileUploadApiRequest.getFileName());
        assertEquals("File Type", actualC2cFileUploadApiRequest.getFileType());
        assertEquals("en", actualC2cFileUploadApiRequest.getLanguage1());
        assertEquals("en", actualC2cFileUploadApiRequest.getLanguage2());
        assertEquals("Pin", actualC2cFileUploadApiRequest.getPin());
        assertEquals("C2CFileUploadApiRequest [fileType=File Type, fileName=foo.txt, fileAttachment=File Attachment]",
                actualToStringResult);
    }
}

