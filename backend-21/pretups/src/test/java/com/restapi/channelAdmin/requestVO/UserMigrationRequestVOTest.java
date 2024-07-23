package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserMigrationRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserMigrationRequestVO}
     *   <li>{@link UserMigrationRequestVO#setFileAttachment(String)}
     *   <li>{@link UserMigrationRequestVO#setFileName(String)}
     *   <li>{@link UserMigrationRequestVO#setFileType(String)}
     *   <li>{@link UserMigrationRequestVO#getFileAttachment()}
     *   <li>{@link UserMigrationRequestVO#getFileName()}
     *   <li>{@link UserMigrationRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserMigrationRequestVO actualUserMigrationRequestVO = new UserMigrationRequestVO();
        actualUserMigrationRequestVO.setFileAttachment("File Attachment");
        actualUserMigrationRequestVO.setFileName("foo.txt");
        actualUserMigrationRequestVO.setFileType("File Type");
        assertEquals("File Attachment", actualUserMigrationRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualUserMigrationRequestVO.getFileName());
        assertEquals("File Type", actualUserMigrationRequestVO.getFileType());
    }
}

