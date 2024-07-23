package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class BatchAddCommisionProfileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchAddCommisionProfileResponseVO}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setErrorFlag(String)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setErrorList(ArrayList)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setFileAttachment(String)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setFileType(String)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setSheetName(String)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setTotalRecords(int)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#setValidRecords(int)}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getErrorFlag()}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getErrorList()}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getErrorMap()}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getFileAttachment()}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getFileType()}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getSheetName()}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getTotalRecords()}
     *   <li>{@link BatchAddCommisionProfileResponseVO#getValidRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchAddCommisionProfileResponseVO actualBatchAddCommisionProfileResponseVO = new BatchAddCommisionProfileResponseVO();
        actualBatchAddCommisionProfileResponseVO.setErrorFlag("An error occurred");
        ArrayList errorList = new ArrayList();
        actualBatchAddCommisionProfileResponseVO.setErrorList(errorList);
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualBatchAddCommisionProfileResponseVO.setErrorMap(errorMap);
        actualBatchAddCommisionProfileResponseVO.setFileAttachment("File Attachment");
        actualBatchAddCommisionProfileResponseVO.setFileType("File Type");
        actualBatchAddCommisionProfileResponseVO.setSheetName("Sheet Name");
        actualBatchAddCommisionProfileResponseVO.setTotalRecords(1);
        actualBatchAddCommisionProfileResponseVO.setValidRecords(1);
        assertEquals("An error occurred", actualBatchAddCommisionProfileResponseVO.getErrorFlag());
        assertSame(errorList, actualBatchAddCommisionProfileResponseVO.getErrorList());
        assertSame(errorMap, actualBatchAddCommisionProfileResponseVO.getErrorMap());
        assertEquals("File Attachment", actualBatchAddCommisionProfileResponseVO.getFileAttachment());
        assertEquals("File Type", actualBatchAddCommisionProfileResponseVO.getFileType());
        assertEquals("Sheet Name", actualBatchAddCommisionProfileResponseVO.getSheetName());
        assertEquals(1, actualBatchAddCommisionProfileResponseVO.getTotalRecords());
        assertEquals(1, actualBatchAddCommisionProfileResponseVO.getValidRecords());
    }
}

