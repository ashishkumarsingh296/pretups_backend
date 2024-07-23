package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2CFileUploadApiResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CFileUploadApiResponse}
     *   <li>{@link C2CFileUploadApiResponse#setBatchID(String)}
     *   <li>{@link C2CFileUploadApiResponse#setFileAttachment(String)}
     *   <li>{@link C2CFileUploadApiResponse#setFileName(String)}
     *   <li>{@link C2CFileUploadApiResponse#setFileValidationErrorList(ArrayList)}
     *   <li>{@link C2CFileUploadApiResponse#toString()}
     *   <li>{@link C2CFileUploadApiResponse#getBatchID()}
     *   <li>{@link C2CFileUploadApiResponse#getFileAttachment()}
     *   <li>{@link C2CFileUploadApiResponse#getFileName()}
     *   <li>{@link C2CFileUploadApiResponse#getFileValidationErrorList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CFileUploadApiResponse actualC2cFileUploadApiResponse = new C2CFileUploadApiResponse();
        actualC2cFileUploadApiResponse.setBatchID("Batch ID");
        actualC2cFileUploadApiResponse.setFileAttachment("File Attachment");
        actualC2cFileUploadApiResponse.setFileName("foo.txt");
        ArrayList<String> fileValidationErrorList = new ArrayList<>();
        actualC2cFileUploadApiResponse.setFileValidationErrorList(fileValidationErrorList);
        String actualToStringResult = actualC2cFileUploadApiResponse.toString();
        assertEquals("Batch ID", actualC2cFileUploadApiResponse.getBatchID());
        assertEquals("File Attachment", actualC2cFileUploadApiResponse.getFileAttachment());
        assertEquals("foo.txt", actualC2cFileUploadApiResponse.getFileName());
        assertSame(fileValidationErrorList, actualC2cFileUploadApiResponse.getFileValidationErrorList());
        assertEquals("C2CFileUploadApiResponse [ fileValidationErrorList=[]]", actualToStringResult);
    }
}

