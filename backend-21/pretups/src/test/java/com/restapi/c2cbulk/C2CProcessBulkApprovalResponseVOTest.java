package com.restapi.c2cbulk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class C2CProcessBulkApprovalResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CProcessBulkApprovalResponseVO}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setFileAttachment(String)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setFileName(String)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setMessage(String)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setMessageCode(String)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setNumberOfRecords(long)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setScheduleBatchId(String)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#setStatus(String)}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#toString()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getErrorMap()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getFileAttachment()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getFileName()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getMessage()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getMessageCode()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getNumberOfRecords()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getScheduleBatchId()}
     *   <li>{@link C2CProcessBulkApprovalResponseVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CProcessBulkApprovalResponseVO actualC2cProcessBulkApprovalResponseVO = new C2CProcessBulkApprovalResponseVO();
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualC2cProcessBulkApprovalResponseVO.setErrorMap(errorMap);
        actualC2cProcessBulkApprovalResponseVO.setFileAttachment("File Attachment");
        actualC2cProcessBulkApprovalResponseVO.setFileName("foo.txt");
        actualC2cProcessBulkApprovalResponseVO.setMessage("Not all who wander are lost");
        actualC2cProcessBulkApprovalResponseVO.setMessageCode("Message Code");
        actualC2cProcessBulkApprovalResponseVO.setNumberOfRecords(1L);
        actualC2cProcessBulkApprovalResponseVO.setScheduleBatchId("42");
        actualC2cProcessBulkApprovalResponseVO.setStatus("Txnstatus");
        String actualToStringResult = actualC2cProcessBulkApprovalResponseVO.toString();
        assertSame(errorMap, actualC2cProcessBulkApprovalResponseVO.getErrorMap());
        assertEquals("File Attachment", actualC2cProcessBulkApprovalResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualC2cProcessBulkApprovalResponseVO.getFileName());
        assertEquals("Not all who wander are lost", actualC2cProcessBulkApprovalResponseVO.getMessage());
        assertEquals("Message Code", actualC2cProcessBulkApprovalResponseVO.getMessageCode());
        assertEquals(1L, actualC2cProcessBulkApprovalResponseVO.getNumberOfRecords());
        assertEquals("42", actualC2cProcessBulkApprovalResponseVO.getScheduleBatchId());
        assertEquals("Txnstatus", actualC2cProcessBulkApprovalResponseVO.getStatus());
        assertEquals("C2SBulkRechargeResponseVO [messageCode=Message Codemessage=Not all who wander are losterrorMap"
                + "=rowErrorMsgList = []masterErrorList[]scheduleBatchId=42txnstatus=Txnstatus", actualToStringResult);
    }
}

