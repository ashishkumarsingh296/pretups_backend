package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeRegisterSubscriberBatchRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DeRegisterSubscriberBatchRequestVO}
     *   <li>{@link DeRegisterSubscriberBatchRequestVO#setFileAttachment(String)}
     *   <li>{@link DeRegisterSubscriberBatchRequestVO#setFileName(String)}
     *   <li>{@link DeRegisterSubscriberBatchRequestVO#setFileType(String)}
     *   <li>{@link DeRegisterSubscriberBatchRequestVO#getFileAttachment()}
     *   <li>{@link DeRegisterSubscriberBatchRequestVO#getFileName()}
     *   <li>{@link DeRegisterSubscriberBatchRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DeRegisterSubscriberBatchRequestVO actualDeRegisterSubscriberBatchRequestVO = new DeRegisterSubscriberBatchRequestVO();
        actualDeRegisterSubscriberBatchRequestVO.setFileAttachment("File Attachment");
        actualDeRegisterSubscriberBatchRequestVO.setFileName("foo.txt");
        actualDeRegisterSubscriberBatchRequestVO.setFileType("File Type");
        assertEquals("File Attachment", actualDeRegisterSubscriberBatchRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualDeRegisterSubscriberBatchRequestVO.getFileName());
        assertEquals("File Type", actualDeRegisterSubscriberBatchRequestVO.getFileType());
    }
}

