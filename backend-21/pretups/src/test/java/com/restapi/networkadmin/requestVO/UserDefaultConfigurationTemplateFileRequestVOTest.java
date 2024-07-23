package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserDefaultConfigurationTemplateFileRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserDefaultConfigurationTemplateFileRequestVO}
     *   <li>{@link UserDefaultConfigurationTemplateFileRequestVO#setFileAttachment(String)}
     *   <li>{@link UserDefaultConfigurationTemplateFileRequestVO#setFileName(String)}
     *   <li>{@link UserDefaultConfigurationTemplateFileRequestVO#setFileType(String)}
     *   <li>{@link UserDefaultConfigurationTemplateFileRequestVO#toString()}
     *   <li>{@link UserDefaultConfigurationTemplateFileRequestVO#getFileAttachment()}
     *   <li>{@link UserDefaultConfigurationTemplateFileRequestVO#getFileName()}
     *   <li>{@link UserDefaultConfigurationTemplateFileRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserDefaultConfigurationTemplateFileRequestVO actualUserDefaultConfigurationTemplateFileRequestVO = new UserDefaultConfigurationTemplateFileRequestVO();
        actualUserDefaultConfigurationTemplateFileRequestVO.setFileAttachment("File Attachment");
        actualUserDefaultConfigurationTemplateFileRequestVO.setFileName("foo.txt");
        actualUserDefaultConfigurationTemplateFileRequestVO.setFileType("File Type");
        String actualToStringResult = actualUserDefaultConfigurationTemplateFileRequestVO.toString();
        assertEquals("File Attachment", actualUserDefaultConfigurationTemplateFileRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualUserDefaultConfigurationTemplateFileRequestVO.getFileName());
        assertEquals("File Type", actualUserDefaultConfigurationTemplateFileRequestVO.getFileType());
        assertEquals(
                "UserDefaultConfigurationTemplateFileRequestVO(fileType=File Type, fileName=foo.txt, fileAttachment=File"
                        + " Attachment)",
                actualToStringResult);
    }
}

