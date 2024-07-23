package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeRegisterSubscriberBatchResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DeRegisterSubscriberBatchResponseVO}
     *   <li>{@link DeRegisterSubscriberBatchResponseVO#setFileAttachment(String)}
     *   <li>{@link DeRegisterSubscriberBatchResponseVO#setFileName(String)}
     *   <li>{@link DeRegisterSubscriberBatchResponseVO#getFileAttachment()}
     *   <li>{@link DeRegisterSubscriberBatchResponseVO#getFileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DeRegisterSubscriberBatchResponseVO actualDeRegisterSubscriberBatchResponseVO = new DeRegisterSubscriberBatchResponseVO();
        actualDeRegisterSubscriberBatchResponseVO.setFileAttachment("File Attachment");
        actualDeRegisterSubscriberBatchResponseVO.setFileName("foo.txt");
        assertEquals("File Attachment", actualDeRegisterSubscriberBatchResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualDeRegisterSubscriberBatchResponseVO.getFileName());
    }
}

