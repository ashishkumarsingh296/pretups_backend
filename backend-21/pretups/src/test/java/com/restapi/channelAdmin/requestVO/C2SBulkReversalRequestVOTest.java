package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2SBulkReversalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SBulkReversalRequestVO}
     *   <li>{@link C2SBulkReversalRequestVO#setAttachment(String)}
     *   <li>{@link C2SBulkReversalRequestVO#setBatchName(String)}
     *   <li>{@link C2SBulkReversalRequestVO#setFileName(String)}
     *   <li>{@link C2SBulkReversalRequestVO#setFileType(String)}
     *   <li>{@link C2SBulkReversalRequestVO#toString()}
     *   <li>{@link C2SBulkReversalRequestVO#getAttachment()}
     *   <li>{@link C2SBulkReversalRequestVO#getBatchName()}
     *   <li>{@link C2SBulkReversalRequestVO#getFileName()}
     *   <li>{@link C2SBulkReversalRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SBulkReversalRequestVO actualC2sBulkReversalRequestVO = new C2SBulkReversalRequestVO();
        actualC2sBulkReversalRequestVO.setAttachment("Attachment");
        actualC2sBulkReversalRequestVO.setBatchName("Batch Name");
        actualC2sBulkReversalRequestVO.setFileName("foo.txt");
        actualC2sBulkReversalRequestVO.setFileType("File Type");
        String actualToStringResult = actualC2sBulkReversalRequestVO.toString();
        assertEquals("Attachment", actualC2sBulkReversalRequestVO.getAttachment());
        assertEquals("Batch Name", actualC2sBulkReversalRequestVO.getBatchName());
        assertEquals("foo.txt", actualC2sBulkReversalRequestVO.getFileName());
        assertEquals("File Type", actualC2sBulkReversalRequestVO.getFileType());
        assertEquals("C2SBulkReversalRequestVO [batchName=Batch Name, fileName=foo.txt, attachment=Attachment]",
                actualToStringResult);
    }
}

