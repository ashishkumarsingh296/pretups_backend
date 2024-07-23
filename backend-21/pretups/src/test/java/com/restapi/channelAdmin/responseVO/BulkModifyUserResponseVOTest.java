package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class BulkModifyUserResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkModifyUserResponseVO}
     *   <li>{@link BulkModifyUserResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link BulkModifyUserResponseVO#setFileAttachment(String)}
     *   <li>{@link BulkModifyUserResponseVO#setFileName(String)}
     *   <li>{@link BulkModifyUserResponseVO#setMessage(String)}
     *   <li>{@link BulkModifyUserResponseVO#setMessageCode(String)}
     *   <li>{@link BulkModifyUserResponseVO#setStatus(String)}
     *   <li>{@link BulkModifyUserResponseVO#setTotalRecords(int)}
     *   <li>{@link BulkModifyUserResponseVO#setValidRecords(int)}
     *   <li>{@link BulkModifyUserResponseVO#toString()}
     *   <li>{@link BulkModifyUserResponseVO#getErrorMap()}
     *   <li>{@link BulkModifyUserResponseVO#getFileAttachment()}
     *   <li>{@link BulkModifyUserResponseVO#getFileName()}
     *   <li>{@link BulkModifyUserResponseVO#getMessage()}
     *   <li>{@link BulkModifyUserResponseVO#getMessageCode()}
     *   <li>{@link BulkModifyUserResponseVO#getStatus()}
     *   <li>{@link BulkModifyUserResponseVO#getTotalRecords()}
     *   <li>{@link BulkModifyUserResponseVO#getValidRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkModifyUserResponseVO actualBulkModifyUserResponseVO = new BulkModifyUserResponseVO();
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualBulkModifyUserResponseVO.setErrorMap(errorMap);
        actualBulkModifyUserResponseVO.setFileAttachment("File Attachment");
        actualBulkModifyUserResponseVO.setFileName("foo.txt");
        actualBulkModifyUserResponseVO.setMessage("Not all who wander are lost");
        actualBulkModifyUserResponseVO.setMessageCode("Message Code");
        actualBulkModifyUserResponseVO.setStatus("Status");
        actualBulkModifyUserResponseVO.setTotalRecords(1);
        actualBulkModifyUserResponseVO.setValidRecords(1);
        actualBulkModifyUserResponseVO.toString();
        assertSame(errorMap, actualBulkModifyUserResponseVO.getErrorMap());
        assertEquals("File Attachment", actualBulkModifyUserResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualBulkModifyUserResponseVO.getFileName());
        assertEquals("Not all who wander are lost", actualBulkModifyUserResponseVO.getMessage());
        assertEquals("Message Code", actualBulkModifyUserResponseVO.getMessageCode());
        assertEquals("Status", actualBulkModifyUserResponseVO.getStatus());
        assertEquals(1, actualBulkModifyUserResponseVO.getTotalRecords());
        assertEquals(1, actualBulkModifyUserResponseVO.getValidRecords());
    }
}

