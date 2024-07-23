package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserMigrationResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserMigrationResponseVO}
     *   <li>{@link UserMigrationResponseVO#setFileAttachment(String)}
     *   <li>{@link UserMigrationResponseVO#setFileName(String)}
     *   <li>{@link UserMigrationResponseVO#getFileAttachment()}
     *   <li>{@link UserMigrationResponseVO#getFileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserMigrationResponseVO actualUserMigrationResponseVO = new UserMigrationResponseVO();
        actualUserMigrationResponseVO.setFileAttachment("File Attachment");
        actualUserMigrationResponseVO.setFileName("foo.txt");
        assertEquals("File Attachment", actualUserMigrationResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualUserMigrationResponseVO.getFileName());
    }
}

