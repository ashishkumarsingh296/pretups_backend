package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkBarredRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkBarredRequestVO}
     *   <li>{@link BulkBarredRequestVO#setBarringReason(String)}
     *   <li>{@link BulkBarredRequestVO#setBarringType(String)}
     *   <li>{@link BulkBarredRequestVO#setBarringTypeName(String)}
     *   <li>{@link BulkBarredRequestVO#setFile(String)}
     *   <li>{@link BulkBarredRequestVO#setFileName(String)}
     *   <li>{@link BulkBarredRequestVO#setFileType(String)}
     *   <li>{@link BulkBarredRequestVO#setModule(String)}
     *   <li>{@link BulkBarredRequestVO#setUserType(String)}
     *   <li>{@link BulkBarredRequestVO#toString()}
     *   <li>{@link BulkBarredRequestVO#getBarringReason()}
     *   <li>{@link BulkBarredRequestVO#getBarringType()}
     *   <li>{@link BulkBarredRequestVO#getBarringTypeName()}
     *   <li>{@link BulkBarredRequestVO#getFile()}
     *   <li>{@link BulkBarredRequestVO#getFileName()}
     *   <li>{@link BulkBarredRequestVO#getFileType()}
     *   <li>{@link BulkBarredRequestVO#getModule()}
     *   <li>{@link BulkBarredRequestVO#getUserType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkBarredRequestVO actualBulkBarredRequestVO = new BulkBarredRequestVO();
        actualBulkBarredRequestVO.setBarringReason("Just cause");
        actualBulkBarredRequestVO.setBarringType("Barring Type");
        actualBulkBarredRequestVO.setBarringTypeName("Barring Type Name");
        actualBulkBarredRequestVO.setFile("File");
        actualBulkBarredRequestVO.setFileName("foo.txt");
        actualBulkBarredRequestVO.setFileType("File Type");
        actualBulkBarredRequestVO.setModule("Module");
        actualBulkBarredRequestVO.setUserType("User Type");
        String actualToStringResult = actualBulkBarredRequestVO.toString();
        assertEquals("Just cause", actualBulkBarredRequestVO.getBarringReason());
        assertEquals("Barring Type", actualBulkBarredRequestVO.getBarringType());
        assertEquals("Barring Type Name", actualBulkBarredRequestVO.getBarringTypeName());
        assertEquals("File", actualBulkBarredRequestVO.getFile());
        assertEquals("foo.txt", actualBulkBarredRequestVO.getFileName());
        assertEquals("File Type", actualBulkBarredRequestVO.getFileType());
        assertEquals("Module", actualBulkBarredRequestVO.getModule());
        assertEquals("User Type", actualBulkBarredRequestVO.getUserType());
        assertEquals(
                "BulkBarredRequestVO [file=File, fileName=foo.txt, userType=User Type, fileType=File Type, barringType=Barring"
                        + " Type, barringTypeName=Barring Type Name, barringReason=Just cause, module=Module]",
                actualToStringResult);
    }
}

