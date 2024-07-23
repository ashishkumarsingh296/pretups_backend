package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class BatchUploadAndProcessAssosiateAlertResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchUploadAndProcessAssosiateAlertResponseVO}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setErrorFlag(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setErrorList(ArrayList)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setFileAttachment(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setFileName(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setFileType(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setMessage(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setMessageCode(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setNoOfRecords(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setStatus(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setTotalRecords(int)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#setValidRecords(int)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#toString()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getErrorFlag()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getErrorList()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getErrorMap()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getFileAttachment()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getFileName()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getFileType()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getMessage()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getMessageCode()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getNoOfRecords()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getStatus()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getTotalRecords()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertResponseVO#getValidRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchUploadAndProcessAssosiateAlertResponseVO actualBatchUploadAndProcessAssosiateAlertResponseVO = new BatchUploadAndProcessAssosiateAlertResponseVO();
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setErrorFlag("An error occurred");
        ArrayList errorList = new ArrayList();
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setErrorList(errorList);
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setErrorMap(errorMap);
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setFileAttachment("File Attachment");
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setFileName("foo.txt");
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setFileType("File Type");
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setMessage("Not all who wander are lost");
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setMessageCode("Message Code");
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setNoOfRecords("No Of Records");
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setStatus("Status");
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setTotalRecords(1);
        actualBatchUploadAndProcessAssosiateAlertResponseVO.setValidRecords(1);
        actualBatchUploadAndProcessAssosiateAlertResponseVO.toString();
        assertEquals("An error occurred", actualBatchUploadAndProcessAssosiateAlertResponseVO.getErrorFlag());
        assertSame(errorList, actualBatchUploadAndProcessAssosiateAlertResponseVO.getErrorList());
        assertSame(errorMap, actualBatchUploadAndProcessAssosiateAlertResponseVO.getErrorMap());
        assertEquals("File Attachment", actualBatchUploadAndProcessAssosiateAlertResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualBatchUploadAndProcessAssosiateAlertResponseVO.getFileName());
        assertEquals("File Type", actualBatchUploadAndProcessAssosiateAlertResponseVO.getFileType());
        assertEquals("Not all who wander are lost", actualBatchUploadAndProcessAssosiateAlertResponseVO.getMessage());
        assertEquals("Message Code", actualBatchUploadAndProcessAssosiateAlertResponseVO.getMessageCode());
        assertEquals("No Of Records", actualBatchUploadAndProcessAssosiateAlertResponseVO.getNoOfRecords());
        assertEquals("Status", actualBatchUploadAndProcessAssosiateAlertResponseVO.getStatus());
        assertEquals(1, actualBatchUploadAndProcessAssosiateAlertResponseVO.getTotalRecords());
        assertEquals(1, actualBatchUploadAndProcessAssosiateAlertResponseVO.getValidRecords());
    }
}

