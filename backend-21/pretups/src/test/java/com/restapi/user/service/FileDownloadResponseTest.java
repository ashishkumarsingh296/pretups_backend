package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileDownloadResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FileDownloadResponse}
     *   <li>{@link FileDownloadResponse#setFileName(String)}
     *   <li>{@link FileDownloadResponse#setFileType(String)}
     *   <li>{@link FileDownloadResponse#setFileattachment(String)}
     *   <li>{@link FileDownloadResponse#getFileName()}
     *   <li>{@link FileDownloadResponse#getFileType()}
     *   <li>{@link FileDownloadResponse#getFileattachment()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FileDownloadResponse actualFileDownloadResponse = new FileDownloadResponse();
        actualFileDownloadResponse.setFileName("foo.txt");
        actualFileDownloadResponse.setFileType("File Type");
        actualFileDownloadResponse.setFileattachment("Fileattachment");
        assertEquals("foo.txt", actualFileDownloadResponse.getFileName());
        assertEquals("File Type", actualFileDownloadResponse.getFileType());
        assertEquals("Fileattachment", actualFileDownloadResponse.getFileattachment());
    }
}

