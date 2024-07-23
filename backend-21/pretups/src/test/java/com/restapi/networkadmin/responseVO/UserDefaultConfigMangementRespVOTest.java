package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserDefaultConfigMangementRespVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserDefaultConfigMangementRespVO}
     *   <li>{@link UserDefaultConfigMangementRespVO#setFileAttachment(String)}
     *   <li>{@link UserDefaultConfigMangementRespVO#setFileName(String)}
     *   <li>{@link UserDefaultConfigMangementRespVO#setFileType(String)}
     *   <li>{@link UserDefaultConfigMangementRespVO#getFileAttachment()}
     *   <li>{@link UserDefaultConfigMangementRespVO#getFileName()}
     *   <li>{@link UserDefaultConfigMangementRespVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserDefaultConfigMangementRespVO actualUserDefaultConfigMangementRespVO = new UserDefaultConfigMangementRespVO();
        actualUserDefaultConfigMangementRespVO.setFileAttachment("File Attachment");
        actualUserDefaultConfigMangementRespVO.setFileName("foo.txt");
        actualUserDefaultConfigMangementRespVO.setFileType("File Type");
        assertEquals("File Attachment", actualUserDefaultConfigMangementRespVO.getFileAttachment());
        assertEquals("foo.txt", actualUserDefaultConfigMangementRespVO.getFileName());
        assertEquals("File Type", actualUserDefaultConfigMangementRespVO.getFileType());
    }
}

