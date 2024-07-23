package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AddBatchPromotionalTransferRuleFileProcessingRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddBatchPromotionalTransferRuleFileProcessingRequestVO}
     *   <li>{@link AddBatchPromotionalTransferRuleFileProcessingRequestVO#setFileAttachment(String)}
     *   <li>{@link AddBatchPromotionalTransferRuleFileProcessingRequestVO#setFileName(String)}
     *   <li>{@link AddBatchPromotionalTransferRuleFileProcessingRequestVO#setFileType(String)}
     *   <li>{@link AddBatchPromotionalTransferRuleFileProcessingRequestVO#getFileAttachment()}
     *   <li>{@link AddBatchPromotionalTransferRuleFileProcessingRequestVO#getFileName()}
     *   <li>{@link AddBatchPromotionalTransferRuleFileProcessingRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddBatchPromotionalTransferRuleFileProcessingRequestVO actualAddBatchPromotionalTransferRuleFileProcessingRequestVO = new AddBatchPromotionalTransferRuleFileProcessingRequestVO();
        actualAddBatchPromotionalTransferRuleFileProcessingRequestVO.setFileAttachment("File Attachment");
        actualAddBatchPromotionalTransferRuleFileProcessingRequestVO.setFileName("foo.txt");
        actualAddBatchPromotionalTransferRuleFileProcessingRequestVO.setFileType("File Type");
        assertEquals("File Attachment", actualAddBatchPromotionalTransferRuleFileProcessingRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualAddBatchPromotionalTransferRuleFileProcessingRequestVO.getFileName());
        assertEquals("File Type", actualAddBatchPromotionalTransferRuleFileProcessingRequestVO.getFileType());
    }
}

