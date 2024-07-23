package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class BulkUserUploadResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkUserUploadResponseVO}
     *   <li>{@link BulkUserUploadResponseVO#setErrorFlag(String)}
     *   <li>{@link BulkUserUploadResponseVO#setErrorList(ArrayList)}
     *   <li>{@link BulkUserUploadResponseVO#setFileName(String)}
     *   <li>{@link BulkUserUploadResponseVO#setFileType(String)}
     *   <li>{@link BulkUserUploadResponseVO#setFileattachment(String)}
     *   <li>{@link BulkUserUploadResponseVO#setNoOfRecords(String)}
     *   <li>{@link BulkUserUploadResponseVO#setTotalRecords(int)}
     *   <li>{@link BulkUserUploadResponseVO#getErrorFlag()}
     *   <li>{@link BulkUserUploadResponseVO#getErrorList()}
     *   <li>{@link BulkUserUploadResponseVO#getFileName()}
     *   <li>{@link BulkUserUploadResponseVO#getFileType()}
     *   <li>{@link BulkUserUploadResponseVO#getFileattachment()}
     *   <li>{@link BulkUserUploadResponseVO#getNoOfRecords()}
     *   <li>{@link BulkUserUploadResponseVO#getTotalRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkUserUploadResponseVO actualBulkUserUploadResponseVO = new BulkUserUploadResponseVO();
        actualBulkUserUploadResponseVO.setErrorFlag("An error occurred");
        ArrayList errorList = new ArrayList();
        actualBulkUserUploadResponseVO.setErrorList(errorList);
        actualBulkUserUploadResponseVO.setFileName("foo.txt");
        actualBulkUserUploadResponseVO.setFileType("File Type");
        actualBulkUserUploadResponseVO.setFileattachment("Fileattachment");
        actualBulkUserUploadResponseVO.setNoOfRecords("No Of Records");
        actualBulkUserUploadResponseVO.setTotalRecords(1);
        assertEquals("An error occurred", actualBulkUserUploadResponseVO.getErrorFlag());
        assertSame(errorList, actualBulkUserUploadResponseVO.getErrorList());
        assertEquals("foo.txt", actualBulkUserUploadResponseVO.getFileName());
        assertEquals("File Type", actualBulkUserUploadResponseVO.getFileType());
        assertEquals("Fileattachment", actualBulkUserUploadResponseVO.getFileattachment());
        assertEquals("No Of Records", actualBulkUserUploadResponseVO.getNoOfRecords());
        assertEquals(1, actualBulkUserUploadResponseVO.getTotalRecords());
    }
}

