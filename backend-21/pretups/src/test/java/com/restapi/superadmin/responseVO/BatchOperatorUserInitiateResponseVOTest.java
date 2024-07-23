package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class BatchOperatorUserInitiateResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchOperatorUserInitiateResponseVO}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setBatchID(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setErrorList(ArrayList)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setFileAttachment(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setFileName(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setFileType(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setMessage(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setMessageCode(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setNoOfRecords(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setStatus(String)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setTotalRecords(int)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#setValidRecords(int)}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#toString()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getBatchID()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getErrorList()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getErrorMap()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getFileAttachment()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getFileName()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getFileType()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getMessage()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getMessageCode()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getNoOfRecords()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getStatus()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getTotalRecords()}
     *   <li>{@link BatchOperatorUserInitiateResponseVO#getValidRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchOperatorUserInitiateResponseVO actualBatchOperatorUserInitiateResponseVO = new BatchOperatorUserInitiateResponseVO();
        actualBatchOperatorUserInitiateResponseVO.setBatchID("Batch ID");
        ArrayList errorList = new ArrayList();
        actualBatchOperatorUserInitiateResponseVO.setErrorList(errorList);
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualBatchOperatorUserInitiateResponseVO.setErrorMap(errorMap);
        actualBatchOperatorUserInitiateResponseVO.setFileAttachment("File Attachment");
        actualBatchOperatorUserInitiateResponseVO.setFileName("foo.txt");
        actualBatchOperatorUserInitiateResponseVO.setFileType("File Type");
        actualBatchOperatorUserInitiateResponseVO.setMessage("Not all who wander are lost");
        actualBatchOperatorUserInitiateResponseVO.setMessageCode("Message Code");
        actualBatchOperatorUserInitiateResponseVO.setNoOfRecords("No Of Records");
        actualBatchOperatorUserInitiateResponseVO.setStatus("Status");
        actualBatchOperatorUserInitiateResponseVO.setTotalRecords(1);
        actualBatchOperatorUserInitiateResponseVO.setValidRecords(1);
        actualBatchOperatorUserInitiateResponseVO.toString();
        assertEquals("Batch ID", actualBatchOperatorUserInitiateResponseVO.getBatchID());
        assertSame(errorList, actualBatchOperatorUserInitiateResponseVO.getErrorList());
        assertSame(errorMap, actualBatchOperatorUserInitiateResponseVO.getErrorMap());
        assertEquals("File Attachment", actualBatchOperatorUserInitiateResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualBatchOperatorUserInitiateResponseVO.getFileName());
        assertEquals("File Type", actualBatchOperatorUserInitiateResponseVO.getFileType());
        assertEquals("Not all who wander are lost", actualBatchOperatorUserInitiateResponseVO.getMessage());
        assertEquals("Message Code", actualBatchOperatorUserInitiateResponseVO.getMessageCode());
        assertEquals("No Of Records", actualBatchOperatorUserInitiateResponseVO.getNoOfRecords());
        assertEquals("Status", actualBatchOperatorUserInitiateResponseVO.getStatus());
        assertEquals(1, actualBatchOperatorUserInitiateResponseVO.getTotalRecords());
        assertEquals(1, actualBatchOperatorUserInitiateResponseVO.getValidRecords());
    }
}

