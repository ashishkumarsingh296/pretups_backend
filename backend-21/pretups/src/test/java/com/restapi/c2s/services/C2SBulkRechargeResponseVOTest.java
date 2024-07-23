package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class C2SBulkRechargeResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SBulkRechargeResponseVO}
     *   <li>{@link C2SBulkRechargeResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link C2SBulkRechargeResponseVO#setFileAttachment(String)}
     *   <li>{@link C2SBulkRechargeResponseVO#setFileName(String)}
     *   <li>{@link C2SBulkRechargeResponseVO#setMessage(String)}
     *   <li>{@link C2SBulkRechargeResponseVO#setMessageCode(String)}
     *   <li>{@link C2SBulkRechargeResponseVO#setNumberOfRecords(long)}
     *   <li>{@link C2SBulkRechargeResponseVO#setScheduleBatchId(String)}
     *   <li>{@link C2SBulkRechargeResponseVO#setStatus(String)}
     *   <li>{@link C2SBulkRechargeResponseVO#toString()}
     *   <li>{@link C2SBulkRechargeResponseVO#getErrorMap()}
     *   <li>{@link C2SBulkRechargeResponseVO#getFileAttachment()}
     *   <li>{@link C2SBulkRechargeResponseVO#getFileName()}
     *   <li>{@link C2SBulkRechargeResponseVO#getMessage()}
     *   <li>{@link C2SBulkRechargeResponseVO#getMessageCode()}
     *   <li>{@link C2SBulkRechargeResponseVO#getNumberOfRecords()}
     *   <li>{@link C2SBulkRechargeResponseVO#getScheduleBatchId()}
     *   <li>{@link C2SBulkRechargeResponseVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SBulkRechargeResponseVO actualC2sBulkRechargeResponseVO = new C2SBulkRechargeResponseVO();
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualC2sBulkRechargeResponseVO.setErrorMap(errorMap);
        actualC2sBulkRechargeResponseVO.setFileAttachment("File Attachment");
        actualC2sBulkRechargeResponseVO.setFileName("foo.txt");
        actualC2sBulkRechargeResponseVO.setMessage("Not all who wander are lost");
        actualC2sBulkRechargeResponseVO.setMessageCode("Message Code");
        actualC2sBulkRechargeResponseVO.setNumberOfRecords(1L);
        actualC2sBulkRechargeResponseVO.setScheduleBatchId("42");
        actualC2sBulkRechargeResponseVO.setStatus("Txnstatus");
        String actualToStringResult = actualC2sBulkRechargeResponseVO.toString();
        assertSame(errorMap, actualC2sBulkRechargeResponseVO.getErrorMap());
        assertEquals("File Attachment", actualC2sBulkRechargeResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualC2sBulkRechargeResponseVO.getFileName());
        assertEquals("Not all who wander are lost", actualC2sBulkRechargeResponseVO.getMessage());
        assertEquals("Message Code", actualC2sBulkRechargeResponseVO.getMessageCode());
        assertEquals(1L, actualC2sBulkRechargeResponseVO.getNumberOfRecords());
        assertEquals("42", actualC2sBulkRechargeResponseVO.getScheduleBatchId());
        assertEquals("Txnstatus", actualC2sBulkRechargeResponseVO.getStatus());
        assertEquals("C2SBulkRechargeResponseVO [messageCode=Message Codemessage=Not all who wander are losterrorMap"
                + "=rowErrorMsgList = []masterErrorList[]scheduleBatchId=42txnstatus=TxnstatusfileName=foo.txtfileAttachment"
                + "=File Attachment", actualToStringResult);
    }
}

