package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class C2SBulkEvdRechargeResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SBulkEvdRechargeResponseVO}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setFileAttachment(String)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setFileName(String)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setMessage(String)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setMessageCode(String)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setNumberOfRecords(long)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setScheduleBatchId(String)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#setStatus(String)}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#toString()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getErrorMap()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getFileAttachment()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getFileName()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getMessage()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getMessageCode()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getNumberOfRecords()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getScheduleBatchId()}
     *   <li>{@link C2SBulkEvdRechargeResponseVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SBulkEvdRechargeResponseVO actualC2sBulkEvdRechargeResponseVO = new C2SBulkEvdRechargeResponseVO();
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualC2sBulkEvdRechargeResponseVO.setErrorMap(errorMap);
        actualC2sBulkEvdRechargeResponseVO.setFileAttachment("File Attachment");
        actualC2sBulkEvdRechargeResponseVO.setFileName("foo.txt");
        actualC2sBulkEvdRechargeResponseVO.setMessage("Not all who wander are lost");
        actualC2sBulkEvdRechargeResponseVO.setMessageCode("Message Code");
        actualC2sBulkEvdRechargeResponseVO.setNumberOfRecords(1L);
        actualC2sBulkEvdRechargeResponseVO.setScheduleBatchId("42");
        actualC2sBulkEvdRechargeResponseVO.setStatus("Txnstatus");
        String actualToStringResult = actualC2sBulkEvdRechargeResponseVO.toString();
        assertSame(errorMap, actualC2sBulkEvdRechargeResponseVO.getErrorMap());
        assertEquals("File Attachment", actualC2sBulkEvdRechargeResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualC2sBulkEvdRechargeResponseVO.getFileName());
        assertEquals("Not all who wander are lost", actualC2sBulkEvdRechargeResponseVO.getMessage());
        assertEquals("Message Code", actualC2sBulkEvdRechargeResponseVO.getMessageCode());
        assertEquals(1L, actualC2sBulkEvdRechargeResponseVO.getNumberOfRecords());
        assertEquals("42", actualC2sBulkEvdRechargeResponseVO.getScheduleBatchId());
        assertEquals("Txnstatus", actualC2sBulkEvdRechargeResponseVO.getStatus());
        assertEquals("C2SBulkRechargeResponseVO [messageCode=Message Codemessage=Not all who wander are losterrorMap"
                + "=rowErrorMsgList = []masterErrorList[]scheduleBatchId=42txnstatus=TxnstatusfileName=foo.txtfileAttachment"
                + "=File Attachment", actualToStringResult);
    }
}

