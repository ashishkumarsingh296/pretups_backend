package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BulkCUStatusChangeResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkCUStatusChangeResponseVO}
     *   <li>{@link BulkCUStatusChangeResponseVO#setErrorLogs(List)}
     *   <li>{@link BulkCUStatusChangeResponseVO#setFileAttachment(String)}
     *   <li>{@link BulkCUStatusChangeResponseVO#setFileName(String)}
     *   <li>{@link BulkCUStatusChangeResponseVO#setMessage(String)}
     *   <li>{@link BulkCUStatusChangeResponseVO#setMessageCode(String)}
     *   <li>{@link BulkCUStatusChangeResponseVO#setNumberOfRecords(long)}
     *   <li>{@link BulkCUStatusChangeResponseVO#setStatus(String)}
     *   <li>{@link BulkCUStatusChangeResponseVO#toString()}
     *   <li>{@link BulkCUStatusChangeResponseVO#getErrorLogs()}
     *   <li>{@link BulkCUStatusChangeResponseVO#getFileAttachment()}
     *   <li>{@link BulkCUStatusChangeResponseVO#getFileName()}
     *   <li>{@link BulkCUStatusChangeResponseVO#getMessage()}
     *   <li>{@link BulkCUStatusChangeResponseVO#getMessageCode()}
     *   <li>{@link BulkCUStatusChangeResponseVO#getNumberOfRecords()}
     *   <li>{@link BulkCUStatusChangeResponseVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkCUStatusChangeResponseVO actualBulkCUStatusChangeResponseVO = new BulkCUStatusChangeResponseVO();
        ArrayList<ErrorLog> errorLogs = new ArrayList<>();
        actualBulkCUStatusChangeResponseVO.setErrorLogs(errorLogs);
        actualBulkCUStatusChangeResponseVO.setFileAttachment("File Attachment");
        actualBulkCUStatusChangeResponseVO.setFileName("foo.txt");
        actualBulkCUStatusChangeResponseVO.setMessage("Not all who wander are lost");
        actualBulkCUStatusChangeResponseVO.setMessageCode("Message Code");
        actualBulkCUStatusChangeResponseVO.setNumberOfRecords(1L);
        actualBulkCUStatusChangeResponseVO.setStatus("Txnstatus");
        String actualToStringResult = actualBulkCUStatusChangeResponseVO.toString();
        assertSame(errorLogs, actualBulkCUStatusChangeResponseVO.getErrorLogs());
        assertEquals("File Attachment", actualBulkCUStatusChangeResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualBulkCUStatusChangeResponseVO.getFileName());
        assertEquals("Not all who wander are lost", actualBulkCUStatusChangeResponseVO.getMessage());
        assertEquals("Message Code", actualBulkCUStatusChangeResponseVO.getMessageCode());
        assertEquals(1L, actualBulkCUStatusChangeResponseVO.getNumberOfRecords());
        assertEquals("Txnstatus", actualBulkCUStatusChangeResponseVO.getStatus());
        assertEquals("BulkCUStatusChangeResponseVO [messageCode=Message Codemessage=Not all who wander are loststatus"
                + "=TxnstatusfileName=foo.txtfileAttachment=File AttachmenterrorLogs= []", actualToStringResult);
    }
}

